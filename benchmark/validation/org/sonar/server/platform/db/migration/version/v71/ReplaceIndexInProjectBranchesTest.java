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
import org.sonar.db.CoreDbTester;


public class ReplaceIndexInProjectBranchesTest {
    @Rule
    public final CoreDbTester dbTester = CoreDbTester.createForSchema(ReplaceIndexInProjectBranchesTest.class, "project_branches.sql");

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private ReplaceIndexInProjectBranches underTest = new ReplaceIndexInProjectBranches(dbTester.database());

    @Test
    public void column_is_part_of_index() throws SQLException {
        underTest.execute();
        dbTester.assertUniqueIndex(ReplaceIndexInProjectBranches.TABLE_NAME, ReplaceIndexInProjectBranches.NEW_INDEX_NAME, ReplaceIndexInProjectBranches.PROJECT_UUID_COLUMN.getName(), ReplaceIndexInProjectBranches.KEE_COLUMN.getName(), ReplaceIndexInProjectBranches.KEY_TYPE_COLUMN.getName());
    }

    @Test
    public void adding_pr_with_same_key_as_existing_branch_fails_before_migration() {
        expectedException.expect(IllegalStateException.class);
        String key = "feature/foo";
        insertBranch(1, key);
        insertPullRequest(2, key);
    }

    @Test
    public void adding_pr_with_same_key_as_existing_branch_works_after_migration() throws SQLException {
        underTest.execute();
        String key = "feature/foo";
        insertBranch(1, key);
        insertPullRequest(2, key);
    }
}

