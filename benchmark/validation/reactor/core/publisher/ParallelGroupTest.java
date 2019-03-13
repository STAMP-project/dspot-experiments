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
import Scannable.Attr.CANCELLED;
import Scannable.Attr.PARENT;
import Scannable.Attr.PREFETCH;
import Scannable.Attr.REQUESTED_FROM_DOWNSTREAM;
import org.junit.Test;
import org.reactivestreams.Subscription;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.ParallelGroup.ParallelInnerGroup;


public class ParallelGroupTest {
    @Test
    public void scanOperator() {
        ParallelFlux<Integer> source = Flux.range(1, 4).parallel(3);
        ParallelGroup<Integer> test = new ParallelGroup(source);
        assertThat(test.scan(PARENT)).isSameAs(source);
        assertThat(test.scan(PREFETCH)).isEqualTo((-1));
    }

    @Test
    public void scanInnerGroup() {
        ParallelInnerGroup<Integer> test = new ParallelInnerGroup(1023);
        CoreSubscriber<Integer> subscriber = new LambdaSubscriber(null, ( e) -> {
        }, null, ( sub) -> sub.request(3));
        Subscription s = Operators.emptySubscription();
        test.onSubscribe(s);
        test.subscribe(subscriber);
        assertThat(test.scan(PARENT)).isSameAs(s);
        assertThat(test.scan(ACTUAL)).isSameAs(subscriber);
        // see other test for request
        assertThat(test.scan(REQUESTED_FROM_DOWNSTREAM)).isZero();
        assertThat(test.scan(CANCELLED)).isFalse();
        test.cancel();
        assertThat(test.scan(CANCELLED)).isTrue();
    }

    @Test
    public void scanInnerGroupRequestNotTrackedWhenParent() {
        ParallelInnerGroup<Integer> test = new ParallelInnerGroup(1023);
        CoreSubscriber<Integer> subscriber = new LambdaSubscriber(null, ( e) -> {
        }, null, ( sub) -> sub.request(3));
        Subscription s = Operators.emptySubscription();
        test.onSubscribe(s);
        test.subscribe(subscriber);
        assertThat(test.scan(REQUESTED_FROM_DOWNSTREAM)).isZero();
        test.request(2);
        assertThat(test.scan(REQUESTED_FROM_DOWNSTREAM)).isZero();
    }

    @Test
    public void scanInnerGroupRequestTrackedWhenNoParent() {
        ParallelInnerGroup<Integer> test = new ParallelInnerGroup(1023);
        CoreSubscriber<Integer> subscriber = new LambdaSubscriber(null, ( e) -> {
        }, null, ( sub) -> sub.request(3));
        test.subscribe(subscriber);
        assertThat(test.scan(REQUESTED_FROM_DOWNSTREAM)).isEqualTo(3);
        test.request(2);
        assertThat(test.scan(REQUESTED_FROM_DOWNSTREAM)).isEqualTo(5);
    }
}

