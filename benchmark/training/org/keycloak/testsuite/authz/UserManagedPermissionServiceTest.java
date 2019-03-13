/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates
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


import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import javax.ws.rs.NotFoundException;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.authorization.client.AuthorizationDeniedException;
import org.keycloak.authorization.client.resource.AuthorizationResource;
import org.keycloak.authorization.client.resource.PolicyResource;
import org.keycloak.authorization.client.resource.ProtectionResource;
import org.keycloak.authorization.client.util.HttpResponseException;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.idm.authorization.AuthorizationRequest;
import org.keycloak.representations.idm.authorization.AuthorizationResponse;
import org.keycloak.representations.idm.authorization.Permission;
import org.keycloak.representations.idm.authorization.PermissionResponse;
import org.keycloak.representations.idm.authorization.PermissionTicketRepresentation;
import org.keycloak.representations.idm.authorization.ResourceRepresentation;
import org.keycloak.representations.idm.authorization.UmaPermissionRepresentation;


/**
 *
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Igor</a>
 */
public class UserManagedPermissionServiceTest extends AbstractResourceServerTest {
    @Test
    public void testCreate() {
        ResourceRepresentation resource = new ResourceRepresentation();
        resource.setName("Resource A");
        resource.setOwnerManagedAccess(true);
        resource.setOwner("marta");
        resource.addScope("Scope A", "Scope B", "Scope C");
        resource = getAuthzClient().protection().resource().create(resource);
        UmaPermissionRepresentation newPermission = new UmaPermissionRepresentation();
        newPermission.setName("Custom User-Managed Permission");
        newPermission.setDescription("Users from specific roles are allowed to access");
        newPermission.addScope("Scope A", "Scope B", "Scope C");
        newPermission.addRole("role_a", "role_b", "role_c", "role_d");
        newPermission.addGroup("/group_a", "/group_a/group_b", "/group_c");
        newPermission.addClient("client-a", "resource-server-test");
        newPermission.setCondition("$evaluation.grant()");
        newPermission.addUser("kolo");
        ProtectionResource protection = getAuthzClient().protection("marta", "password");
        UmaPermissionRepresentation permission = protection.policy(resource.getId()).create(newPermission);
        Assert.assertEquals(newPermission.getName(), permission.getName());
        Assert.assertEquals(newPermission.getDescription(), permission.getDescription());
        Assert.assertNotNull(permission.getScopes());
        Assert.assertTrue(permission.getScopes().containsAll(newPermission.getScopes()));
        Assert.assertNotNull(permission.getRoles());
        Assert.assertTrue(permission.getRoles().containsAll(newPermission.getRoles()));
        Assert.assertNotNull(permission.getGroups());
        Assert.assertTrue(permission.getGroups().containsAll(newPermission.getGroups()));
        Assert.assertNotNull(permission.getClients());
        Assert.assertTrue(permission.getClients().containsAll(newPermission.getClients()));
        Assert.assertEquals(newPermission.getCondition(), permission.getCondition());
        Assert.assertNotNull(permission.getUsers());
        Assert.assertTrue(permission.getUsers().containsAll(newPermission.getUsers()));
    }

