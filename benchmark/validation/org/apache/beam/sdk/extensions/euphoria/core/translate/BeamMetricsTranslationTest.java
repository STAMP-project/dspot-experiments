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
package org.apache.beam.sdk.extensions.euphoria.core.translate;


import org.apache.beam.sdk.PipelineResult;
import org.apache.beam.sdk.metrics.MetricNameFilter;
import org.apache.beam.sdk.metrics.MetricQueryResults;
import org.apache.beam.sdk.metrics.MetricsFilter;
import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.TypeDescriptors;
import org.junit.Rule;
import org.junit.Test;


/**
 * Testing translation of accumulators to Beam {@link org.apache.beam.sdk.metrics.Metrics}.
 */
public class BeamMetricsTranslationTest {
    @Rule
    public TestPipeline testPipeline = TestPipeline.create();

    /**
     * Test metrics counters on {@link ReduceByKey} and {@link MapElements} operators Flow:
     *
     * <ol>
     *   <li>step RBK increment for all keys, add to histogram its value, collect even numbers.
     *   <li>step MapElements increment for every element, add to histogram its value, map to integer.
     *   <li>tep test MapElements with default operator name, increment by value of its element, add
     *       to histogram 2 times value of its element.
     * </ol>
     */
    @Test
    public void testBeamMetricsTranslation() {
        final PCollection<Integer> input = testPipeline.apply("input", Create.of(1, 2, 3, 4, 5).withType(TypeDescriptors.integers()));
        final String counterName1 = "counter1";
        final String operatorName1 = "count_elements_and_save_even_numbers";
        final PCollection<KV<Integer, Integer>> kvInput = org.apache.beam.sdk.extensions.euphoria.core.client.operator.ReduceByKey.named(operatorName1).of(input).keyBy(( e) -> e).reduceBy((java.util.stream.Stream<Integer> list,org.apache.beam.sdk.extensions.euphoria.core.client.io.Collector<Integer> coll) -> list.forEach(( i) -> {
            coll.getCounter(counterName1).increment();
            coll.getHistogram(counterName1).add(i);
            if ((i % 2) == 0) {
                coll.collect(i);
            }
        })).output();
        final String counterName2 = "counter2";
        final String operatorName2 = "map_to_integer";
        final String operatorName3 = "map_elements";
        final PCollection<Integer> mapElementsOutput = // kvInput = [<2,2>, <4,4>]
        org.apache.beam.sdk.extensions.euphoria.core.client.operator.MapElements.named(operatorName2).of(kvInput).using(( kv, context) -> {
            final Integer value = kv.getValue();
            context.getCounter(counterName2).increment();
            context.getHistogram(counterName2).add(value);
            return value;
        }).output();
        final PCollection<Integer> output = // mapElementsOutput = [2,4]
        org.apache.beam.sdk.extensions.euphoria.core.client.operator.MapElements.named(operatorName3).of(mapElementsOutput).using(( value, context) -> {
            context.getCounter(counterName2).increment(value);
            context.getHistogram(counterName2).add(value, 2);
            return value;
        }).output();
        PAssert.that(output).containsInAnyOrder(2, 4);
        final PipelineResult result = testPipeline.run();
        result.waitUntilFinish();
        final MetricQueryResults metricQueryResults = result.metrics().queryMetrics(MetricsFilter.builder().addNameFilter(MetricNameFilter.inNamespace(operatorName1)).addNameFilter(MetricNameFilter.inNamespace(operatorName2)).addNameFilter(MetricNameFilter.inNamespace(operatorName3)).build());
        testStep1Metrics(metricQueryResults, counterName1, operatorName1);
        testStep2Metrics(metricQueryResults, counterName2, operatorName2);
        testStep3WithDefaultOperatorName(metricQueryResults, counterName2, operatorName3);
    }
}

