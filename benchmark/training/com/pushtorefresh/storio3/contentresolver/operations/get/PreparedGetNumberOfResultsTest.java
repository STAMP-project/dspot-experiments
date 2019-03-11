package com.pushtorefresh.storio3.contentresolver.operations.get;


import BackpressureStrategy.MISSING;
import PreparedGetNumberOfResults.CompleteBuilder;
import android.database.Cursor;
import android.net.Uri;
import com.pushtorefresh.storio3.StorIOException;
import com.pushtorefresh.storio3.contentresolver.Changes;
import com.pushtorefresh.storio3.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio3.contentresolver.operations.SchedulerChecker;
import com.pushtorefresh.storio3.contentresolver.queries.Query;
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
        final PreparedGetNumberOfResults operation = getStub.storIOContentResolver.get().numberOfResults().withQuery(getStub.query).withGetResolver(getStub.getResolverForNumberOfResults).prepare();
        assertThat(operation.getData()).isEqualTo(getStub.query);
    }

    @Test
    public void shouldGetNumberOfResultsWithQueryBlocking() {
        final GetNumberOfResultsStub getStub = GetNumberOfResultsStub.newInstance();
        final Integer numberOfResults = getStub.storIOContentResolver.get().numberOfResults().withQuery(getStub.query).withGetResolver(getStub.getResolverForNumberOfResults).prepare().executeAsBlocking();
        getStub.verifyQueryBehaviorForInteger(numberOfResults);
    }

    @Test
    public void shouldGetNumberOfResultsWithQueryAsFlowable() {
        final GetNumberOfResultsStub getStub = GetNumberOfResultsStub.newInstance();
        final Flowable<Integer> numberOfResultsFlowable = getStub.storIOContentResolver.get().numberOfResults().withQuery(getStub.query).withGetResolver(getStub.getResolverForNumberOfResults).prepare().asRxFlowable(MISSING).take(1);
        getStub.verifyQueryBehaviorForInteger(numberOfResultsFlowable);
    }

    @Test
    public void shouldGetNumberOfResultsWithQueryAsSingle() {
        final GetNumberOfResultsStub getStub = GetNumberOfResultsStub.newInstance();
        final Single<Integer> numberOfResultsSingle = getStub.storIOContentResolver.get().numberOfResults().withQuery(getStub.query).withGetResolver(getStub.getResolverForNumberOfResults).prepare().asRxSingle();
        getStub.verifyQueryBehaviorForInteger(numberOfResultsSingle);
    }

    @Test
    public void shouldWrapExceptionIntoStorIOExceptionForBlocking() {
        final StorIOContentResolver storIOContentResolver = Mockito.mock(StorIOContentResolver.class);
        // noinspection unchecked
        final GetResolver<Integer> getResolver = Mockito.mock(GetResolver.class);
        Mockito.when(getResolver.performGet(ArgumentMatchers.eq(storIOContentResolver), ArgumentMatchers.any(Query.class))).thenThrow(new IllegalStateException("test exception"));
        try {
            new PreparedGetNumberOfResults.Builder(storIOContentResolver).withQuery(Query.builder().uri(Mockito.mock(Uri.class)).build()).withGetResolver(getResolver).prepare().executeAsBlocking();
            failBecauseExceptionWasNotThrown(StorIOException.class);
        } catch (StorIOException expected) {
            IllegalStateException cause = ((IllegalStateException) (expected.getCause()));
            assertThat(cause).hasMessage("test exception");
        }
    }

    @Test
    public void shouldWrapExceptionIntoStorIOExceptionForFlowable() {
        final StorIOContentResolver storIOContentResolver = Mockito.mock(StorIOContentResolver.class);
        Uri testUri = Mockito.mock(Uri.class);
        Mockito.when(storIOContentResolver.observeChangesOfUri(ArgumentMatchers.eq(testUri), ArgumentMatchers.eq(MISSING))).thenReturn(Flowable.<Changes>empty());
        // noinspection unchecked
        final GetResolver<Integer> getResolver = Mockito.mock(GetResolver.class);
        Mockito.when(getResolver.performGet(ArgumentMatchers.eq(storIOContentResolver), ArgumentMatchers.any(Query.class))).thenThrow(new IllegalStateException("test exception"));
        final TestSubscriber<Integer> testSubscriber = new TestSubscriber<Integer>();
        new PreparedGetNumberOfResults.Builder(storIOContentResolver).withQuery(Query.builder().uri(testUri).build()).withGetResolver(getResolver).prepare().asRxFlowable(MISSING).subscribe(testSubscriber);
        testSubscriber.awaitTerminalEvent(60, TimeUnit.SECONDS);
        testSubscriber.assertError(StorIOException.class);
        assertThat(testSubscriber.errors()).hasSize(1);
        StorIOException storIOException = ((StorIOException) (testSubscriber.errors().get(0)));
        IllegalStateException cause = ((IllegalStateException) (storIOException.getCause()));
        assertThat(cause).hasMessage("test exception");
        testSubscriber.dispose();
    }

    @Test
    public void shouldWrapExceptionIntoStorIOExceptionForSingle() {
        final StorIOContentResolver storIOContentResolver = Mockito.mock(StorIOContentResolver.class);
        Uri testUri = Mockito.mock(Uri.class);
        // noinspection unchecked
        final GetResolver<Integer> getResolver = Mockito.mock(GetResolver.class);
        Mockito.when(getResolver.performGet(ArgumentMatchers.eq(storIOContentResolver), ArgumentMatchers.any(Query.class))).thenThrow(new IllegalStateException("test exception"));
        final TestObserver<Integer> testObserver = new TestObserver<Integer>();
        new PreparedGetNumberOfResults.Builder(storIOContentResolver).withQuery(Query.builder().uri(testUri).build()).withGetResolver(getResolver).prepare().asRxSingle().subscribe(testObserver);
        testObserver.awaitTerminalEvent(60, TimeUnit.SECONDS);
        testObserver.assertError(StorIOException.class);
        assertThat(testObserver.errors()).hasSize(1);
        StorIOException storIOException = ((StorIOException) (testObserver.errors().get(0)));
        IllegalStateException cause = ((IllegalStateException) (storIOException.getCause()));
        assertThat(cause).hasMessage("test exception");
        testObserver.dispose();
    }

    @Test
    public void verifyThatStandardGetResolverJustReturnsCursorGetCount() {
        final GetNumberOfResultsStub getStub = GetNumberOfResultsStub.newInstance();
        final GetResolver<Integer> standardGetResolver = CompleteBuilder.STANDARD_GET_RESOLVER;
        final Cursor cursor = Mockito.mock(Cursor.class);
        Mockito.when(cursor.getCount()).thenReturn(12314);
        assertThat(standardGetResolver.mapFromCursor(getStub.storIOContentResolver, cursor)).isEqualTo(12314);
    }

    @Test
    public void getNumberOfResultsFlowableExecutesOnSpecifiedScheduler() {
        final GetNumberOfResultsStub getStub = GetNumberOfResultsStub.newInstance();
        final SchedulerChecker schedulerChecker = SchedulerChecker.create(getStub.storIOContentResolver);
        final PreparedGetNumberOfResults operation = getStub.storIOContentResolver.get().numberOfResults().withQuery(getStub.query).withGetResolver(getStub.getResolverForNumberOfResults).prepare();
        schedulerChecker.checkAsFlowable(operation);
    }

    @Test
    public void getNumberOfResultsSingleExecutesOnSpecifiedScheduler() {
        final GetNumberOfResultsStub getStub = GetNumberOfResultsStub.newInstance();
        final SchedulerChecker schedulerChecker = SchedulerChecker.create(getStub.storIOContentResolver);
        final PreparedGetNumberOfResults operation = getStub.storIOContentResolver.get().numberOfResults().withQuery(getStub.query).withGetResolver(getStub.getResolverForNumberOfResults).prepare();
        schedulerChecker.checkAsSingle(operation);
    }

    @Test
    public void shouldPassStorIOContentResolverToGetResolver() {
        final GetNumberOfResultsStub getStub = GetNumberOfResultsStub.newInstance();
        getStub.storIOContentResolver.get().numberOfResults().withQuery(getStub.query).withGetResolver(getStub.getResolverForNumberOfResults).prepare().executeAsBlocking();
        Mockito.verify(getStub.getResolverForNumberOfResults).mapFromCursor(ArgumentMatchers.eq(getStub.storIOContentResolver), ArgumentMatchers.any(Cursor.class));
    }
}

