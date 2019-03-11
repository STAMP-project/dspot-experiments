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
package org.sonar.server.platform.db.migration.version.v65;


import java.sql.SQLException;
import java.util.Random;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.db.CoreDbTester;


public class CleanOrphanRowsInManualMeasuresTest {
    private static final String TABLE_MANUAL_MEASURES = "MANUAL_MEASURES";

    private static final String SCOPE_PROJECT = "PRJ";

    private static final String QUALIFIER_PROJECT = "TRK";

    private static final String QUALIFIER_VIEW = "VW";

    private static final String QUALIFIER_MODULE = "BRC";

    private static final String QUALIFIER_SUBVIEW = "SVW";

    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(CleanOrphanRowsInManualMeasuresTest.class, "manual_measures_and_projects.sql");

    private final Random random = new Random();

    private CleanOrphanRowsInManualMeasures underTest = new CleanOrphanRowsInManualMeasures(db.database());

    @Test
    public void execute_has_no_effect_if_MANUAL_MEASURES_is_empty() throws SQLException {
        underTest.execute();
    }

    @Test
    public void execute_deletes_all_rows_in_MANUAL_MEASURES_when_PROJECTS_is_empty() throws SQLException {
        insertManualMeasure(randomAlphabetic(5));
        insertManualMeasure(null);
        insertManualMeasure(randomAlphabetic(5));
        insertManualMeasure(null);
        underTest.execute();
        assertThat(db.countRowsOfTable(CleanOrphanRowsInManualMeasuresTest.TABLE_MANUAL_MEASURES)).isZero();
    }

    @Test
    public void execute_deletes_rows_without_component_uuid() throws SQLException {
        insertManualMeasure(null);
        insertManualMeasure(null);
        underTest.execute();
        assertThat(db.countRowsOfTable(CleanOrphanRowsInManualMeasuresTest.TABLE_MANUAL_MEASURES)).isZero();
    }

    @Test
    public void execute_deletes_rows_which_component_does_not_exist() throws SQLException {
        insertManualMeasure(randomAlphabetic(3));
        insertManualMeasure(randomAlphabetic(6));
        underTest.execute();
        assertThat(db.countRowsOfTable(CleanOrphanRowsInManualMeasuresTest.TABLE_MANUAL_MEASURES)).isZero();
    }

    @Test
    public void execute_deletes_rows_which_component_is_not_a_project_nor_a_view_nor_a_module_nor_a_subview() throws SQLException {
        Long[] validMeasureIds = new Long[]{ insertManualMeasure(insertComponent(CleanOrphanRowsInManualMeasuresTest.SCOPE_PROJECT, CleanOrphanRowsInManualMeasuresTest.QUALIFIER_PROJECT, true)), insertManualMeasure(insertComponent(CleanOrphanRowsInManualMeasuresTest.SCOPE_PROJECT, CleanOrphanRowsInManualMeasuresTest.QUALIFIER_VIEW, true)), insertManualMeasure(insertComponent(CleanOrphanRowsInManualMeasuresTest.SCOPE_PROJECT, CleanOrphanRowsInManualMeasuresTest.QUALIFIER_MODULE, true)), insertManualMeasure(insertComponent(CleanOrphanRowsInManualMeasuresTest.SCOPE_PROJECT, CleanOrphanRowsInManualMeasuresTest.QUALIFIER_SUBVIEW, true)) };
        insertManualMeasure(insertComponent(CleanOrphanRowsInManualMeasuresTest.SCOPE_PROJECT, CleanOrphanRowsInManualMeasuresTest.QUALIFIER_PROJECT, false));
        insertManualMeasure(insertComponent(CleanOrphanRowsInManualMeasuresTest.SCOPE_PROJECT, CleanOrphanRowsInManualMeasuresTest.QUALIFIER_VIEW, false));
        insertManualMeasure(insertComponent(CleanOrphanRowsInManualMeasuresTest.SCOPE_PROJECT, CleanOrphanRowsInManualMeasuresTest.QUALIFIER_MODULE, false));
        insertManualMeasure(insertComponent(CleanOrphanRowsInManualMeasuresTest.SCOPE_PROJECT, CleanOrphanRowsInManualMeasuresTest.QUALIFIER_SUBVIEW, false));
        String invalidScope = randomAlphabetic(3);
        insertManualMeasure(insertComponent(invalidScope, CleanOrphanRowsInManualMeasuresTest.QUALIFIER_PROJECT, true));
        insertManualMeasure(insertComponent(invalidScope, CleanOrphanRowsInManualMeasuresTest.QUALIFIER_VIEW, true));
        insertManualMeasure(insertComponent(invalidScope, CleanOrphanRowsInManualMeasuresTest.QUALIFIER_MODULE, true));
        insertManualMeasure(insertComponent(invalidScope, CleanOrphanRowsInManualMeasuresTest.QUALIFIER_SUBVIEW, true));
        insertManualMeasure(insertComponent(invalidScope, CleanOrphanRowsInManualMeasuresTest.QUALIFIER_PROJECT, false));
        insertManualMeasure(insertComponent(invalidScope, CleanOrphanRowsInManualMeasuresTest.QUALIFIER_VIEW, false));
        insertManualMeasure(insertComponent(invalidScope, CleanOrphanRowsInManualMeasuresTest.QUALIFIER_MODULE, false));
        insertManualMeasure(insertComponent(invalidScope, CleanOrphanRowsInManualMeasuresTest.QUALIFIER_SUBVIEW, false));
        insertManualMeasure(insertComponent(CleanOrphanRowsInManualMeasuresTest.SCOPE_PROJECT, "DIR", true));
        insertManualMeasure(insertComponent(CleanOrphanRowsInManualMeasuresTest.SCOPE_PROJECT, "FIL", true));
        insertManualMeasure(insertComponent(CleanOrphanRowsInManualMeasuresTest.SCOPE_PROJECT, "DIR", false));
        insertManualMeasure(insertComponent(CleanOrphanRowsInManualMeasuresTest.SCOPE_PROJECT, "FIL", false));
        insertManualMeasure(insertComponent("DIR", "DIR", true));
        insertManualMeasure(insertComponent("FIL", "FIL", true));
        insertManualMeasure(insertComponent("FIL", "TRK", true));
        insertManualMeasure(insertComponent("DIR", "DIR", false));
        insertManualMeasure(insertComponent("FIL", "FIL", false));
        insertManualMeasure(insertComponent("FIL", "TRK", false));
        insertManualMeasure(insertComponent(CleanOrphanRowsInManualMeasuresTest.SCOPE_PROJECT, randomAlphabetic(3), true));
        insertManualMeasure(insertComponent(CleanOrphanRowsInManualMeasuresTest.SCOPE_PROJECT, randomAlphabetic(3), true));
        insertManualMeasure(insertComponent(CleanOrphanRowsInManualMeasuresTest.SCOPE_PROJECT, randomAlphabetic(3), false));
        insertManualMeasure(insertComponent(CleanOrphanRowsInManualMeasuresTest.SCOPE_PROJECT, randomAlphabetic(3), false));
        underTest.execute();
        assertThat(db.select(("select id as \"ID\" from " + (CleanOrphanRowsInManualMeasuresTest.TABLE_MANUAL_MEASURES))).stream().map(( row) -> ((Long) (row.get("ID"))))).containsOnly(validMeasureIds);
    }
}

