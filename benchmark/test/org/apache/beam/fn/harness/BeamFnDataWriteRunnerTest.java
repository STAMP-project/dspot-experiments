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
import RemoteGrpcPortWrite.URN;
import RunnerApi.Components;
import RunnerApi.PCollection;
import RunnerApi.PTransform;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.beam.fn.harness.PTransformRunnerFactory.Registrar;
import org.apache.beam.fn.harness.data.BeamFnDataClient;
import org.apache.beam.fn.harness.data.PCollectionConsumerRegistry;
import org.apache.beam.fn.harness.data.PTransformFunctionRegistry;
import org.apache.beam.model.pipeline.v1.RunnerApi;
import org.apache.beam.model.pipeline.v1.RunnerApi.MessageWithComponents;
import org.apache.beam.runners.core.construction.CoderTranslation;
import org.apache.beam.runners.core.metrics.ExecutionStateTracker;
import org.apache.beam.runners.core.metrics.MetricsContainerStepMap;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.fn.data.CloseableFnDataReceiver;
import org.apache.beam.sdk.fn.data.LogicalEndpoint;
import org.apache.beam.sdk.fn.data.RemoteGrpcPortWrite;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.util.WindowedValue;
import org.apache.beam.vendor.guava.v20_0.com.google.common.base.Suppliers;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableMap;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Iterables;
import org.hamcrest.collection.IsMapContaining;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;


/**
 * Tests for {@link BeamFnDataWriteRunner}.
 */
@RunWith(JUnit4.class)
public class BeamFnDataWriteRunnerTest {
    private static final String ELEM_CODER_ID = "string-coder-id";

    private static final Coder<String> ELEM_CODER = StringUtf8Coder.of();

    private static final String WIRE_CODER_ID = "windowed-string-coder-id";

    private static final Coder<WindowedValue<String>> WIRE_CODER = WindowedValue.getFullCoder(BeamFnDataWriteRunnerTest.ELEM_CODER, INSTANCE);

    private static final Coder WIRE_CODER_SPEC;

    private static final Components COMPONENTS;

    private static final RemoteGrpcPort PORT_SPEC = RemoteGrpcPort.newBuilder().setApiServiceDescriptor(ApiServiceDescriptor.getDefaultInstance()).setCoderId(BeamFnDataWriteRunnerTest.WIRE_CODER_ID).build();

