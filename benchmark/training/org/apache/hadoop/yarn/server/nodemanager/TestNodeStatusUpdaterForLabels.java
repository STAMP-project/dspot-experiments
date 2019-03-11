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
package org.apache.hadoop.yarn.server.nodemanager;


import NodeAction.NORMAL;
import YarnConfiguration.NM_LOCALIZER_ADDRESS;
import YarnConfiguration.NM_NODE_LABELS_RESYNC_INTERVAL;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Set;
import java.util.TimerTask;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.net.ServerSocketUtil;
import org.apache.hadoop.yarn.api.records.NodeLabel;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.event.Dispatcher;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.factories.RecordFactory;
import org.apache.hadoop.yarn.factory.providers.RecordFactoryProvider;
import org.apache.hadoop.yarn.nodelabels.NodeLabelTestBase;
import org.apache.hadoop.yarn.server.api.ResourceTracker;
import org.apache.hadoop.yarn.server.api.protocolrecords.NodeHeartbeatRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.NodeHeartbeatResponse;
import org.apache.hadoop.yarn.server.api.protocolrecords.RegisterNodeManagerRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.RegisterNodeManagerResponse;
import org.apache.hadoop.yarn.server.api.protocolrecords.UnRegisterNodeManagerRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.UnRegisterNodeManagerResponse;
import org.apache.hadoop.yarn.server.api.records.MasterKey;
import org.apache.hadoop.yarn.server.api.records.NodeStatus;
import org.apache.hadoop.yarn.server.api.records.impl.pb.MasterKeyPBImpl;
import org.apache.hadoop.yarn.server.nodemanager.nodelabels.NodeLabelsProvider;
import org.apache.hadoop.yarn.server.utils.YarnServerBuilderUtils;
import org.junit.Assert;
import org.junit.Test;


public class TestNodeStatusUpdaterForLabels extends NodeLabelTestBase {
    private static final RecordFactory recordFactory = RecordFactoryProvider.getRecordFactory(null);

    private NodeManager nm;

    protected TestNodeStatusUpdaterForLabels.DummyNodeLabelsProvider dummyLabelsProviderRef;

    private class ResourceTrackerForLabels implements ResourceTracker {
        int heartbeatID = 0;

        Set<NodeLabel> labels;

        private boolean receivedNMHeartbeat = false;

        private boolean receivedNMRegister = false;

        private MasterKey createMasterKey() {
            MasterKey masterKey = new MasterKeyPBImpl();
            masterKey.setKeyId(123);
            masterKey.setBytes(ByteBuffer.wrap(new byte[]{ new Integer(123).byteValue() }));
            return masterKey;
        }

        @Override
        public RegisterNodeManagerResponse registerNodeManager(RegisterNodeManagerRequest request) throws IOException, YarnException {
            labels = request.getNodeLabels();
            RegisterNodeManagerResponse response = TestNodeStatusUpdaterForLabels.recordFactory.newRecordInstance(RegisterNodeManagerResponse.class);
            response.setNodeAction(NORMAL);
            response.setContainerTokenMasterKey(createMasterKey());
            response.setNMTokenMasterKey(createMasterKey());
            response.setAreNodeLabelsAcceptedByRM(((labels) != null));
            synchronized(TestNodeStatusUpdaterForLabels.ResourceTrackerForLabels.class) {
                receivedNMRegister = true;
                TestNodeStatusUpdaterForLabels.ResourceTrackerForLabels.class.notifyAll();
            }
            return response;
        }

        public void waitTillHeartbeat() throws InterruptedException {
            if (receivedNMHeartbeat) {
                return;
            }
            int i = 15;
            while ((!(receivedNMHeartbeat)) && (i > 0)) {
                synchronized(TestNodeStatusUpdaterForLabels.ResourceTrackerForLabels.class) {
                    if (!(receivedNMHeartbeat)) {
                        System.out.println(("In ResourceTrackerForLabels waiting for heartbeat : " + (System.currentTimeMillis())));
                        TestNodeStatusUpdaterForLabels.ResourceTrackerForLabels.class.wait(200);
                        i--;
                    }
                }
            } 
            if (!(receivedNMHeartbeat)) {
                Assert.fail("Heartbeat dint receive even after waiting");
            }
        }

