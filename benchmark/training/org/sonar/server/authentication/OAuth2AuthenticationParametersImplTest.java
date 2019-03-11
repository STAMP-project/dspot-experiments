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


import java.util.Optional;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.api.platform.Server;


public class OAuth2AuthenticationParametersImplTest {
    private static final String AUTHENTICATION_COOKIE_NAME = "AUTH-PARAMS";

    private ArgumentCaptor<Cookie> cookieArgumentCaptor = ArgumentCaptor.forClass(Cookie.class);

    private Server server = Mockito.mock(Server.class);

    private HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

    private HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

    private OAuth2AuthenticationParameters underTest = new OAuth2AuthenticationParametersImpl();

    @Test
    public void init_create_cookie() {
        Mockito.when(request.getParameter("return_to")).thenReturn("/settings");
        underTest.init(request, response);
        Mockito.verify(response).addCookie(cookieArgumentCaptor.capture());
        Cookie cookie = cookieArgumentCaptor.getValue();
        assertThat(cookie.getName()).isEqualTo(OAuth2AuthenticationParametersImplTest.AUTHENTICATION_COOKIE_NAME);
        assertThat(cookie.getValue()).isNotEmpty();
        assertThat(cookie.getPath()).isEqualTo("/");
        assertThat(cookie.isHttpOnly()).isTrue();
        assertThat(cookie.getMaxAge()).isEqualTo(300);
        assertThat(cookie.getSecure()).isFalse();
    }

    @Test
    public void init_does_not_create_cookie_when_no_parameter() {
        underTest.init(request, response);
        Mockito.verify(response, Mockito.never()).addCookie(ArgumentMatchers.any(Cookie.class));
    }

    @Test
    public void init_does_not_create_cookie_when_parameters_are_empty() {
        Mockito.when(request.getParameter("return_to")).thenReturn("");
        Mockito.when(request.getParameter("allowEmailShift")).thenReturn("");
        Mockito.when(request.getParameter("allowUpdateLogin")).thenReturn("");
        underTest.init(request, response);
        Mockito.verify(response, Mockito.never()).addCookie(ArgumentMatchers.any(Cookie.class));
    }

    @Test
    public void init_does_not_create_cookie_when_parameters_are_null() {
        Mockito.when(request.getParameter("return_to")).thenReturn(null);
        Mockito.when(request.getParameter("allowEmailShift")).thenReturn(null);
        Mockito.when(request.getParameter("allowUpdateLogin")).thenReturn(null);
        underTest.init(request, response);
        Mockito.verify(response, Mockito.never()).addCookie(ArgumentMatchers.any(Cookie.class));
    }

    @Test
    public void return_to_is_not_set_when_not_local() {
        Mockito.when(request.getParameter("return_to")).thenReturn("http://external_url");
        underTest.init(request, response);
        Mockito.verify(response, Mockito.never()).addCookie(ArgumentMatchers.any());
        Mockito.when(request.getParameter("return_to")).thenReturn("//local_file");
        underTest.init(request, response);
        Mockito.verify(response, Mockito.never()).addCookie(ArgumentMatchers.any());
        Mockito.when(request.getParameter("return_to")).thenReturn("/\\local_file");
        underTest.init(request, response);
        Mockito.verify(response, Mockito.never()).addCookie(ArgumentMatchers.any());
        Mockito.when(request.getParameter("return_to")).thenReturn("something_else");
        underTest.init(request, response);
        Mockito.verify(response, Mockito.never()).addCookie(ArgumentMatchers.any());
    }

    @Test
    public void get_return_to_parameter() {
        Mockito.when(request.getCookies()).thenReturn(new Cookie[]{ new Cookie(OAuth2AuthenticationParametersImplTest.AUTHENTICATION_COOKIE_NAME, "{\"return_to\":\"/settings\"}") });
        Optional<String> redirection = underTest.getReturnTo(request);
        assertThat(redirection).isNotEmpty();
        assertThat(redirection.get()).isEqualTo("/settings");
    }

