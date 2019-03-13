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


import HBaseTestingUtility.fam1;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.FSUtils;
import org.apache.hadoop.util.ToolRunner;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ MediumTests.class, RegionServerTests.class })
public class TestCompactionTool {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestCompactionTool.class);

    private final HBaseTestingUtility testUtil = new HBaseTestingUtility();

    private HRegion region;

    private static final byte[] qualifier = Bytes.toBytes("qf");

    private Path rootDir;

    private final TableName tableName = TableName.valueOf(getClass().getSimpleName());

    @Test
    public void testCompactedFilesArchived() throws Exception {
        for (int i = 0; i < 10; i++) {
            this.putAndFlush(i);
        }
        HStore store = region.getStore(fam1);
        Assert.assertEquals(10, store.getStorefilesCount());
        Path tableDir = FSUtils.getTableDir(rootDir, region.getRegionInfo().getTable());
        FileSystem fs = store.getFileSystem();
        String storePath = (((tableDir + "/") + (region.getRegionInfo().getEncodedName())) + "/") + (Bytes.toString(fam1));
        FileStatus[] regionDirFiles = fs.listStatus(new Path(storePath));
        Assert.assertEquals(10, regionDirFiles.length);
        int result = ToolRunner.run(HBaseConfiguration.create(), new CompactionTool(), new String[]{ "-compactOnce", "-major", storePath });
        Assert.assertEquals(0, result);
        regionDirFiles = fs.listStatus(new Path(storePath));
        Assert.assertEquals(1, regionDirFiles.length);
    }
}

