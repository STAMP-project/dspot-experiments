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
package io.grpc.examples.header;


import HeaderServerInterceptor.CUSTOM_HEADER_KEY;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.examples.helloworld.GreeterGrpc;
import io.grpc.examples.helloworld.GreeterGrpc.GreeterBlockingStub;
import io.grpc.examples.helloworld.HelloRequest;
import io.grpc.testing.GrpcCleanupRule;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.AdditionalAnswers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;


/**
 * Unit tests for {@link HeaderClientInterceptor}.
 * For demonstrating how to write gRPC unit test only.
 * Not intended to provide a high code coverage or to test every major usecase.
 *
 * <p>For basic unit test examples see {@link io.grpc.examples.helloworld.HelloWorldClientTest} and
 * {@link io.grpc.examples.helloworld.HelloWorldServerTest}.
 */
@RunWith(JUnit4.class)
public class HeaderServerInterceptorTest {
    /**
     * This rule manages automatic graceful shutdown for the registered servers and channels at the
     * end of test.
     */
    @Rule
    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

    private Channel channel;

    @Test
    public void serverHeaderDeliveredToClient() {
        class SpyingClientInterceptor implements ClientInterceptor {
            ClientCall.Listener<?> spyListener;

            @Override
            public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
                return new io.grpc.ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {
                    @Override
                    public void start(Listener<RespT> responseListener, Metadata headers) {
                        spyListener = responseListener = Mockito.mock(ClientCall.Listener.class, AdditionalAnswers.delegatesTo(responseListener));
                        super.start(responseListener, headers);
                    }
                };
            }
        }
        SpyingClientInterceptor clientInterceptor = new SpyingClientInterceptor();
        GreeterBlockingStub blockingStub = GreeterGrpc.newBlockingStub(channel).withInterceptors(clientInterceptor);
        ArgumentCaptor<Metadata> metadataCaptor = ArgumentCaptor.forClass(Metadata.class);
        blockingStub.sayHello(HelloRequest.getDefaultInstance());
        Assert.assertNotNull(clientInterceptor.spyListener);
        Mockito.verify(clientInterceptor.spyListener).onHeaders(metadataCaptor.capture());
        Assert.assertEquals("customRespondValue", metadataCaptor.getValue().get(CUSTOM_HEADER_KEY));
    }
}

