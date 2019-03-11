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
package org.apache.beam.runners.dataflow.worker;


import Kind.DISTRIBUTION;
import Kind.SUM;
import Origin.USER;
import com.google.api.services.dataflow.model.CounterMetadata;
import com.google.api.services.dataflow.model.CounterStructuredName;
import com.google.api.services.dataflow.model.CounterStructuredNameAndMetadata;
import com.google.api.services.dataflow.model.CounterUpdate;
import com.google.api.services.dataflow.model.DistributionUpdate;
import org.apache.beam.runners.dataflow.worker.counters.DataflowCounterUpdateExtractor;
import org.apache.beam.sdk.metrics.Distribution;
import org.apache.beam.sdk.metrics.MetricName;
import org.apache.beam.sdk.metrics.MetricsContainer;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link StreamingStepMetricsContainer}.
 */
@RunWith(JUnit4.class)
public class StreamingStepMetricsContainerTest {
    private MetricsContainerRegistry registry = StreamingStepMetricsContainer.createRegistry();

    private MetricsContainer c1 = registry.getContainer("s1");

    private MetricsContainer c2 = registry.getContainer("s2");

    private MetricName name1 = MetricName.named("ns", "name1");

    private MetricName name2 = MetricName.named("ns", "name2");

    @Test
    public void testDedupping() {
        Assert.assertThat(c1, Matchers.not(Matchers.sameInstance(c2)));
        Assert.assertThat(c1, Matchers.sameInstance(registry.getContainer("s1")));
    }

    @Test
    public void testCounterUpdateExtraction() {
        c1.getCounter(name1).inc(5);
        c2.getCounter(name1).inc(8);
        c2.getCounter(name2).inc(12);
        Iterable<CounterUpdate> updates = StreamingStepMetricsContainer.extractMetricUpdates(registry);
        Assert.assertThat(updates, Matchers.containsInAnyOrder(new CounterUpdate().setStructuredNameAndMetadata(new CounterStructuredNameAndMetadata().setName(new CounterStructuredName().setOrigin(USER.toString()).setOriginNamespace("ns").setName("name1").setOriginalStepName("s1")).setMetadata(new CounterMetadata().setKind(SUM.toString()))).setCumulative(false).setInteger(DataflowCounterUpdateExtractor.longToSplitInt(5)), new CounterUpdate().setStructuredNameAndMetadata(new CounterStructuredNameAndMetadata().setName(new CounterStructuredName().setOrigin(USER.toString()).setOriginNamespace("ns").setName("name1").setOriginalStepName("s2")).setMetadata(new CounterMetadata().setKind(SUM.toString()))).setCumulative(false).setInteger(DataflowCounterUpdateExtractor.longToSplitInt(8)), new CounterUpdate().setStructuredNameAndMetadata(new CounterStructuredNameAndMetadata().setName(new CounterStructuredName().setOrigin(USER.toString()).setOriginNamespace("ns").setName("name2").setOriginalStepName("s2")).setMetadata(new CounterMetadata().setKind("SUM"))).setCumulative(false).setInteger(DataflowCounterUpdateExtractor.longToSplitInt(12))));
        c2.getCounter(name1).inc(7);
        updates = StreamingStepMetricsContainer.extractMetricUpdates(registry);
        Assert.assertThat(updates, Matchers.containsInAnyOrder(new CounterUpdate().setStructuredNameAndMetadata(new CounterStructuredNameAndMetadata().setName(new CounterStructuredName().setOrigin(USER.toString()).setOriginNamespace("ns").setName("name1").setOriginalStepName("s2")).setMetadata(new CounterMetadata().setKind(SUM.toString()))).setCumulative(false).setInteger(DataflowCounterUpdateExtractor.longToSplitInt(7))));
    }

    @Test
    public void testDistributionUpdateExtraction() {
        Distribution distribution = c1.getDistribution(name1);
        distribution.update(5);
        distribution.update(6);
        distribution.update(7);
        Iterable<CounterUpdate> updates = StreamingStepMetricsContainer.extractMetricUpdates(registry);
        Assert.assertThat(updates, Matchers.containsInAnyOrder(new CounterUpdate().setStructuredNameAndMetadata(new CounterStructuredNameAndMetadata().setName(new CounterStructuredName().setOrigin(USER.toString()).setOriginNamespace("ns").setName("name1").setOriginalStepName("s1")).setMetadata(new CounterMetadata().setKind(DISTRIBUTION.toString()))).setCumulative(false).setDistribution(new DistributionUpdate().setCount(DataflowCounterUpdateExtractor.longToSplitInt(3)).setMax(DataflowCounterUpdateExtractor.longToSplitInt(7)).setMin(DataflowCounterUpdateExtractor.longToSplitInt(5)).setSum(DataflowCounterUpdateExtractor.longToSplitInt(18)))));
        c1.getDistribution(name1).update(3);
        updates = StreamingStepMetricsContainer.extractMetricUpdates(registry);
        Assert.assertThat(updates, Matchers.containsInAnyOrder(new CounterUpdate().setStructuredNameAndMetadata(new CounterStructuredNameAndMetadata().setName(new CounterStructuredName().setOrigin(USER.toString()).setOriginNamespace("ns").setName("name1").setOriginalStepName("s1")).setMetadata(new CounterMetadata().setKind(DISTRIBUTION.toString()))).setCumulative(false).setDistribution(new DistributionUpdate().setCount(DataflowCounterUpdateExtractor.longToSplitInt(1)).setMax(DataflowCounterUpdateExtractor.longToSplitInt(3)).setMin(DataflowCounterUpdateExtractor.longToSplitInt(3)).setSum(DataflowCounterUpdateExtractor.longToSplitInt(3)))));
    }
}

