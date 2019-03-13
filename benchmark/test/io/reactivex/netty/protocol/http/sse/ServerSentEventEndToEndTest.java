/**
 * Copyright 2015 Netflix, Inc.
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
package io.reactivex.netty.protocol.http.sse;


import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.server.HttpServer;
import io.reactivex.netty.protocol.http.server.HttpServerResponse;
import org.junit.Test;
import rx.Observable;


public class ServerSentEventEndToEndTest {
    private HttpServer<ByteBuf, ByteBuf> sseServer;

    @Test(timeout = 60000)
    public void testWriteRawString() throws Exception {
        startServer(new rx.functions.Func1<HttpServerResponse<ServerSentEvent>, Observable<Void>>() {
            @Override
            public Observable<Void> call(HttpServerResponse<ServerSentEvent> response) {
                return response.writeStringAndFlushOnEach(just("data: interval 1\n"));
            }
        });
        receiveAndAssertSingleEvent();
    }

    @Test(timeout = 60000)
    public void testWriteRawBytes() throws Exception {
        startServer(new rx.functions.Func1<HttpServerResponse<ServerSentEvent>, Observable<Void>>() {
            @Override
            public Observable<Void> call(HttpServerResponse<ServerSentEvent> response) {
                return response.writeBytesAndFlushOnEach(just("data: interval 1\n".getBytes()));
            }
        });
        receiveAndAssertSingleEvent();
    }

    @Test(timeout = 60000)
    public void testWriteServerSentEvent() throws Exception {
        startServer(new rx.functions.Func1<HttpServerResponse<ServerSentEvent>, Observable<Void>>() {
            @Override
            public Observable<Void> call(HttpServerResponse<ServerSentEvent> response) {
                return response.writeAndFlushOnEach(just(ServerSentEvent.withData("interval 1")));
            }
        });
        receiveAndAssertSingleEvent();
    }
}

