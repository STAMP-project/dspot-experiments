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
package org.sonar.server.platform.monitoring.cluster;


import CeQueueDto.Status.IN_PROGRESS;
import CeQueueDto.Status.PENDING;
import InternalProperties.COMPUTE_ENGINE_PAUSE;
import ProtobufSystemInfo.Section;
import java.util.Optional;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.ce.configuration.WorkerCountProvider;
import org.sonar.db.DbClient;
import org.sonar.process.systeminfo.protobuf.ProtobufSystemInfo;
import org.sonar.server.platform.monitoring.SystemInfoTesting;


public class CeQueueGlobalSectionTest {
    private DbClient dbClient = Mockito.mock(DbClient.class, Mockito.RETURNS_DEEP_STUBS);

    private WorkerCountProvider workerCountProvider = Mockito.mock(WorkerCountProvider.class);

    @Test
    public void test_queue_state_with_default_settings() {
        Mockito.when(dbClient.ceQueueDao().countByStatus(ArgumentMatchers.any(), ArgumentMatchers.eq(PENDING))).thenReturn(10);
        Mockito.when(dbClient.ceQueueDao().countByStatus(ArgumentMatchers.any(), ArgumentMatchers.eq(IN_PROGRESS))).thenReturn(1);
        CeQueueGlobalSection underTest = new CeQueueGlobalSection(dbClient);
        ProtobufSystemInfo.Section section = underTest.toProtobuf();
        SystemInfoTesting.assertThatAttributeIs(section, "Total Pending", 10);
        SystemInfoTesting.assertThatAttributeIs(section, "Total In Progress", 1);
        SystemInfoTesting.assertThatAttributeIs(section, "Max Workers per Node", 1);
    }

    @Test
    public void test_queue_state_with_overridden_settings() {
        Mockito.when(dbClient.ceQueueDao().countByStatus(ArgumentMatchers.any(), ArgumentMatchers.eq(PENDING))).thenReturn(10);
        Mockito.when(dbClient.ceQueueDao().countByStatus(ArgumentMatchers.any(), ArgumentMatchers.eq(IN_PROGRESS))).thenReturn(2);
        Mockito.when(workerCountProvider.get()).thenReturn(5);
        CeQueueGlobalSection underTest = new CeQueueGlobalSection(dbClient, workerCountProvider);
        ProtobufSystemInfo.Section section = underTest.toProtobuf();
        SystemInfoTesting.assertThatAttributeIs(section, "Total Pending", 10);
        SystemInfoTesting.assertThatAttributeIs(section, "Total In Progress", 2);
        SystemInfoTesting.assertThatAttributeIs(section, "Max Workers per Node", 5);
    }

    @Test
    public void test_workers_not_paused() {
        CeQueueGlobalSection underTest = new CeQueueGlobalSection(dbClient, workerCountProvider);
        ProtobufSystemInfo.Section section = underTest.toProtobuf();
        SystemInfoTesting.assertThatAttributeIs(section, "Workers Paused", false);
    }

    @Test
    public void test_workers_paused() {
        Mockito.when(dbClient.internalPropertiesDao().selectByKey(ArgumentMatchers.any(), ArgumentMatchers.eq(COMPUTE_ENGINE_PAUSE))).thenReturn(Optional.of("true"));
        CeQueueGlobalSection underTest = new CeQueueGlobalSection(dbClient, workerCountProvider);
        ProtobufSystemInfo.Section section = underTest.toProtobuf();
        SystemInfoTesting.assertThatAttributeIs(section, "Workers Paused", true);
    }
}

