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
package keywhiz.service.resources.admin;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.auth.basic.BasicCredentials;
import java.util.Map;
import java.util.Optional;
import javax.inject.Inject;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import keywhiz.KeywhizTestRunner;
import keywhiz.api.LoginRequest;
import keywhiz.auth.User;
import keywhiz.auth.cookie.CookieAuthenticator;
import keywhiz.auth.cookie.CookieConfig;
import keywhiz.auth.cookie.GCMEncryptor;
import keywhiz.auth.cookie.SessionCookie;
import keywhiz.auth.ldap.LdapAuthenticator;
import keywhiz.auth.xsrf.Xsrf;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;


@RunWith(KeywhizTestRunner.class)
public class SessionLoginResourceTest {
    @Mock
    LdapAuthenticator ldapAuthenticator;

    @Inject
    ObjectMapper mapper;

    @Inject
    keywhiz.auth.cookie.GCMEncryptor GCMEncryptor;

    @Inject
    @SessionCookie
    CookieConfig sessionCookieConfig;

    @Inject
    @Xsrf
    CookieConfig xsrfCookieConfig;

    SessionLoginResource sessionLoginResource;

    CookieAuthenticator cookieAuthenticator;

    BasicCredentials goodCredentials = new BasicCredentials("good", "credentials");

    BasicCredentials badCredentials = new BasicCredentials("bad", "credentials");

    @Test(expected = NotAuthorizedException.class)
    public void badCredentialsThrowUnauthorized() throws Exception {
        Mockito.when(ldapAuthenticator.authenticate(badCredentials)).thenReturn(Optional.empty());
        sessionLoginResource.login(LoginRequest.from("bad", "credentials".toCharArray()));
    }

    @Test
    public void goodCredentialsSetsCookie() throws Exception {
        User user = User.named("goodUser");
        Mockito.when(ldapAuthenticator.authenticate(goodCredentials)).thenReturn(Optional.of(user));
        Response response = sessionLoginResource.login(LoginRequest.from("good", "credentials".toCharArray()));
        assertThat(response.getStatus()).isEqualTo(200);
        Map<String, NewCookie> responseCookies = response.getCookies();
        assertThat(responseCookies).hasSize(2).containsOnlyKeys("session", "XSRF-TOKEN");
        User authUser = cookieAuthenticator.authenticate(responseCookies.get("session")).orElseThrow(RuntimeException::new);
        assertThat(authUser).isEqualTo(user);
    }

    @Test(expected = NullPointerException.class)
    public void missingUsernameThrowsException() throws Exception {
        sessionLoginResource.login(LoginRequest.from(null, "password".toCharArray()));
    }

    @Test(expected = NullPointerException.class)
    public void missingPasswordThrowsException() throws Exception {
        sessionLoginResource.login(LoginRequest.from("username", null));
    }
}

