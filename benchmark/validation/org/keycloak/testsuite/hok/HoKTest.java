package org.keycloak.testsuite.hok;


import Details.CODE_ID;
import Details.CONSENT;
import Details.CONSENT_VALUE_NO_CONSENT_REQUIRED;
import Details.REDIRECT_URI;
import EventType.LOGIN;
import HttpHeaders.AUTHORIZATION;
import MutualTLSUtils.DEFAULT_KEYSTOREPASSWORD;
import MutualTLSUtils.DEFAULT_KEYSTOREPATH;
import OAuth2Constants.CODE;
import OAuthClient.AuthorizationEndpointResponse;
import OAuthErrorException.INVALID_REQUEST;
import OAuthErrorException.UNAUTHORIZED_CLIENT;
import Status.NO_CONTENT;
import java.io.IOException;
import java.security.KeyStore;
import java.util.Arrays;
import java.util.List;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.hamcrest.Matchers;
import org.hamcrest.org.keycloak.testsuite.util.Matchers;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.keycloak.common.util.KeystoreUtil;
import org.keycloak.jose.jws.JWSInput;
import org.keycloak.protocol.oidc.utils.OIDCResponseType;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.IDToken;
import org.keycloak.representations.RefreshToken;
import org.keycloak.representations.idm.EventRepresentation;
import org.keycloak.representations.oidc.TokenMetadataRepresentation;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.AbstractTestRealmKeycloakTest;
import org.keycloak.testsuite.AssertEvents;
import org.keycloak.testsuite.drone.Different;
import org.keycloak.testsuite.util.ClientManager;
import org.keycloak.testsuite.util.MutualTLSUtils;
import org.keycloak.testsuite.util.OAuthClient;
import org.keycloak.testsuite.util.OAuthClient.AccessTokenResponse;
import org.keycloak.testsuite.util.UserInfoClientUtil;
import org.keycloak.util.JsonSerialization;
import org.openqa.selenium.WebDriver;


public class HoKTest extends AbstractTestRealmKeycloakTest {
    // KEYCLOAK-6771 Certificate Bound Token
    // https://tools.ietf.org/html/draft-ietf-oauth-mtls-08#section-3
    @Drone
    @Different
    protected WebDriver driver2;

    private static final List<String> CLIENT_LIST = Arrays.asList("test-app", "named-test-app");

    public static class HoKAssertEvents extends AssertEvents {
        public HoKAssertEvents(AbstractKeycloakTest ctx) {
            super(ctx);
        }

        private final String defaultRedirectUri = "https://localhost:8543/auth/realms/master/app/auth";

        @Override
        public AssertEvents.ExpectedEvent expectLogin() {
            return // .detail(Details.USERNAME, DEFAULT_USERNAME)
            // .detail(Details.AUTH_METHOD, OIDCLoginProtocol.LOGIN_PROTOCOL)
            // .detail(Details.AUTH_TYPE, AuthorizationEndpoint.CODE_AUTH_TYPE)
            expect(LOGIN).detail(CODE_ID, AssertEvents.isCodeId()).detail(REDIRECT_URI, defaultRedirectUri).detail(CONSENT, CONSENT_VALUE_NO_CONSENT_REQUIRED).session(AssertEvents.isUUID());
        }
    }

    @Rule
    public HoKTest.HoKAssertEvents events = new HoKTest.HoKAssertEvents(this);

