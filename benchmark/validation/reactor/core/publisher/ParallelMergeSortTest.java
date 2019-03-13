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
import Scannable.Attr.BUFFERED;
import Scannable.Attr.CANCELLED;
import Scannable.Attr.ERROR;
import Scannable.Attr.PARENT;
import Scannable.Attr.PREFETCH;
import Scannable.Attr.REQUESTED_FROM_DOWNSTREAM;
import java.util.List;
import org.junit.Test;
import org.reactivestreams.Subscription;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.ParallelMergeSort.MergeSortInner;
import reactor.core.publisher.ParallelMergeSort.MergeSortMain;


public class ParallelMergeSortTest {
    @Test
    public void scanOperator() {
        ParallelFlux<List<Integer>> source = Flux.just(500, 300).buffer(1).parallel(10);
        ParallelMergeSort<Integer> test = new ParallelMergeSort(source, Integer::compareTo);
        assertThat(test.scan(PARENT)).isSameAs(source);
        assertThat(test.scan(PREFETCH)).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    public void scanMainSubscriber() {
        LambdaSubscriber<Integer> subscriber = new LambdaSubscriber(null, ( e) -> {
        }, null, ( s) -> s.request(2));
        MergeSortMain<Integer> test = new MergeSortMain(subscriber, 4, Integer::compareTo);
        subscriber.onSubscribe(test);
        assertThat(test.scan(ACTUAL)).isSameAs(subscriber);
        assertThat(test.scan(ERROR)).isNull();
        assertThat(test.scan(REQUESTED_FROM_DOWNSTREAM)).isEqualTo(2);
        assertThat(test.scan(BUFFERED)).isEqualTo(0);
        test.remaining = 3;
        assertThat(test.scan(BUFFERED)).isEqualTo(1);
        test.remaining = 0;
        assertThat(test.scan(BUFFERED)).isEqualTo(4);
        assertThat(test.scan(CANCELLED)).isFalse();
        test.cancel();
        assertThat(test.scan(CANCELLED)).isTrue();
    }

    @Test
    public void scanInnerSubscriber() {
        CoreSubscriber<Integer> mainActual = new LambdaSubscriber(null, ( e) -> {
        }, null, null);
        MergeSortMain<Integer> main = new MergeSortMain(mainActual, 2, Integer::compareTo);
        MergeSortInner<Integer> test = new MergeSortInner(main, 1);
        Subscription subscription = Operators.emptySubscription();
        test.onSubscribe(subscription);
        assertThat(test.scan(PARENT)).isSameAs(subscription);
        assertThat(test.scan(ACTUAL)).isSameAs(main);
        assertThat(test.scan(PREFETCH)).isEqualTo(Integer.MAX_VALUE);
        assertThat(test.scan(CANCELLED)).isFalse();
        test.cancel();
        assertThat(test.scan(CANCELLED)).isTrue();
    }
}

