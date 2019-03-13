/**
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.pool;


import com.alibaba.druid.util.OracleUtils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import junit.framework.TestCase;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.OraclePreparedStatement;


public class TestOraclePreparedStatement extends TestCase {
    private String jdbcUrl;

    private String user;

    private String password;

    private String SQL;

    public void test_0() throws Exception {
        Connection conn = DriverManager.getConnection(jdbcUrl, user, password);
        OracleConnection oracleConn = ((OracleConnection) (conn));
        // ResultSet metaRs = conn.getMetaData().getTables(null, "ALIBABA", null, new String[] {"TABLE"});
        // JdbcUtils.printResultSet(metaRs);
        // metaRs.close();
        int fetchRowSize = oracleConn.getDefaultRowPrefetch();
        // {
        // oracleConn.setStatementCacheSize(10);
        // oracleConn.setImplicitCachingEnabled(true);
        // 
        // PreparedStatement stmt = conn.prepareStatement(SQL);
        // stmt.close();
        // 
        // PreparedStatement stmt2 = conn.prepareStatement(SQL);
        // stmt2.close();
        // }
        OraclePreparedStatement oracleStmt = null;
        PreparedStatement stmt = conn.prepareStatement(SQL);
        oracleStmt = ((OraclePreparedStatement) (stmt));
        oracleStmt.setRowPrefetch(10);
        {
            stmt.setInt(1, 327);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
            } 
            rs.close();
            // oracleStmt.clearDefines();
        }
        for (int i = 0; i < 10; ++i) {
            OracleUtils.enterImplicitCache(oracleStmt);
            OracleUtils.exitImplicitCacheToActive(oracleStmt);
            stmt.setInt(1, 327);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
            } 
            rs.close();
        }
        oracleStmt.setRowPrefetch(1000);
        {
            stmt.setInt(1, 11);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            rs.close();
            stmt.close();
        }
        conn.close();
    }
}

