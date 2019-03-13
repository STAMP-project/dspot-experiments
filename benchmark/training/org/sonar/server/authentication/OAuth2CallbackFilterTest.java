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


import LoggerLevel.WARN;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.api.platform.Server;
import org.sonar.api.server.authentication.OAuth2IdentityProvider;
import org.sonar.api.server.authentication.UnauthorizedException;
import org.sonar.api.server.authentication.UserIdentity;
import org.sonar.api.utils.log.LogTester;
import org.sonar.db.user.UserDto;
import org.sonar.db.user.UserTesting;
import org.sonar.server.authentication.event.AuthenticationEvent;
import org.sonar.server.authentication.event.AuthenticationException;
import org.sonar.server.user.ThreadLocalUserSession;


public class OAuth2CallbackFilterTest {
    private static final String OAUTH2_PROVIDER_KEY = "github";

    private static final String LOGIN = "foo";

    @Rule
    public LogTester logTester = new LogTester();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public IdentityProviderRepositoryRule identityProviderRepository = new IdentityProviderRepositoryRule();

    private OAuth2ContextFactory oAuth2ContextFactory = Mockito.mock(OAuth2ContextFactory.class);

    private HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

    private HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

    private Server server = Mockito.mock(Server.class);

    private FilterChain chain = Mockito.mock(FilterChain.class);

    private FakeOAuth2IdentityProvider oAuth2IdentityProvider = new OAuth2CallbackFilterTest.WellbehaveFakeOAuth2IdentityProvider(OAuth2CallbackFilterTest.OAUTH2_PROVIDER_KEY, true, OAuth2CallbackFilterTest.LOGIN);

    private AuthenticationEvent authenticationEvent = Mockito.mock(AuthenticationEvent.class);

    private OAuth2AuthenticationParameters oAuthRedirection = Mockito.mock(OAuth2AuthenticationParameters.class);

    private ThreadLocalUserSession threadLocalUserSession = Mockito.mock(ThreadLocalUserSession.class);

    private ArgumentCaptor<AuthenticationException> authenticationExceptionCaptor = ArgumentCaptor.forClass(AuthenticationException.class);

    private OAuth2CallbackFilter underTest = new OAuth2CallbackFilter(identityProviderRepository, oAuth2ContextFactory, server, authenticationEvent, oAuthRedirection, threadLocalUserSession);

    @Test
    public void do_get_pattern() {
        assertThat(underTest.doGetPattern()).isNotNull();
    }

    @Test
    public void do_filter_with_context() {
        Mockito.when(server.getContextPath()).thenReturn("/sonarqube");
        Mockito.when(request.getRequestURI()).thenReturn(("/sonarqube/oauth2/callback/" + (OAuth2CallbackFilterTest.OAUTH2_PROVIDER_KEY)));
        identityProviderRepository.addIdentityProvider(oAuth2IdentityProvider);
        Mockito.when(threadLocalUserSession.hasSession()).thenReturn(true);
        Mockito.when(threadLocalUserSession.getLogin()).thenReturn(OAuth2CallbackFilterTest.LOGIN);
        underTest.doFilter(request, response, chain);
        assertCallbackCalled(oAuth2IdentityProvider);
        Mockito.verify(authenticationEvent).loginSuccess(request, OAuth2CallbackFilterTest.LOGIN, AuthenticationEvent.Source.oauth2(oAuth2IdentityProvider));
    }

    @Test
    public void do_filter_with_context_no_log_if_provider_did_not_call_authenticate_on_context() {
        Mockito.when(server.getContextPath()).thenReturn("/sonarqube");
        Mockito.when(request.getRequestURI()).thenReturn(("/sonarqube/oauth2/callback/" + (OAuth2CallbackFilterTest.OAUTH2_PROVIDER_KEY)));
        FakeOAuth2IdentityProvider identityProvider = new FakeOAuth2IdentityProvider(OAuth2CallbackFilterTest.OAUTH2_PROVIDER_KEY, true);
        identityProviderRepository.addIdentityProvider(identityProvider);
        underTest.doFilter(request, response, chain);
        assertCallbackCalled(identityProvider);
        Mockito.verify(authenticationEvent).loginFailure(ArgumentMatchers.eq(request), authenticationExceptionCaptor.capture());
        AuthenticationException authenticationException = authenticationExceptionCaptor.getValue();
        assertThat(authenticationException).hasMessage("Plugin did not call authenticate");
        assertThat(authenticationException.getSource()).isEqualTo(AuthenticationEvent.Source.oauth2(identityProvider));
        assertThat(authenticationException.getLogin()).isNull();
        assertThat(authenticationException.getPublicMessage()).isNull();
    }

