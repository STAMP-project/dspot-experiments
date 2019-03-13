package hudson.tasks;


import Result.SUCCESS;
import hudson.Functions;
import hudson.Launcher.ProcStarter;
import hudson.Proc;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.FakeLauncher;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.PretendSlave;
import org.jvnet.hudson.test.recipes.LocalData;


/**
 * Tests for the Shell tasks class
 *
 * @author Kohsuke Kawaguchi
 */
public class ShellTest {
    @Rule
    public JenkinsRule rule = new JenkinsRule();

    @Test
    public void validateShellCommandEOL() throws Exception {
        Shell obj = new Shell("echo A\r\necho B\recho C");
        rule.assertStringContains(obj.getCommand(), "echo A\necho B\necho C");
    }

    @Test
    public void validateShellContents() throws Exception {
        Shell obj = new Shell("echo A\r\necho B\recho C");
        rule.assertStringContains(obj.getContents(), "\necho A\necho B\necho C");
    }

    @Test
    public void testBasic() throws Exception {
        Assume.assumeFalse("If we're on Windows, don't bother doing this", Functions.isWindows());
        // TODO: define a FakeLauncher implementation with easymock so that this kind of assertions can be simplified.
        PretendSlave s = rule.createPretendSlave(new FakeLauncher() {
            public Proc onLaunch(ProcStarter p) throws IOException {
                // test the command line argument.
                List<String> cmds = p.cmds();
                rule.assertStringContains("/bin/sh", cmds.get(0));
                rule.assertStringContains("-xe", cmds.get(1));
                Assert.assertTrue(new File(cmds.get(2)).exists());
                // fake the execution
                PrintStream ps = new PrintStream(p.stdout());
                ps.println("Hudson was here");
                ps.close();
                return new FinishedProc(0);
            }
        });
        FreeStyleProject p = rule.createFreeStyleProject();
        p.getBuildersList().add(new Shell("echo abc"));
        p.setAssignedNode(s);
        FreeStyleBuild b = rule.assertBuildStatusSuccess(p.scheduleBuild2(0).get());
        Assert.assertEquals(1, s.numLaunch);
        Assert.assertTrue(FileUtils.readFileToString(b.getLogFile()).contains("Hudson was here"));
    }

    /* A FakeLauncher that just returns the specified error code */
    private class ReturnCodeFakeLauncher implements FakeLauncher {
        final int code;

        ReturnCodeFakeLauncher(int code) {
            super();
            this.code = code;
        }

        @Override
        public Proc onLaunch(ProcStarter p) throws IOException {
            return new FinishedProc(this.code);
        }
    }

    @Test
    @Issue("JENKINS-23786")
    public void unixExitCodes1To255ShouldMakeBuildUnstable() throws Exception {
        Assume.assumeFalse(Functions.isWindows());
        for (int exitCode : new int[]{ 1, 2, 255 }) {
            nonZeroExitCodeShouldMakeBuildUnstable(exitCode);
        }
    }

    @Test
    @Issue("JENKINS-23786")
    public void unixExitCodes1To255ShouldBreakTheBuildByDefault() throws Exception {
        Assume.assumeFalse(Functions.isWindows());
        for (int exitCode : new int[]{ 1, 2, 255 }) {
            nonZeroExitCodeShouldBreakTheBuildByDefault(exitCode);
        }
    }

    @Test
    @Issue("JENKINS-23786")
    public void unixExitCodes1To255ShouldBreakTheBuildIfNotMatching() throws Exception {
        Assume.assumeFalse(Functions.isWindows());
        for (int exitCode : new int[]{ 1, 2, 255 }) {
            nonZeroExitCodeShouldBreakTheBuildIfNotMatching(exitCode);
        }
    }

    @Test
    @Issue("JENKINS-23786")
    public void unixExitCodes0ShouldNeverMakeTheBuildUnstable() throws Exception {
        Assume.assumeFalse(Functions.isWindows());
        PretendSlave slave = rule.createPretendSlave(new ShellTest.ReturnCodeFakeLauncher(0));
        for (Integer unstableReturn : new Integer[]{ null, 0, 1 }) {
            FreeStyleProject p = rule.createFreeStyleProject();
            p.getBuildersList().add(ShellTest.createNewShell("", unstableReturn));
            p.setAssignedNode(slave);
            rule.assertBuildStatus(SUCCESS, p.scheduleBuild2(0).get());
        }
    }

    @Issue("JENKINS-23786")
    @Test
    public void unixUnstableCodeZeroIsSameAsUnset() throws Exception {
        Assume.assumeFalse(Functions.isWindows());
        /* Creating unstable=0 produces unstable=null */
        Assert.assertNull(ShellTest.createNewShell("", 0).getUnstableReturn());
    }

    @Issue("JENKINS-40894")
    @Test
    @LocalData
    public void canLoadUnstableReturnFromDisk() throws Exception {
        FreeStyleProject p = ((FreeStyleProject) (rule.jenkins.getItemByFullName("test")));
        Shell shell = ((Shell) (p.getBuildersList().get(0)));
        Assert.assertEquals("unstable return", Integer.valueOf(1), shell.getUnstableReturn());
    }
}

