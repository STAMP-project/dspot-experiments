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
package org.apache.ambari.server.actionmanager;


import ActionScheduler.FAILED_TASK_ABORT_REASONING;
import AgentCommandType.BACKGROUND_EXECUTION_COMMAND;
import CommandExecutionType.DEPENDENCY_ORDERED;
import Configuration.PARALLEL_STAGE_EXECUTION;
import HostRoleStatus.ABORTED;
import HostRoleStatus.COMPLETED;
import HostRoleStatus.FAILED;
import HostRoleStatus.HOLDING;
import HostRoleStatus.IN_PROGRESS;
import HostRoleStatus.NOT_COMPLETED_STATUSES;
import HostRoleStatus.PENDING;
import HostRoleStatus.QUEUED;
import HostRoleStatus.SKIPPED_FAILED;
import HostRoleStatus.TIMEDOUT;
import HostState.HEALTHY;
import HostState.HEARTBEAT_LOST;
import Role.AMBARI_SERVER_ACTION;
import Role.DATANODE;
import Role.FLUME_HANDLER;
import Role.GANGLIA_MONITOR;
import Role.GANGLIA_SERVER;
import Role.HBASE_CLIENT;
import Role.HBASE_MASTER;
import Role.HBASE_REGIONSERVER;
import Role.HDFS_CLIENT;
import Role.HIVE_CLIENT;
import Role.KERBEROS_CLIENT;
import Role.MAPREDUCE_CLIENT;
import Role.NAMENODE;
import Role.OOZIE_CLIENT;
import Role.SECONDARY_NAMENODE;
import Role.SQOOP;
import Role.TASKTRACKER;
import RoleCommand.ACTIONEXECUTE;
import RoleCommand.CUSTOM_COMMAND;
import RoleCommand.EXECUTE;
import RoleCommand.INSTALL;
import RoleCommand.SERVICE_CHECK;
import RoleCommand.START;
import RoleCommand.STOP;
import RoleCommand.UPGRADE;
import Service.Type.GANGLIA;
import Service.Type.HBASE;
import Service.Type.HDFS;
import Service.Type.HIVE;
import Service.Type.MAPREDUCE;
import Service.Type.OOZIE;
import Stage.INTERNAL_HOSTNAME;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.persist.UnitOfWork;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.persistence.EntityManager;
import org.apache.ambari.server.AmbariException;
import org.apache.ambari.server.Role;
import org.apache.ambari.server.RoleCommand;
import org.apache.ambari.server.ServiceComponentHostNotFoundException;
import org.apache.ambari.server.actionmanager.ActionScheduler.RoleStats;
import org.apache.ambari.server.agent.AgentCommand;
import org.apache.ambari.server.agent.CancelCommand;
import org.apache.ambari.server.agent.CommandReport;
import org.apache.ambari.server.agent.ExecutionCommand;
import org.apache.ambari.server.configuration.Configuration;
import org.apache.ambari.server.controller.HostsMap;
import org.apache.ambari.server.events.publishers.AgentCommandsPublisher;
import org.apache.ambari.server.events.publishers.AmbariEventPublisher;
import org.apache.ambari.server.metadata.RoleCommandOrder;
import org.apache.ambari.server.metadata.RoleCommandOrderProvider;
import org.apache.ambari.server.metadata.RoleCommandPair;
import org.apache.ambari.server.orm.InMemoryDefaultTestModule;
import org.apache.ambari.server.orm.dao.HostDAO;
import org.apache.ambari.server.orm.dao.HostRoleCommandDAO;
import org.apache.ambari.server.orm.entities.HostEntity;
import org.apache.ambari.server.orm.entities.HostRoleCommandEntity;
import org.apache.ambari.server.orm.entities.RequestEntity;
import org.apache.ambari.server.serveraction.MockServerAction;
import org.apache.ambari.server.serveraction.ServerActionExecutor;
import org.apache.ambari.server.state.Cluster;
import org.apache.ambari.server.state.Clusters;
import org.apache.ambari.server.state.Host;
import org.apache.ambari.server.state.Service;
import org.apache.ambari.server.state.ServiceComponent;
import org.apache.ambari.server.state.ServiceComponentHost;
import org.apache.ambari.server.state.ServiceComponentHostEvent;
import org.apache.ambari.server.state.svccomphost.ServiceComponentHostInstallEvent;
import org.apache.ambari.server.state.svccomphost.ServiceComponentHostOpFailedEvent;
import org.apache.ambari.server.utils.CommandUtils;
import org.apache.ambari.server.utils.StageUtils;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static HostRoleStatus.ABORTED;
import static HostRoleStatus.COMPLETED;
import static HostRoleStatus.FAILED;
import static HostRoleStatus.IN_PROGRESS;
import static HostRoleStatus.PENDING;
import static HostRoleStatus.QUEUED;


public class TestActionScheduler {
    private static final Logger log = LoggerFactory.getLogger(TestActionScheduler.class);

    private static final String CLUSTER_HOST_INFO = "{all_hosts=[c6403.ambari.apache.org," + (" c6401.ambari.apache.org, c6402.ambari.apache.org], slave_hosts=[c6403.ambari.apache.org," + " c6401.ambari.apache.org, c6402.ambari.apache.org]}");

    private static final String CLUSTER_HOST_INFO_UPDATED = "{all_hosts=[c6401.ambari.apache.org," + (" c6402.ambari.apache.org], slave_hosts=[c6401.ambari.apache.org," + " c6402.ambari.apache.org]}");

    private final Injector injector;

    private final String hostname = "ahost.ambari.apache.org";

    private final Long hostId = 1L;

    private final int MAX_CYCLE_ITERATIONS = 100;

    @Inject
    private HostRoleCommandFactory hostRoleCommandFactory;

    @Inject
    private StageFactory stageFactory;

    @Inject
    private HostDAO hostDAO;

    private Provider<EntityManager> entityManagerProviderMock = EasyMock.niceMock(Provider.class);

    public TestActionScheduler() {
        injector = Guice.createInjector(new InMemoryDefaultTestModule());
    }

    /**
     * This test sends a new action to the action scheduler and verifies that the action
     * shows up in the action queue.
     */
    @Test
    public void testActionSchedule() throws Exception {
        Type type = new TypeToken<Map<String, Set<String>>>() {}.getType();
        Map<String, Set<String>> clusterHostInfo = StageUtils.getGson().fromJson(TestActionScheduler.CLUSTER_HOST_INFO, type);
        Properties properties = new Properties();
        Configuration conf = new Configuration(properties);
        Clusters fsm = Mockito.mock(Clusters.class);
        Cluster oneClusterMock = Mockito.mock(Cluster.class);
        Service serviceObj = Mockito.mock(Service.class);
        ServiceComponent scomp = Mockito.mock(ServiceComponent.class);
        ServiceComponentHost sch = Mockito.mock(ServiceComponentHost.class);
        UnitOfWork unitOfWork = Mockito.mock(UnitOfWork.class);
        AgentCommandsPublisher agentCommandsPublisher = Mockito.mock(AgentCommandsPublisher.class);
        Mockito.when(fsm.getCluster(ArgumentMatchers.anyString())).thenReturn(oneClusterMock);
        Mockito.when(fsm.getClusterById(ArgumentMatchers.anyLong())).thenReturn(oneClusterMock);
        Mockito.when(oneClusterMock.getService(ArgumentMatchers.anyString())).thenReturn(serviceObj);
        Mockito.when(oneClusterMock.getClusterId()).thenReturn(Long.valueOf(1L));
        Mockito.when(serviceObj.getServiceComponent(ArgumentMatchers.anyString())).thenReturn(scomp);
        Mockito.when(scomp.getServiceComponentHost(ArgumentMatchers.anyString())).thenReturn(sch);
        Mockito.when(serviceObj.getCluster()).thenReturn(oneClusterMock);
        Host host = Mockito.mock(Host.class);
        HashMap<String, ServiceComponentHost> hosts = new HashMap<>();
        hosts.put(hostname, sch);
        HostEntity hostEntity = new HostEntity();
        hostEntity.setHostName(hostname);
        hostEntity.setHostId(hostId);
        hostDAO.merge(hostEntity);
        Mockito.when(scomp.getServiceComponentHosts()).thenReturn(hosts);
        Mockito.when(fsm.getHost(ArgumentMatchers.anyString())).thenReturn(host);
        Mockito.when(host.getState()).thenReturn(HEALTHY);
        Mockito.when(host.getHostName()).thenReturn(hostname);
        Mockito.when(host.getHostId()).thenReturn(hostId);
        ActionDBAccessor db = Mockito.mock(ActionDBAccessorImpl.class);
        HostRoleCommandDAO hostRoleCommandDAOMock = Mockito.mock(HostRoleCommandDAO.class);
        Mockito.doNothing().when(hostRoleCommandDAOMock).publishTaskCreateEvent(ArgumentMatchers.anyListOf(HostRoleCommand.class));
        Stage s = StageUtils.getATestStage(1, 977, hostname, "{\"host_param\":\"param_value\"}", "{\"stage_param\":\"param_value\"}");
        List<Stage> stages = Collections.singletonList(s);
        Mockito.when(db.getCommandsInProgressCount()).thenReturn(stages.size());
        Mockito.when(db.getFirstStageInProgressPerRequest()).thenReturn(stages);
        RequestEntity request = Mockito.mock(RequestEntity.class);
        Mockito.when(request.isExclusive()).thenReturn(false);
        Mockito.when(request.getClusterHostInfo()).thenReturn(TestActionScheduler.CLUSTER_HOST_INFO);
        Mockito.when(db.getRequestEntity(ArgumentMatchers.anyLong())).thenReturn(request);
        // Keep large number of attempts so that the task is not expired finally
        // Small action timeout to test rescheduling
        ActionScheduler scheduler = new ActionScheduler(100, 5, db, fsm, 10000, new HostsMap(((String) (null))), unitOfWork, null, conf, entityManagerProviderMock, hostRoleCommandDAOMock, null, agentCommandsPublisher);
        scheduler.setTaskTimeoutAdjustment(false);
        List<AgentCommand> commands = waitForQueueSize(hostId, agentCommandsPublisher, 1, scheduler);
        Assert.assertTrue(((commands != null) && ((commands.size()) == 1)));
        AgentCommand scheduledCommand = commands.get(0);
        Assert.assertTrue((scheduledCommand instanceof ExecutionCommand));
        Assert.assertEquals("1-977", getCommandId());
        Assert.assertEquals(clusterHostInfo, getClusterHostInfo());
        // The action status has not changed, it should be queued again.
        commands = waitForQueueSize(hostId, agentCommandsPublisher, 2, scheduler);
        // first command is cancel for previous
        Assert.assertTrue(((commands != null) && ((commands.size()) == 2)));
        scheduledCommand = commands.get(1);
        Assert.assertTrue((scheduledCommand instanceof ExecutionCommand));
        Assert.assertEquals("1-977", getCommandId());
        Assert.assertEquals(clusterHostInfo, getClusterHostInfo());
        // Now change the action status
        s.setHostRoleStatus(hostname, "NAMENODE", COMPLETED);
        // Wait for sometime, it shouldn't be scheduled this time.
        scheduler.doWork();
        EasyMock.verify(entityManagerProviderMock);
    }

    /**
     * This test sends a new action to the action scheduler and verifies that the action
     * shows up in the action queue in case of DEPENDENCY_ORDERED execution type, with RoleCommand
     * having dependencies on himself.
     */
    @Test
    public void testActionScheduleWithDependencyOrderedCommandExecution() throws Exception {
        Type type = new TypeToken<Map<String, Set<String>>>() {}.getType();
        Map<String, List<String>> clusterHostInfo = StageUtils.getGson().fromJson(TestActionScheduler.CLUSTER_HOST_INFO, type);
        Properties properties = new Properties();
        properties.setProperty("server.stage.command.execution_type", "DEPENDENCY_ORDERED");
        Configuration conf = new Configuration(properties);
        Clusters fsm = Mockito.mock(Clusters.class);
        Cluster oneClusterMock = Mockito.mock(Cluster.class);
        Service serviceObj = Mockito.mock(Service.class);
        ServiceComponent scomp = Mockito.mock(ServiceComponent.class);
        ServiceComponentHost sch = Mockito.mock(ServiceComponentHost.class);
        UnitOfWork unitOfWork = Mockito.mock(UnitOfWork.class);
        AgentCommandsPublisher agentCommandsPublisher = Mockito.mock(AgentCommandsPublisher.class);
        RoleCommandOrderProvider rcoProvider = Mockito.mock(RoleCommandOrderProvider.class);
        RoleCommandOrder rco = Mockito.mock(RoleCommandOrder.class);
        Mockito.when(fsm.getCluster(ArgumentMatchers.anyString())).thenReturn(oneClusterMock);
        Mockito.when(fsm.getClusterById(ArgumentMatchers.anyLong())).thenReturn(oneClusterMock);
        Mockito.when(oneClusterMock.getService(ArgumentMatchers.anyString())).thenReturn(serviceObj);
        Mockito.when(oneClusterMock.getClusterId()).thenReturn(Long.valueOf(1L));
        Mockito.when(serviceObj.getServiceComponent(ArgumentMatchers.anyString())).thenReturn(scomp);
        Mockito.when(scomp.getServiceComponentHost(ArgumentMatchers.anyString())).thenReturn(sch);
        Mockito.when(serviceObj.getCluster()).thenReturn(oneClusterMock);
        Mockito.when(rcoProvider.getRoleCommandOrder(1L)).thenReturn(rco);
        Map<RoleCommandPair, Set<RoleCommandPair>> roleCommandDependencies = new HashMap();
        RoleCommandPair roleCommand = new RoleCommandPair(Role.valueOf("NAMENODE"), RoleCommand.INSTALL);
        Set<RoleCommandPair> namenodeInstallDependencies = new HashSet<>();
        namenodeInstallDependencies.add(roleCommand);
        roleCommandDependencies.put(roleCommand, namenodeInstallDependencies);
        Mockito.when(rco.getDependencies()).thenReturn(roleCommandDependencies);
        Host host = Mockito.mock(Host.class);
        HashMap<String, ServiceComponentHost> hosts = new HashMap<>();
        hosts.put(hostname, sch);
        HostEntity hostEntity = new HostEntity();
        hostEntity.setHostName(hostname);
        hostEntity.setHostId(hostId);
        hostDAO.merge(hostEntity);
        Mockito.when(scomp.getServiceComponentHosts()).thenReturn(hosts);
        Mockito.when(fsm.getHost(ArgumentMatchers.anyString())).thenReturn(host);
        Mockito.when(host.getState()).thenReturn(HEALTHY);
        Mockito.when(host.getHostName()).thenReturn(hostname);
        Mockito.when(host.getHostId()).thenReturn(hostId);
        ActionDBAccessor db = Mockito.mock(ActionDBAccessorImpl.class);
        HostRoleCommandDAO hostRoleCommandDAOMock = Mockito.mock(HostRoleCommandDAO.class);
        Mockito.doNothing().when(hostRoleCommandDAOMock).publishTaskCreateEvent(ArgumentMatchers.anyListOf(HostRoleCommand.class));
        Stage s = StageUtils.getATestStage(1, 977, hostname, "{\"host_param\":\"param_value\"}", "{\"stage_param\":\"param_value\"}");
        s.setCommandExecutionType(DEPENDENCY_ORDERED);
        List<Stage> stages = Collections.singletonList(s);
        Mockito.when(db.getCommandsInProgressCount()).thenReturn(stages.size());
        Mockito.when(db.getFirstStageInProgressPerRequest()).thenReturn(stages);
        RequestEntity request = Mockito.mock(RequestEntity.class);
        Mockito.when(request.isExclusive()).thenReturn(false);
        Mockito.when(request.getClusterHostInfo()).thenReturn(TestActionScheduler.CLUSTER_HOST_INFO);
        Mockito.when(db.getRequestEntity(ArgumentMatchers.anyLong())).thenReturn(request);
        // Keep large number of attempts so that the task is not expired finally
        // Small action timeout to test rescheduling
        ActionScheduler scheduler = new ActionScheduler(100, 5, db, fsm, 10000, new HostsMap(((String) (null))), unitOfWork, null, conf, entityManagerProviderMock, hostRoleCommandDAOMock, null, rcoProvider, agentCommandsPublisher);
        scheduler.setTaskTimeoutAdjustment(false);
        List<AgentCommand> commands = waitForQueueSize(hostId, agentCommandsPublisher, 1, scheduler);
        Assert.assertTrue(((commands != null) && ((commands.size()) == 1)));
        AgentCommand scheduledCommand = commands.get(0);
        Assert.assertTrue((scheduledCommand instanceof ExecutionCommand));
        Assert.assertEquals("1-977", getCommandId());
        Assert.assertEquals(clusterHostInfo, getClusterHostInfo());
        // The action status has not changed, it should be queued again.
        commands = waitForQueueSize(hostId, agentCommandsPublisher, 2, scheduler);
        Assert.assertTrue(((commands != null) && ((commands.size()) == 2)));
        scheduledCommand = commands.get(1);
        Assert.assertTrue((scheduledCommand instanceof ExecutionCommand));
        Assert.assertEquals("1-977", getCommandId());
        Assert.assertEquals(clusterHostInfo, getClusterHostInfo());
        // Now change the action status
        s.setHostRoleStatus(hostname, "NAMENODE", COMPLETED);
        // Wait for sometime, it shouldn't be scheduled this time.
        scheduler.doWork();
        EasyMock.verify(entityManagerProviderMock);
    }

