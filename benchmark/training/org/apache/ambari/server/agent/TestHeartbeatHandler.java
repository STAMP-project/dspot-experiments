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


import Configuration.PREFIX_DIR;
import HostState.HEALTHY;
import HostState.UNHEALTHY;
import HostState.WAITING_FOR_HOST_STATUS_UPDATES;
import KerberosIdentityDataFileWriter.HOSTNAME;
import KerberosIdentityDataFileWriter.KEYTAB_FILE_GROUP_ACCESS;
import KerberosIdentityDataFileWriter.KEYTAB_FILE_GROUP_NAME;
import KerberosIdentityDataFileWriter.KEYTAB_FILE_OWNER_ACCESS;
import KerberosIdentityDataFileWriter.KEYTAB_FILE_OWNER_NAME;
import KerberosIdentityDataFileWriter.KEYTAB_FILE_PATH;
import KerberosIdentityDataFileWriter.PRINCIPAL;
import KerberosServerAction.KEYTAB_CONTENT_BASE64;
import Role.DATANODE;
import RoleCommand.INSTALL;
import State.INIT;
import State.INSTALLING;
import State.STARTED;
import com.google.inject.Inject;
import com.google.inject.Injector;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBException;
import org.apache.ambari.server.AmbariException;
import org.apache.ambari.server.actionmanager.ActionDBAccessor;
import org.apache.ambari.server.actionmanager.ActionManager;
import org.apache.ambari.server.actionmanager.ActionManagerTestHelper;
import org.apache.ambari.server.actionmanager.HostRoleCommand;
import org.apache.ambari.server.actionmanager.HostRoleCommandFactory;
import org.apache.ambari.server.actionmanager.Request;
import org.apache.ambari.server.actionmanager.Stage;
import org.apache.ambari.server.actionmanager.StageFactory;
import org.apache.ambari.server.agent.HostStatus.Status;
import org.apache.ambari.server.api.services.AmbariMetaInfo;
import org.apache.ambari.server.audit.AuditLogger;
import org.apache.ambari.server.configuration.Configuration;
import org.apache.ambari.server.events.publishers.AgentCommandsPublisher;
import org.apache.ambari.server.orm.InMemoryDefaultTestModule;
import org.apache.ambari.server.orm.OrmTestHelper;
import org.apache.ambari.server.state.Alert;
import org.apache.ambari.server.state.Cluster;
import org.apache.ambari.server.state.Clusters;
import org.apache.ambari.server.state.Host;
import org.apache.ambari.server.state.Service;
import org.apache.ambari.server.state.ServiceComponentHost;
import org.apache.ambari.server.state.State;
import org.apache.ambari.server.state.fsm.InvalidStateTransitionException;
import org.apache.ambari.server.utils.StageUtils;
import org.apache.commons.codec.binary.Base64;
import org.codehaus.jackson.JsonGenerationException;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;


public class TestHeartbeatHandler {
    private static final Logger log = LoggerFactory.getLogger(TestHeartbeatHandler.class);

    private Injector injector;

    private Clusters clusters;

    long requestId = 23;

    long stageId = 31;

    @Inject
    AmbariMetaInfo metaInfo;

    @Inject
    Configuration config;

    @Inject
    ActionDBAccessor actionDBAccessor;

    @Inject
    StageFactory stageFactory;

    @Inject
    HostRoleCommandFactory hostRoleCommandFactory;

    @Inject
    HeartbeatTestHelper heartbeatTestHelper;

    @Inject
    ActionManagerTestHelper actionManagerTestHelper;

    @Inject
    AuditLogger auditLogger;

    @Inject
    private OrmTestHelper helper;

    @Inject
    private AgentCommandsPublisher agentCommandsPublisher;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private InMemoryDefaultTestModule module;

