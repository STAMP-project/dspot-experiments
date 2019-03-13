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
package org.apache.hadoop.hdfs.server.datanode;


import DataNode.LOG;
import com.google.common.base.Supplier;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.protocolPB.DatanodeLifelineProtocolClientSideTranslatorPB;
import org.apache.hadoop.hdfs.protocolPB.DatanodeProtocolClientSideTranslatorPB;
import org.apache.hadoop.hdfs.server.blockmanagement.DatanodeDescriptor;
import org.apache.hadoop.hdfs.server.datanode.metrics.DataNodeMetrics;
import org.apache.hadoop.hdfs.server.namenode.FSNamesystem;
import org.apache.hadoop.hdfs.server.protocol.DatanodeRegistration;
import org.apache.hadoop.hdfs.server.protocol.SlowDiskReports;
import org.apache.hadoop.hdfs.server.protocol.SlowPeerReports;
import org.apache.hadoop.hdfs.server.protocol.StorageReport;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.test.MetricsAsserts;
import org.apache.log4j.Level;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test suite covering lifeline protocol handling in the DataNode.
 */
public class TestDataNodeLifeline {
    private static final Logger LOG = LoggerFactory.getLogger(TestDataNodeLifeline.class);

    static {
        GenericTestUtils.setLogLevel(DataNode.LOG, Level.ALL);
    }

    @Rule
    public Timeout timeout = new Timeout(60000);

    private MiniDFSCluster cluster;

    private HdfsConfiguration conf;

    private DatanodeLifelineProtocolClientSideTranslatorPB lifelineNamenode;

    private DataNodeMetrics metrics;

    private DatanodeProtocolClientSideTranslatorPB namenode;

    private FSNamesystem namesystem;

    private DataNode dn;

    private BPServiceActor bpsa;

    @Test
    public void testSendLifelineIfHeartbeatBlocked() throws Exception {
        // Run the test for the duration of sending 10 lifeline RPC messages.
        int numLifelines = 10;
        CountDownLatch lifelinesSent = new CountDownLatch(numLifelines);
        // Intercept heartbeat to inject an artificial delay, until all expected
        // lifeline RPC messages have been sent.
        Mockito.doAnswer(new TestDataNodeLifeline.LatchAwaitingAnswer<org.apache.hadoop.hdfs.server.protocol.HeartbeatResponse>(lifelinesSent)).when(namenode).sendHeartbeat(ArgumentMatchers.any(DatanodeRegistration.class), ArgumentMatchers.any(StorageReport[].class), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any(SlowPeerReports.class), ArgumentMatchers.any(SlowDiskReports.class));
        // Intercept lifeline to trigger latch count-down on each call.
        Mockito.doAnswer(new TestDataNodeLifeline.LatchCountingAnswer<Void>(lifelinesSent)).when(lifelineNamenode).sendLifeline(ArgumentMatchers.any(DatanodeRegistration.class), ArgumentMatchers.any(StorageReport[].class), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.any());
        // While waiting on the latch for the expected number of lifeline messages,
        // poll DataNode tracking information.  Thanks to the lifeline, we expect
        // that the DataNode always stays alive, and never goes stale or dead.
        while (!(lifelinesSent.await(1, TimeUnit.SECONDS))) {
            Assert.assertEquals("Expect DataNode to be kept alive by lifeline.", 1, namesystem.getNumLiveDataNodes());
            Assert.assertEquals("Expect DataNode not marked dead due to lifeline.", 0, namesystem.getNumDeadDataNodes());
            Assert.assertEquals("Expect DataNode not marked stale due to lifeline.", 0, namesystem.getNumStaleDataNodes());
            // add a new volume on the next heartbeat
            cluster.getDataNodes().get(0).reconfigurePropertyImpl(DFSConfigKeys.DFS_DATANODE_DATA_DIR_KEY, cluster.getDataDirectory().concat("/data-new"));
        } 
        // Verify that we did in fact call the lifeline RPC.
        Mockito.verify(lifelineNamenode, Mockito.atLeastOnce()).sendLifeline(ArgumentMatchers.any(DatanodeRegistration.class), ArgumentMatchers.any(StorageReport[].class), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.any());
        // Also verify lifeline call through metrics.  We expect at least
        // numLifelines, guaranteed by waiting on the latch.  There is a small
        // possibility of extra lifeline calls depending on timing, so we allow
        // slack in the assertion.
        Assert.assertTrue((("Expect metrics to count at least " + numLifelines) + " calls."), ((MetricsAsserts.getLongCounter("LifelinesNumOps", MetricsAsserts.getMetrics(metrics.name()))) >= numLifelines));
    }

