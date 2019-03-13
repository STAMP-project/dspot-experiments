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
package org.sonar.server.permission.ws;


import Qualifiers.PROJECT;
import UserRole.ADMIN;
import org.junit.Test;
import org.sonar.api.resources.ResourceTypes;
import org.sonar.api.web.UserRole;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.component.ComponentTesting;
import org.sonar.db.component.ResourceTypesRule;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.db.user.UserDto;
import org.sonar.server.exceptions.BadRequestException;
import org.sonar.server.exceptions.ForbiddenException;
import org.sonar.server.exceptions.NotFoundException;
import org.sonar.server.exceptions.ServerException;
import org.sonar.server.permission.PermissionService;


public class RemoveUserActionTest extends BasePermissionWsTest<RemoveUserAction> {
    private static final String A_PROJECT_UUID = "project-uuid";

    private static final String A_PROJECT_KEY = "project-key";

    private static final String A_LOGIN = "ray.bradbury";

    private UserDto user;

    private ResourceTypes resourceTypes = new ResourceTypesRule().setRootQualifiers(PROJECT);

    private PermissionService permissionService = new org.sonar.server.permission.PermissionServiceImpl(resourceTypes);

    private WsParameters wsParameters = new WsParameters(permissionService);

    @Test
    public void remove_permission_from_user() {
        db.users().insertPermissionOnUser(user, PROVISION_PROJECTS);
        db.users().insertPermissionOnUser(user, ADMINISTER_QUALITY_GATES);
        loginAsAdmin(db.getDefaultOrganization());
        newRequest().setParam(PARAM_USER_LOGIN, user.getLogin()).setParam(PARAM_PERMISSION, QUALITY_GATE_ADMIN).execute();
        assertThat(db.users().selectPermissionsOfUser(user, db.getDefaultOrganization())).containsOnly(PROVISION_PROJECTS);
    }

    @Test
    public void fail_to_remove_admin_permission_if_last_admin() {
        db.users().insertPermissionOnUser(user, ADMINISTER);
        loginAsAdmin(db.getDefaultOrganization());
        expectedException.expect(BadRequestException.class);
        expectedException.expectMessage("Last user with permission 'admin'. Permission cannot be removed.");
        newRequest().setParam(PARAM_USER_LOGIN, user.getLogin()).setParam(PARAM_PERMISSION, UserRole.ADMIN).execute();
    }

    @Test
    public void remove_permission_from_project() {
        ComponentDto project = db.components().insertComponent(newPrivateProjectDto(db.organizations().insert(), RemoveUserActionTest.A_PROJECT_UUID).setDbKey(RemoveUserActionTest.A_PROJECT_KEY));
        db.users().insertProjectPermissionOnUser(user, UserRole.CODEVIEWER, project);
        db.users().insertProjectPermissionOnUser(user, UserRole.ISSUE_ADMIN, project);
        loginAsAdmin(db.getDefaultOrganization());
        newRequest().setParam(PARAM_USER_LOGIN, user.getLogin()).setParam(PARAM_PROJECT_ID, project.uuid()).setParam(PARAM_PERMISSION, UserRole.CODEVIEWER).execute();
        assertThat(db.users().selectProjectPermissionsOfUser(user, project)).containsOnly(UserRole.ISSUE_ADMIN);
    }

    @Test
    public void remove_with_project_key() {
        ComponentDto project = db.components().insertComponent(newPrivateProjectDto(db.organizations().insert(), RemoveUserActionTest.A_PROJECT_UUID).setDbKey(RemoveUserActionTest.A_PROJECT_KEY));
        db.users().insertProjectPermissionOnUser(user, UserRole.ISSUE_ADMIN, project);
        db.users().insertProjectPermissionOnUser(user, UserRole.CODEVIEWER, project);
        loginAsAdmin(db.getDefaultOrganization());
        newRequest().setParam(PARAM_USER_LOGIN, user.getLogin()).setParam(PARAM_PROJECT_KEY, project.getDbKey()).setParam(PARAM_PERMISSION, UserRole.ISSUE_ADMIN).execute();
        assertThat(db.users().selectProjectPermissionsOfUser(user, project)).containsOnly(UserRole.CODEVIEWER);
    }

