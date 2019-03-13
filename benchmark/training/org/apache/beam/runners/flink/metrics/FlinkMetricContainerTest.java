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
package org.apache.beam.runners.flink.metrics;


import DistributionResult.IDENTITY_ELEMENT;
import FlinkMetricContainer.FlinkGauge;
import MetricsApi.DistributionData;
import MonitoringInfoLabels.TRANSFORM_VALUE;
import org.apache.beam.model.pipeline.v1.MetricsApi.CounterData;
import org.apache.beam.model.pipeline.v1.MetricsApi.DoubleDistributionData;
import org.apache.beam.model.pipeline.v1.MetricsApi.IntDistributionData;
import org.apache.beam.model.pipeline.v1.MetricsApi.Metric;
import org.apache.beam.model.pipeline.v1.MetricsApi.MonitoringInfo;
import org.apache.beam.model.pipeline.v1.MetricsApi.MonitoringInfo.MonitoringInfoLabels;
import org.apache.beam.runners.core.metrics.SimpleMonitoringInfoBuilder;
import org.apache.beam.runners.flink.metrics.FlinkMetricContainer.FlinkDistributionGauge;
import org.apache.beam.sdk.metrics.Counter;
import org.apache.beam.sdk.metrics.Distribution;
import org.apache.beam.sdk.metrics.DistributionResult;
import org.apache.beam.sdk.metrics.Gauge;
import org.apache.beam.sdk.metrics.GaugeResult;
import org.apache.beam.sdk.metrics.MetricKey;
import org.apache.beam.sdk.metrics.MetricName;
import org.apache.beam.sdk.metrics.MetricsContainer;
import org.apache.beam.vendor.grpc.v1p13p1.com.google.common.collect.ImmutableList;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.metrics.MetricGroup;
import org.apache.flink.metrics.SimpleCounter;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Tests for {@link FlinkMetricContainer}.
 */
public class FlinkMetricContainerTest {
    @Mock
    private RuntimeContext runtimeContext;

    @Mock
    private MetricGroup metricGroup;

    static final String PTRANSFORM_LABEL = MonitoringInfoLabels.forNumber(TRANSFORM_VALUE).getValueDescriptor().getOptions().getExtension(labelProps).getName();

    @Test
    public void testMetricNameGeneration() {
        MetricKey key = MetricKey.create("step", MetricName.named("namespace", "name"));
        String name = FlinkMetricContainer.getFlinkMetricNameString(key);
        Assert.assertThat(name, CoreMatchers.is("namespace.name"));
    }

    @Test
    public void testCounter() {
        SimpleCounter flinkCounter = new SimpleCounter();
        Mockito.when(metricGroup.counter("namespace.name")).thenReturn(flinkCounter);
        FlinkMetricContainer container = new FlinkMetricContainer(runtimeContext);
        MetricsContainer step = container.getMetricsContainer("step");
        MetricName metricName = MetricName.named("namespace", "name");
        Counter counter = step.getCounter(metricName);
        counter.inc();
        counter.inc();
        Assert.assertThat(flinkCounter.getCount(), CoreMatchers.is(0L));
        container.updateMetrics("step");
        Assert.assertThat(flinkCounter.getCount(), CoreMatchers.is(2L));
    }

    @Test
    public void testGauge() {
        FlinkMetricContainer.FlinkGauge flinkGauge = new FlinkMetricContainer.FlinkGauge(GaugeResult.empty());
        Mockito.when(metricGroup.gauge(ArgumentMatchers.eq("namespace.name"), ArgumentMatchers.anyObject())).thenReturn(flinkGauge);
        FlinkMetricContainer container = new FlinkMetricContainer(runtimeContext);
        MetricsContainer step = container.getMetricsContainer("step");
        MetricName metricName = MetricName.named("namespace", "name");
        Gauge gauge = step.getGauge(metricName);
        Assert.assertThat(flinkGauge.getValue(), CoreMatchers.is(GaugeResult.empty()));
        // first set will install the mocked gauge
        container.updateMetrics("step");
        gauge.set(1);
        gauge.set(42);
        container.updateMetrics("step");
        Assert.assertThat(flinkGauge.getValue().getValue(), CoreMatchers.is(42L));
    }

    @Test
    public void testMonitoringInfoUpdate() {
        FlinkMetricContainer container = new FlinkMetricContainer(runtimeContext);
        MetricsContainer step = container.getMetricsContainer("step");
        SimpleCounter userCounter = new SimpleCounter();
        Mockito.when(metricGroup.counter("ns1.metric1")).thenReturn(userCounter);
        SimpleCounter elemCounter = new SimpleCounter();
        Mockito.when(metricGroup.counter("beam.metric:element_count:v1")).thenReturn(elemCounter);
        SimpleMonitoringInfoBuilder userCountBuilder = new SimpleMonitoringInfoBuilder();
        userCountBuilder.setUrnForUserMetric("ns1", "metric1");
        userCountBuilder.setInt64Value(111);
        MonitoringInfo userCountMonitoringInfo = userCountBuilder.build();
        Assert.assertNotNull(userCountMonitoringInfo);
        SimpleMonitoringInfoBuilder elemCountBuilder = new SimpleMonitoringInfoBuilder();
        elemCountBuilder.setUrn(ELEMENT_COUNT_URN);
        elemCountBuilder.setInt64Value(222);
        elemCountBuilder.setPTransformLabel("step");
        elemCountBuilder.setPCollectionLabel("pcoll");
        MonitoringInfo elemCountMonitoringInfo = elemCountBuilder.build();
        Assert.assertNotNull(elemCountMonitoringInfo);
        Assert.assertThat(userCounter.getCount(), CoreMatchers.is(0L));
        Assert.assertThat(elemCounter.getCount(), CoreMatchers.is(0L));
        container.updateMetrics("step", ImmutableList.of(userCountMonitoringInfo, elemCountMonitoringInfo));
        Assert.assertThat(userCounter.getCount(), CoreMatchers.is(111L));
        Assert.assertThat(elemCounter.getCount(), CoreMatchers.is(222L));
    }

