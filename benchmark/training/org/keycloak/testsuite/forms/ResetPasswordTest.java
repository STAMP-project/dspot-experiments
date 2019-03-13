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


import Constants.ACCOUNT_MANAGEMENT_CLIENT_ID;
import Details.ACTION;
import Details.CODE_ID;
import Details.EMAIL;
import Details.REDIRECT_URI;
import Details.USERNAME;
import Errors.EMAIL_SEND_FAILED;
import Errors.PASSWORD_REJECTED;
import EventType.EXECUTE_ACTION_TOKEN_ERROR;
import EventType.RESET_PASSWORD;
import EventType.RESET_PASSWORD_ERROR;
import EventType.SEND_RESET_PASSWORD;
import EventType.SEND_RESET_PASSWORD_ERROR;
import EventType.UPDATE_PASSWORD;
import EventType.UPDATE_PASSWORD_ERROR;
import OAuthClient.AccessTokenResponse;
import RequestType.AUTH_RESPONSE;
import ResetCredentialsActionToken.TOKEN_TYPE;
import SystemClientUtil.SYSTEM_CLIENT_ID;
import java.io.Closeable;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.keycloak.representations.idm.EventRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.AbstractTestRealmKeycloakTest;
import org.keycloak.testsuite.AssertEvents;
import org.keycloak.testsuite.pages.AppPage;
import org.keycloak.testsuite.pages.ErrorPage;
import org.keycloak.testsuite.pages.InfoPage;
import org.keycloak.testsuite.pages.LoginPage;
import org.keycloak.testsuite.pages.LoginPasswordResetPage;
import org.keycloak.testsuite.pages.LoginPasswordUpdatePage;
import org.keycloak.testsuite.pages.VerifyEmailPage;
import org.keycloak.testsuite.updaters.ClientAttributeUpdater;
import org.keycloak.testsuite.util.GreenMailRule;
import org.keycloak.testsuite.util.MailUtils;
import org.keycloak.testsuite.util.OAuthClient;
import org.keycloak.testsuite.util.SecondBrowser;
import org.keycloak.testsuite.util.UserActionTokenBuilder;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;


/**
 *
 *
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 * @author Stan Silvert ssilvert@redhat.com (C) 2016 Red Hat Inc.
 */
public class ResetPasswordTest extends AbstractTestRealmKeycloakTest {
    private String userId;

    @Drone
    @SecondBrowser
    protected WebDriver driver2;

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

    @Rule
    public AssertEvents events = new AssertEvents(this);

    private int expectedMessagesCount;

    @Test
    public void resetPasswordLink() throws IOException, MessagingException {
        String username = "login-test";
        String resetUri = (oauth.AUTH_SERVER_ROOT) + "/realms/test/login-actions/reset-credentials";
        driver.navigate().to(resetUri);
        resetPasswordPage.assertCurrent();
        resetPasswordPage.changePassword(username);
        loginPage.assertCurrent();
        Assert.assertEquals("You should receive an email shortly with further instructions.", loginPage.getSuccessMessage());
        events.expectRequiredAction(SEND_RESET_PASSWORD).user(userId).detail(REDIRECT_URI, ((oauth.AUTH_SERVER_ROOT) + "/realms/test/account/")).client("account").detail(USERNAME, username).detail(EMAIL, "login@test.com").session(((String) (null))).assertEvent();
        Assert.assertEquals(1, greenMail.getReceivedMessages().length);
        MimeMessage message = greenMail.getReceivedMessages()[0];
        String changePasswordUrl = MailUtils.getPasswordResetEmailLink(message);
        driver.navigate().to(changePasswordUrl.trim());
        updatePasswordPage.assertCurrent();
        updatePasswordPage.changePassword("resetPassword", "resetPassword");
        events.expectRequiredAction(UPDATE_PASSWORD).detail(REDIRECT_URI, ((oauth.AUTH_SERVER_ROOT) + "/realms/test/account/")).client("account").user(userId).detail(USERNAME, username).assertEvent();
        String sessionId = events.expectLogin().user(userId).detail(USERNAME, username).detail(REDIRECT_URI, ((oauth.AUTH_SERVER_ROOT) + "/realms/test/account/")).client("account").assertEvent().getSessionId();
        oauth.openLogout();
        events.expectLogout(sessionId).user(userId).session(sessionId).assertEvent();
        loginPage.open();
        loginPage.login("login-test", "resetPassword");
        events.expectLogin().user(userId).detail(USERNAME, "login-test").assertEvent();
        Assert.assertEquals(AUTH_RESPONSE, appPage.getRequestType());
    }