    static {
        try {
            MessageWithComponents coderAndComponents = CoderTranslation.toProto(BeamFnDataWriteRunnerTest.WIRE_CODER);
            WIRE_CODER_SPEC = coderAndComponents.getCoder();
            COMPONENTS = coderAndComponents.getComponents().toBuilder().putCoders(BeamFnDataWriteRunnerTest.WIRE_CODER_ID, BeamFnDataWriteRunnerTest.WIRE_CODER_SPEC).putCoders(BeamFnDataWriteRunnerTest.ELEM_CODER_ID, CoderTranslation.toProto(BeamFnDataWriteRunnerTest.ELEM_CODER).getCoder()).build();
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private static final Target OUTPUT_TARGET = Target.newBuilder().setPrimitiveTransformReference("1").setName("out").build();

    @Mock
    private BeamFnDataClient mockBeamFnDataClient;

    @Test
    public void testCreatingAndProcessingBeamFnDataWriteRunner() throws Exception {
        String bundleId = "57L";
        PCollectionConsumerRegistry consumers = new PCollectionConsumerRegistry(Mockito.mock(MetricsContainerStepMap.class), Mockito.mock(ExecutionStateTracker.class));
        PTransformFunctionRegistry startFunctionRegistry = new PTransformFunctionRegistry(Mockito.mock(MetricsContainerStepMap.class), Mockito.mock(ExecutionStateTracker.class), "start");
        PTransformFunctionRegistry finishFunctionRegistry = new PTransformFunctionRegistry(Mockito.mock(MetricsContainerStepMap.class), Mockito.mock(ExecutionStateTracker.class), "finish");
        String localInputId = "inputPC";
        RunnerApi.PTransform pTransform = RemoteGrpcPortWrite.writeToPort(localInputId, BeamFnDataWriteRunnerTest.PORT_SPEC).toPTransform();
        /* beamFnStateClient */
        /* splitListener */
        new BeamFnDataWriteRunner.Factory<String>().createRunnerForPTransform(PipelineOptionsFactory.create(), mockBeamFnDataClient, null, "ptransformId", pTransform, Suppliers.ofInstance(bundleId)::get, ImmutableMap.of(localInputId, PCollection.newBuilder().setCoderId(BeamFnDataWriteRunnerTest.ELEM_CODER_ID).build()), BeamFnDataWriteRunnerTest.COMPONENTS.getCodersMap(), BeamFnDataWriteRunnerTest.COMPONENTS.getWindowingStrategiesMap(), consumers, startFunctionRegistry, finishFunctionRegistry, null);
        Mockito.verifyZeroInteractions(mockBeamFnDataClient);
        List<WindowedValue<String>> outputValues = new ArrayList<>();
        AtomicBoolean wasCloseCalled = new AtomicBoolean();
        CloseableFnDataReceiver<WindowedValue<String>> outputConsumer = new CloseableFnDataReceiver<WindowedValue<String>>() {
            @Override
            public void close() throws Exception {
                wasCloseCalled.set(true);
            }

            @Override
            public void accept(WindowedValue<String> t) throws Exception {
                outputValues.add(t);
            }

            @Override
            public void flush() throws Exception {
                throw new UnsupportedOperationException("Flush is not supported");
            }
        };
        Mockito.when(mockBeamFnDataClient.send(ArgumentMatchers.any(), ArgumentMatchers.any(), Matchers.<Coder<WindowedValue<String>>>any())).thenReturn(outputConsumer);
        Iterables.getOnlyElement(startFunctionRegistry.getFunctions()).run();
        Mockito.verify(mockBeamFnDataClient).send(ArgumentMatchers.eq(BeamFnDataWriteRunnerTest.PORT_SPEC.getApiServiceDescriptor()), ArgumentMatchers.eq(LogicalEndpoint.of(bundleId, // The local input name is arbitrary, so use whatever the
        // RemoteGrpcPortWrite uses
        Target.newBuilder().setPrimitiveTransformReference("ptransformId").setName(Iterables.getOnlyElement(pTransform.getInputsMap().keySet())).build())), ArgumentMatchers.eq(BeamFnDataWriteRunnerTest.WIRE_CODER));
        Assert.assertThat(consumers.keySet(), containsInAnyOrder(localInputId));
        consumers.getMultiplexingConsumer(localInputId).accept(valueInGlobalWindow("TestValue"));
        Assert.assertThat(outputValues, org.hamcrest.Matchers.contains(valueInGlobalWindow("TestValue")));
        outputValues.clear();
        Assert.assertFalse(wasCloseCalled.get());
        Iterables.getOnlyElement(finishFunctionRegistry.getFunctions()).run();
        Assert.assertTrue(wasCloseCalled.get());
        Mockito.verifyNoMoreInteractions(mockBeamFnDataClient);
    }

    @Test
    public void testReuseForMultipleBundles() throws Exception {
        BeamFnDataWriteRunnerTest.RecordingReceiver<WindowedValue<String>> valuesA = new BeamFnDataWriteRunnerTest.RecordingReceiver<>();
        BeamFnDataWriteRunnerTest.RecordingReceiver<WindowedValue<String>> valuesB = new BeamFnDataWriteRunnerTest.RecordingReceiver<>();
        Mockito.when(mockBeamFnDataClient.send(ArgumentMatchers.any(), ArgumentMatchers.any(), Matchers.<Coder<WindowedValue<String>>>any())).thenReturn(valuesA).thenReturn(valuesB);
        AtomicReference<String> bundleId = new AtomicReference<>("0");
        BeamFnDataWriteRunner<String> writeRunner = new BeamFnDataWriteRunner(RemoteGrpcPortWrite.writeToPort("myWrite", BeamFnDataWriteRunnerTest.PORT_SPEC).toPTransform(), bundleId::get, BeamFnDataWriteRunnerTest.OUTPUT_TARGET, BeamFnDataWriteRunnerTest.WIRE_CODER_SPEC, BeamFnDataWriteRunnerTest.COMPONENTS.getCodersMap(), mockBeamFnDataClient);
        // Process for bundle id 0
        writeRunner.registerForOutput();
        Mockito.verify(mockBeamFnDataClient).send(ArgumentMatchers.eq(BeamFnDataWriteRunnerTest.PORT_SPEC.getApiServiceDescriptor()), ArgumentMatchers.eq(LogicalEndpoint.of(bundleId.get(), BeamFnDataWriteRunnerTest.OUTPUT_TARGET)), ArgumentMatchers.eq(BeamFnDataWriteRunnerTest.WIRE_CODER));
        writeRunner.consume(valueInGlobalWindow("ABC"));
        writeRunner.consume(valueInGlobalWindow("DEF"));
        writeRunner.close();
        Assert.assertTrue(valuesA.closed);
        Assert.assertThat(valuesA, contains(valueInGlobalWindow("ABC"), valueInGlobalWindow("DEF")));
        // Process for bundle id 1
        bundleId.set("1");
        valuesA.clear();
        valuesB.clear();
        writeRunner.registerForOutput();
        Mockito.verify(mockBeamFnDataClient).send(ArgumentMatchers.eq(BeamFnDataWriteRunnerTest.PORT_SPEC.getApiServiceDescriptor()), ArgumentMatchers.eq(LogicalEndpoint.of(bundleId.get(), BeamFnDataWriteRunnerTest.OUTPUT_TARGET)), ArgumentMatchers.eq(BeamFnDataWriteRunnerTest.WIRE_CODER));
        writeRunner.consume(valueInGlobalWindow("GHI"));
        writeRunner.consume(valueInGlobalWindow("JKL"));
        writeRunner.close();
        Assert.assertTrue(valuesB.closed);
        Assert.assertThat(valuesB, contains(valueInGlobalWindow("GHI"), valueInGlobalWindow("JKL")));
        Mockito.verifyNoMoreInteractions(mockBeamFnDataClient);
    }

    private static class RecordingReceiver<T> extends ArrayList<T> implements CloseableFnDataReceiver<T> {
        private boolean closed;

        @Override
        public void close() throws Exception {
            closed = true;
        }

        @Override
        public void accept(T t) throws Exception {
            if (closed) {
                throw new IllegalStateException(("Consumer is closed but attempting to consume " + t));
            }
            add(t);
        }

        @Override
        public void flush() throws Exception {
            throw new UnsupportedOperationException("Flush is not supported");
        }
    }

    @Test
    public void testRegistration() {
        for (Registrar registrar : ServiceLoader.load(Registrar.class)) {
            if (registrar instanceof BeamFnDataWriteRunner.Registrar) {
                Assert.assertThat(registrar.getPTransformRunnerFactories(), IsMapContaining.hasKey(URN));
                return;
            }
        }
        Assert.fail("Expected registrar not found.");
    }
}

