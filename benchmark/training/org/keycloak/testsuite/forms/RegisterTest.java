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


import Details.EMAIL;
import Details.USERNAME;
import EventType.SEND_VERIFY_EMAIL;
import EventType.VERIFY_EMAIL;
import RequestType.AUTH_RESPONSE;
import javax.mail.internet.MimeMessage;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.AbstractTestRealmKeycloakTest;
import org.keycloak.testsuite.AssertEvents;
import org.keycloak.testsuite.util.RealmBuilder;
import org.keycloak.testsuite.util.UserBuilder;


/**
 *
 *
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 * @author Stan Silvert ssilvert@redhat.com (C) 2016 Red Hat Inc.
 */
public class RegisterTest extends AbstractTestRealmKeycloakTest {
    @Rule
    public AssertEvents events = new AssertEvents(this);

    @Page
    protected AppPage appPage;

    @Page
    protected LoginPage loginPage;

    @Page
    protected RegisterPage registerPage;

    @Page
    protected VerifyEmailPage verifyEmailPage;

    @Page
    protected AccountUpdateProfilePage accountPage;

    @Rule
    public GreenMailRule greenMail = new GreenMailRule();

    @Test
    public void registerExistingUsernameForbidden() {
        loginPage.open();
        loginPage.clickRegister();
        registerPage.assertCurrent();
        registerPage.register("firstName", "lastName", "registerExistingUser@email", "roleRichUser", "password", "password");
        registerPage.assertCurrent();
        Assert.assertEquals("Username already exists.", registerPage.getError());
        // assert form keeps form fields on error
        Assert.assertEquals("firstName", registerPage.getFirstName());
        Assert.assertEquals("lastName", registerPage.getLastName());
        Assert.assertEquals("registerExistingUser@email", registerPage.getEmail());
        Assert.assertEquals("", registerPage.getUsername());
        Assert.assertEquals("", registerPage.getPassword());
        Assert.assertEquals("", registerPage.getPasswordConfirm());
        events.expectRegister("roleRichUser", "registerExistingUser@email").removeDetail(EMAIL).user(((String) (null))).error("username_in_use").assertEvent();
    }

    @Test
    public void registerExistingEmailForbidden() {
        loginPage.open();
        loginPage.clickRegister();
        registerPage.assertCurrent();
        registerPage.register("firstName", "lastName", "test-user@localhost", "registerExistingUser", "password", "password");
        registerPage.assertCurrent();
        Assert.assertEquals("Email already exists.", registerPage.getError());
        // assert form keeps form fields on error
        Assert.assertEquals("firstName", registerPage.getFirstName());
        Assert.assertEquals("lastName", registerPage.getLastName());
        Assert.assertEquals("", registerPage.getEmail());
        Assert.assertEquals("registerExistingUser", registerPage.getUsername());
        Assert.assertEquals("", registerPage.getPassword());
        Assert.assertEquals("", registerPage.getPasswordConfirm());
        events.expectRegister("registerExistingUser", "registerExistingUser@email").removeDetail(EMAIL).user(((String) (null))).error("email_in_use").assertEvent();
    }

    @Test
    public void registerExistingEmailAllowed() {
        setDuplicateEmailsAllowed(true);
        loginPage.open();
        loginPage.clickRegister();
        registerPage.assertCurrent();
        registerPage.register("firstName", "lastName", "test-user@localhost", "registerExistingEmailUser", "password", "password");
        Assert.assertEquals(AUTH_RESPONSE, appPage.getRequestType());
        String userId = events.expectRegister("registerExistingEmailUser", "test-user@localhost").assertEvent().getUserId();
        events.expectLogin().detail("username", "registerexistingemailuser").user(userId).assertEvent();
        UserRepresentation user = getUser(userId);
        Assert.assertNotNull(user);
        Assert.assertEquals("registerexistingemailuser", user.getUsername());
        Assert.assertEquals("test-user@localhost", user.getEmail());
        Assert.assertEquals("firstName", user.getFirstName());
        Assert.assertEquals("lastName", user.getLastName());
        testRealm().users().get(userId).remove();
        setDuplicateEmailsAllowed(false);
    }

