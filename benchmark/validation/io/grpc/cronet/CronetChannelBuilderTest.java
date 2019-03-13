/**
 * Copyright 2016 The gRPC Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.grpc.cronet;


import CallOptions.DEFAULT;
import io.grpc.ChannelLogger;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.cronet.CronetChannelBuilder.CronetTransportFactory;
import io.grpc.internal.ClientTransportFactory;
import io.grpc.internal.ClientTransportFactory.ClientTransportOptions;
import io.grpc.internal.SharedResourceHolder;
import io.grpc.testing.TestMethodDescriptors;
import java.net.InetSocketAddress;
import java.util.concurrent.ScheduledExecutorService;
import org.chromium.net.ExperimentalCronetEngine;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public final class CronetChannelBuilderTest {
    @Mock
    private ExperimentalCronetEngine mockEngine;

    @Mock
    private ChannelLogger channelLogger;

    private MethodDescriptor<?, ?> method = TestMethodDescriptors.voidMethod();

    @Test
    public void alwaysUsePutTrue_cronetStreamIsIdempotent() throws Exception {
        CronetChannelBuilder builder = CronetChannelBuilder.forAddress("address", 1234, mockEngine).alwaysUsePut(true);
        CronetTransportFactory transportFactory = ((CronetTransportFactory) (builder.buildTransportFactory()));
        CronetClientTransport transport = ((CronetClientTransport) (transportFactory.newClientTransport(new InetSocketAddress("localhost", 443), new ClientTransportOptions(), channelLogger)));
        CronetClientStream stream = transport.newStream(method, new Metadata(), DEFAULT);
        Assert.assertTrue(stream.idempotent);
    }

    @Test
    public void alwaysUsePut_defaultsToFalse() throws Exception {
        CronetChannelBuilder builder = CronetChannelBuilder.forAddress("address", 1234, mockEngine);
        CronetTransportFactory transportFactory = ((CronetTransportFactory) (builder.buildTransportFactory()));
        CronetClientTransport transport = ((CronetClientTransport) (transportFactory.newClientTransport(new InetSocketAddress("localhost", 443), new ClientTransportOptions(), channelLogger)));
        CronetClientStream stream = transport.newStream(method, new Metadata(), DEFAULT);
        Assert.assertFalse(stream.idempotent);
    }

    @Test
    public void scheduledExecutorService_default() {
        CronetChannelBuilder builder = CronetChannelBuilder.forAddress("address", 1234, mockEngine);
        ClientTransportFactory clientTransportFactory = builder.buildTransportFactory();
        Assert.assertSame(SharedResourceHolder.get(TIMER_SERVICE), clientTransportFactory.getScheduledExecutorService());
        SharedResourceHolder.release(TIMER_SERVICE, clientTransportFactory.getScheduledExecutorService());
        clientTransportFactory.close();
    }

    @Test
    public void scheduledExecutorService_custom() {
        CronetChannelBuilder builder = CronetChannelBuilder.forAddress("address", 1234, mockEngine);
        ScheduledExecutorService scheduledExecutorService = Mockito.mock(ScheduledExecutorService.class);
        CronetChannelBuilder builder1 = builder.scheduledExecutorService(scheduledExecutorService);
        Assert.assertSame(builder, builder1);
        ClientTransportFactory clientTransportFactory = builder1.buildTransportFactory();
        Assert.assertSame(scheduledExecutorService, clientTransportFactory.getScheduledExecutorService());
        clientTransportFactory.close();
    }
}

