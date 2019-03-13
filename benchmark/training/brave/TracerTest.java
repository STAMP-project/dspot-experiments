package brave;


import B3Propagation.FACTORY;
import Kind.CLIENT;
import Kind.SERVER;
import NoopSpanCustomizer.INSTANCE;
import Propagation.Factory;
import Sampler.NEVER_SAMPLE;
import SamplingFlags.EMPTY;
import brave.Tracer.SpanInScope;
import brave.propagation.B3Propagation;
import brave.propagation.ExtraFieldPropagation;
import brave.propagation.TraceContext;
import brave.propagation.TraceContextOrSamplingFlags;
import brave.propagation.TraceIdContext;
import brave.sampler.Sampler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import zipkin2.Endpoint;
import zipkin2.Span;

import static zipkin2.Span;


public class TracerTest {
    List<Span> spans = new ArrayList<>();

    Factory propagationFactory = B3Propagation.FACTORY;

    Tracer tracer = Tracing.newBuilder().spanReporter(new zipkin2.reporter.Reporter<Span>() {
        @Override
        public void report(Span span) {
            spans.add(span);
        }

        @Override
        public String toString() {
            return "MyReporter{}";
        }
    }).propagationFactory(new brave.propagation.Propagation.Factory() {
        @Override
        public <K> brave.propagation.Propagation<K> create(brave.propagation.Propagation.KeyFactory<K> keyFactory) {
            return propagationFactory.create(keyFactory);
        }

        @Override
        public boolean supportsJoin() {
            return propagationFactory.supportsJoin();
        }

        @Override
        public boolean requires128BitTraceId() {
            return propagationFactory.requires128BitTraceId();
        }

        @Override
        public TraceContext decorate(TraceContext context) {
            return propagationFactory.decorate(context);
        }
    }).currentTraceContext(brave.propagation.ThreadLocalCurrentTraceContext.create()).localServiceName("my-service").build().tracer();

    @Test
    public void reporter_hasNiceToString() {
        tracer = Tracing.newBuilder().build().tracer();
        assertThat(tracer.finishedSpanHandler).hasToString("LoggingReporter{name=brave.Tracer}");
    }

    @Test
    public void sampler() {
        Sampler sampler = new Sampler() {
            @Override
            public boolean isSampled(long traceId) {
                return false;
            }
        };
        tracer = Tracing.newBuilder().sampler(sampler).build().tracer();
        assertThat(tracer.sampler).isSameAs(sampler);
    }

    @Test
    public void withSampler() {
        Sampler sampler = new Sampler() {
            @Override
            public boolean isSampled(long traceId) {
                return false;
            }
        };
        tracer = tracer.withSampler(sampler);
        assertThat(tracer.sampler).isSameAs(sampler);
    }

    @Test
    public void localServiceName() {
        tracer = Tracing.newBuilder().localServiceName("my-foo").build().tracer();
        assertThat(tracer).extracting("finishedSpanHandler.delegate.converter.localEndpoint.serviceName").containsExactly("my-foo");
    }

    @Test
    public void localServiceName_defaultIsUnknown() {
        tracer = Tracing.newBuilder().build().tracer();
        assertThat(tracer).extracting("finishedSpanHandler.delegate.converter.localEndpoint.serviceName").containsExactly("unknown");
    }

    @Test
    public void localServiceName_ignoredWhenGivenLocalEndpoint() {
        Endpoint endpoint = Endpoint.newBuilder().ip("1.2.3.4").serviceName("my-bar").build();
        tracer = Tracing.newBuilder().localServiceName("my-foo").endpoint(endpoint).build().tracer();
        assertThat(tracer).extracting("finishedSpanHandler.delegate.converter.localEndpoint").allSatisfy(( e) -> assertThat(e).isEqualTo(endpoint));
    }

    @Test
    public void newTrace_isRootSpan() {
        assertThat(tracer.newTrace()).satisfies(( s) -> assertThat(s.context().parentId()).isNull()).isInstanceOf(RealSpan.class);
    }

