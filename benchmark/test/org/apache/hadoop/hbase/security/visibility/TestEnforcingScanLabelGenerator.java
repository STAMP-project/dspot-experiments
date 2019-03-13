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
import java.security.PrivilegedExceptionAction;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
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
public class TestEnforcingScanLabelGenerator {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestEnforcingScanLabelGenerator.class);

    public static final String CONFIDENTIAL = "confidential";

    private static final String SECRET = "secret";

    public static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final byte[] ROW_1 = Bytes.toBytes("row1");

    private static final byte[] CF = Bytes.toBytes("f");

    private static final byte[] Q1 = Bytes.toBytes("q1");

    private static final byte[] Q2 = Bytes.toBytes("q2");

    private static final byte[] Q3 = Bytes.toBytes("q3");

    private static final byte[] value = Bytes.toBytes("value");

    public static Configuration conf;

    @Rule
    public final TestName TEST_NAME = new TestName();

    public static User SUPERUSER;

    public static User TESTUSER;

    @Test
    public void testEnforcingScanLabelGenerator() throws Exception {
        final TableName tableName = TableName.valueOf(TEST_NAME.getMethodName());
        TestEnforcingScanLabelGenerator.SUPERUSER.runAs(new PrivilegedExceptionAction<Void>() {
            @Override
            public Void run() throws Exception {
                try (Connection connection = ConnectionFactory.createConnection(TestEnforcingScanLabelGenerator.conf);Table table = TestEnforcingScanLabelGenerator.TEST_UTIL.createTable(tableName, TestEnforcingScanLabelGenerator.CF)) {
                    Put put = new Put(TestEnforcingScanLabelGenerator.ROW_1);
                    put.addColumn(TestEnforcingScanLabelGenerator.CF, TestEnforcingScanLabelGenerator.Q1, LATEST_TIMESTAMP, TestEnforcingScanLabelGenerator.value);
                    put.setCellVisibility(new CellVisibility(TestEnforcingScanLabelGenerator.SECRET));
                    table.put(put);
                    put = new Put(TestEnforcingScanLabelGenerator.ROW_1);
                    put.addColumn(TestEnforcingScanLabelGenerator.CF, TestEnforcingScanLabelGenerator.Q2, LATEST_TIMESTAMP, TestEnforcingScanLabelGenerator.value);
                    put.setCellVisibility(new CellVisibility(TestEnforcingScanLabelGenerator.CONFIDENTIAL));
                    table.put(put);
                    put = new Put(TestEnforcingScanLabelGenerator.ROW_1);
                    put.addColumn(TestEnforcingScanLabelGenerator.CF, TestEnforcingScanLabelGenerator.Q3, LATEST_TIMESTAMP, TestEnforcingScanLabelGenerator.value);
                    table.put(put);
                    return null;
                }
            }
        });
        // Test that super user can see all the cells.
        TestEnforcingScanLabelGenerator.SUPERUSER.runAs(new PrivilegedExceptionAction<Void>() {
            @Override
            public Void run() throws Exception {
                try (Connection connection = ConnectionFactory.createConnection(TestEnforcingScanLabelGenerator.conf);Table table = connection.getTable(tableName)) {
                    // Test that super user can see all the cells.
                    Get get = new Get(TestEnforcingScanLabelGenerator.ROW_1);
                    Result result = table.get(get);
                    Assert.assertTrue("Missing authorization", result.containsColumn(TestEnforcingScanLabelGenerator.CF, TestEnforcingScanLabelGenerator.Q1));
                    Assert.assertTrue("Missing authorization", result.containsColumn(TestEnforcingScanLabelGenerator.CF, TestEnforcingScanLabelGenerator.Q2));
                    Assert.assertTrue("Missing authorization", result.containsColumn(TestEnforcingScanLabelGenerator.CF, TestEnforcingScanLabelGenerator.Q3));
                    return null;
                }
            }
        });
        TestEnforcingScanLabelGenerator.TESTUSER.runAs(new PrivilegedExceptionAction<Void>() {
            @Override
            public Void run() throws Exception {
                try (Connection connection = ConnectionFactory.createConnection(TestEnforcingScanLabelGenerator.conf);Table table = connection.getTable(tableName)) {
                    // Test that we enforce the defined set
                    Get get = new Get(TestEnforcingScanLabelGenerator.ROW_1);
                    get.setAuthorizations(new Authorizations(new String[]{ TestEnforcingScanLabelGenerator.SECRET, TestEnforcingScanLabelGenerator.CONFIDENTIAL }));
                    Result result = table.get(get);
                    Assert.assertFalse("Inappropriate authorization", result.containsColumn(TestEnforcingScanLabelGenerator.CF, TestEnforcingScanLabelGenerator.Q1));
                    Assert.assertTrue("Missing authorization", result.containsColumn(TestEnforcingScanLabelGenerator.CF, TestEnforcingScanLabelGenerator.Q2));
                    Assert.assertTrue("Inappropriate filtering", result.containsColumn(TestEnforcingScanLabelGenerator.CF, TestEnforcingScanLabelGenerator.Q3));
                    // Test that we also enforce the defined set for the user if no auths are provided
                    get = new Get(TestEnforcingScanLabelGenerator.ROW_1);
                    result = table.get(get);
                    Assert.assertFalse("Inappropriate authorization", result.containsColumn(TestEnforcingScanLabelGenerator.CF, TestEnforcingScanLabelGenerator.Q1));
                    Assert.assertTrue("Missing authorization", result.containsColumn(TestEnforcingScanLabelGenerator.CF, TestEnforcingScanLabelGenerator.Q2));
                    Assert.assertTrue("Inappropriate filtering", result.containsColumn(TestEnforcingScanLabelGenerator.CF, TestEnforcingScanLabelGenerator.Q3));
                    return null;
                }
            }
        });
    }
}