    @Test
    public void testHeartbeat() throws Exception {
        ActionManager am = actionManagerTestHelper.getMockActionManager();
        expect(am.getTasks(EasyMock.<List<Long>>anyObject())).andReturn(new ArrayList());
        replay(am);
        Cluster cluster = heartbeatTestHelper.getDummyCluster();
        Service hdfs = addService(cluster, DummyHeartbeatConstants.HDFS);
        hdfs.addServiceComponent(DummyHeartbeatConstants.DATANODE);
        hdfs.addServiceComponent(DummyHeartbeatConstants.NAMENODE);
        hdfs.addServiceComponent(DummyHeartbeatConstants.SECONDARY_NAMENODE);
        Collection<Host> hosts = cluster.getHosts();
        Assert.assertEquals(hosts.size(), 1);
        Host hostObject = hosts.iterator().next();
        hostObject.setIPv4("ipv4");
        hostObject.setIPv6("ipv6");
        hostObject.setOsType(DummyHeartbeatConstants.DummyOsType);
        String hostname = hostObject.getHostName();
        HeartBeatHandler handler = createHeartBeatHandler();
        Register reg = new Register();
        HostInfo hi = new HostInfo();
        hi.setHostName(hostname);
        hi.setOS(DummyHeartbeatConstants.DummyOs);
        hi.setOSRelease(DummyHeartbeatConstants.DummyOSRelease);
        reg.setHostname(hostname);
        reg.setHardwareProfile(hi);
        reg.setAgentVersion(metaInfo.getServerVersion());
        handler.handleRegistration(reg);
        hostObject.setState(UNHEALTHY);
        HeartBeat hb = new HeartBeat();
        hb.setResponseId(0);
        HostStatus hs = new HostStatus(Status.HEALTHY, DummyHeartbeatConstants.DummyHostStatus);
        List<Alert> al = new ArrayList<>();
        al.add(new Alert());
        hb.setNodeStatus(hs);
        hb.setHostname(hostname);
        handler.handleHeartBeat(hb);
        Assert.assertEquals(HEALTHY, hostObject.getState());
    }

    @Test
    public void testStatusHeartbeatWithAnnotation() throws Exception {
        Cluster cluster = heartbeatTestHelper.getDummyCluster();
        Service hdfs = addService(cluster, DummyHeartbeatConstants.HDFS);
        hdfs.addServiceComponent(DummyHeartbeatConstants.DATANODE);
        hdfs.addServiceComponent(DummyHeartbeatConstants.NAMENODE);
        hdfs.addServiceComponent(DummyHeartbeatConstants.SECONDARY_NAMENODE);
        HeartBeat hb = new HeartBeat();
        hb.setTimestamp(System.currentTimeMillis());
        hb.setResponseId(0);
        hb.setHostname(DummyHeartbeatConstants.DummyHostname1);
        hb.setNodeStatus(new HostStatus(Status.HEALTHY, DummyHeartbeatConstants.DummyHostStatus));
        hb.setReports(new ArrayList());
        ArrayList<ComponentStatus> componentStatuses = new ArrayList<>();
        hb.setComponentStatus(componentStatuses);
        final HostRoleCommand command = hostRoleCommandFactory.create(DummyHeartbeatConstants.DummyHostname1, DATANODE, null, null);
        ActionManager am = actionManagerTestHelper.getMockActionManager();
        expect(am.getTasks(EasyMock.<List<Long>>anyObject())).andReturn(new ArrayList<HostRoleCommand>() {
            {
                add(command);
            }
        }).anyTimes();
        replay(am);
        HeartBeatHandler handler = heartbeatTestHelper.getHeartBeatHandler(am);
        HeartBeatResponse resp = handler.handleHeartBeat(hb);
        assertFalse(resp.hasMappedComponents());
        hdfs.getServiceComponent(DummyHeartbeatConstants.DATANODE).addServiceComponentHost(DummyHeartbeatConstants.DummyHostname1);
        ServiceComponentHost serviceComponentHost1 = clusters.getCluster(DummyHeartbeatConstants.DummyCluster).getService(DummyHeartbeatConstants.HDFS).getServiceComponent(DummyHeartbeatConstants.DATANODE).getServiceComponentHost(DummyHeartbeatConstants.DummyHostname1);
        serviceComponentHost1.setState(INIT);
        hb = new HeartBeat();
        hb.setTimestamp(System.currentTimeMillis());
        hb.setResponseId(1);
        hb.setHostname(DummyHeartbeatConstants.DummyHostname1);
        hb.setNodeStatus(new HostStatus(Status.HEALTHY, DummyHeartbeatConstants.DummyHostStatus));
        hb.setReports(new ArrayList());
        hb.setComponentStatus(componentStatuses);
        resp = handler.handleHeartBeat(hb);
        assertTrue(resp.hasMappedComponents());
    }

