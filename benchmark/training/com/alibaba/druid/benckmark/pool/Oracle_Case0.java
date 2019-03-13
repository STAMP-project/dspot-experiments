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
import com.jolbox.bonecp.BoneCPDataSource;
import junit.framework.TestCase;
import org.apache.commons.dbcp.BasicDataSource;


public class Oracle_Case0 extends TestCase {
    private String jdbcUrl;

    private String user;

    private String password;

    private String driverClass;

    private int initialSize = 1;

    private int minPoolSize = 1;

    private int maxPoolSize = 2;

    private int maxActive = 2;

    private String validationQuery = "SELECT 1 FROM DUAL";

    public final int LOOP_COUNT = 5;

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
        dataSource.setTestOnBorrow(true);
        for (int i = 0; i < (LOOP_COUNT); ++i) {
            p0(dataSource, "druid");
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
        dataSource.setValidationQuery(validationQuery);
        dataSource.setTestOnBorrow(true);
        for (int i = 0; i < (LOOP_COUNT); ++i) {
            p0(dataSource, "dbcp");
        }
        System.out.println();
    }

    public void test_2() throws Exception {
        BoneCPDataSource dataSource = new BoneCPDataSource();
        // dataSource.(10);
        // dataSource.setMaxActive(50);
        dataSource.setMinConnectionsPerPartition(minPoolSize);
        dataSource.setMaxConnectionsPerPartition(maxPoolSize);
        dataSource.setDriverClass(driverClass);
        dataSource.setJdbcUrl(jdbcUrl);
        // dataSource.setPoolPreparedStatements(true);
        // dataSource.setMaxOpenPreparedStatements(100);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        dataSource.setConnectionTestStatement(validationQuery);
        dataSource.setPartitionCount(1);
        for (int i = 0; i < (LOOP_COUNT); ++i) {
            p0(dataSource, "boneCP");
        }
        System.out.println();
    }
}

