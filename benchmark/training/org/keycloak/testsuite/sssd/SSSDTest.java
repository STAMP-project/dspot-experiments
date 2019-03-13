package org.keycloak.testsuite.sssd;


import org.apache.commons.configuration.PropertiesConfiguration;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.logging.Logger;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.Assert;
import org.keycloak.testsuite.AssertEvents;
import org.keycloak.testsuite.pages.AccountPasswordPage;
import org.keycloak.testsuite.pages.AccountUpdateProfilePage;
import org.keycloak.testsuite.pages.LoginPage;


public class SSSDTest extends AbstractKeycloakTest {
    private static final Logger log = Logger.getLogger(SSSDTest.class);

    private static final String DISPLAY_NAME = "Test user federation";

    private static final String PROVIDER_NAME = "sssd";

    private static final String REALM_NAME = "test";

    private static final String sssdConfigPath = "sssd/sssd.properties";

    private static final String DISABLED_USER = "disabled";

    private static final String NO_EMAIL_USER = "noemail";

    private static final String ADMIN_USER = "admin";

    private static PropertiesConfiguration sssdConfig;

    @Page
    protected LoginPage accountLoginPage;

    @Page
    protected AccountPasswordPage changePasswordPage;

    @Page
    protected AccountUpdateProfilePage profilePage;

    @Rule
    public AssertEvents events = new AssertEvents(this);

    private String SSSDFederationID;

    @Test
    public void testInvalidPassword() {
        String username = getUsername();
        SSSDTest.log.debug(("Testing invalid password for user " + username));
        profilePage.open();
        Assert.assertEquals("Browser should be on login page now", ("Log in to " + (SSSDTest.REALM_NAME)), driver.getTitle());
        accountLoginPage.login(username, "invalid-password");
        Assert.assertEquals("Invalid username or password.", accountLoginPage.getError());
    }

    @Test
    public void testDisabledUser() {
        String username = getUser(SSSDTest.DISABLED_USER);
        Assume.assumeTrue("Ignoring test no disabled user configured", (username != null));
        SSSDTest.log.debug(("Testing disabled user " + username));
        profilePage.open();
        Assert.assertEquals("Browser should be on login page now", ("Log in to " + (SSSDTest.REALM_NAME)), driver.getTitle());
        accountLoginPage.login(username, getPassword(username));
        Assert.assertEquals("Invalid username or password.", accountLoginPage.getError());
    }

    @Test
    public void testAdmin() {
        String username = getUser(SSSDTest.ADMIN_USER);
        Assume.assumeTrue("Ignoring test no admin user configured", (username != null));
        SSSDTest.log.debug(("Testing password for user " + username));
        profilePage.open();
        Assert.assertEquals("Browser should be on login page now", ("Log in to " + (SSSDTest.REALM_NAME)), driver.getTitle());
        accountLoginPage.login(username, getPassword(username));
        Assert.assertTrue(profilePage.isCurrent());
    }

    @Test
    public void testExistingUserLogIn() {
        SSSDTest.log.debug("Testing correct password");
        for (String username : getUsernames()) {
            profilePage.open();
            Assert.assertEquals("Browser should be on login page now", ("Log in to " + (SSSDTest.REALM_NAME)), driver.getTitle());
            accountLoginPage.login(username, getPassword(username));
            Assert.assertTrue(profilePage.isCurrent());
            verifyUserGroups(username, getGroups(username));
            profilePage.logout();
        }
    }

    @Test
    public void testExistingUserWithNoEmailLogIn() {
        SSSDTest.log.debug("Testing correct password, but no e-mail provided");
        String username = getUser(SSSDTest.NO_EMAIL_USER);
        profilePage.open();
        Assert.assertEquals("Browser should be on login page now", ("Log in to " + (SSSDTest.REALM_NAME)), driver.getTitle());
        accountLoginPage.login(username, getPassword(username));
        Assert.assertTrue(profilePage.isCurrent());
    }

    @Test
    public void testDeleteSSSDFederationProvider() {
        SSSDTest.log.debug("Testing correct password");
        profilePage.open();
        String username = getUsername();
        Assert.assertEquals("Browser should be on login page now", ("Log in to " + (SSSDTest.REALM_NAME)), driver.getTitle());
        accountLoginPage.login(username, getPassword(username));
        Assert.assertTrue(profilePage.isCurrent());
        verifyUserGroups(username, getGroups(username));
        int componentsListSize = adminClient.realm(SSSDTest.REALM_NAME).components().query().size();
        adminClient.realm(SSSDTest.REALM_NAME).components().component(SSSDFederationID).remove();
        Assert.assertEquals((componentsListSize - 1), adminClient.realm(SSSDTest.REALM_NAME).components().query().size());
    }

    @Test
    public void changeReadOnlyProfile() throws Exception {
        String username = getUsername();
        profilePage.open();
        accountLoginPage.login(username, getPassword(username));
        Assert.assertEquals(username, profilePage.getUsername());
        Assert.assertEquals(SSSDTest.sssdConfig.getProperty((("user." + username) + ".firstname")), profilePage.getFirstName());
        Assert.assertEquals(SSSDTest.sssdConfig.getProperty((("user." + username) + ".lastname")), profilePage.getLastName());
        Assert.assertEquals(SSSDTest.sssdConfig.getProperty((("user." + username) + ".mail")), profilePage.getEmail());
        profilePage.updateProfile("New first", "New last", "new@email.com");
        Assert.assertEquals("You can't update your account as it is read-only.", profilePage.getError());
    }

    @Test
    public void changeReadOnlyPassword() {
        String username = getUsername();
        changePasswordPage.open();
        accountLoginPage.login(username, getPassword(username));
        changePasswordPage.changePassword(getPassword(username), "new-password", "new-password");
        Assert.assertEquals("You can't update your password as your account is read only.", profilePage.getError());
    }
}

