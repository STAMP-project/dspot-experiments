/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership.  The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.hadoop.ozone.container.common;


import EndpointStateMachine.EndPointStates;
import EndpointStateMachine.EndPointStates.GETVERSION;
import EndpointStateMachine.EndPointStates.HEARTBEAT;
import EndpointStateMachine.EndPointStates.REGISTER;
import EndpointStateMachine.EndPointStates.SHUTDOWN;
import GenericTestUtils.LogCapturer;
import RPC.Server;
import Status.PENDING;
import Type.deleteBlocksCommand;
import Type.replicateContainerCommand;
import VersionEndpointTask.LOG;
import VersionInfo.DESCRIPTION_KEY;
import java.io.File;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdds.conf.OzoneConfiguration;
import org.apache.hadoop.hdds.protocol.DatanodeDetails;
import org.apache.hadoop.hdds.protocol.proto.StorageContainerDatanodeProtocolProtos.SCMHeartbeatRequestProto;
import org.apache.hadoop.hdds.protocol.proto.StorageContainerDatanodeProtocolProtos.SCMHeartbeatResponseProto;
import org.apache.hadoop.hdds.protocol.proto.StorageContainerDatanodeProtocolProtos.SCMRegisteredResponseProto;
import org.apache.hadoop.hdds.protocol.proto.StorageContainerDatanodeProtocolProtos.SCMVersionResponseProto;
import org.apache.hadoop.hdds.scm.TestUtils;
import org.apache.hadoop.hdds.scm.VersionInfo;
import org.apache.hadoop.ozone.container.common.statemachine.EndpointStateMachine;
import org.apache.hadoop.ozone.container.common.statemachine.StateContext;
import org.apache.hadoop.ozone.container.common.states.endpoint.VersionEndpointTask;
import org.apache.hadoop.ozone.container.common.volume.HddsVolume;
import org.apache.hadoop.ozone.container.ozoneimpl.OzoneContainer;
import org.apache.hadoop.ozone.protocol.commands.CommandStatus;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.util.Time;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests the endpoints.
 */
public class TestEndPoint {
    private static InetSocketAddress serverAddress;

    private static Server scmServer;

    private static ScmTestMock scmServerImpl;

    private static File testDir;

    private static Configuration config;

    /**
     * This test asserts that we are able to make a version call to SCM server
     * and gets back the expected values.
     */
    @Test
    public void testGetVersion() throws Exception {
        try (EndpointStateMachine rpcEndPoint = ContainerTestUtils.createEndpoint(SCMTestUtils.getConf(), TestEndPoint.serverAddress, 1000)) {
            SCMVersionResponseProto responseProto = rpcEndPoint.getEndPoint().getVersion(null);
            Assert.assertNotNull(responseProto);
            Assert.assertEquals(DESCRIPTION_KEY, responseProto.getKeys(0).getKey());
            Assert.assertEquals(VersionInfo.getLatestVersion().getDescription(), responseProto.getKeys(0).getValue());
        }
    }

    /**
     * We make getVersion RPC call, but via the VersionEndpointTask which is
     * how the state machine would make the call.
     */
    @Test
    public void testGetVersionTask() throws Exception {
        OzoneConfiguration conf = SCMTestUtils.getConf();
        try (EndpointStateMachine rpcEndPoint = ContainerTestUtils.createEndpoint(conf, TestEndPoint.serverAddress, 1000)) {
            DatanodeDetails datanodeDetails = TestUtils.randomDatanodeDetails();
            OzoneContainer ozoneContainer = new OzoneContainer(datanodeDetails, conf, getContext(datanodeDetails));
            rpcEndPoint.setState(GETVERSION);
            VersionEndpointTask versionTask = new VersionEndpointTask(rpcEndPoint, conf, ozoneContainer);
            EndpointStateMachine.EndPointStates newState = versionTask.call();
            // if version call worked the endpoint should automatically move to the
            // next state.
            Assert.assertEquals(REGISTER, newState);
            // Now rpcEndpoint should remember the version it got from SCM
            Assert.assertNotNull(rpcEndPoint.getVersion());
        }
    }

