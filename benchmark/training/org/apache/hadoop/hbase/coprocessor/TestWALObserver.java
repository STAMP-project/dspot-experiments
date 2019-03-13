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
package org.apache.hadoop.hbase.coprocessor;


import java.security.PrivilegedExceptionAction;
import java.util.NavigableMap;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.RegionInfoBuilder;
import org.apache.hadoop.hbase.client.TableDescriptor;
import org.apache.hadoop.hbase.regionserver.HRegion;
import org.apache.hadoop.hbase.regionserver.MultiVersionConcurrencyControl;
import org.apache.hadoop.hbase.security.User;
import org.apache.hadoop.hbase.testclassification.CoprocessorTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.EnvironmentEdgeManager;
import org.apache.hadoop.hbase.util.FSUtils;
import org.apache.hadoop.hbase.wal.WAL;
import org.apache.hadoop.hbase.wal.WALEdit;
import org.apache.hadoop.hbase.wal.WALFactory;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests invocation of the
 * {@link org.apache.hadoop.hbase.coprocessor.MasterObserver} interface hooks at
 * all appropriate times during normal HMaster operations.
 */
@Category({ CoprocessorTests.class, MediumTests.class })
public class TestWALObserver {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestWALObserver.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestWALObserver.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static byte[] TEST_TABLE = Bytes.toBytes("observedTable");

    private static byte[][] TEST_FAMILY = new byte[][]{ Bytes.toBytes("fam1"), Bytes.toBytes("fam2"), Bytes.toBytes("fam3") };

    private static byte[][] TEST_QUALIFIER = new byte[][]{ Bytes.toBytes("q1"), Bytes.toBytes("q2"), Bytes.toBytes("q3") };

    private static byte[][] TEST_VALUE = new byte[][]{ Bytes.toBytes("v1"), Bytes.toBytes("v2"), Bytes.toBytes("v3") };

    private static byte[] TEST_ROW = Bytes.toBytes("testRow");

    @Rule
    public TestName currentTest = new TestName();

    private Configuration conf;

    private FileSystem fs;

    private Path hbaseRootDir;

    private Path hbaseWALRootDir;

    private Path oldLogDir;

    private Path logDir;

    private WALFactory wals;

    /**
     * Test WAL write behavior with WALObserver. The coprocessor monitors a
     * WALEdit written to WAL, and ignore, modify, and add KeyValue's for the
     * WALEdit.
     */
    @Test
    public void testWALObserverWriteToWAL() throws Exception {
        final WAL log = wals.getWAL(null);
        verifyWritesSeen(log, getCoprocessor(log, SampleRegionWALCoprocessor.class), false);
    }

    /**
     * Coprocessors shouldn't get notice of empty waledits.
     */
    @Test
    public void testEmptyWALEditAreNotSeen() throws Exception {
        RegionInfo hri = createBasicHRegionInfo(Bytes.toString(TestWALObserver.TEST_TABLE));
        TableDescriptor htd = createBasic3FamilyHTD(Bytes.toString(TestWALObserver.TEST_TABLE));
        MultiVersionConcurrencyControl mvcc = new MultiVersionConcurrencyControl();
        NavigableMap<byte[], Integer> scopes = new java.util.TreeMap(Bytes.BYTES_COMPARATOR);
        for (byte[] fam : htd.getColumnFamilyNames()) {
            scopes.put(fam, 0);
        }
        WAL log = wals.getWAL(null);
        try {
            SampleRegionWALCoprocessor cp = getCoprocessor(log, SampleRegionWALCoprocessor.class);
            cp.setTestValues(TestWALObserver.TEST_TABLE, null, null, null, null, null, null, null);
            Assert.assertFalse(cp.isPreWALWriteCalled());
            Assert.assertFalse(cp.isPostWALWriteCalled());
            final long now = EnvironmentEdgeManager.currentTime();
            long txid = log.append(hri, new org.apache.hadoop.hbase.wal.WALKeyImpl(hri.getEncodedNameAsBytes(), hri.getTable(), now, mvcc, scopes), new WALEdit(), true);
            log.sync(txid);
            Assert.assertFalse("Empty WALEdit should skip coprocessor evaluation.", cp.isPreWALWriteCalled());
            Assert.assertFalse("Empty WALEdit should skip coprocessor evaluation.", cp.isPostWALWriteCalled());
        } finally {
            log.close();
        }
    }