    @Test
    public void registerUserInvalidPasswordConfirm() {
        loginPage.open();
        loginPage.clickRegister();
        registerPage.assertCurrent();
        registerPage.register("firstName", "lastName", "registerUserInvalidPasswordConfirm@email", "registerUserInvalidPasswordConfirm", "password", "invalid");
        registerPage.assertCurrent();
        Assert.assertEquals("Password confirmation doesn't match.", registerPage.getError());
        // assert form keeps form fields on error
        Assert.assertEquals("firstName", registerPage.getFirstName());
        Assert.assertEquals("lastName", registerPage.getLastName());
        Assert.assertEquals("registerUserInvalidPasswordConfirm@email", registerPage.getEmail());
        Assert.assertEquals("registerUserInvalidPasswordConfirm", registerPage.getUsername());
        Assert.assertEquals("", registerPage.getPassword());
        Assert.assertEquals("", registerPage.getPasswordConfirm());
        events.expectRegister("registerUserInvalidPasswordConfirm", "registerUserInvalidPasswordConfirm@email").removeDetail(USERNAME).removeDetail(EMAIL).user(((String) (null))).error("invalid_registration").assertEvent();
    }

    @Test
    public void registerUserMissingPassword() {
        loginPage.open();
        loginPage.clickRegister();
        registerPage.assertCurrent();
        registerPage.register("firstName", "lastName", "registerUserMissingPassword@email", "registerUserMissingPassword", null, null);
        registerPage.assertCurrent();
        Assert.assertEquals("Please specify password.", registerPage.getError());
        events.expectRegister("registerUserMissingPassword", "registerUserMissingPassword@email").removeDetail(USERNAME).removeDetail(EMAIL).user(((String) (null))).error("invalid_registration").assertEvent();
    }

    @Test
    public void registerPasswordPolicy() {
        /* keycloakRule.configure(new KeycloakRule.KeycloakSetup() {
        @Override
        public void config(RealmManager manager, RealmModel adminstrationRealm, RealmModel appRealm) {
        appRealm.setPasswordPolicy(new PasswordPolicy("length"));
        }
        });
         */
        RealmRepresentation realm = testRealm().toRepresentation();
        realm.setPasswordPolicy("length");
        testRealm().update(realm);
        try {
            loginPage.open();
            loginPage.clickRegister();
            registerPage.assertCurrent();
            registerPage.register("firstName", "lastName", "registerPasswordPolicy@email", "registerPasswordPolicy", "pass", "pass");
            registerPage.assertCurrent();
            Assert.assertEquals("Invalid password: minimum length 8.", registerPage.getError());
            events.expectRegister("registerPasswordPolicy", "registerPasswordPolicy@email").removeDetail(USERNAME).removeDetail(EMAIL).user(((String) (null))).error("invalid_registration").assertEvent();
            registerPage.register("firstName", "lastName", "registerPasswordPolicy@email", "registerPasswordPolicy", "password", "password");
            Assert.assertEquals(AUTH_RESPONSE, appPage.getRequestType());
            String userId = events.expectRegister("registerPasswordPolicy", "registerPasswordPolicy@email").assertEvent().getUserId();
            events.expectLogin().user(userId).detail(USERNAME, "registerpasswordpolicy").assertEvent();
        } finally {
            /* keycloakRule.configure(new KeycloakRule.KeycloakSetup() {
            @Override
            public void config(RealmManager manager, RealmModel adminstrationRealm, RealmModel appRealm) {
            appRealm.setPasswordPolicy(new PasswordPolicy(null));
            }
            });
             */
        }
    }

