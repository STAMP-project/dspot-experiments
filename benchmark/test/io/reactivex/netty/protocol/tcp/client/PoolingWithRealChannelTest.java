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
package io.reactivex.netty.protocol.tcp.client;


import io.netty.buffer.ByteBuf;
import io.reactivex.netty.client.pool.PooledConnection;
import java.util.ArrayList;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import rx.Observable;
import rx.observers.AssertableSubscriber;


/**
 * This tests the code paths which are not invoked for {@link EmbeddedChannel} as it does not schedule any task
 * (an EmbeddedChannelEventLopp never returns false for isInEventLoop())
 */
public class PoolingWithRealChannelTest {
    @Rule
    public final TcpClientRule clientRule = new TcpClientRule();

    /**
     * Load test to prove concurrency issues mainly seen on heavy load.
     */
    @Test
    public void testLoad() {
        clientRule.startServer(1000);
        MockTcpClientEventListener listener = new MockTcpClientEventListener();
        clientRule.getClient().subscribe(listener);
        int number_of_iterations = 300;
        int numberOfRequests = 10;
        for (int j = 0; j < number_of_iterations; j++) {
            List<Observable<String>> results = new ArrayList<>();
            // Just giving the client some time to recover
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < numberOfRequests; i++) {
                results.add(Observable.fromCallable(new rx.functions.Func0<PooledConnection<ByteBuf, ByteBuf>>() {
                    @Override
                    public PooledConnection<ByteBuf, ByteBuf> call() {
                        return clientRule.connectWithCheck();
                    }
                }).flatMap(new rx.functions.Func1<PooledConnection<ByteBuf, ByteBuf>, Observable<String>>() {
                    @Override
                    public Observable<String> call(PooledConnection<ByteBuf, ByteBuf> connection) {
                        return connection.writeStringAndFlushOnEach(Observable.just("Hello")).toCompletable().<ByteBuf>toObservable().concatWith(connection.getInput()).take(1).single().map(new rx.functions.Func1<ByteBuf, String>() {
                            @Override
                            public String call(ByteBuf byteBuf) {
                                try {
                                    byte[] bytes = new byte[byteBuf.readableBytes()];
                                    byteBuf.readBytes(bytes);
                                    String result = new String(bytes);
                                    return result;
                                } finally {
                                    byteBuf.release();
                                }
                            }
                        }).doOnError(new rx.functions.Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                Assert.fail(("Did not expect exception: " + (throwable.getMessage())));
                                throwable.printStackTrace();
                            }
                        });
                    }
                }));
            }
            AssertableSubscriber<String> test = Observable.merge(results).test();
            test.awaitTerminalEvent();
            test.assertNoErrors();
        }
    }

    /**
     * Load test to prove concurrency issues mainly seen on heavy load.
     */
    @Test
    public void assertPermitsAreReleasedWhenMergingObservablesWithExceptions() {
        clientRule.startServer(10, true);
        MockTcpClientEventListener listener = new MockTcpClientEventListener();
        clientRule.getClient().subscribe(listener);
        int number_of_iterations = 1;
        int numberOfRequests = 3;
        makeRequests(number_of_iterations, numberOfRequests);
        sleep(clientRule.getPoolConfig().getMaxIdleTimeMillis());
        MatcherAssert.assertThat("Permits should be 10", clientRule.getPoolConfig().getPoolLimitDeterminationStrategy().getAvailablePermits(), Matchers.equalTo(10));
    }
}

