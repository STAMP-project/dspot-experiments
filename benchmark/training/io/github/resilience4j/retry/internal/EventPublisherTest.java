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
package io.github.resilience4j.retry.internal;


import RetryConfig.DEFAULT_WAIT_DURATION;
import RetryEvent.Type;
import RetryEvent.Type.ERROR;
import RetryEvent.Type.IGNORED_ERROR;
import RetryEvent.Type.RETRY;
import RetryEvent.Type.SUCCESS;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.event.RetryEvent;
import io.github.resilience4j.test.HelloWorldService;
import io.reactivex.subscribers.TestSubscriber;
import io.vavr.CheckedRunnable;
import io.vavr.control.Try;
import java.io.IOException;
import javax.xml.ws.WebServiceException;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;


public class EventPublisherTest {
    private HelloWorldService helloWorldService;

    private long sleptTime = 0L;

    @Test
    public void shouldReturnAfterThreeAttempts() {
        // Given the HelloWorldService throws an exception
        BDDMockito.willThrow(new WebServiceException("BAM!")).given(helloWorldService).sayHelloWorld();
        // Create a Retry with default configuration
        Retry retry = Retry.ofDefaults("id");
        TestSubscriber<RetryEvent.Type> testSubscriber = toFlowable(retry.getEventPublisher()).map(RetryEvent::getEventType).test();
        // Decorate the invocation of the HelloWorldService
        CheckedRunnable retryableRunnable = Retry.decorateCheckedRunnable(retry, helloWorldService::sayHelloWorld);
        // When
        Try<Void> result = Try.run(retryableRunnable);
        // Then the helloWorldService should be invoked 3 times
        BDDMockito.then(helloWorldService).should(Mockito.times(3)).sayHelloWorld();
        // and the result should be a failure
        Assertions.assertThat(result.isFailure()).isTrue();
        // and the returned exception should be of type RuntimeException
        Assertions.assertThat(result.failed().get()).isInstanceOf(WebServiceException.class);
        Assertions.assertThat(sleptTime).isEqualTo(((RetryConfig.DEFAULT_WAIT_DURATION) * 2));
        testSubscriber.assertValueCount(3).assertValues(RETRY, RETRY, ERROR);
    }

    @Test
    public void shouldReturnAfterTwoAttempts() {
        // Given the HelloWorldService throws an exception
        BDDMockito.willThrow(new WebServiceException("BAM!")).willDoNothing().given(helloWorldService).sayHelloWorld();
        // Create a Retry with default configuration
        Retry retry = Retry.ofDefaults("id");
        TestSubscriber<RetryEvent.Type> testSubscriber = toFlowable(retry.getEventPublisher()).map(RetryEvent::getEventType).test();
        // Decorate the invocation of the HelloWorldService
        CheckedRunnable retryableRunnable = Retry.decorateCheckedRunnable(retry, helloWorldService::sayHelloWorld);
        // When
        Try<Void> result = Try.run(retryableRunnable);
        // Then the helloWorldService should be invoked 2 times
        BDDMockito.then(helloWorldService).should(Mockito.times(2)).sayHelloWorld();
        // and the result should be a sucess
        Assertions.assertThat(result.isSuccess()).isTrue();
        Assertions.assertThat(sleptTime).isEqualTo(DEFAULT_WAIT_DURATION);
        testSubscriber.assertValueCount(2).assertValues(RETRY, SUCCESS);
    }

    @Test
    public void shouldIgnoreError() {
        // Given the HelloWorldService throws an exception
        BDDMockito.willThrow(new WebServiceException("BAM!")).willDoNothing().given(helloWorldService).sayHelloWorld();
        // Create a Retry with default configuration
        RetryConfig config = RetryConfig.custom().retryOnException(( t) -> t instanceof IOException).maxAttempts(3).build();
        Retry retry = Retry.of("id", config);
        TestSubscriber<RetryEvent.Type> testSubscriber = toFlowable(retry.getEventPublisher()).map(RetryEvent::getEventType).test();
        // Decorate the invocation of the HelloWorldService
        CheckedRunnable retryableRunnable = Retry.decorateCheckedRunnable(retry, helloWorldService::sayHelloWorld);
        // When
        Try<Void> result = Try.run(retryableRunnable);
        // Then the helloWorldService should be invoked 2 times
        BDDMockito.then(helloWorldService).should(Mockito.times(1)).sayHelloWorld();
        // and the result should be a sucess
        Assertions.assertThat(result.isFailure()).isTrue();
        Assertions.assertThat(sleptTime).isEqualTo(0);
        testSubscriber.assertValueCount(1).assertValues(IGNORED_ERROR);
    }
}

