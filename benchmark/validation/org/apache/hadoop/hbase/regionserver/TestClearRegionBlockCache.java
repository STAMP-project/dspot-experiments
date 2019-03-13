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


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.CacheEvictionStats;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.MiniHBaseCluster;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.AsyncAdmin;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.io.hfile.BlockCache;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category(MediumTests.class)
@RunWith(Parameterized.class)
public class TestClearRegionBlockCache {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestClearRegionBlockCache.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestClearRegionBlockCache.class);

    private static final TableName TABLE_NAME = TableName.valueOf("testClearRegionBlockCache");

    private static final byte[] FAMILY = Bytes.toBytes("family");

    private static final byte[][] SPLIT_KEY = new byte[][]{ Bytes.toBytes("5") };

    private static final int NUM_RS = 2;

    private final HBaseTestingUtility HTU = new HBaseTestingUtility();

    private Configuration CONF = HTU.getConfiguration();

    private Table table;

    private HRegionServer rs1;

    private HRegionServer rs2;

    private MiniHBaseCluster cluster;

    @Parameterized.Parameter
    public String cacheType;

    @Test
    public void testClearBlockCache() throws Exception {
        BlockCache blockCache1 = rs1.getBlockCache().get();
        BlockCache blockCache2 = rs2.getBlockCache().get();
        long initialBlockCount1 = blockCache1.getBlockCount();
        long initialBlockCount2 = blockCache2.getBlockCount();
        // scan will cause blocks to be added in BlockCache
        scanAllRegionsForRS(rs1);
        Assert.assertEquals(((blockCache1.getBlockCount()) - initialBlockCount1), HTU.getNumHFilesForRS(rs1, TestClearRegionBlockCache.TABLE_NAME, TestClearRegionBlockCache.FAMILY));
        clearRegionBlockCache(rs1);
        scanAllRegionsForRS(rs2);
        Assert.assertEquals(((blockCache2.getBlockCount()) - initialBlockCount2), HTU.getNumHFilesForRS(rs2, TestClearRegionBlockCache.TABLE_NAME, TestClearRegionBlockCache.FAMILY));
        clearRegionBlockCache(rs2);
        Assert.assertEquals(initialBlockCount1, blockCache1.getBlockCount());
        Assert.assertEquals(initialBlockCount2, blockCache2.getBlockCount());
    }

    @Test
    public void testClearBlockCacheFromAdmin() throws Exception {
        Admin admin = HTU.getAdmin();
        BlockCache blockCache1 = rs1.getBlockCache().get();
        BlockCache blockCache2 = rs2.getBlockCache().get();
        long initialBlockCount1 = blockCache1.getBlockCount();
        long initialBlockCount2 = blockCache2.getBlockCount();
        // scan will cause blocks to be added in BlockCache
        scanAllRegionsForRS(rs1);
        Assert.assertEquals(((blockCache1.getBlockCount()) - initialBlockCount1), HTU.getNumHFilesForRS(rs1, TestClearRegionBlockCache.TABLE_NAME, TestClearRegionBlockCache.FAMILY));
        scanAllRegionsForRS(rs2);
        Assert.assertEquals(((blockCache2.getBlockCount()) - initialBlockCount2), HTU.getNumHFilesForRS(rs2, TestClearRegionBlockCache.TABLE_NAME, TestClearRegionBlockCache.FAMILY));
        CacheEvictionStats stats = admin.clearBlockCache(TestClearRegionBlockCache.TABLE_NAME);
        Assert.assertEquals(stats.getEvictedBlocks(), ((HTU.getNumHFilesForRS(rs1, TestClearRegionBlockCache.TABLE_NAME, TestClearRegionBlockCache.FAMILY)) + (HTU.getNumHFilesForRS(rs2, TestClearRegionBlockCache.TABLE_NAME, TestClearRegionBlockCache.FAMILY))));
        Assert.assertEquals(initialBlockCount1, blockCache1.getBlockCount());
        Assert.assertEquals(initialBlockCount2, blockCache2.getBlockCount());
    }

    @Test
    public void testClearBlockCacheFromAsyncAdmin() throws Exception {
        AsyncAdmin admin = ConnectionFactory.createAsyncConnection(HTU.getConfiguration()).get().getAdmin();
        BlockCache blockCache1 = rs1.getBlockCache().get();
        BlockCache blockCache2 = rs2.getBlockCache().get();
        long initialBlockCount1 = blockCache1.getBlockCount();
        long initialBlockCount2 = blockCache2.getBlockCount();
        // scan will cause blocks to be added in BlockCache
        scanAllRegionsForRS(rs1);
        Assert.assertEquals(((blockCache1.getBlockCount()) - initialBlockCount1), HTU.getNumHFilesForRS(rs1, TestClearRegionBlockCache.TABLE_NAME, TestClearRegionBlockCache.FAMILY));
        scanAllRegionsForRS(rs2);
        Assert.assertEquals(((blockCache2.getBlockCount()) - initialBlockCount2), HTU.getNumHFilesForRS(rs2, TestClearRegionBlockCache.TABLE_NAME, TestClearRegionBlockCache.FAMILY));
        CacheEvictionStats stats = admin.clearBlockCache(TestClearRegionBlockCache.TABLE_NAME).get();
        Assert.assertEquals(stats.getEvictedBlocks(), ((HTU.getNumHFilesForRS(rs1, TestClearRegionBlockCache.TABLE_NAME, TestClearRegionBlockCache.FAMILY)) + (HTU.getNumHFilesForRS(rs2, TestClearRegionBlockCache.TABLE_NAME, TestClearRegionBlockCache.FAMILY))));
        Assert.assertEquals(initialBlockCount1, blockCache1.getBlockCount());
        Assert.assertEquals(initialBlockCount2, blockCache2.getBlockCount());
    }
}

