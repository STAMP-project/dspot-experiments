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


import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.FSUtils;
import org.apache.hadoop.hdfs.DFSClient;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static HRegionFileSystem.REGION_MERGES_DIR;
import static HRegionFileSystem.REGION_SPLITS_DIR;
import static HRegionFileSystem.REGION_TEMP_DIR;


@Category({ RegionServerTests.class, MediumTests.class })
public class TestHdfsSnapshotHRegion {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestHdfsSnapshotHRegion.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final String SNAPSHOT_NAME = "foo_snapshot";

    private Table table;

    public static final TableName TABLE_NAME = TableName.valueOf("foo");

    public static final byte[] FAMILY = Bytes.toBytes("f1");

    private DFSClient client;

    private String baseDir;

    @Test
    public void testOpeningReadOnlyRegionBasic() throws Exception {
        String snapshotDir = client.createSnapshot(baseDir, TestHdfsSnapshotHRegion.SNAPSHOT_NAME);
        RegionInfo firstRegion = TestHdfsSnapshotHRegion.TEST_UTIL.getConnection().getRegionLocator(table.getName()).getAllRegionLocations().stream().findFirst().get().getRegion();
        Path tableDir = FSUtils.getTableDir(new Path(snapshotDir), TestHdfsSnapshotHRegion.TABLE_NAME);
        HRegion snapshottedRegion = openSnapshotRegion(firstRegion, tableDir);
        Assert.assertNotNull(snapshottedRegion);
        snapshottedRegion.close();
    }

    @Test
    public void testSnapshottingWithTmpSplitsAndMergeDirectoriesPresent() throws Exception {
        // lets get a region and create those directories and make sure we ignore them
        RegionInfo firstRegion = TestHdfsSnapshotHRegion.TEST_UTIL.getConnection().getRegionLocator(table.getName()).getAllRegionLocations().stream().findFirst().get().getRegion();
        String encodedName = firstRegion.getEncodedName();
        Path tableDir = FSUtils.getTableDir(TestHdfsSnapshotHRegion.TEST_UTIL.getDefaultRootDirPath(), TestHdfsSnapshotHRegion.TABLE_NAME);
        Path regionDirectoryPath = new Path(tableDir, encodedName);
        TestHdfsSnapshotHRegion.TEST_UTIL.getTestFileSystem().create(new Path(regionDirectoryPath, REGION_TEMP_DIR));
        TestHdfsSnapshotHRegion.TEST_UTIL.getTestFileSystem().create(new Path(regionDirectoryPath, REGION_SPLITS_DIR));
        TestHdfsSnapshotHRegion.TEST_UTIL.getTestFileSystem().create(new Path(regionDirectoryPath, REGION_MERGES_DIR));
        // now snapshot
        String snapshotDir = client.createSnapshot(baseDir, "foo_snapshot");
        // everything should still open just fine
        HRegion snapshottedRegion = openSnapshotRegion(firstRegion, FSUtils.getTableDir(new Path(snapshotDir), TestHdfsSnapshotHRegion.TABLE_NAME));
        Assert.assertNotNull(snapshottedRegion);// no errors and the region should open

        snapshottedRegion.close();
    }
}

