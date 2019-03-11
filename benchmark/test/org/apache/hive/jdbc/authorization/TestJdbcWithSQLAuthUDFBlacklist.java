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


import ConfVars.HIVE_SERVER2_BUILTIN_UDF_BLACKLIST;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hive.jdbc.miniHS2.MiniHS2;
import org.junit.Test;


/**
 * Test SQL standard authorization with jdbc/hiveserver2. Specifically udf blacklist behavior.
 * This test needs to start minihs2 with specific configuration, so it is in a different
 * test file from {@link TestJdbcWithSQLAuthorization}
 */
public class TestJdbcWithSQLAuthUDFBlacklist {
    private MiniHS2 miniHS2 = null;

    @Test
    public void testBlackListedUdfUsage() throws Exception {
        HiveConf conf = new HiveConf();
        conf.setVar(HIVE_SERVER2_BUILTIN_UDF_BLACKLIST, "sqrt");
        startHS2(conf);
        // create tables as user1
        Connection hs2Conn = DriverManager.getConnection(miniHS2.getJdbcURL(), "user1", "bar");
        Statement stmt = hs2Conn.createStatement();
        String tableName1 = "test_jdbc_sql_auth_udf_blacklist";
        stmt.execute((("create table " + tableName1) + "(i int) "));
        verifyUDFNotAllowed(stmt, tableName1, "sqrt(1)", "sqrt");
        // verify udf reflect is allowed (no exception will be thrown)
        stmt.execute(("SELECT reflect('java.lang.String', 'valueOf', 1) from " + tableName1));
        stmt.close();
        hs2Conn.close();
    }
}

