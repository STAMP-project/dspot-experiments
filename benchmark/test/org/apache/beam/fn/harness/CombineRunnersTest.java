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
package org.apache.beam.fn.harness;


import RunnerApi.PTransform;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import org.apache.beam.fn.harness.data.PCollectionConsumerRegistry;
import org.apache.beam.fn.harness.data.PTransformFunctionRegistry;
import org.apache.beam.runners.core.metrics.ExecutionStateTracker;
import org.apache.beam.runners.core.metrics.MetricsContainerStepMap;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.fn.data.FnDataReceiver;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.Combine.CombineFn;
import org.apache.beam.sdk.util.WindowedValue;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Iterables;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;


/**
 * Tests for {@link CombineRunners}.
 */
@RunWith(JUnit4.class)
public class CombineRunnersTest {
    // CombineFn that converts strings to ints and sums them up to an accumulator, and negates the
    // value of the accumulator when extracting outputs. These operations are chosen to avoid
    // autoboxing and provide a way to easily check that certain combine steps actually executed.
    private static class TestCombineFn extends CombineFn<String, Integer, Integer> {
        @Override
        public Integer createAccumulator() {
            return 0;
        }

        @Override
        public Integer addInput(Integer accum, String input) {
            accum += Integer.parseInt(input);
            return accum;
        }

        @Override
        public Integer mergeAccumulators(Iterable<Integer> accums) {
            Integer merged = 0;
            for (Integer accum : accums) {
                merged += accum;
            }
            return merged;
        }

        @Override
        public Integer extractOutput(Integer accum) {
            return -accum;
        }
    }

    private static final String TEST_COMBINE_ID = "combineId";

    private PTransform pTransform;

    private String inputPCollectionId;

    private String outputPCollectionId;

    private Pipeline pProto;

    /**
     * Create a Precombine that is given keyed elements and validates that the outputted elements
     * values' are accumulators that were correctly derived from the input.
     */
    @Test
    public void testPrecombine() throws Exception {
        // Create a map of consumers and an output target to check output values.
        MetricsContainerStepMap metricsContainerRegistry = new MetricsContainerStepMap();
        PCollectionConsumerRegistry consumers = new PCollectionConsumerRegistry(metricsContainerRegistry, Mockito.mock(ExecutionStateTracker.class));
        Deque<WindowedValue<KV<String, Integer>>> mainOutputValues = new ArrayDeque<>();
        consumers.register(Iterables.getOnlyElement(pTransform.getOutputsMap().values()), CombineRunnersTest.TEST_COMBINE_ID, ((FnDataReceiver) ((FnDataReceiver<WindowedValue<KV<String, Integer>>>) (mainOutputValues::add))));
        PTransformFunctionRegistry startFunctionRegistry = new PTransformFunctionRegistry(Mockito.mock(MetricsContainerStepMap.class), Mockito.mock(ExecutionStateTracker.class), "start");
        PTransformFunctionRegistry finishFunctionRegistry = new PTransformFunctionRegistry(Mockito.mock(MetricsContainerStepMap.class), Mockito.mock(ExecutionStateTracker.class), "finish");
        // Create runner.
        new CombineRunners.PrecombineFactory<>().createRunnerForPTransform(PipelineOptionsFactory.create(), null, null, CombineRunnersTest.TEST_COMBINE_ID, pTransform, null, pProto.getComponents().getPcollectionsMap(), pProto.getComponents().getCodersMap(), pProto.getComponents().getWindowingStrategiesMap(), consumers, startFunctionRegistry, finishFunctionRegistry, null);
        Iterables.getOnlyElement(startFunctionRegistry.getFunctions()).run();
        // Send elements to runner and check outputs.
        mainOutputValues.clear();
        Assert.assertThat(consumers.keySet(), Matchers.containsInAnyOrder(inputPCollectionId, outputPCollectionId));
        FnDataReceiver<WindowedValue<?>> input = consumers.getMultiplexingConsumer(inputPCollectionId);
        input.accept(valueInGlobalWindow(KV.of("A", "1")));
        input.accept(valueInGlobalWindow(KV.of("A", "2")));
        input.accept(valueInGlobalWindow(KV.of("A", "6")));
        input.accept(valueInGlobalWindow(KV.of("B", "2")));
        input.accept(valueInGlobalWindow(KV.of("C", "3")));
        Iterables.getOnlyElement(finishFunctionRegistry.getFunctions()).run();
        // Check that all values for "A" were converted to accumulators regardless of how they were
        // combined by the Precombine optimization.
        Integer sum = 0;
        for (WindowedValue<KV<String, Integer>> outputValue : mainOutputValues) {
            if ("A".equals(outputValue.getValue().getKey())) {
                sum += outputValue.getValue().getValue();
            }
        }
        Assert.assertThat(sum, IsEqual.equalTo(9));
        // Check that elements for "B" and "C" are present as well.
        mainOutputValues.removeIf(( elem) -> "A".equals(elem.getValue().getKey()));
        Assert.assertThat(mainOutputValues, Matchers.containsInAnyOrder(valueInGlobalWindow(KV.of("B", 2)), valueInGlobalWindow(KV.of("C", 3))));
    }

