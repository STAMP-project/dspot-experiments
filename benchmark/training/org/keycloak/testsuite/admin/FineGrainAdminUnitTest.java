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
package org.keycloak.testsuite.admin;


import AdminRoles.CREATE_CLIENT;
import AdminRoles.QUERY_REALMS;
import AuthRealm.MASTER;
import Constants.ADMIN_CLI_CLIENT_ID;
import Constants.REALM_MANAGEMENT_CLIENT_ID;
import Profile.Feature.TOKEN_EXCHANGE;
import java.util.LinkedList;
import java.util.List;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.Response;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.authorization.AuthorizationProvider;
import org.keycloak.authorization.model.Policy;
import org.keycloak.authorization.model.ResourceServer;
import org.keycloak.models.GroupModel;
import org.keycloak.models.utils.RepresentationToModel;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.ClientScopeRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.idm.authorization.ClientPolicyRepresentation;
import org.keycloak.representations.idm.authorization.UserPolicyRepresentation;
import org.keycloak.services.resources.admin.permissions.AdminPermissionManagement;
import org.keycloak.services.resources.admin.permissions.AdminPermissions;
import org.keycloak.services.resources.admin.permissions.GroupPermissionManagement;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.ProfileAssume;
import org.keycloak.testsuite.arquillian.AuthServerTestEnricher;
import org.keycloak.testsuite.util.AdminClientUtil;
import org.keycloak.testsuite.utils.tls.TLSUtils;


