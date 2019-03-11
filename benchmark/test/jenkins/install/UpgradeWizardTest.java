package jenkins.install;


import java.util.concurrent.Callable;
import net.sf.json.JSONArray;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.SmokeTest;


/**
 *
 *
 * @author Kohsuke Kawaguchi
 */
@Category(SmokeTest.class)
public class UpgradeWizardTest {
    @Rule
    public final JenkinsRule j = new JenkinsRule();

    private JSONArray platformPluginUpdates;

    @Test
    public void snooze() throws Exception {
        j.executeOnServer(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                UpgradeWizardTest.writeVersion("1.5");
                UpgradeWizard uw = getInstance();
                Assert.assertTrue(uw.isDue());
                uw.doSnooze();
                Assert.assertFalse(uw.isDue());
                return null;
            }
        });
    }

    /**
     * If not upgraded, the upgrade should cause some side effect.
     */
    @Test
    public void upgrade() throws Exception {
        j.executeOnServer(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Assert.assertTrue(((j.jenkins.getUpdateCenter().getJobs().size()) == 0));
                UpgradeWizardTest.writeVersion("1.5");
                Assert.assertTrue(getInstance().isDue());
                // can't really test this because UC metadata is empty
                // assertTrue(j.jenkins.getUpdateCenter().getJobs().size() > 0);
                return null;
            }
        });
    }

    /**
     * If already upgraded, don't show anything
     */
    @Test
    public void fullyUpgraded() throws Exception {
        j.executeOnServer(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                platformPluginUpdates = new JSONArray();
                Assert.assertFalse(getInstance().isDue());
                return null;
            }
        });
    }

    @Test
    public void freshInstallation() throws Exception {
        j.executeOnServer(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                InstallState prior = j.jenkins.getInstallState();
                try {
                    UpgradeWizard uw = getInstance();
                    Assert.assertTrue(uw.isDue());// there are platform plugin updates

                    j.jenkins.getSetupWizard().completeSetup();
                    Assert.assertFalse(uw.isDue());
                    return null;
                } finally {
                    j.jenkins.setInstallState(prior);
                }
            }
        });
    }
}

