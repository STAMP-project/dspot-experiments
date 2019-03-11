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
package zipkin2.internal;


import Span.Kind;
import Span.Kind.CONSUMER;
import Span.Kind.PRODUCER;
import Span.Kind.SERVER;
import org.junit.Test;
import zipkin2.Annotation;
import zipkin2.Endpoint;
import zipkin2.TestObjects;
import zipkin2.internal.Proto3ZipkinFields.AnnotationField;
import zipkin2.internal.Proto3ZipkinFields.EndpointField;
import zipkin2.internal.Proto3ZipkinFields.TagField;


public class Proto3ZipkinFieldsTest {
    Buffer buf = new Buffer(2048);// bigger than needed to test sizeInBytes


    /**
     * A map entry is an embedded messages: one for field the key and one for the value
     */
    @Test
    public void tag_sizeInBytes() {
        TagField field = new TagField(((1 << 3) | (Proto3Fields.WIRETYPE_LENGTH_DELIMITED)));
        /* len */
        assertThat(field.sizeInBytes(entry("123", "56789"))).isEqualTo(((((((((0 + 1)/* tag of embedded key field */
         + 1)/* len */
         + 3) + 1)/* tag of embedded value field */
         + 1)/* len */
         + 5) + 1)/* tag of map entry field */
         + 1));
    }

    @Test
    public void annotation_sizeInBytes() {
        AnnotationField field = new AnnotationField(((1 << 3) | (Proto3Fields.WIRETYPE_LENGTH_DELIMITED)));
        /* len */
        assertThat(field.sizeInBytes(Annotation.create(1L, "12345678"))).isEqualTo((((((((0 + 1)/* tag of timestamp field */
         + 8)/* 8 byte number */
         + 1)/* tag of value field */
         + 1)/* len */
         + 8)// 12345678
         + 1)/* tag of annotation field */
         + 1));
    }

    @Test
    public void endpoint_sizeInBytes() {
        EndpointField field = new EndpointField(((1 << 3) | (Proto3Fields.WIRETYPE_LENGTH_DELIMITED)));
        /* len */
        assertThat(field.sizeInBytes(Endpoint.newBuilder().serviceName("12345678").ip("192.168.99.101").ip("2001:db8::c001").port(80).build())).isEqualTo((((((((((((((0 + 1)/* tag of servicename field */
         + 1)/* len */
         + 8)// 12345678
         + 1)/* tag of ipv4 field */
         + 1)/* len */
         + 4)// octets in ipv4
         + 1)/* tag of ipv6 field */
         + 1)/* len */
         + 16)// octets in ipv6
         + 1)/* tag of port field */
         + 1)/* small varint */
         + 1)/* tag of endpoint field */
         + 1));
    }

    @Test
    public void span_write_startsWithFieldInListOfSpans() {
        Proto3ZipkinFields.SPAN.write(buf, Proto3ZipkinFieldsTest.spanBuilder().build());
        /* span key */
        /* bytes for length of the span */
        assertThat(buf.toByteArray()).startsWith(10, 20);
    }

    @Test
    public void span_write_writesIds() {
        Proto3ZipkinFields.SPAN.write(buf, Proto3ZipkinFieldsTest.spanBuilder().build());
        /* span key */
        /* bytes for length of the span */
        /* trace ID key */
        /* bytes for 64-bit trace ID */
        // hex trace ID
        /* span ID key */
        /* bytes for 64-bit span ID */
        // hex span ID
        assertThat(buf.toByteArray()).startsWith(10, 20, 10, 8, 0, 0, 0, 0, 0, 0, 0, 1, 26, 8, 0, 0, 0, 0, 0, 0, 0, 2);
        /* 64-bit fields */
        assertThat(buf.pos).isEqualTo(((3 * 2)/* overhead of three fields */
         + (2 * 8))).isEqualTo(22);// easier math on the next test

    }

    @Test
    public void span_read_ids() {
        assertRoundTrip(Proto3ZipkinFieldsTest.spanBuilder().parentId("1").build());
    }

