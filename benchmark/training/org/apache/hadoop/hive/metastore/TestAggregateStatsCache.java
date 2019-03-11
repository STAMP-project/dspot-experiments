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
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.hadoop.hive.metastore;


import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.hive.metastore.AggregateStatsCache.AggrColStats;
import org.apache.hadoop.hive.metastore.AggregateStatsCache.Key;
import org.apache.hadoop.hive.metastore.annotation.MetastoreUnitTest;
import org.apache.hadoop.hive.metastore.api.ColumnStatisticsObj;
import org.apache.hive.common.util.BloomFilter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(MetastoreUnitTest.class)
public class TestAggregateStatsCache {
    static String DB_NAME = "db";

    static String TAB_PREFIX = "tab";

    static String PART_PREFIX = "part";

    static String COL_PREFIX = "col";

    static int NUM_TABS = 2;

    static int NUM_PARTS = 20;

    static int NUM_COLS = 5;

    static int MAX_CACHE_NODES = 10;

    static int MAX_PARTITIONS_PER_CACHE_NODE = 10;

    static long TIME_TO_LIVE = 2;

    static long MAX_WRITER_WAIT = 1;

    static long MAX_READER_WAIT = 1;

    static double FALSE_POSITIVE_PROBABILITY = 0.01;

    static double MAX_VARIANCE = 0.5;

    static AggregateStatsCache cache;

    static List<String> tables = new ArrayList<>();

    static List<String> tabParts = new ArrayList<>();

    static List<String> tabCols = new ArrayList<>();

    @Test
    public void testCacheKey() {
        Key k1 = new Key("cat", "db", "tbl1", "col");
        Key k2 = new Key("cat", "db", "tbl1", "col");
        // k1 equals k2
        Assert.assertEquals(k1, k2);
        Key k3 = new Key("cat", "db", "tbl2", "col");
        // k1 not equals k3
        Assert.assertNotEquals(k1, k3);
    }

    @Test
    public void testBasicAddAndGet() throws Exception {
        // Partnames: [tab1part1...tab1part9]
        List<String> partNames = preparePartNames(TestAggregateStatsCache.tables.get(0), 1, 9);
        // Prepare the bloom filter
        BloomFilter bloomFilter = prepareBloomFilter(partNames);
        // Add a dummy aggregate stats object for the above parts (part1...part9) of tab1 for col1
        String tblName = TestAggregateStatsCache.tables.get(0);
        String colName = TestAggregateStatsCache.tabCols.get(0);
        int highVal = 100;
        int lowVal = 10;
        int numDVs = 50;
        int numNulls = 5;
        // We'll treat this as the aggregate col stats for part1...part9 of tab1, col1
        ColumnStatisticsObj aggrColStats = getDummyLongColStat(colName, highVal, lowVal, numDVs, numNulls);
        // Now add to cache the dummy colstats for these 10 partitions
        TestAggregateStatsCache.cache.add(Warehouse.DEFAULT_CATALOG_NAME, TestAggregateStatsCache.DB_NAME, tblName, colName, 10, aggrColStats, bloomFilter);
        // Now get from cache
        AggrColStats aggrStatsCached = TestAggregateStatsCache.cache.get(Warehouse.DEFAULT_CATALOG_NAME, TestAggregateStatsCache.DB_NAME, tblName, colName, partNames);
        Assert.assertNotNull(aggrStatsCached);
        ColumnStatisticsObj aggrColStatsCached = aggrStatsCached.getColStats();
        Assert.assertEquals(aggrColStats, aggrColStatsCached);
        // Now get a non-existant entry
        aggrStatsCached = TestAggregateStatsCache.cache.get(Warehouse.DEFAULT_CATALOG_NAME, "dbNotThere", tblName, colName, partNames);
        Assert.assertNull(aggrStatsCached);
    }