    @Test
    public void testUpdate() {
        ResourceRepresentation resource = new ResourceRepresentation();
        resource.setName("Resource A");
        resource.setOwnerManagedAccess(true);
        resource.setOwner("marta");
        resource.addScope("Scope A", "Scope B", "Scope C");
        resource = getAuthzClient().protection().resource().create(resource);
        UmaPermissionRepresentation permission = new UmaPermissionRepresentation();
        permission.setName("Custom User-Managed Permission");
        permission.setDescription("Users from specific roles are allowed to access");
        permission.addScope("Scope A");
        permission.addRole("role_a");
        ProtectionResource protection = getAuthzClient().protection("marta", "password");
        permission = protection.policy(resource.getId()).create(permission);
        Assert.assertEquals(1, getAssociatedPolicies(permission).size());
        permission.setName("Changed");
        permission.setDescription("Changed");
        protection.policy(resource.getId()).update(permission);
        UmaPermissionRepresentation updated = protection.policy(resource.getId()).findById(permission.getId());
        Assert.assertEquals(permission.getName(), updated.getName());
        Assert.assertEquals(permission.getDescription(), updated.getDescription());
        permission.removeRole("role_a");
        permission.addRole("role_b", "role_c");
        protection.policy(resource.getId()).update(permission);
        Assert.assertEquals(1, getAssociatedPolicies(permission).size());
        updated = protection.policy(resource.getId()).findById(permission.getId());
        Assert.assertTrue(permission.getRoles().containsAll(updated.getRoles()));
        permission.addRole("role_d");
        protection.policy(resource.getId()).update(permission);
        Assert.assertEquals(1, getAssociatedPolicies(permission).size());
        updated = protection.policy(resource.getId()).findById(permission.getId());
        Assert.assertTrue(permission.getRoles().containsAll(updated.getRoles()));
        permission.addGroup("/group_a/group_b");
        protection.policy(resource.getId()).update(permission);
        Assert.assertEquals(2, getAssociatedPolicies(permission).size());
        updated = protection.policy(resource.getId()).findById(permission.getId());
        Assert.assertTrue(permission.getGroups().containsAll(updated.getGroups()));
        permission.addGroup("/group_a");
        protection.policy(resource.getId()).update(permission);
        Assert.assertEquals(2, getAssociatedPolicies(permission).size());
        updated = protection.policy(resource.getId()).findById(permission.getId());
        Assert.assertTrue(permission.getGroups().containsAll(updated.getGroups()));
        permission.removeGroup("/group_a/group_b");
        permission.addGroup("/group_c");
        protection.policy(resource.getId()).update(permission);
        Assert.assertEquals(2, getAssociatedPolicies(permission).size());
        updated = protection.policy(resource.getId()).findById(permission.getId());
        Assert.assertTrue(permission.getGroups().containsAll(updated.getGroups()));
        permission.addClient("client-a");
        protection.policy(resource.getId()).update(permission);
        Assert.assertEquals(3, getAssociatedPolicies(permission).size());
        updated = protection.policy(resource.getId()).findById(permission.getId());
        Assert.assertTrue(permission.getClients().containsAll(updated.getClients()));
        permission.addClient("resource-server-test");
        protection.policy(resource.getId()).update(permission);
        Assert.assertEquals(3, getAssociatedPolicies(permission).size());
        updated = protection.policy(resource.getId()).findById(permission.getId());
        Assert.assertTrue(permission.getClients().containsAll(updated.getClients()));
        permission.removeClient("client-a");
        protection.policy(resource.getId()).update(permission);
        Assert.assertEquals(3, getAssociatedPolicies(permission).size());
        updated = protection.policy(resource.getId()).findById(permission.getId());
        Assert.assertTrue(permission.getClients().containsAll(updated.getClients()));
        permission.setCondition("$evaluation.grant()");
        protection.policy(resource.getId()).update(permission);
        Assert.assertEquals(4, getAssociatedPolicies(permission).size());
        updated = protection.policy(resource.getId()).findById(permission.getId());
        Assert.assertEquals(permission.getCondition(), updated.getCondition());
        permission.addUser("alice");
        protection.policy(resource.getId()).update(permission);
        Assert.assertEquals(5, getAssociatedPolicies(permission).size());
        updated = protection.policy(resource.getId()).findById(permission.getId());
        Assert.assertEquals(1, updated.getUsers().size());
        Assert.assertEquals(permission.getUsers(), updated.getUsers());
        permission.addUser("kolo");
        protection.policy(resource.getId()).update(permission);
        Assert.assertEquals(5, getAssociatedPolicies(permission).size());
        updated = protection.policy(resource.getId()).findById(permission.getId());
        Assert.assertEquals(2, updated.getUsers().size());
        Assert.assertEquals(permission.getUsers(), updated.getUsers());
        permission.removeUser("alice");
        protection.policy(resource.getId()).update(permission);
        Assert.assertEquals(5, getAssociatedPolicies(permission).size());
        updated = protection.policy(resource.getId()).findById(permission.getId());
        Assert.assertEquals(1, updated.getUsers().size());
        Assert.assertEquals(permission.getUsers(), updated.getUsers());
        permission.setUsers(null);
        protection.policy(resource.getId()).update(permission);
        Assert.assertEquals(4, getAssociatedPolicies(permission).size());
        updated = protection.policy(resource.getId()).findById(permission.getId());
        Assert.assertEquals(permission.getUsers(), updated.getUsers());
        permission.setCondition(null);
        protection.policy(resource.getId()).update(permission);
        Assert.assertEquals(3, getAssociatedPolicies(permission).size());
        updated = protection.policy(resource.getId()).findById(permission.getId());
        Assert.assertEquals(permission.getCondition(), updated.getCondition());
        permission.setRoles(null);
        protection.policy(resource.getId()).update(permission);
        Assert.assertEquals(2, getAssociatedPolicies(permission).size());
        updated = protection.policy(resource.getId()).findById(permission.getId());
        Assert.assertEquals(permission.getRoles(), updated.getRoles());
        permission.setClients(null);
        protection.policy(resource.getId()).update(permission);
        Assert.assertEquals(1, getAssociatedPolicies(permission).size());
        updated = protection.policy(resource.getId()).findById(permission.getId());
        Assert.assertEquals(permission.getClients(), updated.getClients());
        permission.setGroups(null);
        try {
            protection.policy(resource.getId()).update(permission);
            Assert.assertEquals(1, getAssociatedPolicies(permission).size());
            Assert.fail("Permission must be removed because the last associated policy was removed");
        } catch (NotFoundException ignore) {
        } catch (Exception e) {
            Assert.fail("Expected not found");
        }
    }

