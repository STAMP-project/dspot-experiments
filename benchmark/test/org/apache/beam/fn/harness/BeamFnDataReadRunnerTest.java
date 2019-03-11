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


import BeamFnApi.RemoteGrpcPort;
import BeamFnApi.Target;
import Endpoints.ApiServiceDescriptor;
import GlobalWindow.Coder.INSTANCE;
import RemoteGrpcPortRead.URN;
import RunnerApi.Components;
import RunnerApi.PCollection;
import RunnerApi.PTransform;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.beam.fn.harness.PTransformRunnerFactory.Registrar;
import org.apache.beam.fn.harness.data.BeamFnDataClient;
import org.apache.beam.fn.harness.data.MultiplexingFnDataReceiver;
import org.apache.beam.fn.harness.data.PCollectionConsumerRegistry;
import org.apache.beam.fn.harness.data.PTransformFunctionRegistry;
import org.apache.beam.model.pipeline.v1.RunnerApi;
import org.apache.beam.model.pipeline.v1.RunnerApi.MessageWithComponents;
import org.apache.beam.runners.core.construction.CoderTranslation;
import org.apache.beam.runners.core.metrics.ExecutionStateTracker;
import org.apache.beam.runners.core.metrics.MetricsContainerStepMap;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.fn.data.CompletableFutureInboundDataClient;
import org.apache.beam.sdk.fn.data.FnDataReceiver;
import org.apache.beam.sdk.fn.data.InboundDataClient;
import org.apache.beam.sdk.fn.data.LogicalEndpoint;
import org.apache.beam.sdk.fn.data.RemoteGrpcPortRead;
import org.apache.beam.sdk.fn.test.TestExecutors;
import org.apache.beam.sdk.fn.test.TestExecutors.TestExecutorService;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.util.WindowedValue;
import org.apache.beam.vendor.guava.v20_0.com.google.common.base.Suppliers;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableMap;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Iterables;
import org.apache.beam.vendor.guava.v20_0.com.google.common.util.concurrent.Uninterruptibles;
import org.hamcrest.Matchers;
import org.hamcrest.collection.IsMapContaining;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Tests for {@link BeamFnDataReadRunner}.
 */
@RunWith(JUnit4.class)
public class BeamFnDataReadRunnerTest {
    private static final Coder<String> ELEMENT_CODER = StringUtf8Coder.of();

    private static final String ELEMENT_CODER_SPEC_ID = "string-coder-id";

    private static final Coder<WindowedValue<String>> CODER = WindowedValue.getFullCoder(BeamFnDataReadRunnerTest.ELEMENT_CODER, INSTANCE);

    private static final String CODER_SPEC_ID = "windowed-string-coder-id";

    private static final Coder CODER_SPEC;

    private static final Components COMPONENTS;

    private static final RemoteGrpcPort PORT_SPEC = RemoteGrpcPort.newBuilder().setApiServiceDescriptor(ApiServiceDescriptor.getDefaultInstance()).setCoderId(BeamFnDataReadRunnerTest.CODER_SPEC_ID).build();

