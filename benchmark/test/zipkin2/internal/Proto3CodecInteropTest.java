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


import ANNOTATION.key;
import Span.Kind;
import SpanBytesEncoder.PROTO3;
import com.google.protobuf.ByteString;
import com.google.protobuf.CodedOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.assertj.core.data.MapEntry;
import org.junit.Test;
import zipkin2.Span.Builder;
import zipkin2.Span.Kind.SERVER;
import zipkin2.internal.Proto3ZipkinFields.EndpointField;
import zipkin2.internal.Proto3ZipkinFields.TagField;
import zipkin2.proto3.Annotation;
import zipkin2.proto3.Endpoint;
import zipkin2.proto3.ListOfSpans;
import zipkin2.proto3.Span;
import zipkin2.proto3.zipkin2.Annotation;
import zipkin2.proto3.zipkin2.Endpoint;
import zipkin2.proto3.zipkin2.Span;

import static SpanField.TAG_KEY;


public class Proto3CodecInteropTest {
    static final Endpoint ORDER = zipkin2.Endpoint.newBuilder().serviceName("??????").ip("2001:db8::c001").build();

    static final Endpoint PROFILE = zipkin2.Endpoint.newBuilder().serviceName("??????").ip("192.168.99.101").port(9000).build();

    static final Span ZIPKIN_SPAN = zipkin2.Span.newBuilder().traceId("4d1e00c0db9010db86154a4ba6e91385").parentId("86154a4ba6e91385").id("4d1e00c0db9010db").kind(SERVER).name("??????").timestamp(1472470996199000L).duration(207000L).localEndpoint(Proto3CodecInteropTest.ORDER).remoteEndpoint(Proto3CodecInteropTest.PROFILE).addAnnotation(1472470996199000L, "foo happened").putTag("http.path", "/person/profile/query").putTag("http.status_code", "403").putTag("clnt/finagle.version", "6.45.0").putTag("error", "?????????").shared(true).build();

    static final List<zipkin2.Span> ZIPKIN_SPANS = Arrays.asList(Proto3CodecInteropTest.ZIPKIN_SPAN, Proto3CodecInteropTest.ZIPKIN_SPAN);

    static final Span PROTO_SPAN = Span.newBuilder().setTraceId(Proto3CodecInteropTest.decodeHex(Proto3CodecInteropTest.ZIPKIN_SPAN.traceId())).setParentId(Proto3CodecInteropTest.decodeHex(Proto3CodecInteropTest.ZIPKIN_SPAN.parentId())).setId(Proto3CodecInteropTest.decodeHex(Proto3CodecInteropTest.ZIPKIN_SPAN.id())).setKind(Kind.valueOf(Proto3CodecInteropTest.ZIPKIN_SPAN.kind().name())).setName(Proto3CodecInteropTest.ZIPKIN_SPAN.name()).setTimestamp(Proto3CodecInteropTest.ZIPKIN_SPAN.timestampAsLong()).setDuration(Proto3CodecInteropTest.ZIPKIN_SPAN.durationAsLong()).setLocalEndpoint(Endpoint.newBuilder().setServiceName(Proto3CodecInteropTest.ORDER.serviceName()).setIpv6(ByteString.copyFrom(Proto3CodecInteropTest.ORDER.ipv6Bytes())).build()).setRemoteEndpoint(Endpoint.newBuilder().setServiceName(Proto3CodecInteropTest.PROFILE.serviceName()).setIpv4(ByteString.copyFrom(Proto3CodecInteropTest.PROFILE.ipv4Bytes())).setPort(Proto3CodecInteropTest.PROFILE.portAsInt()).build()).addAnnotations(Annotation.newBuilder().setTimestamp(Proto3CodecInteropTest.ZIPKIN_SPAN.annotations().get(0).timestamp()).setValue(Proto3CodecInteropTest.ZIPKIN_SPAN.annotations().get(0).value()).build()).putAllTags(Proto3CodecInteropTest.ZIPKIN_SPAN.tags()).setShared(true).build();

