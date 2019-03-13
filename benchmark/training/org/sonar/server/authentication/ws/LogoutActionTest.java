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
import org.sonar.db.DbTester;
import org.sonar.db.user.UserDto;
import org.sonar.db.user.UserTesting;
import org.sonar.server.authentication.JwtHttpHandler;
import org.sonar.server.authentication.event.AuthenticationEvent;
import org.sonar.server.authentication.event.AuthenticationException;

import static Source.sso;


public class LogoutActionTest {
    private static final UserDto USER = UserTesting.newUserDto().setLogin("john");

    @Rule
    public DbTester dbTester = DbTester.create(INSTANCE);

    private HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

    private HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

    private FilterChain chain = Mockito.mock(FilterChain.class);

    private JwtHttpHandler jwtHttpHandler = Mockito.mock(JwtHttpHandler.class);

    private AuthenticationEvent authenticationEvent = Mockito.mock(AuthenticationEvent.class);

    private LogoutAction underTest = new LogoutAction(jwtHttpHandler, authenticationEvent);

    @Test
    public void do_get_pattern() {
        assertThat(underTest.doGetPattern().matches("/api/authentication/logout")).isTrue();
        assertThat(underTest.doGetPattern().matches("/api/authentication/login")).isFalse();
        assertThat(underTest.doGetPattern().matches("/api/authentication/logou")).isFalse();
        assertThat(underTest.doGetPattern().matches("/api/authentication/logoutthing")).isFalse();
        assertThat(underTest.doGetPattern().matches("/foo")).isFalse();
    }

    @Test
    public void return_400_on_get_request() throws Exception {
        Mockito.when(request.getMethod()).thenReturn("GET");
        underTest.doFilter(request, response, chain);
        Mockito.verifyZeroInteractions(jwtHttpHandler, chain);
        Mockito.verify(response).setStatus(400);
    }

    @Test
    public void logout_logged_user() throws Exception {
        setUser(LogoutActionTest.USER);
        executeRequest();
        Mockito.verify(jwtHttpHandler).removeToken(request, response);
        Mockito.verifyZeroInteractions(chain);
        Mockito.verify(authenticationEvent).logoutSuccess(request, "john");
    }

    @Test
    public void logout_unlogged_user() throws Exception {
        setNoUser();
        executeRequest();
        Mockito.verify(jwtHttpHandler).removeToken(request, response);
        Mockito.verifyZeroInteractions(chain);
        Mockito.verify(authenticationEvent).logoutSuccess(request, null);
    }

    @Test
    public void generate_auth_event_on_failure() throws Exception {
        setUser(LogoutActionTest.USER);
        AuthenticationException exception = AuthenticationException.newBuilder().setMessage("error!").setSource(sso()).build();
        Mockito.doThrow(exception).when(jwtHttpHandler).getToken(ArgumentMatchers.any(HttpServletRequest.class), ArgumentMatchers.any(HttpServletResponse.class));
        executeRequest();
        Mockito.verify(authenticationEvent).logoutFailure(request, "error!");
        Mockito.verify(jwtHttpHandler).removeToken(ArgumentMatchers.any(HttpServletRequest.class), ArgumentMatchers.any(HttpServletResponse.class));
        Mockito.verifyZeroInteractions(chain);
    }
}

