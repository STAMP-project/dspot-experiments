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


import Param.TEXT_QUERY;
import Qualifiers.PROJECT;
import Qualifiers.VIEW;
import System2.INSTANCE;
import java.net.HttpURLConnection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import org.apache.commons.lang.StringUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.core.util.stream.MoreCollectors;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.DbTester;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.server.component.ComponentCleanerService;
import org.sonar.server.exceptions.ForbiddenException;
import org.sonar.server.exceptions.UnauthorizedException;
import org.sonar.server.organization.BillingValidationsProxy;
import org.sonar.server.organization.TestDefaultOrganizationProvider;
import org.sonar.server.project.Project;
import org.sonar.server.project.ProjectLifeCycleListeners;
import org.sonar.server.tester.UserSessionRule;
import org.sonar.server.ws.TestResponse;
import org.sonar.server.ws.WsActionTester;


public class BulkDeleteActionTest {
    @Rule
    public DbTester db = DbTester.create(INSTANCE);

    @Rule
    public UserSessionRule userSession = UserSessionRule.standalone();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private ComponentCleanerService componentCleanerService = Mockito.mock(ComponentCleanerService.class);

    private DbClient dbClient = db.getDbClient();

    private ProjectsWsSupport support = new ProjectsWsSupport(dbClient, TestDefaultOrganizationProvider.from(db), Mockito.mock(BillingValidationsProxy.class));

    private ProjectLifeCycleListeners projectLifeCycleListeners = Mockito.mock(ProjectLifeCycleListeners.class);

    private BulkDeleteAction underTest = new BulkDeleteAction(componentCleanerService, dbClient, userSession, support, projectLifeCycleListeners);

    private WsActionTester ws = new WsActionTester(underTest);

    private OrganizationDto org1;

    private OrganizationDto org2;

    @Test
    public void delete_projects_in_default_organization_if_no_org_provided() {
        userSession.logIn().setRoot();
        OrganizationDto defaultOrganization = db.getDefaultOrganization();
        ComponentDto toDeleteInOrg1 = db.components().insertPrivateProject(org1);
        ComponentDto toDeleteInOrg2 = db.components().insertPrivateProject(defaultOrganization);
        ComponentDto toKeep = db.components().insertPrivateProject(defaultOrganization);
        TestResponse result = ws.newRequest().setParam("projectIds", (((toDeleteInOrg1.uuid()) + ",") + (toDeleteInOrg2.uuid()))).execute();
        assertThat(result.getStatus()).isEqualTo(HttpURLConnection.HTTP_NO_CONTENT);
        assertThat(result.getInput()).isEmpty();
        verifyDeleted(toDeleteInOrg2);
        verifyListenersOnProjectsDeleted(toDeleteInOrg2);
    }

    @Test
    public void delete_projects_by_keys() {
        userSession.logIn().setRoot();
        ComponentDto toDeleteInOrg1 = db.components().insertPrivateProject(org1);
        ComponentDto toDeleteInOrg2 = db.components().insertPrivateProject(org1);
        ComponentDto toKeep = db.components().insertPrivateProject(org1);
        ws.newRequest().setParam(PARAM_ORGANIZATION, org1.getKey()).setParam(PARAM_PROJECTS, (((toDeleteInOrg1.getDbKey()) + ",") + (toDeleteInOrg2.getDbKey()))).execute();
        verifyDeleted(toDeleteInOrg1, toDeleteInOrg2);
        verifyListenersOnProjectsDeleted(toDeleteInOrg1, toDeleteInOrg2);
    }

    @Test
    public void projects_that_dont_exist_are_ignored_and_dont_break_bulk_deletion() {
        userSession.logIn().setRoot();
        ComponentDto toDelete1 = db.components().insertPrivateProject(org1);
        ComponentDto toDelete2 = db.components().insertPrivateProject(org1);
        ws.newRequest().setParam("organization", org1.getKey()).setParam("projects", ((((toDelete1.getDbKey()) + ",missing,") + (toDelete2.getDbKey())) + ",doesNotExist")).execute();
        verifyDeleted(toDelete1, toDelete2);
        verifyListenersOnProjectsDeleted(toDelete1, toDelete2);
    }

