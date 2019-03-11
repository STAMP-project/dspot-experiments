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
package org.keycloak.testsuite.oauth;


import AuthenticationFlowError.CLIENT_CREDENTIALS_SETUP_REQUIRED;
import Details.CLIENT_AUTH_METHOD;
import Details.CODE_ID;
import Details.CONSENT;
import Details.GRANT_TYPE;
import Details.REDIRECT_URI;
import Details.REFRESH_TOKEN_ID;
import Details.TOKEN_ID;
import Details.UPDATED_REFRESH_TOKEN_ID;
import Details.USERNAME;
import Errors.CLIENT_DISABLED;
import Errors.INVALID_CLIENT_CREDENTIALS;
import Errors.INVALID_TOKEN;
import JWTClientAuthenticator.CERTIFICATE_ATTR;
import JWTClientAuthenticator.PROVIDER_ID;
import OAuth2Constants.CODE;
import OAuth2Constants.PASSWORD;
import OAuthClient.AccessTokenResponse;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.keycloak.OAuth2Constants;
import org.keycloak.adapters.AdapterUtils;
import org.keycloak.adapters.authentication.JWTClientCredentialsProvider;
import org.keycloak.common.constants.ServiceAccountConstants;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.JsonWebToken;
import org.keycloak.representations.RefreshToken;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.EventRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.services.resources.admin.ClientAttributeCertificateResource.CERTIFICATE_PEM;
import org.keycloak.services.resources.admin.ClientAttributeCertificateResource.JSON_WEB_KEY_SET;
import org.keycloak.services.resources.admin.ClientAttributeCertificateResource.PUBLIC_KEY_PEM;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.AssertEvents;
import org.keycloak.testsuite.admin.ApiUtil;
import org.keycloak.testsuite.util.ClientManager;
import org.keycloak.testsuite.util.OAuthClient;


/**
 *
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 * @author Vaclav Muzikar <vmuzikar@redhat.com>
 */
public class ClientAuthSignedJWTTest extends AbstractKeycloakTest {
    @Rule
    public AssertEvents events = new AssertEvents(this);

    private static String client1SAUserId;

    private static RealmRepresentation testRealm;

    private static ClientRepresentation app1;

    private static ClientRepresentation app2;

    private static ClientRepresentation app3;

    private static UserRepresentation defaultUser;

    private static UserRepresentation serviceAccountUser;

    // TEST SUCCESS
    @Test
    public void testServiceAccountAndLogoutSuccess() throws Exception {
        String client1Jwt = getClient1SignedJWT();
        OAuthClient.AccessTokenResponse response = doClientCredentialsGrantRequest(client1Jwt);
        Assert.assertEquals(200, response.getStatusCode());
        AccessToken accessToken = oauth.verifyToken(response.getAccessToken());
        RefreshToken refreshToken = oauth.parseRefreshToken(response.getRefreshToken());
        events.expectClientLogin().client("client1").user(ClientAuthSignedJWTTest.client1SAUserId).session(accessToken.getSessionState()).detail(TOKEN_ID, accessToken.getId()).detail(REFRESH_TOKEN_ID, refreshToken.getId()).detail(USERNAME, ((ServiceAccountConstants.SERVICE_ACCOUNT_USER_PREFIX) + "client1")).detail(CLIENT_AUTH_METHOD, PROVIDER_ID).assertEvent();
        Assert.assertEquals(accessToken.getSessionState(), refreshToken.getSessionState());
        client1Jwt = getClient1SignedJWT();
        OAuthClient.AccessTokenResponse refreshedResponse = doRefreshTokenRequest(response.getRefreshToken(), client1Jwt);
        AccessToken refreshedAccessToken = oauth.verifyToken(refreshedResponse.getAccessToken());
        RefreshToken refreshedRefreshToken = oauth.parseRefreshToken(refreshedResponse.getRefreshToken());
        Assert.assertEquals(accessToken.getSessionState(), refreshedAccessToken.getSessionState());
        Assert.assertEquals(accessToken.getSessionState(), refreshedRefreshToken.getSessionState());
        events.expectRefresh(refreshToken.getId(), refreshToken.getSessionState()).user(ClientAuthSignedJWTTest.client1SAUserId).client("client1").detail(CLIENT_AUTH_METHOD, PROVIDER_ID).assertEvent();
        // Logout and assert refresh will fail
        HttpResponse logoutResponse = doLogout(response.getRefreshToken(), getClient1SignedJWT());
        Assert.assertEquals(204, logoutResponse.getStatusLine().getStatusCode());
        events.expectLogout(accessToken.getSessionState()).client("client1").user(ClientAuthSignedJWTTest.client1SAUserId).removeDetail(REDIRECT_URI).detail(CLIENT_AUTH_METHOD, PROVIDER_ID).assertEvent();
        response = doRefreshTokenRequest(response.getRefreshToken(), getClient1SignedJWT());
        Assert.assertEquals(400, response.getStatusCode());
        Assert.assertEquals("invalid_grant", response.getError());
        events.expectRefresh(refreshToken.getId(), refreshToken.getSessionState()).client("client1").user(ClientAuthSignedJWTTest.client1SAUserId).removeDetail(TOKEN_ID).removeDetail(UPDATED_REFRESH_TOKEN_ID).detail(CLIENT_AUTH_METHOD, PROVIDER_ID).error(INVALID_TOKEN).assertEvent();
    }

