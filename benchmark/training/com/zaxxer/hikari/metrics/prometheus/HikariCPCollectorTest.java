/**
 * Copyright (C) 2013, 2014 Brett Wooldridge
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
package com.zaxxer.hikari.metrics.prometheus;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.mocks.StubConnection;
import com.zaxxer.hikari.pool.TestElf;
import com.zaxxer.hikari.util.UtilityElf;
import io.prometheus.client.CollectorRegistry;
import java.sql.Connection;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class HikariCPCollectorTest {
    private CollectorRegistry collectorRegistry;

    @Test
    public void noConnection() throws Exception {
        HikariConfig config = TestElf.newHikariConfig();
        config.setMinimumIdle(0);
        config.setMetricsTrackerFactory(new PrometheusMetricsTrackerFactory(this.collectorRegistry));
        config.setDataSourceClassName("com.zaxxer.hikari.mocks.StubDataSource");
        StubConnection.slowCreate = true;
        try (HikariDataSource ds = new HikariDataSource(config)) {
            Assert.assertThat(getValue("hikaricp_active_connections", "noConnection"), CoreMatchers.is(0.0));
            Assert.assertThat(getValue("hikaricp_idle_connections", "noConnection"), CoreMatchers.is(0.0));
            Assert.assertThat(getValue("hikaricp_pending_threads", "noConnection"), CoreMatchers.is(0.0));
            Assert.assertThat(getValue("hikaricp_connections", "noConnection"), CoreMatchers.is(0.0));
            Assert.assertThat(getValue("hikaricp_max_connections", "noConnection"), CoreMatchers.is(10.0));
            Assert.assertThat(getValue("hikaricp_min_connections", "noConnection"), CoreMatchers.is(0.0));
        } finally {
            StubConnection.slowCreate = false;
        }
    }

    @Test
    public void noConnectionWithoutPoolName() throws Exception {
        HikariConfig config = new HikariConfig();
        config.setMinimumIdle(0);
        config.setMetricsTrackerFactory(new PrometheusMetricsTrackerFactory(this.collectorRegistry));
        config.setDataSourceClassName("com.zaxxer.hikari.mocks.StubDataSource");
        StubConnection.slowCreate = true;
        try (HikariDataSource ds = new HikariDataSource(config)) {
            String poolName = ds.getHikariConfigMXBean().getPoolName();
            Assert.assertThat(getValue("hikaricp_active_connections", poolName), CoreMatchers.is(0.0));
            Assert.assertThat(getValue("hikaricp_idle_connections", poolName), CoreMatchers.is(0.0));
            Assert.assertThat(getValue("hikaricp_pending_threads", poolName), CoreMatchers.is(0.0));
            Assert.assertThat(getValue("hikaricp_connections", poolName), CoreMatchers.is(0.0));
            Assert.assertThat(getValue("hikaricp_max_connections", poolName), CoreMatchers.is(10.0));
            Assert.assertThat(getValue("hikaricp_min_connections", poolName), CoreMatchers.is(0.0));
        } finally {
            StubConnection.slowCreate = false;
        }
    }

    @Test
    public void connection1() throws Exception {
        HikariConfig config = TestElf.newHikariConfig();
        config.setMetricsTrackerFactory(new PrometheusMetricsTrackerFactory(this.collectorRegistry));
        config.setDataSourceClassName("com.zaxxer.hikari.mocks.StubDataSource");
        config.setMaximumPoolSize(1);
        StubConnection.slowCreate = true;
        try (HikariDataSource ds = new HikariDataSource(config);Connection connection1 = ds.getConnection()) {
            UtilityElf.quietlySleep(1000);
            Assert.assertThat(getValue("hikaricp_active_connections", "connection1"), CoreMatchers.is(1.0));
            Assert.assertThat(getValue("hikaricp_idle_connections", "connection1"), CoreMatchers.is(0.0));
            Assert.assertThat(getValue("hikaricp_pending_threads", "connection1"), CoreMatchers.is(0.0));
            Assert.assertThat(getValue("hikaricp_connections", "connection1"), CoreMatchers.is(1.0));
            Assert.assertThat(getValue("hikaricp_max_connections", "connection1"), CoreMatchers.is(1.0));
            Assert.assertThat(getValue("hikaricp_min_connections", "connection1"), CoreMatchers.is(1.0));
        } finally {
            StubConnection.slowCreate = false;
        }
    }

    @Test
    public void connectionClosed() throws Exception {
        HikariConfig config = TestElf.newHikariConfig();
        config.setMetricsTrackerFactory(new PrometheusMetricsTrackerFactory(this.collectorRegistry));
        config.setDataSourceClassName("com.zaxxer.hikari.mocks.StubDataSource");
        config.setMaximumPoolSize(1);
        StubConnection.slowCreate = true;
        try (HikariDataSource ds = new HikariDataSource(config)) {
            try (Connection connection1 = ds.getConnection()) {
                // close immediately
            }
            Assert.assertThat(getValue("hikaricp_active_connections", "connectionClosed"), CoreMatchers.is(0.0));
            Assert.assertThat(getValue("hikaricp_idle_connections", "connectionClosed"), CoreMatchers.is(1.0));
            Assert.assertThat(getValue("hikaricp_pending_threads", "connectionClosed"), CoreMatchers.is(0.0));
            Assert.assertThat(getValue("hikaricp_connections", "connectionClosed"), CoreMatchers.is(1.0));
            Assert.assertThat(getValue("hikaricp_max_connections", "connectionClosed"), CoreMatchers.is(1.0));
            Assert.assertThat(getValue("hikaricp_min_connections", "connectionClosed"), CoreMatchers.is(1.0));
        } finally {
            StubConnection.slowCreate = false;
        }
    }
}

