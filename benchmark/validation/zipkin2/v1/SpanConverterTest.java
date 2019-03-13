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
package zipkin2.v1;


import Kind.CLIENT;
import Kind.CONSUMER;
import Kind.PRODUCER;
import Kind.SERVER;
import Span.Builder;
import org.junit.Test;
import zipkin2.Endpoint;
import zipkin2.Span;
import zipkin2.TestObjects;


public class SpanConverterTest {
    Endpoint kafka = Endpoint.newBuilder().serviceName("kafka").build();

    V2SpanConverter v2SpanConverter = new V2SpanConverter();

    V1SpanConverter v1SpanConverter = new V1SpanConverter();

    @Test
    public void client() {
        Span v2 = Span.newBuilder().traceId("1").parentId("2").id("3").name("get").kind(CLIENT).localEndpoint(TestObjects.FRONTEND).remoteEndpoint(TestObjects.BACKEND).timestamp(1472470996199000L).duration(207000L).addAnnotation(1472470996238000L, "ws").addAnnotation(1472470996403000L, "wr").putTag("http.path", "/api").putTag("clnt/finagle.version", "6.45.0").build();
        V1Span v1 = // ts order retained
        V1Span.newBuilder().traceId(1L).parentId(2L).id(3L).name("get").timestamp(1472470996199000L).duration(207000L).addAnnotation(1472470996199000L, "cs", TestObjects.FRONTEND).addAnnotation(1472470996238000L, "ws", TestObjects.FRONTEND).addAnnotation(1472470996403000L, "wr", TestObjects.FRONTEND).addAnnotation(1472470996406000L, "cr", TestObjects.FRONTEND).addBinaryAnnotation("http.path", "/api", TestObjects.FRONTEND).addBinaryAnnotation("clnt/finagle.version", "6.45.0", TestObjects.FRONTEND).addBinaryAnnotation("sa", TestObjects.BACKEND).build();
        assertThat(v2SpanConverter.convert(v2)).isEqualToComparingFieldByFieldRecursively(v1);
        assertThat(v1SpanConverter.convert(v1)).containsExactly(v2);
    }

    @Test
    public void client_unfinished() {
        Span v2 = Span.newBuilder().traceId("1").parentId("2").id("3").name("get").kind(CLIENT).localEndpoint(TestObjects.FRONTEND).timestamp(1472470996199000L).addAnnotation(1472470996238000L, "ws").build();
        V1Span v1 = V1Span.newBuilder().traceId(1L).parentId(2L).id(3L).name("get").timestamp(1472470996199000L).addAnnotation(1472470996199000L, "cs", TestObjects.FRONTEND).addAnnotation(1472470996238000L, "ws", TestObjects.FRONTEND).build();
        assertThat(v2SpanConverter.convert(v2)).isEqualToComparingFieldByFieldRecursively(v1);
        assertThat(v1SpanConverter.convert(v1)).containsExactly(v2);
    }

    @Test
    public void client_kindInferredFromAnnotation() {
        Span v2 = Span.newBuilder().traceId("1").parentId("2").id("3").name("get").localEndpoint(TestObjects.FRONTEND).timestamp(1472470996199000L).duration((1472470996238000L - 1472470996199000L)).addAnnotation(1472470996199000L, "cs").build();
        V1Span v1 = V1Span.newBuilder().traceId(1L).parentId(2L).id(3L).name("get").timestamp(1472470996199000L).duration((1472470996238000L - 1472470996199000L)).addAnnotation(1472470996199000L, "cs", TestObjects.FRONTEND).addAnnotation(1472470996238000L, "cr", TestObjects.FRONTEND).build();
        assertThat(v2SpanConverter.convert(v2)).isEqualToComparingFieldByFieldRecursively(v1);
    }

