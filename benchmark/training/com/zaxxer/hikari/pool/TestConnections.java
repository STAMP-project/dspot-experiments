/**
 * Copyright (C) 2013 Brett Wooldridge
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
package com.zaxxer.hikari.pool;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import com.zaxxer.hikari.mocks.StubConnection;
import com.zaxxer.hikari.mocks.StubDataSource;
import com.zaxxer.hikari.mocks.StubStatement;
import com.zaxxer.hikari.pool.HikariPool.PoolInitializationException;
import com.zaxxer.hikari.util.UtilityElf;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTransientConnectionException;
import java.sql.SQLTransientException;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Brett Wooldridge
 */
public class TestConnections {
    @Test
    public void testCreate() throws SQLException {
        HikariConfig config = TestElf.newHikariConfig();
        config.setMinimumIdle(1);
        config.setMaximumPoolSize(1);
        config.setConnectionTestQuery("VALUES 1");
        config.setConnectionInitSql("SELECT 1");
        config.setReadOnly(true);
        config.setConnectionTimeout(2500);
        config.setLeakDetectionThreshold(TimeUnit.SECONDS.toMillis(30));
        config.setDataSourceClassName("com.zaxxer.hikari.mocks.StubDataSource");
        try (HikariDataSource ds = new HikariDataSource(config)) {
            ds.setLoginTimeout(10);
            Assert.assertSame(10, ds.getLoginTimeout());
            HikariPool pool = TestElf.getPool(ds);
            ds.getConnection().close();
            Assert.assertSame("Total connections not as expected", 1, pool.getTotalConnections());
            Assert.assertSame("Idle connections not as expected", 1, pool.getIdleConnections());
            try (Connection connection = ds.getConnection();PreparedStatement statement = connection.prepareStatement("SELECT * FROM device WHERE device_id=?")) {
                Assert.assertNotNull(connection);
                Assert.assertNotNull(statement);
                Assert.assertSame("Total connections not as expected", 1, pool.getTotalConnections());
                Assert.assertSame("Idle connections not as expected", 0, pool.getIdleConnections());
                statement.setInt(1, 0);
                try (ResultSet resultSet = statement.executeQuery()) {
                    Assert.assertNotNull(resultSet);
                    Assert.assertFalse(resultSet.next());
                }
            }
            Assert.assertSame("Total connections not as expected", 1, pool.getTotalConnections());
            Assert.assertSame("Idle connections not as expected", 1, pool.getIdleConnections());
        }
    }

    @Test
    public void testMaxLifetime() throws Exception {
        HikariConfig config = TestElf.newHikariConfig();
        config.setMinimumIdle(0);
        config.setMaximumPoolSize(1);
        config.setConnectionTimeout(2500);
        config.setConnectionTestQuery("VALUES 1");
        config.setInitializationFailTimeout(Long.MAX_VALUE);
        config.setDataSourceClassName("com.zaxxer.hikari.mocks.StubDataSource");
        System.setProperty("com.zaxxer.hikari.housekeeping.periodMs", "100");
        TestElf.setConfigUnitTest(true);
        try (HikariDataSource ds = new HikariDataSource(config)) {
            System.clearProperty("com.zaxxer.hikari.housekeeping.periodMs");
            TestElf.getUnsealedConfig(ds).setMaxLifetime(700);
            HikariPool pool = TestElf.getPool(ds);
            Assert.assertSame("Total connections not as expected", 0, pool.getTotalConnections());
            Assert.assertSame("Idle connections not as expected", 0, pool.getIdleConnections());
            Connection unwrap;
            Connection unwrap2;
            try (Connection connection = ds.getConnection()) {
                unwrap = connection.unwrap(Connection.class);
                Assert.assertNotNull(connection);
                Assert.assertSame("Second total connections not as expected", 1, pool.getTotalConnections());
                Assert.assertSame("Second idle connections not as expected", 0, pool.getIdleConnections());
            }
            Assert.assertSame("Idle connections not as expected", 1, pool.getIdleConnections());
            try (Connection connection = ds.getConnection()) {
                unwrap2 = connection.unwrap(Connection.class);
                Assert.assertSame(unwrap, unwrap2);
                Assert.assertSame("Second total connections not as expected", 1, pool.getTotalConnections());
                Assert.assertSame("Second idle connections not as expected", 0, pool.getIdleConnections());
            }
            UtilityElf.quietlySleep(TimeUnit.SECONDS.toMillis(2));
            try (Connection connection = ds.getConnection()) {
                unwrap2 = connection.unwrap(Connection.class);
                Assert.assertNotSame("Expected a different connection", unwrap, unwrap2);
            }
            Assert.assertSame("Post total connections not as expected", 1, pool.getTotalConnections());
            Assert.assertSame("Post idle connections not as expected", 1, pool.getIdleConnections());
        } finally {
            TestElf.setConfigUnitTest(false);
        }
    }