    @Test
    public void testRegistration() throws Exception, InvalidStateTransitionException {
        ActionManager am = actionManagerTestHelper.getMockActionManager();
        replay(am);
        HeartBeatHandler handler = createHeartBeatHandler();
        clusters.addHost(DummyHeartbeatConstants.DummyHostname1);
        Host hostObject = clusters.getHost(DummyHeartbeatConstants.DummyHostname1);
        hostObject.setIPv4("ipv4");
        hostObject.setIPv6("ipv6");
        Register reg = new Register();
        HostInfo hi = new HostInfo();
        hi.setHostName(DummyHeartbeatConstants.DummyHostname1);
        hi.setOS(DummyHeartbeatConstants.DummyOsType);
        reg.setHostname(DummyHeartbeatConstants.DummyHostname1);
        reg.setCurrentPingPort(DummyHeartbeatConstants.DummyCurrentPingPort);
        reg.setHardwareProfile(hi);
        reg.setAgentVersion(metaInfo.getServerVersion());
        reg.setPrefix(PREFIX_DIR);
        handler.handleRegistration(reg);
        Assert.assertEquals(hostObject.getState(), WAITING_FOR_HOST_STATUS_UPDATES);
        Assert.assertEquals(DummyHeartbeatConstants.DummyOsType, hostObject.getOsType());
        Assert.assertEquals(DummyHeartbeatConstants.DummyCurrentPingPort, hostObject.getCurrentPingPort());
        Assert.assertTrue(((hostObject.getLastRegistrationTime()) != 0));
        Assert.assertEquals(hostObject.getLastHeartbeatTime(), hostObject.getLastRegistrationTime());
    }

    @Test
    public void testRegistrationWithBadVersion() throws Exception, InvalidStateTransitionException {
        ActionManager am = actionManagerTestHelper.getMockActionManager();
        replay(am);
        HeartBeatHandler handler = createHeartBeatHandler();
        clusters.addHost(DummyHeartbeatConstants.DummyHostname1);
        Host hostObject = clusters.getHost(DummyHeartbeatConstants.DummyHostname1);
        hostObject.setIPv4("ipv4");
        hostObject.setIPv6("ipv6");
        Register reg = new Register();
        HostInfo hi = new HostInfo();
        hi.setHostName(DummyHeartbeatConstants.DummyHostname1);
        hi.setOS(DummyHeartbeatConstants.DummyOsType);
        reg.setHostname(DummyHeartbeatConstants.DummyHostname1);
        reg.setHardwareProfile(hi);
        reg.setAgentVersion("");// Invalid agent version

        reg.setPrefix(PREFIX_DIR);
        try {
            handler.handleRegistration(reg);
            Assert.fail("Expected failure for non compatible agent version");
        } catch (AmbariException e) {
            TestHeartbeatHandler.log.debug("Error:{}", e.getMessage());
            assertTrue(e.getMessage().contains("Cannot register host with non compatible agent version"));
        }
        reg.setAgentVersion(null);// Invalid agent version

        try {
            handler.handleRegistration(reg);
            Assert.fail("Expected failure for non compatible agent version");
        } catch (AmbariException e) {
            TestHeartbeatHandler.log.debug("Error:{}", e.getMessage());
            assertTrue(e.getMessage().contains("Cannot register host with non compatible agent version"));
        }
    }

    @Test
    public void testRegistrationPublicHostname() throws Exception, InvalidStateTransitionException {
        ActionManager am = actionManagerTestHelper.getMockActionManager();
        replay(am);
        HeartBeatHandler handler = createHeartBeatHandler();
        clusters.addHost(DummyHeartbeatConstants.DummyHostname1);
        Host hostObject = clusters.getHost(DummyHeartbeatConstants.DummyHostname1);
        hostObject.setIPv4("ipv4");
        hostObject.setIPv6("ipv6");
        Register reg = new Register();
        HostInfo hi = new HostInfo();
        hi.setHostName(DummyHeartbeatConstants.DummyHostname1);
        hi.setOS(DummyHeartbeatConstants.DummyOsType);
        reg.setHostname(DummyHeartbeatConstants.DummyHostname1);
        reg.setHardwareProfile(hi);
        reg.setPublicHostname(((DummyHeartbeatConstants.DummyHostname1) + "-public"));
        reg.setAgentVersion(metaInfo.getServerVersion());
        handler.handleRegistration(reg);
        Assert.assertEquals(hostObject.getState(), WAITING_FOR_HOST_STATUS_UPDATES);
        Assert.assertEquals(DummyHeartbeatConstants.DummyOsType, hostObject.getOsType());
        Assert.assertTrue(((hostObject.getLastRegistrationTime()) != 0));
        Assert.assertEquals(hostObject.getLastHeartbeatTime(), hostObject.getLastRegistrationTime());
        Host verifyHost = clusters.getHost(DummyHeartbeatConstants.DummyHostname1);
        Assert.assertEquals(verifyHost.getPublicHostName(), reg.getPublicHostname());
    }

