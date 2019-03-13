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


import CompactionLifeCycleTracker.DUMMY;
import CompactionThroughputControllerFactory.HBASE_THROUGHPUT_CONTROLLER_KEY;
import Durability.SKIP_WAL;
import HConstants.HREGION_MEMSTORE_BLOCK_MULTIPLIER;
import HConstants.HREGION_MEMSTORE_FLUSH_SIZE;
import Store.PRIORITY_USER;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.ChoreService;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HBaseTestCase;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.regionserver.compactions.CompactionContext;
import org.apache.hadoop.hbase.regionserver.compactions.CompactionLifeCycleTracker;
import org.apache.hadoop.hbase.regionserver.compactions.CompactionRequestImpl;
import org.apache.hadoop.hbase.regionserver.compactions.DefaultCompactor;
import org.apache.hadoop.hbase.regionserver.throttle.NoLimitThroughputController;
import org.apache.hadoop.hbase.regionserver.throttle.ThroughputController;
import org.apache.hadoop.hbase.security.User;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.Threads;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static HStore.DEFAULT_BLOCKING_STOREFILE_COUNT;
import static HStore.closeCheckInterval;


/**
 * Test compaction framework and common functions
 */
@Category({ RegionServerTests.class, MediumTests.class })
public class TestCompaction {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestCompaction.class);

    @Rule
    public TestName name = new TestName();

    private static final HBaseTestingUtility UTIL = HBaseTestingUtility.createLocalHTU();

    protected Configuration conf = TestCompaction.UTIL.getConfiguration();

    private HRegion r = null;

    private HTableDescriptor htd = null;

    private static final byte[] COLUMN_FAMILY = HBaseTestingUtility.fam1;

    private final byte[] STARTROW = Bytes.toBytes(HBaseTestingUtility.START_KEY);

    private static final byte[] COLUMN_FAMILY_TEXT = TestCompaction.COLUMN_FAMILY;

    private int compactionThreshold;

    private byte[] secondRowBytes;

    private byte[] thirdRowBytes;

    private static final long MAX_FILES_TO_COMPACT = 10;

    private final byte[] FAMILY = Bytes.toBytes("cf");

    /**
     * constructor
     */
    public TestCompaction() {
        super();
        // Set cache flush size to 1MB
        conf.setInt(HREGION_MEMSTORE_FLUSH_SIZE, (1024 * 1024));
        conf.setInt(HREGION_MEMSTORE_BLOCK_MULTIPLIER, 100);
        conf.set(HBASE_THROUGHPUT_CONTROLLER_KEY, NoLimitThroughputController.class.getName());
        compactionThreshold = conf.getInt("hbase.hstore.compactionThreshold", 3);
        secondRowBytes = HBaseTestingUtility.START_KEY_BYTES.clone();
        // Increment the least significant character so we get to next row.
        (secondRowBytes[((HBaseTestingUtility.START_KEY_BYTES.length) - 1)])++;
        thirdRowBytes = HBaseTestingUtility.START_KEY_BYTES.clone();
        thirdRowBytes[((HBaseTestingUtility.START_KEY_BYTES.length) - 1)] = ((byte) ((thirdRowBytes[((HBaseTestingUtility.START_KEY_BYTES.length) - 1)]) + 2));
    }

    /**
     * Verify that you can stop a long-running compaction
     * (used during RS shutdown)
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testInterruptCompaction() throws Exception {
        Assert.assertEquals(0, count());
        // lower the polling interval for this test
        int origWI = closeCheckInterval;
        closeCheckInterval = 10 * 1000;// 10 KB

        try {
            // Create a couple store files w/ 15KB (over 10KB interval)
            int jmax = ((int) (Math.ceil((15.0 / (compactionThreshold)))));
            byte[] pad = new byte[1000];// 1 KB chunk

            for (int i = 0; i < (compactionThreshold); i++) {
                Table loader = new RegionAsTable(r);
                Put p = new Put(Bytes.add(STARTROW, Bytes.toBytes(i)));
                p.setDurability(SKIP_WAL);
                for (int j = 0; j < jmax; j++) {
                    p.addColumn(TestCompaction.COLUMN_FAMILY, Bytes.toBytes(j), pad);
                }
                HBaseTestCase.addContent(loader, Bytes.toString(TestCompaction.COLUMN_FAMILY));
                loader.put(p);
                r.flush(true);
            }
            HRegion spyR = Mockito.spy(r);
            Mockito.doAnswer(new Answer() {
                @Override
                public Object answer(InvocationOnMock invocation) throws Throwable {
                    r.writestate.writesEnabled = false;
                    return invocation.callRealMethod();
                }
            }).when(spyR).doRegionCompactionPrep();
            // force a minor compaction, but not before requesting a stop
            spyR.compactStores();
            // ensure that the compaction stopped, all old files are intact,
            HStore s = r.getStore(TestCompaction.COLUMN_FAMILY);
            Assert.assertEquals(compactionThreshold, s.getStorefilesCount());
            Assert.assertTrue(((s.getStorefilesSize()) > (15 * 1000)));
            // and no new store files persisted past compactStores()
            // only one empty dir exists in temp dir
            FileStatus[] ls = r.getFilesystem().listStatus(r.getRegionFileSystem().getTempDir());
            Assert.assertEquals(1, ls.length);
            Path storeTempDir = new Path(r.getRegionFileSystem().getTempDir(), Bytes.toString(TestCompaction.COLUMN_FAMILY));
            Assert.assertTrue(r.getFilesystem().exists(storeTempDir));
            ls = r.getFilesystem().listStatus(storeTempDir);
            Assert.assertEquals(0, ls.length);
        } finally {
            // don't mess up future tests
            r.writestate.writesEnabled = true;
            closeCheckInterval = origWI;
            // Delete all Store information once done using
            for (int i = 0; i < (compactionThreshold); i++) {
                Delete delete = new Delete(Bytes.add(STARTROW, Bytes.toBytes(i)));
                byte[][] famAndQf = new byte[][]{ TestCompaction.COLUMN_FAMILY, null };
                delete.addFamily(famAndQf[0]);
                r.delete(delete);
            }
            r.flush(true);
            // Multiple versions allowed for an entry, so the delete isn't enough
            // Lower TTL and expire to ensure that all our entries have been wiped
            final int ttl = 1000;
            for (HStore store : this.r.stores.values()) {
                ScanInfo old = store.getScanInfo();
                ScanInfo si = old.customize(old.getMaxVersions(), ttl, old.getKeepDeletedCells());
                store.setScanInfo(si);
            }
            Thread.sleep(ttl);
            r.compact(true);
            Assert.assertEquals(0, count());
        }
    }

    @Test
    public void testCompactionWithCorruptResult() throws Exception {
        int nfiles = 10;
        for (int i = 0; i < nfiles; i++) {
            createStoreFile(r);
        }
        HStore store = r.getStore(TestCompaction.COLUMN_FAMILY);
        Collection<HStoreFile> storeFiles = store.getStorefiles();
        DefaultCompactor tool = ((DefaultCompactor) (store.storeEngine.getCompactor()));
        tool.compactForTesting(storeFiles, false);
        // Now lets corrupt the compacted file.
        FileSystem fs = store.getFileSystem();
        // default compaction policy created one and only one new compacted file
        Path dstPath = store.getRegionFileSystem().createTempName();
        FSDataOutputStream stream = fs.create(dstPath, null, true, 512, ((short) (3)), 1024L, null);
        stream.writeChars("CORRUPT FILE!!!!");
        stream.close();
        Path origPath = store.getRegionFileSystem().commitStoreFile(Bytes.toString(TestCompaction.COLUMN_FAMILY), dstPath);
        try {
            ((HStore) (store)).moveFileIntoPlace(origPath);
        } catch (Exception e) {
            // The complete compaction should fail and the corrupt file should remain
            // in the 'tmp' directory;
            Assert.assertTrue(fs.exists(origPath));
            Assert.assertFalse(fs.exists(dstPath));
            System.out.println("testCompactionWithCorruptResult Passed");
            return;
        }
        Assert.fail(("testCompactionWithCorruptResult failed since no exception was" + "thrown while completing a corrupt file"));
    }

    /**
     * Create a custom compaction request and be sure that we can track it through the queue, knowing
     * when the compaction is completed.
     */
    @Test
    public void testTrackingCompactionRequest() throws Exception {
        // setup a compact/split thread on a mock server
        HRegionServer mockServer = Mockito.mock(HRegionServer.class);
        Mockito.when(mockServer.getConfiguration()).thenReturn(r.getBaseConf());
        CompactSplit thread = new CompactSplit(mockServer);
        Mockito.when(mockServer.getCompactSplitThread()).thenReturn(thread);
        // setup a region/store with some files
        HStore store = r.getStore(TestCompaction.COLUMN_FAMILY);
        createStoreFile(r);
        for (int i = 0; i < ((TestCompaction.MAX_FILES_TO_COMPACT) + 1); i++) {
            createStoreFile(r);
        }
        CountDownLatch latch = new CountDownLatch(1);
        TestCompaction.Tracker tracker = new TestCompaction.Tracker(latch);
        thread.requestCompaction(r, store, "test custom comapction", Store.PRIORITY_USER, tracker, null);
        // wait for the latch to complete.
        latch.await();
        thread.interruptIfNecessary();
    }

    @Test
    public void testCompactionFailure() throws Exception {
        // setup a compact/split thread on a mock server
        HRegionServer mockServer = Mockito.mock(HRegionServer.class);
        Mockito.when(mockServer.getConfiguration()).thenReturn(r.getBaseConf());
        CompactSplit thread = new CompactSplit(mockServer);
        Mockito.when(mockServer.getCompactSplitThread()).thenReturn(thread);
        // setup a region/store with some files
        HStore store = r.getStore(TestCompaction.COLUMN_FAMILY);
        createStoreFile(r);
        for (int i = 0; i < ((DEFAULT_BLOCKING_STOREFILE_COUNT) - 1); i++) {
            createStoreFile(r);
        }
        HRegion mockRegion = Mockito.spy(r);
        Mockito.when(mockRegion.checkSplit()).thenThrow(new IndexOutOfBoundsException());
        MetricsRegionWrapper metricsWrapper = new MetricsRegionWrapperImpl(r);
        long preCompletedCount = metricsWrapper.getNumCompactionsCompleted();
        long preFailedCount = metricsWrapper.getNumCompactionsFailed();
        CountDownLatch latch = new CountDownLatch(1);
        TestCompaction.Tracker tracker = new TestCompaction.Tracker(latch);
        thread.requestCompaction(mockRegion, store, "test custom comapction", Store.PRIORITY_USER, tracker, null);
        // wait for the latch to complete.
        latch.await(120, TimeUnit.SECONDS);
        // compaction should have completed and been marked as failed due to error in split request
        long postCompletedCount = metricsWrapper.getNumCompactionsCompleted();
        long postFailedCount = metricsWrapper.getNumCompactionsFailed();
        Assert.assertTrue((((("Completed count should have increased (pre=" + preCompletedCount) + ", post=") + postCompletedCount) + ")"), (postCompletedCount > preCompletedCount));
        Assert.assertTrue((((("Failed count should have increased (pre=" + preFailedCount) + ", post=") + postFailedCount) + ")"), (postFailedCount > preFailedCount));
    }

    /**
     * Test no new Compaction requests are generated after calling stop compactions
     */
    @Test
    public void testStopStartCompaction() throws IOException {
        // setup a compact/split thread on a mock server
        HRegionServer mockServer = Mockito.mock(HRegionServer.class);
        Mockito.when(mockServer.getConfiguration()).thenReturn(r.getBaseConf());
        CompactSplit thread = new CompactSplit(mockServer);
        Mockito.when(mockServer.getCompactSplitThread()).thenReturn(thread);
        // setup a region/store with some files
        HStore store = r.getStore(TestCompaction.COLUMN_FAMILY);
        createStoreFile(r);
        for (int i = 0; i < ((DEFAULT_BLOCKING_STOREFILE_COUNT) - 1); i++) {
            createStoreFile(r);
        }
        thread.switchCompaction(false);
        thread.requestCompaction(r, store, "test", PRIORITY_USER, DUMMY, null);
        Assert.assertEquals(false, thread.isCompactionsEnabled());
        Assert.assertEquals(0, ((thread.getLongCompactions().getActiveCount()) + (thread.getShortCompactions().getActiveCount())));
        thread.switchCompaction(true);
        Assert.assertEquals(true, thread.isCompactionsEnabled());
        thread.requestCompaction(r, store, "test", PRIORITY_USER, DUMMY, null);
        Assert.assertEquals(1, ((thread.getLongCompactions().getActiveCount()) + (thread.getShortCompactions().getActiveCount())));
    }

    @Test
    public void testInterruptingRunningCompactions() throws Exception {
        // setup a compact/split thread on a mock server
        conf.set(HBASE_THROUGHPUT_CONTROLLER_KEY, TestCompaction.WaitThroughPutController.class.getName());
        HRegionServer mockServer = Mockito.mock(HRegionServer.class);
        Mockito.when(mockServer.getConfiguration()).thenReturn(r.getBaseConf());
        CompactSplit thread = new CompactSplit(mockServer);
        Mockito.when(mockServer.getCompactSplitThread()).thenReturn(thread);
        // setup a region/store with some files
        HStore store = r.getStore(TestCompaction.COLUMN_FAMILY);
        int jmax = ((int) (Math.ceil((15.0 / (compactionThreshold)))));
        byte[] pad = new byte[1000];// 1 KB chunk

        for (int i = 0; i < (compactionThreshold); i++) {
            Table loader = new RegionAsTable(r);
            Put p = new Put(Bytes.add(STARTROW, Bytes.toBytes(i)));
            p.setDurability(SKIP_WAL);
            for (int j = 0; j < jmax; j++) {
                p.addColumn(TestCompaction.COLUMN_FAMILY, Bytes.toBytes(j), pad);
            }
            HBaseTestCase.addContent(loader, Bytes.toString(TestCompaction.COLUMN_FAMILY));
            loader.put(p);
            r.flush(true);
        }
        HStore s = r.getStore(TestCompaction.COLUMN_FAMILY);
        int initialFiles = s.getStorefilesCount();
        thread.requestCompaction(r, store, "test custom comapction", Store.PRIORITY_USER, DUMMY, null);
        Thread.sleep(3000);
        thread.switchCompaction(false);
        Assert.assertEquals(initialFiles, s.getStorefilesCount());
        // don't mess up future tests
        thread.switchCompaction(true);
    }

    /**
     * HBASE-7947: Regression test to ensure adding to the correct list in the
     * {@link CompactSplit}
     *
     * @throws Exception
     * 		on failure
     */
    @Test
    public void testMultipleCustomCompactionRequests() throws Exception {
        // setup a compact/split thread on a mock server
        HRegionServer mockServer = Mockito.mock(HRegionServer.class);
        Mockito.when(mockServer.getConfiguration()).thenReturn(r.getBaseConf());
        CompactSplit thread = new CompactSplit(mockServer);
        Mockito.when(mockServer.getCompactSplitThread()).thenReturn(thread);
        // setup a region/store with some files
        int numStores = r.getStores().size();
        CountDownLatch latch = new CountDownLatch(numStores);
        TestCompaction.Tracker tracker = new TestCompaction.Tracker(latch);
        // create some store files and setup requests for each store on which we want to do a
        // compaction
        for (HStore store : r.getStores()) {
            createStoreFile(r, store.getColumnFamilyName());
            createStoreFile(r, store.getColumnFamilyName());
            createStoreFile(r, store.getColumnFamilyName());
            thread.requestCompaction(r, store, "test mulitple custom comapctions", Store.PRIORITY_USER, tracker, null);
        }
        // wait for the latch to complete.
        latch.await();
        thread.interruptIfNecessary();
    }

    class StoreMockMaker extends StatefulStoreMockMaker {
        public ArrayList<HStoreFile> compacting = new ArrayList<>();

        public ArrayList<HStoreFile> notCompacting = new ArrayList<>();

        private final ArrayList<Integer> results;

        public StoreMockMaker(ArrayList<Integer> results) {
            this.results = results;
        }

        public class TestCompactionContext extends CompactionContext {
            private List<HStoreFile> selectedFiles;

            public TestCompactionContext(List<HStoreFile> selectedFiles) {
                super();
                this.selectedFiles = selectedFiles;
            }

            @Override
            public List<HStoreFile> preSelect(List<HStoreFile> filesCompacting) {
                return new ArrayList<>();
            }

            @Override
            public boolean select(List<HStoreFile> filesCompacting, boolean isUserCompaction, boolean mayUseOffPeak, boolean forceMajor) throws IOException {
                this.request = new CompactionRequestImpl(selectedFiles);
                this.request.setPriority(getPriority());
                return true;
            }

            @Override
            public List<Path> compact(ThroughputController throughputController, User user) throws IOException {
                finishCompaction(this.selectedFiles);
                return new ArrayList<>();
            }
        }

        @Override
        public synchronized Optional<CompactionContext> selectCompaction() {
            CompactionContext ctx = new TestCompaction.StoreMockMaker.TestCompactionContext(new ArrayList(notCompacting));
            compacting.addAll(notCompacting);
            notCompacting.clear();
            try {
                ctx.select(null, false, false, false);
            } catch (IOException ex) {
                Assert.fail("Shouldn't happen");
            }
            return Optional.of(ctx);
        }

        @Override
        public synchronized void cancelCompaction(Object object) {
            TestCompaction.StoreMockMaker.TestCompactionContext ctx = ((TestCompaction.StoreMockMaker.TestCompactionContext) (object));
            compacting.removeAll(ctx.selectedFiles);
            notCompacting.addAll(ctx.selectedFiles);
        }

        public synchronized void finishCompaction(List<HStoreFile> sfs) {
            if (sfs.isEmpty())
                return;

            synchronized(results) {
                results.add(sfs.size());
            }
            compacting.removeAll(sfs);
        }

        @Override
        public int getPriority() {
            return (7 - (compacting.size())) - (notCompacting.size());
        }
    }

    public class BlockingStoreMockMaker extends StatefulStoreMockMaker {
        TestCompaction.BlockingStoreMockMaker.BlockingCompactionContext blocked = null;

        public class BlockingCompactionContext extends CompactionContext {
            public volatile boolean isInCompact = false;

            public void unblock() {
                synchronized(this) {
                    this.notifyAll();
                }
            }

            @Override
            public List<Path> compact(ThroughputController throughputController, User user) throws IOException {
                try {
                    isInCompact = true;
                    synchronized(this) {
                        this.wait();
                    }
                } catch (InterruptedException e) {
                    Assume.assumeNoException(e);
                }
                return new ArrayList<>();
            }

            @Override
            public List<HStoreFile> preSelect(List<HStoreFile> filesCompacting) {
                return new ArrayList<>();
            }

            @Override
            public boolean select(List<HStoreFile> f, boolean i, boolean m, boolean e) throws IOException {
                this.request = new CompactionRequestImpl(new ArrayList());
                return true;
            }
        }

        @Override
        public Optional<CompactionContext> selectCompaction() {
            this.blocked = new TestCompaction.BlockingStoreMockMaker.BlockingCompactionContext();
            try {
                this.blocked.select(null, false, false, false);
            } catch (IOException ex) {
                Assert.fail("Shouldn't happen");
            }
            return Optional.of(blocked);
        }

        @Override
        public void cancelCompaction(Object object) {
        }

        @Override
        public int getPriority() {
            return Integer.MIN_VALUE;// some invalid value, see createStoreMock

        }

        public TestCompaction.BlockingStoreMockMaker.BlockingCompactionContext waitForBlocking() {
            while (((this.blocked) == null) || (!(this.blocked.isInCompact))) {
                Threads.sleepWithoutInterrupt(50);
            } 
            TestCompaction.BlockingStoreMockMaker.BlockingCompactionContext ctx = this.blocked;
            this.blocked = null;
            return ctx;
        }

        @Override
        public HStore createStoreMock(String name) throws Exception {
            return createStoreMock(Integer.MIN_VALUE, name);
        }

        public HStore createStoreMock(int priority, String name) throws Exception {
            // Override the mock to always return the specified priority.
            HStore s = super.createStoreMock(name);
            Mockito.when(s.getCompactPriority()).thenReturn(priority);
            return s;
        }
    }

    /**
     * Test compaction priority management and multiple compactions per store (HBASE-8665).
     */
    @Test
    public void testCompactionQueuePriorities() throws Exception {
        // Setup a compact/split thread on a mock server.
        final Configuration conf = HBaseConfiguration.create();
        HRegionServer mockServer = Mockito.mock(HRegionServer.class);
        Mockito.when(mockServer.isStopped()).thenReturn(false);
        Mockito.when(mockServer.getConfiguration()).thenReturn(conf);
        Mockito.when(mockServer.getChoreService()).thenReturn(new ChoreService("test"));
        CompactSplit cst = new CompactSplit(mockServer);
        Mockito.when(mockServer.getCompactSplitThread()).thenReturn(cst);
        // prevent large compaction thread pool stealing job from small compaction queue.
        cst.shutdownLongCompactions();
        // Set up the region mock that redirects compactions.
        HRegion r = Mockito.mock(HRegion.class);
        Mockito.when(r.compact(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).then(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                invocation.<CompactionContext>getArgument(0).compact(invocation.getArgument(2), null);
                return true;
            }
        });
        // Set up store mocks for 2 "real" stores and the one we use for blocking CST.
        ArrayList<Integer> results = new ArrayList<>();
        TestCompaction.StoreMockMaker sm = new TestCompaction.StoreMockMaker(results);
        TestCompaction.StoreMockMaker sm2 = new TestCompaction.StoreMockMaker(results);
        HStore store = sm.createStoreMock("store1");
        HStore store2 = sm2.createStoreMock("store2");
        TestCompaction.BlockingStoreMockMaker blocker = new TestCompaction.BlockingStoreMockMaker();
        // First, block the compaction thread so that we could muck with queue.
        cst.requestSystemCompaction(r, blocker.createStoreMock(1, "b-pri1"), "b-pri1");
        TestCompaction.BlockingStoreMockMaker.BlockingCompactionContext currentBlock = blocker.waitForBlocking();
        // Add 4 files to store1, 3 to store2, and queue compactions; pri 3 and 4 respectively.
        for (int i = 0; i < 4; ++i) {
            sm.notCompacting.add(TestCompaction.createFile());
        }
        cst.requestSystemCompaction(r, store, "s1-pri3");
        for (int i = 0; i < 3; ++i) {
            sm2.notCompacting.add(TestCompaction.createFile());
        }
        cst.requestSystemCompaction(r, store2, "s2-pri4");
        // Now add 2 more files to store1 and queue compaction - pri 1.
        for (int i = 0; i < 2; ++i) {
            sm.notCompacting.add(TestCompaction.createFile());
        }
        cst.requestSystemCompaction(r, store, "s1-pri1");
        // Finally add blocking compaction with priority 2.
        cst.requestSystemCompaction(r, blocker.createStoreMock(2, "b-pri2"), "b-pri2");
        // Unblock the blocking compaction; we should run pri1 and become block again in pri2.
        currentBlock.unblock();
        currentBlock = blocker.waitForBlocking();
        // Pri1 should have "compacted" all 6 files.
        Assert.assertEquals(1, results.size());
        Assert.assertEquals(6, results.get(0).intValue());
        // Add 2 files to store 1 (it has 2 files now).
        for (int i = 0; i < 2; ++i) {
            sm.notCompacting.add(TestCompaction.createFile());
        }
        // Now we have pri4 for store 2 in queue, and pri3 for store1; store1's current priority
        // is 5, however, so it must not preempt store 2. Add blocking compaction at the end.
        cst.requestSystemCompaction(r, blocker.createStoreMock(7, "b-pri7"), "b-pri7");
        currentBlock.unblock();
        currentBlock = blocker.waitForBlocking();
        Assert.assertEquals(3, results.size());
        Assert.assertEquals(3, results.get(1).intValue());// 3 files should go before 2 files.

        Assert.assertEquals(2, results.get(2).intValue());
        currentBlock.unblock();
        cst.interruptIfNecessary();
    }

    /**
     * Firstly write 10 cells (with different time stamp) to a qualifier and flush
     * to hfile1, then write 10 cells (with different time stamp) to the same
     * qualifier and flush to hfile2. The latest cell (cell-A) in hfile1 and the
     * oldest cell (cell-B) in hfile2 are with the same time stamp but different
     * sequence id, and will get scanned successively during compaction.
     * <p/>
     * We set compaction.kv.max to 10 so compaction will scan 10 versions each
     * round, meanwhile we set keepSeqIdPeriod=0 in {@link DummyCompactor} so all
     * 10 versions of hfile2 will be written out with seqId cleaned (set to 0)
     * including cell-B, then when scanner goes to cell-A it will cause a scan
     * out-of-order assertion error before HBASE-16931
     *
     * @throws Exception
     * 		if error occurs during the test
     */
    @Test
    public void testCompactionSeqId() throws Exception {
        final byte[] ROW = Bytes.toBytes("row");
        final byte[] QUALIFIER = Bytes.toBytes("qualifier");
        long timestamp = 10000;
        // row1/cf:a/10009/Put/vlen=2/seqid=11 V: v9
        // row1/cf:a/10008/Put/vlen=2/seqid=10 V: v8
        // row1/cf:a/10007/Put/vlen=2/seqid=9 V: v7
        // row1/cf:a/10006/Put/vlen=2/seqid=8 V: v6
        // row1/cf:a/10005/Put/vlen=2/seqid=7 V: v5
        // row1/cf:a/10004/Put/vlen=2/seqid=6 V: v4
        // row1/cf:a/10003/Put/vlen=2/seqid=5 V: v3
        // row1/cf:a/10002/Put/vlen=2/seqid=4 V: v2
        // row1/cf:a/10001/Put/vlen=2/seqid=3 V: v1
        // row1/cf:a/10000/Put/vlen=2/seqid=2 V: v0
        for (int i = 0; i < 10; i++) {
            Put put = new Put(ROW);
            put.addColumn(FAMILY, QUALIFIER, (timestamp + i), Bytes.toBytes(("v" + i)));
            r.put(put);
        }
        r.flush(true);
        // row1/cf:a/10018/Put/vlen=3/seqid=16 V: v18
        // row1/cf:a/10017/Put/vlen=3/seqid=17 V: v17
        // row1/cf:a/10016/Put/vlen=3/seqid=18 V: v16
        // row1/cf:a/10015/Put/vlen=3/seqid=19 V: v15
        // row1/cf:a/10014/Put/vlen=3/seqid=20 V: v14
        // row1/cf:a/10013/Put/vlen=3/seqid=21 V: v13
        // row1/cf:a/10012/Put/vlen=3/seqid=22 V: v12
        // row1/cf:a/10011/Put/vlen=3/seqid=23 V: v11
        // row1/cf:a/10010/Put/vlen=3/seqid=24 V: v10
        // row1/cf:a/10009/Put/vlen=2/seqid=25 V: v9
        for (int i = 18; i > 8; i--) {
            Put put = new Put(ROW);
            put.addColumn(FAMILY, QUALIFIER, (timestamp + i), Bytes.toBytes(("v" + i)));
            r.put(put);
        }
        r.flush(true);
        r.compact(true);
    }

    public static class DummyCompactor extends DefaultCompactor {
        public DummyCompactor(Configuration conf, HStore store) {
            super(conf, store);
            this.keepSeqIdPeriod = 0;
        }
    }

    /**
     * Simple {@link CompactionLifeCycleTracker} on which you can wait until the requested compaction
     * finishes.
     */
    public static class Tracker implements CompactionLifeCycleTracker {
        private final CountDownLatch done;

        public Tracker(CountDownLatch done) {
            this.done = done;
        }

        @Override
        public void afterExecution(Store store) {
            done.countDown();
        }
    }

    /**
     * Simple {@link CompactionLifeCycleTracker} on which you can wait until the requested compaction
     * finishes.
     */
    public static class WaitThroughPutController extends NoLimitThroughputController {
        public WaitThroughPutController() {
        }

        @Override
        public long control(String compactionName, long size) throws InterruptedException {
            Thread.sleep(6000000);
            return 6000000;
        }
    }
}

