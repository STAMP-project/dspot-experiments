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
package io.grpc.internal;


import Code.INTERNAL;
import Code.UNAUTHENTICATED;
import Code.UNKNOWN;
import Metadata.ASCII_STRING_MARSHALLER;
import Metadata.Key;
import StatsTraceContext.NOOP;
import Status.CANCELLED;
import Status.OK;
import com.google.common.base.Charsets;
import io.grpc.InternalMetadata;
import io.grpc.Metadata;
import io.grpc.Status;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


/**
 * Unit tests for {@link Http2ClientStreamTransportState}.
 */
@RunWith(JUnit4.class)
public class Http2ClientStreamTransportStateTest {
    @Rule
    public final MockitoRule mocks = MockitoJUnit.rule();

    private final Metadata.Key<String> testStatusMashaller = InternalMetadata.keyOf(":status", ASCII_STRING_MARSHALLER);

    private TransportTracer transportTracer;

    @Mock
    private ClientStreamListener mockListener;

    @Captor
    private ArgumentCaptor<Status> statusCaptor;

    @Test
    public void transportHeadersReceived_notifiesListener() {
        Http2ClientStreamTransportStateTest.BaseTransportState state = new Http2ClientStreamTransportStateTest.BaseTransportState(transportTracer);
        state.setListener(mockListener);
        Metadata headers = new Metadata();
        headers.put(testStatusMashaller, "200");
        headers.put(Key.of("content-type", ASCII_STRING_MARSHALLER), "application/grpc");
        state.transportHeadersReceived(headers);
        Mockito.verify(mockListener, Mockito.never()).closed(ArgumentMatchers.any(Status.class), ArgumentMatchers.same(RpcProgress.PROCESSED), ArgumentMatchers.any(Metadata.class));
        Mockito.verify(mockListener).headersRead(headers);
    }

    @Test
    public void transportHeadersReceived_doesntRequire200() {
        Http2ClientStreamTransportStateTest.BaseTransportState state = new Http2ClientStreamTransportStateTest.BaseTransportState(transportTracer);
        state.setListener(mockListener);
        Metadata headers = new Metadata();
        headers.put(testStatusMashaller, "500");
        headers.put(Key.of("content-type", ASCII_STRING_MARSHALLER), "application/grpc");
        state.transportHeadersReceived(headers);
        Mockito.verify(mockListener, Mockito.never()).closed(ArgumentMatchers.any(Status.class), ArgumentMatchers.same(RpcProgress.PROCESSED), ArgumentMatchers.any(Metadata.class));
        Mockito.verify(mockListener).headersRead(headers);
    }

    @Test
    public void transportHeadersReceived_noHttpStatus() {
        Http2ClientStreamTransportStateTest.BaseTransportState state = new Http2ClientStreamTransportStateTest.BaseTransportState(transportTracer);
        state.setListener(mockListener);
        Metadata headers = new Metadata();
        headers.put(Key.of("content-type", ASCII_STRING_MARSHALLER), "application/grpc");
        state.transportHeadersReceived(headers);
        state.transportDataReceived(ReadableBuffers.empty(), true);
        Mockito.verify(mockListener, Mockito.never()).headersRead(ArgumentMatchers.any(Metadata.class));
        Mockito.verify(mockListener).closed(statusCaptor.capture(), ArgumentMatchers.same(RpcProgress.PROCESSED), ArgumentMatchers.same(headers));
        Assert.assertEquals(INTERNAL, statusCaptor.getValue().getCode());
    }

    @Test
    public void transportHeadersReceived_wrongContentType_200() {
        Http2ClientStreamTransportStateTest.BaseTransportState state = new Http2ClientStreamTransportStateTest.BaseTransportState(transportTracer);
        state.setListener(mockListener);
        Metadata headers = new Metadata();
        headers.put(testStatusMashaller, "200");
        headers.put(Key.of("content-type", ASCII_STRING_MARSHALLER), "text/html");
        state.transportHeadersReceived(headers);
        state.transportDataReceived(ReadableBuffers.empty(), true);
        Mockito.verify(mockListener, Mockito.never()).headersRead(ArgumentMatchers.any(Metadata.class));
        Mockito.verify(mockListener).closed(statusCaptor.capture(), ArgumentMatchers.same(RpcProgress.PROCESSED), ArgumentMatchers.same(headers));
        Assert.assertEquals(UNKNOWN, statusCaptor.getValue().getCode());
        Assert.assertTrue(statusCaptor.getValue().getDescription().contains("200"));
    }

