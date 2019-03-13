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
package org.apache.flink.metrics.prometheus;


import com.mashape.unirest.http.exceptions.UnirestException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.apache.flink.api.common.JobID;
import org.apache.flink.metrics.Counter;
import org.apache.flink.metrics.Gauge;
import org.apache.flink.metrics.Histogram;
import org.apache.flink.metrics.Meter;
import org.apache.flink.metrics.Metric;
import org.apache.flink.metrics.SimpleCounter;
import org.apache.flink.metrics.util.TestHistogram;
import org.apache.flink.metrics.util.TestMeter;
import org.apache.flink.runtime.metrics.MetricRegistryConfiguration;
import org.apache.flink.runtime.metrics.MetricRegistryImpl;
import org.apache.flink.runtime.metrics.groups.FrontMetricGroup;
import org.apache.flink.runtime.metrics.groups.TaskManagerJobMetricGroup;
import org.apache.flink.runtime.metrics.groups.TaskManagerMetricGroup;
import org.apache.flink.util.TestLogger;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Basic test for {@link PrometheusReporter}.
 */
public class PrometheusReporterTest extends TestLogger {
    private static final String HOST_NAME = "hostname";

    private static final String TASK_MANAGER = "tm";

    private static final String HELP_PREFIX = "# HELP ";

    private static final String TYPE_PREFIX = "# TYPE ";

    private static final String DIMENSIONS = ((("host=\"" + (PrometheusReporterTest.HOST_NAME)) + "\",tm_id=\"") + (PrometheusReporterTest.TASK_MANAGER)) + "\"";

    private static final String DEFAULT_LABELS = ("{" + (PrometheusReporterTest.DIMENSIONS)) + ",}";

    private static final String SCOPE_PREFIX = "flink_taskmanager_";

    private static final PrometheusReporterTest.PortRangeProvider portRangeProvider = new PrometheusReporterTest.PortRangeProvider();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private MetricRegistryImpl registry;

    private FrontMetricGroup<TaskManagerMetricGroup> metricGroup;

    private PrometheusReporter reporter;

    /**
     * {@link io.prometheus.client.Counter} may not decrease, so report {@link Counter} as {@link io.prometheus.client.Gauge}.
     *
     * @throws UnirestException
     * 		Might be thrown on HTTP problems.
     */
    @Test
    public void counterIsReportedAsPrometheusGauge() throws UnirestException {
        Counter testCounter = new SimpleCounter();
        testCounter.inc(7);
        assertThatGaugeIsExported(testCounter, "testCounter", "7.0");
    }

    @Test
    public void gaugeIsReportedAsPrometheusGauge() throws UnirestException {
        Gauge<Integer> testGauge = new Gauge<Integer>() {
            @Override
            public Integer getValue() {
                return 1;
            }
        };
        assertThatGaugeIsExported(testGauge, "testGauge", "1.0");
    }

    @Test
    public void nullGaugeDoesNotBreakReporter() throws UnirestException {
        Gauge<Integer> testGauge = new Gauge<Integer>() {
            @Override
            public Integer getValue() {
                return null;
            }
        };
        assertThatGaugeIsExported(testGauge, "testGauge", "0.0");
    }

    @Test
    public void meterRateIsReportedAsPrometheusGauge() throws UnirestException {
        Meter testMeter = new TestMeter();
        assertThatGaugeIsExported(testMeter, "testMeter", "5.0");
    }

    @Test
    public void histogramIsReportedAsPrometheusSummary() throws UnirestException {
        Histogram testHistogram = new TestHistogram();
        String histogramName = "testHistogram";
        String summaryName = (PrometheusReporterTest.SCOPE_PREFIX) + histogramName;
        String response = addMetricAndPollResponse(testHistogram, histogramName);
        Assert.assertThat(response, Matchers.containsString(((((((((((((((PrometheusReporterTest.HELP_PREFIX) + summaryName) + " ") + histogramName) + " (scope: taskmanager)\n") + (PrometheusReporterTest.TYPE_PREFIX)) + summaryName) + " summary") + "\n") + summaryName) + "_count") + (PrometheusReporterTest.DEFAULT_LABELS)) + " 1.0") + "\n")));
        for (String quantile : Arrays.asList("0.5", "0.75", "0.95", "0.98", "0.99", "0.999")) {
            Assert.assertThat(response, Matchers.containsString((((((((summaryName + "{") + (PrometheusReporterTest.DIMENSIONS)) + ",quantile=\"") + quantile) + "\",} ") + quantile) + "\n")));
        }
    }

