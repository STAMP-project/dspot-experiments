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


import DecisionStrategy.AFFIRMATIVE;
import HardcodedClaim.CLAIM_VALUE;
import HardcodedClaim.PROVIDER_ID;
import OAuth2Constants.CODE;
import OIDCAttributeMapperHelper.INCLUDE_IN_ACCESS_TOKEN;
import OIDCAttributeMapperHelper.TOKEN_CLAIM_NAME;
import OIDCLoginProtocol.LOGIN_PROTOCOL;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.core.Response;
import org.hamcrest.Matchers;
import org.jboss.arquillian.container.test.api.ContainerController;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.admin.client.resource.AuthorizationResource;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.authorization.client.AuthorizationDeniedException;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.representation.TokenIntrospectionResponse;
import org.keycloak.authorization.client.util.HttpResponseException;
import org.keycloak.common.util.Base64Url;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.AccessToken.Authorization;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.ProtocolMapperRepresentation;
import org.keycloak.representations.idm.authorization.AuthorizationRequest;
import org.keycloak.representations.idm.authorization.AuthorizationRequest.Metadata;
import org.keycloak.representations.idm.authorization.AuthorizationResponse;
import org.keycloak.representations.idm.authorization.JSPolicyRepresentation;
import org.keycloak.representations.idm.authorization.Permission;
import org.keycloak.representations.idm.authorization.PermissionResponse;
import org.keycloak.representations.idm.authorization.PermissionTicketRepresentation;
import org.keycloak.representations.idm.authorization.ResourcePermissionRepresentation;
import org.keycloak.representations.idm.authorization.ResourceRepresentation;
import org.keycloak.representations.idm.authorization.ScopePermissionRepresentation;
import org.keycloak.representations.idm.authorization.ScopeRepresentation;
import org.keycloak.representations.idm.authorization.UserPolicyRepresentation;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.util.OAuthClient;
import org.keycloak.util.JsonSerialization;


/**
 *
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Igor</a>
 */
public class EntitlementAPITest extends AbstractAuthzTest {
    private static final String RESOURCE_SERVER_TEST = "resource-server-test";

    private static final String TEST_CLIENT = "test-client";

    private static final String AUTHZ_CLIENT_CONFIG = "default-keycloak.json";

    private static final String PAIRWISE_RESOURCE_SERVER_TEST = "pairwise-resource-server-test";

    private static final String PAIRWISE_TEST_CLIENT = "test-client-pairwise";

    private static final String PAIRWISE_AUTHZ_CLIENT_CONFIG = "default-keycloak-pairwise.json";

    private static final String PUBLIC_TEST_CLIENT = "test-public-client";

    private static final String PUBLIC_TEST_CLIENT_CONFIG = "default-keycloak-public-client.json";

    private AuthzClient authzClient;

    @ArquillianResource
    protected ContainerController controller;

    @Test
    public void testRptRequestWithoutResourceName() {
        testRptRequestWithoutResourceName(EntitlementAPITest.AUTHZ_CLIENT_CONFIG);
    }

    @Test
    public void testRptRequestWithoutResourceNamePairwise() {
        testRptRequestWithoutResourceName(EntitlementAPITest.PAIRWISE_AUTHZ_CLIENT_CONFIG);
    }

    @Test
    public void testRptRequestWithResourceName() {
        testRptRequestWithResourceName(EntitlementAPITest.AUTHZ_CLIENT_CONFIG);
    }

    @Test
    public void testRptRequestWithResourceNamePairwise() {
        testRptRequestWithResourceName(EntitlementAPITest.PAIRWISE_AUTHZ_CLIENT_CONFIG);
    }

    @Test
    public void testInvalidRequestWithClaimsFromConfidentialClient() throws IOException {
        AuthorizationRequest request = new AuthorizationRequest();
        request.addPermission("Resource 13");
        HashMap<Object, Object> obj = new HashMap<>();
        obj.put("claim-a", "claim-a");
        request.setClaimToken(Base64Url.encode(JsonSerialization.writeValueAsBytes(obj)));
        assertResponse(new Metadata(), () -> getAuthzClient(AUTHZ_CLIENT_CONFIG).authorization("marta", "password").authorize(request));
    }

    @Test
    public void testInvalidRequestWithClaimsFromPublicClient() throws IOException {
        oauth.realm("authz-test");
        oauth.clientId(EntitlementAPITest.PUBLIC_TEST_CLIENT);
        oauth.doLogin("marta", "password");
        // Token request
        String code = oauth.getCurrentQuery().get(CODE);
        OAuthClient.AccessTokenResponse response = oauth.doAccessTokenRequest(code, null);
        AuthorizationRequest request = new AuthorizationRequest();
        request.addPermission("Resource 13");
        HashMap<Object, Object> obj = new HashMap<>();
        obj.put("claim-a", "claim-a");
        request.setClaimToken(Base64Url.encode(JsonSerialization.writeValueAsBytes(obj)));
        try {
            getAuthzClient(EntitlementAPITest.AUTHZ_CLIENT_CONFIG).authorization(response.getAccessToken()).authorize(request);
        } catch (AuthorizationDeniedException expected) {
            Assert.assertEquals(403, HttpResponseException.class.cast(expected.getCause()).getStatusCode());
            Assert.assertTrue(HttpResponseException.class.cast(expected.getCause()).toString().contains("Public clients are not allowed to send claims"));
        }
    }

    @Test
    public void testRequestWithoutClaimsFromPublicClient() {
        oauth.realm("authz-test");
        oauth.clientId(EntitlementAPITest.PUBLIC_TEST_CLIENT);
        oauth.doLogin("marta", "password");
        // Token request
        String code = oauth.getCurrentQuery().get(CODE);
        OAuthClient.AccessTokenResponse response = oauth.doAccessTokenRequest(code, null);
        AuthorizationRequest request = new AuthorizationRequest();
        request.addPermission("Resource 13");
        assertResponse(new Metadata(), () -> getAuthzClient(AUTHZ_CLIENT_CONFIG).authorization(response.getAccessToken()).authorize(request));
    }

    @Test
    public void testPermissionLimit() {
        testPermissionLimit(EntitlementAPITest.AUTHZ_CLIENT_CONFIG);
    }

