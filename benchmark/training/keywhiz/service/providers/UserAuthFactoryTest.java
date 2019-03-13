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
package keywhiz.service.providers;


import java.util.Map;
import java.util.Optional;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.Cookie;
import keywhiz.auth.User;
import keywhiz.auth.cookie.CookieAuthenticator;
import org.glassfish.jersey.server.ContainerRequest;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class UserAuthFactoryTest {
    @Rule
    public MockitoRule mockito = MockitoJUnit.rule();

    @Mock
    ContainerRequest request;

    @Mock
    CookieAuthenticator cookieAuthenticator;

    Map<String, Cookie> cookies;

    UserAuthFactory factory;

    @Test(expected = NotAuthorizedException.class)
    public void noSessionCookie() throws Exception {
        cookies.put("not-session", new Cookie("not-session", "value"));
        factory.provide(request);
    }

    @Test(expected = NotAuthorizedException.class)
    public void invalidSessionCookie() throws Exception {
        Cookie badSessionCookie = new Cookie("session", "bad-value");
        cookies.put(badSessionCookie.getName(), badSessionCookie);
        Mockito.when(cookieAuthenticator.authenticate(badSessionCookie)).thenReturn(Optional.empty());
        factory.provide(request);
    }

    @Test
    public void successfulAuth() throws Exception {
        User user = User.named("username");
        Cookie sessionCookie = new Cookie("session", "valid-session");
        cookies.put(sessionCookie.getName(), sessionCookie);
        Mockito.when(cookieAuthenticator.authenticate(sessionCookie)).thenReturn(Optional.of(user));
        assertThat(factory.provide(request)).isEqualTo(user);
    }
}

