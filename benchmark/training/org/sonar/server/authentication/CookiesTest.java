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


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;


public class CookiesTest {
    private static final String HTTPS_HEADER = "X-Forwarded-Proto";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

    @Test
    public void create_cookie() {
        Cookie cookie = Cookies.newCookieBuilder(request).setName("name").setValue("value").setHttpOnly(true).setExpiry(10).build();
        assertThat(cookie.getName()).isEqualTo("name");
        assertThat(cookie.getValue()).isEqualTo("value");
        assertThat(cookie.isHttpOnly()).isTrue();
        assertThat(cookie.getMaxAge()).isEqualTo(10);
        assertThat(cookie.getSecure()).isFalse();
        assertThat(cookie.getPath()).isEqualTo("/");
    }

    @Test
    public void create_cookie_without_value() {
        Cookie cookie = Cookies.newCookieBuilder(request).setName("name").build();
        assertThat(cookie.getName()).isEqualTo("name");
        assertThat(cookie.getValue()).isNull();
    }

    @Test
    public void create_cookie_when_web_context() {
        Mockito.when(request.getContextPath()).thenReturn("/sonarqube");
        Cookie cookie = Cookies.newCookieBuilder(request).setName("name").setValue("value").setHttpOnly(true).setExpiry(10).build();
        assertThat(cookie.getName()).isEqualTo("name");
        assertThat(cookie.getValue()).isEqualTo("value");
        assertThat(cookie.isHttpOnly()).isTrue();
        assertThat(cookie.getMaxAge()).isEqualTo(10);
        assertThat(cookie.getSecure()).isFalse();
        assertThat(cookie.getPath()).isEqualTo("/sonarqube");
    }

    @Test
    public void create_not_secured_cookie_when_header_is_not_http() {
        Mockito.when(request.getHeader(CookiesTest.HTTPS_HEADER)).thenReturn("http");
        Cookie cookie = Cookies.newCookieBuilder(request).setName("name").setValue("value").setHttpOnly(true).setExpiry(10).build();
        assertThat(cookie.getSecure()).isFalse();
    }

    @Test
    public void create_secured_cookie_when_X_Forwarded_Proto_header_is_https() {
        Mockito.when(request.getHeader(CookiesTest.HTTPS_HEADER)).thenReturn("https");
        Cookie cookie = Cookies.newCookieBuilder(request).setName("name").setValue("value").setHttpOnly(true).setExpiry(10).build();
        assertThat(cookie.getSecure()).isTrue();
    }

    @Test
    public void create_secured_cookie_when_X_Forwarded_Proto_header_is_HTTPS() {
        Mockito.when(request.getHeader(CookiesTest.HTTPS_HEADER)).thenReturn("HTTPS");
        Cookie cookie = Cookies.newCookieBuilder(request).setName("name").setValue("value").setHttpOnly(true).setExpiry(10).build();
        assertThat(cookie.getSecure()).isTrue();
    }

    @Test
    public void find_cookie() {
        Cookie cookie = Cookies.newCookieBuilder(request).setName("name").setValue("value").build();
        Mockito.when(request.getCookies()).thenReturn(new Cookie[]{ cookie });
        assertThat(Cookies.findCookie("name", request)).isPresent();
        assertThat(Cookies.findCookie("NAME", request)).isEmpty();
        assertThat(Cookies.findCookie("unknown", request)).isEmpty();
    }

    @Test
    public void does_not_fail_to_find_cookie_when_no_cookie() {
        assertThat(Cookies.findCookie("unknown", request)).isEmpty();
    }

    @Test
    public void fail_with_NPE_when_cookie_name_is_null() {
        expectedException.expect(NullPointerException.class);
        Cookies.newCookieBuilder(request).setName(null);
    }

    @Test
    public void fail_with_NPE_when_cookie_has_no_name() {
        expectedException.expect(NullPointerException.class);
        Cookies.newCookieBuilder(request).setName(null);
    }
}

