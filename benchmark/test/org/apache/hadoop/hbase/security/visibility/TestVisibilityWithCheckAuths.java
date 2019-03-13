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
import java.io.IOException;
import java.security.PrivilegedExceptionAction;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Append;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.protobuf.generated.VisibilityLabelsProtos.VisibilityLabelsResponse;
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


/**
 * Test visibility by setting 'hbase.security.visibility.mutations.checkauths' to true
 */
@Category({ SecurityTests.class, MediumTests.class })
public class TestVisibilityWithCheckAuths {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestVisibilityWithCheckAuths.class);

    private static final String TOPSECRET = "TOPSECRET";

    private static final String PUBLIC = "PUBLIC";

    public static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final byte[] row1 = Bytes.toBytes("row1");

    private static final byte[] fam = Bytes.toBytes("info");

    private static final byte[] qual = Bytes.toBytes("qual");

    private static final byte[] value = Bytes.toBytes("value");

    public static Configuration conf;

    @Rule
    public final TestName TEST_NAME = new TestName();

    public static User SUPERUSER;

    public static User USER;

    @Test
    public void testVerifyAccessDeniedForInvalidUserAuths() throws Exception {
        PrivilegedExceptionAction<VisibilityLabelsResponse> action = new PrivilegedExceptionAction<VisibilityLabelsResponse>() {
            @Override
            public VisibilityLabelsResponse run() throws Exception {
                try (Connection conn = ConnectionFactory.createConnection(TestVisibilityWithCheckAuths.conf)) {
                    return VisibilityClient.setAuths(conn, new String[]{ TestVisibilityWithCheckAuths.TOPSECRET }, TestVisibilityWithCheckAuths.USER.getShortName());
                } catch (Throwable e) {
                }
                return null;
            }
        };
        TestVisibilityWithCheckAuths.SUPERUSER.runAs(action);
        final TableName tableName = TableName.valueOf(TEST_NAME.getMethodName());
        Admin hBaseAdmin = TestVisibilityWithCheckAuths.TEST_UTIL.getAdmin();
        HColumnDescriptor colDesc = new HColumnDescriptor(TestVisibilityWithCheckAuths.fam);
        colDesc.setMaxVersions(5);
        HTableDescriptor desc = new HTableDescriptor(tableName);
        desc.addFamily(colDesc);
        hBaseAdmin.createTable(desc);
        try {
            TestVisibilityWithCheckAuths.TEST_UTIL.getAdmin().flush(tableName);
            PrivilegedExceptionAction<Void> actiona = new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    try (Connection connection = ConnectionFactory.createConnection(TestVisibilityWithCheckAuths.conf);Table table = connection.getTable(tableName)) {
                        Put p = new Put(TestVisibilityWithCheckAuths.row1);
                        p.setCellVisibility(new CellVisibility((((TestVisibilityWithCheckAuths.PUBLIC) + "&") + (TestVisibilityWithCheckAuths.TOPSECRET))));
                        p.addColumn(TestVisibilityWithCheckAuths.fam, TestVisibilityWithCheckAuths.qual, 125L, TestVisibilityWithCheckAuths.value);
                        table.put(p);
                        Assert.fail("Testcase should fail with AccesDeniedException");
                    } catch (Throwable t) {
                        Assert.assertTrue(t.getMessage().contains("AccessDeniedException"));
                    }
                    return null;
                }
            };
            TestVisibilityWithCheckAuths.USER.runAs(actiona);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Test
    public void testLabelsWithAppend() throws Throwable {
        PrivilegedExceptionAction<VisibilityLabelsResponse> action = new PrivilegedExceptionAction<VisibilityLabelsResponse>() {
            @Override
            public VisibilityLabelsResponse run() throws Exception {
                try (Connection conn = ConnectionFactory.createConnection(TestVisibilityWithCheckAuths.conf)) {
                    return VisibilityClient.setAuths(conn, new String[]{ TestVisibilityWithCheckAuths.TOPSECRET }, TestVisibilityWithCheckAuths.USER.getShortName());
                } catch (Throwable e) {
                }
                return null;
            }
        };
        TestVisibilityWithCheckAuths.SUPERUSER.runAs(action);
        final TableName tableName = TableName.valueOf(TEST_NAME.getMethodName());
        try (Table table = TestVisibilityWithCheckAuths.TEST_UTIL.createTable(tableName, TestVisibilityWithCheckAuths.fam)) {
            final byte[] row1 = Bytes.toBytes("row1");
            final byte[] val = Bytes.toBytes("a");
            PrivilegedExceptionAction<Void> actiona = new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    try (Connection connection = ConnectionFactory.createConnection(TestVisibilityWithCheckAuths.conf);Table table = connection.getTable(tableName)) {
                        Put put = new Put(row1);
                        put.addColumn(TestVisibilityWithCheckAuths.fam, TestVisibilityWithCheckAuths.qual, LATEST_TIMESTAMP, val);
                        put.setCellVisibility(new CellVisibility(TestVisibilityWithCheckAuths.TOPSECRET));
                        table.put(put);
                    }
                    return null;
                }
            };
            TestVisibilityWithCheckAuths.USER.runAs(actiona);
            actiona = new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    try (Connection connection = ConnectionFactory.createConnection(TestVisibilityWithCheckAuths.conf);Table table = connection.getTable(tableName)) {
                        Append append = new Append(row1);
                        append.addColumn(TestVisibilityWithCheckAuths.fam, TestVisibilityWithCheckAuths.qual, Bytes.toBytes("b"));
                        table.append(append);
                    }
                    return null;
                }
            };
            TestVisibilityWithCheckAuths.USER.runAs(actiona);
            actiona = new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    try (Connection connection = ConnectionFactory.createConnection(TestVisibilityWithCheckAuths.conf);Table table = connection.getTable(tableName)) {
                        Append append = new Append(row1);
                        append.addColumn(TestVisibilityWithCheckAuths.fam, TestVisibilityWithCheckAuths.qual, Bytes.toBytes("c"));
                        append.setCellVisibility(new CellVisibility(TestVisibilityWithCheckAuths.PUBLIC));
                        table.append(append);
                        Assert.fail("Testcase should fail with AccesDeniedException");
                    } catch (Throwable t) {
                        Assert.assertTrue(t.getMessage().contains("AccessDeniedException"));
                    }
                    return null;
                }
            };
            TestVisibilityWithCheckAuths.USER.runAs(actiona);
        }
    }
}

