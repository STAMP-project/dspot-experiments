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


import junit.framework.TestCase;


public class CaseKylin_mysql_idle_1 extends TestCase {
    private String jdbcUrl;

    private String user;

    private String password;

    private String driverClass;

    private int initialSize = 1;

    private int minIdle = 1;

    private int maxIdle = 20;

    private int maxActive = 20;

    private int maxWait = 60000;

    private String validationQuery = null;// "SELECT 1";


    private int threadCount = 15;

    private int TEST_COUNT = 3;

    final int LOOP_COUNT = 1000 * 10;

    private boolean testWhileIdle = true;

    private boolean testOnBorrow = false;

    private boolean testOnReturn = false;

    private boolean removeAbandoned = true;

    private int removeAbandonedTimeout = 180;

    private long timeBetweenEvictionRunsMillis = 60000;

    private long minEvictableIdleTimeMillis = 1800000;

    private int numTestsPerEvictionRun = 20;

    public void test_perf() throws Exception {
        druid();
    }
}

