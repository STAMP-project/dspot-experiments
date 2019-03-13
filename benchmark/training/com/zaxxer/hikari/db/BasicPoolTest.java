/**
 * Copyright (C) 2016 Brett Wooldridge
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zaxxer.hikari.db;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool;
import com.zaxxer.hikari.pool.TestElf;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author brettw
 */
public class BasicPoolTest {
    @Test
    public void testIdleTimeout() throws InterruptedException, SQLException {
        HikariConfig config = TestElf.newHikariConfig();
        config.setMinimumIdle(5);
        config.setMaximumPoolSize(10);
        config.setConnectionTestQuery("SELECT 1");
        config.setDataSourceClassName("org.h2.jdbcx.JdbcDataSource");
        config.addDataSourceProperty("url", "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        System.setProperty("com.zaxxer.hikari.housekeeping.periodMs", "1000");
        try (HikariDataSource ds = new HikariDataSource(config)) {
            System.clearProperty("com.zaxxer.hikari.housekeeping.periodMs");
            TimeUnit.SECONDS.sleep(1);
            HikariPool pool = TestElf.getPool(ds);
            TestElf.getUnsealedConfig(ds).setIdleTimeout(3000);
            Assert.assertEquals("Total connections not as expected", 5, pool.getTotalConnections());
            Assert.assertEquals("Idle connections not as expected", 5, pool.getIdleConnections());
            try (Connection connection = ds.getConnection()) {
                Assert.assertNotNull(connection);
                TimeUnit.MILLISECONDS.sleep(1500);
                Assert.assertEquals("Second total connections not as expected", 6, pool.getTotalConnections());
                Assert.assertEquals("Second idle connections not as expected", 5, pool.getIdleConnections());
            }
            Assert.assertEquals("Idle connections not as expected", 6, pool.getIdleConnections());
            TimeUnit.SECONDS.sleep(2);
            Assert.assertEquals("Third total connections not as expected", 5, pool.getTotalConnections());
            Assert.assertEquals("Third idle connections not as expected", 5, pool.getIdleConnections());
        }
    }

    @Test
    public void testIdleTimeout2() throws InterruptedException, SQLException {
        HikariConfig config = TestElf.newHikariConfig();
        config.setMaximumPoolSize(50);
        config.setConnectionTestQuery("SELECT 1");
        config.setDataSourceClassName("org.h2.jdbcx.JdbcDataSource");
        config.addDataSourceProperty("url", "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        System.setProperty("com.zaxxer.hikari.housekeeping.periodMs", "1000");
        try (HikariDataSource ds = new HikariDataSource(config)) {
            System.clearProperty("com.zaxxer.hikari.housekeeping.periodMs");
            TimeUnit.SECONDS.sleep(1);
            HikariPool pool = TestElf.getPool(ds);
            TestElf.getUnsealedConfig(ds).setIdleTimeout(3000);
            Assert.assertEquals("Total connections not as expected", 50, pool.getTotalConnections());
            Assert.assertEquals("Idle connections not as expected", 50, pool.getIdleConnections());
            try (Connection connection = ds.getConnection()) {
                Assert.assertNotNull(connection);
                TimeUnit.MILLISECONDS.sleep(1500);
                Assert.assertEquals("Second total connections not as expected", 50, pool.getTotalConnections());
                Assert.assertEquals("Second idle connections not as expected", 49, pool.getIdleConnections());
            }
            Assert.assertEquals("Idle connections not as expected", 50, pool.getIdleConnections());
            TimeUnit.SECONDS.sleep(3);
            Assert.assertEquals("Third total connections not as expected", 50, pool.getTotalConnections());
            Assert.assertEquals("Third idle connections not as expected", 50, pool.getIdleConnections());
        }
    }
}

