/**
 * The MIT License
 *
 * Copyright (c) 2018 CloudBees, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package jenkins.security;


import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.FreeStyleProject;
import hudson.model.JobProperty;
import hudson.model.JobPropertyDescriptor;
import hudson.slaves.DumbSlave;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLStreamHandler;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.model.Statement;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.RestartableJenkinsRule;
import org.jvnet.hudson.test.TestExtension;


public class Security637Test {
    @Rule
    public RestartableJenkinsRule rr = new RestartableJenkinsRule();

    @Test
    @Issue("SECURITY-637")
    public void urlSafeDeserialization_handler_inSameJVMRemotingContext() {
        rr.addStep(new Statement() {
            @Override
            public void evaluate() throws Exception {
                DumbSlave slave = rr.j.createOnlineSlave();
                String unsafeHandlerClassName = slave.getChannel().call(new Security637Test.URLHandlerCallable(new URL("https://www.google.com/")));
                Assert.assertThat(unsafeHandlerClassName, CoreMatchers.containsString("SafeURLStreamHandler"));
                String safeHandlerClassName = slave.getChannel().call(new Security637Test.URLHandlerCallable(new URL("file", null, (-1), "", null)));
                Assert.assertThat(safeHandlerClassName, CoreMatchers.not(CoreMatchers.containsString("SafeURLStreamHandler")));
            }
        });
    }

    private static class URLHandlerCallable extends MasterToSlaveCallable<String, Exception> {
        private URL url;

        public URLHandlerCallable(URL url) {
            this.url = url;
        }

        @Override
        public String call() throws Exception {
            Field handlerField = URL.class.getDeclaredField("handler");
            handlerField.setAccessible(true);
            URLStreamHandler handler = ((URLStreamHandler) (handlerField.get(url)));
            return handler.getClass().getName();
        }
    }

    @Test
    @Issue("SECURITY-637")
    public void urlDnsEquivalence() {
        rr.addStep(new Statement() {
            @Override
            public void evaluate() throws Exception {
                // due to the DNS resolution they are equal
                Assert.assertEquals(new URL("https://jenkins.io"), new URL("https://www.jenkins.io"));
            }
        });
    }

    @Test
    @Issue("SECURITY-637")
    public void urlSafeDeserialization_urlBuiltInAgent_inSameJVMRemotingContext() {
        rr.addStep(new Statement() {
            @Override
            public void evaluate() throws Exception {
                DumbSlave slave = rr.j.createOnlineSlave();
                // we bypass the standard equals method that resolve the hostname
                Assert.assertThat(slave.getChannel().call(new Security637Test.URLBuilderCallable("https://jenkins.io")), CoreMatchers.not(CoreMatchers.equalTo(slave.getChannel().call(new Security637Test.URLBuilderCallable("https://www.jenkins.io")))));
            }
        });
    }

    private static class URLBuilderCallable extends MasterToSlaveCallable<URL, Exception> {
        private String url;

        public URLBuilderCallable(String url) {
            this.url = url;
        }

        @Override
        public URL call() throws Exception {
            return new URL(url);
        }
    }

    @Test
    @Issue("SECURITY-637")
    public void urlSafeDeserialization_urlBuiltInMaster_inSameJVMRemotingContext() {
        rr.addStep(new Statement() {
            @Override
            public void evaluate() throws Exception {
                DumbSlave slave = rr.j.createOnlineSlave();
                // we bypass the standard equals method that resolve the hostname
                Assert.assertThat(slave.getChannel().call(new Security637Test.URLTransferCallable(new URL("https://jenkins.io"))), CoreMatchers.not(CoreMatchers.equalTo(slave.getChannel().call(new Security637Test.URLTransferCallable(new URL("https://www.jenkins.io"))))));
                // due to the DNS resolution they are equal
                Assert.assertEquals(new URL("https://jenkins.io"), new URL("https://www.jenkins.io"));
            }
        });
    }

    // the URL is serialized / deserialized twice, master => agent and then agent => master
    private static class URLTransferCallable extends MasterToSlaveCallable<URL, Exception> {
        private URL url;

        public URLTransferCallable(URL url) {
            this.url = url;
        }

        @Override
        public URL call() throws Exception {
            return url;
        }
    }

    @Test
    @Issue("SECURITY-637")
    public void urlSafeDeserialization_inXStreamContext() {
        rr.addStep(new Statement() {
            @Override
            public void evaluate() throws Exception {
                FreeStyleProject project = rr.j.createFreeStyleProject("project-with-url");
                Security637Test.URLJobProperty URLJobProperty = // url to be wrapped
                // safe url, not required to be wrapped
                new Security637Test.URLJobProperty(new URL("https://www.google.com/"), new URL("https", null, (-1), "", null));
                project.addProperty(URLJobProperty);
                project.save();
            }
        });
        rr.addStep(new Statement() {
            @Override
            public void evaluate() throws Exception {
                FreeStyleProject project = rr.j.jenkins.getItemByFullName("project-with-url", FreeStyleProject.class);
                Assert.assertNotNull(project);
                Field handlerField = URL.class.getDeclaredField("handler");
                handlerField.setAccessible(true);
                Security637Test.URLJobProperty urlJobProperty = project.getProperty(Security637Test.URLJobProperty.class);
                for (URL url : urlJobProperty.urlSet) {
                    URLStreamHandler handler = ((URLStreamHandler) (handlerField.get(url)));
                    if (StringUtils.isEmpty(url.getHost())) {
                        Assert.assertThat(handler.getClass().getName(), CoreMatchers.not(CoreMatchers.containsString("SafeURLStreamHandler")));
                    } else {
                        Assert.assertThat(handler.getClass().getName(), CoreMatchers.containsString("SafeURLStreamHandler"));
                    }
                }
            }
        });
    }

    public static class URLJobProperty extends JobProperty<FreeStyleProject> {
        private Set<URL> urlSet;

        public URLJobProperty(URL... urls) throws Exception {
            this.urlSet = new HashSet<>();
            for (URL url : urls) {
                urlSet.add(url);
            }
        }

        @Override
        public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
            return true;
        }

        @TestExtension("urlSafeDeserialization_inXStreamContext")
        public static class DescriptorImpl extends JobPropertyDescriptor {}
    }
}

