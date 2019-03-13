package com.pushtorefresh.storio3.operations.internal;


import com.pushtorefresh.storio3.operations.PreparedOperation;
import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.junit.Test;
import org.mockito.Mockito;


public class MapSomethingToExecuteAsBlockingTest {
    @SuppressWarnings("unchecked")
    @Test
    public void verifyBehavior() {
        final PreparedOperation<String, String, Object> preparedOperation = Mockito.mock(PreparedOperation.class);
        final String expectedMapResult = "expected_string";
        Mockito.when(preparedOperation.executeAsBlocking()).thenReturn(expectedMapResult);
        TestSubscriber<String> testSubscriber = new TestSubscriber<String>();
        Flowable.just(1).map(new MapSomethingToExecuteAsBlocking<Integer, String, String, Object>(preparedOperation)).subscribe(testSubscriber);
        Mockito.verify(preparedOperation, Mockito.times(1)).executeAsBlocking();
        testSubscriber.assertValue(expectedMapResult);
        testSubscriber.assertNoErrors();
        testSubscriber.assertComplete();
    }
}

