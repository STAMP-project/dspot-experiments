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
import Algorithm.RS256;
import Errors.INVALID_CLIENT;
import OAuth2Constants.CODE;
import OAuth2Constants.OFFLINE_ACCESS;
import OAuthErrorException.INVALID_REQUEST;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.keycloak.representations.idm.EventRepresentation;
import org.keycloak.representations.idm.OAuth2ErrorRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.oidc.TokenMetadataRepresentation;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.AbstractTestRealmKeycloakTest;
import org.keycloak.testsuite.AssertEvents;
import org.keycloak.testsuite.admin.ApiUtil;
import org.keycloak.testsuite.oidc.AbstractOIDCScopeTest;
import org.keycloak.testsuite.oidc.OIDCScopeTest;
import org.keycloak.testsuite.util.OAuthClient.AccessTokenResponse;
import org.keycloak.testsuite.util.TokenSignatureUtil;
import org.keycloak.util.JsonSerialization;

import static org.keycloak.testsuite.Assert.assertNames;


/**
 *
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Igor</a>
 * @author Stan Silvert ssilvert@redhat.com (C) 2016 Red Hat Inc.
 */
public class TokenIntrospectionTest extends AbstractTestRealmKeycloakTest {
    @Rule
    public AssertEvents events = new AssertEvents(this);

    @Test
    public void testConfidentialClientCredentialsBasicAuthentication() throws Exception {
        oauth.doLogin("test-user@localhost", "password");
        String code = oauth.getCurrentQuery().get(CODE);
        AccessTokenResponse accessTokenResponse = oauth.doAccessTokenRequest(code, "password");
        String tokenResponse = oauth.introspectAccessTokenWithClientCredential("confidential-cli", "secret1", accessTokenResponse.getAccessToken());
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(tokenResponse);
        Assert.assertTrue(jsonNode.get("active").asBoolean());
        Assert.assertEquals("test-user@localhost", jsonNode.get("username").asText());
        Assert.assertEquals("test-app", jsonNode.get("client_id").asText());
        Assert.assertTrue(jsonNode.has("exp"));
        Assert.assertTrue(jsonNode.has("iat"));
        Assert.assertTrue(jsonNode.has("nbf"));
        Assert.assertTrue(jsonNode.has("sub"));
        Assert.assertTrue(jsonNode.has("aud"));
        Assert.assertTrue(jsonNode.has("iss"));
        Assert.assertTrue(jsonNode.has("jti"));
        TokenMetadataRepresentation rep = objectMapper.readValue(tokenResponse, TokenMetadataRepresentation.class);
        Assert.assertTrue(rep.isActive());
        Assert.assertEquals("test-user@localhost", rep.getUserName());
        Assert.assertEquals("test-app", rep.getClientId());
        Assert.assertEquals(jsonNode.get("exp").asInt(), rep.getExpiration());
        Assert.assertEquals(jsonNode.get("iat").asInt(), rep.getIssuedAt());
        Assert.assertEquals(jsonNode.get("nbf").asInt(), rep.getNotBefore());
        Assert.assertEquals(jsonNode.get("sub").asText(), rep.getSubject());
        List<String> audiences = new ArrayList<>();
        // We have single audience in the token - hence it is simple string
        Assert.assertTrue(((jsonNode.get("aud")) instanceof TextNode));
        audiences.add(jsonNode.get("aud").asText());
        assertNames(audiences, rep.getAudience());
        Assert.assertEquals(jsonNode.get("iss").asText(), rep.getIssuer());
        Assert.assertEquals(jsonNode.get("jti").asText(), rep.getId());
    }

    @Test
    public void testInvalidClientCredentials() throws Exception {
        oauth.doLogin("test-user@localhost", "password");
        String code = oauth.getCurrentQuery().get(CODE);
        AccessTokenResponse accessTokenResponse = oauth.doAccessTokenRequest(code, "password");
        String tokenResponse = oauth.introspectAccessTokenWithClientCredential("confidential-cli", "bad_credential", accessTokenResponse.getAccessToken());
        OAuth2ErrorRepresentation errorRep = JsonSerialization.readValue(tokenResponse, OAuth2ErrorRepresentation.class);
        org.keycloak.testsuite.Assert.assertEquals("Authentication failed.", errorRep.getErrorDescription());
        org.keycloak.testsuite.Assert.assertEquals(INVALID_REQUEST, errorRep.getError());
    }

