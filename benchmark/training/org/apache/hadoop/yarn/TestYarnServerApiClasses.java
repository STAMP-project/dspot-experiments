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
package org.apache.hadoop.yarn;


import NodeAction.NORMAL;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.apache.hadoop.hdfs.security.token.delegation.DelegationTokenIdentifier;
import org.apache.hadoop.io.DataInputByteBuffer;
import org.apache.hadoop.io.DataOutputBuffer;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.api.records.NodeLabel;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.impl.pb.SerializedExceptionPBImpl;
import org.apache.hadoop.yarn.factories.RecordFactory;
import org.apache.hadoop.yarn.factory.providers.RecordFactoryProvider;
import org.apache.hadoop.yarn.server.api.protocolrecords.RegisterNodeManagerRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb.NodeHeartbeatRequestPBImpl;
import org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb.NodeHeartbeatResponsePBImpl;
import org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb.RegisterNodeManagerRequestPBImpl;
import org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb.RegisterNodeManagerResponsePBImpl;
import org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb.UnRegisterNodeManagerRequestPBImpl;
import org.apache.hadoop.yarn.server.api.records.AppCollectorData;
import org.apache.hadoop.yarn.server.api.records.impl.pb.MasterKeyPBImpl;
import org.apache.hadoop.yarn.server.api.records.impl.pb.NodeStatusPBImpl;
import org.apache.hadoop.yarn.server.utils.YarnServerBuilderUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * Simple test classes from org.apache.hadoop.yarn.server.api
 */
public class TestYarnServerApiClasses {
    private static final RecordFactory recordFactory = RecordFactoryProvider.getRecordFactory(null);

    /**
     * Test RegisterNodeManagerResponsePBImpl. Test getters and setters. The
     * RegisterNodeManagerResponsePBImpl should generate a prototype and data
     * restore from prototype
     */
    @Test
    public void testRegisterNodeManagerResponsePBImpl() {
        RegisterNodeManagerResponsePBImpl original = new RegisterNodeManagerResponsePBImpl();
        original.setContainerTokenMasterKey(getMasterKey());
        original.setNMTokenMasterKey(getMasterKey());
        original.setNodeAction(NORMAL);
        original.setDiagnosticsMessage("testDiagnosticMessage");
        RegisterNodeManagerResponsePBImpl copy = new RegisterNodeManagerResponsePBImpl(original.getProto());
        Assert.assertEquals(1, copy.getContainerTokenMasterKey().getKeyId());
        Assert.assertEquals(1, copy.getNMTokenMasterKey().getKeyId());
        Assert.assertEquals(NORMAL, copy.getNodeAction());
        Assert.assertEquals("testDiagnosticMessage", copy.getDiagnosticsMessage());
        Assert.assertFalse(copy.getAreNodeLabelsAcceptedByRM());
    }

    @Test
    public void testRegisterNodeManagerResponsePBImplWithRMAcceptLbls() {
        RegisterNodeManagerResponsePBImpl original = new RegisterNodeManagerResponsePBImpl();
        original.setAreNodeLabelsAcceptedByRM(true);
        RegisterNodeManagerResponsePBImpl copy = new RegisterNodeManagerResponsePBImpl(original.getProto());
        Assert.assertTrue(copy.getAreNodeLabelsAcceptedByRM());
    }

    /**
     * Test NodeHeartbeatRequestPBImpl.
     */
    @Test
    public void testNodeHeartbeatRequestPBImpl() {
        NodeHeartbeatRequestPBImpl original = new NodeHeartbeatRequestPBImpl();
        original.setLastKnownContainerTokenMasterKey(getMasterKey());
        original.setLastKnownNMTokenMasterKey(getMasterKey());
        original.setNodeStatus(getNodeStatus());
        original.setNodeLabels(getValidNodeLabels());
        Map<ApplicationId, AppCollectorData> collectors = getCollectors(false);
        original.setRegisteringCollectors(collectors);
        NodeHeartbeatRequestPBImpl copy = new NodeHeartbeatRequestPBImpl(original.getProto());
        Assert.assertEquals(1, copy.getLastKnownContainerTokenMasterKey().getKeyId());
        Assert.assertEquals(1, copy.getLastKnownNMTokenMasterKey().getKeyId());
        Assert.assertEquals("localhost", copy.getNodeStatus().getNodeId().getHost());
        Assert.assertEquals(collectors, copy.getRegisteringCollectors());
        // check labels are coming with valid values
        Assert.assertTrue(original.getNodeLabels().containsAll(copy.getNodeLabels()));
        // check for empty labels
        original.setNodeLabels(new HashSet<NodeLabel>());
        copy = new NodeHeartbeatRequestPBImpl(original.getProto());
        Assert.assertNotNull(copy.getNodeLabels());
        Assert.assertEquals(0, copy.getNodeLabels().size());
    }

