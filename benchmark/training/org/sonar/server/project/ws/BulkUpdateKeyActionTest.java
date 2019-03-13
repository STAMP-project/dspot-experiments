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


import UserRole.USER;
import WebService.Action;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.api.server.ws.WebService;
import org.sonar.api.utils.System2;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.DbTester;
import org.sonar.db.component.ComponentDbTester;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.component.ComponentTesting;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.server.component.ComponentFinder;
import org.sonar.server.component.ComponentService;
import org.sonar.server.component.TestComponentFinder;
import org.sonar.server.es.EsTester;
import org.sonar.server.exceptions.BadRequestException;
import org.sonar.server.exceptions.ForbiddenException;
import org.sonar.server.exceptions.NotFoundException;
import org.sonar.server.tester.UserSessionRule;
import org.sonar.server.ws.WsActionTester;
import org.sonarqube.ws.Projects.BulkUpdateKeyWsResponse;


public class BulkUpdateKeyActionTest {
    private static final String MY_PROJECT_KEY = "my_project";

    private static final String FROM = "my_";

    private static final String TO = "your_";

    private System2 system2 = System2.INSTANCE;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public UserSessionRule userSession = UserSessionRule.standalone().logIn().setRoot();

    @Rule
    public EsTester es = EsTester.create();

    @Rule
    public DbTester db = DbTester.create(system2);

    private ComponentDbTester componentDb = new ComponentDbTester(db);

    private DbClient dbClient = db.getDbClient();

    private ComponentFinder componentFinder = TestComponentFinder.from(db);

    private ComponentService componentService = Mockito.mock(ComponentService.class);

    private WsActionTester ws = new WsActionTester(new BulkUpdateKeyAction(dbClient, componentFinder, componentService, userSession));

    @Test
    public void json_example() {
        OrganizationDto organizationDto = db.organizations().insert();
        ComponentDto project = componentDb.insertComponent(ComponentTesting.newPrivateProjectDto(organizationDto).setDbKey("my_project"));
        componentDb.insertComponent(newModuleDto(project).setDbKey("my_project:module_1"));
        ComponentDto anotherProject = componentDb.insertComponent(ComponentTesting.newPrivateProjectDto(organizationDto).setDbKey("another_project"));
        componentDb.insertComponent(newModuleDto(anotherProject).setDbKey("my_new_project:module_1"));
        ComponentDto module2 = componentDb.insertComponent(newModuleDto(project).setDbKey("my_project:module_2"));
        componentDb.insertComponent(newFileDto(module2, null));
        String result = ws.newRequest().setParam(PARAM_PROJECT, "my_project").setParam(PARAM_FROM, "my_").setParam(PARAM_TO, "my_new_").setParam(PARAM_DRY_RUN, String.valueOf(true)).execute().getInput();
        assertJson(result).withStrictArrayOrder().isSimilarTo(getClass().getResource("bulk_update_key-example.json"));
    }

    @Test
    public void dry_run_by_key() {
        insertMyProject();
        BulkUpdateKeyWsResponse result = callDryRunByKey(BulkUpdateKeyActionTest.MY_PROJECT_KEY, BulkUpdateKeyActionTest.FROM, BulkUpdateKeyActionTest.TO);
        assertThat(result.getKeysCount()).isEqualTo(1);
        assertThat(result.getKeys(0).getNewKey()).isEqualTo("your_project");
    }

    @Test
    public void bulk_update_project_key() {
        ComponentDto project = insertMyProject();
        ComponentDto module = componentDb.insertComponent(newModuleDto(project).setDbKey("my_project:root:module"));
        ComponentDto inactiveModule = componentDb.insertComponent(newModuleDto(project).setDbKey("my_project:root:inactive_module").setEnabled(false));
        ComponentDto file = componentDb.insertComponent(newFileDto(module, null).setDbKey("my_project:root:module:src/File.xoo"));
        ComponentDto inactiveFile = componentDb.insertComponent(newFileDto(module, null).setDbKey("my_project:root:module:src/InactiveFile.xoo").setEnabled(false));
        BulkUpdateKeyWsResponse result = callByUuid(project.uuid(), BulkUpdateKeyActionTest.FROM, BulkUpdateKeyActionTest.TO);
        assertThat(result.getKeysCount()).isEqualTo(2);
        assertThat(result.getKeysList()).extracting(Key::getKey, Key::getNewKey, Key::getDuplicate).containsExactly(tuple(project.getDbKey(), "your_project", false), tuple(module.getDbKey(), "your_project:root:module", false));
        Mockito.verify(componentService).bulkUpdateKey(ArgumentMatchers.any(DbSession.class), ArgumentMatchers.eq(project), ArgumentMatchers.eq(BulkUpdateKeyActionTest.FROM), ArgumentMatchers.eq(BulkUpdateKeyActionTest.TO));
    }

    @Test
    public void bulk_update_provisioned_project_key() {
        String newKey = "provisionedProject2";
        ComponentDto provisionedProject = componentDb.insertPrivateProject();
        callByKey(provisionedProject.getDbKey(), provisionedProject.getDbKey(), newKey);
        Mockito.verify(componentService).bulkUpdateKey(ArgumentMatchers.any(DbSession.class), ArgumentMatchers.eq(provisionedProject), ArgumentMatchers.eq(provisionedProject.getDbKey()), ArgumentMatchers.eq(newKey));
    }

