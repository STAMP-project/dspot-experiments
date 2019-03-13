package com.pushtorefresh.storio3.operations.internal;


import BackpressureStrategy.MISSING;
import com.pushtorefresh.storio3.operations.PreparedOperation;
import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.junit.Test;
import org.mockito.Mockito;


public class FlowableOnSubscribeExecuteAsBlockingTest {
    @SuppressWarnings("CheckResult")
    @Test
    public void shouldExecuteAsBlockingAfterSubscription() {
        // noinspection unchecked
        final PreparedOperation<String, String, String> preparedOperation = Mockito.mock(PreparedOperation.class);
        final String expectedResult = "expected_string";
        Mockito.when(preparedOperation.executeAsBlocking()).thenReturn(expectedResult);
        TestSubscriber<String> testSubscriber = new TestSubscriber<String>();
        Flowable.create(new FlowableOnSubscribeExecuteAsBlocking<String, String, String>(preparedOperation), MISSING).subscribe(testSubscriber);
        Mockito.verify(preparedOperation).executeAsBlocking();
        testSubscriber.assertValue(expectedResult);
        testSubscriber.assertNoErrors();
        testSubscriber.assertComplete();
    }

    @Test
    public void shouldCallOnError() {
        Throwable throwable = new IllegalStateException("Test exception");
        // noinspection unchecked
        PreparedOperation<String, String, String> preparedOperation = Mockito.mock(PreparedOperation.class);
        Mockito.when(preparedOperation.executeAsBlocking()).thenThrow(throwable);
        TestSubscriber<String> testSubscriber = TestSubscriber.create();
        Flowable.create(new FlowableOnSubscribeExecuteAsBlocking<String, String, String>(preparedOperation), MISSING).subscribe(testSubscriber);
        testSubscriber.assertError(throwable);
        testSubscriber.assertNoValues();
        testSubscriber.assertNotComplete();
    }
}

