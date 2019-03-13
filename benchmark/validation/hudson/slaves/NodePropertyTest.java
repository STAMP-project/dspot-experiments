package hudson.slaves;


import com.gargoylesoftware.htmlunit.html.HtmlForm;
import hudson.model.Descriptor;
import hudson.model.Descriptor.FormException;
import hudson.model.Slave;
import java.util.logging.Level;
import net.sf.json.JSONObject;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.LoggerRule;
import org.jvnet.hudson.test.TestExtension;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;


/**
 *
 *
 * @author Kohsuke Kawaguchi
 */
public class NodePropertyTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Rule
    public LoggerRule logs = new LoggerRule();

    @Test
    public void invisibleProperty() throws Exception {
        logs.record(Descriptor.class, Level.ALL);
        DumbSlave s = j.createSlave();
        NodePropertyTest.InvisibleProperty before = new NodePropertyTest.InvisibleProperty();
        s.getNodeProperties().add(before);
        Assert.assertFalse(before.reconfigured);
        DumbSlave s2 = j.configRoundtrip(s);
        Assert.assertNotSame(s, s2);
        NodePropertyTest.InvisibleProperty after = s2.getNodeProperties().get(NodePropertyTest.InvisibleProperty.class);
        Assert.assertSame(before, after);
        Assert.assertTrue(after.reconfigured);
    }

    public static class InvisibleProperty extends NodeProperty<Slave> {
        boolean reconfigured = false;

        @Override
        public NodeProperty<?> reconfigure(StaplerRequest req, JSONObject form) throws FormException {
            reconfigured = true;
            return this;
        }

        @TestExtension("invisibleProperty")
        public static class DescriptorImpl extends NodePropertyDescriptor {}
    }

    @Test
    public void basicConfigRoundtrip() throws Exception {
        DumbSlave s = j.createSlave();
        HtmlForm f = j.createWebClient().goTo((("computer/" + (s.getNodeName())) + "/configure")).getFormByName("config");
        click();
        j.submit(f);
        NodePropertyTest.PropertyImpl p = j.jenkins.getNode(s.getNodeName()).getNodeProperties().get(NodePropertyTest.PropertyImpl.class);
        Assert.assertEquals("Duke", p.name);
        p.name = "Kohsuke";
        j.configRoundtrip(s);
        NodePropertyTest.PropertyImpl p2 = j.jenkins.getNode(s.getNodeName()).getNodeProperties().get(NodePropertyTest.PropertyImpl.class);
        Assert.assertNotSame(p, p2);
        j.assertEqualDataBoundBeans(p, p2);
    }

    public static class PropertyImpl extends NodeProperty<Slave> {
        public String name;

        @DataBoundConstructor
        public PropertyImpl(String name) {
            this.name = name;
        }

        @TestExtension("basicConfigRoundtrip")
        public static class DescriptorImpl extends NodePropertyDescriptor {}
    }
}

