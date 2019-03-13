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


import Operators.MonoSubscriber;
import ParallelReduceSeed.ParallelReduceSeedSubscriber;
import Scannable.Attr.ACTUAL;
import Scannable.Attr.CANCELLED;
import Scannable.Attr.PARENT;
import Scannable.Attr.PREFETCH;
import Scannable.Attr.TERMINATED;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import org.junit.Test;
import reactor.core.CoreSubscriber;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;
import reactor.test.publisher.ParallelOperatorTest;
import reactor.test.subscriber.AssertSubscriber;


public class ParallelReduceSeedTest extends ParallelOperatorTest<String, String> {
    @Test
    public void collectAsyncFused() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        Scheduler scheduler = Schedulers.newParallel("test", 3);
        Flux.range(1, 100000).parallel(3).runOn(scheduler).collect(ArrayList::new, ArrayList::add).sequential().reduce(0, ( a, b) -> a + (b.size())).subscribe(ts);
        ts.await(Duration.ofSeconds(5));
        ts.assertValues(100000).assertNoError().assertComplete();
    }

    @Test
    public void collectAsync() {
        Scheduler s = Schedulers.newParallel("test", 3);
        Supplier<List<Integer>> as = () -> new ArrayList<>();
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        Flux.range(1, 100000).hide().parallel(3).runOn(s).collect(as, ( a, b) -> a.add(b)).doOnNext(( v) -> System.out.println(v.size())).sequential().reduce(0, ( a, b) -> a + (b.size())).subscribe(ts);
        ts.await(Duration.ofSeconds(5));
        ts.assertValues(100000).assertNoError().assertComplete();
    }

    @Test
    public void collectAsync2() {
        Scheduler s = Schedulers.newParallel("test", 3);
        Supplier<List<Integer>> as = () -> new ArrayList<>();
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        Flux.range(1, 100000).hide().publishOn(s).parallel(3).runOn(s).hide().collect(as, ( a, b) -> a.add(b)).doOnNext(( v) -> System.out.println(v.size())).sequential().reduce(0, ( a, b) -> a + (b.size())).subscribe(ts);
        ts.await(Duration.ofSeconds(5));
        ts.assertValues(100000).assertNoError().assertComplete();
    }

    @Test
    public void collectAsync3() {
        Scheduler s = Schedulers.newParallel("test", 3);
        Supplier<List<Integer>> as = () -> new ArrayList<>();
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        Flux.range(1, 100000).hide().publishOn(s).parallel(3).runOn(s).filter(( t) -> true).collect(as, ( a, b) -> a.add(b)).doOnNext(( v) -> System.out.println(v.size())).groups().flatMap(( v) -> v).reduce(0, ( a, b) -> (b.size()) + a).subscribe(ts);
        ts.await(Duration.ofSeconds(5));
        ts.assertValues(100000).assertNoError().assertComplete();
    }

    @Test
    public void collectAsync3Fused() {
        Scheduler s = Schedulers.newParallel("test", 3);
        Supplier<List<Integer>> as = () -> new ArrayList<>();
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        Flux.range(1, 100000).publishOn(s).parallel(3).runOn(s).collect(as, ( a, b) -> a.add(b)).doOnNext(( v) -> System.out.println(v.size())).groups().flatMap(( v) -> v).reduce(0, ( a, b) -> (b.size()) + a).subscribe(ts);
        ts.await(Duration.ofSeconds(5));
        ts.assertValues(100000).assertNoError().assertComplete();
    }

    @Test
    public void collectAsync3Take() {
        Scheduler s = Schedulers.newParallel("test", 4);
        Supplier<List<Integer>> as = () -> new ArrayList<>();
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        Flux.range(1, 100000).publishOn(s).parallel(3).runOn(s).collect(as, ( a, b) -> a.add(b)).doOnNext(( v) -> System.out.println(v.size())).groups().flatMap(( v) -> v).reduce(0, ( a, b) -> (b.size()) + a).subscribe(ts);
        ts.await(Duration.ofSeconds(5));
        ts.assertValues(100000).assertNoError().assertComplete();
    }

    @Test
    public void failInitial() {
        Supplier<Integer> as = () -> {
            throw new RuntimeException("test");
        };
        StepVerifier.create(Flux.range(1, 10).parallel(3).reduce(as, ( a, b) -> b + a)).verifyErrorMessage("test");
    }

    @Test
    public void failCombination() {
        StepVerifier.create(Flux.range(1, 10).parallel(3).reduce(() -> 0, ( a, b) -> {
            throw new RuntimeException("test");
        })).verifyErrorMessage("test");
    }

    @Test
    public void testPrefetch() {
        assertThat(Flux.range(1, 10).parallel(3).reduce(() -> 0, ( a, b) -> a + b).getPrefetch()).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    public void parallelism() {
        ParallelFlux<Integer> source = Flux.just(500, 300).parallel(10);
        ParallelReduceSeed<Integer, String> test = new ParallelReduceSeed(source, () -> "", ( s, i) -> s + i);
        assertThat(test.parallelism()).isEqualTo(source.parallelism()).isEqualTo(10);
    }

    @Test
    public void scanOperator() {
        ParallelFlux<Integer> source = Flux.just(500, 300).parallel(10);
        ParallelReduceSeed<Integer, String> test = new ParallelReduceSeed(source, () -> "", ( s, i) -> s + i);
        assertThat(test.scan(PARENT)).isSameAs(source);
        assertThat(test.scan(PREFETCH)).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    public void scanSubscriber() {
        ParallelFlux<Integer> source = Flux.just(500, 300).parallel(10);
        LambdaSubscriber<String> subscriber = new LambdaSubscriber(null, ( e) -> {
        }, null, null);
        ParallelReduceSeedSubscriber<Integer, String> test = new ParallelReduceSeed.ParallelReduceSeedSubscriber<>(subscriber, "", ( s, i) -> s + i);
        @SuppressWarnings("unchecked")
        final CoreSubscriber<Integer>[] testSubscribers = new CoreSubscriber[1];
        testSubscribers[0] = test;
        source.subscribe(testSubscribers);
        assertThat(test.scan(TERMINATED)).isFalse();
        assertThat(test.scan(PREFETCH)).isEqualTo(Integer.MAX_VALUE);
        assertThat(test.scan(ACTUAL)).isSameAs(subscriber);
        test.state = MonoSubscriber.HAS_REQUEST_HAS_VALUE;
        assertThat(test.scan(TERMINATED)).isTrue();
        test.state = MonoSubscriber.HAS_REQUEST_NO_VALUE;
        assertThat(test.scan(TERMINATED)).isFalse();
        test.state = MonoSubscriber.NO_REQUEST_HAS_VALUE;
        assertThat(test.scan(TERMINATED)).isTrue();
        assertThat(test.scan(CANCELLED)).isFalse();
        test.cancel();
        assertThat(test.scan(CANCELLED)).isTrue();
    }
}