    @Test
    public void remove_with_view_uuid() {
        ComponentDto view = db.components().insertComponent(newView(db.organizations().insert(), "view-uuid").setDbKey("view-key"));
        db.users().insertProjectPermissionOnUser(user, UserRole.ISSUE_ADMIN, view);
        db.users().insertProjectPermissionOnUser(user, UserRole.ADMIN, view);
        loginAsAdmin(db.getDefaultOrganization());
        newRequest().setParam(PARAM_USER_LOGIN, user.getLogin()).setParam(PARAM_PROJECT_KEY, view.getDbKey()).setParam(PARAM_PERMISSION, UserRole.ISSUE_ADMIN).execute();
        assertThat(db.users().selectProjectPermissionsOfUser(user, view)).containsOnly(UserRole.ADMIN);
    }

    @Test
    public void fail_when_project_does_not_exist() {
        loginAsAdmin(db.getDefaultOrganization());
        expectedException.expect(NotFoundException.class);
        newRequest().setParam(PARAM_USER_LOGIN, user.getLogin()).setParam(PARAM_PROJECT_ID, "unknown-project-uuid").setParam(PARAM_PERMISSION, UserRole.ISSUE_ADMIN).execute();
    }

    @Test
    public void fail_when_project_permission_without_permission() {
        loginAsAdmin(db.getDefaultOrganization());
        expectedException.expect(BadRequestException.class);
        newRequest().setParam(PARAM_USER_LOGIN, user.getLogin()).setParam(PARAM_PERMISSION, UserRole.ISSUE_ADMIN).execute();
    }

    @Test
    public void fail_when_component_is_a_module() {
        ComponentDto module = db.components().insertComponent(newModuleDto(ComponentTesting.newPrivateProjectDto(db.organizations().insert())));
        failIfComponentIsNotAProjectOrView(module);
    }

    @Test
    public void fail_when_component_is_a_directory() {
        ComponentDto file = db.components().insertComponent(newDirectory(ComponentTesting.newPrivateProjectDto(db.organizations().insert()), "A/B"));
        failIfComponentIsNotAProjectOrView(file);
    }

    @Test
    public void fail_when_component_is_a_file() {
        ComponentDto file = db.components().insertComponent(newFileDto(ComponentTesting.newPrivateProjectDto(db.organizations().insert()), null, "file-uuid"));
        failIfComponentIsNotAProjectOrView(file);
    }

    @Test
    public void fail_when_component_is_a_subview() {
        ComponentDto file = db.components().insertComponent(newSubView(ComponentTesting.newView(db.organizations().insert())));
        failIfComponentIsNotAProjectOrView(file);
    }

    @Test
    public void fail_when_get_request() {
        loginAsAdmin(db.getDefaultOrganization());
        expectedException.expect(ServerException.class);
        newRequest().setMethod("GET").setParam(PARAM_USER_LOGIN, "george.orwell").setParam(PARAM_PERMISSION, SYSTEM_ADMIN).execute();
    }

    @Test
    public void fail_when_user_login_is_missing() {
        loginAsAdmin(db.getDefaultOrganization());
        expectedException.expect(IllegalArgumentException.class);
        newRequest().setParam(PARAM_PERMISSION, SYSTEM_ADMIN).execute();
    }

    @Test
    public void fail_when_permission_is_missing() {
        loginAsAdmin(db.getDefaultOrganization());
        expectedException.expect(IllegalArgumentException.class);
        newRequest().setParam(PARAM_USER_LOGIN, user.getLogin()).execute();
    }

    @Test
    public void fail_when_project_uuid_and_project_key_are_provided() {
        ComponentDto project = db.components().insertComponent(newPrivateProjectDto(db.organizations().insert(), RemoveUserActionTest.A_PROJECT_UUID).setDbKey(RemoveUserActionTest.A_PROJECT_KEY));
        loginAsAdmin(db.getDefaultOrganization());
        expectedException.expect(BadRequestException.class);
        expectedException.expectMessage("Project id or project key can be provided, not both.");
        newRequest().setParam(PARAM_PERMISSION, SYSTEM_ADMIN).setParam(PARAM_USER_LOGIN, user.getLogin()).setParam(PARAM_PROJECT_ID, project.uuid()).setParam(PARAM_PROJECT_KEY, project.getDbKey()).execute();
    }

