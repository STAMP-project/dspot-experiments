/**
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.handler.codec.http;


import CharsetUtil.US_ASCII;
import LastHttpContent.EMPTY_LAST_CONTENT;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.DecoderResult;
import io.netty.util.IllegalReferenceCountException;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutionException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import static HttpMethod.GET;
import static HttpMethod.POST;
import static HttpVersion.HTTP_1_1;


/**
 *
 */
public class HttpRequestEncoderTest {
    @Test
    public void testUriWithoutPath() throws Exception {
        for (ByteBuf buffer : HttpRequestEncoderTest.getBuffers()) {
            HttpRequestEncoder encoder = new HttpRequestEncoder();
            encoder.encodeInitialLine(buffer, new DefaultHttpRequest(HTTP_1_1, GET, "http://localhost"));
            String req = buffer.toString(Charset.forName("US-ASCII"));
            Assert.assertEquals("GET http://localhost/ HTTP/1.1\r\n", req);
            buffer.release();
        }
    }

    @Test
    public void testUriWithoutPath2() throws Exception {
        for (ByteBuf buffer : HttpRequestEncoderTest.getBuffers()) {
            HttpRequestEncoder encoder = new HttpRequestEncoder();
            encoder.encodeInitialLine(buffer, new DefaultHttpRequest(HTTP_1_1, GET, "http://localhost:9999?p1=v1"));
            String req = buffer.toString(Charset.forName("US-ASCII"));
            Assert.assertEquals("GET http://localhost:9999/?p1=v1 HTTP/1.1\r\n", req);
            buffer.release();
        }
    }

    @Test
    public void testUriWithEmptyPath() throws Exception {
        for (ByteBuf buffer : HttpRequestEncoderTest.getBuffers()) {
            HttpRequestEncoder encoder = new HttpRequestEncoder();
            encoder.encodeInitialLine(buffer, new DefaultHttpRequest(HTTP_1_1, GET, "http://localhost:9999/?p1=v1"));
            String req = buffer.toString(Charset.forName("US-ASCII"));
            Assert.assertEquals("GET http://localhost:9999/?p1=v1 HTTP/1.1\r\n", req);
            buffer.release();
        }
    }

    @Test
    public void testUriWithPath() throws Exception {
        for (ByteBuf buffer : HttpRequestEncoderTest.getBuffers()) {
            HttpRequestEncoder encoder = new HttpRequestEncoder();
            encoder.encodeInitialLine(buffer, new DefaultHttpRequest(HTTP_1_1, GET, "http://localhost/"));
            String req = buffer.toString(Charset.forName("US-ASCII"));
            Assert.assertEquals("GET http://localhost/ HTTP/1.1\r\n", req);
            buffer.release();
        }
    }

    @Test
    public void testAbsPath() throws Exception {
        for (ByteBuf buffer : HttpRequestEncoderTest.getBuffers()) {
            HttpRequestEncoder encoder = new HttpRequestEncoder();
            encoder.encodeInitialLine(buffer, new DefaultHttpRequest(HTTP_1_1, GET, "/"));
            String req = buffer.toString(Charset.forName("US-ASCII"));
            Assert.assertEquals("GET / HTTP/1.1\r\n", req);
            buffer.release();
        }
    }

    @Test
    public void testEmptyAbsPath() throws Exception {
        for (ByteBuf buffer : HttpRequestEncoderTest.getBuffers()) {
            HttpRequestEncoder encoder = new HttpRequestEncoder();
            encoder.encodeInitialLine(buffer, new DefaultHttpRequest(HTTP_1_1, GET, ""));
            String req = buffer.toString(Charset.forName("US-ASCII"));
            Assert.assertEquals("GET / HTTP/1.1\r\n", req);
            buffer.release();
        }
    }

    @Test
    public void testQueryStringPath() throws Exception {
        for (ByteBuf buffer : HttpRequestEncoderTest.getBuffers()) {
            HttpRequestEncoder encoder = new HttpRequestEncoder();
            encoder.encodeInitialLine(buffer, new DefaultHttpRequest(HTTP_1_1, GET, "/?url=http://example.com"));
            String req = buffer.toString(Charset.forName("US-ASCII"));
            Assert.assertEquals("GET /?url=http://example.com HTTP/1.1\r\n", req);
            buffer.release();
        }
    }

