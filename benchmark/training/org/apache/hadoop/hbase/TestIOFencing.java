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
package org.apache.hadoop.hbase;


import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptor;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.TableDescriptor;
import org.apache.hadoop.hbase.regionserver.HRegion;
import org.apache.hadoop.hbase.regionserver.HStore;
import org.apache.hadoop.hbase.regionserver.HStoreFile;
import org.apache.hadoop.hbase.regionserver.RegionServerServices;
import org.apache.hadoop.hbase.regionserver.compactions.CompactionContext;
import org.apache.hadoop.hbase.regionserver.throttle.ThroughputController;
import org.apache.hadoop.hbase.security.User;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.MiscTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.wal.WAL;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test for the case where a regionserver going down has enough cycles to do damage to regions that
 * have actually been assigned elsehwere.
 * <p>
 * If we happen to assign a region before it fully done with in its old location -- i.e. it is on
 * two servers at the same time -- all can work fine until the case where the region on the dying
 * server decides to compact or otherwise change the region file set. The region in its new location
 * will then get a surprise when it tries to do something w/ a file removed by the region in its old
 * location on dying server.
 * <p>
 * Making a test for this case is a little tough in that even if a file is deleted up on the
 * namenode, if the file was opened before the delete, it will continue to let reads happen until
 * something changes the state of cached blocks in the dfsclient that was already open (a block from
 * the deleted file is cleaned from the datanode by NN).
 * <p>
 * What we will do below is do an explicit check for existence on the files listed in the region
 * that has had some files removed because of a compaction. This sort of hurry's along and makes
 * certain what is a chance occurance.
 */
