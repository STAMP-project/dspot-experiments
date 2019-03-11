package hudson.slaves;


import Cloud.PROVISION;
import Computer.PERMISSIONS;
import Jenkins.ADMINISTER;
import Jenkins.ANONYMOUS;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import hudson.model.Action;
import hudson.model.Label;
import hudson.security.Permission;
import hudson.security.SidACL;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import javax.annotation.Nonnull;
import jenkins.model.TransientActionFactory;
import org.acegisecurity.acls.sid.Sid;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.TestExtension;
import org.jvnet.hudson.test.WithoutJenkins;
import org.kohsuke.stapler.StaplerResponse;

import static Cloud.PROVISION;


public class CloudTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    @WithoutJenkins
    @Issue("JENKINS-37616")
    public void provisionPermissionShouldBeIndependentFromAdminister() throws Exception {
        SidACL acl = new SidACL() {
            @Override
            protected Boolean hasPermission(Sid p, Permission permission) {
                return permission == (PROVISION);
            }
        };
        Assert.assertTrue(acl.hasPermission(ANONYMOUS, PROVISION));
        Assert.assertFalse(acl.hasPermission(ANONYMOUS, ADMINISTER));
        Assert.assertEquals(PROVISION, PERMISSIONS.find("Provision"));
    }

    @Test
    @Issue("JENKINS-37616")
    public void ensureProvisionPermissionIsLoadable() throws Exception {
        // Name introduced by JENKINS-37616
        Permission p = Permission.fromId("hudson.model.Computer.Provision");
        Assert.assertEquals("Provision", p.name);
    }

    @Test
    public void ui() throws Exception {
        CloudTest.ACloud aCloud = new CloudTest.ACloud("a", "0");
        j.jenkins.clouds.add(aCloud);
        Assert.assertThat(getAllActions(), Matchers.containsInAnyOrder(Matchers.instanceOf(CloudTest.TaskCloudAction.class), Matchers.instanceOf(CloudTest.ReportingCloudAction.class)));
        HtmlPage page = j.createWebClient().goTo(getUrl());
        String out = page.getWebResponse().getContentAsString();
        Assert.assertThat(out, Matchers.containsString("Cloud a"));// index.jelly

        Assert.assertThat(out, Matchers.containsString("Top cloud view."));// top.jelly

        Assert.assertThat(out, Matchers.containsString("custom cloud main groovy"));// main.jelly

        Assert.assertThat(out, Matchers.containsString("Task Action"));// TaskCloudAction

        Assert.assertThat(out, Matchers.containsString("Sidepanel action box."));// TaskCloudAction/box.jelly

        Assert.assertThat(out, Matchers.containsString("Report Here"));// ReportingCloudAction/summary.jelly

        HtmlPage actionPage = page.getAnchorByText("Task Action").click();
        out = actionPage.getWebResponse().getContentAsString();
        Assert.assertThat(out, Matchers.containsString("doIndex called"));// doIndex

    }

    public static final class ACloud extends AbstractCloudImpl {
        protected ACloud(String name, String instanceCapStr) {
            super(name, instanceCapStr);
        }

        @Override
        public Collection<NodeProvisioner.PlannedNode> provision(Label label, int excessWorkload) {
            return Collections.emptyList();
        }

        @Override
        public boolean canProvision(Label label) {
            return false;
        }
    }

    @TestExtension
    public static final class CloudActionFactory extends TransientActionFactory<Cloud> {
        @Override
        public Class<Cloud> type() {
            return Cloud.class;
        }

        @Nonnull
        @Override
        public Collection<? extends Action> createFor(@Nonnull
        Cloud target) {
            return Arrays.asList(new CloudTest.TaskCloudAction(), new CloudTest.ReportingCloudAction());
        }
    }

    @TestExtension
    public static final class TaskCloudAction implements Action {
        @Override
        public String getIconFileName() {
            return "notepad";
        }

        @Override
        public String getDisplayName() {
            return "Task Action";
        }

        @Override
        public String getUrlName() {
            return "task";
        }

        public void doIndex(StaplerResponse rsp) throws IOException {
            rsp.getOutputStream().println("doIndex called");
        }
    }

    @TestExtension
    public static final class ReportingCloudAction implements Action {
        @Override
        public String getIconFileName() {
            return null;// not task bar icon

        }

        @Override
        public String getDisplayName() {
            return "Reporting Action";
        }

        @Override
        public String getUrlName() {
            return null;// not URL space

        }
    }
}

