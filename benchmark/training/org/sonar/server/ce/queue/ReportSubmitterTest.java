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
package org.sonar.server.ce.queue;


import OrganizationPermission.SCAN;
import Qualifiers.PROJECT;
import System2.INSTANCE;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.stream.IntStream;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.ce.queue.CeQueue;
import org.sonar.ce.queue.CeQueueImpl;
import org.sonar.ce.queue.CeTaskSubmit;
import org.sonar.db.DbSession;
import org.sonar.db.DbTester;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.component.ComponentTesting;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.db.user.UserDto;
import org.sonar.server.component.ComponentUpdater;
import org.sonar.server.component.NewComponent;
import org.sonar.server.exceptions.BadRequestException;
import org.sonar.server.exceptions.ForbiddenException;
import org.sonar.server.exceptions.NotFoundException;
import org.sonar.server.favorite.FavoriteUpdater;
import org.sonar.server.permission.PermissionTemplateService;
import org.sonar.server.tester.UserSessionRule;


public class ReportSubmitterTest {
    private static final String PROJECT_KEY = "MY_PROJECT";

    private static final String PROJECT_UUID = "P1";

    private static final String PROJECT_NAME = "My Project";

    private static final String TASK_UUID = "TASK_1";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public UserSessionRule userSession = UserSessionRule.standalone();

    @Rule
    public DbTester db = DbTester.create(INSTANCE);

    private String defaultOrganizationKey;

    private String defaultOrganizationUuid;

    private CeQueue queue = Mockito.mock(CeQueueImpl.class);

    private ComponentUpdater componentUpdater = Mockito.mock(ComponentUpdater.class);

    private PermissionTemplateService permissionTemplateService = Mockito.mock(PermissionTemplateService.class);

    private FavoriteUpdater favoriteUpdater = Mockito.mock(FavoriteUpdater.class);

    private BranchSupport ossEditionBranchSupport = new BranchSupport();

    private ReportSubmitter underTest = new ReportSubmitter(queue, userSession, componentUpdater, permissionTemplateService, db.getDbClient(), ossEditionBranchSupport);

