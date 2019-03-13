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


import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.Threads;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;


@Category({ MediumTests.class, ClientTests.class })
public class TestSplitOrMergeAtTableLevel {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestSplitOrMergeAtTableLevel.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static byte[] FAMILY = Bytes.toBytes("testFamily");

    @Rule
    public TestName name = new TestName();

    private static Admin admin;

    @Test
    public void testTableSplitSwitch() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        TableDescriptor tableDesc = TableDescriptorBuilder.newBuilder(tableName).setColumnFamily(ColumnFamilyDescriptorBuilder.of(TestSplitOrMergeAtTableLevel.FAMILY)).setSplitEnabled(false).build();
        // create a table with split disabled
        Table t = TestSplitOrMergeAtTableLevel.TEST_UTIL.createTable(tableDesc, null);
        TestSplitOrMergeAtTableLevel.TEST_UTIL.waitTableAvailable(tableName);
        // load data into the table
        TestSplitOrMergeAtTableLevel.TEST_UTIL.loadTable(t, TestSplitOrMergeAtTableLevel.FAMILY, false);
        Assert.assertTrue(((TestSplitOrMergeAtTableLevel.admin.getRegions(tableName).size()) == 1));
        // check that we have split disabled
        Assert.assertFalse(TestSplitOrMergeAtTableLevel.admin.getDescriptor(tableName).isSplitEnabled());
        trySplitAndEnsureItFails(tableName);
        enableTableSplit(tableName);
        trySplitAndEnsureItIsSuccess(tableName);
    }

    @Test
    public void testTableSplitSwitchForPreSplittedTable() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        // create a table with split disabled
        TableDescriptor tableDesc = TableDescriptorBuilder.newBuilder(tableName).setColumnFamily(ColumnFamilyDescriptorBuilder.of(TestSplitOrMergeAtTableLevel.FAMILY)).setSplitEnabled(false).build();
        Table t = TestSplitOrMergeAtTableLevel.TEST_UTIL.createTable(tableDesc, new byte[][]{ Bytes.toBytes(10) });
        TestSplitOrMergeAtTableLevel.TEST_UTIL.waitTableAvailable(tableName);
        // load data into the table
        TestSplitOrMergeAtTableLevel.TEST_UTIL.loadTable(t, TestSplitOrMergeAtTableLevel.FAMILY, false);
        Assert.assertTrue(((TestSplitOrMergeAtTableLevel.admin.getRegions(tableName).size()) == 2));
        // check that we have split disabled
        Assert.assertFalse(TestSplitOrMergeAtTableLevel.admin.getDescriptor(tableName).isSplitEnabled());
        trySplitAndEnsureItFails(tableName);
        enableTableSplit(tableName);
        trySplitAndEnsureItIsSuccess(tableName);
    }

    @Test
    public void testTableMergeSwitch() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        TableDescriptor tableDesc = TableDescriptorBuilder.newBuilder(tableName).setColumnFamily(ColumnFamilyDescriptorBuilder.of(TestSplitOrMergeAtTableLevel.FAMILY)).setMergeEnabled(false).build();
        Table t = TestSplitOrMergeAtTableLevel.TEST_UTIL.createTable(tableDesc, null);
        TestSplitOrMergeAtTableLevel.TEST_UTIL.waitTableAvailable(tableName);
        TestSplitOrMergeAtTableLevel.TEST_UTIL.loadTable(t, TestSplitOrMergeAtTableLevel.FAMILY, false);
        // check merge is disabled for the table
        Assert.assertFalse(TestSplitOrMergeAtTableLevel.admin.getDescriptor(tableName).isMergeEnabled());
        trySplitAndEnsureItIsSuccess(tableName);
        Threads.sleep(10000);
        tryMergeAndEnsureItFails(tableName);
        TestSplitOrMergeAtTableLevel.admin.disableTable(tableName);
        enableTableMerge(tableName);
        TestSplitOrMergeAtTableLevel.admin.enableTable(tableName);
        tryMergeAndEnsureItIsSuccess(tableName);
    }

    @Test
    public void testTableMergeSwitchForPreSplittedTable() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        TableDescriptor tableDesc = TableDescriptorBuilder.newBuilder(tableName).setColumnFamily(ColumnFamilyDescriptorBuilder.of(TestSplitOrMergeAtTableLevel.FAMILY)).setMergeEnabled(false).build();
        Table t = TestSplitOrMergeAtTableLevel.TEST_UTIL.createTable(tableDesc, new byte[][]{ Bytes.toBytes(10) });
        TestSplitOrMergeAtTableLevel.TEST_UTIL.waitTableAvailable(tableName);
        TestSplitOrMergeAtTableLevel.TEST_UTIL.loadTable(t, TestSplitOrMergeAtTableLevel.FAMILY, false);
        // check merge is disabled for the table
        Assert.assertFalse(TestSplitOrMergeAtTableLevel.admin.getDescriptor(tableName).isMergeEnabled());
        Assert.assertTrue(((TestSplitOrMergeAtTableLevel.admin.getRegions(tableName).size()) == 2));
        tryMergeAndEnsureItFails(tableName);
        enableTableMerge(tableName);
        tryMergeAndEnsureItIsSuccess(tableName);
    }
}

