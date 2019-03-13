/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.server.serveraction.upgrades;


import HostRoleStatus.COMPLETED;
import HostRoleStatus.HOLDING;
import RoleCommand.CUSTOM_COMMAND;
import RoleCommand.EXECUTE;
import RoleCommand.SERVICE_CHECK;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Injector;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.ambari.server.Role;
import org.apache.ambari.server.RoleCommand;
import org.apache.ambari.server.actionmanager.ExecutionCommandWrapper;
import org.apache.ambari.server.actionmanager.ExecutionCommandWrapperFactory;
import org.apache.ambari.server.actionmanager.HostRoleCommand;
import org.apache.ambari.server.actionmanager.HostRoleStatus;
import org.apache.ambari.server.agent.CommandReport;
import org.apache.ambari.server.agent.ExecutionCommand;
import org.apache.ambari.server.orm.dao.ExecutionCommandDAO;
import org.apache.ambari.server.orm.dao.HostDAO;
import org.apache.ambari.server.orm.dao.HostRoleCommandDAO;
import org.apache.ambari.server.orm.dao.UpgradeDAO;
import org.apache.ambari.server.orm.entities.HostRoleCommandEntity;
import org.apache.ambari.server.orm.entities.UpgradeGroupEntity;
import org.apache.ambari.server.orm.entities.UpgradeItemEntity;
import org.apache.ambari.server.serveraction.AbstractServerAction;
import org.apache.ambari.server.state.Cluster;
import org.apache.ambari.server.state.Clusters;
import org.apache.ambari.server.state.Service;
import org.apache.ambari.server.state.ServiceComponentHostEvent;
import org.apache.ambari.server.state.StackId;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;


public class AutoSkipFailedSummaryActionTest {
    private Injector m_injector;

    private static final StackId HDP_STACK = new StackId("HDP-2.2.0");

    @Inject
    private ExecutionCommandDAO executionCommandDAO;

    @Inject
    private HostDAO hostDAO;

    @Inject
    private ExecutionCommandWrapperFactory ecwFactory;

    // Mocked out values
    private UpgradeDAO upgradeDAOMock;

    private HostRoleCommandDAO hostRoleCommandDAOMock;

    private Clusters clustersMock;

    private Cluster clusterMock;

    /**
     * Tests successful workflow
     */
    @Test
    public void testAutoSkipFailedSummaryAction__green() throws Exception {
        AutoSkipFailedSummaryAction action = new AutoSkipFailedSummaryAction();
        m_injector.injectMembers(action);
        ServiceComponentHostEvent event = createNiceMock(ServiceComponentHostEvent.class);
        // Set mock for parent's getHostRoleCommand()
        HostRoleCommand hostRoleCommand = new HostRoleCommand("host1", Role.AMBARI_SERVER_ACTION, event, RoleCommand.EXECUTE, hostDAO, executionCommandDAO, ecwFactory);
        hostRoleCommand.setRequestId(1L);
        hostRoleCommand.setStageId(1L);
        ExecutionCommand executionCommand = new ExecutionCommand();
        executionCommand.setClusterName("cc");
        executionCommand.setRoleCommand(EXECUTE);
        executionCommand.setRole("AMBARI_SERVER_ACTION");
        executionCommand.setServiceName("");
        executionCommand.setTaskId(1L);
        ExecutionCommandWrapper wrapper = new ExecutionCommandWrapper(executionCommand);
        hostRoleCommand.setExecutionCommandWrapper(wrapper);
        Field f = AbstractServerAction.class.getDeclaredField("hostRoleCommand");
        f.setAccessible(true);
        f.set(action, hostRoleCommand);
        final UpgradeItemEntity upgradeItem1 = new UpgradeItemEntity();
        upgradeItem1.setStageId(5L);
        final UpgradeItemEntity upgradeItem2 = new UpgradeItemEntity();
        upgradeItem2.setStageId(6L);
        UpgradeGroupEntity upgradeGroupEntity = new UpgradeGroupEntity();
        upgradeGroupEntity.setId(11L);
        // List of upgrade items in group
        List<UpgradeItemEntity> groupUpgradeItems = new ArrayList<UpgradeItemEntity>() {
            {
                add(upgradeItem1);
                add(upgradeItem2);
            }
        };
        upgradeGroupEntity.setItems(groupUpgradeItems);
        UpgradeItemEntity upgradeItemEntity = new UpgradeItemEntity();
        upgradeItemEntity.setGroupEntity(upgradeGroupEntity);
        expect(upgradeDAOMock.findUpgradeItemByRequestAndStage(anyLong(), anyLong())).andReturn(upgradeItemEntity).anyTimes();
        expect(upgradeDAOMock.findUpgradeGroup(anyLong())).andReturn(upgradeGroupEntity).anyTimes();
        replay(upgradeDAOMock);
        List<HostRoleCommandEntity> skippedTasks = new ArrayList<HostRoleCommandEntity>() {
            {
                // It's empty - no skipped tasks
            }
        };
        expect(hostRoleCommandDAOMock.findByStatusBetweenStages(anyLong(), anyObject(HostRoleStatus.class), anyLong(), anyLong())).andReturn(skippedTasks).anyTimes();
        replay(hostRoleCommandDAOMock);
        ConcurrentMap<String, Object> requestSharedDataContext = new ConcurrentHashMap<>();
        CommandReport result = action.execute(requestSharedDataContext);
        Assert.assertNotNull(result.getStructuredOut());
        Assert.assertEquals(0, result.getExitCode());
        Assert.assertEquals(COMPLETED.toString(), result.getStatus());
        Assert.assertEquals("There were no skipped failures", result.getStdOut());
        Assert.assertEquals("{}", result.getStructuredOut());
        Assert.assertEquals("", result.getStdErr());
    }

