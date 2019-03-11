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
import java.util.Date;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.sonar.api.utils.System2;
import org.sonar.api.utils.log.LogTester;
import org.sonar.db.CoreDbTester;
import org.sonar.server.platform.db.migration.version.v63.DefaultOrganizationUuidProviderImpl;


public class RestoreSonarUsersGroupsTest {
    private static final Date PAST = new Date(100000000000L);

    private static final Date NOW = new Date(500000000000L);

    private static final String DEFAULT_ORGANIZATION_UUID = "def-org";

    private static final String SONAR_USERS_NAME = "sonar-users";

    private static final String SONAR_USERS_PENDING_DESCRIPTION = "<PENDING>";

    private static final String SONAR_USERS_FINAL_DESCRIPTION = "Any new users created will automatically join this group";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public LogTester logTester = new LogTester();

    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(RestoreSonarUsersGroupsTest.class, "initial.sql");

    private System2 system2 = Mockito.mock(System2.class);

    private RestoreSonarUsersGroups underTest = new RestoreSonarUsersGroups(db.database(), system2, new DefaultOrganizationUuidProviderImpl());

    @Test
    public void insert_sonar_users_group_when_it_does_not_exist() throws SQLException {
        setupDefaultOrganization();
        setDefaultGroup("default-group");
        underTest.execute();
        checkSonarUsersHasBeenCreated();
    }

    @Test
    public void display_log_when_creating_sonar_users_group() throws SQLException {
        setupDefaultOrganization();
        setDefaultGroup("default-group");
        underTest.execute();
        checkSonarUsersHasBeenCreated();
        assertThat(logTester.logs(WARN)).containsOnly("The default group has been updated from 'default-group' to 'sonar-users'. Please verify your permission schema that everything is in order");
    }

    @Test
    public void copy_permission_from_existing_default_group_to_sonar_users_when_it_does_not_exist() throws Exception {
        setupDefaultOrganization();
        long defaultGroupId = setDefaultGroup("default-group");
        insertGroupRole(defaultGroupId, "user", null);
        insertGroupRole(defaultGroupId, "admin", 1L);
        insertPermissionTemplate(defaultGroupId, "user", 10L);
        underTest.execute();
        checkSonarUsersHasBeenCreated();
        checkUserRolesOnSonarUsers(tuple("user", null, RestoreSonarUsersGroupsTest.DEFAULT_ORGANIZATION_UUID), tuple("admin", 1L, RestoreSonarUsersGroupsTest.DEFAULT_ORGANIZATION_UUID));
        checkPermissionTemplatesOnSonarUsers(tuple("user", 10L, RestoreSonarUsersGroupsTest.NOW, RestoreSonarUsersGroupsTest.NOW));
    }

    @Test
    public void update_sonar_users_group_when_existing_with_incorrect_description() throws Exception {
        setupDefaultOrganization();
        insertGroup(RestoreSonarUsersGroupsTest.SONAR_USERS_NAME, "Other description", RestoreSonarUsersGroupsTest.PAST, RestoreSonarUsersGroupsTest.PAST);
        underTest.execute();
        checkSonarUsersHasBeenUpdated();
    }

    @Test
    public void update_sonar_users_group_when_default_group_setting_is_null() throws SQLException {
        setupDefaultOrganization();
        insertDefaultGroupProperty(null);
        insertGroup(RestoreSonarUsersGroupsTest.SONAR_USERS_NAME, "Other description", RestoreSonarUsersGroupsTest.PAST, RestoreSonarUsersGroupsTest.PAST);
        underTest.execute();
        checkSonarUsersHasBeenUpdated();
    }

    @Test
    public void does_nothing_when_sonar_users_exist_with_right_description() throws SQLException {
        setupDefaultOrganization();
        insertGroup(RestoreSonarUsersGroupsTest.SONAR_USERS_NAME, RestoreSonarUsersGroupsTest.SONAR_USERS_FINAL_DESCRIPTION, RestoreSonarUsersGroupsTest.PAST, RestoreSonarUsersGroupsTest.PAST);
        underTest.execute();
        checkSonarUsersHasNotBeenUpdated();
    }

    @Test
    public void display_log_when_moving_default_group_to_sonar_users_group() throws SQLException {
        setupDefaultOrganization();
        insertGroup(RestoreSonarUsersGroupsTest.SONAR_USERS_NAME, "wrong desc", RestoreSonarUsersGroupsTest.PAST, RestoreSonarUsersGroupsTest.PAST);
        setDefaultGroup("default-group");
        underTest.execute();
        checkSonarUsersHasBeenUpdated();
        assertThat(logTester.logs(WARN)).containsOnly("The default group has been updated from 'default-group' to 'sonar-users'. Please verify your permission schema that everything is in order");
    }

    @Test
    public void does_not_copy_permission_existing_default_group_to_sonar_users_when_it_already_exists() throws Exception {
        setupDefaultOrganization();
        long defaultGroupId = setDefaultGroup("default-group");
        insertGroupRole(defaultGroupId, "user", null);
        insertGroupRole(defaultGroupId, "admin", 1L);
        insertPermissionTemplate(defaultGroupId, "user", 10L);
        // sonar-users has no permission on it
        insertGroup(RestoreSonarUsersGroupsTest.SONAR_USERS_NAME, RestoreSonarUsersGroupsTest.SONAR_USERS_FINAL_DESCRIPTION, RestoreSonarUsersGroupsTest.PAST, RestoreSonarUsersGroupsTest.PAST);
        underTest.execute();
        checkSonarUsersHasNotBeenUpdated();
        // No permission set on sonar-users
        checkUserRolesOnSonarUsers();
        checkPermissionTemplatesOnSonarUsers();
    }