    @Test
    public void fail_to_bulk_update_key_using_branch_db_key() {
        ComponentDto project = db.components().insertMainBranch();
        ComponentDto branch = db.components().insertProjectBranch(project);
        userSession.addProjectPermission(USER, project);
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage(String.format("Component key '%s' not found", branch.getDbKey()));
        callByKey(branch.getDbKey(), BulkUpdateKeyActionTest.FROM, BulkUpdateKeyActionTest.TO);
    }

    @Test
    public void fail_to_bulk_update_key_using_branch_uuid() {
        ComponentDto project = db.components().insertMainBranch();
        ComponentDto branch = db.components().insertProjectBranch(project);
        userSession.addProjectPermission(USER, project);
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage(String.format("Component id '%s' not found", branch.uuid()));
        callByUuid(branch.uuid(), BulkUpdateKeyActionTest.FROM, BulkUpdateKeyActionTest.TO);
    }

    @Test
    public void fail_to_bulk_if_a_component_already_exists_with_the_same_key() {
        componentDb.insertComponent(ComponentTesting.newPrivateProjectDto(db.getDefaultOrganization()).setDbKey("my_project"));
        componentDb.insertComponent(ComponentTesting.newPrivateProjectDto(db.getDefaultOrganization()).setDbKey("your_project"));
        expectedException.expect(BadRequestException.class);
        expectedException.expectMessage("Impossible to update key: a component with key \"your_project\" already exists.");
        callByKey("my_project", "my_", "your_");
    }

    @Test
    public void fail_to_bulk_update_with_invalid_new_key() {
        insertMyProject();
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Malformed key for 'my?project'. Allowed characters are alphanumeric, '-', '_', '.' and ':', with at least one non-digit.");
        callByKey(BulkUpdateKeyActionTest.MY_PROJECT_KEY, BulkUpdateKeyActionTest.FROM, "my?");
    }

    @Test
    public void fail_to_dry_bulk_update_with_invalid_new_key() {
        insertMyProject();
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Malformed key for 'my?project'. Allowed characters are alphanumeric, '-', '_', '.' and ':', with at least one non-digit.");
        callDryRunByKey(BulkUpdateKeyActionTest.MY_PROJECT_KEY, BulkUpdateKeyActionTest.FROM, "my?");
    }

    @Test
    public void fail_to_bulk_update_if_not_project_or_module() {
        ComponentDto project = insertMyProject();
        ComponentDto file = componentDb.insertComponent(newFileDto(project, null));
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Component updated must be a module or a key");
        callByKey(file.getDbKey(), BulkUpdateKeyActionTest.FROM, BulkUpdateKeyActionTest.TO);
    }

    @Test
    public void fail_if_from_string_is_not_provided() {
        expectedException.expect(IllegalArgumentException.class);
        ComponentDto project = insertMyProject();
        callDryRunByKey(project.getDbKey(), null, BulkUpdateKeyActionTest.TO);
    }

    @Test
    public void fail_if_to_string_is_not_provided() {
        expectedException.expect(IllegalArgumentException.class);
        ComponentDto project = insertMyProject();
        callDryRunByKey(project.getDbKey(), BulkUpdateKeyActionTest.FROM, null);
    }

    @Test
    public void fail_if_uuid_nor_key_provided() {
        expectedException.expect(IllegalArgumentException.class);
        call(null, null, BulkUpdateKeyActionTest.FROM, BulkUpdateKeyActionTest.TO, false);
    }

    @Test
    public void fail_if_uuid_and_key_provided() {
        expectedException.expect(IllegalArgumentException.class);
        ComponentDto project = insertMyProject();
        call(project.uuid(), project.getDbKey(), BulkUpdateKeyActionTest.FROM, BulkUpdateKeyActionTest.TO, false);
    }

    @Test
    public void fail_if_project_does_not_exist() {
        expectedException.expect(NotFoundException.class);
        callDryRunByUuid("UNKNOWN_UUID", BulkUpdateKeyActionTest.FROM, BulkUpdateKeyActionTest.TO);
    }

    @Test
    public void throw_ForbiddenException_if_not_project_administrator() {
        userSession.logIn();
        ComponentDto project = insertMyProject();
        expectedException.expect(ForbiddenException.class);
        callDryRunByUuid(project.uuid(), BulkUpdateKeyActionTest.FROM, BulkUpdateKeyActionTest.TO);
    }

    @Test
    public void fail_when_using_branch_db_key() throws Exception {
        ComponentDto project = db.components().insertMainBranch();
        userSession.logIn().addProjectPermission(USER, project);
        ComponentDto branch = db.components().insertProjectBranch(project);
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage(String.format("Component key '%s' not found", branch.getDbKey()));
        callByKey(branch.getDbKey(), BulkUpdateKeyActionTest.FROM, BulkUpdateKeyActionTest.TO);
    }

    @Test
    public void fail_when_using_branch_uuid() {
        ComponentDto project = db.components().insertMainBranch();
        userSession.logIn().addProjectPermission(USER, project);
        ComponentDto branch = db.components().insertProjectBranch(project);
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage(String.format("Component id '%s' not found", branch.uuid()));
        ws.newRequest().setParam(PARAM_PROJECT_ID, branch.uuid()).setParam(PARAM_FROM, "my_").setParam(PARAM_TO, "my_new_").execute();
    }

    @Test
    public void api_definition() {
        WebService.Action definition = ws.getDef();
        assertThat(definition.isPost()).isTrue();
        assertThat(definition.since()).isEqualTo("6.1");
        assertThat(definition.key()).isEqualTo("bulk_update_key");
        assertThat(definition.params()).hasSize(5).extracting(WebService.Param::key).containsOnlyOnce("projectId", "project", "from", "to", "dryRun");
    }
}

