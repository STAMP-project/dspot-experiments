package brave.grpc;


import brave.grpc.GrpcPropagation.Tags;
import brave.propagation.TraceContext;
import java.util.Collections;
import org.junit.Test;


/**
 * Tests here are based on {@code io.opencensus.implcore.trace.propagation.BinaryFormatImplTest}
 */
public class TraceContextBinaryFormatTest {
    TraceContext context = TraceContext.newBuilder().traceIdHigh(Long.MAX_VALUE).traceId(Long.MIN_VALUE).spanId((-1)).sampled(true).build();

    byte[] contextBytes = new byte[]{ 0// version
    , 0, 127, -1, -1, -1, -1, -1, -1, -1, -128, 0, 0, 0, 0, 0, 0, 0// trace ID
    , 1, -1, -1, -1, -1, -1, -1, -1, -1// span ID
    , 2, 1// sampled
     };

    @Test
    public void roundtrip() {
        byte[] serialized = TraceContextBinaryFormat.toBytes(context);
        assertThat(serialized).containsExactly(contextBytes);
        assertThat(TraceContextBinaryFormat.parseBytes(serialized, null)).isEqualTo(context);
    }

    @Test
    public void roundtrip_unsampled() {
        context = context.toBuilder().sampled(false).build();
        byte[] serialized = TraceContextBinaryFormat.toBytes(context);
        contextBytes[((contextBytes.length) - 1)] = 0;// unsampled

        assertThat(serialized).containsExactly(contextBytes);
        assertThat(TraceContextBinaryFormat.parseBytes(serialized, null)).isEqualTo(context);
    }

    @Test
    public void roundtrip_tags() {
        Tags tags = new Tags();
        context = context.toBuilder().extra(Collections.singletonList(tags)).build();
        byte[] serialized = TraceContextBinaryFormat.toBytes(context);
        assertThat(serialized).containsExactly(contextBytes);
        assertThat(TraceContextBinaryFormat.parseBytes(serialized, tags)).isEqualTo(context);
    }

    @Test
    public void parseBytes_empty_toNull() {
        assertThat(TraceContextBinaryFormat.parseBytes(new byte[0], null)).isNull();
    }

    @Test
    public void parseBytes_unsupportedVersionId_toNull() {
        assertThat(TraceContextBinaryFormat.parseBytes(new byte[]{ 1// bad version
        , 0, 127, -1, -1, -1, -1, -1, -1, -1, -128, 0, 0, 0, 0, 0, 0, 0, 1, -1, -1, -1, -1, -1, -1, -1, -1, 2, 1 }, null)).isNull();
    }

    @Test
    public void parseBytes_unsupportedFieldIdFirst_toNull() {
        assertThat(TraceContextBinaryFormat.parseBytes(new byte[]{ 0, 4, 127, -1, -1, -1, -1, -1, -1, -1, -128, 0, 0, 0, 0, 0, 0, 0// bad field number
        , 1, -1, -1, -1, -1, -1, -1, -1, -1, 2, 1 }, null)).isNull();
    }

    @Test
    public void parseBytes_unsupportedFieldIdSecond_toNull() {
        assertThat(TraceContextBinaryFormat.parseBytes(new byte[]{ 0, 0, 127, -1, -1, -1, -1, -1, -1, -1, -128, 0, 0, 0, 0, 0, 0, 0, 4, -1, -1, -1, -1, -1, -1, -1, -1// bad field number
        , 2, 1 }, null)).isNull();
    }

    @Test
    public void parseBytes_unsupportedFieldIdThird_toSampledNull() {
        assertThat(TraceContextBinaryFormat.parseBytes(new byte[]{ 0, 0, 127, -1, -1, -1, -1, -1, -1, -1, -128, 0, 0, 0, 0, 0, 0, 0, 1, -1, -1, -1, -1, -1, -1, -1, -1, 4, 1// bad field number
         }, null).sampled()).isNull();
    }

    @Test
    public void parseBytes_64BitTraceId_toNull() {
        assertThat(TraceContextBinaryFormat.parseBytes(new byte[]{ 0, 0, 127, -1, -1, -1, -1, -1, -1, -1// half a trace ID
        , 1, -1, -1, -1, -1, -1, -1, -1, -1, 2, 1 }, null)).isNull();
    }

    @Test
    public void parseBytes_32BitSpanId_toNull() {
        assertThat(TraceContextBinaryFormat.parseBytes(new byte[]{ 0, 0, 127, -1, -1, -1, -1, -1, -1, -1, -128, 0, 0, 0, 0, 0, 0, 0, 1, -1, -1, -1, -1// half a span ID
        , 2, 1 }, null)).isNull();
    }

    @Test
    public void parseBytes_truncatedTraceOptions_toNull() {
        assertThat(TraceContextBinaryFormat.parseBytes(new byte[]{ 0, 0, 127, -1, -1, -1, -1, -1, -1, -1, -128, 0, 0, 0, 0, 0, 0, 0, 1, -1, -1, -1, -1, -1, -1, -1, -1, 2// has field ID, but missing sampled bit
         }, null)).isNull();
    }

    @Test
    public void parseBytes_missingTraceOptions() {
        assertThat(TraceContextBinaryFormat.parseBytes(// no trace options field
        new byte[]{ 0, 0, 127, -1, -1, -1, -1, -1, -1, -1, -128, 0, 0, 0, 0, 0, 0, 0, 1, -1, -1, -1, -1, -1, -1, -1, -1 }// no trace options field
        , null)).isEqualTo(context);
    }
}

