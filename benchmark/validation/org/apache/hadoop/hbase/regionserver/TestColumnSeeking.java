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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HRegionInfo;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.KeyValueTestUtil;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Scan;
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
public class TestColumnSeeking {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestColumnSeeking.class);

    @Rule
    public TestName name = new TestName();

    private static final HBaseTestingUtility TEST_UTIL = HBaseTestingUtility.createLocalHTU();

    private static final Logger LOG = LoggerFactory.getLogger(TestColumnSeeking.class);

    @SuppressWarnings("unchecked")
    @Test
    public void testDuplicateVersions() throws IOException {
        String family = "Family";
        byte[] familyBytes = Bytes.toBytes("Family");
        TableName table = TableName.valueOf(name.getMethodName());
        HColumnDescriptor hcd = new HColumnDescriptor(familyBytes).setMaxVersions(1000);
        hcd.setMaxVersions(3);
        HTableDescriptor htd = new HTableDescriptor(table);
        htd.addFamily(hcd);
        HRegionInfo info = new HRegionInfo(table, null, null, false);
        // Set this so that the archiver writes to the temp dir as well.
        HRegion region = TestColumnSeeking.TEST_UTIL.createLocalHRegion(info, htd);
        try {
            List<String> rows = generateRandomWords(10, "row");
            List<String> allColumns = generateRandomWords(10, "column");
            List<String> values = generateRandomWords(100, "value");
            long maxTimestamp = 2;
            double selectPercent = 0.5;
            int numberOfTests = 5;
            double flushPercentage = 0.2;
            double minorPercentage = 0.2;
            double majorPercentage = 0.2;
            double putPercentage = 0.2;
            HashMap<String, KeyValue> allKVMap = new HashMap<>();
            HashMap<String, KeyValue>[] kvMaps = new HashMap[numberOfTests];
            ArrayList<String>[] columnLists = new ArrayList[numberOfTests];
            for (int i = 0; i < numberOfTests; i++) {
                kvMaps[i] = new HashMap();
                columnLists[i] = new ArrayList<>();
                for (String column : allColumns) {
                    if ((Math.random()) < selectPercent) {
                        columnLists[i].add(column);
                    }
                }
            }
            for (String value : values) {
                for (String row : rows) {
                    Put p = new Put(Bytes.toBytes(row));
                    p.setDurability(SKIP_WAL);
                    for (String column : allColumns) {
                        for (long timestamp = 1; timestamp <= maxTimestamp; timestamp++) {
                            KeyValue kv = KeyValueTestUtil.create(row, family, column, timestamp, value);
                            if ((Math.random()) < putPercentage) {
                                p.add(kv);
                                allKVMap.put(kv.getKeyString(), kv);
                                for (int i = 0; i < numberOfTests; i++) {
                                    if (columnLists[i].contains(column)) {
                                        kvMaps[i].put(kv.getKeyString(), kv);
                                    }
                                }
                            }
                        }
                    }
                    region.put(p);
                    if ((Math.random()) < flushPercentage) {
                        TestColumnSeeking.LOG.info("Flushing... ");
                        region.flush(true);
                    }
                    if ((Math.random()) < minorPercentage) {
                        TestColumnSeeking.LOG.info("Minor compacting... ");
                        region.compact(false);
                    }
                    if ((Math.random()) < majorPercentage) {
                        TestColumnSeeking.LOG.info("Major compacting... ");
                        region.compact(true);
                    }
                }
            }
            for (int i = 0; i < (numberOfTests + 1); i++) {
                Collection<KeyValue> kvSet;
                Scan scan = new Scan();
                scan.setMaxVersions();
                if (i < numberOfTests) {
                    if (columnLists[i].isEmpty())
                        continue;
                    // HBASE-7700

                    kvSet = kvMaps[i].values();
                    for (String column : columnLists[i]) {
                        scan.addColumn(familyBytes, Bytes.toBytes(column));
                    }
                    TestColumnSeeking.LOG.info("ExplicitColumns scanner");
                    TestColumnSeeking.LOG.info(((("Columns: " + (columnLists[i].size())) + "  Keys: ") + (kvSet.size())));
                } else {
                    kvSet = allKVMap.values();
                    TestColumnSeeking.LOG.info("Wildcard scanner");
                    TestColumnSeeking.LOG.info(((("Columns: " + (allColumns.size())) + "  Keys: ") + (kvSet.size())));
                }
                InternalScanner scanner = region.getScanner(scan);
                List<Cell> results = new ArrayList<>();
                while (scanner.next(results));
                Assert.assertEquals(kvSet.size(), results.size());
                Assert.assertTrue(KeyValueTestUtil.containsIgnoreMvccVersion(results, kvSet));
            }
        } finally {
            HBaseTestingUtility.closeRegionAndWAL(region);
        }
        HBaseTestingUtility.closeRegionAndWAL(region);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testReseeking() throws IOException {
        String family = "Family";
        byte[] familyBytes = Bytes.toBytes("Family");
        TableName table = TableName.valueOf(name.getMethodName());
        HTableDescriptor htd = new HTableDescriptor(table);
        HColumnDescriptor hcd = new HColumnDescriptor(family);
        hcd.setMaxVersions(3);
        htd.addFamily(hcd);
        HRegionInfo info = new HRegionInfo(table, null, null, false);
        HRegion region = TestColumnSeeking.TEST_UTIL.createLocalHRegion(info, htd);
        List<String> rows = generateRandomWords(10, "row");
        List<String> allColumns = generateRandomWords(100, "column");
        long maxTimestamp = 2;
        double selectPercent = 0.5;
        int numberOfTests = 5;
        double flushPercentage = 0.2;
        double minorPercentage = 0.2;
        double majorPercentage = 0.2;
        double putPercentage = 0.2;
        HashMap<String, KeyValue> allKVMap = new HashMap<>();
        HashMap<String, KeyValue>[] kvMaps = new HashMap[numberOfTests];
        ArrayList<String>[] columnLists = new ArrayList[numberOfTests];
        String valueString = "Value";
        for (int i = 0; i < numberOfTests; i++) {
            kvMaps[i] = new HashMap();
            columnLists[i] = new ArrayList<>();
            for (String column : allColumns) {
                if ((Math.random()) < selectPercent) {
                    columnLists[i].add(column);
                }
            }
        }
        for (String row : rows) {
            Put p = new Put(Bytes.toBytes(row));
            p.setDurability(SKIP_WAL);
            for (String column : allColumns) {
                for (long timestamp = 1; timestamp <= maxTimestamp; timestamp++) {
                    KeyValue kv = KeyValueTestUtil.create(row, family, column, timestamp, valueString);
                    if ((Math.random()) < putPercentage) {
                        p.add(kv);
                        allKVMap.put(kv.getKeyString(), kv);
                        for (int i = 0; i < numberOfTests; i++) {
                            if (columnLists[i].contains(column)) {
                                kvMaps[i].put(kv.getKeyString(), kv);
                            }
                        }
                    }
                }
            }
            region.put(p);
            if ((Math.random()) < flushPercentage) {
                TestColumnSeeking.LOG.info("Flushing... ");
                region.flush(true);
            }
            if ((Math.random()) < minorPercentage) {
                TestColumnSeeking.LOG.info("Minor compacting... ");
                region.compact(false);
            }
            if ((Math.random()) < majorPercentage) {
                TestColumnSeeking.LOG.info("Major compacting... ");
                region.compact(true);
            }
        }
        for (int i = 0; i < (numberOfTests + 1); i++) {
            Collection<KeyValue> kvSet;
            Scan scan = new Scan();
            scan.setMaxVersions();
            if (i < numberOfTests) {
                if (columnLists[i].isEmpty())
                    continue;
                // HBASE-7700

                kvSet = kvMaps[i].values();
                for (String column : columnLists[i]) {
                    scan.addColumn(familyBytes, Bytes.toBytes(column));
                }
                TestColumnSeeking.LOG.info("ExplicitColumns scanner");
                TestColumnSeeking.LOG.info(((("Columns: " + (columnLists[i].size())) + "  Keys: ") + (kvSet.size())));
            } else {
                kvSet = allKVMap.values();
                TestColumnSeeking.LOG.info("Wildcard scanner");
                TestColumnSeeking.LOG.info(((("Columns: " + (allColumns.size())) + "  Keys: ") + (kvSet.size())));
            }
            InternalScanner scanner = region.getScanner(scan);
            List<Cell> results = new ArrayList<>();
            while (scanner.next(results));
            Assert.assertEquals(kvSet.size(), results.size());
            Assert.assertTrue(KeyValueTestUtil.containsIgnoreMvccVersion(results, kvSet));
        }
        HBaseTestingUtility.closeRegionAndWAL(region);
    }
}