    @Test
    public void testIntrospectRefreshToken() throws Exception {
        oauth.doLogin("test-user@localhost", "password");
        String code = oauth.getCurrentQuery().get(CODE);
        EventRepresentation loginEvent = events.expectLogin().assertEvent();
        String sessionId = loginEvent.getSessionId();
        AccessTokenResponse accessTokenResponse = oauth.doAccessTokenRequest(code, "password");
        String tokenResponse = oauth.introspectRefreshTokenWithClientCredential("confidential-cli", "secret1", accessTokenResponse.getRefreshToken());
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(tokenResponse);
        Assert.assertTrue(jsonNode.get("active").asBoolean());
        Assert.assertEquals(sessionId, jsonNode.get("session_state").asText());
        Assert.assertEquals("test-app", jsonNode.get("client_id").asText());
        Assert.assertTrue(jsonNode.has("exp"));
        Assert.assertTrue(jsonNode.has("iat"));
        Assert.assertTrue(jsonNode.has("nbf"));
        Assert.assertTrue(jsonNode.has("sub"));
        Assert.assertTrue(jsonNode.has("aud"));
        Assert.assertTrue(jsonNode.has("iss"));
        Assert.assertTrue(jsonNode.has("jti"));
        Assert.assertTrue(jsonNode.has("typ"));
        TokenMetadataRepresentation rep = objectMapper.readValue(tokenResponse, TokenMetadataRepresentation.class);
        Assert.assertTrue(rep.isActive());
        Assert.assertEquals("test-app", rep.getClientId());
        Assert.assertEquals(jsonNode.get("session_state").asText(), rep.getSessionState());
        Assert.assertEquals(jsonNode.get("exp").asInt(), rep.getExpiration());
        Assert.assertEquals(jsonNode.get("iat").asInt(), rep.getIssuedAt());
        Assert.assertEquals(jsonNode.get("nbf").asInt(), rep.getNotBefore());
        Assert.assertEquals(jsonNode.get("iss").asText(), rep.getIssuer());
        Assert.assertEquals(jsonNode.get("jti").asText(), rep.getId());
        Assert.assertEquals(jsonNode.get("typ").asText(), "Refresh");
    }

    @Test
    public void testPublicClientCredentialsNotAllowed() throws Exception {
        oauth.doLogin("test-user@localhost", "password");
        String code = oauth.getCurrentQuery().get(CODE);
        AccessTokenResponse accessTokenResponse = oauth.doAccessTokenRequest(code, "password");
        String tokenResponse = oauth.introspectAccessTokenWithClientCredential("public-cli", "it_doesnt_matter", accessTokenResponse.getAccessToken());
        OAuth2ErrorRepresentation errorRep = JsonSerialization.readValue(tokenResponse, OAuth2ErrorRepresentation.class);
        org.keycloak.testsuite.Assert.assertEquals("Client not allowed.", errorRep.getErrorDescription());
        org.keycloak.testsuite.Assert.assertEquals(INVALID_REQUEST, errorRep.getError());
    }

    @Test
    public void testInactiveAccessToken() throws Exception {
        oauth.doLogin("test-user@localhost", "password");
        String inactiveAccessToken = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJGSjg2R2NGM2pUYk5MT2NvNE52WmtVQ0lVbWZZQ3FvcXRPUWVNZmJoTmxFIn0.eyJqdGkiOiI5NjgxZTRlOC01NzhlLTQ3M2ItOTIwNC0yZWE5OTdhYzMwMTgiLCJleHAiOjE0NzYxMDY4NDksIm5iZiI6MCwiaWF0IjoxNDc2MTA2NTQ5LCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgxODAvYXV0aC9yZWFsbXMvdGVzdCIsImF1ZCI6InRlc3QtYXBwIiwic3ViIjoiZWYyYzk0NjAtZDRkYy00OTk5LWJlYmUtZWVmYWVkNmJmMGU3IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoidGVzdC1hcHAiLCJhdXRoX3RpbWUiOjE0NzYxMDY1NDksInNlc3Npb25fc3RhdGUiOiI1OGY4M2MzMi03MDhkLTQzNjktODhhNC05YjI5OGRjMDY5NzgiLCJhY3IiOiIxIiwiY2xpZW50X3Nlc3Npb24iOiI2NTYyOTVkZC1kZWNkLTQyZDAtYWJmYy0zZGJjZjJlMDE3NzIiLCJhbGxvd2VkLW9yaWdpbnMiOlsiaHR0cDovL2xvY2FsaG9zdDo4MTgwIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJ1c2VyIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsidGVzdC1hcHAiOnsicm9sZXMiOlsiY3VzdG9tZXItdXNlciJdfSwiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsInZpZXctcHJvZmlsZSJdfX0sIm5hbWUiOiJUb20gQnJhZHkiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJ0ZXN0LXVzZXJAbG9jYWxob3N0IiwiZ2l2ZW5fbmFtZSI6IlRvbSIsImZhbWlseV9uYW1lIjoiQnJhZHkiLCJlbWFpbCI6InRlc3QtdXNlckBsb2NhbGhvc3QifQ.LYU7opqZsc9e-ZmdsIhcecjHL3kQkpP13VpwO4MHMqEVNeJsZI1WOkTM5HGVAihcPfQazhaYvcik0gFTF_6ZcKzDqanjx80TGhSIrV5FoCeUrbp7w_66VKDH7ImPc8T2kICQGHh2d521WFBnvXNifw7P6AR1rGg4qrUljHdf_KU";
        String tokenResponse = oauth.introspectAccessTokenWithClientCredential("confidential-cli", "secret1", inactiveAccessToken);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(tokenResponse);
        Assert.assertFalse(jsonNode.get("active").asBoolean());
        TokenMetadataRepresentation rep = objectMapper.readValue(tokenResponse, TokenMetadataRepresentation.class);
        Assert.assertFalse(rep.isActive());
        Assert.assertNull(rep.getUserName());
        Assert.assertNull(rep.getClientId());
        Assert.assertNull(rep.getSubject());
    }

