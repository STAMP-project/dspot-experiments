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
package org.apache.beam.runners.core.metrics;


import DistributionResult.IDENTITY_ELEMENT;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.beam.model.pipeline.v1.MetricsApi.MonitoringInfo;
import org.apache.beam.sdk.metrics.Counter;
import org.apache.beam.sdk.metrics.Distribution;
import org.apache.beam.sdk.metrics.DistributionResult;
import org.apache.beam.sdk.metrics.Gauge;
import org.apache.beam.sdk.metrics.GaugeResult;
import org.apache.beam.sdk.metrics.MetricName;
import org.apache.beam.sdk.metrics.MetricQueryResults;
import org.apache.beam.sdk.metrics.MetricResults;
import org.apache.beam.sdk.metrics.Metrics;
import org.apache.beam.sdk.metrics.MetricsEnvironment;
import org.apache.beam.sdk.metrics.MetricsFilter;
import org.hamcrest.Matchers;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests for {@link MetricsContainerStepMap}.
 */
public class MetricsContainerStepMapTest {
    private static final Logger LOG = LoggerFactory.getLogger(MetricsContainerStepMapTest.class);

    private static final String NAMESPACE = MetricsContainerStepMapTest.class.getName();

    private static final String STEP1 = "myStep1";

    private static final String STEP2 = "myStep2";

    private static final String COUNTER_NAME = "myCounter";

    private static final String DISTRIBUTION_NAME = "myDistribution";

    private static final String GAUGE_NAME = "myGauge";

    private static final long VALUE = 100;

    private static final Counter counter = Metrics.counter(MetricsContainerStepMapTest.class, MetricsContainerStepMapTest.COUNTER_NAME);

    private static final Distribution distribution = Metrics.distribution(MetricsContainerStepMapTest.class, MetricsContainerStepMapTest.DISTRIBUTION_NAME);

    private static final Gauge gauge = Metrics.gauge(MetricsContainerStepMapTest.class, MetricsContainerStepMapTest.GAUGE_NAME);

    private static final MetricsContainerImpl metricsContainer;

    static {
        metricsContainer = new MetricsContainerImpl(null);
        try (Closeable ignored = MetricsEnvironment.scopedMetricsContainer(MetricsContainerStepMapTest.metricsContainer)) {
            MetricsContainerStepMapTest.counter.inc(MetricsContainerStepMapTest.VALUE);
            MetricsContainerStepMapTest.distribution.update(MetricsContainerStepMapTest.VALUE);
            MetricsContainerStepMapTest.distribution.update(((MetricsContainerStepMapTest.VALUE) * 2));
            MetricsContainerStepMapTest.gauge.set(MetricsContainerStepMapTest.VALUE);
        } catch (IOException e) {
            MetricsContainerStepMapTest.LOG.error(e.getMessage(), e);
        }
    }

    @Rule
    public transient ExpectedException thrown = ExpectedException.none();

