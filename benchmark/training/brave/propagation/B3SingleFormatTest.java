package brave.propagation;


import SamplingFlags.DEBUG;
import TraceContextOrSamplingFlags.NOT_SAMPLED;
import TraceContextOrSamplingFlags.SAMPLED;
import brave.internal.Platform;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


// Added to declutter console: tells power mock not to mess with implicit classes we aren't testing
@RunWith(PowerMockRunner.class)
@PowerMockIgnore({ "org.apache.logging.*", "javax.script.*" })
@PrepareForTest({ Platform.class, B3SingleFormat.class })
public class B3SingleFormatTest {
    String traceIdHigh = "0000000000000009";

    String traceId = "0000000000000001";

    String parentId = "0000000000000002";

    String spanId = "0000000000000003";

    Platform platform = mock(Platform.class);

    @Test
    public void writeB3SingleFormat_notYetSampled() {
        TraceContext context = TraceContext.newBuilder().traceId(1).spanId(3).build();
        assertThat(B3SingleFormat.writeB3SingleFormat(context)).isEqualTo((((traceId) + "-") + (spanId))).isEqualTo(new String(B3SingleFormat.writeB3SingleFormatAsBytes(context), StandardCharsets.UTF_8));
    }

    @Test
    public void writeB3SingleFormat_notYetSampled_128() {
        TraceContext context = TraceContext.newBuilder().traceIdHigh(9).traceId(1).spanId(3).build();
        assertThat(B3SingleFormat.writeB3SingleFormat(context)).isEqualTo(((((traceIdHigh) + (traceId)) + "-") + (spanId))).isEqualTo(new String(B3SingleFormat.writeB3SingleFormatAsBytes(context), StandardCharsets.UTF_8));
    }

    @Test
    public void writeB3SingleFormat_unsampled() {
        TraceContext context = TraceContext.newBuilder().traceId(1).spanId(3).sampled(false).build();
        assertThat(B3SingleFormat.writeB3SingleFormat(context)).isEqualTo(((((traceId) + "-") + (spanId)) + "-0")).isEqualTo(new String(B3SingleFormat.writeB3SingleFormatAsBytes(context), StandardCharsets.UTF_8));
    }

    @Test
    public void writeB3SingleFormat_sampled() {
        TraceContext context = TraceContext.newBuilder().traceId(1).spanId(3).sampled(true).build();
        assertThat(B3SingleFormat.writeB3SingleFormat(context)).isEqualTo(((((traceId) + "-") + (spanId)) + "-1")).isEqualTo(new String(B3SingleFormat.writeB3SingleFormatAsBytes(context), StandardCharsets.UTF_8));
    }

    @Test
    public void writeB3SingleFormat_debug() {
        TraceContext context = TraceContext.newBuilder().traceId(1).spanId(3).debug(true).build();
        assertThat(B3SingleFormat.writeB3SingleFormat(context)).isEqualTo(((((traceId) + "-") + (spanId)) + "-d")).isEqualTo(new String(B3SingleFormat.writeB3SingleFormatAsBytes(context), StandardCharsets.UTF_8));
    }

    @Test
    public void writeB3SingleFormat_parent() {
        TraceContext context = TraceContext.newBuilder().traceId(1).parentId(2).spanId(3).sampled(true).build();
        assertThat(B3SingleFormat.writeB3SingleFormat(context)).isEqualTo((((((traceId) + "-") + (spanId)) + "-1-") + (parentId))).isEqualTo(new String(B3SingleFormat.writeB3SingleFormatAsBytes(context), StandardCharsets.UTF_8));
    }

    @Test
    public void writeB3SingleFormat_largest() {
        TraceContext context = TraceContext.newBuilder().traceIdHigh(9).traceId(1).parentId(2).spanId(3).sampled(true).build();
        assertThat(B3SingleFormat.writeB3SingleFormat(context)).isEqualTo(((((((traceIdHigh) + (traceId)) + "-") + (spanId)) + "-1-") + (parentId))).isEqualTo(new String(B3SingleFormat.writeB3SingleFormatAsBytes(context), StandardCharsets.UTF_8));
    }

