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


import Algorithm.ES256;
import Algorithm.RS256;
import Details.AUTH_METHOD;
import Details.SIGNATURE_ALGORITHM;
import Details.SIGNATURE_REQUIRED;
import Details.USERNAME;
import Details.VALIDATE_ACCESS_TOKEN;
import Errors.INVALID_CLIENT;
import Errors.INVALID_TOKEN;
import Errors.USER_SESSION_NOT_FOUND;
import EventType.USER_INFO_REQUEST;
import EventType.USER_INFO_REQUEST_ERROR;
import HttpHeaders.AUTHORIZATION;
import HttpHeaders.CONTENT_TYPE;
import Status.BAD_REQUEST;
import Status.UNAUTHORIZED;
import java.net.URI;
import java.security.PublicKey;
import java.util.Collections;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.common.util.PemUtils;
import org.keycloak.jose.jws.JWSInput;
import org.keycloak.jose.jws.crypto.RSAProvider;
import org.keycloak.protocol.oidc.OIDCAdvancedConfigWrapper;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.UserInfo;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.services.Urls;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.Assert;
import org.keycloak.testsuite.AssertEvents;
import org.keycloak.testsuite.admin.ApiUtil;
import org.keycloak.testsuite.util.OAuthClient;
import org.keycloak.testsuite.util.TokenSignatureUtil;
import org.keycloak.testsuite.util.UserInfoClientUtil;
import org.keycloak.util.JsonSerialization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.keycloak.testsuite.util.ClientBuilder.create;


/**
 *
 *
 * @author pedroigor
 */
public class UserInfoTest extends AbstractKeycloakTest {
    @Rule
    public AssertEvents events = new AssertEvents(this);

    @Test
    public void testSuccess_getMethod_header() throws Exception {
        Client client = ClientBuilder.newClient();
        try {
            AccessTokenResponse accessTokenResponse = executeGrantAccessTokenRequest(client);
            Response response = UserInfoClientUtil.executeUserInfoRequest_getMethod(client, accessTokenResponse.getToken());
            testSuccessfulUserInfoResponse(response);
        } finally {
            client.close();
        }
    }

    @Test
    public void testSuccess_postMethod_header() throws Exception {
        Client client = ClientBuilder.newClient();
        try {
            AccessTokenResponse accessTokenResponse = executeGrantAccessTokenRequest(client);
            WebTarget userInfoTarget = UserInfoClientUtil.getUserInfoWebTarget(client);
            Response response = userInfoTarget.request().header(AUTHORIZATION, ("bearer " + (accessTokenResponse.getToken()))).post(Entity.form(new Form()));
            testSuccessfulUserInfoResponse(response);
        } finally {
            client.close();
        }
    }

    @Test
    public void testSuccess_postMethod_body() throws Exception {
        Client client = ClientBuilder.newClient();
        try {
            AccessTokenResponse accessTokenResponse = executeGrantAccessTokenRequest(client);
            Form form = new Form();
            form.param("access_token", accessTokenResponse.getToken());
            WebTarget userInfoTarget = UserInfoClientUtil.getUserInfoWebTarget(client);
            Response response = userInfoTarget.request().post(Entity.form(form));
            testSuccessfulUserInfoResponse(response);
        } finally {
            client.close();
        }
    }

    // KEYCLOAK-8838
    @Test
    public void testSuccess_dotsInClientId() throws Exception {
        // Create client with dot in the name and with some role
        ClientRepresentation clientRep = create().clientId("my.foo.client").addRedirectUri("http://foo.host").secret("password").directAccessGrants().defaultRoles("my.foo.role").build();
        RealmResource realm = adminClient.realm("test");
        Response resp = realm.clients().create(clientRep);
        String clientUUID = ApiUtil.getCreatedId(resp);
        resp.close();
        getCleanup().addClientUuid(clientUUID);
        // Assign role to the user
        RoleRepresentation fooRole = realm.clients().get(clientUUID).roles().get("my.foo.role").toRepresentation();
        UserResource userResource = ApiUtil.findUserByUsernameId(realm, "test-user@localhost");
        userResource.roles().clientLevel(clientUUID).add(Collections.singletonList(fooRole));
        // Login to the new client
        OAuthClient.AccessTokenResponse accessTokenResponse = oauth.clientId("my.foo.client").doGrantAccessTokenRequest("password", "test-user@localhost", "password");
        AccessToken accessToken = oauth.verifyToken(accessTokenResponse.getAccessToken());
        Assert.assertNames(accessToken.getResourceAccess("my.foo.client").getRoles(), "my.foo.role");
        events.clear();
        // Send UserInfo request and ensure it is correct
        Client client = ClientBuilder.newClient();
        try {
            Response response = UserInfoClientUtil.executeUserInfoRequest_getMethod(client, accessTokenResponse.getAccessToken());
            testSuccessfulUserInfoResponse(response, "my.foo.client");
        } finally {
            client.close();
        }
    }