    @Test
    public void transportHeadersReceived_wrongContentType_401() {
        Http2ClientStreamTransportStateTest.BaseTransportState state = new Http2ClientStreamTransportStateTest.BaseTransportState(transportTracer);
        state.setListener(mockListener);
        Metadata headers = new Metadata();
        headers.put(testStatusMashaller, "401");
        headers.put(Key.of("content-type", ASCII_STRING_MARSHALLER), "text/html");
        state.transportHeadersReceived(headers);
        state.transportDataReceived(ReadableBuffers.empty(), true);
        Mockito.verify(mockListener, Mockito.never()).headersRead(ArgumentMatchers.any(Metadata.class));
        Mockito.verify(mockListener).closed(statusCaptor.capture(), ArgumentMatchers.same(RpcProgress.PROCESSED), ArgumentMatchers.same(headers));
        Assert.assertEquals(UNAUTHENTICATED, statusCaptor.getValue().getCode());
        Assert.assertTrue(statusCaptor.getValue().getDescription().contains("401"));
        Assert.assertTrue(statusCaptor.getValue().getDescription().contains("text/html"));
    }

    @Test
    public void transportHeadersReceived_handles_1xx() {
        Http2ClientStreamTransportStateTest.BaseTransportState state = new Http2ClientStreamTransportStateTest.BaseTransportState(transportTracer);
        state.setListener(mockListener);
        Metadata infoHeaders = new Metadata();
        infoHeaders.put(testStatusMashaller, "100");
        state.transportHeadersReceived(infoHeaders);
        Metadata infoHeaders2 = new Metadata();
        infoHeaders2.put(testStatusMashaller, "101");
        state.transportHeadersReceived(infoHeaders2);
        Metadata headers = new Metadata();
        headers.put(testStatusMashaller, "200");
        headers.put(Key.of("content-type", ASCII_STRING_MARSHALLER), "application/grpc");
        state.transportHeadersReceived(headers);
        Mockito.verify(mockListener, Mockito.never()).closed(ArgumentMatchers.any(Status.class), ArgumentMatchers.same(RpcProgress.PROCESSED), ArgumentMatchers.any(Metadata.class));
        Mockito.verify(mockListener).headersRead(headers);
    }

    @Test
    public void transportHeadersReceived_twice() {
        Http2ClientStreamTransportStateTest.BaseTransportState state = new Http2ClientStreamTransportStateTest.BaseTransportState(transportTracer);
        state.setListener(mockListener);
        Metadata headers = new Metadata();
        headers.put(testStatusMashaller, "200");
        headers.put(Key.of("content-type", ASCII_STRING_MARSHALLER), "application/grpc");
        state.transportHeadersReceived(headers);
        Metadata headersAgain = new Metadata();
        state.transportHeadersReceived(headersAgain);
        state.transportDataReceived(ReadableBuffers.empty(), true);
        Mockito.verify(mockListener).headersRead(headers);
        Mockito.verify(mockListener).closed(statusCaptor.capture(), ArgumentMatchers.same(RpcProgress.PROCESSED), ArgumentMatchers.same(headersAgain));
        Assert.assertEquals(INTERNAL, statusCaptor.getValue().getCode());
        Assert.assertTrue(statusCaptor.getValue().getDescription().contains("twice"));
    }

    @Test
    public void transportHeadersReceived_unknownAndTwiceLogsSecondHeaders() {
        Http2ClientStreamTransportStateTest.BaseTransportState state = new Http2ClientStreamTransportStateTest.BaseTransportState(transportTracer);
        state.setListener(mockListener);
        Metadata headers = new Metadata();
        headers.put(testStatusMashaller, "200");
        headers.put(Key.of("content-type", ASCII_STRING_MARSHALLER), "text/html");
        state.transportHeadersReceived(headers);
        Metadata headersAgain = new Metadata();
        String testString = "This is a test";
        headersAgain.put(Key.of("key", ASCII_STRING_MARSHALLER), testString);
        state.transportHeadersReceived(headersAgain);
        state.transportDataReceived(ReadableBuffers.empty(), true);
        Mockito.verify(mockListener, Mockito.never()).headersRead(ArgumentMatchers.any(Metadata.class));
        Mockito.verify(mockListener).closed(statusCaptor.capture(), ArgumentMatchers.same(RpcProgress.PROCESSED), ArgumentMatchers.same(headers));
        Assert.assertEquals(UNKNOWN, statusCaptor.getValue().getCode());
        Assert.assertTrue(statusCaptor.getValue().getDescription().contains(testString));
    }

