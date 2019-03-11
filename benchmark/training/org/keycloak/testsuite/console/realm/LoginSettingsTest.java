/**
 * JBoss, Home of Professional Open Source
 *
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.keycloak.testsuite.console.realm;


import RequireSSLOption.all;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.testsuite.auth.page.account.Account;
import org.keycloak.testsuite.auth.page.login.Registration;
import org.keycloak.testsuite.auth.page.login.ResetCredentials;
import org.keycloak.testsuite.auth.page.login.VerifyEmail;
import org.keycloak.testsuite.console.AbstractConsoleTest;
import org.keycloak.testsuite.console.page.realm.LoginSettings;
import org.keycloak.testsuite.util.MailServer;
import org.keycloak.testsuite.util.URLUtils;


/**
 *
 *
 * @author tkyjovsk
 */
public class LoginSettingsTest extends AbstractRealmTest {
    private static final String NEW_USERNAME = "newUsername";

    @Page
    private LoginSettings loginSettingsPage;

    @Page
    private Registration testRealmRegistrationPage;

    @Page
    private ResetCredentials testRealmForgottenPasswordPage;

    @Page
    private VerifyEmail testRealmVerifyEmailPage;

    @Page
    private Account testAccountPage;

    @Test
    public void userRegistration() {
        log.info("enabling registration");
        loginSettingsPage.form().setRegistrationAllowed(true);
        Assert.assertTrue(loginSettingsPage.form().isRegistrationAllowed());
        loginSettingsPage.form().save();
        assertAlertSuccess();
        log.debug("enabled");
        testRealmAdminConsolePage.navigateTo();
        testRealmLoginPage.form().register();
        assertCurrentUrlStartsWith(testRealmRegistrationPage);
        Assert.assertTrue(testRealmRegistrationPage.isConfirmPasswordPresent());
        Assert.assertTrue(testRealmRegistrationPage.isUsernamePresent());
        log.info("verified registration is enabled");
        // test email as username
        log.info("enabling email as username");
        loginSettingsPage.navigateTo();
        loginSettingsPage.form().setEmailAsUsername(true);
        Assert.assertTrue(loginSettingsPage.form().isEmailAsUsername());
        loginSettingsPage.form().save();
        assertAlertSuccess();
        log.debug("enabled");
        testRealmAdminConsolePage.navigateTo();
        testRealmLoginPage.form().register();
        assertCurrentUrlStartsWith(testRealmRegistrationPage);
        Assert.assertTrue(testRealmRegistrationPage.isConfirmPasswordPresent());
        Assert.assertFalse(testRealmRegistrationPage.isUsernamePresent());
        log.info("verified email as username");
        // test user reg. disabled
        log.info("disabling registration");
        loginSettingsPage.navigateTo();
        loginSettingsPage.form().setRegistrationAllowed(false);
        Assert.assertFalse(loginSettingsPage.form().isRegistrationAllowed());
        loginSettingsPage.form().save();
        assertAlertSuccess();
        log.debug("disabled");
        testRealmAdminConsolePage.navigateTo();
        Assert.assertFalse(testRealmLoginPage.form().isRegisterLinkPresent());
        log.info("verified regisration is disabled");
    }

    @Test
    public void editUsername() {
        log.info("enabling edit username");
        loginSettingsPage.form().setEditUsernameAllowed(true);
        Assert.assertTrue(loginSettingsPage.form().isEditUsernameAllowed());
        loginSettingsPage.form().save();
        assertAlertSuccess();
        log.debug("enabled");
        log.info("edit username");
        testAccountPage.navigateTo();
        testRealmLoginPage.form().login(testUser);
        assertCurrentUrlStartsWith(testAccountPage);
        testAccountPage.setUsername(LoginSettingsTest.NEW_USERNAME);
        testAccountPage.save();
        testAccountPage.signOut();
        log.debug("edited");
        log.info("log in with edited username");
        assertCurrentUrlStartsWithLoginUrlOf(testAccountPage);
        testRealmLoginPage.form().login(LoginSettingsTest.NEW_USERNAME, PASSWORD);
        assertCurrentUrlStartsWith(testAccountPage);
        log.debug("user is logged in with edited username");
        log.info("disabling edit username");
        loginSettingsPage.navigateTo();
        loginSettingsPage.form().setEditUsernameAllowed(false);
        Assert.assertFalse(loginSettingsPage.form().isEditUsernameAllowed());
        loginSettingsPage.form().save();
        assertAlertSuccess();
        log.debug("disabled");
    }

