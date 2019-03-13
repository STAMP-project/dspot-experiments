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
import Issue.STATUS_CLOSED;
import Issue.STATUS_CONFIRMED;
import Issue.STATUS_OPEN;
import IssueQuery.Builder;
import Severity.BLOCKER;
import Severity.INFO;
import Severity.MAJOR;
import com.google.common.collect.ImmutableMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.TimeZone;
import org.assertj.core.api.Fail;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.utils.System2;
import org.sonar.api.utils.internal.TestSystem2;
import org.sonar.db.DbTester;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.db.organization.OrganizationTesting;
import org.sonar.db.rule.RuleDefinitionDto;
import org.sonar.server.es.EsTester;
import org.sonar.server.es.SearchOptions;
import org.sonar.server.issue.IssueDocTesting;
import org.sonar.server.permission.index.PermissionIndexerTester;
import org.sonar.server.permission.index.WebAuthorizationTypeSupport;
import org.sonar.server.tester.UserSessionRule;
import org.sonar.server.view.index.ViewIndexer;


public class IssueIndexFiltersTest {
    @Rule
    public EsTester es = EsTester.create();

    @Rule
    public UserSessionRule userSessionRule = UserSessionRule.standalone();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private System2 system2 = new TestSystem2().setNow(1500000000000L).setDefaultTimeZone(TimeZone.getTimeZone("GMT-01:00"));

    @Rule
    public DbTester db = DbTester.create(system2);

    private IssueIndexer issueIndexer = new IssueIndexer(es.client(), db.getDbClient(), new IssueIteratorFactory(db.getDbClient()));

    private ViewIndexer viewIndexer = new ViewIndexer(db.getDbClient(), es.client());

    private PermissionIndexerTester authorizationIndexer = new PermissionIndexerTester(es, issueIndexer);

    private IssueIndex underTest = new IssueIndex(es.client(), system2, userSessionRule, new WebAuthorizationTypeSupport(userSessionRule));

    @Test
    public void filter_by_keys() {
        ComponentDto project = newPrivateProjectDto(OrganizationTesting.newOrganizationDto());
        indexIssues(IssueDocTesting.newDoc("I1", newFileDto(project, null)), IssueDocTesting.newDoc("I2", newFileDto(project, null)));
        assertThatSearchReturnsOnly(IssueQuery.builder().issueKeys(Arrays.asList("I1", "I2")), "I1", "I2");
        assertThatSearchReturnsOnly(IssueQuery.builder().issueKeys(Collections.singletonList("I1")), "I1");
        assertThatSearchReturnsEmpty(IssueQuery.builder().issueKeys(Arrays.asList("I3", "I4")));
    }

    @Test
    public void filter_by_projects() {
        ComponentDto project = newPrivateProjectDto(OrganizationTesting.newOrganizationDto());
        ComponentDto module = newModuleDto(project);
        ComponentDto subModule = newModuleDto(module);
        indexIssues(IssueDocTesting.newDoc("I1", project), IssueDocTesting.newDoc("I2", newFileDto(project, null)), IssueDocTesting.newDoc("I3", module), IssueDocTesting.newDoc("I4", newFileDto(module, null)), IssueDocTesting.newDoc("I5", subModule), IssueDocTesting.newDoc("I6", newFileDto(subModule, null)));
        assertThatSearchReturnsOnly(IssueQuery.builder().projectUuids(Collections.singletonList(project.uuid())), "I1", "I2", "I3", "I4", "I5", "I6");
        assertThatSearchReturnsEmpty(IssueQuery.builder().projectUuids(Collections.singletonList("unknown")));
    }

    @Test
    public void filter_by_modules() {
        ComponentDto project = newPrivateProjectDto(OrganizationTesting.newOrganizationDto());
        ComponentDto module = newModuleDto(project);
        ComponentDto subModule = newModuleDto(module);
        ComponentDto file = newFileDto(subModule, null);
        indexIssues(IssueDocTesting.newDoc("I3", module), IssueDocTesting.newDoc("I5", subModule), IssueDocTesting.newDoc("I2", file));
        assertThatSearchReturnsEmpty(IssueQuery.builder().projectUuids(Collections.singletonList(project.uuid())).moduleUuids(Collections.singletonList(file.uuid())));
        assertThatSearchReturnsOnly(IssueQuery.builder().projectUuids(Collections.singletonList(project.uuid())).moduleUuids(Collections.singletonList(module.uuid())), "I3");
        assertThatSearchReturnsOnly(IssueQuery.builder().projectUuids(Collections.singletonList(project.uuid())).moduleUuids(Collections.singletonList(subModule.uuid())), "I2", "I5");
        assertThatSearchReturnsEmpty(IssueQuery.builder().projectUuids(Collections.singletonList(project.uuid())).moduleUuids(Collections.singletonList(project.uuid())));
        assertThatSearchReturnsEmpty(IssueQuery.builder().projectUuids(Collections.singletonList(project.uuid())).moduleUuids(Collections.singletonList("unknown")));
    }

