package org.stagemonitor.tracing.reporter;


import com.fasterxml.jackson.databind.node.ObjectNode;
import io.opentracing.Span;
import io.opentracing.mock.MockTracer;
import java.util.Collections;
import java.util.function.Consumer;
import org.junit.Assert;
import org.junit.Test;
import org.stagemonitor.core.util.JsonUtils;
import org.stagemonitor.tracing.utils.SpanUtils;
import org.stagemonitor.tracing.wrapper.SpanWrapper;


public class SpanJsonModuleTest {
    private MockTracer mockTracer;

    @Test
    public void testSetReservedTagName() {
        final SpanWrapper span = createTestSpan(1, ( s) -> s.setTag("duration_ms", "foo"));
        final ObjectNode jsonSpan = JsonUtils.toObjectNode(span);
        Assert.assertEquals(jsonSpan.toString(), 1, jsonSpan.get("duration_ms").intValue());
    }

    @Test
    public void testIgnoreInternalTags() {
        final SpanWrapper span = createTestSpan(1, ( s) -> s.setTag("internal_foo", "bar"));
        final ObjectNode jsonSpan = JsonUtils.toObjectNode(span);
        assertThat(jsonSpan.get("internal_foo")).isNull();
    }

    @Test
    public void testParameters() {
        final SpanWrapper span = createTestSpan(1, ( s) -> SpanUtils.setParameters(s, Collections.singletonMap("foo", "bar")));
        final ObjectNode jsonSpan = JsonUtils.toObjectNode(span);
        assertThat(jsonSpan.get("parameters")).isNotNull();
        assertThat(jsonSpan.get("parameters").get(0)).isNotNull();
        assertThat(jsonSpan.get("parameters").get(0).get("key").asText()).isEqualTo("foo");
        assertThat(jsonSpan.get("parameters").get(0).get("value").asText()).isEqualTo("bar");
    }
}

