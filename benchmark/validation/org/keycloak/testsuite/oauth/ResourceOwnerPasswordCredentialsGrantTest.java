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


import ClientIdAndSecretAuthenticator.PROVIDER_ID;
import Details.CLIENT_AUTH_METHOD;
import Details.CODE_ID;
import Details.CONSENT;
import Details.GRANT_TYPE;
import Details.REDIRECT_URI;
import Details.REFRESH_TOKEN_ID;
import Details.TOKEN_ID;
import Details.UPDATED_REFRESH_TOKEN_ID;
import Details.USERNAME;
import Errors.INVALID_CLIENT_CREDENTIALS;
import Errors.INVALID_TOKEN;
import Errors.INVALID_USER_CREDENTIALS;
import Errors.NOT_ALLOWED;
import Errors.RESOLVE_REQUIRED_ACTIONS;
import OAuth2Constants.PASSWORD;
import OAuthClient.AccessTokenResponse;
import UserModel.RequiredAction.UPDATE_PASSWORD;
import UserModel.RequiredAction.VERIFY_EMAIL;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.models.utils.TimeBasedOTP;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.RefreshToken;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.AssertEvents;
import org.keycloak.testsuite.util.ClientManager;
import org.keycloak.testsuite.util.OAuthClient;
import org.keycloak.testsuite.util.RealmManager;
import org.keycloak.testsuite.util.UserManager;


