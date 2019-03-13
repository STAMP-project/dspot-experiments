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


public class CleanOrphanRowsInProjectLinksTest {
    private static final String TABLE_PROJECT_LINKS = "project_links";

    private static final String SCOPE_PROJECT = "PRJ";

    private static final String QUALIFIER_VIEW = "VW";

    private static final String QUALIFIER_PROJECT = "TRK";

    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(CleanOrphanRowsInProjectLinksTest.class, "project_links_and_projects.sql");

    private final Random random = new Random();

    private CleanOrphanRowsInProjectLinks underTest = new CleanOrphanRowsInProjectLinks(db.database());

    @Test
    public void execute_has_no_effect_when_table_is_empty() throws SQLException {
        underTest.execute();
    }

    @Test
    public void execute_remove_all_data_from_project_links_if_projects_is_empty() throws SQLException {
        String componentUuid = randomAlphanumeric(6);
        insertProjectLink(componentUuid);
        underTest.execute();
        assertThat(db.countRowsOfTable(CleanOrphanRowsInProjectLinksTest.TABLE_PROJECT_LINKS)).isZero();
    }

    @Test
    public void execute_remove_row_from_project_links_which_component_is_not_an_enabled_project_or_view() throws SQLException {
        Long link1 = insertProjectLink(insertComponent(CleanOrphanRowsInProjectLinksTest.SCOPE_PROJECT, CleanOrphanRowsInProjectLinksTest.QUALIFIER_PROJECT, true));
        Long link2 = insertProjectLink(insertComponent(CleanOrphanRowsInProjectLinksTest.SCOPE_PROJECT, CleanOrphanRowsInProjectLinksTest.QUALIFIER_VIEW, true));
        insertProjectLink(insertComponent(CleanOrphanRowsInProjectLinksTest.SCOPE_PROJECT, CleanOrphanRowsInProjectLinksTest.QUALIFIER_PROJECT, false));
        insertProjectLink(insertComponent(CleanOrphanRowsInProjectLinksTest.SCOPE_PROJECT, CleanOrphanRowsInProjectLinksTest.QUALIFIER_VIEW, false));
        insertProjectLink(insertComponent(randomAlphabetic(3), CleanOrphanRowsInProjectLinksTest.QUALIFIER_PROJECT, true));
        insertProjectLink(insertComponent(randomAlphabetic(3), CleanOrphanRowsInProjectLinksTest.QUALIFIER_PROJECT, false));
        insertProjectLink(insertComponent(randomAlphabetic(3), CleanOrphanRowsInProjectLinksTest.QUALIFIER_VIEW, true));
        insertProjectLink(insertComponent(randomAlphabetic(3), CleanOrphanRowsInProjectLinksTest.QUALIFIER_VIEW, false));
        insertProjectLink(insertComponent(CleanOrphanRowsInProjectLinksTest.SCOPE_PROJECT, randomAlphabetic(3), true));
        insertProjectLink(insertComponent(CleanOrphanRowsInProjectLinksTest.SCOPE_PROJECT, randomAlphabetic(3), false));
        underTest.execute();
        assertThat(db.select("select id as \"ID\" from project_links").stream().map(( row) -> ((Long) (row.get("ID"))))).containsOnly(link1, link2);
    }

    @Test
    public void execute_removes_row_from_project_links_which_component_does_not_exist() throws SQLException {
        String projectUuid = insertComponent(CleanOrphanRowsInProjectLinksTest.SCOPE_PROJECT, CleanOrphanRowsInProjectLinksTest.QUALIFIER_PROJECT, true);
        insertProjectLink((projectUuid + "_2"));
        long linkId = insertProjectLink(projectUuid);
        underTest.execute();
        assertThat(db.select("select id as \"ID\" from project_links").stream().map(( row) -> ((Long) (row.get("ID"))))).containsOnly(linkId);
    }
}

