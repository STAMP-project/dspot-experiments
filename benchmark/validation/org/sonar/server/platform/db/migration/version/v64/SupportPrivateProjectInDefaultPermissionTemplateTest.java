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


public class SupportPrivateProjectInDefaultPermissionTemplateTest {
    private static final String DEFAULT_ORGANIZATION_UUID = "def org uuid";

    private static final String OTHER_ORGANIZATION_UUID = "not def org uuid";

    private static final String PERMISSION_USER = "user";

    private static final String PERMISSION_CODEVIEWER = "codeviewer";

    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(SupportPrivateProjectInDefaultPermissionTemplateTest.class, "organizations_and_groups_and_permission_templates.sql");

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private SupportPrivateProjectInDefaultPermissionTemplate underTest = new SupportPrivateProjectInDefaultPermissionTemplate(db.database(), new DefaultOrganizationUuidProviderImpl());

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
    public void execute_fails_with_ISE_when_default_organization_has_no_default_groupId() throws SQLException {
        setupDefaultOrganization(null, "pt1", "pt2");
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("No default group id is defined for default organization (uuid=def org uuid)");
        underTest.execute();
    }

    @Test
    public void execute_fails_with_ISE_when_default_group_of_default_organization_does_not_exist() throws SQLException {
        setupDefaultOrganization(112, "pT1", "pT2");
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Permission template with uuid pT1 not found");
        underTest.execute();
    }

    @Test
    public void execute_does_nothing_when_default_organization_has_default_permission_template_for_projects() throws SQLException {
        int groupId = insertGroup(SupportPrivateProjectInDefaultPermissionTemplateTest.DEFAULT_ORGANIZATION_UUID);
        setupDefaultOrganization(groupId, null, null);
        underTest.execute();
    }

    @Test
    public void execute_fails_with_ISE_when_default_organization_has_default_permission_template_for_views_but_not_for_projects() throws SQLException {
        int groupId = insertGroup(SupportPrivateProjectInDefaultPermissionTemplateTest.DEFAULT_ORGANIZATION_UUID);
        setupDefaultOrganization(groupId, null, "pt1");
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Inconsistent state for default organization (uuid=def org uuid): no project default template is defined but view default template is");
        underTest.execute();
    }

    @Test
    public void execute_ignores_default_permission_template_for_view_of_default_organization_if_it_does_not_exist_and_removes_the_reference() throws SQLException {
        int groupId = insertGroup(SupportPrivateProjectInDefaultPermissionTemplateTest.DEFAULT_ORGANIZATION_UUID);
        SupportPrivateProjectInDefaultPermissionTemplateTest.IdAndUuid projectDefPermTemplate = insertPermissionTemplate(SupportPrivateProjectInDefaultPermissionTemplateTest.DEFAULT_ORGANIZATION_UUID);
        setupDefaultOrganization(groupId, projectDefPermTemplate.uuid, "fooBar");
        underTest.execute();
        assertThat(db.select((("select default_perm_template_project as \"project\", default_perm_template_view as \"view\" from organizations where uuid=\'" + (SupportPrivateProjectInDefaultPermissionTemplateTest.DEFAULT_ORGANIZATION_UUID)) + "'"))).extracting(( row) -> row.get("project"), ( row) -> row.get("view")).containsOnly(tuple(projectDefPermTemplate.uuid, null));
    }

    @Test
    public void execute_does_not_fail_when_default_organization_has_default_permission_template_for_view() throws SQLException {
        int groupId = insertGroup(SupportPrivateProjectInDefaultPermissionTemplateTest.DEFAULT_ORGANIZATION_UUID);
        SupportPrivateProjectInDefaultPermissionTemplateTest.IdAndUuid projectDefPermTemplate = insertPermissionTemplate(SupportPrivateProjectInDefaultPermissionTemplateTest.DEFAULT_ORGANIZATION_UUID);
        setupDefaultOrganization(groupId, projectDefPermTemplate.uuid, null);
        underTest.execute();
    }

    @Test
    public void execute_adds_permission_USER_and_CODEVIEWER_to_default_group_of_default_organization_in_its_default_project_template() throws SQLException {
        int groupId = insertGroup(SupportPrivateProjectInDefaultPermissionTemplateTest.DEFAULT_ORGANIZATION_UUID);
        SupportPrivateProjectInDefaultPermissionTemplateTest.IdAndUuid projectDefPermTemplate = insertPermissionTemplate(SupportPrivateProjectInDefaultPermissionTemplateTest.DEFAULT_ORGANIZATION_UUID);
        setupDefaultOrganization(groupId, projectDefPermTemplate.uuid, null);
        int otherGroupId = insertGroup(SupportPrivateProjectInDefaultPermissionTemplateTest.OTHER_ORGANIZATION_UUID);
        SupportPrivateProjectInDefaultPermissionTemplateTest.IdAndUuid otherProjectDefPermTemplate = insertPermissionTemplate(SupportPrivateProjectInDefaultPermissionTemplateTest.OTHER_ORGANIZATION_UUID);
        insertOrganization(SupportPrivateProjectInDefaultPermissionTemplateTest.OTHER_ORGANIZATION_UUID, otherGroupId, otherProjectDefPermTemplate.uuid, null);
        underTest.execute();
        verifyPermissionOfGroupInTemplate(projectDefPermTemplate, groupId, SupportPrivateProjectInDefaultPermissionTemplateTest.PERMISSION_USER, SupportPrivateProjectInDefaultPermissionTemplateTest.PERMISSION_CODEVIEWER);
        verifyPermissionOfGroupInTemplate(otherProjectDefPermTemplate, otherGroupId);
    }

