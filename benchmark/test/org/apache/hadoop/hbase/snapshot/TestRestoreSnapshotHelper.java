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


import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.util.FSUtils;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test the restore/clone operation from a file-system point of view.
 */
@Category({ RegionServerTests.class, MediumTests.class })
public class TestRestoreSnapshotHelper {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRestoreSnapshotHelper.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestRestoreSnapshotHelper.class);

    protected static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    protected static final String TEST_HFILE = "abc";

    protected Configuration conf;

    protected Path archiveDir;

    protected FileSystem fs;

    protected Path rootDir;

    @Test
    public void testRestore() throws IOException {
        restoreAndVerify("snapshot", "testRestore");
    }

    @Test
    public void testRestoreWithNamespace() throws IOException {
        restoreAndVerify("snapshot", "namespace1:testRestoreWithNamespace");
    }

    @Test
    public void testNoHFileLinkInRootDir() throws IOException {
        rootDir = TestRestoreSnapshotHelper.TEST_UTIL.getDefaultRootDirPath();
        FSUtils.setRootDir(conf, rootDir);
        fs = rootDir.getFileSystem(conf);
        TableName tableName = TableName.valueOf("testNoHFileLinkInRootDir");
        String snapshotName = (tableName.getNameAsString()) + "-snapshot";
        createTableAndSnapshot(tableName, snapshotName);
        Path restoreDir = new Path("/hbase/.tmp-restore");
        RestoreSnapshotHelper.copySnapshotForScanner(conf, fs, rootDir, restoreDir, snapshotName);
        checkNoHFileLinkInTableDir(tableName);
    }
}

