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
package org.sonar.server.favorite.ws;


import UserRole.ADMIN;
import UserRole.USER;
import WebService.Action;
import java.net.HttpURLConnection;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.server.ws.WebService;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.DbTester;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.db.property.PropertyDto;
import org.sonar.db.property.PropertyQuery;
import org.sonar.server.component.TestComponentFinder;
import org.sonar.server.exceptions.ForbiddenException;
import org.sonar.server.exceptions.NotFoundException;
import org.sonar.server.exceptions.UnauthorizedException;
import org.sonar.server.favorite.FavoriteUpdater;
import org.sonar.server.tester.UserSessionRule;
import org.sonar.server.ws.TestResponse;
import org.sonar.server.ws.WsActionTester;


public class AddActionTest {
    private static final String PROJECT_KEY = "project-key";

    private static final String PROJECT_UUID = "project-uuid";

    private static final int USER_ID = 123;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public UserSessionRule userSession = UserSessionRule.standalone();

    @Rule
    public DbTester db = DbTester.create();

    private DbClient dbClient = db.getDbClient();

    private DbSession dbSession = db.getSession();

    private FavoriteUpdater favoriteUpdater = new FavoriteUpdater(dbClient);

    private WsActionTester ws = new WsActionTester(new AddAction(userSession, dbClient, favoriteUpdater, TestComponentFinder.from(db)));

    @Test
    public void add_a_project() {
        ComponentDto project = insertProjectAndPermissions();
        TestResponse result = call(AddActionTest.PROJECT_KEY);
        assertThat(result.getStatus()).isEqualTo(HttpURLConnection.HTTP_NO_CONTENT);
        List<PropertyDto> favorites = dbClient.propertiesDao().selectByQuery(PropertyQuery.builder().setUserId(AddActionTest.USER_ID).setKey("favourite").build(), dbSession);
        assertThat(favorites).hasSize(1);
        PropertyDto favorite = favorites.get(0);
        assertThat(favorite).extracting(PropertyDto::getResourceId, PropertyDto::getUserId, PropertyDto::getKey).containsOnly(project.getId(), AddActionTest.USER_ID, "favourite");
    }

    @Test
    public void add_a_file() {
        ComponentDto project = insertProjectAndPermissions();
        ComponentDto file = db.components().insertComponent(newFileDto(project));
        userSession.addProjectPermission(USER, project, file);
        call(file.getDbKey());
        List<PropertyDto> favorites = dbClient.propertiesDao().selectByQuery(PropertyQuery.builder().setUserId(AddActionTest.USER_ID).setKey("favourite").build(), dbSession);
        assertThat(favorites).hasSize(1);
        PropertyDto favorite = favorites.get(0);
        assertThat(favorite).extracting(PropertyDto::getResourceId, PropertyDto::getUserId, PropertyDto::getKey).containsOnly(file.getId(), AddActionTest.USER_ID, "favourite");
    }

    @Test
    public void fail_when_no_browse_permission_on_the_project() {
        ComponentDto project = insertProject();
        userSession.logIn();
        userSession.addProjectPermission(ADMIN, project);
        expectedException.expect(ForbiddenException.class);
        call(AddActionTest.PROJECT_KEY);
    }

    @Test
    public void fail_when_component_is_not_found() {
        userSession.logIn();
        expectedException.expect(NotFoundException.class);
        call("P42");
    }

    @Test
    public void fail_when_user_is_not_authenticated() {
        insertProject();
        expectedException.expect(UnauthorizedException.class);
        call(AddActionTest.PROJECT_KEY);
    }

    @Test
    public void fail_when_using_branch_db_key() throws Exception {
        OrganizationDto organization = db.organizations().insert();
        ComponentDto project = db.components().insertMainBranch(organization);
        userSession.logIn().addProjectPermission(USER, project);
        ComponentDto branch = db.components().insertProjectBranch(project);
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage(String.format("Component key '%s' not found", branch.getDbKey()));
        call(branch.getDbKey());
    }

    @Test
    public void definition() {
        WebService.Action definition = ws.getDef();
        assertThat(definition.key()).isEqualTo("add");
        assertThat(definition.isPost()).isTrue();
        assertThat(definition.param("component").isRequired()).isTrue();
    }
}

