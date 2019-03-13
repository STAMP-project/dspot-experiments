/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.server.platform.db.migration.version.v64;


import java.sql.SQLException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.db.CoreDbTester;
import org.sonar.server.platform.db.migration.version.v63.DefaultOrganizationUuidProviderImpl;


public class PopulateOrganizationMembersTableTest {
    private static final String TABLE = "organization_members";

    private static final String DEFAULT_ORGANIZATION_UUID = "def org uuid";

    private static final String PERMISSION_PROVISIONING = "provisioning";

    private static final String PERMISSION_ADMIN = "admin";

    private static final String PERMISSION_BROWSE = "user";

    private static final String PERMISSION_CODEVIEWER = "codeviewer";

    private static final String ORG1_UUID = "ORG1_UUID";

    private static final String ORG2_UUID = "ORG2_UUID";

    private static final String USER1_LOGIN = "USER1";

    private static final String USER2_LOGIN = "USER2";

    private static final String USER3_LOGIN = "USER3";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(PopulateOrganizationMembersTableTest.class, "initial.sql");

    private PopulateOrganizationMembersTable underTest = new PopulateOrganizationMembersTable(db.database(), new DefaultOrganizationUuidProviderImpl());

    @Test
    public void fails_with_ISE_when_no_default_organization_is_set() throws SQLException {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Default organization uuid is missing");
        underTest.execute();
    }

    @Test
    public void fails_with_ISE_when_default_organization_does_not_exist_in_table_ORGANIZATIONS() throws SQLException {
        setDefaultOrganizationProperty("blabla");
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Default organization with uuid 'blabla' does not exist in table ORGANIZATIONS");
        underTest.execute();
    }

    @Test
    public void execute_has_no_effect_when_table_is_empty() throws SQLException {
        setupDefaultOrganization();
        underTest.execute();
    }

    @Test
    public void execute_is_reentrant_when_table_is_empty() throws SQLException {
        setupDefaultOrganization();
        underTest.execute();
        underTest.execute();
    }

    @Test
    public void migrate_user_having_direct_global_permissions() throws Exception {
        setupDefaultOrganization();
        insertOrganization(PopulateOrganizationMembersTableTest.ORG1_UUID);
        insertOrganization(PopulateOrganizationMembersTableTest.ORG2_UUID);
        int userId = insertUser(PopulateOrganizationMembersTableTest.USER1_LOGIN);
        insertUserRole(userId, PopulateOrganizationMembersTableTest.PERMISSION_PROVISIONING, PopulateOrganizationMembersTableTest.ORG1_UUID, null);
        insertUserRole(userId, PopulateOrganizationMembersTableTest.PERMISSION_ADMIN, PopulateOrganizationMembersTableTest.ORG1_UUID, null);
        insertUserRole(userId, PopulateOrganizationMembersTableTest.PERMISSION_ADMIN, PopulateOrganizationMembersTableTest.ORG2_UUID, null);
        underTest.execute();
        verifyUserMembership(userId, PopulateOrganizationMembersTableTest.ORG1_UUID, PopulateOrganizationMembersTableTest.ORG2_UUID, PopulateOrganizationMembersTableTest.DEFAULT_ORGANIZATION_UUID);
    }

    @Test
    public void migrate_user_having_direct_project_permissions() throws Exception {
        setupDefaultOrganization();
        insertOrganization(PopulateOrganizationMembersTableTest.ORG1_UUID);
        insertOrganization(PopulateOrganizationMembersTableTest.ORG2_UUID);
        int userId = insertUser(PopulateOrganizationMembersTableTest.USER1_LOGIN);
        insertUserRole(userId, PopulateOrganizationMembersTableTest.PERMISSION_BROWSE, PopulateOrganizationMembersTableTest.ORG1_UUID, 1);
        insertUserRole(userId, PopulateOrganizationMembersTableTest.PERMISSION_CODEVIEWER, PopulateOrganizationMembersTableTest.ORG1_UUID, 1);
        insertUserRole(userId, PopulateOrganizationMembersTableTest.PERMISSION_ADMIN, PopulateOrganizationMembersTableTest.ORG2_UUID, 2);
        underTest.execute();
        verifyUserMembership(userId, PopulateOrganizationMembersTableTest.ORG1_UUID, PopulateOrganizationMembersTableTest.ORG2_UUID, PopulateOrganizationMembersTableTest.DEFAULT_ORGANIZATION_UUID);
    }

