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
package io.reactivex.netty.protocol.http.client;


import LogLevel.DEBUG;
import LogLevel.ERROR;
import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.client.events.HttpClientEventsListener;
import io.reactivex.netty.protocol.http.server.HttpServer;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import io.reactivex.netty.protocol.http.server.HttpServerResponse;
import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import rx.Observable;


public class EventListenerTest {
    @Rule
    public final EventListenerTest.HttpServerRule rule = new EventListenerTest.HttpServerRule();

    @Test(timeout = 60000)
    public void testEventListener() throws Exception {
        HttpClient<ByteBuf, ByteBuf> client = HttpClient.newClient(rule.serverAddress);
        EventListenerTest.assertListenerCalled(client);
    }

    @Test(timeout = 60000)
    public void testEventListenerPostCopy() throws Exception {
        HttpClient<ByteBuf, ByteBuf> client = HttpClient.newClient(rule.serverAddress).enableWireLogging("test", ERROR);
        EventListenerTest.assertListenerCalled(client);
    }

    @Test(timeout = 60000)
    public void testSubscriptionPreCopy() throws Exception {
        HttpClient<ByteBuf, ByteBuf> client = HttpClient.newClient(rule.serverAddress);
        EventListenerTest.MockHttpClientEventsListener listener = EventListenerTest.subscribe(client);
        client = client.enableWireLogging("test", DEBUG);
        EventListenerTest.connectAndAssertListenerInvocation(client, listener);
    }

    public static class HttpServerRule extends ExternalResource {
        private SocketAddress serverAddress;

        @Override
        public Statement apply(final Statement base, Description description) {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    serverAddress = HttpServer.newServer().enableWireLogging("test", ERROR).start(new io.reactivex.netty.protocol.http.server.RequestHandler<ByteBuf, ByteBuf>() {
                        @Override
                        public Observable<Void> handle(HttpServerRequest<ByteBuf> request, HttpServerResponse<ByteBuf> response) {
                            return response.writeString(Observable.just("Hello"));
                        }
                    }).getServerAddress();
                    base.evaluate();
                }
            };
        }
    }

    private static class MockHttpClientEventsListener extends HttpClientEventsListener {
        private volatile boolean httpListenerInvoked;

        private volatile boolean tcpListenerInvoked;

        @Override
        public void onResponseHeadersReceived(int responseCode, long duration, TimeUnit timeUnit) {
            httpListenerInvoked = true;
        }

        @Override
        public void onByteRead(long bytesRead) {
            tcpListenerInvoked = true;
        }
    }
}

