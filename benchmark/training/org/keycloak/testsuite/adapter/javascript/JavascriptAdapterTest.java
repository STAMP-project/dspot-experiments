package org.keycloak.testsuite.adapter.javascript;


import Details.CODE_ID;
import Details.CONSENT;
import Details.CONSENT_VALUE_CONSENT_GRANTED;
import Details.REDIRECT_URI;
import Details.REVOKED_CLIENT;
import Details.USERNAME;
import EventType.REVOKE_GRANT;
import OAuth2Constants.CODE;
import OAuthClient.AccessTokenResponse;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.common.util.Retry;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.EventRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.Assert;
import org.keycloak.testsuite.AssertEvents;
import org.keycloak.testsuite.ProfileAssume;
import org.keycloak.testsuite.admin.ApiUtil;
import org.keycloak.testsuite.auth.page.account.Applications;
import org.keycloak.testsuite.auth.page.login.OAuthGrant;
import org.keycloak.testsuite.util.JavascriptBrowser;
import org.keycloak.testsuite.util.OAuthClient;
import org.keycloak.testsuite.util.RealmBuilder;
import org.keycloak.testsuite.util.UserBuilder;
import org.keycloak.testsuite.util.WaitUtils;
import org.keycloak.testsuite.util.javascript.JSObjectBuilder;
import org.keycloak.testsuite.util.javascript.JavascriptTestExecutor;
import org.keycloak.testsuite.util.javascript.XMLHttpRequest;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriverException;


/**
 *
 *
 * @author mhajas
 */
public class JavascriptAdapterTest extends AbstractJavascriptTest {
    private String testAppUrl;

    protected JavascriptTestExecutor testExecutor;

    private static int TIME_SKEW_TOLERANCE = 3;

    @Rule
    public AssertEvents events = new AssertEvents(this);

    @Page
    @JavascriptBrowser
    private Applications applicationsPage;

    @Page
    @JavascriptBrowser
    private OAuthGrant oAuthGrantPage;

    @Test
    public void testJSConsoleAuth() {
        testExecutor.init(defaultArguments(), this::assertInitNotAuth).login(this::assertOnLoginPage).loginForm(UserBuilder.create().username("user").password("invalid-password").build(), ( driver1, output, events) -> assertCurrentUrlDoesntStartWith(testAppUrl, driver1)).loginForm(UserBuilder.create().username("invalid-user").password("password").build(), ( driver1, output, events) -> assertCurrentUrlDoesntStartWith(testAppUrl, driver1)).loginForm(AbstractJavascriptTest.testUser, this::assertOnTestAppUrl).init(defaultArguments(), this::assertSuccessfullyLoggedIn).logout(this::assertOnTestAppUrl).init(defaultArguments(), this::assertInitNotAuth);
    }

    @Test
    public void testLoginWithKCLocale() {
        ProfileAssume.assumeCommunity();
        RealmRepresentation testRealmRep = testRealmResource().toRepresentation();
        testRealmRep.setInternationalizationEnabled(true);
        testRealmRep.setDefaultLocale("en");
        testRealmRep.setSupportedLocales(Stream.of("en", "de").collect(Collectors.toSet()));
        testRealmResource().update(testRealmRep);
        testExecutor.init(defaultArguments(), this::assertInitNotAuth).login(this::assertOnLoginPage).loginForm(AbstractJavascriptTest.testUser, this::assertOnTestAppUrl).init(defaultArguments(), this::assertSuccessfullyLoggedIn).login("{kcLocale: 'de'}", assertLocaleIsSet("de")).init(defaultArguments(), this::assertSuccessfullyLoggedIn).login("{kcLocale: 'en'}", assertLocaleIsSet("en"));
    }

    @Test
    public void testRefreshToken() {
        testExecutor.init(defaultArguments(), this::assertInitNotAuth).refreshToken(9999, assertOutputContains("Failed to refresh token")).login(this::assertOnLoginPage).loginForm(AbstractJavascriptTest.testUser, this::assertOnTestAppUrl).init(defaultArguments(), this::assertSuccessfullyLoggedIn).refreshToken(9999, assertEventsContains("Auth Refresh Success"));
    }

