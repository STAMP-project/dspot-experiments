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


import Authenticator.Context;
import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.api.security.Authenticator;
import org.sonar.api.security.ExternalGroupsProvider;
import org.sonar.api.security.ExternalUsersProvider;
import org.sonar.api.security.SecurityRealm;
import org.sonar.api.security.UserDetails;
import org.sonar.server.authentication.event.AuthenticationEvent;
import org.sonar.server.authentication.event.AuthenticationEvent.Source;
import org.sonar.server.authentication.event.AuthenticationExceptionMatcher;
import org.sonar.server.user.SecurityRealmFactory;


public class CredentialsExternalAuthenticationTest {
    private static final String LOGIN = "LOGIN";

    private static final String PASSWORD = "PASSWORD";

    private static final String REALM_NAME = "realm name";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private MapSettings settings = new MapSettings();

    private SecurityRealmFactory securityRealmFactory = Mockito.mock(SecurityRealmFactory.class);

    private SecurityRealm realm = Mockito.mock(SecurityRealm.class);

    private Authenticator authenticator = Mockito.mock(Authenticator.class);

    private ExternalUsersProvider externalUsersProvider = Mockito.mock(ExternalUsersProvider.class);

    private ExternalGroupsProvider externalGroupsProvider = Mockito.mock(ExternalGroupsProvider.class);

    private TestUserRegistrar userIdentityAuthenticator = new TestUserRegistrar();

    private AuthenticationEvent authenticationEvent = Mockito.mock(AuthenticationEvent.class);

    private HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

    private CredentialsExternalAuthentication underTest = new CredentialsExternalAuthentication(settings.asConfig(), securityRealmFactory, userIdentityAuthenticator, authenticationEvent);

    @Test
    public void authenticate() {
        executeStartWithoutGroupSync();
        Mockito.when(authenticator.doAuthenticate(ArgumentMatchers.any(Context.class))).thenReturn(true);
        UserDetails userDetails = new UserDetails();
        userDetails.setName("name");
        userDetails.setEmail("email");
        Mockito.when(externalUsersProvider.doGetUserDetails(ArgumentMatchers.any(ExternalUsersProvider.Context.class))).thenReturn(userDetails);
        underTest.authenticate(new Credentials(CredentialsExternalAuthenticationTest.LOGIN, CredentialsExternalAuthenticationTest.PASSWORD), request, Method.BASIC);
        assertThat(userIdentityAuthenticator.isAuthenticated()).isTrue();
        assertThat(userIdentityAuthenticator.getAuthenticatorParameters().getExistingEmailStrategy()).isEqualTo(ExistingEmailStrategy.FORBID);
        assertThat(userIdentityAuthenticator.getAuthenticatorParameters().getUserIdentity().getLogin()).isEqualTo(CredentialsExternalAuthenticationTest.LOGIN);
        assertThat(userIdentityAuthenticator.getAuthenticatorParameters().getUserIdentity().getProviderLogin()).isEqualTo(CredentialsExternalAuthenticationTest.LOGIN);
        assertThat(userIdentityAuthenticator.getAuthenticatorParameters().getUserIdentity().getProviderId()).isNull();
        assertThat(userIdentityAuthenticator.getAuthenticatorParameters().getUserIdentity().getName()).isEqualTo("name");
        assertThat(userIdentityAuthenticator.getAuthenticatorParameters().getUserIdentity().getEmail()).isEqualTo("email");
        assertThat(userIdentityAuthenticator.getAuthenticatorParameters().getUserIdentity().shouldSyncGroups()).isFalse();
        Mockito.verify(authenticationEvent).loginSuccess(request, CredentialsExternalAuthenticationTest.LOGIN, Source.realm(Method.BASIC, CredentialsExternalAuthenticationTest.REALM_NAME));
    }

    @Test
    public void authenticate_with_sonarqube_identity_provider() {
        executeStartWithoutGroupSync();
        Mockito.when(authenticator.doAuthenticate(ArgumentMatchers.any(Context.class))).thenReturn(true);
        UserDetails userDetails = new UserDetails();
        userDetails.setName("name");
        userDetails.setEmail("email");
        Mockito.when(externalUsersProvider.doGetUserDetails(ArgumentMatchers.any(ExternalUsersProvider.Context.class))).thenReturn(userDetails);
        underTest.authenticate(new Credentials(CredentialsExternalAuthenticationTest.LOGIN, CredentialsExternalAuthenticationTest.PASSWORD), request, Method.BASIC);
        assertThat(userIdentityAuthenticator.isAuthenticated()).isTrue();
        assertThat(userIdentityAuthenticator.getAuthenticatorParameters().getProvider().getKey()).isEqualTo("sonarqube");
        assertThat(userIdentityAuthenticator.getAuthenticatorParameters().getProvider().getName()).isEqualTo("sonarqube");
        assertThat(userIdentityAuthenticator.getAuthenticatorParameters().getProvider().getDisplay()).isNull();
        assertThat(userIdentityAuthenticator.getAuthenticatorParameters().getProvider().isEnabled()).isTrue();
        Mockito.verify(authenticationEvent).loginSuccess(request, CredentialsExternalAuthenticationTest.LOGIN, Source.realm(Method.BASIC, CredentialsExternalAuthenticationTest.REALM_NAME));
    }