    static {
        try {
            MessageWithComponents coderAndComponents = CoderTranslation.toProto(BeamFnDataReadRunnerTest.CODER);
            CODER_SPEC = coderAndComponents.getCoder();
            COMPONENTS = coderAndComponents.getComponents().toBuilder().putCoders(BeamFnDataReadRunnerTest.CODER_SPEC_ID, BeamFnDataReadRunnerTest.CODER_SPEC).putCoders(BeamFnDataReadRunnerTest.ELEMENT_CODER_SPEC_ID, CoderTranslation.toProto(BeamFnDataReadRunnerTest.ELEMENT_CODER).getCoder()).build();
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private static final Target INPUT_TARGET = Target.newBuilder().setPrimitiveTransformReference("1").setName("out").build();

    @Rule
    public TestExecutorService executor = TestExecutors.from(Executors::newCachedThreadPool);

    @Mock
    private BeamFnDataClient mockBeamFnDataClient;

    @Captor
    private ArgumentCaptor<FnDataReceiver<WindowedValue<String>>> consumerCaptor;

    @Test
    public void testCreatingAndProcessingBeamFnDataReadRunner() throws Exception {
        String bundleId = "57";
        List<WindowedValue<String>> outputValues = new ArrayList<>();
        MetricsContainerStepMap metricsContainerRegistry = new MetricsContainerStepMap();
        PCollectionConsumerRegistry consumers = new PCollectionConsumerRegistry(metricsContainerRegistry, Mockito.mock(ExecutionStateTracker.class));
        String localOutputId = "outputPC";
        String pTransformId = "pTransformId";
        consumers.register(localOutputId, pTransformId, ((FnDataReceiver) ((FnDataReceiver<WindowedValue<String>>) (outputValues::add))));
        PTransformFunctionRegistry startFunctionRegistry = new PTransformFunctionRegistry(Mockito.mock(MetricsContainerStepMap.class), Mockito.mock(ExecutionStateTracker.class), "start");
        PTransformFunctionRegistry finishFunctionRegistry = new PTransformFunctionRegistry(Mockito.mock(MetricsContainerStepMap.class), Mockito.mock(ExecutionStateTracker.class), "finish");
        RunnerApi.PTransform pTransform = RemoteGrpcPortRead.readFromPort(BeamFnDataReadRunnerTest.PORT_SPEC, localOutputId).toPTransform();
        /* beamFnStateClient */
        /* splitListener */
        new BeamFnDataReadRunner.Factory<String>().createRunnerForPTransform(PipelineOptionsFactory.create(), mockBeamFnDataClient, null, pTransformId, pTransform, Suppliers.ofInstance(bundleId)::get, ImmutableMap.of(localOutputId, PCollection.newBuilder().setCoderId(BeamFnDataReadRunnerTest.ELEMENT_CODER_SPEC_ID).build()), BeamFnDataReadRunnerTest.COMPONENTS.getCodersMap(), BeamFnDataReadRunnerTest.COMPONENTS.getWindowingStrategiesMap(), consumers, startFunctionRegistry, finishFunctionRegistry, null);
        Mockito.verifyZeroInteractions(mockBeamFnDataClient);
        InboundDataClient completionFuture = CompletableFutureInboundDataClient.create();
        Mockito.when(mockBeamFnDataClient.receive(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(completionFuture);
        Iterables.getOnlyElement(startFunctionRegistry.getFunctions()).run();
        Mockito.verify(mockBeamFnDataClient).receive(ArgumentMatchers.eq(BeamFnDataReadRunnerTest.PORT_SPEC.getApiServiceDescriptor()), ArgumentMatchers.eq(LogicalEndpoint.of(bundleId, Target.newBuilder().setPrimitiveTransformReference("pTransformId").setName(Iterables.getOnlyElement(pTransform.getOutputsMap().keySet())).build())), ArgumentMatchers.eq(BeamFnDataReadRunnerTest.CODER), consumerCaptor.capture());
        consumerCaptor.getValue().accept(valueInGlobalWindow("TestValue"));
        Assert.assertThat(outputValues, Matchers.contains(valueInGlobalWindow("TestValue")));
        outputValues.clear();
        Assert.assertThat(consumers.keySet(), Matchers.containsInAnyOrder(localOutputId));
        completionFuture.complete();
        Iterables.getOnlyElement(finishFunctionRegistry.getFunctions()).run();
        Mockito.verifyNoMoreInteractions(mockBeamFnDataClient);
    }

    @Test
    public void testReuseForMultipleBundles() throws Exception {
        InboundDataClient bundle1Future = CompletableFutureInboundDataClient.create();
        InboundDataClient bundle2Future = CompletableFutureInboundDataClient.create();
        Mockito.when(mockBeamFnDataClient.receive(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(bundle1Future).thenReturn(bundle2Future);
        List<WindowedValue<String>> valuesA = new ArrayList<>();
        List<WindowedValue<String>> valuesB = new ArrayList<>();
        FnDataReceiver<WindowedValue<String>> consumers = MultiplexingFnDataReceiver.forConsumers(ImmutableList.of(valuesA::add, valuesB::add));
        AtomicReference<String> bundleId = new AtomicReference<>("0");
        BeamFnDataReadRunner<String> readRunner = new BeamFnDataReadRunner(RemoteGrpcPortRead.readFromPort(BeamFnDataReadRunnerTest.PORT_SPEC, "localOutput").toPTransform(), bundleId::get, BeamFnDataReadRunnerTest.INPUT_TARGET, BeamFnDataReadRunnerTest.CODER_SPEC, BeamFnDataReadRunnerTest.COMPONENTS.getCodersMap(), mockBeamFnDataClient, consumers);
        // Process for bundle id 0
        readRunner.registerInputLocation();
        Mockito.verify(mockBeamFnDataClient).receive(ArgumentMatchers.eq(BeamFnDataReadRunnerTest.PORT_SPEC.getApiServiceDescriptor()), ArgumentMatchers.eq(LogicalEndpoint.of(bundleId.get(), BeamFnDataReadRunnerTest.INPUT_TARGET)), ArgumentMatchers.eq(BeamFnDataReadRunnerTest.CODER), consumerCaptor.capture());
        Future<?> future = executor.submit(() -> {
            // Sleep for some small amount of time simulating the parent blocking
            Uninterruptibles.sleepUninterruptibly(100, TimeUnit.MILLISECONDS);
            try {
                consumerCaptor.getValue().accept(valueInGlobalWindow("ABC"));
                consumerCaptor.getValue().accept(valueInGlobalWindow("DEF"));
            } catch ( e) {
                bundle1Future.fail(e);
            } finally {
                bundle1Future.complete();
            }
        });
        readRunner.blockTillReadFinishes();
        future.get();
        Assert.assertThat(valuesA, Matchers.contains(valueInGlobalWindow("ABC"), valueInGlobalWindow("DEF")));
        Assert.assertThat(valuesB, Matchers.contains(valueInGlobalWindow("ABC"), valueInGlobalWindow("DEF")));
        // Process for bundle id 1
        bundleId.set("1");
        valuesA.clear();
        valuesB.clear();
        readRunner.registerInputLocation();
        Mockito.verify(mockBeamFnDataClient).receive(ArgumentMatchers.eq(BeamFnDataReadRunnerTest.PORT_SPEC.getApiServiceDescriptor()), ArgumentMatchers.eq(LogicalEndpoint.of(bundleId.get(), BeamFnDataReadRunnerTest.INPUT_TARGET)), ArgumentMatchers.eq(BeamFnDataReadRunnerTest.CODER), consumerCaptor.capture());
        future = executor.submit(() -> {
            // Sleep for some small amount of time simulating the parent blocking
            Uninterruptibles.sleepUninterruptibly(100, TimeUnit.MILLISECONDS);
            try {
                consumerCaptor.getValue().accept(valueInGlobalWindow("GHI"));
                consumerCaptor.getValue().accept(valueInGlobalWindow("JKL"));
            } catch ( e) {
                bundle2Future.fail(e);
            } finally {
                bundle2Future.complete();
            }
        });
        readRunner.blockTillReadFinishes();
        future.get();
        Assert.assertThat(valuesA, Matchers.contains(valueInGlobalWindow("GHI"), valueInGlobalWindow("JKL")));
        Assert.assertThat(valuesB, Matchers.contains(valueInGlobalWindow("GHI"), valueInGlobalWindow("JKL")));
        Mockito.verifyNoMoreInteractions(mockBeamFnDataClient);
    }

    @Test
    public void testRegistration() {
        for (Registrar registrar : ServiceLoader.load(Registrar.class)) {
            if (registrar instanceof BeamFnDataReadRunner.Registrar) {
                Assert.assertThat(registrar.getPTransformRunnerFactories(), IsMapContaining.hasKey(URN));
                return;
            }
        }
        Assert.fail("Expected registrar not found.");
    }
}

