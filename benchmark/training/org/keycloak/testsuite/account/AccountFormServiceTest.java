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
package org.keycloak.testsuite.account;


import AccountApplicationsPage.AppEntry;
import Details.CONSENT;
import Details.PREVIOUS_EMAIL;
import Details.REDIRECT_URI;
import Details.UPDATED_EMAIL;
import Details.USERNAME;
import Errors.INVALID_USER_CREDENTIALS;
import Errors.PASSWORD_CONFIRM_ERROR;
import Errors.PASSWORD_MISSING;
import Errors.PASSWORD_REJECTED;
import EventType.REMOVE_TOTP;
import EventType.UPDATE_EMAIL;
import EventType.UPDATE_PASSWORD;
import EventType.UPDATE_PASSWORD_ERROR;
import EventType.UPDATE_PROFILE;
import EventType.UPDATE_TOTP;
import RequestType.AUTH_RESPONSE;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.hamcrest.Matchers;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.models.PasswordPolicy;
import org.keycloak.models.utils.TimeBasedOTP;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.EventRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.AbstractTestRealmKeycloakTest;
import org.keycloak.testsuite.AssertEvents;
import org.keycloak.testsuite.drone.Different;
import org.keycloak.testsuite.pages.AccountApplicationsPage;
import org.keycloak.testsuite.pages.AccountFederatedIdentityPage;
import org.keycloak.testsuite.pages.AccountLogPage;
import org.keycloak.testsuite.pages.AccountPasswordPage;
import org.keycloak.testsuite.pages.AccountSessionsPage;
import org.keycloak.testsuite.pages.AccountTotpPage;
import org.keycloak.testsuite.pages.AccountUpdateProfilePage;
import org.keycloak.testsuite.pages.AppPage;
import org.keycloak.testsuite.pages.ErrorPage;
import org.keycloak.testsuite.pages.LoginPage;
import org.keycloak.testsuite.pages.RegisterPage;
import org.keycloak.testsuite.updaters.ClientAttributeUpdater;
import org.keycloak.testsuite.updaters.RoleScopeUpdater;
import org.keycloak.testsuite.util.OAuthClient;
import org.keycloak.testsuite.util.UIUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;


/**
 *
 *
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 * @author Stan Silvert ssilvert@redhat.com (C) 2016 Red Hat Inc.
 */
public class AccountFormServiceTest extends AbstractTestRealmKeycloakTest {
    public static final String ROOT_URL_CLIENT = "root-url-client";

    public static final String REALM_NAME = "test";

    // Create second session
    @Drone
    @Different
    WebDriver driver2;

    @Rule
    public AssertEvents events = new AssertEvents(this);

    @Page
    protected AppPage appPage;

    @Page
    protected LoginPage loginPage;

    @Page
    protected RegisterPage registerPage;

    @Page
    protected AccountPasswordPage changePasswordPage;

    @Page
    protected AccountUpdateProfilePage profilePage;

    @Page
    protected AccountTotpPage totpPage;

    @Page
    protected AccountLogPage logPage;

    @Page
    protected AccountSessionsPage sessionsPage;

    @Page
    protected AccountApplicationsPage applicationsPage;

    @Page
    protected AccountFederatedIdentityPage federatedIdentityPage;

    @Page
    protected ErrorPage errorPage;

    private TimeBasedOTP totp = new TimeBasedOTP();

    private String userId;

    @Test
    public void returnToAppFromQueryParam() {
        driver.navigate().to(((profilePage.getPath()) + "?referrer=test-app"));
        loginPage.login("test-user@localhost", "password");
        Assert.assertTrue(profilePage.isCurrent());
        profilePage.backToApplication();
        Assert.assertTrue(appPage.isCurrent());
        driver.navigate().to(String.format("%s?referrer=test-app&referrer_uri=%s://localhost:%s/auth/realms/master/app/auth?test", profilePage.getPath(), AbstractKeycloakTest.AUTH_SERVER_SCHEME, AbstractKeycloakTest.AUTH_SERVER_PORT));
        Assert.assertTrue(profilePage.isCurrent());
        profilePage.backToApplication();
        Assert.assertTrue(appPage.isCurrent());
        Assert.assertEquals(((oauth.APP_AUTH_ROOT) + "?test"), driver.getCurrentUrl());
        driver.navigate().to(((profilePage.getPath()) + "?referrer=test-app"));
        Assert.assertTrue(profilePage.isCurrent());
        driver.findElement(By.linkText("Authenticator")).click();
        Assert.assertTrue(totpPage.isCurrent());
        driver.findElement(By.linkText("Account")).click();
        Assert.assertTrue(profilePage.isCurrent());
        profilePage.backToApplication();
        Assert.assertTrue(appPage.isCurrent());
        events.clear();
    }

    @Test
    public void referrerEscaped() {
        profilePage.open();
        loginPage.login("test-user@localhost", "password");
        driver.navigate().to(((profilePage.getPath()) + "?referrer=test-app&referrer_uri=http%3A%2F%2Flocalhost%3A8180%2Fauth%2Frealms%2Fmaster%2Fapp%2Fauth%2Ftest%2Ffkrenu%3Fq%3D%2522%253E%253Cscript%253Ealert%25281%2529%253C%252fscript%253E"));
        profilePage.assertCurrent();
        Assert.assertFalse(driver.getPageSource().contains("<script>alert"));
    }

