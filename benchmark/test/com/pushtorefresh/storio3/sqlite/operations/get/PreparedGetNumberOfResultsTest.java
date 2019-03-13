package com.pushtorefresh.storio3.sqlite.operations.get;


import PreparedGetNumberOfResults.CompleteBuilder;
import android.database.Cursor;
import com.pushtorefresh.storio3.StorIOException;
import com.pushtorefresh.storio3.sqlite.Changes;
import com.pushtorefresh.storio3.sqlite.StorIOSQLite;
import com.pushtorefresh.storio3.sqlite.operations.SchedulerChecker;
import com.pushtorefresh.storio3.sqlite.queries.Query;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import io.reactivex.subscribers.TestSubscriber;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class PreparedGetNumberOfResultsTest {
    @Test
    public void shouldReturnQueryInGetData() {
        final GetNumberOfResultsStub getStub = GetNumberOfResultsStub.newInstance();
        final PreparedGetNumberOfResults operation = getStub.storIOSQLite.get().numberOfResults().withQuery(getStub.query).withGetResolver(getStub.getResolverForNumberOfResults).prepare();
        assertThat(operation.getData()).isEqualTo(getStub.query);
    }

    @Test
    public void shouldReturnRawQueryInGetData() {
        final GetNumberOfResultsStub getStub = GetNumberOfResultsStub.newInstance();
        final PreparedGetNumberOfResults operation = getStub.storIOSQLite.get().numberOfResults().withQuery(getStub.rawQuery).withGetResolver(getStub.getResolverForNumberOfResults).prepare();
        assertThat(operation.getData()).isEqualTo(getStub.rawQuery);
    }

    @Test
    public void shouldGetNumberOfResultsWithQueryBlocking() {
        final GetNumberOfResultsStub getStub = GetNumberOfResultsStub.newInstance();
        final Integer numberOfResults = getStub.storIOSQLite.get().numberOfResults().withQuery(getStub.query).withGetResolver(getStub.getResolverForNumberOfResults).prepare().executeAsBlocking();
        getStub.verifyQueryBehaviorForInteger(numberOfResults);
    }

    @Test
    public void shouldGetNumberOfResultsWithQueryAsFlowable() {
        final GetNumberOfResultsStub getStub = GetNumberOfResultsStub.newInstance();
        final Flowable<Integer> numberOfResultsFlowable = getStub.storIOSQLite.get().numberOfResults().withQuery(getStub.query).withGetResolver(getStub.getResolverForNumberOfResults).prepare().asRxFlowable(BackpressureStrategy.LATEST).take(1);
        getStub.verifyQueryBehaviorForInteger(numberOfResultsFlowable);
    }

    @Test
    public void shouldGetNumberOfResultsWithQueryAsSingle() {
        final GetNumberOfResultsStub getStub = GetNumberOfResultsStub.newInstance();
        final Single<Integer> numberOfResultsSingle = getStub.storIOSQLite.get().numberOfResults().withQuery(getStub.query).withGetResolver(getStub.getResolverForNumberOfResults).prepare().asRxSingle();
        getStub.verifyQueryBehaviorForInteger(numberOfResultsSingle);
    }

    @Test
    public void shouldGetNumberOfResultsWithRawQueryBlocking() {
        final GetNumberOfResultsStub getStub = GetNumberOfResultsStub.newInstance();
        final Integer numberOfResults = getStub.storIOSQLite.get().numberOfResults().withQuery(getStub.rawQuery).withGetResolver(getStub.getResolverForNumberOfResults).prepare().executeAsBlocking();
        getStub.verifyRawQueryBehaviorForInteger(numberOfResults);
    }

    @Test
    public void shouldGetNumberOfResultsWithRawQueryAsFlowable() {
        final GetNumberOfResultsStub getStub = GetNumberOfResultsStub.newInstance();
        final Flowable<Integer> numberOfResultsFlowable = getStub.storIOSQLite.get().numberOfResults().withQuery(getStub.rawQuery).withGetResolver(getStub.getResolverForNumberOfResults).prepare().asRxFlowable(BackpressureStrategy.LATEST).take(1);
        getStub.verifyRawQueryBehaviorForInteger(numberOfResultsFlowable);
    }

    @Test
    public void shouldGetNumberOfResultsWithRawQueryAsSingle() {
        final GetNumberOfResultsStub getStub = GetNumberOfResultsStub.newInstance();
        final Single<Integer> numberOfResultsSingle = getStub.storIOSQLite.get().numberOfResults().withQuery(getStub.rawQuery).withGetResolver(getStub.getResolverForNumberOfResults).prepare().asRxSingle();
        getStub.verifyRawQueryBehaviorForInteger(numberOfResultsSingle);
    }

    @Test
    public void shouldWrapExceptionIntoStorIOExceptionForBlocking() {
        final StorIOSQLite storIOSQLite = Mockito.mock(StorIOSQLite.class);
        // noinspection unchecked
        final GetResolver<Integer> getResolver = Mockito.mock(GetResolver.class);
        Mockito.when(getResolver.performGet(ArgumentMatchers.eq(storIOSQLite), ArgumentMatchers.any(Query.class))).thenThrow(new IllegalStateException("test exception"));
        try {
            new PreparedGetNumberOfResults.Builder(storIOSQLite).withQuery(Query.builder().table("test_table").build()).withGetResolver(getResolver).prepare().executeAsBlocking();
            failBecauseExceptionWasNotThrown(StorIOException.class);
        } catch (StorIOException expected) {
            IllegalStateException cause = ((IllegalStateException) (expected.getCause()));
            assertThat(cause).hasMessage("test exception");
        }
    }

    @Test
    public void shouldWrapExceptionIntoStorIOExceptionForFlowable() {
        final StorIOSQLite storIOSQLite = Mockito.mock(StorIOSQLite.class);
        Mockito.when(storIOSQLite.observeChanges(ArgumentMatchers.any(BackpressureStrategy.class))).thenReturn(Flowable.<Changes>empty());
        // noinspection unchecked
        final GetResolver<Integer> getResolver = Mockito.mock(GetResolver.class);
        Mockito.when(getResolver.performGet(ArgumentMatchers.eq(storIOSQLite), ArgumentMatchers.any(Query.class))).thenThrow(new IllegalStateException("test exception"));
        final TestSubscriber<Integer> testSubscriber = new TestSubscriber<Integer>();
        new PreparedGetNumberOfResults.Builder(storIOSQLite).withQuery(Query.builder().table("test_table").observesTags("test_tag").build()).withGetResolver(getResolver).prepare().asRxFlowable(BackpressureStrategy.LATEST).subscribe(testSubscriber);
        testSubscriber.awaitTerminalEvent(60, TimeUnit.SECONDS);
        testSubscriber.assertError(StorIOException.class);
        assertThat(testSubscriber.errorCount()).isEqualTo(1);
        StorIOException storIOException = ((StorIOException) (testSubscriber.errors().get(0)));
        IllegalStateException cause = ((IllegalStateException) (storIOException.getCause()));
        assertThat(cause).hasMessage("test exception");
        testSubscriber.dispose();
    }

    @Test
    public void shouldWrapExceptionIntoStorIOExceptionForSingle() {
        final StorIOSQLite storIOSQLite = Mockito.mock(StorIOSQLite.class);
        // noinspection unchecked
        final GetResolver<Integer> getResolver = Mockito.mock(GetResolver.class);
        Mockito.when(getResolver.performGet(ArgumentMatchers.eq(storIOSQLite), ArgumentMatchers.any(Query.class))).thenThrow(new IllegalStateException("test exception"));
        final TestObserver<Integer> testObserver = new TestObserver<Integer>();
        new PreparedGetNumberOfResults.Builder(storIOSQLite).withQuery(Query.builder().table("test_table").build()).withGetResolver(getResolver).prepare().asRxSingle().subscribe(testObserver);
        testObserver.awaitTerminalEvent(60, TimeUnit.SECONDS);
        testObserver.assertError(StorIOException.class);
        assertThat(testObserver.errorCount()).isEqualTo(1);
        StorIOException storIOException = ((StorIOException) (testObserver.errors().get(0)));
        IllegalStateException cause = ((IllegalStateException) (storIOException.getCause()));
        assertThat(cause).hasMessage("test exception");
    }

    @Test
    public void completeBuilderShouldThrowExceptionIfNoQueryWasSet() {
        PreparedGetNumberOfResults.CompleteBuilder completeBuilder = new PreparedGetNumberOfResults.Builder(Mockito.mock(StorIOSQLite.class)).withQuery(Query.builder().table("test_table").build());// We will null it later

        completeBuilder.query = null;
        try {
            completeBuilder.prepare();
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (IllegalStateException expected) {
            assertThat(expected).hasMessage("Please specify query");
        }
    }

    @Test
    public void executeAsBlockingShouldThrowExceptionIfNoQueryWasSet() {
        // noinspection unchecked,ConstantConditions
        PreparedGetNumberOfResults preparedGetNumberOfResults = new PreparedGetNumberOfResults(Mockito.mock(StorIOSQLite.class), ((Query) (null)), ((GetResolver<Integer>) (Mockito.mock(GetResolver.class))));
        try {
            preparedGetNumberOfResults.executeAsBlocking();
            failBecauseExceptionWasNotThrown(StorIOException.class);
        } catch (StorIOException expected) {
            IllegalStateException cause = ((IllegalStateException) (expected.getCause()));
            assertThat(cause).hasMessage("Please specify query");
        }
    }

    @Test
    public void asRxFlowableShouldThrowExceptionIfNoQueryWasSet() {
        // noinspection unchecked,ConstantConditions
        PreparedGetNumberOfResults preparedGetNumberOfResults = new PreparedGetNumberOfResults(Mockito.mock(StorIOSQLite.class), ((Query) (null)), ((GetResolver<Integer>) (Mockito.mock(GetResolver.class))));
        try {
            // noinspection CheckResult
            preparedGetNumberOfResults.asRxFlowable(BackpressureStrategy.LATEST);
            failBecauseExceptionWasNotThrown(StorIOException.class);
        } catch (IllegalStateException expected) {
            assertThat(expected).hasMessage("Please specify query");
        }
    }

    @Test
    public void verifyThatStandardGetResolverJustReturnsCursorGetCount() {
        final GetCursorStub getStub = GetCursorStub.newInstance();
        final GetResolver<Integer> standardGetResolver = CompleteBuilder.STANDARD_GET_RESOLVER;
        final Cursor cursor = Mockito.mock(Cursor.class);
        Mockito.when(cursor.getCount()).thenReturn(12314);
        assertThat(standardGetResolver.mapFromCursor(getStub.storIOSQLite, cursor)).isEqualTo(12314);
    }

    @Test
    public void getNumberOfResultsFlowableExecutesOnSpecifiedScheduler() {
        final GetNumberOfResultsStub getStub = GetNumberOfResultsStub.newInstance();
        final SchedulerChecker schedulerChecker = SchedulerChecker.create(getStub.storIOSQLite);
        final PreparedGetNumberOfResults operation = getStub.storIOSQLite.get().numberOfResults().withQuery(getStub.query).withGetResolver(getStub.getResolverForNumberOfResults).prepare();
        schedulerChecker.checkAsFlowable(operation);
    }

    @Test
    public void getNumberOfResultsSingleExecutesOnSpecifiedScheduler() {
        final GetNumberOfResultsStub getStub = GetNumberOfResultsStub.newInstance();
        final SchedulerChecker schedulerChecker = SchedulerChecker.create(getStub.storIOSQLite);
        final PreparedGetNumberOfResults operation = getStub.storIOSQLite.get().numberOfResults().withQuery(getStub.query).withGetResolver(getStub.getResolverForNumberOfResults).prepare();
        schedulerChecker.checkAsSingle(operation);
    }

    @Test
    public void shouldPassStorIOSQLiteToResolverOnQuery() {
        final GetNumberOfResultsStub getStub = GetNumberOfResultsStub.newInstance();
        getStub.storIOSQLite.get().numberOfResults().withQuery(getStub.query).withGetResolver(getStub.getResolverForNumberOfResults).prepare().executeAsBlocking();
        Mockito.verify(getStub.getResolverForNumberOfResults).mapFromCursor(ArgumentMatchers.eq(getStub.storIOSQLite), ArgumentMatchers.any(Cursor.class));
    }

    @Test
    public void shouldPassStorIOSQLiteToResolverOnRawQuery() {
        final GetNumberOfResultsStub getStub = GetNumberOfResultsStub.newInstance();
        getStub.storIOSQLite.get().numberOfResults().withQuery(getStub.rawQuery).withGetResolver(getStub.getResolverForNumberOfResults).prepare().executeAsBlocking();
        Mockito.verify(getStub.getResolverForNumberOfResults).mapFromCursor(ArgumentMatchers.eq(getStub.storIOSQLite), ArgumentMatchers.any(Cursor.class));
    }
}

