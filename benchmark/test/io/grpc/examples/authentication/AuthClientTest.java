/**
 * Copyright 2015 The gRPC Authors
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
package io.grpc.examples.authentication;


import Constant.JWT_METADATA_KEY;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCall.Listener;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.testing.GrpcCleanupRule;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.AdditionalAnswers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;


/**
 * Unit tests for {@link AuthClient} testing the default and non-default tokens
 */
@RunWith(JUnit4.class)
public class AuthClientTest {
    /**
     * This rule manages automatic graceful shutdown for the registered servers and channels at the
     * end of test.
     */
    @Rule
    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

    private final ServerInterceptor mockServerInterceptor = Mockito.mock(ServerInterceptor.class, AdditionalAnswers.delegatesTo(new ServerInterceptor() {
        @Override
        public <ReqT, RespT> Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
            return next.startCall(call, headers);
        }
    }));

    private AuthClient client;

    /**
     * Test default JWT token used.
     */
    @Test
    public void defaultTokenDeliveredToServer() {
        ArgumentCaptor<Metadata> metadataCaptor = ArgumentCaptor.forClass(Metadata.class);
        String retVal = client.greet("default token test");
        Mockito.verify(mockServerInterceptor).interceptCall(<ServerCall<io.grpc.examples.helloworld.HelloRequest, io.grpc.examples.helloworld.HelloReply>>any(), metadataCaptor.capture(), org.mockito.Matchers.<ServerCallHandler<io.grpc.examples.helloworld.HelloRequest, io.grpc.examples.helloworld.HelloReply>>any());
        Assert.assertEquals("my-default-token", metadataCaptor.getValue().get(JWT_METADATA_KEY));
        Assert.assertEquals("AuthClientTest user=default token test", retVal);
    }

    /**
     * Test non-default JWT token used.
     */
    @Test
    public void nonDefaultTokenDeliveredToServer() {
        ArgumentCaptor<Metadata> metadataCaptor = ArgumentCaptor.forClass(Metadata.class);
        client.setTokenValue("non-default-token");
        String retVal = client.greet("non default token test");
        Mockito.verify(mockServerInterceptor).interceptCall(<ServerCall<io.grpc.examples.helloworld.HelloRequest, io.grpc.examples.helloworld.HelloReply>>any(), metadataCaptor.capture(), org.mockito.Matchers.<ServerCallHandler<io.grpc.examples.helloworld.HelloRequest, io.grpc.examples.helloworld.HelloReply>>any());
        Assert.assertEquals("non-default-token", metadataCaptor.getValue().get(JWT_METADATA_KEY));
        Assert.assertEquals("AuthClientTest user=non default token test", retVal);
    }
}