    @Test
    public void filter_by_components_on_contextualized_search() {
        ComponentDto project = newPrivateProjectDto(OrganizationTesting.newOrganizationDto());
        ComponentDto module = newModuleDto(project);
        ComponentDto subModule = newModuleDto(module);
        ComponentDto file1 = newFileDto(project, null);
        ComponentDto file2 = newFileDto(module, null);
        ComponentDto file3 = newFileDto(subModule, null);
        String view = "ABCD";
        indexView(view, Arrays.asList(project.uuid()));
        indexIssues(IssueDocTesting.newDoc("I1", project), IssueDocTesting.newDoc("I2", file1), IssueDocTesting.newDoc("I3", module), IssueDocTesting.newDoc("I4", file2), IssueDocTesting.newDoc("I5", subModule), IssueDocTesting.newDoc("I6", file3));
        assertThatSearchReturnsOnly(IssueQuery.builder().fileUuids(Arrays.asList(file1.uuid(), file2.uuid(), file3.uuid())), "I2", "I4", "I6");
        assertThatSearchReturnsOnly(IssueQuery.builder().fileUuids(Collections.singletonList(file1.uuid())), "I2");
        assertThatSearchReturnsOnly(IssueQuery.builder().moduleRootUuids(Collections.singletonList(subModule.uuid())), "I5", "I6");
        assertThatSearchReturnsOnly(IssueQuery.builder().moduleRootUuids(Collections.singletonList(module.uuid())), "I3", "I4", "I5", "I6");
        assertThatSearchReturnsOnly(IssueQuery.builder().projectUuids(Collections.singletonList(project.uuid())), "I1", "I2", "I3", "I4", "I5", "I6");
        assertThatSearchReturnsOnly(IssueQuery.builder().viewUuids(Collections.singletonList(view)), "I1", "I2", "I3", "I4", "I5", "I6");
        assertThatSearchReturnsEmpty(IssueQuery.builder().projectUuids(Collections.singletonList("unknown")));
    }

    @Test
    public void filter_by_components_on_non_contextualized_search() {
        ComponentDto project = newPrivateProjectDto(OrganizationTesting.newOrganizationDto(), "project");
        ComponentDto file1 = newFileDto(project, null, "file1");
        ComponentDto module = newModuleDto(project).setUuid("module");
        ComponentDto file2 = newFileDto(module, null, "file2");
        ComponentDto subModule = newModuleDto(module).setUuid("subModule");
        ComponentDto file3 = newFileDto(subModule, null, "file3");
        String view = "ABCD";
        indexView(view, Arrays.asList(project.uuid()));
        indexIssues(IssueDocTesting.newDoc("I1", project), IssueDocTesting.newDoc("I2", file1), IssueDocTesting.newDoc("I3", module), IssueDocTesting.newDoc("I4", file2), IssueDocTesting.newDoc("I5", subModule), IssueDocTesting.newDoc("I6", file3));
        assertThatSearchReturnsEmpty(IssueQuery.builder().projectUuids(Collections.singletonList("unknown")));
        assertThatSearchReturnsOnly(IssueQuery.builder().projectUuids(Collections.singletonList(project.uuid())), "I1", "I2", "I3", "I4", "I5", "I6");
        assertThatSearchReturnsOnly(IssueQuery.builder().viewUuids(Collections.singletonList(view)), "I1", "I2", "I3", "I4", "I5", "I6");
        assertThatSearchReturnsOnly(IssueQuery.builder().moduleUuids(Collections.singletonList(module.uuid())), "I3", "I4");
        assertThatSearchReturnsOnly(IssueQuery.builder().moduleUuids(Collections.singletonList(subModule.uuid())), "I5", "I6");
        assertThatSearchReturnsOnly(IssueQuery.builder().fileUuids(Collections.singletonList(file1.uuid())), "I2");
        assertThatSearchReturnsOnly(IssueQuery.builder().fileUuids(Arrays.asList(file1.uuid(), file2.uuid(), file3.uuid())), "I2", "I4", "I6");
    }

    @Test
    public void filter_by_directories() {
        ComponentDto project = newPrivateProjectDto(OrganizationTesting.newOrganizationDto());
        ComponentDto file1 = newFileDto(project, null).setPath("src/main/xoo/F1.xoo");
        ComponentDto file2 = newFileDto(project, null).setPath("F2.xoo");
        indexIssues(IssueDocTesting.newDoc("I1", file1).setDirectoryPath("/src/main/xoo"), IssueDocTesting.newDoc("I2", file2).setDirectoryPath("/"));
        assertThatSearchReturnsOnly(IssueQuery.builder().directories(Collections.singletonList("/src/main/xoo")), "I1");
        assertThatSearchReturnsOnly(IssueQuery.builder().directories(Collections.singletonList("/")), "I2");
        assertThatSearchReturnsEmpty(IssueQuery.builder().directories(Collections.singletonList("unknown")));
    }

    @Test
    public void filter_by_portfolios() {
        ComponentDto portfolio1 = db.components().insertPrivateApplication(db.getDefaultOrganization());
        ComponentDto portfolio2 = db.components().insertPrivateApplication(db.getDefaultOrganization());
        ComponentDto project1 = db.components().insertPrivateProject();
        ComponentDto file = db.components().insertComponent(newFileDto(project1));
        ComponentDto project2 = db.components().insertPrivateProject();
        IssueDoc issueOnProject1 = IssueDocTesting.newDoc(project1);
        IssueDoc issueOnFile = IssueDocTesting.newDoc(file);
        IssueDoc issueOnProject2 = IssueDocTesting.newDoc(project2);
        indexIssues(issueOnProject1, issueOnFile, issueOnProject2);
        indexView(portfolio1.uuid(), Collections.singletonList(project1.uuid()));
        indexView(portfolio2.uuid(), Collections.singletonList(project2.uuid()));
        assertThatSearchReturnsOnly(IssueQuery.builder().viewUuids(Collections.singletonList(portfolio1.uuid())), issueOnProject1.key(), issueOnFile.key());
        assertThatSearchReturnsOnly(IssueQuery.builder().viewUuids(Collections.singletonList(portfolio2.uuid())), issueOnProject2.key());
        assertThatSearchReturnsOnly(IssueQuery.builder().viewUuids(Arrays.asList(portfolio1.uuid(), portfolio2.uuid())), issueOnProject1.key(), issueOnFile.key(), issueOnProject2.key());
        assertThatSearchReturnsOnly(IssueQuery.builder().viewUuids(Collections.singletonList(portfolio1.uuid())).projectUuids(Collections.singletonList(project1.uuid())), issueOnProject1.key(), issueOnFile.key());
        assertThatSearchReturnsOnly(IssueQuery.builder().viewUuids(Collections.singletonList(portfolio1.uuid())).fileUuids(Collections.singletonList(file.uuid())), issueOnFile.key());
        assertThatSearchReturnsEmpty(IssueQuery.builder().viewUuids(Collections.singletonList("unknown")));
    }