    @Test
    public void resetPassword() throws IOException, MessagingException {
        resetPassword("login-test");
    }

    @Test
    public void resetPasswordTwice() throws IOException, MessagingException {
        String changePasswordUrl = resetPassword("login-test");
        events.clear();
        assertSecondPasswordResetFails(changePasswordUrl, oauth.getClientId());// KC_RESTART doesn't exists, it was deleted after first successful reset-password flow was finished

    }

    @Test
    public void resetPasswordTwiceInNewBrowser() throws IOException, MessagingException {
        String changePasswordUrl = resetPassword("login-test");
        events.clear();
        String resetUri = (oauth.AUTH_SERVER_ROOT) + "/realms/test/login-actions/reset-credentials";
        driver.navigate().to(resetUri);// This is necessary to delete KC_RESTART cookie that is restricted to /auth/realms/test path

        driver.manage().deleteAllCookies();
        assertSecondPasswordResetFails(changePasswordUrl, oauth.getClientId());
    }

    @Test
    public void resetPasswordWithSpacesInUsername() throws IOException, MessagingException {
        resetPassword(" login-test ");
    }

    @Test
    public void resetPasswordCancelChangeUser() throws IOException, MessagingException {
        initiateResetPasswordFromResetPasswordPage("test-user@localhost");
        events.expectRequiredAction(SEND_RESET_PASSWORD).detail(USERNAME, "test-user@localhost").session(((String) (null))).detail(EMAIL, "test-user@localhost").assertEvent();
        loginPage.login("login@test.com", "password");
        EventRepresentation loginEvent = events.expectLogin().user(userId).detail(USERNAME, "login@test.com").assertEvent();
        String code = oauth.getCurrentQuery().get("code");
        OAuthClient.AccessTokenResponse tokenResponse = oauth.doAccessTokenRequest(code, "password");
        Assert.assertEquals(200, tokenResponse.getStatusCode());
        Assert.assertEquals(userId, oauth.verifyToken(tokenResponse.getAccessToken()).getSubject());
        events.expectCodeToToken(loginEvent.getDetails().get(CODE_ID), loginEvent.getSessionId()).user(userId).assertEvent();
    }

    @Test
    public void resetPasswordByEmail() throws IOException, MessagingException {
        resetPassword("login@test.com");
    }

    @Test
    public void resetPasswordWrongEmail() throws IOException, InterruptedException, MessagingException {
        initiateResetPasswordFromResetPasswordPage("invalid");
        Assert.assertEquals(0, greenMail.getReceivedMessages().length);
        events.expectRequiredAction(RESET_PASSWORD).user(((String) (null))).session(((String) (null))).detail(USERNAME, "invalid").removeDetail(EMAIL).removeDetail(CODE_ID).error("user_not_found").assertEvent();
    }

    @Test
    public void resetPasswordMissingUsername() throws IOException, InterruptedException, MessagingException {
        loginPage.open();
        loginPage.resetPassword();
        resetPasswordPage.assertCurrent();
        resetPasswordPage.changePassword("");
        resetPasswordPage.assertCurrent();
        Assert.assertEquals("Please specify username.", resetPasswordPage.getErrorMessage());
        Assert.assertEquals(0, greenMail.getReceivedMessages().length);
        events.expectRequiredAction(RESET_PASSWORD).user(((String) (null))).session(((String) (null))).clearDetails().error("username_missing").assertEvent();
    }

    @Test
    public void resetPasswordExpiredCode() throws IOException, InterruptedException, MessagingException {
        initiateResetPasswordFromResetPasswordPage("login-test");
        events.expectRequiredAction(SEND_RESET_PASSWORD).session(((String) (null))).user(userId).detail(USERNAME, "login-test").detail(EMAIL, "login@test.com").assertEvent();
        Assert.assertEquals(1, greenMail.getReceivedMessages().length);
        MimeMessage message = greenMail.getReceivedMessages()[0];
        String changePasswordUrl = MailUtils.getPasswordResetEmailLink(message);
        try {
            setTimeOffset((1800 + 23));
            driver.navigate().to(changePasswordUrl.trim());
            loginPage.assertCurrent();
            Assert.assertEquals("Action expired. Please start again.", loginPage.getError());
            events.expectRequiredAction(EXECUTE_ACTION_TOKEN_ERROR).error("expired_code").client(((String) (null))).user(userId).session(((String) (null))).clearDetails().detail(ACTION, TOKEN_TYPE).assertEvent();
        } finally {
            setTimeOffset(0);
        }
    }