        public void waitTillRegister() throws InterruptedException {
            if (receivedNMRegister) {
                return;
            }
            while (!(receivedNMRegister)) {
                synchronized(TestNodeStatusUpdaterForLabels.ResourceTrackerForLabels.class) {
                    TestNodeStatusUpdaterForLabels.ResourceTrackerForLabels.class.wait();
                }
            } 
        }

        /**
         * Flag to indicate received any
         */
        public void resetNMHeartbeatReceiveFlag() {
            synchronized(TestNodeStatusUpdaterForLabels.ResourceTrackerForLabels.class) {
                receivedNMHeartbeat = false;
            }
        }

        @Override
        public NodeHeartbeatResponse nodeHeartbeat(NodeHeartbeatRequest request) throws IOException, YarnException {
            System.out.println(("RTS receive heartbeat : " + (System.currentTimeMillis())));
            labels = request.getNodeLabels();
            NodeStatus nodeStatus = request.getNodeStatus();
            nodeStatus.setResponseId(((heartbeatID)++));
            NodeHeartbeatResponse nhResponse = YarnServerBuilderUtils.newNodeHeartbeatResponse(heartbeatID, NORMAL, null, null, null, null, 1000L);
            // to ensure that heartbeats are sent only when required.
            nhResponse.setNextHeartBeatInterval(Long.MAX_VALUE);
            nhResponse.setAreNodeLabelsAcceptedByRM(((labels) != null));
            synchronized(TestNodeStatusUpdaterForLabels.ResourceTrackerForLabels.class) {
                receivedNMHeartbeat = true;
                TestNodeStatusUpdaterForLabels.ResourceTrackerForLabels.class.notifyAll();
            }
            return nhResponse;
        }

        @Override
        public UnRegisterNodeManagerResponse unRegisterNodeManager(UnRegisterNodeManagerRequest request) throws IOException, YarnException {
            return null;
        }
    }

    /**
     * A dummy NodeLabelsProvider class for tests.
     */
    public static class DummyNodeLabelsProvider extends NodeLabelsProvider {
        public DummyNodeLabelsProvider() {
            super("DummyNodeLabelsProvider");
            // disable the fetch timer.
            setIntervalTime((-1));
        }

        @Override
        protected void cleanUp() throws Exception {
            // fake implementation, nothing to cleanup
        }

        @Override
        public TimerTask createTimerTask() {
            return new TimerTask() {
                @Override
                public void run() {
                    setDescriptors(CommonNodeLabelsManager.EMPTY_NODELABEL_SET);
                }
            };
        }
    }

