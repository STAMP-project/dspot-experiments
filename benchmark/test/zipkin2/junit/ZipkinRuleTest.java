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
package zipkin2.junit;


import SpanBytesEncoder.JSON_V1;
import SpanBytesEncoder.JSON_V2;
import SpanBytesEncoder.PROTO3;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import okio.ByteString;
import okio.GzipSink;
import org.junit.AssumptionViolatedException;
import org.junit.Rule;
import org.junit.Test;
import zipkin2.Span;
import zipkin2.TestObjects;


public class ZipkinRuleTest {
    @Rule
    public ZipkinRule zipkin = new ZipkinRule();

    List<Span> spans = Arrays.asList(TestObjects.LOTS_OF_SPANS[0], TestObjects.LOTS_OF_SPANS[1]);

    OkHttpClient client = new OkHttpClient();

    @Test
    public void getTraces_storedViaPost() throws IOException {
        List<Span> trace = Arrays.asList(TestObjects.CLIENT_SPAN);
        // write the span to the zipkin using http
        assertThat(postSpansV1(trace).code()).isEqualTo(202);
        // read the traces directly
        assertThat(zipkin.getTraces()).containsOnly(trace);
    }

    @Test
    public void getTraces_storedViaPostVersion2_json() throws IOException {
        getTraces_storedViaPostVersion2("application/json", JSON_V2);
    }

    @Test
    public void getTraces_storedViaPostVersion2_proto3() throws IOException {
        getTraces_storedViaPostVersion2("application/x-protobuf", PROTO3);
    }

    /**
     * The rule is here to help debugging. Even partial spans should be returned
     */
    @Test
    public void getTraces_whenMissingTimestamps() throws IOException {
        Span span = Span.newBuilder().traceId("1").id("1").name("foo").build();
        // write the span to the zipkin using http
        assertThat(postSpansV1(Arrays.asList(span)).code()).isEqualTo(202);
        // read the traces directly
        assertThat(zipkin.getTraces()).containsOnly(Arrays.asList(span));
    }

    /**
     * The raw query can show affects like redundant rows in the data store.
     */
    @Test
    public void storeSpans_readbackRaw() {
        String traceId = TestObjects.LOTS_OF_SPANS[0].traceId();
        // write the span to zipkin directly
        zipkin.storeSpans(Arrays.asList(TestObjects.LOTS_OF_SPANS[0]));
        zipkin.storeSpans(Arrays.asList(TestObjects.LOTS_OF_SPANS[0]));
        assertThat(zipkin.getTrace(traceId)).containsExactly(TestObjects.LOTS_OF_SPANS[0], TestObjects.LOTS_OF_SPANS[0]);
    }

    @Test
    public void httpRequestCountIncrements() throws IOException {
        postSpansV1(spans);
        postSpansV1(spans);
        assertThat(zipkin.httpRequestCount()).isEqualTo(2);
    }

    /**
     * Normally, a span can be reported twice: for client and server. However, there are bugs that
     * happened where several updates went to the same span id. {@link ZipkinRule#collectorMetrics}
     * can be used to help ensure a span isn't reported more times than expected.
     */
    @Test
    public void collectorMetrics_spans() throws IOException {
        postSpansV1(Arrays.asList(TestObjects.LOTS_OF_SPANS[0]));
        postSpansV1(Arrays.asList(TestObjects.LOTS_OF_SPANS[1], TestObjects.LOTS_OF_SPANS[2]));
        assertThat(zipkin.collectorMetrics().spans()).isEqualTo(3);
    }

    @Test
    public void postSpans_disconnectDuringBody() {
        zipkin.enqueueFailure(HttpFailure.disconnectDuringBody());
        try {
            postSpansV1(spans);
            failBecauseExceptionWasNotThrown(IOException.class);
        } catch (IOException expected) {
            // not always a ConnectException!
        }
        // Zipkin didn't store the spans, as they shouldn't have been readable, due to disconnect
        assertThat(zipkin.getTraces()).isEmpty();
        try {
            // The failure shouldn't affect later requests
            assertThat(postSpansV1(spans).code()).isEqualTo(202);
        } catch (IOException flake) {
            throw new AssumptionViolatedException("test flaked", flake);
        }
    }

    @Test
    public void postSpans_sendErrorResponse400() throws IOException {
        zipkin.enqueueFailure(HttpFailure.sendErrorResponse(400, "Invalid Format"));
        Response response = postSpansV1(spans);
        assertThat(response.code()).isEqualTo(400);
        assertThat(response.body().string()).isEqualTo("Invalid Format");
        // Zipkin didn't store the spans, as they shouldn't have been readable, due to the error
        assertThat(zipkin.getTraces()).isEmpty();
        // The failure shouldn't affect later requests
        assertThat(postSpansV1(spans).code()).isEqualTo(202);
    }

    @Test
    public void gzippedSpans() throws IOException {
        byte[] spansInJson = JSON_V1.encodeList(spans);
        Buffer sink = new Buffer();
        GzipSink gzipSink = new GzipSink(sink);
        gzipSink.write(new Buffer().write(spansInJson), spansInJson.length);
        gzipSink.close();
        ByteString gzippedJson = sink.readByteString();
        client.newCall(new Request.Builder().url(((zipkin.httpUrl()) + "/api/v1/spans")).addHeader("Content-Encoding", "gzip").post(RequestBody.create(MediaType.parse("application/json"), gzippedJson)).build()).execute();
        assertThat(zipkin.collectorMetrics().bytes()).isEqualTo(spansInJson.length);
    }
}

