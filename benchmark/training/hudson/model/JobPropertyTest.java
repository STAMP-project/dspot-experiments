/**
 * The MIT License
 *
 * Copyright (c) 2004-2009, Sun Microsystems, Inc., Kohsuke Kawaguchi, Erik Ramfelt, CloudBees, Inc.
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


import hudson.matrix.MatrixProject;
import hudson.maven.MavenModuleSet;
import hudson.model.Descriptor.FormException;
import java.util.logging.Level;
import net.sf.json.JSONObject;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.LoggerRule;
import org.jvnet.hudson.test.TestExtension;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;


public class JobPropertyTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Rule
    public LoggerRule logs = new LoggerRule();

    @Test
    @Issue("JENKINS-2398")
    public void jobPropertySummaryIsShownInMavenModuleSetIndexPage() throws Exception {
        assertJobPropertySummaryIsShownInIndexPage(MavenModuleSet.class);
    }

    @Test
    @Issue("JENKINS-2398")
    public void jobPropertySummaryIsShownInMatrixProjectIndexPage() throws Exception {
        assertJobPropertySummaryIsShownInIndexPage(MatrixProject.class);
    }

    @Test
    @Issue("JENKINS-2398")
    public void jobPropertySummaryIsShownInFreeStyleProjectIndexPage() throws Exception {
        assertJobPropertySummaryIsShownInIndexPage(FreeStyleProject.class);
    }

    public static class JobPropertyImpl extends JobProperty<Job<?, ?>> {
        private final String propertyString;

        public JobPropertyImpl(String propertyString) {
            this.propertyString = propertyString;
        }

        public String getPropertyString() {
            return propertyString;
        }

        @TestExtension
        public static class DescriptorImpl extends JobPropertyDescriptor {
            @SuppressWarnings("rawtypes")
            @Override
            public boolean isApplicable(Class<? extends Job> jobType) {
                return false;
            }
        }
    }

    /**
     * Make sure that the UI rendering works as designed.
     */
    @Test
    public void configRoundtrip() throws Exception {
        logs.record(Descriptor.class, Level.ALL);
        FreeStyleProject p = j.createFreeStyleProject();
        JobPropertyTest.JobPropertyWithConfigImpl before = new JobPropertyTest.JobPropertyWithConfigImpl("Duke");
        p.addProperty(before);
        j.configRoundtrip(((Item) (p)));
        JobPropertyTest.JobPropertyWithConfigImpl after = p.getProperty(JobPropertyTest.JobPropertyWithConfigImpl.class);
        Assert.assertNotSame(after, before);
        j.assertEqualDataBoundBeans(before, after);
        p.removeProperty(after);
        JobPropertyTest.JobPropertyWithConfigImpl empty = new JobPropertyTest.JobPropertyWithConfigImpl("");
        p.addProperty(empty);
        j.configRoundtrip(((Item) (p)));
        Assert.assertNull(p.getProperty(JobPropertyTest.JobPropertyWithConfigImpl.class));
    }

    public static class JobPropertyWithConfigImpl extends JobProperty<Job<?, ?>> {
        public String name;

        @DataBoundConstructor
        public JobPropertyWithConfigImpl(String name) {
            this.name = name;
        }

        @TestExtension("configRoundtrip")
        public static class DescriptorImpl extends JobPropertyDescriptor {
            @Override
            public JobProperty<?> newInstance(StaplerRequest req, JSONObject formData) throws FormException {
                JobPropertyTest.JobPropertyWithConfigImpl prop = ((JobPropertyTest.JobPropertyWithConfigImpl) (super.newInstance(req, formData)));
                return prop.name.isEmpty() ? null : prop;
            }
        }
    }

    @Test
    public void invisibleProperty() throws Exception {
        FreeStyleProject p = j.createFreeStyleProject();
        JobPropertyTest.InvisibleImpl before = new JobPropertyTest.InvisibleImpl();
        p.addProperty(before);
        j.configRoundtrip(((Item) (p)));
        JobPropertyTest.InvisibleImpl after = p.getProperty(JobPropertyTest.InvisibleImpl.class);
        Assert.assertSame(after, before);
    }

    public static class InvisibleImpl extends JobProperty<Job<?, ?>> {
        public String name;

        InvisibleImpl() {
        }

        @Override
        public JobProperty<?> reconfigure(StaplerRequest req, JSONObject form) throws FormException {
            return this;
        }

        @TestExtension("invisibleProperty")
        public static class DescriptorImpl extends JobPropertyDescriptor {}
    }
}

