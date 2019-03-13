/**
 * Copyright 2017 Red Hat, Inc. and/or its affiliates
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
package org.keycloak.testsuite.authz;


import AccessToken.Authorization;
import HttpHeaders.AUTHORIZATION;
import OAuth2Constants.REFRESH_TOKEN;
import java.net.URI;
import java.util.Collection;
import java.util.Map;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.UriBuilder;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.admin.client.resource.AuthorizationResource;
import org.keycloak.authorization.client.AuthorizationDeniedException;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.representation.TokenIntrospectionResponse;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.protocol.oidc.OIDCLoginProtocolService;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.authorization.AuthorizationResponse;
import org.keycloak.representations.idm.authorization.Permission;
import org.keycloak.representations.idm.authorization.PermissionRequest;
import org.keycloak.representations.idm.authorization.ResourcePermissionRepresentation;
import org.keycloak.representations.idm.authorization.ResourceRepresentation;
import org.keycloak.representations.idm.authorization.ScopePermissionRepresentation;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.util.OAuthClient;
import org.keycloak.util.BasicAuthHelper;
import org.keycloak.util.JsonSerialization;


/**
 *
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Igor</a>
 */
public class UmaGrantTypeTest extends AbstractResourceServerTest {
    private ResourceRepresentation resourceA;

    @Test
    public void testObtainRptWithClientAdditionalScopes() throws Exception {
        AuthorizationResponse response = authorize("marta", "password", "Resource A", new String[]{ "ScopeA", "ScopeB" }, new String[]{ "ScopeC" });
        AccessToken accessToken = toAccessToken(response.getToken());
        AccessToken.Authorization authorization = accessToken.getAuthorization();
        Collection<Permission> permissions = authorization.getPermissions();
        Assert.assertNotNull(permissions);
        assertPermissions(permissions, "Resource A", "ScopeA", "ScopeB", "ScopeC");
        Assert.assertTrue(permissions.isEmpty());
    }

    @Test
    public void testObtainRptWithUpgrade() throws Exception {
        AuthorizationResponse response = authorize("marta", "password", "Resource A", new String[]{ "ScopeA", "ScopeB" });
        String rpt = response.getToken();
        AccessToken.Authorization authorization = toAccessToken(rpt).getAuthorization();
        Collection<Permission> permissions = authorization.getPermissions();
        Assert.assertNotNull(permissions);
        assertPermissions(permissions, "Resource A", "ScopeA", "ScopeB");
        Assert.assertTrue(permissions.isEmpty());
        response = authorize("marta", "password", "Resource A", new String[]{ "ScopeC" }, rpt);
        Assert.assertTrue(response.isUpgraded());
        authorization = toAccessToken(response.getToken()).getAuthorization();
        permissions = authorization.getPermissions();
        Assert.assertNotNull(permissions);
        assertPermissions(permissions, "Resource A", "ScopeA", "ScopeB", "ScopeC");
        Assert.assertTrue(permissions.isEmpty());
    }

    @Test
    public void testObtainRptWithUpgradeOnlyScopes() throws Exception {
        AuthorizationResponse response = authorize("marta", "password", null, new String[]{ "ScopeA", "ScopeB" });
        String rpt = response.getToken();
        AccessToken.Authorization authorization = toAccessToken(rpt).getAuthorization();
        Collection<Permission> permissions = authorization.getPermissions();
        Assert.assertFalse(response.isUpgraded());
        Assert.assertNotNull(permissions);
        assertPermissions(permissions, "Resource A", "ScopeA", "ScopeB");
        Assert.assertTrue(permissions.isEmpty());
        response = authorize("marta", "password", "Resource A", new String[]{ "ScopeC" }, rpt);
        authorization = toAccessToken(response.getToken()).getAuthorization();
        permissions = authorization.getPermissions();
        Assert.assertTrue(response.isUpgraded());
        Assert.assertNotNull(permissions);
        assertPermissions(permissions, "Resource A", "ScopeA", "ScopeB", "ScopeC");
        Assert.assertTrue(permissions.isEmpty());
    }

