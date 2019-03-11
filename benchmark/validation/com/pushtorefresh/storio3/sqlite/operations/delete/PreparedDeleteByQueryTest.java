package com.pushtorefresh.storio3.sqlite.operations.delete;


import StorIOSQLite.LowLevel;
import com.pushtorefresh.storio3.StorIOException;
import com.pushtorefresh.storio3.sqlite.Changes;
import com.pushtorefresh.storio3.sqlite.StorIOSQLite;
import com.pushtorefresh.storio3.sqlite.operations.SchedulerChecker;
import com.pushtorefresh.storio3.sqlite.queries.DeleteQuery;
import io.reactivex.BackpressureStrategy;
import io.reactivex.observers.TestObserver;
import io.reactivex.subscribers.TestSubscriber;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class PreparedDeleteByQueryTest {
    private class DeleteByQueryStub {
        final StorIOSQLite storIOSQLite;

        final DeleteQuery deleteQuery;

        final DeleteResolver<DeleteQuery> deleteResolver;

        final LowLevel lowLevel;

        final DeleteResult expectedDeleteResult;

        private DeleteByQueryStub() {
            storIOSQLite = Mockito.mock(StorIOSQLite.class);
            lowLevel = Mockito.mock(LowLevel.class);
            Mockito.when(storIOSQLite.lowLevel()).thenReturn(lowLevel);
            deleteQuery = DeleteQuery.builder().table("test_table").where("column1 = ?").whereArgs(1).affectsTags("test_tag").build();
            // noinspection unchecked
            deleteResolver = Mockito.mock(DeleteResolver.class);
            expectedDeleteResult = DeleteResult.newInstance(1, deleteQuery.table(), deleteQuery.affectsTags());
            Mockito.when(deleteResolver.performDelete(ArgumentMatchers.same(storIOSQLite), ArgumentMatchers.same(deleteQuery))).thenReturn(expectedDeleteResult);
        }

        void verifyBehaviour() {
            Mockito.verify(storIOSQLite).lowLevel();
            Mockito.verify(storIOSQLite).interceptors();
            Mockito.verify(deleteResolver).performDelete(ArgumentMatchers.same(storIOSQLite), ArgumentMatchers.same(deleteQuery));
            Mockito.verify(lowLevel).notifyAboutChanges(Changes.newInstance(deleteQuery.table(), deleteQuery.affectsTags()));
            Mockito.verifyNoMoreInteractions(storIOSQLite, lowLevel, deleteResolver);
        }
    }

    @Test
    public void shouldReturnQueryInGetData() {
        final PreparedDeleteByQueryTest.DeleteByQueryStub stub = new PreparedDeleteByQueryTest.DeleteByQueryStub();
        final PreparedDeleteByQuery prepared = new PreparedDeleteByQuery.Builder(stub.storIOSQLite, stub.deleteQuery).withDeleteResolver(stub.deleteResolver).prepare();
        assertThat(prepared.getData()).isEqualTo(stub.deleteQuery);
    }

    @Test
    public void shouldPerformDeletionByQueryBlocking() {
        final PreparedDeleteByQueryTest.DeleteByQueryStub stub = new PreparedDeleteByQueryTest.DeleteByQueryStub();
        final DeleteResult actualDeleteResult = new PreparedDeleteByQuery.Builder(stub.storIOSQLite, stub.deleteQuery).withDeleteResolver(stub.deleteResolver).prepare().executeAsBlocking();
        assertThat(actualDeleteResult).isEqualTo(stub.expectedDeleteResult);
        stub.verifyBehaviour();
    }

    @Test
    public void shouldPerformDeletionByQueryFlowable() {
        final PreparedDeleteByQueryTest.DeleteByQueryStub stub = new PreparedDeleteByQueryTest.DeleteByQueryStub();
        final TestSubscriber<DeleteResult> testSubscriber = new TestSubscriber<DeleteResult>();
        new PreparedDeleteByQuery.Builder(stub.storIOSQLite, stub.deleteQuery).withDeleteResolver(stub.deleteResolver).prepare().asRxFlowable(BackpressureStrategy.MISSING).subscribe(testSubscriber);
        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertNoErrors();
        testSubscriber.assertValue(stub.expectedDeleteResult);
        Mockito.verify(stub.storIOSQLite).defaultRxScheduler();
        stub.verifyBehaviour();
    }

    @Test
    public void shouldPerformDeletionByQuerySingle() {
        final PreparedDeleteByQueryTest.DeleteByQueryStub stub = new PreparedDeleteByQueryTest.DeleteByQueryStub();
        final TestObserver<DeleteResult> testObserver = new TestObserver<DeleteResult>();
        new PreparedDeleteByQuery.Builder(stub.storIOSQLite, stub.deleteQuery).withDeleteResolver(stub.deleteResolver).prepare().asRxSingle().subscribe(testObserver);
        testObserver.awaitTerminalEvent();
        testObserver.assertNoErrors();
        testObserver.assertValue(stub.expectedDeleteResult);
        Mockito.verify(stub.storIOSQLite).defaultRxScheduler();
        stub.verifyBehaviour();
    }

    @Test
    public void shouldPerformDeletionByQueryCompletable() {
        final PreparedDeleteByQueryTest.DeleteByQueryStub stub = new PreparedDeleteByQueryTest.DeleteByQueryStub();
        final TestObserver<DeleteResult> testObserver = new TestObserver<DeleteResult>();
        new PreparedDeleteByQuery.Builder(stub.storIOSQLite, stub.deleteQuery).withDeleteResolver(stub.deleteResolver).prepare().asRxCompletable().subscribe(testObserver);
        testObserver.awaitTerminalEvent();
        testObserver.assertNoErrors();
        testObserver.assertNoValues();
        Mockito.verify(stub.storIOSQLite).defaultRxScheduler();
        stub.verifyBehaviour();
    }

    @Test
    public void shouldWrapExceptionIntoStorIOExceptionBlocking() {
        final StorIOSQLite storIOSQLite = Mockito.mock(StorIOSQLite.class);
        final StorIOSQLite.LowLevel lowLevel = Mockito.mock(LowLevel.class);
        Mockito.when(storIOSQLite.lowLevel()).thenReturn(lowLevel);
        // noinspection unchecked
        final DeleteResolver<DeleteQuery> deleteResolver = Mockito.mock(DeleteResolver.class);
        Mockito.when(deleteResolver.performDelete(ArgumentMatchers.same(storIOSQLite), ArgumentMatchers.any(DeleteQuery.class))).thenThrow(new IllegalStateException("test exception"));
        try {
            new PreparedDeleteByQuery.Builder(storIOSQLite, DeleteQuery.builder().table("test_table").build()).withDeleteResolver(deleteResolver).prepare().executeAsBlocking();
            failBecauseExceptionWasNotThrown(StorIOException.class);
        } catch (StorIOException expected) {
            IllegalStateException cause = ((IllegalStateException) (expected.getCause()));
            assertThat(cause).hasMessage("test exception");
            Mockito.verify(deleteResolver).performDelete(ArgumentMatchers.same(storIOSQLite), ArgumentMatchers.any(DeleteQuery.class));
            Mockito.verify(storIOSQLite).interceptors();
            Mockito.verifyNoMoreInteractions(storIOSQLite, lowLevel, deleteResolver);
        }
    }

    @Test
    public void shouldWrapExceptionIntoStorIOExceptionFlowable() {
        final StorIOSQLite storIOSQLite = Mockito.mock(StorIOSQLite.class);
        final StorIOSQLite.LowLevel lowLevel = Mockito.mock(LowLevel.class);
        Mockito.when(storIOSQLite.lowLevel()).thenReturn(lowLevel);
        // noinspection unchecked
        final DeleteResolver<DeleteQuery> deleteResolver = Mockito.mock(DeleteResolver.class);
        Mockito.when(deleteResolver.performDelete(ArgumentMatchers.same(storIOSQLite), ArgumentMatchers.any(DeleteQuery.class))).thenThrow(new IllegalStateException("test exception"));
        final TestSubscriber<DeleteResult> testSubscriber = new TestSubscriber<DeleteResult>();
        new PreparedDeleteByQuery.Builder(storIOSQLite, DeleteQuery.builder().table("test_table").build()).withDeleteResolver(deleteResolver).prepare().asRxFlowable(BackpressureStrategy.MISSING).subscribe(testSubscriber);
        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertNoValues();
        testSubscriber.assertError(StorIOException.class);
        // noinspection ThrowableResultOfMethodCallIgnored
        StorIOException expected = ((StorIOException) (testSubscriber.errors().get(0)));
        IllegalStateException cause = ((IllegalStateException) (expected.getCause()));
        assertThat(cause).hasMessage("test exception");
        Mockito.verify(deleteResolver).performDelete(ArgumentMatchers.same(storIOSQLite), ArgumentMatchers.any(DeleteQuery.class));
        Mockito.verify(storIOSQLite).defaultRxScheduler();
        Mockito.verify(storIOSQLite).interceptors();
        Mockito.verifyNoMoreInteractions(storIOSQLite, lowLevel, deleteResolver);
    }

    @Test
    public void shouldWrapExceptionIntoStorIOExceptionSingle() {
        final StorIOSQLite storIOSQLite = Mockito.mock(StorIOSQLite.class);
        final StorIOSQLite.LowLevel lowLevel = Mockito.mock(LowLevel.class);
        Mockito.when(storIOSQLite.lowLevel()).thenReturn(lowLevel);
        // noinspection unchecked
        final DeleteResolver<DeleteQuery> deleteResolver = Mockito.mock(DeleteResolver.class);
        Mockito.when(deleteResolver.performDelete(ArgumentMatchers.same(storIOSQLite), ArgumentMatchers.any(DeleteQuery.class))).thenThrow(new IllegalStateException("test exception"));
        final TestObserver<DeleteResult> testObserver = new TestObserver<DeleteResult>();
        new PreparedDeleteByQuery.Builder(storIOSQLite, DeleteQuery.builder().table("test_table").build()).withDeleteResolver(deleteResolver).prepare().asRxSingle().subscribe(testObserver);
        testObserver.awaitTerminalEvent();
        testObserver.assertNoValues();
        testObserver.assertError(StorIOException.class);
        // noinspection ThrowableResultOfMethodCallIgnored
        StorIOException expected = ((StorIOException) (testObserver.errors().get(0)));
        IllegalStateException cause = ((IllegalStateException) (expected.getCause()));
        assertThat(cause).hasMessage("test exception");
        Mockito.verify(deleteResolver).performDelete(ArgumentMatchers.same(storIOSQLite), ArgumentMatchers.any(DeleteQuery.class));
        Mockito.verify(storIOSQLite).defaultRxScheduler();
        Mockito.verify(storIOSQLite).interceptors();
        Mockito.verifyNoMoreInteractions(storIOSQLite, lowLevel, deleteResolver);
    }

    @Test
    public void shouldWrapExceptionIntoStorIOExceptionCompletable() {
        final StorIOSQLite storIOSQLite = Mockito.mock(StorIOSQLite.class);
        final StorIOSQLite.LowLevel lowLevel = Mockito.mock(LowLevel.class);
        Mockito.when(storIOSQLite.lowLevel()).thenReturn(lowLevel);
        // noinspection unchecked
        final DeleteResolver<DeleteQuery> deleteResolver = Mockito.mock(DeleteResolver.class);
        Mockito.when(deleteResolver.performDelete(ArgumentMatchers.same(storIOSQLite), ArgumentMatchers.any(DeleteQuery.class))).thenThrow(new IllegalStateException("test exception"));
        final TestObserver<DeleteResult> testObserver = new TestObserver<DeleteResult>();
        new PreparedDeleteByQuery.Builder(storIOSQLite, DeleteQuery.builder().table("test_table").build()).withDeleteResolver(deleteResolver).prepare().asRxCompletable().subscribe(testObserver);
        testObserver.awaitTerminalEvent();
        testObserver.assertNoValues();
        testObserver.assertError(StorIOException.class);
        // noinspection ThrowableResultOfMethodCallIgnored
        StorIOException expected = ((StorIOException) (testObserver.errors().get(0)));
        IllegalStateException cause = ((IllegalStateException) (expected.getCause()));
        assertThat(cause).hasMessage("test exception");
        Mockito.verify(storIOSQLite).defaultRxScheduler();
        Mockito.verify(deleteResolver).performDelete(ArgumentMatchers.same(storIOSQLite), ArgumentMatchers.any(DeleteQuery.class));
        Mockito.verify(storIOSQLite).interceptors();
        Mockito.verifyNoMoreInteractions(storIOSQLite, lowLevel, deleteResolver);
    }

    @Test
    public void shouldNotNotifyIfWasNotDeleted() {
        final StorIOSQLite storIOSQLite = Mockito.mock(StorIOSQLite.class);
        final StorIOSQLite.LowLevel lowLevel = Mockito.mock(LowLevel.class);
        Mockito.when(storIOSQLite.lowLevel()).thenReturn(lowLevel);
        final DeleteQuery deleteQuery = DeleteQuery.builder().table("test_table").where("column1 = ?").whereArgs(1).build();
        // noinspection unchecked
        final DeleteResolver<DeleteQuery> deleteResolver = Mockito.mock(DeleteResolver.class);
        final DeleteResult expectedDeleteResult = DeleteResult.newInstance(0, deleteQuery.table());// No items were deleted

        Mockito.when(deleteResolver.performDelete(ArgumentMatchers.same(storIOSQLite), ArgumentMatchers.same(deleteQuery))).thenReturn(expectedDeleteResult);
        final DeleteResult actualDeleteResult = new PreparedDeleteByQuery.Builder(storIOSQLite, deleteQuery).withDeleteResolver(deleteResolver).prepare().executeAsBlocking();
        assertThat(actualDeleteResult).isEqualTo(expectedDeleteResult);
        Mockito.verify(deleteResolver).performDelete(ArgumentMatchers.same(storIOSQLite), ArgumentMatchers.same(deleteQuery));
        Mockito.verify(lowLevel, Mockito.never()).notifyAboutChanges(ArgumentMatchers.any(Changes.class));
        Mockito.verify(storIOSQLite).interceptors();
        Mockito.verifyNoMoreInteractions(storIOSQLite, lowLevel, deleteResolver);
    }

    @Test
    public void deleteByQueryFlowableExecutesOnSpecifiedScheduler() {
        final PreparedDeleteByQueryTest.DeleteByQueryStub stub = new PreparedDeleteByQueryTest.DeleteByQueryStub();
        final SchedulerChecker schedulerChecker = SchedulerChecker.create(stub.storIOSQLite);
        final PreparedDeleteByQuery operation = new PreparedDeleteByQuery.Builder(stub.storIOSQLite, stub.deleteQuery).withDeleteResolver(stub.deleteResolver).prepare();
        schedulerChecker.checkAsFlowable(operation);
    }

    @Test
    public void deleteByQuerySingleExecutesOnSpecifiedScheduler() {
        final PreparedDeleteByQueryTest.DeleteByQueryStub stub = new PreparedDeleteByQueryTest.DeleteByQueryStub();
        final SchedulerChecker schedulerChecker = SchedulerChecker.create(stub.storIOSQLite);
        final PreparedDeleteByQuery operation = new PreparedDeleteByQuery.Builder(stub.storIOSQLite, stub.deleteQuery).withDeleteResolver(stub.deleteResolver).prepare();
        schedulerChecker.checkAsSingle(operation);
    }

    @Test
    public void deleteByQueryCompletableExecutesOnSpecifiedScheduler() {
        final PreparedDeleteByQueryTest.DeleteByQueryStub stub = new PreparedDeleteByQueryTest.DeleteByQueryStub();
        final SchedulerChecker schedulerChecker = SchedulerChecker.create(stub.storIOSQLite);
        final PreparedDeleteByQuery operation = new PreparedDeleteByQuery.Builder(stub.storIOSQLite, stub.deleteQuery).withDeleteResolver(stub.deleteResolver).prepare();
        schedulerChecker.checkAsCompletable(operation);
    }
}

