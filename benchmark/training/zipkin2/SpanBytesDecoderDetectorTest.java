/**
 * Copyright 2015-2019 The OpenZipkin Authors
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
package zipkin2;


import Span.Kind.CLIENT;
import Span.Kind.SERVER;
import SpanBytesEncoder.JSON_V1;
import SpanBytesEncoder.JSON_V2;
import SpanBytesEncoder.PROTO3;
import SpanBytesEncoder.THRIFT;
import java.util.Arrays;
import org.junit.Test;


public class SpanBytesDecoderDetectorTest {
    Span span1 = Span.newBuilder().traceId("a").id("b").name("get").timestamp(10).duration(30).kind(SERVER).shared(true).putTag("http.method", "GET").localEndpoint(TestObjects.FRONTEND).build();

    Span span2 = Span.newBuilder().traceId("a").parentId("b").id("c").name("get").timestamp(15).duration(10).localEndpoint(TestObjects.FRONTEND).build();

    @Test
    public void decoderForMessage_json_v1() {
        byte[] message = JSON_V1.encode(span1);
        assertThat(SpanBytesDecoderDetector.decoderForMessage(message)).isEqualTo(SpanBytesDecoder.JSON_V1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void decoderForMessage_json_v1_list() {
        byte[] message = JSON_V1.encodeList(Arrays.asList(span1, span2));
        SpanBytesDecoderDetector.decoderForMessage(message);
    }

    @Test
    public void decoderForListMessage_json_v1() {
        byte[] message = JSON_V1.encodeList(Arrays.asList(span1, span2));
        assertThat(SpanBytesDecoderDetector.decoderForListMessage(message)).isEqualTo(SpanBytesDecoder.JSON_V1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void decoderForListMessage_json_v1_singleItem() {
        byte[] message = JSON_V1.encode(span1);
        SpanBytesDecoderDetector.decoderForListMessage(message);
    }

    /**
     * Single-element reads were for legacy non-list encoding. Don't add new code that does this
     */
    @Test(expected = UnsupportedOperationException.class)
    public void decoderForMessage_json_v2() {
        byte[] message = JSON_V2.encode(span1);
        assertThat(SpanBytesDecoderDetector.decoderForMessage(message)).isEqualTo(SpanBytesDecoder.JSON_V2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void decoderForMessage_json_v2_list() {
        byte[] message = JSON_V2.encodeList(Arrays.asList(span1, span2));
        SpanBytesDecoderDetector.decoderForMessage(message);
    }

    @Test
    public void decoderForListMessage_json_v2() {
        byte[] message = JSON_V2.encodeList(Arrays.asList(span1, span2));
        assertThat(SpanBytesDecoderDetector.decoderForListMessage(message)).isEqualTo(SpanBytesDecoder.JSON_V2);
    }

    @Test
    public void decoderForListMessage_json_v2_partial_localEndpoint() {
        Span span = Span.newBuilder().traceId("a").id("b").localEndpoint(Endpoint.newBuilder().serviceName("foo").build()).build();
        byte[] message = JSON_V2.encodeList(Arrays.asList(span));
        assertThat(SpanBytesDecoderDetector.decoderForListMessage(message)).isEqualTo(SpanBytesDecoder.JSON_V2);
    }

    @Test
    public void decoderForListMessage_json_v2_partial_remoteEndpoint() {
        Span span = Span.newBuilder().traceId("a").id("b").kind(CLIENT).remoteEndpoint(Endpoint.newBuilder().serviceName("foo").build()).build();
        byte[] message = JSON_V2.encodeList(Arrays.asList(span));
        assertThat(SpanBytesDecoderDetector.decoderForListMessage(message)).isEqualTo(SpanBytesDecoder.JSON_V2);
    }

    @Test
    public void decoderForListMessage_json_v2_partial_tag() {
        Span span = Span.newBuilder().traceId("a").id("b").putTag("foo", "bar").build();
        byte[] message = JSON_V2.encodeList(Arrays.asList(span));
        assertThat(SpanBytesDecoderDetector.decoderForListMessage(message)).isEqualTo(SpanBytesDecoder.JSON_V2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void decoderForListMessage_json_v2_singleItem() {
        byte[] message = JSON_V2.encode(span1);
        SpanBytesDecoderDetector.decoderForListMessage(message);
    }

    @Test
    public void decoderForMessage_thrift() {
        byte[] message = THRIFT.encode(span1);
        assertThat(SpanBytesDecoderDetector.decoderForMessage(message)).isEqualTo(SpanBytesDecoder.THRIFT);
    }

    @Test(expected = IllegalArgumentException.class)
    public void decoderForMessage_thrift_list() {
        byte[] message = THRIFT.encodeList(Arrays.asList(span1, span2));
        SpanBytesDecoderDetector.decoderForMessage(message);
    }

    @Test
    public void decoderForListMessage_thrift() {
        byte[] message = THRIFT.encodeList(Arrays.asList(span1, span2));
        assertThat(SpanBytesDecoderDetector.decoderForListMessage(message)).isEqualTo(SpanBytesDecoder.THRIFT);
    }

    /**
     * We encoded incorrectly for years, so we have to read this data eventhough it is wrong.
     *
     * <p>See openzipkin/zipkin-reporter-java#133
     */
    @Test
    public void decoderForListMessage_thrift_incorrectFirstByte() {
        byte[] message = THRIFT.encodeList(Arrays.asList(span1, span2));
        message[0] = 11;// We made a typo.. it should have been 12

        assertThat(SpanBytesDecoderDetector.decoderForListMessage(message)).isEqualTo(SpanBytesDecoder.THRIFT);
    }

    @Test(expected = IllegalArgumentException.class)
    public void decoderForListMessage_thrift_singleItem() {
        byte[] message = THRIFT.encode(span1);
        SpanBytesDecoderDetector.decoderForListMessage(message);
    }

    /**
     * Single-element reads were for legacy non-list encoding. Don't add new code that does this
     */
    @Test(expected = UnsupportedOperationException.class)
    public void decoderForMessage_proto3() {
        byte[] message = PROTO3.encode(span1);
        assertThat(SpanBytesDecoderDetector.decoderForMessage(message)).isEqualTo(SpanBytesDecoder.PROTO3);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void decoderForMessage_proto3_list() {
        byte[] message = PROTO3.encodeList(Arrays.asList(span1, span2));
        SpanBytesDecoderDetector.decoderForMessage(message);
    }

    @Test
    public void decoderForListMessage_proto3() {
        byte[] message = PROTO3.encodeList(Arrays.asList(span1, span2));
        assertThat(SpanBytesDecoderDetector.decoderForListMessage(message)).isEqualTo(SpanBytesDecoder.PROTO3);
    }

    /**
     * There is no difference between a list of size one and a single element in proto3
     */
    @Test
    public void decoderForListMessage_proto3_singleItem() {
        byte[] message = PROTO3.encode(span1);
        assertThat(SpanBytesDecoderDetector.decoderForListMessage(message)).isEqualTo(SpanBytesDecoder.PROTO3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void decoderForMessage_unknown() {
        SpanBytesDecoderDetector.decoderForMessage(new byte[]{ 'h' });
    }

    @Test(expected = IllegalArgumentException.class)
    public void decoderForListMessage_unknown() {
        SpanBytesDecoderDetector.decoderForListMessage(new byte[]{ 'h' });
    }
}

