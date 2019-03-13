/**
 * Copyright 2016 Robert Winkler and Bohdan Storozhuk
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
package io.github.resilience4j.ratelimiter;


import RateLimiter.EventPublisher;
import io.vavr.control.Try;
import java.time.Duration;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.slf4j.Logger;


@SuppressWarnings("unchecked")
public class RateLimiterEventPublisherTest {
    private static final int LIMIT = 1;

    private static final Duration TIMEOUT = Duration.ZERO;

    private static final Duration REFRESH_PERIOD = Duration.ofSeconds(5);

    private RateLimiter rateLimiter;

    private Logger logger;

    @Test
    public void shouldReturnTheSameConsumer() {
        RateLimiter.EventPublisher eventPublisher = rateLimiter.getEventPublisher();
        RateLimiter.EventPublisher eventPublisher2 = rateLimiter.getEventPublisher();
        assertThat(eventPublisher).isEqualTo(eventPublisher2);
    }

    @Test
    public void shouldConsumeOnSuccessEvent() throws Throwable {
        rateLimiter.getEventPublisher().onSuccess(( event) -> logger.info(event.getEventType().toString()));
        String result = rateLimiter.executeSupplier(() -> "Hello world");
        assertThat(result).isEqualTo("Hello world");
        BDDMockito.then(logger).should(Mockito.times(1)).info("SUCCESSFUL_ACQUIRE");
    }

    @Test
    public void shouldConsumeOnFailureEvent() throws Throwable {
        rateLimiter.getEventPublisher().onFailure(( event) -> logger.info(event.getEventType().toString()));
        rateLimiter.executeSupplier(() -> "Hello world");
        Try.ofSupplier(RateLimiter.decorateSupplier(rateLimiter, () -> "Hello world"));
        BDDMockito.then(logger).should(Mockito.times(1)).info("FAILED_ACQUIRE");
    }
}