    @Test
    public void changePassword() {
        changePasswordPage.open();
        loginPage.login("test-user@localhost", "password");
        EventRepresentation event = events.expectLogin().client("account").detail(REDIRECT_URI, ((getAccountRedirectUrl()) + "?path=password")).assertEvent();
        String sessionId = event.getSessionId();
        String userId = event.getUserId();
        changePasswordPage.changePassword("", "new-password", "new-password");
        Assert.assertEquals("Please specify password.", profilePage.getError());
        events.expectAccount(UPDATE_PASSWORD_ERROR).error(PASSWORD_MISSING).assertEvent();
        changePasswordPage.changePassword("password", "new-password", "new-password2");
        Assert.assertEquals("Password confirmation doesn't match.", profilePage.getError());
        events.expectAccount(UPDATE_PASSWORD_ERROR).error(PASSWORD_CONFIRM_ERROR).assertEvent();
        changePasswordPage.changePassword("password", "new-password", "new-password");
        Assert.assertEquals("Your password has been updated.", profilePage.getSuccess());
        events.expectAccount(UPDATE_PASSWORD).assertEvent();
        changePasswordPage.logout();
        events.expectLogout(sessionId).detail(REDIRECT_URI, changePasswordPage.getPath()).assertEvent();
        loginPage.open();
        loginPage.login("test-user@localhost", "password");
        Assert.assertEquals("Invalid username or password.", loginPage.getError());
        events.expectLogin().session(((String) (null))).error(INVALID_USER_CREDENTIALS).removeDetail(CONSENT).assertEvent();
        loginPage.open();
        loginPage.login("test-user@localhost", "new-password");
        Assert.assertEquals(AUTH_RESPONSE, appPage.getRequestType());
        events.expectLogin().assertEvent();
    }

    @Test
    public void changePasswordWithBlankCurrentPassword() {
        changePasswordPage.open();
        loginPage.login("test-user@localhost", "password");
        events.expectLogin().client("account").detail(REDIRECT_URI, ((getAccountRedirectUrl()) + "?path=password")).assertEvent();
        changePasswordPage.changePassword("", "new", "new");
        Assert.assertEquals("Please specify password.", profilePage.getError());
        events.expectAccount(UPDATE_PASSWORD_ERROR).error(PASSWORD_MISSING).assertEvent();
        changePasswordPage.changePassword("password", "new", "new");
        Assert.assertEquals("Your password has been updated.", profilePage.getSuccess());
        events.expectAccount(UPDATE_PASSWORD).assertEvent();
    }

    @Test
    public void changePasswordWithLengthPasswordPolicy() {
        setPasswordPolicy("length(8)");
        changePasswordPage.open();
        loginPage.login("test-user@localhost", "password");
        events.expectLogin().client("account").detail(REDIRECT_URI, ((getAccountRedirectUrl()) + "?path=password")).assertEvent();
        changePasswordPage.changePassword("password", "1234", "1234");
        Assert.assertEquals("Invalid password: minimum length 8.", profilePage.getError());
        events.expectAccount(UPDATE_PASSWORD_ERROR).error(PASSWORD_REJECTED).assertEvent();
        changePasswordPage.changePassword("password", "12345678", "12345678");
        Assert.assertEquals("Your password has been updated.", profilePage.getSuccess());
        events.expectAccount(UPDATE_PASSWORD).assertEvent();
    }

    @Test
    public void changePasswordWithDigitsPolicy() {
        setPasswordPolicy("digits(2)");
        changePasswordPage.open();
        loginPage.login("test-user@localhost", "password");
        events.expectLogin().client("account").detail(REDIRECT_URI, ((getAccountRedirectUrl()) + "?path=password")).assertEvent();
        changePasswordPage.changePassword("password", "invalidPassword1", "invalidPassword1");
        Assert.assertEquals("Invalid password: must contain at least 2 numerical digits.", profilePage.getError());
        events.expectAccount(UPDATE_PASSWORD_ERROR).error(PASSWORD_REJECTED).assertEvent();
        changePasswordPage.changePassword("password", "validPassword12", "validPassword12");
        Assert.assertEquals("Your password has been updated.", profilePage.getSuccess());
        events.expectAccount(UPDATE_PASSWORD).assertEvent();
    }

    @Test
    public void changePasswordWithLowerCasePolicy() {
        setPasswordPolicy("lowerCase(2)");
        changePasswordPage.open();
        loginPage.login("test-user@localhost", "password");
        events.expectLogin().client("account").detail(REDIRECT_URI, ((getAccountRedirectUrl()) + "?path=password")).assertEvent();
        changePasswordPage.changePassword("password", "iNVALIDPASSWORD", "iNVALIDPASSWORD");
        Assert.assertEquals("Invalid password: must contain at least 2 lower case characters.", profilePage.getError());
        events.expectAccount(UPDATE_PASSWORD_ERROR).error(PASSWORD_REJECTED).assertEvent();
        changePasswordPage.changePassword("password", "vaLIDPASSWORD", "vaLIDPASSWORD");
        Assert.assertEquals("Your password has been updated.", profilePage.getSuccess());
        events.expectAccount(UPDATE_PASSWORD).assertEvent();
    }

    @Test
    public void changePasswordWithUpperCasePolicy() {
        setPasswordPolicy("upperCase(2)");
        changePasswordPage.open();
        loginPage.login("test-user@localhost", "password");
        events.expectLogin().client("account").detail(REDIRECT_URI, ((getAccountRedirectUrl()) + "?path=password")).assertEvent();
        changePasswordPage.changePassword("password", "Invalidpassword", "Invalidpassword");
        Assert.assertEquals("Invalid password: must contain at least 2 upper case characters.", profilePage.getError());
        events.expectAccount(UPDATE_PASSWORD_ERROR).error(PASSWORD_REJECTED).assertEvent();
        changePasswordPage.changePassword("password", "VAlidpassword", "VAlidpassword");
        Assert.assertEquals("Your password has been updated.", profilePage.getSuccess());
        events.expectAccount(UPDATE_PASSWORD).assertEvent();
    }

    @Test
    public void changePasswordWithSpecialCharsPolicy() {
        setPasswordPolicy("specialChars(2)");
        changePasswordPage.open();
        loginPage.login("test-user@localhost", "password");
        events.expectLogin().client("account").detail(REDIRECT_URI, ((getAccountRedirectUrl()) + "?path=password")).assertEvent();
        changePasswordPage.changePassword("password", "invalidPassword*", "invalidPassword*");
        Assert.assertEquals("Invalid password: must contain at least 2 special characters.", profilePage.getError());
        events.expectAccount(UPDATE_PASSWORD_ERROR).error(PASSWORD_REJECTED).assertEvent();
        changePasswordPage.changePassword("password", "validPassword*#", "validPassword*#");
        Assert.assertEquals("Your password has been updated.", profilePage.getSuccess());
        events.expectAccount(UPDATE_PASSWORD).assertEvent();
    }

