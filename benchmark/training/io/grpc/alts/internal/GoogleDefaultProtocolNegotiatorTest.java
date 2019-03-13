/**
 * Copyright 2018 The gRPC Authors
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
package io.grpc.alts.internal;


import GrpcAttributes.ATTR_LB_PROVIDED_BACKEND;
import io.grpc.Attributes;
import io.grpc.netty.GrpcHttp2ConnectionHandler;
import io.grpc.netty.InternalProtocolNegotiator.ProtocolNegotiator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;


@RunWith(JUnit4.class)
public final class GoogleDefaultProtocolNegotiatorTest {
    private ProtocolNegotiator altsProtocolNegotiator;

    private ProtocolNegotiator tlsProtocolNegotiator;

    private GoogleDefaultProtocolNegotiator googleProtocolNegotiator;

    @Test
    public void altsHandler() {
        Attributes eagAttributes = Attributes.newBuilder().set(ATTR_LB_PROVIDED_BACKEND, true).build();
        GrpcHttp2ConnectionHandler mockHandler = Mockito.mock(GrpcHttp2ConnectionHandler.class);
        Mockito.when(mockHandler.getEagAttributes()).thenReturn(eagAttributes);
        googleProtocolNegotiator.newHandler(mockHandler);
        Mockito.verify(altsProtocolNegotiator, Mockito.times(1)).newHandler(mockHandler);
        Mockito.verify(tlsProtocolNegotiator, Mockito.never()).newHandler(mockHandler);
    }

    @Test
    public void tlsHandler() {
        Attributes eagAttributes = Attributes.EMPTY;
        GrpcHttp2ConnectionHandler mockHandler = Mockito.mock(GrpcHttp2ConnectionHandler.class);
        Mockito.when(mockHandler.getEagAttributes()).thenReturn(eagAttributes);
        googleProtocolNegotiator.newHandler(mockHandler);
        Mockito.verify(altsProtocolNegotiator, Mockito.never()).newHandler(mockHandler);
        Mockito.verify(tlsProtocolNegotiator, Mockito.times(1)).newHandler(mockHandler);
    }
}

