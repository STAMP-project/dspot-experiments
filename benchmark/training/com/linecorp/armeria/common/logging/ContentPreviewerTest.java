/**
 * Copyright 2019 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.linecorp.armeria.common.logging;


import ClientFactory.DEFAULT;
import HttpHeaderNames.ACCEPT;
import HttpHeaderNames.CONTENT_TYPE;
import HttpHeaders.EMPTY_HEADERS;
import HttpMethod.GET;
import HttpStatus.OK;
import LogLevel.INFO;
import MediaType.ANY_TEXT_TYPE;
import MediaType.BASIC_AUDIO;
import MediaType.JSON;
import com.google.common.base.Strings;
import com.linecorp.armeria.client.HttpClient;
import com.linecorp.armeria.client.logging.LoggingClientBuilder;
import com.linecorp.armeria.common.HttpData;
import com.linecorp.armeria.common.HttpHeaders;
import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.server.ServerBuilder;
import com.linecorp.armeria.server.annotation.Get;
import com.linecorp.armeria.server.annotation.Post;
import com.linecorp.armeria.testing.server.ServerRule;
import io.netty.buffer.ByteBufUtil;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import org.junit.ClassRule;
import org.junit.Test;


public class ContentPreviewerTest {
    static class MyHttpClient {
        private final HttpClient client;

        @Nullable
        private volatile CompletableFuture<RequestLog> waitingFuture;

        MyHttpClient(String uri, int reqLength, int resLength) {
            client = new com.linecorp.armeria.client.HttpClientBuilder(ContentPreviewerTest.serverRule.uri(uri)).requestContentPreviewerFactory(ContentPreviewerFactory.ofText(reqLength, StandardCharsets.UTF_8)).responseContentPreviewerFactory(ContentPreviewerFactory.ofText(resLength, StandardCharsets.UTF_8)).decorator(new LoggingClientBuilder().requestLogLevel(INFO).successfulResponseLogLevel(INFO).newDecorator()).decorator(( delegate, ctx, req) -> {
                if ((waitingFuture) != null) {
                    ctx.log().addListener(waitingFuture::complete, RequestLogAvailability.COMPLETE);
                }
                return delegate.execute(ctx, req);
            }).build();
        }

        public RequestLog get(String path) throws Exception {
            waitingFuture = new CompletableFuture();
            getBody(path).aggregate().join();
            final RequestLog log = waitingFuture.get();
            waitingFuture = null;
            return log;
        }

        public HttpResponse getBody(String path) throws Exception {
            return client.execute(HttpHeaders.of(GET, path).set(ACCEPT, "utf-8").set(CONTENT_TYPE, ANY_TEXT_TYPE.toString()));
        }

        public HttpResponse postBody(String path, byte[] content, com.linecorp.armeria.common.MediaType contentType) {
            return client.execute(HttpHeaders.of(HttpMethod.POST, path).contentType(contentType).set(ACCEPT, "utf-8"), content);
        }

        public RequestLog post(String path, byte[] content, com.linecorp.armeria.common.MediaType contentType) throws Exception {
            waitingFuture = new CompletableFuture();
            postBody(path, content, contentType).aggregate().join();
            final RequestLog log = waitingFuture.get();
            waitingFuture = null;
            return log;
        }

        public RequestLog post(String path, String content, Charset charset, com.linecorp.armeria.common.MediaType contentType) throws Exception {
            return post(path, content.getBytes(charset), contentType);
        }

        public RequestLog post(String path, String content, com.linecorp.armeria.common.MediaType contentType) throws Exception {
            return post(path, content.getBytes(), contentType);
        }

        public RequestLog post(String path, String content) throws Exception {
            return post(path, content.getBytes(), ANY_TEXT_TYPE);
        }

        public RequestLog post(String path) throws Exception {
            return post(path, "");
        }
    }

    static class MyHttpServer {
        @Nullable
        private CompletableFuture<RequestLog> waitingFuture;

        ContentPreviewerTest.MyHttpServer.Client newClient(String path) {
            return new ContentPreviewerTest.MyHttpServer.Client(path);
        }

        class Client {
            private final HttpClient client;

            Client(String path) {
                client = HttpClient.of(DEFAULT, ContentPreviewerTest.serverRule.uri(path));
            }

            public RequestLog get(String path) throws Exception {
                waitingFuture = new CompletableFuture();
                getBody(path).aggregate().join();
                final RequestLog log = waitingFuture.get();
                waitingFuture = null;
                return log;
            }

            public HttpResponse getBody(String path) throws Exception {
                return client.execute(HttpHeaders.of(GET, path).set(ACCEPT, "utf-8").set(CONTENT_TYPE, ANY_TEXT_TYPE.toString()));
            }

            public RequestLog post(String path, byte[] content, com.linecorp.armeria.common.MediaType contentType) throws Exception {
                waitingFuture = new CompletableFuture();
                client.execute(HttpHeaders.of(HttpMethod.POST, path).contentType(contentType).set(ACCEPT, "utf-8").set(CONTENT_TYPE, ANY_TEXT_TYPE.toString()), content);
                final RequestLog log = waitingFuture.get();
                waitingFuture = null;
                return log;
            }

            public RequestLog post(String path, String content, Charset charset, com.linecorp.armeria.common.MediaType contentType) throws Exception {
                return post(path, content.getBytes(charset), contentType);
            }

            public RequestLog post(String path, String content, com.linecorp.armeria.common.MediaType contentType) throws Exception {
                return post(path, content.getBytes(), contentType);
            }

            public RequestLog post(String path, String content) throws Exception {
                return post(path, content.getBytes(), ANY_TEXT_TYPE);
            }

            public RequestLog post(String path) throws Exception {
                return post(path, "");
            }
        }

        void build(ServerBuilder sb) {
            sb.contentPreview(10, StandardCharsets.UTF_8);
            sb.decorator(( delegate) -> {
                return ( ctx, req) -> {
                    if ((waitingFuture) != null) {
                        ctx.log().addListener(waitingFuture::complete, RequestLogAvailability.COMPLETE);
                    }
                    return delegate.serve(ctx, req);
                };
            });
            sb.annotatedService("/example", new Object() {
                @Get("/get")
                public String get() {
                    return "test";
                }

                @Get("/get-unicode")
                public HttpResponse getUnicode() {
                    return HttpResponse.of(ANY_TEXT_TYPE, "??");
                }

                @Get("/get-audio")
                public HttpResponse getBinary() {
                    return HttpResponse.of(OK, BASIC_AUDIO, new byte[]{ 1, 2, 3, 4 });
                }

                @Get("/get-json")
                public HttpResponse getJson() {
                    return HttpResponse.of(JSON, "{\"value\":1}");
                }

                @Get("/get-longstring")
                public String getLongString() {
                    return Strings.repeat("a", 10000);
                }

                @Post("/post")
                public String post(String requestContent) {
                    return "abcdefghijkmnopqrstu";
                }
            });
        }
    }

    private static class HexDumpContentPreviewer implements ContentPreviewer {
        @Nullable
        private StringBuilder builder = new StringBuilder();

        @Nullable
        private String preview;

        @Override
        public void onHeaders(HttpHeaders headers) {
            // Invoked when headers of a request or response is received.
        }

        @Override
        public void onData(HttpData data) {
            // Invoked when a new content is received.
            assert (builder) != null;
            builder.append(ByteBufUtil.hexDump(data.array(), data.offset(), data.length()));
        }

        @Override
        public boolean isDone() {
            // If it returns true, no further event is invoked but produce().
            return (preview) != null;
        }

        @Override
        public String produce() {
            // Invoked when a request or response ends.
            if ((preview) != null) {
                return preview;
            }
            preview = builder.toString();
            builder = null;
            return preview;
        }
    }

    private static final ContentPreviewerTest.MyHttpServer server = new ContentPreviewerTest.MyHttpServer();

    @ClassRule
    public static final ServerRule serverRule = new ServerRule() {
        @Override
        protected void configure(ServerBuilder sb) throws Exception {
            ContentPreviewerTest.server.build(sb);
        }
    };

    private static final String TEST_STR = "abcdefghijkmnopqrstuvwyxzABCDEFGHIJKMNOPQRSTUVWXYZ" + "????????????????????????";

    @Test
    public void testAggreagted() {
        for (int sliceLength : new int[]{ 1, 3, 6, 10, 200 }) {
            for (int maxLength : new int[]{ 1, 3, 6, 10, 12, 15, 25, 35, 200 }) {
                ContentPreviewerTest.testSlice(ContentPreviewerTest.TEST_STR, StandardCharsets.UTF_8, maxLength, sliceLength);
                ContentPreviewerTest.testSliceBytes(ContentPreviewerTest.TEST_STR.getBytes(), maxLength, sliceLength);
            }
        }
    }

    @Test
    public void testProduce() {
        assertThat(ContentPreviewer.ofText(0)).isEqualTo(ContentPreviewer.disabled());
        assertThat(ContentPreviewer.ofBinary(0, ( a) -> "")).isEqualTo(ContentPreviewer.disabled());
        assertThat(ContentPreviewer.ofText(10)).isInstanceOf(StringContentPreviewer.class);
        assertThat(ContentPreviewer.ofBinary(1, ( a) -> "")).isInstanceOf(BinaryContentPreviewer.class);
    }

    @Test
    public void testClientLog() throws Exception {
        final ContentPreviewerTest.MyHttpClient client = new ContentPreviewerTest.MyHttpClient("/example", 10, 10);
        assertThat(client.get("/get").responseContentPreview()).isEqualTo("test");
        assertThat(client.getBody("/get").aggregate().get().content().toStringUtf8()).isEqualTo("test");
        assertThat(client.getBody("/get-unicode").aggregate().get().content().toStringUtf8()).isEqualTo("??");
        assertThat(client.get("/get-unicode").responseContentPreview()).isEqualTo("??");
        assertThat(client.getBody("/get-audio").aggregate().get().content().array()).containsExactly(new byte[]{ 1, 2, 3, 4 });
        assertThat(client.get("/get-audio").responseContentPreview()).isNull();
        assertThat(client.get("/get-json").responseContentPreview()).isEqualTo("{\"value\":1");
        assertThat(client.getBody("/get-json").aggregate().get().content().toStringUtf8()).isEqualTo("{\"value\":1}");
        assertThat(client.post("/post").responseContentPreview()).isEqualTo("abcdefghij");
        assertThat(client.post("/post", "abcdefghijkmno").requestContentPreview()).isEqualTo("abcdefghij");
        assertThat(client.get("/get-longstring").responseContentPreview()).isEqualTo("aaaaaaaaaa");
    }

    @Test
    public void testServerLog() throws Exception {
        final ContentPreviewerTest.MyHttpServer.Client client = ContentPreviewerTest.server.newClient("/example");
        assertThat(client.get("/get").responseContentPreview()).isEqualTo("test");
        assertThat(client.getBody("/get").aggregate().get().content().toStringUtf8()).isEqualTo("test");
        assertThat(client.get("/get-unicode").responseContentPreview()).isEqualTo("??");
        assertThat(client.getBody("/get-unicode").aggregate().get().content().toStringUtf8()).isEqualTo("??");
        assertThat(client.getBody("/get-audio").aggregate().get().content().array()).containsExactly(new byte[]{ 1, 2, 3, 4 });
        assertThat(client.get("/get-audio").responseContentPreview()).isNull();
        assertThat(client.get("/get-json").responseContentPreview()).isEqualTo("{\"value\":1");
        assertThat(client.getBody("/get-json").aggregate().get().content().toStringUtf8()).isEqualTo("{\"value\":1}");
        assertThat(client.post("/post").responseContentPreview()).isEqualTo("abcdefghij");
        assertThat(client.post("/post", "abcdefghijkmno").requestContentPreview()).isEqualTo("abcdefghij");
        assertThat(client.get("/get-longstring").responseContentPreview()).isEqualTo("aaaaaaaaaa");
    }

    @Test
    public void testCustomPreviewer() throws Exception {
        ContentPreviewer previewer = new ContentPreviewerTest.HexDumpContentPreviewer();
        previewer.onHeaders(EMPTY_HEADERS);
        previewer.onData(HttpData.of(new byte[]{ 1, 2, 3, 4 }));
        assertThat(previewer.produce()).isEqualTo("01020304");
        previewer = new ContentPreviewerTest.HexDumpContentPreviewer();
        previewer.onHeaders(EMPTY_HEADERS);
        previewer.onData(HttpData.of(new byte[]{ 1, 2, 3 }));
        previewer.onData(HttpData.of(new byte[]{ 4, 5 }));
        assertThat(previewer.produce()).isEqualTo("0102030405");
        assertThat(previewer.produce()).isEqualTo("0102030405");
    }
}

