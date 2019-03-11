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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.admin.client.resource.AuthorizationResource;
import org.keycloak.admin.client.resource.ResourceScopesResource;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.util.HttpResponseException;
import org.keycloak.representations.idm.authorization.AuthorizationRequest;
import org.keycloak.representations.idm.authorization.PermissionRequest;
import org.keycloak.representations.idm.authorization.PermissionResponse;
import org.keycloak.representations.idm.authorization.ResourceRepresentation;
import org.keycloak.representations.idm.authorization.ScopeRepresentation;


/**
 *
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Igor</a>
 */
public class PermissionManagementTest extends AbstractResourceServerTest {
    @Test
    public void testCreatePermissionTicketWithResourceName() throws Exception {
        ResourceRepresentation resource = addResource("Resource A", "kolo", true);
        AuthzClient authzClient = getAuthzClient();
        PermissionResponse response = authzClient.protection("marta", "password").permission().create(new PermissionRequest(resource.getId()));
        AuthorizationRequest request = new AuthorizationRequest();
        request.setTicket(response.getTicket());
        request.setClaimToken(authzClient.obtainAccessToken("marta", "password").getToken());
        try {
            authzClient.authorization().authorize(request);
        } catch (Exception e) {
        }
        assertPersistence(response, resource);
    }

    @Test
    public void testCreatePermissionTicketWithResourceId() throws Exception {
        ResourceRepresentation resource = addResource("Resource A", "kolo", true);
        AuthzClient authzClient = getAuthzClient();
        PermissionResponse response = authzClient.protection("marta", "password").permission().create(new PermissionRequest(resource.getId()));
        AuthorizationRequest request = new AuthorizationRequest();
        request.setTicket(response.getTicket());
        request.setClaimToken(authzClient.obtainAccessToken("marta", "password").getToken());
        try {
            authzClient.authorization().authorize(request);
        } catch (Exception e) {
        }
        Assert.assertNotNull(response.getTicket());
        Assert.assertFalse(authzClient.protection().permission().findByResource(resource.getId()).isEmpty());
    }

    @Test
    public void testCreatePermissionTicketWithScopes() throws Exception {
        ResourceRepresentation resource = addResource("Resource A", "kolo", true, "ScopeA", "ScopeB", "ScopeC");
        AuthzClient authzClient = getAuthzClient();
        PermissionResponse response = authzClient.protection("marta", "password").permission().create(new PermissionRequest(resource.getId(), "ScopeA", "ScopeB", "ScopeC"));
        AuthorizationRequest request = new AuthorizationRequest();
        request.setTicket(response.getTicket());
        request.setClaimToken(authzClient.obtainAccessToken("marta", "password").getToken());
        try {
            authzClient.authorization().authorize(request);
        } catch (Exception e) {
        }
        assertPersistence(response, resource, "ScopeA", "ScopeB", "ScopeC");
    }

    @Test
    public void testDeleteResourceAndPermissionTicket() throws Exception {
        ResourceRepresentation resource = addResource("Resource A", "kolo", true, "ScopeA", "ScopeB", "ScopeC");
        AuthzClient authzClient = getAuthzClient();
        PermissionResponse response = authzClient.protection("marta", "password").permission().create(new PermissionRequest(resource.getId(), "ScopeA", "ScopeB", "ScopeC"));
        AuthorizationRequest request = new AuthorizationRequest();
        request.setTicket(response.getTicket());
        request.setClaimToken(authzClient.obtainAccessToken("marta", "password").getToken());
        try {
            authzClient.authorization().authorize(request);
        } catch (Exception e) {
        }
        assertPersistence(response, resource, "ScopeA", "ScopeB", "ScopeC");
        getAuthzClient().protection().resource().delete(resource.getId());
        Assert.assertTrue(getAuthzClient().protection().permission().findByResource(resource.getId()).isEmpty());
    }

    @Test
    public void testMultiplePermissionRequest() throws Exception {
        List<PermissionRequest> permissions = new ArrayList<>();
        permissions.add(new PermissionRequest(addResource("Resource A", true).getName()));
        permissions.add(new PermissionRequest(addResource("Resource B", true).getName()));
        permissions.add(new PermissionRequest(addResource("Resource C", true).getName()));
        permissions.add(new PermissionRequest(addResource("Resource D", true).getName()));
        PermissionResponse response = getAuthzClient().protection().permission().create(permissions);
        Assert.assertNotNull(response.getTicket());
    }

