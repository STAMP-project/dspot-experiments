package com.pushtorefresh.storio3.operations.internal;


import com.pushtorefresh.storio3.Optional;
import com.pushtorefresh.storio3.operations.PreparedOperation;
import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.junit.Test;
import org.mockito.Mockito;


public class MapSomethingToExecuteAsBlockingOptionalTest {
    @SuppressWarnings("unchecked")
    @Test
    public void verifyBehavior() {
        final PreparedOperation<String, Optional<String>, Object> preparedOperation = Mockito.mock(PreparedOperation.class);
        final String expectedMapResult = "expected_string";
        Mockito.when(preparedOperation.executeAsBlocking()).thenReturn(expectedMapResult);
        TestSubscriber<Optional<String>> testSubscriber = new TestSubscriber<Optional<String>>();
        Flowable.just(1).map(new MapSomethingToExecuteAsBlockingOptional<Integer, String, Object>(preparedOperation)).subscribe(testSubscriber);
        Mockito.verify(preparedOperation).executeAsBlocking();
        testSubscriber.assertValue(Optional.of(expectedMapResult));
        testSubscriber.assertNoErrors();
        testSubscriber.assertComplete();
    }
}

