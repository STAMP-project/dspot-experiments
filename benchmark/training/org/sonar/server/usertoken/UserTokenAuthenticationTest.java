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
package org.sonar.server.usertoken;


import System2.INSTANCE;
import java.util.Optional;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.db.DbTester;
import org.sonar.db.user.UserDto;
import org.sonar.db.user.UserTokenDto;
import org.sonar.server.authentication.UserLastConnectionDatesUpdater;


public class UserTokenAuthenticationTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public DbTester db = DbTester.create(INSTANCE);

    private TokenGenerator tokenGenerator = Mockito.mock(TokenGenerator.class);

    private UserLastConnectionDatesUpdater userLastConnectionDatesUpdater = Mockito.mock(UserLastConnectionDatesUpdater.class);

    private UserTokenAuthentication underTest = new UserTokenAuthentication(tokenGenerator, db.getDbClient(), userLastConnectionDatesUpdater);

    @Test
    public void return_login_when_token_hash_found_in_db() {
        String token = "known-token";
        String tokenHash = "123456789";
        Mockito.when(tokenGenerator.hash(token)).thenReturn(tokenHash);
        UserDto user1 = db.users().insertUser();
        db.users().insertToken(user1, ( t) -> t.setTokenHash(tokenHash));
        UserDto user2 = db.users().insertUser();
        db.users().insertToken(user2, ( t) -> t.setTokenHash("another-token-hash"));
        Optional<String> login = underTest.authenticate(token);
        assertThat(login.isPresent()).isTrue();
        assertThat(login.get()).isEqualTo(user1.getUuid());
        Mockito.verify(userLastConnectionDatesUpdater).updateLastConnectionDateIfNeeded(ArgumentMatchers.any(UserTokenDto.class));
    }

    @Test
    public void return_absent_if_token_hash_is_not_found() {
        Optional<String> login = underTest.authenticate("unknown-token");
        assertThat(login.isPresent()).isFalse();
        Mockito.verify(userLastConnectionDatesUpdater, Mockito.never()).updateLastConnectionDateIfNeeded(ArgumentMatchers.any(UserTokenDto.class));
    }
}