    @Test
    public void testMaxLifetime2() throws Exception {
        HikariConfig config = TestElf.newHikariConfig();
        config.setMinimumIdle(0);
        config.setMaximumPoolSize(1);
        config.setConnectionTimeout(2500);
        config.setConnectionTestQuery("VALUES 1");
        config.setDataSourceClassName("com.zaxxer.hikari.mocks.StubDataSource");
        System.setProperty("com.zaxxer.hikari.housekeeping.periodMs", "100");
        TestElf.setConfigUnitTest(true);
        try (HikariDataSource ds = new HikariDataSource(config)) {
            TestElf.getUnsealedConfig(ds).setMaxLifetime(700);
            HikariPool pool = TestElf.getPool(ds);
            Assert.assertSame("Total connections not as expected", 0, pool.getTotalConnections());
            Assert.assertSame("Idle connections not as expected", 0, pool.getIdleConnections());
            Connection unwrap;
            Connection unwrap2;
            try (Connection connection = ds.getConnection()) {
                unwrap = connection.unwrap(Connection.class);
                Assert.assertNotNull(connection);
                Assert.assertSame("Second total connections not as expected", 1, pool.getTotalConnections());
                Assert.assertSame("Second idle connections not as expected", 0, pool.getIdleConnections());
            }
            Assert.assertSame("Idle connections not as expected", 1, pool.getIdleConnections());
            try (Connection connection = ds.getConnection()) {
                unwrap2 = connection.unwrap(Connection.class);
                Assert.assertSame(unwrap, unwrap2);
                Assert.assertSame("Second total connections not as expected", 1, pool.getTotalConnections());
                Assert.assertSame("Second idle connections not as expected", 0, pool.getIdleConnections());
            }
            UtilityElf.quietlySleep(800);
            try (Connection connection = ds.getConnection()) {
                unwrap2 = connection.unwrap(Connection.class);
                Assert.assertNotSame("Expected a different connection", unwrap, unwrap2);
            }
            Assert.assertSame("Post total connections not as expected", 1, pool.getTotalConnections());
            Assert.assertSame("Post idle connections not as expected", 1, pool.getIdleConnections());
        } finally {
            TestElf.setConfigUnitTest(false);
        }
    }

    @Test
    public void testDoubleClose() throws Exception {
        HikariConfig config = TestElf.newHikariConfig();
        config.setMinimumIdle(1);
        config.setMaximumPoolSize(1);
        config.setConnectionTimeout(2500);
        config.setConnectionTestQuery("VALUES 1");
        config.setDataSourceClassName("com.zaxxer.hikari.mocks.StubDataSource");
        try (HikariDataSource ds = new HikariDataSource(config);Connection connection = ds.getConnection()) {
            connection.close();
            // should no-op
            connection.abort(null);
            Assert.assertTrue("Connection should have closed", connection.isClosed());
            Assert.assertFalse("Connection should have closed", connection.isValid(5));
            Assert.assertTrue(("Expected to contain ClosedConnection, but was " + connection), connection.toString().contains("ClosedConnection"));
        }
    }

    @Test
    public void testEviction() throws Exception {
        HikariConfig config = TestElf.newHikariConfig();
        config.setMinimumIdle(0);
        config.setMaximumPoolSize(5);
        config.setConnectionTimeout(2500);
        config.setConnectionTestQuery("VALUES 1");
        config.setDataSourceClassName("com.zaxxer.hikari.mocks.StubDataSource");
        try (HikariDataSource ds = new HikariDataSource(config)) {
            Connection connection = ds.getConnection();
            HikariPool pool = TestElf.getPool(ds);
            Assert.assertEquals(1, pool.getTotalConnections());
            ds.evictConnection(connection);
            Assert.assertEquals(0, pool.getTotalConnections());
        }
    }

