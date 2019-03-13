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


public class CreateMembersGroupsInEachOrganizationTest {
    private static final Date PAST = new Date(100000000000L);

    private static final Date NOW = new Date(500000000000L);

    private static final String DEFAULT_ORGANIZATION_UUID = "def-org";

    private static final String ORGANIZATION_1 = "ORGANIZATION_1";

    private static final String ORGANIZATION_2 = "ORGANIZATION_2";

    private static final String TEMPLATE_1 = "TEMPLATE_1";

    private static final String TEMPLATE_2 = "TEMPLATE_2";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public LogTester logTester = new LogTester();

    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(CreateMembersGroupsInEachOrganizationTest.class, "initial.sql");

    private System2 system2 = Mockito.mock(System2.class);

    private CreateMembersGroupsInEachOrganization underTest = new CreateMembersGroupsInEachOrganization(db.database(), system2);

    @Test
    public void does_nothing_when_organization_disabled() throws Exception {
        setupDefaultOrganization();
        insertOrganization(CreateMembersGroupsInEachOrganizationTest.ORGANIZATION_1, CreateMembersGroupsInEachOrganizationTest.TEMPLATE_1);
        insertOrganization(CreateMembersGroupsInEachOrganizationTest.ORGANIZATION_2, CreateMembersGroupsInEachOrganizationTest.TEMPLATE_2);
        insertPermissionTemplate(CreateMembersGroupsInEachOrganizationTest.ORGANIZATION_1, CreateMembersGroupsInEachOrganizationTest.TEMPLATE_1, "Default");
        insertPermissionTemplate(CreateMembersGroupsInEachOrganizationTest.ORGANIZATION_2, CreateMembersGroupsInEachOrganizationTest.TEMPLATE_2, "Default");
        underTest.execute();
        checkNoGroups();
        checkNoPermTemplateGroups();
    }

    @Test
    public void insert_members_groups_when_not_existing() throws SQLException {
        setupDefaultOrganization();
        enableOrganization();
        insertOrganization(CreateMembersGroupsInEachOrganizationTest.ORGANIZATION_1, null);
        insertOrganization(CreateMembersGroupsInEachOrganizationTest.ORGANIZATION_2, null);
        underTest.execute();
        checkGroups(tuple(CreateMembersGroupsInEachOrganizationTest.ORGANIZATION_1, "Members", "All members of the organization", CreateMembersGroupsInEachOrganizationTest.NOW, CreateMembersGroupsInEachOrganizationTest.NOW), tuple(CreateMembersGroupsInEachOrganizationTest.ORGANIZATION_2, "Members", "All members of the organization", CreateMembersGroupsInEachOrganizationTest.NOW, CreateMembersGroupsInEachOrganizationTest.NOW), tuple(CreateMembersGroupsInEachOrganizationTest.DEFAULT_ORGANIZATION_UUID, "Members", "All members of the organization", CreateMembersGroupsInEachOrganizationTest.NOW, CreateMembersGroupsInEachOrganizationTest.NOW));
    }

    @Test
    public void does_not_insert_members_group_when_group_already_exist() throws SQLException {
        setupDefaultOrganization();
        enableOrganization();
        insertMembersGroup(CreateMembersGroupsInEachOrganizationTest.DEFAULT_ORGANIZATION_UUID);
        insertOrganization(CreateMembersGroupsInEachOrganizationTest.ORGANIZATION_1, null);
        insertMembersGroup(CreateMembersGroupsInEachOrganizationTest.ORGANIZATION_1);
        insertOrganization(CreateMembersGroupsInEachOrganizationTest.ORGANIZATION_2, null);
        insertMembersGroup(CreateMembersGroupsInEachOrganizationTest.ORGANIZATION_2);
        underTest.execute();
        checkGroups(tuple(CreateMembersGroupsInEachOrganizationTest.ORGANIZATION_1, "Members", "All members of the organization", CreateMembersGroupsInEachOrganizationTest.PAST, CreateMembersGroupsInEachOrganizationTest.PAST), tuple(CreateMembersGroupsInEachOrganizationTest.ORGANIZATION_2, "Members", "All members of the organization", CreateMembersGroupsInEachOrganizationTest.PAST, CreateMembersGroupsInEachOrganizationTest.PAST), tuple(CreateMembersGroupsInEachOrganizationTest.DEFAULT_ORGANIZATION_UUID, "Members", "All members of the organization", CreateMembersGroupsInEachOrganizationTest.PAST, CreateMembersGroupsInEachOrganizationTest.PAST));
    }

