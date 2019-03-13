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
package org.keycloak.testsuite.federation.ldap;


import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.ClientModel;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleModel;
import org.keycloak.models.UserModel;
import org.keycloak.representations.idm.ComponentRepresentation;
import org.keycloak.services.managers.UserStorageSyncManager;
import org.keycloak.storage.UserStorageProviderModel;
import org.keycloak.storage.ldap.LDAPStorageProvider;
import org.keycloak.storage.ldap.idm.model.LDAPObject;
import org.keycloak.storage.ldap.mappers.membership.role.RoleLDAPStorageMapper;
import org.keycloak.storage.user.SynchronizationResult;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.util.LDAPRule;
import org.keycloak.testsuite.util.LDAPTestUtils;


/**
 *
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LDAPRoleMappingsTest extends AbstractLDAPTest {
    @ClassRule
    public static LDAPRule ldapRule = new LDAPRule();

    @Test
    public void test01_ldapOnlyRoleMappings() {
        testingClient.server().run(( session) -> {
            LDAPTestContext ctx = LDAPTestContext.init(session);
            RealmModel appRealm = ctx.getRealm();
            LDAPTestUtils.addOrUpdateRoleLDAPMappers(appRealm, ctx.getLdapModel(), LDAPGroupMapperMode.LDAP_ONLY);
            UserModel john = session.users().getUserByUsername("johnkeycloak", appRealm);
            UserModel mary = session.users().getUserByUsername("marykeycloak", appRealm);
            // 1 - Grant some roles in LDAP
            // This role should already exists as it was imported from LDAP
            RoleModel realmRole1 = appRealm.getRole("realmRole1");
            john.grantRole(realmRole1);
            // This role should already exists as it was imported from LDAP
            RoleModel realmRole2 = appRealm.getRole("realmRole2");
            mary.grantRole(realmRole2);
            // This role may already exists from previous test (was imported from LDAP), but may not
            RoleModel realmRole3 = appRealm.getRole("realmRole3");
            if (realmRole3 == null) {
                realmRole3 = appRealm.addRole("realmRole3");
            }
            john.grantRole(realmRole3);
            mary.grantRole(realmRole3);
            ClientModel accountApp = appRealm.getClientByClientId(Constants.ACCOUNT_MANAGEMENT_CLIENT_ID);
            ClientModel financeApp = appRealm.getClientByClientId("finance");
            RoleModel manageAccountRole = accountApp.getRole(AccountRoles.MANAGE_ACCOUNT);
            RoleModel financeRole1 = financeApp.getRole("financeRole1");
            john.grantRole(financeRole1);
            // 2 - Check that role mappings are not in local Keycloak DB (They are in LDAP).
            UserModel johnDb = session.userLocalStorage().getUserByUsername("johnkeycloak", appRealm);
            Set<RoleModel> johnDbRoles = johnDb.getRoleMappings();
            Assert.assertFalse(johnDbRoles.contains(realmRole1));
            Assert.assertFalse(johnDbRoles.contains(realmRole2));
            Assert.assertFalse(johnDbRoles.contains(realmRole3));
            Assert.assertFalse(johnDbRoles.contains(financeRole1));
            Assert.assertTrue(johnDbRoles.contains(manageAccountRole));
            // 3 - Check that role mappings are in LDAP and hence available through federation
            Set<RoleModel> johnRoles = john.getRoleMappings();
            Assert.assertTrue(johnRoles.contains(realmRole1));
            Assert.assertFalse(johnRoles.contains(realmRole2));
            Assert.assertTrue(johnRoles.contains(realmRole3));
            Assert.assertTrue(johnRoles.contains(financeRole1));
            Assert.assertTrue(johnRoles.contains(manageAccountRole));
            Set<RoleModel> johnRealmRoles = john.getRealmRoleMappings();
            Assert.assertEquals(2, johnRealmRoles.size());
            Assert.assertTrue(johnRealmRoles.contains(realmRole1));
            Assert.assertTrue(johnRealmRoles.contains(realmRole3));
            // account roles are not mapped in LDAP. Those are in Keycloak DB
            Set<RoleModel> johnAccountRoles = john.getClientRoleMappings(accountApp);
            Assert.assertTrue(johnAccountRoles.contains(manageAccountRole));
            Set<RoleModel> johnFinanceRoles = john.getClientRoleMappings(financeApp);
            Assert.assertEquals(1, johnFinanceRoles.size());
            Assert.assertTrue(johnFinanceRoles.contains(financeRole1));
            // 4 - Delete some role mappings and check they are deleted
            john.deleteRoleMapping(realmRole3);
            john.deleteRoleMapping(realmRole1);
            john.deleteRoleMapping(financeRole1);
            john.deleteRoleMapping(manageAccountRole);
            johnRoles = john.getRoleMappings();
            Assert.assertFalse(johnRoles.contains(realmRole1));
            Assert.assertFalse(johnRoles.contains(realmRole2));
            Assert.assertFalse(johnRoles.contains(realmRole3));
            Assert.assertFalse(johnRoles.contains(financeRole1));
            Assert.assertFalse(johnRoles.contains(manageAccountRole));
            // Cleanup
            mary.deleteRoleMapping(realmRole2);
            mary.deleteRoleMapping(realmRole3);
            john.grantRole(manageAccountRole);
        });
    }

    @Test
    public void test02_readOnlyRoleMappings() {
        testingClient.server().run(( session) -> {
            LDAPTestContext ctx = LDAPTestContext.init(session);
            RealmModel appRealm = ctx.getRealm();
            LDAPTestUtils.addOrUpdateRoleLDAPMappers(appRealm, ctx.getLdapModel(), LDAPGroupMapperMode.READ_ONLY);
            UserModel mary = session.users().getUserByUsername("marykeycloak", appRealm);
            RoleModel realmRole1 = appRealm.getRole("realmRole1");
            RoleModel realmRole2 = appRealm.getRole("realmRole2");
            RoleModel realmRole3 = appRealm.getRole("realmRole3");
            if (realmRole3 == null) {
                realmRole3 = appRealm.addRole("realmRole3");
            }
            // Add some role mappings directly into LDAP
            ComponentModel roleMapperModel = LDAPTestUtils.getSubcomponentByName(appRealm, ctx.getLdapModel(), "realmRolesMapper");
            LDAPStorageProvider ldapProvider = LDAPTestUtils.getLdapProvider(session, ctx.getLdapModel());
            RoleLDAPStorageMapper roleMapper = LDAPTestUtils.getRoleMapper(roleMapperModel, ldapProvider, appRealm);
            LDAPObject maryLdap = ldapProvider.loadLDAPUserByUsername(appRealm, "marykeycloak");
            roleMapper.addRoleMappingInLDAP("realmRole1", maryLdap);
            roleMapper.addRoleMappingInLDAP("realmRole2", maryLdap);
            // Add some role to model
            mary.grantRole(realmRole3);
            // Assert that mary has both LDAP and DB mapped roles
            Set<RoleModel> maryRoles = mary.getRealmRoleMappings();
            Assert.assertTrue(maryRoles.contains(realmRole1));
            Assert.assertTrue(maryRoles.contains(realmRole2));
            Assert.assertTrue(maryRoles.contains(realmRole3));
            // Assert that access through DB will have just DB mapped role
            UserModel maryDB = session.userLocalStorage().getUserByUsername("marykeycloak", appRealm);
            Set<RoleModel> maryDBRoles = maryDB.getRealmRoleMappings();
            Assert.assertFalse(maryDBRoles.contains(realmRole1));
            Assert.assertFalse(maryDBRoles.contains(realmRole2));
            Assert.assertTrue(maryDBRoles.contains(realmRole3));
            mary.deleteRoleMapping(realmRole3);
            try {
                mary.deleteRoleMapping(realmRole1);
                Assert.fail("It wasn't expected to successfully delete LDAP role mappings in READ_ONLY mode");
            } catch ( expected) {
            }
            // Delete role mappings directly in LDAP
            deleteRoleMappingsInLDAP(roleMapper, maryLdap, "realmRole1");
            deleteRoleMappingsInLDAP(roleMapper, maryLdap, "realmRole2");
        });
        testingClient.server().run(( session) -> {
            LDAPTestContext ctx = LDAPTestContext.init(session);
            RealmModel appRealm = ctx.getRealm();
            UserModel mary = session.users().getUserByUsername("marykeycloak", appRealm);
            // Assert role mappings is not available
            Set<RoleModel> maryRoles = mary.getRealmRoleMappings();
            Assert.assertFalse(maryRoles.contains(appRealm.getRole("realmRole1")));
            Assert.assertFalse(maryRoles.contains(appRealm.getRole("realmRole2")));
            Assert.assertFalse(maryRoles.contains(appRealm.getRole("realmRole3")));
        });
    }

    @Test
    public void test03_importRoleMappings() {
        testingClient.server().run(( session) -> {
            LDAPTestContext ctx = LDAPTestContext.init(session);
            RealmModel appRealm = ctx.getRealm();
            LDAPTestUtils.addOrUpdateRoleLDAPMappers(appRealm, ctx.getLdapModel(), LDAPGroupMapperMode.IMPORT);
            // Add some role mappings directly in LDAP
            ComponentModel roleMapperModel = LDAPTestUtils.getSubcomponentByName(appRealm, ctx.getLdapModel(), "realmRolesMapper");
            LDAPStorageProvider ldapProvider = LDAPTestUtils.getLdapProvider(session, ctx.getLdapModel());
            RoleLDAPStorageMapper roleMapper = LDAPTestUtils.getRoleMapper(roleMapperModel, ldapProvider, appRealm);
            LDAPObject robLdap = ldapProvider.loadLDAPUserByUsername(appRealm, "robkeycloak");
            roleMapper.addRoleMappingInLDAP("realmRole1", robLdap);
            roleMapper.addRoleMappingInLDAP("realmRole2", robLdap);
            // Get user and check that he has requested roles from LDAP
            UserModel rob = session.users().getUserByUsername("robkeycloak", appRealm);
            RoleModel realmRole1 = appRealm.getRole("realmRole1");
            RoleModel realmRole2 = appRealm.getRole("realmRole2");
            RoleModel realmRole3 = appRealm.getRole("realmRole3");
            if (realmRole3 == null) {
                realmRole3 = appRealm.addRole("realmRole3");
            }
            Set<RoleModel> robRoles = rob.getRealmRoleMappings();
            Assert.assertTrue(robRoles.contains(realmRole1));
            Assert.assertTrue(robRoles.contains(realmRole2));
            Assert.assertFalse(robRoles.contains(realmRole3));
            // Add some role mappings in model and check that user has it
            rob.grantRole(realmRole3);
            robRoles = rob.getRealmRoleMappings();
            Assert.assertTrue(robRoles.contains(realmRole3));
            // Delete some role mappings in LDAP and check that it doesn't have any effect and user still has role
            deleteRoleMappingsInLDAP(roleMapper, robLdap, "realmRole1");
            deleteRoleMappingsInLDAP(roleMapper, robLdap, "realmRole2");
            robRoles = rob.getRealmRoleMappings();
            Assert.assertTrue(robRoles.contains(realmRole1));
            Assert.assertTrue(robRoles.contains(realmRole2));
            // Delete role mappings through model and verifies that user doesn't have them anymore
            rob.deleteRoleMapping(realmRole1);
            rob.deleteRoleMapping(realmRole2);
            rob.deleteRoleMapping(realmRole3);
            robRoles = rob.getRealmRoleMappings();
            Assert.assertFalse(robRoles.contains(realmRole1));
            Assert.assertFalse(robRoles.contains(realmRole2));
            Assert.assertFalse(robRoles.contains(realmRole3));
        });
    }

    /**
     * KEYCLOAK-5698
     */
    @Test
    public void test04_syncRoleMappings() {
        testingClient.server().run(( session) -> {
            LDAPTestContext ctx = LDAPTestContext.init(session);
            RealmModel appRealm = ctx.getRealm();
            LDAPStorageProvider ldapProvider = LDAPTestUtils.getLdapProvider(session, ctx.getLdapModel());
            LDAPObject john = LDAPTestUtils.addLDAPUser(ldapProvider, appRealm, "johnrolemapper", "John", "RoleMapper", "johnrolemapper@email.org", null, "1234");
            LDAPTestUtils.updateLDAPPassword(ldapProvider, john, "Password1");
            LDAPTestUtils.addOrUpdateRoleLDAPMappers(appRealm, ctx.getLdapModel(), LDAPGroupMapperMode.LDAP_ONLY);
            UserStorageSyncManager usersSyncManager = new UserStorageSyncManager();
            SynchronizationResult syncResult = usersSyncManager.syncChangedUsers(session.getKeycloakSessionFactory(), appRealm.getId(), new UserStorageProviderModel(ctx.getLdapModel()));
            syncResult.getAdded();
        });
        testingClient.server().run(( session) -> {
            LDAPTestContext ctx = LDAPTestContext.init(session);
            RealmModel appRealm = ctx.getRealm();
            // make sure user is cached.
            UserModel johnRoleMapper = session.users().getUserByUsername("johnrolemapper", appRealm);
            Assert.assertNotNull(johnRoleMapper);
            Assert.assertEquals(0, johnRoleMapper.getRealmRoleMappings().size());
        });
        testingClient.server().run(( session) -> {
            LDAPTestContext ctx = LDAPTestContext.init(session);
            RealmModel appRealm = ctx.getRealm();
            // Add some role mappings directly in LDAP
            LDAPStorageProvider ldapProvider = LDAPTestUtils.getLdapProvider(session, ctx.getLdapModel());
            ComponentModel roleMapperModel = LDAPTestUtils.getSubcomponentByName(appRealm, ctx.getLdapModel(), "realmRolesMapper");
            RoleLDAPStorageMapper roleMapper = LDAPTestUtils.getRoleMapper(roleMapperModel, ldapProvider, appRealm);
            LDAPObject johnLdap = ldapProvider.loadLDAPUserByUsername(appRealm, "johnrolemapper");
            roleMapper.addRoleMappingInLDAP("realmRole1", johnLdap);
            roleMapper.addRoleMappingInLDAP("realmRole2", johnLdap);
            // Get user and check that he has requested roles from LDAP
            UserModel johnRoleMapper = session.users().getUserByUsername("johnrolemapper", appRealm);
            RoleModel realmRole1 = appRealm.getRole("realmRole1");
            RoleModel realmRole2 = appRealm.getRole("realmRole2");
            Set<RoleModel> johnRoles = johnRoleMapper.getRealmRoleMappings();
            Assert.assertFalse(johnRoles.contains(realmRole1));
            Assert.assertFalse(johnRoles.contains(realmRole2));
        });
        testingClient.server().run(( session) -> {
            LDAPTestContext ctx = LDAPTestContext.init(session);
            RealmModel appRealm = ctx.getRealm();
            // Add some role mappings directly in LDAP
            LDAPStorageProvider ldapProvider = LDAPTestUtils.getLdapProvider(session, ctx.getLdapModel());
            ComponentModel roleMapperModel = LDAPTestUtils.getSubcomponentByName(appRealm, ctx.getLdapModel(), "realmRolesMapper");
            RoleLDAPStorageMapper roleMapper = LDAPTestUtils.getRoleMapper(roleMapperModel, ldapProvider, appRealm);
            LDAPObject johnLdap = ldapProvider.loadLDAPUserByUsername(appRealm, "johnrolemapper");
            roleMapper.addRoleMappingInLDAP("realmRole1", johnLdap);
            roleMapper.addRoleMappingInLDAP("realmRole2", johnLdap);
            UserStorageSyncManager usersSyncManager = new UserStorageSyncManager();
            SynchronizationResult syncResult = usersSyncManager.syncChangedUsers(session.getKeycloakSessionFactory(), appRealm.getId(), new UserStorageProviderModel(ctx.getLdapModel()));
        });
        testingClient.server().run(( session) -> {
            LDAPTestContext ctx = LDAPTestContext.init(session);
            RealmModel appRealm = ctx.getRealm();
            // Get user and check that he has requested roles from LDAP
            UserModel johnRoleMapper = session.users().getUserByUsername("johnrolemapper", appRealm);
            RoleModel realmRole1 = appRealm.getRole("realmRole1");
            RoleModel realmRole2 = appRealm.getRole("realmRole2");
            Set<RoleModel> johnRoles = johnRoleMapper.getRealmRoleMappings();
            Assert.assertTrue(johnRoles.contains(realmRole1));
            Assert.assertTrue(johnRoles.contains(realmRole2));
        });
    }

    // KEYCLOAK-5848
    // Test GET_ROLES_FROM_USER_MEMBEROF_ATTRIBUTE with custom 'Member-Of LDAP Attribute'. As a workaround, we are testing this with custom attribute "street"
    // just because it's available on all the LDAP servers
    @Test
    public void test05_getRolesFromUserMemberOfStrategyTest() throws Exception {
        ComponentRepresentation realmRoleMapper = findMapperRepByName("realmRolesMapper");
        testingClient.server().run(( session) -> {
            LDAPTestContext ctx = LDAPTestContext.init(session);
            RealmModel appRealm = ctx.getRealm();
            // Create street attribute mapper
            LDAPTestUtils.addUserAttributeMapper(appRealm, ctx.getLdapModel(), "streetMapper", "street", LDAPConstants.STREET);
            // Find DN of "group1"
            ComponentModel mapperModel = LDAPTestUtils.getSubcomponentByName(appRealm, ctx.getLdapModel(), "realmRolesMapper");
            RoleLDAPStorageMapper roleMapper = LDAPTestUtils.getRoleMapper(mapperModel, ctx.getLdapProvider(), appRealm);
            LDAPObject ldapRole = roleMapper.loadLDAPRoleByName("realmRole1");
            String ldapRoleDN = ldapRole.getDn().toString();
            // Create new user in LDAP. Add him some "street" referencing existing LDAP Group
            LDAPObject carlos = LDAPTestUtils.addLDAPUser(ctx.getLdapProvider(), appRealm, "carloskeycloak", "Carlos", "Doel", "carlos.doel@email.org", ldapRoleDN, "1234");
            LDAPTestUtils.updateLDAPPassword(ctx.getLdapProvider(), carlos, "Password1");
            // Update group mapper
            LDAPTestUtils.updateGroupMapperConfigOptions(mapperModel, RoleMapperConfig.USER_ROLES_RETRIEVE_STRATEGY, RoleMapperConfig.GET_ROLES_FROM_USER_MEMBEROF_ATTRIBUTE, RoleMapperConfig.MEMBEROF_LDAP_ATTRIBUTE, LDAPConstants.STREET);
            appRealm.updateComponent(mapperModel);
        });
        ComponentRepresentation streetMapper = findMapperRepByName("streetMapper");
        testingClient.server().run(( session) -> {
            LDAPTestContext ctx = LDAPTestContext.init(session);
            RealmModel appRealm = ctx.getRealm();
            // Get user in Keycloak. Ensure that he is member of requested group
            UserModel carlos = session.users().getUserByUsername("carloskeycloak", appRealm);
            Set<RoleModel> carlosRoles = carlos.getRealmRoleMappings();
            RoleModel realmRole1 = appRealm.getRole("realmRole1");
            RoleModel realmRole2 = appRealm.getRole("realmRole2");
            Assert.assertTrue(carlosRoles.contains(realmRole1));
            Assert.assertFalse(carlosRoles.contains(realmRole2));
        });
        // Revert mappers
        testRealm().components().component(streetMapper.getId()).remove();
        testRealm().components().component(realmRoleMapper.getId()).remove();
        realmRoleMapper.setId(null);
        testRealm().components().add(realmRoleMapper);
    }

    @Test
    public void test06_newUserDefaultRolesImportModeTest() throws Exception {
        testingClient.server().run(( session) -> {
            LDAPTestContext ctx = LDAPTestContext.init(session);
            RealmModel appRealm = ctx.getRealm();
            // Set a default role on the realm
            appRealm.addDefaultRole("realmRole1");
            UserModel david = session.users().addUser(appRealm, "davidkeycloak");
            RoleModel defaultRole = appRealm.getRole("realmRole1");
            RoleModel realmRole2 = appRealm.getRole("realmRole2");
            Assert.assertNotNull(defaultRole);
            Assert.assertNotNull(realmRole2);
            Set<RoleModel> davidRoles = david.getRealmRoleMappings();
            Assert.assertTrue(davidRoles.contains(defaultRole));
            Assert.assertFalse(davidRoles.contains(realmRole2));
        });
    }
}