    @Test
    public void submit_with_characteristics_fails_with_ISE_when_no_branch_support_delegate() {
        userSession.addPermission(SCAN, db.getDefaultOrganization().getUuid()).addPermission(PROVISION_PROJECTS, db.getDefaultOrganization());
        ComponentDto project = newPrivateProjectDto(db.getDefaultOrganization(), ReportSubmitterTest.PROJECT_UUID).setDbKey(ReportSubmitterTest.PROJECT_KEY);
        mockSuccessfulPrepareSubmitCall();
        Mockito.when(componentUpdater.create(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(project);
        Mockito.when(permissionTemplateService.wouldUserHaveScanPermissionWithDefaultTemplate(ArgumentMatchers.any(), ArgumentMatchers.eq(defaultOrganizationUuid), ArgumentMatchers.any(), ArgumentMatchers.eq(ReportSubmitterTest.PROJECT_KEY), ArgumentMatchers.eq(PROJECT))).thenReturn(true);
        Map<String, String> nonEmptyCharacteristics = IntStream.range(0, (1 + (new Random().nextInt(5)))).boxed().collect(uniqueIndex(( i) -> randomAlphabetic((i + 10)), ( i) -> randomAlphabetic((i + 20))));
        InputStream reportInput = IOUtils.toInputStream("{binary}", StandardCharsets.UTF_8);
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Current edition does not support branch feature");
        underTest.submit(defaultOrganizationKey, ReportSubmitterTest.PROJECT_KEY, null, ReportSubmitterTest.PROJECT_NAME, nonEmptyCharacteristics, reportInput);
    }

    @Test
    public void submit_stores_report() {
        userSession.addPermission(SCAN, db.getDefaultOrganization().getUuid()).addPermission(PROVISION_PROJECTS, db.getDefaultOrganization());
        ComponentDto project = newPrivateProjectDto(db.getDefaultOrganization(), ReportSubmitterTest.PROJECT_UUID).setDbKey(ReportSubmitterTest.PROJECT_KEY);
        mockSuccessfulPrepareSubmitCall();
        Mockito.when(componentUpdater.createWithoutCommit(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(project);
        Mockito.when(permissionTemplateService.wouldUserHaveScanPermissionWithDefaultTemplate(ArgumentMatchers.any(), ArgumentMatchers.eq(defaultOrganizationUuid), ArgumentMatchers.any(), ArgumentMatchers.eq(ReportSubmitterTest.PROJECT_KEY), ArgumentMatchers.eq(PROJECT))).thenReturn(true);
        underTest.submit(defaultOrganizationKey, ReportSubmitterTest.PROJECT_KEY, null, ReportSubmitterTest.PROJECT_NAME, Collections.emptyMap(), IOUtils.toInputStream("{binary}", StandardCharsets.UTF_8));
        verifyReportIsPersisted(ReportSubmitterTest.TASK_UUID);
        Mockito.verify(componentUpdater).commitAndIndex(ArgumentMatchers.any(DbSession.class), ArgumentMatchers.eq(project));
    }

    @Test
    public void submit_a_report_on_existing_project() {
        ComponentDto project = db.components().insertPrivateProject(db.getDefaultOrganization());
        UserDto user = db.users().insertUser();
        userSession.logIn(user).addProjectPermission(SCAN_EXECUTION, project);
        mockSuccessfulPrepareSubmitCall();
        underTest.submit(defaultOrganizationKey, project.getDbKey(), null, project.name(), Collections.emptyMap(), IOUtils.toInputStream("{binary}", StandardCharsets.UTF_8));
        verifyReportIsPersisted(ReportSubmitterTest.TASK_UUID);
        Mockito.verifyZeroInteractions(permissionTemplateService);
        Mockito.verifyZeroInteractions(favoriteUpdater);
        Mockito.verify(queue).submit(ArgumentMatchers.argThat(( submit) -> (((submit.getType().equals(CeTaskTypes.REPORT)) && (submit.getComponent().filter(( cpt) -> (cpt.getUuid().equals(project.uuid())) && (cpt.getMainComponentUuid().equals(project.uuid()))).isPresent())) && (submit.getSubmitterUuid().equals(user.getUuid()))) && (submit.getUuid().equals(TASK_UUID))));
    }

    @Test
    public void provision_project_if_does_not_exist() {
        OrganizationDto organization = db.organizations().insert();
        userSession.addPermission(SCAN, organization.getUuid()).addPermission(PROVISION_PROJECTS, organization);
        mockSuccessfulPrepareSubmitCall();
        ComponentDto createdProject = newPrivateProjectDto(organization, ReportSubmitterTest.PROJECT_UUID).setDbKey(ReportSubmitterTest.PROJECT_KEY);
        Mockito.when(componentUpdater.createWithoutCommit(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.isNull())).thenReturn(createdProject);
        Mockito.when(permissionTemplateService.wouldUserHaveScanPermissionWithDefaultTemplate(ArgumentMatchers.any(), ArgumentMatchers.eq(organization.getUuid()), ArgumentMatchers.any(), ArgumentMatchers.eq(ReportSubmitterTest.PROJECT_KEY), ArgumentMatchers.eq(PROJECT))).thenReturn(true);
        Mockito.when(permissionTemplateService.hasDefaultTemplateWithPermissionOnProjectCreator(ArgumentMatchers.any(), ArgumentMatchers.eq(organization.getUuid()), ArgumentMatchers.any())).thenReturn(true);
        underTest.submit(organization.getKey(), ReportSubmitterTest.PROJECT_KEY, null, ReportSubmitterTest.PROJECT_NAME, Collections.emptyMap(), IOUtils.toInputStream("{binary}"));
        verifyReportIsPersisted(ReportSubmitterTest.TASK_UUID);
        Mockito.verify(queue).submit(ArgumentMatchers.argThat(( submit) -> ((submit.getType().equals(CeTaskTypes.REPORT)) && (submit.getComponent().filter(( cpt) -> (cpt.getUuid().equals(PROJECT_UUID)) && (cpt.getMainComponentUuid().equals(PROJECT_UUID))).isPresent())) && (submit.getUuid().equals(TASK_UUID))));
        Mockito.verify(componentUpdater).commitAndIndex(ArgumentMatchers.any(DbSession.class), ArgumentMatchers.eq(createdProject));
    }

    @Test
    public void no_favorite_when_no_project_creator_permission_on_permission_template() {
        userSession.addPermission(SCAN, db.getDefaultOrganization().getUuid()).addPermission(PROVISION_PROJECTS, db.getDefaultOrganization());
        ComponentDto createdProject = newPrivateProjectDto(db.getDefaultOrganization(), ReportSubmitterTest.PROJECT_UUID).setDbKey(ReportSubmitterTest.PROJECT_KEY);
        Mockito.when(componentUpdater.createWithoutCommit(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.isNull())).thenReturn(createdProject);
        Mockito.when(permissionTemplateService.wouldUserHaveScanPermissionWithDefaultTemplate(ArgumentMatchers.any(), ArgumentMatchers.eq(defaultOrganizationUuid), ArgumentMatchers.any(), ArgumentMatchers.eq(ReportSubmitterTest.PROJECT_KEY), ArgumentMatchers.eq(PROJECT))).thenReturn(true);
        Mockito.when(permissionTemplateService.hasDefaultTemplateWithPermissionOnProjectCreator(ArgumentMatchers.any(), ArgumentMatchers.eq(defaultOrganizationUuid), ArgumentMatchers.any())).thenReturn(false);
        mockSuccessfulPrepareSubmitCall();
        underTest.submit(defaultOrganizationKey, ReportSubmitterTest.PROJECT_KEY, null, ReportSubmitterTest.PROJECT_NAME, Collections.emptyMap(), IOUtils.toInputStream("{binary}"));
        Mockito.verifyZeroInteractions(favoriteUpdater);
        Mockito.verify(componentUpdater).commitAndIndex(ArgumentMatchers.any(DbSession.class), ArgumentMatchers.eq(createdProject));
    }

    @Test
    public void submit_a_report_on_new_project_with_scan_permission_on_organization() {
        userSession.addPermission(SCAN, db.getDefaultOrganization().getUuid()).addPermission(PROVISION_PROJECTS, db.getDefaultOrganization());
        ComponentDto project = newPrivateProjectDto(db.getDefaultOrganization(), ReportSubmitterTest.PROJECT_UUID).setDbKey(ReportSubmitterTest.PROJECT_KEY);
        mockSuccessfulPrepareSubmitCall();
        Mockito.when(componentUpdater.createWithoutCommit(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(project);
        Mockito.when(permissionTemplateService.wouldUserHaveScanPermissionWithDefaultTemplate(ArgumentMatchers.any(), ArgumentMatchers.eq(defaultOrganizationUuid), ArgumentMatchers.any(), ArgumentMatchers.eq(ReportSubmitterTest.PROJECT_KEY), ArgumentMatchers.eq(PROJECT))).thenReturn(true);
        underTest.submit(defaultOrganizationKey, ReportSubmitterTest.PROJECT_KEY, null, ReportSubmitterTest.PROJECT_NAME, Collections.emptyMap(), IOUtils.toInputStream("{binary}"));
        Mockito.verify(queue).submit(ArgumentMatchers.any(CeTaskSubmit.class));
        Mockito.verify(componentUpdater).commitAndIndex(ArgumentMatchers.any(DbSession.class), ArgumentMatchers.eq(project));
    }

    @Test
    public void user_with_scan_permission_on_organization_is_allowed_to_submit_a_report_on_existing_project() {
        OrganizationDto org = db.organizations().insert();
        ComponentDto project = db.components().insertPrivateProject(org);
        userSession.addPermission(SCAN, org);
        mockSuccessfulPrepareSubmitCall();
        underTest.submit(org.getKey(), project.getDbKey(), null, project.name(), Collections.emptyMap(), IOUtils.toInputStream("{binary}"));
        Mockito.verify(queue).submit(ArgumentMatchers.any(CeTaskSubmit.class));
    }

    @Test
    public void submit_a_report_on_existing_project_with_project_scan_permission() {
        ComponentDto project = db.components().insertPrivateProject(db.getDefaultOrganization());
        userSession.addProjectPermission(SCAN_EXECUTION, project);
        mockSuccessfulPrepareSubmitCall();
        underTest.submit(defaultOrganizationKey, project.getDbKey(), null, project.name(), Collections.emptyMap(), IOUtils.toInputStream("{binary}"));
        Mockito.verify(queue).submit(ArgumentMatchers.any(CeTaskSubmit.class));
    }

    /**
     * SONAR-8757
     */
    @Test
    public void project_branch_must_not_benefit_from_the_scan_permission_on_main_project() {
        String branchName = "branchFoo";
        ComponentDto mainProject = db.components().insertPrivateProject();
        userSession.addProjectPermission(GlobalPermissions.SCAN_EXECUTION, mainProject);
        // user does not have the "scan" permission on the branch, so it can't scan it
        ComponentDto branchProject = db.components().insertPrivateProject(( p) -> p.setDbKey((((mainProject.getDbKey()) + ":") + branchName)));
        expectedException.expect(ForbiddenException.class);
        underTest.submit(defaultOrganizationKey, mainProject.getDbKey(), branchName, ReportSubmitterTest.PROJECT_NAME, Collections.emptyMap(), IOUtils.toInputStream("{binary}"));
    }

    @Test
    public void fail_with_NotFoundException_if_organization_with_specified_key_does_not_exist() {
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("Organization with key 'fop' does not exist");
        /* method will fail before parameter is used */
        underTest.submit("fop", ReportSubmitterTest.PROJECT_KEY, null, null, Collections.emptyMap(), null);
    }

    @Test
    public void fail_with_organizationKey_does_not_match_organization_of_specified_component() {
        userSession.logIn().setRoot();
        OrganizationDto organization = db.organizations().insert();
        ComponentDto project = db.components().insertPrivateProject(organization);
        mockSuccessfulPrepareSubmitCall();
        underTest.submit(organization.getKey(), project.getDbKey(), null, project.name(), Collections.emptyMap(), IOUtils.toInputStream("{binary}"));
    }

    @Test
    public void fail_if_component_is_not_a_project() {
        ComponentDto component = db.components().insertPublicPortfolio(db.getDefaultOrganization());
        userSession.logIn().addProjectPermission(SCAN_EXECUTION, component);
        mockSuccessfulPrepareSubmitCall();
        expectedException.expect(BadRequestException.class);
        expectedException.expectMessage(String.format("Component '%s' is not a project", component.getKey()));
        underTest.submit(defaultOrganizationKey, component.getDbKey(), null, component.name(), Collections.emptyMap(), IOUtils.toInputStream("{binary}"));
    }

    @Test
    public void fail_if_project_key_already_exists_as_module() {
        ComponentDto project = db.components().insertPrivateProject(db.getDefaultOrganization());
        ComponentDto module = db.components().insertComponent(newModuleDto(project));
        userSession.logIn().addProjectPermission(SCAN_EXECUTION, project);
        mockSuccessfulPrepareSubmitCall();
        try {
            underTest.submit(defaultOrganizationKey, module.getDbKey(), null, module.name(), Collections.emptyMap(), IOUtils.toInputStream("{binary}"));
            Assert.fail();
        } catch (BadRequestException e) {
            assertThat(e.errors()).contains(String.format(("The project '%s' is already defined in SonarQube but as a module of project '%s'. " + "If you really want to stop directly analysing project '%s', please first delete it from SonarQube and then relaunch the analysis of project '%s'."), module.getKey(), project.getKey(), project.getKey(), module.getKey()));
        }
    }

    @Test
    public void fail_with_forbidden_exception_when_no_scan_permission() {
        expectedException.expect(ForbiddenException.class);
        underTest.submit(defaultOrganizationKey, ReportSubmitterTest.PROJECT_KEY, null, ReportSubmitterTest.PROJECT_NAME, Collections.emptyMap(), IOUtils.toInputStream("{binary}"));
    }

    @Test
    public void fail_with_forbidden_exception_on_new_project_when_only_project_scan_permission() {
        ComponentDto component = ComponentTesting.newPrivateProjectDto(db.getDefaultOrganization(), ReportSubmitterTest.PROJECT_UUID);
        userSession.addProjectPermission(SCAN_EXECUTION, component);
        mockSuccessfulPrepareSubmitCall();
        Mockito.when(componentUpdater.create(ArgumentMatchers.any(DbSession.class), ArgumentMatchers.any(NewComponent.class), ArgumentMatchers.eq(null))).thenReturn(new ComponentDto().setUuid(ReportSubmitterTest.PROJECT_UUID).setDbKey(ReportSubmitterTest.PROJECT_KEY));
        expectedException.expect(ForbiddenException.class);
        underTest.submit(defaultOrganizationKey, ReportSubmitterTest.PROJECT_KEY, null, ReportSubmitterTest.PROJECT_NAME, Collections.emptyMap(), IOUtils.toInputStream("{binary}"));
    }
}

