package hudson.security;


import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlFormUtil;
import hudson.model.User;
import jenkins.security.apitoken.ApiTokenTestHelper;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.JenkinsRule.WebClient;
import org.jvnet.hudson.test.recipes.PresetData;
import org.jvnet.hudson.test.recipes.PresetData.DataSet;


/**
 *
 *
 * @author Kohsuke Kawaguchi
 */
public class LoginTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    /**
     * Requesting a loginError page directly should result in a redirect,
     * on a non-secured Hudson.
     */
    @Test
    public void loginErrorRedirect1() throws Exception {
        verifyNotError(j.createWebClient());
    }

    /**
     * Same as {@link #loginErrorRedirect1()} if the user has already successfully authenticated.
     */
    @Test
    @PresetData(DataSet.ANONYMOUS_READONLY)
    public void loginErrorRedirect2() throws Exception {
        ApiTokenTestHelper.enableLegacyBehavior();
        // in a secured Hudson, the error page should render.
        WebClient wc = j.createWebClient();
        wc.assertFails("loginError", SC_UNAUTHORIZED);
        // but not once the user logs in.
        verifyNotError(wc.withBasicApiToken(User.getById("alice", true)));
    }

    /**
     * Test 'remember me' cookie
     */
    @Test
    @PresetData(DataSet.SECURED_ACEGI)
    public void loginRememberMe() throws Exception {
        WebClient wc = j.createWebClient();
        HtmlFormUtil.submit(prepareLoginFormWithRememberMeChecked(wc), null);
        Assert.assertNotNull(getRememberMeCookie(wc));
    }

    /**
     * Test that 'remember me' cookie will not be set if disabled even if requested by user.
     * This models the case when the feature is disabled between another user loading and submitting the login page.
     */
    @Test
    @PresetData(DataSet.SECURED_ACEGI)
    public void loginDisabledRememberMe() throws Exception {
        WebClient wc = j.createWebClient();
        HtmlForm form = prepareLoginFormWithRememberMeChecked(wc);
        j.jenkins.setDisableRememberMe(true);
        HtmlFormUtil.submit(form, null);
        Assert.assertNull(getRememberMeCookie(wc));
    }
}