    @Test
    public void testObtainRptWithUpgradeWithUnauthorizedResource() throws Exception {
        AuthorizationResponse response = authorize("marta", "password", "Resource A", new String[]{ "ScopeA", "ScopeB" });
        String rpt = response.getToken();
        AccessToken.Authorization authorization = toAccessToken(rpt).getAuthorization();
        Collection<Permission> permissions = authorization.getPermissions();
        Assert.assertFalse(response.isUpgraded());
        Assert.assertNotNull(permissions);
        assertPermissions(permissions, "Resource A", "ScopeA", "ScopeB");
        Assert.assertTrue(permissions.isEmpty());
        ResourcePermissionRepresentation permission = new ResourcePermissionRepresentation();
        ResourceRepresentation resourceB = addResource("Resource B", "ScopeA", "ScopeB", "ScopeC");
        permission.setName(((resourceB.getName()) + " Permission"));
        permission.addResource(resourceB.getName());
        permission.addPolicy("Deny Policy");
        getClient(getRealm()).authorization().permissions().resource().create(permission).close();
        try {
            authorize("marta", "password", "Resource B", new String[]{ "ScopeC" }, rpt);
            Assert.fail("Should be denied, resource b not granted");
        } catch (AuthorizationDeniedException ignore) {
        }
    }

    @Test
    public void testObtainRptWithUpgradeWithUnauthorizedResourceFromRpt() throws Exception {
        ResourcePermissionRepresentation permissionA = new ResourcePermissionRepresentation();
        ResourceRepresentation resourceA = addResource(KeycloakModelUtils.generateId(), "ScopeA", "ScopeB", "ScopeC");
        permissionA.setName(((resourceA.getName()) + " Permission"));
        permissionA.addResource(resourceA.getName());
        permissionA.addPolicy("Default Policy");
        AuthorizationResource authzResource = getClient(getRealm()).authorization();
        authzResource.permissions().resource().create(permissionA).close();
        AuthorizationResponse response = authorize("marta", "password", resourceA.getId(), new String[]{ "ScopeA", "ScopeB" });
        String rpt = response.getToken();
        AccessToken.Authorization authorization = toAccessToken(rpt).getAuthorization();
        Collection<Permission> permissions = authorization.getPermissions();
        Assert.assertFalse(response.isUpgraded());
        Assert.assertNotNull(permissions);
        assertPermissions(permissions, resourceA.getName(), "ScopeA", "ScopeB");
        Assert.assertTrue(permissions.isEmpty());
        ResourceRepresentation resourceB = addResource(KeycloakModelUtils.generateId(), "ScopeA", "ScopeB", "ScopeC");
        ResourcePermissionRepresentation permissionB = new ResourcePermissionRepresentation();
        permissionB.setName(((resourceB.getName()) + " Permission"));
        permissionB.addResource(resourceB.getName());
        permissionB.addPolicy("Default Policy");
        authzResource.permissions().resource().create(permissionB).close();
        response = authorize("marta", "password", resourceB.getId(), new String[]{ "ScopeC" }, rpt);
        rpt = response.getToken();
        authorization = toAccessToken(rpt).getAuthorization();
        permissions = authorization.getPermissions();
        Assert.assertTrue(response.isUpgraded());
        Assert.assertNotNull(permissions);
        assertPermissions(permissions, resourceA.getName(), "ScopeA", "ScopeB");
        assertPermissions(permissions, resourceB.getName(), "ScopeC");
        Assert.assertTrue(permissions.isEmpty());
        permissionB = authzResource.permissions().resource().findByName(permissionB.getName());
        permissionB.removePolicy("Default Policy");
        permissionB.addPolicy("Deny Policy");
        authzResource.permissions().resource().findById(permissionB.getId()).update(permissionB);
        response = authorize("marta", "password", resourceA.getId(), new String[]{ "ScopeC" }, rpt);
        rpt = response.getToken();
        authorization = toAccessToken(rpt).getAuthorization();
        permissions = authorization.getPermissions();
        Assert.assertFalse(response.isUpgraded());
        Assert.assertNotNull(permissions);
        assertPermissions(permissions, resourceA.getName(), "ScopeA", "ScopeB", "ScopeC");
        Assert.assertTrue(permissions.isEmpty());
    }