    @Test
    public void testCodeToTokenRequestSuccess() throws Exception {
        oauth.clientId("client2");
        oauth.doLogin("test-user@localhost", "password");
        EventRepresentation loginEvent = events.expectLogin().client("client2").assertEvent();
        String code = oauth.getCurrentQuery().get(CODE);
        OAuthClient.AccessTokenResponse response = doAccessTokenRequest(code, getClient2SignedJWT());
        Assert.assertEquals(200, response.getStatusCode());
        oauth.verifyToken(response.getAccessToken());
        oauth.parseRefreshToken(response.getRefreshToken());
        events.expectCodeToToken(loginEvent.getDetails().get(CODE_ID), loginEvent.getSessionId()).client("client2").detail(CLIENT_AUTH_METHOD, PROVIDER_ID).assertEvent();
    }

    @Test
    public void testDirectGrantRequestSuccess() throws Exception {
        oauth.clientId("client2");
        OAuthClient.AccessTokenResponse response = doGrantAccessTokenRequest("test-user@localhost", "password", getClient2SignedJWT());
        Assert.assertEquals(200, response.getStatusCode());
        AccessToken accessToken = oauth.verifyToken(response.getAccessToken());
        RefreshToken refreshToken = oauth.parseRefreshToken(response.getRefreshToken());
        events.expectLogin().client("client2").session(accessToken.getSessionState()).detail(GRANT_TYPE, PASSWORD).detail(TOKEN_ID, accessToken.getId()).detail(REFRESH_TOKEN_ID, refreshToken.getId()).detail(USERNAME, "test-user@localhost").detail(CLIENT_AUTH_METHOD, PROVIDER_ID).removeDetail(CODE_ID).removeDetail(REDIRECT_URI).removeDetail(CONSENT).assertEvent();
    }

    @Test
    public void testClientWithGeneratedKeysJKS() throws Exception {
        testClientWithGeneratedKeys("JKS");
    }

    @Test
    public void testClientWithGeneratedKeysPKCS12() throws Exception {
        testClientWithGeneratedKeys("PKCS12");
    }

    @Test
    public void testUploadKeystoreJKS() throws Exception {
        testUploadKeystore("JKS", "client-auth-test/keystore-client1.jks", "clientkey", "storepass");
    }

    @Test
    public void testUploadKeystorePKCS12() throws Exception {
        testUploadKeystore("PKCS12", "client-auth-test/keystore-client2.p12", "clientkey", "pwd2");
    }

    @Test
    public void testUploadCertificatePEM() throws Exception {
        testUploadKeystore(CERTIFICATE_PEM, "client-auth-test/certificate.pem", "undefined", "undefined");
    }