    @Test
    public void testUserManagedPermission() {
        ResourceRepresentation resource = new ResourceRepresentation();
        resource.setName("Resource A");
        resource.setOwnerManagedAccess(true);
        resource.setOwner("marta");
        resource.addScope("Scope A", "Scope B", "Scope C");
        resource = getAuthzClient().protection().resource().create(resource);
        UmaPermissionRepresentation permission = new UmaPermissionRepresentation();
        permission.setName("Custom User-Managed Permission");
        permission.setDescription("Users from specific roles are allowed to access");
        permission.addScope("Scope A");
        permission.addRole("role_a");
        ProtectionResource protection = getAuthzClient().protection("marta", "password");
        permission = protection.policy(resource.getId()).create(permission);
        AuthorizationResource authorization = getAuthzClient().authorization("kolo", "password");
        AuthorizationRequest request = new AuthorizationRequest();
        request.addPermission(resource.getId(), "Scope A");
        AuthorizationResponse authzResponse = authorization.authorize(request);
        Assert.assertNotNull(authzResponse);
        permission.removeRole("role_a");
        permission.addRole("role_b");
        protection.policy(resource.getId()).update(permission);
        try {
            authorization.authorize(request);
            Assert.fail("User should not have permission");
        } catch (Exception e) {
            Assert.assertTrue(AuthorizationDeniedException.class.isInstance(e));
        }
        try {
            getAuthzClient().authorization("alice", "password").authorize(request);
            Assert.fail("User should not have permission");
        } catch (Exception e) {
            Assert.assertTrue(AuthorizationDeniedException.class.isInstance(e));
        }
        permission.addRole("role_a");
        protection.policy(resource.getId()).update(permission);
        authzResponse = authorization.authorize(request);
        Assert.assertNotNull(authzResponse);
        protection.policy(resource.getId()).delete(permission.getId());
        try {
            authorization.authorize(request);
            Assert.fail("User should not have permission");
        } catch (Exception e) {
            Assert.assertTrue(AuthorizationDeniedException.class.isInstance(e));
        }
        try {
            getAuthzClient().protection("marta", "password").policy(resource.getId()).findById(permission.getId());
            Assert.fail("Permission must not exist");
        } catch (Exception e) {
            Assert.assertEquals(404, HttpResponseException.class.cast(e.getCause()).getStatusCode());
        }
        // create a user based permission, where only selected users are allowed access to the resource.
        permission = new UmaPermissionRepresentation();
        permission.setName("Custom User-Managed Permission");
        permission.setDescription("Specific users are allowed access to the resource");
        permission.addScope("Scope A");
        permission.addUser("alice");
        protection.policy(resource.getId()).create(permission);
        // alice should be able to access the resource with the updated permission.
        authzResponse = getAuthzClient().authorization("alice", "password").authorize(request);
        Assert.assertNotNull(authzResponse);
        // kolo shouldn't be able to access the resource with the updated permission.
        try {
            authorization.authorize(request);
            Assert.fail("User should not have permission to access the protected resource");
        } catch (Exception e) {
            Assert.assertTrue(AuthorizationDeniedException.class.isInstance(e));
        }
    }

