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


import Span.Kind.CONSUMER;
import Span.Kind.PRODUCER;
import Span.Kind.SERVER;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.junit.Test;
import zipkin2.Endpoint;
import zipkin2.Span;
import zipkin2.TestObjects;


public class V1JsonSpanWriterTest {
    V1JsonSpanWriter writer = new V1JsonSpanWriter();

    Buffer buf = new Buffer(2048);// bigger than needed to test sizeOf


    @Test
    public void sizeInBytes() {
        writer.write(TestObjects.CLIENT_SPAN, buf);
        assertThat(writer.sizeInBytes(TestObjects.CLIENT_SPAN)).isEqualTo(buf.pos);
    }

    @Test
    public void writesCoreAnnotations_client() throws IOException {
        writer.write(TestObjects.CLIENT_SPAN, buf);
        writesCoreAnnotations("cs", "cr");
    }

    @Test
    public void writesCoreAnnotations_server() throws IOException {
        writer.write(TestObjects.CLIENT_SPAN.toBuilder().kind(SERVER).build(), buf);
        writesCoreAnnotations("sr", "ss");
    }

    @Test
    public void writesCoreAnnotations_producer() throws IOException {
        writer.write(TestObjects.CLIENT_SPAN.toBuilder().kind(PRODUCER).build(), buf);
        writesCoreAnnotations("ms", "ws");
    }

    @Test
    public void writesCoreAnnotations_consumer() throws IOException {
        writer.write(TestObjects.CLIENT_SPAN.toBuilder().kind(CONSUMER).build(), buf);
        writesCoreAnnotations("wr", "mr");
    }

    @Test
    public void writesCoreSendAnnotations_client() throws IOException {
        writer.write(TestObjects.CLIENT_SPAN.toBuilder().duration(null).build(), buf);
        writesCoreSendAnnotations("cs");
    }

    @Test
    public void writesCoreSendAnnotations_server() throws IOException {
        writer.write(TestObjects.CLIENT_SPAN.toBuilder().duration(null).kind(SERVER).build(), buf);
        writesCoreSendAnnotations("sr");
    }

    @Test
    public void writesCoreSendAnnotations_producer() throws IOException {
        writer.write(TestObjects.CLIENT_SPAN.toBuilder().duration(null).kind(PRODUCER).build(), buf);
        writesCoreSendAnnotations("ms");
    }

    @Test
    public void writesCoreSendAnnotations_consumer() throws IOException {
        writer.write(TestObjects.CLIENT_SPAN.toBuilder().duration(null).kind(CONSUMER).build(), buf);
        writesCoreSendAnnotations("mr");
    }

    @Test
    public void writesAddressBinaryAnnotation_client() throws IOException {
        writer.write(TestObjects.CLIENT_SPAN.toBuilder().build(), buf);
        writesAddressBinaryAnnotation("sa");
    }

    @Test
    public void writesAddressBinaryAnnotation_server() throws IOException {
        writer.write(TestObjects.CLIENT_SPAN.toBuilder().kind(SERVER).build(), buf);
        writesAddressBinaryAnnotation("ca");
    }

    @Test
    public void writesAddressBinaryAnnotation_producer() throws IOException {
        writer.write(TestObjects.CLIENT_SPAN.toBuilder().kind(PRODUCER).build(), buf);
        writesAddressBinaryAnnotation("ma");
    }

    @Test
    public void writesAddressBinaryAnnotation_consumer() throws IOException {
        writer.write(TestObjects.CLIENT_SPAN.toBuilder().kind(CONSUMER).build(), buf);
        writesAddressBinaryAnnotation("ma");
    }

    @Test
    public void writes128BitTraceId() throws UnsupportedEncodingException {
        writer.write(TestObjects.CLIENT_SPAN, buf);
        assertThat(new String(buf.toByteArray(), "UTF-8")).startsWith((("{\"traceId\":\"" + (TestObjects.CLIENT_SPAN.traceId())) + "\""));
    }

    @Test
    public void annotationsHaveEndpoints() throws IOException {
        writer.write(TestObjects.CLIENT_SPAN, buf);
        assertThat(new String(buf.toByteArray(), "UTF-8")).contains("\"value\":\"foo\",\"endpoint\":{\"serviceName\":\"frontend\",\"ipv4\":\"127.0.0.1\"}");
    }

    @Test
    public void writesTimestampAndDuration() throws IOException {
        writer.write(TestObjects.CLIENT_SPAN, buf);
        assertThat(new String(buf.toByteArray(), "UTF-8")).contains(((("\"timestamp\":" + (TestObjects.CLIENT_SPAN.timestamp())) + ",\"duration\":") + (TestObjects.CLIENT_SPAN.duration())));
    }

    @Test
    public void skipsTimestampAndDuration_shared() throws IOException {
        writer.write(TestObjects.CLIENT_SPAN.toBuilder().kind(SERVER).shared(true).build(), buf);
        assertThat(new String(buf.toByteArray(), "UTF-8")).doesNotContain(((("\"timestamp\":" + (TestObjects.CLIENT_SPAN.timestamp())) + ",\"duration\":") + (TestObjects.CLIENT_SPAN.duration())));
    }

    @Test
    public void writesEmptySpanName() throws IOException {
        Span span = Span.newBuilder().traceId("7180c278b62e8f6a216a2aea45d08fc9").parentId("6b221d5bc9e6496c").id("5b4185666d50f68b").build();
        writer.write(span, buf);
        assertThat(new String(buf.toByteArray(), "UTF-8")).contains("\"name\":\"\"");
    }

    @Test
    public void writesEmptyServiceName() throws IOException {
        Span span = TestObjects.CLIENT_SPAN.toBuilder().localEndpoint(Endpoint.newBuilder().ip("127.0.0.1").build()).build();
        writer.write(span, buf);
        assertThat(new String(buf.toByteArray(), "UTF-8")).contains("\"value\":\"foo\",\"endpoint\":{\"serviceName\":\"\",\"ipv4\":\"127.0.0.1\"}");
    }

    @Test
    public void tagsAreBinaryAnnotations() throws IOException {
        writer.write(TestObjects.CLIENT_SPAN, buf);
        assertThat(new String(buf.toByteArray(), "UTF-8")).contains(("\"binaryAnnotations\":[" + ("{\"key\":\"clnt/finagle.version\",\"value\":\"6.45.0\",\"endpoint\":{\"serviceName\":\"frontend\",\"ipv4\":\"127.0.0.1\"}}," + "{\"key\":\"http.path\",\"value\":\"/api\",\"endpoint\":{\"serviceName\":\"frontend\",\"ipv4\":\"127.0.0.1\"}}")));
    }
}

