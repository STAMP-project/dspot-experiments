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


import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DruidDataSource;
import com.jolbox.bonecp.BoneCPDataSource;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.taobao.datasource.LocalTxDataSourceDO;
import com.taobao.datasource.TaobaoDataSourceFactory;
import com.taobao.datasource.resource.adapter.jdbc.local.LocalTxDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;
import javax.sql.DataSource;
import junit.framework.TestCase;
import org.apache.commons.dbcp.BasicDataSource;
import org.logicalcobwebs.proxool.ProxoolDataSource;


/**
 * TestOnBo ?Case1.java??????TODO ?????
 *
 * @author admin 2011-5-28 ??03:47:40
 */
public class Case1 extends TestCase {
    private String jdbcUrl;

    private String user;

    private String password;

    private String driverClass;

    private int initialSize = 10;

    private int minPoolSize = 10;

    private int maxPoolSize = 50;

    private int maxActive = 50;

    private String validationQuery = "SELECT 1";

    private int threadCount = 5;

    private int loopCount = 10;

    final int LOOP_COUNT = ((1000 * 1) * 1) / (threadCount);

    private static AtomicLong physicalConnStat = new AtomicLong();

    public static class TestDriver extends MockDriver {
        public static Case1.TestDriver instance = new Case1.TestDriver();

        public boolean acceptsURL(String url) throws SQLException {
            if (url.startsWith("jdbc:test:")) {
                return true;
            }
            return super.acceptsURL(url);
        }

        public Connection connect(String url, Properties info) throws SQLException {
            Case1.physicalConnStat.incrementAndGet();
            return super.connect("jdbc:mock:case1", info);
        }
    }

    public void test_druid() throws Exception {
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
        dataSource.setTestOnBorrow(false);
        for (int i = 0; i < (loopCount); ++i) {
            p0(dataSource, "druid", threadCount);
        }
        System.out.println();
    }

    public void test_jobss() throws Exception {
        LocalTxDataSourceDO dataSourceDO = new LocalTxDataSourceDO();
        dataSourceDO.setBlockingTimeoutMillis((1000 * 60));
        dataSourceDO.setMaxPoolSize(maxPoolSize);
        dataSourceDO.setMinPoolSize(minPoolSize);
        dataSourceDO.setDriverClass(driverClass);
        dataSourceDO.setConnectionURL(jdbcUrl);
        dataSourceDO.setUserName(user);
        dataSourceDO.setPassword(password);
        LocalTxDataSource tx = TaobaoDataSourceFactory.createLocalTxDataSource(dataSourceDO);
        DataSource dataSource = tx.getDatasource();
        for (int i = 0; i < (loopCount); ++i) {
            p0(dataSource, "jboss-datasource", threadCount);
        }
        System.out.println();
    }

    public void test_dbcp() throws Exception {
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
        dataSource.setTestOnBorrow(false);
        for (int i = 0; i < (loopCount); ++i) {
            p0(dataSource, "dbcp", threadCount);
        }
        System.out.println();
    }

    public void test_bonecp() throws Exception {
        BoneCPDataSource dataSource = new BoneCPDataSource();
        // dataSource.(10);
        // dataSource.setMaxActive(50);
        dataSource.setMinConnectionsPerPartition(minPoolSize);
        dataSource.setMaxConnectionsPerPartition(maxPoolSize);
        dataSource.setDriverClass(driverClass);
        dataSource.setJdbcUrl(jdbcUrl);
        dataSource.setStatementsCacheSize(100);
        dataSource.setServiceOrder("LIFO");
        // dataSource.setMaxOpenPreparedStatements(100);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        // dataSource.setConnectionTestStatement("SELECT 1");
        dataSource.setPartitionCount(1);
        dataSource.setAcquireIncrement(5);
        dataSource.setIdleConnectionTestPeriod(0L);
        // dataSource.setDisableConnectionTracking(true);
        for (int i = 0; i < (loopCount); ++i) {
            p0(dataSource, "boneCP", threadCount);
        }
        System.out.println();
    }

    public void test_c3p0() throws Exception {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        // dataSource.(10);
        // dataSource.setMaxActive(50);
        dataSource.setMinPoolSize(minPoolSize);
        dataSource.setMaxPoolSize(maxPoolSize);
        dataSource.setDriverClass(driverClass);
        dataSource.setJdbcUrl(jdbcUrl);
        // dataSource.setPoolPreparedStatements(true);
        // dataSource.setMaxOpenPreparedStatements(100);
        dataSource.setUser(user);
        dataSource.setPassword(password);
        for (int i = 0; i < (loopCount); ++i) {
            p0(dataSource, "c3p0", threadCount);
        }
        System.out.println();
    }

    public void test_proxool() throws Exception {
        ProxoolDataSource dataSource = new ProxoolDataSource();
        // dataSource.(10);
        // dataSource.setMaxActive(50);
        dataSource.setMinimumConnectionCount(minPoolSize);
        dataSource.setMaximumConnectionCount(maxPoolSize);
        dataSource.setDriver(driverClass);
        dataSource.setDriverUrl(jdbcUrl);
        // dataSource.setPoolPreparedStatements(true);
        // dataSource.setMaxOpenPreparedStatements(100);
        dataSource.setUser(user);
        dataSource.setPassword(password);
        for (int i = 0; i < (loopCount); ++i) {
            p0(dataSource, "proxool", threadCount);
        }
        System.out.println();
    }

    public void test_tomcat_jdbc() throws Exception {
        org.apache.tomcat.jdbc.pool.DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource();
        // dataSource.(10);
        dataSource.setMaxIdle(maxPoolSize);
        dataSource.setMinIdle(minPoolSize);
        dataSource.setMaxActive(maxPoolSize);
        dataSource.setDriverClassName(driverClass);
        dataSource.setUrl(jdbcUrl);
        // dataSource.setPoolPreparedStatements(true);
        // dataSource.setMaxOpenPreparedStatements(100);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        for (int i = 0; i < (loopCount); ++i) {
            p0(dataSource, "tomcat-jdbc", threadCount);
        }
        System.out.println();
    }
}

