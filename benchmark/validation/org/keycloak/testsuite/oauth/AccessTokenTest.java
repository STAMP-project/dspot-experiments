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
import Constants.ADMIN_CONSOLE_CLIENT_ID;
import Details.CODE_ID;
import Details.REDIRECT_URI;
import Details.REFRESH_TOKEN_ID;
import Details.REFRESH_TOKEN_TYPE;
import Details.TOKEN_ID;
import Errors.INVALID_CODE;
import HttpHeaders.AUTHORIZATION;
import OAuth2Constants.CODE;
import OAuth2Constants.GRANT_TYPE;
import OAuth2Constants.PASSWORD;
import OAuthClient.AccessTokenResponse;
import OIDCConfigAttributes.ACCESS_TOKEN_LIFESPAN;
import Response.Status.UNAUTHORIZED;
import SslRequired.ALL;
import SslRequired.EXTERNAL;
import UserModel.RequiredAction.UPDATE_PASSWORD;
import UserModel.RequiredAction.UPDATE_PROFILE;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.ClientScopeResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.jose.jws.JWSHeader;
import org.keycloak.models.ProtocolMapperModel;
import org.keycloak.models.utils.ModelToRepresentation;
import org.keycloak.protocol.oidc.OIDCLoginProtocolService;
import org.keycloak.protocol.oidc.mappers.HardcodedClaim;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.IDToken;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.ClientScopeRepresentation;
import org.keycloak.representations.idm.EventRepresentation;
import org.keycloak.representations.idm.ProtocolMapperRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.ActionURIUtils;
import org.keycloak.testsuite.AssertEvents;
import org.keycloak.testsuite.admin.ApiUtil;
import org.keycloak.testsuite.arquillian.AuthServerTestEnricher;
import org.keycloak.testsuite.util.OAuthClient;
import org.keycloak.testsuite.util.ProtocolMapperUtil;
import org.keycloak.testsuite.util.RealmManager;
import org.keycloak.testsuite.util.RoleBuilder;
import org.keycloak.testsuite.util.TokenSignatureUtil;
import org.keycloak.testsuite.util.UserInfoClientUtil;
import org.keycloak.util.BasicAuthHelper;
import org.openqa.selenium.By;

import static org.keycloak.testsuite.Assert.assertExpiration;
import static org.keycloak.testsuite.util.ClientBuilder.create;
import static org.keycloak.testsuite.util.UserManager.realm;


