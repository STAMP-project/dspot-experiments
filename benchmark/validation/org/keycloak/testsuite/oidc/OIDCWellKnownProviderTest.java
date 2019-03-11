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
package org.keycloak.testsuite.oidc;


import Cors.ACCESS_CONTROL_ALLOW_ORIGIN;
import Cors.ORIGIN_HEADER;
import IDToken.EMAIL;
import IDToken.FAMILY_NAME;
import IDToken.NAME;
import IDToken.PREFERRED_USERNAME;
import Invocation.Builder;
import OAuth2Constants.AUTHORIZATION_CODE;
import OAuth2Constants.CODE;
import OAuth2Constants.IMPLICIT;
import OAuthClient.AUTH_SERVER_ROOT;
import OAuthClient.AccessTokenResponse;
import OAuthClient.AuthorizationEndpointResponse;
import OIDCClientRegistrationProviderFactory.ID;
import OIDCResponseType.ID_TOKEN;
import OIDCWellKnownProviderFactory.PROVIDER_ID;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.net.URI;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.broker.provider.util.SimpleHttp;
import org.keycloak.jose.jwk.JSONWebKeySet;
import org.keycloak.protocol.oidc.OIDCLoginProtocolService;
import org.keycloak.protocol.oidc.representations.OIDCConfigurationRepresentation;
import org.keycloak.representations.IDToken;
import org.keycloak.services.clientregistration.ClientRegistrationService;
import org.keycloak.services.resources.RealmsResource;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.util.OAuthClient;
import org.keycloak.testsuite.util.TokenSignatureUtil;
import org.keycloak.util.JsonSerialization;

import static org.keycloak.testsuite.Assert.assertNames;