    ListOfSpans PROTO_SPANS = ListOfSpans.newBuilder().addSpans(Proto3CodecInteropTest.PROTO_SPAN).addSpans(Proto3CodecInteropTest.PROTO_SPAN).build();

    @Test
    public void encodeIsCompatible() throws Exception {
        byte[] buff = new byte[computeMessageSize(1, Proto3CodecInteropTest.PROTO_SPAN)];
        CodedOutputStream out = CodedOutputStream.newInstance(buff);
        out.writeMessage(1, Proto3CodecInteropTest.PROTO_SPAN);
        assertThat(PROTO3.encode(Proto3CodecInteropTest.ZIPKIN_SPAN)).containsExactly(buff);
    }

    @Test
    public void decodeOneIsCompatible() {
        assertThat(SpanBytesDecoder.PROTO3.decodeOne(PROTO_SPANS.toByteArray())).isEqualTo(Proto3CodecInteropTest.ZIPKIN_SPAN);
    }

    @Test
    public void decodeListIsCompatible() {
        assertThat(SpanBytesDecoder.PROTO3.decodeList(PROTO_SPANS.toByteArray())).containsExactly(Proto3CodecInteropTest.ZIPKIN_SPAN, Proto3CodecInteropTest.ZIPKIN_SPAN);
    }

    @Test
    public void encodeListIsCompatible_buff() throws Exception {
        byte[] buff = new byte[PROTO_SPANS.getSerializedSize()];
        CodedOutputStream out = CodedOutputStream.newInstance(buff);
        PROTO_SPANS.writeTo(out);
        byte[] zipkin_buff = new byte[10 + (buff.length)];
        assertThat(PROTO3.encodeList(Proto3CodecInteropTest.ZIPKIN_SPANS, zipkin_buff, 5)).isEqualTo(buff.length);
        assertThat(zipkin_buff).startsWith(0, 0, 0, 0, 0).containsSequence(buff).endsWith(0, 0, 0, 0, 0);
    }

    @Test
    public void encodeListIsCompatible() throws Exception {
        byte[] buff = new byte[PROTO_SPANS.getSerializedSize()];
        CodedOutputStream out = CodedOutputStream.newInstance(buff);
        PROTO_SPANS.writeTo(out);
        assertThat(PROTO3.encodeList(Proto3CodecInteropTest.ZIPKIN_SPANS)).containsExactly(buff);
    }

    @Test
    public void span_sizeInBytes_matchesProto3() {
        assertThat(Proto3ZipkinFields.SPAN.sizeInBytes(Proto3CodecInteropTest.ZIPKIN_SPAN)).isEqualTo(computeMessageSize(SPAN.fieldNumber, Proto3CodecInteropTest.PROTO_SPAN));
    }

    @Test
    public void annotation_sizeInBytes_matchesProto3() {
        zipkin2.Annotation zipkinAnnotation = Proto3CodecInteropTest.ZIPKIN_SPAN.annotations().get(0);
        assertThat(SpanField.ANNOTATION.sizeInBytes(zipkinAnnotation)).isEqualTo(computeMessageSize(ANNOTATION.fieldNumber, Proto3CodecInteropTest.PROTO_SPAN.getAnnotations(0)));
    }

    @Test
    public void annotation_write_matchesProto3() throws IOException {
        zipkin2.Annotation zipkinAnnotation = Proto3CodecInteropTest.ZIPKIN_SPAN.annotations().get(0);
        Annotation protoAnnotation = Proto3CodecInteropTest.PROTO_SPAN.getAnnotations(0);
        Buffer buffer = new Buffer(SpanField.ANNOTATION.sizeInBytes(zipkinAnnotation));
        SpanField.ANNOTATION.write(buffer, zipkinAnnotation);
        assertThat(buffer.toByteArray()).containsExactly(Proto3CodecInteropTest.writeSpan(Span.newBuilder().addAnnotations(protoAnnotation).build()));
    }

