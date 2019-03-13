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
package org.sonar.server.platform.db.migration.version.v60;


import java.sql.SQLException;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.db.CoreDbTester;


public class PopulateUuidPathColumnOnProjectsTest {
    private static final String TABLE_PROJECTS = "projects";

    private static final String TABLE_SNAPSHOTS = "snapshots";

    private static final String A_PROJECT_UUID = "U_PRJ";

    private static final String A_MODULE_UUID = "U_MOD";

    private static final String A_DIR_UUID = "U_DIR";

    private static final String A_FILE_UUID = "U_FIL";

    private static final String QUALIFIER_PROJECT = "TRK";

    private static final String QUALIFIER_MODULE = "BRC";

    private static final String QUALIFIER_DIR = "DIR";

    private static final String QUALIFIER_FILE = "FIL";

    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(PopulateUuidPathColumnOnProjectsTest.class, "in_progress_projects_and_snapshots.sql");

    private PopulateUuidPathColumnOnProjects underTest = new PopulateUuidPathColumnOnProjects(db.database());

    @Test
    public void has_no_effect_on_empty_tables() throws SQLException {
        underTest.execute();
        assertThat(db.countRowsOfTable(PopulateUuidPathColumnOnProjectsTest.TABLE_PROJECTS)).isEqualTo(0);
    }

    @Test
    public void migrates_provisioned_projects() throws SQLException {
        insert(PopulateUuidPathColumnOnProjectsTest.QUALIFIER_PROJECT, PopulateUuidPathColumnOnProjectsTest.A_PROJECT_UUID, PopulateUuidPathColumnOnProjectsTest.A_PROJECT_UUID);
        underTest.execute();
        verifyPath(PopulateUuidPathColumnOnProjectsTest.A_PROJECT_UUID, ".");
    }

    @Test
    public void migrates_projects_without_modules() throws SQLException {
        insert(PopulateUuidPathColumnOnProjectsTest.QUALIFIER_PROJECT, PopulateUuidPathColumnOnProjectsTest.A_PROJECT_UUID, PopulateUuidPathColumnOnProjectsTest.A_PROJECT_UUID, new PopulateUuidPathColumnOnProjectsTest.Snapshot(1L, "", true));
        insert(PopulateUuidPathColumnOnProjectsTest.QUALIFIER_DIR, PopulateUuidPathColumnOnProjectsTest.A_DIR_UUID, PopulateUuidPathColumnOnProjectsTest.A_PROJECT_UUID, new PopulateUuidPathColumnOnProjectsTest.Snapshot(2L, "1.", true));
        insert(PopulateUuidPathColumnOnProjectsTest.QUALIFIER_FILE, PopulateUuidPathColumnOnProjectsTest.A_FILE_UUID, PopulateUuidPathColumnOnProjectsTest.A_PROJECT_UUID, new PopulateUuidPathColumnOnProjectsTest.Snapshot(3L, "1.2.", true));
        underTest.execute();
        verifyPath(PopulateUuidPathColumnOnProjectsTest.A_PROJECT_UUID, ".");
        verifyPath(PopulateUuidPathColumnOnProjectsTest.A_DIR_UUID, String.format(".%s.", PopulateUuidPathColumnOnProjectsTest.A_PROJECT_UUID));
        verifyPath(PopulateUuidPathColumnOnProjectsTest.A_FILE_UUID, String.format(".%s.%s.", PopulateUuidPathColumnOnProjectsTest.A_PROJECT_UUID, PopulateUuidPathColumnOnProjectsTest.A_DIR_UUID));
    }

    @Test
    public void migrates_projects_with_modules() throws SQLException {
        insert(PopulateUuidPathColumnOnProjectsTest.QUALIFIER_PROJECT, PopulateUuidPathColumnOnProjectsTest.A_PROJECT_UUID, PopulateUuidPathColumnOnProjectsTest.A_PROJECT_UUID, new PopulateUuidPathColumnOnProjectsTest.Snapshot(1L, "", true));
        insert(PopulateUuidPathColumnOnProjectsTest.QUALIFIER_MODULE, PopulateUuidPathColumnOnProjectsTest.A_MODULE_UUID, PopulateUuidPathColumnOnProjectsTest.A_PROJECT_UUID, new PopulateUuidPathColumnOnProjectsTest.Snapshot(2L, "1.", true));
        insert(PopulateUuidPathColumnOnProjectsTest.QUALIFIER_DIR, PopulateUuidPathColumnOnProjectsTest.A_DIR_UUID, PopulateUuidPathColumnOnProjectsTest.A_PROJECT_UUID, new PopulateUuidPathColumnOnProjectsTest.Snapshot(3L, "1.2.", true));
        insert(PopulateUuidPathColumnOnProjectsTest.QUALIFIER_FILE, PopulateUuidPathColumnOnProjectsTest.A_FILE_UUID, PopulateUuidPathColumnOnProjectsTest.A_PROJECT_UUID, new PopulateUuidPathColumnOnProjectsTest.Snapshot(4L, "1.2.3.", true));
        underTest.execute();
        verifyPath(PopulateUuidPathColumnOnProjectsTest.A_PROJECT_UUID, ".");
        verifyPath(PopulateUuidPathColumnOnProjectsTest.A_MODULE_UUID, String.format(".%s.", PopulateUuidPathColumnOnProjectsTest.A_PROJECT_UUID));
        verifyPath(PopulateUuidPathColumnOnProjectsTest.A_DIR_UUID, String.format(".%s.%s.", PopulateUuidPathColumnOnProjectsTest.A_PROJECT_UUID, PopulateUuidPathColumnOnProjectsTest.A_MODULE_UUID));
        verifyPath(PopulateUuidPathColumnOnProjectsTest.A_FILE_UUID, String.format(".%s.%s.%s.", PopulateUuidPathColumnOnProjectsTest.A_PROJECT_UUID, PopulateUuidPathColumnOnProjectsTest.A_MODULE_UUID, PopulateUuidPathColumnOnProjectsTest.A_DIR_UUID));
    }