    @Test
    public void testUnsupportedToken() throws Exception {
        oauth.doLogin("test-user@localhost", "password");
        String inactiveAccessToken = "unsupported";
        String tokenResponse = oauth.introspectAccessTokenWithClientCredential("confidential-cli", "secret1", inactiveAccessToken);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(tokenResponse);
        Assert.assertFalse(jsonNode.get("active").asBoolean());
        TokenMetadataRepresentation rep = objectMapper.readValue(tokenResponse, TokenMetadataRepresentation.class);
        Assert.assertFalse(rep.isActive());
        Assert.assertNull(rep.getUserName());
        Assert.assertNull(rep.getClientId());
        Assert.assertNull(rep.getSubject());
    }

    @Test
    public void testIntrospectAccessToken() throws Exception {
        oauth.doLogin("test-user@localhost", "password");
        String code = oauth.getCurrentQuery().get(CODE);
        EventRepresentation loginEvent = events.expectLogin().assertEvent();
        AccessTokenResponse accessTokenResponse = oauth.doAccessTokenRequest(code, "password");
        String tokenResponse = oauth.introspectAccessTokenWithClientCredential("confidential-cli", "secret1", accessTokenResponse.getAccessToken());
        TokenMetadataRepresentation rep = JsonSerialization.readValue(tokenResponse, TokenMetadataRepresentation.class);
        Assert.assertTrue(rep.isActive());
        Assert.assertEquals("test-user@localhost", rep.getUserName());
        Assert.assertEquals("test-app", rep.getClientId());
        Assert.assertEquals(loginEvent.getUserId(), rep.getSubject());
        // Assert expected scope
        AbstractOIDCScopeTest.assertScopes("openid email profile", rep.getScope());
    }

    @Test
    public void testIntrospectAccessTokenES256() throws Exception {
        try {
            TokenSignatureUtil.registerKeyProvider("P-256", adminClient, testContext);
            TokenSignatureUtil.changeClientAccessTokenSignatureProvider(ApiUtil.findClientByClientId(adminClient.realm("test"), "test-app"), ES256);
            oauth.doLogin("test-user@localhost", "password");
            String code = oauth.getCurrentQuery().get(CODE);
            EventRepresentation loginEvent = events.expectLogin().assertEvent();
            AccessTokenResponse accessTokenResponse = oauth.doAccessTokenRequest(code, "password");
            Assert.assertEquals("ES256", getHeader().getAlgorithm().name());
            String tokenResponse = oauth.introspectAccessTokenWithClientCredential("confidential-cli", "secret1", accessTokenResponse.getAccessToken());
            TokenMetadataRepresentation rep = JsonSerialization.readValue(tokenResponse, TokenMetadataRepresentation.class);
            Assert.assertTrue(rep.isActive());
            Assert.assertEquals("test-user@localhost", rep.getUserName());
            Assert.assertEquals("test-app", rep.getClientId());
            Assert.assertEquals(loginEvent.getUserId(), rep.getSubject());
            // Assert expected scope
            OIDCScopeTest.assertScopes("openid email profile", rep.getScope());
        } finally {
            TokenSignatureUtil.changeClientAccessTokenSignatureProvider(ApiUtil.findClientByClientId(adminClient.realm("test"), "test-app"), RS256);
        }
    }

    @Test
    public void testIntrospectAccessTokenSessionInvalid() throws Exception {
        oauth.doLogin("test-user@localhost", "password");
        String code = oauth.getCurrentQuery().get(CODE);
        AccessTokenResponse accessTokenResponse = oauth.doAccessTokenRequest(code, "password");
        oauth.doLogout(accessTokenResponse.getRefreshToken(), "password");
        String tokenResponse = oauth.introspectAccessTokenWithClientCredential("confidential-cli", "secret1", accessTokenResponse.getAccessToken());
        TokenMetadataRepresentation rep = JsonSerialization.readValue(tokenResponse, TokenMetadataRepresentation.class);
        Assert.assertFalse(rep.isActive());
        Assert.assertNull(rep.getUserName());
        Assert.assertNull(rep.getClientId());
        Assert.assertNull(rep.getSubject());
    }

