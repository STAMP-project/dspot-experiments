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
package com.alibaba.druid.bvt.pool;


import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.pool.DruidPooledPreparedStatement;
import com.alibaba.druid.pool.DruidPooledResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import junit.framework.TestCase;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.OraclePreparedStatement;
import oracle.jdbc.OracleResultSet;
import org.junit.Assert;


public class TestOracleWrap2 extends TestCase {
    private DruidDataSource dataSource;

    public void test_oracle() throws Exception {
        String sql = "SELECT 1";
        {
            Connection conn = dataSource.getConnection();
            Assert.assertTrue(conn.isWrapperFor(DruidPooledConnection.class));
            Assert.assertNotNull(conn.unwrap(DruidPooledConnection.class));
            Assert.assertTrue(conn.isWrapperFor(OracleConnection.class));
            Assert.assertNotNull(conn.unwrap(OracleConnection.class));
            Assert.assertTrue(conn.isWrapperFor(Connection.class));
            Assert.assertNotNull(conn.unwrap(Connection.class));
            // /////////////
            PreparedStatement stmt = conn.prepareStatement(sql);
            Assert.assertNotNull(stmt.unwrap(OraclePreparedStatement.class));
            Assert.assertTrue(stmt.isWrapperFor(OraclePreparedStatement.class));
            Assert.assertTrue(stmt.isWrapperFor(DruidPooledPreparedStatement.class));
            Assert.assertNotNull(stmt.unwrap(DruidPooledPreparedStatement.class));
            Assert.assertTrue(stmt.isWrapperFor(PreparedStatement.class));
            Assert.assertNotNull(stmt.unwrap(PreparedStatement.class));
            ResultSet rs = stmt.executeQuery();
            Assert.assertNotNull(rs.unwrap(OracleResultSet.class));
            Assert.assertTrue(rs.isWrapperFor(OracleResultSet.class));
            Assert.assertTrue(rs.isWrapperFor(DruidPooledResultSet.class));
            Assert.assertNotNull(rs.unwrap(DruidPooledResultSet.class));
            Assert.assertTrue(rs.isWrapperFor(ResultSet.class));
            Assert.assertNotNull(rs.unwrap(ResultSet.class));
            rs.next();
            rs.close();
            stmt.close();
            conn.close();
        }
        for (int i = 0; i < 10; ++i) {
            Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            rs.close();
            stmt.close();
            conn.close();
        }
        Assert.assertEquals(1, dataSource.getCachedPreparedStatementCount());
    }
}

