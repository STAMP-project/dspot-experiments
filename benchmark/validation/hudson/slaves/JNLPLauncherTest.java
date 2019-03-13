/**
 * The MIT License
 *
 * Copyright (c) 2004-2009, Sun Microsystems, Inc., Kohsuke Kawaguchi
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
package hudson.slaves;


import com.gargoylesoftware.htmlunit.html.HtmlPage;
import hudson.model.Computer;
import java.awt.GraphicsEnvironment;
import java.io.File;
import jenkins.security.SlaveToMasterCallable;
import jenkins.slaves.RemotingWorkDirSettings;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TemporaryFolder;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.SmokeTest;
import org.jvnet.hudson.test.TestExtension;
import org.jvnet.hudson.test.recipes.LocalData;


/**
 * Tests of {@link JNLPLauncher}.
 *
 * @author Kohsuke Kawaguchi
 */
@Category(SmokeTest.class)
public class JNLPLauncherTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Rule
    public TemporaryFolder tmpDir = new TemporaryFolder();

    /**
     * Starts a JNLP agent and makes sure it successfully connects to Jenkins.
     */
    @Test
    public void testLaunch() throws Exception {
        Assume.assumeFalse("Skipping JNLPLauncherTest.testLaunch because we are running headless", GraphicsEnvironment.isHeadless());
        Computer c = addTestSlave(false);
        launchJnlpAndVerify(c, buildJnlpArgs(c));
    }

    /**
     * Starts a JNLP agent and makes sure it successfully connects to Jenkins.
     */
    @Test
    @Issue("JENKINS-39370")
    public void testLaunchWithWorkDir() throws Exception {
        Assume.assumeFalse("Skipping JNLPLauncherTest.testLaunch because we are running headless", GraphicsEnvironment.isHeadless());
        File workDir = tmpDir.newFolder("workDir");
        Computer c = addTestSlave(false);
        launchJnlpAndVerify(c, buildJnlpArgs(c).add("-workDir", workDir.getAbsolutePath()));
        Assert.assertTrue("Remoting work dir should have been created", new File(workDir, "remoting").exists());
    }

    /**
     * Tests the '-headless' option.
     * (Although this test doesn't really assert that the agent really is running in a headless mdoe.)
     */
    @Test
    public void testHeadlessLaunch() throws Exception {
        Computer c = addTestSlave(false);
        launchJnlpAndVerify(c, buildJnlpArgs(c).add("-arg", "-headless"));
        // make sure that onOffline gets called just the right number of times
        Assert.assertEquals(1, ComputerListener.all().get(JNLPLauncherTest.ListenerImpl.class).offlined);
    }

    @Test
    @Issue("JENKINS-44112")
    public void testHeadlessLaunchWithWorkDir() throws Exception {
        Assume.assumeFalse("Skipping JNLPLauncherTest.testLaunch because we are running headless", GraphicsEnvironment.isHeadless());
        Computer c = addTestSlave(true);
        launchJnlpAndVerify(c, buildJnlpArgs(c).add("-arg", "-headless"));
        Assert.assertEquals(1, ComputerListener.all().get(JNLPLauncherTest.ListenerImpl.class).offlined);
    }

    @Test
    @Issue("JENKINS-39370")
    public void testHeadlessLaunchWithCustomWorkDir() throws Exception {
        Assume.assumeFalse("Skipping JNLPLauncherTest.testLaunch because we are running headless", GraphicsEnvironment.isHeadless());
        File workDir = tmpDir.newFolder("workDir");
        Computer c = addTestSlave(false);
        launchJnlpAndVerify(c, buildJnlpArgs(c).add("-arg", "-headless", "-workDir", workDir.getAbsolutePath()));
        Assert.assertEquals(1, ComputerListener.all().get(JNLPLauncherTest.ListenerImpl.class).offlined);
    }

    @Test
    @LocalData
    @Issue("JENKINS-44112")
    public void testNoWorkDirMigration() throws Exception {
        Computer computer = j.jenkins.getComputer("Foo");
        Assert.assertThat(computer, Matchers.instanceOf(SlaveComputer.class));
        SlaveComputer c = ((SlaveComputer) (computer));
        ComputerLauncher launcher = c.getLauncher();
        Assert.assertThat(launcher, Matchers.instanceOf(JNLPLauncher.class));
        JNLPLauncher jnlpLauncher = ((JNLPLauncher) (launcher));
        Assert.assertNotNull("Work Dir Settings should be defined", jnlpLauncher.getWorkDirSettings());
        Assert.assertTrue("Work directory should be disabled for the migrated agent", jnlpLauncher.getWorkDirSettings().isDisabled());
    }

    @Test
    @Issue("JENKINS-44112")
    @SuppressWarnings("deprecation")
    public void testDefaults() throws Exception {
        Assert.assertTrue("Work directory should be disabled for agents created via old API", new JNLPLauncher().getWorkDirSettings().isDisabled());
    }

    @Test
    @Issue("JENKINS-47056")
    public void testDelegatingComputerLauncher() throws Exception {
        Assume.assumeFalse("Skipping JNLPLauncherTest.testDelegatingComputerLauncher because we are running headless", GraphicsEnvironment.isHeadless());
        File workDir = tmpDir.newFolder("workDir");
        ComputerLauncher launcher = new JNLPLauncher("", "", new RemotingWorkDirSettings(false, workDir.getAbsolutePath(), "internalDir", false));
        launcher = new JNLPLauncherTest.DelegatingComputerLauncherImpl(launcher);
        Computer c = addTestSlave(launcher);
        launchJnlpAndVerify(c, buildJnlpArgs(c));
        Assert.assertTrue("Remoting work dir should have been created", new File(workDir, "internalDir").exists());
    }

    @Test
    @Issue("JENKINS-47056")
    public void testComputerLauncherFilter() throws Exception {
        Assume.assumeFalse("Skipping JNLPLauncherTest.testComputerLauncherFilter because we are running headless", GraphicsEnvironment.isHeadless());
        File workDir = tmpDir.newFolder("workDir");
        ComputerLauncher launcher = new JNLPLauncher("", "", new RemotingWorkDirSettings(false, workDir.getAbsolutePath(), "internalDir", false));
        launcher = new JNLPLauncherTest.ComputerLauncherFilterImpl(launcher);
        Computer c = addTestSlave(launcher);
        launchJnlpAndVerify(c, buildJnlpArgs(c));
        Assert.assertTrue("Remoting work dir should have been created", new File(workDir, "internalDir").exists());
    }

    @TestExtension("testHeadlessLaunch")
    public static class ListenerImpl extends ComputerListener {
        int offlined = 0;

        @Override
        public void onOffline(Computer c) {
            (offlined)++;
            Assert.assertTrue(c.isOffline());
        }
    }

    private static class DelegatingComputerLauncherImpl extends DelegatingComputerLauncher {
        public DelegatingComputerLauncherImpl(ComputerLauncher launcher) {
            super(launcher);
        }
    }

    private static class ComputerLauncherFilterImpl extends ComputerLauncherFilter {
        public ComputerLauncherFilterImpl(ComputerLauncher launcher) {
            super(launcher);
        }
    }

    private static class NoopTask extends SlaveToMasterCallable<String, RuntimeException> {
        public String call() {
            return "done";
        }

        private static final long serialVersionUID = 1L;
    }

    @Test
    public void testConfigRoundtrip() throws Exception {
        DumbSlave s = j.createSlave();
        JNLPLauncher original = new JNLPLauncher("a", "b");
        s.setLauncher(original);
        j.assertEqualDataBoundBeans(getWorkDirSettings(), RemotingWorkDirSettings.getEnabledDefaults());
        RemotingWorkDirSettings custom = new RemotingWorkDirSettings(false, null, "custom", false);
        ((JNLPLauncher) (s.getLauncher())).setWorkDirSettings(custom);
        HtmlPage p = j.createWebClient().getPage(s, "configure");
        j.submit(p.getFormByName("config"));
        j.assertEqualBeans(original, s.getLauncher(), "tunnel,vmargs");
        j.assertEqualDataBoundBeans(getWorkDirSettings(), custom);
    }
}

