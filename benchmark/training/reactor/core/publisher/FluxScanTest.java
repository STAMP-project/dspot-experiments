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
import Scannable.Attr.PARENT;
import Scannable.Attr.TERMINATED;
import java.util.concurrent.atomic.AtomicBoolean;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.reactivestreams.Subscription;
import reactor.core.CoreSubscriber;
import reactor.test.publisher.FluxOperatorTest;
import reactor.test.subscriber.AssertSubscriber;
import reactor.test.util.RaceTestUtils;


public class FluxScanTest extends FluxOperatorTest<String, String> {
    @Test(expected = NullPointerException.class)
    public void sourceNull() {
        new FluxScan(null, ( a, b) -> a);
    }

    @Test(expected = NullPointerException.class)
    public void accumulatorNull() {
        Flux.never().scan(null);
    }

    @Test
    public void normal() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        Flux.range(1, 10).scan(( a, b) -> b).subscribe(ts);
        ts.assertValues(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).assertComplete().assertNoError();
    }

    @Test
    public void normalBackpressured() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create(0);
        Flux.range(1, 10).scan(( a, b) -> b).subscribe(ts);
        ts.assertNoValues().assertNoError().assertNotComplete();
        ts.request(2);
        ts.assertValues(1, 2).assertNoError().assertNotComplete();
        ts.request(8);
        ts.assertValues(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).assertComplete().assertNoError();
    }

    @Test
    public void accumulatorThrows() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        Flux.range(1, 10).scan(( a, b) -> {
            throw new RuntimeException("forced failure");
        }).subscribe(ts);
        ts.assertValues(1).assertNotComplete().assertError(RuntimeException.class).assertErrorMessage("forced failure");
    }

    @Test
    public void accumulatorReturnsNull() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        Flux.range(1, 10).scan(( a, b) -> null).subscribe(ts);
        ts.assertValues(1).assertNotComplete().assertError(NullPointerException.class);
    }

    @Test
    public void onNextAndCancelRaceDontPassNullToAccumulator() {
        AtomicBoolean accumulatorCheck = new AtomicBoolean(true);
        final AssertSubscriber<Integer> testSubscriber = AssertSubscriber.create();
        FluxScan.ScanSubscriber<Integer> sub = new FluxScan.ScanSubscriber<>(testSubscriber, ( accumulated, next) -> {
            if ((accumulated == null) || (next == null)) {
                accumulatorCheck.set(false);
            }
            return next;
        });
        sub.onSubscribe(Operators.emptySubscription());
        for (int i = 0; i < 1000; i++) {
            RaceTestUtils.race(sub::cancel, () -> sub.onNext(1));
            testSubscriber.assertNoError();
            assertThat(accumulatorCheck).as(("no NPE due to onNext/cancel race in round " + i)).isTrue();
        }
    }

    @Test
    public void noRetainValueOnComplete() {
        final AssertSubscriber<Object> testSubscriber = AssertSubscriber.create();
        FluxScan.ScanSubscriber<Integer> sub = new FluxScan.ScanSubscriber<>(testSubscriber, ( current, next) -> current + next);
        sub.onSubscribe(Operators.emptySubscription());
        sub.onNext(1);
        sub.onNext(2);
        assertThat(sub.value).isEqualTo(3);
        sub.onComplete();
        assertThat(sub.value).isNull();
        testSubscriber.assertNoError();
    }

    @Test
    public void noRetainValueOnError() {
        final AssertSubscriber<Object> testSubscriber = AssertSubscriber.create();
        FluxScan.ScanSubscriber<Integer> sub = new FluxScan.ScanSubscriber<>(testSubscriber, ( current, next) -> current + next);
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
        CoreSubscriber<Integer> actual = new LambdaSubscriber(null, ( e) -> {
        }, null, null);
        FluxScan.ScanSubscriber<Integer> test = new FluxScan.ScanSubscriber<>(actual, ( i, j) -> i + j);
        Subscription parent = Operators.emptySubscription();
        test.onSubscribe(parent);
        Assertions.assertThat(test.scan(PARENT)).isSameAs(parent);
        Assertions.assertThat(test.scan(ACTUAL)).isSameAs(actual);
        test.value = 5;
        Assertions.assertThat(test.scan(BUFFERED)).isEqualTo(1);
        Assertions.assertThat(test.scan(TERMINATED)).isFalse();
        test.onComplete();
        Assertions.assertThat(test.scan(TERMINATED)).isTrue();
    }
}

