package jenkins.model;


import Mode.EXCLUSIVE;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;


public class MasterBuildConfigurationTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    @Issue("JENKINS-23966")
    public void retainMasterLabelWhenNoSlaveDefined() throws Exception {
        Jenkins jenkins = j.getInstance();
        Assert.assertTrue("Test is for master with no slave", ((jenkins.getComputers().length) == 1));
        // set our own label & mode
        final String myTestLabel = "TestLabelx0123";
        jenkins.setLabelString(myTestLabel);
        jenkins.setMode(EXCLUSIVE);
        // call global config page
        j.configRoundtrip();
        // make sure settings were not lost
        Assert.assertTrue("Master's label is lost", myTestLabel.equals(jenkins.getLabelString()));
        Assert.assertTrue("Master's mode is lost", EXCLUSIVE.equals(jenkins.getMode()));
    }
}

