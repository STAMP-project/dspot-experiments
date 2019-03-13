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
package org.apache.hadoop.hbase.client;


import Consistency.TIMELINE;
import com.codahale.metrics.Counter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.HRegionInfo;
import org.apache.hadoop.hbase.RegionLocations;
import org.apache.hadoop.hbase.TableNotFoundException;
import org.apache.hadoop.hbase.coprocessor.ObserverContext;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessor;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessorEnvironment;
import org.apache.hadoop.hbase.coprocessor.RegionObserver;
import org.apache.hadoop.hbase.regionserver.InternalScanner;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests for region replicas. Sad that we cannot isolate these without bringing up a whole
 * cluster. See {@link org.apache.hadoop.hbase.regionserver.TestRegionServerNoMaster}.
 */
@Category({ MediumTests.class, ClientTests.class })
@SuppressWarnings("deprecation")
public class TestReplicasClient {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestReplicasClient.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestReplicasClient.class);

    private static final int NB_SERVERS = 1;

    private static Table table = null;

    private static final byte[] row = Bytes.toBytes(TestReplicasClient.class.getName());

    private static HRegionInfo hriPrimary;

    private static HRegionInfo hriSecondary;

    private static final HBaseTestingUtility HTU = new HBaseTestingUtility();

    private static final byte[] f = HConstants.CATALOG_FAMILY;

    private static final int REFRESH_PERIOD = 1000;

    /**
     * This copro is used to synchronize the tests.
     */
    public static class SlowMeCopro implements RegionCoprocessor , RegionObserver {
        static final AtomicLong sleepTime = new AtomicLong(0);

        static final AtomicBoolean slowDownNext = new AtomicBoolean(false);

        static final AtomicInteger countOfNext = new AtomicInteger(0);

        private static final AtomicReference<CountDownLatch> primaryCdl = new AtomicReference<>(new CountDownLatch(0));

        private static final AtomicReference<CountDownLatch> secondaryCdl = new AtomicReference<>(new CountDownLatch(0));

        Random r = new Random();

        public SlowMeCopro() {
        }

        @Override
        public Optional<RegionObserver> getRegionObserver() {
            return Optional.of(this);
        }

        @Override
        public void preGetOp(final ObserverContext<RegionCoprocessorEnvironment> e, final Get get, final List<Cell> results) throws IOException {
            slowdownCode(e);
        }

        @Override
        public void preScannerOpen(final ObserverContext<RegionCoprocessorEnvironment> e, final Scan scan) throws IOException {
            slowdownCode(e);
        }

        @Override
        public boolean preScannerNext(final ObserverContext<RegionCoprocessorEnvironment> e, final InternalScanner s, final List<Result> results, final int limit, final boolean hasMore) throws IOException {
            // this will slow down a certain next operation if the conditions are met. The slowness
            // will allow the call to go to a replica
            if (TestReplicasClient.SlowMeCopro.slowDownNext.get()) {
                // have some "next" return successfully from the primary; hence countOfNext checked
                if ((TestReplicasClient.SlowMeCopro.countOfNext.incrementAndGet()) == 2) {
                    TestReplicasClient.SlowMeCopro.sleepTime.set(2000);
                    slowdownCode(e);
                }
            }
            return true;
        }

        private void slowdownCode(final ObserverContext<RegionCoprocessorEnvironment> e) {
            if ((e.getEnvironment().getRegion().getRegionInfo().getReplicaId()) == 0) {
                TestReplicasClient.LOG.info("We're the primary replicas.");
                CountDownLatch latch = TestReplicasClient.SlowMeCopro.getPrimaryCdl().get();
                try {
                    if ((TestReplicasClient.SlowMeCopro.sleepTime.get()) > 0) {
                        TestReplicasClient.LOG.info((("Sleeping for " + (TestReplicasClient.SlowMeCopro.sleepTime.get())) + " ms"));
                        Thread.sleep(TestReplicasClient.SlowMeCopro.sleepTime.get());
                    } else
                        if ((latch.getCount()) > 0) {
                            TestReplicasClient.LOG.info("Waiting for the counterCountDownLatch");
                            latch.await(2, TimeUnit.MINUTES);// To help the tests to finish.

                            if ((latch.getCount()) > 0) {
                                throw new RuntimeException("Can't wait more");
                            }
                        }

                } catch (InterruptedException e1) {
                    TestReplicasClient.LOG.error(e1.toString(), e1);
                }
            } else {
                TestReplicasClient.LOG.info("We're not the primary replicas.");
                CountDownLatch latch = TestReplicasClient.SlowMeCopro.getSecondaryCdl().get();
                try {
                    if ((latch.getCount()) > 0) {
                        TestReplicasClient.LOG.info("Waiting for the secondary counterCountDownLatch");
                        latch.await(2, TimeUnit.MINUTES);// To help the tests to finish.

                        if ((latch.getCount()) > 0) {
                            throw new RuntimeException("Can't wait more");
                        }
                    }
                } catch (InterruptedException e1) {
                    TestReplicasClient.LOG.error(e1.toString(), e1);
                }
            }
        }

        public static AtomicReference<CountDownLatch> getPrimaryCdl() {
            return TestReplicasClient.SlowMeCopro.primaryCdl;
        }

        public static AtomicReference<CountDownLatch> getSecondaryCdl() {
            return TestReplicasClient.SlowMeCopro.secondaryCdl;
        }
    }

    @Test
    public void testUseRegionWithoutReplica() throws Exception {
        byte[] b1 = Bytes.toBytes("testUseRegionWithoutReplica");
        openRegion(TestReplicasClient.hriSecondary);
        TestReplicasClient.SlowMeCopro.getPrimaryCdl().set(new CountDownLatch(0));
        try {
            Get g = new Get(b1);
            Result r = TestReplicasClient.table.get(g);
            Assert.assertFalse(r.isStale());
        } finally {
            closeRegion(TestReplicasClient.hriSecondary);
        }
    }

    @Test
    public void testLocations() throws Exception {
        byte[] b1 = Bytes.toBytes("testLocations");
        openRegion(TestReplicasClient.hriSecondary);
        ClusterConnection hc = ((ClusterConnection) (TestReplicasClient.HTU.getAdmin().getConnection()));
        try {
            hc.clearRegionCache();
            RegionLocations rl = hc.locateRegion(TestReplicasClient.table.getName(), b1, false, false);
            Assert.assertEquals(2, rl.size());
            rl = hc.locateRegion(TestReplicasClient.table.getName(), b1, true, false);
            Assert.assertEquals(2, rl.size());
            hc.clearRegionCache();
            rl = hc.locateRegion(TestReplicasClient.table.getName(), b1, true, false);
            Assert.assertEquals(2, rl.size());
            rl = hc.locateRegion(TestReplicasClient.table.getName(), b1, false, false);
            Assert.assertEquals(2, rl.size());
        } finally {
            closeRegion(TestReplicasClient.hriSecondary);
        }
    }

    @Test
    public void testGetNoResultNoStaleRegionWithReplica() throws Exception {
        byte[] b1 = Bytes.toBytes("testGetNoResultNoStaleRegionWithReplica");
        openRegion(TestReplicasClient.hriSecondary);
        try {
            // A get works and is not stale
            Get g = new Get(b1);
            Result r = TestReplicasClient.table.get(g);
            Assert.assertFalse(r.isStale());
        } finally {
            closeRegion(TestReplicasClient.hriSecondary);
        }
    }

    @Test
    public void testGetNoResultStaleRegionWithReplica() throws Exception {
        byte[] b1 = Bytes.toBytes("testGetNoResultStaleRegionWithReplica");
        openRegion(TestReplicasClient.hriSecondary);
        TestReplicasClient.SlowMeCopro.getPrimaryCdl().set(new CountDownLatch(1));
        try {
            Get g = new Get(b1);
            g.setConsistency(TIMELINE);
            Result r = TestReplicasClient.table.get(g);
            Assert.assertTrue(r.isStale());
        } finally {
            TestReplicasClient.SlowMeCopro.getPrimaryCdl().get().countDown();
            closeRegion(TestReplicasClient.hriSecondary);
        }
    }

    @Test
    public void testGetNoResultNotStaleSleepRegionWithReplica() throws Exception {
        byte[] b1 = Bytes.toBytes("testGetNoResultNotStaleSleepRegionWithReplica");
        openRegion(TestReplicasClient.hriSecondary);
        try {
            // We sleep; but we won't go to the stale region as we don't get the stale by default.
            TestReplicasClient.SlowMeCopro.sleepTime.set(2000);
            Get g = new Get(b1);
            Result r = TestReplicasClient.table.get(g);
            Assert.assertFalse(r.isStale());
        } finally {
            TestReplicasClient.SlowMeCopro.sleepTime.set(0);
            closeRegion(TestReplicasClient.hriSecondary);
        }
    }

    @Test
    public void testFlushTable() throws Exception {
        openRegion(TestReplicasClient.hriSecondary);
        try {
            flushRegion(TestReplicasClient.hriPrimary);
            flushRegion(TestReplicasClient.hriSecondary);
            Put p = new Put(TestReplicasClient.row);
            p.addColumn(TestReplicasClient.f, TestReplicasClient.row, TestReplicasClient.row);
            TestReplicasClient.table.put(p);
            flushRegion(TestReplicasClient.hriPrimary);
            flushRegion(TestReplicasClient.hriSecondary);
        } finally {
            Delete d = new Delete(TestReplicasClient.row);
            TestReplicasClient.table.delete(d);
            closeRegion(TestReplicasClient.hriSecondary);
        }
    }

    @Test
    public void testFlushPrimary() throws Exception {
        openRegion(TestReplicasClient.hriSecondary);
        try {
            flushRegion(TestReplicasClient.hriPrimary);
            Put p = new Put(TestReplicasClient.row);
            p.addColumn(TestReplicasClient.f, TestReplicasClient.row, TestReplicasClient.row);
            TestReplicasClient.table.put(p);
            flushRegion(TestReplicasClient.hriPrimary);
        } finally {
            Delete d = new Delete(TestReplicasClient.row);
            TestReplicasClient.table.delete(d);
            closeRegion(TestReplicasClient.hriSecondary);
        }
    }

    @Test
    public void testFlushSecondary() throws Exception {
        openRegion(TestReplicasClient.hriSecondary);
        try {
            flushRegion(TestReplicasClient.hriSecondary);
            Put p = new Put(TestReplicasClient.row);
            p.addColumn(TestReplicasClient.f, TestReplicasClient.row, TestReplicasClient.row);
            TestReplicasClient.table.put(p);
            flushRegion(TestReplicasClient.hriSecondary);
        } catch (TableNotFoundException expected) {
        } finally {
            Delete d = new Delete(TestReplicasClient.row);
            TestReplicasClient.table.delete(d);
            closeRegion(TestReplicasClient.hriSecondary);
        }
    }

    @Test
    public void testUseRegionWithReplica() throws Exception {
        byte[] b1 = Bytes.toBytes("testUseRegionWithReplica");
        openRegion(TestReplicasClient.hriSecondary);
        try {
            // A simple put works, even if there here a second replica
            Put p = new Put(b1);
            p.addColumn(TestReplicasClient.f, b1, b1);
            TestReplicasClient.table.put(p);
            TestReplicasClient.LOG.info("Put done");
            // A get works and is not stale
            Get g = new Get(b1);
            Result r = TestReplicasClient.table.get(g);
            Assert.assertFalse(r.isStale());
            Assert.assertFalse(r.getColumnCells(TestReplicasClient.f, b1).isEmpty());
            TestReplicasClient.LOG.info("get works and is not stale done");
            // Even if it we have to wait a little on the main region
            TestReplicasClient.SlowMeCopro.sleepTime.set(2000);
            g = new Get(b1);
            r = TestReplicasClient.table.get(g);
            Assert.assertFalse(r.isStale());
            Assert.assertFalse(r.getColumnCells(TestReplicasClient.f, b1).isEmpty());
            TestReplicasClient.SlowMeCopro.sleepTime.set(0);
            TestReplicasClient.LOG.info("sleep and is not stale done");
            // But if we ask for stale we will get it
            TestReplicasClient.SlowMeCopro.getPrimaryCdl().set(new CountDownLatch(1));
            g = new Get(b1);
            g.setConsistency(TIMELINE);
            r = TestReplicasClient.table.get(g);
            Assert.assertTrue(r.isStale());
            Assert.assertTrue(r.getColumnCells(TestReplicasClient.f, b1).isEmpty());
            TestReplicasClient.SlowMeCopro.getPrimaryCdl().get().countDown();
            TestReplicasClient.LOG.info("stale done");
            // exists works and is not stale
            g = new Get(b1);
            g.setCheckExistenceOnly(true);
            r = TestReplicasClient.table.get(g);
            Assert.assertFalse(r.isStale());
            Assert.assertTrue(r.getExists());
            TestReplicasClient.LOG.info("exists not stale done");
            // exists works on stale but don't see the put
            TestReplicasClient.SlowMeCopro.getPrimaryCdl().set(new CountDownLatch(1));
            g = new Get(b1);
            g.setCheckExistenceOnly(true);
            g.setConsistency(TIMELINE);
            r = TestReplicasClient.table.get(g);
            Assert.assertTrue(r.isStale());
            Assert.assertFalse("The secondary has stale data", r.getExists());
            TestReplicasClient.SlowMeCopro.getPrimaryCdl().get().countDown();
            TestReplicasClient.LOG.info("exists stale before flush done");
            flushRegion(TestReplicasClient.hriPrimary);
            flushRegion(TestReplicasClient.hriSecondary);
            TestReplicasClient.LOG.info("flush done");
            Thread.sleep((1000 + ((TestReplicasClient.REFRESH_PERIOD) * 2)));
            // get works and is not stale
            TestReplicasClient.SlowMeCopro.getPrimaryCdl().set(new CountDownLatch(1));
            g = new Get(b1);
            g.setConsistency(TIMELINE);
            r = TestReplicasClient.table.get(g);
            Assert.assertTrue(r.isStale());
            Assert.assertFalse(r.isEmpty());
            TestReplicasClient.SlowMeCopro.getPrimaryCdl().get().countDown();
            TestReplicasClient.LOG.info("stale done");
            // exists works on stale and we see the put after the flush
            TestReplicasClient.SlowMeCopro.getPrimaryCdl().set(new CountDownLatch(1));
            g = new Get(b1);
            g.setCheckExistenceOnly(true);
            g.setConsistency(TIMELINE);
            r = TestReplicasClient.table.get(g);
            Assert.assertTrue(r.isStale());
            Assert.assertTrue(r.getExists());
            TestReplicasClient.SlowMeCopro.getPrimaryCdl().get().countDown();
            TestReplicasClient.LOG.info("exists stale after flush done");
        } finally {
            TestReplicasClient.SlowMeCopro.getPrimaryCdl().get().countDown();
            TestReplicasClient.SlowMeCopro.sleepTime.set(0);
            Delete d = new Delete(b1);
            TestReplicasClient.table.delete(d);
            closeRegion(TestReplicasClient.hriSecondary);
        }
    }

    @Test
    public void testHedgedRead() throws Exception {
        byte[] b1 = Bytes.toBytes("testHedgedRead");
        openRegion(TestReplicasClient.hriSecondary);
        try {
            // A simple put works, even if there here a second replica
            Put p = new Put(b1);
            p.addColumn(TestReplicasClient.f, b1, b1);
            TestReplicasClient.table.put(p);
            TestReplicasClient.LOG.info("Put done");
            // A get works and is not stale
            Get g = new Get(b1);
            Result r = TestReplicasClient.table.get(g);
            Assert.assertFalse(r.isStale());
            Assert.assertFalse(r.getColumnCells(TestReplicasClient.f, b1).isEmpty());
            TestReplicasClient.LOG.info("get works and is not stale done");
            // reset
            ClusterConnection connection = ((ClusterConnection) (TestReplicasClient.HTU.getConnection()));
            Counter hedgedReadOps = connection.getConnectionMetrics().hedgedReadOps;
            Counter hedgedReadWin = connection.getConnectionMetrics().hedgedReadWin;
            hedgedReadOps.dec(hedgedReadOps.getCount());
            hedgedReadWin.dec(hedgedReadWin.getCount());
            // Wait a little on the main region, just enough to happen once hedged read
            // and hedged read did not returned faster
            int primaryCallTimeoutMicroSecond = connection.getConnectionConfiguration().getPrimaryCallTimeoutMicroSecond();
            TestReplicasClient.SlowMeCopro.sleepTime.set(TimeUnit.MICROSECONDS.toMillis(primaryCallTimeoutMicroSecond));
            TestReplicasClient.SlowMeCopro.getSecondaryCdl().set(new CountDownLatch(1));
            g = new Get(b1);
            g.setConsistency(TIMELINE);
            r = TestReplicasClient.table.get(g);
            Assert.assertFalse(r.isStale());
            Assert.assertFalse(r.getColumnCells(TestReplicasClient.f, b1).isEmpty());
            Assert.assertEquals(1, hedgedReadOps.getCount());
            Assert.assertEquals(0, hedgedReadWin.getCount());
            TestReplicasClient.SlowMeCopro.sleepTime.set(0);
            TestReplicasClient.SlowMeCopro.getSecondaryCdl().get().countDown();
            TestReplicasClient.LOG.info("hedged read occurred but not faster");
            // But if we ask for stale we will get it and hedged read returned faster
            TestReplicasClient.SlowMeCopro.getPrimaryCdl().set(new CountDownLatch(1));
            g = new Get(b1);
            g.setConsistency(TIMELINE);
            r = TestReplicasClient.table.get(g);
            Assert.assertTrue(r.isStale());
            Assert.assertTrue(r.getColumnCells(TestReplicasClient.f, b1).isEmpty());
            Assert.assertEquals(2, hedgedReadOps.getCount());
            Assert.assertEquals(1, hedgedReadWin.getCount());
            TestReplicasClient.SlowMeCopro.getPrimaryCdl().get().countDown();
            TestReplicasClient.LOG.info("hedged read occurred and faster");
        } finally {
            TestReplicasClient.SlowMeCopro.getPrimaryCdl().get().countDown();
            TestReplicasClient.SlowMeCopro.getSecondaryCdl().get().countDown();
            TestReplicasClient.SlowMeCopro.sleepTime.set(0);
            Delete d = new Delete(b1);
            TestReplicasClient.table.delete(d);
            closeRegion(TestReplicasClient.hriSecondary);
        }
    }

    @Test
    public void testScanWithReplicas() throws Exception {
        // simple scan
        runMultipleScansOfOneType(false, false);
    }

    @Test
    public void testSmallScanWithReplicas() throws Exception {
        // small scan
        runMultipleScansOfOneType(false, true);
    }

    @Test
    public void testReverseScanWithReplicas() throws Exception {
        // reverse scan
        runMultipleScansOfOneType(true, false);
    }

    @Test
    public void testCancelOfScan() throws Exception {
        openRegion(TestReplicasClient.hriSecondary);
        int NUMROWS = 100;
        try {
            for (int i = 0; i < NUMROWS; i++) {
                byte[] b1 = Bytes.toBytes(("testUseRegionWithReplica" + i));
                Put p = new Put(b1);
                p.addColumn(TestReplicasClient.f, b1, b1);
                TestReplicasClient.table.put(p);
            }
            TestReplicasClient.LOG.debug("PUT done");
            int caching = 20;
            byte[] start;
            start = Bytes.toBytes(("testUseRegionWithReplica" + 0));
            flushRegion(TestReplicasClient.hriPrimary);
            TestReplicasClient.LOG.info("flush done");
            Thread.sleep((1000 + ((TestReplicasClient.REFRESH_PERIOD) * 2)));
            // now make some 'next' calls slow
            TestReplicasClient.SlowMeCopro.slowDownNext.set(true);
            TestReplicasClient.SlowMeCopro.countOfNext.set(0);
            TestReplicasClient.SlowMeCopro.sleepTime.set(5000);
            Scan scan = new Scan(start);
            scan.setCaching(caching);
            scan.setConsistency(TIMELINE);
            ResultScanner scanner = TestReplicasClient.table.getScanner(scan);
            Iterator<Result> iter = scanner.iterator();
            iter.next();
            Assert.assertTrue(isAnyRPCcancelled());
            TestReplicasClient.SlowMeCopro.slowDownNext.set(false);
            TestReplicasClient.SlowMeCopro.countOfNext.set(0);
        } finally {
            TestReplicasClient.SlowMeCopro.getPrimaryCdl().get().countDown();
            TestReplicasClient.SlowMeCopro.sleepTime.set(0);
            TestReplicasClient.SlowMeCopro.slowDownNext.set(false);
            TestReplicasClient.SlowMeCopro.countOfNext.set(0);
            for (int i = 0; i < NUMROWS; i++) {
                byte[] b1 = Bytes.toBytes(("testUseRegionWithReplica" + i));
                Delete d = new Delete(b1);
                TestReplicasClient.table.delete(d);
            }
            closeRegion(TestReplicasClient.hriSecondary);
        }
    }
}