    @Test
    public void lateRemoteEndpoint_cr() {
        Span v2 = Span.newBuilder().traceId("1").parentId("2").id("3").name("get").kind(CLIENT).localEndpoint(TestObjects.FRONTEND).remoteEndpoint(TestObjects.BACKEND).addAnnotation(1472470996199000L, "cr").build();
        V1Span v1 = V1Span.newBuilder().traceId(1L).parentId(2L).id(3L).name("get").addAnnotation(1472470996199000L, "cr", TestObjects.FRONTEND).addBinaryAnnotation("sa", TestObjects.BACKEND).build();
        assertThat(v2SpanConverter.convert(v2)).isEqualToComparingFieldByFieldRecursively(v1);
        assertThat(v1SpanConverter.convert(v1)).containsExactly(v2);
    }

    @Test
    public void lateRemoteEndpoint_sa() {
        Span v2 = Span.newBuilder().traceId("1").parentId("2").id("3").remoteEndpoint(TestObjects.BACKEND).build();
        V1Span v1 = V1Span.newBuilder().traceId(1L).parentId(2L).id(3L).addBinaryAnnotation("sa", TestObjects.BACKEND).build();
        assertThat(v2SpanConverter.convert(v2)).isEqualToComparingFieldByFieldRecursively(v1);
        assertThat(v1SpanConverter.convert(v1)).containsExactly(v2);
    }

    @Test
    public void noAnnotationsExceptAddresses() {
        Span v2 = Span.newBuilder().traceId("1").parentId("2").id("3").name("get").localEndpoint(TestObjects.FRONTEND).remoteEndpoint(TestObjects.BACKEND).timestamp(1472470996199000L).duration(207000L).build();
        V1Span v1 = V1Span.newBuilder().traceId(1L).parentId(2L).id(3L).name("get").timestamp(1472470996199000L).duration(207000L).addBinaryAnnotation("lc", "", TestObjects.FRONTEND).addBinaryAnnotation("sa", TestObjects.BACKEND).build();
        assertThat(v2SpanConverter.convert(v2)).isEqualToComparingFieldByFieldRecursively(v1);
        assertThat(v1SpanConverter.convert(v1)).containsExactly(v2);
    }

    @Test
    public void server() {
        Span v2 = Span.newBuilder().traceId("1").id("2").name("get").kind(SERVER).localEndpoint(TestObjects.BACKEND).remoteEndpoint(TestObjects.FRONTEND).timestamp(1472470996199000L).duration(207000L).putTag("http.path", "/api").putTag("finagle.version", "6.45.0").build();
        V1Span v1 = V1Span.newBuilder().traceId(1L).id(2L).name("get").timestamp(1472470996199000L).duration(207000L).addAnnotation(1472470996199000L, "sr", TestObjects.BACKEND).addAnnotation(1472470996406000L, "ss", TestObjects.BACKEND).addBinaryAnnotation("http.path", "/api", TestObjects.BACKEND).addBinaryAnnotation("finagle.version", "6.45.0", TestObjects.BACKEND).addBinaryAnnotation("ca", TestObjects.FRONTEND).build();
        assertThat(v2SpanConverter.convert(v2)).isEqualToComparingFieldByFieldRecursively(v1);
        assertThat(v1SpanConverter.convert(v1)).containsExactly(v2);
    }

    /**
     * This shows a historical finagle span, which has client-side socket info.
     */
    @Test
    public void server_clientAddress() {
        Span v2 = Span.newBuilder().traceId("1").id("2").name("get").kind(SERVER).localEndpoint(TestObjects.BACKEND).remoteEndpoint(TestObjects.FRONTEND.toBuilder().port(63840).build()).timestamp(TestObjects.TODAY).duration(207000L).addAnnotation(((TestObjects.TODAY) + 500L), "Gc(9,0.PSScavenge,2015-09-17 12:37:02 +0000,304.milliseconds+762.microseconds)").putTag("srv/finagle.version", "6.28.0").shared(true).build();
        V1Span v1 = // Sometimes, finagle does not add port info on binary annotations/tags, but does elsewhere
        V1Span.newBuilder().traceId("1").id("2").name("get").addAnnotation(v2.timestampAsLong(), "sr", v2.localEndpoint()).addAnnotation(((v2.timestampAsLong()) + 500L), "Gc(9,0.PSScavenge,2015-09-17 12:37:02 +0000,304.milliseconds+762.microseconds)", v2.localEndpoint()).addAnnotation(((v2.timestampAsLong()) + (v2.durationAsLong())), "ss", v2.localEndpoint()).addBinaryAnnotation("srv/finagle.version", "6.28.0", v2.localEndpoint().toBuilder().port(0).build()).addBinaryAnnotation("sa", v2.localEndpoint()).addBinaryAnnotation("ca", v2.remoteEndpoint()).build();
        assertThat(v1SpanConverter.convert(v1)).containsExactly(v2);
    }

