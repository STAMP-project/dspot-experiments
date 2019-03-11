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


import GlobalWindow.INSTANCE;
import PaneInfo.NO_FIRING;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.beam.fn.harness.MapFnRunners.ValueMapFnFactory;
import org.apache.beam.fn.harness.data.PCollectionConsumerRegistry;
import org.apache.beam.fn.harness.data.PTransformFunctionRegistry;
import org.apache.beam.model.pipeline.v1.RunnerApi.PTransform;
import org.apache.beam.runners.core.metrics.ExecutionStateTracker;
import org.apache.beam.runners.core.metrics.MetricsContainerStepMap;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.windowing.IntervalWindow;
import org.apache.beam.sdk.util.WindowedValue;
import org.apache.beam.vendor.guava.v20_0.com.google.common.base.Suppliers;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableSet;
import org.hamcrest.Matchers;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;


/**
 * Tests for {@link MapFnRunners}.
 */
@RunWith(JUnit4.class)
public class MapFnRunnersTest {
    private static final String EXPECTED_ID = "pTransformId";

    private static final PTransform EXPECTED_PTRANSFORM = RunnerApi.PTransform.newBuilder().putInputs("input", "inputPC").putOutputs("output", "outputPC").build();

    @Test
    public void testValueOnlyMapping() throws Exception {
        List<WindowedValue<?>> outputConsumer = new ArrayList<>();
        MetricsContainerStepMap metricsContainerRegistry = new MetricsContainerStepMap();
        PCollectionConsumerRegistry consumers = new PCollectionConsumerRegistry(metricsContainerRegistry, Mockito.mock(ExecutionStateTracker.class));
        consumers.register("outputPC", MapFnRunnersTest.EXPECTED_ID, outputConsumer::add);
        PTransformFunctionRegistry startFunctionRegistry = new PTransformFunctionRegistry(metricsContainerRegistry, Mockito.mock(ExecutionStateTracker.class), "start");
        PTransformFunctionRegistry finishFunctionRegistry = new PTransformFunctionRegistry(metricsContainerRegistry, Mockito.mock(ExecutionStateTracker.class), "finish");
        ValueMapFnFactory<String, String> factory = ( ptId, pt) -> String::toUpperCase;
        /* beamFnDataClient */
        /* beamFnStateClient */
        /* splitListener */
        MapFnRunners.forValueMapFnFactory(factory).createRunnerForPTransform(PipelineOptionsFactory.create(), null, null, MapFnRunnersTest.EXPECTED_ID, MapFnRunnersTest.EXPECTED_PTRANSFORM, Suppliers.ofInstance("57L")::get, Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap(), consumers, startFunctionRegistry, finishFunctionRegistry, null);
        Assert.assertThat(startFunctionRegistry.getFunctions(), Matchers.empty());
        Assert.assertThat(finishFunctionRegistry.getFunctions(), Matchers.empty());
        Assert.assertThat(consumers.keySet(), Matchers.containsInAnyOrder("inputPC", "outputPC"));
        consumers.getMultiplexingConsumer("inputPC").accept(valueInGlobalWindow("abc"));
        Assert.assertThat(outputConsumer, Matchers.contains(valueInGlobalWindow("ABC")));
    }

    @Test
    public void testFullWindowedValueMapping() throws Exception {
        List<WindowedValue<?>> outputConsumer = new ArrayList<>();
        MetricsContainerStepMap metricsContainerRegistry = new MetricsContainerStepMap();
        PCollectionConsumerRegistry consumers = new PCollectionConsumerRegistry(metricsContainerRegistry, Mockito.mock(ExecutionStateTracker.class));
        consumers.register("outputPC", MapFnRunnersTest.EXPECTED_ID, outputConsumer::add);
        PTransformFunctionRegistry startFunctionRegistry = new PTransformFunctionRegistry(metricsContainerRegistry, Mockito.mock(ExecutionStateTracker.class), "start");
        PTransformFunctionRegistry finishFunctionRegistry = new PTransformFunctionRegistry(metricsContainerRegistry, Mockito.mock(ExecutionStateTracker.class), "finish");
        /* beamFnDataClient */
        /* beamFnStateClient */
        /* splitListener */
        MapFnRunners.forWindowedValueMapFnFactory(this::createMapFunctionForPTransform).createRunnerForPTransform(PipelineOptionsFactory.create(), null, null, MapFnRunnersTest.EXPECTED_ID, MapFnRunnersTest.EXPECTED_PTRANSFORM, Suppliers.ofInstance("57L")::get, Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap(), consumers, startFunctionRegistry, finishFunctionRegistry, null);
        Assert.assertThat(startFunctionRegistry.getFunctions(), Matchers.empty());
        Assert.assertThat(finishFunctionRegistry.getFunctions(), Matchers.empty());
        Assert.assertThat(consumers.keySet(), Matchers.containsInAnyOrder("inputPC", "outputPC"));
        consumers.getMultiplexingConsumer("inputPC").accept(valueInGlobalWindow("abc"));
        Assert.assertThat(outputConsumer, Matchers.contains(valueInGlobalWindow("ABC")));
    }

    @Test
    public void testFullWindowedValueMappingWithCompressedWindow() throws Exception {
        List<WindowedValue<?>> outputConsumer = new ArrayList<>();
        PCollectionConsumerRegistry consumers = new PCollectionConsumerRegistry(Mockito.mock(MetricsContainerStepMap.class), Mockito.mock(ExecutionStateTracker.class));
        consumers.register("outputPC", "pTransformId", outputConsumer::add);
        PTransformFunctionRegistry startFunctionRegistry = new PTransformFunctionRegistry(Mockito.mock(MetricsContainerStepMap.class), Mockito.mock(ExecutionStateTracker.class), "start");
        PTransformFunctionRegistry finishFunctionRegistry = new PTransformFunctionRegistry(Mockito.mock(MetricsContainerStepMap.class), Mockito.mock(ExecutionStateTracker.class), "finish");
        /* beamFnDataClient */
        /* beamFnStateClient */
        /* splitListener */
        MapFnRunners.forWindowedValueMapFnFactory(this::createMapFunctionForPTransform).createRunnerForPTransform(PipelineOptionsFactory.create(), null, null, MapFnRunnersTest.EXPECTED_ID, MapFnRunnersTest.EXPECTED_PTRANSFORM, Suppliers.ofInstance("57L")::get, Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap(), consumers, startFunctionRegistry, finishFunctionRegistry, null);
        Assert.assertThat(startFunctionRegistry.getFunctions(), Matchers.empty());
        Assert.assertThat(finishFunctionRegistry.getFunctions(), Matchers.empty());
        Assert.assertThat(consumers.keySet(), Matchers.containsInAnyOrder("inputPC", "outputPC"));
        IntervalWindow firstWindow = new IntervalWindow(new Instant(0L), Duration.standardMinutes(10L));
        IntervalWindow secondWindow = new IntervalWindow(new Instant((-10L)), Duration.standardSeconds(22L));
        consumers.getMultiplexingConsumer("inputPC").accept(WindowedValue.of("abc", new Instant(12), ImmutableSet.of(firstWindow, INSTANCE, secondWindow), NO_FIRING));
        Assert.assertThat(outputConsumer, Matchers.containsInAnyOrder(WindowedValue.timestampedValueInGlobalWindow("ABC", new Instant(12)), WindowedValue.of("ABC", new Instant(12), secondWindow, NO_FIRING), WindowedValue.of("ABC", new Instant(12), firstWindow, NO_FIRING)));
    }
}