    @Test
    public void testRefreshTokenIfUnder30s() {
        // instead of wait move in time
        testExecutor.init(defaultArguments(), this::assertInitNotAuth).login(this::assertOnLoginPage).loginForm(AbstractJavascriptTest.testUser, this::assertOnTestAppUrl).init(defaultArguments(), this::assertSuccessfullyLoggedIn).refreshToken(30, assertOutputContains("Token not refreshed, valid for")).addTimeSkew((-5)).refreshToken(30, assertEventsContains("Auth Refresh Success"));
    }

    @Test
    public void testGetProfile() {
        testExecutor.init(defaultArguments(), this::assertInitNotAuth).getProfile(assertOutputContains("Failed to load profile")).login(this::assertOnLoginPage).loginForm(AbstractJavascriptTest.testUser, this::assertOnTestAppUrl).init(defaultArguments(), this::assertSuccessfullyLoggedIn).getProfile(( driver1, output, events) -> Assert.assertThat(((Map<String, String>) (output)), hasEntry("username", AbstractJavascriptTest.testUser.getUsername())));
    }

    @Test
    public void grantBrowserBasedApp() {
        Assume.assumeTrue("This test doesn't work with phantomjs", (!("phantomjs".equals(System.getProperty("js.browser")))));
        ClientResource clientResource = ApiUtil.findClientResourceByClientId(adminClient.realm(AbstractJavascriptTest.REALM_NAME), AbstractJavascriptTest.CLIENT_ID);
        ClientRepresentation client = clientResource.toRepresentation();
        try {
            client.setConsentRequired(true);
            clientResource.update(client);
            // I am not sure why is this driver1 argument to isCurrent necessary, but I got exception without it
            testExecutor.init(defaultArguments(), this::assertInitNotAuth).login(this::assertOnLoginPage).loginForm(AbstractJavascriptTest.testUser, ( driver1, output, events) -> assertTrue(oAuthGrantPage.isCurrent(driver1)));
            oAuthGrantPage.accept();
            EventRepresentation loginEvent = events.expectLogin().client(AbstractJavascriptTest.CLIENT_ID).detail(CONSENT, CONSENT_VALUE_CONSENT_GRANTED).detail(REDIRECT_URI, testAppUrl).detail(USERNAME, AbstractJavascriptTest.testUser.getUsername()).assertEvent();
            String codeId = loginEvent.getDetails().get(CODE_ID);
            testExecutor.init(defaultArguments(), this::assertSuccessfullyLoggedIn);
            applicationsPage.navigateTo();
            events.expectCodeToToken(codeId, loginEvent.getSessionId()).client(AbstractJavascriptTest.CLIENT_ID).assertEvent();
            applicationsPage.revokeGrantForApplication(AbstractJavascriptTest.CLIENT_ID);
            events.expect(REVOKE_GRANT).client("account").detail(REVOKED_CLIENT, AbstractJavascriptTest.CLIENT_ID).assertEvent();
            jsDriver.navigate().to(testAppUrl);
            // need to configure because we refreshed page
            testExecutor.configure().init(defaultArguments(), this::assertInitNotAuth).login(( driver1, output, events) -> assertTrue(oAuthGrantPage.isCurrent(driver1)));
        } finally {
            // Clean
            client.setConsentRequired(false);
            clientResource.update(client);
        }
    }

    @Test
    public void implicitFlowTest() {
        testExecutor.init(defaultArguments().implicitFlow(), this::assertInitNotAuth).login(this::assertOnTestAppUrl).errorResponse(assertOutputContains("Implicit flow is disabled for the client"));
        setImplicitFlowForClient();
        jsDriver.navigate().to(testAppUrl);
        testExecutor.init(defaultArguments(), this::assertInitNotAuth).login(this::assertOnTestAppUrl).errorResponse(assertOutputContains("Standard flow is disabled for the client"));
        jsDriver.navigate().to(testAppUrl);
        testExecutor.init(defaultArguments().implicitFlow(), this::assertInitNotAuth).login(this::assertOnLoginPage).loginForm(AbstractJavascriptTest.testUser, this::assertOnTestAppUrl).init(defaultArguments().implicitFlow(), this::assertSuccessfullyLoggedIn);
    }

