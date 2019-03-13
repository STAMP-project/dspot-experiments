package jenkins.install;


import InstallState.NEW;
import InstallState.UNKNOWN;
import hudson.Main;
import java.io.IOException;
import jenkins.model.Jenkins;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runners.model.Statement;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.RestartableJenkinsRule;
import org.jvnet.hudson.test.SmokeTest;


@Category(SmokeTest.class)
public class SetupWizardRestartTest {
    @Rule
    public RestartableJenkinsRule rr = new RestartableJenkinsRule();

    @Issue("JENKINS-47439")
    @Test
    public void restartKeepsSetupWizardState() {
        rr.addStep(new Statement() {
            @Override
            public void evaluate() throws IOException {
                // Modify state so that we get into the same conditions as a real start
                Main.isUnitTest = false;
                FileUtils.write(InstallUtil.getLastExecVersionFile(), "");
                Jenkins j = rr.j.getInstance();
                // Re-evaluate current state based on the new context
                InstallUtil.proceedToNextStateFrom(UNKNOWN);
                Assert.assertEquals("Unexpected install state", NEW, j.getInstallState());
                Assert.assertTrue("Expecting setup wizard filter to be up", j.getSetupWizard().hasSetupWizardFilter());
                InstallUtil.saveLastExecVersion();
            }
        });
        // Check that the state is retained after a restart
        rr.addStep(new Statement() {
            @Override
            public void evaluate() {
                Jenkins j = rr.j.getInstance();
                Assert.assertEquals("Unexpected install state", NEW, j.getInstallState());
                Assert.assertTrue("Expecting setup wizard filter to be up after restart", j.getSetupWizard().hasSetupWizardFilter());
            }
        });
    }
}

