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


import java.io.IOException;
import org.apache.hadoop.ipc.Server;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.factories.RecordFactory;
import org.apache.hadoop.yarn.factory.providers.RecordFactoryProvider;
import org.apache.hadoop.yarn.server.api.ResourceTracker;
import org.apache.hadoop.yarn.server.api.protocolrecords.NodeHeartbeatRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.NodeHeartbeatResponse;
import org.apache.hadoop.yarn.server.api.protocolrecords.RegisterNodeManagerRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.RegisterNodeManagerResponse;
import org.apache.hadoop.yarn.server.api.protocolrecords.UnRegisterNodeManagerRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.UnRegisterNodeManagerResponse;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test ResourceTrackerPBClientImpl. this class should have methods
 * registerNodeManager and newRecordInstance.
 */
public class TestResourceTrackerPBClientImpl {
    private static ResourceTracker client;

    private static Server server;

    private static final RecordFactory recordFactory = RecordFactoryProvider.getRecordFactory(null);

    /**
     * Test the method registerNodeManager. Method should return a not null
     * result.
     */
    @Test
    public void testResourceTrackerPBClientImpl() throws Exception {
        RegisterNodeManagerRequest request = TestResourceTrackerPBClientImpl.recordFactory.newRecordInstance(RegisterNodeManagerRequest.class);
        Assert.assertNotNull(TestResourceTrackerPBClientImpl.client.registerNodeManager(request));
        TestResourceTrackerPBClientImpl.ResourceTrackerTestImpl.exception = true;
        try {
            TestResourceTrackerPBClientImpl.client.registerNodeManager(request);
            Assert.fail("there  should be YarnException");
        } catch (YarnException e) {
            Assert.assertTrue(e.getMessage().startsWith("testMessage"));
        } finally {
            TestResourceTrackerPBClientImpl.ResourceTrackerTestImpl.exception = false;
        }
    }

    /**
     * Test the method nodeHeartbeat. Method should return a not null result.
     */
    @Test
    public void testNodeHeartbeat() throws Exception {
        NodeHeartbeatRequest request = TestResourceTrackerPBClientImpl.recordFactory.newRecordInstance(NodeHeartbeatRequest.class);
        Assert.assertNotNull(TestResourceTrackerPBClientImpl.client.nodeHeartbeat(request));
        TestResourceTrackerPBClientImpl.ResourceTrackerTestImpl.exception = true;
        try {
            TestResourceTrackerPBClientImpl.client.nodeHeartbeat(request);
            Assert.fail("there  should be YarnException");
        } catch (YarnException e) {
            Assert.assertTrue(e.getMessage().startsWith("testMessage"));
        } finally {
            TestResourceTrackerPBClientImpl.ResourceTrackerTestImpl.exception = false;
        }
    }

    /**
     * Test the method unRegisterNodeManager. Method should return a not null
     * result.
     */
    @Test
    public void testUnRegisterNodeManager() throws Exception {
        UnRegisterNodeManagerRequest request = UnRegisterNodeManagerRequest.newInstance(NodeId.newInstance("host1", 1234));
        Assert.assertNotNull(TestResourceTrackerPBClientImpl.client.unRegisterNodeManager(request));
        TestResourceTrackerPBClientImpl.ResourceTrackerTestImpl.exception = true;
        try {
            TestResourceTrackerPBClientImpl.client.unRegisterNodeManager(request);
            Assert.fail("there  should be YarnException");
        } catch (YarnException e) {
            Assert.assertTrue(e.getMessage().startsWith("testMessage"));
        } finally {
            TestResourceTrackerPBClientImpl.ResourceTrackerTestImpl.exception = false;
        }
    }

    public static class ResourceTrackerTestImpl implements ResourceTracker {
        public static boolean exception = false;

        @Override
        public RegisterNodeManagerResponse registerNodeManager(RegisterNodeManagerRequest request) throws IOException, YarnException {
            if (TestResourceTrackerPBClientImpl.ResourceTrackerTestImpl.exception) {
                throw new YarnException("testMessage");
            }
            return TestResourceTrackerPBClientImpl.recordFactory.newRecordInstance(RegisterNodeManagerResponse.class);
        }

        @Override
        public NodeHeartbeatResponse nodeHeartbeat(NodeHeartbeatRequest request) throws IOException, YarnException {
            if (TestResourceTrackerPBClientImpl.ResourceTrackerTestImpl.exception) {
                throw new YarnException("testMessage");
            }
            return TestResourceTrackerPBClientImpl.recordFactory.newRecordInstance(NodeHeartbeatResponse.class);
        }

        @Override
        public UnRegisterNodeManagerResponse unRegisterNodeManager(UnRegisterNodeManagerRequest request) throws IOException, YarnException {
            if (TestResourceTrackerPBClientImpl.ResourceTrackerTestImpl.exception) {
                throw new YarnException("testMessage");
            }
            return UnRegisterNodeManagerResponse.newInstance();
        }
    }
}

