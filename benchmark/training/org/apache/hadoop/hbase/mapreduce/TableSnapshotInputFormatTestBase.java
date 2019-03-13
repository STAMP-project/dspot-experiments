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
package org.apache.hadoop.hbase.mapreduce;


import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.io.HFileLink;
import org.apache.hadoop.hbase.regionserver.StoreFileInfo;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.FSUtils;
import org.apache.hadoop.hbase.util.HFileArchiveUtil;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class TableSnapshotInputFormatTestBase {
    private static final Logger LOG = LoggerFactory.getLogger(TableSnapshotInputFormatTestBase.class);

    protected final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    protected static final int NUM_REGION_SERVERS = 2;

    protected static final byte[][] FAMILIES = new byte[][]{ Bytes.toBytes("f1"), Bytes.toBytes("f2") };

    protected FileSystem fs;

    protected Path rootDir;

    @Test
    public void testWithMockedMapReduceSingleRegion() throws Exception {
        testWithMockedMapReduce(UTIL, "testWithMockedMapReduceSingleRegion", 1, 1, 1, true);
    }

    @Test
    public void testWithMockedMapReduceMultiRegion() throws Exception {
        testWithMockedMapReduce(UTIL, "testWithMockedMapReduceMultiRegion", 10, 1, 8, false);
    }

    @Test
    public void testWithMapReduceSingleRegion() throws Exception {
        testWithMapReduce(UTIL, "testWithMapReduceSingleRegion", 1, 1, 1, false);
    }

    @Test
    public void testWithMapReduceMultiRegion() throws Exception {
        testWithMapReduce(UTIL, "testWithMapReduceMultiRegion", 10, 1, 8, false);
    }

    // run the MR job while HBase is offline
    @Test
    public void testWithMapReduceAndOfflineHBaseMultiRegion() throws Exception {
        testWithMapReduce(UTIL, "testWithMapReduceAndOfflineHBaseMultiRegion", 10, 1, 8, true);
    }

    // Test that snapshot restore does not create back references in the HBase root dir.
    @Test
    public void testRestoreSnapshotDoesNotCreateBackRefLinks() throws Exception {
        setupCluster();
        TableName tableName = TableName.valueOf("testRestoreSnapshotDoesNotCreateBackRefLinks");
        String snapshotName = "foo";
        try {
            TableSnapshotInputFormatTestBase.createTableAndSnapshot(UTIL, tableName, snapshotName, getStartRow(), getEndRow(), 1);
            Path tmpTableDir = UTIL.getDataTestDirOnTestFS(snapshotName);
            testRestoreSnapshotDoesNotCreateBackRefLinksInit(tableName, snapshotName, tmpTableDir);
            Path rootDir = FSUtils.getRootDir(UTIL.getConfiguration());
            for (Path regionDir : FSUtils.getRegionDirs(fs, FSUtils.getTableDir(rootDir, tableName))) {
                for (Path storeDir : FSUtils.getFamilyDirs(fs, regionDir)) {
                    for (FileStatus status : fs.listStatus(storeDir)) {
                        System.out.println(status.getPath());
                        if (StoreFileInfo.isValid(status)) {
                            Path archiveStoreDir = HFileArchiveUtil.getStoreArchivePath(UTIL.getConfiguration(), tableName, regionDir.getName(), storeDir.getName());
                            Path path = HFileLink.getBackReferencesDir(storeDir, status.getPath().getName());
                            // assert back references directory is empty
                            Assert.assertFalse(("There is a back reference in " + path), fs.exists(path));
                            path = HFileLink.getBackReferencesDir(archiveStoreDir, status.getPath().getName());
                            // assert back references directory is empty
                            Assert.assertFalse(("There is a back reference in " + path), fs.exists(path));
                        }
                    }
                }
            }
        } finally {
            UTIL.getAdmin().deleteSnapshot(snapshotName);
            UTIL.deleteTable(tableName);
            tearDownCluster();
        }
    }
}

