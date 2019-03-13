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


import Attributes.Key;
import BidirectionalStream.Builder;
import BidirectionalStream.Callback;
import CallOptions.DEFAULT;
import GrpcAttributes.ATTR_CLIENT_EAG_ATTRS;
import GrpcAttributes.ATTR_SECURITY_LEVEL;
import ManagedClientTransport.Listener;
import SecurityLevel.PRIVACY_AND_INTEGRITY;
import Status.UNAVAILABLE;
import io.grpc.Attributes;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.Status;
import io.grpc.cronet.CronetChannelBuilder.StreamBuilderFactory;
import io.grpc.internal.ClientStreamListener;
import io.grpc.testing.TestMethodDescriptors;
import java.util.concurrent.Executor;
import org.chromium.net.BidirectionalStream;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public final class CronetClientTransportTest {
    private static final String AUTHORITY = "test.example.com";

    private static final Attributes.Key<String> EAG_ATTR_KEY = Key.create("eag-attr");

    private static final Attributes EAG_ATTRS = Attributes.newBuilder().set(io.grpc.cronet.EAG_ATTR_KEY, "value").build();

    private CronetClientTransport transport;

    @Mock
    private StreamBuilderFactory streamFactory;

    @Mock
    private Executor executor;

    private MethodDescriptor<Void, Void> descriptor = TestMethodDescriptors.voidMethod();

    @Mock
    private Listener clientTransportListener;

    @Mock
    private Builder builder;

    @Test
    public void transportAttributes() {
        Attributes attrs = transport.getAttributes();
        Assert.assertEquals(PRIVACY_AND_INTEGRITY, attrs.get(ATTR_SECURITY_LEVEL));
        Assert.assertEquals(CronetClientTransportTest.EAG_ATTRS, attrs.get(ATTR_CLIENT_EAG_ATTRS));
    }

    @Test
    public void shutdownTransport() throws Exception {
        CronetClientStream stream1 = transport.newStream(descriptor, new Metadata(), DEFAULT);
        CronetClientStream stream2 = transport.newStream(descriptor, new Metadata(), DEFAULT);
        // Create a transport and start two streams on it.
        ArgumentCaptor<BidirectionalStream.Callback> callbackCaptor = ArgumentCaptor.forClass(Callback.class);
        Mockito.when(streamFactory.newBidirectionalStreamBuilder(ArgumentMatchers.any(String.class), callbackCaptor.capture(), ArgumentMatchers.any(Executor.class))).thenReturn(builder);
        BidirectionalStream cronetStream1 = Mockito.mock(BidirectionalStream.class);
        Mockito.when(builder.build()).thenReturn(cronetStream1);
        stream1.start(Mockito.mock(ClientStreamListener.class));
        BidirectionalStream.Callback callback1 = callbackCaptor.getValue();
        BidirectionalStream cronetStream2 = Mockito.mock(BidirectionalStream.class);
        Mockito.when(builder.build()).thenReturn(cronetStream2);
        stream2.start(Mockito.mock(ClientStreamListener.class));
        BidirectionalStream.Callback callback2 = callbackCaptor.getValue();
        // Shut down the transport. transportShutdown should be called immediately.
        transport.shutdown();
        Mockito.verify(clientTransportListener).transportShutdown(ArgumentMatchers.any(Status.class));
        // Have two live streams. Transport has not been terminated.
        Mockito.verify(clientTransportListener, Mockito.times(0)).transportTerminated();
        callback1.onCanceled(cronetStream1, null);
        // Still has one live stream
        Mockito.verify(clientTransportListener, Mockito.times(0)).transportTerminated();
        callback2.onCanceled(cronetStream1, null);
        // All streams are gone now.
        Mockito.verify(clientTransportListener, Mockito.times(1)).transportTerminated();
    }

    @Test
    public void startStreamAfterShutdown() throws Exception {
        CronetClientStream stream = transport.newStream(descriptor, new Metadata(), DEFAULT);
        transport.shutdown();
        CronetClientTransportTest.BaseClientStreamListener listener = new CronetClientTransportTest.BaseClientStreamListener();
        stream.start(listener);
        Assert.assertEquals(UNAVAILABLE.getCode(), listener.status.getCode());
    }

    private static class BaseClientStreamListener implements ClientStreamListener {
        private Status status;

        @Override
        public void messagesAvailable(MessageProducer producer) {
        }

        @Override
        public void onReady() {
        }

        @Override
        public void headersRead(Metadata headers) {
        }

        @Override
        public void closed(Status status, Metadata trailers) {
        }

        @Override
        public void closed(Status status, RpcProgress rpcProgress, Metadata trailers) {
            this.status = status;
        }
    }
}

