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
package org.apache.hadoop.hbase.replication;


import BaseReplicationEndpoint.REPLICATION_WALENTRYFILTER_CONFIG_KEY;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.Waiter;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.regionserver.HRegion;
import org.apache.hadoop.hbase.replication.regionserver.HBaseInterClusterReplicationEndpoint;
import org.apache.hadoop.hbase.replication.regionserver.MetricsReplicationSourceImpl;
import org.apache.hadoop.hbase.replication.regionserver.MetricsReplicationSourceSource;
import org.apache.hadoop.hbase.replication.regionserver.MetricsSource;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.ReplicationTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.Threads;
import org.apache.hadoop.hbase.wal.WAL.Entry;
import org.apache.hadoop.hbase.zookeeper.ZKConfig;
import org.apache.hadoop.metrics2.lib.DynamicMetricsRegistry;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests ReplicationSource and ReplicationEndpoint interactions
 */
@Category({ ReplicationTests.class, MediumTests.class })
public class TestReplicationEndpoint extends TestReplicationBase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestReplicationEndpoint.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestReplicationEndpoint.class);

    static int numRegionServers;

    @Test
    public void testCustomReplicationEndpoint() throws Exception {
        // test installing a custom replication endpoint other than the default one.
        TestReplicationBase.admin.addPeer("testCustomReplicationEndpoint", new ReplicationPeerConfig().setClusterKey(ZKConfig.getZooKeeperClusterKey(TestReplicationBase.conf1)).setReplicationEndpointImpl(TestReplicationEndpoint.ReplicationEndpointForTest.class.getName()), null);
        // check whether the class has been constructed and started
        Waiter.waitFor(TestReplicationBase.conf1, 60000, new Waiter.Predicate<Exception>() {
            @Override
            public boolean evaluate() throws Exception {
                return (ReplicationEndpointForTest.contructedCount.get()) >= (TestReplicationEndpoint.numRegionServers);
            }
        });
        Waiter.waitFor(TestReplicationBase.conf1, 60000, new Waiter.Predicate<Exception>() {
            @Override
            public boolean evaluate() throws Exception {
                return (ReplicationEndpointForTest.startedCount.get()) >= (TestReplicationEndpoint.numRegionServers);
            }
        });
        Assert.assertEquals(0, TestReplicationEndpoint.ReplicationEndpointForTest.replicateCount.get());
        // now replicate some data.
        doPut(Bytes.toBytes("row42"));
        Waiter.waitFor(TestReplicationBase.conf1, 60000, new Waiter.Predicate<Exception>() {
            @Override
            public boolean evaluate() throws Exception {
                return (ReplicationEndpointForTest.replicateCount.get()) >= 1;
            }
        });
        TestReplicationEndpoint.doAssert(Bytes.toBytes("row42"));
        TestReplicationBase.admin.removePeer("testCustomReplicationEndpoint");
    }

    @Test
    public void testReplicationEndpointReturnsFalseOnReplicate() throws Exception {
        Assert.assertEquals(0, TestReplicationEndpoint.ReplicationEndpointForTest.replicateCount.get());
        Assert.assertTrue((!(TestReplicationEndpoint.ReplicationEndpointReturningFalse.replicated.get())));
        int peerCount = TestReplicationBase.admin.getPeersCount();
        final String id = "testReplicationEndpointReturnsFalseOnReplicate";
        TestReplicationBase.admin.addPeer(id, new ReplicationPeerConfig().setClusterKey(ZKConfig.getZooKeeperClusterKey(TestReplicationBase.conf1)).setReplicationEndpointImpl(TestReplicationEndpoint.ReplicationEndpointReturningFalse.class.getName()), null);
        // This test is flakey and then there is so much stuff flying around in here its, hard to
        // debug.  Peer needs to be up for the edit to make it across. This wait on
        // peer count seems to be a hack that has us not progress till peer is up.
        if ((TestReplicationBase.admin.getPeersCount()) <= peerCount) {
            TestReplicationEndpoint.LOG.info(("Waiting on peercount to go up from " + peerCount));
            Threads.sleep(100);
        }
        // now replicate some data
        doPut(TestReplicationBase.row);
        Waiter.waitFor(TestReplicationBase.conf1, 60000, new Waiter.Predicate<Exception>() {
            @Override
            public boolean evaluate() throws Exception {
                // Looks like replication endpoint returns false unless we put more than 10 edits. We
                // only send over one edit.
                int count = ReplicationEndpointForTest.replicateCount.get();
                TestReplicationEndpoint.LOG.info(("count=" + count));
                return ReplicationEndpointReturningFalse.replicated.get();
            }
        });
        if ((TestReplicationEndpoint.ReplicationEndpointReturningFalse.ex.get()) != null) {
            throw TestReplicationEndpoint.ReplicationEndpointReturningFalse.ex.get();
        }
        TestReplicationBase.admin.removePeer("testReplicationEndpointReturnsFalseOnReplicate");
    }

    @Test
    public void testInterClusterReplication() throws Exception {
        final String id = "testInterClusterReplication";
        List<HRegion> regions = TestReplicationBase.utility1.getHBaseCluster().getRegions(TestReplicationBase.tableName);
        int totEdits = 0;
        // Make sure edits are spread across regions because we do region based batching
        // before shipping edits.
        for (HRegion region : regions) {
            RegionInfo hri = region.getRegionInfo();
            byte[] row = hri.getStartKey();
            for (int i = 0; i < 100; i++) {
                if ((row.length) > 0) {
                    Put put = new Put(row);
                    put.addColumn(TestReplicationBase.famName, row, row);
                    region.put(put);
                    totEdits++;
                }
            }
        }
        TestReplicationBase.admin.addPeer(id, new ReplicationPeerConfig().setClusterKey(ZKConfig.getZooKeeperClusterKey(TestReplicationBase.conf2)).setReplicationEndpointImpl(TestReplicationEndpoint.InterClusterReplicationEndpointForTest.class.getName()), null);
        final int numEdits = totEdits;
        Waiter.waitFor(TestReplicationBase.conf1, 30000, new Waiter.ExplainingPredicate<Exception>() {
            @Override
            public boolean evaluate() throws Exception {
                return (InterClusterReplicationEndpointForTest.replicateCount.get()) == numEdits;
            }

            @Override
            public String explainFailure() throws Exception {
                String failure = (("Failed to replicate all edits, expected = " + numEdits) + " replicated = ") + (InterClusterReplicationEndpointForTest.replicateCount.get());
                return failure;
            }
        });
        TestReplicationBase.admin.removePeer("testInterClusterReplication");
        TestReplicationBase.utility1.deleteTableData(TestReplicationBase.tableName);
    }

    @Test
    public void testWALEntryFilterFromReplicationEndpoint() throws Exception {
        ReplicationPeerConfig rpc = new ReplicationPeerConfig().setClusterKey(ZKConfig.getZooKeeperClusterKey(TestReplicationBase.conf1)).setReplicationEndpointImpl(TestReplicationEndpoint.ReplicationEndpointWithWALEntryFilter.class.getName());
        // test that we can create mutliple WALFilters reflectively
        rpc.getConfiguration().put(REPLICATION_WALENTRYFILTER_CONFIG_KEY, (((TestReplicationEndpoint.EverythingPassesWALEntryFilter.class.getName()) + ",") + (TestReplicationEndpoint.EverythingPassesWALEntryFilterSubclass.class.getName())));
        TestReplicationBase.admin.addPeer("testWALEntryFilterFromReplicationEndpoint", rpc);
        // now replicate some data.
        try (Connection connection = ConnectionFactory.createConnection(TestReplicationBase.conf1)) {
            doPut(connection, Bytes.toBytes("row1"));
            doPut(connection, TestReplicationBase.row);
            doPut(connection, Bytes.toBytes("row2"));
        }
        Waiter.waitFor(TestReplicationBase.conf1, 60000, new Waiter.Predicate<Exception>() {
            @Override
            public boolean evaluate() throws Exception {
                return (ReplicationEndpointForTest.replicateCount.get()) >= 1;
            }
        });
        Assert.assertNull(TestReplicationEndpoint.ReplicationEndpointWithWALEntryFilter.ex.get());
        // make sure our reflectively created filter is in the filter chain
        Assert.assertTrue(TestReplicationEndpoint.EverythingPassesWALEntryFilter.hasPassedAnEntry());
        TestReplicationBase.admin.removePeer("testWALEntryFilterFromReplicationEndpoint");
    }

    @Test(expected = IOException.class)
    public void testWALEntryFilterAddValidation() throws Exception {
        ReplicationPeerConfig rpc = new ReplicationPeerConfig().setClusterKey(ZKConfig.getZooKeeperClusterKey(TestReplicationBase.conf1)).setReplicationEndpointImpl(TestReplicationEndpoint.ReplicationEndpointWithWALEntryFilter.class.getName());
        // test that we can create mutliple WALFilters reflectively
        rpc.getConfiguration().put(REPLICATION_WALENTRYFILTER_CONFIG_KEY, "IAmNotARealWalEntryFilter");
        TestReplicationBase.admin.addPeer("testWALEntryFilterAddValidation", rpc);
    }

    @Test(expected = IOException.class)
    public void testWALEntryFilterUpdateValidation() throws Exception {
        ReplicationPeerConfig rpc = new ReplicationPeerConfig().setClusterKey(ZKConfig.getZooKeeperClusterKey(TestReplicationBase.conf1)).setReplicationEndpointImpl(TestReplicationEndpoint.ReplicationEndpointWithWALEntryFilter.class.getName());
        // test that we can create mutliple WALFilters reflectively
        rpc.getConfiguration().put(REPLICATION_WALENTRYFILTER_CONFIG_KEY, "IAmNotARealWalEntryFilter");
        TestReplicationBase.admin.updatePeerConfig("testWALEntryFilterUpdateValidation", rpc);
    }

    @Test
    public void testMetricsSourceBaseSourcePassthrough() {
        /* The replication MetricsSource wraps a MetricsReplicationSourceSourceImpl
        and a MetricsReplicationGlobalSourceSource, so that metrics get written to both namespaces.
        Both of those classes wrap a MetricsReplicationSourceImpl that implements BaseSource, which
        allows for custom JMX metrics.
        This test checks to make sure the BaseSource decorator logic on MetricsSource actually calls down through
        the two layers of wrapping to the actual BaseSource.
         */
        String id = "id";
        DynamicMetricsRegistry mockRegistry = Mockito.mock(DynamicMetricsRegistry.class);
        MetricsReplicationSourceImpl singleRms = Mockito.mock(MetricsReplicationSourceImpl.class);
        Mockito.when(singleRms.getMetricsRegistry()).thenReturn(mockRegistry);
        MetricsReplicationSourceImpl globalRms = Mockito.mock(MetricsReplicationSourceImpl.class);
        Mockito.when(globalRms.getMetricsRegistry()).thenReturn(mockRegistry);
        MetricsReplicationSourceSource singleSourceSource = new org.apache.hadoop.hbase.replication.regionserver.MetricsReplicationSourceSourceImpl(singleRms, id);
        MetricsReplicationSourceSource globalSourceSource = new org.apache.hadoop.hbase.replication.regionserver.MetricsReplicationGlobalSourceSource(globalRms);
        MetricsReplicationSourceSource spyglobalSourceSource = Mockito.spy(globalSourceSource);
        Mockito.doNothing().when(spyglobalSourceSource).incrFailedRecoveryQueue();
        Map<String, MetricsReplicationSourceSource> singleSourceSourceByTable = new HashMap<>();
        MetricsSource source = new MetricsSource(id, singleSourceSource, spyglobalSourceSource, singleSourceSourceByTable);
        String gaugeName = "gauge";
        String singleGaugeName = "source.id." + gaugeName;
        String globalGaugeName = "source." + gaugeName;
        long delta = 1;
        String counterName = "counter";
        String singleCounterName = "source.id." + counterName;
        String globalCounterName = "source." + counterName;
        long count = 2;
        source.decGauge(gaugeName, delta);
        source.getMetricsContext();
        source.getMetricsDescription();
        source.getMetricsJmxContext();
        source.getMetricsName();
        source.incCounters(counterName, count);
        source.incGauge(gaugeName, delta);
        source.init();
        source.removeMetric(gaugeName);
        source.setGauge(gaugeName, delta);
        source.updateHistogram(counterName, count);
        source.incrFailedRecoveryQueue();
        Mockito.verify(singleRms).decGauge(singleGaugeName, delta);
        Mockito.verify(globalRms).decGauge(globalGaugeName, delta);
        Mockito.verify(globalRms).getMetricsContext();
        Mockito.verify(globalRms).getMetricsJmxContext();
        Mockito.verify(globalRms).getMetricsName();
        Mockito.verify(singleRms).incCounters(singleCounterName, count);
        Mockito.verify(globalRms).incCounters(globalCounterName, count);
        Mockito.verify(singleRms).incGauge(singleGaugeName, delta);
        Mockito.verify(globalRms).incGauge(globalGaugeName, delta);
        Mockito.verify(globalRms).init();
        Mockito.verify(singleRms).removeMetric(singleGaugeName);
        Mockito.verify(globalRms).removeMetric(globalGaugeName);
        Mockito.verify(singleRms).setGauge(singleGaugeName, delta);
        Mockito.verify(globalRms).setGauge(globalGaugeName, delta);
        Mockito.verify(singleRms).updateHistogram(singleCounterName, count);
        Mockito.verify(globalRms).updateHistogram(globalCounterName, count);
        Mockito.verify(spyglobalSourceSource).incrFailedRecoveryQueue();
        // check singleSourceSourceByTable metrics.
        // singleSourceSourceByTable map entry will be created only
        // after calling #setAgeOfLastShippedOpByTable
        boolean containsRandomNewTable = source.getSingleSourceSourceByTable().containsKey("RandomNewTable");
        Assert.assertEquals(false, containsRandomNewTable);
        source.setAgeOfLastShippedOpByTable(123L, "RandomNewTable");
        containsRandomNewTable = source.getSingleSourceSourceByTable().containsKey("RandomNewTable");
        Assert.assertEquals(true, containsRandomNewTable);
        MetricsReplicationSourceSource msr = source.getSingleSourceSourceByTable().get("RandomNewTable");
        // cannot put more concreate value here to verify because the age is arbitrary.
        // as long as it's greater than 0, we see it as correct answer.
        Assert.assertTrue(((msr.getLastShippedAge()) > 0));
    }

    public static class ReplicationEndpointForTest extends BaseReplicationEndpoint {
        static UUID uuid = getRandomUUID();

        static AtomicInteger contructedCount = new AtomicInteger();

        static AtomicInteger startedCount = new AtomicInteger();

        static AtomicInteger stoppedCount = new AtomicInteger();

        static AtomicInteger replicateCount = new AtomicInteger();

        static volatile List<Entry> lastEntries = null;

        public ReplicationEndpointForTest() {
            TestReplicationEndpoint.ReplicationEndpointForTest.replicateCount.set(0);
            TestReplicationEndpoint.ReplicationEndpointForTest.contructedCount.incrementAndGet();
        }

        @Override
        public UUID getPeerUUID() {
            return TestReplicationEndpoint.ReplicationEndpointForTest.uuid;
        }

        @Override
        public boolean replicate(ReplicateContext replicateContext) {
            TestReplicationEndpoint.ReplicationEndpointForTest.replicateCount.incrementAndGet();
            TestReplicationEndpoint.ReplicationEndpointForTest.lastEntries = new ArrayList(replicateContext.entries);
            return true;
        }

        @Override
        public void start() {
            startAsync();
        }

        @Override
        public void stop() {
            stopAsync();
        }

        @Override
        protected void doStart() {
            TestReplicationEndpoint.ReplicationEndpointForTest.startedCount.incrementAndGet();
            notifyStarted();
        }

        @Override
        protected void doStop() {
            TestReplicationEndpoint.ReplicationEndpointForTest.stoppedCount.incrementAndGet();
            notifyStopped();
        }
    }

    public static class InterClusterReplicationEndpointForTest extends HBaseInterClusterReplicationEndpoint {
        static AtomicInteger replicateCount = new AtomicInteger();

        static boolean failedOnce;

        public InterClusterReplicationEndpointForTest() {
            TestReplicationEndpoint.InterClusterReplicationEndpointForTest.replicateCount.set(0);
        }

        @Override
        public boolean replicate(ReplicateContext replicateContext) {
            boolean success = super.replicate(replicateContext);
            if (success) {
                TestReplicationEndpoint.InterClusterReplicationEndpointForTest.replicateCount.addAndGet(replicateContext.entries.size());
            }
            return success;
        }

        @Override
        protected Callable<Integer> createReplicator(List<Entry> entries, int ordinal) {
            // Fail only once, we don't want to slow down the test.
            if (TestReplicationEndpoint.InterClusterReplicationEndpointForTest.failedOnce) {
                return () -> ordinal;
            } else {
                TestReplicationEndpoint.InterClusterReplicationEndpointForTest.failedOnce = true;
                return () -> {
                    throw new IOException("Sample Exception: Failed to replicate.");
                };
            }
        }
    }

    public static class ReplicationEndpointReturningFalse extends TestReplicationEndpoint.ReplicationEndpointForTest {
        static int COUNT = 10;

        static AtomicReference<Exception> ex = new AtomicReference<>(null);

        static AtomicBoolean replicated = new AtomicBoolean(false);

        @Override
        public boolean replicate(ReplicateContext replicateContext) {
            try {
                // check row
                TestReplicationEndpoint.doAssert(TestReplicationBase.row);
            } catch (Exception e) {
                TestReplicationEndpoint.ReplicationEndpointReturningFalse.ex.set(e);
            }
            super.replicate(replicateContext);
            TestReplicationEndpoint.LOG.info(((("Replicated " + (Bytes.toString(TestReplicationBase.row))) + ", count=") + (TestReplicationEndpoint.ReplicationEndpointForTest.replicateCount.get())));
            TestReplicationEndpoint.ReplicationEndpointReturningFalse.replicated.set(((TestReplicationEndpoint.ReplicationEndpointForTest.replicateCount.get()) > (TestReplicationEndpoint.ReplicationEndpointReturningFalse.COUNT)));// first 10 times, we return false

            return TestReplicationEndpoint.ReplicationEndpointReturningFalse.replicated.get();
        }
    }

    // return a WALEntry filter which only accepts "row", but not other rows
    public static class ReplicationEndpointWithWALEntryFilter extends TestReplicationEndpoint.ReplicationEndpointForTest {
        static AtomicReference<Exception> ex = new AtomicReference<>(null);

        @Override
        public boolean replicate(ReplicateContext replicateContext) {
            try {
                super.replicate(replicateContext);
                TestReplicationEndpoint.doAssert(TestReplicationBase.row);
            } catch (Exception e) {
                TestReplicationEndpoint.ReplicationEndpointWithWALEntryFilter.ex.set(e);
            }
            return true;
        }

        @Override
        public WALEntryFilter getWALEntryfilter() {
            return new ChainWALEntryFilter(super.getWALEntryfilter(), new WALEntryFilter() {
                @Override
                public Entry filter(Entry entry) {
                    ArrayList<Cell> cells = entry.getEdit().getCells();
                    int size = cells.size();
                    for (int i = size - 1; i >= 0; i--) {
                        Cell cell = cells.get(i);
                        if (!(Bytes.equals(cell.getRowArray(), cell.getRowOffset(), cell.getRowLength(), TestReplicationBase.row, 0, TestReplicationEndpoint.row.length))) {
                            cells.remove(i);
                        }
                    }
                    return entry;
                }
            });
        }
    }

    public static class EverythingPassesWALEntryFilter implements WALEntryFilter {
        private static boolean passedEntry = false;

        @Override
        public Entry filter(Entry entry) {
            TestReplicationEndpoint.EverythingPassesWALEntryFilter.passedEntry = true;
            return entry;
        }

        public static boolean hasPassedAnEntry() {
            return TestReplicationEndpoint.EverythingPassesWALEntryFilter.passedEntry;
        }
    }

    public static class EverythingPassesWALEntryFilterSubclass extends TestReplicationEndpoint.EverythingPassesWALEntryFilter {}
}