    /**
     * Tests workflow with few skipped tasks
     */
    @Test
    public void testAutoSkipFailedSummaryAction__red() throws Exception {
        AutoSkipFailedSummaryAction action = new AutoSkipFailedSummaryAction();
        m_injector.injectMembers(action);
        EasyMock.reset(clusterMock);
        Service hdfsService = createNiceMock(Service.class);
        expect(hdfsService.getName()).andReturn("HDFS").anyTimes();
        expect(clusterMock.getServiceByComponentName("DATANODE")).andReturn(hdfsService).anyTimes();
        Service zkService = createNiceMock(Service.class);
        expect(zkService.getName()).andReturn("ZOOKEEPER").anyTimes();
        expect(clusterMock.getServiceByComponentName("ZOOKEEPER_CLIENT")).andReturn(zkService).anyTimes();
        replay(clusterMock, hdfsService, zkService);
        ServiceComponentHostEvent event = createNiceMock(ServiceComponentHostEvent.class);
        // Set mock for parent's getHostRoleCommand()
        final HostRoleCommand hostRoleCommand = new HostRoleCommand("host1", Role.AMBARI_SERVER_ACTION, event, RoleCommand.EXECUTE, hostDAO, executionCommandDAO, ecwFactory);
        hostRoleCommand.setRequestId(1L);
        hostRoleCommand.setStageId(1L);
        ExecutionCommand executionCommand = new ExecutionCommand();
        executionCommand.setClusterName("cc");
        executionCommand.setRoleCommand(EXECUTE);
        executionCommand.setRole("AMBARI_SERVER_ACTION");
        executionCommand.setServiceName("");
        executionCommand.setTaskId(1L);
        ExecutionCommandWrapper wrapper = new ExecutionCommandWrapper(executionCommand);
        hostRoleCommand.setExecutionCommandWrapper(wrapper);
        Field f = AbstractServerAction.class.getDeclaredField("hostRoleCommand");
        f.setAccessible(true);
        f.set(action, hostRoleCommand);
        final UpgradeItemEntity upgradeItem1 = new UpgradeItemEntity();
        upgradeItem1.setStageId(5L);
        final UpgradeItemEntity upgradeItem2 = new UpgradeItemEntity();
        upgradeItem2.setStageId(6L);
        UpgradeGroupEntity upgradeGroupEntity = new UpgradeGroupEntity();
        upgradeGroupEntity.setId(11L);
        // List of upgrade items in group
        List<UpgradeItemEntity> groupUpgradeItems = new ArrayList<UpgradeItemEntity>() {
            {
                add(upgradeItem1);
                add(upgradeItem2);
            }
        };
        upgradeGroupEntity.setItems(groupUpgradeItems);
        UpgradeItemEntity upgradeItemEntity = new UpgradeItemEntity();
        upgradeItemEntity.setGroupEntity(upgradeGroupEntity);
        expect(upgradeDAOMock.findUpgradeItemByRequestAndStage(anyLong(), anyLong())).andReturn(upgradeItemEntity).anyTimes();
        expect(upgradeDAOMock.findUpgradeGroup(anyLong())).andReturn(upgradeGroupEntity).anyTimes();
        replay(upgradeDAOMock);
        List<HostRoleCommandEntity> skippedTasks = new ArrayList<HostRoleCommandEntity>() {
            {
                add(createSkippedTask("DATANODE", "DATANODE", "host1.vm", "RESTART HDFS/DATANODE", CUSTOM_COMMAND, "RESTART"));
                add(createSkippedTask("DATANODE", "DATANODE", "host2.vm", "RESTART HDFS/DATANODE", CUSTOM_COMMAND, "RESTART"));
                add(createSkippedTask("ZOOKEEPER_QUORUM_SERVICE_CHECK", "ZOOKEEPER_CLIENT", "host2.vm", "SERVICE_CHECK ZOOKEEPER", SERVICE_CHECK, null));
            }
        };
        expect(hostRoleCommandDAOMock.findByStatusBetweenStages(anyLong(), anyObject(HostRoleStatus.class), anyLong(), anyLong())).andReturn(skippedTasks).anyTimes();
        replay(hostRoleCommandDAOMock);
        ConcurrentMap<String, Object> requestSharedDataContext = new ConcurrentHashMap<>();
        CommandReport result = action.execute(requestSharedDataContext);
        Assert.assertNotNull(result.getStructuredOut());
        Assert.assertEquals(0, result.getExitCode());
        Assert.assertEquals(HOLDING.toString(), result.getStatus());
        Assert.assertEquals(("There were 3 skipped failure(s) that must be addressed " + "before you can proceed. Please resolve each failure before continuing with the upgrade."), result.getStdOut());
        Assert.assertEquals(("{\"failures\":" + (((("{\"service_check\":[\"ZOOKEEPER\"]," + "\"host_component\":{") + "\"host1.vm\":[{\"component\":\"DATANODE\",\"service\":\"HDFS\"}],") + "\"host2.vm\":[{\"component\":\"DATANODE\",\"service\":\"HDFS\"}]}},") + "\"skipped\":[\"service_check\",\"host_component\"]}")), result.getStructuredOut());
        Assert.assertEquals(("The following steps failed but were automatically skipped:\n" + (("DATANODE on host1.vm: RESTART HDFS/DATANODE\n" + "DATANODE on host2.vm: RESTART HDFS/DATANODE\n") + "ZOOKEEPER_CLIENT on host2.vm: SERVICE_CHECK ZOOKEEPER\n")), result.getStdErr());
    }

