/**
 * Copyright 2017 The gRPC Authors
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


import BlankSpan.INSTANCE;
import CallOptions.DEFAULT;
import CallOptions.Key;
import CensusTracingModule.ClientCallTracer;
import ClientStreamTracer.StreamInfo;
import Context.ROOT;
import ContextUtils.CONTEXT_SPAN_KEY;
import DeprecatedCensusConstants.RPC_CLIENT_ERROR_COUNT;
import DeprecatedCensusConstants.RPC_CLIENT_FINISHED_COUNT;
import DeprecatedCensusConstants.RPC_CLIENT_REQUEST_BYTES;
import DeprecatedCensusConstants.RPC_CLIENT_REQUEST_COUNT;
import DeprecatedCensusConstants.RPC_CLIENT_RESPONSE_BYTES;
import DeprecatedCensusConstants.RPC_CLIENT_RESPONSE_COUNT;
import DeprecatedCensusConstants.RPC_CLIENT_ROUNDTRIP_LATENCY;
import DeprecatedCensusConstants.RPC_CLIENT_SERVER_ELAPSED_TIME;
import DeprecatedCensusConstants.RPC_CLIENT_STARTED_COUNT;
import DeprecatedCensusConstants.RPC_CLIENT_UNCOMPRESSED_REQUEST_BYTES;
import DeprecatedCensusConstants.RPC_CLIENT_UNCOMPRESSED_RESPONSE_BYTES;
import DeprecatedCensusConstants.RPC_METHOD;
import DeprecatedCensusConstants.RPC_STATUS;
import Metadata.BINARY_BYTE_MARSHALLER;
import MethodDescriptor.MethodType.UNKNOWN;
import ServerStreamTracer.Factory;
import SpanContext.INVALID;
import StatsTestUtils.MetricsRecord;
import Status.CANCELLED;
import Status.Code;
import Status.DEADLINE_EXCEEDED;
import Status.OK;
import Type.RECEIVED;
import Type.SENT;
import io.grpc.Attributes;
import io.grpc.CallOptions;
import io.grpc.ClientCall;
import io.grpc.ClientStreamTracer;
import io.grpc.Context;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.ServerCall;
import io.grpc.ServerStreamTracer;
import io.grpc.Status;
import io.grpc.internal.testing.StatsTestUtils;
import io.grpc.internal.testing.StatsTestUtils.FakeStatsRecorder;
import io.grpc.internal.testing.StatsTestUtils.FakeTagContextBinarySerializer;
import io.grpc.internal.testing.StatsTestUtils.FakeTagger;
import io.grpc.internal.testing.StatsTestUtils.MockableSpan;
import io.grpc.testing.GrpcServerRule;
import io.opencensus.tags.TagValue;
import io.opencensus.trace.EndSpanOptions;
import io.opencensus.trace.MessageEvent;
import io.opencensus.trace.Span;
import io.opencensus.trace.SpanBuilder;
import io.opencensus.trace.SpanContext;
import io.opencensus.trace.Tracer;
import io.opencensus.trace.propagation.BinaryFormat;
import io.opencensus.trace.propagation.SpanContextParseException;
import java.io.InputStream;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


/**
 * Test for {@link CensusStatsModule} and {@link CensusTracingModule}.
 */
@RunWith(JUnit4.class)
public class CensusModulesTest {
    private static final CallOptions.Key<String> CUSTOM_OPTION = Key.createWithDefault("option1", "default");

    private static final CallOptions CALL_OPTIONS = DEFAULT.withOption(io.grpc.internal.CUSTOM_OPTION, "customvalue");

    private static final StreamInfo STREAM_INFO = new ClientStreamTracer.StreamInfo() {
        @Override
        public Attributes getTransportAttrs() {
            return Attributes.EMPTY;
        }

        @Override
        public CallOptions getCallOptions() {
            return CallOptions.DEFAULT;
        }
    };

    private static class StringInputStream extends InputStream {
        final String string;

        StringInputStream(String string) {
            this.string = string;
        }

        @Override
        public int read() {
            // InProcessTransport doesn't actually read bytes from the InputStream.  The InputStream is
            // passed to the InProcess server and consumed by MARSHALLER.parse().
            throw new UnsupportedOperationException("Should not be called");
        }
    }

    private static final MethodDescriptor.Marshaller<String> MARSHALLER = new MethodDescriptor.Marshaller<String>() {
        @Override
        public InputStream stream(String value) {
            return new io.grpc.internal.StringInputStream(value);
        }

        @Override
        public String parse(InputStream stream) {
            return ((io.grpc.internal.StringInputStream) (stream)).string;
        }
    };

