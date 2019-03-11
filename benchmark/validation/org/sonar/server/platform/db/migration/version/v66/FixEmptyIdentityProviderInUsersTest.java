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
package org.sonar.server.platform.db.migration.version.v66;


import java.sql.SQLException;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.api.utils.System2;
import org.sonar.api.utils.internal.TestSystem2;
import org.sonar.db.CoreDbTester;


public class FixEmptyIdentityProviderInUsersTest {
    private static final long PAST = 100000000000L;

    private static final long NOW = 500000000000L;

    private System2 system2 = new TestSystem2().setNow(FixEmptyIdentityProviderInUsersTest.NOW);

    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(FixEmptyIdentityProviderInUsersTest.class, "users.sql");

    private FixEmptyIdentityProviderInUsers underTest = new FixEmptyIdentityProviderInUsers(db.database(), system2);

    @Test
    public void execute_has_no_effect_if_tables_are_empty() throws SQLException {
        underTest.execute();
    }

    @Test
    public void migrate_user_without_external_identity_info() throws SQLException {
        insertUser("userWithoutExternalIdentityInfo", null, null);
        underTest.execute();
        assertUsers(tuple("userWithoutExternalIdentityInfo", "userWithoutExternalIdentityInfo", "sonarqube", FixEmptyIdentityProviderInUsersTest.NOW));
    }

    @Test
    public void migrate_user_with_partial_external_identity_info() throws SQLException {
        insertUser("userWithoutExternalIdentity", "user", null);
        insertUser("userWithoutExternalIdentityProvider", null, "github");
        underTest.execute();
        assertUsers(tuple("userWithoutExternalIdentity", "userWithoutExternalIdentity", "sonarqube", FixEmptyIdentityProviderInUsersTest.NOW), tuple("userWithoutExternalIdentityProvider", "userWithoutExternalIdentityProvider", "sonarqube", FixEmptyIdentityProviderInUsersTest.NOW));
    }

    @Test
    public void does_not_migrate_user_with_external_identity_info() throws SQLException {
        insertUser("userWithIdentityInfo", "user", "sonarqube");
        underTest.execute();
        assertUsers(tuple("userWithIdentityInfo", "user", "sonarqube", FixEmptyIdentityProviderInUsersTest.PAST));
    }
}