    @Test
    public void testEmptyReleasedBufferShouldNotWriteEmptyBufferToChannel() throws Exception {
        HttpRequestEncoder encoder = new HttpRequestEncoder();
        EmbeddedChannel channel = new EmbeddedChannel(encoder);
        ByteBuf buf = Unpooled.buffer();
        buf.release();
        try {
            channel.writeAndFlush(buf).get();
            Assert.fail();
        } catch (ExecutionException e) {
            Assert.assertThat(e.getCause().getCause(), Matchers.is(Matchers.instanceOf(IllegalReferenceCountException.class)));
        }
        channel.finishAndReleaseAll();
    }

    @Test
    public void testEmptyBufferShouldPassThrough() throws Exception {
        HttpRequestEncoder encoder = new HttpRequestEncoder();
        EmbeddedChannel channel = new EmbeddedChannel(encoder);
        ByteBuf buffer = Unpooled.buffer();
        channel.writeAndFlush(buffer).get();
        channel.finishAndReleaseAll();
        Assert.assertEquals(0, buffer.refCnt());
    }

    @Test
    public void testEmptyContentsChunked() throws Exception {
        testEmptyContents(true, false);
    }

    @Test
    public void testEmptyContentsChunkedWithTrailers() throws Exception {
        testEmptyContents(true, true);
    }

    @Test
    public void testEmptyContentsNotChunked() throws Exception {
        testEmptyContents(false, false);
    }

    @Test
    public void testEmptyContentNotsChunkedWithTrailers() throws Exception {
        testEmptyContents(false, true);
    }

    /**
     * A test that checks for a NPE that would occur if when processing {@link LastHttpContent#EMPTY_LAST_CONTENT}
     * when a certain initialization order of {@link EmptyHttpHeaders} would occur.
     */
    @Test
    public void testForChunkedRequestNpe() throws Exception {
        EmbeddedChannel channel = new EmbeddedChannel(new HttpRequestEncoder());
        Assert.assertTrue(channel.writeOutbound(new HttpRequestEncoderTest.CustomHttpRequest()));
        Assert.assertTrue(channel.writeOutbound(new DefaultHttpContent(Unpooled.copiedBuffer("test", US_ASCII))));
        Assert.assertTrue(channel.writeOutbound(EMPTY_LAST_CONTENT));
        Assert.assertTrue(channel.finishAndReleaseAll());
    }

    /**
     * This class is required to triggered the desired initialization order of {@link EmptyHttpHeaders}.
     * If {@link DefaultHttpRequest} is used, the {@link HttpHeaders} class will be initialized before {@link HttpUtil}
     * and the test won't trigger the original issue.
     */
    private static final class CustomHttpRequest implements HttpRequest {
        @Override
        public DecoderResult decoderResult() {
            return DecoderResult.SUCCESS;
        }

        @Override
        public void setDecoderResult(DecoderResult result) {
        }

        @Override
        public DecoderResult getDecoderResult() {
            return decoderResult();
        }

        @Override
        public HttpVersion getProtocolVersion() {
            return HttpVersion.HTTP_1_1;
        }

        @Override
        public HttpVersion protocolVersion() {
            return getProtocolVersion();
        }

        @Override
        public HttpHeaders headers() {
            DefaultHttpHeaders headers = new DefaultHttpHeaders();
            headers.add("Transfer-Encoding", "chunked");
            return headers;
        }

        @Override
        public HttpMethod getMethod() {
            return POST;
        }

        @Override
        public HttpMethod method() {
            return getMethod();
        }

        @Override
        public HttpRequest setMethod(HttpMethod method) {
            return this;
        }

        @Override
        public String getUri() {
            return "/";
        }

        @Override
        public String uri() {
            return "/";
        }

        @Override
        public HttpRequest setUri(String uri) {
            return this;
        }

        @Override
        public HttpRequest setProtocolVersion(HttpVersion version) {
            return this;
        }
    }
}