    @Test
    public void changePasswordWithNotUsernamePolicy() {
        setPasswordPolicy("notUsername(1)");
        changePasswordPage.open();
        loginPage.login("test-user@localhost", "password");
        events.expectLogin().client("account").detail(REDIRECT_URI, ((getAccountRedirectUrl()) + "?path=password")).assertEvent();
        changePasswordPage.changePassword("password", "test-user@localhost", "test-user@localhost");
        Assert.assertEquals("Invalid password: must not be equal to the username.", profilePage.getError());
        events.expectAccount(UPDATE_PASSWORD_ERROR).error(PASSWORD_REJECTED).assertEvent();
        changePasswordPage.changePassword("password", "newPassword", "newPassword");
        Assert.assertEquals("Your password has been updated.", profilePage.getSuccess());
        events.expectAccount(UPDATE_PASSWORD).assertEvent();
    }

    @Test
    public void changePasswordWithRegexPatternsPolicy() {
        setPasswordPolicy("regexPattern(^[A-Z]+#[a-z]{8}$)");
        changePasswordPage.open();
        loginPage.login("test-user@localhost", "password");
        events.expectLogin().client("account").detail(REDIRECT_URI, ((getAccountRedirectUrl()) + "?path=password")).assertEvent();
        changePasswordPage.changePassword("password", "invalidPassword", "invalidPassword");
        Assert.assertEquals("Invalid password: fails to match regex pattern(s).", profilePage.getError());
        events.expectAccount(UPDATE_PASSWORD_ERROR).error(PASSWORD_REJECTED).assertEvent();
        changePasswordPage.changePassword("password", "VALID#password", "VALID#password");
        Assert.assertEquals("Your password has been updated.", profilePage.getSuccess());
        events.expectAccount(UPDATE_PASSWORD).assertEvent();
    }

    @Test
    public void changePasswordWithPasswordHistoryPolicyThreePasswords() {
        userId = createUser("test", "user-changePasswordWithPasswordHistoryPolicyThreePasswords", "password");
        setPasswordPolicy(((PasswordPolicy.PASSWORD_HISTORY_ID) + "(3)"));
        changePasswordPage.open();
        loginPage.login("user-changePasswordWithPasswordHistoryPolicyThreePasswords", "password");
        events.expectLogin().user(userId).client("account").detail(REDIRECT_URI, ((getAccountRedirectUrl()) + "?path=password")).assertEvent();
        assertChangePasswordFails("password", "password");// current: password

        assertNumberOfStoredCredentials(1);
        assertChangePasswordSucceeds("password", "password3");// current: password

        assertNumberOfStoredCredentials(2);
        assertChangePasswordFails("password3", "password");// current: password1, history: password

        assertNumberOfStoredCredentials(2);
        assertChangePasswordFails("password3", "password3");// current: password1, history: password

        assertNumberOfStoredCredentials(2);
        assertChangePasswordSucceeds("password3", "password4");// current: password1, history: password

        assertNumberOfStoredCredentials(3);
        assertChangePasswordFails("password4", "password");// current: password2, history: password, password1

        assertNumberOfStoredCredentials(3);
        assertChangePasswordFails("password4", "password3");// current: password2, history: password, password1

        assertNumberOfStoredCredentials(3);
        assertChangePasswordFails("password4", "password4");// current: password2, history: password, password1

        assertNumberOfStoredCredentials(3);
        assertChangePasswordSucceeds("password4", "password5");// current: password2, history: password, password1

        assertNumberOfStoredCredentials(3);
        assertChangePasswordSucceeds("password5", "password");// current: password3, history: password1, password2

        assertNumberOfStoredCredentials(3);
    }

    @Test
    public void changePasswordWithPasswordHistoryPolicyTwoPasswords() {
        userId = createUser("test", "user-changePasswordWithPasswordHistoryPolicyTwoPasswords", "password");
        setPasswordPolicy(((PasswordPolicy.PASSWORD_HISTORY_ID) + "(2)"));
        changePasswordPage.open();
        loginPage.login("user-changePasswordWithPasswordHistoryPolicyTwoPasswords", "password");
        events.expectLogin().user(userId).client("account").detail(REDIRECT_URI, ((getAccountRedirectUrl()) + "?path=password")).assertEvent();
        assertChangePasswordFails("password", "password");// current: password

        assertNumberOfStoredCredentials(1);
        assertChangePasswordSucceeds("password", "password1");// current: password

        assertNumberOfStoredCredentials(2);
        assertChangePasswordFails("password1", "password");// current: password1, history: password

        assertNumberOfStoredCredentials(2);
        assertChangePasswordFails("password1", "password1");// current: password1, history: password

        assertNumberOfStoredCredentials(2);
        assertChangePasswordSucceeds("password1", "password2");// current: password1, history: password

        assertNumberOfStoredCredentials(2);
        assertChangePasswordFails("password2", "password1");// current: password2, history: password1

        assertNumberOfStoredCredentials(2);
        assertChangePasswordFails("password2", "password2");// current: password2, history: password1

        assertNumberOfStoredCredentials(2);
        assertChangePasswordSucceeds("password2", "password");// current: password2, history: password1

        assertNumberOfStoredCredentials(2);
    }

    @Test
    public void changePasswordWithPasswordHistoryPolicyOnePwds() {
        userId = createUser("test", "user-changePasswordWithPasswordHistoryPolicyOnePwds", "password");
        // One password means only the active password is checked
        setPasswordPolicy(((PasswordPolicy.PASSWORD_HISTORY_ID) + "(1)"));
        changePasswordPage.open();
        loginPage.login("user-changePasswordWithPasswordHistoryPolicyOnePwds", "password");
        events.expectLogin().user(userId).client("account").detail(REDIRECT_URI, ((getAccountRedirectUrl()) + "?path=password")).assertEvent();
        assertChangePasswordFails("password", "password");// current: password

        assertNumberOfStoredCredentials(1);
        assertChangePasswordSucceeds("password", "password6");// current: password

        assertNumberOfStoredCredentials(1);
        assertChangePasswordFails("password6", "password6");// current: password1

        assertNumberOfStoredCredentials(1);
        assertChangePasswordSucceeds("password6", "password");// current: password1

        assertNumberOfStoredCredentials(1);
    }