    @Test
    public void migrates_components_without_snapshot_path() throws SQLException {
        // these components do not have snapshots
        insert(PopulateUuidPathColumnOnProjectsTest.QUALIFIER_DIR, PopulateUuidPathColumnOnProjectsTest.A_DIR_UUID, PopulateUuidPathColumnOnProjectsTest.A_PROJECT_UUID);
        insert(PopulateUuidPathColumnOnProjectsTest.QUALIFIER_FILE, PopulateUuidPathColumnOnProjectsTest.A_FILE_UUID, PopulateUuidPathColumnOnProjectsTest.A_PROJECT_UUID);
        underTest.execute();
        verifyPath(PopulateUuidPathColumnOnProjectsTest.A_DIR_UUID, String.format(".%s.", PopulateUuidPathColumnOnProjectsTest.A_PROJECT_UUID));
        verifyPath(PopulateUuidPathColumnOnProjectsTest.A_FILE_UUID, String.format(".%s.", PopulateUuidPathColumnOnProjectsTest.A_PROJECT_UUID));
    }

    @Test
    public void migration_is_reentrant() throws SQLException {
        insert(PopulateUuidPathColumnOnProjectsTest.QUALIFIER_PROJECT, PopulateUuidPathColumnOnProjectsTest.A_PROJECT_UUID, PopulateUuidPathColumnOnProjectsTest.A_PROJECT_UUID, new PopulateUuidPathColumnOnProjectsTest.Snapshot(1L, "", true));
        insert(PopulateUuidPathColumnOnProjectsTest.QUALIFIER_DIR, PopulateUuidPathColumnOnProjectsTest.A_DIR_UUID, PopulateUuidPathColumnOnProjectsTest.A_PROJECT_UUID, new PopulateUuidPathColumnOnProjectsTest.Snapshot(2L, "1.", true));
        insert(PopulateUuidPathColumnOnProjectsTest.QUALIFIER_FILE, PopulateUuidPathColumnOnProjectsTest.A_FILE_UUID, PopulateUuidPathColumnOnProjectsTest.A_PROJECT_UUID, new PopulateUuidPathColumnOnProjectsTest.Snapshot(3L, "1.2.", true));
        underTest.execute();
        verifyNoNullPath();
        underTest.execute();
        verifyNoNullPath();
    }

    @Test
    public void ignore_snapshots_with_invalid_snapshots_in_path() throws SQLException {
        insert(PopulateUuidPathColumnOnProjectsTest.QUALIFIER_PROJECT, PopulateUuidPathColumnOnProjectsTest.A_PROJECT_UUID, PopulateUuidPathColumnOnProjectsTest.A_PROJECT_UUID, new PopulateUuidPathColumnOnProjectsTest.Snapshot(1L, "", true));
        // the ID 999999 is unknown in the path
        insert(PopulateUuidPathColumnOnProjectsTest.QUALIFIER_DIR, PopulateUuidPathColumnOnProjectsTest.A_DIR_UUID, PopulateUuidPathColumnOnProjectsTest.A_PROJECT_UUID, new PopulateUuidPathColumnOnProjectsTest.Snapshot(2L, "1.999999.", true));
        underTest.execute();
        verifyPath(PopulateUuidPathColumnOnProjectsTest.A_PROJECT_UUID, ".");
        // path of orphans is the path to project only
        verifyPath(PopulateUuidPathColumnOnProjectsTest.A_DIR_UUID, String.format(".%s.", PopulateUuidPathColumnOnProjectsTest.A_PROJECT_UUID));
    }

    private static final class Snapshot {
        private final long id;

        private final String idPath;

        private final boolean isLast;

        Snapshot(long id, String idPath, boolean isLast) {
            this.id = id;
            this.idPath = idPath;
            this.isLast = isLast;
        }
    }
}