    @Test
    public void filter_by_portfolios_not_having_projects() {
        OrganizationDto organizationDto = OrganizationTesting.newOrganizationDto();
        ComponentDto project1 = newPrivateProjectDto(organizationDto);
        ComponentDto file1 = newFileDto(project1, null);
        indexIssues(IssueDocTesting.newDoc("I2", file1));
        String view1 = "ABCD";
        indexView(view1, Collections.emptyList());
        assertThatSearchReturnsOnly(IssueQuery.builder().viewUuids(Collections.singletonList(view1)));
    }

    @Test
    public void do_not_return_issues_from_project_branch_when_filtering_by_portfolios() {
        ComponentDto portfolio = db.components().insertPrivateApplication(db.getDefaultOrganization());
        ComponentDto project = db.components().insertMainBranch();
        ComponentDto projectBranch = db.components().insertProjectBranch(project);
        ComponentDto fileOnProjectBranch = db.components().insertComponent(newFileDto(projectBranch));
        indexView(portfolio.uuid(), Collections.singletonList(project.uuid()));
        IssueDoc issueOnProject = IssueDocTesting.newDoc(project);
        IssueDoc issueOnProjectBranch = IssueDocTesting.newDoc(projectBranch);
        IssueDoc issueOnFileOnProjectBranch = IssueDocTesting.newDoc(fileOnProjectBranch);
        indexIssues(issueOnProject, issueOnFileOnProjectBranch, issueOnProjectBranch);
        assertThatSearchReturnsOnly(IssueQuery.builder().viewUuids(Collections.singletonList(portfolio.uuid())), issueOnProject.key());
        assertThatSearchReturnsOnly(IssueQuery.builder().viewUuids(Collections.singletonList(portfolio.uuid())).projectUuids(Collections.singletonList(project.uuid())), issueOnProject.key());
        assertThatSearchReturnsEmpty(IssueQuery.builder().viewUuids(Collections.singletonList(portfolio.uuid())).projectUuids(Collections.singletonList(projectBranch.uuid())));
    }

    @Test
    public void filter_one_issue_by_project_and_branch() {
        ComponentDto project = db.components().insertPrivateProject();
        ComponentDto branch = db.components().insertProjectBranch(project);
        ComponentDto anotherbBranch = db.components().insertProjectBranch(project);
        IssueDoc issueOnProject = IssueDocTesting.newDoc(project);
        IssueDoc issueOnBranch = IssueDocTesting.newDoc(branch);
        IssueDoc issueOnAnotherBranch = IssueDocTesting.newDoc(anotherbBranch);
        indexIssues(issueOnProject, issueOnBranch, issueOnAnotherBranch);
        assertThatSearchReturnsOnly(IssueQuery.builder().branchUuid(branch.uuid()).mainBranch(false), issueOnBranch.key());
        assertThatSearchReturnsOnly(IssueQuery.builder().componentUuids(Collections.singletonList(branch.uuid())).branchUuid(branch.uuid()).mainBranch(false), issueOnBranch.key());
        assertThatSearchReturnsOnly(IssueQuery.builder().projectUuids(Collections.singletonList(project.uuid())).branchUuid(branch.uuid()).mainBranch(false), issueOnBranch.key());
        assertThatSearchReturnsOnly(IssueQuery.builder().componentUuids(Collections.singletonList(branch.uuid())).projectUuids(Collections.singletonList(project.uuid())).branchUuid(branch.uuid()).mainBranch(false), issueOnBranch.key());
        assertThatSearchReturnsEmpty(IssueQuery.builder().branchUuid("unknown"));
    }

    @Test
    public void issues_from_branch_component_children() {
        ComponentDto project = db.components().insertPrivateProject();
        ComponentDto projectModule = db.components().insertComponent(newModuleDto(project));
        ComponentDto projectFile = db.components().insertComponent(newFileDto(projectModule));
        ComponentDto branch = db.components().insertProjectBranch(project, ( b) -> b.setKey("my_branch"));
        ComponentDto branchModule = db.components().insertComponent(newModuleDto(branch));
        ComponentDto branchFile = db.components().insertComponent(newFileDto(branchModule));
        indexIssues(IssueDocTesting.newDoc("I1", project), IssueDocTesting.newDoc("I2", projectFile), IssueDocTesting.newDoc("I3", projectModule), IssueDocTesting.newDoc("I4", branch), IssueDocTesting.newDoc("I5", branchModule), IssueDocTesting.newDoc("I6", branchFile));
        assertThatSearchReturnsOnly(IssueQuery.builder().branchUuid(branch.uuid()).mainBranch(false), "I4", "I5", "I6");
        assertThatSearchReturnsOnly(IssueQuery.builder().moduleUuids(Collections.singletonList(branchModule.uuid())).branchUuid(branch.uuid()).mainBranch(false), "I5", "I6");
        assertThatSearchReturnsOnly(IssueQuery.builder().fileUuids(Collections.singletonList(branchFile.uuid())).branchUuid(branch.uuid()).mainBranch(false), "I6");
        assertThatSearchReturnsEmpty(IssueQuery.builder().fileUuids(Collections.singletonList(branchFile.uuid())).mainBranch(false).branchUuid("unknown"));
    }

