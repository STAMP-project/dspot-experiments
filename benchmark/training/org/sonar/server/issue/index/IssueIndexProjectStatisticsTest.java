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
package org.sonar.server.issue.index;


import Issue.RESOLUTION_FALSE_POSITIVE;
import Issue.RESOLUTION_FIXED;
import Issue.RESOLUTION_REMOVED;
import Issue.RESOLUTION_WONT_FIX;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.utils.System2;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.db.organization.OrganizationTesting;
import org.sonar.server.es.EsTester;
import org.sonar.server.issue.IssueDocTesting;
import org.sonar.server.permission.index.PermissionIndexerTester;
import org.sonar.server.permission.index.WebAuthorizationTypeSupport;
import org.sonar.server.tester.UserSessionRule;


public class IssueIndexProjectStatisticsTest {
    private System2 system2 = Mockito.mock(System2.class);

    @Rule
    public EsTester es = EsTester.create();

    @Rule
    public UserSessionRule userSessionRule = UserSessionRule.standalone();

    private IssueIndexer issueIndexer = new IssueIndexer(es.client(), null, new IssueIteratorFactory(null));

    private PermissionIndexerTester authorizationIndexer = new PermissionIndexerTester(es, issueIndexer);

    private IssueIndex underTest = new IssueIndex(es.client(), system2, userSessionRule, new WebAuthorizationTypeSupport(userSessionRule));

    @Test
    public void searchProjectStatistics_returns_empty_list_if_no_input() {
        List<ProjectStatistics> result = underTest.searchProjectStatistics(Collections.emptyList(), Collections.emptyList(), "unknownUser");
        assertThat(result).isEmpty();
    }

    @Test
    public void searchProjectStatistics_returns_empty_list_if_the_input_does_not_match_anything() {
        List<ProjectStatistics> result = underTest.searchProjectStatistics(Collections.singletonList("unknownProjectUuid"), Collections.singletonList(1111234567890L), "unknownUser");
        assertThat(result).isEmpty();
    }

    @Test
    public void searchProjectStatistics_returns_something() {
        OrganizationDto organization = OrganizationTesting.newOrganizationDto();
        ComponentDto project = newPrivateProjectDto(organization);
        String userUuid = randomAlphanumeric(40);
        long from = 1111234567890L;
        indexIssues(IssueDocTesting.newDoc("issue1", project).setAssigneeUuid(userUuid).setFuncCreationDate(new Date((from + 1L))));
        List<ProjectStatistics> result = underTest.searchProjectStatistics(Collections.singletonList(project.uuid()), Collections.singletonList(from), userUuid);
        assertThat(result).extracting(ProjectStatistics::getProjectUuid).containsExactly(project.uuid());
    }

    @Test
    public void searchProjectStatistics_does_not_return_results_if_assignee_does_not_match() {
        OrganizationDto org1 = OrganizationTesting.newOrganizationDto();
        ComponentDto project = newPrivateProjectDto(org1);
        String user1Uuid = randomAlphanumeric(40);
        String user2Uuid = randomAlphanumeric(40);
        long from = 1111234567890L;
        indexIssues(IssueDocTesting.newDoc("issue1", project).setAssigneeUuid(user1Uuid).setFuncCreationDate(new Date((from + 1L))));
        List<ProjectStatistics> result = underTest.searchProjectStatistics(Collections.singletonList(project.uuid()), Collections.singletonList(from), user2Uuid);
        assertThat(result).isEmpty();
    }

    @Test
    public void searchProjectStatistics_returns_results_if_assignee_matches() {
        OrganizationDto org1 = OrganizationTesting.newOrganizationDto();
        ComponentDto project = newPrivateProjectDto(org1);
        String user1Uuid = randomAlphanumeric(40);
        long from = 1111234567890L;
        indexIssues(IssueDocTesting.newDoc("issue1", project).setAssigneeUuid(user1Uuid).setFuncCreationDate(new Date((from + 1L))));
        List<ProjectStatistics> result = underTest.searchProjectStatistics(Collections.singletonList(project.uuid()), Collections.singletonList(from), user1Uuid);
        assertThat(result).extracting(ProjectStatistics::getProjectUuid).containsExactly(project.uuid());
    }

