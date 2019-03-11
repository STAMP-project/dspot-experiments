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


import Qualifiers.MODULE;
import Qualifiers.SUBVIEW;
import Qualifiers.UNIT_TEST_FILE;
import Qualifiers.VIEW;
import Scopes.DIRECTORY;
import Scopes.FILE;
import Scopes.PROJECT;
import java.sql.SQLException;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.db.CoreDbTester;


public class PopulateAnalysisUuidOnEventsTest {
    private static final String TABLE_EVENTS = "events";

    private static final String TABLE_SNAPSHOTS = "snapshots";

    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(PopulateAnalysisUuidOnEventsTest.class, "in_progress_events_with_snapshots.sql");

    private PopulateAnalysisUuidOnEvents underTest = new PopulateAnalysisUuidOnEvents(db.database());

    @Test
    public void migration_has_no_effect_on_empty_tables() throws SQLException {
        underTest.execute();
        assertThat(db.countRowsOfTable(PopulateAnalysisUuidOnEventsTest.TABLE_EVENTS)).isEqualTo(0);
    }

    @Test
    public void migration_populates_uuids_of_root_components_only() throws SQLException {
        insertSnapshot(1, "U1", PROJECT, Qualifiers.PROJECT);
        insertSnapshot(2, "U2", PROJECT, MODULE);
        insertSnapshot(3, "U3", DIRECTORY, Qualifiers.DIRECTORY);
        insertSnapshot(4, "U4", FILE, Qualifiers.FILE);
        insertSnapshot(5, "U5", FILE, UNIT_TEST_FILE);
        insertSnapshot(6, "U6", PROJECT, VIEW);
        insertSnapshot(7, "U7", PROJECT, SUBVIEW);
        insertSnapshot(8, "U8", FILE, Qualifiers.PROJECT);
        insertSnapshot(9, "U9", PROJECT, "DEV");
        insertSnapshot(10, "U10", PROJECT, "DEV_PRJ");
        insertSnapshot(11, "U11", "FOO", "BAR");
        insertEvent(21, null);
        insertEvent(22, 1L);
        insertEvent(23, 2L);
        insertEvent(24, 3L);
        insertEvent(25, 4L);
        insertEvent(26, 5L);
        insertEvent(27, 6L);
        insertEvent(28, 7L);
        insertEvent(29, 8L);
        insertEvent(210, 9L);
        insertEvent(211, 10L);
        insertEvent(212, 11L);
        underTest.execute();
        verifyAnalysisUuid(21, null);
        verifyAnalysisUuid(22, "U1");
        verifyAnalysisUuid(23, null);
        verifyAnalysisUuid(24, null);
        verifyAnalysisUuid(25, null);
        verifyAnalysisUuid(26, null);
        verifyAnalysisUuid(27, "U6");
        verifyAnalysisUuid(28, null);
        verifyAnalysisUuid(29, null);
        verifyAnalysisUuid(210, "U9");
        verifyAnalysisUuid(212, null);
        verifyAnalysisUuid(211, null);
    }

    @Test
    public void migration_is_reentrant() throws SQLException {
        insertSnapshot(1, "U1", PROJECT, Qualifiers.PROJECT);
        insertEvent(1, null);
        insertEvent(2, 1L);
        underTest.execute();
        verifyAnalysisUuid(1, null);
        verifyAnalysisUuid(2, "U1");
        underTest.execute();
        verifyAnalysisUuid(1, null);
        verifyAnalysisUuid(2, "U1");
    }
}