    /**
     * Buggy instrumentation can send data with missing endpoints. Make sure we can record it.
     */
    @Test
    public void missingEndpoints() {
        Span v2 = Span.newBuilder().traceId("1").parentId("1").id("2").name("foo").timestamp(1472470996199000L).duration(207000L).build();
        V1Span v1 = V1Span.newBuilder().traceId(1L).parentId(1L).id(2L).name("foo").timestamp(1472470996199000L).duration(207000L).build();
        assertThat(v2SpanConverter.convert(v2)).isEqualToComparingFieldByFieldRecursively(v1);
        assertThat(v1SpanConverter.convert(v1)).containsExactly(v2);
    }

    /**
     * No special treatment for invalid core annotations: missing endpoint
     */
    @Test
    public void missingEndpoints_coreAnnotation() {
        Span v2 = Span.newBuilder().traceId("1").parentId("1").id("2").name("foo").timestamp(1472470996199000L).addAnnotation(1472470996199000L, "sr").build();
        V1Span v1 = V1Span.newBuilder().traceId(1L).parentId(1L).id(2L).name("foo").timestamp(1472470996199000L).addAnnotation(1472470996199000L, "sr", null).build();
        assertThat(v2SpanConverter.convert(v2)).isEqualToComparingFieldByFieldRecursively(v1);
        assertThat(v1SpanConverter.convert(v1)).containsExactly(v2);
    }

    @Test
    public void server_shared_v1_no_timestamp_duration() {
        Span v2 = Span.newBuilder().traceId("1").parentId('2').id("3").name("get").kind(SERVER).shared(true).localEndpoint(TestObjects.BACKEND).timestamp(1472470996199000L).duration(207000L).build();
        V1Span v1 = V1Span.newBuilder().traceId("1").parentId('2').id("3").name("get").addAnnotation(1472470996199000L, "sr", TestObjects.BACKEND).addAnnotation(1472470996406000L, "ss", TestObjects.BACKEND).build();
        assertThat(v2SpanConverter.convert(v2)).isEqualToComparingFieldByFieldRecursively(v1);
        assertThat(v1SpanConverter.convert(v1)).containsExactly(v2);
    }

    @Test
    public void server_incomplete_shared() {
        Span v2 = Span.newBuilder().traceId("1").parentId('2').id("3").name("get").kind(SERVER).shared(true).localEndpoint(TestObjects.BACKEND).timestamp(1472470996199000L).build();
        V1Span v1 = V1Span.newBuilder().traceId("1").parentId('2').id("3").name("get").addAnnotation(1472470996199000L, "sr", TestObjects.BACKEND).build();
        assertThat(v2SpanConverter.convert(v2)).isEqualToComparingFieldByFieldRecursively(v1);
        assertThat(v1SpanConverter.convert(v1)).containsExactly(v2);
    }

    /**
     * Late flushed data on a v2 span
     */
    @Test
    public void lateRemoteEndpoint_ss() {
        Span v2 = Span.newBuilder().traceId("1").id("2").name("get").kind(SERVER).localEndpoint(TestObjects.BACKEND).remoteEndpoint(TestObjects.FRONTEND).addAnnotation(1472470996199000L, "ss").build();
        V1Span v1 = V1Span.newBuilder().traceId(1L).id(2L).name("get").addAnnotation(1472470996199000L, "ss", TestObjects.BACKEND).addBinaryAnnotation("ca", TestObjects.FRONTEND).build();
        assertThat(v2SpanConverter.convert(v2)).isEqualToComparingFieldByFieldRecursively(v1);
        assertThat(v1SpanConverter.convert(v1)).containsExactly(v2);
    }