    @Test
    public void newTrace_traceId128Bit() {
        tracer = Tracing.newBuilder().traceId128Bit(true).build().tracer();
        assertThat(tracer.newTrace().context().traceIdHigh()).isNotZero();
    }

    @Test
    public void newTrace_notSampled_tracer() {
        tracer = tracer.withSampler(NEVER_SAMPLE);
        assertThat(tracer.newTrace()).isInstanceOf(NoopSpan.class);
    }

    /**
     * When we join a sampled request, we are sharing the same trace identifiers.
     */
    @Test
    public void join_setsShared() {
        TraceContext fromIncomingRequest = tracer.newTrace().context();
        TraceContext joined = tracer.joinSpan(fromIncomingRequest).context();
        assertThat(joined.shared()).isTrue();
        assertThat(joined).isEqualToIgnoringGivenFields(fromIncomingRequest.toBuilder().shared(true).build(), "hashCode");
    }

    /**
     * Data from loopback requests should be partitioned into two spans: one for the client and the
     * other for the server.
     */
    @Test
    public void join_sharedDataIsSeparate() {
        Span clientSide = tracer.newTrace().kind(CLIENT).start(1L);
        Span serverSide = tracer.joinSpan(clientSide.context()).kind(SERVER).start(2L);
        serverSide.finish(3L);
        clientSide.finish(4L);
        // Ensure they use the same span ID (sanity check)
        String spanId = spans.get(0).id();
        assertThat(spans).extracting(Span::id).containsExactly(spanId, spanId);
        // Ensure the important parts are separated correctly
        assertThat(spans).extracting(Span::kind, Span::shared, Span::timestamp, Span::duration).containsExactly(tuple(zipkin2.Span.Kind.SERVER, true, 2L, 1L), tuple(zipkin2.Span.Kind.CLIENT, null, 1L, 3L));
    }

    @Test
    public void join_createsChildWhenUnsupported() {
        tracer = Tracing.newBuilder().supportsJoin(false).spanReporter(spans::add).build().tracer();
        TraceContext fromIncomingRequest = tracer.newTrace().context();
        TraceContext shouldBeChild = tracer.joinSpan(fromIncomingRequest).context();
        assertThat(shouldBeChild.shared()).isFalse();
        assertThat(shouldBeChild.parentId()).isEqualTo(fromIncomingRequest.spanId());
    }

    @Test
    public void finish_doesntCrashOnBadReporter() {
        tracer = Tracing.newBuilder().spanReporter(( span) -> {
            throw new RuntimeException();
        }).build().tracer();
        tracer.newTrace().start().finish();
    }

    @Test
    public void join_createsChildWhenUnsupportedByPropagation() {
        tracer = Tracing.newBuilder().propagationFactory(new brave.propagation.Propagation.Factory() {
            @Override
            public <K> brave.propagation.Propagation<K> create(brave.propagation.Propagation.KeyFactory<K> keyFactory) {
                return FACTORY.create(keyFactory);
            }
        }).spanReporter(spans::add).build().tracer();
        TraceContext fromIncomingRequest = tracer.newTrace().context();
        TraceContext shouldBeChild = tracer.joinSpan(fromIncomingRequest).context();
        assertThat(shouldBeChild.shared()).isFalse();
        assertThat(shouldBeChild.parentId()).isEqualTo(fromIncomingRequest.spanId());
    }

    @Test
    public void join_noop() {
        TraceContext fromIncomingRequest = tracer.newTrace().context();
        tracer.noop.set(true);
        assertThat(tracer.joinSpan(fromIncomingRequest)).isInstanceOf(NoopSpan.class);
    }

    @Test
    public void join_noopReporter() {
        tracer = Tracing.newBuilder().spanReporter(Reporter.NOOP).build().tracer();
        TraceContext fromIncomingRequest = tracer.newTrace().context();
        // context is sampled, but we aren't recording
        assertThat(tracer.joinSpan(fromIncomingRequest)).matches(( s) -> s.context().sampled()).isInstanceOf(NoopSpan.class);
    }