    @Test
    public void transportDataReceived_noHeaderReceived() {
        Http2ClientStreamTransportStateTest.BaseTransportState state = new Http2ClientStreamTransportStateTest.BaseTransportState(transportTracer);
        state.setListener(mockListener);
        String testString = "This is a test";
        state.transportDataReceived(ReadableBuffers.wrap(testString.getBytes(Charsets.US_ASCII)), true);
        Mockito.verify(mockListener).closed(statusCaptor.capture(), ArgumentMatchers.same(RpcProgress.PROCESSED), ArgumentMatchers.any(Metadata.class));
        Assert.assertEquals(INTERNAL, statusCaptor.getValue().getCode());
    }

    @Test
    public void transportDataReceived_debugData() {
        Http2ClientStreamTransportStateTest.BaseTransportState state = new Http2ClientStreamTransportStateTest.BaseTransportState(transportTracer);
        state.setListener(mockListener);
        Metadata headers = new Metadata();
        headers.put(testStatusMashaller, "200");
        headers.put(Key.of("content-type", ASCII_STRING_MARSHALLER), "text/html");
        state.transportHeadersReceived(headers);
        String testString = "This is a test";
        state.transportDataReceived(ReadableBuffers.wrap(testString.getBytes(Charsets.US_ASCII)), true);
        Mockito.verify(mockListener).closed(statusCaptor.capture(), ArgumentMatchers.same(RpcProgress.PROCESSED), ArgumentMatchers.same(headers));
        Assert.assertTrue(statusCaptor.getValue().getDescription().contains(testString));
    }

    @Test
    public void transportTrailersReceived_notifiesListener() {
        Http2ClientStreamTransportStateTest.BaseTransportState state = new Http2ClientStreamTransportStateTest.BaseTransportState(transportTracer);
        state.setListener(mockListener);
        Metadata trailers = new Metadata();
        trailers.put(testStatusMashaller, "200");
        trailers.put(Key.of("content-type", ASCII_STRING_MARSHALLER), "application/grpc");
        trailers.put(Key.of("grpc-status", ASCII_STRING_MARSHALLER), "0");
        state.transportTrailersReceived(trailers);
        Mockito.verify(mockListener, Mockito.never()).headersRead(ArgumentMatchers.any(Metadata.class));
        Mockito.verify(mockListener).closed(OK, RpcProgress.PROCESSED, trailers);
    }

    @Test
    public void transportTrailersReceived_afterHeaders() {
        Http2ClientStreamTransportStateTest.BaseTransportState state = new Http2ClientStreamTransportStateTest.BaseTransportState(transportTracer);
        state.setListener(mockListener);
        Metadata headers = new Metadata();
        headers.put(testStatusMashaller, "200");
        headers.put(Key.of("content-type", ASCII_STRING_MARSHALLER), "application/grpc");
        state.transportHeadersReceived(headers);
        Metadata trailers = new Metadata();
        trailers.put(Key.of("grpc-status", ASCII_STRING_MARSHALLER), "0");
        state.transportTrailersReceived(trailers);
        Mockito.verify(mockListener).headersRead(headers);
        Mockito.verify(mockListener).closed(OK, RpcProgress.PROCESSED, trailers);
    }

    @Test
    public void transportTrailersReceived_observesStatus() {
        Http2ClientStreamTransportStateTest.BaseTransportState state = new Http2ClientStreamTransportStateTest.BaseTransportState(transportTracer);
        state.setListener(mockListener);
        Metadata trailers = new Metadata();
        trailers.put(testStatusMashaller, "200");
        trailers.put(Key.of("content-type", ASCII_STRING_MARSHALLER), "application/grpc");
        trailers.put(Key.of("grpc-status", ASCII_STRING_MARSHALLER), "1");
        state.transportTrailersReceived(trailers);
        Mockito.verify(mockListener, Mockito.never()).headersRead(ArgumentMatchers.any(Metadata.class));
        Mockito.verify(mockListener).closed(CANCELLED, RpcProgress.PROCESSED, trailers);
    }

