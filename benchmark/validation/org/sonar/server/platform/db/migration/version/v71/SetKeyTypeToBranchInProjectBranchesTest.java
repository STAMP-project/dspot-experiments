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
package org.sonar.server.platform.db.migration.version.v71;


import java.sql.SQLException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.utils.System2;
import org.sonar.api.utils.internal.TestSystem2;
import org.sonar.db.CoreDbTester;


public class SetKeyTypeToBranchInProjectBranchesTest {
    private static final long PAST = 10000000000L;

    private static final long NOW = 50000000000L;

    private System2 system2 = new TestSystem2().setNow(SetKeyTypeToBranchInProjectBranchesTest.NOW);

    @Rule
    public final CoreDbTester dbTester = CoreDbTester.createForSchema(SetKeyTypeToBranchInProjectBranchesTest.class, "project_branches.sql");

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private SetKeyTypeToBranchInProjectBranches underTest = new SetKeyTypeToBranchInProjectBranches(dbTester.database(), system2);

    @Test
    public void has_no_effect_if_table_project_branches_is_empty() throws SQLException {
        underTest.execute();
        assertThat(dbTester.countRowsOfTable(SetKeyTypeToBranchInProjectBranches.TABLE_NAME)).isEqualTo(0);
    }

    @Test
    public void updates_rows_to_BRANCH() throws SQLException {
        insertRow(1, "SHORT");
        insertRow(2, "LONG");
        insertRow(3, "SHORT");
        insertRow(4, "LONG");
        String countUpdatedAtSQL = ("select count(uuid) from " + (SetKeyTypeToBranchInProjectBranches.TABLE_NAME)) + " where updated_at = ";
        assertThat(countRowsWithValue(null)).isEqualTo(4);
        assertThat(countRowsWithValue(SetKeyTypeToBranchInProjectBranches.DEFAULT_KEY_TYPE)).isEqualTo(0);
        assertThat(dbTester.countSql((countUpdatedAtSQL + (SetKeyTypeToBranchInProjectBranchesTest.PAST)))).isEqualTo(4);
        underTest.execute();
        assertThat(countRowsWithValue(null)).isEqualTo(0);
        assertThat(countRowsWithValue(SetKeyTypeToBranchInProjectBranches.DEFAULT_KEY_TYPE)).isEqualTo(4);
        assertThat(dbTester.countSql((countUpdatedAtSQL + (SetKeyTypeToBranchInProjectBranchesTest.NOW)))).isEqualTo(4);
    }

    @Test
    public void execute_is_reentreant() throws SQLException {
        insertRow(1, "SHORT");
        insertRow(2, "LONG");
        insertRow(3, "SHORT");
        insertRow(4, "LONG");
        underTest.execute();
        underTest.execute();
        assertThat(countRowsWithValue(null)).isEqualTo(0);
        assertThat(countRowsWithValue(SetKeyTypeToBranchInProjectBranches.DEFAULT_KEY_TYPE)).isEqualTo(4);
    }
}

