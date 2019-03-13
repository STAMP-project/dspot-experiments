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
package org.sonar.server.usertoken.ws;


import MediaTypes.JSON;
import System2.INSTANCE;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.api.utils.System2;
import org.sonar.db.DbTester;
import org.sonar.db.user.UserDto;
import org.sonar.server.exceptions.BadRequestException;
import org.sonar.server.exceptions.ForbiddenException;
import org.sonar.server.exceptions.NotFoundException;
import org.sonar.server.exceptions.ServerException;
import org.sonar.server.exceptions.UnauthorizedException;
import org.sonar.server.tester.UserSessionRule;
import org.sonar.server.usertoken.TokenGenerator;
import org.sonar.server.ws.WsActionTester;
import org.sonarqube.ws.UserTokens.GenerateWsResponse;


public class GenerateActionTest {
    private static final String TOKEN_NAME = "Third Party Application";

    @Rule
    public DbTester db = DbTester.create(INSTANCE);

    @Rule
    public UserSessionRule userSession = UserSessionRule.standalone();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private TokenGenerator tokenGenerator = Mockito.mock(TokenGenerator.class);

    private WsActionTester ws = new WsActionTester(new GenerateAction(db.getDbClient(), System2.INSTANCE, tokenGenerator, new UserTokenSupport(db.getDbClient(), userSession)));

    @Test
    public void json_example() {
        UserDto user1 = db.users().insertUser(( u) -> u.setLogin("grace.hopper"));
        UserDto user2 = db.users().insertUser(( u) -> u.setLogin("ada.lovelace"));
        logInAsSystemAdministrator();
        String response = ws.newRequest().setMediaType(JSON).setParam(UserTokenSupport.PARAM_LOGIN, user1.getLogin()).setParam(UserTokenSupport.PARAM_NAME, GenerateActionTest.TOKEN_NAME).execute().getInput();
        assertJson(response).ignoreFields("createdAt").isSimilarTo(getClass().getResource("generate-example.json"));
    }

    @Test
    public void a_user_can_generate_token_for_himself() {
        UserDto user = db.users().insertUser();
        userSession.logIn(user);
        GenerateWsResponse response = newRequest(null, GenerateActionTest.TOKEN_NAME);
        assertThat(response.getLogin()).isEqualTo(user.getLogin());
        assertThat(response.getCreatedAt()).isNotEmpty();
    }

    @Test
    public void fail_if_login_does_not_exist() {
        logInAsSystemAdministrator();
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("User with login 'unknown-login' doesn't exist");
        newRequest("unknown-login", "any-name");
    }

    @Test
    public void fail_if_name_is_blank() {
        UserDto user = db.users().insertUser();
        logInAsSystemAdministrator();
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("The 'name' parameter is missing");
        newRequest(user.getLogin(), "   ");
    }

    @Test
    public void fail_if_token_with_same_login_and_name_exists() {
        UserDto user = db.users().insertUser();
        logInAsSystemAdministrator();
        db.users().insertToken(user, ( t) -> t.setName(TOKEN_NAME));
        expectedException.expect(BadRequestException.class);
        expectedException.expectMessage(String.format("A user token for login '%s' and name 'Third Party Application' already exists", user.getLogin()));
        newRequest(user.getLogin(), GenerateActionTest.TOKEN_NAME);
    }

    @Test
    public void fail_if_token_hash_already_exists_in_db() {
        UserDto user = db.users().insertUser();
        logInAsSystemAdministrator();
        Mockito.when(tokenGenerator.hash(ArgumentMatchers.anyString())).thenReturn("987654321");
        db.users().insertToken(user, ( t) -> t.setTokenHash("987654321"));
        expectedException.expect(ServerException.class);
        expectedException.expectMessage("Error while generating token. Please try again.");
        newRequest(user.getLogin(), GenerateActionTest.TOKEN_NAME);
    }

    @Test
    public void throw_ForbiddenException_if_non_administrator_creates_token_for_someone_else() {
        UserDto user = db.users().insertUser();
        userSession.logIn().setNonSystemAdministrator();
        expectedException.expect(ForbiddenException.class);
        newRequest(user.getLogin(), GenerateActionTest.TOKEN_NAME);
    }

    @Test
    public void throw_UnauthorizedException_if_not_logged_in() {
        UserDto user = db.users().insertUser();
        userSession.anonymous();
        expectedException.expect(UnauthorizedException.class);
        newRequest(user.getLogin(), GenerateActionTest.TOKEN_NAME);
    }
}