    @Test
    public void testNodeHBRequestPBImplWithNullCollectorToken() {
        NodeHeartbeatRequestPBImpl original = new NodeHeartbeatRequestPBImpl();
        Map<ApplicationId, AppCollectorData> collectors = getCollectors(true);
        original.setRegisteringCollectors(collectors);
        NodeHeartbeatRequestPBImpl copy = new NodeHeartbeatRequestPBImpl(original.getProto());
        Assert.assertEquals(collectors, copy.getRegisteringCollectors());
    }

    /**
     * Test NodeHeartbeatRequestPBImpl.
     */
    @Test
    public void testNodeHeartbeatRequestPBImplWithNullLabels() {
        NodeHeartbeatRequestPBImpl original = new NodeHeartbeatRequestPBImpl();
        NodeHeartbeatRequestPBImpl copy = new NodeHeartbeatRequestPBImpl(original.getProto());
        Assert.assertNull(copy.getNodeLabels());
    }

    /**
     * Test NodeHeartbeatResponsePBImpl.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testNodeHeartbeatResponsePBImpl() throws IOException {
        NodeHeartbeatResponsePBImpl original = new NodeHeartbeatResponsePBImpl();
        original.setDiagnosticsMessage("testDiagnosticMessage");
        original.setContainerTokenMasterKey(getMasterKey());
        original.setNMTokenMasterKey(getMasterKey());
        original.setNextHeartBeatInterval(1000);
        original.setNodeAction(NORMAL);
        original.setResponseId(100);
        Map<ApplicationId, AppCollectorData> collectors = getCollectors(false);
        original.setAppCollectors(collectors);
        // create token1
        Text userText1 = new Text("user1");
        DelegationTokenIdentifier dtId1 = new DelegationTokenIdentifier(userText1, new Text("renewer1"), userText1);
        final Token<DelegationTokenIdentifier> expectedToken1 = new Token<DelegationTokenIdentifier>(dtId1.getBytes(), "password12".getBytes(), dtId1.getKind(), new Text("service1"));
        Credentials credentials1 = new Credentials();
        credentials1.addToken(expectedToken1.getService(), expectedToken1);
        DataOutputBuffer dob1 = new DataOutputBuffer();
        credentials1.writeTokenStorageToStream(dob1);
        ByteBuffer byteBuffer1 = ByteBuffer.wrap(dob1.getData(), 0, dob1.getLength());
        Map<ApplicationId, ByteBuffer> systemCredentials = new HashMap<ApplicationId, ByteBuffer>();
        systemCredentials.put(getApplicationId(1), byteBuffer1);
        original.setSystemCredentialsForApps(YarnServerBuilderUtils.convertToProtoFormat(systemCredentials));
        NodeHeartbeatResponsePBImpl copy = new NodeHeartbeatResponsePBImpl(original.getProto());
        Assert.assertEquals(100, copy.getResponseId());
        Assert.assertEquals(NORMAL, copy.getNodeAction());
        Assert.assertEquals(1000, copy.getNextHeartBeatInterval());
        Assert.assertEquals(1, copy.getContainerTokenMasterKey().getKeyId());
        Assert.assertEquals(1, copy.getNMTokenMasterKey().getKeyId());
        Assert.assertEquals("testDiagnosticMessage", copy.getDiagnosticsMessage());
        Assert.assertEquals(collectors, copy.getAppCollectors());
        Assert.assertEquals(false, copy.getAreNodeLabelsAcceptedByRM());
        Assert.assertEquals(1, copy.getSystemCredentialsForApps().size());
        Credentials credentials1Out = new Credentials();
        DataInputByteBuffer buf = new DataInputByteBuffer();
        ByteBuffer buffer = YarnServerBuilderUtils.convertFromProtoFormat(copy.getSystemCredentialsForApps()).get(getApplicationId(1));
        Assert.assertNotNull(buffer);
        buffer.rewind();
        buf.reset(buffer);
        credentials1Out.readTokenStorageStream(buf);
        Assert.assertEquals(1, credentials1Out.getAllTokens().size());
        // Ensure token1's password "password12" is available from proto response
        Assert.assertEquals(10, credentials1Out.getAllTokens().iterator().next().getPassword().length);
    }

    @Test
    public void testNodeHeartbeatResponsePBImplWithRMAcceptLbls() {
        NodeHeartbeatResponsePBImpl original = new NodeHeartbeatResponsePBImpl();
        original.setAreNodeLabelsAcceptedByRM(true);
        NodeHeartbeatResponsePBImpl copy = new NodeHeartbeatResponsePBImpl(original.getProto());
        Assert.assertTrue(copy.getAreNodeLabelsAcceptedByRM());
    }

    @Test
    public void testNodeHBResponsePBImplWithNullCollectorToken() {
        NodeHeartbeatResponsePBImpl original = new NodeHeartbeatResponsePBImpl();
        Map<ApplicationId, AppCollectorData> collectors = getCollectors(true);
        original.setAppCollectors(collectors);
        NodeHeartbeatResponsePBImpl copy = new NodeHeartbeatResponsePBImpl(original.getProto());
        Assert.assertEquals(collectors, copy.getAppCollectors());
    }

    @Test
    public void testNodeHeartbeatResponsePBImplWithDecreasedContainers() {
        NodeHeartbeatResponsePBImpl original = new NodeHeartbeatResponsePBImpl();
        original.addAllContainersToUpdate(Arrays.asList(getDecreasedContainer(1, 2, 2048, 2), getDecreasedContainer(2, 3, 1024, 1)));
        NodeHeartbeatResponsePBImpl copy = new NodeHeartbeatResponsePBImpl(original.getProto());
        Assert.assertEquals(1, copy.getContainersToUpdate().get(0).getId().getContainerId());
        Assert.assertEquals(1024, copy.getContainersToUpdate().get(1).getResource().getMemorySize());
    }

    /**
     * Test RegisterNodeManagerRequestPBImpl.
     */
    @Test
    public void testRegisterNodeManagerRequestPBImpl() {
        RegisterNodeManagerRequestPBImpl original = new RegisterNodeManagerRequestPBImpl();
        original.setHttpPort(8080);
        original.setNodeId(getNodeId());
        Resource resource = TestYarnServerApiClasses.recordFactory.newRecordInstance(Resource.class);
        resource.setMemorySize(10000);
        resource.setVirtualCores(2);
        original.setResource(resource);
        original.setPhysicalResource(resource);
        RegisterNodeManagerRequestPBImpl copy = new RegisterNodeManagerRequestPBImpl(original.getProto());
        Assert.assertEquals(8080, copy.getHttpPort());
        Assert.assertEquals(9090, copy.getNodeId().getPort());
        Assert.assertEquals(10000, copy.getResource().getMemorySize());
        Assert.assertEquals(2, copy.getResource().getVirtualCores());
        Assert.assertEquals(10000, copy.getPhysicalResource().getMemorySize());
        Assert.assertEquals(2, copy.getPhysicalResource().getVirtualCores());
    }

