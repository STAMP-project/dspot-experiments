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


import MasterSwitchType.MERGE;
import MasterSwitchType.SPLIT;
import java.io.IOException;
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
public class TestSplitOrMergeStatus {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestSplitOrMergeStatus.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static byte[] FAMILY = Bytes.toBytes("testFamily");

    @Rule
    public TestName name = new TestName();

    @Test
    public void testSplitSwitch() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        Table t = TestSplitOrMergeStatus.TEST_UTIL.createTable(tableName, TestSplitOrMergeStatus.FAMILY);
        TestSplitOrMergeStatus.TEST_UTIL.loadTable(t, TestSplitOrMergeStatus.FAMILY, false);
        RegionLocator locator = TestSplitOrMergeStatus.TEST_UTIL.getConnection().getRegionLocator(t.getName());
        int originalCount = locator.getAllRegionLocations().size();
        Admin admin = TestSplitOrMergeStatus.TEST_UTIL.getAdmin();
        initSwitchStatus(admin);
        boolean[] results = admin.setSplitOrMergeEnabled(false, false, SPLIT);
        Assert.assertEquals(1, results.length);
        Assert.assertTrue(results[0]);
        admin.split(t.getName());
        int count = admin.getTableRegions(tableName).size();
        Assert.assertTrue((originalCount == count));
        results = admin.setSplitOrMergeEnabled(true, false, SPLIT);
        Assert.assertEquals(1, results.length);
        Assert.assertFalse(results[0]);
        admin.split(t.getName());
        while ((count = admin.getTableRegions(tableName).size()) == originalCount) {
            Threads.sleep(1);
        } 
        count = admin.getTableRegions(tableName).size();
        Assert.assertTrue((originalCount < count));
        admin.close();
    }

    @Test
    public void testMultiSwitches() throws IOException {
        Admin admin = TestSplitOrMergeStatus.TEST_UTIL.getAdmin();
        boolean[] switches = admin.setSplitOrMergeEnabled(false, false, SPLIT, MERGE);
        for (boolean s : switches) {
            Assert.assertTrue(s);
        }
        Assert.assertFalse(admin.isSplitOrMergeEnabled(SPLIT));
        Assert.assertFalse(admin.isSplitOrMergeEnabled(MERGE));
        admin.close();
    }
}

