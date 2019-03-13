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
package org.apache.hadoop.hbase.client;


import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test to verify that the cloned table is independent of the table from which it was cloned
 */
@Category({ LargeTests.class, ClientTests.class })
public class TestSnapshotCloneIndependence {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestSnapshotCloneIndependence.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestSnapshotCloneIndependence.class);

    @Rule
    public TestName testName = new TestName();

    protected static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    protected static final int NUM_RS = 2;

    private static final String STRING_TABLE_NAME = "test";

    private static final String TEST_FAM_STR = "fam";

    protected static final byte[] TEST_FAM = Bytes.toBytes(TestSnapshotCloneIndependence.TEST_FAM_STR);

    private static final int CLEANER_INTERVAL = 100;

    private FileSystem fs;

    private Path rootDir;

    private Admin admin;

    private TableName originalTableName;

    private Table originalTable;

    private TableName cloneTableName;

    private int countOriginalTable;

    String snapshotNameAsString;

    byte[] snapshotName;

    /**
     * Verify that adding data to the cloned table will not affect the original, and vice-versa when
     * it is taken as an online snapshot.
     */
    @Test
    public void testOnlineSnapshotAppendIndependent() throws Exception {
        createAndCloneSnapshot(true);
        runTestSnapshotAppendIndependent();
    }

    /**
     * Verify that adding data to the cloned table will not affect the original, and vice-versa when
     * it is taken as an offline snapshot.
     */
    @Test
    public void testOfflineSnapshotAppendIndependent() throws Exception {
        createAndCloneSnapshot(false);
        runTestSnapshotAppendIndependent();
    }

    /**
     * Verify that adding metadata to the cloned table will not affect the original, and vice-versa
     * when it is taken as an online snapshot.
     */
    @Test
    public void testOnlineSnapshotMetadataChangesIndependent() throws Exception {
        createAndCloneSnapshot(true);
        runTestSnapshotMetadataChangesIndependent();
    }

    /**
     * Verify that adding netadata to the cloned table will not affect the original, and vice-versa
     * when is taken as an online snapshot.
     */
    @Test
    public void testOfflineSnapshotMetadataChangesIndependent() throws Exception {
        createAndCloneSnapshot(false);
        runTestSnapshotMetadataChangesIndependent();
    }

    /**
     * Verify that region operations, in this case splitting a region, are independent between the
     * cloned table and the original.
     */
    @Test
    public void testOfflineSnapshotRegionOperationsIndependent() throws Exception {
        createAndCloneSnapshot(false);
        runTestRegionOperationsIndependent();
    }

    /**
     * Verify that region operations, in this case splitting a region, are independent between the
     * cloned table and the original.
     */
    @Test
    public void testOnlineSnapshotRegionOperationsIndependent() throws Exception {
        createAndCloneSnapshot(true);
        runTestRegionOperationsIndependent();
    }

    @Test
    public void testOfflineSnapshotDeleteIndependent() throws Exception {
        createAndCloneSnapshot(false);
        runTestSnapshotDeleteIndependent();
    }

    @Test
    public void testOnlineSnapshotDeleteIndependent() throws Exception {
        createAndCloneSnapshot(true);
        runTestSnapshotDeleteIndependent();
    }
}

