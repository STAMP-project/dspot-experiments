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
package org.sonar.server.authentication.ws;


import System2.INSTANCE;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.DbTester;
import org.sonar.db.user.UserDto;
import org.sonar.db.user.UserTesting;
import org.sonar.server.authentication.Credentials;
import org.sonar.server.authentication.CredentialsAuthentication;
import org.sonar.server.authentication.JwtHttpHandler;
import org.sonar.server.authentication.event.AuthenticationEvent;
import org.sonar.server.authentication.event.AuthenticationException;
import org.sonar.server.exceptions.UnauthorizedException;
import org.sonar.server.user.TestUserSessionFactory;
import org.sonar.server.user.ThreadLocalUserSession;


public class LoginActionTest {
    private static final String LOGIN = "LOGIN";

    private static final String PASSWORD = "PASSWORD";

    @Rule
    public DbTester dbTester = DbTester.create(INSTANCE);

    private DbClient dbClient = dbTester.getDbClient();

    private DbSession dbSession = dbTester.getSession();

    private ThreadLocalUserSession threadLocalUserSession = new ThreadLocalUserSession();

    private HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

    private HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

    private FilterChain chain = Mockito.mock(FilterChain.class);

    private CredentialsAuthentication credentialsAuthentication = Mockito.mock(CredentialsAuthentication.class);

    private JwtHttpHandler jwtHttpHandler = Mockito.mock(JwtHttpHandler.class);

    private AuthenticationEvent authenticationEvent = Mockito.mock(AuthenticationEvent.class);

    private TestUserSessionFactory userSessionFactory = TestUserSessionFactory.standalone();

    private UserDto user = UserTesting.newUserDto().setLogin(LoginActionTest.LOGIN);

    private LoginAction underTest = new LoginAction(credentialsAuthentication, jwtHttpHandler, threadLocalUserSession, authenticationEvent, userSessionFactory);

    @Test
    public void do_get_pattern() {
        assertThat(underTest.doGetPattern().matches("/api/authentication/login")).isTrue();
        assertThat(underTest.doGetPattern().matches("/api/authentication/logout")).isFalse();
        assertThat(underTest.doGetPattern().matches("/foo")).isFalse();
    }

    @Test
    public void do_authenticate() throws Exception {
        Mockito.when(credentialsAuthentication.authenticate(new Credentials(LoginActionTest.LOGIN, LoginActionTest.PASSWORD), request, Method.FORM)).thenReturn(user);
        executeRequest(LoginActionTest.LOGIN, LoginActionTest.PASSWORD);
        assertThat(threadLocalUserSession.isLoggedIn()).isTrue();
        Mockito.verify(credentialsAuthentication).authenticate(new Credentials(LoginActionTest.LOGIN, LoginActionTest.PASSWORD), request, Method.FORM);
        Mockito.verify(jwtHttpHandler).generateToken(user, request, response);
        Mockito.verifyZeroInteractions(chain);
        Mockito.verifyZeroInteractions(authenticationEvent);
    }

    @Test
    public void ignore_get_request() {
        Mockito.when(request.getMethod()).thenReturn("GET");
        underTest.doFilter(request, response, chain);
        Mockito.verifyZeroInteractions(credentialsAuthentication, jwtHttpHandler, chain);
        Mockito.verifyZeroInteractions(authenticationEvent);
    }

    @Test
    public void return_authorized_code_when_unauthorized_exception_is_thrown() throws Exception {
        Mockito.doThrow(new UnauthorizedException("error !")).when(credentialsAuthentication).authenticate(new Credentials(LoginActionTest.LOGIN, LoginActionTest.PASSWORD), request, Method.FORM);
        executeRequest(LoginActionTest.LOGIN, LoginActionTest.PASSWORD);
        Mockito.verify(response).setStatus(401);
        assertThat(threadLocalUserSession.hasSession()).isFalse();
        Mockito.verifyZeroInteractions(authenticationEvent);
    }

    @Test
    public void return_unauthorized_code_when_no_login() throws Exception {
        executeRequest(null, LoginActionTest.PASSWORD);
        Mockito.verify(response).setStatus(401);
        Mockito.verify(authenticationEvent).loginFailure(ArgumentMatchers.eq(request), ArgumentMatchers.any(AuthenticationException.class));
    }

    @Test
    public void return_unauthorized_code_when_empty_login() throws Exception {
        executeRequest("", LoginActionTest.PASSWORD);
        Mockito.verify(response).setStatus(401);
        Mockito.verify(authenticationEvent).loginFailure(ArgumentMatchers.eq(request), ArgumentMatchers.any(AuthenticationException.class));
    }

    @Test
    public void return_unauthorized_code_when_no_password() throws Exception {
        executeRequest(LoginActionTest.LOGIN, null);
        Mockito.verify(response).setStatus(401);
        Mockito.verify(authenticationEvent).loginFailure(ArgumentMatchers.eq(request), ArgumentMatchers.any(AuthenticationException.class));
    }

    @Test
    public void return_unauthorized_code_when_empty_password() throws Exception {
        executeRequest(LoginActionTest.LOGIN, "");
        Mockito.verify(response).setStatus(401);
        Mockito.verify(authenticationEvent).loginFailure(ArgumentMatchers.eq(request), ArgumentMatchers.any(AuthenticationException.class));
    }
}