    /**
     * Late flushed data on a v1 v1
     */
    @Test
    public void lateRemoteEndpoint_ca() {
        Span v2 = Span.newBuilder().traceId("1").id("2").kind(SERVER).remoteEndpoint(TestObjects.FRONTEND).build();
        V1Span v1 = V1Span.newBuilder().traceId(1L).id(2L).addBinaryAnnotation("ca", TestObjects.FRONTEND).build();
        assertThat(v2SpanConverter.convert(v2)).isEqualToComparingFieldByFieldRecursively(v1);
        assertThat(v1SpanConverter.convert(v1)).containsExactly(v2);
    }

    @Test
    public void localSpan_emptyComponent() {
        Span v2 = Span.newBuilder().traceId("1").id("2").name("local").localEndpoint(Endpoint.newBuilder().serviceName("frontend").build()).timestamp(1472470996199000L).duration(207000L).build();
        V1Span v1 = V1Span.newBuilder().traceId(1L).id(2L).name("local").timestamp(1472470996199000L).duration(207000L).addBinaryAnnotation("lc", "", Endpoint.newBuilder().serviceName("frontend").build()).build();
        assertThat(v2SpanConverter.convert(v2)).isEqualToComparingFieldByFieldRecursively(v1);
        assertThat(v1SpanConverter.convert(v1)).containsExactly(v2);
    }

    @Test
    public void producer_remote() {
        Span v2 = Span.newBuilder().traceId("1").parentId("2").id("3").name("send").kind(PRODUCER).localEndpoint(TestObjects.FRONTEND).remoteEndpoint(kafka).timestamp(1472470996199000L).build();
        V1Span v1 = V1Span.newBuilder().traceId(1L).parentId(2L).id(3L).name("send").timestamp(1472470996199000L).addAnnotation(1472470996199000L, "ms", TestObjects.FRONTEND).addBinaryAnnotation("ma", kafka).build();
        assertThat(v2SpanConverter.convert(v2)).isEqualToComparingFieldByFieldRecursively(v1);
        assertThat(v1SpanConverter.convert(v1)).containsExactly(v2);
    }

    @Test
    public void producer_duration() {
        Span v2 = Span.newBuilder().traceId("1").parentId("2").id("3").name("send").kind(PRODUCER).localEndpoint(TestObjects.FRONTEND).timestamp(1472470996199000L).duration(51000L).build();
        V1Span v1 = V1Span.newBuilder().traceId(1L).parentId(2L).id(3L).name("send").timestamp(1472470996199000L).duration(51000L).addAnnotation(1472470996199000L, "ms", TestObjects.FRONTEND).addAnnotation(1472470996250000L, "ws", TestObjects.FRONTEND).build();
        assertThat(v2SpanConverter.convert(v2)).isEqualToComparingFieldByFieldRecursively(v1);
        assertThat(v1SpanConverter.convert(v1)).containsExactly(v2);
    }

    @Test
    public void consumer() {
        Span v2 = Span.newBuilder().traceId("1").parentId("2").id("3").name("next-message").kind(CONSUMER).localEndpoint(TestObjects.BACKEND).timestamp(1472470996199000L).build();
        V1Span v1 = V1Span.newBuilder().traceId(1L).parentId(2L).id(3L).name("next-message").timestamp(1472470996199000L).addAnnotation(1472470996199000L, "mr", TestObjects.BACKEND).build();
        assertThat(v2SpanConverter.convert(v2)).isEqualToComparingFieldByFieldRecursively(v1);
        assertThat(v1SpanConverter.convert(v1)).containsExactly(v2);
    }

    @Test
    public void consumer_remote() {
        Span v2 = Span.newBuilder().traceId("1").parentId("2").id("3").name("next-message").kind(CONSUMER).localEndpoint(TestObjects.BACKEND).remoteEndpoint(kafka).timestamp(1472470996199000L).build();
        V1Span v1 = V1Span.newBuilder().traceId(1L).parentId(2L).id(3L).name("next-message").timestamp(1472470996199000L).addAnnotation(1472470996199000L, "mr", TestObjects.BACKEND).addBinaryAnnotation("ma", kafka).build();
        assertThat(v2SpanConverter.convert(v2)).isEqualToComparingFieldByFieldRecursively(v1);
        assertThat(v1SpanConverter.convert(v1)).containsExactly(v2);
    }