    /**
     * Test MasterKeyPBImpl.
     */
    @Test
    public void testMasterKeyPBImpl() {
        MasterKeyPBImpl original = new MasterKeyPBImpl();
        original.setBytes(ByteBuffer.allocate(0));
        original.setKeyId(1);
        MasterKeyPBImpl copy = new MasterKeyPBImpl(original.getProto());
        Assert.assertEquals(1, copy.getKeyId());
        Assert.assertTrue(original.equals(copy));
        Assert.assertEquals(original.hashCode(), copy.hashCode());
    }

    /**
     * Test SerializedExceptionPBImpl.
     */
    @Test
    public void testSerializedExceptionPBImpl() {
        SerializedExceptionPBImpl original = new SerializedExceptionPBImpl();
        original.init("testMessage");
        SerializedExceptionPBImpl copy = new SerializedExceptionPBImpl(original.getProto());
        Assert.assertEquals("testMessage", copy.getMessage());
        original = new SerializedExceptionPBImpl();
        original.init("testMessage", new Throwable(new Throwable("parent")));
        copy = new SerializedExceptionPBImpl(original.getProto());
        Assert.assertEquals("testMessage", copy.getMessage());
        Assert.assertEquals("parent", copy.getCause().getMessage());
        Assert.assertTrue(copy.getRemoteTrace().startsWith("java.lang.Throwable: java.lang.Throwable: parent"));
    }

