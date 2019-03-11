package io.github.resilience4j.ratelimiter.operator;


import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.reactivex.Flowable;
import java.io.IOException;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;


/**
 * Unit test for {@link RateLimiterSubscriber}.
 */
@SuppressWarnings("unchecked")
public class RateLimiterSubscriberTest extends RateLimiterAssertions {
    @Test
    public void shouldEmitSingleEventWithSinglePermit() {
        Flowable.just(1).lift(RateLimiterOperator.of(rateLimiter)).test().assertResult(1);
        assertSinglePermitUsed();
    }

    @Test
    public void shouldEmitAllEvents() {
        Flowable.fromArray(1, 2).lift(RateLimiterOperator.of(rateLimiter)).test().assertResult(1, 2);
        assertUsedPermits(2);
    }

    @Test
    public void shouldPropagateError() {
        Flowable.error(new IOException("BAM!")).lift(RateLimiterOperator.of(rateLimiter)).test().assertSubscribed().assertError(IOException.class).assertNotComplete();
        assertSinglePermitUsed();
    }

    @Test
    public void shouldEmitErrorWithRequestNotPermittedException() {
        saturateRateLimiter();
        Flowable.just(1).lift(RateLimiterOperator.of(rateLimiter)).test().assertSubscribed().assertError(RequestNotPermitted.class).assertNotComplete();
        assertNoPermitLeft();
    }

    @Test
    public void shouldHonorCancelledWhenCallingOnNext() throws Exception {
        // Given
        Subscription subscription = Mockito.mock(Subscription.class);
        Subscriber childSubscriber = Mockito.mock(Subscriber.class);
        Subscriber decoratedSubscriber = RateLimiterOperator.of(rateLimiter).apply(childSubscriber);
        decoratedSubscriber.onSubscribe(subscription);
        // When
        cancel();
        decoratedSubscriber.onNext(1);
        // Then
        Mockito.verify(childSubscriber, Mockito.never()).onNext(ArgumentMatchers.any());
        assertSinglePermitUsed();
    }

    @Test
    public void shouldHonorCancelledWhenCallingOnError() throws Exception {
        // Given
        Subscription subscription = Mockito.mock(Subscription.class);
        Subscriber childSubscriber = Mockito.mock(Subscriber.class);
        Subscriber decoratedSubscriber = RateLimiterOperator.of(rateLimiter).apply(childSubscriber);
        decoratedSubscriber.onSubscribe(subscription);
        // When
        cancel();
        decoratedSubscriber.onError(new IllegalStateException());
        // Then
        Mockito.verify(childSubscriber, Mockito.never()).onError(ArgumentMatchers.any());
        assertSinglePermitUsed();
    }

    @Test
    public void shouldHonorCancelledWhenCallingOnComplete() throws Exception {
        // Given
        Subscription subscription = Mockito.mock(Subscription.class);
        Subscriber childSubscriber = Mockito.mock(Subscriber.class);
        Subscriber decoratedSubscriber = RateLimiterOperator.of(rateLimiter).apply(childSubscriber);
        decoratedSubscriber.onSubscribe(subscription);
        // When
        cancel();
        decoratedSubscriber.onComplete();
        // Then
        Mockito.verify(childSubscriber, Mockito.never()).onComplete();
        assertSinglePermitUsed();
    }
}