    @Test
    public void get_return_to_is_empty_when_no_cookie() {
        Mockito.when(request.getCookies()).thenReturn(new Cookie[]{  });
        Optional<String> redirection = underTest.getReturnTo(request);
        assertThat(redirection).isEmpty();
    }

    @Test
    public void get_return_to_is_empty_when_no_value() {
        Mockito.when(request.getCookies()).thenReturn(new Cookie[]{ new Cookie(OAuth2AuthenticationParametersImplTest.AUTHENTICATION_COOKIE_NAME, "{}") });
        Optional<String> redirection = underTest.getReturnTo(request);
        assertThat(redirection).isEmpty();
    }

    @Test
    public void get_allowEmailShift_parameter() {
        Mockito.when(request.getCookies()).thenReturn(new Cookie[]{ new Cookie(OAuth2AuthenticationParametersImplTest.AUTHENTICATION_COOKIE_NAME, "{\"allowEmailShift\":\"true\"}") });
        Optional<Boolean> allowEmailShift = underTest.getAllowEmailShift(request);
        assertThat(allowEmailShift).isNotEmpty();
        assertThat(allowEmailShift.get()).isTrue();
    }

    @Test
    public void get_allowEmailShift_is_empty_when_no_cookie() {
        Mockito.when(request.getCookies()).thenReturn(new Cookie[]{  });
        Optional<Boolean> allowEmailShift = underTest.getAllowEmailShift(request);
        assertThat(allowEmailShift).isEmpty();
    }

    @Test
    public void get_allowEmailShift_is_empty_when_no_value() {
        Mockito.when(request.getCookies()).thenReturn(new Cookie[]{ new Cookie(OAuth2AuthenticationParametersImplTest.AUTHENTICATION_COOKIE_NAME, "{}") });
        Optional<Boolean> allowEmailShift = underTest.getAllowEmailShift(request);
        assertThat(allowEmailShift).isEmpty();
    }

    @Test
    public void getAllowUpdateLogin() {
        Mockito.when(request.getCookies()).thenReturn(new Cookie[]{ new Cookie(OAuth2AuthenticationParametersImplTest.AUTHENTICATION_COOKIE_NAME, "{\"allowUpdateLogin\":\"true\"}") });
        Optional<Boolean> allowLoginUpdate = underTest.getAllowUpdateLogin(request);
        assertThat(allowLoginUpdate).isNotEmpty();
        assertThat(allowLoginUpdate.get()).isTrue();
    }

    @Test
    public void getAllowUpdateLogin_is_empty_when_no_cookie() {
        Mockito.when(request.getCookies()).thenReturn(new Cookie[]{  });
        Optional<Boolean> allowLoginUpdate = underTest.getAllowUpdateLogin(request);
        assertThat(allowLoginUpdate).isEmpty();
    }

    @Test
    public void getAllowUpdateLogin_is_empty_when_no_value() {
        Mockito.when(request.getCookies()).thenReturn(new Cookie[]{ new Cookie(OAuth2AuthenticationParametersImplTest.AUTHENTICATION_COOKIE_NAME, "{}") });
        Optional<Boolean> allowLoginUpdate = underTest.getAllowUpdateLogin(request);
        assertThat(allowLoginUpdate).isEmpty();
    }

    @Test
    public void delete() {
        Mockito.when(request.getCookies()).thenReturn(new Cookie[]{ new Cookie(OAuth2AuthenticationParametersImplTest.AUTHENTICATION_COOKIE_NAME, "{\"return_to\":\"/settings\"}") });
        underTest.delete(request, response);
        Mockito.verify(response).addCookie(cookieArgumentCaptor.capture());
        Cookie updatedCookie = cookieArgumentCaptor.getValue();
        assertThat(updatedCookie.getName()).isEqualTo(OAuth2AuthenticationParametersImplTest.AUTHENTICATION_COOKIE_NAME);
        assertThat(updatedCookie.getValue()).isNull();
        assertThat(updatedCookie.getPath()).isEqualTo("/");
        assertThat(updatedCookie.getMaxAge()).isEqualTo(0);
    }
}

