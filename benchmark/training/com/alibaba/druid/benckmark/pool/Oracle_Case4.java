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


public class Oracle_Case4 extends TestCase {
    private String jdbcUrl;

    private String user;

    private String password;

    private String driverClass;

    private int maxIdle = 40;

    private int maxActive = 50;

    private int maxWait = 5000;

    private String validationQuery = "SELECT 1 FROM DUAL";

    private int threadCount = 1;

    private int loopCount = 5;

    final int LOOP_COUNT = 1000 * 1;

    private boolean testOnBorrow = false;

    private boolean preparedStatementCache = true;

    private int preparedStatementCacheSize = 50;

    private String properties = "defaultRowPrefetch=50";

    private String SQL;

    public void test_druid() throws Exception {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setMaxActive(maxActive);
        dataSource.setMaxIdle(maxIdle);
        dataSource.setMaxWait(maxWait);
        dataSource.setPoolPreparedStatements(preparedStatementCache);
        dataSource.setMaxOpenPreparedStatements(preparedStatementCacheSize);
        dataSource.setDriverClassName(driverClass);
        dataSource.setUrl(jdbcUrl);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        dataSource.setValidationQuery(validationQuery);
        dataSource.setTestOnBorrow(testOnBorrow);
        dataSource.setConnectionProperties(properties);
        dataSource.setUseOracleImplicitCache(true);
        dataSource.init();
        // printAV_INFO(dataSource);
        // printTables(dataSource);
        // printWP_ORDERS(dataSource);
        for (int i = 0; i < (loopCount); ++i) {
            p0(dataSource, "druid", threadCount);
        }
        System.out.println();
    }

    public void test_dbcp() throws Exception {
        final BasicDataSource dataSource = new BasicDataSource();
        dataSource.setMaxActive(maxActive);
        dataSource.setMaxIdle(maxIdle);
        dataSource.setMaxWait(maxWait);
        dataSource.setPoolPreparedStatements(preparedStatementCache);
        dataSource.setMaxOpenPreparedStatements(preparedStatementCacheSize);
        dataSource.setDriverClassName(driverClass);
        dataSource.setUrl(jdbcUrl);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        dataSource.setValidationQuery(validationQuery);
        dataSource.setTestOnBorrow(testOnBorrow);
        dataSource.setConnectionProperties(properties);
        // printAV_INFO(dataSource);
        for (int i = 0; i < (loopCount); ++i) {
            p0(dataSource, "dbcp", threadCount);
        }
        System.out.println();
    }
}

