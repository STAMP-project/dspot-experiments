/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.metrics.influxdb;


import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.apache.flink.metrics.Counter;
import org.apache.flink.metrics.reporter.MetricReporter;
import org.apache.flink.runtime.metrics.MetricRegistryImpl;
import org.apache.flink.util.TestLogger;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


/**
 * Integration test for {@link InfluxdbReporter}.
 */
public class InfluxdbReporterTest extends TestLogger {
    private static final String TEST_INFLUXDB_DB = "test-42";

    private static final String METRIC_HOSTNAME = "task-mgr-1";

    private static final String METRIC_TM_ID = "tm-id-123";

    @Rule
    public final WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort().notifier(new ConsoleNotifier(false)));

    @Test
    public void testReporterRegistration() throws Exception {
        MetricRegistryImpl metricRegistry = createMetricRegistry();
        try {
            Assert.assertEquals(1, metricRegistry.getReporters().size());
            MetricReporter reporter = metricRegistry.getReporters().get(0);
            Assert.assertTrue((reporter instanceof InfluxdbReporter));
        } finally {
            metricRegistry.shutdown().get();
        }
    }

    @Test
    public void testMetricRegistration() throws Exception {
        MetricRegistryImpl metricRegistry = createMetricRegistry();
        try {
            String metricName = "TestCounter";
            Counter counter = InfluxdbReporterTest.registerTestMetric(metricName, metricRegistry);
            InfluxdbReporter reporter = ((InfluxdbReporter) (metricRegistry.getReporters().get(0)));
            MeasurementInfo measurementInfo = reporter.counters.get(counter);
            Assert.assertNotNull("test metric must be registered in the reporter", measurementInfo);
            Assert.assertEquals(("taskmanager_" + metricName), measurementInfo.getName());
            Assert.assertThat(measurementInfo.getTags(), Matchers.hasEntry("host", InfluxdbReporterTest.METRIC_HOSTNAME));
            Assert.assertThat(measurementInfo.getTags(), Matchers.hasEntry("tm_id", InfluxdbReporterTest.METRIC_TM_ID));
        } finally {
            metricRegistry.shutdown().get();
        }
    }

    @Test
    public void testMetricReporting() throws Exception {
        MetricRegistryImpl metricRegistry = createMetricRegistry();
        try {
            String metricName = "TestCounter";
            Counter counter = InfluxdbReporterTest.registerTestMetric(metricName, metricRegistry);
            counter.inc(42);
            stubFor(post(urlPathEqualTo("/write")).willReturn(aResponse().withStatus(200)));
            InfluxdbReporter reporter = ((InfluxdbReporter) (metricRegistry.getReporters().get(0)));
            reporter.report();
            verify(postRequestedFor(urlPathEqualTo("/write")).withQueryParam("db", equalTo(InfluxdbReporterTest.TEST_INFLUXDB_DB)).withHeader("Content-Type", containing("text/plain")).withRequestBody(containing((((((("taskmanager_" + metricName) + ",host=") + (InfluxdbReporterTest.METRIC_HOSTNAME)) + ",tm_id=") + (InfluxdbReporterTest.METRIC_TM_ID)) + " count=42i"))));
        } finally {
            metricRegistry.shutdown().get();
        }
    }
}

