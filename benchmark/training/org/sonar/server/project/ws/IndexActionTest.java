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


import System2.INSTANCE;
import WebService.Action;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.server.ws.WebService;
import org.sonar.db.DbClient;
import org.sonar.db.DbTester;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.component.ComponentTesting;
import org.sonar.db.user.UserDto;
import org.sonar.server.tester.UserSessionRule;
import org.sonar.server.ws.WsActionTester;


public class IndexActionTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public UserSessionRule userSession = UserSessionRule.standalone();

    @Rule
    public DbTester db = DbTester.create(INSTANCE);

    private DbClient dbClient = db.getDbClient();

    private UserDto user;

    private WsActionTester ws = new WsActionTester(new IndexAction(dbClient, userSession));

    @Test
    public void search_all_projects() {
        insertProjectsAuthorizedForUser(ComponentTesting.newPrivateProjectDto(db.getDefaultOrganization()).setDbKey("org.jenkins-ci.plugins:sonar").setName("Jenkins Sonar Plugin"), ComponentTesting.newPrivateProjectDto(db.getDefaultOrganization()).setDbKey("org.codehaus.sonar-plugins:sonar-ant-task").setName("Sonar Ant Task"), ComponentTesting.newPrivateProjectDto(db.getDefaultOrganization()).setDbKey("org.codehaus.sonar-plugins:sonar-build-breaker-plugin").setName("Sonar Build Breaker Plugin"));
        String result = call(null, null, null);
        verifyResult(result, "search_projects.json");
    }

    @Test
    public void search_projects_with_modules() {
        ComponentDto project1 = ComponentTesting.newPrivateProjectDto(db.getDefaultOrganization()).setDbKey("org.jenkins-ci.plugins:sonar").setName("Jenkins Sonar Plugin");
        ComponentDto project2 = ComponentTesting.newPrivateProjectDto(db.getDefaultOrganization()).setDbKey("org.codehaus.sonar-plugins:sonar-ant-task").setName("Sonar Ant Task");
        insertProjectsAuthorizedForUser(project1, project2);
        db.components().insertComponents(newModuleDto(project1).setDbKey("org.jenkins-ci.plugins:sonar-common").setName("Common"), newModuleDto(project2).setDbKey("org.codehaus.sonar-plugins:sonar-ant-db").setName("Ant DB"));
        String result = call(null, null, true);
        verifyResult(result, "search_projects_with_modules.json");
    }

    @Test
    public void search_project_by_key() {
        insertProjectsAuthorizedForUser(ComponentTesting.newPrivateProjectDto(db.getDefaultOrganization()).setDbKey("org.jenkins-ci.plugins:sonar").setName("Jenkins Sonar Plugin"), ComponentTesting.newPrivateProjectDto(db.getDefaultOrganization()).setDbKey("org.codehaus.sonar-plugins:sonar-ant-task").setName("Sonar Ant Task"), ComponentTesting.newPrivateProjectDto(db.getDefaultOrganization()).setDbKey("org.codehaus.sonar-plugins:sonar-build-breaker-plugin").setName("Sonar Build Breaker Plugin"));
        String result = call("org.jenkins-ci.plugins:sonar", null, null);
        verifyResult(result, "search_project_by_key.json");
    }

    @Test
    public void search_project_by_id() {
        ComponentDto project = ComponentTesting.newPrivateProjectDto(db.getDefaultOrganization()).setDbKey("org.jenkins-ci.plugins:sonar").setName("Jenkins Sonar Plugin");
        insertProjectsAuthorizedForUser(project, ComponentTesting.newPrivateProjectDto(db.getDefaultOrganization()).setDbKey("org.codehaus.sonar-plugins:sonar-ant-task").setName("Sonar Ant Task"), ComponentTesting.newPrivateProjectDto(db.getDefaultOrganization()).setDbKey("org.codehaus.sonar-plugins:sonar-build-breaker-plugin").setName("Sonar Build Breaker Plugin"));
        String result = call(Long.toString(project.getId()), null, null);
        verifyResult(result, "search_project_by_id.json");
    }

    @Test
    public void search_projects_by_name() {
        insertProjectsAuthorizedForUser(ComponentTesting.newPrivateProjectDto(db.getDefaultOrganization()).setDbKey("org.jenkins-ci.plugins:sonar").setName("Jenkins Sonar Plugin"), ComponentTesting.newPrivateProjectDto(db.getDefaultOrganization()).setDbKey("org.codehaus.sonar-plugins:sonar-ant-task").setName("Sonar Ant Task"), ComponentTesting.newPrivateProjectDto(db.getDefaultOrganization()).setDbKey("org.codehaus.sonar-plugins:sonar-build-breaker-plugin").setName("Sonar Build Breaker Plugin"));
        String result = call(null, "Plu", null);
        verifyResult(result, "search_projects_by_name.json");
    }

    @Test
    public void search_projects_with_modules_by_name() {
        ComponentDto project1 = ComponentTesting.newPrivateProjectDto(db.getDefaultOrganization()).setDbKey("org.jenkins-ci.plugins:sonar").setName("Jenkins Sonar Plugin");
        ComponentDto project2 = ComponentTesting.newPrivateProjectDto(db.getDefaultOrganization()).setDbKey("org.codehaus.sonar-plugins:sonar-ant-task").setName("Sonar Ant Task");
        insertProjectsAuthorizedForUser(project1, project2);
        db.components().insertComponents(newModuleDto(project1).setDbKey("org.jenkins-ci.plugins:sonar-common-db").setName("Jenkins Common DB"), newModuleDto(project1).setDbKey("org.jenkins-ci.plugins:sonar-common-server").setName("Jenkins Common Server"), newModuleDto(project2).setDbKey("org.codehaus.sonar-plugins:sonar-ant-db").setName("Ant DB"));
        String result = call(null, "Com", true);
        verifyResult(result, "search_projects_with_modules_by_name.json");
    }

    @Test
    public void return_empty_list_when_no_project_match_search() {
        insertProjectsAuthorizedForUser(ComponentTesting.newPrivateProjectDto(db.getDefaultOrganization()).setDbKey("org.jenkins-ci.plugins:sonar").setName("Jenkins Sonar Plugin"), ComponentTesting.newPrivateProjectDto(db.getDefaultOrganization()).setDbKey("org.codehaus.sonar-plugins:sonar-ant-task").setName("Sonar Ant Task"), ComponentTesting.newPrivateProjectDto(db.getDefaultOrganization()).setDbKey("org.codehaus.sonar-plugins:sonar-build-breaker-plugin").setName("Sonar Build Breaker Plugin"));
        String result = call(null, "Unknown", null);
        verifyResult(result, "empty.json");
    }

    @Test
    public void return_only_projects_authorized_for_user() {
        insertProjectsAuthorizedForUser(ComponentTesting.newPrivateProjectDto(db.getDefaultOrganization()).setDbKey("org.jenkins-ci.plugins:sonar").setName("Jenkins Sonar Plugin"), ComponentTesting.newPrivateProjectDto(db.getDefaultOrganization()).setDbKey("org.codehaus.sonar-plugins:sonar-ant-task").setName("Sonar Ant Task"));
        db.components().insertComponent(ComponentTesting.newPrivateProjectDto(db.getDefaultOrganization()).setDbKey("org.codehaus.sonar-plugins:sonar-build-breaker-plugin").setName("Sonar Build Breaker Plugin"));
        String result = call(null, null, null);
        verifyResult(result, "return_only_projects_authorized_for_user.json");
    }

    @Test
    public void do_not_verify_permissions_if_user_is_root() {
        ComponentDto project = db.components().insertPrivateProject(( p) -> p.setDbKey("P1").setName("POne"));
        String result = call(null, null, null);
        userSession.setNonRoot();
        assertThat(result).isEqualTo("[]");
        userSession.setRoot();
        result = call(null, null, null);
        assertJson(result).isSimilarTo(((((((((("[" + ("  {" + "  \"id\":")) + (project.getId())) + ",") + "  \"k\":\"P1\",") + "  \"nm\":\"POne\",") + "  \"sc\":\"PRJ\",") + "   \"qu\":\"TRK\"") + "  }") + "]"));
    }

    @Test
    public void does_not_return_branches_when_searching_all_components() {
        ComponentDto project = db.components().insertMainBranch();
        ComponentDto branch = db.components().insertProjectBranch(project);
        userSession.setRoot();
        String result = call(null, null, null);
        assertJson(result).isSimilarTo(((((("[" + ("  {" + "  \"id\":")) + (project.getId())) + ",") + "  }") + "]"));
    }

    @Test
    public void does_not_return_branches_when_searching_by_key() {
        ComponentDto project = db.components().insertMainBranch();
        ComponentDto branch = db.components().insertProjectBranch(project);
        userSession.setRoot();
        String result = call(branch.getDbKey(), null, null);
        assertJson(result).isSimilarTo("[]");
    }

    @Test
    public void test_example() {
        insertProjectsAuthorizedForUser(ComponentTesting.newPrivateProjectDto(db.getDefaultOrganization()).setDbKey("org.jenkins-ci.plugins:sonar").setName("Jenkins Sonar Plugin"), ComponentTesting.newPrivateProjectDto(db.getDefaultOrganization()).setDbKey("org.codehaus.sonar-plugins:sonar-ant-task").setName("Sonar Ant Task"), ComponentTesting.newPrivateProjectDto(db.getDefaultOrganization()).setDbKey("org.codehaus.sonar-plugins:sonar-build-breaker-plugin").setName("Sonar Build Breaker Plugin"));
        String result = call(null, null, null);
        assertJson(result).ignoreFields("id").isSimilarTo(ws.getDef().responseExampleAsString());
    }

    @Test
    public void define_index_action() {
        WebService.Action action = ws.getDef();
        assertThat(action).isNotNull();
        assertThat(action.responseExampleAsString()).isNotEmpty();
        assertThat(action.params()).hasSize(8);
    }
}

