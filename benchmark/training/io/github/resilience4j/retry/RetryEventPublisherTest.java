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
package io.github.resilience4j.retry;


import Retry.EventPublisher;
import io.github.resilience4j.test.HelloWorldService;
import io.vavr.control.Try;
import javax.xml.ws.WebServiceException;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.slf4j.Logger;


public class RetryEventPublisherTest {
    private HelloWorldService helloWorldService;

    private Logger logger;

    private Retry retry;

    @Test
    public void shouldReturnTheSameConsumer() {
        Retry.EventPublisher eventPublisher = retry.getEventPublisher();
        Retry.EventPublisher eventPublisher2 = retry.getEventPublisher();
        assertThat(eventPublisher).isEqualTo(eventPublisher2);
    }

    @Test
    public void shouldConsumeOnSuccessEvent() {
        // Given the HelloWorldService returns Hello world
        BDDMockito.given(helloWorldService.returnHelloWorld()).willThrow(new WebServiceException("BAM!")).willReturn("Hello world");
        retry.getEventPublisher().onSuccess(( event) -> logger.info(event.getEventType().toString()));
        retry.executeSupplier(helloWorldService::returnHelloWorld);
        BDDMockito.then(helloWorldService).should(Mockito.times(2)).returnHelloWorld();
        BDDMockito.then(logger).should(Mockito.times(1)).info("SUCCESS");
    }

    @Test
    public void shouldConsumeOnRetryEvent() {
        BDDMockito.given(helloWorldService.returnHelloWorld()).willThrow(new WebServiceException("BAM!"));
        retry.getEventPublisher().onRetry(( event) -> logger.info(event.getEventType().toString()));
        Try.ofSupplier(Retry.decorateSupplier(retry, helloWorldService::returnHelloWorld));
        BDDMockito.then(helloWorldService).should(Mockito.times(3)).returnHelloWorld();
        BDDMockito.then(logger).should(Mockito.times(2)).info("RETRY");
    }

    @Test
    public void shouldConsumeOnErrorEvent() {
        BDDMockito.given(helloWorldService.returnHelloWorld()).willThrow(new WebServiceException("BAM!"));
        retry.getEventPublisher().onError(( event) -> logger.info(event.getEventType().toString()));
        Try.ofSupplier(Retry.decorateSupplier(retry, helloWorldService::returnHelloWorld));
        BDDMockito.then(logger).should(Mockito.times(1)).info("ERROR");
        BDDMockito.then(helloWorldService).should(Mockito.times(3)).returnHelloWorld();
    }

    @Test
    public void shouldConsumeIgnoredErrorEvent() {
        BDDMockito.given(helloWorldService.returnHelloWorld()).willThrow(new WebServiceException("BAM!"));
        RetryConfig retryConfig = RetryConfig.custom().retryOnException(( throwable) -> Match(throwable).of(Case($(instanceOf(.class)), false), Case($(), true))).build();
        retry = Retry.of("testName", retryConfig);
        retry.getEventPublisher().onIgnoredError(( event) -> logger.info(event.getEventType().toString()));
        Try.ofSupplier(Retry.decorateSupplier(retry, helloWorldService::returnHelloWorld));
        BDDMockito.then(logger).should(Mockito.times(1)).info("IGNORED_ERROR");
        BDDMockito.then(helloWorldService).should(Mockito.times(1)).returnHelloWorld();
    }
}

