/**
 * Copyright 2018 The Bazel Authors. All rights reserved.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.google.devtools.build.lib.remote.blobstore.http;


import HttpHeaderValues.CLOSE;
import HttpHeaderValues.KEEP_ALIVE;
import HttpResponseStatus.NOT_FOUND;
import LastHttpContent.EMPTY_LAST_CONTENT;
import com.google.common.net.HttpHeaders;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelPromise;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;


/**
 * Tests for {@link HttpDownloadHandler}.
 */
@RunWith(JUnit4.class)
public class HttpDownloadHandlerTest extends AbstractHttpHandlerTest {
    private static final URI CACHE_URI = URI.create("http://storage.googleapis.com:80/cache-bucket");

    /**
     * Test that downloading blobs works from both the Action Cache and the CAS. Also test that the
     * handler is reusable.
     */
    @Test
    public void downloadShouldWork() throws IOException {
        EmbeddedChannel ch = new EmbeddedChannel(new HttpDownloadHandler(null));
        downloadShouldWork(true, ch);
        downloadShouldWork(false, ch);
    }

    /**
     * Test that the handler correctly supports http error codes i.e. 404 (NOT FOUND).
     */
    @Test
    public void httpErrorsAreSupported() throws IOException {
        EmbeddedChannel ch = new EmbeddedChannel(new HttpDownloadHandler(null));
        ByteArrayOutputStream out = Mockito.spy(new ByteArrayOutputStream());
        DownloadCommand cmd = new DownloadCommand(HttpDownloadHandlerTest.CACHE_URI, true, "abcdef", out);
        ChannelPromise writePromise = ch.newPromise();
        ch.writeOneOutbound(cmd, writePromise);
        HttpResponse response = new io.netty.handler.codec.http.DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);
        response.headers().set(HttpHeaders.HOST, "localhost");
        response.headers().set(HttpHeaders.CONTENT_LENGTH, 0);
        response.headers().set(HttpHeaders.CONNECTION, KEEP_ALIVE);
        ch.writeInbound(response);
        ch.writeInbound(EMPTY_LAST_CONTENT);
        assertThat(writePromise.isDone()).isTrue();
        assertThat(writePromise.cause()).isInstanceOf(HttpException.class);
        assertThat(response().status()).isEqualTo(NOT_FOUND);
        // No data should have been written to the OutputStream and it should have been closed.
        assertThat(out.size()).isEqualTo(0);
        // The caller is responsible for closing the stream.
        Mockito.verify(out, Mockito.never()).close();
        assertThat(ch.isOpen()).isTrue();
    }

    /**
     * Test that the handler correctly supports http error codes i.e. 404 (NOT FOUND) with a
     * Content-Length header.
     */
    @Test
    public void httpErrorsWithContentAreSupported() throws IOException {
        EmbeddedChannel ch = new EmbeddedChannel(new HttpDownloadHandler(null));
        ByteArrayOutputStream out = Mockito.spy(new ByteArrayOutputStream());
        DownloadCommand cmd = new DownloadCommand(HttpDownloadHandlerTest.CACHE_URI, true, "abcdef", out);
        ChannelPromise writePromise = ch.newPromise();
        ch.writeOneOutbound(cmd, writePromise);
        HttpResponse response = new io.netty.handler.codec.http.DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);
        ByteBuf errorMessage = ByteBufUtil.writeAscii(ch.alloc(), "Error message");
        response.headers().set(HttpHeaders.HOST, "localhost");
        response.headers().set(HttpHeaders.CONTENT_LENGTH, String.valueOf(errorMessage.readableBytes()));
        response.headers().set(HttpHeaders.CONNECTION, CLOSE);
        ch.writeInbound(response);
        // The promise must not be done because we haven't received the error message yet.
        assertThat(writePromise.isDone()).isFalse();
        ch.writeInbound(new io.netty.handler.codec.http.DefaultHttpContent(errorMessage));
        ch.writeInbound(EMPTY_LAST_CONTENT);
        assertThat(writePromise.isDone()).isTrue();
        assertThat(writePromise.cause()).isInstanceOf(HttpException.class);
        assertThat(response().status()).isEqualTo(NOT_FOUND);
        // No data should have been written to the OutputStream and it should have been closed.
        assertThat(out.size()).isEqualTo(0);
        // The caller is responsible for closing the stream.
        Mockito.verify(out, Mockito.never()).close();
        assertThat(ch.isOpen()).isFalse();
    }
}

