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


import ExistingEmailStrategy.ALLOW;
import ExistingEmailStrategy.WARN;
import OAuth2IdentityProvider.CallbackContext;
import OAuth2IdentityProvider.InitContext;
import com.google.common.collect.ImmutableSet;
import java.util.Optional;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.api.platform.Server;
import org.sonar.api.server.authentication.OAuth2IdentityProvider;
import org.sonar.api.server.authentication.UserIdentity;
import org.sonar.db.user.UserDto;
import org.sonar.server.authentication.OAuth2ContextFactory.OAuthContextImpl;
import org.sonar.server.user.TestUserSessionFactory;
import org.sonar.server.user.ThreadLocalUserSession;
import org.sonar.server.user.UserSession;


public class OAuth2ContextFactoryTest {
    private static final String PROVIDER_KEY = "github";

    private static final String SECURED_PUBLIC_ROOT_URL = "https://mydomain.com";

    private static final String PROVIDER_NAME = "provider name";

    private static final UserIdentity USER_IDENTITY = UserIdentity.builder().setProviderId("ABCD").setProviderLogin("johndoo").setLogin("id:johndoo").setName("John").setEmail("john@email.com").build();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private ThreadLocalUserSession threadLocalUserSession = Mockito.mock(ThreadLocalUserSession.class);

    private TestUserRegistrar userIdentityAuthenticator = new TestUserRegistrar();

    private Server server = Mockito.mock(Server.class);

    private OAuthCsrfVerifier csrfVerifier = Mockito.mock(OAuthCsrfVerifier.class);

    private JwtHttpHandler jwtHttpHandler = Mockito.mock(JwtHttpHandler.class);

    private TestUserSessionFactory userSessionFactory = TestUserSessionFactory.standalone();

    private OAuth2AuthenticationParameters oAuthParameters = Mockito.mock(OAuth2AuthenticationParameters.class);

    private HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

    private HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

    private HttpSession session = Mockito.mock(HttpSession.class);

    private OAuth2IdentityProvider identityProvider = Mockito.mock(OAuth2IdentityProvider.class);

    private OAuth2ContextFactory underTest = new OAuth2ContextFactory(threadLocalUserSession, userIdentityAuthenticator, server, csrfVerifier, jwtHttpHandler, userSessionFactory, oAuthParameters);

    @Test
    public void create_context() {
        Mockito.when(server.getPublicRootUrl()).thenReturn(OAuth2ContextFactoryTest.SECURED_PUBLIC_ROOT_URL);
        OAuth2IdentityProvider.InitContext context = newInitContext();
        assertThat(context.getRequest()).isEqualTo(request);
        assertThat(context.getResponse()).isEqualTo(response);
        assertThat(context.getCallbackUrl()).isEqualTo("https://mydomain.com/oauth2/callback/github");
    }

    @Test
    public void generate_csrf_state() {
        OAuth2IdentityProvider.InitContext context = newInitContext();
        context.generateCsrfState();
        Mockito.verify(csrfVerifier).generateState(request, response);
    }

    @Test
    public void redirect_to() throws Exception {
        OAuth2IdentityProvider.InitContext context = newInitContext();
        context.redirectTo("/test");
        Mockito.verify(response).sendRedirect("/test");
    }

    @Test
    public void create_callback() {
        Mockito.when(server.getPublicRootUrl()).thenReturn(OAuth2ContextFactoryTest.SECURED_PUBLIC_ROOT_URL);
        OAuth2IdentityProvider.CallbackContext callback = newCallbackContext();
        assertThat(callback.getRequest()).isEqualTo(request);
        assertThat(callback.getResponse()).isEqualTo(response);
        assertThat(callback.getCallbackUrl()).isEqualTo("https://mydomain.com/oauth2/callback/github");
    }

    @Test
    public void authenticate() {
        OAuth2IdentityProvider.CallbackContext callback = newCallbackContext();
        callback.authenticate(OAuth2ContextFactoryTest.USER_IDENTITY);
        assertThat(userIdentityAuthenticator.isAuthenticated()).isTrue();
        Mockito.verify(threadLocalUserSession).set(ArgumentMatchers.any(UserSession.class));
        ArgumentCaptor<UserDto> userArgumentCaptor = ArgumentCaptor.forClass(UserDto.class);
        Mockito.verify(jwtHttpHandler).generateToken(userArgumentCaptor.capture(), ArgumentMatchers.eq(request), ArgumentMatchers.eq(response));
        assertThat(userArgumentCaptor.getValue().getLogin()).isEqualTo(OAuth2ContextFactoryTest.USER_IDENTITY.getLogin());
        assertThat(userArgumentCaptor.getValue().getExternalId()).isEqualTo(OAuth2ContextFactoryTest.USER_IDENTITY.getProviderId());
        assertThat(userArgumentCaptor.getValue().getExternalLogin()).isEqualTo(OAuth2ContextFactoryTest.USER_IDENTITY.getProviderLogin());
        assertThat(userArgumentCaptor.getValue().getExternalIdentityProvider()).isEqualTo(OAuth2ContextFactoryTest.PROVIDER_KEY);
    }

