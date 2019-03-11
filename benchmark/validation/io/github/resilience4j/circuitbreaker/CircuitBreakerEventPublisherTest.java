/**
 * Copyright 2016 Robert Winkler
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.github.resilience4j.circuitbreaker;


import CircuitBreaker.EventPublisher;
import java.io.IOException;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.slf4j.Logger;


public class CircuitBreakerEventPublisherTest {
    private Logger logger;

    private CircuitBreaker circuitBreaker;

    @Test
    public void shouldReturnTheSameConsumer() {
        CircuitBreaker.EventPublisher eventPublisher = circuitBreaker.getEventPublisher();
        CircuitBreaker.EventPublisher eventPublisher2 = circuitBreaker.getEventPublisher();
        assertThat(eventPublisher).isEqualTo(eventPublisher2);
    }

    @Test
    public void shouldConsumeOnEvent() {
        circuitBreaker.getEventPublisher().onEvent(this::logEventType);
        circuitBreaker.onSuccess(1000);
        BDDMockito.then(logger).should(Mockito.times(1)).info("SUCCESS");
    }

    @Test
    public void shouldConsumeOnSuccessEvent() {
        circuitBreaker.getEventPublisher().onSuccess(this::logEventType);
        circuitBreaker.onSuccess(1000);
        BDDMockito.then(logger).should(Mockito.times(1)).info("SUCCESS");
    }

    @Test
    public void shouldConsumeOnErrorEvent() {
        circuitBreaker.getEventPublisher().onError(this::logEventType);
        circuitBreaker.onError(1000, new IOException("BAM!"));
        BDDMockito.then(logger).should(Mockito.times(1)).info("ERROR");
    }

    @Test
    public void shouldConsumeOnResetEvent() {
        circuitBreaker.getEventPublisher().onReset(this::logEventType);
        circuitBreaker.reset();
        BDDMockito.then(logger).should(Mockito.times(1)).info("RESET");
    }

    @Test
    public void shouldConsumeOnStateTransitionEvent() {
        circuitBreaker = CircuitBreaker.of("test", CircuitBreakerConfig.custom().ringBufferSizeInClosedState(1).build());
        circuitBreaker.getEventPublisher().onStateTransition(this::logEventType);
        circuitBreaker.onError(1000, new IOException("BAM!"));
        circuitBreaker.onError(1000, new IOException("BAM!"));
        BDDMockito.then(logger).should(Mockito.times(1)).info("STATE_TRANSITION");
    }

    @Test
    public void shouldConsumeCallNotPermittedEvent() {
        circuitBreaker = CircuitBreaker.of("test", CircuitBreakerConfig.custom().ringBufferSizeInClosedState(1).build());
        circuitBreaker.getEventPublisher().onCallNotPermitted(this::logEventType);
        circuitBreaker.onError(1000, new IOException("BAM!"));
        circuitBreaker.onError(1000, new IOException("BAM!"));
        circuitBreaker.isCallPermitted();
        BDDMockito.then(logger).should(Mockito.times(1)).info("NOT_PERMITTED");
    }

    @Test
    public void shouldNotProduceEventsInDisabledState() {
        // Given
        circuitBreaker = CircuitBreaker.of("test", CircuitBreakerConfig.custom().ringBufferSizeInClosedState(1).build());
        circuitBreaker.getEventPublisher().onEvent(this::logEventType);
        // When we transition to disabled
        circuitBreaker.transitionToDisabledState();
        // And we execute other calls that should generate events
        circuitBreaker.onError(1000, new IOException("BAM!"));
        circuitBreaker.onError(1000, new IOException("BAM!"));
        circuitBreaker.isCallPermitted();
        circuitBreaker.onSuccess(0);
        circuitBreaker.onError(1000, new IOException("BAM!"));
        // Then we do not produce events
        BDDMockito.then(logger).should(Mockito.times(1)).info("STATE_TRANSITION");
        BDDMockito.then(logger).should(Mockito.times(0)).info("NOT_PERMITTED");
        BDDMockito.then(logger).should(Mockito.times(0)).info("SUCCESS");
        BDDMockito.then(logger).should(Mockito.times(0)).info("ERROR");
        BDDMockito.then(logger).should(Mockito.times(0)).info("IGNORED_ERROR");
    }

    @Test
    public void shouldConsumeIgnoredErrorEvent() {
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom().recordFailure(( throwable) -> Match(throwable).of(Case($(instanceOf(.class)), true), Case($(), false))).build();
        circuitBreaker = CircuitBreaker.of("test", circuitBreakerConfig);
        circuitBreaker.getEventPublisher().onIgnoredError(this::logEventType);
        circuitBreaker.onError(1000, new IOException("BAM!"));
        BDDMockito.then(logger).should(Mockito.times(1)).info("IGNORED_ERROR");
    }
}