    @Test
    public void issues_from_main_branch() {
        ComponentDto project = db.components().insertPrivateProject();
        ComponentDto branch = db.components().insertProjectBranch(project);
        IssueDoc issueOnProject = IssueDocTesting.newDoc(project);
        IssueDoc issueOnBranch = IssueDocTesting.newDoc(branch);
        indexIssues(issueOnProject, issueOnBranch);
        assertThatSearchReturnsOnly(IssueQuery.builder().branchUuid(project.uuid()).mainBranch(true), issueOnProject.key());
        assertThatSearchReturnsOnly(IssueQuery.builder().componentUuids(Collections.singletonList(project.uuid())).branchUuid(project.uuid()).mainBranch(true), issueOnProject.key());
        assertThatSearchReturnsOnly(IssueQuery.builder().projectUuids(Collections.singletonList(project.uuid())).branchUuid(project.uuid()).mainBranch(true), issueOnProject.key());
        assertThatSearchReturnsOnly(IssueQuery.builder().componentUuids(Collections.singletonList(project.uuid())).projectUuids(Collections.singletonList(project.uuid())).branchUuid(project.uuid()).mainBranch(true), issueOnProject.key());
    }

    @Test
    public void branch_issues_are_ignored_when_no_branch_param() {
        ComponentDto project = db.components().insertPrivateProject();
        ComponentDto branch = db.components().insertProjectBranch(project, ( b) -> b.setKey("my_branch"));
        IssueDoc projectIssue = IssueDocTesting.newDoc(project);
        IssueDoc branchIssue = IssueDocTesting.newDoc(branch);
        indexIssues(projectIssue, branchIssue);
        assertThatSearchReturnsOnly(IssueQuery.builder(), projectIssue.key());
    }

    @Test
    public void filter_by_main_application() {
        ComponentDto application1 = db.components().insertPrivateApplication(db.getDefaultOrganization());
        ComponentDto application2 = db.components().insertPrivateApplication(db.getDefaultOrganization());
        ComponentDto project1 = db.components().insertPrivateProject();
        ComponentDto file = db.components().insertComponent(newFileDto(project1));
        ComponentDto project2 = db.components().insertPrivateProject();
        indexView(application1.uuid(), Collections.singletonList(project1.uuid()));
        indexView(application2.uuid(), Collections.singletonList(project2.uuid()));
        IssueDoc issueOnProject1 = IssueDocTesting.newDoc(project1);
        IssueDoc issueOnFile = IssueDocTesting.newDoc(file);
        IssueDoc issueOnProject2 = IssueDocTesting.newDoc(project2);
        indexIssues(issueOnProject1, issueOnFile, issueOnProject2);
        assertThatSearchReturnsOnly(IssueQuery.builder().viewUuids(Collections.singletonList(application1.uuid())), issueOnProject1.key(), issueOnFile.key());
        assertThatSearchReturnsOnly(IssueQuery.builder().viewUuids(Collections.singletonList(application2.uuid())), issueOnProject2.key());
        assertThatSearchReturnsOnly(IssueQuery.builder().viewUuids(Arrays.asList(application1.uuid(), application2.uuid())), issueOnProject1.key(), issueOnFile.key(), issueOnProject2.key());
        assertThatSearchReturnsOnly(IssueQuery.builder().viewUuids(Collections.singletonList(application1.uuid())).projectUuids(Collections.singletonList(project1.uuid())), issueOnProject1.key(), issueOnFile.key());
        assertThatSearchReturnsOnly(IssueQuery.builder().viewUuids(Collections.singletonList(application1.uuid())).fileUuids(Collections.singletonList(file.uuid())), issueOnFile.key());
        assertThatSearchReturnsEmpty(IssueQuery.builder().viewUuids(Collections.singletonList("unknown")));
    }

    @Test
    public void filter_by_application_branch() {
        ComponentDto application = db.components().insertMainBranch(( c) -> c.setQualifier(APP));
        ComponentDto branch1 = db.components().insertProjectBranch(application);
        ComponentDto branch2 = db.components().insertProjectBranch(application);
        ComponentDto project1 = db.components().insertPrivateProject();
        ComponentDto file = db.components().insertComponent(newFileDto(project1));
        ComponentDto project2 = db.components().insertPrivateProject();
        indexView(branch1.uuid(), Collections.singletonList(project1.uuid()));
        indexView(branch2.uuid(), Collections.singletonList(project2.uuid()));
        IssueDoc issueOnProject1 = IssueDocTesting.newDoc(project1);
        IssueDoc issueOnFile = IssueDocTesting.newDoc(file);
        IssueDoc issueOnProject2 = IssueDocTesting.newDoc(project2);
        indexIssues(issueOnProject1, issueOnFile, issueOnProject2);
        assertThatSearchReturnsOnly(IssueQuery.builder().viewUuids(Collections.singletonList(branch1.uuid())).branchUuid(branch1.uuid()).mainBranch(false), issueOnProject1.key(), issueOnFile.key());
        assertThatSearchReturnsOnly(IssueQuery.builder().viewUuids(Collections.singletonList(branch1.uuid())).projectUuids(Collections.singletonList(project1.uuid())).branchUuid(branch1.uuid()).mainBranch(false), issueOnProject1.key(), issueOnFile.key());
        assertThatSearchReturnsOnly(IssueQuery.builder().viewUuids(Collections.singletonList(branch1.uuid())).fileUuids(Collections.singletonList(file.uuid())).branchUuid(branch1.uuid()).mainBranch(false), issueOnFile.key());
        assertThatSearchReturnsEmpty(IssueQuery.builder().branchUuid("unknown"));
    }