    @Test
    public void parseB3SingleFormat_largest() {
        assertThat(B3SingleFormat.parseB3SingleFormat(((((((traceIdHigh) + (traceId)) + "-") + (spanId)) + "-1-") + (parentId)))).extracting(TraceContextOrSamplingFlags::context).isEqualToComparingFieldByField(TraceContext.newBuilder().traceIdHigh(9).traceId(1).parentId(2).spanId(3).sampled(true).build());
    }

    @Test
    public void writeB3SingleFormatWithoutParent_notYetSampled() {
        TraceContext context = TraceContext.newBuilder().traceId(1).spanId(3).build();
        assertThat(B3SingleFormat.writeB3SingleFormatWithoutParentId(context)).isEqualTo((((traceId) + "-") + (spanId))).isEqualTo(new String(B3SingleFormat.writeB3SingleFormatWithoutParentIdAsBytes(context), StandardCharsets.UTF_8));
    }

    @Test
    public void writeB3SingleFormatWithoutParent_unsampled() {
        TraceContext context = TraceContext.newBuilder().traceId(1).parentId(2).spanId(3).sampled(false).build();
        assertThat(B3SingleFormat.writeB3SingleFormatWithoutParentId(context)).isEqualTo(((((traceId) + "-") + (spanId)) + "-0")).isEqualTo(new String(B3SingleFormat.writeB3SingleFormatWithoutParentIdAsBytes(context), StandardCharsets.UTF_8));
    }

    @Test
    public void writeB3SingleFormatWithoutParent_sampled() {
        TraceContext context = TraceContext.newBuilder().traceId(1).parentId(2).spanId(3).sampled(true).build();
        assertThat(B3SingleFormat.writeB3SingleFormatWithoutParentId(context)).isEqualTo(((((traceId) + "-") + (spanId)) + "-1")).isEqualTo(new String(B3SingleFormat.writeB3SingleFormatWithoutParentIdAsBytes(context), StandardCharsets.UTF_8));
    }

    @Test
    public void writeB3SingleFormatWithoutParent_debug() {
        TraceContext context = TraceContext.newBuilder().traceId(1).parentId(2).spanId(3).debug(true).build();
        assertThat(B3SingleFormat.writeB3SingleFormatWithoutParentId(context)).isEqualTo(((((traceId) + "-") + (spanId)) + "-d")).isEqualTo(new String(B3SingleFormat.writeB3SingleFormatWithoutParentIdAsBytes(context), StandardCharsets.UTF_8));
    }

    /**
     * for example, parsing a w3c context
     */
    @Test
    public void parseB3SingleFormat_middleOfString() {
        String input = (((("b3=" + (traceId)) + (traceId)) + "-") + (spanId)) + ",";
        assertThat(B3SingleFormat.parseB3SingleFormat(input, 3, ((input.length()) - 1)).context()).isEqualToComparingFieldByField(TraceContext.newBuilder().traceIdHigh(1).traceId(1).spanId(3).build());
    }

    /**
     * for example, parsing a w3c context
     */
    @Test
    public void parseB3SingleFormat_middleOfString_debugOnly() {
        String input = "b2=foo,b3=d,b4=bar";
        assertThat(B3SingleFormat.parseB3SingleFormat(input, 10, 11).samplingFlags()).isSameAs(DEBUG);
    }

    @Test
    public void parseB3SingleFormat_middleOfString_incorrectOffset() {
        String input = "b2=foo,b3=d,b4=bar";
        assertThat(B3SingleFormat.parseB3SingleFormat(input, 10, 12)).isNull();// instead of raising exception

        Mockito.verify(platform).log("Invalid input: truncated", null);
    }

