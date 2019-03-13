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


import HostStatus.Status;
import Role.DATANODE;
import Role.NAMENODE;
import Role.SECONDARY_NAMENODE;
import State.INSTALLED;
import com.google.inject.Injector;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.ambari.server.AmbariException;
import org.apache.ambari.server.actionmanager.ActionManager;
import org.apache.ambari.server.api.services.AmbariMetaInfo;
import org.apache.ambari.server.orm.OrmTestHelper;
import org.apache.ambari.server.orm.entities.RepositoryVersionEntity;
import org.apache.ambari.server.security.encryption.Encryptor;
import org.apache.ambari.server.state.Cluster;
import org.apache.ambari.server.state.Clusters;
import org.apache.ambari.server.state.Config;
import org.apache.ambari.server.state.ConfigFactory;
import org.apache.ambari.server.state.Service;
import org.apache.ambari.server.state.StackId;
import org.apache.ambari.server.state.fsm.InvalidStateTransitionException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static KeyNames.HOOKS_FOLDER;


public class TestHeartbeatMonitor {
    private static Injector injector;

    private String hostname1 = "host1";

    private String hostname2 = "host2";

    private String clusterName = "cluster1";

    private String serviceName = "HDFS";

    private int heartbeatMonitorWakeupIntervalMS = 30;

    private static AmbariMetaInfo ambariMetaInfo;

    private static OrmTestHelper helper;

    private static final Logger LOG = LoggerFactory.getLogger(TestHeartbeatMonitor.class);

    @Test
    public void testStateCommandsGeneration() throws InterruptedException, AmbariException, InvalidStateTransitionException {
        StackId stackId = new StackId("HDP-0.1");
        Clusters clusters = TestHeartbeatMonitor.injector.getInstance(Clusters.class);
        clusters.addHost(hostname1);
        setOsFamily(clusters.getHost(hostname1), "redhat", "6.3");
        clusters.addHost(hostname2);
        setOsFamily(clusters.getHost(hostname2), "redhat", "6.3");
        clusters.addCluster(clusterName, stackId);
        Cluster cluster = clusters.getCluster(clusterName);
        RepositoryVersionEntity repositoryVersion = TestHeartbeatMonitor.helper.getOrCreateRepositoryVersion(stackId, stackId.getStackVersion());
        Set<String> hostNames = new HashSet<String>() {
            {
                add(hostname1);
                add(hostname2);
            }
        };
        ConfigFactory configFactory = TestHeartbeatMonitor.injector.getInstance(ConfigFactory.class);
        Config config = configFactory.createNew(cluster, "hadoop-env", "version1", new HashMap<String, String>() {
            {
                put("a", "b");
            }
        }, new HashMap());
        cluster.addDesiredConfig("_test", Collections.singleton(config));
        clusters.mapAndPublishHostsToCluster(hostNames, clusterName);
        Service hdfs = cluster.addService(serviceName, repositoryVersion);
        hdfs.addServiceComponent(DATANODE.name());
        hdfs.getServiceComponent(DATANODE.name()).addServiceComponentHost(hostname1);
        hdfs.addServiceComponent(NAMENODE.name());
        hdfs.getServiceComponent(NAMENODE.name()).addServiceComponentHost(hostname1);
        hdfs.addServiceComponent(SECONDARY_NAMENODE.name());
        hdfs.getServiceComponent(SECONDARY_NAMENODE.name()).addServiceComponentHost(hostname1);
        hdfs.getServiceComponent(DATANODE.name()).getServiceComponentHost(hostname1).setState(INSTALLED);
        hdfs.getServiceComponent(NAMENODE.name()).getServiceComponentHost(hostname1).setState(INSTALLED);
        hdfs.getServiceComponent(SECONDARY_NAMENODE.name()).getServiceComponentHost(hostname1).setState(INSTALLED);
        ActionManager am = Mockito.mock(ActionManager.class);
        HeartbeatMonitor hm = new HeartbeatMonitor(clusters, am, heartbeatMonitorWakeupIntervalMS, TestHeartbeatMonitor.injector);
        HeartBeatHandler handler = new HeartBeatHandler(clusters, am, Encryptor.NONE, TestHeartbeatMonitor.injector);
        Register reg = new Register();
        reg.setHostname(hostname1);
        reg.setResponseId(12);
        reg.setTimestamp(((System.currentTimeMillis()) - 300));
        reg.setAgentVersion(TestHeartbeatMonitor.ambariMetaInfo.getServerVersion());
        HostInfo hi = new HostInfo();
        hi.setOS("Centos5");
        reg.setHardwareProfile(hi);
        handler.handleRegistration(reg);
        HeartBeat hb = new HeartBeat();
        hb.setHostname(hostname1);
        hb.setNodeStatus(new HostStatus(Status.HEALTHY, "cool"));
        hb.setTimestamp(System.currentTimeMillis());
        hb.setResponseId(12);
        handler.handleHeartBeat(hb);
        hm.getAgentRequests().setExecutionDetailsRequest(hostname1, "DATANODE", Boolean.TRUE.toString());
        List<StatusCommand> cmds = hm.generateStatusCommands(hostname1);
        Assert.assertTrue("HeartbeatMonitor should generate StatusCommands for host1", ((cmds.size()) == 3));
        Assert.assertEquals("HDFS", cmds.get(0).getServiceName());
        boolean containsDATANODEStatus = false;
        boolean containsNAMENODEStatus = false;
        boolean containsSECONDARY_NAMENODEStatus = false;
        for (StatusCommand cmd : cmds) {
            boolean isDataNode = cmd.getComponentName().equals("DATANODE");
            containsDATANODEStatus |= isDataNode;
            containsNAMENODEStatus |= cmd.getComponentName().equals("NAMENODE");
            containsSECONDARY_NAMENODEStatus |= cmd.getComponentName().equals("SECONDARY_NAMENODE");
            Assert.assertTrue(((cmd.getConfigurations().size()) > 0));
            ExecutionCommand execCmd = cmd.getExecutionCommand();
            Assert.assertEquals(isDataNode, (execCmd != null));
            if (execCmd != null) {
                Map<String, String> commandParams = execCmd.getCommandParams();
                Assert.assertTrue(((HOOKS_FOLDER) + " should be included"), commandParams.containsKey(KeyNames.HOOKS_FOLDER));
            }
        }
        Assert.assertEquals(true, containsDATANODEStatus);
        Assert.assertEquals(true, containsNAMENODEStatus);
        Assert.assertEquals(true, containsSECONDARY_NAMENODEStatus);
        cmds = hm.generateStatusCommands(hostname2);
        Assert.assertTrue("HeartbeatMonitor should not generate StatusCommands for host2 because it has no services", cmds.isEmpty());
    }

