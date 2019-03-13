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


import TableName.META_TABLE_NAME;
import java.util.Optional;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static CompactionState.MAJOR;
import static CompactionState.NONE;


/**
 * Class to test asynchronous table admin operations
 *
 * @see TestAsyncTableAdminApi This test and it used to be joined it was taking longer than our
ten minute timeout so they were split.
 */
@RunWith(Parameterized.class)
@Category({ LargeTests.class, ClientTests.class })
public class TestAsyncTableAdminApi2 extends TestAsyncAdminBase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestAsyncTableAdminApi2.class);

    @Test
    public void testDisableCatalogTable() throws Exception {
        try {
            this.admin.disableTable(META_TABLE_NAME).join();
            Assert.fail("Expected to throw ConstraintException");
        } catch (Exception e) {
        }
        // Before the fix for HBASE-6146, the below table creation was failing as the hbase:meta table
        // actually getting disabled by the disableTable() call.
        createTableWithDefaultConf(tableName);
    }

    @Test
    public void testAddColumnFamily() throws Exception {
        // Create a table with two families
        TableDescriptorBuilder builder = TableDescriptorBuilder.newBuilder(tableName);
        builder.setColumnFamily(ColumnFamilyDescriptorBuilder.of(TestAsyncAdminBase.FAMILY_0));
        admin.createTable(builder.build()).join();
        admin.disableTable(tableName).join();
        // Verify the table descriptor
        verifyTableDescriptor(tableName, TestAsyncAdminBase.FAMILY_0);
        // Modify the table removing one family and verify the descriptor
        admin.addColumnFamily(tableName, ColumnFamilyDescriptorBuilder.of(TestAsyncAdminBase.FAMILY_1)).join();
        verifyTableDescriptor(tableName, TestAsyncAdminBase.FAMILY_0, TestAsyncAdminBase.FAMILY_1);
    }

    @Test
    public void testAddSameColumnFamilyTwice() throws Exception {
        // Create a table with one families
        TableDescriptorBuilder builder = TableDescriptorBuilder.newBuilder(tableName);
        builder.setColumnFamily(ColumnFamilyDescriptorBuilder.of(TestAsyncAdminBase.FAMILY_0));
        admin.createTable(builder.build()).join();
        admin.disableTable(tableName).join();
        // Verify the table descriptor
        verifyTableDescriptor(tableName, TestAsyncAdminBase.FAMILY_0);
        // Modify the table removing one family and verify the descriptor
        admin.addColumnFamily(tableName, ColumnFamilyDescriptorBuilder.of(TestAsyncAdminBase.FAMILY_1)).join();
        verifyTableDescriptor(tableName, TestAsyncAdminBase.FAMILY_0, TestAsyncAdminBase.FAMILY_1);
        try {
            // Add same column family again - expect failure
            this.admin.addColumnFamily(tableName, ColumnFamilyDescriptorBuilder.of(TestAsyncAdminBase.FAMILY_1)).join();
            Assert.fail("Delete a non-exist column family should fail");
        } catch (Exception e) {
            // Expected.
        }
    }

    @Test
    public void testModifyColumnFamily() throws Exception {
        TableDescriptorBuilder tdBuilder = TableDescriptorBuilder.newBuilder(tableName);
        ColumnFamilyDescriptor cfd = ColumnFamilyDescriptorBuilder.of(TestAsyncAdminBase.FAMILY_0);
        int blockSize = cfd.getBlocksize();
        admin.createTable(tdBuilder.setColumnFamily(cfd).build()).join();
        admin.disableTable(tableName).join();
        // Verify the table descriptor
        verifyTableDescriptor(tableName, TestAsyncAdminBase.FAMILY_0);
        int newBlockSize = 2 * blockSize;
        cfd = ColumnFamilyDescriptorBuilder.newBuilder(TestAsyncAdminBase.FAMILY_0).setBlocksize(newBlockSize).build();
        // Modify colymn family
        admin.modifyColumnFamily(tableName, cfd).join();
        TableDescriptor htd = admin.getDescriptor(tableName).get();
        ColumnFamilyDescriptor hcfd = htd.getColumnFamily(TestAsyncAdminBase.FAMILY_0);
        Assert.assertTrue(((hcfd.getBlocksize()) == newBlockSize));
    }

    @Test
    public void testModifyNonExistingColumnFamily() throws Exception {
        TableDescriptorBuilder tdBuilder = TableDescriptorBuilder.newBuilder(tableName);
        ColumnFamilyDescriptor cfd = ColumnFamilyDescriptorBuilder.of(TestAsyncAdminBase.FAMILY_0);
        int blockSize = cfd.getBlocksize();
        admin.createTable(tdBuilder.setColumnFamily(cfd).build()).join();
        admin.disableTable(tableName).join();
        // Verify the table descriptor
        verifyTableDescriptor(tableName, TestAsyncAdminBase.FAMILY_0);
        int newBlockSize = 2 * blockSize;
        cfd = ColumnFamilyDescriptorBuilder.newBuilder(TestAsyncAdminBase.FAMILY_1).setBlocksize(newBlockSize).build();
        // Modify a column family that is not in the table.
        try {
            admin.modifyColumnFamily(tableName, cfd).join();
            Assert.fail("Modify a non-exist column family should fail");
        } catch (Exception e) {
            // Expected.
        }
    }

    @Test
    public void testDeleteColumnFamily() throws Exception {
        // Create a table with two families
        TableDescriptorBuilder builder = TableDescriptorBuilder.newBuilder(tableName);
        builder.setColumnFamily(ColumnFamilyDescriptorBuilder.of(TestAsyncAdminBase.FAMILY_0)).setColumnFamily(ColumnFamilyDescriptorBuilder.of(TestAsyncAdminBase.FAMILY_1));
        admin.createTable(builder.build()).join();
        admin.disableTable(tableName).join();
        // Verify the table descriptor
        verifyTableDescriptor(tableName, TestAsyncAdminBase.FAMILY_0, TestAsyncAdminBase.FAMILY_1);
        // Modify the table removing one family and verify the descriptor
        admin.deleteColumnFamily(tableName, TestAsyncAdminBase.FAMILY_1).join();
        verifyTableDescriptor(tableName, TestAsyncAdminBase.FAMILY_0);
    }

    @Test
    public void testDeleteSameColumnFamilyTwice() throws Exception {
        // Create a table with two families
        TableDescriptorBuilder builder = TableDescriptorBuilder.newBuilder(tableName);
        builder.setColumnFamily(ColumnFamilyDescriptorBuilder.of(TestAsyncAdminBase.FAMILY_0)).setColumnFamily(ColumnFamilyDescriptorBuilder.of(TestAsyncAdminBase.FAMILY_1));
        admin.createTable(builder.build()).join();
        admin.disableTable(tableName).join();
        // Verify the table descriptor
        verifyTableDescriptor(tableName, TestAsyncAdminBase.FAMILY_0, TestAsyncAdminBase.FAMILY_1);
        // Modify the table removing one family and verify the descriptor
        admin.deleteColumnFamily(tableName, TestAsyncAdminBase.FAMILY_1).join();
        verifyTableDescriptor(tableName, TestAsyncAdminBase.FAMILY_0);
        try {
            // Delete again - expect failure
            admin.deleteColumnFamily(tableName, TestAsyncAdminBase.FAMILY_1).join();
            Assert.fail("Delete a non-exist column family should fail");
        } catch (Exception e) {
            // Expected.
        }
    }

    @Test
    public void testTableAvailableWithRandomSplitKeys() throws Exception {
        createTableWithDefaultConf(tableName);
        byte[][] splitKeys = new byte[1][];
        splitKeys = new byte[][]{ new byte[]{ 1, 1, 1 }, new byte[]{ 2, 2, 2 } };
        boolean tableAvailable = admin.isTableAvailable(tableName, splitKeys).get();
        Assert.assertFalse("Table should be created with 1 row in META", tableAvailable);
    }

    @Test
    public void testCompactionTimestamps() throws Exception {
        createTableWithDefaultConf(tableName);
        AsyncTable<?> table = TestAsyncAdminBase.ASYNC_CONN.getTable(tableName);
        Optional<Long> ts = admin.getLastMajorCompactionTimestamp(tableName).get();
        Assert.assertFalse(ts.isPresent());
        Put p = new Put(Bytes.toBytes("row1"));
        p.addColumn(TestAsyncAdminBase.FAMILY, Bytes.toBytes("q"), Bytes.toBytes("v"));
        table.put(p).join();
        ts = admin.getLastMajorCompactionTimestamp(tableName).get();
        // no files written -> no data
        Assert.assertFalse(ts.isPresent());
        admin.flush(tableName).join();
        ts = admin.getLastMajorCompactionTimestamp(tableName).get();
        // still 0, we flushed a file, but no major compaction happened
        Assert.assertFalse(ts.isPresent());
        byte[] regionName = TestAsyncAdminBase.ASYNC_CONN.getRegionLocator(tableName).getRegionLocation(Bytes.toBytes("row1")).get().getRegion().getRegionName();
        Optional<Long> ts1 = admin.getLastMajorCompactionTimestampForRegion(regionName).get();
        Assert.assertFalse(ts1.isPresent());
        p = new Put(Bytes.toBytes("row2"));
        p.addColumn(TestAsyncAdminBase.FAMILY, Bytes.toBytes("q"), Bytes.toBytes("v"));
        table.put(p).join();
        admin.flush(tableName).join();
        ts1 = admin.getLastMajorCompactionTimestamp(tableName).get();
        // make sure the region API returns the same value, as the old file is still around
        Assert.assertFalse(ts1.isPresent());
        for (int i = 0; i < 3; i++) {
            table.put(p).join();
            admin.flush(tableName).join();
        }
        admin.majorCompact(tableName).join();
        long curt = System.currentTimeMillis();
        long waitTime = 10000;
        long endt = curt + waitTime;
        CompactionState state = admin.getCompactionState(tableName).get();
        TestAsyncAdminBase.LOG.info(("Current compaction state 1 is " + state));
        while ((state == (NONE)) && (curt < endt)) {
            Thread.sleep(100);
            state = admin.getCompactionState(tableName).get();
            curt = System.currentTimeMillis();
            TestAsyncAdminBase.LOG.info(("Current compaction state 2 is " + state));
        } 
        // Now, should have the right compaction state, let's wait until the compaction is done
        if (state == (MAJOR)) {
            state = admin.getCompactionState(tableName).get();
            TestAsyncAdminBase.LOG.info(("Current compaction state 3 is " + state));
            while ((state != (NONE)) && (curt < endt)) {
                Thread.sleep(10);
                state = admin.getCompactionState(tableName).get();
                TestAsyncAdminBase.LOG.info(("Current compaction state 4 is " + state));
            } 
        }
        // Sleep to wait region server report
        Thread.sleep(((TestAsyncAdminBase.TEST_UTIL.getConfiguration().getInt("hbase.regionserver.msginterval", (3 * 1000))) * 2));
        ts = admin.getLastMajorCompactionTimestamp(tableName).get();
        // after a compaction our earliest timestamp will have progressed forward
        Assert.assertTrue(ts.isPresent());
        Assert.assertTrue(((ts.get()) > 0));
        // region api still the same
        ts1 = admin.getLastMajorCompactionTimestampForRegion(regionName).get();
        Assert.assertTrue(ts1.isPresent());
        Assert.assertEquals(ts.get(), ts1.get());
        table.put(p).join();
        admin.flush(tableName).join();
        ts = admin.getLastMajorCompactionTimestamp(tableName).join();
        Assert.assertTrue(ts.isPresent());
        Assert.assertEquals(ts.get(), ts1.get());
        ts1 = admin.getLastMajorCompactionTimestampForRegion(regionName).get();
        Assert.assertTrue(ts1.isPresent());
        Assert.assertEquals(ts.get(), ts1.get());
    }
}

