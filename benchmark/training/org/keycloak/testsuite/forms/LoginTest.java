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


import Algorithm.ES256;
import Algorithm.RS256;
import BrowserSecurityHeaders.defaultHeaders;
import BrowserSecurityHeaders.headerAttributeMap;
import Constants.ADMIN_CONSOLE_CLIENT_ID;
import Details.CONSENT;
import Details.REMEMBER_ME;
import Details.RESTART_AFTER_TIMEOUT;
import Details.USERNAME;
import Errors.EXPIRED_CODE;
import EventType.UPDATE_PASSWORD;
import OAuth2Constants.CODE;
import RequestType.AUTH_RESPONSE;
import java.util.Map;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import org.hamcrest.Matchers;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.jose.jws.JWSInput;
import org.keycloak.jose.jws.JWSInputException;
import org.keycloak.models.utils.SessionTimeoutHelper;
import org.keycloak.protocol.oidc.OIDCLoginProtocolService;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.EventRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.AbstractTestRealmKeycloakTest;
import org.keycloak.testsuite.AssertEvents;
import org.keycloak.testsuite.admin.ApiUtil;
import org.keycloak.testsuite.arquillian.AuthServerTestEnricher;
import org.keycloak.testsuite.pages.AppPage;
import org.keycloak.testsuite.pages.ErrorPage;
import org.keycloak.testsuite.pages.LoginPage;
import org.keycloak.testsuite.pages.LoginPasswordUpdatePage;
import org.keycloak.testsuite.util.OAuthClient;
import org.keycloak.testsuite.util.TokenSignatureUtil;
import org.openqa.selenium.NoSuchElementException;