    @Test
    public void testPermissionInAdditionToUserGrantedPermission() {
        ResourceRepresentation resource = new ResourceRepresentation();
        resource.setName("Resource A");
        resource.setOwnerManagedAccess(true);
        resource.setOwner("marta");
        resource.addScope("Scope A", "Scope B", "Scope C");
        resource = getAuthzClient().protection().resource().create(resource);
        PermissionResponse ticketResponse = getAuthzClient().protection().permission().create(new org.keycloak.representations.idm.authorization.PermissionRequest(resource.getId(), "Scope A"));
        AuthorizationRequest request = new AuthorizationRequest();
        request.setTicket(ticketResponse.getTicket());
        try {
            getAuthzClient().authorization("kolo", "password").authorize(request);
            Assert.fail("User should not have permission");
        } catch (Exception e) {
            Assert.assertTrue(AuthorizationDeniedException.class.isInstance(e));
            Assert.assertTrue(e.getMessage().contains("request_submitted"));
        }
        List<PermissionTicketRepresentation> tickets = getAuthzClient().protection().permission().findByResource(resource.getId());
        Assert.assertEquals(1, tickets.size());
        PermissionTicketRepresentation ticket = tickets.get(0);
        ticket.setGranted(true);
        getAuthzClient().protection().permission().update(ticket);
        AuthorizationResponse authzResponse = getAuthzClient().authorization("kolo", "password").authorize(request);
        Assert.assertNotNull(authzResponse);
        UmaPermissionRepresentation permission = new UmaPermissionRepresentation();
        permission.setName("Custom User-Managed Permission");
        permission.addScope("Scope A");
        permission.addRole("role_a");
        ProtectionResource protection = getAuthzClient().protection("marta", "password");
        permission = protection.policy(resource.getId()).create(permission);
        getAuthzClient().authorization("kolo", "password").authorize(request);
        ticket.setGranted(false);
        getAuthzClient().protection().permission().update(ticket);
        getAuthzClient().authorization("kolo", "password").authorize(request);
        permission = getAuthzClient().protection("marta", "password").policy(resource.getId()).findById(permission.getId());
        Assert.assertNotNull(permission);
        permission.removeRole("role_a");
        permission.addRole("role_b");
        getAuthzClient().protection("marta", "password").policy(resource.getId()).update(permission);
        try {
            getAuthzClient().authorization("kolo", "password").authorize(request);
            Assert.fail("User should not have permission");
        } catch (Exception e) {
            Assert.assertTrue(AuthorizationDeniedException.class.isInstance(e));
        }
        request = new AuthorizationRequest();
        request.addPermission(resource.getId());
        try {
            getAuthzClient().authorization("kolo", "password").authorize(request);
            Assert.fail("User should not have permission");
        } catch (Exception e) {
            Assert.assertTrue(AuthorizationDeniedException.class.isInstance(e));
        }
        getAuthzClient().protection("marta", "password").policy(resource.getId()).delete(permission.getId());
        try {
            getAuthzClient().authorization("kolo", "password").authorize(request);
            Assert.fail("User should not have permission");
        } catch (Exception e) {
            Assert.assertTrue(AuthorizationDeniedException.class.isInstance(e));
        }
    }

    @Test
    public void testPermissionWithoutScopes() {
        ResourceRepresentation resource = new ResourceRepresentation();
        resource.setName(UUID.randomUUID().toString());
        resource.setOwner("marta");
        resource.setOwnerManagedAccess(true);
        resource.addScope("Scope A", "Scope B", "Scope C");
        ProtectionResource protection = getAuthzClient().protection();
        resource = protection.resource().create(resource);
        UmaPermissionRepresentation permission = new UmaPermissionRepresentation();
        permission.setName("Custom User-Managed Policy");
        permission.addRole("role_a");
        PolicyResource policy = getAuthzClient().protection("marta", "password").policy(resource.getId());
        permission = policy.create(permission);
        Assert.assertEquals(3, permission.getScopes().size());
        Assert.assertTrue(Arrays.asList("Scope A", "Scope B", "Scope C").containsAll(permission.getScopes()));
        permission = policy.findById(permission.getId());
        Assert.assertTrue(Arrays.asList("Scope A", "Scope B", "Scope C").containsAll(permission.getScopes()));
        Assert.assertEquals(3, permission.getScopes().size());
        permission.removeScope("Scope B");
        policy.update(permission);
        permission = policy.findById(permission.getId());
        Assert.assertEquals(2, permission.getScopes().size());
        Assert.assertTrue(Arrays.asList("Scope A", "Scope C").containsAll(permission.getScopes()));
    }

    @Test
    public void testOnlyResourceOwnerCanManagePolicies() {
        ResourceRepresentation resource = new ResourceRepresentation();
        resource.setName(UUID.randomUUID().toString());
        resource.setOwner("marta");
        resource.addScope("Scope A", "Scope B", "Scope C");
        ProtectionResource protection = getAuthzClient().protection();
        resource = protection.resource().create(resource);
        try {
            getAuthzClient().protection("alice", "password").policy(resource.getId()).create(new UmaPermissionRepresentation());
            Assert.fail("Error expected");
        } catch (Exception e) {
            Assert.assertTrue(HttpResponseException.class.cast(e.getCause()).toString().contains("Only resource owner can access policies for resource"));
        }
    }