    /**
     * Test whether scheduler times out an action
     */
    @Test
    public void testActionTimeout() throws Exception {
        Properties properties = new Properties();
        Configuration conf = new Configuration(properties);
        Clusters fsm = Mockito.mock(Clusters.class);
        Cluster oneClusterMock = Mockito.mock(Cluster.class);
        Service serviceObj = Mockito.mock(Service.class);
        ServiceComponent scomp = Mockito.mock(ServiceComponent.class);
        ServiceComponentHost sch = Mockito.mock(ServiceComponentHost.class);
        UnitOfWork unitOfWork = Mockito.mock(UnitOfWork.class);
        AgentCommandsPublisher agentCommandsPublisher = Mockito.mock(AgentCommandsPublisher.class);
        Mockito.when(fsm.getCluster(ArgumentMatchers.anyString())).thenReturn(oneClusterMock);
        Mockito.when(oneClusterMock.getService(ArgumentMatchers.anyString())).thenReturn(serviceObj);
        Mockito.when(serviceObj.getServiceComponent(ArgumentMatchers.anyString())).thenReturn(scomp);
        Mockito.when(scomp.getServiceComponentHost(ArgumentMatchers.anyString())).thenReturn(sch);
        Mockito.when(serviceObj.getCluster()).thenReturn(oneClusterMock);
        Host host = Mockito.mock(Host.class);
        HashMap<String, ServiceComponentHost> hosts = new HashMap<>();
        hosts.put(hostname, sch);
        Mockito.when(scomp.getServiceComponentHosts()).thenReturn(hosts);
        Mockito.when(fsm.getHost(ArgumentMatchers.anyString())).thenReturn(host);
        Mockito.when(host.getState()).thenReturn(HEALTHY);
        Mockito.when(host.getHostName()).thenReturn(hostname);
        Mockito.when(host.getHostId()).thenReturn(hostId);
        HostEntity hostEntity = new HostEntity();
        hostEntity.setHostName(hostname);
        hostEntity.setHostId(hostId);
        hostDAO.create(hostEntity);
        final Stage s = StageUtils.getATestStage(1, 977, hostname, "{\"host_param\":\"param_value\"}", "{\"stage_param\":\"param_value\"}");
        s.addHostRoleExecutionCommand(hostname, SECONDARY_NAMENODE, INSTALL, new ServiceComponentHostInstallEvent("SECONDARY_NAMENODE", hostname, System.currentTimeMillis(), "HDP-1.2.0"), "cluster1", "HDFS", false, false);
        s.setHostRoleStatus(hostname, "SECONDARY_NAMENODE", IN_PROGRESS);
        List<Stage> stages = Collections.singletonList(s);
        ActionDBAccessor db = Mockito.mock(ActionDBAccessor.class);
        HostRoleCommandDAO hostRoleCommandDAOMock = Mockito.mock(HostRoleCommandDAO.class);
        Mockito.doNothing().when(hostRoleCommandDAOMock).publishTaskCreateEvent(ArgumentMatchers.anyListOf(HostRoleCommand.class));
        Mockito.when(db.getCommandsInProgressCount()).thenReturn(stages.size());
        Mockito.when(db.getFirstStageInProgressPerRequest()).thenReturn(stages);
        RequestEntity request = Mockito.mock(RequestEntity.class);
        Mockito.when(request.isExclusive()).thenReturn(false);
        Mockito.when(request.getClusterHostInfo()).thenReturn(TestActionScheduler.CLUSTER_HOST_INFO);
        Mockito.when(db.getRequestEntity(ArgumentMatchers.anyLong())).thenReturn(request);
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                String host = ((String) (invocation.getArguments()[0]));
                String role = ((String) (invocation.getArguments()[3]));
                HostRoleCommand command = s.getHostRoleCommand(host, role);
                command.setStatus(TIMEDOUT);
                return null;
            }
        }).when(db).timeoutHostRole(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.eq(false));
        // Small action timeout to test rescheduling
        ActionScheduler scheduler = new ActionScheduler(100, 0, db, fsm, 3, new HostsMap(((String) (null))), unitOfWork, null, conf, entityManagerProviderMock, hostRoleCommandDAOMock, null, agentCommandsPublisher);
        scheduler.setTaskTimeoutAdjustment(false);
        // Start the thread
        int cycleCount = 0;
        scheduler.doWork();
        // Check that in_progress command is rescheduled
        Assert.assertEquals(QUEUED, stages.get(0).getHostRoleStatus(hostname, "SECONDARY_NAMENODE"));
        // Check was generated cancel command on timeout
        List<AgentCommand> commands = waitForQueueSize(hostId, agentCommandsPublisher, 1, scheduler);
        Assert.assertTrue(((commands != null) && ((commands.size()) >= 1)));
        AgentCommand scheduledCommand = commands.get(0);
        Assert.assertTrue((scheduledCommand instanceof CancelCommand));
        // Switch command back to IN_PROGRESS status and check that other command is not rescheduled
        stages.get(0).setHostRoleStatus(hostname, "SECONDARY_NAMENODE", IN_PROGRESS);
        scheduler.doWork();
        Assert.assertEquals(2, stages.get(0).getAttemptCount(hostname, "NAMENODE"));
        Assert.assertEquals(3, stages.get(0).getAttemptCount(hostname, "SECONDARY_NAMENODE"));
        while ((!(stages.get(0).getHostRoleStatus(hostname, "SECONDARY_NAMENODE").equals(TIMEDOUT))) && ((cycleCount++) <= (MAX_CYCLE_ITERATIONS))) {
            scheduler.doWork();
        } 
        Assert.assertEquals(TIMEDOUT, stages.get(0).getHostRoleStatus(hostname, "SECONDARY_NAMENODE"));
        Mockito.verify(db, Mockito.times(1)).startRequest(ArgumentMatchers.eq(1L));
        Mockito.verify(db, Mockito.times(1)).abortOperation(1L);
        EasyMock.verify(entityManagerProviderMock);
    }

    @Test
    public void testActionTimeoutForLostHost() throws Exception {
        Properties properties = new Properties();
        Configuration conf = new Configuration(properties);
        Clusters fsm = Mockito.mock(Clusters.class);
        Cluster oneClusterMock = Mockito.mock(Cluster.class);
        Service serviceObj = Mockito.mock(Service.class);
        ServiceComponent scomp = Mockito.mock(ServiceComponent.class);
        ServiceComponentHost sch = Mockito.mock(ServiceComponentHost.class);
        UnitOfWork unitOfWork = Mockito.mock(UnitOfWork.class);
        AgentCommandsPublisher agentCommandsPublisher = Mockito.mock(AgentCommandsPublisher.class);
        Mockito.when(fsm.getCluster(ArgumentMatchers.anyString())).thenReturn(oneClusterMock);
        Mockito.when(oneClusterMock.getService(ArgumentMatchers.anyString())).thenReturn(serviceObj);
        Mockito.when(serviceObj.getServiceComponent(ArgumentMatchers.anyString())).thenReturn(scomp);
        Mockito.when(scomp.getServiceComponentHost(ArgumentMatchers.anyString())).thenReturn(sch);
        Mockito.when(serviceObj.getCluster()).thenReturn(oneClusterMock);
        Host host = Mockito.mock(Host.class);
        HashMap<String, ServiceComponentHost> hosts = new HashMap<>();
        hosts.put(hostname, sch);
        Mockito.when(scomp.getServiceComponentHosts()).thenReturn(hosts);
        Mockito.when(fsm.getHost(ArgumentMatchers.anyString())).thenReturn(host);
        Mockito.when(host.getState()).thenReturn(HEARTBEAT_LOST);
        Mockito.when(host.getHostName()).thenReturn(hostname);
        final Stage s = StageUtils.getATestStage(1, 977, hostname, "{\"host_param\":\"param_value\"}", "{\"stage_param\":\"param_value\"}");
        List<Stage> stages = Collections.singletonList(s);
        ActionDBAccessor db = Mockito.mock(ActionDBAccessor.class);
        RequestEntity request = Mockito.mock(RequestEntity.class);
        Mockito.when(request.isExclusive()).thenReturn(false);
        Mockito.when(db.getRequestEntity(ArgumentMatchers.anyLong())).thenReturn(request);
        Mockito.when(db.getCommandsInProgressCount()).thenReturn(stages.size());
        Mockito.when(db.getFirstStageInProgressPerRequest()).thenReturn(stages);
        HostRoleCommandDAO hostRoleCommandDAOMock = Mockito.mock(HostRoleCommandDAO.class);
        Mockito.doNothing().when(hostRoleCommandDAOMock).publishTaskCreateEvent(ArgumentMatchers.anyListOf(HostRoleCommand.class));
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                String host = ((String) (invocation.getArguments()[0]));
                String role = ((String) (invocation.getArguments()[3]));
                HostRoleCommand command = s.getHostRoleCommand(host, role);
                command.setStatus(ABORTED);
                return null;
            }
        }).when(db).timeoutHostRole(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.eq(true));
        // Small action timeout to test rescheduling
        AmbariEventPublisher aep = EasyMock.createNiceMock(AmbariEventPublisher.class);
        ActionScheduler scheduler = new ActionScheduler(100, 0, db, fsm, 3, new HostsMap(((String) (null))), unitOfWork, null, conf, entityManagerProviderMock, hostRoleCommandDAOMock, null, agentCommandsPublisher);
        scheduler.setTaskTimeoutAdjustment(false);
        int cycleCount = 0;
        while ((!(stages.get(0).getHostRoleStatus(hostname, "NAMENODE").equals(ABORTED))) && ((cycleCount++) <= (MAX_CYCLE_ITERATIONS))) {
            scheduler.doWork();
        } 
        Assert.assertEquals(ABORTED, stages.get(0).getHostRoleStatus(hostname, "NAMENODE"));
        EasyMock.verify(entityManagerProviderMock);
    }

    @Test
    public void testOpFailedEventRaisedForAbortedHostRole() throws Exception {
        Properties properties = new Properties();
        Configuration conf = new Configuration(properties);
        Clusters fsm = Mockito.mock(Clusters.class);
        Cluster oneClusterMock = Mockito.mock(Cluster.class);
        Service serviceObj = Mockito.mock(Service.class);
        ServiceComponent scomp = Mockito.mock(ServiceComponent.class);
        ServiceComponentHost sch1 = Mockito.mock(ServiceComponentHost.class);
        ServiceComponentHost sch2 = Mockito.mock(ServiceComponentHost.class);
        String hostname1 = "host1";
        String hostname2 = "host2";
        Host host1 = Mockito.mock(Host.class);
        Host host2 = Mockito.mock(Host.class);
        HostEntity hostEntity1 = new HostEntity();
        hostEntity1.setHostName(hostname1);
        HostEntity hostEntity2 = new HostEntity();
        hostEntity2.setHostName(hostname2);
        hostDAO.merge(hostEntity1);
        hostDAO.merge(hostEntity2);
        HashMap<String, ServiceComponentHost> hosts = new HashMap<>();
        hosts.put(hostname1, sch1);
        hosts.put(hostname2, sch2);
        Mockito.when(scomp.getServiceComponentHosts()).thenReturn(hosts);
        Mockito.when(fsm.getHost(hostname1)).thenReturn(host1);
        Mockito.when(fsm.getHost(hostname2)).thenReturn(host2);
        Mockito.when(host1.getState()).thenReturn(HEARTBEAT_LOST);
        Mockito.when(host2.getState()).thenReturn(HEALTHY);
        Mockito.when(host1.getHostName()).thenReturn(hostname1);
        Mockito.when(host2.getHostName()).thenReturn(hostname2);
        Mockito.when(scomp.getServiceComponentHost(hostname1)).thenReturn(sch1);
        Mockito.when(scomp.getServiceComponentHost(hostname2)).thenReturn(sch2);
        UnitOfWork unitOfWork = Mockito.mock(UnitOfWork.class);
        AgentCommandsPublisher agentCommandsPublisher = Mockito.mock(AgentCommandsPublisher.class);
        Mockito.when(fsm.getCluster(ArgumentMatchers.anyString())).thenReturn(oneClusterMock);
        Mockito.when(oneClusterMock.getService(ArgumentMatchers.anyString())).thenReturn(serviceObj);
        Mockito.when(serviceObj.getServiceComponent(ArgumentMatchers.anyString())).thenReturn(scomp);
        Mockito.when(serviceObj.getCluster()).thenReturn(oneClusterMock);
        final Stage stage = stageFactory.createNew(1, "/tmp", "cluster1", 1L, "stageWith2Tasks", "{\"command_param\":\"param_value\"}", "{\"host_param\":\"param_value\"}");
        addInstallTaskToStage(stage, hostname1, "cluster1", DATANODE, INSTALL, HDFS, 1);
        addInstallTaskToStage(stage, hostname2, "cluster1", NAMENODE, INSTALL, HDFS, 2);
        final List<Stage> stages = Collections.singletonList(stage);
        ActionDBAccessor db = Mockito.mock(ActionDBAccessor.class);
        RequestEntity request = Mockito.mock(RequestEntity.class);
        Mockito.when(request.isExclusive()).thenReturn(false);
        Mockito.when(db.getRequestEntity(ArgumentMatchers.anyLong())).thenReturn(request);
        Mockito.when(db.getCommandsInProgressCount()).thenReturn(stages.size());
        Mockito.when(db.getFirstStageInProgressPerRequest()).thenReturn(stages);
        HostRoleCommandDAO hostRoleCommandDAOMock = Mockito.mock(HostRoleCommandDAO.class);
        Mockito.doNothing().when(hostRoleCommandDAOMock).publishTaskCreateEvent(ArgumentMatchers.anyListOf(HostRoleCommand.class));
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                String host = ((String) (invocation.getArguments()[0]));
                String role = ((String) (invocation.getArguments()[3]));
                HostRoleCommand command = stage.getHostRoleCommand(host, role);
                command.setStatus(ABORTED);
                return null;
            }
        }).when(db).timeoutHostRole(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.eq(true));
        Mockito.doAnswer(new Answer<Collection<HostRoleCommandEntity>>() {
            @Override
            public Collection<HostRoleCommandEntity> answer(InvocationOnMock invocation) throws Throwable {
                Long requestId = ((Long) (invocation.getArguments()[0]));
                List<HostRoleCommandEntity> abortedCommands = Lists.newArrayList();
                for (Stage stage : stages) {
                    if (requestId.equals(stage.getRequestId())) {
                        for (HostRoleCommand command : stage.getOrderedHostRoleCommands()) {
                            if ((((command.getStatus()) == (QUEUED)) || ((command.getStatus()) == (IN_PROGRESS))) || ((command.getStatus()) == (PENDING))) {
                                command.setStatus(ABORTED);
                                HostRoleCommandEntity hostRoleCommandEntity = command.constructNewPersistenceEntity();
                                hostRoleCommandEntity.setStage(stage.constructNewPersistenceEntity());
                                abortedCommands.add(hostRoleCommandEntity);
                            }
                        }
                    }
                }
                return abortedCommands;
            }
        }).when(db).abortOperation(ArgumentMatchers.anyLong());
        ArgumentCaptor<ServiceComponentHostEvent> eventsCapture1 = ArgumentCaptor.forClass(ServiceComponentHostEvent.class);
        ArgumentCaptor<ServiceComponentHostEvent> eventsCapture2 = ArgumentCaptor.forClass(ServiceComponentHostEvent.class);
        // Make sure the NN install doesn't timeout
        ActionScheduler scheduler = new ActionScheduler(100, 50000, db, fsm, 3, new HostsMap(((String) (null))), unitOfWork, null, conf, entityManagerProviderMock, hostRoleCommandDAOMock, ((HostRoleCommandFactory) (null)), agentCommandsPublisher);
        scheduler.setTaskTimeoutAdjustment(false);
        int cycleCount = 0;
        while ((!((stages.get(0).getHostRoleStatus(hostname1, "DATANODE").equals(ABORTED)) && (stages.get(0).getHostRoleStatus(hostname2, "NAMENODE").equals(ABORTED)))) && ((cycleCount++) <= (MAX_CYCLE_ITERATIONS))) {
            scheduler.doWork();
        } 
        Assert.assertEquals(ABORTED, stages.get(0).getHostRoleStatus(hostname1, "DATANODE"));
        Assert.assertEquals(ABORTED, stages.get(0).getHostRoleStatus(hostname2, "NAMENODE"));
        Mockito.verify(sch1, Mockito.atLeastOnce()).handleEvent(eventsCapture1.capture());
        Mockito.verify(sch2, Mockito.atLeastOnce()).handleEvent(eventsCapture2.capture());
        List<ServiceComponentHostEvent> eventTypes = eventsCapture1.getAllValues();
        eventTypes.addAll(eventsCapture2.getAllValues());
        Assert.assertNotNull(eventTypes);
        ServiceComponentHostOpFailedEvent datanodeFailedEvent = null;
        ServiceComponentHostOpFailedEvent namenodeFailedEvent = null;
        for (ServiceComponentHostEvent eventType : eventTypes) {
            if (eventType instanceof ServiceComponentHostOpFailedEvent) {
                ServiceComponentHostOpFailedEvent event = ((ServiceComponentHostOpFailedEvent) (eventType));
                if (event.getServiceComponentName().equals("DATANODE")) {
                    datanodeFailedEvent = event;
                } else
                    if (event.getServiceComponentName().equals("NAMENODE")) {
                        namenodeFailedEvent = event;
                    }

            }
        }
        Assert.assertNotNull("Datanode should be in Install failed state.", datanodeFailedEvent);
        Assert.assertNotNull("Namenode should be in Install failed state.", namenodeFailedEvent);
    }

    /**
     * Test server action
     */
    @Test
    public void testServerAction() throws Exception {
        Properties properties = new Properties();
        Configuration conf = new Configuration(properties);
        Clusters fsm = Mockito.mock(Clusters.class);
        UnitOfWork unitOfWork = Mockito.mock(UnitOfWork.class);
        AgentCommandsPublisher agentCommandsPublisher = Mockito.mock(AgentCommandsPublisher.class);
        Map<String, String> payload = new HashMap<>();
        final Stage s = getStageWithServerAction(1, 977, payload, "test", 1200, false, false);
        List<Stage> stages = Collections.singletonList(s);
        ActionDBAccessor db = Mockito.mock(ActionDBAccessor.class);
        HostRoleCommandDAO hostRoleCommandDAOMock = Mockito.mock(HostRoleCommandDAO.class);
        Mockito.doNothing().when(hostRoleCommandDAOMock).publishTaskCreateEvent(ArgumentMatchers.anyListOf(HostRoleCommand.class));
        RequestEntity request = Mockito.mock(RequestEntity.class);
        Mockito.when(request.isExclusive()).thenReturn(false);
        Mockito.when(request.getClusterHostInfo()).thenReturn(TestActionScheduler.CLUSTER_HOST_INFO);
        Mockito.when(db.getRequestEntity(ArgumentMatchers.anyLong())).thenReturn(request);
        Mockito.when(db.getCommandsInProgressCount()).thenReturn(stages.size());
        Mockito.when(db.getFirstStageInProgressPerRequest()).thenReturn(stages);
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                String host = ((String) (invocation.getArguments()[0]));
                String role = ((String) (invocation.getArguments()[3]));
                CommandReport commandReport = ((CommandReport) (invocation.getArguments()[4]));
                HostRoleCommand command = null;
                if (null == host) {
                    command = s.getHostRoleCommand(null, role);
                } else {
                    command = s.getHostRoleCommand(host, role);
                }
                command.setStatus(HostRoleStatus.valueOf(commandReport.getStatus()));
                return null;
            }
        }).when(db).updateHostRoleState(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyString(), ArgumentMatchers.any(CommandReport.class));
        Mockito.doAnswer(new Answer<HostRoleCommand>() {
            @Override
            public HostRoleCommand answer(InvocationOnMock invocation) throws Throwable {
                return s.getHostRoleCommand(null, "AMBARI_SERVER_ACTION");
            }
        }).when(db).getTask(ArgumentMatchers.anyLong());
        Mockito.doAnswer(new Answer<List<HostRoleCommand>>() {
            @Override
            public List<HostRoleCommand> answer(InvocationOnMock invocation) throws Throwable {
                String role = ((String) (invocation.getArguments()[0]));
                HostRoleStatus status = ((HostRoleStatus) (invocation.getArguments()[1]));
                HostRoleCommand task = s.getHostRoleCommand(null, role);
                if ((task.getStatus()) == status) {
                    return Arrays.asList(task);
                } else {
                    return Collections.emptyList();
                }
            }
        }).when(db).getTasksByRoleAndStatus(ArgumentMatchers.anyString(), ArgumentMatchers.any(HostRoleStatus.class));
        ServerActionExecutor.init(injector);
        ActionScheduler scheduler = new ActionScheduler(100, 50, db, fsm, 3, new HostsMap(((String) (null))), unitOfWork, null, conf, entityManagerProviderMock, hostRoleCommandDAOMock, ((HostRoleCommandFactory) (null)), agentCommandsPublisher);
        int cycleCount = 0;
        while ((!(stages.get(0).getHostRoleStatus(null, "AMBARI_SERVER_ACTION").equals(COMPLETED))) && ((cycleCount++) <= (MAX_CYCLE_ITERATIONS))) {
            scheduler.doWork();
            scheduler.getServerActionExecutor().doWork();
        } 
        Assert.assertEquals(stages.get(0).getHostRoleStatus(null, "AMBARI_SERVER_ACTION"), COMPLETED);
    }

    /**
     * Test server actions in multiple requests.
     *
     * This is used to make sure the server-side actions do not get filtered out from
     * {@link org.apache.ambari.server.actionmanager.ActionScheduler#filterParallelPerHostStages(java.util.List)}
     */
    @Test
    public void testServerActionInMultipleRequests() throws Exception {
        Clusters fsm = Mockito.mock(Clusters.class);
        Cluster oneClusterMock = Mockito.mock(Cluster.class);
        Service serviceObj = Mockito.mock(Service.class);
        ServiceComponent scomp = Mockito.mock(ServiceComponent.class);
        ServiceComponentHost sch = Mockito.mock(ServiceComponentHost.class);
        UnitOfWork unitOfWork = Mockito.mock(UnitOfWork.class);
        AgentCommandsPublisher agentCommandsPublisher = Mockito.mock(AgentCommandsPublisher.class);
        Mockito.when(fsm.getCluster(ArgumentMatchers.anyString())).thenReturn(oneClusterMock);
        Mockito.when(oneClusterMock.getService(ArgumentMatchers.anyString())).thenReturn(serviceObj);
        Mockito.when(serviceObj.getServiceComponent(ArgumentMatchers.anyString())).thenReturn(scomp);
        Mockito.when(scomp.getServiceComponentHost(ArgumentMatchers.anyString())).thenReturn(sch);
        Mockito.when(serviceObj.getCluster()).thenReturn(oneClusterMock);
        String clusterName = "cluster1";
        String hostname1 = "ahost.ambari.apache.org";
        String hostname2 = "bhost.ambari.apache.org";
        HashMap<String, ServiceComponentHost> hosts = new HashMap<>();
        hosts.put(hostname1, sch);
        hosts.put(hostname2, sch);
        hosts.put(INTERNAL_HOSTNAME, sch);
        Mockito.when(scomp.getServiceComponentHosts()).thenReturn(hosts);
        List<Stage> stages = new ArrayList<>();
        Stage stage01 = createStage(clusterName, 0, 1);
        addTask(stage01, INTERNAL_HOSTNAME, clusterName, AMBARI_SERVER_ACTION, ACTIONEXECUTE, "AMBARI", 1);
        Stage stage11 = createStage("cluster1", 1, 1);
        addTask(stage11, hostname1, clusterName, KERBEROS_CLIENT, CUSTOM_COMMAND, "KERBEROS", 2);
        Stage stage02 = createStage("cluster1", 0, 2);
        addTask(stage02, INTERNAL_HOSTNAME, clusterName, AMBARI_SERVER_ACTION, ACTIONEXECUTE, "AMBARI", 3);
        Stage stage12 = createStage("cluster1", 1, 2);
        addTask(stage12, hostname2, clusterName, KERBEROS_CLIENT, CUSTOM_COMMAND, "KERBEROS", 4);
        stages.add(stage01);
        stages.add(stage11);
        stages.add(stage02);
        stages.add(stage12);
        ActionDBAccessor db = Mockito.mock(ActionDBAccessor.class);
        HostRoleCommandDAO hostRoleCommandDAOMock = Mockito.mock(HostRoleCommandDAO.class);
        Mockito.doNothing().when(hostRoleCommandDAOMock).publishTaskCreateEvent(ArgumentMatchers.anyListOf(HostRoleCommand.class));
        RequestEntity request = Mockito.mock(RequestEntity.class);
        Mockito.when(request.isExclusive()).thenReturn(false);
        Mockito.when(request.getClusterHostInfo()).thenReturn(TestActionScheduler.CLUSTER_HOST_INFO);
        Mockito.when(db.getRequestEntity(ArgumentMatchers.anyLong())).thenReturn(request);
        Mockito.when(db.getCommandsInProgressCount()).thenReturn(stages.size());
        Mockito.when(db.getFirstStageInProgressPerRequest()).thenReturn(stages);
        Properties properties = new Properties();
        properties.put(PARALLEL_STAGE_EXECUTION.getKey(), "true");
        Configuration conf = new Configuration(properties);
        ActionScheduler scheduler = new ActionScheduler(100, 50, db, fsm, 3, new HostsMap(((String) (null))), unitOfWork, EasyMock.createNiceMock(AmbariEventPublisher.class), conf, entityManagerProviderMock, hostRoleCommandDAOMock, ((HostRoleCommandFactory) (null)), agentCommandsPublisher);
        scheduler.doWork();
        Assert.assertEquals(QUEUED, stages.get(0).getHostRoleStatus(INTERNAL_HOSTNAME, AMBARI_SERVER_ACTION.name()));
        Assert.assertEquals(PENDING, stages.get(1).getHostRoleStatus(hostname1, KERBEROS_CLIENT.name()));
        Assert.assertEquals(QUEUED, stages.get(2).getHostRoleStatus(INTERNAL_HOSTNAME, AMBARI_SERVER_ACTION.name()));
        Assert.assertEquals(PENDING, stages.get(3).getHostRoleStatus(hostname2, KERBEROS_CLIENT.name()));
    }

    /**
     * Test server action
     */
    @Test
    public void testServerActionTimeOut() throws Exception {
        Properties properties = new Properties();
        Configuration conf = new Configuration(properties);
        Clusters fsm = Mockito.mock(Clusters.class);
        UnitOfWork unitOfWork = Mockito.mock(UnitOfWork.class);
        AgentCommandsPublisher agentCommandsPublisher = Mockito.mock(AgentCommandsPublisher.class);
        Map<String, String> payload = new HashMap<>();
        payload.put(MockServerAction.PAYLOAD_FORCE_FAIL, "timeout");
        final Stage s = getStageWithServerAction(1, 977, payload, "test", 2, false, false);
        List<Stage> stages = Collections.singletonList(s);
        ActionDBAccessor db = Mockito.mock(ActionDBAccessor.class);
        HostRoleCommandDAO hostRoleCommandDAOMock = Mockito.mock(HostRoleCommandDAO.class);
        Mockito.doNothing().when(hostRoleCommandDAOMock).publishTaskCreateEvent(ArgumentMatchers.anyListOf(HostRoleCommand.class));
        RequestEntity request = Mockito.mock(RequestEntity.class);
        Mockito.when(request.isExclusive()).thenReturn(false);
        Mockito.when(request.getClusterHostInfo()).thenReturn(TestActionScheduler.CLUSTER_HOST_INFO);
        Mockito.when(db.getRequestEntity(ArgumentMatchers.anyLong())).thenReturn(request);
        Mockito.when(db.getCommandsInProgressCount()).thenReturn(stages.size());
        Mockito.when(db.getFirstStageInProgressPerRequest()).thenReturn(stages);
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                String host = ((String) (invocation.getArguments()[0]));
                String role = ((String) (invocation.getArguments()[3]));
                CommandReport commandReport = ((CommandReport) (invocation.getArguments()[4]));
                HostRoleCommand command = null;
                if (null == host) {
                    command = s.getHostRoleCommand(null, role);
                } else {
                    command = s.getHostRoleCommand(host, role);
                }
                command.setStatus(HostRoleStatus.valueOf(commandReport.getStatus()));
                return null;
            }
        }).when(db).updateHostRoleState(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyString(), ArgumentMatchers.any(CommandReport.class));
        Mockito.doAnswer(new Answer<HostRoleCommand>() {
            @Override
            public HostRoleCommand answer(InvocationOnMock invocation) throws Throwable {
                return s.getHostRoleCommand(null, "AMBARI_SERVER_ACTION");
            }
        }).when(db).getTask(ArgumentMatchers.anyLong());
        Mockito.doAnswer(new Answer<List<HostRoleCommand>>() {
            @Override
            public List<HostRoleCommand> answer(InvocationOnMock invocation) throws Throwable {
                String role = ((String) (invocation.getArguments()[0]));
                HostRoleStatus status = ((HostRoleStatus) (invocation.getArguments()[1]));
                HostRoleCommand task = s.getHostRoleCommand(null, role);
                if ((task.getStatus()) == status) {
                    return Arrays.asList(task);
                } else {
                    return Collections.emptyList();
                }
            }
        }).when(db).getTasksByRoleAndStatus(ArgumentMatchers.anyString(), ArgumentMatchers.any(HostRoleStatus.class));
        ServerActionExecutor.init(injector);
        ActionScheduler scheduler = new ActionScheduler(100, 50, db, fsm, 3, new HostsMap(((String) (null))), unitOfWork, null, conf, entityManagerProviderMock, hostRoleCommandDAOMock, ((HostRoleCommandFactory) (null)), agentCommandsPublisher);
        int cycleCount = 0;
        while ((!(stages.get(0).getHostRoleStatus(null, "AMBARI_SERVER_ACTION").isCompletedState())) && ((cycleCount++) <= (MAX_CYCLE_ITERATIONS))) {
            scheduler.doWork();
            scheduler.getServerActionExecutor().doWork();
        } 
        Assert.assertEquals(TIMEDOUT, stages.get(0).getHostRoleStatus(null, "AMBARI_SERVER_ACTION"));
    }

    @Test
    public void testTimeOutWithHostNull() throws AmbariException {
        Stage s = getStageWithServerAction(1, 977, null, "test", 2, false, false);
        s.setHostRoleStatus(null, AMBARI_SERVER_ACTION.toString(), IN_PROGRESS);
        ActionScheduler scheduler = EasyMock.createMockBuilder(ActionScheduler.class).withConstructor(long.class, long.class, ActionDBAccessor.class, Clusters.class, int.class, HostsMap.class, UnitOfWork.class, AmbariEventPublisher.class, Configuration.class, Provider.class, HostRoleCommandDAO.class, HostRoleCommandFactory.class, AgentCommandsPublisher.class).withArgs(100L, 50L, null, null, (-1), null, null, null, null, entityManagerProviderMock, Mockito.mock(HostRoleCommandDAO.class), Mockito.mock(HostRoleCommandFactory.class), Mockito.mock(AgentCommandsPublisher.class)).createNiceMock();
        EasyMock.replay(scheduler);
        // currentTime should be set to -1 and taskTimeout to 1 because it is needed for timeOutActionNeeded method will return false value
        Assert.assertEquals(false, scheduler.timeOutActionNeeded(IN_PROGRESS, s, null, AMBARI_SERVER_ACTION.toString(), (-1L), 1L));
        EasyMock.verify(scheduler);
    }

    @Test
    public void testTimeoutRequestDueAgentRestartExecuteCommand() throws Exception {
        testTimeoutRequest(EXECUTE, false, false);
    }

    @Test
    public void testTimeoutRequestDueAgentRestartCustomCommand() throws Exception {
        testTimeoutRequest(CUSTOM_COMMAND, false, false);
    }

    @Test
    public void testTimeoutRequestDueAgentRestartActionExecute() throws Exception {
        testTimeoutRequest(ACTIONEXECUTE, false, false);
    }

    @Test
    public void testTimeoutRequestDueAgentRestartServiceCheck() throws Exception {
        testTimeoutRequest(SERVICE_CHECK, false, false);
    }

    /**
     * Ensures that the task is timed out but is not skipped just because its
     * stage is skipped.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testTimeoutWithSkippableStageButNotCommand() throws Exception {
        testTimeoutRequest(EXECUTE, true, false);
    }

    /**
     * Ensures that the task is timed out and that it will be skipped.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testTimeoutWithSkippableCommand() throws Exception {
        testTimeoutRequest(EXECUTE, true, true);
    }

    @Test
    public void testServerActionFailed() throws Exception {
        Properties properties = new Properties();
        Configuration conf = new Configuration(properties);
        Clusters fsm = Mockito.mock(Clusters.class);
        UnitOfWork unitOfWork = Mockito.mock(UnitOfWork.class);
        AgentCommandsPublisher agentCommandsPublisher = Mockito.mock(AgentCommandsPublisher.class);
        Map<String, String> payload = new HashMap<>();
        payload.put(MockServerAction.PAYLOAD_FORCE_FAIL, "exception");
        final Stage s = getStageWithServerAction(1, 977, payload, "test", 300, false, false);
        List<Stage> stages = Collections.singletonList(s);
        ActionDBAccessor db = Mockito.mock(ActionDBAccessor.class);
        HostRoleCommandDAO hostRoleCommandDAOMock = Mockito.mock(HostRoleCommandDAO.class);
        Mockito.doNothing().when(hostRoleCommandDAOMock).publishTaskCreateEvent(ArgumentMatchers.anyListOf(HostRoleCommand.class));
        RequestEntity request = Mockito.mock(RequestEntity.class);
        Mockito.when(request.isExclusive()).thenReturn(false);
        Mockito.when(request.getClusterHostInfo()).thenReturn(TestActionScheduler.CLUSTER_HOST_INFO);
        Mockito.when(db.getRequestEntity(ArgumentMatchers.anyLong())).thenReturn(request);
        Mockito.when(db.getCommandsInProgressCount()).thenReturn(stages.size());
        Mockito.when(db.getFirstStageInProgressPerRequest()).thenReturn(stages);
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                String host = ((String) (invocation.getArguments()[0]));
                String role = ((String) (invocation.getArguments()[3]));
                CommandReport commandReport = ((CommandReport) (invocation.getArguments()[4]));
                HostRoleCommand command = null;
                if (null == host) {
                    command = s.getHostRoleCommand(null, role);
                } else {
                    command = s.getHostRoleCommand(host, role);
                }
                command.setStatus(HostRoleStatus.valueOf(commandReport.getStatus()));
                return null;
            }
        }).when(db).updateHostRoleState(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyString(), ArgumentMatchers.any(CommandReport.class));
        Mockito.doAnswer(new Answer<HostRoleCommand>() {
            @Override
            public HostRoleCommand answer(InvocationOnMock invocation) throws Throwable {
                return s.getHostRoleCommand(null, "AMBARI_SERVER_ACTION");
            }
        }).when(db).getTask(ArgumentMatchers.anyLong());
        Mockito.doAnswer(new Answer<List<HostRoleCommand>>() {
            @Override
            public List<HostRoleCommand> answer(InvocationOnMock invocation) throws Throwable {
                String role = ((String) (invocation.getArguments()[0]));
                HostRoleStatus status = ((HostRoleStatus) (invocation.getArguments()[1]));
                HostRoleCommand task = s.getHostRoleCommand(null, role);
                if ((task.getStatus()) == status) {
                    return Arrays.asList(task);
                } else {
                    return Collections.emptyList();
                }
            }
        }).when(db).getTasksByRoleAndStatus(ArgumentMatchers.anyString(), ArgumentMatchers.any(HostRoleStatus.class));
        ActionScheduler scheduler = new ActionScheduler(100, 50, db, fsm, 3, new HostsMap(((String) (null))), unitOfWork, null, conf, entityManagerProviderMock, hostRoleCommandDAOMock, ((HostRoleCommandFactory) (null)), agentCommandsPublisher);
        int cycleCount = 0;
        while ((!(stages.get(0).getHostRoleStatus(null, "AMBARI_SERVER_ACTION").equals(FAILED))) && ((cycleCount++) <= (MAX_CYCLE_ITERATIONS))) {
            scheduler.doWork();
            scheduler.getServerActionExecutor().doWork();
        } 
        Assert.assertEquals(stages.get(0).getHostRoleStatus(null, "AMBARI_SERVER_ACTION"), FAILED);
        Assert.assertEquals("test", stages.get(0).getRequestContext());
    }

    /**
     * Verifies that stages that are executed on different hosts and
     * rely to different requests are scheduled to be  executed in parallel
     */
    @Test
    public void testIndependentStagesExecution() throws Exception {
        Clusters fsm = Mockito.mock(Clusters.class);
        Cluster oneClusterMock = Mockito.mock(Cluster.class);
        Service serviceObj = Mockito.mock(Service.class);
        ServiceComponent scomp = Mockito.mock(ServiceComponent.class);
        ServiceComponentHost sch = Mockito.mock(ServiceComponentHost.class);
        UnitOfWork unitOfWork = Mockito.mock(UnitOfWork.class);
        AgentCommandsPublisher agentCommandsPublisher = Mockito.mock(AgentCommandsPublisher.class);
        Mockito.when(fsm.getCluster(ArgumentMatchers.anyString())).thenReturn(oneClusterMock);
        // when(fsm.getHost(anyString())).thenReturn(oneClusterMock);
        Mockito.when(oneClusterMock.getService(ArgumentMatchers.anyString())).thenReturn(serviceObj);
        Mockito.when(serviceObj.getServiceComponent(ArgumentMatchers.anyString())).thenReturn(scomp);
        Mockito.when(scomp.getServiceComponentHost(ArgumentMatchers.anyString())).thenReturn(sch);
        Mockito.when(serviceObj.getCluster()).thenReturn(oneClusterMock);
        String hostname1 = "ahost.ambari.apache.org";
        String hostname2 = "bhost.ambari.apache.org";
        String hostname3 = "chost.ambari.apache.org";
        String hostname4 = "chost.ambari.apache.org";
        Long hostId1 = 1L;
        Long hostId2 = 2L;
        Long hostId3 = 3L;
        Long hostId4 = 4L;
        HashMap<String, ServiceComponentHost> hosts = new HashMap<>();
        hosts.put(hostname1, sch);
        hosts.put(hostname2, sch);
        hosts.put(hostname3, sch);
        hosts.put(hostname4, sch);
        Mockito.when(scomp.getServiceComponentHosts()).thenReturn(hosts);
        Host host1 = Mockito.mock(Host.class);
        Mockito.when(fsm.getHost(hostname1)).thenReturn(host1);
        Mockito.when(host1.getState()).thenReturn(HEALTHY);
        Mockito.when(host1.getHostName()).thenReturn(hostname1);
        Mockito.when(host1.getHostId()).thenReturn(hostId1);
        Host host2 = Mockito.mock(Host.class);
        Mockito.when(fsm.getHost(hostname2)).thenReturn(host2);
        Mockito.when(host2.getState()).thenReturn(HEALTHY);
        Mockito.when(host2.getHostName()).thenReturn(hostname2);
        Mockito.when(host2.getHostId()).thenReturn(hostId2);
        Host host3 = Mockito.mock(Host.class);
        Mockito.when(fsm.getHost(hostname3)).thenReturn(host3);
        Mockito.when(host3.getState()).thenReturn(HEALTHY);
        Mockito.when(host3.getHostName()).thenReturn(hostname3);
        Mockito.when(host3.getHostId()).thenReturn(hostId3);
        Host host4 = Mockito.mock(Host.class);
        Mockito.when(fsm.getHost(hostname4)).thenReturn(host4);
        Mockito.when(host4.getState()).thenReturn(HEALTHY);
        Mockito.when(host4.getHostName()).thenReturn(hostname4);
        Mockito.when(host4.getHostId()).thenReturn(hostId4);
        List<Stage> firstStageInProgressPerRequest = new ArrayList<>();
        firstStageInProgressPerRequest.add(getStageWithSingleTask(hostname1, "cluster1", DATANODE, START, HDFS, 1, 1, 1));
        // Stage with the same hostname, should not be scheduled
        firstStageInProgressPerRequest.add(getStageWithSingleTask(hostname1, "cluster1", GANGLIA_MONITOR, START, GANGLIA, 2, 2, 2));
        firstStageInProgressPerRequest.add(getStageWithSingleTask(hostname2, "cluster1", DATANODE, START, HDFS, 3, 3, 3));
        firstStageInProgressPerRequest.add(getStageWithSingleTask(hostname3, "cluster1", DATANODE, START, HDFS, 4, 4, 4));
        ActionDBAccessor db = Mockito.mock(ActionDBAccessor.class);
        HostRoleCommandDAO hostRoleCommandDAOMock = Mockito.mock(HostRoleCommandDAO.class);
        Mockito.doNothing().when(hostRoleCommandDAOMock).publishTaskCreateEvent(ArgumentMatchers.anyListOf(HostRoleCommand.class));
        List<String> blockingHostsRequest1 = new ArrayList<>();
        Mockito.when(hostRoleCommandDAOMock.getBlockingHostsForRequest(1, 1)).thenReturn(blockingHostsRequest1);
        List<String> blockingHostsRequest2 = Lists.newArrayList(hostname1);
        Mockito.when(hostRoleCommandDAOMock.getBlockingHostsForRequest(1, 2)).thenReturn(blockingHostsRequest2);
        RequestEntity request = Mockito.mock(RequestEntity.class);
        Mockito.when(request.isExclusive()).thenReturn(false);
        Mockito.when(request.getClusterHostInfo()).thenReturn(TestActionScheduler.CLUSTER_HOST_INFO);
        Mockito.when(db.getRequestEntity(ArgumentMatchers.anyLong())).thenReturn(request);
        Mockito.when(db.getCommandsInProgressCount()).thenReturn(firstStageInProgressPerRequest.size());
        Mockito.when(db.getFirstStageInProgressPerRequest()).thenReturn(firstStageInProgressPerRequest);
        Properties properties = new Properties();
        Configuration conf = new Configuration(properties);
        ActionScheduler scheduler = Mockito.spy(new ActionScheduler(100, 50, db, fsm, 3, new HostsMap(((String) (null))), unitOfWork, null, conf, entityManagerProviderMock, hostRoleCommandDAOMock, ((HostRoleCommandFactory) (null)), agentCommandsPublisher));
        Mockito.doReturn(false).when(scheduler).wasAgentRestartedDuringOperation(ArgumentMatchers.any(Host.class), ArgumentMatchers.any(Stage.class), ArgumentMatchers.anyString());
        scheduler.doWork();
        Assert.assertEquals(QUEUED, firstStageInProgressPerRequest.get(0).getHostRoleStatus(hostname1, "DATANODE"));
        Assert.assertEquals(PENDING, firstStageInProgressPerRequest.get(1).getHostRoleStatus(hostname1, "GANGLIA_MONITOR"));
        Assert.assertEquals(QUEUED, firstStageInProgressPerRequest.get(2).getHostRoleStatus(hostname2, "DATANODE"));
        Assert.assertEquals(QUEUED, firstStageInProgressPerRequest.get(3).getHostRoleStatus(hostname3, "DATANODE"));
    }

    /**
     * Verifies that ActionScheduler respects "disable parallel stage execution option"
     */
    @Test
    public void testIndependentStagesExecutionDisabled() throws Exception {
        Clusters fsm = Mockito.mock(Clusters.class);
        Cluster oneClusterMock = Mockito.mock(Cluster.class);
        Service serviceObj = Mockito.mock(Service.class);
        ServiceComponent scomp = Mockito.mock(ServiceComponent.class);
        ServiceComponentHost sch = Mockito.mock(ServiceComponentHost.class);
        UnitOfWork unitOfWork = Mockito.mock(UnitOfWork.class);
        AgentCommandsPublisher agentCommandsPublisher = Mockito.mock(AgentCommandsPublisher.class);
        Mockito.when(fsm.getCluster(ArgumentMatchers.anyString())).thenReturn(oneClusterMock);
        Mockito.when(oneClusterMock.getService(ArgumentMatchers.anyString())).thenReturn(serviceObj);
        Mockito.when(serviceObj.getServiceComponent(ArgumentMatchers.anyString())).thenReturn(scomp);
        Mockito.when(scomp.getServiceComponentHost(ArgumentMatchers.anyString())).thenReturn(sch);
        Mockito.when(serviceObj.getCluster()).thenReturn(oneClusterMock);
        String hostname1 = "ahost.ambari.apache.org";
        String hostname2 = "bhost.ambari.apache.org";
        String hostname3 = "chost.ambari.apache.org";
        String hostname4 = "chost.ambari.apache.org";
        Long hostId1 = 1L;
        Long hostId2 = 2L;
        Long hostId3 = 3L;
        Long hostId4 = 4L;
        HashMap<String, ServiceComponentHost> hosts = new HashMap<>();
        hosts.put(hostname1, sch);
        hosts.put(hostname2, sch);
        hosts.put(hostname3, sch);
        hosts.put(hostname4, sch);
        Mockito.when(scomp.getServiceComponentHosts()).thenReturn(hosts);
        Host host1 = Mockito.mock(Host.class);
        Mockito.when(fsm.getHost(hostname1)).thenReturn(host1);
        Mockito.when(host1.getState()).thenReturn(HEALTHY);
        Mockito.when(host1.getHostName()).thenReturn(hostname1);
        Mockito.when(host1.getHostId()).thenReturn(hostId1);
        Host host2 = Mockito.mock(Host.class);
        Mockito.when(fsm.getHost(hostname2)).thenReturn(host2);
        Mockito.when(host2.getState()).thenReturn(HEALTHY);
        Mockito.when(host2.getHostName()).thenReturn(hostname2);
        Mockito.when(host2.getHostId()).thenReturn(hostId2);
        Host host3 = Mockito.mock(Host.class);
        Mockito.when(fsm.getHost(hostname3)).thenReturn(host3);
        Mockito.when(host3.getState()).thenReturn(HEALTHY);
        Mockito.when(host3.getHostName()).thenReturn(hostname3);
        Mockito.when(host3.getHostId()).thenReturn(hostId3);
        Host host4 = Mockito.mock(Host.class);
        Mockito.when(fsm.getHost(hostname4)).thenReturn(host4);
        Mockito.when(host4.getState()).thenReturn(HEALTHY);
        Mockito.when(host4.getHostName()).thenReturn(hostname4);
        Mockito.when(host4.getHostId()).thenReturn(hostId4);
        List<Stage> stages = new ArrayList<>();
        Stage stage = getStageWithSingleTask(hostname1, "cluster1", HIVE_CLIENT, INSTALL, HIVE, 1, 1, 1);
        Map<String, String> hiveSite = new TreeMap<>();
        hiveSite.put("javax.jdo.option.ConnectionPassword", "password");
        hiveSite.put("hive.server2.thrift.port", "10000");
        Map<String, Map<String, String>> configurations = new TreeMap<>();
        configurations.put("hive-site", hiveSite);
        stage.getExecutionCommands(hostname1).get(0).getExecutionCommand().setConfigurations(configurations);
        stages.add(stage);
        // Stage with the same hostname, should not be scheduled
        stages.add(getStageWithSingleTask(hostname1, "cluster1", GANGLIA_MONITOR, START, GANGLIA, 2, 2, 2));
        stages.add(getStageWithSingleTask(hostname2, "cluster1", HIVE_CLIENT, INSTALL, HIVE, 3, 3, 3));
        stages.add(getStageWithSingleTask(hostname3, "cluster1", DATANODE, START, HDFS, 4, 4, 4));
        // Stage with the same request id, should not be scheduled
        stages.add(getStageWithSingleTask(hostname4, "cluster1", GANGLIA_MONITOR, START, GANGLIA, 5, 5, 4));
        ActionDBAccessor db = Mockito.mock(ActionDBAccessor.class);
        HostRoleCommandDAO hostRoleCommandDAOMock = Mockito.mock(HostRoleCommandDAO.class);
        Mockito.doNothing().when(hostRoleCommandDAOMock).publishTaskCreateEvent(ArgumentMatchers.anyListOf(HostRoleCommand.class));
        RequestEntity request = Mockito.mock(RequestEntity.class);
        Mockito.when(request.isExclusive()).thenReturn(false);
        Mockito.when(request.getClusterHostInfo()).thenReturn(TestActionScheduler.CLUSTER_HOST_INFO);
        Mockito.when(db.getRequestEntity(ArgumentMatchers.anyLong())).thenReturn(request);
        Mockito.when(db.getCommandsInProgressCount()).thenReturn(stages.size());
        Mockito.when(db.getFirstStageInProgressPerRequest()).thenReturn(stages);
        Properties properties = new Properties();
        properties.put(PARALLEL_STAGE_EXECUTION.getKey(), "false");
        Configuration conf = new Configuration(properties);
        ActionScheduler scheduler = Mockito.spy(new ActionScheduler(100, 50, db, fsm, 3, new HostsMap(((String) (null))), unitOfWork, null, conf, entityManagerProviderMock, hostRoleCommandDAOMock, ((HostRoleCommandFactory) (null)), agentCommandsPublisher));
        Mockito.doReturn(false).when(scheduler).wasAgentRestartedDuringOperation(ArgumentMatchers.any(Host.class), ArgumentMatchers.any(Stage.class), ArgumentMatchers.anyString());
        scheduler.doWork();
        Assert.assertEquals(QUEUED, stages.get(0).getHostRoleStatus(hostname1, "HIVE_CLIENT"));
        Assert.assertEquals(PENDING, stages.get(1).getHostRoleStatus(hostname1, "GANGLIA_MONITOR"));
        Assert.assertEquals(PENDING, stages.get(2).getHostRoleStatus(hostname2, "HIVE_CLIENT"));
        Assert.assertEquals(PENDING, stages.get(3).getHostRoleStatus(hostname3, "DATANODE"));
        Assert.assertEquals(PENDING, stages.get(4).getHostRoleStatus(hostname4, "GANGLIA_MONITOR"));
        Assert.assertFalse(stages.get(0).getExecutionCommands(hostname1).get(0).getExecutionCommand().getConfigurations().containsKey("javax.jdo.option.ConnectionPassword"));
    }

    /**
     * Verifies that ActionScheduler allows to execute background tasks in parallel
     */
    @Test
    public void testBackgroundStagesExecutionEnable() throws Exception {
        Clusters fsm = Mockito.mock(Clusters.class);
        Cluster oneClusterMock = Mockito.mock(Cluster.class);
        Service serviceObj = Mockito.mock(Service.class);
        ServiceComponent scomp = Mockito.mock(ServiceComponent.class);
        ServiceComponentHost sch = Mockito.mock(ServiceComponentHost.class);
        UnitOfWork unitOfWork = Mockito.mock(UnitOfWork.class);
        AgentCommandsPublisher agentCommandsPublisher = Mockito.mock(AgentCommandsPublisher.class);
        Mockito.when(fsm.getCluster(ArgumentMatchers.anyString())).thenReturn(oneClusterMock);
        Mockito.when(oneClusterMock.getService(ArgumentMatchers.anyString())).thenReturn(serviceObj);
        Mockito.when(serviceObj.getServiceComponent(ArgumentMatchers.anyString())).thenReturn(scomp);
        Mockito.when(scomp.getServiceComponentHost(ArgumentMatchers.anyString())).thenReturn(sch);
        Mockito.when(serviceObj.getCluster()).thenReturn(oneClusterMock);
        String hostname1 = "ahost.ambari.apache.org";
        String hostname2 = "bhost.ambari.apache.org";
        Long hostId1 = 1L;
        Long hostId2 = 2L;
        HashMap<String, ServiceComponentHost> hosts = new HashMap<>();
        hosts.put(hostname1, sch);
        hosts.put(hostname2, sch);
        Mockito.when(scomp.getServiceComponentHosts()).thenReturn(hosts);
        Host host1 = Mockito.mock(Host.class);
        Mockito.when(fsm.getHost(hostname1)).thenReturn(host1);
        Mockito.when(host1.getState()).thenReturn(HEALTHY);
        Mockito.when(host1.getHostName()).thenReturn(hostname1);
        Mockito.when(host1.getHostId()).thenReturn(hostId1);
        Host host2 = Mockito.mock(Host.class);
        Mockito.when(fsm.getHost(hostname2)).thenReturn(host2);
        Mockito.when(host2.getState()).thenReturn(HEALTHY);
        Mockito.when(host2.getHostName()).thenReturn(hostname2);
        Mockito.when(host2.getHostId()).thenReturn(hostId2);
        List<Stage> stages = new ArrayList<>();
        Stage backgroundStage = null;
        // stage with background command
        stages.add((backgroundStage = getStageWithSingleTask(hostname1, "cluster1", NAMENODE, CUSTOM_COMMAND, "REBALANCEHDFS", HDFS, 1, 1, 1)));
        Assert.assertEquals(BACKGROUND_EXECUTION_COMMAND, backgroundStage.getExecutionCommands(hostname1).get(0).getExecutionCommand().getCommandType());
        // Stage with the same hostname, should be scheduled
        stages.add(getStageWithSingleTask(hostname1, "cluster1", GANGLIA_MONITOR, START, GANGLIA, 2, 2, 2));
        stages.add(getStageWithSingleTask(hostname2, "cluster1", DATANODE, START, HDFS, 3, 3, 3));
        ActionDBAccessor db = Mockito.mock(ActionDBAccessor.class);
        HostRoleCommandDAO hostRoleCommandDAOMock = Mockito.mock(HostRoleCommandDAO.class);
        Mockito.doNothing().when(hostRoleCommandDAOMock).publishTaskCreateEvent(ArgumentMatchers.anyListOf(HostRoleCommand.class));
        RequestEntity request = Mockito.mock(RequestEntity.class);
        Mockito.when(request.isExclusive()).thenReturn(false);
        Mockito.when(request.getClusterHostInfo()).thenReturn(TestActionScheduler.CLUSTER_HOST_INFO);
        Mockito.when(db.getRequestEntity(ArgumentMatchers.anyLong())).thenReturn(request);
        Mockito.when(db.getCommandsInProgressCount()).thenReturn(stages.size());
        Mockito.when(db.getFirstStageInProgressPerRequest()).thenReturn(stages);
        Properties properties = new Properties();
        properties.put(PARALLEL_STAGE_EXECUTION.getKey(), "true");
        Configuration conf = new Configuration(properties);
        ActionScheduler scheduler = Mockito.spy(new ActionScheduler(100, 50, db, fsm, 3, new HostsMap(((String) (null))), unitOfWork, null, conf, entityManagerProviderMock, hostRoleCommandDAOMock, ((HostRoleCommandFactory) (null)), agentCommandsPublisher));
        Mockito.doReturn(false).when(scheduler).wasAgentRestartedDuringOperation(ArgumentMatchers.any(Host.class), ArgumentMatchers.any(Stage.class), ArgumentMatchers.anyString());
        scheduler.doWork();
        Assert.assertEquals(QUEUED, stages.get(0).getHostRoleStatus(hostname1, "NAMENODE"));
        Assert.assertEquals(QUEUED, stages.get(2).getHostRoleStatus(hostname2, "DATANODE"));
        Assert.assertEquals(QUEUED, stages.get(1).getHostRoleStatus(hostname1, "GANGLIA_MONITOR"));
    }

    @Test
    public void testRequestFailureOnStageFailure() throws Exception {
        Clusters fsm = Mockito.mock(Clusters.class);
        Cluster oneClusterMock = Mockito.mock(Cluster.class);
        Service serviceObj = Mockito.mock(Service.class);
        ServiceComponent scomp = Mockito.mock(ServiceComponent.class);
        ServiceComponentHost sch = Mockito.mock(ServiceComponentHost.class);
        UnitOfWork unitOfWork = Mockito.mock(UnitOfWork.class);
        AmbariEventPublisher ambariEventPublisher = Mockito.mock(AmbariEventPublisher.class);
        RequestFactory requestFactory = Mockito.mock(RequestFactory.class);
        Mockito.when(fsm.getCluster(ArgumentMatchers.anyString())).thenReturn(oneClusterMock);
        Mockito.when(oneClusterMock.getService(ArgumentMatchers.anyString())).thenReturn(serviceObj);
        Mockito.when(serviceObj.getServiceComponent(ArgumentMatchers.anyString())).thenReturn(scomp);
        Mockito.when(scomp.getServiceComponentHost(ArgumentMatchers.anyString())).thenReturn(sch);
        Mockito.when(serviceObj.getCluster()).thenReturn(oneClusterMock);
        HashMap<String, ServiceComponentHost> hosts = new HashMap<>();
        hosts.put(hostname, sch);
        Mockito.when(scomp.getServiceComponentHosts()).thenReturn(hosts);
        final List<Stage> stages = new ArrayList<>();
        stages.add(getStageWithSingleTask(hostname, "cluster1", NAMENODE, UPGRADE, HDFS, 1, 1, 1));
        List<Stage> firstStageInProgress = Collections.singletonList(stages.get(0));
        stages.add(getStageWithSingleTask(hostname, "cluster1", DATANODE, UPGRADE, HDFS, 2, 2, 1));
        Host host = Mockito.mock(Host.class);
        Mockito.when(fsm.getHost(ArgumentMatchers.anyString())).thenReturn(host);
        Mockito.when(host.getState()).thenReturn(HEALTHY);
        Mockito.when(host.getHostName()).thenReturn(hostname);
        ActionDBAccessor db = Mockito.mock(ActionDBAccessor.class);
        RequestEntity request = Mockito.mock(RequestEntity.class);
        Mockito.when(request.isExclusive()).thenReturn(false);
        Mockito.when(request.getClusterHostInfo()).thenReturn(TestActionScheduler.CLUSTER_HOST_INFO);
        Mockito.when(db.getRequestEntity(ArgumentMatchers.anyLong())).thenReturn(request);
        Mockito.when(db.getCommandsInProgressCount()).thenReturn(stages.size());
        Mockito.when(db.getFirstStageInProgressPerRequest()).thenReturn(firstStageInProgress);
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                List<CommandReport> reports = ((List<CommandReport>) (invocation.getArguments()[0]));
                for (CommandReport report : reports) {
                    String actionId = report.getActionId();
                    long[] requestStageIds = StageUtils.getRequestStage(actionId);
                    Long requestId = requestStageIds[0];
                    Long stageId = requestStageIds[1];
                    Long id = report.getTaskId();
                    for (Stage stage : stages) {
                        if ((requestId.equals(stage.getRequestId())) && (stageId.equals(stage.getStageId()))) {
                            for (HostRoleCommand hostRoleCommand : stage.getOrderedHostRoleCommands()) {
                                if ((hostRoleCommand.getTaskId()) == id) {
                                    hostRoleCommand.setStatus(HostRoleStatus.valueOf(report.getStatus()));
                                }
                            }
                        }
                    }
                }
                return null;
            }
        }).when(db).updateHostRoleStates(ArgumentMatchers.anyCollectionOf(CommandReport.class));
        Mockito.when(db.getTask(ArgumentMatchers.anyLong())).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Long taskId = ((Long) (invocation.getArguments()[0]));
                for (Stage stage : stages) {
                    for (HostRoleCommand command : stage.getOrderedHostRoleCommands()) {
                        if (taskId.equals(command.getTaskId())) {
                            return command;
                        }
                    }
                }
                return null;
            }
        });
        Mockito.doAnswer(new Answer<Collection<HostRoleCommandEntity>>() {
            @Override
            public Collection<HostRoleCommandEntity> answer(InvocationOnMock invocation) throws Throwable {
                Long requestId = ((Long) (invocation.getArguments()[0]));
                List<HostRoleCommandEntity> abortedCommands = Lists.newArrayList();
                for (Stage stage : stages) {
                    if (requestId.equals(stage.getRequestId())) {
                        for (HostRoleCommand command : stage.getOrderedHostRoleCommands()) {
                            if ((((command.getStatus()) == (HostRoleStatus.QUEUED)) || ((command.getStatus()) == (HostRoleStatus.IN_PROGRESS))) || ((command.getStatus()) == (HostRoleStatus.PENDING))) {
                                command.setStatus(ABORTED);
                                HostRoleCommandEntity hostRoleCommandEntity = command.constructNewPersistenceEntity();
                                hostRoleCommandEntity.setStage(stage.constructNewPersistenceEntity());
                                abortedCommands.add(hostRoleCommandEntity);
                            }
                        }
                    }
                }
                return abortedCommands;
            }
        }).when(db).abortOperation(ArgumentMatchers.anyLong());
        Properties properties = new Properties();
        Configuration conf = new Configuration(properties);
        Capture<Collection<HostRoleCommand>> cancelCommandList = EasyMock.newCapture();
        ActionScheduler scheduler = EasyMock.createMockBuilder(ActionScheduler.class).withConstructor(((long) (100)), ((long) (50)), db, fsm, 3, new HostsMap(((String) (null))), unitOfWork, EasyMock.createNiceMock(AmbariEventPublisher.class), conf, entityManagerProviderMock, Mockito.mock(HostRoleCommandDAO.class), Mockito.mock(HostRoleCommandFactory.class), Mockito.mock(RoleCommandOrderProvider.class), Mockito.mock(AgentCommandsPublisher.class)).addMockedMethod("cancelHostRoleCommands").createMock();
        scheduler.cancelHostRoleCommands(EasyMock.capture(cancelCommandList), EasyMock.eq(FAILED_TASK_ABORT_REASONING));
        EasyMock.expectLastCall().once();
        EasyMock.replay(scheduler);
        ActionManager am = new ActionManager(db, requestFactory, scheduler);
        scheduler.doWork();
        List<CommandReport> reports = new ArrayList<>();
        reports.add(getCommandReport(FAILED, NAMENODE, HDFS, "1-1", 1));
        am.processTaskResponse(hostname, reports, CommandUtils.convertToTaskIdCommandMap(stages.get(0).getOrderedHostRoleCommands()));
        scheduler.doWork();
        Assert.assertEquals(FAILED, stages.get(0).getHostRoleStatus(hostname, "NAMENODE"));
        Assert.assertEquals(ABORTED, stages.get(1).getHostRoleStatus(hostname, "DATANODE"));
        Assert.assertEquals(cancelCommandList.getValue().size(), 1);
        EasyMock.verify(scheduler, entityManagerProviderMock);
    }

    /**
     * Tests that the whole request is aborted when there are no QUEUED tasks for a role and
     * success factor is not met. As long as there is one QUEUED task the request is not
     * aborted.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testRequestAbortsOnlyWhenNoQueuedTaskAndSuccessFactorUnmet() throws Exception {
        Clusters fsm = Mockito.mock(Clusters.class);
        Cluster oneClusterMock = Mockito.mock(Cluster.class);
        Service serviceObj = Mockito.mock(Service.class);
        ServiceComponent scomp = Mockito.mock(ServiceComponent.class);
        ServiceComponentHost sch = Mockito.mock(ServiceComponentHost.class);
        UnitOfWork unitOfWork = Mockito.mock(UnitOfWork.class);
        AgentCommandsPublisher agentCommandsPublisher = Mockito.mock(AgentCommandsPublisher.class);
        Mockito.when(fsm.getCluster(ArgumentMatchers.anyString())).thenReturn(oneClusterMock);
        Mockito.when(oneClusterMock.getService(ArgumentMatchers.anyString())).thenReturn(serviceObj);
        Mockito.when(serviceObj.getServiceComponent(ArgumentMatchers.anyString())).thenReturn(scomp);
        Mockito.when(scomp.getServiceComponentHost(ArgumentMatchers.anyString())).thenReturn(sch);
        Mockito.when(serviceObj.getCluster()).thenReturn(oneClusterMock);
        String host1 = "host1";
        String host2 = "host2";
        Host host = Mockito.mock(Host.class);
        HashMap<String, ServiceComponentHost> hosts = new HashMap<>();
        hosts.put(host1, sch);
        hosts.put(host2, sch);
        Mockito.when(scomp.getServiceComponentHosts()).thenReturn(hosts);
        Mockito.when(fsm.getHost(ArgumentMatchers.anyString())).thenReturn(host);
        Mockito.when(host.getState()).thenReturn(HEALTHY);
        Mockito.when(host.getHostName()).thenReturn(host1);
        HostEntity hostEntity1 = new HostEntity();
        HostEntity hostEntity2 = new HostEntity();
        hostEntity1.setHostName(host1);
        hostEntity2.setHostName(host2);
        hostDAO.create(hostEntity1);
        hostDAO.create(hostEntity2);
        long now = System.currentTimeMillis();
        Stage stage = stageFactory.createNew(1, "/tmp", "cluster1", 1L, "testRequestFailureBasedOnSuccessFactor", "", "");
        stage.setStageId(1);
        addHostRoleExecutionCommand(now, stage, SQOOP, Service.Type.SQOOP, INSTALL, host1, "cluster1");
        addHostRoleExecutionCommand(now, stage, OOZIE_CLIENT, OOZIE, INSTALL, host1, "cluster1");
        addHostRoleExecutionCommand(now, stage, MAPREDUCE_CLIENT, MAPREDUCE, INSTALL, host1, "cluster1");
        addHostRoleExecutionCommand(now, stage, HBASE_CLIENT, HBASE, INSTALL, host1, "cluster1");
        addHostRoleExecutionCommand(now, stage, GANGLIA_MONITOR, GANGLIA, INSTALL, host1, "cluster1");
        addHostRoleExecutionCommand(now, stage, HBASE_CLIENT, HBASE, INSTALL, host2, "cluster1");
        addHostRoleExecutionCommand(now, stage, GANGLIA_MONITOR, GANGLIA, INSTALL, host2, "cluster1");
        final List<Stage> stages = Collections.singletonList(stage);
        HostRoleStatus[] statusesAtIterOne = new HostRoleStatus[]{ HostRoleStatus.QUEUED, HostRoleStatus.QUEUED, HostRoleStatus.QUEUED, HostRoleStatus.QUEUED, FAILED, FAILED, HostRoleStatus.QUEUED, HostRoleStatus.QUEUED };
        for (int index = 0; index < (stage.getOrderedHostRoleCommands().size()); index++) {
            stage.getOrderedHostRoleCommands().get(index).setTaskId((index + 1));
            stage.getOrderedHostRoleCommands().get(index).setStatus(statusesAtIterOne[index]);
        }
        stage.setLastAttemptTime(host1, SQOOP.toString(), now);
        stage.setLastAttemptTime(host1, MAPREDUCE_CLIENT.toString(), now);
        stage.setLastAttemptTime(host1, OOZIE_CLIENT.toString(), now);
        stage.setLastAttemptTime(host1, GANGLIA_MONITOR.toString(), now);
        stage.setLastAttemptTime(host1, HBASE_CLIENT.toString(), now);
        stage.setLastAttemptTime(host2, GANGLIA_MONITOR.toString(), now);
        stage.setLastAttemptTime(host2, HBASE_CLIENT.toString(), now);
        ActionDBAccessor db = Mockito.mock(ActionDBAccessor.class);
        HostRoleCommandDAO hostRoleCommandDAOMock = Mockito.mock(HostRoleCommandDAO.class);
        Mockito.doNothing().when(hostRoleCommandDAOMock).publishTaskCreateEvent(ArgumentMatchers.anyListOf(HostRoleCommand.class));
        RequestEntity request = Mockito.mock(RequestEntity.class);
        Mockito.when(request.isExclusive()).thenReturn(false);
        Mockito.when(db.getRequestEntity(ArgumentMatchers.anyLong())).thenReturn(request);
        Mockito.when(db.getCommandsInProgressCount()).thenReturn(stages.size());
        Mockito.when(db.getFirstStageInProgressPerRequest()).thenReturn(stages);
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                String host = ((String) (invocation.getArguments()[0]));
                Long requestId = ((Long) (invocation.getArguments()[1]));
                Long stageId = ((Long) (invocation.getArguments()[2]));
                String role = ((String) (invocation.getArguments()[3]));
                CommandReport commandReport = ((CommandReport) (invocation.getArguments()[4]));
                for (Stage stage : stages) {
                    if ((requestId.equals(stage.getRequestId())) && (stageId.equals(stage.getStageId()))) {
                        HostRoleCommand command = stage.getHostRoleCommand(host, role);
                        command.setStatus(HostRoleStatus.valueOf(commandReport.getStatus()));
                    }
                }
                return null;
            }
        }).when(db).updateHostRoleState(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyString(), ArgumentMatchers.any(CommandReport.class));
        Mockito.when(db.getTask(ArgumentMatchers.anyLong())).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Long taskId = ((Long) (invocation.getArguments()[0]));
                for (Stage stage : stages) {
                    for (HostRoleCommand command : stage.getOrderedHostRoleCommands()) {
                        if (taskId.equals(command.getTaskId())) {
                            return command;
                        }
                    }
                }
                return null;
            }
        });
        Mockito.doAnswer(new Answer<Collection<HostRoleCommandEntity>>() {
            @Override
            public Collection<HostRoleCommandEntity> answer(InvocationOnMock invocation) throws Throwable {
                Long requestId = ((Long) (invocation.getArguments()[0]));
                List<HostRoleCommandEntity> abortedCommands = Lists.newArrayList();
                for (Stage stage : stages) {
                    if (requestId.equals(stage.getRequestId())) {
                        for (HostRoleCommand command : stage.getOrderedHostRoleCommands()) {
                            if ((((command.getStatus()) == (HostRoleStatus.QUEUED)) || ((command.getStatus()) == (HostRoleStatus.IN_PROGRESS))) || ((command.getStatus()) == (HostRoleStatus.PENDING))) {
                                command.setStatus(ABORTED);
                                HostRoleCommandEntity hostRoleCommandEntity = command.constructNewPersistenceEntity();
                                hostRoleCommandEntity.setStage(stage.constructNewPersistenceEntity());
                                abortedCommands.add(hostRoleCommandEntity);
                            }
                        }
                    }
                }
                return abortedCommands;
            }
        }).when(db).abortOperation(ArgumentMatchers.anyLong());
        Properties properties = new Properties();
        Configuration conf = new Configuration(properties);
        ActionScheduler scheduler = new ActionScheduler(100, 10000, db, fsm, 3, new HostsMap(((String) (null))), unitOfWork, null, conf, entityManagerProviderMock, hostRoleCommandDAOMock, ((HostRoleCommandFactory) (null)), agentCommandsPublisher);
        scheduler.doWork();
        // Request is not aborted because all roles are in progress
        HostRoleStatus[] expectedStatusesAtIterOne = new HostRoleStatus[]{ HostRoleStatus.QUEUED, HostRoleStatus.QUEUED, HostRoleStatus.QUEUED, HostRoleStatus.QUEUED, FAILED, FAILED, HostRoleStatus.QUEUED, HostRoleStatus.QUEUED };
        for (int index = 0; index < (stage.getOrderedHostRoleCommands().size()); index++) {
            TestActionScheduler.log.info(stage.getOrderedHostRoleCommands().get(index).toString());
            Assert.assertEquals(expectedStatusesAtIterOne[index], stage.getOrderedHostRoleCommands().get(index).getStatus());
        }
        HostRoleStatus[] statusesAtIterTwo = new HostRoleStatus[]{ HostRoleStatus.QUEUED, HostRoleStatus.QUEUED, HostRoleStatus.QUEUED, HostRoleStatus.QUEUED, FAILED, FAILED, HostRoleStatus.QUEUED, COMPLETED };
        for (int index = 0; index < (stage.getOrderedHostRoleCommands().size()); index++) {
            stage.getOrderedHostRoleCommands().get(index).setStatus(statusesAtIterTwo[index]);
        }
        scheduler.doWork();
        // Request is not aborted because GANGLIA_MONITOR's success factor (0.5) is met
        HostRoleStatus[] expectedStatusesAtIterTwo = new HostRoleStatus[]{ HostRoleStatus.QUEUED, HostRoleStatus.QUEUED, HostRoleStatus.QUEUED, HostRoleStatus.QUEUED, FAILED, FAILED, HostRoleStatus.QUEUED, COMPLETED };
        for (int index = 0; index < (stage.getOrderedHostRoleCommands().size()); index++) {
            TestActionScheduler.log.info(stage.getOrderedHostRoleCommands().get(index).toString());
            Assert.assertEquals(expectedStatusesAtIterTwo[index], stage.getOrderedHostRoleCommands().get(index).getStatus());
        }
        HostRoleStatus[] statusesAtIterThree = new HostRoleStatus[]{ HostRoleStatus.QUEUED, HostRoleStatus.QUEUED, HostRoleStatus.QUEUED, HostRoleStatus.QUEUED, FAILED, FAILED, FAILED, COMPLETED };
        for (int index = 0; index < (stage.getOrderedHostRoleCommands().size()); index++) {
            stage.getOrderedHostRoleCommands().get(index).setStatus(statusesAtIterThree[index]);
        }
        // Fails becuse HostRoleCommand doesn't have a hostName
        scheduler.doWork();
        // Request is aborted because HBASE_CLIENT's success factor (1) is not met
        HostRoleStatus[] expectedStatusesAtIterThree = new HostRoleStatus[]{ ABORTED, ABORTED, ABORTED, ABORTED, FAILED, FAILED, FAILED, COMPLETED };
        for (int index = 0; index < (stage.getOrderedHostRoleCommands().size()); index++) {
            TestActionScheduler.log.info(stage.getOrderedHostRoleCommands().get(index).toString());
            Assert.assertEquals(expectedStatusesAtIterThree[index], stage.getOrderedHostRoleCommands().get(index).getStatus());
        }
    }

    @Test
    public void testRequestFailureBasedOnSuccessFactor() throws Exception {
        Clusters fsm = Mockito.mock(Clusters.class);
        Cluster oneClusterMock = Mockito.mock(Cluster.class);
        Service serviceObj = Mockito.mock(Service.class);
        ServiceComponent scomp = Mockito.mock(ServiceComponent.class);
        ServiceComponentHost sch = Mockito.mock(ServiceComponentHost.class);
        UnitOfWork unitOfWork = Mockito.mock(UnitOfWork.class);
        AgentCommandsPublisher agentCommandsPublisher = Mockito.mock(AgentCommandsPublisher.class);
        RequestFactory requestFactory = Mockito.mock(RequestFactory.class);
        Mockito.when(fsm.getCluster(ArgumentMatchers.anyString())).thenReturn(oneClusterMock);
        Mockito.when(oneClusterMock.getService(ArgumentMatchers.anyString())).thenReturn(serviceObj);
        Mockito.when(serviceObj.getServiceComponent(ArgumentMatchers.anyString())).thenReturn(scomp);
        Mockito.when(scomp.getServiceComponentHost(ArgumentMatchers.anyString())).thenReturn(sch);
        Mockito.when(serviceObj.getCluster()).thenReturn(oneClusterMock);
        final List<Stage> stages = new ArrayList<>();
        long now = System.currentTimeMillis();
        Stage stage = stageFactory.createNew(1, "/tmp", "cluster1", 1L, "testRequestFailureBasedOnSuccessFactor", "", "");
        stage.setStageId(1);
        stage.addHostRoleExecutionCommand("host1", DATANODE, UPGRADE, new org.apache.ambari.server.state.svccomphost.ServiceComponentHostUpgradeEvent(DATANODE.toString(), "host1", now, "HDP-0.2"), "cluster1", HDFS.toString(), false, false);
        stage.getExecutionCommandWrapper("host1", DATANODE.toString()).getExecutionCommand();
        stage.addHostRoleExecutionCommand("host2", DATANODE, UPGRADE, new org.apache.ambari.server.state.svccomphost.ServiceComponentHostUpgradeEvent(DATANODE.toString(), "host2", now, "HDP-0.2"), "cluster1", HDFS.toString(), false, false);
        stage.getExecutionCommandWrapper("host2", DATANODE.toString()).getExecutionCommand();
        stage.addHostRoleExecutionCommand("host3", DATANODE, UPGRADE, new org.apache.ambari.server.state.svccomphost.ServiceComponentHostUpgradeEvent(DATANODE.toString(), "host3", now, "HDP-0.2"), "cluster1", HDFS.toString(), false, false);
        stage.getExecutionCommandWrapper("host3", DATANODE.toString()).getExecutionCommand();
        stages.add(stage);
        List<Stage> stageInProgress = Collections.singletonList(stage);
        stage.getOrderedHostRoleCommands().get(0).setTaskId(1);
        stage.getOrderedHostRoleCommands().get(1).setTaskId(2);
        stage.getOrderedHostRoleCommands().get(2).setTaskId(3);
        stages.add(getStageWithSingleTask("host1", "cluster1", HDFS_CLIENT, UPGRADE, HDFS, 4, 2, 1));
        ActionDBAccessor db = Mockito.mock(ActionDBAccessor.class);
        HostRoleCommandDAO hostRoleCommandDAOMock = Mockito.mock(HostRoleCommandDAO.class);
        Mockito.doNothing().when(hostRoleCommandDAOMock).publishTaskCreateEvent(ArgumentMatchers.anyListOf(HostRoleCommand.class));
        RequestEntity request = Mockito.mock(RequestEntity.class);
        Mockito.when(request.isExclusive()).thenReturn(false);
        Mockito.when(db.getRequestEntity(ArgumentMatchers.anyLong())).thenReturn(request);
        Mockito.when(db.getCommandsInProgressCount()).thenReturn(stageInProgress.size());
        Mockito.when(db.getFirstStageInProgressPerRequest()).thenReturn(stageInProgress);
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                List<CommandReport> reports = ((List<CommandReport>) (invocation.getArguments()[0]));
                for (CommandReport report : reports) {
                    String actionId = report.getActionId();
                    long[] requestStageIds = StageUtils.getRequestStage(actionId);
                    Long requestId = requestStageIds[0];
                    Long stageId = requestStageIds[1];
                    Long id = report.getTaskId();
                    for (Stage stage : stages) {
                        if ((requestId.equals(stage.getRequestId())) && (stageId.equals(stage.getStageId()))) {
                            for (HostRoleCommand hostRoleCommand : stage.getOrderedHostRoleCommands()) {
                                if ((hostRoleCommand.getTaskId()) == id) {
                                    hostRoleCommand.setStatus(HostRoleStatus.valueOf(report.getStatus()));
                                }
                            }
                        }
                    }
                }
                return null;
            }
        }).when(db).updateHostRoleStates(ArgumentMatchers.anyCollectionOf(CommandReport.class));
        Mockito.when(db.getTask(ArgumentMatchers.anyLong())).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Long taskId = ((Long) (invocation.getArguments()[0]));
                for (Stage stage : stages) {
                    for (HostRoleCommand command : stage.getOrderedHostRoleCommands()) {
                        if (taskId.equals(command.getTaskId())) {
                            return command;
                        }
                    }
                }
                return null;
            }
        });
        Mockito.doAnswer(new Answer<Collection<HostRoleCommandEntity>>() {
            @Override
            public Collection<HostRoleCommandEntity> answer(InvocationOnMock invocation) throws Throwable {
                Long requestId = ((Long) (invocation.getArguments()[0]));
                List<HostRoleCommandEntity> abortedCommands = Lists.newArrayList();
                for (Stage stage : stages) {
                    if (requestId.equals(stage.getRequestId())) {
                        for (HostRoleCommand command : stage.getOrderedHostRoleCommands()) {
                            if ((((command.getStatus()) == (HostRoleStatus.QUEUED)) || ((command.getStatus()) == (HostRoleStatus.IN_PROGRESS))) || ((command.getStatus()) == (HostRoleStatus.PENDING))) {
                                command.setStatus(ABORTED);
                                HostRoleCommandEntity hostRoleCommandEntity = command.constructNewPersistenceEntity();
                                hostRoleCommandEntity.setStage(stage.constructNewPersistenceEntity());
                                abortedCommands.add(hostRoleCommandEntity);
                            }
                        }
                    }
                }
                return abortedCommands;
            }
        }).when(db).abortOperation(ArgumentMatchers.anyLong());
        Properties properties = new Properties();
        Configuration conf = new Configuration(properties);
        ActionScheduler scheduler = new ActionScheduler(100, 50, db, fsm, 3, new HostsMap(((String) (null))), unitOfWork, null, conf, entityManagerProviderMock, hostRoleCommandDAOMock, ((HostRoleCommandFactory) (null)), agentCommandsPublisher);
        ActionManager am = new ActionManager(db, requestFactory, scheduler);
        scheduler.doWork();
        List<CommandReport> reports = new ArrayList<>();
        reports.add(getCommandReport(FAILED, DATANODE, HDFS, "1-1", 1));
        am.processTaskResponse("host1", reports, CommandUtils.convertToTaskIdCommandMap(stage.getOrderedHostRoleCommands()));
        reports.clear();
        reports.add(getCommandReport(FAILED, DATANODE, HDFS, "1-1", 2));
        am.processTaskResponse("host2", reports, CommandUtils.convertToTaskIdCommandMap(stage.getOrderedHostRoleCommands()));
        reports.clear();
        reports.add(getCommandReport(COMPLETED, DATANODE, HDFS, "1-1", 3));
        am.processTaskResponse("host3", reports, CommandUtils.convertToTaskIdCommandMap(stage.getOrderedHostRoleCommands()));
        scheduler.doWork();
        Assert.assertEquals(ABORTED, stages.get(1).getHostRoleStatus("host1", "HDFS_CLIENT"));
    }

    @Test
    public void testSuccessFactors() {
        Stage s = StageUtils.getATestStage(1, 1, TestActionScheduler.CLUSTER_HOST_INFO, "{\"host_param\":\"param_value\"}", "{\"stage_param\":\"param_value\"}");
        Assert.assertEquals(new Float(0.5), new Float(s.getSuccessFactor(DATANODE)));
        Assert.assertEquals(new Float(0.5), new Float(s.getSuccessFactor(TASKTRACKER)));
        Assert.assertEquals(new Float(0.5), new Float(s.getSuccessFactor(GANGLIA_MONITOR)));
        Assert.assertEquals(new Float(0.5), new Float(s.getSuccessFactor(HBASE_REGIONSERVER)));
        Assert.assertEquals(new Float(1.0), new Float(s.getSuccessFactor(NAMENODE)));
        Assert.assertEquals(new Float(1.0), new Float(s.getSuccessFactor(GANGLIA_SERVER)));
    }

    @Test
    public void testSuccessCriteria() {
        RoleStats rs1 = new RoleStats(1, ((float) (0.5)));
        rs1.numSucceeded = 1;
        Assert.assertTrue(rs1.isSuccessFactorMet());
        rs1.numSucceeded = 0;
        Assert.assertFalse(rs1.isSuccessFactorMet());
        RoleStats rs2 = new RoleStats(2, ((float) (0.5)));
        rs2.numSucceeded = 1;
        Assert.assertTrue(rs2.isSuccessFactorMet());
        RoleStats rs3 = new RoleStats(3, ((float) (0.5)));
        rs3.numSucceeded = 2;
        Assert.assertTrue(rs2.isSuccessFactorMet());
        rs3.numSucceeded = 1;
        Assert.assertFalse(rs3.isSuccessFactorMet());
        RoleStats rs4 = new RoleStats(3, ((float) (1.0)));
        rs4.numSucceeded = 2;
        Assert.assertFalse(rs3.isSuccessFactorMet());
    }

    /**
     * This test sends verifies that ActionScheduler returns up-to-date cluster host info and caching works correctly.
     */
    @Test
    public void testClusterHostInfoCache() throws Exception {
        Type type = new TypeToken<Map<String, Set<String>>>() {}.getType();
        // Data for stages
        Map<String, Set<String>> clusterHostInfo1 = StageUtils.getGson().fromJson(TestActionScheduler.CLUSTER_HOST_INFO, type);
        int stageId = 1;
        int requestId1 = 1;
        int requestId2 = 2;
        Properties properties = new Properties();
        Configuration conf = new Configuration(properties);
        Clusters fsm = Mockito.mock(Clusters.class);
        Cluster oneClusterMock = Mockito.mock(Cluster.class);
        Service serviceObj = Mockito.mock(Service.class);
        ServiceComponent scomp = Mockito.mock(ServiceComponent.class);
        ServiceComponentHost sch = Mockito.mock(ServiceComponentHost.class);
        UnitOfWork unitOfWork = Mockito.mock(UnitOfWork.class);
        AgentCommandsPublisher agentCommandsPublisher = Mockito.mock(AgentCommandsPublisher.class);
        Mockito.when(fsm.getCluster(ArgumentMatchers.anyString())).thenReturn(oneClusterMock);
        Mockito.when(oneClusterMock.getService(ArgumentMatchers.anyString())).thenReturn(serviceObj);
        Mockito.when(serviceObj.getServiceComponent(ArgumentMatchers.anyString())).thenReturn(scomp);
        Mockito.when(scomp.getServiceComponentHost(ArgumentMatchers.anyString())).thenReturn(sch);
        Mockito.when(serviceObj.getCluster()).thenReturn(oneClusterMock);
        Host host = Mockito.mock(Host.class);
        HashMap<String, ServiceComponentHost> hosts = new HashMap<>();
        hosts.put(hostname, sch);
        Mockito.when(scomp.getServiceComponentHosts()).thenReturn(hosts);
        Mockito.when(fsm.getHost(ArgumentMatchers.anyString())).thenReturn(host);
        Mockito.when(host.getState()).thenReturn(HEALTHY);
        Mockito.when(host.getHostName()).thenReturn(hostname);
        Mockito.when(host.getHostId()).thenReturn(hostId);
        ActionDBAccessor db = Mockito.mock(ActionDBAccessorImpl.class);
        HostRoleCommandDAO hostRoleCommandDAOMock = Mockito.mock(HostRoleCommandDAO.class);
        Mockito.doNothing().when(hostRoleCommandDAOMock).publishTaskCreateEvent(ArgumentMatchers.anyListOf(HostRoleCommand.class));
        RequestEntity request = Mockito.mock(RequestEntity.class);
        Mockito.when(request.isExclusive()).thenReturn(false);
        Mockito.when(request.getClusterHostInfo()).thenReturn(TestActionScheduler.CLUSTER_HOST_INFO);
        Mockito.when(db.getRequestEntity(ArgumentMatchers.anyLong())).thenReturn(request);
        Stage s1 = StageUtils.getATestStage(requestId1, stageId, hostname, "{\"host_param\":\"param_value\"}", "{\"stage_param\":\"param_value\"}");
        Stage s2 = StageUtils.getATestStage(requestId2, stageId, hostname, "{\"host_param\":\"param_value\"}", "{\"stage_param\":\"param_value\"}");
        Mockito.when(db.getCommandsInProgressCount()).thenReturn(1);
        Mockito.when(db.getFirstStageInProgressPerRequest()).thenReturn(Collections.singletonList(s1));
        // Keep large number of attempts so that the task is not expired finally
        // Small action timeout to test rescheduling
        ActionScheduler scheduler = new ActionScheduler(100, 100, db, fsm, 10000, new HostsMap(((String) (null))), unitOfWork, null, conf, entityManagerProviderMock, hostRoleCommandDAOMock, ((HostRoleCommandFactory) (null)), agentCommandsPublisher);
        scheduler.setTaskTimeoutAdjustment(false);
        List<AgentCommand> commands = waitForQueueSize(hostId, agentCommandsPublisher, 1, scheduler);
        Assert.assertTrue(((commands != null) && ((commands.size()) == 1)));
        AgentCommand scheduledCommand = commands.get(0);
        Assert.assertTrue((scheduledCommand instanceof ExecutionCommand));
        Assert.assertEquals((((String.valueOf(requestId1)) + "-") + stageId), getCommandId());
        Assert.assertEquals(clusterHostInfo1, getClusterHostInfo());
        Mockito.when(db.getCommandsInProgressCount()).thenReturn(1);
        Mockito.when(db.getFirstStageInProgressPerRequest()).thenReturn(Collections.singletonList(s2));
        // Verify that ActionSheduler does not return cached value of cluster host info for new requestId
        commands = waitForQueueSize(hostId, agentCommandsPublisher, 1, scheduler);
        Assert.assertTrue(((commands != null) && ((commands.size()) == 1)));
        scheduledCommand = commands.get(0);
        Assert.assertTrue((scheduledCommand instanceof ExecutionCommand));
        Assert.assertEquals((((String.valueOf(requestId2)) + "-") + stageId), getCommandId());
        Assert.assertEquals(clusterHostInfo1, getClusterHostInfo());
    }

    /**
     * Checks what happens when stage has an execution command for
     * host component that has been recently deleted
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testCommandAbortForDeletedComponent() throws Exception {
        Properties properties = new Properties();
        Configuration conf = new Configuration(properties);
        Clusters fsm = Mockito.mock(Clusters.class);
        Cluster oneClusterMock = Mockito.mock(Cluster.class);
        Service serviceObj = Mockito.mock(Service.class);
        ServiceComponent scomp = Mockito.mock(ServiceComponent.class);
        ServiceComponent scWithDeletedSCH = Mockito.mock(ServiceComponent.class);
        ServiceComponentHost sch1 = Mockito.mock(ServiceComponentHost.class);
        String hostname1 = "host1";
        Host host1 = Mockito.mock(Host.class);
        Mockito.when(fsm.getHost(hostname1)).thenReturn(host1);
        Mockito.when(host1.getState()).thenReturn(HEALTHY);
        Mockito.when(host1.getHostName()).thenReturn(hostname1);
        Mockito.when(scomp.getServiceComponentHost(hostname1)).thenReturn(sch1);
        HashMap<String, ServiceComponentHost> hosts = new HashMap<>();
        hosts.put(hostname1, sch1);
        Mockito.when(scomp.getServiceComponentHosts()).thenReturn(hosts);
        HostRoleCommandDAO hostRoleCommandDAO = Mockito.mock(HostRoleCommandDAO.class);
        HostEntity hostEntity = new HostEntity();
        hostEntity.setHostName(hostname1);
        hostDAO.create(hostEntity);
        UnitOfWork unitOfWork = Mockito.mock(UnitOfWork.class);
        Mockito.when(fsm.getCluster(ArgumentMatchers.anyString())).thenReturn(oneClusterMock);
        Mockito.when(oneClusterMock.getService(ArgumentMatchers.anyString())).thenReturn(serviceObj);
        Mockito.when(serviceObj.getServiceComponent(HBASE_MASTER.toString())).thenReturn(scWithDeletedSCH);
        Mockito.when(serviceObj.getServiceComponent(HBASE_REGIONSERVER.toString())).thenReturn(scomp);
        Mockito.when(scWithDeletedSCH.getServiceComponentHost(ArgumentMatchers.anyString())).thenThrow(new ServiceComponentHostNotFoundException("dummyCluster", "dummyService", "dummyComponent", "dummyHostname"));
        Mockito.when(serviceObj.getCluster()).thenReturn(oneClusterMock);
        Stage stage1 = stageFactory.createNew(1, "/tmp", "cluster1", 1L, "stageWith2Tasks", "", "");
        addInstallTaskToStage(stage1, hostname1, "cluster1", HBASE_MASTER, INSTALL, HBASE, 1);
        addInstallTaskToStage(stage1, hostname1, "cluster1", HBASE_REGIONSERVER, INSTALL, HBASE, 2);
        final List<Stage> stages = Collections.singletonList(stage1);
        ActionDBAccessor db = Mockito.mock(ActionDBAccessor.class);
        RequestEntity request = Mockito.mock(RequestEntity.class);
        Mockito.when(request.isExclusive()).thenReturn(false);
        Mockito.when(db.getRequestEntity(ArgumentMatchers.anyLong())).thenReturn(request);
        Mockito.when(db.getCommandsInProgressCount()).thenReturn(stages.size());
        Mockito.when(db.getFirstStageInProgressPerRequest()).thenReturn(stages);
        ActionScheduler scheduler = new ActionScheduler(100, 50000, db, fsm, 3, new HostsMap(((String) (null))), unitOfWork, null, conf, entityManagerProviderMock, hostRoleCommandDAO, ((HostRoleCommandFactory) (null)), null);
        final CountDownLatch abortCalls = new CountDownLatch(2);
        Mockito.doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Long requestId = ((Long) (invocation.getArguments()[0]));
                for (Stage stage : stages) {
                    if (requestId.equals(stage.getRequestId())) {
                        for (HostRoleCommand command : stage.getOrderedHostRoleCommands()) {
                            if ((((command.getStatus()) == (HostRoleStatus.QUEUED)) || ((command.getStatus()) == (HostRoleStatus.IN_PROGRESS))) || ((command.getStatus()) == (HostRoleStatus.PENDING))) {
                                command.setStatus(ABORTED);
                            }
                        }
                    }
                }
                abortCalls.countDown();
                return null;
            }
        }).when(db).abortOperation(ArgumentMatchers.anyLong());
        scheduler.setTaskTimeoutAdjustment(false);
        // Start the thread
        scheduler.start();
        long timeout = 60;
        abortCalls.await(timeout, TimeUnit.SECONDS);
        Assert.assertEquals(ABORTED, stages.get(0).getHostRoleStatus(hostname1, "HBASE_MASTER"));
        Assert.assertEquals(ABORTED, stages.get(0).getHostRoleStatus(hostname1, "HBASE_REGIONSERVER"));
        // If regression occured, scheduler thread would fail with an exception
        // instead of aborting request
        Mockito.verify(db, Mockito.times(2)).abortOperation(ArgumentMatchers.anyLong());
        scheduler.stop();
    }

    @Test
    public void testServerActionWOService() throws Exception {
        Properties properties = new Properties();
        Configuration conf = new Configuration(properties);
        Clusters fsm = Mockito.mock(Clusters.class);
        UnitOfWork unitOfWork = Mockito.mock(UnitOfWork.class);
        AgentCommandsPublisher agentCommandsPublisher = Mockito.mock(AgentCommandsPublisher.class);
        Map<String, String> payload = new HashMap<>();
        final Stage s = getStageWithServerAction(1, 977, payload, "test", 300, false, false);
        List<Stage> stages = Collections.singletonList(s);
        ActionDBAccessor db = Mockito.mock(ActionDBAccessor.class);
        HostRoleCommandDAO hostRoleCommandDAOMock = Mockito.mock(HostRoleCommandDAO.class);
        Mockito.doNothing().when(hostRoleCommandDAOMock).publishTaskCreateEvent(ArgumentMatchers.anyListOf(HostRoleCommand.class));
        RequestEntity request = Mockito.mock(RequestEntity.class);
        Mockito.when(request.getClusterHostInfo()).thenReturn(TestActionScheduler.CLUSTER_HOST_INFO);
        Mockito.when(request.isExclusive()).thenReturn(false);
        Mockito.when(db.getRequestEntity(ArgumentMatchers.anyLong())).thenReturn(request);
        Mockito.when(db.getCommandsInProgressCount()).thenReturn(stages.size());
        Mockito.when(db.getFirstStageInProgressPerRequest()).thenReturn(stages);
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                String host = ((String) (invocation.getArguments()[0]));
                String role = ((String) (invocation.getArguments()[3]));
                CommandReport commandReport = ((CommandReport) (invocation.getArguments()[4]));
                HostRoleCommand command = null;
                if (null == host) {
                    command = s.getHostRoleCommand(null, role);
                } else {
                    command = s.getHostRoleCommand(host, role);
                }
                command.setStatus(HostRoleStatus.valueOf(commandReport.getStatus()));
                return null;
            }
        }).when(db).updateHostRoleState(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyString(), ArgumentMatchers.any(CommandReport.class));
        Mockito.doAnswer(new Answer<List<HostRoleCommand>>() {
            @Override
            public List<HostRoleCommand> answer(InvocationOnMock invocation) throws Throwable {
                String role = ((String) (invocation.getArguments()[0]));
                HostRoleStatus status = ((HostRoleStatus) (invocation.getArguments()[1]));
                HostRoleCommand task = s.getHostRoleCommand(null, role);
                if ((task.getStatus()) == status) {
                    return Arrays.asList(task);
                } else {
                    return Collections.emptyList();
                }
            }
        }).when(db).getTasksByRoleAndStatus(ArgumentMatchers.anyString(), ArgumentMatchers.any(HostRoleStatus.class));
        Mockito.doAnswer(new Answer<HostRoleCommand>() {
            @Override
            public HostRoleCommand answer(InvocationOnMock invocation) throws Throwable {
                return s.getHostRoleCommand(null, "AMBARI_SERVER_ACTION");
            }
        }).when(db).getTask(ArgumentMatchers.anyLong());
        ServerActionExecutor.init(injector);
        ActionScheduler scheduler = new ActionScheduler(100, 50, db, fsm, 3, new HostsMap(((String) (null))), unitOfWork, null, conf, entityManagerProviderMock, hostRoleCommandDAOMock, ((HostRoleCommandFactory) (null)), agentCommandsPublisher);
        int cycleCount = 0;
        while ((!(stages.get(0).getHostRoleStatus(null, "AMBARI_SERVER_ACTION").equals(COMPLETED))) && ((cycleCount++) <= (MAX_CYCLE_ITERATIONS))) {
            scheduler.doWork();
            scheduler.getServerActionExecutor().doWork();
        } 
        Assert.assertEquals(stages.get(0).getHostRoleStatus(null, "AMBARI_SERVER_ACTION"), COMPLETED);
    }

    @Test
    public void testCancelRequests() throws Exception {
        Clusters fsm = Mockito.mock(Clusters.class);
        Cluster oneClusterMock = Mockito.mock(Cluster.class);
        Service serviceObj = Mockito.mock(Service.class);
        ServiceComponent scomp = Mockito.mock(ServiceComponent.class);
        ServiceComponentHost sch = Mockito.mock(ServiceComponentHost.class);
        UnitOfWork unitOfWork = Mockito.mock(UnitOfWork.class);
        AgentCommandsPublisher agentCommandsPublisher = Mockito.mock(AgentCommandsPublisher.class);
        HostRoleCommandDAO hostRoleCommandDAO = Mockito.mock(HostRoleCommandDAO.class);
        HostRoleCommandFactory hostRoleCommandFactory = Mockito.mock(HostRoleCommandFactory.class);
        Mockito.when(fsm.getCluster(ArgumentMatchers.anyString())).thenReturn(oneClusterMock);
        Mockito.when(oneClusterMock.getService(ArgumentMatchers.anyString())).thenReturn(serviceObj);
        Mockito.when(serviceObj.getServiceComponent(ArgumentMatchers.anyString())).thenReturn(scomp);
        Mockito.when(scomp.getServiceComponentHost(ArgumentMatchers.anyString())).thenReturn(sch);
        Mockito.when(serviceObj.getCluster()).thenReturn(oneClusterMock);
        final Long hostId = 1L;
        HostEntity hostEntity = new HostEntity();
        hostEntity.setHostName(hostname);
        hostEntity.setHostId(hostId);
        hostDAO.create(hostEntity);
        HashMap<String, ServiceComponentHost> hosts = new HashMap<>();
        hosts.put(hostname, sch);
        Mockito.when(scomp.getServiceComponentHosts()).thenReturn(hosts);
        // Create a single request with 3 stages, each with a single task - the first stage will be completed and should not
        // be included when cancelling the unfinished tasks of the request
        long requestId = 1;
        final List<Stage> allStages = new ArrayList<>();
        final List<Stage> stagesInProgress = new ArrayList<>();
        final List<Stage> firstStageInProgress = new ArrayList<>();
        final List<HostRoleCommand> tasksInProgress = new ArrayList<>();
        final List<HostRoleCommandEntity> hrcEntitiesInProgress = new ArrayList<>();
        int secondaryNamenodeCmdTaskId = 1;
        int namenodeCmdTaskId = 2;
        int datanodeCmdTaskId = 3;
        Stage stageWithTask = getStageWithSingleTask(hostname, "cluster1", SECONDARY_NAMENODE, START, HDFS, secondaryNamenodeCmdTaskId, 1, ((int) (requestId)));
        // complete the first stage
        stageWithTask.getOrderedHostRoleCommands().get(0).setStatus(COMPLETED);
        allStages.add(stageWithTask);
        stageWithTask = getStageWithSingleTask(hostname, "cluster1", NAMENODE, START, HDFS, namenodeCmdTaskId, 2, ((int) (requestId)));
        tasksInProgress.addAll(stageWithTask.getOrderedHostRoleCommands());
        firstStageInProgress.add(stageWithTask);
        stagesInProgress.add(stageWithTask);
        allStages.add(stageWithTask);
        stageWithTask = getStageWithSingleTask(hostname, "cluster1", DATANODE, START, HDFS, datanodeCmdTaskId, 3, ((int) (requestId)));
        tasksInProgress.addAll(stageWithTask.getOrderedHostRoleCommands());
        stagesInProgress.add(stageWithTask);
        allStages.add(stageWithTask);
        // convert HRC to HRCEntity for the mock DAO to use
        for (HostRoleCommand hostRoleCommand : tasksInProgress) {
            HostRoleCommandEntity entity = Mockito.mock(HostRoleCommandEntity.class);
            Mockito.when(entity.getTaskId()).thenReturn(hostRoleCommand.getTaskId());
            Mockito.when(entity.getStageId()).thenReturn(hostRoleCommand.getStageId());
            Mockito.when(entity.getRequestId()).thenReturn(hostRoleCommand.getRequestId());
            Mockito.when(entity.getHostId()).thenReturn(hostRoleCommand.getHostId());
            Mockito.when(entity.getHostName()).thenReturn(hostRoleCommand.getHostName());
            Mockito.when(entity.getRole()).thenReturn(hostRoleCommand.getRole());
            Mockito.when(entity.getStatus()).thenReturn(hostRoleCommand.getStatus());
            Mockito.when(entity.getRoleCommand()).thenReturn(hostRoleCommand.getRoleCommand());
            hrcEntitiesInProgress.add(entity);
            Mockito.when(hostRoleCommandFactory.createExisting(entity)).thenReturn(hostRoleCommand);
        }
        Host host = Mockito.mock(Host.class);
        Mockito.when(fsm.getHost(ArgumentMatchers.anyString())).thenReturn(host);
        Mockito.when(host.getState()).thenReturn(HEALTHY);
        Mockito.when(host.getHostName()).thenReturn(hostname);
        Mockito.when(host.getHostId()).thenReturn(hostId);
        ActionDBAccessor db = Mockito.mock(ActionDBAccessor.class);
        RequestEntity request = Mockito.mock(RequestEntity.class);
        Mockito.when(request.isExclusive()).thenReturn(false);
        Mockito.when(request.getClusterHostInfo()).thenReturn(TestActionScheduler.CLUSTER_HOST_INFO);
        Mockito.when(db.getRequestEntity(ArgumentMatchers.anyLong())).thenReturn(request);
        Mockito.when(db.getCommandsInProgressCount()).thenReturn(stagesInProgress.size());
        Mockito.when(db.getFirstStageInProgressPerRequest()).thenReturn(stagesInProgress);
        Mockito.when(db.getStagesInProgressForRequest(requestId)).thenReturn(stagesInProgress);
        Mockito.when(db.getAllStages(ArgumentMatchers.anyLong())).thenReturn(allStages);
        List<HostRoleCommand> requestTasks = new ArrayList<>();
        for (Stage stage : allStages) {
            requestTasks.addAll(stage.getOrderedHostRoleCommands());
        }
        Mockito.when(db.getRequestTasks(ArgumentMatchers.anyLong())).thenReturn(requestTasks);
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                List<CommandReport> reports = ((List<CommandReport>) (invocation.getArguments()[0]));
                for (CommandReport report : reports) {
                    String actionId = report.getActionId();
                    long[] requestStageIds = StageUtils.getRequestStage(actionId);
                    Long requestId = requestStageIds[0];
                    Long stageId = requestStageIds[1];
                    Long id = report.getTaskId();
                    for (Stage stage : stagesInProgress) {
                        if ((requestId.equals(stage.getRequestId())) && (stageId.equals(stage.getStageId()))) {
                            for (HostRoleCommand hostRoleCommand : stage.getOrderedHostRoleCommands()) {
                                if ((hostRoleCommand.getTaskId()) == id) {
                                    hostRoleCommand.setStatus(HostRoleStatus.valueOf(report.getStatus()));
                                }
                            }
                        }
                    }
                }
                return null;
            }
        }).when(db).updateHostRoleStates(ArgumentMatchers.anyCollectionOf(CommandReport.class));
        Mockito.when(db.getTask(ArgumentMatchers.anyLong())).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Long taskId = ((Long) (invocation.getArguments()[0]));
                for (Stage stage : allStages) {
                    for (HostRoleCommand command : stage.getOrderedHostRoleCommands()) {
                        if (taskId.equals(command.getTaskId())) {
                            return command;
                        }
                    }
                }
                return null;
            }
        });
        Mockito.doAnswer(new Answer<Collection<HostRoleCommandEntity>>() {
            @Override
            public Collection<HostRoleCommandEntity> answer(InvocationOnMock invocation) throws Throwable {
                Long requestId = ((Long) (invocation.getArguments()[0]));
                List<HostRoleCommandEntity> abortedCommands = Lists.newArrayList();
                for (Stage stage : stagesInProgress) {
                    if (requestId.equals(stage.getRequestId())) {
                        for (HostRoleCommand command : stage.getOrderedHostRoleCommands()) {
                            if ((((command.getStatus()) == (HostRoleStatus.QUEUED)) || ((command.getStatus()) == (HostRoleStatus.IN_PROGRESS))) || ((command.getStatus()) == (HostRoleStatus.PENDING))) {
                                command.setStatus(ABORTED);
                                HostRoleCommandEntity hostRoleCommandEntity = command.constructNewPersistenceEntity();
                                hostRoleCommandEntity.setStage(stage.constructNewPersistenceEntity());
                                abortedCommands.add(hostRoleCommandEntity);
                            }
                        }
                    }
                }
                return abortedCommands;
            }
        }).when(db).abortOperation(ArgumentMatchers.anyLong());
        Map<Long, List<AgentCommand>> commands = new HashMap<>();
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Long hostId = ((Long) (invocation.getArguments()[0]));
                if (!(commands.containsKey(hostId))) {
                    commands.put(hostId, new ArrayList());
                }
                commands.get(hostId).add(((AgentCommand) (invocation.getArguments()[1])));
                return null;
            }
        }).when(agentCommandsPublisher).sendAgentCommand(ArgumentMatchers.anyLong(), ArgumentMatchers.any(AgentCommand.class));
        Properties properties = new Properties();
        Configuration conf = new Configuration(properties);
        Mockito.when(hostRoleCommandDAO.findByRequestIdAndStatuses(requestId, NOT_COMPLETED_STATUSES)).thenReturn(hrcEntitiesInProgress);
        ActionScheduler scheduler = new ActionScheduler(100, 50, db, fsm, 3, new HostsMap(((String) (null))), unitOfWork, null, conf, entityManagerProviderMock, hostRoleCommandDAO, hostRoleCommandFactory, agentCommandsPublisher);
        scheduler.doWork();
        String reason = "Some reason";
        scheduler.scheduleCancellingRequest(requestId, reason);
        scheduler.doWork();
        Assert.assertEquals(COMPLETED, allStages.get(0).getHostRoleStatus(hostname, "SECONDARY_NAMENODE"));
        Assert.assertEquals(ABORTED, allStages.get(1).getHostRoleStatus(hostname, "NAMENODE"));
        Assert.assertEquals(ABORTED, allStages.get(2).getHostRoleStatus(hostname, "DATANODE"));
        Assert.assertEquals(1, commands.get(hostId).size());// Cancel commands should be generated only for 1 stage

        CancelCommand cancelCommand = ((CancelCommand) (commands.get(hostId).get(0)));
        Assert.assertEquals(cancelCommand.getTargetTaskId(), namenodeCmdTaskId);
        Assert.assertEquals(cancelCommand.getReason(), reason);
    }

    @Test
    public void testExclusiveRequests() throws Exception {
        Clusters fsm = Mockito.mock(Clusters.class);
        Cluster oneClusterMock = Mockito.mock(Cluster.class);
        Service serviceObj = Mockito.mock(Service.class);
        ServiceComponent scomp = Mockito.mock(ServiceComponent.class);
        ServiceComponentHost sch = Mockito.mock(ServiceComponentHost.class);
        UnitOfWork unitOfWork = Mockito.mock(UnitOfWork.class);
        AgentCommandsPublisher agentCommandsPublisher = Mockito.mock(AgentCommandsPublisher.class);
        Mockito.when(fsm.getCluster(ArgumentMatchers.anyString())).thenReturn(oneClusterMock);
        Mockito.when(oneClusterMock.getService(ArgumentMatchers.anyString())).thenReturn(serviceObj);
        Mockito.when(serviceObj.getServiceComponent(ArgumentMatchers.anyString())).thenReturn(scomp);
        Mockito.when(scomp.getServiceComponentHost(ArgumentMatchers.anyString())).thenReturn(sch);
        Mockito.when(serviceObj.getCluster()).thenReturn(oneClusterMock);
        HashMap<String, ServiceComponentHost> hosts = new HashMap<>();
        String hostname1 = "hostname1";
        String hostname2 = "hostname2";
        String hostname3 = "hostname3";
        hosts.put(hostname1, sch);
        hosts.put(hostname2, sch);
        hosts.put(hostname3, sch);
        Mockito.when(scomp.getServiceComponentHosts()).thenReturn(hosts);
        long requestId1 = 1;
        long requestId2 = 2;
        long requestId3 = 3;
        final List<Stage> firstStageInProgressByRequest = new ArrayList<>();
        final List<Stage> stagesInProgress = new ArrayList<>();
        int namenodeCmdTaskId = 1;
        Stage request1Stage1 = getStageWithSingleTask(hostname1, "cluster1", NAMENODE, START, HDFS, namenodeCmdTaskId, 1, ((int) (requestId1)));
        Stage request1Stage2 = getStageWithSingleTask(hostname1, "cluster1", DATANODE, START, HDFS, 2, 2, ((int) (requestId1)));
        Stage request2Stage1 = // Exclusive
        getStageWithSingleTask(hostname2, "cluster1", DATANODE, STOP, HDFS, 3, 3, ((int) (requestId2)));
        Stage request3Stage1 = getStageWithSingleTask(hostname3, "cluster1", DATANODE, START, HDFS, 4, 4, ((int) (requestId3)));
        firstStageInProgressByRequest.add(request1Stage1);
        firstStageInProgressByRequest.add(request2Stage1);
        firstStageInProgressByRequest.add(request3Stage1);
        stagesInProgress.add(request1Stage1);
        stagesInProgress.add(request1Stage2);
        stagesInProgress.add(request2Stage1);
        stagesInProgress.add(request3Stage1);
        Host host1 = Mockito.mock(Host.class);
        Mockito.when(fsm.getHost(ArgumentMatchers.anyString())).thenReturn(host1);
        Mockito.when(host1.getState()).thenReturn(HEALTHY);
        Mockito.when(host1.getHostName()).thenReturn(hostname);
        Host host2 = Mockito.mock(Host.class);
        Mockito.when(fsm.getHost(ArgumentMatchers.anyString())).thenReturn(host2);
        Mockito.when(host2.getState()).thenReturn(HEALTHY);
        Mockito.when(host2.getHostName()).thenReturn(hostname);
        Host host3 = Mockito.mock(Host.class);
        Mockito.when(fsm.getHost(ArgumentMatchers.anyString())).thenReturn(host3);
        Mockito.when(host3.getState()).thenReturn(HEALTHY);
        Mockito.when(host3.getHostName()).thenReturn(hostname);
        ActionDBAccessor db = Mockito.mock(ActionDBAccessor.class);
        HostRoleCommandDAO hostRoleCommandDAOMock = Mockito.mock(HostRoleCommandDAO.class);
        Mockito.doNothing().when(hostRoleCommandDAOMock).publishTaskCreateEvent(ArgumentMatchers.anyListOf(HostRoleCommand.class));
        Mockito.when(db.getCommandsInProgressCount()).thenReturn(stagesInProgress.size());
        Mockito.when(db.getFirstStageInProgressPerRequest()).thenReturn(firstStageInProgressByRequest);
        List<HostRoleCommand> requestTasks = new ArrayList<>();
        for (Stage stage : stagesInProgress) {
            requestTasks.addAll(stage.getOrderedHostRoleCommands());
        }
        Mockito.when(db.getRequestTasks(ArgumentMatchers.anyLong())).thenReturn(requestTasks);
        Mockito.when(db.getAllStages(ArgumentMatchers.anyLong())).thenReturn(stagesInProgress);
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                List<CommandReport> reports = ((List<CommandReport>) (invocation.getArguments()[0]));
                for (CommandReport report : reports) {
                    String actionId = report.getActionId();
                    long[] requestStageIds = StageUtils.getRequestStage(actionId);
                    Long requestId = requestStageIds[0];
                    Long stageId = requestStageIds[1];
                    Long id = report.getTaskId();
                    for (Stage stage : stagesInProgress) {
                        if ((requestId.equals(stage.getRequestId())) && (stageId.equals(stage.getStageId()))) {
                            for (HostRoleCommand hostRoleCommand : stage.getOrderedHostRoleCommands()) {
                                if ((hostRoleCommand.getTaskId()) == id) {
                                    hostRoleCommand.setStatus(HostRoleStatus.valueOf(report.getStatus()));
                                }
                            }
                        }
                    }
                }
                return null;
            }
        }).when(db).updateHostRoleStates(ArgumentMatchers.anyCollectionOf(CommandReport.class));
        Mockito.when(db.getTask(ArgumentMatchers.anyLong())).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Long taskId = ((Long) (invocation.getArguments()[0]));
                for (Stage stage : stagesInProgress) {
                    for (HostRoleCommand command : stage.getOrderedHostRoleCommands()) {
                        if (taskId.equals(command.getTaskId())) {
                            return command;
                        }
                    }
                }
                return null;
            }
        });
        final Map<Long, Boolean> startedRequests = new HashMap<>();
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                startedRequests.put(((Long) (invocation.getArguments()[0])), true);
                return null;
            }
        }).when(db).startRequest(ArgumentMatchers.anyLong());
        RequestEntity request1 = Mockito.mock(RequestEntity.class);
        Mockito.when(request1.isExclusive()).thenReturn(false);
        Mockito.when(request1.getClusterHostInfo()).thenReturn(TestActionScheduler.CLUSTER_HOST_INFO);
        RequestEntity request2 = Mockito.mock(RequestEntity.class);
        Mockito.when(request2.isExclusive()).thenReturn(true);
        Mockito.when(request2.getClusterHostInfo()).thenReturn(TestActionScheduler.CLUSTER_HOST_INFO);
        RequestEntity request3 = Mockito.mock(RequestEntity.class);
        Mockito.when(request3.isExclusive()).thenReturn(false);
        Mockito.when(request3.getClusterHostInfo()).thenReturn(TestActionScheduler.CLUSTER_HOST_INFO);
        Mockito.when(db.getRequestEntity(requestId1)).thenReturn(request1);
        Mockito.when(db.getRequestEntity(requestId2)).thenReturn(request2);
        Mockito.when(db.getRequestEntity(requestId3)).thenReturn(request3);
        Properties properties = new Properties();
        Configuration conf = new Configuration(properties);
        ActionScheduler scheduler = Mockito.spy(new ActionScheduler(100, 50, db, fsm, 3, new HostsMap(((String) (null))), unitOfWork, null, conf, entityManagerProviderMock, hostRoleCommandDAOMock, ((HostRoleCommandFactory) (null)), agentCommandsPublisher));
        Mockito.doReturn(false).when(scheduler).wasAgentRestartedDuringOperation(ArgumentMatchers.any(Host.class), ArgumentMatchers.any(Stage.class), ArgumentMatchers.anyString());
        // Execution of request 1
        scheduler.doWork();
        Assert.assertTrue(startedRequests.containsKey(requestId1));
        Assert.assertFalse(startedRequests.containsKey(requestId2));
        Assert.assertFalse(startedRequests.containsKey(requestId3));
        stagesInProgress.remove(0);
        firstStageInProgressByRequest.clear();
        firstStageInProgressByRequest.add(request1Stage2);
        firstStageInProgressByRequest.add(request2Stage1);
        firstStageInProgressByRequest.add(request3Stage1);
        scheduler.doWork();
        Assert.assertTrue(startedRequests.containsKey(requestId1));
        Assert.assertFalse(startedRequests.containsKey(requestId2));
        Assert.assertFalse(startedRequests.containsKey(requestId3));
        // Execution of request 2
        stagesInProgress.remove(0);
        firstStageInProgressByRequest.clear();
        firstStageInProgressByRequest.add(request2Stage1);
        firstStageInProgressByRequest.add(request3Stage1);
        scheduler.doWork();
        Assert.assertTrue(startedRequests.containsKey(requestId1));
        Assert.assertTrue(startedRequests.containsKey(requestId2));
        Assert.assertFalse(startedRequests.containsKey(requestId3));
        // Execution of request 3
        stagesInProgress.remove(0);
        firstStageInProgressByRequest.clear();
        firstStageInProgressByRequest.add(request3Stage1);
        scheduler.doWork();
        Assert.assertTrue(startedRequests.containsKey(requestId1));
        Assert.assertTrue(startedRequests.containsKey(requestId2));
        Assert.assertTrue(startedRequests.containsKey(requestId3));
    }

    @Test
    public void testAbortHolding() throws AmbariException {
        UnitOfWork unitOfWork = EasyMock.createMock(UnitOfWork.class);
        ActionDBAccessor db = EasyMock.createMock(ActionDBAccessor.class);
        Clusters fsm = EasyMock.createMock(Clusters.class);
        Configuration conf = new Configuration(new Properties());
        HostEntity hostEntity1 = new HostEntity();
        hostEntity1.setHostName("h1");
        hostDAO.merge(hostEntity1);
        db.abortHostRole("h1", (-1L), (-1L), "AMBARI_SERVER_ACTION");
        EasyMock.expectLastCall();
        EasyMock.replay(db);
        ActionScheduler scheduler = new ActionScheduler(100, 50, db, fsm, 3, new HostsMap(((String) (null))), unitOfWork, null, conf, entityManagerProviderMock, ((HostRoleCommandDAO) (null)), ((HostRoleCommandFactory) (null)), null);
        HostRoleCommand hrc1 = hostRoleCommandFactory.create("h1", NAMENODE, null, EXECUTE);
        hrc1.setStatus(COMPLETED);
        HostRoleCommand hrc3 = hostRoleCommandFactory.create("h1", AMBARI_SERVER_ACTION, null, CUSTOM_COMMAND);
        hrc3.setStatus(HOLDING);
        HostRoleCommand hrc4 = hostRoleCommandFactory.create("h1", FLUME_HANDLER, null, EXECUTE);
        hrc4.setStatus(PENDING);
        List<HostRoleCommand> hostRoleCommands = Arrays.asList(hrc1, hrc3, hrc4);
        scheduler.cancelHostRoleCommands(hostRoleCommands, "foo");
        EasyMock.verify(db);
    }

    @Test
    public void testAbortAmbariServerAction() throws AmbariException {
        UnitOfWork unitOfWork = EasyMock.createMock(UnitOfWork.class);
        ActionDBAccessor db = EasyMock.createMock(ActionDBAccessor.class);
        Clusters fsm = EasyMock.createMock(Clusters.class);
        Configuration conf = new Configuration(new Properties());
        HostEntity hostEntity1 = new HostEntity();
        hostEntity1.setHostName("h1");
        hostDAO.merge(hostEntity1);
        EasyMock.replay(db);
        ActionScheduler scheduler = new ActionScheduler(100, 50, db, fsm, 3, new HostsMap(((String) (null))), unitOfWork, null, conf, entityManagerProviderMock, ((HostRoleCommandDAO) (null)), ((HostRoleCommandFactory) (null)), null);
        HostRoleCommand hrc1 = hostRoleCommandFactory.create("h1", NAMENODE, null, EXECUTE);
        hrc1.setStatus(COMPLETED);
        HostRoleCommand hrc3 = hostRoleCommandFactory.create(null, AMBARI_SERVER_ACTION, null, CUSTOM_COMMAND);
        hrc3.setStatus(IN_PROGRESS);
        HostRoleCommand hrc4 = hostRoleCommandFactory.create("h1", FLUME_HANDLER, null, EXECUTE);
        hrc4.setStatus(PENDING);
        List<HostRoleCommand> hostRoleCommands = Arrays.asList(hrc1, hrc3, hrc4);
        scheduler.cancelHostRoleCommands(hostRoleCommands, "foo");
        EasyMock.verify(db);
    }

    /**
     * Tests that command failures in skippable stages do not cause the request to
     * be aborted.
     */
    @Test
    public void testSkippableCommandFailureDoesNotAbortRequest() throws Exception {
        Properties properties = new Properties();
        Configuration conf = new Configuration(properties);
        Clusters fsm = Mockito.mock(Clusters.class);
        Cluster oneClusterMock = Mockito.mock(Cluster.class);
        Host host = Mockito.mock(Host.class);
        Service serviceObj = Mockito.mock(Service.class);
        ServiceComponent scomp = Mockito.mock(ServiceComponent.class);
        ServiceComponentHost sch = Mockito.mock(ServiceComponentHost.class);
        UnitOfWork unitOfWork = Mockito.mock(UnitOfWork.class);
        AgentCommandsPublisher agentCommandsPublisher = Mockito.mock(AgentCommandsPublisher.class);
        Mockito.when(fsm.getCluster(ArgumentMatchers.anyString())).thenReturn(oneClusterMock);
        Mockito.when(fsm.getHost(ArgumentMatchers.anyString())).thenReturn(host);
        Mockito.when(host.getHostId()).thenReturn(1L);
        Mockito.when(host.getState()).thenReturn(HEALTHY);
        Mockito.when(oneClusterMock.getService(ArgumentMatchers.anyString())).thenReturn(serviceObj);
        Mockito.when(serviceObj.getServiceComponent(ArgumentMatchers.anyString())).thenReturn(scomp);
        Mockito.when(scomp.getServiceComponentHost(ArgumentMatchers.anyString())).thenReturn(sch);
        Mockito.when(serviceObj.getCluster()).thenReturn(oneClusterMock);
        String hostname1 = "ahost.ambari.apache.org";
        HashMap<String, ServiceComponentHost> hosts = new HashMap<>();
        hosts.put(hostname1, sch);
        Mockito.when(scomp.getServiceComponentHosts()).thenReturn(hosts);
        // create 1 stage with 2 commands and then another stage with 1 command
        Stage stage = null;
        Stage stage2 = null;
        final List<Stage> stages = new ArrayList<>();
        final List<Stage> firstStageInProgress = new ArrayList<>();
        stages.add((stage = getStageWithSingleTask(hostname1, "cluster1", NAMENODE, STOP, HDFS, 1, 1, 1)));
        addInstallTaskToStage(stage, hostname1, "cluster1", HBASE_MASTER, INSTALL, HBASE, 1);
        stages.add((stage2 = getStageWithSingleTask(hostname1, "cluster1", DATANODE, STOP, HDFS, 1, 1, 1)));
        // !!! this is the test; make the stages skippable so that when their
        // commands fail, the entire request is not aborted
        for (Stage stageToMakeSkippable : stages) {
            stageToMakeSkippable.setSkippable(true);
        }
        // fail the first task - normally this would cause an abort, exception that our stages
        // are skippable now so it should not
        HostRoleCommand command = stage.getOrderedHostRoleCommands().iterator().next();
        command.setStatus(FAILED);
        // still in progress even though 1 task has been failed
        firstStageInProgress.add(stage);
        ActionDBAccessor db = Mockito.mock(ActionDBAccessor.class);
        HostRoleCommandDAO hostRoleCommandDAOMock = Mockito.mock(HostRoleCommandDAO.class);
        Mockito.doNothing().when(hostRoleCommandDAOMock).publishTaskCreateEvent(ArgumentMatchers.anyListOf(HostRoleCommand.class));
        RequestEntity request = Mockito.mock(RequestEntity.class);
        Mockito.when(request.getClusterHostInfo()).thenReturn(TestActionScheduler.CLUSTER_HOST_INFO);
        Mockito.when(request.isExclusive()).thenReturn(false);
        Mockito.when(db.getRequestEntity(ArgumentMatchers.anyLong())).thenReturn(request);
        Mockito.when(db.getCommandsInProgressCount()).thenReturn(firstStageInProgress.size());
        Mockito.when(db.getFirstStageInProgressPerRequest()).thenReturn(firstStageInProgress);
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                List<CommandReport> reports = ((List<CommandReport>) (invocation.getArguments()[0]));
                for (CommandReport report : reports) {
                    String actionId = report.getActionId();
                    long[] requestStageIds = StageUtils.getRequestStage(actionId);
                    Long requestId = requestStageIds[0];
                    Long stageId = requestStageIds[1];
                    Long id = report.getTaskId();
                    for (Stage stage : stages) {
                        if ((requestId.equals(stage.getRequestId())) && (stageId.equals(stage.getStageId()))) {
                            for (HostRoleCommand hostRoleCommand : stage.getOrderedHostRoleCommands()) {
                                if ((hostRoleCommand.getTaskId()) == id) {
                                    hostRoleCommand.setStatus(HostRoleStatus.valueOf(report.getStatus()));
                                }
                            }
                        }
                    }
                }
                return null;
            }
        }).when(db).updateHostRoleStates(ArgumentMatchers.anyCollectionOf(CommandReport.class));
        Mockito.when(db.getTask(ArgumentMatchers.anyLong())).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Long taskId = ((Long) (invocation.getArguments()[0]));
                for (Stage stage : stages) {
                    for (HostRoleCommand command : stage.getOrderedHostRoleCommands()) {
                        if (taskId.equals(command.getTaskId())) {
                            return command;
                        }
                    }
                }
                return null;
            }
        });
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Long requestId = ((Long) (invocation.getArguments()[0]));
                for (Stage stage : stages) {
                    if (requestId.equals(stage.getRequestId())) {
                        for (HostRoleCommand command : stage.getOrderedHostRoleCommands()) {
                            if ((((command.getStatus()) == (HostRoleStatus.QUEUED)) || ((command.getStatus()) == (HostRoleStatus.IN_PROGRESS))) || ((command.getStatus()) == (HostRoleStatus.PENDING))) {
                                command.setStatus(ABORTED);
                            }
                        }
                    }
                }
                return null;
            }
        }).when(db).abortOperation(ArgumentMatchers.anyLong());
        ActionScheduler scheduler = Mockito.spy(new ActionScheduler(100, 50, db, fsm, 3, new HostsMap(((String) (null))), unitOfWork, null, conf, entityManagerProviderMock, hostRoleCommandDAOMock, ((HostRoleCommandFactory) (null)), agentCommandsPublisher));
        Mockito.doReturn(false).when(scheduler).wasAgentRestartedDuringOperation(ArgumentMatchers.any(Host.class), ArgumentMatchers.any(Stage.class), ArgumentMatchers.anyString());
        scheduler.doWork();
        Assert.assertEquals(FAILED, stages.get(0).getHostRoleStatus(hostname1, "NAMENODE"));
        // the remaining tasks should NOT have been aborted since the stage is
        // skippable - these tasks would normally be ABORTED if the stage was not
        // skippable
        Assert.assertEquals(QUEUED, stages.get(0).getHostRoleStatus(hostname1, "HBASE_MASTER"));
        Assert.assertEquals(PENDING, stages.get(1).getHostRoleStatus(hostname1, "DATANODE"));
        EasyMock.verify(entityManagerProviderMock);
    }

    @Test
    public void testSkippableCommandFailureDoesNotAbortNextStage() throws Exception {
        Stage previousStage = createMock(Stage.class);
        Stage nextStage = createMock(Stage.class);
        ActionDBAccessor actionDBAccessor = createMock(ActionDBAccessor.class);
        expect(previousStage.isSkippable()).andReturn(false);
        expect(nextStage.getStageId()).andReturn(5L);
        expect(nextStage.getRequestId()).andReturn(1L);
        expect(actionDBAccessor.getStage("1-4")).andReturn(previousStage);
        Map<String, HostRoleCommand> roleCommandMap = new HashMap<>();
        HostRoleCommand hostRoleCommand = createMock(HostRoleCommand.class);
        expect(hostRoleCommand.getRole()).andReturn(DATANODE).anyTimes();
        expect(hostRoleCommand.getStatus()).andReturn(SKIPPED_FAILED);
        roleCommandMap.put(DATANODE.toString(), hostRoleCommand);
        Map<String, Map<String, HostRoleCommand>> hostRoleCommands = new HashMap<>();
        hostRoleCommands.put("host", roleCommandMap);
        expect(previousStage.getHostRoleCommands()).andReturn(hostRoleCommands).anyTimes();
        expect(previousStage.getSuccessFactor(DATANODE)).andReturn(0.5F);
        ActionScheduler scheduler = new ActionScheduler(100, 50, actionDBAccessor, null, 3, new HostsMap(((String) (null))), null, null, null, entityManagerProviderMock, ((HostRoleCommandDAO) (null)), ((HostRoleCommandFactory) (null)), null, null);
        replay(previousStage, nextStage, actionDBAccessor, hostRoleCommand);
        Method method = scheduler.getClass().getDeclaredMethod("hasPreviousStageFailed", Stage.class);
        method.setAccessible(true);
        Object result = method.invoke(scheduler, nextStage);
        Assert.assertFalse(((Boolean) (result)));
        EasyMock.verify(previousStage, nextStage, actionDBAccessor, hostRoleCommand);
    }

    @Test
    public void testPreviousStageToFailForFirstStage() throws Exception {
        Stage nextStage = createNiceMock(Stage.class);
        expect(nextStage.getStageId()).andReturn(0L);
        ActionScheduler scheduler = new ActionScheduler(100, 50, null, null, 3, new HostsMap(((String) (null))), null, null, null, entityManagerProviderMock, null, null, null);
        replay(nextStage);
        Method method = scheduler.getClass().getDeclaredMethod("hasPreviousStageFailed", Stage.class);
        method.setAccessible(true);
        Object result = method.invoke(scheduler, nextStage);
        Assert.assertFalse(((Boolean) (result)));
        EasyMock.verify(nextStage);
    }

    @Test
    public void testPreviousStageToFailForSecondStage() throws Exception {
        Stage previousStage = createMock(Stage.class);
        Stage nextStage = createMock(Stage.class);
        ActionDBAccessor actionDBAccessor = createMock(ActionDBAccessor.class);
        expect(previousStage.isSkippable()).andReturn(false);
        expect(nextStage.getStageId()).andReturn(1L);
        expect(nextStage.getRequestId()).andReturn(1L);
        expect(actionDBAccessor.getStage("1-0")).andReturn(previousStage);
        Map<String, HostRoleCommand> roleCommandMap = new HashMap<>();
        HostRoleCommand hostRoleCommand = createMock(HostRoleCommand.class);
        expect(hostRoleCommand.getRole()).andReturn(DATANODE).anyTimes();
        expect(hostRoleCommand.getStatus()).andReturn(FAILED);
        roleCommandMap.put(DATANODE.toString(), hostRoleCommand);
        Map<String, Map<String, HostRoleCommand>> hostRoleCommands = new HashMap<>();
        hostRoleCommands.put("host", roleCommandMap);
        expect(previousStage.getHostRoleCommands()).andReturn(hostRoleCommands).anyTimes();
        expect(previousStage.getSuccessFactor(DATANODE)).andReturn(0.5F);
        ActionScheduler scheduler = new ActionScheduler(100, 50, actionDBAccessor, null, 3, new HostsMap(((String) (null))), null, null, null, entityManagerProviderMock, null, null, null);
        replay(previousStage, nextStage, actionDBAccessor, hostRoleCommand);
        Method method = scheduler.getClass().getDeclaredMethod("hasPreviousStageFailed", Stage.class);
        method.setAccessible(true);
        Object result = method.invoke(scheduler, nextStage);
        Assert.assertTrue(((Boolean) (result)));
        EasyMock.verify(previousStage, nextStage, actionDBAccessor, hostRoleCommand);
    }

    public static class MockModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(Clusters.class).toInstance(Mockito.mock(Clusters.class));
        }
    }
}