/**
 *
 *
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class AccessTokenTest extends AbstractKeycloakTest {
    @Rule
    public AssertEvents events = new AssertEvents(this);

    @Test
    public void loginFormUsernameOrEmailLabel() throws Exception {
        oauth.openLoginForm();
        Assert.assertEquals("Username or email", driver.findElement(By.xpath("//label[@for='username']")).getText());
    }

    @Test
    public void accessTokenRequest() throws Exception {
        oauth.doLogin("test-user@localhost", "password");
        EventRepresentation loginEvent = events.expectLogin().assertEvent();
        String sessionId = loginEvent.getSessionId();
        String codeId = loginEvent.getDetails().get(CODE_ID);
        String code = oauth.getCurrentQuery().get(CODE);
        OAuthClient.AccessTokenResponse response = oauth.doAccessTokenRequest(code, "password");
        Assert.assertEquals(200, response.getStatusCode());
        Assert.assertThat(response.getExpiresIn(), Matchers.allOf(Matchers.greaterThanOrEqualTo(250), Matchers.lessThanOrEqualTo(300)));
        Assert.assertThat(response.getRefreshExpiresIn(), Matchers.allOf(Matchers.greaterThanOrEqualTo(1750), Matchers.lessThanOrEqualTo(1800)));
        Assert.assertEquals("bearer", response.getTokenType());
        String expectedKid = oauth.doCertsRequest("test").getKeys()[0].getKeyId();
        JWSHeader header = getHeader();
        Assert.assertEquals("RS256", header.getAlgorithm().name());
        Assert.assertEquals("JWT", header.getType());
        Assert.assertEquals(expectedKid, header.getKeyId());
        Assert.assertNull(header.getContentType());
        header = new org.keycloak.jose.jws.JWSInput(response.getIdToken()).getHeader();
        Assert.assertEquals("RS256", header.getAlgorithm().name());
        Assert.assertEquals("JWT", header.getType());
        Assert.assertEquals(expectedKid, header.getKeyId());
        Assert.assertNull(header.getContentType());
        header = new org.keycloak.jose.jws.JWSInput(response.getRefreshToken()).getHeader();
        Assert.assertEquals("HS256", header.getAlgorithm().name());
        Assert.assertEquals("JWT", header.getType());
        Assert.assertNull(header.getContentType());
        AccessToken token = oauth.verifyToken(response.getAccessToken());
        Assert.assertEquals(ApiUtil.findUserByUsername(adminClient.realm("test"), "test-user@localhost").getId(), token.getSubject());
        Assert.assertNotEquals("test-user@localhost", token.getSubject());
        Assert.assertEquals(sessionId, token.getSessionState());
        Assert.assertEquals(1, token.getRealmAccess().getRoles().size());
        Assert.assertTrue(token.getRealmAccess().isUserInRole("user"));
        Assert.assertEquals(1, token.getResourceAccess(oauth.getClientId()).getRoles().size());
        Assert.assertTrue(token.getResourceAccess(oauth.getClientId()).isUserInRole("customer-user"));
        EventRepresentation event = events.expectCodeToToken(codeId, sessionId).assertEvent();
        Assert.assertEquals(token.getId(), event.getDetails().get(TOKEN_ID));
        Assert.assertEquals(oauth.parseRefreshToken(response.getRefreshToken()).getId(), event.getDetails().get(REFRESH_TOKEN_ID));
        Assert.assertEquals(sessionId, token.getSessionState());
    }

    // KEYCLOAK-3692
    @Test
    public void accessTokenWrongCode() throws Exception {
        oauth.clientId(ADMIN_CONSOLE_CLIENT_ID);
        oauth.redirectUri(((AuthServerTestEnricher.getAuthServerContextRoot()) + "/auth/admin/test/console/nosuch.html"));
        oauth.openLoginForm();
        String actionURI = ActionURIUtils.getActionURIFromPageSource(driver.getPageSource());
        String loginPageCode = ActionURIUtils.parseQueryParamsFromActionURI(actionURI).get("code");
        oauth.fillLoginForm("test-user@localhost", "password");
        events.expectLogin().client(ADMIN_CONSOLE_CLIENT_ID).detail(REDIRECT_URI, ((AuthServerTestEnricher.getAuthServerContextRoot()) + "/auth/admin/test/console/nosuch.html")).assertEvent();
        OAuthClient.AccessTokenResponse response = oauth.doAccessTokenRequest(loginPageCode, null);
        Assert.assertEquals(400, response.getStatusCode());
        Assert.assertNull(response.getRefreshToken());
    }

    @Test
    public void accessTokenInvalidClientCredentials() throws Exception {
        oauth.doLogin("test-user@localhost", "password");
        EventRepresentation loginEvent = events.expectLogin().assertEvent();
        String codeId = loginEvent.getDetails().get(CODE_ID);
        String code = oauth.getCurrentQuery().get(CODE);
        OAuthClient.AccessTokenResponse response = oauth.doAccessTokenRequest(code, "invalid");
        Assert.assertEquals(400, response.getStatusCode());
        AssertEvents.ExpectedEvent expectedEvent = events.expectCodeToToken(codeId, loginEvent.getSessionId()).error("invalid_client_credentials").clearDetails().user(((String) (null))).session(((String) (null)));
        expectedEvent.assertEvent();
    }

    @Test
    public void accessTokenMissingClientCredentials() throws Exception {
        oauth.doLogin("test-user@localhost", "password");
        EventRepresentation loginEvent = events.expectLogin().assertEvent();
        String codeId = loginEvent.getDetails().get(CODE_ID);
        String code = oauth.getCurrentQuery().get(CODE);
        OAuthClient.AccessTokenResponse response = oauth.doAccessTokenRequest(code, null);
        Assert.assertEquals(400, response.getStatusCode());
        AssertEvents.ExpectedEvent expectedEvent = events.expectCodeToToken(codeId, loginEvent.getSessionId()).error("invalid_client_credentials").clearDetails().user(((String) (null))).session(((String) (null)));
        expectedEvent.assertEvent();
    }

    @Test
    public void accessTokenInvalidRedirectUri() throws Exception {
        oauth.doLogin("test-user@localhost", "password");
        EventRepresentation loginEvent = events.expectLogin().assertEvent();
        String codeId = loginEvent.getDetails().get(CODE_ID);
        String code = oauth.getCurrentQuery().get(CODE);
        // @TODO This new and was necesssary to not mess up with other tests cases
        String redirectUri = oauth.getRedirectUri();
        oauth.redirectUri("http://invalid");
        OAuthClient.AccessTokenResponse response = oauth.doAccessTokenRequest(code, "password");
        Assert.assertEquals(400, response.getStatusCode());
        Assert.assertEquals("invalid_grant", response.getError());
        Assert.assertEquals("Incorrect redirect_uri", response.getErrorDescription());
        events.expectCodeToToken(codeId, loginEvent.getSessionId()).error("invalid_code").removeDetail(TOKEN_ID).removeDetail(REFRESH_TOKEN_ID).removeDetail(REFRESH_TOKEN_TYPE).assertEvent();
        // @TODO Reset back to the original URI. Maybe we should have something to reset to the original state at OAuthClient
        oauth.redirectUri(redirectUri);
    }

    @Test
    public void accessTokenUserSessionExpired() {
        oauth.doLogin("test-user@localhost", "password");
        EventRepresentation loginEvent = events.expectLogin().assertEvent();
        String codeId = loginEvent.getDetails().get(CODE_ID);
        String sessionId = loginEvent.getSessionId();
        testingClient.testing().removeUserSession("test", sessionId);
        String code = oauth.getCurrentQuery().get(CODE);
        OAuthClient.AccessTokenResponse tokenResponse = oauth.doAccessTokenRequest(code, "password");
        Assert.assertEquals(400, tokenResponse.getStatusCode());
        Assert.assertNull(tokenResponse.getAccessToken());
        Assert.assertNull(tokenResponse.getRefreshToken());
        events.expectCodeToToken(codeId, sessionId).removeDetail(TOKEN_ID).user(((String) (null))).removeDetail(REFRESH_TOKEN_ID).removeDetail(REFRESH_TOKEN_TYPE).error(INVALID_CODE).assertEvent();
        events.clear();
    }

    @Test
    public void accessTokenCodeExpired() {
        RealmManager.realm(adminClient.realm("test")).accessCodeLifeSpan(1);
        oauth.doLogin("test-user@localhost", "password");
        EventRepresentation loginEvent = events.expectLogin().assertEvent();
        String codeId = loginEvent.getDetails().get(CODE_ID);
        loginEvent.getSessionId();
        String code = oauth.getCurrentQuery().get(CODE);
        setTimeOffset(2);
        OAuthClient.AccessTokenResponse response = oauth.doAccessTokenRequest(code, "password");
        Assert.assertEquals(400, response.getStatusCode());
        setTimeOffset(0);
        AssertEvents.ExpectedEvent expectedEvent = events.expectCodeToToken(codeId, codeId);
        expectedEvent.error("expired_code").removeDetail(TOKEN_ID).removeDetail(REFRESH_TOKEN_ID).removeDetail(REFRESH_TOKEN_TYPE).user(((String) (null)));
        expectedEvent.assertEvent();
        events.clear();
        RealmManager.realm(adminClient.realm("test")).accessCodeLifeSpan(60);
    }

    @Test
    public void accessTokenCodeUsed() throws IOException {
        oauth.doLogin("test-user@localhost", "password");
        EventRepresentation loginEvent = events.expectLogin().assertEvent();
        String codeId = loginEvent.getDetails().get(CODE_ID);
        loginEvent.getSessionId();
        String code = oauth.getCurrentQuery().get(CODE);
        OAuthClient.AccessTokenResponse response = oauth.doAccessTokenRequest(code, "password");
        Assert.assertEquals(200, response.getStatusCode());
        String accessToken = response.getAccessToken();
        Client jaxrsClient = ClientBuilder.newClient();
        try {
            // Check that userInfo can be invoked
            Response userInfoResponse = UserInfoClientUtil.executeUserInfoRequest_getMethod(jaxrsClient, accessToken);
            UserInfoClientUtil.testSuccessfulUserInfoResponse(userInfoResponse, "test-user@localhost", "test-user@localhost");
            // Check that tokenIntrospection can be invoked
            String introspectionResponse = oauth.introspectAccessTokenWithClientCredential("test-app", "password", accessToken);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(introspectionResponse);
            Assert.assertEquals(true, jsonNode.get("active").asBoolean());
            Assert.assertEquals("test-user@localhost", jsonNode.get("email").asText());
            events.clear();
            // Repeating attempt to exchange code should be refused and invalidate previous clientSession
            response = oauth.doAccessTokenRequest(code, "password");
            Assert.assertEquals(400, response.getStatusCode());
            AssertEvents.ExpectedEvent expectedEvent = events.expectCodeToToken(codeId, codeId);
            expectedEvent.error("invalid_code").removeDetail(TOKEN_ID).removeDetail(REFRESH_TOKEN_ID).removeDetail(REFRESH_TOKEN_TYPE).user(((String) (null)));
            expectedEvent.assertEvent();
            // Check that userInfo can't be invoked with invalidated accessToken
            userInfoResponse = UserInfoClientUtil.executeUserInfoRequest_getMethod(jaxrsClient, accessToken);
            Assert.assertEquals(UNAUTHORIZED.getStatusCode(), userInfoResponse.getStatus());
            userInfoResponse.close();
            // Check that tokenIntrospection can't be invoked with invalidated accessToken
            introspectionResponse = oauth.introspectAccessTokenWithClientCredential("test-app", "password", accessToken);
            objectMapper = new ObjectMapper();
            jsonNode = objectMapper.readTree(introspectionResponse);
            Assert.assertEquals(false, jsonNode.get("active").asBoolean());
            Assert.assertNull(jsonNode.get("email"));
            events.clear();
            RealmManager.realm(adminClient.realm("test")).accessCodeLifeSpan(60);
        } finally {
            jaxrsClient.close();
        }
    }

    @Test
    public void accessTokenCodeRoleMissing() {
        RealmResource realmResource = adminClient.realm("test");
        RoleRepresentation role = RoleBuilder.create().name("tmp-role").build();
        realmResource.roles().create(role);
        UserResource user = ApiUtil.findUserByUsernameId(realmResource, "test-user@localhost");
        realm(realmResource).user(user).assignRoles(role.getName());
        oauth.doLogin("test-user@localhost", "password");
        EventRepresentation loginEvent = events.expectLogin().assertEvent();
        loginEvent.getDetails().get(CODE_ID);
        String code = oauth.getCurrentQuery().get(CODE);
        realmResource.roles().deleteRole("tmp-role");
        OAuthClient.AccessTokenResponse response = oauth.doAccessTokenRequest(code, "password");
        Assert.assertEquals(200, response.getStatusCode());
        AccessToken token = oauth.verifyToken(response.getAccessToken());
        Assert.assertEquals(1, token.getRealmAccess().getRoles().size());
        Assert.assertTrue(token.getRealmAccess().isUserInRole("user"));
        events.clear();
    }

    @Test
    public void accessTokenCodeHasRequiredAction() {
        UserResource user = ApiUtil.findUserByUsernameId(adminClient.realm("test"), "test-user@localhost");
        realm(adminClient.realm("test")).user(user).addRequiredAction(UPDATE_PROFILE.toString());
        oauth.doLogin("test-user@localhost", "password");
        String actionURI = ActionURIUtils.getActionURIFromPageSource(driver.getPageSource());
        String code = ActionURIUtils.parseQueryParamsFromActionURI(actionURI).get("code");
        OAuthClient.AccessTokenResponse response = oauth.doAccessTokenRequest(code, "password");
        Assert.assertEquals(400, response.getStatusCode());
        EventRepresentation event = events.poll();
        Assert.assertNull(event.getDetails().get(CODE_ID));
        realm(adminClient.realm("test")).user(user).removeRequiredAction(UPDATE_PROFILE.toString());
    }

    @Test
    public void testGrantAccessToken() throws Exception {
        Client client = ClientBuilder.newClient();
        UriBuilder builder = UriBuilder.fromUri(OAuthClient.AUTH_SERVER_ROOT);
        URI grantUri = OIDCLoginProtocolService.tokenUrl(builder).build("test");
        WebTarget grantTarget = client.target(grantUri);
        {
            // test checkSsl
            {
                RealmResource realmsResource = adminClient.realm("test");
                RealmRepresentation realmRepresentation = realmsResource.toRepresentation();
                realmRepresentation.setSslRequired(ALL.toString());
                realmsResource.update(realmRepresentation);
            }
            Response response = executeGrantAccessTokenRequest(grantTarget);
            Assert.assertEquals((AbstractKeycloakTest.AUTH_SERVER_SSL_REQUIRED ? 200 : 403), response.getStatus());
            response.close();
            {
                RealmResource realmsResource = realmsResouce().realm("test");
                RealmRepresentation realmRepresentation = realmsResource.toRepresentation();
                realmRepresentation.setSslRequired(EXTERNAL.toString());
                realmsResource.update(realmRepresentation);
            }
        }
        {
            // test null username
            String header = BasicAuthHelper.createHeader("test-app", "password");
            Form form = new Form();
            form.param(GRANT_TYPE, PASSWORD);
            form.param("password", "password");
            Response response = grantTarget.request().header(AUTHORIZATION, header).post(Entity.form(form));
            Assert.assertEquals(401, response.getStatus());
            response.close();
        }
        {
            // test no password
            String header = BasicAuthHelper.createHeader("test-app", "password");
            Form form = new Form();
            form.param(GRANT_TYPE, PASSWORD);
            form.param("username", "test-user@localhost");
            Response response = grantTarget.request().header(AUTHORIZATION, header).post(Entity.form(form));
            Assert.assertEquals(401, response.getStatus());
            response.close();
        }
        {
            // test invalid password
            String header = BasicAuthHelper.createHeader("test-app", "password");
            Form form = new Form();
            form.param(GRANT_TYPE, PASSWORD);
            form.param("username", "test-user@localhost");
            form.param("password", "invalid");
            Response response = grantTarget.request().header(AUTHORIZATION, header).post(Entity.form(form));
            Assert.assertEquals(401, response.getStatus());
            response.close();
        }
        {
            // test no password
            String header = BasicAuthHelper.createHeader("test-app", "password");
            Form form = new Form();
            form.param(GRANT_TYPE, PASSWORD);
            form.param("username", "test-user@localhost");
            Response response = grantTarget.request().header(AUTHORIZATION, header).post(Entity.form(form));
            Assert.assertEquals(401, response.getStatus());
            response.close();
        }
        {
            // test bearer-only
            {
                ClientResource clientResource = ApiUtil.findClientByClientId(adminClient.realm("test"), "test-app");
                ClientRepresentation clientRepresentation = clientResource.toRepresentation();
                clientRepresentation.setBearerOnly(true);
                clientResource.update(clientRepresentation);
            }
            Response response = executeGrantAccessTokenRequest(grantTarget);
            Assert.assertEquals(400, response.getStatus());
            response.close();
            {
                ClientResource clientResource = ApiUtil.findClientByClientId(adminClient.realm("test"), "test-app");
                ClientRepresentation clientRepresentation = clientResource.toRepresentation();
                clientRepresentation.setBearerOnly(false);
                clientResource.update(clientRepresentation);
            }
        }
        {
            // test realm disabled
            {
                RealmResource realmsResource = realmsResouce().realm("test");
                RealmRepresentation realmRepresentation = realmsResource.toRepresentation();
                realmRepresentation.setEnabled(false);
                realmsResource.update(realmRepresentation);
            }
            Response response = executeGrantAccessTokenRequest(grantTarget);
            Assert.assertEquals(403, response.getStatus());
            response.close();
            {
                RealmResource realmsResource = realmsResouce().realm("test");
                RealmRepresentation realmRepresentation = realmsResource.toRepresentation();
                realmRepresentation.setEnabled(true);
                realmsResource.update(realmRepresentation);
            }
        }
        {
            // test application disabled
            {
                ClientResource clientResource = ApiUtil.findClientByClientId(adminClient.realm("test"), "test-app");
                ClientRepresentation clientRepresentation = clientResource.toRepresentation();
                clientRepresentation.setEnabled(false);
                clientResource.update(clientRepresentation);
            }
            Response response = executeGrantAccessTokenRequest(grantTarget);
            Assert.assertEquals(400, response.getStatus());
            response.close();
            {
                ClientResource clientResource = ApiUtil.findClientByClientId(adminClient.realm("test"), "test-app");
                ClientRepresentation clientRepresentation = clientResource.toRepresentation();
                clientRepresentation.setEnabled(true);
                clientResource.update(clientRepresentation);
            }
        }
        {
            // test user action required
            {
                UserResource userResource = ApiUtil.findUserByUsernameId(adminClient.realm("test"), "test-user@localhost");
                UserRepresentation userRepresentation = userResource.toRepresentation();
                userRepresentation.getRequiredActions().add(UPDATE_PASSWORD.toString());
                userResource.update(userRepresentation);
            }
            // good password is 400 => Account is not fully set up
            try (Response response = executeGrantAccessTokenRequest(grantTarget)) {
                Assert.assertEquals(400, response.getStatus());
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(response.readEntity(String.class));
                Assert.assertEquals("invalid_grant", jsonNode.get("error").asText());
                Assert.assertEquals("Account is not fully set up", jsonNode.get("error_description").asText());
            }
            // wrong password is 401 => Invalid user credentials
            try (Response response = executeGrantAccessTokenRequestWrongPassword(grantTarget)) {
                Assert.assertEquals(401, response.getStatus());
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(response.readEntity(String.class));
                Assert.assertEquals("invalid_grant", jsonNode.get("error").asText());
                Assert.assertEquals("Invalid user credentials", jsonNode.get("error_description").asText());
            }
            {
                UserResource userResource = ApiUtil.findUserByUsernameId(adminClient.realm("test"), "test-user@localhost");
                UserRepresentation userRepresentation = userResource.toRepresentation();
                userRepresentation.getRequiredActions().remove(UPDATE_PASSWORD.toString());
                userResource.update(userRepresentation);
            }
        }
        {
            // test user disabled
            {
                UserResource userResource = ApiUtil.findUserByUsernameId(adminClient.realm("test"), "test-user@localhost");
                UserRepresentation userRepresentation = userResource.toRepresentation();
                userRepresentation.setEnabled(false);
                userResource.update(userRepresentation);
            }
            Response response = executeGrantAccessTokenRequest(grantTarget);
            Assert.assertEquals(400, response.getStatus());
            response.close();
            {
                UserResource userResource = ApiUtil.findUserByUsernameId(adminClient.realm("test"), "test-user@localhost");
                UserRepresentation userRepresentation = userResource.toRepresentation();
                userRepresentation.setEnabled(true);
                userResource.update(userRepresentation);
            }
        }
        {
            Response response = executeGrantAccessTokenRequest(grantTarget);
            Assert.assertEquals(200, response.getStatus());
            org.keycloak.representations.AccessTokenResponse tokenResponse = response.readEntity(org.keycloak.representations.AccessTokenResponse.class);
            response.close();
        }
        client.close();
        events.clear();
    }

    @Test
    public void testKeycloak2221() throws Exception {
        Client client = ClientBuilder.newClient();
        UriBuilder builder = UriBuilder.fromUri(OAuthClient.AUTH_SERVER_ROOT);
        URI grantUri = OIDCLoginProtocolService.tokenUrl(builder).build("test");
        WebTarget grantTarget = client.target(grantUri);
        ClientResource clientResource;
        {
            clientResource = ApiUtil.findClientByClientId(adminClient.realm("test"), "test-app");
            clientResource.getProtocolMappers().createMapper(ProtocolMapperUtil.createRoleNameMapper("rename-role", "user", "realm-user"));
            clientResource.getProtocolMappers().createMapper(ProtocolMapperUtil.createRoleNameMapper("rename-role2", "admin", "the-admin"));
        }
        {
            Response response = executeGrantRequest(grantTarget, "no-permissions", "password");
            Assert.assertEquals(200, response.getStatus());
            org.keycloak.representations.AccessTokenResponse tokenResponse = response.readEntity(org.keycloak.representations.AccessTokenResponse.class);
            AccessToken accessToken = getAccessToken(tokenResponse);
            Assert.assertEquals(accessToken.getRealmAccess().getRoles().size(), 1);
            Assert.assertTrue(accessToken.getRealmAccess().getRoles().contains("realm-user"));
            response.close();
        }
        // undo mappers
        {
            ClientResource app = ApiUtil.findClientByClientId(adminClient.realm("test"), "test-app");
            ClientRepresentation clientRepresentation = app.toRepresentation();
            for (ProtocolMapperRepresentation protocolRep : clientRepresentation.getProtocolMappers()) {
                if (protocolRep.getName().startsWith("rename-role")) {
                    clientResource.getProtocolMappers().delete(protocolRep.getId());
                }
            }
        }
        events.clear();
    }

    @Test
    public void testClientScope() throws Exception {
        RealmResource realm = adminClient.realm("test");
        RoleRepresentation realmRole = new RoleRepresentation();
        realmRole.setName("realm-test-role");
        realm.roles().create(realmRole);
        realmRole = realm.roles().get("realm-test-role").toRepresentation();
        RoleRepresentation realmRole2 = new RoleRepresentation();
        realmRole2.setName("realm-test-role2");
        realm.roles().create(realmRole2);
        realmRole2 = realm.roles().get("realm-test-role2").toRepresentation();
        List<UserRepresentation> users = realm.users().search("test-user@localhost", (-1), (-1));
        Assert.assertEquals(1, users.size());
        UserRepresentation user = users.get(0);
        List<RoleRepresentation> addRoles = new LinkedList<>();
        addRoles.add(realmRole);
        addRoles.add(realmRole2);
        realm.users().get(user.getId()).roles().realmLevel().add(addRoles);
        ClientScopeRepresentation rep = new ClientScopeRepresentation();
        rep.setName("scope");
        rep.setProtocol("openid-connect");
        Response response = realm.clientScopes().create(rep);
        Assert.assertEquals(201, response.getStatus());
        URI scopeUri = response.getLocation();
        String clientScopeId = ApiUtil.getCreatedId(response);
        response.close();
        ClientScopeResource clientScopeResource = adminClient.proxy(ClientScopeResource.class, scopeUri);
        ProtocolMapperModel hard = HardcodedClaim.create("hard", "hard", "coded", "String", true, true);
        ProtocolMapperRepresentation mapper = ModelToRepresentation.toRepresentation(hard);
        response = clientScopeResource.getProtocolMappers().createMapper(mapper);
        Assert.assertEquals(201, response.getStatus());
        response.close();
        ClientRepresentation clientRep = ApiUtil.findClientByClientId(realm, "test-app").toRepresentation();
        realm.clients().get(clientRep.getId()).addDefaultClientScope(clientScopeId);
        clientRep.setFullScopeAllowed(false);
        realm.clients().get(clientRep.getId()).update(clientRep);
        {
            Client client = ClientBuilder.newClient();
            UriBuilder builder = UriBuilder.fromUri(OAuthClient.AUTH_SERVER_ROOT);
            URI grantUri = OIDCLoginProtocolService.tokenUrl(builder).build("test");
            WebTarget grantTarget = client.target(grantUri);
            response = executeGrantAccessTokenRequest(grantTarget);
            Assert.assertEquals(200, response.getStatus());
            org.keycloak.representations.AccessTokenResponse tokenResponse = response.readEntity(org.keycloak.representations.AccessTokenResponse.class);
            IDToken idToken = getIdToken(tokenResponse);
            Assert.assertEquals("coded", idToken.getOtherClaims().get("hard"));
            AccessToken accessToken = getAccessToken(tokenResponse);
            Assert.assertEquals("coded", accessToken.getOtherClaims().get("hard"));
            // check zero scope for client scope
            Assert.assertFalse(accessToken.getRealmAccess().getRoles().contains(realmRole.getName()));
            Assert.assertFalse(accessToken.getRealmAccess().getRoles().contains(realmRole2.getName()));
            response.close();
            client.close();
        }
        // test that scope is added
        List<RoleRepresentation> addRole1 = new LinkedList<>();
        addRole1.add(realmRole);
        clientScopeResource.getScopeMappings().realmLevel().add(addRole1);
        {
            Client client = ClientBuilder.newClient();
            UriBuilder builder = UriBuilder.fromUri(OAuthClient.AUTH_SERVER_ROOT);
            URI grantUri = OIDCLoginProtocolService.tokenUrl(builder).build("test");
            WebTarget grantTarget = client.target(grantUri);
            response = executeGrantAccessTokenRequest(grantTarget);
            Assert.assertEquals(200, response.getStatus());
            org.keycloak.representations.AccessTokenResponse tokenResponse = response.readEntity(org.keycloak.representations.AccessTokenResponse.class);
            AccessToken accessToken = getAccessToken(tokenResponse);
            // check single role in scope for client scope
            Assert.assertNotNull(accessToken.getRealmAccess());
            Assert.assertTrue(accessToken.getRealmAccess().getRoles().contains(realmRole.getName()));
            Assert.assertFalse(accessToken.getRealmAccess().getRoles().contains(realmRole2.getName()));
            response.close();
            client.close();
        }
        // test combined scopes
        List<RoleRepresentation> addRole2 = new LinkedList<>();
        addRole2.add(realmRole2);
        realm.clients().get(clientRep.getId()).getScopeMappings().realmLevel().add(addRole2);
        {
            Client client = ClientBuilder.newClient();
            UriBuilder builder = UriBuilder.fromUri(OAuthClient.AUTH_SERVER_ROOT);
            URI grantUri = OIDCLoginProtocolService.tokenUrl(builder).build("test");
            WebTarget grantTarget = client.target(grantUri);
            response = executeGrantAccessTokenRequest(grantTarget);
            Assert.assertEquals(200, response.getStatus());
            org.keycloak.representations.AccessTokenResponse tokenResponse = response.readEntity(org.keycloak.representations.AccessTokenResponse.class);
            AccessToken accessToken = getAccessToken(tokenResponse);
            // check zero scope for client scope
            Assert.assertNotNull(accessToken.getRealmAccess());
            Assert.assertTrue(accessToken.getRealmAccess().getRoles().contains(realmRole.getName()));
            Assert.assertTrue(accessToken.getRealmAccess().getRoles().contains(realmRole2.getName()));
            response.close();
            client.close();
        }
        // remove scopes and retest
        clientScopeResource.getScopeMappings().realmLevel().remove(addRole1);
        realm.clients().get(clientRep.getId()).getScopeMappings().realmLevel().remove(addRole2);
        {
            Client client = ClientBuilder.newClient();
            UriBuilder builder = UriBuilder.fromUri(OAuthClient.AUTH_SERVER_ROOT);
            URI grantUri = OIDCLoginProtocolService.tokenUrl(builder).build("test");
            WebTarget grantTarget = client.target(grantUri);
            response = executeGrantAccessTokenRequest(grantTarget);
            Assert.assertEquals(200, response.getStatus());
            org.keycloak.representations.AccessTokenResponse tokenResponse = response.readEntity(org.keycloak.representations.AccessTokenResponse.class);
            AccessToken accessToken = getAccessToken(tokenResponse);
            Assert.assertFalse(accessToken.getRealmAccess().getRoles().contains(realmRole.getName()));
            Assert.assertFalse(accessToken.getRealmAccess().getRoles().contains(realmRole2.getName()));
            response.close();
            client.close();
        }
        // test don't use client scope scope. Add roles back to the clientScope, but they won't be available
        realm.clients().get(clientRep.getId()).removeDefaultClientScope(clientScopeId);
        clientScopeResource.getScopeMappings().realmLevel().add(addRole1);
        clientScopeResource.getScopeMappings().realmLevel().add(addRole2);
        {
            Client client = ClientBuilder.newClient();
            UriBuilder builder = UriBuilder.fromUri(OAuthClient.AUTH_SERVER_ROOT);
            URI grantUri = OIDCLoginProtocolService.tokenUrl(builder).build("test");
            WebTarget grantTarget = client.target(grantUri);
            response = executeGrantAccessTokenRequest(grantTarget);
            Assert.assertEquals(200, response.getStatus());
            org.keycloak.representations.AccessTokenResponse tokenResponse = response.readEntity(org.keycloak.representations.AccessTokenResponse.class);
            AccessToken accessToken = getAccessToken(tokenResponse);
            Assert.assertFalse(accessToken.getRealmAccess().getRoles().contains(realmRole.getName()));
            Assert.assertFalse(accessToken.getRealmAccess().getRoles().contains(realmRole2.getName()));
            Assert.assertNull(accessToken.getOtherClaims().get("hard"));
            IDToken idToken = getIdToken(tokenResponse);
            Assert.assertNull(idToken.getOtherClaims().get("hard"));
            response.close();
            client.close();
        }
        // undo mappers
        realm.users().get(user.getId()).roles().realmLevel().remove(addRoles);
        realm.roles().get(realmRole.getName()).remove();
        realm.roles().get(realmRole2.getName()).remove();
        clientScopeResource.remove();
        {
            Client client = ClientBuilder.newClient();
            UriBuilder builder = UriBuilder.fromUri(OAuthClient.AUTH_SERVER_ROOT);
            URI grantUri = OIDCLoginProtocolService.tokenUrl(builder).build("test");
            WebTarget grantTarget = client.target(grantUri);
            response = executeGrantAccessTokenRequest(grantTarget);
            Assert.assertEquals(200, response.getStatus());
            org.keycloak.representations.AccessTokenResponse tokenResponse = response.readEntity(org.keycloak.representations.AccessTokenResponse.class);
            IDToken idToken = getIdToken(tokenResponse);
            Assert.assertNull(idToken.getOtherClaims().get("hard"));
            AccessToken accessToken = getAccessToken(tokenResponse);
            Assert.assertNull(accessToken.getOtherClaims().get("hard"));
            response.close();
            client.close();
        }
        events.clear();
    }

    // KEYCLOAK-1595 Assert that public client is able to retrieve token even if header "Authorization: Negotiate something" was used (parameter client_id has preference in this case)
    @Test
    public void testAuthorizationNegotiateHeaderIgnored() throws Exception {
        adminClient.realm("test").clients().create(create().clientId("sample-public-client").authenticatorType("client-secret").redirectUris(((oauth.getRedirectUri()) + "/*")).publicClient().build());
        oauth.clientId("sample-public-client");
        oauth.doLogin("test-user@localhost", "password");
        EventRepresentation loginEvent = events.expectLogin().client("sample-public-client").assertEvent();
        String sessionId = loginEvent.getSessionId();
        String codeId = loginEvent.getDetails().get(CODE_ID);
        String code = oauth.getCurrentQuery().get(CODE);
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            HttpPost post = new HttpPost(oauth.getAccessTokenUrl());
            List<NameValuePair> parameters = new LinkedList<>();
            parameters.add(new BasicNameValuePair(OAuth2Constants.GRANT_TYPE, OAuth2Constants.AUTHORIZATION_CODE));
            parameters.add(new BasicNameValuePair(OAuth2Constants.CODE, code));
            parameters.add(new BasicNameValuePair(OAuth2Constants.REDIRECT_URI, oauth.getRedirectUri()));
            parameters.add(new BasicNameValuePair(OAuth2Constants.CLIENT_ID, oauth.getClientId()));
            post.setHeader("Authorization", "Negotiate something-which-will-be-ignored");
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(parameters, "UTF-8");
            post.setEntity(formEntity);
            OAuthClient.AccessTokenResponse response = new OAuthClient.AccessTokenResponse(client.execute(post));
            Assert.assertEquals(200, response.getStatusCode());
            AccessToken token = oauth.verifyToken(response.getAccessToken());
            events.expectCodeToToken(codeId, sessionId).client("sample-public-client").assertEvent();
        }
    }

    // KEYCLOAK-4215
    @Test
    public void expiration() throws Exception {
        int sessionMax = ((int) (TimeUnit.MINUTES.toSeconds(30)));
        int sessionIdle = ((int) (TimeUnit.MINUTES.toSeconds(30)));
        int tokenLifespan = ((int) (TimeUnit.MINUTES.toSeconds(5)));
        RealmResource realm = adminClient.realm("test");
        RealmRepresentation rep = realm.toRepresentation();
        Integer originalSessionMax = rep.getSsoSessionMaxLifespan();
        rep.setSsoSessionMaxLifespan(sessionMax);
        realm.update(rep);
        try {
            oauth.doLogin("test-user@localhost", "password");
            String code = oauth.getCurrentQuery().get(CODE);
            OAuthClient.AccessTokenResponse response = oauth.doAccessTokenRequest(code, "password");
            Assert.assertEquals(200, response.getStatusCode());
            // Assert refresh expiration equals session idle
            assertExpiration(response.getRefreshExpiresIn(), sessionIdle);
            // Assert token expiration equals token lifespan
            assertExpiration(response.getExpiresIn(), tokenLifespan);
            setTimeOffset((sessionMax - 60));
            response = oauth.doRefreshTokenRequest(response.getRefreshToken(), "password");
            Assert.assertEquals(200, response.getStatusCode());
            // Assert expiration equals session expiration
            assertExpiration(response.getRefreshExpiresIn(), 60);
            assertExpiration(response.getExpiresIn(), 60);
        } finally {
            rep.setSsoSessionMaxLifespan(originalSessionMax);
            realm.update(rep);
        }
    }

    @Test
    public void accessTokenResponseHeader() throws Exception {
        oauth.doLogin("test-user@localhost", "password");
        EventRepresentation loginEvent = events.expectLogin().assertEvent();
        String sessionId = loginEvent.getSessionId();
        String codeId = loginEvent.getDetails().get(CODE_ID);
        String code = oauth.getCurrentQuery().get(CODE);
        OAuthClient.AccessTokenResponse response = oauth.doAccessTokenRequest(code, "password");
        Assert.assertEquals(200, response.getStatusCode());
        Map<String, String> headers = response.getHeaders();
        Assert.assertEquals("application/json", headers.get("Content-Type"));
        Assert.assertEquals("no-store", headers.get("Cache-Control"));
        Assert.assertEquals("no-cache", headers.get("Pragma"));
    }

    @Test
    public void accessTokenRequest_RealmRS256_ClientRS384_EffectiveRS384() throws Exception {
        try {
            TokenSignatureUtil.changeRealmTokenSignatureProvider(adminClient, RS256);
            TokenSignatureUtil.changeClientAccessTokenSignatureProvider(ApiUtil.findClientByClientId(adminClient.realm("test"), "test-app"), RS384);
            tokenRequest(HS256, RS384, RS256);
        } finally {
            TokenSignatureUtil.changeClientAccessTokenSignatureProvider(ApiUtil.findClientByClientId(adminClient.realm("test"), "test-app"), RS256);
        }
    }

    @Test
    public void accessTokenRequest_RealmRS512_ClientRS512_EffectiveRS512() throws Exception {
        try {
            TokenSignatureUtil.changeRealmTokenSignatureProvider(adminClient, RS512);
            TokenSignatureUtil.changeClientAccessTokenSignatureProvider(ApiUtil.findClientByClientId(adminClient.realm("test"), "test-app"), RS512);
            tokenRequest(HS256, RS512, RS512);
        } finally {
            TokenSignatureUtil.changeRealmTokenSignatureProvider(adminClient, RS256);
            TokenSignatureUtil.changeClientAccessTokenSignatureProvider(ApiUtil.findClientByClientId(adminClient.realm("test"), "test-app"), RS256);
        }
    }

    @Test
    public void accessTokenRequest_RealmRS256_ClientES256_EffectiveES256() throws Exception {
        try {
            TokenSignatureUtil.changeRealmTokenSignatureProvider(adminClient, RS256);
            TokenSignatureUtil.changeClientAccessTokenSignatureProvider(ApiUtil.findClientByClientId(adminClient.realm("test"), "test-app"), ES256);
            TokenSignatureUtil.registerKeyProvider("P-256", adminClient, testContext);
            tokenRequestSignatureVerifyOnly(HS256, ES256, RS256);
        } finally {
            TokenSignatureUtil.changeClientAccessTokenSignatureProvider(ApiUtil.findClientByClientId(adminClient.realm("test"), "test-app"), RS256);
        }
    }

    @Test
    public void accessTokenRequest_RealmES384_ClientES384_EffectiveES384() throws Exception {
        try {
            TokenSignatureUtil.changeRealmTokenSignatureProvider(adminClient, ES384);
            TokenSignatureUtil.changeClientAccessTokenSignatureProvider(ApiUtil.findClientByClientId(adminClient.realm("test"), "test-app"), ES384);
            TokenSignatureUtil.registerKeyProvider("P-384", adminClient, testContext);
            tokenRequestSignatureVerifyOnly(HS256, ES384, ES384);
        } finally {
            TokenSignatureUtil.changeRealmTokenSignatureProvider(adminClient, RS256);
            TokenSignatureUtil.changeClientAccessTokenSignatureProvider(ApiUtil.findClientByClientId(adminClient.realm("test"), "test-app"), RS256);
        }
    }

    @Test
    public void accessTokenRequest_RealmRS256_ClientES512_EffectiveES512() throws Exception {
        try {
            TokenSignatureUtil.changeRealmTokenSignatureProvider(adminClient, RS256);
            TokenSignatureUtil.changeClientAccessTokenSignatureProvider(ApiUtil.findClientByClientId(adminClient.realm("test"), "test-app"), ES512);
            TokenSignatureUtil.registerKeyProvider("P-521", adminClient, testContext);
            tokenRequestSignatureVerifyOnly(HS256, ES512, RS256);
        } finally {
            TokenSignatureUtil.changeClientAccessTokenSignatureProvider(ApiUtil.findClientByClientId(adminClient.realm("test"), "test-app"), RS256);
        }
    }

    @Test
    public void clientAccessTokenLifespanOverride() {
        ClientResource client = ApiUtil.findClientByClientId(adminClient.realm("test"), "test-app");
        ClientRepresentation clientRep = client.toRepresentation();
        RealmResource realm = adminClient.realm("test");
        RealmRepresentation rep = realm.toRepresentation();
        int sessionMax = rep.getSsoSessionMaxLifespan();
        int accessTokenLifespan = rep.getAccessTokenLifespan();
        // Make sure realm lifespan is not same as client override
        Assert.assertNotEquals(accessTokenLifespan, 500);
        try {
            clientRep.getAttributes().put(ACCESS_TOKEN_LIFESPAN, "500");
            client.update(clientRep);
            oauth.doLogin("test-user@localhost", "password");
            // Check access token expires in 500 seconds as specified on client
            String code = oauth.getCurrentQuery().get(CODE);
            OAuthClient.AccessTokenResponse response = oauth.doAccessTokenRequest(code, "password");
            Assert.assertEquals(200, response.getStatusCode());
            assertExpiration(response.getExpiresIn(), 500);
            // Check access token expires when session expires
            clientRep.getAttributes().put(ACCESS_TOKEN_LIFESPAN, "-1");
            client.update(clientRep);
            String refreshToken = response.getRefreshToken();
            response = oauth.doRefreshTokenRequest(refreshToken, "password");
            Assert.assertEquals(200, response.getStatusCode());
            assertExpiration(response.getExpiresIn(), sessionMax);
        } finally {
            clientRep.getAttributes().put(ACCESS_TOKEN_LIFESPAN, null);
            client.update(clientRep);
        }
    }
}

