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
import com.zaxxer.hikari.pool.TestElf;
import io.prometheus.client.CollectorRegistry;
import java.sql.Connection;
import java.sql.SQLTransientConnectionException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class PrometheusMetricsTrackerTest {
    private CollectorRegistry collectorRegistry;

    private static final String POOL_LABEL_NAME = "pool";

    private static final String QUANTILE_LABEL_NAME = "quantile";

    private static final String[] QUANTILE_LABEL_VALUES = new String[]{ "0.5", "0.95", "0.99" };

    @Test
    public void recordConnectionTimeout() throws Exception {
        HikariConfig config = TestElf.newHikariConfig();
        config.setMetricsTrackerFactory(new PrometheusMetricsTrackerFactory(collectorRegistry));
        config.setJdbcUrl("jdbc:h2:mem:");
        config.setMaximumPoolSize(2);
        config.setConnectionTimeout(250);
        String[] labelNames = new String[]{ PrometheusMetricsTrackerTest.POOL_LABEL_NAME };
        String[] labelValues = new String[]{ config.getPoolName() };
        try (HikariDataSource hikariDataSource = new HikariDataSource(config)) {
            try (Connection connection1 = hikariDataSource.getConnection();Connection connection2 = hikariDataSource.getConnection()) {
                try (Connection connection3 = hikariDataSource.getConnection()) {
                } catch (SQLTransientConnectionException ignored) {
                }
            }
            Double total = collectorRegistry.getSampleValue("hikaricp_connection_timeout_total", labelNames, labelValues);
            Assert.assertThat(total, CoreMatchers.is(1.0));
        }
    }

    @Test
    public void connectionAcquisitionMetrics() {
        checkSummaryMetricFamily("hikaricp_connection_acquired_nanos");
    }

    @Test
    public void connectionUsageMetrics() {
        checkSummaryMetricFamily("hikaricp_connection_usage_millis");
    }

    @Test
    public void connectionCreationMetrics() {
        checkSummaryMetricFamily("hikaricp_connection_creation_millis");
    }

    @Test
    public void testMultiplePoolName() throws Exception {
        String[] labelNames = new String[]{ PrometheusMetricsTrackerTest.POOL_LABEL_NAME };
        HikariConfig config = TestElf.newHikariConfig();
        config.setMetricsTrackerFactory(new PrometheusMetricsTrackerFactory(collectorRegistry));
        config.setPoolName("first");
        config.setJdbcUrl("jdbc:h2:mem:");
        config.setMaximumPoolSize(2);
        config.setConnectionTimeout(250);
        String[] labelValues1 = new String[]{ config.getPoolName() };
        try (HikariDataSource ignored = new HikariDataSource(config)) {
            Assert.assertThat(collectorRegistry.getSampleValue("hikaricp_connection_timeout_total", labelNames, labelValues1), CoreMatchers.is(0.0));
            CollectorRegistry collectorRegistry2 = new CollectorRegistry();
            HikariConfig config2 = TestElf.newHikariConfig();
            config2.setMetricsTrackerFactory(new PrometheusMetricsTrackerFactory(collectorRegistry2));
            config2.setPoolName("second");
            config2.setJdbcUrl("jdbc:h2:mem:");
            config2.setMaximumPoolSize(4);
            config2.setConnectionTimeout(250);
            String[] labelValues2 = new String[]{ config2.getPoolName() };
            try (HikariDataSource ignored2 = new HikariDataSource(config2)) {
                Assert.assertThat(collectorRegistry2.getSampleValue("hikaricp_connection_timeout_total", labelNames, labelValues2), CoreMatchers.is(0.0));
            }
        }
    }
}

