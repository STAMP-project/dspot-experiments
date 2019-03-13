package com.pushtorefresh.storio3.contentresolver.operations.get;


import BackpressureStrategy.MISSING;
import PreparedGetCursor.CompleteBuilder.STANDARD_GET_RESOLVER;
import StorIOContentResolver.LowLevel;
import android.database.Cursor;
import android.net.Uri;
import com.pushtorefresh.storio3.StorIOException;
import com.pushtorefresh.storio3.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio3.contentresolver.operations.SchedulerChecker;
import com.pushtorefresh.storio3.contentresolver.queries.Query;
import io.reactivex.Flowable;
import io.reactivex.Single;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class PreparedGetCursorTest {
    @Test
    public void shouldReturnQueryInGetData() {
        final GetCursorStub getStub = GetCursorStub.newInstance();
        final PreparedGetCursor operation = getStub.storIOContentResolver.get().cursor().withQuery(getStub.query).withGetResolver(getStub.getResolver).prepare();
        assertThat(operation.getData()).isEqualTo(getStub.query);
    }

    @Test
    public void getCursorBlocking() {
        final GetCursorStub getStub = GetCursorStub.newInstance();
        final Cursor cursor = getStub.storIOContentResolver.get().cursor().withQuery(getStub.query).withGetResolver(getStub.getResolver).prepare().executeAsBlocking();
        getStub.verifyQueryBehaviorForCursor(cursor);
    }

    @Test
    public void getCursorFlowable() {
        final GetCursorStub getStub = GetCursorStub.newInstance();
        final Flowable<Cursor> cursorFlowable = getStub.storIOContentResolver.get().cursor().withQuery(getStub.query).withGetResolver(getStub.getResolver).prepare().asRxFlowable(MISSING).take(1);
        getStub.verifyQueryBehaviorForCursor(cursorFlowable);
    }

    @Test
    public void getCursorSingle() {
        final GetCursorStub getStub = GetCursorStub.newInstance();
        final Single<Cursor> cursorFlowable = getStub.storIOContentResolver.get().cursor().withQuery(getStub.query).withGetResolver(getStub.getResolver).prepare().asRxSingle();
        getStub.verifyQueryBehaviorForCursor(cursorFlowable);
    }

    @Test
    public void shouldUseStandardGetResolverWithoutExplicitlyPassed() {
        StorIOContentResolver storIOContentResolver = Mockito.mock(StorIOContentResolver.class);
        StorIOContentResolver.LowLevel lowLevel = Mockito.mock(LowLevel.class);
        Mockito.when(storIOContentResolver.lowLevel()).thenReturn(lowLevel);
        Query query = Query.builder().uri(Mockito.mock(Uri.class)).build();
        new PreparedGetCursor.Builder(storIOContentResolver).withQuery(query).prepare().executeAsBlocking();
        Mockito.verify(storIOContentResolver).lowLevel();
        Mockito.verify(storIOContentResolver).interceptors();
        Mockito.verify(lowLevel).query(query);
        Mockito.verifyNoMoreInteractions(storIOContentResolver, lowLevel);
    }

    @Test
    public void checkThatStandardGetResolverDoesNotModifyCursor() {
        StorIOContentResolver storIOContentResolver = Mockito.mock(StorIOContentResolver.class);
        Cursor cursor = Mockito.mock(Cursor.class);
        Cursor cursorAfterMap = STANDARD_GET_RESOLVER.mapFromCursor(storIOContentResolver, cursor);
        assertThat(cursorAfterMap).isEqualTo(cursor);
    }

    @Test
    public void getCursorFlowableExecutesOnSpecifiedScheduler() {
        final GetCursorStub getStub = GetCursorStub.newInstance();
        final SchedulerChecker schedulerChecker = SchedulerChecker.create(getStub.storIOContentResolver);
        final PreparedGetCursor operation = getStub.storIOContentResolver.get().cursor().withQuery(getStub.query).withGetResolver(getStub.getResolver).prepare();
        schedulerChecker.checkAsFlowable(operation);
    }

    @Test
    public void getCursorSingleExecutesOnSpecifiedScheduler() {
        final GetCursorStub getStub = GetCursorStub.newInstance();
        final SchedulerChecker schedulerChecker = SchedulerChecker.create(getStub.storIOContentResolver);
        final PreparedGetCursor operation = getStub.storIOContentResolver.get().cursor().withQuery(getStub.query).withGetResolver(getStub.getResolver).prepare();
        schedulerChecker.checkAsSingle(operation);
    }

    @Test
    public void shouldWrapExceptionIntoStorIOException() {
        final GetCursorStub stub = GetCursorStub.newInstance();
        Throwable throwable = new IllegalStateException("Test exception");
        Mockito.when(stub.getResolver.performGet(ArgumentMatchers.any(StorIOContentResolver.class), ArgumentMatchers.any(Query.class))).thenThrow(throwable);
        final PreparedGetCursor operation = stub.storIOContentResolver.get().cursor().withQuery(stub.query).withGetResolver(stub.getResolver).prepare();
        try {
            operation.executeAsBlocking();
            failBecauseExceptionWasNotThrown(StorIOException.class);
        } catch (StorIOException expected) {
            assertThat(expected).hasMessageStartingWith("Error has occurred during Get operation. query = ").hasCause(throwable);
        }
    }
}