    @Test
    public void old_projects() {
        userSession.logIn().addPermission(ADMINISTER, db.getDefaultOrganization());
        long aLongTimeAgo = 1000000000L;
        long recentTime = 3000000000L;
        ComponentDto oldProject = db.components().insertPublicProject();
        db.getDbClient().snapshotDao().insert(db.getSession(), newAnalysis(oldProject).setCreatedAt(aLongTimeAgo));
        ComponentDto recentProject = db.components().insertPublicProject();
        db.getDbClient().snapshotDao().insert(db.getSession(), newAnalysis(recentProject).setCreatedAt(recentTime));
        db.commit();
        ws.newRequest().setParam(PARAM_ANALYZED_BEFORE, formatDate(new Date(recentTime))).execute();
        verifyDeleted(oldProject);
        verifyListenersOnProjectsDeleted(oldProject);
    }

    @Test
    public void provisioned_projects() {
        userSession.logIn().addPermission(ADMINISTER, db.getDefaultOrganization());
        ComponentDto provisionedProject = db.components().insertPrivateProject();
        ComponentDto analyzedProject = db.components().insertPrivateProject();
        db.components().insertSnapshot(newAnalysis(analyzedProject));
        ws.newRequest().setParam(PARAM_ON_PROVISIONED_ONLY, "true").execute();
        verifyDeleted(provisionedProject);
        verifyListenersOnProjectsDeleted(provisionedProject);
    }

    @Test
    public void delete_more_than_50_projects() {
        userSession.logIn().addPermission(ADMINISTER, db.getDefaultOrganization());
        ComponentDto[] projects = IntStream.range(0, 55).mapToObj(( i) -> db.components().insertPrivateProject()).toArray(ComponentDto[]::new);
        ws.newRequest().execute();
        verifyDeleted(projects);
        verifyListenersOnProjectsDeleted(projects);
    }

    @Test
    public void projects_and_views() {
        userSession.logIn().addPermission(ADMINISTER, db.getDefaultOrganization());
        ComponentDto project = db.components().insertPrivateProject();
        ComponentDto view = db.components().insertView();
        ws.newRequest().setParam(PARAM_QUALIFIERS, String.join(",", PROJECT, VIEW)).execute();
        verifyDeleted(project, view);
        verifyListenersOnProjectsDeleted(project, view);
    }

    @Test
    public void delete_by_key_query_with_partial_match_case_insensitive() {
        userSession.logIn().addPermission(ADMINISTER, db.getDefaultOrganization());
        ComponentDto matchKeyProject = db.components().insertPrivateProject(( p) -> p.setDbKey("project-_%-key"));
        ComponentDto matchUppercaseKeyProject = db.components().insertPrivateProject(( p) -> p.setDbKey("PROJECT-_%-KEY"));
        ComponentDto noMatchProject = db.components().insertPrivateProject(( p) -> p.setDbKey("project-key-without-escaped-characters"));
        ws.newRequest().setParam(TEXT_QUERY, "JeCt-_%-k").execute();
        verifyDeleted(matchKeyProject, matchUppercaseKeyProject);
        verifyListenersOnProjectsDeleted(matchKeyProject, matchUppercaseKeyProject);
    }

    @Test
    public void throw_ForbiddenException_if_organization_administrator_does_not_set_organization_parameter() {
        userSession.logIn().addPermission(ADMINISTER, org1);
        ComponentDto project = db.components().insertPrivateProject(org1);
        expectedException.expect(ForbiddenException.class);
        expectedException.expectMessage("Insufficient privileges");
        try {
            ws.newRequest().setParam("projects", project.getDbKey()).execute();
        } finally {
            verifyNoDeletions();
            Mockito.verifyZeroInteractions(projectLifeCycleListeners);
        }
    }

