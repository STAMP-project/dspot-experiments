package io.github.resilience4j.ratelimiter.operator;


import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.reactivex.Maybe;
import io.reactivex.MaybeObserver;
import io.reactivex.disposables.Disposable;
import java.io.IOException;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Unit test for {@link RateLimiterMaybeObserver}.
 */
@SuppressWarnings("unchecked")
public class RateLimiterMaybeObserverTest extends RateLimiterAssertions {
    @Test
    public void shouldEmitEvent() {
        Maybe.just(1).lift(RateLimiterOperator.of(rateLimiter)).test().assertResult(1);
        assertSinglePermitUsed();
    }

    @Test
    public void shouldPropagateError() {
        Maybe.error(new IOException("BAM!")).lift(RateLimiterOperator.of(rateLimiter)).test().assertSubscribed().assertError(IOException.class).assertNotComplete();
        assertSinglePermitUsed();
    }

    @Test
    public void shouldEmitErrorWithRequestNotPermittedException() {
        saturateRateLimiter();
        Maybe.just(1).lift(RateLimiterOperator.of(rateLimiter)).test().assertSubscribed().assertError(RequestNotPermitted.class).assertNotComplete();
        assertNoPermitLeft();
    }

    @Test
    public void shouldHonorDisposedWhenCallingOnSuccess() throws Exception {
        // Given
        Disposable disposable = Mockito.mock(Disposable.class);
        MaybeObserver childObserver = Mockito.mock(MaybeObserver.class);
        MaybeObserver decoratedObserver = RateLimiterOperator.of(rateLimiter).apply(childObserver);
        decoratedObserver.onSubscribe(disposable);
        // When
        dispose();
        decoratedObserver.onSuccess(1);
        // Then
        Mockito.verify(childObserver, Mockito.never()).onSuccess(ArgumentMatchers.any());
        assertSinglePermitUsed();
    }

    @Test
    public void shouldHonorDisposedWhenCallingOnError() throws Exception {
        // Given
        Disposable disposable = Mockito.mock(Disposable.class);
        MaybeObserver childObserver = Mockito.mock(MaybeObserver.class);
        MaybeObserver decoratedObserver = RateLimiterOperator.of(rateLimiter).apply(childObserver);
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
        MaybeObserver childObserver = Mockito.mock(MaybeObserver.class);
        MaybeObserver decoratedObserver = RateLimiterOperator.of(rateLimiter).apply(childObserver);
        decoratedObserver.onSubscribe(disposable);
        // When
        dispose();
        decoratedObserver.onComplete();
        // Then
        Mockito.verify(childObserver, Mockito.never()).onComplete();
        assertSinglePermitUsed();
    }
}

