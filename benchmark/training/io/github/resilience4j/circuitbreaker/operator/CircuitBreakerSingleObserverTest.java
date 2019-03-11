package io.github.resilience4j.circuitbreaker.operator;


import io.github.resilience4j.circuitbreaker.CircuitBreakerOpenException;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import java.io.IOException;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Unit test for {@link CircuitBreakerSingleObserver}.
 */
@SuppressWarnings("unchecked")
public class CircuitBreakerSingleObserverTest extends CircuitBreakerAssertions {
    @Test
    public void shouldEmitAllEvents() {
        Single.just(1).lift(CircuitBreakerOperator.of(circuitBreaker)).test().assertResult(1);
        assertSingleSuccessfulCall();
    }

    @Test
    public void shouldPropagateError() {
        Single.error(new IOException("BAM!")).lift(CircuitBreakerOperator.of(circuitBreaker)).test().assertSubscribed().assertError(IOException.class).assertNotComplete();
        assertSingleFailedCall();
    }

    @Test
    public void shouldEmitErrorWithCircuitBreakerOpenException() {
        circuitBreaker.transitionToOpenState();
        Single.just(1).lift(CircuitBreakerOperator.of(circuitBreaker)).test().assertSubscribed().assertError(CircuitBreakerOpenException.class).assertNotComplete();
        assertNoRegisteredCall();
    }

    @Test
    public void shouldHonorDisposedWhenCallingOnSuccess() throws Exception {
        // Given
        Disposable disposable = Mockito.mock(Disposable.class);
        SingleObserver childObserver = Mockito.mock(SingleObserver.class);
        SingleObserver decoratedObserver = CircuitBreakerOperator.of(circuitBreaker).apply(childObserver);
        decoratedObserver.onSubscribe(disposable);
        // When
        dispose();
        decoratedObserver.onSuccess(1);
        // Then
        Mockito.verify(childObserver, Mockito.never()).onSuccess(ArgumentMatchers.any());
        assertSingleSuccessfulCall();
    }

    @Test
    public void shouldHonorDisposedWhenCallingOnError() throws Exception {
        // Given
        Disposable disposable = Mockito.mock(Disposable.class);
        SingleObserver childObserver = Mockito.mock(SingleObserver.class);
        SingleObserver decoratedObserver = CircuitBreakerOperator.of(circuitBreaker).apply(childObserver);
        decoratedObserver.onSubscribe(disposable);
        // When
        dispose();
        decoratedObserver.onError(new IllegalStateException());
        // Then
        Mockito.verify(childObserver, Mockito.never()).onError(ArgumentMatchers.any());
        assertSingleFailedCall();
    }

    @Test
    public void shouldNotReleaseBulkheadWhenWasDisposedAfterNotPermittedSubscribe() throws Exception {
        // Given
        Disposable disposable = Mockito.mock(Disposable.class);
        SingleObserver childObserver = Mockito.mock(SingleObserver.class);
        SingleObserver decoratedObserver = CircuitBreakerOperator.of(circuitBreaker).apply(childObserver);
        circuitBreaker.transitionToOpenState();
        decoratedObserver.onSubscribe(disposable);
        // When
        dispose();
        // Then
        assertNoRegisteredCall();
    }
}

