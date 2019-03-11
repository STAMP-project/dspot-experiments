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
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.sonar.api.platform.Server;
import org.sonar.api.server.authentication.OAuth2IdentityProvider;
import org.sonar.server.authentication.event.AuthenticationExceptionMatcher;


public class OAuthCsrfVerifierTest {
    private static final String PROVIDER_NAME = "provider name";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private ArgumentCaptor<Cookie> cookieArgumentCaptor = ArgumentCaptor.forClass(Cookie.class);

    private OAuth2IdentityProvider identityProvider = Mockito.mock(OAuth2IdentityProvider.class);

    private Server server = Mockito.mock(Server.class);

    private HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

    private HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

    private OAuthCsrfVerifier underTest = new OAuthCsrfVerifier();

    @Test
    public void generate_state() {
        String state = underTest.generateState(request, response);
        assertThat(state).isNotEmpty();
        Mockito.verify(response).addCookie(cookieArgumentCaptor.capture());
        verifyCookie(cookieArgumentCaptor.getValue());
    }

    @Test
    public void verify_state() {
        String state = "state";
        Mockito.when(request.getCookies()).thenReturn(new Cookie[]{ new Cookie("OAUTHSTATE", DigestUtils.sha256Hex(state)) });
        Mockito.when(request.getParameter("aStateParameter")).thenReturn(state);
        underTest.verifyState(request, response, identityProvider, "aStateParameter");
        Mockito.verify(response).addCookie(cookieArgumentCaptor.capture());
        Cookie updatedCookie = cookieArgumentCaptor.getValue();
        assertThat(updatedCookie.getName()).isEqualTo("OAUTHSTATE");
        assertThat(updatedCookie.getValue()).isNull();
        assertThat(updatedCookie.getPath()).isEqualTo("/");
        assertThat(updatedCookie.getMaxAge()).isEqualTo(0);
    }

    @Test
    public void verify_state_using_default_state_parameter() {
        String state = "state";
        Mockito.when(request.getCookies()).thenReturn(new Cookie[]{ new Cookie("OAUTHSTATE", DigestUtils.sha256Hex(state)) });
        Mockito.when(request.getParameter("state")).thenReturn(state);
        underTest.verifyState(request, response, identityProvider);
        Mockito.verify(response).addCookie(cookieArgumentCaptor.capture());
        Cookie updatedCookie = cookieArgumentCaptor.getValue();
        assertThat(updatedCookie.getName()).isEqualTo("OAUTHSTATE");
        assertThat(updatedCookie.getValue()).isNull();
        assertThat(updatedCookie.getPath()).isEqualTo("/");
        assertThat(updatedCookie.getMaxAge()).isEqualTo(0);
    }

    @Test
    public void fail_with_AuthenticationException_when_state_cookie_is_not_the_same_as_state_parameter() {
        Mockito.when(request.getCookies()).thenReturn(new Cookie[]{ new Cookie("OAUTHSTATE", DigestUtils.sha1Hex("state")) });
        Mockito.when(request.getParameter("state")).thenReturn("other value");
        thrown.expect(AuthenticationExceptionMatcher.authenticationException().from(Source.oauth2(identityProvider)).withoutLogin().andNoPublicMessage());
        thrown.expectMessage("CSRF state value is invalid");
        underTest.verifyState(request, response, identityProvider);
    }

    @Test
    public void fail_with_AuthenticationException_when_state_cookie_is_null() {
        Mockito.when(request.getCookies()).thenReturn(new Cookie[]{ new Cookie("OAUTHSTATE", null) });
        Mockito.when(request.getParameter("state")).thenReturn("state");
        thrown.expect(AuthenticationExceptionMatcher.authenticationException().from(Source.oauth2(identityProvider)).withoutLogin().andNoPublicMessage());
        thrown.expectMessage("CSRF state value is invalid");
        underTest.verifyState(request, response, identityProvider);
    }

    @Test
    public void fail_with_AuthenticationException_when_state_parameter_is_empty() {
        Mockito.when(request.getCookies()).thenReturn(new Cookie[]{ new Cookie("OAUTHSTATE", DigestUtils.sha1Hex("state")) });
        Mockito.when(request.getParameter("state")).thenReturn("");
        thrown.expect(AuthenticationExceptionMatcher.authenticationException().from(Source.oauth2(identityProvider)).withoutLogin().andNoPublicMessage());
        thrown.expectMessage("CSRF state value is invalid");
        underTest.verifyState(request, response, identityProvider);
    }

    @Test
    public void fail_with_AuthenticationException_when_cookie_is_missing() {
        Mockito.when(request.getCookies()).thenReturn(new Cookie[]{  });
        thrown.expect(AuthenticationExceptionMatcher.authenticationException().from(Source.oauth2(identityProvider)).withoutLogin().andNoPublicMessage());
        thrown.expectMessage("Cookie 'OAUTHSTATE' is missing");
        underTest.verifyState(request, response, identityProvider);
    }
}