    @Test
    public void authenticate_with_allow_email_shift() {
        Mockito.when(oAuthParameters.getAllowEmailShift(request)).thenReturn(Optional.of(true));
        OAuth2IdentityProvider.CallbackContext callback = newCallbackContext();
        callback.authenticate(OAuth2ContextFactoryTest.USER_IDENTITY);
        assertThat(userIdentityAuthenticator.isAuthenticated()).isTrue();
        assertThat(userIdentityAuthenticator.getAuthenticatorParameters().getExistingEmailStrategy()).isEqualTo(ALLOW);
    }

    @Test
    public void authenticate_without_email_shift() {
        Mockito.when(oAuthParameters.getAllowEmailShift(request)).thenReturn(Optional.of(false));
        OAuth2IdentityProvider.CallbackContext callback = newCallbackContext();
        callback.authenticate(OAuth2ContextFactoryTest.USER_IDENTITY);
        assertThat(userIdentityAuthenticator.isAuthenticated()).isTrue();
        assertThat(userIdentityAuthenticator.getAuthenticatorParameters().getExistingEmailStrategy()).isEqualTo(WARN);
    }

    @Test
    public void authenticate_with_allow_login_update() {
        Mockito.when(oAuthParameters.getAllowUpdateLogin(request)).thenReturn(Optional.of(true));
        OAuth2IdentityProvider.CallbackContext callback = newCallbackContext();
        callback.authenticate(OAuth2ContextFactoryTest.USER_IDENTITY);
        assertThat(userIdentityAuthenticator.isAuthenticated()).isTrue();
        assertThat(userIdentityAuthenticator.getAuthenticatorParameters().getUpdateLoginStrategy()).isEqualTo(UpdateLoginStrategy.ALLOW);
    }

    @Test
    public void authenticate_without_allowing_login_update() {
        Mockito.when(oAuthParameters.getAllowUpdateLogin(request)).thenReturn(Optional.of(false));
        OAuth2IdentityProvider.CallbackContext callback = newCallbackContext();
        callback.authenticate(OAuth2ContextFactoryTest.USER_IDENTITY);
        assertThat(userIdentityAuthenticator.isAuthenticated()).isTrue();
        assertThat(userIdentityAuthenticator.getAuthenticatorParameters().getUpdateLoginStrategy()).isEqualTo(UpdateLoginStrategy.WARN);
    }

    @Test
    public void authenticate_with_organization_alm_ids() {
        OAuthContextImpl callback = ((OAuthContextImpl) (newCallbackContext()));
        Set<String> organizationAlmIds = ImmutableSet.of("ABCD", "EFGH");
        callback.authenticate(OAuth2ContextFactoryTest.USER_IDENTITY, organizationAlmIds);
        assertThat(userIdentityAuthenticator.getAuthenticatorParameters().getOrganizationAlmIds()).containsAll(organizationAlmIds);
    }

    @Test
    public void redirect_to_home() throws Exception {
        Mockito.when(server.getContextPath()).thenReturn("");
        Mockito.when(oAuthParameters.getReturnTo(request)).thenReturn(Optional.empty());
        OAuth2IdentityProvider.CallbackContext callback = newCallbackContext();
        callback.redirectToRequestedPage();
        Mockito.verify(response).sendRedirect("/");
    }

    @Test
    public void redirect_to_home_with_context() throws Exception {
        Mockito.when(server.getContextPath()).thenReturn("/sonarqube");
        Mockito.when(oAuthParameters.getReturnTo(request)).thenReturn(Optional.empty());
        OAuth2IdentityProvider.CallbackContext callback = newCallbackContext();
        callback.redirectToRequestedPage();
        Mockito.verify(response).sendRedirect("/sonarqube/");
    }

    @Test
    public void redirect_to_requested_page() throws Exception {
        Mockito.when(oAuthParameters.getReturnTo(request)).thenReturn(Optional.of("/settings"));
        Mockito.when(server.getContextPath()).thenReturn("");
        OAuth2IdentityProvider.CallbackContext callback = newCallbackContext();
        callback.redirectToRequestedPage();
        Mockito.verify(response).sendRedirect("/settings");
    }

    @Test
    public void redirect_to_requested_page_does_not_need_context() throws Exception {
        Mockito.when(oAuthParameters.getReturnTo(request)).thenReturn(Optional.of("/sonarqube/settings"));
        Mockito.when(server.getContextPath()).thenReturn("/other");
        OAuth2IdentityProvider.CallbackContext callback = newCallbackContext();
        callback.redirectToRequestedPage();
        Mockito.verify(response).sendRedirect("/sonarqube/settings");
    }

    @Test
    public void verify_csrf_state() {
        OAuth2IdentityProvider.CallbackContext callback = newCallbackContext();
        callback.verifyCsrfState();
        Mockito.verify(csrfVerifier).verifyState(request, response, identityProvider);
    }

    @Test
    public void delete_oauth2_parameters_during_redirection() {
        Mockito.when(oAuthParameters.getReturnTo(request)).thenReturn(Optional.of("/settings"));
        Mockito.when(server.getContextPath()).thenReturn("");
        OAuth2IdentityProvider.CallbackContext callback = newCallbackContext();
        callback.redirectToRequestedPage();
        Mockito.verify(oAuthParameters).delete(ArgumentMatchers.eq(request), ArgumentMatchers.eq(response));
    }
}