    @Test
    public void consumer_duration() {
        Span v2 = Span.newBuilder().traceId("1").parentId("2").id("3").name("next-message").kind(CONSUMER).localEndpoint(TestObjects.BACKEND).timestamp(1472470996199000L).duration(51000L).build();
        V1Span v1 = V1Span.newBuilder().traceId(1L).parentId(2L).id(3L).name("next-message").timestamp(1472470996199000L).duration(51000L).addAnnotation(1472470996199000L, "wr", TestObjects.BACKEND).addAnnotation(1472470996250000L, "mr", TestObjects.BACKEND).build();
        assertThat(v2SpanConverter.convert(v2)).isEqualToComparingFieldByFieldRecursively(v1);
        assertThat(v1SpanConverter.convert(v1)).containsExactly(v2);
    }

    @Test
    public void clientAndServer() {
        V1Span v1 = V1Span.newBuilder().traceId(1L).parentId(2L).id(3L).name("get").timestamp(1472470996199000L).duration(207000L).addAnnotation(1472470996199000L, "cs", TestObjects.FRONTEND).addAnnotation(1472470996238000L, "ws", TestObjects.FRONTEND).addAnnotation(1472470996250000L, "sr", TestObjects.BACKEND).addAnnotation(1472470996350000L, "ss", TestObjects.BACKEND).addAnnotation(1472470996403000L, "wr", TestObjects.FRONTEND).addAnnotation(1472470996406000L, "cr", TestObjects.FRONTEND).addBinaryAnnotation("http.path", "/api", TestObjects.FRONTEND).addBinaryAnnotation("http.path", "/BACKEND", TestObjects.BACKEND).addBinaryAnnotation("clnt/finagle.version", "6.45.0", TestObjects.FRONTEND).addBinaryAnnotation("srv/finagle.version", "6.44.0", TestObjects.BACKEND).addBinaryAnnotation("ca", TestObjects.FRONTEND).addBinaryAnnotation("sa", TestObjects.BACKEND).build();
        Span.Builder newBuilder = Span.newBuilder().traceId("1").parentId("2").id("3").name("get");
        // the v1 side owns timestamp and duration
        Span clientV2 = newBuilder.clone().kind(CLIENT).localEndpoint(TestObjects.FRONTEND).remoteEndpoint(TestObjects.BACKEND).timestamp(1472470996199000L).duration(207000L).addAnnotation(1472470996238000L, "ws").addAnnotation(1472470996403000L, "wr").putTag("http.path", "/api").putTag("clnt/finagle.version", "6.45.0").build();
        // notice v1 tags are different than the v1, and the v1's annotations aren't here
        Span serverV2 = newBuilder.clone().kind(SERVER).shared(true).localEndpoint(TestObjects.BACKEND).remoteEndpoint(TestObjects.FRONTEND).timestamp(1472470996250000L).duration(100000L).putTag("http.path", "/BACKEND").putTag("srv/finagle.version", "6.44.0").build();
        assertThat(v1SpanConverter.convert(v1)).containsExactly(clientV2, serverV2);
    }

    /**
     * The old v1 format had no means of saying it is shared or not. This uses lack of timestamp as a
     * signal
     */
    @Test
    public void assumesServerWithoutTimestampIsShared() {
        V1Span v1 = V1Span.newBuilder().traceId(1L).parentId(2L).id(3L).name("get").addAnnotation(1472470996250000L, "sr", TestObjects.BACKEND).addAnnotation(1472470996350000L, "ss", TestObjects.BACKEND).build();
        Span v2 = Span.newBuilder().traceId("1").parentId("2").id("3").name("get").kind(SERVER).shared(true).localEndpoint(TestObjects.BACKEND).timestamp(1472470996250000L).duration(100000L).build();
        assertThat(v1SpanConverter.convert(v1)).containsExactly(v2);
    }