    @Test
    public void testPermissionLimitPairwise() {
        testPermissionLimit(EntitlementAPITest.PAIRWISE_AUTHZ_CLIENT_CONFIG);
    }

    @Test
    public void testResourceServerAsAudience() throws Exception {
        testResourceServerAsAudience(EntitlementAPITest.TEST_CLIENT, EntitlementAPITest.RESOURCE_SERVER_TEST, EntitlementAPITest.AUTHZ_CLIENT_CONFIG);
    }

    @Test
    public void testResourceServerAsAudienceWithPairwiseClient() throws Exception {
        testResourceServerAsAudience(EntitlementAPITest.PAIRWISE_TEST_CLIENT, EntitlementAPITest.RESOURCE_SERVER_TEST, EntitlementAPITest.AUTHZ_CLIENT_CONFIG);
    }

    @Test
    public void testPairwiseResourceServerAsAudience() throws Exception {
        testResourceServerAsAudience(EntitlementAPITest.TEST_CLIENT, EntitlementAPITest.PAIRWISE_RESOURCE_SERVER_TEST, EntitlementAPITest.PAIRWISE_AUTHZ_CLIENT_CONFIG);
    }

    @Test
    public void testPairwiseResourceServerAsAudienceWithPairwiseClient() throws Exception {
        testResourceServerAsAudience(EntitlementAPITest.PAIRWISE_TEST_CLIENT, EntitlementAPITest.PAIRWISE_RESOURCE_SERVER_TEST, EntitlementAPITest.PAIRWISE_AUTHZ_CLIENT_CONFIG);
    }

    @Test
    public void testObtainAllEntitlements() throws Exception {
        ClientResource client = getClient(getRealm(), EntitlementAPITest.RESOURCE_SERVER_TEST);
        AuthorizationResource authorization = client.authorization();
        JSPolicyRepresentation policy = new JSPolicyRepresentation();
        policy.setName("Only Owner Policy");
        policy.setCode("if ($evaluation.getContext().getIdentity().getId() == $evaluation.getPermission().getResource().getOwner()) {$evaluation.grant();}");
        authorization.policies().js().create(policy).close();
        ResourceRepresentation resource = new ResourceRepresentation();
        resource.setName("Marta Resource");
        resource.setOwner("marta");
        resource.setOwnerManagedAccess(true);
        try (Response response = authorization.resources().create(resource)) {
            resource = response.readEntity(ResourceRepresentation.class);
        }
        ResourcePermissionRepresentation permission = new ResourcePermissionRepresentation();
        permission.setName("Marta Resource Permission");
        permission.addResource(resource.getId());
        permission.addPolicy(policy.getName());
        authorization.permissions().resource().create(permission).close();
        Assert.assertTrue(hasPermission("marta", "password", resource.getId()));
        Assert.assertFalse(hasPermission("kolo", "password", resource.getId()));
        String accessToken = new OAuthClient().realm("authz-test").clientId(EntitlementAPITest.RESOURCE_SERVER_TEST).doGrantAccessTokenRequest("secret", "kolo", "password").getAccessToken();
        AuthzClient authzClient = getAuthzClient(EntitlementAPITest.AUTHZ_CLIENT_CONFIG);
        PermissionResponse permissionResponse = authzClient.protection().permission().create(new org.keycloak.representations.idm.authorization.PermissionRequest(resource.getId()));
        AuthorizationRequest request = new AuthorizationRequest();
        request.setTicket(permissionResponse.getTicket());
        try {
            authzClient.authorization(accessToken).authorize(request);
        } catch (Exception ignore) {
        }
        List<PermissionTicketRepresentation> tickets = authzClient.protection().permission().findByResource(resource.getId());
        Assert.assertEquals(1, tickets.size());
        PermissionTicketRepresentation ticket = tickets.get(0);
        ticket.setGranted(true);
        authzClient.protection().permission().update(ticket);
        Assert.assertTrue(hasPermission("kolo", "password", resource.getId()));
        resource.addScope("Scope A");
        authorization.resources().resource(resource.getId()).update(resource);
        // the addition of a new scope still grants access to resource and any scope
        Assert.assertFalse(hasPermission("kolo", "password", resource.getId()));
        accessToken = new OAuthClient().realm("authz-test").clientId(EntitlementAPITest.RESOURCE_SERVER_TEST).doGrantAccessTokenRequest("secret", "kolo", "password").getAccessToken();
        permissionResponse = authzClient.protection().permission().create(new org.keycloak.representations.idm.authorization.PermissionRequest(resource.getId(), "Scope A"));
        request = new AuthorizationRequest();
        request.setTicket(permissionResponse.getTicket());
        try {
            authzClient.authorization(accessToken).authorize(request);
        } catch (Exception ignore) {
        }
        tickets = authzClient.protection().permission().find(resource.getId(), "Scope A", null, null, false, false, null, null);
        Assert.assertEquals(1, tickets.size());
        ticket = tickets.get(0);
        ticket.setGranted(true);
        authzClient.protection().permission().update(ticket);
        Assert.assertTrue(hasPermission("kolo", "password", resource.getId(), "Scope A"));
        resource.addScope("Scope B");
        authorization.resources().resource(resource.getId()).update(resource);
        Assert.assertTrue(hasPermission("kolo", "password", resource.getId()));
        Assert.assertTrue(hasPermission("kolo", "password", resource.getId(), "Scope A"));
        Assert.assertFalse(hasPermission("kolo", "password", resource.getId(), "Scope B"));
        resource.setScopes(new HashSet());
        authorization.resources().resource(resource.getId()).update(resource);
        Assert.assertTrue(hasPermission("kolo", "password", resource.getId()));
        Assert.assertFalse(hasPermission("kolo", "password", resource.getId(), "Scope A"));
        Assert.assertFalse(hasPermission("kolo", "password", resource.getId(), "Scope B"));
    }

