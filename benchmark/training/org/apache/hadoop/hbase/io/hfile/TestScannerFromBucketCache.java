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
package org.apache.hadoop.hbase.io.hfile;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.ByteBufferKeyValue;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.PrivateCellUtil;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.regionserver.HRegion;
import org.apache.hadoop.hbase.regionserver.InternalScanner;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ RegionServerTests.class, MediumTests.class })
public class TestScannerFromBucketCache {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestScannerFromBucketCache.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestScannerFromBucketCache.class);

    @Rule
    public TestName name = new TestName();

    HRegion region = null;

    private HBaseTestingUtility test_util;

    public Configuration conf;

    private final int MAX_VERSIONS = 2;

    byte[] val = new byte[512 * 1024];

    // Test names
    private TableName tableName;

    @Test
    public void testBasicScanWithLRUCache() throws IOException {
        setUp(false);
        byte[] row1 = Bytes.toBytes("row1");
        byte[] qf1 = Bytes.toBytes("qualifier1");
        byte[] qf2 = Bytes.toBytes("qualifier2");
        byte[] fam1 = Bytes.toBytes("lrucache");
        long ts1 = 1;// System.currentTimeMillis();

        long ts2 = ts1 + 1;
        long ts3 = ts1 + 2;
        // Setting up region
        String method = this.getName();
        this.region = TestScannerFromBucketCache.initHRegion(tableName, method, conf, test_util, fam1);
        try {
            List<Cell> expected = insertData(row1, qf1, qf2, fam1, ts1, ts2, ts3, false);
            List<Cell> actual = performScan(row1, fam1);
            // Verify result
            for (int i = 0; i < (expected.size()); i++) {
                Assert.assertFalse(((actual.get(i)) instanceof ByteBufferKeyValue));
                Assert.assertTrue(PrivateCellUtil.equalsIgnoreMvccVersion(expected.get(i), actual.get(i)));
            }
            // do the scan again and verify. This time it should be from the lru cache
            actual = performScan(row1, fam1);
            // Verify result
            for (int i = 0; i < (expected.size()); i++) {
                Assert.assertFalse(((actual.get(i)) instanceof ByteBufferKeyValue));
                Assert.assertTrue(PrivateCellUtil.equalsIgnoreMvccVersion(expected.get(i), actual.get(i)));
            }
        } finally {
            HBaseTestingUtility.closeRegionAndWAL(this.region);
            this.region = null;
        }
    }

    @Test
    public void testBasicScanWithOffheapBucketCache() throws IOException {
        setUp(true);
        byte[] row1 = Bytes.toBytes("row1offheap");
        byte[] qf1 = Bytes.toBytes("qualifier1");
        byte[] qf2 = Bytes.toBytes("qualifier2");
        byte[] fam1 = Bytes.toBytes("famoffheap");
        long ts1 = 1;// System.currentTimeMillis();

        long ts2 = ts1 + 1;
        long ts3 = ts1 + 2;
        // Setting up region
        String method = this.getName();
        this.region = TestScannerFromBucketCache.initHRegion(tableName, method, conf, test_util, fam1);
        try {
            List<Cell> expected = insertData(row1, qf1, qf2, fam1, ts1, ts2, ts3, false);
            List<Cell> actual = performScan(row1, fam1);
            // Verify result
            for (int i = 0; i < (expected.size()); i++) {
                Assert.assertFalse(((actual.get(i)) instanceof ByteBufferKeyValue));
                Assert.assertTrue(PrivateCellUtil.equalsIgnoreMvccVersion(expected.get(i), actual.get(i)));
            }
            // Wait for the bucket cache threads to move the data to offheap
            Thread.sleep(500);
            // do the scan again and verify. This time it should be from the bucket cache in offheap mode
            actual = performScan(row1, fam1);
            // Verify result
            for (int i = 0; i < (expected.size()); i++) {
                Assert.assertTrue(((actual.get(i)) instanceof ByteBufferKeyValue));
                Assert.assertTrue(PrivateCellUtil.equalsIgnoreMvccVersion(expected.get(i), actual.get(i)));
            }
        } catch (InterruptedException e) {
        } finally {
            HBaseTestingUtility.closeRegionAndWAL(this.region);
            this.region = null;
        }
    }

    @Test
    public void testBasicScanWithOffheapBucketCacheWithMBB() throws IOException {
        setUp(true);
        byte[] row1 = Bytes.toBytes("row1offheap");
        byte[] qf1 = Bytes.toBytes("qualifier1");
        byte[] qf2 = Bytes.toBytes("qualifier2");
        byte[] fam1 = Bytes.toBytes("famoffheap");
        long ts1 = 1;// System.currentTimeMillis();

        long ts2 = ts1 + 1;
        long ts3 = ts1 + 2;
        // Setting up region
        String method = this.getName();
        this.region = TestScannerFromBucketCache.initHRegion(tableName, method, conf, test_util, fam1);
        try {
            List<Cell> expected = insertData(row1, qf1, qf2, fam1, ts1, ts2, ts3, true);
            List<Cell> actual = performScan(row1, fam1);
            // Verify result
            for (int i = 0; i < (expected.size()); i++) {
                Assert.assertFalse(((actual.get(i)) instanceof ByteBufferKeyValue));
                Assert.assertTrue(PrivateCellUtil.equalsIgnoreMvccVersion(expected.get(i), actual.get(i)));
            }
            // Wait for the bucket cache threads to move the data to offheap
            Thread.sleep(500);
            // do the scan again and verify. This time it should be from the bucket cache in offheap mode
            // but one of the cell will be copied due to the asSubByteBuff call
            Scan scan = new Scan().withStartRow(row1).addFamily(fam1).readVersions(10);
            actual = new ArrayList();
            InternalScanner scanner = region.getScanner(scan);
            boolean hasNext = scanner.next(actual);
            Assert.assertEquals(false, hasNext);
            // Verify result
            for (int i = 0; i < (expected.size()); i++) {
                if (i != 5) {
                    // the last cell fetched will be of type shareable but not offheap because
                    // the MBB is copied to form a single cell
                    Assert.assertTrue(((actual.get(i)) instanceof ByteBufferKeyValue));
                }
            }
        } catch (InterruptedException e) {
        } finally {
            HBaseTestingUtility.closeRegionAndWAL(this.region);
            this.region = null;
        }
    }
}