    @Test
    public void resetPasswordExpiredCodeShort() throws IOException, InterruptedException, MessagingException {
        final AtomicInteger originalValue = new AtomicInteger();
        RealmRepresentation realmRep = testRealm().toRepresentation();
        originalValue.set(realmRep.getActionTokenGeneratedByUserLifespan());
        realmRep.setActionTokenGeneratedByUserLifespan(60);
        testRealm().update(realmRep);
        try {
            initiateResetPasswordFromResetPasswordPage("login-test");
            events.expectRequiredAction(SEND_RESET_PASSWORD).session(((String) (null))).user(userId).detail(USERNAME, "login-test").detail(EMAIL, "login@test.com").assertEvent();
            Assert.assertEquals(1, greenMail.getReceivedMessages().length);
            MimeMessage message = greenMail.getReceivedMessages()[0];
            String changePasswordUrl = MailUtils.getPasswordResetEmailLink(message);
            setTimeOffset(70);
            driver.navigate().to(changePasswordUrl.trim());
            loginPage.assertCurrent();
            Assert.assertEquals("Action expired. Please start again.", loginPage.getError());
            events.expectRequiredAction(EXECUTE_ACTION_TOKEN_ERROR).error("expired_code").client(((String) (null))).user(userId).session(((String) (null))).clearDetails().detail(ACTION, TOKEN_TYPE).assertEvent();
        } finally {
            setTimeOffset(0);
            realmRep.setActionTokenGeneratedByUserLifespan(originalValue.get());
            testRealm().update(realmRep);
        }
    }

    @Test
    public void resetPasswordExpiredCodeShortPerActionLifespan() throws IOException, InterruptedException, MessagingException {
        RealmRepresentation realmRep = testRealm().toRepresentation();
        Map<String, String> originalAttributes = Collections.unmodifiableMap(new HashMap(realmRep.getAttributes()));
        realmRep.setAttributes(UserActionTokenBuilder.create().resetCredentialsLifespan(60).build());
        testRealm().update(realmRep);
        try {
            initiateResetPasswordFromResetPasswordPage("login-test");
            events.expectRequiredAction(SEND_RESET_PASSWORD).session(((String) (null))).user(userId).detail(USERNAME, "login-test").detail(EMAIL, "login@test.com").assertEvent();
            Assert.assertEquals(1, greenMail.getReceivedMessages().length);
            MimeMessage message = greenMail.getReceivedMessages()[0];
            String changePasswordUrl = MailUtils.getPasswordResetEmailLink(message);
            setTimeOffset(70);
            driver.navigate().to(changePasswordUrl.trim());
            loginPage.assertCurrent();
            Assert.assertEquals("Action expired. Please start again.", loginPage.getError());
            events.expectRequiredAction(EXECUTE_ACTION_TOKEN_ERROR).error("expired_code").client(((String) (null))).user(userId).session(((String) (null))).clearDetails().detail(ACTION, TOKEN_TYPE).assertEvent();
        } finally {
            setTimeOffset(0);
            realmRep.setAttributes(originalAttributes);
            testRealm().update(realmRep);
        }
    }

