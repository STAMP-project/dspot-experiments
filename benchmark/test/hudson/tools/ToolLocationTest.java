package hudson.tools;


import Ant.AntInstallation;
import Maven.DescriptorImpl;
import Maven.MavenInstallation;
import hudson.model.JDK;
import hudson.tasks.Ant;
import hudson.tasks.Maven;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.recipes.LocalData;


/**
 *
 *
 * @author huybrechts
 */
public class ToolLocationTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    /**
     * Test xml compatibility since 'extends ToolInstallation'
     */
    @Test
    @LocalData
    public void toolCompatibility() {
        Maven[] maven = j.jenkins.getDescriptorByType(DescriptorImpl.class).getInstallations();
        Assert.assertEquals(maven.length, 1);
        Assert.assertEquals(maven[0].getHome(), "bar");
        Assert.assertEquals(maven[0].getName(), "Maven 1");
        Ant[] ant = j.jenkins.getDescriptorByType(Ant.DescriptorImpl.class).getInstallations();
        Assert.assertEquals(ant.length, 1);
        Assert.assertEquals(ant[0].getHome(), "foo");
        Assert.assertEquals(ant[0].getName(), "Ant 1");
        JDK[] jdk = j.jenkins.getDescriptorByType(JDK.DescriptorImpl.class).getInstallations();
        Assert.assertEquals(Arrays.asList(jdk), j.jenkins.getJDKs());
        Assert.assertEquals(2, jdk.length);// HudsonTestCase adds a 'default' JDK

        Assert.assertEquals("default", jdk[1].getName());// make sure it's really that we're seeing

        Assert.assertEquals("FOOBAR", jdk[0].getHome());
        Assert.assertEquals("FOOBAR", jdk[0].getJavaHome());
        Assert.assertEquals("1.6", jdk[0].getName());
    }
}