    @Test
    public void changePasswordWithPasswordHistoryPolicyZeroPwdsInHistory() {
        userId = createUser("test", "user-changePasswordWithPasswordHistoryPolicyZeroPwdsInHistory", "password");
        setPasswordPolicy(((PasswordPolicy.PASSWORD_HISTORY_ID) + "(0)"));
        changePasswordPage.open();
        loginPage.login("user-changePasswordWithPasswordHistoryPolicyZeroPwdsInHistory", "password");
        events.expectLogin().user(userId).client("account").detail(REDIRECT_URI, ((getAccountRedirectUrl()) + "?path=password")).assertEvent();
        assertChangePasswordFails("password", "password");// current: password

        assertNumberOfStoredCredentials(1);
        assertChangePasswordSucceeds("password", "password1");// current: password

        assertNumberOfStoredCredentials(1);
        assertChangePasswordFails("password1", "password1");// current: password1

        assertNumberOfStoredCredentials(1);
        assertChangePasswordSucceeds("password1", "password");// current: password1

        assertNumberOfStoredCredentials(1);
    }

    @Test
    public void changePasswordWithPasswordHistoryPolicyExpiration() {
        userId = createUser("test", "user-changePasswordWithPasswordHistoryPolicyExpiration", "password");
        setPasswordPolicy(((PasswordPolicy.PASSWORD_HISTORY_ID) + "(3)"));
        changePasswordPage.open();
        loginPage.login("user-changePasswordWithPasswordHistoryPolicyExpiration", "password");
        events.expectLogin().user(userId).client("account").detail(REDIRECT_URI, ((getAccountRedirectUrl()) + "?path=password")).assertEvent();
        assertNumberOfStoredCredentials(1);
        assertChangePasswordSucceeds("password", "password2");// current: password

        assertNumberOfStoredCredentials(2);
        assertChangePasswordSucceeds("password2", "password4");// current: password2, history: password

        assertNumberOfStoredCredentials(3);
        setPasswordPolicy(((PasswordPolicy.PASSWORD_HISTORY_ID) + "(2)"));
        assertChangePasswordSucceeds("password4", "password5");// current: password4, history: password2

        assertNumberOfStoredCredentials(2);
        setPasswordPolicy(((PasswordPolicy.PASSWORD_HISTORY_ID) + "(1)"));
        assertChangePasswordSucceeds("password5", "password6");// current: password5, history: -

        assertNumberOfStoredCredentials(1);
        setPasswordPolicy(((PasswordPolicy.PASSWORD_HISTORY_ID) + "(2)"));
        assertChangePasswordSucceeds("password6", "password7");// current: password6, history: password5

        assertNumberOfStoredCredentials(2);
        setPasswordPolicy(((PasswordPolicy.PASSWORD_HISTORY_ID) + "(0)"));
        assertChangePasswordSucceeds("password7", "password8");// current: password5, history: -

        assertNumberOfStoredCredentials(1);
    }

    @Test
    public void changeProfile() throws Exception {
        setEditUsernameAllowed(false);
        setRegistrationEmailAsUsername(false);
        profilePage.open();
        loginPage.login("test-user@localhost", "password");
        events.expectLogin().client("account").detail(REDIRECT_URI, getAccountRedirectUrl()).assertEvent();
        Assert.assertEquals("test-user@localhost", profilePage.getUsername());
        Assert.assertEquals("Tom", profilePage.getFirstName());
        Assert.assertEquals("Brady", profilePage.getLastName());
        Assert.assertEquals("test-user@localhost", profilePage.getEmail());
        // All fields are required, so there should be an error when something is missing.
        profilePage.updateProfile("", "New last", "new@email.com");
        Assert.assertEquals("Please specify first name.", profilePage.getError());
        Assert.assertEquals("test-user@localhost", profilePage.getUsername());
        Assert.assertEquals("", profilePage.getFirstName());
        Assert.assertEquals("New last", profilePage.getLastName());
        Assert.assertEquals("new@email.com", profilePage.getEmail());
        events.assertEmpty();
        profilePage.updateProfile("New first", "", "new@email.com");
        Assert.assertEquals("Please specify last name.", profilePage.getError());
        Assert.assertEquals("New first", profilePage.getFirstName());
        Assert.assertEquals("", profilePage.getLastName());
        Assert.assertEquals("new@email.com", profilePage.getEmail());
        events.assertEmpty();
        profilePage.updateProfile("New first", "New last", "");
        Assert.assertEquals("Please specify email.", profilePage.getError());
        Assert.assertEquals("New first", profilePage.getFirstName());
        Assert.assertEquals("New last", profilePage.getLastName());
        Assert.assertEquals("", profilePage.getEmail());
        events.assertEmpty();
        profilePage.clickCancel();
        Assert.assertEquals("test-user@localhost", profilePage.getUsername());
        Assert.assertEquals("Tom", profilePage.getFirstName());
        Assert.assertEquals("Brady", profilePage.getLastName());
        Assert.assertEquals("test-user@localhost", profilePage.getEmail());
        events.assertEmpty();
        profilePage.updateProfile("New first", "New last", "new@email.com");
        Assert.assertEquals("Your account has been updated.", profilePage.getSuccess());
        Assert.assertEquals("test-user@localhost", profilePage.getUsername());
        Assert.assertEquals("New first", profilePage.getFirstName());
        Assert.assertEquals("New last", profilePage.getLastName());
        Assert.assertEquals("new@email.com", profilePage.getEmail());
        events.expectAccount(UPDATE_EMAIL).detail(PREVIOUS_EMAIL, "test-user@localhost").detail(UPDATED_EMAIL, "new@email.com").assertEvent();
        events.expectAccount(UPDATE_PROFILE).assertEvent();
        // reset user for other tests
        profilePage.updateProfile("Tom", "Brady", "test-user@localhost");
        events.clear();
        // Revert
        setEditUsernameAllowed(true);
    }

