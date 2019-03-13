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
package org.apache.hadoop.hbase.regionserver.wal;


import Durability.ASYNC_WAL;
import Durability.FSYNC_WAL;
import Durability.SKIP_WAL;
import Durability.SYNC_WAL;
import Durability.USE_DEFAULT;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.client.Increment;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.regionserver.HRegion;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.wal.WAL;
import org.apache.hadoop.hbase.wal.WALFactory;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Tests for WAL write durability
 */
@RunWith(Parameterized.class)
@Category({ RegionServerTests.class, MediumTests.class })
public class TestDurability {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestDurability.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static FileSystem FS;

    private static MiniDFSCluster CLUSTER;

    private static Configuration CONF;

    private static Path DIR;

    private static byte[] FAMILY = Bytes.toBytes("family");

    private static byte[] ROW = Bytes.toBytes("row");

    private static byte[] COL = Bytes.toBytes("col");

    @Parameterized.Parameter
    public String walProvider;

    @Rule
    public TestName name = new TestName();

    @Test
    public void testDurability() throws Exception {
        WALFactory wals = new WALFactory(TestDurability.CONF, ServerName.valueOf("TestDurability", 16010, System.currentTimeMillis()).toString());
        HRegion region = createHRegion(wals, USE_DEFAULT);
        WAL wal = region.getWAL();
        HRegion deferredRegion = createHRegion(region.getTableDescriptor(), region.getRegionInfo(), "deferredRegion", wal, ASYNC_WAL);
        region.put(newPut(null));
        verifyWALCount(wals, wal, 1);
        // a put through the deferred table does not write to the wal immediately,
        // but maybe has been successfully sync-ed by the underlying AsyncWriter +
        // AsyncFlusher thread
        deferredRegion.put(newPut(null));
        // but will after we sync the wal
        wal.sync();
        verifyWALCount(wals, wal, 2);
        // a put through a deferred table will be sync with the put sync'ed put
        deferredRegion.put(newPut(null));
        wal.sync();
        verifyWALCount(wals, wal, 3);
        region.put(newPut(null));
        verifyWALCount(wals, wal, 4);
        // a put through a deferred table will be sync with the put sync'ed put
        deferredRegion.put(newPut(USE_DEFAULT));
        wal.sync();
        verifyWALCount(wals, wal, 5);
        region.put(newPut(USE_DEFAULT));
        verifyWALCount(wals, wal, 6);
        // SKIP_WAL never writes to the wal
        region.put(newPut(SKIP_WAL));
        deferredRegion.put(newPut(SKIP_WAL));
        verifyWALCount(wals, wal, 6);
        wal.sync();
        verifyWALCount(wals, wal, 6);
        // Async overrides sync table default
        region.put(newPut(ASYNC_WAL));
        deferredRegion.put(newPut(ASYNC_WAL));
        wal.sync();
        verifyWALCount(wals, wal, 8);
        // sync overrides async table default
        region.put(newPut(SYNC_WAL));
        deferredRegion.put(newPut(SYNC_WAL));
        verifyWALCount(wals, wal, 10);
        // fsync behaves like sync
        region.put(newPut(FSYNC_WAL));
        deferredRegion.put(newPut(FSYNC_WAL));
        verifyWALCount(wals, wal, 12);
    }

    @Test
    public void testIncrement() throws Exception {
        byte[] row1 = Bytes.toBytes("row1");
        byte[] col1 = Bytes.toBytes("col1");
        byte[] col2 = Bytes.toBytes("col2");
        byte[] col3 = Bytes.toBytes("col3");
        // Setting up region
        WALFactory wals = new WALFactory(TestDurability.CONF, ServerName.valueOf("TestIncrement", 16010, System.currentTimeMillis()).toString());
        HRegion region = createHRegion(wals, USE_DEFAULT);
        WAL wal = region.getWAL();
        // col1: amount = 0, 1 write back to WAL
        Increment inc1 = new Increment(row1);
        inc1.addColumn(TestDurability.FAMILY, col1, 0);
        Result res = region.increment(inc1);
        Assert.assertEquals(1, res.size());
        Assert.assertEquals(0, Bytes.toLong(res.getValue(TestDurability.FAMILY, col1)));
        verifyWALCount(wals, wal, 1);
        // col1: amount = 1, 1 write back to WAL
        inc1 = new Increment(row1);
        inc1.addColumn(TestDurability.FAMILY, col1, 1);
        res = region.increment(inc1);
        Assert.assertEquals(1, res.size());
        Assert.assertEquals(1, Bytes.toLong(res.getValue(TestDurability.FAMILY, col1)));
        verifyWALCount(wals, wal, 2);
        // col1: amount = 0, 1 write back to WAL
        inc1 = new Increment(row1);
        inc1.addColumn(TestDurability.FAMILY, col1, 0);
        res = region.increment(inc1);
        Assert.assertEquals(1, res.size());
        Assert.assertEquals(1, Bytes.toLong(res.getValue(TestDurability.FAMILY, col1)));
        verifyWALCount(wals, wal, 3);
        // col1: amount = 0, col2: amount = 0, col3: amount = 0
        // 1 write back to WAL
        inc1 = new Increment(row1);
        inc1.addColumn(TestDurability.FAMILY, col1, 0);
        inc1.addColumn(TestDurability.FAMILY, col2, 0);
        inc1.addColumn(TestDurability.FAMILY, col3, 0);
        res = region.increment(inc1);
        Assert.assertEquals(3, res.size());
        Assert.assertEquals(1, Bytes.toLong(res.getValue(TestDurability.FAMILY, col1)));
        Assert.assertEquals(0, Bytes.toLong(res.getValue(TestDurability.FAMILY, col2)));
        Assert.assertEquals(0, Bytes.toLong(res.getValue(TestDurability.FAMILY, col3)));
        verifyWALCount(wals, wal, 4);
        // col1: amount = 5, col2: amount = 4, col3: amount = 3
        // 1 write back to WAL
        inc1 = new Increment(row1);
        inc1.addColumn(TestDurability.FAMILY, col1, 5);
        inc1.addColumn(TestDurability.FAMILY, col2, 4);
        inc1.addColumn(TestDurability.FAMILY, col3, 3);
        res = region.increment(inc1);
        Assert.assertEquals(3, res.size());
        Assert.assertEquals(6, Bytes.toLong(res.getValue(TestDurability.FAMILY, col1)));
        Assert.assertEquals(4, Bytes.toLong(res.getValue(TestDurability.FAMILY, col2)));
        Assert.assertEquals(3, Bytes.toLong(res.getValue(TestDurability.FAMILY, col3)));
        verifyWALCount(wals, wal, 5);
    }

    /**
     * Test when returnResults set to false in increment it should not return the result instead it
     * resturn null.
     */
    @Test
    public void testIncrementWithReturnResultsSetToFalse() throws Exception {
        byte[] row1 = Bytes.toBytes("row1");
        byte[] col1 = Bytes.toBytes("col1");
        // Setting up region
        WALFactory wals = new WALFactory(TestDurability.CONF, ServerName.valueOf("testIncrementWithReturnResultsSetToFalse", 16010, System.currentTimeMillis()).toString());
        HRegion region = createHRegion(wals, USE_DEFAULT);
        Increment inc1 = new Increment(row1);
        inc1.setReturnResults(false);
        inc1.addColumn(TestDurability.FAMILY, col1, 1);
        Result res = region.increment(inc1);
        Assert.assertTrue(res.isEmpty());
    }
}

