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


import ProcessBundleHandler.JAVA_SOURCE_URN;
import RunnerApi.FunctionSpec;
import RunnerApi.PTransform;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;
import org.apache.beam.fn.harness.PTransformRunnerFactory.Registrar;
import org.apache.beam.fn.harness.data.PCollectionConsumerRegistry;
import org.apache.beam.fn.harness.data.PTransformFunctionRegistry;
import org.apache.beam.model.pipeline.v1.RunnerApi;
import org.apache.beam.runners.core.metrics.ExecutionStateTracker;
import org.apache.beam.runners.core.metrics.MetricsContainerStepMap;
import org.apache.beam.sdk.fn.data.FnDataReceiver;
import org.apache.beam.sdk.io.BoundedSource;
import org.apache.beam.sdk.io.CountingSource;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.util.SerializableUtils;
import org.apache.beam.sdk.util.WindowedValue;
import org.apache.beam.vendor.grpc.v1p13p1.com.google.protobuf.ByteString;
import org.apache.beam.vendor.guava.v20_0.com.google.common.base.Suppliers;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Iterables;
import org.hamcrest.Matchers;
import org.hamcrest.collection.IsMapContaining;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;


/**
 * Tests for {@link BoundedSourceRunner}.
 */
@RunWith(JUnit4.class)
public class BoundedSourceRunnerTest {
    public static final String URN = "urn:org.apache.beam:source:java:0.1";

    @Test
    public void testRunReadLoopWithMultipleSources() throws Exception {
        List<WindowedValue<Long>> out1Values = new ArrayList<>();
        List<WindowedValue<Long>> out2Values = new ArrayList<>();
        Collection<FnDataReceiver<WindowedValue<Long>>> consumers = ImmutableList.of(out1Values::add, out2Values::add);
        BoundedSourceRunner<BoundedSource<Long>, Long> runner = new BoundedSourceRunner(PipelineOptionsFactory.create(), FunctionSpec.getDefaultInstance(), consumers);
        runner.runReadLoop(valueInGlobalWindow(CountingSource.upTo(2)));
        runner.runReadLoop(valueInGlobalWindow(CountingSource.upTo(1)));
        Assert.assertThat(out1Values, Matchers.contains(valueInGlobalWindow(0L), valueInGlobalWindow(1L), valueInGlobalWindow(0L)));
        Assert.assertThat(out2Values, Matchers.contains(valueInGlobalWindow(0L), valueInGlobalWindow(1L), valueInGlobalWindow(0L)));
    }

    @Test
    public void testRunReadLoopWithEmptySource() throws Exception {
        List<WindowedValue<Long>> outValues = new ArrayList<>();
        Collection<FnDataReceiver<WindowedValue<Long>>> consumers = ImmutableList.of(outValues::add);
        BoundedSourceRunner<BoundedSource<Long>, Long> runner = new BoundedSourceRunner(PipelineOptionsFactory.create(), FunctionSpec.getDefaultInstance(), consumers);
        runner.runReadLoop(valueInGlobalWindow(CountingSource.upTo(0)));
        Assert.assertThat(outValues, empty());
    }

    @Test
    public void testStart() throws Exception {
        List<WindowedValue<Long>> outValues = new ArrayList<>();
        Collection<FnDataReceiver<WindowedValue<Long>>> consumers = ImmutableList.of(outValues::add);
        ByteString encodedSource = ByteString.copyFrom(SerializableUtils.serializeToByteArray(CountingSource.upTo(3)));
        BoundedSourceRunner<BoundedSource<Long>, Long> runner = new BoundedSourceRunner(PipelineOptionsFactory.create(), FunctionSpec.newBuilder().setUrn(JAVA_SOURCE_URN).setPayload(encodedSource).build(), consumers);
        runner.start();
        Assert.assertThat(outValues, Matchers.contains(valueInGlobalWindow(0L), valueInGlobalWindow(1L), valueInGlobalWindow(2L)));
    }

    @Test
    public void testCreatingAndProcessingSourceFromFactory() throws Exception {
        List<WindowedValue<String>> outputValues = new ArrayList<>();
        MetricsContainerStepMap metricsContainerRegistry = new MetricsContainerStepMap();
        PCollectionConsumerRegistry consumers = new PCollectionConsumerRegistry(metricsContainerRegistry, Mockito.mock(ExecutionStateTracker.class));
        consumers.register("outputPC", "pTransformId", ((FnDataReceiver) ((FnDataReceiver<WindowedValue<String>>) (outputValues::add))));
        PTransformFunctionRegistry startFunctionRegistry = new PTransformFunctionRegistry(Mockito.mock(MetricsContainerStepMap.class), Mockito.mock(ExecutionStateTracker.class), "start");
        PTransformFunctionRegistry finishFunctionRegistry = new PTransformFunctionRegistry(Mockito.mock(MetricsContainerStepMap.class), Mockito.mock(ExecutionStateTracker.class), "finish");
        RunnerApi.FunctionSpec functionSpec = FunctionSpec.newBuilder().setUrn("urn:org.apache.beam:source:java:0.1").setPayload(ByteString.copyFrom(SerializableUtils.serializeToByteArray(CountingSource.upTo(3)))).build();
        RunnerApi.PTransform pTransform = PTransform.newBuilder().setSpec(functionSpec).putInputs("input", "inputPC").putOutputs("output", "outputPC").build();
        /* beamFnDataClient */
        /* beamFnStateClient */
        /* splitListener */
        new BoundedSourceRunner.Factory<>().createRunnerForPTransform(PipelineOptionsFactory.create(), null, null, "pTransformId", pTransform, Suppliers.ofInstance("57L")::get, Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap(), consumers, startFunctionRegistry, finishFunctionRegistry, null);
        // This is testing a deprecated way of running sources and should be removed
        // once all source definitions are instead propagated along the input edge.
        Iterables.getOnlyElement(startFunctionRegistry.getFunctions()).run();
        Assert.assertThat(outputValues, Matchers.contains(valueInGlobalWindow(0L), valueInGlobalWindow(1L), valueInGlobalWindow(2L)));
        outputValues.clear();
        // Check that when passing a source along as an input, the source is processed.
        Assert.assertThat(consumers.keySet(), Matchers.containsInAnyOrder("inputPC", "outputPC"));
        consumers.getMultiplexingConsumer("inputPC").accept(valueInGlobalWindow(CountingSource.upTo(2)));
        Assert.assertThat(outputValues, Matchers.contains(valueInGlobalWindow(0L), valueInGlobalWindow(1L)));
        Assert.assertThat(finishFunctionRegistry.getFunctions(), Matchers.empty());
    }

    @Test
    public void testRegistration() {
        for (Registrar registrar : ServiceLoader.load(Registrar.class)) {
            if (registrar instanceof BoundedSourceRunner.Registrar) {
                Assert.assertThat(registrar.getPTransformRunnerFactories(), IsMapContaining.hasKey(BoundedSourceRunnerTest.URN));
                return;
            }
        }
        Assert.fail("Expected registrar not found.");
    }
}