    @Rule
    public final MockitoRule mocks = MockitoJUnit.rule();

    private final MethodDescriptor<String, String> method = MethodDescriptor.<String, String>newBuilder().setType(UNKNOWN).setRequestMarshaller(io.grpc.internal.MARSHALLER).setResponseMarshaller(io.grpc.internal.MARSHALLER).setFullMethodName("package1.service2/method3").build();

    private final MethodDescriptor<String, String> sampledMethod = method.toBuilder().setSampledToLocalTracing(true).build();

    private final FakeClock fakeClock = new FakeClock();

    private final FakeTagger tagger = new FakeTagger();

    private final FakeTagContextBinarySerializer tagCtxSerializer = new FakeTagContextBinarySerializer();

    private final FakeStatsRecorder statsRecorder = new FakeStatsRecorder();

    private final Random random = new Random(1234);

    private final Span fakeClientParentSpan = MockableSpan.generateRandomSpan(random);

    private final Span spyClientSpan = Mockito.spy(MockableSpan.generateRandomSpan(random));

    private final SpanContext fakeClientSpanContext = spyClientSpan.getContext();

    private final Span spyServerSpan = Mockito.spy(MockableSpan.generateRandomSpan(random));

    private final byte[] binarySpanContext = new byte[]{ 3, 1, 5 };

    private final SpanBuilder spyClientSpanBuilder = Mockito.spy(new MockableSpan.Builder());

    private final SpanBuilder spyServerSpanBuilder = Mockito.spy(new MockableSpan.Builder());

    @Rule
    public final GrpcServerRule grpcServerRule = new GrpcServerRule().directExecutor();

    @Mock
    private Tracer tracer;

    @Mock
    private BinaryFormat mockTracingPropagationHandler;

    @Mock
    private ClientCall.Listener<String> mockClientCallListener;

    @Mock
    private ServerCall.Listener<String> mockServerCallListener;

    @Captor
    private ArgumentCaptor<Status> statusCaptor;

    @Captor
    private ArgumentCaptor<MessageEvent> messageEventCaptor;

    private CensusStatsModule censusStats;

    private CensusTracingModule censusTracing;

    @Test
    public void clientInterceptorNoCustomTag() {
        testClientInterceptors(false);
    }

    @Test
    public void clientInterceptorCustomTag() {
        testClientInterceptors(true);
    }

    @Test
    public void clientBasicStatsDefaultContext_starts_finishes_noRealTime() {
        subtestClientBasicStatsDefaultContext(true, true, false);
    }

    @Test
    public void clientBasicStatsDefaultContext_starts_noFinishes_noRealTime() {
        subtestClientBasicStatsDefaultContext(true, false, false);
    }

    @Test
    public void clientBasicStatsDefaultContext_noStarts_finishes_noRealTime() {
        subtestClientBasicStatsDefaultContext(false, true, false);
    }

    @Test
    public void clientBasicStatsDefaultContext_noStarts_noFinishes_noRealTime() {
        subtestClientBasicStatsDefaultContext(false, false, false);
    }

    @Test
    public void clientBasicStatsDefaultContext_starts_finishes_realTime() {
        subtestClientBasicStatsDefaultContext(true, true, true);
    }

    @Test
    public void clientBasicTracingDefaultSpan() {
        CensusTracingModule.ClientCallTracer callTracer = censusTracing.newClientCallTracer(null, method);
        Metadata headers = new Metadata();
        ClientStreamTracer clientStreamTracer = callTracer.newClientStreamTracer(CensusModulesTest.STREAM_INFO, headers);
        Mockito.verify(tracer).spanBuilderWithExplicitParent(ArgumentMatchers.eq("Sent.package1.service2.method3"), ArgumentMatchers.isNull(Span.class));
        Mockito.verify(spyClientSpan, Mockito.never()).end(ArgumentMatchers.any(EndSpanOptions.class));
        clientStreamTracer.outboundMessage(0);
        clientStreamTracer.outboundMessageSent(0, 882, (-1));
        clientStreamTracer.inboundMessage(0);
        clientStreamTracer.outboundMessage(1);
        clientStreamTracer.outboundMessageSent(1, (-1), 27);
        clientStreamTracer.inboundMessageRead(0, 255, 90);
        clientStreamTracer.streamClosed(OK);
        callTracer.callEnded(OK);
        InOrder inOrder = Mockito.inOrder(spyClientSpan);
        inOrder.verify(spyClientSpan, Mockito.times(3)).addMessageEvent(messageEventCaptor.capture());
        List<MessageEvent> events = messageEventCaptor.getAllValues();
        Assert.assertEquals(MessageEvent.builder(SENT, 0).setCompressedMessageSize(882).build(), events.get(0));
        Assert.assertEquals(MessageEvent.builder(SENT, 1).setUncompressedMessageSize(27).build(), events.get(1));
        Assert.assertEquals(MessageEvent.builder(RECEIVED, 0).setCompressedMessageSize(255).setUncompressedMessageSize(90).build(), events.get(2));
        inOrder.verify(spyClientSpan).end(EndSpanOptions.builder().setStatus(io.opencensus.trace.Status.OK).setSampleToLocalSpanStore(false).build());
        Mockito.verifyNoMoreInteractions(spyClientSpan);
        Mockito.verifyNoMoreInteractions(tracer);
    }