    @Test
    public void annotation_read_matchesProto3() throws IOException {
        zipkin2.Annotation zipkinAnnotation = Proto3CodecInteropTest.ZIPKIN_SPAN.annotations().get(0);
        Annotation protoAnnotation = Proto3CodecInteropTest.PROTO_SPAN.getAnnotations(0);
        Buffer buffer = new Buffer(Proto3CodecInteropTest.writeSpan(Span.newBuilder().addAnnotations(protoAnnotation).build()), 0);
        assertThat(buffer.readVarint32()).isEqualTo(key);
        Builder builder = Proto3CodecInteropTest.zipkinSpanBuilder();
        SpanField.ANNOTATION.readLengthPrefixAndValue(buffer, builder);
        assertThat(builder.build().annotations()).containsExactly(zipkinAnnotation);
    }

    @Test
    public void endpoint_sizeInBytes_matchesProto3() {
        assertThat(SpanField.LOCAL_ENDPOINT.sizeInBytes(Proto3CodecInteropTest.ZIPKIN_SPAN.localEndpoint())).isEqualTo(computeMessageSize(LOCAL_ENDPOINT.fieldNumber, Proto3CodecInteropTest.PROTO_SPAN.getLocalEndpoint()));
        assertThat(SpanField.REMOTE_ENDPOINT.sizeInBytes(Proto3CodecInteropTest.ZIPKIN_SPAN.remoteEndpoint())).isEqualTo(computeMessageSize(REMOTE_ENDPOINT.fieldNumber, Proto3CodecInteropTest.PROTO_SPAN.getRemoteEndpoint()));
    }

    @Test
    public void localEndpoint_write_matchesProto3() throws IOException {
        Buffer buffer = new Buffer(SpanField.LOCAL_ENDPOINT.sizeInBytes(Proto3CodecInteropTest.ZIPKIN_SPAN.localEndpoint()));
        SpanField.LOCAL_ENDPOINT.write(buffer, Proto3CodecInteropTest.ZIPKIN_SPAN.localEndpoint());
        assertThat(buffer.toByteArray()).containsExactly(Proto3CodecInteropTest.writeSpan(Span.newBuilder().setLocalEndpoint(Proto3CodecInteropTest.PROTO_SPAN.getLocalEndpoint()).build()));
    }

    @Test
    public void remoteEndpoint_write_matchesProto3() throws IOException {
        Buffer buffer = new Buffer(SpanField.REMOTE_ENDPOINT.sizeInBytes(Proto3CodecInteropTest.ZIPKIN_SPAN.remoteEndpoint()));
        SpanField.REMOTE_ENDPOINT.write(buffer, Proto3CodecInteropTest.ZIPKIN_SPAN.remoteEndpoint());
        assertThat(buffer.toByteArray()).containsExactly(Proto3CodecInteropTest.writeSpan(Span.newBuilder().setRemoteEndpoint(Proto3CodecInteropTest.PROTO_SPAN.getRemoteEndpoint()).build()));
    }

    @Test
    public void utf8_sizeInBytes_matchesProto3() {
        assertThat(new zipkin2.internal.Proto3Fields.Utf8Field(EndpointField.SERVICE_NAME_KEY).sizeInBytes(Proto3CodecInteropTest.ORDER.serviceName())).isEqualTo(CodedOutputStream.computeStringSize(1, Proto3CodecInteropTest.ORDER.serviceName()));
    }

    @Test
    public void tag_sizeInBytes_matchesProto3() {
        MapEntry<String, String> entry = entry("clnt/finagle.version", "6.45.0");
        assertThat(new TagField(TAG_KEY).sizeInBytes(entry)).isEqualTo(Span.newBuilder().putTags(entry.key, entry.value).build().getSerializedSize());
    }

    @Test
    public void writeTagField_matchesProto3() throws IOException {
        MapEntry<String, String> entry = entry("clnt/finagle.version", "6.45.0");
        TagField field = new TagField(TAG_KEY);
        Buffer buffer = new Buffer(field.sizeInBytes(entry));
        field.write(buffer, entry);
        Span oneField = Span.newBuilder().putTags(entry.key, entry.value).build();
        byte[] buff = new byte[oneField.getSerializedSize()];
        CodedOutputStream out = CodedOutputStream.newInstance(buff);
        oneField.writeTo(out);
        assertThat(buffer.toByteArray()).containsExactly(buff);
    }
}

