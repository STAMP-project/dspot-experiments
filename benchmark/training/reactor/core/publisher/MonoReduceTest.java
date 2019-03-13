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


import Scannable.Attr.ACTUAL;
import Scannable.Attr.CANCELLED;
import Scannable.Attr.PARENT;
import Scannable.Attr.PREFETCH;
import Scannable.Attr.TERMINATED;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import org.junit.Assert;
import org.junit.Test;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.CoreSubscriber;
import reactor.core.scheduler.Schedulers;
import reactor.test.publisher.ReduceOperatorTest;
import reactor.test.subscriber.AssertSubscriber;
import reactor.test.util.RaceTestUtils;
import reactor.util.context.Context;


public class MonoReduceTest extends ReduceOperatorTest<String, String> {
    private static final Logger LOG = LoggerFactory.getLogger(MonoReduceTest.class);

    /* @Test
    public void constructors() {
    ConstructorTestBuilder ctb = new ConstructorTestBuilder(MonoReduce.class);

    ctb.addRef("source", Mono.never());
    ctb.addRef("aggregator", (BiFunction<Object, Object, Object>) (a, b) -> b);

    ctb.test();
    }
     */
    @Test
    public void normal() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        Flux.range(1, 10).reduce(( a, b) -> a + b).subscribe(ts);
        ts.assertValues(55).assertNoError().assertComplete();
    }

    @Test
    public void normalBackpressured() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create(0L);
        Flux.range(1, 10).reduce(( a, b) -> a + b).subscribe(ts);
        ts.assertNoValues().assertNoError().assertNotComplete();
        ts.request(1);
        ts.assertValues(55).assertNoError().assertComplete();
    }

    @Test
    public void single() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        Flux.just(1).reduce(( a, b) -> a + b).subscribe(ts);
        ts.assertValues(1).assertNoError().assertComplete();
    }

    @Test
    public void empty() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        Flux.<Integer>empty().reduce(( a, b) -> a + b).subscribe(ts);
        ts.assertNoValues().assertNoError().assertComplete();
    }

    @Test
    public void error() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        Flux.<Integer>error(new RuntimeException("forced failure")).reduce(( a, b) -> a + b).subscribe(ts);
        ts.assertNoValues().assertError(RuntimeException.class).assertErrorWith(( e) -> Assert.assertTrue(e.getMessage().contains("forced failure"))).assertNotComplete();
    }

    @Test
    public void aggregatorThrows() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        Flux.range(1, 10).reduce(( a, b) -> {
            throw new RuntimeException("forced failure");
        }).subscribe(ts);
        ts.assertNoValues().assertError(RuntimeException.class).assertErrorWith(( e) -> Assert.assertTrue(e.getMessage().contains("forced failure"))).assertNotComplete();
    }

    @Test
    public void aggregatorReturnsNull() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        Flux.range(1, 10).reduce(( a, b) -> null).subscribe(ts);
        ts.assertNoValues().assertError(NullPointerException.class).assertNotComplete();
    }

    /* see issue #230 */
    @Test
    public void should_reduce_to_10_events() {
        AtomicInteger count = new AtomicInteger();
        AtomicInteger countNulls = new AtomicInteger();
        Flux.range(0, 10).flatMap(( x) -> // .log("reduced."+x)
        Flux.range(0, 2).map(( y) -> blockingOp(x, y)).subscribeOn(Schedulers.elastic()).reduce(( l, r) -> (l + "_") + r).doOnSuccess(( s) -> {
            if (s == null)
                countNulls.incrementAndGet();
            else
                count.incrementAndGet();

            LOG.info("Completed with {}", s);
        })).blockLast();
        Assert.assertEquals(10, count.get());
        Assert.assertEquals(0, countNulls.get());
    }

    @Test
    public void onNextAndCancelRace() {
        final AssertSubscriber<Integer> testSubscriber = AssertSubscriber.create();
        MonoReduce.ReduceSubscriber<Integer> sub = new MonoReduce.ReduceSubscriber<>(testSubscriber, ( current, next) -> current + next);
        sub.onSubscribe(Operators.emptySubscription());
        // the race alone _could_ previously produce a NPE
        RaceTestUtils.race(() -> sub.onNext(1), sub::cancel);
        testSubscriber.assertNoError();
        // to be even more sure, we try an onNext AFTER the cancel
        sub.onNext(2);
        testSubscriber.assertNoError();
    }

    @Test
    public void discardAccumulatedOnCancel() {
        final List<Object> discarded = new ArrayList<>();
        final AssertSubscriber<Object> testSubscriber = new AssertSubscriber(Operators.enableOnDiscard(Context.empty(), discarded::add));
        MonoReduce.ReduceSubscriber<Integer> sub = new MonoReduce.ReduceSubscriber<>(testSubscriber, ( current, next) -> current + next);
        sub.onSubscribe(Operators.emptySubscription());
        sub.onNext(1);
        assertThat(sub.value).isEqualTo(1);
        sub.cancel();
        testSubscriber.assertNoError();
        assertThat(discarded).containsExactly(1);
    }

    @Test
    public void discardOnError() {
        final List<Object> discarded = new ArrayList<>();
        final AssertSubscriber<Object> testSubscriber = new AssertSubscriber(Operators.enableOnDiscard(Context.empty(), discarded::add));
        MonoReduce.ReduceSubscriber<Integer> sub = new MonoReduce.ReduceSubscriber<>(testSubscriber, ( current, next) -> current + next);
        sub.onSubscribe(Operators.emptySubscription());
        sub.onNext(1);
        assertThat(sub.value).isEqualTo(1);
        sub.onError(new RuntimeException("boom"));
        testSubscriber.assertErrorMessage("boom");
        assertThat(discarded).containsExactly(1);
    }

    @Test
    public void noRetainValueOnComplete() {
        final AssertSubscriber<Object> testSubscriber = AssertSubscriber.create();
        MonoReduce.ReduceSubscriber<Integer> sub = new MonoReduce.ReduceSubscriber<>(testSubscriber, ( current, next) -> current + next);
        sub.onSubscribe(Operators.emptySubscription());
        sub.onNext(1);
        sub.onNext(2);
        assertThat(sub.value).isEqualTo(3);
        sub.request(1);
        sub.onComplete();
        assertThat(sub.value).isNull();
        testSubscriber.assertNoError();
    }

    @Test
    public void noRetainValueOnError() {
        final AssertSubscriber<Object> testSubscriber = AssertSubscriber.create();
        MonoReduce.ReduceSubscriber<Integer> sub = new MonoReduce.ReduceSubscriber<>(testSubscriber, ( current, next) -> current + next);
        sub.onSubscribe(Operators.emptySubscription());
        sub.onNext(1);
        sub.onNext(2);
        assertThat(sub.value).isEqualTo(3);
        sub.onError(new RuntimeException("boom"));
        assertThat(sub.value).isNull();
        testSubscriber.assertErrorMessage("boom");
    }

    @Test
    public void scanSubscriber() {
        CoreSubscriber<String> actual = new LambdaMonoSubscriber(null, ( e) -> {
        }, null, null);
        MonoReduce.ReduceSubscriber<String> test = new MonoReduce.ReduceSubscriber<>(actual, ( s1, s2) -> s1 + s2);
        Subscription parent = Operators.emptySubscription();
        test.onSubscribe(parent);
        assertThat(test.scan(PREFETCH)).isEqualTo(Integer.MAX_VALUE);
        assertThat(test.scan(PARENT)).isSameAs(parent);
        assertThat(test.scan(ACTUAL)).isSameAs(actual);
        assertThat(test.scan(TERMINATED)).isFalse();
        test.onError(new IllegalStateException("boom"));
        assertThat(test.scan(TERMINATED)).isTrue();
        assertThat(test.scan(CANCELLED)).isFalse();
        test.cancel();
        assertThat(test.scan(CANCELLED)).isTrue();
    }
}