    @Test
    public void testCertEndpoint() {
        testExecutor.logInAndInit(defaultArguments(), AbstractJavascriptTest.testUser, this::assertSuccessfullyLoggedIn).sendXMLHttpRequest(XMLHttpRequest.create().url(((((authServerContextRootPage) + "/auth/realms/") + (AbstractJavascriptTest.REALM_NAME)) + "/protocol/openid-connect/certs")).method("GET").addHeader("Accept", "application/json").addHeader("Authorization", "Bearer ' + keycloak.token + '"), assertResponseStatus(200));
    }

    @Test
    public void implicitFlowQueryTest() {
        setImplicitFlowForClient();
        testExecutor.init(JSObjectBuilder.create().implicitFlow().queryResponse(), this::assertInitNotAuth).login(( driver1, output, events1) -> Retry.execute(() -> assertThat(driver1.getCurrentUrl(), containsString("Response_mode+%27query%27+not+allowed")), 20, 50));
    }

    @Test
    public void implicitFlowRefreshTokenTest() {
        setImplicitFlowForClient();
        testExecutor.logInAndInit(defaultArguments().implicitFlow(), AbstractJavascriptTest.testUser, this::assertSuccessfullyLoggedIn).refreshToken(9999, assertOutputContains("Failed to refresh token"));
    }

    @Test
    public void implicitFlowOnTokenExpireTest() {
        RealmRepresentation realm = adminClient.realms().realm(AbstractJavascriptTest.REALM_NAME).toRepresentation();
        Integer storeAccesTokenLifespan = realm.getAccessTokenLifespanForImplicitFlow();
        try {
            realm.setAccessTokenLifespanForImplicitFlow(5);
            adminClient.realms().realm(AbstractJavascriptTest.REALM_NAME).update(realm);
            setImplicitFlowForClient();
            testExecutor.logInAndInit(defaultArguments().implicitFlow(), AbstractJavascriptTest.testUser, this::assertSuccessfullyLoggedIn).addTimeSkew((-5));// Move in time instead of wait

            WaitUtils.waitUntilElement(eventsArea).text().contains("Access token expired");
        } finally {
            // Get to origin state
            realm.setAccessTokenLifespanForImplicitFlow(storeAccesTokenLifespan);
            adminClient.realms().realm(AbstractJavascriptTest.REALM_NAME).update(realm);
        }
    }

    @Test
    public void implicitFlowCertEndpoint() {
        setImplicitFlowForClient();
        testExecutor.logInAndInit(defaultArguments().implicitFlow(), AbstractJavascriptTest.testUser, this::assertSuccessfullyLoggedIn).sendXMLHttpRequest(XMLHttpRequest.create().url(((((authServerContextRootPage) + "/auth/realms/") + (AbstractJavascriptTest.REALM_NAME)) + "/protocol/openid-connect/certs")).method("GET").addHeader("Accept", "application/json").addHeader("Authorization", "Bearer ' + keycloak.token + '"), assertResponseStatus(200));
    }

    @Test
    public void testBearerRequest() {
        XMLHttpRequest request = XMLHttpRequest.create().url(((((authServerContextRootPage) + "/auth/admin/realms/") + (AbstractJavascriptTest.REALM_NAME)) + "/roles")).method("GET").addHeader("Accept", "application/json").addHeader("Authorization", "Bearer ' + keycloak.token + '");
        testExecutor.init(defaultArguments()).sendXMLHttpRequest(request, assertResponseStatus(401)).refresh();
        if (!("phantomjs".equals(System.getProperty("js.browser")))) {
            // I have no idea why, but this request doesn't work with phantomjs, it works in chrome
            testExecutor.logInAndInit(defaultArguments(), AbstractJavascriptTest.unauthorizedUser, this::assertSuccessfullyLoggedIn).sendXMLHttpRequest(request, ( output) -> Assert.assertThat(output, hasEntry("status", 403L))).logout(this::assertOnTestAppUrl).refresh();
        }
        testExecutor.logInAndInit(defaultArguments(), AbstractJavascriptTest.testUser, this::assertSuccessfullyLoggedIn).sendXMLHttpRequest(request, assertResponseStatus(200));
    }

