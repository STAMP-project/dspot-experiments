/**
 * Copyright 2019 The gRPC Authors
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
package io.grpc.grpclb;


import Attributes.EMPTY;
import ClientStreamTracer.Factory;
import ClientStreamTracer.StreamInfo;
import GrpcAttributes.ATTR_CLIENT_EAG_ATTRS;
import GrpclbConstants.TOKEN_ATTRIBUTE_KEY;
import GrpclbConstants.TOKEN_METADATA_KEY;
import io.grpc.Attributes;
import io.grpc.CallOptions;
import io.grpc.ClientStreamTracer;
import io.grpc.Metadata;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.AdditionalAnswers;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.same;


/**
 * Unit tests for {@link TokenAttachingTracerFactory}.
 */
@RunWith(JUnit4.class)
public class TokenAttachingTracerFactoryTest {
    private static final ClientStreamTracer fakeTracer = new ClientStreamTracer() {};

    private final Factory delegate = Mockito.mock(Factory.class, AdditionalAnswers.delegatesTo(new ClientStreamTracer.Factory() {
        @Override
        public ClientStreamTracer newClientStreamTracer(ClientStreamTracer.StreamInfo info, Metadata headers) {
            return TokenAttachingTracerFactoryTest.fakeTracer;
        }
    }));

    @Test
    public void hasToken() {
        TokenAttachingTracerFactory factory = new TokenAttachingTracerFactory(delegate);
        ClientStreamTracer.StreamInfo info = new ClientStreamTracer.StreamInfo() {
            @Override
            public Attributes getTransportAttrs() {
                Attributes eagAttrs = Attributes.newBuilder().set(TOKEN_ATTRIBUTE_KEY, "token0001").build();
                return Attributes.newBuilder().set(ATTR_CLIENT_EAG_ATTRS, eagAttrs).build();
            }

            @Override
            public CallOptions getCallOptions() {
                return CallOptions.DEFAULT;
            }
        };
        Metadata headers = new Metadata();
        // Preexisting token should be replaced
        headers.put(TOKEN_METADATA_KEY, "preexisting-token");
        ClientStreamTracer tracer = factory.newClientStreamTracer(info, headers);
        Mockito.verify(delegate).newClientStreamTracer(same(info), same(headers));
        assertThat(tracer).isSameAs(TokenAttachingTracerFactoryTest.fakeTracer);
        assertThat(headers.getAll(TOKEN_METADATA_KEY)).containsExactly("token0001");
    }

    @Test
    public void noToken() {
        TokenAttachingTracerFactory factory = new TokenAttachingTracerFactory(delegate);
        ClientStreamTracer.StreamInfo info = new ClientStreamTracer.StreamInfo() {
            @Override
            public Attributes getTransportAttrs() {
                return Attributes.newBuilder().set(ATTR_CLIENT_EAG_ATTRS, EMPTY).build();
            }

            @Override
            public CallOptions getCallOptions() {
                return CallOptions.DEFAULT;
            }
        };
        Metadata headers = new Metadata();
        // Preexisting token should be removed
        headers.put(TOKEN_METADATA_KEY, "preexisting-token");
        ClientStreamTracer tracer = factory.newClientStreamTracer(info, headers);
        Mockito.verify(delegate).newClientStreamTracer(same(info), same(headers));
        assertThat(tracer).isSameAs(TokenAttachingTracerFactoryTest.fakeTracer);
        assertThat(headers.get(TOKEN_METADATA_KEY)).isNull();
    }

    @Test
    public void nullDelegate() {
        TokenAttachingTracerFactory factory = new TokenAttachingTracerFactory(null);
        ClientStreamTracer.StreamInfo info = new ClientStreamTracer.StreamInfo() {
            @Override
            public Attributes getTransportAttrs() {
                return Attributes.newBuilder().set(ATTR_CLIENT_EAG_ATTRS, EMPTY).build();
            }

            @Override
            public CallOptions getCallOptions() {
                return CallOptions.DEFAULT;
            }
        };
        Metadata headers = new Metadata();
        ClientStreamTracer tracer = factory.newClientStreamTracer(info, headers);
        assertThat(tracer).isNotNull();
        assertThat(headers.get(TOKEN_METADATA_KEY)).isNull();
    }
}

