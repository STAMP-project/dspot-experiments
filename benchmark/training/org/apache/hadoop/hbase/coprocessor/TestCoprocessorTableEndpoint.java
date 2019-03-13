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
package org.apache.hadoop.hbase.coprocessor;


import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.testclassification.CoprocessorTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;


@Category({ CoprocessorTests.class, MediumTests.class })
public class TestCoprocessorTableEndpoint {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestCoprocessorTableEndpoint.class);

    private static final byte[] TEST_FAMILY = Bytes.toBytes("TestFamily");

    private static final byte[] TEST_QUALIFIER = Bytes.toBytes("TestQualifier");

    private static final byte[] ROW = Bytes.toBytes("testRow");

    private static final int ROWSIZE = 20;

    private static final int rowSeperator1 = 5;

    private static final int rowSeperator2 = 12;

    private static final byte[][] ROWS = TestCoprocessorTableEndpoint.makeN(TestCoprocessorTableEndpoint.ROW, TestCoprocessorTableEndpoint.ROWSIZE);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    @Rule
    public TestName name = new TestName();

    @Test
    public void testCoprocessorTableEndpoint() throws Throwable {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        HTableDescriptor desc = new HTableDescriptor(tableName);
        desc.addFamily(new HColumnDescriptor(TestCoprocessorTableEndpoint.TEST_FAMILY));
        desc.addCoprocessor(ColumnAggregationEndpoint.class.getName());
        TestCoprocessorTableEndpoint.createTable(desc);
        TestCoprocessorTableEndpoint.verifyTable(tableName);
    }

    @Test
    public void testDynamicCoprocessorTableEndpoint() throws Throwable {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        HTableDescriptor desc = new HTableDescriptor(tableName);
        desc.addFamily(new HColumnDescriptor(TestCoprocessorTableEndpoint.TEST_FAMILY));
        TestCoprocessorTableEndpoint.createTable(desc);
        desc.addCoprocessor(ColumnAggregationEndpoint.class.getName());
        TestCoprocessorTableEndpoint.updateTable(desc);
        TestCoprocessorTableEndpoint.verifyTable(tableName);
    }
}

