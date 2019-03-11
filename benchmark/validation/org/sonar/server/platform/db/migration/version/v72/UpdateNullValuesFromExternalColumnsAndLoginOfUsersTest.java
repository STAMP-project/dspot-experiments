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


import LoggerLevel.WARN;
import java.sql.SQLException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.utils.System2;
import org.sonar.api.utils.internal.TestSystem2;
import org.sonar.api.utils.log.LogTester;
import org.sonar.core.util.SequenceUuidFactory;
import org.sonar.db.CoreDbTester;


public class UpdateNullValuesFromExternalColumnsAndLoginOfUsersTest {
    private static final long PAST = 5000000000L;

    private static final long NOW = 10000000000L;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(UpdateNullValuesFromExternalColumnsAndLoginOfUsersTest.class, "users.sql");

    @Rule
    public LogTester logTester = new LogTester();

    private System2 system2 = new TestSystem2().setNow(UpdateNullValuesFromExternalColumnsAndLoginOfUsersTest.NOW);

    private UpdateNullValuesFromExternalColumnsAndLoginOfUsers underTest = new UpdateNullValuesFromExternalColumnsAndLoginOfUsers(db.database(), system2, new SequenceUuidFactory());

    @Test
    public void update_users() throws SQLException {
        insertUser("USER_1", "user1", "github");
        insertUser("USER_2", null, null);
        insertUser("USER_3", "user", null);
        insertUser("USER_4", null, "github");
        insertUser(null, "user", "bitbucket");
        insertUser(null, null, null);
        underTest.execute();
        assertUsers(tuple("USER_1", "user1", "github", UpdateNullValuesFromExternalColumnsAndLoginOfUsersTest.PAST), tuple("USER_2", "USER_2", "sonarqube", UpdateNullValuesFromExternalColumnsAndLoginOfUsersTest.NOW), tuple("USER_3", "USER_3", "sonarqube", UpdateNullValuesFromExternalColumnsAndLoginOfUsersTest.NOW), tuple("USER_4", "USER_4", "sonarqube", UpdateNullValuesFromExternalColumnsAndLoginOfUsersTest.NOW), tuple("1", "1", "sonarqube", UpdateNullValuesFromExternalColumnsAndLoginOfUsersTest.NOW), tuple("2", "2", "sonarqube", UpdateNullValuesFromExternalColumnsAndLoginOfUsersTest.NOW));
    }

    @Test
    public void log_warning_when_login_is_null() throws SQLException {
        insertUser(null, "user", "bitbucket");
        long id = ((long) (db.selectFirst("SELECT ID FROM USERS").get("ID")));
        underTest.execute();
        assertThat(logTester.logs(WARN)).containsExactlyInAnyOrder(String.format("No login has been found for user id '%s'. A UUID has been generated to not have null value.", id));
    }

    @Test
    public void is_reentrant() throws SQLException {
        insertUser("USER_1", null, null);
        underTest.execute();
        underTest.execute();
        assertUsers(tuple("USER_1", "USER_1", "sonarqube", UpdateNullValuesFromExternalColumnsAndLoginOfUsersTest.NOW));
    }
}

