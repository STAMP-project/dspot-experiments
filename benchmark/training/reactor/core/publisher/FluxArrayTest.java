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


import Fuseable.ConditionalSubscriber;
import Scannable.Attr.ACTUAL;
import Scannable.Attr.BUFFERED;
import Scannable.Attr.CANCELLED;
import Scannable.Attr.REQUESTED_FROM_DOWNSTREAM;
import Scannable.Attr.TERMINATED;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import reactor.core.CoreSubscriber;
import reactor.test.MockUtils;
import reactor.test.subscriber.AssertSubscriber;


public class FluxArrayTest {
    @Test(expected = NullPointerException.class)
    public void arrayNull() {
        Flux.fromArray(((Integer[]) (null)));
    }

    @Test
    public void normal() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        Flux.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).subscribe(ts);
        ts.assertNoError().assertValues(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).assertComplete();
    }

    @Test
    public void normalBackpressured() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create(0);
        Flux.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).subscribe(ts);
        ts.assertNoError().assertNoValues().assertNotComplete();
        ts.request(5);
        ts.assertNoError().assertValues(1, 2, 3, 4, 5).assertNotComplete();
        ts.request(10);
        ts.assertNoError().assertValues(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).assertComplete();
    }

    @Test
    public void normalBackpressuredExact() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create(10);
        Flux.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).subscribe(ts);
        ts.assertNoError().assertValues(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).assertComplete();
        ts.request(10);
        ts.assertNoError().assertValues(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).assertComplete();
    }

    @Test
    public void arrayContainsNull() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        Flux.just(1, 2, 3, 4, 5, null, 7, 8, 9, 10).subscribe(ts);
        ts.assertError(NullPointerException.class).assertValues(1, 2, 3, 4, 5).assertNotComplete();
    }

    @Test
    public void scanOperator() {
        FluxArray s = new FluxArray("A", "B", "C");
        assertThat(s.scan(BUFFERED)).isEqualTo(3);
    }

    @Test
    public void scanConditionalSubscription() {
        @SuppressWarnings("unchecked")
        ConditionalSubscriber<? extends Object> subscriber = Mockito.mock(MockUtils.TestScannableConditionalSubscriber.class);
        FluxArray.ArrayConditionalSubscription<Object> test = new FluxArray.ArrayConditionalSubscription<>(subscriber, new Object[]{ "foo", "bar", "baz" });
        assertThat(test.scan(TERMINATED)).isFalse();
        assertThat(test.scan(CANCELLED)).isFalse();
        assertThat(test.scan(BUFFERED)).isEqualTo(3);
        assertThat(test.scan(REQUESTED_FROM_DOWNSTREAM)).isZero();
        assertThat(test.scan(ACTUAL)).isSameAs(subscriber);
        test.poll();
        assertThat(test.scan(BUFFERED)).isEqualTo(2);
        test.poll();
        test.request(2);
        assertThat(test.scan(REQUESTED_FROM_DOWNSTREAM)).isEqualTo(2L);
        test.poll();
        assertThat(test.scan(TERMINATED)).isTrue();
    }

    @Test
    public void scanConditionalSubscriptionRequested() {
        @SuppressWarnings("unchecked")
        ConditionalSubscriber<? extends Object> subscriber = Mockito.mock(MockUtils.TestScannableConditionalSubscriber.class);
        // the mock will not drain the request, so it can be tested
        Mockito.when(subscriber.tryOnNext(Mockito.any())).thenReturn(false);
        FluxArray.ArrayConditionalSubscription<Object> test = new FluxArray.ArrayConditionalSubscription<>(subscriber, new Object[]{ "foo", "bar", "baz" });
        test.request(2);
        assertThat(test.scan(REQUESTED_FROM_DOWNSTREAM)).isEqualTo(2L);
        assertThat(test.scan(BUFFERED)).isEqualTo(3);
    }

    @Test
    public void scanConditionalSubscriptionCancelled() {
        @SuppressWarnings("unchecked")
        ConditionalSubscriber<? extends Object> subscriber = Mockito.mock(MockUtils.TestScannableConditionalSubscriber.class);
        FluxArray.ArrayConditionalSubscription<Object> test = new FluxArray.ArrayConditionalSubscription<>(subscriber, new Object[]{ "foo", "bar", "baz" });
        test.cancel();
        assertThat(test.scan(TERMINATED)).isFalse();
        assertThat(test.scan(CANCELLED)).isTrue();
    }

    @Test
    public void scanSubscription() {
        @SuppressWarnings("unchecked")
        InnerOperator<String, String> subscriber = Mockito.mock(InnerOperator.class);
        FluxArray.ArraySubscription<String> test = new FluxArray.ArraySubscription<>(subscriber, new String[]{ "foo", "bar", "baz" });
        assertThat(test.scan(TERMINATED)).isFalse();
        assertThat(test.scan(CANCELLED)).isFalse();
        assertThat(test.scan(BUFFERED)).isEqualTo(3);
        assertThat(test.scan(REQUESTED_FROM_DOWNSTREAM)).isEqualTo(0L);
        assertThat(test.scan(ACTUAL)).isSameAs(subscriber);
        test.poll();
        assertThat(test.scan(BUFFERED)).isEqualTo(2);
        test.request(2);
        assertThat(test.scan(REQUESTED_FROM_DOWNSTREAM)).isEqualTo(2L);
        test.poll();
        test.poll();
        assertThat(test.scan(TERMINATED)).isTrue();
    }

    @Test
    public void scanSubscriptionCancelled() {
        @SuppressWarnings("unchecked")
        CoreSubscriber<String> subscriber = Mockito.mock(InnerOperator.class);
        FluxArray.ArraySubscription<String> test = new FluxArray.ArraySubscription<>(subscriber, new String[]{ "foo", "bar", "baz" });
        assertThat(test.scan(CANCELLED)).isFalse();
        test.cancel();
        assertThat(test.scan(CANCELLED)).isTrue();
    }
}

