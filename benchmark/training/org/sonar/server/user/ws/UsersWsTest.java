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
package org.sonar.server.user.ws;


import WebService.Action;
import WebService.Controller;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.api.server.ws.WebService;
import org.sonar.db.DbTester;
import org.sonar.server.authentication.CredentialsLocalAuthentication;
import org.sonar.server.tester.UserSessionRule;


public class UsersWsTest {
    @Rule
    public UserSessionRule userSessionRule = UserSessionRule.standalone();

    @Rule
    public DbTester db = DbTester.create();

    private Controller controller;

    private CredentialsLocalAuthentication localAuthentication = new CredentialsLocalAuthentication(db.getDbClient());

    @Test
    public void define_controller() {
        assertThat(controller).isNotNull();
        assertThat(controller.description()).isNotEmpty();
        assertThat(controller.since()).isEqualTo("3.6");
        assertThat(controller.actions()).hasSize(4);
    }

    @Test
    public void define_search_action() {
        WebService.Action action = controller.action("search");
        assertThat(action).isNotNull();
        assertThat(action.isPost()).isFalse();
        assertThat(action.responseExampleAsString()).isNotEmpty();
        assertThat(action.params()).hasSize(4);
    }

    @Test
    public void define_create_action() {
        WebService.Action action = controller.action("create");
        assertThat(action).isNotNull();
        assertThat(action.isPost()).isTrue();
        assertThat(action.params()).hasSize(7);
    }

    @Test
    public void define_update_action() {
        WebService.Action action = controller.action("update");
        assertThat(action).isNotNull();
        assertThat(action.isPost()).isTrue();
        assertThat(action.params()).hasSize(5);
    }

    @Test
    public void define_change_password_action() {
        WebService.Action action = controller.action("change_password");
        assertThat(action).isNotNull();
        assertThat(action.isPost()).isTrue();
        assertThat(action.params()).hasSize(3);
    }
}

