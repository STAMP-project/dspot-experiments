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


public class PopulateComponentUuidOfMeasuresTest {
    private static final int SNAPSHOT_ID_1 = 40;

    private static final int SNAPSHOT_ID_2 = 50;

    private static final int SNAPSHOT_ID_3 = 60;

    private static final int SNAPSHOT_ID_4 = 70;

    private static final int SNAPSHOT_ID_5 = 80;

    private static final int COMPONENT_ID_1 = 400;

    private static final int COMPONENT_ID_2 = 500;

    private static final int COMPONENT_ID_3 = 600;

    private static final String COMPONENT_UUID_1 = "U400";

    private static final String COMPONENT_UUID_2 = "U500";

    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(PopulateComponentUuidOfMeasuresTest.class, "in_progress_measures_with_projects.sql");

    private PopulateComponentUuidOfMeasures underTest = new PopulateComponentUuidOfMeasures(db.database());

    @Test
    public void migration_has_no_effect_on_empty_tables() throws SQLException {
        underTest.execute();
        assertThat(db.countRowsOfTable("project_measures")).isEqualTo(0);
        assertThat(db.countRowsOfTable("snapshots")).isEqualTo(0);
    }

    @Test
    public void migration_updates_component_uuid_with_values_from_table_snapshots_when_they_exist() throws SQLException {
        insertSnapshot(PopulateComponentUuidOfMeasuresTest.SNAPSHOT_ID_1, PopulateComponentUuidOfMeasuresTest.COMPONENT_UUID_1);
        insertSnapshot(PopulateComponentUuidOfMeasuresTest.SNAPSHOT_ID_2, PopulateComponentUuidOfMeasuresTest.COMPONENT_UUID_1);
        insertSnapshot(PopulateComponentUuidOfMeasuresTest.SNAPSHOT_ID_3, PopulateComponentUuidOfMeasuresTest.COMPONENT_UUID_2);
        insertSnapshot(PopulateComponentUuidOfMeasuresTest.SNAPSHOT_ID_4, PopulateComponentUuidOfMeasuresTest.COMPONENT_UUID_2);
        insertMeasure(1, PopulateComponentUuidOfMeasuresTest.SNAPSHOT_ID_1, PopulateComponentUuidOfMeasuresTest.COMPONENT_ID_1);
        insertMeasure(2, PopulateComponentUuidOfMeasuresTest.SNAPSHOT_ID_2, PopulateComponentUuidOfMeasuresTest.COMPONENT_ID_1);
        insertMeasure(3, PopulateComponentUuidOfMeasuresTest.SNAPSHOT_ID_3, PopulateComponentUuidOfMeasuresTest.COMPONENT_ID_2);
        insertMeasure(4, PopulateComponentUuidOfMeasuresTest.SNAPSHOT_ID_5, PopulateComponentUuidOfMeasuresTest.COMPONENT_ID_3);// snapshot does not exist

        underTest.execute();
        verifyMeasure(1, PopulateComponentUuidOfMeasuresTest.SNAPSHOT_ID_1, PopulateComponentUuidOfMeasuresTest.COMPONENT_UUID_1);
        verifyMeasure(2, PopulateComponentUuidOfMeasuresTest.SNAPSHOT_ID_2, PopulateComponentUuidOfMeasuresTest.COMPONENT_UUID_1);
        verifyMeasure(3, PopulateComponentUuidOfMeasuresTest.SNAPSHOT_ID_3, PopulateComponentUuidOfMeasuresTest.COMPONENT_UUID_2);
        verifyMeasure(4, PopulateComponentUuidOfMeasuresTest.SNAPSHOT_ID_5, null);
    }

    @Test
    public void migration_is_reentrant() throws SQLException {
        insertSnapshot(PopulateComponentUuidOfMeasuresTest.SNAPSHOT_ID_1, PopulateComponentUuidOfMeasuresTest.COMPONENT_UUID_1);
        insertMeasure(1, PopulateComponentUuidOfMeasuresTest.SNAPSHOT_ID_1, PopulateComponentUuidOfMeasuresTest.COMPONENT_ID_1);
        underTest.execute();
        verifyMeasure(1, PopulateComponentUuidOfMeasuresTest.SNAPSHOT_ID_1, PopulateComponentUuidOfMeasuresTest.COMPONENT_UUID_1);
        underTest.execute();
        verifyMeasure(1, PopulateComponentUuidOfMeasuresTest.SNAPSHOT_ID_1, PopulateComponentUuidOfMeasuresTest.COMPONENT_UUID_1);
    }
}

