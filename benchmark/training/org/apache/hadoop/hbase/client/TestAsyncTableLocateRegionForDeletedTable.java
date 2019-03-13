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


import java.io.IOException;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Fix an infinite loop in {@link AsyncNonMetaRegionLocator}, see the comments on HBASE-21943 for
 * more details.
 */
@Category({ MediumTests.class, ClientTests.class })
public class TestAsyncTableLocateRegionForDeletedTable {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestAsyncTableLocateRegionForDeletedTable.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static TableName TABLE_NAME = TableName.valueOf("async");

    private static byte[] FAMILY = Bytes.toBytes("cf");

    private static byte[] QUALIFIER = Bytes.toBytes("cq");

    private static byte[] VALUE = Bytes.toBytes("value");

    private static AsyncConnection ASYNC_CONN;

    @Test
    public void test() throws IOException, InterruptedException {
        try (Table table = TestAsyncTableLocateRegionForDeletedTable.TEST_UTIL.getConnection().getTable(TestAsyncTableLocateRegionForDeletedTable.TABLE_NAME)) {
            for (int i = 0; i < 100; i++) {
                table.put(addColumn(TestAsyncTableLocateRegionForDeletedTable.FAMILY, TestAsyncTableLocateRegionForDeletedTable.QUALIFIER, TestAsyncTableLocateRegionForDeletedTable.VALUE));
            }
        }
        TestAsyncTableLocateRegionForDeletedTable.TEST_UTIL.getAdmin().split(TestAsyncTableLocateRegionForDeletedTable.TABLE_NAME, Bytes.toBytes(50));
        waitFor(60000, () -> (TEST_UTIL.getMiniHBaseCluster().getRegions(TestAsyncTableLocateRegionForDeletedTable.TABLE_NAME).size()) == 2);
        // make sure we can access the split regions
        try (Table table = TestAsyncTableLocateRegionForDeletedTable.TEST_UTIL.getConnection().getTable(TestAsyncTableLocateRegionForDeletedTable.TABLE_NAME)) {
            for (int i = 0; i < 100; i++) {
                Assert.assertFalse(table.get(new Get(Bytes.toBytes(i))).isEmpty());
            }
        }
        // let's cache the two old locations
        AsyncTableRegionLocator locator = TestAsyncTableLocateRegionForDeletedTable.ASYNC_CONN.getRegionLocator(TestAsyncTableLocateRegionForDeletedTable.TABLE_NAME);
        locator.getRegionLocation(Bytes.toBytes(0)).join();
        locator.getRegionLocation(Bytes.toBytes(99)).join();
        // recreate the table
        TestAsyncTableLocateRegionForDeletedTable.TEST_UTIL.getAdmin().disableTable(TestAsyncTableLocateRegionForDeletedTable.TABLE_NAME);
        TestAsyncTableLocateRegionForDeletedTable.TEST_UTIL.getAdmin().deleteTable(TestAsyncTableLocateRegionForDeletedTable.TABLE_NAME);
        TestAsyncTableLocateRegionForDeletedTable.TEST_UTIL.createTable(TestAsyncTableLocateRegionForDeletedTable.TABLE_NAME, TestAsyncTableLocateRegionForDeletedTable.FAMILY);
        TestAsyncTableLocateRegionForDeletedTable.TEST_UTIL.waitTableAvailable(TestAsyncTableLocateRegionForDeletedTable.TABLE_NAME);
        // confirm that we can still get the correct location
        Assert.assertFalse(TestAsyncTableLocateRegionForDeletedTable.ASYNC_CONN.getTable(TestAsyncTableLocateRegionForDeletedTable.TABLE_NAME).exists(new Get(Bytes.toBytes(99))).join());
    }
}

