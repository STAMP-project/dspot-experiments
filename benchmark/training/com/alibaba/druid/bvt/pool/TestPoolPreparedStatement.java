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
import com.alibaba.druid.mock.MockPreparedStatement;
import com.alibaba.druid.pool.DruidDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import junit.framework.TestCase;
import org.junit.Assert;


public class TestPoolPreparedStatement extends TestCase {
    private MockDriver driver;

    private DruidDataSource dataSource;

    public void test_removeAbandoned() throws Exception {
        MockPreparedStatement raw;
        {
            Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT ?");
            raw = stmt.unwrap(MockPreparedStatement.class);
            stmt.execute();
            stmt.close();
            conn.close();
        }
        {
            Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT ?");
            Assert.assertTrue((raw == (stmt.unwrap(MockPreparedStatement.class))));
            stmt.execute();
            stmt.close();
            conn.close();
        }
        Assert.assertEquals(0, dataSource.getActiveCount());
        Assert.assertEquals(1, dataSource.getPoolingCount());
    }
}

