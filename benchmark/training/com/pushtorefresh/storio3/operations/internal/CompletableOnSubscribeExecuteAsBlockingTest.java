package com.pushtorefresh.storio3.operations.internal;


import com.pushtorefresh.storio3.StorIOException;
import com.pushtorefresh.storio3.operations.PreparedCompletableOperation;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.observers.TestObserver;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class CompletableOnSubscribeExecuteAsBlockingTest {
    @SuppressWarnings("ResourceType")
    @Test
    public void shouldExecuteAsBlockingAfterSubscription() {
        final PreparedCompletableOperation preparedOperation = Mockito.mock(PreparedCompletableOperation.class);
        TestObserver testObserver = new TestObserver();
        Mockito.verifyZeroInteractions(preparedOperation);
        Completable completable = Completable.create(new CompletableOnSubscribeExecuteAsBlocking(preparedOperation));
        Mockito.verifyZeroInteractions(preparedOperation);
        completable.subscribe(testObserver);
        testObserver.assertNoErrors();
        testObserver.assertComplete();
        Mockito.verify(preparedOperation).executeAsBlocking();
        Mockito.verify(preparedOperation, Mockito.never()).asRxFlowable(ArgumentMatchers.any(BackpressureStrategy.class));
        Mockito.verify(preparedOperation, Mockito.never()).asRxSingle();
        Mockito.verify(preparedOperation, Mockito.never()).asRxCompletable();
    }

    @SuppressWarnings({ "ThrowableInstanceNeverThrown", "ResourceType" })
    @Test
    public void shouldCallOnErrorIfExceptionOccurred() {
        final PreparedCompletableOperation preparedOperation = Mockito.mock(PreparedCompletableOperation.class);
        StorIOException expectedException = new StorIOException("test exception");
        Mockito.when(preparedOperation.executeAsBlocking()).thenThrow(expectedException);
        TestObserver testObserver = new TestObserver();
        Completable completable = Completable.create(new CompletableOnSubscribeExecuteAsBlocking(preparedOperation));
        Mockito.verifyZeroInteractions(preparedOperation);
        completable.subscribe(testObserver);
        testObserver.assertError(expectedException);
        testObserver.assertNotComplete();
        Mockito.verify(preparedOperation).executeAsBlocking();
        Mockito.verify(preparedOperation, Mockito.never()).asRxFlowable(ArgumentMatchers.any(BackpressureStrategy.class));
        Mockito.verify(preparedOperation, Mockito.never()).asRxSingle();
        Mockito.verify(preparedOperation, Mockito.never()).asRxCompletable();
    }
}