    @Test
    public void clientTracingSampledToLocalSpanStore() {
        CensusTracingModule.ClientCallTracer callTracer = censusTracing.newClientCallTracer(null, sampledMethod);
        callTracer.callEnded(OK);
        Mockito.verify(spyClientSpan).end(EndSpanOptions.builder().setStatus(io.opencensus.trace.Status.OK).setSampleToLocalSpanStore(true).build());
    }

    @Test
    public void clientStreamNeverCreatedStillRecordStats() {
        CensusStatsModule.ClientCallTracer callTracer = censusStats.newClientCallTracer(tagger.empty(), method.getFullMethodName());
        fakeClock.forwardTime(3000, TimeUnit.MILLISECONDS);
        callTracer.callEnded(DEADLINE_EXCEEDED.withDescription("3 seconds"));
        // Upstart record
        StatsTestUtils.MetricsRecord record = statsRecorder.pollRecord();
        Assert.assertNotNull(record);
        CensusModulesTest.assertNoServerContent(record);
        Assert.assertEquals(1, record.tags.size());
        TagValue methodTagOld = record.tags.get(RPC_METHOD);
        Assert.assertEquals(method.getFullMethodName(), methodTagOld.asString());
        Assert.assertEquals(1, record.getMetricAsLongOrFail(RPC_CLIENT_STARTED_COUNT));
        // Completion record
        record = statsRecorder.pollRecord();
        Assert.assertNotNull(record);
        CensusModulesTest.assertNoServerContent(record);
        methodTagOld = record.tags.get(RPC_METHOD);
        Assert.assertEquals(method.getFullMethodName(), methodTagOld.asString());
        TagValue statusTagOld = record.tags.get(RPC_STATUS);
        Assert.assertEquals(Status.Code.DEADLINE_EXCEEDED.toString(), statusTagOld.asString());
        Assert.assertEquals(1, record.getMetricAsLongOrFail(RPC_CLIENT_FINISHED_COUNT));
        Assert.assertEquals(1, record.getMetricAsLongOrFail(RPC_CLIENT_ERROR_COUNT));
        Assert.assertEquals(0, record.getMetricAsLongOrFail(RPC_CLIENT_REQUEST_COUNT));
        Assert.assertEquals(0, record.getMetricAsLongOrFail(RPC_CLIENT_REQUEST_BYTES));
        Assert.assertEquals(0, record.getMetricAsLongOrFail(RPC_CLIENT_UNCOMPRESSED_REQUEST_BYTES));
        Assert.assertEquals(0, record.getMetricAsLongOrFail(RPC_CLIENT_RESPONSE_COUNT));
        Assert.assertEquals(0, record.getMetricAsLongOrFail(RPC_CLIENT_RESPONSE_BYTES));
        Assert.assertEquals(0, record.getMetricAsLongOrFail(RPC_CLIENT_UNCOMPRESSED_RESPONSE_BYTES));
        Assert.assertEquals(3000, record.getMetricAsLongOrFail(RPC_CLIENT_ROUNDTRIP_LATENCY));
        Assert.assertNull(record.getMetric(RPC_CLIENT_SERVER_ELAPSED_TIME));
    }