    @Test
    public void login_is_used_when_no_name_provided() {
        executeStartWithoutGroupSync();
        Mockito.when(authenticator.doAuthenticate(ArgumentMatchers.any(Context.class))).thenReturn(true);
        UserDetails userDetails = new UserDetails();
        userDetails.setEmail("email");
        Mockito.when(externalUsersProvider.doGetUserDetails(ArgumentMatchers.any(ExternalUsersProvider.Context.class))).thenReturn(userDetails);
        underTest.authenticate(new Credentials(CredentialsExternalAuthenticationTest.LOGIN, CredentialsExternalAuthenticationTest.PASSWORD), request, Method.BASIC);
        assertThat(userIdentityAuthenticator.getAuthenticatorParameters().getProvider().getName()).isEqualTo("sonarqube");
        Mockito.verify(authenticationEvent).loginSuccess(request, CredentialsExternalAuthenticationTest.LOGIN, Source.realm(Method.BASIC, CredentialsExternalAuthenticationTest.REALM_NAME));
    }

    @Test
    public void authenticate_with_group_sync() {
        Mockito.when(externalGroupsProvider.doGetGroups(ArgumentMatchers.any(ExternalGroupsProvider.Context.class))).thenReturn(Arrays.asList("group1", "group2"));
        executeStartWithGroupSync();
        executeAuthenticate();
        assertThat(userIdentityAuthenticator.isAuthenticated()).isTrue();
        assertThat(userIdentityAuthenticator.getAuthenticatorParameters().getUserIdentity().shouldSyncGroups()).isTrue();
        Mockito.verify(authenticationEvent).loginSuccess(request, CredentialsExternalAuthenticationTest.LOGIN, Source.realm(Method.BASIC, CredentialsExternalAuthenticationTest.REALM_NAME));
    }

    @Test
    public void use_login_if_user_details_contains_no_name() {
        executeStartWithoutGroupSync();
        Mockito.when(authenticator.doAuthenticate(ArgumentMatchers.any(Context.class))).thenReturn(true);
        UserDetails userDetails = new UserDetails();
        userDetails.setName(null);
        Mockito.when(externalUsersProvider.doGetUserDetails(ArgumentMatchers.any(ExternalUsersProvider.Context.class))).thenReturn(userDetails);
        underTest.authenticate(new Credentials(CredentialsExternalAuthenticationTest.LOGIN, CredentialsExternalAuthenticationTest.PASSWORD), request, Method.BASIC);
        assertThat(userIdentityAuthenticator.isAuthenticated()).isTrue();
        assertThat(userIdentityAuthenticator.getAuthenticatorParameters().getUserIdentity().getName()).isEqualTo(CredentialsExternalAuthenticationTest.LOGIN);
        Mockito.verify(authenticationEvent).loginSuccess(request, CredentialsExternalAuthenticationTest.LOGIN, Source.realm(Method.BASIC, CredentialsExternalAuthenticationTest.REALM_NAME));
    }

    @Test
    public void use_downcase_login() {
        settings.setProperty("sonar.authenticator.downcase", true);
        executeStartWithoutGroupSync();
        executeAuthenticate("LOGIN");
        assertThat(userIdentityAuthenticator.isAuthenticated()).isTrue();
        assertThat(userIdentityAuthenticator.getAuthenticatorParameters().getUserIdentity().getLogin()).isEqualTo("login");
        assertThat(userIdentityAuthenticator.getAuthenticatorParameters().getUserIdentity().getProviderLogin()).isEqualTo("login");
        Mockito.verify(authenticationEvent).loginSuccess(request, "login", Source.realm(Method.BASIC, CredentialsExternalAuthenticationTest.REALM_NAME));
    }