    @Test
    public void loginRequiredAction() {
        try {
            testExecutor.init(defaultArguments().loginRequiredOnLoad());
            // This throws exception because when JavascriptExecutor waits for AsyncScript to finish
            // it is redirected to login page and executor gets no response
            throw new RuntimeException("Probably the login-required OnLoad mode doesn't work, because testExecutor should fail with error that page was redirected.");
        } catch (WebDriverException ex) {
            // should happen
        }
        testExecutor.loginForm(AbstractJavascriptTest.testUser, this::assertOnTestAppUrl).init(defaultArguments(), this::assertSuccessfullyLoggedIn);
    }

    @Test
    public void testUpdateToken() {
        XMLHttpRequest request = XMLHttpRequest.create().url(((((authServerContextRootPage) + "/auth/admin/realms/") + (AbstractJavascriptTest.REALM_NAME)) + "/roles")).method("GET").addHeader("Accept", "application/json").addHeader("Authorization", "Bearer ' + keycloak.token + '");
        testExecutor.logInAndInit(defaultArguments(), AbstractJavascriptTest.testUser, this::assertSuccessfullyLoggedIn).addTimeSkew((-33));
        setTimeOffset(33);
        testExecutor.refreshToken(5, assertEventsContains("Auth Refresh Success"));
        setTimeOffset(67);
        testExecutor.addTimeSkew((-34)).sendXMLHttpRequest(request, assertResponseStatus(401)).refreshToken(5, assertEventsContains("Auth Refresh Success")).sendXMLHttpRequest(request, assertResponseStatus(200));
    }

    @Test
    public void timeSkewTest() {
        testExecutor.logInAndInit(defaultArguments(), AbstractJavascriptTest.testUser, this::assertSuccessfullyLoggedIn).checkTimeSkew(( driver1, output, events) -> assertThat(toIntExact(((long) (output))), is(both(greaterThan((0 - (JavascriptAdapterTest.TIME_SKEW_TOLERANCE)))).and(lessThan(JavascriptAdapterTest.TIME_SKEW_TOLERANCE)))));
        setTimeOffset(40);
        testExecutor.refreshToken(9999, assertEventsContains("Auth Refresh Success")).checkTimeSkew(( driver1, output, events) -> assertThat(toIntExact(((long) (output))), is(both(greaterThan(((-40) - (JavascriptAdapterTest.TIME_SKEW_TOLERANCE)))).and(lessThan(((-40) + (JavascriptAdapterTest.TIME_SKEW_TOLERANCE)))))));
    }

    @Test
    public void testOneSecondTimeSkewTokenUpdate() {
        setTimeOffset(1);
        testExecutor.logInAndInit(defaultArguments(), AbstractJavascriptTest.testUser, this::assertSuccessfullyLoggedIn).refreshToken(9999, assertEventsContains("Auth Refresh Success"));
        try {
            // The events element should contain "Auth logout" but we need to wait for it
            // and text().not().contains() doesn't wait. With KEYCLOAK-4179 it took some time for "Auth Logout" to be present
            WaitUtils.waitUntilElement(eventsArea).text().contains("Auth Logout");
            throw new RuntimeException("The events element shouldn\'t contain \"Auth Logout\" text");
        } catch (TimeoutException e) {
            // OK
        }
    }

