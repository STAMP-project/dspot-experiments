package org.keycloak.testsuite.broker;


import java.util.Properties;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.auth.page.login.UpdateAccount;
import org.keycloak.testsuite.pages.LoginPage;
import org.keycloak.testsuite.pages.social.AbstractSocialLoginPage;
import org.keycloak.testsuite.pages.social.BitbucketLoginPage;
import org.keycloak.testsuite.pages.social.FacebookLoginPage;
import org.keycloak.testsuite.pages.social.GitHubLoginPage;
import org.keycloak.testsuite.pages.social.GitLabLoginPage;
import org.keycloak.testsuite.pages.social.GoogleLoginPage;
import org.keycloak.testsuite.pages.social.InstagramLoginPage;
import org.keycloak.testsuite.pages.social.LinkedInLoginPage;
import org.keycloak.testsuite.pages.social.MicrosoftLoginPage;
import org.keycloak.testsuite.pages.social.OpenShiftLoginPage;
import org.keycloak.testsuite.pages.social.PayPalLoginPage;
import org.keycloak.testsuite.pages.social.StackOverflowLoginPage;
import org.keycloak.testsuite.pages.social.TwitterLoginPage;
import org.keycloak.testsuite.util.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


/**
 *
 *
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 * @author Vaclav Muzikar <vmuzikar@redhat.com>
 */
public class SocialLoginTest extends AbstractKeycloakTest {
    public static final String SOCIAL_CONFIG = "social.config";

    public static final String REALM = "social";

    public static final String EXCHANGE_CLIENT = "exchange-client";

    private static Properties config = new Properties();

    @Page
    private LoginPage loginPage;

    @Page
    private UpdateAccount updateAccountPage;

    public enum Provider {

        GOOGLE("google", GoogleLoginPage.class),
        GOOGLE_HOSTED_DOMAIN("google", "google-hosted-domain", GoogleLoginPage.class),
        GOOGLE_NON_MATCHING_HOSTED_DOMAIN("google", "google-hosted-domain", GoogleLoginPage.class),
        FACEBOOK("facebook", FacebookLoginPage.class),
        GITHUB("github", GitHubLoginPage.class),
        GITHUB_PRIVATE_EMAIL("github", "github-private-email", GitHubLoginPage.class),
        TWITTER("twitter", TwitterLoginPage.class),
        LINKEDIN("linkedin", LinkedInLoginPage.class),
        MICROSOFT("microsoft", MicrosoftLoginPage.class),
        PAYPAL("paypal", PayPalLoginPage.class),
        STACKOVERFLOW("stackoverflow", StackOverflowLoginPage.class),
        OPENSHIFT("openshift-v3", OpenShiftLoginPage.class),
        GITLAB("gitlab", GitLabLoginPage.class),
        BITBUCKET("bitbucket", BitbucketLoginPage.class),
        INSTAGRAM("instagram", InstagramLoginPage.class);
        private String id;

        private Class<? extends AbstractSocialLoginPage> pageObjectClazz;

        private String configId = null;

        Provider(String id, Class<? extends AbstractSocialLoginPage> pageObjectClazz) {
            this.id = id;
            this.pageObjectClazz = pageObjectClazz;
        }

        Provider(String id, String configId, Class<? extends AbstractSocialLoginPage> pageObjectClazz) {
            this.id = id;
            this.pageObjectClazz = pageObjectClazz;
            this.configId = configId;
        }

        public String id() {
            return id;
        }

        public Class<? extends AbstractSocialLoginPage> pageObjectClazz() {
            return pageObjectClazz;
        }

        public String configId() {
            return (configId) != null ? configId : id;
        }
    }

    private SocialLoginTest.Provider currentTestProvider = null;

    private AbstractSocialLoginPage currentSocialLoginPage = null;

    @Test
    public void openshiftLogin() {
        setTestProvider(SocialLoginTest.Provider.OPENSHIFT);
        performLogin();
        assertUpdateProfile(false, false, true);
        assertAccount();
    }

    @Test
    public void googleLogin() throws InterruptedException {
        setTestProvider(SocialLoginTest.Provider.GOOGLE);
        performLogin();
        assertAccount();
        testTokenExchange();
    }

    @Test
    public void googleHostedDomainLogin() throws InterruptedException {
        setTestProvider(SocialLoginTest.Provider.GOOGLE_HOSTED_DOMAIN);
        navigateToLoginPage();
        Assert.assertTrue(driver.getCurrentUrl().contains(("hd=" + (getConfig(SocialLoginTest.Provider.GOOGLE_HOSTED_DOMAIN, "hostedDomain")))));
        doLogin();
        assertAccount();
        testTokenExchange();
    }

    @Test
    public void googleNonMatchingHostedDomainLogin() throws InterruptedException {
        setTestProvider(SocialLoginTest.Provider.GOOGLE_NON_MATCHING_HOSTED_DOMAIN);
        navigateToLoginPage();
        Assert.assertTrue(driver.getCurrentUrl().contains("hd=non-matching-hosted-domain"));
        doLogin();
        // Just to be sure there's no redirect in progress
        WaitUtils.waitForPageToLoad();
        WebElement errorMessage = driver.findElement(By.xpath(".//p[@class='instruction']"));
        Assert.assertTrue(errorMessage.isDisplayed());
        Assert.assertEquals("Unexpected error when authenticating with identity provider", errorMessage.getText());
    }

    @Test
    public void bitbucketLogin() throws InterruptedException {
        setTestProvider(SocialLoginTest.Provider.BITBUCKET);
        performLogin();
        assertAccount();
        testTokenExchange();
    }

    @Test
    public void gitlabLogin() throws InterruptedException {
        setTestProvider(SocialLoginTest.Provider.GITLAB);
        performLogin();
        assertAccount();
        testTokenExchange();
    }

    @Test
    public void facebookLogin() throws InterruptedException {
        setTestProvider(SocialLoginTest.Provider.FACEBOOK);
        performLogin();
        assertAccount();
        testTokenExchange();
    }

    @Test
    public void instagramLogin() throws InterruptedException {
        setTestProvider(SocialLoginTest.Provider.INSTAGRAM);
        performLogin();
        assertUpdateProfile(false, false, true);
        assertAccount();
    }

    @Test
    public void githubLogin() throws InterruptedException {
        setTestProvider(SocialLoginTest.Provider.GITHUB);
        performLogin();
        assertAccount();
        testTokenExchange();
    }

    @Test
    public void githubPrivateEmailLogin() throws InterruptedException {
        setTestProvider(SocialLoginTest.Provider.GITHUB_PRIVATE_EMAIL);
        performLogin();
        assertAccount();
    }

    @Test
    public void twitterLogin() {
        setTestProvider(SocialLoginTest.Provider.TWITTER);
        performLogin();
        assertUpdateProfile(false, false, true);
        assertAccount();
    }

    @Test
    public void linkedinLogin() {
        setTestProvider(SocialLoginTest.Provider.LINKEDIN);
        performLogin();
        assertAccount();
    }

    @Test
    public void microsoftLogin() {
        setTestProvider(SocialLoginTest.Provider.MICROSOFT);
        performLogin();
        assertAccount();
    }

    @Test
    public void paypalLogin() {
        setTestProvider(SocialLoginTest.Provider.PAYPAL);
        performLogin();
        assertAccount();
    }

    @Test
    public void stackoverflowLogin() throws InterruptedException {
        setTestProvider(SocialLoginTest.Provider.STACKOVERFLOW);
        performLogin();
        assertUpdateProfile(false, false, true);
        assertAccount();
    }
}

