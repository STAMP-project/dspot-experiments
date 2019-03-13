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


import HConstants.HBASE_DIR;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.shaded.protobuf.generated.SnapshotProtos.SnapshotDescription;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test that the {@link SnapshotDescription} helper is helping correctly.
 */
@Category({ RegionServerTests.class, MediumTests.class })
public class TestSnapshotDescriptionUtils {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestSnapshotDescriptionUtils.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static FileSystem fs;

    private static Path root;

    private static final Logger LOG = LoggerFactory.getLogger(TestSnapshotDescriptionUtils.class);

    @Test
    public void testValidateMissingTableName() throws IOException {
        Configuration conf = new Configuration(false);
        try {
            SnapshotDescriptionUtils.validate(SnapshotDescription.newBuilder().setName("fail").build(), conf);
            Assert.fail("Snapshot was considered valid without a table name");
        } catch (IllegalArgumentException e) {
            TestSnapshotDescriptionUtils.LOG.debug("Correctly failed when snapshot doesn't have a tablename");
        }
    }

    /**
     * Test that we throw an exception if there is no working snapshot directory when we attempt to
     * 'complete' the snapshot
     *
     * @throws Exception
     * 		on failure
     */
    @Test
    public void testCompleteSnapshotWithNoSnapshotDirectoryFailure() throws Exception {
        Path snapshotDir = new Path(TestSnapshotDescriptionUtils.root, HConstants.SNAPSHOT_DIR_NAME);
        Path tmpDir = new Path(snapshotDir, ".tmp");
        Path workingDir = new Path(tmpDir, "not_a_snapshot");
        Assert.assertFalse((("Already have working snapshot dir: " + workingDir) + " but shouldn't. Test file leak?"), TestSnapshotDescriptionUtils.fs.exists(workingDir));
        SnapshotDescription snapshot = SnapshotDescription.newBuilder().setName("snapshot").build();
        try {
            SnapshotDescriptionUtils.completeSnapshot(snapshot, TestSnapshotDescriptionUtils.root, workingDir, TestSnapshotDescriptionUtils.fs);
            Assert.fail("Shouldn't successfully complete move of a non-existent directory.");
        } catch (IOException e) {
            TestSnapshotDescriptionUtils.LOG.info(("Correctly failed to move non-existant directory: " + (e.getMessage())));
        }
    }

    @Test
    public void testIsSubDirectoryWorks() {
        Path rootDir = new Path("hdfs://root/.hbase-snapshot/");
        Assert.assertFalse(SnapshotDescriptionUtils.isSubDirectoryOf(rootDir, rootDir));
        Assert.assertFalse(SnapshotDescriptionUtils.isSubDirectoryOf(new Path("hdfs://root/.hbase-snapshotdir"), rootDir));
        Assert.assertFalse(SnapshotDescriptionUtils.isSubDirectoryOf(new Path("hdfs://root/.hbase-snapshot"), rootDir));
        Assert.assertFalse(SnapshotDescriptionUtils.isSubDirectoryOf(new Path("hdfs://.hbase-snapshot"), rootDir));
        Assert.assertFalse(SnapshotDescriptionUtils.isSubDirectoryOf(new Path("hdfs://.hbase-snapshot/.tmp"), rootDir));
        Assert.assertFalse(SnapshotDescriptionUtils.isSubDirectoryOf(new Path("hdfs://root"), rootDir));
        Assert.assertTrue(SnapshotDescriptionUtils.isSubDirectoryOf(new Path("hdfs://root/.hbase-snapshot/.tmp"), rootDir));
        Assert.assertTrue(SnapshotDescriptionUtils.isSubDirectoryOf(new Path("hdfs://root/.hbase-snapshot/.tmp/snapshot"), rootDir));
        Assert.assertFalse(SnapshotDescriptionUtils.isSubDirectoryOf(new Path("s3://root/.hbase-snapshot/"), rootDir));
        Assert.assertFalse(SnapshotDescriptionUtils.isSubDirectoryOf(new Path("s3://root"), rootDir));
        Assert.assertFalse(SnapshotDescriptionUtils.isSubDirectoryOf(new Path("s3://root/.hbase-snapshot/.tmp/snapshot"), rootDir));
    }

    @Test
    public void testIsWithinWorkingDir() {
        Configuration conf = new Configuration();
        conf.set(HBASE_DIR, "hdfs://root/");
        Assert.assertFalse(SnapshotDescriptionUtils.isWithinDefaultWorkingDir(new Path("hdfs://root/"), conf));
        Assert.assertFalse(SnapshotDescriptionUtils.isWithinDefaultWorkingDir(new Path("hdfs://root/.hbase-snapshotdir"), conf));
        Assert.assertFalse(SnapshotDescriptionUtils.isWithinDefaultWorkingDir(new Path("hdfs://root/.hbase-snapshot"), conf));
        Assert.assertFalse(SnapshotDescriptionUtils.isWithinDefaultWorkingDir(new Path("hdfs://.hbase-snapshot"), conf));
        Assert.assertFalse(SnapshotDescriptionUtils.isWithinDefaultWorkingDir(new Path("hdfs://.hbase-snapshot/.tmp"), conf));
        Assert.assertFalse(SnapshotDescriptionUtils.isWithinDefaultWorkingDir(new Path("hdfs://root"), conf));
        Assert.assertTrue(SnapshotDescriptionUtils.isWithinDefaultWorkingDir(new Path("hdfs://root/.hbase-snapshot/.tmp"), conf));
        Assert.assertTrue(SnapshotDescriptionUtils.isWithinDefaultWorkingDir(new Path("hdfs://root/.hbase-snapshot/.tmp/snapshot"), conf));
        Assert.assertFalse(SnapshotDescriptionUtils.isWithinDefaultWorkingDir(new Path("s3://root/.hbase-snapshot/"), conf));
        Assert.assertFalse(SnapshotDescriptionUtils.isWithinDefaultWorkingDir(new Path("s3://root"), conf));
        Assert.assertFalse(SnapshotDescriptionUtils.isWithinDefaultWorkingDir(new Path("s3://root/.hbase-snapshot/.tmp/snapshot"), conf));
    }
}

