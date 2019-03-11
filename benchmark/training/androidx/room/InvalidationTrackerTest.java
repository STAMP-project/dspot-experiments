/**
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package androidx.room;


import InvalidationTracker.Observer;
import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.arch.core.executor.JunitTaskExecutorRule;
import androidx.sqlite.db.SimpleSQLiteQuery;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsCollectionContaining;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


@RunWith(JUnit4.class)
public class InvalidationTrackerTest {
    private InvalidationTracker mTracker;

    @Mock
    private RoomDatabase mRoomDatabase;

    @Mock
    private SupportSQLiteDatabase mSqliteDb;

    @Mock
    private SupportSQLiteOpenHelper mOpenHelper;

    @Rule
    public JunitTaskExecutorRule mTaskExecutorRule = new JunitTaskExecutorRule(1, true);

    @Test
    public void tableIds() {
        MatcherAssert.assertThat(mTracker.mTableIdLookup.get("a"), CoreMatchers.is(0));
        MatcherAssert.assertThat(mTracker.mTableIdLookup.get("b"), CoreMatchers.is(1));
        MatcherAssert.assertThat(mTracker.mTableIdLookup.get("i"), CoreMatchers.is(2));
        MatcherAssert.assertThat(mTracker.mTableIdLookup.get("c"), CoreMatchers.is(3));
    }

    @Test
    public void testWeak() throws InterruptedException {
        final AtomicInteger data = new AtomicInteger(0);
        InvalidationTracker.Observer observer = new InvalidationTracker.Observer("a") {
            @Override
            public void onInvalidated(@NonNull
            Set<String> tables) {
                data.incrementAndGet();
            }
        };
        mTracker.addWeakObserver(observer);
        setInvalidatedTables(0);
        refreshSync();
        MatcherAssert.assertThat(data.get(), CoreMatchers.is(1));
        observer = null;
        InvalidationTrackerTest.forceGc();
        setInvalidatedTables(0);
        refreshSync();
        MatcherAssert.assertThat(data.get(), CoreMatchers.is(1));
    }

    @Test
    public void addRemoveObserver() throws Exception {
        InvalidationTracker.Observer observer = new InvalidationTrackerTest.LatchObserver(1, "a");
        mTracker.addObserver(observer);
        MatcherAssert.assertThat(mTracker.mObserverMap.size(), CoreMatchers.is(1));
        mTracker.removeObserver(new InvalidationTrackerTest.LatchObserver(1, "a"));
        MatcherAssert.assertThat(mTracker.mObserverMap.size(), CoreMatchers.is(1));
        mTracker.removeObserver(observer);
        MatcherAssert.assertThat(mTracker.mObserverMap.size(), CoreMatchers.is(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void badObserver() {
        InvalidationTracker.Observer observer = new InvalidationTrackerTest.LatchObserver(1, "x");
        mTracker.addObserver(observer);
    }

    @Test
    public void refreshCheckTasks() throws Exception {
        Mockito.when(mRoomDatabase.query(ArgumentMatchers.any(SimpleSQLiteQuery.class))).thenReturn(Mockito.mock(Cursor.class));
        mTracker.refreshVersionsAsync();
        mTracker.refreshVersionsAsync();
        Mockito.verify(mTaskExecutorRule.getTaskExecutor()).executeOnDiskIO(mTracker.mRefreshRunnable);
        drainTasks();
        Mockito.reset(mTaskExecutorRule.getTaskExecutor());
        mTracker.refreshVersionsAsync();
        Mockito.verify(mTaskExecutorRule.getTaskExecutor()).executeOnDiskIO(mTracker.mRefreshRunnable);
    }

    @Test
    public void observe1Table() throws Exception {
        InvalidationTrackerTest.LatchObserver observer = new InvalidationTrackerTest.LatchObserver(1, "a");
        mTracker.addObserver(observer);
        setInvalidatedTables(0);
        refreshSync();
        MatcherAssert.assertThat(observer.await(), CoreMatchers.is(true));
        MatcherAssert.assertThat(observer.getInvalidatedTables().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(observer.getInvalidatedTables(), IsCollectionContaining.hasItem("a"));
        setInvalidatedTables(1);
        observer.reset(1);
        refreshSync();
        MatcherAssert.assertThat(observer.await(), CoreMatchers.is(false));
        setInvalidatedTables(0);
        refreshSync();
        MatcherAssert.assertThat(observer.await(), CoreMatchers.is(true));
        MatcherAssert.assertThat(observer.getInvalidatedTables().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(observer.getInvalidatedTables(), IsCollectionContaining.hasItem("a"));
    }

    @Test
    public void observe2Tables() throws Exception {
        InvalidationTrackerTest.LatchObserver observer = new InvalidationTrackerTest.LatchObserver(1, "A", "B");
        mTracker.addObserver(observer);
        setInvalidatedTables(0, 1);
        refreshSync();
        MatcherAssert.assertThat(observer.await(), CoreMatchers.is(true));
        MatcherAssert.assertThat(observer.getInvalidatedTables().size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(observer.getInvalidatedTables(), IsCollectionContaining.hasItems("A", "B"));
        setInvalidatedTables(1, 2);
        observer.reset(1);
        refreshSync();
        MatcherAssert.assertThat(observer.await(), CoreMatchers.is(true));
        MatcherAssert.assertThat(observer.getInvalidatedTables().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(observer.getInvalidatedTables(), IsCollectionContaining.hasItem("B"));
        setInvalidatedTables(0, 3);
        observer.reset(1);
        refreshSync();
        MatcherAssert.assertThat(observer.await(), CoreMatchers.is(true));
        MatcherAssert.assertThat(observer.getInvalidatedTables().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(observer.getInvalidatedTables(), IsCollectionContaining.hasItem("A"));
        observer.reset(1);
        refreshSync();
        MatcherAssert.assertThat(observer.await(), CoreMatchers.is(false));
    }

    @Test
    public void locale() {
        InvalidationTrackerTest.LatchObserver observer = new InvalidationTrackerTest.LatchObserver(1, "I");
        mTracker.addObserver(observer);
    }

    @Test
    public void closedDb() {
        Mockito.doReturn(false).when(mRoomDatabase).isOpen();
        Mockito.doThrow(new IllegalStateException("foo")).when(mOpenHelper).getWritableDatabase();
        mTracker.addObserver(new InvalidationTrackerTest.LatchObserver(1, "a", "b"));
        mTracker.mRefreshRunnable.run();
    }

    @Test
    public void createTriggerOnShadowTable() {
        InvalidationTrackerTest.LatchObserver observer = new InvalidationTrackerTest.LatchObserver(1, "C");
        String[] triggers = new String[]{ "UPDATE", "DELETE", "INSERT" };
        ArgumentCaptor<String> sqlArgCaptor;
        List<String> sqlCaptorValues;
        mTracker.addObserver(observer);
        sqlArgCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(mSqliteDb, Mockito.times(4)).execSQL(sqlArgCaptor.capture());
        sqlCaptorValues = sqlArgCaptor.getAllValues();
        MatcherAssert.assertThat(sqlCaptorValues.get(0), CoreMatchers.is("INSERT OR IGNORE INTO room_table_modification_log VALUES(3, 0)"));
        for (int i = 0; i < (triggers.length); i++) {
            MatcherAssert.assertThat(sqlCaptorValues.get((i + 1)), CoreMatchers.is((((((("CREATE TEMP TRIGGER IF NOT EXISTS " + "`room_table_modification_trigger_d_") + (triggers[i])) + "` AFTER ") + (triggers[i])) + " ON `d` BEGIN UPDATE room_table_modification_log ") + "SET invalidated = 1 WHERE table_id = 3 AND invalidated = 0; END")));
        }
        Mockito.reset(mSqliteDb);
        mTracker.removeObserver(observer);
        sqlArgCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(mSqliteDb, Mockito.times(3)).execSQL(sqlArgCaptor.capture());
        sqlCaptorValues = sqlArgCaptor.getAllValues();
        for (int i = 0; i < (triggers.length); i++) {
            MatcherAssert.assertThat(sqlCaptorValues.get(i), CoreMatchers.is((("DROP TRIGGER IF EXISTS `room_table_modification_trigger_d_" + (triggers[i])) + "`")));
        }
    }

    @Test
    public void observeView() throws InterruptedException {
        InvalidationTrackerTest.LatchObserver observer = new InvalidationTrackerTest.LatchObserver(1, "E");
        mTracker.addObserver(observer);
        setInvalidatedTables(0, 1);
        refreshSync();
        MatcherAssert.assertThat(observer.await(), CoreMatchers.is(true));
        MatcherAssert.assertThat(observer.getInvalidatedTables().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(observer.getInvalidatedTables(), IsCollectionContaining.hasItem("a"));
        setInvalidatedTables(2, 3);
        observer.reset(1);
        refreshSync();
        MatcherAssert.assertThat(observer.await(), CoreMatchers.is(false));
        setInvalidatedTables(0, 1);
        refreshSync();
        MatcherAssert.assertThat(observer.await(), CoreMatchers.is(true));
        MatcherAssert.assertThat(observer.getInvalidatedTables().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(observer.getInvalidatedTables(), IsCollectionContaining.hasItem("a"));
    }

    @Test
    public void failFastCreateLiveData() {
        // assert that sending a bad createLiveData table name fails instantly
        try {
            mTracker.createLiveData(new String[]{ "invalid table name" }, new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    return null;
                }
            });
            Assert.fail("should've throw an exception for invalid table name");
        } catch (IllegalArgumentException expected) {
            // expected
        }
    }

    static class LatchObserver extends InvalidationTracker.Observer {
        private CountDownLatch mLatch;

        private Set<String> mInvalidatedTables;

        LatchObserver(int count, String... tableNames) {
            super(tableNames);
            mLatch = new CountDownLatch(count);
        }

        boolean await() throws InterruptedException {
            return mLatch.await(3, TimeUnit.SECONDS);
        }

        @Override
        public void onInvalidated(@NonNull
        Set<String> tables) {
            mInvalidatedTables = tables;
            mLatch.countDown();
        }

        void reset(@SuppressWarnings("SameParameterValue")
        int count) {
            mInvalidatedTables = null;
            mLatch = new CountDownLatch(count);
        }

        Set<String> getInvalidatedTables() {
            return mInvalidatedTables;
        }
    }
}

