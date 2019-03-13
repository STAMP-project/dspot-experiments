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


import com.google.common.base.Strings;
import java.sql.SQLException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.utils.System2;
import org.sonar.api.utils.internal.TestSystem2;
import org.sonar.core.util.UuidFactory;
import org.sonar.core.util.UuidFactoryFast;
import org.sonar.db.CoreDbTester;


public class PopulateUUIDOnUsersTest {
    private static final long PAST = 10000000000L;

    private static final long NOW = 50000000000L;

    private System2 system2 = new TestSystem2().setNow(PopulateUUIDOnUsersTest.NOW);

    private static final String NO_LOGIN = null;

    private static final String NO_UUID = null;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(PopulateUUIDOnUsersTest.class, "users.sql");

    private UuidFactory uuidFactory = UuidFactoryFast.getInstance();

    private PopulateUUIDOnUsers underTest = new PopulateUUIDOnUsers(db.database(), system2, uuidFactory);

    @Test
    public void update_uuid_when_login_is_present() throws SQLException {
        String login1 = insertUser(PopulateUUIDOnUsersTest.NO_UUID, randomAlphanumeric(10));
        String login2 = insertUser(PopulateUUIDOnUsersTest.NO_UUID, randomAlphanumeric(10));
        String login3 = insertUser(PopulateUUIDOnUsersTest.NO_UUID, randomAlphanumeric(10));
        underTest.execute();
        assertUser(tuple(login1, login1, PopulateUUIDOnUsersTest.PAST, PopulateUUIDOnUsersTest.NOW), tuple(login2, login2, PopulateUUIDOnUsersTest.PAST, PopulateUUIDOnUsersTest.NOW), tuple(login3, login3, PopulateUUIDOnUsersTest.PAST, PopulateUUIDOnUsersTest.NOW));
    }

    @Test
    public void check_max_length() throws Exception {
        String login = insertUser(PopulateUUIDOnUsersTest.NO_UUID, Strings.repeat("a", 255));
        underTest.execute();
        assertUser(tuple(login, login, PopulateUUIDOnUsersTest.PAST, PopulateUUIDOnUsersTest.NOW));
    }

    @Test
    public void generate_random_uuid_when_login_is_null() throws SQLException {
        insertUser(PopulateUUIDOnUsersTest.NO_UUID, PopulateUUIDOnUsersTest.NO_LOGIN);
        insertUser(PopulateUUIDOnUsersTest.NO_UUID, PopulateUUIDOnUsersTest.NO_LOGIN);
        insertUser(PopulateUUIDOnUsersTest.NO_UUID, PopulateUUIDOnUsersTest.NO_LOGIN);
        underTest.execute();
        assertThat(new java.util.ArrayList(db.select("SELECT distinct UUID FROM USERS"))).hasSize(3);
    }

    @Test
    public void do_nothing_when_uuid_is_already_present() throws SQLException {
        String login1 = insertUser(PopulateUUIDOnUsersTest.NO_UUID, randomAlphanumeric(10));
        String login2 = insertUser("existing-uuid", randomAlphanumeric(10));
        underTest.execute();
        assertUser(tuple(login1, login1, PopulateUUIDOnUsersTest.PAST, PopulateUUIDOnUsersTest.NOW), tuple("existing-uuid", login2, PopulateUUIDOnUsersTest.PAST, PopulateUUIDOnUsersTest.PAST));
    }

    @Test
    public void is_reentrant() throws SQLException {
        String login1 = insertUser(PopulateUUIDOnUsersTest.NO_UUID, randomAlphanumeric(10));
        String login2 = insertUser("existing-uuid", randomAlphanumeric(10));
        underTest.execute();
        underTest.execute();
        assertUser(tuple(login1, login1, PopulateUUIDOnUsersTest.PAST, PopulateUUIDOnUsersTest.NOW), tuple("existing-uuid", login2, PopulateUUIDOnUsersTest.PAST, PopulateUUIDOnUsersTest.PAST));
    }
}

