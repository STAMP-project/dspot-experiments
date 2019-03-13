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


import com.alibaba.druid.util.JdbcUtils;
import java.sql.Connection;
import junit.framework.TestCase;
import org.junit.Assert;


public class TestRollBack extends TestCase {
    private DruidDataSource dataSource;

    public void test_druid() throws Exception {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            insert(conn, 1, "abc");
            insert(conn, 2, "1234567");
        } catch (Exception e) {
            conn.rollback();
        } finally {
            JdbcUtils.close(conn);
        }
        Assert.assertEquals(0, count());
    }
}