    @Test
    public void testCheckVersionResponse() throws Exception {
        OzoneConfiguration conf = SCMTestUtils.getConf();
        try (EndpointStateMachine rpcEndPoint = ContainerTestUtils.createEndpoint(conf, TestEndPoint.serverAddress, 1000)) {
            GenericTestUtils.LogCapturer logCapturer = LogCapturer.captureLogs(LOG);
            DatanodeDetails datanodeDetails = TestUtils.randomDatanodeDetails();
            OzoneContainer ozoneContainer = new OzoneContainer(datanodeDetails, conf, getContext(datanodeDetails));
            rpcEndPoint.setState(GETVERSION);
            VersionEndpointTask versionTask = new VersionEndpointTask(rpcEndPoint, conf, ozoneContainer);
            EndpointStateMachine.EndPointStates newState = versionTask.call();
            // if version call worked the endpoint should automatically move to the
            // next state.
            Assert.assertEquals(REGISTER, newState);
            // Now rpcEndpoint should remember the version it got from SCM
            Assert.assertNotNull(rpcEndPoint.getVersion());
            // Now change server scmId, so datanode scmId  will be
            // different from SCM server response scmId
            String newScmId = UUID.randomUUID().toString();
            TestEndPoint.scmServerImpl.setScmId(newScmId);
            rpcEndPoint.setState(GETVERSION);
            newState = versionTask.call();
            Assert.assertEquals(SHUTDOWN, newState);
            List<HddsVolume> volumesList = ozoneContainer.getVolumeSet().getFailedVolumesList();
            Assert.assertTrue(((volumesList.size()) == 1));
            File expectedScmDir = new File(volumesList.get(0).getHddsRootDir(), TestEndPoint.scmServerImpl.getScmId());
            Assert.assertTrue(logCapturer.getOutput().contains((((("expected scm " + "directory ") + (expectedScmDir.getAbsolutePath())) + " does not ") + "exist")));
            Assert.assertTrue(((ozoneContainer.getVolumeSet().getVolumesList().size()) == 0));
            Assert.assertTrue(((ozoneContainer.getVolumeSet().getFailedVolumesList().size()) == 1));
        }
    }

    /**
     * This test makes a call to end point where there is no SCM server. We
     * expect that versionTask should be able to handle it.
     */
    @Test
    public void testGetVersionToInvalidEndpoint() throws Exception {
        OzoneConfiguration conf = SCMTestUtils.getConf();
        InetSocketAddress nonExistentServerAddress = SCMTestUtils.getReuseableAddress();
        try (EndpointStateMachine rpcEndPoint = ContainerTestUtils.createEndpoint(conf, nonExistentServerAddress, 1000)) {
            rpcEndPoint.setState(GETVERSION);
            DatanodeDetails datanodeDetails = TestUtils.randomDatanodeDetails();
            OzoneContainer ozoneContainer = new OzoneContainer(datanodeDetails, conf, getContext(datanodeDetails));
            VersionEndpointTask versionTask = new VersionEndpointTask(rpcEndPoint, conf, ozoneContainer);
            EndpointStateMachine.EndPointStates newState = versionTask.call();
            // This version call did NOT work, so endpoint should remain in the same
            // state.
            Assert.assertEquals(GETVERSION, newState);
        }
    }