    @Test
    public void testInvalidOSRegistration() throws Exception, InvalidStateTransitionException {
        ActionManager am = actionManagerTestHelper.getMockActionManager();
        replay(am);
        HeartBeatHandler handler = createHeartBeatHandler();
        clusters.addHost(DummyHeartbeatConstants.DummyHostname1);
        Host hostObject = clusters.getHost(DummyHeartbeatConstants.DummyHostname1);
        hostObject.setIPv4("ipv4");
        hostObject.setIPv6("ipv6");
        Register reg = new Register();
        HostInfo hi = new HostInfo();
        hi.setHostName(DummyHeartbeatConstants.DummyHostname1);
        hi.setOS("MegaOperatingSystem");
        reg.setHostname(DummyHeartbeatConstants.DummyHostname1);
        reg.setHardwareProfile(hi);
        reg.setAgentVersion(metaInfo.getServerVersion());
        try {
            handler.handleRegistration(reg);
            Assert.fail("Expected failure for non matching os type");
        } catch (AmbariException e) {
            // Expected
        }
    }

    @Test
    public void testIncompatibleAgentRegistration() throws Exception, InvalidStateTransitionException {
        ActionManager am = actionManagerTestHelper.getMockActionManager();
        replay(am);
        HeartBeatHandler handler = createHeartBeatHandler();
        clusters.addHost(DummyHeartbeatConstants.DummyHostname1);
        Host hostObject = clusters.getHost(DummyHeartbeatConstants.DummyHostname1);
        hostObject.setIPv4("ipv4");
        hostObject.setIPv6("ipv6");
        Register reg = new Register();
        HostInfo hi = new HostInfo();
        hi.setHostName(DummyHeartbeatConstants.DummyHostname1);
        hi.setOS(DummyHeartbeatConstants.DummyOsType);
        reg.setHostname(DummyHeartbeatConstants.DummyHostname1);
        reg.setHardwareProfile(hi);
        reg.setAgentVersion("0.0.0");// Invalid agent version

        try {
            handler.handleRegistration(reg);
            Assert.fail("Expected failure for non compatible agent version");
        } catch (AmbariException e) {
            // Expected
        }
    }

    @Test
    public void testRegisterNewNode() throws Exception, InvalidStateTransitionException {
        ActionManager am = actionManagerTestHelper.getMockActionManager();
        replay(am);
        Clusters fsm = clusters;
        fsm.addHost(DummyHeartbeatConstants.DummyHostname1);
        Host hostObject = clusters.getHost(DummyHeartbeatConstants.DummyHostname1);
        hostObject.setIPv4("ipv4");
        hostObject.setIPv6("ipv6");
        HeartBeatHandler handler = createHeartBeatHandler();
        Register reg = new Register();
        HostInfo hi = new HostInfo();
        hi.setHostName(DummyHeartbeatConstants.DummyHostname1);
        hi.setOS("redhat5");
        reg.setHostname(DummyHeartbeatConstants.DummyHostname1);
        reg.setHardwareProfile(hi);
        reg.setAgentVersion(metaInfo.getServerVersion());
        reg.setPrefix(PREFIX_DIR);
        RegistrationResponse response = handler.handleRegistration(reg);
        Assert.assertEquals(hostObject.getState(), WAITING_FOR_HOST_STATUS_UPDATES);
        Assert.assertEquals("redhat5", hostObject.getOsType());
        Assert.assertEquals(0, response.getResponseId());
        Assert.assertEquals(reg.getPrefix(), hostObject.getPrefix());
    }

    @Test
    public void testRequestId() throws IOException, JAXBException, InvalidStateTransitionException, JsonGenerationException {
        HeartBeatHandler heartBeatHandler = injector.getInstance(HeartBeatHandler.class);
        Register register = new Register();
        register.setHostname("newHost");
        register.setTimestamp(new Date().getTime());
        register.setResponseId(123);
        HostInfo hi = new HostInfo();
        hi.setHostName(DummyHeartbeatConstants.DummyHostname1);
        hi.setOS("redhat5");
        register.setHardwareProfile(hi);
        register.setAgentVersion(metaInfo.getServerVersion());
        RegistrationResponse registrationResponse = heartBeatHandler.handleRegistration(register);
        Assert.assertEquals("ResponseId should start from zero", 0L, registrationResponse.getResponseId());
        HeartBeat heartBeat = constructHeartBeat("newHost", registrationResponse.getResponseId(), Status.HEALTHY);
        HeartBeatResponse hbResponse = heartBeatHandler.handleHeartBeat(heartBeat);
        Assert.assertEquals("responseId was not incremented", 1L, hbResponse.getResponseId());
        Assert.assertTrue("Not cached response returned", (hbResponse == (heartBeatHandler.handleHeartBeat(heartBeat))));
        heartBeat.setResponseId(1L);
        hbResponse = heartBeatHandler.handleHeartBeat(heartBeat);
        Assert.assertEquals("responseId was not incremented", 2L, hbResponse.getResponseId());
        Assert.assertTrue("Agent is flagged for restart", ((hbResponse.isRestartAgent()) == null));
        TestHeartbeatHandler.log.debug(StageUtils.jaxbToString(hbResponse));
        heartBeat.setResponseId(20L);
        hbResponse = heartBeatHandler.handleHeartBeat(heartBeat);
        // assertEquals("responseId was not incremented", 2L, hbResponse.getResponseId());
        Assert.assertTrue("Agent is not flagged for restart", hbResponse.isRestartAgent());
        TestHeartbeatHandler.log.debug(StageUtils.jaxbToString(hbResponse));
    }

