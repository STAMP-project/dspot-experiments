/**
 * Copyright (c) 2011-Present Pivotal Software Inc, All Rights Reserved.
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


import MonoCollect.CollectSubscriber;
import Scannable.Attr.ACTUAL;
import Scannable.Attr.CANCELLED;
import Scannable.Attr.PARENT;
import Scannable.Attr.PREFETCH;
import Scannable.Attr.TERMINATED;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import org.junit.Assert;
import org.junit.Test;
import org.reactivestreams.Subscription;
import reactor.core.CoreSubscriber;
import reactor.test.StepVerifier;
import reactor.test.subscriber.AssertSubscriber;


public class MonoCollectTest {
    @Test(expected = NullPointerException.class)
    public void nullSource() {
        new MonoCollect(null, () -> 1, ( a, b) -> {
        });
    }

    @Test(expected = NullPointerException.class)
    public void nullSupplier() {
        Flux.never().collect(null, ( a, b) -> {
        });
    }

    @Test(expected = NullPointerException.class)
    public void nullAction() {
        Flux.never().collect(() -> 1, null);
    }

    @Test
    public void normal() {
        AssertSubscriber<ArrayList<Integer>> ts = AssertSubscriber.create();
        Flux.range(1, 10).collect(ArrayList<Integer>::new, ( a, b) -> a.add(b)).subscribe(ts);
        ts.assertValues(new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))).assertNoError().assertComplete();
    }

    @Test
    public void normalBackpressured() {
        AssertSubscriber<ArrayList<Integer>> ts = AssertSubscriber.create(0);
        Flux.range(1, 10).collect(ArrayList<Integer>::new, ArrayList::add).subscribe(ts);
        ts.assertNoValues().assertNoError().assertNotComplete();
        ts.request(2);
        ts.assertValues(new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))).assertNoError().assertComplete();
    }

    @Test
    public void supplierThrows() {
        AssertSubscriber<Object> ts = AssertSubscriber.create();
        Flux.range(1, 10).collect(() -> {
            throw new RuntimeException("forced failure");
        }, ( a, b) -> {
        }).subscribe(ts);
        ts.assertNoValues().assertError(RuntimeException.class).assertErrorWith(( e) -> Assert.assertTrue(e.getMessage().contains("forced failure"))).assertNotComplete();
    }

    @Test
    public void supplierReturnsNull() {
        AssertSubscriber<Object> ts = AssertSubscriber.create();
        Flux.range(1, 10).collect(() -> null, ( a, b) -> {
        }).subscribe(ts);
        ts.assertNoValues().assertError(NullPointerException.class).assertNotComplete();
    }

    @Test
    public void actionThrows() {
        AssertSubscriber<Object> ts = AssertSubscriber.create();
        Flux.range(1, 10).collect(() -> 1, ( a, b) -> {
            throw new RuntimeException("forced failure");
        }).subscribe(ts);
        ts.assertNoValues().assertError(RuntimeException.class).assertErrorWith(( e) -> Assert.assertTrue(e.getMessage().contains("forced failure"))).assertNotComplete();
    }

    @Test
    public void scanSubscriber() {
        CoreSubscriber<List<String>> actual = new LambdaMonoSubscriber(null, ( e) -> {
        }, null, null);
        CollectSubscriber<String, List<String>> test = new MonoCollect.CollectSubscriber<>(actual, ( l, v) -> l.add(v), new ArrayList<>());
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

    @Test
    public void discardOnError() {
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        AtomicBoolean res = new AtomicBoolean();
        Mono<List<Integer>> test = Flux.range(1, 10).hide().map(( i) -> {
            if (i == 5) {
                throw new IllegalStateException("boom");
            }
            return i;
        }).<List<Integer>>collect(ArrayList::new, List::add).doOnDiscard(List.class, ( l) -> {
            l.removeAll(list);
            res.set(l.isEmpty());
        });
        StepVerifier.create(test).expectErrorMessage("boom").verify();
        Assert.assertTrue("all discarded", res.get());
    }

    @Test
    public void discardOnCancel() {
        List<Long> list = new ArrayList<>();
        list.add(0L);
        list.add(1L);
        AtomicBoolean res = new AtomicBoolean();
        StepVerifier.withVirtualTime(() -> Flux.interval(Duration.ofMillis(100)).take(10).<List<Long>>collect(ArrayList::new, List::add).doOnDiscard(.class, ( l) -> {
            l.removeAll(list);
            res.set(l.isEmpty());
        })).expectSubscription().expectNoEvent(Duration.ofMillis(210)).thenCancel().verify();
        Assert.assertTrue("all discarded", res.get());
    }
}

