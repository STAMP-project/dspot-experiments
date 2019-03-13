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


import GroupMapperConfig.LOAD_GROUPS_BY_MEMBER_ATTRIBUTE;
import GroupMapperConfig.USER_ROLES_RETRIEVE_STRATEGY;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.GroupModel;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.representations.idm.ComponentRepresentation;
import org.keycloak.storage.ldap.LDAPConfig;
import org.keycloak.storage.ldap.LDAPStorageProvider;
import org.keycloak.storage.ldap.LDAPUtils;
import org.keycloak.storage.ldap.idm.model.LDAPDn;
import org.keycloak.storage.ldap.idm.model.LDAPObject;
import org.keycloak.storage.ldap.mappers.membership.group.GroupLDAPStorageMapper;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.util.LDAPRule;
import org.keycloak.testsuite.util.LDAPTestUtils;


/**
 *
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LDAPGroupMapperTest extends AbstractLDAPTest {
    @ClassRule
    public static LDAPRule ldapRule = new LDAPRule();

    @Test
    public void test01_ldapOnlyGroupMappings() {
        test01_ldapOnlyGroupMappings(true);
    }

    @Test
    public void test02_readOnlyGroupMappings() {
        test02_readOnlyGroupMappings(true);
    }

    @Test
    public void test03_importGroupMappings() {
        testingClient.server().run(( session) -> {
            LDAPTestContext ctx = LDAPTestContext.init(session);
            RealmModel appRealm = ctx.getRealm();
            ComponentModel mapperModel = LDAPTestUtils.getSubcomponentByName(appRealm, ctx.getLdapModel(), "groupsMapper");
            LDAPTestUtils.updateGroupMapperConfigOptions(mapperModel, GroupMapperConfig.MODE, LDAPGroupMapperMode.IMPORT.toString());
            appRealm.updateComponent(mapperModel);
            // Add some group mappings directly in LDAP
            LDAPStorageProvider ldapProvider = LDAPTestUtils.getLdapProvider(session, ctx.getLdapModel());
            GroupLDAPStorageMapper groupMapper = LDAPTestUtils.getGroupMapper(mapperModel, ldapProvider, appRealm);
            GroupModel group1 = KeycloakModelUtils.findGroupByPath(appRealm, "/group1");
            GroupModel group11 = KeycloakModelUtils.findGroupByPath(appRealm, "/group1/group11");
            GroupModel group12 = KeycloakModelUtils.findGroupByPath(appRealm, "/group1/group12");
            LDAPObject robLdap = ldapProvider.loadLDAPUserByUsername(appRealm, "robkeycloak");
            groupMapper.addGroupMappingInLDAP(appRealm, group11, robLdap);
            groupMapper.addGroupMappingInLDAP(appRealm, group12, robLdap);
            // Get user and check that he has requested groups from LDAP
            UserModel rob = session.users().getUserByUsername("robkeycloak", appRealm);
            Set<GroupModel> robGroups = rob.getGroups();
            Assert.assertFalse(robGroups.contains(group1));
            Assert.assertTrue(robGroups.contains(group11));
            Assert.assertTrue(robGroups.contains(group12));
            Set<GroupModel> robGroupsWithGr = rob.getGroups("Gr", 0, 10);
            Assert.assertEquals(4, robGroupsWithGr.size());
            Set<GroupModel> robGroupsWithGr2 = rob.getGroups("Gr", 1, 10);
            Assert.assertEquals(3, robGroupsWithGr2.size());
            Set<GroupModel> robGroupsWithGr3 = rob.getGroups("Gr", 0, 1);
            Assert.assertEquals(1, robGroupsWithGr3.size());
            Set<GroupModel> robGroupsWith12 = rob.getGroups("12", 0, 10);
            Assert.assertEquals(2, robGroupsWith12.size());
            long dbGroupCount = rob.getGroupsCount();
            Assert.assertEquals(4, dbGroupCount);
            // Delete some group mappings in LDAP and check that it doesn't have any effect and user still has groups
            LDAPObject ldapGroup = groupMapper.loadLDAPGroupByName("group11");
            groupMapper.deleteGroupMappingInLDAP(robLdap, ldapGroup);
            ldapGroup = groupMapper.loadLDAPGroupByName("group12");
            groupMapper.deleteGroupMappingInLDAP(robLdap, ldapGroup);
            robGroups = rob.getGroups();
            Assert.assertTrue(robGroups.contains(group11));
            Assert.assertTrue(robGroups.contains(group12));
            // Delete group mappings through model and verifies that user doesn't have them anymore
            rob.leaveGroup(group11);
            rob.leaveGroup(group12);
            robGroups = rob.getGroups();
            Assert.assertEquals(2, robGroups.size());
        });
    }

    // KEYCLOAK-2682
    @Test
    public void test04_groupReferencingNonExistentMember() {
        testingClient.server().run(( session) -> {
            LDAPTestContext ctx = LDAPTestContext.init(session);
            RealmModel appRealm = ctx.getRealm();
            // Ignoring this test on ActiveDirectory as it's not allowed to have LDAP group referencing nonexistent member. KEYCLOAK-2682 was related to OpenLDAP TODO: Better solution than programmatic...
            LDAPConfig config = ctx.getLdapProvider().getLdapIdentityStore().getConfig();
            if (config.isActiveDirectory()) {
                return;
            }
            ComponentModel mapperModel = LDAPTestUtils.getSubcomponentByName(appRealm, ctx.getLdapModel(), "groupsMapper");
            LDAPTestUtils.updateGroupMapperConfigOptions(mapperModel, GroupMapperConfig.MODE, LDAPGroupMapperMode.LDAP_ONLY.toString());
            appRealm.updateComponent(mapperModel);
            String descriptionAttrName = getGroupDescriptionLDAPAttrName(ctx.getLdapProvider());
            // 1 - Add some group to LDAP for testing
            LDAPStorageProvider ldapProvider = LDAPTestUtils.getLdapProvider(session, ctx.getLdapModel());
            GroupLDAPStorageMapper groupMapper = LDAPTestUtils.getGroupMapper(mapperModel, ldapProvider, appRealm);
            LDAPObject group2 = LDAPTestUtils.createLDAPGroup(session, appRealm, ctx.getLdapModel(), "group2", descriptionAttrName, "group2 - description");
            // 2 - Add one existing user rob to LDAP group
            LDAPObject jamesLdap = ldapProvider.loadLDAPUserByUsername(appRealm, "jameskeycloak");
            LDAPUtils.addMember(ldapProvider, MembershipType.DN, LDAPConstants.MEMBER, "not-used", group2, jamesLdap, false);
            // 3 - Add non-existing user to LDAP group
            LDAPDn nonExistentDn = LDAPDn.fromString(ldapProvider.getLdapIdentityStore().getConfig().getUsersDn());
            nonExistentDn.addFirst(jamesLdap.getRdnAttributeName(), "nonexistent");
            LDAPObject nonExistentLdapUser = new LDAPObject();
            nonExistentLdapUser.setDn(nonExistentDn);
            LDAPUtils.addMember(ldapProvider, MembershipType.DN, LDAPConstants.MEMBER, "not-used", group2, nonExistentLdapUser, true);
            // 4 - Check group members. Just existing user rob should be present
            groupMapper.syncDataFromFederationProviderToKeycloak(appRealm);
            GroupModel kcGroup2 = KeycloakModelUtils.findGroupByPath(appRealm, "/group2");
            List<UserModel> groupUsers = session.users().getGroupMembers(appRealm, kcGroup2, 0, 5);
            Assert.assertEquals(1, groupUsers.size());
            UserModel rob = groupUsers.get(0);
            Assert.assertEquals("jameskeycloak", rob.getUsername());
        });
    }

    // KEYCLOAK-5848
    // Test GET_GROUPS_FROM_USER_MEMBEROF_ATTRIBUTE with custom 'Member-Of LDAP Attribute'. As a workaround, we are testing this with custom attribute "street"
    // just because it's available on all the LDAP servers
    @Test
    public void test05_getGroupsFromUserMemberOfStrategyTest() throws Exception {
        ComponentRepresentation groupMapperRep = findMapperRepByName("groupsMapper");
        testingClient.server().run(( session) -> {
            LDAPTestContext ctx = LDAPTestContext.init(session);
            RealmModel appRealm = ctx.getRealm();
            // Create street attribute mapper
            LDAPTestUtils.addUserAttributeMapper(appRealm, ctx.getLdapModel(), "streetMapper", "street", LDAPConstants.STREET);
            // Find DN of "group1"
            ComponentModel mapperModel = LDAPTestUtils.getSubcomponentByName(appRealm, ctx.getLdapModel(), "groupsMapper");
            GroupLDAPStorageMapper groupMapper = LDAPTestUtils.getGroupMapper(mapperModel, ctx.getLdapProvider(), appRealm);
            LDAPObject ldapGroup = groupMapper.loadLDAPGroupByName("group1");
            String ldapGroupDN = ldapGroup.getDn().toString();
            // Create new user in LDAP. Add him some "street" referencing existing LDAP Group
            LDAPObject carlos = LDAPTestUtils.addLDAPUser(ctx.getLdapProvider(), appRealm, "carloskeycloak", "Carlos", "Doel", "carlos.doel@email.org", ldapGroupDN, "1234");
            LDAPTestUtils.updateLDAPPassword(ctx.getLdapProvider(), carlos, "Password1");
            // Update group mapper
            LDAPTestUtils.updateGroupMapperConfigOptions(mapperModel, GroupMapperConfig.USER_ROLES_RETRIEVE_STRATEGY, GroupMapperConfig.GET_GROUPS_FROM_USER_MEMBEROF_ATTRIBUTE, GroupMapperConfig.MEMBEROF_LDAP_ATTRIBUTE, LDAPConstants.STREET);
            appRealm.updateComponent(mapperModel);
        });
        ComponentRepresentation streetMapperRep = findMapperRepByName("streetMapper");
        testingClient.server().run(( session) -> {
            LDAPTestContext ctx = LDAPTestContext.init(session);
            RealmModel appRealm = ctx.getRealm();
            // Get user in Keycloak. Ensure that he is member of requested group
            UserModel carlos = session.users().getUserByUsername("carloskeycloak", appRealm);
            Set<GroupModel> carlosGroups = carlos.getGroups();
            GroupModel group1 = KeycloakModelUtils.findGroupByPath(appRealm, "/group1");
            GroupModel group11 = KeycloakModelUtils.findGroupByPath(appRealm, "/group1/group11");
            GroupModel group12 = KeycloakModelUtils.findGroupByPath(appRealm, "/group1/group12");
            Assert.assertTrue(carlosGroups.contains(group1));
            Assert.assertFalse(carlosGroups.contains(group11));
            Assert.assertFalse(carlosGroups.contains(group12));
            Assert.assertEquals(1, carlosGroups.size());
        });
        // Revert mappers
        testRealm().components().component(streetMapperRep.getId()).remove();
        groupMapperRep.getConfig().putSingle(USER_ROLES_RETRIEVE_STRATEGY, LOAD_GROUPS_BY_MEMBER_ATTRIBUTE);
        testRealm().components().component(groupMapperRep.getId()).update(groupMapperRep);
    }

    // KEYCLOAK-5017
    @Test
    public void test06_addingUserToNewKeycloakGroup() throws Exception {
        // Add some groups to Keycloak
        testingClient.server().run(( session) -> {
            LDAPTestContext ctx = LDAPTestContext.init(session);
            RealmModel appRealm = ctx.getRealm();
            GroupModel group3 = appRealm.createGroup("group3");
            session.realms().addTopLevelGroup(appRealm, group3);
            GroupModel group31 = appRealm.createGroup("group31");
            group3.addChild(group31);
            GroupModel group32 = appRealm.createGroup("group32");
            group3.addChild(group32);
            GroupModel group4 = appRealm.createGroup("group4");
            session.realms().addTopLevelGroup(appRealm, group4);
            GroupModel group14 = appRealm.createGroup("group14");
            GroupModel group1 = KeycloakModelUtils.findGroupByPath(appRealm, "/group1");
            group1.addChild(group14);
        });
        // Add user to some newly created KC groups
        testingClient.server().run(( session) -> {
            LDAPTestContext ctx = LDAPTestContext.init(session);
            RealmModel appRealm = ctx.getRealm();
            UserModel john = session.users().getUserByUsername("johnkeycloak", appRealm);
            GroupModel group4 = KeycloakModelUtils.findGroupByPath(appRealm, "/group4");
            john.joinGroup(group4);
            GroupModel group31 = KeycloakModelUtils.findGroupByPath(appRealm, "/group3/group31");
            GroupModel group32 = KeycloakModelUtils.findGroupByPath(appRealm, "/group3/group32");
            john.joinGroup(group31);
            john.joinGroup(group32);
            GroupModel group14 = KeycloakModelUtils.findGroupByPath(appRealm, "/group1/group14");
            john.joinGroup(group14);
        });
        // Check user group memberships
        testingClient.server().run(( session) -> {
            LDAPTestContext ctx = LDAPTestContext.init(session);
            RealmModel appRealm = ctx.getRealm();
            UserModel john = session.users().getUserByUsername("johnkeycloak", appRealm);
            GroupModel group14 = KeycloakModelUtils.findGroupByPath(appRealm, "/group1/group14");
            GroupModel group3 = KeycloakModelUtils.findGroupByPath(appRealm, "/group3");
            GroupModel group31 = KeycloakModelUtils.findGroupByPath(appRealm, "/group3/group31");
            GroupModel group32 = KeycloakModelUtils.findGroupByPath(appRealm, "/group3/group32");
            GroupModel group4 = KeycloakModelUtils.findGroupByPath(appRealm, "/group4");
            Set<GroupModel> groups = john.getGroups();
            Assert.assertTrue(groups.contains(group14));
            Assert.assertFalse(groups.contains(group3));
            Assert.assertTrue(groups.contains(group31));
            Assert.assertTrue(groups.contains(group32));
            Assert.assertTrue(groups.contains(group4));
            long groupsCount = john.getGroupsCount();
            Assert.assertEquals(4, groupsCount);
            Set<GroupModel> groupsWith3v1 = john.getGroups("3", 0, 10);
            Assert.assertEquals(2, groupsWith3v1.size());
            Set<GroupModel> groupsWith3v2 = john.getGroups("3", 1, 10);
            Assert.assertEquals(1, groupsWith3v2.size());
            Set<GroupModel> groupsWith3v3 = john.getGroups("3", 1, 1);
            Assert.assertEquals(1, groupsWith3v3.size());
            Set<GroupModel> groupsWith3v4 = john.getGroups("3", 1, 0);
            Assert.assertEquals(0, groupsWith3v4.size());
            Set<GroupModel> groupsWithKeycloak = john.getGroups("Keycloak", 0, 10);
            Assert.assertEquals(0, groupsWithKeycloak.size());
        });
    }

    @Test
    public void test07_newUserDefaultGroupsImportModeTest() throws Exception {
        // Check user group memberships
        testingClient.server().run(( session) -> {
            LDAPTestContext ctx = LDAPTestContext.init(session);
            RealmModel appRealm = ctx.getRealm();
            ComponentModel mapperModel = LDAPTestUtils.getSubcomponentByName(appRealm, ctx.getLdapModel(), "groupsMapper");
            LDAPTestUtils.updateGroupMapperConfigOptions(mapperModel, GroupMapperConfig.MODE, LDAPGroupMapperMode.IMPORT.toString());
            appRealm.updateComponent(mapperModel);
            UserModel david = session.users().addUser(appRealm, "davidkeycloak");
            GroupModel defaultGroup11 = KeycloakModelUtils.findGroupByPath(appRealm, "/defaultGroup1/defaultGroup11");
            Assert.assertNotNull(defaultGroup11);
            GroupModel defaultGroup12 = KeycloakModelUtils.findGroupByPath(appRealm, "/defaultGroup1/defaultGroup12");
            Assert.assertNotNull(defaultGroup12);
            GroupModel group31 = KeycloakModelUtils.findGroupByPath(appRealm, "/group3/group31");
            Assert.assertNotNull(group31);
            GroupModel group32 = KeycloakModelUtils.findGroupByPath(appRealm, "/group3/group32");
            Assert.assertNotNull(group32);
            GroupModel group4 = KeycloakModelUtils.findGroupByPath(appRealm, "/group4");
            Assert.assertNotNull(group4);
            Set<GroupModel> groups = david.getGroups();
            Assert.assertTrue(groups.contains(defaultGroup11));
            Assert.assertTrue(groups.contains(defaultGroup12));
            Assert.assertFalse(groups.contains(group31));
            Assert.assertFalse(groups.contains(group32));
            Assert.assertFalse(groups.contains(group4));
        });
    }
}

