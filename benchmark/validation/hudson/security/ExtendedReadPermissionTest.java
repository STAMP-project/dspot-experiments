package hudson.security;


import Item.EXTENDED_READ;
import JenkinsRule.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.net.HttpURLConnection;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.recipes.LocalData;


/**
 *
 *
 * @author dty
 */
// TODO configureLink; viewConfigurationLink; matrixWithPermissionEnabled; matrixWithPermissionDisabled
public class ExtendedReadPermissionTest {
    @Rule
    public JenkinsRule r = new JenkinsRule();

    private static boolean enabled;

    @LocalData
    @Test
    public void readOnlyConfigAccessWithPermissionEnabled() throws Exception {
        setPermissionEnabled(true);
        AuthorizationStrategy as = r.jenkins.getAuthorizationStrategy();
        Assert.assertTrue("Expecting GlobalMatrixAuthorizationStrategy", (as instanceof GlobalMatrixAuthorizationStrategy));
        GlobalMatrixAuthorizationStrategy gas = ((GlobalMatrixAuthorizationStrategy) (as));
        Assert.assertTrue("Charlie should have extended read for this test", gas.hasExplicitPermission("charlie", EXTENDED_READ));
        JenkinsRule.WebClient wc = r.createWebClient();
        wc.withBasicCredentials("charlie");
        HtmlPage page = wc.goTo("job/a/configure");
        HtmlForm form = page.getFormByName("config");
        HtmlButton saveButton = r.getButtonByCaption(form, "Save");
        Assert.assertNull(saveButton);
    }

    @LocalData
    @Test
    public void readOnlyConfigAccessWithPermissionDisabled() throws Exception {
        setPermissionEnabled(false);
        AuthorizationStrategy as = r.jenkins.getAuthorizationStrategy();
        Assert.assertTrue("Expecting GlobalMatrixAuthorizationStrategy", (as instanceof GlobalMatrixAuthorizationStrategy));
        GlobalMatrixAuthorizationStrategy gas = ((GlobalMatrixAuthorizationStrategy) (as));
        Assert.assertFalse("Charlie should not have extended read for this test", gas.hasExplicitPermission("charlie", EXTENDED_READ));
        JenkinsRule.WebClient wc = r.createWebClient();
        wc.withBasicCredentials("charlie");
        wc.assertFails("job/a/configure", HttpURLConnection.HTTP_FORBIDDEN);
    }

    @LocalData
    @Test
    public void noConfigAccessWithPermissionEnabled() throws Exception {
        setPermissionEnabled(true);
        AuthorizationStrategy as = r.jenkins.getAuthorizationStrategy();
        Assert.assertTrue("Expecting GlobalMatrixAuthorizationStrategy", (as instanceof GlobalMatrixAuthorizationStrategy));
        GlobalMatrixAuthorizationStrategy gas = ((GlobalMatrixAuthorizationStrategy) (as));
        Assert.assertFalse("Bob should not have extended read for this test", gas.hasExplicitPermission("bob", EXTENDED_READ));
        JenkinsRule.WebClient wc = r.createWebClient();
        wc.withBasicCredentials("bob");
        wc.assertFails("job/a/configure", HttpURLConnection.HTTP_FORBIDDEN);
    }
}

