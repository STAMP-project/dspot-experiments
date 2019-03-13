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
package org.apache.hadoop.hbase.security.visibility;


import HConstants.LATEST_TIMESTAMP;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.security.User;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.SecurityTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;


@Category({ SecurityTests.class, MediumTests.class })
public class TestVisibilityLabelsWithSLGStack {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestVisibilityLabelsWithSLGStack.class);

    public static final String CONFIDENTIAL = "confidential";

    private static final String SECRET = "secret";

    public static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final byte[] ROW_1 = Bytes.toBytes("row1");

    private static final byte[] CF = Bytes.toBytes("f");

    private static final byte[] Q1 = Bytes.toBytes("q1");

    private static final byte[] Q2 = Bytes.toBytes("q2");

    private static final byte[] value = Bytes.toBytes("value");

    public static Configuration conf;

    @Rule
    public final TestName TEST_NAME = new TestName();

    public static User SUPERUSER;

    @Test
    public void testWithSAGStack() throws Exception {
        TableName tableName = TableName.valueOf(TEST_NAME.getMethodName());
        try (Table table = TestVisibilityLabelsWithSLGStack.TEST_UTIL.createTable(tableName, TestVisibilityLabelsWithSLGStack.CF)) {
            Put put = new Put(TestVisibilityLabelsWithSLGStack.ROW_1);
            put.addColumn(TestVisibilityLabelsWithSLGStack.CF, TestVisibilityLabelsWithSLGStack.Q1, LATEST_TIMESTAMP, TestVisibilityLabelsWithSLGStack.value);
            put.setCellVisibility(new CellVisibility(TestVisibilityLabelsWithSLGStack.SECRET));
            table.put(put);
            put = new Put(TestVisibilityLabelsWithSLGStack.ROW_1);
            put.addColumn(TestVisibilityLabelsWithSLGStack.CF, TestVisibilityLabelsWithSLGStack.Q2, LATEST_TIMESTAMP, TestVisibilityLabelsWithSLGStack.value);
            put.setCellVisibility(new CellVisibility(TestVisibilityLabelsWithSLGStack.CONFIDENTIAL));
            table.put(put);
            LabelFilteringScanLabelGenerator.labelToFilter = TestVisibilityLabelsWithSLGStack.CONFIDENTIAL;
            Scan s = new Scan();
            s.setAuthorizations(new Authorizations(TestVisibilityLabelsWithSLGStack.SECRET, TestVisibilityLabelsWithSLGStack.CONFIDENTIAL));
            ResultScanner scanner = table.getScanner(s);
            Result next = scanner.next();
            Assert.assertNotNull(next.getColumnLatestCell(TestVisibilityLabelsWithSLGStack.CF, TestVisibilityLabelsWithSLGStack.Q1));
            Assert.assertNull(next.getColumnLatestCell(TestVisibilityLabelsWithSLGStack.CF, TestVisibilityLabelsWithSLGStack.Q2));
        }
    }
}

