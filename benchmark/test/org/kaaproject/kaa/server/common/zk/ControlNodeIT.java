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


import java.util.Random;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.test.TestingCluster;
import org.apache.curator.test.Timing;
import org.junit.Assert;
import org.junit.Test;
import org.kaaproject.kaa.server.common.zk.control.ControlNode;
import org.kaaproject.kaa.server.common.zk.control.ControlNodeListener;
import org.kaaproject.kaa.server.common.zk.gen.ControlNodeInfo;
import org.kaaproject.kaa.server.common.zk.gen.OperationsNodeInfo;
import org.kaaproject.kaa.server.common.zk.operations.OperationsNode;
import org.mockito.Mockito;


public class ControlNodeIT {
    private static final String ENDPOINT_NODE_HOST = "192.168.0.101";

    private static final String SECONDARY_NODE_HOST = "192.168.0.2";

    private static final String CONTROL_NODE_HOST = "192.168.0.1";

    private CuratorFramework zkClient;

    private TestingCluster cluster;

    @Test
    public void masterFailoverTest() throws Exception {
        Timing timing = new Timing();
        ControlNodeInfo controlNodeInfo = buildControlNodeInfo();
        ControlNodeInfo secondaryNodeInfo = buildSecondaryNodeInfo();
        OperationsNodeInfo endpointNodeInfo = buildOperationsNodeInfo();
        OperationsNode endpointNode = new OperationsNode(endpointNodeInfo, zkClient);
        endpointNode.start();
        Assert.assertNull(endpointNode.getControlServerInfo());
        ControlNode controlNode = new ControlNode(controlNodeInfo, zkClient);
        Assert.assertFalse(controlNode.isMaster());
        controlNode.start();
        ControlNode secondaryNode = new ControlNode(secondaryNodeInfo, zkClient);
        Assert.assertFalse(secondaryNode.isMaster());
        secondaryNode.start();
        timing.sleepABit();
        Assert.assertTrue(controlNode.isMaster());
        Assert.assertFalse(secondaryNode.isMaster());
        Assert.assertNotNull(endpointNode.getControlServerInfo());
        Assert.assertEquals(ControlNodeIT.CONTROL_NODE_HOST, endpointNode.getControlServerInfo().getConnectionInfo().getThriftHost().toString());
        controlNode.close();
        timing.sleepABit();
        Assert.assertNotNull(endpointNode.getControlServerInfo());
        Assert.assertEquals(ControlNodeIT.SECONDARY_NODE_HOST, endpointNode.getControlServerInfo().getConnectionInfo().getThriftHost().toString());
        secondaryNode.close();
        endpointNode.close();
    }

    @Test
    public void masterListenerTest() throws Exception {
        Timing timing = new Timing();
        ControlNodeInfo controlNodeInfo = buildControlNodeInfo();
        ControlNodeInfo secondaryNodeInfo = buildSecondaryNodeInfo();
        OperationsNodeInfo endpointNodeInfo = buildOperationsNodeInfo();
        OperationsNode endpointNode = new OperationsNode(endpointNodeInfo, zkClient);
        ControlNodeListener mockListener = Mockito.mock(ControlNodeListener.class);
        endpointNode.addListener(mockListener);
        endpointNode.start();
        ControlNode controlNode = new ControlNode(controlNodeInfo, zkClient);
        controlNode.start();
        ControlNode secondaryNode = new ControlNode(secondaryNodeInfo, zkClient);
        secondaryNode.start();
        timing.sleepABit();
        Mockito.verify(mockListener).onControlNodeChange(controlNodeInfo);
        int random = new Random().nextInt();
        controlNodeInfo.setBootstrapServerCount(random);
        controlNode.updateNodeData(controlNodeInfo);
        int random2 = new Random().nextInt();
        secondaryNodeInfo.setBootstrapServerCount(random2);
        secondaryNode.updateNodeData(secondaryNodeInfo);
        timing.sleepABit();
        Mockito.verify(mockListener).onControlNodeChange(controlNodeInfo);
        Assert.assertEquals(new Integer(random), endpointNode.getControlServerInfo().getBootstrapServerCount());
        Assert.assertEquals(new Integer(random2), secondaryNode.getCurrentNodeInfo().getBootstrapServerCount());
        controlNode.close();
        timing.sleepABit();
        Mockito.verify(mockListener).onControlNodeDown();
        Mockito.verify(mockListener).onControlNodeChange(secondaryNodeInfo);
        Assert.assertTrue(endpointNode.removeListener(mockListener));
        Assert.assertFalse(endpointNode.removeListener(mockListener));
        secondaryNode.close();
        endpointNode.close();
    }
}

