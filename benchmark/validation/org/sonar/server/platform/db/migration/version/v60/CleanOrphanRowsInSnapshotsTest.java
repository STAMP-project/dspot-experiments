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


public class CleanOrphanRowsInSnapshotsTest {
    private static final String SNAPSHOTS_TABLE = "snapshots";

    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(CleanOrphanRowsInSnapshotsTest.class, "in_progress_snapshots_and_children_tables.sql");

    private CleanOrphanRowsInSnapshots underTest = new CleanOrphanRowsInSnapshots(db.database());

    @Test
    public void migration_has_no_effect_on_empty_table() throws SQLException {
        underTest.execute();
        assertThat(db.countRowsOfTable(CleanOrphanRowsInSnapshotsTest.SNAPSHOTS_TABLE)).isEqualTo(0);
    }

    @Test
    public void migration_deletes_any_row_with_a_null_uuid() throws SQLException {
        insertSnapshots(1, true, true);
        insertSnapshots(2, false, false);
        insertSnapshots(3, true, false);
        insertSnapshots(4, false, true);
        insertSnapshots(5, true, true);
        underTest.execute();
        assertThat(idsOfRowsInSnapshots()).containsOnly(1L, 5L);
    }

    @Test
    public void migration_deletes_rows_in_children_tables_referencing_snapshots_with_at_least_null_uuid() throws SQLException {
        insertSnapshots(1, true, true);
        insertSnapshots(2, false, true);
        insertSnapshots(3, true, false);
        insertDuplicationIndex(1, 1);
        insertDuplicationIndex(30, 1);
        insertDuplicationIndex(1, 40);
        insertDuplicationIndex(50, 2);
        insertDuplicationIndex(2, 2);
        insertDuplicationIndex(2, 60);
        insertDuplicationIndex(3, 3);
        insertDuplicationIndex(70, 3);
        insertDuplicationIndex(3, 90);
        insertProjectMeasure(1);
        insertProjectMeasure(2);
        insertProjectMeasure(3);
        insertCeActivity(1);
        insertCeActivity(2);
        insertCeActivity(3);
        insertEvents(1);
        insertEvents(2);
        insertEvents(3);
        underTest.execute();
        verifyLineCountsPerSnapshot(1, 1, 3, 1, 1, 1);
        verifyLineCountsPerSnapshot(2, 0, 0, 0, 0, 0);
        verifyLineCountsPerSnapshot(3, 0, 0, 0, 0, 0);
    }
}