    @Test
    public void testAddGetWithVariance() throws Exception {
        // Partnames: [tab1part1...tab1part9]
        List<String> partNames = preparePartNames(TestAggregateStatsCache.tables.get(0), 1, 9);
        // Prepare the bloom filter
        BloomFilter bloomFilter = prepareBloomFilter(partNames);
        // Add a dummy aggregate stats object for the above parts (part1...part9) of tab1 for col1
        String tblName = TestAggregateStatsCache.tables.get(0);
        String colName = TestAggregateStatsCache.tabCols.get(0);
        int highVal = 100;
        int lowVal = 10;
        int numDVs = 50;
        int numNulls = 5;
        // We'll treat this as the aggregate col stats for part1...part9 of tab1, col1
        ColumnStatisticsObj aggrColStats = getDummyLongColStat(colName, highVal, lowVal, numDVs, numNulls);
        // Now add to cache
        TestAggregateStatsCache.cache.add(Warehouse.DEFAULT_CATALOG_NAME, TestAggregateStatsCache.DB_NAME, tblName, colName, 10, aggrColStats, bloomFilter);
        // Now prepare partnames with only 5 partitions: [tab1part1...tab1part5]
        partNames = preparePartNames(TestAggregateStatsCache.tables.get(0), 1, 5);
        // This get should fail because its variance ((10-5)/5) is way past MAX_VARIANCE (0.5)
        AggrColStats aggrStatsCached = TestAggregateStatsCache.cache.get(Warehouse.DEFAULT_CATALOG_NAME, TestAggregateStatsCache.DB_NAME, tblName, colName, partNames);
        Assert.assertNull(aggrStatsCached);
        // Now prepare partnames with 10 partitions: [tab1part11...tab1part20], but with no overlap
        partNames = preparePartNames(TestAggregateStatsCache.tables.get(0), 11, 20);
        // This get should fail because its variance ((10-0)/10) is way past MAX_VARIANCE (0.5)
        aggrStatsCached = TestAggregateStatsCache.cache.get(Warehouse.DEFAULT_CATALOG_NAME, TestAggregateStatsCache.DB_NAME, tblName, colName, partNames);
        Assert.assertNull(aggrStatsCached);
        // Now prepare partnames with 9 partitions: [tab1part1...tab1part8], which are contained in the
        // object that we added to the cache
        partNames = preparePartNames(TestAggregateStatsCache.tables.get(0), 1, 8);
        // This get should succeed because its variance ((10-9)/9) is within past MAX_VARIANCE (0.5)
        aggrStatsCached = TestAggregateStatsCache.cache.get(Warehouse.DEFAULT_CATALOG_NAME, TestAggregateStatsCache.DB_NAME, tblName, colName, partNames);
        Assert.assertNotNull(aggrStatsCached);
        ColumnStatisticsObj aggrColStatsCached = aggrStatsCached.getColStats();
        Assert.assertEquals(aggrColStats, aggrColStatsCached);
    }

    @Test
    public void testTimeToLive() throws Exception {
        // Add a dummy node to cache
        // Partnames: [tab1part1...tab1part9]
        List<String> partNames = preparePartNames(TestAggregateStatsCache.tables.get(0), 1, 9);
        // Prepare the bloom filter
        BloomFilter bloomFilter = prepareBloomFilter(partNames);
        // Add a dummy aggregate stats object for the above parts (part1...part9) of tab1 for col1
        String tblName = TestAggregateStatsCache.tables.get(0);
        String colName = TestAggregateStatsCache.tabCols.get(0);
        int highVal = 100;
        int lowVal = 10;
        int numDVs = 50;
        int numNulls = 5;
        // We'll treat this as the aggregate col stats for part1...part9 of tab1, col1
        ColumnStatisticsObj aggrColStats = getDummyLongColStat(colName, highVal, lowVal, numDVs, numNulls);
        // Now add to cache
        TestAggregateStatsCache.cache.add(Warehouse.DEFAULT_CATALOG_NAME, TestAggregateStatsCache.DB_NAME, tblName, colName, 10, aggrColStats, bloomFilter);
        // Sleep for 3 seconds
        Thread.sleep(3000);
        // Get should fail now (since TTL is 2s) and we've snoozed for 3 seconds
        AggrColStats aggrStatsCached = TestAggregateStatsCache.cache.get(Warehouse.DEFAULT_CATALOG_NAME, TestAggregateStatsCache.DB_NAME, tblName, colName, partNames);
        Assert.assertNull(aggrStatsCached);
    }
}

