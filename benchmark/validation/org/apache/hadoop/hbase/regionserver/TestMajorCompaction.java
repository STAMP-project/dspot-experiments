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


import CompactingMemStore.COMPACTING_MEMSTORE_TYPE_KEY;
import CompactionLifeCycleTracker.DUMMY;
import HConstants.HREGION_MEMSTORE_BLOCK_MULTIPLIER;
import HConstants.HREGION_MEMSTORE_FLUSH_SIZE;
import HConstants.MAJOR_COMPACTION_PERIOD;
import KeepDeletedCells.FALSE;
import KeepDeletedCells.TRUE;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.regionserver.compactions.CompactionRequestImpl;
import org.apache.hadoop.hbase.regionserver.compactions.RatioBasedCompactionPolicy;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test major compactions
 */
@Category({ RegionServerTests.class, MediumTests.class })
@RunWith(Parameterized.class)
public class TestMajorCompaction {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestMajorCompaction.class);

    @Rule
    public TestName name;

    private static final Logger LOG = LoggerFactory.getLogger(TestMajorCompaction.class.getName());

    private static final HBaseTestingUtility UTIL = HBaseTestingUtility.createLocalHTU();

    protected Configuration conf = TestMajorCompaction.UTIL.getConfiguration();

    private HRegion r = null;

    private HTableDescriptor htd = null;

    private static final byte[] COLUMN_FAMILY = HBaseTestingUtility.fam1;

    private final byte[] STARTROW = Bytes.toBytes(HBaseTestingUtility.START_KEY);

    private static final byte[] COLUMN_FAMILY_TEXT = TestMajorCompaction.COLUMN_FAMILY;

    private int compactionThreshold;

    private byte[] secondRowBytes;

    private byte[] thirdRowBytes;

    private static final long MAX_FILES_TO_COMPACT = 10;

    /**
     * constructor
     */
    public TestMajorCompaction(String compType) {
        super();
        name = new TestName();
        // Set cache flush size to 1MB
        conf.setInt(HREGION_MEMSTORE_FLUSH_SIZE, (1024 * 1024));
        conf.setInt(HREGION_MEMSTORE_BLOCK_MULTIPLIER, 100);
        compactionThreshold = conf.getInt("hbase.hstore.compactionThreshold", 3);
        conf.set(COMPACTING_MEMSTORE_TYPE_KEY, String.valueOf(compType));
        secondRowBytes = HBaseTestingUtility.START_KEY_BYTES.clone();
        // Increment the least significant character so we get to next row.
        (secondRowBytes[((HBaseTestingUtility.START_KEY_BYTES.length) - 1)])++;
        thirdRowBytes = HBaseTestingUtility.START_KEY_BYTES.clone();
        thirdRowBytes[((HBaseTestingUtility.START_KEY_BYTES.length) - 1)] = ((byte) ((thirdRowBytes[((HBaseTestingUtility.START_KEY_BYTES.length) - 1)]) + 2));
    }

    /**
     * Test that on a major compaction, if all cells are expired or deleted, then
     * we'll end up with no product.  Make sure scanner over region returns
     * right answer in this case - and that it just basically works.
     *
     * @throws IOException
     * 		exception encountered
     */
    @Test
    public void testMajorCompactingToNoOutput() throws IOException {
        testMajorCompactingWithDeletes(FALSE);
    }

    /**
     * Test that on a major compaction,Deleted cells are retained if keep deleted cells is set to true
     *
     * @throws IOException
     * 		exception encountered
     */
    @Test
    public void testMajorCompactingWithKeepDeletedCells() throws IOException {
        testMajorCompactingWithDeletes(TRUE);
    }

    /**
     * Run compaction and flushing memstore
     * Assert deletes get cleaned up.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testMajorCompaction() throws Exception {
        majorCompaction();
    }

    @Test
    public void testDataBlockEncodingInCacheOnly() throws Exception {
        majorCompactionWithDataBlockEncoding(true);
    }

    @Test
    public void testDataBlockEncodingEverywhere() throws Exception {
        majorCompactionWithDataBlockEncoding(false);
    }

    @Test
    public void testTimeBasedMajorCompaction() throws Exception {
        // create 2 storefiles and force a major compaction to reset the time
        int delay = 10 * 1000;// 10 sec

        float jitterPct = 0.2F;// 20%

        conf.setLong(MAJOR_COMPACTION_PERIOD, delay);
        conf.setFloat("hbase.hregion.majorcompaction.jitter", jitterPct);
        HStore s = ((HStore) (r.getStore(TestMajorCompaction.COLUMN_FAMILY)));
        s.storeEngine.getCompactionPolicy().setConf(conf);
        try {
            createStoreFile(r);
            createStoreFile(r);
            r.compact(true);
            // add one more file & verify that a regular compaction won't work
            createStoreFile(r);
            r.compact(false);
            Assert.assertEquals(2, s.getStorefilesCount());
            // ensure that major compaction time is deterministic
            RatioBasedCompactionPolicy c = ((RatioBasedCompactionPolicy) (s.storeEngine.getCompactionPolicy()));
            Collection<HStoreFile> storeFiles = s.getStorefiles();
            long mcTime = c.getNextMajorCompactTime(storeFiles);
            for (int i = 0; i < 10; ++i) {
                Assert.assertEquals(mcTime, c.getNextMajorCompactTime(storeFiles));
            }
            // ensure that the major compaction time is within the variance
            long jitter = Math.round((delay * jitterPct));
            Assert.assertTrue((((delay - jitter) <= mcTime) && (mcTime <= (delay + jitter))));
            // wait until the time-based compaction interval
            Thread.sleep(mcTime);
            // trigger a compaction request and ensure that it's upgraded to major
            r.compact(false);
            Assert.assertEquals(1, s.getStorefilesCount());
        } finally {
            // reset the timed compaction settings
            conf.setLong(MAJOR_COMPACTION_PERIOD, (((1000 * 60) * 60) * 24));
            conf.setFloat("hbase.hregion.majorcompaction.jitter", 0.2F);
            // run a major to reset the cache
            createStoreFile(r);
            r.compact(true);
            Assert.assertEquals(1, s.getStorefilesCount());
        }
    }

    /**
     * Test for HBASE-5920 - Test user requested major compactions always occurring
     */
    @Test
    public void testNonUserMajorCompactionRequest() throws Exception {
        HStore store = r.getStore(TestMajorCompaction.COLUMN_FAMILY);
        createStoreFile(r);
        for (int i = 0; i < ((TestMajorCompaction.MAX_FILES_TO_COMPACT) + 1); i++) {
            createStoreFile(r);
        }
        store.triggerMajorCompaction();
        CompactionRequestImpl request = store.requestCompaction().get().getRequest();
        Assert.assertNotNull("Expected to receive a compaction request", request);
        Assert.assertEquals("System-requested major compaction should not occur if there are too many store files", false, request.isMajor());
    }

    /**
     * Test for HBASE-5920
     */
    @Test
    public void testUserMajorCompactionRequest() throws IOException {
        HStore store = r.getStore(TestMajorCompaction.COLUMN_FAMILY);
        createStoreFile(r);
        for (int i = 0; i < ((TestMajorCompaction.MAX_FILES_TO_COMPACT) + 1); i++) {
            createStoreFile(r);
        }
        store.triggerMajorCompaction();
        CompactionRequestImpl request = store.requestCompaction(Store.PRIORITY_USER, DUMMY, null).get().getRequest();
        Assert.assertNotNull("Expected to receive a compaction request", request);
        Assert.assertEquals("User-requested major compaction should always occur, even if there are too many store files", true, request.isMajor());
    }

    /**
     * Test that on a major compaction, if all cells are expired or deleted, then we'll end up with no
     * product. Make sure scanner over region returns right answer in this case - and that it just
     * basically works.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testMajorCompactingToNoOutputWithReverseScan() throws IOException {
        createStoreFile(r);
        for (int i = 0; i < (compactionThreshold); i++) {
            createStoreFile(r);
        }
        // Now delete everything.
        Scan scan = new Scan();
        scan.setReversed(true);
        InternalScanner s = r.getScanner(scan);
        do {
            List<Cell> results = new ArrayList<>();
            boolean result = s.next(results);
            Assert.assertTrue((!(results.isEmpty())));
            r.delete(new org.apache.hadoop.hbase.client.Delete(CellUtil.cloneRow(results.get(0))));
            if (!result) {
                break;
            }
        } while (true );
        s.close();
        // Flush
        r.flush(true);
        // Major compact.
        r.compact(true);
        scan = new Scan();
        scan.setReversed(true);
        s = r.getScanner(scan);
        int counter = 0;
        do {
            List<Cell> results = new ArrayList<>();
            boolean result = s.next(results);
            if (!result) {
                break;
            }
            counter++;
        } while (true );
        s.close();
        Assert.assertEquals(0, counter);
    }
}

