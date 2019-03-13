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


import JVMClusterUtil.RegionServerThread;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.CoprocessorEnvironment;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HRegionLocation;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.coprocessor.MasterCoprocessor;
import org.apache.hadoop.hbase.coprocessor.MasterCoprocessorEnvironment;
import org.apache.hadoop.hbase.coprocessor.MasterObserver;
import org.apache.hadoop.hbase.coprocessor.ObserverContext;
import org.apache.hadoop.hbase.master.RegionPlan;
import org.apache.hadoop.hbase.testclassification.FlakeyTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.JVMClusterUtil;
import org.apache.hadoop.hbase.util.Threads;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ MediumTests.class, FlakeyTests.class })
public class TestMultiParallel {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestMultiParallel.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestMultiParallel.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static final byte[] VALUE = Bytes.toBytes("value");

    private static final byte[] QUALIFIER = Bytes.toBytes("qual");

    private static final String FAMILY = "family";

    private static final TableName TEST_TABLE = TableName.valueOf("multi_test_table");

    private static final byte[] BYTES_FAMILY = Bytes.toBytes(TestMultiParallel.FAMILY);

    private static final byte[] ONE_ROW = Bytes.toBytes("xxx");

    private static final byte[][] KEYS = TestMultiParallel.makeKeys();

    private static final int slaves = 5;// also used for testing HTable pool size


    private static Connection CONNECTION;

    /**
     * This is for testing the active number of threads that were used while
     * doing a batch operation. It inserts one row per region via the batch
     * operation, and then checks the number of active threads.
     * <p/>
     * For HBASE-3553
     */
    @Test
    public void testActiveThreadsCount() throws Exception {
        TestMultiParallel.UTIL.getConfiguration().setLong("hbase.htable.threads.coresize", ((TestMultiParallel.slaves) + 1));
        try (Connection connection = ConnectionFactory.createConnection(TestMultiParallel.UTIL.getConfiguration())) {
            ThreadPoolExecutor executor = HTable.getDefaultExecutor(TestMultiParallel.UTIL.getConfiguration());
            try {
                try (Table t = connection.getTable(TestMultiParallel.TEST_TABLE, executor)) {
                    List<Put> puts = constructPutRequests();// creates a Put for every region

                    t.batch(puts, null);
                    HashSet<ServerName> regionservers = new HashSet<>();
                    try (RegionLocator locator = connection.getRegionLocator(TestMultiParallel.TEST_TABLE)) {
                        for (Row r : puts) {
                            HRegionLocation location = locator.getRegionLocation(r.getRow());
                            regionservers.add(location.getServerName());
                        }
                    }
                    Assert.assertEquals(regionservers.size(), executor.getLargestPoolSize());
                }
            } finally {
                executor.shutdownNow();
            }
        }
    }

    @Test
    public void testBatchWithGet() throws Exception {
        TestMultiParallel.LOG.info("test=testBatchWithGet");
        Table table = TestMultiParallel.UTIL.getConnection().getTable(TestMultiParallel.TEST_TABLE);
        // load test data
        List<Put> puts = constructPutRequests();
        table.batch(puts, null);
        // create a list of gets and run it
        List<Row> gets = new ArrayList<>();
        for (byte[] k : TestMultiParallel.KEYS) {
            Get get = new Get(k);
            get.addColumn(TestMultiParallel.BYTES_FAMILY, TestMultiParallel.QUALIFIER);
            gets.add(get);
        }
        Result[] multiRes = new Result[gets.size()];
        table.batch(gets, multiRes);
        // Same gets using individual call API
        List<Result> singleRes = new ArrayList<>();
        for (Row get : gets) {
            singleRes.add(table.get(((Get) (get))));
        }
        // Compare results
        Assert.assertEquals(singleRes.size(), multiRes.length);
        for (int i = 0; i < (singleRes.size()); i++) {
            Assert.assertTrue(singleRes.get(i).containsColumn(TestMultiParallel.BYTES_FAMILY, TestMultiParallel.QUALIFIER));
            Cell[] singleKvs = singleRes.get(i).rawCells();
            Cell[] multiKvs = multiRes[i].rawCells();
            for (int j = 0; j < (singleKvs.length); j++) {
                Assert.assertEquals(singleKvs[j], multiKvs[j]);
                Assert.assertEquals(0, Bytes.compareTo(CellUtil.cloneValue(singleKvs[j]), CellUtil.cloneValue(multiKvs[j])));
            }
        }
        table.close();
    }

