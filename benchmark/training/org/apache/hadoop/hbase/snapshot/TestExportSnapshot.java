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


import ExportSnapshot.CONF_SKIP_TMP;
import ExportSnapshot.Testing.CONF_TEST_FAILURE;
import ExportSnapshot.Testing.CONF_TEST_FAILURE_COUNT;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.VerySlowMapReduceTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test Export Snapshot Tool
 */
@Category({ VerySlowMapReduceTests.class, LargeTests.class })
public class TestExportSnapshot {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestExportSnapshot.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestExportSnapshot.class);

    protected static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    protected static final byte[] FAMILY = Bytes.toBytes("cf");

    @Rule
    public final TestName testName = new TestName();

    protected TableName tableName;

    private byte[] emptySnapshotName;

    private byte[] snapshotName;

    private int tableNumFiles;

    private Admin admin;

    protected interface RegionPredicate {
        boolean evaluate(final RegionInfo regionInfo);
    }

    /**
     * Verify if exported snapshot and copied files matches the original one.
     */
    @Test
    public void testExportFileSystemState() throws Exception {
        testExportFileSystemState(tableName, snapshotName, snapshotName, tableNumFiles);
    }

    @Test
    public void testExportFileSystemStateWithSkipTmp() throws Exception {
        TestExportSnapshot.TEST_UTIL.getConfiguration().setBoolean(CONF_SKIP_TMP, true);
        try {
            testExportFileSystemState(tableName, snapshotName, snapshotName, tableNumFiles);
        } finally {
            TestExportSnapshot.TEST_UTIL.getConfiguration().setBoolean(CONF_SKIP_TMP, false);
        }
    }

    @Test
    public void testEmptyExportFileSystemState() throws Exception {
        testExportFileSystemState(tableName, emptySnapshotName, emptySnapshotName, 0);
    }

    @Test
    public void testConsecutiveExports() throws Exception {
        Path copyDir = getLocalDestinationDir();
        testExportFileSystemState(tableName, snapshotName, snapshotName, tableNumFiles, copyDir, false);
        testExportFileSystemState(tableName, snapshotName, snapshotName, tableNumFiles, copyDir, true);
        TestExportSnapshot.removeExportDir(copyDir);
    }

    @Test
    public void testExportWithTargetName() throws Exception {
        final byte[] targetName = Bytes.toBytes("testExportWithTargetName");
        testExportFileSystemState(tableName, snapshotName, targetName, tableNumFiles);
    }

    /**
     * Check that ExportSnapshot will succeed if something fails but the retry succeed.
     */
    @Test
    public void testExportRetry() throws Exception {
        Path copyDir = getLocalDestinationDir();
        FileSystem fs = FileSystem.get(copyDir.toUri(), new Configuration());
        copyDir = copyDir.makeQualified(fs);
        Configuration conf = new Configuration(TestExportSnapshot.TEST_UTIL.getConfiguration());
        conf.setBoolean(CONF_TEST_FAILURE, true);
        conf.setInt(CONF_TEST_FAILURE_COUNT, 2);
        conf.setInt("mapreduce.map.maxattempts", 3);
        TestExportSnapshot.testExportFileSystemState(conf, tableName, snapshotName, snapshotName, tableNumFiles, TestExportSnapshot.TEST_UTIL.getDefaultRootDirPath(), copyDir, true, getBypassRegionPredicate(), true);
    }

    /**
     * Check that ExportSnapshot will fail if we inject failure more times than MR will retry.
     */
    @Test
    public void testExportFailure() throws Exception {
        Path copyDir = getLocalDestinationDir();
        FileSystem fs = FileSystem.get(copyDir.toUri(), new Configuration());
        copyDir = copyDir.makeQualified(fs);
        Configuration conf = new Configuration(TestExportSnapshot.TEST_UTIL.getConfiguration());
        conf.setBoolean(CONF_TEST_FAILURE, true);
        conf.setInt(CONF_TEST_FAILURE_COUNT, 4);
        conf.setInt("mapreduce.map.maxattempts", 3);
        TestExportSnapshot.testExportFileSystemState(conf, tableName, snapshotName, snapshotName, tableNumFiles, TestExportSnapshot.TEST_UTIL.getDefaultRootDirPath(), copyDir, true, getBypassRegionPredicate(), false);
    }
}

