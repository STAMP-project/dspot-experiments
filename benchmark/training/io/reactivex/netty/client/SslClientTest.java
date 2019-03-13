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
package io.reactivex.netty.client;


import org.junit.Test;


public class SslClientTest {
    @Test(timeout = 60000)
    public void testReleaseOnSslFailure() throws Exception {
        // TODO: Fix me
        /* HttpServer<ByteBuf, ByteBuf> server =
        HttpServer.newServer()
        .start(new RequestHandler<ByteBuf, ByteBuf>() {
        @Override
        public Observable<Void> handle(HttpServerRequest<ByteBuf> request,
        HttpServerResponse<ByteBuf> response) {
        return Observable.empty();
        }
        });
        final SocketAddress serverAddress = new InetSocketAddress("127.0.0.1", server.getServerPort());

        MockPoolLimitDeterminationStrategy strategy = new MockPoolLimitDeterminationStrategy(1);

        // The connect fails because the server does not support SSL.
        TestSubscriber<ByteBuf> subscriber = new TestSubscriber<>();
        final PoolConfig<ByteBuf, ByteBuf> config = new PoolConfig<>();
        config.limitDeterminationStrategy(strategy);

        ConnectionProvider<ByteBuf, ByteBuf> connectionProvider = PooledConnectionProvider.create(config, serverAddress);

        HttpClient.newClient(connectionProvider)
        .unsafeSecure()
        .createGet("/")
        .flatMap(new Func1<HttpClientResponse<ByteBuf>, Observable<ByteBuf>>() {
        @Override
        public Observable<ByteBuf> call(HttpClientResponse<ByteBuf> response) {
        return response.getContent();
        }
        })
        .subscribe(subscriber);

        subscriber.awaitTerminalEvent();

        assertThat("Unexpected error notifications.", subscriber.getOnErrorEvents(), hasSize(1));
        assertThat("Unexpected error.", subscriber.getOnErrorEvents().get(0), is(instanceOf(SSLException.class)));

        Assert.assertEquals("Unexpected acquire counts.", 1, strategy.getAcquireCount());
        Assert.assertEquals("Unexpected release counts.", 1, strategy.getReleaseCount());
        Assert.assertEquals("Unexpected available permits.", 1, strategy.getAvailablePermits());
         */
    }
}

