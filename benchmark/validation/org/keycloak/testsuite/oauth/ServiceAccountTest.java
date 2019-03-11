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
import Details.REDIRECT_URI;
import Details.REFRESH_TOKEN_ID;
import Details.RESPONSE_TYPE;
import Details.TOKEN_ID;
import Details.UPDATED_REFRESH_TOKEN_ID;
import Details.USERNAME;
import Errors.INVALID_CLIENT;
import Errors.INVALID_CLIENT_CREDENTIALS;
import Errors.INVALID_TOKEN;
import OAuthClient.AccessTokenResponse;
import ServiceAccountConstants.CLIENT_ADDRESS;
import ServiceAccountConstants.CLIENT_HOST;
import ServiceAccountConstants.CLIENT_ID;
import org.apache.http.HttpResponse;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.keycloak.common.constants.ServiceAccountConstants;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.RefreshToken;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.AssertEvents;
import org.keycloak.testsuite.util.ClientManager;
import org.keycloak.testsuite.util.OAuthClient;


/**
 *
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class ServiceAccountTest extends AbstractKeycloakTest {
    private static String userId;

    private static String userName;

    @Rule
    public AssertEvents events = new AssertEvents(this);

    @Test
    public void clientCredentialsAuthSuccess() throws Exception {
        oauth.clientId("service-account-cl");
        OAuthClient.AccessTokenResponse response = oauth.doClientCredentialsGrantAccessTokenRequest("secret1");
        Assert.assertEquals(200, response.getStatusCode());
        AccessToken accessToken = oauth.verifyToken(response.getAccessToken());
        RefreshToken refreshToken = oauth.parseRefreshToken(response.getRefreshToken());
        events.expectClientLogin().client("service-account-cl").user(ServiceAccountTest.userId).session(accessToken.getSessionState()).detail(TOKEN_ID, accessToken.getId()).detail(REFRESH_TOKEN_ID, refreshToken.getId()).detail(USERNAME, ServiceAccountTest.userName).assertEvent();
        Assert.assertEquals(accessToken.getSessionState(), refreshToken.getSessionState());
        System.out.println(("Access token other claims: " + (accessToken.getOtherClaims())));
        Assert.assertEquals("service-account-cl", accessToken.getOtherClaims().get(CLIENT_ID));
        Assert.assertTrue(accessToken.getOtherClaims().containsKey(CLIENT_ADDRESS));
        Assert.assertTrue(accessToken.getOtherClaims().containsKey(CLIENT_HOST));
        OAuthClient.AccessTokenResponse refreshedResponse = oauth.doRefreshTokenRequest(response.getRefreshToken(), "secret1");
        AccessToken refreshedAccessToken = oauth.verifyToken(refreshedResponse.getAccessToken());
        RefreshToken refreshedRefreshToken = oauth.parseRefreshToken(refreshedResponse.getRefreshToken());
        Assert.assertEquals(accessToken.getSessionState(), refreshedAccessToken.getSessionState());
        Assert.assertEquals(accessToken.getSessionState(), refreshedRefreshToken.getSessionState());
        events.expectRefresh(refreshToken.getId(), refreshToken.getSessionState()).user(ServiceAccountTest.userId).client("service-account-cl").assertEvent();
    }

    @Test
    public void clientCredentialsLogout() throws Exception {
        oauth.clientId("service-account-cl");
        OAuthClient.AccessTokenResponse response = oauth.doClientCredentialsGrantAccessTokenRequest("secret1");
        Assert.assertEquals(200, response.getStatusCode());
        AccessToken accessToken = oauth.verifyToken(response.getAccessToken());
        RefreshToken refreshToken = oauth.parseRefreshToken(response.getRefreshToken());
        events.expectClientLogin().client("service-account-cl").user(ServiceAccountTest.userId).session(accessToken.getSessionState()).detail(TOKEN_ID, accessToken.getId()).detail(REFRESH_TOKEN_ID, refreshToken.getId()).detail(USERNAME, ServiceAccountTest.userName).detail(CLIENT_AUTH_METHOD, PROVIDER_ID).assertEvent();
        HttpResponse logoutResponse = oauth.doLogout(response.getRefreshToken(), "secret1");
        Assert.assertEquals(204, logoutResponse.getStatusLine().getStatusCode());
        events.expectLogout(accessToken.getSessionState()).client("service-account-cl").user(ServiceAccountTest.userId).removeDetail(REDIRECT_URI).assertEvent();
        response = oauth.doRefreshTokenRequest(response.getRefreshToken(), "secret1");
        Assert.assertEquals(400, response.getStatusCode());
        Assert.assertEquals("invalid_grant", response.getError());
        events.expectRefresh(refreshToken.getId(), refreshToken.getSessionState()).client("service-account-cl").user(ServiceAccountTest.userId).removeDetail(TOKEN_ID).removeDetail(UPDATED_REFRESH_TOKEN_ID).error(INVALID_TOKEN).assertEvent();
    }

    @Test
    public void clientCredentialsInvalidClientCredentials() throws Exception {
        oauth.clientId("service-account-cl");
        OAuthClient.AccessTokenResponse response = oauth.doClientCredentialsGrantAccessTokenRequest("secret2");
        Assert.assertEquals(400, response.getStatusCode());
        Assert.assertEquals("unauthorized_client", response.getError());
        events.expectClientLogin().client("service-account-cl").session(((String) (null))).clearDetails().error(INVALID_CLIENT_CREDENTIALS).user(((String) (null))).assertEvent();
    }

    @Test
    public void clientCredentialsDisabledServiceAccount() throws Exception {
        oauth.clientId("service-account-disabled");
        OAuthClient.AccessTokenResponse response = oauth.doClientCredentialsGrantAccessTokenRequest("secret1");
        Assert.assertEquals(401, response.getStatusCode());
        Assert.assertEquals("unauthorized_client", response.getError());
        events.expectClientLogin().client("service-account-disabled").user(((String) (null))).session(((String) (null))).removeDetail(USERNAME).removeDetail(RESPONSE_TYPE).error(INVALID_CLIENT).assertEvent();
    }

    @Test
    public void changeClientIdTest() throws Exception {
        ClientManager.realm(adminClient.realm("test")).clientId("service-account-cl").renameTo("updated-client");
        oauth.clientId("updated-client");
        OAuthClient.AccessTokenResponse response = oauth.doClientCredentialsGrantAccessTokenRequest("secret1");
        Assert.assertEquals(200, response.getStatusCode());
        AccessToken accessToken = oauth.verifyToken(response.getAccessToken());
        RefreshToken refreshToken = oauth.parseRefreshToken(response.getRefreshToken());
        Assert.assertEquals("updated-client", accessToken.getOtherClaims().get(CLIENT_ID));
        // Username updated after client ID changed
        events.expectClientLogin().client("updated-client").user(ServiceAccountTest.userId).session(accessToken.getSessionState()).detail(TOKEN_ID, accessToken.getId()).detail(REFRESH_TOKEN_ID, refreshToken.getId()).detail(USERNAME, ((ServiceAccountConstants.SERVICE_ACCOUNT_USER_PREFIX) + "updated-client")).assertEvent();
        ClientManager.realm(adminClient.realm("test")).clientId("updated-client").renameTo("service-account-cl");
    }

    @Test
    public void refreshTokenRefreshForDisabledServiceAccount() throws Exception {
        try {
            oauth.clientId("service-account-cl");
            OAuthClient.AccessTokenResponse response = oauth.doClientCredentialsGrantAccessTokenRequest("secret1");
            Assert.assertEquals(200, response.getStatusCode());
            ClientManager.realm(adminClient.realm("test")).clientId("service-account-cl").setServiceAccountsEnabled(false);
            response = oauth.doRefreshTokenRequest(response.getRefreshToken(), "secret1");
            Assert.assertEquals(400, response.getStatusCode());
        } finally {
            ClientManager.realm(adminClient.realm("test")).clientId("service-account-cl").setServiceAccountsEnabled(true);
            UserRepresentation user = ClientManager.realm(adminClient.realm("test")).clientId("service-account-cl").getServiceAccountUser();
            ServiceAccountTest.userId = user.getId();
            ServiceAccountTest.userName = user.getUsername();
        }
    }
}

