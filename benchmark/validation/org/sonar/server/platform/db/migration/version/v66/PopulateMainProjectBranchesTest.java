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
import org.junit.rules.ExpectedException;
import org.sonar.api.utils.System2;
import org.sonar.api.utils.internal.TestSystem2;
import org.sonar.db.CoreDbTester;


public class PopulateMainProjectBranchesTest {
    private static final long PAST = 10000000000L;

    private static final long NOW = 50000000000L;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(PopulateMainProjectBranchesTest.class, "initial.sql");

    private System2 system2 = new TestSystem2().setNow(PopulateMainProjectBranchesTest.NOW);

    private PopulateMainProjectBranches underTest = new PopulateMainProjectBranches(db.database(), system2);

    @Test
    public void migrate() throws SQLException {
        String project = insertProject();
        underTest.execute();
        assertProjectBranches(tuple("master", project, project, "LONG", PopulateMainProjectBranchesTest.NOW, PopulateMainProjectBranchesTest.NOW));
    }

    @Test
    public void does_nothing_on_non_projects() throws SQLException {
        insertProject(null, "BRC");
        insertProject(null, "VW");
        underTest.execute();
        assertThat(db.countRowsOfTable("project_branches")).isZero();
    }

    @Test
    public void does_nothing_on_empty_table() throws SQLException {
        underTest.execute();
        assertThat(db.countRowsOfTable("project_branches")).isZero();
    }

    @Test
    public void does_nothing_if_already_migrated() throws SQLException {
        String project = insertProject();
        insertMainBranch(project);
        underTest.execute();
        assertProjectBranches(tuple("master", project, project, "LONG", PopulateMainProjectBranchesTest.PAST, PopulateMainProjectBranchesTest.PAST));
    }
}

