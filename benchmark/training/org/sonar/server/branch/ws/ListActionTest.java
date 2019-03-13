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
package org.sonar.server.branch.ws;


import MediaTypes.JSON;
import ProjectBranches.ShowWsResponse;
import System2.INSTANCE;
import WebService.Action;
import java.util.Collections;
import java.util.Optional;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.resources.ResourceTypes;
import org.sonar.api.server.ws.WebService;
import org.sonar.api.utils.System2;
import org.sonar.api.web.UserRole;
import org.sonar.db.DbTester;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.component.ComponentTesting;
import org.sonar.db.component.ResourceTypesRule;
import org.sonar.db.metric.MetricDto;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.db.rule.RuleDefinitionDto;
import org.sonar.server.es.EsTester;
import org.sonar.server.exceptions.NotFoundException;
import org.sonar.server.issue.index.IssueIndex;
import org.sonar.server.issue.index.IssueIndexer;
import org.sonar.server.permission.index.PermissionIndexerTester;
import org.sonar.server.permission.index.WebAuthorizationTypeSupport;
import org.sonar.server.tester.UserSessionRule;
import org.sonar.server.ws.WsActionTester;
import org.sonarqube.ws.ProjectBranches;
import org.sonarqube.ws.ProjectBranches.Branch;
import org.sonarqube.ws.ProjectBranches.ListWsResponse;