    @Test
    public void testDropUnexpectedMonitoringInfoTypes() {
        FlinkMetricContainer flinkContainer = new FlinkMetricContainer(runtimeContext);
        MetricsContainer step = flinkContainer.getMetricsContainer("step");
        MonitoringInfo intCounter = MonitoringInfo.newBuilder().setUrn(((USER_COUNTER_URN_PREFIX) + "ns1:int_counter")).putLabels(FlinkMetricContainerTest.PTRANSFORM_LABEL, "step").setMetric(Metric.newBuilder().setCounterData(CounterData.newBuilder().setInt64Value(111))).build();
        MonitoringInfo doubleCounter = MonitoringInfo.newBuilder().setUrn(((USER_COUNTER_URN_PREFIX) + "ns2:double_counter")).putLabels(FlinkMetricContainerTest.PTRANSFORM_LABEL, "step").setMetric(Metric.newBuilder().setCounterData(CounterData.newBuilder().setDoubleValue(222))).build();
        MonitoringInfo intDistribution = MonitoringInfo.newBuilder().setUrn(((USER_COUNTER_URN_PREFIX) + "ns3:int_distribution")).putLabels(FlinkMetricContainerTest.PTRANSFORM_LABEL, "step").setMetric(Metric.newBuilder().setDistributionData(DistributionData.newBuilder().setIntDistributionData(IntDistributionData.newBuilder().setSum(30).setCount(10).setMin(1).setMax(5)))).build();
        MonitoringInfo doubleDistribution = MonitoringInfo.newBuilder().setUrn(((USER_COUNTER_URN_PREFIX) + "ns4:double_distribution")).putLabels(FlinkMetricContainerTest.PTRANSFORM_LABEL, "step").setMetric(Metric.newBuilder().setDistributionData(DistributionData.newBuilder().setDoubleDistributionData(DoubleDistributionData.newBuilder().setSum(30).setCount(10).setMin(1).setMax(5)))).build();
        // Mock out the counter that Flink returns; the distribution gets created by
        // FlinkMetricContainer, not by Flink itself, so we verify it in a different way below
        SimpleCounter counter = new SimpleCounter();
        Mockito.when(metricGroup.counter("ns1.int_counter")).thenReturn(counter);
        flinkContainer.updateMetrics("step", ImmutableList.of(intCounter, doubleCounter, intDistribution, doubleDistribution));
        // Flink's MetricGroup should only have asked for one counter (the integer-typed one) to be
        // created (the double-typed one is dropped currently)
        Mockito.verify(metricGroup).counter(ArgumentMatchers.eq("ns1.int_counter"));
        // Verify that the counter injected into flink has the right value
        Assert.assertThat(counter.getCount(), CoreMatchers.is(111L));
        // Verify the counter in the java SDK MetricsContainer
        long count = getCumulative();
        Assert.assertThat(count, CoreMatchers.is(111L));
        // The one Flink distribution that gets created is a FlinkDistributionGauge; here we verify its
        // initial (and in this test, final) value
        Mockito.verify(metricGroup).gauge(ArgumentMatchers.eq("ns3.int_distribution"), ArgumentMatchers.argThat(new ArgumentMatcher<FlinkDistributionGauge>() {
            @Override
            public boolean matches(Object argument) {
                DistributionResult actual = getValue();
                DistributionResult expected = DistributionResult.create(30, 10, 1, 5);
                return actual.equals(expected);
            }
        }));
        // Verify that the Java SDK MetricsContainer holds the same information
        org.apache.beam.runners.core.metrics.DistributionData distributionData = ((org.apache.beam.runners.core.metrics.DistributionCell) (step.getDistribution(MetricName.named("ns3", "int_distribution")))).getCumulative();
        Assert.assertThat(distributionData, CoreMatchers.is(org.apache.beam.runners.core.metrics.DistributionData.create(30, 10, 1, 5)));
    }

    @Test
    public void testDistribution() {
        FlinkMetricContainer.FlinkDistributionGauge flinkGauge = new FlinkMetricContainer.FlinkDistributionGauge(DistributionResult.IDENTITY_ELEMENT);
        Mockito.when(metricGroup.gauge(ArgumentMatchers.eq("namespace.name"), ArgumentMatchers.anyObject())).thenReturn(flinkGauge);
        FlinkMetricContainer container = new FlinkMetricContainer(runtimeContext);
        MetricsContainer step = container.getMetricsContainer("step");
        MetricName metricName = MetricName.named("namespace", "name");
        Distribution distribution = step.getDistribution(metricName);
        Assert.assertThat(flinkGauge.getValue(), CoreMatchers.is(IDENTITY_ELEMENT));
        // first set will install the mocked distribution
        container.updateMetrics("step");
        distribution.update(42);
        distribution.update((-23));
        distribution.update(0);
        distribution.update(1);
        container.updateMetrics("step");
        Assert.assertThat(flinkGauge.getValue().getMax(), CoreMatchers.is(42L));
        Assert.assertThat(flinkGauge.getValue().getMin(), CoreMatchers.is((-23L)));
        Assert.assertThat(flinkGauge.getValue().getCount(), CoreMatchers.is(4L));
        Assert.assertThat(flinkGauge.getValue().getSum(), CoreMatchers.is(20L));
        Assert.assertThat(flinkGauge.getValue().getMean(), CoreMatchers.is(5.0));
    }
}

