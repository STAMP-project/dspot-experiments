/**
 * The MIT License
 *
 * Copyright 2013 Jesse Glick.
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
package hudson.model;


import java.io.File;
import java.io.IOException;
import jenkins.model.Jenkins;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.TestExtension;


public class ViewJobTest {
    @Rule
    public JenkinsRule rule = new JenkinsRule();

    @Issue("JENKINS-19377")
    @Test
    public void removeRun() throws Exception {
        ViewJobTest.J j = rule.jenkins.createProject(ViewJobTest.J.class, "j");
        ViewJobTest.R r1 = j.nue();
        ViewJobTest.R r2 = j.nue();
        Assert.assertEquals("[2, 1]", getBuildsAsMap().keySet().toString());
        j.removeRun(r1);
        Assert.assertEquals("[2]", getBuildsAsMap().keySet().toString());
    }

    @SuppressWarnings({ "rawtypes", "unchecked", "deprecation" })
    public static final class J extends ViewJob<ViewJobTest.J, ViewJobTest.R> implements TopLevelItem {
        public J(ItemGroup parent, String name) {
            super(parent, name);
        }

        @Override
        protected void reload() {
            runs.load(this, new RunMap.Constructor<ViewJobTest.R>() {
                @Override
                public hudson.model.R create(File d) throws IOException {
                    return new hudson.model.R(hudson.model.J.this, d);
                }
            });
        }

        @Override
        public TopLevelItemDescriptor getDescriptor() {
            return Jenkins.getInstance().getDescriptorByType(ViewJobTest.J.DescriptorImpl.class);
        }

        @TestExtension
        public static final class DescriptorImpl extends TopLevelItemDescriptor {
            @Override
            public TopLevelItem newInstance(ItemGroup parent, String name) {
                return new ViewJobTest.J(parent, name);
            }
        }

        ViewJobTest.R nue() throws IOException {
            ViewJobTest.R r = new ViewJobTest.R(this);
            _getRuns();
            runs.put(r);
            return r;
        }
    }

    public static final class R extends Run<ViewJobTest.J, ViewJobTest.R> {
        public R(ViewJobTest.J j) throws IOException {
            super(j);
        }

        public R(ViewJobTest.J j, File d) throws IOException {
            super(j, d);
        }
    }
}