    @Test
    public void resetPasswordExpiredCodeShortPerActionMultipleTimeouts() throws IOException, InterruptedException, MessagingException {
        RealmRepresentation realmRep = testRealm().toRepresentation();
        Map<String, String> originalAttributes = Collections.unmodifiableMap(new HashMap(realmRep.getAttributes()));
        // Make sure that one attribute settings won't affect the other
        realmRep.setAttributes(UserActionTokenBuilder.create().resetCredentialsLifespan(60).verifyEmailLifespan(300).build());
        testRealm().update(realmRep);
        try {
            initiateResetPasswordFromResetPasswordPage("login-test");
            events.expectRequiredAction(SEND_RESET_PASSWORD).session(((String) (null))).user(userId).detail(USERNAME, "login-test").detail(EMAIL, "login@test.com").assertEvent();
            Assert.assertEquals(1, greenMail.getReceivedMessages().length);
            MimeMessage message = greenMail.getReceivedMessages()[0];
            String changePasswordUrl = MailUtils.getPasswordResetEmailLink(message);
            setTimeOffset(70);
            driver.navigate().to(changePasswordUrl.trim());
            loginPage.assertCurrent();
            Assert.assertEquals("Action expired. Please start again.", loginPage.getError());
            events.expectRequiredAction(EXECUTE_ACTION_TOKEN_ERROR).error("expired_code").client(((String) (null))).user(userId).session(((String) (null))).clearDetails().detail(ACTION, TOKEN_TYPE).assertEvent();
        } finally {
            setTimeOffset(0);
            realmRep.setAttributes(originalAttributes);
            testRealm().update(realmRep);
        }
    }

    // KEYCLOAK-4016
    @Test
    public void resetPasswordExpiredCodeAndAuthSession() throws IOException, InterruptedException, MessagingException {
        final AtomicInteger originalValue = new AtomicInteger();
        RealmRepresentation realmRep = testRealm().toRepresentation();
        originalValue.set(realmRep.getActionTokenGeneratedByUserLifespan());
        realmRep.setActionTokenGeneratedByUserLifespan(60);
        testRealm().update(realmRep);
        try {
            initiateResetPasswordFromResetPasswordPage("login-test");
            events.expectRequiredAction(SEND_RESET_PASSWORD).session(((String) (null))).user(userId).detail(USERNAME, "login-test").detail(EMAIL, "login@test.com").assertEvent();
            Assert.assertEquals(1, greenMail.getReceivedMessages().length);
            MimeMessage message = greenMail.getReceivedMessages()[0];
            String changePasswordUrl = MailUtils.getPasswordResetEmailLink(message).replace("&amp;", "&");
            setTimeOffset(70);
            log.debug("Going to reset password URI.");
            driver.navigate().to(((oauth.AUTH_SERVER_ROOT) + "/realms/test/login-actions/reset-credentials"));// This is necessary to delete KC_RESTART cookie that is restricted to /auth/realms/test path

            log.debug("Removing cookies.");
            driver.manage().deleteAllCookies();
            driver.navigate().to(changePasswordUrl.trim());
            errorPage.assertCurrent();
            Assert.assertEquals("Action expired.", errorPage.getError());
            String backToAppLink = errorPage.getBackToApplicationLink();
            Assert.assertTrue(backToAppLink.endsWith("/app/auth"));
            events.expectRequiredAction(EXECUTE_ACTION_TOKEN_ERROR).error("expired_code").client(((String) (null))).user(userId).session(((String) (null))).clearDetails().detail(ACTION, TOKEN_TYPE).assertEvent();
        } finally {
            setTimeOffset(0);
            realmRep.setActionTokenGeneratedByUserLifespan(originalValue.get());
            testRealm().update(realmRep);
        }
    }

    @Test
    public void resetPasswordExpiredCodeAndAuthSessionPerActionLifespan() throws IOException, InterruptedException, MessagingException {
        RealmRepresentation realmRep = testRealm().toRepresentation();
        Map<String, String> originalAttributes = Collections.unmodifiableMap(new HashMap(realmRep.getAttributes()));
        realmRep.setAttributes(UserActionTokenBuilder.create().resetCredentialsLifespan(60).build());
        testRealm().update(realmRep);
        try {
            initiateResetPasswordFromResetPasswordPage("login-test");
            events.expectRequiredAction(SEND_RESET_PASSWORD).session(((String) (null))).user(userId).detail(USERNAME, "login-test").detail(EMAIL, "login@test.com").assertEvent();
            Assert.assertEquals(1, greenMail.getReceivedMessages().length);
            MimeMessage message = greenMail.getReceivedMessages()[0];
            String changePasswordUrl = MailUtils.getPasswordResetEmailLink(message).replace("&amp;", "&");
            setTimeOffset(70);
            log.debug("Going to reset password URI.");
            driver.navigate().to(((oauth.AUTH_SERVER_ROOT) + "/realms/test/login-actions/reset-credentials"));// This is necessary to delete KC_RESTART cookie that is restricted to /auth/realms/test path

            log.debug("Removing cookies.");
            driver.manage().deleteAllCookies();
            driver.navigate().to(changePasswordUrl.trim());
            errorPage.assertCurrent();
            Assert.assertEquals("Action expired.", errorPage.getError());
            String backToAppLink = errorPage.getBackToApplicationLink();
            Assert.assertTrue(backToAppLink.endsWith("/app/auth"));
            events.expectRequiredAction(EXECUTE_ACTION_TOKEN_ERROR).error("expired_code").client(((String) (null))).user(userId).session(((String) (null))).clearDetails().detail(ACTION, TOKEN_TYPE).assertEvent();
        } finally {
            setTimeOffset(0);
            realmRep.setAttributes(originalAttributes);
            testRealm().update(realmRep);
        }
    }

