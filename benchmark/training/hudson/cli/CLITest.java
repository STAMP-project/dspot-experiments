/**
 * The MIT License
 *
 * Copyright 2017 CloudBees, Inc.
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
package hudson.cli;


import HttpResponses.HttpResponseException;
import Jenkins.ADMINISTER;
import JenkinsRule.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import hudson.model.FreeStyleProject;
import hudson.model.UnprotectedRootAction;
import hudson.model.User;
import hudson.security.csrf.CrumbExclusion;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import jenkins.model.GlobalConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.jenkinsci.main.modules.cli.auth.ssh.UserPropertyImpl;
import org.jenkinsci.main.modules.sshd.SSHD;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.jvnet.hudson.test.BuildWatcher;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.MockAuthorizationStrategy;
import org.jvnet.hudson.test.SleepBuilder;
import org.jvnet.hudson.test.TestExtension;
import org.kohsuke.stapler.HttpResponses;
import org.kohsuke.stapler.Stapler;
import org.kohsuke.stapler.StaplerProxy;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;


public class CLITest {
    @ClassRule
    public static BuildWatcher buildWatcher = new BuildWatcher();

    @Rule
    public JenkinsRule r = new JenkinsRule();

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    private File home;

    private File jar;

    @Issue("JENKINS-41745")
    @Test
    public void strictHostKey() throws Exception {
        home = tempHome();
        grabCliJar();
        r.jenkins.setSecurityRealm(r.createDummySecurityRealm());
        r.jenkins.setAuthorizationStrategy(new MockAuthorizationStrategy().grant(ADMINISTER).everywhere().to("admin"));
        SSHD.get().setPort(0);
        File privkey = tmp.newFile("id_rsa");
        FileUtils.copyURLToFile(CLITest.class.getResource("id_rsa"), privkey);
        User.get("admin").addProperty(new UserPropertyImpl(IOUtils.toString(CLITest.class.getResource("id_rsa.pub"))));
        Assert.assertNotEquals(0, launch().cmds("java", ("-Duser.home=" + (home)), "-jar", jar.getAbsolutePath(), "-s", r.getURL().toString(), "-ssh", "-user", "admin", "-i", privkey.getAbsolutePath(), "-strictHostKey", "who-am-i").stdout(System.out).stderr(System.err).join());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Assert.assertEquals(0, launch().cmds("java", ("-Duser.home=" + (home)), "-jar", jar.getAbsolutePath(), "-s", r.getURL().toString(), "-ssh", "-user", "admin", "-i", privkey.getAbsolutePath(), "-logger", "FINEST", "who-am-i").stdout(baos).stderr(System.err).join());
        Assert.assertThat(baos.toString(), containsString("Authenticated as: admin"));
        baos = new ByteArrayOutputStream();
        Assert.assertEquals(0, launch().cmds("java", ("-Duser.home=" + (home)), "-jar", jar.getAbsolutePath(), "-s", /* just checking */
        r.getURL().toString().replaceFirst("/$", ""), "-ssh", "-user", "admin", "-i", privkey.getAbsolutePath(), "-strictHostKey", "who-am-i").stdout(baos).stderr(System.err).join());
        Assert.assertThat(baos.toString(), containsString("Authenticated as: admin"));
    }

    @Issue("JENKINS-41745")
    @Test
    public void interrupt() throws Exception {
        home = tempHome();
        grabCliJar();
        r.jenkins.setSecurityRealm(r.createDummySecurityRealm());
        r.jenkins.setAuthorizationStrategy(new MockAuthorizationStrategy().grant(ADMINISTER).everywhere().to("admin"));
        SSHD.get().setPort(0);
        File privkey = tmp.newFile("id_rsa");
        FileUtils.copyURLToFile(CLITest.class.getResource("id_rsa"), privkey);
        User.get("admin").addProperty(new UserPropertyImpl(IOUtils.toString(CLITest.class.getResource("id_rsa.pub"))));
        FreeStyleProject p = r.createFreeStyleProject("p");
        p.getBuildersList().add(new SleepBuilder(TimeUnit.MINUTES.toMillis(5)));
        doInterrupt(p, "-ssh", "-user", "admin", "-i", privkey.getAbsolutePath());
        doInterrupt(p, "-http", "-auth", "admin:admin");
    }

    @Test
    @Issue("JENKINS-44361")
    public void reportNotJenkins() throws Exception {
        home = tempHome();
        grabCliJar();
        String url = (r.getURL().toExternalForm()) + "not-jenkins/";
        for (String transport : Arrays.asList("-http", "-ssh")) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int ret = launch().cmds("java", ("-Duser.home=" + (home)), "-jar", jar.getAbsolutePath(), "-s", url, transport, "-user", "asdf", "who-am-i").stdout(baos).stderr(baos).join();
            Assert.assertThat(baos.toString(), containsString("There's no Jenkins running at"));
            Assert.assertNotEquals(0, ret);
        }
    }

    @TestExtension("reportNotJenkins")
    public static final class NoJenkinsAction extends CrumbExclusion implements UnprotectedRootAction , StaplerProxy {
        @Override
        public String getIconFileName() {
            return "not-jenkins";
        }

        @Override
        public String getDisplayName() {
            return "not-jenkins";
        }

        @Override
        public String getUrlName() {
            return "not-jenkins";
        }

        @Override
        public Object getTarget() {
            doDynamic(Stapler.getCurrentRequest(), Stapler.getCurrentResponse());
            return this;
        }

        public void doDynamic(StaplerRequest req, StaplerResponse rsp) {
            rsp.setStatus(200);
        }

        // Permit access to cli-proxy/XXX without CSRF checks
        @Override
        public boolean process(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
            chain.doFilter(request, response);
            return true;
        }
    }

    @Test
    @Issue("JENKINS-44361")
    public void redirectToEndpointShouldBeFollowed() throws Exception {
        home = tempHome();
        grabCliJar();
        // Enable CLI over SSH
        SSHD sshd = GlobalConfiguration.all().get(SSHD.class);
        sshd.setPort(0);// random

        sshd.start();
        // Sanity check
        JenkinsRule.WebClient wc = r.createWebClient().withRedirectEnabled(false).withThrowExceptionOnFailingStatusCode(false);
        WebResponse rsp = wc.goTo("cli-proxy/").getWebResponse();
        Assert.assertEquals(rsp.getContentAsString(), HttpURLConnection.HTTP_MOVED_TEMP, rsp.getStatusCode());
        Assert.assertEquals(rsp.getContentAsString(), null, rsp.getResponseHeaderValue("X-Jenkins"));
        Assert.assertEquals(rsp.getContentAsString(), null, rsp.getResponseHeaderValue("X-Jenkins-CLI-Port"));
        Assert.assertEquals(rsp.getContentAsString(), null, rsp.getResponseHeaderValue("X-SSH-Endpoint"));
        for (String transport : Arrays.asList("-http", "-ssh")) {
            String url = (r.getURL().toString()) + "cli-proxy/";
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int ret = launch().cmds("java", ("-Duser.home=" + (home)), "-jar", jar.getAbsolutePath(), "-s", url, transport, "-user", "asdf", "who-am-i").stdout(baos).stderr(baos).join();
            // assertThat(baos.toString(), containsString("There's no Jenkins running at"));
            Assert.assertThat(baos.toString(), containsString("Authenticated as: anonymous"));
            Assert.assertEquals(0, ret);
        }
    }

    @TestExtension("redirectToEndpointShouldBeFollowed")
    public static final class CliProxyAction extends CrumbExclusion implements UnprotectedRootAction , StaplerProxy {
        @Override
        public String getIconFileName() {
            return "cli-proxy";
        }

        @Override
        public String getDisplayName() {
            return "cli-proxy";
        }

        @Override
        public String getUrlName() {
            return "cli-proxy";
        }

        @Override
        public Object getTarget() {
            throw doDynamic(Stapler.getCurrentRequest(), Stapler.getCurrentResponse());
        }

        public HttpResponseException doDynamic(StaplerRequest req, StaplerResponse rsp) {
            final String url = req.getRequestURIWithQueryString().replaceFirst("/cli-proxy", "");
            // Custom written redirect so no traces of Jenkins are present in headers
            return new HttpResponses.HttpResponseException() {
                @Override
                public void generateResponse(StaplerRequest req, StaplerResponse rsp, Object node) throws IOException, ServletException {
                    rsp.setHeader("Location", url);
                    rsp.setContentType("text/html");
                    rsp.setStatus(HttpURLConnection.HTTP_MOVED_TEMP);
                    PrintWriter w = rsp.getWriter();
                    w.append("Redirect to ").append(url);
                }
            };
        }

        // Permit access to cli-proxy/XXX without CSRF checks
        @Override
        public boolean process(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
            chain.doFilter(request, response);
            return true;
        }
    }
}

