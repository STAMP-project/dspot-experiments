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
package org.apache.hadoop.hbase.regionserver;


import CompareOperator.EQUAL;
import Durability.ASYNC_WAL;
import HConstants.NO_NONCE;
import HConstants.REGION_IMPL;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.MultithreadedTestUtil;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Append;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Increment;
import org.apache.hadoop.hbase.client.Mutation;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.RowMutations;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.TableDescriptor;
import org.apache.hadoop.hbase.io.HeapSize;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.VerySlowRegionServerTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.wal.WAL;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Testing of HRegion.incrementColumnValue, HRegion.increment,
 * and HRegion.append
 */
// Starts 100 threads
@Category({ VerySlowRegionServerTests.class, MediumTests.class })
public class TestAtomicOperation {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestAtomicOperation.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestAtomicOperation.class);

    @Rule
    public TestName name = new TestName();

    HRegion region = null;

    private HBaseTestingUtility TEST_UTIL = HBaseTestingUtility.createLocalHTU();

    // Test names
    static byte[] tableName;

    static final byte[] qual1 = Bytes.toBytes("qual1");

    static final byte[] qual2 = Bytes.toBytes("qual2");

    static final byte[] qual3 = Bytes.toBytes("qual3");

    static final byte[] value1 = Bytes.toBytes("value1");

    static final byte[] value2 = Bytes.toBytes("value2");

    static final byte[] row = Bytes.toBytes("rowA");

    static final byte[] row2 = Bytes.toBytes("rowB");

    // ////////////////////////////////////////////////////////////////////////////
    // New tests that doesn't spin up a mini cluster but rather just test the
    // individual code pieces in the HRegion.
    // ////////////////////////////////////////////////////////////////////////////
    /**
     * Test basic append operation.
     * More tests in
     *
     * @see org.apache.hadoop.hbase.client.TestFromClientSide#testAppend()
     */
    @Test
    public void testAppend() throws IOException {
        initHRegion(TestAtomicOperation.tableName, name.getMethodName(), HBaseTestingUtility.fam1);
        String v1 = "Ultimate Answer to the Ultimate Question of Life," + " The Universe, and Everything";
        String v2 = " is... 42.";
        Append a = new Append(TestAtomicOperation.row);
        a.setReturnResults(false);
        a.addColumn(HBaseTestingUtility.fam1, TestAtomicOperation.qual1, Bytes.toBytes(v1));
        a.addColumn(HBaseTestingUtility.fam1, TestAtomicOperation.qual2, Bytes.toBytes(v2));
        Assert.assertTrue(region.append(a, NO_NONCE, NO_NONCE).isEmpty());
        a = new Append(TestAtomicOperation.row);
        a.addColumn(HBaseTestingUtility.fam1, TestAtomicOperation.qual1, Bytes.toBytes(v2));
        a.addColumn(HBaseTestingUtility.fam1, TestAtomicOperation.qual2, Bytes.toBytes(v1));
        Result result = region.append(a, NO_NONCE, NO_NONCE);
        Assert.assertEquals(0, Bytes.compareTo(Bytes.toBytes((v1 + v2)), result.getValue(HBaseTestingUtility.fam1, TestAtomicOperation.qual1)));
        Assert.assertEquals(0, Bytes.compareTo(Bytes.toBytes((v2 + v1)), result.getValue(HBaseTestingUtility.fam1, TestAtomicOperation.qual2)));
    }

    @Test
    public void testAppendWithMultipleFamilies() throws IOException {
        final byte[] fam3 = Bytes.toBytes("colfamily31");
        initHRegion(TestAtomicOperation.tableName, name.getMethodName(), HBaseTestingUtility.fam1, HBaseTestingUtility.fam2, fam3);
        String v1 = "Appended";
        String v2 = "Value";
        Append a = new Append(TestAtomicOperation.row);
        a.setReturnResults(false);
        a.addColumn(HBaseTestingUtility.fam1, TestAtomicOperation.qual1, Bytes.toBytes(v1));
        a.addColumn(HBaseTestingUtility.fam2, TestAtomicOperation.qual2, Bytes.toBytes(v2));
        Result result = region.append(a, NO_NONCE, NO_NONCE);
        Assert.assertTrue((("Expected an empty result but result contains " + (result.size())) + " keys"), result.isEmpty());
        a = new Append(TestAtomicOperation.row);
        a.addColumn(HBaseTestingUtility.fam2, TestAtomicOperation.qual2, Bytes.toBytes(v1));
        a.addColumn(HBaseTestingUtility.fam1, TestAtomicOperation.qual1, Bytes.toBytes(v2));
        a.addColumn(fam3, TestAtomicOperation.qual3, Bytes.toBytes(v2));
        a.addColumn(HBaseTestingUtility.fam1, TestAtomicOperation.qual2, Bytes.toBytes(v1));
        result = region.append(a, NO_NONCE, NO_NONCE);
        byte[] actualValue1 = result.getValue(HBaseTestingUtility.fam1, TestAtomicOperation.qual1);
        byte[] actualValue2 = result.getValue(HBaseTestingUtility.fam2, TestAtomicOperation.qual2);
        byte[] actualValue3 = result.getValue(fam3, TestAtomicOperation.qual3);
        byte[] actualValue4 = result.getValue(HBaseTestingUtility.fam1, TestAtomicOperation.qual2);
        Assert.assertNotNull("Value1 should bot be null", actualValue1);
        Assert.assertNotNull("Value2 should bot be null", actualValue2);
        Assert.assertNotNull("Value3 should bot be null", actualValue3);
        Assert.assertNotNull("Value4 should bot be null", actualValue4);
        Assert.assertEquals(0, Bytes.compareTo(Bytes.toBytes((v1 + v2)), actualValue1));
        Assert.assertEquals(0, Bytes.compareTo(Bytes.toBytes((v2 + v1)), actualValue2));
        Assert.assertEquals(0, Bytes.compareTo(Bytes.toBytes(v2), actualValue3));
        Assert.assertEquals(0, Bytes.compareTo(Bytes.toBytes(v1), actualValue4));
    }

    @Test
    public void testAppendWithNonExistingFamily() throws IOException {
        initHRegion(TestAtomicOperation.tableName, name.getMethodName(), HBaseTestingUtility.fam1);
        final String v1 = "Value";
        final Append a = new Append(TestAtomicOperation.row);
        a.addColumn(HBaseTestingUtility.fam1, TestAtomicOperation.qual1, Bytes.toBytes(v1));
        a.addColumn(HBaseTestingUtility.fam2, TestAtomicOperation.qual2, Bytes.toBytes(v1));
        Result result = null;
        try {
            result = region.append(a, NO_NONCE, NO_NONCE);
            Assert.fail("Append operation should fail with NoSuchColumnFamilyException.");
        } catch (NoSuchColumnFamilyException e) {
            Assert.assertEquals(null, result);
        } catch (Exception e) {
            Assert.fail("Append operation should fail with NoSuchColumnFamilyException.");
        }
    }

    @Test
    public void testIncrementWithNonExistingFamily() throws IOException {
        initHRegion(TestAtomicOperation.tableName, name.getMethodName(), HBaseTestingUtility.fam1);
        final Increment inc = new Increment(TestAtomicOperation.row);
        inc.addColumn(HBaseTestingUtility.fam1, TestAtomicOperation.qual1, 1);
        inc.addColumn(HBaseTestingUtility.fam2, TestAtomicOperation.qual2, 1);
        inc.setDurability(ASYNC_WAL);
        try {
            region.increment(inc, NO_NONCE, NO_NONCE);
        } catch (NoSuchColumnFamilyException e) {
            final Get g = new Get(TestAtomicOperation.row);
            final Result result = region.get(g);
            Assert.assertEquals(null, result.getValue(HBaseTestingUtility.fam1, TestAtomicOperation.qual1));
            Assert.assertEquals(null, result.getValue(HBaseTestingUtility.fam2, TestAtomicOperation.qual2));
        } catch (Exception e) {
            Assert.fail("Increment operation should fail with NoSuchColumnFamilyException.");
        }
    }

    /**
     * Test multi-threaded increments.
     */
    @Test
    public void testIncrementMultiThreads() throws IOException {
        boolean fast = true;
        TestAtomicOperation.LOG.info("Starting test testIncrementMultiThreads");
        // run a with mixed column families (1 and 3 versions)
        initHRegion(TestAtomicOperation.tableName, name.getMethodName(), new int[]{ 1, 3 }, HBaseTestingUtility.fam1, HBaseTestingUtility.fam2);
        // Create 100 threads, each will increment by its own quantity. All 100 threads update the
        // same row over two column families.
        int numThreads = 100;
        int incrementsPerThread = 1000;
        TestAtomicOperation.Incrementer[] all = new TestAtomicOperation.Incrementer[numThreads];
        int expectedTotal = 0;
        // create all threads
        for (int i = 0; i < numThreads; i++) {
            all[i] = new TestAtomicOperation.Incrementer(region, i, i, incrementsPerThread);
            expectedTotal += i * incrementsPerThread;
        }
        // run all threads
        for (int i = 0; i < numThreads; i++) {
            all[i].start();
        }
        // wait for all threads to finish
        for (int i = 0; i < numThreads; i++) {
            try {
                all[i].join();
            } catch (InterruptedException e) {
                TestAtomicOperation.LOG.info("Ignored", e);
            }
        }
        assertICV(TestAtomicOperation.row, HBaseTestingUtility.fam1, TestAtomicOperation.qual1, expectedTotal, fast);
        assertICV(TestAtomicOperation.row, HBaseTestingUtility.fam1, TestAtomicOperation.qual2, (expectedTotal * 2), fast);
        assertICV(TestAtomicOperation.row, HBaseTestingUtility.fam2, TestAtomicOperation.qual3, (expectedTotal * 3), fast);
        TestAtomicOperation.LOG.info(("testIncrementMultiThreads successfully verified that total is " + expectedTotal));
    }

    /**
     * A thread that makes increment calls always on the same row, this.row against two column
     * families on this row.
     */
    public static class Incrementer extends Thread {
        private final Region region;

        private final int numIncrements;

        private final int amount;

        public Incrementer(Region region, int threadNumber, int amount, int numIncrements) {
            super(("Incrementer." + threadNumber));
            this.region = region;
            this.numIncrements = numIncrements;
            this.amount = amount;
            setDaemon(true);
        }

        @Override
        public void run() {
            for (int i = 0; i < (numIncrements); i++) {
                try {
                    Increment inc = new Increment(TestAtomicOperation.row);
                    inc.addColumn(HBaseTestingUtility.fam1, TestAtomicOperation.qual1, amount);
                    inc.addColumn(HBaseTestingUtility.fam1, TestAtomicOperation.qual2, ((amount) * 2));
                    inc.addColumn(HBaseTestingUtility.fam2, TestAtomicOperation.qual3, ((amount) * 3));
                    inc.setDurability(ASYNC_WAL);
                    Result result = region.increment(inc);
                    if (result != null) {
                        Assert.assertEquals(((Bytes.toLong(result.getValue(HBaseTestingUtility.fam1, TestAtomicOperation.qual1))) * 2), Bytes.toLong(result.getValue(HBaseTestingUtility.fam1, TestAtomicOperation.qual2)));
                        Assert.assertTrue(((result.getValue(HBaseTestingUtility.fam2, TestAtomicOperation.qual3)) != null));
                        Assert.assertEquals(((Bytes.toLong(result.getValue(HBaseTestingUtility.fam1, TestAtomicOperation.qual1))) * 3), Bytes.toLong(result.getValue(HBaseTestingUtility.fam2, TestAtomicOperation.qual3)));
                        Assert.assertEquals(((Bytes.toLong(result.getValue(HBaseTestingUtility.fam1, TestAtomicOperation.qual1))) * 2), Bytes.toLong(result.getValue(HBaseTestingUtility.fam1, TestAtomicOperation.qual2)));
                        long fam1Increment = (Bytes.toLong(result.getValue(HBaseTestingUtility.fam1, TestAtomicOperation.qual1))) * 3;
                        long fam2Increment = Bytes.toLong(result.getValue(HBaseTestingUtility.fam2, TestAtomicOperation.qual3));
                        Assert.assertEquals(((("fam1=" + fam1Increment) + ", fam2=") + fam2Increment), fam1Increment, fam2Increment);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Test
    public void testAppendMultiThreads() throws IOException {
        TestAtomicOperation.LOG.info("Starting test testAppendMultiThreads");
        // run a with mixed column families (1 and 3 versions)
        initHRegion(TestAtomicOperation.tableName, name.getMethodName(), new int[]{ 1, 3 }, HBaseTestingUtility.fam1, HBaseTestingUtility.fam2);
        int numThreads = 100;
        int opsPerThread = 100;
        TestAtomicOperation.AtomicOperation[] all = new TestAtomicOperation.AtomicOperation[numThreads];
        final byte[] val = new byte[]{ 1 };
        AtomicInteger failures = new AtomicInteger(0);
        // create all threads
        for (int i = 0; i < numThreads; i++) {
            all[i] = new TestAtomicOperation.AtomicOperation(region, opsPerThread, null, failures) {
                @Override
                public void run() {
                    for (int i = 0; i < (numOps); i++) {
                        try {
                            Append a = new Append(TestAtomicOperation.row);
                            a.addColumn(HBaseTestingUtility.fam1, TestAtomicOperation.qual1, val);
                            a.addColumn(HBaseTestingUtility.fam1, TestAtomicOperation.qual2, val);
                            a.addColumn(HBaseTestingUtility.fam2, TestAtomicOperation.qual3, val);
                            a.setDurability(ASYNC_WAL);
                            region.append(a, NO_NONCE, NO_NONCE);
                            Get g = new Get(TestAtomicOperation.row);
                            Result result = region.get(g);
                            Assert.assertEquals(result.getValue(HBaseTestingUtility.fam1, TestAtomicOperation.qual1).length, result.getValue(HBaseTestingUtility.fam1, TestAtomicOperation.qual2).length);
                            Assert.assertEquals(result.getValue(HBaseTestingUtility.fam1, TestAtomicOperation.qual1).length, result.getValue(HBaseTestingUtility.fam2, TestAtomicOperation.qual3).length);
                        } catch (IOException e) {
                            e.printStackTrace();
                            failures.incrementAndGet();
                            Assert.fail();
                        }
                    }
                }
            };
        }
        // run all threads
        for (int i = 0; i < numThreads; i++) {
            all[i].start();
        }
        // wait for all threads to finish
        for (int i = 0; i < numThreads; i++) {
            try {
                all[i].join();
            } catch (InterruptedException e) {
            }
        }
        Assert.assertEquals(0, failures.get());
        Get g = new Get(TestAtomicOperation.row);
        Result result = region.get(g);
        Assert.assertEquals(10000, result.getValue(HBaseTestingUtility.fam1, TestAtomicOperation.qual1).length);
        Assert.assertEquals(10000, result.getValue(HBaseTestingUtility.fam1, TestAtomicOperation.qual2).length);
        Assert.assertEquals(10000, result.getValue(HBaseTestingUtility.fam2, TestAtomicOperation.qual3).length);
    }

    /**
     * Test multi-threaded row mutations.
     */
    @Test
    public void testRowMutationMultiThreads() throws IOException {
        TestAtomicOperation.LOG.info("Starting test testRowMutationMultiThreads");
        initHRegion(TestAtomicOperation.tableName, name.getMethodName(), HBaseTestingUtility.fam1);
        // create 10 threads, each will alternate between adding and
        // removing a column
        int numThreads = 10;
        int opsPerThread = 250;
        TestAtomicOperation.AtomicOperation[] all = new TestAtomicOperation.AtomicOperation[numThreads];
        AtomicLong timeStamps = new AtomicLong(0);
        AtomicInteger failures = new AtomicInteger(0);
        // create all threads
        for (int i = 0; i < numThreads; i++) {
            all[i] = new TestAtomicOperation.AtomicOperation(region, opsPerThread, timeStamps, failures) {
                @Override
                public void run() {
                    boolean op = true;
                    for (int i = 0; i < (numOps); i++) {
                        try {
                            // throw in some flushes
                            if ((i % 10) == 0) {
                                synchronized(region) {
                                    TestAtomicOperation.LOG.debug("flushing");
                                    region.flush(true);
                                    if ((i % 100) == 0) {
                                        region.compact(false);
                                    }
                                }
                            }
                            long ts = timeStamps.incrementAndGet();
                            RowMutations rm = new RowMutations(TestAtomicOperation.row);
                            if (op) {
                                Put p = new Put(TestAtomicOperation.row, ts);
                                p.addColumn(HBaseTestingUtility.fam1, TestAtomicOperation.qual1, TestAtomicOperation.value1);
                                p.setDurability(ASYNC_WAL);
                                rm.add(p);
                                Delete d = new Delete(TestAtomicOperation.row);
                                d.addColumns(HBaseTestingUtility.fam1, TestAtomicOperation.qual2, ts);
                                d.setDurability(ASYNC_WAL);
                                rm.add(d);
                            } else {
                                Delete d = new Delete(TestAtomicOperation.row);
                                d.addColumns(HBaseTestingUtility.fam1, TestAtomicOperation.qual1, ts);
                                d.setDurability(ASYNC_WAL);
                                rm.add(d);
                                Put p = new Put(TestAtomicOperation.row, ts);
                                p.addColumn(HBaseTestingUtility.fam1, TestAtomicOperation.qual2, TestAtomicOperation.value2);
                                p.setDurability(ASYNC_WAL);
                                rm.add(p);
                            }
                            region.mutateRow(rm);
                            op ^= true;
                            // check: should always see exactly one column
                            Get g = new Get(TestAtomicOperation.row);
                            Result r = region.get(g);
                            if ((r.size()) != 1) {
                                TestAtomicOperation.LOG.debug(Objects.toString(r));
                                failures.incrementAndGet();
                                Assert.fail();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            failures.incrementAndGet();
                            Assert.fail();
                        }
                    }
                }
            };
        }
        // run all threads
        for (int i = 0; i < numThreads; i++) {
            all[i].start();
        }
        // wait for all threads to finish
        for (int i = 0; i < numThreads; i++) {
            try {
                all[i].join();
            } catch (InterruptedException e) {
            }
        }
        Assert.assertEquals(0, failures.get());
    }

    /**
     * Test multi-threaded region mutations.
     */
    @Test
    public void testMultiRowMutationMultiThreads() throws IOException {
        TestAtomicOperation.LOG.info("Starting test testMultiRowMutationMultiThreads");
        initHRegion(TestAtomicOperation.tableName, name.getMethodName(), HBaseTestingUtility.fam1);
        // create 10 threads, each will alternate between adding and
        // removing a column
        int numThreads = 10;
        int opsPerThread = 250;
        TestAtomicOperation.AtomicOperation[] all = new TestAtomicOperation.AtomicOperation[numThreads];
        AtomicLong timeStamps = new AtomicLong(0);
        AtomicInteger failures = new AtomicInteger(0);
        final List<byte[]> rowsToLock = Arrays.asList(TestAtomicOperation.row, TestAtomicOperation.row2);
        // create all threads
        for (int i = 0; i < numThreads; i++) {
            all[i] = new TestAtomicOperation.AtomicOperation(region, opsPerThread, timeStamps, failures) {
                @Override
                public void run() {
                    boolean op = true;
                    for (int i = 0; i < (numOps); i++) {
                        try {
                            // throw in some flushes
                            if ((i % 10) == 0) {
                                synchronized(region) {
                                    TestAtomicOperation.LOG.debug("flushing");
                                    region.flush(true);
                                    if ((i % 100) == 0) {
                                        region.compact(false);
                                    }
                                }
                            }
                            long ts = timeStamps.incrementAndGet();
                            List<Mutation> mrm = new ArrayList<>();
                            if (op) {
                                Put p = new Put(TestAtomicOperation.row2, ts);
                                p.addColumn(HBaseTestingUtility.fam1, TestAtomicOperation.qual1, TestAtomicOperation.value1);
                                p.setDurability(ASYNC_WAL);
                                mrm.add(p);
                                Delete d = new Delete(TestAtomicOperation.row);
                                d.addColumns(HBaseTestingUtility.fam1, TestAtomicOperation.qual1, ts);
                                d.setDurability(ASYNC_WAL);
                                mrm.add(d);
                            } else {
                                Delete d = new Delete(TestAtomicOperation.row2);
                                d.addColumns(HBaseTestingUtility.fam1, TestAtomicOperation.qual1, ts);
                                d.setDurability(ASYNC_WAL);
                                mrm.add(d);
                                Put p = new Put(TestAtomicOperation.row, ts);
                                p.setDurability(ASYNC_WAL);
                                p.addColumn(HBaseTestingUtility.fam1, TestAtomicOperation.qual1, TestAtomicOperation.value2);
                                mrm.add(p);
                            }
                            region.mutateRowsWithLocks(mrm, rowsToLock, NO_NONCE, NO_NONCE);
                            op ^= true;
                            // check: should always see exactly one column
                            Scan s = new Scan(TestAtomicOperation.row);
                            RegionScanner rs = region.getScanner(s);
                            List<Cell> r = new ArrayList<>();
                            while (rs.next(r));
                            rs.close();
                            if ((r.size()) != 1) {
                                TestAtomicOperation.LOG.debug(Objects.toString(r));
                                failures.incrementAndGet();
                                Assert.fail();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            failures.incrementAndGet();
                            Assert.fail();
                        }
                    }
                }
            };
        }
        // run all threads
        for (int i = 0; i < numThreads; i++) {
            all[i].start();
        }
        // wait for all threads to finish
        for (int i = 0; i < numThreads; i++) {
            try {
                all[i].join();
            } catch (InterruptedException e) {
            }
        }
        Assert.assertEquals(0, failures.get());
    }

    public static class AtomicOperation extends Thread {
        protected final HRegion region;

        protected final int numOps;

        protected final AtomicLong timeStamps;

        protected final AtomicInteger failures;

        protected final Random r = new Random();

        public AtomicOperation(HRegion region, int numOps, AtomicLong timeStamps, AtomicInteger failures) {
            this.region = region;
            this.numOps = numOps;
            this.timeStamps = timeStamps;
            this.failures = failures;
        }
    }

    private static CountDownLatch latch = new CountDownLatch(1);

    // completed checkAndPut
    // NOTE: at the end of these steps, the value of the cell should be 50, not 11!
    private enum TestStep {

        INIT,
        // initial put of 10 to set value of the cell
        PUT_STARTED,
        // began doing a put of 50 to cell
        PUT_COMPLETED,
        // put complete (released RowLock, but may not have advanced MVCC).
        CHECKANDPUT_STARTED,
        // began checkAndPut: if 10 -> 11
        CHECKANDPUT_COMPLETED;}

    private static volatile TestAtomicOperation.TestStep testStep = TestAtomicOperation.TestStep.INIT;

    private final String family = "f1";

    /**
     * Test written as a verifier for HBASE-7051, CheckAndPut should properly read
     * MVCC.
     *
     * Moved into TestAtomicOperation from its original location, TestHBase7051
     */
    @Test
    public void testPutAndCheckAndPutInParallel() throws Exception {
        Configuration conf = TEST_UTIL.getConfiguration();
        conf.setClass(REGION_IMPL, TestAtomicOperation.MockHRegion.class, HeapSize.class);
        HTableDescriptor htd = new HTableDescriptor(TableName.valueOf(name.getMethodName())).addFamily(new HColumnDescriptor(family));
        this.region = TEST_UTIL.createLocalHRegion(htd, null, null);
        Put[] puts = new Put[1];
        Put put = new Put(Bytes.toBytes("r1"));
        put.addColumn(Bytes.toBytes(family), Bytes.toBytes("q1"), Bytes.toBytes("10"));
        puts[0] = put;
        region.batchMutate(puts, NO_NONCE, NO_NONCE);
        MultithreadedTestUtil.TestContext ctx = new MultithreadedTestUtil.TestContext(conf);
        ctx.addThread(new TestAtomicOperation.PutThread(ctx, region));
        ctx.addThread(new TestAtomicOperation.CheckAndPutThread(ctx, region));
        ctx.startThreads();
        while ((TestAtomicOperation.testStep) != (TestAtomicOperation.TestStep.CHECKANDPUT_COMPLETED)) {
            Thread.sleep(100);
        } 
        ctx.stop();
        Scan s = new Scan();
        RegionScanner scanner = region.getScanner(s);
        List<Cell> results = new ArrayList<>();
        ScannerContext scannerContext = ScannerContext.newBuilder().setBatchLimit(2).build();
        scanner.next(results, scannerContext);
        for (Cell keyValue : results) {
            Assert.assertEquals("50", Bytes.toString(CellUtil.cloneValue(keyValue)));
        }
    }

    private class PutThread extends MultithreadedTestUtil.TestThread {
        private Region region;

        PutThread(MultithreadedTestUtil.TestContext ctx, Region region) {
            super(ctx);
            this.region = region;
        }

        @Override
        public void doWork() throws Exception {
            Put[] puts = new Put[1];
            Put put = new Put(Bytes.toBytes("r1"));
            put.addColumn(Bytes.toBytes(family), Bytes.toBytes("q1"), Bytes.toBytes("50"));
            puts[0] = put;
            TestAtomicOperation.testStep = TestAtomicOperation.TestStep.PUT_STARTED;
            region.batchMutate(puts);
        }
    }

    private class CheckAndPutThread extends MultithreadedTestUtil.TestThread {
        private Region region;

        CheckAndPutThread(MultithreadedTestUtil.TestContext ctx, Region region) {
            super(ctx);
            this.region = region;
        }

        @Override
        public void doWork() throws Exception {
            Put[] puts = new Put[1];
            Put put = new Put(Bytes.toBytes("r1"));
            put.addColumn(Bytes.toBytes(family), Bytes.toBytes("q1"), Bytes.toBytes("11"));
            puts[0] = put;
            while ((TestAtomicOperation.testStep) != (TestAtomicOperation.TestStep.PUT_COMPLETED)) {
                Thread.sleep(100);
            } 
            TestAtomicOperation.testStep = TestAtomicOperation.TestStep.CHECKANDPUT_STARTED;
            region.checkAndMutate(Bytes.toBytes("r1"), Bytes.toBytes(family), Bytes.toBytes("q1"), EQUAL, new org.apache.hadoop.hbase.filter.BinaryComparator(Bytes.toBytes("10")), put);
            TestAtomicOperation.testStep = TestAtomicOperation.TestStep.CHECKANDPUT_COMPLETED;
        }
    }

    public static class MockHRegion extends HRegion {
        public MockHRegion(Path tableDir, WAL log, FileSystem fs, Configuration conf, final RegionInfo regionInfo, final TableDescriptor htd, RegionServerServices rsServices) {
            super(tableDir, log, fs, conf, regionInfo, htd, rsServices);
        }

        @Override
        public RowLock getRowLockInternal(final byte[] row, boolean readLock, final RowLock prevRowlock) throws IOException {
            if ((TestAtomicOperation.testStep) == (TestAtomicOperation.TestStep.CHECKANDPUT_STARTED)) {
                TestAtomicOperation.latch.countDown();
            }
            return new TestAtomicOperation.MockHRegion.WrappedRowLock(super.getRowLockInternal(row, readLock, null));
        }

        public class WrappedRowLock implements RowLock {
            private final RowLock rowLock;

            private WrappedRowLock(RowLock rowLock) {
                this.rowLock = rowLock;
            }

            @Override
            public void release() {
                if ((TestAtomicOperation.testStep) == (TestAtomicOperation.TestStep.INIT)) {
                    this.rowLock.release();
                    return;
                }
                if ((TestAtomicOperation.testStep) == (TestAtomicOperation.TestStep.PUT_STARTED)) {
                    try {
                        TestAtomicOperation.testStep = TestAtomicOperation.TestStep.PUT_COMPLETED;
                        this.rowLock.release();
                        // put has been written to the memstore and the row lock has been released, but the
                        // MVCC has not been advanced.  Prior to fixing HBASE-7051, the following order of
                        // operations would cause the non-atomicity to show up:
                        // 1) Put releases row lock (where we are now)
                        // 2) CheckAndPut grabs row lock and reads the value prior to the put (10)
                        // because the MVCC has not advanced
                        // 3) Put advances MVCC
                        // So, in order to recreate this order, we wait for the checkAndPut to grab the rowLock
                        // (see below), and then wait some more to give the checkAndPut time to read the old
                        // value.
                        TestAtomicOperation.latch.await();
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                } else
                    if ((TestAtomicOperation.testStep) == (TestAtomicOperation.TestStep.CHECKANDPUT_STARTED)) {
                        this.rowLock.release();
                    }

            }
        }
    }
}