    @Test
    public void testUploadPublicKeyPEM() throws Exception {
        testUploadKeystore(PUBLIC_KEY_PEM, "client-auth-test/publickey.pem", "undefined", "undefined");
    }

    @Test
    public void testUploadJWKS() throws Exception {
        testUploadKeystore(JSON_WEB_KEY_SET, "clientreg-test/jwks.json", "undefined", "undefined");
    }

    // TEST ERRORS
    @Test
    public void testMissingClientAssertionType() throws Exception {
        List<NameValuePair> parameters = new LinkedList<NameValuePair>();
        parameters.add(new BasicNameValuePair(OAuth2Constants.GRANT_TYPE, OAuth2Constants.CLIENT_CREDENTIALS));
        CloseableHttpResponse resp = sendRequest(oauth.getServiceAccountUrl(), parameters);
        OAuthClient.AccessTokenResponse response = new OAuthClient.AccessTokenResponse(resp);
        assertError(response, null, "unauthorized_client", INVALID_CLIENT_CREDENTIALS);
    }

    @Test
    public void testInvalidClientAssertionType() throws Exception {
        List<NameValuePair> parameters = new LinkedList<NameValuePair>();
        parameters.add(new BasicNameValuePair(OAuth2Constants.GRANT_TYPE, OAuth2Constants.CLIENT_CREDENTIALS));
        parameters.add(new BasicNameValuePair(OAuth2Constants.CLIENT_ASSERTION_TYPE, "invalid"));
        CloseableHttpResponse resp = sendRequest(oauth.getServiceAccountUrl(), parameters);
        OAuthClient.AccessTokenResponse response = new OAuthClient.AccessTokenResponse(resp);
        assertError(response, null, "unauthorized_client", INVALID_CLIENT_CREDENTIALS);
    }

    @Test
    public void testMissingClientAssertion() throws Exception {
        List<NameValuePair> parameters = new LinkedList<NameValuePair>();
        parameters.add(new BasicNameValuePair(OAuth2Constants.GRANT_TYPE, OAuth2Constants.CLIENT_CREDENTIALS));
        parameters.add(new BasicNameValuePair(OAuth2Constants.CLIENT_ASSERTION_TYPE, OAuth2Constants.CLIENT_ASSERTION_TYPE_JWT));
        CloseableHttpResponse resp = sendRequest(oauth.getServiceAccountUrl(), parameters);
        OAuthClient.AccessTokenResponse response = new OAuthClient.AccessTokenResponse(resp);
        assertError(response, null, "unauthorized_client", INVALID_CLIENT_CREDENTIALS);
    }

    @Test
    public void testAssertionMissingIssuer() throws Exception {
        String invalidJwt = getClientSignedJWT(getClient1KeyPair(), null);
        List<NameValuePair> parameters = new LinkedList<NameValuePair>();
        parameters.add(new BasicNameValuePair(OAuth2Constants.GRANT_TYPE, OAuth2Constants.CLIENT_CREDENTIALS));
        parameters.add(new BasicNameValuePair(OAuth2Constants.CLIENT_ASSERTION_TYPE, OAuth2Constants.CLIENT_ASSERTION_TYPE_JWT));
        parameters.add(new BasicNameValuePair(OAuth2Constants.CLIENT_ASSERTION, invalidJwt));
        CloseableHttpResponse resp = sendRequest(oauth.getServiceAccountUrl(), parameters);
        OAuthClient.AccessTokenResponse response = new OAuthClient.AccessTokenResponse(resp);
        assertError(response, null, "unauthorized_client", INVALID_CLIENT_CREDENTIALS);
    }