    @Test
    public void resetPasswordExpiredCodeAndAuthSessionPerActionMultipleTimeouts() throws IOException, InterruptedException, MessagingException {
        RealmRepresentation realmRep = testRealm().toRepresentation();
        Map<String, String> originalAttributes = Collections.unmodifiableMap(new HashMap(realmRep.getAttributes()));
        // Make sure that one attribute settings won't affect the other
        realmRep.setAttributes(UserActionTokenBuilder.create().resetCredentialsLifespan(60).verifyEmailLifespan(300).build());
        testRealm().update(realmRep);
        try {
            initiateResetPasswordFromResetPasswordPage("login-test");
            events.expectRequiredAction(SEND_RESET_PASSWORD).session(((String) (null))).user(userId).detail(USERNAME, "login-test").detail(EMAIL, "login@test.com").assertEvent();
            Assert.assertEquals(1, greenMail.getReceivedMessages().length);
            MimeMessage message = greenMail.getReceivedMessages()[0];
            String changePasswordUrl = MailUtils.getPasswordResetEmailLink(message).replace("&amp;", "&");
            setTimeOffset(70);
            log.debug("Going to reset password URI.");
            driver.navigate().to(((oauth.AUTH_SERVER_ROOT) + "/realms/test/login-actions/reset-credentials"));// This is necessary to delete KC_RESTART cookie that is restricted to /auth/realms/test path

            log.debug("Removing cookies.");
            driver.manage().deleteAllCookies();
            driver.navigate().to(changePasswordUrl.trim());
            errorPage.assertCurrent();
            Assert.assertEquals("Action expired.", errorPage.getError());
            String backToAppLink = errorPage.getBackToApplicationLink();
            Assert.assertTrue(backToAppLink.endsWith("/app/auth"));
            events.expectRequiredAction(EXECUTE_ACTION_TOKEN_ERROR).error("expired_code").client(((String) (null))).user(userId).session(((String) (null))).clearDetails().detail(ACTION, TOKEN_TYPE).assertEvent();
        } finally {
            setTimeOffset(0);
            realmRep.setAttributes(originalAttributes);
            testRealm().update(realmRep);
        }
    }

    // KEYCLOAK-5061
    @Test
    public void resetPasswordExpiredCodeForgotPasswordFlow() throws IOException, InterruptedException, MessagingException {
        final AtomicInteger originalValue = new AtomicInteger();
        RealmRepresentation realmRep = testRealm().toRepresentation();
        originalValue.set(realmRep.getActionTokenGeneratedByUserLifespan());
        realmRep.setActionTokenGeneratedByUserLifespan(60);
        testRealm().update(realmRep);
        try {
            // Redirect directly to KC "forgot password" endpoint instead of "authenticate" endpoint
            String loginUrl = oauth.getLoginFormUrl();
            String forgotPasswordUrl = loginUrl.replace("/auth?", "/forgot-credentials?");// Workaround, but works

            driver.navigate().to(forgotPasswordUrl);
            resetPasswordPage.assertCurrent();
            resetPasswordPage.changePassword("login-test");
            loginPage.assertCurrent();
            Assert.assertEquals("You should receive an email shortly with further instructions.", loginPage.getSuccessMessage());
            (expectedMessagesCount)++;
            events.expectRequiredAction(SEND_RESET_PASSWORD).session(((String) (null))).user(userId).detail(USERNAME, "login-test").detail(EMAIL, "login@test.com").assertEvent();
            Assert.assertEquals(1, greenMail.getReceivedMessages().length);
            MimeMessage message = greenMail.getReceivedMessages()[0];
            String changePasswordUrl = MailUtils.getPasswordResetEmailLink(message);
            setTimeOffset(70);
            driver.navigate().to(changePasswordUrl.trim());
            resetPasswordPage.assertCurrent();
            Assert.assertEquals("Action expired. Please start again.", loginPage.getError());
            events.expectRequiredAction(EXECUTE_ACTION_TOKEN_ERROR).error("expired_code").client(((String) (null))).user(userId).session(((String) (null))).clearDetails().detail(ACTION, TOKEN_TYPE).assertEvent();
        } finally {
            setTimeOffset(0);
            realmRep.setActionTokenGeneratedByUserLifespan(originalValue.get());
            testRealm().update(realmRep);
        }
    }

