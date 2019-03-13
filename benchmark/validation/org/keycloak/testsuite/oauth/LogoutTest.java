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


import HttpHeaders.LOCATION;
import OAuth2Constants.CODE;
import OAuthClient.AccessTokenResponse;
import Status.BAD_REQUEST;
import Status.FOUND;
import Status.NO_CONTENT;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.keycloak.common.util.Time;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.AssertEvents;
import org.keycloak.testsuite.admin.ApiUtil;
import org.keycloak.testsuite.util.RealmBuilder;


/**
 *
 *
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class LogoutTest extends AbstractKeycloakTest {
    @Rule
    public AssertEvents events = new AssertEvents(this);

    @Test
    public void postLogout() throws Exception {
        oauth.doLogin("test-user@localhost", "password");
        String code = oauth.getCurrentQuery().get(CODE);
        oauth.clientSessionState("client-session");
        OAuthClient.AccessTokenResponse tokenResponse = oauth.doAccessTokenRequest(code, "password");
        String refreshTokenString = tokenResponse.getRefreshToken();
        try (CloseableHttpResponse response = oauth.doLogout(refreshTokenString, "password")) {
            Assert.assertThat(response, Matchers.Matchers.statusCodeIsHC(NO_CONTENT));
            Assert.assertNotNull(testingClient.testApp().getAdminLogoutAction());
        }
    }

    @Test
    public void postLogoutExpiredRefreshToken() throws Exception {
        oauth.doLogin("test-user@localhost", "password");
        String code = oauth.getCurrentQuery().get(CODE);
        oauth.clientSessionState("client-session");
        OAuthClient.AccessTokenResponse tokenResponse = oauth.doAccessTokenRequest(code, "password");
        String refreshTokenString = tokenResponse.getRefreshToken();
        adminClient.realm("test").update(RealmBuilder.create().notBefore(((Time.currentTime()) + 1)).build());
        // Logout should succeed with expired refresh token, see KEYCLOAK-3302
        try (CloseableHttpResponse response = oauth.doLogout(refreshTokenString, "password")) {
            Assert.assertThat(response, Matchers.Matchers.statusCodeIsHC(NO_CONTENT));
            Assert.assertNotNull(testingClient.testApp().getAdminLogoutAction());
        }
    }

    @Test
    public void postLogoutFailWithCredentialsOfDifferentClient() throws Exception {
        oauth.doLogin("test-user@localhost", "password");
        String code = oauth.getCurrentQuery().get(CODE);
        oauth.clientSessionState("client-session");
        OAuthClient.AccessTokenResponse tokenResponse = oauth.doAccessTokenRequest(code, "password");
        String refreshTokenString = tokenResponse.getRefreshToken();
        oauth.clientId("test-app-scope");
        // Assert logout fails with 400 when trying to use different client credentials
        try (CloseableHttpResponse response = oauth.doLogout(refreshTokenString, "password")) {
            Assert.assertThat(response, Matchers.Matchers.statusCodeIsHC(BAD_REQUEST));
        }
        oauth.clientId("test-app");
    }

    @Test
    public void postLogoutWithValidIdToken() throws Exception {
        oauth.doLogin("test-user@localhost", "password");
        String code = oauth.getCurrentQuery().get(CODE);
        oauth.clientSessionState("client-session");
        OAuthClient.AccessTokenResponse tokenResponse = oauth.doAccessTokenRequest(code, "password");
        String idTokenString = tokenResponse.getIdToken();
        String logoutUrl = oauth.getLogoutUrl().idTokenHint(idTokenString).postLogoutRedirectUri(oauth.APP_AUTH_ROOT).build();
        try (CloseableHttpClient c = HttpClientBuilder.create().disableRedirectHandling().build();CloseableHttpResponse response = c.execute(new HttpGet(logoutUrl))) {
            Assert.assertThat(response, Matchers.Matchers.statusCodeIsHC(FOUND));
            Assert.assertThat(response.getFirstHeader(LOCATION).getValue(), is(oauth.APP_AUTH_ROOT));
        }
    }

    @Test
    public void postLogoutWithExpiredIdToken() throws Exception {
        oauth.doLogin("test-user@localhost", "password");
        String code = oauth.getCurrentQuery().get(CODE);
        oauth.clientSessionState("client-session");
        OAuthClient.AccessTokenResponse tokenResponse = oauth.doAccessTokenRequest(code, "password");
        String idTokenString = tokenResponse.getIdToken();
        // Logout should succeed with expired ID token, see KEYCLOAK-3399
        setTimeOffset(((60 * 60) * 24));
        String logoutUrl = oauth.getLogoutUrl().idTokenHint(idTokenString).postLogoutRedirectUri(oauth.APP_AUTH_ROOT).build();
        try (CloseableHttpClient c = HttpClientBuilder.create().disableRedirectHandling().build();CloseableHttpResponse response = c.execute(new HttpGet(logoutUrl))) {
            Assert.assertThat(response, Matchers.Matchers.statusCodeIsHC(FOUND));
            Assert.assertThat(response.getFirstHeader(LOCATION).getValue(), is(oauth.APP_AUTH_ROOT));
        }
    }

    @Test
    public void postLogoutWithValidIdTokenWhenLoggedOutByAdmin() throws Exception {
        oauth.doLogin("test-user@localhost", "password");
        String code = oauth.getCurrentQuery().get(CODE);
        oauth.clientSessionState("client-session");
        OAuthClient.AccessTokenResponse tokenResponse = oauth.doAccessTokenRequest(code, "password");
        String idTokenString = tokenResponse.getIdToken();
        adminClient.realm("test").logoutAll();
        // Logout should succeed with user already logged out, see KEYCLOAK-3399
        String logoutUrl = oauth.getLogoutUrl().idTokenHint(idTokenString).postLogoutRedirectUri(oauth.APP_AUTH_ROOT).build();
        try (CloseableHttpClient c = HttpClientBuilder.create().disableRedirectHandling().build();CloseableHttpResponse response = c.execute(new HttpGet(logoutUrl))) {
            Assert.assertThat(response, Matchers.Matchers.statusCodeIsHC(FOUND));
            Assert.assertThat(response.getFirstHeader(LOCATION).getValue(), is(oauth.APP_AUTH_ROOT));
        }
    }

    @Test
    public void backchannelLogoutRequest_RealmRS384_ClientRS512() throws Exception {
        try {
            TokenSignatureUtil.changeRealmTokenSignatureProvider(adminClient, "RS384");
            TokenSignatureUtil.changeClientAccessTokenSignatureProvider(ApiUtil.findClientByClientId(adminClient.realm("test"), "test-app"), "RS512");
            backchannelLogoutRequest("HS256", "RS512", "RS384");
        } finally {
            TokenSignatureUtil.changeRealmTokenSignatureProvider(adminClient, "RS256");
            TokenSignatureUtil.changeClientAccessTokenSignatureProvider(ApiUtil.findClientByClientId(adminClient.realm("test"), "test-app"), "RS256");
        }
    }
}