    @Test
    public void migrate_user_having_global_permissions_from_group() throws Exception {
        setupDefaultOrganization();
        insertOrganization(PopulateOrganizationMembersTableTest.ORG1_UUID);
        insertOrganization(PopulateOrganizationMembersTableTest.ORG2_UUID);
        int userId = insertUser(PopulateOrganizationMembersTableTest.USER1_LOGIN);
        int group1Id = insertNewGroup(PopulateOrganizationMembersTableTest.ORG1_UUID);
        int group2Id = insertNewGroup(PopulateOrganizationMembersTableTest.ORG2_UUID);
        insertUserGroup(userId, group1Id);
        insertUserGroup(userId, group2Id);
        underTest.execute();
        verifyUserMembership(userId, PopulateOrganizationMembersTableTest.ORG1_UUID, PopulateOrganizationMembersTableTest.ORG2_UUID, PopulateOrganizationMembersTableTest.DEFAULT_ORGANIZATION_UUID);
    }

    @Test
    public void user_without_any_permission_should_be_member_of_default_organization() throws Exception {
        setupDefaultOrganization();
        int userId = insertUser(PopulateOrganizationMembersTableTest.USER1_LOGIN);
        underTest.execute();
        verifyUserMembership(userId, PopulateOrganizationMembersTableTest.DEFAULT_ORGANIZATION_UUID, PopulateOrganizationMembersTableTest.DEFAULT_ORGANIZATION_UUID);
    }

    @Test
    public void migrate_users_having_any_kind_of_permission() throws Exception {
        setupDefaultOrganization();
        insertOrganization(PopulateOrganizationMembersTableTest.ORG1_UUID);
        insertOrganization(PopulateOrganizationMembersTableTest.ORG2_UUID);
        int user1 = insertUser(PopulateOrganizationMembersTableTest.USER1_LOGIN);
        int user2 = insertUser(PopulateOrganizationMembersTableTest.USER2_LOGIN);
        int user3 = insertUser(PopulateOrganizationMembersTableTest.USER3_LOGIN);
        int groupId = insertNewGroup(PopulateOrganizationMembersTableTest.ORG1_UUID);
        insertUserGroup(user2, groupId);
        insertUserRole(user1, PopulateOrganizationMembersTableTest.PERMISSION_PROVISIONING, PopulateOrganizationMembersTableTest.ORG1_UUID, null);
        insertUserRole(user1, PopulateOrganizationMembersTableTest.PERMISSION_BROWSE, PopulateOrganizationMembersTableTest.ORG2_UUID, 1);
        underTest.execute();
        verifyUserMembership(user1, PopulateOrganizationMembersTableTest.ORG1_UUID, PopulateOrganizationMembersTableTest.ORG2_UUID, PopulateOrganizationMembersTableTest.DEFAULT_ORGANIZATION_UUID);
        verifyUserMembership(user2, PopulateOrganizationMembersTableTest.ORG1_UUID, PopulateOrganizationMembersTableTest.DEFAULT_ORGANIZATION_UUID);
        verifyUserMembership(user3, PopulateOrganizationMembersTableTest.DEFAULT_ORGANIZATION_UUID);
    }

    @Test
    public void migrate_missing_membership_on_direct_permission() throws Exception {
        setupDefaultOrganization();
        insertOrganization(PopulateOrganizationMembersTableTest.ORG1_UUID);
        insertOrganization(PopulateOrganizationMembersTableTest.ORG2_UUID);
        int userId = insertUser(PopulateOrganizationMembersTableTest.USER1_LOGIN);
        insertUserRole(userId, PopulateOrganizationMembersTableTest.PERMISSION_ADMIN, PopulateOrganizationMembersTableTest.ORG1_UUID, null);
        insertUserRole(userId, PopulateOrganizationMembersTableTest.PERMISSION_PROVISIONING, PopulateOrganizationMembersTableTest.ORG2_UUID, null);
        // Membership on organization 1 already exists, migration will add membership on organization 2 and default organization
        insertOrganizationMember(userId, PopulateOrganizationMembersTableTest.ORG1_UUID);
        underTest.execute();
        verifyUserMembership(userId, PopulateOrganizationMembersTableTest.ORG1_UUID, PopulateOrganizationMembersTableTest.ORG2_UUID, PopulateOrganizationMembersTableTest.DEFAULT_ORGANIZATION_UUID);
    }

