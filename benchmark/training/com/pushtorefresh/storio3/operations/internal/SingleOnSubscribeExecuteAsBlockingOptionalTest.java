package com.pushtorefresh.storio3.operations.internal;


import com.pushtorefresh.storio3.Optional;
import com.pushtorefresh.storio3.StorIOException;
import com.pushtorefresh.storio3.operations.PreparedOperation;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import org.junit.Test;
import org.mockito.Mockito;


public class SingleOnSubscribeExecuteAsBlockingOptionalTest {
    @SuppressWarnings("CheckResult")
    @Test
    public void shouldExecuteAsBlockingAfterSubscription() {
        // noinspection unchecked
        final PreparedOperation<String, Optional<String>, String> preparedOperation = Mockito.mock(PreparedOperation.class);
        String expectedResult = "test";
        Mockito.when(preparedOperation.executeAsBlocking()).thenReturn(expectedResult);
        TestObserver<Optional<String>> testObserver = new TestObserver<Optional<String>>();
        Mockito.verifyZeroInteractions(preparedOperation);
        Single<Optional<String>> single = Single.create(new SingleOnSubscribeExecuteAsBlockingOptional<String, String>(preparedOperation));
        Mockito.verifyZeroInteractions(preparedOperation);
        single.subscribe(testObserver);
        testObserver.assertValue(Optional.of(expectedResult));
        testObserver.assertNoErrors();
        testObserver.assertComplete();
        Mockito.verify(preparedOperation).executeAsBlocking();
    }

    @SuppressWarnings("CheckResult")
    @Test
    public void shouldCallOnErrorIfExceptionOccurred() {
        // noinspection unchecked
        final PreparedOperation<Object, Optional<Object>, Object> preparedOperation = Mockito.mock(PreparedOperation.class);
        StorIOException expectedException = new StorIOException("test exception");
        Mockito.when(preparedOperation.executeAsBlocking()).thenThrow(expectedException);
        TestObserver<Optional<Object>> testObserver = new TestObserver<Optional<Object>>();
        Single<Optional<Object>> single = Single.create(new SingleOnSubscribeExecuteAsBlockingOptional<Object, Object>(preparedOperation));
        Mockito.verifyZeroInteractions(preparedOperation);
        single.subscribe(testObserver);
        testObserver.assertError(expectedException);
        testObserver.assertNotComplete();
        Mockito.verify(preparedOperation).executeAsBlocking();
    }
}