/**
 *
 *
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class ResourceOwnerPasswordCredentialsGrantTest extends AbstractKeycloakTest {
    private static String userId;

    private static String userId2;

    private final TimeBasedOTP totp = new TimeBasedOTP();

    @Rule
    public AssertEvents events = new AssertEvents(this);

    @Test
    public void grantAccessTokenUsername() throws Exception {
        grantAccessToken("direct-login", "resource-owner");
    }

    @Test
    public void grantAccessTokenEmail() throws Exception {
        grantAccessToken("direct-login@localhost", "resource-owner");
    }

    @Test
    public void grantAccessTokenPublic() throws Exception {
        grantAccessToken("direct-login", "resource-owner-public");
    }

    @Test
    public void grantAccessTokenWithTotp() throws Exception {
        grantAccessToken(ResourceOwnerPasswordCredentialsGrantTest.userId2, "direct-login-otp", "resource-owner", totp.generateTOTP("totpSecret"));
    }

    @Test
    public void grantAccessTokenMissingTotp() throws Exception {
        oauth.clientId("resource-owner");
        OAuthClient.AccessTokenResponse response = oauth.doGrantAccessTokenRequest("secret", "direct-login-otp", "password");
        Assert.assertEquals(401, response.getStatusCode());
        Assert.assertEquals("invalid_grant", response.getError());
        events.expectLogin().client("resource-owner").session(((String) (null))).clearDetails().error(INVALID_USER_CREDENTIALS).user(ResourceOwnerPasswordCredentialsGrantTest.userId2).assertEvent();
    }

    @Test
    public void grantAccessTokenInvalidTotp() throws Exception {
        oauth.clientId("resource-owner");
        OAuthClient.AccessTokenResponse response = oauth.doGrantAccessTokenRequest("secret", "direct-login-otp", "password", totp.generateTOTP("totpSecret2"));
        Assert.assertEquals(401, response.getStatusCode());
        Assert.assertEquals("invalid_grant", response.getError());
        events.expectLogin().client("resource-owner").session(((String) (null))).clearDetails().error(INVALID_USER_CREDENTIALS).user(ResourceOwnerPasswordCredentialsGrantTest.userId2).assertEvent();
    }

    @Test
    public void grantAccessTokenLogout() throws Exception {
        oauth.clientId("resource-owner");
        OAuthClient.AccessTokenResponse response = oauth.doGrantAccessTokenRequest("secret", "test-user@localhost", "password");
        Assert.assertEquals(200, response.getStatusCode());
        AccessToken accessToken = oauth.verifyToken(response.getAccessToken());
        RefreshToken refreshToken = oauth.parseRefreshToken(response.getRefreshToken());
        events.expectLogin().client("resource-owner").session(accessToken.getSessionState()).detail(GRANT_TYPE, PASSWORD).detail(TOKEN_ID, accessToken.getId()).detail(REFRESH_TOKEN_ID, refreshToken.getId()).removeDetail(CODE_ID).removeDetail(REDIRECT_URI).removeDetail(CONSENT).detail(CLIENT_AUTH_METHOD, PROVIDER_ID).assertEvent();
        HttpResponse logoutResponse = oauth.doLogout(response.getRefreshToken(), "secret");
        Assert.assertEquals(204, logoutResponse.getStatusLine().getStatusCode());
        events.expectLogout(accessToken.getSessionState()).client("resource-owner").removeDetail(REDIRECT_URI).assertEvent();
        response = oauth.doRefreshTokenRequest(response.getRefreshToken(), "secret");
        Assert.assertEquals(400, response.getStatusCode());
        Assert.assertEquals("invalid_grant", response.getError());
        events.expectRefresh(refreshToken.getId(), refreshToken.getSessionState()).client("resource-owner").removeDetail(TOKEN_ID).removeDetail(UPDATED_REFRESH_TOKEN_ID).error(INVALID_TOKEN).assertEvent();
    }

    @Test
    public void grantAccessTokenInvalidClientCredentials() throws Exception {
        oauth.clientId("resource-owner");
        OAuthClient.AccessTokenResponse response = oauth.doGrantAccessTokenRequest("invalid", "test-user@localhost", "password");
        Assert.assertEquals(400, response.getStatusCode());
        Assert.assertEquals("unauthorized_client", response.getError());
        events.expectLogin().client("resource-owner").session(((String) (null))).clearDetails().error(INVALID_CLIENT_CREDENTIALS).user(((String) (null))).assertEvent();
    }

    @Test
    public void grantAccessTokenMissingClientCredentials() throws Exception {
        oauth.clientId("resource-owner");
        OAuthClient.AccessTokenResponse response = oauth.doGrantAccessTokenRequest(null, "test-user@localhost", "password");
        Assert.assertEquals(400, response.getStatusCode());
        Assert.assertEquals("unauthorized_client", response.getError());
        events.expectLogin().client("resource-owner").session(((String) (null))).clearDetails().error(INVALID_CLIENT_CREDENTIALS).user(((String) (null))).assertEvent();
    }

    @Test
    public void grantAccessTokenClientNotAllowed() throws Exception {
        ClientManager.realm(adminClient.realm("test")).clientId("resource-owner").directAccessGrant(false);
        oauth.clientId("resource-owner");
        OAuthClient.AccessTokenResponse response = oauth.doGrantAccessTokenRequest("secret", "test-user@localhost", "password");
        Assert.assertEquals(400, response.getStatusCode());
        Assert.assertEquals("invalid_grant", response.getError());
        events.expectLogin().client("resource-owner").session(((String) (null))).clearDetails().error(NOT_ALLOWED).user(((String) (null))).assertEvent();
        ClientManager.realm(adminClient.realm("test")).clientId("resource-owner").directAccessGrant(true);
    }

    @Test
    public void grantAccessTokenVerifyEmail() throws Exception {
        RealmResource realmResource = adminClient.realm("test");
        RealmManager.realm(realmResource).verifyEmail(true);
        oauth.clientId("resource-owner");
        OAuthClient.AccessTokenResponse response = oauth.doGrantAccessTokenRequest("secret", "test-user@localhost", "password");
        Assert.assertEquals(400, response.getStatusCode());
        Assert.assertEquals("invalid_grant", response.getError());
        Assert.assertEquals("Account is not fully set up", response.getErrorDescription());
        events.expectLogin().client("resource-owner").session(((String) (null))).clearDetails().error(RESOLVE_REQUIRED_ACTIONS).user(((String) (null))).assertEvent();
        RealmManager.realm(realmResource).verifyEmail(false);
        UserManager.realm(realmResource).username("test-user@localhost").removeRequiredAction(VERIFY_EMAIL.toString());
    }

    @Test
    public void grantAccessTokenVerifyEmailInvalidPassword() throws Exception {
        RealmResource realmResource = adminClient.realm("test");
        RealmManager.realm(realmResource).verifyEmail(true);
        oauth.clientId("resource-owner");
        OAuthClient.AccessTokenResponse response = oauth.doGrantAccessTokenRequest("secret", "test-user@localhost", "bad-password");
        Assert.assertEquals(401, response.getStatusCode());
        Assert.assertEquals("invalid_grant", response.getError());
        Assert.assertEquals("Invalid user credentials", response.getErrorDescription());
        events.expectLogin().client("resource-owner").session(((String) (null))).detail(GRANT_TYPE, PASSWORD).removeDetail(CODE_ID).removeDetail(REDIRECT_URI).removeDetail(CONSENT).error(INVALID_USER_CREDENTIALS).assertEvent();
        RealmManager.realm(realmResource).verifyEmail(false);
        UserManager.realm(realmResource).username("test-user@localhost").removeRequiredAction(VERIFY_EMAIL.toString());
    }

    @Test
    public void grantAccessTokenExpiredPassword() throws Exception {
        RealmResource realmResource = adminClient.realm("test");
        RealmManager.realm(realmResource).passwordPolicy("forceExpiredPasswordChange(1)");
        try {
            setTimeOffset(((60 * 60) * 48));
            oauth.clientId("resource-owner");
            OAuthClient.AccessTokenResponse response = oauth.doGrantAccessTokenRequest("secret", "test-user@localhost", "password");
            Assert.assertEquals(400, response.getStatusCode());
            Assert.assertEquals("invalid_grant", response.getError());
            Assert.assertEquals("Account is not fully set up", response.getErrorDescription());
            setTimeOffset(0);
            events.expectLogin().client("resource-owner").session(((String) (null))).clearDetails().error(RESOLVE_REQUIRED_ACTIONS).user(((String) (null))).assertEvent();
        } finally {
            RealmManager.realm(realmResource).passwordPolicy("");
            UserManager.realm(realmResource).username("test-user@localhost").removeRequiredAction(UPDATE_PASSWORD.toString());
        }
    }

    @Test
    public void grantAccessTokenExpiredPasswordInvalidPassword() throws Exception {
        RealmResource realmResource = adminClient.realm("test");
        RealmManager.realm(realmResource).passwordPolicy("forceExpiredPasswordChange(1)");
        try {
            setTimeOffset(((60 * 60) * 48));
            oauth.clientId("resource-owner");
            OAuthClient.AccessTokenResponse response = oauth.doGrantAccessTokenRequest("secret", "test-user@localhost", "bad-password");
            Assert.assertEquals(401, response.getStatusCode());
            Assert.assertEquals("invalid_grant", response.getError());
            Assert.assertEquals("Invalid user credentials", response.getErrorDescription());
            events.expectLogin().client("resource-owner").session(((String) (null))).detail(GRANT_TYPE, PASSWORD).removeDetail(CODE_ID).removeDetail(REDIRECT_URI).removeDetail(CONSENT).error(INVALID_USER_CREDENTIALS).assertEvent();
        } finally {
            RealmManager.realm(realmResource).passwordPolicy("");
            UserManager.realm(realmResource).username("test-user@localhost").removeRequiredAction(UPDATE_PASSWORD.toString());
        }
    }

    @Test
    public void grantAccessTokenInvalidUserCredentials() throws Exception {
        oauth.clientId("resource-owner");
        OAuthClient.AccessTokenResponse response = oauth.doGrantAccessTokenRequest("secret", "test-user@localhost", "invalid");
        Assert.assertEquals(401, response.getStatusCode());
        Assert.assertEquals("invalid_grant", response.getError());
        Assert.assertEquals("Invalid user credentials", response.getErrorDescription());
        events.expectLogin().client("resource-owner").session(((String) (null))).detail(GRANT_TYPE, PASSWORD).removeDetail(CODE_ID).removeDetail(REDIRECT_URI).removeDetail(CONSENT).error(INVALID_USER_CREDENTIALS).assertEvent();
    }

    @Test
    public void grantAccessTokenUserNotFound() throws Exception {
        oauth.clientId("resource-owner");
        OAuthClient.AccessTokenResponse response = oauth.doGrantAccessTokenRequest("secret", "invalid", "invalid");
        Assert.assertEquals(401, response.getStatusCode());
        Assert.assertEquals("invalid_grant", response.getError());
        events.expectLogin().client("resource-owner").user(((String) (null))).session(((String) (null))).detail(GRANT_TYPE, PASSWORD).detail(USERNAME, "invalid").removeDetail(CODE_ID).removeDetail(REDIRECT_URI).removeDetail(CONSENT).error(INVALID_USER_CREDENTIALS).assertEvent();
    }

    @Test
    public void grantAccessTokenMissingGrantType() throws Exception {
        oauth.clientId("resource-owner");
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            HttpPost post = new HttpPost(oauth.getResourceOwnerPasswordCredentialGrantUrl());
            OAuthClient.AccessTokenResponse response = new OAuthClient.AccessTokenResponse(client.execute(post));
            Assert.assertEquals(400, response.getStatusCode());
            Assert.assertEquals("invalid_request", response.getError());
            Assert.assertEquals("Missing form parameter: grant_type", response.getErrorDescription());
        }
    }
}

