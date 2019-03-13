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
package org.sonar.server.platform.db.migration.version.v64;


import java.sql.SQLException;
import java.util.Random;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.core.util.stream.MoreCollectors;
import org.sonar.db.CoreDbTester;


public class PurgeComponentsWithoutProjectUuidTest {
    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(PurgeComponentsWithoutProjectUuidTest.class, "projects_and_children_tables.sql");

    private Random random = new Random();

    private PurgeComponentsWithoutProjectUuid underTest = new PurgeComponentsWithoutProjectUuid(db.database());

    @Test
    public void execute_has_no_effect_when_every_tables_are_empty() throws SQLException {
        underTest.execute();
    }

    @Test
    public void execute_deletes_rows_from_PROJECTS_where_project_uuid_is_null() throws SQLException {
        insertComponent("u1", "u1");
        insertComponent("u2", "u1");
        insertComponent("u3", null);
        underTest.execute();
        assertThat(db.select("select uuid from projects").stream().flatMap(( map) -> map.entrySet().stream()).map(( entry) -> ((String) (entry.getValue()))).collect(MoreCollectors.toList())).containsOnly("u1", "u2");
    }

    @Test
    public void executes_deletes_from_child_table_DUPLICATIONS_INDEX() throws SQLException {
        insertComponent("u1", "foo");
        insertComponent("u2", null);
        Stream.of("u1", "u2").forEach(( componentUuid) -> {
            String snapshotUuid = "s" + componentUuid;
            insertSnapshot(snapshotUuid, componentUuid);
            insertDuplicationsIndex(snapshotUuid, componentUuid);
            insertDuplicationsIndex(snapshotUuid, ("other_component" + (random.nextInt(30))));
        });
        underTest.execute();
        assertThat(db.countSql(("select count(1) from duplications_index where analysis_uuid = '" + ("su1" + "'")))).isEqualTo(2);
        assertThat(db.countSql(("select count(1) from duplications_index where analysis_uuid = '" + ("su2" + "'")))).isEqualTo(0);
    }

    @Test
    public void execute_deletes_from_child_table_PROJECT_MEASURES() throws SQLException {
        insertComponent("u1", "foo");
        insertComponent("u2", null);
        Stream.of("u1", "u2").forEach(( componentUuid) -> {
            String snapshotUuid1 = "s1" + componentUuid;
            insertSnapshot(snapshotUuid1, componentUuid);
            insertProjectMeasure(snapshotUuid1, componentUuid);
            String snapshotUuid2 = "s2" + componentUuid;
            insertSnapshot(snapshotUuid2, ("other_component" + (random.nextInt(30))));
            insertProjectMeasure(snapshotUuid2, componentUuid);
        });
        underTest.execute();
        assertThat(db.countSql(("select count(1) from project_measures where component_uuid = '" + ("u1" + "'")))).isEqualTo(2);
        assertThat(db.countSql(("select count(1) from project_measures where component_uuid = '" + ("u2" + "'")))).isEqualTo(0);
    }

    @Test
    public void execute_deletes_from_child_table_CE_ACTIVITY() throws SQLException {
        insertComponent("u1", "foo");
        insertComponent("u2", null);
        Stream.of("u1", "u2").forEach(( componentUuid) -> {
            insertCeActivity(("ce1" + componentUuid), componentUuid);
        });
        insertCeActivity("ce2", null);
        underTest.execute();
        assertThat(db.countSql(("select count(1) from ce_activity where component_uuid = '" + ("u1" + "'")))).isEqualTo(1);
        assertThat(db.countSql(("select count(1) from ce_activity where component_uuid = '" + ("u2" + "'")))).isEqualTo(0);
        assertThat(db.countSql("select count(1) from ce_activity where component_uuid is null")).isEqualTo(1);
    }

    @Test
    public void execute_deletes_from_child_table_EVENTS() throws SQLException {
        insertComponent("u1", "foo");
        insertComponent("u2", null);
        String snapshotOfOtherComponent = "bar";
        insertSnapshot(snapshotOfOtherComponent, "foo");
        Stream.of("u1", "u2").forEach(( componentUuid) -> {
            String snapshot1 = "s1" + componentUuid;
            insertSnapshot(snapshot1, componentUuid);
            insertEvent(("e1" + componentUuid), snapshot1, null);
            insertEvent(("e2" + componentUuid), snapshot1, componentUuid);
            insertEvent(("e3" + componentUuid), snapshotOfOtherComponent, componentUuid);
        });
        underTest.execute();
        assertThat(db.countSql(("select count(1) from events where component_uuid = '" + ("u1" + "'")))).isEqualTo(2);
        assertThat(db.countSql(("select count(1) from events where analysis_uuid = '" + ("s1u1" + "'")))).isEqualTo(2);
        assertThat(db.countSql(("select count(1) from events where component_uuid = '" + ("u2" + "'")))).isEqualTo(0);
        assertThat(db.countSql(("select count(1) from events where analysis_uuid = '" + ("s1u2" + "'")))).isEqualTo(0);
        assertThat(db.countSql((("select count(1) from events where analysis_uuid = '" + snapshotOfOtherComponent) + "'"))).isEqualTo(1);
    }

