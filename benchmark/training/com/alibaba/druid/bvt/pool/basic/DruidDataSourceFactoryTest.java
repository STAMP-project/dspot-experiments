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
package com.alibaba.druid.bvt.pool.basic;


import com.alibaba.druid.pool.DruidDataSourceFactory;
import java.util.Properties;
import junit.framework.TestCase;


public class DruidDataSourceFactoryTest extends TestCase {
    public void test_createDataSource() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("defaultAutoCommit", "true");
        properties.setProperty("defaultReadOnly", "true");
        properties.setProperty("defaultTransactionIsolation", "NONE");
        properties.setProperty("defaultCatalog", "cn");
        properties.setProperty("driverClassName", "com.alibaba.druid.mock.MockDriver");
        properties.setProperty("maxActive", "8");
        properties.setProperty("maxIdle", "8");
        properties.setProperty("minIdle", "3");
        properties.setProperty("initialSize", "1");
        properties.setProperty("maxWait", "-1");
        properties.setProperty("testOnBorrow", "true");
        properties.setProperty("testOnReturn", "true");
        properties.setProperty("timeBetweenEvictionRunsMillis", "3000");
        properties.setProperty("numTestsPerEvictionRun", "1");
        properties.setProperty("minEvictableIdleTimeMillis", "10000");
        properties.setProperty("testWhileIdle", "true");
        properties.setProperty("password", "xxx");
        properties.setProperty("url", "jdbc:mock:xxx");
        properties.setProperty("username", "user");
        properties.setProperty("validationQuery", "select 1");
        properties.setProperty("validationQueryTimeout", "30");
        properties.setProperty("initConnectionSqls", "select 1");
        properties.setProperty("accessToUnderlyingConnectionAllowed", "true");
        properties.setProperty("removeAbandoned", "true");
        properties.setProperty("removeAbandonedTimeout", "30");
        properties.setProperty("logAbandoned", "true");
        properties.setProperty("poolPreparedStatements", "true");
        properties.setProperty("maxOpenPreparedStatements", "200");
        properties.setProperty("connectionProperties", "x=1;y=2;;");
        properties.setProperty("filters", "stat;trace");
        properties.setProperty("exceptionSorter", "com.alibaba.druid.pool.vendor.NullExceptionSorter");
        properties.setProperty("exception-sorter-class-name", "com.alibaba.druid.pool.vendor.NullExceptionSorter");
        DruidDataSourceFactory.createDataSource(properties);
    }
}

