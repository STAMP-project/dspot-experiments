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


import ThriftField.TYPE_BOOL;
import ThriftField.TYPE_STRING;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.junit.Test;
import zipkin2.Endpoint;
import zipkin2.Span;


public class V1ThriftSpanWriterTest {
    Span span = Span.newBuilder().traceId("1").id("2").build();

    Endpoint endpoint = Endpoint.newBuilder().serviceName("frontend").ip("1.2.3.4").build();

    Buffer buf = new Buffer(2048);// bigger than needed to test sizeOf


    V1ThriftSpanWriter writer = new V1ThriftSpanWriter();

    byte[] endpointBytes;

    @Test
    public void endpoint_highPort() {
        int highPort = 63840;
        Endpoint endpoint = Endpoint.newBuilder().ip("127.0.0.1").port(63840).build();
        Buffer endpointBuffer = new Buffer(ThriftEndpointCodec.sizeInBytes(endpoint));
        ThriftEndpointCodec.write(endpoint, endpointBuffer);
        byte[] buff = endpointBuffer.toByteArray();
        // ipv4
        assertThat(buff).containsSequence(ThriftField.TYPE_I32, 0, 1, 127, 0, 0, 1).containsSequence(ThriftField.TYPE_I16, 0, 2, ((highPort >> 8) & 255), (highPort & 255));// port

        assertThat(ThriftEndpointCodec.read(ByteBuffer.wrap(buff)).portAsInt()).isEqualTo(highPort);
    }

    @Test
    public void write_startsWithI64Prefix() {
        byte[] buff = writer.write(span);
        assertThat(buff).hasSize(writer.sizeInBytes(span)).startsWith(ThriftField.TYPE_I64, 0, 1);// short value of field number 1

    }

    @Test
    public void writeList_startsWithListPrefix() {
        byte[] buff = writer.writeList(Arrays.asList(span));
        // member type of the list and an integer with the count
        assertThat(buff).hasSize((5 + (writer.sizeInBytes(span)))).startsWith(ThriftField.TYPE_STRUCT, 0, 0, 0, 1);
    }

    @Test
    public void writeList_startsWithListPrefix_multiple() {
        byte[] buff = writer.writeList(Arrays.asList(span, span));
        // member type of the list and an integer with the count
        assertThat(buff).hasSize((5 + ((writer.sizeInBytes(span)) * 2))).startsWith(ThriftField.TYPE_STRUCT, 0, 0, 0, 2);
    }

    @Test
    public void writeList_empty() {
        assertThat(writer.writeList(Arrays.asList())).isEmpty();
    }

    @Test
    public void writeList_offset_startsWithListPrefix() {
        writer.writeList(Arrays.asList(span, span), buf.toByteArray(), 1);
        // member type of the list and an integer with the count
        assertThat(buf.toByteArray()).startsWith(0, ThriftField.TYPE_STRUCT, 0, 0, 0, 2);
    }

    @Test
    public void doesntWriteAnnotationsWhenMissingTimestamp() {
        writer.write(span.toBuilder().kind(Kind.CLIENT).build(), buf);
        Buffer buf2 = new Buffer(2048);
        writer.write(span, buf2);
        assertThat(buf.toByteArray()).containsExactly(buf.toByteArray());
    }

    @Test
    public void writesCoreAnnotations_client_noEndpoint() {
        writesCoreAnnotationsNoEndpoint(Kind.CLIENT, "cs", "cr");
    }

    @Test
    public void writesCoreAnnotations_server_noEndpoint() {
        writesCoreAnnotationsNoEndpoint(Kind.SERVER, "sr", "ss");
    }

    @Test
    public void writesCoreAnnotations_producer_noEndpoint() {
        writesCoreAnnotationsNoEndpoint(Kind.PRODUCER, "ms", "ws");
    }

    @Test
    public void writesCoreAnnotations_consumer_noEndpoint() {
        writesCoreAnnotationsNoEndpoint(Kind.CONSUMER, "wr", "mr");
    }

    @Test
    public void writesBeginAnnotation_client_noEndpoint() {
        writesBeginAnnotationNoEndpoint(Kind.CLIENT, "cs");
    }

    @Test
    public void writesBeginAnnotation_server_noEndpoint() {
        writesBeginAnnotationNoEndpoint(Kind.SERVER, "sr");
    }

    @Test
    public void writesBeginAnnotation_producer_noEndpoint() {
        writesBeginAnnotationNoEndpoint(Kind.PRODUCER, "ms");
    }

    @Test
    public void writesBeginAnnotation_consumer_noEndpoint() {
        writesBeginAnnotationNoEndpoint(Kind.CONSUMER, "mr");
    }

    @Test
    public void writesAddressBinaryAnnotation_client() {
        writesAddressBinaryAnnotation(Kind.CLIENT, "sa");
    }

    @Test
    public void writesAddressBinaryAnnotation_server() {
        writesAddressBinaryAnnotation(Kind.SERVER, "ca");
    }

    @Test
    public void writesAddressBinaryAnnotation_producer() {
        writesAddressBinaryAnnotation(Kind.PRODUCER, "ma");
    }

    @Test
    public void writesAddressBinaryAnnotation_consumer() {
        writesAddressBinaryAnnotation(Kind.CONSUMER, "ma");
    }

