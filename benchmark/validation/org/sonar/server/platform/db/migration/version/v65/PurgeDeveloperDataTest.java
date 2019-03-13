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


public class PurgeDeveloperDataTest {
    private static final String TABLE_PROJECT_MEASURE = "PROJECT_MEASURES";

    private static final String TABLE_CE_ACTIVITY = "CE_ACTIVITY";

    private static final String TABLE_SNAPSHOTS = "SNAPSHOTS";

    private static final String TABLE_GROUP_ROLES = "GROUP_ROLES";

    private static final String TABLE_USER_ROLES = "USER_ROLES";

    private static final String SCOPE_PROJECT = "PRJ";

    private static final String QUALIFIER_DEVELOPER = "DEV";

    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(PurgeDeveloperDataTest.class, "projects_and_child_tables.sql");

    private final Random random = new Random();

    private PurgeDeveloperData underTest = new PurgeDeveloperData(db.database());

    @Test
    public void execute_has_no_effect_when_table_PROJECTS_is_empty() throws SQLException {
        insertProjectMeasure(randomAlphabetic(5), randomAlphabetic(5));
        insertCeActivity(randomAlphabetic(3));
        insertSnapshot(randomAlphabetic(3));
        insertGroupRole(random.nextInt());
        insertUserRole(random.nextInt());
        underTest.execute();
        assertThat(db.countRowsOfTable(PurgeDeveloperDataTest.TABLE_PROJECT_MEASURE)).isEqualTo(1);
        assertThat(db.countRowsOfTable(PurgeDeveloperDataTest.TABLE_CE_ACTIVITY)).isEqualTo(1);
        assertThat(db.countRowsOfTable(PurgeDeveloperDataTest.TABLE_SNAPSHOTS)).isEqualTo(1);
        assertThat(db.countRowsOfTable(PurgeDeveloperDataTest.TABLE_GROUP_ROLES)).isEqualTo(1);
        assertThat(db.countRowsOfTable(PurgeDeveloperDataTest.TABLE_USER_ROLES)).isEqualTo(1);
    }

    @Test
    public void execute_deletes_developer_and_children_of_a_developer_ignoring_scope_and_qualifier() throws SQLException {
        String devUuid = insertComponent(PurgeDeveloperDataTest.SCOPE_PROJECT, PurgeDeveloperDataTest.QUALIFIER_DEVELOPER, null);
        insertComponent(randomAlphabetic(3), randomAlphabetic(3), devUuid);
        insertComponent(randomAlphabetic(3), randomAlphabetic(3), devUuid);
        String notADevChild = insertComponent(randomAlphabetic(3), randomAlphabetic(3), null);
        String notADev = insertComponent(PurgeDeveloperDataTest.SCOPE_PROJECT, randomAlphabetic(3), null);
        underTest.execute();
        assertThat(db.select("select uuid as \"UUID\" from projects").stream().map(( row) -> row.get("UUID"))).containsOnly(notADev, notADevChild);
    }

    @Test
    public void execute_deletes_PROJECT_MEASURE_of_developer() throws SQLException {
        String devUuid = insertComponent(PurgeDeveloperDataTest.SCOPE_PROJECT, PurgeDeveloperDataTest.QUALIFIER_DEVELOPER, null);
        insertProjectMeasure(devUuid, randomAlphabetic(3));
        underTest.execute();
        assertThat(db.countRowsOfTable(PurgeDeveloperDataTest.TABLE_PROJECT_MEASURE)).isZero();
    }

    @Test
    public void execute_deletes_CE_ACTIVITY_of_developer() throws SQLException {
        String devUuid = insertComponent(PurgeDeveloperDataTest.SCOPE_PROJECT, PurgeDeveloperDataTest.QUALIFIER_DEVELOPER, null);
        insertCeActivity(devUuid);
        underTest.execute();
        assertThat(db.countRowsOfTable(PurgeDeveloperDataTest.TABLE_CE_ACTIVITY)).isZero();
    }

