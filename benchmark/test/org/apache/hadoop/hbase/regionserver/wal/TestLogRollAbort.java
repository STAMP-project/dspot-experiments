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


import AbstractFSWALProvider.SPLITTING_EXT;
import HBaseMarkers.FATAL;
import HConstants.CATALOG_FAMILY;
import TableName.META_TABLE_NAME;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.NavigableMap;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.MiniHBaseCluster;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.RegionInfoBuilder;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.client.TableDescriptor;
import org.apache.hadoop.hbase.client.TableDescriptorBuilder;
import org.apache.hadoop.hbase.regionserver.HRegionServer;
import org.apache.hadoop.hbase.regionserver.MultiVersionConcurrencyControl;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.wal.AbstractFSWALProvider;
import org.apache.hadoop.hbase.wal.WAL;
import org.apache.hadoop.hbase.wal.WALEdit;
import org.apache.hadoop.hbase.wal.WALFactory;
import org.apache.hadoop.hbase.wal.WALSplitter;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests for conditions that should trigger RegionServer aborts when
 * rolling the current WAL fails.
 */
@Category({ RegionServerTests.class, MediumTests.class })
public class TestLogRollAbort {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestLogRollAbort.class);

    private static final Logger LOG = LoggerFactory.getLogger(AbstractTestLogRolling.class);

    private static MiniDFSCluster dfsCluster;

    private static Admin admin;

    private static MiniHBaseCluster cluster;

    protected static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    /* For the split-then-roll test */
    private static final Path HBASEDIR = new Path("/hbase");

    private static final Path HBASELOGDIR = new Path("/hbaselog");

    private static final Path OLDLOGDIR = new Path(TestLogRollAbort.HBASELOGDIR, HConstants.HREGION_OLDLOGDIR_NAME);

    private Configuration conf;

    private FileSystem fs;

    /**
     * Tests that RegionServer aborts if we hit an error closing the WAL when
     * there are unsynced WAL edits.  See HBASE-4282.
     */
    @Test
    public void testRSAbortWithUnflushedEdits() throws Exception {
        TestLogRollAbort.LOG.info("Starting testRSAbortWithUnflushedEdits()");
        // When the hbase:meta table can be opened, the region servers are running
        TestLogRollAbort.TEST_UTIL.getConnection().getTable(META_TABLE_NAME).close();
        // Create the test table and open it
        TableName tableName = TableName.valueOf(this.getClass().getSimpleName());
        TableDescriptor desc = TableDescriptorBuilder.newBuilder(tableName).setColumnFamily(ColumnFamilyDescriptorBuilder.of(CATALOG_FAMILY)).build();
        TestLogRollAbort.admin.createTable(desc);
        Table table = TestLogRollAbort.TEST_UTIL.getConnection().getTable(tableName);
        try {
            HRegionServer server = TestLogRollAbort.TEST_UTIL.getRSForFirstRegionInTable(tableName);
            WAL log = server.getWAL(null);
            Put p = new Put(Bytes.toBytes("row2001"));
            p.addColumn(CATALOG_FAMILY, Bytes.toBytes("col"), Bytes.toBytes(2001));
            table.put(p);
            log.sync();
            p = new Put(Bytes.toBytes("row2002"));
            p.addColumn(CATALOG_FAMILY, Bytes.toBytes("col"), Bytes.toBytes(2002));
            table.put(p);
            TestLogRollAbort.dfsCluster.restartDataNodes();
            TestLogRollAbort.LOG.info("Restarted datanodes");
            try {
                log.rollWriter(true);
            } catch (FailedLogCloseException flce) {
                // Expected exception.  We used to expect that there would be unsynced appends but this
                // not reliable now that sync plays a roll in wall rolling.  The above puts also now call
                // sync.
            } catch (Throwable t) {
                TestLogRollAbort.LOG.error(FATAL, "FAILED TEST: Got wrong exception", t);
            }
        } finally {
            table.close();
        }
    }

    /**
     * Tests the case where a RegionServer enters a GC pause,
     * comes back online after the master declared it dead and started to split.
     * Want log rolling after a master split to fail. See HBASE-2312.
     */
    @Test
    public void testLogRollAfterSplitStart() throws IOException {
        TestLogRollAbort.LOG.info("Verify wal roll after split starts will fail.");
        String logName = ServerName.valueOf("testLogRollAfterSplitStart", 16010, System.currentTimeMillis()).toString();
        Path thisTestsDir = new Path(TestLogRollAbort.HBASELOGDIR, AbstractFSWALProvider.getWALDirectoryName(logName));
        final WALFactory wals = new WALFactory(conf, logName);
        try {
            // put some entries in an WAL
            TableName tableName = TableName.valueOf(this.getClass().getName());
            RegionInfo regionInfo = RegionInfoBuilder.newBuilder(tableName).build();
            WAL log = wals.getWAL(regionInfo);
            MultiVersionConcurrencyControl mvcc = new MultiVersionConcurrencyControl(1);
            int total = 20;
            for (int i = 0; i < total; i++) {
                WALEdit kvs = new WALEdit();
                kvs.add(new org.apache.hadoop.hbase.KeyValue(Bytes.toBytes(i), tableName.getName(), tableName.getName()));
                NavigableMap<byte[], Integer> scopes = new java.util.TreeMap(Bytes.BYTES_COMPARATOR);
                scopes.put(Bytes.toBytes("column"), 0);
                log.append(regionInfo, new org.apache.hadoop.hbase.wal.WALKeyImpl(regionInfo.getEncodedNameAsBytes(), tableName, System.currentTimeMillis(), mvcc, scopes), kvs, true);
            }
            // Send the data to HDFS datanodes and close the HDFS writer
            log.sync();
            ((AbstractFSWAL<?>) (log)).replaceWriter(getOldPath(), null, null);
            // code taken from MasterFileSystem.getLogDirs(), which is called from
            // MasterFileSystem.splitLog() handles RS shutdowns (as observed by the splitting process)
            // rename the directory so a rogue RS doesn't create more WALs
            Path rsSplitDir = thisTestsDir.suffix(SPLITTING_EXT);
            if (!(fs.rename(thisTestsDir, rsSplitDir))) {
                throw new IOException(("Failed fs.rename for log split: " + thisTestsDir));
            }
            TestLogRollAbort.LOG.debug(("Renamed region directory: " + rsSplitDir));
            TestLogRollAbort.LOG.debug("Processing the old log files.");
            WALSplitter.split(TestLogRollAbort.HBASELOGDIR, rsSplitDir, TestLogRollAbort.OLDLOGDIR, fs, conf, wals);
            TestLogRollAbort.LOG.debug("Trying to roll the WAL.");
            try {
                log.rollWriter();
                Assert.fail("rollWriter() did not throw any exception.");
            } catch (IOException ioe) {
                if ((ioe.getCause()) instanceof FileNotFoundException) {
                    TestLogRollAbort.LOG.info("Got the expected exception: ", ioe.getCause());
                } else {
                    Assert.fail(("Unexpected exception: " + ioe));
                }
            }
        } finally {
            wals.close();
            if (fs.exists(thisTestsDir)) {
                fs.delete(thisTestsDir, true);
            }
        }
    }
}

