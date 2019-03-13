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


public class PopulateComponentUuidAndAnalysisUuidOfDuplicationsIndexTest {
    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(PopulateComponentUuidAndAnalysisUuidOfDuplicationsIndexTest.class, "in_progress_measures_with_snapshots.sql");

    private PopulateComponentUuidAndAnalysisUuidOfDuplicationsIndex underTest = new PopulateComponentUuidAndAnalysisUuidOfDuplicationsIndex(db.database());

    @Test
    public void migration_has_no_effect_on_empty_tables() throws SQLException {
        underTest.execute();
        assertThat(db.countRowsOfTable("duplications_index")).isEqualTo(0);
        assertThat(db.countRowsOfTable("snapshots")).isEqualTo(0);
    }

    @Test
    public void migration_updates_component_uuid_with_values_from_table_snapshots_when_they_exist() throws SQLException {
        insertSnapshot(40, "cpt1");
        String rootUuid1 = insertSnapshot(50, "cpt2");
        insertSnapshot(60, "cpt3");
        String rootUuid2 = insertSnapshot(70, "cpt4");
        insertDuplicationIndex(1, 40, 50);
        insertDuplicationIndex(2, 40, 50);
        insertDuplicationIndex(3, 40, 70);
        insertDuplicationIndex(4, 60, 110);// 110 doesn't exist

        insertDuplicationIndex(5, 90, 120);// 90 and 120 does not exist

        insertDuplicationIndex(6, 100, 70);// 100 does not exist

        underTest.execute();
        verifyDuplicationsIndex(1, 40, "cpt1", rootUuid1);
        verifyDuplicationsIndex(2, 40, "cpt1", rootUuid1);
        verifyDuplicationsIndex(3, 40, "cpt1", rootUuid2);
        verifyDuplicationsIndex(4, 60, "cpt3", null);
        verifyDuplicationsIndex(5, 90, null, null);
        verifyDuplicationsIndex(6, 100, null, rootUuid2);
    }

    @Test
    public void migration_is_reentrant() throws SQLException {
        insertSnapshot(40, "cpt1");
        String rootUuid = insertSnapshot(50, "cp2");
        insertDuplicationIndex(1, 40, 50);
        underTest.execute();
        verifyDuplicationsIndex(1, 40, "cpt1", rootUuid);
        underTest.execute();
        verifyDuplicationsIndex(1, 40, "cpt1", rootUuid);
    }
}

