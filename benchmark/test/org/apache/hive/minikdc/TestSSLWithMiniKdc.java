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


import java.sql.Connection;
import java.sql.Statement;
import org.apache.hadoop.fs.Path;
import org.apache.hive.jdbc.miniHS2.MiniHS2;
import org.hadoop.hive.jdbc.SSLTestUtils;
import org.junit.Test;


public class TestSSLWithMiniKdc {
    private static MiniHS2 miniHS2 = null;

    private static MiniHiveKdc miniHiveKdc = null;

    @Test
    public void testConnection() throws Exception {
        String tableName = "testTable";
        Path dataFilePath = new Path(SSLTestUtils.getDataFileDir(), "kv1.txt");
        Connection hs2Conn = getConnection(MiniHiveKdc.HIVE_TEST_USER_1);
        Statement stmt = hs2Conn.createStatement();
        SSLTestUtils.setupTestTableWithData(tableName, dataFilePath, hs2Conn);
        stmt.execute(("select * from " + tableName));
        stmt.execute(("drop table " + tableName));
        stmt.close();
    }
}