    // KEYCLOAK-4829
    @Test
    public void testIntrospectAccessTokenOfflineAccess() throws Exception {
        oauth.scope(OFFLINE_ACCESS);
        oauth.doLogin("test-user@localhost", "password");
        String code = oauth.getCurrentQuery().get(CODE);
        AccessTokenResponse accessTokenResponse = oauth.doAccessTokenRequest(code, "password");
        setTimeOffset(86400);
        // "Online" session still exists, but is invalid
        accessTokenResponse = oauth.doRefreshTokenRequest(accessTokenResponse.getRefreshToken(), "password");
        String tokenResponse = oauth.introspectAccessTokenWithClientCredential("confidential-cli", "secret1", accessTokenResponse.getAccessToken());
        TokenMetadataRepresentation rep = JsonSerialization.readValue(tokenResponse, TokenMetadataRepresentation.class);
        Assert.assertTrue(rep.isActive());
        Assert.assertEquals("test-user@localhost", rep.getUserName());
        Assert.assertEquals("test-app", rep.getClientId());
        // "Online" session doesn't even exists
        testingClient.testing().removeExpired("test");
        accessTokenResponse = oauth.doRefreshTokenRequest(accessTokenResponse.getRefreshToken(), "password");
        tokenResponse = oauth.introspectAccessTokenWithClientCredential("confidential-cli", "secret1", accessTokenResponse.getAccessToken());
        rep = JsonSerialization.readValue(tokenResponse, TokenMetadataRepresentation.class);
        Assert.assertTrue(rep.isActive());
        Assert.assertEquals("test-user@localhost", rep.getUserName());
        Assert.assertEquals("test-app", rep.getClientId());
    }

    @Test
    public void testIntrospectAccessTokenUserDisabled() throws Exception {
        oauth.doLogin("test-user@localhost", "password");
        String code = oauth.getCurrentQuery().get(CODE);
        AccessTokenResponse accessTokenResponse = oauth.doAccessTokenRequest(code, "password");
        EventRepresentation loginEvent = events.expectLogin().assertEvent();
        UserRepresentation userRep = new UserRepresentation();
        try {
            userRep.setEnabled(false);
            adminClient.realm(oauth.getRealm()).users().get(loginEvent.getUserId()).update(userRep);
            String tokenResponse = oauth.introspectAccessTokenWithClientCredential("confidential-cli", "secret1", accessTokenResponse.getAccessToken());
            TokenMetadataRepresentation rep = JsonSerialization.readValue(tokenResponse, TokenMetadataRepresentation.class);
            Assert.assertFalse(rep.isActive());
            Assert.assertNull(rep.getUserName());
            Assert.assertNull(rep.getClientId());
            Assert.assertNull(rep.getSubject());
        } finally {
            userRep.setEnabled(true);
            adminClient.realm(oauth.getRealm()).users().get(loginEvent.getUserId()).update(userRep);
        }
    }

    @Test
    public void testIntrospectAccessTokenExpired() throws Exception {
        oauth.doLogin("test-user@localhost", "password");
        String code = oauth.getCurrentQuery().get(CODE);
        AccessTokenResponse accessTokenResponse = oauth.doAccessTokenRequest(code, "password");
        setTimeOffset(((adminClient.realm(oauth.getRealm()).toRepresentation().getAccessTokenLifespan()) + 1));
        String tokenResponse = oauth.introspectAccessTokenWithClientCredential("confidential-cli", "secret1", accessTokenResponse.getAccessToken());
        TokenMetadataRepresentation rep = JsonSerialization.readValue(tokenResponse, TokenMetadataRepresentation.class);
        Assert.assertFalse(rep.isActive());
        Assert.assertNull(rep.getUserName());
        Assert.assertNull(rep.getClientId());
        Assert.assertNull(rep.getSubject());
    }

    /**
     * Test covers the same scenario from different endpoints like TokenEndpoint and LogoutEndpoint.
     */
    @Test
    public void testIntrospectWithSamlClient() throws Exception {
        oauth.doLogin("test-user@localhost", "password");
        String code = oauth.getCurrentQuery().get(CODE);
        events.expectLogin().assertEvent();
        AccessTokenResponse accessTokenResponse = oauth.doAccessTokenRequest(code, "password");
        String tokenResponse = oauth.introspectAccessTokenWithClientCredential("saml-client", "secret2", accessTokenResponse.getAccessToken());
        TokenMetadataRepresentation rep = JsonSerialization.readValue(tokenResponse, TokenMetadataRepresentation.class);
        Assert.assertEquals(INVALID_CLIENT, rep.getOtherClaims().get("error"));
        Assert.assertNull(rep.getSubject());
    }
}

