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
package org.apache.hadoop.hbase.master.procedure;


import java.io.IOException;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.InvalidFamilyOperationException;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;


/**
 * Verify that the HTableDescriptor is updated after
 * addColumn(), deleteColumn() and modifyTable() operations.
 */
@Category({ MasterTests.class, LargeTests.class })
public class TestTableDescriptorModificationFromClient {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestTableDescriptorModificationFromClient.class);

    @Rule
    public TestName name = new TestName();

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static TableName TABLE_NAME = null;

    private static final byte[] FAMILY_0 = Bytes.toBytes("cf0");

    private static final byte[] FAMILY_1 = Bytes.toBytes("cf1");

    @Test
    public void testModifyTable() throws IOException {
        Admin admin = TestTableDescriptorModificationFromClient.TEST_UTIL.getAdmin();
        // Create a table with one family
        HTableDescriptor baseHtd = new HTableDescriptor(TestTableDescriptorModificationFromClient.TABLE_NAME);
        baseHtd.addFamily(new HColumnDescriptor(TestTableDescriptorModificationFromClient.FAMILY_0));
        admin.createTable(baseHtd);
        admin.disableTable(TestTableDescriptorModificationFromClient.TABLE_NAME);
        try {
            // Verify the table descriptor
            verifyTableDescriptor(TestTableDescriptorModificationFromClient.TABLE_NAME, TestTableDescriptorModificationFromClient.FAMILY_0);
            // Modify the table adding another family and verify the descriptor
            HTableDescriptor modifiedHtd = new HTableDescriptor(TestTableDescriptorModificationFromClient.TABLE_NAME);
            modifiedHtd.addFamily(new HColumnDescriptor(TestTableDescriptorModificationFromClient.FAMILY_0));
            modifiedHtd.addFamily(new HColumnDescriptor(TestTableDescriptorModificationFromClient.FAMILY_1));
            admin.modifyTable(TestTableDescriptorModificationFromClient.TABLE_NAME, modifiedHtd);
            verifyTableDescriptor(TestTableDescriptorModificationFromClient.TABLE_NAME, TestTableDescriptorModificationFromClient.FAMILY_0, TestTableDescriptorModificationFromClient.FAMILY_1);
        } finally {
            admin.deleteTable(TestTableDescriptorModificationFromClient.TABLE_NAME);
        }
    }

    @Test
    public void testAddColumn() throws IOException {
        Admin admin = TestTableDescriptorModificationFromClient.TEST_UTIL.getAdmin();
        // Create a table with two families
        HTableDescriptor baseHtd = new HTableDescriptor(TestTableDescriptorModificationFromClient.TABLE_NAME);
        baseHtd.addFamily(new HColumnDescriptor(TestTableDescriptorModificationFromClient.FAMILY_0));
        admin.createTable(baseHtd);
        admin.disableTable(TestTableDescriptorModificationFromClient.TABLE_NAME);
        try {
            // Verify the table descriptor
            verifyTableDescriptor(TestTableDescriptorModificationFromClient.TABLE_NAME, TestTableDescriptorModificationFromClient.FAMILY_0);
            // Modify the table removing one family and verify the descriptor
            admin.addColumnFamily(TestTableDescriptorModificationFromClient.TABLE_NAME, new HColumnDescriptor(TestTableDescriptorModificationFromClient.FAMILY_1));
            verifyTableDescriptor(TestTableDescriptorModificationFromClient.TABLE_NAME, TestTableDescriptorModificationFromClient.FAMILY_0, TestTableDescriptorModificationFromClient.FAMILY_1);
        } finally {
            admin.deleteTable(TestTableDescriptorModificationFromClient.TABLE_NAME);
        }
    }

    @Test
    public void testAddSameColumnFamilyTwice() throws IOException {
        Admin admin = TestTableDescriptorModificationFromClient.TEST_UTIL.getAdmin();
        // Create a table with one families
        HTableDescriptor baseHtd = new HTableDescriptor(TestTableDescriptorModificationFromClient.TABLE_NAME);
        baseHtd.addFamily(new HColumnDescriptor(TestTableDescriptorModificationFromClient.FAMILY_0));
        admin.createTable(baseHtd);
        admin.disableTable(TestTableDescriptorModificationFromClient.TABLE_NAME);
        try {
            // Verify the table descriptor
            verifyTableDescriptor(TestTableDescriptorModificationFromClient.TABLE_NAME, TestTableDescriptorModificationFromClient.FAMILY_0);
            // Modify the table removing one family and verify the descriptor
            admin.addColumnFamily(TestTableDescriptorModificationFromClient.TABLE_NAME, new HColumnDescriptor(TestTableDescriptorModificationFromClient.FAMILY_1));
            verifyTableDescriptor(TestTableDescriptorModificationFromClient.TABLE_NAME, TestTableDescriptorModificationFromClient.FAMILY_0, TestTableDescriptorModificationFromClient.FAMILY_1);
            try {
                // Add same column family again - expect failure
                admin.addColumnFamily(TestTableDescriptorModificationFromClient.TABLE_NAME, new HColumnDescriptor(TestTableDescriptorModificationFromClient.FAMILY_1));
                Assert.fail("Delete a non-exist column family should fail");
            } catch (InvalidFamilyOperationException e) {
                // Expected.
            }
        } finally {
            admin.deleteTable(TestTableDescriptorModificationFromClient.TABLE_NAME);
        }
    }

    @Test
    public void testModifyColumnFamily() throws IOException {
        Admin admin = TestTableDescriptorModificationFromClient.TEST_UTIL.getAdmin();
        HColumnDescriptor cfDescriptor = new HColumnDescriptor(TestTableDescriptorModificationFromClient.FAMILY_0);
        int blockSize = cfDescriptor.getBlocksize();
        // Create a table with one families
        HTableDescriptor baseHtd = new HTableDescriptor(TestTableDescriptorModificationFromClient.TABLE_NAME);
        baseHtd.addFamily(cfDescriptor);
        admin.createTable(baseHtd);
        admin.disableTable(TestTableDescriptorModificationFromClient.TABLE_NAME);
        try {
            // Verify the table descriptor
            verifyTableDescriptor(TestTableDescriptorModificationFromClient.TABLE_NAME, TestTableDescriptorModificationFromClient.FAMILY_0);
            int newBlockSize = 2 * blockSize;
            cfDescriptor.setBlocksize(newBlockSize);
            // Modify colymn family
            admin.modifyColumnFamily(TestTableDescriptorModificationFromClient.TABLE_NAME, cfDescriptor);
            HTableDescriptor htd = admin.getTableDescriptor(TestTableDescriptorModificationFromClient.TABLE_NAME);
            HColumnDescriptor hcfd = htd.getFamily(TestTableDescriptorModificationFromClient.FAMILY_0);
            Assert.assertTrue(((hcfd.getBlocksize()) == newBlockSize));
        } finally {
            admin.deleteTable(TestTableDescriptorModificationFromClient.TABLE_NAME);
        }
    }

    @Test
    public void testModifyNonExistingColumnFamily() throws IOException {
        Admin admin = TestTableDescriptorModificationFromClient.TEST_UTIL.getAdmin();
        HColumnDescriptor cfDescriptor = new HColumnDescriptor(TestTableDescriptorModificationFromClient.FAMILY_1);
        int blockSize = cfDescriptor.getBlocksize();
        // Create a table with one families
        HTableDescriptor baseHtd = new HTableDescriptor(TestTableDescriptorModificationFromClient.TABLE_NAME);
        baseHtd.addFamily(new HColumnDescriptor(TestTableDescriptorModificationFromClient.FAMILY_0));
        admin.createTable(baseHtd);
        admin.disableTable(TestTableDescriptorModificationFromClient.TABLE_NAME);
        try {
            // Verify the table descriptor
            verifyTableDescriptor(TestTableDescriptorModificationFromClient.TABLE_NAME, TestTableDescriptorModificationFromClient.FAMILY_0);
            int newBlockSize = 2 * blockSize;
            cfDescriptor.setBlocksize(newBlockSize);
            // Modify a column family that is not in the table.
            try {
                admin.modifyColumnFamily(TestTableDescriptorModificationFromClient.TABLE_NAME, cfDescriptor);
                Assert.fail("Modify a non-exist column family should fail");
            } catch (InvalidFamilyOperationException e) {
                // Expected.
            }
        } finally {
            admin.deleteTable(TestTableDescriptorModificationFromClient.TABLE_NAME);
        }
    }

    @Test
    public void testDeleteColumn() throws IOException {
        Admin admin = TestTableDescriptorModificationFromClient.TEST_UTIL.getAdmin();
        // Create a table with two families
        HTableDescriptor baseHtd = new HTableDescriptor(TestTableDescriptorModificationFromClient.TABLE_NAME);
        baseHtd.addFamily(new HColumnDescriptor(TestTableDescriptorModificationFromClient.FAMILY_0));
        baseHtd.addFamily(new HColumnDescriptor(TestTableDescriptorModificationFromClient.FAMILY_1));
        admin.createTable(baseHtd);
        admin.disableTable(TestTableDescriptorModificationFromClient.TABLE_NAME);
        try {
            // Verify the table descriptor
            verifyTableDescriptor(TestTableDescriptorModificationFromClient.TABLE_NAME, TestTableDescriptorModificationFromClient.FAMILY_0, TestTableDescriptorModificationFromClient.FAMILY_1);
            // Modify the table removing one family and verify the descriptor
            admin.deleteColumnFamily(TestTableDescriptorModificationFromClient.TABLE_NAME, TestTableDescriptorModificationFromClient.FAMILY_1);
            verifyTableDescriptor(TestTableDescriptorModificationFromClient.TABLE_NAME, TestTableDescriptorModificationFromClient.FAMILY_0);
        } finally {
            admin.deleteTable(TestTableDescriptorModificationFromClient.TABLE_NAME);
        }
    }

    @Test
    public void testDeleteSameColumnFamilyTwice() throws IOException {
        Admin admin = TestTableDescriptorModificationFromClient.TEST_UTIL.getAdmin();
        // Create a table with two families
        HTableDescriptor baseHtd = new HTableDescriptor(TestTableDescriptorModificationFromClient.TABLE_NAME);
        baseHtd.addFamily(new HColumnDescriptor(TestTableDescriptorModificationFromClient.FAMILY_0));
        baseHtd.addFamily(new HColumnDescriptor(TestTableDescriptorModificationFromClient.FAMILY_1));
        admin.createTable(baseHtd);
        admin.disableTable(TestTableDescriptorModificationFromClient.TABLE_NAME);
        try {
            // Verify the table descriptor
            verifyTableDescriptor(TestTableDescriptorModificationFromClient.TABLE_NAME, TestTableDescriptorModificationFromClient.FAMILY_0, TestTableDescriptorModificationFromClient.FAMILY_1);
            // Modify the table removing one family and verify the descriptor
            admin.deleteColumnFamily(TestTableDescriptorModificationFromClient.TABLE_NAME, TestTableDescriptorModificationFromClient.FAMILY_1);
            verifyTableDescriptor(TestTableDescriptorModificationFromClient.TABLE_NAME, TestTableDescriptorModificationFromClient.FAMILY_0);
            try {
                // Delete again - expect failure
                admin.deleteColumnFamily(TestTableDescriptorModificationFromClient.TABLE_NAME, TestTableDescriptorModificationFromClient.FAMILY_1);
                Assert.fail("Delete a non-exist column family should fail");
            } catch (Exception e) {
                // Expected.
            }
        } finally {
            admin.deleteTable(TestTableDescriptorModificationFromClient.TABLE_NAME);
        }
    }
}

