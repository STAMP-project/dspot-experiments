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


import com.alibaba.druid.mock.MockConnection;
import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.mock.MockPreparedStatement;
import com.alibaba.druid.pool.DruidDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import junit.framework.TestCase;
import org.junit.Assert;


public class TestGetUpdateCount extends TestCase {
    private DruidDataSource dataSource;

    private MockDriver driver;

    public void test_executeQuery() throws Exception {
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("select ?");
        TestGetUpdateCount.MyPreparedStatement myStmt = stmt.unwrap(TestGetUpdateCount.MyPreparedStatement.class);
        Assert.assertNull(myStmt.updateCount);
        stmt.setString(1, "xxx");
        ResultSet rs = stmt.executeQuery();
        Assert.assertEquals((-1), myStmt.updateCount.intValue());
        rs.close();
        stmt.close();
        conn.close();
    }

    public void test_execute() throws Exception {
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("update t set id = ?");
        TestGetUpdateCount.MyPreparedStatement myStmt = stmt.unwrap(TestGetUpdateCount.MyPreparedStatement.class);
        Assert.assertNull(myStmt.updateCount);
        stmt.setString(1, "xxx");
        stmt.execute();
        Assert.assertNotNull(myStmt.updateCount);
        Assert.assertEquals(1, stmt.getUpdateCount());
        stmt.close();
        conn.close();
    }

    public void test_execute_multi() throws Exception {
        TestGetUpdateCount.MyPreparedStatement myStmtA = null;
        TestGetUpdateCount.MyPreparedStatement myStmtB = null;
        {
            Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement("update t set id = ?");
            myStmtA = stmt.unwrap(TestGetUpdateCount.MyPreparedStatement.class);
            Assert.assertNull(myStmtA.updateCount);
            stmt.setString(1, "xxx");
            stmt.execute();
            Assert.assertNotNull(myStmtA.updateCount);
            Assert.assertEquals(1, stmt.getUpdateCount());
            stmt.close();
            conn.close();
        }
        {
            Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement("update t set id = ?");
            myStmtB = stmt.unwrap(TestGetUpdateCount.MyPreparedStatement.class);
            Assert.assertSame(myStmtA, myStmtB);
            Assert.assertNotNull(myStmtB.updateCount);
            stmt.setString(1, "xxx");
            stmt.execute();
            Assert.assertNotNull(myStmtB.updateCount);
            Assert.assertEquals(1, stmt.getUpdateCount());
            stmt.close();
            conn.close();
        }
    }

    public static class MyPreparedStatement extends MockPreparedStatement {
        Integer updateCount = null;

        public MyPreparedStatement(MockConnection conn, String sql) {
            super(conn, sql);
        }

        public boolean execute() throws SQLException {
            updateCount = null;
            return false;
        }

        public ResultSet executeQuery() throws SQLException {
            updateCount = -1;
            return super.executeQuery();
        }

        @Override
        public int getUpdateCount() throws SQLException {
            if ((updateCount) != null) {
                throw new SQLException("illegal state");
            }
            updateCount = 1;
            return updateCount;
        }
    }
}

