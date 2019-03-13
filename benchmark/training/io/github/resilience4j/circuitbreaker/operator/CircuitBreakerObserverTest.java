package io.github.resilience4j.circuitbreaker.operator;


import io.github.resilience4j.circuitbreaker.CircuitBreakerOpenException;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import java.io.IOException;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Unit test for {@link CircuitBreakerObserver}.
 */
@SuppressWarnings("unchecked")
public class CircuitBreakerObserverTest extends CircuitBreakerAssertions {
    @Test
    public void shouldEmitAllEvents() {
        Observable.fromArray("Event 1", "Event 2").lift(CircuitBreakerOperator.of(circuitBreaker)).test().assertResult("Event 1", "Event 2");
        assertSingleSuccessfulCall();
    }

    @Test
    public void shouldPropagateError() {
        Observable.error(new IOException("BAM!")).lift(CircuitBreakerOperator.of(circuitBreaker)).test().assertSubscribed().assertError(IOException.class).assertNotComplete();
        assertSingleFailedCall();
    }

    @Test
    public void shouldEmitErrorWithCircuitBreakerOpenException() {
        circuitBreaker.transitionToOpenState();
        Observable.fromArray("Event 1", "Event 2").lift(CircuitBreakerOperator.of(circuitBreaker)).test().assertSubscribed().assertError(CircuitBreakerOpenException.class).assertNotComplete();
        assertNoRegisteredCall();
    }

    @Test
    public void shouldHonorDisposedWhenCallingOnNext() throws Exception {
        // Given
        Disposable disposable = Mockito.mock(Disposable.class);
        Observer childObserver = Mockito.mock(Observer.class);
        Observer decoratedObserver = CircuitBreakerOperator.of(circuitBreaker).apply(childObserver);
        decoratedObserver.onSubscribe(disposable);
        // When
        decoratedObserver.onNext("one");
        dispose();
        decoratedObserver.onNext("two");
        // Then
        Mockito.verify(childObserver, Mockito.times(1)).onNext("one");
        assertNoRegisteredCall();
    }

    @Test
    public void shouldHonorDisposedWhenCallingOnComplete() throws Exception {
        // Given
        Disposable disposable = Mockito.mock(Disposable.class);
        Observer childObserver = Mockito.mock(Observer.class);
        Observer decoratedObserver = CircuitBreakerOperator.of(circuitBreaker).apply(childObserver);
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
        Observer childObserver = Mockito.mock(Observer.class);
        Observer decoratedObserver = CircuitBreakerOperator.of(circuitBreaker).apply(childObserver);
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
        Observer childObserver = Mockito.mock(Observer.class);
        Observer decoratedObserver = CircuitBreakerOperator.of(circuitBreaker).apply(childObserver);
        circuitBreaker.transitionToOpenState();
        decoratedObserver.onSubscribe(disposable);
        // When
        dispose();
        // Then
        assertNoRegisteredCall();
    }
}