    @Test
    public void clientStreamNeverCreatedStillRecordTracing() {
        CensusTracingModule.ClientCallTracer callTracer = censusTracing.newClientCallTracer(fakeClientParentSpan, method);
        Mockito.verify(tracer).spanBuilderWithExplicitParent(ArgumentMatchers.eq("Sent.package1.service2.method3"), ArgumentMatchers.same(fakeClientParentSpan));
        Mockito.verify(spyClientSpanBuilder).setRecordEvents(ArgumentMatchers.eq(true));
        callTracer.callEnded(DEADLINE_EXCEEDED.withDescription("3 seconds"));
        Mockito.verify(spyClientSpan).end(EndSpanOptions.builder().setStatus(io.opencensus.trace.Status.DEADLINE_EXCEEDED.withDescription("3 seconds")).setSampleToLocalSpanStore(false).build());
        Mockito.verifyNoMoreInteractions(spyClientSpan);
    }

    @Test
    public void statsHeadersPropagateTags_record() {
        subtestStatsHeadersPropagateTags(true, true);
    }

    @Test
    public void statsHeadersPropagateTags_notRecord() {
        subtestStatsHeadersPropagateTags(true, false);
    }

    @Test
    public void statsHeadersNotPropagateTags_record() {
        subtestStatsHeadersPropagateTags(false, true);
    }

    @Test
    public void statsHeadersNotPropagateTags_notRecord() {
        subtestStatsHeadersPropagateTags(false, false);
    }

    @Test
    public void statsHeadersNotPropagateDefaultContext() {
        CensusStatsModule.ClientCallTracer callTracer = censusStats.newClientCallTracer(tagger.empty(), method.getFullMethodName());
        Metadata headers = new Metadata();
        callTracer.newClientStreamTracer(CensusModulesTest.STREAM_INFO, headers);
        Assert.assertFalse(headers.containsKey(censusStats.statsHeader));
        // Clear recorded stats to satisfy the assertions in wrapUp()
        statsRecorder.rolloverRecords();
    }

    @Test
    public void statsHeaderMalformed() {
        // Construct a malformed header and make sure parsing it will throw
        byte[] statsHeaderValue = new byte[]{ 1 };
        Metadata.Key<byte[]> arbitraryStatsHeader = Metadata.Key.of("grpc-tags-bin", BINARY_BYTE_MARSHALLER);
        try {
            tagCtxSerializer.fromByteArray(statsHeaderValue);
            Assert.fail("Should have thrown");
        } catch (Exception e) {
            // Expected
        }
        // But the header key will return a default context for it
        Metadata headers = new Metadata();
        Assert.assertNull(headers.get(censusStats.statsHeader));
        headers.put(arbitraryStatsHeader, statsHeaderValue);
        Assert.assertSame(tagger.empty(), headers.get(censusStats.statsHeader));
    }

    @Test
    public void traceHeadersPropagateSpanContext() throws Exception {
        CensusTracingModule.ClientCallTracer callTracer = censusTracing.newClientCallTracer(fakeClientParentSpan, method);
        Metadata headers = new Metadata();
        callTracer.newClientStreamTracer(CensusModulesTest.STREAM_INFO, headers);
        Mockito.verify(mockTracingPropagationHandler).toByteArray(ArgumentMatchers.same(fakeClientSpanContext));
        Mockito.verifyNoMoreInteractions(mockTracingPropagationHandler);
        Mockito.verify(tracer).spanBuilderWithExplicitParent(ArgumentMatchers.eq("Sent.package1.service2.method3"), ArgumentMatchers.same(fakeClientParentSpan));
        Mockito.verify(spyClientSpanBuilder).setRecordEvents(ArgumentMatchers.eq(true));
        Mockito.verifyNoMoreInteractions(tracer);
        Assert.assertTrue(headers.containsKey(censusTracing.tracingHeader));
        ServerStreamTracer serverTracer = censusTracing.getServerTracerFactory().newServerStreamTracer(method.getFullMethodName(), headers);
        Mockito.verify(mockTracingPropagationHandler).fromByteArray(ArgumentMatchers.same(binarySpanContext));
        Mockito.verify(tracer).spanBuilderWithRemoteParent(ArgumentMatchers.eq("Recv.package1.service2.method3"), ArgumentMatchers.same(spyClientSpan.getContext()));
        Mockito.verify(spyServerSpanBuilder).setRecordEvents(ArgumentMatchers.eq(true));
        Context filteredContext = serverTracer.filterContext(ROOT);
        Assert.assertSame(spyServerSpan, CONTEXT_SPAN_KEY.get(filteredContext));
    }

    @Test
    public void traceHeaders_propagateSpanContext() throws Exception {
        CensusTracingModule.ClientCallTracer callTracer = censusTracing.newClientCallTracer(fakeClientParentSpan, method);
        Metadata headers = new Metadata();
        callTracer.newClientStreamTracer(CensusModulesTest.STREAM_INFO, headers);
        assertThat(headers.keys()).isNotEmpty();
    }