    @Test
    public void execute_deletes_from_child_table_PROJECT_LINKS() throws SQLException {
        insertComponent("u1", "foo");
        insertComponent("u2", null);
        Stream.of("u1", "u2").forEach(( componentUuid) -> {
            insertProjectLink(componentUuid);
        });
        insertProjectLink(null);
        underTest.execute();
        assertThat(db.countSql(("select count(1) from project_links where component_uuid = '" + ("u1" + "'")))).isEqualTo(1);
        assertThat(db.countSql(("select count(1) from project_links where component_uuid = '" + ("u2" + "'")))).isEqualTo(0);
        assertThat(db.countSql("select count(1) from project_links where component_uuid is null")).isEqualTo(1);
    }

    @Test
    public void execute_deletes_from_child_table_SNAPSHOTS() throws SQLException {
        insertComponent("u1", "foo");
        insertComponent("u2", null);
        Stream.of("u1", "u2").forEach(( componentUuid) -> {
            insertSnapshot(("s1" + componentUuid), componentUuid);
        });
        underTest.execute();
        assertThat(db.countSql(("select count(1) from snapshots where component_uuid = '" + ("u1" + "'")))).isEqualTo(1);
        assertThat(db.countSql(("select count(1) from snapshots where component_uuid = '" + ("u2" + "'")))).isEqualTo(0);
    }

    @Test
    public void execute_deletes_from_child_table_ISSUES() throws SQLException {
        insertComponent("u1", "foo");
        insertComponent("u2", null);
        Stream.of("u1", "u2").forEach(( componentUuid) -> {
            insertIssue(("s1" + componentUuid), componentUuid);
        });
        insertIssue("s2", null);
        underTest.execute();
        assertThat(db.countSql(("select count(1) from issues where component_uuid = '" + ("u1" + "'")))).isEqualTo(1);
        assertThat(db.countSql(("select count(1) from issues where component_uuid = '" + ("u2" + "'")))).isEqualTo(0);
        assertThat(db.countSql("select count(1) from issues where component_uuid is null")).isEqualTo(1);
    }

    @Test
    public void execute_deletes_from_child_table_FILE_SOURCES() throws SQLException {
        insertComponent("u1", "foo");
        insertComponent("u2", null);
        Stream.of("u1", "u2").forEach(( componentUuid) -> {
            insertFileSource(componentUuid, "foo");
            insertFileSource("bar", componentUuid);
            insertFileSource(componentUuid, componentUuid);
        });
        underTest.execute();
        assertThat(db.countSql(("select count(1) from file_sources where project_uuid = '" + ((("u1" + "' or file_uuid = '") + "u1") + "'")))).isEqualTo(3);
        assertThat(db.countSql(("select count(1) from file_sources where project_uuid = '" + ((("u2" + "' or file_uuid = '") + "u2") + "'")))).isEqualTo(0);
    }

    @Test
    public void execute_deletes_from_child_table_GROUP_ROLES() throws SQLException {
        long project1 = insertComponent("u1", "foo");
        long project2 = insertComponent("u2", null);
        Stream.of(project1, project2).forEach(( componentId) -> {
            insertGroupRole(componentId);
        });
        insertGroupRole(null);
        underTest.execute();
        assertThat(db.countSql(("select count(1) from group_roles where RESOURCE_ID = " + project1))).isEqualTo(1);
        assertThat(db.countSql(("select count(1) from group_roles where RESOURCE_ID = " + project2))).isEqualTo(0);
        assertThat(db.countSql("select count(1) from group_roles where RESOURCE_ID is null")).isEqualTo(1);
    }

    @Test
    public void execute_deletes_from_child_table_USER_ROLES() throws SQLException {
        long project1 = insertComponent("u1", "foo");
        long project2 = insertComponent("u2", null);
        Stream.of(project1, project2).forEach(( componentId) -> {
            insertUserRole(componentId);
        });
        insertUserRole(null);
        underTest.execute();
        assertThat(db.countSql(("select count(1) from user_roles where resource_id = " + project1))).isEqualTo(1);
        assertThat(db.countSql(("select count(1) from user_roles where resource_id = " + project2))).isEqualTo(0);
        assertThat(db.countSql("select count(1) from user_roles where resource_id is null")).isEqualTo(1);
    }

    @Test
    public void execute_deletes_from_child_table_PROPERTIES() throws SQLException {
        long project1 = insertComponent("u1", "foo");
        long project2 = insertComponent("u2", null);
        Stream.of(project1, project2).forEach(( componentId) -> {
            insertProperties(componentId);
        });
        insertProperties(null);
        underTest.execute();
        assertThat(db.countSql(("select count(1) from properties where resource_id = " + project1))).isEqualTo(1);
        assertThat(db.countSql(("select count(1) from properties where resource_id = " + project2))).isEqualTo(0);
        assertThat(db.countSql("select count(1) from properties where resource_id is null")).isEqualTo(1);
    }
}

