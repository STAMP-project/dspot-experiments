package org.keycloak.testsuite.broker;


import MailServerConfiguration.FROM;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.admin.ApiUtil;
import org.keycloak.testsuite.pages.ConsentPage;
import org.keycloak.testsuite.util.ClientBuilder;
import org.keycloak.testsuite.util.MailAssert;
import org.keycloak.testsuite.util.MailServer;
import org.keycloak.testsuite.util.RealmBuilder;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;


public abstract class AbstractBrokerTest extends AbstractInitializedBaseBrokerTest {
    public static final String ROLE_USER = "user";

    public static final String ROLE_MANAGER = "manager";

    public static final String ROLE_FRIENDLY_MANAGER = "friendly-manager";

    @Drone
    @SecondBrowser
    protected WebDriver driver2;

    @Test
    public void testLogInAsUserInIDP() {
        loginUser();
        testSingleLogout();
    }

    @Test
    public void loginWithExistingUser() {
        testLogInAsUserInIDP();
        Integer userCount = adminClient.realm(bc.consumerRealmName()).users().count();
        driver.navigate().to(getAccountUrl(bc.consumerRealmName()));
        log.debug(("Clicking social " + (bc.getIDPAlias())));
        accountLoginPage.clickSocial(bc.getIDPAlias());
        BrokerTestTools.waitForPage(driver, "log in to", true);
        org.keycloak.testsuite.Assert.assertTrue("Driver should be on the provider realm page right now", driver.getCurrentUrl().contains((("/auth/realms/" + (bc.providerRealmName())) + "/")));
        accountLoginPage.login(bc.getUserLogin(), bc.getUserPassword());
        Assert.assertEquals(((accountPage.buildUri().toASCIIString().replace("master", "consumer")) + "/"), driver.getCurrentUrl());
        Assert.assertEquals(userCount, adminClient.realm(bc.consumerRealmName()).users().count());
    }

    // KEYCLOAK-2957
    @Test
    public void testLinkAccountWithEmailVerified() {
        // start mail server
        MailServer.start();
        MailServer.createEmailAccount(BrokerTestConstants.USER_EMAIL, "password");
        try {
            configureSMPTServer();
            // create user on consumer's site who should be linked later
            String linkedUserId = createUser("consumer");
            // test
            driver.navigate().to(getAccountUrl(bc.consumerRealmName()));
            log.debug(("Clicking social " + (bc.getIDPAlias())));
            accountLoginPage.clickSocial(bc.getIDPAlias());
            BrokerTestTools.waitForPage(driver, "log in to", true);
            org.keycloak.testsuite.Assert.assertTrue("Driver should be on the provider realm page right now", driver.getCurrentUrl().contains((("/auth/realms/" + (bc.providerRealmName())) + "/")));
            log.debug("Logging in");
            accountLoginPage.login(bc.getUserLogin(), bc.getUserPassword());
            BrokerTestTools.waitForPage(driver, "update account information", false);
            org.keycloak.testsuite.Assert.assertTrue(updateAccountInformationPage.isCurrent());
            org.keycloak.testsuite.Assert.assertTrue("We must be on correct realm right now", driver.getCurrentUrl().contains((("/auth/realms/" + (bc.consumerRealmName())) + "/")));
            log.debug("Updating info on updateAccount page");
            updateAccountInformationPage.updateAccountInformation("Firstname", "Lastname");
            // link account by email
            BrokerTestTools.waitForPage(driver, "account already exists", false);
            idpConfirmLinkPage.clickLinkAccount();
            String url = MailAssert.assertEmailAndGetUrl(FROM, BrokerTestConstants.USER_EMAIL, "Someone wants to link your ", false);
            log.info(("navigating to url from email: " + url));
            driver.navigate().to(url);
            // test if user is logged in
            Assert.assertEquals(((accountPage.buildUri().toASCIIString().replace("master", "consumer")) + "/"), driver.getCurrentUrl());
            // test if the user has verified email
            Assert.assertTrue(adminClient.realm(bc.consumerRealmName()).users().get(linkedUserId).toRepresentation().isEmailVerified());
        } finally {
            ApiUtil.removeUserByUsername(adminClient.realm(bc.consumerRealmName()), "consumer");
            // stop mail server
            MailServer.stop();
        }
    }

