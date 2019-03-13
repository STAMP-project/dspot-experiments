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


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.io.encoding.DataBlockEncoding;
import org.apache.hadoop.hbase.regionserver.BloomType;
import org.apache.hadoop.hbase.snapshot.SnapshotTestingUtils;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test class to verify that metadata is consistent before and after a snapshot attempt.
 */
@Category({ MediumTests.class, ClientTests.class })
public class TestSnapshotMetadata {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestSnapshotMetadata.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestSnapshotMetadata.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static final int NUM_RS = 2;

    private static final String STRING_TABLE_NAME = "TestSnapshotMetadata";

    private static final String MAX_VERSIONS_FAM_STR = "fam_max_columns";

    private static final byte[] MAX_VERSIONS_FAM = Bytes.toBytes(TestSnapshotMetadata.MAX_VERSIONS_FAM_STR);

    private static final String COMPRESSED_FAM_STR = "fam_compressed";

    private static final byte[] COMPRESSED_FAM = Bytes.toBytes(TestSnapshotMetadata.COMPRESSED_FAM_STR);

    private static final String BLOCKSIZE_FAM_STR = "fam_blocksize";

    private static final byte[] BLOCKSIZE_FAM = Bytes.toBytes(TestSnapshotMetadata.BLOCKSIZE_FAM_STR);

    private static final String BLOOMFILTER_FAM_STR = "fam_bloomfilter";

    private static final byte[] BLOOMFILTER_FAM = Bytes.toBytes(TestSnapshotMetadata.BLOOMFILTER_FAM_STR);

    private static final String TEST_CONF_CUSTOM_VALUE = "TestCustomConf";

    private static final String TEST_CUSTOM_VALUE = "TestCustomValue";

    private static final byte[][] families = new byte[][]{ TestSnapshotMetadata.MAX_VERSIONS_FAM, TestSnapshotMetadata.BLOOMFILTER_FAM, TestSnapshotMetadata.COMPRESSED_FAM, TestSnapshotMetadata.BLOCKSIZE_FAM };

    private static final DataBlockEncoding DATA_BLOCK_ENCODING_TYPE = DataBlockEncoding.FAST_DIFF;

    private static final BloomType BLOOM_TYPE = BloomType.ROW;

    private static final int BLOCK_SIZE = 98;

    private static final int MAX_VERSIONS = 8;

    private Admin admin;

    private String originalTableDescription;

    private HTableDescriptor originalTableDescriptor;

    TableName originalTableName;

    private static FileSystem fs;

    private static Path rootDir;

    /**
     * Verify that the describe for a cloned table matches the describe from the original.
     */
    @Test
    public void testDescribeMatchesAfterClone() throws Exception {
        // Clone the original table
        final String clonedTableNameAsString = "clone" + (originalTableName);
        final TableName clonedTableName = TableName.valueOf(clonedTableNameAsString);
        final String snapshotNameAsString = ("snapshot" + (originalTableName)) + (System.currentTimeMillis());
        final byte[] snapshotName = Bytes.toBytes(snapshotNameAsString);
        // restore the snapshot into a cloned table and examine the output
        List<byte[]> familiesList = new ArrayList<>();
        Collections.addAll(familiesList, TestSnapshotMetadata.families);
        // Create a snapshot in which all families are empty
        /* onlineSnapshot= */
        SnapshotTestingUtils.createSnapshotAndValidate(admin, originalTableName, null, familiesList, snapshotNameAsString, TestSnapshotMetadata.rootDir, TestSnapshotMetadata.fs, false);
        admin.cloneSnapshot(snapshotName, clonedTableName);
        Table clonedTable = TestSnapshotMetadata.UTIL.getConnection().getTable(clonedTableName);
        HTableDescriptor cloneHtd = admin.getTableDescriptor(clonedTableName);
        Assert.assertEquals(originalTableDescription.replace(originalTableName.getNameAsString(), clonedTableNameAsString), cloneHtd.toStringCustomizedValues());
        // Verify the custom fields
        Assert.assertEquals(originalTableDescriptor.getValues().size(), cloneHtd.getValues().size());
        Assert.assertEquals(originalTableDescriptor.getConfiguration().size(), cloneHtd.getConfiguration().size());
        Assert.assertEquals(TestSnapshotMetadata.TEST_CUSTOM_VALUE, cloneHtd.getValue(TestSnapshotMetadata.TEST_CUSTOM_VALUE));
        Assert.assertEquals(TestSnapshotMetadata.TEST_CONF_CUSTOM_VALUE, cloneHtd.getConfigurationValue(TestSnapshotMetadata.TEST_CONF_CUSTOM_VALUE));
        Assert.assertEquals(originalTableDescriptor.getValues(), cloneHtd.getValues());
        Assert.assertEquals(originalTableDescriptor.getConfiguration(), cloneHtd.getConfiguration());
        admin.enableTable(originalTableName);
        clonedTable.close();
    }

    /**
     * Verify that the describe for a restored table matches the describe for one the original.
     */
    @Test
    public void testDescribeMatchesAfterRestore() throws Exception {
        runRestoreWithAdditionalMetadata(false);
    }

    /**
     * Verify that if metadata changed after a snapshot was taken, that the old metadata replaces the
     * new metadata during a restore
     */
    @Test
    public void testDescribeMatchesAfterMetadataChangeAndRestore() throws Exception {
        runRestoreWithAdditionalMetadata(true);
    }

    /**
     * Verify that when the table is empty, making metadata changes after the restore does not affect
     * the restored table's original metadata
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testDescribeOnEmptyTableMatchesAfterMetadataChangeAndRestore() throws Exception {
        runRestoreWithAdditionalMetadata(true, false);
    }
}

