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


import RoomSQLiteQuery.sQueryPool;
import androidx.sqlite.db.SupportSQLiteProgram;
import java.util.ArrayList;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import static RoomSQLiteQuery.POOL_LIMIT;


@RunWith(JUnit4.class)
public class RoomSQLiteQueryTest {
    @Test
    public void acquireBasic() {
        RoomSQLiteQuery query = RoomSQLiteQuery.acquire("abc", 3);
        MatcherAssert.assertThat(query.getSql(), CoreMatchers.is("abc"));
        MatcherAssert.assertThat(query.mArgCount, CoreMatchers.is(3));
        MatcherAssert.assertThat(query.mBlobBindings.length, CoreMatchers.is(4));
        MatcherAssert.assertThat(query.mLongBindings.length, CoreMatchers.is(4));
        MatcherAssert.assertThat(query.mStringBindings.length, CoreMatchers.is(4));
        MatcherAssert.assertThat(query.mDoubleBindings.length, CoreMatchers.is(4));
    }

    @Test
    public void acquireSameSizeAgain() {
        RoomSQLiteQuery query = RoomSQLiteQuery.acquire("abc", 3);
        query.release();
        MatcherAssert.assertThat(RoomSQLiteQuery.acquire("blah", 3), CoreMatchers.sameInstance(query));
    }

    @Test
    public void acquireSameSizeWithoutRelease() {
        RoomSQLiteQuery query = RoomSQLiteQuery.acquire("abc", 3);
        MatcherAssert.assertThat(RoomSQLiteQuery.acquire("fda", 3), CoreMatchers.not(CoreMatchers.sameInstance(query)));
    }

    @Test
    public void bindings() {
        RoomSQLiteQuery query = RoomSQLiteQuery.acquire("abc", 6);
        byte[] myBlob = new byte[3];
        long myLong = 3L;
        double myDouble = 7.0;
        String myString = "ss";
        query.bindBlob(1, myBlob);
        query.bindLong(2, myLong);
        query.bindNull(3);
        query.bindDouble(4, myDouble);
        query.bindString(5, myString);
        query.bindNull(6);
        SupportSQLiteProgram program = Mockito.mock(SupportSQLiteProgram.class);
        query.bindTo(program);
        Mockito.verify(program).bindBlob(1, myBlob);
        Mockito.verify(program).bindLong(2, myLong);
        Mockito.verify(program).bindNull(3);
        Mockito.verify(program).bindDouble(4, myDouble);
        Mockito.verify(program).bindString(5, myString);
        Mockito.verify(program).bindNull(6);
    }

    @Test
    public void dontKeepSameSizeTwice() {
        RoomSQLiteQuery query1 = RoomSQLiteQuery.acquire("abc", 3);
        RoomSQLiteQuery query2 = RoomSQLiteQuery.acquire("zx", 3);
        RoomSQLiteQuery query3 = RoomSQLiteQuery.acquire("qw", 0);
        query1.release();
        query2.release();
        MatcherAssert.assertThat(sQueryPool.size(), CoreMatchers.is(1));
        query3.release();
        MatcherAssert.assertThat(sQueryPool.size(), CoreMatchers.is(2));
    }

    @Test
    public void returnExistingForSmallerSize() {
        RoomSQLiteQuery query = RoomSQLiteQuery.acquire("abc", 3);
        query.release();
        MatcherAssert.assertThat(RoomSQLiteQuery.acquire("dsa", 2), CoreMatchers.sameInstance(query));
    }

    @Test
    public void returnNewForBigger() {
        RoomSQLiteQuery query = RoomSQLiteQuery.acquire("abc", 3);
        query.release();
        MatcherAssert.assertThat(RoomSQLiteQuery.acquire("dsa", 4), CoreMatchers.not(CoreMatchers.sameInstance(query)));
    }

    @Test
    public void pruneCache() {
        for (int i = 0; i < (POOL_LIMIT); i++) {
            RoomSQLiteQuery.acquire("dsdsa", i).release();
        }
        pruneCacheTest();
    }

    @Test
    public void pruneCacheReverseInsertion() {
        List<RoomSQLiteQuery> queries = new ArrayList<>();
        for (int i = (POOL_LIMIT) - 1; i >= 0; i--) {
            queries.add(RoomSQLiteQuery.acquire("dsdsa", i));
        }
        for (RoomSQLiteQuery query : queries) {
            query.release();
        }
        pruneCacheTest();
    }
}

