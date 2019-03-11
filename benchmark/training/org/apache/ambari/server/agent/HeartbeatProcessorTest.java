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
package org.apache.ambari.server.agent;


import HostRoleStatus.COMPLETED;
import HostRoleStatus.IN_PROGRESS;
import HostRoleStatus.QUEUED;
import HostState.UNHEALTHY;
import HostStatus.Status;
import Role.DATANODE;
import RoleCommand.ACTIONEXECUTE;
import State.INSTALLING;
import State.UPGRADING;
import com.google.gson.JsonObject;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.ambari.server.AmbariException;
import org.apache.ambari.server.actionmanager.ActionDBAccessor;
import org.apache.ambari.server.actionmanager.ActionDBAccessorImpl;
import org.apache.ambari.server.actionmanager.ActionManager;
import org.apache.ambari.server.actionmanager.ActionManagerTestHelper;
import org.apache.ambari.server.actionmanager.HostRoleCommand;
import org.apache.ambari.server.actionmanager.HostRoleCommandFactory;
import org.apache.ambari.server.actionmanager.Stage;
import org.apache.ambari.server.actionmanager.StageFactory;
import org.apache.ambari.server.api.services.AmbariMetaInfo;
import org.apache.ambari.server.configuration.Configuration;
import org.apache.ambari.server.orm.InMemoryDefaultTestModule;
import org.apache.ambari.server.orm.OrmTestHelper;
import org.apache.ambari.server.orm.dao.HostDAO;
import org.apache.ambari.server.orm.dao.RepositoryVersionDAO;
import org.apache.ambari.server.orm.entities.RepositoryVersionEntity;
import org.apache.ambari.server.security.encryption.Encryptor;
import org.apache.ambari.server.state.Alert;
import org.apache.ambari.server.state.AlertState;
import org.apache.ambari.server.state.Cluster;
import org.apache.ambari.server.state.Clusters;
import org.apache.ambari.server.state.Host;
import org.apache.ambari.server.state.Service;
import org.apache.ambari.server.state.ServiceComponentHost;
import org.apache.ambari.server.state.StackId;
import org.apache.ambari.server.utils.CommandUtils;
import org.apache.ambari.server.utils.EventBusSynchronizer;
import org.apache.ambari.server.utils.StageUtils;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;


public class HeartbeatProcessorTest {
    private Injector injector;

    private long requestId = 23;

    private long stageId = 31;

    @Inject
    private Clusters clusters;

    @Inject
    Configuration config;

    @Inject
    private ActionDBAccessor actionDBAccessor;

    @Inject
    private HeartbeatTestHelper heartbeatTestHelper;

    @Inject
    private ActionManagerTestHelper actionManagerTestHelper;

    @Inject
    private HostRoleCommandFactory hostRoleCommandFactory;

    @Inject
    private HostDAO hostDAO;

    @Inject
    private StageFactory stageFactory;

    @Inject
    private AmbariMetaInfo metaInfo;

    @Inject
    private OrmTestHelper helper;

    public HeartbeatProcessorTest() {
        InMemoryDefaultTestModule module = HeartbeatTestHelper.getTestModule();
        injector = Guice.createInjector(module);
    }

    @Test
    public void testCommandReport() throws AmbariException {
        injector.injectMembers(this);
        clusters.addHost(DummyHeartbeatConstants.DummyHostname1);
        StackId dummyStackId = new StackId(DummyHeartbeatConstants.DummyStackId);
        clusters.addCluster(DummyHeartbeatConstants.DummyCluster, dummyStackId);
        ActionDBAccessor db = injector.getInstance(ActionDBAccessorImpl.class);
        ActionManager am = injector.getInstance(ActionManager.class);
        heartbeatTestHelper.populateActionDB(db, DummyHeartbeatConstants.DummyHostname1, requestId, stageId);
        Stage stage = db.getAllStages(requestId).get(0);
        Assert.assertEquals(stageId, stage.getStageId());
        stage.setHostRoleStatus(DummyHeartbeatConstants.DummyHostname1, DummyHeartbeatConstants.HBASE_MASTER, QUEUED);
        db.hostRoleScheduled(stage, DummyHeartbeatConstants.DummyHostname1, DummyHeartbeatConstants.HBASE_MASTER);
        List<CommandReport> reports = new ArrayList<>();
        CommandReport cr = new CommandReport();
        cr.setActionId(StageUtils.getActionId(requestId, stageId));
        cr.setTaskId(1);
        cr.setRole(DummyHeartbeatConstants.HBASE_MASTER);
        cr.setStatus("COMPLETED");
        cr.setStdErr("");
        cr.setStdOut("");
        cr.setExitCode(215);
        reports.add(cr);
        am.processTaskResponse(DummyHeartbeatConstants.DummyHostname1, reports, CommandUtils.convertToTaskIdCommandMap(stage.getOrderedHostRoleCommands()));
        Assert.assertEquals(215, am.getAction(requestId, stageId).getExitCode(DummyHeartbeatConstants.DummyHostname1, DummyHeartbeatConstants.HBASE_MASTER));
        Assert.assertEquals(COMPLETED, am.getAction(requestId, stageId).getHostRoleStatus(DummyHeartbeatConstants.DummyHostname1, DummyHeartbeatConstants.HBASE_MASTER));
        Stage s = db.getAllStages(requestId).get(0);
        Assert.assertEquals(COMPLETED, s.getHostRoleStatus(DummyHeartbeatConstants.DummyHostname1, DummyHeartbeatConstants.HBASE_MASTER));
        Assert.assertEquals(215, s.getExitCode(DummyHeartbeatConstants.DummyHostname1, DummyHeartbeatConstants.HBASE_MASTER));
    }