    @Test
    public void insert_only_missing_members_group_when_some_groups_already_exist() throws SQLException {
        setupDefaultOrganization();
        enableOrganization();
        insertMembersGroup(CreateMembersGroupsInEachOrganizationTest.DEFAULT_ORGANIZATION_UUID);
        insertOrganization(CreateMembersGroupsInEachOrganizationTest.ORGANIZATION_1, null);
        insertMembersGroup(CreateMembersGroupsInEachOrganizationTest.ORGANIZATION_1);
        insertOrganization(CreateMembersGroupsInEachOrganizationTest.ORGANIZATION_2, null);
        underTest.execute();
        checkGroups(tuple(CreateMembersGroupsInEachOrganizationTest.ORGANIZATION_1, "Members", "All members of the organization", CreateMembersGroupsInEachOrganizationTest.PAST, CreateMembersGroupsInEachOrganizationTest.PAST), tuple(CreateMembersGroupsInEachOrganizationTest.ORGANIZATION_2, "Members", "All members of the organization", CreateMembersGroupsInEachOrganizationTest.NOW, CreateMembersGroupsInEachOrganizationTest.NOW), tuple(CreateMembersGroupsInEachOrganizationTest.DEFAULT_ORGANIZATION_UUID, "Members", "All members of the organization", CreateMembersGroupsInEachOrganizationTest.PAST, CreateMembersGroupsInEachOrganizationTest.PAST));
    }

    @Test
    public void insert_permission_template_groups_when_not_existing() throws Exception {
        setupDefaultOrganization();
        enableOrganization();
        insertOrganization(CreateMembersGroupsInEachOrganizationTest.ORGANIZATION_1, CreateMembersGroupsInEachOrganizationTest.TEMPLATE_1);
        long template1 = insertPermissionTemplate(CreateMembersGroupsInEachOrganizationTest.ORGANIZATION_1, CreateMembersGroupsInEachOrganizationTest.TEMPLATE_1, "Default");
        insertOrganization(CreateMembersGroupsInEachOrganizationTest.ORGANIZATION_2, CreateMembersGroupsInEachOrganizationTest.TEMPLATE_2);
        long template2 = insertPermissionTemplate(CreateMembersGroupsInEachOrganizationTest.ORGANIZATION_2, CreateMembersGroupsInEachOrganizationTest.TEMPLATE_2, "Default");
        underTest.execute();
        long group1 = selectMembersGroupId(CreateMembersGroupsInEachOrganizationTest.ORGANIZATION_1);
        long group2 = selectMembersGroupId(CreateMembersGroupsInEachOrganizationTest.ORGANIZATION_2);
        checkPermTemplateGroups(tuple(group1, template1, "user", CreateMembersGroupsInEachOrganizationTest.NOW, CreateMembersGroupsInEachOrganizationTest.NOW), tuple(group1, template1, "codeviewer", CreateMembersGroupsInEachOrganizationTest.NOW, CreateMembersGroupsInEachOrganizationTest.NOW), tuple(group2, template2, "user", CreateMembersGroupsInEachOrganizationTest.NOW, CreateMembersGroupsInEachOrganizationTest.NOW), tuple(group2, template2, "codeviewer", CreateMembersGroupsInEachOrganizationTest.NOW, CreateMembersGroupsInEachOrganizationTest.NOW));
    }

    @Test
    public void does_not_insert_permission_template_groups_when_no_default_permission_template() throws Exception {
        setupDefaultOrganization();
        enableOrganization();
        insertOrganization(CreateMembersGroupsInEachOrganizationTest.ORGANIZATION_1, CreateMembersGroupsInEachOrganizationTest.TEMPLATE_1);
        insertOrganization(CreateMembersGroupsInEachOrganizationTest.ORGANIZATION_2, CreateMembersGroupsInEachOrganizationTest.TEMPLATE_2);
        underTest.execute();
        checkNoPermTemplateGroups();
    }

    @Test
    public void does_not_insert_permission_template_groups_when_already_existing() throws Exception {
        setupDefaultOrganization();
        enableOrganization();
        insertOrganization(CreateMembersGroupsInEachOrganizationTest.ORGANIZATION_1, CreateMembersGroupsInEachOrganizationTest.TEMPLATE_1);
        long template1 = insertPermissionTemplate(CreateMembersGroupsInEachOrganizationTest.ORGANIZATION_1, CreateMembersGroupsInEachOrganizationTest.TEMPLATE_1, "Default");
        long group1 = insertMembersGroup(CreateMembersGroupsInEachOrganizationTest.ORGANIZATION_1);
        insertPermissionTemplateGroup(group1, "user", template1);
        insertPermissionTemplateGroup(group1, "codeviewer", template1);
        insertOrganization(CreateMembersGroupsInEachOrganizationTest.ORGANIZATION_2, CreateMembersGroupsInEachOrganizationTest.TEMPLATE_2);
        long template2 = insertPermissionTemplate(CreateMembersGroupsInEachOrganizationTest.ORGANIZATION_2, CreateMembersGroupsInEachOrganizationTest.TEMPLATE_2, "Default");
        long group2 = insertMembersGroup(CreateMembersGroupsInEachOrganizationTest.ORGANIZATION_2);
        insertPermissionTemplateGroup(group2, "user", template2);
        insertPermissionTemplateGroup(group2, "codeviewer", template2);
        underTest.execute();
        checkPermTemplateGroups(tuple(group1, template1, "user", CreateMembersGroupsInEachOrganizationTest.PAST, CreateMembersGroupsInEachOrganizationTest.PAST), tuple(group1, template1, "codeviewer", CreateMembersGroupsInEachOrganizationTest.PAST, CreateMembersGroupsInEachOrganizationTest.PAST), tuple(group2, template2, "user", CreateMembersGroupsInEachOrganizationTest.PAST, CreateMembersGroupsInEachOrganizationTest.PAST), tuple(group2, template2, "codeviewer", CreateMembersGroupsInEachOrganizationTest.PAST, CreateMembersGroupsInEachOrganizationTest.PAST));
    }

