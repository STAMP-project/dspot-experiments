/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.keycloak.testsuite.forms;


import Constants.EXECUTION;
import org.hamcrest.Matchers;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.AbstractTestRealmKeycloakTest;
import org.keycloak.testsuite.ActionURIUtils;
import org.keycloak.testsuite.AssertEvents;
import org.keycloak.testsuite.pages.AppPage;
import org.keycloak.testsuite.pages.ErrorPage;
import org.keycloak.testsuite.pages.InfoPage;
import org.keycloak.testsuite.pages.LoginExpiredPage;
import org.keycloak.testsuite.pages.LoginPage;
import org.keycloak.testsuite.pages.LoginPasswordResetPage;
import org.keycloak.testsuite.pages.LoginPasswordUpdatePage;
import org.keycloak.testsuite.pages.LoginUpdateProfilePage;
import org.keycloak.testsuite.pages.OAuthGrantPage;
import org.keycloak.testsuite.pages.RegisterPage;
import org.keycloak.testsuite.pages.VerifyEmailPage;
import org.keycloak.testsuite.util.GreenMailRule;


/**
 * Tries to simulate testing with multiple browser tabs
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class MultipleTabsLoginTest extends AbstractTestRealmKeycloakTest {
    private String userId;

    @Rule
    public GreenMailRule greenMail = new GreenMailRule();

    @Page
    protected AppPage appPage;

    @Page
    protected LoginPage loginPage;

    @Page
    protected ErrorPage errorPage;

    @Page
    protected InfoPage infoPage;

    @Page
    protected VerifyEmailPage verifyEmailPage;

    @Page
    protected LoginPasswordResetPage resetPasswordPage;

    @Page
    protected LoginPasswordUpdatePage updatePasswordPage;

    @Page
    protected LoginUpdateProfilePage updateProfilePage;

    @Page
    protected LoginExpiredPage loginExpiredPage;

    @Page
    protected RegisterPage registerPage;

    @Page
    protected OAuthGrantPage grantPage;

    @Rule
    public AssertEvents events = new AssertEvents(this);

    @Test
    public void multipleTabsParallelLoginTest() {
        oauth.openLoginForm();
        loginPage.assertCurrent();
        loginPage.login("login-test", "password");
        updatePasswordPage.assertCurrent();
        String tab1Url = driver.getCurrentUrl();
        // Simulate login in different browser tab tab2. I will be on loginPage again.
        oauth.openLoginForm();
        loginPage.assertCurrent();
        // Login in tab2
        loginPage.login("login-test", "password");
        updatePasswordPage.assertCurrent();
        updatePasswordPage.changePassword("password", "password");
        updateProfilePage.update("John", "Doe3", "john@doe3.com");
        appPage.assertCurrent();
        // Try to go back to tab 1. We should have ALREADY_LOGGED_IN info page
        driver.navigate().to(tab1Url);
        infoPage.assertCurrent();
        org.keycloak.testsuite.Assert.assertEquals("You are already logged in.", infoPage.getInfo());
        infoPage.clickBackToApplicationLink();
        appPage.assertCurrent();
    }

    @Test
    public void expiredAuthenticationAction_currentCodeExpiredExecution() {
        // Simulate to open login form in 2 tabs
        oauth.openLoginForm();
        loginPage.assertCurrent();
        String actionUrl1 = ActionURIUtils.getActionURIFromPageSource(driver.getPageSource());
        // Click "register" in tab2
        loginPage.clickRegister();
        registerPage.assertCurrent();
        // Simulate going back to tab1 and confirm login form. Page "showExpired" should be shown (NOTE: WebDriver does it with GET, when real browser would do it with POST. Improve test if needed...)
        driver.navigate().to(actionUrl1);
        loginExpiredPage.assertCurrent();
        // Click on continue and assert I am on "register" form
        loginExpiredPage.clickLoginContinueLink();
        registerPage.assertCurrent();
        // Finally click "Back to login" and authenticate
        registerPage.clickBackToLogin();
        loginPage.assertCurrent();
        // Login success now
        loginPage.login("login-test", "password");
        updatePasswordPage.changePassword("password", "password");
        updateProfilePage.update("John", "Doe3", "john@doe3.com");
        appPage.assertCurrent();
    }

    @Test
    public void expiredAuthenticationAction_expiredCodeCurrentExecution() {
        // Simulate to open login form in 2 tabs
        oauth.openLoginForm();
        loginPage.assertCurrent();
        String actionUrl1 = ActionURIUtils.getActionURIFromPageSource(driver.getPageSource());
        loginPage.login("invalid", "invalid");
        loginPage.assertCurrent();
        org.keycloak.testsuite.Assert.assertEquals("Invalid username or password.", loginPage.getError());
        // Simulate going back to tab1 and confirm login form. Login page with "action expired" message should be shown (NOTE: WebDriver does it with GET, when real browser would do it with POST. Improve test if needed...)
        driver.navigate().to(actionUrl1);
        loginPage.assertCurrent();
        org.keycloak.testsuite.Assert.assertEquals("Action expired. Please continue with login now.", loginPage.getError());
        // Login success now
        loginPage.login("login-test", "password");
        updatePasswordPage.changePassword("password", "password");
        updateProfilePage.update("John", "Doe3", "john@doe3.com");
        appPage.assertCurrent();
    }

    @Test
    public void expiredAuthenticationAction_expiredCodeExpiredExecution() {
        // Open tab1
        oauth.openLoginForm();
        loginPage.assertCurrent();
        String actionUrl1 = ActionURIUtils.getActionURIFromPageSource(driver.getPageSource());
        // Authenticate in tab2
        loginPage.login("login-test", "password");
        updatePasswordPage.assertCurrent();
        // Simulate going back to tab1 and confirm login form. Page "Page expired" should be shown (NOTE: WebDriver does it with GET, when real browser would do it with POST. Improve test if needed...)
        driver.navigate().to(actionUrl1);
        loginExpiredPage.assertCurrent();
        // Finish login
        loginExpiredPage.clickLoginContinueLink();
        updatePasswordPage.assertCurrent();
        updatePasswordPage.changePassword("password", "password");
        updateProfilePage.update("John", "Doe3", "john@doe3.com");
        appPage.assertCurrent();
    }

    @Test
    public void loginActionWithoutExecution() throws Exception {
        oauth.openLoginForm();
        // Manually remove execution from the URL and try to simulate the request just with "code" parameter
        String actionUrl = ActionURIUtils.getActionURIFromPageSource(driver.getPageSource());
        actionUrl = ActionURIUtils.removeQueryParamFromURI(actionUrl, EXECUTION);
        driver.navigate().to(actionUrl);
        loginExpiredPage.assertCurrent();
    }

    // Same like "loginActionWithoutExecution", but AuthenticationSession is in REQUIRED_ACTIONS action
    @Test
    public void loginActionWithoutExecutionInRequiredActions() throws Exception {
        oauth.openLoginForm();
        loginPage.assertCurrent();
        loginPage.login("login-test", "password");
        updatePasswordPage.assertCurrent();
        // Manually remove execution from the URL and try to simulate the request just with "code" parameter
        String actionUrl = ActionURIUtils.getActionURIFromPageSource(driver.getPageSource());
        actionUrl = ActionURIUtils.removeQueryParamFromURI(actionUrl, EXECUTION);
        driver.navigate().to(actionUrl);
        // Back on updatePasswordPage now
        updatePasswordPage.assertCurrent();
        updatePasswordPage.changePassword("password", "password");
        updateProfilePage.update("John", "Doe3", "john@doe3.com");
        appPage.assertCurrent();
    }

    // KEYCLOAK-5797
    @Test
    public void loginWithDifferentClients() throws Exception {
        String redirectUri = String.format("%s://localhost:%s/foo/bar/baz", AbstractKeycloakTest.AUTH_SERVER_SCHEME, AbstractKeycloakTest.AUTH_SERVER_PORT);
        // Open tab1 and start login here
        oauth.openLoginForm();
        loginPage.assertCurrent();
        loginPage.login("login-test", "bad-password");
        String tab1Url = driver.getCurrentUrl();
        // Go to tab2 and start login with different client "root-url-client"
        oauth.clientId("root-url-client");
        oauth.redirectUri(redirectUri);
        oauth.openLoginForm();
        loginPage.assertCurrent();
        String tab2Url = driver.getCurrentUrl();
        // Go back to tab1 and finish login here
        driver.navigate().to(tab1Url);
        loginPage.login("login-test", "password");
        updatePasswordPage.changePassword("password", "password");
        updateProfilePage.update("John", "Doe3", "john@doe3.com");
        // Assert I am redirected to the appPage in tab1
        appPage.assertCurrent();
        // Go back to tab2 and finish login here. Should be on the root-url-client page
        driver.navigate().to(tab2Url);
        String currentUrl = driver.getCurrentUrl();
        org.keycloak.testsuite.Assert.assertThat(currentUrl, Matchers.startsWith(redirectUri));
    }

    // KEYCLOAK-5938
    @Test
    public void loginWithSameClientDifferentStatesLoginInTab1() throws Exception {
        String redirectUri1 = String.format("%s://localhost:%s/auth/realms/master/app/auth/suffix1", AbstractKeycloakTest.AUTH_SERVER_SCHEME, AbstractKeycloakTest.AUTH_SERVER_PORT);
        String redirectUri2 = String.format("%s://localhost:%s/auth/realms/master/app/auth/suffix2", AbstractKeycloakTest.AUTH_SERVER_SCHEME, AbstractKeycloakTest.AUTH_SERVER_PORT);
        // Open tab1 and start login here
        oauth.stateParamHardcoded("state1");
        oauth.redirectUri(redirectUri1);
        oauth.openLoginForm();
        loginPage.assertCurrent();
        loginPage.login("login-test", "bad-password");
        String tab1Url = driver.getCurrentUrl();
        // Go to tab2 and start login with different client "root-url-client"
        oauth.stateParamHardcoded("state2");
        oauth.redirectUri(redirectUri2);
        oauth.openLoginForm();
        loginPage.assertCurrent();
        String tab2Url = driver.getCurrentUrl();
        // Go back to tab1 and finish login here
        driver.navigate().to(tab1Url);
        loginPage.login("login-test", "password");
        updatePasswordPage.changePassword("password", "password");
        updateProfilePage.update("John", "Doe3", "john@doe3.com");
        // Assert I am redirected to the appPage in tab1 and have state corresponding to tab1
        appPage.assertCurrent();
        String currentUrl = driver.getCurrentUrl();
        org.keycloak.testsuite.Assert.assertThat(currentUrl, Matchers.startsWith(redirectUri1));
        org.keycloak.testsuite.Assert.assertTrue(currentUrl.contains("state1"));
    }

    // KEYCLOAK-5938
    @Test
    public void loginWithSameClientDifferentStatesLoginInTab2() throws Exception {
        String redirectUri1 = String.format("%s://localhost:%s/auth/realms/master/app/auth/suffix1", AbstractKeycloakTest.AUTH_SERVER_SCHEME, AbstractKeycloakTest.AUTH_SERVER_PORT);
        String redirectUri2 = String.format("%s://localhost:%s/auth/realms/master/app/auth/suffix2", AbstractKeycloakTest.AUTH_SERVER_SCHEME, AbstractKeycloakTest.AUTH_SERVER_PORT);
        // Open tab1 and start login here
        oauth.stateParamHardcoded("state1");
        oauth.redirectUri(redirectUri1);
        oauth.openLoginForm();
        loginPage.assertCurrent();
        loginPage.login("login-test", "bad-password");
        String tab1Url = driver.getCurrentUrl();
        // Go to tab2 and start login with different client "root-url-client"
        oauth.stateParamHardcoded("state2");
        oauth.redirectUri(redirectUri2);
        oauth.openLoginForm();
        loginPage.assertCurrent();
        String tab2Url = driver.getCurrentUrl();
        // Continue in tab2 and finish login here
        loginPage.login("login-test", "password");
        updatePasswordPage.changePassword("password", "password");
        updateProfilePage.update("John", "Doe3", "john@doe3.com");
        // Assert I am redirected to the appPage in tab2 and have state corresponding to tab2
        appPage.assertCurrent();
        String currentUrl = driver.getCurrentUrl();
        org.keycloak.testsuite.Assert.assertThat(currentUrl, Matchers.startsWith(redirectUri2));
        org.keycloak.testsuite.Assert.assertTrue(currentUrl.contains("state2"));
    }
}

