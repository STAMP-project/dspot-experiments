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


import BaseIdentityProvider.Context;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.api.platform.Server;
import org.sonar.api.server.authentication.BaseIdentityProvider;
import org.sonar.api.server.authentication.UserIdentity;
import org.sonar.db.user.UserDto;
import org.sonar.server.user.TestUserSessionFactory;
import org.sonar.server.user.ThreadLocalUserSession;
import org.sonar.server.user.UserSession;


public class BaseContextFactoryTest {
    private static final String PUBLIC_ROOT_URL = "https://mydomain.com";

    private static final UserIdentity USER_IDENTITY = UserIdentity.builder().setProviderId("ABCD").setProviderLogin("johndoo").setLogin("id:johndoo").setName("John").setEmail("john@email.com").build();

    private ThreadLocalUserSession threadLocalUserSession = Mockito.mock(ThreadLocalUserSession.class);

    private TestUserRegistrar userIdentityAuthenticator = new TestUserRegistrar();

    private Server server = Mockito.mock(Server.class);

    private HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

    private HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

    private BaseIdentityProvider identityProvider = Mockito.mock(BaseIdentityProvider.class);

    private JwtHttpHandler jwtHttpHandler = Mockito.mock(JwtHttpHandler.class);

    private TestUserSessionFactory userSessionFactory = TestUserSessionFactory.standalone();

    private BaseContextFactory underTest = new BaseContextFactory(userIdentityAuthenticator, server, jwtHttpHandler, threadLocalUserSession, userSessionFactory);

    @Test
    public void create_context() {
        BaseIdentityProvider.Context context = underTest.newContext(request, response, identityProvider);
        assertThat(context.getRequest()).isEqualTo(request);
        assertThat(context.getResponse()).isEqualTo(response);
        assertThat(context.getServerBaseURL()).isEqualTo(BaseContextFactoryTest.PUBLIC_ROOT_URL);
    }

    @Test
    public void authenticate() {
        BaseIdentityProvider.Context context = underTest.newContext(request, response, identityProvider);
        ArgumentCaptor<UserDto> userArgumentCaptor = ArgumentCaptor.forClass(UserDto.class);
        context.authenticate(BaseContextFactoryTest.USER_IDENTITY);
        assertThat(userIdentityAuthenticator.isAuthenticated()).isTrue();
        Mockito.verify(threadLocalUserSession).set(ArgumentMatchers.any(UserSession.class));
        Mockito.verify(jwtHttpHandler).generateToken(userArgumentCaptor.capture(), ArgumentMatchers.eq(request), ArgumentMatchers.eq(response));
        assertThat(userArgumentCaptor.getValue().getLogin()).isEqualTo(BaseContextFactoryTest.USER_IDENTITY.getLogin());
        assertThat(userArgumentCaptor.getValue().getExternalId()).isEqualTo(BaseContextFactoryTest.USER_IDENTITY.getProviderId());
        assertThat(userArgumentCaptor.getValue().getExternalLogin()).isEqualTo(BaseContextFactoryTest.USER_IDENTITY.getProviderLogin());
        assertThat(userArgumentCaptor.getValue().getExternalIdentityProvider()).isEqualTo("github");
    }
}

