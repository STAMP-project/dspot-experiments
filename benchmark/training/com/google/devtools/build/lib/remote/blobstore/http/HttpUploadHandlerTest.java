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
import HttpResponseStatus.FORBIDDEN;
import HttpResponseStatus.NOT_FOUND;
import com.google.common.net.HttpHeaders;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelPromise;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpChunkedInput;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import java.io.ByteArrayInputStream;
import java.net.URI;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link HttpUploadHandler}.
 */
@RunWith(JUnit4.class)
public class HttpUploadHandlerTest extends AbstractHttpHandlerTest {
    private static final URI CACHE_URI = URI.create("http://storage.googleapis.com:80/cache-bucket");

    /**
     * Test that uploading blobs works to both the Action Cache and the CAS. Also test that the
     * handler is reusable.
     */
    @Test
    public void uploadsShouldWork() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new HttpUploadHandler(null));
        HttpResponseStatus[] statuses = new HttpResponseStatus[]{ HttpResponseStatus.OK, HttpResponseStatus.CREATED, HttpResponseStatus.ACCEPTED, HttpResponseStatus.NO_CONTENT };
        for (HttpResponseStatus status : statuses) {
            uploadsShouldWork(true, ch, status);
            uploadsShouldWork(false, ch, status);
        }
    }

    /**
     * Test that the handler correctly supports http error codes i.e. 404 (NOT FOUND).
     */
    @Test
    public void httpErrorsAreSupported() {
        EmbeddedChannel ch = new EmbeddedChannel(new HttpUploadHandler(null));
        ByteArrayInputStream data = new ByteArrayInputStream(new byte[]{ 1, 2, 3, 4, 5 });
        ChannelPromise writePromise = ch.newPromise();
        ch.writeOneOutbound(new UploadCommand(HttpUploadHandlerTest.CACHE_URI, true, "abcdef", data, 5), writePromise);
        HttpRequest request = ch.readOutbound();
        assertThat(request).isInstanceOf(HttpRequest.class);
        HttpChunkedInput content = ch.readOutbound();
        assertThat(content).isInstanceOf(HttpChunkedInput.class);
        FullHttpResponse response = new io.netty.handler.codec.http.DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN);
        response.headers().set(HttpHeaders.CONNECTION, CLOSE);
        ch.writeInbound(response);
        assertThat(writePromise.isDone()).isTrue();
        assertThat(writePromise.cause()).isInstanceOf(HttpException.class);
        assertThat(response().status()).isEqualTo(FORBIDDEN);
        assertThat(ch.isOpen()).isFalse();
    }

    /**
     * Test that the handler correctly supports http error codes i.e. 404 (NOT FOUND) with a
     * Content-Length header.
     */
    @Test
    public void httpErrorsWithContentAreSupported() {
        EmbeddedChannel ch = new EmbeddedChannel(new HttpUploadHandler(null));
        ByteArrayInputStream data = new ByteArrayInputStream(new byte[]{ 1, 2, 3, 4, 5 });
        ChannelPromise writePromise = ch.newPromise();
        ch.writeOneOutbound(new UploadCommand(HttpUploadHandlerTest.CACHE_URI, true, "abcdef", data, 5), writePromise);
        HttpRequest request = ch.readOutbound();
        assertThat(request).isInstanceOf(HttpRequest.class);
        HttpChunkedInput content = ch.readOutbound();
        assertThat(content).isInstanceOf(HttpChunkedInput.class);
        ByteBuf errorMsg = ByteBufUtil.writeAscii(ch.alloc(), "error message");
        FullHttpResponse response = new io.netty.handler.codec.http.DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND, errorMsg);
        response.headers().set(HttpHeaders.CONNECTION, KEEP_ALIVE);
        ch.writeInbound(response);
        assertThat(writePromise.isDone()).isTrue();
        assertThat(writePromise.cause()).isInstanceOf(HttpException.class);
        assertThat(response().status()).isEqualTo(NOT_FOUND);
        assertThat(ch.isOpen()).isTrue();
    }
}