    @Test
    public void testTaskInProgressHandling() throws Exception, InvalidStateTransitionException {
        Cluster cluster = heartbeatTestHelper.getDummyCluster();
        Service hdfs = addService(cluster, DummyHeartbeatConstants.HDFS);
        hdfs.addServiceComponent(DummyHeartbeatConstants.DATANODE);
        hdfs.getServiceComponent(DummyHeartbeatConstants.DATANODE).addServiceComponentHost(DummyHeartbeatConstants.DummyHostname1);
        hdfs.addServiceComponent(DummyHeartbeatConstants.NAMENODE);
        hdfs.getServiceComponent(DummyHeartbeatConstants.NAMENODE).addServiceComponentHost(DummyHeartbeatConstants.DummyHostname1);
        hdfs.addServiceComponent(DummyHeartbeatConstants.SECONDARY_NAMENODE);
        hdfs.getServiceComponent(DummyHeartbeatConstants.SECONDARY_NAMENODE).addServiceComponentHost(DummyHeartbeatConstants.DummyHostname1);
        ServiceComponentHost serviceComponentHost1 = clusters.getCluster(DummyHeartbeatConstants.DummyCluster).getService(DummyHeartbeatConstants.HDFS).getServiceComponent(DummyHeartbeatConstants.DATANODE).getServiceComponentHost(DummyHeartbeatConstants.DummyHostname1);
        serviceComponentHost1.setState(INSTALLING);
        HeartBeat hb = new HeartBeat();
        hb.setTimestamp(System.currentTimeMillis());
        hb.setResponseId(0);
        hb.setHostname(DummyHeartbeatConstants.DummyHostname1);
        hb.setNodeStatus(new HostStatus(Status.HEALTHY, DummyHeartbeatConstants.DummyHostStatus));
        List<CommandReport> reports = new ArrayList<>();
        CommandReport cr = new CommandReport();
        cr.setActionId(StageUtils.getActionId(requestId, stageId));
        cr.setTaskId(1);
        cr.setClusterId(DummyHeartbeatConstants.DummyClusterId);
        cr.setServiceName(DummyHeartbeatConstants.HDFS);
        cr.setRole(DummyHeartbeatConstants.DATANODE);
        cr.setRoleCommand("INSTALL");
        cr.setStatus("IN_PROGRESS");
        cr.setStdErr("none");
        cr.setStdOut("dummy output");
        cr.setExitCode(777);
        reports.add(cr);
        hb.setReports(reports);
        hb.setComponentStatus(new ArrayList());
        final HostRoleCommand command = hostRoleCommandFactory.create(DummyHeartbeatConstants.DummyHostname1, DATANODE, null, INSTALL);
        ActionManager am = actionManagerTestHelper.getMockActionManager();
        expect(am.getTasks(EasyMock.<List<Long>>anyObject())).andReturn(new ArrayList<HostRoleCommand>() {
            {
                add(command);
            }
        });
        replay(am);
        HeartBeatHandler handler = heartbeatTestHelper.getHeartBeatHandler(am);
        handler.handleHeartBeat(hb);
        handler.getHeartbeatProcessor().processHeartbeat(hb);
        State componentState1 = serviceComponentHost1.getState();
        Assert.assertEquals("Host state should still be installing", INSTALLING, componentState1);
    }

