/**
 * Copyright 2014 The gRPC Authors
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
package io.grpc.testing.integration;


import Messages.StreamingOutputCallRequest.Builder;
import Messages.StreamingOutputCallResponse;
import TestServiceGrpc.TestServiceBlockingStub;
import TestUtils.TEST_SERVER_HOST;
import com.google.common.base.Throwables;
import io.grpc.ManagedChannel;
import io.grpc.internal.GrpcUtil;
import io.grpc.internal.testing.StreamRecorder;
import io.grpc.stub.StreamObserver;
import io.grpc.testing.integration.EmptyProtos.Empty;
import java.net.InetSocketAddress;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Integration tests for GRPC over Http2 using the OkHttp framework.
 */
@RunWith(JUnit4.class)
public class Http2OkHttpTest extends AbstractInteropTest {
    private static final String BAD_HOSTNAME = "I.am.a.bad.hostname";

    @Test
    public void receivedDataForFinishedStream() throws Exception {
        Messages.ResponseParameters.Builder responseParameters = Messages.ResponseParameters.newBuilder().setSize(1);
        Messages.StreamingOutputCallRequest.Builder requestBuilder = Messages.StreamingOutputCallRequest.newBuilder();
        for (int i = 0; i < 1000; i++) {
            requestBuilder.addResponseParameters(responseParameters);
        }
        StreamRecorder<Messages.StreamingOutputCallResponse> recorder = StreamRecorder.create();
        StreamObserver<Messages.StreamingOutputCallRequest> requestStream = asyncStub.fullDuplexCall(recorder);
        Messages.StreamingOutputCallRequest request = requestBuilder.build();
        requestStream.onNext(request);
        recorder.firstValue().get();
        requestStream.onError(new Exception("failed"));
        recorder.awaitCompletion();
        Assert.assertEquals(EMPTY, blockingStub.emptyCall(EMPTY));
    }

    @Test
    public void wrongHostNameFailHostnameVerification() throws Exception {
        int port = ((InetSocketAddress) (getListenAddress())).getPort();
        ManagedChannel channel = createChannelBuilder().overrideAuthority(GrpcUtil.authorityFromHostAndPort(Http2OkHttpTest.BAD_HOSTNAME, port)).build();
        TestServiceGrpc.TestServiceBlockingStub blockingStub = TestServiceGrpc.newBlockingStub(channel);
        Throwable actualThrown = null;
        try {
            blockingStub.emptyCall(Empty.getDefaultInstance());
        } catch (Throwable t) {
            actualThrown = t;
        }
        Assert.assertNotNull("The rpc should have been failed due to hostname verification", actualThrown);
        Throwable cause = Throwables.getRootCause(actualThrown);
        Assert.assertTrue(("Failed by unexpected exception: " + cause), (cause instanceof SSLPeerUnverifiedException));
        channel.shutdown();
    }

    @Test
    public void hostnameVerifierWithBadHostname() throws Exception {
        int port = ((InetSocketAddress) (getListenAddress())).getPort();
        ManagedChannel channel = createChannelBuilder().overrideAuthority(GrpcUtil.authorityFromHostAndPort(Http2OkHttpTest.BAD_HOSTNAME, port)).hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        }).build();
        TestServiceGrpc.TestServiceBlockingStub blockingStub = TestServiceGrpc.newBlockingStub(channel);
        blockingStub.emptyCall(Empty.getDefaultInstance());
        channel.shutdown();
    }

    @Test
    public void hostnameVerifierWithCorrectHostname() throws Exception {
        int port = ((InetSocketAddress) (getListenAddress())).getPort();
        ManagedChannel channel = createChannelBuilder().overrideAuthority(GrpcUtil.authorityFromHostAndPort(TEST_SERVER_HOST, port)).hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return false;
            }
        }).build();
        TestServiceGrpc.TestServiceBlockingStub blockingStub = TestServiceGrpc.newBlockingStub(channel);
        Throwable actualThrown = null;
        try {
            blockingStub.emptyCall(Empty.getDefaultInstance());
        } catch (Throwable t) {
            actualThrown = t;
        }
        Assert.assertNotNull("The rpc should have been failed due to hostname verification", actualThrown);
        Throwable cause = Throwables.getRootCause(actualThrown);
        Assert.assertTrue(("Failed by unexpected exception: " + cause), (cause instanceof SSLPeerUnverifiedException));
        channel.shutdown();
    }
}

