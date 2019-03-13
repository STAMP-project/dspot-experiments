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
import Scannable.Attr.TERMINATED;
import org.junit.Test;
import org.reactivestreams.Subscription;
import reactor.core.CoreSubscriber;
import reactor.util.concurrent.Queues;


public class ParallelSourceTest {
    @Test
    public void parallelism() {
        Flux<String> source = Flux.empty();
        ParallelSource<String> test = new ParallelSource(source, 100, 123, Queues.small());
        assertThat(test.parallelism()).isEqualTo(100);
    }

    @Test
    public void scanOperator() throws Exception {
        Flux<String> source = Flux.just("").map(( i) -> i);
        ParallelSource<String> test = new ParallelSource(source, 100, 123, Queues.small());
        assertThat(test.scan(PARENT)).isSameAs(source);
        assertThat(test.scan(PREFETCH)).isEqualTo(123);
    }

    @Test
    public void scanMainSubscriber() {
        @SuppressWarnings("unchecked")
        CoreSubscriber<String>[] subs = new CoreSubscriber[1];
        subs[0] = new LambdaSubscriber(null, ( e) -> {
        }, null, null);
        ParallelSource.ParallelSourceMain<String> test = new ParallelSource.ParallelSourceMain<>(subs, 123, Queues.one());
        Subscription parent = Operators.emptySubscription();
        test.onSubscribe(parent);
        assertThat(test.scan(PARENT)).isSameAs(parent);
        assertThat(test.scan(PREFETCH)).isEqualTo(123);
        assertThat(test.scan(BUFFERED)).isZero();
        test.queue.offer("foo");
        assertThat(test.scan(BUFFERED)).isEqualTo(1);
        assertThat(test.scan(TERMINATED)).isFalse();
        assertThat(test.scan(CANCELLED)).isFalse();
        assertThat(test.scan(ERROR)).isNull();
        test.onError(new IllegalStateException("boom"));
        assertThat(test.scan(TERMINATED)).isTrue();
        assertThat(test.scan(CANCELLED)).isFalse();
        assertThat(test.scan(ERROR)).hasMessage("boom");
        test.cancel();
        assertThat(test.scan(CANCELLED)).isTrue();
    }

    @Test
    public void scanInnerSubscriber() {
        @SuppressWarnings("unchecked")
        CoreSubscriber<String>[] subs = new CoreSubscriber[2];
        subs[0] = new LambdaSubscriber(null, ( e) -> {
        }, null, null);
        subs[1] = new LambdaSubscriber(null, ( e) -> {
        }, null, null);
        ParallelSource.ParallelSourceMain<String> main = new ParallelSource.ParallelSourceMain<>(subs, 123, Queues.one());
        ParallelSource.ParallelSourceMain.ParallelSourceInner<String> test = new ParallelSource.ParallelSourceMain.ParallelSourceInner<>(main, 1, 10);
        assertThat(test.scan(PARENT)).isSameAs(main);
        assertThat(test.scan(ACTUAL)).isSameAs(subs[test.index]);
    }
}

