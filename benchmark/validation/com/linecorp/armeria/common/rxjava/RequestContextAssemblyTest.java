/**
 * Copyright 2018 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.linecorp.armeria.common.rxjava;


import HttpMethod.GET;
import HttpStatus.INTERNAL_SERVER_ERROR;
import HttpStatus.OK;
import com.linecorp.armeria.client.HttpClient;
import com.linecorp.armeria.common.HttpHeaders;
import com.linecorp.armeria.common.HttpRequest;
import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.server.ServerBuilder;
import com.linecorp.armeria.server.ServiceRequestContext;
import com.linecorp.armeria.server.annotation.Get;
import com.linecorp.armeria.testing.server.ServerRule;
import io.reactivex.Single;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Rule;
import org.junit.Test;


public class RequestContextAssemblyTest {
    @Rule
    public final ServerRule rule = new ServerRule() {
        @Override
        protected void configure(ServerBuilder sb) throws Exception {
            sb.annotatedService(new Object() {
                @Get("/foo")
                public HttpResponse foo(ServiceRequestContext ctx, HttpRequest req) {
                    final CompletableFuture<HttpResponse> res = new CompletableFuture<>();
                    flowable(10).map(RequestContextAssemblyTest::checkRequestContext).flatMapSingle(RequestContextAssemblyTest::single).map(RequestContextAssemblyTest::checkRequestContext).flatMapSingle(RequestContextAssemblyTest::nestSingle).map(RequestContextAssemblyTest::checkRequestContext).flatMapMaybe(RequestContextAssemblyTest::maybe).map(RequestContextAssemblyTest::checkRequestContext).flatMapCompletable(RequestContextAssemblyTest::completable).subscribe(() -> {
                        res.complete(HttpResponse.of(HttpStatus.OK));
                    }, ( e) -> {
                        res.completeExceptionally(e);
                    });
                    return HttpResponse.from(res);
                }

                @Get("/single")
                public HttpResponse single(ServiceRequestContext ctx, HttpRequest req) {
                    final CompletableFuture<HttpResponse> res = new CompletableFuture<>();
                    Single.just("").flatMap(RequestContextAssemblyTest::single).subscribe(( s, throwable) -> {
                        res.complete(HttpResponse.of(HttpStatus.OK));
                    });
                    return HttpResponse.from(res);
                }
            });
        }
    };

    private final ExecutorService pool = Executors.newCachedThreadPool();

    @Test
    public void enableTracking() throws Exception {
        try {
            RequestContextAssembly.enable();
            final HttpClient client = HttpClient.of(rule.uri("/"));
            assertThat(client.execute(HttpHeaders.of(GET, "/foo")).aggregate().get().status()).isEqualTo(OK);
        } finally {
            RequestContextAssembly.disable();
        }
    }

    @Test
    public void withoutTracking() throws Exception {
        final HttpClient client = HttpClient.of(rule.uri("/"));
        assertThat(client.execute(HttpHeaders.of(GET, "/foo")).aggregate().get().status()).isEqualTo(INTERNAL_SERVER_ERROR);
    }

    @Test
    public void composeWithOtherHook() throws Exception {
        final AtomicInteger calledFlag = new AtomicInteger();
        RxJavaPlugins.setOnSingleAssembly(( single) -> {
            calledFlag.incrementAndGet();
            return single;
        });
        final HttpClient client = HttpClient.of(rule.uri("/"));
        client.execute(HttpHeaders.of(GET, "/single")).aggregate().get();
        assertThat(calledFlag.get()).isEqualTo(3);
        try {
            RequestContextAssembly.enable();
            client.execute(HttpHeaders.of(GET, "/single")).aggregate().get();
            assertThat(calledFlag.get()).isEqualTo(6);
        } finally {
            RequestContextAssembly.disable();
        }
        client.execute(HttpHeaders.of(GET, "/single")).aggregate().get();
        assertThat(calledFlag.get()).isEqualTo(9);
        RxJavaPlugins.setOnSingleAssembly(null);
        client.execute(HttpHeaders.of(GET, "/single")).aggregate().get();
        assertThat(calledFlag.get()).isEqualTo(9);
    }
}