    /**
     * This test makes a getVersionRPC call, but the DummyStorageServer is
     * going to respond little slowly. We will assert that we are still in the
     * GETVERSION state after the timeout.
     */
    @Test
    public void testGetVersionAssertRpcTimeOut() throws Exception {
        final long rpcTimeout = 1000;
        final long tolerance = 100;
        OzoneConfiguration conf = SCMTestUtils.getConf();
        try (EndpointStateMachine rpcEndPoint = ContainerTestUtils.createEndpoint(conf, TestEndPoint.serverAddress, ((int) (rpcTimeout)))) {
            rpcEndPoint.setState(GETVERSION);
            DatanodeDetails datanodeDetails = TestUtils.randomDatanodeDetails();
            OzoneContainer ozoneContainer = new OzoneContainer(datanodeDetails, conf, getContext(datanodeDetails));
            VersionEndpointTask versionTask = new VersionEndpointTask(rpcEndPoint, conf, ozoneContainer);
            TestEndPoint.scmServerImpl.setRpcResponseDelay(1500);
            long start = Time.monotonicNow();
            EndpointStateMachine.EndPointStates newState = versionTask.call();
            long end = Time.monotonicNow();
            TestEndPoint.scmServerImpl.setRpcResponseDelay(0);
            Assert.assertThat((end - start), Matchers.lessThanOrEqualTo((rpcTimeout + tolerance)));
            Assert.assertEquals(GETVERSION, newState);
        }
    }

    @Test
    public void testRegister() throws Exception {
        DatanodeDetails nodeToRegister = TestUtils.randomDatanodeDetails();
        try (EndpointStateMachine rpcEndPoint = ContainerTestUtils.createEndpoint(SCMTestUtils.getConf(), TestEndPoint.serverAddress, 1000)) {
            SCMRegisteredResponseProto responseProto = rpcEndPoint.getEndPoint().register(nodeToRegister.getProtoBufMessage(), TestUtils.createNodeReport(getStorageReports(nodeToRegister.getUuid())), TestUtils.getRandomContainerReports(10), TestUtils.getRandomPipelineReports());
            Assert.assertNotNull(responseProto);
            Assert.assertEquals(nodeToRegister.getUuidString(), responseProto.getDatanodeUUID());
            Assert.assertNotNull(responseProto.getClusterID());
            Assert.assertEquals(10, TestEndPoint.scmServerImpl.getContainerCountsForDatanode(nodeToRegister));
            Assert.assertEquals(1, TestEndPoint.scmServerImpl.getNodeReportsCount(nodeToRegister));
        }
    }

    @Test
    public void testRegisterTask() throws Exception {
        try (EndpointStateMachine rpcEndpoint = registerTaskHelper(TestEndPoint.serverAddress, 1000, false)) {
            // Successful register should move us to Heartbeat state.
            Assert.assertEquals(HEARTBEAT, rpcEndpoint.getState());
        }
    }

    @Test
    public void testRegisterToInvalidEndpoint() throws Exception {
        InetSocketAddress address = SCMTestUtils.getReuseableAddress();
        try (EndpointStateMachine rpcEndpoint = registerTaskHelper(address, 1000, false)) {
            Assert.assertEquals(REGISTER, rpcEndpoint.getState());
        }
    }

    @Test
    public void testRegisterNoContainerID() throws Exception {
        InetSocketAddress address = SCMTestUtils.getReuseableAddress();
        try (EndpointStateMachine rpcEndpoint = registerTaskHelper(address, 1000, true)) {
            // No Container ID, therefore we tell the datanode that we would like to
            // shutdown.
            Assert.assertEquals(SHUTDOWN, rpcEndpoint.getState());
        }
    }

    @Test
    public void testRegisterRpcTimeout() throws Exception {
        final long rpcTimeout = 1000;
        final long tolerance = 200;
        TestEndPoint.scmServerImpl.setRpcResponseDelay(1500);
        long start = Time.monotonicNow();
        registerTaskHelper(TestEndPoint.serverAddress, 1000, false).close();
        long end = Time.monotonicNow();
        TestEndPoint.scmServerImpl.setRpcResponseDelay(0);
        Assert.assertThat((end - start), Matchers.lessThanOrEqualTo((rpcTimeout + tolerance)));
    }

