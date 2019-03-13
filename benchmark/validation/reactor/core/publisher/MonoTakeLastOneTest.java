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
import Scannable.Attr.CANCELLED;
import Scannable.Attr.PARENT;
import Scannable.Attr.PREFETCH;
import Scannable.Attr.TERMINATED;
import org.junit.Test;
import org.reactivestreams.Subscription;
import reactor.core.CoreSubscriber;
import reactor.test.StepVerifier;


public class MonoTakeLastOneTest {
    @Test
    public void emptyThrowsNoSuchElement() {
        StepVerifier.create(Flux.empty().hide().last()).verifyErrorSatisfies(( e) -> assertThat(e).isInstanceOf(.class).hasMessage("Flux#last() didn't observe any onNext signal"));
    }

    @Test
    public void emptyCallableThrowsNoSuchElement() {
        StepVerifier.create(Flux.empty().last()).verifyErrorSatisfies(( e) -> assertThat(e).isInstanceOf(.class).hasMessage("Flux#last() didn't observe any onNext signal from Callable flux"));
    }

    @Test
    public void fallback() {
        StepVerifier.create(Flux.empty().last(1)).expectNext(1).verifyComplete();
    }

    @Test
    public void error() {
        StepVerifier.create(Flux.error(new Exception("test")).last()).verifyErrorMessage("test");
    }

    @Test
    public void errorHide() {
        StepVerifier.create(Flux.error(new Exception("test")).hide().last()).verifyErrorMessage("test");
    }

    @Test
    public void errorDefault() {
        StepVerifier.create(Flux.error(new Exception("test")).last("blah")).verifyErrorMessage("test");
    }

    @Test
    public void errorHideDefault() {
        StepVerifier.create(Flux.error(new Exception("test")).hide().last("blah")).verifyErrorMessage("test");
    }

    @Test
    public void normal() {
        StepVerifier.create(Flux.range(1, 100).last()).expectNext(100).verifyComplete();
    }

    @Test
    public void normal2() {
        StepVerifier.create(Flux.range(1, 100).last((-1))).expectNext(100).verifyComplete();
    }

    @Test
    public void normal3() {
        StepVerifier.create(Mono.fromCallable(() -> 100).flux().last((-1))).expectNext(100).verifyComplete();
    }

    @Test
    public void normalHide() {
        StepVerifier.create(Flux.range(1, 100).hide().last()).expectNext(100).verifyComplete();
    }

    @Test
    public void norma2() {
        StepVerifier.create(Flux.just(100).last((-1))).expectNext(100).verifyComplete();
    }

    @Test
    public void scanTakeLastOneSubscriber() {
        CoreSubscriber<String> actual = new LambdaMonoSubscriber(null, ( e) -> {
        }, null, null);
        MonoTakeLastOne.TakeLastOneSubscriber<String> test = new MonoTakeLastOne.TakeLastOneSubscriber<>(actual, "foo", true);
        Subscription parent = Operators.emptySubscription();
        test.onSubscribe(parent);
        assertThat(test.scan(PREFETCH)).isEqualTo(Integer.MAX_VALUE);
        assertThat(test.scan(PARENT)).isSameAs(parent);
        assertThat(test.scan(ACTUAL)).isSameAs(actual);
        // terminated is detected via state HAS_VALUE
        assertThat(test.scan(TERMINATED)).isFalse();
        test.complete("bar");
        assertThat(test.scan(TERMINATED)).isTrue();
        assertThat(test.scan(CANCELLED)).isFalse();
        test.cancel();
        assertThat(test.scan(CANCELLED)).isTrue();
    }
}

