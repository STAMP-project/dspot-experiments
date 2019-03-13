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
package org.sonar.server.qualitygate.ws;


import Status.NONE;
import System2.INSTANCE;
import UserRole.ADMIN;
import UserRole.USER;
import WebService.Action;
import java.io.IOException;
import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.server.ws.WebService;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.DbTester;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.component.SnapshotDto;
import org.sonar.db.metric.MetricDto;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.server.component.TestComponentFinder;
import org.sonar.server.exceptions.BadRequestException;
import org.sonar.server.exceptions.ForbiddenException;
import org.sonar.server.exceptions.NotFoundException;
import org.sonar.server.tester.UserSessionRule;
import org.sonar.server.ws.WsActionTester;
import org.sonarqube.ws.Qualitygates.ProjectStatusResponse;


public class ProjectStatusActionTest {
    private static final String ANALYSIS_ID = "task-uuid";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public UserSessionRule userSession = UserSessionRule.standalone();

    @Rule
    public DbTester db = DbTester.create(INSTANCE);

    private DbClient dbClient = db.getDbClient();

    private DbSession dbSession = db.getSession();

    private WsActionTester ws = new WsActionTester(new ProjectStatusAction(dbClient, TestComponentFinder.from(db), userSession));

    @Test
    public void test_definition() {
        WebService.Action action = ws.getDef();
        assertThat(action.params()).extracting(WebService.Param::key, WebService.Param::isRequired).containsExactlyInAnyOrder(tuple("analysisId", false), tuple("projectKey", false), tuple("projectId", false));
    }

    @Test
    public void test_json_example() throws IOException {
        OrganizationDto organization = db.organizations().insert();
        ComponentDto project = db.components().insertPrivateProject(organization);
        userSession.addProjectPermission(USER, project);
        MetricDto gateDetailsMetric = insertGateDetailMetric();
        SnapshotDto snapshot = dbClient.snapshotDao().insert(dbSession, newAnalysis(project).setPeriodMode("last_version").setPeriodParam("2015-12-07").setPeriodDate(956789123987L));
        dbClient.measureDao().insert(dbSession, newMeasureDto(gateDetailsMetric, project, snapshot).setData(IOUtils.toString(getClass().getResource("ProjectStatusActionTest/measure_data.json"))));
        dbSession.commit();
        String response = ws.newRequest().setParam("analysisId", snapshot.getUuid()).execute().getInput();
        assertJson(response).isSimilarTo(getClass().getResource("project_status-example.json"));
    }

    @Test
    public void return_past_status_when_project_is_referenced_by_past_analysis_id() throws IOException {
        OrganizationDto organization = db.organizations().insert();
        ComponentDto project = db.components().insertPrivateProject(organization);
        SnapshotDto pastAnalysis = dbClient.snapshotDao().insert(dbSession, newAnalysis(project).setLast(false).setPeriodMode("last_version").setPeriodParam("2015-12-07").setPeriodDate(956789123987L));
        SnapshotDto lastAnalysis = dbClient.snapshotDao().insert(dbSession, newAnalysis(project).setLast(true).setPeriodMode("last_version").setPeriodParam("2016-12-07").setPeriodDate(1500L));
        MetricDto gateDetailsMetric = insertGateDetailMetric();
        dbClient.measureDao().insert(dbSession, newMeasureDto(gateDetailsMetric, project, pastAnalysis).setData(IOUtils.toString(getClass().getResource("ProjectStatusActionTest/measure_data.json"))));
        dbClient.measureDao().insert(dbSession, newMeasureDto(gateDetailsMetric, project, lastAnalysis).setData("not_used"));
        dbSession.commit();
        userSession.addProjectPermission(USER, project);
        String response = ws.newRequest().setParam(QualityGatesWsParameters.PARAM_ANALYSIS_ID, pastAnalysis.getUuid()).execute().getInput();
        assertJson(response).isSimilarTo(getClass().getResource("project_status-example.json"));
    }

