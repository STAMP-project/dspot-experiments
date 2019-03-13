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


import MonoFlatMap.FlatMapMain;
import Scannable.Attr.ACTUAL;
import Scannable.Attr.CANCELLED;
import Scannable.Attr.PARENT;
import Scannable.Attr.PREFETCH;
import Scannable.Attr.TERMINATED;
import org.junit.Test;
import org.reactivestreams.Subscription;
import reactor.core.CoreSubscriber;
import reactor.test.publisher.TestPublisher;
import reactor.test.subscriber.AssertSubscriber;


public class MonoFlatMapTest {
    @Test
    public void normalHidden() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        Mono.just(1).hide().flatMap(( v) -> Mono.just(2).hide()).subscribe(ts);
        ts.assertValues(2).assertComplete().assertNoError();
    }

    @Test
    public void cancel() {
        TestPublisher<String> cancelTester = TestPublisher.create();
        MonoProcessor<Integer> processor = cancelTester.mono().flatMap(( s) -> Mono.just(s.length())).toProcessor();
        processor.subscribe();
        processor.cancel();
        cancelTester.assertCancelled();
    }

    @Test
    public void scanMain() {
        CoreSubscriber<Integer> actual = new LambdaMonoSubscriber(null, ( e) -> {
        }, null, null);
        FlatMapMain<String, Integer> test = new MonoFlatMap.FlatMapMain<>(actual, ( s) -> Mono.just(s.length()));
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
    public void scanInner() {
        CoreSubscriber<Integer> actual = new LambdaMonoSubscriber(null, ( e) -> {
        }, null, null);
        FlatMapMain<String, Integer> main = new MonoFlatMap.FlatMapMain<>(actual, ( s) -> Mono.just(s.length()));
        MonoFlatMap.FlatMapInner<Integer> test = new MonoFlatMap.FlatMapInner<>(main);
        Subscription innerSubscription = Operators.emptySubscription();
        test.onSubscribe(innerSubscription);
        assertThat(test.scan(PARENT)).isSameAs(innerSubscription);
        assertThat(test.scan(ACTUAL)).isSameAs(main);
        assertThat(test.scan(CANCELLED)).isFalse();
        assertThat(test.scan(TERMINATED)).isFalse();
        test.onError(new IllegalStateException("boom"));
        assertThat(test.scan(TERMINATED)).isTrue();
        assertThat(test.scan(CANCELLED)).isFalse();
        test.cancel();
        assertThat(test.scan(CANCELLED)).isTrue();
    }
}