    @Test
    public void join_ensuresSampling() {
        TraceContext notYetSampled = tracer.newTrace().context().toBuilder().sampled(null).build();
        assertThat(tracer.joinSpan(notYetSampled).context()).isEqualTo(notYetSampled.toBuilder().sampled(true).build());
    }

    @Test
    public void newChild_ensuresSampling() {
        TraceContext notYetSampled = tracer.newTrace().context().toBuilder().sampled(null).build();
        assertThat(tracer.newChild(notYetSampled).context().sampled()).isTrue();
    }

    @Test
    public void nextSpan_ensuresSampling_whenCreatingNewChild() {
        TraceContext notYetSampled = tracer.newTrace().context().toBuilder().sampled(null).build();
        TraceContextOrSamplingFlags extracted = TraceContextOrSamplingFlags.create(notYetSampled);
        assertThat(tracer.nextSpan(extracted).context().sampled()).isTrue();
    }

    @Test
    public void toSpan() {
        TraceContext context = tracer.newTrace().context();
        assertThat(tracer.toSpan(context)).isInstanceOf(RealSpan.class).extracting(Span::context).isEqualTo(context);
    }

    @Test
    public void toSpan_noop() {
        TraceContext context = tracer.newTrace().context();
        tracer.noop.set(true);
        assertThat(tracer.toSpan(context)).isInstanceOf(NoopSpan.class);
    }

    @Test
    public void toSpan_noopReporter() {
        tracer = Tracing.newBuilder().spanReporter(Reporter.NOOP).build().tracer();
        TraceContext context = tracer.newTrace().context();
        // context is sampled, but we aren't recording
        assertThat(tracer.toSpan(context)).matches(( s) -> s.context().sampled()).isInstanceOf(NoopSpan.class);
    }

    @Test
    public void toSpan_sampledLocalIsNotNoop() {
        TraceContext sampledLocal = tracer.newTrace().context().toBuilder().sampled(false).sampledLocal(true).build();
        assertThat(tracer.toSpan(sampledLocal)).isInstanceOf(RealSpan.class);
    }

    @Test
    public void toSpan_notSampledIsNoop() {
        TraceContext notSampled = tracer.newTrace().context().toBuilder().sampled(false).build();
        assertThat(tracer.toSpan(notSampled)).isInstanceOf(NoopSpan.class);
    }

    @Test
    public void newChild() {
        TraceContext parent = tracer.newTrace().context();
        assertThat(tracer.newChild(parent)).satisfies(( c) -> {
            assertThat(c.context().traceIdString()).isEqualTo(parent.traceIdString());
            assertThat(c.context().parentIdString()).isEqualTo(parent.spanIdString());
        }).isInstanceOf(RealSpan.class);
    }

    /**
     * A child span is not sharing a span ID with its parent by definition
     */
    @Test
    public void newChild_isntShared() {
        TraceContext parent = tracer.newTrace().context();
        assertThat(tracer.newChild(parent).context().shared()).isFalse();
    }

    @Test
    public void newChild_noop() {
        TraceContext parent = tracer.newTrace().context();
        tracer.noop.set(true);
        assertThat(tracer.newChild(parent)).isInstanceOf(NoopSpan.class);
    }

    @Test
    public void newChild_noopReporter() {
        tracer = Tracing.newBuilder().spanReporter(Reporter.NOOP).build().tracer();
        TraceContext parent = tracer.newTrace().context();
        // context is sampled, but we aren't recording
        assertThat(tracer.newChild(parent)).matches(( s) -> s.context().sampled()).isInstanceOf(NoopSpan.class);
    }

    @Test
    public void newChild_notSampledIsNoop() {
        TraceContext notSampled = tracer.newTrace().context().toBuilder().sampled(false).build();
        assertThat(tracer.newChild(notSampled)).isInstanceOf(NoopSpan.class);
    }

    @Test
    public void currentSpanCustomizer_defaultsToNoop() {
        assertThat(tracer.currentSpanCustomizer()).isSameAs(INSTANCE);
    }