    @Test
    public void registerUserMissingUsername() {
        loginPage.open();
        loginPage.clickRegister();
        registerPage.assertCurrent();
        registerPage.register("firstName", "lastName", "registerUserMissingUsername@email", null, "password", "password");
        registerPage.assertCurrent();
        Assert.assertEquals("Please specify username.", registerPage.getError());
        events.expectRegister(null, "registerUserMissingUsername@email").removeDetail(USERNAME).removeDetail(EMAIL).error("invalid_registration").assertEvent();
    }

    @Test
    public void registerUserManyErrors() {
        loginPage.open();
        loginPage.clickRegister();
        registerPage.assertCurrent();
        registerPage.register(null, null, null, null, null, null);
        registerPage.assertCurrent();
        Assert.assertEquals(("Please specify username.\n" + ((("Please specify first name.\n" + "Please specify last name.\n") + "Please specify email.\n") + "Please specify password.")), registerPage.getError());
        events.expectRegister(null, "registerUserMissingUsername@email").removeDetail(USERNAME).removeDetail(EMAIL).error("invalid_registration").assertEvent();
    }

    @Test
    public void registerUserMissingEmail() {
        loginPage.open();
        loginPage.clickRegister();
        registerPage.assertCurrent();
        registerPage.register("firstName", "lastName", null, "registerUserMissingEmail", "password", "password");
        registerPage.assertCurrent();
        Assert.assertEquals("Please specify email.", registerPage.getError());
        events.expectRegister("registerUserMissingEmail", null).removeDetail("email").error("invalid_registration").assertEvent();
    }

    @Test
    public void registerUserInvalidEmail() {
        loginPage.open();
        loginPage.clickRegister();
        registerPage.assertCurrent();
        registerPage.register("firstName", "lastName", "registerUserInvalidEmailemail", "registerUserInvalidEmail", "password", "password");
        registerPage.assertCurrent();
        Assert.assertEquals("registerUserInvalidEmailemail", registerPage.getEmail());
        Assert.assertEquals("Invalid email address.", registerPage.getError());
        events.expectRegister("registerUserInvalidEmail", "registerUserInvalidEmailemail").error("invalid_registration").assertEvent();
    }

    @Test
    public void registerUserSuccess() {
        loginPage.open();
        loginPage.clickRegister();
        registerPage.assertCurrent();
        registerPage.register("firstName", "lastName", "registerUserSuccess@email", "registerUserSuccess", "password", "password");
        appPage.assertCurrent();
        Assert.assertEquals(AUTH_RESPONSE, appPage.getRequestType());
        String userId = events.expectRegister("registerUserSuccess", "registerUserSuccess@email").assertEvent().getUserId();
        assertUserRegistered(userId, "registerusersuccess", "registerusersuccess@email");
    }

    @Test
    public void registerUserSuccessWithEmailVerification() throws Exception {
        RealmRepresentation realm = testRealm().toRepresentation();
        boolean origVerifyEmail = realm.isVerifyEmail();
        try {
            realm.setVerifyEmail(true);
            testRealm().update(realm);
            loginPage.open();
            loginPage.clickRegister();
            registerPage.assertCurrent();
            registerPage.register("firstName", "lastName", "registerUserSuccessWithEmailVerification@email", "registerUserSuccessWithEmailVerification", "password", "password");
            verifyEmailPage.assertCurrent();
            String userId = events.expectRegister("registerUserSuccessWithEmailVerification", "registerUserSuccessWithEmailVerification@email").assertEvent().getUserId();
            {
                assertTrue("Expecting verify email", greenMail.waitForIncomingEmail(1000, 1));
                events.expect(SEND_VERIFY_EMAIL).detail(EMAIL, "registerUserSuccessWithEmailVerification@email".toLowerCase()).user(userId).assertEvent();
                MimeMessage message = greenMail.getLastReceivedMessage();
                String link = MailUtils.getPasswordResetEmailLink(message);
                driver.navigate().to(link);
            }
            events.expectRequiredAction(VERIFY_EMAIL).detail(EMAIL, "registerUserSuccessWithEmailVerification@email".toLowerCase()).user(userId).assertEvent();
            assertUserRegistered(userId, "registerUserSuccessWithEmailVerification", "registerUserSuccessWithEmailVerification@email");
            appPage.assertCurrent();
            Assert.assertEquals(AUTH_RESPONSE, appPage.getRequestType());
            // test that timestamp is current with 10s tollerance
            // test user info is set from form
        } finally {
            realm.setVerifyEmail(origVerifyEmail);
            testRealm().update(realm);
        }
    }