    @Test
    public void testBadFam() throws Exception {
        TestMultiParallel.LOG.info("test=testBadFam");
        Table table = TestMultiParallel.UTIL.getConnection().getTable(TestMultiParallel.TEST_TABLE);
        List<Row> actions = new ArrayList<>();
        Put p = new Put(Bytes.toBytes("row1"));
        p.addColumn(Bytes.toBytes("bad_family"), Bytes.toBytes("qual"), Bytes.toBytes("value"));
        actions.add(p);
        p = new Put(Bytes.toBytes("row2"));
        p.addColumn(TestMultiParallel.BYTES_FAMILY, Bytes.toBytes("qual"), Bytes.toBytes("value"));
        actions.add(p);
        // row1 and row2 should be in the same region.
        Object[] r = new Object[actions.size()];
        try {
            table.batch(actions, r);
            Assert.fail();
        } catch (RetriesExhaustedWithDetailsException ex) {
            TestMultiParallel.LOG.debug(ex.toString(), ex);
            // good!
            Assert.assertFalse(ex.mayHaveClusterIssues());
        }
        Assert.assertEquals(2, r.length);
        Assert.assertTrue(((r[0]) instanceof Throwable));
        Assert.assertTrue(((r[1]) instanceof Result));
        table.close();
    }

    @Test
    public void testFlushCommitsNoAbort() throws Exception {
        TestMultiParallel.LOG.info("test=testFlushCommitsNoAbort");
        doTestFlushCommits(false);
    }

    /**
     * Only run one Multi test with a forced RegionServer abort. Otherwise, the
     * unit tests will take an unnecessarily long time to run.
     */
    @Test
    public void testFlushCommitsWithAbort() throws Exception {
        TestMultiParallel.LOG.info("test=testFlushCommitsWithAbort");
        doTestFlushCommits(true);
    }

    @Test
    public void testBatchWithPut() throws Exception {
        TestMultiParallel.LOG.info("test=testBatchWithPut");
        Table table = TestMultiParallel.CONNECTION.getTable(TestMultiParallel.TEST_TABLE);
        // put multiple rows using a batch
        List<Put> puts = constructPutRequests();
        Object[] results = new Object[puts.size()];
        table.batch(puts, results);
        validateSizeAndEmpty(results, TestMultiParallel.KEYS.length);
        if (true) {
            int liveRScount = TestMultiParallel.UTIL.getMiniHBaseCluster().getLiveRegionServerThreads().size();
            assert liveRScount > 0;
            JVMClusterUtil.RegionServerThread liveRS = TestMultiParallel.UTIL.getMiniHBaseCluster().getLiveRegionServerThreads().get(0);
            liveRS.getRegionServer().abort("Aborting for tests", new Exception("testBatchWithPut"));
            puts = constructPutRequests();
            try {
                results = new Object[puts.size()];
                table.batch(puts, results);
            } catch (RetriesExhaustedWithDetailsException ree) {
                TestMultiParallel.LOG.info(ree.getExhaustiveDescription());
                table.close();
                throw ree;
            }
            validateSizeAndEmpty(results, TestMultiParallel.KEYS.length);
        }
        validateLoadedData(table);
        table.close();
    }

    @Test
    public void testBatchWithDelete() throws Exception {
        TestMultiParallel.LOG.info("test=testBatchWithDelete");
        Table table = TestMultiParallel.UTIL.getConnection().getTable(TestMultiParallel.TEST_TABLE);
        // Load some data
        List<Put> puts = constructPutRequests();
        Object[] results = new Object[puts.size()];
        table.batch(puts, results);
        validateSizeAndEmpty(results, TestMultiParallel.KEYS.length);
        // Deletes
        List<Row> deletes = new ArrayList<>();
        for (int i = 0; i < (TestMultiParallel.KEYS.length); i++) {
            Delete delete = new Delete(TestMultiParallel.KEYS[i]);
            delete.addFamily(TestMultiParallel.BYTES_FAMILY);
            deletes.add(delete);
        }
        results = new Object[deletes.size()];
        table.batch(deletes, results);
        validateSizeAndEmpty(results, TestMultiParallel.KEYS.length);
        // Get to make sure ...
        for (byte[] k : TestMultiParallel.KEYS) {
            Get get = new Get(k);
            get.addColumn(TestMultiParallel.BYTES_FAMILY, TestMultiParallel.QUALIFIER);
            Assert.assertFalse(table.exists(get));
        }
        table.close();
    }