    @Test
    public void testAssertionUnknownClient() throws Exception {
        String invalidJwt = getClientSignedJWT(getClient1KeyPair(), "unknown-client");
        List<NameValuePair> parameters = new LinkedList<NameValuePair>();
        parameters.add(new BasicNameValuePair(OAuth2Constants.GRANT_TYPE, OAuth2Constants.CLIENT_CREDENTIALS));
        parameters.add(new BasicNameValuePair(OAuth2Constants.CLIENT_ASSERTION_TYPE, OAuth2Constants.CLIENT_ASSERTION_TYPE_JWT));
        parameters.add(new BasicNameValuePair(OAuth2Constants.CLIENT_ASSERTION, invalidJwt));
        CloseableHttpResponse resp = sendRequest(oauth.getServiceAccountUrl(), parameters);
        OAuthClient.AccessTokenResponse response = new OAuthClient.AccessTokenResponse(resp);
        assertError(response, "unknown-client", "unauthorized_client", INVALID_CLIENT_CREDENTIALS);
    }

    @Test
    public void testAssertionDisabledClient() throws Exception {
        ClientManager.realm(adminClient.realm("test")).clientId("client1").enabled(false);
        String invalidJwt = getClient1SignedJWT();
        List<NameValuePair> parameters = new LinkedList<NameValuePair>();
        parameters.add(new BasicNameValuePair(OAuth2Constants.GRANT_TYPE, OAuth2Constants.CLIENT_CREDENTIALS));
        parameters.add(new BasicNameValuePair(OAuth2Constants.CLIENT_ASSERTION_TYPE, OAuth2Constants.CLIENT_ASSERTION_TYPE_JWT));
        parameters.add(new BasicNameValuePair(OAuth2Constants.CLIENT_ASSERTION, invalidJwt));
        CloseableHttpResponse resp = sendRequest(oauth.getServiceAccountUrl(), parameters);
        OAuthClient.AccessTokenResponse response = new OAuthClient.AccessTokenResponse(resp);
        assertError(response, "client1", "unauthorized_client", CLIENT_DISABLED);
        ClientManager.realm(adminClient.realm("test")).clientId("client1").enabled(true);
    }

    @Test
    public void testAssertionUnconfiguredClientCertificate() throws Exception {
        class CertificateHolder {
            String certificate;
        }
        final CertificateHolder backupClient1Cert = new CertificateHolder();
        backupClient1Cert.certificate = ApiUtil.findClientByClientId(adminClient.realm("test"), "client1").toRepresentation().getAttributes().get(CERTIFICATE_ATTR);
        ClientManager.realm(adminClient.realm("test")).clientId("client1").updateAttribute(CERTIFICATE_ATTR, null);
        String invalidJwt = getClient1SignedJWT();
        List<NameValuePair> parameters = new LinkedList<NameValuePair>();
        parameters.add(new BasicNameValuePair(OAuth2Constants.GRANT_TYPE, OAuth2Constants.CLIENT_CREDENTIALS));
        parameters.add(new BasicNameValuePair(OAuth2Constants.CLIENT_ASSERTION_TYPE, OAuth2Constants.CLIENT_ASSERTION_TYPE_JWT));
        parameters.add(new BasicNameValuePair(OAuth2Constants.CLIENT_ASSERTION, invalidJwt));
        CloseableHttpResponse resp = sendRequest(oauth.getServiceAccountUrl(), parameters);
        OAuthClient.AccessTokenResponse response = new OAuthClient.AccessTokenResponse(resp);
        assertError(response, "client1", "unauthorized_client", "client_credentials_setup_required");
        ClientManager.realm(adminClient.realm("test")).clientId("client1").updateAttribute(CERTIFICATE_ATTR, backupClient1Cert.certificate);
    }

    @Test
    public void testAssertionInvalidSignature() throws Exception {
        // JWT for client1, but signed by privateKey of client2
        String invalidJwt = getClientSignedJWT(getClient2KeyPair(), "client1");
        List<NameValuePair> parameters = new LinkedList<NameValuePair>();
        parameters.add(new BasicNameValuePair(OAuth2Constants.GRANT_TYPE, OAuth2Constants.CLIENT_CREDENTIALS));
        parameters.add(new BasicNameValuePair(OAuth2Constants.CLIENT_ASSERTION_TYPE, OAuth2Constants.CLIENT_ASSERTION_TYPE_JWT));
        parameters.add(new BasicNameValuePair(OAuth2Constants.CLIENT_ASSERTION, invalidJwt));
        CloseableHttpResponse resp = sendRequest(oauth.getServiceAccountUrl(), parameters);
        OAuthClient.AccessTokenResponse response = new OAuthClient.AccessTokenResponse(resp);
        assertError(response, "client1", "unauthorized_client", CLIENT_CREDENTIALS_SETUP_REQUIRED.toString().toLowerCase());
    }

