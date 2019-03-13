package hudson;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.recipes.LocalData;


public class XMLFileTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    @LocalData
    public void canStartWithXml_1_1_ConfigsTest() {
        Assert.assertThat(j.jenkins.getLabelString(), CoreMatchers.is("LESS_TERMCAP_mb=\u001b[01;31m"));
    }

    /**
     * This test validates that xml v1.0 configs silently get migrated to xml v1.1 when they are persisted
     */
    @Test
    @LocalData
    public void silentlyMigrateConfigsTest() throws Exception {
        j.jenkins.save();
        // verify that we did indeed load our test config.xml
        Assert.assertThat(j.jenkins.getLabelString(), CoreMatchers.is("I am a label"));
        // verify that the persisted top level config.xml is v1.1
        File configFile = new File(j.jenkins.getRootDir(), "config.xml");
        Assert.assertThat(configFile.exists(), CoreMatchers.is(true));
        try (BufferedReader config = new BufferedReader(new FileReader(configFile))) {
            Assert.assertThat(config.readLine(), CoreMatchers.is("<?xml version='1.1' encoding='UTF-8'?>"));
            config.close();
        }
    }
}

