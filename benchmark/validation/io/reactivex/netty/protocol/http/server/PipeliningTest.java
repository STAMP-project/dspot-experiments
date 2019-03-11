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
package io.reactivex.netty.protocol.http.server;


import HttpResponseStatus.OK;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.reactivex.netty.channel.Connection;
import io.reactivex.netty.protocol.tcp.client.TcpClient;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import rx.Observable;
import rx.observers.TestSubscriber;


public class PipeliningTest {
    @Rule
    public final HttpServerRule serverRule = new HttpServerRule();

    @Test(timeout = 60000)
    public void testPipelining() throws Exception {
        serverRule.startServer();
        /* Since HTTP client does not yet support pipeling, this example uses a TCP client */
        TestSubscriber<String> testSubscriber = new TestSubscriber();
        TcpClient.newClient(serverRule.getServerAddress()).<FullHttpRequest, FullHttpResponse>pipelineConfigurator(new rx.functions.Action1<ChannelPipeline>() {
            @Override
            public void call(ChannelPipeline pipeline) {
                pipeline.addLast(new HttpClientCodec());
                pipeline.addLast(new HttpObjectAggregator((1024 * 1024)));
            }
        }).createConnectionRequest().flatMap(new rx.functions.Func1<Connection<FullHttpResponse, FullHttpRequest>, Observable<FullHttpResponse>>() {
            @Override
            public Observable<FullHttpResponse> call(Connection<FullHttpResponse, FullHttpRequest> c) {
                DefaultFullHttpRequest request1 = new DefaultFullHttpRequest(HTTP_1_1, GET, "/1");
                DefaultFullHttpRequest request2 = new DefaultFullHttpRequest(HTTP_1_1, GET, "/2");
                return c.write(Observable.<FullHttpRequest>just(request1, request2)).ignoreElements().cast(FullHttpResponse.class).concatWith(c.getInput());
            }
        }).map(new rx.functions.Func1<FullHttpResponse, String>() {
            @Override
            public String call(FullHttpResponse resp) {
                return resp.status().toString();
            }
        }).take(2).subscribe(testSubscriber);
        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertNoErrors();
        MatcherAssert.assertThat("Unexpected number of responses.", testSubscriber.getOnNextEvents(), hasSize(2));
        MatcherAssert.assertThat("OK status code not found in response #1.", testSubscriber.getOnNextEvents().get(0), containsString(OK.toString()));
        MatcherAssert.assertThat("OK status code not found in response #2.", testSubscriber.getOnNextEvents().get(1), containsString(OK.toString()));
    }
}

