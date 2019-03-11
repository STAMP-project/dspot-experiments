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


import EventType.RESET_PASSWORD_ERROR;
import OAuthClient.AccessTokenResponse;
import java.util.Collections;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.keycloak.models.utils.TimeBasedOTP;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.AbstractTestRealmKeycloakTest;
import org.keycloak.testsuite.AssertEvents;
import org.keycloak.testsuite.pages.AppPage;
import org.keycloak.testsuite.pages.LoginPage;
import org.keycloak.testsuite.pages.LoginPasswordResetPage;
import org.keycloak.testsuite.pages.LoginTotpPage;
import org.keycloak.testsuite.pages.RegisterPage;
import org.keycloak.testsuite.util.GreenMailRule;
import org.keycloak.testsuite.util.OAuthClient;


/**
 *
 *
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 * @author Stan Silvert ssilvert@redhat.com (C) 2016 Red Hat Inc.
 */
public class BruteForceTest extends AbstractTestRealmKeycloakTest {
    private static String userId;

    @Rule
    public AssertEvents events = new AssertEvents(this);

    @Rule
    public GreenMailRule greenMail = new GreenMailRule();

    @Page
    protected AppPage appPage;

    @Page
    protected LoginPage loginPage;

    @Page
    protected LoginPasswordResetPage resetPasswordPage;

    @Page
    private RegisterPage registerPage;

    @Page
    protected LoginTotpPage loginTotpPage;

    private TimeBasedOTP totp = new TimeBasedOTP();

    private int lifespan;

    @Test
    public void testGrantInvalidPassword() throws Exception {
        {
            String totpSecret = totp.generateTOTP("totpSecret");
            OAuthClient.AccessTokenResponse response = getTestToken("password", totpSecret);
            Assert.assertNotNull(response.getAccessToken());
            Assert.assertNull(response.getError());
            events.clear();
        }
        {
            String totpSecret = totp.generateTOTP("totpSecret");
            OAuthClient.AccessTokenResponse response = getTestToken("invalid", totpSecret);
            Assert.assertNull(response.getAccessToken());
            Assert.assertEquals(response.getError(), "invalid_grant");
            Assert.assertEquals(response.getErrorDescription(), "Invalid user credentials");
            events.clear();
        }
        {
            String totpSecret = totp.generateTOTP("totpSecret");
            OAuthClient.AccessTokenResponse response = getTestToken("invalid", totpSecret);
            Assert.assertNull(response.getAccessToken());
            Assert.assertEquals(response.getError(), "invalid_grant");
            Assert.assertEquals(response.getErrorDescription(), "Invalid user credentials");
            events.clear();
        }
        {
            String totpSecret = totp.generateTOTP("totpSecret");
            OAuthClient.AccessTokenResponse response = getTestToken("password", totpSecret);
            Assert.assertNull(response.getAccessToken());
            Assert.assertNotNull(response.getError());
            Assert.assertEquals("invalid_grant", response.getError());
            Assert.assertEquals("Invalid user credentials", response.getErrorDescription());
            assertUserDisabledEvent();
            events.clear();
        }
        clearUserFailures();
        {
            String totpSecret = totp.generateTOTP("totpSecret");
            OAuthClient.AccessTokenResponse response = getTestToken("password", totpSecret);
            Assert.assertNotNull(response.getAccessToken());
            Assert.assertNull(response.getError());
            events.clear();
        }
    }

    @Test
    public void testGrantInvalidOtp() throws Exception {
        {
            String totpSecret = totp.generateTOTP("totpSecret");
            OAuthClient.AccessTokenResponse response = getTestToken("password", totpSecret);
            Assert.assertNotNull(response.getAccessToken());
            Assert.assertNull(response.getError());
            events.clear();
        }
        {
            OAuthClient.AccessTokenResponse response = getTestToken("password", "shite");
            Assert.assertNull(response.getAccessToken());
            Assert.assertEquals(response.getError(), "invalid_grant");
            Assert.assertEquals(response.getErrorDescription(), "Invalid user credentials");
            events.clear();
        }
        {
            OAuthClient.AccessTokenResponse response = getTestToken("password", "shite");
            Assert.assertNull(response.getAccessToken());
            Assert.assertEquals(response.getError(), "invalid_grant");
            Assert.assertEquals(response.getErrorDescription(), "Invalid user credentials");
            events.clear();
        }
        {
            String totpSecret = totp.generateTOTP("totpSecret");
            OAuthClient.AccessTokenResponse response = getTestToken("password", totpSecret);
            assertTokenNull(response);
            Assert.assertNotNull(response.getError());
            Assert.assertEquals(response.getError(), "invalid_grant");
            Assert.assertEquals("Invalid user credentials", response.getErrorDescription());
            assertUserDisabledEvent();
            events.clear();
        }
        clearUserFailures();
        {
            String totpSecret = totp.generateTOTP("totpSecret");
            OAuthClient.AccessTokenResponse response = getTestToken("password", totpSecret);
            Assert.assertNotNull(response.getAccessToken());
            Assert.assertNull(response.getError());
            events.clear();
        }
    }