    @Test
    public void do_filter_on_auth2_identity_provider() {
        Mockito.when(request.getRequestURI()).thenReturn(("/oauth2/callback/" + (OAuth2CallbackFilterTest.OAUTH2_PROVIDER_KEY)));
        identityProviderRepository.addIdentityProvider(oAuth2IdentityProvider);
        Mockito.when(threadLocalUserSession.hasSession()).thenReturn(true);
        Mockito.when(threadLocalUserSession.getLogin()).thenReturn(OAuth2CallbackFilterTest.LOGIN);
        underTest.doFilter(request, response, chain);
        assertCallbackCalled(oAuth2IdentityProvider);
        Mockito.verify(authenticationEvent).loginSuccess(request, OAuth2CallbackFilterTest.LOGIN, AuthenticationEvent.Source.oauth2(oAuth2IdentityProvider));
    }

    @Test
    public void fail_on_not_oauth2_provider() throws Exception {
        String providerKey = "openid";
        Mockito.when(request.getRequestURI()).thenReturn(("/oauth2/callback/" + providerKey));
        identityProviderRepository.addIdentityProvider(new FakeBasicIdentityProvider(providerKey, true));
        underTest.doFilter(request, response, chain);
        assertError("Not an OAuth2IdentityProvider: class org.sonar.server.authentication.FakeBasicIdentityProvider");
        Mockito.verifyZeroInteractions(authenticationEvent);
    }

    @Test
    public void fail_on_disabled_provider() throws Exception {
        Mockito.when(request.getRequestURI()).thenReturn(("/oauth2/callback/" + (OAuth2CallbackFilterTest.OAUTH2_PROVIDER_KEY)));
        identityProviderRepository.addIdentityProvider(new FakeOAuth2IdentityProvider(OAuth2CallbackFilterTest.OAUTH2_PROVIDER_KEY, false));
        underTest.doFilter(request, response, chain);
        assertError("Failed to retrieve IdentityProvider for key 'github'");
        Mockito.verifyZeroInteractions(authenticationEvent);
    }

    @Test
    public void redirect_when_failing_because_of_UnauthorizedExceptionException() throws Exception {
        OAuth2CallbackFilterTest.FailWithUnauthorizedExceptionIdProvider identityProvider = new OAuth2CallbackFilterTest.FailWithUnauthorizedExceptionIdProvider();
        Mockito.when(request.getRequestURI()).thenReturn(("/oauth2/callback/" + (identityProvider.getKey())));
        identityProviderRepository.addIdentityProvider(identityProvider);
        underTest.doFilter(request, response, chain);
        Mockito.verify(response).sendRedirect("/sessions/unauthorized?message=Email+john%40email.com+is+already+used");
        Mockito.verify(authenticationEvent).loginFailure(ArgumentMatchers.eq(request), authenticationExceptionCaptor.capture());
        AuthenticationException authenticationException = authenticationExceptionCaptor.getValue();
        assertThat(authenticationException).hasMessage("Email john@email.com is already used");
        assertThat(authenticationException.getSource()).isEqualTo(AuthenticationEvent.Source.oauth2(identityProvider));
        assertThat(authenticationException.getLogin()).isNull();
        assertThat(authenticationException.getPublicMessage()).isEqualTo("Email john@email.com is already used");
        Mockito.verify(oAuthRedirection).delete(ArgumentMatchers.eq(request), ArgumentMatchers.eq(response));
    }

    @Test
    public void redirect_with_context_path_when_failing_because_of_UnauthorizedExceptionException() throws Exception {
        Mockito.when(server.getContextPath()).thenReturn("/sonarqube");
        OAuth2CallbackFilterTest.FailWithUnauthorizedExceptionIdProvider identityProvider = new OAuth2CallbackFilterTest.FailWithUnauthorizedExceptionIdProvider();
        Mockito.when(request.getRequestURI()).thenReturn(("/sonarqube/oauth2/callback/" + (identityProvider.getKey())));
        identityProviderRepository.addIdentityProvider(identityProvider);
        underTest.doFilter(request, response, chain);
        Mockito.verify(response).sendRedirect("/sonarqube/sessions/unauthorized?message=Email+john%40email.com+is+already+used");
        Mockito.verify(oAuthRedirection).delete(ArgumentMatchers.eq(request), ArgumentMatchers.eq(response));
    }