    @Test
    public void resetPasswordExpiredCodeForgotPasswordFlowPerActionLifespan() throws IOException, InterruptedException, MessagingException {
        RealmRepresentation realmRep = testRealm().toRepresentation();
        Map<String, String> originalAttributes = Collections.unmodifiableMap(new HashMap(realmRep.getAttributes()));
        realmRep.setAttributes(UserActionTokenBuilder.create().resetCredentialsLifespan(60).build());
        testRealm().update(realmRep);
        try {
            // Redirect directly to KC "forgot password" endpoint instead of "authenticate" endpoint
            String loginUrl = oauth.getLoginFormUrl();
            String forgotPasswordUrl = loginUrl.replace("/auth?", "/forgot-credentials?");// Workaround, but works

            driver.navigate().to(forgotPasswordUrl);
            resetPasswordPage.assertCurrent();
            resetPasswordPage.changePassword("login-test");
            loginPage.assertCurrent();
            Assert.assertEquals("You should receive an email shortly with further instructions.", loginPage.getSuccessMessage());
            (expectedMessagesCount)++;
            events.expectRequiredAction(SEND_RESET_PASSWORD).session(((String) (null))).user(userId).detail(USERNAME, "login-test").detail(EMAIL, "login@test.com").assertEvent();
            Assert.assertEquals(1, greenMail.getReceivedMessages().length);
            MimeMessage message = greenMail.getReceivedMessages()[0];
            String changePasswordUrl = MailUtils.getPasswordResetEmailLink(message);
            setTimeOffset(70);
            driver.navigate().to(changePasswordUrl.trim());
            resetPasswordPage.assertCurrent();
            Assert.assertEquals("Action expired. Please start again.", loginPage.getError());
            events.expectRequiredAction(EXECUTE_ACTION_TOKEN_ERROR).error("expired_code").client(((String) (null))).user(userId).session(((String) (null))).clearDetails().detail(ACTION, TOKEN_TYPE).assertEvent();
        } finally {
            setTimeOffset(0);
            realmRep.setAttributes(originalAttributes);
            testRealm().update(realmRep);
        }
    }

    @Test
    public void resetPasswordExpiredCodeForgotPasswordFlowPerActionMultipleTimeouts() throws IOException, InterruptedException, MessagingException {
        RealmRepresentation realmRep = testRealm().toRepresentation();
        Map<String, String> originalAttributes = Collections.unmodifiableMap(new HashMap(realmRep.getAttributes()));
        // Make sure that one attribute settings won't affect the other
        realmRep.setAttributes(UserActionTokenBuilder.create().resetCredentialsLifespan(60).verifyEmailLifespan(300).build());
        testRealm().update(realmRep);
        try {
            // Redirect directly to KC "forgot password" endpoint instead of "authenticate" endpoint
            String loginUrl = oauth.getLoginFormUrl();
            String forgotPasswordUrl = loginUrl.replace("/auth?", "/forgot-credentials?");// Workaround, but works

            driver.navigate().to(forgotPasswordUrl);
            resetPasswordPage.assertCurrent();
            resetPasswordPage.changePassword("login-test");
            loginPage.assertCurrent();
            Assert.assertEquals("You should receive an email shortly with further instructions.", loginPage.getSuccessMessage());
            (expectedMessagesCount)++;
            events.expectRequiredAction(SEND_RESET_PASSWORD).session(((String) (null))).user(userId).detail(USERNAME, "login-test").detail(EMAIL, "login@test.com").assertEvent();
            Assert.assertEquals(1, greenMail.getReceivedMessages().length);
            MimeMessage message = greenMail.getReceivedMessages()[0];
            String changePasswordUrl = MailUtils.getPasswordResetEmailLink(message);
            setTimeOffset(70);
            driver.navigate().to(changePasswordUrl.trim());
            resetPasswordPage.assertCurrent();
            Assert.assertEquals("Action expired. Please start again.", loginPage.getError());
            events.expectRequiredAction(EXECUTE_ACTION_TOKEN_ERROR).error("expired_code").client(((String) (null))).user(userId).session(((String) (null))).clearDetails().detail(ACTION, TOKEN_TYPE).assertEvent();
        } finally {
            setTimeOffset(0);
            realmRep.setAttributes(originalAttributes);
            testRealm().update(realmRep);
        }
    }

