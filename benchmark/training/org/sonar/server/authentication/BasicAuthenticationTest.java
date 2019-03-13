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


import Method.BASIC_TOKEN;
import System2.INSTANCE;
import java.util.Base64;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.sonar.db.DbClient;
import org.sonar.db.DbTester;
import org.sonar.db.user.UserDto;
import org.sonar.db.user.UserTesting;
import org.sonar.server.authentication.event.AuthenticationEvent;
import org.sonar.server.authentication.event.AuthenticationExceptionMatcher;
import org.sonar.server.usertoken.UserTokenAuthentication;


public class BasicAuthenticationTest {
    private static final Base64.Encoder BASE64_ENCODER = Base64.getEncoder();

    private static final String A_LOGIN = "login";

    private static final String A_PASSWORD = "password";

    private static final String CREDENTIALS_IN_BASE64 = BasicAuthenticationTest.toBase64((((BasicAuthenticationTest.A_LOGIN) + ":") + (BasicAuthenticationTest.A_PASSWORD)));

    private static final UserDto USER = UserTesting.newUserDto().setLogin(BasicAuthenticationTest.A_LOGIN);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public DbTester db = DbTester.create(INSTANCE);

    private DbClient dbClient = db.getDbClient();

    private CredentialsAuthentication credentialsAuthentication = Mockito.mock(CredentialsAuthentication.class);

    private UserTokenAuthentication userTokenAuthentication = Mockito.mock(UserTokenAuthentication.class);

    private HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

    private AuthenticationEvent authenticationEvent = Mockito.mock(AuthenticationEvent.class);

    private BasicAuthentication underTest = new BasicAuthentication(dbClient, credentialsAuthentication, userTokenAuthentication, authenticationEvent);

    @Test
    public void authenticate_from_basic_http_header() {
        Mockito.when(request.getHeader("Authorization")).thenReturn(("Basic " + (BasicAuthenticationTest.CREDENTIALS_IN_BASE64)));
        Credentials credentials = new Credentials(BasicAuthenticationTest.A_LOGIN, BasicAuthenticationTest.A_PASSWORD);
        Mockito.when(credentialsAuthentication.authenticate(credentials, request, BASIC)).thenReturn(BasicAuthenticationTest.USER);
        underTest.authenticate(request);
        Mockito.verify(credentialsAuthentication).authenticate(credentials, request, BASIC);
        Mockito.verifyNoMoreInteractions(authenticationEvent);
    }

    @Test
    public void authenticate_from_basic_http_header_with_password_containing_semi_colon() {
        String password = "!ascii-only:-)@";
        Mockito.when(request.getHeader("Authorization")).thenReturn(("Basic " + (BasicAuthenticationTest.toBase64((((BasicAuthenticationTest.A_LOGIN) + ":") + password)))));
        Mockito.when(credentialsAuthentication.authenticate(new Credentials(BasicAuthenticationTest.A_LOGIN, password), request, BASIC)).thenReturn(BasicAuthenticationTest.USER);
        underTest.authenticate(request);
        Mockito.verify(credentialsAuthentication).authenticate(new Credentials(BasicAuthenticationTest.A_LOGIN, password), request, BASIC);
        Mockito.verifyNoMoreInteractions(authenticationEvent);
    }

    @Test
    public void does_not_authenticate_when_no_authorization_header() {
        underTest.authenticate(request);
        Mockito.verifyZeroInteractions(credentialsAuthentication, authenticationEvent);
    }

    @Test
    public void does_not_authenticate_when_authorization_header_is_not_BASIC() {
        Mockito.when(request.getHeader("Authorization")).thenReturn(("OTHER " + (BasicAuthenticationTest.CREDENTIALS_IN_BASE64)));
        underTest.authenticate(request);
        Mockito.verifyZeroInteractions(credentialsAuthentication, authenticationEvent);
    }

