package com.pushtorefresh.storio3.sqlite.integration;


import android.database.Cursor;
import io.reactivex.BackpressureStrategy;
import io.reactivex.subscribers.TestSubscriber;
import org.junit.Test;


public class GetCursorObserveChangesTest extends BaseOperationObserveChangesTest {
    @Test
    public void repeatsOperationWithQueryByChangeOfTable() {
        putUserBlocking();
        TestSubscriber<Cursor> testSubscriber = new TestSubscriber<Cursor>();
        storIOSQLite.get().cursor().withQuery(query).prepare().asRxFlowable(BackpressureStrategy.MISSING).subscribe(testSubscriber);
        testSubscriber.assertValueCount(1);
        storIOSQLite.lowLevel().notifyAboutChanges(tableChanges);
        testSubscriber.assertValueCount(2);
    }

    @Test
    public void repeatsOperationWithRawQueryByChangeOfTable() {
        putUserBlocking();
        TestSubscriber<Cursor> testSubscriber = new TestSubscriber<Cursor>();
        storIOSQLite.get().cursor().withQuery(rawQuery).prepare().asRxFlowable(BackpressureStrategy.MISSING).subscribe(testSubscriber);
        testSubscriber.assertValueCount(1);
        storIOSQLite.lowLevel().notifyAboutChanges(tableChanges);
        testSubscriber.assertValueCount(2);
    }

    @Test
    public void repeatsOperationWithQueryByChangeOfTag() {
        putUserBlocking();
        TestSubscriber<Cursor> testSubscriber = new TestSubscriber<Cursor>();
        storIOSQLite.get().cursor().withQuery(query).prepare().asRxFlowable(BackpressureStrategy.MISSING).subscribe(testSubscriber);
        testSubscriber.assertValueCount(1);
        storIOSQLite.lowLevel().notifyAboutChanges(tagChanges);
        testSubscriber.assertValueCount(2);
    }

    @Test
    public void repeatsOperationWithRawQueryByChangeOfTag() {
        putUserBlocking();
        TestSubscriber<Cursor> testSubscriber = new TestSubscriber<Cursor>();
        storIOSQLite.get().cursor().withQuery(rawQuery).prepare().asRxFlowable(BackpressureStrategy.MISSING).subscribe(testSubscriber);
        testSubscriber.assertValueCount(1);
        storIOSQLite.lowLevel().notifyAboutChanges(tagChanges);
        testSubscriber.assertValueCount(2);
    }
}