    @Test
    public void metricIsRemovedWhenCollectorIsNotUnregisteredYet() throws UnirestException {
        TaskManagerMetricGroup tmMetricGroup = new TaskManagerMetricGroup(registry, PrometheusReporterTest.HOST_NAME, PrometheusReporterTest.TASK_MANAGER);
        String metricName = "metric";
        Counter metric1 = new SimpleCounter();
        FrontMetricGroup<TaskManagerJobMetricGroup> metricGroup1 = new FrontMetricGroup(0, new TaskManagerJobMetricGroup(registry, tmMetricGroup, JobID.generate(), "job_1"));
        reporter.notifyOfAddedMetric(metric1, metricName, metricGroup1);
        Counter metric2 = new SimpleCounter();
        FrontMetricGroup<TaskManagerJobMetricGroup> metricGroup2 = new FrontMetricGroup(0, new TaskManagerJobMetricGroup(registry, tmMetricGroup, JobID.generate(), "job_2"));
        reporter.notifyOfAddedMetric(metric2, metricName, metricGroup2);
        reporter.notifyOfRemovedMetric(metric1, metricName, metricGroup1);
        String response = PrometheusReporterTest.pollMetrics(reporter.getPort()).getBody();
        Assert.assertThat(response, Matchers.not(Matchers.containsString("job_1")));
    }

    @Test
    public void invalidCharactersAreReplacedWithUnderscore() {
        Assert.assertThat(PrometheusReporter.replaceInvalidChars(""), Matchers.equalTo(""));
        Assert.assertThat(PrometheusReporter.replaceInvalidChars("abc"), Matchers.equalTo("abc"));
        Assert.assertThat(PrometheusReporter.replaceInvalidChars("abc\""), Matchers.equalTo("abc_"));
        Assert.assertThat(PrometheusReporter.replaceInvalidChars("\"abc"), Matchers.equalTo("_abc"));
        Assert.assertThat(PrometheusReporter.replaceInvalidChars("\"abc\""), Matchers.equalTo("_abc_"));
        Assert.assertThat(PrometheusReporter.replaceInvalidChars("\"a\"b\"c\""), Matchers.equalTo("_a_b_c_"));
        Assert.assertThat(PrometheusReporter.replaceInvalidChars("\"\"\"\""), Matchers.equalTo("____"));
        Assert.assertThat(PrometheusReporter.replaceInvalidChars("    "), Matchers.equalTo("____"));
        Assert.assertThat(PrometheusReporter.replaceInvalidChars("\"ab ;(c)\'"), Matchers.equalTo("_ab___c__"));
        Assert.assertThat(PrometheusReporter.replaceInvalidChars("a b c"), Matchers.equalTo("a_b_c"));
        Assert.assertThat(PrometheusReporter.replaceInvalidChars("a b c "), Matchers.equalTo("a_b_c_"));
        Assert.assertThat(PrometheusReporter.replaceInvalidChars("a;b'c*"), Matchers.equalTo("a_b_c_"));
        Assert.assertThat(PrometheusReporter.replaceInvalidChars("a,=;:?'b,=;:?'c"), Matchers.equalTo("a___:__b___:__c"));
    }

    @Test
    public void doubleGaugeIsConvertedCorrectly() {
        Assert.assertThat(reporter.gaugeFrom(new Gauge<Double>() {
            @Override
            public Double getValue() {
                return 3.14;
            }
        }).get(), Matchers.equalTo(3.14));
    }