    @Test
    public void testAttemptedAccumulatedMetricResults() {
        MetricsContainerStepMap attemptedMetrics = new MetricsContainerStepMap();
        attemptedMetrics.update(MetricsContainerStepMapTest.STEP1, MetricsContainerStepMapTest.metricsContainer);
        attemptedMetrics.update(MetricsContainerStepMapTest.STEP2, MetricsContainerStepMapTest.metricsContainer);
        attemptedMetrics.update(MetricsContainerStepMapTest.STEP2, MetricsContainerStepMapTest.metricsContainer);
        MetricResults metricResults = MetricsContainerStepMap.asAttemptedOnlyMetricResults(attemptedMetrics);
        MetricQueryResults step1res = metricResults.queryMetrics(MetricsFilter.builder().addStep(MetricsContainerStepMapTest.STEP1).build());
        assertIterableSize(step1res.getCounters(), 1);
        assertIterableSize(step1res.getDistributions(), 1);
        assertIterableSize(step1res.getGauges(), 1);
        assertCounter(MetricsContainerStepMapTest.COUNTER_NAME, step1res, MetricsContainerStepMapTest.STEP1, MetricsContainerStepMapTest.VALUE, false);
        assertDistribution(MetricsContainerStepMapTest.DISTRIBUTION_NAME, step1res, MetricsContainerStepMapTest.STEP1, DistributionResult.create(((MetricsContainerStepMapTest.VALUE) * 3), 2, MetricsContainerStepMapTest.VALUE, ((MetricsContainerStepMapTest.VALUE) * 2)), false);
        assertGauge(MetricsContainerStepMapTest.GAUGE_NAME, step1res, MetricsContainerStepMapTest.STEP1, GaugeResult.create(MetricsContainerStepMapTest.VALUE, Instant.now()), false);
        MetricQueryResults step2res = metricResults.queryMetrics(MetricsFilter.builder().addStep(MetricsContainerStepMapTest.STEP2).build());
        assertIterableSize(step2res.getCounters(), 1);
        assertIterableSize(step2res.getDistributions(), 1);
        assertIterableSize(step2res.getGauges(), 1);
        assertCounter(MetricsContainerStepMapTest.COUNTER_NAME, step2res, MetricsContainerStepMapTest.STEP2, ((MetricsContainerStepMapTest.VALUE) * 2), false);
        assertDistribution(MetricsContainerStepMapTest.DISTRIBUTION_NAME, step2res, MetricsContainerStepMapTest.STEP2, DistributionResult.create(((MetricsContainerStepMapTest.VALUE) * 6), 4, MetricsContainerStepMapTest.VALUE, ((MetricsContainerStepMapTest.VALUE) * 2)), false);
        assertGauge(MetricsContainerStepMapTest.GAUGE_NAME, step2res, MetricsContainerStepMapTest.STEP2, GaugeResult.create(MetricsContainerStepMapTest.VALUE, Instant.now()), false);
        MetricQueryResults allres = metricResults.allMetrics();
        assertIterableSize(allres.getCounters(), 2);
        assertIterableSize(allres.getDistributions(), 2);
        assertIterableSize(allres.getGauges(), 2);
    }

    @Test
    public void testCounterCommittedUnsupportedInAttemptedAccumulatedMetricResults() {
        MetricsContainerStepMap attemptedMetrics = new MetricsContainerStepMap();
        attemptedMetrics.update(MetricsContainerStepMapTest.STEP1, MetricsContainerStepMapTest.metricsContainer);
        MetricResults metricResults = MetricsContainerStepMap.asAttemptedOnlyMetricResults(attemptedMetrics);
        MetricQueryResults step1res = metricResults.queryMetrics(MetricsFilter.builder().addStep(MetricsContainerStepMapTest.STEP1).build());
        thrown.expect(UnsupportedOperationException.class);
        thrown.expectMessage("This runner does not currently support committed metrics results.");
        assertCounter(MetricsContainerStepMapTest.COUNTER_NAME, step1res, MetricsContainerStepMapTest.STEP1, MetricsContainerStepMapTest.VALUE, true);
    }

    @Test
    public void testDistributionCommittedUnsupportedInAttemptedAccumulatedMetricResults() {
        MetricsContainerStepMap attemptedMetrics = new MetricsContainerStepMap();
        attemptedMetrics.update(MetricsContainerStepMapTest.STEP1, MetricsContainerStepMapTest.metricsContainer);
        MetricResults metricResults = MetricsContainerStepMap.asAttemptedOnlyMetricResults(attemptedMetrics);
        MetricQueryResults step1res = metricResults.queryMetrics(MetricsFilter.builder().addStep(MetricsContainerStepMapTest.STEP1).build());
        thrown.expect(UnsupportedOperationException.class);
        thrown.expectMessage("This runner does not currently support committed metrics results.");
        assertDistribution(MetricsContainerStepMapTest.DISTRIBUTION_NAME, step1res, MetricsContainerStepMapTest.STEP1, IDENTITY_ELEMENT, true);
    }

