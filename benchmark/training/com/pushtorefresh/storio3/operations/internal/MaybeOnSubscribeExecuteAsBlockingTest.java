package com.pushtorefresh.storio3.operations.internal;


import com.pushtorefresh.storio3.StorIOException;
import com.pushtorefresh.storio3.operations.PreparedMaybeOperation;
import io.reactivex.Maybe;
import io.reactivex.observers.TestObserver;
import org.junit.Test;
import org.mockito.Mockito;


public class MaybeOnSubscribeExecuteAsBlockingTest {
    @SuppressWarnings("CheckResult")
    @Test
    public void shouldExecuteAsBlockingAfterSubscription() {
        // noinspection unchecked
        final PreparedMaybeOperation<String, String, String> preparedOperation = Mockito.mock(PreparedMaybeOperation.class);
        String expectedResult = "test";
        Mockito.when(preparedOperation.executeAsBlocking()).thenReturn(expectedResult);
        TestObserver<String> testObserver = new TestObserver<String>();
        Mockito.verifyZeroInteractions(preparedOperation);
        Maybe<String> maybe = Maybe.create(new MaybeOnSubscribeExecuteAsBlocking<String, String, String>(preparedOperation));
        Mockito.verifyZeroInteractions(preparedOperation);
        maybe.subscribe(testObserver);
        testObserver.assertValue(expectedResult);
        testObserver.assertNoErrors();
        testObserver.assertComplete();
        Mockito.verify(preparedOperation).executeAsBlocking();
    }

    @SuppressWarnings("CheckResult")
    @Test
    public void shouldCompleteIfNullOccurred() {
        // noinspection unchecked
        final PreparedMaybeOperation<String, String, String> preparedOperation = Mockito.mock(PreparedMaybeOperation.class);
        Mockito.when(preparedOperation.executeAsBlocking()).thenReturn(null);
        TestObserver<String> testObserver = new TestObserver<String>();
        Mockito.verifyZeroInteractions(preparedOperation);
        Maybe<String> maybe = Maybe.create(new MaybeOnSubscribeExecuteAsBlocking<String, String, String>(preparedOperation));
        Mockito.verifyZeroInteractions(preparedOperation);
        maybe.subscribe(testObserver);
        testObserver.assertNoErrors();
        testObserver.assertComplete();
        Mockito.verify(preparedOperation).executeAsBlocking();
    }

    @SuppressWarnings("CheckResult")
    @Test
    public void shouldCallOnErrorIfExceptionOccurred() {
        // noinspection unchecked
        final PreparedMaybeOperation<Object, Object, Object> preparedOperation = Mockito.mock(PreparedMaybeOperation.class);
        StorIOException expectedException = new StorIOException("test exception");
        Mockito.when(preparedOperation.executeAsBlocking()).thenThrow(expectedException);
        TestObserver<Object> testObserver = new TestObserver<Object>();
        Maybe<Object> maybe = Maybe.create(new MaybeOnSubscribeExecuteAsBlocking<Object, Object, Object>(preparedOperation));
        Mockito.verifyZeroInteractions(preparedOperation);
        maybe.subscribe(testObserver);
        testObserver.assertError(expectedException);
        testObserver.assertNotComplete();
        Mockito.verify(preparedOperation).executeAsBlocking();
    }
}