    @Test
    public void testEvictAllRefill() throws Exception {
        HikariConfig config = TestElf.newHikariConfig();
        config.setMinimumIdle(5);
        config.setMaximumPoolSize(10);
        config.setConnectionTimeout(2500);
        config.setConnectionTestQuery("VALUES 1");
        config.setDataSourceClassName("com.zaxxer.hikari.mocks.StubDataSource");
        System.setProperty("com.zaxxer.hikari.housekeeping.periodMs", "100");
        try (HikariDataSource ds = new HikariDataSource(config)) {
            HikariPoolMXBean poolMXBean = ds.getHikariPoolMXBean();
            while ((poolMXBean.getIdleConnections()) < 5) {
                // wait until the pool fills
                UtilityElf.quietlySleep(100);
            } 
            // Get and evict all the idle connections
            for (int i = 0; i < 5; i++) {
                final Connection conn = ds.getConnection();
                ds.evictConnection(conn);
            }
            Assert.assertTrue("Expected idle connections to be less than idle", ((poolMXBean.getIdleConnections()) < 5));
            // Wait a bit
            UtilityElf.quietlySleep(TimeUnit.SECONDS.toMillis(2));
            int count = 0;
            while (((poolMXBean.getIdleConnections()) < 5) && ((count++) < 20)) {
                UtilityElf.quietlySleep(100);
            } 
            // Assert that the pool as returned to 5 connections
            Assert.assertEquals("After eviction, refill did not reach expected 5 connections.", 5, poolMXBean.getIdleConnections());
        } finally {
            System.clearProperty("com.zaxxer.hikari.housekeeping.periodMs");
        }
    }

    @Test
    public void testBackfill() throws Exception {
        HikariConfig config = TestElf.newHikariConfig();
        config.setMinimumIdle(1);
        config.setMaximumPoolSize(4);
        config.setConnectionTimeout(1000);
        config.setInitializationFailTimeout(Long.MAX_VALUE);
        config.setConnectionTestQuery("VALUES 1");
        config.setDataSourceClassName("com.zaxxer.hikari.mocks.StubDataSource");
        StubConnection.slowCreate = true;
        try (HikariDataSource ds = new HikariDataSource(config)) {
            HikariPool pool = TestElf.getPool(ds);
            UtilityElf.quietlySleep(1250);
            Assert.assertSame("Total connections not as expected", 1, pool.getTotalConnections());
            Assert.assertSame("Idle connections not as expected", 1, pool.getIdleConnections());
            // This will take the pool down to zero
            try (Connection connection = ds.getConnection()) {
                Assert.assertNotNull(connection);
                Assert.assertSame("Total connections not as expected", 1, pool.getTotalConnections());
                Assert.assertSame("Idle connections not as expected", 0, pool.getIdleConnections());
                PreparedStatement statement = connection.prepareStatement("SELECT some, thing FROM somewhere WHERE something=?");
                Assert.assertNotNull(statement);
                ResultSet resultSet = statement.executeQuery();
                Assert.assertNotNull(resultSet);
                try {
                    statement.getMaxFieldSize();
                    Assert.fail();
                } catch (Exception e) {
                    Assert.assertSame(SQLException.class, e.getClass());
                }
                pool.logPoolState("testBackfill() before close...");
                // The connection will be ejected from the pool here
            }
            Assert.assertSame("Total connections not as expected", 0, pool.getTotalConnections());
            pool.logPoolState("testBackfill() after close...");
            UtilityElf.quietlySleep(1250);
            Assert.assertSame("Total connections not as expected", 1, pool.getTotalConnections());
            Assert.assertSame("Idle connections not as expected", 1, pool.getIdleConnections());
        } finally {
            StubConnection.slowCreate = false;
        }
    }

