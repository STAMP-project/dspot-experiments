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
package org.apache.beam.sdk.runners;


import org.apache.beam.sdk.PipelineResult;
import org.apache.beam.sdk.PipelineRunner;
import org.apache.beam.sdk.metrics.Counter;
import org.apache.beam.sdk.metrics.MetricNameFilter;
import org.apache.beam.sdk.metrics.MetricResultsMatchers;
import org.apache.beam.sdk.metrics.Metrics;
import org.apache.beam.sdk.metrics.MetricsFilter;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.testing.CrashingRunner;
import org.apache.beam.sdk.testing.NeedsRunner;
import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.transforms.SimpleFunction;
import org.apache.beam.sdk.values.PBegin;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.POutput;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link PipelineRunner}.
 */
@RunWith(JUnit4.class)
public class PipelineRunnerTest {
    @Test
    public void testInstantiation() {
        PipelineOptions options = PipelineOptionsFactory.create();
        options.setRunner(CrashingRunner.class);
        PipelineRunner<?> runner = PipelineRunner.fromOptions(options);
        Assert.assertTrue((runner instanceof CrashingRunner));
    }

    private static class ScaleFn<T extends Number> extends SimpleFunction<T, Double> {
        private final double scalar;

        private final Counter counter;

        public ScaleFn(double scalar, Counter counter) {
            this.scalar = scalar;
            this.counter = counter;
        }

        @Override
        public Double apply(T input) {
            counter.inc();
            return (scalar) * (input.doubleValue());
        }
    }

    @Test
    @Category(NeedsRunner.class)
    public void testRunPTransform() {
        final String namespace = PipelineRunnerTest.class.getName();
        final Counter counter = Metrics.counter(namespace, "count");
        final PipelineResult result = PipelineRunner.create().run(new org.apache.beam.sdk.transforms.PTransform<PBegin, POutput>() {
            @Override
            public POutput expand(PBegin input) {
                PCollection<Double> output = input.apply(org.apache.beam.sdk.transforms.Create.of(1, 2, 3, 4)).apply("ScaleByTwo", org.apache.beam.sdk.transforms.MapElements.via(new PipelineRunnerTest.ScaleFn(2.0, counter)));
                PAssert.that(output).containsInAnyOrder(2.0, 4.0, 6.0, 8.0);
                return output;
            }
        });
        // Checking counters to verify the pipeline actually ran.
        Assert.assertThat(result.metrics().queryMetrics(MetricsFilter.builder().addNameFilter(MetricNameFilter.inNamespace(namespace)).build()).getCounters(), Matchers.hasItem(MetricResultsMatchers.metricsResult(namespace, "count", "ScaleByTwo", 4L, true)));
    }
}