    @Test
    public void testAssertionExpired() throws Exception {
        String invalidJwt = getClient1SignedJWT();
        setTimeOffset(1000);
        List<NameValuePair> parameters = new LinkedList<NameValuePair>();
        parameters.add(new BasicNameValuePair(OAuth2Constants.GRANT_TYPE, OAuth2Constants.CLIENT_CREDENTIALS));
        parameters.add(new BasicNameValuePair(OAuth2Constants.CLIENT_ASSERTION_TYPE, OAuth2Constants.CLIENT_ASSERTION_TYPE_JWT));
        parameters.add(new BasicNameValuePair(OAuth2Constants.CLIENT_ASSERTION, invalidJwt));
        CloseableHttpResponse resp = sendRequest(oauth.getServiceAccountUrl(), parameters);
        OAuthClient.AccessTokenResponse response = new OAuthClient.AccessTokenResponse(resp);
        setTimeOffset(0);
        assertError(response, "client1", "unauthorized_client", INVALID_CLIENT_CREDENTIALS);
    }

    @Test
    public void testAssertionInvalidNotBefore() throws Exception {
        String invalidJwt = getClient1SignedJWT();
        setTimeOffset((-1000));
        List<NameValuePair> parameters = new LinkedList<NameValuePair>();
        parameters.add(new BasicNameValuePair(OAuth2Constants.GRANT_TYPE, OAuth2Constants.CLIENT_CREDENTIALS));
        parameters.add(new BasicNameValuePair(OAuth2Constants.CLIENT_ASSERTION_TYPE, OAuth2Constants.CLIENT_ASSERTION_TYPE_JWT));
        parameters.add(new BasicNameValuePair(OAuth2Constants.CLIENT_ASSERTION, invalidJwt));
        CloseableHttpResponse resp = sendRequest(oauth.getServiceAccountUrl(), parameters);
        OAuthClient.AccessTokenResponse response = new OAuthClient.AccessTokenResponse(resp);
        setTimeOffset(0);
        assertError(response, "client1", "unauthorized_client", INVALID_CLIENT_CREDENTIALS);
    }

    @Test
    public void testAssertionReuse() throws Exception {
        String clientJwt = getClient1SignedJWT();
        OAuthClient.AccessTokenResponse response = doClientCredentialsGrantRequest(clientJwt);
        Assert.assertEquals(200, response.getStatusCode());
        AccessToken accessToken = oauth.verifyToken(response.getAccessToken());
        org.keycloak.testsuite.Assert.assertNotNull(accessToken);
        org.keycloak.testsuite.Assert.assertNull(response.getError());
        // 2nd attempt to reuse same JWT should fail
        response = doClientCredentialsGrantRequest(clientJwt);
        Assert.assertEquals(400, response.getStatusCode());
        Assert.assertEquals("unauthorized_client", response.getError());
    }

    @Test
    public void testMissingIdClaim() throws Exception {
        OAuthClient.AccessTokenResponse response = testMissingClaim("id");
        assertError(response, ClientAuthSignedJWTTest.app1.getClientId(), "unauthorized_client", INVALID_CLIENT_CREDENTIALS);
    }

    @Test
    public void testMissingIssuerClaim() throws Exception {
        OAuthClient.AccessTokenResponse response = testMissingClaim("issuer");
        assertSuccess(response, ClientAuthSignedJWTTest.app1.getClientId(), ClientAuthSignedJWTTest.serviceAccountUser.getId(), ClientAuthSignedJWTTest.serviceAccountUser.getUsername());
    }

    @Test
    public void testMissingSubjectClaim() throws Exception {
        OAuthClient.AccessTokenResponse response = testMissingClaim("subject");
        assertError(response, null, "unauthorized_client", INVALID_CLIENT_CREDENTIALS);
    }