/**
 *
 *
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class LoginTest extends AbstractTestRealmKeycloakTest {
    @Rule
    public AssertEvents events = new AssertEvents(this);

    @Page
    protected AppPage appPage;

    @Page
    protected LoginPage loginPage;

    @Page
    protected ErrorPage errorPage;

    @Page
    protected LoginPasswordUpdatePage updatePasswordPage;

    private static String userId;

    private static String user2Id;

    @Test
    public void testBrowserSecurityHeaders() {
        Client client = ClientBuilder.newClient();
        Response response = client.target(oauth.getLoginFormUrl()).request().get();
        Assert.assertThat(response.getStatus(), Matchers.is(Matchers.equalTo(200)));
        for (Map.Entry<String, String> entry : defaultHeaders.entrySet()) {
            String headerName = headerAttributeMap.get(entry.getKey());
            String headerValue = response.getHeaderString(headerName);
            if (entry.getValue().isEmpty()) {
                Assert.assertNull(headerValue);
            } else {
                Assert.assertNotNull(headerValue);
                Assert.assertThat(headerValue, Matchers.is(Matchers.equalTo(entry.getValue())));
            }
        }
        response.close();
        client.close();
    }

    @Test
    public void testContentSecurityPolicyReportOnlyBrowserSecurityHeader() {
        final String expectedCspReportOnlyValue = "default-src 'none'";
        final String cspReportOnlyAttr = "contentSecurityPolicyReportOnly";
        final String cspReportOnlyHeader = "Content-Security-Policy-Report-Only";
        RealmRepresentation realmRep = adminClient.realm("test").toRepresentation();
        final String defaultContentSecurityPolicyReportOnly = realmRep.getBrowserSecurityHeaders().get(cspReportOnlyAttr);
        realmRep.getBrowserSecurityHeaders().put(cspReportOnlyAttr, expectedCspReportOnlyValue);
        adminClient.realm("test").update(realmRep);
        try {
            Client client = ClientBuilder.newClient();
            Response response = client.target(oauth.getLoginFormUrl()).request().get();
            String headerValue = response.getHeaderString(cspReportOnlyHeader);
            Assert.assertThat(headerValue, Matchers.is(Matchers.equalTo(expectedCspReportOnlyValue)));
            response.close();
            client.close();
        } finally {
            realmRep.getBrowserSecurityHeaders().put(cspReportOnlyAttr, defaultContentSecurityPolicyReportOnly);
            adminClient.realm("test").update(realmRep);
        }
    }

    // KEYCLOAK-5556
    @Test
    public void testPOSTAuthenticationRequest() {
        Client client = ClientBuilder.newClient();
        // POST request to http://localhost:8180/auth/realms/test/protocol/openid-connect/auth;
        UriBuilder b = OIDCLoginProtocolService.authUrl(UriBuilder.fromUri(OAuthClient.AUTH_SERVER_ROOT));
        Response response = client.target(b.build(oauth.getRealm())).request().post(oauth.getLoginEntityForPOST());
        Assert.assertThat(response.getStatus(), Matchers.is(Matchers.equalTo(200)));
        Assert.assertThat(response, org.keycloak.testsuite.util.Matchers.body(Matchers.containsString("Log In")));
        response.close();
        client.close();
    }

    @Test
    public void loginChangeUserAfterInvalidPassword() {
        loginPage.open();
        loginPage.login("login-test2", "invalid");
        loginPage.assertCurrent();
        Assert.assertEquals("login-test2", loginPage.getUsername());
        Assert.assertEquals("", loginPage.getPassword());
        Assert.assertEquals("Invalid username or password.", loginPage.getError());
        events.expectLogin().user(LoginTest.user2Id).session(((String) (null))).error("invalid_user_credentials").detail(USERNAME, "login-test2").removeDetail(CONSENT).assertEvent();
        loginPage.login("login-test", "password");
        Assert.assertEquals(AUTH_RESPONSE, appPage.getRequestType());
        Assert.assertNotNull(oauth.getCurrentQuery().get(CODE));
        events.expectLogin().user(LoginTest.userId).detail(USERNAME, "login-test").assertEvent();
    }

    @Test
    public void loginInvalidPassword() {
        loginPage.open();
        loginPage.login("login-test", "invalid");
        loginPage.assertCurrent();
        // KEYCLOAK-1741 - assert form field values kept
        Assert.assertEquals("login-test", loginPage.getUsername());
        Assert.assertEquals("", loginPage.getPassword());
        Assert.assertEquals("Invalid username or password.", loginPage.getError());
        events.expectLogin().user(LoginTest.userId).session(((String) (null))).error("invalid_user_credentials").detail(USERNAME, "login-test").removeDetail(CONSENT).assertEvent();
    }

    @Test
    public void loginMissingPassword() {
        loginPage.open();
        loginPage.missingPassword("login-test");
        loginPage.assertCurrent();
        // KEYCLOAK-1741 - assert form field values kept
        Assert.assertEquals("login-test", loginPage.getUsername());
        Assert.assertEquals("", loginPage.getPassword());
        Assert.assertEquals("Invalid username or password.", loginPage.getError());
        events.expectLogin().user(LoginTest.userId).session(((String) (null))).error("invalid_user_credentials").detail(USERNAME, "login-test").removeDetail(CONSENT).assertEvent();
    }

    @Test
    public void loginInvalidPasswordDisabledUser() {
        setUserEnabled("login-test", false);
        try {
            loginPage.open();
            loginPage.login("login-test", "invalid");
            loginPage.assertCurrent();
            // KEYCLOAK-1741 - assert form field values kept
            Assert.assertEquals("login-test", loginPage.getUsername());
            Assert.assertEquals("", loginPage.getPassword());
            // KEYCLOAK-2024
            Assert.assertEquals("Invalid username or password.", loginPage.getError());
            events.expectLogin().user(LoginTest.userId).session(((String) (null))).error("invalid_user_credentials").detail(USERNAME, "login-test").removeDetail(CONSENT).assertEvent();
        } finally {
            setUserEnabled("login-test", true);
        }
    }

    @Test
    public void loginDisabledUser() {
        setUserEnabled("login-test", false);
        try {
            loginPage.open();
            loginPage.login("login-test", "password");
            loginPage.assertCurrent();
            // KEYCLOAK-1741 - assert form field values kept
            Assert.assertEquals("login-test", loginPage.getUsername());
            Assert.assertEquals("", loginPage.getPassword());
            // KEYCLOAK-2024
            Assert.assertEquals("Account is disabled, contact admin.", loginPage.getError());
            events.expectLogin().user(LoginTest.userId).session(((String) (null))).error("user_disabled").detail(USERNAME, "login-test").removeDetail(CONSENT).assertEvent();
        } finally {
            setUserEnabled("login-test", true);
        }
    }

    @Test
    public void loginInvalidUsername() {
        loginPage.open();
        loginPage.login("invalid", "password");
        loginPage.assertCurrent();
        // KEYCLOAK-1741 - assert form field values kept
        Assert.assertEquals("invalid", loginPage.getUsername());
        Assert.assertEquals("", loginPage.getPassword());
        Assert.assertEquals("Invalid username or password.", loginPage.getError());
        events.expectLogin().user(((String) (null))).session(((String) (null))).error("user_not_found").detail(USERNAME, "invalid").removeDetail(CONSENT).assertEvent();
        loginPage.login("login-test", "password");
        Assert.assertEquals(AUTH_RESPONSE, appPage.getRequestType());
        Assert.assertNotNull(oauth.getCurrentQuery().get(CODE));
        events.expectLogin().user(LoginTest.userId).detail(USERNAME, "login-test").assertEvent();
    }

    @Test
    public void loginMissingUsername() {
        loginPage.open();
        loginPage.missingUsername();
        loginPage.assertCurrent();
        Assert.assertEquals("Invalid username or password.", loginPage.getError());
        events.expectLogin().user(((String) (null))).session(((String) (null))).error("user_not_found").removeDetail(CONSENT).assertEvent();
    }

    // KEYCLOAK-2557
    @Test
    public void loginUserWithEmailAsUsername() {
        loginPage.open();
        loginPage.login("login@test.com", "password");
        Assert.assertEquals(AUTH_RESPONSE, appPage.getRequestType());
        Assert.assertNotNull(oauth.getCurrentQuery().get(CODE));
        events.expectLogin().user(LoginTest.userId).detail(USERNAME, "login@test.com").assertEvent();
    }

    @Test
    public void loginSuccess() {
        loginPage.open();
        loginPage.login("login-test", "password");
        Assert.assertEquals(AUTH_RESPONSE, appPage.getRequestType());
        Assert.assertNotNull(oauth.getCurrentQuery().get(CODE));
        events.expectLogin().user(LoginTest.userId).detail(USERNAME, "login-test").assertEvent();
    }

    @Test
    public void loginSuccessRealmSigningAlgorithms() throws JWSInputException {
        loginPage.open();
        loginPage.login("login-test", "password");
        Assert.assertEquals(AUTH_RESPONSE, appPage.getRequestType());
        Assert.assertNotNull(oauth.getCurrentQuery().get(CODE));
        events.expectLogin().user(LoginTest.userId).detail(USERNAME, "login-test").assertEvent();
        driver.navigate().to(((AuthServerTestEnricher.getAuthServerContextRoot()) + "/auth/realms/test/"));
        String keycloakIdentity = driver.manage().getCookieNamed("KEYCLOAK_IDENTITY").getValue();
        // Check identity cookie is signed with HS256
        String algorithm = new JWSInput(keycloakIdentity).getHeader().getAlgorithm().name();
        Assert.assertEquals("HS256", algorithm);
        try {
            TokenSignatureUtil.registerKeyProvider("P-256", adminClient, testContext);
            TokenSignatureUtil.changeRealmTokenSignatureProvider(adminClient, ES256);
            oauth.openLoginForm();
            Assert.assertEquals(AUTH_RESPONSE, appPage.getRequestType());
            driver.navigate().to(((AuthServerTestEnricher.getAuthServerContextRoot()) + "/auth/realms/test/"));
            keycloakIdentity = driver.manage().getCookieNamed("KEYCLOAK_IDENTITY").getValue();
            // Check identity cookie is still signed with HS256
            algorithm = new JWSInput(keycloakIdentity).getHeader().getAlgorithm().name();
            Assert.assertEquals("HS256", algorithm);
            // Check identity cookie still works
            oauth.openLoginForm();
            Assert.assertEquals(AUTH_RESPONSE, appPage.getRequestType());
        } finally {
            TokenSignatureUtil.changeRealmTokenSignatureProvider(adminClient, RS256);
        }
    }

    @Test
    public void loginWithWhitespaceSuccess() {
        loginPage.open();
        loginPage.login(" login-test \t ", "password");
        Assert.assertEquals(AUTH_RESPONSE, appPage.getRequestType());
        Assert.assertNotNull(oauth.getCurrentQuery().get(CODE));
        events.expectLogin().user(LoginTest.userId).detail(USERNAME, "login-test").assertEvent();
    }

    @Test
    public void loginWithEmailWhitespaceSuccess() {
        loginPage.open();
        loginPage.login("    login@test.com    ", "password");
        Assert.assertEquals(AUTH_RESPONSE, appPage.getRequestType());
        Assert.assertNotNull(oauth.getCurrentQuery().get(CODE));
        events.expectLogin().user(LoginTest.userId).assertEvent();
    }

    @Test
    public void loginWithForcePasswordChangePolicy() {
        setPasswordPolicy("forceExpiredPasswordChange(1)");
        try {
            // Setting offset to more than one day to force password update
            // elapsedTime > timeToExpire
            setTimeOffset(86405);
            loginPage.open();
            loginPage.login("login-test", "password");
            updatePasswordPage.assertCurrent();
            updatePasswordPage.changePassword("updatedPassword", "updatedPassword");
            setTimeOffset(0);
            events.expectRequiredAction(UPDATE_PASSWORD).user(LoginTest.userId).detail(USERNAME, "login-test").assertEvent();
            String currentUrl = driver.getCurrentUrl();
            String pageSource = driver.getPageSource();
            Assert.assertEquals(("bad expectation, on page: " + currentUrl), AUTH_RESPONSE, appPage.getRequestType());
            events.expectLogin().user(LoginTest.userId).detail(USERNAME, "login-test").assertEvent();
        } finally {
            setPasswordPolicy(null);
            UserResource userRsc = adminClient.realm("test").users().get("login-test");
            ApiUtil.resetUserPassword(userRsc, "password", false);
        }
    }

    @Test
    public void loginWithoutForcePasswordChangePolicy() {
        setPasswordPolicy("forceExpiredPasswordChange(1)");
        try {
            // Setting offset to less than one day to avoid forced password update
            // elapsedTime < timeToExpire
            setTimeOffset(86205);
            loginPage.open();
            loginPage.login("login-test", "password");
            Assert.assertEquals(AUTH_RESPONSE, appPage.getRequestType());
            Assert.assertNotNull(oauth.getCurrentQuery().get(CODE));
            setTimeOffset(0);
            events.expectLogin().user(LoginTest.userId).detail(USERNAME, "login-test").assertEvent();
        } finally {
            setPasswordPolicy(null);
        }
    }

    @Test
    public void loginNoTimeoutWithLongWait() {
        loginPage.open();
        setTimeOffset(1700);
        loginPage.login("login-test", "password");
        setTimeOffset(0);
        events.expectLogin().user(LoginTest.userId).detail(USERNAME, "login-test").assertEvent().getSessionId();
    }

    @Test
    public void loginLoginHint() {
        String loginFormUrl = (oauth.getLoginFormUrl()) + "&login_hint=login-test";
        driver.navigate().to(loginFormUrl);
        Assert.assertEquals("login-test", loginPage.getUsername());
        loginPage.login("password");
        Assert.assertEquals(AUTH_RESPONSE, appPage.getRequestType());
        Assert.assertNotNull(oauth.getCurrentQuery().get(CODE));
        events.expectLogin().user(LoginTest.userId).detail(USERNAME, "login-test").assertEvent();
    }

    @Test
    public void loginWithEmailSuccess() {
        loginPage.open();
        loginPage.login("login@test.com", "password");
        Assert.assertEquals(AUTH_RESPONSE, appPage.getRequestType());
        Assert.assertNotNull(oauth.getCurrentQuery().get(CODE));
        events.expectLogin().user(LoginTest.userId).assertEvent();
    }

    @Test
    public void loginWithRememberMe() {
        setRememberMe(true);
        try {
            loginPage.open();
            Assert.assertFalse(loginPage.isRememberMeChecked());
            loginPage.setRememberMe(true);
            Assert.assertTrue(loginPage.isRememberMeChecked());
            loginPage.login("login-test", "password");
            Assert.assertEquals(AUTH_RESPONSE, appPage.getRequestType());
            Assert.assertNotNull(oauth.getCurrentQuery().get(CODE));
            EventRepresentation loginEvent = events.expectLogin().user(LoginTest.userId).detail(USERNAME, "login-test").detail(REMEMBER_ME, "true").assertEvent();
            String sessionId = loginEvent.getSessionId();
            // Expire session
            testingClient.testing().removeUserSession("test", sessionId);
            // Assert rememberMe checked and username/email prefilled
            loginPage.open();
            Assert.assertTrue(loginPage.isRememberMeChecked());
            Assert.assertEquals("login-test", loginPage.getUsername());
            loginPage.setRememberMe(false);
        } finally {
            setRememberMe(false);
        }
    }

    // KEYCLOAK-2741
    @Test
    public void loginAgainWithoutRememberMe() {
        setRememberMe(true);
        try {
            // login with remember me
            loginPage.open();
            Assert.assertFalse(loginPage.isRememberMeChecked());
            loginPage.setRememberMe(true);
            Assert.assertTrue(loginPage.isRememberMeChecked());
            loginPage.login("login-test", "password");
            Assert.assertEquals(AUTH_RESPONSE, appPage.getRequestType());
            Assert.assertNotNull(oauth.getCurrentQuery().get(CODE));
            EventRepresentation loginEvent = events.expectLogin().user(LoginTest.userId).detail(USERNAME, "login-test").detail(REMEMBER_ME, "true").assertEvent();
            String sessionId = loginEvent.getSessionId();
            // Expire session
            testingClient.testing().removeUserSession("test", sessionId);
            // Assert rememberMe checked and username/email prefilled
            loginPage.open();
            Assert.assertTrue(loginPage.isRememberMeChecked());
            Assert.assertEquals("login-test", loginPage.getUsername());
            // login without remember me
            loginPage.setRememberMe(false);
            loginPage.login("login-test", "password");
            // Expire session
            loginEvent = events.expectLogin().user(LoginTest.userId).detail(USERNAME, "login-test").assertEvent();
            sessionId = loginEvent.getSessionId();
            testingClient.testing().removeUserSession("test", sessionId);
            // Assert rememberMe not checked nor username/email prefilled
            loginPage.open();
            Assert.assertFalse(loginPage.isRememberMeChecked());
            Assert.assertNotEquals("login-test", loginPage.getUsername());
        } finally {
            setRememberMe(false);
        }
    }

    // KEYCLOAK-3181
    @Test
    public void loginWithEmailUserAndRememberMe() {
        setRememberMe(true);
        try {
            loginPage.open();
            loginPage.setRememberMe(true);
            Assert.assertTrue(loginPage.isRememberMeChecked());
            loginPage.login("login@test.com", "password");
            Assert.assertEquals(AUTH_RESPONSE, appPage.getRequestType());
            Assert.assertNotNull(oauth.getCurrentQuery().get(CODE));
            EventRepresentation loginEvent = events.expectLogin().user(LoginTest.userId).detail(USERNAME, "login@test.com").detail(REMEMBER_ME, "true").assertEvent();
            String sessionId = loginEvent.getSessionId();
            // Expire session
            testingClient.testing().removeUserSession("test", sessionId);
            // Assert rememberMe checked and username/email prefilled
            loginPage.open();
            Assert.assertTrue(loginPage.isRememberMeChecked());
            Assert.assertEquals("login@test.com", loginPage.getUsername());
            loginPage.setRememberMe(false);
        } finally {
            setRememberMe(false);
        }
    }

    // Login timeout scenarios
    // KEYCLOAK-1037
    @Test
    public void loginExpiredCode() {
        loginPage.open();
        setTimeOffset(5000);
        // No explicitly call "removeExpired". Hence authSession will still exists, but will be expired
        // testingClient.testing().removeExpired("test");
        loginPage.login("login@test.com", "password");
        loginPage.assertCurrent();
        Assert.assertEquals("You took too long to login. Login process starting from beginning.", loginPage.getError());
        setTimeOffset(0);
        events.expectLogin().user(((String) (null))).session(((String) (null))).error(EXPIRED_CODE).clearDetails().assertEvent();
    }

    // KEYCLOAK-1037
    @Test
    public void loginExpiredCodeWithExplicitRemoveExpired() {
        loginPage.open();
        setTimeOffset(5000);
        // Explicitly call "removeExpired". Hence authSession won't exist, but will be restarted from the KC_RESTART
        testingClient.testing().removeExpired("test");
        loginPage.login("login@test.com", "password");
        // loginPage.assertCurrent();
        loginPage.assertCurrent();
        Assert.assertEquals("You took too long to login. Login process starting from beginning.", loginPage.getError());
        setTimeOffset(0);
        events.expectLogin().user(((String) (null))).session(((String) (null))).error(EXPIRED_CODE).clearDetails().detail(RESTART_AFTER_TIMEOUT, "true").client(((String) (null))).assertEvent();
    }

    @Test
    public void loginExpiredCodeAndExpiredCookies() {
        loginPage.open();
        driver.manage().deleteAllCookies();
        // Cookies are expired including KC_RESTART. No way to continue login. Error page must be shown with the "back to application" link
        loginPage.login("login@test.com", "password");
        errorPage.assertCurrent();
        String link = errorPage.getBackToApplicationLink();
        ClientRepresentation thirdParty = ApiUtil.findClientByClientId(adminClient.realm("test"), "third-party").toRepresentation();
        Assert.assertNotNull(link, thirdParty.getBaseUrl());
    }

    @Test
    public void openLoginFormWithDifferentApplication() throws Exception {
        // Login form shown after redirect from admin console
        oauth.clientId(ADMIN_CONSOLE_CLIENT_ID);
        oauth.redirectUri(((AuthServerTestEnricher.getAuthServerContextRoot()) + "/auth/admin/test/console"));
        oauth.openLoginForm();
        // Login form shown after redirect from app
        oauth.clientId("test-app");
        oauth.redirectUri(((OAuthClient.APP_ROOT) + "/auth"));
        oauth.openLoginForm();
        Assert.assertTrue(loginPage.isCurrent());
        loginPage.login("test-user@localhost", "password");
        appPage.assertCurrent();
        events.expectLogin().detail(USERNAME, "test-user@localhost").assertEvent();
    }

    @Test
    public void openLoginFormAfterExpiredCode() throws Exception {
        oauth.openLoginForm();
        setTimeOffset(5000);
        oauth.openLoginForm();
        loginPage.assertCurrent();
        try {
            String loginError = loginPage.getError();
            Assert.fail(("Not expected to have error on loginForm. Error is: " + loginError));
        } catch (NoSuchElementException nsee) {
            // Expected
        }
        loginPage.login("test-user@localhost", "password");
        appPage.assertCurrent();
        events.expectLogin().detail(USERNAME, "test-user@localhost").assertEvent();
    }

    @Test
    public void loginRememberMeExpiredIdle() throws Exception {
        setRememberMe(true, 1, null);
        try {
            // login form shown after redirect from app
            oauth.clientId("test-app");
            oauth.redirectUri(((OAuthClient.APP_ROOT) + "/auth"));
            oauth.openLoginForm();
            Assert.assertTrue(loginPage.isCurrent());
            loginPage.setRememberMe(true);
            loginPage.login("test-user@localhost", "password");
            // sucessful login - app page should be on display.
            events.expectLogin().detail(USERNAME, "test-user@localhost").assertEvent();
            appPage.assertCurrent();
            // expire idle timeout using the timeout window.
            setTimeOffset((2 + (SessionTimeoutHelper.IDLE_TIMEOUT_WINDOW_SECONDS)));
            // trying to open the account page with an expired idle timeout should redirect back to the login page.
            appPage.openAccount();
            loginPage.assertCurrent();
        } finally {
            setRememberMe(false);
        }
    }

    @Test
    public void loginRememberMeExpiredMaxLifespan() throws Exception {
        setRememberMe(true, null, 1);
        try {
            // login form shown after redirect from app
            oauth.clientId("test-app");
            oauth.redirectUri(((OAuthClient.APP_ROOT) + "/auth"));
            oauth.openLoginForm();
            Assert.assertTrue(loginPage.isCurrent());
            loginPage.setRememberMe(true);
            loginPage.login("test-user@localhost", "password");
            // sucessful login - app page should be on display.
            events.expectLogin().detail(USERNAME, "test-user@localhost").assertEvent();
            appPage.assertCurrent();
            // expire the max lifespan.
            setTimeOffset(2);
            // trying to open the account page with an expired lifespan should redirect back to the login page.
            appPage.openAccount();
            loginPage.assertCurrent();
        } finally {
            setRememberMe(false);
        }
    }
}

