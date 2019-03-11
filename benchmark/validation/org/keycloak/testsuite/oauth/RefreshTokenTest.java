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


import Algorithm.ES256;
import Algorithm.ES384;
import Algorithm.ES512;
import Algorithm.HS256;
import Algorithm.RS256;
import Algorithm.RS384;
import Algorithm.RS512;
import Details.CODE_ID;
import Details.REFRESH_TOKEN_ID;
import Details.TOKEN_ID;
import Details.UPDATED_REFRESH_TOKEN_ID;
import Errors.CLIENT_DISABLED;
import Errors.INVALID_TOKEN;
import HttpHeaders.AUTHORIZATION;
import OAuth2Constants.CODE;
import SslRequired.ALL;
import SslRequired.EXTERNAL;
import java.net.URI;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.models.utils.SessionTimeoutHelper;
import org.keycloak.protocol.oidc.OIDCLoginProtocolService;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.RefreshToken;
import org.keycloak.representations.idm.EventRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.AssertEvents;
import org.keycloak.testsuite.admin.ApiUtil;
import org.keycloak.testsuite.util.ClientManager;
import org.keycloak.testsuite.util.OAuthClient;
import org.keycloak.testsuite.util.RealmManager;
import org.keycloak.testsuite.util.TokenSignatureUtil;
import org.keycloak.testsuite.util.UserManager;
import org.keycloak.util.BasicAuthHelper;


