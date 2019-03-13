package io.github.resilience4j.ratelimiter.operator;


import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import java.io.IOException;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Unit test for {@link RateLimiterObserver}.
 */
@SuppressWarnings("unchecked")
public class RateLimiterObserverTest extends RateLimiterAssertions {
    @Test
    public void shouldEmitSingleEventWithSinglePermit() {
        Observable.just(1).lift(RateLimiterOperator.of(rateLimiter)).test().assertResult(1);
        assertSinglePermitUsed();
    }

    @Test
    public void shouldEmitAllEvents() {
        Observable.fromArray(1, 2).lift(RateLimiterOperator.of(rateLimiter)).test().assertResult(1, 2);
        assertUsedPermits(2);
    }

    @Test
    public void shouldPropagateError() {
        Observable.error(new IOException("BAM!")).lift(RateLimiterOperator.of(rateLimiter)).test().assertSubscribed().assertError(IOException.class).assertNotComplete();
        assertSinglePermitUsed();
    }

    @Test
    public void shouldEmitErrorWithRequestNotPermittedException() {
        saturateRateLimiter();
        Observable.just(1).lift(RateLimiterOperator.of(rateLimiter)).test().assertSubscribed().assertError(RequestNotPermitted.class).assertNotComplete();
        assertNoPermitLeft();
    }

    @Test
    public void shouldHonorDisposedWhenCallingOnNext() throws Exception {
        // Given
        Disposable disposable = Mockito.mock(Disposable.class);
        Observer childObserver = Mockito.mock(Observer.class);
        Observer decoratedObserver = RateLimiterOperator.of(rateLimiter).apply(childObserver);
        decoratedObserver.onSubscribe(disposable);
        // When
        dispose();
        decoratedObserver.onNext(1);
        // Then
        Mockito.verify(childObserver, Mockito.never()).onNext(ArgumentMatchers.any());
        assertSinglePermitUsed();
    }

    @Test
    public void shouldHonorDisposedWhenCallingOnError() throws Exception {
        // Given
        Disposable disposable = Mockito.mock(Disposable.class);
        Observer childObserver = Mockito.mock(Observer.class);
        Observer decoratedObserver = RateLimiterOperator.of(rateLimiter).apply(childObserver);
        decoratedObserver.onSubscribe(disposable);
        // When
        dispose();
        decoratedObserver.onError(new IllegalStateException());
        // Then
        Mockito.verify(childObserver, Mockito.never()).onError(ArgumentMatchers.any());
        assertSinglePermitUsed();
    }

    @Test
    public void shouldHonorDisposedWhenCallingOnComplete() throws Exception {
        // Given
        Disposable disposable = Mockito.mock(Disposable.class);
        Observer childObserver = Mockito.mock(Observer.class);
        Observer decoratedObserver = RateLimiterOperator.of(rateLimiter).apply(childObserver);
        decoratedObserver.onSubscribe(disposable);
        // When
        dispose();
        decoratedObserver.onComplete();
        // Then
        Mockito.verify(childObserver, Mockito.never()).onComplete();
        assertSinglePermitUsed();
    }
}

