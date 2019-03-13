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


import HConstants.CATALOG_FAMILY;
import TableName.META_TABLE_NAME;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.MiniHBaseCluster;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.regionserver.HRegion;
import org.apache.hadoop.hbase.regionserver.HRegionServer;
import org.apache.hadoop.hbase.regionserver.Store;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.Threads;
import org.apache.hadoop.hbase.wal.AbstractFSWALProvider;
import org.apache.hadoop.hbase.wal.WAL;
import org.apache.hadoop.hbase.wal.WALFactory;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test log deletion as logs are rolled.
 */
public abstract class AbstractTestLogRolling {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractTestLogRolling.class);

    protected HRegionServer server;

    protected String tableName;

    protected byte[] value;

    protected FileSystem fs;

    protected MiniDFSCluster dfsCluster;

    protected Admin admin;

    protected MiniHBaseCluster cluster;

    protected static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    @Rule
    public final TestName name = new TestName();

    public AbstractTestLogRolling() {
        this.server = null;
        this.tableName = null;
        String className = this.getClass().getName();
        StringBuilder v = new StringBuilder(className);
        while ((v.length()) < 1000) {
            v.append(className);
        } 
        this.value = Bytes.toBytes(v.toString());
    }

    /**
     * Tests that log rolling doesn't hang when no data is written.
     */
    @Test
    public void testLogRollOnNothingWritten() throws Exception {
        final Configuration conf = AbstractTestLogRolling.TEST_UTIL.getConfiguration();
        final WALFactory wals = new WALFactory(conf, ServerName.valueOf("test.com", 8080, 1).toString());
        final WAL newLog = wals.getWAL(null);
        try {
            // Now roll the log before we write anything.
            newLog.rollWriter(true);
        } finally {
            wals.close();
        }
    }

    /**
     * Tests that logs are deleted
     */
    @Test
    public void testLogRolling() throws Exception {
        this.tableName = getName();
        // TODO: Why does this write data take for ever?
        startAndWriteData();
        RegionInfo region = server.getRegions(TableName.valueOf(tableName)).get(0).getRegionInfo();
        final WAL log = server.getWAL(region);
        AbstractTestLogRolling.LOG.info((("after writing there are " + (AbstractFSWALProvider.getNumRolledLogFiles(log))) + " log files"));
        assertLogFileSize(log);
        // flush all regions
        for (HRegion r : server.getOnlineRegionsLocalContext()) {
            r.flush(true);
        }
        // Now roll the log
        log.rollWriter();
        int count = AbstractFSWALProvider.getNumRolledLogFiles(log);
        AbstractTestLogRolling.LOG.info((("after flushing all regions and rolling logs there are " + count) + " log files"));
        Assert.assertTrue(("actual count: " + count), (count <= 2));
        assertLogFileSize(log);
    }

    /**
     * Tests that logs are deleted when some region has a compaction
     * record in WAL and no other records. See HBASE-8597.
     */
    @Test
    public void testCompactionRecordDoesntBlockRolling() throws Exception {
        Table table = null;
        // When the hbase:meta table can be opened, the region servers are running
        Table t = AbstractTestLogRolling.TEST_UTIL.getConnection().getTable(META_TABLE_NAME);
        try {
            table = createTestTable(getName());
            server = AbstractTestLogRolling.TEST_UTIL.getRSForFirstRegionInTable(table.getName());
            HRegion region = server.getRegions(table.getName()).get(0);
            final WAL log = server.getWAL(region.getRegionInfo());
            Store s = region.getStore(CATALOG_FAMILY);
            // Put some stuff into table, to make sure we have some files to compact.
            for (int i = 1; i <= 2; ++i) {
                doPut(table, i);
                admin.flush(table.getName());
            }
            doPut(table, 3);// don't flush yet, or compaction might trigger before we roll WAL

            Assert.assertEquals("Should have no WAL after initial writes", 0, AbstractFSWALProvider.getNumRolledLogFiles(log));
            Assert.assertEquals(2, s.getStorefilesCount());
            // Roll the log and compact table, to have compaction record in the 2nd WAL.
            log.rollWriter();
            Assert.assertEquals("Should have WAL; one table is not flushed", 1, AbstractFSWALProvider.getNumRolledLogFiles(log));
            admin.flush(table.getName());
            region.compact(false);
            // Wait for compaction in case if flush triggered it before us.
            Assert.assertNotNull(s);
            for (int waitTime = 3000; ((s.getStorefilesCount()) > 1) && (waitTime > 0); waitTime -= 200) {
                Threads.sleepWithoutInterrupt(200);
            }
            Assert.assertEquals("Compaction didn't happen", 1, s.getStorefilesCount());
            // Write some value to the table so the WAL cannot be deleted until table is flushed.
            doPut(table, 0);// Now 2nd WAL will have both compaction and put record for table.

            log.rollWriter();// 1st WAL deleted, 2nd not deleted yet.

            Assert.assertEquals("Should have WAL; one table is not flushed", 1, AbstractFSWALProvider.getNumRolledLogFiles(log));
            // Flush table to make latest WAL obsolete; write another record, and roll again.
            admin.flush(table.getName());
            doPut(table, 1);
            log.rollWriter();// Now 2nd WAL is deleted and 3rd is added.

            Assert.assertEquals("Should have 1 WALs at the end", 1, AbstractFSWALProvider.getNumRolledLogFiles(log));
        } finally {
            if (t != null)
                t.close();

            if (table != null)
                table.close();

        }
    }
}