@Category({ MiscTests.class, LargeTests.class })
public class TestIOFencing {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestIOFencing.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestIOFencing.class);

    static {
        // Uncomment the following lines if more verbosity is needed for
        // debugging (see HBASE-12285 for details).
        // ((Log4JLogger)FSNamesystem.LOG).getLogger().setLevel(Level.ALL);
        // ((Log4JLogger)DataNode.LOG).getLogger().setLevel(Level.ALL);
        // ((Log4JLogger)LeaseManager.LOG).getLogger().setLevel(Level.ALL);
        // ((Log4JLogger)LogFactory.getLog("org.apache.hadoop.hdfs.server.namenode.FSNamesystem"))
        // .getLogger().setLevel(Level.ALL);
        // ((Log4JLogger)DFSClient.LOG).getLogger().setLevel(Level.ALL);
    }

    public abstract static class CompactionBlockerRegion extends HRegion {
        AtomicInteger compactCount = new AtomicInteger();

        volatile CountDownLatch compactionsBlocked = new CountDownLatch(0);

        volatile CountDownLatch compactionsWaiting = new CountDownLatch(0);

        @SuppressWarnings("deprecation")
        public CompactionBlockerRegion(Path tableDir, WAL log, FileSystem fs, Configuration confParam, RegionInfo info, TableDescriptor htd, RegionServerServices rsServices) {
            super(tableDir, log, fs, confParam, info, htd, rsServices);
        }

        public void stopCompactions() {
            compactionsBlocked = new CountDownLatch(1);
            compactionsWaiting = new CountDownLatch(1);
        }

        public void allowCompactions() {
            TestIOFencing.LOG.debug("allowing compactions");
            compactionsBlocked.countDown();
        }

        public void waitForCompactionToBlock() throws IOException {
            try {
                TestIOFencing.LOG.debug("waiting for compaction to block");
                compactionsWaiting.await();
                TestIOFencing.LOG.debug("compaction block reached");
            } catch (InterruptedException ex) {
                throw new IOException(ex);
            }
        }

        @Override
        public boolean compact(CompactionContext compaction, HStore store, ThroughputController throughputController) throws IOException {
            try {
                return super.compact(compaction, store, throughputController);
            } finally {
                compactCount.getAndIncrement();
            }
        }

        @Override
        public boolean compact(CompactionContext compaction, HStore store, ThroughputController throughputController, User user) throws IOException {
            try {
                return super.compact(compaction, store, throughputController, user);
            } finally {
                compactCount.getAndIncrement();
            }
        }

        public int countStoreFiles() {
            int count = 0;
            for (HStore store : stores.values()) {
                count += store.getStorefilesCount();
            }
            return count;
        }
    }

    /**
     * An override of HRegion that allows us park compactions in a holding pattern and
     * then when appropriate for the test, allow them proceed again.
     */
    public static class BlockCompactionsInPrepRegion extends TestIOFencing.CompactionBlockerRegion {
        public BlockCompactionsInPrepRegion(Path tableDir, WAL log, FileSystem fs, Configuration confParam, RegionInfo info, TableDescriptor htd, RegionServerServices rsServices) {
            super(tableDir, log, fs, confParam, info, htd, rsServices);
        }

        @Override
        protected void doRegionCompactionPrep() throws IOException {
            compactionsWaiting.countDown();
            try {
                compactionsBlocked.await();
            } catch (InterruptedException ex) {
                throw new IOException();
            }
            super.doRegionCompactionPrep();
        }
    }

    /**
     * An override of HRegion that allows us park compactions in a holding pattern and
     * then when appropriate for the test, allow them proceed again. This allows the compaction
     * entry to go the WAL before blocking, but blocks afterwards
     */
    public static class BlockCompactionsInCompletionRegion extends TestIOFencing.CompactionBlockerRegion {
        public BlockCompactionsInCompletionRegion(Path tableDir, WAL log, FileSystem fs, Configuration confParam, RegionInfo info, TableDescriptor htd, RegionServerServices rsServices) {
            super(tableDir, log, fs, confParam, info, htd, rsServices);
        }

        @Override
        protected HStore instantiateHStore(final ColumnFamilyDescriptor family) throws IOException {
            return new TestIOFencing.BlockCompactionsInCompletionHStore(this, family, this.conf);
        }
    }

    public static class BlockCompactionsInCompletionHStore extends HStore {
        TestIOFencing.CompactionBlockerRegion r;

        protected BlockCompactionsInCompletionHStore(HRegion region, ColumnFamilyDescriptor family, Configuration confParam) throws IOException {
            super(region, family, confParam);
            r = ((TestIOFencing.CompactionBlockerRegion) (region));
        }

        @Override
        protected void completeCompaction(Collection<HStoreFile> compactedFiles) throws IOException {
            try {
                r.compactionsWaiting.countDown();
                r.compactionsBlocked.await();
            } catch (InterruptedException ex) {
                throw new IOException(ex);
            }
            super.completeCompaction(compactedFiles);
        }
    }

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final TableName TABLE_NAME = TableName.valueOf("tabletest");

    private static final byte[] FAMILY = Bytes.toBytes("family");

    private static final int FIRST_BATCH_COUNT = 4000;

    private static final int SECOND_BATCH_COUNT = TestIOFencing.FIRST_BATCH_COUNT;

    /**
     * Test that puts up a regionserver, starts a compaction on a loaded region but holds the
     * compaction until after we have killed the server and the region has come up on
     * a new regionserver altogether.  This fakes the double assignment case where region in one
     * location changes the files out from underneath a region being served elsewhere.
     */
    @Test
    public void testFencingAroundCompaction() throws Exception {
        for (MemoryCompactionPolicy policy : MemoryCompactionPolicy.values()) {
            doTest(TestIOFencing.BlockCompactionsInPrepRegion.class, policy);
        }
    }

    /**
     * Test that puts up a regionserver, starts a compaction on a loaded region but holds the
     * compaction completion until after we have killed the server and the region has come up on
     * a new regionserver altogether.  This fakes the double assignment case where region in one
     * location changes the files out from underneath a region being served elsewhere.
     */
    @Test
    public void testFencingAroundCompactionAfterWALSync() throws Exception {
        for (MemoryCompactionPolicy policy : MemoryCompactionPolicy.values()) {
            doTest(TestIOFencing.BlockCompactionsInCompletionRegion.class, policy);
        }
    }
}

