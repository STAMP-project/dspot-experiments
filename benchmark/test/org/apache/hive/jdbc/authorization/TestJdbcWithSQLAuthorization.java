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
package org.apache.hive.jdbc.authorization;


import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.hive.jdbc.miniHS2.MiniHS2;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test SQL standard authorization with jdbc/hiveserver2
 */
public class TestJdbcWithSQLAuthorization {
    private static MiniHS2 miniHS2 = null;

    @Test
    public void testAuthorization1() throws Exception {
        String tableName1 = "test_jdbc_sql_auth1";
        String tableName2 = "test_jdbc_sql_auth2";
        // using different code blocks so that jdbc variables are not accidently re-used
        // between the actions. Different connection/statement object should be used for each action.
        {
            // create tables as user1
            Connection hs2Conn = getConnection("user1");
            Statement stmt = hs2Conn.createStatement();
            // create tables
            stmt.execute((("create table " + tableName1) + "(i int) "));
            stmt.execute((("create table " + tableName2) + "(i int) "));
            stmt.close();
            hs2Conn.close();
        }
        {
            // try dropping table as user1 - should succeed
            Connection hs2Conn = getConnection("user1");
            Statement stmt = hs2Conn.createStatement();
            stmt.execute(("drop table " + tableName1));
        }
        {
            // try using jdbc metadata api to get column list as user2 - should fail
            Connection hs2Conn = getConnection("user2");
            try {
                hs2Conn.getMetaData().getColumns(null, "default", tableName2, null);
                Assert.fail("Exception due to authorization failure is expected");
            } catch (SQLException e) {
                String msg = e.getMessage();
                // check parts of the error, not the whole string so as not to tightly
                // couple the error message with test
                System.err.println(("Got SQLException with message " + msg));
                Assert.assertTrue("Checking permission denied error", msg.contains("user2"));
                Assert.assertTrue("Checking permission denied error", msg.contains(tableName2));
                Assert.assertTrue("Checking permission denied error", msg.contains("SELECT"));
            }
        }
        {
            // try dropping table as user2 - should fail
            Connection hs2Conn = getConnection("user2");
            try {
                Statement stmt = hs2Conn.createStatement();
                stmt.execute(("drop table " + tableName2));
                Assert.fail("Exception due to authorization failure is expected");
            } catch (SQLException e) {
                String msg = e.getMessage();
                System.err.println(("Got SQLException with message " + msg));
                // check parts of the error, not the whole string so as not to tightly
                // couple the error message with test
                Assert.assertTrue("Checking permission denied error", msg.contains("user2"));
                Assert.assertTrue("Checking permission denied error", msg.contains(tableName2));
                Assert.assertTrue("Checking permission denied error", msg.contains("OBJECT OWNERSHIP"));
            }
        }
    }

    @Test
    public void testAllowedCommands() throws Exception {
        // using different code blocks so that jdbc variables are not accidently re-used
        // between the actions. Different connection/statement object should be used for each action.
        {
            // create tables as user1
            Connection hs2Conn = getConnection("user1");
            boolean caughtException = false;
            Statement stmt = hs2Conn.createStatement();
            // create tables
            try {
                stmt.execute("dfs -ls /tmp/");
            } catch (SQLException e) {
                caughtException = true;
                String msg = "Permission denied: Principal [name=user1, type=USER] does not have " + ("following privileges for operation DFS [[ADMIN PRIVILEGE] on " + "Object [type=COMMAND_PARAMS, name=[-ls, /tmp/]]]");
                Assert.assertTrue(("Checking content of error message:" + (e.getMessage())), e.getMessage().contains(msg));
            } finally {
                stmt.close();
                hs2Conn.close();
            }
            Assert.assertTrue("Exception expected ", caughtException);
        }
    }

    @Test
    public void testAuthZFailureLlapCachePurge() throws Exception {
        // using different code blocks so that jdbc variables are not accidently re-used
        // between the actions. Different connection/statement object should be used for each action.
        {
            Connection hs2Conn = getConnection("user1");
            boolean caughtException = false;
            Statement stmt = hs2Conn.createStatement();
            try {
                stmt.execute("llap cache -purge");
            } catch (SQLException e) {
                caughtException = true;
                String msg = "Error while processing statement: Permission denied: Principal [name=user1, type=USER] does " + (("not have following privileges for operation LLAP_CACHE_PURGE [[ADMIN PRIVILEGE] on Object " + "[type=COMMAND_PARAMS, name=[llap, cache, -purge]], [ADMIN PRIVILEGE] on Object ") + "[type=SERVICE_NAME, name=localhost]]");
                Assert.assertEquals(msg, e.getMessage());
            } finally {
                stmt.close();
                hs2Conn.close();
            }
            Assert.assertTrue("Exception expected ", caughtException);
        }
    }

    @Test
    public void testBlackListedUdfUsage() throws Exception {
        // create tables as user1
        Connection hs2Conn = getConnection("user1");
        Statement stmt = hs2Conn.createStatement();
        String tableName1 = "test_jdbc_sql_auth_udf";
        stmt.execute((("create table " + tableName1) + "(i int) "));
        verifyUDFNotAllowed(stmt, tableName1, "reflect('java.lang.String', 'valueOf', 1)", "reflect");
        verifyUDFNotAllowed(stmt, tableName1, "reflect2('java.lang.String', 'valueOf', 1)", "reflect2");
        verifyUDFNotAllowed(stmt, tableName1, "java_method('java.lang.String', 'valueOf', 1)", "java_method");
        stmt.close();
        hs2Conn.close();
    }

    @Test
    public void testConfigWhiteList() throws Exception {
        // create tables as user1
        Connection hs2Conn = getConnection("user1");
        Statement stmt = hs2Conn.createStatement();
        try {
            stmt.execute("set hive.metastore.uris=x");
            Assert.fail("exception expected");
        } catch (SQLException e) {
            String msg = "Cannot modify hive.metastore.uris at runtime. " + "It is not in list of params that are allowed to be modified at runtime";
            Assert.assertTrue(e.getMessage().contains(msg));
        }
        stmt.execute("set hive.exec.reducers.bytes.per.reducer=10000");
        // no exception should be thrown
        stmt.close();
        hs2Conn.close();
    }
}

