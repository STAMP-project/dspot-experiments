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


import java.io.IOException;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.AbstractTestRealmKeycloakTest;
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
import org.keycloak.testsuite.util.MailUtils;


/**
 * Test for browser back/forward/refresh buttons
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class BrowserButtonsTest extends AbstractTestRealmKeycloakTest {
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

    private int expectedMessagesCount;

    // KEYCLOAK-4670 - Flow 1
    @Test
    public void invalidLoginAndBackButton() throws IOException, MessagingException {
        loginPage.open();
        loginPage.login("login-test2", "invalid");
        loginPage.assertCurrent();
        loginPage.login("login-test3", "invalid");
        loginPage.assertCurrent();
        // Click browser back. Should be still on login page (TODO: Retest with real browsers like FF or Chrome. Maybe they need some additional actions to confirm re-sending POST request )
        driver.navigate().back();
        loginPage.assertCurrent();
        // Click browser refresh. Should be still on login page
        driver.navigate().refresh();
        loginPage.assertCurrent();
    }

    // KEYCLOAK-4670 - Flow 2
    @Test
    public void requiredActionsBackForwardTest() throws IOException, MessagingException {
        loginPage.open();
        // Login and assert on "updatePassword" page
        loginPage.login("login-test", "password");
        updatePasswordPage.assertCurrent();
        // Update password and assert on "updateProfile" page
        updatePasswordPage.changePassword("password", "password");
        updateProfilePage.assertCurrent();
        // Click browser back. Assert on "Page expired" page
        driver.navigate().back();
        loginExpiredPage.assertCurrent();
        // Click browser forward. Assert on "updateProfile" page again
        driver.navigate().forward();
        updateProfilePage.assertCurrent();
        // Successfully update profile and assert user logged
        updateProfilePage.update("John", "Doe3", "john@doe3.com");
        appPage.assertCurrent();
    }

    // KEYCLOAK-4670 - Flow 3 extended
    @Test
    public void requiredActionsBackAndRefreshTest() throws IOException, MessagingException {
        loginPage.open();
        // Login and assert on "updatePassword" page
        loginPage.login("login-test", "password");
        updatePasswordPage.assertCurrent();
        // Click browser refresh. Assert still on updatePassword page
        driver.navigate().refresh();
        updatePasswordPage.assertCurrent();
        // Update password and assert on "updateProfile" page
        updatePasswordPage.changePassword("password", "password");
        updateProfilePage.assertCurrent();
        // Click browser back. Assert on "Page expired" page
        driver.navigate().back();
        loginExpiredPage.assertCurrent();
        // Click browser refresh. Assert still on "Page expired" page
        driver.navigate().refresh();
        loginExpiredPage.assertCurrent();
        // Click "login restart" and assert on loginPage
        loginExpiredPage.clickLoginRestartLink();
        loginPage.assertCurrent();
        // Login again and assert on "updateProfile" page
        loginPage.login("login-test", "password");
        updateProfilePage.assertCurrent();
        // Click browser back. Assert on "Page expired" page
        driver.navigate().back();
        loginExpiredPage.assertCurrent();
        // Click "login continue" and assert on updateProfile page
        loginExpiredPage.clickLoginContinueLink();
        updateProfilePage.assertCurrent();
        // Successfully update profile and assert user logged
        updateProfilePage.update("John", "Doe3", "john@doe3.com");
        appPage.assertCurrent();
    }

    // KEYCLOAK-4670 - Flow 4
    @Test
    public void consentRefresh() {
        oauth.clientId("third-party");
        // Login and go through required actions
        loginPage.open();
        loginPage.login("login-test", "password");
        updatePasswordPage.changePassword("password", "password");
        updateProfilePage.update("John", "Doe3", "john@doe3.com");
        // Assert on consent screen
        grantPage.assertCurrent();
        // Click browser back. Assert on "page expired"
        driver.navigate().back();
        loginExpiredPage.assertCurrent();
        // Click continue login. Assert on consent screen again
        loginExpiredPage.clickLoginContinueLink();
        grantPage.assertCurrent();
        // Click refresh. Assert still on consent screen
        driver.navigate().refresh();
        grantPage.assertCurrent();
        // Confirm consent. Assert authenticated
        grantPage.accept();
        appPage.assertCurrent();
    }

    // KEYCLOAK-4670 - Flow 5
    @Test
    public void clickBackButtonAfterReturnFromRegister() throws Exception {
        loginPage.open();
        loginPage.clickRegister();
        registerPage.assertCurrent();
        // Click "Back to login" link on registerPage
        registerPage.clickBackToLogin();
        loginPage.assertCurrent();
        // Click browser "back" button.
        driver.navigate().back();
        registerPage.assertCurrent();
    }

    @Test
    public void clickBackButtonFromRegisterPage() {
        loginPage.open();
        loginPage.clickRegister();
        registerPage.assertCurrent();
        // Click browser "back" button. Should be back on login page
        driver.navigate().back();
        loginPage.assertCurrent();
    }

    // KEYCLOAK-5136
    @Test
    public void clickRefreshButtonOnRegisterPage() {
        loginPage.open();
        loginPage.clickRegister();
        registerPage.assertCurrent();
        // Click browser "refresh" button. Should be still on register page
        driver.navigate().refresh();
        registerPage.assertCurrent();
        // Click 'back to login'. Should be on login page
        registerPage.clickBackToLogin();
        loginPage.assertCurrent();
        // Click browser 'refresh'. Should be still on login page
        driver.navigate().refresh();
        loginPage.assertCurrent();
    }

    @Test
    public void backButtonToAuthorizationEndpoint() {
        loginPage.open();
        // Login and assert on "updatePassword" page
        loginPage.login("login-test", "password");
        updatePasswordPage.assertCurrent();
        // Click browser back. I should be on login page . URL corresponds to OIDC AuthorizationEndpoint
        driver.navigate().back();
        loginPage.assertCurrent();
    }

    @Test
    public void backButtonInResetPasswordFlow() throws Exception {
        // Click on "forgot password" and type username
        loginPage.open();
        loginPage.login("login-test", "bad-username");
        loginPage.resetPassword();
        resetPasswordPage.assertCurrent();
        resetPasswordPage.changePassword("login-test");
        loginPage.assertCurrent();
        Assert.assertEquals("You should receive an email shortly with further instructions.", loginPage.getSuccessMessage());
        // Receive email
        MimeMessage message = greenMail.getReceivedMessages()[((greenMail.getReceivedMessages().length) - 1)];
        String changePasswordUrl = MailUtils.getPasswordResetEmailLink(message);
        driver.navigate().to(changePasswordUrl.trim());
        updatePasswordPage.assertCurrent();
        // Click browser back. Should be on loginPage for "forked flow"
        driver.navigate().back();
        loginPage.assertCurrent();
        // When clicking browser forward, back on updatePasswordPage
        driver.navigate().forward();
        updatePasswordPage.assertCurrent();
        // Click browser back. And continue login. Should be on updatePasswordPage
        driver.navigate().back();
        loginPage.assertCurrent();
        loginPage.login("login-test", "password");
        updatePasswordPage.assertCurrent();
    }

    @Test
    public void appInitiatedRegistrationWithBackButton() throws Exception {
        // Send request from the application directly to 'registrations'
        String appInitiatedRegisterUrl = oauth.getLoginFormUrl();
        appInitiatedRegisterUrl = appInitiatedRegisterUrl.replace("openid-connect/auth", "openid-connect/registrations");// Should be done better way...

        driver.navigate().to(appInitiatedRegisterUrl);
        registerPage.assertCurrent();
        // Click 'back to login'
        registerPage.clickBackToLogin();
        loginPage.assertCurrent();
        // Login
        loginPage.login("login-test", "password");
        updatePasswordPage.assertCurrent();
        // Click browser back. Should be on 'page expired'
        driver.navigate().back();
        loginExpiredPage.assertCurrent();
        // Click 'continue' should be on updatePasswordPage
        loginExpiredPage.clickLoginContinueLink();
        updatePasswordPage.assertCurrent();
        // Click browser back. Should be on 'page expired'
        driver.navigate().back();
        loginExpiredPage.assertCurrent();
        // Click 'restart' . Check that I was put to the registration page as flow was initiated as registration flow
        loginExpiredPage.clickLoginRestartLink();
        registerPage.assertCurrent();
    }
}

