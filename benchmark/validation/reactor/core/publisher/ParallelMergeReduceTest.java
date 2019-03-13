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
import Scannable.Attr.BUFFERED;
import Scannable.Attr.CANCELLED;
import Scannable.Attr.ERROR;
import Scannable.Attr.PARENT;
import Scannable.Attr.PREFETCH;
import Scannable.Attr.TERMINATED;
import java.time.Duration;
import org.junit.Test;
import org.reactivestreams.Subscription;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.ParallelMergeReduce.MergeReduceInner;
import reactor.core.publisher.ParallelMergeReduce.MergeReduceMain;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.test.subscriber.AssertSubscriber;


public class ParallelMergeReduceTest {
    @Test
    public void reduceFull() {
        for (int i = 1; i <= ((Runtime.getRuntime().availableProcessors()) * 2); i++) {
            AssertSubscriber<Integer> ts = AssertSubscriber.create();
            Flux.range(1, 10).parallel(i).reduce(( a, b) -> a + b).subscribe(ts);
            ts.assertValues(55);
        }
    }

    @Test
    public void parallelReduceFull() {
        int m = 100000;
        for (int n = 1; n <= m; n *= 10) {
            // System.out.println(n);
            for (int i = 1; i <= (Runtime.getRuntime().availableProcessors()); i++) {
                // System.out.println("  " + i);
                Scheduler scheduler = Schedulers.newParallel("test", i);
                try {
                    AssertSubscriber<Long> ts = AssertSubscriber.create();
                    Flux.range(1, n).map(( v) -> ((long) (v))).parallel(i).runOn(scheduler).reduce(( a, b) -> a + b).subscribe(ts);
                    ts.await(Duration.ofSeconds(500));
                    long e = (((long) (n)) * (1 + n)) / 2;
                    ts.assertValues(e);
                } finally {
                    scheduler.dispose();
                }
            }
        }
    }

    @Test
    public void scanOperator() {
        ParallelFlux<Integer> source = Flux.range(1, 4).parallel();
        ParallelMergeReduce<Integer> test = new ParallelMergeReduce(source, ( a, b) -> a + b);
        assertThat(test.scan(PARENT)).isSameAs(source);
    }

    @Test
    public void scanMainSubscriber() {
        CoreSubscriber<? super Integer> subscriber = new LambdaSubscriber(null, ( e) -> {
        }, null, ( sub) -> sub.request(2));
        MergeReduceMain<Integer> test = new MergeReduceMain(subscriber, 2, ( a, b) -> a + b);
        subscriber.onSubscribe(test);
        assertThat(test.scan(ACTUAL)).isSameAs(subscriber);
        assertThat(test.scan(PREFETCH)).isEqualTo(Integer.MAX_VALUE);
        assertThat(test.scan(TERMINATED)).isFalse();
        assertThat(test.scan(ERROR)).isNull();
        test.innerComplete(1);
        test.innerComplete(2);
        assertThat(test.scan(TERMINATED)).isTrue();
        assertThat(test.scan(CANCELLED)).isFalse();
        test.cancel();
        assertThat(test.scan(CANCELLED)).isTrue();
    }

    @Test
    public void scanMainSubscriberError() {
        CoreSubscriber<? super Integer> subscriber = new LambdaSubscriber(null, ( e) -> {
        }, null, ( sub) -> sub.request(2));
        MergeReduceMain<Integer> test = new MergeReduceMain(subscriber, 2, ( a, b) -> a + b);
        subscriber.onSubscribe(test);
        assertThat(test.scan(ERROR)).isNull();
        test.innerError(new IllegalStateException("boom"));
        assertThat(test.scan(ERROR)).hasMessage("boom");
    }

    @Test
    public void scanInnerSubscriber() {
        CoreSubscriber<? super Integer> subscriber = new LambdaSubscriber(null, ( e) -> {
        }, null, null);
        MergeReduceMain<Integer> main = new MergeReduceMain(subscriber, 2, ( a, b) -> a + b);
        MergeReduceInner<Integer> test = new MergeReduceInner(main, ( a, b) -> a + b);
        Subscription s = Operators.emptySubscription();
        test.onSubscribe(s);
        assertThat(test.scan(PARENT)).isSameAs(s);
        assertThat(test.scan(ACTUAL)).isSameAs(main);
        assertThat(test.scan(PREFETCH)).isEqualTo(Integer.MAX_VALUE);
        assertThat(test.scan(TERMINATED)).isFalse();
        test.done = true;
        assertThat(test.scan(TERMINATED)).isTrue();
        assertThat(test.scan(BUFFERED)).isZero();
        test.value = 3;
        assertThat(test.scan(BUFFERED)).isEqualTo(1);
        assertThat(test.scan(CANCELLED)).isFalse();
        test.cancel();
        assertThat(test.scan(CANCELLED)).isTrue();
    }
}