    @Test
    public void changeProfileEmailAsUsernameEnabled() throws Exception {
        setRegistrationEmailAsUsername(true);
        profilePage.open();
        loginPage.login("test-user@localhost", "password");
        Assert.assertFalse(((driver.findElements(By.id("username")).size()) > 0));
        // Revert
        setRegistrationEmailAsUsername(false);
    }

    // KEYCLOAK-5443
    @Test
    public void changeProfileEmailAsUsernameAndEditUsernameEnabled() throws Exception {
        setEditUsernameAllowed(true);
        setRegistrationEmailAsUsername(true);
        profilePage.open();
        loginPage.login("test-user@localhost", "password");
        Assert.assertFalse(((driver.findElements(By.id("username")).size()) > 0));
        profilePage.updateProfile("New First", "New Last", "new-email@email");
        Assert.assertEquals("Your account has been updated.", profilePage.getSuccess());
        Assert.assertEquals("New First", profilePage.getFirstName());
        Assert.assertEquals("New Last", profilePage.getLastName());
        Assert.assertEquals("new-email@email", profilePage.getEmail());
        List<UserRepresentation> list = adminClient.realm("test").users().search(null, null, null, "new-email@email", null, null);
        Assert.assertEquals(1, list.size());
        UserRepresentation user = list.get(0);
        Assert.assertEquals("new-email@email", user.getUsername());
        // Revert
        user.setUsername("test-user@localhost");
        user.setFirstName("Tom");
        user.setLastName("Brady");
        user.setEmail("test-user@localhost");
        adminClient.realm("test").users().get(user.getId()).update(user);
        setRegistrationEmailAsUsername(false);
        setEditUsernameAllowed(false);
    }

    @Test
    public void changeUsername() {
        // allow to edit the username in realm
        setEditUsernameAllowed(true);
        profilePage.open();
        loginPage.login("test-user@localhost", "password");
        events.expectLogin().client("account").detail(REDIRECT_URI, getAccountRedirectUrl()).assertEvent();
        Assert.assertEquals("test-user@localhost", profilePage.getUsername());
        Assert.assertEquals("Tom", profilePage.getFirstName());
        Assert.assertEquals("Brady", profilePage.getLastName());
        Assert.assertEquals("test-user@localhost", profilePage.getEmail());
        // All fields are required, so there should be an error when something is missing.
        profilePage.updateProfile("", "New first", "New last", "new@email.com");
        Assert.assertEquals("Please specify username.", profilePage.getError());
        Assert.assertEquals("", profilePage.getUsername());
        Assert.assertEquals("New first", profilePage.getFirstName());
        Assert.assertEquals("New last", profilePage.getLastName());
        Assert.assertEquals("new@email.com", profilePage.getEmail());
        events.assertEmpty();
        // Change to the username already occupied by other user
        profilePage.updateProfile("test-user-no-access@localhost", "New first", "New last", "new@email.com");
        Assert.assertEquals("Username already exists.", profilePage.getError());
        Assert.assertEquals("test-user-no-access@localhost", profilePage.getUsername());
        Assert.assertEquals("New first", profilePage.getFirstName());
        Assert.assertEquals("New last", profilePage.getLastName());
        Assert.assertEquals("new@email.com", profilePage.getEmail());
        events.assertEmpty();
        profilePage.updateProfile("test-user-new@localhost", "New first", "New last", "new@email.com");
        Assert.assertEquals("Your account has been updated.", profilePage.getSuccess());
        Assert.assertEquals("test-user-new@localhost", profilePage.getUsername());
        Assert.assertEquals("New first", profilePage.getFirstName());
        Assert.assertEquals("New last", profilePage.getLastName());
        Assert.assertEquals("new@email.com", profilePage.getEmail());
        // Revert
        profilePage.updateProfile("test-user@localhost", "Tom", "Brady", "test-user@localhost");
    }

    @Test
    public void changeUsernameLoginWithOldUsername() {
        addUser("change-username", "change-username@localhost");
        setEditUsernameAllowed(true);
        profilePage.open();
        loginPage.login("change-username", "password");
        profilePage.updateUsername("change-username-updated");
        Assert.assertEquals("Your account has been updated.", profilePage.getSuccess());
        profilePage.logout();
        profilePage.open();
        Assert.assertTrue(loginPage.isCurrent());
        loginPage.login("change-username", "password");
        Assert.assertTrue(loginPage.isCurrent());
        Assert.assertEquals("Invalid username or password.", loginPage.getError());
        loginPage.login("change-username-updated", "password");
    }

    @Test
    public void changeEmailLoginWithOldEmail() {
        addUser("change-email", "change-username@localhost");
        setEditUsernameAllowed(true);
        profilePage.open();
        loginPage.login("change-username@localhost", "password");
        profilePage.updateEmail("change-username-updated@localhost");
        Assert.assertEquals("Your account has been updated.", profilePage.getSuccess());
        profilePage.logout();
        profilePage.open();
        Assert.assertTrue(loginPage.isCurrent());
        loginPage.login("change-username@localhost", "password");
        Assert.assertTrue(loginPage.isCurrent());
        Assert.assertEquals("Invalid username or password.", loginPage.getError());
        loginPage.login("change-username-updated@localhost", "password");
    }

