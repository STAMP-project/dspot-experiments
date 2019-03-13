/**
 * Copyright 2017 The Bazel Authors. All rights reserved.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.google.devtools.build.lib.buildeventservice;


import PublishBuildEventGrpc.PublishBuildEventImplBase;
import Status.INTERNAL;
import Status.OK;
import com.google.devtools.build.lib.buildeventservice.client.BuildEventServiceGrpcClient;
import com.google.devtools.build.v1.PublishBuildToolEventStreamRequest;
import com.google.devtools.build.v1.PublishBuildToolEventStreamResponse;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Tests {@link BuildEventServiceGrpcClient}.
 */
@RunWith(JUnit4.class)
public class BuildEventServiceGrpcClientTest {
    private BuildEventServiceGrpcClient grpcClient;

    @Mock
    private PublishBuildEventImplBase fakeServer;

    private static final StreamObserver<PublishBuildToolEventStreamRequest> NULL_OBSERVER = new StreamObserver<PublishBuildToolEventStreamRequest>() {
        @Override
        public void onNext(PublishBuildToolEventStreamRequest value) {
        }

        @Override
        public void onError(Throwable t) {
        }

        @Override
        public void onCompleted() {
        }
    };

    @Test
    @SuppressWarnings("unchecked")
    public void testImmediateSuccess() throws Exception {
        Mockito.when(fakeServer.publishBuildToolEventStream(ArgumentMatchers.any())).thenAnswer(( invocation) -> {
            StreamObserver<PublishBuildToolEventStreamResponse> responseObserver = ((StreamObserver<PublishBuildToolEventStreamResponse>) (invocation.getArguments()[0]));
            responseObserver.onCompleted();
            return NULL_OBSERVER;
        });
        assertThat(grpcClient.openStream(( ack) -> {
        }).getStatus().get()).isEqualTo(OK);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testImmediateFailure() throws Exception {
        Throwable failure = new io.grpc.StatusException(Status.INTERNAL);
        Mockito.when(fakeServer.publishBuildToolEventStream(ArgumentMatchers.any())).thenAnswer(( invocation) -> {
            StreamObserver<PublishBuildToolEventStreamResponse> responseObserver = ((StreamObserver<PublishBuildToolEventStreamResponse>) (invocation.getArguments()[0]));
            responseObserver.onError(failure);
            return NULL_OBSERVER;
        });
        assertThat(grpcClient.openStream(( ack) -> {
        }).getStatus().get()).isEqualTo(INTERNAL);
    }
}