    @Test
    public void testHeartbeat() throws Exception {
        DatanodeDetails dataNode = TestUtils.randomDatanodeDetails();
        try (EndpointStateMachine rpcEndPoint = ContainerTestUtils.createEndpoint(SCMTestUtils.getConf(), TestEndPoint.serverAddress, 1000)) {
            SCMHeartbeatRequestProto request = SCMHeartbeatRequestProto.newBuilder().setDatanodeDetails(dataNode.getProtoBufMessage()).setNodeReport(TestUtils.createNodeReport(getStorageReports(UUID.randomUUID()))).build();
            SCMHeartbeatResponseProto responseProto = rpcEndPoint.getEndPoint().sendHeartbeat(request);
            Assert.assertNotNull(responseProto);
            Assert.assertEquals(0, responseProto.getCommandsCount());
        }
    }

    @Test
    public void testHeartbeatWithCommandStatusReport() throws Exception {
        DatanodeDetails dataNode = TestUtils.randomDatanodeDetails();
        try (EndpointStateMachine rpcEndPoint = ContainerTestUtils.createEndpoint(SCMTestUtils.getConf(), TestEndPoint.serverAddress, 1000)) {
            // Add some scmCommands for heartbeat response
            addScmCommands();
            SCMHeartbeatRequestProto request = SCMHeartbeatRequestProto.newBuilder().setDatanodeDetails(dataNode.getProtoBufMessage()).setNodeReport(TestUtils.createNodeReport(getStorageReports(UUID.randomUUID()))).build();
            SCMHeartbeatResponseProto responseProto = rpcEndPoint.getEndPoint().sendHeartbeat(request);
            Assert.assertNotNull(responseProto);
            Assert.assertEquals(3, responseProto.getCommandsCount());
            Assert.assertEquals(0, TestEndPoint.scmServerImpl.getCommandStatusReportCount());
            // Send heartbeat again from heartbeat endpoint task
            final StateContext stateContext = heartbeatTaskHelper(TestEndPoint.serverAddress, 3000);
            Map<Long, CommandStatus> map = stateContext.getCommandStatusMap();
            Assert.assertNotNull(map);
            Assert.assertEquals("Should have 2 objects", 2, map.size());
            Assert.assertTrue(map.containsKey(Long.valueOf(2)));
            Assert.assertTrue(map.containsKey(Long.valueOf(3)));
            Assert.assertTrue(map.get(Long.valueOf(2)).getType().equals(replicateContainerCommand));
            Assert.assertTrue(map.get(Long.valueOf(3)).getType().equals(deleteBlocksCommand));
            Assert.assertTrue(map.get(Long.valueOf(2)).getStatus().equals(PENDING));
            Assert.assertTrue(map.get(Long.valueOf(3)).getStatus().equals(PENDING));
            TestEndPoint.scmServerImpl.clearScmCommandRequests();
        }
    }

    @Test
    public void testHeartbeatTask() throws Exception {
        heartbeatTaskHelper(TestEndPoint.serverAddress, 1000);
    }

    @Test
    public void testHeartbeatTaskToInvalidNode() throws Exception {
        InetSocketAddress invalidAddress = SCMTestUtils.getReuseableAddress();
        heartbeatTaskHelper(invalidAddress, 1000);
    }

    @Test
    public void testHeartbeatTaskRpcTimeOut() throws Exception {
        final long rpcTimeout = 1000;
        final long tolerance = 200;
        TestEndPoint.scmServerImpl.setRpcResponseDelay(1500);
        long start = Time.monotonicNow();
        InetSocketAddress invalidAddress = SCMTestUtils.getReuseableAddress();
        heartbeatTaskHelper(invalidAddress, 1000);
        long end = Time.monotonicNow();
        TestEndPoint.scmServerImpl.setRpcResponseDelay(0);
        Assert.assertThat((end - start), Matchers.lessThanOrEqualTo((rpcTimeout + tolerance)));
    }
}

