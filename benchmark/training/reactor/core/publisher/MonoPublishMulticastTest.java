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
import Scannable.Attr.REQUESTED_FROM_DOWNSTREAM;
import Scannable.Attr.TERMINATED;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.reactivestreams.Subscription;
import reactor.core.CoreSubscriber;
import reactor.test.StepVerifier;
import reactor.test.subscriber.AssertSubscriber;
import reactor.util.context.Context;


public class MonoPublishMulticastTest {
    @Test
    public void normal() {
        AtomicInteger i = new AtomicInteger();
        Mono<Integer> m = Mono.fromCallable(i::incrementAndGet).publish(( o) -> o.map(( s) -> 2));
        StepVerifier.create(m).expectNext(2).verifyComplete();
        StepVerifier.create(m).expectNext(2).verifyComplete();
    }

    @Test
    public void normalHide() {
        AtomicInteger i = new AtomicInteger();
        Mono<Integer> m = Mono.fromCallable(i::incrementAndGet).hide().publish(( o) -> o.map(( s) -> 2));
        StepVerifier.create(m).expectNext(2).verifyComplete();
        StepVerifier.create(m).expectNext(2).verifyComplete();
    }

    @Test
    public void cancelComposes() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        MonoProcessor<Integer> sp = MonoProcessor.create();
        sp.publish(( o) -> Mono.<Integer>never()).subscribe(ts);
        Assert.assertTrue("Not subscribed?", ((sp.downstreamCount()) != 0));
        ts.cancel();
        Assert.assertFalse("Still subscribed?", sp.isCancelled());
    }

    @Test
    public void cancelComposes2() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        MonoProcessor<Integer> sp = MonoProcessor.create();
        sp.publish(( o) -> Mono.<Integer>empty()).subscribe(ts);
        Assert.assertFalse("Still subscribed?", sp.isCancelled());
    }

    @Test
    public void nullFunction() {
        assertThatNullPointerException().isThrownBy(() -> Mono.just("Foo").publish(null)).withMessage("transform");
    }

    @Test
    public void npeFunction() {
        StepVerifier.create(Mono.just("Foo").publish(( m) -> null)).expectErrorSatisfies(( e) -> assertThat(e).isInstanceOf(.class).hasMessage("The transform returned a null Mono")).verify();
    }

    @Test
    public void failingFunction() {
        RuntimeException expected = new IllegalStateException("boom");
        StepVerifier.create(Mono.just("Foo").publish(( m) -> {
            throw expected;
        })).expectErrorSatisfies(( e) -> assertThat(e).isSameAs(expected)).verify();
    }

    @Test
    public void syncCancelBeforeComplete() {
        assertThat(Mono.just(Mono.just(1).publish(( v) -> v)).flatMapMany(( v) -> v).blockLast()).isEqualTo(1);
    }

    @Test
    public void normalCancelBeforeComplete() {
        assertThat(Mono.just(Mono.just(1).hide().publish(( v) -> v)).flatMapMany(( v) -> v).blockLast()).isEqualTo(1);
    }

    @Test
    public void scanMulticaster() {
        MonoPublishMulticast.MonoPublishMulticaster<Integer> test = new MonoPublishMulticast.MonoPublishMulticaster<>(Context.empty());
        Subscription parent = Operators.emptySubscription();
        test.onSubscribe(parent);
        assertThat(test.scan(PARENT)).isSameAs(parent);
        assertThat(test.scan(PREFETCH)).isEqualTo(1);
        assertThat(test.scan(BUFFERED)).isEqualTo(0);
        test.value = 1;
        assertThat(test.scan(BUFFERED)).isEqualTo(1);
        assertThat(test.scan(TERMINATED)).isFalse();
        assertThat(test.scan(ERROR)).isNull();
        test.error = new IllegalArgumentException("boom");
        assertThat(test.scan(ERROR)).isSameAs(test.error);
        test.onComplete();
        assertThat(test.scan(TERMINATED)).isTrue();
        assertThat(test.scan(CANCELLED)).isFalse();
        test.terminate();
        assertThat(test.scan(CANCELLED)).isTrue();
    }

    @Test
    public void scanMulticastInner() {
        CoreSubscriber<Integer> actual = new LambdaSubscriber(null, ( e) -> {
        }, null, null);
        MonoPublishMulticast.MonoPublishMulticaster<Integer> parent = new MonoPublishMulticast.MonoPublishMulticaster<>(Context.empty());
        MonoPublishMulticast.PublishMulticastInner<Integer> test = new MonoPublishMulticast.PublishMulticastInner<>(parent, actual);
        assertThat(test.scan(PARENT)).isSameAs(parent);
        assertThat(test.scan(ACTUAL)).isSameAs(actual);
        test.request(789);
        // does not track request in the Mono version
        assertThat(test.scan(REQUESTED_FROM_DOWNSTREAM)).isEqualTo(0);
        assertThat(test.scan(CANCELLED)).isFalse();
        test.cancel();
        assertThat(test.scan(CANCELLED)).isTrue();
    }
}

