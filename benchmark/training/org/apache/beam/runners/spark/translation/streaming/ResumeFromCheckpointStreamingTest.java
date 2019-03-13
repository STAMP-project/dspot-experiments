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
package org.apache.beam.runners.spark.translation.streaming;


import PAssert.FAILURE_COUNTER;
import PAssert.SUCCESS_COUNTER;
import java.io.Serializable;
import java.util.List;
import java.util.Properties;
import org.apache.beam.runners.spark.ReuseSparkContextRule;
import org.apache.beam.runners.spark.SparkPipelineResult;
import org.apache.beam.runners.spark.UsesCheckpointRecovery;
import org.apache.beam.runners.spark.translation.streaming.utils.EmbeddedKafkaCluster;
import org.apache.beam.sdk.metrics.Counter;
import org.apache.beam.sdk.metrics.MetricNameFilter;
import org.apache.beam.sdk.metrics.MetricResult;
import org.apache.beam.sdk.metrics.Metrics;
import org.apache.beam.sdk.metrics.MetricsFilter;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PCollectionView;
import org.apache.beam.sdk.values.PDone;
import org.apache.beam.vendor.guava.v20_0.com.google.common.base.Optional;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableMap;
import org.hamcrest.Matchers;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TemporaryFolder;


/**
 * Tests DStream recovery from checkpoint.
 *
 * <p>Runs the pipeline reading from a Kafka backlog with a WM function that will move to infinity
 * on a EOF signal. After resuming from checkpoint, a single output (guaranteed by the WM) is
 * asserted, along with {@link Metrics} values that are expected to resume from previous count and a
 * side-input that is expected to recover as well.
 */
public class ResumeFromCheckpointStreamingTest implements Serializable {
    private static final EmbeddedKafkaCluster.EmbeddedZookeeper EMBEDDED_ZOOKEEPER = new EmbeddedKafkaCluster.EmbeddedZookeeper();

    private static final EmbeddedKafkaCluster EMBEDDED_KAFKA_CLUSTER = new EmbeddedKafkaCluster(ResumeFromCheckpointStreamingTest.EMBEDDED_ZOOKEEPER.getConnection(), new Properties());

    private static final String TOPIC = "kafka_beam_test_topic";

    private transient TemporaryFolder temporaryFolder;

    @Rule
    public final transient ReuseSparkContextRule noContextReuse = ReuseSparkContextRule.no();

    @Test
    @Category(UsesCheckpointRecovery.class)
    public void testWithResume() throws Exception {
        // write to Kafka
        ResumeFromCheckpointStreamingTest.produce(ImmutableMap.of("k1", new Instant(100), "k2", new Instant(200), "k3", new Instant(300), "k4", new Instant(400)));
        MetricsFilter metricsFilter = MetricsFilter.builder().addNameFilter(MetricNameFilter.inNamespace(ResumeFromCheckpointStreamingTest.class)).build();
        // first run should expect EOT matching the last injected element.
        SparkPipelineResult res = run(Optional.of(new Instant(400)), 0);
        Assert.assertThat(res.metrics().queryMetrics(metricsFilter).getCounters(), Matchers.hasItem(attemptedMetricsResult(ResumeFromCheckpointStreamingTest.class.getName(), "allMessages", "EOFShallNotPassFn", 4L)));
        Assert.assertThat(res.metrics().queryMetrics(metricsFilter).getCounters(), Matchers.hasItem(attemptedMetricsResult(ResumeFromCheckpointStreamingTest.class.getName(), "processedMessages", "EOFShallNotPassFn", 4L)));
        // --- between executions:
        // - clear state.
        clean();
        // - write a bit more.
        ResumeFromCheckpointStreamingTest.produce(// to be dropped from [0, 500).
        ImmutableMap.of("k5", new Instant(499), "EOF", new Instant(500)));
        // recovery should resume from last read offset, and read the second batch of input.
        res = runAgain(1);
        // assertions 2:
        Assert.assertThat(res.metrics().queryMetrics(metricsFilter).getCounters(), Matchers.hasItem(attemptedMetricsResult(ResumeFromCheckpointStreamingTest.class.getName(), "processedMessages", "EOFShallNotPassFn", 5L)));
        Assert.assertThat(res.metrics().queryMetrics(metricsFilter).getCounters(), Matchers.hasItem(attemptedMetricsResult(ResumeFromCheckpointStreamingTest.class.getName(), "allMessages", "EOFShallNotPassFn", 6L)));
        long successAssertions = 0;
        Iterable<MetricResult<Long>> counterResults = res.metrics().queryMetrics(MetricsFilter.builder().addNameFilter(MetricNameFilter.named(ResumeFromCheckpointStreamingTest.PAssertWithoutFlatten.class, SUCCESS_COUNTER)).build()).getCounters();
        for (MetricResult<Long> counter : counterResults) {
            if ((counter.getAttempted()) > 0) {
                successAssertions++;
            }
        }
        Assert.assertThat(String.format("Expected %d successful assertions, but found %d.", 1L, successAssertions), successAssertions, Matchers.is(1L));
        // validate assertion didn't fail.
        long failedAssertions = 0;
        Iterable<MetricResult<Long>> failCounterResults = res.metrics().queryMetrics(MetricsFilter.builder().addNameFilter(MetricNameFilter.named(ResumeFromCheckpointStreamingTest.PAssertWithoutFlatten.class, FAILURE_COUNTER)).build()).getCounters();
        for (MetricResult<Long> counter : failCounterResults) {
            if ((counter.getAttempted()) > 0) {
                failedAssertions++;
            }
        }
        Assert.assertThat(String.format("Found %d failed assertions.", failedAssertions), failedAssertions, Matchers.is(0L));
    }

