package hudson.model;


import Item.CONFIGURE;
import Item.CREATE;
import Item.READ;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import hudson.security.ACL;
import hudson.security.ACLContext;
import hudson.security.AccessDeniedException2;
import java.io.File;
import java.net.HttpURLConnection;
import java.util.Arrays;
import jenkins.model.Jenkins;
import jenkins.model.ProjectNamingStrategy;
import org.apache.commons.io.FileUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.JenkinsRule.WebClient;
import org.jvnet.hudson.test.MockAuthorizationStrategy;
import org.jvnet.hudson.test.SleepBuilder;


public class AbstractItemTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    /**
     * Tests the reload functionality
     */
    @Test
    public void reload() throws Exception {
        Jenkins jenkins = j.jenkins;
        FreeStyleProject p = jenkins.createProject(FreeStyleProject.class, "foo");
        p.setDescription("Hello World");
        FreeStyleBuild b = j.assertBuildStatusSuccess(p.scheduleBuild2(0));
        b.setDescription("This is my build");
        // update on disk representation
        File f = p.getConfigFile().getFile();
        FileUtils.writeStringToFile(f, FileUtils.readFileToString(f).replaceAll("Hello World", "Good Evening"));
        // reload away
        p.doReload();
        Assert.assertEquals("Good Evening", p.getDescription());
        FreeStyleBuild b2 = p.getBuildByNumber(1);
        Assert.assertNotEquals(b, b2);// should be different object

        Assert.assertEquals(b.getDescription(), b2.getDescription());// but should have the same properties

    }

    @Test
    public void checkRenameValidity() throws Exception {
        FreeStyleProject p = j.createFreeStyleProject("foo");
        p.getBuildersList().add(new SleepBuilder(10));
        j.createFreeStyleProject("foo-exists");
        Assert.assertThat(checkNameAndReturnError(p, ""), Matchers.equalTo(Messages.Hudson_NoName()));
        Assert.assertThat(checkNameAndReturnError(p, ".."), Matchers.equalTo(Messages.Jenkins_NotAllowedName("..")));
        Assert.assertThat(checkNameAndReturnError(p, "50%"), Matchers.equalTo(Messages.Hudson_UnsafeChar('%')));
        Assert.assertThat(checkNameAndReturnError(p, "foo"), Matchers.equalTo(Messages.AbstractItem_NewNameUnchanged()));
        Assert.assertThat(checkNameAndReturnError(p, "foo-exists"), Matchers.equalTo(Messages.AbstractItem_NewNameInUse("foo-exists")));
        j.jenkins.setProjectNamingStrategy(new ProjectNamingStrategy.PatternProjectNamingStrategy("bar", "", false));
        Assert.assertThat(checkNameAndReturnError(p, "foo1"), Matchers.equalTo(jenkins.model.Messages.Hudson_JobNameConventionNotApplyed("foo1", "bar")));
        p.scheduleBuild2(0).waitForStart();
        Assert.assertThat(checkNameAndReturnError(p, "bar"), Matchers.equalTo(Messages.Job_NoRenameWhileBuilding()));
    }

    @Test
    public void checkRenamePermissions() throws Exception {
        j.jenkins.setSecurityRealm(j.createDummySecurityRealm());
        MockAuthorizationStrategy mas = new MockAuthorizationStrategy();
        mas.grant(CONFIGURE).everywhere().to("alice", "bob");
        mas.grant(READ).everywhere().to("alice");
        j.jenkins.setAuthorizationStrategy(mas);
        FreeStyleProject p = j.createFreeStyleProject("foo");
        j.createFreeStyleProject("foo-exists");
        try (ACLContext unused = ACL.as(User.getById("alice", true))) {
            Assert.assertThat(checkNameAndReturnError(p, "foo-exists"), Matchers.equalTo(Messages.AbstractItem_NewNameInUse("foo-exists")));
        }
        try (ACLContext unused = ACL.as(User.getById("bob", true))) {
            Assert.assertThat(checkNameAndReturnError(p, "foo-exists"), Matchers.equalTo(Messages.Jenkins_NotAllowedName("foo-exists")));
        }
        try (ACLContext unused = ACL.as(User.getById("carol", true))) {
            try {
                p.doCheckNewName("foo");
                Assert.fail("Expecting AccessDeniedException");
            } catch (AccessDeniedException2 e) {
                Assert.assertThat(e.permission, Matchers.equalTo(CREATE));
            }
        }
    }

    @Test
    public void renameViaRestApi() throws Exception {
        j.jenkins.setSecurityRealm(j.createDummySecurityRealm());
        MockAuthorizationStrategy mas = new MockAuthorizationStrategy();
        mas.grant(READ, Jenkins.READ).everywhere().to("alice", "bob");
        mas.grant(CONFIGURE).everywhere().to("alice");
        j.jenkins.setAuthorizationStrategy(mas);
        FreeStyleProject p = j.createFreeStyleProject("foo");
        WebClient w = j.createWebClient();
        WebRequest wr = new WebRequest(w.createCrumbedUrl(((p.getUrl()) + "confirmRename")), HttpMethod.POST);
        wr.setRequestParameters(Arrays.asList(new NameValuePair("newName", "bar")));
        w.login("alice", "alice");
        Page page = w.getPage(wr);
        Assert.assertThat(getPath(page.getUrl()), Matchers.equalTo(p.getUrl()));
        Assert.assertThat(p.getName(), Matchers.equalTo("bar"));
        wr = new WebRequest(w.createCrumbedUrl(((p.getUrl()) + "confirmRename")), HttpMethod.POST);
        wr.setRequestParameters(Arrays.asList(new NameValuePair("newName", "baz")));
        w.login("bob", "bob");
        w.setThrowExceptionOnFailingStatusCode(false);
        page = w.getPage(wr);
        Assert.assertEquals(HttpURLConnection.HTTP_FORBIDDEN, page.getWebResponse().getStatusCode());
        Assert.assertThat(p.getName(), Matchers.equalTo("bar"));
    }
}

