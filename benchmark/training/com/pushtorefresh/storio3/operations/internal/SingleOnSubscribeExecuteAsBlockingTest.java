package com.pushtorefresh.storio3.operations.internal;


import com.pushtorefresh.storio3.StorIOException;
import com.pushtorefresh.storio3.operations.PreparedOperation;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import org.junit.Test;
import org.mockito.Mockito;


public class SingleOnSubscribeExecuteAsBlockingTest {
    @SuppressWarnings("CheckResult")
    @Test
    public void shouldExecuteAsBlockingAfterSubscription() {
        // noinspection unchecked
        final PreparedOperation<String, String, String> preparedOperation = Mockito.mock(PreparedOperation.class);
        String expectedResult = "test";
        Mockito.when(preparedOperation.executeAsBlocking()).thenReturn(expectedResult);
        TestObserver<String> testObserver = new TestObserver<String>();
        Mockito.verifyZeroInteractions(preparedOperation);
        Single<String> single = Single.create(new SingleOnSubscribeExecuteAsBlocking<String, String, String>(preparedOperation));
        Mockito.verifyZeroInteractions(preparedOperation);
        single.subscribe(testObserver);
        testObserver.assertValue(expectedResult);
        testObserver.assertNoErrors();
        testObserver.assertComplete();
        Mockito.verify(preparedOperation).executeAsBlocking();
    }

    @SuppressWarnings("CheckResult")
    @Test
    public void shouldCallOnErrorIfExceptionOccurred() {
        // noinspection unchecked
        final PreparedOperation<Object, Object, Object> preparedOperation = Mockito.mock(PreparedOperation.class);
        StorIOException expectedException = new StorIOException("test exception");
        Mockito.when(preparedOperation.executeAsBlocking()).thenThrow(expectedException);
        TestObserver<Object> testObserver = new TestObserver<Object>();
        Single<Object> single = Single.create(new SingleOnSubscribeExecuteAsBlocking<Object, Object, Object>(preparedOperation));
        Mockito.verifyZeroInteractions(preparedOperation);
        single.subscribe(testObserver);
        testObserver.assertError(expectedException);
        testObserver.assertNotComplete();
        Mockito.verify(preparedOperation).executeAsBlocking();
    }
}