    // Authorization Code Flow
    // Bind HoK Token
    @Test
    public void accessTokenRequestWithClientCertificate() throws Exception {
        oauth.doLogin("test-user@localhost", "password");
        EventRepresentation loginEvent = events.expectLogin().assertEvent();
        String sessionId = loginEvent.getSessionId();
        String codeId = loginEvent.getDetails().get(CODE_ID);
        String code = oauth.getCurrentQuery().get(CODE);
        AccessTokenResponse response;
        try (CloseableHttpClient client = MutualTLSUtils.newCloseableHttpClientWithDefaultKeyStoreAndTrustStore()) {
            response = oauth.doAccessTokenRequest(code, "password", client);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        // Success Pattern
        expectSuccessfulResponseFromTokenEndpoint(sessionId, codeId, response);
        verifyHoKTokenDefaultCertThumbPrint(response);
    }

    @Test
    public void accessTokenRequestWithoutClientCertificate() throws Exception {
        oauth.doLogin("test-user@localhost", "password");
        String code = oauth.getCurrentQuery().get(CODE);
        AccessTokenResponse response;
        try (CloseableHttpClient client = MutualTLSUtils.newCloseableHttpClientWithoutKeyStoreAndTrustStore()) {
            response = oauth.doAccessTokenRequest(code, "password", client);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        // Error Pattern
        Assert.assertEquals(400, response.getStatusCode());
        Assert.assertEquals(INVALID_REQUEST, response.getError());
        Assert.assertEquals("Client Certification missing for MTLS HoK Token Binding", response.getErrorDescription());
    }

    // verify HoK Token - Token Refresh
    @Test
    public void refreshTokenRequestByHoKRefreshTokenByOtherClient() throws Exception {
        // first client user login
        oauth.doLogin("test-user@localhost", "password");
        String code = oauth.getCurrentQuery().get(CODE);
        AccessTokenResponse tokenResponse = null;
        try (CloseableHttpClient client = MutualTLSUtils.newCloseableHttpClientWithDefaultKeyStoreAndTrustStore()) {
            tokenResponse = oauth.doAccessTokenRequest(code, "password", client);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        verifyHoKTokenDefaultCertThumbPrint(tokenResponse);
        String refreshTokenString = tokenResponse.getRefreshToken();
        // second client user login
        OAuthClient oauth2 = new OAuthClient();
        oauth2.init(driver2);
        oauth2.doLogin("john-doh@localhost", "password");
        String code2 = oauth2.getCurrentQuery().get(CODE);
        AccessTokenResponse tokenResponse2 = null;
        try (CloseableHttpClient client = MutualTLSUtils.newCloseableHttpClientWithOtherKeyStoreAndTrustStore()) {
            tokenResponse2 = oauth2.doAccessTokenRequest(code2, "password", client);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        verifyHoKTokenOtherCertThumbPrint(tokenResponse2);
        // token refresh by second client by first client's refresh token
        AccessTokenResponse response = null;
        try (CloseableHttpClient client = MutualTLSUtils.newCloseableHttpClientWithOtherKeyStoreAndTrustStore()) {
            response = oauth2.doRefreshTokenRequest(refreshTokenString, "password", client);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        // Error Pattern
        Assert.assertEquals(401, response.getStatusCode());
        Assert.assertEquals(UNAUTHORIZED_CLIENT, response.getError());
        Assert.assertEquals("Client certificate missing, or its thumbprint and one in the refresh token did NOT match", response.getErrorDescription());
    }

    @Test
    public void refreshTokenRequestByHoKRefreshTokenWithClientCertificate() throws Exception {
        oauth.doLogin("test-user@localhost", "password");
        EventRepresentation loginEvent = events.expectLogin().assertEvent();
        String sessionId = loginEvent.getSessionId();
        String codeId = loginEvent.getDetails().get(CODE_ID);
        String code = oauth.getCurrentQuery().get(CODE);
        AccessTokenResponse tokenResponse = null;
        try (CloseableHttpClient client = MutualTLSUtils.newCloseableHttpClientWithDefaultKeyStoreAndTrustStore()) {
            tokenResponse = oauth.doAccessTokenRequest(code, "password", client);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        verifyHoKTokenDefaultCertThumbPrint(tokenResponse);
        AccessToken token = oauth.verifyToken(tokenResponse.getAccessToken());
        String refreshTokenString = tokenResponse.getRefreshToken();
        RefreshToken refreshToken = oauth.parseRefreshToken(refreshTokenString);
        EventRepresentation tokenEvent = events.expectCodeToToken(codeId, sessionId).assertEvent();
        Assert.assertNotNull(refreshTokenString);
        Assert.assertEquals("bearer", tokenResponse.getTokenType());
        Assert.assertThat(((token.getExpiration()) - (getCurrentTime())), Matchers.allOf(Matchers.greaterThanOrEqualTo(200), Matchers.lessThanOrEqualTo(350)));
        int actual = (refreshToken.getExpiration()) - (getCurrentTime());
        Assert.assertThat(actual, Matchers.allOf(Matchers.greaterThanOrEqualTo(1799), Matchers.lessThanOrEqualTo(1800)));
        Assert.assertEquals(sessionId, refreshToken.getSessionState());
        setTimeOffset(2);
        AccessTokenResponse response = null;
        try (CloseableHttpClient client = MutualTLSUtils.newCloseableHttpClientWithDefaultKeyStoreAndTrustStore()) {
            response = oauth.doRefreshTokenRequest(refreshTokenString, "password", client);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        // Success Pattern
        expectSuccessfulResponseFromTokenEndpoint(response, sessionId, token, refreshToken, tokenEvent);
        verifyHoKTokenDefaultCertThumbPrint(response);
    }

    @Test
    public void refreshTokenRequestByRefreshTokenWithoutClientCertificate() throws Exception {
        oauth.doLogin("test-user@localhost", "password");
        EventRepresentation loginEvent = events.expectLogin().assertEvent();
        String sessionId = loginEvent.getSessionId();
        String code = oauth.getCurrentQuery().get(CODE);
        AccessTokenResponse tokenResponse = null;
        tokenResponse = oauth.doAccessTokenRequest(code, "password");
        verifyHoKTokenDefaultCertThumbPrint(tokenResponse);
        AccessToken token = oauth.verifyToken(tokenResponse.getAccessToken());
        String refreshTokenString = tokenResponse.getRefreshToken();
        RefreshToken refreshToken = oauth.parseRefreshToken(refreshTokenString);
        Assert.assertNotNull(refreshTokenString);
        Assert.assertEquals("bearer", tokenResponse.getTokenType());
        Assert.assertThat(((token.getExpiration()) - (getCurrentTime())), Matchers.allOf(Matchers.greaterThanOrEqualTo(200), Matchers.lessThanOrEqualTo(350)));
        int actual = (refreshToken.getExpiration()) - (getCurrentTime());
        Assert.assertThat(actual, Matchers.allOf(Matchers.greaterThanOrEqualTo(1799), Matchers.lessThanOrEqualTo(1800)));
        Assert.assertEquals(sessionId, refreshToken.getSessionState());
        setTimeOffset(2);
        AccessTokenResponse response = null;
        try (CloseableHttpClient client = MutualTLSUtils.newCloseableHttpClientWithoutKeyStoreAndTrustStore()) {
            response = oauth.doRefreshTokenRequest(refreshTokenString, "password", client);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        // Error Pattern
        Assert.assertEquals(401, response.getStatusCode());
        Assert.assertEquals(UNAUTHORIZED_CLIENT, response.getError());
        Assert.assertEquals("Client certificate missing, or its thumbprint and one in the refresh token did NOT match", response.getErrorDescription());
    }

    // verify HoK Token - Get UserInfo
    @Test
    public void getUserInfoByHoKAccessTokenWithClientCertificate() throws Exception {
        // get an access token
        oauth.doLogin("test-user@localhost", "password");
        EventRepresentation loginEvent = events.expectLogin().assertEvent();
        String sessionId = loginEvent.getSessionId();
        String codeId = loginEvent.getDetails().get(CODE_ID);
        String code = oauth.getCurrentQuery().get(CODE);
        AccessTokenResponse tokenResponse = null;
        try (CloseableHttpClient client = MutualTLSUtils.newCloseableHttpClientWithDefaultKeyStoreAndTrustStore()) {
            tokenResponse = oauth.doAccessTokenRequest(code, "password", client);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        verifyHoKTokenDefaultCertThumbPrint(tokenResponse);
        events.expectCodeToToken(codeId, sessionId).assertEvent();
        // execute the access token to get UserInfo with token binded client certificate in mutual authentication TLS
        ClientBuilder clientBuilder = ClientBuilder.newBuilder();
        KeyStore keystore = null;
        keystore = KeystoreUtil.loadKeyStore(DEFAULT_KEYSTOREPATH, DEFAULT_KEYSTOREPASSWORD);
        clientBuilder.keyStore(keystore, DEFAULT_KEYSTOREPASSWORD);
        Client client = clientBuilder.build();
        WebTarget userInfoTarget = null;
        Response response = null;
        try {
            userInfoTarget = UserInfoClientUtil.getUserInfoWebTarget(client);
            response = userInfoTarget.request().header(AUTHORIZATION, ("bearer " + (tokenResponse.getAccessToken()))).get();
            testSuccessfulUserInfoResponse(response);
        } finally {
            response.close();
            client.close();
        }
    }

    @Test
    public void getUserInfoByHoKAccessTokenWithoutClientCertificate() throws Exception {
        // get an access token
        oauth.doLogin("test-user@localhost", "password");
        EventRepresentation loginEvent = events.expectLogin().assertEvent();
        String sessionId = loginEvent.getSessionId();
        String codeId = loginEvent.getDetails().get(CODE_ID);
        String code = oauth.getCurrentQuery().get(CODE);
        AccessTokenResponse tokenResponse = null;
        try (CloseableHttpClient client = MutualTLSUtils.newCloseableHttpClientWithDefaultKeyStoreAndTrustStore()) {
            tokenResponse = oauth.doAccessTokenRequest(code, "password", client);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        verifyHoKTokenDefaultCertThumbPrint(tokenResponse);
        events.expectCodeToToken(codeId, sessionId).assertEvent();
        // execute the access token to get UserInfo without token binded client certificate in mutual authentication TLS
        ClientBuilder clientBuilder = ClientBuilder.newBuilder();
        Client client = clientBuilder.build();
        WebTarget userInfoTarget = null;
        Response response = null;
        try {
            userInfoTarget = UserInfoClientUtil.getUserInfoWebTarget(client);
            response = userInfoTarget.request().header(AUTHORIZATION, ("bearer " + (tokenResponse.getAccessToken()))).get();
            Assert.assertEquals(401, response.getStatus());
        } finally {
            response.close();
            client.close();
        }
    }

    // verify HoK Token - Back Channel Logout
    @Test
    public void postLogoutByHoKRefreshTokenWithClientCertificate() throws Exception {
        String refreshTokenString = execPreProcessPostLogout();
        CloseableHttpResponse response = null;
        try (CloseableHttpClient client = MutualTLSUtils.newCloseableHttpClientWithDefaultKeyStoreAndTrustStore()) {
            response = oauth.doLogout(refreshTokenString, "password", client);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        // Success Pattern
        Assert.assertThat(response, org.keycloak.testsuite.util.Matchers.statusCodeIsHC(NO_CONTENT));
        Assert.assertNotNull(testingClient.testApp().getAdminLogoutAction());
    }

    @Test
    public void postLogoutByHoKRefreshTokenWithoutClientCertificate() throws Exception {
        String refreshTokenString = execPreProcessPostLogout();
        CloseableHttpResponse response = null;
        try (CloseableHttpClient client = MutualTLSUtils.newCloseableHttpClientWithoutKeyStoreAndTrustStore()) {
            response = oauth.doLogout(refreshTokenString, "password", client);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        // Error Pattern
        Assert.assertEquals(401, response.getStatusLine().getStatusCode());
    }

    // Hybrid Code Flow : response_type = code id_token
    // Bind HoK Token
    @Test
    public void accessTokenRequestWithClientCertificateInHybridFlowWithCodeIDToken() throws Exception {
        String nonce = "ckw938gnspa93dj";
        ClientManager.realm(adminClient.realm("test")).clientId("test-app").standardFlow(true).implicitFlow(true);
        oauth.clientId("test-app");
        oauth.responseType((((OIDCResponseType.CODE) + " ") + (OIDCResponseType.ID_TOKEN)));
        oauth.nonce(nonce);
        oauth.doLogin("test-user@localhost", "password");
        EventRepresentation loginEvent = events.expectLogin().assertEvent();
        OAuthClient.AuthorizationEndpointResponse authzResponse = new OAuthClient.AuthorizationEndpointResponse(oauth, true);
        Assert.assertNotNull(authzResponse.getSessionState());
        List<IDToken> idTokens = testAuthzResponseAndRetrieveIDTokens(authzResponse, loginEvent);
        for (IDToken idToken : idTokens) {
            Assert.assertEquals(nonce, idToken.getNonce());
            Assert.assertEquals(authzResponse.getSessionState(), idToken.getSessionState());
        }
    }

    @Test
    public void testIntrospectHoKAccessToken() throws Exception {
        // get an access token with client certificate in mutual authenticate TLS
        // mimic Client
        oauth.doLogin("test-user@localhost", "password");
        String code = oauth.getCurrentQuery().get(CODE);
        EventRepresentation loginEvent = events.expectLogin().assertEvent();
        AccessTokenResponse accessTokenResponse = null;
        try (CloseableHttpClient client = MutualTLSUtils.newCloseableHttpClientWithDefaultKeyStoreAndTrustStore()) {
            accessTokenResponse = oauth.doAccessTokenRequest(code, "password", client);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        // Do token introspection
        // mimic Resource Server
        String tokenResponse;
        try (CloseableHttpClient client = MutualTLSUtils.newCloseableHttpClientWithoutKeyStoreAndTrustStore()) {
            tokenResponse = oauth.introspectTokenWithClientCredential("confidential-cli", "secret1", "access_token", accessTokenResponse.getAccessToken(), client);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        TokenMetadataRepresentation rep = JsonSerialization.readValue(tokenResponse, TokenMetadataRepresentation.class);
        JWSInput jws = new JWSInput(accessTokenResponse.getAccessToken());
        AccessToken at = jws.readJsonContent(AccessToken.class);
        jws = new JWSInput(accessTokenResponse.getRefreshToken());
        RefreshToken rt = jws.readJsonContent(RefreshToken.class);
        String certThumprintFromAccessToken = at.getCertConf().getCertThumbprint();
        String certThumprintFromRefreshToken = rt.getCertConf().getCertThumbprint();
        String certThumprintFromTokenIntrospection = rep.getCertConf().getCertThumbprint();
        String certThumprintFromBoundClientCertificate = MutualTLSUtils.getThumbprintFromDefaultClientCert();
        Assert.assertTrue(rep.isActive());
        Assert.assertEquals("test-user@localhost", rep.getUserName());
        Assert.assertEquals("test-app", rep.getClientId());
        Assert.assertEquals(loginEvent.getUserId(), rep.getSubject());
        Assert.assertEquals(certThumprintFromTokenIntrospection, certThumprintFromBoundClientCertificate);
        Assert.assertEquals(certThumprintFromBoundClientCertificate, certThumprintFromAccessToken);
        Assert.assertEquals(certThumprintFromAccessToken, certThumprintFromRefreshToken);
    }
}

