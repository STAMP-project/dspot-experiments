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
import Qualifiers.VIEW;
import Scopes.DIRECTORY;
import Scopes.FILE;
import Scopes.PROJECT;
import com.google.common.collect.ImmutableList;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.db.CoreDbTester;


public class CleanUsurperRootComponentsTest {
    private static final List<String> TABLES = ImmutableList.of("duplications_index", "project_measures", "ce_activity", "events", "snapshots", "project_links", "project_measures", "issues", "file_sources", "group_roles", "user_roles", "properties", "widgets", "projects");

    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(CleanUsurperRootComponentsTest.class, "complete_schema.sql");

    private CleanUsurperRootComponents underTest = new CleanUsurperRootComponents(db.database());

    @Test
    public void migration_has_no_effect_on_empty_db() throws SQLException {
        underTest.execute();
        CleanUsurperRootComponentsTest.TABLES.forEach(( tableName) -> assertThat(db.countRowsOfTable(tableName)).isEqualTo(0));
    }

    @Test
    public void execute_fixes_scope_and_qualifier_of_snapshot_inconsistent_with_component() throws SQLException {
        String[] componentUuids = new String[]{ insertComponent("sc1", "qu1"), insertComponent("sc2", "qu2"), insertComponent("sc3", "qu3"), insertComponent("sc4", "qu4") };
        Long[] snapshotIds = new Long[]{ insertSnapshot(componentUuids[0], "sc1", "qu1"), insertSnapshot(componentUuids[1], "sc2", "quW"), insertSnapshot(componentUuids[2], "scX", "qu3"), insertSnapshot(componentUuids[3], "scY", "quZ") };
        underTest.execute();
        assertSnapshot(snapshotIds[0], "sc1", "qu1");
        assertSnapshot(snapshotIds[1], "sc2", "qu2");
        assertSnapshot(snapshotIds[2], "sc3", "qu3");
        assertSnapshot(snapshotIds[3], "sc4", "qu4");
    }

    @Test
    public void executes_deletes_usurper_root_components() throws SQLException {
        String[] componentUuids = new String[]{ insertRootComponent(PROJECT, Qualifiers.PROJECT), insertRootComponent(PROJECT, MODULE), insertRootComponent(DIRECTORY, Qualifiers.DIRECTORY), insertRootComponent(FILE, Qualifiers.FILE), insertRootComponent(PROJECT, VIEW), insertRootComponent(PROJECT, SUBVIEW), insertRootComponent(FILE, Qualifiers.PROJECT), insertRootComponent(PROJECT, "DEV"), insertRootComponent(PROJECT, "DEV_PRJ") };
        underTest.execute();
        assertUuidsInTableProjects("projects", componentUuids[0], componentUuids[4], componentUuids[7]);
    }

    @Test
    public void executes_deletes_data_in_all_children_tables_of_component_for_usurper_root_components() throws SQLException {
        long usurperId = 12L;
        String usurperUuid = "usurper_uuid";
        insertComponent(PROJECT, MODULE, usurperId, usurperUuid, usurperUuid);
        Long snapshotId = insertSnapshot(usurperUuid, usurperUuid);
        insertDuplicationsIndex(snapshotId);
        insertProjectMeasures(usurperUuid, dontCareLong());
        insertCeActivity(usurperUuid, dontCareLong());
        insertEvent(usurperUuid, dontCareLong());
        insertSnapshot(usurperUuid, dontCare());
        insertSnapshot(dontCare(), usurperUuid);
        insertProjectLinks(usurperUuid);
        insertIssue(usurperUuid, null);
        insertIssue(null, usurperUuid);
        insertIssue(usurperUuid, usurperUuid);
        insertFileSource(null, usurperUuid);
        insertFileSource(usurperUuid, null);
        insertFileSource(usurperUuid, usurperUuid);
        insertGroupRole(usurperId);
        insertUserRole(usurperId);
        insertProperties(usurperId);
        insertWidget(usurperId);
        CleanUsurperRootComponentsTest.TABLES.stream().forEach(( s) -> assertThat(db.countRowsOfTable(s)).describedAs(("table " + s)).isGreaterThanOrEqualTo(1));
        underTest.execute();
        CleanUsurperRootComponentsTest.TABLES.stream().forEach(( s) -> assertThat(db.countRowsOfTable(s)).describedAs(("table " + s)).isEqualTo(0));
    }

    @Test
    public void execute_deletes_snapshots_which_root_is_not_root() throws SQLException {
        String[] componentUuids = new String[]{ insertRootComponent(PROJECT, Qualifiers.PROJECT), insertComponent(PROJECT, MODULE), insertComponent(DIRECTORY, Qualifiers.DIRECTORY), insertComponent(FILE, Qualifiers.FILE), insertComponent(PROJECT, VIEW), insertComponent(PROJECT, SUBVIEW), insertComponent(FILE, Qualifiers.PROJECT), insertComponent(PROJECT, "DEV"), insertComponent(PROJECT, "DEV_PRJ") };
        Long[] snapshotIds = new Long[]{ insertSnapshot(dontCare(), componentUuids[0]), insertSnapshot(dontCare(), componentUuids[1]), insertSnapshot(dontCare(), componentUuids[2]), insertSnapshot(dontCare(), componentUuids[3]), insertSnapshot(dontCare(), componentUuids[4]), insertSnapshot(dontCare(), componentUuids[5]), insertSnapshot(dontCare(), componentUuids[6]), insertSnapshot(dontCare(), componentUuids[7]), insertSnapshot(dontCare(), componentUuids[8]) };
        underTest.execute();
        assertIdsInTableProjects("snapshots", snapshotIds[0], snapshotIds[4], snapshotIds[7]);
    }

    @Test
    public void execute_deletes_children_tables_of_snapshots_when_root_of_snapshot_is_not_root() throws SQLException {
        String componentUuid = insertComponent(FILE, FILE);
        Long snapshotId = insertSnapshot(dontCare(), componentUuid);
        insertProjectMeasures(dontCare(), snapshotId);
        insertCeActivity(componentUuid, snapshotId);
        insertEvent(componentUuid, snapshotId);
        underTest.execute();
        CleanUsurperRootComponentsTest.TABLES.stream().filter(( s1) -> !(s1.equals("projects"))).forEach(( s) -> assertThat(db.countRowsOfTable(s)).describedAs(("table " + s)).isEqualTo(0));
    }

    private long idGenerator = 0;

    private long dontCareGenerator = 0;
}

