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


import Attr.PREFETCH;
import Scannable.Attr.CANCELLED;
import Scannable.Attr.ERROR;
import Scannable.Attr.PARENT;
import Scannable.Attr.TERMINATED;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;
import org.reactivestreams.Subscription;
import reactor.test.StepVerifier;
import reactor.util.concurrent.Queues;


public class BlockingIterableTest {
    @Test(timeout = 5000)
    public void normal() {
        List<Integer> values = new ArrayList<>();
        for (Integer i : Flux.range(1, 10).toIterable()) {
            values.add(i);
        }
        Assert.assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), values);
    }

    @Test(timeout = 5000)
    public void normal2() {
        Queue<Integer> q = new ArrayBlockingQueue<>(1);
        List<Integer> values = new ArrayList<>();
        for (Integer i : Flux.range(1, 10).toIterable(1, () -> q)) {
            values.add(i);
        }
        Assert.assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), values);
    }

    @Test(timeout = 5000)
    public void empty() {
        List<Integer> values = new ArrayList<>();
        for (Integer i : FluxEmpty.<Integer>instance().toIterable()) {
            values.add(i);
        }
        Assert.assertEquals(Collections.emptyList(), values);
    }

    @Test(timeout = 5000, expected = RuntimeException.class)
    public void error() {
        List<Integer> values = new ArrayList<>();
        for (Integer i : Flux.<Integer>error(new RuntimeException("forced failure")).toIterable()) {
            values.add(i);
        }
        Assert.assertEquals(Collections.emptyList(), values);
    }

    @Test(timeout = 5000)
    public void toStream() {
        List<Integer> values = new ArrayList<>();
        Flux.range(1, 10).toStream().forEach(values::add);
        Assert.assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), values);
    }

    @Test(timeout = 5000)
    public void streamEmpty() {
        List<Integer> values = new ArrayList<>();
        FluxEmpty.<Integer>instance().toStream().forEach(values::add);
        Assert.assertEquals(Collections.emptyList(), values);
    }

    @Test(timeout = 5000)
    public void streamLimit() {
        List<Integer> values = new ArrayList<>();
        Flux.range(1, Integer.MAX_VALUE).toStream().limit(10).forEach(values::add);
        Assert.assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), values);
    }

    @Test(timeout = 5000)
    public void streamParallel() {
        int n = 1000000;
        Optional<Integer> opt = Flux.range(1, n).toStream().parallel().max(Integer::compare);
        Assert.assertTrue("No maximum?", opt.isPresent());
        Assert.assertEquals(((Integer) (n)), opt.get());
    }

    @Test
    public void scanOperator() {
        Flux<Integer> source = Flux.range(1, 10);
        BlockingIterable<Integer> test = new BlockingIterable(source, 35, Queues.one());
        assertThat(test.scanUnsafe(PARENT)).describedAs("PARENT").isSameAs(source);
        // type safe attributes
        assertThat(test.scanUnsafe(PREFETCH)).describedAs("PREFETCH unsafe").isEqualTo(35);
        assertThat(test.scan(PREFETCH)).describedAs("PREFETCH").isEqualTo(35);// FIXME

    }

    @Test
    public void scanOperatorLargePrefetchIsLimitedToIntMax() {
        Flux<Integer> source = Flux.range(1, 10);
        BlockingIterable<Integer> test = new BlockingIterable(source, Integer.MAX_VALUE, Queues.one());
        assertThat(test.scan(PREFETCH)).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    public void scanSubscriber() {
        BlockingIterable.SubscriberIterator<String> subscriberIterator = new BlockingIterable.SubscriberIterator<>(Queues.<String>one().get(), 123);
        Subscription s = Operators.emptySubscription();
        subscriberIterator.onSubscribe(s);
        assertThat(subscriberIterator.scan(PARENT)).describedAs("PARENT").isSameAs(s);
        assertThat(subscriberIterator.scan(TERMINATED)).describedAs("TERMINATED").isFalse();
        assertThat(subscriberIterator.scan(CANCELLED)).describedAs("CANCELLED").isFalse();
        assertThat(subscriberIterator.scan(ERROR)).describedAs("ERROR").isNull();
        assertThat(subscriberIterator.scan(PREFETCH)).describedAs("PREFETCH").isEqualTo(123);// FIXME

    }

    @Test
    public void scanSubscriberLargePrefetchIsLimitedToIntMax() {
        BlockingIterable.SubscriberIterator<String> subscriberIterator = new BlockingIterable.SubscriberIterator<>(Queues.<String>one().get(), Integer.MAX_VALUE);
        assertThat(subscriberIterator.scan(PREFETCH)).isEqualTo(Integer.MAX_VALUE);// FIXME

    }

    @Test
    public void scanSubscriberTerminated() {
        BlockingIterable.SubscriberIterator<String> test = new BlockingIterable.SubscriberIterator<>(Queues.<String>one().get(), 123);
        assertThat(test.scan(TERMINATED)).describedAs("before TERMINATED").isFalse();
        test.onComplete();
        assertThat(test.scan(TERMINATED)).describedAs("after TERMINATED").isTrue();
    }

    @Test
    public void scanSubscriberError() {
        BlockingIterable.SubscriberIterator<String> test = new BlockingIterable.SubscriberIterator<>(Queues.<String>one().get(), 123);
        IllegalStateException error = new IllegalStateException("boom");
        assertThat(test.scan(ERROR)).describedAs("before ERROR").isNull();
        test.onError(error);
        assertThat(test.scan(ERROR)).describedAs("after ERROR").isSameAs(error);
        assertThat(test.scan(TERMINATED)).isTrue();
    }

    @Test
    public void scanSubscriberCancelled() {
        BlockingIterable.SubscriberIterator<String> test = new BlockingIterable.SubscriberIterator<>(Queues.<String>one().get(), 123);
        // simulate cancellation by offering two elements
        test.onNext("a");
        assertThat(test.scan(CANCELLED)).describedAs("before CANCELLED").isFalse();
        test.onNext("b");
        assertThat(test.scan(CANCELLED)).describedAs("after CANCELLED").isTrue();
    }

    @Test(timeout = 1000)
    public void gh841_streamCreate() {
        Flux<String> source = Flux.<String>create(( sink) -> {
            sink.next("a");
            sink.next("b");
            sink.complete();
        }).sort(( a, b) -> {
            throw new IllegalStateException("boom");
        });
        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> source.toStream().collect(Collectors.toSet())).withMessage("boom");
    }

    @Test(timeout = 1000)
    public void gh841_streamCreateDeferredError() {
        Flux<Integer> source = Flux.<Integer>create(( sink) -> {
            sink.next(1);
            sink.next(2);
            sink.next(0);
            sink.complete();
        }).map(( v) -> 4 / v).log();
        assertThatExceptionOfType(ArithmeticException.class).isThrownBy(() -> source.toStream(1).collect(Collectors.toSet())).withMessage("/ by zero");
    }

    @Test(timeout = 1000)
    public void gh841_streamFromIterable() {
        Flux<String> source = Flux.fromIterable(Arrays.asList("a", "b")).sort(( a, b) -> {
            throw new IllegalStateException("boom");
        });
        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> source.toStream().collect(Collectors.toSet())).withMessage("boom");
    }

    @Test(timeout = 1000)
    public void gh841_iteratorFromCreate() {
        Iterator<String> it = Flux.<String>create(( sink) -> {
            sink.next("a");
            sink.next("b");
            sink.complete();
        }).sort(( a, b) -> {
            throw new IllegalStateException("boom");
        }).toIterable().iterator();
        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(it::hasNext).withMessage("boom");
    }

    @Test(timeout = 1000)
    public void gh841_workaroundFlux() {
        Flux<String> source = Flux.<String>create(( sink) -> {
            sink.next("a");
            sink.next("b");
            sink.complete();
        }).collectSortedList(( a, b) -> {
            throw new IllegalStateException("boom");
        }).hide().flatMapIterable(Function.identity());
        StepVerifier.create(source).expectErrorSatisfies(( e) -> assertThat(e).isInstanceOf(.class).hasMessage("boom")).verify();
    }

    @Test(timeout = 1000)
    public void gh841_workaroundStream() {
        Flux<String> source = Flux.<String>create(( sink) -> {
            sink.next("a");
            sink.next("b");
            sink.complete();
        }).collectSortedList(( a, b) -> {
            throw new IllegalStateException("boom");
        }).hide().flatMapIterable(Function.identity());
        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> source.toStream().collect(Collectors.toSet())).withMessage("boom");
    }
}