    @Test
    public void resetPassword() {
        log.info("enabling reset password");
        loginSettingsPage.form().setResetPasswordAllowed(true);
        Assert.assertTrue(loginSettingsPage.form().isResetPasswordAllowed());
        loginSettingsPage.form().save();
        assertAlertSuccess();
        log.debug("enabled");
        testRealmAdminConsolePage.navigateTo();
        testRealmLoginPage.form().forgotPassword();
        Assert.assertEquals("Enter your username or email address and we will send you instructions on how to create a new password.", testRealmForgottenPasswordPage.getInfoMessage());
        log.info("verified reset password is enabled");
        log.info("disabling reset password");
        loginSettingsPage.navigateTo();
        loginSettingsPage.form().setResetPasswordAllowed(false);
        Assert.assertFalse(loginSettingsPage.form().isResetPasswordAllowed());
        loginSettingsPage.form().save();
        assertAlertSuccess();
        log.debug("disabled");
        testRealmAdminConsolePage.navigateTo();
        Assert.assertFalse(testRealmLoginPage.form().isForgotPasswordLinkPresent());
        log.info("verified reset password is disabled");
    }

    @Test
    public void rememberMe() {
        log.info("enabling remember me");
        loginSettingsPage.form().setRememberMeAllowed(true);
        Assert.assertTrue(loginSettingsPage.form().isRememberMeAllowed());
        loginSettingsPage.form().save();
        assertAlertSuccess();
        log.debug("enabled");
        log.info("login with remember me checked");
        testAccountPage.navigateTo();
        testRealmLoginPage.form().rememberMe(true);
        testRealmLoginPage.form().login(testUser);
        assertCurrentUrlStartsWith(testAccountPage);
        Assert.assertTrue("Cookie KEYCLOAK_REMEMBER_ME should be present.", getCookieNames().contains("KEYCLOAK_REMEMBER_ME"));
        log.info("verified remember me is enabled");
        log.info("disabling remember me");
        loginSettingsPage.navigateTo();
        loginSettingsPage.form().setRememberMeAllowed(false);
        Assert.assertFalse(loginSettingsPage.form().isRememberMeAllowed());
        loginSettingsPage.form().save();
        assertAlertSuccess();
        log.debug("disabled");
        testAccountPage.navigateTo();
        testAccountPage.signOut();
        Assert.assertTrue(testRealmLoginPage.form().isLoginButtonPresent());
        Assert.assertFalse(testRealmLoginPage.form().isRememberMePresent());
        log.info("verified remember me is disabled");
    }

    @Test
    public void verifyEmail() {
        MailServer.start();
        MailServer.createEmailAccount(testUser.getEmail(), "password");
        log.info("enabling verify email in login settings");
        loginSettingsPage.form().setVerifyEmailAllowed(true);
        Assert.assertTrue(loginSettingsPage.form().isVerifyEmailAllowed());
        loginSettingsPage.form().save();
        assertAlertSuccess();
        log.debug("enabled");
        log.info("configure smtp server in test realm");
        RealmRepresentation testRealmRep = testRealmResource().toRepresentation();
        testRealmRep.setSmtpServer(suiteContext.getSmtpServer());
        testRealmResource().update(testRealmRep);
        testAccountPage.navigateTo();
        testRealmLoginPage.form().login(testUser);
        Assert.assertEquals("An email with instructions to verify your email address has been sent to you.", testRealmVerifyEmailPage.getInstructionMessage());
        log.info("verified verify email is enabled");
        log.info("disabling verify email");
        loginSettingsPage.navigateTo();
        loginSettingsPage.form().setVerifyEmailAllowed(false);
        Assert.assertFalse(loginSettingsPage.form().isVerifyEmailAllowed());
        loginSettingsPage.form().save();
        assertAlertSuccess();
        log.debug("disabled");
        log.debug("create new test user");
        UserRepresentation newUser = createUserRepresentation("new_user", "new_user@email.test", "new", "user", true);
        setPasswordFor(newUser, PASSWORD);
        String id = createUserAndResetPasswordWithAdminClient(testRealmResource(), newUser, PASSWORD);
        newUser.setId(id);
        log.info("log in as new user");
        testAccountPage.navigateTo();
        testRealmLoginPage.form().login(newUser);
        assertCurrentUrlStartsWith(testAccountPage);
        log.info("verified verify email is disabled");
        MailServer.stop();
    }

    @Test
    public void requireSSLAllRequests() throws InterruptedException {
        log.info("set require ssl for all requests");
        loginSettingsPage.form().selectRequireSSL(all);
        loginSettingsPage.form().save();
        assertAlertSuccess();
        log.debug("set");
        log.info("check HTTPS required");
        String accountPageUri = testAccountPage.toString();
        if (AUTH_SERVER_SSL_REQUIRED) {
            // quick and dirty (and hopefully provisional) workaround to force HTTP
            accountPageUri = accountPageUri.replace("https", "http").replace(AUTH_SERVER_PORT, System.getProperty("auth.server.http.port"));
        }
        URLUtils.navigateToUri(accountPageUri);
        Assert.assertEquals("HTTPS required", testAccountPage.getErrorMessage());
    }
}

