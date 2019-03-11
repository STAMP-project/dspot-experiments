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
package org.keycloak.testsuite.admin.group;


import AdminRoles.ADMIN;
import Constants.ADMIN_CLI_CLIENT_ID;
import OperationType.CREATE;
import OperationType.DELETE;
import OperationType.UPDATE;
import ResourceType.CLIENT;
import ResourceType.CLIENT_ROLE;
import ResourceType.CLIENT_ROLE_MAPPING;
import ResourceType.GROUP;
import ResourceType.GROUP_MEMBERSHIP;
import ResourceType.REALM_ROLE_MAPPING;
import ResourceType.USER;
import Response.Status.FORBIDDEN;
import Status.CREATED;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.GroupResource;
import org.keycloak.admin.client.resource.GroupsResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleMappingResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.MappingsRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.admin.ApiUtil;
import org.keycloak.testsuite.arquillian.AuthServerTestEnricher;
import org.keycloak.testsuite.auth.page.AuthRealm;
import org.keycloak.testsuite.util.AdminEventPaths;
import org.keycloak.testsuite.util.ClientBuilder;
import org.keycloak.testsuite.util.GroupBuilder;
import org.keycloak.testsuite.util.RoleBuilder;
import org.keycloak.testsuite.util.URLAssert;
import org.keycloak.testsuite.util.UserBuilder;
import org.keycloak.testsuite.utils.tls.TLSUtils;
import org.keycloak.util.JsonSerialization;

import static org.keycloak.testsuite.Assert.assertNames;


/**
 *
 *
 * @author <a href="mailto:mstrukel@redhat.com">Marko Strukelj</a>
 */
