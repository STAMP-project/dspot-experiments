package jenkins.security;


import ApiTokenProperty.DescriptorImpl;
import ApiTokenStore.HashedToken;
import Cause.UserIdCause;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import hudson.Util;
import hudson.model.Cause;
import hudson.model.FreeStyleProject;
import hudson.model.User;
import hudson.security.ACL;
import hudson.security.ACLContext;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import jenkins.model.Jenkins;
import jenkins.security.apitoken.ApiTokenPropertyConfiguration;
import jenkins.security.apitoken.ApiTokenStore;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.JenkinsRule.WebClient;
import org.jvnet.hudson.test.recipes.LocalData;


/**
 *
 *
 * @author Kohsuke Kawaguchi
 */
// test no token are generated for new user with the global configuration set to false
public class ApiTokenPropertyTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    /**
     * Tests the UI interaction and authentication.
     */
    @Test
    public void basics() throws Exception {
        j.jenkins.setSecurityRealm(j.createDummySecurityRealm());
        User u = User.getById("foo", true);
        final ApiTokenProperty t = u.getProperty(ApiTokenProperty.class);
        final String token = t.getApiToken();
        // Make sure that user is able to get the token via the interface
        try (ACLContext acl = ACL.as(u)) {
            Assert.assertEquals("User is unable to get its own token", token, t.getApiToken());
        }
        // test the authentication via Token
        WebClient wc = createClientForUser("foo");
        Assert.assertEquals(u, wc.executeOnServer(new Callable<User>() {
            public User call() throws Exception {
                return User.current();
            }
        }));
        // Make sure the UI shows the token to the user
        HtmlPage config = wc.goTo(((u.getUrl()) + "/configure"));
        HtmlForm form = config.getFormByName("config");
        Assert.assertEquals(token, form.getInputByName("_.apiToken").getValueAttribute());
        // round-trip shouldn't change the API token
        j.submit(form);
        Assert.assertSame(t, u.getProperty(ApiTokenProperty.class));
    }

    @Test
    public void security49Upgrade() throws Exception {
        j.jenkins.setSecurityRealm(j.createDummySecurityRealm());
        User u = User.get("foo");
        String historicalInitialValue = Util.getDigestOf((((Jenkins.getInstance().getSecretKey()) + ":") + (u.getId())));
        // we won't accept historically used initial value as it may be compromised
        ApiTokenProperty t = new ApiTokenProperty(historicalInitialValue);
        u.addProperty(t);
        String apiToken1 = t.getApiToken();
        Assert.assertNotEquals(apiToken1, Util.getDigestOf(historicalInitialValue));
        // the replacement for the compromised value must be consistent and cannot be random
        ApiTokenProperty t2 = new ApiTokenProperty(historicalInitialValue);
        u.addProperty(t2);
        Assert.assertEquals(apiToken1, t2.getApiToken());
        // any other value is OK. those are changed values
        t = new ApiTokenProperty((historicalInitialValue + "somethingElse"));
        u.addProperty(t);
        Assert.assertEquals(t.getApiToken(), Util.getDigestOf((historicalInitialValue + "somethingElse")));
    }

    @Issue("SECURITY-200")
    @Test
    public void adminsShouldBeUnableToSeeTokensByDefault() throws Exception {
        j.jenkins.setSecurityRealm(j.createDummySecurityRealm());
        User u = User.get("foo");
        final ApiTokenProperty t = u.getProperty(ApiTokenProperty.class);
        final String token = t.getApiToken();
        // Make sure the UI does not show the token to another user
        WebClient wc = createClientForUser("bar");
        HtmlPage config = wc.goTo(((u.getUrl()) + "/configure"));
        HtmlForm form = config.getFormByName("config");
        Assert.assertEquals(Messages.ApiTokenProperty_ChangeToken_TokenIsHidden(), form.getInputByName("_.apiToken").getValueAttribute());
    }

    @Issue("SECURITY-200")
    @Test
    public void adminsShouldBeUnableToChangeTokensByDefault() throws Exception {
        j.jenkins.setSecurityRealm(j.createDummySecurityRealm());
        User foo = User.get("foo");
        User bar = User.get("bar");
        final ApiTokenProperty t = foo.getProperty(ApiTokenProperty.class);
        final ApiTokenProperty.DescriptorImpl descriptor = ((ApiTokenProperty.DescriptorImpl) (t.getDescriptor()));
        // Make sure that Admin can reset a token of another user
        WebClient wc = createClientForUser("bar").withThrowExceptionOnFailingStatusCode(false);
        HtmlPage requirePOST = wc.goTo(((((foo.getUrl()) + "/") + (descriptor.getDescriptorUrl())) + "/changeToken"));
        Assert.assertEquals("method should not be allowed", HttpURLConnection.HTTP_BAD_METHOD, requirePOST.getWebResponse().getStatusCode());
        wc.setThrowExceptionOnFailingStatusCode(true);
        WebRequest request = new WebRequest(new URL((((((j.getURL().toString()) + (foo.getUrl())) + "/") + (descriptor.getDescriptorUrl())) + "/changeToken")), HttpMethod.POST);
        HtmlPage res = wc.getPage(request);
        // TODO This nicer alternative requires https://github.com/jenkinsci/jenkins/pull/2268 or similar to work
        // HtmlPage res = requirePOST.getPage().getForms().get(0).getElementsByAttribute("input", "type", "submit").get(0).click();
        Assert.assertEquals("Update token response is incorrect", Messages.ApiTokenProperty_ChangeToken_SuccessHidden(), (("<div>" + (res.getBody().asText())) + "</div>"));
    }

    @Test
    public void postWithUsernameAndTokenInBasicAuthHeader() throws Exception {
        FreeStyleProject p = j.createFreeStyleProject("bar");
        j.jenkins.setSecurityRealm(j.createDummySecurityRealm());
        User.getById("foo", true);
        WebClient wc = createClientForUser("foo");
        WebRequest wr = new WebRequest(new URL(j.getURL(), "job/bar/build"), HttpMethod.POST);
        Assert.assertEquals(HttpURLConnection.HTTP_CREATED, wc.getPage(wr).getWebResponse().getStatusCode());
        j.waitUntilNoActivity();
        Cause.UserIdCause triggeredBy = p.getBuildByNumber(1).getCause(UserIdCause.class);
        Assert.assertEquals("foo", triggeredBy.getUserId());
    }

    @Test
    @Issue("JENKINS-32776")
    public void generateNewTokenWithoutName() throws Exception {
        j.jenkins.setCrumbIssuer(null);
        j.jenkins.setSecurityRealm(j.createDummySecurityRealm());
        // user is still able to connect with legacy token
        User admin = User.getById("admin", true);
        WebClient wc = j.createWebClient();
        wc.withBasicCredentials("admin", "admin");
        ApiTokenPropertyTest.GenerateNewTokenResponse token1 = generateNewToken(wc, "admin", "");
        Assert.assertNotEquals("", token1.tokenName.trim());
        ApiTokenPropertyTest.GenerateNewTokenResponse token2 = generateNewToken(wc, "admin", "New Token");
        Assert.assertEquals("New Token", token2.tokenName);
    }

    @Test
    @LocalData
    @Issue("JENKINS-32776")
    public void migrationFromLegacyToken() throws Exception {
        j.jenkins.setCrumbIssuer(null);
        // user is still able to connect with legacy token
        User admin = User.getById("admin", false);
        Assert.assertNotNull("Admin user not configured correctly in local data", admin);
        ApiTokenProperty apiTokenProperty = admin.getProperty(ApiTokenProperty.class);
        WebClient wc = j.createWebClient();
        wc.withBasicCredentials("admin", "admin");
        checkUserIsConnected(wc);
        // 7be8e81ad5a350fa3f3e2acfae4adb14
        String localLegacyToken = apiTokenProperty.getApiTokenInsecure();
        wc = j.createWebClient();
        wc.withBasicCredentials("admin", localLegacyToken);
        checkUserIsConnected(wc);
        // can still renew it after (using API)
        Assert.assertEquals(1, apiTokenProperty.getTokenList().size());
        apiTokenProperty.changeApiToken();
        Assert.assertEquals(1, apiTokenProperty.getTokenList().size());
        String newLegacyToken = apiTokenProperty.getApiTokenInsecure();
        // use the new legacy api token
        wc = j.createWebClient();
        wc.withBasicCredentials("admin", newLegacyToken);
        checkUserIsConnected(wc);
        // but previous one is not more usable
        wc = j.createWebClient();
        wc.withBasicCredentials("admin", localLegacyToken);
        checkUserIsNotConnected(wc);
        // ===== new system =====
        // revoke the legacy
        ApiTokenStore.HashedToken legacyToken = apiTokenProperty.getTokenStore().getLegacyToken();
        Assert.assertNotNull(legacyToken);
        String legacyUuid = legacyToken.getUuid();
        wc = j.createWebClient();
        wc.withBasicCredentials("admin", newLegacyToken);
        revokeToken(wc, "admin", legacyUuid);
        Assert.assertEquals(0, apiTokenProperty.getTokenList().size());
        // check it does not work any more
        wc = j.createWebClient();
        wc.withBasicCredentials("admin", newLegacyToken);
        checkUserIsNotConnected(wc);
        wc = j.createWebClient();
        wc.withBasicCredentials("admin", localLegacyToken);
        checkUserIsNotConnected(wc);
        // ensure the user can still connect using its username / password
        wc = j.createWebClient();
        wc.withBasicCredentials("admin", "admin");
        checkUserIsConnected(wc);
        // generate new token with the new system
        wc = j.createWebClient();
        wc.login("admin", "admin");
        ApiTokenPropertyTest.GenerateNewTokenResponse newToken = generateNewToken(wc, "admin", "New Token");
        // use the new one
        wc = j.createWebClient();
        wc.withBasicCredentials("admin", newToken.tokenValue);
        checkUserIsConnected(wc);
    }

    @Test
    @Issue("JENKINS-32776")
    public void legacyTokenChange() throws Exception {
        j.jenkins.setCrumbIssuer(null);
        j.jenkins.setSecurityRealm(j.createDummySecurityRealm());
        ApiTokenPropertyConfiguration config = ApiTokenPropertyConfiguration.get();
        config.setTokenGenerationOnCreationEnabled(true);
        User user = User.getById("user", true);
        WebClient wc = j.createWebClient();
        wc.withBasicCredentials("user", "user");
        ApiTokenProperty apiTokenProperty = user.getProperty(ApiTokenProperty.class);
        {
            // with one legacy token, we can change it using web UI or direct internal call
            String currentLegacyToken = apiTokenProperty.getApiToken();
            Assert.assertEquals(1, apiTokenProperty.getTokenList().size());
            config.setCreationOfLegacyTokenEnabled(true);
            {
                // change using web UI
                changeLegacyToken(wc, "user", true);
                String newLegacyToken = apiTokenProperty.getApiToken();
                Assert.assertNotEquals(newLegacyToken, currentLegacyToken);
                // change using internal call
                apiTokenProperty.changeApiToken();
                String newLegacyToken2 = apiTokenProperty.getApiToken();
                Assert.assertNotEquals(newLegacyToken2, newLegacyToken);
                Assert.assertNotEquals(newLegacyToken2, currentLegacyToken);
                currentLegacyToken = newLegacyToken2;
            }
            config.setCreationOfLegacyTokenEnabled(false);
            {
                // change using web UI
                changeLegacyToken(wc, "user", true);
                String newLegacyToken = apiTokenProperty.getApiToken();
                Assert.assertNotEquals(newLegacyToken, currentLegacyToken);
                // change using internal call
                apiTokenProperty.changeApiToken();
                String newLegacyToken2 = apiTokenProperty.getApiToken();
                Assert.assertNotEquals(newLegacyToken2, newLegacyToken);
                Assert.assertNotEquals(newLegacyToken2, currentLegacyToken);
            }
        }
        {
            // but without any legacy token, the direct internal call remains but web UI depends on config
            revokeAllToken(wc, user);
            checkCombinationWithConfigAndMethodForLegacyTokenCreation(config, wc, user);
        }
        {
            // only the legacy token have impact on that capability
            generateNewToken(wc, "user", "New token");
            checkCombinationWithConfigAndMethodForLegacyTokenCreation(config, wc, user);
        }
    }

    public static class GenerateNewTokenResponse {
        public String tokenUuid;

        public String tokenName;

        public String tokenValue;
    }
}

