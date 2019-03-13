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
package org.sonar.server.platform.db.migration.version.v56;


import java.sql.SQLException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.sonar.api.utils.System2;
import org.sonar.db.CoreDbTester;


public class PopulateInitialSchemaTest {
    private static final long NOW = 1500L;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private System2 system2 = Mockito.mock(System2.class);

    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(PopulateInitialSchemaTest.class, "v56.sql");

    private PopulateInitialSchema underTest = new PopulateInitialSchema(db.database(), system2);

    @Test
    public void migration_inserts_users_and_groups() throws SQLException {
        Mockito.when(system2.now()).thenReturn(PopulateInitialSchemaTest.NOW);
        underTest.execute();
        verifyGroup("sonar-administrators", "System administrators");
        verifyGroup("sonar-users", "Any new users created will automatically join this group");
        verifyRolesOfAdminsGroup();
        verifyRolesOfUsersGroup();
        verifyRolesOfAnyone();
        verifyAdminUser();
        verifyMembershipOfAdminUser();
    }
}

