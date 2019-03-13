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
public class PopulateTmpColumnsToCeQueueTest {
    private static final Map<String, String> NO_CHARACTERISTICS = Collections.emptyMap();

    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(PopulateTmpColumnsToCeQueueTest.class, "ce_queue.sql");

    private PopulateTmpColumnsToCeQueue underTest = new PopulateTmpColumnsToCeQueue(db.database());

    @Test
    public void no_action_on_empty_table() throws SQLException {
        underTest.execute();
        assertThat(db.countRowsOfTable("ce_queue")).isZero();
    }

    @Test
    public void execute_populates_tmp_columns_with_component_uuid_for_existing_main_branch() throws SQLException {
        String mainComponentUuid = randomAlphabetic(2);
        insertProjects(mainComponentUuid, randomAlphabetic(3));
        String taskUuid = insertCeQueue(new Row(newUuid(), mainComponentUuid, null, null), PopulateTmpColumnsToCeQueueTest.NO_CHARACTERISTICS);
        underTest.execute();
        assertThat(rowsInCeQueue()).containsOnly(new Row(taskUuid, mainComponentUuid, mainComponentUuid, mainComponentUuid));
    }

    @Test
    public void execute_deletes_tasks_of_branches_without_row_in_PROJECTS_and_populates_others_matching_row_in_PROJECTS_by_KEE() throws SQLException {
        String mainComponentUuid = randomAlphabetic(2);
        String mainComponentKey = randomAlphabetic(3);
        String branchUuid = randomAlphabetic(4);
        String branchType1 = randomAlphabetic(5);
        String branchName1 = randomAlphabetic(6);
        String branchType2 = randomAlphabetic(7);
        String branchName2 = randomAlphabetic(8);
        insertProjects(mainComponentUuid, mainComponentKey);
        insertProjects(branchUuid, ((mainComponentKey + ":BRANCH:") + branchName2));
        String deletedTaskUuid = insertCeQueue(new Row(newUuid(), mainComponentUuid, null, null), PopulateTmpColumnsToCeQueueTest.branchCharacteristics(branchType1, branchName1));
        String updatedTaskUuid = insertCeQueue(new Row(newUuid(), mainComponentUuid, null, null), PopulateTmpColumnsToCeQueueTest.branchCharacteristics(branchType2, branchName2));
        underTest.execute();
        assertThat(rowsInCeQueue()).containsOnly(new Row(updatedTaskUuid, mainComponentUuid, branchUuid, mainComponentUuid));
    }

    @Test
    public void execute_deletes_tasks_of_prs_without_row_in_PROJECTS_and_populates_others_matching_row_in_PROJECTS_by_KEE() throws SQLException {
        String mainComponentUuid = randomAlphabetic(2);
        String mainComponentKey = randomAlphabetic(3);
        String prUuid = randomAlphabetic(4);
        String prName1 = randomAlphabetic(6);
        String prName2 = randomAlphabetic(8);
        insertProjects(mainComponentUuid, mainComponentKey);
        insertProjects(prUuid, ((mainComponentKey + ":PULL_REQUEST:") + prName2));
        String deletedTaskUuid = insertCeQueue(new Row(newUuid(), mainComponentUuid, null, null), PopulateTmpColumnsToCeQueueTest.prCharacteristics(prName1));
        String updatedTaskUuid = insertCeQueue(new Row(newUuid(), mainComponentUuid, null, null), PopulateTmpColumnsToCeQueueTest.prCharacteristics(prName2));
        underTest.execute();
        assertThat(rowsInCeQueue()).containsOnly(new Row(updatedTaskUuid, mainComponentUuid, prUuid, mainComponentUuid));
    }

    private int uuidGenerator = new Random().nextInt(9000);
}