    @Test
    public void resetPasswordDisabledUser() throws IOException, InterruptedException, MessagingException {
        UserRepresentation user = findUser("login-test");
        try {
            user.setEnabled(false);
            updateUser(user);
            initiateResetPasswordFromResetPasswordPage("login-test");
            Assert.assertEquals(0, greenMail.getReceivedMessages().length);
            events.expectRequiredAction(RESET_PASSWORD).session(((String) (null))).user(userId).detail(USERNAME, "login-test").removeDetail(CODE_ID).error("user_disabled").assertEvent();
        } finally {
            user.setEnabled(true);
            updateUser(user);
        }
    }

    @Test
    public void resetPasswordNoEmail() throws IOException, InterruptedException, MessagingException {
        final String email;
        UserRepresentation user = findUser("login-test");
        email = user.getEmail();
        try {
            user.setEmail("");
            updateUser(user);
            initiateResetPasswordFromResetPasswordPage("login-test");
            Assert.assertEquals(0, greenMail.getReceivedMessages().length);
            events.expectRequiredAction(RESET_PASSWORD_ERROR).session(((String) (null))).user(userId).detail(USERNAME, "login-test").removeDetail(CODE_ID).error("invalid_email").assertEvent();
        } finally {
            user.setEmail(email);
            updateUser(user);
        }
    }

    @Test
    public void resetPasswordWrongSmtp() throws IOException, InterruptedException, MessagingException {
        final String[] host = new String[1];
        Map<String, String> smtpConfig = new HashMap<>();
        smtpConfig.putAll(testRealm().toRepresentation().getSmtpServer());
        host[0] = smtpConfig.get("host");
        smtpConfig.put("host", "invalid_host");
        RealmRepresentation realmRep = testRealm().toRepresentation();
        Map<String, String> oldSmtp = realmRep.getSmtpServer();
        try {
            realmRep.setSmtpServer(smtpConfig);
            testRealm().update(realmRep);
            loginPage.open();
            loginPage.resetPassword();
            resetPasswordPage.assertCurrent();
            resetPasswordPage.changePassword("login-test");
            errorPage.assertCurrent();
            Assert.assertEquals("Failed to send email, please try again later.", errorPage.getError());
            Assert.assertEquals(0, greenMail.getReceivedMessages().length);
            events.expectRequiredAction(SEND_RESET_PASSWORD_ERROR).user(userId).session(((String) (null))).detail(USERNAME, "login-test").removeDetail(CODE_ID).error(EMAIL_SEND_FAILED).assertEvent();
        } finally {
            // Revert SMTP back
            realmRep.setSmtpServer(oldSmtp);
            testRealm().update(realmRep);
        }
    }

    @Test
    public void resetPasswordWithLengthPasswordPolicy() throws IOException, MessagingException {
        setPasswordPolicy("length");
        initiateResetPasswordFromResetPasswordPage("login-test");
        Assert.assertEquals(1, greenMail.getReceivedMessages().length);
        MimeMessage message = greenMail.getReceivedMessages()[0];
        String changePasswordUrl = MailUtils.getPasswordResetEmailLink(message);
        events.expectRequiredAction(SEND_RESET_PASSWORD).session(((String) (null))).user(userId).detail(USERNAME, "login-test").detail(EMAIL, "login@test.com").assertEvent();
        driver.navigate().to(changePasswordUrl.trim());
        updatePasswordPage.assertCurrent();
        updatePasswordPage.changePassword("invalid", "invalid");
        Assert.assertEquals("Invalid password: minimum length 8.", resetPasswordPage.getErrorMessage());
        events.expectRequiredAction(UPDATE_PASSWORD_ERROR).error(PASSWORD_REJECTED).user(userId).detail(USERNAME, "login-test").assertEvent().getSessionId();
        updatePasswordPage.changePassword("resetPasswordWithPasswordPolicy", "resetPasswordWithPasswordPolicy");
        events.expectRequiredAction(UPDATE_PASSWORD).user(userId).detail(USERNAME, "login-test").assertEvent().getSessionId();
        Assert.assertEquals(AUTH_RESPONSE, appPage.getRequestType());
        String sessionId = events.expectLogin().user(userId).detail(USERNAME, "login-test").assertEvent().getSessionId();
        oauth.openLogout();
        events.expectLogout(sessionId).user(userId).session(sessionId).assertEvent();
        loginPage.open();
        loginPage.login("login-test", "resetPasswordWithPasswordPolicy");
        Assert.assertEquals(AUTH_RESPONSE, appPage.getRequestType());
        events.expectLogin().user(userId).detail(USERNAME, "login-test").assertEvent();
    }

