/**
 * Copyright 2014-2016 CyberVision, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kaaproject.kaa.server.common.zk;


import java.io.IOException;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.test.TestingCluster;
import org.apache.curator.test.Timing;
import org.apache.zookeeper.data.Stat;
import org.junit.Assert;
import org.junit.Test;
import org.kaaproject.kaa.server.common.zk.bootstrap.BootstrapNode;
import org.kaaproject.kaa.server.common.zk.gen.BootstrapNodeInfo;
import org.kaaproject.kaa.server.common.zk.gen.OperationsNodeInfo;
import org.kaaproject.kaa.server.common.zk.operations.OperationsNode;
import org.kaaproject.kaa.server.common.zk.operations.OperationsNodeListener;
import org.mockito.Mockito;


public class OperationsNodeIT {
    private static final int NEW_HTTP_ID = (BootstrapNodeIT.HTTP_ID) + 1;

    private static final String BOOTSTRAP_NODE_HOST = "192.168.0.202";

    private static final String ENDPOINT_NODE_HOST = "192.168.0.101";

    private CuratorFramework zkClient;

    private TestingCluster cluster;

    @Test
    public void endpointListenerTest() throws Exception {
        Timing timing = new Timing();
        OperationsNodeInfo endpointNodeInfo = buildOperationsNodeInfo();
        BootstrapNodeInfo bootstrapNodeInfo = buildBootstrapNodeInfo();
        BootstrapNode bootstrapNode = new BootstrapNode(bootstrapNodeInfo, zkClient);
        OperationsNodeListener mockListener = Mockito.mock(OperationsNodeListener.class);
        bootstrapNode.addListener(mockListener);
        bootstrapNode.start();
        OperationsNode endpointNode = new OperationsNode(endpointNodeInfo, zkClient);
        endpointNode.start();
        timing.sleepABit();
        Mockito.verify(mockListener).onNodeAdded(endpointNodeInfo);
        Assert.assertNotNull(bootstrapNode.getCurrentOperationServerNodes());
        Assert.assertEquals(1, bootstrapNode.getCurrentOperationServerNodes().size());
        OperationsNodeInfo testNodeInfo = bootstrapNode.getCurrentOperationServerNodes().get(0);
        Assert.assertNotNull(testNodeInfo.getTransports());
        Assert.assertEquals(2, testNodeInfo.getTransports().size());
        Assert.assertNotNull(testNodeInfo.getTransports().get(0));
        Assert.assertEquals(BootstrapNodeIT.HTTP_ID, testNodeInfo.getTransports().get(0).getId().intValue());
        Assert.assertEquals(BootstrapNodeIT.TCP_ID, testNodeInfo.getTransports().get(1).getId().intValue());
        Assert.assertNotNull(testNodeInfo.getTransports().get(0).getConnectionInfo());
        endpointNodeInfo.getTransports().get(0).setId(OperationsNodeIT.NEW_HTTP_ID);
        endpointNode.updateNodeData(endpointNodeInfo);
        timing.sleepABit();
        Mockito.verify(mockListener).onNodeUpdated(endpointNodeInfo);
        Assert.assertNotNull(bootstrapNode.getCurrentOperationServerNodes());
        Assert.assertEquals(1, bootstrapNode.getCurrentOperationServerNodes().size());
        Assert.assertNotNull(bootstrapNode.getCurrentOperationServerNodes().get(0));
        testNodeInfo = bootstrapNode.getCurrentOperationServerNodes().get(0);
        Assert.assertNotNull(testNodeInfo.getTransports());
        Assert.assertEquals(OperationsNodeIT.NEW_HTTP_ID, testNodeInfo.getTransports().get(0).getId().intValue());
        endpointNode.close();
        timing.sleepABit();
        Mockito.verify(mockListener).onNodeRemoved(endpointNodeInfo);
        Assert.assertTrue(bootstrapNode.removeListener(mockListener));
        Assert.assertFalse(bootstrapNode.removeListener(mockListener));
        bootstrapNode.close();
    }

    @Test
    public void endpointExceptionTest() throws Exception {
        OperationsNodeInfo endpointNodeInfo = buildOperationsNodeInfo();
        OperationsNode endpointNode = new OperationsNode(endpointNodeInfo, zkClient);
        endpointNode.start();
        endpointNode.doZkClientAction(new ControlNodeTracker.ZkClientAction() {
            @Override
            public void doWithZkClient(CuratorFramework client) throws Exception {
                throw new Exception("for test");
            }
        });
        // check if operations node changed and deleted child node
        // after unexpected exception, as child node was ephemeral and connection lost
        String OPERATIONS_SERVER_NODE_PATH = "/operationsServerNodes";
        Stat stat = zkClient.checkExists().forPath(OPERATIONS_SERVER_NODE_PATH);
        int cversion = stat.getCversion();
        int numChildren = stat.getNumChildren();
        Assert.assertEquals(cversion, 2);
        Assert.assertEquals(numChildren, 0);
        endpointNode.close();
    }

    @Test(expected = IOException.class)
    public void endpointIOExceptionTest() throws Exception {
        OperationsNodeInfo endpointNodeInfo = buildOperationsNodeInfo();
        OperationsNode endpointNode = new OperationsNode(endpointNodeInfo, zkClient);
        endpointNode.start();
        endpointNode.doZkClientAction(new ControlNodeTracker.ZkClientAction() {
            @Override
            public void doWithZkClient(CuratorFramework client) throws Exception {
                throw new Exception("for test");
            }
        }, true);
        Assert.assertFalse(endpointNode.isConnected());
        endpointNode.close();
    }

    @Test
    public void outdatedRemovalTest() throws Exception {
        Timing timing = new Timing();
        OperationsNodeInfo endpointNodeInfo = buildOperationsNodeInfo();
        BootstrapNodeInfo bootstrapNodeInfo = buildBootstrapNodeInfo();
        BootstrapNode bootstrapNode = new BootstrapNode(bootstrapNodeInfo, zkClient);
        OperationsNodeListener mockListener = Mockito.mock(OperationsNodeListener.class);
        bootstrapNode.addListener(mockListener);
        bootstrapNode.start();
        OperationsNode endpointNode = new OperationsNode(endpointNodeInfo, zkClient);
        endpointNode.start();
        timing.sleepABit();
        Mockito.verify(mockListener).onNodeAdded(endpointNodeInfo);
        OperationsNodeInfo endpointNodeInfoWithGreaterTimeStarted = buildOperationsNodeInfo();
        OperationsNode endpointNodeWithGreaterTimeStarted = new OperationsNode(endpointNodeInfoWithGreaterTimeStarted, zkClient);
        endpointNodeWithGreaterTimeStarted.start();
        timing.sleepABit();
        endpointNode.close();
        timing.sleepABit();
        Mockito.verify(mockListener, Mockito.never()).onNodeRemoved(endpointNodeInfo);
        endpointNodeWithGreaterTimeStarted.close();
        timing.sleepABit();
        Mockito.verify(mockListener).onNodeRemoved(endpointNodeInfoWithGreaterTimeStarted);
        bootstrapNode.close();
    }
}