    @Test
    public void transportTrailersReceived_missingStatusUsesHttpStatus() {
        Http2ClientStreamTransportStateTest.BaseTransportState state = new Http2ClientStreamTransportStateTest.BaseTransportState(transportTracer);
        state.setListener(mockListener);
        Metadata trailers = new Metadata();
        trailers.put(testStatusMashaller, "401");
        trailers.put(Key.of("content-type", ASCII_STRING_MARSHALLER), "application/grpc");
        state.transportTrailersReceived(trailers);
        Mockito.verify(mockListener, Mockito.never()).headersRead(ArgumentMatchers.any(Metadata.class));
        Mockito.verify(mockListener).closed(statusCaptor.capture(), ArgumentMatchers.same(RpcProgress.PROCESSED), ArgumentMatchers.same(trailers));
        Assert.assertEquals(UNAUTHENTICATED, statusCaptor.getValue().getCode());
        Assert.assertTrue(statusCaptor.getValue().getDescription().contains("401"));
    }

    @Test
    public void transportTrailersReceived_missingHttpStatus() {
        Http2ClientStreamTransportStateTest.BaseTransportState state = new Http2ClientStreamTransportStateTest.BaseTransportState(transportTracer);
        state.setListener(mockListener);
        Metadata trailers = new Metadata();
        trailers.put(Key.of("content-type", ASCII_STRING_MARSHALLER), "application/grpc");
        trailers.put(Key.of("grpc-status", ASCII_STRING_MARSHALLER), "0");
        state.transportTrailersReceived(trailers);
        Mockito.verify(mockListener, Mockito.never()).headersRead(ArgumentMatchers.any(Metadata.class));
        Mockito.verify(mockListener).closed(statusCaptor.capture(), ArgumentMatchers.same(RpcProgress.PROCESSED), ArgumentMatchers.same(trailers));
        Assert.assertEquals(INTERNAL, statusCaptor.getValue().getCode());
    }

    @Test
    public void transportTrailersReceived_missingStatusAndMissingHttpStatus() {
        Http2ClientStreamTransportStateTest.BaseTransportState state = new Http2ClientStreamTransportStateTest.BaseTransportState(transportTracer);
        state.setListener(mockListener);
        Metadata trailers = new Metadata();
        trailers.put(Key.of("content-type", ASCII_STRING_MARSHALLER), "application/grpc");
        state.transportTrailersReceived(trailers);
        Mockito.verify(mockListener, Mockito.never()).headersRead(ArgumentMatchers.any(Metadata.class));
        Mockito.verify(mockListener).closed(statusCaptor.capture(), ArgumentMatchers.same(RpcProgress.PROCESSED), ArgumentMatchers.same(trailers));
        Assert.assertEquals(INTERNAL, statusCaptor.getValue().getCode());
    }

    @Test
    public void transportTrailersReceived_missingStatusAfterHeadersIgnoresHttpStatus() {
        Http2ClientStreamTransportStateTest.BaseTransportState state = new Http2ClientStreamTransportStateTest.BaseTransportState(transportTracer);
        state.setListener(mockListener);
        Metadata headers = new Metadata();
        headers.put(testStatusMashaller, "200");
        headers.put(Key.of("content-type", ASCII_STRING_MARSHALLER), "application/grpc");
        state.transportHeadersReceived(headers);
        Metadata trailers = new Metadata();
        trailers.put(testStatusMashaller, "401");
        state.transportTrailersReceived(trailers);
        Mockito.verify(mockListener).headersRead(headers);
        Mockito.verify(mockListener).closed(statusCaptor.capture(), ArgumentMatchers.same(RpcProgress.PROCESSED), ArgumentMatchers.same(trailers));
        Assert.assertEquals(UNKNOWN, statusCaptor.getValue().getCode());
    }

    private static class BaseTransportState extends Http2ClientStreamTransportState {
        public BaseTransportState(TransportTracer transportTracer) {
            super(GrpcUtil.DEFAULT_MAX_MESSAGE_SIZE, NOOP, transportTracer);
        }

        @Override
        protected void http2ProcessingFailed(Status status, boolean stopDelivery, Metadata trailers) {
            transportReportStatus(status, stopDelivery, trailers);
        }

        @Override
        public void deframeFailed(Throwable cause) {
        }

        @Override
        public void bytesRead(int processedBytes) {
        }

        @Override
        public void runOnTransportThread(Runnable r) {
            r.run();
        }
    }
}