    @Test
    public void return_live_status_when_project_is_referenced_by_its_id() throws IOException {
        OrganizationDto organization = db.organizations().insert();
        ComponentDto project = db.components().insertPrivateProject(organization);
        dbClient.snapshotDao().insert(dbSession, newAnalysis(project).setPeriodMode("last_version").setPeriodParam("2015-12-07").setPeriodDate(956789123987L));
        MetricDto gateDetailsMetric = insertGateDetailMetric();
        dbClient.liveMeasureDao().insert(dbSession, newLiveMeasure(project, gateDetailsMetric).setData(IOUtils.toString(getClass().getResource("ProjectStatusActionTest/measure_data.json"))));
        dbSession.commit();
        userSession.addProjectPermission(USER, project);
        String response = ws.newRequest().setParam(QualityGatesWsParameters.PARAM_PROJECT_ID, project.uuid()).execute().getInput();
        assertJson(response).isSimilarTo(getClass().getResource("project_status-example.json"));
    }

    @Test
    public void return_live_status_when_project_is_referenced_by_its_key() throws IOException {
        OrganizationDto organization = db.organizations().insert();
        ComponentDto project = db.components().insertPrivateProject(organization);
        dbClient.snapshotDao().insert(dbSession, newAnalysis(project).setPeriodMode("last_version").setPeriodParam("2015-12-07").setPeriodDate(956789123987L));
        MetricDto gateDetailsMetric = insertGateDetailMetric();
        dbClient.liveMeasureDao().insert(dbSession, newLiveMeasure(project, gateDetailsMetric).setData(IOUtils.toString(getClass().getResource("ProjectStatusActionTest/measure_data.json"))));
        dbSession.commit();
        userSession.addProjectPermission(USER, project);
        String response = ws.newRequest().setParam(QualityGatesWsParameters.PARAM_PROJECT_KEY, project.getKey()).execute().getInput();
        assertJson(response).isSimilarTo(getClass().getResource("project_status-example.json"));
    }

    @Test
    public void return_undefined_status_if_specified_analysis_is_not_found() {
        OrganizationDto organization = db.organizations().insert();
        ComponentDto project = db.components().insertPrivateProject(organization);
        SnapshotDto snapshot = dbClient.snapshotDao().insert(dbSession, newAnalysis(project));
        dbSession.commit();
        userSession.addProjectPermission(USER, project);
        ProjectStatusResponse result = ws.newRequest().setParam(QualityGatesWsParameters.PARAM_ANALYSIS_ID, snapshot.getUuid()).executeProtobuf(ProjectStatusResponse.class);
        assertThat(result.getProjectStatus().getStatus()).isEqualTo(NONE);
        assertThat(result.getProjectStatus().getConditionsCount()).isEqualTo(0);
    }

    @Test
    public void return_undefined_status_if_project_is_not_analyzed() {
        OrganizationDto organization = db.organizations().insert();
        ComponentDto project = db.components().insertPrivateProject(organization);
        userSession.addProjectPermission(USER, project);
        ProjectStatusResponse result = ws.newRequest().setParam(QualityGatesWsParameters.PARAM_PROJECT_ID, project.uuid()).executeProtobuf(ProjectStatusResponse.class);
        assertThat(result.getProjectStatus().getStatus()).isEqualTo(NONE);
        assertThat(result.getProjectStatus().getConditionsCount()).isEqualTo(0);
    }

    @Test
    public void project_administrator_is_allowed_to_get_project_status() {
        OrganizationDto organization = db.organizations().insert();
        ComponentDto project = db.components().insertPrivateProject(organization);
        SnapshotDto snapshot = dbClient.snapshotDao().insert(dbSession, newAnalysis(project));
        dbSession.commit();
        userSession.addProjectPermission(ADMIN, project);
        ws.newRequest().setParam(QualityGatesWsParameters.PARAM_ANALYSIS_ID, snapshot.getUuid()).executeProtobuf(ProjectStatusResponse.class);
    }

    @Test
    public void project_user_is_allowed_to_get_project_status() {
        OrganizationDto organization = db.organizations().insert();
        ComponentDto project = db.components().insertPrivateProject(organization);
        SnapshotDto snapshot = dbClient.snapshotDao().insert(dbSession, newAnalysis(project));
        dbSession.commit();
        userSession.addProjectPermission(USER, project);
        ws.newRequest().setParam(QualityGatesWsParameters.PARAM_ANALYSIS_ID, snapshot.getUuid()).executeProtobuf(ProjectStatusResponse.class);
    }

