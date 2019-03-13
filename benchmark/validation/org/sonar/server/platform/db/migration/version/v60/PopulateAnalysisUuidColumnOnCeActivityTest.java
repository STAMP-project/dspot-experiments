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


public class PopulateAnalysisUuidColumnOnCeActivityTest {
    private static final long A_DATE = 123456L;

    private static final String TABLE_CE_ACTIVITY = "ce_activity";

    private static final String TABLE_SNAPSHOTS = "snapshots";

    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(PopulateAnalysisUuidColumnOnCeActivityTest.class, "in_progress_ce_activity.sql");

    private PopulateAnalysisUuidColumnOnCeActivity underTest = new PopulateAnalysisUuidColumnOnCeActivity(db.database());

    @Test
    public void migration_has_no_effect_on_empty_tables() throws SQLException {
        underTest.execute();
        assertThat(db.countRowsOfTable(PopulateAnalysisUuidColumnOnCeActivityTest.TABLE_CE_ACTIVITY)).isEqualTo(0);
    }

    @Test
    public void migration_generates_uuids() throws SQLException {
        insertSnapshot(1, "U1");
        insertSnapshot(2, "U2");
        insertCeActivity(1, null);
        insertCeActivity(2, 1L);
        insertCeActivity(3, 2L);
        underTest.execute();
        verifyAnalysisUuid(1, null);
        verifyAnalysisUuid(2, "U1");
        verifyAnalysisUuid(3, "U2");
    }

    @Test
    public void migration_is_reentrant() throws SQLException {
        insertSnapshot(1, "U1");
        insertCeActivity(1, null);
        insertCeActivity(2, 1L);
        underTest.execute();
        verifyAnalysisUuid(1, null);
        verifyAnalysisUuid(2, "U1");
        underTest.execute();
        verifyAnalysisUuid(1, null);
        verifyAnalysisUuid(2, "U1");
    }
}

