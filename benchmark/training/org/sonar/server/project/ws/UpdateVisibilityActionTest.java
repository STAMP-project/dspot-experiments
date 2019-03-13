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
package org.sonar.server.project.ws;


import BillingValidations.Organization;
import ProjectIndexer.Cause.PERMISSION_CHANGE;
import Qualifiers.PROJECT;
import System2.INSTANCE;
import UserRole.ADMIN;
import UserRole.CODEVIEWER;
import UserRole.ISSUE_ADMIN;
import UserRole.USER;
import WebService.Action;
import java.util.Arrays;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.api.resources.ResourceTypes;
import org.sonar.api.server.ws.WebService;
import org.sonar.core.util.stream.MoreCollectors;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.DbTester;
import org.sonar.db.component.BranchDto;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.component.ComponentTesting;
import org.sonar.db.component.ResourceTypesRule;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.db.organization.OrganizationTesting;
import org.sonar.db.permission.OrganizationPermission;
import org.sonar.db.user.GroupDto;
import org.sonar.db.user.UserDto;
import org.sonar.server.component.TestComponentFinder;
import org.sonar.server.es.EsTester;
import org.sonar.server.es.TestProjectIndexers;
import org.sonar.server.exceptions.BadRequestException;
import org.sonar.server.exceptions.NotFoundException;
import org.sonar.server.exceptions.UnauthorizedException;
import org.sonar.server.organization.BillingValidations;
import org.sonar.server.organization.BillingValidationsProxy;
import org.sonar.server.organization.TestDefaultOrganizationProvider;
import org.sonar.server.permission.PermissionService;
import org.sonar.server.permission.index.FooIndexDefinition;
import org.sonar.server.tester.UserSessionRule;
import org.sonar.server.ws.TestRequest;
import org.sonar.server.ws.WsActionTester;


public class UpdateVisibilityActionTest {
    private static final String PARAM_VISIBILITY = "visibility";

    private static final String PARAM_PROJECT = "project";

    private static final String PUBLIC = "public";

    private static final String PRIVATE = "private";

    private static final Set<String> ORGANIZATION_PERMISSIONS_NAME_SET = Arrays.stream(OrganizationPermission.values()).map(OrganizationPermission::getKey).collect(MoreCollectors.toSet(OrganizationPermission.values().length));

    @Rule
    public DbTester dbTester = DbTester.create(INSTANCE);

    @Rule
    public EsTester es = EsTester.createCustom(new FooIndexDefinition());

    @Rule
    public UserSessionRule userSessionRule = UserSessionRule.standalone().logIn();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private ResourceTypes resourceTypes = new ResourceTypesRule().setRootQualifiers(PROJECT);

    private PermissionService permissionService = new org.sonar.server.permission.PermissionServiceImpl(resourceTypes);

    private final Set<String> PROJECT_PERMISSIONS_BUT_USER_AND_CODEVIEWER = permissionService.getAllProjectPermissions().stream().filter(( perm) -> (!(perm.equals(UserRole.USER))) && (!(perm.equals(UserRole.CODEVIEWER)))).collect(MoreCollectors.toSet(((permissionService.getAllProjectPermissions().size()) - 2)));

    private DbClient dbClient = dbTester.getDbClient();

    private DbSession dbSession = dbTester.getSession();

    private TestProjectIndexers projectIndexers = new TestProjectIndexers();

    private BillingValidationsProxy billingValidations = Mockito.mock(BillingValidationsProxy.class);

    private ProjectsWsSupport wsSupport = new ProjectsWsSupport(dbClient, TestDefaultOrganizationProvider.from(dbTester), billingValidations);

    private UpdateVisibilityAction underTest = new UpdateVisibilityAction(dbClient, TestComponentFinder.from(dbTester), userSessionRule, projectIndexers, wsSupport);

    private WsActionTester ws = new WsActionTester(underTest);

    private final Random random = new Random();

    private final String randomVisibility = (random.nextBoolean()) ? UpdateVisibilityActionTest.PUBLIC : UpdateVisibilityActionTest.PRIVATE;