    @Test
    public void currentSpanCustomizer_noop_when_notSampled() {
        ScopedSpan parent = tracer.withSampler(NEVER_SAMPLE).startScopedSpan("parent");
        try {
            assertThat(tracer.currentSpanCustomizer()).isSameAs(INSTANCE);
        } finally {
            parent.finish();
        }
    }

    @Test
    public void currentSpanCustomizer_noopReporter() {
        tracer = Tracing.newBuilder().spanReporter(Reporter.NOOP).build().tracer();
        ScopedSpan parent = tracer.startScopedSpan("parent");
        try {
            assertThat(tracer.currentSpanCustomizer()).isSameAs(INSTANCE);
        } finally {
            parent.finish();
        }
    }

    @Test
    public void currentSpanCustomizer_real_when_sampled() {
        ScopedSpan parent = tracer.startScopedSpan("parent");
        try {
            assertThat(tracer.currentSpanCustomizer()).isInstanceOf(RealSpanCustomizer.class);
        } finally {
            parent.finish();
        }
    }

    @Test
    public void currentSpan_defaultsToNull() {
        assertThat(tracer.currentSpan()).isNull();
    }

    @Test
    public void nextSpan_defaultsToMakeNewTrace() {
        assertThat(tracer.nextSpan().context().parentId()).isNull();
    }

    @Test
    public void nextSpan_extractedNothing_makesChildOfCurrent() {
        Span parent = tracer.newTrace();
        try (SpanInScope ws = tracer.withSpanInScope(parent)) {
            Span nextSpan = tracer.nextSpan(TraceContextOrSamplingFlags.create(EMPTY));
            assertThat(nextSpan.context().parentId()).isEqualTo(parent.context().spanId());
        }
    }

    @Test
    public void nextSpan_extractedNothing_defaultsToMakeNewTrace() {
        Span nextSpan = tracer.nextSpan(TraceContextOrSamplingFlags.create(EMPTY));
        assertThat(nextSpan.context().parentId()).isNull();
    }

    @Test
    public void nextSpan_makesChildOfCurrent() {
        Span parent = tracer.newTrace();
        try (SpanInScope ws = tracer.withSpanInScope(parent)) {
            assertThat(tracer.nextSpan().context().parentId()).isEqualTo(parent.context().spanId());
        }
    }

    @Test
    public void nextSpan_extractedExtra_newTrace() {
        TraceContextOrSamplingFlags extracted = TraceContextOrSamplingFlags.create(EMPTY).toBuilder().addExtra(1L).build();
        assertThat(tracer.nextSpan(extracted).context().extra()).containsExactly(1L);
    }

    @Test
    public void nextSpan_extractedExtra_childOfCurrent() {
        Span parent = tracer.newTrace();
        TraceContextOrSamplingFlags extracted = TraceContextOrSamplingFlags.create(EMPTY).toBuilder().addExtra(1L).build();
        try (SpanInScope ws = tracer.withSpanInScope(parent)) {
            assertThat(tracer.nextSpan(extracted).context().extra()).containsExactly(1L);
        }
    }

    @Test
    public void nextSpan_extractedExtra_appendsToChildOfCurrent() {
        // current parent already has extra stuff
        Span parent = tracer.toSpan(tracer.newTrace().context().toBuilder().extra(Arrays.asList(1L)).build());
        TraceContextOrSamplingFlags extracted = TraceContextOrSamplingFlags.create(EMPTY).toBuilder().addExtra(1.0F).build();
        try (SpanInScope ws = tracer.withSpanInScope(parent)) {
            assertThat(tracer.nextSpan(extracted).context().extra()).containsExactlyInAnyOrder(1L, 1.0F);
        }
    }

    @Test
    public void nextSpan_extractedTraceId() {
        TraceIdContext traceIdContext = TraceIdContext.newBuilder().traceId(1L).build();
        TraceContextOrSamplingFlags extracted = TraceContextOrSamplingFlags.create(traceIdContext);
        assertThat(tracer.nextSpan(extracted).context().traceId()).isEqualTo(1L);
    }

