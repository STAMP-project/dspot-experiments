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
package org.apache.hive.minikdc;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hive.jdbc.miniHS2.MiniHS2;
import org.apache.hive.service.cli.HiveSQLException;
import org.apache.hive.service.cli.session.HiveSessionHook;
import org.apache.hive.service.cli.session.HiveSessionHookContext;
import org.junit.Assert;
import org.junit.Test;


public class TestJdbcWithMiniKdc {
    // Need to hive.server2.session.hook to SessionHookTest in hive-site
    public static final String SESSION_USER_NAME = "proxy.test.session.user";

    // set current user in session conf
    public static class SessionHookTest implements HiveSessionHook {
        @Override
        public void run(HiveSessionHookContext sessionHookContext) throws HiveSQLException {
            sessionHookContext.getSessionConf().set(TestJdbcWithMiniKdc.SESSION_USER_NAME, sessionHookContext.getSessionUser());
        }
    }

    protected static MiniHS2 miniHS2 = null;

    protected static MiniHiveKdc miniHiveKdc = null;

    protected static Map<String, String> confOverlay = new HashMap<String, String>();

    protected Connection hs2Conn;

    /**
     * *
     * Basic connection test
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testConnection() throws Exception {
        TestJdbcWithMiniKdc.miniHiveKdc.loginUser(MiniHiveKdc.HIVE_TEST_USER_1);
        hs2Conn = DriverManager.getConnection(TestJdbcWithMiniKdc.miniHS2.getJdbcURL());
        verifyProperty(TestJdbcWithMiniKdc.SESSION_USER_NAME, MiniHiveKdc.HIVE_TEST_USER_1);
    }

    /**
     * *
     * Negative test, verify that connection to secure HS2 fails when
     * required connection attributes are not provided
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testConnectionNeg() throws Exception {
        TestJdbcWithMiniKdc.miniHiveKdc.loginUser(MiniHiveKdc.HIVE_TEST_USER_1);
        try {
            String url = TestJdbcWithMiniKdc.miniHS2.getJdbcURL().replaceAll(";principal.*", "");
            hs2Conn = DriverManager.getConnection(url);
            Assert.fail("NON kerberos connection should fail");
        } catch (SQLException e) {
            // expected error
            Assert.assertEquals("08S01", e.getSQLState().trim());
        }
    }

    /**
     * *
     * Test isValid() method
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testIsValid() throws Exception {
        TestJdbcWithMiniKdc.miniHiveKdc.loginUser(MiniHiveKdc.HIVE_TEST_SUPER_USER);
        hs2Conn = DriverManager.getConnection(TestJdbcWithMiniKdc.miniHS2.getJdbcURL());
        Assert.assertTrue(hs2Conn.isValid(1000));
        hs2Conn.close();
    }

    /**
     * *
     * Negative test isValid() method
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testIsValidNeg() throws Exception {
        TestJdbcWithMiniKdc.miniHiveKdc.loginUser(MiniHiveKdc.HIVE_TEST_SUPER_USER);
        hs2Conn = DriverManager.getConnection(TestJdbcWithMiniKdc.miniHS2.getJdbcURL());
        hs2Conn.close();
        Assert.assertFalse(hs2Conn.isValid(1000));
    }

    /**
     * *
     * Test token based authentication over kerberos
     * Login as super user and retrieve the token for normal user
     * use the token to connect connect as normal user
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testTokenAuth() throws Exception {
        TestJdbcWithMiniKdc.miniHiveKdc.loginUser(MiniHiveKdc.HIVE_TEST_SUPER_USER);
        hs2Conn = DriverManager.getConnection(TestJdbcWithMiniKdc.miniHS2.getJdbcURL());
        // retrieve token and store in the cache
        String token = getDelegationToken(MiniHiveKdc.HIVE_TEST_USER_1, MiniHiveKdc.HIVE_SERVICE_PRINCIPAL);
        Assert.assertTrue(((token != null) && (!(token.isEmpty()))));
        hs2Conn.close();
        UserGroupInformation ugi = TestJdbcWithMiniKdc.miniHiveKdc.loginUser(MiniHiveKdc.HIVE_TEST_USER_1);
        // Store token in the cache
        storeToken(token, ugi);
        hs2Conn = DriverManager.getConnection(((TestJdbcWithMiniKdc.miniHS2.getBaseJdbcURL()) + "default;auth=delegationToken"));
        verifyProperty(TestJdbcWithMiniKdc.SESSION_USER_NAME, MiniHiveKdc.HIVE_TEST_USER_1);
    }

    @Test
    public void testRenewDelegationToken() throws Exception {
        UserGroupInformation currentUGI = TestJdbcWithMiniKdc.miniHiveKdc.loginUser(MiniHiveKdc.HIVE_TEST_SUPER_USER);
        hs2Conn = DriverManager.getConnection(TestJdbcWithMiniKdc.miniHS2.getJdbcURL());
        String currentUser = currentUGI.getUserName();
        // retrieve token and store in the cache
        String token = getDelegationToken(MiniHiveKdc.HIVE_TEST_USER_1, TestJdbcWithMiniKdc.miniHiveKdc.getFullyQualifiedServicePrincipal(MiniHiveKdc.HIVE_TEST_SUPER_USER));
        Assert.assertTrue(((token != null) && (!(token.isEmpty()))));
        renewDelegationToken(token);
        hs2Conn.close();
    }

    @Test
    public void testCancelRenewTokenFlow() throws Exception {
        TestJdbcWithMiniKdc.miniHiveKdc.loginUser(MiniHiveKdc.HIVE_TEST_SUPER_USER);
        hs2Conn = DriverManager.getConnection(TestJdbcWithMiniKdc.miniHS2.getJdbcURL());
        // retrieve token and store in the cache
        String token = getDelegationToken(MiniHiveKdc.HIVE_TEST_USER_1, MiniHiveKdc.HIVE_SERVICE_PRINCIPAL);
        Assert.assertTrue(((token != null) && (!(token.isEmpty()))));
        Exception ex = null;
        cancelDelegationToken(token);
        try {
            renewDelegationToken(token);
        } catch (Exception SQLException) {
            ex = SQLException;
        }
        Assert.assertTrue(((ex != null) && (ex instanceof HiveSQLException)));
        // retrieve token and store in the cache
        token = getDelegationToken(MiniHiveKdc.HIVE_TEST_USER_1, MiniHiveKdc.HIVE_SERVICE_PRINCIPAL);
        Assert.assertTrue(((token != null) && (!(token.isEmpty()))));
        hs2Conn.close();
    }

    /**
     * *
     * Negative test for token based authentication
     * Verify that a user can't retrieve a token for user that
     * it's not allowed to impersonate
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testNegativeTokenAuth() throws Exception {
        TestJdbcWithMiniKdc.miniHiveKdc.loginUser(MiniHiveKdc.HIVE_TEST_SUPER_USER);
        hs2Conn = DriverManager.getConnection(TestJdbcWithMiniKdc.miniHS2.getJdbcURL());
        try {
            // retrieve token and store in the cache
            String token = getDelegationToken(MiniHiveKdc.HIVE_TEST_USER_2, MiniHiveKdc.HIVE_SERVICE_PRINCIPAL);
            Assert.fail((((MiniHiveKdc.HIVE_TEST_SUPER_USER) + " shouldn't be allowed to retrieve token for ") + (MiniHiveKdc.HIVE_TEST_USER_2)));
        } catch (SQLException e) {
            // Expected error
            Assert.assertEquals("Unexpected type of exception class thrown", HiveSQLException.class, e.getClass());
            Assert.assertTrue(e.getCause().getCause().getMessage().contains("is not allowed to impersonate"));
        } finally {
            hs2Conn.close();
        }
    }

    /**
     * Test connection using the proxy user connection property
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testProxyAuth() throws Exception {
        TestJdbcWithMiniKdc.miniHiveKdc.loginUser(MiniHiveKdc.HIVE_TEST_SUPER_USER);
        hs2Conn = DriverManager.getConnection(TestJdbcWithMiniKdc.miniHS2.getJdbcURL("default", (";hive.server2.proxy.user=" + (MiniHiveKdc.HIVE_TEST_USER_1))));
        verifyProperty(TestJdbcWithMiniKdc.SESSION_USER_NAME, MiniHiveKdc.HIVE_TEST_USER_1);
    }

    /**
     * Test connection using the proxy user connection property.
     * Verify proxy connection fails when super user doesn't have privilege to
     * impersonate the given user
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testNegativeProxyAuth() throws Exception {
        TestJdbcWithMiniKdc.miniHiveKdc.loginUser(MiniHiveKdc.HIVE_TEST_SUPER_USER);
        try {
            hs2Conn = DriverManager.getConnection(TestJdbcWithMiniKdc.miniHS2.getJdbcURL("default", (";hive.server2.proxy.user=" + (MiniHiveKdc.HIVE_TEST_USER_2))));
            verifyProperty(TestJdbcWithMiniKdc.SESSION_USER_NAME, MiniHiveKdc.HIVE_TEST_USER_2);
            Assert.fail((((MiniHiveKdc.HIVE_TEST_SUPER_USER) + " shouldn't be allowed proxy connection for ") + (MiniHiveKdc.HIVE_TEST_USER_2)));
        } catch (SQLException e) {
            // Expected error
            e.printStackTrace();
            Assert.assertTrue(e.getMessage().contains("Failed to validate proxy privilege"));
            Assert.assertTrue(e.getCause().getCause().getCause().getMessage().contains("is not allowed to impersonate"));
        }
    }
}

