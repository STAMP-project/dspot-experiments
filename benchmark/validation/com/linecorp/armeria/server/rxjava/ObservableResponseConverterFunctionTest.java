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
package com.linecorp.armeria.server.rxjava;


import HttpStatus.BAD_REQUEST;
import HttpStatus.INTERNAL_SERVER_ERROR;
import HttpStatus.OK;
import MediaType.JSON_SEQ;
import MediaType.JSON_UTF_8;
import MediaType.PLAIN_TEXT_UTF_8;
import com.google.common.collect.ImmutableList;
import com.linecorp.armeria.client.HttpClient;
import com.linecorp.armeria.common.AggregatedHttpMessage;
import com.linecorp.armeria.common.HttpHeaders;
import com.linecorp.armeria.common.HttpObject;
import com.linecorp.armeria.server.ServerBuilder;
import com.linecorp.armeria.server.annotation.Get;
import com.linecorp.armeria.server.annotation.ProducesJson;
import com.linecorp.armeria.server.annotation.ProducesJsonSequences;
import com.linecorp.armeria.server.annotation.ProducesText;
import com.linecorp.armeria.testing.internal.AnticipatedException;
import com.linecorp.armeria.testing.server.ServerRule;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.ClassRule;
import org.junit.Test;


public class ObservableResponseConverterFunctionTest {
    @ClassRule
    public static final ServerRule rule = new ServerRule() {
        @Override
        protected void configure(ServerBuilder sb) throws Exception {
            sb.annotatedService("/maybe", new Object() {
                @Get("/string")
                public Maybe<String> string() {
                    return Maybe.just("a");
                }

                @Get("/json")
                @ProducesJson
                public Maybe<String> json() {
                    return Maybe.just("a");
                }

                @Get("/empty")
                public Maybe<String> empty() {
                    return Maybe.empty();
                }

                @Get("/error")
                public Maybe<String> error() {
                    return Maybe.error(new AnticipatedException());
                }
            });
            sb.annotatedService("/single", new Object() {
                @Get("/string")
                public Single<String> string() {
                    return Single.just("a");
                }

                @Get("/json")
                @ProducesJson
                public Single<String> json() {
                    return Single.just("a");
                }

                @Get("/error")
                public Single<String> error() {
                    return Single.error(new AnticipatedException());
                }
            });
            sb.annotatedService("/completable", new Object() {
                @Get("/done")
                public Completable done() {
                    return Completable.complete();
                }

                @Get("/error")
                public Completable error() {
                    return Completable.error(new AnticipatedException());
                }
            });
            sb.annotatedService("/observable", new Object() {
                @Get("/string")
                @ProducesText
                public Observable<String> string() {
                    return Observable.just("a");
                }

                @Get("/json/1")
                @ProducesJson
                public Observable<String> json1() {
                    return Observable.just("a");
                }

                @Get("/json/3")
                @ProducesJson
                public Observable<String> json3() {
                    return Observable.just("a", "b", "c");
                }

                @Get("/error")
                public Observable<String> error() {
                    return Observable.error(new AnticipatedException());
                }
            });
            sb.annotatedService("/streaming", new Object() {
                @Get("/json")
                @ProducesJsonSequences
                public Observable<String> json() {
                    return Observable.just("a", "b", "c");
                }
            });
            sb.annotatedService("/failure", new Object() {
                @Get("/immediate1")
                public Observable<String> immediate1() {
                    throw new IllegalArgumentException("Bad request!");
                }

                @Get("/immediate2")
                public Observable<String> immediate2() {
                    return Observable.error(new IllegalArgumentException("Bad request!"));
                }

                @Get("/defer1")
                public Observable<String> defer1() {
                    return Observable.defer(() -> Observable.error(new IllegalArgumentException("Bad request!")));
                }

                @Get("/defer2")
                public Observable<String> defer2() {
                    return Observable.switchOnNext(Observable.just(Observable.just("a", "b", "c"), Observable.error(new IllegalArgumentException("Bad request!"))));
                }
            });
        }
    };

    @Test
    public void maybe() {
        final HttpClient client = HttpClient.of(ObservableResponseConverterFunctionTest.rule.uri("/maybe"));
        AggregatedHttpMessage msg;
        msg = client.get("/string").aggregate().join();
        assertThat(msg.contentType()).isEqualTo(PLAIN_TEXT_UTF_8);
        assertThat(msg.contentUtf8()).isEqualTo("a");
        msg = client.get("/json").aggregate().join();
        assertThat(msg.contentType()).isEqualTo(JSON_UTF_8);
        assertThatJson(msg.contentUtf8()).isStringEqualTo("a");
        msg = client.get("/empty").aggregate().join();
        assertThat(msg.status()).isEqualTo(OK);
        assertThat(isEmpty()).isTrue();
        msg = client.get("/error").aggregate().join();
        assertThat(msg.status()).isEqualTo(INTERNAL_SERVER_ERROR);
    }