    @Test
    public void testLocationHeaderInResponse() {
        XMLHttpRequest request = XMLHttpRequest.create().url(((((authServerContextRootPage) + "/auth/admin/realms/") + (AbstractJavascriptTest.REALM_NAME)) + "/users")).method("POST").content("JSON.stringify(JSON.parse(\'{\"emailVerified\" : false, \"enabled\" : true, \"username\": \"mhajas\", \"firstName\" :\"First\", \"lastName\":\"Last\",\"email\":\"email@redhat.com\", \"attributes\": {}}\'))").addHeader("Accept", "application/json").addHeader("Authorization", "Bearer ' + keycloak.token + '").addHeader("Content-Type", "application/json; charset=UTF-8");
        testExecutor.logInAndInit(defaultArguments(), AbstractJavascriptTest.testUser, this::assertSuccessfullyLoggedIn).sendXMLHttpRequest(request, ( response) -> {
            List<UserRepresentation> users = adminClient.realm(AbstractJavascriptTest.REALM_NAME).users().search("mhajas", 0, 1);
            assertEquals("There should be created user mhajas", 1, users.size());
            assertThat(((String) (response.get("responseHeaders"))).toLowerCase(), containsString(((((("location: " + (authServerContextRootPage.toString())) + "/auth/admin/realms/") + (AbstractJavascriptTest.REALM_NAME)) + "/users/") + (users.get(0).getId()))));
        });
    }

    @Test
    public void spaceInRealmNameTest() {
        // Unfortunately this test doesn't work on phantomjs
        // it looks like phantomjs double encode %20 => %25%20
        Assume.assumeTrue("This test doesn't work with phantomjs", (!("phantomjs".equals(System.getProperty("js.browser")))));
        try {
            adminClient.realm(AbstractJavascriptTest.REALM_NAME).update(RealmBuilder.edit(adminClient.realm(AbstractJavascriptTest.REALM_NAME).toRepresentation()).name(AbstractJavascriptTest.SPACE_REALM_NAME).build());
            JSObjectBuilder configuration = JSObjectBuilder.create().add("url", ((authServerContextRootPage) + "/auth")).add("realm", AbstractJavascriptTest.SPACE_REALM_NAME).add("clientId", AbstractJavascriptTest.CLIENT_ID);
            testAppUrl = ((authServerContextRootPage) + (AbstractJavascriptTest.JAVASCRIPT_ENCODED_SPACE_URL)) + "/index.html";
            jsDriver.navigate().to(testAppUrl);
            jsDriverTestRealmLoginPage.setAuthRealm(AbstractJavascriptTest.SPACE_REALM_NAME);
            testExecutor.configure(configuration).init(defaultArguments(), this::assertInitNotAuth).login(this::assertOnLoginPage).loginForm(AbstractJavascriptTest.testUser, this::assertOnTestAppUrl).configure(configuration).init(defaultArguments(), this::assertSuccessfullyLoggedIn);
        } finally {
            adminClient.realm(AbstractJavascriptTest.SPACE_REALM_NAME).update(RealmBuilder.edit(adminClient.realm(AbstractJavascriptTest.SPACE_REALM_NAME).toRepresentation()).name(AbstractJavascriptTest.REALM_NAME).build());
            jsDriverTestRealmLoginPage.setAuthRealm(AbstractJavascriptTest.REALM_NAME);
        }
    }

    @Test
    public void initializeWithTokenTest() {
        oauth.setDriver(jsDriver);
        oauth.realm(AbstractJavascriptTest.REALM_NAME);
        oauth.clientId(AbstractJavascriptTest.CLIENT_ID);
        oauth.redirectUri(testAppUrl);
        oauth.doLogin(AbstractJavascriptTest.testUser);
        String code = oauth.getCurrentQuery().get(CODE);
        OAuthClient.AccessTokenResponse tokenResponse = oauth.doAccessTokenRequest(code, "password");
        String token = tokenResponse.getAccessToken();
        String refreshToken = tokenResponse.getRefreshToken();
        testExecutor.init(JSObjectBuilder.create().add("token", token).add("refreshToken", refreshToken), this::assertSuccessfullyLoggedIn).refreshToken(9999, assertEventsContains("Auth Refresh Success"));
    }

