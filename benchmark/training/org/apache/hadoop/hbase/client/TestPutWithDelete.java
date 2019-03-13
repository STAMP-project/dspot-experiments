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


import KeyValue.Type;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;

import static HConstants.LATEST_TIMESTAMP;


@Category({ MediumTests.class, ClientTests.class })
public class TestPutWithDelete {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestPutWithDelete.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    @Rule
    public TestName name = new TestName();

    @Test
    public void testHbasePutDeleteCell() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        final byte[] rowKey = Bytes.toBytes("12345");
        final byte[] family = Bytes.toBytes("cf");
        Table table = TestPutWithDelete.TEST_UTIL.createTable(tableName, family);
        TestPutWithDelete.TEST_UTIL.waitTableAvailable(tableName.getName(), 5000);
        try {
            // put one row
            Put put = new Put(rowKey);
            put.addColumn(family, Bytes.toBytes("A"), Bytes.toBytes("a"));
            put.addColumn(family, Bytes.toBytes("B"), Bytes.toBytes("b"));
            put.addColumn(family, Bytes.toBytes("C"), Bytes.toBytes("c"));
            put.addColumn(family, Bytes.toBytes("D"), Bytes.toBytes("d"));
            table.put(put);
            // get row back and assert the values
            Get get = new Get(rowKey);
            Result result = table.get(get);
            Assert.assertTrue("Column A value should be a", Bytes.toString(result.getValue(family, Bytes.toBytes("A"))).equals("a"));
            Assert.assertTrue("Column B value should be b", Bytes.toString(result.getValue(family, Bytes.toBytes("B"))).equals("b"));
            Assert.assertTrue("Column C value should be c", Bytes.toString(result.getValue(family, Bytes.toBytes("C"))).equals("c"));
            Assert.assertTrue("Column D value should be d", Bytes.toString(result.getValue(family, Bytes.toBytes("D"))).equals("d"));
            // put the same row again with C column deleted
            put = new Put(rowKey);
            put.addColumn(family, Bytes.toBytes("A"), Bytes.toBytes("a1"));
            put.addColumn(family, Bytes.toBytes("B"), Bytes.toBytes("b1"));
            KeyValue marker = new KeyValue(rowKey, family, Bytes.toBytes("C"), LATEST_TIMESTAMP, Type.DeleteColumn);
            put.addColumn(family, Bytes.toBytes("D"), Bytes.toBytes("d1"));
            put.add(marker);
            table.put(put);
            // get row back and assert the values
            get = new Get(rowKey);
            result = table.get(get);
            Assert.assertTrue("Column A value should be a1", Bytes.toString(result.getValue(family, Bytes.toBytes("A"))).equals("a1"));
            Assert.assertTrue("Column B value should be b1", Bytes.toString(result.getValue(family, Bytes.toBytes("B"))).equals("b1"));
            Assert.assertTrue("Column C should not exist", ((result.getValue(family, Bytes.toBytes("C"))) == null));
            Assert.assertTrue("Column D value should be d1", Bytes.toString(result.getValue(family, Bytes.toBytes("D"))).equals("d1"));
        } finally {
            table.close();
        }
    }
}

