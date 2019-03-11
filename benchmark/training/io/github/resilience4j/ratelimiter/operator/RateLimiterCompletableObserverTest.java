package io.github.resilience4j.ratelimiter.operator;


import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.disposables.Disposable;
import java.io.IOException;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Unit test for {@link RateLimiterCompletableObserver}.
 */
@SuppressWarnings("unchecked")
public class RateLimiterCompletableObserverTest extends RateLimiterAssertions {
    @Test
    public void shouldEmitCompleted() {
        Completable.complete().lift(RateLimiterOperator.of(rateLimiter)).test().assertComplete();
        assertSinglePermitUsed();
    }

    @Test
    public void shouldPropagateError() {
        Completable.error(new IOException("BAM!")).lift(RateLimiterOperator.of(rateLimiter)).test().assertSubscribed().assertError(IOException.class).assertNotComplete();
        assertSinglePermitUsed();
    }

    @Test
    public void shouldEmitErrorWithRequestNotPermittedException() {
        saturateRateLimiter();
        Completable.complete().lift(RateLimiterOperator.of(rateLimiter)).test().assertSubscribed().assertError(RequestNotPermitted.class).assertNotComplete();
        assertNoPermitLeft();
    }

    @Test
    public void shouldHonorDisposedWhenCallingOnComplete() throws Exception {
        // Given
        Disposable disposable = Mockito.mock(Disposable.class);
        CompletableObserver childObserver = Mockito.mock(CompletableObserver.class);
        CompletableObserver decoratedObserver = RateLimiterOperator.of(rateLimiter).apply(childObserver);
        decoratedObserver.onSubscribe(disposable);
        // When
        dispose();
        decoratedObserver.onComplete();
        // Then
        Mockito.verify(childObserver, Mockito.never()).onComplete();
        assertSinglePermitUsed();
    }

    @Test
    public void shouldHonorDisposedWhenCallingOnError() throws Exception {
        // Given
        Disposable disposable = Mockito.mock(Disposable.class);
        CompletableObserver childObserver = Mockito.mock(CompletableObserver.class);
        CompletableObserver decoratedObserver = RateLimiterOperator.of(rateLimiter).apply(childObserver);
        decoratedObserver.onSubscribe(disposable);
        // When
        dispose();
        decoratedObserver.onError(new IllegalStateException());
        // Then
        Mockito.verify(childObserver, Mockito.never()).onError(ArgumentMatchers.any());
        assertSinglePermitUsed();
    }
}