    @Test
    public void testObtainRptOnlyAuthorizedScopes() throws Exception {
        ResourceRepresentation resourceA = addResource(KeycloakModelUtils.generateId(), "READ", "WRITE");
        ScopePermissionRepresentation permissionA = new ScopePermissionRepresentation();
        permissionA.setName(KeycloakModelUtils.generateId());
        permissionA.addScope("READ");
        permissionA.addPolicy("Default Policy");
        AuthorizationResource authzResource = getClient(getRealm()).authorization();
        authzResource.permissions().scope().create(permissionA).close();
        ScopePermissionRepresentation permissionB = new ScopePermissionRepresentation();
        permissionB.setName(KeycloakModelUtils.generateId());
        permissionB.addScope("WRITE");
        permissionB.addPolicy("Deny Policy");
        authzResource.permissions().scope().create(permissionB).close();
        AuthorizationResponse response = authorize("marta", "password", resourceA.getName(), new String[]{ "READ" });
        String rpt = response.getToken();
        AccessToken.Authorization authorization = toAccessToken(rpt).getAuthorization();
        Collection<Permission> permissions = authorization.getPermissions();
        Assert.assertFalse(response.isUpgraded());
        Assert.assertNotNull(permissions);
        assertPermissions(permissions, resourceA.getName(), "READ");
        Assert.assertTrue(permissions.isEmpty());
        response = authorize("marta", "password", resourceA.getName(), new String[]{ "READ", "WRITE" });
        rpt = response.getToken();
        authorization = toAccessToken(rpt).getAuthorization();
        permissions = authorization.getPermissions();
        Assert.assertFalse(response.isUpgraded());
        Assert.assertNotNull(permissions);
        assertPermissions(permissions, resourceA.getName(), "READ");
        Assert.assertTrue(permissions.isEmpty());
    }

    @Test
    public void testObtainRptWithOwnerManagedResource() throws Exception {
        ResourcePermissionRepresentation permission = new ResourcePermissionRepresentation();
        ResourceRepresentation resourceA = addResource("Resource Marta", "marta", true, "ScopeA", "ScopeB", "ScopeC");
        permission.setName(((resourceA.getName()) + " Permission"));
        permission.addResource(resourceA.getId());
        permission.addPolicy("Default Policy");
        getClient(getRealm()).authorization().permissions().resource().create(permission).close();
        ResourceRepresentation resourceB = addResource("Resource B", "marta", "ScopeA", "ScopeB", "ScopeC");
        permission.setName(((resourceB.getName()) + " Permission"));
        permission.addResource(resourceB.getId());
        permission.addPolicy("Default Policy");
        getClient(getRealm()).authorization().permissions().resource().create(permission).close();
        AuthorizationResponse response = authorize("marta", "password", new PermissionRequest(resourceA.getName(), "ScopeA", "ScopeB"), new PermissionRequest(resourceB.getName(), "ScopeC"));
        String rpt = response.getToken();
        AccessToken.Authorization authorization = toAccessToken(rpt).getAuthorization();
        Collection<Permission> permissions = authorization.getPermissions();
        Assert.assertNotNull(permissions);
        assertPermissions(permissions, resourceA.getName(), "ScopeA", "ScopeB");
        assertPermissions(permissions, resourceB.getName(), "ScopeC");
        Assert.assertTrue(permissions.isEmpty());
    }

    @Test
    public void testObtainRptWithClientCredentials() throws Exception {
        AuthorizationResponse response = authorize("Resource A", new String[]{ "ScopeA", "ScopeB" });
        String rpt = response.getToken();
        Assert.assertNotNull(rpt);
        Assert.assertFalse(response.isUpgraded());
        AccessToken accessToken = toAccessToken(rpt);
        AccessToken.Authorization authorization = accessToken.getAuthorization();
        Assert.assertNotNull(authorization);
        Collection<Permission> permissions = authorization.getPermissions();
        Assert.assertNotNull(permissions);
        assertPermissions(permissions, "Resource A", "ScopeA", "ScopeB");
        Assert.assertTrue(permissions.isEmpty());
    }