    @Test
    public void searchProjectStatistics_returns_results_if_functional_date_is_strictly_after_from_date() {
        OrganizationDto org1 = OrganizationTesting.newOrganizationDto();
        ComponentDto project = newPrivateProjectDto(org1);
        String userUuid = randomAlphanumeric(40);
        long from = 1111234567890L;
        indexIssues(IssueDocTesting.newDoc("issue1", project).setAssigneeUuid(userUuid).setFuncCreationDate(new Date((from + 1L))));
        List<ProjectStatistics> result = underTest.searchProjectStatistics(Collections.singletonList(project.uuid()), Collections.singletonList(from), userUuid);
        assertThat(result).extracting(ProjectStatistics::getProjectUuid).containsExactly(project.uuid());
    }

    @Test
    public void searchProjectStatistics_does_not_return_results_if_functional_date_is_same_as_from_date() {
        OrganizationDto org1 = OrganizationTesting.newOrganizationDto();
        ComponentDto project = newPrivateProjectDto(org1);
        String userUuid = randomAlphanumeric(40);
        long from = 1111234567890L;
        indexIssues(IssueDocTesting.newDoc("issue1", project).setAssigneeUuid(userUuid).setFuncCreationDate(new Date(from)));
        List<ProjectStatistics> result = underTest.searchProjectStatistics(Collections.singletonList(project.uuid()), Collections.singletonList(from), userUuid);
        assertThat(result).extracting(ProjectStatistics::getProjectUuid).containsExactly(project.uuid());
    }

    @Test
    public void searchProjectStatistics_does_not_return_resolved_issues() {
        OrganizationDto org1 = OrganizationTesting.newOrganizationDto();
        ComponentDto project = newPrivateProjectDto(org1);
        String userUuid = randomAlphanumeric(40);
        long from = 1111234567890L;
        indexIssues(IssueDocTesting.newDoc("issue1", project).setAssigneeUuid(userUuid).setFuncCreationDate(new Date((from + 1L))).setResolution(RESOLUTION_FALSE_POSITIVE), IssueDocTesting.newDoc("issue1", project).setAssigneeUuid(userUuid).setFuncCreationDate(new Date((from + 1L))).setResolution(RESOLUTION_FIXED), IssueDocTesting.newDoc("issue1", project).setAssigneeUuid(userUuid).setFuncCreationDate(new Date((from + 1L))).setResolution(RESOLUTION_REMOVED), IssueDocTesting.newDoc("issue1", project).setAssigneeUuid(userUuid).setFuncCreationDate(new Date((from + 1L))).setResolution(RESOLUTION_WONT_FIX));
        List<ProjectStatistics> result = underTest.searchProjectStatistics(Collections.singletonList(project.uuid()), Collections.singletonList(from), userUuid);
        assertThat(result).isEmpty();
    }

    @Test
    public void searchProjectStatistics_does_not_return_results_if_functional_date_is_before_from_date() {
        OrganizationDto org1 = OrganizationTesting.newOrganizationDto();
        ComponentDto project = newPrivateProjectDto(org1);
        String userUuid = randomAlphanumeric(40);
        long from = 1111234567890L;
        indexIssues(IssueDocTesting.newDoc("issue1", project).setAssigneeUuid(userUuid).setFuncCreationDate(new Date((from - 1000L))));
        List<ProjectStatistics> result = underTest.searchProjectStatistics(Collections.singletonList(project.uuid()), Collections.singletonList(from), userUuid);
        assertThat(result).isEmpty();
    }

    @Test
    public void searchProjectStatistics_returns_issue_count() {
        OrganizationDto org1 = OrganizationTesting.newOrganizationDto();
        ComponentDto project = newPrivateProjectDto(org1);
        String userUuid = randomAlphanumeric(40);
        long from = 1111234567890L;
        indexIssues(IssueDocTesting.newDoc("issue1", project).setAssigneeUuid(userUuid).setFuncCreationDate(new Date((from + 1L))), IssueDocTesting.newDoc("issue2", project).setAssigneeUuid(userUuid).setFuncCreationDate(new Date((from + 1L))), IssueDocTesting.newDoc("issue3", project).setAssigneeUuid(userUuid).setFuncCreationDate(new Date((from + 1L))));
        List<ProjectStatistics> result = underTest.searchProjectStatistics(Collections.singletonList(project.uuid()), Collections.singletonList(from), userUuid);
        assertThat(result).extracting(ProjectStatistics::getIssueCount).containsExactly(3L);
    }

