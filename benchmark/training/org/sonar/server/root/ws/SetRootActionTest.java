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
package org.sonar.server.root.ws;


import System2.INSTANCE;
import WebService.Action;
import WebService.Param;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.server.ws.WebService;
import org.sonar.db.DbSession;
import org.sonar.db.DbTester;
import org.sonar.db.user.UserDao;
import org.sonar.db.user.UserDto;
import org.sonar.db.user.UserTesting;
import org.sonar.server.exceptions.NotFoundException;
import org.sonar.server.tester.UserSessionRule;
import org.sonar.server.ws.WsActionTester;


public class SetRootActionTest {
    private static final String SOME_LOGIN = "johndoe";

    @Rule
    public DbTester dbTester = DbTester.create(INSTANCE);

    @Rule
    public UserSessionRule userSessionRule = UserSessionRule.standalone();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private UserDao userDao = dbTester.getDbClient().userDao();

    private DbSession dbSession = dbTester.getSession();

    private SetRootAction underTest = new SetRootAction(userSessionRule, dbTester.getDbClient());

    private WsActionTester wsTester = new WsActionTester(underTest);

    @Test
    public void verify_definition() {
        WebService.Action action = wsTester.getDef();
        assertThat(action.key()).isEqualTo("set_root");
        assertThat(action.isInternal()).isTrue();
        assertThat(action.isPost()).isTrue();
        assertThat(action.since()).isEqualTo("6.2");
        assertThat(action.description()).isEqualTo(("Make the specified user root.<br/>" + "Requires to be root."));
        assertThat(action.responseExample()).isNull();
        assertThat(action.deprecatedKey()).isNull();
        assertThat(action.deprecatedSince()).isNull();
        assertThat(action.handler()).isSameAs(underTest);
        assertThat(action.params()).hasSize(1);
        WebService.Param param = action.param("login");
        assertThat(param.isRequired()).isTrue();
        assertThat(param.description()).isEqualTo("A user login");
        assertThat(param.defaultValue()).isNull();
        assertThat(param.deprecatedSince()).isNull();
        assertThat(param.deprecatedKey()).isNull();
        assertThat(param.exampleValue()).isEqualTo("admin");
    }

    @Test
    public void execute_fails_with_ForbiddenException_when_user_is_not_logged_in() {
        expectInsufficientPrivilegesForbiddenException();
        executeRequest(SetRootActionTest.SOME_LOGIN);
    }

    @Test
    public void execute_fails_with_ForbiddenException_when_user_is_not_root() {
        userSessionRule.logIn().setNonRoot();
        expectInsufficientPrivilegesForbiddenException();
        executeRequest(SetRootActionTest.SOME_LOGIN);
    }

    @Test
    public void execute_fails_with_IAE_when_login_param_is_not_provided() {
        logInAsRoot();
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("The 'login' parameter is missing");
        executeRequest(null);
    }

    @Test
    public void execute_makes_user_with_specified_login_root_when_it_is_not() {
        UserDto otherUser = UserTesting.newUserDto();
        userDao.insert(dbSession, otherUser);
        userDao.insert(dbSession, UserTesting.newUserDto(SetRootActionTest.SOME_LOGIN, "name", "email"));
        dbSession.commit();
        logInAsRoot();
        executeRequest(SetRootActionTest.SOME_LOGIN);
        assertThat(userDao.selectByLogin(dbSession, SetRootActionTest.SOME_LOGIN).isRoot()).isTrue();
        assertThat(userDao.selectByLogin(dbSession, otherUser.getLogin()).isRoot()).isFalse();
    }

    @Test
    public void execute_has_no_effect_when_user_is_already_root() {
        UserDto otherUser = UserTesting.newUserDto();
        userDao.insert(dbSession, otherUser);
        userDao.insert(dbSession, UserTesting.newUserDto(SetRootActionTest.SOME_LOGIN, "name", "email"));
        userDao.setRoot(dbSession, SetRootActionTest.SOME_LOGIN, true);
        dbSession.commit();
        logInAsRoot();
        executeRequest(SetRootActionTest.SOME_LOGIN);
        assertThat(userDao.selectByLogin(dbSession, SetRootActionTest.SOME_LOGIN).isRoot()).isTrue();
        assertThat(userDao.selectByLogin(dbSession, otherUser.getLogin()).isRoot()).isFalse();
    }

    @Test
    public void execute_fails_with_NotFoundException_when_user_for_specified_login_does_not_exist() {
        logInAsRoot();
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("User with login 'foo_bar' not found");
        executeRequest("foo_bar");
    }

    @Test
    public void execute_fails_with_NotFoundException_when_user_for_specified_login_is_not_active() {
        UserDto userDto = UserTesting.newUserDto().setActive(false);
        userDao.insert(dbSession, userDto);
        dbSession.commit();
        logInAsRoot();
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage((("User with login '" + (userDto.getLogin())) + "' not found"));
        executeRequest(userDto.getLogin());
    }
}