    /**
     * Create a Merge Accumulators function that is given keyed lists of accumulators and validates
     * that the accumulators of each list were merged.
     */
    @Test
    public void testMergeAccumulators() throws Exception {
        // Create a map of consumers and an output target to check output values.
        MetricsContainerStepMap metricsContainerRegistry = new MetricsContainerStepMap();
        PCollectionConsumerRegistry consumers = new PCollectionConsumerRegistry(metricsContainerRegistry, Mockito.mock(ExecutionStateTracker.class));
        Deque<WindowedValue<KV<String, Integer>>> mainOutputValues = new ArrayDeque<>();
        consumers.register(Iterables.getOnlyElement(pTransform.getOutputsMap().values()), CombineRunnersTest.TEST_COMBINE_ID, ((FnDataReceiver) ((FnDataReceiver<WindowedValue<KV<String, Integer>>>) (mainOutputValues::add))));
        PTransformFunctionRegistry startFunctionRegistry = new PTransformFunctionRegistry(Mockito.mock(MetricsContainerStepMap.class), Mockito.mock(ExecutionStateTracker.class), "start");
        PTransformFunctionRegistry finishFunctionRegistry = new PTransformFunctionRegistry(Mockito.mock(MetricsContainerStepMap.class), Mockito.mock(ExecutionStateTracker.class), "finish");
        // Create runner.
        MapFnRunners.forValueMapFnFactory(CombineRunners::createMergeAccumulatorsMapFunction).createRunnerForPTransform(PipelineOptionsFactory.create(), null, null, CombineRunnersTest.TEST_COMBINE_ID, pTransform, null, Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap(), consumers, startFunctionRegistry, finishFunctionRegistry, null);
        Assert.assertThat(startFunctionRegistry.getFunctions(), Matchers.empty());
        Assert.assertThat(finishFunctionRegistry.getFunctions(), Matchers.empty());
        // Send elements to runner and check outputs.
        mainOutputValues.clear();
        Assert.assertThat(consumers.keySet(), Matchers.containsInAnyOrder(inputPCollectionId, outputPCollectionId));
        FnDataReceiver<WindowedValue<?>> input = consumers.getMultiplexingConsumer(inputPCollectionId);
        input.accept(valueInGlobalWindow(KV.of("A", Arrays.asList(1, 2, 6))));
        input.accept(valueInGlobalWindow(KV.of("B", Arrays.asList(2, 3))));
        input.accept(valueInGlobalWindow(KV.of("C", Arrays.asList(5, 2))));
        Assert.assertThat(mainOutputValues, Matchers.contains(valueInGlobalWindow(KV.of("A", 9)), valueInGlobalWindow(KV.of("B", 5)), valueInGlobalWindow(KV.of("C", 7))));
    }