    @Test
    public void searchProjectStatistics_returns_issue_count_for_multiple_projects() {
        OrganizationDto org1 = OrganizationTesting.newOrganizationDto();
        ComponentDto project1 = newPrivateProjectDto(org1);
        ComponentDto project2 = newPrivateProjectDto(org1);
        ComponentDto project3 = newPrivateProjectDto(org1);
        String userUuid = randomAlphanumeric(40);
        long from = 1111234567890L;
        indexIssues(IssueDocTesting.newDoc("issue1", project1).setAssigneeUuid(userUuid).setFuncCreationDate(new Date((from + 1L))), IssueDocTesting.newDoc("issue2", project1).setAssigneeUuid(userUuid).setFuncCreationDate(new Date((from + 1L))), IssueDocTesting.newDoc("issue3", project1).setAssigneeUuid(userUuid).setFuncCreationDate(new Date((from + 1L))), IssueDocTesting.newDoc("issue4", project3).setAssigneeUuid(userUuid).setFuncCreationDate(new Date((from + 1L))), IssueDocTesting.newDoc("issue5", project3).setAssigneeUuid(userUuid).setFuncCreationDate(new Date((from + 1L))));
        List<ProjectStatistics> result = underTest.searchProjectStatistics(Arrays.asList(project1.uuid(), project2.uuid(), project3.uuid()), Arrays.asList(from, from, from), userUuid);
        assertThat(result).extracting(ProjectStatistics::getProjectUuid, ProjectStatistics::getIssueCount).containsExactlyInAnyOrder(tuple(project1.uuid(), 3L), tuple(project3.uuid(), 2L));
    }

    @Test
    public void searchProjectStatistics_returns_max_date_for_multiple_projects() {
        OrganizationDto org1 = OrganizationTesting.newOrganizationDto();
        ComponentDto project1 = newPrivateProjectDto(org1);
        ComponentDto project2 = newPrivateProjectDto(org1);
        ComponentDto project3 = newPrivateProjectDto(org1);
        String userUuid = randomAlphanumeric(40);
        long from = 1111234567000L;
        indexIssues(IssueDocTesting.newDoc("issue1", project1).setAssigneeUuid(userUuid).setFuncCreationDate(new Date((from + 1000L))), IssueDocTesting.newDoc("issue2", project1).setAssigneeUuid(userUuid).setFuncCreationDate(new Date((from + 2000L))), IssueDocTesting.newDoc("issue3", project1).setAssigneeUuid(userUuid).setFuncCreationDate(new Date((from + 3000L))), IssueDocTesting.newDoc("issue4", project3).setAssigneeUuid(userUuid).setFuncCreationDate(new Date((from + 4000L))), IssueDocTesting.newDoc("issue5", project3).setAssigneeUuid(userUuid).setFuncCreationDate(new Date((from + 5000L))));
        List<ProjectStatistics> result = underTest.searchProjectStatistics(Arrays.asList(project1.uuid(), project2.uuid(), project3.uuid()), Arrays.asList(from, from, from), userUuid);
        assertThat(result).extracting(ProjectStatistics::getProjectUuid, ProjectStatistics::getLastIssueDate).containsExactlyInAnyOrder(tuple(project1.uuid(), (from + 3000L)), tuple(project3.uuid(), (from + 5000L)));
    }

    @Test
    public void searchProjectStatistics_return_branch_issues() {
        OrganizationDto organization = OrganizationTesting.newOrganizationDto();
        ComponentDto project = newPrivateProjectDto(organization);
        ComponentDto branch = newProjectBranch(project, newBranchDto(project).setKey("branch"));
        String userUuid = randomAlphanumeric(40);
        long from = 1111234567890L;
        indexIssues(IssueDocTesting.newDoc("issue1", branch).setAssigneeUuid(userUuid).setFuncCreationDate(new Date((from + 1L))), IssueDocTesting.newDoc("issue2", branch).setAssigneeUuid(userUuid).setFuncCreationDate(new Date((from + 2L))), IssueDocTesting.newDoc("issue3", project).setAssigneeUuid(userUuid).setFuncCreationDate(new Date((from + 1L))));
        List<ProjectStatistics> result = underTest.searchProjectStatistics(Collections.singletonList(project.uuid()), Collections.singletonList(from), userUuid);
        assertThat(result).extracting(ProjectStatistics::getIssueCount, ProjectStatistics::getProjectUuid, ProjectStatistics::getLastIssueDate).containsExactly(tuple(2L, branch.uuid(), (from + 2L)), tuple(1L, project.uuid(), (from + 1L)));
    }
}

