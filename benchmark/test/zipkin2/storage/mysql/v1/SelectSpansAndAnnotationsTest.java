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
package zipkin2.storage.mysql.v1;


import V1Span.Builder;
import org.jooq.Record4;
import org.junit.Test;
import zipkin2.Endpoint;
import zipkin2.v1.V1Annotation;
import zipkin2.v1.V1BinaryAnnotation;
import zipkin2.v1.V1Span;


public class SelectSpansAndAnnotationsTest {
    @Test
    public void processAnnotationRecord_nulls() {
        Record4<Integer, Long, String, byte[]> annotationRecord = SelectSpansAndAnnotationsTest.annotationRecord(null, null, null, null);
        V1Span.Builder builder = V1Span.newBuilder().traceId(1).id(1);
        SelectSpansAndAnnotations.processAnnotationRecord(annotationRecord, builder, null);
        assertThat(builder).isEqualToComparingFieldByFieldRecursively(V1Span.newBuilder().traceId(1).id(1));
    }

    @Test
    public void processAnnotationRecord_annotation() {
        Record4<Integer, Long, String, byte[]> annotationRecord = SelectSpansAndAnnotationsTest.annotationRecord((-1), 0L, "foo", null);
        V1Span.Builder builder = V1Span.newBuilder().traceId(1).id(1);
        SelectSpansAndAnnotations.processAnnotationRecord(annotationRecord, builder, null);
        assertThat(builder.build().annotations().get(0)).isEqualTo(V1Annotation.create(0L, "foo", null));
    }

    @Test
    public void processAnnotationRecord_tag() {
        Record4<Integer, Long, String, byte[]> annotationRecord = SelectSpansAndAnnotationsTest.annotationRecord(6, null, "foo", new byte[0]);
        V1Span.Builder builder = V1Span.newBuilder().traceId(1).id(1);
        SelectSpansAndAnnotations.processAnnotationRecord(annotationRecord, builder, null);
        assertThat(builder.build().binaryAnnotations().get(0)).isEqualTo(V1BinaryAnnotation.createString("foo", "", null));
    }

    @Test
    public void processAnnotationRecord_address() {
        Record4<Integer, Long, String, byte[]> annotationRecord = SelectSpansAndAnnotationsTest.annotationRecord(0, null, "ca", new byte[]{ 1 });
        Endpoint ep = Endpoint.newBuilder().serviceName("foo").build();
        V1Span.Builder builder = V1Span.newBuilder().traceId(1).id(1);
        SelectSpansAndAnnotations.processAnnotationRecord(annotationRecord, builder, ep);
        assertThat(builder.build().binaryAnnotations().get(0)).isEqualTo(V1BinaryAnnotation.createAddress("ca", ep));
    }

    @Test
    public void processAnnotationRecord_address_skipMissingEndpoint() {
        Record4<Integer, Long, String, byte[]> annotationRecord = SelectSpansAndAnnotationsTest.annotationRecord(0, null, "ca", new byte[]{ 1 });
        V1Span.Builder builder = V1Span.newBuilder().traceId(1).id(1);
        SelectSpansAndAnnotations.processAnnotationRecord(annotationRecord, builder, null);
        assertThat(builder.build().binaryAnnotations()).isEmpty();
    }

    @Test
    public void processAnnotationRecord_address_skipWrongKey() {
        Record4<Integer, Long, String, byte[]> annotationRecord = SelectSpansAndAnnotationsTest.annotationRecord(0, null, "sr", new byte[]{ 1 });
        Endpoint ep = Endpoint.newBuilder().serviceName("foo").build();
        V1Span.Builder builder = V1Span.newBuilder().traceId(1).id(1);
        SelectSpansAndAnnotations.processAnnotationRecord(annotationRecord, builder, ep);
        assertThat(builder.build().binaryAnnotations()).isEmpty();
    }

    @Test
    public void endpoint_justIpv4() {
        Record4<String, Integer, Short, byte[]> endpointRecord = SelectSpansAndAnnotationsTest.endpointRecord("", ((127 << 24) | 1), ((short) (0)), new byte[0]);
        assertThat(SelectSpansAndAnnotations.endpoint(endpointRecord)).isEqualTo(Endpoint.newBuilder().ip("127.0.0.1").build());
    }
}

