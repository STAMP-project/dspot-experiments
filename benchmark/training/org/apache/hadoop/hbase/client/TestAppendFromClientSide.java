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


import KeyValue.Type.Put;
import java.io.IOException;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;


/**
 * Run Append tests that use the HBase clients;
 */
@Category(LargeTests.class)
public class TestAppendFromClientSide {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestAppendFromClientSide.class);

    protected static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static byte[] ROW = Bytes.toBytes("testRow");

    private static byte[] FAMILY = Bytes.toBytes("testFamily");

    private static byte[] QUALIFIER = Bytes.toBytes("testQualifier");

    @Rule
    public TestName name = new TestName();

    @Test
    public void testAppendWithCustomTimestamp() throws IOException {
        TableName TABLENAME = TableName.valueOf(name.getMethodName());
        Table table = TestAppendFromClientSide.TEST_UTIL.createTable(TABLENAME, TestAppendFromClientSide.FAMILY);
        long timestamp = 999;
        Append append = new Append(TestAppendFromClientSide.ROW);
        append.add(CellUtil.createCell(TestAppendFromClientSide.ROW, TestAppendFromClientSide.FAMILY, TestAppendFromClientSide.QUALIFIER, timestamp, Put.getCode(), Bytes.toBytes(100L)));
        Result r = table.append(append);
        Assert.assertEquals(1, r.size());
        Assert.assertEquals(timestamp, r.rawCells()[0].getTimestamp());
        r = table.get(new Get(TestAppendFromClientSide.ROW));
        Assert.assertEquals(1, r.size());
        Assert.assertEquals(timestamp, r.rawCells()[0].getTimestamp());
        r = table.append(append);
        Assert.assertEquals(1, r.size());
        Assert.assertNotEquals(timestamp, r.rawCells()[0].getTimestamp());
        r = table.get(new Get(TestAppendFromClientSide.ROW));
        Assert.assertEquals(1, r.size());
        Assert.assertNotEquals(timestamp, r.rawCells()[0].getTimestamp());
    }
}

