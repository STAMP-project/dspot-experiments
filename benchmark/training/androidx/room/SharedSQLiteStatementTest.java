/**
 * Copyright (C) 2016 The Android Open Source Project
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


import androidx.sqlite.db.SupportSQLiteStatement;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;


@RunWith(JUnit4.class)
public class SharedSQLiteStatementTest {
    private SharedSQLiteStatement mSharedStmt;

    RoomDatabase mDb;

    @Test
    public void checkMainThread() {
        mSharedStmt.acquire();
        Mockito.verify(mDb).assertNotMainThread();
    }

    @Test
    public void basic() {
        MatcherAssert.assertThat(mSharedStmt.acquire(), CoreMatchers.notNullValue());
    }

    @Test
    public void getTwiceWithoutReleasing() {
        SupportSQLiteStatement stmt1 = mSharedStmt.acquire();
        SupportSQLiteStatement stmt2 = mSharedStmt.acquire();
        MatcherAssert.assertThat(stmt1, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(stmt2, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(stmt1, CoreMatchers.is(CoreMatchers.not(stmt2)));
    }

    @Test
    public void getTwiceWithReleasing() {
        SupportSQLiteStatement stmt1 = mSharedStmt.acquire();
        mSharedStmt.release(stmt1);
        SupportSQLiteStatement stmt2 = mSharedStmt.acquire();
        MatcherAssert.assertThat(stmt1, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(stmt1, CoreMatchers.is(stmt2));
    }

    @Test
    public void getFromAnotherThreadWhileHolding() throws InterruptedException, ExecutionException {
        SupportSQLiteStatement stmt1 = mSharedStmt.acquire();
        FutureTask<SupportSQLiteStatement> task = new FutureTask(new Callable<SupportSQLiteStatement>() {
            @Override
            public SupportSQLiteStatement call() throws Exception {
                return mSharedStmt.acquire();
            }
        });
        new Thread(task).run();
        SupportSQLiteStatement stmt2 = task.get();
        MatcherAssert.assertThat(stmt1, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(stmt2, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(stmt1, CoreMatchers.is(CoreMatchers.not(stmt2)));
    }

    @Test
    public void getFromAnotherThreadAfterReleasing() throws InterruptedException, ExecutionException {
        SupportSQLiteStatement stmt1 = mSharedStmt.acquire();
        mSharedStmt.release(stmt1);
        FutureTask<SupportSQLiteStatement> task = new FutureTask(new Callable<SupportSQLiteStatement>() {
            @Override
            public SupportSQLiteStatement call() throws Exception {
                return mSharedStmt.acquire();
            }
        });
        new Thread(task).run();
        SupportSQLiteStatement stmt2 = task.get();
        MatcherAssert.assertThat(stmt1, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(stmt1, CoreMatchers.is(stmt2));
    }
}

