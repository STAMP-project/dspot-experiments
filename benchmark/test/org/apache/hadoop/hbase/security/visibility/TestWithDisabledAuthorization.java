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


import com.google.protobuf.ByteString;
import java.security.PrivilegedExceptionAction;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.protobuf.generated.VisibilityLabelsProtos.GetAuthsResponse;
import org.apache.hadoop.hbase.security.User;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.SecurityTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;


@Category({ SecurityTests.class, LargeTests.class })
public class TestWithDisabledAuthorization {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestWithDisabledAuthorization.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final String CONFIDENTIAL = "confidential";

    private static final String SECRET = "secret";

    private static final String PRIVATE = "private";

    private static final byte[] TEST_FAMILY = Bytes.toBytes("test");

    private static final byte[] TEST_QUALIFIER = Bytes.toBytes("q");

    private static final byte[] ZERO = Bytes.toBytes(0L);

    @Rule
    public final TestName TEST_NAME = new TestName();

    private static User SUPERUSER;

    private static User USER_RW;

    private static Configuration conf;

    @Test
    public void testManageUserAuths() throws Throwable {
        // Even though authorization is disabled, we should be able to manage user auths
        TestWithDisabledAuthorization.SUPERUSER.runAs(new PrivilegedExceptionAction<Void>() {
            @Override
            public Void run() throws Exception {
                try (Connection conn = ConnectionFactory.createConnection(TestWithDisabledAuthorization.conf)) {
                    VisibilityClient.setAuths(conn, new String[]{ TestWithDisabledAuthorization.SECRET, TestWithDisabledAuthorization.CONFIDENTIAL }, TestWithDisabledAuthorization.USER_RW.getShortName());
                } catch (Throwable t) {
                    Assert.fail("Should not have failed");
                }
                return null;
            }
        });
        PrivilegedExceptionAction<List<String>> getAuths = new PrivilegedExceptionAction<List<String>>() {
            @Override
            public List<String> run() throws Exception {
                GetAuthsResponse authsResponse = null;
                try (Connection conn = ConnectionFactory.createConnection(TestWithDisabledAuthorization.conf)) {
                    authsResponse = VisibilityClient.getAuths(conn, TestWithDisabledAuthorization.USER_RW.getShortName());
                } catch (Throwable t) {
                    Assert.fail("Should not have failed");
                }
                List<String> authsList = new java.util.ArrayList(authsResponse.getAuthList().size());
                for (ByteString authBS : authsResponse.getAuthList()) {
                    authsList.add(Bytes.toString(authBS.toByteArray()));
                }
                return authsList;
            }
        };
        List<String> authsList = TestWithDisabledAuthorization.SUPERUSER.runAs(getAuths);
        Assert.assertEquals(2, authsList.size());
        Assert.assertTrue(authsList.contains(TestWithDisabledAuthorization.SECRET));
        Assert.assertTrue(authsList.contains(TestWithDisabledAuthorization.CONFIDENTIAL));
        TestWithDisabledAuthorization.SUPERUSER.runAs(new PrivilegedExceptionAction<Void>() {
            @Override
            public Void run() throws Exception {
                try (Connection conn = ConnectionFactory.createConnection(TestWithDisabledAuthorization.conf)) {
                    VisibilityClient.clearAuths(conn, new String[]{ TestWithDisabledAuthorization.SECRET }, TestWithDisabledAuthorization.USER_RW.getShortName());
                } catch (Throwable t) {
                    Assert.fail("Should not have failed");
                }
                return null;
            }
        });
        authsList = TestWithDisabledAuthorization.SUPERUSER.runAs(getAuths);
        Assert.assertEquals(1, authsList.size());
        Assert.assertTrue(authsList.contains(TestWithDisabledAuthorization.CONFIDENTIAL));
        TestWithDisabledAuthorization.SUPERUSER.runAs(new PrivilegedExceptionAction<Void>() {
            @Override
            public Void run() throws Exception {
                try (Connection conn = ConnectionFactory.createConnection(TestWithDisabledAuthorization.conf)) {
                    VisibilityClient.clearAuths(conn, new String[]{ TestWithDisabledAuthorization.CONFIDENTIAL }, TestWithDisabledAuthorization.USER_RW.getShortName());
                } catch (Throwable t) {
                    Assert.fail("Should not have failed");
                }
                return null;
            }
        });
        authsList = TestWithDisabledAuthorization.SUPERUSER.runAs(getAuths);
        Assert.assertEquals(0, authsList.size());
    }

    @Test
    public void testPassiveVisibility() throws Exception {
        // No values should be filtered regardless of authorization if we are passive
        try (Table t = TestWithDisabledAuthorization.createTableAndWriteDataWithLabels(TableName.valueOf(TEST_NAME.getMethodName()), TestWithDisabledAuthorization.SECRET, TestWithDisabledAuthorization.PRIVATE, (((TestWithDisabledAuthorization.SECRET) + "|") + (TestWithDisabledAuthorization.CONFIDENTIAL)), (((TestWithDisabledAuthorization.PRIVATE) + "|") + (TestWithDisabledAuthorization.CONFIDENTIAL)))) {
            Scan s = new Scan();
            s.setAuthorizations(new Authorizations());
            try (ResultScanner scanner = t.getScanner(s)) {
                Result[] next = scanner.next(10);
                Assert.assertEquals(4, next.length);
            }
            s = new Scan();
            s.setAuthorizations(new Authorizations(TestWithDisabledAuthorization.SECRET));
            try (ResultScanner scanner = t.getScanner(s)) {
                Result[] next = scanner.next(10);
                Assert.assertEquals(4, next.length);
            }
            s = new Scan();
            s.setAuthorizations(new Authorizations(TestWithDisabledAuthorization.SECRET, TestWithDisabledAuthorization.CONFIDENTIAL));
            try (ResultScanner scanner = t.getScanner(s)) {
                Result[] next = scanner.next(10);
                Assert.assertEquals(4, next.length);
            }
            s = new Scan();
            s.setAuthorizations(new Authorizations(TestWithDisabledAuthorization.SECRET, TestWithDisabledAuthorization.CONFIDENTIAL, TestWithDisabledAuthorization.PRIVATE));
            try (ResultScanner scanner = t.getScanner(s)) {
                Result[] next = scanner.next(10);
                Assert.assertEquals(4, next.length);
            }
        }
    }
}