public class GroupTest extends AbstractGroupTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    /**
     * KEYCLOAK-2716
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testClientRemoveWithClientRoleGroupMapping() throws Exception {
        RealmResource realm = adminClient.realms().realm("test");
        ClientRepresentation client = new ClientRepresentation();
        client.setClientId("foo");
        client.setRootUrl("http://foo");
        client.setProtocol("openid-connect");
        Response response = realm.clients().create(client);
        response.close();
        String clientUuid = ApiUtil.getCreatedId(response);
        assertAdminEvents.assertEvent("test", CREATE, AdminEventPaths.clientResourcePath(clientUuid), client, CLIENT);
        client = realm.clients().findByClientId("foo").get(0);
        RoleRepresentation role = new RoleRepresentation();
        role.setName("foo-role");
        realm.clients().get(client.getId()).roles().create(role);
        assertAdminEvents.assertEvent("test", CREATE, AdminEventPaths.clientRoleResourcePath(clientUuid, "foo-role"), role, CLIENT_ROLE);
        role = realm.clients().get(client.getId()).roles().get("foo-role").toRepresentation();
        GroupRepresentation group = new GroupRepresentation();
        group.setName("2716");
        group = createGroup(realm, group);
        List<RoleRepresentation> list = new LinkedList<>();
        list.add(role);
        realm.groups().group(group.getId()).roles().clientLevel(client.getId()).add(list);
        assertAdminEvents.assertEvent("test", CREATE, AdminEventPaths.groupRolesClientRolesPath(group.getId(), clientUuid), list, CLIENT_ROLE_MAPPING);
        realm.clients().get(client.getId()).remove();
        assertAdminEvents.assertEvent("test", DELETE, AdminEventPaths.clientResourcePath(clientUuid), CLIENT);
    }

    @Test
    public void doNotAllowSameGroupNameAtSameLevel() throws Exception {
        RealmResource realm = adminClient.realms().realm("test");
        GroupRepresentation topGroup = new GroupRepresentation();
        topGroup.setName("top");
        topGroup = createGroup(realm, topGroup);
        GroupRepresentation anotherTopGroup = new GroupRepresentation();
        anotherTopGroup.setName("top");
        Response response = realm.groups().add(anotherTopGroup);
        Assert.assertEquals(409, response.getStatus());// conflict status 409 - same name not allowed

        GroupRepresentation level2Group = new GroupRepresentation();
        level2Group.setName("level2");
        response = realm.groups().group(topGroup.getId()).subGroup(level2Group);
        response.close();
        Assert.assertEquals(201, response.getStatus());// created status

        GroupRepresentation anotherlevel2Group = new GroupRepresentation();
        anotherlevel2Group.setName("level2");
        response = realm.groups().group(topGroup.getId()).subGroup(anotherlevel2Group);
        response.close();
        Assert.assertEquals(409, response.getStatus());// conflict status 409 - same name not allowed

    }

    @Test
    public void createAndTestGroups() throws Exception {
        RealmResource realm = adminClient.realms().realm("test");
        {
            RoleRepresentation groupRole = new RoleRepresentation();
            groupRole.setName("topRole");
            realm.roles().create(groupRole);
        }
        RoleRepresentation topRole = realm.roles().get("topRole").toRepresentation();
        {
            RoleRepresentation groupRole = new RoleRepresentation();
            groupRole.setName("level2Role");
            realm.roles().create(groupRole);
        }
        RoleRepresentation level2Role = realm.roles().get("level2Role").toRepresentation();
        {
            RoleRepresentation groupRole = new RoleRepresentation();
            groupRole.setName("level3Role");
            realm.roles().create(groupRole);
        }
        RoleRepresentation level3Role = realm.roles().get("level3Role").toRepresentation();
        // Role events tested elsewhere
        assertAdminEvents.clear();
        GroupRepresentation topGroup = new GroupRepresentation();
        topGroup.setName("top");
        topGroup = createGroup(realm, topGroup);
        List<RoleRepresentation> roles = new LinkedList<>();
        roles.add(topRole);
        realm.groups().group(topGroup.getId()).roles().realmLevel().add(roles);
        assertAdminEvents.assertEvent("test", CREATE, AdminEventPaths.groupRolesRealmRolesPath(topGroup.getId()), roles, REALM_ROLE_MAPPING);
        GroupRepresentation level2Group = new GroupRepresentation();
        level2Group.setName("level2");
        Response response = realm.groups().group(topGroup.getId()).subGroup(level2Group);
        response.close();
        assertAdminEvents.assertEvent("test", CREATE, AdminEventPaths.groupSubgroupsPath(topGroup.getId()), level2Group, GROUP);
        URI location = response.getLocation();
        final String level2Id = ApiUtil.getCreatedId(response);
        final GroupRepresentation level2GroupById = realm.groups().group(level2Id).toRepresentation();
        Assert.assertEquals(level2Id, level2GroupById.getId());
        Assert.assertEquals(level2Group.getName(), level2GroupById.getName());
        URLAssert.assertGetURL(location, adminClient.tokenManager().getAccessTokenString(), new URLAssert.AssertJSONResponseHandler() {
            @Override
            protected void assertResponseBody(String body) throws IOException {
                GroupRepresentation level2 = JsonSerialization.readValue(body, GroupRepresentation.class);
                Assert.assertEquals(level2Id, level2.getId());
            }
        });
        level2Group = realm.getGroupByPath("/top/level2");
        Assert.assertNotNull(level2Group);
        roles.clear();
        roles.add(level2Role);
        realm.groups().group(level2Group.getId()).roles().realmLevel().add(roles);
        assertAdminEvents.assertEvent("test", CREATE, AdminEventPaths.groupRolesRealmRolesPath(level2Group.getId()), roles, REALM_ROLE_MAPPING);
        GroupRepresentation level3Group = new GroupRepresentation();
        level3Group.setName("level3");
        response = realm.groups().group(level2Group.getId()).subGroup(level3Group);
        response.close();
        assertAdminEvents.assertEvent("test", CREATE, AdminEventPaths.groupSubgroupsPath(level2Group.getId()), level3Group, GROUP);
        level3Group = realm.getGroupByPath("/top/level2/level3");
        Assert.assertNotNull(level3Group);
        roles.clear();
        roles.add(level3Role);
        realm.groups().group(level3Group.getId()).roles().realmLevel().add(roles);
        assertAdminEvents.assertEvent("test", CREATE, AdminEventPaths.groupRolesRealmRolesPath(level3Group.getId()), roles, REALM_ROLE_MAPPING);
        topGroup = realm.getGroupByPath("/top");
        Assert.assertEquals(1, topGroup.getRealmRoles().size());
        Assert.assertTrue(contains("topRole"));
        Assert.assertEquals(1, topGroup.getSubGroups().size());
        level2Group = topGroup.getSubGroups().get(0);
        Assert.assertEquals("level2", level2Group.getName());
        Assert.assertEquals(1, level2Group.getRealmRoles().size());
        Assert.assertTrue(contains("level2Role"));
        Assert.assertEquals(1, level2Group.getSubGroups().size());
        level3Group = level2Group.getSubGroups().get(0);
        Assert.assertEquals("level3", level3Group.getName());
        Assert.assertEquals(1, level3Group.getRealmRoles().size());
        Assert.assertTrue(contains("level3Role"));
        UserRepresentation user = realm.users().search("direct-login", (-1), (-1)).get(0);
        realm.users().get(user.getId()).joinGroup(level3Group.getId());
        assertAdminEvents.assertEvent("test", CREATE, AdminEventPaths.userGroupPath(user.getId(), level3Group.getId()), GROUP_MEMBERSHIP);
        List<GroupRepresentation> membership = realm.users().get(user.getId()).groups();
        Assert.assertEquals(1, membership.size());
        Assert.assertEquals("level3", membership.get(0).getName());
        AccessToken token = login("direct-login", "resource-owner", "secret", user.getId());
        Assert.assertTrue(contains("topRole"));
        Assert.assertTrue(contains("level2Role"));
        Assert.assertTrue(contains("level3Role"));
        realm.addDefaultGroup(level3Group.getId());
        assertAdminEvents.assertEvent("test", CREATE, AdminEventPaths.defaultGroupPath(level3Group.getId()), GROUP);
        List<GroupRepresentation> defaultGroups = realm.getDefaultGroups();
        Assert.assertEquals(1, defaultGroups.size());
        Assert.assertEquals(defaultGroups.get(0).getId(), level3Group.getId());
        UserRepresentation newUser = new UserRepresentation();
        newUser.setUsername("groupUser");
        newUser.setEmail("group@group.com");
        response = realm.users().create(newUser);
        String userId = ApiUtil.getCreatedId(response);
        response.close();
        assertAdminEvents.assertEvent("test", CREATE, AdminEventPaths.userResourcePath(userId), newUser, USER);
        membership = realm.users().get(userId).groups();
        Assert.assertEquals(1, membership.size());
        Assert.assertEquals("level3", membership.get(0).getName());
        realm.removeDefaultGroup(level3Group.getId());
        assertAdminEvents.assertEvent("test", DELETE, AdminEventPaths.defaultGroupPath(level3Group.getId()), GROUP);
        defaultGroups = realm.getDefaultGroups();
        Assert.assertEquals(0, defaultGroups.size());
        realm.groups().group(topGroup.getId()).remove();
        assertAdminEvents.assertEvent("test", DELETE, AdminEventPaths.groupPath(topGroup.getId()), GROUP);
        try {
            realm.getGroupByPath("/top/level2/level3");
            Assert.fail("Group should not have been found");
        } catch (NotFoundException e) {
        }
        try {
            realm.getGroupByPath("/top/level2");
            Assert.fail("Group should not have been found");
        } catch (NotFoundException e) {
        }
        try {
            realm.getGroupByPath("/top");
            Assert.fail("Group should not have been found");
        } catch (NotFoundException e) {
        }
        Assert.assertNull(login("direct-login", "resource-owner", "secret", user.getId()).getRealmAccess());
    }

    @Test
    public void updateGroup() {
        RealmResource realm = adminClient.realms().realm("test");
        final String groupName = "group-" + (UUID.randomUUID());
        GroupRepresentation group = GroupBuilder.create().name(groupName).singleAttribute("attr1", "attrval1").singleAttribute("attr2", "attrval2").build();
        createGroup(realm, group);
        group = realm.getGroupByPath(("/" + groupName));
        Assert.assertNotNull(group);
        Assert.assertThat(group.getName(), is(groupName));
        Assert.assertThat(group.getAttributes().keySet(), containsInAnyOrder("attr1", "attr2"));
        Assert.assertThat(group.getAttributes(), hasEntry(is("attr1"), contains("attrval1")));
        Assert.assertThat(group.getAttributes(), hasEntry(is("attr2"), contains("attrval2")));
        final String groupNewName = "group-" + (UUID.randomUUID());
        group.setName(groupNewName);
        group.getAttributes().remove("attr1");
        group.getAttributes().get("attr2").add("attrval2-2");
        group.getAttributes().put("attr3", Collections.singletonList("attrval2"));
        realm.groups().group(group.getId()).update(group);
        assertAdminEvents.assertEvent("test", UPDATE, AdminEventPaths.groupPath(group.getId()), group, GROUP);
        group = realm.getGroupByPath(("/" + groupNewName));
        Assert.assertThat(group.getName(), is(groupNewName));
        Assert.assertThat(group.getAttributes().keySet(), containsInAnyOrder("attr2", "attr3"));
        Assert.assertThat(group.getAttributes(), hasEntry(is("attr2"), containsInAnyOrder("attrval2", "attrval2-2")));
        Assert.assertThat(group.getAttributes(), hasEntry(is("attr3"), contains("attrval2")));
    }

    @Test
    public void groupMembership() {
        RealmResource realm = adminClient.realms().realm("test");
        GroupRepresentation group = new GroupRepresentation();
        group.setName("group");
        String groupId = createGroup(realm, group).getId();
        Response response = realm.users().create(UserBuilder.create().username("user-a").build());
        String userAId = ApiUtil.getCreatedId(response);
        response.close();
        assertAdminEvents.assertEvent("test", CREATE, AdminEventPaths.userResourcePath(userAId), USER);
        response = realm.users().create(UserBuilder.create().username("user-b").build());
        String userBId = ApiUtil.getCreatedId(response);
        response.close();
        assertAdminEvents.assertEvent("test", CREATE, AdminEventPaths.userResourcePath(userBId), USER);
        realm.users().get(userAId).joinGroup(groupId);
        assertAdminEvents.assertEvent("test", CREATE, AdminEventPaths.userGroupPath(userAId, groupId), group, GROUP_MEMBERSHIP);
        List<UserRepresentation> members = realm.groups().group(groupId).members(0, 10);
        org.keycloak.testsuite.Assert.assertNames(members, "user-a");
        realm.users().get(userBId).joinGroup(groupId);
        assertAdminEvents.assertEvent("test", CREATE, AdminEventPaths.userGroupPath(userBId, groupId), group, GROUP_MEMBERSHIP);
        members = realm.groups().group(groupId).members(0, 10);
        org.keycloak.testsuite.Assert.assertNames(members, "user-a", "user-b");
        realm.users().get(userAId).leaveGroup(groupId);
        assertAdminEvents.assertEvent("test", DELETE, AdminEventPaths.userGroupPath(userAId, groupId), group, GROUP_MEMBERSHIP);
        members = realm.groups().group(groupId).members(0, 10);
        org.keycloak.testsuite.Assert.assertNames(members, "user-b");
    }

    // KEYCLOAK-6300
    @Test
    public void groupMembershipUsersOrder() {
        RealmResource realm = adminClient.realms().realm("test");
        GroupRepresentation group = new GroupRepresentation();
        group.setName("group");
        String groupId = createGroup(realm, group).getId();
        List<String> usernames = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            UserRepresentation user = UserBuilder.create().username(("user" + i)).build();
            usernames.add(user.getUsername());
            try (Response create = realm.users().create(user)) {
                Assert.assertEquals(CREATED, create.getStatusInfo());
                String userAId = ApiUtil.getCreatedId(create);
                realm.users().get(userAId).joinGroup(groupId);
            }
        }
        List<String> memberUsernames = new ArrayList<>();
        for (UserRepresentation member : realm.groups().group(groupId).members(0, 10)) {
            memberUsernames.add(member.getUsername());
        }
        Assert.assertArrayEquals(((("Expected: " + usernames) + ", was: ") + memberUsernames), usernames.toArray(), memberUsernames.toArray());
    }

    // KEYCLOAK-2700
    @Test
    public void deleteRealmWithDefaultGroups() throws IOException {
        RealmRepresentation rep = new RealmRepresentation();
        rep.setRealm("foo");
        GroupRepresentation group = new GroupRepresentation();
        group.setName("default1");
        group.setPath("/default1");
        rep.setGroups(Collections.singletonList(group));
        rep.setDefaultGroups(Collections.singletonList("/default1"));
        adminClient.realms().create(rep);
        adminClient.realm(rep.getRealm()).remove();
    }

    @Test
    public void roleMappings() {
        RealmResource realm = adminClient.realms().realm("test");
        realm.roles().create(RoleBuilder.create().name("realm-role").build());
        realm.roles().create(RoleBuilder.create().name("realm-composite").build());
        realm.roles().create(RoleBuilder.create().name("realm-child").build());
        realm.roles().get("realm-composite").addComposites(Collections.singletonList(realm.roles().get("realm-child").toRepresentation()));
        try (Response response = realm.clients().create(ClientBuilder.create().clientId("myclient").build())) {
            String clientId = ApiUtil.getCreatedId(response);
            realm.clients().get(clientId).roles().create(RoleBuilder.create().name("client-role").build());
            realm.clients().get(clientId).roles().create(RoleBuilder.create().name("client-role2").build());
            realm.clients().get(clientId).roles().create(RoleBuilder.create().name("client-composite").build());
            realm.clients().get(clientId).roles().create(RoleBuilder.create().name("client-child").build());
            realm.clients().get(clientId).roles().get("client-composite").addComposites(Collections.singletonList(realm.clients().get(clientId).roles().get("client-child").toRepresentation()));
            // Roles+clients tested elsewhere
            assertAdminEvents.clear();
            GroupRepresentation group = new GroupRepresentation();
            group.setName("group");
            String groupId = createGroup(realm, group).getId();
            RoleMappingResource roles = realm.groups().group(groupId).roles();
            Assert.assertEquals(0, roles.realmLevel().listAll().size());
            // Add realm roles
            List<RoleRepresentation> l = new LinkedList<>();
            l.add(realm.roles().get("realm-role").toRepresentation());
            l.add(realm.roles().get("realm-composite").toRepresentation());
            roles.realmLevel().add(l);
            assertAdminEvents.assertEvent("test", CREATE, AdminEventPaths.groupRolesRealmRolesPath(group.getId()), l, REALM_ROLE_MAPPING);
            // Add client roles
            RoleRepresentation clientRole = realm.clients().get(clientId).roles().get("client-role").toRepresentation();
            RoleRepresentation clientComposite = realm.clients().get(clientId).roles().get("client-composite").toRepresentation();
            roles.clientLevel(clientId).add(Collections.singletonList(clientRole));
            roles.clientLevel(clientId).add(Collections.singletonList(clientComposite));
            assertAdminEvents.assertEvent("test", CREATE, AdminEventPaths.groupRolesClientRolesPath(group.getId(), clientId), Collections.singletonList(clientRole), CLIENT_ROLE_MAPPING);
            assertAdminEvents.assertEvent("test", CREATE, AdminEventPaths.groupRolesClientRolesPath(group.getId(), clientId), Collections.singletonList(clientComposite), CLIENT_ROLE_MAPPING);
            // List realm roles
            assertNames(roles.realmLevel().listAll(), "realm-role", "realm-composite");
            assertNames(roles.realmLevel().listAvailable(), "admin", "offline_access", Constants.AUTHZ_UMA_AUTHORIZATION, "user", "customer-user-premium", "realm-composite-role", "sample-realm-role", "attribute-role");
            assertNames(roles.realmLevel().listEffective(), "realm-role", "realm-composite", "realm-child");
            // List client roles
            assertNames(roles.clientLevel(clientId).listAll(), "client-role", "client-composite");
            assertNames(roles.clientLevel(clientId).listAvailable(), "client-role2");
            assertNames(roles.clientLevel(clientId).listEffective(), "client-role", "client-composite", "client-child");
            // Get mapping representation
            MappingsRepresentation all = roles.getAll();
            assertNames(all.getRealmMappings(), "realm-role", "realm-composite");
            Assert.assertEquals(1, all.getClientMappings().size());
            assertNames(all.getClientMappings().get("myclient").getMappings(), "client-role", "client-composite");
            // Remove realm role
            RoleRepresentation realmRoleRep = realm.roles().get("realm-role").toRepresentation();
            roles.realmLevel().remove(Collections.singletonList(realmRoleRep));
            assertAdminEvents.assertEvent("test", DELETE, AdminEventPaths.groupRolesRealmRolesPath(group.getId()), Collections.singletonList(realmRoleRep), REALM_ROLE_MAPPING);
            assertNames(roles.realmLevel().listAll(), "realm-composite");
            // Remove client role
            RoleRepresentation clientRoleRep = realm.clients().get(clientId).roles().get("client-role").toRepresentation();
            roles.clientLevel(clientId).remove(Collections.singletonList(clientRoleRep));
            assertAdminEvents.assertEvent("test", DELETE, AdminEventPaths.groupRolesClientRolesPath(group.getId(), clientId), Collections.singletonList(clientRoleRep), CLIENT_ROLE_MAPPING);
            assertNames(roles.clientLevel(clientId).listAll(), "client-composite");
        }
    }

    /**
     * Verifies that the user does not have access to Keycloak Admin endpoint when role is not
     * assigned to that user.
     *
     * @unknown https://issues.jboss.org/browse/KEYCLOAK-2964
     */
    @Test
    public void noAdminEndpointAccessWhenNoRoleAssigned() {
        String userName = "user-" + (UUID.randomUUID());
        final String realmName = AuthRealm.MASTER;
        createUser(realmName, userName, "pwd");
        try (Keycloak userClient = Keycloak.getInstance(((AuthServerTestEnricher.getAuthServerContextRoot()) + "/auth"), realmName, userName, "pwd", ADMIN_CLI_CLIENT_ID, TLSUtils.initializeTLS())) {
            expectedException.expect(ClientErrorException.class);
            expectedException.expectMessage(String.valueOf(FORBIDDEN.getStatusCode()));
            userClient.realms().findAll();// Any admin operation will do

        }
    }

    /**
     * Verifies that the role assigned to a user is correctly handled by Keycloak Admin endpoint.
     *
     * @unknown https://issues.jboss.org/browse/KEYCLOAK-2964
     */
    @Test
    public void adminEndpointAccessibleWhenAdminRoleAssignedToUser() {
        String userName = "user-" + (UUID.randomUUID());
        final String realmName = AuthRealm.MASTER;
        RealmResource realm = adminClient.realms().realm(realmName);
        RoleRepresentation adminRole = realm.roles().get(ADMIN).toRepresentation();
        Assert.assertThat(adminRole, notNullValue());
        Assert.assertThat(adminRole.getId(), notNullValue());
        String userId = createUser(realmName, userName, "pwd");
        Assert.assertThat(userId, notNullValue());
        RoleMappingResource mappings = realm.users().get(userId).roles();
        mappings.realmLevel().add(Collections.singletonList(adminRole));
        try (Keycloak userClient = Keycloak.getInstance(((AuthServerTestEnricher.getAuthServerContextRoot()) + "/auth"), realmName, userName, "pwd", ADMIN_CLI_CLIENT_ID, TLSUtils.initializeTLS())) {
            // Any admin operation will do
            Assert.assertThat(userClient.realms().findAll(), not(empty()));
        }
    }

    /**
     * Verifies that the role assigned to a user's group is correctly handled by Keycloak Admin endpoint.
     *
     * @unknown https://issues.jboss.org/browse/KEYCLOAK-2964
     */
    @Test
    public void adminEndpointAccessibleWhenAdminRoleAssignedToGroup() {
        String userName = "user-" + (UUID.randomUUID());
        String groupName = "group-" + (UUID.randomUUID());
        final String realmName = AuthRealm.MASTER;
        RealmResource realm = adminClient.realms().realm(realmName);
        RoleRepresentation adminRole = realm.roles().get(ADMIN).toRepresentation();
        Assert.assertThat(adminRole, notNullValue());
        Assert.assertThat(adminRole.getId(), notNullValue());
        String userId = createUser(realmName, userName, "pwd");
        GroupRepresentation group = GroupBuilder.create().name(groupName).build();
        try (Response response = realm.groups().add(group)) {
            String groupId = ApiUtil.getCreatedId(response);
            RoleMappingResource mappings = realm.groups().group(groupId).roles();
            mappings.realmLevel().add(Collections.singletonList(adminRole));
            realm.users().get(userId).joinGroup(groupId);
        }
        try (Keycloak userClient = Keycloak.getInstance(((AuthServerTestEnricher.getAuthServerContextRoot()) + "/auth"), realmName, userName, "pwd", ADMIN_CLI_CLIENT_ID, TLSUtils.initializeTLS())) {
            // Any admin operation will do
            Assert.assertThat(userClient.realms().findAll(), not(empty()));
        }
    }

    /**
     * Verifies that the role assigned to a user's group is correctly handled by Keycloak Admin endpoint.
     *
     * @unknown https://issues.jboss.org/browse/KEYCLOAK-2964
     */
    @Test
    public void adminEndpointAccessibleWhenAdminRoleAssignedToGroupAfterUserJoinedIt() {
        String userName = "user-" + (UUID.randomUUID());
        String groupName = "group-" + (UUID.randomUUID());
        final String realmName = AuthRealm.MASTER;
        RealmResource realm = adminClient.realms().realm(realmName);
        RoleRepresentation adminRole = realm.roles().get(ADMIN).toRepresentation();
        Assert.assertThat(adminRole, notNullValue());
        Assert.assertThat(adminRole.getId(), notNullValue());
        String userId = createUser(realmName, userName, "pwd");
        GroupRepresentation group = GroupBuilder.create().name(groupName).build();
        try (Response response = realm.groups().add(group)) {
            String groupId = ApiUtil.getCreatedId(response);
            realm.users().get(userId).joinGroup(groupId);
            RoleMappingResource mappings = realm.groups().group(groupId).roles();
            mappings.realmLevel().add(Collections.singletonList(adminRole));
        }
        try (Keycloak userClient = Keycloak.getInstance(((AuthServerTestEnricher.getAuthServerContextRoot()) + "/auth"), realmName, userName, "pwd", ADMIN_CLI_CLIENT_ID, TLSUtils.initializeTLS())) {
            // Any admin operation will do
            Assert.assertThat(userClient.realms().findAll(), not(empty()));
        }
    }

    @Test
    public void defaultMaxResults() {
        GroupsResource groups = adminClient.realms().realm("test").groups();
        try (Response response = groups.add(GroupBuilder.create().name("test").build())) {
            String groupId = ApiUtil.getCreatedId(response);
            GroupResource group = groups.group(groupId);
            UsersResource users = adminClient.realms().realm("test").users();
            for (int i = 0; i < 110; i++) {
                try (Response r = users.create(UserBuilder.create().username(("test-" + i)).build())) {
                    users.get(ApiUtil.getCreatedId(r)).joinGroup(groupId);
                }
            }
            Assert.assertEquals(100, group.members(null, null).size());
            Assert.assertEquals(100, group.members().size());
            Assert.assertEquals(105, group.members(0, 105).size());
            Assert.assertEquals(110, group.members(0, 1000).size());
            Assert.assertEquals(110, group.members((-1), (-2)).size());
        }
    }

    @Test
    public void searchAndCountGroups() throws Exception {
        String firstGroupId = "";
        RealmResource realm = adminClient.realms().realm("test");
        // Clean up all test groups
        for (GroupRepresentation group : realm.groups().groups()) {
            GroupResource resource = realm.groups().group(group.getId());
            resource.remove();
            assertAdminEvents.assertEvent("test", DELETE, AdminEventPaths.groupPath(group.getId()), GROUP);
        }
        // Add 20 new groups with known names
        for (int i = 0; i < 20; i++) {
            GroupRepresentation group = new GroupRepresentation();
            group.setName(("group" + i));
            group = createGroup(realm, group);
            if (i == 0) {
                firstGroupId = group.getId();
            }
        }
        // Get groups by search and pagination
        List<GroupRepresentation> allGroups = realm.groups().groups();
        Assert.assertEquals(20, allGroups.size());
        List<GroupRepresentation> slice = realm.groups().groups(5, 7);
        Assert.assertEquals(7, slice.size());
        List<GroupRepresentation> search = realm.groups().groups("group1", 0, 20);
        Assert.assertEquals(11, search.size());
        for (GroupRepresentation group : search) {
            Assert.assertTrue(contains("group1"));
        }
        List<GroupRepresentation> noResultSearch = realm.groups().groups("abcd", 0, 20);
        Assert.assertEquals(0, noResultSearch.size());
        // Count
        Assert.assertEquals(new Long(allGroups.size()), realm.groups().count().get("count"));
        Assert.assertEquals(new Long(search.size()), realm.groups().count("group1").get("count"));
        Assert.assertEquals(new Long(noResultSearch.size()), realm.groups().count("abcd").get("count"));
        // Add a subgroup for onlyTopLevel flag testing
        GroupRepresentation level2Group = new GroupRepresentation();
        level2Group.setName("group1111");
        Response response = realm.groups().group(firstGroupId).subGroup(level2Group);
        response.close();
        assertAdminEvents.assertEvent("test", CREATE, AdminEventPaths.groupSubgroupsPath(firstGroupId), level2Group, GROUP);
        Assert.assertEquals(new Long(allGroups.size()), realm.groups().count(true).get("count"));
        Assert.assertEquals(new Long(((allGroups.size()) + 1)), realm.groups().count(false).get("count"));
    }
}

