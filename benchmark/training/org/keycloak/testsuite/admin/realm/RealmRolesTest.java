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
package org.keycloak.testsuite.admin.realm;


import OperationType.CREATE;
import OperationType.DELETE;
import OperationType.UPDATE;
import ResourceType.REALM_ROLE;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.NotFoundException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.admin.AbstractAdminTest;
import org.keycloak.testsuite.util.AdminEventPaths;
import org.keycloak.testsuite.util.RoleBuilder;

import static org.keycloak.testsuite.Assert.assertNames;


/**
 *
 *
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class RealmRolesTest extends AbstractAdminTest {
    private RolesResource resource;

    private Map<String, String> ids = new HashMap<>();

    private String clientUuid;

    @Test
    public void getRole() {
        RoleRepresentation role = resource.get("role-a").toRepresentation();
        Assert.assertNotNull(role);
        Assert.assertEquals("role-a", role.getName());
        Assert.assertEquals("Role A", role.getDescription());
        Assert.assertFalse(role.isComposite());
    }

    @Test
    public void updateRole() {
        RoleRepresentation role = resource.get("role-a").toRepresentation();
        role.setName("role-a-new");
        role.setDescription("Role A New");
        resource.get("role-a").update(role);
        assertAdminEvents.assertEvent(realmId, UPDATE, AdminEventPaths.roleResourcePath("role-a"), role, REALM_ROLE);
        role = resource.get("role-a-new").toRepresentation();
        Assert.assertNotNull(role);
        Assert.assertEquals("role-a-new", role.getName());
        Assert.assertEquals("Role A New", role.getDescription());
        Assert.assertFalse(role.isComposite());
    }

    @Test
    public void deleteRole() {
        Assert.assertNotNull(resource.get("role-a"));
        resource.deleteRole("role-a");
        assertAdminEvents.assertEvent(realmId, DELETE, AdminEventPaths.roleResourcePath("role-a"), REALM_ROLE);
        try {
            resource.get("role-a").toRepresentation();
            Assert.fail("Expected 404");
        } catch (NotFoundException e) {
            // expected
        }
    }

    @Test
    public void composites() {
        Assert.assertFalse(resource.get("role-a").toRepresentation().isComposite());
        Assert.assertEquals(0, resource.get("role-a").getRoleComposites().size());
        List<RoleRepresentation> l = new LinkedList<>();
        l.add(RoleBuilder.create().id(ids.get("role-b")).build());
        l.add(RoleBuilder.create().id(ids.get("role-c")).build());
        resource.get("role-a").addComposites(l);
        assertAdminEvents.assertEvent(realmId, CREATE, AdminEventPaths.roleResourceCompositesPath("role-a"), l, REALM_ROLE);
        Set<RoleRepresentation> composites = resource.get("role-a").getRoleComposites();
        Assert.assertTrue(resource.get("role-a").toRepresentation().isComposite());
        assertNames(composites, "role-b", "role-c");
        Set<RoleRepresentation> realmComposites = resource.get("role-a").getRealmRoleComposites();
        assertNames(realmComposites, "role-b");
        Set<RoleRepresentation> clientComposites = resource.get("role-a").getClientRoleComposites(clientUuid);
        assertNames(clientComposites, "role-c");
        resource.get("role-a").deleteComposites(l);
        assertAdminEvents.assertEvent(realmId, DELETE, AdminEventPaths.roleResourceCompositesPath("role-a"), l, REALM_ROLE);
        Assert.assertFalse(resource.get("role-a").toRepresentation().isComposite());
        Assert.assertEquals(0, resource.get("role-a").getRoleComposites().size());
    }

    /**
     * KEYCLOAK-2035 Verifies that Users assigned to Role are being properly retrieved as members in API endpoint for role membership
     */
    @Test
    public void testUsersInRole() {
        RoleResource role = resource.get("role-with-users");
        List<UserRepresentation> users = adminClient.realm(AbstractAdminTest.REALM_NAME).users().search("test-role-member", null, null, null, null, null);
        Assert.assertEquals(1, users.size());
        UserResource user = adminClient.realm(AbstractAdminTest.REALM_NAME).users().get(users.get(0).getId());
        UserRepresentation userRep = user.toRepresentation();
        RoleResource roleResource = adminClient.realm(AbstractAdminTest.REALM_NAME).roles().get(role.toRepresentation().getName());
        List<RoleRepresentation> rolesToAdd = new LinkedList<>();
        rolesToAdd.add(roleResource.toRepresentation());
        adminClient.realm(AbstractAdminTest.REALM_NAME).users().get(userRep.getId()).roles().realmLevel().add(rolesToAdd);
        roleResource = adminClient.realm(AbstractAdminTest.REALM_NAME).roles().get(role.toRepresentation().getName());
        roleResource.getRoleUserMembers();
        // roleResource.getRoleUserMembers().stream().forEach((member) -> log.infof("Found user {}", member.getUsername()));
        Assert.assertEquals(1, roleResource.getRoleUserMembers().size());
    }

    /**
     * KEYCLOAK-2035  Verifies that Role with no users assigned is being properly retrieved without members in API endpoint for role membership
     */
    @Test
    public void testUsersNotInRole() {
        RoleResource role = resource.get("role-without-users");
        role = adminClient.realm(AbstractAdminTest.REALM_NAME).roles().get(role.toRepresentation().getName());
        role.getRoleUserMembers();
        Assert.assertEquals(0, role.getRoleUserMembers().size());
    }

    /**
     * KEYCLOAK-2035 Verifies that Role Membership is ok after user removal
     */
    @Test
    public void roleMembershipAfterUserRemoval() {
        RoleResource role = resource.get("role-with-users");
        List<UserRepresentation> users = adminClient.realm(AbstractAdminTest.REALM_NAME).users().search("test-role-member", null, null, null, null, null);
        Assert.assertEquals(1, users.size());
        UserResource user = adminClient.realm(AbstractAdminTest.REALM_NAME).users().get(users.get(0).getId());
        UserRepresentation userRep = user.toRepresentation();
        RoleResource roleResource = adminClient.realm(AbstractAdminTest.REALM_NAME).roles().get(role.toRepresentation().getName());
        List<RoleRepresentation> rolesToAdd = new LinkedList<>();
        rolesToAdd.add(roleResource.toRepresentation());
        adminClient.realm(AbstractAdminTest.REALM_NAME).users().get(userRep.getId()).roles().realmLevel().add(rolesToAdd);
        roleResource = adminClient.realm(AbstractAdminTest.REALM_NAME).roles().get(role.toRepresentation().getName());
        roleResource.getRoleUserMembers();
        Assert.assertEquals(1, roleResource.getRoleUserMembers().size());
        adminClient.realm(AbstractAdminTest.REALM_NAME).users().delete(userRep.getId());
        roleResource.getRoleUserMembers();
        Assert.assertEquals(0, roleResource.getRoleUserMembers().size());
    }

    @Test
    public void testRoleMembershipWithPagination() {
        RoleResource role = resource.get("role-with-users");
        // Add a second user
        UserRepresentation userRep2 = new UserRepresentation();
        userRep2.setUsername("test-role-member2");
        userRep2.setEmail("test-role-member2@test-role-member.com");
        userRep2.setRequiredActions(Collections.<String>emptyList());
        userRep2.setEnabled(true);
        adminClient.realm(AbstractAdminTest.REALM_NAME).users().create(userRep2);
        List<UserRepresentation> users = adminClient.realm(AbstractAdminTest.REALM_NAME).users().search("test-role-member", null, null, null, null, null);
        MatcherAssert.assertThat(users, hasSize(2));
        for (UserRepresentation userRepFromList : users) {
            UserResource user = adminClient.realm(AbstractAdminTest.REALM_NAME).users().get(userRepFromList.getId());
            UserRepresentation userRep = user.toRepresentation();
            RoleResource roleResource = adminClient.realm(AbstractAdminTest.REALM_NAME).roles().get(role.toRepresentation().getName());
            List<RoleRepresentation> rolesToAdd = new LinkedList<>();
            rolesToAdd.add(roleResource.toRepresentation());
            adminClient.realm(AbstractAdminTest.REALM_NAME).users().get(userRep.getId()).roles().realmLevel().add(rolesToAdd);
        }
        RoleResource roleResource = adminClient.realm(AbstractAdminTest.REALM_NAME).roles().get(role.toRepresentation().getName());
        Set<UserRepresentation> roleUserMembers = roleResource.getRoleUserMembers(0, 1);
        Set<String> expectedMembers = new HashSet<>();
        MatcherAssert.assertThat(roleUserMembers, hasSize(1));
        expectedMembers.add(roleUserMembers.iterator().next().getUsername());
        roleUserMembers = roleResource.getRoleUserMembers(1, 1);
        MatcherAssert.assertThat(roleUserMembers, hasSize(1));
        expectedMembers.add(roleUserMembers.iterator().next().getUsername());
        roleUserMembers = roleResource.getRoleUserMembers(2, 1);
        MatcherAssert.assertThat(roleUserMembers, Matchers.is(Matchers.empty()));
        MatcherAssert.assertThat(expectedMembers, Matchers.containsInAnyOrder("test-role-member", "test-role-member2"));
    }
}