    @Test
    public void migrate_missing_membership_on_group_permission() throws Exception {
        setupDefaultOrganization();
        insertOrganization(PopulateOrganizationMembersTableTest.ORG1_UUID);
        insertOrganization(PopulateOrganizationMembersTableTest.ORG2_UUID);
        int userId = insertUser(PopulateOrganizationMembersTableTest.USER1_LOGIN);
        int group1Id = insertNewGroup(PopulateOrganizationMembersTableTest.ORG1_UUID);
        int group2Id = insertNewGroup(PopulateOrganizationMembersTableTest.ORG2_UUID);
        insertUserGroup(userId, group1Id);
        insertUserGroup(userId, group2Id);
        // Membership on organization 1 already exists, migration will add membership on organization 2 and default organization
        insertOrganizationMember(userId, PopulateOrganizationMembersTableTest.ORG1_UUID);
        underTest.execute();
        verifyUserMembership(userId, PopulateOrganizationMembersTableTest.ORG1_UUID, PopulateOrganizationMembersTableTest.ORG2_UUID, PopulateOrganizationMembersTableTest.DEFAULT_ORGANIZATION_UUID);
    }

    @Test
    public void migrate_active_users_to_default_organization() throws Exception {
        setupDefaultOrganization();
        int user1Id = insertUser(PopulateOrganizationMembersTableTest.USER1_LOGIN, false);
        int user2Id = insertUser(PopulateOrganizationMembersTableTest.USER2_LOGIN, false);
        int user3Id = insertUser(PopulateOrganizationMembersTableTest.USER3_LOGIN, false);
        int group1Id = insertNewGroup(PopulateOrganizationMembersTableTest.ORG1_UUID);
        insertUserRole(user1Id, PopulateOrganizationMembersTableTest.PERMISSION_ADMIN, PopulateOrganizationMembersTableTest.ORG1_UUID, null);
        insertUserGroup(user2Id, group1Id);
        underTest.execute();
        verifyUserMembership(user1Id);
        verifyUserMembership(user2Id);
        verifyUserMembership(user3Id);
    }

    @Test
    public void ignore_already_associated_users() throws Exception {
        setupDefaultOrganization();
        insertOrganization(PopulateOrganizationMembersTableTest.ORG1_UUID);
        int userId = insertUser(PopulateOrganizationMembersTableTest.USER1_LOGIN);
        insertUserRole(userId, PopulateOrganizationMembersTableTest.PERMISSION_PROVISIONING, PopulateOrganizationMembersTableTest.ORG1_UUID, null);
        // User is already associated to organization 1 and to default organization, it should not fail
        insertOrganizationMember(userId, PopulateOrganizationMembersTableTest.ORG1_UUID);
        insertOrganizationMember(userId, PopulateOrganizationMembersTableTest.DEFAULT_ORGANIZATION_UUID);
        underTest.execute();
        verifyUserMembership(userId, PopulateOrganizationMembersTableTest.ORG1_UUID, PopulateOrganizationMembersTableTest.DEFAULT_ORGANIZATION_UUID);
    }

    @Test
    public void migration_is_reentrant() throws Exception {
        setupDefaultOrganization();
        insertOrganization(PopulateOrganizationMembersTableTest.ORG1_UUID);
        int userId = insertUser(PopulateOrganizationMembersTableTest.USER1_LOGIN);
        insertUserRole(userId, PopulateOrganizationMembersTableTest.PERMISSION_PROVISIONING, PopulateOrganizationMembersTableTest.ORG1_UUID, null);
        verifyUserMembership(userId);
        underTest.execute();
        verifyUserMembership(userId, PopulateOrganizationMembersTableTest.ORG1_UUID, PopulateOrganizationMembersTableTest.DEFAULT_ORGANIZATION_UUID);
        underTest.execute();
        verifyUserMembership(userId, PopulateOrganizationMembersTableTest.ORG1_UUID, PopulateOrganizationMembersTableTest.DEFAULT_ORGANIZATION_UUID);
    }
}

