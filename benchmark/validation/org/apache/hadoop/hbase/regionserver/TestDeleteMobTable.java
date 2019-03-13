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


import java.util.Random;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;


@Category(MediumTests.class)
public class TestDeleteMobTable {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestDeleteMobTable.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final byte[] FAMILY = Bytes.toBytes("family");

    private static final byte[] QF = Bytes.toBytes("qualifier");

    private static Random random = new Random();

    @Rule
    public TestName name = new TestName();

    @Test
    public void testDeleteMobTable() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        HTableDescriptor htd = createTableDescriptor(tableName, true);
        HColumnDescriptor hcd = htd.getFamily(TestDeleteMobTable.FAMILY);
        String fileName = null;
        Table table = createTableWithOneFile(htd);
        try {
            // the mob file exists
            Assert.assertEquals(1, countMobFiles(tableName, hcd.getNameAsString()));
            Assert.assertEquals(0, countArchiveMobFiles(tableName, hcd.getNameAsString()));
            fileName = assertHasOneMobRow(table, tableName, hcd.getNameAsString());
            Assert.assertFalse(mobArchiveExist(tableName, hcd.getNameAsString(), fileName));
            Assert.assertTrue(mobTableDirExist(tableName));
        } finally {
            table.close();
            TestDeleteMobTable.TEST_UTIL.deleteTable(tableName);
        }
        Assert.assertFalse(TestDeleteMobTable.TEST_UTIL.getAdmin().tableExists(tableName));
        Assert.assertEquals(0, countMobFiles(tableName, hcd.getNameAsString()));
        Assert.assertEquals(1, countArchiveMobFiles(tableName, hcd.getNameAsString()));
        Assert.assertTrue(mobArchiveExist(tableName, hcd.getNameAsString(), fileName));
        Assert.assertFalse(mobTableDirExist(tableName));
    }

    @Test
    public void testDeleteNonMobTable() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        HTableDescriptor htd = createTableDescriptor(tableName, false);
        HColumnDescriptor hcd = htd.getFamily(TestDeleteMobTable.FAMILY);
        Table table = createTableWithOneFile(htd);
        try {
            // the mob file doesn't exist
            Assert.assertEquals(0, countMobFiles(tableName, hcd.getNameAsString()));
            Assert.assertEquals(0, countArchiveMobFiles(tableName, hcd.getNameAsString()));
            Assert.assertFalse(mobTableDirExist(tableName));
        } finally {
            table.close();
            TestDeleteMobTable.TEST_UTIL.deleteTable(tableName);
        }
        Assert.assertFalse(TestDeleteMobTable.TEST_UTIL.getAdmin().tableExists(tableName));
        Assert.assertEquals(0, countMobFiles(tableName, hcd.getNameAsString()));
        Assert.assertEquals(0, countArchiveMobFiles(tableName, hcd.getNameAsString()));
        Assert.assertFalse(mobTableDirExist(tableName));
    }

    @Test
    public void testMobFamilyDelete() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        HTableDescriptor htd = createTableDescriptor(tableName, true);
        HColumnDescriptor hcd = htd.getFamily(TestDeleteMobTable.FAMILY);
        htd.addFamily(new HColumnDescriptor(Bytes.toBytes("family2")));
        Table table = createTableWithOneFile(htd);
        try {
            // the mob file exists
            Assert.assertEquals(1, countMobFiles(tableName, hcd.getNameAsString()));
            Assert.assertEquals(0, countArchiveMobFiles(tableName, hcd.getNameAsString()));
            String fileName = assertHasOneMobRow(table, tableName, hcd.getNameAsString());
            Assert.assertFalse(mobArchiveExist(tableName, hcd.getNameAsString(), fileName));
            Assert.assertTrue(mobTableDirExist(tableName));
            TestDeleteMobTable.TEST_UTIL.getAdmin().deleteColumnFamily(tableName, TestDeleteMobTable.FAMILY);
            Assert.assertEquals(0, countMobFiles(tableName, hcd.getNameAsString()));
            Assert.assertEquals(1, countArchiveMobFiles(tableName, hcd.getNameAsString()));
            Assert.assertTrue(mobArchiveExist(tableName, hcd.getNameAsString(), fileName));
            Assert.assertFalse(mobColumnFamilyDirExist(tableName, hcd.getNameAsString()));
        } finally {
            table.close();
            TestDeleteMobTable.TEST_UTIL.deleteTable(tableName);
        }
    }
}

