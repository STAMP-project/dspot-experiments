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
package org.apache.hadoop.hbase.wal;


import WALFactory.WAL_PROVIDER;
import java.io.IOException;
import java.util.HashSet;
import java.util.NavigableMap;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.RegionInfoBuilder;
import org.apache.hadoop.hbase.client.TableDescriptor;
import org.apache.hadoop.hbase.client.TableDescriptorBuilder;
import org.apache.hadoop.hbase.regionserver.MultiVersionConcurrencyControl;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.FSUtils;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ RegionServerTests.class, MediumTests.class })
public class TestFSHLogProvider {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestFSHLogProvider.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestFSHLogProvider.class);

    private static Configuration conf;

    private static FileSystem fs;

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private MultiVersionConcurrencyControl mvcc;

    @Rule
    public final TestName currentTest = new TestName();

    @Test
    public void testGetServerNameFromWALDirectoryName() throws IOException {
        ServerName sn = ServerName.valueOf("hn", 450, 1398);
        String hl = ((FSUtils.getRootDir(TestFSHLogProvider.conf)) + "/") + (AbstractFSWALProvider.getWALDirectoryName(sn.toString()));
        // Must not throw exception
        Assert.assertNull(AbstractFSWALProvider.getServerNameFromWALDirectoryName(TestFSHLogProvider.conf, null));
        Assert.assertNull(AbstractFSWALProvider.getServerNameFromWALDirectoryName(TestFSHLogProvider.conf, FSUtils.getRootDir(TestFSHLogProvider.conf).toUri().toString()));
        Assert.assertNull(AbstractFSWALProvider.getServerNameFromWALDirectoryName(TestFSHLogProvider.conf, ""));
        Assert.assertNull(AbstractFSWALProvider.getServerNameFromWALDirectoryName(TestFSHLogProvider.conf, "                  "));
        Assert.assertNull(AbstractFSWALProvider.getServerNameFromWALDirectoryName(TestFSHLogProvider.conf, hl));
        Assert.assertNull(AbstractFSWALProvider.getServerNameFromWALDirectoryName(TestFSHLogProvider.conf, (hl + "qdf")));
        Assert.assertNull(AbstractFSWALProvider.getServerNameFromWALDirectoryName(TestFSHLogProvider.conf, (("sfqf" + hl) + "qdf")));
        final String wals = "/WALs/";
        ServerName parsed = AbstractFSWALProvider.getServerNameFromWALDirectoryName(TestFSHLogProvider.conf, ((((FSUtils.getRootDir(TestFSHLogProvider.conf).toUri().toString()) + wals) + sn) + "/localhost%2C32984%2C1343316388997.1343316390417"));
        Assert.assertEquals("standard", sn, parsed);
        parsed = AbstractFSWALProvider.getServerNameFromWALDirectoryName(TestFSHLogProvider.conf, (hl + "/qdf"));
        Assert.assertEquals("subdir", sn, parsed);
        parsed = AbstractFSWALProvider.getServerNameFromWALDirectoryName(TestFSHLogProvider.conf, ((((FSUtils.getRootDir(TestFSHLogProvider.conf).toUri().toString()) + wals) + sn) + "-splitting/localhost%3A57020.1340474893931"));
        Assert.assertEquals("split", sn, parsed);
    }

    @Test
    public void testLogCleaning() throws Exception {
        TestFSHLogProvider.LOG.info(currentTest.getMethodName());
        TableDescriptor htd = TableDescriptorBuilder.newBuilder(TableName.valueOf(currentTest.getMethodName())).setColumnFamily(ColumnFamilyDescriptorBuilder.of("row")).build();
        TableDescriptor htd2 = TableDescriptorBuilder.newBuilder(TableName.valueOf(((currentTest.getMethodName()) + "2"))).setColumnFamily(ColumnFamilyDescriptorBuilder.of("row")).build();
        NavigableMap<byte[], Integer> scopes1 = new java.util.TreeMap(Bytes.BYTES_COMPARATOR);
        for (byte[] fam : htd.getColumnFamilyNames()) {
            scopes1.put(fam, 0);
        }
        NavigableMap<byte[], Integer> scopes2 = new java.util.TreeMap(Bytes.BYTES_COMPARATOR);
        for (byte[] fam : htd2.getColumnFamilyNames()) {
            scopes2.put(fam, 0);
        }
        Configuration localConf = new Configuration(TestFSHLogProvider.conf);
        localConf.set(WAL_PROVIDER, FSHLogProvider.class.getName());
        WALFactory wals = new WALFactory(localConf, currentTest.getMethodName());
        try {
            RegionInfo hri = RegionInfoBuilder.newBuilder(htd.getTableName()).build();
            RegionInfo hri2 = RegionInfoBuilder.newBuilder(htd2.getTableName()).build();
            // we want to mix edits from regions, so pick our own identifier.
            WAL log = wals.getWAL(null);
            // Add a single edit and make sure that rolling won't remove the file
            // Before HBASE-3198 it used to delete it
            addEdits(log, hri, htd, 1, scopes1);
            log.rollWriter();
            Assert.assertEquals(1, AbstractFSWALProvider.getNumRolledLogFiles(log));
            // See if there's anything wrong with more than 1 edit
            addEdits(log, hri, htd, 2, scopes1);
            log.rollWriter();
            Assert.assertEquals(2, FSHLogProvider.getNumRolledLogFiles(log));
            // Now mix edits from 2 regions, still no flushing
            addEdits(log, hri, htd, 1, scopes1);
            addEdits(log, hri2, htd2, 1, scopes2);
            addEdits(log, hri, htd, 1, scopes1);
            addEdits(log, hri2, htd2, 1, scopes2);
            log.rollWriter();
            Assert.assertEquals(3, AbstractFSWALProvider.getNumRolledLogFiles(log));
            // Flush the first region, we expect to see the first two files getting
            // archived. We need to append something or writer won't be rolled.
            addEdits(log, hri2, htd2, 1, scopes2);
            log.startCacheFlush(hri.getEncodedNameAsBytes(), htd.getColumnFamilyNames());
            log.completeCacheFlush(hri.getEncodedNameAsBytes());
            log.rollWriter();
            int count = AbstractFSWALProvider.getNumRolledLogFiles(log);
            Assert.assertEquals(2, count);
            // Flush the second region, which removes all the remaining output files
            // since the oldest was completely flushed and the two others only contain
            // flush information
            addEdits(log, hri2, htd2, 1, scopes2);
            log.startCacheFlush(hri2.getEncodedNameAsBytes(), htd2.getColumnFamilyNames());
            log.completeCacheFlush(hri2.getEncodedNameAsBytes());
            log.rollWriter();
            Assert.assertEquals(0, AbstractFSWALProvider.getNumRolledLogFiles(log));
        } finally {
            if (wals != null) {
                wals.close();
            }
        }
    }

    /**
     * Tests wal archiving by adding data, doing flushing/rolling and checking we archive old logs
     * and also don't archive "live logs" (that is, a log with un-flushed entries).
     * <p>
     * This is what it does:
     * It creates two regions, and does a series of inserts along with log rolling.
     * Whenever a WAL is rolled, HLogBase checks previous wals for archiving. A wal is eligible for
     * archiving if for all the regions which have entries in that wal file, have flushed - past
     * their maximum sequence id in that wal file.
     * <p>
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testWALArchiving() throws IOException {
        TestFSHLogProvider.LOG.debug(currentTest.getMethodName());
        TableDescriptor table1 = TableDescriptorBuilder.newBuilder(TableName.valueOf(((currentTest.getMethodName()) + "1"))).setColumnFamily(ColumnFamilyDescriptorBuilder.of("row")).build();
        TableDescriptor table2 = TableDescriptorBuilder.newBuilder(TableName.valueOf(((currentTest.getMethodName()) + "2"))).setColumnFamily(ColumnFamilyDescriptorBuilder.of("row")).build();
        NavigableMap<byte[], Integer> scopes1 = new java.util.TreeMap(Bytes.BYTES_COMPARATOR);
        for (byte[] fam : table1.getColumnFamilyNames()) {
            scopes1.put(fam, 0);
        }
        NavigableMap<byte[], Integer> scopes2 = new java.util.TreeMap(Bytes.BYTES_COMPARATOR);
        for (byte[] fam : table2.getColumnFamilyNames()) {
            scopes2.put(fam, 0);
        }
        Configuration localConf = new Configuration(TestFSHLogProvider.conf);
        localConf.set(WAL_PROVIDER, FSHLogProvider.class.getName());
        WALFactory wals = new WALFactory(localConf, currentTest.getMethodName());
        try {
            WAL wal = wals.getWAL(null);
            Assert.assertEquals(0, AbstractFSWALProvider.getNumRolledLogFiles(wal));
            RegionInfo hri1 = RegionInfoBuilder.newBuilder(table1.getTableName()).build();
            RegionInfo hri2 = RegionInfoBuilder.newBuilder(table2.getTableName()).build();
            // variables to mock region sequenceIds.
            // start with the testing logic: insert a waledit, and roll writer
            addEdits(wal, hri1, table1, 1, scopes1);
            wal.rollWriter();
            // assert that the wal is rolled
            Assert.assertEquals(1, AbstractFSWALProvider.getNumRolledLogFiles(wal));
            // add edits in the second wal file, and roll writer.
            addEdits(wal, hri1, table1, 1, scopes1);
            wal.rollWriter();
            // assert that the wal is rolled
            Assert.assertEquals(2, AbstractFSWALProvider.getNumRolledLogFiles(wal));
            // add a waledit to table1, and flush the region.
            addEdits(wal, hri1, table1, 3, scopes1);
            flushRegion(wal, hri1.getEncodedNameAsBytes(), table1.getColumnFamilyNames());
            // roll log; all old logs should be archived.
            wal.rollWriter();
            Assert.assertEquals(0, AbstractFSWALProvider.getNumRolledLogFiles(wal));
            // add an edit to table2, and roll writer
            addEdits(wal, hri2, table2, 1, scopes2);
            wal.rollWriter();
            Assert.assertEquals(1, AbstractFSWALProvider.getNumRolledLogFiles(wal));
            // add edits for table1, and roll writer
            addEdits(wal, hri1, table1, 2, scopes1);
            wal.rollWriter();
            Assert.assertEquals(2, AbstractFSWALProvider.getNumRolledLogFiles(wal));
            // add edits for table2, and flush hri1.
            addEdits(wal, hri2, table2, 2, scopes2);
            flushRegion(wal, hri1.getEncodedNameAsBytes(), table2.getColumnFamilyNames());
            // the log : region-sequenceId map is
            // log1: region2 (unflushed)
            // log2: region1 (flushed)
            // log3: region2 (unflushed)
            // roll the writer; log2 should be archived.
            wal.rollWriter();
            Assert.assertEquals(2, AbstractFSWALProvider.getNumRolledLogFiles(wal));
            // flush region2, and all logs should be archived.
            addEdits(wal, hri2, table2, 2, scopes2);
            flushRegion(wal, hri2.getEncodedNameAsBytes(), table2.getColumnFamilyNames());
            wal.rollWriter();
            Assert.assertEquals(0, AbstractFSWALProvider.getNumRolledLogFiles(wal));
        } finally {
            if (wals != null) {
                wals.close();
            }
        }
    }

    /**
     * Write to a log file with three concurrent threads and verifying all data is written.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testConcurrentWrites() throws Exception {
        // Run the WPE tool with three threads writing 3000 edits each concurrently.
        // When done, verify that all edits were written.
        int errCode = WALPerformanceEvaluation.innerMain(new Configuration(TestFSHLogProvider.TEST_UTIL.getConfiguration()), new String[]{ "-threads", "3", "-verify", "-noclosefs", "-iterations", "3000" });
        Assert.assertEquals(0, errCode);
    }

    /**
     * Ensure that we can use Set.add to deduplicate WALs
     */
    @Test
    public void setMembershipDedups() throws IOException {
        Configuration localConf = new Configuration(TestFSHLogProvider.conf);
        localConf.set(WAL_PROVIDER, FSHLogProvider.class.getName());
        WALFactory wals = new WALFactory(localConf, currentTest.getMethodName());
        try {
            final Set<WAL> seen = new HashSet<>(1);
            Assert.assertTrue("first attempt to add WAL from default provider should work.", seen.add(wals.getWAL(null)));
            for (int i = 0; i < 1000; i++) {
                Assert.assertFalse(("default wal provider is only supposed to return a single wal, which should " + "compare as .equals itself."), seen.add(wals.getWAL(RegionInfoBuilder.newBuilder(TableName.valueOf(("Table-" + (ThreadLocalRandom.current().nextInt())))).build())));
            }
        } finally {
            wals.close();
        }
    }
}

