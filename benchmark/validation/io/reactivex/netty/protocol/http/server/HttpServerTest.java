/**
 * Copyright 2016 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.reactivex.netty.protocol.http.server;


import HttpHeaderNames.CONTENT_LENGTH;
import HttpResponseStatus.BAD_REQUEST;
import io.netty.buffer.ByteBuf;
import org.junit.Rule;
import org.junit.Test;
import rx.Observable;


public class HttpServerTest {
    @Rule
    public final HttpServerRule serverRule = new HttpServerRule();

    @Test(timeout = 60000)
    public void testResponseWithNoContentLengthHeaderOrContentReturnsEmptyBody() throws Exception {
        serverRule.startServer(new RequestHandler<ByteBuf, ByteBuf>() {
            @Override
            public Observable<Void> handle(HttpServerRequest<ByteBuf> request, HttpServerResponse<ByteBuf> response) {
                return response.setStatus(BAD_REQUEST);
            }
        });
        serverRule.assertEmptyBodyWithContentLengthZero();
    }

    @Test(timeout = 60000)
    public void testResponseWithNoContentLengthHeaderAndSendHeadersReturnsEmptyBody() throws Exception {
        serverRule.startServer(new RequestHandler<ByteBuf, ByteBuf>() {
            @Override
            public Observable<Void> handle(HttpServerRequest<ByteBuf> request, HttpServerResponse<ByteBuf> response) {
                return response.setStatus(BAD_REQUEST).sendHeaders();
            }
        });
        serverRule.assertEmptyBodyWithContentLengthZero();
    }

    @Test(timeout = 60000)
    public void testResponseWithNoContentLengthHeaderAndContentReturnsContentChunkAndSingleEmptyChunk() throws Exception {
        serverRule.startServer(new RequestHandler<ByteBuf, ByteBuf>() {
            @Override
            public Observable<Void> handle(HttpServerRequest<ByteBuf> request, HttpServerResponse<ByteBuf> response) {
                return response.sendHeaders().writeString(Observable.just("Hello"));
            }
        });
        serverRule.assertChunks("Hello");
    }

    @Test(timeout = 60000)
    public void testResponseWithContentLengthReturnsRawBody() throws Exception {
        serverRule.startServer(new RequestHandler<ByteBuf, ByteBuf>() {
            @Override
            public Observable<Void> handle(HttpServerRequest<ByteBuf> request, HttpServerResponse<ByteBuf> response) {
                return response.setStatus(BAD_REQUEST).setHeader(CONTENT_LENGTH, 5).writeString(Observable.just("Hello"));
            }
        });
        serverRule.assertBodyWithContentLength(5, "Hello");
    }

    @Test(timeout = 60000)
    public void testResponseWithZeroContentLengthReturnsEmptyBody() throws Exception {
        serverRule.startServer(new RequestHandler<ByteBuf, ByteBuf>() {
            @Override
            public Observable<Void> handle(HttpServerRequest<ByteBuf> request, HttpServerResponse<ByteBuf> response) {
                return response.setStatus(BAD_REQUEST).setHeader(CONTENT_LENGTH, 0);
            }
        });
        serverRule.assertEmptyBodyWithContentLengthZero();
    }

    @Test(timeout = 60000)
    public void testResponseWithOnlyPositiveContentLengthReturnsEmptyBody() throws Exception {
        serverRule.startServer(new RequestHandler<ByteBuf, ByteBuf>() {
            @Override
            public Observable<Void> handle(HttpServerRequest<ByteBuf> request, HttpServerResponse<ByteBuf> response) {
                return response.setStatus(BAD_REQUEST).setHeader(CONTENT_LENGTH, 5);
            }
        });
        serverRule.assertEmptyBodyWithContentLengthZero();
    }
}

