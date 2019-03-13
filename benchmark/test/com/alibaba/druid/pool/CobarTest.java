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


import com.alibaba.druid.pool.vendor.MySqlExceptionSorter;
import java.sql.Connection;
import junit.framework.TestCase;


public class CobarTest extends TestCase {
    private String jdbcUrl;

    private String user;

    private String password;

    private String driverClass;

    private DruidDataSource dataSource;

    public void test_0() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setDriverClassName(driverClass);
        dataSource.setUrl(jdbcUrl);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        dataSource.setFilters("stat");
        dataSource.setExceptionSorter(MySqlExceptionSorter.class.getName());
        Connection conn = dataSource.getConnection();
        conn.close();
        dataSource.close();
    }
}

