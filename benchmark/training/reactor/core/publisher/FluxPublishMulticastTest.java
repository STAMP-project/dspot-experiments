/**
 * Copyright (c) 2011-2017 Pivotal Software Inc, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package reactor.core.publisher;


import Fuseable.ANY;
import Fuseable.NONE;
import Fuseable.SYNC;
import Scannable.Attr.ACTUAL;
import Scannable.Attr.BUFFERED;
import Scannable.Attr.CANCELLED;
import Scannable.Attr.ERROR;
import Scannable.Attr.PARENT;
import Scannable.Attr.PREFETCH;
import Scannable.Attr.REQUESTED_FROM_DOWNSTREAM;
import Scannable.Attr.TERMINATED;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import org.junit.Assert;
import org.junit.Test;
import org.reactivestreams.Subscription;
import reactor.core.CoreSubscriber;
import reactor.test.StepVerifier;
import reactor.test.publisher.FluxOperatorTest;
import reactor.test.subscriber.AssertSubscriber;
import reactor.util.concurrent.Queues;
import reactor.util.context.Context;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;


public class FluxPublishMulticastTest extends FluxOperatorTest<String, String> {
    @Test(expected = IllegalArgumentException.class)
    public void failPrefetch() {
        Flux.never().publish(( f) -> f, (-1));
    }

    @Test
    public void subsequentSum() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        Flux.range(1, 5).publish(( o) -> zip((Object[] a) -> ((Integer) (a[0])) + ((Integer) (a[1])), o, o.skip(1))).subscribe(ts);
        ts.assertValues((1 + 2), (2 + 3), (3 + 4), (4 + 5)).assertNoError().assertComplete();
    }

    @Test
    public void subsequentSumHidden() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        Flux.range(1, 5).hide().publish(( o) -> zip((Object[] a) -> ((Integer) (a[0])) + ((Integer) (a[1])), o, o.skip(1))).subscribe(ts);
        ts.assertValues((1 + 2), (2 + 3), (3 + 4), (4 + 5)).assertNoError().assertComplete();
    }

    @Test
    public void subsequentSumAsync() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        UnicastProcessor<Integer> up = UnicastProcessor.create(Queues.<Integer>get(16).get());
        up.publish(( o) -> zip((Object[] a) -> ((Integer) (a[0])) + ((Integer) (a[1])), o, o.skip(1))).subscribe(ts);
        up.onNext(1);
        up.onNext(2);
        up.onNext(3);
        up.onNext(4);
        up.onNext(5);
        up.onComplete();
        ts.assertValues((1 + 2), (2 + 3), (3 + 4), (4 + 5)).assertNoError().assertComplete();
    }

    @Test
    public void cancelComposes() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        EmitterProcessor<Integer> sp = EmitterProcessor.create();
        sp.publish(( o) -> Flux.<Integer>never()).subscribe(ts);
        Assert.assertTrue("Not subscribed?", ((sp.downstreamCount()) != 0));
        ts.cancel();
        Assert.assertTrue("Still subscribed?", ((sp.downstreamCount()) == 0));
    }

    @Test
    public void cancelComposes2() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        EmitterProcessor<Integer> sp = EmitterProcessor.create();
        sp.publish(( o) -> Flux.<Integer>empty()).subscribe(ts);
        Assert.assertFalse("Still subscribed?", ((sp.downstreamCount()) == 1));
    }

    @Test
    public void pairWise() {
        AssertSubscriber<Tuple2<Integer, Integer>> ts = AssertSubscriber.create();
        Flux.range(1, 9).transform(( o) -> zip(o, o.skip(1))).subscribe(ts);
        ts.assertValues(Tuples.of(1, 2), Tuples.of(2, 3), Tuples.of(3, 4), Tuples.of(4, 5), Tuples.of(5, 6), Tuples.of(6, 7), Tuples.of(7, 8), Tuples.of(8, 9)).assertComplete();
    }

    @Test
    public void innerCanFuse() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        ts.requestedFusionMode(ANY);
        Flux.never().publish(( o) -> range(1, 5)).subscribe(ts);
        ts.assertFuseableSource().assertFusionMode(SYNC).assertValues(1, 2, 3, 4, 5).assertComplete().assertNoError();
    }

    @Test
    public void suppressedSubscriber() {
        CoreSubscriber<Integer> s = new CoreSubscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Integer integer) {
            }

            @Override
            public void onError(Throwable t) {
            }

            @Override
            public void onComplete() {
            }
        };
        FluxPublishMulticast.CancelMulticaster<Integer> sfs = new FluxPublishMulticast.CancelMulticaster<>(s, null);
        assertThat(sfs.size()).isEqualTo(0);
        assertThat(sfs.isEmpty()).isFalse();
        assertThat(sfs.poll()).isNull();
        assertThat(sfs.requestFusion(ANY)).isEqualTo(NONE);
        sfs.clear();// NOOP

    }

    @Test
    public void syncCancelBeforeComplete() {
        assertThat(Flux.just(Flux.just(1).publish(( v) -> v)).flatMap(( v) -> v).blockLast()).isEqualTo(1);
    }

    @Test
    public void normalCancelBeforeComplete() {
        assertThat(Flux.just(Flux.just(1).hide().publish(( v) -> v)).flatMap(( v) -> v).blockLast()).isEqualTo(1);
    }

    @Test
    public void scanMulticaster() {
        FluxPublishMulticast.FluxPublishMulticaster<Integer> test = new FluxPublishMulticast.FluxPublishMulticaster<>(123, Queues.<Integer>unbounded(), Context.empty());
        Subscription parent = Operators.emptySubscription();
        test.onSubscribe(parent);
        assertThat(test.scan(PARENT)).isSameAs(parent);
        assertThat(test.scan(PREFETCH)).isEqualTo(123);
        assertThat(test.scan(BUFFERED)).isEqualTo(0);
        test.queue.add(1);
        assertThat(test.scan(BUFFERED)).isEqualTo(1);
        assertThat(test.scan(TERMINATED)).isFalse();
        assertThat(test.scan(ERROR)).isNull();
        test.error = new IllegalArgumentException("boom");
        assertThat(test.scan(ERROR)).isSameAs(test.error);
        test.onComplete();
        assertThat(test.scan(TERMINATED)).isTrue();
        assertThat(test.scan(CANCELLED)).isFalse();
        test.terminate();
        assertThat(test.scan(CANCELLED)).isTrue();
    }

    @Test
    public void scanMulticastInner() {
        CoreSubscriber<Integer> actual = new LambdaSubscriber(null, ( e) -> {
        }, null, null);
        FluxPublishMulticast.FluxPublishMulticaster<Integer> parent = new FluxPublishMulticast.FluxPublishMulticaster<>(123, Queues.<Integer>unbounded(), Context.empty());
        FluxPublishMulticast.PublishMulticastInner<Integer> test = new FluxPublishMulticast.PublishMulticastInner<>(parent, actual);
        assertThat(test.scan(PARENT)).isSameAs(parent);
        assertThat(test.scan(ACTUAL)).isSameAs(actual);
        test.request(789);
        assertThat(test.scan(REQUESTED_FROM_DOWNSTREAM)).isEqualTo(789);
        assertThat(test.scan(CANCELLED)).isFalse();
        test.cancel();
        assertThat(test.scan(CANCELLED)).isTrue();
    }

    @Test
    public void scanCancelMulticaster() {
        CoreSubscriber<Integer> actual = new LambdaSubscriber(null, ( e) -> {
        }, null, null);
        FluxPublishMulticast.FluxPublishMulticaster<Integer> parent = new FluxPublishMulticast.FluxPublishMulticaster<>(123, Queues.<Integer>unbounded(), Context.empty());
        FluxPublishMulticast.CancelMulticaster<Integer> test = new FluxPublishMulticast.CancelMulticaster<>(actual, parent);
        Subscription sub = Operators.emptySubscription();
        test.onSubscribe(sub);
        assertThat(test.scan(PARENT)).isSameAs(sub);
        assertThat(test.scan(ACTUAL)).isSameAs(actual);
    }

    @Test
    public void scanCancelFuseableMulticaster() {
        CoreSubscriber<Integer> actual = new LambdaSubscriber(null, ( e) -> {
        }, null, null);
        FluxPublishMulticast.FluxPublishMulticaster<Integer> parent = new FluxPublishMulticast.FluxPublishMulticaster<>(123, Queues.<Integer>unbounded(), Context.empty());
        FluxPublishMulticast.CancelFuseableMulticaster<Integer> test = new FluxPublishMulticast.CancelFuseableMulticaster<>(actual, parent);
        Subscription sub = Operators.emptySubscription();
        test.onSubscribe(sub);
        assertThat(test.scan(PARENT)).isSameAs(sub);
        assertThat(test.scan(ACTUAL)).isSameAs(actual);
    }

    @Test
    public void gh870() throws Exception {
        CountDownLatch cancelled = new CountDownLatch(1);
        StepVerifier.create(// .doOnCancel(() -> System.out.println("cancel 1"))
        // .doOnCancel(() -> System.out.println("cancel 2"))
        Flux.<Integer>create(( sink) -> {
            int i = 0;
            sink.onCancel(cancelled::countDown);
            try {
                while (true) {
                    sink.next((i++));
                    Thread.sleep(1);
                    if (sink.isCancelled()) {
                        break;
                    }
                } 
            } catch ( e) {
                sink.error(e);
            }
        }).publish(Function.identity()).take(5)).expectNextCount(5).verifyComplete();
        if (!(cancelled.await(5, TimeUnit.SECONDS))) {
            fail("Flux.create() did not receive cancellation signal");
        }
    }
}

