package com.pushtorefresh.storio3.sqlite.operations.put;


import StorIOSQLite.LowLevel;
import android.content.ContentValues;
import com.pushtorefresh.storio3.StorIOException;
import com.pushtorefresh.storio3.sqlite.StorIOSQLite;
import com.pushtorefresh.storio3.sqlite.operations.SchedulerChecker;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import io.reactivex.subscribers.TestSubscriber;
import java.util.Collections;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class PreparedPutContentValuesIterableTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldReturnContentValuesInGetData() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForMultipleContentValues(false);
        final PreparedPutContentValuesIterable operation = putStub.storIOSQLite.put().contentValues(putStub.contentValues).withPutResolver(putStub.putResolver).prepare();
        assertThat(operation.getData()).isEqualTo(putStub.contentValues);
    }

    @Test
    public void putMultipleBlockingWithTransaction() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForMultipleContentValues(true);
        final PutResults<ContentValues> putResults = putStub.storIOSQLite.put().contentValues(putStub.contentValues).withPutResolver(putStub.putResolver).useTransaction(true).prepare().executeAsBlocking();
        putStub.verifyBehaviorForMultipleContentValues(putResults);
    }

    @Test
    public void putMultipleFlowableWithTransaction() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForMultipleContentValues(true);
        final Flowable<PutResults<ContentValues>> putResultsFlowable = putStub.storIOSQLite.put().contentValues(putStub.contentValues).withPutResolver(putStub.putResolver).useTransaction(true).prepare().asRxFlowable(BackpressureStrategy.MISSING);
        putStub.verifyBehaviorForMultipleContentValues(putResultsFlowable);
    }

    @Test
    public void putMultipleSingleWithTransaction() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForMultipleContentValues(true);
        final Single<PutResults<ContentValues>> putResultsSingle = putStub.storIOSQLite.put().contentValues(putStub.contentValues).withPutResolver(putStub.putResolver).useTransaction(true).prepare().asRxSingle();
        putStub.verifyBehaviorForMultipleContentValues(putResultsSingle);
    }

    @Test
    public void putMultipleCompletableWithTransaction() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForMultipleContentValues(true);
        final Completable completable = putStub.storIOSQLite.put().contentValues(putStub.contentValues).withPutResolver(putStub.putResolver).useTransaction(true).prepare().asRxCompletable();
        putStub.verifyBehaviorForMultipleContentValues(completable);
    }

    @Test
    public void putMultipleBlockingWithoutTransaction() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForMultipleContentValues(false);
        final PutResults<ContentValues> putResults = putStub.storIOSQLite.put().contentValues(putStub.contentValues).withPutResolver(putStub.putResolver).useTransaction(false).prepare().executeAsBlocking();
        putStub.verifyBehaviorForMultipleContentValues(putResults);
    }

    @Test
    public void putMultipleFlowableWithoutTransaction() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForMultipleContentValues(false);
        final Flowable<PutResults<ContentValues>> putResultsFlowable = putStub.storIOSQLite.put().contentValues(putStub.contentValues).withPutResolver(putStub.putResolver).useTransaction(false).prepare().asRxFlowable(BackpressureStrategy.MISSING);
        putStub.verifyBehaviorForMultipleContentValues(putResultsFlowable);
    }

    @Test
    public void putMultipleSingleWithoutTransaction() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForMultipleContentValues(false);
        final Single<PutResults<ContentValues>> putResultsSingle = putStub.storIOSQLite.put().contentValues(putStub.contentValues).withPutResolver(putStub.putResolver).useTransaction(false).prepare().asRxSingle();
        putStub.verifyBehaviorForMultipleContentValues(putResultsSingle);
    }

    @Test
    public void putMultipleCompletableWithoutTransaction() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForMultipleContentValues(false);
        final Completable completable = putStub.storIOSQLite.put().contentValues(putStub.contentValues).withPutResolver(putStub.putResolver).useTransaction(false).prepare().asRxCompletable();
        putStub.verifyBehaviorForMultipleContentValues(completable);
    }

    @Test
    public void shouldFinishTransactionIfExceptionHasOccurredBlocking() {
        final StorIOSQLite storIOSQLite = Mockito.mock(StorIOSQLite.class);
        final StorIOSQLite.LowLevel lowLevel = Mockito.mock(LowLevel.class);
        Mockito.when(storIOSQLite.lowLevel()).thenReturn(lowLevel);
        // noinspection unchecked
        final PutResolver<ContentValues> putResolver = Mockito.mock(PutResolver.class);
        final List<ContentValues> contentValues = Collections.singletonList(Mockito.mock(ContentValues.class));
        Mockito.when(putResolver.performPut(ArgumentMatchers.same(storIOSQLite), ArgumentMatchers.any(ContentValues.class))).thenThrow(new IllegalStateException("test exception"));
        try {
            new PreparedPutContentValuesIterable.Builder(storIOSQLite, contentValues).withPutResolver(putResolver).useTransaction(true).prepare().executeAsBlocking();
            failBecauseExceptionWasNotThrown(StorIOException.class);
        } catch (StorIOException expected) {
            IllegalStateException cause = ((IllegalStateException) (expected.getCause()));
            assertThat(cause).hasMessage("test exception");
            Mockito.verify(lowLevel).beginTransaction();
            Mockito.verify(lowLevel, Mockito.never()).setTransactionSuccessful();
            Mockito.verify(lowLevel).endTransaction();
            Mockito.verify(storIOSQLite).lowLevel();
            Mockito.verify(storIOSQLite).interceptors();
            Mockito.verify(putResolver).performPut(ArgumentMatchers.same(storIOSQLite), ArgumentMatchers.any(ContentValues.class));
            Mockito.verifyNoMoreInteractions(storIOSQLite, lowLevel, putResolver);
        }
    }

    @Test
    public void shouldFinishTransactionIfExceptionHasOccurredFlowable() {
        final StorIOSQLite storIOSQLite = Mockito.mock(StorIOSQLite.class);
        final StorIOSQLite.LowLevel lowLevel = Mockito.mock(LowLevel.class);
        Mockito.when(storIOSQLite.lowLevel()).thenReturn(lowLevel);
        // noinspection unchecked
        final PutResolver<ContentValues> putResolver = Mockito.mock(PutResolver.class);
        final List<ContentValues> contentValues = Collections.singletonList(Mockito.mock(ContentValues.class));
        Mockito.when(putResolver.performPut(ArgumentMatchers.same(storIOSQLite), ArgumentMatchers.any(ContentValues.class))).thenThrow(new IllegalStateException("test exception"));
        final TestSubscriber<PutResults<ContentValues>> testSubscriber = new TestSubscriber<PutResults<ContentValues>>();
        new PreparedPutContentValuesIterable.Builder(storIOSQLite, contentValues).withPutResolver(putResolver).useTransaction(true).prepare().asRxFlowable(BackpressureStrategy.MISSING).subscribe(testSubscriber);
        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertNoValues();
        testSubscriber.assertError(StorIOException.class);
        // noinspection ThrowableResultOfMethodCallIgnored
        StorIOException expected = ((StorIOException) (testSubscriber.errors().get(0)));
        IllegalStateException cause = ((IllegalStateException) (expected.getCause()));
        assertThat(cause).hasMessage("test exception");
        Mockito.verify(lowLevel).beginTransaction();
        Mockito.verify(lowLevel, Mockito.never()).setTransactionSuccessful();
        Mockito.verify(lowLevel).endTransaction();
        Mockito.verify(storIOSQLite).lowLevel();
        Mockito.verify(storIOSQLite).defaultRxScheduler();
        Mockito.verify(storIOSQLite).interceptors();
        Mockito.verify(putResolver).performPut(ArgumentMatchers.same(storIOSQLite), ArgumentMatchers.any(ContentValues.class));
        Mockito.verifyNoMoreInteractions(storIOSQLite, lowLevel, putResolver);
    }

    @Test
    public void shouldFinishTransactionIfExceptionHasOccurredSingle() {
        final StorIOSQLite storIOSQLite = Mockito.mock(StorIOSQLite.class);
        final StorIOSQLite.LowLevel lowLevel = Mockito.mock(LowLevel.class);
        Mockito.when(storIOSQLite.lowLevel()).thenReturn(lowLevel);
        // noinspection unchecked
        final PutResolver<ContentValues> putResolver = Mockito.mock(PutResolver.class);
        final List<ContentValues> contentValues = Collections.singletonList(Mockito.mock(ContentValues.class));
        Mockito.when(putResolver.performPut(ArgumentMatchers.same(storIOSQLite), ArgumentMatchers.any(ContentValues.class))).thenThrow(new IllegalStateException("test exception"));
        final TestObserver<PutResults<ContentValues>> testObserver = new TestObserver<PutResults<ContentValues>>();
        new PreparedPutContentValuesIterable.Builder(storIOSQLite, contentValues).withPutResolver(putResolver).useTransaction(true).prepare().asRxSingle().subscribe(testObserver);
        testObserver.awaitTerminalEvent();
        testObserver.assertNoValues();
        testObserver.assertError(StorIOException.class);
        // noinspection ThrowableResultOfMethodCallIgnored
        StorIOException expected = ((StorIOException) (testObserver.errors().get(0)));
        IllegalStateException cause = ((IllegalStateException) (expected.getCause()));
        assertThat(cause).hasMessage("test exception");
        Mockito.verify(lowLevel).beginTransaction();
        Mockito.verify(lowLevel, Mockito.never()).setTransactionSuccessful();
        Mockito.verify(lowLevel).endTransaction();
        Mockito.verify(storIOSQLite).lowLevel();
        Mockito.verify(storIOSQLite).defaultRxScheduler();
        Mockito.verify(storIOSQLite).interceptors();
        Mockito.verify(putResolver).performPut(ArgumentMatchers.same(storIOSQLite), ArgumentMatchers.any(ContentValues.class));
        Mockito.verifyNoMoreInteractions(storIOSQLite, lowLevel, putResolver);
    }

    @Test
    public void shouldFinishTransactionIfExceptionHasOccurredCompletable() {
        final StorIOSQLite storIOSQLite = Mockito.mock(StorIOSQLite.class);
        final StorIOSQLite.LowLevel lowLevel = Mockito.mock(LowLevel.class);
        Mockito.when(storIOSQLite.lowLevel()).thenReturn(lowLevel);
        // noinspection unchecked
        final PutResolver<ContentValues> putResolver = Mockito.mock(PutResolver.class);
        final List<ContentValues> contentValues = Collections.singletonList(Mockito.mock(ContentValues.class));
        Mockito.when(putResolver.performPut(ArgumentMatchers.same(storIOSQLite), ArgumentMatchers.any(ContentValues.class))).thenThrow(new IllegalStateException("test exception"));
        final TestObserver<PutResults<ContentValues>> testObserver = new TestObserver<PutResults<ContentValues>>();
        new PreparedPutContentValuesIterable.Builder(storIOSQLite, contentValues).withPutResolver(putResolver).useTransaction(true).prepare().asRxCompletable().subscribe(testObserver);
        testObserver.awaitTerminalEvent();
        testObserver.assertNoValues();
        testObserver.assertError(StorIOException.class);
        // noinspection ThrowableResultOfMethodCallIgnored
        StorIOException expected = ((StorIOException) (testObserver.errors().get(0)));
        IllegalStateException cause = ((IllegalStateException) (expected.getCause()));
        assertThat(cause).hasMessage("test exception");
        Mockito.verify(lowLevel).beginTransaction();
        Mockito.verify(lowLevel, Mockito.never()).setTransactionSuccessful();
        Mockito.verify(lowLevel).endTransaction();
        Mockito.verify(storIOSQLite).lowLevel();
        Mockito.verify(storIOSQLite).defaultRxScheduler();
        Mockito.verify(storIOSQLite).interceptors();
        Mockito.verify(putResolver).performPut(ArgumentMatchers.same(storIOSQLite), ArgumentMatchers.any(ContentValues.class));
        Mockito.verifyNoMoreInteractions(storIOSQLite, lowLevel, putResolver);
    }

    @Test
    public void verifyBehaviorInCaseOfExceptionWithoutTransactionBlocking() {
        final StorIOSQLite storIOSQLite = Mockito.mock(StorIOSQLite.class);
        final StorIOSQLite.LowLevel lowLevel = Mockito.mock(LowLevel.class);
        // noinspection unchecked
        final PutResolver<ContentValues> putResolver = Mockito.mock(PutResolver.class);
        final List<ContentValues> contentValues = Collections.singletonList(Mockito.mock(ContentValues.class));
        Mockito.when(putResolver.performPut(ArgumentMatchers.same(storIOSQLite), ArgumentMatchers.any(ContentValues.class))).thenThrow(new IllegalStateException("test exception"));
        try {
            new PreparedPutContentValuesIterable.Builder(storIOSQLite, contentValues).withPutResolver(putResolver).useTransaction(false).prepare().executeAsBlocking();
            failBecauseExceptionWasNotThrown(StorIOException.class);
        } catch (StorIOException expected) {
            IllegalStateException cause = ((IllegalStateException) (expected.getCause()));
            assertThat(cause).hasMessage("test exception");
            // Main check of this test
            Mockito.verify(lowLevel, Mockito.never()).endTransaction();
            Mockito.verify(storIOSQLite).lowLevel();
            Mockito.verify(storIOSQLite).interceptors();
            Mockito.verify(putResolver).performPut(ArgumentMatchers.same(storIOSQLite), ArgumentMatchers.any(ContentValues.class));
            Mockito.verifyNoMoreInteractions(storIOSQLite, lowLevel, putResolver);
        }
    }

    @Test
    public void verifyBehaviorInCaseOfExceptionWithoutTransactionFlowable() {
        final StorIOSQLite storIOSQLite = Mockito.mock(StorIOSQLite.class);
        final StorIOSQLite.LowLevel lowLevel = Mockito.mock(LowLevel.class);
        // noinspection unchecked
        final PutResolver<ContentValues> putResolver = Mockito.mock(PutResolver.class);
        final List<ContentValues> contentValues = Collections.singletonList(Mockito.mock(ContentValues.class));
        Mockito.when(putResolver.performPut(ArgumentMatchers.same(storIOSQLite), ArgumentMatchers.any(ContentValues.class))).thenThrow(new IllegalStateException("test exception"));
        final TestSubscriber<PutResults<ContentValues>> testSubscriber = new TestSubscriber<PutResults<ContentValues>>();
        new PreparedPutContentValuesIterable.Builder(storIOSQLite, contentValues).withPutResolver(putResolver).useTransaction(false).prepare().asRxFlowable(BackpressureStrategy.MISSING).subscribe(testSubscriber);
        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertNoValues();
        testSubscriber.assertError(StorIOException.class);
        // noinspection ThrowableResultOfMethodCallIgnored
        StorIOException expected = ((StorIOException) (testSubscriber.errors().get(0)));
        IllegalStateException cause = ((IllegalStateException) (expected.getCause()));
        assertThat(cause).hasMessage("test exception");
        // Main check of this test
        Mockito.verify(lowLevel, Mockito.never()).endTransaction();
        Mockito.verify(storIOSQLite).lowLevel();
        Mockito.verify(storIOSQLite).defaultRxScheduler();
        Mockito.verify(storIOSQLite).interceptors();
        Mockito.verify(putResolver).performPut(ArgumentMatchers.same(storIOSQLite), ArgumentMatchers.any(ContentValues.class));
        Mockito.verifyNoMoreInteractions(storIOSQLite, lowLevel, putResolver);
    }

    @Test
    public void verifyBehaviorInCaseOfExceptionWithoutTransactionSingle() {
        final StorIOSQLite storIOSQLite = Mockito.mock(StorIOSQLite.class);
        final StorIOSQLite.LowLevel lowLevel = Mockito.mock(LowLevel.class);
        // noinspection unchecked
        final PutResolver<ContentValues> putResolver = Mockito.mock(PutResolver.class);
        final List<ContentValues> contentValues = Collections.singletonList(Mockito.mock(ContentValues.class));
        Mockito.when(putResolver.performPut(ArgumentMatchers.same(storIOSQLite), ArgumentMatchers.any(ContentValues.class))).thenThrow(new IllegalStateException("test exception"));
        final TestObserver<PutResults<ContentValues>> testObserver = new TestObserver<PutResults<ContentValues>>();
        new PreparedPutContentValuesIterable.Builder(storIOSQLite, contentValues).withPutResolver(putResolver).useTransaction(false).prepare().asRxSingle().subscribe(testObserver);
        testObserver.awaitTerminalEvent();
        testObserver.assertNoValues();
        testObserver.assertError(StorIOException.class);
        // noinspection ThrowableResultOfMethodCallIgnored
        StorIOException expected = ((StorIOException) (testObserver.errors().get(0)));
        IllegalStateException cause = ((IllegalStateException) (expected.getCause()));
        assertThat(cause).hasMessage("test exception");
        // Main check of this test
        Mockito.verify(lowLevel, Mockito.never()).endTransaction();
        Mockito.verify(storIOSQLite).lowLevel();
        Mockito.verify(storIOSQLite).defaultRxScheduler();
        Mockito.verify(storIOSQLite).interceptors();
        Mockito.verify(putResolver).performPut(ArgumentMatchers.same(storIOSQLite), ArgumentMatchers.any(ContentValues.class));
        Mockito.verifyNoMoreInteractions(storIOSQLite, lowLevel, putResolver);
    }

    @Test
    public void verifyBehaviorInCaseOfExceptionWithoutTransactionCompletable() {
        final StorIOSQLite storIOSQLite = Mockito.mock(StorIOSQLite.class);
        final StorIOSQLite.LowLevel lowLevel = Mockito.mock(LowLevel.class);
        // noinspection unchecked
        final PutResolver<ContentValues> putResolver = Mockito.mock(PutResolver.class);
        final List<ContentValues> contentValues = Collections.singletonList(Mockito.mock(ContentValues.class));
        Mockito.when(putResolver.performPut(ArgumentMatchers.same(storIOSQLite), ArgumentMatchers.any(ContentValues.class))).thenThrow(new IllegalStateException("test exception"));
        final TestObserver<PutResults<ContentValues>> testObserver = new TestObserver<PutResults<ContentValues>>();
        new PreparedPutContentValuesIterable.Builder(storIOSQLite, contentValues).withPutResolver(putResolver).useTransaction(false).prepare().asRxCompletable().subscribe(testObserver);
        testObserver.awaitTerminalEvent();
        testObserver.assertNoValues();
        testObserver.assertError(StorIOException.class);
        // noinspection ThrowableResultOfMethodCallIgnored
        StorIOException expected = ((StorIOException) (testObserver.errors().get(0)));
        IllegalStateException cause = ((IllegalStateException) (expected.getCause()));
        assertThat(cause).hasMessage("test exception");
        // Main check of this test
        Mockito.verify(lowLevel, Mockito.never()).endTransaction();
        Mockito.verify(storIOSQLite).lowLevel();
        Mockito.verify(storIOSQLite).defaultRxScheduler();
        Mockito.verify(storIOSQLite).interceptors();
        Mockito.verify(putResolver).performPut(ArgumentMatchers.same(storIOSQLite), ArgumentMatchers.any(ContentValues.class));
        Mockito.verifyNoMoreInteractions(storIOSQLite, lowLevel, putResolver);
    }

    @Test
    public void putMultipleFlowableExecutesOnSpecifiedScheduler() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForMultipleContentValues(true);
        final SchedulerChecker schedulerChecker = SchedulerChecker.create(putStub.storIOSQLite);
        final PreparedPutContentValuesIterable operation = putStub.storIOSQLite.put().contentValues(putStub.contentValues).withPutResolver(putStub.putResolver).prepare();
        schedulerChecker.checkAsFlowable(operation);
    }

    @Test
    public void putMultipleSingleExecutesOnSpecifiedScheduler() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForMultipleContentValues(true);
        final SchedulerChecker schedulerChecker = SchedulerChecker.create(putStub.storIOSQLite);
        final PreparedPutContentValuesIterable operation = putStub.storIOSQLite.put().contentValues(putStub.contentValues).withPutResolver(putStub.putResolver).prepare();
        schedulerChecker.checkAsSingle(operation);
    }

    @Test
    public void putMultipleCompletableExecutesOnSpecifiedScheduler() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForMultipleContentValues(true);
        final SchedulerChecker schedulerChecker = SchedulerChecker.create(putStub.storIOSQLite);
        final PreparedPutContentValuesIterable operation = putStub.storIOSQLite.put().contentValues(putStub.contentValues).withPutResolver(putStub.putResolver).prepare();
        schedulerChecker.checkAsCompletable(operation);
    }

    @Test
    public void shouldWrapExceptionIntoStorIOExceptionBlocking() {
        final PutContentValuesStub stub = PutContentValuesStub.newPutStubForMultipleContentValues(true);
        IllegalStateException testException = new IllegalStateException("test exception");
        Mockito.doThrow(testException).when(stub.putResolver).performPut(ArgumentMatchers.eq(stub.storIOSQLite), ArgumentMatchers.any(ContentValues.class));
        expectedException.expect(StorIOException.class);
        expectedException.expectMessage(CoreMatchers.startsWith("Error has occurred during Put operation. contentValues ="));
        expectedException.expectCause(CoreMatchers.equalTo(testException));
        stub.storIOSQLite.put().contentValues(stub.contentValues).withPutResolver(stub.putResolver).prepare().executeAsBlocking();
        Mockito.verifyNoMoreInteractions(stub.storIOSQLite, stub.lowLevel);
    }

    @Test
    public void shouldWrapExceptionIntoStorIOExceptionFlowable() {
        final PutContentValuesStub stub = PutContentValuesStub.newPutStubForMultipleContentValues(true);
        IllegalStateException testException = new IllegalStateException("test exception");
        Mockito.doThrow(testException).when(stub.putResolver).performPut(ArgumentMatchers.eq(stub.storIOSQLite), ArgumentMatchers.any(ContentValues.class));
        final TestSubscriber<Object> testSubscriber = new TestSubscriber<Object>();
        stub.storIOSQLite.put().contentValues(stub.contentValues).withPutResolver(stub.putResolver).prepare().asRxFlowable(BackpressureStrategy.MISSING).subscribe(testSubscriber);
        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertNoValues();
        testSubscriber.assertError(StorIOException.class);
        // noinspection ThrowableResultOfMethodCallIgnored
        StorIOException expected = ((StorIOException) (testSubscriber.errors().get(0)));
        assertThat(expected).hasMessageStartingWith("Error has occurred during Put operation. contentValues =");
        IllegalStateException cause = ((IllegalStateException) (expected.getCause()));
        assertThat(cause).hasMessage("test exception");
        Mockito.verify(stub.storIOSQLite).put();
        Mockito.verify(stub.storIOSQLite).lowLevel();
        Mockito.verify(stub.lowLevel).beginTransaction();
        Mockito.verify(stub.lowLevel).endTransaction();
        Mockito.verify(stub.storIOSQLite).defaultRxScheduler();
        Mockito.verify(stub.storIOSQLite).interceptors();
        Mockito.verifyNoMoreInteractions(stub.storIOSQLite, stub.lowLevel);
    }

    @Test
    public void shouldWrapExceptionIntoStorIOExceptionSingle() {
        final PutContentValuesStub stub = PutContentValuesStub.newPutStubForMultipleContentValues(true);
        IllegalStateException testException = new IllegalStateException("test exception");
        Mockito.doThrow(testException).when(stub.putResolver).performPut(ArgumentMatchers.eq(stub.storIOSQLite), ArgumentMatchers.any(ContentValues.class));
        final TestObserver<Object> testObserver = new TestObserver<Object>();
        stub.storIOSQLite.put().contentValues(stub.contentValues).withPutResolver(stub.putResolver).prepare().asRxSingle().subscribe(testObserver);
        testObserver.awaitTerminalEvent();
        testObserver.assertNoValues();
        testObserver.assertError(StorIOException.class);
        // noinspection ThrowableResultOfMethodCallIgnored
        StorIOException expected = ((StorIOException) (testObserver.errors().get(0)));
        assertThat(expected).hasMessageStartingWith("Error has occurred during Put operation. contentValues =");
        IllegalStateException cause = ((IllegalStateException) (expected.getCause()));
        assertThat(cause).hasMessage("test exception");
        Mockito.verify(stub.storIOSQLite).put();
        Mockito.verify(stub.storIOSQLite).lowLevel();
        Mockito.verify(stub.lowLevel).beginTransaction();
        Mockito.verify(stub.lowLevel).endTransaction();
        Mockito.verify(stub.storIOSQLite).defaultRxScheduler();
        Mockito.verify(stub.storIOSQLite).interceptors();
        Mockito.verifyNoMoreInteractions(stub.storIOSQLite, stub.lowLevel);
    }

    @Test
    public void shouldWrapExceptionIntoStorIOExceptionCompletable() {
        final PutContentValuesStub stub = PutContentValuesStub.newPutStubForMultipleContentValues(true);
        IllegalStateException testException = new IllegalStateException("test exception");
        Mockito.doThrow(testException).when(stub.putResolver).performPut(ArgumentMatchers.eq(stub.storIOSQLite), ArgumentMatchers.any(ContentValues.class));
        final TestObserver testObserver = new TestObserver();
        stub.storIOSQLite.put().contentValues(stub.contentValues).withPutResolver(stub.putResolver).prepare().asRxCompletable().subscribe(testObserver);
        testObserver.awaitTerminalEvent();
        testObserver.assertNoValues();
        testObserver.assertError(StorIOException.class);
        // noinspection ThrowableResultOfMethodCallIgnored
        StorIOException expected = ((StorIOException) (testObserver.errors().get(0)));
        assertThat(expected).hasMessageStartingWith("Error has occurred during Put operation. contentValues =");
        IllegalStateException cause = ((IllegalStateException) (expected.getCause()));
        assertThat(cause).hasMessage("test exception");
        Mockito.verify(stub.storIOSQLite).put();
        Mockito.verify(stub.storIOSQLite).lowLevel();
        Mockito.verify(stub.lowLevel).beginTransaction();
        Mockito.verify(stub.lowLevel).endTransaction();
        Mockito.verify(stub.storIOSQLite).defaultRxScheduler();
        Mockito.verify(stub.storIOSQLite).interceptors();
        Mockito.verifyNoMoreInteractions(stub.storIOSQLite, stub.lowLevel);
    }

    @Test
    public void shouldNotNotifyIfCollectionEmptyWithoutTransaction() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForEmptyCollectionWithoutTransaction();
        final PutResults<ContentValues> putResults = putStub.storIOSQLite.put().objects(putStub.contentValues).useTransaction(false).prepare().executeAsBlocking();
        putStub.verifyBehaviorForMultipleContentValues(putResults);
    }

    @Test
    public void shouldNotNotifyIfCollectionEmptyWithTransaction() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForEmptyCollectionWithTransaction();
        final PutResults<ContentValues> putResults = putStub.storIOSQLite.put().objects(putStub.contentValues).useTransaction(true).prepare().executeAsBlocking();
        putStub.verifyBehaviorForMultipleContentValues(putResults);
    }

    @Test
    public void shouldNotNotifyIfWasNotInsertedAndUpdatedWithoutTransaction() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForMultipleContentValuesWithoutInsertsAndUpdatesWithoutTransaction();
        final PutResults<ContentValues> putResults = putStub.storIOSQLite.put().objects(putStub.contentValues).useTransaction(false).withPutResolver(putStub.putResolver).prepare().executeAsBlocking();
        putStub.verifyBehaviorForMultipleContentValues(putResults);
    }

    @Test
    public void shouldNotNotifyIfWasNotInsertedAndUpdatedWithTransaction() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForMultipleContentValuesWithoutInsertsAndUpdatesWithTransaction();
        final PutResults<ContentValues> putResults = putStub.storIOSQLite.put().objects(putStub.contentValues).useTransaction(true).withPutResolver(putStub.putResolver).prepare().executeAsBlocking();
        putStub.verifyBehaviorForMultipleContentValues(putResults);
    }
}

