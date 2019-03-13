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


import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import junit.framework.TestCase;
import org.junit.Assert;


public class TestPoolPreparedStatement2 extends TestCase {
    private MockDriver driver;

    private DruidDataSource dataSource;

    public void test_stmtCache() throws Exception {
        for (int j = 0; j < 10; ++j) {
            for (int i = 0; i < 10; ++i) {
                Connection conn = dataSource.getConnection();
                String sql = "SELECT" + i;
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.execute();
                stmt.close();
                conn.close();
            }
        }
        dataSource.setPoolPreparedStatements(true);
        for (int j = 0; j < 10; ++j) {
            for (int i = 0; i < 10; ++i) {
                Connection conn = dataSource.getConnection();
                String sql = "SELECT" + i;
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.execute();
                stmt.close();
                conn.close();
            }
        }
        for (int i = 0; i < (10 * 1); ++i) {
            Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(("SELECT " + i));
            stmt.execute();
            stmt.close();
            conn.close();
        }
        Connection conn = dataSource.getConnection();
        DruidPooledConnection poolableConn = conn.unwrap(DruidPooledConnection.class);
        Assert.assertNotNull(poolableConn);
        Assert.assertEquals(dataSource.getMaxPoolPreparedStatementPerConnectionSize(), poolableConn.getConnectionHolder().getStatementPool().getMap().size());
        conn.close();
        Assert.assertEquals(0, dataSource.getActiveCount());
        Assert.assertEquals(1, dataSource.getPoolingCount());
    }
}