    @Test
    public void testGaugeCommittedUnsupportedInAttemptedAccumulatedMetricResults() {
        MetricsContainerStepMap attemptedMetrics = new MetricsContainerStepMap();
        attemptedMetrics.update(MetricsContainerStepMapTest.STEP1, MetricsContainerStepMapTest.metricsContainer);
        MetricResults metricResults = MetricsContainerStepMap.asAttemptedOnlyMetricResults(attemptedMetrics);
        MetricQueryResults step1res = metricResults.queryMetrics(MetricsFilter.builder().addStep(MetricsContainerStepMapTest.STEP1).build());
        thrown.expect(UnsupportedOperationException.class);
        thrown.expectMessage("This runner does not currently support committed metrics results.");
        assertGauge(MetricsContainerStepMapTest.GAUGE_NAME, step1res, MetricsContainerStepMapTest.STEP1, GaugeResult.empty(), true);
    }

    @Test
    public void testUserMetricDroppedOnUnbounded() {
        MetricsContainerStepMap testObject = new MetricsContainerStepMap();
        CounterCell c1 = testObject.getUnboundContainer().getCounter(MetricName.named("ns", "name1"));
        c1.inc(5);
        List<MonitoringInfo> expected = new ArrayList<MonitoringInfo>();
        Assert.assertThat(testObject.getMonitoringInfos(), Matchers.containsInAnyOrder(expected.toArray()));
    }

    @Test
    public void testUpdateAllUpdatesUnboundedAndBoundedContainers() {
        MetricsContainerStepMap baseMetricContainerRegistry = new MetricsContainerStepMap();
        CounterCell c1 = baseMetricContainerRegistry.getContainer(MetricsContainerStepMapTest.STEP1).getCounter(MetricName.named("ns", "name1"));
        CounterCell c2 = baseMetricContainerRegistry.getUnboundContainer().getCounter(MonitoringInfoTestUtil.testElementCountName());
        c1.inc(7);
        c2.inc(14);
        MetricsContainerStepMap testObject = new MetricsContainerStepMap();
        testObject.updateAll(baseMetricContainerRegistry);
        List<MonitoringInfo> expected = new ArrayList<MonitoringInfo>();
        SimpleMonitoringInfoBuilder builder = new SimpleMonitoringInfoBuilder();
        builder.setUrnForUserMetric("ns", "name1");
        builder.setPTransformLabel(MetricsContainerStepMapTest.STEP1);
        builder.setInt64Value(7);
        expected.add(builder.build());
        expected.add(MonitoringInfoTestUtil.testElementCountMonitoringInfo(14));
        ArrayList<MonitoringInfo> actual = new ArrayList<MonitoringInfo>();
        for (MonitoringInfo mi : testObject.getMonitoringInfos()) {
            actual.add(SimpleMonitoringInfoBuilder.clearTimestamp(mi));
        }
        Assert.assertThat(actual, Matchers.containsInAnyOrder(expected.toArray()));
    }

