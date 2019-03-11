package io.github.resilience4j.bulkhead.operator;


import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.bulkhead.BulkheadFullException;
import io.reactivex.Maybe;
import io.reactivex.MaybeObserver;
import io.reactivex.disposables.Disposable;
import java.io.IOException;
import java.util.Arrays;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Unit test for {@link BulkheadMaybeObserver} using {@link BulkheadOperator}.
 */
@SuppressWarnings("unchecked")
public class BulkheadMaybeObserverTest {
    private Bulkhead bulkhead = Bulkhead.of("test", BulkheadConfig.custom().maxConcurrentCalls(1).maxWaitTime(0).build());

    @Test
    public void shouldEmitAllEvents() {
        Maybe.just(1).lift(BulkheadOperator.of(bulkhead)).test().assertResult(1);
        assertThat(bulkhead.getMetrics().getAvailableConcurrentCalls()).isEqualTo(1);
    }

    @Test
    public void shouldPropagateError() {
        Maybe.error(new IOException("BAM!")).lift(BulkheadOperator.of(bulkhead)).test().assertSubscribed().assertError(IOException.class).assertNotComplete();
        assertThat(bulkhead.getMetrics().getAvailableConcurrentCalls()).isEqualTo(1);
    }

    @Test
    public void shouldEmitErrorWithBulkheadFullException() {
        bulkhead.isCallPermitted();
        Maybe.just(1).lift(BulkheadOperator.of(bulkhead)).test().assertSubscribed().assertError(BulkheadFullException.class).assertNotComplete();
        assertThat(bulkhead.getMetrics().getAvailableConcurrentCalls()).isEqualTo(0);
    }

    @Test
    public void shouldHonorDisposedWhenCallingOnSuccess() throws Exception {
        // Given
        Disposable disposable = Mockito.mock(Disposable.class);
        MaybeObserver childObserver = Mockito.mock(MaybeObserver.class);
        MaybeObserver decoratedObserver = BulkheadOperator.of(bulkhead).apply(childObserver);
        decoratedObserver.onSubscribe(disposable);
        // When
        dispose();
        decoratedObserver.onSuccess(1);
        // Then
        Mockito.verify(childObserver, Mockito.never()).onSuccess(ArgumentMatchers.any());
        assertThat(bulkhead.getMetrics().getAvailableConcurrentCalls()).isEqualTo(1);
    }

    @Test
    public void shouldHonorDisposedWhenCallingOnError() throws Exception {
        // Given
        Disposable disposable = Mockito.mock(Disposable.class);
        MaybeObserver childObserver = Mockito.mock(MaybeObserver.class);
        MaybeObserver decoratedObserver = BulkheadOperator.of(bulkhead).apply(childObserver);
        decoratedObserver.onSubscribe(disposable);
        // When
        dispose();
        decoratedObserver.onError(new IllegalStateException());
        // Then
        Mockito.verify(childObserver, Mockito.never()).onError(ArgumentMatchers.any());
        assertThat(bulkhead.getMetrics().getAvailableConcurrentCalls()).isEqualTo(1);
    }

    @Test
    public void shouldHonorDisposedWhenCallingOnComplete() throws Exception {
        // Given
        Disposable disposable = Mockito.mock(Disposable.class);
        MaybeObserver childObserver = Mockito.mock(MaybeObserver.class);
        MaybeObserver decoratedObserver = BulkheadOperator.of(bulkhead).apply(childObserver);
        decoratedObserver.onSubscribe(disposable);
        // When
        dispose();
        decoratedObserver.onComplete();
        // Then
        Mockito.verify(childObserver, Mockito.never()).onComplete();
        assertThat(bulkhead.getMetrics().getAvailableConcurrentCalls()).isEqualTo(1);
    }

    @Test
    public void shouldNotReleaseBulkheadWhenWasDisposedAfterNotPermittedSubscribe() throws Exception {
        // Given
        Disposable disposable = Mockito.mock(Disposable.class);
        MaybeObserver childObserver = Mockito.mock(MaybeObserver.class);
        MaybeObserver decoratedObserver = BulkheadOperator.of(bulkhead).apply(childObserver);
        bulkhead.isCallPermitted();
        assertThat(bulkhead.getMetrics().getAvailableConcurrentCalls()).isEqualTo(0);
        decoratedObserver.onSubscribe(disposable);
        // When
        dispose();
        // Then
        assertThat(bulkhead.getMetrics().getAvailableConcurrentCalls()).isEqualTo(0);
    }

    @Test
    public void shouldReleaseBulkheadOnlyOnce() {
        // this with the previous line triggers an extra dispose
        Maybe.just(Arrays.asList(1, 2, 3)).lift(BulkheadOperator.of(bulkhead)).flatMapObservable(Observable::fromIterable).take(2).test().assertResult(1, 2);
        assertThat(bulkhead.getMetrics().getAvailableConcurrentCalls()).isEqualTo(1);
    }
}

