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


public class Oracle_Case3 extends TestCase {
    private String jdbcUrl;

    private String user;

    private String password;

    private String driverClass;

    private int maxIdle = 40;

    private int maxActive = 50;

    private int maxWait = 5000;

    private String validationQuery = "SELECT 1 FROM DUAL";

    private int threadCount = 40;

    private int loopCount = 5;

    final int LOOP_COUNT = 1000 * 10;

    private boolean testOnBorrow = true;

    public void test_0() throws Exception {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setMaxActive(maxActive);
        dataSource.setMaxIdle(maxIdle);
        dataSource.setMaxWait(maxWait);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setDriverClassName(driverClass);
        dataSource.setUrl(jdbcUrl);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        dataSource.setValidationQuery(validationQuery);
        dataSource.setTestOnBorrow(testOnBorrow);
        for (int i = 0; i < (loopCount); ++i) {
            p0(dataSource, "druid", threadCount);
        }
        System.out.println();
    }

    public void test_1() throws Exception {
        final BasicDataSource dataSource = new BasicDataSource();
        dataSource.setMaxActive(maxActive);
        dataSource.setMaxIdle(maxIdle);
        dataSource.setMaxWait(maxWait);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setDriverClassName(driverClass);
        dataSource.setUrl(jdbcUrl);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        dataSource.setValidationQuery(validationQuery);
        dataSource.setTestOnBorrow(testOnBorrow);
        for (int i = 0; i < (loopCount); ++i) {
            p0(dataSource, "dbcp", threadCount);
        }
        System.out.println();
    }
}

