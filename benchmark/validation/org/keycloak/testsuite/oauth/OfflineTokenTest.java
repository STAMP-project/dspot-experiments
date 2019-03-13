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


import AccountApplicationsPage.AppEntry;
import AdminRoles.VIEW_REALM;
import AuthRealm.MASTER;
import Constants.ADMIN_CLI_CLIENT_ID;
import Constants.OFFLINE_ACCESS_ROLE;
import Constants.REALM_MANAGEMENT_CLIENT_ID;
import Details.CODE_ID;
import Details.CONSENT;
import Details.GRANT_TYPE;
import Details.REDIRECT_URI;
import Details.REFRESH_TOKEN_ID;
import Details.REFRESH_TOKEN_TYPE;
import Details.TOKEN_ID;
import Details.USERNAME;
import Errors.INVALID_TOKEN;
import OAuth2Constants.CODE;
import OAuth2Constants.OFFLINE_ACCESS;
import OAuth2Constants.PASSWORD;
import OAuthClient.AccessTokenResponse;
import TokenUtil.TOKEN_TYPE_OFFLINE;
import TokenUtil.TOKEN_TYPE_REFRESH;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.common.constants.ServiceAccountConstants;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.models.utils.SessionTimeoutHelper;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.RefreshToken;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.ClientScopeRepresentation;
import org.keycloak.representations.idm.EventRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.AssertEvents;
import org.keycloak.testsuite.admin.ApiUtil;
import org.keycloak.testsuite.arquillian.AuthServerTestEnricher;
import org.keycloak.testsuite.pages.AccountApplicationsPage;
import org.keycloak.testsuite.pages.LoginPage;
import org.keycloak.testsuite.util.ClientBuilder;
import org.keycloak.testsuite.util.ClientManager;
import org.keycloak.testsuite.util.OAuthClient;
import org.keycloak.testsuite.util.RealmManager;
import org.keycloak.testsuite.util.RoleBuilder;
import org.keycloak.testsuite.util.TokenSignatureUtil;
import org.keycloak.testsuite.utils.tls.TLSUtils;