    @Test
    public void parseB3SingleFormat_idsNotYetSampled() {
        assertThat(B3SingleFormat.parseB3SingleFormat((((traceId) + "-") + (spanId))).context()).isEqualToComparingFieldByField(TraceContext.newBuilder().traceId(1).spanId(3).build());
    }

    @Test
    public void parseB3SingleFormat_idsNotYetSampled128() {
        assertThat(B3SingleFormat.parseB3SingleFormat(((((traceId) + (traceId)) + "-") + (spanId))).context()).isEqualToComparingFieldByField(TraceContext.newBuilder().traceIdHigh(1).traceId(1).spanId(3).build());
    }

    @Test
    public void parseB3SingleFormat_idsUnsampled() {
        assertThat(B3SingleFormat.parseB3SingleFormat(((((traceId) + "-") + (spanId)) + "-0")).context()).isEqualToComparingFieldByField(TraceContext.newBuilder().traceId(1).spanId(3).sampled(false).build());
    }

    @Test
    public void parseB3SingleFormat_parent_unsampled() {
        assertThat(B3SingleFormat.parseB3SingleFormat((((((traceId) + "-") + (spanId)) + "-0-") + (parentId))).context()).isEqualToComparingFieldByField(TraceContext.newBuilder().traceId(1).parentId(2).spanId(3).sampled(false).build());
    }

    @Test
    public void parseB3SingleFormat_parent_debug() {
        assertThat(B3SingleFormat.parseB3SingleFormat((((((traceId) + "-") + (spanId)) + "-d-") + (parentId))).context()).isEqualToComparingFieldByField(TraceContext.newBuilder().traceId(1).parentId(2).spanId(3).debug(true).build());
    }

    @Test
    public void parseB3SingleFormat_idsWithDebug() {
        assertThat(B3SingleFormat.parseB3SingleFormat(((((traceId) + "-") + (spanId)) + "-d")).context()).isEqualToComparingFieldByField(TraceContext.newBuilder().traceId(1).spanId(3).debug(true).build());
    }

    @Test
    public void parseB3SingleFormat_sampledFalse() {
        assertThat(B3SingleFormat.parseB3SingleFormat("0")).isEqualTo(NOT_SAMPLED);
    }

    @Test
    public void parseB3SingleFormat_sampled() {
        assertThat(B3SingleFormat.parseB3SingleFormat("1")).isEqualTo(SAMPLED);
    }

    @Test
    public void parseB3SingleFormat_debug() {
        assertThat(B3SingleFormat.parseB3SingleFormat("d")).isEqualTo(TraceContextOrSamplingFlags.DEBUG);
    }

    @Test
    public void parseB3SingleFormat_malformed_traceId() {
        assertThat(B3SingleFormat.parseB3SingleFormat((((traceId.substring(0, 15)) + "?-") + (spanId)))).isNull();// instead of raising exception

        Mockito.verify(platform).log("Invalid input: expected a 16 or 32 lower hex trace ID at offset 0", null);
    }

    @Test
    public void parseB3SingleFormat_malformed_id() {
        assertThat(B3SingleFormat.parseB3SingleFormat(((((traceId) + "-") + (spanId.substring(0, 15))) + "?"))).isNull();// instead of raising exception

        Mockito.verify(platform).log("Invalid input: expected a 16 lower hex span ID at offset {0}", 17, null);
    }

    @Test
    public void parseB3SingleFormat_malformed_sampled_parentid() {
        assertThat(B3SingleFormat.parseB3SingleFormat(((((((traceId) + "-") + (spanId)) + "-1-") + (parentId.substring(0, 15))) + "?"))).isNull();// instead of raising exception

        Mockito.verify(platform).log("Invalid input: expected a 16 lower hex parent ID at offset {0}", 36, null);
    }

    @Test
    public void parseB3SingleFormat_malformed_invalid_delimiter_before_parent() {
        assertThat(B3SingleFormat.parseB3SingleFormat((((((traceId) + "-") + (spanId)) + "-1!") + (parentId)))).isNull();// instead of raising exception

        Mockito.verify(platform).log("Invalid input: expected a hyphen(-) delimiter at offset {0}", 35, null);
    }

