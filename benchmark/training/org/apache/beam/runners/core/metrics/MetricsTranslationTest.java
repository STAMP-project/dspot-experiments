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


import BeamFnApi.Metrics.User;
import BeamFnApi.Metrics.User.CounterData;
import BeamFnApi.Metrics.User.DistributionData;
import BeamFnApi.Metrics.User.MetricName;
import ImmutableMap.Builder;
import java.util.Collection;
import java.util.Map;
import org.apache.beam.model.fnexecution.v1.BeamFnApi;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableMap;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Tests for {@link MetricsTranslation}.
 */
@RunWith(Parameterized.class)
public class MetricsTranslationTest {
    // Transform names are arbitrary user-meaningful steps in processing
    private static final String TRANSFORM1 = "transform1";

    private static final String TRANSFORM2 = "transform2";

    private static final String TRANSFORM3 = "transform3";

    // Namespaces correspond to different contexts for a metric
    private static final String NAMESPACE1 = "fakeNamespace1";

    private static final String NAMESPACE2 = "fakeNamespace2";

    // Names are what is being measured
    private static final String COUNTER_NAME1 = "elements";

    private static final String COUNTER_NAME2 = "dropped";

    private static final String DISTRIBUTION_NAME1 = "someMillis";

    private static final String DISTRIBUTION_NAME2 = "otherMillis";

    private static final String GAUGE_NAME1 = "load";

    private static final String GAUGE_NAME2 = "memory";

    private static final MetricName COUNTER_METRIC1 = MetricName.newBuilder().setNamespace(MetricsTranslationTest.NAMESPACE1).setName(MetricsTranslationTest.COUNTER_NAME1).build();

    private static final MetricName COUNTER_METRIC2 = MetricName.newBuilder().setNamespace(MetricsTranslationTest.NAMESPACE1).setName(MetricsTranslationTest.COUNTER_NAME2).build();

    private static final MetricName DISTRIBUTION_METRIC1 = MetricName.newBuilder().setNamespace(MetricsTranslationTest.NAMESPACE2).setName(MetricsTranslationTest.DISTRIBUTION_NAME1).build();

    private static final MetricName DISTRIBUTION_METRIC2 = MetricName.newBuilder().setNamespace(MetricsTranslationTest.NAMESPACE2).setName(MetricsTranslationTest.DISTRIBUTION_NAME2).build();

    private static final MetricName GAUGE_METRIC1 = MetricName.newBuilder().setNamespace(MetricsTranslationTest.NAMESPACE1).setName(MetricsTranslationTest.GAUGE_NAME1).build();

    private static final MetricName GAUGE_METRIC2 = MetricName.newBuilder().setNamespace(MetricsTranslationTest.NAMESPACE2).setName(MetricsTranslationTest.GAUGE_NAME2).build();

    private static final User DISTRIBUTION1 = User.newBuilder().setMetricName(MetricsTranslationTest.DISTRIBUTION_METRIC1).setDistributionData(DistributionData.newBuilder().setCount(42).setSum(4839L).setMax(348L).setMin(12L)).build();

    private static final User DISTRIBUTION2 = User.newBuilder().setMetricName(MetricsTranslationTest.DISTRIBUTION_METRIC2).setDistributionData(DistributionData.newBuilder().setCount(3).setSum(49L).setMax(43L).setMin(1L)).build();

    private static final User COUNTER1 = User.newBuilder().setMetricName(MetricsTranslationTest.COUNTER_METRIC1).setCounterData(CounterData.newBuilder().setValue(92L)).build();

    private static final User COUNTER2 = User.newBuilder().setMetricName(MetricsTranslationTest.COUNTER_METRIC2).setCounterData(CounterData.newBuilder().setValue(0L)).build();

    private static final User GAUGE1 = User.newBuilder().setMetricName(MetricsTranslationTest.GAUGE_METRIC2).setCounterData(CounterData.newBuilder().setValue(56L)).build();

    private static final User GAUGE2 = User.newBuilder().setMetricName(MetricsTranslationTest.GAUGE_METRIC2).setCounterData(CounterData.newBuilder().setValue(3L)).build();

    @Parameterized.Parameter(0)
    public Map<String, Collection<BeamFnApi.Metrics.User>> fnMetrics;

    @Test
    public void testToFromProtoMetricUpdates() {
        Builder<String, Collection<BeamFnApi.Metrics.User>> result = ImmutableMap.builder();
        for (Map.Entry<String, Collection<BeamFnApi.Metrics.User>> entry : fnMetrics.entrySet()) {
            MetricUpdates updates = MetricsTranslation.metricUpdatesFromProto(entry.getKey(), entry.getValue());
            result.putAll(MetricsTranslation.metricUpdatesToProto(updates));
        }
        Map<String, Collection<BeamFnApi.Metrics.User>> backToProto = result.build();
        Assert.assertThat(backToProto.keySet(), Matchers.equalTo(fnMetrics.keySet()));
        for (String ptransformName : backToProto.keySet()) {
            Assert.assertThat(backToProto.get(ptransformName), Matchers.containsInAnyOrder(fnMetrics.get(ptransformName).toArray()));
        }
    }
}