    @Test
    public void testMissingAudienceClaim() throws Exception {
        OAuthClient.AccessTokenResponse response = testMissingClaim("audience");
        assertError(response, ClientAuthSignedJWTTest.app1.getClientId(), "unauthorized_client", INVALID_CLIENT_CREDENTIALS);
    }

    @Test
    public void testMissingIssuedAtClaim() throws Exception {
        OAuthClient.AccessTokenResponse response = testMissingClaim("issuedAt");
        assertSuccess(response, ClientAuthSignedJWTTest.app1.getClientId(), ClientAuthSignedJWTTest.serviceAccountUser.getId(), ClientAuthSignedJWTTest.serviceAccountUser.getUsername());
    }

    // KEYCLOAK-2986
    @Test
    public void testMissingExpirationClaim() throws Exception {
        // Missing only exp; the lifespan should be calculated from issuedAt
        OAuthClient.AccessTokenResponse response = testMissingClaim("expiration");
        assertSuccess(response, ClientAuthSignedJWTTest.app1.getClientId(), ClientAuthSignedJWTTest.serviceAccountUser.getId(), ClientAuthSignedJWTTest.serviceAccountUser.getUsername());
        // Test expired lifespan
        response = testMissingClaim((-11), "expiration");
        assertError(response, ClientAuthSignedJWTTest.app1.getClientId(), "unauthorized_client", INVALID_CLIENT_CREDENTIALS);
        // Missing exp and issuedAt should return error
        response = testMissingClaim("expiration", "issuedAt");
        assertError(response, ClientAuthSignedJWTTest.app1.getClientId(), "unauthorized_client", INVALID_CLIENT_CREDENTIALS);
    }

    @Test
    public void testMissingNotBeforeClaim() throws Exception {
        OAuthClient.AccessTokenResponse response = testMissingClaim("notBefore");
        assertSuccess(response, ClientAuthSignedJWTTest.app1.getClientId(), ClientAuthSignedJWTTest.serviceAccountUser.getId(), ClientAuthSignedJWTTest.serviceAccountUser.getUsername());
    }

    /**
     * Custom JWTClientCredentialsProvider with support for missing JWT claims
     */
    protected class CustomJWTClientCredentialsProvider extends JWTClientCredentialsProvider {
        private Map<String, Boolean> enabledClaims = new HashMap<>();

        public CustomJWTClientCredentialsProvider() {
            super();
            final String[] claims = new String[]{ "id", "issuer", "subject", "audience", "expiration", "notBefore", "issuedAt" };
            for (String claim : claims) {
                enabledClaims.put(claim, true);
            }
        }

        public void enableClaim(String claim, boolean value) {
            if (!(enabledClaims.containsKey(claim))) {
                throw new IllegalArgumentException((("Claim \"" + claim) + "\" doesn\'t exist"));
            }
            enabledClaims.put(claim, value);
        }

        public boolean isClaimEnabled(String claim) {
            Boolean value = enabledClaims.get(claim);
            if (value == null) {
                throw new IllegalArgumentException((("Claim \"" + claim) + "\" doesn\'t exist"));
            }
            return value;
        }

        public Set<String> getClaims() {
            return enabledClaims.keySet();
        }

        @Override
        protected JsonWebToken createRequestToken(String clientId, String realmInfoUrl) {
            JsonWebToken reqToken = new JsonWebToken();
            if (isClaimEnabled("id"))
                reqToken.id(AdapterUtils.generateId());

            if (isClaimEnabled("issuer"))
                reqToken.issuer(clientId);

            if (isClaimEnabled("subject"))
                reqToken.subject(clientId);

            if (isClaimEnabled("audience"))
                reqToken.audience(realmInfoUrl);

            int now = Time.currentTime();
            if (isClaimEnabled("issuedAt"))
                reqToken.issuedAt(now);

            if (isClaimEnabled("expiration"))
                reqToken.expiration((now + (getTokenTimeout())));

            if (isClaimEnabled("notBefore"))
                reqToken.notBefore(now);

            return reqToken;
        }
    }
}