    // KEYCLOAK-1534
    @Test
    public void changeEmailToExistingForbidden() {
        profilePage.open();
        loginPage.login("test-user@localhost", "password");
        events.expectLogin().client("account").detail(REDIRECT_URI, getAccountRedirectUrl()).assertEvent();
        Assert.assertEquals("test-user@localhost", profilePage.getUsername());
        Assert.assertEquals("test-user@localhost", profilePage.getEmail());
        // Change to the email, which some other user has
        profilePage.updateProfile("New first", "New last", "test-user-no-access@localhost");
        profilePage.assertCurrent();
        Assert.assertEquals("Email already exists.", profilePage.getError());
        Assert.assertEquals("New first", profilePage.getFirstName());
        Assert.assertEquals("New last", profilePage.getLastName());
        Assert.assertEquals("test-user-no-access@localhost", profilePage.getEmail());
        events.assertEmpty();
        // Change some other things, but not email
        profilePage.updateProfile("New first", "New last", "test-user@localhost");
        Assert.assertEquals("Your account has been updated.", profilePage.getSuccess());
        Assert.assertEquals("New first", profilePage.getFirstName());
        Assert.assertEquals("New last", profilePage.getLastName());
        Assert.assertEquals("test-user@localhost", profilePage.getEmail());
        events.expectAccount(UPDATE_PROFILE).assertEvent();
        // Change email and other things to original values
        profilePage.updateProfile("Tom", "Brady", "test-user@localhost");
        events.expectAccount(UPDATE_PROFILE).assertEvent();
    }

    @Test
    public void changeEmailToExistingAllowed() {
        setDuplicateEmailsAllowed(true);
        profilePage.open();
        loginPage.login("test-user@localhost", "password");
        events.expectLogin().client("account").detail(REDIRECT_URI, getAccountRedirectUrl()).assertEvent();
        Assert.assertEquals("test-user@localhost", profilePage.getUsername());
        Assert.assertEquals("test-user@localhost", profilePage.getEmail());
        // Change to the email, which some other user has
        profilePage.updateProfile("New first", "New last", "test-user-no-access@localhost");
        Assert.assertEquals("Your account has been updated.", profilePage.getSuccess());
    }

    @Test
    public void setupTotp() {
        totpPage.open();
        loginPage.login("test-user@localhost", "password");
        events.expectLogin().client("account").detail(REDIRECT_URI, ((getAccountRedirectUrl()) + "?path=totp")).assertEvent();
        Assert.assertTrue(totpPage.isCurrent());
        Assert.assertFalse(driver.getPageSource().contains("Remove Google"));
        String pageSource = driver.getPageSource();
        Assert.assertTrue(pageSource.contains("Install one of the following applications on your mobile"));
        Assert.assertTrue(pageSource.contains("FreeOTP"));
        Assert.assertTrue(pageSource.contains("Google Authenticator"));
        Assert.assertTrue(pageSource.contains("Open the application and scan the barcode"));
        Assert.assertFalse(pageSource.contains("Open the application and enter the key"));
        Assert.assertTrue(pageSource.contains("Unable to scan?"));
        Assert.assertFalse(pageSource.contains("Scan barcode?"));
        totpPage.clickManual();
        pageSource = driver.getPageSource();
        Assert.assertTrue(pageSource.contains("Install one of the following applications on your mobile"));
        Assert.assertTrue(pageSource.contains("FreeOTP"));
        Assert.assertTrue(pageSource.contains("Google Authenticator"));
        Assert.assertFalse(pageSource.contains("Open the application and scan the barcode"));
        Assert.assertTrue(pageSource.contains("Open the application and enter the key"));
        Assert.assertFalse(pageSource.contains("Unable to scan?"));
        Assert.assertTrue(pageSource.contains("Scan barcode?"));
        Assert.assertTrue(UIUtils.getTextFromElement(driver.findElement(By.id("kc-totp-secret-key"))).matches("[\\w]{4}( [\\w]{4}){7}"));
        Assert.assertEquals("Type: Time-based", driver.findElement(By.id("kc-totp-type")).getText());
        Assert.assertEquals("Algorithm: SHA1", driver.findElement(By.id("kc-totp-algorithm")).getText());
        Assert.assertEquals("Digits: 6", driver.findElement(By.id("kc-totp-digits")).getText());
        Assert.assertEquals("Interval: 30", driver.findElement(By.id("kc-totp-period")).getText());
        // Error with false code
        totpPage.configure(totp.generateTOTP(((totpPage.getTotpSecret()) + "123")));
        Assert.assertEquals("Invalid authenticator code.", profilePage.getError());
        totpPage.configure(totp.generateTOTP(totpPage.getTotpSecret()));
        Assert.assertEquals("Mobile authenticator configured.", profilePage.getSuccess());
        events.expectAccount(UPDATE_TOTP).assertEvent();
        Assert.assertTrue(driver.getPageSource().contains("pficon-delete"));
        totpPage.removeTotp();
        events.expectAccount(REMOVE_TOTP).assertEvent();
        accountPage.logOut();
        Assert.assertFalse(errorPage.isCurrent());
    }

    @Test
    public void changeProfileNoAccess() throws Exception {
        profilePage.open();
        loginPage.login("test-user-no-access@localhost", "password");
        UserRepresentation noAccessUser = this.findUser("test-user-no-access@localhost");
        events.expectLogin().client("account").user(noAccessUser.getId()).detail(USERNAME, "test-user-no-access@localhost").detail(REDIRECT_URI, getAccountRedirectUrl()).assertEvent();
        Assert.assertTrue(((((("Expected errorPage but was " + (driver.getTitle())) + " (") + (driver.getCurrentUrl())) + "). Page source: ") + (driver.getPageSource())), errorPage.isCurrent());
        Assert.assertEquals("No access", errorPage.getError());
    }

    @Test
    public void viewLog() {
        setEventsEnabled();
        List<EventRepresentation> expectedEvents = new LinkedList<>();
        loginPage.open();
        loginPage.clickRegister();
        registerPage.register("view", "log", "view-log@localhost", "view-log", "password", "password");
        expectedEvents.add(events.poll());
        expectedEvents.add(events.poll());
        profilePage.open();
        profilePage.updateProfile("view", "log2", "view-log@localhost");
        expectedEvents.add(events.poll());
        logPage.open();
        Assert.assertTrue(logPage.isCurrent());
        List<List<String>> actualEvents = logPage.getEvents();
        Assert.assertEquals(expectedEvents.size(), actualEvents.size());
        for (EventRepresentation e : expectedEvents) {
            boolean match = false;
            for (List<String> a : logPage.getEvents()) {
                if (((e.getType().toString().replace('_', ' ').toLowerCase().equals(a.get(1))) && (e.getIpAddress().equals(a.get(2)))) && (e.getClientId().equals(a.get(3)))) {
                    match = true;
                    break;
                }
            }
            if (!match) {
                Assert.fail(("Event not found " + (e.getType())));
            }
        }
    }