    @Test
    public void testGrantMissingOtp() throws Exception {
        {
            String totpSecret = totp.generateTOTP("totpSecret");
            OAuthClient.AccessTokenResponse response = getTestToken("password", totpSecret);
            Assert.assertNotNull(response.getAccessToken());
            Assert.assertNull(response.getError());
            events.clear();
        }
        {
            OAuthClient.AccessTokenResponse response = getTestToken("password", null);
            Assert.assertNull(response.getAccessToken());
            Assert.assertEquals(response.getError(), "invalid_grant");
            Assert.assertEquals(response.getErrorDescription(), "Invalid user credentials");
            events.clear();
        }
        {
            OAuthClient.AccessTokenResponse response = getTestToken("password", null);
            Assert.assertNull(response.getAccessToken());
            Assert.assertEquals(response.getError(), "invalid_grant");
            Assert.assertEquals(response.getErrorDescription(), "Invalid user credentials");
            events.clear();
        }
        {
            String totpSecret = totp.generateTOTP("totpSecret");
            OAuthClient.AccessTokenResponse response = getTestToken("password", totpSecret);
            assertTokenNull(response);
            Assert.assertNotNull(response.getError());
            Assert.assertEquals(response.getError(), "invalid_grant");
            Assert.assertEquals("Invalid user credentials", response.getErrorDescription());
            assertUserDisabledEvent();
            events.clear();
        }
        clearUserFailures();
        {
            String totpSecret = totp.generateTOTP("totpSecret");
            OAuthClient.AccessTokenResponse response = getTestToken("password", totpSecret);
            Assert.assertNotNull(response.getAccessToken());
            Assert.assertNull(response.getError());
            events.clear();
        }
    }

    @Test
    public void testBrowserInvalidPassword() throws Exception {
        loginSuccess();
        loginInvalidPassword();
        loginInvalidPassword();
        expectTemporarilyDisabled();
        expectTemporarilyDisabled("test-user@localhost", null, "invalid");
        clearUserFailures();
        loginSuccess();
        loginInvalidPassword();
        loginInvalidPassword();
        expectTemporarilyDisabled();
        expectTemporarilyDisabled("test-user@localhost", null, "invalid");
        clearAllUserFailures();
        loginSuccess();
    }

    @Test
    public void testWait() throws Exception {
        loginSuccess();
        loginInvalidPassword();
        loginInvalidPassword();
        expectTemporarilyDisabled();
        expectTemporarilyDisabled("test-user@localhost", null, "invalid");
        // KEYCLOAK-5420
        // Test to make sure that temporarily disabled doesn't increment failure count
        testingClient.testing().setTimeOffset(Collections.singletonMap("offset", String.valueOf(6)));
        // should be unlocked now
        loginSuccess();
        clearUserFailures();
        clearAllUserFailures();
        loginSuccess();
        testingClient.testing().setTimeOffset(Collections.singletonMap("offset", String.valueOf(0)));
    }

    @Test
    public void testBrowserInvalidPasswordDifferentCase() throws Exception {
        loginSuccess("test-user@localhost");
        loginInvalidPassword("test-User@localhost");
        loginInvalidPassword("Test-user@localhost");
        expectTemporarilyDisabled();
        expectTemporarilyDisabled("test-user@localhost", null, "invalid");
        clearAllUserFailures();
    }