    @Test
    public void testOPFailedEventForAbortedTask() throws Exception, InvalidStateTransitionException {
        Cluster cluster = heartbeatTestHelper.getDummyCluster();
        Service hdfs = addService(cluster, DummyHeartbeatConstants.HDFS);
        hdfs.addServiceComponent(DummyHeartbeatConstants.DATANODE);
        hdfs.getServiceComponent(DummyHeartbeatConstants.DATANODE).addServiceComponentHost(DummyHeartbeatConstants.DummyHostname1);
        hdfs.addServiceComponent(DummyHeartbeatConstants.NAMENODE);
        hdfs.getServiceComponent(DummyHeartbeatConstants.NAMENODE).addServiceComponentHost(DummyHeartbeatConstants.DummyHostname1);
        hdfs.addServiceComponent(DummyHeartbeatConstants.SECONDARY_NAMENODE);
        hdfs.getServiceComponent(DummyHeartbeatConstants.SECONDARY_NAMENODE).addServiceComponentHost(DummyHeartbeatConstants.DummyHostname1);
        ServiceComponentHost serviceComponentHost1 = clusters.getCluster(DummyHeartbeatConstants.DummyCluster).getService(DummyHeartbeatConstants.HDFS).getServiceComponent(DummyHeartbeatConstants.DATANODE).getServiceComponentHost(DummyHeartbeatConstants.DummyHostname1);
        serviceComponentHost1.setState(INSTALLING);
        Stage s = stageFactory.createNew(1, "/a/b", "cluster1", 1L, "action manager test", "commandParamsStage", "hostParamsStage");
        s.setStageId(1);
        s.addHostRoleExecutionCommand(DummyHeartbeatConstants.DummyHostname1, DATANODE, INSTALL, new org.apache.ambari.server.state.svccomphost.ServiceComponentHostInstallEvent(DATANODE.toString(), DummyHeartbeatConstants.DummyHostname1, System.currentTimeMillis(), "HDP-1.3.0"), DummyHeartbeatConstants.DummyCluster, "HDFS", false, false);
        List<Stage> stages = new ArrayList<>();
        stages.add(s);
        Request request = new Request(stages, "clusterHostInfo", clusters);
        actionDBAccessor.persistActions(request);
        actionDBAccessor.abortHostRole(DummyHeartbeatConstants.DummyHostname1, 1, 1, DATANODE.name());
        HeartBeat hb = new HeartBeat();
        hb.setTimestamp(System.currentTimeMillis());
        hb.setResponseId(0);
        hb.setHostname(DummyHeartbeatConstants.DummyHostname1);
        hb.setNodeStatus(new HostStatus(Status.HEALTHY, DummyHeartbeatConstants.DummyHostStatus));
        List<CommandReport> reports = new ArrayList<>();
        CommandReport cr = new CommandReport();
        cr.setActionId(StageUtils.getActionId(1, 1));
        cr.setTaskId(1);
        cr.setClusterId(DummyHeartbeatConstants.DummyClusterId);
        cr.setServiceName(DummyHeartbeatConstants.HDFS);
        cr.setRole(DummyHeartbeatConstants.DATANODE);
        cr.setRoleCommand("INSTALL");
        cr.setStatus("FAILED");
        cr.setStdErr("none");
        cr.setStdOut("dummy output");
        cr.setExitCode(777);
        reports.add(cr);
        hb.setReports(reports);
        hb.setComponentStatus(new ArrayList());
        final HostRoleCommand command = hostRoleCommandFactory.create(DummyHeartbeatConstants.DummyHostname1, DATANODE, null, null);
        ActionManager am = actionManagerTestHelper.getMockActionManager();
        expect(am.getTasks(EasyMock.<List<Long>>anyObject())).andReturn(new ArrayList<HostRoleCommand>() {
            {
                add(command);
            }
        });
        replay(am);
        HeartBeatHandler handler = heartbeatTestHelper.getHeartBeatHandler(am);
        handler.handleHeartBeat(hb);
        handler.getHeartbeatProcessor().processHeartbeat(hb);
        State componentState1 = serviceComponentHost1.getState();
        Assert.assertEquals("Host state should still be installing", INSTALLING, componentState1);
    }