    @Test
    public void testNoLifelineSentIfHeartbeatsOnTime() throws Exception {
        // Run the test for the duration of sending 10 heartbeat RPC messages.
        int numHeartbeats = 10;
        CountDownLatch heartbeatsSent = new CountDownLatch(numHeartbeats);
        // Intercept heartbeat to trigger latch count-down on each call.
        Mockito.doAnswer(new TestDataNodeLifeline.LatchCountingAnswer<org.apache.hadoop.hdfs.server.protocol.HeartbeatResponse>(heartbeatsSent)).when(namenode).sendHeartbeat(ArgumentMatchers.any(DatanodeRegistration.class), ArgumentMatchers.any(StorageReport[].class), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any(SlowPeerReports.class), ArgumentMatchers.any(SlowDiskReports.class));
        // While waiting on the latch for the expected number of heartbeat messages,
        // poll DataNode tracking information.  We expect that the DataNode always
        // stays alive, and never goes stale or dead.
        while (!(heartbeatsSent.await(1, TimeUnit.SECONDS))) {
            Assert.assertEquals("Expect DataNode to be kept alive by lifeline.", 1, namesystem.getNumLiveDataNodes());
            Assert.assertEquals("Expect DataNode not marked dead due to lifeline.", 0, namesystem.getNumDeadDataNodes());
            Assert.assertEquals("Expect DataNode not marked stale due to lifeline.", 0, namesystem.getNumStaleDataNodes());
        } 
        // Verify that we did not call the lifeline RPC.
        Mockito.verify(lifelineNamenode, Mockito.never()).sendLifeline(ArgumentMatchers.any(DatanodeRegistration.class), ArgumentMatchers.any(StorageReport[].class), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.any());
        // Also verify no lifeline calls through metrics.
        Assert.assertEquals("Expect metrics to count no lifeline calls.", 0, MetricsAsserts.getLongCounter("LifelinesNumOps", MetricsAsserts.getMetrics(metrics.name())));
    }

    @Test
    public void testLifelineForDeadNode() throws Exception {
        long initialCapacity = cluster.getNamesystem(0).getCapacityTotal();
        Assert.assertTrue((initialCapacity > 0));
        dn.setHeartbeatsDisabledForTests(true);
        cluster.setDataNodesDead();
        Assert.assertEquals("Capacity should be 0 after all DNs dead", 0, cluster.getNamesystem(0).getCapacityTotal());
        bpsa.sendLifelineForTests();
        Assert.assertEquals("Lifeline should be ignored for dead node", 0, cluster.getNamesystem(0).getCapacityTotal());
        // Wait for re-registration and heartbeat
        dn.setHeartbeatsDisabledForTests(false);
        final DatanodeDescriptor dnDesc = cluster.getNamesystem(0).getBlockManager().getDatanodeManager().getDatanodes().iterator().next();
        GenericTestUtils.waitFor(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                return (dnDesc.isAlive()) && (dnDesc.isHeartbeatedSinceRegistration());
            }
        }, 100, 5000);
        Assert.assertEquals("Capacity should include only live capacity", initialCapacity, cluster.getNamesystem(0).getCapacityTotal());
    }

    /**
     * Waits on a {@link CountDownLatch} before calling through to the method.
     */
    private final class LatchAwaitingAnswer<T> implements Answer<T> {
        private final CountDownLatch latch;

        public LatchAwaitingAnswer(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        @SuppressWarnings("unchecked")
        public T answer(InvocationOnMock invocation) throws Throwable {
            TestDataNodeLifeline.LOG.info("Awaiting, remaining latch count is {}.", latch.getCount());
            latch.await();
            return ((T) (invocation.callRealMethod()));
        }
    }

    /**
     * Counts on a {@link CountDownLatch} after each call through to the method.
     */
    private final class LatchCountingAnswer<T> implements Answer<T> {
        private final CountDownLatch latch;

        public LatchCountingAnswer(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        @SuppressWarnings("unchecked")
        public T answer(InvocationOnMock invocation) throws Throwable {
            T result = ((T) (invocation.callRealMethod()));
            latch.countDown();
            TestDataNodeLifeline.LOG.info("Countdown, remaining latch count is {}.", latch.getCount());
            return result;
        }
    }
}

