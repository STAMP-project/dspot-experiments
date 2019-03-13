package brave.propagation;


import TraceContext.Builder;
import brave.internal.HexCodec;
import brave.internal.InternalPropagation;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;


public class TraceContextTest {
    TraceContext base = TraceContext.newBuilder().traceId(1L).spanId(1L).build();

    @Test
    public void compareUnequalIds() {
        TraceContext context = TraceContext.newBuilder().traceId(333L).spanId(3L).build();
        assertThat(context).isNotEqualTo(TraceContext.newBuilder().traceId(333L).spanId(1L).build());
        assertThat(context.hashCode()).isNotEqualTo(TraceContext.newBuilder().traceId(333L).spanId(1L).build().hashCode());
    }

    @Test
    public void contextWithShared_true() {
        assertThat(base.toBuilder().sampled(false).shared(true).build().flags).isEqualTo(((InternalPropagation.FLAG_SAMPLED_SET) | (InternalPropagation.FLAG_SHARED)));
    }

    @Test
    public void contextWithShared_false() {
        assertThat(base.toBuilder().sampled(false).shared(false).build().flags).isEqualTo(InternalPropagation.FLAG_SAMPLED_SET);
    }

    /**
     * Shared context is different than an unshared one, notably this keeps client/server loopback
     * separate.
     */
    @Test
    public void compareUnequalIds_onShared() {
        TraceContext context = TraceContext.newBuilder().traceId(333L).spanId(3L).build();
        assertThat(context).isNotEqualTo(context.toBuilder().shared(true).build());
        assertThat(context.hashCode()).isNotEqualTo(context.toBuilder().shared(true).build().hashCode());
    }

    @Test
    public void compareEqualIds() {
        TraceContext context = TraceContext.newBuilder().traceId(333L).spanId(444L).build();
        assertThat(context).isEqualTo(TraceContext.newBuilder().traceId(333L).spanId(444L).build());
        assertThat(context.hashCode()).isEqualTo(TraceContext.newBuilder().traceId(333L).spanId(444L).build().hashCode());
    }

    @Test
    public void equalOnSameTraceIdSpanId() {
        TraceContext context = TraceContext.newBuilder().traceId(333L).spanId(444L).build();
        assertThat(context).isEqualTo(context.toBuilder().parentId(1L).build());
        assertThat(context.hashCode()).isEqualTo(context.toBuilder().parentId(1L).build().hashCode());
    }

    @Test
    public void testToString_lo() {
        TraceContext context = TraceContext.newBuilder().traceId(333L).spanId(3).parentId(2L).build();
        assertThat(context.toString()).isEqualTo("000000000000014d/0000000000000003");
    }

    @Test
    public void testToString() {
        TraceContext context = TraceContext.newBuilder().traceIdHigh(333L).traceId(444L).spanId(3).parentId(2L).build();
        assertThat(context.toString()).isEqualTo("000000000000014d00000000000001bc/0000000000000003");
    }

    @Test
    public void canUsePrimitiveOverloads_true() {
        TraceContext primitives = base.toBuilder().parentId(1L).sampled(true).debug(true).build();
        TraceContext objects = base.toBuilder().parentId(Long.valueOf(1L)).sampled(Boolean.TRUE).debug(Boolean.TRUE).build();
        assertThat(primitives).isEqualToComparingFieldByField(objects);
        assertThat(primitives.debug()).isTrue();
        assertThat(primitives.sampled()).isTrue();
    }

    @Test
    public void canUsePrimitiveOverloads_false() {
        base = base.toBuilder().debug(true).build();
        TraceContext primitives = base.toBuilder().parentId(1L).sampled(false).debug(false).build();
        TraceContext objects = base.toBuilder().parentId(Long.valueOf(1L)).sampled(Boolean.FALSE).debug(Boolean.FALSE).build();
        assertThat(primitives).isEqualToComparingFieldByField(objects);
        assertThat(primitives.debug()).isFalse();
        assertThat(primitives.sampled()).isFalse();
    }

