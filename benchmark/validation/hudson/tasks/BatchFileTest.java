package hudson.tasks;


import Result.SUCCESS;
import hudson.Functions;
import hudson.Launcher.ProcStarter;
import hudson.Proc;
import hudson.model.FreeStyleProject;
import java.io.IOException;
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
 * Tests for the BatchFile tasks class.
 *
 * @author David Ruhmann
 */
public class BatchFileTest {
    @Rule
    public JenkinsRule rule = new JenkinsRule();

    @Issue("JENKINS-7478")
    @Test
    public void validateBatchFileCommandEOL() throws Exception {
        BatchFile obj = new BatchFile("echo A\necho B\recho C");
        rule.assertStringContains(obj.getCommand(), "echo A\r\necho B\r\necho C");
    }

    @Test
    public void validateBatchFileContents() throws Exception {
        BatchFile obj = new BatchFile("echo A\necho B\recho C");
        rule.assertStringContains(obj.getContents(), "echo A\r\necho B\r\necho C\r\nexit %ERRORLEVEL%");
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
    public void windowsNonZeroErrorlevelsShouldMakeBuildUnstable() throws Exception {
        Assume.assumeTrue(Functions.isWindows());
        for (int exitCode : new int[]{ Integer.MIN_VALUE, -1, 1, Integer.MAX_VALUE }) {
            nonZeroErrorlevelShouldMakeBuildUnstable(exitCode);
        }
    }

    @Test
    @Issue("JENKINS-23786")
    public void windowsNonZeroErrorlevelsShouldBreakTheBuildByDefault() throws Exception {
        Assume.assumeTrue(Functions.isWindows());
        for (int exitCode : new int[]{ Integer.MIN_VALUE, -1, 1, Integer.MAX_VALUE }) {
            nonZeroErrorlevelShouldBreakTheBuildByDefault(exitCode);
        }
    }

    @Test
    @Issue("JENKINS-23786")
    public void windowsErrorlevelsShouldBreakTheBuildIfNotMatching() throws Exception {
        Assume.assumeTrue(Functions.isWindows());
        for (int exitCode : new int[]{ Integer.MIN_VALUE, -1, 1, Integer.MAX_VALUE }) {
            nonZeroErrorlevelShouldBreakTheBuildIfNotMatching(exitCode);
        }
    }

    @Test
    @Issue("JENKINS-23786")
    public void windowsErrorlevel0ShouldNeverMakeTheBuildUnstable() throws Exception {
        Assume.assumeTrue(Functions.isWindows());
        PretendSlave slave = rule.createPretendSlave(new BatchFileTest.ReturnCodeFakeLauncher(0));
        for (Integer unstableReturn : new Integer[]{ null, 0, 1 }) {
            FreeStyleProject p = rule.createFreeStyleProject();
            p.getBuildersList().add(BatchFileTest.createNewBatchTask("", unstableReturn));
            p.setAssignedNode(slave);
            rule.assertBuildStatus(SUCCESS, p.scheduleBuild2(0).get());
        }
    }

    @Issue("JENKINS-23786")
    @Test
    public void windowsUnstableCodeZeroIsSameAsUnset() throws Exception {
        Assume.assumeTrue(Functions.isWindows());
        /* Creating unstable=0 produces unstable=null */
        Assert.assertNull(BatchFileTest.createNewBatchTask("", 0).getUnstableReturn());
    }

    @Issue("JENKINS-40894")
    @Test
    @LocalData
    public void canLoadUnstableReturnFromDisk() throws Exception {
        FreeStyleProject p = ((FreeStyleProject) (rule.jenkins.getItemByFullName("batch")));
        BatchFile batchFile = ((BatchFile) (p.getBuildersList().get(0)));
        Assert.assertEquals("unstable return", Integer.valueOf(1), batchFile.getUnstableReturn());
    }
}