    @Test
    public void removing_global_permission_fails_if_not_administrator_of_organization() {
        userSession.logIn();
        expectedException.expect(ForbiddenException.class);
        newRequest().setParam(PARAM_USER_LOGIN, user.getLogin()).setParam(PARAM_PERMISSION, PROVISIONING).execute();
    }

    @Test
    public void removing_project_permission_fails_if_not_administrator_of_project() {
        ComponentDto project = db.components().insertPrivateProject();
        userSession.logIn();
        expectedException.expect(ForbiddenException.class);
        newRequest().setParam(PARAM_USER_LOGIN, user.getLogin()).setParam(PARAM_PERMISSION, UserRole.ISSUE_ADMIN).setParam(PARAM_PROJECT_KEY, project.getDbKey()).execute();
    }

    /**
     * User is project administrator but not system administrator
     */
    @Test
    public void removing_project_permission_is_allowed_to_project_administrators() {
        ComponentDto project = db.components().insertPrivateProject();
        db.users().insertProjectPermissionOnUser(user, UserRole.CODEVIEWER, project);
        db.users().insertProjectPermissionOnUser(user, UserRole.ISSUE_ADMIN, project);
        userSession.logIn().addProjectPermission(ADMIN, project);
        newRequest().setParam(PARAM_USER_LOGIN, user.getLogin()).setParam(PARAM_PROJECT_ID, project.uuid()).setParam(PARAM_PERMISSION, UserRole.ISSUE_ADMIN).execute();
        assertThat(db.users().selectProjectPermissionsOfUser(user, project)).containsOnly(UserRole.CODEVIEWER);
    }

    @Test
    public void fail_when_removing_USER_permission_on_a_public_project() {
        OrganizationDto organization = db.organizations().insert();
        ComponentDto project = db.components().insertPublicProject(organization);
        userSession.logIn().addProjectPermission(ADMIN, project);
        expectedException.expect(BadRequestException.class);
        expectedException.expectMessage("Permission user can't be removed from a public component");
        newRequest().setParam(PARAM_USER_LOGIN, user.getLogin()).setParam(PARAM_PROJECT_ID, project.uuid()).setParam(PARAM_PERMISSION, UserRole.USER).execute();
    }

    @Test
    public void fail_when_removing_CODEVIEWER_permission_on_a_public_project() {
        OrganizationDto organization = db.organizations().insert();
        ComponentDto project = db.components().insertPublicProject(organization);
        userSession.logIn().addProjectPermission(ADMIN, project);
        expectedException.expect(BadRequestException.class);
        expectedException.expectMessage("Permission codeviewer can't be removed from a public component");
        newRequest().setParam(PARAM_USER_LOGIN, user.getLogin()).setParam(PARAM_PROJECT_ID, project.uuid()).setParam(PARAM_PERMISSION, UserRole.CODEVIEWER).execute();
    }

    @Test
    public void fail_when_using_branch_db_key() throws Exception {
        OrganizationDto organization = db.organizations().insert();
        ComponentDto project = db.components().insertMainBranch(organization);
        userSession.logIn().addProjectPermission(ADMIN, project);
        ComponentDto branch = db.components().insertProjectBranch(project);
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage(String.format("Project key '%s' not found", branch.getDbKey()));
        newRequest().setParam(PARAM_ORGANIZATION, organization.getKey()).setParam(PARAM_PROJECT_KEY, branch.getDbKey()).setParam(PARAM_USER_LOGIN, user.getLogin()).setParam(PARAM_PERMISSION, SYSTEM_ADMIN).execute();
    }

    @Test
    public void fail_when_using_branch_uuid() {
        OrganizationDto organization = db.organizations().insert();
        ComponentDto project = db.components().insertMainBranch(organization);
        userSession.logIn().addProjectPermission(ADMIN, project);
        ComponentDto branch = db.components().insertProjectBranch(project);
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage(String.format("Project id '%s' not found", branch.uuid()));
        newRequest().setParam(PARAM_ORGANIZATION, organization.getKey()).setParam(PARAM_PROJECT_ID, branch.uuid()).setParam(PARAM_USER_LOGIN, user.getLogin()).setParam(PARAM_PERMISSION, SYSTEM_ADMIN).execute();
    }
}

