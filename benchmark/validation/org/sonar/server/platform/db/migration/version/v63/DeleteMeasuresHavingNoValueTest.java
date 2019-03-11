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
package org.sonar.server.platform.db.migration.version.v63;


import java.sql.SQLException;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.db.CoreDbTester;


public class DeleteMeasuresHavingNoValueTest {
    private static final String TABLE_MEASURES = "project_measures";

    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(DeleteMeasuresHavingNoValueTest.class, "project_measures.sql");

    private DeleteMeasuresHavingNoValue underTest = new DeleteMeasuresHavingNoValue(db.database());

    @Test
    public void migration_has_no_effect_on_empty_tables() throws SQLException {
        underTest.execute();
        assertThat(db.countRowsOfTable(DeleteMeasuresHavingNoValueTest.TABLE_MEASURES)).isZero();
    }

    @Test
    public void migration_does_not_remove_measures_with_value() throws SQLException {
        insertMeasure(5.0, null, null, null);
        insertMeasure(null, "text", null, null);
        insertMeasure(null, null, "data", null);
        insertMeasure(null, null, null, 50.0);
        underTest.execute();
        assertThat(db.countRowsOfTable(DeleteMeasuresHavingNoValueTest.TABLE_MEASURES)).isEqualTo(4);
    }

    @Test
    public void migration_removes_measures_with_no_values() throws SQLException {
        insertMeasure(null, null, null, null);
        insertMeasure(null, null, null, null);
        insertMeasure(null, null, null, null);
        underTest.execute();
        assertThat(db.countRowsOfTable(DeleteMeasuresHavingNoValueTest.TABLE_MEASURES)).isZero();
    }

    @Test
    public void migration_does_not_remove_measures_having_variation_on_leak_period() throws SQLException {
        insertMeasureOnlyOnVariations(10.0, null, null, null, null);
        insertMeasureOnlyOnVariations(11.0, 2.0, null, null, null);
        insertMeasureOnlyOnVariations(12.0, null, 3.0, 4.0, 5.0);
        insertMeasureOnlyOnVariations(12.0, 2.0, 3.0, 4.0, 5.0);
        underTest.execute();
        assertThat(db.countRowsOfTable(DeleteMeasuresHavingNoValueTest.TABLE_MEASURES)).isEqualTo(4);
    }

    @Test
    public void migration_removes_measures_having_only_variation_on_periods2_to_5() throws SQLException {
        insertMeasureOnlyOnVariations(null, 2.0, null, null, null);
        insertMeasureOnlyOnVariations(null, null, 3.0, null, null);
        insertMeasureOnlyOnVariations(null, null, null, 4.0, null);
        insertMeasureOnlyOnVariations(null, null, null, null, 5.0);
        insertMeasureOnlyOnVariations(null, 2.0, 3.0, 4.0, 5.0);
        underTest.execute();
        assertThat(db.countRowsOfTable(DeleteMeasuresHavingNoValueTest.TABLE_MEASURES)).isZero();
    }

    @Test
    public void migration_is_reentrant() throws SQLException {
        insertMeasure(5.0, null, null, null);
        insertMeasure(null, "text", null, null);
        insertMeasure(null, null, null, null);
        insertMeasure(null, null, null, null);
        underTest.execute();
        assertThat(db.countRowsOfTable(DeleteMeasuresHavingNoValueTest.TABLE_MEASURES)).isEqualTo(2);
        underTest.execute();
        assertThat(db.countRowsOfTable(DeleteMeasuresHavingNoValueTest.TABLE_MEASURES)).isEqualTo(2);
    }
}

