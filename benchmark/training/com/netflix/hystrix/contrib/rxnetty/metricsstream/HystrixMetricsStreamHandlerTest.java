/**
 * Copyright 2015 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netflix.hystrix.contrib.rxnetty.metricsstream;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.HystrixCommandMetrics;
import com.netflix.hystrix.HystrixCommandMetricsSamples;
import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.client.HttpClient;
import io.reactivex.netty.protocol.http.client.HttpClientRequest;
import io.reactivex.netty.protocol.http.client.HttpClientResponse;
import io.reactivex.netty.protocol.http.server.HttpServer;
import io.reactivex.netty.protocol.http.sse.ServerSentEvent;
import java.util.Collection;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import rx.Observable;

import static java.util.concurrent.TimeUnit.MILLISECONDS;


/**
 *
 *
 * @author Tomasz Bak
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(HystrixCommandMetrics.class)
public class HystrixMetricsStreamHandlerTest {
    private static final ObjectMapper mapper = new ObjectMapper();

    private static Collection<HystrixCommandMetrics> SAMPLE_HYSTRIX_COMMAND_METRICS = Collections.singleton(HystrixCommandMetricsSamples.SAMPLE_1);

    private int port;

    private HttpServer<ByteBuf, ByteBuf> server;

    private HttpClient<ByteBuf, ServerSentEvent> client;

    @Test
    public void testMetricsAreDeliveredAsSseStream() throws Exception {
        replayAll();
        Observable<ServerSentEvent> objectObservable = client.submit(HttpClientRequest.createGet(DEFAULT_HYSTRIX_PREFIX)).flatMap(new rx.functions.Func1<HttpClientResponse<ServerSentEvent>, Observable<? extends ServerSentEvent>>() {
            @Override
            public Observable<? extends ServerSentEvent> call(HttpClientResponse<ServerSentEvent> httpClientResponse) {
                return httpClientResponse.getContent().take(1);
            }
        });
        Object first = Observable.amb(objectObservable, Observable.timer(5000, MILLISECONDS)).toBlocking().first();
        Assert.assertTrue("Expected SSE message", (first instanceof ServerSentEvent));
        ServerSentEvent sse = ((ServerSentEvent) (first));
        JsonNode jsonNode = HystrixMetricsStreamHandlerTest.mapper.readTree(sse.contentAsString());
        Assert.assertEquals("Expected hystrix key name", HystrixCommandMetricsSamples.SAMPLE_1.getCommandKey().name(), jsonNode.get("name").asText());
    }
}