    @Test
    public void execute_deletes_SNAPSHOT_of_developer() throws SQLException {
        String devUuid = insertComponent(PurgeDeveloperDataTest.SCOPE_PROJECT, PurgeDeveloperDataTest.QUALIFIER_DEVELOPER, null);
        insertSnapshot(devUuid);
        underTest.execute();
        assertThat(db.countRowsOfTable(PurgeDeveloperDataTest.TABLE_SNAPSHOTS)).isZero();
    }

    @Test
    public void execute_deletes_roles_of_developer() throws SQLException {
        String devUuid = insertComponent(PurgeDeveloperDataTest.SCOPE_PROJECT, PurgeDeveloperDataTest.QUALIFIER_DEVELOPER, null);
        long devId = idOfComponent(devUuid);
        insertUserRole(devId);
        insertGroupRole(devId);
        underTest.execute();
        assertThat(db.countRowsOfTable(PurgeDeveloperDataTest.TABLE_GROUP_ROLES)).isZero();
        assertThat(db.countRowsOfTable(PurgeDeveloperDataTest.TABLE_USER_ROLES)).isZero();
    }

    @Test
    public void execute_deletes_PROJECT_MEASURE_of_children_of_developer() throws SQLException {
        String devUuid = insertComponent(PurgeDeveloperDataTest.SCOPE_PROJECT, PurgeDeveloperDataTest.QUALIFIER_DEVELOPER, null);
        String childUuid = insertComponent(randomAlphabetic(3), randomAlphabetic(3), devUuid);
        insertProjectMeasure(childUuid, randomAlphabetic(3));
        underTest.execute();
        assertThat(db.countRowsOfTable(PurgeDeveloperDataTest.TABLE_PROJECT_MEASURE)).isEqualTo(0);
    }

    @Test
    public void execute_does_not_delete_CE_ACTIVITY_of_children_of_developer() throws SQLException {
        String devUuid = insertComponent(PurgeDeveloperDataTest.SCOPE_PROJECT, PurgeDeveloperDataTest.QUALIFIER_DEVELOPER, null);
        String childUuid = insertComponent(randomAlphabetic(3), randomAlphabetic(3), devUuid);
        insertCeActivity(childUuid);
        underTest.execute();
        assertThat(db.countRowsOfTable(PurgeDeveloperDataTest.TABLE_CE_ACTIVITY)).isEqualTo(1);
    }

    @Test
    public void execute_does_not_delete_SNAPSHOT_of_children_of_developer() throws SQLException {
        String devUuid = insertComponent(PurgeDeveloperDataTest.SCOPE_PROJECT, PurgeDeveloperDataTest.QUALIFIER_DEVELOPER, null);
        String childUuid = insertComponent(randomAlphabetic(3), randomAlphabetic(3), devUuid);
        insertSnapshot(childUuid);
        underTest.execute();
        assertThat(db.countRowsOfTable(PurgeDeveloperDataTest.TABLE_SNAPSHOTS)).isEqualTo(1);
    }

    @Test
    public void execute_does_not_delete_roles_of_children_of_developer() throws SQLException {
        String devUuid = insertComponent(PurgeDeveloperDataTest.SCOPE_PROJECT, PurgeDeveloperDataTest.QUALIFIER_DEVELOPER, null);
        String childUuid = insertComponent(randomAlphabetic(3), randomAlphabetic(3), devUuid);
        long childId = idOfComponent(childUuid);
        insertUserRole(childId);
        insertGroupRole(childId);
        underTest.execute();
        assertThat(db.countRowsOfTable(PurgeDeveloperDataTest.TABLE_GROUP_ROLES)).isEqualTo(1);
        assertThat(db.countRowsOfTable(PurgeDeveloperDataTest.TABLE_USER_ROLES)).isEqualTo(1);
    }
}