    @Test
    public void testRecoveryStatusReports() throws Exception {
        Cluster cluster = heartbeatTestHelper.getDummyCluster();
        Host hostObject = clusters.getHost(DummyHeartbeatConstants.DummyHostname1);
        Service hdfs = addService(cluster, DummyHeartbeatConstants.HDFS);
        hdfs.addServiceComponent(DummyHeartbeatConstants.DATANODE);
        hdfs.getServiceComponent(DummyHeartbeatConstants.DATANODE).addServiceComponentHost(DummyHeartbeatConstants.DummyHostname1);
        hdfs.addServiceComponent(DummyHeartbeatConstants.NAMENODE);
        hdfs.getServiceComponent(DummyHeartbeatConstants.NAMENODE).addServiceComponentHost(DummyHeartbeatConstants.DummyHostname1);
        hdfs.getServiceComponent(DummyHeartbeatConstants.NAMENODE).getServiceComponentHost(DummyHeartbeatConstants.DummyHostname1).setState(STARTED);
        hdfs.getServiceComponent(DummyHeartbeatConstants.DATANODE).getServiceComponentHost(DummyHeartbeatConstants.DummyHostname1).setState(STARTED);
        final HostRoleCommand command = hostRoleCommandFactory.create(DummyHeartbeatConstants.DummyHostname1, DATANODE, null, null);
        ActionManager am = actionManagerTestHelper.getMockActionManager();
        expect(am.getTasks(EasyMock.<List<Long>>anyObject())).andReturn(new ArrayList<HostRoleCommand>() {
            {
                add(command);
                add(command);
            }
        }).anyTimes();
        replay(am);
        HeartBeatHandler handler = createHeartBeatHandler();
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
        // aq.enqueue(DummyHostname1, new StatusCommand());
        // All components are up
        HeartBeat hb1 = new HeartBeat();
        hb1.setResponseId(0);
        hb1.setNodeStatus(new HostStatus(Status.HEALTHY, DummyHeartbeatConstants.DummyHostStatus));
        hb1.setHostname(DummyHeartbeatConstants.DummyHostname1);
        RecoveryReport rr = new RecoveryReport();
        rr.setSummary("RECOVERABLE");
        List<ComponentRecoveryReport> compRecReports = new ArrayList<>();
        ComponentRecoveryReport compRecReport = new ComponentRecoveryReport();
        compRecReport.setLimitReached(Boolean.FALSE);
        compRecReport.setName("DATANODE");
        compRecReport.setNumAttempts(2);
        compRecReports.add(compRecReport);
        rr.setComponentReports(compRecReports);
        hb1.setRecoveryReport(rr);
        handler.handleHeartBeat(hb1);
        Assert.assertEquals("RECOVERABLE", hostObject.getRecoveryReport().getSummary());
        Assert.assertEquals(1, hostObject.getRecoveryReport().getComponentReports().size());
        Assert.assertEquals(2, hostObject.getRecoveryReport().getComponentReports().get(0).getNumAttempts());
        HeartBeat hb2 = new HeartBeat();
        hb2.setResponseId(1);
        hb2.setNodeStatus(new HostStatus(Status.HEALTHY, DummyHeartbeatConstants.DummyHostStatus));
        hb2.setHostname(DummyHeartbeatConstants.DummyHostname1);
        rr = new RecoveryReport();
        rr.setSummary("UNRECOVERABLE");
        compRecReports = new ArrayList();
        compRecReport = new ComponentRecoveryReport();
        compRecReport.setLimitReached(Boolean.TRUE);
        compRecReport.setName("DATANODE");
        compRecReport.setNumAttempts(5);
        compRecReports.add(compRecReport);
        rr.setComponentReports(compRecReports);
        hb2.setRecoveryReport(rr);
        handler.handleHeartBeat(hb2);
        Assert.assertEquals("UNRECOVERABLE", hostObject.getRecoveryReport().getSummary());
        Assert.assertEquals(1, hostObject.getRecoveryReport().getComponentReports().size());
        Assert.assertEquals(5, hostObject.getRecoveryReport().getComponentReports().get(0).getNumAttempts());
    }

    @Test
    public void testCommandStatusProcesses_empty() throws Exception {
        Cluster cluster = heartbeatTestHelper.getDummyCluster();
        Service hdfs = addService(cluster, DummyHeartbeatConstants.HDFS);
        hdfs.addServiceComponent(DummyHeartbeatConstants.DATANODE);
        hdfs.getServiceComponent(DummyHeartbeatConstants.DATANODE).addServiceComponentHost(DummyHeartbeatConstants.DummyHostname1);
        hdfs.getServiceComponent(DummyHeartbeatConstants.DATANODE).getServiceComponentHost(DummyHeartbeatConstants.DummyHostname1).setState(STARTED);
        HeartBeat hb = new HeartBeat();
        hb.setTimestamp(System.currentTimeMillis());
        hb.setResponseId(0);
        hb.setHostname(DummyHeartbeatConstants.DummyHostname1);
        hb.setNodeStatus(new HostStatus(Status.HEALTHY, DummyHeartbeatConstants.DummyHostStatus));
        hb.setReports(new ArrayList());
        ArrayList<ComponentStatus> componentStatuses = new ArrayList<>();
        ComponentStatus componentStatus1 = new ComponentStatus();
        // componentStatus1.setClusterName(DummyCluster);
        componentStatus1.setServiceName(DummyHeartbeatConstants.HDFS);
        componentStatus1.setMessage(DummyHeartbeatConstants.DummyHostStatus);
        componentStatus1.setStatus(STARTED.name());
        componentStatus1.setComponentName(DummyHeartbeatConstants.DATANODE);
        componentStatuses.add(componentStatus1);
        hb.setComponentStatus(componentStatuses);
        final HostRoleCommand command = hostRoleCommandFactory.create(DummyHeartbeatConstants.DummyHostname1, DATANODE, null, null);
        ActionManager am = actionManagerTestHelper.getMockActionManager();
        expect(am.getTasks(EasyMock.<List<Long>>anyObject())).andReturn(new ArrayList<HostRoleCommand>() {
            {
                add(command);
            }
        });
        replay(am);
        ServiceComponentHost sch = hdfs.getServiceComponent(DummyHeartbeatConstants.DATANODE).getServiceComponentHost(DummyHeartbeatConstants.DummyHostname1);
        junit.framework.Assert.assertEquals(Integer.valueOf(0), Integer.valueOf(sch.getProcesses().size()));
    }

