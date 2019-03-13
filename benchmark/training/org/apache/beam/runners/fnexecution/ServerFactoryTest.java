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
package org.apache.beam.runners.fnexecution;


import ApiServiceDescriptor.Builder;
import BeamFnApi.Elements.Data;
import BeamFnDataGrpc.BeamFnDataImplBase;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.beam.model.fnexecution.v1.BeamFnApi;
import org.apache.beam.model.fnexecution.v1.BeamFnApi.Elements;
import org.apache.beam.model.fnexecution.v1.BeamFnDataGrpc;
import org.apache.beam.model.pipeline.v1.Endpoints;
import org.apache.beam.model.pipeline.v1.Endpoints.ApiServiceDescriptor;
import org.apache.beam.sdk.fn.channel.ManagedChannelFactory;
import org.apache.beam.sdk.fn.test.TestStreams;
import org.apache.beam.vendor.grpc.v1p13p1.io.grpc.Server;
import org.apache.beam.vendor.grpc.v1p13p1.io.grpc.stub.CallStreamObserver;
import org.apache.beam.vendor.grpc.v1p13p1.io.grpc.stub.StreamObserver;
import org.apache.beam.vendor.grpc.v1p13p1.io.netty.channel.epoll.Epoll;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.apache.beam.vendor.guava.v20_0.com.google.common.net.HostAndPort;
import org.apache.beam.vendor.guava.v20_0.com.google.common.util.concurrent.Uninterruptibles;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


/**
 * Tests for {@link ServerFactory}.
 */
public class ServerFactoryTest {
    private static final Elements CLIENT_DATA = BeamFnApi.Elements.newBuilder().addData(Data.newBuilder().setInstructionReference("1")).build();

    private static final Elements SERVER_DATA = BeamFnApi.Elements.newBuilder().addData(Data.newBuilder().setInstructionReference("1")).build();

    @Test
    public void defaultServerWorks() throws Exception {
        Endpoints.ApiServiceDescriptor apiServiceDescriptor = runTestUsing(ServerFactory.createDefault(), ManagedChannelFactory.createDefault());
        HostAndPort hostAndPort = HostAndPort.fromString(apiServiceDescriptor.getUrl());
        Assert.assertThat(hostAndPort.getHost(), Matchers.anyOf(Matchers.equalTo(InetAddress.getLoopbackAddress().getHostName()), Matchers.equalTo(InetAddress.getLoopbackAddress().getHostAddress())));
        Assert.assertThat(hostAndPort.getPort(), Matchers.allOf(Matchers.greaterThan(0), Matchers.lessThan(65536)));
    }

    @Test
    public void usesUrlFactory() throws Exception {
        ServerFactory serverFactory = ServerFactory.createWithUrlFactory(( host, port) -> "foo");
        CallStreamObserver<Elements> observer = TestStreams.withOnNext((Elements unused) -> {
        }).withOnCompleted(() -> {
        }).build();
        ServerFactoryTest.TestDataService service = new ServerFactoryTest.TestDataService(observer);
        ApiServiceDescriptor.Builder descriptorBuilder = ApiServiceDescriptor.newBuilder();
        Server server = serverFactory.allocateAddressAndCreate(ImmutableList.of(service), descriptorBuilder);
        // Immediately terminate server. We don't actually use it here.
        server.shutdown();
        Assert.assertThat(descriptorBuilder.getUrl(), Matchers.is("foo"));
    }

    @Test
    public void defaultServerWithPortSupplier() throws Exception {
        Endpoints.ApiServiceDescriptor apiServiceDescriptor = runTestUsing(ServerFactory.createWithPortSupplier(() -> 65535), ManagedChannelFactory.createDefault());
        HostAndPort hostAndPort = HostAndPort.fromString(apiServiceDescriptor.getUrl());
        Assert.assertThat(hostAndPort.getHost(), Matchers.anyOf(Matchers.equalTo(InetAddress.getLoopbackAddress().getHostName()), Matchers.equalTo(InetAddress.getLoopbackAddress().getHostAddress())));
        Assert.assertThat(hostAndPort.getPort(), Matchers.is(65535));
    }

    @Test
    public void urlFactoryWithPortSupplier() throws Exception {
        ServerFactory serverFactory = ServerFactory.createWithUrlFactoryAndPortSupplier(( host, port) -> ("foo" + ":") + port, () -> 65535);
        CallStreamObserver<Elements> observer = TestStreams.withOnNext((Elements unused) -> {
        }).withOnCompleted(() -> {
        }).build();
        ServerFactoryTest.TestDataService service = new ServerFactoryTest.TestDataService(observer);
        ApiServiceDescriptor.Builder descriptorBuilder = ApiServiceDescriptor.newBuilder();
        Server server = null;
        try {
            server = serverFactory.allocateAddressAndCreate(ImmutableList.of(service), descriptorBuilder);
            Assert.assertThat(descriptorBuilder.getUrl(), Matchers.is("foo:65535"));
        } finally {
            if (server != null) {
                server.shutdown();
            }
        }
    }

    @Test
    public void testCreatingEpollServer() throws Exception {
        Assume.assumeTrue(Epoll.isAvailable());
        // tcnative only supports the ipv4 address family
        Assume.assumeTrue(((InetAddress.getLoopbackAddress()) instanceof Inet4Address));
        Endpoints.ApiServiceDescriptor apiServiceDescriptor = runTestUsing(ServerFactory.createEpollSocket(), ManagedChannelFactory.createEpoll());
        HostAndPort hostAndPort = HostAndPort.fromString(apiServiceDescriptor.getUrl());
        Assert.assertThat(hostAndPort.getHost(), Matchers.anyOf(Matchers.equalTo(InetAddress.getLoopbackAddress().getHostName()), Matchers.equalTo(InetAddress.getLoopbackAddress().getHostAddress())));
        Assert.assertThat(hostAndPort.getPort(), Matchers.allOf(Matchers.greaterThan(0), Matchers.lessThan(65536)));
    }

    @Test
    public void testCreatingUnixDomainSocketServer() throws Exception {
        Assume.assumeTrue(Epoll.isAvailable());
        Endpoints.ApiServiceDescriptor apiServiceDescriptor = runTestUsing(ServerFactory.createEpollDomainSocket(), ManagedChannelFactory.createEpoll());
        Assert.assertThat(apiServiceDescriptor.getUrl(), Matchers.startsWith(("unix://" + (System.getProperty("java.io.tmpdir")))));
    }

    /**
     * A test gRPC service that uses the provided inbound observer for all clients.
     */
    private static class TestDataService extends BeamFnDataGrpc.BeamFnDataImplBase {
        private final LinkedBlockingQueue<StreamObserver<BeamFnApi.Elements>> outboundObservers;

        private final StreamObserver<BeamFnApi.Elements> inboundObserver;

        private TestDataService(StreamObserver<BeamFnApi.Elements> inboundObserver) {
            this.inboundObserver = inboundObserver;
            this.outboundObservers = new LinkedBlockingQueue();
        }

        @Override
        public StreamObserver<BeamFnApi.Elements> data(StreamObserver<BeamFnApi.Elements> outboundObserver) {
            Uninterruptibles.putUninterruptibly(outboundObservers, outboundObserver);
            return inboundObserver;
        }
    }
}