    // odd but possible to not yet sample a child
    @Test
    public void parseB3SingleFormat_parentid_notYetSampled() {
        assertThat(B3SingleFormat.parseB3SingleFormat((((((traceId) + "-") + (spanId)) + "-") + (parentId))).context()).isEqualToComparingFieldByField(TraceContext.newBuilder().traceId(1).parentId(2).spanId(3).build());
    }

    @Test
    public void parseB3SingleFormat_malformed_parentid_notYetSampled() {
        assertThat(B3SingleFormat.parseB3SingleFormat(((((((traceId) + "-") + (spanId)) + "-") + (parentId.substring(0, 15))) + "?"))).isNull();// instead of raising exception

        Mockito.verify(platform).log("Invalid input: expected a 16 lower hex parent ID at offset {0}", 34, null);
    }

    @Test
    public void parseB3SingleFormat_malformed() {
        assertThat(B3SingleFormat.parseB3SingleFormat("not-a-tumor")).isNull();// instead of raising exception

        Mockito.verify(platform).log("Invalid input: truncated", null);
    }

    @Test
    public void parseB3SingleFormat_malformed_uuid() {
        assertThat(B3SingleFormat.parseB3SingleFormat("b970dafd-0d95-40aa-95d8-1d8725aebe40")).isNull();// instead of raising exception

        Mockito.verify(platform).log("Invalid input: expected a 16 or 32 lower hex trace ID at offset 0", null);
    }

    @Test
    public void parseB3SingleFormat_empty() {
        assertThat(B3SingleFormat.parseB3SingleFormat("")).isNull();
        Mockito.verify(platform).log("Invalid input: empty", null);
    }

    @Test
    public void parseB3SingleFormat_hyphenNotSampled() {
        assertThat(B3SingleFormat.parseB3SingleFormat("-")).isNull();
        Mockito.verify(platform).log("Invalid input: expected 0, 1 or d for sampled at offset {0}", 0, null);
    }

    @Test
    public void parseB3SingleFormat_truncated() {
        List<String> truncated = Arrays.asList("-1", "1-", traceId.substring(0, 15), traceId, ((traceId) + "-"), (((traceId.substring(0, 15)) + "-") + (spanId)), (((traceId) + "-") + (spanId.substring(0, 15))), ((((traceId) + "-") + (spanId)) + "-"), ((((traceId) + "-") + (spanId)) + "-1-"), (((((traceId) + "-") + (spanId)) + "-1-") + (parentId.substring(0, 15))));
        for (String b3 : truncated) {
            assertThat(B3SingleFormat.parseB3SingleFormat(b3)).withFailMessage((("expected " + b3) + " to not parse")).isNull();
            Mockito.verify(platform).log("Invalid input: truncated", null);
            Mockito.reset(platform);
        }
    }

    @Test
    public void parseB3SingleFormat_traceIdTooLong() {
        assertThat(B3SingleFormat.parseB3SingleFormat((((((traceId) + (traceId)) + "a") + "-") + (spanId)))).isNull();// instead of raising exception

        Mockito.verify(platform).log("Invalid input: trace ID is too long", null);
    }

    @Test
    public void parseB3SingleFormat_spanIdTooLong() {
        assertThat(B3SingleFormat.parseB3SingleFormat(((((traceId) + "-") + (spanId)) + "a"))).isNull();// instead of raising exception

        Mockito.verify(platform).log("Invalid input: span ID is too long", null);
    }

    @Test
    public void parseB3SingleFormat_parentIdTooLong() {
        assertThat(B3SingleFormat.parseB3SingleFormat(((((((traceId) + "-") + (spanId)) + "-") + (parentId)) + "a"))).isNull();// instead of raising exception

        Mockito.verify(platform).log("Invalid input: parent ID is too long", null);
    }
}