    @Test
    public void testMaximumPoolLimit() throws Exception {
        HikariConfig config = TestElf.newHikariConfig();
        config.setMinimumIdle(1);
        config.setMaximumPoolSize(4);
        config.setConnectionTimeout(20000);
        config.setInitializationFailTimeout(0);
        config.setConnectionTestQuery("VALUES 1");
        config.setDataSourceClassName("com.zaxxer.hikari.mocks.StubDataSource");
        final AtomicReference<Exception> ref = new AtomicReference<>();
        StubConnection.count.set(0);// reset counter

        try (final HikariDataSource ds = new HikariDataSource(config)) {
            final HikariPool pool = TestElf.getPool(ds);
            Thread[] threads = new Thread[20];
            for (int i = 0; i < (threads.length); i++) {
                threads[i] = new Thread(() -> {
                    try {
                        pool.logPoolState("Before acquire ");
                        try (Connection ignored = ds.getConnection()) {
                            pool.logPoolState("After  acquire ");
                            UtilityElf.quietlySleep(500);
                        }
                    } catch (Exception e) {
                        ref.set(e);
                    }
                });
            }
            for (Thread thread : threads) {
                thread.start();
            }
            for (Thread thread : threads) {
                thread.join();
            }
            pool.logPoolState("before check ");
            Assert.assertNull(((ref.get()) != null ? ref.get().toString() : ""), ref.get());
            Assert.assertSame("StubConnection count not as expected", 4, StubConnection.count.get());
        }
    }

    @Test
    @SuppressWarnings("EmptyTryBlock")
    public void testOldDriver() throws Exception {
        HikariConfig config = TestElf.newHikariConfig();
        config.setMinimumIdle(1);
        config.setMaximumPoolSize(1);
        config.setConnectionTimeout(2500);
        config.setConnectionTestQuery("VALUES 1");
        config.setDataSourceClassName("com.zaxxer.hikari.mocks.StubDataSource");
        StubConnection.oldDriver = true;
        StubStatement.oldDriver = true;
        try (HikariDataSource ds = new HikariDataSource(config)) {
            UtilityElf.quietlySleep(500);
            try (Connection ignored = ds.getConnection()) {
                // close
            }
            UtilityElf.quietlySleep(500);
            try (Connection ignored = ds.getConnection()) {
                // close
            }
        } finally {
            StubConnection.oldDriver = false;
            StubStatement.oldDriver = false;
        }
    }

    @Test
    public void testSuspendResume() throws Exception {
        HikariConfig config = TestElf.newHikariConfig();
        config.setMinimumIdle(3);
        config.setMaximumPoolSize(3);
        config.setConnectionTimeout(2500);
        config.setAllowPoolSuspension(true);
        config.setConnectionTestQuery("VALUES 1");
        config.setDataSourceClassName("com.zaxxer.hikari.mocks.StubDataSource");
        try (final HikariDataSource ds = new HikariDataSource(config)) {
            HikariPool pool = TestElf.getPool(ds);
            while ((pool.getTotalConnections()) < 3) {
                UtilityElf.quietlySleep(50);
            } 
            Thread t = new Thread(() -> {
                try {
                    ds.getConnection();
                    ds.getConnection();
                } catch (Exception e) {
                    Assert.fail();
                }
            });
            try (Connection ignored = ds.getConnection()) {
                Assert.assertEquals(2, pool.getIdleConnections());
                pool.suspendPool();
                t.start();
                UtilityElf.quietlySleep(500);
                Assert.assertEquals(2, pool.getIdleConnections());
            }
            Assert.assertEquals(3, pool.getIdleConnections());
            pool.resumePool();
            UtilityElf.quietlySleep(500);
            Assert.assertEquals(1, pool.getIdleConnections());
        }
    }

