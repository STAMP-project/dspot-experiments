package io.github.resilience4j.circuitbreaker.operator;


import io.github.resilience4j.circuitbreaker.CircuitBreakerOpenException;
import io.reactivex.Maybe;
import io.reactivex.MaybeObserver;
import io.reactivex.disposables.Disposable;
import java.io.IOException;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Unit test for {@link CircuitBreakerMaybeObserver}.
 */
@SuppressWarnings("unchecked")
public class CircuitBreakerMaybeObserverTest extends CircuitBreakerAssertions {
    @Test
    public void shouldEmitAllEvents() {
        Maybe.just(1).lift(CircuitBreakerOperator.of(circuitBreaker)).test().assertResult(1);
        assertSingleSuccessfulCall();
    }

    @Test
    public void shouldPropagateError() {
        Maybe.error(new IOException("BAM!")).lift(CircuitBreakerOperator.of(circuitBreaker)).test().assertSubscribed().assertError(IOException.class).assertNotComplete();
        assertSingleFailedCall();
    }

    @Test
    public void shouldEmitErrorWithCircuitBreakerOpenException() {
        circuitBreaker.transitionToOpenState();
        Maybe.just(1).lift(CircuitBreakerOperator.of(circuitBreaker)).test().assertSubscribed().assertError(CircuitBreakerOpenException.class).assertNotComplete();
        assertNoRegisteredCall();
    }

    @Test
    public void shouldHonorDisposedWhenCallingOnSuccess() throws Exception {
        // Given
        Disposable disposable = Mockito.mock(Disposable.class);
        MaybeObserver childObserver = Mockito.mock(MaybeObserver.class);
        MaybeObserver decoratedObserver = CircuitBreakerOperator.of(circuitBreaker).apply(childObserver);
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
        MaybeObserver childObserver = Mockito.mock(MaybeObserver.class);
        MaybeObserver decoratedObserver = CircuitBreakerOperator.of(circuitBreaker).apply(childObserver);
        decoratedObserver.onSubscribe(disposable);
        // When
        dispose();
        decoratedObserver.onError(new IllegalStateException());
        // Then
        Mockito.verify(childObserver, Mockito.never()).onError(ArgumentMatchers.any());
        assertSingleFailedCall();
    }

    @Test
    public void shouldHonorDisposedWhenCallingOnComplete() throws Exception {
        // Given
        Disposable disposable = Mockito.mock(Disposable.class);
        MaybeObserver childObserver = Mockito.mock(MaybeObserver.class);
        MaybeObserver decoratedObserver = CircuitBreakerOperator.of(circuitBreaker).apply(childObserver);
        decoratedObserver.onSubscribe(disposable);
        // When
        dispose();
        decoratedObserver.onComplete();
        // Then
        Mockito.verify(childObserver, Mockito.never()).onComplete();
        assertSingleSuccessfulCall();
    }

    @Test
    public void shouldNotAffectCircuitBreakerWhenWasDisposedAfterNotPermittedSubscribe() throws Exception {
        // Given
        Disposable disposable = Mockito.mock(Disposable.class);
        MaybeObserver childObserver = Mockito.mock(MaybeObserver.class);
        MaybeObserver decoratedObserver = CircuitBreakerOperator.of(circuitBreaker).apply(childObserver);
        circuitBreaker.transitionToOpenState();
        decoratedObserver.onSubscribe(disposable);
        // When
        dispose();
        // Then
        assertNoRegisteredCall();
    }
}

