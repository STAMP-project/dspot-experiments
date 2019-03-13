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


import Permission.Action.READ;
import com.google.protobuf.ByteString;
import java.security.PrivilegedExceptionAction;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.protobuf.generated.VisibilityLabelsProtos.GetAuthsResponse;
import org.apache.hadoop.hbase.protobuf.generated.VisibilityLabelsProtos.VisibilityLabelsResponse;
import org.apache.hadoop.hbase.security.User;
import org.apache.hadoop.hbase.security.access.SecureTestUtil;
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
public class TestVisibilityLabelsWithACL {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestVisibilityLabelsWithACL.class);

    private static final String PRIVATE = "private";

    private static final String CONFIDENTIAL = "confidential";

    private static final String SECRET = "secret";

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final byte[] row1 = Bytes.toBytes("row1");

    private static final byte[] fam = Bytes.toBytes("info");

    private static final byte[] qual = Bytes.toBytes("qual");

    private static final byte[] value = Bytes.toBytes("value");

    private static Configuration conf;

    @Rule
    public final TestName TEST_NAME = new TestName();

    private static User SUPERUSER;

    private static User NORMAL_USER1;

    private static User NORMAL_USER2;

    @Test
    public void testScanForUserWithFewerLabelAuthsThanLabelsInScanAuthorizations() throws Throwable {
        String[] auths = new String[]{ TestVisibilityLabelsWithACL.SECRET };
        String user = "user2";
        VisibilityClient.setAuths(TestVisibilityLabelsWithACL.TEST_UTIL.getConnection(), auths, user);
        TableName tableName = TableName.valueOf(TEST_NAME.getMethodName());
        final Table table = TestVisibilityLabelsWithACL.createTableAndWriteDataWithLabels(tableName, (((((TestVisibilityLabelsWithACL.SECRET) + "&") + (TestVisibilityLabelsWithACL.CONFIDENTIAL)) + "&!") + (TestVisibilityLabelsWithACL.PRIVATE)), (((TestVisibilityLabelsWithACL.SECRET) + "&!") + (TestVisibilityLabelsWithACL.PRIVATE)));
        SecureTestUtil.grantOnTable(TestVisibilityLabelsWithACL.TEST_UTIL, TestVisibilityLabelsWithACL.NORMAL_USER2.getShortName(), tableName, null, null, READ);
        PrivilegedExceptionAction<Void> scanAction = new PrivilegedExceptionAction<Void>() {
            @Override
            public Void run() throws Exception {
                Scan s = new Scan();
                s.setAuthorizations(new Authorizations(TestVisibilityLabelsWithACL.SECRET, TestVisibilityLabelsWithACL.CONFIDENTIAL));
                try (Connection connection = ConnectionFactory.createConnection(TestVisibilityLabelsWithACL.conf);Table t = connection.getTable(table.getName())) {
                    ResultScanner scanner = t.getScanner(s);
                    Result result = scanner.next();
                    Assert.assertTrue((!(result.isEmpty())));
                    Assert.assertTrue(Bytes.equals(Bytes.toBytes("row2"), result.getRow()));
                    result = scanner.next();
                    Assert.assertNull(result);
                }
                return null;
            }
        };
        TestVisibilityLabelsWithACL.NORMAL_USER2.runAs(scanAction);
    }

    @Test
    public void testScanForSuperUserWithFewerLabelAuths() throws Throwable {
        String[] auths = new String[]{ TestVisibilityLabelsWithACL.SECRET };
        String user = "admin";
        try (Connection conn = ConnectionFactory.createConnection(TestVisibilityLabelsWithACL.conf)) {
            VisibilityClient.setAuths(conn, auths, user);
        }
        TableName tableName = TableName.valueOf(TEST_NAME.getMethodName());
        final Table table = TestVisibilityLabelsWithACL.createTableAndWriteDataWithLabels(tableName, (((((TestVisibilityLabelsWithACL.SECRET) + "&") + (TestVisibilityLabelsWithACL.CONFIDENTIAL)) + "&!") + (TestVisibilityLabelsWithACL.PRIVATE)), (((TestVisibilityLabelsWithACL.SECRET) + "&!") + (TestVisibilityLabelsWithACL.PRIVATE)));
        PrivilegedExceptionAction<Void> scanAction = new PrivilegedExceptionAction<Void>() {
            @Override
            public Void run() throws Exception {
                Scan s = new Scan();
                s.setAuthorizations(new Authorizations(TestVisibilityLabelsWithACL.SECRET, TestVisibilityLabelsWithACL.CONFIDENTIAL));
                try (Connection connection = ConnectionFactory.createConnection(TestVisibilityLabelsWithACL.conf);Table t = connection.getTable(table.getName())) {
                    ResultScanner scanner = t.getScanner(s);
                    Result[] result = scanner.next(5);
                    Assert.assertTrue(((result.length) == 2));
                }
                return null;
            }
        };
        TestVisibilityLabelsWithACL.SUPERUSER.runAs(scanAction);
    }

    @Test
    public void testGetForSuperUserWithFewerLabelAuths() throws Throwable {
        String[] auths = new String[]{ TestVisibilityLabelsWithACL.SECRET };
        String user = "admin";
        VisibilityClient.setAuths(TestVisibilityLabelsWithACL.TEST_UTIL.getConnection(), auths, user);
        TableName tableName = TableName.valueOf(TEST_NAME.getMethodName());
        final Table table = TestVisibilityLabelsWithACL.createTableAndWriteDataWithLabels(tableName, (((((TestVisibilityLabelsWithACL.SECRET) + "&") + (TestVisibilityLabelsWithACL.CONFIDENTIAL)) + "&!") + (TestVisibilityLabelsWithACL.PRIVATE)), (((TestVisibilityLabelsWithACL.SECRET) + "&!") + (TestVisibilityLabelsWithACL.PRIVATE)));
        PrivilegedExceptionAction<Void> scanAction = new PrivilegedExceptionAction<Void>() {
            @Override
            public Void run() throws Exception {
                Get g = new Get(TestVisibilityLabelsWithACL.row1);
                g.setAuthorizations(new Authorizations(TestVisibilityLabelsWithACL.SECRET, TestVisibilityLabelsWithACL.CONFIDENTIAL));
                try (Connection connection = ConnectionFactory.createConnection(TestVisibilityLabelsWithACL.conf);Table t = connection.getTable(table.getName())) {
                    Result result = t.get(g);
                    Assert.assertTrue((!(result.isEmpty())));
                }
                return null;
            }
        };
        TestVisibilityLabelsWithACL.SUPERUSER.runAs(scanAction);
    }

    @Test
    public void testVisibilityLabelsForUserWithNoAuths() throws Throwable {
        String user = "admin";
        String[] auths = new String[]{ TestVisibilityLabelsWithACL.SECRET };
        try (Connection conn = ConnectionFactory.createConnection(TestVisibilityLabelsWithACL.conf)) {
            VisibilityClient.clearAuths(conn, auths, user);// Removing all auths if any.

            VisibilityClient.setAuths(conn, auths, "user1");
        }
        TableName tableName = TableName.valueOf(TEST_NAME.getMethodName());
        final Table table = TestVisibilityLabelsWithACL.createTableAndWriteDataWithLabels(tableName, TestVisibilityLabelsWithACL.SECRET);
        SecureTestUtil.grantOnTable(TestVisibilityLabelsWithACL.TEST_UTIL, TestVisibilityLabelsWithACL.NORMAL_USER1.getShortName(), tableName, null, null, READ);
        SecureTestUtil.grantOnTable(TestVisibilityLabelsWithACL.TEST_UTIL, TestVisibilityLabelsWithACL.NORMAL_USER2.getShortName(), tableName, null, null, READ);
        PrivilegedExceptionAction<Void> getAction = new PrivilegedExceptionAction<Void>() {
            @Override
            public Void run() throws Exception {
                Get g = new Get(TestVisibilityLabelsWithACL.row1);
                g.setAuthorizations(new Authorizations(TestVisibilityLabelsWithACL.SECRET, TestVisibilityLabelsWithACL.CONFIDENTIAL));
                try (Connection connection = ConnectionFactory.createConnection(TestVisibilityLabelsWithACL.conf);Table t = connection.getTable(table.getName())) {
                    Result result = t.get(g);
                    Assert.assertTrue(result.isEmpty());
                }
                return null;
            }
        };
        TestVisibilityLabelsWithACL.NORMAL_USER2.runAs(getAction);
    }

    @Test
    public void testLabelsTableOpsWithDifferentUsers() throws Throwable {
        PrivilegedExceptionAction<VisibilityLabelsResponse> action = new PrivilegedExceptionAction<VisibilityLabelsResponse>() {
            @Override
            public VisibilityLabelsResponse run() throws Exception {
                try (Connection conn = ConnectionFactory.createConnection(TestVisibilityLabelsWithACL.conf)) {
                    return VisibilityClient.addLabels(conn, new String[]{ "l1", "l2" });
                } catch (Throwable e) {
                }
                return null;
            }
        };
        VisibilityLabelsResponse response = TestVisibilityLabelsWithACL.NORMAL_USER1.runAs(action);
        Assert.assertEquals("org.apache.hadoop.hbase.security.AccessDeniedException", response.getResult(0).getException().getName());
        Assert.assertEquals("org.apache.hadoop.hbase.security.AccessDeniedException", response.getResult(1).getException().getName());
        action = new PrivilegedExceptionAction<VisibilityLabelsResponse>() {
            @Override
            public VisibilityLabelsResponse run() throws Exception {
                try (Connection conn = ConnectionFactory.createConnection(TestVisibilityLabelsWithACL.conf)) {
                    return VisibilityClient.setAuths(conn, new String[]{ TestVisibilityLabelsWithACL.CONFIDENTIAL, TestVisibilityLabelsWithACL.PRIVATE }, "user1");
                } catch (Throwable e) {
                }
                return null;
            }
        };
        response = TestVisibilityLabelsWithACL.NORMAL_USER1.runAs(action);
        Assert.assertEquals("org.apache.hadoop.hbase.security.AccessDeniedException", response.getResult(0).getException().getName());
        Assert.assertEquals("org.apache.hadoop.hbase.security.AccessDeniedException", response.getResult(1).getException().getName());
        action = new PrivilegedExceptionAction<VisibilityLabelsResponse>() {
            @Override
            public VisibilityLabelsResponse run() throws Exception {
                try (Connection conn = ConnectionFactory.createConnection(TestVisibilityLabelsWithACL.conf)) {
                    return VisibilityClient.setAuths(conn, new String[]{ TestVisibilityLabelsWithACL.CONFIDENTIAL, TestVisibilityLabelsWithACL.PRIVATE }, "user1");
                } catch (Throwable e) {
                }
                return null;
            }
        };
        response = TestVisibilityLabelsWithACL.SUPERUSER.runAs(action);
        Assert.assertTrue(response.getResult(0).getException().getValue().isEmpty());
        Assert.assertTrue(response.getResult(1).getException().getValue().isEmpty());
        action = new PrivilegedExceptionAction<VisibilityLabelsResponse>() {
            @Override
            public VisibilityLabelsResponse run() throws Exception {
                try (Connection conn = ConnectionFactory.createConnection(TestVisibilityLabelsWithACL.conf)) {
                    return VisibilityClient.clearAuths(conn, new String[]{ TestVisibilityLabelsWithACL.CONFIDENTIAL, TestVisibilityLabelsWithACL.PRIVATE }, "user1");
                } catch (Throwable e) {
                }
                return null;
            }
        };
        response = TestVisibilityLabelsWithACL.NORMAL_USER1.runAs(action);
        Assert.assertEquals("org.apache.hadoop.hbase.security.AccessDeniedException", response.getResult(0).getException().getName());
        Assert.assertEquals("org.apache.hadoop.hbase.security.AccessDeniedException", response.getResult(1).getException().getName());
        response = VisibilityClient.clearAuths(TestVisibilityLabelsWithACL.TEST_UTIL.getConnection(), new String[]{ TestVisibilityLabelsWithACL.CONFIDENTIAL, TestVisibilityLabelsWithACL.PRIVATE }, "user1");
        Assert.assertTrue(response.getResult(0).getException().getValue().isEmpty());
        Assert.assertTrue(response.getResult(1).getException().getValue().isEmpty());
        VisibilityClient.setAuths(TestVisibilityLabelsWithACL.TEST_UTIL.getConnection(), new String[]{ TestVisibilityLabelsWithACL.CONFIDENTIAL, TestVisibilityLabelsWithACL.PRIVATE }, "user3");
        PrivilegedExceptionAction<GetAuthsResponse> action1 = new PrivilegedExceptionAction<GetAuthsResponse>() {
            @Override
            public GetAuthsResponse run() throws Exception {
                try (Connection conn = ConnectionFactory.createConnection(TestVisibilityLabelsWithACL.conf)) {
                    return VisibilityClient.getAuths(conn, "user3");
                } catch (Throwable e) {
                }
                return null;
            }
        };
        GetAuthsResponse authsResponse = TestVisibilityLabelsWithACL.NORMAL_USER1.runAs(action1);
        Assert.assertNull(authsResponse);
        authsResponse = TestVisibilityLabelsWithACL.SUPERUSER.runAs(action1);
        List<String> authsList = new java.util.ArrayList(authsResponse.getAuthList().size());
        for (ByteString authBS : authsResponse.getAuthList()) {
            authsList.add(Bytes.toString(authBS.toByteArray()));
        }
        Assert.assertEquals(2, authsList.size());
        Assert.assertTrue(authsList.contains(TestVisibilityLabelsWithACL.CONFIDENTIAL));
        Assert.assertTrue(authsList.contains(TestVisibilityLabelsWithACL.PRIVATE));
    }
}