    @Test
    public void testObtainAllEntitlementsWithLimit() throws Exception {
        org.keycloak.authorization.client.resource.AuthorizationResource authorizationResource = getAuthzClient(EntitlementAPITest.AUTHZ_CLIENT_CONFIG).authorization("marta", "password");
        AuthorizationResponse response = authorizationResource.authorize();
        AccessToken accessToken = toAccessToken(response.getToken());
        Authorization authorization = accessToken.getAuthorization();
        Assert.assertTrue(((authorization.getPermissions().size()) >= 20));
        AuthorizationRequest request = new AuthorizationRequest();
        Metadata metadata = new Metadata();
        metadata.setLimit(10);
        request.setMetadata(metadata);
        response = authorizationResource.authorize(request);
        accessToken = toAccessToken(response.getToken());
        authorization = accessToken.getAuthorization();
        Assert.assertEquals(10, authorization.getPermissions().size());
        metadata.setLimit(1);
        request.setMetadata(metadata);
        response = authorizationResource.authorize(request);
        accessToken = toAccessToken(response.getToken());
        authorization = accessToken.getAuthorization();
        Assert.assertEquals(1, authorization.getPermissions().size());
    }

    @Test
    public void testObtainAllEntitlementsInvalidResource() throws Exception {
        ClientResource client = getClient(getRealm(), EntitlementAPITest.RESOURCE_SERVER_TEST);
        AuthorizationResource authorization = client.authorization();
        JSPolicyRepresentation policy = new JSPolicyRepresentation();
        policy.setName(KeycloakModelUtils.generateId());
        policy.setCode("$evaluation.grant();");
        authorization.policies().js().create(policy).close();
        ResourceRepresentation resource = new ResourceRepresentation();
        resource.setName("Sensors");
        resource.addScope("sensors:view", "sensors:update", "sensors:delete");
        authorization.resources().create(resource).close();
        ScopePermissionRepresentation permission = new ScopePermissionRepresentation();
        permission.setName("View Sensor");
        permission.addScope("sensors:view");
        permission.addPolicy(policy.getName());
        authorization.permissions().scope().create(permission).close();
        String accessToken = new OAuthClient().realm("authz-test").clientId(EntitlementAPITest.RESOURCE_SERVER_TEST).doGrantAccessTokenRequest("secret", "kolo", "password").getAccessToken();
        AuthzClient authzClient = getAuthzClient(EntitlementAPITest.AUTHZ_CLIENT_CONFIG);
        AuthorizationRequest request = new AuthorizationRequest();
        request.addPermission("Sensortest", "sensors:view");
        try {
            authzClient.authorization(accessToken).authorize(request);
            Assert.fail("resource is invalid");
        } catch (RuntimeException expected) {
            Assert.assertEquals(400, HttpResponseException.class.cast(expected.getCause()).getStatusCode());
            Assert.assertTrue(HttpResponseException.class.cast(expected.getCause()).toString().contains("invalid_resource"));
        }
    }

    @Test
    public void testObtainAllEntitlementsInvalidScope() throws Exception {
        ClientResource client = getClient(getRealm(), EntitlementAPITest.RESOURCE_SERVER_TEST);
        AuthorizationResource authorization = client.authorization();
        JSPolicyRepresentation policy = new JSPolicyRepresentation();
        policy.setName(KeycloakModelUtils.generateId());
        policy.setCode("$evaluation.grant();");
        authorization.policies().js().create(policy).close();
        ResourceRepresentation resource = new ResourceRepresentation();
        resource.setName(KeycloakModelUtils.generateId());
        resource.addScope("sensors:view", "sensors:update", "sensors:delete");
        try (Response response = authorization.resources().create(resource)) {
            resource = response.readEntity(ResourceRepresentation.class);
        }
        ScopePermissionRepresentation permission = new ScopePermissionRepresentation();
        permission.setName(KeycloakModelUtils.generateId());
        permission.addScope("sensors:view");
        permission.addPolicy(policy.getName());
        authorization.permissions().scope().create(permission).close();
        String accessToken = new OAuthClient().realm("authz-test").clientId(EntitlementAPITest.RESOURCE_SERVER_TEST).doGrantAccessTokenRequest("secret", "kolo", "password").getAccessToken();
        AuthzClient authzClient = getAuthzClient(EntitlementAPITest.AUTHZ_CLIENT_CONFIG);
        AuthorizationRequest request = new AuthorizationRequest();
        request.addPermission(resource.getId(), "sensors:view_invalid");
        try {
            authzClient.authorization(accessToken).authorize(request);
            Assert.fail("scope is invalid");
        } catch (RuntimeException expected) {
            Assert.assertEquals(400, HttpResponseException.class.cast(expected.getCause()).getStatusCode());
            Assert.assertTrue(HttpResponseException.class.cast(expected.getCause()).toString().contains("invalid_scope"));
        }
        request = new AuthorizationRequest();
        request.addPermission(null, "sensors:view_invalid");
        try {
            authzClient.authorization(accessToken).authorize(request);
            Assert.fail("scope is invalid");
        } catch (RuntimeException expected) {
            Assert.assertEquals(400, HttpResponseException.class.cast(expected.getCause()).getStatusCode());
            Assert.assertTrue(HttpResponseException.class.cast(expected.getCause()).toString().contains("invalid_scope"));
        }
    }

