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


import SnapshotManifest.SNAPSHOT_MANIFEST_SIZE_LIMIT_CONF_KEY;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.shaded.protobuf.generated.SnapshotProtos.SnapshotDescription;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static SnapshotManifest.DATA_MANIFEST_NAME;


@Category({ MasterTests.class, SmallTests.class })
public class TestSnapshotManifest {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestSnapshotManifest.class);

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private static final String TABLE_NAME_STR = "testSnapshotManifest";

    private static final TableName TABLE_NAME = TableName.valueOf(TestSnapshotManifest.TABLE_NAME_STR);

    private static final int TEST_NUM_REGIONS = 16000;

    private static final int TEST_NUM_REGIONFILES = 1000000;

    private static HBaseTestingUtility TEST_UTIL;

    private Configuration conf;

    private FileSystem fs;

    private Path rootDir;

    private Path snapshotDir;

    private SnapshotDescription snapshotDesc;

    private SnapshotTestingUtils.SnapshotMock.SnapshotBuilder builder;

    @Test
    public void testReadSnapshotManifest() throws IOException {
        Path p = createDataManifest();
        try {
            SnapshotManifest.open(conf, fs, snapshotDir, snapshotDesc);
            Assert.fail("fail to test snapshot manifest because message size is too small.");
        } catch (CorruptedSnapshotException cse) {
            try {
                conf.setInt(SNAPSHOT_MANIFEST_SIZE_LIMIT_CONF_KEY, ((128 * 1024) * 1024));
                SnapshotManifest.open(conf, fs, snapshotDir, snapshotDesc);
                LOG.info("open snapshot manifest succeed.");
            } catch (CorruptedSnapshotException cse2) {
                Assert.fail("fail to take snapshot because Manifest proto-message too large.");
            }
        } finally {
            fs.delete(p, false);
        }
    }

    @Test
    public void testReadSnapshotRegionManifest() throws IOException {
        // remove datamanifest file
        fs.delete(new Path(snapshotDir, DATA_MANIFEST_NAME), true);
        Path regionPath = createRegionManifest();
        try {
            conf.setInt(SNAPSHOT_MANIFEST_SIZE_LIMIT_CONF_KEY, ((128 * 1024) * 1024));
            SnapshotManifest.open(conf, fs, snapshotDir, snapshotDesc);
        } catch (CorruptedSnapshotException e) {
            Assert.fail("fail to test snapshot manifest because region message size is too small.");
        } finally {
            fs.delete(regionPath, false);
        }
    }
}