    @Test
    public void filter_by_application_branch_having_project_branches() {
        ComponentDto application = db.components().insertMainBranch(( c) -> c.setQualifier(APP).setDbKey("app"));
        ComponentDto applicationBranch1 = db.components().insertProjectBranch(application, ( a) -> a.setKey("app-branch1"));
        ComponentDto applicationBranch2 = db.components().insertProjectBranch(application, ( a) -> a.setKey("app-branch2"));
        ComponentDto project1 = db.components().insertPrivateProject(( p) -> p.setDbKey("prj1"));
        ComponentDto project1Branch1 = db.components().insertProjectBranch(project1);
        ComponentDto fileOnProject1Branch1 = db.components().insertComponent(newFileDto(project1Branch1));
        ComponentDto project1Branch2 = db.components().insertProjectBranch(project1);
        ComponentDto project2 = db.components().insertPrivateProject(( p) -> p.setDbKey("prj2"));
        indexView(applicationBranch1.uuid(), Arrays.asList(project1Branch1.uuid(), project2.uuid()));
        indexView(applicationBranch2.uuid(), Collections.singletonList(project1Branch2.uuid()));
        IssueDoc issueOnProject1 = IssueDocTesting.newDoc(project1);
        IssueDoc issueOnProject1Branch1 = IssueDocTesting.newDoc(project1Branch1);
        IssueDoc issueOnFileOnProject1Branch1 = IssueDocTesting.newDoc(fileOnProject1Branch1);
        IssueDoc issueOnProject1Branch2 = IssueDocTesting.newDoc(project1Branch2);
        IssueDoc issueOnProject2 = IssueDocTesting.newDoc(project2);
        indexIssues(issueOnProject1, issueOnProject1Branch1, issueOnFileOnProject1Branch1, issueOnProject1Branch2, issueOnProject2);
        assertThatSearchReturnsOnly(IssueQuery.builder().viewUuids(Collections.singletonList(applicationBranch1.uuid())).branchUuid(applicationBranch1.uuid()).mainBranch(false), issueOnProject1Branch1.key(), issueOnFileOnProject1Branch1.key(), issueOnProject2.key());
        assertThatSearchReturnsOnly(IssueQuery.builder().viewUuids(Collections.singletonList(applicationBranch1.uuid())).projectUuids(Collections.singletonList(project1.uuid())).branchUuid(applicationBranch1.uuid()).mainBranch(false), issueOnProject1Branch1.key(), issueOnFileOnProject1Branch1.key());
        assertThatSearchReturnsOnly(IssueQuery.builder().viewUuids(Collections.singletonList(applicationBranch1.uuid())).fileUuids(Collections.singletonList(fileOnProject1Branch1.uuid())).branchUuid(applicationBranch1.uuid()).mainBranch(false), issueOnFileOnProject1Branch1.key());
        assertThatSearchReturnsEmpty(IssueQuery.builder().viewUuids(Collections.singletonList(applicationBranch1.uuid())).projectUuids(Collections.singletonList("unknown")).branchUuid(applicationBranch1.uuid()).mainBranch(false));
    }

    @Test
    public void filter_by_created_after_by_projects() {
        Date now = new Date();
        OrganizationDto organizationDto = OrganizationTesting.newOrganizationDto();
        ComponentDto project1 = newPrivateProjectDto(organizationDto);
        IssueDoc project1Issue1 = IssueDocTesting.newDoc(project1).setFuncCreationDate(addDays(now, (-10)));
        IssueDoc project1Issue2 = IssueDocTesting.newDoc(project1).setFuncCreationDate(addDays(now, (-20)));
        ComponentDto project2 = newPrivateProjectDto(organizationDto);
        IssueDoc project2Issue1 = IssueDocTesting.newDoc(project2).setFuncCreationDate(addDays(now, (-15)));
        IssueDoc project2Issue2 = IssueDocTesting.newDoc(project2).setFuncCreationDate(addDays(now, (-30)));
        indexIssues(project1Issue1, project1Issue2, project2Issue1, project2Issue2);
        // Search for issues of project 1 having less than 15 days
        assertThatSearchReturnsOnly(IssueQuery.builder().createdAfterByProjectUuids(ImmutableMap.of(project1.uuid(), new IssueQuery.PeriodStart(addDays(now, (-15)), true))), project1Issue1.key());
        // Search for issues of project 1 having less than 14 days and project 2 having less then 25 days
        assertThatSearchReturnsOnly(IssueQuery.builder().createdAfterByProjectUuids(ImmutableMap.of(project1.uuid(), new IssueQuery.PeriodStart(addDays(now, (-14)), true), project2.uuid(), new IssueQuery.PeriodStart(addDays(now, (-25)), true))), project1Issue1.key(), project2Issue1.key());
        // Search for issues of project 1 having less than 30 days
        assertThatSearchReturnsOnly(IssueQuery.builder().createdAfterByProjectUuids(ImmutableMap.of(project1.uuid(), new IssueQuery.PeriodStart(addDays(now, (-30)), true))), project1Issue1.key(), project1Issue2.key());
        // Search for issues of project 1 and project 2 having less than 5 days
        assertThatSearchReturnsOnly(IssueQuery.builder().createdAfterByProjectUuids(ImmutableMap.of(project1.uuid(), new IssueQuery.PeriodStart(addDays(now, (-5)), true), project2.uuid(), new IssueQuery.PeriodStart(addDays(now, (-5)), true))));
    }

