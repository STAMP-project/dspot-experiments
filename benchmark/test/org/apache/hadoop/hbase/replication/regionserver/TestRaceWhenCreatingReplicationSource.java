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
package org.apache.hadoop.hbase.replication.regionserver;


import HConstants.REPLICATION_SCOPE_GLOBAL;
import WAL.Reader;
import WALProvider.Writer;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.UUID;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.client.TableDescriptorBuilder;
import org.apache.hadoop.hbase.replication.BaseReplicationEndpoint;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.ReplicationTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.JVMClusterUtil.RegionServerThread;
import org.apache.hadoop.hbase.wal.WAL;
import org.apache.hadoop.hbase.wal.WAL.Entry;
import org.apache.hadoop.hbase.wal.WALFactory;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Testcase for HBASE-20624.
 */
@Category({ ReplicationTests.class, MediumTests.class })
public class TestRaceWhenCreatingReplicationSource {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRaceWhenCreatingReplicationSource.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static String PEER_ID = "1";

    private static TableName TABLE_NAME = TableName.valueOf("race");

    private static byte[] CF = Bytes.toBytes("CF");

    private static byte[] CQ = Bytes.toBytes("CQ");

    private static FileSystem FS;

    private static Path LOG_PATH;

    private static Writer WRITER;

    private static volatile boolean NULL_UUID = true;

    public static final class LocalReplicationEndpoint extends BaseReplicationEndpoint {
        private static final UUID PEER_UUID = getRandomUUID();

        @Override
        public UUID getPeerUUID() {
            if (TestRaceWhenCreatingReplicationSource.NULL_UUID) {
                return null;
            } else {
                return TestRaceWhenCreatingReplicationSource.LocalReplicationEndpoint.PEER_UUID;
            }
        }

        @Override
        public boolean replicate(ReplicateContext replicateContext) {
            synchronized(TestRaceWhenCreatingReplicationSource.WRITER) {
                try {
                    for (Entry entry : replicateContext.getEntries()) {
                        TestRaceWhenCreatingReplicationSource.WRITER.append(entry);
                    }
                    TestRaceWhenCreatingReplicationSource.WRITER.sync(false);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
            return true;
        }

        @Override
        public void start() {
            startAsync();
        }

        @Override
        public void stop() {
            stopAsync();
        }

        @Override
        protected void doStart() {
            notifyStarted();
        }

        @Override
        protected void doStop() {
            notifyStopped();
        }
    }

    @Test
    public void testRace() throws Exception {
        TestRaceWhenCreatingReplicationSource.UTIL.waitFor(30000, new org.apache.hadoop.hbase.Waiter.ExplainingPredicate<Exception>() {
            @Override
            public boolean evaluate() throws Exception {
                for (RegionServerThread t : TestRaceWhenCreatingReplicationSource.UTIL.getMiniHBaseCluster().getRegionServerThreads()) {
                    ReplicationSource source = ((ReplicationSource) (getReplicationManager().getSource(TestRaceWhenCreatingReplicationSource.PEER_ID)));
                    if ((source == null) || ((source.getReplicationEndpoint()) == null)) {
                        return false;
                    }
                }
                return true;
            }

            @Override
            public String explainFailure() throws Exception {
                return "Replication source has not been initialized yet";
            }
        });
        TestRaceWhenCreatingReplicationSource.UTIL.getAdmin().createTable(TableDescriptorBuilder.newBuilder(TestRaceWhenCreatingReplicationSource.TABLE_NAME).setColumnFamily(ColumnFamilyDescriptorBuilder.newBuilder(TestRaceWhenCreatingReplicationSource.CF).setScope(REPLICATION_SCOPE_GLOBAL).build()).build());
        TestRaceWhenCreatingReplicationSource.UTIL.waitTableAvailable(TestRaceWhenCreatingReplicationSource.TABLE_NAME);
        try (Table table = TestRaceWhenCreatingReplicationSource.UTIL.getConnection().getTable(TestRaceWhenCreatingReplicationSource.TABLE_NAME)) {
            table.put(new org.apache.hadoop.hbase.client.Put(Bytes.toBytes(1)).addColumn(TestRaceWhenCreatingReplicationSource.CF, TestRaceWhenCreatingReplicationSource.CQ, Bytes.toBytes(1)));
        }
        TestRaceWhenCreatingReplicationSource.NULL_UUID = false;
        waitFor(30000, new org.apache.hadoop.hbase.Waiter.ExplainingPredicate<Exception>() {
            @Override
            public boolean evaluate() throws Exception {
                try (WAL.Reader reader = WALFactory.createReader(TestRaceWhenCreatingReplicationSource.FS, TestRaceWhenCreatingReplicationSource.LOG_PATH, TestRaceWhenCreatingReplicationSource.UTIL.getConfiguration())) {
                    return (reader.next()) != null;
                } catch (IOException e) {
                    return false;
                }
            }

            @Override
            public String explainFailure() throws Exception {
                return "Replication has not catched up";
            }
        });
        try (WAL.Reader reader = WALFactory.createReader(TestRaceWhenCreatingReplicationSource.FS, TestRaceWhenCreatingReplicationSource.LOG_PATH, TestRaceWhenCreatingReplicationSource.UTIL.getConfiguration())) {
            Cell cell = reader.next().getEdit().getCells().get(0);
            Assert.assertEquals(1, Bytes.toInt(cell.getRowArray(), cell.getRowOffset(), cell.getRowLength()));
            Assert.assertArrayEquals(TestRaceWhenCreatingReplicationSource.CF, CellUtil.cloneFamily(cell));
            Assert.assertArrayEquals(TestRaceWhenCreatingReplicationSource.CQ, CellUtil.cloneQualifier(cell));
            Assert.assertEquals(1, Bytes.toInt(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength()));
        }
    }
}