    @Test
    public void redirect_when_failing_because_of_Exception() throws Exception {
        OAuth2CallbackFilterTest.FailWithIllegalStateException identityProvider = new OAuth2CallbackFilterTest.FailWithIllegalStateException();
        Mockito.when(request.getRequestURI()).thenReturn(("/oauth2/callback/" + (identityProvider.getKey())));
        identityProviderRepository.addIdentityProvider(identityProvider);
        underTest.doFilter(request, response, chain);
        Mockito.verify(response).sendRedirect("/sessions/unauthorized");
        assertThat(logTester.logs(WARN)).containsExactlyInAnyOrder("Fail to callback authentication with 'failing'");
        Mockito.verify(oAuthRedirection).delete(ArgumentMatchers.eq(request), ArgumentMatchers.eq(response));
    }

    @Test
    public void redirect_when_failing_because_of_EmailAlreadyExistException() throws Exception {
        UserDto existingUser = UserTesting.newUserDto().setEmail("john@email.com").setExternalLogin("john.bitbucket").setExternalIdentityProvider("bitbucket");
        OAuth2CallbackFilterTest.FailWithEmailAlreadyExistException identityProvider = new OAuth2CallbackFilterTest.FailWithEmailAlreadyExistException(existingUser);
        Mockito.when(request.getRequestURI()).thenReturn(("/oauth2/callback/" + (identityProvider.getKey())));
        identityProviderRepository.addIdentityProvider(identityProvider);
        underTest.doFilter(request, response, chain);
        Mockito.verify(response).sendRedirect("/sessions/email_already_exists?email=john%40email.com&login=john.github&provider=failing&existingLogin=john.bitbucket&existingProvider=bitbucket");
        Mockito.verify(oAuthRedirection).delete(ArgumentMatchers.eq(request), ArgumentMatchers.eq(response));
    }

    @Test
    public void fail_when_no_oauth2_provider_provided() throws Exception {
        Mockito.when(request.getRequestURI()).thenReturn("/oauth2/callback");
        underTest.doFilter(request, response, chain);
        assertError("No provider key found in URI");
        Mockito.verifyZeroInteractions(authenticationEvent);
    }

    private static class FailWithUnauthorizedExceptionIdProvider extends OAuth2CallbackFilterTest.FailingIdentityProvider {
        @Override
        public void callback(CallbackContext context) {
            throw new UnauthorizedException("Email john@email.com is already used");
        }
    }

    private static class FailWithIllegalStateException extends OAuth2CallbackFilterTest.FailingIdentityProvider {
        @Override
        public void callback(CallbackContext context) {
            throw new IllegalStateException("Failure !");
        }
    }

    private static class FailWithEmailAlreadyExistException extends OAuth2CallbackFilterTest.FailingIdentityProvider {
        private final UserDto existingUser;

        public FailWithEmailAlreadyExistException(UserDto existingUser) {
            this.existingUser = existingUser;
        }

        @Override
        public void callback(CallbackContext context) {
            throw new org.sonar.server.authentication.exception.EmailAlreadyExistsRedirectionException(existingUser.getEmail(), existingUser, UserIdentity.builder().setProviderLogin("john.github").setLogin("john.github").setName(existingUser.getName()).setEmail(existingUser.getEmail()).build(), this);
        }
    }

    private abstract static class FailingIdentityProvider extends TestIdentityProvider implements OAuth2IdentityProvider {
        FailingIdentityProvider() {
            this.setKey("failing");
            this.setName("Failing");
            this.setEnabled(true);
        }

        @Override
        public void init(InitContext context) {
            // Nothing to do
        }
    }

    /**
     * An extension of {@link FakeOAuth2IdentityProvider} that actually call {@link org.sonar.api.server.authentication.OAuth2IdentityProvider.CallbackContext#authenticate(UserIdentity)}.
     */
    private static class WellbehaveFakeOAuth2IdentityProvider extends FakeOAuth2IdentityProvider {
        private final String login;

        public WellbehaveFakeOAuth2IdentityProvider(String key, boolean enabled, String login) {
            super(key, enabled);
            this.login = login;
        }

        @Override
        public void callback(CallbackContext context) {
            super.callback(context);
            context.authenticate(UserIdentity.builder().setLogin(login).setProviderLogin(login).setEmail(((login) + "@toto.com")).setName(("name of " + (login))).build());
        }
    }
}

