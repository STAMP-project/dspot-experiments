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
import Scannable.Attr.CAPACITY;
import Scannable.Attr.ERROR;
import Scannable.Attr.LARGE_BUFFERED;
import Scannable.Attr.PARENT;
import Scannable.Attr.REQUESTED_FROM_DOWNSTREAM;
import Scannable.Attr.TERMINATED;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.mockito.Mockito;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;
import reactor.core.CoreSubscriber;
import reactor.test.publisher.FluxOperatorTest;
import reactor.test.subscriber.AssertSubscriber;
import reactor.util.concurrent.Queues;


public class FluxWindowTest extends FluxOperatorTest<String, Flux<String>> {
    // javac can't handle these inline and fails with type inference error
    final Supplier<Queue<Integer>> pqs = ConcurrentLinkedQueue::new;

    final Supplier<Queue<UnicastProcessor<Integer>>> oqs = ConcurrentLinkedQueue::new;

    @Test(expected = NullPointerException.class)
    public void source1Null() {
        new FluxWindow(null, 1, pqs);
    }

    @Test(expected = NullPointerException.class)
    public void source2Null() {
        new FluxWindow(null, 1, 2, pqs, oqs);
    }

    @Test(expected = NullPointerException.class)
    public void processorQueue1Null() {
        new FluxWindow(Flux.never(), 1, null);
    }

    @Test(expected = NullPointerException.class)
    public void processorQueue2Null() {
        new FluxWindow(Flux.never(), 1, 1, null, oqs);
    }

