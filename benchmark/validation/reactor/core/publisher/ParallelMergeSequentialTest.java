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
import Scannable.Attr.ERROR;
import Scannable.Attr.PARENT;
import Scannable.Attr.PREFETCH;
import Scannable.Attr.REQUESTED_FROM_DOWNSTREAM;
import Scannable.Attr.TERMINATED;
import org.junit.Test;
import org.reactivestreams.Subscription;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.ParallelMergeSequential.MergeSequentialInner;
import reactor.core.publisher.ParallelMergeSequential.MergeSequentialMain;
import reactor.util.concurrent.Queues;


public class ParallelMergeSequentialTest {
    @Test
    public void scanOperator() {
        ParallelFlux<Integer> source = Flux.just(500, 300).parallel(10);
        ParallelMergeSequential<Integer> test = new ParallelMergeSequential(source, 123, Queues.one());
        assertThat(test.scan(PARENT)).isSameAs(source);
        assertThat(test.scan(PREFETCH)).isEqualTo(123);
    }

    @Test
    public void scanMainSubscriber() {
        LambdaSubscriber<Integer> subscriber = new LambdaSubscriber(null, ( e) -> {
        }, null, ( s) -> s.request(2));
        MergeSequentialMain<Integer> test = new MergeSequentialMain(subscriber, 4, 123, Queues.small());
        subscriber.onSubscribe(test);
        assertThat(test.scan(ACTUAL)).isSameAs(subscriber);
        assertThat(test.scan(REQUESTED_FROM_DOWNSTREAM)).isEqualTo(2);
        assertThat(test.scan(TERMINATED)).isFalse();
        assertThat(test.scan(ERROR)).isNull();
        assertThat(test.scan(CANCELLED)).isFalse();
        test.cancel();
        assertThat(test.scan(CANCELLED)).isTrue();
    }

    @Test
    public void scanMainSubscriberDoneAfterNComplete() {
        LambdaSubscriber<Integer> subscriber = new LambdaSubscriber(null, ( e) -> {
        }, null, ( s) -> s.request(2));
        int n = 4;
        MergeSequentialMain<Integer> test = new MergeSequentialMain(subscriber, n, 123, Queues.small());
        subscriber.onSubscribe(test);
        for (int i = 0; i < n; i++) {
            assertThat(test.scan(TERMINATED)).as(("complete " + i)).isFalse();
            test.onComplete();
        }
        assertThat(test.scan(TERMINATED)).isTrue();
    }

    @Test
    public void scanMainSubscriberError() {
        LambdaSubscriber<Integer> subscriber = new LambdaSubscriber(null, ( e) -> {
        }, null, ( s) -> s.request(2));
        MergeSequentialMain<Integer> test = new MergeSequentialMain(subscriber, 4, 123, Queues.small());
        subscriber.onSubscribe(test);
        assertThat(test.scan(TERMINATED)).isFalse();
        assertThat(test.scan(ERROR)).isNull();
        test.onError(new IllegalStateException("boom"));
        assertThat(test.scan(TERMINATED)).isFalse();
        assertThat(test.scan(ERROR)).hasMessage("boom");
    }

    @Test
    public void scanInnerSubscriber() {
        CoreSubscriber<Integer> mainActual = new LambdaSubscriber(null, ( e) -> {
        }, null, null);
        MergeSequentialMain<Integer> main = new MergeSequentialMain(mainActual, 2, 123, Queues.small());
        MergeSequentialInner<Integer> test = new MergeSequentialInner(main, 456);
        Subscription subscription = Operators.emptySubscription();
        test.onSubscribe(subscription);
    }
}