    @Test
    public void testSuccess_postMethod_header_textEntity() throws Exception {
        Client client = ClientBuilder.newClient();
        try {
            AccessTokenResponse accessTokenResponse = executeGrantAccessTokenRequest(client);
            WebTarget userInfoTarget = UserInfoClientUtil.getUserInfoWebTarget(client);
            Response response = userInfoTarget.request().header(AUTHORIZATION, ("bearer " + (accessTokenResponse.getToken()))).post(Entity.text(""));
            testSuccessfulUserInfoResponse(response);
        } finally {
            client.close();
        }
    }

    @Test
    public void testSuccessSignedResponse() throws Exception {
        // Require signed userInfo request
        ClientResource clientResource = ApiUtil.findClientByClientId(adminClient.realm("test"), "test-app");
        ClientRepresentation clientRep = clientResource.toRepresentation();
        OIDCAdvancedConfigWrapper.fromClientRepresentation(clientRep).setUserInfoSignedResponseAlg(RS256);
        clientResource.update(clientRep);
        // test signed response
        Client client = ClientBuilder.newClient();
        try {
            AccessTokenResponse accessTokenResponse = executeGrantAccessTokenRequest(client);
            Response response = UserInfoClientUtil.executeUserInfoRequest_getMethod(client, accessTokenResponse.getToken());
            events.expect(USER_INFO_REQUEST).session(Matchers.notNullValue(String.class)).detail(AUTH_METHOD, VALIDATE_ACCESS_TOKEN).detail(USERNAME, "test-user@localhost").detail(SIGNATURE_REQUIRED, "true").detail(SIGNATURE_ALGORITHM, RS256.toString()).assertEvent();
            // Check signature and content
            PublicKey publicKey = PemUtils.decodePublicKey(ApiUtil.findActiveKey(adminClient.realm("test")).getPublicKey());
            Assert.assertEquals(200, response.getStatus());
            assertEquals(response.getHeaderString(CONTENT_TYPE), MediaType.APPLICATION_JWT);
            String signedResponse = response.readEntity(String.class);
            response.close();
            JWSInput jwsInput = new JWSInput(signedResponse);
            assertTrue(RSAProvider.verify(jwsInput, publicKey));
            UserInfo userInfo = JsonSerialization.readValue(jwsInput.getContent(), UserInfo.class);
            assertNotNull(userInfo);
            assertNotNull(userInfo.getSubject());
            Assert.assertEquals("test-user@localhost", userInfo.getEmail());
            Assert.assertEquals("test-user@localhost", userInfo.getPreferredUsername());
            assertTrue(userInfo.hasAudience("test-app"));
            String expectedIssuer = Urls.realmIssuer(new URI(OAuthClient.AUTH_SERVER_ROOT), "test");
            Assert.assertEquals(expectedIssuer, userInfo.getIssuer());
        } finally {
            client.close();
        }
        // Revert signed userInfo request
        OIDCAdvancedConfigWrapper.fromClientRepresentation(clientRep).setUserInfoSignedResponseAlg(null);
        clientResource.update(clientRep);
    }

    @Test
    public void testSuccessSignedResponseES256() throws Exception {
        try {
            TokenSignatureUtil.registerKeyProvider("P-256", adminClient, testContext);
            // Require signed userInfo request
            ClientResource clientResource = ApiUtil.findClientByClientId(adminClient.realm("test"), "test-app");
            ClientRepresentation clientRep = clientResource.toRepresentation();
            OIDCAdvancedConfigWrapper.fromClientRepresentation(clientRep).setUserInfoSignedResponseAlg(ES256);
            clientResource.update(clientRep);
            // test signed response
            Client client = ClientBuilder.newClient();
            try {
                AccessTokenResponse accessTokenResponse = executeGrantAccessTokenRequest(client);
                Response response = UserInfoClientUtil.executeUserInfoRequest_getMethod(client, accessTokenResponse.getToken());
                events.expect(USER_INFO_REQUEST).session(Matchers.notNullValue(String.class)).detail(AUTH_METHOD, VALIDATE_ACCESS_TOKEN).detail(USERNAME, "test-user@localhost").detail(SIGNATURE_REQUIRED, "true").detail(SIGNATURE_ALGORITHM, ES256.toString()).assertEvent();
                Assert.assertEquals(200, response.getStatus());
                assertEquals(response.getHeaderString(CONTENT_TYPE), MediaType.APPLICATION_JWT);
                String signedResponse = response.readEntity(String.class);
                response.close();
                JWSInput jwsInput = new JWSInput(signedResponse);
                org.junit.Assert.assertEquals("ES256", jwsInput.getHeader().getAlgorithm().name());
                UserInfo userInfo = JsonSerialization.readValue(jwsInput.getContent(), UserInfo.class);
                assertNotNull(userInfo);
                assertNotNull(userInfo.getSubject());
                Assert.assertEquals("test-user@localhost", userInfo.getEmail());
                Assert.assertEquals("test-user@localhost", userInfo.getPreferredUsername());
                assertTrue(userInfo.hasAudience("test-app"));
                String expectedIssuer = Urls.realmIssuer(new URI(OAuthClient.AUTH_SERVER_ROOT), "test");
                Assert.assertEquals(expectedIssuer, userInfo.getIssuer());
            } finally {
                client.close();
            }
            // Revert signed userInfo request
            OIDCAdvancedConfigWrapper.fromClientRepresentation(clientRep).setUserInfoSignedResponseAlg(null);
            clientResource.update(clientRep);
        } finally {
            TokenSignatureUtil.changeRealmTokenSignatureProvider(adminClient, org.keycloak.crypto.Algorithm.RS256);
        }
    }

