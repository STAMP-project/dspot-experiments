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


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.RegionInfoBuilder;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category(MediumTests.class)
public class TestWALRootDir {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestWALRootDir.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestWALRootDir.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static Configuration conf;

    private static FileSystem fs;

    private static FileSystem walFs;

    private static final TableName tableName = TableName.valueOf("TestWALWALDir");

    private static final byte[] rowName = Bytes.toBytes("row");

    private static final byte[] family = Bytes.toBytes("column");

    private static Path walRootDir;

    private static Path rootDir;

    private static WALFactory wals;

    @Test
    public void testWALRootDir() throws Exception {
        RegionInfo regionInfo = RegionInfoBuilder.newBuilder(TestWALRootDir.tableName).build();
        TestWALRootDir.wals = new WALFactory(TestWALRootDir.conf, "testWALRootDir");
        WAL log = TestWALRootDir.wals.getWAL(regionInfo);
        Assert.assertEquals(1, getWALFiles(TestWALRootDir.walFs, TestWALRootDir.walRootDir).size());
        byte[] value = Bytes.toBytes("value");
        WALEdit edit = new WALEdit();
        edit.add(new org.apache.hadoop.hbase.KeyValue(TestWALRootDir.rowName, TestWALRootDir.family, Bytes.toBytes("1"), System.currentTimeMillis(), value));
        long txid = log.append(regionInfo, getWalKey(System.currentTimeMillis(), regionInfo, 0), edit, true);
        log.sync(txid);
        Assert.assertEquals("Expect 1 log have been created", 1, getWALFiles(TestWALRootDir.walFs, TestWALRootDir.walRootDir).size());
        log.rollWriter();
        // Create 1 more WAL
        Assert.assertEquals(2, getWALFiles(TestWALRootDir.walFs, new Path(TestWALRootDir.walRootDir, HConstants.HREGION_LOGDIR_NAME)).size());
        edit.add(new org.apache.hadoop.hbase.KeyValue(TestWALRootDir.rowName, TestWALRootDir.family, Bytes.toBytes("2"), System.currentTimeMillis(), value));
        txid = log.append(regionInfo, getWalKey(System.currentTimeMillis(), regionInfo, 1), edit, true);
        log.sync(txid);
        log.rollWriter();
        log.shutdown();
        Assert.assertEquals("Expect 3 logs in WALs dir", 3, getWALFiles(TestWALRootDir.walFs, new Path(TestWALRootDir.walRootDir, HConstants.HREGION_LOGDIR_NAME)).size());
    }
}