    @Test
    public void sessions() {
        loginPage.open();
        loginPage.clickRegister();
        registerPage.register("view", "sessions", "view-sessions@localhost", "view-sessions", "password", "password");
        EventRepresentation registerEvent = events.expectRegister("view-sessions", "view-sessions@localhost").assertEvent();
        String userId = registerEvent.getUserId();
        events.expectLogin().user(userId).detail(USERNAME, "view-sessions").assertEvent();
        sessionsPage.open();
        Assert.assertTrue(sessionsPage.isCurrent());
        List<List<String>> sessions = sessionsPage.getSessions();
        Assert.assertEquals(1, sessions.size());
        Assert.assertEquals("127.0.0.1", sessions.get(0).get(0));
        // Create second session
        try {
            OAuthClient oauth2 = new OAuthClient();
            oauth2.init(driver2);
            oauth2.doLogin("view-sessions", "password");
            EventRepresentation login2Event = events.expectLogin().user(userId).detail(USERNAME, "view-sessions").assertEvent();
            sessionsPage.open();
            sessions = sessionsPage.getSessions();
            Assert.assertEquals(2, sessions.size());
            sessionsPage.logoutAll();
            events.expectLogout(registerEvent.getSessionId());
            events.expectLogout(login2Event.getSessionId());
        } finally {
            driver2.close();
        }
    }

    // KEYCLOAK-5155
    @Test
    public void testConsoleListedInApplications() {
        applicationsPage.open();
        loginPage.login("realm-admin", "password");
        Assert.assertTrue(applicationsPage.isCurrent());
        Map<String, AccountApplicationsPage.AppEntry> apps = applicationsPage.getApplications();
        Assert.assertThat(apps.keySet(), Matchers.hasItems("Admin CLI", "Security Admin Console"));
        events.clear();
    }

    @Test
    public void applicationsVisibilityNoScopesNoConsent() throws Exception {
        try (ClientAttributeUpdater cau = ClientAttributeUpdater.forClient(adminClient, AccountFormServiceTest.REALM_NAME, AccountFormServiceTest.ROOT_URL_CLIENT).setConsentRequired(false).setFullScopeAllowed(false).setDefaultClientScopes(Collections.EMPTY_LIST).setOptionalClientScopes(Collections.EMPTY_LIST).update();RoleScopeUpdater rsu = cau.realmRoleScope().update()) {
            applicationsPage.open();
            loginPage.login("john-doh@localhost", "password");
            applicationsPage.assertCurrent();
            Map<String, AccountApplicationsPage.AppEntry> apps = applicationsPage.getApplications();
            Assert.assertThat(apps.keySet(), /* "root-url-client", */
            Matchers.containsInAnyOrder("Account", "test-app", "test-app-scope", "third-party", "test-app-authz", "My Named Test App", "Test App Named - ${client_account}", "direct-grant"));
            rsu.add(testRealm().roles().get("user").toRepresentation()).update();
            driver.navigate().refresh();
            apps = applicationsPage.getApplications();
            Assert.assertThat(apps.keySet(), Matchers.containsInAnyOrder("root-url-client", "Account", "test-app", "test-app-scope", "third-party", "test-app-authz", "My Named Test App", "Test App Named - ${client_account}", "direct-grant"));
        }
    }

    @Test
    public void applicationsVisibilityNoScopesAndConsent() throws Exception {
        try (ClientAttributeUpdater cau = ClientAttributeUpdater.forClient(adminClient, AccountFormServiceTest.REALM_NAME, AccountFormServiceTest.ROOT_URL_CLIENT).setConsentRequired(true).setFullScopeAllowed(false).setDefaultClientScopes(Collections.EMPTY_LIST).setOptionalClientScopes(Collections.EMPTY_LIST).update()) {
            applicationsPage.open();
            loginPage.login("john-doh@localhost", "password");
            applicationsPage.assertCurrent();
            Map<String, AccountApplicationsPage.AppEntry> apps = applicationsPage.getApplications();
            Assert.assertThat(apps.keySet(), Matchers.containsInAnyOrder("root-url-client", "Account", "test-app", "test-app-scope", "third-party", "test-app-authz", "My Named Test App", "Test App Named - ${client_account}", "direct-grant"));
        }
    }

