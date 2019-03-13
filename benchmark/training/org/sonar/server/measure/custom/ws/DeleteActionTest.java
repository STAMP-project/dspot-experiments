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
package org.sonar.server.measure.custom.ws;


import System2.INSTANCE;
import UserRole.ADMIN;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.DbTester;
import org.sonar.db.component.ComponentDto;
import org.sonar.server.exceptions.ForbiddenException;
import org.sonar.server.exceptions.UnauthorizedException;
import org.sonar.server.tester.UserSessionRule;
import org.sonar.server.ws.WsTester;


public class DeleteActionTest {
    public static final String ACTION = "delete";

    @Rule
    public UserSessionRule userSession = UserSessionRule.standalone();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public DbTester db = DbTester.create(INSTANCE);

    private DbClient dbClient = db.getDbClient();

    private final DbSession dbSession = db.getSession();

    private WsTester ws;

    @Test
    public void project_administrator_can_delete_custom_measures() throws Exception {
        ComponentDto project = db.components().insertPrivateProject();
        userSession.logIn().addProjectPermission(ADMIN, project);
        long id = insertCustomMeasure(project);
        newRequest().setParam(DeleteAction.PARAM_ID, String.valueOf(id)).execute();
        assertThat(dbClient.customMeasureDao().selectById(dbSession, id)).isNull();
    }

    @Test
    public void throw_RowNotFoundException_if_id_does_not_exist() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Custom measure with id '42' does not exist");
        newRequest().setParam(DeleteAction.PARAM_ID, "42").execute();
    }

    @Test
    public void throw_ForbiddenException_if_not_system_administrator() throws Exception {
        ComponentDto project = db.components().insertPrivateProject();
        long id = insertCustomMeasure(project);
        userSession.logIn().setNonSystemAdministrator();
        expectedException.expect(ForbiddenException.class);
        newRequest().setParam(DeleteAction.PARAM_ID, String.valueOf(id)).execute();
    }

    @Test
    public void throw_UnauthorizedException_if_not_logged_in() throws Exception {
        ComponentDto project = db.components().insertPrivateProject();
        long id = insertCustomMeasure(project);
        userSession.anonymous();
        expectedException.expect(UnauthorizedException.class);
        newRequest().setParam(DeleteAction.PARAM_ID, String.valueOf(id)).execute();
    }
}