    @Test
    public void clientAndServer_loopback() {
        V1Span v1 = V1Span.newBuilder().traceId(1L).parentId(2L).id(3L).name("get").timestamp(1472470996199000L).duration(207000L).addAnnotation(1472470996199000L, "cs", TestObjects.FRONTEND).addAnnotation(1472470996250000L, "sr", TestObjects.FRONTEND).addAnnotation(1472470996350000L, "ss", TestObjects.FRONTEND).addAnnotation(1472470996406000L, "cr", TestObjects.FRONTEND).build();
        Span.Builder newBuilder = Span.newBuilder().traceId("1").parentId("2").id("3").name("get");
        Span clientV2 = newBuilder.clone().kind(CLIENT).localEndpoint(TestObjects.FRONTEND).timestamp(1472470996199000L).duration(207000L).build();
        Span serverV2 = newBuilder.clone().kind(SERVER).shared(true).localEndpoint(TestObjects.FRONTEND).timestamp(1472470996250000L).duration(100000L).build();
        assertThat(v1SpanConverter.convert(v1)).containsExactly(clientV2, serverV2);
    }

    @Test
    public void oneway_loopback() {
        V1Span v1 = V1Span.newBuilder().traceId(1L).parentId(2L).id(3L).name("get").addAnnotation(1472470996199000L, "cs", TestObjects.FRONTEND).addAnnotation(1472470996250000L, "sr", TestObjects.FRONTEND).build();
        Span.Builder newBuilder = Span.newBuilder().traceId("1").parentId("2").id("3").name("get");
        Span clientV2 = newBuilder.clone().kind(CLIENT).localEndpoint(TestObjects.FRONTEND).timestamp(1472470996199000L).build();
        Span serverV2 = newBuilder.clone().kind(SERVER).shared(true).localEndpoint(TestObjects.FRONTEND).timestamp(1472470996250000L).build();
        assertThat(v1SpanConverter.convert(v1)).containsExactly(clientV2, serverV2);
    }

    @Test
    public void producer() {
        V1Span v1 = V1Span.newBuilder().traceId(1L).parentId(2L).id(3L).name("send").addAnnotation(1472470996199000L, "ms", TestObjects.FRONTEND).build();
        Span v2 = Span.newBuilder().traceId("1").parentId("2").id("3").name("send").kind(PRODUCER).localEndpoint(TestObjects.FRONTEND).timestamp(1472470996199000L).build();
        assertThat(v1SpanConverter.convert(v1)).containsExactly(v2);
    }

    /**
     * Fix a v1 reported half in new style and half in old style, ex via a bridge
     */
    @Test
    public void client_missingCs() {
        Span v2 = Span.newBuilder().traceId("1").id("2").name("get").kind(CLIENT).localEndpoint(TestObjects.FRONTEND).timestamp(1472470996199000L).duration(207000L).build();
        V1Span v1 = V1Span.newBuilder().traceId("1").id("2").name("get").timestamp(1472470996199000L).duration(207000L).addAnnotation(1472470996406000L, "cs", TestObjects.FRONTEND).build();
        assertThat(v1SpanConverter.convert(v1)).containsExactly(v2);
    }

    @Test
    public void server_missingSr() {
        Span v2 = Span.newBuilder().traceId("1").id("2").name("get").kind(SERVER).localEndpoint(TestObjects.BACKEND).timestamp(1472470996199000L).duration(207000L).build();
        V1Span v1 = V1Span.newBuilder().traceId("1").id("2").name("get").timestamp(1472470996199000L).duration(207000L).addAnnotation(1472470996406000L, "ss", TestObjects.BACKEND).build();
        assertThat(v1SpanConverter.convert(v1)).containsExactly(v2);
    }

