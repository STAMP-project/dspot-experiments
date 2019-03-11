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
package org.apache.hadoop.yarn.server.resourcemanager;


import ContainerExitStatus.INVALID;
import ContainerState.COMPLETE;
import ExecutionType.GUARANTEED;
import ExecutionType.OPPORTUNISTIC;
import NodeAction.NORMAL;
import NodeAction.RESYNC;
import NodeAction.SHUTDOWN;
import NodeAttribute.PREFIX_CENTRALIZED;
import NodeAttribute.PREFIX_DISTRIBUTED;
import NodeAttributeType.STRING;
import NodeState.DECOMMISSIONED;
import NodeState.DECOMMISSIONING;
import NodeState.RUNNING;
import RMAppState.FINISHED;
import YarnConfiguration.DEFAULT_NODELABEL_CONFIGURATION_TYPE;
import YarnConfiguration.DEFAULT_RM_NODE_GRACEFUL_DECOMMISSION_TIMEOUT;
import YarnConfiguration.DISTRIBUTED_NODELABEL_CONFIGURATION_TYPE;
import YarnConfiguration.FS_NODE_ATTRIBUTE_STORE_IMPL_CLASS;
import YarnConfiguration.FS_NODE_ATTRIBUTE_STORE_ROOT_DIR;
import YarnConfiguration.NM_AUX_SERVICES;
import YarnConfiguration.NODELABEL_CONFIGURATION_TYPE;
import YarnConfiguration.RECOVERY_ENABLED;
import YarnConfiguration.RM_NM_HEARTBEAT_INTERVAL_MS;
import YarnConfiguration.RM_NM_REGISTRATION_IP_HOSTNAME_CHECK_KEY;
import YarnConfiguration.RM_NODEMANAGER_MINIMUM_VERSION;
import YarnConfiguration.RM_NODES_EXCLUDE_FILE_PATH;
import YarnConfiguration.RM_NODES_INCLUDE_FILE_PATH;
import YarnConfiguration.RM_NODE_GRACEFUL_DECOMMISSION_TIMEOUT;
import YarnConfiguration.RM_RESOURCE_TRACKER_ADDRESS;
import YarnConfiguration.RM_SCHEDULER_MINIMUM_ALLOCATION_MB;
import YarnConfiguration.RM_SCHEDULER_MINIMUM_ALLOCATION_VCORES;
import YarnConfiguration.RM_WORK_PRESERVING_RECOVERY_ENABLED;
import YarnConfiguration.TIMELINE_SERVICE_ENABLED;
import YarnConfiguration.TIMELINE_SERVICE_VERSION;
import YarnConfiguration.TIMELINE_SERVICE_WRITER_CLASS;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.DataOutputBuffer;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.net.NetUtils;
import org.apache.hadoop.net.ServerSocketUtil;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.security.token.delegation.web.DelegationTokenIdentifier;
import org.apache.hadoop.yarn.LocalConfigurationProvider;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.apache.hadoop.yarn.api.records.ExecutionType;
import org.apache.hadoop.yarn.api.records.NodeAttribute;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.api.records.NodeLabel;
import org.apache.hadoop.yarn.api.records.NodeState;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.event.Dispatcher;
import org.apache.hadoop.yarn.event.DrainDispatcher;
import org.apache.hadoop.yarn.event.Event;
import org.apache.hadoop.yarn.event.EventHandler;
import org.apache.hadoop.yarn.event.InlineDispatcher;
import org.apache.hadoop.yarn.factories.RecordFactory;
import org.apache.hadoop.yarn.factory.providers.RecordFactoryProvider;
import org.apache.hadoop.yarn.nodelabels.AttributeValue;
import org.apache.hadoop.yarn.nodelabels.NodeAttributeStore;
import org.apache.hadoop.yarn.nodelabels.NodeAttributesManager;
import org.apache.hadoop.yarn.nodelabels.NodeLabelTestBase;
import org.apache.hadoop.yarn.nodelabels.NodeLabelUtil;
import org.apache.hadoop.yarn.proto.YarnServerCommonServiceProtos.SystemCredentialsForAppsProto;
import org.apache.hadoop.yarn.server.api.ResourceTracker;
import org.apache.hadoop.yarn.server.api.ServerRMProxy;
import org.apache.hadoop.yarn.server.api.protocolrecords.NMContainerStatus;
import org.apache.hadoop.yarn.server.api.protocolrecords.NodeHeartbeatRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.NodeHeartbeatResponse;
import org.apache.hadoop.yarn.server.api.protocolrecords.NodeToAttributes;
import org.apache.hadoop.yarn.server.api.protocolrecords.RegisterNodeManagerRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.RegisterNodeManagerResponse;
import org.apache.hadoop.yarn.server.api.protocolrecords.UnRegisterNodeManagerRequest;
import org.apache.hadoop.yarn.server.api.records.AppCollectorData;
import org.apache.hadoop.yarn.server.api.records.NodeHealthStatus;
import org.apache.hadoop.yarn.server.api.records.NodeStatus;
import org.apache.hadoop.yarn.server.resourcemanager.nodelabels.FileSystemNodeAttributeStore;
import org.apache.hadoop.yarn.server.resourcemanager.nodelabels.NodeLabelsUtils;
import org.apache.hadoop.yarn.server.resourcemanager.nodelabels.NullRMNodeLabelsManager;
import org.apache.hadoop.yarn.server.resourcemanager.nodelabels.RMNodeLabelsManager;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMApp;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppImpl;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttemptImpl;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainer;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNode;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNodeEventType;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNodeImpl;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.AbstractYarnScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.QueueMetrics;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.SchedulerApplicationAttempt;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.SchedulerNode;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.SchedulerEvent;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.SchedulerEventType;
import org.apache.hadoop.yarn.server.resourcemanager.security.NMTokenSecretManagerInRM;
import org.apache.hadoop.yarn.server.resourcemanager.security.RMContainerTokenSecretManager;
import org.apache.hadoop.yarn.server.timelineservice.collector.PerNodeTimelineCollectorsAuxService;
import org.apache.hadoop.yarn.server.timelineservice.storage.FileSystemTimelineWriterImpl;
import org.apache.hadoop.yarn.server.timelineservice.storage.TimelineWriter;
import org.apache.hadoop.yarn.server.utils.BuilderUtils;
import org.apache.hadoop.yarn.server.utils.YarnServerBuilderUtils;
import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.yarn.util.YarnVersionInfo;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class TestResourceTrackerService extends NodeLabelTestBase {
    private static final File TEMP_DIR = new File(System.getProperty("test.build.data", "/tmp"), "decommision");

    private final File hostFile = new File((((TestResourceTrackerService.TEMP_DIR) + (File.separator)) + "hostFile.txt"));

    private final File excludeHostFile = new File((((TestResourceTrackerService.TEMP_DIR) + (File.separator)) + "excludeHostFile.txt"));

    private final File excludeHostXmlFile = new File((((TestResourceTrackerService.TEMP_DIR) + (File.separator)) + "excludeHostFile.xml"));

    private MockRM rm;

    /**
     * Test RM read NM next heartBeat Interval correctly from Configuration file,
     * and NM get next heartBeat Interval from RM correctly
     */
    @Test(timeout = 50000)
    public void testGetNextHeartBeatInterval() throws Exception {
        Configuration conf = new Configuration();
        conf.set(RM_NM_HEARTBEAT_INTERVAL_MS, "4000");
        rm = new MockRM(conf);
        start();
        MockNM nm1 = rm.registerNode("host1:1234", 5120);
        MockNM nm2 = rm.registerNode("host2:5678", 10240);
        NodeHeartbeatResponse nodeHeartbeat = nm1.nodeHeartbeat(true);
        Assert.assertEquals(4000, nodeHeartbeat.getNextHeartBeatInterval());
        NodeHeartbeatResponse nodeHeartbeat2 = nm2.nodeHeartbeat(true);
        Assert.assertEquals(4000, nodeHeartbeat2.getNextHeartBeatInterval());
    }

    /**
     * Decommissioning using a pre-configured include hosts file
     */
    @Test
    public void testDecommissionWithIncludeHosts() throws Exception {
        writeToHostsFile("localhost", "host1", "host2");
        Configuration conf = new Configuration();
        conf.set(RM_NODES_INCLUDE_FILE_PATH, hostFile.getAbsolutePath());
        rm = new MockRM(conf);
        start();
        MockNM nm1 = rm.registerNode("host1:1234", 5120);
        MockNM nm2 = rm.registerNode("host2:5678", 10240);
        MockNM nm3 = rm.registerNode("localhost:4433", 1024);
        ClusterMetrics metrics = ClusterMetrics.getMetrics();
        assert metrics != null;
        int metricCount = metrics.getNumDecommisionedNMs();
        NodeHeartbeatResponse nodeHeartbeat = nm1.nodeHeartbeat(true);
        Assert.assertTrue(NORMAL.equals(nodeHeartbeat.getNodeAction()));
        nodeHeartbeat = nm2.nodeHeartbeat(true);
        Assert.assertTrue(NORMAL.equals(nodeHeartbeat.getNodeAction()));
        nodeHeartbeat = nm3.nodeHeartbeat(true);
        Assert.assertTrue(NORMAL.equals(nodeHeartbeat.getNodeAction()));
        // To test that IPs also work
        String ip = NetUtils.normalizeHostName("localhost");
        writeToHostsFile("host1", ip);
        rm.getNodesListManager().refreshNodes(conf);
        checkShutdownNMCount(rm, (++metricCount));
        nodeHeartbeat = nm1.nodeHeartbeat(true);
        Assert.assertTrue(NORMAL.equals(nodeHeartbeat.getNodeAction()));
        Assert.assertEquals(1, ClusterMetrics.getMetrics().getNumShutdownNMs());
        nodeHeartbeat = nm2.nodeHeartbeat(true);
        Assert.assertTrue("Node is not decommisioned.", SHUTDOWN.equals(nodeHeartbeat.getNodeAction()));
        nodeHeartbeat = nm3.nodeHeartbeat(true);
        Assert.assertTrue(NORMAL.equals(nodeHeartbeat.getNodeAction()));
        Assert.assertEquals(metricCount, ClusterMetrics.getMetrics().getNumShutdownNMs());
        stop();
    }

    /**
     * Decommissioning using a pre-configured exclude hosts file
     */
    @Test
    public void testDecommissionWithExcludeHosts() throws Exception {
        Configuration conf = new Configuration();
        conf.set(RM_NODES_EXCLUDE_FILE_PATH, hostFile.getAbsolutePath());
        writeToHostsFile("");
        rm = new MockRM(conf);
        start();
        MockNM nm1 = rm.registerNode("host1:1234", 5120);
        MockNM nm2 = rm.registerNode("host2:5678", 10240);
        MockNM nm3 = rm.registerNode("localhost:4433", 1024);
        int metricCount = ClusterMetrics.getMetrics().getNumDecommisionedNMs();
        NodeHeartbeatResponse nodeHeartbeat = nm1.nodeHeartbeat(true);
        Assert.assertTrue(NORMAL.equals(nodeHeartbeat.getNodeAction()));
        nodeHeartbeat = nm2.nodeHeartbeat(true);
        Assert.assertTrue(NORMAL.equals(nodeHeartbeat.getNodeAction()));
        rm.drainEvents();
        // To test that IPs also work
        String ip = NetUtils.normalizeHostName("localhost");
        writeToHostsFile("host2", ip);
        rm.getNodesListManager().refreshNodes(conf);
        checkDecommissionedNMCount(rm, (metricCount + 2));
        nodeHeartbeat = nm1.nodeHeartbeat(true);
        Assert.assertTrue(NORMAL.equals(nodeHeartbeat.getNodeAction()));
        nodeHeartbeat = nm2.nodeHeartbeat(true);
        Assert.assertTrue("The decommisioned metrics are not updated", SHUTDOWN.equals(nodeHeartbeat.getNodeAction()));
        nodeHeartbeat = nm3.nodeHeartbeat(true);
        Assert.assertTrue("The decommisioned metrics are not updated", SHUTDOWN.equals(nodeHeartbeat.getNodeAction()));
        rm.drainEvents();
        writeToHostsFile("");
        rm.getNodesListManager().refreshNodes(conf);
        nm3 = rm.registerNode("localhost:4433", 1024);
        nodeHeartbeat = nm3.nodeHeartbeat(true);
        rm.drainEvents();
        Assert.assertTrue(NORMAL.equals(nodeHeartbeat.getNodeAction()));
        // decommissined node is 1 since 1 node is rejoined after updating exclude
        // file
        checkDecommissionedNMCount(rm, (metricCount + 1));
    }

    /**
     * Graceful decommission node with no running application.
     */
    @Test
    public void testGracefulDecommissionNoApp() throws Exception {
        Configuration conf = new Configuration();
        conf.set(RM_NODES_EXCLUDE_FILE_PATH, hostFile.getAbsolutePath());
        writeToHostsFile("");
        rm = new MockRM(conf);
        start();
        MockNM nm1 = rm.registerNode("host1:1234", 5120);
        MockNM nm2 = rm.registerNode("host2:5678", 10240);
        MockNM nm3 = rm.registerNode("host3:4433", 5120);
        int metricCount = ClusterMetrics.getMetrics().getNumDecommisionedNMs();
        NodeHeartbeatResponse nodeHeartbeat1 = nm1.nodeHeartbeat(true);
        NodeHeartbeatResponse nodeHeartbeat2 = nm2.nodeHeartbeat(true);
        NodeHeartbeatResponse nodeHeartbeat3 = nm3.nodeHeartbeat(true);
        Assert.assertTrue(NORMAL.equals(nodeHeartbeat1.getNodeAction()));
        Assert.assertTrue(NORMAL.equals(nodeHeartbeat2.getNodeAction()));
        Assert.assertTrue(NORMAL.equals(nodeHeartbeat3.getNodeAction()));
        rm.waitForState(nm2.getNodeId(), RUNNING);
        rm.waitForState(nm3.getNodeId(), RUNNING);
        // Graceful decommission both host2 and host3.
        writeToHostsFile("host2", "host3");
        rm.getNodesListManager().refreshNodes(conf, true);
        rm.waitForState(nm2.getNodeId(), DECOMMISSIONING);
        rm.waitForState(nm3.getNodeId(), DECOMMISSIONING);
        nodeHeartbeat1 = nm1.nodeHeartbeat(true);
        nodeHeartbeat2 = nm2.nodeHeartbeat(true);
        nodeHeartbeat3 = nm3.nodeHeartbeat(true);
        checkDecommissionedNMCount(rm, (metricCount + 2));
        rm.waitForState(nm2.getNodeId(), DECOMMISSIONED);
        rm.waitForState(nm3.getNodeId(), DECOMMISSIONED);
        Assert.assertTrue(NORMAL.equals(nodeHeartbeat1.getNodeAction()));
        Assert.assertEquals(SHUTDOWN, nodeHeartbeat2.getNodeAction());
        Assert.assertEquals(SHUTDOWN, nodeHeartbeat3.getNodeAction());
    }

    @Test
    public void testGracefulDecommissionDefaultTimeoutResolution() throws Exception {
        Configuration conf = new Configuration();
        conf.set(RM_NODES_EXCLUDE_FILE_PATH, excludeHostXmlFile.getAbsolutePath());
        writeToHostsXmlFile(excludeHostXmlFile, Pair.of("", null));
        rm = new MockRM(conf);
        start();
        int nodeMemory = 1024;
        MockNM nm1 = rm.registerNode("host1:1234", nodeMemory);
        MockNM nm2 = rm.registerNode("host2:5678", nodeMemory);
        MockNM nm3 = rm.registerNode("host3:9101", nodeMemory);
        NodeHeartbeatResponse nodeHeartbeat1 = nm1.nodeHeartbeat(true);
        NodeHeartbeatResponse nodeHeartbeat2 = nm2.nodeHeartbeat(true);
        NodeHeartbeatResponse nodeHeartbeat3 = nm3.nodeHeartbeat(true);
        Assert.assertTrue(NORMAL.equals(nodeHeartbeat1.getNodeAction()));
        Assert.assertTrue(NORMAL.equals(nodeHeartbeat2.getNodeAction()));
        Assert.assertTrue(NORMAL.equals(nodeHeartbeat3.getNodeAction()));
        rm.waitForState(nm1.getNodeId(), RUNNING);
        rm.waitForState(nm2.getNodeId(), RUNNING);
        rm.waitForState(nm3.getNodeId(), RUNNING);
        // Graceful decommission both host1 and host2, with
        // non default timeout for host1
        final Integer nm1DecommissionTimeout = 20;
        writeToHostsXmlFile(excludeHostXmlFile, Pair.of(nm1.getNodeId().getHost(), nm1DecommissionTimeout), Pair.of(nm2.getNodeId().getHost(), null));
        rm.getNodesListManager().refreshNodes(conf, true);
        rm.waitForState(nm1.getNodeId(), DECOMMISSIONING);
        rm.waitForState(nm2.getNodeId(), DECOMMISSIONING);
        Assert.assertEquals(nm1DecommissionTimeout, rm.getDecommissioningTimeout(nm1.getNodeId()));
        Integer defaultDecTimeout = conf.getInt(RM_NODE_GRACEFUL_DECOMMISSION_TIMEOUT, DEFAULT_RM_NODE_GRACEFUL_DECOMMISSION_TIMEOUT);
        Assert.assertEquals(defaultDecTimeout, rm.getDecommissioningTimeout(nm2.getNodeId()));
        // Graceful decommission host3 with a new default timeout
        final Integer newDefaultDecTimeout = defaultDecTimeout + 10;
        writeToHostsXmlFile(excludeHostXmlFile, Pair.of(nm3.getNodeId().getHost(), null));
        conf.setInt(RM_NODE_GRACEFUL_DECOMMISSION_TIMEOUT, newDefaultDecTimeout);
        rm.getNodesListManager().refreshNodes(conf, true);
        rm.waitForState(nm3.getNodeId(), DECOMMISSIONING);
        Assert.assertEquals(newDefaultDecTimeout, rm.getDecommissioningTimeout(nm3.getNodeId()));
    }

    /**
     * Graceful decommission node with running application.
     */
    @Test
    public void testGracefulDecommissionWithApp() throws Exception {
        Configuration conf = new Configuration();
        conf.set(RM_NODES_EXCLUDE_FILE_PATH, hostFile.getAbsolutePath());
        writeToHostsFile("");
        rm = new MockRM(conf);
        start();
        MockNM nm1 = rm.registerNode("host1:1234", 10240);
        MockNM nm2 = rm.registerNode("host2:5678", 20480);
        MockNM nm3 = rm.registerNode("host3:4433", 10240);
        NodeId id1 = nm1.getNodeId();
        NodeId id3 = nm3.getNodeId();
        rm.waitForState(id1, RUNNING);
        rm.waitForState(id3, RUNNING);
        // Create an app and launch two containers on host1.
        RMApp app = rm.submitApp(2000);
        MockAM am = MockRM.launchAndRegisterAM(app, rm, nm1);
        ApplicationAttemptId aaid = app.getCurrentAppAttempt().getAppAttemptId();
        nm1.nodeHeartbeat(aaid, 2, ContainerState.RUNNING);
        nm3.nodeHeartbeat(true);
        // Graceful decommission host1 and host3
        writeToHostsFile("host1", "host3");
        rm.getNodesListManager().refreshNodes(conf, true);
        rm.waitForState(id1, DECOMMISSIONING);
        rm.waitForState(id3, DECOMMISSIONING);
        // host1 should be DECOMMISSIONING due to running containers.
        // host3 should become DECOMMISSIONED.
        nm1.nodeHeartbeat(true);
        nm3.nodeHeartbeat(true);
        rm.waitForState(id1, DECOMMISSIONING);
        rm.waitForState(id3, DECOMMISSIONED);
        nm1.nodeHeartbeat(aaid, 2, ContainerState.RUNNING);
        // Complete containers on host1.
        // Since the app is still RUNNING, expect NodeAction.NORMAL.
        NodeHeartbeatResponse nodeHeartbeat1 = nm1.nodeHeartbeat(aaid, 2, COMPLETE);
        Assert.assertEquals(NORMAL, nodeHeartbeat1.getNodeAction());
        // Finish the app and verified DECOMMISSIONED.
        MockRM.finishAMAndVerifyAppState(app, rm, nm1, am);
        rm.waitForState(app.getApplicationId(), FINISHED);
        nodeHeartbeat1 = nm1.nodeHeartbeat(aaid, 2, COMPLETE);
        Assert.assertEquals(SHUTDOWN, nodeHeartbeat1.getNodeAction());
        rm.waitForState(id1, DECOMMISSIONED);
    }

    /**
     * Decommissioning using a post-configured include hosts file
     */
    @Test
    public void testAddNewIncludePathToConfiguration() throws Exception {
        Configuration conf = new Configuration();
        rm = new MockRM(conf);
        start();
        MockNM nm1 = rm.registerNode("host1:1234", 5120);
        MockNM nm2 = rm.registerNode("host2:5678", 10240);
        ClusterMetrics metrics = ClusterMetrics.getMetrics();
        assert metrics != null;
        int initialMetricCount = metrics.getNumShutdownNMs();
        NodeHeartbeatResponse nodeHeartbeat = nm1.nodeHeartbeat(true);
        Assert.assertEquals(NORMAL, nodeHeartbeat.getNodeAction());
        nodeHeartbeat = nm2.nodeHeartbeat(true);
        Assert.assertEquals(NORMAL, nodeHeartbeat.getNodeAction());
        writeToHostsFile("host1");
        conf.set(RM_NODES_INCLUDE_FILE_PATH, hostFile.getAbsolutePath());
        rm.getNodesListManager().refreshNodes(conf);
        checkShutdownNMCount(rm, (++initialMetricCount));
        nodeHeartbeat = nm1.nodeHeartbeat(true);
        Assert.assertEquals("Node should not have been shutdown.", NORMAL, nodeHeartbeat.getNodeAction());
        NodeState nodeState = getRMContext().getInactiveRMNodes().get(nm2.getNodeId()).getState();
        Assert.assertEquals(("Node should have been shutdown but is in state" + nodeState), NodeState.SHUTDOWN, nodeState);
    }

    /**
     * Decommissioning using a post-configured exclude hosts file
     */
    @Test
    public void testAddNewExcludePathToConfiguration() throws Exception {
        Configuration conf = new Configuration();
        rm = new MockRM(conf);
        start();
        MockNM nm1 = rm.registerNode("host1:1234", 5120);
        MockNM nm2 = rm.registerNode("host2:5678", 10240);
        ClusterMetrics metrics = ClusterMetrics.getMetrics();
        assert metrics != null;
        int initialMetricCount = metrics.getNumDecommisionedNMs();
        NodeHeartbeatResponse nodeHeartbeat = nm1.nodeHeartbeat(true);
        Assert.assertEquals(NORMAL, nodeHeartbeat.getNodeAction());
        nodeHeartbeat = nm2.nodeHeartbeat(true);
        Assert.assertEquals(NORMAL, nodeHeartbeat.getNodeAction());
        writeToHostsFile("host2");
        conf.set(RM_NODES_EXCLUDE_FILE_PATH, hostFile.getAbsolutePath());
        rm.getNodesListManager().refreshNodes(conf);
        checkDecommissionedNMCount(rm, (++initialMetricCount));
        nodeHeartbeat = nm1.nodeHeartbeat(true);
        Assert.assertEquals("Node should not have been decommissioned.", NORMAL, nodeHeartbeat.getNodeAction());
        nodeHeartbeat = nm2.nodeHeartbeat(true);
        Assert.assertEquals(("Node should have been decommissioned but is in state" + (nodeHeartbeat.getNodeAction())), SHUTDOWN, nodeHeartbeat.getNodeAction());
    }

    @Test
    public void testNodeRegistrationSuccess() throws Exception {
        writeToHostsFile("host2");
        Configuration conf = new Configuration();
        conf.set(RM_NODES_INCLUDE_FILE_PATH, hostFile.getAbsolutePath());
        rm = new MockRM(conf);
        start();
        ResourceTrackerService resourceTrackerService = getResourceTrackerService();
        RegisterNodeManagerRequest req = Records.newRecord(RegisterNodeManagerRequest.class);
        NodeId nodeId = NodeId.newInstance("host2", 1234);
        Resource capability = BuilderUtils.newResource(1024, 1);
        req.setResource(capability);
        req.setNodeId(nodeId);
        req.setHttpPort(1234);
        req.setNMVersion(YarnVersionInfo.getVersion());
        // trying to register a invalid node.
        RegisterNodeManagerResponse response = resourceTrackerService.registerNodeManager(req);
        Assert.assertEquals(NORMAL, response.getNodeAction());
    }

    @Test
    public void testNodeRegistrationWithLabels() throws Exception {
        writeToHostsFile("host2");
        Configuration conf = new Configuration();
        conf.set(RM_NODES_INCLUDE_FILE_PATH, hostFile.getAbsolutePath());
        conf.set(NODELABEL_CONFIGURATION_TYPE, DISTRIBUTED_NODELABEL_CONFIGURATION_TYPE);
        final RMNodeLabelsManager nodeLabelsMgr = new NullRMNodeLabelsManager();
        rm = new MockRM(conf) {
            @Override
            protected RMNodeLabelsManager createNodeLabelManager() {
                return nodeLabelsMgr;
            }
        };
        start();
        try {
            nodeLabelsMgr.addToCluserNodeLabelsWithDefaultExclusivity(toSet("A", "B", "C"));
        } catch (IOException e) {
            Assert.fail("Caught Exception while initializing");
            e.printStackTrace();
        }
        ResourceTrackerService resourceTrackerService = getResourceTrackerService();
        RegisterNodeManagerRequest registerReq = Records.newRecord(RegisterNodeManagerRequest.class);
        NodeId nodeId = NodeId.newInstance("host2", 1234);
        Resource capability = BuilderUtils.newResource(1024, 1);
        registerReq.setResource(capability);
        registerReq.setNodeId(nodeId);
        registerReq.setHttpPort(1234);
        registerReq.setNMVersion(YarnVersionInfo.getVersion());
        registerReq.setNodeLabels(toSet(NodeLabel.newInstance("A")));
        RegisterNodeManagerResponse response = resourceTrackerService.registerNodeManager(registerReq);
        Assert.assertEquals("Action should be normal on valid Node Labels", NORMAL, response.getNodeAction());
        assertCollectionEquals(nodeLabelsMgr.getNodeLabels().get(nodeId), NodeLabelsUtils.convertToStringSet(registerReq.getNodeLabels()));
        Assert.assertTrue("Valid Node Labels were not accepted by RM", response.getAreNodeLabelsAcceptedByRM());
        stop();
    }

    @Test
    public void testNodeRegistrationWithInvalidLabels() throws Exception {
        writeToHostsFile("host2");
        Configuration conf = new Configuration();
        conf.set(RM_NODES_INCLUDE_FILE_PATH, hostFile.getAbsolutePath());
        conf.set(NODELABEL_CONFIGURATION_TYPE, DISTRIBUTED_NODELABEL_CONFIGURATION_TYPE);
        final RMNodeLabelsManager nodeLabelsMgr = new NullRMNodeLabelsManager();
        rm = new MockRM(conf) {
            @Override
            protected RMNodeLabelsManager createNodeLabelManager() {
                return nodeLabelsMgr;
            }
        };
        start();
        try {
            nodeLabelsMgr.addToCluserNodeLabelsWithDefaultExclusivity(toSet("X", "Y", "Z"));
        } catch (IOException e) {
            Assert.fail("Caught Exception while initializing");
            e.printStackTrace();
        }
        ResourceTrackerService resourceTrackerService = getResourceTrackerService();
        RegisterNodeManagerRequest registerReq = Records.newRecord(RegisterNodeManagerRequest.class);
        NodeId nodeId = NodeId.newInstance("host2", 1234);
        Resource capability = BuilderUtils.newResource(1024, 1);
        registerReq.setResource(capability);
        registerReq.setNodeId(nodeId);
        registerReq.setHttpPort(1234);
        registerReq.setNMVersion(YarnVersionInfo.getVersion());
        registerReq.setNodeLabels(toNodeLabelSet("A", "B", "C"));
        RegisterNodeManagerResponse response = resourceTrackerService.registerNodeManager(registerReq);
        Assert.assertEquals("On Invalid Node Labels action is expected to be normal", NORMAL, response.getNodeAction());
        Assert.assertNull(nodeLabelsMgr.getNodeLabels().get(nodeId));
        Assert.assertNotNull(response.getDiagnosticsMessage());
        Assert.assertFalse("Node Labels should not accepted by RM If Invalid", response.getAreNodeLabelsAcceptedByRM());
        if ((rm) != null) {
            stop();
        }
    }

    @Test
    public void testNodeRegistrationWithInvalidLabelsSyntax() throws Exception {
        writeToHostsFile("host2");
        Configuration conf = new Configuration();
        conf.set(RM_NODES_INCLUDE_FILE_PATH, hostFile.getAbsolutePath());
        conf.set(NODELABEL_CONFIGURATION_TYPE, DISTRIBUTED_NODELABEL_CONFIGURATION_TYPE);
        final RMNodeLabelsManager nodeLabelsMgr = new NullRMNodeLabelsManager();
        rm = new MockRM(conf) {
            @Override
            protected RMNodeLabelsManager createNodeLabelManager() {
                return nodeLabelsMgr;
            }
        };
        start();
        try {
            nodeLabelsMgr.addToCluserNodeLabelsWithDefaultExclusivity(toSet("X", "Y", "Z"));
        } catch (IOException e) {
            Assert.fail("Caught Exception while initializing");
            e.printStackTrace();
        }
        ResourceTrackerService resourceTrackerService = getResourceTrackerService();
        RegisterNodeManagerRequest req = Records.newRecord(RegisterNodeManagerRequest.class);
        NodeId nodeId = NodeId.newInstance("host2", 1234);
        Resource capability = BuilderUtils.newResource(1024, 1);
        req.setResource(capability);
        req.setNodeId(nodeId);
        req.setHttpPort(1234);
        req.setNMVersion(YarnVersionInfo.getVersion());
        req.setNodeLabels(toNodeLabelSet("#Y"));
        RegisterNodeManagerResponse response = resourceTrackerService.registerNodeManager(req);
        Assert.assertEquals("On Invalid Node Labels action is expected to be normal", NORMAL, response.getNodeAction());
        Assert.assertNull(nodeLabelsMgr.getNodeLabels().get(nodeId));
        Assert.assertNotNull(response.getDiagnosticsMessage());
        Assert.assertFalse("Node Labels should not accepted by RM If Invalid", response.getAreNodeLabelsAcceptedByRM());
        if ((rm) != null) {
            stop();
        }
    }

    @Test
    public void testNodeRegistrationWithCentralLabelConfig() throws Exception {
        writeToHostsFile("host2");
        Configuration conf = new Configuration();
        conf.set(RM_NODES_INCLUDE_FILE_PATH, hostFile.getAbsolutePath());
        conf.set(NODELABEL_CONFIGURATION_TYPE, DEFAULT_NODELABEL_CONFIGURATION_TYPE);
        final RMNodeLabelsManager nodeLabelsMgr = new NullRMNodeLabelsManager();
        rm = new MockRM(conf) {
            @Override
            protected RMNodeLabelsManager createNodeLabelManager() {
                return nodeLabelsMgr;
            }
        };
        start();
        try {
            nodeLabelsMgr.addToCluserNodeLabelsWithDefaultExclusivity(toSet("A", "B", "C"));
        } catch (IOException e) {
            Assert.fail("Caught Exception while initializing");
            e.printStackTrace();
        }
        ResourceTrackerService resourceTrackerService = getResourceTrackerService();
        RegisterNodeManagerRequest req = Records.newRecord(RegisterNodeManagerRequest.class);
        NodeId nodeId = NodeId.newInstance("host2", 1234);
        Resource capability = BuilderUtils.newResource(1024, 1);
        req.setResource(capability);
        req.setNodeId(nodeId);
        req.setHttpPort(1234);
        req.setNMVersion(YarnVersionInfo.getVersion());
        req.setNodeLabels(toNodeLabelSet("A"));
        RegisterNodeManagerResponse response = resourceTrackerService.registerNodeManager(req);
        // registered to RM with central label config
        Assert.assertEquals(NORMAL, response.getNodeAction());
        Assert.assertNull(nodeLabelsMgr.getNodeLabels().get(nodeId));
        Assert.assertFalse(("Node Labels should not accepted by RM If its configured with " + "Central configuration"), response.getAreNodeLabelsAcceptedByRM());
        if ((rm) != null) {
            stop();
        }
    }

    @Test
    public void testNodeRegistrationWithAttributes() throws Exception {
        writeToHostsFile("host2");
        Configuration conf = new Configuration();
        conf.set(RM_NODES_INCLUDE_FILE_PATH, hostFile.getAbsolutePath());
        conf.setClass(FS_NODE_ATTRIBUTE_STORE_IMPL_CLASS, FileSystemNodeAttributeStore.class, NodeAttributeStore.class);
        File tempDir = File.createTempFile("nattr", ".tmp");
        tempDir.delete();
        tempDir.mkdirs();
        tempDir.deleteOnExit();
        conf.set(FS_NODE_ATTRIBUTE_STORE_ROOT_DIR, tempDir.getAbsolutePath());
        rm = new MockRM(conf);
        start();
        ResourceTrackerService resourceTrackerService = getResourceTrackerService();
        RegisterNodeManagerRequest registerReq = Records.newRecord(RegisterNodeManagerRequest.class);
        NodeId nodeId = NodeId.newInstance("host2", 1234);
        Resource capability = BuilderUtils.newResource(1024, 1);
        NodeAttribute nodeAttribute1 = NodeAttribute.newInstance(PREFIX_DISTRIBUTED, "Attr1", STRING, "V1");
        NodeAttribute nodeAttribute2 = NodeAttribute.newInstance(PREFIX_DISTRIBUTED, "Attr2", STRING, "V2");
        registerReq.setResource(capability);
        registerReq.setNodeId(nodeId);
        registerReq.setHttpPort(1234);
        registerReq.setNMVersion(YarnVersionInfo.getVersion());
        registerReq.setNodeAttributes(toSet(nodeAttribute1, nodeAttribute2));
        RegisterNodeManagerResponse response = resourceTrackerService.registerNodeManager(registerReq);
        Assert.assertEquals("Action should be normal on valid Node Attributes", NORMAL, response.getNodeAction());
        Assert.assertTrue(NodeLabelUtil.isNodeAttributesEquals(getRMContext().getNodeAttributesManager().getAttributesForNode(nodeId.getHost()).keySet(), registerReq.getNodeAttributes()));
        Assert.assertTrue("Valid Node Attributes were not accepted by RM", response.getAreNodeAttributesAcceptedByRM());
        if ((rm) != null) {
            stop();
        }
    }

    @Test
    public void testNodeRegistrationWithInvalidAttributes() throws Exception {
        writeToHostsFile("host2");
        Configuration conf = new Configuration();
        conf.set(RM_NODES_INCLUDE_FILE_PATH, hostFile.getAbsolutePath());
        conf.setClass(FS_NODE_ATTRIBUTE_STORE_IMPL_CLASS, FileSystemNodeAttributeStore.class, NodeAttributeStore.class);
        conf.set(FS_NODE_ATTRIBUTE_STORE_ROOT_DIR, TestResourceTrackerService.TEMP_DIR.getAbsolutePath());
        rm = new MockRM(conf);
        start();
        ResourceTrackerService resourceTrackerService = getResourceTrackerService();
        RegisterNodeManagerRequest req = Records.newRecord(RegisterNodeManagerRequest.class);
        NodeId nodeId = NodeId.newInstance("host2", 1234);
        Resource capability = BuilderUtils.newResource(1024, 1);
        NodeAttribute validNodeAttribute = NodeAttribute.newInstance(PREFIX_DISTRIBUTED, "Attr1", STRING, "V1");
        NodeAttribute invalidPrefixNodeAttribute = NodeAttribute.newInstance("_P", "Attr1", STRING, "V2");
        NodeAttribute invalidNameNodeAttribute = NodeAttribute.newInstance(PREFIX_DISTRIBUTED, "_N", STRING, "V2");
        NodeAttribute invalidValueNodeAttribute = NodeAttribute.newInstance(PREFIX_DISTRIBUTED, "Attr2", STRING, "...");
        req.setResource(capability);
        req.setNodeId(nodeId);
        req.setHttpPort(1234);
        req.setNMVersion(YarnVersionInfo.getVersion());
        // check invalid prefix
        req.setNodeAttributes(toSet(validNodeAttribute, invalidPrefixNodeAttribute));
        RegisterNodeManagerResponse response = resourceTrackerService.registerNodeManager(req);
        Assert.assertEquals(0, getRMContext().getNodeAttributesManager().getAttributesForNode(nodeId.getHost()).size());
        assertRegisterResponseForInvalidAttributes(response);
        Assert.assertTrue(response.getDiagnosticsMessage().endsWith("attributes in HB must have prefix nm.yarn.io"));
        // check invalid name
        req.setNodeAttributes(toSet(validNodeAttribute, invalidNameNodeAttribute));
        response = resourceTrackerService.registerNodeManager(req);
        Assert.assertEquals(0, getRMContext().getNodeAttributesManager().getAttributesForNode(nodeId.getHost()).size());
        assertRegisterResponseForInvalidAttributes(response);
        Assert.assertTrue(response.getDiagnosticsMessage().startsWith("attribute name should only contains"));
        // check invalid value
        req.setNodeAttributes(toSet(validNodeAttribute, invalidValueNodeAttribute));
        response = resourceTrackerService.registerNodeManager(req);
        Assert.assertEquals(0, getRMContext().getNodeAttributesManager().getAttributesForNode(nodeId.getHost()).size());
        assertRegisterResponseForInvalidAttributes(response);
        Assert.assertTrue(response.getDiagnosticsMessage().startsWith("attribute value should only contains"));
        if ((rm) != null) {
            stop();
        }
    }

    @Test
    public void testNodeHeartBeatWithLabels() throws Exception {
        writeToHostsFile("host2");
        Configuration conf = new Configuration();
        conf.set(RM_NODES_INCLUDE_FILE_PATH, hostFile.getAbsolutePath());
        conf.set(NODELABEL_CONFIGURATION_TYPE, DISTRIBUTED_NODELABEL_CONFIGURATION_TYPE);
        final RMNodeLabelsManager nodeLabelsMgr = new NullRMNodeLabelsManager();
        rm = new MockRM(conf) {
            @Override
            protected RMNodeLabelsManager createNodeLabelManager() {
                return nodeLabelsMgr;
            }
        };
        start();
        // adding valid labels
        try {
            nodeLabelsMgr.addToCluserNodeLabelsWithDefaultExclusivity(toSet("A", "B", "C"));
        } catch (IOException e) {
            Assert.fail("Caught Exception while initializing");
            e.printStackTrace();
        }
        // Registering of labels and other required info to RM
        ResourceTrackerService resourceTrackerService = getResourceTrackerService();
        RegisterNodeManagerRequest registerReq = Records.newRecord(RegisterNodeManagerRequest.class);
        NodeId nodeId = NodeId.newInstance("host2", 1234);
        Resource capability = BuilderUtils.newResource(1024, 1);
        registerReq.setResource(capability);
        registerReq.setNodeId(nodeId);
        registerReq.setHttpPort(1234);
        registerReq.setNMVersion(YarnVersionInfo.getVersion());
        registerReq.setNodeLabels(toNodeLabelSet("A"));// Node register label

        RegisterNodeManagerResponse registerResponse = resourceTrackerService.registerNodeManager(registerReq);
        // modification of labels during heartbeat
        NodeHeartbeatRequest heartbeatReq = Records.newRecord(NodeHeartbeatRequest.class);
        heartbeatReq.setNodeLabels(toNodeLabelSet("B"));// Node heartbeat label update

        NodeStatus nodeStatusObject = getNodeStatusObject(nodeId);
        heartbeatReq.setNodeStatus(nodeStatusObject);
        heartbeatReq.setLastKnownNMTokenMasterKey(registerResponse.getNMTokenMasterKey());
        heartbeatReq.setLastKnownContainerTokenMasterKey(registerResponse.getContainerTokenMasterKey());
        NodeHeartbeatResponse nodeHeartbeatResponse = resourceTrackerService.nodeHeartbeat(heartbeatReq);
        Assert.assertEquals("InValid Node Labels were not accepted by RM", NORMAL, nodeHeartbeatResponse.getNodeAction());
        assertCollectionEquals(nodeLabelsMgr.getNodeLabels().get(nodeId), NodeLabelsUtils.convertToStringSet(heartbeatReq.getNodeLabels()));
        Assert.assertTrue("Valid Node Labels were not accepted by RM", nodeHeartbeatResponse.getAreNodeLabelsAcceptedByRM());
        // After modification of labels next heartbeat sends null informing no update
        Set<String> oldLabels = nodeLabelsMgr.getNodeLabels().get(nodeId);
        int responseId = nodeStatusObject.getResponseId();
        heartbeatReq = Records.newRecord(NodeHeartbeatRequest.class);
        heartbeatReq.setNodeLabels(null);// Node heartbeat label update

        nodeStatusObject = getNodeStatusObject(nodeId);
        nodeStatusObject.setResponseId((responseId + 1));
        heartbeatReq.setNodeStatus(nodeStatusObject);
        heartbeatReq.setLastKnownNMTokenMasterKey(registerResponse.getNMTokenMasterKey());
        heartbeatReq.setLastKnownContainerTokenMasterKey(registerResponse.getContainerTokenMasterKey());
        nodeHeartbeatResponse = resourceTrackerService.nodeHeartbeat(heartbeatReq);
        Assert.assertEquals("InValid Node Labels were not accepted by RM", NORMAL, nodeHeartbeatResponse.getNodeAction());
        assertCollectionEquals(nodeLabelsMgr.getNodeLabels().get(nodeId), oldLabels);
        Assert.assertFalse("Node Labels should not accepted by RM", nodeHeartbeatResponse.getAreNodeLabelsAcceptedByRM());
        stop();
    }

    @Test
    public void testNodeHeartbeatWithNodeAttributes() throws Exception {
        writeToHostsFile("host2");
        Configuration conf = new Configuration();
        conf.set(RM_NODES_INCLUDE_FILE_PATH, hostFile.getAbsolutePath());
        conf.setClass(FS_NODE_ATTRIBUTE_STORE_IMPL_CLASS, FileSystemNodeAttributeStore.class, NodeAttributeStore.class);
        conf.set(FS_NODE_ATTRIBUTE_STORE_ROOT_DIR, TestResourceTrackerService.TEMP_DIR.getAbsolutePath());
        rm = new MockRM(conf);
        start();
        // Register to RM
        ResourceTrackerService resourceTrackerService = getResourceTrackerService();
        RegisterNodeManagerRequest registerReq = Records.newRecord(RegisterNodeManagerRequest.class);
        NodeId nodeId = NodeId.newInstance("host2", 1234);
        Resource capability = BuilderUtils.newResource(1024, 1);
        registerReq.setResource(capability);
        registerReq.setNodeId(nodeId);
        registerReq.setHttpPort(1234);
        registerReq.setNMVersion(YarnVersionInfo.getVersion());
        RegisterNodeManagerResponse registerResponse = resourceTrackerService.registerNodeManager(registerReq);
        Set<NodeAttribute> nodeAttributes = new HashSet<>();
        nodeAttributes.add(NodeAttribute.newInstance(PREFIX_DISTRIBUTED, "host", STRING, "host2"));
        // Set node attributes in HB.
        NodeHeartbeatRequest heartbeatReq = Records.newRecord(NodeHeartbeatRequest.class);
        NodeStatus nodeStatusObject = getNodeStatusObject(nodeId);
        int responseId = nodeStatusObject.getResponseId();
        heartbeatReq.setNodeStatus(nodeStatusObject);
        heartbeatReq.setLastKnownNMTokenMasterKey(registerResponse.getNMTokenMasterKey());
        heartbeatReq.setLastKnownContainerTokenMasterKey(registerResponse.getContainerTokenMasterKey());
        heartbeatReq.setNodeAttributes(nodeAttributes);
        resourceTrackerService.nodeHeartbeat(heartbeatReq);
        // Ensure RM gets correct node attributes update.
        NodeAttributesManager attributeManager = getRMContext().getNodeAttributesManager();
        Map<NodeAttribute, AttributeValue> attrs = attributeManager.getAttributesForNode(nodeId.getHost());
        Assert.assertEquals(1, attrs.size());
        NodeAttribute na = attrs.keySet().iterator().next();
        Assert.assertEquals("host", na.getAttributeKey().getAttributeName());
        Assert.assertEquals("host2", na.getAttributeValue());
        Assert.assertEquals(STRING, na.getAttributeType());
        // Send another HB to RM with updated node atrribute
        nodeAttributes.clear();
        nodeAttributes.add(NodeAttribute.newInstance(PREFIX_DISTRIBUTED, "host", STRING, "host3"));
        nodeStatusObject = getNodeStatusObject(nodeId);
        nodeStatusObject.setResponseId((++responseId));
        heartbeatReq.setNodeStatus(nodeStatusObject);
        heartbeatReq.setNodeAttributes(nodeAttributes);
        resourceTrackerService.nodeHeartbeat(heartbeatReq);
        // Make sure RM gets the updated attribute
        attrs = attributeManager.getAttributesForNode(nodeId.getHost());
        Assert.assertEquals(1, attrs.size());
        na = attrs.keySet().iterator().next();
        Assert.assertEquals("host", na.getAttributeKey().getAttributeName());
        Assert.assertEquals("host3", na.getAttributeValue());
        Assert.assertEquals(STRING, na.getAttributeType());
    }

    @Test
    public void testNodeHeartbeatWithInvalidNodeAttributes() throws Exception {
        writeToHostsFile("host2");
        Configuration conf = new Configuration();
        conf.set(RM_NODES_INCLUDE_FILE_PATH, hostFile.getAbsolutePath());
        conf.setClass(FS_NODE_ATTRIBUTE_STORE_IMPL_CLASS, FileSystemNodeAttributeStore.class, NodeAttributeStore.class);
        conf.set(FS_NODE_ATTRIBUTE_STORE_ROOT_DIR, TestResourceTrackerService.TEMP_DIR.getAbsolutePath());
        rm = new MockRM(conf);
        start();
        // Register to RM
        ResourceTrackerService resourceTrackerService = getResourceTrackerService();
        RegisterNodeManagerRequest registerReq = Records.newRecord(RegisterNodeManagerRequest.class);
        NodeId nodeId = NodeId.newInstance("host2", 1234);
        Resource capability = BuilderUtils.newResource(1024, 1);
        registerReq.setResource(capability);
        registerReq.setNodeId(nodeId);
        registerReq.setHttpPort(1234);
        registerReq.setNMVersion(YarnVersionInfo.getVersion());
        RegisterNodeManagerResponse registerResponse = resourceTrackerService.registerNodeManager(registerReq);
        NodeAttribute validNodeAttribute = NodeAttribute.newInstance(PREFIX_DISTRIBUTED, "host", STRING, "host2");
        NodeAttribute invalidPrefixNodeAttribute = NodeAttribute.newInstance("_P", "Attr1", STRING, "V2");
        NodeAttribute invalidNameNodeAttribute = NodeAttribute.newInstance(PREFIX_DISTRIBUTED, "_N", STRING, "V2");
        NodeAttribute invalidValueNodeAttribute = NodeAttribute.newInstance(PREFIX_DISTRIBUTED, "Attr2", STRING, "...");
        // Set node attributes in HB.
        NodeHeartbeatRequest heartbeatReq = Records.newRecord(NodeHeartbeatRequest.class);
        NodeStatus nodeStatusObject = getNodeStatusObject(nodeId);
        int responseId = nodeStatusObject.getResponseId();
        heartbeatReq.setNodeStatus(nodeStatusObject);
        heartbeatReq.setLastKnownNMTokenMasterKey(registerResponse.getNMTokenMasterKey());
        heartbeatReq.setLastKnownContainerTokenMasterKey(registerResponse.getContainerTokenMasterKey());
        heartbeatReq.setNodeAttributes(toSet(validNodeAttribute));
        // Send first HB to RM with invalid prefix node attributes
        heartbeatReq.setNodeAttributes(toSet(validNodeAttribute, invalidPrefixNodeAttribute));
        NodeHeartbeatResponse response = resourceTrackerService.nodeHeartbeat(heartbeatReq);
        Assert.assertEquals(0, getRMContext().getNodeAttributesManager().getAttributesForNode(nodeId.getHost()).size());
        assertNodeHeartbeatResponseForInvalidAttributes(response);
        Assert.assertTrue(response.getDiagnosticsMessage().endsWith("attributes in HB must have prefix nm.yarn.io"));
        // Send another HB to RM with invalid name node attributes
        nodeStatusObject.setResponseId((++responseId));
        heartbeatReq.setNodeAttributes(toSet(validNodeAttribute, invalidNameNodeAttribute));
        response = resourceTrackerService.nodeHeartbeat(heartbeatReq);
        Assert.assertEquals(0, getRMContext().getNodeAttributesManager().getAttributesForNode(nodeId.getHost()).size());
        assertNodeHeartbeatResponseForInvalidAttributes(response);
        Assert.assertTrue(response.getDiagnosticsMessage().startsWith("attribute name should only contains"));
        // Send another HB to RM with invalid value node attributes
        nodeStatusObject.setResponseId((++responseId));
        heartbeatReq.setNodeAttributes(toSet(validNodeAttribute, invalidValueNodeAttribute));
        response = resourceTrackerService.nodeHeartbeat(heartbeatReq);
        Assert.assertEquals(0, getRMContext().getNodeAttributesManager().getAttributesForNode(nodeId.getHost()).size());
        assertNodeHeartbeatResponseForInvalidAttributes(response);
        Assert.assertTrue(response.getDiagnosticsMessage().startsWith("attribute value should only contains"));
        // Send another HB to RM with updated node attribute
        NodeAttribute updatedNodeAttribute = NodeAttribute.newInstance(PREFIX_DISTRIBUTED, "host", STRING, "host3");
        nodeStatusObject.setResponseId((++responseId));
        heartbeatReq.setNodeAttributes(toSet(updatedNodeAttribute));
        resourceTrackerService.nodeHeartbeat(heartbeatReq);
        // Make sure RM gets the updated attribute
        NodeAttributesManager attributeManager = getRMContext().getNodeAttributesManager();
        Map<NodeAttribute, AttributeValue> attrs = attributeManager.getAttributesForNode(nodeId.getHost());
        Assert.assertEquals(1, attrs.size());
        NodeAttribute na = attrs.keySet().iterator().next();
        Assert.assertEquals("host", na.getAttributeKey().getAttributeName());
        Assert.assertEquals("host3", na.getAttributeValue());
        Assert.assertEquals(STRING, na.getAttributeType());
    }

    @Test
    public void testNodeHeartbeatOnlyUpdateNodeAttributesIfNeeded() throws Exception {
        writeToHostsFile("host2");
        Configuration conf = new Configuration();
        conf.set(RM_NODES_INCLUDE_FILE_PATH, hostFile.getAbsolutePath());
        conf.setClass(FS_NODE_ATTRIBUTE_STORE_IMPL_CLASS, TestResourceTrackerService.NullNodeAttributeStore.class, NodeAttributeStore.class);
        conf.set(FS_NODE_ATTRIBUTE_STORE_ROOT_DIR, TestResourceTrackerService.TEMP_DIR.getAbsolutePath());
        rm = new MockRM(conf);
        start();
        // spy node attributes manager
        NodeAttributesManager tmpAttributeManager = getRMContext().getNodeAttributesManager();
        NodeAttributesManager spyAttributeManager = Mockito.spy(tmpAttributeManager);
        getRMContext().setNodeAttributesManager(spyAttributeManager);
        AtomicInteger count = new AtomicInteger(0);
        Mockito.doAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock invocation) throws Exception {
                count.incrementAndGet();
                tmpAttributeManager.replaceNodeAttributes(((String) (invocation.getArguments()[0])), ((Map<String, Set<NodeAttribute>>) (invocation.getArguments()[1])));
                return null;
            }
        }).when(spyAttributeManager).replaceNodeAttributes(Mockito.any(String.class), Mockito.any(Map.class));
        // Register to RM
        ResourceTrackerService resourceTrackerService = getResourceTrackerService();
        RegisterNodeManagerRequest registerReq = Records.newRecord(RegisterNodeManagerRequest.class);
        NodeId nodeId = NodeId.newInstance("host2", 1234);
        Resource capability = BuilderUtils.newResource(1024, 1);
        registerReq.setResource(capability);
        registerReq.setNodeId(nodeId);
        registerReq.setHttpPort(1234);
        registerReq.setNMVersion(YarnVersionInfo.getVersion());
        RegisterNodeManagerResponse registerResponse = resourceTrackerService.registerNodeManager(registerReq);
        Set<NodeAttribute> nodeAttributes = new HashSet<>();
        nodeAttributes.add(NodeAttribute.newInstance(PREFIX_DISTRIBUTED, "host", STRING, "host2"));
        // Set node attributes in HB.
        NodeHeartbeatRequest heartbeatReq = Records.newRecord(NodeHeartbeatRequest.class);
        NodeStatus nodeStatusObject = getNodeStatusObject(nodeId);
        int responseId = nodeStatusObject.getResponseId();
        heartbeatReq.setNodeStatus(nodeStatusObject);
        heartbeatReq.setLastKnownNMTokenMasterKey(registerResponse.getNMTokenMasterKey());
        heartbeatReq.setLastKnownContainerTokenMasterKey(registerResponse.getContainerTokenMasterKey());
        heartbeatReq.setNodeAttributes(nodeAttributes);
        resourceTrackerService.nodeHeartbeat(heartbeatReq);
        // Ensure RM gets correct node attributes update.
        Map<NodeAttribute, AttributeValue> attrs = spyAttributeManager.getAttributesForNode(nodeId.getHost());
        spyAttributeManager.getNodesToAttributes(ImmutableSet.of(nodeId.getHost()));
        Assert.assertEquals(1, attrs.size());
        NodeAttribute na = attrs.keySet().iterator().next();
        Assert.assertEquals("host", na.getAttributeKey().getAttributeName());
        Assert.assertEquals("host2", na.getAttributeValue());
        Assert.assertEquals(STRING, na.getAttributeType());
        Assert.assertEquals(1, count.get());
        // Send HBs to RM with the same node attributes
        nodeStatusObject.setResponseId((++responseId));
        heartbeatReq.setNodeStatus(nodeStatusObject);
        resourceTrackerService.nodeHeartbeat(heartbeatReq);
        nodeStatusObject.setResponseId((++responseId));
        heartbeatReq.setNodeStatus(nodeStatusObject);
        resourceTrackerService.nodeHeartbeat(heartbeatReq);
        // Make sure RM updated node attributes once
        Assert.assertEquals(1, count.get());
        // Send another HB to RM with updated node attributes
        nodeAttributes.clear();
        nodeAttributes.add(NodeAttribute.newInstance(PREFIX_DISTRIBUTED, "host", STRING, "host3"));
        nodeStatusObject.setResponseId((++responseId));
        heartbeatReq.setNodeStatus(nodeStatusObject);
        heartbeatReq.setNodeAttributes(nodeAttributes);
        resourceTrackerService.nodeHeartbeat(heartbeatReq);
        // Make sure RM gets the updated attribute
        attrs = spyAttributeManager.getAttributesForNode(nodeId.getHost());
        Assert.assertEquals(1, attrs.size());
        na = attrs.keySet().iterator().next();
        Assert.assertEquals("host", na.getAttributeKey().getAttributeName());
        Assert.assertEquals("host3", na.getAttributeValue());
        Assert.assertEquals(STRING, na.getAttributeType());
        // Make sure RM updated node attributes twice
        Assert.assertEquals(2, count.get());
        // Add centralized attributes
        Map<String, Set<NodeAttribute>> nodeAttributeMapping = ImmutableMap.of(nodeId.getHost(), ImmutableSet.of(NodeAttribute.newInstance(PREFIX_CENTRALIZED, "centAttr", STRING, "x")));
        spyAttributeManager.replaceNodeAttributes(PREFIX_CENTRALIZED, nodeAttributeMapping);
        // Make sure RM updated node attributes three times
        Assert.assertEquals(3, count.get());
        // Send another HB to RM with non-updated node attributes
        nodeAttributes.clear();
        nodeAttributes.add(NodeAttribute.newInstance(PREFIX_DISTRIBUTED, "host", STRING, "host3"));
        nodeStatusObject.setResponseId((++responseId));
        heartbeatReq.setNodeStatus(nodeStatusObject);
        heartbeatReq.setNodeAttributes(nodeAttributes);
        resourceTrackerService.nodeHeartbeat(heartbeatReq);
        // Make sure RM still updated node attributes three times
        Assert.assertEquals(3, count.get());
        // Send another HB to RM with updated node attributes
        nodeAttributes.clear();
        nodeAttributes.add(NodeAttribute.newInstance(PREFIX_DISTRIBUTED, "host", STRING, "host4"));
        nodeStatusObject.setResponseId((++responseId));
        heartbeatReq.setNodeStatus(nodeStatusObject);
        heartbeatReq.setNodeAttributes(nodeAttributes);
        resourceTrackerService.nodeHeartbeat(heartbeatReq);
        // Make sure RM gets the updated attribute
        attrs = spyAttributeManager.getAttributesForNode(nodeId.getHost());
        Assert.assertEquals(2, attrs.size());
        attrs.keySet().stream().forEach(( e) -> {
            Assert.assertEquals(NodeAttributeType.STRING, e.getAttributeType());
            if ((e.getAttributeKey().getAttributePrefix()) == NodeAttribute.PREFIX_DISTRIBUTED) {
                Assert.assertEquals("host", e.getAttributeKey().getAttributeName());
                Assert.assertEquals("host4", e.getAttributeValue());
            } else
                if ((e.getAttributeKey().getAttributePrefix()) == NodeAttribute.PREFIX_CENTRALIZED) {
                    Assert.assertEquals("centAttr", e.getAttributeKey().getAttributeName());
                    Assert.assertEquals("x", e.getAttributeValue());
                }

        });
        // Make sure RM updated node attributes four times
        Assert.assertEquals(4, count.get());
        if ((rm) != null) {
            stop();
        }
    }

    @Test
    public void testNodeHeartBeatWithInvalidLabels() throws Exception {
        writeToHostsFile("host2");
        Configuration conf = new Configuration();
        conf.set(RM_NODES_INCLUDE_FILE_PATH, hostFile.getAbsolutePath());
        conf.set(NODELABEL_CONFIGURATION_TYPE, DISTRIBUTED_NODELABEL_CONFIGURATION_TYPE);
        final RMNodeLabelsManager nodeLabelsMgr = new NullRMNodeLabelsManager();
        rm = new MockRM(conf) {
            @Override
            protected RMNodeLabelsManager createNodeLabelManager() {
                return nodeLabelsMgr;
            }
        };
        start();
        try {
            nodeLabelsMgr.addToCluserNodeLabelsWithDefaultExclusivity(toSet("A", "B", "C"));
        } catch (IOException e) {
            Assert.fail("Caught Exception while initializing");
            e.printStackTrace();
        }
        ResourceTrackerService resourceTrackerService = getResourceTrackerService();
        RegisterNodeManagerRequest registerReq = Records.newRecord(RegisterNodeManagerRequest.class);
        NodeId nodeId = NodeId.newInstance("host2", 1234);
        Resource capability = BuilderUtils.newResource(1024, 1);
        registerReq.setResource(capability);
        registerReq.setNodeId(nodeId);
        registerReq.setHttpPort(1234);
        registerReq.setNMVersion(YarnVersionInfo.getVersion());
        registerReq.setNodeLabels(toNodeLabelSet("A"));
        RegisterNodeManagerResponse registerResponse = resourceTrackerService.registerNodeManager(registerReq);
        NodeHeartbeatRequest heartbeatReq = Records.newRecord(NodeHeartbeatRequest.class);
        heartbeatReq.setNodeLabels(toNodeLabelSet("B", "#C"));// Invalid heart beat labels

        heartbeatReq.setNodeStatus(getNodeStatusObject(nodeId));
        heartbeatReq.setLastKnownNMTokenMasterKey(registerResponse.getNMTokenMasterKey());
        heartbeatReq.setLastKnownContainerTokenMasterKey(registerResponse.getContainerTokenMasterKey());
        NodeHeartbeatResponse nodeHeartbeatResponse = resourceTrackerService.nodeHeartbeat(heartbeatReq);
        // response should be NORMAL when RM heartbeat labels are rejected
        Assert.assertEquals(("Response should be NORMAL when RM heartbeat labels" + " are rejected"), NORMAL, nodeHeartbeatResponse.getNodeAction());
        Assert.assertFalse(nodeHeartbeatResponse.getAreNodeLabelsAcceptedByRM());
        Assert.assertNotNull(nodeHeartbeatResponse.getDiagnosticsMessage());
        stop();
    }

    @Test
    public void testNodeHeartbeatWithCentralLabelConfig() throws Exception {
        writeToHostsFile("host2");
        Configuration conf = new Configuration();
        conf.set(RM_NODES_INCLUDE_FILE_PATH, hostFile.getAbsolutePath());
        conf.set(NODELABEL_CONFIGURATION_TYPE, DEFAULT_NODELABEL_CONFIGURATION_TYPE);
        final RMNodeLabelsManager nodeLabelsMgr = new NullRMNodeLabelsManager();
        rm = new MockRM(conf) {
            @Override
            protected RMNodeLabelsManager createNodeLabelManager() {
                return nodeLabelsMgr;
            }
        };
        start();
        ResourceTrackerService resourceTrackerService = getResourceTrackerService();
        RegisterNodeManagerRequest req = Records.newRecord(RegisterNodeManagerRequest.class);
        NodeId nodeId = NodeId.newInstance("host2", 1234);
        Resource capability = BuilderUtils.newResource(1024, 1);
        req.setResource(capability);
        req.setNodeId(nodeId);
        req.setHttpPort(1234);
        req.setNMVersion(YarnVersionInfo.getVersion());
        req.setNodeLabels(toNodeLabelSet("A", "B", "C"));
        RegisterNodeManagerResponse registerResponse = resourceTrackerService.registerNodeManager(req);
        NodeHeartbeatRequest heartbeatReq = Records.newRecord(NodeHeartbeatRequest.class);
        heartbeatReq.setNodeLabels(toNodeLabelSet("B"));// Valid heart beat labels

        heartbeatReq.setNodeStatus(getNodeStatusObject(nodeId));
        heartbeatReq.setLastKnownNMTokenMasterKey(registerResponse.getNMTokenMasterKey());
        heartbeatReq.setLastKnownContainerTokenMasterKey(registerResponse.getContainerTokenMasterKey());
        NodeHeartbeatResponse nodeHeartbeatResponse = resourceTrackerService.nodeHeartbeat(heartbeatReq);
        // response should be ok but the RMacceptNodeLabelsUpdate should be false
        Assert.assertEquals(NORMAL, nodeHeartbeatResponse.getNodeAction());
        // no change in the labels,
        Assert.assertNull(nodeLabelsMgr.getNodeLabels().get(nodeId));
        // heartbeat labels rejected
        Assert.assertFalse("Invalid Node Labels should not accepted by RM", nodeHeartbeatResponse.getAreNodeLabelsAcceptedByRM());
        if ((rm) != null) {
            stop();
        }
    }

    @Test
    public void testNodeRegistrationVersionLessThanRM() throws Exception {
        writeToHostsFile("host2");
        Configuration conf = new Configuration();
        conf.set(RM_NODES_INCLUDE_FILE_PATH, hostFile.getAbsolutePath());
        conf.set(RM_NODEMANAGER_MINIMUM_VERSION, "EqualToRM");
        rm = new MockRM(conf);
        start();
        String nmVersion = "1.9.9";
        ResourceTrackerService resourceTrackerService = getResourceTrackerService();
        RegisterNodeManagerRequest req = Records.newRecord(RegisterNodeManagerRequest.class);
        NodeId nodeId = NodeId.newInstance("host2", 1234);
        Resource capability = BuilderUtils.newResource(1024, 1);
        req.setResource(capability);
        req.setNodeId(nodeId);
        req.setHttpPort(1234);
        req.setNMVersion(nmVersion);
        // trying to register a invalid node.
        RegisterNodeManagerResponse response = resourceTrackerService.registerNodeManager(req);
        Assert.assertEquals(SHUTDOWN, response.getNodeAction());
        Assert.assertTrue(((("Diagnostic message did not contain: 'Disallowed NodeManager " + "Version ") + nmVersion) + ", is less than the minimum version'"), response.getDiagnosticsMessage().contains((("Disallowed NodeManager Version " + nmVersion) + ", is less than the minimum version ")));
    }

    @Test
    public void testNodeRegistrationFailure() throws Exception {
        writeToHostsFile("host1");
        Configuration conf = new Configuration();
        conf.set(RM_NODES_INCLUDE_FILE_PATH, hostFile.getAbsolutePath());
        rm = new MockRM(conf);
        start();
        ResourceTrackerService resourceTrackerService = getResourceTrackerService();
        RegisterNodeManagerRequest req = Records.newRecord(RegisterNodeManagerRequest.class);
        NodeId nodeId = NodeId.newInstance("host2", 1234);
        req.setNodeId(nodeId);
        req.setHttpPort(1234);
        // trying to register a invalid node.
        RegisterNodeManagerResponse response = resourceTrackerService.registerNodeManager(req);
        Assert.assertEquals(SHUTDOWN, response.getNodeAction());
        Assert.assertEquals("Disallowed NodeManager from  host2, Sending SHUTDOWN signal to the NodeManager.", response.getDiagnosticsMessage());
    }

    @Test
    public void testSetRMIdentifierInRegistration() throws Exception {
        Configuration conf = new Configuration();
        rm = new MockRM(conf);
        start();
        MockNM nm = new MockNM("host1:1234", 5120, rm.getResourceTrackerService());
        RegisterNodeManagerResponse response = nm.registerNode();
        // Verify the RMIdentifier is correctly set in RegisterNodeManagerResponse
        Assert.assertEquals(ResourceManager.getClusterTimeStamp(), response.getRMIdentifier());
    }

    @Test
    public void testNodeRegistrationWithMinimumAllocations() throws Exception {
        Configuration conf = new Configuration();
        conf.set(RM_SCHEDULER_MINIMUM_ALLOCATION_MB, "2048");
        conf.set(RM_SCHEDULER_MINIMUM_ALLOCATION_VCORES, "4");
        rm = new MockRM(conf);
        start();
        ResourceTrackerService resourceTrackerService = getResourceTrackerService();
        RegisterNodeManagerRequest req = Records.newRecord(RegisterNodeManagerRequest.class);
        NodeId nodeId = BuilderUtils.newNodeId("host", 1234);
        req.setNodeId(nodeId);
        Resource capability = BuilderUtils.newResource(1024, 1);
        req.setResource(capability);
        RegisterNodeManagerResponse response1 = resourceTrackerService.registerNodeManager(req);
        Assert.assertEquals(SHUTDOWN, response1.getNodeAction());
        capability.setMemorySize(2048);
        capability.setVirtualCores(1);
        req.setResource(capability);
        RegisterNodeManagerResponse response2 = resourceTrackerService.registerNodeManager(req);
        Assert.assertEquals(SHUTDOWN, response2.getNodeAction());
        capability.setMemorySize(1024);
        capability.setVirtualCores(4);
        req.setResource(capability);
        RegisterNodeManagerResponse response3 = resourceTrackerService.registerNodeManager(req);
        Assert.assertEquals(SHUTDOWN, response3.getNodeAction());
        capability.setMemorySize(2048);
        capability.setVirtualCores(4);
        req.setResource(capability);
        RegisterNodeManagerResponse response4 = resourceTrackerService.registerNodeManager(req);
        Assert.assertEquals(NORMAL, response4.getNodeAction());
    }

    @Test
    public void testReboot() throws Exception {
        Configuration conf = new Configuration();
        rm = new MockRM(conf);
        start();
        MockNM nm1 = rm.registerNode("host1:1234", 5120);
        MockNM nm2 = rm.registerNode("host2:1234", 2048);
        int initialMetricCount = ClusterMetrics.getMetrics().getNumRebootedNMs();
        NodeHeartbeatResponse nodeHeartbeat = nm1.nodeHeartbeat(true);
        Assert.assertTrue(NORMAL.equals(nodeHeartbeat.getNodeAction()));
        nodeHeartbeat = nm2.nodeHeartbeat(new HashMap<ApplicationId, List<ContainerStatus>>(), true, (-100));
        Assert.assertTrue(RESYNC.equals(nodeHeartbeat.getNodeAction()));
        Assert.assertEquals("Too far behind rm response id:0 nm response id:-100", nodeHeartbeat.getDiagnosticsMessage());
        checkRebootedNMCount(rm, (++initialMetricCount));
    }

    @Test
    public void testNodeHeartbeatForAppCollectorsMap() throws Exception {
        Configuration conf = new Configuration();
        conf.setBoolean(TIMELINE_SERVICE_ENABLED, true);
        // set version to 2
        conf.setFloat(TIMELINE_SERVICE_VERSION, 2.0F);
        // enable aux-service based timeline collectors
        conf.set(NM_AUX_SERVICES, "timeline_collector");
        conf.set(((((YarnConfiguration.NM_AUX_SERVICES) + ".") + "timeline_collector") + ".class"), PerNodeTimelineCollectorsAuxService.class.getName());
        conf.setClass(TIMELINE_SERVICE_WRITER_CLASS, FileSystemTimelineWriterImpl.class, TimelineWriter.class);
        rm = new MockRM(conf);
        start();
        MockNM nm1 = rm.registerNode("host1:1234", 5120);
        MockNM nm2 = rm.registerNode("host2:1234", 2048);
        NodeHeartbeatResponse nodeHeartbeat1 = nm1.nodeHeartbeat(true);
        NodeHeartbeatResponse nodeHeartbeat2 = nm2.nodeHeartbeat(true);
        RMNodeImpl node1 = ((RMNodeImpl) (getRMContext().getRMNodes().get(nm1.getNodeId())));
        RMNodeImpl node2 = ((RMNodeImpl) (getRMContext().getRMNodes().get(nm2.getNodeId())));
        RMAppImpl app1 = ((RMAppImpl) (rm.submitApp(1024)));
        String collectorAddr1 = "1.2.3.4:5";
        app1.setCollectorData(AppCollectorData.newInstance(app1.getApplicationId(), collectorAddr1));
        String collectorAddr2 = "5.4.3.2:1";
        RMAppImpl app2 = ((RMAppImpl) (rm.submitApp(1024)));
        app2.setCollectorData(AppCollectorData.newInstance(app2.getApplicationId(), collectorAddr2));
        String collectorAddr3 = "5.4.3.2:2";
        app2.setCollectorData(AppCollectorData.newInstance(app2.getApplicationId(), collectorAddr3, 0, 1));
        String collectorAddr4 = "5.4.3.2:3";
        app2.setCollectorData(AppCollectorData.newInstance(app2.getApplicationId(), collectorAddr4, 1, 0));
        // Create a running container for app1 running on nm1
        ContainerId runningContainerId1 = BuilderUtils.newContainerId(BuilderUtils.newApplicationAttemptId(app1.getApplicationId(), 0), 0);
        ContainerStatus status1 = ContainerStatus.newInstance(runningContainerId1, ContainerState.RUNNING, "", 0);
        List<ContainerStatus> statusList = new ArrayList<ContainerStatus>();
        statusList.add(status1);
        NodeHealthStatus nodeHealth = NodeHealthStatus.newInstance(true, "", System.currentTimeMillis());
        NodeStatus nodeStatus = NodeStatus.newInstance(nm1.getNodeId(), 0, statusList, null, nodeHealth, null, null, null);
        node1.handle(new org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNodeStatusEvent(nm1.getNodeId(), nodeStatus));
        Assert.assertEquals(1, node1.getRunningApps().size());
        Assert.assertEquals(app1.getApplicationId(), node1.getRunningApps().get(0));
        // Create a running container for app2 running on nm2
        ContainerId runningContainerId2 = BuilderUtils.newContainerId(BuilderUtils.newApplicationAttemptId(app2.getApplicationId(), 0), 0);
        ContainerStatus status2 = ContainerStatus.newInstance(runningContainerId2, ContainerState.RUNNING, "", 0);
        statusList = new ArrayList<ContainerStatus>();
        statusList.add(status2);
        nodeStatus = NodeStatus.newInstance(nm1.getNodeId(), 0, statusList, null, nodeHealth, null, null, null);
        node2.handle(new org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNodeStatusEvent(nm2.getNodeId(), nodeStatus));
        Assert.assertEquals(1, node2.getRunningApps().size());
        Assert.assertEquals(app2.getApplicationId(), node2.getRunningApps().get(0));
        nodeHeartbeat1 = nm1.nodeHeartbeat(true);
        Map<ApplicationId, AppCollectorData> map1 = nodeHeartbeat1.getAppCollectors();
        Assert.assertEquals(1, map1.size());
        Assert.assertEquals(collectorAddr1, map1.get(app1.getApplicationId()).getCollectorAddr());
        nodeHeartbeat2 = nm2.nodeHeartbeat(true);
        Map<ApplicationId, AppCollectorData> map2 = nodeHeartbeat2.getAppCollectors();
        Assert.assertEquals(1, map2.size());
        Assert.assertEquals(collectorAddr4, map2.get(app2.getApplicationId()).getCollectorAddr());
    }

    @Test
    public void testUnhealthyNodeStatus() throws Exception {
        Configuration conf = new Configuration();
        conf.set(RM_NODES_EXCLUDE_FILE_PATH, hostFile.getAbsolutePath());
        rm = new MockRM(conf);
        start();
        MockNM nm1 = rm.registerNode("host1:1234", 5120);
        Assert.assertEquals(0, ClusterMetrics.getMetrics().getUnhealthyNMs());
        // node healthy
        nm1.nodeHeartbeat(true);
        // node unhealthy
        nm1.nodeHeartbeat(false);
        checkUnhealthyNMCount(rm, nm1, true, 1);
        // node healthy again
        nm1.nodeHeartbeat(true);
        checkUnhealthyNMCount(rm, nm1, false, 0);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testHandleContainerStatusInvalidCompletions() throws Exception {
        rm = new MockRM(new YarnConfiguration());
        start();
        EventHandler handler = Mockito.spy(getRMContext().getDispatcher().getEventHandler());
        // Case 1: Unmanaged AM
        RMApp app = rm.submitApp(1024, true);
        // Case 1.1: AppAttemptId is null
        NMContainerStatus report = NMContainerStatus.newInstance(ContainerId.newContainerId(ApplicationAttemptId.newInstance(app.getApplicationId(), 2), 1), 0, COMPLETE, Resource.newInstance(1024, 1), "Dummy Completed", 0, Priority.newInstance(10), 1234);
        rm.getResourceTrackerService().handleNMContainerStatus(report, null);
        Mockito.verify(handler, Mockito.never()).handle(((Event) (ArgumentMatchers.any())));
        // Case 1.2: Master container is null
        RMAppAttemptImpl currentAttempt = ((RMAppAttemptImpl) (app.getCurrentAppAttempt()));
        currentAttempt.setMasterContainer(null);
        report = NMContainerStatus.newInstance(ContainerId.newContainerId(currentAttempt.getAppAttemptId(), 0), 0, COMPLETE, Resource.newInstance(1024, 1), "Dummy Completed", 0, Priority.newInstance(10), 1234);
        rm.getResourceTrackerService().handleNMContainerStatus(report, null);
        Mockito.verify(handler, Mockito.never()).handle(((Event) (ArgumentMatchers.any())));
        // Case 2: Managed AM
        app = rm.submitApp(1024);
        // Case 2.1: AppAttemptId is null
        report = NMContainerStatus.newInstance(ContainerId.newContainerId(ApplicationAttemptId.newInstance(app.getApplicationId(), 2), 1), 0, COMPLETE, Resource.newInstance(1024, 1), "Dummy Completed", 0, Priority.newInstance(10), 1234);
        try {
            rm.getResourceTrackerService().handleNMContainerStatus(report, null);
        } catch (Exception e) {
            // expected - ignore
        }
        Mockito.verify(handler, Mockito.never()).handle(((Event) (ArgumentMatchers.any())));
        // Case 2.2: Master container is null
        currentAttempt = ((RMAppAttemptImpl) (app.getCurrentAppAttempt()));
        currentAttempt.setMasterContainer(null);
        report = NMContainerStatus.newInstance(ContainerId.newContainerId(currentAttempt.getAppAttemptId(), 0), 0, COMPLETE, Resource.newInstance(1024, 1), "Dummy Completed", 0, Priority.newInstance(10), 1234);
        try {
            rm.getResourceTrackerService().handleNMContainerStatus(report, null);
        } catch (Exception e) {
            // expected - ignore
        }
        Mockito.verify(handler, Mockito.never()).handle(((Event) (ArgumentMatchers.any())));
    }

    @Test
    public void testReconnectNode() throws Exception {
        rm = new MockRM() {
            @Override
            protected EventHandler<SchedulerEvent> createSchedulerEventDispatcher() {
                return new org.apache.hadoop.yarn.event.EventDispatcher<SchedulerEvent>(this.scheduler, this.scheduler.getClass().getName()) {
                    @Override
                    public void handle(SchedulerEvent event) {
                        scheduler.handle(event);
                    }
                };
            }
        };
        start();
        MockNM nm1 = rm.registerNode("host1:1234", 5120);
        MockNM nm2 = rm.registerNode("host2:5678", 5120);
        nm1.nodeHeartbeat(true);
        nm2.nodeHeartbeat(false);
        rm.drainEvents();
        checkUnhealthyNMCount(rm, nm2, true, 1);
        final int expectedNMs = ClusterMetrics.getMetrics().getNumActiveNMs();
        QueueMetrics metrics = getResourceScheduler().getRootQueueMetrics();
        // TODO Metrics incorrect in case of the FifoScheduler
        Assert.assertEquals(5120, metrics.getAvailableMB());
        // reconnect of healthy node
        nm1 = rm.registerNode("host1:1234", 5120);
        NodeHeartbeatResponse response = nm1.nodeHeartbeat(true);
        Assert.assertTrue(NORMAL.equals(response.getNodeAction()));
        rm.drainEvents();
        Assert.assertEquals(expectedNMs, ClusterMetrics.getMetrics().getNumActiveNMs());
        checkUnhealthyNMCount(rm, nm2, true, 1);
        // reconnect of unhealthy node
        nm2 = rm.registerNode("host2:5678", 5120);
        response = nm2.nodeHeartbeat(false);
        Assert.assertTrue(NORMAL.equals(response.getNodeAction()));
        rm.drainEvents();
        Assert.assertEquals(expectedNMs, ClusterMetrics.getMetrics().getNumActiveNMs());
        checkUnhealthyNMCount(rm, nm2, true, 1);
        // unhealthy node changed back to healthy
        nm2 = rm.registerNode("host2:5678", 5120);
        response = nm2.nodeHeartbeat(true);
        response = nm2.nodeHeartbeat(true);
        rm.drainEvents();
        Assert.assertEquals((5120 + 5120), metrics.getAvailableMB());
        // reconnect of node with changed capability
        nm1 = rm.registerNode("host2:5678", 10240);
        response = nm1.nodeHeartbeat(true);
        rm.drainEvents();
        Assert.assertTrue(NORMAL.equals(response.getNodeAction()));
        Assert.assertEquals((5120 + 10240), metrics.getAvailableMB());
        // reconnect of node with changed capability and running applications
        List<ApplicationId> runningApps = new ArrayList<ApplicationId>();
        runningApps.add(ApplicationId.newInstance(1, 0));
        nm1 = rm.registerNode("host2:5678", 15360, 2, runningApps);
        response = nm1.nodeHeartbeat(true);
        rm.drainEvents();
        Assert.assertTrue(NORMAL.equals(response.getNodeAction()));
        Assert.assertEquals((5120 + 15360), metrics.getAvailableMB());
        // reconnect healthy node changing http port
        nm1 = new MockNM("host1:1234", 5120, rm.getResourceTrackerService());
        nm1.setHttpPort(3);
        nm1.registerNode();
        response = nm1.nodeHeartbeat(true);
        response = nm1.nodeHeartbeat(true);
        rm.drainEvents();
        RMNode rmNode = getRMContext().getRMNodes().get(nm1.getNodeId());
        Assert.assertEquals(3, rmNode.getHttpPort());
        Assert.assertEquals(5120, rmNode.getTotalCapability().getMemorySize());
        Assert.assertEquals((5120 + 15360), metrics.getAvailableMB());
    }

    @Test
    public void testNMUnregistration() throws Exception {
        Configuration conf = new Configuration();
        rm = new MockRM(conf);
        start();
        ResourceTrackerService resourceTrackerService = getResourceTrackerService();
        MockNM nm1 = rm.registerNode("host1:1234", 5120);
        int shutdownNMsCount = ClusterMetrics.getMetrics().getNumShutdownNMs();
        NodeHeartbeatResponse nodeHeartbeat = nm1.nodeHeartbeat(true);
        Assert.assertTrue(NORMAL.equals(nodeHeartbeat.getNodeAction()));
        UnRegisterNodeManagerRequest request = Records.newRecord(UnRegisterNodeManagerRequest.class);
        request.setNodeId(nm1.getNodeId());
        resourceTrackerService.unRegisterNodeManager(request);
        checkShutdownNMCount(rm, (++shutdownNMsCount));
        // The RM should remove the node after unregistration, hence send a reboot
        // command.
        nodeHeartbeat = nm1.nodeHeartbeat(true);
        Assert.assertTrue(RESYNC.equals(nodeHeartbeat.getNodeAction()));
    }

    @Test
    public void testUnhealthyNMUnregistration() throws Exception {
        Configuration conf = new Configuration();
        rm = new MockRM(conf);
        start();
        ResourceTrackerService resourceTrackerService = getResourceTrackerService();
        MockNM nm1 = rm.registerNode("host1:1234", 5120);
        Assert.assertEquals(0, ClusterMetrics.getMetrics().getUnhealthyNMs());
        // node healthy
        nm1.nodeHeartbeat(true);
        int shutdownNMsCount = ClusterMetrics.getMetrics().getNumShutdownNMs();
        // node unhealthy
        nm1.nodeHeartbeat(false);
        checkUnhealthyNMCount(rm, nm1, true, 1);
        UnRegisterNodeManagerRequest request = Records.newRecord(UnRegisterNodeManagerRequest.class);
        request.setNodeId(nm1.getNodeId());
        resourceTrackerService.unRegisterNodeManager(request);
        checkShutdownNMCount(rm, (++shutdownNMsCount));
    }

    @Test
    public void testInvalidNMUnregistration() throws Exception {
        Configuration conf = new Configuration();
        rm = new MockRM(conf);
        start();
        ResourceTrackerService resourceTrackerService = getResourceTrackerService();
        int decommisionedNMsCount = ClusterMetrics.getMetrics().getNumDecommisionedNMs();
        // Node not found for unregister
        UnRegisterNodeManagerRequest request = Records.newRecord(UnRegisterNodeManagerRequest.class);
        request.setNodeId(BuilderUtils.newNodeId("host", 1234));
        resourceTrackerService.unRegisterNodeManager(request);
        checkShutdownNMCount(rm, 0);
        checkDecommissionedNMCount(rm, 0);
        // 1. Register the Node Manager
        // 2. Exclude the same Node Manager host
        // 3. Give NM heartbeat to RM
        // 4. Unregister the Node Manager
        MockNM nm1 = new MockNM("host1:1234", 5120, resourceTrackerService);
        RegisterNodeManagerResponse response = nm1.registerNode();
        Assert.assertEquals(NORMAL, response.getNodeAction());
        int shutdownNMsCount = ClusterMetrics.getMetrics().getNumShutdownNMs();
        writeToHostsFile("host2");
        conf.set(RM_NODES_INCLUDE_FILE_PATH, hostFile.getAbsolutePath());
        rm.getNodesListManager().refreshNodes(conf);
        NodeHeartbeatResponse heartbeatResponse = nm1.nodeHeartbeat(true);
        Assert.assertEquals(SHUTDOWN, heartbeatResponse.getNodeAction());
        checkDecommissionedNMCount(rm, decommisionedNMsCount);
        request.setNodeId(nm1.getNodeId());
        resourceTrackerService.unRegisterNodeManager(request);
        checkShutdownNMCount(rm, (++shutdownNMsCount));
        checkDecommissionedNMCount(rm, decommisionedNMsCount);
        // 1. Register the Node Manager
        // 2. Exclude the same Node Manager host
        // 3. Unregister the Node Manager
        MockNM nm2 = new MockNM("host2:1234", 5120, resourceTrackerService);
        RegisterNodeManagerResponse response2 = nm2.registerNode();
        Assert.assertEquals(NORMAL, response2.getNodeAction());
        writeToHostsFile("host1");
        conf.set(RM_NODES_INCLUDE_FILE_PATH, hostFile.getAbsolutePath());
        rm.getNodesListManager().refreshNodes(conf);
        request.setNodeId(nm2.getNodeId());
        resourceTrackerService.unRegisterNodeManager(request);
        checkShutdownNMCount(rm, (++shutdownNMsCount));
        checkDecommissionedNMCount(rm, decommisionedNMsCount);
        stop();
    }

    @Test(timeout = 30000)
    public void testInitDecommMetric() throws Exception {
        testInitDecommMetricHelper(true);
        testInitDecommMetricHelper(false);
    }

    @Test(timeout = 30000)
    public void testInitDecommMetricNoRegistration() throws Exception {
        Configuration conf = new Configuration();
        rm = new MockRM(conf);
        start();
        MockNM nm1 = rm.registerNode("host1:1234", 5120);
        MockNM nm2 = rm.registerNode("host2:5678", 10240);
        nm1.nodeHeartbeat(true);
        nm2.nodeHeartbeat(true);
        // host3 will not register or heartbeat
        writeToHostsFile(excludeHostFile, "host3", "host2");
        conf.set(RM_NODES_EXCLUDE_FILE_PATH, excludeHostFile.getAbsolutePath());
        writeToHostsFile(hostFile, "host1", "host2");
        conf.set(RM_NODES_INCLUDE_FILE_PATH, hostFile.getAbsolutePath());
        rm.getNodesListManager().refreshNodes(conf);
        rm.drainEvents();
        Assert.assertEquals("The decommissioned nodes metric should be 1 ", 1, ClusterMetrics.getMetrics().getNumDecommisionedNMs());
        stop();
        MockRM rm1 = new MockRM(conf);
        start();
        rm1.getNodesListManager().refreshNodes(conf);
        rm1.drainEvents();
        Assert.assertEquals("The decommissioned nodes metric should be 2 ", 2, ClusterMetrics.getMetrics().getNumDecommisionedNMs());
        stop();
    }

    @Test
    public void testIncorrectRecommission() throws Exception {
        // Check decommissioned node not get recommissioned with graceful refresh
        Configuration conf = new Configuration();
        rm = new MockRM(conf);
        start();
        MockNM nm1 = rm.registerNode("host1:1234", 5120);
        MockNM nm2 = rm.registerNode("host2:5678", 10240);
        nm1.nodeHeartbeat(true);
        nm2.nodeHeartbeat(true);
        writeToHostsFile(excludeHostFile, "host3", "host2");
        conf.set(RM_NODES_EXCLUDE_FILE_PATH, excludeHostFile.getAbsolutePath());
        writeToHostsFile(hostFile, "host1", "host2");
        writeToHostsFile(excludeHostFile, "host1");
        rm.getNodesListManager().refreshNodesGracefully(conf, null);
        rm.drainEvents();
        nm1.nodeHeartbeat(true);
        rm.drainEvents();
        Assert.assertTrue((("Node " + (nm1.getNodeId().getHost())) + " should be Decommissioned"), ((getRMContext().getInactiveRMNodes().get(nm1.getNodeId()).getState()) == (NodeState.DECOMMISSIONED)));
        writeToHostsFile(excludeHostFile, "");
        rm.getNodesListManager().refreshNodesGracefully(conf, null);
        rm.drainEvents();
        Assert.assertTrue((("Node " + (nm1.getNodeId().getHost())) + " should be Decommissioned"), ((getRMContext().getInactiveRMNodes().get(nm1.getNodeId()).getState()) == (NodeState.DECOMMISSIONED)));
        stop();
    }

    /**
     * Remove a node from all lists and check if its forgotten.
     */
    @Test
    public void testNodeRemovalNormally() throws Exception {
        testNodeRemovalUtil(false);
        testNodeRemovalUtilLost(false);
        testNodeRemovalUtilRebooted(false);
        testNodeRemovalUtilUnhealthy(false);
    }

    @Test
    public void testNodeRemovalGracefully() throws Exception {
        testNodeRemovalUtil(true);
        testNodeRemovalUtilLost(true);
        testNodeRemovalUtilRebooted(true);
        testNodeRemovalUtilUnhealthy(true);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testHandleOpportunisticContainerStatus() throws Exception {
        final DrainDispatcher dispatcher = new DrainDispatcher();
        YarnConfiguration conf = new YarnConfiguration();
        conf.setBoolean(RECOVERY_ENABLED, true);
        conf.setBoolean(RM_WORK_PRESERVING_RECOVERY_ENABLED, true);
        rm = new MockRM(conf) {
            @Override
            protected Dispatcher createDispatcher() {
                return dispatcher;
            }
        };
        start();
        RMApp app = rm.submitApp(1024, true);
        ApplicationAttemptId appAttemptId = app.getCurrentAppAttempt().getAppAttemptId();
        ResourceTrackerService resourceTrackerService = getResourceTrackerService();
        SchedulerApplicationAttempt applicationAttempt = null;
        while (applicationAttempt == null) {
            applicationAttempt = ((AbstractYarnScheduler) (getRMContext().getScheduler())).getApplicationAttempt(appAttemptId);
            Thread.sleep(100);
        } 
        Resource currentConsumption = applicationAttempt.getCurrentConsumption();
        Assert.assertEquals(Resource.newInstance(0, 0), currentConsumption);
        Resource allocResources = applicationAttempt.getQueue().getMetrics().getAllocatedResources();
        Assert.assertEquals(Resource.newInstance(0, 0), allocResources);
        RegisterNodeManagerRequest req = Records.newRecord(RegisterNodeManagerRequest.class);
        NodeId nodeId = NodeId.newInstance("host2", 1234);
        Resource capability = BuilderUtils.newResource(1024, 1);
        req.setResource(capability);
        req.setNodeId(nodeId);
        req.setHttpPort(1234);
        req.setNMVersion(YarnVersionInfo.getVersion());
        ContainerId c1 = ContainerId.newContainerId(appAttemptId, 1);
        ContainerId c2 = ContainerId.newContainerId(appAttemptId, 2);
        ContainerId c3 = ContainerId.newContainerId(appAttemptId, 3);
        NMContainerStatus queuedOpp = NMContainerStatus.newInstance(c1, 1, ContainerState.RUNNING, Resource.newInstance(1024, 1), "Dummy Queued OC", INVALID, Priority.newInstance(5), 1234, "", OPPORTUNISTIC, (-1));
        NMContainerStatus runningOpp = NMContainerStatus.newInstance(c2, 1, ContainerState.RUNNING, Resource.newInstance(2048, 1), "Dummy Running OC", INVALID, Priority.newInstance(6), 1234, "", OPPORTUNISTIC, (-1));
        NMContainerStatus runningGuar = NMContainerStatus.newInstance(c3, 1, ContainerState.RUNNING, Resource.newInstance(2048, 1), "Dummy Running GC", INVALID, Priority.newInstance(6), 1234, "", GUARANTEED, (-1));
        req.setContainerStatuses(Arrays.asList(queuedOpp, runningOpp, runningGuar));
        // trying to register a invalid node.
        RegisterNodeManagerResponse response = resourceTrackerService.registerNodeManager(req);
        dispatcher.await();
        Thread.sleep(2000);
        dispatcher.await();
        Assert.assertEquals(NORMAL, response.getNodeAction());
        Collection<RMContainer> liveContainers = applicationAttempt.getLiveContainers();
        Assert.assertEquals(3, liveContainers.size());
        Iterator<RMContainer> iter = liveContainers.iterator();
        while (iter.hasNext()) {
            RMContainer rc = iter.next();
            Assert.assertEquals((rc.getContainerId().equals(c3) ? ExecutionType.GUARANTEED : ExecutionType.OPPORTUNISTIC), rc.getExecutionType());
        } 
        // Should only include GUARANTEED resources
        currentConsumption = applicationAttempt.getCurrentConsumption();
        Assert.assertEquals(Resource.newInstance(2048, 1), currentConsumption);
        allocResources = applicationAttempt.getQueue().getMetrics().getAllocatedResources();
        Assert.assertEquals(Resource.newInstance(2048, 1), allocResources);
        SchedulerNode schedulerNode = getRMContext().getScheduler().getSchedulerNode(nodeId);
        Assert.assertNotNull(schedulerNode);
        Resource nodeResources = schedulerNode.getAllocatedResource();
        Assert.assertEquals(Resource.newInstance(2048, 1), nodeResources);
    }

    @Test(timeout = 60000)
    public void testNodeHeartBeatResponseForUnknownContainerCleanUp() throws Exception {
        Configuration conf = new Configuration();
        rm = new MockRM(conf);
        rm.init(conf);
        start();
        MockNM nm1 = rm.registerNode("host1:1234", 5120);
        rm.drainEvents();
        // send 1st heartbeat
        nm1.nodeHeartbeat(true);
        // Create 2 unknown containers tracked by NM
        ApplicationId applicationId = BuilderUtils.newApplicationId(1, 1);
        ApplicationAttemptId applicationAttemptId = BuilderUtils.newApplicationAttemptId(applicationId, 1);
        ContainerId cid1 = BuilderUtils.newContainerId(applicationAttemptId, 2);
        ContainerId cid2 = BuilderUtils.newContainerId(applicationAttemptId, 3);
        ArrayList<ContainerStatus> containerStats = new ArrayList<ContainerStatus>();
        containerStats.add(ContainerStatus.newInstance(cid1, COMPLETE, "", (-1)));
        containerStats.add(ContainerStatus.newInstance(cid2, COMPLETE, "", (-1)));
        Map<ApplicationId, List<ContainerStatus>> conts = new HashMap<ApplicationId, List<ContainerStatus>>();
        conts.put(applicationAttemptId.getApplicationId(), containerStats);
        // add RMApp into context.
        RMApp app1 = Mockito.mock(RMApp.class);
        Mockito.when(app1.getApplicationId()).thenReturn(applicationId);
        getRMContext().getRMApps().put(applicationId, app1);
        // Send unknown container status in heartbeat
        nm1.nodeHeartbeat(conts, true);
        rm.drainEvents();
        int containersToBeRemovedFromNM = 0;
        while (true) {
            NodeHeartbeatResponse nodeHeartbeat = nm1.nodeHeartbeat(true);
            rm.drainEvents();
            containersToBeRemovedFromNM += nodeHeartbeat.getContainersToBeRemovedFromNM().size();
            // asserting for 2 since two unknown containers status has been sent
            if (containersToBeRemovedFromNM == 2) {
                break;
            }
        } 
    }

    @Test
    public void testResponseIdOverflow() throws Exception {
        Configuration conf = new Configuration();
        rm = new MockRM(conf);
        start();
        MockNM nm1 = rm.registerNode("host1:1234", 5120);
        NodeHeartbeatResponse nodeHeartbeat = nm1.nodeHeartbeat(true);
        Assert.assertEquals(NORMAL, nodeHeartbeat.getNodeAction());
        // prepare the responseId that's about to overflow
        RMNode node = getRMContext().getRMNodes().get(nm1.getNodeId());
        node.getLastNodeHeartBeatResponse().setResponseId(Integer.MAX_VALUE);
        nm1.setResponseId(Integer.MAX_VALUE);
        // heartbeat twice and check responseId
        nodeHeartbeat = nm1.nodeHeartbeat(true);
        Assert.assertEquals(NORMAL, nodeHeartbeat.getNodeAction());
        Assert.assertEquals(0, nodeHeartbeat.getResponseId());
        nodeHeartbeat = nm1.nodeHeartbeat(true);
        Assert.assertEquals(NORMAL, nodeHeartbeat.getNodeAction());
        Assert.assertEquals(1, nodeHeartbeat.getResponseId());
    }

    @Test
    public void testNMIpHostNameResolution() throws Exception {
        Configuration conf = new Configuration();
        conf.set(RM_RESOURCE_TRACKER_ADDRESS, ("localhost:" + (ServerSocketUtil.getPort(10000, 10))));
        conf.setBoolean(RM_NM_REGISTRATION_IP_HOSTNAME_CHECK_KEY, true);
        MockRM mockRM = new MockRM(conf) {
            @Override
            protected ResourceTrackerService createResourceTrackerService() {
                return new ResourceTrackerService(getRMContext(), nodesListManager, this.nmLivelinessMonitor, rmContext.getContainerTokenSecretManager(), rmContext.getNMTokenSecretManager()) {};
            }
        };
        start();
        ResourceTracker rmTracker = ServerRMProxy.createRMProxy(getConfig(), ResourceTracker.class);
        RegisterNodeManagerResponse response = rmTracker.registerNodeManager(RegisterNodeManagerRequest.newInstance(NodeId.newInstance(("host1" + (System.currentTimeMillis())), 1234), 1236, Resource.newInstance(10000, 10), "2", new ArrayList(), new ArrayList()));
        Assert.assertEquals("Shutdown signal should be received", SHUTDOWN, response.getNodeAction());
        Assert.assertTrue("Diagnostic Message", response.getDiagnosticsMessage().contains("hostname cannot be resolved "));
        // Test success
        rmTracker = ServerRMProxy.createRMProxy(getConfig(), ResourceTracker.class);
        response = rmTracker.registerNodeManager(RegisterNodeManagerRequest.newInstance(NodeId.newInstance("localhost", 1234), 1236, Resource.newInstance(10000, 10), "2", new ArrayList(), new ArrayList()));
        Assert.assertEquals("Successfull registration", NORMAL, response.getNodeAction());
        stop();
    }

    /**
     * A no-op implementation of NodeAttributeStore for testing
     */
    public static class NullNodeAttributeStore implements NodeAttributeStore {
        @Override
        public void replaceNodeAttributes(List<NodeToAttributes> nodeToAttribute) {
        }

        @Override
        public void addNodeAttributes(List<NodeToAttributes> nodeToAttribute) {
        }

        @Override
        public void removeNodeAttributes(List<NodeToAttributes> nodeToAttribute) {
        }

        @Override
        public void init(Configuration configuration, NodeAttributesManager mgr) {
        }

        @Override
        public void recover() {
        }

        @Override
        public void close() {
        }
    }

    @Test(timeout = 5000)
    public void testSystemCredentialsAfterTokenSequenceNoChange() throws Exception {
        Configuration conf = new Configuration();
        RMContext rmContext = Mockito.mock(RMContextImpl.class);
        Dispatcher dispatcher = new InlineDispatcher();
        Mockito.when(rmContext.getDispatcher()).thenReturn(dispatcher);
        NodeId nodeId = NodeId.newInstance("localhost", 1234);
        ConcurrentMap<NodeId, RMNode> rmNodes = new ConcurrentHashMap<NodeId, RMNode>();
        RMNode rmNode = MockNodes.newNodeInfo(1, Resource.newInstance(1024, 1), 1, "localhost", 1234, rmContext);
        rmNodes.put(nodeId, rmNode);
        Mockito.when(rmContext.getRMNodes()).thenReturn(rmNodes);
        ConcurrentMap<NodeId, RMNode> inactiveNodes = new ConcurrentHashMap<NodeId, RMNode>();
        Mockito.when(rmContext.getInactiveRMNodes()).thenReturn(inactiveNodes);
        Mockito.when(rmContext.getConfigurationProvider()).thenReturn(new LocalConfigurationProvider());
        dispatcher.register(SchedulerEventType.class, new InlineDispatcher.EmptyEventHandler());
        dispatcher.register(RMNodeEventType.class, new org.apache.hadoop.yarn.server.resourcemanager.ResourceManager.NodeEventDispatcher(rmContext));
        NMLivelinessMonitor nmLivelinessMonitor = new NMLivelinessMonitor(dispatcher);
        nmLivelinessMonitor.init(conf);
        nmLivelinessMonitor.start();
        NodesListManager nodesListManager = new NodesListManager(rmContext);
        nodesListManager.init(conf);
        RMContainerTokenSecretManager containerTokenSecretManager = new RMContainerTokenSecretManager(conf);
        containerTokenSecretManager.start();
        NMTokenSecretManagerInRM nmTokenSecretManager = new NMTokenSecretManagerInRM(conf);
        nmTokenSecretManager.start();
        ResourceTrackerService resourceTrackerService = new ResourceTrackerService(rmContext, nodesListManager, nmLivelinessMonitor, containerTokenSecretManager, nmTokenSecretManager);
        resourceTrackerService.init(conf);
        resourceTrackerService.start();
        RecordFactory recordFactory = RecordFactoryProvider.getRecordFactory(null);
        RegisterNodeManagerRequest request = recordFactory.newRecordInstance(RegisterNodeManagerRequest.class);
        request.setNodeId(nodeId);
        request.setHttpPort(1234);
        request.setResource(BuilderUtils.newResource(1024, 1));
        resourceTrackerService.registerNodeManager(request);
        NodeStatus nodeStatus = recordFactory.newRecordInstance(NodeStatus.class);
        nodeStatus.setNodeId(nodeId);
        nodeStatus.setResponseId(0);
        nodeStatus.setNodeHealthStatus(recordFactory.newRecordInstance(NodeHealthStatus.class));
        nodeStatus.getNodeHealthStatus().setIsNodeHealthy(true);
        NodeHeartbeatRequest request1 = recordFactory.newRecordInstance(NodeHeartbeatRequest.class);
        request1.setNodeStatus(nodeStatus);
        // Set NM's token sequence no as 1
        request1.setTokenSequenceNo(1);
        // Set RM's token sequence no as 1
        Mockito.when(rmContext.getTokenSequenceNo()).thenReturn(((long) (1)));
        // Populate SystemCredentialsForApps
        final ApplicationId appId = ApplicationId.newInstance(1234, 1);
        Credentials app1Cred = new Credentials();
        Token<DelegationTokenIdentifier> token = new Token<DelegationTokenIdentifier>();
        token.setKind(new Text("kind1"));
        app1Cred.addToken(new Text("token1"), token);
        Token<DelegationTokenIdentifier> token2 = new Token<DelegationTokenIdentifier>();
        token2.setKind(new Text("kind2"));
        app1Cred.addToken(new Text("token2"), token2);
        DataOutputBuffer dob = new DataOutputBuffer();
        app1Cred.writeTokenStorageToStream(dob);
        ByteBuffer byteBuffer = ByteBuffer.wrap(dob.getData(), 0, dob.getLength());
        SystemCredentialsForAppsProto systemCredentialsForAppsProto = YarnServerBuilderUtils.newSystemCredentialsForAppsProto(appId, byteBuffer);
        ConcurrentHashMap<ApplicationId, SystemCredentialsForAppsProto> systemCredentialsForApps = new ConcurrentHashMap<ApplicationId, SystemCredentialsForAppsProto>(1);
        systemCredentialsForApps.put(appId, systemCredentialsForAppsProto);
        Mockito.when(rmContext.getSystemCredentialsForApps()).thenReturn(systemCredentialsForApps);
        // first ping
        NodeHeartbeatResponse response = resourceTrackerService.nodeHeartbeat(request1);
        // Though SystemCredentialsForApps size is 1, it is not being sent as part
        // of response as there is no difference between NM's and RM's token
        // sequence no
        Assert.assertEquals(1, rmContext.getTokenSequenceNo());
        Assert.assertEquals(1, rmContext.getSystemCredentialsForApps().size());
        Assert.assertEquals(1, response.getTokenSequenceNo());
        Assert.assertEquals(0, response.getSystemCredentialsForApps().size());
        // Set RM's token sequence no as 2
        Mockito.when(rmContext.getTokenSequenceNo()).thenReturn(((long) (2)));
        // Ensure new heartbeat has been sent to avoid duplicate issues
        nodeStatus.setResponseId(1);
        request1.setNodeStatus(nodeStatus);
        // second ping
        NodeHeartbeatResponse response1 = resourceTrackerService.nodeHeartbeat(request1);
        // Since NM's and RM's token sequence no is different, response should
        // contain SystemCredentialsForApps
        Assert.assertEquals(2, response1.getTokenSequenceNo());
        Assert.assertEquals(1, response1.getSystemCredentialsForApps().size());
        resourceTrackerService.close();
    }
}