    @Test
    public void testStateCommandsWithAlertsGeneration() throws InterruptedException, AmbariException, InvalidStateTransitionException {
        StackId stackId = new StackId("HDP-2.0.7");
        Clusters clusters = TestHeartbeatMonitor.injector.getInstance(Clusters.class);
        clusters.addHost(hostname1);
        setOsFamily(clusters.getHost(hostname1), "redhat", "6.3");
        clusters.addHost(hostname2);
        setOsFamily(clusters.getHost(hostname2), "redhat", "6.3");
        clusters.addCluster(clusterName, stackId);
        Cluster cluster = clusters.getCluster(clusterName);
        cluster.setDesiredStackVersion(stackId);
        RepositoryVersionEntity repositoryVersion = TestHeartbeatMonitor.helper.getOrCreateRepositoryVersion(stackId, stackId.getStackVersion());
        Set<String> hostNames = new HashSet<String>() {
            {
                add(hostname1);
                add(hostname2);
            }
        };
        clusters.mapAndPublishHostsToCluster(hostNames, clusterName);
        Service hdfs = cluster.addService(serviceName, repositoryVersion);
        hdfs.addServiceComponent(DATANODE.name());
        hdfs.getServiceComponent(DATANODE.name()).addServiceComponentHost(hostname1);
        hdfs.addServiceComponent(NAMENODE.name());
        hdfs.getServiceComponent(NAMENODE.name()).addServiceComponentHost(hostname1);
        hdfs.addServiceComponent(SECONDARY_NAMENODE.name());
        hdfs.getServiceComponent(SECONDARY_NAMENODE.name()).addServiceComponentHost(hostname1);
        hdfs.getServiceComponent(DATANODE.name()).getServiceComponentHost(hostname1).setState(INSTALLED);
        hdfs.getServiceComponent(NAMENODE.name()).getServiceComponentHost(hostname1).setState(INSTALLED);
        hdfs.getServiceComponent(SECONDARY_NAMENODE.name()).getServiceComponentHost(hostname1).setState(INSTALLED);
        // ActionQueue aq = new ActionQueue();
        ActionManager am = Mockito.mock(ActionManager.class);
        HeartbeatMonitor hm = new HeartbeatMonitor(clusters, am, heartbeatMonitorWakeupIntervalMS, TestHeartbeatMonitor.injector);
        HeartBeatHandler handler = new HeartBeatHandler(clusters, am, Encryptor.NONE, TestHeartbeatMonitor.injector);
        Register reg = new Register();
        reg.setHostname(hostname1);
        reg.setResponseId(12);
        reg.setTimestamp(((System.currentTimeMillis()) - 300));
        reg.setAgentVersion(TestHeartbeatMonitor.ambariMetaInfo.getServerVersion());
        HostInfo hi = new HostInfo();
        hi.setOS("Centos5");
        reg.setHardwareProfile(hi);
        handler.handleRegistration(reg);
        HeartBeat hb = new HeartBeat();
        hb.setHostname(hostname1);
        hb.setNodeStatus(new HostStatus(Status.HEALTHY, "cool"));
        hb.setTimestamp(System.currentTimeMillis());
        hb.setResponseId(12);
        handler.handleHeartBeat(hb);
        List<StatusCommand> cmds = hm.generateStatusCommands(hostname1);
        Assert.assertEquals("HeartbeatMonitor should generate StatusCommands for host1", 3, cmds.size());
        Assert.assertEquals("HDFS", cmds.get(0).getServiceName());
        cmds = hm.generateStatusCommands(hostname2);
        Assert.assertTrue("HeartbeatMonitor should not generate StatusCommands for host2 because it has no services", cmds.isEmpty());
    }
}

