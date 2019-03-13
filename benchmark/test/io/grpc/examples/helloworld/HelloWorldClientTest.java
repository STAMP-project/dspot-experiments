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
package io.grpc.examples.helloworld;


import GreeterGrpc.GreeterImplBase;
import io.grpc.stub.StreamObserver;
import io.grpc.testing.GrpcCleanupRule;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.AdditionalAnswers;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Matchers;
import org.mockito.Mockito;


/**
 * Unit tests for {@link HelloWorldClient}.
 * For demonstrating how to write gRPC unit test only.
 * Not intended to provide a high code coverage or to test every major usecase.
 *
 * <p>For more unit test examples see {@link io.grpc.examples.routeguide.RouteGuideClientTest} and
 * {@link io.grpc.examples.routeguide.RouteGuideServerTest}.
 */
@RunWith(JUnit4.class)
public class HelloWorldClientTest {
    /**
     * This rule manages automatic graceful shutdown for the registered servers and channels at the
     * end of test.
     */
    @Rule
    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

    private final GreeterImplBase serviceImpl = Mockito.mock(GreeterImplBase.class, AdditionalAnswers.delegatesTo(new GreeterGrpc.GreeterImplBase() {}));

    private HelloWorldClient client;

    /**
     * To test the client, call from the client against the fake server, and verify behaviors or state
     * changes from the server side.
     */
    @Test
    public void greet_messageDeliveredToServer() {
        ArgumentCaptor<HelloRequest> requestCaptor = ArgumentCaptor.forClass(HelloRequest.class);
        client.greet("test name");
        Mockito.verify(serviceImpl).sayHello(requestCaptor.capture(), Matchers.<StreamObserver<HelloReply>>any());
        Assert.assertEquals("test name", requestCaptor.getValue().getName());
    }
}

