package com.nytimes.android.external.store3.base.impl;


import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.PublishSubject;
import java.util.concurrent.Callable;
import org.junit.Test;
import org.mockito.Mockito;


public class RepeatWhenEmitsTest {
    @Test
    public void testTransformer() throws Exception {
        // prepare mock
        // noinspection unchecked
        Callable<String> mockCallable = ((Callable<String>) (Mockito.mock(Callable.class)));
        Mockito.when(mockCallable.call()).thenReturn("value");
        // create an observable and apply the transformer to test
        PublishSubject<String> source = PublishSubject.create();
        TestObserver<String> testSubscriber = Observable.fromCallable(mockCallable).compose(RepeatWhenEmits.<String>from(source)).test();
        for (int i = 1; i < 10; i++) {
            // check that the original observable was called i times and that i events arrived.
            Mockito.verify(mockCallable, Mockito.times(i)).call();
            testSubscriber.assertValueCount(i);
            source.onNext("event");
        }
    }
}