    @Test
    public void filter_by_severities() {
        ComponentDto project = newPrivateProjectDto(OrganizationTesting.newOrganizationDto());
        ComponentDto file = newFileDto(project, null);
        indexIssues(IssueDocTesting.newDoc("I1", file).setSeverity(INFO), IssueDocTesting.newDoc("I2", file).setSeverity(MAJOR));
        assertThatSearchReturnsOnly(IssueQuery.builder().severities(Arrays.asList(INFO, MAJOR)), "I1", "I2");
        assertThatSearchReturnsOnly(IssueQuery.builder().severities(Collections.singletonList(INFO)), "I1");
        assertThatSearchReturnsEmpty(IssueQuery.builder().severities(Collections.singletonList(BLOCKER)));
    }

    @Test
    public void facets_on_severities() {
        ComponentDto project = newPrivateProjectDto(OrganizationTesting.newOrganizationDto());
        ComponentDto file = newFileDto(project, null);
        indexIssues(IssueDocTesting.newDoc("I1", file).setSeverity(INFO), IssueDocTesting.newDoc("I2", file).setSeverity(INFO), IssueDocTesting.newDoc("I3", file).setSeverity(MAJOR));
        assertThatFacetHasOnly(IssueQuery.builder(), "severities", entry("INFO", 2L), entry("MAJOR", 1L));
    }

    @Test
    public void filter_by_statuses() {
        ComponentDto project = newPrivateProjectDto(OrganizationTesting.newOrganizationDto());
        ComponentDto file = newFileDto(project, null);
        indexIssues(IssueDocTesting.newDoc("I1", file).setStatus(STATUS_CLOSED), IssueDocTesting.newDoc("I2", file).setStatus(STATUS_OPEN));
        assertThatSearchReturnsOnly(IssueQuery.builder().statuses(Arrays.asList(STATUS_CLOSED, STATUS_OPEN)), "I1", "I2");
        assertThatSearchReturnsOnly(IssueQuery.builder().statuses(Collections.singletonList(STATUS_CLOSED)), "I1");
        assertThatSearchReturnsEmpty(IssueQuery.builder().statuses(Collections.singletonList(STATUS_CONFIRMED)));
    }

    @Test
    public void filter_by_resolutions() {
        ComponentDto project = newPrivateProjectDto(OrganizationTesting.newOrganizationDto());
        ComponentDto file = newFileDto(project, null);
        indexIssues(IssueDocTesting.newDoc("I1", file).setResolution(RESOLUTION_FALSE_POSITIVE), IssueDocTesting.newDoc("I2", file).setResolution(RESOLUTION_FIXED));
        assertThatSearchReturnsOnly(IssueQuery.builder().resolutions(Arrays.asList(RESOLUTION_FALSE_POSITIVE, RESOLUTION_FIXED)), "I1", "I2");
        assertThatSearchReturnsOnly(IssueQuery.builder().resolutions(Collections.singletonList(RESOLUTION_FALSE_POSITIVE)), "I1");
        assertThatSearchReturnsEmpty(IssueQuery.builder().resolutions(Collections.singletonList(RESOLUTION_REMOVED)));
    }

    @Test
    public void filter_by_resolved() {
        ComponentDto project = newPrivateProjectDto(OrganizationTesting.newOrganizationDto());
        ComponentDto file = newFileDto(project, null);
        indexIssues(IssueDocTesting.newDoc("I1", file).setStatus(STATUS_CLOSED).setResolution(RESOLUTION_FIXED), IssueDocTesting.newDoc("I2", file).setStatus(STATUS_OPEN).setResolution(null), IssueDocTesting.newDoc("I3", file).setStatus(STATUS_OPEN).setResolution(null));
        assertThatSearchReturnsOnly(IssueQuery.builder().resolved(true), "I1");
        assertThatSearchReturnsOnly(IssueQuery.builder().resolved(false), "I2", "I3");
        assertThatSearchReturnsOnly(IssueQuery.builder().resolved(null), "I1", "I2", "I3");
    }

    @Test
    public void filter_by_rules() {
        ComponentDto project = newPrivateProjectDto(OrganizationTesting.newOrganizationDto());
        ComponentDto file = newFileDto(project, null);
        RuleDefinitionDto ruleDefinitionDto = newRule();
        db.rules().insert(ruleDefinitionDto);
        indexIssues(IssueDocTesting.newDoc("I1", file).setRuleId(ruleDefinitionDto.getId()));
        assertThatSearchReturnsOnly(IssueQuery.builder().rules(Collections.singletonList(ruleDefinitionDto)), "I1");
        assertThatSearchReturnsEmpty(IssueQuery.builder().rules(Collections.singletonList(new RuleDefinitionDto().setId((-1)))));
    }

    @Test
    public void filter_by_languages() {
        ComponentDto project = newPrivateProjectDto(OrganizationTesting.newOrganizationDto());
        ComponentDto file = newFileDto(project, null);
        RuleDefinitionDto ruleDefinitionDto = newRule();
        db.rules().insert(ruleDefinitionDto);
        indexIssues(IssueDocTesting.newDoc("I1", file).setRuleId(ruleDefinitionDto.getId()).setLanguage("xoo"));
        assertThatSearchReturnsOnly(IssueQuery.builder().languages(Collections.singletonList("xoo")), "I1");
        assertThatSearchReturnsEmpty(IssueQuery.builder().languages(Collections.singletonList("unknown")));
    }