    @Test
    public void annotationsHaveEndpoints() {
        writer.write(span.toBuilder().localEndpoint(endpoint).addAnnotation(5, "foo").build(), buf);
        // value
        // timestamp
        // one annotation
        assertThat(buf.toByteArray()).containsSequence(ThriftField.TYPE_LIST, 0, 6, ThriftField.TYPE_STRUCT, 0, 0, 0, 1).containsSequence(ThriftField.TYPE_I64, 0, 1, 0, 0, 0, 0, 0, 0, 0, 5).containsSequence(ThriftField.TYPE_STRING, 0, 2, 0, 0, 0, 3, 'f', 'o', 'o').containsSequence(endpointBytes);
    }

    @Test
    public void writesTimestampAndDuration() {
        writer.write(span.toBuilder().timestamp(5).duration(10).build(), buf);
        // timestamp
        assertThat(buf.toByteArray()).containsSequence(ThriftField.TYPE_I64, 0, 10, 0, 0, 0, 0, 0, 0, 0, 5).containsSequence(ThriftField.TYPE_I64, 0, 11, 0, 0, 0, 0, 0, 0, 0, 10);// duration

    }

    @Test
    public void skipsTimestampAndDuration_shared() {
        writer.write(span.toBuilder().kind(Kind.SERVER).timestamp(5).duration(10).shared(true).build(), buf);
        Buffer buf2 = new Buffer(2048);
        writer.write(span.toBuilder().kind(Kind.SERVER).build(), buf2);
        assertThat(buf.toByteArray()).containsExactly(buf.toByteArray());
    }

    @Test
    public void writesEmptySpanName() {
        Span span = Span.newBuilder().traceId("1").id("2").build();
        writer.write(span, buf);
        assertThat(buf.toByteArray()).containsSequence(TYPE_STRING, 0, 3, 0, 0, 0, 0);// name (empty is 32 zero bits)

    }

    @Test
    public void writesTraceAndSpanIds() {
        writer.write(span, buf);
        // trace ID
        assertThat(buf.toByteArray()).startsWith(ThriftField.TYPE_I64, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1).containsSequence(ThriftField.TYPE_I64, 0, 4, 0, 0, 0, 0, 0, 0, 0, 2);// ID

    }

    @Test
    public void writesParentAnd128BitTraceId() {
        writer.write(Span.newBuilder().traceId("00000000000000010000000000000002").parentId("3").id("4").build(), buf);
        // trace ID high
        // trace ID
        assertThat(buf.toByteArray()).startsWith(ThriftField.TYPE_I64, 0, 1, 0, 0, 0, 0, 0, 0, 0, 2).containsSequence(ThriftField.TYPE_I64, 0, 12, 0, 0, 0, 0, 0, 0, 0, 1).containsSequence(ThriftField.TYPE_I64, 0, 5, 0, 0, 0, 0, 0, 0, 0, 3);// parent ID

    }

    /**
     * For finagle compatibility
     */
    @Test
    public void writesEmptyAnnotationAndBinaryAnnotations() {
        Span span = Span.newBuilder().traceId("1").id("2").build();
        writer.write(span, buf);
        // empty annotations
        assertThat(buf.toByteArray()).containsSequence(ThriftField.TYPE_LIST, 0, 6, ThriftField.TYPE_STRUCT, 0, 0, 0, 0).containsSequence(ThriftField.TYPE_LIST, 0, 8, ThriftField.TYPE_STRUCT, 0, 0, 0, 0);// empty binary annotations

    }

    @Test
    public void writesEmptyLocalComponentWhenNoAnnotationsOrTags() {
        span = span.toBuilder().name("foo").localEndpoint(endpoint).build();
        writer.write(span, buf);
        // type 6 == string
        // empty value
        // key
        // one binary annotation
        assertThat(buf.toByteArray()).containsSequence(ThriftField.TYPE_LIST, 0, 8, ThriftField.TYPE_STRUCT, 0, 0, 0, 1).containsSequence(ThriftField.TYPE_STRING, 0, 1, 0, 0, 0, 2, 'l', 'c').containsSequence(ThriftField.TYPE_STRING, 0, 2, 0, 0, 0, 0).containsSequence(ThriftField.TYPE_I32, 0, 3, 0, 0, 0, 6).containsSequence(endpointBytes);
    }

    @Test
    public void writesEmptyServiceName() {
        span = span.toBuilder().name("foo").localEndpoint(Endpoint.newBuilder().ip("127.0.0.1").build()).build();
        writer.write(span, buf);
        assertThat(buf.toByteArray()).containsSequence(TYPE_STRING, 0, 3, 0, 0, 0, 0);// serviceName (empty is 32 zero bits)

    }

    /**
     * To match finagle
     */
    @Test
    public void writesDebugFalse() {
        span = span.toBuilder().debug(false).build();
        writer.write(span, buf);
        assertThat(buf.toByteArray()).containsSequence(TYPE_BOOL, 0);
    }

    @Test
    public void tagsAreBinaryAnnotations() {
        writer.write(span.toBuilder().putTag("foo", "bar").build(), buf);
        // value
        // key
        // one binary annotation
        assertThat(buf.toByteArray()).containsSequence(ThriftField.TYPE_LIST, 0, 8, ThriftField.TYPE_STRUCT, 0, 0, 0, 1).containsSequence(ThriftField.TYPE_STRING, 0, 1, 0, 0, 0, 3, 'f', 'o', 'o').containsSequence(ThriftField.TYPE_STRING, 0, 2, 0, 0, 0, 3, 'b', 'a', 'r').containsSequence(ThriftField.TYPE_I32, 0, 3, 0, 0, 0, 6);// type 6 == string

    }
}