    @Test
    public void testOnlyResourcesWithOwnerManagedAccess() {
        ResourceRepresentation resource = new ResourceRepresentation();
        resource.setName(UUID.randomUUID().toString());
        resource.setOwner("marta");
        resource.addScope("Scope A", "Scope B", "Scope C");
        ProtectionResource protection = getAuthzClient().protection();
        resource = protection.resource().create(resource);
        try {
            getAuthzClient().protection("marta", "password").policy(resource.getId()).create(new UmaPermissionRepresentation());
            Assert.fail("Error expected");
        } catch (Exception e) {
            Assert.assertTrue(HttpResponseException.class.cast(e.getCause()).toString().contains("Only resources with owner managed accessed can have policies"));
        }
    }

    @Test
    public void testFindPermission() {
        ResourceRepresentation resource = new ResourceRepresentation();
        resource.setName(UUID.randomUUID().toString());
        resource.setOwner("marta");
        resource.setOwnerManagedAccess(true);
        resource.addScope("Scope A", "Scope B", "Scope C");
        ProtectionResource protection = getAuthzClient().protection();
        resource = protection.resource().create(resource);
        PolicyResource policy = getAuthzClient().protection("marta", "password").policy(resource.getId());
        for (int i = 0; i < 10; i++) {
            UmaPermissionRepresentation permission = new UmaPermissionRepresentation();
            permission.setName(("Custom User-Managed Policy " + i));
            permission.addRole("role_a");
            policy.create(permission);
        }
        Assert.assertEquals(10, policy.find(null, null, null, null).size());
        List<UmaPermissionRepresentation> byId = policy.find("Custom User-Managed Policy 8", null, null, null);
        Assert.assertEquals(1, byId.size());
        Assert.assertEquals(byId.get(0).getId(), policy.findById(byId.get(0).getId()).getId());
        Assert.assertEquals(10, policy.find(null, "Scope A", null, null).size());
        Assert.assertEquals(5, policy.find(null, null, (-1), 5).size());
        Assert.assertEquals(2, policy.find(null, null, (-1), 2).size());
    }

    @Test
    public void testGrantRequestedScopesOnly() {
        ResourceRepresentation resource = new ResourceRepresentation();
        resource.setName(UUID.randomUUID().toString());
        resource.setOwnerManagedAccess(true);
        resource.setOwner("marta");
        resource.addScope("view", "delete");
        ProtectionResource protection = getAuthzClient().protection("marta", "password");
        resource = protection.resource().create(resource);
        UmaPermissionRepresentation permission = new UmaPermissionRepresentation();
        permission.setName("Custom User-Managed Permission");
        permission.addScope("view");
        permission.addUser("kolo");
        permission = protection.policy(resource.getId()).create(permission);
        AuthorizationRequest request = new AuthorizationRequest();
        request.addPermission(resource.getId(), "view");
        AuthorizationResponse response = getAuthzClient().authorization("kolo", "password").authorize(request);
        AccessToken rpt = toAccessToken(response.getToken());
        Collection<Permission> permissions = rpt.getAuthorization().getPermissions();
        assertPermissions(permissions, resource.getId(), "view");
        Assert.assertTrue(permissions.isEmpty());
        request = new AuthorizationRequest();
        request.addPermission(resource.getId(), "delete");
        try {
            getAuthzClient().authorization("kolo", "password").authorize(request);
            Assert.fail("User should not have permission");
        } catch (Exception e) {
            Assert.assertTrue(AuthorizationDeniedException.class.isInstance(e));
        }
        request = new AuthorizationRequest();
        request.addPermission(resource.getId(), "delete");
        try {
            getAuthzClient().authorization("kolo", "password").authorize(request);
            Assert.fail("User should not have permission");
        } catch (Exception e) {
            Assert.assertTrue(AuthorizationDeniedException.class.isInstance(e));
        }
        request = new AuthorizationRequest();
        request.addPermission(resource.getId());
        response = getAuthzClient().authorization("kolo", "password").authorize(request);
        rpt = toAccessToken(response.getToken());
        permissions = rpt.getAuthorization().getPermissions();
        assertPermissions(permissions, resource.getId(), "view");
        Assert.assertTrue(permissions.isEmpty());
    }
}