    @Test
    public void single() {
        final HttpClient client = HttpClient.of(ObservableResponseConverterFunctionTest.rule.uri("/single"));
        AggregatedHttpMessage msg;
        msg = client.get("/string").aggregate().join();
        assertThat(msg.contentType()).isEqualTo(PLAIN_TEXT_UTF_8);
        assertThat(msg.contentUtf8()).isEqualTo("a");
        msg = client.get("/json").aggregate().join();
        assertThat(msg.contentType()).isEqualTo(JSON_UTF_8);
        assertThatJson(msg.contentUtf8()).isStringEqualTo("a");
        msg = client.get("/error").aggregate().join();
        assertThat(msg.status()).isEqualTo(INTERNAL_SERVER_ERROR);
    }

    @Test
    public void completable() {
        final HttpClient client = HttpClient.of(ObservableResponseConverterFunctionTest.rule.uri("/completable"));
        AggregatedHttpMessage msg;
        msg = client.get("/done").aggregate().join();
        assertThat(msg.status()).isEqualTo(OK);
        msg = client.get("/error").aggregate().join();
        assertThat(msg.status()).isEqualTo(INTERNAL_SERVER_ERROR);
    }

    @Test
    public void observable() {
        final HttpClient client = HttpClient.of(ObservableResponseConverterFunctionTest.rule.uri("/observable"));
        AggregatedHttpMessage msg;
        msg = client.get("/string").aggregate().join();
        assertThat(msg.contentType()).isEqualTo(PLAIN_TEXT_UTF_8);
        assertThat(msg.contentUtf8()).isEqualTo("a");
        msg = client.get("/json/1").aggregate().join();
        assertThat(msg.contentType()).isEqualTo(JSON_UTF_8);
        assertThatJson(msg.contentUtf8()).isArray().ofLength(1).thatContains("a");
        msg = client.get("/json/3").aggregate().join();
        assertThat(msg.contentType()).isEqualTo(JSON_UTF_8);
        assertThatJson(msg.contentUtf8()).isArray().ofLength(3).thatContains("a").thatContains("b").thatContains("c");
        msg = client.get("/error").aggregate().join();
        assertThat(msg.status()).isEqualTo(INTERNAL_SERVER_ERROR);
    }

    @Test
    public void streaming() {
        final HttpClient client = HttpClient.of(ObservableResponseConverterFunctionTest.rule.uri("/streaming"));
        final AtomicBoolean isFinished = new AtomicBoolean();
        client.get("/json").subscribe(new io.reactivex.subscribers.DefaultSubscriber<HttpObject>() {
            final ImmutableList.Builder<HttpObject> received = new ImmutableList.Builder<>();

            @Override
            public void onNext(HttpObject httpObject) {
                received.add(httpObject);
            }

            @Override
            public void onError(Throwable t) {
                throw new Error("Should not reach here.");
            }

            @Override
            public void onComplete() {
                final Iterator<HttpObject> it = received.build().iterator();
                final HttpHeaders headers = ((HttpHeaders) (it.next()));
                assertThat(headers.status()).isEqualTo(OK);
                assertThat(headers.contentType()).isEqualTo(JSON_SEQ);
                // JSON Text Sequences: *(Record Separator[0x1E] JSON-text Line Feed[0x0A])
                assertThat(array()).isEqualTo(new byte[]{ 30, '\"', 'a', '\"', 10 });
                assertThat(array()).isEqualTo(new byte[]{ 30, '\"', 'b', '\"', 10 });
                assertThat(array()).isEqualTo(new byte[]{ 30, '\"', 'c', '\"', 10 });
                assertThat(isEmpty()).isTrue();
                assertThat(it.hasNext()).isFalse();
                isFinished.set(true);
            }
        });
        await().until(isFinished::get);
    }

    @Test
    public void failure() {
        final HttpClient client = HttpClient.of(ObservableResponseConverterFunctionTest.rule.uri("/failure"));
        AggregatedHttpMessage msg;
        msg = client.get("/immediate1").aggregate().join();
        assertThat(msg.status()).isEqualTo(BAD_REQUEST);
        msg = client.get("/immediate2").aggregate().join();
        assertThat(msg.status()).isEqualTo(BAD_REQUEST);
        msg = client.get("/defer1").aggregate().join();
        assertThat(msg.status()).isEqualTo(BAD_REQUEST);
        msg = client.get("/defer2").aggregate().join();
        assertThat(msg.status()).isEqualTo(BAD_REQUEST);
    }
}