/**
 *
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
// @Ignore
public class FineGrainAdminUnitTest extends AbstractKeycloakTest {
    public static final String CLIENT_NAME = "application";

    @Test
    public void testRestEvaluation() throws Exception {
        testingClient.server().run(FineGrainAdminUnitTest::setupPolices);
        testingClient.server().run(FineGrainAdminUnitTest::setupUsers);
        UserRepresentation user1 = adminClient.realm(TEST).users().search("user1").get(0);
        UserRepresentation anotherAdmin = adminClient.realm(TEST).users().search("anotherAdmin").get(0);
        UserRepresentation groupMember = adminClient.realm(TEST).users().search("groupMember").get(0);
        RoleRepresentation realmRole = adminClient.realm(TEST).roles().get("realm-role").toRepresentation();
        List<RoleRepresentation> realmRoleSet = new LinkedList<>();
        realmRoleSet.add(realmRole);
        RoleRepresentation realmRole2 = adminClient.realm(TEST).roles().get("realm-role2").toRepresentation();
        List<RoleRepresentation> realmRole2Set = new LinkedList<>();
        realmRole2Set.add(realmRole2);
        ClientRepresentation client = adminClient.realm(TEST).clients().findByClientId(FineGrainAdminUnitTest.CLIENT_NAME).get(0);
        ClientScopeRepresentation scope = adminClient.realm(TEST).clientScopes().findAll().get(0);
        RoleRepresentation clientRole = adminClient.realm(TEST).clients().get(client.getId()).roles().get("client-role").toRepresentation();
        List<RoleRepresentation> clientRoleSet = new LinkedList<>();
        clientRoleSet.add(clientRole);
        // test configure client
        {
            try (Keycloak realmClient = AdminClientUtil.createAdminClient(suiteContext.isAdapterCompatTesting(), TEST, "clientConfigurer", "password", ADMIN_CLI_CLIENT_ID, null)) {
                client.setAdminUrl("http://nowhere");
                realmClient.realm(TEST).clients().get(client.getId()).update(client);
                client.setFullScopeAllowed(true);
                try {
                    realmClient.realm(TEST).clients().get(client.getId()).update(client);
                    Assert.fail("should fail with forbidden exception");
                } catch (ClientErrorException e) {
                    Assert.assertEquals(403, e.getResponse().getStatus());
                }
                client.setFullScopeAllowed(false);
                realmClient.realm(TEST).clients().get(client.getId()).update(client);
                try {
                    realmClient.realm(TEST).clients().get(client.getId()).addDefaultClientScope(scope.getId());
                    Assert.fail("should fail with forbidden exception");
                } catch (ClientErrorException e) {
                    Assert.assertEquals(403, e.getResponse().getStatus());
                }
                try {
                    realmClient.realm(TEST).clients().get(client.getId()).getScopeMappings().realmLevel().add(realmRoleSet);
                    Assert.fail("should fail with forbidden exception");
                } catch (ClientErrorException e) {
                    Assert.assertEquals(403, e.getResponse().getStatus());
                }
            }
        }
        // test illegal impersonation
        if (!(ImpersonationDisabledTest.IMPERSONATION_DISABLED)) {
            Keycloak realmClient = AdminClientUtil.createAdminClient(suiteContext.isAdapterCompatTesting(), TEST, "nomap-admin", "password", ADMIN_CLI_CLIENT_ID, null);
            try {
                realmClient.realm(TEST).users().get(user1.getId()).impersonate();
                realmClient.close();// just in case of cookie settings

                realmClient = AdminClientUtil.createAdminClient(suiteContext.isAdapterCompatTesting(), TEST, "nomap-admin", "password", ADMIN_CLI_CLIENT_ID, null);
                try {
                    realmClient.realm(TEST).users().get(anotherAdmin.getId()).impersonate();
                    Assert.fail("should fail with forbidden exception");
                } catch (ClientErrorException e) {
                    Assert.assertEquals(403, e.getResponse().getStatus());
                }
            } finally {
                realmClient.close();
            }
        }
        {
            try (Keycloak realmClient = AdminClientUtil.createAdminClient(suiteContext.isAdapterCompatTesting(), TEST, "authorized", "password", ADMIN_CLI_CLIENT_ID, null)) {
                realmClient.realm(TEST).users().get(user1.getId()).roles().realmLevel().add(realmRoleSet);
                List<RoleRepresentation> roles = adminClient.realm(TEST).users().get(user1.getId()).roles().realmLevel().listAll();
                Assert.assertTrue(roles.stream().anyMatch(( r) -> {
                    return r.getName().equals("realm-role");
                }));
                realmClient.realm(TEST).users().get(user1.getId()).roles().realmLevel().remove(realmRoleSet);
                roles = adminClient.realm(TEST).users().get(user1.getId()).roles().realmLevel().listAll();
                Assert.assertTrue(roles.stream().noneMatch(( r) -> {
                    return r.getName().equals("realm-role");
                }));
                realmClient.realm(TEST).users().get(user1.getId()).roles().clientLevel(client.getId()).add(clientRoleSet);
                roles = adminClient.realm(TEST).users().get(user1.getId()).roles().clientLevel(client.getId()).listAll();
                Assert.assertTrue(roles.stream().anyMatch(( r) -> {
                    return r.getName().equals("client-role");
                }));
                realmClient.realm(TEST).users().get(user1.getId()).roles().clientLevel(client.getId()).remove(clientRoleSet);
                roles = adminClient.realm(TEST).users().get(user1.getId()).roles().clientLevel(client.getId()).listAll();
                Assert.assertTrue(roles.stream().noneMatch(( r) -> {
                    return r.getName().equals("client-role");
                }));
            }
        }
        {
            try (Keycloak realmClient = AdminClientUtil.createAdminClient(suiteContext.isAdapterCompatTesting(), TEST, "authorizedComposite", "password", ADMIN_CLI_CLIENT_ID, null)) {
                realmClient.realm(TEST).users().get(user1.getId()).roles().realmLevel().add(realmRoleSet);
                List<RoleRepresentation> roles = adminClient.realm(TEST).users().get(user1.getId()).roles().realmLevel().listAll();
                Assert.assertTrue(roles.stream().anyMatch(( r) -> {
                    return r.getName().equals("realm-role");
                }));
                realmClient.realm(TEST).users().get(user1.getId()).roles().realmLevel().remove(realmRoleSet);
                roles = adminClient.realm(TEST).users().get(user1.getId()).roles().realmLevel().listAll();
                Assert.assertTrue(roles.stream().noneMatch(( r) -> {
                    return r.getName().equals("realm-role");
                }));
                realmClient.realm(TEST).users().get(user1.getId()).roles().clientLevel(client.getId()).add(clientRoleSet);
                roles = adminClient.realm(TEST).users().get(user1.getId()).roles().clientLevel(client.getId()).listAll();
                Assert.assertTrue(roles.stream().anyMatch(( r) -> {
                    return r.getName().equals("client-role");
                }));
                realmClient.realm(TEST).users().get(user1.getId()).roles().clientLevel(client.getId()).remove(clientRoleSet);
                roles = adminClient.realm(TEST).users().get(user1.getId()).roles().clientLevel(client.getId()).listAll();
                Assert.assertTrue(roles.stream().noneMatch(( r) -> {
                    return r.getName().equals("client-role");
                }));
            }
        }
        {
            try (Keycloak realmClient = AdminClientUtil.createAdminClient(suiteContext.isAdapterCompatTesting(), TEST, "unauthorized", "password", ADMIN_CLI_CLIENT_ID, null)) {
                realmClient.realm(TEST).users().get(user1.getId()).roles().realmLevel().add(realmRoleSet);
                Assert.fail("should fail with forbidden exception");
            } catch (ClientErrorException e) {
                Assert.assertEquals(403, e.getResponse().getStatus());
            }
        }
        {
            try (Keycloak realmClient = AdminClientUtil.createAdminClient(suiteContext.isAdapterCompatTesting(), TEST, "unauthorizedMapper", "password", ADMIN_CLI_CLIENT_ID, null)) {
                realmClient.realm(TEST).users().get(user1.getId()).roles().realmLevel().add(realmRoleSet);
                Assert.fail("should fail with forbidden exception");
            } catch (ClientErrorException e) {
                Assert.assertEquals(403, e.getResponse().getStatus());
            }
        }
        {
            try (Keycloak realmClient = AdminClientUtil.createAdminClient(suiteContext.isAdapterCompatTesting(), TEST, "groupManager", "password", ADMIN_CLI_CLIENT_ID, null)) {
                realmClient.realm(TEST).users().get(groupMember.getId()).roles().clientLevel(client.getId()).add(clientRoleSet);
                List<RoleRepresentation> roles = realmClient.realm(TEST).users().get(groupMember.getId()).roles().clientLevel(client.getId()).listAll();
                Assert.assertTrue(roles.stream().anyMatch(( r) -> {
                    return r.getName().equals("client-role");
                }));
                realmClient.realm(TEST).users().get(groupMember.getId()).roles().clientLevel(client.getId()).remove(clientRoleSet);
                roles = realmClient.realm(TEST).users().get(groupMember.getId()).roles().realmLevel().listAvailable();
                Assert.assertEquals(1, roles.size());
                realmClient.realm(TEST).users().get(groupMember.getId()).roles().realmLevel().add(realmRoleSet);
                realmClient.realm(TEST).users().get(groupMember.getId()).roles().realmLevel().remove(realmRoleSet);
                try {
                    realmClient.realm(TEST).users().get(groupMember.getId()).roles().realmLevel().add(realmRole2Set);
                    Assert.fail("should fail with forbidden exception");
                } catch (ClientErrorException e) {
                    Assert.assertEquals(403, e.getResponse().getStatus());
                }
                try {
                    realmClient.realm(TEST).users().get(user1.getId()).roles().realmLevel().add(realmRoleSet);
                    Assert.fail("should fail with forbidden exception");
                } catch (ClientErrorException e) {
                    Assert.assertEquals(403, e.getResponse().getStatus());
                }
            }
        }
        // test client.mapRoles
        {
            try (Keycloak realmClient = AdminClientUtil.createAdminClient(suiteContext.isAdapterCompatTesting(), TEST, "clientMapper", "password", ADMIN_CLI_CLIENT_ID, null)) {
                List<RoleRepresentation> roles = realmClient.realm(TEST).users().get(user1.getId()).roles().clientLevel(client.getId()).listAll();
                Assert.assertTrue(roles.isEmpty());
                realmClient.realm(TEST).users().get(user1.getId()).roles().clientLevel(client.getId()).add(clientRoleSet);
                roles = realmClient.realm(TEST).users().get(user1.getId()).roles().clientLevel(client.getId()).listAll();
                Assert.assertTrue(roles.stream().anyMatch(( r) -> {
                    return r.getName().equals("client-role");
                }));
                roles = realmClient.realm(TEST).users().get(user1.getId()).roles().realmLevel().listAvailable();
                Assert.assertTrue(roles.isEmpty());
                try {
                    realmClient.realm(TEST).users().get(user1.getId()).roles().realmLevel().add(realmRoleSet);
                    Assert.fail("should fail with forbidden exception");
                } catch (ClientErrorException e) {
                    Assert.assertEquals(403, e.getResponse().getStatus());
                }
            }
        }
        // KEYCLOAK-5878
        {
            try (Keycloak realmClient = AdminClientUtil.createAdminClient(suiteContext.isAdapterCompatTesting(), TEST, "groupViewer", "password", ADMIN_CLI_CLIENT_ID, null)) {
                // Should only return the list of users that belong to "top" group
                List<UserRepresentation> queryUsers = realmClient.realm(TEST).users().list();
                Assert.assertEquals(queryUsers.size(), 1);
                Assert.assertEquals("groupmember", queryUsers.get(0).getUsername());
                for (UserRepresentation user : queryUsers) {
                    System.out.println(user.getUsername());
                }
            }
        }
    }

    @Test
    public void testMasterRealm() throws Exception {
        // test that master realm can still perform operations when policies are in place
        // 
        testingClient.server().run(FineGrainAdminUnitTest::setupPolices);
        testingClient.server().run(FineGrainAdminUnitTest::setupUsers);
        UserRepresentation user1 = adminClient.realm(TEST).users().search("user1").get(0);
        RoleRepresentation realmRole = adminClient.realm(TEST).roles().get("realm-role").toRepresentation();
        List<RoleRepresentation> realmRoleSet = new LinkedList<>();
        realmRoleSet.add(realmRole);
        RoleRepresentation realmRole2 = adminClient.realm(TEST).roles().get("realm-role2").toRepresentation();
        List<RoleRepresentation> realmRole2Set = new LinkedList<>();
        realmRole2Set.add(realmRole);
        ClientRepresentation client = adminClient.realm(TEST).clients().findByClientId(FineGrainAdminUnitTest.CLIENT_NAME).get(0);
        RoleRepresentation clientRole = adminClient.realm(TEST).clients().get(client.getId()).roles().get("client-role").toRepresentation();
        List<RoleRepresentation> clientRoleSet = new LinkedList<>();
        clientRoleSet.add(clientRole);
        {
            try (Keycloak realmClient = AdminClientUtil.createAdminClient(suiteContext.isAdapterCompatTesting())) {
                realmClient.realm(TEST).users().get(user1.getId()).roles().realmLevel().add(realmRoleSet);
                List<RoleRepresentation> roles = adminClient.realm(TEST).users().get(user1.getId()).roles().realmLevel().listAll();
                Assert.assertTrue(roles.stream().anyMatch(( r) -> {
                    return r.getName().equals("realm-role");
                }));
                realmClient.realm(TEST).users().get(user1.getId()).roles().realmLevel().remove(realmRoleSet);
                roles = adminClient.realm(TEST).users().get(user1.getId()).roles().realmLevel().listAll();
                Assert.assertTrue(roles.stream().noneMatch(( r) -> {
                    return r.getName().equals("realm-role");
                }));
                realmClient.realm(TEST).users().get(user1.getId()).roles().clientLevel(client.getId()).add(clientRoleSet);
                roles = adminClient.realm(TEST).users().get(user1.getId()).roles().clientLevel(client.getId()).listAll();
                Assert.assertTrue(roles.stream().anyMatch(( r) -> {
                    return r.getName().equals("client-role");
                }));
                realmClient.realm(TEST).users().get(user1.getId()).roles().clientLevel(client.getId()).remove(clientRoleSet);
                roles = adminClient.realm(TEST).users().get(user1.getId()).roles().clientLevel(client.getId()).listAll();
                Assert.assertTrue(roles.stream().noneMatch(( r) -> {
                    return r.getName().equals("client-role");
                }));
            }
        }
    }

    // KEYCLOAK-5152
    @Test
    public void testMasterRealmWithComposites() throws Exception {
        RoleRepresentation composite = new RoleRepresentation();
        composite.setName("composite");
        composite.setComposite(true);
        adminClient.realm(TEST).roles().create(composite);
        composite = adminClient.realm(TEST).roles().get("composite").toRepresentation();
        ClientRepresentation client = adminClient.realm(TEST).clients().findByClientId(REALM_MANAGEMENT_CLIENT_ID).get(0);
        RoleRepresentation createClient = adminClient.realm(TEST).clients().get(client.getId()).roles().get(CREATE_CLIENT).toRepresentation();
        RoleRepresentation queryRealms = adminClient.realm(TEST).clients().get(client.getId()).roles().get(QUERY_REALMS).toRepresentation();
        List<RoleRepresentation> composites = new LinkedList<>();
        composites.add(createClient);
        composites.add(queryRealms);
        adminClient.realm(TEST).rolesById().addComposites(composite.getId(), composites);
    }

    // KEYCLOAK-5152
    @Test
    public void testRealmWithComposites() throws Exception {
        testingClient.server().run(FineGrainAdminUnitTest::setup5152);
        try (Keycloak realmClient = AdminClientUtil.createAdminClient(suiteContext.isAdapterCompatTesting(), TEST, "realm-admin", "password", ADMIN_CLI_CLIENT_ID, null)) {
            RoleRepresentation composite = new RoleRepresentation();
            composite.setName("composite");
            composite.setComposite(true);
            realmClient.realm(TEST).roles().create(composite);
            composite = adminClient.realm(TEST).roles().get("composite").toRepresentation();
            ClientRepresentation client = adminClient.realm(TEST).clients().findByClientId(REALM_MANAGEMENT_CLIENT_ID).get(0);
            RoleRepresentation viewUsers = adminClient.realm(TEST).clients().get(client.getId()).roles().get(CREATE_CLIENT).toRepresentation();
            List<RoleRepresentation> composites = new LinkedList<>();
            composites.add(viewUsers);
            realmClient.realm(TEST).rolesById().addComposites(composite.getId(), composites);
        }
    }

    @Test
    public void testRemoveCleanup() throws Exception {
        testingClient.server().run(FineGrainAdminUnitTest::setupDeleteTest);
        testingClient.server().run(FineGrainAdminUnitTest::invokeDelete);
    }

    // KEYCLOAK-5211
    @Test
    public void testCreateRealmCreateClient() throws Exception {
        ClientRepresentation rep = new ClientRepresentation();
        rep.setName("fullScopedClient");
        rep.setClientId("fullScopedClient");
        rep.setFullScopeAllowed(true);
        rep.setSecret("618268aa-51e6-4e64-93c4-3c0bc65b8171");
        rep.setProtocol("openid-connect");
        rep.setPublicClient(false);
        rep.setEnabled(true);
        adminClient.realm("master").clients().create(rep);
        Keycloak realmClient = AdminClientUtil.createAdminClient(suiteContext.isAdapterCompatTesting(), "master", "admin", "admin", "fullScopedClient", "618268aa-51e6-4e64-93c4-3c0bc65b8171");
        try {
            RealmRepresentation newRealm = new RealmRepresentation();
            newRealm.setRealm("anotherRealm");
            newRealm.setId("anotherRealm");
            newRealm.setEnabled(true);
            realmClient.realms().create(newRealm);
            ClientRepresentation newClient = new ClientRepresentation();
            newClient.setName("newClient");
            newClient.setClientId("newClient");
            newClient.setFullScopeAllowed(true);
            newClient.setSecret("secret");
            newClient.setProtocol("openid-connect");
            newClient.setPublicClient(false);
            newClient.setEnabled(true);
            Response response = realmClient.realm("anotherRealm").clients().create(newClient);
            Assert.assertEquals(403, response.getStatus());
            realmClient.close();
            realmClient = AdminClientUtil.createAdminClient(suiteContext.isAdapterCompatTesting(), "master", "admin", "admin", "fullScopedClient", "618268aa-51e6-4e64-93c4-3c0bc65b8171");
            response = realmClient.realm("anotherRealm").clients().create(newClient);
            Assert.assertEquals(201, response.getStatus());
        } finally {
            adminClient.realm("anotherRealm").remove();
            realmClient.close();
        }
    }

    // KEYCLOAK-5211
    @Test
    public void testCreateRealmCreateClientWithMaster() throws Exception {
        ClientRepresentation rep = new ClientRepresentation();
        rep.setName("fullScopedClient");
        rep.setClientId("fullScopedClient");
        rep.setFullScopeAllowed(true);
        rep.setSecret("618268aa-51e6-4e64-93c4-3c0bc65b8171");
        rep.setProtocol("openid-connect");
        rep.setPublicClient(false);
        rep.setEnabled(true);
        adminClient.realm("master").clients().create(rep);
        RealmRepresentation newRealm = new RealmRepresentation();
        newRealm.setRealm("anotherRealm");
        newRealm.setId("anotherRealm");
        newRealm.setEnabled(true);
        adminClient.realms().create(newRealm);
        try {
            ClientRepresentation newClient = new ClientRepresentation();
            newClient.setName("newClient");
            newClient.setClientId("newClient");
            newClient.setFullScopeAllowed(true);
            newClient.setSecret("secret");
            newClient.setProtocol("openid-connect");
            newClient.setPublicClient(false);
            newClient.setEnabled(true);
            Response response = adminClient.realm("anotherRealm").clients().create(newClient);
            Assert.assertEquals(201, response.getStatus());
        } finally {
            adminClient.realm("anotherRealm").remove();
        }
    }

    /**
     * KEYCLOAK-7406
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testWithTokenExchange() throws Exception {
        ProfileAssume.assumeFeatureEnabled(TOKEN_EXCHANGE);
        testingClient.server().run(( session) -> {
            RealmModel realm = session.realms().getRealmByName("master");
            ClientModel client = session.realms().getClientByClientId("kcinit", realm);
            if (client != null) {
                return;
            }
            ClientModel kcinit = realm.addClient("kcinit");
            kcinit.setEnabled(true);
            kcinit.addRedirectUri("http://localhost:*");
            kcinit.setPublicClient(false);
            kcinit.setSecret("password");
            kcinit.setDirectAccessGrantsEnabled(true);
            // permission for client to client exchange to "target" client
            ClientModel adminCli = realm.getClientByClientId(ConfigUtil.DEFAULT_CLIENT);
            AdminPermissionManagement management = AdminPermissions.management(session, realm);
            management.clients().setPermissionsEnabled(adminCli, true);
            ClientPolicyRepresentation clientRep = new ClientPolicyRepresentation();
            clientRep.setName("to");
            clientRep.addClient(kcinit.getId());
            ResourceServer server = management.realmResourceServer();
            Policy clientPolicy = management.authz().getStoreFactory().getPolicyStore().create(clientRep, server);
            management.clients().exchangeToPermission(adminCli).addAssociatedPolicy(clientPolicy);
        });
        oauth.realm("master");
        oauth.clientId("kcinit");
        String token = oauth.doGrantAccessTokenRequest("password", "admin", "admin").getAccessToken();
        Assert.assertNotNull(token);
        String exchanged = oauth.doTokenExchange("master", token, "admin-cli", "kcinit", "password").getAccessToken();
        Assert.assertNotNull(exchanged);
        try (Keycloak client = Keycloak.getInstance(((AuthServerTestEnricher.getAuthServerContextRoot()) + "/auth"), MASTER, ADMIN_CLI_CLIENT_ID, exchanged, TLSUtils.initializeTLS())) {
            Assert.assertNotNull(client.realm("master").roles().get("offline_access"));
        }
    }

    @Test
    public void testUserPagination() {
        testingClient.server().run(( session) -> {
            RealmModel realm = session.realms().getRealmByName("test");
            session.getContext().setRealm(realm);
            GroupModel customerAGroup = session.realms().createGroup(realm, "Customer A");
            UserModel customerAManager = session.users().addUser(realm, "customer-a-manager");
            session.userCredentialManager().updateCredential(realm, customerAManager, UserCredentialModel.password("password"));
            customerAManager.joinGroup(customerAGroup);
            ClientModel realmAdminClient = realm.getClientByClientId(Constants.REALM_MANAGEMENT_CLIENT_ID);
            customerAManager.grantRole(realmAdminClient.getRole(AdminRoles.QUERY_USERS));
            customerAManager.setEnabled(true);
            UserModel regularAdminUser = session.users().addUser(realm, "regular-admin-user");
            session.userCredentialManager().updateCredential(realm, regularAdminUser, UserCredentialModel.password("password"));
            regularAdminUser.grantRole(realmAdminClient.getRole(AdminRoles.VIEW_USERS));
            regularAdminUser.setEnabled(true);
            AdminPermissionManagement management = AdminPermissions.management(session, realm);
            GroupPermissionManagement groupPermission = management.groups();
            groupPermission.setPermissionsEnabled(customerAGroup, true);
            UserPolicyRepresentation userPolicyRepresentation = new UserPolicyRepresentation();
            userPolicyRepresentation.setName(("Only " + (customerAManager.getUsername())));
            userPolicyRepresentation.addUser(customerAManager.getId());
            Policy policy = groupPermission.viewMembersPermission(customerAGroup);
            AuthorizationProvider provider = session.getProvider(.class);
            Policy userPolicy = provider.getStoreFactory().getPolicyStore().create(userPolicyRepresentation, management.realmResourceServer());
            policy.addAssociatedPolicy(RepresentationToModel.toModel(userPolicyRepresentation, provider, userPolicy));
            for (int i = 0; i < 20; i++) {
                UserModel userModel = session.users().addUser(realm, ("a" + i));
                userModel.setFirstName("test");
            }
            for (int i = 20; i < 40; i++) {
                UserModel userModel = session.users().addUser(realm, ("b" + i));
                userModel.setFirstName("test");
                userModel.joinGroup(customerAGroup);
            }
        });
        try (Keycloak client = Keycloak.getInstance(((AuthServerTestEnricher.getAuthServerContextRoot()) + "/auth"), "test", "customer-a-manager", "password", ADMIN_CLI_CLIENT_ID, TLSUtils.initializeTLS())) {
            List<UserRepresentation> result = client.realm("test").users().search(null, "test", null, null, (-1), 20);
            Assert.assertEquals(20, result.size());
            Assert.assertThat(result, Matchers.everyItem(Matchers.hasProperty("username", Matchers.startsWith("b"))));
            result = client.realm("test").users().search(null, "test", null, null, 20, 40);
            Assert.assertEquals(0, result.size());
        }
        try (Keycloak client = Keycloak.getInstance(((AuthServerTestEnricher.getAuthServerContextRoot()) + "/auth"), "test", "regular-admin-user", "password", ADMIN_CLI_CLIENT_ID, TLSUtils.initializeTLS())) {
            List<UserRepresentation> result = client.realm("test").users().search(null, "test", null, null, (-1), 20);
            Assert.assertEquals(20, result.size());
            Assert.assertThat(result, Matchers.everyItem(Matchers.hasProperty("username", Matchers.startsWith("a"))));
            client.realm("test").users().search(null, null, null, null, (-1), (-1));
            Assert.assertEquals(20, result.size());
            Assert.assertThat(result, Matchers.everyItem(Matchers.hasProperty("username", Matchers.startsWith("a"))));
        }
        try (Keycloak client = Keycloak.getInstance(((AuthServerTestEnricher.getAuthServerContextRoot()) + "/auth"), "test", "customer-a-manager", "password", ADMIN_CLI_CLIENT_ID, TLSUtils.initializeTLS())) {
            List<UserRepresentation> result = client.realm("test").users().search(null, null, null, null, (-1), 20);
            Assert.assertEquals(20, result.size());
            Assert.assertThat(result, Matchers.everyItem(Matchers.hasProperty("username", Matchers.startsWith("b"))));
            result = client.realm("test").users().search("a", (-1), 20, false);
            Assert.assertEquals(0, result.size());
        }
    }
}

