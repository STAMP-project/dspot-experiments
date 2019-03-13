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


import BranchSupport.ComponentKey;
import Qualifiers.PROJECT;
import System2.INSTANCE;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.sonar.ce.queue.CeQueue;
import org.sonar.ce.queue.CeQueueImpl;
import org.sonar.core.component.ComponentKeys;
import org.sonar.db.DbSession;
import org.sonar.db.DbTester;
import org.sonar.db.component.BranchDto;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.db.user.UserDto;
import org.sonar.server.component.ComponentUpdater;
import org.sonar.server.exceptions.ForbiddenException;
import org.sonar.server.favorite.FavoriteUpdater;
import org.sonar.server.permission.PermissionTemplateService;
import org.sonar.server.tester.UserSessionRule;


/**
 * Tests of {@link ReportSubmitter} when branch support is installed.
 */
@RunWith(DataProviderRunner.class)
public class BranchReportSubmitterTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public UserSessionRule userSession = UserSessionRule.standalone();

    @Rule
    public DbTester db = DbTester.create(INSTANCE);

    private CeQueue queue = Mockito.mock(CeQueueImpl.class);

    private ComponentUpdater componentUpdater = Mockito.mock(ComponentUpdater.class);

    private PermissionTemplateService permissionTemplateService = Mockito.mock(PermissionTemplateService.class);

    private FavoriteUpdater favoriteUpdater = Mockito.mock(FavoriteUpdater.class);

    private BranchSupportDelegate branchSupportDelegate = Mockito.mock(BranchSupportDelegate.class);

    private BranchSupport branchSupport = Mockito.spy(new BranchSupport(branchSupportDelegate));

    private ReportSubmitter underTest = new ReportSubmitter(queue, userSession, componentUpdater, permissionTemplateService, db.getDbClient(), branchSupport);

    @Test
    public void submit_does_not_use_delegate_if_characteristics_are_empty() {
        OrganizationDto organization = db.organizations().insert();
        ComponentDto project = db.components().insertMainBranch(organization);
        UserDto user = db.users().insertUser();
        userSession.logIn(user).addProjectPermission(SCAN_EXECUTION, project);
        mockSuccessfulPrepareSubmitCall();
        InputStream reportInput = IOUtils.toInputStream("{binary}", StandardCharsets.UTF_8);
        underTest.submit(organization.getKey(), project.getDbKey(), null, project.name(), Collections.emptyMap(), reportInput);
        Mockito.verifyZeroInteractions(branchSupportDelegate);
    }

    @Test
    public void submit_a_report_on_existing_branch() {
        OrganizationDto organization = db.organizations().insert();
        ComponentDto project = db.components().insertMainBranch(organization);
        ComponentDto branch = db.components().insertProjectBranch(project);
        UserDto user = db.users().insertUser();
        userSession.logIn(user).addProjectPermission(SCAN_EXECUTION, project);
        Map<String, String> randomCharacteristics = BranchReportSubmitterTest.randomNonEmptyMap();
        BranchSupport.ComponentKey componentKey = BranchReportSubmitterTest.createComponentKeyOfBranch(branch);
        Mockito.when(branchSupportDelegate.createComponentKey(project.getDbKey(), randomCharacteristics)).thenReturn(componentKey);
        InputStream reportInput = IOUtils.toInputStream("{binary}", StandardCharsets.UTF_8);
        String taskUuid = mockSuccessfulPrepareSubmitCall();
        underTest.submit(organization.getKey(), project.getDbKey(), null, project.name(), randomCharacteristics, reportInput);
        Mockito.verifyZeroInteractions(permissionTemplateService);
        Mockito.verifyZeroInteractions(favoriteUpdater);
        Mockito.verify(branchSupport, Mockito.times(0)).createBranchComponent(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(branchSupportDelegate).createComponentKey(project.getDbKey(), randomCharacteristics);
        Mockito.verify(branchSupportDelegate, Mockito.times(0)).createBranchComponent(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verifyNoMoreInteractions(branchSupportDelegate);
        verifyQueueSubmit(project, branch, user, randomCharacteristics, taskUuid);
    }

    @Test
    public void submit_a_report_on_existing_deprecated_branch() {
        OrganizationDto organization = db.organizations().insert();
        String projectKey = randomAlphabetic(10);
        String deprecatedBranchName = randomAlphabetic(11);
        ComponentDto deprecatedBranch = db.components().insertMainBranch(organization, ( cpt) -> cpt.setDbKey(ComponentKeys.createKey(projectKey, deprecatedBranchName)));
        UserDto user = db.users().insertUser();
        userSession.logIn(user).addProjectPermission(SCAN_EXECUTION, deprecatedBranch);
        Map<String, String> noCharacteristics = Collections.emptyMap();
        InputStream reportInput = IOUtils.toInputStream("{binary}", StandardCharsets.UTF_8);
        String taskUuid = mockSuccessfulPrepareSubmitCall();
        underTest.submit(organization.getKey(), deprecatedBranch.getDbKey(), null, deprecatedBranch.name(), noCharacteristics, reportInput);
        Mockito.verifyZeroInteractions(permissionTemplateService);
        Mockito.verifyZeroInteractions(favoriteUpdater);
        Mockito.verify(branchSupport, Mockito.times(0)).createBranchComponent(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verifyZeroInteractions(branchSupportDelegate);
        verifyQueueSubmit(deprecatedBranch, deprecatedBranch, user, noCharacteristics, taskUuid);
    }

    @Test
    public void submit_a_report_on_missing_branch_but_existing_project() {
        OrganizationDto organization = db.organizations().insert();
        ComponentDto existingProject = db.components().insertMainBranch(organization);
        BranchDto exitingProjectMainBranch = db.getDbClient().branchDao().selectByUuid(db.getSession(), existingProject.uuid()).get();
        UserDto user = db.users().insertUser();
        userSession.logIn(user).addProjectPermission(SCAN_EXECUTION, existingProject);
        Map<String, String> randomCharacteristics = BranchReportSubmitterTest.randomNonEmptyMap();
        ComponentDto createdBranch = BranchReportSubmitterTest.createButDoNotInsertBranch(existingProject);
        BranchSupport.ComponentKey componentKey = BranchReportSubmitterTest.createComponentKeyOfBranch(createdBranch);
        Mockito.when(branchSupportDelegate.createComponentKey(existingProject.getDbKey(), randomCharacteristics)).thenReturn(componentKey);
        Mockito.when(branchSupportDelegate.createBranchComponent(ArgumentMatchers.any(DbSession.class), ArgumentMatchers.same(componentKey), ArgumentMatchers.eq(organization), ArgumentMatchers.eq(existingProject), ArgumentMatchers.eq(exitingProjectMainBranch))).thenReturn(createdBranch);
        InputStream reportInput = IOUtils.toInputStream("{binary}", StandardCharsets.UTF_8);
        String taskUuid = mockSuccessfulPrepareSubmitCall();
        underTest.submit(organization.getKey(), existingProject.getDbKey(), null, existingProject.name(), randomCharacteristics, reportInput);
        Mockito.verifyZeroInteractions(permissionTemplateService);
        Mockito.verifyZeroInteractions(favoriteUpdater);
        Mockito.verify(branchSupport).createBranchComponent(ArgumentMatchers.any(DbSession.class), ArgumentMatchers.same(componentKey), ArgumentMatchers.eq(organization), ArgumentMatchers.eq(existingProject), ArgumentMatchers.eq(exitingProjectMainBranch));
        Mockito.verify(branchSupportDelegate).createComponentKey(existingProject.getDbKey(), randomCharacteristics);
        Mockito.verify(branchSupportDelegate).createBranchComponent(ArgumentMatchers.any(DbSession.class), ArgumentMatchers.same(componentKey), ArgumentMatchers.eq(organization), ArgumentMatchers.eq(existingProject), ArgumentMatchers.eq(exitingProjectMainBranch));
        Mockito.verifyNoMoreInteractions(branchSupportDelegate);
        Mockito.verify(componentUpdater, Mockito.times(0)).commitAndIndex(ArgumentMatchers.any(), ArgumentMatchers.any());
        verifyQueueSubmit(existingProject, createdBranch, user, randomCharacteristics, taskUuid);
    }

    @Test
    public void submit_report_on_missing_branch_of_missing_project_provisions_project_when_org_PROVISION_PROJECT_perm() {
        OrganizationDto organization = db.organizations().insert();
        ComponentDto nonExistingProject = newPrivateProjectDto(organization);
        UserDto user = db.users().insertUser();
        userSession.logIn(user).addPermission(PROVISION_PROJECTS, organization).addPermission(SCAN, organization);
        Map<String, String> randomCharacteristics = BranchReportSubmitterTest.randomNonEmptyMap();
        ComponentDto createdBranch = BranchReportSubmitterTest.createButDoNotInsertBranch(nonExistingProject);
        BranchSupport.ComponentKey componentKey = BranchReportSubmitterTest.createComponentKeyOfBranch(createdBranch);
        Mockito.when(branchSupportDelegate.createComponentKey(nonExistingProject.getDbKey(), randomCharacteristics)).thenReturn(componentKey);
        Mockito.when(componentUpdater.createWithoutCommit(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.eq(user.getId()))).thenAnswer(((Answer<ComponentDto>) (( invocation) -> db.components().insertMainBranch(nonExistingProject))));
        Mockito.when(branchSupportDelegate.createBranchComponent(ArgumentMatchers.any(DbSession.class), ArgumentMatchers.same(componentKey), ArgumentMatchers.eq(organization), ArgumentMatchers.eq(nonExistingProject), ArgumentMatchers.any())).thenReturn(createdBranch);
        Mockito.when(permissionTemplateService.wouldUserHaveScanPermissionWithDefaultTemplate(ArgumentMatchers.any(), ArgumentMatchers.eq(organization.getUuid()), ArgumentMatchers.any(), ArgumentMatchers.eq(nonExistingProject.getKey()), ArgumentMatchers.eq(PROJECT))).thenReturn(true);
        String taskUuid = mockSuccessfulPrepareSubmitCall();
        InputStream reportInput = IOUtils.toInputStream("{binary}", StandardCharsets.UTF_8);
        underTest.submit(organization.getKey(), nonExistingProject.getDbKey(), null, nonExistingProject.name(), randomCharacteristics, reportInput);
        BranchDto exitingProjectMainBranch = db.getDbClient().branchDao().selectByUuid(db.getSession(), nonExistingProject.uuid()).get();
        Mockito.verify(branchSupport).createBranchComponent(ArgumentMatchers.any(DbSession.class), ArgumentMatchers.same(componentKey), ArgumentMatchers.eq(organization), ArgumentMatchers.eq(nonExistingProject), ArgumentMatchers.eq(exitingProjectMainBranch));
        Mockito.verify(branchSupportDelegate).createComponentKey(nonExistingProject.getDbKey(), randomCharacteristics);
        Mockito.verify(branchSupportDelegate).createBranchComponent(ArgumentMatchers.any(DbSession.class), ArgumentMatchers.same(componentKey), ArgumentMatchers.eq(organization), ArgumentMatchers.eq(nonExistingProject), ArgumentMatchers.eq(exitingProjectMainBranch));
        Mockito.verifyNoMoreInteractions(branchSupportDelegate);
        verifyQueueSubmit(nonExistingProject, createdBranch, user, randomCharacteristics, taskUuid);
        Mockito.verify(componentUpdater).commitAndIndex(ArgumentMatchers.any(DbSession.class), ArgumentMatchers.eq(nonExistingProject));
    }

    @Test
    public void submit_fails_if_branch_support_delegate_createComponentKey_throws_an_exception() {
        OrganizationDto organization = db.organizations().insert();
        ComponentDto project = db.components().insertMainBranch(organization);
        UserDto user = db.users().insertUser();
        userSession.logIn(user).addProjectPermission(SCAN_EXECUTION, project);
        Map<String, String> randomCharacteristics = BranchReportSubmitterTest.randomNonEmptyMap();
        InputStream reportInput = IOUtils.toInputStream("{binary}", StandardCharsets.UTF_8);
        RuntimeException expected = new RuntimeException("Faking an exception thrown by branchSupportDelegate");
        Mockito.when(branchSupportDelegate.createComponentKey(ArgumentMatchers.any(), ArgumentMatchers.any())).thenThrow(expected);
        try {
            underTest.submit(organization.getKey(), project.getDbKey(), null, project.name(), randomCharacteristics, reportInput);
            Assert.fail("exception should have been thrown");
        } catch (Exception e) {
            assertThat(e).isSameAs(expected);
        }
    }

    @Test
    public void submit_report_on_missing_branch_of_missing_project_fails_with_ForbiddenException_if_only_scan_permission() {
        OrganizationDto organization = db.organizations().insert();
        ComponentDto nonExistingProject = newPrivateProjectDto(organization);
        UserDto user = db.users().insertUser();
        userSession.logIn(user).addProjectPermission(SCAN_EXECUTION, nonExistingProject);
        Map<String, String> randomCharacteristics = BranchReportSubmitterTest.randomNonEmptyMap();
        ComponentDto createdBranch = BranchReportSubmitterTest.createButDoNotInsertBranch(nonExistingProject);
        BranchSupport.ComponentKey componentKey = BranchReportSubmitterTest.createComponentKeyOfBranch(createdBranch);
        Mockito.when(branchSupportDelegate.createComponentKey(nonExistingProject.getDbKey(), randomCharacteristics)).thenReturn(componentKey);
        Mockito.when(branchSupportDelegate.createBranchComponent(ArgumentMatchers.any(DbSession.class), ArgumentMatchers.same(componentKey), ArgumentMatchers.eq(organization), ArgumentMatchers.eq(nonExistingProject), ArgumentMatchers.any())).thenReturn(createdBranch);
        InputStream reportInput = IOUtils.toInputStream("{binary}", StandardCharsets.UTF_8);
        expectedException.expect(ForbiddenException.class);
        expectedException.expectMessage("Insufficient privileges");
        underTest.submit(organization.getKey(), nonExistingProject.getDbKey(), null, nonExistingProject.name(), randomCharacteristics, reportInput);
    }
}

