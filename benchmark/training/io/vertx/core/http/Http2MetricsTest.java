/**
 * Copyright (c) 2011-2018 Contributors to the Eclipse Foundation
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package io.vertx.core.http;


import HttpVersion.HTTP_2;
import io.vertx.test.core.TestUtils;
import io.vertx.test.core.VertxTestBase;
import io.vertx.test.fakemetrics.FakeHttpClientMetrics;
import io.vertx.test.fakemetrics.HttpClientOptions;
import io.vertx.test.fakemetrics.HttpServerOptions;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static io.vertx.test.fakemetrics.FakeMetricsBase.getMetrics;


@RunWith(Parameterized.class)
public class Http2MetricsTest extends HttpMetricsTestBase {
    private HttpClientOptions clientOptions;

    private HttpServerOptions serverOptions;

    public Http2MetricsTest(HttpClientOptions clientOptions, HttpServerOptions serverOptions) {
        super(HTTP_2);
        this.clientOptions = clientOptions;
        this.serverOptions = serverOptions.setHandle100ContinueAutomatically(true);
    }

    @Test
    public void testPushPromise() throws Exception {
        waitFor(2);
        int numBuffers = 10;
        int contentLength = numBuffers * 1000;
        server.requestHandler(( req) -> {
            req.response().push(HttpMethod.GET, "/wibble", ( ar) -> {
                HttpServerResponse pushedResp = ar.result();
                FakeHttpServerMetrics serverMetrics = FakeMetricsBase.getMetrics(server);
                HttpServerMetric serverMetric = serverMetrics.getMetric(pushedResp);
                assertNotNull(serverMetric);
                pushedResp.putHeader("content-length", ("" + contentLength));
                AtomicInteger numBuffer = new AtomicInteger(numBuffers);
                vertx.setPeriodic(1, ( timerID) -> {
                    if ((numBuffer.getAndDecrement()) == 0) {
                        pushedResp.end();
                        assertNull(serverMetrics.getMetric(pushedResp));
                        vertx.cancelTimer(timerID);
                        complete();
                    } else {
                        pushedResp.write(TestUtils.randomBuffer(1000));
                    }
                });
            });
        });
        startServer();
        client = vertx.createHttpClient(createBaseClientOptions());
        FakeHttpClientMetrics metrics = getMetrics(client);
        HttpClientRequest req = client.get(HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, "/somepath", ( resp) -> {
        });
        req.pushHandler(( pushedReq) -> {
            HttpClientMetric metric = metrics.getMetric(pushedReq);
            assertNotNull(metric);
            assertSame(pushedReq, metric.request);
            pushedReq.handler(onSuccess(( resp) -> {
                resp.endHandler(( v) -> {
                    assertNull(metrics.getMetric(pushedReq));
                    complete();
                });
            }));
        });
        req.end();
        await();
    }
}