    @Test
    public void testObtainAllEntitlementsForScope() throws Exception {
        ClientResource client = getClient(getRealm(), EntitlementAPITest.RESOURCE_SERVER_TEST);
        AuthorizationResource authorization = client.authorization();
        JSPolicyRepresentation policy = new JSPolicyRepresentation();
        policy.setName(KeycloakModelUtils.generateId());
        policy.setCode("$evaluation.grant();");
        authorization.policies().js().create(policy).close();
        Set<String> resourceIds = new HashSet<>();
        ResourceRepresentation resource = new ResourceRepresentation();
        resource.setName(KeycloakModelUtils.generateId());
        resource.addScope("sensors:view", "sensors:update", "sensors:delete");
        try (Response response = authorization.resources().create(resource)) {
            resourceIds.add(response.readEntity(ResourceRepresentation.class).getId());
        }
        resource = new ResourceRepresentation();
        resource.setName(KeycloakModelUtils.generateId());
        resource.addScope("sensors:view", "sensors:update");
        try (Response response = authorization.resources().create(resource)) {
            resourceIds.add(response.readEntity(ResourceRepresentation.class).getId());
        }
        ScopePermissionRepresentation permission = new ScopePermissionRepresentation();
        permission.setName(KeycloakModelUtils.generateId());
        permission.addScope("sensors:view", "sensors:update");
        permission.addPolicy(policy.getName());
        authorization.permissions().scope().create(permission).close();
        String accessToken = new OAuthClient().realm("authz-test").clientId(EntitlementAPITest.RESOURCE_SERVER_TEST).doGrantAccessTokenRequest("secret", "kolo", "password").getAccessToken();
        AuthzClient authzClient = getAuthzClient(EntitlementAPITest.AUTHZ_CLIENT_CONFIG);
        AuthorizationRequest request = new AuthorizationRequest();
        request.addPermission(null, "sensors:view");
        AuthorizationResponse response = authzClient.authorization(accessToken).authorize(request);
        Assert.assertNotNull(response.getToken());
        Collection<Permission> permissions = toAccessToken(response.getToken()).getAuthorization().getPermissions();
        Assert.assertEquals(2, permissions.size());
        for (Permission grantedPermission : permissions) {
            Assert.assertTrue(resourceIds.containsAll(Arrays.asList(grantedPermission.getResourceId())));
            Assert.assertEquals(1, grantedPermission.getScopes().size());
            Assert.assertTrue(grantedPermission.getScopes().containsAll(Arrays.asList("sensors:view")));
        }
        request.addPermission(null, "sensors:view", "sensors:update");
        response = authzClient.authorization(accessToken).authorize(request);
        Assert.assertNotNull(response.getToken());
        permissions = toAccessToken(response.getToken()).getAuthorization().getPermissions();
        Assert.assertEquals(2, permissions.size());
        for (Permission grantedPermission : permissions) {
            Assert.assertTrue(resourceIds.containsAll(Arrays.asList(grantedPermission.getResourceId())));
            Assert.assertEquals(2, grantedPermission.getScopes().size());
            Assert.assertTrue(grantedPermission.getScopes().containsAll(Arrays.asList("sensors:view", "sensors:update")));
        }
        request.addPermission(null, "sensors:view", "sensors:update", "sensors:delete");
        response = authzClient.authorization(accessToken).authorize(request);
        Assert.assertNotNull(response.getToken());
        permissions = toAccessToken(response.getToken()).getAuthorization().getPermissions();
        Assert.assertEquals(2, permissions.size());
        for (Permission grantedPermission : permissions) {
            Assert.assertTrue(resourceIds.containsAll(Arrays.asList(grantedPermission.getResourceId())));
            Assert.assertEquals(2, grantedPermission.getScopes().size());
            Assert.assertTrue(grantedPermission.getScopes().containsAll(Arrays.asList("sensors:view", "sensors:update")));
        }
        request = new AuthorizationRequest();
        request.addPermission(null, "sensors:view");
        request.addPermission(null, "sensors:update");
        response = authzClient.authorization(accessToken).authorize(request);
        Assert.assertNotNull(response.getToken());
        permissions = toAccessToken(response.getToken()).getAuthorization().getPermissions();
        Assert.assertEquals(2, permissions.size());
        for (Permission grantedPermission : permissions) {
            Assert.assertTrue(resourceIds.containsAll(Arrays.asList(grantedPermission.getResourceId())));
            Assert.assertEquals(2, grantedPermission.getScopes().size());
            Assert.assertTrue(grantedPermission.getScopes().containsAll(Arrays.asList("sensors:view", "sensors:update")));
        }
    }

    @Test
    public void testObtainAllEntitlementsForScopeWithDeny() throws Exception {
        ClientResource client = getClient(getRealm(), EntitlementAPITest.RESOURCE_SERVER_TEST);
        AuthorizationResource authorization = client.authorization();
        JSPolicyRepresentation policy = new JSPolicyRepresentation();
        policy.setName(KeycloakModelUtils.generateId());
        policy.setCode("$evaluation.grant();");
        authorization.policies().js().create(policy).close();
        authorization.scopes().create(new ScopeRepresentation("sensors:view")).close();
        ScopePermissionRepresentation permission = new ScopePermissionRepresentation();
        permission.setName(KeycloakModelUtils.generateId());
        permission.addScope("sensors:view");
        permission.addPolicy(policy.getName());
        authorization.permissions().scope().create(permission).close();
        String accessToken = new OAuthClient().realm("authz-test").clientId(EntitlementAPITest.RESOURCE_SERVER_TEST).doGrantAccessTokenRequest("secret", "kolo", "password").getAccessToken();
        AuthzClient authzClient = getAuthzClient(EntitlementAPITest.AUTHZ_CLIENT_CONFIG);
        AuthorizationRequest request = new AuthorizationRequest();
        request.addPermission(null, "sensors:view");
        AuthorizationResponse response = authzClient.authorization(accessToken).authorize(request);
        Assert.assertNotNull(response.getToken());
        Collection<Permission> permissions = toAccessToken(response.getToken()).getAuthorization().getPermissions();
        Assert.assertEquals(1, permissions.size());
        for (Permission grantedPermission : permissions) {
            Assert.assertNull(grantedPermission.getResourceId());
            Assert.assertEquals(1, grantedPermission.getScopes().size());
            Assert.assertTrue(grantedPermission.getScopes().containsAll(Arrays.asList("sensors:view")));
        }
    }

