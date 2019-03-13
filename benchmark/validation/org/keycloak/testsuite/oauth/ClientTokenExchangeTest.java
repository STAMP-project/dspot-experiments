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


import HttpHeaders.AUTHORIZATION;
import OAuth2Constants.ACCESS_TOKEN_TYPE;
import OAuth2Constants.AUDIENCE;
import OAuth2Constants.GRANT_TYPE;
import OAuth2Constants.REQUESTED_SUBJECT;
import OAuth2Constants.SUBJECT_TOKEN;
import OAuth2Constants.SUBJECT_TOKEN_TYPE;
import OAuth2Constants.TOKEN_EXCHANGE_GRANT_TYPE;
import OAuthClient.AUTH_SERVER_ROOT;
import Profile.Feature.TOKEN_EXCHANGE;
import java.util.Map;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.keycloak.TokenVerifier;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.AssertEvents;
import org.keycloak.testsuite.ProfileAssume;
import org.keycloak.testsuite.util.OAuthClient;
import org.keycloak.util.BasicAuthHelper;


/**
 *
 *
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class ClientTokenExchangeTest extends AbstractKeycloakTest {
    @Rule
    public AssertEvents events = new AssertEvents(this);

    @Test
    public void testExchange() throws Exception {
        ProfileAssume.assumeFeatureEnabled(TOKEN_EXCHANGE);
        testingClient.server().run(ClientTokenExchangeTest::setupRealm);
        oauth.realm(TEST);
        oauth.clientId("client-exchanger");
        OAuthClient.AccessTokenResponse response = oauth.doGrantAccessTokenRequest("secret", "user", "password");
        String accessToken = response.getAccessToken();
        TokenVerifier<AccessToken> accessTokenVerifier = TokenVerifier.create(accessToken, AccessToken.class);
        AccessToken token = accessTokenVerifier.parse().getToken();
        org.keycloak.testsuite.Assert.assertEquals(token.getPreferredUsername(), "user");
        org.keycloak.testsuite.Assert.assertTrue((((token.getRealmAccess()) == null) || (!(token.getRealmAccess().isUserInRole("example")))));
        {
            response = oauth.doTokenExchange(TEST, accessToken, "target", "client-exchanger", "secret");
            String exchangedTokenString = response.getAccessToken();
            TokenVerifier<AccessToken> verifier = TokenVerifier.create(exchangedTokenString, AccessToken.class);
            AccessToken exchangedToken = verifier.parse().getToken();
            org.keycloak.testsuite.Assert.assertEquals("client-exchanger", exchangedToken.getIssuedFor());
            org.keycloak.testsuite.Assert.assertEquals("target", exchangedToken.getAudience()[0]);
            org.keycloak.testsuite.Assert.assertEquals(exchangedToken.getPreferredUsername(), "user");
            org.keycloak.testsuite.Assert.assertTrue(exchangedToken.getRealmAccess().isUserInRole("example"));
        }
        {
            response = oauth.doTokenExchange(TEST, accessToken, "target", "legal", "secret");
            String exchangedTokenString = response.getAccessToken();
            TokenVerifier<AccessToken> verifier = TokenVerifier.create(exchangedTokenString, AccessToken.class);
            AccessToken exchangedToken = verifier.parse().getToken();
            org.keycloak.testsuite.Assert.assertEquals("legal", exchangedToken.getIssuedFor());
            org.keycloak.testsuite.Assert.assertEquals("target", exchangedToken.getAudience()[0]);
            org.keycloak.testsuite.Assert.assertEquals(exchangedToken.getPreferredUsername(), "user");
            org.keycloak.testsuite.Assert.assertTrue(exchangedToken.getRealmAccess().isUserInRole("example"));
        }
        {
            response = oauth.doTokenExchange(TEST, accessToken, "target", "illegal", "secret");
            org.keycloak.testsuite.Assert.assertEquals(403, response.getStatusCode());
        }
    }

    @Test
    public void testImpersonation() throws Exception {
        ProfileAssume.assumeFeatureEnabled(TOKEN_EXCHANGE);
        testingClient.server().run(ClientTokenExchangeTest::setupRealm);
        oauth.realm(TEST);
        oauth.clientId("client-exchanger");
        Client httpClient = ClientBuilder.newClient();
        WebTarget exchangeUrl = httpClient.target(AUTH_SERVER_ROOT).path("/realms").path(TEST).path("protocol/openid-connect/token");
        System.out.println(("Exchange url: " + (exchangeUrl.getUri().toString())));
        OAuthClient.AccessTokenResponse tokenResponse = oauth.doGrantAccessTokenRequest("secret", "user", "password");
        String accessToken = tokenResponse.getAccessToken();
        TokenVerifier<AccessToken> accessTokenVerifier = TokenVerifier.create(accessToken, AccessToken.class);
        AccessToken token = accessTokenVerifier.parse().getToken();
        org.keycloak.testsuite.Assert.assertEquals(token.getPreferredUsername(), "user");
        org.keycloak.testsuite.Assert.assertTrue((((token.getRealmAccess()) == null) || (!(token.getRealmAccess().isUserInRole("example")))));
        // client-exchanger can impersonate from token "user" to user "impersonated-user"
        {
            Response response = exchangeUrl.request().header(AUTHORIZATION, BasicAuthHelper.createHeader("client-exchanger", "secret")).post(Entity.form(new Form().param(GRANT_TYPE, TOKEN_EXCHANGE_GRANT_TYPE).param(SUBJECT_TOKEN, accessToken).param(SUBJECT_TOKEN_TYPE, ACCESS_TOKEN_TYPE).param(REQUESTED_SUBJECT, "impersonated-user")));
            Assert.assertEquals(200, response.getStatus());
            AccessTokenResponse accessTokenResponse = response.readEntity(AccessTokenResponse.class);
            response.close();
            String exchangedTokenString = accessTokenResponse.getToken();
            TokenVerifier<AccessToken> verifier = TokenVerifier.create(exchangedTokenString, AccessToken.class);
            AccessToken exchangedToken = verifier.parse().getToken();
            org.keycloak.testsuite.Assert.assertEquals("client-exchanger", exchangedToken.getIssuedFor());
            org.keycloak.testsuite.Assert.assertNull(exchangedToken.getAudience());
            org.keycloak.testsuite.Assert.assertEquals("impersonated-user", exchangedToken.getPreferredUsername());
            org.keycloak.testsuite.Assert.assertNull(exchangedToken.getRealmAccess());
            Object impersonatorRaw = exchangedToken.getOtherClaims().get("impersonator");
            org.keycloak.testsuite.Assert.assertThat(impersonatorRaw, Matchers.instanceOf(Map.class));
            Map impersonatorClaim = ((Map) (impersonatorRaw));
            org.keycloak.testsuite.Assert.assertEquals(token.getSubject(), impersonatorClaim.get("id"));
            org.keycloak.testsuite.Assert.assertEquals("user", impersonatorClaim.get("username"));
        }
        // client-exchanger can impersonate from token "user" to user "impersonated-user" and to "target" client
        {
            Response response = exchangeUrl.request().header(AUTHORIZATION, BasicAuthHelper.createHeader("client-exchanger", "secret")).post(Entity.form(new Form().param(GRANT_TYPE, TOKEN_EXCHANGE_GRANT_TYPE).param(SUBJECT_TOKEN, accessToken).param(SUBJECT_TOKEN_TYPE, ACCESS_TOKEN_TYPE).param(REQUESTED_SUBJECT, "impersonated-user").param(AUDIENCE, "target")));
            Assert.assertEquals(200, response.getStatus());
            AccessTokenResponse accessTokenResponse = response.readEntity(AccessTokenResponse.class);
            response.close();
            String exchangedTokenString = accessTokenResponse.getToken();
            TokenVerifier<AccessToken> verifier = TokenVerifier.create(exchangedTokenString, AccessToken.class);
            AccessToken exchangedToken = verifier.parse().getToken();
            org.keycloak.testsuite.Assert.assertEquals("client-exchanger", exchangedToken.getIssuedFor());
            org.keycloak.testsuite.Assert.assertEquals("target", exchangedToken.getAudience()[0]);
            org.keycloak.testsuite.Assert.assertEquals(exchangedToken.getPreferredUsername(), "impersonated-user");
            org.keycloak.testsuite.Assert.assertTrue(exchangedToken.getRealmAccess().isUserInRole("example"));
        }
    }

    @Test
    public void testBadImpersonator() throws Exception {
        ProfileAssume.assumeFeatureEnabled(TOKEN_EXCHANGE);
        testingClient.server().run(ClientTokenExchangeTest::setupRealm);
        oauth.realm(TEST);
        oauth.clientId("client-exchanger");
        Client httpClient = ClientBuilder.newClient();
        WebTarget exchangeUrl = httpClient.target(AUTH_SERVER_ROOT).path("/realms").path(TEST).path("protocol/openid-connect/token");
        System.out.println(("Exchange url: " + (exchangeUrl.getUri().toString())));
        OAuthClient.AccessTokenResponse tokenResponse = oauth.doGrantAccessTokenRequest("secret", "bad-impersonator", "password");
        String accessToken = tokenResponse.getAccessToken();
        TokenVerifier<AccessToken> accessTokenVerifier = TokenVerifier.create(accessToken, AccessToken.class);
        AccessToken token = accessTokenVerifier.parse().getToken();
        org.keycloak.testsuite.Assert.assertEquals(token.getPreferredUsername(), "bad-impersonator");
        org.keycloak.testsuite.Assert.assertTrue((((token.getRealmAccess()) == null) || (!(token.getRealmAccess().isUserInRole("example")))));
        // test that user does not have impersonator permission
        {
            Response response = exchangeUrl.request().header(AUTHORIZATION, BasicAuthHelper.createHeader("client-exchanger", "secret")).post(Entity.form(new Form().param(GRANT_TYPE, TOKEN_EXCHANGE_GRANT_TYPE).param(SUBJECT_TOKEN, accessToken).param(SUBJECT_TOKEN_TYPE, ACCESS_TOKEN_TYPE).param(REQUESTED_SUBJECT, "impersonated-user")));
            Assert.assertEquals(403, response.getStatus());
            response.close();
        }
    }

    @Test
    public void testDirectImpersonation() throws Exception {
        ProfileAssume.assumeFeatureEnabled(TOKEN_EXCHANGE);
        testingClient.server().run(ClientTokenExchangeTest::setupRealm);
        Client httpClient = ClientBuilder.newClient();
        WebTarget exchangeUrl = httpClient.target(AUTH_SERVER_ROOT).path("/realms").path(TEST).path("protocol/openid-connect/token");
        System.out.println(("Exchange url: " + (exchangeUrl.getUri().toString())));
        // direct-exchanger can impersonate from token "user" to user "impersonated-user"
        {
            Response response = exchangeUrl.request().header(AUTHORIZATION, BasicAuthHelper.createHeader("direct-exchanger", "secret")).post(Entity.form(new Form().param(GRANT_TYPE, TOKEN_EXCHANGE_GRANT_TYPE).param(REQUESTED_SUBJECT, "impersonated-user")));
            Assert.assertEquals(200, response.getStatus());
            AccessTokenResponse accessTokenResponse = response.readEntity(AccessTokenResponse.class);
            response.close();
            String exchangedTokenString = accessTokenResponse.getToken();
            TokenVerifier<AccessToken> verifier = TokenVerifier.create(exchangedTokenString, AccessToken.class);
            AccessToken exchangedToken = verifier.parse().getToken();
            org.keycloak.testsuite.Assert.assertEquals("direct-exchanger", exchangedToken.getIssuedFor());
            org.keycloak.testsuite.Assert.assertNull(exchangedToken.getAudience());
            org.keycloak.testsuite.Assert.assertEquals(exchangedToken.getPreferredUsername(), "impersonated-user");
            org.keycloak.testsuite.Assert.assertNull(exchangedToken.getRealmAccess());
        }
        // direct-legal can impersonate from token "user" to user "impersonated-user" and to "target" client
        {
            Response response = exchangeUrl.request().header(AUTHORIZATION, BasicAuthHelper.createHeader("direct-legal", "secret")).post(Entity.form(new Form().param(GRANT_TYPE, TOKEN_EXCHANGE_GRANT_TYPE).param(REQUESTED_SUBJECT, "impersonated-user").param(AUDIENCE, "target")));
            Assert.assertEquals(200, response.getStatus());
            AccessTokenResponse accessTokenResponse = response.readEntity(AccessTokenResponse.class);
            response.close();
            String exchangedTokenString = accessTokenResponse.getToken();
            TokenVerifier<AccessToken> verifier = TokenVerifier.create(exchangedTokenString, AccessToken.class);
            AccessToken exchangedToken = verifier.parse().getToken();
            org.keycloak.testsuite.Assert.assertEquals("direct-legal", exchangedToken.getIssuedFor());
            org.keycloak.testsuite.Assert.assertEquals("target", exchangedToken.getAudience()[0]);
            org.keycloak.testsuite.Assert.assertEquals(exchangedToken.getPreferredUsername(), "impersonated-user");
            org.keycloak.testsuite.Assert.assertTrue(exchangedToken.getRealmAccess().isUserInRole("example"));
        }
        // direct-public fails impersonation
        {
            Response response = exchangeUrl.request().header(AUTHORIZATION, BasicAuthHelper.createHeader("direct-public", "secret")).post(Entity.form(new Form().param(GRANT_TYPE, TOKEN_EXCHANGE_GRANT_TYPE).param(REQUESTED_SUBJECT, "impersonated-user").param(AUDIENCE, "target")));
            org.keycloak.testsuite.Assert.assertEquals(403, response.getStatus());
            response.close();
        }
        // direct-no-secret fails impersonation
        {
            Response response = exchangeUrl.request().header(AUTHORIZATION, BasicAuthHelper.createHeader("direct-no-secret", "secret")).post(Entity.form(new Form().param(GRANT_TYPE, TOKEN_EXCHANGE_GRANT_TYPE).param(REQUESTED_SUBJECT, "impersonated-user").param(AUDIENCE, "target")));
            org.keycloak.testsuite.Assert.assertTrue(((response.getStatus()) >= 400));
            response.close();
        }
    }
}