    @Test
    public void traceHeaders_missingCensusImpl_notPropagateSpanContext() throws Exception {
        Mockito.reset(spyClientSpanBuilder);
        Mockito.when(spyClientSpanBuilder.startSpan()).thenReturn(INSTANCE);
        Metadata headers = new Metadata();
        CensusTracingModule.ClientCallTracer callTracer = censusTracing.newClientCallTracer(INSTANCE, method);
        callTracer.newClientStreamTracer(CensusModulesTest.STREAM_INFO, headers);
        assertThat(headers.keys()).isEmpty();
    }

    @Test
    public void traceHeaders_clientMissingCensusImpl_preservingHeaders() throws Exception {
        Mockito.reset(spyClientSpanBuilder);
        Mockito.when(spyClientSpanBuilder.startSpan()).thenReturn(INSTANCE);
        Metadata headers = new Metadata();
        headers.put(Metadata.Key.of("never-used-key-bin", BINARY_BYTE_MARSHALLER), new byte[]{  });
        Set<String> originalHeaderKeys = new java.util.HashSet(headers.keys());
        CensusTracingModule.ClientCallTracer callTracer = censusTracing.newClientCallTracer(INSTANCE, method);
        callTracer.newClientStreamTracer(CensusModulesTest.STREAM_INFO, headers);
        assertThat(headers.keys()).containsExactlyElementsIn(originalHeaderKeys);
    }

    @Test
    public void traceHeaderMalformed() throws Exception {
        // As comparison, normal header parsing
        Metadata headers = new Metadata();
        headers.put(censusTracing.tracingHeader, fakeClientSpanContext);
        // mockTracingPropagationHandler was stubbed to always return fakeServerParentSpanContext
        Assert.assertSame(spyClientSpan.getContext(), headers.get(censusTracing.tracingHeader));
        // Make BinaryPropagationHandler always throw when parsing the header
        Mockito.when(mockTracingPropagationHandler.fromByteArray(ArgumentMatchers.any(byte[].class))).thenThrow(new SpanContextParseException("Malformed header"));
        headers = new Metadata();
        Assert.assertNull(headers.get(censusTracing.tracingHeader));
        headers.put(censusTracing.tracingHeader, fakeClientSpanContext);
        Assert.assertSame(INVALID, headers.get(censusTracing.tracingHeader));
        Assert.assertNotSame(spyClientSpan.getContext(), INVALID);
        // A null Span is used as the parent in this case
        censusTracing.getServerTracerFactory().newServerStreamTracer(method.getFullMethodName(), headers);
        Mockito.verify(tracer).spanBuilderWithRemoteParent(ArgumentMatchers.eq("Recv.package1.service2.method3"), ArgumentMatchers.isNull(SpanContext.class));
        Mockito.verify(spyServerSpanBuilder).setRecordEvents(ArgumentMatchers.eq(true));
    }

    @Test
    public void serverBasicStatsNoHeaders_starts_finishes_noRealTime() {
        subtestServerBasicStatsNoHeaders(true, true, false);
    }

    @Test
    public void serverBasicStatsNoHeaders_starts_noFinishes_noRealTime() {
        subtestServerBasicStatsNoHeaders(true, false, false);
    }

    @Test
    public void serverBasicStatsNoHeaders_noStarts_finishes_noRealTime() {
        subtestServerBasicStatsNoHeaders(false, true, false);
    }

    @Test
    public void serverBasicStatsNoHeaders_noStarts_noFinishes_noRealTime() {
        subtestServerBasicStatsNoHeaders(false, false, false);
    }

    @Test
    public void serverBasicStatsNoHeaders_starts_finishes_realTime() {
        subtestServerBasicStatsNoHeaders(true, true, true);
    }