    @Test
    public void testHTableDeleteWithList() throws Exception {
        TestMultiParallel.LOG.info("test=testHTableDeleteWithList");
        Table table = TestMultiParallel.UTIL.getConnection().getTable(TestMultiParallel.TEST_TABLE);
        // Load some data
        List<Put> puts = constructPutRequests();
        Object[] results = new Object[puts.size()];
        table.batch(puts, results);
        validateSizeAndEmpty(results, TestMultiParallel.KEYS.length);
        // Deletes
        ArrayList<Delete> deletes = new ArrayList<>();
        for (int i = 0; i < (TestMultiParallel.KEYS.length); i++) {
            Delete delete = new Delete(TestMultiParallel.KEYS[i]);
            delete.addFamily(TestMultiParallel.BYTES_FAMILY);
            deletes.add(delete);
        }
        table.delete(deletes);
        Assert.assertTrue(deletes.isEmpty());
        // Get to make sure ...
        for (byte[] k : TestMultiParallel.KEYS) {
            Get get = new Get(k);
            get.addColumn(TestMultiParallel.BYTES_FAMILY, TestMultiParallel.QUALIFIER);
            Assert.assertFalse(table.exists(get));
        }
        table.close();
    }

    @Test
    public void testBatchWithManyColsInOneRowGetAndPut() throws Exception {
        TestMultiParallel.LOG.info("test=testBatchWithManyColsInOneRowGetAndPut");
        Table table = TestMultiParallel.UTIL.getConnection().getTable(TestMultiParallel.TEST_TABLE);
        List<Row> puts = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Put put = new Put(TestMultiParallel.ONE_ROW);
            byte[] qual = Bytes.toBytes(("column" + i));
            put.addColumn(TestMultiParallel.BYTES_FAMILY, qual, TestMultiParallel.VALUE);
            puts.add(put);
        }
        Object[] results = new Object[puts.size()];
        table.batch(puts, results);
        // validate
        validateSizeAndEmpty(results, 100);
        // get the data back and validate that it is correct
        List<Row> gets = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Get get = new Get(TestMultiParallel.ONE_ROW);
            byte[] qual = Bytes.toBytes(("column" + i));
            get.addColumn(TestMultiParallel.BYTES_FAMILY, qual);
            gets.add(get);
        }
        Object[] multiRes = new Object[gets.size()];
        table.batch(gets, multiRes);
        int idx = 0;
        for (Object r : multiRes) {
            byte[] qual = Bytes.toBytes(("column" + idx));
            validateResult(r, qual, TestMultiParallel.VALUE);
            idx++;
        }
        table.close();
    }

    @Test
    public void testBatchWithIncrementAndAppend() throws Exception {
        TestMultiParallel.LOG.info("test=testBatchWithIncrementAndAppend");
        final byte[] QUAL1 = Bytes.toBytes("qual1");
        final byte[] QUAL2 = Bytes.toBytes("qual2");
        final byte[] QUAL3 = Bytes.toBytes("qual3");
        final byte[] QUAL4 = Bytes.toBytes("qual4");
        Table table = TestMultiParallel.UTIL.getConnection().getTable(TestMultiParallel.TEST_TABLE);
        Delete d = new Delete(TestMultiParallel.ONE_ROW);
        table.delete(d);
        Put put = new Put(TestMultiParallel.ONE_ROW);
        put.addColumn(TestMultiParallel.BYTES_FAMILY, QUAL1, Bytes.toBytes("abc"));
        put.addColumn(TestMultiParallel.BYTES_FAMILY, QUAL2, Bytes.toBytes(1L));
        table.put(put);
        Increment inc = new Increment(TestMultiParallel.ONE_ROW);
        inc.addColumn(TestMultiParallel.BYTES_FAMILY, QUAL2, 1);
        inc.addColumn(TestMultiParallel.BYTES_FAMILY, QUAL3, 1);
        Append a = new Append(TestMultiParallel.ONE_ROW);
        a.addColumn(TestMultiParallel.BYTES_FAMILY, QUAL1, Bytes.toBytes("def"));
        a.addColumn(TestMultiParallel.BYTES_FAMILY, QUAL4, Bytes.toBytes("xyz"));
        List<Row> actions = new ArrayList<>();
        actions.add(inc);
        actions.add(a);
        Object[] multiRes = new Object[actions.size()];
        table.batch(actions, multiRes);
        validateResult(multiRes[1], QUAL1, Bytes.toBytes("abcdef"));
        validateResult(multiRes[1], QUAL4, Bytes.toBytes("xyz"));
        validateResult(multiRes[0], QUAL2, Bytes.toBytes(2L));
        validateResult(multiRes[0], QUAL3, Bytes.toBytes(1L));
        table.close();
    }

    @Test
    public void testNonceCollision() throws Exception {
        TestMultiParallel.LOG.info("test=testNonceCollision");
        final Connection connection = ConnectionFactory.createConnection(TestMultiParallel.UTIL.getConfiguration());
        Table table = connection.getTable(TestMultiParallel.TEST_TABLE);
        Put put = new Put(TestMultiParallel.ONE_ROW);
        put.addColumn(TestMultiParallel.BYTES_FAMILY, TestMultiParallel.QUALIFIER, Bytes.toBytes(0L));
        // Replace nonce manager with the one that returns each nonce twice.
        NonceGenerator cnm = new NonceGenerator() {
            private final PerClientRandomNonceGenerator delegate = PerClientRandomNonceGenerator.get();

            private long lastNonce = -1;

            @Override
            public synchronized long newNonce() {
                long nonce = 0;
                if ((lastNonce) == (-1)) {
                    lastNonce = nonce = delegate.newNonce();
                } else {
                    nonce = lastNonce;
                    lastNonce = -1L;
                }
                return nonce;
            }

            @Override
            public long getNonceGroup() {
                return delegate.getNonceGroup();
            }
        };
        NonceGenerator oldCnm = ConnectionUtils.injectNonceGeneratorForTesting(((ClusterConnection) (connection)), cnm);
        // First test sequential requests.
        try {
            Increment inc = new Increment(TestMultiParallel.ONE_ROW);
            inc.addColumn(TestMultiParallel.BYTES_FAMILY, TestMultiParallel.QUALIFIER, 1L);
            table.increment(inc);
            // duplicate increment
            inc = new Increment(TestMultiParallel.ONE_ROW);
            inc.addColumn(TestMultiParallel.BYTES_FAMILY, TestMultiParallel.QUALIFIER, 1L);
            Result result = table.increment(inc);
            validateResult(result, TestMultiParallel.QUALIFIER, Bytes.toBytes(1L));
            Get get = new Get(TestMultiParallel.ONE_ROW);
            get.addColumn(TestMultiParallel.BYTES_FAMILY, TestMultiParallel.QUALIFIER);
            result = table.get(get);
            validateResult(result, TestMultiParallel.QUALIFIER, Bytes.toBytes(1L));
            // Now run a bunch of requests in parallel, exactly half should succeed.
            int numRequests = 40;
            final CountDownLatch startedLatch = new CountDownLatch(numRequests);
            final CountDownLatch startLatch = new CountDownLatch(1);
            final CountDownLatch doneLatch = new CountDownLatch(numRequests);
            for (int i = 0; i < numRequests; ++i) {
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        Table table = null;
                        try {
                            table = connection.getTable(TestMultiParallel.TEST_TABLE);
                        } catch (IOException e) {
                            Assert.fail("Not expected");
                        }
                        Increment inc = new Increment(TestMultiParallel.ONE_ROW);
                        inc.addColumn(TestMultiParallel.BYTES_FAMILY, TestMultiParallel.QUALIFIER, 1L);
                        startedLatch.countDown();
                        try {
                            startLatch.await();
                        } catch (InterruptedException e) {
                            Assert.fail("Not expected");
                        }
                        try {
                            table.increment(inc);
                        } catch (IOException ioEx) {
                            Assert.fail("Not expected");
                        }
                        doneLatch.countDown();
                    }
                };
                Threads.setDaemonThreadRunning(new Thread(r));
            }
            startedLatch.await();// Wait until all threads are ready...

            startLatch.countDown();// ...and unleash the herd!

            doneLatch.await();
            // Now verify
            get = new Get(TestMultiParallel.ONE_ROW);
            get.addColumn(TestMultiParallel.BYTES_FAMILY, TestMultiParallel.QUALIFIER);
            result = table.get(get);
            validateResult(result, TestMultiParallel.QUALIFIER, Bytes.toBytes(((numRequests / 2) + 1L)));
            table.close();
        } finally {
            ConnectionImplementation.injectNonceGeneratorForTesting(((ClusterConnection) (connection)), oldCnm);
        }
    }

    @Test
    public void testBatchWithMixedActions() throws Exception {
        TestMultiParallel.LOG.info("test=testBatchWithMixedActions");
        Table table = TestMultiParallel.UTIL.getConnection().getTable(TestMultiParallel.TEST_TABLE);
        // Load some data to start
        List<Put> puts = constructPutRequests();
        Object[] results = new Object[puts.size()];
        table.batch(puts, results);
        validateSizeAndEmpty(results, TestMultiParallel.KEYS.length);
        // Batch: get, get, put(new col), delete, get, get of put, get of deleted,
        // put
        List<Row> actions = new ArrayList<>();
        byte[] qual2 = Bytes.toBytes("qual2");
        byte[] val2 = Bytes.toBytes("putvalue2");
        // 0 get
        Get get = new Get(TestMultiParallel.KEYS[10]);
        get.addColumn(TestMultiParallel.BYTES_FAMILY, TestMultiParallel.QUALIFIER);
        actions.add(get);
        // 1 get
        get = new Get(TestMultiParallel.KEYS[11]);
        get.addColumn(TestMultiParallel.BYTES_FAMILY, TestMultiParallel.QUALIFIER);
        actions.add(get);
        // 2 put of new column
        Put put = new Put(TestMultiParallel.KEYS[10]);
        put.addColumn(TestMultiParallel.BYTES_FAMILY, qual2, val2);
        actions.add(put);
        // 3 delete
        Delete delete = new Delete(TestMultiParallel.KEYS[20]);
        delete.addFamily(TestMultiParallel.BYTES_FAMILY);
        actions.add(delete);
        // 4 get
        get = new Get(TestMultiParallel.KEYS[30]);
        get.addColumn(TestMultiParallel.BYTES_FAMILY, TestMultiParallel.QUALIFIER);
        actions.add(get);
        // There used to be a 'get' of a previous put here, but removed
        // since this API really cannot guarantee order in terms of mixed
        // get/puts.
        // 5 put of new column
        put = new Put(TestMultiParallel.KEYS[40]);
        put.addColumn(TestMultiParallel.BYTES_FAMILY, qual2, val2);
        actions.add(put);
        // 6 RowMutations
        RowMutations rm = new RowMutations(TestMultiParallel.KEYS[50]);
        put = new Put(TestMultiParallel.KEYS[50]);
        put.addColumn(TestMultiParallel.BYTES_FAMILY, qual2, val2);
        rm.add(((Mutation) (put)));
        byte[] qual3 = Bytes.toBytes("qual3");
        byte[] val3 = Bytes.toBytes("putvalue3");
        put = new Put(TestMultiParallel.KEYS[50]);
        put.addColumn(TestMultiParallel.BYTES_FAMILY, qual3, val3);
        rm.add(((Mutation) (put)));
        actions.add(rm);
        // 7 Add another Get to the mixed sequence after RowMutations
        get = new Get(TestMultiParallel.KEYS[10]);
        get.addColumn(TestMultiParallel.BYTES_FAMILY, TestMultiParallel.QUALIFIER);
        actions.add(get);
        results = new Object[actions.size()];
        table.batch(actions, results);
        // Validation
        validateResult(results[0]);
        validateResult(results[1]);
        validateEmpty(results[3]);
        validateResult(results[4]);
        validateEmpty(results[5]);
        validateEmpty(results[6]);
        validateResult(results[7]);
        // validate last put, externally from the batch
        get = new Get(TestMultiParallel.KEYS[40]);
        get.addColumn(TestMultiParallel.BYTES_FAMILY, qual2);
        Result r = table.get(get);
        validateResult(r, qual2, val2);
        // validate last RowMutations, externally from the batch
        get = new Get(TestMultiParallel.KEYS[50]);
        get.addColumn(TestMultiParallel.BYTES_FAMILY, qual2);
        r = table.get(get);
        validateResult(r, qual2, val2);
        get = new Get(TestMultiParallel.KEYS[50]);
        get.addColumn(TestMultiParallel.BYTES_FAMILY, qual3);
        r = table.get(get);
        validateResult(r, qual3, val3);
        table.close();
    }

    public static class MyMasterObserver implements MasterCoprocessor , MasterObserver {
        private static final AtomicInteger postBalanceCount = new AtomicInteger(0);

        private static final AtomicBoolean start = new AtomicBoolean(false);

        @Override
        public void start(CoprocessorEnvironment env) throws IOException {
            TestMultiParallel.MyMasterObserver.start.set(true);
        }

        @Override
        public Optional<MasterObserver> getMasterObserver() {
            return Optional.of(this);
        }

        @Override
        public void postBalance(final ObserverContext<MasterCoprocessorEnvironment> ctx, List<RegionPlan> plans) throws IOException {
            if (!(plans.isEmpty())) {
                TestMultiParallel.MyMasterObserver.postBalanceCount.incrementAndGet();
            }
        }
    }
}

