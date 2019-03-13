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
package io.reactivex.netty.protocol.http.ws.client;


import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.reactivex.netty.channel.Connection;
import io.reactivex.netty.protocol.http.ws.WebSocketConnection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import rx.Observable;
import rx.Subscriber;
import rx.observers.TestSubscriber;
import rx.subjects.Subject;


public class OperatorCacheSingleWebsocketConnectionTest {
    @Rule
    public final OperatorCacheSingleWebsocketConnectionTest.OpRule opRule = new OperatorCacheSingleWebsocketConnectionTest.OpRule();

    @Test(timeout = 60000)
    public void testLifecycleNeverEnds() throws Exception {
        opRule.subscribeAndAssertValues(opRule.getSourceWithCache().repeat(2), 1);
    }

    @Test(timeout = 60000)
    public void testLifecycleCompletesImmediately() throws Exception {
        opRule.subscribeAndAssertValues(opRule.getSourceWithCache().map(new rx.functions.Func1<WebSocketConnection, WebSocketConnection>() {
            @Override
            public WebSocketConnection call(WebSocketConnection c) {
                opRule.terminateFirstConnection(null);
                return c;
            }
        }).repeat(2), 2);// Since the cached item is immediately invalid, two items will be emitted from source.

    }

    @Test(timeout = 60000)
    public void testLifecycleErrorsImmediately() throws Exception {
        opRule.subscribeAndAssertValues(opRule.getSourceWithCache().map(new rx.functions.Func1<WebSocketConnection, WebSocketConnection>() {
            @Override
            public WebSocketConnection call(WebSocketConnection c) {
                opRule.terminateFirstConnection(new IllegalStateException());
                return c;
            }
        }).repeat(2), 2);// Since the cached item is immediately invalid, two items will be emitted from source.

    }

    @Test(timeout = 60000)
    public void testSourceEmitsNoItems() throws Exception {
        TestSubscriber<WebSocketConnection> subscriber = new TestSubscriber();
        Observable.<Observable<Observable<WebSocketConnection>>>empty().lift(new OperatorCacheSingleWebsocketConnection()).subscribe(subscriber);
        subscriber.awaitTerminalEvent();
        subscriber.assertError(IllegalStateException.class);
    }

    @Test(timeout = 60000)
    public void testSourceEmitsError() throws Exception {
        TestSubscriber<WebSocketConnection> subscriber = new TestSubscriber();
        Observable.<Observable<WebSocketConnection>>error(new NullPointerException()).nest().lift(new OperatorCacheSingleWebsocketConnection()).subscribe(subscriber);
        subscriber.awaitTerminalEvent();
        subscriber.assertError(NullPointerException.class);
    }

    @Test(timeout = 60000)
    public void testUnsubscribeFromLifecycle() throws Exception {
        opRule.subscribeAndAssertValues(opRule.getSourceWithCache(), 1);
        OperatorCacheSingleWebsocketConnectionTest.LifecycleSubject lifecycleSubject = opRule.lifecycles.poll();
        MatcherAssert.assertThat("No subscribers to lifecycle.", lifecycleSubject.subscribers, hasSize(1));
        MatcherAssert.assertThat("Lifecycle subscriber not unsubscribed.", lifecycleSubject.subscribers.poll().isUnsubscribed(), is(true));
    }

    private static class OpRule extends ExternalResource {
        private Observable<Observable<Observable<WebSocketConnection>>> source;

        private ConcurrentLinkedQueue<OperatorCacheSingleWebsocketConnectionTest.LifecycleSubject> lifecycles;

        @Override
        public Statement apply(final Statement base, Description description) {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    lifecycles = new ConcurrentLinkedQueue<>();
                    source = Observable.create(new rx.Observable.OnSubscribe<Observable<WebSocketConnection>>() {
                        @Override
                        public void call(Subscriber<? super Observable<WebSocketConnection>> s) {
                            OperatorCacheSingleWebsocketConnectionTest.LifecycleSubject l = new OperatorCacheSingleWebsocketConnectionTest.LifecycleSubject(new ConcurrentLinkedQueue<Subscriber<? super Void>>());
                            lifecycles.add(l);
                            WebSocketConnection conn = newConnection(l);
                            /* Subscriptions to the emitted Observable will always give the same connection but
                            subscription to the source (this Observable) creates a new connection. This simulates
                            how the actual websocket connection get works.
                             */
                            s.onNext(Observable.just(conn));
                            s.onCompleted();
                        }
                    }).nest();
                    base.evaluate();
                }
            };
        }

        public boolean terminateFirstConnection(Throwable error) {
            OperatorCacheSingleWebsocketConnectionTest.LifecycleSubject poll = lifecycles.poll();
            if (null != poll) {
                if (null == error) {
                    poll.onCompleted();
                } else {
                    poll.onError(error);
                }
                return true;
            }
            return false;
        }

        public Observable<WebSocketConnection> getSourceWithCache() {
            return source.lift(new OperatorCacheSingleWebsocketConnection());
        }

        public void subscribeAndAssertValues(Observable<WebSocketConnection> source, int distinctItemsCount) {
            TestSubscriber<WebSocketConnection> subscriber = new TestSubscriber();
            source.subscribe(subscriber);
            subscriber.awaitTerminalEvent();
            subscriber.assertNoErrors();
            List<WebSocketConnection> onNextEvents = subscriber.getOnNextEvents();
            Set<WebSocketConnection> distinctConns = new java.util.HashSet(onNextEvents);
            MatcherAssert.assertThat("Unexpected number of distinct connections.", distinctConns, hasSize(distinctItemsCount));
        }

        private WebSocketConnection newConnection(final Observable<Void> lifecycle) {
            @SuppressWarnings("unchecked")
            Connection<WebSocketFrame, WebSocketFrame> mock = Mockito.mock(Connection.class);
            Mockito.when(mock.closeListener()).thenAnswer(new Answer<Observable<Void>>() {
                @Override
                public Observable<Void> answer(InvocationOnMock invocation) throws Throwable {
                    return lifecycle;
                }
            });
            return new WebSocketConnection(mock);
        }
    }

    private static class LifecycleSubject extends Subject<Void, Void> {
        private final ConcurrentLinkedQueue<Subscriber<? super Void>> subscribers;

        protected LifecycleSubject(final ConcurrentLinkedQueue<Subscriber<? super Void>> subscribers) {
            super(new rx.Observable.OnSubscribe<Void>() {
                @Override
                public void call(Subscriber<? super Void> subscriber) {
                    subscribers.add(subscriber);
                }
            });
            this.subscribers = subscribers;
        }

        @Override
        public boolean hasObservers() {
            return !(subscribers.isEmpty());
        }

        @Override
        public void onCompleted() {
            for (Subscriber<? super Void> subscriber : subscribers) {
                subscriber.onCompleted();
            }
        }

        @Override
        public void onError(Throwable e) {
            for (Subscriber<? super Void> subscriber : subscribers) {
                subscriber.onError(e);
            }
        }

        @Override
        public void onNext(Void aVoid) {
            // No op ...
        }
    }
}