    @Test
    public void filter_by_assignees() {
        ComponentDto project = newPrivateProjectDto(OrganizationTesting.newOrganizationDto());
        ComponentDto file = newFileDto(project, null);
        indexIssues(IssueDocTesting.newDoc("I1", file).setAssigneeUuid("steph-uuid"), IssueDocTesting.newDoc("I2", file).setAssigneeUuid("marcel-uuid"), IssueDocTesting.newDoc("I3", file).setAssigneeUuid(null));
        assertThatSearchReturnsOnly(IssueQuery.builder().assigneeUuids(Collections.singletonList("steph-uuid")), "I1");
        assertThatSearchReturnsOnly(IssueQuery.builder().assigneeUuids(Arrays.asList("steph-uuid", "marcel-uuid")), "I1", "I2");
        assertThatSearchReturnsEmpty(IssueQuery.builder().assigneeUuids(Collections.singletonList("unknown")));
    }

    @Test
    public void filter_by_assigned() {
        ComponentDto project = newPrivateProjectDto(OrganizationTesting.newOrganizationDto());
        ComponentDto file = newFileDto(project, null);
        indexIssues(IssueDocTesting.newDoc("I1", file).setAssigneeUuid("steph-uuid"), IssueDocTesting.newDoc("I2", file).setAssigneeUuid(null), IssueDocTesting.newDoc("I3", file).setAssigneeUuid(null));
        assertThatSearchReturnsOnly(IssueQuery.builder().assigned(true), "I1");
        assertThatSearchReturnsOnly(IssueQuery.builder().assigned(false), "I2", "I3");
        assertThatSearchReturnsOnly(IssueQuery.builder().assigned(null), "I1", "I2", "I3");
    }

    @Test
    public void filter_by_authors() {
        ComponentDto project = newPrivateProjectDto(OrganizationTesting.newOrganizationDto());
        ComponentDto file = newFileDto(project, null);
        indexIssues(IssueDocTesting.newDoc("I1", file).setAuthorLogin("steph"), IssueDocTesting.newDoc("I2", file).setAuthorLogin("marcel"), IssueDocTesting.newDoc("I3", file).setAssigneeUuid(null));
        assertThatSearchReturnsOnly(IssueQuery.builder().authors(Collections.singletonList("steph")), "I1");
        assertThatSearchReturnsOnly(IssueQuery.builder().authors(Arrays.asList("steph", "marcel")), "I1", "I2");
        assertThatSearchReturnsEmpty(IssueQuery.builder().authors(Collections.singletonList("unknown")));
    }

    @Test
    public void filter_by_created_after() {
        ComponentDto project = newPrivateProjectDto(OrganizationTesting.newOrganizationDto());
        ComponentDto file = newFileDto(project, null);
        indexIssues(IssueDocTesting.newDoc("I1", file).setFuncCreationDate(parseDate("2014-09-20")), IssueDocTesting.newDoc("I2", file).setFuncCreationDate(parseDate("2014-09-23")));
        assertThatSearchReturnsOnly(IssueQuery.builder().createdAfter(parseDate("2014-09-19")), "I1", "I2");
        // Lower bound is included
        assertThatSearchReturnsOnly(IssueQuery.builder().createdAfter(parseDate("2014-09-20")), "I1", "I2");
        assertThatSearchReturnsOnly(IssueQuery.builder().createdAfter(parseDate("2014-09-21")), "I2");
        assertThatSearchReturnsEmpty(IssueQuery.builder().createdAfter(parseDate("2014-09-25")));
    }

    @Test
    public void filter_by_created_before() {
        ComponentDto project = newPrivateProjectDto(OrganizationTesting.newOrganizationDto());
        ComponentDto file = newFileDto(project, null);
        indexIssues(IssueDocTesting.newDoc("I1", file).setFuncCreationDate(parseDate("2014-09-20")), IssueDocTesting.newDoc("I2", file).setFuncCreationDate(parseDate("2014-09-23")));
        assertThatSearchReturnsEmpty(IssueQuery.builder().createdBefore(parseDate("2014-09-19")));
        // Upper bound is excluded
        assertThatSearchReturnsEmpty(IssueQuery.builder().createdBefore(parseDate("2014-09-20")));
        assertThatSearchReturnsOnly(IssueQuery.builder().createdBefore(parseDate("2014-09-21")), "I1");
        assertThatSearchReturnsOnly(IssueQuery.builder().createdBefore(parseDate("2014-09-25")), "I1", "I2");
    }

