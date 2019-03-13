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
package com.alibaba.druid.bvt.proxy;


import com.alibaba.druid.util.JdbcUtils;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import junit.framework.TestCase;
import org.junit.Assert;


public class SchemaReadTest extends TestCase {
    private static String url = "jdbc:wrap-jdbc:filters=default,commonLogging,log4j:name=demo:jdbc:derby:classpath:petstore-db";

    public void test_schema() throws Exception {
        Class.forName("com.alibaba.druid.proxy.DruidDriver");
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DriverManager.getConnection(SchemaReadTest.url);
            Assert.assertTrue(conn.isReadOnly());
            // just call
            conn.getHoldability();
            conn.getTransactionIsolation();
            conn.getWarnings();
            conn.getTypeMap();
            conn.getAutoCommit();
            conn.getCatalog();
            conn.getClientInfo();
            conn.getClientInfo("xx");
            DatabaseMetaData metadata = conn.getMetaData();
            {
                ResultSet tableTypes = metadata.getTableTypes();
                JdbcUtils.printResultSet(tableTypes, System.out);
                JdbcUtils.close(tableTypes);
            }
            {
                conn.setAutoCommit(false);
                ResultSet tables = metadata.getTables(null, null, null, null);
                JdbcUtils.printResultSet(tables, System.out);
                conn.commit();
                conn.setAutoCommit(true);
                JdbcUtils.close(tables);
            }
            {
                ResultSet tables = metadata.getTables(null, null, null, null);
                while (tables.next()) {
                    String schema = tables.getString(2);
                    String tableName = tables.getString(3);
                    String sql = (("SELECT * FROM " + schema) + ".") + tableName;
                    stmt = conn.createStatement();
                    rs = stmt.executeQuery(sql);
                    JdbcUtils.printResultSet(rs, System.out);
                    JdbcUtils.close(rs);
                    Assert.assertTrue(rs.isClosed());
                    JdbcUtils.close(stmt);
                    Assert.assertTrue(stmt.isClosed());
                } 
                JdbcUtils.close(tables);
            }
        } finally {
            JdbcUtils.close(rs);
            JdbcUtils.close(stmt);
            JdbcUtils.close(conn);
            Assert.assertTrue(conn.isClosed());
        }
    }

    public void test_schema2() throws Exception {
        Class.forName("com.alibaba.druid.proxy.DruidDriver");
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DriverManager.getConnection(SchemaReadTest.url);
            Assert.assertTrue(conn.isReadOnly());
            // just call
            conn.getHoldability();
            conn.getTransactionIsolation();
            conn.getWarnings();
            conn.getTypeMap();
            conn.getAutoCommit();
            conn.getCatalog();
            conn.getClientInfo();
            conn.getClientInfo("xx");
            conn.isValid(10);
            DatabaseMetaData metadata = conn.getMetaData();
            {
                ResultSet tableTypes = metadata.getTableTypes();
                SchemaReadTest.printResultSetUseColumnName(tableTypes, System.out);
                JdbcUtils.close(tableTypes);
            }
            {
                conn.setAutoCommit(false);
                ResultSet tables = metadata.getTables(null, null, null, null);
                SchemaReadTest.printResultSetUseColumnName(tables, System.out);
                conn.commit();
                conn.setAutoCommit(true);
                JdbcUtils.close(tables);
            }
            {
                ResultSet tables = metadata.getTables(null, null, null, null);
                while (tables.next()) {
                    String schema = tables.getString(2);
                    String tableName = tables.getString(3);
                    String sql = (("SELECT * FROM " + schema) + ".") + tableName;
                    stmt = conn.createStatement();
                    rs = stmt.executeQuery(sql);
                    SchemaReadTest.printResultSetUseColumnName(rs, System.out);
                    JdbcUtils.close(rs);
                    Assert.assertTrue(rs.isClosed());
                    JdbcUtils.close(stmt);
                    Assert.assertTrue(stmt.isClosed());
                } 
                JdbcUtils.close(tables);
            }
        } finally {
            JdbcUtils.close(rs);
            JdbcUtils.close(stmt);
            JdbcUtils.close(conn);
            Assert.assertTrue(conn.isClosed());
        }
    }
}