    @Test
    public void testComponentUpgradeInProgressReport() throws Exception {
        Cluster cluster = heartbeatTestHelper.getDummyCluster();
        Service hdfs = addService(cluster, DummyHeartbeatConstants.HDFS);
        hdfs.addServiceComponent(DummyHeartbeatConstants.DATANODE);
        hdfs.getServiceComponent(DummyHeartbeatConstants.DATANODE).addServiceComponentHost(DummyHeartbeatConstants.DummyHostname1);
        hdfs.addServiceComponent(DummyHeartbeatConstants.NAMENODE);
        hdfs.getServiceComponent(DummyHeartbeatConstants.NAMENODE).addServiceComponentHost(DummyHeartbeatConstants.DummyHostname1);
        hdfs.addServiceComponent(DummyHeartbeatConstants.HDFS_CLIENT);
        hdfs.getServiceComponent(DummyHeartbeatConstants.HDFS_CLIENT).addServiceComponentHost(DummyHeartbeatConstants.DummyHostname1);
        ServiceComponentHost serviceComponentHost1 = clusters.getCluster(DummyHeartbeatConstants.DummyCluster).getService(DummyHeartbeatConstants.HDFS).getServiceComponent(DummyHeartbeatConstants.DATANODE).getServiceComponentHost(DummyHeartbeatConstants.DummyHostname1);
        ServiceComponentHost serviceComponentHost2 = clusters.getCluster(DummyHeartbeatConstants.DummyCluster).getService(DummyHeartbeatConstants.HDFS).getServiceComponent(DummyHeartbeatConstants.NAMENODE).getServiceComponentHost(DummyHeartbeatConstants.DummyHostname1);
        StackId stack130 = new StackId("HDP-1.3.0");
        StackId stack120 = new StackId("HDP-1.2.0");
        serviceComponentHost1.setState(UPGRADING);
        serviceComponentHost2.setState(INSTALLING);
        HeartBeat hb = new HeartBeat();
        hb.setTimestamp(System.currentTimeMillis());
        hb.setResponseId(0);
        hb.setHostname(DummyHeartbeatConstants.DummyHostname1);
        hb.setNodeStatus(new HostStatus(Status.HEALTHY, DummyHeartbeatConstants.DummyHostStatus));
        CommandReport cr1 = new CommandReport();
        cr1.setActionId(StageUtils.getActionId(requestId, stageId));
        cr1.setTaskId(1);
        // cr1.setClusterName(DummyCluster);
        cr1.setServiceName(DummyHeartbeatConstants.HDFS);
        cr1.setRole(DummyHeartbeatConstants.DATANODE);
        cr1.setRoleCommand("INSTALL");
        cr1.setStatus(IN_PROGRESS.toString());
        cr1.setStdErr("none");
        cr1.setStdOut("dummy output");
        cr1.setExitCode(777);
        CommandReport cr2 = new CommandReport();
        cr2.setActionId(StageUtils.getActionId(requestId, stageId));
        cr2.setTaskId(2);
        // cr2.setClusterName(DummyCluster);
        cr2.setServiceName(DummyHeartbeatConstants.HDFS);
        cr2.setRole(DummyHeartbeatConstants.NAMENODE);
        cr2.setRoleCommand("INSTALL");
        cr2.setStatus(IN_PROGRESS.toString());
        cr2.setStdErr("none");
        cr2.setStdOut("dummy output");
        cr2.setExitCode(777);
        ArrayList<CommandReport> reports = new ArrayList<>();
        reports.add(cr1);
        reports.add(cr2);
        hb.setReports(reports);
        final HostRoleCommand command = hostRoleCommandFactory.create(DummyHeartbeatConstants.DummyHostname1, DATANODE, null, null);
        ActionManager am = actionManagerTestHelper.getMockActionManager();
        expect(am.getTasks(EasyMock.<List<Long>>anyObject())).andReturn(new ArrayList<HostRoleCommand>() {
            {
                add(command);
                add(command);
            }
        });
        replay(am);
        HeartBeatHandler handler = heartbeatTestHelper.getHeartBeatHandler(am);
        handler.handleHeartBeat(hb);
        Assert.assertEquals("State of SCH not change while operation is in progress", UPGRADING, serviceComponentHost1.getState());
        Assert.assertEquals("State of SCH not change while operation is  in progress", INSTALLING, serviceComponentHost2.getState());
    }

