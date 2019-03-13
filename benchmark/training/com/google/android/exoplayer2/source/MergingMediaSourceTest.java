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
package com.google.android.exoplayer2.source;


import IllegalMergeException.REASON_PERIOD_COUNT_MISMATCH;
import RobolectricUtil.CustomLooper;
import RobolectricUtil.CustomMessageQueue;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.source.MergingMediaSource.IllegalMergeException;
import com.google.android.exoplayer2.testutil.FakeMediaSource;
import com.google.android.exoplayer2.testutil.FakeTimeline;
import com.google.android.exoplayer2.testutil.FakeTimeline.TimelineWindowDefinition;
import com.google.android.exoplayer2.testutil.MediaSourceTestRunner;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


/**
 * Unit tests for {@link MergingMediaSource}.
 */
@RunWith(RobolectricTestRunner.class)
@Config(shadows = { CustomLooper.class, CustomMessageQueue.class })
public class MergingMediaSourceTest {
    @Test
    public void testMergingDynamicTimelines() throws IOException {
        FakeTimeline firstTimeline = new FakeTimeline(new TimelineWindowDefinition(true, true, C.TIME_UNSET));
        FakeTimeline secondTimeline = new FakeTimeline(new TimelineWindowDefinition(true, true, C.TIME_UNSET));
        MergingMediaSourceTest.testMergingMediaSourcePrepare(firstTimeline, secondTimeline);
    }

    @Test
    public void testMergingStaticTimelines() throws IOException {
        FakeTimeline firstTimeline = new FakeTimeline(new TimelineWindowDefinition(true, false, 20));
        FakeTimeline secondTimeline = new FakeTimeline(new TimelineWindowDefinition(true, false, 10));
        MergingMediaSourceTest.testMergingMediaSourcePrepare(firstTimeline, secondTimeline);
    }

    @Test
    public void testMergingTimelinesWithDifferentPeriodCounts() throws IOException {
        FakeTimeline firstTimeline = new FakeTimeline(new TimelineWindowDefinition(1, null));
        FakeTimeline secondTimeline = new FakeTimeline(new TimelineWindowDefinition(2, null));
        try {
            MergingMediaSourceTest.testMergingMediaSourcePrepare(firstTimeline, secondTimeline);
            Assert.fail("Expected merging to fail.");
        } catch (IllegalMergeException e) {
            assertThat(e.reason).isEqualTo(REASON_PERIOD_COUNT_MISMATCH);
        }
    }

    @Test
    public void testMergingMediaSourcePeriodCreation() throws Exception {
        FakeMediaSource[] mediaSources = new FakeMediaSource[2];
        for (int i = 0; i < (mediaSources.length); i++) {
            mediaSources[i] = /* manifest= */
            new FakeMediaSource(/* windowCount= */
            new FakeTimeline(2), null);
        }
        MergingMediaSource mediaSource = new MergingMediaSource(mediaSources);
        MediaSourceTestRunner testRunner = new MediaSourceTestRunner(mediaSource, null);
        try {
            testRunner.prepareSource();
            testRunner.assertPrepareAndReleaseAllPeriods();
            for (FakeMediaSource element : mediaSources) {
                assertThat(element.getCreatedMediaPeriods()).isNotEmpty();
            }
            testRunner.releaseSource();
        } finally {
            testRunner.release();
        }
    }
}