    @Test
    public void testInjectKeytabApplicableHost() throws Exception {
        List<Map<String, String>> kcp;
        Map<String, String> properties;
        Cluster cluster = heartbeatTestHelper.getDummyCluster();
        Service hdfs = addService(cluster, DummyHeartbeatConstants.HDFS);
        hdfs.addServiceComponent(DummyHeartbeatConstants.DATANODE);
        hdfs.addServiceComponent(DummyHeartbeatConstants.NAMENODE);
        hdfs.addServiceComponent(DummyHeartbeatConstants.SECONDARY_NAMENODE);
        kcp = testInjectKeytabSetKeytab("c6403.ambari.apache.org");
        assertNotNull(kcp);
        junit.framework.Assert.assertEquals(1, kcp.size());
        properties = kcp.get(0);
        assertNotNull(properties);
        assertEquals("c6403.ambari.apache.org", properties.get(HOSTNAME));
        assertEquals("dn/_HOST@_REALM", properties.get(PRINCIPAL));
        assertEquals("/etc/security/keytabs/dn.service.keytab", properties.get(KEYTAB_FILE_PATH));
        assertEquals("hdfs", properties.get(KEYTAB_FILE_OWNER_NAME));
        assertEquals("r", properties.get(KEYTAB_FILE_OWNER_ACCESS));
        assertEquals("hadoop", properties.get(KEYTAB_FILE_GROUP_NAME));
        assertEquals("", properties.get(KEYTAB_FILE_GROUP_ACCESS));
        assertEquals(Base64.encodeBase64String("hello".getBytes()), kcp.get(0).get(KEYTAB_CONTENT_BASE64));
        kcp = testInjectKeytabRemoveKeytab("c6403.ambari.apache.org");
        assertNotNull(kcp);
        junit.framework.Assert.assertEquals(1, kcp.size());
        properties = kcp.get(0);
        assertNotNull(properties);
        assertEquals("c6403.ambari.apache.org", properties.get(HOSTNAME));
        assertEquals("dn/_HOST@_REALM", properties.get(PRINCIPAL));
        assertEquals("/etc/security/keytabs/dn.service.keytab", properties.get(KEYTAB_FILE_PATH));
        assertFalse(properties.containsKey(KEYTAB_FILE_OWNER_NAME));
        assertFalse(properties.containsKey(KEYTAB_FILE_OWNER_ACCESS));
        assertFalse(properties.containsKey(KEYTAB_FILE_GROUP_NAME));
        assertFalse(properties.containsKey(KEYTAB_FILE_GROUP_ACCESS));
        assertFalse(properties.containsKey(KEYTAB_CONTENT_BASE64));
    }

    @Test
    public void testInjectKeytabNotApplicableHost() throws Exception {
        Cluster cluster = heartbeatTestHelper.getDummyCluster();
        Service hdfs = addService(cluster, DummyHeartbeatConstants.HDFS);
        hdfs.addServiceComponent(DummyHeartbeatConstants.DATANODE);
        hdfs.addServiceComponent(DummyHeartbeatConstants.NAMENODE);
        hdfs.addServiceComponent(DummyHeartbeatConstants.SECONDARY_NAMENODE);
        List<Map<String, String>> kcp;
        kcp = testInjectKeytabSetKeytab("c6401.ambari.apache.org");
        assertNotNull(kcp);
        assertTrue(kcp.isEmpty());
        kcp = testInjectKeytabRemoveKeytab("c6401.ambari.apache.org");
        assertNotNull(kcp);
        assertTrue(kcp.isEmpty());
    }
}