    @Test
    public void default_organization_is_used_when_no_organization_parameter() {
        OrganizationDto organization = db.getDefaultOrganization();
        ComponentDto project = db.components().insertPrivateProject(organization);
        userSession.logIn().addProjectPermission(USER, project);
        ProjectStatusResponse result = ws.newRequest().setParam(QualityGatesWsParameters.PARAM_PROJECT_ID, project.uuid()).executeProtobuf(ProjectStatusResponse.class);
        assertThat(result.getProjectStatus().getStatus()).isEqualTo(NONE);
    }

    @Test
    public void fail_if_no_snapshot_id_found() {
        OrganizationDto organization = db.organizations().insert();
        logInAsSystemAdministrator();
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("Analysis with id 'task-uuid' is not found");
        ws.newRequest().setParam(QualityGatesWsParameters.PARAM_ANALYSIS_ID, ProjectStatusActionTest.ANALYSIS_ID).executeProtobuf(ProjectStatusResponse.class);
    }

    @Test
    public void fail_if_insufficient_privileges() {
        OrganizationDto organization = db.organizations().insert();
        ComponentDto project = db.components().insertPrivateProject(organization);
        SnapshotDto snapshot = dbClient.snapshotDao().insert(dbSession, newAnalysis(project));
        dbSession.commit();
        userSession.logIn();
        expectedException.expect(ForbiddenException.class);
        ws.newRequest().setParam(QualityGatesWsParameters.PARAM_ANALYSIS_ID, snapshot.getUuid()).executeProtobuf(ProjectStatusResponse.class);
    }

    @Test
    public void fail_if_project_id_and_ce_task_id_provided() {
        OrganizationDto organization = db.organizations().insert();
        ComponentDto project = db.components().insertPrivateProject(organization);
        logInAsSystemAdministrator();
        expectedException.expect(BadRequestException.class);
        expectedException.expectMessage("Either 'analysisId', 'projectId' or 'projectKey' must be provided");
        ws.newRequest().setParam(QualityGatesWsParameters.PARAM_ANALYSIS_ID, "analysis-id").setParam(QualityGatesWsParameters.PARAM_PROJECT_ID, "project-uuid").setParam(QualityGatesWsParameters.PARAM_ORGANIZATION, organization.getKey()).execute().getInput();
    }

    @Test
    public void fail_if_no_parameter_provided() {
        logInAsSystemAdministrator();
        expectedException.expect(BadRequestException.class);
        expectedException.expectMessage("Either 'analysisId', 'projectId' or 'projectKey' must be provided");
        ws.newRequest().execute().getInput();
    }

    @Test
    public void fail_when_using_branch_db_key() throws Exception {
        OrganizationDto organization = db.organizations().insert();
        ComponentDto project = db.components().insertMainBranch(organization);
        userSession.logIn().addProjectPermission(ADMIN, project);
        ComponentDto branch = db.components().insertProjectBranch(project);
        SnapshotDto snapshot = db.components().insertSnapshot(branch);
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage(String.format("Component key '%s' not found", branch.getDbKey()));
        ws.newRequest().setParam(QualityGatesWsParameters.PARAM_PROJECT_KEY, branch.getDbKey()).setParam(QualityGatesWsParameters.PARAM_ORGANIZATION, organization.getKey()).execute();
    }

    @Test
    public void fail_when_using_branch_uuid() {
        OrganizationDto organization = db.organizations().insert();
        ComponentDto project = db.components().insertMainBranch(organization);
        userSession.logIn().addProjectPermission(ADMIN, project);
        ComponentDto branch = db.components().insertProjectBranch(project);
        SnapshotDto snapshot = db.components().insertSnapshot(branch);
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage(String.format("Component id '%s' not found", branch.uuid()));
        ws.newRequest().setParam("projectId", branch.uuid()).execute();
    }
}

