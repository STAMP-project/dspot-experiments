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
package org.apache.beam.runners.direct;


import java.util.concurrent.ExecutorService;
import org.apache.beam.runners.core.metrics.DistributionData;
import org.apache.beam.runners.core.metrics.GaugeData;
import org.apache.beam.runners.core.metrics.MetricUpdates;
import org.apache.beam.runners.core.metrics.MetricUpdates.MetricUpdate;
import org.apache.beam.sdk.metrics.DistributionResult;
import org.apache.beam.sdk.metrics.GaugeResult;
import org.apache.beam.sdk.metrics.MetricKey;
import org.apache.beam.sdk.metrics.MetricName;
import org.apache.beam.sdk.metrics.MetricQueryResults;
import org.apache.beam.sdk.metrics.MetricsFilter;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.hamcrest.Matchers;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;


/**
 * Tests for {@link DirectMetrics}.
 */
@RunWith(JUnit4.class)
public class DirectMetricsTest {
    @Mock
    private CommittedBundle<Object> bundle1;

    @Mock
    private CommittedBundle<Object> bundle2;

    private static final MetricName NAME1 = MetricName.named("ns1", "name1");

    private static final MetricName NAME2 = MetricName.named("ns1", "name2");

    private static final MetricName NAME3 = MetricName.named("ns2", "name1");

    private static final MetricName NAME4 = MetricName.named("ns2", "name2");

    private DirectMetrics metrics;

    private ExecutorService executor;

