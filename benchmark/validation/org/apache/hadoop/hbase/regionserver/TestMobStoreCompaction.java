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


import KeyValue.Type.DeleteFamily;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.FSUtils;
import org.apache.hadoop.hbase.util.Pair;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test mob store compaction
 */
@Category(MediumTests.class)
public class TestMobStoreCompaction {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestMobStoreCompaction.class);

    @Rule
    public TestName name = new TestName();

    static final Logger LOG = LoggerFactory.getLogger(TestMobStoreCompaction.class.getName());

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private Configuration conf = null;

    private HRegion region = null;

    private HTableDescriptor htd = null;

    private HColumnDescriptor hcd = null;

    private long mobCellThreshold = 1000;

    private FileSystem fs;

    private static final byte[] COLUMN_FAMILY = HBaseTestingUtility.fam1;

    private final byte[] STARTROW = Bytes.toBytes(HBaseTestingUtility.START_KEY);

    private int compactionThreshold;

    /**
     * During compaction, cells smaller than the threshold won't be affected.
     */
    @Test
    public void testSmallerValue() throws Exception {
        init(TestMobStoreCompaction.UTIL.getConfiguration(), 500);
        byte[] dummyData = makeDummyData(300);// smaller than mob threshold

        Table loader = new RegionAsTable(region);
        // one hfile per row
        for (int i = 0; i < (compactionThreshold); i++) {
            Put p = createPut(i, dummyData);
            loader.put(p);
            region.flush(true);
        }
        Assert.assertEquals("Before compaction: store files", compactionThreshold, countStoreFiles());
        Assert.assertEquals("Before compaction: mob file count", 0, countMobFiles());
        Assert.assertEquals("Before compaction: rows", compactionThreshold, TestMobStoreCompaction.UTIL.countRows(region));
        Assert.assertEquals("Before compaction: mob rows", 0, countMobRows());
        region.compactStores();
        Assert.assertEquals("After compaction: store files", 1, countStoreFiles());
        Assert.assertEquals("After compaction: mob file count", 0, countMobFiles());
        Assert.assertEquals("After compaction: referenced mob file count", 0, countReferencedMobFiles());
        Assert.assertEquals("After compaction: rows", compactionThreshold, TestMobStoreCompaction.UTIL.countRows(region));
        Assert.assertEquals("After compaction: mob rows", 0, countMobRows());
    }

    /**
     * During compaction, the mob threshold size is changed.
     */
    @Test
    public void testLargerValue() throws Exception {
        init(TestMobStoreCompaction.UTIL.getConfiguration(), 200);
        byte[] dummyData = makeDummyData(300);// larger than mob threshold

        Table loader = new RegionAsTable(region);
        for (int i = 0; i < (compactionThreshold); i++) {
            Put p = createPut(i, dummyData);
            loader.put(p);
            region.flush(true);
        }
        Assert.assertEquals("Before compaction: store files", compactionThreshold, countStoreFiles());
        Assert.assertEquals("Before compaction: mob file count", compactionThreshold, countMobFiles());
        Assert.assertEquals("Before compaction: rows", compactionThreshold, TestMobStoreCompaction.UTIL.countRows(region));
        Assert.assertEquals("Before compaction: mob rows", compactionThreshold, countMobRows());
        Assert.assertEquals("Before compaction: number of mob cells", compactionThreshold, countMobCellsInMetadata());
        // Change the threshold larger than the data size
        TestMobStoreCompaction.setMobThreshold(region, TestMobStoreCompaction.COLUMN_FAMILY, 500);
        region.initialize();
        region.compactStores();
        Assert.assertEquals("After compaction: store files", 1, countStoreFiles());
        Assert.assertEquals("After compaction: mob file count", compactionThreshold, countMobFiles());
        Assert.assertEquals("After compaction: referenced mob file count", 0, countReferencedMobFiles());
        Assert.assertEquals("After compaction: rows", compactionThreshold, TestMobStoreCompaction.UTIL.countRows(region));
        Assert.assertEquals("After compaction: mob rows", 0, countMobRows());
    }

    /**
     * This test will first generate store files, then bulk load them and trigger the compaction.
     * When compaction, the cell value will be larger than the threshold.
     */
    @Test
    public void testMobCompactionWithBulkload() throws Exception {
        // The following will produce store files of 600.
        init(TestMobStoreCompaction.UTIL.getConfiguration(), 300);
        byte[] dummyData = makeDummyData(600);
        Path hbaseRootDir = FSUtils.getRootDir(conf);
        Path basedir = new Path(hbaseRootDir, htd.getNameAsString());
        List<Pair<byte[], String>> hfiles = new ArrayList<>(1);
        for (int i = 0; i < (compactionThreshold); i++) {
            Path hpath = new Path(basedir, ("hfile" + i));
            hfiles.add(Pair.newPair(TestMobStoreCompaction.COLUMN_FAMILY, hpath.toString()));
            createHFile(hpath, i, dummyData);
        }
        // The following will bulk load the above generated store files and compact, with 600(fileSize)
        // > 300(threshold)
        Map<byte[], List<Path>> map = region.bulkLoadHFiles(hfiles, true, null);
        Assert.assertTrue("Bulkload result:", (!(map.isEmpty())));
        Assert.assertEquals("Before compaction: store files", compactionThreshold, countStoreFiles());
        Assert.assertEquals("Before compaction: mob file count", 0, countMobFiles());
        Assert.assertEquals("Before compaction: rows", compactionThreshold, TestMobStoreCompaction.UTIL.countRows(region));
        Assert.assertEquals("Before compaction: mob rows", 0, countMobRows());
        Assert.assertEquals("Before compaction: referenced mob file count", 0, countReferencedMobFiles());
        region.compactStores();
        Assert.assertEquals("After compaction: store files", 1, countStoreFiles());
        Assert.assertEquals("After compaction: mob file count:", 1, countMobFiles());
        Assert.assertEquals("After compaction: rows", compactionThreshold, TestMobStoreCompaction.UTIL.countRows(region));
        Assert.assertEquals("After compaction: mob rows", compactionThreshold, countMobRows());
        Assert.assertEquals("After compaction: referenced mob file count", 1, countReferencedMobFiles());
        Assert.assertEquals("After compaction: number of mob cells", compactionThreshold, countMobCellsInMetadata());
    }

    @Test
    public void testMajorCompactionAfterDelete() throws Exception {
        init(TestMobStoreCompaction.UTIL.getConfiguration(), 100);
        byte[] dummyData = makeDummyData(200);// larger than mob threshold

        Table loader = new RegionAsTable(region);
        // create hfiles and mob hfiles but don't trigger compaction
        int numHfiles = (compactionThreshold) - 1;
        byte[] deleteRow = Bytes.add(STARTROW, Bytes.toBytes(0));
        for (int i = 0; i < numHfiles; i++) {
            Put p = createPut(i, dummyData);
            loader.put(p);
            region.flush(true);
        }
        Assert.assertEquals("Before compaction: store files", numHfiles, countStoreFiles());
        Assert.assertEquals("Before compaction: mob file count", numHfiles, countMobFiles());
        Assert.assertEquals("Before compaction: rows", numHfiles, TestMobStoreCompaction.UTIL.countRows(region));
        Assert.assertEquals("Before compaction: mob rows", numHfiles, countMobRows());
        Assert.assertEquals("Before compaction: number of mob cells", numHfiles, countMobCellsInMetadata());
        // now let's delete some cells that contain mobs
        Delete delete = new Delete(deleteRow);
        delete.addFamily(TestMobStoreCompaction.COLUMN_FAMILY);
        region.delete(delete);
        region.flush(true);
        Assert.assertEquals("Before compaction: store files", (numHfiles + 1), countStoreFiles());
        Assert.assertEquals("Before compaction: mob files", numHfiles, countMobFiles());
        // region.compactStores();
        region.compact(true);
        Assert.assertEquals("After compaction: store files", 1, countStoreFiles());
        // still have original mob hfiles and now added a mob del file
        Assert.assertEquals("After compaction: mob files", (numHfiles + 1), countMobFiles());
        Scan scan = new Scan();
        scan.setRaw(true);
        InternalScanner scanner = region.getScanner(scan);
        List<Cell> results = new ArrayList<>();
        scanner.next(results);
        int deleteCount = 0;
        while (!(results.isEmpty())) {
            for (Cell c : results) {
                if ((c.getTypeByte()) == (DeleteFamily.getCode())) {
                    deleteCount++;
                    Assert.assertTrue(Bytes.equals(CellUtil.cloneRow(c), deleteRow));
                }
            }
            results.clear();
            scanner.next(results);
        } 
        // assert the delete mark is retained after the major compaction
        Assert.assertEquals(1, deleteCount);
        scanner.close();
        // assert the deleted cell is not counted
        Assert.assertEquals("The cells in mob files", (numHfiles - 1), countMobCellsInMobFiles(1));
    }
}

