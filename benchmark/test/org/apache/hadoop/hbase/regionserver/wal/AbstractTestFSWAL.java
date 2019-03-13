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


import AbstractFSWALProvider.META_WAL_PROVIDER_ID;
import CommonFSUtils.StreamLacksCapabilityException;
import HConstants.HREGION_OLDLOGDIR_NAME;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NavigableMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.CellScanner;
import org.apache.hadoop.hbase.Coprocessor;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.RegionInfoBuilder;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.TableDescriptor;
import org.apache.hadoop.hbase.client.TableDescriptorBuilder;
import org.apache.hadoop.hbase.coprocessor.SampleRegionWALCoprocessor;
import org.apache.hadoop.hbase.regionserver.HRegion;
import org.apache.hadoop.hbase.regionserver.MultiVersionConcurrencyControl;
import org.apache.hadoop.hbase.regionserver.SequenceId;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.CommonFSUtils;
import org.apache.hadoop.hbase.util.EnvironmentEdge;
import org.apache.hadoop.hbase.util.EnvironmentEdgeManager;
import org.apache.hadoop.hbase.util.Threads;
import org.apache.hadoop.hbase.wal.WALEdit;
import org.apache.hadoop.hbase.wal.WALKey;
import org.apache.hadoop.hbase.wal.WALKeyImpl;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AbstractTestFSWAL {
    protected static final Logger LOG = LoggerFactory.getLogger(AbstractTestFSWAL.class);

    protected static Configuration CONF;

    protected static FileSystem FS;

    protected static Path DIR;

    protected static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    @Rule
    public final TestName currentTest = new TestName();

    /**
     * A loaded WAL coprocessor won't break existing WAL test cases.
     */
    @Test
    public void testWALCoprocessorLoaded() throws Exception {
        // test to see whether the coprocessor is loaded or not.
        AbstractFSWAL<?> wal = null;
        try {
            wal = newWAL(AbstractTestFSWAL.FS, CommonFSUtils.getWALRootDir(AbstractTestFSWAL.CONF), AbstractTestFSWAL.DIR.toString(), HREGION_OLDLOGDIR_NAME, AbstractTestFSWAL.CONF, null, true, null, null);
            WALCoprocessorHost host = wal.getCoprocessorHost();
            Coprocessor c = host.findCoprocessor(SampleRegionWALCoprocessor.class);
            Assert.assertNotNull(c);
        } finally {
            if (wal != null) {
                wal.close();
            }
        }
    }

    /**
     * tests the log comparator. Ensure that we are not mixing meta logs with non-meta logs (throws
     * exception if we do). Comparison is based on the timestamp present in the wal name.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testWALComparator() throws Exception {
        AbstractFSWAL<?> wal1 = null;
        AbstractFSWAL<?> walMeta = null;
        try {
            wal1 = newWAL(AbstractTestFSWAL.FS, CommonFSUtils.getWALRootDir(AbstractTestFSWAL.CONF), AbstractTestFSWAL.DIR.toString(), HREGION_OLDLOGDIR_NAME, AbstractTestFSWAL.CONF, null, true, null, null);
            AbstractTestFSWAL.LOG.debug(("Log obtained is: " + wal1));
            Comparator<Path> comp = wal1.LOG_NAME_COMPARATOR;
            Path p1 = wal1.computeFilename(11);
            Path p2 = wal1.computeFilename(12);
            // comparing with itself returns 0
            Assert.assertTrue(((comp.compare(p1, p1)) == 0));
            // comparing with different filenum.
            Assert.assertTrue(((comp.compare(p1, p2)) < 0));
            walMeta = newWAL(AbstractTestFSWAL.FS, CommonFSUtils.getWALRootDir(AbstractTestFSWAL.CONF), AbstractTestFSWAL.DIR.toString(), HREGION_OLDLOGDIR_NAME, AbstractTestFSWAL.CONF, null, true, null, META_WAL_PROVIDER_ID);
            Comparator<Path> compMeta = walMeta.LOG_NAME_COMPARATOR;
            Path p1WithMeta = walMeta.computeFilename(11);
            Path p2WithMeta = walMeta.computeFilename(12);
            Assert.assertTrue(((compMeta.compare(p1WithMeta, p1WithMeta)) == 0));
            Assert.assertTrue(((compMeta.compare(p1WithMeta, p2WithMeta)) < 0));
            // mixing meta and non-meta logs gives error
            boolean ex = false;
            try {
                comp.compare(p1WithMeta, p2);
            } catch (IllegalArgumentException e) {
                ex = true;
            }
            Assert.assertTrue("Comparator doesn't complain while checking meta log files", ex);
            boolean exMeta = false;
            try {
                compMeta.compare(p1WithMeta, p2);
            } catch (IllegalArgumentException e) {
                exMeta = true;
            }
            Assert.assertTrue("Meta comparator doesn't complain while checking log files", exMeta);
        } finally {
            if (wal1 != null) {
                wal1.close();
            }
            if (walMeta != null) {
                walMeta.close();
            }
        }
    }

    /**
     * On rolling a wal after reaching the threshold, {@link WAL#rollWriter()} returns the list of
     * regions which should be flushed in order to archive the oldest wal file.
     * <p>
     * This method tests this behavior by inserting edits and rolling the wal enough times to reach
     * the max number of logs threshold. It checks whether we get the "right regions" for flush on
     * rolling the wal.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testFindMemStoresEligibleForFlush() throws Exception {
        AbstractTestFSWAL.LOG.debug("testFindMemStoresEligibleForFlush");
        Configuration conf1 = HBaseConfiguration.create(AbstractTestFSWAL.CONF);
        conf1.setInt("hbase.regionserver.maxlogs", 1);
        AbstractFSWAL<?> wal = newWAL(AbstractTestFSWAL.FS, CommonFSUtils.getWALRootDir(conf1), AbstractTestFSWAL.DIR.toString(), HREGION_OLDLOGDIR_NAME, conf1, null, true, null, null);
        TableDescriptor t1 = TableDescriptorBuilder.newBuilder(TableName.valueOf("t1")).setColumnFamily(ColumnFamilyDescriptorBuilder.of("row")).build();
        TableDescriptor t2 = TableDescriptorBuilder.newBuilder(TableName.valueOf("t2")).setColumnFamily(ColumnFamilyDescriptorBuilder.of("row")).build();
        RegionInfo hri1 = RegionInfoBuilder.newBuilder(t1.getTableName()).build();
        RegionInfo hri2 = RegionInfoBuilder.newBuilder(t2.getTableName()).build();
        // add edits and roll the wal
        MultiVersionConcurrencyControl mvcc = new MultiVersionConcurrencyControl();
        NavigableMap<byte[], Integer> scopes1 = new java.util.TreeMap(Bytes.BYTES_COMPARATOR);
        for (byte[] fam : t1.getColumnFamilyNames()) {
            scopes1.put(fam, 0);
        }
        NavigableMap<byte[], Integer> scopes2 = new java.util.TreeMap(Bytes.BYTES_COMPARATOR);
        for (byte[] fam : t2.getColumnFamilyNames()) {
            scopes2.put(fam, 0);
        }
        try {
            addEdits(wal, hri1, t1, 2, mvcc, scopes1);
            wal.rollWriter();
            // add some more edits and roll the wal. This would reach the log number threshold
            addEdits(wal, hri1, t1, 2, mvcc, scopes1);
            wal.rollWriter();
            // with above rollWriter call, the max logs limit is reached.
            Assert.assertTrue(((wal.getNumRolledLogFiles()) == 2));
            // get the regions to flush; since there is only one region in the oldest wal, it should
            // return only one region.
            byte[][] regionsToFlush = wal.findRegionsToForceFlush();
            Assert.assertEquals(1, regionsToFlush.length);
            Assert.assertEquals(hri1.getEncodedNameAsBytes(), regionsToFlush[0]);
            // insert edits in second region
            addEdits(wal, hri2, t2, 2, mvcc, scopes2);
            // get the regions to flush, it should still read region1.
            regionsToFlush = wal.findRegionsToForceFlush();
            Assert.assertEquals(1, regionsToFlush.length);
            Assert.assertEquals(hri1.getEncodedNameAsBytes(), regionsToFlush[0]);
            // flush region 1, and roll the wal file. Only last wal which has entries for region1 should
            // remain.
            flushRegion(wal, hri1.getEncodedNameAsBytes(), t1.getColumnFamilyNames());
            wal.rollWriter();
            // only one wal should remain now (that is for the second region).
            Assert.assertEquals(1, wal.getNumRolledLogFiles());
            // flush the second region
            flushRegion(wal, hri2.getEncodedNameAsBytes(), t2.getColumnFamilyNames());
            wal.rollWriter(true);
            // no wal should remain now.
            Assert.assertEquals(0, wal.getNumRolledLogFiles());
            // add edits both to region 1 and region 2, and roll.
            addEdits(wal, hri1, t1, 2, mvcc, scopes1);
            addEdits(wal, hri2, t2, 2, mvcc, scopes2);
            wal.rollWriter();
            // add edits and roll the writer, to reach the max logs limit.
            Assert.assertEquals(1, wal.getNumRolledLogFiles());
            addEdits(wal, hri1, t1, 2, mvcc, scopes1);
            wal.rollWriter();
            // it should return two regions to flush, as the oldest wal file has entries
            // for both regions.
            regionsToFlush = wal.findRegionsToForceFlush();
            Assert.assertEquals(2, regionsToFlush.length);
            // flush both regions
            flushRegion(wal, hri1.getEncodedNameAsBytes(), t1.getColumnFamilyNames());
            flushRegion(wal, hri2.getEncodedNameAsBytes(), t2.getColumnFamilyNames());
            wal.rollWriter(true);
            Assert.assertEquals(0, wal.getNumRolledLogFiles());
            // Add an edit to region1, and roll the wal.
            addEdits(wal, hri1, t1, 2, mvcc, scopes1);
            // tests partial flush: roll on a partial flush, and ensure that wal is not archived.
            wal.startCacheFlush(hri1.getEncodedNameAsBytes(), t1.getColumnFamilyNames());
            wal.rollWriter();
            wal.completeCacheFlush(hri1.getEncodedNameAsBytes());
            Assert.assertEquals(1, wal.getNumRolledLogFiles());
        } finally {
            if (wal != null) {
                wal.close();
            }
        }
    }

    @Test(expected = IOException.class)
    public void testFailedToCreateWALIfParentRenamed() throws StreamLacksCapabilityException, IOException {
        final String name = "testFailedToCreateWALIfParentRenamed";
        AbstractFSWAL<?> wal = newWAL(AbstractTestFSWAL.FS, CommonFSUtils.getWALRootDir(AbstractTestFSWAL.CONF), name, HREGION_OLDLOGDIR_NAME, AbstractTestFSWAL.CONF, null, true, null, null);
        long filenum = System.currentTimeMillis();
        Path path = wal.computeFilename(filenum);
        wal.createWriterInstance(path);
        Path parent = path.getParent();
        path = wal.computeFilename((filenum + 1));
        Path newPath = new Path(parent.getParent(), ((parent.getName()) + "-splitting"));
        AbstractTestFSWAL.FS.rename(parent, newPath);
        wal.createWriterInstance(path);
        Assert.fail("It should fail to create the new WAL");
    }

    /**
     * Test flush for sure has a sequence id that is beyond the last edit appended. We do this by
     * slowing appends in the background ring buffer thread while in foreground we call flush. The
     * addition of the sync over HRegion in flush should fix an issue where flush was returning before
     * all of its appends had made it out to the WAL (HBASE-11109).
     *
     * @throws IOException
     * 		
     * @see <a href="https://issues.apache.org/jira/browse/HBASE-11109">HBASE-11109</a>
     */
    @Test
    public void testFlushSequenceIdIsGreaterThanAllEditsInHFile() throws IOException {
        String testName = currentTest.getMethodName();
        final TableName tableName = TableName.valueOf(testName);
        final RegionInfo hri = RegionInfoBuilder.newBuilder(tableName).build();
        final byte[] rowName = tableName.getName();
        final TableDescriptor htd = TableDescriptorBuilder.newBuilder(tableName).setColumnFamily(ColumnFamilyDescriptorBuilder.of("f")).build();
        HRegion r = HBaseTestingUtility.createRegionAndWAL(hri, AbstractTestFSWAL.TEST_UTIL.getDefaultRootDirPath(), AbstractTestFSWAL.TEST_UTIL.getConfiguration(), htd);
        HBaseTestingUtility.closeRegionAndWAL(r);
        final int countPerFamily = 10;
        final AtomicBoolean goslow = new AtomicBoolean(false);
        NavigableMap<byte[], Integer> scopes = new java.util.TreeMap(Bytes.BYTES_COMPARATOR);
        for (byte[] fam : htd.getColumnFamilyNames()) {
            scopes.put(fam, 0);
        }
        // subclass and doctor a method.
        AbstractFSWAL<?> wal = newSlowWAL(AbstractTestFSWAL.FS, CommonFSUtils.getWALRootDir(AbstractTestFSWAL.CONF), AbstractTestFSWAL.DIR.toString(), testName, AbstractTestFSWAL.CONF, null, true, null, null, new Runnable() {
            @Override
            public void run() {
                if (goslow.get()) {
                    Threads.sleep(100);
                    AbstractTestFSWAL.LOG.debug("Sleeping before appending 100ms");
                }
            }
        });
        HRegion region = HRegion.openHRegion(AbstractTestFSWAL.TEST_UTIL.getConfiguration(), AbstractTestFSWAL.TEST_UTIL.getTestFileSystem(), AbstractTestFSWAL.TEST_UTIL.getDefaultRootDirPath(), hri, htd, wal);
        EnvironmentEdge ee = EnvironmentEdgeManager.getDelegate();
        try {
            List<Put> puts = null;
            for (byte[] fam : htd.getColumnFamilyNames()) {
                puts = TestWALReplay.addRegionEdits(rowName, fam, countPerFamily, ee, region, "x");
            }
            // Now assert edits made it in.
            final Get g = new Get(rowName);
            Result result = region.get(g);
            Assert.assertEquals((countPerFamily * (htd.getColumnFamilyNames().size())), result.size());
            // Construct a WALEdit and add it a few times to the WAL.
            WALEdit edits = new WALEdit();
            for (Put p : puts) {
                CellScanner cs = p.cellScanner();
                while (cs.advance()) {
                    edits.add(cs.current());
                } 
            }
            // Add any old cluster id.
            List<UUID> clusterIds = new ArrayList<>(1);
            clusterIds.add(getRandomUUID());
            // Now make appends run slow.
            goslow.set(true);
            for (int i = 0; i < countPerFamily; i++) {
                final RegionInfo info = region.getRegionInfo();
                final WALKeyImpl logkey = new WALKeyImpl(info.getEncodedNameAsBytes(), tableName, System.currentTimeMillis(), clusterIds, (-1), (-1), region.getMVCC(), scopes);
                wal.append(info, logkey, edits, true);
                region.getMVCC().completeAndWait(logkey.getWriteEntry());
            }
            region.flush(true);
            // FlushResult.flushSequenceId is not visible here so go get the current sequence id.
            long currentSequenceId = region.getReadPoint(null);
            // Now release the appends
            goslow.set(false);
            Assert.assertTrue((currentSequenceId >= (region.getReadPoint(null))));
        } finally {
            region.close(true);
            wal.close();
        }
    }

    @Test
    public void testSyncNoAppend() throws IOException {
        String testName = currentTest.getMethodName();
        AbstractFSWAL<?> wal = newWAL(AbstractTestFSWAL.FS, CommonFSUtils.getWALRootDir(AbstractTestFSWAL.CONF), AbstractTestFSWAL.DIR.toString(), testName, AbstractTestFSWAL.CONF, null, true, null, null);
        try {
            wal.sync();
        } finally {
            wal.close();
        }
    }

    @Test
    public void testWriteEntryCanBeNull() throws IOException {
        String testName = currentTest.getMethodName();
        AbstractFSWAL<?> wal = newWAL(AbstractTestFSWAL.FS, CommonFSUtils.getWALRootDir(AbstractTestFSWAL.CONF), AbstractTestFSWAL.DIR.toString(), testName, AbstractTestFSWAL.CONF, null, true, null, null);
        wal.close();
        TableDescriptor td = TableDescriptorBuilder.newBuilder(TableName.valueOf("table")).setColumnFamily(ColumnFamilyDescriptorBuilder.of("row")).build();
        RegionInfo ri = RegionInfoBuilder.newBuilder(td.getTableName()).build();
        MultiVersionConcurrencyControl mvcc = new MultiVersionConcurrencyControl();
        NavigableMap<byte[], Integer> scopes = new java.util.TreeMap(Bytes.BYTES_COMPARATOR);
        for (byte[] fam : td.getColumnFamilyNames()) {
            scopes.put(fam, 0);
        }
        long timestamp = System.currentTimeMillis();
        byte[] row = Bytes.toBytes("row");
        WALEdit cols = new WALEdit();
        cols.add(new KeyValue(row, row, row, timestamp, row));
        WALKeyImpl key = new WALKeyImpl(ri.getEncodedNameAsBytes(), td.getTableName(), SequenceId.NO_SEQUENCE_ID, timestamp, WALKey.EMPTY_UUIDS, HConstants.NO_NONCE, HConstants.NO_NONCE, mvcc, scopes);
        try {
            wal.append(ri, key, cols, true);
            Assert.fail("Should fail since the wal has already been closed");
        } catch (IOException e) {
            // expected
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("log is closed"));
            // the WriteEntry should be null since we fail before setting it.
            Assert.assertNull(key.getWriteEntry());
        }
    }

    @Test(expected = WALClosedException.class)
    public void testRollWriterForClosedWAL() throws IOException {
        String testName = currentTest.getMethodName();
        AbstractFSWAL<?> wal = newWAL(AbstractTestFSWAL.FS, CommonFSUtils.getWALRootDir(AbstractTestFSWAL.CONF), AbstractTestFSWAL.DIR.toString(), testName, AbstractTestFSWAL.CONF, null, true, null, null);
        wal.close();
        wal.rollWriter();
    }
}

