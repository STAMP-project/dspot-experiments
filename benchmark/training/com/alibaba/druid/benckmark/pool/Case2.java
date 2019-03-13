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
package com.alibaba.druid.benckmark.pool;


import com.alibaba.druid.pool.DruidDataSource;
import junit.framework.TestCase;
import org.apache.commons.dbcp.BasicDataSource;


/**
 * TestOnBo ?Case1.java??????TODO ?????
 *
 * @author admin 2011-5-28 ??03:47:40
 */
public class Case2 extends TestCase {
    private String jdbcUrl;

    private String user;

    private String password;

    private String driverClass;

    private int initialSize = 10;

    private int minPoolSize = 10;

    private int maxPoolSize = 50;

    private int maxActive = 50;

    private String validationQuery = "SELECT 1";

    private int threadCount = 100;

    private int executeCount = 4;

    final int LOOP_COUNT = (1000 * 100) / (executeCount);

    private boolean testOnBorrow = true;

    public void test_0() throws Exception {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setInitialSize(initialSize);
        dataSource.setMaxActive(maxActive);
        dataSource.setMinIdle(minPoolSize);
        dataSource.setMaxIdle(maxPoolSize);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setDriverClassName(driverClass);
        dataSource.setUrl(jdbcUrl);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        dataSource.setValidationQuery(validationQuery);
        dataSource.setTestOnBorrow(testOnBorrow);
        for (int i = 0; i < (executeCount); ++i) {
            p0(dataSource, "druid", threadCount);
        }
        System.out.println();
    }

    public void test_1() throws Exception {
        final BasicDataSource dataSource = new BasicDataSource();
        dataSource.setInitialSize(initialSize);
        dataSource.setMaxActive(maxActive);
        dataSource.setMinIdle(minPoolSize);
        dataSource.setMaxIdle(maxPoolSize);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setDriverClassName(driverClass);
        dataSource.setUrl(jdbcUrl);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        dataSource.setValidationQuery("SELECT 1");
        dataSource.setTestOnBorrow(testOnBorrow);
        for (int i = 0; i < (executeCount); ++i) {
            p0(dataSource, "dbcp", threadCount);
        }
        System.out.println();
    }
}