    @Test
    public void testSuspendResumeWithThrow() throws Exception {
        HikariConfig config = TestElf.newHikariConfig();
        config.setMinimumIdle(3);
        config.setMaximumPoolSize(3);
        config.setConnectionTimeout(2500);
        config.setAllowPoolSuspension(true);
        config.setConnectionTestQuery("VALUES 1");
        config.setDataSourceClassName("com.zaxxer.hikari.mocks.StubDataSource");
        System.setProperty("com.zaxxer.hikari.throwIfSuspended", "true");
        try (final HikariDataSource ds = new HikariDataSource(config)) {
            HikariPool pool = TestElf.getPool(ds);
            while ((pool.getTotalConnections()) < 3) {
                UtilityElf.quietlySleep(50);
            } 
            AtomicReference<Exception> exception = new AtomicReference<>();
            Thread t = new Thread(() -> {
                try {
                    ds.getConnection();
                    ds.getConnection();
                } catch (Exception e) {
                    exception.set(e);
                }
            });
            try (Connection ignored = ds.getConnection()) {
                Assert.assertEquals(2, pool.getIdleConnections());
                pool.suspendPool();
                t.start();
                UtilityElf.quietlySleep(500);
                Assert.assertEquals(SQLTransientException.class, exception.get().getClass());
                Assert.assertEquals(2, pool.getIdleConnections());
            }
            Assert.assertEquals(3, pool.getIdleConnections());
            pool.resumePool();
            try (Connection ignored = ds.getConnection()) {
                Assert.assertEquals(2, pool.getIdleConnections());
            }
        } finally {
            System.getProperties().remove("com.zaxxer.hikari.throwIfSuspended");
        }
    }

    @Test
    public void testInitializationFailure1() {
        StubDataSource stubDataSource = new StubDataSource();
        stubDataSource.setThrowException(new SQLException("Connection refused"));
        try (HikariDataSource ds = TestElf.newHikariDataSource()) {
            ds.setMinimumIdle(1);
            ds.setMaximumPoolSize(1);
            ds.setConnectionTimeout(2500);
            ds.setConnectionTestQuery("VALUES 1");
            ds.setDataSource(stubDataSource);
            try (Connection ignored = ds.getConnection()) {
                Assert.fail("Initialization should have failed");
            } catch (SQLException e) {
                // passed
            }
        }
    }

    @Test
    public void testInitializationFailure2() throws SQLException {
        StubDataSource stubDataSource = new StubDataSource();
        stubDataSource.setThrowException(new SQLException("Connection refused"));
        HikariConfig config = TestElf.newHikariConfig();
        config.setMinimumIdle(1);
        config.setConnectionTestQuery("VALUES 1");
        config.setDataSource(stubDataSource);
        try (HikariDataSource ds = new HikariDataSource(config);Connection ignored = ds.getConnection()) {
            Assert.fail("Initialization should have failed");
        } catch (PoolInitializationException e) {
            // passed
        }
    }

    @Test
    public void testInvalidConnectionTestQuery() {
        class BadConnection extends StubConnection {
            /**
             * {@inheritDoc }
             */
            @Override
            public Statement createStatement() throws SQLException {
                throw new SQLException("Simulated exception in createStatement()");
            }
        }
        StubDataSource stubDataSource = new StubDataSource() {
            /**
             * {@inheritDoc }
             */
            @Override
            public Connection getConnection() {
                return new BadConnection();
            }
        };
        HikariConfig config = TestElf.newHikariConfig();
        config.setMinimumIdle(1);
        config.setMaximumPoolSize(2);
        config.setConnectionTimeout(TimeUnit.SECONDS.toMillis(3));
        config.setConnectionTestQuery("VALUES 1");
        config.setInitializationFailTimeout(TimeUnit.SECONDS.toMillis(2));
        config.setDataSource(stubDataSource);
        try (HikariDataSource ds = new HikariDataSource(config)) {
            try (Connection ignored = ds.getConnection()) {
                Assert.fail("getConnection() should have failed");
            } catch (SQLException e) {
                Assert.assertSame("Simulated exception in createStatement()", e.getNextException().getMessage());
            }
        } catch (PoolInitializationException e) {
            Assert.assertSame("Simulated exception in createStatement()", e.getCause().getMessage());
        }
        config.setInitializationFailTimeout(0);
        try (HikariDataSource ignored = new HikariDataSource(config)) {
            Assert.fail("Initialization should have failed");
        } catch (PoolInitializationException e) {
            // passed
        }
    }

    @Test
    public void testDataSourceRaisesErrorWhileInitializationTestQuery() throws SQLException {
        TestConnections.StubDataSourceWithErrorSwitch stubDataSource = new TestConnections.StubDataSourceWithErrorSwitch();
        stubDataSource.setErrorOnConnection(true);
        HikariConfig config = TestElf.newHikariConfig();
        config.setMinimumIdle(1);
        config.setConnectionTestQuery("VALUES 1");
        config.setDataSource(stubDataSource);
        try (HikariDataSource ds = new HikariDataSource(config);Connection ignored = ds.getConnection()) {
            Assert.fail("Initialization should have failed");
        } catch (PoolInitializationException e) {
            // passed
        }
    }