    /**
     * Tests workflow with failed service check
     */
    @Test
    public void testAutoSkipFailedSummaryAction__red__service_checks_only() throws Exception {
        AutoSkipFailedSummaryAction action = new AutoSkipFailedSummaryAction();
        m_injector.injectMembers(action);
        ServiceComponentHostEvent event = createNiceMock(ServiceComponentHostEvent.class);
        // Set mock for parent's getHostRoleCommand()
        final HostRoleCommand hostRoleCommand = new HostRoleCommand("host1", Role.AMBARI_SERVER_ACTION, event, RoleCommand.EXECUTE, hostDAO, executionCommandDAO, ecwFactory);
        hostRoleCommand.setRequestId(1L);
        hostRoleCommand.setStageId(1L);
        ExecutionCommand executionCommand = new ExecutionCommand();
        executionCommand.setClusterName("cc");
        executionCommand.setRoleCommand(EXECUTE);
        executionCommand.setRole("AMBARI_SERVER_ACTION");
        executionCommand.setServiceName("");
        executionCommand.setTaskId(1L);
        ExecutionCommandWrapper wrapper = new ExecutionCommandWrapper(executionCommand);
        hostRoleCommand.setExecutionCommandWrapper(wrapper);
        Field f = AbstractServerAction.class.getDeclaredField("hostRoleCommand");
        f.setAccessible(true);
        f.set(action, hostRoleCommand);
        final UpgradeItemEntity upgradeItem1 = new UpgradeItemEntity();
        upgradeItem1.setStageId(5L);
        final UpgradeItemEntity upgradeItem2 = new UpgradeItemEntity();
        upgradeItem2.setStageId(6L);
        UpgradeGroupEntity upgradeGroupEntity = new UpgradeGroupEntity();
        upgradeGroupEntity.setId(11L);
        // List of upgrade items in group
        List<UpgradeItemEntity> groupUpgradeItems = new ArrayList<UpgradeItemEntity>() {
            {
                add(upgradeItem1);
                add(upgradeItem2);
            }
        };
        upgradeGroupEntity.setItems(groupUpgradeItems);
        UpgradeItemEntity upgradeItemEntity = new UpgradeItemEntity();
        upgradeItemEntity.setGroupEntity(upgradeGroupEntity);
        expect(upgradeDAOMock.findUpgradeItemByRequestAndStage(anyLong(), anyLong())).andReturn(upgradeItemEntity).anyTimes();
        expect(upgradeDAOMock.findUpgradeGroup(anyLong())).andReturn(upgradeGroupEntity).anyTimes();
        replay(upgradeDAOMock);
        List<HostRoleCommandEntity> skippedTasks = new ArrayList<HostRoleCommandEntity>() {
            {
                add(createSkippedTask("ZOOKEEPER_QUORUM_SERVICE_CHECK", "ZOOKEEPER_CLIENT", "host2.vm", "SERVICE_CHECK ZOOKEEPER", SERVICE_CHECK, null));
            }
        };
        expect(hostRoleCommandDAOMock.findByStatusBetweenStages(anyLong(), anyObject(HostRoleStatus.class), anyLong(), anyLong())).andReturn(skippedTasks).anyTimes();
        replay(hostRoleCommandDAOMock);
        ConcurrentMap<String, Object> requestSharedDataContext = new ConcurrentHashMap<>();
        CommandReport result = action.execute(requestSharedDataContext);
        Assert.assertNotNull(result.getStructuredOut());
        Assert.assertEquals(0, result.getExitCode());
        Assert.assertEquals(HOLDING.toString(), result.getStatus());
        Assert.assertEquals(("There were 1 skipped failure(s) that must be addressed " + "before you can proceed. Please resolve each failure before continuing with the upgrade."), result.getStdOut());
        Assert.assertEquals("{\"failures\":{\"service_check\":[\"ZOOKEEPER\"]},\"skipped\":[\"service_check\"]}", result.getStructuredOut());
        Assert.assertEquals(("The following steps failed but were automatically skipped:\n" + "ZOOKEEPER_CLIENT on host2.vm: SERVICE_CHECK ZOOKEEPER\n"), result.getStdErr());
    }

