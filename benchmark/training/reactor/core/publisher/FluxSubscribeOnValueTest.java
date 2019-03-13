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


import FluxSubscribeOnValue.ScheduledScalar;
import Fuseable.ASYNC;
import Fuseable.NONE;
import Scannable.Attr.ACTUAL;
import Scannable.Attr.BUFFERED;
import Scannable.Attr.CANCELLED;
import Scannable.Attr.RUN_ON;
import Scannable.Attr.TERMINATED;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.junit.Assert;
import org.junit.Test;
import reactor.core.CoreSubscriber;
import reactor.core.Scannable;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import static OperatorDisposables.DISPOSED;


public class FluxSubscribeOnValueTest {
    ConcurrentMap<Integer, Integer> execs = new ConcurrentHashMap<>();

    @Test
    public void finishedConstantsAreNotSame() {
        assertThat(FluxSubscribeOnValue.ScheduledScalar.FINISHED).isNotSameAs(FluxSubscribeOnValue.ScheduledEmpty.FINISHED);
    }

    @Test
    public void testSubscribeOnValueFusion() {
        StepVerifier.create(Flux.range(1, 100).flatMap(( f) -> Flux.just(f).subscribeOn(Schedulers.parallel()).log("testSubscribeOnValueFusion", Level.FINE).map(this::slow))).expectFusion(ASYNC, NONE).expectNextCount(100).verifyComplete();
        int minExec = 2;
        for (Integer counted : execs.values()) {
            Assert.assertTrue(((("Thread used less than " + minExec) + " ") + "times"), (counted >= minExec));
        }
    }

    @Test
    public void scanOperator() {
        final Flux<Integer> test = Flux.just(1).subscribeOn(Schedulers.immediate());
        assertThat(test).isInstanceOf(Scannable.class).isInstanceOf(FluxSubscribeOnValue.class);
        assertThat(((Scannable) (test)).scan(RUN_ON)).isSameAs(Schedulers.immediate());
    }

    @Test
    public void scanMainSubscriber() {
        CoreSubscriber<Integer> actual = new LambdaSubscriber(null, ( e) -> {
        }, null, null);
        FluxSubscribeOnValue.ScheduledScalar<Integer> test = new FluxSubscribeOnValue.ScheduledScalar<Integer>(actual, 1, Schedulers.single());
        assertThat(test.scan(ACTUAL)).isSameAs(actual);
        assertThat(test.scan(BUFFERED)).isEqualTo(1);
        assertThat(test.scan(TERMINATED)).isFalse();
        test.future = ScheduledScalar.FINISHED;
        assertThat(test.scan(TERMINATED)).isTrue();
        assertThat(test.scan(CANCELLED)).isFalse();
        test.future = DISPOSED;
        assertThat(test.scan(CANCELLED)).isTrue();
        assertThat(test.scan(RUN_ON)).isSameAs(Schedulers.single());
    }
}

