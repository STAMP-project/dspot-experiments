/**
 * Copyright (c) 2011-2017 Pivotal Software Inc, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package reactor.core.publisher;


import Scannable.Attr.ACTUAL;
import Scannable.Attr.BUFFERED;
import Scannable.Attr.CANCELLED;
import Scannable.Attr.ERROR;
import Scannable.Attr.PARENT;
import Scannable.Attr.REQUESTED_FROM_DOWNSTREAM;
import Scannable.Attr.TERMINATED;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import org.junit.Test;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.FluxExpand.ExpandBreathSubscriber;
import reactor.core.publisher.FluxExpand.ExpandDepthSubscriber;
import reactor.core.publisher.FluxExpand.ExpandDepthSubscription;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;
import reactor.test.StepVerifierOptions;
import reactor.test.publisher.TestPublisher;
import reactor.test.subscriber.AssertSubscriber;
import reactor.test.util.RaceTestUtils;
import reactor.util.context.Context;


public class FluxExpandTest {
    Function<Integer, Publisher<Integer>> countDown = ( v) -> v == 0 ? Flux.empty() : Flux.just((v - 1));

    @Test
    public void recursiveCountdownDepth() {
        StepVerifier.create(Flux.just(10).expandDeep(countDown)).expectNext(10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0).verifyComplete();
    }

    @Test
    public void recursiveCountdownBreadth() {
        StepVerifier.create(Flux.just(10).expand(countDown)).expectNext(10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0).verifyComplete();
    }

    @Test
    public void error() {
        StepVerifier.create(Flux.<Integer>error(new IllegalStateException("boom")).expand(countDown)).verifyErrorMessage("boom");
    }

    @Test
    public void errorDepth() {
        StepVerifier.create(Flux.<Integer>error(new IllegalStateException("boom")).expandDeep(countDown)).verifyErrorMessage("boom");
    }

    @Test
    public void empty() {
        StepVerifier.create(Flux.<Integer>empty().expand(countDown)).verifyComplete();
    }

    @Test
    public void emptyDepth() {
        StepVerifier.create(Flux.<Integer>empty().expandDeep(countDown)).verifyComplete();
    }

    @Test
    public void recursiveCountdownLoop() {
        for (int i = 0; i < 1000; i = (i < 100) ? i + 1 : i + 50) {
            String tag = ("i = " + i) + ", strategy = breadth";
            List<Integer> list = new ArrayList<>();
            StepVerifier.create(Flux.just(i).expand(countDown)).expectSubscription().recordWith(() -> list).expectNextCount((i + 1)).as(tag).verifyComplete();
            for (int j = 0; j <= i; j++) {
                assertThat(list.get(j).intValue()).as(((tag + ", ") + list)).isEqualTo((i - j));
            }
        }
    }

    @Test
    public void recursiveCountdownLoopDepth() {
        for (int i = 0; i < 1000; i = (i < 100) ? i + 1 : i + 50) {
            String tag = ("i = " + i) + ", strategy = depth";
            List<Integer> list = new ArrayList<>();
            StepVerifier.create(Flux.just(i).expand(countDown)).expectSubscription().recordWith(() -> list).expectNextCount((i + 1)).as(tag).verifyComplete();
            for (int j = 0; j <= i; j++) {
                assertThat(list.get(j).intValue()).as(((tag + ", ") + list)).isEqualTo((i - j));
            }
        }
    }

    @Test
    public void recursiveCountdownTake() {
        StepVerifier.create(Flux.just(10).expand(countDown).take(5)).expectNext(10, 9, 8, 7, 6).verifyComplete();
    }

    @Test
    public void recursiveCountdownTakeDepth() {
        StepVerifier.create(Flux.just(10).expandDeep(countDown).take(5)).expectNext(10, 9, 8, 7, 6).verifyComplete();
    }

    @Test
    public void recursiveCountdownBackpressure() {
        StepVerifier.create(Flux.just(10).expand(countDown), StepVerifierOptions.create().initialRequest(0).checkUnderRequesting(false)).thenRequest(1).expectNext(10).thenRequest(3).expectNext(9, 8, 7).thenRequest(4).expectNext(6, 5, 4, 3).thenRequest(3).expectNext(2, 1, 0).verifyComplete();
    }

    @Test
    public void recursiveCountdownBackpressureDepth() {
        StepVerifier.create(Flux.just(10).expandDeep(countDown), StepVerifierOptions.create().initialRequest(0).checkUnderRequesting(false)).thenRequest(1).expectNext(10).thenRequest(3).expectNext(9, 8, 7).thenRequest(4).expectNext(6, 5, 4, 3).thenRequest(3).expectNext(2, 1, 0).verifyComplete();
    }

    @Test
    public void expanderThrows() {
        StepVerifier.create(Flux.just(10).expand(( v) -> {
            throw new IllegalStateException("boom");
        })).expectNext(10).verifyErrorMessage("boom");
    }

    @Test
    public void expanderThrowsDepth() {
        StepVerifier.create(Flux.just(10).expandDeep(( v) -> {
            throw new IllegalStateException("boom");
        })).expectNext(10).verifyErrorMessage("boom");
    }

    @Test
    public void expanderReturnsNull() {
        StepVerifier.create(Flux.just(10).expand(( v) -> null)).expectNext(10).verifyError(NullPointerException.class);
    }

    @Test
    public void expanderReturnsNullDepth() {
        StepVerifier.create(Flux.just(10).expandDeep(( v) -> null)).expectNext(10).verifyError(NullPointerException.class);
    }

    static final class Node {
        final String name;

        final List<FluxExpandTest.Node> children;

        Node(String name, FluxExpandTest.Node... nodes) {
            this.name = name;
            this.children = new ArrayList<>();
            children.addAll(Arrays.asList(nodes));
        }

        @Override
        public String toString() {
            return name;
        }
    }

    @Test(timeout = 5000)
    public void depthFirst() {
        FluxExpandTest.Node root = createTest();
        StepVerifier.create(Flux.just(root).expandDeep(( v) -> Flux.fromIterable(v.children)).map(( v) -> v.name)).expectNext("root", "1", "11", "2", "21", "22", "221", "3", "31", "32", "321", "33", "331", "332", "3321", "4", "41", "42", "421", "43", "431", "432", "4321", "44", "441", "442", "4421", "443", "4431", "4432").verifyComplete();
    }

    @Test
    public void depthFirstAsync() {
        FluxExpandTest.Node root = createTest();
        StepVerifier.create(Flux.just(root).expandDeep(( v) -> Flux.fromIterable(v.children).subscribeOn(Schedulers.elastic())).map(( v) -> v.name)).expectNext("root", "1", "11", "2", "21", "22", "221", "3", "31", "32", "321", "33", "331", "332", "3321", "4", "41", "42", "421", "43", "431", "432", "4321", "44", "441", "442", "4421", "443", "4431", "4432").expectComplete().verify(Duration.ofSeconds(5));
    }

    @Test(timeout = 5000)
    public void breadthFirst() {
        FluxExpandTest.Node root = createTest();
        StepVerifier.create(Flux.just(root).expand(( v) -> Flux.fromIterable(v.children)).map(( v) -> v.name)).expectNext("root", "1", "2", "3", "4", "11", "21", "22", "31", "32", "33", "41", "42", "43", "44", "221", "321", "331", "332", "421", "431", "432", "441", "442", "443", "3321", "4321", "4421", "4431", "4432").verifyComplete();
    }

    @Test
    public void breadthFirstAsync() {
        FluxExpandTest.Node root = createTest();
        StepVerifier.create(Flux.just(root).expand(( v) -> Flux.fromIterable(v.children).subscribeOn(Schedulers.elastic())).map(( v) -> v.name)).expectNext("root", "1", "2", "3", "4", "11", "21", "22", "31", "32", "33", "41", "42", "43", "44", "221", "321", "331", "332", "421", "431", "432", "441", "442", "443", "3321", "4321", "4421", "4431", "4432").expectComplete().verify(Duration.ofSeconds(5));
    }

    @Test
    public void depthFirstCancel() {
        final TestPublisher<Integer> pp = TestPublisher.create();
        final AssertSubscriber<Integer> ts = AssertSubscriber.create();
        CoreSubscriber<Integer> s = new CoreSubscriber<Integer>() {
            Subscription upstream;

            @Override
            public void onSubscribe(Subscription s) {
                upstream = s;
                ts.onSubscribe(s);
            }

            @Override
            public void onNext(Integer t) {
                ts.onNext(t);
                upstream.cancel();
                upstream.request(1);
                onComplete();
            }

            @Override
            public void onError(Throwable t) {
                ts.onError(t);
            }

            @Override
            public void onComplete() {
                ts.onComplete();
            }
        };
        Flux.just(1).expandDeep(( it) -> pp).subscribe(s);
        pp.assertNoSubscribers();
        ts.assertValues(1);
    }

    @Test
    public void depthCancelRace() {
        for (int i = 0; i < 1000; i++) {
            final AssertSubscriber<Integer> ts = AssertSubscriber.create(0);
            Flux.just(0).expandDeep(countDown).subscribe(ts);
            Runnable r1 = () -> ts.request(1);
            Runnable r2 = ts::cancel;
            RaceTestUtils.race(r1, r2, Schedulers.single());
        }
    }

    @Test
    public void depthEmitCancelRace() {
        for (int i = 0; i < 1000; i++) {
            final TestPublisher<Integer> pp = TestPublisher.create();
            final AssertSubscriber<Integer> ts = AssertSubscriber.create(1);
            Flux.just(0).expandDeep(( it) -> pp).subscribe(ts);
            Runnable r1 = () -> pp.next(1);
            Runnable r2 = ts::cancel;
            RaceTestUtils.race(r1, r2, Schedulers.single());
        }
    }

    @Test
    public void depthCompleteCancelRace() {
        for (int i = 0; i < 1000; i++) {
            final TestPublisher<Integer> pp = TestPublisher.create();
            final AssertSubscriber<Integer> ts = AssertSubscriber.create(1);
            Flux.just(0).expandDeep(( it) -> pp).subscribe(ts);
            Runnable r1 = pp::complete;
            Runnable r2 = ts::cancel;
            RaceTestUtils.race(r1, r2, Schedulers.single());
        }
    }

    @Test
    public void depthCancelRace2() throws Exception {
        for (int i = 0; i < 1000; i++) {
            final TestPublisher<Integer> pp = TestPublisher.create();
            Flux<Integer> source = Flux.just(0).expandDeep(( it) -> pp);
            final CountDownLatch cdl = new CountDownLatch(1);
            AssertSubscriber<Integer> ts = new AssertSubscriber<Integer>() {
                final AtomicInteger sync = new AtomicInteger(2);

                @Override
                public void onNext(Integer t) {
                    super.onNext(t);
                    Schedulers.single().schedule(() -> {
                        if ((sync.decrementAndGet()) != 0) {
                            while ((sync.get()) != 0) {
                            } 
                        }
                        cancel();
                        cdl.countDown();
                    });
                    if ((sync.decrementAndGet()) != 0) {
                        while ((sync.get()) != 0) {
                        } 
                    }
                }
            };
            source.subscribe(ts);
            assertThat(cdl.await(5, TimeUnit.SECONDS)).as("runs under 5s").isTrue();
        }
    }

    static final FluxExpandTest.Node ROOT_A = new FluxExpandTest.Node("A", new FluxExpandTest.Node("AA", new FluxExpandTest.Node("aa1")));

    static final FluxExpandTest.Node ROOT_B = new FluxExpandTest.Node("B", new FluxExpandTest.Node("BB", new FluxExpandTest.Node("bb1")));

    @Test
    public void javadocExampleBreadthFirst() {
        List<String> breadthFirstExpected = Arrays.asList("A", "B", "AA", "BB", "aa1", "bb1");
        StepVerifier.create(Flux.just(FluxExpandTest.ROOT_A, FluxExpandTest.ROOT_B).expand(( v) -> Flux.fromIterable(v.children)).map(( n) -> n.name)).expectNextSequence(breadthFirstExpected).verifyComplete();
    }

    @Test
    public void javadocExampleDepthFirst() {
        List<String> depthFirstExpected = Arrays.asList("A", "AA", "aa1", "B", "BB", "bb1");
        StepVerifier.create(Flux.just(FluxExpandTest.ROOT_A, FluxExpandTest.ROOT_B).expandDeep(( v) -> Flux.fromIterable(v.children)).map(( n) -> n.name)).expectNextSequence(depthFirstExpected).verifyComplete();
    }

    @Test
    public void scanExpandBreathSubscriber() {
        CoreSubscriber<Integer> actual = new LambdaSubscriber(null, Throwable::printStackTrace, null, null);
        ExpandBreathSubscriber<Integer> test = new ExpandBreathSubscriber(actual, ( i) -> i > 5 ? Mono.empty() : Mono.just((i + 1)), 123);
        Subscription s = Operators.emptySubscription();
        test.onSubscribe(s);
        assertThat(test.scan(PARENT)).isEqualTo(s);
        assertThat(test.scan(ACTUAL)).isEqualTo(actual);
        test.request(3);
        assertThat(test.scan(REQUESTED_FROM_DOWNSTREAM)).isEqualTo(3);
        assertThat(test.scan(BUFFERED)).isEqualTo(0);
        test.onNext(1);
        assertThat(test.scan(BUFFERED)).isEqualTo(1);
        assertThat(test.scan(CANCELLED)).isFalse();
        test.cancel();
        assertThat(test.scan(CANCELLED)).isTrue();
    }

    @Test
    public void scanExpandDepthSubscriber() {
        CoreSubscriber<Integer> parentActual = new LambdaSubscriber(null, Throwable::printStackTrace, null, null);
        ExpandDepthSubscription<Integer> eds = new ExpandDepthSubscription(parentActual, ( i) -> i > 5 ? Mono.empty() : Mono.just((i + 1)), 123);
        ExpandDepthSubscriber<Integer> test = new ExpandDepthSubscriber(eds);
        Subscription s = Operators.emptySubscription();
        test.onSubscribe(s);
        assertThat(test.scan(PARENT)).isSameAs(s);
        assertThat(test.scan(ACTUAL)).isSameAs(parentActual);
        assertThat(test.scan(TERMINATED)).isFalse();
        test.onComplete();
        assertThat(test.scan(TERMINATED)).isTrue();
    }

    @Test
    public void scanExpandDepthSubscriberError() {
        CoreSubscriber<Integer> parentActual = new LambdaSubscriber(null, Throwable::printStackTrace, null, null);
        ExpandDepthSubscription<Integer> eds = new ExpandDepthSubscription(parentActual, ( i) -> i > 5 ? Mono.empty() : Mono.just((i + 1)), 123);
        ExpandDepthSubscriber<Integer> test = new ExpandDepthSubscriber(eds);
        assertThat(test.scan(TERMINATED)).isFalse();
        test.onError(new IllegalStateException("boom"));
        assertThat(test.scan(TERMINATED)).isTrue();
    }

    @Test
    public void currentContextForExpandDepthSubscriber() {
        final Context context = Context.of("foo", "bar");
        CoreSubscriber<Integer> parentActual = new BaseSubscriber<Integer>() {
            @Override
            public Context currentContext() {
                return context;
            }
        };
        ExpandDepthSubscription<Integer> expandDepthSubscription = new ExpandDepthSubscription(parentActual, ( i) -> i > 5 ? Mono.empty() : Mono.just((i + 1)), 123);
        ExpandDepthSubscriber<Integer> test = new ExpandDepthSubscriber(expandDepthSubscription);
        assertThat(test.currentContext()).isSameAs(context);
    }

    @Test
    public void scanExpandDepthSubscription() {
        CoreSubscriber<Integer> parentActual = new LambdaSubscriber(null, Throwable::printStackTrace, null, null);
        ExpandDepthSubscription<Integer> test = new ExpandDepthSubscription(parentActual, ( i) -> i > 5 ? Mono.empty() : Mono.just((i + 1)), 123);
        assertThat(test.scan(ACTUAL)).isSameAs(parentActual);
        assertThat(test.scan(ERROR)).isNull();
        test.error = new IllegalStateException("boom");
        assertThat(test.scan(ERROR)).isSameAs(test.error);
        test.request(20);
        assertThat(test.scan(REQUESTED_FROM_DOWNSTREAM)).isEqualTo(20);
        assertThat(test.scan(CANCELLED)).isFalse();
        test.cancel();
        assertThat(test.scan(CANCELLED)).isTrue();
    }
}

