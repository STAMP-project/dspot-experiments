package io.github.resilience4j.bulkhead.operator;


import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.bulkhead.BulkheadFullException;
import io.reactivex.Flowable;
import java.io.IOException;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;


/**
 * Unit test for {@link BulkheadSubscriber} using {@link BulkheadOperator}.
 */
@SuppressWarnings("unchecked")
public class BulkheadSubscriberTest {
    private Bulkhead bulkhead = Bulkhead.of("test", BulkheadConfig.custom().maxConcurrentCalls(1).maxWaitTime(0).build());

    @Test
    public void shouldEmitAllEvents() {
        Flowable.fromArray("Event 1", "Event 2").lift(BulkheadOperator.of(bulkhead)).test().assertResult("Event 1", "Event 2");
        assertThat(bulkhead.getMetrics().getAvailableConcurrentCalls()).isEqualTo(1);
    }

    @Test
    public void shouldPropagateError() {
        Flowable.error(new IOException("BAM!")).lift(BulkheadOperator.of(bulkhead)).test().assertSubscribed().assertError(IOException.class).assertNotComplete();
        assertThat(bulkhead.getMetrics().getAvailableConcurrentCalls()).isEqualTo(1);
    }

    @Test
    public void shouldEmitErrorWithBulkheadFullException() {
        bulkhead.isCallPermitted();
        Flowable.fromArray("Event 1", "Event 2").lift(BulkheadOperator.of(bulkhead)).test().assertSubscribed().assertError(BulkheadFullException.class).assertNotComplete();
        assertThat(bulkhead.getMetrics().getAvailableConcurrentCalls()).isEqualTo(0);
    }

    @Test
    public void shouldHonorCancelledWhenCallingOnNext() throws Exception {
        // Given
        Subscription subscription = Mockito.mock(Subscription.class);
        Subscriber childSubscriber = Mockito.mock(Subscriber.class);
        Subscriber decoratedSubscriber = BulkheadOperator.of(bulkhead).apply(childSubscriber);
        decoratedSubscriber.onSubscribe(subscription);
        // When
        decoratedSubscriber.onNext("one");
        cancel();
        decoratedSubscriber.onNext("two");
        // Then
        Mockito.verify(childSubscriber, Mockito.times(1)).onNext(ArgumentMatchers.any());
        assertThat(bulkhead.getMetrics().getAvailableConcurrentCalls()).isEqualTo(1);
    }

    @Test
    public void shouldHonorCancelledWhenCallingOnError() throws Exception {
        // Given
        Subscription subscription = Mockito.mock(Subscription.class);
        Subscriber childSubscriber = Mockito.mock(Subscriber.class);
        Subscriber decoratedSubscriber = BulkheadOperator.of(bulkhead).apply(childSubscriber);
        decoratedSubscriber.onSubscribe(subscription);
        // When
        cancel();
        decoratedSubscriber.onError(new IllegalStateException());
        // Then
        Mockito.verify(childSubscriber, Mockito.never()).onError(ArgumentMatchers.any());
        assertThat(bulkhead.getMetrics().getAvailableConcurrentCalls()).isEqualTo(1);
    }

    @Test
    public void shouldHonorCancelledWhenCallingOnComplete() throws Exception {
        // Given
        Subscription subscription = Mockito.mock(Subscription.class);
        Subscriber childSubscriber = Mockito.mock(Subscriber.class);
        Subscriber decoratedSubscriber = BulkheadOperator.of(bulkhead).apply(childSubscriber);
        decoratedSubscriber.onSubscribe(subscription);
        // When
        cancel();
        decoratedSubscriber.onComplete();
        // Then
        Mockito.verify(childSubscriber, Mockito.never()).onComplete();
        assertThat(bulkhead.getMetrics().getAvailableConcurrentCalls()).isEqualTo(1);
    }

    @Test
    public void shouldNotReleaseBulkheadWhenWasCancelledAfterNotPermittedSubscribe() throws Exception {
        // Given
        Subscription subscription = Mockito.mock(Subscription.class);
        Subscriber childObserver = Mockito.mock(Subscriber.class);
        Subscriber decoratedObserver = BulkheadOperator.of(bulkhead).apply(childObserver);
        bulkhead.isCallPermitted();
        assertThat(bulkhead.getMetrics().getAvailableConcurrentCalls()).isEqualTo(0);
        decoratedObserver.onSubscribe(subscription);
        // When
        cancel();
        // Then
        assertThat(bulkhead.getMetrics().getAvailableConcurrentCalls()).isEqualTo(0);
    }
}

