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
package org.sonar.server.platform.db.migration.version.v72;


import java.sql.SQLException;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.utils.System2;
import org.sonar.api.utils.internal.TestSystem2;
import org.sonar.db.CoreDbTester;


public class PopulateHashMethodOnUsersTest {
    private static final long NOW = 10000000000L;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(PopulateHashMethodOnUsersTest.class, "users.sql");

    private System2 system2 = new TestSystem2().setNow(PopulateHashMethodOnUsersTest.NOW);

    private PopulateHashMethodOnUsers underTest = new PopulateHashMethodOnUsers(db.database(), system2);

    @Test
    public void should_update_only_local_users() throws SQLException {
        IntStream.range(0, 99).forEach(( i) -> insertLocalUser(null));
        IntStream.range(0, 100).forEach(( i) -> insertExternalUser());
        underTest.execute();
        assertThat(db.countSql("select count(id) from users where hash_method='SHA1'")).isEqualTo(99);
        assertThat(db.countSql("select count(id) from users where hash_method is not null and crypted_password is null")).isEqualTo(0);
    }

    @Test
    public void should_add_sha1_in_hash_method() throws SQLException {
        IntStream.range(0, 99).forEach(( i) -> insertLocalUser(null));
        underTest.execute();
        assertThat(db.countSql("select count(id) from users where hash_method <> 'SHA1'")).isEqualTo(0);
    }

    @Test
    public void is_reentrant() throws SQLException {
        IntStream.range(0, 99).forEach(( i) -> insertLocalUser(null));
        IntStream.range(0, 100).forEach(( i) -> insertExternalUser());
        underTest.execute();
        underTest.execute();
        assertThat(db.countSql("select count(id) from users where hash_method='SHA1'")).isEqualTo(99);
        assertThat(db.countSql("select count(id) from users where hash_method is not null and crypted_password is null")).isEqualTo(0);
    }
}

