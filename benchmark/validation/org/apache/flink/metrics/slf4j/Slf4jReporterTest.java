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
package org.apache.flink.metrics.slf4j;


import org.apache.flink.metrics.Gauge;
import org.apache.flink.metrics.Histogram;
import org.apache.flink.metrics.Meter;
import org.apache.flink.metrics.MeterView;
import org.apache.flink.metrics.SimpleCounter;
import org.apache.flink.metrics.util.TestHistogram;
import org.apache.flink.runtime.metrics.MetricRegistryImpl;
import org.apache.flink.runtime.metrics.groups.TaskMetricGroup;
import org.apache.flink.util.TestLogger;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test for {@link Slf4jReporter}.
 */
public class Slf4jReporterTest extends TestLogger {
    private static final String HOST_NAME = "localhost";

    private static final String TASK_MANAGER_ID = "tm01";

    private static final String JOB_NAME = "jn01";

    private static final String TASK_NAME = "tn01";

    private static MetricRegistryImpl registry;

    private static char delimiter;

    private static TaskMetricGroup taskMetricGroup;

    private static Slf4jReporter reporter;

    @Test
    public void testAddCounter() throws Exception {
        String counterName = "simpleCounter";
        SimpleCounter counter = new SimpleCounter();
        Slf4jReporterTest.taskMetricGroup.counter(counterName, counter);
        Assert.assertTrue(Slf4jReporterTest.reporter.getCounters().containsKey(counter));
        String expectedCounterReport = (((((((Slf4jReporterTest.reporter.filterCharacters(Slf4jReporterTest.HOST_NAME)) + (Slf4jReporterTest.delimiter)) + (Slf4jReporterTest.reporter.filterCharacters(Slf4jReporterTest.TASK_MANAGER_ID))) + (Slf4jReporterTest.delimiter)) + (Slf4jReporterTest.reporter.filterCharacters(Slf4jReporterTest.JOB_NAME))) + (Slf4jReporterTest.delimiter)) + (Slf4jReporterTest.reporter.filterCharacters(counterName))) + ": 0";
        Slf4jReporterTest.reporter.report();
        TestUtils.checkForLogString(expectedCounterReport);
    }

    @Test
    public void testAddGauge() throws Exception {
        String gaugeName = "gauge";
        Slf4jReporterTest.taskMetricGroup.gauge(gaugeName, null);
        Assert.assertTrue(Slf4jReporterTest.reporter.getGauges().isEmpty());
        Gauge<Long> gauge = () -> null;
        Slf4jReporterTest.taskMetricGroup.gauge(gaugeName, gauge);
        Assert.assertTrue(Slf4jReporterTest.reporter.getGauges().containsKey(gauge));
        String expectedGaugeReport = (((((((Slf4jReporterTest.reporter.filterCharacters(Slf4jReporterTest.HOST_NAME)) + (Slf4jReporterTest.delimiter)) + (Slf4jReporterTest.reporter.filterCharacters(Slf4jReporterTest.TASK_MANAGER_ID))) + (Slf4jReporterTest.delimiter)) + (Slf4jReporterTest.reporter.filterCharacters(Slf4jReporterTest.JOB_NAME))) + (Slf4jReporterTest.delimiter)) + (Slf4jReporterTest.reporter.filterCharacters(gaugeName))) + ": null";
        Slf4jReporterTest.reporter.report();
        TestUtils.checkForLogString(expectedGaugeReport);
    }

    @Test
    public void testAddMeter() throws Exception {
        String meterName = "meter";
        Meter meter = Slf4jReporterTest.taskMetricGroup.meter(meterName, new MeterView(5));
        Assert.assertTrue(Slf4jReporterTest.reporter.getMeters().containsKey(meter));
        String expectedMeterReport = (((((((Slf4jReporterTest.reporter.filterCharacters(Slf4jReporterTest.HOST_NAME)) + (Slf4jReporterTest.delimiter)) + (Slf4jReporterTest.reporter.filterCharacters(Slf4jReporterTest.TASK_MANAGER_ID))) + (Slf4jReporterTest.delimiter)) + (Slf4jReporterTest.reporter.filterCharacters(Slf4jReporterTest.JOB_NAME))) + (Slf4jReporterTest.delimiter)) + (Slf4jReporterTest.reporter.filterCharacters(meterName))) + ": 0.0";
        Slf4jReporterTest.reporter.report();
        TestUtils.checkForLogString(expectedMeterReport);
    }

    @Test
    public void testAddHistogram() throws Exception {
        String histogramName = "histogram";
        Histogram histogram = Slf4jReporterTest.taskMetricGroup.histogram(histogramName, new TestHistogram());
        Assert.assertTrue(Slf4jReporterTest.reporter.getHistograms().containsKey(histogram));
        String expectedHistogramName = ((((((Slf4jReporterTest.reporter.filterCharacters(Slf4jReporterTest.HOST_NAME)) + (Slf4jReporterTest.delimiter)) + (Slf4jReporterTest.reporter.filterCharacters(Slf4jReporterTest.TASK_MANAGER_ID))) + (Slf4jReporterTest.delimiter)) + (Slf4jReporterTest.reporter.filterCharacters(Slf4jReporterTest.JOB_NAME))) + (Slf4jReporterTest.delimiter)) + (Slf4jReporterTest.reporter.filterCharacters(histogramName));
        Slf4jReporterTest.reporter.report();
        TestUtils.checkForLogString(expectedHistogramName);
    }

    @Test
    public void testFilterCharacters() throws Exception {
        Slf4jReporter reporter = new Slf4jReporter();
        Assert.assertThat(reporter.filterCharacters(""), Matchers.equalTo(""));
        Assert.assertThat(reporter.filterCharacters("abc"), Matchers.equalTo("abc"));
        Assert.assertThat(reporter.filterCharacters("a:b$%^::"), Matchers.equalTo("a:b$%^::"));
    }
}