    @Test
    public void resetPasswordWithPasswordHistoryPolicy() throws IOException, MessagingException {
        // Block passwords that are equal to previous passwords. Default value is 3.
        setPasswordPolicy("passwordHistory");
        try {
            setTimeOffset(2000000);
            resetPassword("login-test", "password1");
            resetPasswordInvalidPassword("login-test", "password1", "Invalid password: must not be equal to any of last 3 passwords.");
            setTimeOffset(4000000);
            resetPassword("login-test", "password2");
            resetPasswordInvalidPassword("login-test", "password1", "Invalid password: must not be equal to any of last 3 passwords.");
            resetPasswordInvalidPassword("login-test", "password2", "Invalid password: must not be equal to any of last 3 passwords.");
            setTimeOffset(6000000);
            resetPassword("login-test", "password3");
            resetPasswordInvalidPassword("login-test", "password1", "Invalid password: must not be equal to any of last 3 passwords.");
            resetPasswordInvalidPassword("login-test", "password2", "Invalid password: must not be equal to any of last 3 passwords.");
            resetPasswordInvalidPassword("login-test", "password3", "Invalid password: must not be equal to any of last 3 passwords.");
            setTimeOffset(8000000);
            resetPassword("login-test", "password");
        } finally {
            setTimeOffset(0);
        }
    }

    @Test
    public void resetPasswordLinkOpenedInNewBrowser() throws IOException, MessagingException {
        resetPasswordLinkOpenedInNewBrowser(ACCOUNT_MANAGEMENT_CLIENT_ID);
    }

    // KEYCLOAK-5982
    @Test
    public void resetPasswordLinkOpenedInNewBrowserAndAccountClientRenamed() throws IOException, MessagingException {
        // Temporarily rename client "account" . Revert it back after the test
        try (Closeable accountClientUpdater = ClientAttributeUpdater.forClient(adminClient, "test", ACCOUNT_MANAGEMENT_CLIENT_ID).setClientId("account-changed").update()) {
            // Assert resetPassword link opened in new browser works even if client "account" not available
            resetPasswordLinkOpenedInNewBrowser(SYSTEM_CLIENT_ID);
        }
    }

    @Test
    public void resetPasswordLinkNewBrowserSessionPreserveClient() throws IOException, MessagingException {
        loginPage.open();
        loginPage.resetPassword();
        resetPasswordPage.assertCurrent();
        resetPasswordPage.changePassword("login-test");
        loginPage.assertCurrent();
        Assert.assertEquals("You should receive an email shortly with further instructions.", loginPage.getSuccessMessage());
        Assert.assertEquals(1, greenMail.getReceivedMessages().length);
        MimeMessage message = greenMail.getReceivedMessages()[0];
        String changePasswordUrl = MailUtils.getPasswordResetEmailLink(message);
        driver2.navigate().to(changePasswordUrl.trim());
        final WebElement newPassword = driver2.findElement(By.id("password-new"));
        newPassword.sendKeys("resetPassword");
        final WebElement confirmPassword = driver2.findElement(By.id("password-confirm"));
        confirmPassword.sendKeys("resetPassword");
        final WebElement submit = driver2.findElement(By.cssSelector("input[type=\"submit\"]"));
        submit.click();
        MatcherAssert.assertThat(driver2.getCurrentUrl(), Matchers.containsString("client_id=test-app"));
        MatcherAssert.assertThat(driver2.getPageSource(), Matchers.containsString("Your account has been updated."));
    }
}