    @Test
    public void registerUserSuccessWithEmailVerificationWithResend() throws Exception {
        RealmRepresentation realm = testRealm().toRepresentation();
        boolean origVerifyEmail = realm.isVerifyEmail();
        try {
            realm.setVerifyEmail(true);
            testRealm().update(realm);
            loginPage.open();
            loginPage.clickRegister();
            registerPage.assertCurrent();
            registerPage.register("firstName", "lastName", "registerUserSuccessWithEmailVerificationWithResend@email", "registerUserSuccessWithEmailVerificationWithResend", "password", "password");
            verifyEmailPage.assertCurrent();
            String userId = events.expectRegister("registerUserSuccessWithEmailVerificationWithResend", "registerUserSuccessWithEmailVerificationWithResend@email").assertEvent().getUserId();
            {
                assertTrue("Expecting verify email", greenMail.waitForIncomingEmail(1000, 1));
                events.expect(SEND_VERIFY_EMAIL).detail(EMAIL, "registerUserSuccessWithEmailVerificationWithResend@email".toLowerCase()).user(userId).assertEvent();
                verifyEmailPage.clickResendEmail();
                verifyEmailPage.assertCurrent();
                assertTrue("Expecting second verify email", greenMail.waitForIncomingEmail(1000, 1));
                events.expect(SEND_VERIFY_EMAIL).detail(EMAIL, "registerUserSuccessWithEmailVerificationWithResend@email".toLowerCase()).user(userId).assertEvent();
                MimeMessage message = greenMail.getLastReceivedMessage();
                String link = MailUtils.getPasswordResetEmailLink(message);
                driver.navigate().to(link);
            }
            events.expectRequiredAction(VERIFY_EMAIL).detail(EMAIL, "registerUserSuccessWithEmailVerificationWithResend@email".toLowerCase()).user(userId).assertEvent();
            assertUserRegistered(userId, "registerUserSuccessWithEmailVerificationWithResend", "registerUserSuccessWithEmailVerificationWithResend@email");
            appPage.assertCurrent();
            Assert.assertEquals(AUTH_RESPONSE, appPage.getRequestType());
            // test that timestamp is current with 10s tollerance
            // test user info is set from form
        } finally {
            realm.setVerifyEmail(origVerifyEmail);
            testRealm().update(realm);
        }
    }

    @Test
    public void registerUserUmlats() {
        loginPage.open();
        assertTrue(loginPage.isCurrent());
        loginPage.clickRegister();
        registerPage.assertCurrent();
        registerPage.register("??????", "???", "registeruserumlats@email", "registeruserumlats", "password", "password");
        String userId = events.expectRegister("registeruserumlats", "registeruserumlats@email").assertEvent().getUserId();
        events.expectLogin().detail("username", "registeruserumlats").user(userId).assertEvent();
        accountPage.open();
        assertTrue(accountPage.isCurrent());
        UserRepresentation user = getUser(userId);
        Assert.assertNotNull(user);
        Assert.assertEquals("??????", user.getFirstName());
        Assert.assertEquals("???", user.getLastName());
        Assert.assertEquals("??????", accountPage.getFirstName());
        Assert.assertEquals("???", accountPage.getLastName());
    }

