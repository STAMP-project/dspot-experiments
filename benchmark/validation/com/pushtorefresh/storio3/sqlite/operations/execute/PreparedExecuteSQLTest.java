package com.pushtorefresh.storio3.sqlite.operations.execute;


import StorIOSQLite.LowLevel;
import android.support.annotation.NonNull;
import com.pushtorefresh.storio3.StorIOException;
import com.pushtorefresh.storio3.sqlite.Changes;
import com.pushtorefresh.storio3.sqlite.StorIOSQLite;
import com.pushtorefresh.storio3.sqlite.operations.SchedulerChecker;
import com.pushtorefresh.storio3.sqlite.queries.RawQuery;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import io.reactivex.subscribers.TestSubscriber;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class PreparedExecuteSQLTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldReturnRawQueryInGetData() {
        final PreparedExecuteSQLTest.Stub stub = PreparedExecuteSQLTest.Stub.newInstanceDoNotApplyAffectedTablesAndTags();
        final PreparedExecuteSQL operation = stub.storIOSQLite.executeSQL().withQuery(stub.rawQuery).prepare();
        assertThat(operation.getData()).isEqualTo(stub.rawQuery);
    }

    @Test
    public void executeSQLBlockingWithoutNotifications() {
        final PreparedExecuteSQLTest.Stub stub = PreparedExecuteSQLTest.Stub.newInstanceDoNotApplyAffectedTablesAndTags();
        stub.storIOSQLite.executeSQL().withQuery(stub.rawQuery).prepare().executeAsBlocking();
        Mockito.verify(stub.storIOSQLite, Mockito.never()).defaultRxScheduler();
        stub.verifyBehavior();
    }

    @Test
    public void executeSQLBlockingWithEmptyAffectedTablesAndTags() {
        final PreparedExecuteSQLTest.Stub stub = PreparedExecuteSQLTest.Stub.newInstanceApplyEmptyAffectedTablesAndTags();
        stub.storIOSQLite.executeSQL().withQuery(stub.rawQuery).prepare().executeAsBlocking();
        Mockito.verify(stub.storIOSQLite, Mockito.never()).defaultRxScheduler();
        stub.verifyBehavior();
    }

    @Test
    public void executeSQLBlockingWithNotification() {
        final PreparedExecuteSQLTest.Stub stub = PreparedExecuteSQLTest.Stub.newInstanceApplyNotEmptyAffectedTablesAndTags();
        stub.storIOSQLite.executeSQL().withQuery(stub.rawQuery).prepare().executeAsBlocking();
        Mockito.verify(stub.storIOSQLite, Mockito.never()).defaultRxScheduler();
        stub.verifyBehavior();
    }

    @Test
    public void executeSQLFlowableWithoutNotifications() {
        final PreparedExecuteSQLTest.Stub stub = PreparedExecuteSQLTest.Stub.newInstanceDoNotApplyAffectedTablesAndTags();
        final Flowable<Object> flowable = stub.storIOSQLite.executeSQL().withQuery(stub.rawQuery).prepare().asRxFlowable(BackpressureStrategy.MISSING);
        Mockito.verify(stub.storIOSQLite).defaultRxScheduler();
        stub.verifyBehavior(flowable);
    }

    @Test
    public void executeSQLSingleWithNotification() {
        final PreparedExecuteSQLTest.Stub stub = PreparedExecuteSQLTest.Stub.newInstanceApplyNotEmptyAffectedTablesAndTags();
        final Single<Object> single = stub.storIOSQLite.executeSQL().withQuery(stub.rawQuery).prepare().asRxSingle();
        Mockito.verify(stub.storIOSQLite).defaultRxScheduler();
        stub.verifyBehavior(single);
    }

    @Test
    public void executeSQLSingleWithoutNotifications() {
        final PreparedExecuteSQLTest.Stub stub = PreparedExecuteSQLTest.Stub.newInstanceDoNotApplyAffectedTablesAndTags();
        final Single<Object> single = stub.storIOSQLite.executeSQL().withQuery(stub.rawQuery).prepare().asRxSingle();
        Mockito.verify(stub.storIOSQLite).defaultRxScheduler();
        stub.verifyBehavior(single);
    }

    @Test
    public void executeSQLCompletableWithNotification() {
        final PreparedExecuteSQLTest.Stub stub = PreparedExecuteSQLTest.Stub.newInstanceApplyNotEmptyAffectedTablesAndTags();
        final Completable completable = stub.storIOSQLite.executeSQL().withQuery(stub.rawQuery).prepare().asRxCompletable();
        Mockito.verify(stub.storIOSQLite).defaultRxScheduler();
        stub.verifyBehavior(completable);
    }

    @Test
    public void executeSQLCompletableWithoutNotifications() {
        final PreparedExecuteSQLTest.Stub stub = PreparedExecuteSQLTest.Stub.newInstanceDoNotApplyAffectedTablesAndTags();
        final Completable completable = stub.storIOSQLite.executeSQL().withQuery(stub.rawQuery).prepare().asRxCompletable();
        Mockito.verify(stub.storIOSQLite).defaultRxScheduler();
        stub.verifyBehavior(completable);
    }

    @Test
    public void executeSQLFlowableWithNotification() {
        final PreparedExecuteSQLTest.Stub stub = PreparedExecuteSQLTest.Stub.newInstanceApplyNotEmptyAffectedTablesAndTags();
        final Flowable<Object> flowable = stub.storIOSQLite.executeSQL().withQuery(stub.rawQuery).prepare().asRxFlowable(BackpressureStrategy.MISSING);
        Mockito.verify(stub.storIOSQLite).defaultRxScheduler();
        stub.verifyBehavior(flowable);
    }

    @Test
    public void executeSQLFlowableExecutesOnSpecifiedScheduler() {
        final PreparedExecuteSQLTest.Stub stub = PreparedExecuteSQLTest.Stub.newInstanceApplyNotEmptyAffectedTablesAndTags();
        final SchedulerChecker schedulerChecker = SchedulerChecker.create(stub.storIOSQLite);
        final PreparedExecuteSQL operation = stub.storIOSQLite.executeSQL().withQuery(stub.rawQuery).prepare();
        schedulerChecker.checkAsFlowable(operation);
    }

    @Test
    public void executeSQLSingleExecutesOnSpecifiedScheduler() {
        final PreparedExecuteSQLTest.Stub stub = PreparedExecuteSQLTest.Stub.newInstanceApplyNotEmptyAffectedTablesAndTags();
        final SchedulerChecker schedulerChecker = SchedulerChecker.create(stub.storIOSQLite);
        final PreparedExecuteSQL operation = stub.storIOSQLite.executeSQL().withQuery(stub.rawQuery).prepare();
        schedulerChecker.checkAsSingle(operation);
    }

    @Test
    public void executeSQLCompletableExecutesOnSpecifiedScheduler() {
        final PreparedExecuteSQLTest.Stub stub = PreparedExecuteSQLTest.Stub.newInstanceApplyNotEmptyAffectedTablesAndTags();
        final SchedulerChecker schedulerChecker = SchedulerChecker.create(stub.storIOSQLite);
        final PreparedExecuteSQL operation = stub.storIOSQLite.executeSQL().withQuery(stub.rawQuery).prepare();
        schedulerChecker.checkAsCompletable(operation);
    }

    @Test
    public void shouldWrapExceptionIntoStorIOExceptionBlocking() {
        final PreparedExecuteSQLTest.Stub stub = PreparedExecuteSQLTest.Stub.newInstanceApplyNotEmptyAffectedTablesAndTags();
        IllegalStateException testException = new IllegalStateException("test exception");
        Mockito.doThrow(testException).when(stub.lowLevel).executeSQL(stub.rawQuery);
        expectedException.expect(StorIOException.class);
        expectedException.expectMessage("Error has occurred during ExecuteSQL operation. query = RawQuery{query='DROP TABLE users!', args=[], affectsTables=[test_table1, test_table2], affectsTags=[test_tag1, test_tag2], observesTables=[], observesTags=[]}");
        expectedException.expectCause(CoreMatchers.equalTo(testException));
        stub.storIOSQLite.executeSQL().withQuery(stub.rawQuery).prepare().executeAsBlocking();
        Mockito.verifyNoMoreInteractions(stub.storIOSQLite, stub.lowLevel);
    }

    @Test
    public void shouldWrapExceptionIntoStorIOExceptionFlowable() {
        final PreparedExecuteSQLTest.Stub stub = PreparedExecuteSQLTest.Stub.newInstanceApplyNotEmptyAffectedTablesAndTags();
        IllegalStateException testException = new IllegalStateException("test exception");
        Mockito.doThrow(testException).when(stub.lowLevel).executeSQL(stub.rawQuery);
        final TestSubscriber<Object> testSubscriber = new TestSubscriber<Object>();
        stub.storIOSQLite.executeSQL().withQuery(stub.rawQuery).prepare().asRxFlowable(BackpressureStrategy.MISSING).subscribe(testSubscriber);
        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertNoValues();
        testSubscriber.assertError(StorIOException.class);
        // noinspection ThrowableResultOfMethodCallIgnored
        StorIOException expected = ((StorIOException) (testSubscriber.errors().get(0)));
        IllegalStateException cause = ((IllegalStateException) (expected.getCause()));
        assertThat(cause).hasMessage("test exception");
        Mockito.verify(stub.storIOSQLite).executeSQL();
        Mockito.verify(stub.storIOSQLite).defaultRxScheduler();
        Mockito.verify(stub.storIOSQLite).lowLevel();
        Mockito.verify(stub.lowLevel).executeSQL(stub.rawQuery);
        Mockito.verify(stub.storIOSQLite).interceptors();
        Mockito.verifyNoMoreInteractions(stub.storIOSQLite, stub.lowLevel);
    }

    @Test
    public void shouldWrapExceptionIntoStorIOExceptionSingle() {
        final PreparedExecuteSQLTest.Stub stub = PreparedExecuteSQLTest.Stub.newInstanceApplyNotEmptyAffectedTablesAndTags();
        IllegalStateException testException = new IllegalStateException("test exception");
        Mockito.doThrow(testException).when(stub.lowLevel).executeSQL(stub.rawQuery);
        final TestObserver<Object> testObserver = new TestObserver<Object>();
        stub.storIOSQLite.executeSQL().withQuery(stub.rawQuery).prepare().asRxSingle().subscribe(testObserver);
        testObserver.awaitTerminalEvent();
        testObserver.assertNoValues();
        testObserver.assertError(StorIOException.class);
        // noinspection ThrowableResultOfMethodCallIgnored
        StorIOException expected = ((StorIOException) (testObserver.errors().get(0)));
        IllegalStateException cause = ((IllegalStateException) (expected.getCause()));
        assertThat(cause).hasMessage("test exception");
        Mockito.verify(stub.storIOSQLite).executeSQL();
        Mockito.verify(stub.storIOSQLite).defaultRxScheduler();
        Mockito.verify(stub.storIOSQLite).lowLevel();
        Mockito.verify(stub.lowLevel).executeSQL(stub.rawQuery);
        Mockito.verify(stub.storIOSQLite).interceptors();
        Mockito.verifyNoMoreInteractions(stub.storIOSQLite, stub.lowLevel);
    }

    @Test
    public void shouldWrapExceptionIntoStorIOExceptionCompletable() {
        final PreparedExecuteSQLTest.Stub stub = PreparedExecuteSQLTest.Stub.newInstanceApplyNotEmptyAffectedTablesAndTags();
        IllegalStateException testException = new IllegalStateException("test exception");
        Mockito.doThrow(testException).when(stub.lowLevel).executeSQL(stub.rawQuery);
        final TestObserver<Object> testObserver = new TestObserver<Object>();
        stub.storIOSQLite.executeSQL().withQuery(stub.rawQuery).prepare().asRxCompletable().subscribe(testObserver);
        testObserver.awaitTerminalEvent();
        testObserver.assertNoValues();
        testObserver.assertError(StorIOException.class);
        // noinspection ThrowableResultOfMethodCallIgnored
        StorIOException expected = ((StorIOException) (testObserver.errors().get(0)));
        IllegalStateException cause = ((IllegalStateException) (expected.getCause()));
        assertThat(cause).hasMessage("test exception");
        Mockito.verify(stub.storIOSQLite).executeSQL();
        Mockito.verify(stub.storIOSQLite).defaultRxScheduler();
        Mockito.verify(stub.storIOSQLite).lowLevel();
        Mockito.verify(stub.lowLevel).executeSQL(stub.rawQuery);
        Mockito.verify(stub.storIOSQLite).interceptors();
        Mockito.verifyNoMoreInteractions(stub.storIOSQLite, stub.lowLevel);
    }

    static class Stub {
        private final StorIOSQLite storIOSQLite;

        private final LowLevel lowLevel;

        private final RawQuery rawQuery;

        private final boolean applyAffectedTablesAndTags;

        @NonNull
        private static final String[] DEFAULT_AFFECTED_TABLES = new String[]{ "test_table1", "test_table2" };

        @NonNull
        private static final List<String> DEFAULT_AFFECTED_TAGS = Arrays.asList("test_tag1", "test_tag2");

        @NonNull
        private final String[] affectedTables;

        @NonNull
        private final List<String> affectedTags;

        @NonNull
        public static PreparedExecuteSQLTest.Stub newInstanceDoNotApplyAffectedTablesAndTags() {
            return new PreparedExecuteSQLTest.Stub(false, PreparedExecuteSQLTest.Stub.DEFAULT_AFFECTED_TABLES, PreparedExecuteSQLTest.Stub.DEFAULT_AFFECTED_TAGS);
        }

        @NonNull
        public static PreparedExecuteSQLTest.Stub newInstanceApplyEmptyAffectedTablesAndTags() {
            return new PreparedExecuteSQLTest.Stub(false, new String[0], Collections.<String>emptyList());
        }

        @NonNull
        public static PreparedExecuteSQLTest.Stub newInstanceApplyNotEmptyAffectedTablesAndTags() {
            return new PreparedExecuteSQLTest.Stub(true, PreparedExecuteSQLTest.Stub.DEFAULT_AFFECTED_TABLES, PreparedExecuteSQLTest.Stub.DEFAULT_AFFECTED_TAGS);
        }

        private Stub(boolean applyAffectedTablesAndTags, @NonNull
        String[] affectedTables, @NonNull
        List<String> affectedTags) {
            this.applyAffectedTablesAndTags = applyAffectedTablesAndTags;
            this.affectedTables = affectedTables;
            this.affectedTags = affectedTags;
            storIOSQLite = Mockito.mock(StorIOSQLite.class);
            lowLevel = Mockito.mock(LowLevel.class);
            if (applyAffectedTablesAndTags) {
                rawQuery = RawQuery.builder().query("DROP TABLE users!").affectsTables(affectedTables).affectsTags(affectedTags).build();
            } else {
                rawQuery = RawQuery.builder().query("DROP TABLE users!").build();
            }
            Mockito.when(storIOSQLite.lowLevel()).thenReturn(lowLevel);
            Mockito.when(storIOSQLite.executeSQL()).thenReturn(new PreparedExecuteSQL.Builder(storIOSQLite));
        }

        @SuppressWarnings("unchecked")
        void verifyBehavior() {
            // storIOSQLite.executeSQL() should be called once
            Mockito.verify(storIOSQLite).executeSQL();
            // storIOSQLite.lowLevel.executeSQL() should be called once for ANY RawQuery
            Mockito.verify(lowLevel).executeSQL(ArgumentMatchers.any(RawQuery.class));
            // storIOSQLite.lowLevel.executeSQL() should be called once for required RawQuery
            Mockito.verify(lowLevel).executeSQL(rawQuery);
            if ((applyAffectedTablesAndTags) && (((affectedTables.length) > 0) || (!(affectedTags.isEmpty())))) {
                final Changes changes = Changes.newInstance(new HashSet<String>(Arrays.asList(affectedTables)), new HashSet<String>(affectedTags));
                Mockito.verify(lowLevel).notifyAboutChanges(changes);
            } else {
                Mockito.verify(lowLevel, Mockito.never()).notifyAboutChanges(ArgumentMatchers.any(Changes.class));
            }
        }

        void verifyBehavior(@NonNull
        Flowable<Object> flowable) {
            new com.pushtorefresh.storio3.test.FlowableBehaviorChecker<Object>().flowable(flowable).expectedNumberOfEmissions(1).testAction(new io.reactivex.functions.Consumer<Object>() {
                @Override
                public void accept(Object anObject) {
                    verifyBehavior();
                }
            }).checkBehaviorOfFlowable();
        }

        void verifyBehavior(@NonNull
        Single<Object> single) {
            new com.pushtorefresh.storio3.test.FlowableBehaviorChecker<Object>().flowable(single.toFlowable()).expectedNumberOfEmissions(1).testAction(new io.reactivex.functions.Consumer<Object>() {
                @Override
                public void accept(Object anObject) {
                    verifyBehavior();
                }
            }).checkBehaviorOfFlowable();
        }

        void verifyBehavior(@NonNull
        Completable completable) {
            new com.pushtorefresh.storio3.test.FlowableBehaviorChecker<Object>().flowable(completable.toFlowable()).expectedNumberOfEmissions(1).testAction(new io.reactivex.functions.Consumer<Object>() {
                @Override
                public void accept(Object anObject) {
                    verifyBehavior();
                }
            }).checkBehaviorOfFlowable();
        }
    }
}