    @Test
    public void testSessionExpired() {
        Client client = ClientBuilder.newClient();
        try {
            AccessTokenResponse accessTokenResponse = executeGrantAccessTokenRequest(client);
            testingClient.testing().removeUserSessions("test");
            Response response = UserInfoClientUtil.executeUserInfoRequest_getMethod(client, accessTokenResponse.getToken());
            assertEquals(UNAUTHORIZED.getStatusCode(), response.getStatus());
            response.close();
            events.expect(USER_INFO_REQUEST_ERROR).error(USER_SESSION_NOT_FOUND).user(Matchers.nullValue(String.class)).session(Matchers.nullValue(String.class)).detail(AUTH_METHOD, VALIDATE_ACCESS_TOKEN).assertEvent();
        } finally {
            client.close();
        }
    }

    @Test
    public void testAccessTokenExpired() {
        Client client = ClientBuilder.newClient();
        try {
            AccessTokenResponse accessTokenResponse = executeGrantAccessTokenRequest(client);
            setTimeOffset(600);
            Response response = UserInfoClientUtil.executeUserInfoRequest_getMethod(client, accessTokenResponse.getToken());
            assertEquals(UNAUTHORIZED.getStatusCode(), response.getStatus());
            response.close();
            events.expect(USER_INFO_REQUEST_ERROR).error(INVALID_TOKEN).user(Matchers.nullValue(String.class)).session(Matchers.nullValue(String.class)).detail(AUTH_METHOD, VALIDATE_ACCESS_TOKEN).client(((String) (null))).assertEvent();
        } finally {
            client.close();
        }
    }

    @Test
    public void testSessionExpiredOfflineAccess() throws Exception {
        Client client = ClientBuilder.newClient();
        try {
            AccessTokenResponse accessTokenResponse = executeGrantAccessTokenRequest(client, true);
            testingClient.testing().removeUserSessions("test");
            Response response = UserInfoClientUtil.executeUserInfoRequest_getMethod(client, accessTokenResponse.getToken());
            testSuccessfulUserInfoResponse(response);
            response.close();
        } finally {
            client.close();
        }
    }

    @Test
    public void testUnsuccessfulUserInfoRequest() throws Exception {
        Client client = ClientBuilder.newClient();
        try {
            Response response = UserInfoClientUtil.executeUserInfoRequest_getMethod(client, "bad");
            response.close();
            assertEquals(UNAUTHORIZED.getStatusCode(), response.getStatus());
            events.expect(USER_INFO_REQUEST_ERROR).error(INVALID_TOKEN).client(((String) (null))).user(Matchers.nullValue(String.class)).session(Matchers.nullValue(String.class)).detail(AUTH_METHOD, VALIDATE_ACCESS_TOKEN).assertEvent();
        } finally {
            client.close();
        }
    }

    @Test
    public void testUserInfoRequestWithSamlClient() throws Exception {
        // obtain an access token
        String accessToken = oauth.doGrantAccessTokenRequest("test", "test-user@localhost", "password", null, "saml-client", "secret").getAccessToken();
        // change client's protocol
        ClientRepresentation samlClient = adminClient.realm("test").clients().findByClientId("saml-client").get(0);
        samlClient.setProtocol("saml");
        adminClient.realm("test").clients().get(samlClient.getId()).update(samlClient);
        Client client = ClientBuilder.newClient();
        try {
            events.clear();
            Response response = UserInfoClientUtil.executeUserInfoRequest_getMethod(client, accessToken);
            response.close();
            assertEquals(BAD_REQUEST.getStatusCode(), response.getStatus());
            events.expect(USER_INFO_REQUEST).error(INVALID_CLIENT).client(((String) (null))).user(Matchers.nullValue(String.class)).session(Matchers.nullValue(String.class)).detail(AUTH_METHOD, VALIDATE_ACCESS_TOKEN).assertEvent();
        } finally {
            client.close();
        }
    }
}