    private final TestRequest request = ws.newRequest();

    @Test
    public void definition() {
        WebService.Action definition = ws.getDef();
        assertThat(definition.key()).isEqualTo("update_visibility");
        assertThat(definition.isPost()).isTrue();
        assertThat(definition.since()).isEqualTo("6.4");
        assertThat(definition.params()).extracting(WebService.Param::key).containsExactlyInAnyOrder("project", "visibility");
    }

    @Test
    public void execute_fails_if_user_is_not_logged_in() {
        userSessionRule.anonymous();
        expectedException.expect(UnauthorizedException.class);
        expectedException.expectMessage("Authentication is required");
        request.execute();
    }

    @Test
    public void execute_fails_with_IAE_when_project_parameter_is_not_provided() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("The 'project' parameter is missing");
        request.execute();
    }

    @Test
    public void execute_fails_with_IAE_when_project_parameter_is_not_empty() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("The 'project' parameter is missing");
        request.execute();
    }

    @Test
    public void execute_fails_with_IAE_when_parameter_visibility_is_not_provided() {
        request.setParam(UpdateVisibilityActionTest.PARAM_PROJECT, "foo");
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("The 'visibility' parameter is missing");
        request.execute();
    }

    @Test
    public void execute_fails_with_IAE_when_parameter_visibility_is_empty() {
        request.setParam(UpdateVisibilityActionTest.PARAM_PROJECT, "foo").setParam(UpdateVisibilityActionTest.PARAM_VISIBILITY, "");
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage((("Value of parameter '" + (UpdateVisibilityActionTest.PARAM_VISIBILITY)) + "' () must be one of: [private, public]"));
        request.execute();
    }

    @Test
    public void execute_fails_with_IAE_when_value_of_parameter_visibility_is_not_lowercase() {
        request.setParam(UpdateVisibilityActionTest.PARAM_PROJECT, "foo");
        Stream.of("PUBLIC", "pUBliC", "PRIVATE", "PrIVAtE").forEach(( visibility) -> {
            try {
                request.setParam(UpdateVisibilityActionTest.PARAM_VISIBILITY, visibility).execute();
                fail("An exception should have been raised");
            } catch (IllegalArgumentException e) {
                assertThat(e.getMessage()).isEqualTo(String.format("Value of parameter '%s' (%s) must be one of: [private, public]", UpdateVisibilityActionTest.PARAM_VISIBILITY, visibility));
            }
        });
    }

    @Test
    public void execute_fails_with_NotFoundException_when_specified_component_does_not_exist() {
        request.setParam(UpdateVisibilityActionTest.PARAM_PROJECT, "foo").setParam(UpdateVisibilityActionTest.PARAM_VISIBILITY, randomVisibility);
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("Component key 'foo' not found");
        request.execute();
    }

    @Test
    public void execute_fails_with_BadRequestException_if_specified_component_is_neither_a_project_a_portfolio_nor_an_application() {
        OrganizationDto organization = dbTester.organizations().insert();
        ComponentDto project = randomPublicOrPrivateProject();
        ComponentDto module = ComponentTesting.newModuleDto(project);
        ComponentDto dir = ComponentTesting.newDirectory(project, "path");
        ComponentDto file = ComponentTesting.newFileDto(project);
        dbTester.components().insertComponents(module, dir, file);
        ComponentDto application = dbTester.components().insertApplication(organization);
        ComponentDto portfolio = dbTester.components().insertView(organization);
        ComponentDto subView = ComponentTesting.newSubView(portfolio);
        ComponentDto projectCopy = newProjectCopy("foo", project, subView);
        dbTester.components().insertComponents(subView, projectCopy);
        userSessionRule.addProjectPermission(ADMIN, project, portfolio, application);
        Stream.of(project, portfolio, application).forEach(( c) -> request.setParam(PARAM_PROJECT, c.getDbKey()).setParam(PARAM_VISIBILITY, randomVisibility).execute());
        Stream.of(module, dir, file, subView, projectCopy).forEach(( nonRootComponent) -> {
            request.setParam(PARAM_PROJECT, nonRootComponent.getDbKey()).setParam(PARAM_VISIBILITY, randomVisibility);
            try {
                request.execute();
                fail("a BadRequestException should have been raised");
            } catch ( e) {
                assertThat(e.getMessage()).isEqualTo("Component must be a project, a portfolio or an application");
            }
        });
    }

    @Test
    public void execute_throws_ForbiddenException_if_user_has_no_permission_on_specified_component() {
        OrganizationDto organization = dbTester.organizations().insert();
        ComponentDto project = dbTester.components().insertPrivateProject(organization);
        request.setParam(UpdateVisibilityActionTest.PARAM_PROJECT, project.getDbKey()).setParam(UpdateVisibilityActionTest.PARAM_VISIBILITY, randomVisibility);
        expectInsufficientPrivilegeException();
        request.execute();
    }

    @Test
    public void execute_throws_ForbiddenException_if_user_has_all_permissions_but_ADMIN_on_specified_component() {
        OrganizationDto organization = dbTester.organizations().insert();
        ComponentDto project = dbTester.components().insertPublicProject(organization);
        request.setParam(UpdateVisibilityActionTest.PARAM_PROJECT, project.getDbKey()).setParam(UpdateVisibilityActionTest.PARAM_VISIBILITY, randomVisibility);
        userSessionRule.addProjectPermission(ISSUE_ADMIN, project);
        Arrays.stream(OrganizationPermission.values()).forEach(( perm) -> userSessionRule.addPermission(perm, organization));
        request.setParam(UpdateVisibilityActionTest.PARAM_PROJECT, project.getDbKey()).setParam(UpdateVisibilityActionTest.PARAM_VISIBILITY, randomVisibility);
        expectInsufficientPrivilegeException();
        request.execute();
    }

    @Test
    public void execute_throws_BadRequestException_if_specified_component_has_pending_tasks() {
        ComponentDto project = randomPublicOrPrivateProject();
        IntStream.range(0, (1 + (Math.abs(random.nextInt(5))))).forEach(( i) -> insertPendingTask(project));
        request.setParam(UpdateVisibilityActionTest.PARAM_PROJECT, project.getDbKey()).setParam(UpdateVisibilityActionTest.PARAM_VISIBILITY, randomVisibility);
        userSessionRule.addProjectPermission(ADMIN, project);
        expectedException.expect(BadRequestException.class);
        expectedException.expectMessage("Component visibility can't be changed as long as it has background task(s) pending or in progress");
        request.execute();
    }

    @Test
    public void execute_throws_BadRequestException_if_main_component_of_specified_component_has_in_progress_tasks() {
        ComponentDto project = randomPublicOrPrivateProject();
        IntStream.range(0, (1 + (Math.abs(random.nextInt(5))))).forEach(( i) -> insertInProgressTask(project));
        request.setParam(UpdateVisibilityActionTest.PARAM_PROJECT, project.getDbKey()).setParam(UpdateVisibilityActionTest.PARAM_VISIBILITY, randomVisibility);
        userSessionRule.addProjectPermission(ADMIN, project);
        expectedException.expect(BadRequestException.class);
        expectedException.expectMessage("Component visibility can't be changed as long as it has background task(s) pending or in progress");
        request.execute();
    }

    @Test
    public void execute_throws_ISE_when_project_organization_uuid_does_not_match_existing_organization() {
        // Organization is not persisted
        OrganizationDto organization = OrganizationTesting.newOrganizationDto();
        ComponentDto project = dbTester.components().insertPublicProject(organization);
        userSessionRule.addProjectPermission(ADMIN, project);
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage(String.format("Could not find organization with uuid '%s' of project '%s'", organization.getUuid(), project.getDbKey()));
        request.setParam(UpdateVisibilityActionTest.PARAM_PROJECT, project.getDbKey()).setParam(UpdateVisibilityActionTest.PARAM_VISIBILITY, UpdateVisibilityActionTest.PRIVATE).execute();
    }

    @Test
    public void execute_changes_private_flag_of_specified_project_and_all_children_to_specified_new_visibility() {
        ComponentDto project = randomPublicOrPrivateProject();
        boolean initiallyPrivate = project.isPrivate();
        BranchDto branchDto = ComponentTesting.newBranchDto(project);
        dbClient.branchDao().insert(dbSession, branchDto);
        ComponentDto branch = ComponentTesting.newProjectBranch(project, branchDto);
        ComponentDto module = ComponentTesting.newModuleDto(project);
        ComponentDto dir = ComponentTesting.newDirectory(project, "path");
        ComponentDto file = ComponentTesting.newFileDto(project);
        dbTester.components().insertComponents(branch, module, dir, file);
        userSessionRule.addProjectPermission(ADMIN, project);
        request.setParam(UpdateVisibilityActionTest.PARAM_PROJECT, project.getDbKey()).setParam(UpdateVisibilityActionTest.PARAM_VISIBILITY, (initiallyPrivate ? UpdateVisibilityActionTest.PUBLIC : UpdateVisibilityActionTest.PRIVATE)).execute();
        assertThat(isPrivateInDb(project)).isEqualTo((!initiallyPrivate));
        assertThat(isPrivateInDb(branch)).isEqualTo((!initiallyPrivate));
        assertThat(isPrivateInDb(module)).isEqualTo((!initiallyPrivate));
        assertThat(isPrivateInDb(dir)).isEqualTo((!initiallyPrivate));
        assertThat(isPrivateInDb(file)).isEqualTo((!initiallyPrivate));
    }

    @Test
    public void execute_has_no_effect_if_specified_project_already_has_specified_visibility() {
        ComponentDto project = randomPublicOrPrivateProject();
        boolean initiallyPrivate = project.isPrivate();
        BranchDto branchDto = ComponentTesting.newBranchDto(project);
        dbClient.branchDao().insert(dbSession, branchDto);
        ComponentDto branch = ComponentTesting.newProjectBranch(project, branchDto).setPrivate(initiallyPrivate);
        ComponentDto module = ComponentTesting.newModuleDto(project).setPrivate(initiallyPrivate);
        ComponentDto dir = // child is inconsistent with root (should not occur) and won't be fixed
        ComponentTesting.newDirectory(project, "path").setPrivate((!initiallyPrivate));
        ComponentDto file = ComponentTesting.newFileDto(project).setPrivate(initiallyPrivate);
        dbTester.components().insertComponents(branch, module, dir, file);
        userSessionRule.addProjectPermission(ADMIN, project);
        request.setParam(UpdateVisibilityActionTest.PARAM_PROJECT, project.getDbKey()).setParam(UpdateVisibilityActionTest.PARAM_VISIBILITY, (initiallyPrivate ? UpdateVisibilityActionTest.PRIVATE : UpdateVisibilityActionTest.PUBLIC)).execute();
        assertThat(isPrivateInDb(project)).isEqualTo(initiallyPrivate);
        assertThat(isPrivateInDb(branch)).isEqualTo(initiallyPrivate);
        assertThat(isPrivateInDb(module)).isEqualTo(initiallyPrivate);
        assertThat(isPrivateInDb(dir)).isEqualTo((!initiallyPrivate));
        assertThat(isPrivateInDb(file)).isEqualTo(initiallyPrivate);
    }

    @Test
    public void execute_deletes_all_permissions_to_Anyone_on_specified_project_when_new_visibility_is_private() {
        OrganizationDto organization = dbTester.organizations().insert();
        ComponentDto project = dbTester.components().insertPublicProject(organization);
        UserDto user = dbTester.users().insertUser();
        GroupDto group = dbTester.users().insertGroup(organization);
        unsafeGiveAllPermissionsToRootComponent(project, user, group, organization);
        userSessionRule.addProjectPermission(ADMIN, project);
        request.setParam(UpdateVisibilityActionTest.PARAM_PROJECT, project.getDbKey()).setParam(UpdateVisibilityActionTest.PARAM_VISIBILITY, UpdateVisibilityActionTest.PRIVATE).execute();
        verifyHasAllPermissionsButProjectPermissionsToGroupAnyOne(project, user, group);
    }

    @Test
    public void execute_does_not_delete_all_permissions_to_AnyOne_on_specified_project_if_already_private() {
        OrganizationDto organization = dbTester.organizations().insert();
        ComponentDto project = dbTester.components().insertPrivateProject(organization);
        UserDto user = dbTester.users().insertUser();
        GroupDto group = dbTester.users().insertGroup(organization);
        unsafeGiveAllPermissionsToRootComponent(project, user, group, organization);
        userSessionRule.addProjectPermission(ADMIN, project);
        request.setParam(UpdateVisibilityActionTest.PARAM_PROJECT, project.getDbKey()).setParam(UpdateVisibilityActionTest.PARAM_VISIBILITY, UpdateVisibilityActionTest.PRIVATE).execute();
        verifyStillHasAllPermissions(project, user, group);
    }

    @Test
    public void execute_deletes_all_permissions_USER_and_BROWSE_of_specified_project_when_new_visibility_is_public() {
        OrganizationDto organization = dbTester.organizations().insert();
        ComponentDto project = dbTester.components().insertPrivateProject(organization);
        UserDto user = dbTester.users().insertUser();
        GroupDto group = dbTester.users().insertGroup(organization);
        unsafeGiveAllPermissionsToRootComponent(project, user, group, organization);
        userSessionRule.addProjectPermission(ADMIN, project);
        request.setParam(UpdateVisibilityActionTest.PARAM_PROJECT, project.getDbKey()).setParam(UpdateVisibilityActionTest.PARAM_VISIBILITY, UpdateVisibilityActionTest.PUBLIC).execute();
        verifyHasAllPermissionsButProjectPermissionsUserAndBrowse(project, user, group);
    }

    @Test
    public void execute_does_not_delete_permissions_USER_and_BROWSE_of_specified_project_when_new_component_is_already_public() {
        OrganizationDto organization = dbTester.organizations().insert();
        ComponentDto project = dbTester.components().insertPublicProject(organization);
        UserDto user = dbTester.users().insertUser();
        GroupDto group = dbTester.users().insertGroup(organization);
        unsafeGiveAllPermissionsToRootComponent(project, user, group, organization);
        userSessionRule.addProjectPermission(ADMIN, project);
        request.setParam(UpdateVisibilityActionTest.PARAM_PROJECT, project.getDbKey()).setParam(UpdateVisibilityActionTest.PARAM_VISIBILITY, UpdateVisibilityActionTest.PUBLIC).execute();
        verifyStillHasAllPermissions(project, user, group);
    }

    @Test
    public void execute_updates_permission_of_specified_project_in_indexes_when_changing_visibility() {
        ComponentDto project = randomPublicOrPrivateProject();
        boolean initiallyPrivate = project.isPrivate();
        userSessionRule.addProjectPermission(ADMIN, project);
        request.setParam(UpdateVisibilityActionTest.PARAM_PROJECT, project.getDbKey()).setParam(UpdateVisibilityActionTest.PARAM_VISIBILITY, (initiallyPrivate ? UpdateVisibilityActionTest.PUBLIC : UpdateVisibilityActionTest.PRIVATE)).execute();
        assertThat(projectIndexers.hasBeenCalled(project.uuid(), PERMISSION_CHANGE)).isTrue();
    }

    @Test
    public void execute_does_not_update_permission_of_specified_project_in_indexes_if_already_has_specified_visibility() {
        ComponentDto project = randomPublicOrPrivateProject();
        boolean initiallyPrivate = project.isPrivate();
        userSessionRule.addProjectPermission(ADMIN, project);
        request.setParam(UpdateVisibilityActionTest.PARAM_PROJECT, project.getDbKey()).setParam(UpdateVisibilityActionTest.PARAM_VISIBILITY, (initiallyPrivate ? UpdateVisibilityActionTest.PRIVATE : UpdateVisibilityActionTest.PUBLIC)).execute();
        assertThat(projectIndexers.hasBeenCalled(project.uuid())).isFalse();
    }

    @Test
    public void execute_grants_USER_and_CODEVIEWER_permissions_to_any_user_with_at_least_one_permission_when_making_project_private() {
        OrganizationDto organization = dbTester.organizations().insert();
        ComponentDto project = dbTester.components().insertPublicProject(organization);
        UserDto user1 = dbTester.users().insertUser();
        UserDto user2 = dbTester.users().insertUser();
        UserDto user3 = dbTester.users().insertUser();
        dbTester.users().insertProjectPermissionOnUser(user1, "p1", project);
        dbTester.users().insertProjectPermissionOnUser(user1, "p2", project);
        dbTester.users().insertProjectPermissionOnUser(user2, "p2", project);
        userSessionRule.addProjectPermission(ADMIN, project);
        request.setParam(UpdateVisibilityActionTest.PARAM_PROJECT, project.getDbKey()).setParam(UpdateVisibilityActionTest.PARAM_VISIBILITY, UpdateVisibilityActionTest.PRIVATE).execute();
        assertThat(dbClient.userPermissionDao().selectProjectPermissionsOfUser(dbSession, user1.getId(), project.getId())).containsOnly(USER, CODEVIEWER, "p1", "p2");
        assertThat(dbClient.userPermissionDao().selectProjectPermissionsOfUser(dbSession, user2.getId(), project.getId())).containsOnly(USER, CODEVIEWER, "p2");
        assertThat(dbClient.userPermissionDao().selectProjectPermissionsOfUser(dbSession, user3.getId(), project.getId())).isEmpty();
    }

    @Test
    public void execute_grants_USER_and_CODEVIEWER_permissions_to_any_group_with_at_least_one_permission_when_making_project_private() {
        OrganizationDto organization = dbTester.organizations().insert();
        ComponentDto project = dbTester.components().insertPublicProject(organization);
        GroupDto group1 = dbTester.users().insertGroup(organization);
        GroupDto group2 = dbTester.users().insertGroup(organization);
        GroupDto group3 = dbTester.users().insertGroup(organization);
        dbTester.users().insertProjectPermissionOnGroup(group1, "p1", project);
        dbTester.users().insertProjectPermissionOnGroup(group1, "p2", project);
        dbTester.users().insertProjectPermissionOnGroup(group2, "p2", project);
        userSessionRule.addProjectPermission(ADMIN, project);
        request.setParam(UpdateVisibilityActionTest.PARAM_PROJECT, project.getDbKey()).setParam(UpdateVisibilityActionTest.PARAM_VISIBILITY, UpdateVisibilityActionTest.PRIVATE).execute();
        assertThat(dbClient.groupPermissionDao().selectProjectPermissionsOfGroup(dbSession, organization.getUuid(), group1.getId(), project.getId())).containsOnly(USER, CODEVIEWER, "p1", "p2");
        assertThat(dbClient.groupPermissionDao().selectProjectPermissionsOfGroup(dbSession, organization.getUuid(), group2.getId(), project.getId())).containsOnly(USER, CODEVIEWER, "p2");
        assertThat(dbClient.groupPermissionDao().selectProjectPermissionsOfGroup(dbSession, organization.getUuid(), group3.getId(), project.getId())).isEmpty();
    }

    @Test
    public void update_a_portfolio_to_private() {
        OrganizationDto organization = dbTester.organizations().insert();
        ComponentDto portfolio = dbTester.components().insertPublicPortfolio(organization);
        GroupDto group = dbTester.users().insertGroup(organization);
        dbTester.users().insertProjectPermissionOnGroup(group, ISSUE_ADMIN, portfolio);
        UserDto user = dbTester.users().insertUser();
        dbTester.users().insertProjectPermissionOnUser(user, ADMIN, portfolio);
        userSessionRule.addProjectPermission(ADMIN, portfolio);
        request.setParam(UpdateVisibilityActionTest.PARAM_PROJECT, portfolio.getDbKey()).setParam(UpdateVisibilityActionTest.PARAM_VISIBILITY, UpdateVisibilityActionTest.PRIVATE).execute();
        assertThat(dbClient.componentDao().selectByUuid(dbSession, portfolio.uuid()).get().isPrivate()).isTrue();
        assertThat(dbClient.groupPermissionDao().selectProjectPermissionsOfGroup(dbSession, organization.getUuid(), group.getId(), portfolio.getId())).containsOnly(USER, CODEVIEWER, ISSUE_ADMIN);
        assertThat(dbClient.userPermissionDao().selectProjectPermissionsOfUser(dbSession, user.getId(), portfolio.getId())).containsOnly(USER, CODEVIEWER, ADMIN);
    }

    @Test
    public void update_a_portfolio_to_public() {
        OrganizationDto organization = dbTester.organizations().insert();
        ComponentDto portfolio = dbTester.components().insertPrivatePortfolio(organization);
        userSessionRule.addProjectPermission(ADMIN, portfolio);
        GroupDto group = dbTester.users().insertGroup(organization);
        dbTester.users().insertProjectPermissionOnGroup(group, ISSUE_ADMIN, portfolio);
        dbTester.users().insertProjectPermissionOnGroup(group, USER, portfolio);
        dbTester.users().insertProjectPermissionOnGroup(group, CODEVIEWER, portfolio);
        UserDto user = dbTester.users().insertUser();
        dbTester.users().insertProjectPermissionOnUser(user, ADMIN, portfolio);
        dbTester.users().insertProjectPermissionOnUser(user, USER, portfolio);
        dbTester.users().insertProjectPermissionOnUser(user, CODEVIEWER, portfolio);
        request.setParam(UpdateVisibilityActionTest.PARAM_PROJECT, portfolio.getDbKey()).setParam(UpdateVisibilityActionTest.PARAM_VISIBILITY, UpdateVisibilityActionTest.PUBLIC).execute();
        assertThat(dbClient.componentDao().selectByUuid(dbSession, portfolio.uuid()).get().isPrivate()).isFalse();
        assertThat(dbClient.groupPermissionDao().selectProjectPermissionsOfGroup(dbSession, organization.getUuid(), group.getId(), portfolio.getId())).containsOnly(ISSUE_ADMIN);
        assertThat(dbClient.userPermissionDao().selectProjectPermissionsOfUser(dbSession, user.getId(), portfolio.getId())).containsOnly(ADMIN);
    }

    @Test
    public void update_an_application_to_private() {
        OrganizationDto organization = dbTester.organizations().insert();
        ComponentDto application = dbTester.components().insertPublicApplication(organization);
        GroupDto group = dbTester.users().insertGroup(organization);
        dbTester.users().insertProjectPermissionOnGroup(group, ISSUE_ADMIN, application);
        UserDto user = dbTester.users().insertUser();
        dbTester.users().insertProjectPermissionOnUser(user, ADMIN, application);
        userSessionRule.addProjectPermission(ADMIN, application);
        request.setParam(UpdateVisibilityActionTest.PARAM_PROJECT, application.getDbKey()).setParam(UpdateVisibilityActionTest.PARAM_VISIBILITY, UpdateVisibilityActionTest.PRIVATE).execute();
        assertThat(dbClient.componentDao().selectByUuid(dbSession, application.uuid()).get().isPrivate()).isTrue();
        assertThat(dbClient.groupPermissionDao().selectProjectPermissionsOfGroup(dbSession, organization.getUuid(), group.getId(), application.getId())).containsOnly(USER, CODEVIEWER, ISSUE_ADMIN);
        assertThat(dbClient.userPermissionDao().selectProjectPermissionsOfUser(dbSession, user.getId(), application.getId())).containsOnly(USER, CODEVIEWER, ADMIN);
    }

    @Test
    public void update_an_application_to_public() {
        OrganizationDto organization = dbTester.organizations().insert();
        ComponentDto portfolio = dbTester.components().insertPrivateApplication(organization);
        userSessionRule.addProjectPermission(ADMIN, portfolio);
        GroupDto group = dbTester.users().insertGroup(organization);
        dbTester.users().insertProjectPermissionOnGroup(group, ISSUE_ADMIN, portfolio);
        dbTester.users().insertProjectPermissionOnGroup(group, USER, portfolio);
        dbTester.users().insertProjectPermissionOnGroup(group, CODEVIEWER, portfolio);
        UserDto user = dbTester.users().insertUser();
        dbTester.users().insertProjectPermissionOnUser(user, ADMIN, portfolio);
        dbTester.users().insertProjectPermissionOnUser(user, USER, portfolio);
        dbTester.users().insertProjectPermissionOnUser(user, CODEVIEWER, portfolio);
        request.setParam(UpdateVisibilityActionTest.PARAM_PROJECT, portfolio.getDbKey()).setParam(UpdateVisibilityActionTest.PARAM_VISIBILITY, UpdateVisibilityActionTest.PUBLIC).execute();
        assertThat(dbClient.componentDao().selectByUuid(dbSession, portfolio.uuid()).get().isPrivate()).isFalse();
        assertThat(dbClient.groupPermissionDao().selectProjectPermissionsOfGroup(dbSession, organization.getUuid(), group.getId(), portfolio.getId())).containsOnly(ISSUE_ADMIN);
        assertThat(dbClient.userPermissionDao().selectProjectPermissionsOfUser(dbSession, user.getId(), portfolio.getId())).containsOnly(ADMIN);
    }

    @Test
    public void fail_to_update_visibility_to_private_when_organization_is_not_allowed_to_use_private_projects() {
        OrganizationDto organization = dbTester.organizations().insert();
        ComponentDto project = dbTester.components().insertPublicProject(organization);
        dbTester.organizations().setNewProjectPrivate(organization, true);
        userSessionRule.addProjectPermission(ADMIN, project);
        Mockito.doThrow(new BillingValidations.BillingValidationsException("This organization cannot use project private")).when(billingValidations).checkCanUpdateProjectVisibility(ArgumentMatchers.any(Organization.class), ArgumentMatchers.eq(true));
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("This organization cannot use project private");
        request.setParam(UpdateVisibilityActionTest.PARAM_PROJECT, project.getDbKey()).setParam(UpdateVisibilityActionTest.PARAM_VISIBILITY, UpdateVisibilityActionTest.PRIVATE).execute();
    }

    @Test
    public void does_not_fail_to_update_visibility_to_public_when_organization_is_not_allowed_to_use_private_projects() {
        OrganizationDto organization = dbTester.organizations().insert();
        ComponentDto project = dbTester.components().insertPublicProject(organization);
        dbTester.organizations().setNewProjectPrivate(organization, true);
        userSessionRule.addProjectPermission(ADMIN, project);
        Mockito.doThrow(new BillingValidations.BillingValidationsException("This organization cannot use project private")).when(billingValidations).checkCanUpdateProjectVisibility(ArgumentMatchers.any(Organization.class), ArgumentMatchers.eq(true));
        request.setParam(UpdateVisibilityActionTest.PARAM_PROJECT, project.getDbKey()).setParam(UpdateVisibilityActionTest.PARAM_VISIBILITY, UpdateVisibilityActionTest.PUBLIC).execute();
    }

    @Test
    public void fail_when_using_branch_db_key() throws Exception {
        ComponentDto project = dbTester.components().insertMainBranch();
        userSessionRule.logIn().addProjectPermission(USER, project);
        ComponentDto branch = dbTester.components().insertProjectBranch(project);
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage(String.format("Component key '%s' not found", branch.getDbKey()));
        request.setParam(UpdateVisibilityActionTest.PARAM_PROJECT, branch.getDbKey()).setParam(UpdateVisibilityActionTest.PARAM_VISIBILITY, UpdateVisibilityActionTest.PUBLIC).execute();
    }

    private int counter = 0;
}

