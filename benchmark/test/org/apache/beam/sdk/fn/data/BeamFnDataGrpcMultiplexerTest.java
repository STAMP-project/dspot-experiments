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
package org.apache.beam.sdk.fn.data;


import BeamFnApi.Elements;
import BeamFnApi.Elements.Data;
import BeamFnApi.Target;
import Endpoints.ApiServiceDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.beam.model.fnexecution.v1.BeamFnApi;
import org.apache.beam.sdk.fn.stream.OutboundObserverFactory;
import org.apache.beam.sdk.fn.test.TestStreams;
import org.apache.beam.vendor.grpc.v1p13p1.com.google.protobuf.ByteString;
import org.apache.beam.vendor.guava.v20_0.com.google.common.util.concurrent.Uninterruptibles;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link BeamFnDataGrpcMultiplexer}.
 */
public class BeamFnDataGrpcMultiplexerTest {
    private static final ApiServiceDescriptor DESCRIPTOR = ApiServiceDescriptor.newBuilder().setUrl("test").build();

    private static final LogicalEndpoint OUTPUT_LOCATION = LogicalEndpoint.of("777L", Target.newBuilder().setName("name").setPrimitiveTransformReference("888L").build());

    private static final Elements ELEMENTS = Elements.newBuilder().addData(Data.newBuilder().setInstructionReference(BeamFnDataGrpcMultiplexerTest.OUTPUT_LOCATION.getInstructionId()).setTarget(BeamFnDataGrpcMultiplexerTest.OUTPUT_LOCATION.getTarget()).setData(ByteString.copyFrom(new byte[1]))).build();

    private static final Elements TERMINAL_ELEMENTS = Elements.newBuilder().addData(Data.newBuilder().setInstructionReference(BeamFnDataGrpcMultiplexerTest.OUTPUT_LOCATION.getInstructionId()).setTarget(BeamFnDataGrpcMultiplexerTest.OUTPUT_LOCATION.getTarget())).build();

    @Test
    public void testOutboundObserver() {
        final Collection<BeamFnApi.Elements> values = new ArrayList<>();
        BeamFnDataGrpcMultiplexer multiplexer = new BeamFnDataGrpcMultiplexer(BeamFnDataGrpcMultiplexerTest.DESCRIPTOR, OutboundObserverFactory.clientDirect(), ( inboundObserver) -> TestStreams.withOnNext(values::add).build());
        multiplexer.getOutboundObserver().onNext(BeamFnDataGrpcMultiplexerTest.ELEMENTS);
        Assert.assertThat(values, Matchers.contains(BeamFnDataGrpcMultiplexerTest.ELEMENTS));
    }

    @Test
    public void testInboundObserverBlocksTillConsumerConnects() throws Exception {
        final Collection<BeamFnApi.Elements> outboundValues = new ArrayList<>();
        final Collection<BeamFnApi.Elements.Data> inboundValues = new ArrayList<>();
        final BeamFnDataGrpcMultiplexer multiplexer = new BeamFnDataGrpcMultiplexer(BeamFnDataGrpcMultiplexerTest.DESCRIPTOR, OutboundObserverFactory.clientDirect(), ( inboundObserver) -> TestStreams.withOnNext(outboundValues::add).build());
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.submit(() -> {
            // Purposefully sleep to simulate a delay in a consumer connecting.
            Uninterruptibles.sleepUninterruptibly(100, TimeUnit.MILLISECONDS);
            multiplexer.registerConsumer(BeamFnDataGrpcMultiplexerTest.OUTPUT_LOCATION, inboundValues::add);
        }).get();
        multiplexer.getInboundObserver().onNext(BeamFnDataGrpcMultiplexerTest.ELEMENTS);
        Assert.assertTrue(multiplexer.hasConsumer(BeamFnDataGrpcMultiplexerTest.OUTPUT_LOCATION));
        // Ensure that when we see a terminal Elements object, we remove the consumer
        multiplexer.getInboundObserver().onNext(BeamFnDataGrpcMultiplexerTest.TERMINAL_ELEMENTS);
        Assert.assertFalse(multiplexer.hasConsumer(BeamFnDataGrpcMultiplexerTest.OUTPUT_LOCATION));
        // Assert that normal and terminal Elements are passed to the consumer
        Assert.assertThat(inboundValues, Matchers.contains(BeamFnDataGrpcMultiplexerTest.ELEMENTS.getData(0), BeamFnDataGrpcMultiplexerTest.TERMINAL_ELEMENTS.getData(0)));
    }
}