    @Test(timeout = 20000)
    public void testNodeStatusUpdaterForNodeLabels() throws IOException, InterruptedException {
        final TestNodeStatusUpdaterForLabels.ResourceTrackerForLabels resourceTracker = new TestNodeStatusUpdaterForLabels.ResourceTrackerForLabels();
        nm = new NodeManager() {
            @Override
            protected NodeLabelsProvider createNodeLabelsProvider(Configuration conf) throws IOException {
                return dummyLabelsProviderRef;
            }

            @Override
            protected NodeStatusUpdater createNodeStatusUpdater(Context context, Dispatcher dispatcher, NodeHealthCheckerService healthChecker) {
                return new NodeStatusUpdaterImpl(context, dispatcher, healthChecker, metrics) {
                    @Override
                    protected ResourceTracker getRMClient() {
                        return resourceTracker;
                    }

                    @Override
                    protected void stopRMProxy() {
                        return;
                    }
                };
            }
        };
        YarnConfiguration conf = createNMConfigForDistributeNodeLabels();
        conf.setLong(NM_NODE_LABELS_RESYNC_INTERVAL, 2000);
        conf.set(NM_LOCALIZER_ADDRESS, ("0.0.0.0:" + (ServerSocketUtil.getPort(8040, 10))));
        nm.init(conf);
        resourceTracker.resetNMHeartbeatReceiveFlag();
        nm.start();
        resourceTracker.waitTillRegister();
        assertNLCollectionEquals(getDescriptors(), resourceTracker.labels);
        resourceTracker.waitTillHeartbeat();// wait till the first heartbeat

        resourceTracker.resetNMHeartbeatReceiveFlag();
        // heartbeat with updated labels
        dummyLabelsProviderRef.setDescriptors(toNodeLabelSet("P"));
        sendOutofBandHeartBeat();
        resourceTracker.waitTillHeartbeat();
        assertNLCollectionEquals(getDescriptors(), resourceTracker.labels);
        resourceTracker.resetNMHeartbeatReceiveFlag();
        // heartbeat without updating labels
        sendOutofBandHeartBeat();
        resourceTracker.waitTillHeartbeat();
        resourceTracker.resetNMHeartbeatReceiveFlag();
        Assert.assertNull("If no change in labels then null should be sent as part of request", resourceTracker.labels);
        // provider return with null labels
        setDescriptors(null);
        sendOutofBandHeartBeat();
        resourceTracker.waitTillHeartbeat();
        Assert.assertNotNull("If provider sends null then empty label set should be sent and not null", resourceTracker.labels);
        Assert.assertTrue("If provider sends null then empty labels should be sent", resourceTracker.labels.isEmpty());
        resourceTracker.resetNMHeartbeatReceiveFlag();
        // Since the resync interval is set to 2 sec in every alternate heartbeat
        // the labels will be send along with heartbeat.In loop we sleep for 1 sec
        // so that every sec 1 heartbeat is send.
        int nullLabels = 0;
        int nonNullLabels = 0;
        dummyLabelsProviderRef.setDescriptors(toNodeLabelSet("P1"));
        for (int i = 0; i < 5; i++) {
            sendOutofBandHeartBeat();
            resourceTracker.waitTillHeartbeat();
            if (null == (resourceTracker.labels)) {
                nullLabels++;
            } else {
                Assert.assertEquals("In heartbeat PI labels should be send", toNodeLabelSet("P1"), resourceTracker.labels);
                nonNullLabels++;
            }
            resourceTracker.resetNMHeartbeatReceiveFlag();
            Thread.sleep(1000);
        }
        Assert.assertTrue("More than one heartbeat with empty labels expected", (nullLabels > 1));
        Assert.assertTrue("More than one heartbeat with labels expected", (nonNullLabels > 1));
        nm.stop();
    }

    @Test(timeout = 20000)
    public void testInvalidNodeLabelsFromProvider() throws IOException, InterruptedException {
        final TestNodeStatusUpdaterForLabels.ResourceTrackerForLabels resourceTracker = new TestNodeStatusUpdaterForLabels.ResourceTrackerForLabels();
        nm = new NodeManager() {
            @Override
            protected NodeLabelsProvider createNodeLabelsProvider(Configuration conf) throws IOException {
                return dummyLabelsProviderRef;
            }

            @Override
            protected NodeStatusUpdater createNodeStatusUpdater(Context context, Dispatcher dispatcher, NodeHealthCheckerService healthChecker) {
                return new NodeStatusUpdaterImpl(context, dispatcher, healthChecker, metrics) {
                    @Override
                    protected ResourceTracker getRMClient() {
                        return resourceTracker;
                    }

                    @Override
                    protected void stopRMProxy() {
                        return;
                    }
                };
            }
        };
        YarnConfiguration conf = createNMConfigForDistributeNodeLabels();
        conf.set(NM_LOCALIZER_ADDRESS, ("0.0.0.0:" + (ServerSocketUtil.getPort(8040, 10))));
        nm.init(conf);
        resourceTracker.resetNMHeartbeatReceiveFlag();
        nm.start();
        dummyLabelsProviderRef.setDescriptors(toNodeLabelSet("P"));
        resourceTracker.waitTillHeartbeat();// wait till the first heartbeat

        resourceTracker.resetNMHeartbeatReceiveFlag();
        // heartbeat with invalid labels
        dummyLabelsProviderRef.setDescriptors(toNodeLabelSet("_.P"));
        sendOutofBandHeartBeat();
        resourceTracker.waitTillHeartbeat();
        Assert.assertNull(("On Invalid Labels we need to retain earlier labels, HB " + "needs to send null"), resourceTracker.labels);
        resourceTracker.resetNMHeartbeatReceiveFlag();
        // on next heartbeat same invalid labels will be given by the provider, but
        // again label validation check and reset RM with empty labels set should
        // not happen
        sendOutofBandHeartBeat();
        resourceTracker.waitTillHeartbeat();
        Assert.assertNull(("NodeStatusUpdater need not send repeatedly empty labels on " + "invalid labels from provider "), resourceTracker.labels);
        resourceTracker.resetNMHeartbeatReceiveFlag();
    }
}