    @SuppressWarnings("unchecked")
    @Test
    public void testApplyCommittedNoFilter() {
        metrics.commitLogical(bundle1, MetricUpdates.create(ImmutableList.of(MetricUpdate.create(MetricKey.create("step1", DirectMetricsTest.NAME1), 5L), MetricUpdate.create(MetricKey.create("step1", DirectMetricsTest.NAME2), 8L)), ImmutableList.of(MetricUpdate.create(MetricKey.create("step1", DirectMetricsTest.NAME1), DistributionData.create(8, 2, 3, 5))), ImmutableList.of(MetricUpdate.create(MetricKey.create("step1", DirectMetricsTest.NAME4), GaugeData.create(15L)))));
        metrics.commitLogical(bundle1, MetricUpdates.create(ImmutableList.of(MetricUpdate.create(MetricKey.create("step2", DirectMetricsTest.NAME1), 7L), MetricUpdate.create(MetricKey.create("step1", DirectMetricsTest.NAME2), 4L)), ImmutableList.of(MetricUpdate.create(MetricKey.create("step1", DirectMetricsTest.NAME1), DistributionData.create(4, 1, 4, 4))), ImmutableList.of(MetricUpdate.create(MetricKey.create("step1", DirectMetricsTest.NAME4), GaugeData.create(27L)))));
        MetricQueryResults results = metrics.allMetrics();
        Assert.assertThat(results.getCounters(), Matchers.containsInAnyOrder(attemptedMetricsResult("ns1", "name1", "step1", 0L), attemptedMetricsResult("ns1", "name2", "step1", 0L), attemptedMetricsResult("ns1", "name1", "step2", 0L)));
        Assert.assertThat(results.getCounters(), Matchers.containsInAnyOrder(committedMetricsResult("ns1", "name1", "step1", 5L), committedMetricsResult("ns1", "name2", "step1", 12L), committedMetricsResult("ns1", "name1", "step2", 7L)));
        Assert.assertThat(results.getDistributions(), Matchers.contains(attemptedMetricsResult("ns1", "name1", "step1", DistributionResult.IDENTITY_ELEMENT)));
        Assert.assertThat(results.getDistributions(), Matchers.contains(committedMetricsResult("ns1", "name1", "step1", DistributionResult.create(12, 3, 3, 5))));
        Assert.assertThat(results.getGauges(), Matchers.contains(attemptedMetricsResult("ns2", "name2", "step1", GaugeResult.empty())));
        Assert.assertThat(results.getGauges(), Matchers.contains(committedMetricsResult("ns2", "name2", "step1", GaugeResult.create(27L, Instant.now()))));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testApplyAttemptedCountersQueryOneNamespace() {
        metrics.updatePhysical(bundle1, MetricUpdates.create(ImmutableList.of(MetricUpdate.create(MetricKey.create("step1", DirectMetricsTest.NAME1), 5L), MetricUpdate.create(MetricKey.create("step1", DirectMetricsTest.NAME3), 8L)), ImmutableList.of(), ImmutableList.of()));
        metrics.updatePhysical(bundle1, MetricUpdates.create(ImmutableList.of(MetricUpdate.create(MetricKey.create("step2", DirectMetricsTest.NAME1), 7L), MetricUpdate.create(MetricKey.create("step1", DirectMetricsTest.NAME3), 4L)), ImmutableList.of(), ImmutableList.of()));
        MetricQueryResults results = metrics.queryMetrics(MetricsFilter.builder().addNameFilter(inNamespace("ns1")).build());
        Assert.assertThat(results.getCounters(), Matchers.containsInAnyOrder(attemptedMetricsResult("ns1", "name1", "step1", 5L), attemptedMetricsResult("ns1", "name1", "step2", 7L)));
        Assert.assertThat(results.getCounters(), Matchers.containsInAnyOrder(committedMetricsResult("ns1", "name1", "step1", 0L), committedMetricsResult("ns1", "name1", "step2", 0L)));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testApplyAttemptedQueryCompositeScope() {
        metrics.updatePhysical(bundle1, MetricUpdates.create(ImmutableList.of(MetricUpdate.create(MetricKey.create("Outer1/Inner1", DirectMetricsTest.NAME1), 5L), MetricUpdate.create(MetricKey.create("Outer1/Inner2", DirectMetricsTest.NAME1), 8L)), ImmutableList.of(), ImmutableList.of()));
        metrics.updatePhysical(bundle1, MetricUpdates.create(ImmutableList.of(MetricUpdate.create(MetricKey.create("Outer1/Inner1", DirectMetricsTest.NAME1), 12L), MetricUpdate.create(MetricKey.create("Outer2/Inner2", DirectMetricsTest.NAME1), 18L)), ImmutableList.of(), ImmutableList.of()));
        MetricQueryResults results = metrics.queryMetrics(MetricsFilter.builder().addStep("Outer1").build());
        Assert.assertThat(results.getCounters(), Matchers.containsInAnyOrder(attemptedMetricsResult("ns1", "name1", "Outer1/Inner1", 12L), attemptedMetricsResult("ns1", "name1", "Outer1/Inner2", 8L)));
        Assert.assertThat(results.getCounters(), Matchers.containsInAnyOrder(committedMetricsResult("ns1", "name1", "Outer1/Inner1", 0L), committedMetricsResult("ns1", "name1", "Outer1/Inner2", 0L)));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testPartialScopeMatchingInMetricsQuery() {
        metrics.updatePhysical(bundle1, MetricUpdates.create(ImmutableList.of(MetricUpdate.create(MetricKey.create("Top1/Outer1/Inner1", DirectMetricsTest.NAME1), 5L), MetricUpdate.create(MetricKey.create("Top1/Outer1/Inner2", DirectMetricsTest.NAME1), 8L)), ImmutableList.of(), ImmutableList.of()));
        metrics.updatePhysical(bundle1, MetricUpdates.create(ImmutableList.of(MetricUpdate.create(MetricKey.create("Top2/Outer1/Inner1", DirectMetricsTest.NAME1), 12L), MetricUpdate.create(MetricKey.create("Top1/Outer2/Inner2", DirectMetricsTest.NAME1), 18L)), ImmutableList.of(), ImmutableList.of()));
        MetricQueryResults results = metrics.queryMetrics(MetricsFilter.builder().addStep("Top1/Outer1").build());
        Assert.assertThat(results.getCounters(), Matchers.containsInAnyOrder(attemptedMetricsResult("ns1", "name1", "Top1/Outer1/Inner1", 5L), attemptedMetricsResult("ns1", "name1", "Top1/Outer1/Inner2", 8L)));
        results = metrics.queryMetrics(MetricsFilter.builder().addStep("Inner2").build());
        Assert.assertThat(results.getCounters(), Matchers.containsInAnyOrder(attemptedMetricsResult("ns1", "name1", "Top1/Outer1/Inner2", 8L), attemptedMetricsResult("ns1", "name1", "Top1/Outer2/Inner2", 18L)));
    }
}