    /**
     * SONAR-10356
     */
    @Test
    public void delete_only_the_1000_first_projects() {
        userSession.logIn().addPermission(ADMINISTER, org1);
        List<String> keys = IntStream.range(0, 1010).mapToObj(( i) -> "key" + i).collect(MoreCollectors.toArrayList());
        keys.forEach(( key) -> db.components().insertPrivateProject(org1, ( p) -> p.setDbKey(key)));
        ws.newRequest().setParam("organization", org1.getKey()).setParam("projects", StringUtils.join(keys, ",")).execute();
        Mockito.verify(componentCleanerService, Mockito.times(1000)).delete(ArgumentMatchers.any(DbSession.class), ArgumentMatchers.any(ComponentDto.class));
        ArgumentCaptor<Set<Project>> projectsCaptor = ArgumentCaptor.forClass(Set.class);
        Mockito.verify(projectLifeCycleListeners).onProjectsDeleted(projectsCaptor.capture());
        assertThat(projectsCaptor.getValue()).hasSize(1000);
    }

    @Test
    public void projectLifeCycleListeners_onProjectsDeleted_called_even_if_delete_fails() {
        userSession.logIn().addPermission(ADMINISTER, org1);
        ComponentDto project1 = db.components().insertPrivateProject(org1);
        ComponentDto project2 = db.components().insertPrivateProject(org1);
        ComponentDto project3 = db.components().insertPrivateProject(org1);
        ComponentCleanerService componentCleanerService = Mockito.mock(ComponentCleanerService.class);
        RuntimeException expectedException = new RuntimeException("Faking delete failing on 2nd project");
        Mockito.doNothing().doThrow(expectedException).when(componentCleanerService).delete(ArgumentMatchers.any(), ArgumentMatchers.any(ComponentDto.class));
        try {
            ws.newRequest().setParam("organization", org1.getKey()).setParam("projects", (((((project1.getDbKey()) + ",") + (project2.getDbKey())) + ",") + (project3.getDbKey()))).execute();
        } catch (RuntimeException e) {
            assertThat(e).isSameAs(expectedException);
            verifyListenersOnProjectsDeleted(project1, project2, project3);
        }
    }

    @Test
    public void organization_administrator_deletes_projects_by_keys_in_his_organization() {
        userSession.logIn().addPermission(ADMINISTER, org1);
        ComponentDto toDelete = db.components().insertPrivateProject(org1);
        ComponentDto cantBeDeleted = db.components().insertPrivateProject(org2);
        ws.newRequest().setParam("organization", org1.getKey()).setParam("projects", (((toDelete.getDbKey()) + ",") + (cantBeDeleted.getDbKey()))).execute();
        verifyDeleted(toDelete);
        verifyListenersOnProjectsDeleted(toDelete);
    }

    @Test
    public void throw_UnauthorizedException_if_not_logged_in() {
        expectedException.expect(UnauthorizedException.class);
        expectedException.expectMessage("Authentication is required");
        ws.newRequest().setParam("ids", "whatever-the-uuid").execute();
        verifyNoDeletions();
        Mockito.verifyZeroInteractions(projectLifeCycleListeners);
    }

    @Test
    public void throw_ForbiddenException_if_param_organization_is_not_set_and_not_system_administrator() {
        userSession.logIn().setNonSystemAdministrator();
        expectedException.expect(ForbiddenException.class);
        expectedException.expectMessage("Insufficient privileges");
        ws.newRequest().setParam("ids", "whatever-the-uuid").execute();
        verifyNoDeletions();
        Mockito.verifyZeroInteractions(projectLifeCycleListeners);
    }

    @Test
    public void throw_ForbiddenException_if_param_organization_is_set_but_not_organization_administrator() {
        userSession.logIn();
        expectedException.expect(ForbiddenException.class);
        expectedException.expectMessage("Insufficient privileges");
        ws.newRequest().setParam("organization", org1.getKey()).setParam("ids", "whatever-the-uuid").execute();
        verifyNoDeletions();
        Mockito.verifyZeroInteractions(projectLifeCycleListeners);
    }
}