    @Test
    public void does_not_display_log_when_default_group_is_sonar_users() throws SQLException {
        setupDefaultOrganization();
        insertGroup(RestoreSonarUsersGroupsTest.SONAR_USERS_NAME, RestoreSonarUsersGroupsTest.SONAR_USERS_FINAL_DESCRIPTION, RestoreSonarUsersGroupsTest.PAST, RestoreSonarUsersGroupsTest.PAST);
        insertDefaultGroupProperty(RestoreSonarUsersGroupsTest.SONAR_USERS_NAME);
        underTest.execute();
        assertThat(logTester.logs(WARN)).isEmpty();
    }

    @Test
    public void continue_migration_when_description_is_pending() throws Exception {
        setupDefaultOrganization();
        // Default group with is permissions
        long defaultGroupId = setDefaultGroup("default-group");
        insertGroupRole(defaultGroupId, "admin", 1L);
        insertGroupRole(defaultGroupId, "user", 2L);
        insertGroupRole(defaultGroupId, "codeviewer", null);
        insertPermissionTemplate(defaultGroupId, "user", 10L);
        insertPermissionTemplate(defaultGroupId, "admin", 11L);
        // sonar-users group with partial permissions from default group
        long sonarUsersGroupId = insertGroup(RestoreSonarUsersGroupsTest.SONAR_USERS_NAME, RestoreSonarUsersGroupsTest.SONAR_USERS_PENDING_DESCRIPTION, RestoreSonarUsersGroupsTest.PAST, RestoreSonarUsersGroupsTest.PAST);
        insertGroupRole(sonarUsersGroupId, "admin", 1L);
        insertPermissionTemplate(sonarUsersGroupId, "user", 10L);
        underTest.execute();
        checkSonarUsersHasBeenUpdated();
        checkUserRolesOnSonarUsers(tuple("admin", 1L, RestoreSonarUsersGroupsTest.DEFAULT_ORGANIZATION_UUID), tuple("user", 2L, RestoreSonarUsersGroupsTest.DEFAULT_ORGANIZATION_UUID), tuple("codeviewer", null, RestoreSonarUsersGroupsTest.DEFAULT_ORGANIZATION_UUID));
        checkPermissionTemplatesOnSonarUsers(tuple("user", 10L, RestoreSonarUsersGroupsTest.PAST, RestoreSonarUsersGroupsTest.PAST), tuple("admin", 11L, RestoreSonarUsersGroupsTest.NOW, RestoreSonarUsersGroupsTest.NOW));
    }

    @Test
    public void does_not_update_other_groups() throws SQLException {
        setupDefaultOrganization();
        insertGroup("another-group", "another-group", RestoreSonarUsersGroupsTest.PAST, RestoreSonarUsersGroupsTest.PAST);
        insertGroup(RestoreSonarUsersGroupsTest.SONAR_USERS_NAME, RestoreSonarUsersGroupsTest.SONAR_USERS_FINAL_DESCRIPTION, RestoreSonarUsersGroupsTest.PAST, RestoreSonarUsersGroupsTest.PAST);
        underTest.execute();
        checkSonarUsersHasNotBeenUpdated();
        assertThat(db.countRowsOfTable("groups")).isEqualTo(2);
    }

    @Test
    public void migration_is_reentrant() throws Exception {
        setupDefaultOrganization();
        long defaultGroupId = setDefaultGroup("default-group");
        insertGroupRole(defaultGroupId, "user", null);
        insertGroupRole(defaultGroupId, "admin", 1L);
        insertPermissionTemplate(defaultGroupId, "user", 10L);
        underTest.execute();
        checkSonarUsersHasBeenCreated();
        checkUserRolesOnSonarUsers(tuple("user", null, RestoreSonarUsersGroupsTest.DEFAULT_ORGANIZATION_UUID), tuple("admin", 1L, RestoreSonarUsersGroupsTest.DEFAULT_ORGANIZATION_UUID));
        checkPermissionTemplatesOnSonarUsers(tuple("user", 10L, RestoreSonarUsersGroupsTest.NOW, RestoreSonarUsersGroupsTest.NOW));
        underTest.execute();
        checkSonarUsersHasBeenCreated();
        checkUserRolesOnSonarUsers(tuple("user", null, RestoreSonarUsersGroupsTest.DEFAULT_ORGANIZATION_UUID), tuple("admin", 1L, RestoreSonarUsersGroupsTest.DEFAULT_ORGANIZATION_UUID));
        checkPermissionTemplatesOnSonarUsers(tuple("user", 10L, RestoreSonarUsersGroupsTest.NOW, RestoreSonarUsersGroupsTest.NOW));
    }

    @Test
    public void fail_when_no_default_group_in_setting_and_sonar_users_does_not_exist() throws Exception {
        setupDefaultOrganization();
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Default group setting sonar.defaultGroup is defined to a 'sonar-users' group but it doesn't exist.");
        underTest.execute();
    }

    @Test
    public void fail_when_default_group_setting_is_set_to_an_unknown_group() throws SQLException {
        setupDefaultOrganization();
        insertDefaultGroupProperty("unknown");
        insertGroup(RestoreSonarUsersGroupsTest.SONAR_USERS_NAME, RestoreSonarUsersGroupsTest.SONAR_USERS_FINAL_DESCRIPTION, RestoreSonarUsersGroupsTest.PAST, RestoreSonarUsersGroupsTest.PAST);
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Default group setting sonar.defaultGroup is defined to an unknown group.");
        underTest.execute();
    }
}