    // More tests (including revoke) are in OAuthGrantTest and OfflineTokenTest
    @Test
    public void applications() {
        applicationsPage.open();
        loginPage.login("test-user@localhost", "password");
        events.expectLogin().client("account").detail(REDIRECT_URI, ((getAccountRedirectUrl()) + "?path=applications")).assertEvent();
        applicationsPage.assertCurrent();
        Map<String, AccountApplicationsPage.AppEntry> apps = applicationsPage.getApplications();
        Assert.assertThat(apps.keySet(), Matchers.containsInAnyOrder("root-url-client", "Account", "Broker", "test-app", "test-app-scope", "third-party", "test-app-authz", "My Named Test App", "Test App Named - ${client_account}", "direct-grant"));
        AccountApplicationsPage.AppEntry accountEntry = apps.get("Account");
        Assert.assertThat(accountEntry.getRolesAvailable(), Matchers.containsInAnyOrder("Manage account links in Account", "Manage account in Account", "View profile in Account", "Offline access"));
        Assert.assertThat(accountEntry.getClientScopesGranted(), Matchers.containsInAnyOrder("Full Access"));
        Assert.assertEquals(((oauth.AUTH_SERVER_ROOT) + "/realms/test/account"), accountEntry.getHref());
        AccountApplicationsPage.AppEntry testAppEntry = apps.get("test-app");
        Assert.assertEquals(6, testAppEntry.getRolesAvailable().size());
        Assert.assertTrue(testAppEntry.getRolesAvailable().contains("Offline access"));
        Assert.assertTrue(testAppEntry.getClientScopesGranted().contains("Full Access"));
        Assert.assertEquals(oauth.APP_AUTH_ROOT, testAppEntry.getHref());
        AccountApplicationsPage.AppEntry thirdPartyEntry = apps.get("third-party");
        Assert.assertEquals(3, thirdPartyEntry.getRolesAvailable().size());
        Assert.assertTrue(thirdPartyEntry.getRolesAvailable().contains("Have User privileges"));
        Assert.assertTrue(thirdPartyEntry.getRolesAvailable().contains("Have Customer User privileges in test-app"));
        Assert.assertEquals(0, thirdPartyEntry.getClientScopesGranted().size());
        Assert.assertEquals("http://localhost:8180/auth/realms/master/app/auth", thirdPartyEntry.getHref());
        AccountApplicationsPage.AppEntry testAppNamed = apps.get("Test App Named - ${client_account}");
        Assert.assertEquals("http://localhost:8180/varnamedapp/base", testAppNamed.getHref());
        AccountApplicationsPage.AppEntry rootUrlClient = apps.get("root-url-client");
        Assert.assertEquals("http://localhost:8180/foo/bar/baz", rootUrlClient.getHref());
        AccountApplicationsPage.AppEntry authzApp = apps.get("test-app-authz");
        Assert.assertEquals(((oauth.SERVER_ROOT) + "/test-app-authz"), authzApp.getHref());
        AccountApplicationsPage.AppEntry namedApp = apps.get("My Named Test App");
        Assert.assertEquals("http://localhost:8180/namedapp/base", namedApp.getHref());
        AccountApplicationsPage.AppEntry testAppScope = apps.get("test-app-scope");
        Assert.assertNull(testAppScope.getHref());
    }

    @Test
    public void loginToSpecificPage() {
        changePasswordPage.open();
        loginPage.login("test-user@localhost", "password");
        Assert.assertTrue(changePasswordPage.isCurrent());
        events.clear();
    }

    @Test
    public void loginToSpecificPageWithReferrer() {
        driver.navigate().to(((changePasswordPage.getPath()) + "?referrer=account"));
        System.out.println(driver.getCurrentUrl());
        loginPage.login("test-user@localhost", "password");
        System.out.println(driver.getCurrentUrl());
        Assert.assertTrue(changePasswordPage.isCurrent());
        events.clear();
    }

    @Test
    public void testIdentityProviderCapitalization() {
        loginPage.open();
        Assert.assertEquals("GitHub", loginPage.findSocialButton("github").getText());
        Assert.assertEquals("mysaml", loginPage.findSocialButton("mysaml").getText());
        Assert.assertEquals("MyOIDC", loginPage.findSocialButton("myoidc").getText());
    }

    @Test
    public void testIdentityProviderHiddenOnLoginPageIsVisbleInAccount() {
        federatedIdentityPage.open();
        loginPage.login("test-user@localhost", "password");
        Assert.assertNotNull(federatedIdentityPage.findAddProvider("myhiddenoidc"));
    }

    @Test
    public void testInvalidReferrer() {
        driver.navigate().to(((profilePage.getPath()) + "?referrer=test-app"));
        loginPage.login("test-user@localhost", "password");
        Assert.assertTrue(profilePage.isCurrent());
        profilePage.backToApplication();
        Assert.assertTrue(appPage.isCurrent());
        driver.navigate().to(((profilePage.getPath()) + "?referrer=test-invalid&referrer_uri=http://localhost:8180/auth/realms/master/app/auth?test"));
        Assert.assertTrue(profilePage.isCurrent());
        events.clear();
    }

    @Test
    public void testReferrerLinkContents() {
        RealmResource testRealm = testRealm();
        List<ClientRepresentation> foundClients = testRealm.clients().findByClientId("named-test-app");
        if (foundClients.isEmpty()) {
            Assert.fail("Unable to find named-test-app");
        }
        ClientRepresentation namedClient = foundClients.get(0);
        driver.navigate().to((((profilePage.getPath()) + "?referrer=") + (namedClient.getClientId())));
        loginPage.login("test-user@localhost", "password");
        Assert.assertTrue(profilePage.isCurrent());
        // When a client has a name provided, the name should be available to the back link
        Assert.assertEquals(("Back to " + (namedClient.getName())), profilePage.getBackToApplicationLinkText());
        Assert.assertEquals(namedClient.getBaseUrl(), profilePage.getBackToApplicationLinkHref());
        foundClients = testRealm.clients().findByClientId("var-named-test-app");
        if (foundClients.isEmpty()) {
            Assert.fail("Unable to find var-named-test-app");
        }
        namedClient = foundClients.get(0);
        driver.navigate().to((((profilePage.getPath()) + "?referrer=") + (namedClient.getClientId())));
        Assert.assertTrue(profilePage.isCurrent());
        // When a client has a name provided as a variable, the name should be resolved using a localized bundle and available to the back link
        Assert.assertEquals("Back to Test App Named - Account", profilePage.getBackToApplicationLinkText());
        Assert.assertEquals(namedClient.getBaseUrl(), profilePage.getBackToApplicationLinkHref());
        foundClients = testRealm.clients().findByClientId("test-app");
        if (foundClients.isEmpty()) {
            Assert.fail("Unable to find test-app");
        }
        ClientRepresentation namelessClient = foundClients.get(0);
        driver.navigate().to((((profilePage.getPath()) + "?referrer=") + (namelessClient.getClientId())));
        Assert.assertTrue(profilePage.isCurrent());
        // When a client has no name provided, the client-id should be available to the back link
        Assert.assertEquals(("Back to " + (namelessClient.getClientId())), profilePage.getBackToApplicationLinkText());
        Assert.assertEquals(namelessClient.getBaseUrl(), profilePage.getBackToApplicationLinkHref());
        driver.navigate().to(((profilePage.getPath()) + "?referrer=test-invalid"));
        Assert.assertTrue(profilePage.isCurrent());
        // When a client is invalid, the back link should not exist
        Assert.assertNull(profilePage.getBackToApplicationLinkText());
        events.clear();
    }
}

