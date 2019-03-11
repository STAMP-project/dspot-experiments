package com.pushtorefresh.storio3.operations.internal;


import com.pushtorefresh.storio3.Optional;
import com.pushtorefresh.storio3.operations.PreparedOperation;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.junit.Test;
import org.mockito.Mockito;


public class FlowableOnSubscribeExecuteAsBlockingOptionalTest {
    @SuppressWarnings("CheckResult")
    @Test
    public void shouldExecuteAsBlockingAfterSubscription() {
        // noinspection unchecked
        final PreparedOperation<String, Optional<String>, String> preparedOperation = Mockito.mock(PreparedOperation.class);
        final String expectedResult = "expected_string";
        Mockito.when(preparedOperation.executeAsBlocking()).thenReturn(expectedResult);
        TestSubscriber<Optional<String>> testSubscriber = new TestSubscriber<Optional<String>>();
        Flowable.create(new FlowableOnSubscribeExecuteAsBlockingOptional<String, String>(preparedOperation), BackpressureStrategy.MISSING).subscribe(testSubscriber);
        Mockito.verify(preparedOperation).executeAsBlocking();
        testSubscriber.assertValue(Optional.of(expectedResult));
        testSubscriber.assertNoErrors();
        testSubscriber.assertComplete();
    }

    @Test
    public void shouldCallOnError() {
        Throwable throwable = new IllegalStateException("Test exception");
        // noinspection unchecked
        PreparedOperation<String, Optional<String>, String> preparedOperation = Mockito.mock(PreparedOperation.class);
        Mockito.when(preparedOperation.executeAsBlocking()).thenThrow(throwable);
        TestSubscriber<Optional<String>> testSubscriber = TestSubscriber.create();
        Flowable.create(new FlowableOnSubscribeExecuteAsBlockingOptional<String, String>(preparedOperation), BackpressureStrategy.MISSING).subscribe(testSubscriber);
        testSubscriber.assertError(throwable);
        testSubscriber.assertNoValues();
        testSubscriber.assertNotComplete();
    }
}

