/**
 * Copyright 2017 Robert Winkler, Lucas Lech
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
package io.github.resilience4j.bulkhead;


import Bulkhead.EventPublisher;
import io.github.resilience4j.test.HelloWorldService;
import io.vavr.control.Try;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.slf4j.Logger;


public class BulkheadEventPublisherTest {
    private HelloWorldService helloWorldService;

    private BulkheadConfig config;

    private Logger logger;

    private Bulkhead bulkhead;

    @Test
    public void shouldReturnTheSameConsumer() {
        Bulkhead.EventPublisher eventPublisher = bulkhead.getEventPublisher();
        Bulkhead.EventPublisher eventPublisher2 = bulkhead.getEventPublisher();
        assertThat(eventPublisher).isEqualTo(eventPublisher2);
    }

    @Test
    public void shouldConsumeOnCallPermittedEvent() {
        // Given
        Bulkhead bulkhead = Bulkhead.of("test", config);
        BDDMockito.given(helloWorldService.returnHelloWorld()).willReturn("Hello world");
        // When
        bulkhead.getEventPublisher().onCallPermitted(( event) -> logger.info(event.getEventType().toString()));
        String result = bulkhead.executeSupplier(helloWorldService::returnHelloWorld);
        // Then
        assertThat(result).isEqualTo("Hello world");
        BDDMockito.then(logger).should(Mockito.times(1)).info("CALL_PERMITTED");
    }

    @Test
    public void shouldConsumeOnCallRejectedEvent() {
        // Given
        Bulkhead bulkhead = Bulkhead.of("test", config);
        // When
        bulkhead.getEventPublisher().onCallRejected(( event) -> logger.info(event.getEventType().toString()));
        bulkhead.isCallPermitted();
        Try.ofSupplier(Bulkhead.decorateSupplier(bulkhead, helloWorldService::returnHelloWorld));
        // Then
        BDDMockito.then(logger).should(Mockito.times(1)).info("CALL_REJECTED");
    }

    @Test
    public void shouldConsumeOnCallFinishedEventWhenExecutionIsFinished() throws Exception {
        // Given
        Bulkhead bulkhead = Bulkhead.of("test", config);
        // When
        bulkhead.getEventPublisher().onCallFinished(( event) -> logger.info(event.getEventType().toString()));
        Try.ofSupplier(Bulkhead.decorateSupplier(bulkhead, helloWorldService::returnHelloWorld));
        // Then
        BDDMockito.then(logger).should(Mockito.times(1)).info("CALL_FINISHED");
    }

    @Test
    public void shouldConsumeOnCallFinishedEventOnComplete() throws Exception {
        // Given
        Bulkhead bulkhead = Bulkhead.of("test", config);
        // When
        bulkhead.getEventPublisher().onCallFinished(( event) -> logger.info(event.getEventType().toString()));
        bulkhead.onComplete();
        // Then
        BDDMockito.then(logger).should(Mockito.times(1)).info("CALL_FINISHED");
    }
}