    @Test
    public void testObtainAllEntitlementsForResource() throws Exception {
        ClientResource client = getClient(getRealm(), EntitlementAPITest.RESOURCE_SERVER_TEST);
        AuthorizationResource authorization = client.authorization();
        JSPolicyRepresentation policy = new JSPolicyRepresentation();
        policy.setName(KeycloakModelUtils.generateId());
        policy.setCode("$evaluation.grant();");
        authorization.policies().js().create(policy).close();
        ResourceRepresentation resource = new ResourceRepresentation();
        resource.setName(KeycloakModelUtils.generateId());
        resource.addScope("scope:view", "scope:update", "scope:delete");
        try (Response response = authorization.resources().create(resource)) {
            resource = response.readEntity(ResourceRepresentation.class);
        }
        ResourcePermissionRepresentation permission = new ResourcePermissionRepresentation();
        permission.setName(KeycloakModelUtils.generateId());
        permission.addResource(resource.getId());
        permission.addPolicy(policy.getName());
        authorization.permissions().resource().create(permission).close();
        String accessToken = new OAuthClient().realm("authz-test").clientId(EntitlementAPITest.RESOURCE_SERVER_TEST).doGrantAccessTokenRequest("secret", "kolo", "password").getAccessToken();
        AuthzClient authzClient = getAuthzClient(EntitlementAPITest.AUTHZ_CLIENT_CONFIG);
        AuthorizationRequest request = new AuthorizationRequest();
        request.addPermission(null, "scope:view", "scope:update", "scope:delete");
        AuthorizationResponse response = authzClient.authorization(accessToken).authorize(request);
        Assert.assertNotNull(response.getToken());
        Collection<Permission> permissions = toAccessToken(response.getToken()).getAuthorization().getPermissions();
        Assert.assertEquals(1, permissions.size());
        for (Permission grantedPermission : permissions) {
            Assert.assertEquals(resource.getId(), grantedPermission.getResourceId());
            Assert.assertEquals(3, grantedPermission.getScopes().size());
            Assert.assertTrue(grantedPermission.getScopes().containsAll(Arrays.asList("scope:view")));
        }
        resource.setScopes(new HashSet());
        resource.addScope("scope:view", "scope:update");
        authorization.resources().resource(resource.getId()).update(resource);
        request = new AuthorizationRequest();
        request.addPermission(null, "scope:view", "scope:update", "scope:delete");
        response = authzClient.authorization(accessToken).authorize(request);
        Assert.assertNotNull(response.getToken());
        permissions = toAccessToken(response.getToken()).getAuthorization().getPermissions();
        Assert.assertEquals(1, permissions.size());
        for (Permission grantedPermission : permissions) {
            Assert.assertEquals(resource.getId(), grantedPermission.getResourceId());
            Assert.assertEquals(2, grantedPermission.getScopes().size());
            Assert.assertTrue(grantedPermission.getScopes().containsAll(Arrays.asList("scope:view", "scope:update")));
        }
        request = new AuthorizationRequest();
        request.addPermission(resource.getId(), "scope:view", "scope:update", "scope:delete");
        for (Permission grantedPermission : permissions) {
            Assert.assertEquals(resource.getId(), grantedPermission.getResourceId());
            Assert.assertEquals(2, grantedPermission.getScopes().size());
            Assert.assertTrue(grantedPermission.getScopes().containsAll(Arrays.asList("scope:view", "scope:update")));
        }
    }

