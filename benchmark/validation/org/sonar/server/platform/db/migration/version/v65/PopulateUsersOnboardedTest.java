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
package org.sonar.server.platform.db.migration.version.v65;


import java.sql.SQLException;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.api.utils.System2;
import org.sonar.api.utils.internal.TestSystem2;
import org.sonar.db.CoreDbTester;


public class PopulateUsersOnboardedTest {
    private static final long PAST = 100000000000L;

    private static final long NOW = 500000000000L;

    private System2 system2 = new TestSystem2().setNow(PopulateUsersOnboardedTest.NOW);

    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(PopulateUsersOnboardedTest.class, "users_with_onboarded_column.sql");

    public PopulateUsersOnboarded underTest = new PopulateUsersOnboarded(db.database(), system2);

    @Test
    public void set_onboarded_to_true() throws SQLException {
        insertUser("admin");
        insertUser("user");
        assertUsers(tuple("admin", false, PopulateUsersOnboardedTest.PAST), tuple("user", false, PopulateUsersOnboardedTest.PAST));
        underTest.execute();
        assertUsers(tuple("admin", true, PopulateUsersOnboardedTest.NOW), tuple("user", true, PopulateUsersOnboardedTest.NOW));
    }
}

