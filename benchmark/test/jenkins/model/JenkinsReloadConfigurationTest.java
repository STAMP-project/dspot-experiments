package jenkins.model;


import Mailer.DescriptorImpl;
import hudson.model.FreeStyleProject;
import hudson.model.ListView;
import hudson.model.Node;
import hudson.model.User;
import hudson.tasks.Mailer;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;


/**
 * Ensure direct configuration change on disk is reflected after reload.
 *
 * @author ogondza
 */
public class JenkinsReloadConfigurationTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    public void reloadMasterConfig() throws Exception {
        Node node = j.jenkins;
        node.setLabelString("oldLabel");
        modifyNode(node);
        Assert.assertEquals("newLabel", node.getLabelString());
    }

    @Test
    public void reloadSlaveConfig() throws Exception {
        Node node = j.createSlave("a_slave", "oldLabel", null);
        modifyNode(node);
        node = j.jenkins.getNode("a_slave");
        Assert.assertEquals("newLabel", node.getLabelString());
    }

    @Test
    public void reloadUserConfigUsingGlobalReload() throws Exception {
        String originalName = "oldName";
        String temporaryName = "newName";
        {
            User user = User.get("some_user", true, null);
            user.setFullName(originalName);
            user.save();
            Assert.assertEquals(originalName, user.getFullName());
            user.setFullName(temporaryName);
            Assert.assertEquals(temporaryName, user.getFullName());
        }
        j.jenkins.reload();
        {
            Assert.assertEquals(originalName, User.getById("some_user", false).getFullName());
        }
    }

    @Test
    public void reloadJobConfig() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject("a_project");
        project.setDescription("oldDescription");
        replace("jobs/a_project/config.xml", "oldDescription", "newDescription");
        Assert.assertEquals("oldDescription", project.getDescription());
        j.jenkins.reload();
        project = j.jenkins.getItem("a_project", j.jenkins, FreeStyleProject.class);
        Assert.assertEquals("newDescription", project.getDescription());
    }

    @Test
    public void reloadViewConfig() throws Exception {
        ListView view = new ListView("a_view");
        j.jenkins.addView(view);
        view.setIncludeRegex("oldIncludeRegex");
        view.save();
        replace("config.xml", "oldIncludeRegex", "newIncludeRegex");
        Assert.assertEquals("oldIncludeRegex", view.getIncludeRegex());
        j.jenkins.reload();
        view = ((ListView) (j.jenkins.getView("a_view")));
        Assert.assertEquals("newIncludeRegex", view.getIncludeRegex());
    }

    @Test
    public void reloadDescriptorConfig() {
        Mailer.DescriptorImpl desc = mailerDescriptor();
        desc.setDefaultSuffix("@oldSuffix");
        desc.save();
        replace("hudson.tasks.Mailer.xml", "@oldSuffix", "@newSuffix");
        Assert.assertEquals("@oldSuffix", desc.getDefaultSuffix());
        desc.load();
        Assert.assertEquals("@newSuffix", desc.getDefaultSuffix());
    }
}

