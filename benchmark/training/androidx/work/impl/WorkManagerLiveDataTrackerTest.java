/**
 * Copyright 2018 The Android Open Source Project
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
package androidx.work.impl;


import androidx.lifecycle.LiveData;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class WorkManagerLiveDataTrackerTest {
    private WorkManagerLiveDataTracker mContainer = new WorkManagerLiveDataTracker();

    @Test
    public void add() {
        LiveData liveData = createLiveData();
        Assert.assertThat(mContainer.mLiveDataSet, CoreMatchers.is(WorkManagerLiveDataTrackerTest.setOf()));
        mContainer.onActive(liveData);
        Assert.assertThat(mContainer.mLiveDataSet, CoreMatchers.is(WorkManagerLiveDataTrackerTest.setOf(liveData)));
    }

    @Test
    public void add_twice() {
        LiveData liveData = createLiveData();
        mContainer.onActive(liveData);
        mContainer.onActive(liveData);
        Assert.assertThat(mContainer.mLiveDataSet, CoreMatchers.is(WorkManagerLiveDataTrackerTest.setOf(liveData)));
    }

    @Test
    public void remove() {
        LiveData liveData = createLiveData();
        mContainer.onActive(liveData);
        mContainer.onInactive(liveData);
        Assert.assertThat(mContainer.mLiveDataSet, CoreMatchers.is(WorkManagerLiveDataTrackerTest.setOf()));
    }

    @Test
    public void remove_twice() {
        LiveData liveData = createLiveData();
        mContainer.onActive(liveData);
        mContainer.onInactive(liveData);
        mContainer.onInactive(liveData);
        Assert.assertThat(mContainer.mLiveDataSet, CoreMatchers.is(WorkManagerLiveDataTrackerTest.setOf()));
    }

    @Test
    public void addRemoveMultiple() {
        LiveData ld1 = createLiveData();
        LiveData ld2 = createLiveData();
        Assert.assertThat(mContainer.mLiveDataSet, CoreMatchers.is(WorkManagerLiveDataTrackerTest.setOf()));
        mContainer.onActive(ld1);
        mContainer.onActive(ld2);
        Assert.assertThat(mContainer.mLiveDataSet, CoreMatchers.is(WorkManagerLiveDataTrackerTest.setOf(ld1, ld2)));
        mContainer.onInactive(ld1);
        Assert.assertThat(mContainer.mLiveDataSet, CoreMatchers.is(WorkManagerLiveDataTrackerTest.setOf(ld2)));
        mContainer.onInactive(ld1);// intentional

        Assert.assertThat(mContainer.mLiveDataSet, CoreMatchers.is(WorkManagerLiveDataTrackerTest.setOf(ld2)));
        mContainer.onActive(ld1);
        Assert.assertThat(mContainer.mLiveDataSet, CoreMatchers.is(WorkManagerLiveDataTrackerTest.setOf(ld1, ld2)));
        mContainer.onActive(ld1);// intentional

        Assert.assertThat(mContainer.mLiveDataSet, CoreMatchers.is(WorkManagerLiveDataTrackerTest.setOf(ld1, ld2)));
        mContainer.onInactive(ld2);
        Assert.assertThat(mContainer.mLiveDataSet, CoreMatchers.is(WorkManagerLiveDataTrackerTest.setOf(ld1)));
        mContainer.onInactive(ld1);
        Assert.assertThat(mContainer.mLiveDataSet, CoreMatchers.is(WorkManagerLiveDataTrackerTest.setOf()));
        mContainer.onActive(ld1);
        Assert.assertThat(mContainer.mLiveDataSet, CoreMatchers.is(WorkManagerLiveDataTrackerTest.setOf(ld1)));
        mContainer.onActive(ld2);
        Assert.assertThat(mContainer.mLiveDataSet, CoreMatchers.is(WorkManagerLiveDataTrackerTest.setOf(ld1, ld2)));
    }
}

