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
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.hive.jdbc.miniHS2.MiniHS2;
import org.junit.Assert;
import org.junit.Test;


public abstract class JdbcWithMiniKdcSQLAuthTest {
    private static MiniHS2 miniHS2 = null;

    private static MiniHiveKdc miniHiveKdc;

    private Connection hs2Conn;

    @Test
    public void testAuthorization1() throws Exception {
        String tableName1 = "test_jdbc_sql_auth1";
        String tableName2 = "test_jdbc_sql_auth2";
        // using different code blocks so that jdbc variables are not accidently re-used
        // between the actions. Different connection/statement object should be used for each action.
        {
            // create tables as user1
            Connection hs2Conn = getConnection(MiniHiveKdc.HIVE_TEST_USER_1);
            Statement stmt = hs2Conn.createStatement();
            stmt.execute(("drop table if exists " + tableName1));
            stmt.execute(("drop table if exists " + tableName2));
            // create tables
            stmt.execute((("create table " + tableName1) + "(i int) "));
            stmt.execute((("create table " + tableName2) + "(i int) "));
            stmt.execute(((("grant select on table " + tableName2) + " to user ") + (MiniHiveKdc.HIVE_TEST_USER_2)));
            stmt.close();
            hs2Conn.close();
        }
        {
            // try dropping table as user1 - should succeed
            Connection hs2Conn = getConnection(MiniHiveKdc.HIVE_TEST_USER_1);
            Statement stmt = hs2Conn.createStatement();
            stmt.execute(("drop table " + tableName1));
        }
        {
            // try dropping table as user2 - should fail
            Connection hs2Conn = getConnection(MiniHiveKdc.HIVE_TEST_USER_2);
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
        {
            // try reading table2 as user2 - should succeed
            Connection hs2Conn = getConnection(MiniHiveKdc.HIVE_TEST_USER_2);
            Statement stmt = hs2Conn.createStatement();
            stmt.execute((" desc " + tableName2));
        }
    }
}