    /**
     * Create an Extract Outputs function that is given keyed accumulators and validates that the
     * accumulators were turned into the output type.
     */
    @Test
    public void testExtractOutputs() throws Exception {
        // Create a map of consumers and an output target to check output values.
        MetricsContainerStepMap metricsContainerRegistry = new MetricsContainerStepMap();
        PCollectionConsumerRegistry consumers = new PCollectionConsumerRegistry(metricsContainerRegistry, Mockito.mock(ExecutionStateTracker.class));
        Deque<WindowedValue<KV<String, Integer>>> mainOutputValues = new ArrayDeque<>();
        consumers.register(Iterables.getOnlyElement(pTransform.getOutputsMap().values()), CombineRunnersTest.TEST_COMBINE_ID, ((FnDataReceiver) ((FnDataReceiver<WindowedValue<KV<String, Integer>>>) (mainOutputValues::add))));
        PTransformFunctionRegistry startFunctionRegistry = new PTransformFunctionRegistry(Mockito.mock(MetricsContainerStepMap.class), Mockito.mock(ExecutionStateTracker.class), "start");
        PTransformFunctionRegistry finishFunctionRegistry = new PTransformFunctionRegistry(Mockito.mock(MetricsContainerStepMap.class), Mockito.mock(ExecutionStateTracker.class), "finish");
        // Create runner.
        MapFnRunners.forValueMapFnFactory(CombineRunners::createExtractOutputsMapFunction).createRunnerForPTransform(PipelineOptionsFactory.create(), null, null, CombineRunnersTest.TEST_COMBINE_ID, pTransform, null, Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap(), consumers, startFunctionRegistry, finishFunctionRegistry, null);
        Assert.assertThat(startFunctionRegistry.getFunctions(), Matchers.empty());
        Assert.assertThat(finishFunctionRegistry.getFunctions(), Matchers.empty());
        // Send elements to runner and check outputs.
        mainOutputValues.clear();
        Assert.assertThat(consumers.keySet(), Matchers.containsInAnyOrder(inputPCollectionId, outputPCollectionId));
        FnDataReceiver<WindowedValue<?>> input = consumers.getMultiplexingConsumer(inputPCollectionId);
        input.accept(valueInGlobalWindow(KV.of("A", 9)));
        input.accept(valueInGlobalWindow(KV.of("B", 5)));
        input.accept(valueInGlobalWindow(KV.of("C", 7)));
        Assert.assertThat(mainOutputValues, Matchers.contains(valueInGlobalWindow(KV.of("A", (-9))), valueInGlobalWindow(KV.of("B", (-5))), valueInGlobalWindow(KV.of("C", (-7)))));
    }

    /**
     * Create a Combine Grouped Values function that is given lists of values that are grouped by key
     * and validates that the lists are properly combined.
     */
    @Test
    public void testCombineGroupedValues() throws Exception {
        // Create a map of consumers and an output target to check output values.
        MetricsContainerStepMap metricsContainerRegistry = new MetricsContainerStepMap();
        PCollectionConsumerRegistry consumers = new PCollectionConsumerRegistry(metricsContainerRegistry, Mockito.mock(ExecutionStateTracker.class));
        Deque<WindowedValue<KV<String, Integer>>> mainOutputValues = new ArrayDeque<>();
        consumers.register(Iterables.getOnlyElement(pTransform.getOutputsMap().values()), CombineRunnersTest.TEST_COMBINE_ID, ((FnDataReceiver) ((FnDataReceiver<WindowedValue<KV<String, Integer>>>) (mainOutputValues::add))));
        PTransformFunctionRegistry startFunctionRegistry = new PTransformFunctionRegistry(Mockito.mock(MetricsContainerStepMap.class), Mockito.mock(ExecutionStateTracker.class), "start");
        PTransformFunctionRegistry finishFunctionRegistry = new PTransformFunctionRegistry(Mockito.mock(MetricsContainerStepMap.class), Mockito.mock(ExecutionStateTracker.class), "finish");
        // Create runner.
        MapFnRunners.forValueMapFnFactory(CombineRunners::createCombineGroupedValuesMapFunction).createRunnerForPTransform(PipelineOptionsFactory.create(), null, null, CombineRunnersTest.TEST_COMBINE_ID, pTransform, null, Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap(), consumers, startFunctionRegistry, finishFunctionRegistry, null);
        Assert.assertThat(startFunctionRegistry.getFunctions(), Matchers.empty());
        Assert.assertThat(finishFunctionRegistry.getFunctions(), Matchers.empty());
        // Send elements to runner and check outputs.
        mainOutputValues.clear();
        Assert.assertThat(consumers.keySet(), Matchers.containsInAnyOrder(inputPCollectionId, outputPCollectionId));
        FnDataReceiver<WindowedValue<?>> input = consumers.getMultiplexingConsumer(inputPCollectionId);
        input.accept(valueInGlobalWindow(KV.of("A", Arrays.asList("1", "2", "6"))));
        input.accept(valueInGlobalWindow(KV.of("B", Arrays.asList("2", "3"))));
        input.accept(valueInGlobalWindow(KV.of("C", Arrays.asList("5", "2"))));
        Assert.assertThat(mainOutputValues, Matchers.contains(valueInGlobalWindow(KV.of("A", (-9))), valueInGlobalWindow(KV.of("B", (-5))), valueInGlobalWindow(KV.of("C", (-7)))));
    }
}

