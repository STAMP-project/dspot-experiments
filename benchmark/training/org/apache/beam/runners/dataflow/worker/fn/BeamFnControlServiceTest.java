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
package org.apache.beam.runners.dataflow.worker.fn;


import BeamFnControlGrpc.BeamFnControlStub;
import Endpoints.ApiServiceDescriptor;
import java.util.concurrent.TimeUnit;
import org.apache.beam.model.fnexecution.v1.BeamFnApi;
import org.apache.beam.model.fnexecution.v1.BeamFnControlGrpc;
import org.apache.beam.model.pipeline.v1.Endpoints;
import org.apache.beam.runners.dataflow.worker.fn.stream.ServerStreamObserverFactory;
import org.apache.beam.runners.fnexecution.GrpcContextHeaderAccessorProvider;
import org.apache.beam.runners.fnexecution.ServerFactory;
import org.apache.beam.runners.fnexecution.control.FnApiControlClient;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.vendor.grpc.v1p13p1.io.grpc.ManagedChannelBuilder;
import org.apache.beam.vendor.grpc.v1p13p1.io.grpc.Server;
import org.apache.beam.vendor.grpc.v1p13p1.io.grpc.stub.StreamObserver;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Tests for {@link BeamFnControlService}.
 */
@RunWith(JUnit4.class)
public class BeamFnControlServiceTest {
    @Mock
    private StreamObserver<BeamFnApi.InstructionRequest> requestObserver;

    @Mock
    private StreamObserver<BeamFnApi.InstructionRequest> anotherRequestObserver;

    @Test
    public void testClientConnecting() throws Exception {
        PipelineOptions options = PipelineOptionsFactory.create();
        Endpoints.ApiServiceDescriptor descriptor = findOpenPort();
        BeamFnControlService service = new BeamFnControlService(descriptor, ServerStreamObserverFactory.fromOptions(options)::from, GrpcContextHeaderAccessorProvider.getHeaderAccessor());
        Server server = ServerFactory.createDefault().create(ImmutableList.of(service), descriptor);
        String url = service.getApiServiceDescriptor().getUrl();
        BeamFnControlGrpc.BeamFnControlStub clientStub = BeamFnControlGrpc.newStub(ManagedChannelBuilder.forTarget(url).usePlaintext(true).build());
        // Connect from the client.
        clientStub.control(requestObserver);
        try (FnApiControlClient client = service.get()) {
            Assert.assertNotNull(client);
        }
        server.shutdown();
        server.awaitTermination(1, TimeUnit.SECONDS);
        server.shutdownNow();
        Thread.sleep(1000);// Wait for stub to close stream.

        Mockito.verify(requestObserver).onCompleted();
        Mockito.verifyNoMoreInteractions(requestObserver);
    }

    @Test
    public void testMultipleClientsConnecting() throws Exception {
        PipelineOptions options = PipelineOptionsFactory.create();
        Endpoints.ApiServiceDescriptor descriptor = findOpenPort();
        BeamFnControlService service = new BeamFnControlService(descriptor, ServerStreamObserverFactory.fromOptions(options)::from, GrpcContextHeaderAccessorProvider.getHeaderAccessor());
        Server server = ServerFactory.createDefault().create(ImmutableList.of(service), descriptor);
        String url = service.getApiServiceDescriptor().getUrl();
        BeamFnControlGrpc.BeamFnControlStub clientStub = BeamFnControlGrpc.newStub(ManagedChannelBuilder.forTarget(url).usePlaintext(true).build());
        BeamFnControlGrpc.BeamFnControlStub anotherClientStub = BeamFnControlGrpc.newStub(ManagedChannelBuilder.forTarget(url).usePlaintext(true).build());
        // Connect from the client.
        clientStub.control(requestObserver);
        // Connect again from the client.
        anotherClientStub.control(anotherRequestObserver);
        try (FnApiControlClient client = service.get()) {
            Assert.assertNotNull(client);
            try (FnApiControlClient anotherClient = service.get()) {
                Assert.assertNotNull(anotherClient);
            }
        }
        server.shutdown();
        server.awaitTermination(1, TimeUnit.SECONDS);
        server.shutdownNow();
        Thread.sleep(1000);// Wait for stub to close stream.

        Mockito.verify(requestObserver).onCompleted();
        Mockito.verifyNoMoreInteractions(requestObserver);
        Mockito.verify(anotherRequestObserver).onCompleted();
        Mockito.verifyNoMoreInteractions(anotherRequestObserver);
    }
}

