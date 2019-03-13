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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Level;
import org.junit.Test;
import org.reactivestreams.Subscription;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.ParallelCollect.ParallelCollectSubscriber;
import reactor.test.StepVerifier;
import reactor.test.subscriber.AssertSubscriber;


public class ParallelCollectTest {
    @Test
    public void collect() {
        Supplier<List<Integer>> as = () -> new ArrayList<>();
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        Flux.range(1, 10).parallel().collect(as, ( a, b) -> a.add(b)).sequential().flatMapIterable(( v) -> v).log("ParallelCollectTest#collect", Level.FINE).subscribe(ts);
        ts.assertContainValues(new HashSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))).assertNoError().assertComplete();
    }

    @Test
    public void failInitial() {
        Supplier<List<Integer>> as = () -> {
            throw new RuntimeException("test");
        };
        StepVerifier.create(Flux.range(1, 10).parallel(3).collect(as, List::add)).verifyErrorMessage("test");
    }

    @Test
    public void failCombination() {
        StepVerifier.create(Flux.range(1, 10).parallel(3).collect(() -> 0, ( a, b) -> {
            throw new RuntimeException("test");
        })).verifyErrorMessage("test");
    }

    @Test
    public void testPrefetch() {
        assertThat(Flux.range(1, 10).parallel(3).collect(ArrayList::new, List::add).getPrefetch()).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    public void parallelism() {
        ParallelFlux<Integer> source = Flux.range(1, 4).parallel(3);
        ParallelCollect<Integer, List<Integer>> test = new ParallelCollect(source, ArrayList::new, List::add);
        assertThat(test.parallelism()).isEqualTo(3).isEqualTo(source.parallelism());
    }

    @Test
    public void scanOperator() {
        ParallelFlux<Integer> source = Flux.range(1, 4).parallel(3);
        ParallelCollect<Integer, List<Integer>> test = new ParallelCollect(source, ArrayList::new, List::add);
        assertThat(test.scan(PARENT)).isSameAs(source);
        assertThat(test.scan(PREFETCH)).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    public void scanSubscriber() {
        CoreSubscriber<List<Integer>> subscriber = new LambdaSubscriber(null, ( e) -> {
        }, null, null);
        ParallelCollectSubscriber<Integer, List<Integer>> test = new ParallelCollectSubscriber(subscriber, new ArrayList(), List::add);
        Subscription s = Operators.emptySubscription();
        test.onSubscribe(s);
        assertThat(test.scan(ACTUAL)).isSameAs(subscriber);
        assertThat(test.scan(PREFETCH)).isEqualTo(Integer.MAX_VALUE);
        assertThat(test.scan(TERMINATED)).isFalse();
        test.complete(Collections.emptyList());
        assertThat(test.scan(TERMINATED)).isTrue();
        assertThat(test.scan(CANCELLED)).isFalse();
        test.cancel();
        assertThat(test.scan(CANCELLED)).isTrue();
    }
}