    @Test
    public void shortGaugeIsConvertedCorrectly() {
        Assert.assertThat(reporter.gaugeFrom(new Gauge<Short>() {
            @Override
            public Short getValue() {
                return 13;
            }
        }).get(), Matchers.equalTo(13.0));
    }

    @Test
    public void booleanGaugeIsConvertedCorrectly() {
        Assert.assertThat(reporter.gaugeFrom(new Gauge<Boolean>() {
            @Override
            public Boolean getValue() {
                return true;
            }
        }).get(), Matchers.equalTo(1.0));
    }

    /**
     * Prometheus only supports numbers, so report non-numeric gauges as 0.
     */
    @Test
    public void stringGaugeCannotBeConverted() {
        Assert.assertThat(reporter.gaugeFrom(new Gauge<String>() {
            @Override
            public String getValue() {
                return "I am not a number";
            }
        }).get(), Matchers.equalTo(0.0));
    }

    @Test
    public void registeringSameMetricTwiceDoesNotThrowException() {
        Counter counter = new SimpleCounter();
        counter.inc();
        String counterName = "testCounter";
        reporter.notifyOfAddedMetric(counter, counterName, metricGroup);
        reporter.notifyOfAddedMetric(counter, counterName, metricGroup);
    }

    @Test
    public void addingUnknownMetricTypeDoesNotThrowException() {
        class SomeMetricType implements Metric {}
        reporter.notifyOfAddedMetric(new SomeMetricType(), "name", metricGroup);
    }

    @Test
    public void cannotStartTwoReportersOnSamePort() throws Exception {
        final MetricRegistryImpl fixedPort1 = new MetricRegistryImpl(MetricRegistryConfiguration.fromConfiguration(PrometheusReporterTest.createConfigWithOneReporter("test1", PrometheusReporterTest.portRangeProvider.next())));
        Assert.assertThat(fixedPort1.getReporters(), Matchers.hasSize(1));
        PrometheusReporter firstReporter = ((PrometheusReporter) (fixedPort1.getReporters().get(0)));
        final MetricRegistryImpl fixedPort2 = new MetricRegistryImpl(MetricRegistryConfiguration.fromConfiguration(PrometheusReporterTest.createConfigWithOneReporter("test2", String.valueOf(firstReporter.getPort()))));
        Assert.assertThat(fixedPort2.getReporters(), Matchers.hasSize(0));
        fixedPort1.shutdown().get();
        fixedPort2.shutdown().get();
    }

    @Test
    public void canStartTwoReportersWhenUsingPortRange() throws Exception {
        String portRange = PrometheusReporterTest.portRangeProvider.next();
        final MetricRegistryImpl portRange1 = new MetricRegistryImpl(MetricRegistryConfiguration.fromConfiguration(PrometheusReporterTest.createConfigWithOneReporter("test1", portRange)));
        final MetricRegistryImpl portRange2 = new MetricRegistryImpl(MetricRegistryConfiguration.fromConfiguration(PrometheusReporterTest.createConfigWithOneReporter("test2", portRange)));
        Assert.assertThat(portRange1.getReporters(), Matchers.hasSize(1));
        Assert.assertThat(portRange2.getReporters(), Matchers.hasSize(1));
        portRange1.shutdown().get();
        portRange2.shutdown().get();
    }

    /**
     * Utility class providing distinct port ranges.
     */
    private static class PortRangeProvider implements Iterator<String> {
        private int base = 9000;

        @Override
        public boolean hasNext() {
            return (base) < 14000;// arbitrary limit that should be sufficient for test purposes

        }

        /**
         * Returns the next port range containing exactly 100 ports.
         *
         * @return next port range
         */
        public String next() {
            if (!(hasNext())) {
                throw new NoSuchElementException();
            }
            int lowEnd = base;
            int highEnd = (base) + 99;
            base += 100;
            return ((String.valueOf(lowEnd)) + "-") + (String.valueOf(highEnd));
        }
    }
}

