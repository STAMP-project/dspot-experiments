/**
 * Copyright 2018-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.cloud.gateway.rsocket.socketacceptor;


import Success.INSTANCE;
import java.util.Collections;
import org.junit.Test;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.rsocket.filter.RSocketFilter.Success;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


public class SocketAcceptorPredicateFilterTests {
    @Test
    public void noPredicateWorks() {
        Mono<Success> result = runFilter(Collections.emptyList());
        StepVerifier.create(result).expectNext(INSTANCE).verifyComplete();
    }

    @Test
    public void singleTruePredicateWorks() {
        SocketAcceptorPredicateFilterTests.TestPredicate predicate = new SocketAcceptorPredicateFilterTests.TestPredicate(true);
        Mono<Success> result = runFilter(predicate);
        StepVerifier.create(result).expectNext(INSTANCE).verifyComplete();
        assertThat(predicate.invoked()).isTrue();
    }

    @Test
    public void singleFalsePredicateWorks() {
        SocketAcceptorPredicateFilterTests.TestPredicate predicate = new SocketAcceptorPredicateFilterTests.TestPredicate(false);
        Mono<Success> result = runFilter(predicate);
        StepVerifier.create(result).verifyComplete();
        assertThat(predicate.invoked()).isTrue();
    }

    @Test
    public void multipleFalsePredicateWorks() {
        SocketAcceptorPredicateFilterTests.TestPredicate predicate = new SocketAcceptorPredicateFilterTests.TestPredicate(false);
        SocketAcceptorPredicateFilterTests.TestPredicate predicate2 = new SocketAcceptorPredicateFilterTests.TestPredicate(false);
        Mono<Success> result = runFilter(predicate, predicate2);
        StepVerifier.create(result).verifyComplete();
        assertThat(predicate.invoked()).isTrue();
        assertThat(predicate2.invoked()).isTrue();// Async predicates don't short circuit

    }

    @Test
    public void multiplePredicatesNoSuccessWorks() {
        SocketAcceptorPredicateFilterTests.TestPredicate truePredicate = new SocketAcceptorPredicateFilterTests.TestPredicate(true);
        SocketAcceptorPredicateFilterTests.TestPredicate falsePredicate = new SocketAcceptorPredicateFilterTests.TestPredicate(false);
        Mono<Success> result = runFilter(truePredicate, falsePredicate);
        StepVerifier.create(result).verifyComplete();
        assertThat(truePredicate.invoked()).isTrue();
        assertThat(falsePredicate.invoked()).isTrue();
    }

    @Test
    public void multiplePredicatesSuccessWorks() {
        SocketAcceptorPredicateFilterTests.TestPredicate truePredicate = new SocketAcceptorPredicateFilterTests.TestPredicate(true);
        SocketAcceptorPredicateFilterTests.TestPredicate truePredicate2 = new SocketAcceptorPredicateFilterTests.TestPredicate(true);
        Mono<Success> result = runFilter(truePredicate, truePredicate2);
        StepVerifier.create(result).expectNext(INSTANCE).verifyComplete();
        assertThat(truePredicate.invoked()).isTrue();
        assertThat(truePredicate2.invoked()).isTrue();
    }

    private class TestPredicate implements SocketAcceptorPredicate {
        private boolean invoked = false;

        private final Mono<Boolean> test;

        TestPredicate(boolean value) {
            test = Mono.just(value);
        }

        @Override
        public Publisher<Boolean> apply(SocketAcceptorExchange exchange) {
            invoked = true;
            return test;
        }

        public boolean invoked() {
            return invoked;
        }
    }
}