/**
 *
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class OIDCWellKnownProviderTest extends AbstractKeycloakTest {
    private CloseableHttpClient client;

    @Test
    public void testDiscovery() {
        Client client = ClientBuilder.newClient();
        try {
            OIDCConfigurationRepresentation oidcConfig = getOIDCDiscoveryRepresentation(client);
            // URIs are filled
            Assert.assertEquals(oidcConfig.getAuthorizationEndpoint(), OIDCLoginProtocolService.authUrl(UriBuilder.fromUri(AUTH_SERVER_ROOT)).build("test").toString());
            Assert.assertEquals(oidcConfig.getTokenEndpoint(), oauth.getAccessTokenUrl());
            Assert.assertEquals(oidcConfig.getUserinfoEndpoint(), OIDCLoginProtocolService.userInfoUrl(UriBuilder.fromUri(AUTH_SERVER_ROOT)).build("test").toString());
            Assert.assertEquals(oidcConfig.getJwksUri(), oauth.getCertsUrl("test"));
            String registrationUri = UriBuilder.fromUri(AUTH_SERVER_ROOT).path(RealmsResource.class).path(RealmsResource.class, "getClientsService").path(ClientRegistrationService.class, "provider").build("test", ID).toString();
            Assert.assertEquals(oidcConfig.getRegistrationEndpoint(), registrationUri);
            // Support standard + implicit + hybrid flow
            assertContains(oidcConfig.getResponseTypesSupported(), CODE, ID_TOKEN, "id_token token", "code id_token", "code token", "code id_token token");
            assertContains(oidcConfig.getGrantTypesSupported(), AUTHORIZATION_CODE, IMPLICIT);
            assertContains(oidcConfig.getResponseModesSupported(), "query", "fragment");
            assertNames(oidcConfig.getSubjectTypesSupported(), "pairwise", "public");
            assertNames(oidcConfig.getIdTokenSigningAlgValuesSupported(), Algorithm.RS256, Algorithm.RS384, Algorithm.RS512, Algorithm.ES256, Algorithm.ES384, Algorithm.ES512, Algorithm.HS256, Algorithm.HS384, Algorithm.HS512);
            assertNames(oidcConfig.getUserInfoSigningAlgValuesSupported(), "none", Algorithm.RS256, Algorithm.RS384, Algorithm.RS512, Algorithm.ES256, Algorithm.ES384, Algorithm.ES512, Algorithm.HS256, Algorithm.HS384, Algorithm.HS512);
            assertNames(oidcConfig.getRequestObjectSigningAlgValuesSupported(), "none", Algorithm.RS256, Algorithm.RS384, Algorithm.RS512, Algorithm.ES256, Algorithm.ES384, Algorithm.ES512);
            // Client authentication
            assertNames(oidcConfig.getTokenEndpointAuthMethodsSupported(), "client_secret_basic", "client_secret_post", "private_key_jwt", "client_secret_jwt");
            assertNames(oidcConfig.getTokenEndpointAuthSigningAlgValuesSupported(), Algorithm.RS256);
            // Claims
            assertContains(oidcConfig.getClaimsSupported(), NAME, EMAIL, PREFERRED_USERNAME, FAMILY_NAME);
            assertNames(oidcConfig.getClaimTypesSupported(), "normal");
            org.keycloak.testsuite.Assert.assertFalse(oidcConfig.getClaimsParameterSupported());
            // Scopes supported
            assertNames(oidcConfig.getScopesSupported(), OAuth2Constants.SCOPE_OPENID, OAuth2Constants.OFFLINE_ACCESS, OAuth2Constants.SCOPE_PROFILE, OAuth2Constants.SCOPE_EMAIL, OAuth2Constants.SCOPE_PHONE, OAuth2Constants.SCOPE_ADDRESS, OIDCLoginProtocolFactory.ROLES_SCOPE, OIDCLoginProtocolFactory.WEB_ORIGINS_SCOPE);
            // Request and Request_Uri
            org.keycloak.testsuite.Assert.assertTrue(oidcConfig.getRequestParameterSupported());
            org.keycloak.testsuite.Assert.assertTrue(oidcConfig.getRequestUriParameterSupported());
            // KEYCLOAK-7451 OAuth Authorization Server Metadata for Proof Key for Code Exchange
            // PKCE support
            assertNames(oidcConfig.getCodeChallengeMethodsSupported(), OAuth2Constants.PKCE_METHOD_PLAIN, OAuth2Constants.PKCE_METHOD_S256);
            // KEYCLOAK-6771 Certificate Bound Token
            // https://tools.ietf.org/html/draft-ietf-oauth-mtls-08#section-6.2
            org.keycloak.testsuite.Assert.assertTrue(oidcConfig.getTlsClientCertificateBoundAccessTokens());
        } finally {
            client.close();
        }
    }

    @Test
    public void testIssuerMatches() throws Exception {
        OAuthClient.AuthorizationEndpointResponse authzResp = oauth.doLogin("test-user@localhost", "password");
        OAuthClient.AccessTokenResponse response = oauth.doAccessTokenRequest(authzResp.getCode(), "password");
        Assert.assertEquals(200, response.getStatusCode());
        IDToken idToken = oauth.verifyIDToken(response.getIdToken());
        Client client = ClientBuilder.newClient();
        try {
            OIDCConfigurationRepresentation oidcConfig = getOIDCDiscoveryRepresentation(client);
            // assert issuer matches
            Assert.assertEquals(idToken.getIssuer(), oidcConfig.getIssuer());
        } finally {
            client.close();
        }
    }

    @Test
    public void corsTest() {
        Client client = ClientBuilder.newClient();
        UriBuilder builder = UriBuilder.fromUri(AUTH_SERVER_ROOT);
        URI oidcDiscoveryUri = RealmsResource.wellKnownProviderUrl(builder).build("test", PROVIDER_ID);
        WebTarget oidcDiscoveryTarget = client.target(oidcDiscoveryUri);
        Invocation.Builder request = oidcDiscoveryTarget.request();
        request.header(ORIGIN_HEADER, "http://somehost");
        Response response = request.get();
        Assert.assertEquals("http://somehost", response.getHeaders().getFirst(ACCESS_CONTROL_ALLOW_ORIGIN));
    }

    @Test
    public void certs() throws IOException {
        TokenSignatureUtil.registerKeyProvider("P-256", adminClient, testContext);
        OIDCConfigurationRepresentation representation = SimpleHttp.doGet(((getAuthServerRoot().toString()) + "realms/test/.well-known/openid-configuration"), client).asJson(OIDCConfigurationRepresentation.class);
        String jwksUri = representation.getJwksUri();
        JSONWebKeySet jsonWebKeySet = SimpleHttp.doGet(jwksUri, client).asJson(JSONWebKeySet.class);
        Assert.assertEquals(2, jsonWebKeySet.getKeys().length);
    }

    @Test
    public void testIntrospectionEndpointClaim() throws IOException {
        Client client = ClientBuilder.newClient();
        try {
            ObjectNode oidcConfig = JsonSerialization.readValue(getOIDCDiscoveryConfiguration(client), ObjectNode.class);
            Assert.assertEquals(oidcConfig.get("introspection_endpoint").asText(), getOIDCDiscoveryRepresentation(client).getTokenIntrospectionEndpoint());
        } finally {
            client.close();
        }
    }
}

