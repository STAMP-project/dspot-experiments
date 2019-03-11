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
package org.apache.hive.jdbc;


import java.sql.Connection;
import java.sql.Statement;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hive.jdbc.miniHS2.MiniHS2;
import org.apache.hive.service.cli.HiveSQLException;
import org.apache.hive.service.cli.session.HiveSessionHook;
import org.apache.hive.service.cli.session.HiveSessionHookContext;
import org.junit.Test;


/**
 * This class is cloned from TestJdbcWithMiniMR, except use Namenode HA.
 */
public class TestJdbcWithMiniHA {
    public static final String TEST_TAG = "miniHS2.miniHA.tag";

    public static final String TEST_TAG_VALUE = "miniHS2.miniHA.value";

    public static class HATestSessionHook implements HiveSessionHook {
        @Override
        public void run(HiveSessionHookContext sessionHookContext) throws HiveSQLException {
            sessionHookContext.getSessionConf().set(TestJdbcWithMiniHA.TEST_TAG, TestJdbcWithMiniHA.TEST_TAG_VALUE);
        }
    }

    private static MiniHS2 miniHS2 = null;

    private static HiveConf conf;

    private static Path dataFilePath;

    private static String dbName = "mrTestDb";

    private Connection hs2Conn = null;

    private Statement stmt;

    /**
     * Verify that the connection to MiniHS2 is successful
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testConnection() throws Exception {
        // the session hook should set the property
        verifyProperty(TestJdbcWithMiniHA.TEST_TAG, TestJdbcWithMiniHA.TEST_TAG_VALUE);
    }

    /**
     * Run nonMr query
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testNonMrQuery() throws Exception {
        String tableName = "testTab1";
        String resultVal = "val_238";
        String queryStr = "SELECT * FROM " + tableName;
        testKvQuery(tableName, queryStr, resultVal);
    }

    /**
     * Run nonMr query
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testMrQuery() throws Exception {
        String tableName = "testTab2";
        String resultVal = "val_238";
        String queryStr = ((("SELECT * FROM " + tableName) + " where value = '") + resultVal) + "'";
        testKvQuery(tableName, queryStr, resultVal);
    }
}

