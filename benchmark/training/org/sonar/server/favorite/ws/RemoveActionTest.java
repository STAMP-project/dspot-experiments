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


import UserRole.USER;
import WebService.Action;
import java.net.HttpURLConnection;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.server.ws.WebService;
import org.sonar.db.DbClient;
import org.sonar.db.DbTester;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.server.component.TestComponentFinder;
import org.sonar.server.exceptions.NotFoundException;
import org.sonar.server.exceptions.UnauthorizedException;
import org.sonar.server.favorite.FavoriteUpdater;
import org.sonar.server.tester.UserSessionRule;
import org.sonar.server.ws.TestResponse;
import org.sonar.server.ws.WsActionTester;


public class RemoveActionTest {
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

    private FavoriteUpdater favoriteUpdater = new FavoriteUpdater(dbClient);

    private WsActionTester ws = new WsActionTester(new RemoveAction(userSession, dbClient, favoriteUpdater, TestComponentFinder.from(db)));

    @Test
    public void remove_a_favorite_project() {
        ComponentDto project = insertProjectAndPermissions();
        ComponentDto file = db.components().insertComponent(newFileDto(project));
        db.favorites().add(project, RemoveActionTest.USER_ID);
        db.favorites().add(file, RemoveActionTest.USER_ID);
        TestResponse result = call(RemoveActionTest.PROJECT_KEY);
        assertThat(result.getStatus()).isEqualTo(HttpURLConnection.HTTP_NO_CONTENT);
        assertThat(db.favorites().hasFavorite(project, RemoveActionTest.USER_ID)).isFalse();
        assertThat(db.favorites().hasFavorite(file, RemoveActionTest.USER_ID)).isTrue();
    }

    @Test
    public void fail_if_not_already_a_favorite() {
        insertProjectAndPermissions();
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage((("Component '" + (RemoveActionTest.PROJECT_KEY)) + "' is not a favorite"));
        call(RemoveActionTest.PROJECT_KEY);
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
        call(RemoveActionTest.PROJECT_KEY);
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
        assertThat(definition.key()).isEqualTo("remove");
        assertThat(definition.isPost()).isTrue();
        assertThat(definition.param("component").isRequired()).isTrue();
    }
}