    @Test
    public void canSetSampledNull() {
        base = base.toBuilder().sampled(true).build();
        TraceContext objects = base.toBuilder().sampled(null).build();
        assertThat(objects.debug()).isFalse();
        assertThat(objects.sampled()).isNull();
    }

    @Test
    public void nullToZero() {
        TraceContext nulls = base.toBuilder().parentId(null).build();
        TraceContext zeros = base.toBuilder().parentId(0L).build();
        assertThat(nulls).isEqualToComparingFieldByField(zeros);
    }

    @Test
    public void parseTraceId_128bit() {
        String traceIdString = "463ac35c9f6413ad48485a3953bb6124";
        TraceContext.Builder builder = parseGoodTraceID(traceIdString);
        assertThat(HexCodec.toLowerHex(builder.traceIdHigh)).isEqualTo("463ac35c9f6413ad");
        assertThat(HexCodec.toLowerHex(builder.traceId)).isEqualTo("48485a3953bb6124");
    }

    @Test
    public void parseTraceId_64bit() {
        String traceIdString = "48485a3953bb6124";
        TraceContext.Builder builder = parseGoodTraceID(traceIdString);
        assertThat(builder.traceIdHigh).isZero();
        assertThat(HexCodec.toLowerHex(builder.traceId)).isEqualTo(traceIdString);
    }

    @Test
    public void parseTraceId_short128bit() {
        String traceIdString = "3ac35c9f6413ad48485a3953bb6124";
        TraceContext.Builder builder = parseGoodTraceID(traceIdString);
        assertThat(HexCodec.toLowerHex(builder.traceIdHigh)).isEqualTo("003ac35c9f6413ad");
        assertThat(HexCodec.toLowerHex(builder.traceId)).isEqualTo("48485a3953bb6124");
    }

    @Test
    public void parseTraceId_short64bit() {
        String traceIdString = "6124";
        TraceContext.Builder builder = parseGoodTraceID(traceIdString);
        assertThat(builder.traceIdHigh).isZero();
        assertThat(HexCodec.toLowerHex(builder.traceId)).isEqualTo(("000000000000" + traceIdString));
    }

    /**
     * Trace ID is a required parameter, so it cannot be null empty malformed or other nonsense.
     *
     * <p>Notably, this shouldn't throw exception or allocate anything
     */
    @Test
    public void parseTraceId_malformedReturnsFalse() {
        parseBadTraceId("463acL$c9f6413ad48485a3953bb6124");
        parseBadTraceId("holy ?");
        parseBadTraceId("-");
        parseBadTraceId("");
        parseBadTraceId(null);
    }

    @Test
    public void parseSpanId() {
        String spanIdString = "48485a3953bb6124";
        TraceContext.Builder builder = parseGoodSpanId(spanIdString);
        assertThat(HexCodec.toLowerHex(builder.spanId)).isEqualTo(spanIdString);
    }

    @Test
    public void parseSpanId_short64bit() {
        String spanIdString = "6124";
        TraceContext.Builder builder = parseGoodSpanId(spanIdString);
        assertThat(HexCodec.toLowerHex(builder.spanId)).isEqualTo(("000000000000" + spanIdString));
    }

    /**
     * Span ID is a required parameter, so it cannot be null empty malformed or other nonsense.
     *
     * <p>Notably, this shouldn't throw exception or allocate anything
     */
    @Test
    public void parseSpanId_malformedReturnsFalse() {
        parseBadSpanId("463acL$c9f6413ad");
        parseBadSpanId("holy ?");
        parseBadSpanId("-");
        parseBadSpanId("");
        parseBadSpanId(null);
    }

    @Test
    public void parseParentId() {
        String parentIdString = "48485a3953bb6124";
        TraceContext.Builder builder = parseGoodParentId(parentIdString);
        assertThat(HexCodec.toLowerHex(builder.parentId)).isEqualTo(parentIdString);
    }

