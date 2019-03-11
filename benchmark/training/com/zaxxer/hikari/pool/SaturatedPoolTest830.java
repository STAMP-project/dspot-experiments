/**
 * Copyright (C) 2017 Brett Wooldridge
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


import Level.DEBUG;
import Level.INFO;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.mocks.StubConnection;
import com.zaxxer.hikari.mocks.StubStatement;
import com.zaxxer.hikari.util.ClockSource;
import com.zaxxer.hikari.util.UtilityElf;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 *
 * @author Brett Wooldridge
 */
public class SaturatedPoolTest830 {
    private static final Logger LOGGER = LoggerFactory.getLogger(SaturatedPoolTest830.class);

    private static final int MAX_POOL_SIZE = 10;

    @Test
    public void saturatedPoolTest() throws Exception {
        HikariConfig config = TestElf.newHikariConfig();
        config.setMinimumIdle(5);
        config.setMaximumPoolSize(SaturatedPoolTest830.MAX_POOL_SIZE);
        config.setInitializationFailTimeout(Long.MAX_VALUE);
        config.setConnectionTimeout(1000);
        config.setDataSourceClassName("com.zaxxer.hikari.mocks.StubDataSource");
        StubConnection.slowCreate = true;
        StubStatement.setSimulatedQueryTime(1000);
        TestElf.setSlf4jLogLevel(HikariPool.class, DEBUG);
        System.setProperty("com.zaxxer.hikari.housekeeping.periodMs", "5000");
        final long start = ClockSource.currentTime();
        try (final HikariDataSource ds = new HikariDataSource(config)) {
            LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
            ThreadPoolExecutor threadPool = /* core */
            /* max */
            /* keepalive */
            new ThreadPoolExecutor(50, 50, 2, TimeUnit.SECONDS, queue, new ThreadPoolExecutor.CallerRunsPolicy());
            threadPool.allowCoreThreadTimeOut(true);
            AtomicInteger windowIndex = new AtomicInteger();
            boolean[] failureWindow = new boolean[100];
            Arrays.fill(failureWindow, true);
            // Initial saturation
            for (int i = 0; i < 50; i++) {
                threadPool.execute(() -> {
                    try (Connection conn = ds.getConnection();Statement stmt = conn.createStatement()) {
                        stmt.execute("SELECT bogus FROM imaginary");
                    } catch (SQLException e) {
                        SaturatedPoolTest830.LOGGER.info(e.getMessage());
                    }
                });
            }
            long sleep = 80;
            outer : while (true) {
                UtilityElf.quietlySleep(sleep);
                if (((ClockSource.elapsedMillis(start)) > (TimeUnit.SECONDS.toMillis(12))) && (sleep < 100)) {
                    sleep = 100;
                    SaturatedPoolTest830.LOGGER.warn("Switching to 100ms sleep");
                } else
                    if (((ClockSource.elapsedMillis(start)) > (TimeUnit.SECONDS.toMillis(6))) && (sleep < 90)) {
                        sleep = 90;
                        SaturatedPoolTest830.LOGGER.warn("Switching to 90ms sleep");
                    }

                threadPool.execute(() -> {
                    int ndx = (windowIndex.incrementAndGet()) % (failureWindow.length);
                    try (Connection conn = ds.getConnection();Statement stmt = conn.createStatement()) {
                        stmt.execute("SELECT bogus FROM imaginary");
                        failureWindow[ndx] = false;
                    } catch (SQLException e) {
                        SaturatedPoolTest830.LOGGER.info(e.getMessage());
                        failureWindow[ndx] = true;
                    }
                });
                for (int i = 0; i < (failureWindow.length); i++) {
                    if (failureWindow[i]) {
                        if (((ClockSource.elapsedMillis(start)) % ((TimeUnit.SECONDS.toMillis(1)) - sleep)) < sleep) {
                            SaturatedPoolTest830.LOGGER.info("Active threads {}, submissions per second {}, waiting threads {}", threadPool.getActiveCount(), ((TimeUnit.SECONDS.toMillis(1)) / sleep), TestElf.getPool(ds).getThreadsAwaitingConnection());
                        }
                        continue outer;
                    }
                }
                SaturatedPoolTest830.LOGGER.info("Timeouts have subsided.");
                SaturatedPoolTest830.LOGGER.info("Active threads {}, submissions per second {}, waiting threads {}", threadPool.getActiveCount(), ((TimeUnit.SECONDS.toMillis(1)) / sleep), TestElf.getPool(ds).getThreadsAwaitingConnection());
                break;
            } 
            SaturatedPoolTest830.LOGGER.info("Waiting for completion of {} active tasks.", threadPool.getActiveCount());
            while ((TestElf.getPool(ds).getActiveConnections()) > 0) {
                UtilityElf.quietlySleep(50);
            } 
            Assert.assertEquals("Rate not in balance at 10req/s", ((TimeUnit.SECONDS.toMillis(1)) / sleep), 10L);
        } finally {
            StubStatement.setSimulatedQueryTime(0);
            StubConnection.slowCreate = false;
            System.clearProperty("com.zaxxer.hikari.housekeeping.periodMs");
            TestElf.setSlf4jLogLevel(HikariPool.class, INFO);
        }
    }
}