    @Test
    public void testOverridePermission() throws Exception {
        ClientResource client = getClient(getRealm(), EntitlementAPITest.RESOURCE_SERVER_TEST);
        AuthorizationResource authorization = client.authorization();
        JSPolicyRepresentation onlyOwnerPolicy = createOnlyOwnerPolicy();
        authorization.policies().js().create(onlyOwnerPolicy).close();
        ResourceRepresentation typedResource = new ResourceRepresentation();
        typedResource.setType("resource");
        typedResource.setName(KeycloakModelUtils.generateId());
        typedResource.addScope("read", "update");
        try (Response response = authorization.resources().create(typedResource)) {
            typedResource = response.readEntity(ResourceRepresentation.class);
        }
        ResourcePermissionRepresentation typedResourcePermission = new ResourcePermissionRepresentation();
        typedResourcePermission.setName(KeycloakModelUtils.generateId());
        typedResourcePermission.setResourceType("resource");
        typedResourcePermission.addPolicy(onlyOwnerPolicy.getName());
        try (Response response = authorization.permissions().resource().create(typedResourcePermission)) {
            typedResourcePermission = response.readEntity(ResourcePermissionRepresentation.class);
        }
        ResourceRepresentation martaResource = new ResourceRepresentation();
        martaResource.setType("resource");
        martaResource.setName(KeycloakModelUtils.generateId());
        martaResource.addScope("read", "update");
        martaResource.setOwner("marta");
        try (Response response = authorization.resources().create(martaResource)) {
            martaResource = response.readEntity(ResourceRepresentation.class);
        }
        String accessToken = new OAuthClient().realm("authz-test").clientId(EntitlementAPITest.RESOURCE_SERVER_TEST).doGrantAccessTokenRequest("secret", "marta", "password").getAccessToken();
        AuthzClient authzClient = getAuthzClient(EntitlementAPITest.AUTHZ_CLIENT_CONFIG);
        AuthorizationRequest request = new AuthorizationRequest();
        request.addPermission(martaResource.getName());
        // marta can access her resource
        AuthorizationResponse response = authzClient.authorization(accessToken).authorize(request);
        Assert.assertNotNull(response.getToken());
        Collection<Permission> permissions = toAccessToken(response.getToken()).getAuthorization().getPermissions();
        Assert.assertEquals(1, permissions.size());
        for (Permission grantedPermission : permissions) {
            Assert.assertEquals(martaResource.getName(), grantedPermission.getResourceName());
            Set<String> scopes = grantedPermission.getScopes();
            Assert.assertEquals(2, scopes.size());
            Assert.assertThat(scopes, Matchers.containsInAnyOrder("read", "update"));
        }
        accessToken = new OAuthClient().realm("authz-test").clientId(EntitlementAPITest.RESOURCE_SERVER_TEST).doGrantAccessTokenRequest("secret", "kolo", "password").getAccessToken();
        authzClient = getAuthzClient(EntitlementAPITest.AUTHZ_CLIENT_CONFIG);
        request = new AuthorizationRequest();
        request.addPermission(martaResource.getId());
        try {
            authzClient.authorization(accessToken).authorize(request);
            Assert.fail("kolo can not access marta resource");
        } catch (RuntimeException expected) {
            Assert.assertEquals(403, HttpResponseException.class.cast(expected.getCause()).getStatusCode());
            Assert.assertTrue(HttpResponseException.class.cast(expected.getCause()).toString().contains("access_denied"));
        }
        UserPolicyRepresentation onlyKoloPolicy = new UserPolicyRepresentation();
        onlyKoloPolicy.setName(KeycloakModelUtils.generateId());
        onlyKoloPolicy.addUser("kolo");
        authorization.policies().user().create(onlyKoloPolicy).close();
        ResourcePermissionRepresentation martaResourcePermission = new ResourcePermissionRepresentation();
        martaResourcePermission.setName(KeycloakModelUtils.generateId());
        martaResourcePermission.addResource(martaResource.getId());
        martaResourcePermission.addPolicy(onlyKoloPolicy.getName());
        try (Response response1 = authorization.permissions().resource().create(martaResourcePermission)) {
            martaResourcePermission = response1.readEntity(ResourcePermissionRepresentation.class);
        }
        response = authzClient.authorization(accessToken).authorize(request);
        Assert.assertNotNull(response.getToken());
        permissions = toAccessToken(response.getToken()).getAuthorization().getPermissions();
        Assert.assertEquals(1, permissions.size());
        for (Permission grantedPermission : permissions) {
            Assert.assertEquals(martaResource.getName(), grantedPermission.getResourceName());
            Set<String> scopes = grantedPermission.getScopes();
            Assert.assertEquals(2, scopes.size());
            Assert.assertThat(scopes, Matchers.containsInAnyOrder("read", "update"));
        }
        typedResourcePermission.setResourceType(null);
        typedResourcePermission.addResource(typedResource.getName());
        authorization.permissions().resource().findById(typedResourcePermission.getId()).update(typedResourcePermission);
        // now kolo can access marta's resources, last permission is overriding policies from typed resource
        response = authzClient.authorization(accessToken).authorize(request);
        Assert.assertNotNull(response.getToken());
        permissions = toAccessToken(response.getToken()).getAuthorization().getPermissions();
        Assert.assertEquals(1, permissions.size());
        for (Permission grantedPermission : permissions) {
            Assert.assertEquals(martaResource.getName(), grantedPermission.getResourceName());
            Set<String> scopes = grantedPermission.getScopes();
            Assert.assertEquals(2, scopes.size());
            Assert.assertThat(scopes, Matchers.containsInAnyOrder("read", "update"));
        }
        ScopePermissionRepresentation martaResourceUpdatePermission = new ScopePermissionRepresentation();
        martaResourceUpdatePermission.setName(KeycloakModelUtils.generateId());
        martaResourceUpdatePermission.addResource(martaResource.getId());
        martaResourceUpdatePermission.addScope("update");
        martaResourceUpdatePermission.addPolicy(onlyOwnerPolicy.getName());
        try (Response response1 = authorization.permissions().scope().create(martaResourceUpdatePermission)) {
            martaResourceUpdatePermission = response1.readEntity(ScopePermissionRepresentation.class);
        }
        // now kolo can only read, but not update
        response = authzClient.authorization(accessToken).authorize(request);
        Assert.assertNotNull(response.getToken());
        permissions = toAccessToken(response.getToken()).getAuthorization().getPermissions();
        Assert.assertEquals(1, permissions.size());
        for (Permission grantedPermission : permissions) {
            Assert.assertEquals(martaResource.getName(), grantedPermission.getResourceName());
            Set<String> scopes = grantedPermission.getScopes();
            Assert.assertEquals(1, scopes.size());
            Assert.assertThat(scopes, Matchers.containsInAnyOrder("read"));
        }
        authorization.permissions().resource().findById(martaResourcePermission.getId()).remove();
        try {
            // after removing permission to marta resource, kolo can not access any scope in the resource
            authzClient.authorization(accessToken).authorize(request);
            Assert.fail("kolo can not access marta resource");
        } catch (RuntimeException expected) {
            Assert.assertEquals(403, HttpResponseException.class.cast(expected.getCause()).getStatusCode());
            Assert.assertTrue(HttpResponseException.class.cast(expected.getCause()).toString().contains("access_denied"));
        }
        martaResourceUpdatePermission.addPolicy(onlyKoloPolicy.getName());
        martaResourceUpdatePermission.setDecisionStrategy(AFFIRMATIVE);
        authorization.permissions().scope().findById(martaResourceUpdatePermission.getId()).update(martaResourceUpdatePermission);
        // now kolo can access because update permission changed to allow him to access the resource using an affirmative strategy
        response = authzClient.authorization(accessToken).authorize(request);
        Assert.assertNotNull(response.getToken());
        permissions = toAccessToken(response.getToken()).getAuthorization().getPermissions();
        Assert.assertEquals(1, permissions.size());
        for (Permission grantedPermission : permissions) {
            Assert.assertEquals(martaResource.getName(), grantedPermission.getResourceName());
            Set<String> scopes = grantedPermission.getScopes();
            Assert.assertEquals(1, scopes.size());
            Assert.assertThat(scopes, Matchers.containsInAnyOrder("update"));
        }
        accessToken = new OAuthClient().realm("authz-test").clientId(EntitlementAPITest.RESOURCE_SERVER_TEST).doGrantAccessTokenRequest("secret", "marta", "password").getAccessToken();
        // marta can still access her resource
        response = authzClient.authorization(accessToken).authorize(request);
        Assert.assertNotNull(response.getToken());
        permissions = toAccessToken(response.getToken()).getAuthorization().getPermissions();
        Assert.assertEquals(1, permissions.size());
        for (Permission grantedPermission : permissions) {
            Assert.assertEquals(martaResource.getName(), grantedPermission.getResourceName());
            Set<String> scopes = grantedPermission.getScopes();
            Assert.assertEquals(2, scopes.size());
            Assert.assertThat(scopes, Matchers.containsInAnyOrder("update", "read"));
        }
        authorization.permissions().scope().findById(martaResourceUpdatePermission.getId()).remove();
        accessToken = new OAuthClient().realm("authz-test").clientId(EntitlementAPITest.RESOURCE_SERVER_TEST).doGrantAccessTokenRequest("secret", "kolo", "password").getAccessToken();
        try {
            // back to original setup, permissions not granted by the type resource
            authzClient.authorization(accessToken).authorize(request);
            Assert.fail("kolo can not access marta resource");
        } catch (RuntimeException expected) {
            Assert.assertEquals(403, HttpResponseException.class.cast(expected.getCause()).getStatusCode());
            Assert.assertTrue(HttpResponseException.class.cast(expected.getCause()).toString().contains("access_denied"));
        }
    }