    /**
     * A pass-through fn that prevents EOF event from passing.
     */
    private static class EOFShallNotPassFn extends DoFn<String, String> {
        final PCollectionView<List<String>> view;

        private final Counter aggregator = Metrics.counter(ResumeFromCheckpointStreamingTest.class, "processedMessages");

        Counter counter = Metrics.counter(ResumeFromCheckpointStreamingTest.class, "allMessages");

        private EOFShallNotPassFn(PCollectionView<List<String>> view) {
            this.view = view;
        }

        @ProcessElement
        public void process(ProcessContext c) {
            String element = c.element();
            // assert that side input is passed correctly before/after resuming from checkpoint.
            Assert.assertThat(c.sideInput(view), Matchers.containsInAnyOrder("side1", "side2"));
            counter.inc();
            if (!("EOF".equals(element))) {
                aggregator.inc();
                c.output(c.element());
            }
        }
    }

    /**
     * A custom PAssert that avoids using {@link org.apache.beam.sdk.transforms.Flatten} until
     * BEAM-1444 is resolved.
     */
    private static class PAssertWithoutFlatten<T> extends PTransform<PCollection<Iterable<T>>, PDone> {
        private final T[] expected;

        private PAssertWithoutFlatten(T... expected) {
            this.expected = expected;
        }

        @Override
        public PDone expand(PCollection<Iterable<T>> input) {
            input.apply(ParDo.of(new ResumeFromCheckpointStreamingTest.PAssertWithoutFlatten.AssertDoFn(expected)));
            return PDone.in(input.getPipeline());
        }

        private static class AssertDoFn<T> extends DoFn<Iterable<T>, Void> {
            private final Counter success = Metrics.counter(ResumeFromCheckpointStreamingTest.PAssertWithoutFlatten.class, SUCCESS_COUNTER);

            private final Counter failure = Metrics.counter(ResumeFromCheckpointStreamingTest.PAssertWithoutFlatten.class, FAILURE_COUNTER);

            private final T[] expected;

            AssertDoFn(T[] expected) {
                this.expected = expected;
            }

            @ProcessElement
            public void processElement(ProcessContext c) throws Exception {
                try {
                    Assert.assertThat(c.element(), ResumeFromCheckpointStreamingTest.PAssertWithoutFlatten.AssertDoFn.containsInAnyOrder(expected));
                    success.inc();
                } catch (Throwable t) {
                    failure.inc();
                    throw t;
                }
            }
        }
    }
}

