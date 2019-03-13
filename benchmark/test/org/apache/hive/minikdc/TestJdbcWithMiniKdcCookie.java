/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hive.minikdc;


import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hive.jdbc.miniHS2.MiniHS2;
import org.junit.Assert;
import org.junit.Test;


public class TestJdbcWithMiniKdcCookie {
    private static MiniHS2 miniHS2 = null;

    private static MiniHiveKdc miniHiveKdc = null;

    private Connection hs2Conn;

    File dataFile;

    protected static HiveConf hiveConf;

    private static String HIVE_NON_EXISTENT_USER = "hive_no_exist";

    @Test
    public void testCookie() throws Exception {
        String tableName = "test_cookie";
        dataFile = new File(TestJdbcWithMiniKdcCookie.hiveConf.get("test.data.files"), "kv1.txt");
        Connection hs2Conn = getConnection(MiniHiveKdc.HIVE_TEST_USER_1);
        Statement stmt = hs2Conn.createStatement();
        // create table
        stmt.execute((("create table " + tableName) + "(key int, value string) "));
        stmt.execute(((("load data local inpath '" + (dataFile)) + "' into table ") + tableName));
        // run a query in a loop so that we hit a 401 occasionally
        for (int i = 0; i < 10; i++) {
            stmt.execute(("select * from " + tableName));
        }
        stmt.execute(("drop table " + tableName));
        stmt.close();
    }

    @Test
    public void testCookieNegative() throws Exception {
        try {
            // Trying to connect with a non-existent user should still fail with
            // login failure.
            getConnection(TestJdbcWithMiniKdcCookie.HIVE_NON_EXISTENT_USER);
        } catch (IOException e) {
            Assert.assertTrue(e.getMessage().contains("javax.security.auth.login.LoginException"));
        }
    }
}