    @Test
    public void testPermissionsWithResourceAttributes() throws Exception {
        ClientResource client = getClient(getRealm(), EntitlementAPITest.RESOURCE_SERVER_TEST);
        AuthorizationResource authorization = client.authorization();
        JSPolicyRepresentation onlyPublicResourcesPolicy = new JSPolicyRepresentation();
        onlyPublicResourcesPolicy.setName(KeycloakModelUtils.generateId());
        onlyPublicResourcesPolicy.setCode(("var createPermission = $evaluation.getPermission();\n" + ((((((((((("var resource = createPermission.getResource();\n" + "\n") + "if (resource) {\n") + "    var attributes = resource.getAttributes();\n") + "    var visibility = attributes.get(\'visibility\');\n") + "    \n") + "    if (visibility && \"private\".equals(visibility.get(0))) {\n") + "        $evaluation.deny();\n") + "      } else {\n") + "        $evaluation.grant();\n") + "    }\n") + "}")));
        authorization.policies().js().create(onlyPublicResourcesPolicy).close();
        JSPolicyRepresentation onlyOwnerPolicy = createOnlyOwnerPolicy();
        authorization.policies().js().create(onlyOwnerPolicy).close();
        ResourceRepresentation typedResource = new ResourceRepresentation();
        typedResource.setType("resource");
        typedResource.setName(KeycloakModelUtils.generateId());
        try (Response response = authorization.resources().create(typedResource)) {
            typedResource = response.readEntity(ResourceRepresentation.class);
        }
        ResourceRepresentation userResource = new ResourceRepresentation();
        userResource.setName(KeycloakModelUtils.generateId());
        userResource.setType("resource");
        userResource.setOwner("marta");
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("visibility", Arrays.asList("private"));
        userResource.setAttributes(attributes);
        try (Response response = authorization.resources().create(userResource)) {
            userResource = response.readEntity(ResourceRepresentation.class);
        }
        ResourcePermissionRepresentation typedResourcePermission = new ResourcePermissionRepresentation();
        typedResourcePermission.setName(KeycloakModelUtils.generateId());
        typedResourcePermission.setResourceType("resource");
        typedResourcePermission.addPolicy(onlyPublicResourcesPolicy.getName());
        try (Response response = authorization.permissions().resource().create(typedResourcePermission)) {
            typedResourcePermission = response.readEntity(ResourcePermissionRepresentation.class);
        }
        // marta can access any public resource
        AuthzClient authzClient = getAuthzClient(EntitlementAPITest.AUTHZ_CLIENT_CONFIG);
        AuthorizationRequest request = new AuthorizationRequest();
        request.addPermission(typedResource.getId());
        request.addPermission(userResource.getId());
        AuthorizationResponse response = authzClient.authorization("marta", "password").authorize(request);
        Assert.assertNotNull(response.getToken());
        Collection<Permission> permissions = toAccessToken(response.getToken()).getAuthorization().getPermissions();
        Assert.assertEquals(1, permissions.size());
        for (Permission grantedPermission : permissions) {
            Assert.assertEquals(typedResource.getName(), grantedPermission.getResourceName());
        }
        typedResourcePermission.addPolicy(onlyOwnerPolicy.getName());
        typedResourcePermission.setDecisionStrategy(AFFIRMATIVE);
        authorization.permissions().resource().findById(typedResourcePermission.getId()).update(typedResourcePermission);
        response = authzClient.authorization("marta", "password").authorize(request);
        Assert.assertNotNull(response.getToken());
        permissions = toAccessToken(response.getToken()).getAuthorization().getPermissions();
        Assert.assertEquals(2, permissions.size());
        for (Permission grantedPermission : permissions) {
            Assert.assertThat(Arrays.asList(typedResource.getName(), userResource.getName()), Matchers.hasItem(grantedPermission.getResourceName()));
        }
        typedResource.setAttributes(attributes);
        authorization.resources().resource(typedResource.getId()).update(typedResource);
        response = authzClient.authorization("marta", "password").authorize(request);
        Assert.assertNotNull(response.getToken());
        permissions = toAccessToken(response.getToken()).getAuthorization().getPermissions();
        Assert.assertEquals(1, permissions.size());
        for (Permission grantedPermission : permissions) {
            Assert.assertThat(userResource.getName(), Matchers.equalTo(grantedPermission.getResourceName()));
        }
        userResource.addScope("create", "read");
        authorization.resources().resource(userResource.getId()).update(userResource);
        typedResource.addScope("create", "read");
        authorization.resources().resource(typedResource.getId()).update(typedResource);
        ScopePermissionRepresentation createPermission = new ScopePermissionRepresentation();
        createPermission.setName(KeycloakModelUtils.generateId());
        createPermission.addScope("create");
        createPermission.addPolicy(onlyPublicResourcesPolicy.getName());
        authorization.permissions().scope().create(createPermission).close();
        response = authzClient.authorization("marta", "password").authorize(request);
        Assert.assertNotNull(response.getToken());
        permissions = toAccessToken(response.getToken()).getAuthorization().getPermissions();
        Assert.assertEquals(1, permissions.size());
        for (Permission grantedPermission : permissions) {
            Assert.assertThat(userResource.getName(), Matchers.equalTo(grantedPermission.getResourceName()));
            Assert.assertThat(grantedPermission.getScopes(), Matchers.not(Matchers.hasItem("create")));
        }
        typedResource.setAttributes(new HashMap());
        authorization.resources().resource(typedResource.getId()).update(typedResource);
        response = authzClient.authorization("marta", "password").authorize();
        Assert.assertNotNull(response.getToken());
        permissions = toAccessToken(response.getToken()).getAuthorization().getPermissions();
        for (Permission grantedPermission : permissions) {
            if (grantedPermission.getResourceName().equals(userResource.getName())) {
                Assert.assertThat(grantedPermission.getScopes(), Matchers.not(Matchers.hasItem("create")));
            } else
                if (grantedPermission.getResourceName().equals(typedResource.getName())) {
                    Assert.assertThat(grantedPermission.getScopes(), Matchers.containsInAnyOrder("create", "read"));
                }

        }
        request = new AuthorizationRequest();
        request.addPermission(typedResource.getId());
        request.addPermission(userResource.getId());
        response = authzClient.authorization("marta", "password").authorize(request);
        Assert.assertNotNull(response.getToken());
        permissions = toAccessToken(response.getToken()).getAuthorization().getPermissions();
        for (Permission grantedPermission : permissions) {
            if (grantedPermission.getResourceName().equals(userResource.getName())) {
                Assert.assertThat(grantedPermission.getScopes(), Matchers.not(Matchers.hasItem("create")));
            } else
                if (grantedPermission.getResourceName().equals(typedResource.getName())) {
                    Assert.assertThat(grantedPermission.getScopes(), Matchers.containsInAnyOrder("create", "read"));
                }

        }
        request = new AuthorizationRequest();
        request.addPermission(userResource.getId());
        request.addPermission(typedResource.getId());
        response = authzClient.authorization("marta", "password").authorize(request);
        Assert.assertNotNull(response.getToken());
        permissions = toAccessToken(response.getToken()).getAuthorization().getPermissions();
        for (Permission grantedPermission : permissions) {
            if (grantedPermission.getResourceName().equals(userResource.getName())) {
                Assert.assertThat(grantedPermission.getScopes(), Matchers.not(Matchers.hasItem("create")));
            } else
                if (grantedPermission.getResourceName().equals(typedResource.getName())) {
                    Assert.assertThat(grantedPermission.getScopes(), Matchers.containsInAnyOrder("create", "read"));
                }

        }
    }

