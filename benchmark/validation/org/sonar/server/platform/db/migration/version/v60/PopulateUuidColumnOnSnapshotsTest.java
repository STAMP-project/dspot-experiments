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
import org.sonar.core.util.UuidFactoryImpl;
import org.sonar.db.CoreDbTester;


public class PopulateUuidColumnOnSnapshotsTest {
    private static final String TABLE_SNAPSHOTS = "snapshots";

    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(PopulateUuidColumnOnSnapshotsTest.class, "in_progress_snapshots.sql");

    private PopulateUuidColumnOnSnapshots underTest = new PopulateUuidColumnOnSnapshots(db.database(), UuidFactoryImpl.INSTANCE);

    @Test
    public void migration_has_no_effect_on_empty_tables() throws SQLException {
        underTest.execute();
        assertThat(db.countRowsOfTable(PopulateUuidColumnOnSnapshotsTest.TABLE_SNAPSHOTS)).isEqualTo(0);
    }

    @Test
    public void migration_generates_uuids() throws SQLException {
        insertSnapshot(1);
        insertSnapshot(2);
        insertSnapshot(3);
        underTest.execute();
        verifyUuids(3);
    }

    @Test
    public void migration_is_reentrant() throws SQLException {
        insertSnapshot(1);
        underTest.execute();
        verifyUuids(1);
        underTest.execute();
        verifyUuids(1);
    }
}

