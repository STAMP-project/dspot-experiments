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
package org.apache.hadoop.hbase.mapreduce;


import Counter.DIFFERENTCELLVALUES;
import Counter.ROWSWITHDIFFS;
import Counter.SOURCEMISSINGCELLS;
import Counter.SOURCEMISSINGROWS;
import Counter.TARGETMISSINGCELLS;
import Counter.TARGETMISSINGROWS;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.mapreduce.Counters;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Basic test for the SyncTable M/R tool
 */
@Category(LargeTests.class)
public class TestSyncTable {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestSyncTable.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestSyncTable.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    @Rule
    public TestName name = new TestName();

    @Test
    public void testSyncTable() throws Exception {
        final TableName sourceTableName = TableName.valueOf(((name.getMethodName()) + "_source"));
        final TableName targetTableName = TableName.valueOf(((name.getMethodName()) + "_target"));
        Path testDir = TestSyncTable.TEST_UTIL.getDataTestDirOnTestFS("testSyncTable");
        writeTestData(sourceTableName, targetTableName);
        hashSourceTable(sourceTableName, testDir);
        Counters syncCounters = syncTables(sourceTableName, targetTableName, testDir);
        assertEqualTables(90, sourceTableName, targetTableName);
        Assert.assertEquals(60, syncCounters.findCounter(ROWSWITHDIFFS).getValue());
        Assert.assertEquals(10, syncCounters.findCounter(SOURCEMISSINGROWS).getValue());
        Assert.assertEquals(10, syncCounters.findCounter(TARGETMISSINGROWS).getValue());
        Assert.assertEquals(50, syncCounters.findCounter(SOURCEMISSINGCELLS).getValue());
        Assert.assertEquals(50, syncCounters.findCounter(TARGETMISSINGCELLS).getValue());
        Assert.assertEquals(20, syncCounters.findCounter(DIFFERENTCELLVALUES).getValue());
        TestSyncTable.TEST_UTIL.deleteTable(sourceTableName);
        TestSyncTable.TEST_UTIL.deleteTable(targetTableName);
    }

    @Test
    public void testSyncTableDoDeletesFalse() throws Exception {
        final TableName sourceTableName = TableName.valueOf(((name.getMethodName()) + "_source"));
        final TableName targetTableName = TableName.valueOf(((name.getMethodName()) + "_target"));
        Path testDir = TestSyncTable.TEST_UTIL.getDataTestDirOnTestFS("testSyncTableDoDeletesFalse");
        writeTestData(sourceTableName, targetTableName);
        hashSourceTable(sourceTableName, testDir);
        Counters syncCounters = syncTables(sourceTableName, targetTableName, testDir, "--doDeletes=false");
        assertTargetDoDeletesFalse(100, sourceTableName, targetTableName);
        Assert.assertEquals(60, syncCounters.findCounter(ROWSWITHDIFFS).getValue());
        Assert.assertEquals(10, syncCounters.findCounter(SOURCEMISSINGROWS).getValue());
        Assert.assertEquals(10, syncCounters.findCounter(TARGETMISSINGROWS).getValue());
        Assert.assertEquals(50, syncCounters.findCounter(SOURCEMISSINGCELLS).getValue());
        Assert.assertEquals(50, syncCounters.findCounter(TARGETMISSINGCELLS).getValue());
        Assert.assertEquals(20, syncCounters.findCounter(DIFFERENTCELLVALUES).getValue());
        TestSyncTable.TEST_UTIL.deleteTable(sourceTableName);
        TestSyncTable.TEST_UTIL.deleteTable(targetTableName);
    }

    @Test
    public void testSyncTableDoPutsFalse() throws Exception {
        final TableName sourceTableName = TableName.valueOf(((name.getMethodName()) + "_source"));
        final TableName targetTableName = TableName.valueOf(((name.getMethodName()) + "_target"));
        Path testDir = TestSyncTable.TEST_UTIL.getDataTestDirOnTestFS("testSyncTableDoPutsFalse");
        writeTestData(sourceTableName, targetTableName);
        hashSourceTable(sourceTableName, testDir);
        Counters syncCounters = syncTables(sourceTableName, targetTableName, testDir, "--doPuts=false");
        assertTargetDoPutsFalse(70, sourceTableName, targetTableName);
        Assert.assertEquals(60, syncCounters.findCounter(ROWSWITHDIFFS).getValue());
        Assert.assertEquals(10, syncCounters.findCounter(SOURCEMISSINGROWS).getValue());
        Assert.assertEquals(10, syncCounters.findCounter(TARGETMISSINGROWS).getValue());
        Assert.assertEquals(50, syncCounters.findCounter(SOURCEMISSINGCELLS).getValue());
        Assert.assertEquals(50, syncCounters.findCounter(TARGETMISSINGCELLS).getValue());
        Assert.assertEquals(20, syncCounters.findCounter(DIFFERENTCELLVALUES).getValue());
        TestSyncTable.TEST_UTIL.deleteTable(sourceTableName);
        TestSyncTable.TEST_UTIL.deleteTable(targetTableName);
    }
}

