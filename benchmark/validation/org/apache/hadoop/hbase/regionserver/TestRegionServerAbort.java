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


import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.MiniHBaseCluster;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Durability;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.client.TableDescriptor;
import org.apache.hadoop.hbase.coprocessor.CoreCoprocessor;
import org.apache.hadoop.hbase.coprocessor.ObserverContext;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessor;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessorEnvironment;
import org.apache.hadoop.hbase.coprocessor.RegionObserver;
import org.apache.hadoop.hbase.coprocessor.RegionServerCoprocessor;
import org.apache.hadoop.hbase.coprocessor.RegionServerCoprocessorEnvironment;
import org.apache.hadoop.hbase.coprocessor.RegionServerObserver;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.wal.WAL;
import org.apache.hadoop.hbase.wal.WALEdit;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests around regionserver shutdown and abort
 */
@Category({ RegionServerTests.class, MediumTests.class })
public class TestRegionServerAbort {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRegionServerAbort.class);

    private static final byte[] FAMILY_BYTES = Bytes.toBytes("f");

    private static final Logger LOG = LoggerFactory.getLogger(TestRegionServerAbort.class);

    private HBaseTestingUtility testUtil;

    private Configuration conf;

    private MiniDFSCluster dfsCluster;

    private MiniHBaseCluster cluster;

    /**
     * Test that a regionserver is able to abort properly, even when a coprocessor
     * throws an exception in preStopRegionServer().
     */
    @Test
    public void testAbortFromRPC() throws Exception {
        TableName tableName = TableName.valueOf("testAbortFromRPC");
        // create a test table
        Table table = testUtil.createTable(tableName, TestRegionServerAbort.FAMILY_BYTES);
        // write some edits
        testUtil.loadTable(table, TestRegionServerAbort.FAMILY_BYTES);
        TestRegionServerAbort.LOG.info("Wrote data");
        // force a flush
        cluster.flushcache(tableName);
        TestRegionServerAbort.LOG.info("Flushed table");
        // Send a poisoned put to trigger the abort
        Put put = new Put(new byte[]{ 0, 0, 0, 0 });
        put.addColumn(TestRegionServerAbort.FAMILY_BYTES, Bytes.toBytes("c"), new byte[]{  });
        put.setAttribute(TestRegionServerAbort.StopBlockingRegionObserver.DO_ABORT, new byte[]{ 1 });
        List<HRegion> regions = cluster.findRegionsForTable(tableName);
        HRegion firstRegion = cluster.findRegionsForTable(tableName).get(0);
        table.put(put);
        // Verify that the regionserver is stopped
        Assert.assertNotNull(firstRegion);
        Assert.assertNotNull(firstRegion.getRegionServerServices());
        TestRegionServerAbort.LOG.info(("isAborted = " + (firstRegion.getRegionServerServices().isAborted())));
        Assert.assertTrue(firstRegion.getRegionServerServices().isAborted());
        TestRegionServerAbort.LOG.info(("isStopped = " + (firstRegion.getRegionServerServices().isStopped())));
        Assert.assertTrue(firstRegion.getRegionServerServices().isStopped());
    }

    /**
     * Test that a coprocessor is able to override a normal regionserver stop request.
     */
    @Test
    public void testStopOverrideFromCoprocessor() throws Exception {
        Admin admin = testUtil.getHBaseAdmin();
        HRegionServer regionserver = cluster.getRegionServer(0);
        admin.stopRegionServer(regionserver.getServerName().getHostAndPort());
        // regionserver should have failed to stop due to coprocessor
        Assert.assertFalse(cluster.getRegionServer(0).isAborted());
        Assert.assertFalse(cluster.getRegionServer(0).isStopped());
    }

    @CoreCoprocessor
    public static class StopBlockingRegionObserver implements RegionCoprocessor , RegionObserver , RegionServerCoprocessor , RegionServerObserver {
        public static final String DO_ABORT = "DO_ABORT";

        private boolean stopAllowed;

        @Override
        public Optional<RegionObserver> getRegionObserver() {
            return Optional.of(this);
        }

        @Override
        public Optional<RegionServerObserver> getRegionServerObserver() {
            return Optional.of(this);
        }

        @Override
        public void prePut(ObserverContext<RegionCoprocessorEnvironment> c, Put put, WALEdit edit, Durability durability) throws IOException {
            if ((put.getAttribute(TestRegionServerAbort.StopBlockingRegionObserver.DO_ABORT)) != null) {
                // TODO: Change this so it throws a CP Abort Exception instead.
                RegionServerServices rss = getRegionServerServices();
                String str = "Aborting for test";
                TestRegionServerAbort.LOG.info(((str + " ") + (rss.getServerName())));
                rss.abort(str, new Throwable(str));
            }
        }

        @Override
        public void preStopRegionServer(ObserverContext<RegionServerCoprocessorEnvironment> env) throws IOException {
            if (!(stopAllowed)) {
                throw new IOException("Stop not allowed");
            }
        }

        public void setStopAllowed(boolean allowed) {
            this.stopAllowed = allowed;
        }
    }

    /**
     * Throws an exception during store file refresh in order to trigger a regionserver abort.
     */
    public static class ErrorThrowingHRegion extends HRegion {
        public ErrorThrowingHRegion(Path tableDir, WAL wal, FileSystem fs, Configuration confParam, RegionInfo regionInfo, TableDescriptor htd, RegionServerServices rsServices) {
            super(tableDir, wal, fs, confParam, regionInfo, htd, rsServices);
        }

        public ErrorThrowingHRegion(HRegionFileSystem fs, WAL wal, Configuration confParam, TableDescriptor htd, RegionServerServices rsServices) {
            super(fs, wal, confParam, htd, rsServices);
        }

        @Override
        protected boolean refreshStoreFiles(boolean force) throws IOException {
            // forced when called through RegionScannerImpl.handleFileNotFound()
            if (force) {
                throw new IOException("Failing file refresh for testing");
            }
            return super.refreshStoreFiles(force);
        }
    }
}