    @Test
    public void testVerifyEmailInNewBrowserWithPreserveClient() {
        // start mail server
        MailServer.start();
        MailServer.createEmailAccount(BrokerTestConstants.USER_EMAIL, "password");
        try {
            configureSMPTServer();
            // create user on consumer's site who should be linked later
            String linkedUserId = createUser("consumer");
            driver.navigate().to(getLoginUrl(bc.consumerRealmName(), "broker-app"));
            log.debug(("Clicking social " + (bc.getIDPAlias())));
            accountLoginPage.clickSocial(bc.getIDPAlias());
            BrokerTestTools.waitForPage(driver, "log in to", true);
            org.keycloak.testsuite.Assert.assertTrue("Driver should be on the provider realm page right now", driver.getCurrentUrl().contains((("/auth/realms/" + (bc.providerRealmName())) + "/")));
            log.debug("Logging in");
            accountLoginPage.login(bc.getUserLogin(), bc.getUserPassword());
            BrokerTestTools.waitForPage(driver, "update account information", false);
            org.keycloak.testsuite.Assert.assertTrue(updateAccountInformationPage.isCurrent());
            org.keycloak.testsuite.Assert.assertTrue("We must be on correct realm right now", driver.getCurrentUrl().contains((("/auth/realms/" + (bc.consumerRealmName())) + "/")));
            log.debug("Updating info on updateAccount page");
            updateAccountInformationPage.updateAccountInformation("Firstname", "Lastname");
            // link account by email
            BrokerTestTools.waitForPage(driver, "account already exists", false);
            idpConfirmLinkPage.clickLinkAccount();
            String url = MailAssert.assertEmailAndGetUrl(FROM, BrokerTestConstants.USER_EMAIL, "Someone wants to link your ", false);
            log.info(("navigating to url from email in second browser: " + url));
            // navigate to url in the second browser
            driver2.navigate().to(url);
            final WebElement proceedLink = driver2.findElement(By.linkText("? Click here to proceed"));
            MatcherAssert.assertThat(proceedLink, Matchers.notNullValue());
            // check if the initial client is preserved
            String link = proceedLink.getAttribute("href");
            MatcherAssert.assertThat(link, Matchers.containsString("client_id=broker-app"));
            proceedLink.click();
            Assert.assertThat(driver2.getPageSource(), Matchers.containsString("You successfully verified your email. Please go back to your original browser and continue there with the login."));
            // test if the user has verified email
            Assert.assertTrue(adminClient.realm(bc.consumerRealmName()).users().get(linkedUserId).toRepresentation().isEmailVerified());
        } finally {
            ApiUtil.removeUserByUsername(adminClient.realm(bc.consumerRealmName()), "consumer");
            // stop mail server
            MailServer.stop();
        }
    }

    // KEYCLOAK-3267
    @Test
    public void loginWithExistingUserWithBruteForceEnabled() {
        adminClient.realm(bc.consumerRealmName()).update(RealmBuilder.create().bruteForceProtected(true).failureFactor(2).build());
        loginWithExistingUser();
        driver.navigate().to(getAccountPasswordUrl(bc.consumerRealmName()));
        accountPasswordPage.changePassword("password", "password");
        logoutFromRealm(bc.providerRealmName());
        driver.navigate().to(getAccountUrl(bc.consumerRealmName()));
        try {
            BrokerTestTools.waitForPage(driver, "log in to", true);
        } catch (TimeoutException e) {
            log.debug(driver.getTitle());
            log.debug(driver.getPageSource());
            org.keycloak.testsuite.Assert.fail("Timeout while waiting for login page");
        }
        for (int i = 0; i < 3; i++) {
            try {
                BrokerTestTools.waitForElementEnabled(driver, "login");
            } catch (TimeoutException e) {
                org.keycloak.testsuite.Assert.fail("Timeout while waiting for login element enabled");
            }
            accountLoginPage.login(bc.getUserLogin(), "invalid");
        }
        Assert.assertEquals("Invalid username or password.", accountLoginPage.getError());
        accountLoginPage.clickSocial(bc.getIDPAlias());
        try {
            BrokerTestTools.waitForPage(driver, "log in to", true);
        } catch (TimeoutException e) {
            log.debug(driver.getTitle());
            log.debug(driver.getPageSource());
            org.keycloak.testsuite.Assert.fail("Timeout while waiting for login page");
        }
        org.keycloak.testsuite.Assert.assertTrue("Driver should be on the provider realm page right now", driver.getCurrentUrl().contains((("/auth/realms/" + (bc.providerRealmName())) + "/")));
        accountLoginPage.login(bc.getUserLogin(), bc.getUserPassword());
        Assert.assertEquals("Account is disabled, contact admin.", errorPage.getError());
    }

