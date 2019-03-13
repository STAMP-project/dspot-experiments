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


public class Case0 extends TestCase {
    private String jdbcUrl;

    private String user;

    private String password;

    private String driverClass;

    private int initialSize = 1;

    private int minIdle = 3;

    private int maxIdle = 8;

    private int maxActive = 8;

    private String validationQuery = "SELECT 1";

    private boolean testOnBorrow = false;

    private long minEvictableIdleTimeMillis = 3000;

    public final int LOOP_COUNT = 5;

    public final int COUNT = (1000 * 1000) * 1;

    public void test_druid() throws Exception {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setInitialSize(initialSize);
        dataSource.setMaxActive(maxActive);
        dataSource.setMinIdle(minIdle);
        dataSource.setMaxIdle(maxIdle);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setDriverClassName(driverClass);
        dataSource.setUrl(jdbcUrl);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        dataSource.setValidationQuery(validationQuery);
        dataSource.setTestOnBorrow(testOnBorrow);
        dataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        for (int i = 0; i < (LOOP_COUNT); ++i) {
            p0(dataSource, "druid");
        }
        System.out.println();
    }
}