    @Test
    public void testEmail() throws Exception {
        String userId = adminClient.realm("test").users().search("user2", null, null, null, 0, 1).get(0).getId();
        loginSuccess("user2@localhost");
        loginInvalidPassword("user2@localhost");
        loginInvalidPassword("user2@localhost");
        expectTemporarilyDisabled("user2@localhost", userId);
        clearAllUserFailures();
    }

    @Test
    public void testBrowserMissingPassword() throws Exception {
        loginSuccess();
        loginMissingPassword();
        loginMissingPassword();
        expectTemporarilyDisabled();
        expectTemporarilyDisabled("test-user@localhost", null, "invalid");
        clearUserFailures();
        loginSuccess();
    }

    @Test
    public void testBrowserInvalidTotp() throws Exception {
        loginSuccess();
        loginInvalidPassword();
        loginWithTotpFailure();
        continueLoginWithCorrectTotpExpectFailure();
        continueLoginWithInvalidTotp();
        clearUserFailures();
        continueLoginWithTotp();
    }

    @Test
    public void testTotpGoingBack() throws Exception {
        loginPage.open();
        loginPage.login("test-user@localhost", "password");
        continueLoginWithInvalidTotp();
        loginTotpPage.cancel();
        loginPage.assertCurrent();
        loginPage.login("test-user@localhost", "password");
        continueLoginWithInvalidTotp();
        continueLoginWithCorrectTotpExpectFailure();
        clearUserFailures();
        continueLoginWithTotp();
    }

    @Test
    public void testBrowserMissingTotp() throws Exception {
        loginSuccess();
        loginWithMissingTotp();
        loginWithMissingTotp();
        continueLoginWithMissingTotp();
        continueLoginWithCorrectTotpExpectFailure();
        // wait to unlock
        testingClient.testing().setTimeOffset(Collections.singletonMap("offset", String.valueOf(6)));
        continueLoginWithTotp();
        testingClient.testing().setTimeOffset(Collections.singletonMap("offset", String.valueOf(0)));
    }

    @Test
    public void testPermanentLockout() throws Exception {
        RealmRepresentation realm = testRealm().toRepresentation();
        try {
            // arrange
            realm.setPermanentLockout(true);
            testRealm().update(realm);
            // act
            loginInvalidPassword();
            loginInvalidPassword();
            // assert
            expectPermanentlyDisabled();
            Assert.assertFalse(adminClient.realm("test").users().search("test-user@localhost", 0, 1).get(0).isEnabled());
        } finally {
            realm.setPermanentLockout(false);
            testRealm().update(realm);
            UserRepresentation user = adminClient.realm("test").users().search("test-user@localhost", 0, 1).get(0);
            user.setEnabled(true);
            updateUser(user);
        }
    }

    @Test
    public void testResetLoginFailureCount() throws Exception {
        RealmRepresentation realm = testRealm().toRepresentation();
        try {
            // arrange
            realm.setPermanentLockout(true);
            testRealm().update(realm);
            // act
            loginInvalidPassword();
            loginSuccess();
            loginInvalidPassword();
            loginSuccess();
            // assert
            Assert.assertTrue(adminClient.realm("test").users().search("test-user@localhost", 0, 1).get(0).isEnabled());
        } finally {
            realm.setPermanentLockout(false);
            testRealm().update(realm);
        }
    }

    @Test
    public void testNonExistingAccounts() throws Exception {
        loginInvalidPassword("non-existent-user");
        loginInvalidPassword("non-existent-user");
        loginInvalidPassword("non-existent-user");
        registerUser("non-existent-user");
    }

    @Test
    public void testResetPassword() throws Exception {
        String userId = adminClient.realm("test").users().search("test-user@localhost", null, null, null, 0, 1).get(0).getId();
        loginSuccess();
        loginInvalidPassword();
        loginInvalidPassword();
        expectTemporarilyDisabled();
        expectTemporarilyDisabled("test-user@localhost", null, "invalid");
        loginPage.resetPassword();
        resetPasswordPage.assertCurrent();
        resetPasswordPage.changePassword("test-user@localhost");
        loginPage.assertCurrent();
        Assert.assertEquals("You should receive an email shortly with further instructions.", loginPage.getSuccessMessage());
        events.expectRequiredAction(RESET_PASSWORD_ERROR).user(userId);
        events.clear();
        clearUserFailures();
    }
}

