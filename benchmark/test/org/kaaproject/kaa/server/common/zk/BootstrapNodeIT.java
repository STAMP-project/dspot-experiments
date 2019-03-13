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


import java.util.List;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.test.TestingCluster;
import org.apache.curator.test.Timing;
import org.junit.Assert;
import org.junit.Test;
import org.kaaproject.kaa.server.common.zk.bootstrap.BootstrapNode;
import org.kaaproject.kaa.server.common.zk.bootstrap.BootstrapNodeListener;
import org.kaaproject.kaa.server.common.zk.control.ControlNode;
import org.kaaproject.kaa.server.common.zk.gen.BootstrapNodeInfo;
import org.kaaproject.kaa.server.common.zk.gen.ControlNodeInfo;
import org.kaaproject.kaa.server.common.zk.gen.TransportMetaData;
import org.mockito.Mockito;


public class BootstrapNodeIT {
    static final int TCP_ID = 73;

    static final int HTTP_ID = 42;

    private static final String UTF_8 = "UTF-8";

    private static final String BOOTSTRAP_NODE_HOST = "192.168.0.202";

    private static final String CONTROL_NODE_HOST = "192.168.0.1";

    private CuratorFramework zkClient;

    private TestingCluster cluster;

    @Test
    public void boostrapListenerTest() throws Exception {
        Timing timing = new Timing();
        ControlNodeInfo controlNodeInfo = buildControlNodeInfo();
        BootstrapNodeInfo bootstrapNodeInfo = buildBootstrapNodeInfo();
        ControlNode controlNode = new ControlNode(controlNodeInfo, zkClient);
        BootstrapNodeListener mockListener = Mockito.mock(BootstrapNodeListener.class);
        controlNode.addListener(mockListener);
        controlNode.start();
        BootstrapNode bootstrapNode = new BootstrapNode(bootstrapNodeInfo, zkClient);
        bootstrapNode.start();
        timing.sleepABit();
        Mockito.verify(mockListener).onNodeAdded(bootstrapNodeInfo);
        List<TransportMetaData> transports = bootstrapNodeInfo.getTransports();
        transports.remove(BootstrapNodeIT.getHttpTransportMD());
        bootstrapNode.updateNodeData(bootstrapNodeInfo);
        timing.sleepABit();
        Mockito.verify(mockListener).onNodeUpdated(bootstrapNodeInfo);
        bootstrapNode.close();
        timing.sleepABit();
        Mockito.verify(mockListener).onNodeRemoved(bootstrapNodeInfo);
        bootstrapNode.close();
        Assert.assertTrue(controlNode.removeListener(mockListener));
        Assert.assertFalse(controlNode.removeListener(mockListener));
        controlNode.close();
    }

    @Test
    public void outdatedRemovalTest() throws Exception {
        Timing timing = new Timing();
        ControlNodeInfo controlNodeInfo = buildControlNodeInfo();
        BootstrapNodeInfo bootstrapNodeInfo = buildBootstrapNodeInfo();
        ControlNode controlNode = new ControlNode(controlNodeInfo, zkClient);
        BootstrapNodeListener mockListener = Mockito.mock(BootstrapNodeListener.class);
        controlNode.addListener(mockListener);
        controlNode.start();
        BootstrapNode bootstrapNode = new BootstrapNode(bootstrapNodeInfo, zkClient);
        bootstrapNode.start();
        timing.sleepABit();
        Mockito.verify(mockListener).onNodeAdded(bootstrapNodeInfo);
        BootstrapNodeInfo bootstrapNodeInfoWithGreaterTimeStarted = buildBootstrapNodeInfo();
        BootstrapNode bootstrapNodeWithGreaterTimeStarted = new BootstrapNode(bootstrapNodeInfoWithGreaterTimeStarted, zkClient);
        bootstrapNodeWithGreaterTimeStarted.start();
        timing.sleepABit();
        bootstrapNode.close();
        timing.sleepABit();
        Mockito.verify(mockListener, Mockito.never()).onNodeRemoved(bootstrapNodeInfo);
        bootstrapNodeWithGreaterTimeStarted.close();
        timing.sleepABit();
        Mockito.verify(mockListener).onNodeRemoved(bootstrapNodeInfoWithGreaterTimeStarted);
        controlNode.close();
    }
}