    @Test
    public void nextSpan_extractedTraceId_extra() {
        TraceIdContext traceIdContext = TraceIdContext.newBuilder().traceId(1L).build();
        TraceContextOrSamplingFlags extracted = TraceContextOrSamplingFlags.create(traceIdContext).toBuilder().addExtra(1L).build();
        assertThat(tracer.nextSpan(extracted).context().extra()).containsExactly(1L);
    }

    @Test
    public void nextSpan_extractedTraceContext() {
        TraceContext traceContext = TraceContext.newBuilder().traceId(1L).spanId(2L).build();
        TraceContextOrSamplingFlags extracted = TraceContextOrSamplingFlags.create(traceContext);
        assertThat(tracer.nextSpan(extracted).context()).extracting(TraceContext::traceId, TraceContext::parentId).containsExactly(1L, 2L);
    }

    @Test
    public void nextSpan_extractedTraceContext_extra() {
        TraceContext traceContext = TraceContext.newBuilder().traceId(1L).spanId(2L).build();
        TraceContextOrSamplingFlags extracted = TraceContextOrSamplingFlags.create(traceContext).toBuilder().addExtra(1L).build();
        assertThat(tracer.nextSpan(extracted).context().extra()).contains(1L);
    }

    @Test
    public void startScopedSpan_isInScope() {
        RealScopedSpan current = ((RealScopedSpan) (tracer.startScopedSpan("foo")));
        try {
            assertThat(tracer.currentSpan().context()).isEqualTo(current.context);
            assertThat(tracer.currentSpanCustomizer()).isNotEqualTo(INSTANCE);
        } finally {
            current.finish();
        }
        // context was cleared
        assertThat(tracer.currentSpan()).isNull();
    }

    @Test
    public void startScopedSpan_noopIsInScope() {
        tracer = tracer.withSampler(NEVER_SAMPLE);
        NoopScopedSpan current = ((NoopScopedSpan) (tracer.startScopedSpan("foo")));
        try {
            assertThat(tracer.currentSpan().context()).isEqualTo(current.context);
            assertThat(tracer.currentSpanCustomizer()).isSameAs(INSTANCE);
        } finally {
            current.finish();
        }
        // context was cleared
        assertThat(tracer.currentSpan()).isNull();
    }

    @Test
    public void withSpanInScope() {
        Span current = tracer.newTrace();
        try (SpanInScope ws = tracer.withSpanInScope(current)) {
            assertThat(tracer.currentSpan()).isEqualTo(current);
            assertThat(tracer.currentSpanCustomizer()).isNotEqualTo(current).isNotEqualTo(INSTANCE);
        }
        // context was cleared
        assertThat(tracer.currentSpan()).isNull();
    }

    @Test
    public void withNoopSpanInScope() {
        Span current = tracer.withSampler(NEVER_SAMPLE).nextSpan();
        try (SpanInScope ws = tracer.withSpanInScope(current)) {
            assertThat(tracer.currentSpan()).isEqualTo(current);
            assertThat(tracer.currentSpanCustomizer()).isNotEqualTo(current).isEqualTo(INSTANCE);
        }
        // context was cleared
        assertThat(tracer.currentSpan()).isNull();
    }

    @Test
    public void toString_withSpanInScope() {
        TraceContext context = TraceContext.newBuilder().traceId(1L).spanId(10L).sampled(true).build();
        try (SpanInScope ws = tracer.withSpanInScope(tracer.toSpan(context))) {
            assertThat(tracer.toString()).hasToString("Tracer{currentSpan=0000000000000001/000000000000000a, finishedSpanHandler=MyReporter{}}");
        }
    }

    @Test
    public void toString_whenNoop() {
        Tracing.current().setNoop(true);
        assertThat(tracer).hasToString("Tracer{noop=true, finishedSpanHandler=MyReporter{}}");
    }

    @Test
    public void withSpanInScope_nested() {
        Span parent = tracer.newTrace();
        try (SpanInScope wsParent = tracer.withSpanInScope(parent)) {
            Span child = tracer.newChild(parent.context());
            try (SpanInScope wsChild = tracer.withSpanInScope(child)) {
                assertThat(tracer.currentSpan()).isEqualTo(child);
            }
            // old parent reverted
            assertThat(tracer.currentSpan()).isEqualTo(parent);
        }
    }