    @Test
    public void testDeleteScopeAndPermissionTicket() throws Exception {
        ResourceRepresentation resource = addResource("Resource A", "kolo", true, "ScopeA", "ScopeB", "ScopeC");
        PermissionRequest permissionRequest = new PermissionRequest(resource.getId());
        permissionRequest.setScopes(new HashSet(Arrays.asList("ScopeA", "ScopeB", "ScopeC")));
        AuthzClient authzClient = getAuthzClient();
        PermissionResponse response = authzClient.protection("marta", "password").permission().create(permissionRequest);
        Assert.assertNotNull(response.getTicket());
        AuthorizationRequest request = new AuthorizationRequest();
        request.setTicket(response.getTicket());
        request.setClaimToken(authzClient.obtainAccessToken("marta", "password").getToken());
        try {
            authzClient.authorization().authorize(request);
        } catch (Exception e) {
        }
        Assert.assertEquals(3, authzClient.protection().permission().findByResource(resource.getId()).size());
        AuthorizationResource authorization = getClient(getRealm()).authorization();
        ResourceScopesResource scopes = authorization.scopes();
        ScopeRepresentation scope = scopes.findByName("ScopeA");
        List permissions = authzClient.protection().permission().findByScope(scope.getId());
        Assert.assertFalse(permissions.isEmpty());
        Assert.assertEquals(1, permissions.size());
        resource.setScopes(Collections.emptySet());
        authorization.resources().resource(resource.getId()).update(resource);
        scopes.scope(scope.getId()).remove();
        Assert.assertTrue(authzClient.protection().permission().findByScope(scope.getId()).isEmpty());
        Assert.assertEquals(0, authzClient.protection().permission().findByResource(resource.getId()).size());
    }

    @Test
    public void testRemoveScopeFromResource() throws Exception {
        ResourceRepresentation resource = addResource("Resource A", "kolo", true, "ScopeA", "ScopeB");
        PermissionRequest permissionRequest = new PermissionRequest(resource.getId(), "ScopeA", "ScopeB");
        AuthzClient authzClient = getAuthzClient();
        PermissionResponse response = authzClient.protection("marta", "password").permission().create(permissionRequest);
        Assert.assertNotNull(response.getTicket());
        AuthorizationRequest request = new AuthorizationRequest();
        request.setTicket(response.getTicket());
        request.setClaimToken(authzClient.obtainAccessToken("marta", "password").getToken());
        try {
            authzClient.authorization().authorize(request);
        } catch (Exception e) {
        }
        AuthorizationResource authorization = getClient(getRealm()).authorization();
        ResourceScopesResource scopes = authorization.scopes();
        ScopeRepresentation removedScope = scopes.findByName("ScopeA");
        List permissions = authzClient.protection().permission().findByScope(removedScope.getId());
        Assert.assertFalse(permissions.isEmpty());
        resource.setScopes(new HashSet());
        resource.addScope("ScopeB");
        authorization.resources().resource(resource.getId()).update(resource);
        permissions = authzClient.protection().permission().findByScope(removedScope.getId());
        Assert.assertTrue(permissions.isEmpty());
        ScopeRepresentation scopeB = scopes.findByName("ScopeB");
        permissions = authzClient.protection().permission().findByScope(scopeB.getId());
        Assert.assertFalse(permissions.isEmpty());
    }

    @Test
    public void testCreatePermissionTicketWithResourceWithoutManagedAccess() throws Exception {
        ResourceRepresentation resource = addResource("Resource A");
        PermissionResponse response = getAuthzClient().protection().permission().create(new PermissionRequest(resource.getName()));
        Assert.assertNotNull(response.getTicket());
        Assert.assertTrue(getAuthzClient().protection().permission().findByResource(resource.getId()).isEmpty());
    }

