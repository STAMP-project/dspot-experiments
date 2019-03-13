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


import InvalidationTracker.ObservedTableTracker;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class ObservedTableTrackerTest {
    private static final int TABLE_COUNT = 5;

    private ObservedTableTracker mTracker;

    @Test
    public void basicAdd() {
        mTracker.onAdded(2, 3);
        MatcherAssert.assertThat(mTracker.getTablesToSync(), CoreMatchers.is(ObservedTableTrackerTest.createResponse(2, ObservedTableTracker.ADD, 3, ObservedTableTracker.ADD)));
    }

    @Test
    public void basicRemove() {
        initState(2, 3);
        mTracker.onRemoved(3);
        MatcherAssert.assertThat(mTracker.getTablesToSync(), CoreMatchers.is(ObservedTableTrackerTest.createResponse(3, ObservedTableTracker.REMOVE)));
    }

    @Test
    public void noChange() {
        initState(1, 3);
        mTracker.onAdded(3);
        MatcherAssert.assertThat(mTracker.getTablesToSync(), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void returnNullUntilSync() {
        initState(1, 3);
        mTracker.onAdded(4);
        MatcherAssert.assertThat(mTracker.getTablesToSync(), CoreMatchers.is(ObservedTableTrackerTest.createResponse(4, ObservedTableTracker.ADD)));
        mTracker.onAdded(0);
        MatcherAssert.assertThat(mTracker.getTablesToSync(), CoreMatchers.is(CoreMatchers.nullValue()));
        mTracker.onSyncCompleted();
        MatcherAssert.assertThat(mTracker.getTablesToSync(), CoreMatchers.is(ObservedTableTrackerTest.createResponse(0, ObservedTableTracker.ADD)));
    }

    @Test
    public void multipleAdditionsDeletions() {
        initState(2, 4);
        mTracker.onAdded(2);
        MatcherAssert.assertThat(mTracker.getTablesToSync(), CoreMatchers.is(CoreMatchers.nullValue()));
        mTracker.onAdded(2, 4);
        MatcherAssert.assertThat(mTracker.getTablesToSync(), CoreMatchers.is(CoreMatchers.nullValue()));
        mTracker.onRemoved(2);
        MatcherAssert.assertThat(mTracker.getTablesToSync(), CoreMatchers.is(CoreMatchers.nullValue()));
        mTracker.onRemoved(2, 4);
        MatcherAssert.assertThat(mTracker.getTablesToSync(), CoreMatchers.is(CoreMatchers.nullValue()));
        mTracker.onAdded(1, 3);
        mTracker.onRemoved(2, 4);
        MatcherAssert.assertThat(mTracker.getTablesToSync(), CoreMatchers.is(ObservedTableTrackerTest.createResponse(1, ObservedTableTracker.ADD, 2, ObservedTableTracker.REMOVE, 3, ObservedTableTracker.ADD, 4, ObservedTableTracker.REMOVE)));
    }
}