    @Test
    public void testAttemptedAndCommittedAccumulatedMetricResults() {
        MetricsContainerStepMap attemptedMetrics = new MetricsContainerStepMap();
        attemptedMetrics.update(MetricsContainerStepMapTest.STEP1, MetricsContainerStepMapTest.metricsContainer);
        attemptedMetrics.update(MetricsContainerStepMapTest.STEP1, MetricsContainerStepMapTest.metricsContainer);
        attemptedMetrics.update(MetricsContainerStepMapTest.STEP2, MetricsContainerStepMapTest.metricsContainer);
        attemptedMetrics.update(MetricsContainerStepMapTest.STEP2, MetricsContainerStepMapTest.metricsContainer);
        attemptedMetrics.update(MetricsContainerStepMapTest.STEP2, MetricsContainerStepMapTest.metricsContainer);
        MetricsContainerStepMap committedMetrics = new MetricsContainerStepMap();
        committedMetrics.update(MetricsContainerStepMapTest.STEP1, MetricsContainerStepMapTest.metricsContainer);
        committedMetrics.update(MetricsContainerStepMapTest.STEP2, MetricsContainerStepMapTest.metricsContainer);
        committedMetrics.update(MetricsContainerStepMapTest.STEP2, MetricsContainerStepMapTest.metricsContainer);
        MetricResults metricResults = MetricsContainerStepMap.asMetricResults(attemptedMetrics, committedMetrics);
        MetricQueryResults step1res = metricResults.queryMetrics(MetricsFilter.builder().addStep(MetricsContainerStepMapTest.STEP1).build());
        assertIterableSize(step1res.getCounters(), 1);
        assertIterableSize(step1res.getDistributions(), 1);
        assertIterableSize(step1res.getGauges(), 1);
        assertCounter(MetricsContainerStepMapTest.COUNTER_NAME, step1res, MetricsContainerStepMapTest.STEP1, ((MetricsContainerStepMapTest.VALUE) * 2), false);
        assertDistribution(MetricsContainerStepMapTest.DISTRIBUTION_NAME, step1res, MetricsContainerStepMapTest.STEP1, DistributionResult.create(((MetricsContainerStepMapTest.VALUE) * 6), 4, MetricsContainerStepMapTest.VALUE, ((MetricsContainerStepMapTest.VALUE) * 2)), false);
        assertGauge(MetricsContainerStepMapTest.GAUGE_NAME, step1res, MetricsContainerStepMapTest.STEP1, GaugeResult.create(MetricsContainerStepMapTest.VALUE, Instant.now()), false);
        assertCounter(MetricsContainerStepMapTest.COUNTER_NAME, step1res, MetricsContainerStepMapTest.STEP1, MetricsContainerStepMapTest.VALUE, true);
        assertDistribution(MetricsContainerStepMapTest.DISTRIBUTION_NAME, step1res, MetricsContainerStepMapTest.STEP1, DistributionResult.create(((MetricsContainerStepMapTest.VALUE) * 3), 2, MetricsContainerStepMapTest.VALUE, ((MetricsContainerStepMapTest.VALUE) * 2)), true);
        assertGauge(MetricsContainerStepMapTest.GAUGE_NAME, step1res, MetricsContainerStepMapTest.STEP1, GaugeResult.create(MetricsContainerStepMapTest.VALUE, Instant.now()), true);
        MetricQueryResults step2res = metricResults.queryMetrics(MetricsFilter.builder().addStep(MetricsContainerStepMapTest.STEP2).build());
        assertIterableSize(step2res.getCounters(), 1);
        assertIterableSize(step2res.getDistributions(), 1);
        assertIterableSize(step2res.getGauges(), 1);
        assertCounter(MetricsContainerStepMapTest.COUNTER_NAME, step2res, MetricsContainerStepMapTest.STEP2, ((MetricsContainerStepMapTest.VALUE) * 3), false);
        assertDistribution(MetricsContainerStepMapTest.DISTRIBUTION_NAME, step2res, MetricsContainerStepMapTest.STEP2, DistributionResult.create(((MetricsContainerStepMapTest.VALUE) * 9), 6, MetricsContainerStepMapTest.VALUE, ((MetricsContainerStepMapTest.VALUE) * 2)), false);
        assertGauge(MetricsContainerStepMapTest.GAUGE_NAME, step2res, MetricsContainerStepMapTest.STEP2, GaugeResult.create(MetricsContainerStepMapTest.VALUE, Instant.now()), false);
        assertCounter(MetricsContainerStepMapTest.COUNTER_NAME, step2res, MetricsContainerStepMapTest.STEP2, ((MetricsContainerStepMapTest.VALUE) * 2), true);
        assertDistribution(MetricsContainerStepMapTest.DISTRIBUTION_NAME, step2res, MetricsContainerStepMapTest.STEP2, DistributionResult.create(((MetricsContainerStepMapTest.VALUE) * 6), 4, MetricsContainerStepMapTest.VALUE, ((MetricsContainerStepMapTest.VALUE) * 2)), true);
        assertGauge(MetricsContainerStepMapTest.GAUGE_NAME, step2res, MetricsContainerStepMapTest.STEP2, GaugeResult.create(MetricsContainerStepMapTest.VALUE, Instant.now()), true);
        MetricQueryResults allres = metricResults.queryMetrics(MetricsFilter.builder().build());
        assertIterableSize(allres.getCounters(), 2);
        assertIterableSize(allres.getDistributions(), 2);
        assertIterableSize(allres.getGauges(), 2);
    }
}