    @Page
    ConsentPage consentPage;

    // KEYCLOAK-4181
    @Test
    public void loginWithExistingUserWithErrorFromProviderIdP() {
        ClientRepresentation client = adminClient.realm(bc.providerRealmName()).clients().findByClientId(bc.getIDPClientIdInProviderRealm(suiteContext)).get(0);
        adminClient.realm(bc.providerRealmName()).clients().get(client.getId()).update(ClientBuilder.edit(client).consentRequired(true).build());
        driver.navigate().to(getAccountUrl(bc.consumerRealmName()));
        log.debug(("Clicking social " + (bc.getIDPAlias())));
        accountLoginPage.clickSocial(bc.getIDPAlias());
        BrokerTestTools.waitForPage(driver, "log in to", true);
        org.keycloak.testsuite.Assert.assertTrue("Driver should be on the provider realm page right now", driver.getCurrentUrl().contains((("/auth/realms/" + (bc.providerRealmName())) + "/")));
        log.debug("Logging in");
        accountLoginPage.login(bc.getUserLogin(), bc.getUserPassword());
        driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.MINUTES);
        BrokerTestTools.waitForPage(driver, "grant access", false);
        consentPage.cancel();
        BrokerTestTools.waitForPage(driver, "log in to", true);
        // Revert consentRequired
        adminClient.realm(bc.providerRealmName()).clients().get(client.getId()).update(ClientBuilder.edit(client).consentRequired(false).build());
    }

    // KEYCLOAK-3987
    @Test
    public void grantNewRoleFromToken() {
        createRolesForRealm(bc.providerRealmName());
        createRolesForRealm(bc.consumerRealmName());
        createRoleMappersForConsumerRealm();
        RoleRepresentation managerRole = adminClient.realm(bc.providerRealmName()).roles().get(AbstractBrokerTest.ROLE_MANAGER).toRepresentation();
        RoleRepresentation userRole = adminClient.realm(bc.providerRealmName()).roles().get(AbstractBrokerTest.ROLE_USER).toRepresentation();
        UserResource userResource = adminClient.realm(bc.providerRealmName()).users().get(userId);
        userResource.roles().realmLevel().add(Collections.singletonList(managerRole));
        logInAsUserInIDPForFirstTime();
        Set<String> currentRoles = userResource.roles().realmLevel().listAll().stream().map(RoleRepresentation::getName).collect(Collectors.toSet());
        Assert.assertThat(currentRoles, Matchers.hasItems(AbstractBrokerTest.ROLE_MANAGER));
        Assert.assertThat(currentRoles, Matchers.not(Matchers.hasItems(AbstractBrokerTest.ROLE_USER)));
        logoutFromRealm(bc.consumerRealmName());
        userResource.roles().realmLevel().add(Collections.singletonList(userRole));
        logInAsUserInIDP();
        currentRoles = userResource.roles().realmLevel().listAll().stream().map(RoleRepresentation::getName).collect(Collectors.toSet());
        Assert.assertThat(currentRoles, Matchers.hasItems(AbstractBrokerTest.ROLE_MANAGER, AbstractBrokerTest.ROLE_USER));
        logoutFromRealm(bc.providerRealmName());
        logoutFromRealm(bc.consumerRealmName());
    }

    // KEYCLOAK-4016
    @Test
    public void testExpiredCode() {
        driver.navigate().to(getAccountUrl(bc.consumerRealmName()));
        log.debug("Expire all browser cookies");
        driver.manage().deleteAllCookies();
        log.debug(("Clicking social " + (bc.getIDPAlias())));
        accountLoginPage.clickSocial(bc.getIDPAlias());
        BrokerTestTools.waitForPage(driver, "sorry", false);
        errorPage.assertCurrent();
        String link = errorPage.getBackToApplicationLink();
        org.keycloak.testsuite.Assert.assertTrue(link.endsWith("/auth/realms/consumer/account"));
    }
}