    @Test
    public void parseParentId_null_is_ok() {
        TraceContext.Builder builder = parseGoodParentId(null);
        assertThat(builder.parentId).isZero();
    }

    @Test
    public void parseParentId_short64bit() {
        String parentIdString = "6124";
        TraceContext.Builder builder = parseGoodParentId(parentIdString);
        assertThat(HexCodec.toLowerHex(builder.parentId)).isEqualTo(("000000000000" + parentIdString));
    }

    /**
     * Parent Span ID is an optional parameter, but it cannot be empty malformed or other nonsense.
     *
     * <p>Notably, this shouldn't throw exception or allocate anything
     */
    @Test
    public void parseParentId_malformedReturnsFalse() {
        parseBadParentId("463acL$c9f6413ad");
        parseBadParentId("holy ?");
        parseBadParentId("-");
        parseBadParentId("");
    }

    TraceContext context = TraceContext.newBuilder().traceId(1L).spanId(2L).build();

    @Test
    public void withExtra_notEmpty() {
        assertThat(context.withExtra(Arrays.asList(1L))).extracting("extra").containsExactly(Arrays.asList(1L));
    }

    @Test
    public void withExtra_empty() {
        assertThat(context.toBuilder().extra(Arrays.asList(1L)).build().withExtra(Collections.emptyList())).extracting("extra").containsExactly(Collections.emptyList());
    }

    @Test
    public void caches_hashCode() {
        TraceContext context = TraceContext.newBuilder().traceId(333L).spanId(3L).build();
        assertThat(context.hashCode).isZero();
        assertThat(context.hashCode()).isNotZero().isEqualTo(TraceContext.newBuilder().traceId(333L).spanId(3L).build().hashCode());
    }

    @Test
    public void traceIdString_caches() {
        TraceContext context = TraceContext.newBuilder().traceId(1L).spanId(2L).build();
        assertThat(context.traceIdString).isNull();
        assertThat(context.traceIdString()).isNotNull().isEqualTo("0000000000000001");
        assertThat(context.traceIdString).isEqualTo("0000000000000001");
    }

    @Test
    public void parentIdString_caches() {
        TraceContext context = TraceContext.newBuilder().traceId(1L).parentId(2L).spanId(3L).build();
        assertThat(context.parentIdString).isNull();
        assertThat(context.parentIdString()).isNotNull().isEqualTo("0000000000000002");
        assertThat(context.parentIdString).isEqualTo("0000000000000002");
    }

    @Test
    public void parentIdString_doesNotCacheNull() {
        TraceContext context = TraceContext.newBuilder().traceId(1L).spanId(3L).build();
        assertThat(context.parentIdString).isNull();
        assertThat(context.parentIdString()).isNull();
        assertThat(context.parentIdString).isNull();
    }

    @Test
    public void localRootIdString_caches() {
        TraceContext.Builder builder = TraceContext.newBuilder().traceId(1L);
        builder.localRootId = 2L;
        TraceContext context = builder.spanId(3L).build();
        assertThat(context.localRootIdString).isNull();
        assertThat(context.localRootIdString()).isNotNull().isEqualTo("0000000000000002");
        assertThat(context.localRootIdString).isEqualTo("0000000000000002");
    }

    @Test
    public void localRootIdString_doesNotCacheNull() {
        TraceContext context = TraceContext.newBuilder().traceId(1L).spanId(3L).build();
        assertThat(context.localRootIdString).isNull();
        assertThat(context.localRootIdString()).isNull();
        assertThat(context.localRootIdString).isNull();
    }

    @Test
    public void spanIdString_caches() {
        TraceContext context = TraceContext.newBuilder().traceId(1L).spanId(2L).build();
        assertThat(context.spanIdString).isNull();
        assertThat(context.spanIdString()).isNotNull().isEqualTo("0000000000000002");
        assertThat(context.spanIdString).isEqualTo("0000000000000002");
    }
}

