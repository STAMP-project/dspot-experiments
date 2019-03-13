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


import SnapshotMock.SnapshotBuilder;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseCommonTestingUtility;
import org.apache.hadoop.hbase.snapshot.SnapshotTestingUtils.SnapshotMock;
import org.apache.hadoop.hbase.testclassification.MapReduceTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test Export Snapshot Tool
 */
@Category({ MapReduceTests.class, MediumTests.class })
public class TestExportSnapshotNoCluster {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestExportSnapshotNoCluster.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestExportSnapshotNoCluster.class);

    protected static final HBaseCommonTestingUtility TEST_UTIL = new HBaseCommonTestingUtility();

    private static FileSystem fs;

    private static Path testDir;

    /**
     * Mock a snapshot with files in the archive dir,
     * two regions, and one reference file.
     */
    @Test
    public void testSnapshotWithRefsExportFileSystemState() throws Exception {
        SnapshotMock snapshotMock = new SnapshotMock(TestExportSnapshotNoCluster.TEST_UTIL.getConfiguration(), TestExportSnapshotNoCluster.fs, TestExportSnapshotNoCluster.testDir);
        SnapshotMock.SnapshotBuilder builder = snapshotMock.createSnapshotV2("tableWithRefsV1", "tableWithRefsV1");
        testSnapshotWithRefsExportFileSystemState(builder);
        snapshotMock = new SnapshotMock(TestExportSnapshotNoCluster.TEST_UTIL.getConfiguration(), TestExportSnapshotNoCluster.fs, TestExportSnapshotNoCluster.testDir);
        builder = snapshotMock.createSnapshotV2("tableWithRefsV2", "tableWithRefsV2");
        testSnapshotWithRefsExportFileSystemState(builder);
    }
}

