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


import CredentialsLocalAuthentication.HashMethod.SHA1;
import System2.INSTANCE;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.DbTester;
import org.sonar.db.user.UserDto;
import org.sonar.db.user.UserTesting;
import org.sonar.server.authentication.event.AuthenticationEvent;
import org.sonar.server.authentication.event.AuthenticationExceptionMatcher;


public class CredentialsAuthenticationTest {
    private static final String LOGIN = "LOGIN";

    private static final String PASSWORD = "PASSWORD";

    private static final String SALT = "0242b0b4c0a93ddfe09dd886de50bc25ba000b51";

    private static final String ENCRYPTED_PASSWORD = "540e4fc4be4e047db995bc76d18374a5b5db08cc";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public DbTester dbTester = DbTester.create(INSTANCE);

    private DbClient dbClient = dbTester.getDbClient();

    private DbSession dbSession = dbTester.getSession();

    private HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

    private AuthenticationEvent authenticationEvent = Mockito.mock(AuthenticationEvent.class);

    private CredentialsExternalAuthentication externalAuthentication = Mockito.mock(CredentialsExternalAuthentication.class);

    private CredentialsLocalAuthentication localAuthentication = new CredentialsLocalAuthentication(dbClient);

    private CredentialsAuthentication underTest = new CredentialsAuthentication(dbClient, authenticationEvent, externalAuthentication, localAuthentication);

    @Test
    public void authenticate_local_user() {
        insertUser(UserTesting.newUserDto().setLogin(CredentialsAuthenticationTest.LOGIN).setCryptedPassword(CredentialsAuthenticationTest.ENCRYPTED_PASSWORD).setHashMethod(SHA1.name()).setSalt(CredentialsAuthenticationTest.SALT).setLocal(true));
        UserDto userDto = executeAuthenticate(Method.BASIC);
        assertThat(userDto.getLogin()).isEqualTo(CredentialsAuthenticationTest.LOGIN);
        Mockito.verify(authenticationEvent).loginSuccess(request, CredentialsAuthenticationTest.LOGIN, AuthenticationEvent.Source.local(Method.BASIC));
    }

    @Test
    public void fail_to_authenticate_local_user_when_password_is_wrong() {
        insertUser(UserTesting.newUserDto().setLogin(CredentialsAuthenticationTest.LOGIN).setCryptedPassword("Wrong password").setSalt("Wrong salt").setHashMethod(SHA1.name()).setLocal(true));
        expectedException.expect(AuthenticationExceptionMatcher.authenticationException().from(AuthenticationEvent.Source.local(Method.BASIC)).withLogin(CredentialsAuthenticationTest.LOGIN).andNoPublicMessage());
        expectedException.expectMessage("wrong password");
        try {
            executeAuthenticate(Method.BASIC);
        } finally {
            Mockito.verifyZeroInteractions(authenticationEvent);
        }
    }

    @Test
    public void authenticate_external_user() {
        Mockito.when(externalAuthentication.authenticate(new Credentials(CredentialsAuthenticationTest.LOGIN, CredentialsAuthenticationTest.PASSWORD), request, Method.BASIC)).thenReturn(Optional.of(UserTesting.newUserDto()));
        insertUser(UserTesting.newUserDto().setLogin(CredentialsAuthenticationTest.LOGIN).setLocal(false));
        executeAuthenticate(Method.BASIC);
        Mockito.verify(externalAuthentication).authenticate(new Credentials(CredentialsAuthenticationTest.LOGIN, CredentialsAuthenticationTest.PASSWORD), request, Method.BASIC);
        Mockito.verifyZeroInteractions(authenticationEvent);
    }

    @Test
    public void fail_to_authenticate_authenticate_external_user_when_no_external_authentication() {
        Mockito.when(externalAuthentication.authenticate(new Credentials(CredentialsAuthenticationTest.LOGIN, CredentialsAuthenticationTest.PASSWORD), request, Method.BASIC_TOKEN)).thenReturn(Optional.empty());
        insertUser(UserTesting.newUserDto().setLogin(CredentialsAuthenticationTest.LOGIN).setLocal(false));
        expectedException.expect(AuthenticationExceptionMatcher.authenticationException().from(AuthenticationEvent.Source.local(Method.BASIC_TOKEN)).withLogin(CredentialsAuthenticationTest.LOGIN).andNoPublicMessage());
        expectedException.expectMessage("User is not local");
        try {
            executeAuthenticate(Method.BASIC_TOKEN);
        } finally {
            Mockito.verifyZeroInteractions(authenticationEvent);
        }
    }

    @Test
    public void fail_to_authenticate_local_user_that_have_no_password() {
        insertUser(UserTesting.newUserDto().setLogin(CredentialsAuthenticationTest.LOGIN).setCryptedPassword(null).setSalt(CredentialsAuthenticationTest.SALT).setHashMethod(SHA1.name()).setLocal(true));
        expectedException.expect(AuthenticationExceptionMatcher.authenticationException().from(AuthenticationEvent.Source.local(Method.BASIC)).withLogin(CredentialsAuthenticationTest.LOGIN).andNoPublicMessage());
        expectedException.expectMessage("null password in DB");
        try {
            executeAuthenticate(Method.BASIC);
        } finally {
            Mockito.verifyZeroInteractions(authenticationEvent);
        }
    }

    @Test
    public void fail_to_authenticate_local_user_that_have_no_salt() {
        insertUser(UserTesting.newUserDto().setLogin(CredentialsAuthenticationTest.LOGIN).setCryptedPassword(CredentialsAuthenticationTest.ENCRYPTED_PASSWORD).setSalt(null).setHashMethod(SHA1.name()).setLocal(true));
        expectedException.expect(AuthenticationExceptionMatcher.authenticationException().from(AuthenticationEvent.Source.local(Method.BASIC_TOKEN)).withLogin(CredentialsAuthenticationTest.LOGIN).andNoPublicMessage());
        expectedException.expectMessage("null salt");
        try {
            executeAuthenticate(Method.BASIC_TOKEN);
        } finally {
            Mockito.verifyZeroInteractions(authenticationEvent);
        }
    }
}