public class ListActionTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public DbTester db = DbTester.create(INSTANCE);

    @Rule
    public EsTester es = EsTester.create();

    @Rule
    public UserSessionRule userSession = UserSessionRule.standalone();

    private ResourceTypes resourceTypes = new ResourceTypesRule().setRootQualifiers(PROJECT);

    private IssueIndexer issueIndexer = new IssueIndexer(es.client(), db.getDbClient(), new org.sonar.server.issue.index.IssueIteratorFactory(db.getDbClient()));

    private IssueIndex issueIndex = new IssueIndex(es.client(), System2.INSTANCE, userSession, new WebAuthorizationTypeSupport(userSession));

    private PermissionIndexerTester permissionIndexerTester = new PermissionIndexerTester(es, issueIndexer);

    private MetricDto qualityGateStatus;

    public WsActionTester ws = new WsActionTester(new ListAction(db.getDbClient(), userSession, new org.sonar.server.component.ComponentFinder(db.getDbClient(), resourceTypes), issueIndex));

    @Test
    public void test_definition() {
        WebService.Action definition = ws.getDef();
        assertThat(definition.key()).isEqualTo("list");
        assertThat(definition.isPost()).isFalse();
        assertThat(definition.isInternal()).isFalse();
        assertThat(definition.params()).extracting(WebService.Param::key).containsExactlyInAnyOrder("project");
        assertThat(definition.since()).isEqualTo("6.6");
    }

    @Test
    public void test_example() {
        ComponentDto project = db.components().insertMainBranch(( p) -> p.setDbKey("sonarqube"));
        db.getDbClient().snapshotDao().insert(db.getSession(), newAnalysis(project).setLast(true).setCreatedAt(parseDateTime("2017-04-01T01:15:42+0100").getTime()));
        db.measures().insertLiveMeasure(project, qualityGateStatus, ( m) -> m.setData("ERROR"));
        ComponentDto shortLivingBranch = db.components().insertProjectBranch(project, ( b) -> b.setKey("feature/foo").setBranchType(SHORT).setMergeBranchUuid(project.uuid()));
        db.getDbClient().snapshotDao().insert(db.getSession(), newAnalysis(shortLivingBranch).setLast(true).setCreatedAt(parseDateTime("2017-04-03T13:37:00+0100").getTime()));
        db.measures().insertLiveMeasure(shortLivingBranch, qualityGateStatus, ( m) -> m.setData("OK"));
        RuleDefinitionDto rule = db.rules().insert();
        db.issues().insert(rule, shortLivingBranch, shortLivingBranch, ( i) -> i.setType(BUG).setResolution(null));
        issueIndexer.indexOnStartup(Collections.emptySet());
        userSession.logIn().addProjectPermission(UserRole.USER, project);
        String json = ws.newRequest().setParam("project", project.getDbKey()).execute().getInput();
        assertJson(json).isSimilarTo(ws.getDef().responseExampleAsString());
        assertJson(ws.getDef().responseExampleAsString()).isSimilarTo(json);
    }

    @Test
    public void test_with_SCAN_EXCUTION_permission() {
        ComponentDto project = db.components().insertMainBranch(( p) -> p.setDbKey("sonarqube"));
        db.getDbClient().snapshotDao().insert(db.getSession(), newAnalysis(project).setLast(true).setCreatedAt(parseDateTime("2017-04-01T01:15:42+0100").getTime()));
        db.measures().insertLiveMeasure(project, qualityGateStatus, ( m) -> m.setData("ERROR"));
        ComponentDto shortLivingBranch = db.components().insertProjectBranch(project, ( b) -> b.setKey("feature/foo").setBranchType(SHORT).setMergeBranchUuid(project.uuid()));
        db.getDbClient().snapshotDao().insert(db.getSession(), newAnalysis(shortLivingBranch).setLast(true).setCreatedAt(parseDateTime("2017-04-03T13:37:00+0100").getTime()));
        db.measures().insertLiveMeasure(shortLivingBranch, qualityGateStatus, ( m) -> m.setData("OK"));
        RuleDefinitionDto rule = db.rules().insert();
        db.issues().insert(rule, shortLivingBranch, shortLivingBranch, ( i) -> i.setType(BUG).setResolution(null));
        issueIndexer.indexOnStartup(Collections.emptySet());
        userSession.logIn().addProjectPermission(SCAN_EXECUTION, project);
        String json = ws.newRequest().setParam("project", project.getDbKey()).execute().getInput();
        assertJson(json).isSimilarTo(ws.getDef().responseExampleAsString());
    }

    @Test
    public void main_branch() {
        ComponentDto project = db.components().insertMainBranch();
        userSession.logIn().addProjectPermission(UserRole.USER, project);
        ListWsResponse response = ws.newRequest().setParam("project", project.getDbKey()).executeProtobuf(ListWsResponse.class);
        assertThat(response.getBranchesList()).extracting(Branch::getName, Branch::getIsMain, Branch::getType).containsExactlyInAnyOrder(tuple("master", true, BranchType.LONG));
    }

    @Test
    public void main_branch_with_specified_name() {
        OrganizationDto organization = db.organizations().insert();
        ComponentDto project = db.components().insertMainBranch(organization, "head");
        userSession.logIn().addProjectPermission(UserRole.USER, project);
        ListWsResponse response = ws.newRequest().setParam("project", project.getDbKey()).executeProtobuf(ListWsResponse.class);
        assertThat(response.getBranchesList()).extracting(Branch::getName, Branch::getIsMain, Branch::getType).containsExactlyInAnyOrder(tuple("head", true, BranchType.LONG));
    }

    @Test
    public void test_project_with_zero_branches() {
        ComponentDto project = db.components().insertPrivateProject();
        userSession.logIn().addProjectPermission(UserRole.USER, project);
        String json = ws.newRequest().setParam("project", project.getDbKey()).setMediaType(JSON).execute().getInput();
        assertJson(json).isSimilarTo("{\"branches\": []}");
    }

    @Test
    public void test_project_with_branches() {
        ComponentDto project = db.components().insertMainBranch();
        db.components().insertProjectBranch(project, ( b) -> b.setKey("feature/bar"));
        db.components().insertProjectBranch(project, ( b) -> b.setKey("feature/foo"));
        userSession.logIn().addProjectPermission(UserRole.USER, project);
        ListWsResponse response = ws.newRequest().setParam("project", project.getDbKey()).executeProtobuf(ListWsResponse.class);
        assertThat(response.getBranchesList()).extracting(Branch::getName, Branch::getType).containsExactlyInAnyOrder(tuple("master", BranchType.LONG), tuple("feature/foo", BranchType.LONG), tuple("feature/bar", BranchType.LONG));
    }

    @Test
    public void short_living_branches() {
        ComponentDto project = db.components().insertMainBranch();
        userSession.logIn().addProjectPermission(UserRole.USER, project);
        ComponentDto longLivingBranch = db.components().insertProjectBranch(project, ( b) -> b.setKey("long").setBranchType(org.sonar.db.component.BranchType.LONG));
        ComponentDto shortLivingBranch = db.components().insertProjectBranch(project, ( b) -> b.setKey("short").setBranchType(SHORT).setMergeBranchUuid(longLivingBranch.uuid()));
        ComponentDto shortLivingBranchOnMaster = db.components().insertProjectBranch(project, ( b) -> b.setKey("short_on_master").setBranchType(SHORT).setMergeBranchUuid(project.uuid()));
        ListWsResponse response = ws.newRequest().setParam("project", project.getKey()).executeProtobuf(ListWsResponse.class);
        assertThat(response.getBranchesList()).extracting(Branch::getName, Branch::getType, Branch::getMergeBranch).containsExactlyInAnyOrder(tuple("master", BranchType.LONG, ""), tuple(longLivingBranch.getBranch(), BranchType.LONG, ""), tuple(shortLivingBranch.getBranch(), BranchType.SHORT, longLivingBranch.getBranch()), tuple(shortLivingBranchOnMaster.getBranch(), BranchType.SHORT, "master"));
    }

    @Test
    public void mergeBranch_is_using_default_main_name_when_main_branch_has_no_name() {
        ComponentDto project = db.components().insertMainBranch();
        userSession.logIn().addProjectPermission(UserRole.USER, project);
        ComponentDto shortLivingBranch = db.components().insertProjectBranch(project, ( b) -> b.setKey("short").setBranchType(SHORT).setMergeBranchUuid(project.uuid()));
        ProjectBranches.ShowWsResponse response = ws.newRequest().setParam("project", shortLivingBranch.getKey()).executeProtobuf(ShowWsResponse.class);
        assertThat(response.getBranch()).extracting(Branch::getName, Branch::getType, Branch::getMergeBranch).containsExactlyInAnyOrder(shortLivingBranch.getBranch(), BranchType.SHORT, "master");
    }

    @Test
    public void short_living_branch_on_removed_branch() {
        ComponentDto project = db.components().insertMainBranch();
        userSession.logIn().addProjectPermission(UserRole.USER, project);
        ComponentDto shortLivingBranch = db.components().insertProjectBranch(project, ( b) -> b.setKey("short").setBranchType(SHORT).setMergeBranchUuid("unknown"));
        ListWsResponse response = ws.newRequest().setParam("project", project.getKey()).executeProtobuf(ListWsResponse.class);
        assertThat(response.getBranchesList()).extracting(Branch::getName, Branch::getType, Branch::hasMergeBranch, Branch::getIsOrphan).containsExactlyInAnyOrder(tuple("master", BranchType.LONG, false, false), tuple(shortLivingBranch.getBranch(), BranchType.SHORT, false, true));
    }

    @Test
    public void status_on_long_living_branch() {
        ComponentDto project = db.components().insertMainBranch();
        userSession.logIn().addProjectPermission(UserRole.USER, project);
        ComponentDto branch = db.components().insertProjectBranch(project, ( b) -> b.setBranchType(org.sonar.db.component.BranchType.LONG));
        db.measures().insertLiveMeasure(branch, qualityGateStatus, ( m) -> m.setData("OK"));
        ListWsResponse response = ws.newRequest().setParam("project", project.getKey()).executeProtobuf(ListWsResponse.class);
        assertThat(response.getBranchesList()).extracting(( b) -> b.getStatus().hasQualityGateStatus(), ( b) -> b.getStatus().getQualityGateStatus()).containsExactlyInAnyOrder(tuple(false, ""), tuple(true, "OK"));
    }

    @Test
    public void status_on_short_living_branches() {
        ComponentDto project = db.components().insertMainBranch();
        userSession.logIn().addProjectPermission(UserRole.USER, project);
        ComponentDto longLivingBranch = db.components().insertProjectBranch(project, ( b) -> b.setBranchType(org.sonar.db.component.BranchType.LONG));
        ComponentDto shortLivingBranch = db.components().insertProjectBranch(project, ( b) -> b.setKey("short").setBranchType(SHORT).setMergeBranchUuid(longLivingBranch.uuid()));
        db.measures().insertLiveMeasure(shortLivingBranch, qualityGateStatus, ( m) -> m.setData("OK"));
        RuleDefinitionDto rule = db.rules().insert();
        db.issues().insert(rule, shortLivingBranch, shortLivingBranch, ( i) -> i.setType(BUG).setResolution(null));
        db.issues().insert(rule, shortLivingBranch, shortLivingBranch, ( i) -> i.setType(BUG).setResolution(RESOLUTION_FIXED));
        db.issues().insert(rule, shortLivingBranch, shortLivingBranch, ( i) -> i.setType(VULNERABILITY).setResolution(null));
        db.issues().insert(rule, shortLivingBranch, shortLivingBranch, ( i) -> i.setType(VULNERABILITY).setResolution(null));
        db.issues().insert(rule, shortLivingBranch, shortLivingBranch, ( i) -> i.setType(CODE_SMELL).setResolution(null));
        db.issues().insert(rule, shortLivingBranch, shortLivingBranch, ( i) -> i.setType(CODE_SMELL).setResolution(null));
        db.issues().insert(rule, shortLivingBranch, shortLivingBranch, ( i) -> i.setType(CODE_SMELL).setResolution(null));
        db.issues().insert(rule, shortLivingBranch, shortLivingBranch, ( i) -> i.setType(CODE_SMELL).setResolution(RESOLUTION_FALSE_POSITIVE));
        issueIndexer.indexOnStartup(Collections.emptySet());
        permissionIndexerTester.allowOnlyAnyone(project);
        ListWsResponse response = ws.newRequest().setParam("project", project.getKey()).executeProtobuf(ListWsResponse.class);
        assertThat(response.getBranchesList().stream().map(ProjectBranches.Branch::getStatus)).extracting(Status::hasBugs, Status::getBugs, Status::hasVulnerabilities, Status::getVulnerabilities, Status::hasCodeSmells, Status::getCodeSmells).containsExactlyInAnyOrder(tuple(false, 0L, false, 0L, false, 0L), tuple(false, 0L, false, 0L, false, 0L), tuple(true, 1L, true, 2L, true, 3L));
        Optional<Branch> shortBranch = response.getBranchesList().stream().filter(( b) -> b.getName().equals("short")).findFirst();
        assertThat(shortBranch).isPresent();
        assertThat(shortBranch.get().getStatus().getQualityGateStatus()).isNotEmpty();
    }

    @Test
    public void status_on_short_living_branch_with_no_issue() {
        ComponentDto project = db.components().insertMainBranch();
        userSession.logIn().addProjectPermission(UserRole.USER, project);
        ComponentDto longLivingBranch = db.components().insertProjectBranch(project, ( b) -> b.setBranchType(org.sonar.db.component.BranchType.LONG));
        db.components().insertProjectBranch(project, ( b) -> b.setBranchType(SHORT).setMergeBranchUuid(longLivingBranch.uuid()));
        issueIndexer.indexOnStartup(Collections.emptySet());
        permissionIndexerTester.allowOnlyAnyone(project);
        ListWsResponse response = ws.newRequest().setParam("project", project.getKey()).executeProtobuf(ListWsResponse.class);
        assertThat(response.getBranchesList().stream().filter(( b) -> b.getType().equals(BranchType.SHORT)).map(ProjectBranches.Branch::getStatus)).extracting(Status::getBugs, Status::getVulnerabilities, Status::getCodeSmells).containsExactlyInAnyOrder(tuple(0L, 0L, 0L));
    }

    @Test
    public void response_contains_date_of_last_analysis() {
        Long lastAnalysisLongLivingBranch = dateToLong(parseDateTime("2017-04-01T00:00:00+0100"));
        Long previousAnalysisShortLivingBranch = dateToLong(parseDateTime("2017-04-02T00:00:00+0100"));
        Long lastAnalysisShortLivingBranch = dateToLong(parseDateTime("2017-04-03T00:00:00+0100"));
        ComponentDto project = db.components().insertMainBranch();
        userSession.logIn().addProjectPermission(UserRole.USER, project);
        ComponentDto shortLivingBranch1 = db.components().insertProjectBranch(project, ( b) -> b.setBranchType(SHORT).setMergeBranchUuid(project.uuid()));
        ComponentDto longLivingBranch2 = db.components().insertProjectBranch(project, ( b) -> b.setBranchType(org.sonar.db.component.BranchType.LONG));
        ComponentDto shortLivingBranch2 = db.components().insertProjectBranch(project, ( b) -> b.setBranchType(SHORT).setMergeBranchUuid(longLivingBranch2.uuid()));
        db.getDbClient().snapshotDao().insert(db.getSession(), newAnalysis(longLivingBranch2).setCreatedAt(lastAnalysisLongLivingBranch));
        db.getDbClient().snapshotDao().insert(db.getSession(), newAnalysis(shortLivingBranch2).setCreatedAt(previousAnalysisShortLivingBranch).setLast(false));
        db.getDbClient().snapshotDao().insert(db.getSession(), newAnalysis(shortLivingBranch2).setCreatedAt(lastAnalysisShortLivingBranch));
        db.commit();
        issueIndexer.indexOnStartup(Collections.emptySet());
        permissionIndexerTester.allowOnlyAnyone(project);
        ListWsResponse response = ws.newRequest().setParam("project", project.getKey()).executeProtobuf(ListWsResponse.class);
        assertThat(response.getBranchesList()).extracting(ProjectBranches.Branch::getType, ProjectBranches.Branch::hasAnalysisDate, ( b) -> "".equals(b.getAnalysisDate()) ? null : dateToLong(parseDateTime(b.getAnalysisDate()))).containsExactlyInAnyOrder(tuple(BranchType.LONG, false, null), tuple(BranchType.SHORT, false, null), tuple(BranchType.LONG, true, lastAnalysisLongLivingBranch), tuple(BranchType.SHORT, true, lastAnalysisShortLivingBranch));
    }

    @Test
    public void application_branches() {
        ComponentDto application = db.components().insertPrivateApplication(db.getDefaultOrganization());
        db.components().insertProjectBranch(application, ( b) -> b.setKey("feature/bar"));
        db.components().insertProjectBranch(application, ( b) -> b.setKey("feature/foo"));
        userSession.logIn().addProjectPermission(UserRole.USER, application);
        ListWsResponse response = ws.newRequest().setParam("project", application.getDbKey()).executeProtobuf(ListWsResponse.class);
        assertThat(response.getBranchesList()).extracting(Branch::getName, Branch::getType).containsExactlyInAnyOrder(tuple("feature/foo", BranchType.LONG), tuple("feature/bar", BranchType.LONG));
    }

    @Test
    public void fail_when_using_branch_db_key() throws Exception {
        OrganizationDto organization = db.organizations().insert();
        ComponentDto project = db.components().insertMainBranch(organization);
        userSession.logIn().addProjectPermission(UserRole.USER, project);
        ComponentDto branch = db.components().insertProjectBranch(project);
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage(String.format("Component key '%s' not found", branch.getDbKey()));
        ws.newRequest().setParam("project", branch.getDbKey()).execute();
    }

    @Test
    public void fail_if_missing_project_parameter() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("The 'project' parameter is missing");
        ws.newRequest().execute();
    }

    @Test
    public void fail_if_not_a_reference_on_project() {
        ComponentDto project = db.components().insertPrivateProject();
        ComponentDto file = db.components().insertComponent(ComponentTesting.newFileDto(project));
        userSession.logIn().addProjectPermission(UserRole.USER, project);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Invalid project");
        ws.newRequest().setParam("project", file.getDbKey()).execute();
    }

    @Test
    public void fail_if_project_does_not_exist() {
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("Component key 'foo' not found");
        ws.newRequest().setParam("project", "foo").execute();
    }
}