    @Test
    public void testTicketNotCreatedWhenResourceOwner() throws Exception {
        ResourceRepresentation resource = addResource("Resource A", "marta", true);
        AuthzClient authzClient = getAuthzClient();
        PermissionResponse response = authzClient.protection("marta", "password").permission().create(new PermissionRequest(resource.getId()));
        Assert.assertNotNull(response.getTicket());
        AuthorizationRequest request = new AuthorizationRequest();
        request.setTicket(response.getTicket());
        request.setClaimToken(authzClient.obtainAccessToken("marta", "password").getToken());
        try {
            authzClient.authorization().authorize(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List permissions = authzClient.protection().permission().findByResource(resource.getId());
        Assert.assertTrue(permissions.isEmpty());
        response = authzClient.protection("kolo", "password").permission().create(new PermissionRequest(resource.getId()));
        Assert.assertNotNull(response.getTicket());
        request = new AuthorizationRequest();
        request.setTicket(response.getTicket());
        request.setClaimToken(authzClient.obtainAccessToken("kolo", "password").getToken());
        try {
            authzClient.authorization().authorize(request);
        } catch (Exception e) {
        }
        permissions = authzClient.protection().permission().findByResource(resource.getId());
        Assert.assertFalse(permissions.isEmpty());
        Assert.assertEquals(1, permissions.size());
    }

    @Test
    public void testPermissionForTypedScope() throws Exception {
        ResourceRepresentation typedResource = addResource("Typed Resource", "ScopeC");
        typedResource.setType("typed-resource");
        getClient(getRealm()).authorization().resources().resource(typedResource.getId()).update(typedResource);
        ResourceRepresentation resourceA = addResource("Resource A", "marta", true, "ScopeA", "ScopeB");
        resourceA.setType(typedResource.getType());
        getClient(getRealm()).authorization().resources().resource(resourceA.getId()).update(resourceA);
        PermissionRequest permissionRequest = new PermissionRequest(resourceA.getId());
        permissionRequest.setScopes(new HashSet(Arrays.asList("ScopeA", "ScopeC")));
        AuthzClient authzClient = getAuthzClient();
        PermissionResponse response = authzClient.protection("kolo", "password").permission().create(permissionRequest);
        AuthorizationRequest request = new AuthorizationRequest();
        request.setTicket(response.getTicket());
        request.setClaimToken(authzClient.obtainAccessToken("kolo", "password").getToken());
        try {
            authzClient.authorization().authorize(request);
        } catch (Exception e) {
        }
        assertPersistence(response, resourceA, "ScopeA", "ScopeC");
    }

    @Test
    public void testSameTicketForSamePermissionRequest() throws Exception {
        ResourceRepresentation resource = addResource("Resource A", true);
        PermissionResponse response = getAuthzClient().protection("marta", "password").permission().create(new PermissionRequest(resource.getName()));
        Assert.assertNotNull(response.getTicket());
    }

    @Test
    public void failInvalidResource() {
        try {
            getAuthzClient().protection().permission().create(new PermissionRequest("Invalid Resource"));
            Assert.fail("Should fail, resource does not exist");
        } catch (RuntimeException cause) {
            Assert.assertTrue(HttpResponseException.class.isInstance(cause.getCause()));
            Assert.assertEquals(400, HttpResponseException.class.cast(cause.getCause()).getStatusCode());
            Assert.assertTrue(new String(HttpResponseException.class.cast(cause.getCause()).getBytes()).contains("invalid_resource_id"));
        }
        try {
            getAuthzClient().protection().permission().create(new PermissionRequest());
            Assert.fail("Should fail, resource is empty");
        } catch (RuntimeException cause) {
            cause.printStackTrace();
            Assert.assertTrue(HttpResponseException.class.isInstance(cause.getCause()));
            Assert.assertEquals(400, HttpResponseException.class.cast(cause.getCause()).getStatusCode());
            Assert.assertTrue(new String(HttpResponseException.class.cast(cause.getCause()).getBytes()).contains("invalid_resource_id"));
        }
    }

    @Test
    public void failInvalidScope() throws Exception {
        addResource("Resource A", "ScopeA", "ScopeB");
        try {
            PermissionRequest permissionRequest = new PermissionRequest("Resource A");
            permissionRequest.setScopes(new HashSet(Arrays.asList("ScopeA", "ScopeC")));
            getAuthzClient().protection().permission().create(permissionRequest);
            Assert.fail("Should fail, resource does not exist");
        } catch (RuntimeException cause) {
            Assert.assertTrue(HttpResponseException.class.isInstance(cause.getCause()));
            Assert.assertEquals(400, HttpResponseException.class.cast(cause.getCause()).getStatusCode());
            Assert.assertTrue(new String(HttpResponseException.class.cast(cause.getCause()).getBytes()).contains("invalid_scope"));
        }
    }
}