/**
 *
 *
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class RefreshTokenTest extends AbstractKeycloakTest {
    @Rule
    public AssertEvents events = new AssertEvents(this);

    /**
     * KEYCLOAK-547
     *
     * @throws Exception
     * 		
     */
    @Test
    public void nullRefreshToken() throws Exception {
        Client client = ClientBuilder.newClient();
        UriBuilder builder = UriBuilder.fromUri(OAuthClient.AUTH_SERVER_ROOT);
        URI uri = OIDCLoginProtocolService.tokenUrl(builder).build("test");
        WebTarget target = client.target(uri);
        AccessTokenResponse tokenResponse = null;
        {
            String header = BasicAuthHelper.createHeader("test-app", "password");
            Form form = new Form();
            Response response = target.request().header(AUTHORIZATION, header).post(Entity.form(form));
            Assert.assertEquals(400, response.getStatus());
            response.close();
        }
        events.clear();
    }

    @Test
    public void invalidRefreshToken() throws Exception {
        OAuthClient.AccessTokenResponse response = oauth.doRefreshTokenRequest("invalid", "password");
        Assert.assertEquals(400, response.getStatusCode());
        Assert.assertEquals("invalid_grant", response.getError());
        events.clear();
    }

    @Test
    public void refreshTokenRequest() throws Exception {
        oauth.nonce("123456");
        oauth.doLogin("test-user@localhost", "password");
        EventRepresentation loginEvent = events.expectLogin().assertEvent();
        String sessionId = loginEvent.getSessionId();
        String codeId = loginEvent.getDetails().get(CODE_ID);
        String code = oauth.getCurrentQuery().get(CODE);
        OAuthClient.AccessTokenResponse tokenResponse = oauth.doAccessTokenRequest(code, "password");
        AccessToken token = oauth.verifyToken(tokenResponse.getAccessToken());
        Assert.assertEquals("123456", token.getNonce());
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
        OAuthClient.AccessTokenResponse response = oauth.doRefreshTokenRequest(refreshTokenString, "password");
        AccessToken refreshedToken = oauth.verifyToken(response.getAccessToken());
        RefreshToken refreshedRefreshToken = oauth.parseRefreshToken(response.getRefreshToken());
        Assert.assertEquals(200, response.getStatusCode());
        Assert.assertEquals(sessionId, refreshedToken.getSessionState());
        Assert.assertEquals(sessionId, refreshedRefreshToken.getSessionState());
        Assert.assertThat(response.getExpiresIn(), Matchers.allOf(Matchers.greaterThanOrEqualTo(250), Matchers.lessThanOrEqualTo(300)));
        Assert.assertThat(((refreshedToken.getExpiration()) - (getCurrentTime())), Matchers.allOf(Matchers.greaterThanOrEqualTo(250), Matchers.lessThanOrEqualTo(300)));
        Assert.assertThat(((refreshedToken.getExpiration()) - (token.getExpiration())), Matchers.allOf(Matchers.greaterThanOrEqualTo(1), Matchers.lessThanOrEqualTo(10)));
        Assert.assertThat(((refreshedRefreshToken.getExpiration()) - (refreshToken.getExpiration())), Matchers.allOf(Matchers.greaterThanOrEqualTo(1), Matchers.lessThanOrEqualTo(10)));
        // "test-app" should not be an audience in the refresh token
        Assert.assertEquals("test-app", refreshedRefreshToken.getIssuedFor());
        Assert.assertFalse(refreshedRefreshToken.hasAudience("test-app"));
        Assert.assertNotEquals(token.getId(), refreshedToken.getId());
        Assert.assertNotEquals(refreshToken.getId(), refreshedRefreshToken.getId());
        Assert.assertEquals("bearer", response.getTokenType());
        Assert.assertEquals(ApiUtil.findUserByUsername(adminClient.realm("test"), "test-user@localhost").getId(), refreshedToken.getSubject());
        Assert.assertNotEquals("test-user@localhost", refreshedToken.getSubject());
        Assert.assertTrue(refreshedToken.getRealmAccess().isUserInRole("user"));
        Assert.assertEquals(1, refreshedToken.getResourceAccess(oauth.getClientId()).getRoles().size());
        Assert.assertTrue(refreshedToken.getResourceAccess(oauth.getClientId()).isUserInRole("customer-user"));
        EventRepresentation refreshEvent = events.expectRefresh(tokenEvent.getDetails().get(REFRESH_TOKEN_ID), sessionId).assertEvent();
        Assert.assertNotEquals(tokenEvent.getDetails().get(TOKEN_ID), refreshEvent.getDetails().get(TOKEN_ID));
        Assert.assertNotEquals(tokenEvent.getDetails().get(REFRESH_TOKEN_ID), refreshEvent.getDetails().get(UPDATED_REFRESH_TOKEN_ID));
        Assert.assertEquals("123456", refreshedToken.getNonce());
        setTimeOffset(0);
    }

    @Test
    public void refreshTokenWithAccessToken() throws Exception {
        oauth.doLogin("test-user@localhost", "password");
        String code = oauth.getCurrentQuery().get(CODE);
        OAuthClient.AccessTokenResponse tokenResponse = oauth.doAccessTokenRequest(code, "password");
        String accessTokenString = tokenResponse.getAccessToken();
        OAuthClient.AccessTokenResponse response = oauth.doRefreshTokenRequest(accessTokenString, "password");
        Assert.assertNotEquals(200, response.getStatusCode());
    }

    @Test
    public void refreshTokenReuseTokenWithoutRefreshTokensRevoked() throws Exception {
        try {
            oauth.doLogin("test-user@localhost", "password");
            EventRepresentation loginEvent = events.expectLogin().assertEvent();
            String sessionId = loginEvent.getSessionId();
            String codeId = loginEvent.getDetails().get(CODE_ID);
            String code = oauth.getCurrentQuery().get(CODE);
            OAuthClient.AccessTokenResponse response1 = oauth.doAccessTokenRequest(code, "password");
            RefreshToken refreshToken1 = oauth.parseRefreshToken(response1.getRefreshToken());
            events.expectCodeToToken(codeId, sessionId).assertEvent();
            setTimeOffset(2);
            OAuthClient.AccessTokenResponse response2 = oauth.doRefreshTokenRequest(response1.getRefreshToken(), "password");
            Assert.assertEquals(200, response2.getStatusCode());
            events.expectRefresh(refreshToken1.getId(), sessionId).assertEvent();
            OAuthClient.AccessTokenResponse response3 = oauth.doRefreshTokenRequest(response1.getRefreshToken(), "password");
            Assert.assertEquals(200, response3.getStatusCode());
            events.expectRefresh(refreshToken1.getId(), sessionId).assertEvent();
        } finally {
            setTimeOffset(0);
        }
    }

    @Test
    public void refreshTokenReuseTokenWithRefreshTokensRevoked() throws Exception {
        try {
            RealmManager.realm(adminClient.realm("test")).revokeRefreshToken(true);
            oauth.doLogin("test-user@localhost", "password");
            EventRepresentation loginEvent = events.expectLogin().assertEvent();
            String sessionId = loginEvent.getSessionId();
            String codeId = loginEvent.getDetails().get(CODE_ID);
            String code = oauth.getCurrentQuery().get(CODE);
            OAuthClient.AccessTokenResponse response1 = oauth.doAccessTokenRequest(code, "password");
            RefreshToken refreshToken1 = oauth.parseRefreshToken(response1.getRefreshToken());
            events.expectCodeToToken(codeId, sessionId).assertEvent();
            setTimeOffset(2);
            OAuthClient.AccessTokenResponse response2 = oauth.doRefreshTokenRequest(response1.getRefreshToken(), "password");
            RefreshToken refreshToken2 = oauth.parseRefreshToken(response2.getRefreshToken());
            Assert.assertEquals(200, response2.getStatusCode());
            events.expectRefresh(refreshToken1.getId(), sessionId).assertEvent();
            OAuthClient.AccessTokenResponse response3 = oauth.doRefreshTokenRequest(response1.getRefreshToken(), "password");
            Assert.assertEquals(400, response3.getStatusCode());
            events.expectRefresh(refreshToken1.getId(), sessionId).removeDetail(TOKEN_ID).removeDetail(UPDATED_REFRESH_TOKEN_ID).error("invalid_token").assertEvent();
            oauth.doRefreshTokenRequest(response2.getRefreshToken(), "password");
            events.expectRefresh(refreshToken2.getId(), sessionId).assertEvent();
        } finally {
            setTimeOffset(0);
            RealmManager.realm(adminClient.realm("test")).revokeRefreshToken(false);
        }
    }

    @Test
    public void refreshTokenReuseTokenWithRefreshTokensRevokedAfterSingleReuse() throws Exception {
        try {
            RealmManager.realm(adminClient.realm("test")).revokeRefreshToken(true).refreshTokenMaxReuse(1);
            oauth.doLogin("test-user@localhost", "password");
            EventRepresentation loginEvent = events.expectLogin().assertEvent();
            String sessionId = loginEvent.getSessionId();
            String codeId = loginEvent.getDetails().get(CODE_ID);
            String code = oauth.getCurrentQuery().get(CODE);
            OAuthClient.AccessTokenResponse initialResponse = oauth.doAccessTokenRequest(code, "password");
            RefreshToken initialRefreshToken = oauth.parseRefreshToken(initialResponse.getRefreshToken());
            events.expectCodeToToken(codeId, sessionId).assertEvent();
            setTimeOffset(2);
            // Initial refresh.
            OAuthClient.AccessTokenResponse responseFirstUse = oauth.doRefreshTokenRequest(initialResponse.getRefreshToken(), "password");
            RefreshToken newTokenFirstUse = oauth.parseRefreshToken(responseFirstUse.getRefreshToken());
            Assert.assertEquals(200, responseFirstUse.getStatusCode());
            events.expectRefresh(initialRefreshToken.getId(), sessionId).assertEvent();
            setTimeOffset(4);
            // Second refresh (allowed).
            OAuthClient.AccessTokenResponse responseFirstReuse = oauth.doRefreshTokenRequest(initialResponse.getRefreshToken(), "password");
            RefreshToken newTokenFirstReuse = oauth.parseRefreshToken(responseFirstReuse.getRefreshToken());
            Assert.assertEquals(200, responseFirstReuse.getStatusCode());
            events.expectRefresh(initialRefreshToken.getId(), sessionId).assertEvent();
            // Token reused twice, became invalid.
            OAuthClient.AccessTokenResponse responseSecondReuse = oauth.doRefreshTokenRequest(initialResponse.getRefreshToken(), "password");
            Assert.assertEquals(400, responseSecondReuse.getStatusCode());
            events.expectRefresh(initialRefreshToken.getId(), sessionId).removeDetail(TOKEN_ID).removeDetail(UPDATED_REFRESH_TOKEN_ID).error("invalid_token").assertEvent();
            // Refresh token from first use became invalid.
            OAuthClient.AccessTokenResponse responseUseOfInvalidatedRefreshToken = oauth.doRefreshTokenRequest(responseFirstUse.getRefreshToken(), "password");
            Assert.assertEquals(400, responseUseOfInvalidatedRefreshToken.getStatusCode());
            events.expectRefresh(newTokenFirstUse.getId(), sessionId).removeDetail(TOKEN_ID).removeDetail(UPDATED_REFRESH_TOKEN_ID).error("invalid_token").assertEvent();
            // Refresh token from reuse is still valid.
            OAuthClient.AccessTokenResponse responseUseOfValidRefreshToken = oauth.doRefreshTokenRequest(responseFirstReuse.getRefreshToken(), "password");
            Assert.assertEquals(200, responseUseOfValidRefreshToken.getStatusCode());
            events.expectRefresh(newTokenFirstReuse.getId(), sessionId).assertEvent();
        } finally {
            setTimeOffset(0);
            RealmManager.realm(adminClient.realm("test")).refreshTokenMaxReuse(0).revokeRefreshToken(false);
        }
    }

    @Test
    public void refreshTokenReuseOfExistingTokenAfterEnablingReuseRevokation() throws Exception {
        try {
            oauth.doLogin("test-user@localhost", "password");
            EventRepresentation loginEvent = events.expectLogin().assertEvent();
            String sessionId = loginEvent.getSessionId();
            String codeId = loginEvent.getDetails().get(CODE_ID);
            String code = oauth.getCurrentQuery().get(CODE);
            OAuthClient.AccessTokenResponse initialResponse = oauth.doAccessTokenRequest(code, "password");
            RefreshToken initialRefreshToken = oauth.parseRefreshToken(initialResponse.getRefreshToken());
            events.expectCodeToToken(codeId, sessionId).assertEvent();
            setTimeOffset(2);
            // Infinite reuse allowed
            processExpectedValidRefresh(sessionId, initialRefreshToken, initialResponse.getRefreshToken());
            processExpectedValidRefresh(sessionId, initialRefreshToken, initialResponse.getRefreshToken());
            processExpectedValidRefresh(sessionId, initialRefreshToken, initialResponse.getRefreshToken());
            RealmManager.realm(adminClient.realm("test")).revokeRefreshToken(true).refreshTokenMaxReuse(1);
            // Config changed, we start tracking reuse.
            processExpectedValidRefresh(sessionId, initialRefreshToken, initialResponse.getRefreshToken());
            processExpectedValidRefresh(sessionId, initialRefreshToken, initialResponse.getRefreshToken());
            OAuthClient.AccessTokenResponse responseReuseExceeded = oauth.doRefreshTokenRequest(initialResponse.getRefreshToken(), "password");
            Assert.assertEquals(400, responseReuseExceeded.getStatusCode());
            events.expectRefresh(initialRefreshToken.getId(), sessionId).removeDetail(TOKEN_ID).removeDetail(UPDATED_REFRESH_TOKEN_ID).error("invalid_token").assertEvent();
        } finally {
            setTimeOffset(0);
            RealmManager.realm(adminClient.realm("test")).refreshTokenMaxReuse(0).revokeRefreshToken(false);
        }
    }

    @Test
    public void refreshTokenReuseOfExistingTokenAfterDisablingReuseRevokation() throws Exception {
        try {
            RealmManager.realm(adminClient.realm("test")).revokeRefreshToken(true).refreshTokenMaxReuse(1);
            oauth.doLogin("test-user@localhost", "password");
            EventRepresentation loginEvent = events.expectLogin().assertEvent();
            String sessionId = loginEvent.getSessionId();
            String codeId = loginEvent.getDetails().get(CODE_ID);
            String code = oauth.getCurrentQuery().get(CODE);
            OAuthClient.AccessTokenResponse initialResponse = oauth.doAccessTokenRequest(code, "password");
            RefreshToken initialRefreshToken = oauth.parseRefreshToken(initialResponse.getRefreshToken());
            events.expectCodeToToken(codeId, sessionId).assertEvent();
            setTimeOffset(2);
            // Single reuse authorized.
            processExpectedValidRefresh(sessionId, initialRefreshToken, initialResponse.getRefreshToken());
            processExpectedValidRefresh(sessionId, initialRefreshToken, initialResponse.getRefreshToken());
            OAuthClient.AccessTokenResponse responseReuseExceeded = oauth.doRefreshTokenRequest(initialResponse.getRefreshToken(), "password");
            Assert.assertEquals(400, responseReuseExceeded.getStatusCode());
            events.expectRefresh(initialRefreshToken.getId(), sessionId).removeDetail(TOKEN_ID).removeDetail(UPDATED_REFRESH_TOKEN_ID).error("invalid_token").assertEvent();
            RealmManager.realm(adminClient.realm("test")).revokeRefreshToken(false);
            // Config changed, token can be reused again
            processExpectedValidRefresh(sessionId, initialRefreshToken, initialResponse.getRefreshToken());
            processExpectedValidRefresh(sessionId, initialRefreshToken, initialResponse.getRefreshToken());
            processExpectedValidRefresh(sessionId, initialRefreshToken, initialResponse.getRefreshToken());
        } finally {
            setTimeOffset(0);
            RealmManager.realm(adminClient.realm("test")).refreshTokenMaxReuse(0).revokeRefreshToken(false);
        }
    }

    String privateKey;

    String publicKey;

    @Test
    public void refreshTokenClientDisabled() throws Exception {
        oauth.doLogin("test-user@localhost", "password");
        EventRepresentation loginEvent = events.expectLogin().assertEvent();
        String sessionId = loginEvent.getSessionId();
        String codeId = loginEvent.getDetails().get(CODE_ID);
        String code = oauth.getCurrentQuery().get(CODE);
        OAuthClient.AccessTokenResponse response = oauth.doAccessTokenRequest(code, "password");
        String refreshTokenString = response.getRefreshToken();
        RefreshToken refreshToken = oauth.parseRefreshToken(refreshTokenString);
        events.expectCodeToToken(codeId, sessionId).assertEvent();
        try {
            ClientManager.realm(adminClient.realm("test")).clientId(oauth.getClientId()).enabled(false);
            response = oauth.doRefreshTokenRequest(refreshTokenString, "password");
            Assert.assertEquals(400, response.getStatusCode());
            Assert.assertEquals("unauthorized_client", response.getError());
            events.expectRefresh(refreshToken.getId(), sessionId).user(((String) (null))).session(((String) (null))).clearDetails().error(CLIENT_DISABLED).assertEvent();
        } finally {
            ClientManager.realm(adminClient.realm("test")).clientId(oauth.getClientId()).enabled(true);
        }
    }

    @Test
    public void refreshTokenUserSessionExpired() {
        oauth.doLogin("test-user@localhost", "password");
        EventRepresentation loginEvent = events.expectLogin().assertEvent();
        String sessionId = loginEvent.getSessionId();
        String code = oauth.getCurrentQuery().get(CODE);
        OAuthClient.AccessTokenResponse tokenResponse = oauth.doAccessTokenRequest(code, "password");
        events.poll();
        String refreshId = oauth.parseRefreshToken(tokenResponse.getRefreshToken()).getId();
        testingClient.testing().removeUserSession("test", sessionId);
        tokenResponse = oauth.doRefreshTokenRequest(tokenResponse.getRefreshToken(), "password");
        Assert.assertEquals(400, tokenResponse.getStatusCode());
        Assert.assertNull(tokenResponse.getAccessToken());
        Assert.assertNull(tokenResponse.getRefreshToken());
        events.expectRefresh(refreshId, sessionId).error(INVALID_TOKEN);
        events.clear();
    }

    @Test
    public void testUserSessionRefreshAndIdle() throws Exception {
        oauth.doLogin("test-user@localhost", "password");
        EventRepresentation loginEvent = events.expectLogin().assertEvent();
        String sessionId = loginEvent.getSessionId();
        String code = oauth.getCurrentQuery().get(CODE);
        OAuthClient.AccessTokenResponse tokenResponse = oauth.doAccessTokenRequest(code, "password");
        events.poll();
        String refreshId = oauth.parseRefreshToken(tokenResponse.getRefreshToken()).getId();
        int last = testingClient.testing().getLastSessionRefresh("test", sessionId, false);
        setTimeOffset(2);
        tokenResponse = oauth.doRefreshTokenRequest(tokenResponse.getRefreshToken(), "password");
        AccessToken refreshedToken = oauth.verifyToken(tokenResponse.getAccessToken());
        RefreshToken refreshedRefreshToken = oauth.parseRefreshToken(tokenResponse.getRefreshToken());
        Assert.assertEquals(200, tokenResponse.getStatusCode());
        int next = testingClient.testing().getLastSessionRefresh("test", sessionId, false);
        Assert.assertNotEquals(last, next);
        RealmResource realmResource = adminClient.realm("test");
        int lastAccessTokenLifespan = realmResource.toRepresentation().getAccessTokenLifespan();
        RealmManager.realm(realmResource).accessTokenLifespan(100000);
        setTimeOffset(4);
        tokenResponse = oauth.doRefreshTokenRequest(tokenResponse.getRefreshToken(), "password");
        next = testingClient.testing().getLastSessionRefresh("test", sessionId, false);
        // lastSEssionRefresh should be updated because access code lifespan is higher than sso idle timeout
        Assert.assertThat(next, Matchers.allOf(Matchers.greaterThan(last), Matchers.lessThan((last + 50))));
        int originalIdle = realmResource.toRepresentation().getSsoSessionIdleTimeout();
        RealmManager.realm(realmResource).ssoSessionIdleTimeout(1);
        events.clear();
        // Needs to add some additional time due the tollerance allowed by IDLE_TIMEOUT_WINDOW_SECONDS
        setTimeOffset((6 + (SessionTimeoutHelper.IDLE_TIMEOUT_WINDOW_SECONDS)));
        tokenResponse = oauth.doRefreshTokenRequest(tokenResponse.getRefreshToken(), "password");
        // test idle timeout
        Assert.assertEquals(400, tokenResponse.getStatusCode());
        Assert.assertNull(tokenResponse.getAccessToken());
        Assert.assertNull(tokenResponse.getRefreshToken());
        events.expectRefresh(refreshId, sessionId).error(INVALID_TOKEN);
        RealmManager.realm(realmResource).ssoSessionIdleTimeout(originalIdle).accessTokenLifespan(lastAccessTokenLifespan);
        events.clear();
        setTimeOffset(0);
    }

    @Test
    public void testUserSessionRefreshAndIdleRememberMe() throws Exception {
        RealmResource testRealm = adminClient.realm("test");
        RealmRepresentation testRealmRep = testRealm.toRepresentation();
        Boolean previousRememberMe = testRealmRep.isRememberMe();
        int originalIdleRememberMe = testRealmRep.getSsoSessionIdleTimeoutRememberMe();
        try {
            testRealmRep.setRememberMe(true);
            testRealm.update(testRealmRep);
            oauth.doRememberMeLogin("test-user@localhost", "password");
            EventRepresentation loginEvent = events.expectLogin().assertEvent();
            String sessionId = loginEvent.getSessionId();
            String code = oauth.getCurrentQuery().get(CODE);
            OAuthClient.AccessTokenResponse tokenResponse = oauth.doAccessTokenRequest(code, "password");
            events.poll();
            String refreshId = oauth.parseRefreshToken(tokenResponse.getRefreshToken()).getId();
            int last = testingClient.testing().getLastSessionRefresh("test", sessionId, false);
            setTimeOffset(2);
            tokenResponse = oauth.doRefreshTokenRequest(tokenResponse.getRefreshToken(), "password");
            oauth.verifyToken(tokenResponse.getAccessToken());
            oauth.parseRefreshToken(tokenResponse.getRefreshToken());
            Assert.assertEquals(200, tokenResponse.getStatusCode());
            int next = testingClient.testing().getLastSessionRefresh("test", sessionId, false);
            Assert.assertNotEquals(last, next);
            testRealmRep.setSsoSessionIdleTimeoutRememberMe(1);
            testRealm.update(testRealmRep);
            events.clear();
            // Needs to add some additional time due the tollerance allowed by IDLE_TIMEOUT_WINDOW_SECONDS
            setTimeOffset((6 + (SessionTimeoutHelper.IDLE_TIMEOUT_WINDOW_SECONDS)));
            tokenResponse = oauth.doRefreshTokenRequest(tokenResponse.getRefreshToken(), "password");
            // test idle remember me timeout
            Assert.assertEquals(400, tokenResponse.getStatusCode());
            Assert.assertNull(tokenResponse.getAccessToken());
            Assert.assertNull(tokenResponse.getRefreshToken());
            events.expectRefresh(refreshId, sessionId).error(INVALID_TOKEN);
            events.clear();
        } finally {
            testRealmRep.setSsoSessionIdleTimeoutRememberMe(originalIdleRememberMe);
            testRealmRep.setRememberMe(previousRememberMe);
            testRealm.update(testRealmRep);
            setTimeOffset(0);
        }
    }

    @Test
    public void refreshTokenUserSessionMaxLifespan() throws Exception {
        oauth.doLogin("test-user@localhost", "password");
        EventRepresentation loginEvent = events.expectLogin().assertEvent();
        String sessionId = loginEvent.getSessionId();
        String code = oauth.getCurrentQuery().get(CODE);
        OAuthClient.AccessTokenResponse tokenResponse = oauth.doAccessTokenRequest(code, "password");
        events.poll();
        String refreshId = oauth.parseRefreshToken(tokenResponse.getRefreshToken()).getId();
        RealmResource realmResource = adminClient.realm("test");
        Integer maxLifespan = realmResource.toRepresentation().getSsoSessionMaxLifespan();
        RealmManager.realm(realmResource).ssoSessionMaxLifespan(1);
        setTimeOffset(1);
        tokenResponse = oauth.doRefreshTokenRequest(tokenResponse.getRefreshToken(), "password");
        Assert.assertEquals(400, tokenResponse.getStatusCode());
        Assert.assertNull(tokenResponse.getAccessToken());
        Assert.assertNull(tokenResponse.getRefreshToken());
        RealmManager.realm(realmResource).ssoSessionMaxLifespan(maxLifespan);
        events.expectRefresh(refreshId, sessionId).error(INVALID_TOKEN);
        events.clear();
        setTimeOffset(0);
    }

    /**
     * KEYCLOAK-1267
     *
     * @throws Exception
     * 		
     */
    @Test
    public void refreshTokenUserSessionMaxLifespanWithRememberMe() throws Exception {
        RealmResource testRealm = adminClient.realm("test");
        RealmRepresentation testRealmRep = testRealm.toRepresentation();
        Boolean previousRememberMe = testRealmRep.isRememberMe();
        int previousSsoMaxLifespanRememberMe = testRealmRep.getSsoSessionMaxLifespanRememberMe();
        try {
            testRealmRep.setRememberMe(true);
            testRealm.update(testRealmRep);
            oauth.doRememberMeLogin("test-user@localhost", "password");
            EventRepresentation loginEvent = events.expectLogin().assertEvent();
            String sessionId = loginEvent.getSessionId();
            String code = oauth.getCurrentQuery().get(CODE);
            OAuthClient.AccessTokenResponse tokenResponse = oauth.doAccessTokenRequest(code, "password");
            events.poll();
            String refreshId = oauth.parseRefreshToken(tokenResponse.getRefreshToken()).getId();
            testRealmRep.setSsoSessionMaxLifespanRememberMe(1);
            testRealm.update(testRealmRep);
            setTimeOffset(2);
            tokenResponse = oauth.doRefreshTokenRequest(tokenResponse.getRefreshToken(), "password");
            Assert.assertEquals(400, tokenResponse.getStatusCode());
            Assert.assertNull(tokenResponse.getAccessToken());
            Assert.assertNull(tokenResponse.getRefreshToken());
            events.expectRefresh(refreshId, sessionId).error(INVALID_TOKEN);
            events.clear();
        } finally {
            testRealmRep.setSsoSessionMaxLifespanRememberMe(previousSsoMaxLifespanRememberMe);
            testRealmRep.setRememberMe(previousRememberMe);
            testRealm.update(testRealmRep);
            setTimeOffset(0);
        }
    }

    @Test
    public void testCheckSsl() throws Exception {
        Client client = ClientBuilder.newClient();
        UriBuilder builder = UriBuilder.fromUri(OAuthClient.AUTH_SERVER_ROOT);
        URI grantUri = OIDCLoginProtocolService.tokenUrl(builder).build("test");
        WebTarget grantTarget = client.target(grantUri);
        builder = UriBuilder.fromUri(OAuthClient.AUTH_SERVER_ROOT);
        URI uri = OIDCLoginProtocolService.tokenUrl(builder).build("test");
        WebTarget refreshTarget = client.target(uri);
        String refreshToken = null;
        {
            Response response = executeGrantAccessTokenRequest(grantTarget);
            Assert.assertEquals(200, response.getStatus());
            AccessTokenResponse tokenResponse = response.readEntity(AccessTokenResponse.class);
            refreshToken = tokenResponse.getRefreshToken();
            response.close();
        }
        {
            Response response = executeRefreshToken(refreshTarget, refreshToken);
            Assert.assertEquals(200, response.getStatus());
            AccessTokenResponse tokenResponse = response.readEntity(AccessTokenResponse.class);
            refreshToken = tokenResponse.getRefreshToken();
            response.close();
        }
        if (!(AbstractKeycloakTest.AUTH_SERVER_SSL_REQUIRED)) {
            // test checkSsl
            RealmResource realmResource = adminClient.realm("test");
            {
                RealmManager.realm(realmResource).sslRequired(ALL.toString());
            }
            Response response = executeRefreshToken(refreshTarget, refreshToken);
            Assert.assertEquals(403, response.getStatus());
            response.close();
            {
                RealmManager.realm(realmResource).sslRequired(EXTERNAL.toString());
            }
        }
        {
            Response response = executeRefreshToken(refreshTarget, refreshToken);
            Assert.assertEquals(200, response.getStatus());
            AccessTokenResponse tokenResponse = response.readEntity(AccessTokenResponse.class);
            refreshToken = tokenResponse.getRefreshToken();
            response.close();
        }
        client.close();
        events.clear();
    }

    @Test
    public void refreshTokenUserDisabled() throws Exception {
        oauth.doLogin("test-user@localhost", "password");
        EventRepresentation loginEvent = events.expectLogin().assertEvent();
        String sessionId = loginEvent.getSessionId();
        String codeId = loginEvent.getDetails().get(CODE_ID);
        String code = oauth.getCurrentQuery().get(CODE);
        OAuthClient.AccessTokenResponse response = oauth.doAccessTokenRequest(code, "password");
        String refreshTokenString = response.getRefreshToken();
        RefreshToken refreshToken = oauth.parseRefreshToken(refreshTokenString);
        events.expectCodeToToken(codeId, sessionId).assertEvent();
        try {
            UserManager.realm(adminClient.realm("test")).username("test-user@localhost").enabled(false);
            response = oauth.doRefreshTokenRequest(refreshTokenString, "password");
            Assert.assertEquals(400, response.getStatusCode());
            Assert.assertEquals("invalid_grant", response.getError());
            events.expectRefresh(refreshToken.getId(), sessionId).clearDetails().error(INVALID_TOKEN).assertEvent();
        } finally {
            UserManager.realm(adminClient.realm("test")).username("test-user@localhost").enabled(true);
        }
    }

    @Test
    public void refreshTokenUserDeleted() throws Exception {
        String userId = createUser("test", "temp-user@localhost", "password");
        oauth.doLogin("temp-user@localhost", "password");
        EventRepresentation loginEvent = events.expectLogin().user(userId).assertEvent();
        String sessionId = loginEvent.getSessionId();
        String codeId = loginEvent.getDetails().get(CODE_ID);
        String code = oauth.getCurrentQuery().get(CODE);
        OAuthClient.AccessTokenResponse response = oauth.doAccessTokenRequest(code, "password");
        String refreshTokenString = response.getRefreshToken();
        RefreshToken refreshToken = oauth.parseRefreshToken(refreshTokenString);
        events.expectCodeToToken(codeId, sessionId).user(userId).assertEvent();
        adminClient.realm("test").users().delete(userId);
        response = oauth.doRefreshTokenRequest(refreshTokenString, "password");
        Assert.assertEquals(400, response.getStatusCode());
        Assert.assertEquals("invalid_grant", response.getError());
        events.expectRefresh(refreshToken.getId(), sessionId).user(userId).clearDetails().error(INVALID_TOKEN).assertEvent();
    }

    @Test
    public void refreshTokenServiceAccount() throws Exception {
        OAuthClient.AccessTokenResponse response = oauth.clientId("service-account-app").doClientCredentialsGrantAccessTokenRequest("secret");
        Assert.assertNotNull(response.getRefreshToken());
        response = oauth.doRefreshTokenRequest(response.getRefreshToken(), "secret");
        Assert.assertNotNull(response.getRefreshToken());
    }

    @Test
    public void tokenRefreshRequest_RealmRS384_ClientRS384_EffectiveRS384() throws Exception {
        try {
            TokenSignatureUtil.changeRealmTokenSignatureProvider(adminClient, RS384);
            TokenSignatureUtil.changeClientAccessTokenSignatureProvider(ApiUtil.findClientByClientId(adminClient.realm("test"), "test-app"), RS384);
            refreshToken(HS256, RS384, RS384);
        } finally {
            TokenSignatureUtil.changeRealmTokenSignatureProvider(adminClient, RS256);
            TokenSignatureUtil.changeClientAccessTokenSignatureProvider(ApiUtil.findClientByClientId(adminClient.realm("test"), "test-app"), RS256);
        }
    }

    @Test
    public void tokenRefreshRequest_RealmRS256_ClientRS512_EffectiveRS256() throws Exception {
        try {
            TokenSignatureUtil.changeRealmTokenSignatureProvider(adminClient, RS256);
            TokenSignatureUtil.changeClientAccessTokenSignatureProvider(ApiUtil.findClientByClientId(adminClient.realm("test"), "test-app"), RS512);
            refreshToken(HS256, RS512, RS256);
        } finally {
            TokenSignatureUtil.changeClientAccessTokenSignatureProvider(ApiUtil.findClientByClientId(adminClient.realm("test"), "test-app"), RS256);
        }
    }

    @Test
    public void tokenRefreshRequest_RealmRS256_ClientES256_EffectiveRS256() throws Exception {
        try {
            TokenSignatureUtil.changeRealmTokenSignatureProvider(adminClient, RS256);
            TokenSignatureUtil.changeClientAccessTokenSignatureProvider(ApiUtil.findClientByClientId(adminClient.realm("test"), "test-app"), ES256);
            TokenSignatureUtil.registerKeyProvider("P-256", adminClient, testContext);
            refreshTokenSignatureVerifyOnly(HS256, ES256, RS256);
        } finally {
            TokenSignatureUtil.changeClientAccessTokenSignatureProvider(ApiUtil.findClientByClientId(adminClient.realm("test"), "test-app"), RS256);
        }
    }

    @Test
    public void tokenRefreshRequest_RealmES384_ClientES384_EffectiveES384() throws Exception {
        try {
            TokenSignatureUtil.changeRealmTokenSignatureProvider(adminClient, ES384);
            TokenSignatureUtil.changeClientAccessTokenSignatureProvider(ApiUtil.findClientByClientId(adminClient.realm("test"), "test-app"), ES384);
            TokenSignatureUtil.registerKeyProvider("P-384", adminClient, testContext);
            refreshTokenSignatureVerifyOnly(HS256, ES384, ES384);
        } finally {
            TokenSignatureUtil.changeRealmTokenSignatureProvider(adminClient, RS256);
            TokenSignatureUtil.changeClientAccessTokenSignatureProvider(ApiUtil.findClientByClientId(adminClient.realm("test"), "test-app"), RS256);
        }
    }

    @Test
    public void tokenRefreshRequest_RealmRS256_ClientES512_EffectiveRS256() throws Exception {
        try {
            TokenSignatureUtil.changeRealmTokenSignatureProvider(adminClient, RS256);
            TokenSignatureUtil.changeClientAccessTokenSignatureProvider(ApiUtil.findClientByClientId(adminClient.realm("test"), "test-app"), ES512);
            TokenSignatureUtil.registerKeyProvider("P-521", adminClient, testContext);
            refreshTokenSignatureVerifyOnly(HS256, ES512, RS256);
        } finally {
            TokenSignatureUtil.changeClientAccessTokenSignatureProvider(ApiUtil.findClientByClientId(adminClient.realm("test"), "test-app"), RS256);
        }
    }
}