    /**
     * Tests workflow with failed host component tasks only
     */
    @Test
    public void testAutoSkipFailedSummaryAction__red__host_components_only() throws Exception {
        AutoSkipFailedSummaryAction action = new AutoSkipFailedSummaryAction();
        m_injector.injectMembers(action);
        EasyMock.reset(clusterMock);
        Service hdfsService = createNiceMock(Service.class);
        expect(hdfsService.getName()).andReturn("HDFS").anyTimes();
        expect(clusterMock.getServiceByComponentName("DATANODE")).andReturn(hdfsService).anyTimes();
        replay(clusterMock, hdfsService);
        ServiceComponentHostEvent event = createNiceMock(ServiceComponentHostEvent.class);
        // Set mock for parent's getHostRoleCommand()
        final HostRoleCommand hostRoleCommand = new HostRoleCommand("host1", Role.AMBARI_SERVER_ACTION, event, RoleCommand.EXECUTE, hostDAO, executionCommandDAO, ecwFactory);
        hostRoleCommand.setRequestId(1L);
        hostRoleCommand.setStageId(1L);
        ExecutionCommand executionCommand = new ExecutionCommand();
        executionCommand.setClusterName("cc");
        executionCommand.setRoleCommand(EXECUTE);
        executionCommand.setRole("AMBARI_SERVER_ACTION");
        executionCommand.setServiceName("");
        executionCommand.setTaskId(1L);
        ExecutionCommandWrapper wrapper = new ExecutionCommandWrapper(executionCommand);
        hostRoleCommand.setExecutionCommandWrapper(wrapper);
        Field f = AbstractServerAction.class.getDeclaredField("hostRoleCommand");
        f.setAccessible(true);
        f.set(action, hostRoleCommand);
        final UpgradeItemEntity upgradeItem1 = new UpgradeItemEntity();
        upgradeItem1.setStageId(5L);
        final UpgradeItemEntity upgradeItem2 = new UpgradeItemEntity();
        upgradeItem2.setStageId(6L);
        UpgradeGroupEntity upgradeGroupEntity = new UpgradeGroupEntity();
        upgradeGroupEntity.setId(11L);
        // List of upgrade items in group
        List<UpgradeItemEntity> groupUpgradeItems = new ArrayList<UpgradeItemEntity>() {
            {
                add(upgradeItem1);
                add(upgradeItem2);
            }
        };
        upgradeGroupEntity.setItems(groupUpgradeItems);
        UpgradeItemEntity upgradeItemEntity = new UpgradeItemEntity();
        upgradeItemEntity.setGroupEntity(upgradeGroupEntity);
        expect(upgradeDAOMock.findUpgradeItemByRequestAndStage(anyLong(), anyLong())).andReturn(upgradeItemEntity).anyTimes();
        expect(upgradeDAOMock.findUpgradeGroup(anyLong())).andReturn(upgradeGroupEntity).anyTimes();
        replay(upgradeDAOMock);
        List<HostRoleCommandEntity> skippedTasks = new ArrayList<HostRoleCommandEntity>() {
            {
                add(createSkippedTask("DATANODE", "DATANODE", "host1.vm", "RESTART HDFS/DATANODE", CUSTOM_COMMAND, "RESTART"));
                add(createSkippedTask("DATANODE", "DATANODE", "host2.vm", "RESTART HDFS/DATANODE", CUSTOM_COMMAND, "RESTART"));
            }
        };
        expect(hostRoleCommandDAOMock.findByStatusBetweenStages(anyLong(), anyObject(HostRoleStatus.class), anyLong(), anyLong())).andReturn(skippedTasks).anyTimes();
        replay(hostRoleCommandDAOMock);
        ConcurrentMap<String, Object> requestSharedDataContext = new ConcurrentHashMap<>();
        CommandReport result = action.execute(requestSharedDataContext);
        Assert.assertNotNull(result.getStructuredOut());
        Assert.assertEquals(0, result.getExitCode());
        Assert.assertEquals(HOLDING.toString(), result.getStatus());
        Assert.assertEquals(("There were 2 skipped failure(s) that must be addressed " + "before you can proceed. Please resolve each failure before continuing with the upgrade."), result.getStdOut());
        Assert.assertEquals(("{\"failures\":" + ((("{\"host_component\":" + "{\"host1.vm\":[{\"component\":\"DATANODE\",\"service\":\"HDFS\"}],") + "\"host2.vm\":[{\"component\":\"DATANODE\",\"service\":\"HDFS\"}]}},") + "\"skipped\":[\"host_component\"]}")), result.getStructuredOut());
        Assert.assertEquals(("The following steps failed but were automatically skipped:\n" + ("DATANODE on host1.vm: RESTART HDFS/DATANODE\n" + "DATANODE on host2.vm: RESTART HDFS/DATANODE\n")), result.getStdErr());
    }

    public class MockModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(UpgradeDAO.class).toInstance(upgradeDAOMock);
            bind(HostRoleCommandDAO.class).toInstance(hostRoleCommandDAOMock);
            bind(Clusters.class).toInstance(clustersMock);
        }
    }
}