/**
 *
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class OfflineTokenTest extends AbstractKeycloakTest {
    private static String userId;

    private static String offlineClientAppUri;

    private static String serviceAccountUserId;

    @Page
    protected LoginPage loginPage;

    @Page
    protected AccountApplicationsPage applicationsPage;

    @Rule
    public AssertEvents events = new AssertEvents(this);

    @Test
    public void offlineTokenDisabledForClient() throws Exception {
        // Remove offline-access scope from client
        ClientScopeRepresentation offlineScope = adminClient.realm("test").clientScopes().findAll().stream().filter((ClientScopeRepresentation clientScope) -> {
            return OAuth2Constants.OFFLINE_ACCESS.equals(clientScope.getName());
        }).findFirst().get();
        ClientManager.realm(adminClient.realm("test")).clientId("offline-client").fullScopeAllowed(false).removeClientScope(offlineScope.getId(), false);
        oauth.scope(OFFLINE_ACCESS);
        oauth.clientId("offline-client");
        oauth.redirectUri(OfflineTokenTest.offlineClientAppUri);
        oauth.doLogin("test-user@localhost", "password");
        EventRepresentation loginEvent = events.expectLogin().client("offline-client").detail(REDIRECT_URI, OfflineTokenTest.offlineClientAppUri).assertEvent();
        String sessionId = loginEvent.getSessionId();
        String codeId = loginEvent.getDetails().get(CODE_ID);
        String code = oauth.getCurrentQuery().get(CODE);
        OAuthClient.AccessTokenResponse tokenResponse = oauth.doAccessTokenRequest(code, "secret1");
        String offlineTokenString = tokenResponse.getRefreshToken();
        RefreshToken refreshToken = oauth.parseRefreshToken(offlineTokenString);
        // Token is refreshed, but it's not offline token
        events.expectCodeToToken(codeId, sessionId).client("offline-client").detail(REFRESH_TOKEN_TYPE, TOKEN_TYPE_REFRESH).assertEvent();
        Assert.assertEquals(TOKEN_TYPE_REFRESH, refreshToken.getType());
        Assert.assertFalse(tokenResponse.getScope().contains(OFFLINE_ACCESS));
        // Revert changes
        ClientManager.realm(adminClient.realm("test")).clientId("offline-client").fullScopeAllowed(true).addClientScope(offlineScope.getId(), false);
    }

    @Test
    public void offlineTokenUserNotAllowed() throws Exception {
        String userId = ApiUtil.findUserByUsername(adminClient.realm("test"), "keycloak-user@localhost").getId();
        oauth.scope(OFFLINE_ACCESS);
        oauth.clientId("offline-client");
        oauth.redirectUri(OfflineTokenTest.offlineClientAppUri);
        oauth.doLogin("keycloak-user@localhost", "password");
        EventRepresentation loginEvent = events.expectLogin().client("offline-client").user(userId).detail(REDIRECT_URI, OfflineTokenTest.offlineClientAppUri).assertEvent();
        String sessionId = loginEvent.getSessionId();
        String codeId = loginEvent.getDetails().get(CODE_ID);
        String code = oauth.getCurrentQuery().get(CODE);
        OAuthClient.AccessTokenResponse tokenResponse = oauth.doAccessTokenRequest(code, "secret1");
        Assert.assertEquals(400, tokenResponse.getStatusCode());
        Assert.assertEquals("not_allowed", tokenResponse.getError());
        events.expectCodeToToken(codeId, sessionId).client("offline-client").user(userId).error("not_allowed").clearDetails().assertEvent();
    }

    @Test
    public void offlineTokenBrowserFlow() throws Exception {
        oauth.scope(OFFLINE_ACCESS);
        oauth.clientId("offline-client");
        oauth.redirectUri(OfflineTokenTest.offlineClientAppUri);
        oauth.doLogin("test-user@localhost", "password");
        EventRepresentation loginEvent = events.expectLogin().client("offline-client").detail(REDIRECT_URI, OfflineTokenTest.offlineClientAppUri).assertEvent();
        final String sessionId = loginEvent.getSessionId();
        String codeId = loginEvent.getDetails().get(CODE_ID);
        String code = oauth.getCurrentQuery().get(CODE);
        OAuthClient.AccessTokenResponse tokenResponse = oauth.doAccessTokenRequest(code, "secret1");
        AccessToken token = oauth.verifyToken(tokenResponse.getAccessToken());
        String offlineTokenString = tokenResponse.getRefreshToken();
        RefreshToken offlineToken = oauth.parseRefreshToken(offlineTokenString);
        events.expectCodeToToken(codeId, sessionId).client("offline-client").detail(REFRESH_TOKEN_TYPE, TOKEN_TYPE_OFFLINE).assertEvent();
        Assert.assertEquals(TOKEN_TYPE_OFFLINE, offlineToken.getType());
        Assert.assertEquals(0, offlineToken.getExpiration());
        Assert.assertTrue(tokenResponse.getScope().contains(OFFLINE_ACCESS));
        String newRefreshTokenString = testRefreshWithOfflineToken(token, offlineToken, offlineTokenString, sessionId, OfflineTokenTest.userId);
        // Change offset to very big value to ensure offline session expires
        setTimeOffset(3000000);
        OAuthClient.AccessTokenResponse response = oauth.doRefreshTokenRequest(newRefreshTokenString, "secret1");
        Assert.assertEquals(400, response.getStatusCode());
        Assert.assertEquals("invalid_grant", response.getError());
        events.expectRefresh(offlineToken.getId(), sessionId).client("offline-client").error(INVALID_TOKEN).user(OfflineTokenTest.userId).clearDetails().assertEvent();
        setTimeOffset(0);
    }

    @Test
    public void offlineTokenDirectGrantFlow() throws Exception {
        oauth.scope(OFFLINE_ACCESS);
        oauth.clientId("offline-client");
        OAuthClient.AccessTokenResponse tokenResponse = oauth.doGrantAccessTokenRequest("secret1", "test-user@localhost", "password");
        Assert.assertNull(tokenResponse.getErrorDescription());
        AccessToken token = oauth.verifyToken(tokenResponse.getAccessToken());
        String offlineTokenString = tokenResponse.getRefreshToken();
        RefreshToken offlineToken = oauth.parseRefreshToken(offlineTokenString);
        events.expectLogin().client("offline-client").user(OfflineTokenTest.userId).session(token.getSessionState()).detail(GRANT_TYPE, PASSWORD).detail(TOKEN_ID, token.getId()).detail(REFRESH_TOKEN_ID, offlineToken.getId()).detail(REFRESH_TOKEN_TYPE, TOKEN_TYPE_OFFLINE).detail(USERNAME, "test-user@localhost").removeDetail(CODE_ID).removeDetail(REDIRECT_URI).removeDetail(CONSENT).assertEvent();
        Assert.assertEquals(TOKEN_TYPE_OFFLINE, offlineToken.getType());
        Assert.assertEquals(0, offlineToken.getExpiration());
        testRefreshWithOfflineToken(token, offlineToken, offlineTokenString, token.getSessionState(), OfflineTokenTest.userId);
        // Assert same token can be refreshed again
        testRefreshWithOfflineToken(token, offlineToken, offlineTokenString, token.getSessionState(), OfflineTokenTest.userId);
    }

    @Test
    public void offlineTokenDirectGrantFlowWithRefreshTokensRevoked() throws Exception {
        RealmManager.realm(adminClient.realm("test")).revokeRefreshToken(true);
        oauth.scope(OFFLINE_ACCESS);
        oauth.clientId("offline-client");
        OAuthClient.AccessTokenResponse tokenResponse = oauth.doGrantAccessTokenRequest("secret1", "test-user@localhost", "password");
        AccessToken token = oauth.verifyToken(tokenResponse.getAccessToken());
        String offlineTokenString = tokenResponse.getRefreshToken();
        RefreshToken offlineToken = oauth.parseRefreshToken(offlineTokenString);
        events.expectLogin().client("offline-client").user(OfflineTokenTest.userId).session(token.getSessionState()).detail(GRANT_TYPE, PASSWORD).detail(TOKEN_ID, token.getId()).detail(REFRESH_TOKEN_ID, offlineToken.getId()).detail(REFRESH_TOKEN_TYPE, TOKEN_TYPE_OFFLINE).detail(USERNAME, "test-user@localhost").removeDetail(CODE_ID).removeDetail(REDIRECT_URI).removeDetail(CONSENT).assertEvent();
        Assert.assertEquals(TOKEN_TYPE_OFFLINE, offlineToken.getType());
        Assert.assertEquals(0, offlineToken.getExpiration());
        String offlineTokenString2 = testRefreshWithOfflineToken(token, offlineToken, offlineTokenString, token.getSessionState(), OfflineTokenTest.userId);
        RefreshToken offlineToken2 = oauth.parseRefreshToken(offlineTokenString2);
        // Assert second refresh with same refresh token will fail
        OAuthClient.AccessTokenResponse response = oauth.doRefreshTokenRequest(offlineTokenString, "secret1");
        Assert.assertEquals(400, response.getStatusCode());
        events.expectRefresh(offlineToken.getId(), token.getSessionState()).client("offline-client").error(INVALID_TOKEN).user(OfflineTokenTest.userId).clearDetails().assertEvent();
        // Refresh with new refreshToken is successful now
        testRefreshWithOfflineToken(token, offlineToken2, offlineTokenString2, token.getSessionState(), OfflineTokenTest.userId);
        RealmManager.realm(adminClient.realm("test")).revokeRefreshToken(false);
    }

    @Test
    public void offlineTokenServiceAccountFlow() throws Exception {
        oauth.scope(OFFLINE_ACCESS);
        oauth.clientId("offline-client");
        OAuthClient.AccessTokenResponse tokenResponse = oauth.doClientCredentialsGrantAccessTokenRequest("secret1");
        AccessToken token = oauth.verifyToken(tokenResponse.getAccessToken());
        String offlineTokenString = tokenResponse.getRefreshToken();
        RefreshToken offlineToken = oauth.parseRefreshToken(offlineTokenString);
        events.expectClientLogin().client("offline-client").user(OfflineTokenTest.serviceAccountUserId).session(token.getSessionState()).detail(TOKEN_ID, token.getId()).detail(REFRESH_TOKEN_ID, offlineToken.getId()).detail(REFRESH_TOKEN_TYPE, TOKEN_TYPE_OFFLINE).detail(USERNAME, ((ServiceAccountConstants.SERVICE_ACCOUNT_USER_PREFIX) + "offline-client")).assertEvent();
        Assert.assertEquals(TOKEN_TYPE_OFFLINE, offlineToken.getType());
        Assert.assertEquals(0, offlineToken.getExpiration());
        testRefreshWithOfflineToken(token, offlineToken, offlineTokenString, token.getSessionState(), OfflineTokenTest.serviceAccountUserId);
        // Now retrieve another offline token and verify that previous offline token is still valid
        tokenResponse = oauth.doClientCredentialsGrantAccessTokenRequest("secret1");
        AccessToken token2 = oauth.verifyToken(tokenResponse.getAccessToken());
        String offlineTokenString2 = tokenResponse.getRefreshToken();
        RefreshToken offlineToken2 = oauth.parseRefreshToken(offlineTokenString2);
        events.expectClientLogin().client("offline-client").user(OfflineTokenTest.serviceAccountUserId).session(token2.getSessionState()).detail(TOKEN_ID, token2.getId()).detail(REFRESH_TOKEN_ID, offlineToken2.getId()).detail(REFRESH_TOKEN_TYPE, TOKEN_TYPE_OFFLINE).detail(USERNAME, ((ServiceAccountConstants.SERVICE_ACCOUNT_USER_PREFIX) + "offline-client")).assertEvent();
        // Refresh with both offline tokens is fine
        testRefreshWithOfflineToken(token, offlineToken, offlineTokenString, token.getSessionState(), OfflineTokenTest.serviceAccountUserId);
        testRefreshWithOfflineToken(token2, offlineToken2, offlineTokenString2, token2.getSessionState(), OfflineTokenTest.serviceAccountUserId);
    }

    @Test
    public void offlineTokenAllowedWithCompositeRole() throws Exception {
        RealmResource appRealm = adminClient.realm("test");
        UserResource testUser = ApiUtil.findUserByUsernameId(appRealm, "test-user@localhost");
        RoleRepresentation offlineAccess = ApiUtil.findRealmRoleByName(adminClient.realm("test"), OFFLINE_ACCESS_ROLE).toRepresentation();
        // Grant offline_access role indirectly through composite role
        appRealm.roles().create(RoleBuilder.create().name("composite").build());
        RoleResource roleResource = appRealm.roles().get("composite");
        roleResource.addComposites(Collections.singletonList(offlineAccess));
        testUser.roles().realmLevel().remove(Collections.singletonList(offlineAccess));
        testUser.roles().realmLevel().add(Collections.singletonList(roleResource.toRepresentation()));
        // Integration test
        offlineTokenDirectGrantFlow();
        // Revert changes
        testUser.roles().realmLevel().remove(Collections.singletonList(appRealm.roles().get("composite").toRepresentation()));
        appRealm.roles().get("composite").remove();
        testUser.roles().realmLevel().add(Collections.singletonList(offlineAccess));
    }

    /**
     * KEYCLOAK-4201
     *
     * @throws Exception
     * 		
     */
    @Test
    public void offlineTokenAdminRESTAccess() throws Exception {
        // Grant "view-realm" role to user
        RealmResource appRealm = adminClient.realm("test");
        ClientResource realmMgmt = ApiUtil.findClientByClientId(appRealm, REALM_MANAGEMENT_CLIENT_ID);
        String realmMgmtUuid = realmMgmt.toRepresentation().getId();
        RoleRepresentation roleRep = realmMgmt.roles().get(VIEW_REALM).toRepresentation();
        UserResource testUser = ApiUtil.findUserByUsernameId(appRealm, "test-user@localhost");
        testUser.roles().clientLevel(realmMgmtUuid).add(Collections.singletonList(roleRep));
        // Login with offline token now
        oauth.scope(OFFLINE_ACCESS);
        oauth.clientId("offline-client");
        OAuthClient.AccessTokenResponse tokenResponse = oauth.doGrantAccessTokenRequest("secret1", "test-user@localhost", "password");
        events.clear();
        // Set the time offset, so that "normal" userSession expires
        setTimeOffset(86400);
        // Remove expired sessions. This will remove "normal" userSession
        testingClient.testing().removeUserSessions(appRealm.toRepresentation().getId());
        // Refresh with the offline token
        tokenResponse = oauth.doRefreshTokenRequest(tokenResponse.getRefreshToken(), "secret1");
        // Use accessToken to admin REST request
        try (Keycloak offlineTokenAdmin = Keycloak.getInstance(((AuthServerTestEnricher.getAuthServerContextRoot()) + "/auth"), MASTER, ADMIN_CLI_CLIENT_ID, tokenResponse.getAccessToken(), TLSUtils.initializeTLS())) {
            RealmRepresentation testRealm = offlineTokenAdmin.realm("test").toRepresentation();
            Assert.assertNotNull(testRealm);
        }
    }

    // KEYCLOAK-4525
    @Test
    public void offlineTokenRemoveClientWithTokens() throws Exception {
        // Create new client
        RealmResource appRealm = adminClient.realm("test");
        ClientRepresentation clientRep = ClientBuilder.create().clientId("offline-client-2").id(KeycloakModelUtils.generateId()).directAccessGrants().secret("secret1").build();
        appRealm.clients().create(clientRep);
        // Direct grant login requesting offline token
        oauth.scope(OFFLINE_ACCESS);
        oauth.clientId("offline-client-2");
        OAuthClient.AccessTokenResponse tokenResponse = oauth.doGrantAccessTokenRequest("secret1", "test-user@localhost", "password");
        Assert.assertNull(tokenResponse.getErrorDescription());
        AccessToken token = oauth.verifyToken(tokenResponse.getAccessToken());
        String offlineTokenString = tokenResponse.getRefreshToken();
        RefreshToken offlineToken = oauth.parseRefreshToken(offlineTokenString);
        events.expectLogin().client("offline-client-2").user(OfflineTokenTest.userId).session(token.getSessionState()).detail(GRANT_TYPE, PASSWORD).detail(TOKEN_ID, token.getId()).detail(REFRESH_TOKEN_ID, offlineToken.getId()).detail(REFRESH_TOKEN_TYPE, TOKEN_TYPE_OFFLINE).detail(USERNAME, "test-user@localhost").removeDetail(CODE_ID).removeDetail(REDIRECT_URI).removeDetail(CONSENT).assertEvent();
        // Go to account mgmt applications page
        applicationsPage.open();
        loginPage.login("test-user@localhost", "password");
        events.expectLogin().client("account").detail(REDIRECT_URI, ((getAccountRedirectUrl()) + "?path=applications")).assertEvent();
        Assert.assertTrue(applicationsPage.isCurrent());
        Map<String, AccountApplicationsPage.AppEntry> apps = applicationsPage.getApplications();
        Assert.assertTrue(apps.containsKey("offline-client-2"));
        Assert.assertEquals("Offline Token", apps.get("offline-client-2").getAdditionalGrants().get(0));
        // Now remove the client
        ClientResource offlineTokenClient2 = ApiUtil.findClientByClientId(appRealm, "offline-client-2");
        offlineTokenClient2.remove();
        // Go to applications page and see offline-client not anymore
        applicationsPage.open();
        apps = applicationsPage.getApplications();
        Assert.assertFalse(apps.containsKey("offline-client-2"));
        // Login as admin and see consents of user
        UserResource user = ApiUtil.findUserByUsernameId(appRealm, "test-user@localhost");
        List<Map<String, Object>> consents = user.getConsents();
        Assert.assertTrue(consents.isEmpty());
    }

    @Test
    public void offlineTokenLogout() throws Exception {
        oauth.scope(OFFLINE_ACCESS);
        oauth.clientId("offline-client");
        OAuthClient.AccessTokenResponse response = oauth.doGrantAccessTokenRequest("secret1", "test-user@localhost", "password");
        Assert.assertEquals(200, response.getStatusCode());
        response = oauth.doRefreshTokenRequest(response.getRefreshToken(), "secret1");
        Assert.assertEquals(200, response.getStatusCode());
        CloseableHttpResponse logoutResponse = oauth.doLogout(response.getRefreshToken(), "secret1");
        Assert.assertEquals(204, logoutResponse.getStatusLine().getStatusCode());
        response = oauth.doRefreshTokenRequest(response.getRefreshToken(), "secret1");
        Assert.assertEquals(400, response.getStatusCode());
    }

    @Test
    public void browserOfflineTokenLogoutFollowedByLoginSameSession() throws Exception {
        oauth.scope(OFFLINE_ACCESS);
        oauth.clientId("offline-client");
        oauth.redirectUri(OfflineTokenTest.offlineClientAppUri);
        oauth.doLogin("test-user@localhost", "password");
        EventRepresentation loginEvent = events.expectLogin().client("offline-client").detail(REDIRECT_URI, OfflineTokenTest.offlineClientAppUri).assertEvent();
        final String sessionId = loginEvent.getSessionId();
        String codeId = loginEvent.getDetails().get(CODE_ID);
        String code = oauth.getCurrentQuery().get(CODE);
        OAuthClient.AccessTokenResponse tokenResponse = oauth.doAccessTokenRequest(code, "secret1");
        oauth.verifyToken(tokenResponse.getAccessToken());
        String offlineTokenString = tokenResponse.getRefreshToken();
        RefreshToken offlineToken = oauth.parseRefreshToken(offlineTokenString);
        events.expectCodeToToken(codeId, sessionId).client("offline-client").detail(REFRESH_TOKEN_TYPE, TOKEN_TYPE_OFFLINE).assertEvent();
        Assert.assertEquals(TOKEN_TYPE_OFFLINE, offlineToken.getType());
        Assert.assertEquals(0, offlineToken.getExpiration());
        try (CloseableHttpResponse logoutResponse = oauth.doLogout(offlineTokenString, "secret1")) {
            Assert.assertEquals(204, logoutResponse.getStatusLine().getStatusCode());
        }
        events.expectLogout(offlineToken.getSessionState()).client("offline-client").removeDetail(REDIRECT_URI).assertEvent();
        // Need to login again now
        oauth.doLogin("test-user@localhost", "password");
        String code2 = oauth.getCurrentQuery().get(CODE);
        OAuthClient.AccessTokenResponse tokenResponse2 = oauth.doAccessTokenRequest(code2, "secret1");
        Assert.assertEquals(200, tokenResponse2.getStatusCode());
        oauth.verifyToken(tokenResponse2.getAccessToken());
        String offlineTokenString2 = tokenResponse2.getRefreshToken();
        RefreshToken offlineToken2 = oauth.parseRefreshToken(offlineTokenString2);
        loginEvent = events.expectLogin().client("offline-client").detail(REDIRECT_URI, OfflineTokenTest.offlineClientAppUri).assertEvent();
        codeId = loginEvent.getDetails().get(CODE_ID);
        events.expectCodeToToken(codeId, offlineToken2.getSessionState()).client("offline-client").detail(REFRESH_TOKEN_TYPE, TOKEN_TYPE_OFFLINE).assertEvent();
        Assert.assertEquals(TOKEN_TYPE_OFFLINE, offlineToken2.getType());
        Assert.assertEquals(0, offlineToken2.getExpiration());
        // Assert session changed
        Assert.assertNotEquals(offlineToken.getSessionState(), offlineToken2.getSessionState());
    }

    @Test
    public void offlineTokenBrowserFlowMaxLifespanExpired() throws Exception {
        // expect that offline session expired by max lifespan
        final int MAX_LIFESPAN = 3600;
        final int IDLE_LIFESPAN = 6000;
        testOfflineSessionExpiration(IDLE_LIFESPAN, MAX_LIFESPAN, (MAX_LIFESPAN + 60));
    }

    @Test
    public void offlineTokenBrowserFlowIdleTimeExpired() throws Exception {
        // expect that offline session expired by idle time
        final int MAX_LIFESPAN = 3000;
        final int IDLE_LIFESPAN = 600;
        // Additional time window is added for the case when session was updated in different DC and the update to current DC was postponed
        testOfflineSessionExpiration(IDLE_LIFESPAN, MAX_LIFESPAN, ((IDLE_LIFESPAN + (SessionTimeoutHelper.IDLE_TIMEOUT_WINDOW_SECONDS)) + 60));
    }

    @Test
    public void offlineTokenRequest_RealmRS512_ClientRS384() throws Exception {
        try {
            TokenSignatureUtil.changeRealmTokenSignatureProvider(adminClient, "RS512");
            TokenSignatureUtil.changeClientAccessTokenSignatureProvider(ApiUtil.findClientByClientId(adminClient.realm("test"), "offline-client"), "RS384");
            offlineTokenRequest("HS256", "RS384", "RS512");
        } finally {
            TokenSignatureUtil.changeRealmTokenSignatureProvider(adminClient, "RS256");
            TokenSignatureUtil.changeClientAccessTokenSignatureProvider(ApiUtil.findClientByClientId(adminClient.realm("test"), "offline-client"), "RS256");
        }
    }
}