    @Test
    public void withSpanInScope_clear() {
        Span parent = tracer.newTrace();
        try (SpanInScope wsParent = tracer.withSpanInScope(parent)) {
            try (SpanInScope clearScope = tracer.withSpanInScope(null)) {
                assertThat(tracer.currentSpan()).isNull();
                assertThat(tracer.currentSpanCustomizer()).isEqualTo(INSTANCE);
            }
            // old parent reverted
            assertThat(tracer.currentSpan()).isEqualTo(parent);
        }
    }

    @Test
    public void join_getsExtraFromPropagationFactory() {
        propagationFactory = ExtraFieldPropagation.newFactory(FACTORY, "service");
        TraceContext context = tracer.nextSpan().context();
        ExtraFieldPropagation.set(context, "service", "napkin");
        TraceContext joined = tracer.joinSpan(context).context();
        assertThat(ExtraFieldPropagation.get(joined, "service")).isEqualTo("napkin");
    }

    @Test
    public void nextSpan_getsExtraFromPropagationFactory() {
        propagationFactory = ExtraFieldPropagation.newFactory(FACTORY, "service");
        Span parent = tracer.nextSpan();
        ExtraFieldPropagation.set(parent.context(), "service", "napkin");
        TraceContext nextSpan;
        try (SpanInScope scope = tracer.withSpanInScope(parent)) {
            nextSpan = tracer.nextSpan().context();
        }
        assertThat(ExtraFieldPropagation.get(nextSpan, "service")).isEqualTo("napkin");
    }

    @Test
    public void newChild_getsExtraFromPropagationFactory() {
        propagationFactory = ExtraFieldPropagation.newFactory(FACTORY, "service");
        TraceContext context = tracer.nextSpan().context();
        ExtraFieldPropagation.set(context, "service", "napkin");
        TraceContext newChild = tracer.newChild(context).context();
        assertThat(ExtraFieldPropagation.get(newChild, "service")).isEqualTo("napkin");
    }

    @Test
    public void startScopedSpanWithParent_getsExtraFromPropagationFactory() {
        propagationFactory = ExtraFieldPropagation.newFactory(FACTORY, "service");
        TraceContext context = tracer.nextSpan().context();
        ExtraFieldPropagation.set(context, "service", "napkin");
        ScopedSpan scoped = tracer.startScopedSpanWithParent("foo", context);
        scoped.finish();
        assertThat(ExtraFieldPropagation.get(scoped.context(), "service")).isEqualTo("napkin");
    }

    @Test
    public void startScopedSpan_getsExtraFromPropagationFactory() {
        propagationFactory = ExtraFieldPropagation.newFactory(FACTORY, "service");
        Span parent = tracer.nextSpan();
        ExtraFieldPropagation.set(parent.context(), "service", "napkin");
        ScopedSpan scoped;
        try (SpanInScope scope = tracer.withSpanInScope(parent)) {
            scoped = tracer.startScopedSpan("foo");
            scoped.finish();
        }
        assertThat(ExtraFieldPropagation.get(scoped.context(), "service")).isEqualTo("napkin");
    }

    @Test
    public void startScopedSpan() {
        ScopedSpan scoped = tracer.startScopedSpan("foo");
        try {
            assertThat(tracer.currentTraceContext.get()).isSameAs(scoped.context());
        } finally {
            scoped.finish();
        }
        assertThat(spans.get(0).name()).isEqualTo("foo");
        assertThat(spans.get(0).durationAsLong()).isPositive();
    }

    @Test
    public void localRootId_joinSpan_notYetSampled() {
        TraceContext context1 = TraceContext.newBuilder().traceId(1).spanId(2).build();
        TraceContext context2 = TraceContext.newBuilder().traceId(1).spanId(3).build();
        localRootId(context1, context2, ( ctx) -> tracer.joinSpan(ctx.context()));
    }

