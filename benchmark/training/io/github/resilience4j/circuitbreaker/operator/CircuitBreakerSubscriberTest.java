package io.github.resilience4j.circuitbreaker.operator;


import io.github.resilience4j.circuitbreaker.CircuitBreakerOpenException;
import io.reactivex.Flowable;
import java.io.IOException;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;


/**
 * Unit test for {@link CircuitBreakerSubscriber}.
 */
@SuppressWarnings("unchecked")
public class CircuitBreakerSubscriberTest extends CircuitBreakerAssertions {
    @Test
    public void shouldEmitAllEvents() {
        Flowable.fromArray("Event 1", "Event 2").lift(CircuitBreakerOperator.of(circuitBreaker)).test().assertResult("Event 1", "Event 2");
        assertSingleSuccessfulCall();
    }

    @Test
    public void shouldPropagateError() {
        Flowable.error(new IOException("BAM!")).lift(CircuitBreakerOperator.of(circuitBreaker)).test().assertSubscribed().assertError(IOException.class).assertNotComplete();
        assertSingleFailedCall();
    }

    @Test
    public void shouldEmitErrorWithCircuitBreakerOpenException() {
        circuitBreaker.transitionToOpenState();
        Flowable.fromArray("Event 1", "Event 2").lift(CircuitBreakerOperator.of(circuitBreaker)).test().assertSubscribed().assertError(CircuitBreakerOpenException.class).assertNotComplete();
        assertNoRegisteredCall();
    }

    @Test
    public void shouldHonorDisposedWhenCallingOnNext() throws Exception {
        // Given
        Subscription subscription = Mockito.mock(Subscription.class);
        Subscriber childSubscriber = Mockito.mock(Subscriber.class);
        Subscriber decoratedSubscriber = CircuitBreakerOperator.of(circuitBreaker).apply(childSubscriber);
        decoratedSubscriber.onSubscribe(subscription);
        // When
        decoratedSubscriber.onNext("one");
        cancel();
        decoratedSubscriber.onNext("two");
        // Then
        Mockito.verify(childSubscriber, Mockito.times(1)).onNext(ArgumentMatchers.any());
        assertNoRegisteredCall();
    }

    @Test
    public void shouldHonorDisposedWhenCallingOnComplete() throws Exception {
        // Given
        Subscription subscription = Mockito.mock(Subscription.class);
        Subscriber childSubscriber = Mockito.mock(Subscriber.class);
        Subscriber decoratedSubscriber = CircuitBreakerOperator.of(circuitBreaker).apply(childSubscriber);
        decoratedSubscriber.onSubscribe(subscription);
        // When
        cancel();
        decoratedSubscriber.onComplete();
        // Then
        Mockito.verify(childSubscriber, Mockito.never()).onComplete();
        assertSingleSuccessfulCall();
    }

    @Test
    public void shouldHonorDisposedWhenCallingOnError() throws Exception {
        // Given
        Subscription subscription = Mockito.mock(Subscription.class);
        Subscriber childSubscriber = Mockito.mock(Subscriber.class);
        Subscriber decoratedSubscriber = CircuitBreakerOperator.of(circuitBreaker).apply(childSubscriber);
        decoratedSubscriber.onSubscribe(subscription);
        // When
        cancel();
        decoratedSubscriber.onError(new IllegalStateException());
        // Then
        Mockito.verify(childSubscriber, Mockito.never()).onError(ArgumentMatchers.any());
        assertSingleFailedCall();
    }

    @Test
    public void shouldNotAffectCircuitBreakerWhenWasCancelledAfterNotPermittedSubscribe() throws Exception {
        // Given
        Subscription subscription = Mockito.mock(Subscription.class);
        Subscriber childObserver = Mockito.mock(Subscriber.class);
        Subscriber decoratedObserver = CircuitBreakerOperator.of(circuitBreaker).apply(childObserver);
        circuitBreaker.transitionToOpenState();
        decoratedObserver.onSubscribe(subscription);
        // When
        cancel();
        // Then
        assertNoRegisteredCall();
    }
}

