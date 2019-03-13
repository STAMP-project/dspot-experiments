package com.pushtorefresh.storio3.contentresolver.operations.put;


import BackpressureStrategy.MISSING;
import android.content.ContentValues;
import com.pushtorefresh.storio3.StorIOException;
import com.pushtorefresh.storio3.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio3.contentresolver.operations.SchedulerChecker;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class PreparedPutContentValuesTest {
    @Test
    public void shouldReturnContentValuesInGetData() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForOneContentValues();
        final PreparedPutContentValues operation = putStub.storIOContentResolver.put().contentValues(putStub.contentValues.get(0)).withPutResolver(putStub.putResolver).prepare();
        assertThat(operation.getData()).isEqualTo(putStub.contentValues.get(0));
    }

    @Test
    public void putContentValuesBlocking() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForOneContentValues();
        final PutResult putResult = putStub.storIOContentResolver.put().contentValues(putStub.contentValues.get(0)).withPutResolver(putStub.putResolver).prepare().executeAsBlocking();
        putStub.verifyBehaviorForOneContentValues(putResult);
    }

    @Test
    public void putContentValuesFlowable() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForOneContentValues();
        final Flowable<PutResult> putResultFlowable = putStub.storIOContentResolver.put().contentValues(putStub.contentValues.get(0)).withPutResolver(putStub.putResolver).prepare().asRxFlowable(MISSING);
        putStub.verifyBehaviorForOneContentValues(putResultFlowable);
    }

    @Test
    public void putContentValuesSingle() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForOneContentValues();
        final Single<PutResult> putResultSingle = putStub.storIOContentResolver.put().contentValues(putStub.contentValues.get(0)).withPutResolver(putStub.putResolver).prepare().asRxSingle();
        putStub.verifyBehaviorForOneContentValues(putResultSingle);
    }

    @Test
    public void putContentValuesCompletable() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForOneContentValues();
        final Completable completable = putStub.storIOContentResolver.put().contentValues(putStub.contentValues.get(0)).withPutResolver(putStub.putResolver).prepare().asRxCompletable();
        putStub.verifyBehaviorForOneContentValues(completable);
    }

    @Test
    public void putContentValuesFlowableExecutesOnSpecifiedScheduler() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForOneContentValues();
        final SchedulerChecker schedulerChecker = SchedulerChecker.create(putStub.storIOContentResolver);
        final PreparedPutContentValues operation = putStub.storIOContentResolver.put().contentValues(putStub.contentValues.get(0)).withPutResolver(putStub.putResolver).prepare();
        schedulerChecker.checkAsFlowable(operation);
    }

    @Test
    public void putContentValuesSingleExecutesOnSpecifiedScheduler() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForOneContentValues();
        final SchedulerChecker schedulerChecker = SchedulerChecker.create(putStub.storIOContentResolver);
        final PreparedPutContentValues operation = putStub.storIOContentResolver.put().contentValues(putStub.contentValues.get(0)).withPutResolver(putStub.putResolver).prepare();
        schedulerChecker.checkAsSingle(operation);
    }

    @Test
    public void putContentValuesCompletableExecutesOnSpecifiedScheduler() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForOneContentValues();
        final SchedulerChecker schedulerChecker = SchedulerChecker.create(putStub.storIOContentResolver);
        final PreparedPutContentValues operation = putStub.storIOContentResolver.put().contentValues(putStub.contentValues.get(0)).withPutResolver(putStub.putResolver).prepare();
        schedulerChecker.checkAsCompletable(operation);
    }

    @Test
    public void shouldWrapExceptionIntoStorIOException() {
        final PutContentValuesStub stub = PutContentValuesStub.newPutStubForOneContentValues();
        Throwable throwable = new IllegalStateException("Test exception");
        Mockito.when(stub.putResolver.performPut(ArgumentMatchers.any(StorIOContentResolver.class), ArgumentMatchers.any(ContentValues.class))).thenThrow(throwable);
        final PreparedPutContentValues operation = stub.storIOContentResolver.put().contentValues(stub.contentValues.get(0)).withPutResolver(stub.putResolver).prepare();
        try {
            operation.executeAsBlocking();
            failBecauseExceptionWasNotThrown(StorIOException.class);
        } catch (StorIOException expected) {
            assertThat(expected).hasMessageStartingWith("Error has occurred during Put operation. contentValues = ").hasCause(throwable);
        }
    }
}