    @Test
    public void testObtainRptUsingAccessToken() throws Exception {
        AccessTokenResponse accessTokenResponse = getAuthzClient().obtainAccessToken("marta", "password");
        AuthorizationResponse response = authorize(null, null, null, null, accessTokenResponse.getToken(), null, null, new PermissionRequest("Resource A", "ScopeA", "ScopeB"));
        String rpt = response.getToken();
        Assert.assertNotNull(rpt);
        Assert.assertFalse(response.isUpgraded());
        AccessToken accessToken = toAccessToken(rpt);
        AccessToken.Authorization authorization = accessToken.getAuthorization();
        Assert.assertNotNull(authorization);
        Collection<Permission> permissions = authorization.getPermissions();
        Assert.assertNotNull(permissions);
        assertPermissions(permissions, "Resource A", "ScopeA", "ScopeB");
        Assert.assertTrue(permissions.isEmpty());
    }

    @Test
    public void testRefreshRpt() {
        AccessTokenResponse accessTokenResponse = getAuthzClient().obtainAccessToken("marta", "password");
        AuthorizationResponse response = authorize(null, null, null, null, accessTokenResponse.getToken(), null, null, new PermissionRequest("Resource A", "ScopeA", "ScopeB"));
        String rpt = response.getToken();
        Assert.assertNotNull(rpt);
        AccessToken accessToken = toAccessToken(rpt);
        AccessToken.Authorization authorization = accessToken.getAuthorization();
        Assert.assertNotNull(authorization);
        Collection<Permission> permissions = authorization.getPermissions();
        Assert.assertNotNull(permissions);
        assertPermissions(permissions, "Resource A", "ScopeA", "ScopeB");
        Assert.assertTrue(permissions.isEmpty());
        String refreshToken = response.getRefreshToken();
        Assert.assertNotNull(refreshToken);
        AccessToken refreshTokenToken = toAccessToken(refreshToken);
        Assert.assertNotNull(refreshTokenToken.getAuthorization());
        Client client = ClientBuilder.newClient();
        UriBuilder builder = UriBuilder.fromUri(OAuthClient.AUTH_SERVER_ROOT);
        URI uri = OIDCLoginProtocolService.tokenUrl(builder).build(AbstractResourceServerTest.REALM_NAME);
        WebTarget target = client.target(uri);
        Form parameters = new Form();
        parameters.param("grant_type", REFRESH_TOKEN);
        parameters.param(REFRESH_TOKEN, refreshToken);
        AccessTokenResponse refreshTokenResponse = target.request().header(AUTHORIZATION, BasicAuthHelper.createHeader("resource-server-test", "secret")).post(Entity.form(parameters)).readEntity(AccessTokenResponse.class);
        Assert.assertNotNull(refreshTokenResponse.getToken());
        refreshToken = refreshTokenResponse.getRefreshToken();
        refreshTokenToken = toAccessToken(refreshToken);
        Assert.assertNotNull(refreshTokenToken.getAuthorization());
        AccessToken refreshedToken = toAccessToken(rpt);
        authorization = refreshedToken.getAuthorization();
        Assert.assertNotNull(authorization);
        permissions = authorization.getPermissions();
        Assert.assertNotNull(permissions);
        assertPermissions(permissions, "Resource A", "ScopeA", "ScopeB");
        Assert.assertTrue(permissions.isEmpty());
        refreshTokenResponse = target.request().header(AUTHORIZATION, BasicAuthHelper.createHeader("resource-server-test", "secret")).post(Entity.form(parameters)).readEntity(AccessTokenResponse.class);
        Assert.assertNotNull(refreshTokenResponse.getToken());
        refreshToken = refreshTokenResponse.getRefreshToken();
        refreshTokenToken = toAccessToken(refreshToken);
        Assert.assertNotNull(refreshTokenToken.getAuthorization());
        refreshedToken = toAccessToken(rpt);
        authorization = refreshedToken.getAuthorization();
        Assert.assertNotNull(authorization);
        permissions = authorization.getPermissions();
        Assert.assertNotNull(permissions);
        assertPermissions(permissions, "Resource A", "ScopeA", "ScopeB");
        Assert.assertTrue(permissions.isEmpty());
    }

