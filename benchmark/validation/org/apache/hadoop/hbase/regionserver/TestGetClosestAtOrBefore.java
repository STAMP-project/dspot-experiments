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


import Durability.SKIP_WAL;
import HConstants.EMPTY_BYTE_ARRAY;
import HConstants.ZEROES;
import HRegionInfo.FIRST_META_REGIONINFO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.HRegionInfo;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.MetaTableAccessor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.TableDescriptorBuilder;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.EnvironmentEdgeManager;
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
 * TestGet is a medley of tests of get all done up as a single test.
 * This class
 */
@Category({ RegionServerTests.class, MediumTests.class })
public class TestGetClosestAtOrBefore {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestGetClosestAtOrBefore.class);

    @Rule
    public TestName testName = new TestName();

    private static final Logger LOG = LoggerFactory.getLogger(TestGetClosestAtOrBefore.class);

    private static final byte[] T00 = Bytes.toBytes("000");

    private static final byte[] T10 = Bytes.toBytes("010");

    private static final byte[] T11 = Bytes.toBytes("011");

    private static final byte[] T12 = Bytes.toBytes("012");

    private static final byte[] T20 = Bytes.toBytes("020");

    private static final byte[] T30 = Bytes.toBytes("030");

    private static final byte[] T31 = Bytes.toBytes("031");

    private static final byte[] T35 = Bytes.toBytes("035");

    private static final byte[] T40 = Bytes.toBytes("040");

    private static HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static Configuration conf = TestGetClosestAtOrBefore.UTIL.getConfiguration();

    @Test
    public void testUsingMetaAndBinary() throws IOException {
        FileSystem filesystem = FileSystem.get(TestGetClosestAtOrBefore.conf);
        Path rootdir = TestGetClosestAtOrBefore.UTIL.getDataTestDirOnTestFS();
        // Up flush size else we bind up when we use default catalog flush of 16k.
        TableDescriptorBuilder metaBuilder = TestGetClosestAtOrBefore.UTIL.getMetaTableDescriptorBuilder().setMemStoreFlushSize(((64 * 1024) * 1024));
        HRegion mr = HBaseTestingUtility.createRegionAndWAL(FIRST_META_REGIONINFO, rootdir, this.conf, metaBuilder.build());
        try {
            // Write rows for three tables 'A', 'B', and 'C'.
            for (char c = 'A'; c < 'D'; c++) {
                HTableDescriptor htd = new HTableDescriptor(TableName.valueOf(("" + c)));
                final int last = 128;
                final int interval = 2;
                for (int i = 0; i <= last; i += interval) {
                    HRegionInfo hri = new HRegionInfo(htd.getTableName(), (i == 0 ? HConstants.EMPTY_BYTE_ARRAY : Bytes.toBytes(((byte) (i)))), (i == last ? HConstants.EMPTY_BYTE_ARRAY : Bytes.toBytes((((byte) (i)) + interval))));
                    Put put = MetaTableAccessor.makePutFromRegionInfo(hri, EnvironmentEdgeManager.currentTime());
                    put.setDurability(SKIP_WAL);
                    mr.put(put);
                }
            }
            InternalScanner s = mr.getScanner(new Scan());
            try {
                List<Cell> keys = new ArrayList<>();
                while (s.next(keys)) {
                    TestGetClosestAtOrBefore.LOG.info(Objects.toString(keys));
                    keys.clear();
                } 
            } finally {
                s.close();
            }
            findRow(mr, 'C', 44, 44);
            findRow(mr, 'C', 45, 44);
            findRow(mr, 'C', 46, 46);
            findRow(mr, 'C', 43, 42);
            mr.flush(true);
            findRow(mr, 'C', 44, 44);
            findRow(mr, 'C', 45, 44);
            findRow(mr, 'C', 46, 46);
            findRow(mr, 'C', 43, 42);
            // Now delete 'C' and make sure I don't get entries from 'B'.
            byte[] firstRowInC = HRegionInfo.createRegionName(TableName.valueOf(("" + 'C')), EMPTY_BYTE_ARRAY, ZEROES, false);
            Scan scan = new Scan(firstRowInC);
            s = mr.getScanner(scan);
            try {
                List<Cell> keys = new ArrayList<>();
                while (s.next(keys)) {
                    mr.delete(new Delete(CellUtil.cloneRow(keys.get(0))));
                    keys.clear();
                } 
            } finally {
                s.close();
            }
            // Assert we get null back (pass -1).
            findRow(mr, 'C', 44, (-1));
            findRow(mr, 'C', 45, (-1));
            findRow(mr, 'C', 46, (-1));
            findRow(mr, 'C', 43, (-1));
            mr.flush(true);
            findRow(mr, 'C', 44, (-1));
            findRow(mr, 'C', 45, (-1));
            findRow(mr, 'C', 46, (-1));
            findRow(mr, 'C', 43, (-1));
        } finally {
            HBaseTestingUtility.closeRegionAndWAL(mr);
        }
    }

    /**
     * Test file of multiple deletes and with deletes as final key.
     *
     * @see <a href="https://issues.apache.org/jira/browse/HBASE-751">HBASE-751</a>
     */
    @Test
    public void testGetClosestRowBefore3() throws IOException {
        HRegion region = null;
        byte[] c0 = TestGetClosestAtOrBefore.UTIL.COLUMNS[0];
        byte[] c1 = TestGetClosestAtOrBefore.UTIL.COLUMNS[1];
        try {
            TableName tn = TableName.valueOf(testName.getMethodName());
            HTableDescriptor htd = TestGetClosestAtOrBefore.UTIL.createTableDescriptor(tn);
            region = TestGetClosestAtOrBefore.UTIL.createLocalHRegion(htd, null, null);
            Put p = new Put(TestGetClosestAtOrBefore.T00);
            p.addColumn(c0, c0, TestGetClosestAtOrBefore.T00);
            region.put(p);
            p = new Put(TestGetClosestAtOrBefore.T10);
            p.addColumn(c0, c0, TestGetClosestAtOrBefore.T10);
            region.put(p);
            p = new Put(TestGetClosestAtOrBefore.T20);
            p.addColumn(c0, c0, TestGetClosestAtOrBefore.T20);
            region.put(p);
            Result r = TestGetClosestAtOrBefore.UTIL.getClosestRowBefore(region, TestGetClosestAtOrBefore.T20, c0);
            Assert.assertTrue(Bytes.equals(TestGetClosestAtOrBefore.T20, r.getRow()));
            Delete d = new Delete(TestGetClosestAtOrBefore.T20);
            d.addColumn(c0, c0);
            region.delete(d);
            r = TestGetClosestAtOrBefore.UTIL.getClosestRowBefore(region, TestGetClosestAtOrBefore.T20, c0);
            Assert.assertTrue(Bytes.equals(TestGetClosestAtOrBefore.T10, r.getRow()));
            p = new Put(TestGetClosestAtOrBefore.T30);
            p.addColumn(c0, c0, TestGetClosestAtOrBefore.T30);
            region.put(p);
            r = TestGetClosestAtOrBefore.UTIL.getClosestRowBefore(region, TestGetClosestAtOrBefore.T30, c0);
            Assert.assertTrue(Bytes.equals(TestGetClosestAtOrBefore.T30, r.getRow()));
            d = new Delete(TestGetClosestAtOrBefore.T30);
            d.addColumn(c0, c0);
            region.delete(d);
            r = TestGetClosestAtOrBefore.UTIL.getClosestRowBefore(region, TestGetClosestAtOrBefore.T30, c0);
            Assert.assertTrue(Bytes.equals(TestGetClosestAtOrBefore.T10, r.getRow()));
            r = TestGetClosestAtOrBefore.UTIL.getClosestRowBefore(region, TestGetClosestAtOrBefore.T31, c0);
            Assert.assertTrue(Bytes.equals(TestGetClosestAtOrBefore.T10, r.getRow()));
            region.flush(true);
            // try finding "010" after flush
            r = TestGetClosestAtOrBefore.UTIL.getClosestRowBefore(region, TestGetClosestAtOrBefore.T30, c0);
            Assert.assertTrue(Bytes.equals(TestGetClosestAtOrBefore.T10, r.getRow()));
            r = TestGetClosestAtOrBefore.UTIL.getClosestRowBefore(region, TestGetClosestAtOrBefore.T31, c0);
            Assert.assertTrue(Bytes.equals(TestGetClosestAtOrBefore.T10, r.getRow()));
            // Put into a different column family.  Should make it so I still get t10
            p = new Put(TestGetClosestAtOrBefore.T20);
            p.addColumn(c1, c1, TestGetClosestAtOrBefore.T20);
            region.put(p);
            r = TestGetClosestAtOrBefore.UTIL.getClosestRowBefore(region, TestGetClosestAtOrBefore.T30, c0);
            Assert.assertTrue(Bytes.equals(TestGetClosestAtOrBefore.T10, r.getRow()));
            r = TestGetClosestAtOrBefore.UTIL.getClosestRowBefore(region, TestGetClosestAtOrBefore.T31, c0);
            Assert.assertTrue(Bytes.equals(TestGetClosestAtOrBefore.T10, r.getRow()));
            region.flush(true);
            r = TestGetClosestAtOrBefore.UTIL.getClosestRowBefore(region, TestGetClosestAtOrBefore.T30, c0);
            Assert.assertTrue(Bytes.equals(TestGetClosestAtOrBefore.T10, r.getRow()));
            r = TestGetClosestAtOrBefore.UTIL.getClosestRowBefore(region, TestGetClosestAtOrBefore.T31, c0);
            Assert.assertTrue(Bytes.equals(TestGetClosestAtOrBefore.T10, r.getRow()));
            // Now try combo of memcache and mapfiles.  Delete the t20 COLUMS[1]
            // in memory; make sure we get back t10 again.
            d = new Delete(TestGetClosestAtOrBefore.T20);
            d.addColumn(c1, c1);
            region.delete(d);
            r = TestGetClosestAtOrBefore.UTIL.getClosestRowBefore(region, TestGetClosestAtOrBefore.T30, c0);
            Assert.assertTrue(Bytes.equals(TestGetClosestAtOrBefore.T10, r.getRow()));
            // Ask for a value off the end of the file.  Should return t10.
            r = TestGetClosestAtOrBefore.UTIL.getClosestRowBefore(region, TestGetClosestAtOrBefore.T31, c0);
            Assert.assertTrue(Bytes.equals(TestGetClosestAtOrBefore.T10, r.getRow()));
            region.flush(true);
            r = TestGetClosestAtOrBefore.UTIL.getClosestRowBefore(region, TestGetClosestAtOrBefore.T31, c0);
            Assert.assertTrue(Bytes.equals(TestGetClosestAtOrBefore.T10, r.getRow()));
            // Ok.  Let the candidate come out of hfile but have delete of
            // the candidate be in memory.
            p = new Put(TestGetClosestAtOrBefore.T11);
            p.addColumn(c0, c0, TestGetClosestAtOrBefore.T11);
            region.put(p);
            d = new Delete(TestGetClosestAtOrBefore.T10);
            d.addColumn(c1, c1);
            r = TestGetClosestAtOrBefore.UTIL.getClosestRowBefore(region, TestGetClosestAtOrBefore.T12, c0);
            Assert.assertTrue(Bytes.equals(TestGetClosestAtOrBefore.T11, r.getRow()));
        } finally {
            if (region != null) {
                try {
                    WAL wal = region.getWAL();
                    region.close();
                    wal.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * For HBASE-694
     */
    @Test
    public void testGetClosestRowBefore2() throws IOException {
        HRegion region = null;
        byte[] c0 = TestGetClosestAtOrBefore.UTIL.COLUMNS[0];
        try {
            TableName tn = TableName.valueOf(testName.getMethodName());
            HTableDescriptor htd = TestGetClosestAtOrBefore.UTIL.createTableDescriptor(tn);
            region = TestGetClosestAtOrBefore.UTIL.createLocalHRegion(htd, null, null);
            Put p = new Put(TestGetClosestAtOrBefore.T10);
            p.addColumn(c0, c0, TestGetClosestAtOrBefore.T10);
            region.put(p);
            p = new Put(TestGetClosestAtOrBefore.T30);
            p.addColumn(c0, c0, TestGetClosestAtOrBefore.T30);
            region.put(p);
            p = new Put(TestGetClosestAtOrBefore.T40);
            p.addColumn(c0, c0, TestGetClosestAtOrBefore.T40);
            region.put(p);
            // try finding "035"
            Result r = TestGetClosestAtOrBefore.UTIL.getClosestRowBefore(region, TestGetClosestAtOrBefore.T35, c0);
            Assert.assertTrue(Bytes.equals(TestGetClosestAtOrBefore.T30, r.getRow()));
            region.flush(true);
            // try finding "035"
            r = TestGetClosestAtOrBefore.UTIL.getClosestRowBefore(region, TestGetClosestAtOrBefore.T35, c0);
            Assert.assertTrue(Bytes.equals(TestGetClosestAtOrBefore.T30, r.getRow()));
            p = new Put(TestGetClosestAtOrBefore.T20);
            p.addColumn(c0, c0, TestGetClosestAtOrBefore.T20);
            region.put(p);
            // try finding "035"
            r = TestGetClosestAtOrBefore.UTIL.getClosestRowBefore(region, TestGetClosestAtOrBefore.T35, c0);
            Assert.assertTrue(Bytes.equals(TestGetClosestAtOrBefore.T30, r.getRow()));
            region.flush(true);
            // try finding "035"
            r = TestGetClosestAtOrBefore.UTIL.getClosestRowBefore(region, TestGetClosestAtOrBefore.T35, c0);
            Assert.assertTrue(Bytes.equals(TestGetClosestAtOrBefore.T30, r.getRow()));
        } finally {
            if (region != null) {
                try {
                    WAL wal = region.getWAL();
                    region.close();
                    wal.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