    @Test
    public void testDataSourceRaisesErrorAfterInitializationTestQuery() {
        TestConnections.StubDataSourceWithErrorSwitch stubDataSource = new TestConnections.StubDataSourceWithErrorSwitch();
        HikariConfig config = TestElf.newHikariConfig();
        config.setMinimumIdle(0);
        config.setMaximumPoolSize(2);
        config.setConnectionTimeout(TimeUnit.SECONDS.toMillis(3));
        config.setConnectionTestQuery("VALUES 1");
        config.setInitializationFailTimeout(TimeUnit.SECONDS.toMillis(2));
        config.setDataSource(stubDataSource);
        try (HikariDataSource ds = new HikariDataSource(config)) {
            // this will make datasource throws Error, which will become uncaught
            stubDataSource.setErrorOnConnection(true);
            try (Connection ignored = ds.getConnection()) {
                Assert.fail("SQLException should occur!");
            } catch (SQLException e) {
                // request will get timed-out
                Assert.assertTrue(e.getMessage().contains("request timed out"));
            }
        }
    }

    @Test
    public void testPopulationSlowAcquisition() throws InterruptedException, SQLException {
        HikariConfig config = TestElf.newHikariConfig();
        config.setMaximumPoolSize(20);
        config.setConnectionTestQuery("VALUES 1");
        config.setDataSourceClassName("com.zaxxer.hikari.mocks.StubDataSource");
        System.setProperty("com.zaxxer.hikari.housekeeping.periodMs", "1000");
        StubConnection.slowCreate = true;
        try (HikariDataSource ds = new HikariDataSource(config)) {
            System.clearProperty("com.zaxxer.hikari.housekeeping.periodMs");
            TestElf.getUnsealedConfig(ds).setIdleTimeout(3000);
            TimeUnit.SECONDS.sleep(2);
            HikariPool pool = TestElf.getPool(ds);
            Assert.assertSame("Total connections not as expected", 2, pool.getTotalConnections());
            Assert.assertSame("Idle connections not as expected", 2, pool.getIdleConnections());
            try (Connection connection = ds.getConnection()) {
                Assert.assertNotNull(connection);
                TimeUnit.SECONDS.sleep(20);
                Assert.assertSame("Second total connections not as expected", 20, pool.getTotalConnections());
                Assert.assertSame("Second idle connections not as expected", 19, pool.getIdleConnections());
            }
            Assert.assertSame("Idle connections not as expected", 20, pool.getIdleConnections());
            TimeUnit.SECONDS.sleep(5);
            Assert.assertSame("Third total connections not as expected", 20, pool.getTotalConnections());
            Assert.assertSame("Third idle connections not as expected", 20, pool.getIdleConnections());
        } finally {
            StubConnection.slowCreate = false;
        }
    }

    @Test
    @SuppressWarnings("EmptyTryBlock")
    public void testMinimumIdleZero() throws SQLException {
        HikariConfig config = TestElf.newHikariConfig();
        config.setMinimumIdle(0);
        config.setMaximumPoolSize(5);
        config.setConnectionTimeout(1000L);
        config.setConnectionTestQuery("VALUES 1");
        config.setDataSourceClassName("com.zaxxer.hikari.mocks.StubDataSource");
        try (HikariDataSource ds = new HikariDataSource(config);Connection ignored = ds.getConnection()) {
            // passed
        } catch (SQLTransientConnectionException sqle) {
            Assert.fail("Failed to obtain connection");
        }
    }

    class StubDataSourceWithErrorSwitch extends StubDataSource {
        private boolean errorOnConnection = false;

        /**
         * {@inheritDoc }
         */
        @Override
        public Connection getConnection() throws SQLException {
            if (!(errorOnConnection)) {
                return new StubConnection();
            }
            throw new RuntimeException("Bad thing happens on datasource.");
        }

        public void setErrorOnConnection(boolean errorOnConnection) {
            this.errorOnConnection = errorOnConnection;
        }
    }
}