    @Test
    public void testOfflineRequestingPartyToken() throws Exception {
        ClientResource client = getClient(getRealm(), EntitlementAPITest.RESOURCE_SERVER_TEST);
        AuthorizationResource authorization = client.authorization();
        JSPolicyRepresentation policy = new JSPolicyRepresentation();
        policy.setName(KeycloakModelUtils.generateId());
        policy.setCode("$evaluation.grant();");
        authorization.policies().js().create(policy).close();
        ResourceRepresentation resource = new ResourceRepresentation();
        resource.setName("Sensors");
        resource.addScope("sensors:view", "sensors:update", "sensors:delete");
        try (Response response = authorization.resources().create(resource)) {
            resource = response.readEntity(ResourceRepresentation.class);
        }
        ScopePermissionRepresentation permission = new ScopePermissionRepresentation();
        permission.setName("View Sensor");
        permission.addScope("sensors:view");
        permission.addPolicy(policy.getName());
        authorization.permissions().scope().create(permission).close();
        String accessToken = new OAuthClient().realm("authz-test").clientId(EntitlementAPITest.RESOURCE_SERVER_TEST).scope("offline_access").doGrantAccessTokenRequest("secret", "offlineuser", "password").getAccessToken();
        AuthzClient authzClient = getAuthzClient(EntitlementAPITest.AUTHZ_CLIENT_CONFIG);
        AccessTokenResponse response = authzClient.authorization(accessToken).authorize();
        Assert.assertNotNull(response.getToken());
        controller.stop(suiteContext.getAuthServerInfo().getQualifier());
        controller.start(suiteContext.getAuthServerInfo().getQualifier());
        reconnectAdminClient();
        TokenIntrospectionResponse introspectionResponse = authzClient.protection().introspectRequestingPartyToken(response.getToken());
        Assert.assertTrue(introspectionResponse.getActive());
        Assert.assertFalse(introspectionResponse.getPermissions().isEmpty());
        response = authzClient.authorization(accessToken).authorize();
        Assert.assertNotNull(response.getToken());
    }

    @Test
    public void testProcessMappersForTargetAudience() throws Exception {
        ClientResource publicClient = getClient(getRealm(), EntitlementAPITest.PUBLIC_TEST_CLIENT);
        ProtocolMapperRepresentation customClaimMapper = new ProtocolMapperRepresentation();
        customClaimMapper.setName("custom_claim");
        customClaimMapper.setProtocolMapper(PROVIDER_ID);
        customClaimMapper.setProtocol(LOGIN_PROTOCOL);
        Map<String, String> config = new HashMap<>();
        config.put(TOKEN_CLAIM_NAME, "custom_claim");
        config.put(CLAIM_VALUE, EntitlementAPITest.PUBLIC_TEST_CLIENT);
        config.put(INCLUDE_IN_ACCESS_TOKEN, "true");
        customClaimMapper.setConfig(config);
        publicClient.getProtocolMappers().createMapper(customClaimMapper);
        ClientResource client = getClient(getRealm(), EntitlementAPITest.RESOURCE_SERVER_TEST);
        config.put(CLAIM_VALUE, EntitlementAPITest.RESOURCE_SERVER_TEST);
        client.getProtocolMappers().createMapper(customClaimMapper);
        AuthorizationResource authorization = client.authorization();
        JSPolicyRepresentation policy = new JSPolicyRepresentation();
        policy.setName(KeycloakModelUtils.generateId());
        policy.setCode("$evaluation.grant();");
        authorization.policies().js().create(policy).close();
        ResourceRepresentation resource = new ResourceRepresentation();
        resource.setName("Sensors");
        try (Response response = authorization.resources().create(resource)) {
            resource = response.readEntity(ResourceRepresentation.class);
        }
        ResourcePermissionRepresentation permission = new ResourcePermissionRepresentation();
        permission.setName("View Sensor");
        permission.addResource(resource.getName());
        permission.addPolicy(policy.getName());
        authorization.permissions().resource().create(permission).close();
        oauth.realm("authz-test");
        oauth.clientId(EntitlementAPITest.PUBLIC_TEST_CLIENT);
        oauth.doLogin("marta", "password");
        // Token request
        String code = oauth.getCurrentQuery().get(CODE);
        OAuthClient.AccessTokenResponse response = oauth.doAccessTokenRequest(code, null);
        AccessToken token = toAccessToken(response.getAccessToken());
        Assert.assertEquals(EntitlementAPITest.PUBLIC_TEST_CLIENT, token.getOtherClaims().get("custom_claim"));
        AuthorizationRequest request = new AuthorizationRequest();
        request.addPermission("Sensors");
        AuthorizationResponse authorizationResponse = getAuthzClient(EntitlementAPITest.AUTHZ_CLIENT_CONFIG).authorization(response.getAccessToken()).authorize(request);
        token = toAccessToken(authorizationResponse.getToken());
        Assert.assertEquals(EntitlementAPITest.RESOURCE_SERVER_TEST, token.getOtherClaims().get("custom_claim"));
        Assert.assertEquals(EntitlementAPITest.PUBLIC_TEST_CLIENT, token.getIssuedFor());
        authorizationResponse = getAuthzClient(EntitlementAPITest.AUTHZ_CLIENT_CONFIG).authorization(response.getAccessToken()).authorize(request);
        token = toAccessToken(authorizationResponse.getToken());
        Assert.assertEquals(EntitlementAPITest.RESOURCE_SERVER_TEST, token.getOtherClaims().get("custom_claim"));
        Assert.assertEquals(EntitlementAPITest.PUBLIC_TEST_CLIENT, token.getIssuedFor());
    }
}