    /**
     * Intentionally create service loopback endpoints as dependency linker can correct it later if
     * incorrect, provided the server is instrumented.
     */
    @Test
    public void redundantAddressAnnotations_client() {
        Span v2 = Span.newBuilder().traceId("1").parentId("2").id("3").kind(CLIENT).name("get").localEndpoint(TestObjects.FRONTEND).remoteEndpoint(TestObjects.FRONTEND).timestamp(1472470996199000L).duration(207000L).build();
        V1Span v1 = V1Span.newBuilder().traceId(1L).parentId(2L).id(3L).name("get").timestamp(1472470996199000L).duration(207000L).addAnnotation(1472470996199000L, "cs", TestObjects.FRONTEND).addAnnotation(1472470996406000L, "cr", TestObjects.FRONTEND).addBinaryAnnotation("ca", TestObjects.FRONTEND).addBinaryAnnotation("sa", TestObjects.FRONTEND).build();
        assertThat(v1SpanConverter.convert(v1)).containsExactly(v2);
    }

    /**
     * On server spans, ignore service name on remote address binary annotation that appear loopback
     * based on the service name. This could happen when finagle service labels are used incorrectly,
     * which as common in early instrumentation.
     *
     * <p>This prevents an uncorrectable scenario which results in extra (loopback) links on server
     * spans.
     */
    @Test
    public void redundantServiceNameOnAddressAnnotations_server() {
        Span v2 = Span.newBuilder().traceId("1").parentId("2").id("3").kind(SERVER).name("get").localEndpoint(TestObjects.FRONTEND).timestamp(1472470996199000L).duration(207000L).build();
        V1Span v1 = V1Span.newBuilder().traceId(1L).parentId(2L).id(3L).name("get").timestamp(1472470996199000L).duration(207000L).addAnnotation(1472470996199000L, "sr", TestObjects.FRONTEND).addAnnotation(1472470996406000L, "ss", TestObjects.FRONTEND).addBinaryAnnotation("ca", TestObjects.FRONTEND).addBinaryAnnotation("sa", TestObjects.FRONTEND).build();
        assertThat(v1SpanConverter.convert(v1)).containsExactly(v2);
    }

    @Test
    public void redundantServiceNameOnAddressAnnotations_serverRetainsClientSocket() {
        Span v2 = Span.newBuilder().traceId("1").parentId("2").id("3").kind(SERVER).name("get").localEndpoint(TestObjects.BACKEND).remoteEndpoint(TestObjects.FRONTEND.toBuilder().serviceName(null).build()).timestamp(1472470996199000L).duration(207000L).build();
        V1Span v1 = V1Span.newBuilder().traceId(1L).parentId(2L).id(3L).name("get").timestamp(1472470996199000L).duration(207000L).addAnnotation(1472470996199000L, "sr", TestObjects.BACKEND).addAnnotation(1472470996406000L, "ss", TestObjects.BACKEND).addBinaryAnnotation("ca", TestObjects.FRONTEND.toBuilder().serviceName("backend").build()).addBinaryAnnotation("sa", TestObjects.BACKEND).build();
        assertThat(v1SpanConverter.convert(v1)).containsExactly(v2);
    }

    /**
     * shared v1 IDs for messaging spans isn't supported, but shouldn't break
     */
    @Test
    public void producerAndConsumer() {
        V1Span v1 = V1Span.newBuilder().traceId(1L).parentId(2L).id(3L).name("whatev").addAnnotation(1472470996199000L, "ms", TestObjects.FRONTEND).addAnnotation(1472470996238000L, "ws", TestObjects.FRONTEND).addAnnotation(1472470996403000L, "wr", TestObjects.BACKEND).addAnnotation(1472470996406000L, "mr", TestObjects.BACKEND).addBinaryAnnotation("ma", kafka).build();
        Span.Builder newBuilder = Span.newBuilder().traceId("1").parentId("2").id("3").name("whatev");
        Span producer = newBuilder.clone().kind(PRODUCER).localEndpoint(TestObjects.FRONTEND).remoteEndpoint(kafka).timestamp(1472470996199000L).duration((1472470996238000L - 1472470996199000L)).build();
        Span consumer = newBuilder.clone().kind(CONSUMER).shared(true).localEndpoint(TestObjects.BACKEND).remoteEndpoint(kafka).timestamp(1472470996403000L).duration((1472470996406000L - 1472470996403000L)).build();
        assertThat(v1SpanConverter.convert(v1)).containsExactly(producer, consumer);
    }