    @Test
    public void localRootId_joinSpan_notSampled() {
        TraceContext context1 = TraceContext.newBuilder().traceId(1).spanId(2).sampled(false).build();
        TraceContext context2 = TraceContext.newBuilder().traceId(1).spanId(3).sampled(false).build();
        localRootId(context1, context2, ( ctx) -> tracer.joinSpan(ctx.context()));
    }

    @Test
    public void localRootId_joinSpan_sampled() {
        TraceContext context1 = TraceContext.newBuilder().traceId(1).spanId(2).sampled(true).build();
        TraceContext context2 = TraceContext.newBuilder().traceId(1).spanId(3).sampled(true).build();
        localRootId(context1, context2, ( ctx) -> tracer.joinSpan(ctx.context()));
    }

    @Test
    public void localRootId_nextSpan_notYetSampled() {
        TraceContext context1 = TraceContext.newBuilder().traceId(1).spanId(2).build();
        TraceContext context2 = TraceContext.newBuilder().traceId(1).spanId(3).build();
        localRootId(context1, context2, ( ctx) -> tracer.nextSpan(ctx));
    }

    @Test
    public void localRootId_nextSpan_notSampled() {
        TraceContext context1 = TraceContext.newBuilder().traceId(1).spanId(2).sampled(false).build();
        TraceContext context2 = TraceContext.newBuilder().traceId(1).spanId(3).sampled(false).build();
        localRootId(context1, context2, ( ctx) -> tracer.nextSpan(ctx));
    }

    @Test
    public void localRootId_nextSpan_sampled() {
        TraceContext context1 = TraceContext.newBuilder().traceId(1).spanId(2).sampled(true).build();
        TraceContext context2 = TraceContext.newBuilder().traceId(1).spanId(3).sampled(true).build();
        localRootId(context1, context2, ( ctx) -> tracer.nextSpan(ctx));
    }

    @Test
    public void localRootId_nextSpan_ids_notYetSampled() {
        TraceIdContext context1 = TraceIdContext.newBuilder().traceId(1).build();
        TraceIdContext context2 = TraceIdContext.newBuilder().traceId(2).build();
        localRootId(context1, context2, ( ctx) -> tracer.nextSpan(ctx));
    }

    @Test
    public void localRootId_nextSpan_ids_notSampled() {
        TraceIdContext context1 = TraceIdContext.newBuilder().traceId(1).sampled(false).build();
        TraceIdContext context2 = TraceIdContext.newBuilder().traceId(2).sampled(false).build();
        localRootId(context1, context2, ( ctx) -> tracer.nextSpan(ctx));
    }

    @Test
    public void localRootId_nextSpan_ids_sampled() {
        TraceIdContext context1 = TraceIdContext.newBuilder().traceId(1).sampled(true).build();
        TraceIdContext context2 = TraceIdContext.newBuilder().traceId(2).sampled(true).build();
        localRootId(context1, context2, ( ctx) -> tracer.nextSpan(ctx));
    }

    @Test
    public void localRootId_nextSpan_flags_empty() {
        TraceContextOrSamplingFlags flags = TraceContextOrSamplingFlags.EMPTY;
        localRootId(flags, flags, ( ctx) -> tracer.nextSpan(ctx));
    }

    @Test
    public void localRootId_nextSpan_flags_notSampled() {
        TraceContextOrSamplingFlags flags = TraceContextOrSamplingFlags.NOT_SAMPLED;
        localRootId(flags, flags, ( ctx) -> tracer.nextSpan(ctx));
    }

    @Test
    public void localRootId_nextSpan_flags_sampled() {
        TraceContextOrSamplingFlags flags = TraceContextOrSamplingFlags.SAMPLED;
        localRootId(flags, flags, ( ctx) -> tracer.nextSpan(ctx));
    }

    @Test
    public void localRootId_nextSpan_flags_debug() {
        TraceContextOrSamplingFlags flags = TraceContextOrSamplingFlags.DEBUG;
        localRootId(flags, flags, ( ctx) -> tracer.nextSpan(ctx));
    }
}

