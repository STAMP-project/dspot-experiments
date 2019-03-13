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


import java.util.NavigableMap;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.RegionInfoBuilder;
import org.apache.hadoop.hbase.regionserver.MultiVersionConcurrencyControl;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.wal.WAL;
import org.apache.hadoop.hbase.wal.WALEdit;
import org.apache.hadoop.hbase.wal.WALFactory;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Test that the actions are called while playing with an WAL
 */
@Category({ RegionServerTests.class, SmallTests.class })
public class TestWALActionsListener {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestWALActionsListener.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final byte[] SOME_BYTES = Bytes.toBytes("t");

    private static Configuration conf;

    private static Path rootDir;

    private static Path walRootDir;

    private static FileSystem fs;

    private static FileSystem logFs;

    /**
     * Add a bunch of dummy data and roll the logs every two insert. We
     * should end up with 10 rolled files (plus the roll called in
     * the constructor). Also test adding a listener while it's running.
     */
    @Test
    public void testActionListener() throws Exception {
        TestWALActionsListener.DummyWALActionsListener observer = new TestWALActionsListener.DummyWALActionsListener();
        final WALFactory wals = new WALFactory(TestWALActionsListener.conf, "testActionListener");
        wals.getWALProvider().addWALActionsListener(observer);
        TestWALActionsListener.DummyWALActionsListener laterobserver = new TestWALActionsListener.DummyWALActionsListener();
        RegionInfo hri = RegionInfoBuilder.newBuilder(TableName.valueOf(TestWALActionsListener.SOME_BYTES)).setStartKey(TestWALActionsListener.SOME_BYTES).setEndKey(TestWALActionsListener.SOME_BYTES).build();
        final WAL wal = wals.getWAL(hri);
        MultiVersionConcurrencyControl mvcc = new MultiVersionConcurrencyControl();
        for (int i = 0; i < 20; i++) {
            byte[] b = Bytes.toBytes((i + ""));
            KeyValue kv = new KeyValue(b, b, b);
            WALEdit edit = new WALEdit();
            edit.add(kv);
            NavigableMap<byte[], Integer> scopes = new java.util.TreeMap(Bytes.BYTES_COMPARATOR);
            scopes.put(b, 0);
            long txid = wal.append(hri, new org.apache.hadoop.hbase.wal.WALKeyImpl(hri.getEncodedNameAsBytes(), TableName.valueOf(b), 0, mvcc, scopes), edit, true);
            wal.sync(txid);
            if (i == 10) {
                wal.registerWALActionsListener(laterobserver);
            }
            if ((i % 2) == 0) {
                wal.rollWriter();
            }
        }
        wal.close();
        Assert.assertEquals(11, observer.preLogRollCounter);
        Assert.assertEquals(11, observer.postLogRollCounter);
        Assert.assertEquals(5, laterobserver.preLogRollCounter);
        Assert.assertEquals(5, laterobserver.postLogRollCounter);
        Assert.assertEquals(1, observer.closedCount);
    }

    /**
     * Just counts when methods are called
     */
    public static class DummyWALActionsListener implements WALActionsListener {
        public int preLogRollCounter = 0;

        public int postLogRollCounter = 0;

        public int closedCount = 0;

        @Override
        public void preLogRoll(Path oldFile, Path newFile) {
            (preLogRollCounter)++;
        }

        @Override
        public void postLogRoll(Path oldFile, Path newFile) {
            (postLogRollCounter)++;
        }

        @Override
        public void logCloseRequested() {
            (closedCount)++;
        }
    }
}

