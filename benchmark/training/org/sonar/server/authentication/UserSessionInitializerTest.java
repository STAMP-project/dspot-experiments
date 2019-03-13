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


import Method.BASIC;
import System2.INSTANCE;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.db.DbTester;
import org.sonar.server.authentication.event.AuthenticationEvent;
import org.sonar.server.authentication.event.AuthenticationEvent.Source;
import org.sonar.server.authentication.event.AuthenticationException;
import org.sonar.server.tester.AnonymousMockUserSession;
import org.sonar.server.user.ThreadLocalUserSession;


public class UserSessionInitializerTest {
    @Rule
    public DbTester dbTester = DbTester.create(INSTANCE);

    private ThreadLocalUserSession threadLocalSession = Mockito.mock(ThreadLocalUserSession.class);

    private HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

    private HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

    private RequestAuthenticator authenticator = Mockito.mock(RequestAuthenticator.class);

    private AuthenticationEvent authenticationEvent = Mockito.mock(AuthenticationEvent.class);

    private MapSettings settings = new MapSettings();

    private UserSessionInitializer underTest = new UserSessionInitializer(settings.asConfig(), threadLocalSession, authenticationEvent, authenticator);

    @Test
    public void check_urls() {
        assertPathIsNotIgnored("/");
        assertPathIsNotIgnored("/foo");
        assertPathIsNotIgnored("/api/server_id/show");
        assertPathIsIgnored("/api/authentication/login");
        assertPathIsIgnored("/api/authentication/logout");
        assertPathIsIgnored("/api/authentication/validate");
        assertPathIsIgnored("/batch/index");
        assertPathIsIgnored("/batch/file");
        assertPathIsIgnored("/maintenance/index");
        assertPathIsIgnored("/setup/index");
        assertPathIsIgnored("/sessions/new");
        assertPathIsIgnored("/sessions/logout");
        assertPathIsIgnored("/sessions/unauthorized");
        assertPathIsIgnored("/oauth2/callback/github");
        assertPathIsIgnored("/oauth2/callback/foo");
        assertPathIsIgnored("/api/system/db_migration_status");
        assertPathIsIgnored("/api/system/status");
        assertPathIsIgnored("/api/system/migrate_db");
        assertPathIsIgnored("/api/server/version");
        assertPathIsIgnored("/api/users/identity_providers");
        assertPathIsIgnored("/api/l10n/index");
        // exlude passcode urls
        assertPathIsIgnoredWithAnonymousAccess("/api/ce/info");
        assertPathIsIgnoredWithAnonymousAccess("/api/ce/pause");
        assertPathIsIgnoredWithAnonymousAccess("/api/ce/resume");
        assertPathIsIgnoredWithAnonymousAccess("/api/system/health");
        // exclude static resources
        assertPathIsIgnored("/css/style.css");
        assertPathIsIgnored("/images/logo.png");
        assertPathIsIgnored("/js/jquery.js");
    }

    @Test
    public void return_code_401_when_not_authenticated_and_with_force_authentication() {
        ArgumentCaptor<AuthenticationException> exceptionArgumentCaptor = ArgumentCaptor.forClass(AuthenticationException.class);
        Mockito.when(threadLocalSession.isLoggedIn()).thenReturn(false);
        Mockito.when(authenticator.authenticate(request, response)).thenReturn(new AnonymousMockUserSession());
        settings.setProperty("sonar.forceAuthentication", true);
        assertThat(underTest.initUserSession(request, response)).isTrue();
        Mockito.verifyZeroInteractions(response);
        Mockito.verify(authenticationEvent).loginFailure(ArgumentMatchers.eq(request), exceptionArgumentCaptor.capture());
        Mockito.verifyZeroInteractions(threadLocalSession);
        AuthenticationException authenticationException = exceptionArgumentCaptor.getValue();
        assertThat(authenticationException.getSource()).isEqualTo(Source.local(BASIC));
        assertThat(authenticationException.getLogin()).isNull();
        assertThat(authenticationException.getMessage()).isEqualTo("User must be authenticated");
        assertThat(authenticationException.getPublicMessage()).isNull();
    }

    @Test
    public void return_401_and_stop_on_ws() {
        Mockito.when(request.getRequestURI()).thenReturn("/api/issues");
        AuthenticationException authenticationException = AuthenticationException.newBuilder().setSource(Source.jwt()).setMessage("Token id hasn't been found").build();
        Mockito.doThrow(authenticationException).when(authenticator).authenticate(request, response);
        assertThat(underTest.initUserSession(request, response)).isFalse();
        Mockito.verify(response).setStatus(401);
        Mockito.verify(authenticationEvent).loginFailure(request, authenticationException);
        Mockito.verifyZeroInteractions(threadLocalSession);
    }

    @Test
    public void return_401_and_stop_on_batch_ws() {
        Mockito.when(request.getRequestURI()).thenReturn("/batch/global");
        Mockito.doThrow(AuthenticationException.newBuilder().setSource(Source.jwt()).setMessage("Token id hasn't been found").build()).when(authenticator).authenticate(request, response);
        assertThat(underTest.initUserSession(request, response)).isFalse();
        Mockito.verify(response).setStatus(401);
        Mockito.verifyZeroInteractions(threadLocalSession);
    }

    @Test
    public void return_to_session_unauthorized_when_error_on_from_external_provider() throws Exception {
        Mockito.doThrow(AuthenticationException.newBuilder().setSource(Source.external(UserSessionInitializerTest.newBasicIdentityProvider("failing"))).setPublicMessage("Token id hasn't been found").build()).when(authenticator).authenticate(request, response);
        assertThat(underTest.initUserSession(request, response)).isFalse();
        Mockito.verify(response).sendRedirect("/sessions/unauthorized?message=Token+id+hasn%27t+been+found");
    }

    @Test
    public void return_to_session_unauthorized_when_error_on_from_external_provider_with_context_path() throws Exception {
        Mockito.when(request.getContextPath()).thenReturn("/sonarqube");
        Mockito.doThrow(AuthenticationException.newBuilder().setSource(Source.external(UserSessionInitializerTest.newBasicIdentityProvider("failing"))).setPublicMessage("Token id hasn't been found").build()).when(authenticator).authenticate(request, response);
        assertThat(underTest.initUserSession(request, response)).isFalse();
        Mockito.verify(response).sendRedirect("/sonarqube/sessions/unauthorized?message=Token+id+hasn%27t+been+found");
    }
}