    @Test
    public void insert_only_missing_permission_template_groups() throws Exception {
        setupDefaultOrganization();
        enableOrganization();
        insertOrganization(CreateMembersGroupsInEachOrganizationTest.ORGANIZATION_1, CreateMembersGroupsInEachOrganizationTest.TEMPLATE_1);
        long template1 = insertPermissionTemplate(CreateMembersGroupsInEachOrganizationTest.ORGANIZATION_1, CreateMembersGroupsInEachOrganizationTest.TEMPLATE_1, "Default");
        long group1 = insertMembersGroup(CreateMembersGroupsInEachOrganizationTest.ORGANIZATION_1);
        insertPermissionTemplateGroup(group1, "user", template1);
        insertPermissionTemplateGroup(group1, "codeviewer", template1);
        insertOrganization(CreateMembersGroupsInEachOrganizationTest.ORGANIZATION_2, CreateMembersGroupsInEachOrganizationTest.TEMPLATE_2);
        long template2 = insertPermissionTemplate(CreateMembersGroupsInEachOrganizationTest.ORGANIZATION_2, CreateMembersGroupsInEachOrganizationTest.TEMPLATE_2, "Default");
        long group2 = insertMembersGroup(CreateMembersGroupsInEachOrganizationTest.ORGANIZATION_2);
        underTest.execute();
        checkPermTemplateGroups(tuple(group1, template1, "user", CreateMembersGroupsInEachOrganizationTest.PAST, CreateMembersGroupsInEachOrganizationTest.PAST), tuple(group1, template1, "codeviewer", CreateMembersGroupsInEachOrganizationTest.PAST, CreateMembersGroupsInEachOrganizationTest.PAST), tuple(group2, template2, "user", CreateMembersGroupsInEachOrganizationTest.NOW, CreateMembersGroupsInEachOrganizationTest.NOW), tuple(group2, template2, "codeviewer", CreateMembersGroupsInEachOrganizationTest.NOW, CreateMembersGroupsInEachOrganizationTest.NOW));
    }

    @Test
    public void migration_is_reentrant() throws Exception {
        setupDefaultOrganization();
        enableOrganization();
        insertOrganization(CreateMembersGroupsInEachOrganizationTest.ORGANIZATION_1, CreateMembersGroupsInEachOrganizationTest.TEMPLATE_1);
        long template1 = insertPermissionTemplate(CreateMembersGroupsInEachOrganizationTest.ORGANIZATION_1, CreateMembersGroupsInEachOrganizationTest.TEMPLATE_1, "Default");
        insertOrganization(CreateMembersGroupsInEachOrganizationTest.ORGANIZATION_2, CreateMembersGroupsInEachOrganizationTest.TEMPLATE_2);
        long template2 = insertPermissionTemplate(CreateMembersGroupsInEachOrganizationTest.ORGANIZATION_2, CreateMembersGroupsInEachOrganizationTest.TEMPLATE_2, "Default");
        underTest.execute();
        long group1 = selectMembersGroupId(CreateMembersGroupsInEachOrganizationTest.ORGANIZATION_1);
        long group2 = selectMembersGroupId(CreateMembersGroupsInEachOrganizationTest.ORGANIZATION_2);
        checkPermTemplateGroups(tuple(group1, template1, "user", CreateMembersGroupsInEachOrganizationTest.NOW, CreateMembersGroupsInEachOrganizationTest.NOW), tuple(group1, template1, "codeviewer", CreateMembersGroupsInEachOrganizationTest.NOW, CreateMembersGroupsInEachOrganizationTest.NOW), tuple(group2, template2, "user", CreateMembersGroupsInEachOrganizationTest.NOW, CreateMembersGroupsInEachOrganizationTest.NOW), tuple(group2, template2, "codeviewer", CreateMembersGroupsInEachOrganizationTest.NOW, CreateMembersGroupsInEachOrganizationTest.NOW));
    }
}