    @Test
    public void fail_to_authenticate_when_no_login() {
        Mockito.when(request.getHeader("Authorization")).thenReturn(("Basic " + (BasicAuthenticationTest.toBase64((":" + (BasicAuthenticationTest.A_PASSWORD))))));
        expectedException.expect(AuthenticationExceptionMatcher.authenticationException().from(AuthenticationEvent.Source.local(Method.BASIC)).withoutLogin().andNoPublicMessage());
        try {
            underTest.authenticate(request);
        } finally {
            Mockito.verifyZeroInteractions(authenticationEvent);
        }
    }

    @Test
    public void fail_to_authenticate_when_invalid_header() {
        Mockito.when(request.getHeader("Authorization")).thenReturn("Basic Inv?lid");
        expectedException.expect(AuthenticationExceptionMatcher.authenticationException().from(AuthenticationEvent.Source.local(Method.BASIC)).withoutLogin().andNoPublicMessage());
        expectedException.expectMessage("Invalid basic header");
        underTest.authenticate(request);
    }

    @Test
    public void authenticate_from_user_token() {
        UserDto user = db.users().insertUser();
        Mockito.when(userTokenAuthentication.authenticate("token")).thenReturn(Optional.of(user.getUuid()));
        Mockito.when(request.getHeader("Authorization")).thenReturn(("Basic " + (BasicAuthenticationTest.toBase64("token:"))));
        Optional<UserDto> userAuthenticated = underTest.authenticate(request);
        assertThat(userAuthenticated.isPresent()).isTrue();
        assertThat(userAuthenticated.get().getLogin()).isEqualTo(user.getLogin());
        Mockito.verify(authenticationEvent).loginSuccess(request, user.getLogin(), AuthenticationEvent.Source.local(Method.BASIC_TOKEN));
    }

    @Test
    public void does_not_authenticate_from_user_token_when_token_is_invalid() {
        Mockito.when(userTokenAuthentication.authenticate("token")).thenReturn(Optional.empty());
        Mockito.when(request.getHeader("Authorization")).thenReturn(("Basic " + (BasicAuthenticationTest.toBase64("token:"))));
        expectedException.expect(AuthenticationExceptionMatcher.authenticationException().from(AuthenticationEvent.Source.local(Method.BASIC_TOKEN)).withoutLogin().andNoPublicMessage());
        try {
            underTest.authenticate(request);
        } finally {
            Mockito.verifyZeroInteractions(authenticationEvent);
        }
    }

    @Test
    public void does_not_authenticate_from_user_token_when_token_does_not_match_existing_user() {
        Mockito.when(userTokenAuthentication.authenticate("token")).thenReturn(Optional.of("Unknown user"));
        Mockito.when(request.getHeader("Authorization")).thenReturn(("Basic " + (BasicAuthenticationTest.toBase64("token:"))));
        expectedException.expect(AuthenticationExceptionMatcher.authenticationException().from(AuthenticationEvent.Source.local(BASIC_TOKEN)).withoutLogin().andNoPublicMessage());
        try {
            underTest.authenticate(request);
        } finally {
            Mockito.verifyZeroInteractions(authenticationEvent);
        }
    }

    @Test
    public void does_not_authenticate_from_user_token_when_token_does_not_match_active_user() {
        UserDto user = db.users().insertDisabledUser();
        Mockito.when(userTokenAuthentication.authenticate("token")).thenReturn(Optional.of(user.getUuid()));
        Mockito.when(request.getHeader("Authorization")).thenReturn(("Basic " + (BasicAuthenticationTest.toBase64("token:"))));
        expectedException.expect(AuthenticationExceptionMatcher.authenticationException().from(AuthenticationEvent.Source.local(BASIC_TOKEN)).withoutLogin().andNoPublicMessage());
        try {
            underTest.authenticate(request);
        } finally {
            Mockito.verifyZeroInteractions(authenticationEvent);
        }
    }
}

