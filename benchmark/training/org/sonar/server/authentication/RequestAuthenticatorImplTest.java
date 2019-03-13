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
package org.sonar.server.authentication;


import AuthenticationEvent.Source;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.db.user.UserDto;
import org.sonar.db.user.UserTesting;
import org.sonar.server.authentication.event.AuthenticationException;
import org.sonar.server.tester.MockUserSession;
import org.sonar.server.user.UserSession;
import org.sonar.server.user.UserSessionFactory;


public class RequestAuthenticatorImplTest {
    private static final UserDto A_USER = UserTesting.newUserDto();

    private HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

    private HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

    private JwtHttpHandler jwtHttpHandler = Mockito.mock(JwtHttpHandler.class);

    private BasicAuthentication basicAuthentication = Mockito.mock(BasicAuthentication.class);

    private HttpHeadersAuthentication httpHeadersAuthentication = Mockito.mock(HttpHeadersAuthentication.class);

    private UserSessionFactory sessionFactory = Mockito.mock(UserSessionFactory.class);

    private CustomAuthentication customAuthentication1 = Mockito.mock(CustomAuthentication.class);

    private CustomAuthentication customAuthentication2 = Mockito.mock(CustomAuthentication.class);

    private RequestAuthenticator underTest = new RequestAuthenticatorImpl(jwtHttpHandler, basicAuthentication, httpHeadersAuthentication, sessionFactory, new CustomAuthentication[]{ customAuthentication1, customAuthentication2 });

    @Test
    public void authenticate_from_jwt_token() {
        Mockito.when(httpHeadersAuthentication.authenticate(request, response)).thenReturn(Optional.empty());
        Mockito.when(jwtHttpHandler.validateToken(request, response)).thenReturn(Optional.of(RequestAuthenticatorImplTest.A_USER));
        assertThat(underTest.authenticate(request, response).getUuid()).isEqualTo(RequestAuthenticatorImplTest.A_USER.getUuid());
        Mockito.verify(response, Mockito.never()).setStatus(ArgumentMatchers.anyInt());
    }

    @Test
    public void authenticate_from_basic_header() {
        Mockito.when(basicAuthentication.authenticate(request)).thenReturn(Optional.of(RequestAuthenticatorImplTest.A_USER));
        Mockito.when(httpHeadersAuthentication.authenticate(request, response)).thenReturn(Optional.empty());
        Mockito.when(jwtHttpHandler.validateToken(request, response)).thenReturn(Optional.empty());
        assertThat(underTest.authenticate(request, response).getUuid()).isEqualTo(RequestAuthenticatorImplTest.A_USER.getUuid());
        Mockito.verify(jwtHttpHandler).validateToken(request, response);
        Mockito.verify(basicAuthentication).authenticate(request);
        Mockito.verify(response, Mockito.never()).setStatus(ArgumentMatchers.anyInt());
    }

    @Test
    public void authenticate_from_sso() {
        Mockito.when(httpHeadersAuthentication.authenticate(request, response)).thenReturn(Optional.of(RequestAuthenticatorImplTest.A_USER));
        Mockito.when(jwtHttpHandler.validateToken(request, response)).thenReturn(Optional.empty());
        assertThat(underTest.authenticate(request, response).getUuid()).isEqualTo(RequestAuthenticatorImplTest.A_USER.getUuid());
        Mockito.verify(httpHeadersAuthentication).authenticate(request, response);
        Mockito.verify(jwtHttpHandler, Mockito.never()).validateToken(request, response);
        Mockito.verify(response, Mockito.never()).setStatus(ArgumentMatchers.anyInt());
    }

    @Test
    public void return_empty_if_not_authenticated() {
        Mockito.when(jwtHttpHandler.validateToken(request, response)).thenReturn(Optional.empty());
        Mockito.when(httpHeadersAuthentication.authenticate(request, response)).thenReturn(Optional.empty());
        Mockito.when(basicAuthentication.authenticate(request)).thenReturn(Optional.empty());
        UserSession session = underTest.authenticate(request, response);
        assertThat(session.isLoggedIn()).isFalse();
        assertThat(session.getUuid()).isNull();
        Mockito.verify(response, Mockito.never()).setStatus(ArgumentMatchers.anyInt());
    }

    @Test
    public void delegate_to_CustomAuthentication() {
        Mockito.when(customAuthentication1.authenticate(request, response)).thenReturn(Optional.of(new MockUserSession("foo")));
        UserSession session = underTest.authenticate(request, response);
        assertThat(session.getLogin()).isEqualTo("foo");
    }

    @Test
    public void CustomAuthentication_has_priority_over_core_authentications() {
        // use-case: both custom and core authentications check the HTTP header "Authorization".
        // The custom authentication should be able to test the header because that the core authentication
        // throws an exception.
        Mockito.when(customAuthentication1.authenticate(request, response)).thenReturn(Optional.of(new MockUserSession("foo")));
        Mockito.when(basicAuthentication.authenticate(request)).thenThrow(AuthenticationException.newBuilder().setSource(Source.sso()).setMessage("message").build());
        UserSession session = underTest.authenticate(request, response);
        assertThat(session.getLogin()).isEqualTo("foo");
    }
}

