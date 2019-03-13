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
package org.apache.hadoop.hbase.snapshot;


import HMaster.MASTER;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.TestTableName;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.master.snapshot.SnapshotHFileCleaner;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.FSUtils;
import org.apache.hbase.thirdparty.com.google.common.collect.ImmutableMap;
import org.apache.hbase.thirdparty.com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test Case for HBASE-21387
 */
@Category({ LargeTests.class })
public class TestSnapshotWhenChoreCleaning {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestSnapshotWhenChoreCleaning.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final Configuration CONF = TestSnapshotWhenChoreCleaning.TEST_UTIL.getConfiguration();

    private static final Logger LOG = LoggerFactory.getLogger(TestSnapshotClientRetries.class);

    private static final TableName TABLE_NAME = TableName.valueOf("testTable");

    private static final int MAX_SPLIT_KEYS_NUM = 100;

    private static final byte[] FAMILY = Bytes.toBytes("family");

    private static final byte[] QUALIFIER = Bytes.toBytes("qualifier");

    private static final byte[] VALUE = Bytes.toBytes("value");

    private static Table TABLE;

    @Rule
    public TestTableName TEST_TABLE = new TestTableName();

    @Test
    public void testSnapshotWhenSnapshotHFileCleanerRunning() throws Exception {
        // Load data and flush to generate huge number of HFiles.
        TestSnapshotWhenChoreCleaning.loadDataAndFlush();
        SnapshotHFileCleaner cleaner = new SnapshotHFileCleaner();
        cleaner.init(ImmutableMap.of(MASTER, TestSnapshotWhenChoreCleaning.TEST_UTIL.getHBaseCluster().getMaster()));
        cleaner.setConf(TestSnapshotWhenChoreCleaning.CONF);
        FileSystem fs = FSUtils.getCurrentFileSystem(TestSnapshotWhenChoreCleaning.CONF);
        List<Path> fileNames = TestSnapshotWhenChoreCleaning.listHFileNames(fs, FSUtils.getTableDir(FSUtils.getRootDir(TestSnapshotWhenChoreCleaning.CONF), TestSnapshotWhenChoreCleaning.TABLE_NAME));
        List<FileStatus> files = new ArrayList<>();
        for (Path fileName : fileNames) {
            files.add(fs.getFileStatus(fileName));
        }
        TestSnapshotWhenChoreCleaning.TEST_UTIL.getAdmin().snapshot("snapshotName_prev", TestSnapshotWhenChoreCleaning.TABLE_NAME);
        Assert.assertEquals(Lists.newArrayList(cleaner.getDeletableFiles(files)).size(), 0);
        TestSnapshotWhenChoreCleaning.TEST_UTIL.getAdmin().deleteSnapshot("snapshotName_prev");
        cleaner.getFileCacheForTesting().triggerCacheRefreshForTesting();
        Assert.assertEquals(Lists.newArrayList(cleaner.getDeletableFiles(files)).size(), 100);
        Runnable snapshotRunnable = () -> {
            try {
                // The thread will be busy on taking snapshot;
                for (int k = 0; k < 5; k++) {
                    TestSnapshotWhenChoreCleaning.TEST_UTIL.getAdmin().snapshot(("snapshotName_" + k), TestSnapshotWhenChoreCleaning.TABLE_NAME);
                }
            } catch (Exception e) {
                TestSnapshotWhenChoreCleaning.LOG.error("Snapshot failed: ", e);
            }
        };
        final AtomicBoolean success = new AtomicBoolean(true);
        Runnable cleanerRunnable = () -> {
            try {
                while (!(TestSnapshotWhenChoreCleaning.isAnySnapshots(fs))) {
                    TestSnapshotWhenChoreCleaning.LOG.info("Not found any snapshot, sleep 100ms");
                    Thread.sleep(100);
                } 
                for (int k = 0; k < 5; k++) {
                    cleaner.getFileCacheForTesting().triggerCacheRefreshForTesting();
                    Iterable<FileStatus> toDeleteFiles = cleaner.getDeletableFiles(files);
                    List<FileStatus> deletableFiles = Lists.newArrayList(toDeleteFiles);
                    TestSnapshotWhenChoreCleaning.LOG.info(("Size of deletableFiles is: " + (deletableFiles.size())));
                    for (int i = 0; i < (deletableFiles.size()); i++) {
                        TestSnapshotWhenChoreCleaning.LOG.debug("toDeleteFiles[{}] is: {}", i, deletableFiles.get(i));
                    }
                    if ((deletableFiles.size()) > 0) {
                        success.set(false);
                    }
                }
            } catch (Exception e) {
                TestSnapshotWhenChoreCleaning.LOG.error("Chore cleaning failed: ", e);
            }
        };
        Thread t1 = new Thread(snapshotRunnable);
        t1.start();
        Thread t2 = new Thread(cleanerRunnable);
        t2.start();
        t1.join();
        t2.join();
        Assert.assertTrue(success.get());
    }
}

