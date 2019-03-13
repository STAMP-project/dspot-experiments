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
package org.sonar.ce.container;


import CoreProperties.SERVER_ID;
import CoreProperties.SERVER_STARTTIME;
import InternalProperties.SERVER_ID_CHECKSUM;
import System2.INSTANCE;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import java.util.stream.Collectors;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.picocontainer.MutablePicoContainer;
import org.sonar.api.utils.DateUtils;
import org.sonar.ce.CeDistributedInformationImpl;
import org.sonar.ce.StandaloneCeDistributedInformation;
import org.sonar.db.DbTester;
import org.sonar.process.Props;


public class ComputeEngineContainerImplTest {
    private static final int CONTAINER_ITSELF = 1;

    private static final int COMPONENTS_IN_LEVEL_1_AT_CONSTRUCTION = (ComputeEngineContainerImplTest.CONTAINER_ITSELF) + 1;

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Rule
    public DbTester db = DbTester.create(INSTANCE);

    private ComputeEngineContainerImpl underTest;

    @Test
    public void constructor_does_not_create_container() {
        assertThat(underTest.getComponentContainer()).isNull();
    }

    @Test
    public void test_real_start() throws IOException {
        Properties properties = getProperties();
        // required persisted properties
        insertProperty(SERVER_ID, "a_server_id");
        insertProperty(SERVER_STARTTIME, DateUtils.formatDateTime(new Date()));
        insertInternalProperty(SERVER_ID_CHECKSUM, DigestUtils.sha256Hex(("a_server_id|" + (cleanJdbcUrl()))));
        underTest.start(new Props(properties));
        MutablePicoContainer picoContainer = underTest.getComponentContainer().getPicoContainer();
        try {
            // CeDistributedInformation
            assertThat(picoContainer.getComponentAdapters()).hasSize(((((((((((((ComputeEngineContainerImplTest.CONTAINER_ITSELF) + 69)// level 4
             + 6)// content of CeConfigurationModule
             + 4)// content of CeQueueModule
             + 3)// content of CeHttpModule
             + 3)// content of CeTaskCommonsModule
             + 4)// content of ProjectAnalysisTaskModule
             + 9)// content of CeTaskProcessorModule
             + 3)// content of ReportAnalysisFailureNotificationModule
             + 3)// CeCleaningModule + its content
             + 4)// WebhookModule
             + 1));
            // level 3
            assertThat(picoContainer.getParent().getComponentAdapters()).hasSize(((ComputeEngineContainerImplTest.CONTAINER_ITSELF) + 8));
            // level 2
            assertThat(picoContainer.getParent().getParent().getComponentAdapters()).hasSize((((ComputeEngineContainerImplTest.CONTAINER_ITSELF) + 21)// MigrationConfigurationModule
             + 17));
            // StopFlagContainer
            assertThat(picoContainer.getParent().getParent().getParent().getComponentAdapters()).hasSize(((((((ComputeEngineContainerImplTest.COMPONENTS_IN_LEVEL_1_AT_CONSTRUCTION) + 26)// level 1
             + 60)// content of DaoModule
             + 3)// content of EsModule
             + 52)// content of CorePropertyDefinitions
             + 1));
            assertThat(picoContainer.getComponentAdapters().stream().map(ComponentAdapter::getComponentImplementation).collect(Collectors.toList())).doesNotContain(((Class) (CeDistributedInformationImpl.class))).contains(((Class) (StandaloneCeDistributedInformation.class)));
            assertThat(picoContainer.getParent().getParent().getParent().getParent()).isNull();
        } finally {
            underTest.stop();
        }
        assertThat(picoContainer.getLifecycleState().isStarted()).isFalse();
        assertThat(picoContainer.getLifecycleState().isStopped()).isFalse();
        assertThat(picoContainer.getLifecycleState().isDisposed()).isTrue();
    }
}