    @Test
    public void testObtainRptWithIDToken() throws Exception {
        String idToken = getIdToken("marta", "password");
        AuthorizationResponse response = authorize("Resource A", new String[]{ "ScopeA", "ScopeB" }, idToken, "http://openid.net/specs/openid-connect-core-1_0.html#IDToken");
        String rpt = response.getToken();
        Assert.assertNotNull(rpt);
        Assert.assertFalse(response.isUpgraded());
        AccessToken accessToken = toAccessToken(rpt);
        AccessToken.Authorization authorization = accessToken.getAuthorization();
        Assert.assertNotNull(authorization);
        Collection<Permission> permissions = authorization.getPermissions();
        Assert.assertNotNull(permissions);
        assertPermissions(permissions, "Resource A", "ScopeA", "ScopeB");
        Assert.assertTrue(permissions.isEmpty());
    }

    @Test
    public void testTokenIntrospect() throws Exception {
        AuthzClient authzClient = getAuthzClient();
        AccessTokenResponse accessTokenResponse = authzClient.obtainAccessToken("marta", "password");
        AuthorizationResponse response = authorize(null, null, null, null, accessTokenResponse.getToken(), null, null, new PermissionRequest("Resource A", "ScopeA", "ScopeB"));
        String rpt = response.getToken();
        Assert.assertNotNull(rpt);
        Assert.assertFalse(response.isUpgraded());
        AccessToken accessToken = toAccessToken(rpt);
        AccessToken.Authorization authorization = accessToken.getAuthorization();
        Assert.assertNotNull(authorization);
        Collection<Permission> permissions = authorization.getPermissions();
        Assert.assertNotNull(permissions);
        assertPermissions(permissions, "Resource A", "ScopeA", "ScopeB");
        Assert.assertTrue(permissions.isEmpty());
        TokenIntrospectionResponse introspectionResponse = authzClient.protection().introspectRequestingPartyToken(rpt);
        Assert.assertNotNull(introspectionResponse);
        Assert.assertNotNull(introspectionResponse.getPermissions());
        oauth.realm("authz-test");
        String introspectHttpResponse = oauth.introspectTokenWithClientCredential("resource-server-test", "secret", "requesting_party_token", rpt);
        Map jsonNode = JsonSerialization.readValue(introspectHttpResponse, Map.class);
        Assert.assertEquals(true, jsonNode.get("active"));
        Collection permissionClaims = ((Collection) (jsonNode.get("permissions")));
        Assert.assertNotNull(permissionClaims);
        Assert.assertEquals(1, permissionClaims.size());
        Map<String, Object> claim = ((Map) (permissionClaims.iterator().next()));
        Assert.assertThat(claim.keySet(), Matchers.containsInAnyOrder("resource_id", "rsname", "resource_scopes", "scopes", "rsid"));
        Assert.assertThat(claim.get("rsname"), Matchers.equalTo("Resource A"));
        ResourceRepresentation resourceRep = authzClient.protection().resource().findByName("Resource A");
        Assert.assertThat(claim.get("rsid"), Matchers.equalTo(resourceRep.getId()));
        Assert.assertThat(claim.get("resource_id"), Matchers.equalTo(resourceRep.getId()));
        Assert.assertThat(((Collection<String>) (claim.get("resource_scopes"))), Matchers.containsInAnyOrder("ScopeA", "ScopeB"));
        Assert.assertThat(((Collection<String>) (claim.get("scopes"))), Matchers.containsInAnyOrder("ScopeA", "ScopeB"));
    }
}

