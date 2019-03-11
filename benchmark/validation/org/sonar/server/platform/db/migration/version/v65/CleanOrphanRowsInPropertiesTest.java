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


public class CleanOrphanRowsInPropertiesTest {
    private static final String TABLE_PROPERTIES = "PROPERTIES";

    private static final String SCOPE_PROJECT = "PRJ";

    private static final String QUALIFIER_PROJECT = "TRK";

    private static final String QUALIFIER_VIEW = "VW";

    private static final String QUALIFIER_MODULE = "BRC";

    private static final String QUALIFIER_SUBVIEW = "SVW";

    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(CleanOrphanRowsInPropertiesTest.class, "properties_and_projects.sql");

    private final Random random = new Random();

    private CleanOrphanRowsInProperties underTest = new CleanOrphanRowsInProperties(db.database());

    @Test
    public void execute_has_no_effect_is_PROPERTIES_is_empty() throws SQLException {
        underTest.execute();
    }

    @Test
    public void execute_deletes_all_properties_with_resource_id_if_PROJECTS_is_empty() throws SQLException {
        insertProperty(random.nextInt());
        insertProperty(random.nextInt());
        insertProperty(random.nextInt());
        underTest.execute();
        assertThat(db.countRowsOfTable(CleanOrphanRowsInPropertiesTest.TABLE_PROPERTIES)).isZero();
    }

    @Test
    public void execute_does_not_delete_properties_without_resource_id_if_PROJECTS_is_empty() throws SQLException {
        long propId1 = insertProperty(null);
        long propId2 = insertProperty(null);
        underTest.execute();
        assertThat(db.select("select id as \"ID\" from properties").stream().map(( row) -> ((Long) (row.get("ID"))))).containsOnly(propId1, propId2);
    }

    @Test
    public void execute_deletes_properties_which_component_does_not_exist() throws SQLException {
        Long propId1 = insertProperty(insertComponent(CleanOrphanRowsInPropertiesTest.SCOPE_PROJECT, CleanOrphanRowsInPropertiesTest.QUALIFIER_PROJECT, true));
        insertProperty(random.nextInt());
        underTest.execute();
        assertThat(db.select("select id as \"ID\" from properties").stream().map(( row) -> ((Long) (row.get("ID"))))).containsOnly(propId1);
    }

    @Test
    public void execute_deletes_properties_which_component_is_neither_project_nor_view_nor_module_nor_subview_not_enabled() throws SQLException {
        Long[] validPropIds = new Long[]{ insertProperty(insertComponent(CleanOrphanRowsInPropertiesTest.SCOPE_PROJECT, CleanOrphanRowsInPropertiesTest.QUALIFIER_PROJECT, true)), insertProperty(insertComponent(CleanOrphanRowsInPropertiesTest.SCOPE_PROJECT, CleanOrphanRowsInPropertiesTest.QUALIFIER_VIEW, true)), insertProperty(insertComponent(CleanOrphanRowsInPropertiesTest.SCOPE_PROJECT, CleanOrphanRowsInPropertiesTest.QUALIFIER_MODULE, true)), insertProperty(insertComponent(CleanOrphanRowsInPropertiesTest.SCOPE_PROJECT, CleanOrphanRowsInPropertiesTest.QUALIFIER_SUBVIEW, true)) };
        insertProperty(insertComponent(CleanOrphanRowsInPropertiesTest.SCOPE_PROJECT, CleanOrphanRowsInPropertiesTest.QUALIFIER_PROJECT, false));
        insertProperty(insertComponent(CleanOrphanRowsInPropertiesTest.SCOPE_PROJECT, CleanOrphanRowsInPropertiesTest.QUALIFIER_VIEW, false));
        insertProperty(insertComponent(CleanOrphanRowsInPropertiesTest.SCOPE_PROJECT, CleanOrphanRowsInPropertiesTest.QUALIFIER_MODULE, false));
        insertProperty(insertComponent(CleanOrphanRowsInPropertiesTest.SCOPE_PROJECT, CleanOrphanRowsInPropertiesTest.QUALIFIER_SUBVIEW, false));
        String notProjectScope = randomAlphabetic(3);
        insertProperty(insertComponent(notProjectScope, CleanOrphanRowsInPropertiesTest.QUALIFIER_PROJECT, true));
        insertProperty(insertComponent(notProjectScope, CleanOrphanRowsInPropertiesTest.QUALIFIER_VIEW, true));
        insertProperty(insertComponent(notProjectScope, CleanOrphanRowsInPropertiesTest.QUALIFIER_MODULE, true));
        insertProperty(insertComponent(notProjectScope, CleanOrphanRowsInPropertiesTest.QUALIFIER_SUBVIEW, true));
        insertProperty(insertComponent(notProjectScope, CleanOrphanRowsInPropertiesTest.QUALIFIER_PROJECT, false));
        insertProperty(insertComponent(notProjectScope, CleanOrphanRowsInPropertiesTest.QUALIFIER_VIEW, false));
        insertProperty(insertComponent(notProjectScope, CleanOrphanRowsInPropertiesTest.QUALIFIER_MODULE, false));
        insertProperty(insertComponent(notProjectScope, CleanOrphanRowsInPropertiesTest.QUALIFIER_SUBVIEW, false));
        insertProperty(insertComponent(CleanOrphanRowsInPropertiesTest.SCOPE_PROJECT, "DIR", true));
        insertProperty(insertComponent(CleanOrphanRowsInPropertiesTest.SCOPE_PROJECT, "FIL", true));
        insertProperty(insertComponent(CleanOrphanRowsInPropertiesTest.SCOPE_PROJECT, randomAlphabetic(3), true));
        underTest.execute();
        assertThat(db.select("select id as \"ID\" from properties").stream().map(( row) -> ((Long) (row.get("ID"))))).containsOnly(validPropIds);
    }
}

