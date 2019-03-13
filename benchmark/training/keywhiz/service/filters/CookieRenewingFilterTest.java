/**
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package keywhiz.service.filters;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.net.HttpHeaders;
import java.util.Optional;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.NewCookie;
import keywhiz.auth.User;
import keywhiz.auth.cookie.CookieAuthenticator;
import keywhiz.service.resources.admin.SessionLoginResource;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class CookieRenewingFilterTest {
    private static final String SESSION_COOKIE = "session";

    @Rule
    public MockitoRule mockito = MockitoJUnit.rule();

    @Mock
    CookieAuthenticator authenticator;

    @Mock
    SessionLoginResource sessionLoginResource;

    @Mock
    ContainerRequestContext request;

    @Mock
    ContainerResponseContext response;

    private CookieRenewingFilter filter;

    private final Cookie cookie = new Cookie(CookieRenewingFilterTest.SESSION_COOKIE, "some cookie");

    @Test
    public void setsAllNewCookieWithValidCookie() throws Exception {
        User user = User.named("username");
        Mockito.when(request.getCookies()).thenReturn(ImmutableMap.of(CookieRenewingFilterTest.SESSION_COOKIE, cookie));
        Mockito.when(authenticator.authenticate(cookie)).thenReturn(Optional.of(user));
        NewCookie newCookie1 = new NewCookie(CookieRenewingFilterTest.SESSION_COOKIE, "new session");
        NewCookie newCookie2 = new NewCookie("XSRF", "new xsrf");
        Mockito.when(sessionLoginResource.cookiesForUser(user)).thenReturn(ImmutableList.of(newCookie1, newCookie2));
        filter.filter(request, response);
        assertThat(CookieRenewingFilterTest.getCookieMap(response)).contains(entry(newCookie1.getName(), newCookie1.getValue()), entry(newCookie2.getName(), newCookie2.getValue()));
    }

    @Test
    public void doesNothingWhenCookieInvalid() throws Exception {
        Mockito.when(request.getCookies()).thenReturn(ImmutableMap.of(CookieRenewingFilterTest.SESSION_COOKIE, cookie));
        Mockito.when(authenticator.authenticate(cookie)).thenReturn(Optional.empty());
        filter.filter(request, response);
        assertThat(response.getHeaders()).doesNotContainKey(HttpHeaders.SET_COOKIE);
    }

    @Test
    public void doesNothingWhenRequestHasNoCookies() throws Exception {
        Mockito.when(request.getCookies()).thenReturn(ImmutableMap.of());
        filter.filter(request, response);
        assertThat(response.getHeaders()).doesNotContainKey(HttpHeaders.SET_COOKIE);
    }

    @Test
    public void doesNothingWhenResponseSetsSessionCookie() throws Exception {
        ImmutableMap<String, NewCookie> immutableResponseCookies = ImmutableMap.of(CookieRenewingFilterTest.SESSION_COOKIE, NewCookie.valueOf(cookie.getValue()));
        Mockito.when(response.getCookies()).thenReturn(immutableResponseCookies);
        filter.filter(request, response);
        assertThat(response.getCookies()).isSameAs(immutableResponseCookies);
        assertThat(response.getHeaders()).doesNotContainKey(HttpHeaders.SET_COOKIE);
    }
}