    /**
     * Tests that if there is an invalid cluster in heartbeat data, the heartbeat
     * doesn't fail.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testHeartBeatWithAlertAndInvalidCluster() throws Exception {
        ActionManager am = actionManagerTestHelper.getMockActionManager();
        expect(am.getTasks(EasyMock.<List<Long>>anyObject())).andReturn(new ArrayList());
        replay(am);
        Cluster cluster = heartbeatTestHelper.getDummyCluster();
        Clusters fsm = clusters;
        Host hostObject = clusters.getHost(DummyHeartbeatConstants.DummyHostname1);
        hostObject.setIPv4("ipv4");
        hostObject.setIPv6("ipv6");
        hostObject.setOsType(DummyHeartbeatConstants.DummyOsType);
        HeartBeatHandler handler = new HeartBeatHandler(fsm, am, Encryptor.NONE, injector);
        Register reg = new Register();
        HostInfo hi = new HostInfo();
        hi.setHostName(DummyHeartbeatConstants.DummyHostname1);
        hi.setOS(DummyHeartbeatConstants.DummyOs);
        hi.setOSRelease(DummyHeartbeatConstants.DummyOSRelease);
        reg.setHostname(DummyHeartbeatConstants.DummyHostname1);
        reg.setHardwareProfile(hi);
        reg.setAgentVersion(metaInfo.getServerVersion());
        handler.handleRegistration(reg);
        hostObject.setState(UNHEALTHY);
        ExecutionCommand execCmd = new ExecutionCommand();
        execCmd.setRequestAndStage(2, 34);
        execCmd.setHostname(DummyHeartbeatConstants.DummyHostname1);
        // aq.enqueue(DummyHostname1, new ExecutionCommand());
        HeartBeat hb = new HeartBeat();
        HostStatus hs = new HostStatus(Status.HEALTHY, DummyHeartbeatConstants.DummyHostStatus);
        hb.setResponseId(0);
        hb.setNodeStatus(hs);
        hb.setHostname(DummyHeartbeatConstants.DummyHostname1);
        Alert alert = new Alert("foo", "bar", "baz", "foobar", "foobarbaz", AlertState.OK);
        alert.setClusterId((-1L));
        List<Alert> alerts = Collections.singletonList(alert);
        hb.setAlerts(alerts);
        // should NOT throw AmbariException from alerts.
        handler.getHeartbeatProcessor().processHeartbeat(hb);
    }

    @Test
    public void testInstallPackagesWithId() throws Exception {
        // required since this test method checks the DAO result of handling a
        // heartbeat which performs some async tasks
        EventBusSynchronizer.synchronizeAmbariEventPublisher(injector);
        final HostRoleCommand command = hostRoleCommandFactory.create(DummyHeartbeatConstants.DummyHostname1, DATANODE, null, null);
        ActionManager am = actionManagerTestHelper.getMockActionManager();
        expect(am.getTasks(EasyMock.<List<Long>>anyObject())).andReturn(Collections.singletonList(command)).anyTimes();
        replay(am);
        Cluster cluster = heartbeatTestHelper.getDummyCluster();
        RepositoryVersionDAO dao = injector.getInstance(RepositoryVersionDAO.class);
        RepositoryVersionEntity entity = helper.getOrCreateRepositoryVersion(cluster);
        Assert.assertNotNull(entity);
        HeartBeatHandler handler = heartbeatTestHelper.getHeartBeatHandler(am);
        HeartbeatProcessor heartbeatProcessor = handler.getHeartbeatProcessor();
        HeartBeat hb = new HeartBeat();
        JsonObject json = new JsonObject();
        json.addProperty("actual_version", "2.2.1.0-2222");
        json.addProperty("package_installation_result", "SUCCESS");
        json.addProperty("repository_version_id", entity.getId());
        CommandReport cmdReport = new CommandReport();
        cmdReport.setActionId(StageUtils.getActionId(requestId, stageId));
        cmdReport.setTaskId(1);
        cmdReport.setCustomCommand("install_packages");
        cmdReport.setStructuredOut(json.toString());
        cmdReport.setRoleCommand(ACTIONEXECUTE.name());
        cmdReport.setStatus(COMPLETED.name());
        cmdReport.setRole("install_packages");
        cmdReport.setClusterId(DummyHeartbeatConstants.DummyClusterId);
        List<CommandReport> reports = new ArrayList<>();
        reports.add(cmdReport);
        hb.setReports(reports);
        hb.setTimestamp(0L);
        hb.setResponseId(0);
        hb.setNodeStatus(new HostStatus(Status.HEALTHY, DummyHeartbeatConstants.DummyHostStatus));
        hb.setHostname(DummyHeartbeatConstants.DummyHostname1);
        hb.setComponentStatus(new ArrayList());
        StackId stackId = new StackId("HDP", "0.1");
        heartbeatProcessor.processHeartbeat(hb);
        entity = dao.findByStackAndVersion(stackId, "2.2.1.0-2222");
        Assert.assertNotNull(entity);
        entity = dao.findByStackAndVersion(stackId, "0.1.1");
        Assert.assertNull(entity);
    }
}

