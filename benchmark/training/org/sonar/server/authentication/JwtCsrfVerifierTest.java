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


import Method.JWT;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.sonar.server.authentication.event.AuthenticationEvent;
import org.sonar.server.authentication.event.AuthenticationExceptionMatcher;


public class JwtCsrfVerifierTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private static final int TIMEOUT = 30;

    private static final String CSRF_STATE = "STATE";

    private static final String JAVA_WS_URL = "/api/metrics/create";

    private static final String LOGIN = "foo login";

    private ArgumentCaptor<Cookie> cookieArgumentCaptor = ArgumentCaptor.forClass(Cookie.class);

    private HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

    private HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

    private JwtCsrfVerifier underTest = new JwtCsrfVerifier();

    @Test
    public void generate_state() {
        String state = underTest.generateState(request, response, JwtCsrfVerifierTest.TIMEOUT);
        assertThat(state).isNotEmpty();
        Mockito.verify(response).addCookie(cookieArgumentCaptor.capture());
        verifyCookie(cookieArgumentCaptor.getValue());
    }

    @Test
    public void verify_state() {
        mockRequestCsrf(JwtCsrfVerifierTest.CSRF_STATE);
        mockPostJavaWsRequest();
        underTest.verifyState(request, JwtCsrfVerifierTest.CSRF_STATE, JwtCsrfVerifierTest.LOGIN);
    }

    @Test
    public void fail_with_AuthenticationException_when_state_header_is_not_the_same_as_state_parameter() {
        mockRequestCsrf("other value");
        mockPostJavaWsRequest();
        thrown.expect(AuthenticationExceptionMatcher.authenticationException().from(AuthenticationEvent.Source.local(JWT)).withLogin(JwtCsrfVerifierTest.LOGIN).andNoPublicMessage());
        thrown.expectMessage("Wrong CSFR in request");
        underTest.verifyState(request, JwtCsrfVerifierTest.CSRF_STATE, JwtCsrfVerifierTest.LOGIN);
    }

    @Test
    public void fail_with_AuthenticationException_when_state_is_null() {
        mockRequestCsrf(JwtCsrfVerifierTest.CSRF_STATE);
        mockPostJavaWsRequest();
        thrown.expect(AuthenticationExceptionMatcher.authenticationException().from(AuthenticationEvent.Source.local(JWT)).withLogin(JwtCsrfVerifierTest.LOGIN).andNoPublicMessage());
        thrown.expectMessage("Missing reference CSRF value");
        underTest.verifyState(request, null, JwtCsrfVerifierTest.LOGIN);
    }

    @Test
    public void fail_with_AuthenticationException_when_state_parameter_is_empty() {
        mockRequestCsrf(JwtCsrfVerifierTest.CSRF_STATE);
        mockPostJavaWsRequest();
        thrown.expect(AuthenticationExceptionMatcher.authenticationException().from(AuthenticationEvent.Source.local(JWT)).withLogin(JwtCsrfVerifierTest.LOGIN).andNoPublicMessage());
        thrown.expectMessage("Missing reference CSRF value");
        underTest.verifyState(request, "", JwtCsrfVerifierTest.LOGIN);
    }

    @Test
    public void verify_POST_request() {
        mockRequestCsrf("other value");
        Mockito.when(request.getRequestURI()).thenReturn(JwtCsrfVerifierTest.JAVA_WS_URL);
        Mockito.when(request.getMethod()).thenReturn("POST");
        thrown.expect(AuthenticationExceptionMatcher.authenticationException().from(AuthenticationEvent.Source.local(JWT)).withLogin(JwtCsrfVerifierTest.LOGIN).andNoPublicMessage());
        thrown.expectMessage("Wrong CSFR in request");
        underTest.verifyState(request, JwtCsrfVerifierTest.CSRF_STATE, JwtCsrfVerifierTest.LOGIN);
    }

    @Test
    public void verify_PUT_request() {
        mockRequestCsrf("other value");
        Mockito.when(request.getRequestURI()).thenReturn(JwtCsrfVerifierTest.JAVA_WS_URL);
        Mockito.when(request.getMethod()).thenReturn("PUT");
        thrown.expect(AuthenticationExceptionMatcher.authenticationException().from(AuthenticationEvent.Source.local(JWT)).withLogin(JwtCsrfVerifierTest.LOGIN).andNoPublicMessage());
        thrown.expectMessage("Wrong CSFR in request");
        underTest.verifyState(request, JwtCsrfVerifierTest.CSRF_STATE, JwtCsrfVerifierTest.LOGIN);
    }

    @Test
    public void verify_DELETE_request() {
        mockRequestCsrf("other value");
        Mockito.when(request.getRequestURI()).thenReturn(JwtCsrfVerifierTest.JAVA_WS_URL);
        Mockito.when(request.getMethod()).thenReturn("DELETE");
        thrown.expect(AuthenticationExceptionMatcher.authenticationException().from(AuthenticationEvent.Source.local(JWT)).withLogin(JwtCsrfVerifierTest.LOGIN).andNoPublicMessage());
        thrown.expectMessage("Wrong CSFR in request");
        underTest.verifyState(request, JwtCsrfVerifierTest.CSRF_STATE, JwtCsrfVerifierTest.LOGIN);
    }

    @Test
    public void ignore_GET_request() {
        Mockito.when(request.getRequestURI()).thenReturn(JwtCsrfVerifierTest.JAVA_WS_URL);
        Mockito.when(request.getMethod()).thenReturn("GET");
        underTest.verifyState(request, null, JwtCsrfVerifierTest.LOGIN);
    }

    @Test
    public void ignore_not_api_requests() {
        executeVerifyStateDoesNotFailOnRequest("/events", "POST");
        executeVerifyStateDoesNotFailOnRequest("/favorites", "POST");
    }

    @Test
    public void refresh_state() {
        underTest.refreshState(request, response, JwtCsrfVerifierTest.CSRF_STATE, 30);
        Mockito.verify(response).addCookie(cookieArgumentCaptor.capture());
        verifyCookie(cookieArgumentCaptor.getValue());
    }

    @Test
    public void remove_state() {
        underTest.removeState(request, response);
        Mockito.verify(response).addCookie(cookieArgumentCaptor.capture());
        Cookie cookie = cookieArgumentCaptor.getValue();
        assertThat(cookie.getValue()).isNull();
        assertThat(cookie.getMaxAge()).isEqualTo(0);
    }
}

