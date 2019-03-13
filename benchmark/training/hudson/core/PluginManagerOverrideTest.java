package hudson.core;


import JenkinsRule.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import hudson.PluginManagerStaplerOverride;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.TestExtension;


/**
 * Verify that the PluginManagerStaplerOverride extensions register and allow safely modifying PluginManager views
 *
 * @author Sam Van Oort
 */
public class PluginManagerOverrideTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    public void testViewOverrides() throws Exception {
        // Verify extension registered correctly and comes back in overrides
        Assert.assertEquals(1, PluginManagerStaplerOverride.all().size());
        Assert.assertTrue(((PluginManagerStaplerOverride.all().get(0)) instanceof PluginManagerOverrideTest.BasicPluginManagerOverride));
        // Verify we can load untouched resources
        JenkinsRule.WebClient client = j.createWebClient();
        Assert.assertEquals(200, client.goTo("self/pluginManager/available").getWebResponse().getStatusCode());
        // Verify new view loads
        HtmlPage p = j.createWebClient().goTo("self/pluginManager/newview");
        Assert.assertEquals("LoremIpsum", p.getElementById("dummyElement").getTextContent());
    }

    /**
     * Micro-implementation simply to allow adding a view resource
     */
    @TestExtension("testViewOverrides")
    public static class BasicPluginManagerOverride extends PluginManagerStaplerOverride {}
}