    /**
     * Test NodeStatusPBImpl.
     */
    @Test
    public void testNodeStatusPBImpl() {
        NodeStatusPBImpl original = new NodeStatusPBImpl();
        original.setContainersStatuses(Arrays.asList(getContainerStatus(1, 2, 1), getContainerStatus(2, 3, 1)));
        original.setKeepAliveApplications(Arrays.asList(getApplicationId(3), getApplicationId(4)));
        original.setNodeHealthStatus(getNodeHealthStatus());
        original.setNodeId(getNodeId());
        original.setResponseId(1);
        original.setIncreasedContainers(Arrays.asList(getIncreasedContainer(1, 2, 2048, 2), getIncreasedContainer(2, 3, 4096, 3)));
        NodeStatusPBImpl copy = new NodeStatusPBImpl(original.getProto());
        Assert.assertEquals(3L, copy.getContainersStatuses().get(1).getContainerId().getContainerId());
        Assert.assertEquals(3, copy.getKeepAliveApplications().get(0).getId());
        Assert.assertEquals(1000, copy.getNodeHealthStatus().getLastHealthReportTime());
        Assert.assertEquals(9090, copy.getNodeId().getPort());
        Assert.assertEquals(1, copy.getResponseId());
        Assert.assertEquals(1, copy.getIncreasedContainers().get(0).getId().getContainerId());
        Assert.assertEquals(4096, copy.getIncreasedContainers().get(1).getResource().getMemorySize());
    }

    @Test
    public void testRegisterNodeManagerRequestWithNullLabels() {
        RegisterNodeManagerRequest request = RegisterNodeManagerRequest.newInstance(NodeId.newInstance("host", 1234), 1234, Resource.newInstance(0, 0), "version", null, null);
        // serialze to proto, and get request from proto
        RegisterNodeManagerRequest request1 = new RegisterNodeManagerRequestPBImpl(getProto());
        // check labels are coming with no values
        Assert.assertNull(request1.getNodeLabels());
    }

    @Test
    public void testRegisterNodeManagerRequestWithValidLabels() {
        HashSet<NodeLabel> nodeLabels = getValidNodeLabels();
        RegisterNodeManagerRequest request = RegisterNodeManagerRequest.newInstance(NodeId.newInstance("host", 1234), 1234, Resource.newInstance(0, 0), "version", null, null, nodeLabels);
        // serialze to proto, and get request from proto
        RegisterNodeManagerRequest copy = new RegisterNodeManagerRequestPBImpl(getProto());
        // check labels are coming with valid values
        Assert.assertEquals(true, nodeLabels.containsAll(copy.getNodeLabels()));
        // check for empty labels
        request.setNodeLabels(new HashSet<NodeLabel>());
        copy = new RegisterNodeManagerRequestPBImpl(getProto());
        Assert.assertNotNull(copy.getNodeLabels());
        Assert.assertEquals(0, copy.getNodeLabels().size());
    }

    @Test
    public void testUnRegisterNodeManagerRequestPBImpl() throws Exception {
        UnRegisterNodeManagerRequestPBImpl request = new UnRegisterNodeManagerRequestPBImpl();
        NodeId nodeId = NodeId.newInstance("host", 1234);
        request.setNodeId(nodeId);
        UnRegisterNodeManagerRequestPBImpl copy = new UnRegisterNodeManagerRequestPBImpl(request.getProto());
        Assert.assertEquals(nodeId, copy.getNodeId());
    }
}

