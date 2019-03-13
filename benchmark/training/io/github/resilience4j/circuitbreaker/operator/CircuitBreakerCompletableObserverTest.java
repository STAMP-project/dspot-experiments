package io.github.resilience4j.circuitbreaker.operator;


import io.github.resilience4j.circuitbreaker.CircuitBreakerOpenException;
import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.disposables.Disposable;
import java.io.IOException;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Unit test for {@link CircuitBreakerCompletableObserver}.
 */
public class CircuitBreakerCompletableObserverTest extends CircuitBreakerAssertions {
    @Test
    public void shouldCompleteAndMarkSuccess() {
        Completable.complete().lift(CircuitBreakerOperator.of(circuitBreaker)).test().assertSubscribed().assertComplete();
        assertSingleSuccessfulCall();
    }

    @Test
    public void shouldPropagateAndMarkError() {
        Completable.error(new IOException("BAM!")).lift(CircuitBreakerOperator.of(circuitBreaker)).test().assertSubscribed().assertError(IOException.class).assertNotComplete();
        assertSingleFailedCall();
    }

    @Test
    public void shouldEmitErrorWithCircuitBreakerOpenException() {
        circuitBreaker.transitionToOpenState();
        Completable.complete().lift(CircuitBreakerOperator.of(circuitBreaker)).test().assertSubscribed().assertError(CircuitBreakerOpenException.class).assertNotComplete();
        assertNoRegisteredCall();
    }

    @Test
    public void shouldHonorDisposedWhenCallingOnComplete() throws Exception {
        // Given
        Disposable disposable = Mockito.mock(Disposable.class);
        CompletableObserver childObserver = Mockito.mock(CompletableObserver.class);
        CompletableObserver decoratedObserver = CircuitBreakerOperator.of(circuitBreaker).apply(childObserver);
        decoratedObserver.onSubscribe(disposable);
        // When
        dispose();
        decoratedObserver.onComplete();
        // Then
        Mockito.verify(childObserver, Mockito.never()).onComplete();
        assertSingleSuccessfulCall();
    }

    @Test
    public void shouldHonorDisposedWhenCallingOnError() throws Exception {
        // Given
        Disposable disposable = Mockito.mock(Disposable.class);
        CompletableObserver childObserver = Mockito.mock(CompletableObserver.class);
        CompletableObserver decoratedObserver = CircuitBreakerOperator.of(circuitBreaker).apply(childObserver);
        decoratedObserver.onSubscribe(disposable);
        // When
        dispose();
        decoratedObserver.onError(new IllegalStateException());
        // Then
        Mockito.verify(childObserver, Mockito.never()).onError(ArgumentMatchers.any());
        assertSingleFailedCall();
    }

    @Test
    public void shouldNotAffectCircuitBreakerWhenWasDisposedAfterNotPermittedSubscribe() throws Exception {
        // Given
        Disposable disposable = Mockito.mock(Disposable.class);
        CompletableObserver childObserver = Mockito.mock(CompletableObserver.class);
        CompletableObserver decoratedObserver = CircuitBreakerOperator.of(circuitBreaker).apply(childObserver);
        circuitBreaker.transitionToOpenState();
        decoratedObserver.onSubscribe(disposable);
        // When
        dispose();
        // Then
        assertNoRegisteredCall();
    }
}

