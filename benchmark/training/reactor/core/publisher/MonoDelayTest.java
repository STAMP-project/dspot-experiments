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


import MonoDelay.MonoDelayRunnable;
import Scannable.Attr.ACTUAL;
import Scannable.Attr.CANCELLED;
import Scannable.Attr.PARENT;
import Scannable.Attr.RUN_ON;
import Scannable.Attr.TERMINATED;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Test;
import reactor.core.CoreSubscriber;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;


public class MonoDelayTest {
    @Test
    public void delayedSource() {
        StepVerifier.withVirtualTime(this::scenario_delayedSource).thenAwait(Duration.ofSeconds(4)).expectNext(0L).verifyComplete();
    }

    @Test
    public void delayedSourceError() {
        StepVerifier.withVirtualTime(this::scenario_delayedSourceError, 0L).thenAwait(Duration.ofSeconds(5)).verifyErrorMatches(Exceptions::isOverflow);
    }

    @Test
    public void multipleDelaysUsingDefaultScheduler() throws InterruptedException {
        AtomicLong counter = new AtomicLong();
        Mono.delay(Duration.ofMillis(50)).subscribe(( v) -> counter.incrementAndGet());
        Mono.delay(Duration.ofMillis(100)).subscribe(( v) -> counter.incrementAndGet());
        Mono.delay(Duration.ofMillis(150)).subscribe(( v) -> counter.incrementAndGet());
        Mono.delay(Duration.ofMillis(200)).subscribe(( v) -> counter.incrementAndGet());
        assertThat(counter.intValue()).isEqualTo(0);
        Thread.sleep(110);
        assertThat(counter.intValue()).isEqualTo(2);
        Thread.sleep(110);
        assertThat(counter.intValue()).isEqualTo(4);
    }

    @Test
    public void scanOperator() {
        MonoDelay test = new MonoDelay(1, TimeUnit.SECONDS, Schedulers.immediate());
        assertThat(test.scan(RUN_ON)).isSameAs(Schedulers.immediate());
    }

    @Test
    public void scanDelayRunnable() {
        CoreSubscriber<Long> actual = new LambdaMonoSubscriber(null, ( e) -> {
        }, null, null);
        MonoDelay.MonoDelayRunnable test = new MonoDelay.MonoDelayRunnable(actual);
        actual.onSubscribe(test);
        assertThat(test.scan(PARENT)).isNull();
        assertThat(test.scan(ACTUAL)).isSameAs(actual);
        assertThat(test.scan(TERMINATED)).isFalse();
        test.request(1);
        test.run();
        assertThat(test.scan(TERMINATED)).isTrue();
        assertThat(test.scan(CANCELLED)).isFalse();
    }

    @Test
    public void scanDelayRunnableCancelled() {
        CoreSubscriber<Long> actual = new LambdaMonoSubscriber(null, ( e) -> {
        }, null, null);
        MonoDelay.MonoDelayRunnable test = new MonoDelay.MonoDelayRunnable(actual);
        assertThat(test.scan(CANCELLED)).isFalse();
        test.cancel();
        assertThat(test.scan(CANCELLED)).isTrue();
    }
}

