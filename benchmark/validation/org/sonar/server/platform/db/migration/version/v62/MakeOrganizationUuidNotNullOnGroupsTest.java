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
package org.sonar.server.platform.db.migration.version.v62;


import java.sql.SQLException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.db.CoreDbTester;


public class MakeOrganizationUuidNotNullOnGroupsTest {
    private static final String TABLE_GROUPS = "groups";

    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(MakeOrganizationUuidNotNullOnGroupsTest.class, "in_progress_groups.sql");

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private MakeOrganizationUuidNotNullOnGroups underTest = new MakeOrganizationUuidNotNullOnGroups(db.database());

    @Test
    public void migration_sets_uuid_column_not_nullable_on_empty_table() throws SQLException {
        underTest.execute();
        verifyColumnDefinition();
    }

    @Test
    public void migration_sets_uuid_column_not_nullable_on_populated_table() throws SQLException {
        insertGroup(1, true);
        insertGroup(2, true);
        underTest.execute();
        verifyColumnDefinition();
    }

    @Test
    public void migration_fails_if_some_row_has_a_null_uuid() throws SQLException {
        insertGroup(1, false);
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Fail to execute");
        underTest.execute();
    }
}

