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
package org.sonar.server.platform.db.migration.version.v74;


import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;
import java.util.Random;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sonar.db.CoreDbTester;


@RunWith(DataProviderRunner.class)
public class PopulateTmpColumnsToCeActivityTest {
    private static final Map<String, String> NO_CHARACTERISTICS = Collections.emptyMap();

    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(PopulateTmpColumnsToCeActivityTest.class, "ce_activity.sql");

    private PopulateTmpColumnsToCeActivity underTest = new PopulateTmpColumnsToCeActivity(db.database());

    @Test
    public void execute_has_no_effect_on_empty_table() throws SQLException {
        underTest.execute();
        assertThat(rowsInCeActivity()).isEmpty();
    }

    @Test
    public void execute_populates_tmp_columns_with_component_uuid_for_existing_main_branch() throws SQLException {
        String mainComponentUuid = randomAlphabetic(2);
        insertProjects(mainComponentUuid, randomAlphabetic(3));
        String taskUuid = insertCeActivity(new Row(newUuid(), mainComponentUuid, null, null), PopulateTmpColumnsToCeActivityTest.NO_CHARACTERISTICS);
        underTest.execute();
        assertThat(rowsInCeActivity()).containsOnly(new Row(taskUuid, mainComponentUuid, mainComponentUuid, mainComponentUuid));
    }

    @Test
    public void execute_deletes_populates_branches_of_task_without_row_in_PROJECTS_with_COMPONENT_UUID_and_those_with_row_in_PROJECTS_by_KEE() throws SQLException {
        String mainComponentUuid = randomAlphabetic(2);
        String mainComponentKey = randomAlphabetic(3);
        String branchUuid = randomAlphabetic(4);
        String branchType1 = randomAlphabetic(5);
        String branchName1 = randomAlphabetic(6);
        String branchType2 = randomAlphabetic(7);
        String branchName2 = randomAlphabetic(8);
        insertProjects(mainComponentUuid, mainComponentKey);
        insertProjects(branchUuid, ((mainComponentKey + ":BRANCH:") + branchName2));
        String orphanTaskUuid = insertCeActivity(new Row(newUuid(), mainComponentUuid, null, null), PopulateTmpColumnsToCeActivityTest.branchCharacteristics(branchType1, branchName1));
        String regularTaskUuid = insertCeActivity(new Row(newUuid(), mainComponentUuid, null, null), PopulateTmpColumnsToCeActivityTest.branchCharacteristics(branchType2, branchName2));
        underTest.execute();
        assertThat(rowsInCeActivity()).containsOnly(new Row(orphanTaskUuid, mainComponentUuid, mainComponentUuid, mainComponentUuid), new Row(regularTaskUuid, mainComponentUuid, branchUuid, mainComponentUuid));
    }

    @Test
    public void execute_deletes_populates_prs_of_task_without_row_in_PROJECTS_with_COMPONENT_UUID_and_those_with_row_in_PROJECTS_by_KEE() throws SQLException {
        String mainComponentUuid = randomAlphabetic(2);
        String mainComponentKey = randomAlphabetic(3);
        String prUuid = randomAlphabetic(4);
        String prName1 = randomAlphabetic(6);
        String prName2 = randomAlphabetic(8);
        insertProjects(mainComponentUuid, mainComponentKey);
        insertProjects(prUuid, ((mainComponentKey + ":PULL_REQUEST:") + prName2));
        String orphanTaskUuid = insertCeActivity(new Row(newUuid(), mainComponentUuid, null, null), PopulateTmpColumnsToCeActivityTest.prCharacteristics(prName1));
        String regularTaskUuid = insertCeActivity(new Row(newUuid(), mainComponentUuid, null, null), PopulateTmpColumnsToCeActivityTest.prCharacteristics(prName2));
        underTest.execute();
        assertThat(rowsInCeActivity()).containsOnly(new Row(orphanTaskUuid, mainComponentUuid, mainComponentUuid, mainComponentUuid), new Row(regularTaskUuid, mainComponentUuid, prUuid, mainComponentUuid));
    }

    private int uuidGenerator = new Random().nextInt(9000);
}

