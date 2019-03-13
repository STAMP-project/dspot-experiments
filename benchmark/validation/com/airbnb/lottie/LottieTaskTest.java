package com.airbnb.lottie;


import java.util.concurrent.Callable;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class LottieTaskTest extends BaseTest {
    @Mock
    public LottieListener<Integer> successListener;

    @Mock
    public LottieListener<Throwable> failureListener;

    @Test
    public void testListener() {
        LottieTask<Integer> task = new LottieTask(new Callable<LottieResult<Integer>>() {
            @Override
            public LottieResult<Integer> call() {
                return new LottieResult(5);
            }
        }, true).addListener(successListener).addFailureListener(failureListener);
        Mockito.verify(successListener, Mockito.times(1)).onResult(5);
        Mockito.verifyZeroInteractions(failureListener);
    }

    @Test
    public void testException() {
        final IllegalStateException exception = new IllegalStateException("foo");
        LottieTask<Integer> task = new LottieTask(new Callable<LottieResult<Integer>>() {
            @Override
            public LottieResult<Integer> call() {
                throw exception;
            }
        }, true).addListener(successListener).addFailureListener(failureListener);
        Mockito.verifyZeroInteractions(successListener);
        Mockito.verify(failureListener, Mockito.times(1)).onResult(exception);
    }

    @Test
    public void testAddListenerAfter() {
        LottieTask<Integer> task = new LottieTask(new Callable<LottieResult<Integer>>() {
            @Override
            public LottieResult<Integer> call() {
                return new LottieResult(5);
            }
        }, true);
        task.addListener(successListener);
        task.addFailureListener(failureListener);
        Mockito.verify(successListener, Mockito.times(1)).onResult(5);
        Mockito.verifyZeroInteractions(failureListener);
    }
}

