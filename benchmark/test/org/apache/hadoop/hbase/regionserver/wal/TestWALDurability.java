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


import Durability.FSYNC_WAL;
import Durability.SYNC_WAL;
import HRegion.WAL_HSYNC_CONF_KEY;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.regionserver.HRegion;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;


/**
 * Tests for WAL write durability - hflush vs hsync
 */
@Category({ MediumTests.class })
public class TestWALDurability {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestWALDurability.class);

    private static final String COLUMN_FAMILY = "MyCF";

    private static final byte[] COLUMN_FAMILY_BYTES = Bytes.toBytes(TestWALDurability.COLUMN_FAMILY);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private Configuration conf;

    private String dir;

    @Rule
    public TestName name = new TestName();

    // Test names
    protected TableName tableName;

    @Test
    public void testWALDurability() throws IOException {
        class CustomFSLog extends FSHLog {
            private Boolean syncFlag;

            public CustomFSLog(FileSystem fs, Path root, String logDir, Configuration conf) throws IOException {
                super(fs, root, logDir, conf);
            }

            @Override
            public void sync(boolean forceSync) throws IOException {
                syncFlag = forceSync;
                super.sync(forceSync);
            }

            @Override
            public void sync(long txid, boolean forceSync) throws IOException {
                syncFlag = forceSync;
                super.sync(txid, forceSync);
            }

            private void resetSyncFlag() {
                this.syncFlag = null;
            }
        }
        conf.set(WAL_HSYNC_CONF_KEY, "false");
        FileSystem fs = FileSystem.get(conf);
        Path rootDir = new Path(((dir) + (getName())));
        CustomFSLog customFSLog = new CustomFSLog(fs, rootDir, getName(), conf);
        init();
        HRegion region = TestWALDurability.initHRegion(tableName, null, null, customFSLog);
        byte[] bytes = Bytes.toBytes(getName());
        Put put = new Put(bytes);
        put.addColumn(TestWALDurability.COLUMN_FAMILY_BYTES, Bytes.toBytes("1"), bytes);
        customFSLog.resetSyncFlag();
        Assert.assertNull(customFSLog.syncFlag);
        region.put(put);
        Assert.assertEquals(customFSLog.syncFlag, false);
        // global hbase.wal.hsync true, no override in put call
        conf.set(WAL_HSYNC_CONF_KEY, "true");
        fs = FileSystem.get(conf);
        customFSLog = new CustomFSLog(fs, rootDir, getName(), conf);
        init();
        region = TestWALDurability.initHRegion(tableName, null, null, customFSLog);
        customFSLog.resetSyncFlag();
        Assert.assertNull(customFSLog.syncFlag);
        region.put(put);
        Assert.assertEquals(customFSLog.syncFlag, true);
        // global hbase.wal.hsync true, durability set in put call - fsync
        put.setDurability(FSYNC_WAL);
        customFSLog.resetSyncFlag();
        Assert.assertNull(customFSLog.syncFlag);
        region.put(put);
        Assert.assertEquals(customFSLog.syncFlag, true);
        // global hbase.wal.hsync true, durability set in put call - sync
        put = new Put(bytes);
        put.addColumn(TestWALDurability.COLUMN_FAMILY_BYTES, Bytes.toBytes("1"), bytes);
        put.setDurability(SYNC_WAL);
        customFSLog.resetSyncFlag();
        Assert.assertNull(customFSLog.syncFlag);
        region.put(put);
        Assert.assertEquals(customFSLog.syncFlag, false);
        HBaseTestingUtility.closeRegionAndWAL(region);
    }
}