    /**
     * shared v1 IDs for messaging spans isn't supported, but shouldn't break
     */
    @Test
    public void producerAndConsumer_loopback_shared() {
        V1Span v1 = V1Span.newBuilder().traceId(1).parentId(2).id(3).name("message").addAnnotation(1472470996199000L, "ms", TestObjects.FRONTEND).addAnnotation(1472470996238000L, "ws", TestObjects.FRONTEND).addAnnotation(1472470996403000L, "wr", TestObjects.FRONTEND).addAnnotation(1472470996406000L, "mr", TestObjects.FRONTEND).build();
        Span.Builder newBuilder = Span.newBuilder().traceId("1").parentId("2").id("3").name("message");
        Span producer = newBuilder.clone().kind(PRODUCER).localEndpoint(TestObjects.FRONTEND).timestamp(1472470996199000L).duration((1472470996238000L - 1472470996199000L)).build();
        Span consumer = newBuilder.clone().kind(CONSUMER).shared(true).localEndpoint(TestObjects.FRONTEND).timestamp(1472470996403000L).duration((1472470996406000L - 1472470996403000L)).build();
        assertThat(v1SpanConverter.convert(v1)).containsExactly(producer, consumer);
    }

    @Test
    public void onlyAddressAnnotations() {
        V1Span v1 = V1Span.newBuilder().traceId(1).parentId(2).id(3).name("rpc").addBinaryAnnotation("ca", TestObjects.FRONTEND).addBinaryAnnotation("sa", TestObjects.BACKEND).build();
        Span v2 = Span.newBuilder().traceId("1").parentId("2").id("3").name("rpc").localEndpoint(TestObjects.FRONTEND).remoteEndpoint(TestObjects.BACKEND).build();
        assertThat(v1SpanConverter.convert(v1)).containsExactly(v2);
    }

    @Test
    public void dataMissingEndpointGoesOnFirstSpan() {
        V1Span v1 = V1Span.newBuilder().traceId(1).id(2).name("missing").addAnnotation(1472470996199000L, "foo", TestObjects.FRONTEND).addAnnotation(1472470996238000L, "bar", TestObjects.FRONTEND).addAnnotation(1472470996250000L, "baz", TestObjects.BACKEND).addAnnotation(1472470996350000L, "qux", TestObjects.BACKEND).addAnnotation(1472470996403000L, "missing", null).addBinaryAnnotation("foo", "bar", TestObjects.FRONTEND).addBinaryAnnotation("baz", "qux", TestObjects.BACKEND).addBinaryAnnotation("missing", "", null).build();
        Span.Builder newBuilder = Span.newBuilder().traceId("1").id("2").name("missing");
        Span first = newBuilder.clone().localEndpoint(TestObjects.FRONTEND).addAnnotation(1472470996199000L, "foo").addAnnotation(1472470996238000L, "bar").addAnnotation(1472470996403000L, "missing").putTag("foo", "bar").putTag("missing", "").build();
        Span second = newBuilder.clone().localEndpoint(TestObjects.BACKEND).addAnnotation(1472470996250000L, "baz").addAnnotation(1472470996350000L, "qux").putTag("baz", "qux").build();
        assertThat(v1SpanConverter.convert(v1)).containsExactly(first, second);
    }

    /**
     * This emulates a situation in mysql where the row representing a span has the client's timestamp
     */
    @Test
    public void parsesSharedFlagFromRPCSpan() {
        V1Span v1 = V1Span.newBuilder().traceId(1L).parentId(2L).id(3L).name("get").timestamp(10).addAnnotation(20, "sr", TestObjects.BACKEND).addAnnotation(30, "ss", TestObjects.BACKEND).build();
        Span v2 = Span.newBuilder().traceId("1").parentId("2").id("3").name("get").kind(SERVER).shared(true).localEndpoint(TestObjects.BACKEND).timestamp(20).duration(10L).build();
        assertThat(v1SpanConverter.convert(v1)).containsExactly(v2);
    }
}

