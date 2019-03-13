package com.pushtorefresh.storio3.sqlite.operations.put;


import android.content.ContentValues;
import com.pushtorefresh.storio3.StorIOException;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import io.reactivex.subscribers.TestSubscriber;
import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;


public class PreparedPutContentValuesTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldReturnContentValuesInGetData() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForOneContentValues();
        final PreparedPutContentValues operation = putStub.storIOSQLite.put().contentValues(putStub.contentValues.get(0)).withPutResolver(putStub.putResolver).prepare();
        assertThat(operation.getData()).isEqualTo(putStub.contentValues.get(0));
    }

    @Test
    public void putContentValuesBlocking() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForOneContentValues();
        final PutResult putResult = putStub.storIOSQLite.put().contentValues(putStub.contentValues.get(0)).withPutResolver(putStub.putResolver).prepare().executeAsBlocking();
        putStub.verifyBehaviorForOneContentValues(putResult);
    }

    @Test
    public void putContentValuesFlowable() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForOneContentValues();
        final Flowable<PutResult> putResultFlowable = putStub.storIOSQLite.put().contentValues(putStub.contentValues.get(0)).withPutResolver(putStub.putResolver).prepare().asRxFlowable(BackpressureStrategy.MISSING);
        putStub.verifyBehaviorForOneContentValues(putResultFlowable);
    }

    @Test
    public void putContentValuesSingle() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForOneContentValues();
        final Single<PutResult> putResultSingle = putStub.storIOSQLite.put().contentValues(putStub.contentValues.get(0)).withPutResolver(putStub.putResolver).prepare().asRxSingle();
        putStub.verifyBehaviorForOneContentValues(putResultSingle);
    }

    @Test
    public void putContentValuesCompletable() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForOneContentValues();
        final Completable completable = putStub.storIOSQLite.put().contentValues(putStub.contentValues.get(0)).withPutResolver(putStub.putResolver).prepare().asRxCompletable();
        putStub.verifyBehaviorForOneContentValues(completable);
    }

    @Test
    public void shouldWrapExceptionIntoStorIOExceptionBlocking() {
        final PutContentValuesStub stub = PutContentValuesStub.newPutStubForOneContentValues();
        ContentValues contentValues = stub.contentValues.get(0);
        IllegalStateException testException = new IllegalStateException("test exception");
        Mockito.doThrow(testException).when(stub.putResolver).performPut(stub.storIOSQLite, contentValues);
        expectedException.expect(StorIOException.class);
        expectedException.expectMessage(CoreMatchers.startsWith("Error has occurred during Put operation. contentValues ="));
        expectedException.expectCause(CoreMatchers.equalTo(testException));
        stub.storIOSQLite.put().contentValues(contentValues).withPutResolver(stub.putResolver).prepare().executeAsBlocking();
        Mockito.verifyNoMoreInteractions(stub.storIOSQLite, stub.lowLevel);
    }

    @Test
    public void shouldWrapExceptionIntoStorIOExceptionFlowable() {
        final PutContentValuesStub stub = PutContentValuesStub.newPutStubForOneContentValues();
        ContentValues contentValues = stub.contentValues.get(0);
        IllegalStateException testException = new IllegalStateException("test exception");
        Mockito.doThrow(testException).when(stub.putResolver).performPut(stub.storIOSQLite, contentValues);
        final TestSubscriber<Object> testSubscriber = new TestSubscriber<Object>();
        stub.storIOSQLite.put().contentValues(contentValues).withPutResolver(stub.putResolver).prepare().asRxFlowable(BackpressureStrategy.MISSING).subscribe(testSubscriber);
        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertNoValues();
        testSubscriber.assertError(StorIOException.class);
        // noinspection ThrowableResultOfMethodCallIgnored
        StorIOException expected = ((StorIOException) (testSubscriber.errors().get(0)));
        assertThat(expected).hasMessageStartingWith("Error has occurred during Put operation. contentValues =");
        IllegalStateException cause = ((IllegalStateException) (expected.getCause()));
        assertThat(cause).hasMessage("test exception");
        Mockito.verify(stub.storIOSQLite).put();
        Mockito.verify(stub.storIOSQLite).defaultRxScheduler();
        Mockito.verify(stub.storIOSQLite).interceptors();
        Mockito.verifyNoMoreInteractions(stub.storIOSQLite, stub.lowLevel);
    }

    @Test
    public void shouldWrapExceptionIntoStorIOExceptionSingle() {
        final PutContentValuesStub stub = PutContentValuesStub.newPutStubForOneContentValues();
        ContentValues contentValues = stub.contentValues.get(0);
        IllegalStateException testException = new IllegalStateException("test exception");
        Mockito.doThrow(testException).when(stub.putResolver).performPut(stub.storIOSQLite, contentValues);
        final TestObserver<Object> testObserver = new TestObserver<Object>();
        stub.storIOSQLite.put().contentValues(contentValues).withPutResolver(stub.putResolver).prepare().asRxSingle().subscribe(testObserver);
        testObserver.awaitTerminalEvent();
        testObserver.assertNoValues();
        testObserver.assertError(StorIOException.class);
        // noinspection ThrowableResultOfMethodCallIgnored
        StorIOException expected = ((StorIOException) (testObserver.errors().get(0)));
        assertThat(expected).hasMessageStartingWith("Error has occurred during Put operation. contentValues =");
        IllegalStateException cause = ((IllegalStateException) (expected.getCause()));
        assertThat(cause).hasMessage("test exception");
        Mockito.verify(stub.storIOSQLite).put();
        Mockito.verify(stub.storIOSQLite).defaultRxScheduler();
        Mockito.verify(stub.storIOSQLite).interceptors();
        Mockito.verifyNoMoreInteractions(stub.storIOSQLite, stub.lowLevel);
    }

    @Test
    public void shouldWrapExceptionIntoStorIOExceptionCompletable() {
        final PutContentValuesStub stub = PutContentValuesStub.newPutStubForOneContentValues();
        ContentValues contentValues = stub.contentValues.get(0);
        IllegalStateException testException = new IllegalStateException("test exception");
        Mockito.doThrow(testException).when(stub.putResolver).performPut(stub.storIOSQLite, contentValues);
        final TestObserver testObserver = new TestObserver();
        stub.storIOSQLite.put().contentValues(contentValues).withPutResolver(stub.putResolver).prepare().asRxCompletable().subscribe(testObserver);
        testObserver.awaitTerminalEvent();
        testObserver.assertNoValues();
        testObserver.assertError(StorIOException.class);
        // noinspection ThrowableResultOfMethodCallIgnored
        StorIOException expected = ((StorIOException) (testObserver.errors().get(0)));
        assertThat(expected).hasMessageStartingWith("Error has occurred during Put operation. contentValues =");
        IllegalStateException cause = ((IllegalStateException) (expected.getCause()));
        assertThat(cause).hasMessage("test exception");
        Mockito.verify(stub.storIOSQLite).put();
        Mockito.verify(stub.storIOSQLite).defaultRxScheduler();
        Mockito.verify(stub.storIOSQLite).interceptors();
        Mockito.verifyNoMoreInteractions(stub.storIOSQLite, stub.lowLevel);
    }

    @Test
    public void shouldNotNotifyIfWasNotInsertedAndUpdated() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForOneContentValuesWithoutInsertsAndUpdates();
        final PutResult putResult = putStub.storIOSQLite.put().contentValues(putStub.contentValues.get(0)).withPutResolver(putStub.putResolver).prepare().executeAsBlocking();
        putStub.verifyBehaviorForOneContentValues(putResult);
    }
}