    @Test
    public void initializeWithTimeSkew() {
        oauth.setDriver(jsDriver);// Oauth need to login with jsDriver

        // Get access token and refresh token to initialize with
        setTimeOffset(600);
        oauth.realm(AbstractJavascriptTest.REALM_NAME);
        oauth.clientId(AbstractJavascriptTest.CLIENT_ID);
        oauth.redirectUri(testAppUrl);
        oauth.doLogin(AbstractJavascriptTest.testUser);
        String code = oauth.getCurrentQuery().get(CODE);
        OAuthClient.AccessTokenResponse tokenResponse = oauth.doAccessTokenRequest(code, "password");
        String token = tokenResponse.getAccessToken();
        String refreshToken = tokenResponse.getRefreshToken();
        // Perform test
        testExecutor.init(JSObjectBuilder.create().add("token", token).add("refreshToken", refreshToken).add("timeSkew", (-600)), this::assertSuccessfullyLoggedIn).checkTimeSkew(( driver1, output, events) -> assertThat(((Long) (output)), is(both(greaterThan(((-600L) - (JavascriptAdapterTest.TIME_SKEW_TOLERANCE)))).and(lessThan(((-600L) + (JavascriptAdapterTest.TIME_SKEW_TOLERANCE))))))).refreshToken(9999, assertEventsContains("Auth Refresh Success")).checkTimeSkew(( driver1, output, events) -> assertThat(((Long) (output)), is(both(greaterThan(((-600L) - (JavascriptAdapterTest.TIME_SKEW_TOLERANCE)))).and(lessThan(((-600L) + (JavascriptAdapterTest.TIME_SKEW_TOLERANCE)))))));
    }

    // KEYCLOAK-4503
    @Test
    public void initializeWithRefreshToken() {
        oauth.setDriver(jsDriver);// Oauth need to login with jsDriver

        oauth.realm(AbstractJavascriptTest.REALM_NAME);
        oauth.clientId(AbstractJavascriptTest.CLIENT_ID);
        oauth.redirectUri(testAppUrl);
        oauth.doLogin(AbstractJavascriptTest.testUser);
        String code = oauth.getCurrentQuery().get(CODE);
        OAuthClient.AccessTokenResponse tokenResponse = oauth.doAccessTokenRequest(code, "password");
        String token = tokenResponse.getAccessToken();
        String refreshToken = tokenResponse.getRefreshToken();
        testExecutor.init(JSObjectBuilder.create().add("refreshToken", refreshToken), ( driver1, output, events) -> {
            assertInitNotAuth(driver1, output, events);
            waitUntilElement(events).text().not().contains("Auth Success");
        });
    }

    @Test
    public void reentrancyCallbackTest() {
        testExecutor.logInAndInit(defaultArguments(), AbstractJavascriptTest.testUser, this::assertSuccessfullyLoggedIn).executeAsyncScript(("var callback = arguments[arguments.length - 1];" + ((((((("keycloak.updateToken(60).success(function () {" + "       event(\"First callback\");") + "       keycloak.updateToken(60).success(function () {") + "          event(\"Second callback\");") + "          callback(\"Success\");") + "       });") + "    }") + ");")), ( driver1, output, events) -> {
            waitUntilElement(events).text().contains("First callback");
            waitUntilElement(events).text().contains("Second callback");
            waitUntilElement(events).text().not().contains("Auth Logout");
        });
    }

    @Test
    public void fragmentInURLTest() {
        jsDriver.navigate().to(((testAppUrl) + "#fragmentPart"));
        testExecutor.init(defaultArguments(), this::assertInitNotAuth).login(this::assertOnLoginPage).loginForm(AbstractJavascriptTest.testUser, this::assertOnTestAppUrl).init(defaultArguments(), ( driver1, output, events1) -> {
            assertSuccessfullyLoggedIn(driver1, output, events1);
            assertThat(driver1.getCurrentUrl(), containsString("#fragmentPart"));
        });
    }

    @Test
    public void fragmentInLoginFunction() {
        testExecutor.init(defaultArguments(), this::assertInitNotAuth).login(JSObjectBuilder.create().add("redirectUri", ((testAppUrl) + "#fragmentPart")).build(), this::assertOnLoginPage).loginForm(AbstractJavascriptTest.testUser, this::assertOnTestAppUrl).init(defaultArguments(), ( driver1, output, events1) -> {
            assertSuccessfullyLoggedIn(driver1, output, events1);
            assertThat(driver1.getCurrentUrl(), containsString("#fragmentPart"));
        });
    }
}

