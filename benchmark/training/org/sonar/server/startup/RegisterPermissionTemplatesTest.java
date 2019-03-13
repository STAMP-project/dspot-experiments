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
package org.sonar.server.startup;


import DefaultGroups.ADMINISTRATORS;
import LoggerLevel.ERROR;
import OrganizationPermission.APPLICATION_CREATOR;
import OrganizationPermission.PORTFOLIO_CREATOR;
import Qualifiers.APP;
import Qualifiers.VIEW;
import System2.INSTANCE;
import UserRole.ADMIN;
import UserRole.CODEVIEWER;
import UserRole.ISSUE_ADMIN;
import UserRole.SECURITYHOTSPOT_ADMIN;
import UserRole.USER;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.api.resources.ResourceTypes;
import org.sonar.api.utils.log.LogTester;
import org.sonar.db.DbTester;
import org.sonar.db.permission.template.PermissionTemplateDto;
import org.sonar.db.permission.template.PermissionTemplateGroupDto;
import org.sonar.db.user.GroupDto;
import org.sonar.server.organization.DefaultOrganizationProvider;
import org.sonar.server.organization.TestDefaultOrganizationProvider;


public class RegisterPermissionTemplatesTest {
    private static final String DEFAULT_TEMPLATE_UUID = "default_template";

    @Rule
    public DbTester db = DbTester.create(INSTANCE);

    @Rule
    public LogTester logTester = new LogTester();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private DefaultOrganizationProvider defaultOrganizationProvider = TestDefaultOrganizationProvider.from(db);

    private ResourceTypes resourceTypes = Mockito.mock(ResourceTypes.class);

    private RegisterPermissionTemplates underTest = new RegisterPermissionTemplates(db.getDbClient(), defaultOrganizationProvider);

    @Test
    public void fail_with_ISE_if_default_template_must_be_created_and_no_default_group_is_defined() {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage((("Default group for organization " + (db.getDefaultOrganization().getUuid())) + " is not defined"));
        underTest.start();
    }

    @Test
    public void fail_with_ISE_if_default_template_must_be_created_and_default_group_does_not_exist() {
        setDefaultGroupId(new GroupDto().setId(22));
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage((("Default group with id 22 for organization " + (db.getDefaultOrganization().getUuid())) + " doesn't exist"));
        underTest.start();
    }

    @Test
    public void insert_default_permission_template_if_fresh_install_without_governance() {
        GroupDto defaultGroup = createAndSetDefaultGroup();
        db.users().insertGroup(db.getDefaultOrganization(), ADMINISTRATORS);
        Mockito.when(resourceTypes.isQualifierPresent(ArgumentMatchers.eq(APP))).thenReturn(false);
        Mockito.when(resourceTypes.isQualifierPresent(ArgumentMatchers.eq(VIEW))).thenReturn(false);
        underTest.start();
        PermissionTemplateDto defaultTemplate = selectTemplate();
        assertThat(defaultTemplate.getName()).isEqualTo("Default template");
        List<PermissionTemplateGroupDto> groupPermissions = selectGroupPermissions(defaultTemplate);
        assertThat(groupPermissions).hasSize(7);
        expectGroupPermission(groupPermissions, ADMIN, ADMINISTRATORS);
        expectGroupPermission(groupPermissions, ISSUE_ADMIN, ADMINISTRATORS);
        expectGroupPermission(groupPermissions, SECURITYHOTSPOT_ADMIN, ADMINISTRATORS);
        expectGroupPermission(groupPermissions, APPLICATION_CREATOR.getKey(), ADMINISTRATORS);
        expectGroupPermission(groupPermissions, PORTFOLIO_CREATOR.getKey(), ADMINISTRATORS);
        expectGroupPermission(groupPermissions, CODEVIEWER, defaultGroup.getName());
        expectGroupPermission(groupPermissions, USER, defaultGroup.getName());
        verifyDefaultTemplates();
        assertThat(logTester.logs(ERROR)).isEmpty();
    }

    @Test
    public void insert_default_permission_template_if_fresh_install_with_governance() {
        GroupDto defaultGroup = createAndSetDefaultGroup();
        db.users().insertGroup(db.getDefaultOrganization(), ADMINISTRATORS);
        Mockito.when(resourceTypes.isQualifierPresent(ArgumentMatchers.eq(APP))).thenReturn(true);
        Mockito.when(resourceTypes.isQualifierPresent(ArgumentMatchers.eq(VIEW))).thenReturn(true);
        underTest.start();
        PermissionTemplateDto defaultTemplate = selectTemplate();
        assertThat(defaultTemplate.getName()).isEqualTo("Default template");
        List<PermissionTemplateGroupDto> groupPermissions = selectGroupPermissions(defaultTemplate);
        assertThat(groupPermissions).hasSize(7);
        expectGroupPermission(groupPermissions, ADMIN, ADMINISTRATORS);
        expectGroupPermission(groupPermissions, ISSUE_ADMIN, ADMINISTRATORS);
        expectGroupPermission(groupPermissions, SECURITYHOTSPOT_ADMIN, ADMINISTRATORS);
        expectGroupPermission(groupPermissions, APPLICATION_CREATOR.getKey(), ADMINISTRATORS);
        expectGroupPermission(groupPermissions, PORTFOLIO_CREATOR.getKey(), ADMINISTRATORS);
        expectGroupPermission(groupPermissions, CODEVIEWER, defaultGroup.getName());
        expectGroupPermission(groupPermissions, USER, defaultGroup.getName());
        verifyDefaultTemplates();
        assertThat(logTester.logs(ERROR)).isEmpty();
    }

    @Test
    public void ignore_administrators_permissions_if_group_does_not_exist() {
        GroupDto defaultGroup = createAndSetDefaultGroup();
        underTest.start();
        PermissionTemplateDto defaultTemplate = selectTemplate();
        assertThat(defaultTemplate.getName()).isEqualTo("Default template");
        List<PermissionTemplateGroupDto> groupPermissions = selectGroupPermissions(defaultTemplate);
        assertThat(groupPermissions).hasSize(2);
        expectGroupPermission(groupPermissions, CODEVIEWER, defaultGroup.getName());
        expectGroupPermission(groupPermissions, USER, defaultGroup.getName());
        verifyDefaultTemplates();
        assertThat(logTester.logs(ERROR)).contains("Cannot setup default permission for group: sonar-administrators");
    }

    @Test
    public void do_not_create_default_template_if_already_exists_but_register_when_it_is_not() {
        db.permissionTemplates().insertTemplate(newPermissionTemplateDto().setOrganizationUuid(db.getDefaultOrganization().getUuid()).setUuid(RegisterPermissionTemplatesTest.DEFAULT_TEMPLATE_UUID));
        underTest.start();
        verifyDefaultTemplates();
    }

    @Test
    public void do_not_fail_if_default_template_exists_and_is_registered() {
        PermissionTemplateDto projectTemplate = db.permissionTemplates().insertTemplate(newPermissionTemplateDto().setOrganizationUuid(db.getDefaultOrganization().getUuid()).setUuid(RegisterPermissionTemplatesTest.DEFAULT_TEMPLATE_UUID));
        db.organizations().setDefaultTemplates(projectTemplate, null, null);
        underTest.start();
        verifyDefaultTemplates();
    }
}