    @Test
    public void does_not_user_downcase_login() {
        settings.setProperty("sonar.authenticator.downcase", false);
        executeStartWithoutGroupSync();
        executeAuthenticate("LoGiN");
        assertThat(userIdentityAuthenticator.isAuthenticated()).isTrue();
        assertThat(userIdentityAuthenticator.getAuthenticatorParameters().getUserIdentity().getLogin()).isEqualTo("LoGiN");
        assertThat(userIdentityAuthenticator.getAuthenticatorParameters().getUserIdentity().getProviderLogin()).isEqualTo("LoGiN");
        Mockito.verify(authenticationEvent).loginSuccess(request, "LoGiN", Source.realm(Method.BASIC, CredentialsExternalAuthenticationTest.REALM_NAME));
    }

    @Test
    public void fail_to_authenticate_when_user_details_are_null() {
        executeStartWithoutGroupSync();
        Mockito.when(authenticator.doAuthenticate(ArgumentMatchers.any(Context.class))).thenReturn(true);
        Mockito.when(externalUsersProvider.doGetUserDetails(ArgumentMatchers.any(ExternalUsersProvider.Context.class))).thenReturn(null);
        expectedException.expect(AuthenticationExceptionMatcher.authenticationException().from(Source.realm(Method.BASIC, CredentialsExternalAuthenticationTest.REALM_NAME)).withLogin(CredentialsExternalAuthenticationTest.LOGIN).andNoPublicMessage());
        expectedException.expectMessage("No user details");
        try {
            underTest.authenticate(new Credentials(CredentialsExternalAuthenticationTest.LOGIN, CredentialsExternalAuthenticationTest.PASSWORD), request, Method.BASIC);
        } finally {
            Mockito.verifyZeroInteractions(authenticationEvent);
        }
    }

    @Test
    public void fail_to_authenticate_when_external_authentication_fails() {
        executeStartWithoutGroupSync();
        Mockito.when(externalUsersProvider.doGetUserDetails(ArgumentMatchers.any(ExternalUsersProvider.Context.class))).thenReturn(new UserDetails());
        Mockito.when(authenticator.doAuthenticate(ArgumentMatchers.any(Context.class))).thenReturn(false);
        expectedException.expect(AuthenticationExceptionMatcher.authenticationException().from(Source.realm(Method.BASIC, CredentialsExternalAuthenticationTest.REALM_NAME)).withLogin(CredentialsExternalAuthenticationTest.LOGIN).andNoPublicMessage());
        expectedException.expectMessage("Realm returned authenticate=false");
        try {
            underTest.authenticate(new Credentials(CredentialsExternalAuthenticationTest.LOGIN, CredentialsExternalAuthenticationTest.PASSWORD), request, Method.BASIC);
        } finally {
            Mockito.verifyZeroInteractions(authenticationEvent);
        }
    }

    @Test
    public void fail_to_authenticate_when_any_exception_is_thrown() {
        executeStartWithoutGroupSync();
        String expectedMessage = "emulating exception in doAuthenticate";
        Mockito.doThrow(new IllegalArgumentException(expectedMessage)).when(authenticator).doAuthenticate(ArgumentMatchers.any(Context.class));
        Mockito.when(externalUsersProvider.doGetUserDetails(ArgumentMatchers.any(ExternalUsersProvider.Context.class))).thenReturn(new UserDetails());
        expectedException.expect(AuthenticationExceptionMatcher.authenticationException().from(Source.realm(Method.BASIC_TOKEN, CredentialsExternalAuthenticationTest.REALM_NAME)).withLogin(CredentialsExternalAuthenticationTest.LOGIN).andNoPublicMessage());
        expectedException.expectMessage(expectedMessage);
        try {
            underTest.authenticate(new Credentials(CredentialsExternalAuthenticationTest.LOGIN, CredentialsExternalAuthenticationTest.PASSWORD), request, Method.BASIC_TOKEN);
        } finally {
            Mockito.verifyZeroInteractions(authenticationEvent);
        }
    }

    @Test
    public void return_empty_user_when_no_realm() {
        assertThat(underTest.authenticate(new Credentials(CredentialsExternalAuthenticationTest.LOGIN, CredentialsExternalAuthenticationTest.PASSWORD), request, Method.BASIC)).isEmpty();
        Mockito.verifyNoMoreInteractions(authenticationEvent);
    }

    @Test
    public void fail_to_start_when_no_authenticator() {
        Mockito.when(realm.doGetAuthenticator()).thenReturn(null);
        Mockito.when(securityRealmFactory.getRealm()).thenReturn(realm);
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("No authenticator available");
        underTest.start();
    }

    @Test
    public void fail_to_start_when_no_user_provider() {
        Mockito.when(realm.doGetAuthenticator()).thenReturn(authenticator);
        Mockito.when(realm.getUsersProvider()).thenReturn(null);
        Mockito.when(securityRealmFactory.getRealm()).thenReturn(realm);
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("No users provider available");
        underTest.start();
    }
}