    @Test
    public void filter_by_created_after_and_before() {
        ComponentDto project = newPrivateProjectDto(OrganizationTesting.newOrganizationDto());
        ComponentDto file = newFileDto(project, null);
        indexIssues(IssueDocTesting.newDoc("I1", file).setFuncCreationDate(parseDate("2014-09-20")), IssueDocTesting.newDoc("I2", file).setFuncCreationDate(parseDate("2014-09-23")));
        // 19 < createdAt < 25
        assertThatSearchReturnsOnly(IssueQuery.builder().createdAfter(parseDate("2014-09-19")).createdBefore(parseDate("2014-09-25")), "I1", "I2");
        // 20 < createdAt < 25: excludes first issue
        assertThatSearchReturnsOnly(IssueQuery.builder().createdAfter(parseDate("2014-09-20")).createdBefore(parseDate("2014-09-25")), "I1", "I2");
        // 21 < createdAt < 25
        assertThatSearchReturnsOnly(IssueQuery.builder().createdAfter(parseDate("2014-09-21")).createdBefore(parseDate("2014-09-25")), "I2");
        // 21 < createdAt < 24
        assertThatSearchReturnsOnly(IssueQuery.builder().createdAfter(parseDate("2014-09-21")).createdBefore(parseDate("2014-09-24")), "I2");
        // 21 < createdAt < 23: excludes second issue
        assertThatSearchReturnsEmpty(IssueQuery.builder().createdAfter(parseDate("2014-09-21")).createdBefore(parseDate("2014-09-23")));
        // 19 < createdAt < 21: only first issue
        assertThatSearchReturnsOnly(IssueQuery.builder().createdAfter(parseDate("2014-09-19")).createdBefore(parseDate("2014-09-21")), "I1");
        // 20 < createdAt < 20: exception
        expectedException.expect(IllegalArgumentException.class);
        underTest.search(IssueQuery.builder().createdAfter(parseDate("2014-09-20")).createdBefore(parseDate("2014-09-20")).build(), new SearchOptions());
    }

    @Test
    public void filter_by_created_after_and_before_take_into_account_timezone() {
        ComponentDto project = newPrivateProjectDto(OrganizationTesting.newOrganizationDto());
        ComponentDto file = newFileDto(project, null);
        indexIssues(IssueDocTesting.newDoc("I1", file).setFuncCreationDate(parseDateTime("2014-09-20T00:00:00+0100")), IssueDocTesting.newDoc("I2", file).setFuncCreationDate(parseDateTime("2014-09-23T00:00:00+0100")));
        assertThatSearchReturnsOnly(IssueQuery.builder().createdAfter(parseDateTime("2014-09-19T23:00:00+0000")).createdBefore(parseDateTime("2014-09-22T23:00:01+0000")), "I1", "I2");
        assertThatSearchReturnsEmpty(IssueQuery.builder().createdAfter(parseDateTime("2014-09-19T23:00:01+0000")).createdBefore(parseDateTime("2014-09-22T23:00:00+0000")));
    }

    @Test
    public void filter_by_created_before_must_be_lower_than_after() {
        try {
            underTest.search(IssueQuery.builder().createdAfter(parseDate("2014-09-20")).createdBefore(parseDate("2014-09-19")).build(), new SearchOptions());
            Fail.failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException exception) {
            assertThat(exception.getMessage()).isEqualTo("Start bound cannot be larger or equal to end bound");
        }
    }

    @Test
    public void fail_if_created_before_equals_created_after() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Start bound cannot be larger or equal to end bound");
        underTest.search(IssueQuery.builder().createdAfter(parseDate("2014-09-20")).createdBefore(parseDate("2014-09-20")).build(), new SearchOptions());
    }

    @Test
    public void filter_by_created_after_must_not_be_in_future() {
        try {
            underTest.search(IssueQuery.builder().createdAfter(new Date(Long.MAX_VALUE)).build(), new SearchOptions());
            Fail.failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException exception) {
            assertThat(exception.getMessage()).isEqualTo("Start bound cannot be in the future");
        }
    }

    @Test
    public void filter_by_created_at() {
        ComponentDto project = newPrivateProjectDto(OrganizationTesting.newOrganizationDto());
        ComponentDto file = newFileDto(project, null);
        indexIssues(IssueDocTesting.newDoc("I1", file).setFuncCreationDate(parseDate("2014-09-20")));
        assertThatSearchReturnsOnly(IssueQuery.builder().createdAt(parseDate("2014-09-20")), "I1");
        assertThatSearchReturnsEmpty(IssueQuery.builder().createdAt(parseDate("2014-09-21")));
    }

    @Test
    public void filter_by_organization() {
        OrganizationDto org1 = OrganizationTesting.newOrganizationDto();
        ComponentDto projectInOrg1 = newPrivateProjectDto(org1);
        OrganizationDto org2 = OrganizationTesting.newOrganizationDto();
        ComponentDto projectInOrg2 = newPrivateProjectDto(org2);
        indexIssues(IssueDocTesting.newDoc("issueInOrg1", projectInOrg1), IssueDocTesting.newDoc("issue1InOrg2", projectInOrg2), IssueDocTesting.newDoc("issue2InOrg2", projectInOrg2));
        verifyOrganizationFilter(org1.getUuid(), "issueInOrg1");
        verifyOrganizationFilter(org2.getUuid(), "issue1InOrg2", "issue2InOrg2");
        verifyOrganizationFilter("does_not_exist");
    }

    @Test
    public void filter_by_organization_and_project() {
        OrganizationDto org1 = OrganizationTesting.newOrganizationDto();
        ComponentDto projectInOrg1 = newPrivateProjectDto(org1);
        OrganizationDto org2 = OrganizationTesting.newOrganizationDto();
        ComponentDto projectInOrg2 = newPrivateProjectDto(org2);
        indexIssues(IssueDocTesting.newDoc("issueInOrg1", projectInOrg1), IssueDocTesting.newDoc("issue1InOrg2", projectInOrg2), IssueDocTesting.newDoc("issue2InOrg2", projectInOrg2));
        // no conflict
        IssueQuery.Builder query = IssueQuery.builder().organizationUuid(org1.getUuid()).projectUuids(Collections.singletonList(projectInOrg1.uuid()));
        assertThatSearchReturnsOnly(query, "issueInOrg1");
        // conflict
        query = IssueQuery.builder().organizationUuid(org1.getUuid()).projectUuids(Collections.singletonList(projectInOrg2.uuid()));
        assertThatSearchReturnsEmpty(query);
    }
}

