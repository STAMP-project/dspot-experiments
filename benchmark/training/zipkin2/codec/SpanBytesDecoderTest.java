/**
 * Copyright 2015-2018 The OpenZipkin Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package zipkin2.codec;


import Span.Kind.SERVER;
import SpanBytesDecoder.JSON_V2;
import SpanBytesDecoder.PROTO3;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import zipkin2.Endpoint;
import zipkin2.Span;
import zipkin2.TestObjects;


public class SpanBytesDecoderTest {
    Span span = SpanBytesEncoderTest.SPAN;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void spanRoundTrip_JSON_V2() {
        assertThat(JSON_V2.decodeOne(SpanBytesEncoder.JSON_V2.encode(span))).isEqualTo(span);
    }

    @Test
    public void spanRoundTrip_PROTO3() {
        assertThat(PROTO3.decodeOne(SpanBytesEncoder.PROTO3.encode(span))).isEqualTo(span);
    }

    @Test
    public void localSpanRoundTrip_JSON_V2() {
        assertThat(JSON_V2.decodeOne(SpanBytesEncoder.JSON_V2.encode(SpanBytesEncoderTest.LOCAL_SPAN))).isEqualTo(SpanBytesEncoderTest.LOCAL_SPAN);
    }

    @Test
    public void localSpanRoundTrip_PROTO3() {
        assertThat(PROTO3.decodeOne(SpanBytesEncoder.PROTO3.encode(SpanBytesEncoderTest.LOCAL_SPAN))).isEqualTo(SpanBytesEncoderTest.LOCAL_SPAN);
    }

    @Test
    public void spanRoundTrip_64bitTraceId_JSON_V2() {
        span = span.toBuilder().traceId(span.traceId().substring(16)).build();
        assertThat(JSON_V2.decodeOne(SpanBytesEncoder.JSON_V2.encode(span))).isEqualTo(span);
    }

    @Test
    public void spanRoundTrip_64bitTraceId_PROTO3() {
        span = span.toBuilder().traceId(span.traceId().substring(16)).build();
        assertThat(PROTO3.decodeOne(SpanBytesEncoder.PROTO3.encode(span))).isEqualTo(span);
    }

    @Test
    public void spanRoundTrip_shared_JSON_V2() {
        span = span.toBuilder().kind(SERVER).shared(true).build();
        assertThat(JSON_V2.decodeOne(SpanBytesEncoder.JSON_V2.encode(span))).isEqualTo(span);
    }

    @Test
    public void spanRoundTrip_shared_PROTO3() {
        span = span.toBuilder().kind(SERVER).shared(true).build();
        assertThat(PROTO3.decodeOne(SpanBytesEncoder.PROTO3.encode(span))).isEqualTo(span);
    }

    /**
     * This isn't a test of what we "should" accept as a span, rather that characters that trip-up
     * json don't fail in codec.
     */
    @Test
    public void specialCharsInJson_JSON_V2() {
        assertThat(JSON_V2.decodeOne(SpanBytesEncoder.JSON_V2.encode(SpanBytesEncoderTest.UTF8_SPAN))).isEqualTo(SpanBytesEncoderTest.UTF8_SPAN);
    }

    @Test
    public void specialCharsInJson_PROTO3() {
        assertThat(PROTO3.decodeOne(SpanBytesEncoder.PROTO3.encode(SpanBytesEncoderTest.UTF8_SPAN))).isEqualTo(SpanBytesEncoderTest.UTF8_SPAN);
    }

    @Test
    public void falseOnEmpty_inputSpans_JSON_V2() {
        assertThat(JSON_V2.decodeList(new byte[0], new ArrayList())).isFalse();
    }

    @Test
    public void falseOnEmpty_inputSpans_PROTO3() {
        assertThat(PROTO3.decodeList(new byte[0], new ArrayList())).isFalse();
    }

    /**
     * Particulary, thrift can mistake malformed content as a huge list. Let's not blow up.
     */
    @Test
    public void niceErrorOnMalformed_inputSpans_JSON_V2() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Malformed reading List<Span> from ");
        JSON_V2.decodeList(new byte[]{ 'h', 'e', 'l', 'l', 'o' });
    }

    @Test
    public void niceErrorOnMalformed_inputSpans_PROTO3() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Truncated: length 101 > bytes remaining 3 reading List<Span> from proto3");
        PROTO3.decodeList(new byte[]{ 'h', 'e', 'l', 'l', 'o' });
    }

    @Test
    public void traceRoundTrip_JSON_V2() {
        byte[] message = SpanBytesEncoder.JSON_V2.encodeList(TestObjects.TRACE);
        assertThat(JSON_V2.decodeList(message)).isEqualTo(TestObjects.TRACE);
    }

    @Test
    public void traceRoundTrip_PROTO3() {
        byte[] message = SpanBytesEncoder.PROTO3.encodeList(TestObjects.TRACE);
        assertThat(PROTO3.decodeList(message)).isEqualTo(TestObjects.TRACE);
    }

    @Test
    public void spansRoundTrip_JSON_V2() {
        List<Span> tenClientSpans = Collections.nCopies(10, span);
        byte[] message = SpanBytesEncoder.JSON_V2.encodeList(tenClientSpans);
        assertThat(JSON_V2.decodeList(message)).isEqualTo(tenClientSpans);
    }

    @Test
    public void spansRoundTrip_PROTO3() {
        List<Span> tenClientSpans = Collections.nCopies(10, span);
        byte[] message = SpanBytesEncoder.PROTO3.encodeList(tenClientSpans);
        assertThat(PROTO3.decodeList(message)).isEqualTo(tenClientSpans);
    }

    @Test
    public void spanRoundTrip_noRemoteServiceName_JSON_V2() {
        span = span.toBuilder().remoteEndpoint(TestObjects.BACKEND.toBuilder().serviceName(null).build()).build();
        assertThat(JSON_V2.decodeOne(SpanBytesEncoder.JSON_V2.encode(span))).isEqualTo(span);
    }

    @Test
    public void spanRoundTrip_noRemoteServiceName_PROTO3() {
        span = span.toBuilder().remoteEndpoint(TestObjects.BACKEND.toBuilder().serviceName(null).build()).build();
        assertThat(PROTO3.decodeOne(SpanBytesEncoder.PROTO3.encode(span))).isEqualTo(span);
    }

    @Test
    public void spanRoundTrip_noAnnotations_rootServerSpan_JSON_V2() {
        span = SpanBytesEncoderTest.NO_ANNOTATIONS_ROOT_SERVER_SPAN;
        assertThat(JSON_V2.decodeOne(SpanBytesEncoder.JSON_V2.encode(span))).isEqualTo(span);
    }

    @Test
    public void spanRoundTrip_noAnnotations_rootServerSpan_PROTO3() {
        span = SpanBytesEncoderTest.NO_ANNOTATIONS_ROOT_SERVER_SPAN;
        assertThat(PROTO3.decodeOne(SpanBytesEncoder.PROTO3.encode(span))).isEqualTo(span);
    }

    @Test
    public void spanRoundTrip_noAnnotations_rootServerSpan_incomplete_JSON_V2() {
        span = SpanBytesEncoderTest.NO_ANNOTATIONS_ROOT_SERVER_SPAN.toBuilder().duration(null).build();
        assertThat(JSON_V2.decodeOne(SpanBytesEncoder.JSON_V2.encode(span))).isEqualTo(span);
    }

    @Test
    public void spanRoundTrip_noAnnotations_rootServerSpan_incomplete_PROTO3() {
        span = SpanBytesEncoderTest.NO_ANNOTATIONS_ROOT_SERVER_SPAN.toBuilder().duration(null).build();
        assertThat(PROTO3.decodeOne(SpanBytesEncoder.PROTO3.encode(span))).isEqualTo(span);
    }

    @Test
    public void spanRoundTrip_noAnnotations_rootServerSpan_shared_JSON_V2() {
        span = SpanBytesEncoderTest.NO_ANNOTATIONS_ROOT_SERVER_SPAN.toBuilder().shared(true).build();
        assertThat(JSON_V2.decodeOne(SpanBytesEncoder.JSON_V2.encode(span))).isEqualTo(span);
    }

    @Test
    public void spanRoundTrip_noAnnotations_rootServerSpan_shared_PROTO3() {
        span = SpanBytesEncoderTest.NO_ANNOTATIONS_ROOT_SERVER_SPAN.toBuilder().shared(true).build();
        assertThat(PROTO3.decodeOne(SpanBytesEncoder.PROTO3.encode(span))).isEqualTo(span);
    }

    @Test
    public void niceErrorOnUppercase_traceId_JSON_V2() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("48485A3953BB6124 should be lower-hex encoded with no prefix");
        String json = "{\n" + ((("  \"traceId\": \"48485A3953BB6124\",\n" + "  \"name\": \"get-traces\",\n") + "  \"id\": \"6b221d5bc9e6496c\"\n") + "}");
        JSON_V2.decodeOne(json.getBytes(SpanBytesEncoderTest.UTF_8));
    }

    @Test
    public void readsTraceIdHighFromTraceIdField() {
        byte[] with128BitTraceId = ("{\n" + ((("  \"traceId\": \"48485a3953bb61246b221d5bc9e6496c\",\n" + "  \"name\": \"get-traces\",\n") + "  \"id\": \"6b221d5bc9e6496c\"\n") + "}")).getBytes(SpanBytesEncoderTest.UTF_8);
        byte[] withLower64bitsTraceId = ("{\n" + ((("  \"traceId\": \"6b221d5bc9e6496c\",\n" + "  \"name\": \"get-traces\",\n") + "  \"id\": \"6b221d5bc9e6496c\"\n") + "}")).getBytes(SpanBytesEncoderTest.UTF_8);
        assertThat(JSON_V2.decodeOne(with128BitTraceId)).isEqualTo(JSON_V2.decodeOne(withLower64bitsTraceId).toBuilder().traceId("48485a3953bb61246b221d5bc9e6496c").build());
    }

    @Test
    public void ignoresNull_topLevelFields() {
        String json = "{\n" + (((((((((((("  \"traceId\": \"6b221d5bc9e6496c\",\n" + "  \"parentId\": null,\n") + "  \"id\": \"6b221d5bc9e6496c\",\n") + "  \"name\": null,\n") + "  \"timestamp\": null,\n") + "  \"duration\": null,\n") + "  \"localEndpoint\": null,\n") + "  \"remoteEndpoint\": null,\n") + "  \"annotations\": null,\n") + "  \"tags\": null,\n") + "  \"debug\": null,\n") + "  \"shared\": null\n") + "}");
        JSON_V2.decodeOne(json.getBytes(SpanBytesEncoderTest.UTF_8));
    }

    @Test
    public void ignoresNull_endpoint_topLevelFields() {
        String json = "{\n" + ((((((((("  \"traceId\": \"6b221d5bc9e6496c\",\n" + "  \"name\": \"get-traces\",\n") + "  \"id\": \"6b221d5bc9e6496c\",\n") + "  \"localEndpoint\": {\n") + "    \"serviceName\": null,\n") + "    \"ipv4\": \"127.0.0.1\",\n") + "    \"ipv6\": null,\n") + "    \"port\": null\n") + "  }\n") + "}");
        assertThat(JSON_V2.decodeOne(json.getBytes(SpanBytesEncoderTest.UTF_8)).localEndpoint()).isEqualTo(Endpoint.newBuilder().ip("127.0.0.1").build());
    }

    @Test
    public void skipsIncompleteEndpoint() {
        assertThat(JSON_V2.decodeOne(("{\n" + (((((((("  \"traceId\": \"6b221d5bc9e6496c\",\n" + "  \"id\": \"6b221d5bc9e6496c\",\n") + "  \"localEndpoint\": {\n") + "    \"serviceName\": null,\n") + "    \"ipv4\": null,\n") + "    \"ipv6\": null,\n") + "    \"port\": null\n") + "  }\n") + "}")).getBytes(SpanBytesEncoderTest.UTF_8)).localEndpoint()).isNull();
        assertThat(JSON_V2.decodeOne(("{\n" + (((("  \"traceId\": \"6b221d5bc9e6496c\",\n" + "  \"id\": \"6b221d5bc9e6496c\",\n") + "  \"localEndpoint\": {\n") + "  }\n") + "}")).getBytes(SpanBytesEncoderTest.UTF_8)).localEndpoint()).isNull();
        assertThat(JSON_V2.decodeOne(("{\n" + (((((((("  \"traceId\": \"6b221d5bc9e6496c\",\n" + "  \"id\": \"6b221d5bc9e6496c\",\n") + "  \"remoteEndpoint\": {\n") + "    \"serviceName\": null,\n") + "    \"ipv4\": null,\n") + "    \"ipv6\": null,\n") + "    \"port\": null\n") + "  }\n") + "}")).getBytes(SpanBytesEncoderTest.UTF_8)).remoteEndpoint()).isNull();
        assertThat(JSON_V2.decodeOne(("{\n" + (((("  \"traceId\": \"6b221d5bc9e6496c\",\n" + "  \"id\": \"6b221d5bc9e6496c\",\n") + "  \"remoteEndpoint\": {\n") + "  }\n") + "}")).getBytes(SpanBytesEncoderTest.UTF_8)).remoteEndpoint()).isNull();
    }

    @Test
    public void niceErrorOnIncomplete_annotation() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Incomplete annotation at $.annotations[0].timestamp");
        String json = "{\n" + (((((("  \"traceId\": \"6b221d5bc9e6496c\",\n" + "  \"name\": \"get-traces\",\n") + "  \"id\": \"6b221d5bc9e6496c\",\n") + "  \"annotations\": [\n") + "    { \"timestamp\": 1472470996199000}\n") + "  ]\n") + "}");
        JSON_V2.decodeOne(json.getBytes(SpanBytesEncoderTest.UTF_8));
    }

    @Test
    public void niceErrorOnNull_traceId() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Expected a string but was NULL");
        String json = "{\n" + ((("  \"traceId\": null,\n" + "  \"name\": \"get-traces\",\n") + "  \"id\": \"6b221d5bc9e6496c\"\n") + "}");
        JSON_V2.decodeOne(json.getBytes(SpanBytesEncoderTest.UTF_8));
    }

    @Test
    public void niceErrorOnNull_id() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Expected a string but was NULL");
        String json = "{\n" + ((("  \"traceId\": \"6b221d5bc9e6496c\",\n" + "  \"name\": \"get-traces\",\n") + "  \"id\": null\n") + "}");
        JSON_V2.decodeOne(json.getBytes(SpanBytesEncoderTest.UTF_8));
    }

    @Test
    public void niceErrorOnNull_tagValue() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("No value at $.tags.foo");
        String json = "{\n" + (((((("  \"traceId\": \"6b221d5bc9e6496c\",\n" + "  \"name\": \"get-traces\",\n") + "  \"id\": \"6b221d5bc9e6496c\",\n") + "  \"tags\": {\n") + "    \"foo\": NULL\n") + "  }\n") + "}");
        JSON_V2.decodeOne(json.getBytes(SpanBytesEncoderTest.UTF_8));
    }

    @Test
    public void niceErrorOnNull_annotationValue() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("$.annotations[0].value");
        String json = "{\n" + (((((("  \"traceId\": \"6b221d5bc9e6496c\",\n" + "  \"name\": \"get-traces\",\n") + "  \"id\": \"6b221d5bc9e6496c\",\n") + "  \"annotations\": [\n") + "    { \"timestamp\": 1472470996199000, \"value\": NULL}\n") + "  ]\n") + "}");
        JSON_V2.decodeOne(json.getBytes(SpanBytesEncoderTest.UTF_8));
    }

    @Test
    public void niceErrorOnNull_annotationTimestamp() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("$.annotations[0].timestamp");
        String json = "{\n" + (((((("  \"traceId\": \"6b221d5bc9e6496c\",\n" + "  \"name\": \"get-traces\",\n") + "  \"id\": \"6b221d5bc9e6496c\",\n") + "  \"annotations\": [\n") + "    { \"timestamp\": NULL, \"value\": \"foo\"}\n") + "  ]\n") + "}");
        JSON_V2.decodeOne(json.getBytes(SpanBytesEncoderTest.UTF_8));
    }

    @Test
    public void readSpan_localEndpoint_noServiceName() {
        String json = "{\n" + (((((("  \"traceId\": \"6b221d5bc9e6496c\",\n" + "  \"name\": \"get-traces\",\n") + "  \"id\": \"6b221d5bc9e6496c\",\n") + "  \"localEndpoint\": {\n") + "    \"ipv4\": \"127.0.0.1\"\n") + "  }\n") + "}");
        assertThat(JSON_V2.decodeOne(json.getBytes(SpanBytesEncoderTest.UTF_8)).localServiceName()).isNull();
    }

    @Test
    public void readSpan_remoteEndpoint_noServiceName() {
        String json = "{\n" + (((((("  \"traceId\": \"6b221d5bc9e6496c\",\n" + "  \"name\": \"get-traces\",\n") + "  \"id\": \"6b221d5bc9e6496c\",\n") + "  \"remoteEndpoint\": {\n") + "    \"ipv4\": \"127.0.0.1\"\n") + "  }\n") + "}");
        assertThat(JSON_V2.decodeOne(json.getBytes(SpanBytesEncoderTest.UTF_8)).remoteServiceName()).isNull();
    }
}

