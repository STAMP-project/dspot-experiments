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


import JdbcUtils.H2;
import com.alibaba.druid.pool.DruidDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import junit.framework.TestCase;
import org.junit.Assert;


public class TestH2 extends TestCase {
    private DruidDataSource dataSource;

    public void test_h2() throws Exception {
        Assert.assertSame(H2, dataSource.getDbType());
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT ?");
        stmt.setString(1, "xxxx");
        ResultSet rs = stmt.executeQuery();
        rs.next();
        Assert.assertEquals("xxxx", rs.getString(1));
        rs.close();
        stmt.close();
        conn.close();
    }
}

