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
package org.sonar.ce.queue;


import CeQueueDto.Status.IN_PROGRESS;
import CeQueueDto.Status.PENDING;
import CeTaskInputDao.DataStream;
import ProcessProperties.Property.BLUE_GREEN_ENABLED;
import System2.INSTANCE;
import java.util.Optional;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.api.platform.ServerUpgradeStatus;
import org.sonar.db.DbTester;
import org.sonar.db.ce.CeTaskInputDao;


public class CeQueueCleanerTest {
    @Rule
    public DbTester dbTester = DbTester.create(INSTANCE);

    private ServerUpgradeStatus serverUpgradeStatus = Mockito.mock(ServerUpgradeStatus.class);

    private InternalCeQueue queue = Mockito.mock(InternalCeQueue.class);

    private MapSettings settings = new MapSettings();

    @Test
    public void start_does_not_reset_in_progress_tasks_to_pending() {
        insertInQueue("TASK_1", PENDING);
        insertInQueue("TASK_2", IN_PROGRESS);
        runCleaner();
        assertThat(dbTester.getDbClient().ceQueueDao().countByStatus(dbTester.getSession(), PENDING)).isEqualTo(1);
        assertThat(dbTester.getDbClient().ceQueueDao().countByStatus(dbTester.getSession(), IN_PROGRESS)).isEqualTo(1);
    }

    @Test
    public void start_clears_queue_if_version_upgrade() {
        Mockito.when(serverUpgradeStatus.isUpgraded()).thenReturn(true);
        runCleaner();
        Mockito.verify(queue).clear();
    }

    @Test
    public void start_does_not_clear_queue_if_version_upgrade_but_blue_green_deployment() {
        Mockito.when(serverUpgradeStatus.isUpgraded()).thenReturn(true);
        settings.setProperty(BLUE_GREEN_ENABLED.getKey(), true);
        runCleaner();
        Mockito.verify(queue, Mockito.never()).clear();
    }

    @Test
    public void start_deletes_orphan_report_files() {
        // analysis reports are persisted but the associated
        // task is not in the queue
        insertInQueue("TASK_1", PENDING);
        insertTaskData("TASK_1");
        insertTaskData("TASK_2");
        runCleaner();
        CeTaskInputDao dataDao = dbTester.getDbClient().ceTaskInputDao();
        Optional<CeTaskInputDao.DataStream> task1Data = dataDao.selectData(dbTester.getSession(), "TASK_1");
        assertThat(task1Data).isPresent();
        task1Data.get().close();
        assertThat(dataDao.selectData(dbTester.getSession(), "TASK_2")).isNotPresent();
    }
}