    // KEYCLOAK-3266
    @Test
    public void registerUserNotUsernamePasswordPolicy() {
        adminClient.realm("test").update(RealmBuilder.create().passwordPolicy("notUsername").build());
        loginPage.open();
        assertTrue(loginPage.isCurrent());
        loginPage.clickRegister();
        registerPage.assertCurrent();
        registerPage.register("firstName", "lastName", "registerUserNotUsername@email", "registerUserNotUsername", "registerUserNotUsername", "registerUserNotUsername");
        assertTrue(registerPage.isCurrent());
        Assert.assertEquals("Invalid password: must not be equal to the username.", registerPage.getError());
        adminClient.realm("test").users().create(UserBuilder.create().username("registerUserNotUsername").build());
        registerPage.register("firstName", "lastName", "registerUserNotUsername@email", "registerUserNotUsername", "registerUserNotUsername", "registerUserNotUsername");
        assertTrue(registerPage.isCurrent());
        Assert.assertEquals("Username already exists.", registerPage.getError());
        registerPage.register("firstName", "lastName", "registerUserNotUsername@email", null, "password", "password");
        assertTrue(registerPage.isCurrent());
        Assert.assertEquals("Please specify username.", registerPage.getError());
    }

    @Test
    public void registerExistingUser_emailAsUsername() {
        configureRelamRegistrationEmailAsUsername(true);
        try {
            loginPage.open();
            loginPage.clickRegister();
            registerPage.assertCurrent();
            registerPage.registerWithEmailAsUsername("firstName", "lastName", "test-user@localhost", "password", "password");
            registerPage.assertCurrent();
            Assert.assertEquals("Email already exists.", registerPage.getError());
            events.expectRegister("test-user@localhost", "test-user@localhost").user(((String) (null))).error("email_in_use").assertEvent();
        } finally {
            configureRelamRegistrationEmailAsUsername(false);
        }
    }

    @Test
    public void registerUserMissingOrInvalidEmail_emailAsUsername() {
        configureRelamRegistrationEmailAsUsername(true);
        try {
            loginPage.open();
            loginPage.clickRegister();
            registerPage.assertCurrent();
            registerPage.registerWithEmailAsUsername("firstName", "lastName", null, "password", "password");
            registerPage.assertCurrent();
            Assert.assertEquals("Please specify email.", registerPage.getError());
            events.expectRegister(null, null).removeDetail("username").removeDetail("email").error("invalid_registration").assertEvent();
            registerPage.registerWithEmailAsUsername("firstName", "lastName", "registerUserInvalidEmailemail", "password", "password");
            registerPage.assertCurrent();
            Assert.assertEquals("Invalid email address.", registerPage.getError());
            events.expectRegister("registerUserInvalidEmailemail", "registerUserInvalidEmailemail").error("invalid_registration").assertEvent();
        } finally {
            configureRelamRegistrationEmailAsUsername(false);
        }
    }

    @Test
    public void registerUserSuccess_emailAsUsername() {
        configureRelamRegistrationEmailAsUsername(true);
        try {
            loginPage.open();
            loginPage.clickRegister();
            registerPage.assertCurrent();
            registerPage.registerWithEmailAsUsername("firstName", "lastName", "registerUserSuccessE@email", "password", "password");
            Assert.assertEquals(AUTH_RESPONSE, appPage.getRequestType());
            String userId = events.expectRegister("registerUserSuccessE@email", "registerUserSuccessE@email").assertEvent().getUserId();
            events.expectLogin().detail("username", "registerusersuccesse@email").user(userId).assertEvent();
            UserRepresentation user = getUser(userId);
            Assert.assertNotNull(user);
            Assert.assertNotNull(user.getCreatedTimestamp());
            // test that timestamp is current with 10s tollerance
            Assert.assertTrue((((System.currentTimeMillis()) - (user.getCreatedTimestamp())) < 10000));
        } finally {
            configureRelamRegistrationEmailAsUsername(false);
        }
    }
}