    @Test
    public void execute_does_not_fail_if_default_group_already_has_permission_USER_and_adds_only_CODEVIEWER_to_default_group_of_default_organization_in_its_default_project_template() throws SQLException {
        int groupId = insertGroup(SupportPrivateProjectInDefaultPermissionTemplateTest.DEFAULT_ORGANIZATION_UUID);
        SupportPrivateProjectInDefaultPermissionTemplateTest.IdAndUuid projectDefPermTemplate = insertPermissionTemplate(SupportPrivateProjectInDefaultPermissionTemplateTest.DEFAULT_ORGANIZATION_UUID);
        insertGroupPermission(projectDefPermTemplate, groupId, SupportPrivateProjectInDefaultPermissionTemplateTest.PERMISSION_USER);
        setupDefaultOrganization(groupId, projectDefPermTemplate.uuid, null);
        int otherGroupId = insertGroup(SupportPrivateProjectInDefaultPermissionTemplateTest.OTHER_ORGANIZATION_UUID);
        SupportPrivateProjectInDefaultPermissionTemplateTest.IdAndUuid otherProjectDefPermTemplateUuid = insertPermissionTemplate(SupportPrivateProjectInDefaultPermissionTemplateTest.OTHER_ORGANIZATION_UUID);
        insertOrganization(SupportPrivateProjectInDefaultPermissionTemplateTest.OTHER_ORGANIZATION_UUID, otherGroupId, otherProjectDefPermTemplateUuid.uuid, null);
        underTest.execute();
        verifyPermissionOfGroupInTemplate(projectDefPermTemplate, groupId, SupportPrivateProjectInDefaultPermissionTemplateTest.PERMISSION_USER, SupportPrivateProjectInDefaultPermissionTemplateTest.PERMISSION_CODEVIEWER);
        verifyPermissionOfGroupInTemplate(otherProjectDefPermTemplateUuid, otherGroupId);
    }

    @Test
    public void execute_does_not_fail_if_default_group_already_has_permission_CODEVIEWER_and_adds_only_USER_to_default_group_of_default_organization_in_its_default_project_template() throws SQLException {
        int groupId = insertGroup(SupportPrivateProjectInDefaultPermissionTemplateTest.DEFAULT_ORGANIZATION_UUID);
        SupportPrivateProjectInDefaultPermissionTemplateTest.IdAndUuid projectDefPermTemplate = insertPermissionTemplate(SupportPrivateProjectInDefaultPermissionTemplateTest.DEFAULT_ORGANIZATION_UUID);
        insertGroupPermission(projectDefPermTemplate, groupId, SupportPrivateProjectInDefaultPermissionTemplateTest.PERMISSION_CODEVIEWER);
        setupDefaultOrganization(groupId, projectDefPermTemplate.uuid, null);
        int otherGroupId = insertGroup(SupportPrivateProjectInDefaultPermissionTemplateTest.OTHER_ORGANIZATION_UUID);
        SupportPrivateProjectInDefaultPermissionTemplateTest.IdAndUuid otherProjectDefPermTemplateUuid = insertPermissionTemplate(SupportPrivateProjectInDefaultPermissionTemplateTest.OTHER_ORGANIZATION_UUID);
        insertOrganization(SupportPrivateProjectInDefaultPermissionTemplateTest.OTHER_ORGANIZATION_UUID, otherGroupId, otherProjectDefPermTemplateUuid.uuid, null);
        underTest.execute();
        verifyPermissionOfGroupInTemplate(projectDefPermTemplate, groupId, SupportPrivateProjectInDefaultPermissionTemplateTest.PERMISSION_USER, SupportPrivateProjectInDefaultPermissionTemplateTest.PERMISSION_CODEVIEWER);
        verifyPermissionOfGroupInTemplate(otherProjectDefPermTemplateUuid, otherGroupId);
    }

    @Test
    public void execute_is_reentrant() throws SQLException {
        int groupId = insertGroup(SupportPrivateProjectInDefaultPermissionTemplateTest.DEFAULT_ORGANIZATION_UUID);
        SupportPrivateProjectInDefaultPermissionTemplateTest.IdAndUuid projectDefPermTemplate = insertPermissionTemplate(SupportPrivateProjectInDefaultPermissionTemplateTest.DEFAULT_ORGANIZATION_UUID);
        setupDefaultOrganization(groupId, projectDefPermTemplate.uuid, null);
        int otherGroupId = insertGroup(SupportPrivateProjectInDefaultPermissionTemplateTest.OTHER_ORGANIZATION_UUID);
        SupportPrivateProjectInDefaultPermissionTemplateTest.IdAndUuid otherProjectDefPermTemplateUuid = insertPermissionTemplate(SupportPrivateProjectInDefaultPermissionTemplateTest.OTHER_ORGANIZATION_UUID);
        insertOrganization(SupportPrivateProjectInDefaultPermissionTemplateTest.OTHER_ORGANIZATION_UUID, otherGroupId, otherProjectDefPermTemplateUuid.uuid, null);
        underTest.execute();
        underTest.execute();
        verifyPermissionOfGroupInTemplate(projectDefPermTemplate, groupId, SupportPrivateProjectInDefaultPermissionTemplateTest.PERMISSION_USER, SupportPrivateProjectInDefaultPermissionTemplateTest.PERMISSION_CODEVIEWER);
        verifyPermissionOfGroupInTemplate(otherProjectDefPermTemplateUuid, otherGroupId);
    }

    private static final class IdAndUuid {
        private final int id;

        private final String uuid;

        private IdAndUuid(int id, String uuid) {
            this.id = id;
            this.uuid = uuid;
        }
    }
}

