package io.github.resilience4j.bulkhead.operator;


import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.bulkhead.BulkheadFullException;
import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.disposables.Disposable;
import java.io.IOException;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Unit test for {@link BulkheadCompletableObserver} using {@link BulkheadOperator}.
 */
@SuppressWarnings("unchecked")
public class BulkheadCompletableObserverTest {
    private Bulkhead bulkhead = Bulkhead.of("test", BulkheadConfig.custom().maxConcurrentCalls(1).maxWaitTime(0).build());

    @Test
    public void shouldComplete() {
        Completable.complete().lift(BulkheadOperator.of(bulkhead)).test().assertSubscribed().assertComplete();
        assertThat(bulkhead.getMetrics().getAvailableConcurrentCalls()).isEqualTo(1);
    }

    @Test
    public void shouldPropagateError() {
        Completable.error(new IOException("BAM!")).lift(BulkheadOperator.of(bulkhead)).test().assertSubscribed().assertError(IOException.class).assertNotComplete();
        assertThat(bulkhead.getMetrics().getAvailableConcurrentCalls()).isEqualTo(1);
    }

    @Test
    public void shouldEmitErrorWithBulkheadFullException() {
        bulkhead.isCallPermitted();
        Completable.complete().lift(BulkheadOperator.of(bulkhead)).test().assertSubscribed().assertError(BulkheadFullException.class).assertNotComplete();
        assertThat(bulkhead.getMetrics().getAvailableConcurrentCalls()).isEqualTo(0);
    }

    @Test
    public void shouldHonorDisposedWhenCallingOnComplete() throws Exception {
        // Given
        Disposable disposable = Mockito.mock(Disposable.class);
        CompletableObserver childObserver = Mockito.mock(CompletableObserver.class);
        CompletableObserver decoratedObserver = BulkheadOperator.of(bulkhead).apply(childObserver);
        decoratedObserver.onSubscribe(disposable);
        // When
        dispose();
        decoratedObserver.onComplete();
        // Then
        Mockito.verify(childObserver, Mockito.never()).onComplete();
        assertThat(bulkhead.getMetrics().getAvailableConcurrentCalls()).isEqualTo(1);
    }

    @Test
    public void shouldHonorDisposedWhenCallingOnError() throws Exception {
        // Given
        Disposable disposable = Mockito.mock(Disposable.class);
        CompletableObserver childObserver = Mockito.mock(CompletableObserver.class);
        CompletableObserver decoratedObserver = BulkheadOperator.of(bulkhead).apply(childObserver);
        decoratedObserver.onSubscribe(disposable);
        // When
        dispose();
        decoratedObserver.onError(new IllegalStateException());
        // Then
        Mockito.verify(childObserver, Mockito.never()).onError(ArgumentMatchers.any());
        assertThat(bulkhead.getMetrics().getAvailableConcurrentCalls()).isEqualTo(1);
    }

    @Test
    public void shouldNotReleaseBulkheadWhenWasDisposedAfterNotPermittedSubscribe() throws Exception {
        // Given
        Disposable disposable = Mockito.mock(Disposable.class);
        CompletableObserver childObserver = Mockito.mock(CompletableObserver.class);
        CompletableObserver decoratedObserver = BulkheadOperator.of(bulkhead).apply(childObserver);
        bulkhead.isCallPermitted();
        assertThat(bulkhead.getMetrics().getAvailableConcurrentCalls()).isEqualTo(0);
        decoratedObserver.onSubscribe(disposable);
        // When
        dispose();
        // Then
        assertThat(bulkhead.getMetrics().getAvailableConcurrentCalls()).isEqualTo(0);
    }
}

