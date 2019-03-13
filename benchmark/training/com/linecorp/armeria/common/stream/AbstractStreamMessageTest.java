/**
 * Copyright 2017 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.linecorp.armeria.common.stream;


import com.google.common.collect.ImmutableList;
import com.linecorp.armeria.common.util.Exceptions;
import com.linecorp.armeria.testing.common.EventLoopRule;
import com.linecorp.armeria.unsafe.ByteBufHttpData;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.UnpooledHeapByteBuf;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.reactivestreams.Subscription;


// Allow using the same tests for writers and non-writers
@SuppressWarnings("unchecked")
public abstract class AbstractStreamMessageTest {
    static final List<Integer> TEN_INTEGERS = IntStream.range(0, 10).boxed().collect(ImmutableList.toImmutableList());

    @ClassRule
    public static final EventLoopRule eventLoop = new EventLoopRule();

    private List<Integer> result;

    private volatile boolean completed;

    private volatile Throwable error;

    @Test
    public void full_writeFirst() throws Exception {
        final StreamMessage<Integer> stream = newStream(streamValues());
        writeTenIntegers(stream);
        stream.subscribe(new AbstractStreamMessageTest.ResultCollectingSubscriber() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(Long.MAX_VALUE);
            }
        });
        assertSuccess();
    }

    @Test
    public void full_writeAfter() throws Exception {
        final StreamMessage<Integer> stream = newStream(streamValues());
        stream.subscribe(new AbstractStreamMessageTest.ResultCollectingSubscriber() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(Long.MAX_VALUE);
            }
        });
        writeTenIntegers(stream);
        assertSuccess();
    }

    // Verifies that re-entrancy into onNext, e.g. calling onNext -> request -> onNext, is not allowed, as per
    // reactive streams spec 3.03. If it were allowed, the order of processing would be incorrect and the test
    // would fail.
    @Test
    public void flowControlled_writeThenDemandThenProcess() throws Exception {
        final StreamMessage<Integer> stream = newStream(streamValues());
        writeTenIntegers(stream);
        stream.subscribe(new AbstractStreamMessageTest.ResultCollectingSubscriber() {
            private Subscription subscription;

            @Override
            public void onSubscribe(Subscription s) {
                subscription = s;
                subscription.request(1);
            }

            @Override
            public void onNext(Integer value) {
                subscription.request(1);
                super.onNext(value);
            }
        });
        assertSuccess();
    }

    @Test
    public void flowControlled_writeThenDemandThenProcess_eventLoop() throws Exception {
        final StreamMessage<Integer> stream = newStream(streamValues());
        writeTenIntegers(stream);
        AbstractStreamMessageTest.eventLoop.get().submit(() -> stream.subscribe(new com.linecorp.armeria.common.stream.ResultCollectingSubscriber() {
            private Subscription subscription;

            @Override
            public void onSubscribe(Subscription s) {
                subscription = s;
                subscription.request(1);
            }

            @Override
            public void onNext(Integer value) {
                subscription.request(1);
                super.onNext(value);
            }
        }, eventLoop.get())).syncUninterruptibly();
        assertSuccess();
    }

    @Test
    public void flowControlled_writeThenProcessThenDemand() throws Exception {
        final StreamMessage<Integer> stream = newStream(streamValues());
        writeTenIntegers(stream);
        stream.subscribe(new AbstractStreamMessageTest.ResultCollectingSubscriber() {
            private Subscription subscription;

            @Override
            public void onSubscribe(Subscription s) {
                subscription = s;
                subscription.request(1);
            }

            @Override
            public void onNext(Integer value) {
                super.onNext(value);
                subscription.request(1);
            }
        });
        assertSuccess();
    }

    @Test
    public void releaseOnConsumption_ByteBuf() throws Exception {
        final ByteBuf buf = AbstractStreamMessageTest.newPooledBuffer();
        final StreamMessage<ByteBuf> stream = newStream(ImmutableList.of(buf));
        if (stream instanceof StreamWriter) {
            ((StreamWriter<ByteBuf>) (stream)).write(buf);
            ((StreamWriter<?>) (stream)).close();
        }
        assertThat(buf.refCnt()).isEqualTo(1);
        stream.subscribe(new org.reactivestreams.Subscriber<ByteBuf>() {
            @Override
            public void onSubscribe(Subscription subscription) {
                subscription.request(1);
            }

            @Override
            public void onNext(ByteBuf o) {
                assertThat(o).isNotSameAs(buf);
                assertThat(o).isInstanceOf(UnpooledHeapByteBuf.class);
                assertThat(o.refCnt()).isEqualTo(1);
                assertThat(buf.refCnt()).isZero();
            }

            @Override
            public void onError(Throwable throwable) {
                Exceptions.throwUnsafely(throwable);
            }

            @Override
            public void onComplete() {
                completed = true;
            }
        });
        await().untilAsserted(() -> assertThat(completed).isTrue());
    }

    @Test
    public void releaseOnConsumption_HttpData() throws Exception {
        final ByteBufHttpData data = new ByteBufHttpData(AbstractStreamMessageTest.newPooledBuffer(), false);
        final StreamMessage<ByteBufHolder> stream = newStream(ImmutableList.of(data));
        if (stream instanceof StreamWriter) {
            ((StreamWriter<ByteBufHolder>) (stream)).write(data);
            ((StreamWriter<?>) (stream)).close();
        }
        assertThat(data.refCnt()).isEqualTo(1);
        stream.subscribe(new org.reactivestreams.Subscriber<ByteBufHolder>() {
            @Override
            public void onSubscribe(Subscription subscription) {
                subscription.request(1);
            }

            @Override
            public void onNext(ByteBufHolder o) {
                assertThat(o).isNotSameAs(data);
                assertThat(o).isInstanceOf(ByteBufHttpData.class);
                assertThat(o.content()).isInstanceOf(UnpooledHeapByteBuf.class);
                assertThat(o.refCnt()).isEqualTo(1);
                assertThat(data.refCnt()).isZero();
            }

            @Override
            public void onError(Throwable throwable) {
                Exceptions.throwUnsafely(throwable);
            }

            @Override
            public void onComplete() {
                completed = true;
            }
        });
        await().untilAsserted(() -> assertThat(completed).isTrue());
    }

    @Test
    public void releaseWithZeroDemand() {
        final ByteBufHttpData data = new ByteBufHttpData(AbstractStreamMessageTest.newPooledBuffer(), true);
        final StreamMessage<Object> stream = newStream(ImmutableList.of(data));
        if (stream instanceof StreamWriter) {
            ((StreamWriter<Object>) (stream)).write(data);
        }
        stream.subscribe(new org.reactivestreams.Subscriber<Object>() {
            @Override
            public void onSubscribe(Subscription subscription) {
                // Cancel the subscription when the demand is 0.
                subscription.cancel();
            }

            @Override
            public void onNext(Object o) {
                Assert.fail();
            }

            @Override
            public void onError(Throwable throwable) {
                Assert.fail();
            }

            @Override
            public void onComplete() {
                Assert.fail();
            }
        }, true);
        await().untilAsserted(() -> assertThat(stream.isOpen()).isFalse());
        await().untilAsserted(() -> assertThat(data.refCnt()).isZero());
    }

    @Test
    public void releaseWithZeroDemandAndClosedStream() {
        final ByteBufHttpData data = new ByteBufHttpData(AbstractStreamMessageTest.newPooledBuffer(), true);
        final StreamMessage<Object> stream = newStream(ImmutableList.of(data));
        if (stream instanceof StreamWriter) {
            ((StreamWriter<Object>) (stream)).write(data);
            ((StreamWriter<Object>) (stream)).close();
        }
        stream.subscribe(new org.reactivestreams.Subscriber<Object>() {
            @Override
            public void onSubscribe(Subscription subscription) {
                // Cancel the subscription when the demand is 0.
                subscription.cancel();
            }

            @Override
            public void onNext(Object o) {
                Assert.fail();
            }

            @Override
            public void onError(Throwable throwable) {
                Assert.fail();
            }

            @Override
            public void onComplete() {
                Assert.fail();
            }
        }, true);
        await().untilAsserted(() -> assertThat(stream.isOpen()).isFalse());
        await().untilAsserted(() -> assertThat(data.refCnt()).isZero());
    }

    private abstract class ResultCollectingSubscriber implements org.reactivestreams.Subscriber<Integer> {
        @Override
        public void onNext(Integer value) {
            result.add(value);
        }

        @Override
        public void onComplete() {
            completed = true;
        }

        @Override
        public void onError(Throwable t) {
            error = t;
        }
    }
}