    @Test
    public void span_read_name() {
        assertRoundTrip(Proto3ZipkinFieldsTest.spanBuilder().name("romeo").build());
    }

    @Test
    public void span_read_kind() {
        assertRoundTrip(Proto3ZipkinFieldsTest.spanBuilder().kind(CONSUMER).build());
    }

    @Test
    public void span_read_timestamp_duration() {
        assertRoundTrip(Proto3ZipkinFieldsTest.spanBuilder().timestamp(TestObjects.TODAY).duration(134).build());
    }

    @Test
    public void span_read_endpoints() {
        assertRoundTrip(Proto3ZipkinFieldsTest.spanBuilder().localEndpoint(TestObjects.FRONTEND).remoteEndpoint(TestObjects.BACKEND).build());
    }

    @Test
    public void span_read_annotation() {
        assertRoundTrip(Proto3ZipkinFieldsTest.spanBuilder().addAnnotation(TestObjects.TODAY, "parked on sidewalk").build());
    }

    @Test
    public void span_read_tag() {
        assertRoundTrip(Proto3ZipkinFieldsTest.spanBuilder().putTag("foo", "bar").build());
    }

    @Test
    public void span_read_tag_empty() {
        assertRoundTrip(Proto3ZipkinFieldsTest.spanBuilder().putTag("empty", "").build());
    }

    @Test
    public void span_read_shared() {
        assertRoundTrip(Proto3ZipkinFieldsTest.spanBuilder().shared(true).build());
    }

    @Test
    public void span_read_debug() {
        assertRoundTrip(Proto3ZipkinFieldsTest.spanBuilder().debug(true).build());
    }

    @Test
    public void span_read() {
        assertRoundTrip(TestObjects.CLIENT_SPAN);
    }

    @Test
    public void span_write_omitsEmptyEndpoints() {
        Proto3ZipkinFields.SPAN.write(buf, Proto3ZipkinFieldsTest.spanBuilder().localEndpoint(Endpoint.newBuilder().build()).remoteEndpoint(Endpoint.newBuilder().build()).build());
        assertThat(buf.pos).isEqualTo(22);
    }

    @Test
    public void span_write_kind() {
        Proto3ZipkinFields.SPAN.write(buf, Proto3ZipkinFieldsTest.spanBuilder().kind(PRODUCER).build());
        // (field_number << 3) | wire_type = 4 << 3 | 0
        assertThat(buf.toByteArray()).contains(32, atIndex(22)).contains(3, atIndex(23));// producer's index is 3

    }

    @Test
    public void span_read_kind_tolerant() {
        assertRoundTrip(Proto3ZipkinFieldsTest.spanBuilder().kind(CONSUMER).build());
        buf.pos = 0;
        buf.toByteArray()[23] = ((byte) ((Kind.values().length) + 1));// undefined kind

        assertThat(Proto3ZipkinFields.SPAN.read(buf)).isEqualTo(Proto3ZipkinFieldsTest.spanBuilder().build());// skips undefined kind instead of dying

        buf.pos = 0;
        buf.toByteArray()[23] = 0;// serialized zero

        assertThat(Proto3ZipkinFields.SPAN.read(buf)).isEqualTo(Proto3ZipkinFieldsTest.spanBuilder().build());
    }

    @Test
    public void span_write_debug() {
        Proto3ZipkinFields.SPAN.write(buf, TestObjects.CLIENT_SPAN.toBuilder().debug(true).build());
        // (field_number << 3) | wire_type = 12 << 3 | 0
        assertThat(buf.toByteArray()).contains(96, atIndex(((buf.pos) - 2))).contains(1, atIndex(((buf.pos) - 1)));// true

    }

    @Test
    public void span_write_shared() {
        Proto3ZipkinFields.SPAN.write(buf, TestObjects.CLIENT_SPAN.toBuilder().kind(SERVER).shared(true).build());
        // (field_number << 3) | wire_type = 13 << 3 | 0
        assertThat(buf.toByteArray()).contains(104, atIndex(((buf.pos) - 2))).contains(1, atIndex(((buf.pos) - 1)));// true

    }
}

