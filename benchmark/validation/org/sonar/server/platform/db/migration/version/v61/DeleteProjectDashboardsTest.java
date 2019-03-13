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
package org.sonar.server.platform.db.migration.version.v61;


import java.sql.SQLException;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.db.CoreDbTester;


public class DeleteProjectDashboardsTest {
    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(DeleteProjectDashboardsTest.class, "schema.sql");

    private DeleteProjectDashboards underTest = new DeleteProjectDashboards(db.database());

    @Test
    public void no_effect_on_empty_tables() throws SQLException {
        underTest.execute();
        assertThat(db.countRowsOfTable("dashboards")).isEqualTo(0);
    }

    @Test
    public void delete_project_dashboard_data() throws SQLException {
        insertGlobalDashboards(1L, 10L, 11L);
        insertProjectDashboards(2L, 20L, 21L);
        insertActiveDashboards(1L, 10L, 11L, 12L);
        insertActiveDashboards(2L, 20L, 21L, 22L);
        insertWidgets(1L, 100L, 101L, 102L);
        insertWidgets(2L, 200L, 201L, 202L);
        insertWidgetProperties(100L, 1001L, 1002L, 1003L);
        insertWidgetProperties(202L, 2021L, 2022L, 2023L);
        underTest.execute();
        assertIdsOfDashboardsAre(1L, 10L, 11L);
        assertIdsOfActiveDashboardsAre(10L, 11L, 12L);
        assertIdsOfWidgetsAre(100L, 101L, 102L);
        assertIdsOfWidgetPropertiesAre(1001L, 1002L, 1003L);
    }

    @Test
    public void is_reentrant() throws SQLException {
        insertGlobalDashboards(10L, 11L, 12L);
        insertProjectDashboards(20L, 21L, 22L);
        underTest.execute();
        assertIdsOfDashboardsAre(10L, 11L, 12L);
        underTest.execute();
        assertIdsOfDashboardsAre(10L, 11L, 12L);
    }
}