    /**
     * Test WAL replay behavior with WALObserver.
     */
    @Test
    public void testWALCoprocessorReplay() throws Exception {
        // WAL replay is handled at HRegion::replayRecoveredEdits(), which is
        // ultimately called by HRegion::initialize()
        TableName tableName = TableName.valueOf(currentTest.getMethodName());
        TableDescriptor htd = getBasic3FamilyHTableDescriptor(tableName);
        MultiVersionConcurrencyControl mvcc = new MultiVersionConcurrencyControl();
        // final HRegionInfo hri =
        // createBasic3FamilyHRegionInfo(Bytes.toString(tableName));
        // final HRegionInfo hri1 =
        // createBasic3FamilyHRegionInfo(Bytes.toString(tableName));
        RegionInfo hri = RegionInfoBuilder.newBuilder(tableName).build();
        final Path basedir = FSUtils.getTableDir(this.hbaseRootDir, tableName);
        deleteDir(basedir);
        fs.mkdirs(new Path(basedir, hri.getEncodedName()));
        final Configuration newConf = HBaseConfiguration.create(this.conf);
        // WAL wal = new WAL(this.fs, this.dir, this.oldLogDir, this.conf);
        WAL wal = wals.getWAL(null);
        // Put p = creatPutWith2Families(TEST_ROW);
        WALEdit edit = new WALEdit();
        long now = EnvironmentEdgeManager.currentTime();
        final int countPerFamily = 1000;
        NavigableMap<byte[], Integer> scopes = new java.util.TreeMap(Bytes.BYTES_COMPARATOR);
        for (byte[] fam : htd.getColumnFamilyNames()) {
            scopes.put(fam, 0);
        }
        for (byte[] fam : htd.getColumnFamilyNames()) {
            addWALEdits(tableName, hri, TestWALObserver.TEST_ROW, fam, countPerFamily, EnvironmentEdgeManager.getDelegate(), wal, scopes, mvcc);
        }
        wal.append(hri, new org.apache.hadoop.hbase.wal.WALKeyImpl(hri.getEncodedNameAsBytes(), tableName, now, mvcc, scopes), edit, true);
        // sync to fs.
        wal.sync();
        User user = HBaseTestingUtility.getDifferentUser(newConf, ".replay.wal.secondtime");
        user.runAs(new PrivilegedExceptionAction<Void>() {
            @Override
            public Void run() throws Exception {
                Path p = runWALSplit(newConf);
                TestWALObserver.LOG.info(("WALSplit path == " + p));
                // Make a new wal for new region open.
                final WALFactory wals2 = new WALFactory(conf, ServerName.valueOf(((currentTest.getMethodName()) + "2"), 16010, System.currentTimeMillis()).toString());
                WAL wal2 = wals2.getWAL(null);
                HRegion region = HRegion.openHRegion(newConf, FileSystem.get(newConf), hbaseRootDir, hri, htd, wal2, TestWALObserver.TEST_UTIL.getHBaseCluster().getRegionServer(0), null);
                SampleRegionWALCoprocessor cp2 = region.getCoprocessorHost().findCoprocessor(SampleRegionWALCoprocessor.class);
                // TODO: asserting here is problematic.
                Assert.assertNotNull(cp2);
                Assert.assertTrue(cp2.isPreWALRestoreCalled());
                Assert.assertTrue(cp2.isPostWALRestoreCalled());
                region.close();
                wals2.close();
                return null;
            }
        });
    }

    /**
     * Test to see CP loaded successfully or not. There is a duplication at
     * TestHLog, but the purpose of that one is to see whether the loaded CP will
     * impact existing WAL tests or not.
     */
    @Test
    public void testWALObserverLoaded() throws Exception {
        WAL log = wals.getWAL(null);
        Assert.assertNotNull(getCoprocessor(log, SampleRegionWALCoprocessor.class));
    }

    @Test
    public void testWALObserverRoll() throws Exception {
        final WAL wal = wals.getWAL(null);
        final SampleRegionWALCoprocessor cp = getCoprocessor(wal, SampleRegionWALCoprocessor.class);
        cp.setTestValues(TestWALObserver.TEST_TABLE, null, null, null, null, null, null, null);
        Assert.assertFalse(cp.isPreWALRollCalled());
        Assert.assertFalse(cp.isPostWALRollCalled());
        wal.rollWriter(true);
        Assert.assertTrue(cp.isPreWALRollCalled());
        Assert.assertTrue(cp.isPostWALRollCalled());
    }
}