    @Test(expected = NullPointerException.class)
    public void overflowQueueNull() {
        new FluxWindow(Flux.never(), 1, 1, pqs, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void size1Invalid() {
        Flux.never().window(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void size2Invalid() {
        Flux.never().window(0, 2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void skipInvalid() {
        Flux.never().window(1, 0);
    }

    @Test
    public void exact() {
        AssertSubscriber<Publisher<Integer>> ts = AssertSubscriber.create();
        Flux.range(1, 10).window(3).subscribe(ts);
        ts.assertValueCount(4).assertComplete().assertNoError();
        FluxWindowTest.toList(ts.values().get(0)).assertValues(1, 2, 3).assertComplete().assertNoError();
        FluxWindowTest.toList(ts.values().get(1)).assertValues(4, 5, 6).assertComplete().assertNoError();
        FluxWindowTest.toList(ts.values().get(2)).assertValues(7, 8, 9).assertComplete().assertNoError();
        FluxWindowTest.toList(ts.values().get(3)).assertValues(10).assertComplete().assertNoError();
    }

    @Test
    public void exactBackpressured() {
        AssertSubscriber<Publisher<Integer>> ts = AssertSubscriber.create(0L);
        Flux.range(1, 10).window(3).subscribe(ts);
        ts.assertNoValues().assertNoError().assertNotComplete();
        ts.request(1);
        ts.assertValueCount(1).assertNotComplete().assertNoError();
        FluxWindowTest.toList(ts.values().get(0)).assertValues(1, 2, 3).assertComplete().assertNoError();
        ts.request(1);
        ts.assertValueCount(2).assertNotComplete().assertNoError();
        FluxWindowTest.toList(ts.values().get(1)).assertValues(4, 5, 6).assertComplete().assertNoError();
        ts.request(1);
        ts.assertValueCount(3).assertNotComplete().assertNoError();
        FluxWindowTest.toList(ts.values().get(2)).assertValues(7, 8, 9).assertComplete().assertNoError();
        ts.request(1);
        ts.assertValueCount(4).assertComplete().assertNoError();
        FluxWindowTest.toList(ts.values().get(3)).assertValues(10).assertComplete().assertNoError();
    }

    @Test
    public void exactWindowCount() {
        AssertSubscriber<Publisher<Integer>> ts = AssertSubscriber.create();
        Flux.range(1, 9).window(3).subscribe(ts);
        ts.assertValueCount(3).assertComplete().assertNoError();
        FluxWindowTest.toList(ts.values().get(0)).assertValues(1, 2, 3).assertComplete().assertNoError();
        FluxWindowTest.toList(ts.values().get(1)).assertValues(4, 5, 6).assertComplete().assertNoError();
        FluxWindowTest.toList(ts.values().get(2)).assertValues(7, 8, 9).assertComplete().assertNoError();
    }

    @Test
    public void skip() {
        AssertSubscriber<Publisher<Integer>> ts = AssertSubscriber.create();
        Flux.range(1, 10).window(2, 3).subscribe(ts);
        ts.assertValueCount(4).assertComplete().assertNoError();
        FluxWindowTest.toList(ts.values().get(0)).assertValues(1, 2).assertComplete().assertNoError();
        FluxWindowTest.toList(ts.values().get(1)).assertValues(4, 5).assertComplete().assertNoError();
        FluxWindowTest.toList(ts.values().get(2)).assertValues(7, 8).assertComplete().assertNoError();
        FluxWindowTest.toList(ts.values().get(3)).assertValues(10).assertComplete().assertNoError();
    }

    @Test
    public void skipBackpressured() {
        AssertSubscriber<Publisher<Integer>> ts = AssertSubscriber.create(0L);
        Flux.range(1, 10).window(2, 3).subscribe(ts);
        ts.assertNoValues().assertNoError().assertNotComplete();
        ts.request(1);
        ts.assertValueCount(1).assertNotComplete().assertNoError();
        FluxWindowTest.toList(ts.values().get(0)).assertValues(1, 2).assertComplete().assertNoError();
        ts.request(1);
        ts.assertValueCount(2).assertNotComplete().assertNoError();
        FluxWindowTest.toList(ts.values().get(1)).assertValues(4, 5).assertComplete().assertNoError();
        ts.request(1);
        ts.assertValueCount(3).assertNotComplete().assertNoError();
        FluxWindowTest.toList(ts.values().get(2)).assertValues(7, 8).assertComplete().assertNoError();
        ts.request(1);
        ts.assertValueCount(4).assertComplete().assertNoError();
        FluxWindowTest.toList(ts.values().get(3)).assertValues(10).assertComplete().assertNoError();
    }

    @Test
    public void overlap() {
        AssertSubscriber<Publisher<Integer>> ts = AssertSubscriber.create();
        Flux.range(1, 10).window(3, 1).subscribe(ts);
        ts.assertValueCount(10).assertComplete().assertNoError();
        FluxWindowTest.expect(ts, 0, 1, 2, 3);
        FluxWindowTest.expect(ts, 1, 2, 3, 4);
        FluxWindowTest.expect(ts, 2, 3, 4, 5);
        FluxWindowTest.expect(ts, 3, 4, 5, 6);
        FluxWindowTest.expect(ts, 4, 5, 6, 7);
        FluxWindowTest.expect(ts, 5, 6, 7, 8);
        FluxWindowTest.expect(ts, 6, 7, 8, 9);
        FluxWindowTest.expect(ts, 7, 8, 9, 10);
        FluxWindowTest.expect(ts, 8, 9, 10);
        FluxWindowTest.expect(ts, 9, 10);
    }

    @Test
    public void overlapBackpressured() {
        AssertSubscriber<Publisher<Integer>> ts = AssertSubscriber.create(0L);
        Flux.range(1, 10).window(3, 1).subscribe(ts);
        ts.assertNoValues().assertNoError().assertNotComplete();
        for (int i = 0; i < 10; i++) {
            ts.request(1);
            ts.assertValueCount((i + 1)).assertNoError();
            if (i == 9) {
                ts.assertComplete();
            } else {
                ts.assertNotComplete();
            }
            switch (i) {
                case 9 :
                    FluxWindowTest.expect(ts, 9, 10);
                    break;
                case 8 :
                    FluxWindowTest.expect(ts, 8, 9, 10);
                    break;
                case 7 :
                    FluxWindowTest.expect(ts, 7, 8, 9, 10);
                    break;
                case 6 :
                    FluxWindowTest.expect(ts, 6, 7, 8, 9);
                    break;
                case 5 :
                    FluxWindowTest.expect(ts, 5, 6, 7, 8);
                    break;
                case 4 :
                    FluxWindowTest.expect(ts, 4, 5, 6, 7);
                    break;
                case 3 :
                    FluxWindowTest.expect(ts, 3, 4, 5, 6);
                    break;
                case 2 :
                    FluxWindowTest.expect(ts, 2, 3, 4, 5);
                    break;
                case 1 :
                    FluxWindowTest.expect(ts, 1, 2, 3, 4);
                    break;
                case 0 :
                    FluxWindowTest.expect(ts, 0, 1, 2, 3);
                    break;
            }
        }
    }

    @Test
    public void exactError() {
        AssertSubscriber<Publisher<Integer>> ts = AssertSubscriber.create();
        DirectProcessor<Integer> sp = DirectProcessor.create();
        sp.window(2, 2).subscribe(ts);
        ts.assertValueCount(0).assertNotComplete().assertNoError();
        sp.onNext(1);
        sp.onError(new RuntimeException("forced failure"));
        ts.assertValueCount(1).assertNotComplete().assertError(RuntimeException.class).assertErrorMessage("forced failure");
        FluxWindowTest.toList(ts.values().get(0)).assertValues(1).assertNotComplete().assertError(RuntimeException.class).assertErrorMessage("forced failure");
    }

    @Test
    public void skipError() {
        AssertSubscriber<Publisher<Integer>> ts = AssertSubscriber.create();
        DirectProcessor<Integer> sp = DirectProcessor.create();
        sp.window(2, 3).subscribe(ts);
        ts.assertValueCount(0).assertNotComplete().assertNoError();
        sp.onNext(1);
        sp.onError(new RuntimeException("forced failure"));
        ts.assertValueCount(1).assertNotComplete().assertError(RuntimeException.class).assertErrorMessage("forced failure");
        FluxWindowTest.toList(ts.values().get(0)).assertValues(1).assertNotComplete().assertError(RuntimeException.class).assertErrorMessage("forced failure");
    }

    @Test
    public void skipInGapError() {
        AssertSubscriber<Publisher<Integer>> ts = AssertSubscriber.create();
        DirectProcessor<Integer> sp = DirectProcessor.create();
        sp.window(1, 3).subscribe(ts);
        ts.assertValueCount(0).assertNotComplete().assertNoError();
        sp.onNext(1);
        sp.onNext(2);
        sp.onError(new RuntimeException("forced failure"));
        ts.assertValueCount(1).assertNotComplete().assertError(RuntimeException.class).assertErrorMessage("forced failure");
        FluxWindowTest.expect(ts, 0, 1);
    }

    @Test
    public void overlapError() {
        AssertSubscriber<Publisher<Integer>> ts = AssertSubscriber.create();
        DirectProcessor<Integer> sp = DirectProcessor.create();
        sp.window(2, 1).subscribe(ts);
        ts.assertValueCount(0).assertNotComplete().assertNoError();
        sp.onNext(1);
        sp.onError(new RuntimeException("forced failure"));
        ts.assertValueCount(1).assertNotComplete().assertError(RuntimeException.class).assertErrorMessage("forced failure");
        FluxWindowTest.toList(ts.values().get(0)).assertValues(1).assertNotComplete().assertError(RuntimeException.class).assertErrorMessage("forced failure");
    }

    @Test
    public void windowWillSubdivideAnInputFlux() {
        Flux<Integer> numbers = Flux.just(1, 2, 3, 4, 5, 6, 7, 8);
        // "non overlapping windows"
        List<List<Integer>> res = numbers.window(2, 3).concatMap(Flux::buffer).buffer().blockLast();
        assertThat(res).containsExactly(Arrays.asList(1, 2), Arrays.asList(4, 5), Arrays.asList(7, 8));
    }

    @Test
    public void windowWillSubdivideAnInputFluxOverlap() {
        Flux<Integer> numbers = Flux.just(1, 2, 3, 4, 5, 6, 7, 8);
        // "non overlapping windows"
        List<List<Integer>> res = numbers.window(3, 2).concatMap(Flux::buffer).buffer().blockLast();
        assertThat(res).containsExactly(Arrays.asList(1, 2, 3), Arrays.asList(3, 4, 5), Arrays.asList(5, 6, 7), Arrays.asList(7, 8));
    }

    @Test
    public void windowWillRerouteAsManyElementAsSpecified() {
        assertThat(Flux.just(1, 2, 3, 4, 5).window(2).concatMap(Flux::collectList).collectList().block()).containsExactly(Arrays.asList(1, 2), Arrays.asList(3, 4), Arrays.asList(5));
    }

    @Test
    public void scanExactSubscriber() {
        CoreSubscriber<Flux<Integer>> actual = new LambdaSubscriber(null, ( e) -> {
        }, null, null);
        FluxWindow.WindowExactSubscriber<Integer> test = new FluxWindow.WindowExactSubscriber<Integer>(actual, 123, Queues.unbounded());
        Subscription parent = Operators.emptySubscription();
        test.onSubscribe(parent);
        Assertions.assertThat(test.scan(PARENT)).isSameAs(parent);
        Assertions.assertThat(test.scan(ACTUAL)).isSameAs(actual);
        Assertions.assertThat(test.scan(CAPACITY)).isEqualTo(123);
        Assertions.assertThat(test.scan(TERMINATED)).isFalse();
        test.onComplete();
        Assertions.assertThat(test.scan(TERMINATED)).isTrue();
        Assertions.assertThat(test.scan(CANCELLED)).isFalse();
        test.cancel();
        Assertions.assertThat(test.scan(CANCELLED)).isTrue();
    }

    @Test
    public void scanOverlapSubscriber() {
        CoreSubscriber<Flux<Integer>> actual = new LambdaSubscriber(null, ( e) -> {
        }, null, null);
        FluxWindow.WindowOverlapSubscriber<Integer> test = new FluxWindow.WindowOverlapSubscriber<Integer>(actual, 123, 3, Queues.unbounded(), Queues.<UnicastProcessor<Integer>>unbounded().get());
        Subscription parent = Operators.emptySubscription();
        test.onSubscribe(parent);
        Assertions.assertThat(test.scan(PARENT)).isSameAs(parent);
        Assertions.assertThat(test.scan(ACTUAL)).isSameAs(actual);
        Assertions.assertThat(test.scan(CAPACITY)).isEqualTo(123);
        test.requested = 35;
        Assertions.assertThat(test.scan(REQUESTED_FROM_DOWNSTREAM)).isEqualTo(35);
        test.onNext(2);
        Assertions.assertThat(test.scan(BUFFERED)).isEqualTo(1);
        Assertions.assertThat(test.scan(TERMINATED)).isFalse();
        Assertions.assertThat(test.scan(ERROR)).isNull();
        test.onError(new IllegalStateException("boom"));
        Assertions.assertThat(test.scan(ERROR)).hasMessage("boom");
        Assertions.assertThat(test.scan(TERMINATED)).isTrue();
        Assertions.assertThat(test.scan(CANCELLED)).isFalse();
        test.cancel();
        Assertions.assertThat(test.scan(CANCELLED)).isTrue();
    }

    @Test
    public void scanOverlapSubscriberSmallBuffered() {
        @SuppressWarnings("unchecked")
        Queue<UnicastProcessor<Integer>> mockQueue = Mockito.mock(Queue.class);
        CoreSubscriber<Flux<Integer>> actual = new LambdaSubscriber(null, ( e) -> {
        }, null, null);
        FluxWindow.WindowOverlapSubscriber<Integer> test = new FluxWindow.WindowOverlapSubscriber<Integer>(actual, 3, 3, Queues.unbounded(), mockQueue);
        Mockito.when(mockQueue.size()).thenReturn(((Integer.MAX_VALUE) - 2));
        // size() is 1
        test.offer(UnicastProcessor.create());
        assertThat(test.scan(BUFFERED)).isEqualTo(((Integer.MAX_VALUE) - 1));
        assertThat(test.scan(LARGE_BUFFERED)).isEqualTo(((Integer.MAX_VALUE) - 1L));
    }

    @Test
    public void scanOverlapSubscriberLargeBuffered() {
        @SuppressWarnings("unchecked")
        Queue<UnicastProcessor<Integer>> mockQueue = Mockito.mock(Queue.class);
        CoreSubscriber<Flux<Integer>> actual = new LambdaSubscriber(null, ( e) -> {
        }, null, null);
        FluxWindow.WindowOverlapSubscriber<Integer> test = new FluxWindow.WindowOverlapSubscriber<Integer>(actual, 3, 3, Queues.unbounded(), mockQueue);
        Mockito.when(mockQueue.size()).thenReturn(Integer.MAX_VALUE);
        // size() is 5
        test.offer(UnicastProcessor.create());
        test.offer(UnicastProcessor.create());
        test.offer(UnicastProcessor.create());
        test.offer(UnicastProcessor.create());
        test.offer(UnicastProcessor.create());
        assertThat(test.scan(BUFFERED)).isEqualTo(Integer.MIN_VALUE);
        assertThat(test.scan(LARGE_BUFFERED)).isEqualTo(((Integer.MAX_VALUE) + 5L));
    }

    @Test
    public void scanSkipSubscriber() {
        CoreSubscriber<Flux<Integer>> actual = new LambdaSubscriber(null, ( e) -> {
        }, null, null);
        FluxWindow.WindowSkipSubscriber<Integer> test = new FluxWindow.WindowSkipSubscriber<Integer>(actual, 123, 3, Queues.unbounded());
        Subscription parent = Operators.emptySubscription();
        test.onSubscribe(parent);
        Assertions.assertThat(test.scan(PARENT)).isSameAs(parent);
        Assertions.assertThat(test.scan(ACTUAL)).isSameAs(actual);
        Assertions.assertThat(test.scan(CAPACITY)).isEqualTo(123);
        Assertions.assertThat(test.scan(TERMINATED)).isFalse();
        test.onComplete();
        Assertions.assertThat(test.scan(TERMINATED)).isTrue();
        Assertions.assertThat(test.scan(CANCELLED)).isFalse();
        test.cancel();
        Assertions.assertThat(test.scan(CANCELLED)).isTrue();
    }
}