    @Test
    public void serverBasicTracingNoHeaders() {
        ServerStreamTracer.Factory tracerFactory = censusTracing.getServerTracerFactory();
        ServerStreamTracer serverStreamTracer = tracerFactory.newServerStreamTracer(method.getFullMethodName(), new Metadata());
        Mockito.verifyZeroInteractions(mockTracingPropagationHandler);
        Mockito.verify(tracer).spanBuilderWithRemoteParent(ArgumentMatchers.eq("Recv.package1.service2.method3"), ArgumentMatchers.isNull(SpanContext.class));
        Mockito.verify(spyServerSpanBuilder).setRecordEvents(ArgumentMatchers.eq(true));
        Context filteredContext = serverStreamTracer.filterContext(ROOT);
        Assert.assertSame(spyServerSpan, CONTEXT_SPAN_KEY.get(filteredContext));
        serverStreamTracer.serverCallStarted(new ServerCallInfoImpl(method, Attributes.EMPTY, null));
        Mockito.verify(spyServerSpan, Mockito.never()).end(ArgumentMatchers.any(EndSpanOptions.class));
        serverStreamTracer.outboundMessage(0);
        serverStreamTracer.outboundMessageSent(0, 882, (-1));
        serverStreamTracer.inboundMessage(0);
        serverStreamTracer.outboundMessage(1);
        serverStreamTracer.outboundMessageSent(1, (-1), 27);
        serverStreamTracer.inboundMessageRead(0, 255, 90);
        serverStreamTracer.streamClosed(CANCELLED);
        InOrder inOrder = Mockito.inOrder(spyServerSpan);
        inOrder.verify(spyServerSpan, Mockito.times(3)).addMessageEvent(messageEventCaptor.capture());
        List<MessageEvent> events = messageEventCaptor.getAllValues();
        Assert.assertEquals(MessageEvent.builder(SENT, 0).setCompressedMessageSize(882).build(), events.get(0));
        Assert.assertEquals(MessageEvent.builder(SENT, 1).setUncompressedMessageSize(27).build(), events.get(1));
        Assert.assertEquals(MessageEvent.builder(RECEIVED, 0).setCompressedMessageSize(255).setUncompressedMessageSize(90).build(), events.get(2));
        inOrder.verify(spyServerSpan).end(EndSpanOptions.builder().setStatus(io.opencensus.trace.Status.CANCELLED).setSampleToLocalSpanStore(false).build());
        Mockito.verifyNoMoreInteractions(spyServerSpan);
    }

    @Test
    public void serverTracingSampledToLocalSpanStore() {
        ServerStreamTracer.Factory tracerFactory = censusTracing.getServerTracerFactory();
        ServerStreamTracer serverStreamTracer = tracerFactory.newServerStreamTracer(sampledMethod.getFullMethodName(), new Metadata());
        serverStreamTracer.filterContext(ROOT);
        serverStreamTracer.serverCallStarted(new ServerCallInfoImpl(sampledMethod, Attributes.EMPTY, null));
        serverStreamTracer.streamClosed(CANCELLED);
        Mockito.verify(spyServerSpan).end(EndSpanOptions.builder().setStatus(io.opencensus.trace.Status.CANCELLED).setSampleToLocalSpanStore(true).build());
    }

    @Test
    public void serverTracingNotSampledToLocalSpanStore_whenServerCallNotCreated() {
        ServerStreamTracer.Factory tracerFactory = censusTracing.getServerTracerFactory();
        ServerStreamTracer serverStreamTracer = tracerFactory.newServerStreamTracer(sampledMethod.getFullMethodName(), new Metadata());
        serverStreamTracer.streamClosed(CANCELLED);
        Mockito.verify(spyServerSpan).end(EndSpanOptions.builder().setStatus(io.opencensus.trace.Status.CANCELLED).setSampleToLocalSpanStore(false).build());
    }

    @Test
    public void convertToTracingStatus() {
        // Without description
        for (Status.Code grpcCode : Code.values()) {
            Status grpcStatus = Status.fromCode(grpcCode);
            io.opencensus.trace.Status tracingStatus = CensusTracingModule.convertStatus(grpcStatus);
            Assert.assertEquals(grpcCode.toString(), tracingStatus.getCanonicalCode().toString());
            Assert.assertNull(tracingStatus.getDescription());
        }
        // With description
        for (Status.Code grpcCode : Code.values()) {
            Status grpcStatus = Status.fromCode(grpcCode).withDescription("This is my description");
            io.opencensus.trace.Status tracingStatus = CensusTracingModule.convertStatus(grpcStatus);
            Assert.assertEquals(grpcCode.toString(), tracingStatus.getCanonicalCode().toString());
            Assert.assertEquals(grpcStatus.getDescription(), tracingStatus.getDescription());
        }
    }

    @Test
    public void generateTraceSpanName() {
        Assert.assertEquals("Sent.io.grpc.Foo", CensusTracingModule.generateTraceSpanName(false, "io.grpc/Foo"));
        Assert.assertEquals("Recv.io.grpc.Bar", CensusTracingModule.generateTraceSpanName(true, "io.grpc/Bar"));
    }
}

