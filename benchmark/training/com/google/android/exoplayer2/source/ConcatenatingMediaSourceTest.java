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


import C.INDEX_UNSET;
import MediaSourceTestRunner.TIMEOUT_MS;
import Player.REPEAT_MODE_ALL;
import Player.REPEAT_MODE_OFF;
import Player.REPEAT_MODE_ONE;
import RobolectricUtil.CustomLooper;
import RobolectricUtil.CustomMessageQueue;
import android.os.ConditionVariable;
import android.os.Handler;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.MediaSource.MediaPeriodId;
import com.google.android.exoplayer2.source.ShuffleOrder.DefaultShuffleOrder;
import com.google.android.exoplayer2.testutil.DummyMainThread;
import com.google.android.exoplayer2.testutil.FakeMediaSource;
import com.google.android.exoplayer2.testutil.FakeShuffleOrder;
import com.google.android.exoplayer2.testutil.FakeTimeline;
import com.google.android.exoplayer2.testutil.MediaSourceTestRunner;
import com.google.android.exoplayer2.testutil.TimelineAsserts;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


/**
 * Unit tests for {@link ConcatenatingMediaSource}.
 */
@RunWith(RobolectricTestRunner.class)
@Config(shadows = { CustomLooper.class, CustomMessageQueue.class })
public final class ConcatenatingMediaSourceTest {
    private ConcatenatingMediaSource mediaSource;

    private MediaSourceTestRunner testRunner;

    @Test
    public void testPlaylistChangesAfterPreparation() throws IOException, InterruptedException {
        Timeline timeline = testRunner.prepareSource();
        TimelineAsserts.assertEmpty(timeline);
        FakeMediaSource[] childSources = ConcatenatingMediaSourceTest.createMediaSources(7);
        // Add first source.
        mediaSource.addMediaSource(childSources[0]);
        timeline = testRunner.assertTimelineChangeBlocking();
        TimelineAsserts.assertPeriodCounts(timeline, 1);
        TimelineAsserts.assertWindowTags(timeline, 111);
        // Add at front of queue.
        mediaSource.addMediaSource(0, childSources[1]);
        timeline = testRunner.assertTimelineChangeBlocking();
        TimelineAsserts.assertPeriodCounts(timeline, 2, 1);
        TimelineAsserts.assertWindowTags(timeline, 222, 111);
        // Add at back of queue.
        mediaSource.addMediaSource(childSources[2]);
        timeline = testRunner.assertTimelineChangeBlocking();
        TimelineAsserts.assertPeriodCounts(timeline, 2, 1, 3);
        TimelineAsserts.assertWindowTags(timeline, 222, 111, 333);
        // Add in the middle.
        mediaSource.addMediaSource(1, childSources[3]);
        timeline = testRunner.assertTimelineChangeBlocking();
        TimelineAsserts.assertPeriodCounts(timeline, 2, 4, 1, 3);
        TimelineAsserts.assertWindowTags(timeline, 222, 444, 111, 333);
        // Add bulk.
        mediaSource.addMediaSources(3, Arrays.asList(childSources[4], childSources[5], childSources[6]));
        timeline = testRunner.assertTimelineChangeBlocking();
        TimelineAsserts.assertPeriodCounts(timeline, 2, 4, 1, 5, 6, 7, 3);
        TimelineAsserts.assertWindowTags(timeline, 222, 444, 111, 555, 666, 777, 333);
        // Move sources.
        mediaSource.moveMediaSource(2, 3);
        timeline = testRunner.assertTimelineChangeBlocking();
        TimelineAsserts.assertPeriodCounts(timeline, 2, 4, 5, 1, 6, 7, 3);
        TimelineAsserts.assertWindowTags(timeline, 222, 444, 555, 111, 666, 777, 333);
        mediaSource.moveMediaSource(3, 2);
        timeline = testRunner.assertTimelineChangeBlocking();
        TimelineAsserts.assertPeriodCounts(timeline, 2, 4, 1, 5, 6, 7, 3);
        TimelineAsserts.assertWindowTags(timeline, 222, 444, 111, 555, 666, 777, 333);
        mediaSource.moveMediaSource(0, 6);
        timeline = testRunner.assertTimelineChangeBlocking();
        TimelineAsserts.assertPeriodCounts(timeline, 4, 1, 5, 6, 7, 3, 2);
        TimelineAsserts.assertWindowTags(timeline, 444, 111, 555, 666, 777, 333, 222);
        mediaSource.moveMediaSource(6, 0);
        timeline = testRunner.assertTimelineChangeBlocking();
        TimelineAsserts.assertPeriodCounts(timeline, 2, 4, 1, 5, 6, 7, 3);
        TimelineAsserts.assertWindowTags(timeline, 222, 444, 111, 555, 666, 777, 333);
        // Remove in the middle.
        mediaSource.removeMediaSource(3);
        testRunner.assertTimelineChangeBlocking();
        mediaSource.removeMediaSource(3);
        testRunner.assertTimelineChangeBlocking();
        mediaSource.removeMediaSource(3);
        testRunner.assertTimelineChangeBlocking();
        mediaSource.removeMediaSource(1);
        timeline = testRunner.assertTimelineChangeBlocking();
        TimelineAsserts.assertPeriodCounts(timeline, 2, 1, 3);
        TimelineAsserts.assertWindowTags(timeline, 222, 111, 333);
        for (int i = 3; i <= 6; i++) {
            childSources[i].assertReleased();
        }
        // Assert the correct child source preparation load events have been returned (with the
        // respective window index at the time of preparation).
        testRunner.assertCompletedManifestLoads(0, 0, 2, 1, 3, 4, 5);
        // Assert correct next and previous indices behavior after some insertions and removals.
        TimelineAsserts.assertNextWindowIndices(timeline, REPEAT_MODE_OFF, false, 1, 2, INDEX_UNSET);
        TimelineAsserts.assertNextWindowIndices(timeline, REPEAT_MODE_ONE, false, 0, 1, 2);
        TimelineAsserts.assertNextWindowIndices(timeline, REPEAT_MODE_ALL, false, 1, 2, 0);
        TimelineAsserts.assertPreviousWindowIndices(timeline, REPEAT_MODE_OFF, false, INDEX_UNSET, 0, 1);
        TimelineAsserts.assertPreviousWindowIndices(timeline, REPEAT_MODE_ONE, false, 0, 1, 2);
        TimelineAsserts.assertPreviousWindowIndices(timeline, REPEAT_MODE_ALL, false, 2, 0, 1);
        assertThat(timeline.getFirstWindowIndex(false)).isEqualTo(0);
        assertThat(timeline.getLastWindowIndex(false)).isEqualTo(((timeline.getWindowCount()) - 1));
        TimelineAsserts.assertNextWindowIndices(timeline, REPEAT_MODE_OFF, true, INDEX_UNSET, 0, 1);
        TimelineAsserts.assertNextWindowIndices(timeline, REPEAT_MODE_ONE, true, 0, 1, 2);
        TimelineAsserts.assertNextWindowIndices(timeline, REPEAT_MODE_ALL, true, 2, 0, 1);
        TimelineAsserts.assertPreviousWindowIndices(timeline, REPEAT_MODE_OFF, true, 1, 2, INDEX_UNSET);
        TimelineAsserts.assertPreviousWindowIndices(timeline, REPEAT_MODE_ONE, true, 0, 1, 2);
        TimelineAsserts.assertPreviousWindowIndices(timeline, REPEAT_MODE_ALL, true, 1, 2, 0);
        assertThat(timeline.getFirstWindowIndex(true)).isEqualTo(((timeline.getWindowCount()) - 1));
        assertThat(timeline.getLastWindowIndex(true)).isEqualTo(0);
        // Assert all periods can be prepared and the respective load events are returned.
        testRunner.assertPrepareAndReleaseAllPeriods();
        assertCompletedAllMediaPeriodLoads(timeline);
        // Remove at front of queue.
        mediaSource.removeMediaSource(0);
        timeline = testRunner.assertTimelineChangeBlocking();
        TimelineAsserts.assertPeriodCounts(timeline, 1, 3);
        TimelineAsserts.assertWindowTags(timeline, 111, 333);
        childSources[1].assertReleased();
        // Remove at back of queue.
        mediaSource.removeMediaSource(1);
        timeline = testRunner.assertTimelineChangeBlocking();
        TimelineAsserts.assertPeriodCounts(timeline, 1);
        TimelineAsserts.assertWindowTags(timeline, 111);
        childSources[2].assertReleased();
        // Remove last source.
        mediaSource.removeMediaSource(0);
        timeline = testRunner.assertTimelineChangeBlocking();
        TimelineAsserts.assertEmpty(timeline);
        childSources[3].assertReleased();
    }

    @Test
    public void testPlaylistChangesBeforePreparation() throws IOException, InterruptedException {
        FakeMediaSource[] childSources = ConcatenatingMediaSourceTest.createMediaSources(4);
        mediaSource.addMediaSource(childSources[0]);
        mediaSource.addMediaSource(childSources[1]);
        mediaSource.addMediaSource(0, childSources[2]);
        mediaSource.moveMediaSource(0, 2);
        mediaSource.removeMediaSource(0);
        mediaSource.moveMediaSource(1, 0);
        mediaSource.addMediaSource(1, childSources[3]);
        testRunner.assertNoTimelineChange();
        Timeline timeline = testRunner.prepareSource();
        TimelineAsserts.assertPeriodCounts(timeline, 3, 4, 2);
        TimelineAsserts.assertWindowTags(timeline, 333, 444, 222);
        TimelineAsserts.assertNextWindowIndices(timeline, REPEAT_MODE_OFF, false, 1, 2, INDEX_UNSET);
        TimelineAsserts.assertPreviousWindowIndices(timeline, REPEAT_MODE_OFF, false, INDEX_UNSET, 0, 1);
        TimelineAsserts.assertNextWindowIndices(timeline, REPEAT_MODE_OFF, true, INDEX_UNSET, 0, 1);
        TimelineAsserts.assertPreviousWindowIndices(timeline, REPEAT_MODE_OFF, true, 1, 2, INDEX_UNSET);
        testRunner.assertPrepareAndReleaseAllPeriods();
        testRunner.assertCompletedManifestLoads(0, 1, 2);
        assertCompletedAllMediaPeriodLoads(timeline);
        testRunner.releaseSource();
        for (int i = 1; i < 4; i++) {
            childSources[i].assertReleased();
        }
    }

    @Test
    public void testPlaylistWithLazyMediaSource() throws IOException, InterruptedException {
        // Create some normal (immediately preparing) sources and some lazy sources whose timeline
        // updates need to be triggered.
        FakeMediaSource[] fastSources = ConcatenatingMediaSourceTest.createMediaSources(2);
        final FakeMediaSource[] lazySources = new FakeMediaSource[4];
        for (int i = 0; i < 4; i++) {
            lazySources[i] = new FakeMediaSource(null, null);
        }
        // Add lazy sources and normal sources before preparation. Also remove one lazy source again
        // before preparation to check it doesn't throw or change the result.
        mediaSource.addMediaSource(lazySources[0]);
        mediaSource.addMediaSource(0, fastSources[0]);
        mediaSource.removeMediaSource(1);
        mediaSource.addMediaSource(1, lazySources[1]);
        testRunner.assertNoTimelineChange();
        // Prepare and assert that the timeline contains all information for normal sources while having
        // placeholder information for lazy sources.
        Timeline timeline = testRunner.prepareSource();
        TimelineAsserts.assertPeriodCounts(timeline, 1, 1);
        TimelineAsserts.assertWindowTags(timeline, 111, null);
        TimelineAsserts.assertWindowIsDynamic(timeline, false, true);
        // Trigger source info refresh for lazy source and check that the timeline now contains all
        // information for all windows.
        testRunner.runOnPlaybackThread(() -> lazySources[1].setNewSourceInfo(createFakeTimeline(8), null));
        timeline = testRunner.assertTimelineChangeBlocking();
        TimelineAsserts.assertPeriodCounts(timeline, 1, 9);
        TimelineAsserts.assertWindowTags(timeline, 111, 999);
        TimelineAsserts.assertWindowIsDynamic(timeline, false, false);
        testRunner.assertPrepareAndReleaseAllPeriods();
        testRunner.assertCompletedManifestLoads(0, 1);
        assertCompletedAllMediaPeriodLoads(timeline);
        // Add further lazy and normal sources after preparation. Also remove one lazy source again to
        // check it doesn't throw or change the result.
        mediaSource.addMediaSource(1, lazySources[2]);
        testRunner.assertTimelineChangeBlocking();
        mediaSource.addMediaSource(2, fastSources[1]);
        testRunner.assertTimelineChangeBlocking();
        mediaSource.addMediaSource(0, lazySources[3]);
        testRunner.assertTimelineChangeBlocking();
        mediaSource.removeMediaSource(2);
        timeline = testRunner.assertTimelineChangeBlocking();
        TimelineAsserts.assertPeriodCounts(timeline, 1, 1, 2, 9);
        TimelineAsserts.assertWindowTags(timeline, null, 111, 222, 999);
        TimelineAsserts.assertWindowIsDynamic(timeline, true, false, false, false);
        // Create a period from an unprepared lazy media source and assert Callback.onPrepared is not
        // called yet.
        MediaPeriod lazyPeriod = testRunner.createPeriod(/* windowSequenceNumber= */
        new MediaPeriodId(/* periodIndex= */
        timeline.getUidOfPeriod(0), 0));
        CountDownLatch preparedCondition = testRunner.preparePeriod(lazyPeriod, 0);
        assertThat(preparedCondition.getCount()).isEqualTo(1);
        // Assert that a second period can also be created and released without problems.
        MediaPeriod secondLazyPeriod = testRunner.createPeriod(/* windowSequenceNumber= */
        new MediaPeriodId(/* periodIndex= */
        timeline.getUidOfPeriod(0), 0));
        testRunner.releasePeriod(secondLazyPeriod);
        // Trigger source info refresh for lazy media source. Assert that now all information is
        // available again and the previously created period now also finished preparing.
        testRunner.runOnPlaybackThread(() -> lazySources[3].setNewSourceInfo(createFakeTimeline(7), null));
        timeline = testRunner.assertTimelineChangeBlocking();
        TimelineAsserts.assertPeriodCounts(timeline, 8, 1, 2, 9);
        TimelineAsserts.assertWindowTags(timeline, 888, 111, 222, 999);
        TimelineAsserts.assertWindowIsDynamic(timeline, false, false, false, false);
        assertThat(preparedCondition.getCount()).isEqualTo(0);
        // Release the period and source.
        testRunner.releasePeriod(lazyPeriod);
        testRunner.releaseSource();
        // Assert all sources were fully released.
        for (FakeMediaSource fastSource : fastSources) {
            fastSource.assertReleased();
        }
        for (FakeMediaSource lazySource : lazySources) {
            lazySource.assertReleased();
        }
    }

    @Test
    public void testEmptyTimelineMediaSource() throws IOException, InterruptedException {
        Timeline timeline = testRunner.prepareSource();
        TimelineAsserts.assertEmpty(timeline);
        mediaSource.addMediaSource(new FakeMediaSource(Timeline.EMPTY, null));
        timeline = testRunner.assertTimelineChangeBlocking();
        TimelineAsserts.assertEmpty(timeline);
        mediaSource.addMediaSources(Arrays.asList(new MediaSource[]{ new FakeMediaSource(Timeline.EMPTY, null), new FakeMediaSource(Timeline.EMPTY, null), new FakeMediaSource(Timeline.EMPTY, null), new FakeMediaSource(Timeline.EMPTY, null), new FakeMediaSource(Timeline.EMPTY, null), new FakeMediaSource(Timeline.EMPTY, null) }));
        timeline = testRunner.assertTimelineChangeBlocking();
        TimelineAsserts.assertEmpty(timeline);
        /* empty */
        testRunner.assertCompletedManifestLoads();
        // Insert non-empty media source to leave empty sources at the start, the end, and the middle
        // (with single and multiple empty sources in a row).
        MediaSource[] mediaSources = ConcatenatingMediaSourceTest.createMediaSources(3);
        mediaSource.addMediaSource(1, mediaSources[0]);
        testRunner.assertTimelineChangeBlocking();
        mediaSource.addMediaSource(4, mediaSources[1]);
        testRunner.assertTimelineChangeBlocking();
        mediaSource.addMediaSource(6, mediaSources[2]);
        timeline = testRunner.assertTimelineChangeBlocking();
        TimelineAsserts.assertWindowTags(timeline, 111, 222, 333);
        TimelineAsserts.assertPeriodCounts(timeline, 1, 2, 3);
        TimelineAsserts.assertPreviousWindowIndices(timeline, REPEAT_MODE_OFF, false, INDEX_UNSET, 0, 1);
        TimelineAsserts.assertPreviousWindowIndices(timeline, REPEAT_MODE_ONE, false, 0, 1, 2);
        TimelineAsserts.assertPreviousWindowIndices(timeline, REPEAT_MODE_ALL, false, 2, 0, 1);
        TimelineAsserts.assertNextWindowIndices(timeline, REPEAT_MODE_OFF, false, 1, 2, INDEX_UNSET);
        TimelineAsserts.assertNextWindowIndices(timeline, REPEAT_MODE_ONE, false, 0, 1, 2);
        TimelineAsserts.assertNextWindowIndices(timeline, REPEAT_MODE_ALL, false, 1, 2, 0);
        TimelineAsserts.assertPreviousWindowIndices(timeline, REPEAT_MODE_OFF, true, 1, 2, INDEX_UNSET);
        TimelineAsserts.assertPreviousWindowIndices(timeline, REPEAT_MODE_ONE, true, 0, 1, 2);
        TimelineAsserts.assertPreviousWindowIndices(timeline, REPEAT_MODE_ALL, true, 1, 2, 0);
        TimelineAsserts.assertNextWindowIndices(timeline, REPEAT_MODE_OFF, true, INDEX_UNSET, 0, 1);
        TimelineAsserts.assertNextWindowIndices(timeline, REPEAT_MODE_ONE, true, 0, 1, 2);
        TimelineAsserts.assertNextWindowIndices(timeline, REPEAT_MODE_ALL, true, 2, 0, 1);
        assertThat(timeline.getFirstWindowIndex(false)).isEqualTo(0);
        assertThat(timeline.getLastWindowIndex(false)).isEqualTo(2);
        assertThat(timeline.getFirstWindowIndex(true)).isEqualTo(2);
        assertThat(timeline.getLastWindowIndex(true)).isEqualTo(0);
        testRunner.assertPrepareAndReleaseAllPeriods();
        testRunner.assertCompletedManifestLoads(0, 1, 2);
        assertCompletedAllMediaPeriodLoads(timeline);
    }

    @Test
    public void testDynamicChangeOfEmptyTimelines() throws IOException {
        FakeMediaSource[] childSources = new FakeMediaSource[]{ /* manifest= */
        new FakeMediaSource(Timeline.EMPTY, null), /* manifest= */
        new FakeMediaSource(Timeline.EMPTY, null), /* manifest= */
        new FakeMediaSource(Timeline.EMPTY, null) };
        Timeline nonEmptyTimeline = /* windowCount = */
        new FakeTimeline(1);
        mediaSource.addMediaSources(Arrays.asList(childSources));
        Timeline timeline = testRunner.prepareSource();
        TimelineAsserts.assertEmpty(timeline);
        /* newManifest== */
        childSources[0].setNewSourceInfo(nonEmptyTimeline, null);
        timeline = testRunner.assertTimelineChangeBlocking();
        TimelineAsserts.assertPeriodCounts(timeline, 1);
        /* newManifest== */
        childSources[2].setNewSourceInfo(nonEmptyTimeline, null);
        timeline = testRunner.assertTimelineChangeBlocking();
        TimelineAsserts.assertPeriodCounts(timeline, 1, 1);
        /* newManifest== */
        childSources[1].setNewSourceInfo(nonEmptyTimeline, null);
        timeline = testRunner.assertTimelineChangeBlocking();
        TimelineAsserts.assertPeriodCounts(timeline, 1, 1, 1);
    }

    @Test
    public void testIllegalArguments() {
        MediaSource validSource = new FakeMediaSource(ConcatenatingMediaSourceTest.createFakeTimeline(1), null);
        // Null sources.
        try {
            mediaSource.addMediaSource(null);
            Assert.fail("Null mediaSource not allowed.");
        } catch (NullPointerException e) {
            // Expected.
        }
        MediaSource[] mediaSources = new MediaSource[]{ validSource, null };
        try {
            mediaSource.addMediaSources(Arrays.asList(mediaSources));
            Assert.fail("Null mediaSource not allowed.");
        } catch (NullPointerException e) {
            // Expected.
        }
    }

    @Test
    public void testCustomCallbackBeforePreparationAddSingle() {
        Runnable runnable = Mockito.mock(Runnable.class);
        mediaSource.addMediaSource(ConcatenatingMediaSourceTest.createFakeMediaSource(), new Handler(), runnable);
        Mockito.verify(runnable).run();
    }

    @Test
    public void testCustomCallbackBeforePreparationAddMultiple() {
        Runnable runnable = Mockito.mock(Runnable.class);
        mediaSource.addMediaSources(Arrays.asList(new MediaSource[]{ ConcatenatingMediaSourceTest.createFakeMediaSource(), ConcatenatingMediaSourceTest.createFakeMediaSource() }), new Handler(), runnable);
        Mockito.verify(runnable).run();
    }

    @Test
    public void testCustomCallbackBeforePreparationAddSingleWithIndex() {
        Runnable runnable = Mockito.mock(Runnable.class);
        /* index */
        mediaSource.addMediaSource(0, ConcatenatingMediaSourceTest.createFakeMediaSource(), new Handler(), runnable);
        Mockito.verify(runnable).run();
    }

    @Test
    public void testCustomCallbackBeforePreparationAddMultipleWithIndex() {
        Runnable runnable = Mockito.mock(Runnable.class);
        /* index */
        mediaSource.addMediaSources(0, Arrays.asList(new MediaSource[]{ ConcatenatingMediaSourceTest.createFakeMediaSource(), ConcatenatingMediaSourceTest.createFakeMediaSource() }), new Handler(), runnable);
        Mockito.verify(runnable).run();
    }

    @Test
    public void testCustomCallbackBeforePreparationRemove() {
        Runnable runnable = Mockito.mock(Runnable.class);
        mediaSource.addMediaSource(ConcatenatingMediaSourceTest.createFakeMediaSource());
        /* index */
        mediaSource.removeMediaSource(0, new Handler(), runnable);
        Mockito.verify(runnable).run();
    }

    @Test
    public void testCustomCallbackBeforePreparationMove() {
        Runnable runnable = Mockito.mock(Runnable.class);
        mediaSource.addMediaSources(Arrays.asList(new MediaSource[]{ ConcatenatingMediaSourceTest.createFakeMediaSource(), ConcatenatingMediaSourceTest.createFakeMediaSource() }));
        /* fromIndex */
        /* toIndex */
        mediaSource.moveMediaSource(1, 0, new Handler(), runnable);
        Mockito.verify(runnable).run();
    }

    @Test
    public void testCustomCallbackAfterPreparationAddSingle() throws IOException {
        DummyMainThread dummyMainThread = new DummyMainThread();
        try {
            testRunner.prepareSource();
            final ConcatenatingMediaSourceTest.TimelineGrabber timelineGrabber = new ConcatenatingMediaSourceTest.TimelineGrabber(testRunner);
            dummyMainThread.runOnMainThread(() -> mediaSource.addMediaSource(createFakeMediaSource(), new Handler(), timelineGrabber));
            Timeline timeline = timelineGrabber.assertTimelineChangeBlocking();
            assertThat(timeline.getWindowCount()).isEqualTo(1);
        } finally {
            dummyMainThread.release();
        }
    }

    @Test
    public void testCustomCallbackAfterPreparationAddMultiple() throws IOException {
        DummyMainThread dummyMainThread = new DummyMainThread();
        try {
            testRunner.prepareSource();
            final ConcatenatingMediaSourceTest.TimelineGrabber timelineGrabber = new ConcatenatingMediaSourceTest.TimelineGrabber(testRunner);
            dummyMainThread.runOnMainThread(() -> mediaSource.addMediaSources(Arrays.asList(new MediaSource[]{ createFakeMediaSource(), createFakeMediaSource() }), new Handler(), timelineGrabber));
            Timeline timeline = timelineGrabber.assertTimelineChangeBlocking();
            assertThat(timeline.getWindowCount()).isEqualTo(2);
        } finally {
            dummyMainThread.release();
        }
    }

    @Test
    public void testCustomCallbackAfterPreparationAddSingleWithIndex() throws IOException {
        DummyMainThread dummyMainThread = new DummyMainThread();
        try {
            testRunner.prepareSource();
            final ConcatenatingMediaSourceTest.TimelineGrabber timelineGrabber = new ConcatenatingMediaSourceTest.TimelineGrabber(testRunner);
            dummyMainThread.runOnMainThread(() -> /* index */
            mediaSource.addMediaSource(0, createFakeMediaSource(), new Handler(), timelineGrabber));
            Timeline timeline = timelineGrabber.assertTimelineChangeBlocking();
            assertThat(timeline.getWindowCount()).isEqualTo(1);
        } finally {
            dummyMainThread.release();
        }
    }

    @Test
    public void testCustomCallbackAfterPreparationAddMultipleWithIndex() throws IOException {
        DummyMainThread dummyMainThread = new DummyMainThread();
        try {
            testRunner.prepareSource();
            final ConcatenatingMediaSourceTest.TimelineGrabber timelineGrabber = new ConcatenatingMediaSourceTest.TimelineGrabber(testRunner);
            dummyMainThread.runOnMainThread(() -> /* index */
            mediaSource.addMediaSources(0, Arrays.asList(new MediaSource[]{ createFakeMediaSource(), createFakeMediaSource() }), new Handler(), timelineGrabber));
            Timeline timeline = timelineGrabber.assertTimelineChangeBlocking();
            assertThat(timeline.getWindowCount()).isEqualTo(2);
        } finally {
            dummyMainThread.release();
        }
    }

    @Test
    public void testCustomCallbackAfterPreparationRemove() throws IOException {
        DummyMainThread dummyMainThread = new DummyMainThread();
        try {
            testRunner.prepareSource();
            dummyMainThread.runOnMainThread(() -> mediaSource.addMediaSource(createFakeMediaSource()));
            testRunner.assertTimelineChangeBlocking();
            final ConcatenatingMediaSourceTest.TimelineGrabber timelineGrabber = new ConcatenatingMediaSourceTest.TimelineGrabber(testRunner);
            dummyMainThread.runOnMainThread(() -> /* index */
            mediaSource.removeMediaSource(0, new Handler(), timelineGrabber));
            Timeline timeline = timelineGrabber.assertTimelineChangeBlocking();
            assertThat(timeline.getWindowCount()).isEqualTo(0);
        } finally {
            dummyMainThread.release();
        }
    }

    @Test
    public void testCustomCallbackAfterPreparationMove() throws IOException {
        DummyMainThread dummyMainThread = new DummyMainThread();
        try {
            testRunner.prepareSource();
            dummyMainThread.runOnMainThread(() -> mediaSource.addMediaSources(Arrays.asList(new MediaSource[]{ createFakeMediaSource(), createFakeMediaSource() })));
            testRunner.assertTimelineChangeBlocking();
            final ConcatenatingMediaSourceTest.TimelineGrabber timelineGrabber = new ConcatenatingMediaSourceTest.TimelineGrabber(testRunner);
            dummyMainThread.runOnMainThread(() -> /* fromIndex */
            /* toIndex */
            mediaSource.moveMediaSource(1, 0, new Handler(), timelineGrabber));
            Timeline timeline = timelineGrabber.assertTimelineChangeBlocking();
            assertThat(timeline.getWindowCount()).isEqualTo(2);
        } finally {
            dummyMainThread.release();
        }
    }

    @Test
    public void testPeriodCreationWithAds() throws IOException, InterruptedException {
        // Create concatenated media source with ad child source.
        Timeline timelineContentOnly = new FakeTimeline(new com.google.android.exoplayer2.testutil.FakeTimeline.TimelineWindowDefinition(2, 111, true, false, (10 * (C.MICROS_PER_SECOND))));
        Timeline timelineWithAds = new FakeTimeline(new com.google.android.exoplayer2.testutil.FakeTimeline.TimelineWindowDefinition(2, 222, true, false, (10 * (C.MICROS_PER_SECOND)), /* adsPerAdGroup= */
        /* adGroupTimesUs= */
        FakeTimeline.createAdPlaybackState(1, 0)));
        FakeMediaSource mediaSourceContentOnly = new FakeMediaSource(timelineContentOnly, null);
        FakeMediaSource mediaSourceWithAds = new FakeMediaSource(timelineWithAds, null);
        mediaSource.addMediaSource(mediaSourceContentOnly);
        mediaSource.addMediaSource(mediaSourceWithAds);
        Timeline timeline = testRunner.prepareSource();
        // Assert the timeline contains ad groups.
        TimelineAsserts.assertAdGroupCounts(timeline, 0, 0, 1, 1);
        // Create all periods and assert period creation of child media sources has been called.
        testRunner.assertPrepareAndReleaseAllPeriods();
        Object timelineContentOnlyPeriodUid0 = /* periodIndex= */
        timelineContentOnly.getUidOfPeriod(0);
        Object timelineContentOnlyPeriodUid1 = /* periodIndex= */
        timelineContentOnly.getUidOfPeriod(1);
        Object timelineWithAdsPeriodUid0 = /* periodIndex= */
        timelineWithAds.getUidOfPeriod(0);
        Object timelineWithAdsPeriodUid1 = /* periodIndex= */
        timelineWithAds.getUidOfPeriod(1);
        mediaSourceContentOnly.assertMediaPeriodCreated(/* windowSequenceNumber= */
        new MediaPeriodId(timelineContentOnlyPeriodUid0, 0));
        mediaSourceContentOnly.assertMediaPeriodCreated(/* windowSequenceNumber= */
        new MediaPeriodId(timelineContentOnlyPeriodUid1, 0));
        mediaSourceWithAds.assertMediaPeriodCreated(/* windowSequenceNumber= */
        new MediaPeriodId(timelineWithAdsPeriodUid0, 1));
        mediaSourceWithAds.assertMediaPeriodCreated(/* windowSequenceNumber= */
        new MediaPeriodId(timelineWithAdsPeriodUid1, 1));
        mediaSourceWithAds.assertMediaPeriodCreated(/* adGroupIndex= */
        /* adIndexInAdGroup= */
        /* windowSequenceNumber= */
        new MediaPeriodId(timelineWithAdsPeriodUid0, 0, 0, 1));
        mediaSourceWithAds.assertMediaPeriodCreated(/* adGroupIndex= */
        /* adIndexInAdGroup= */
        /* windowSequenceNumber= */
        new MediaPeriodId(timelineWithAdsPeriodUid1, 0, 0, 1));
        testRunner.assertCompletedManifestLoads(0, 1);
        assertCompletedAllMediaPeriodLoads(timeline);
    }

    @Test
    public void testAtomicTimelineWindowOrder() throws IOException {
        // Release default test runner with non-atomic media source and replace with new test runner.
        testRunner.release();
        ConcatenatingMediaSource mediaSource = /* isAtomic= */
        new ConcatenatingMediaSource(true, new FakeShuffleOrder(0));
        testRunner = new MediaSourceTestRunner(mediaSource, null);
        mediaSource.addMediaSources(Arrays.asList(ConcatenatingMediaSourceTest.createMediaSources(3)));
        Timeline timeline = testRunner.prepareSource();
        TimelineAsserts.assertWindowTags(timeline, 111, 222, 333);
        TimelineAsserts.assertPeriodCounts(timeline, 1, 2, 3);
        /* shuffleModeEnabled= */
        TimelineAsserts.assertPreviousWindowIndices(timeline, REPEAT_MODE_OFF, false, INDEX_UNSET, 0, 1);
        /* shuffleModeEnabled= */
        TimelineAsserts.assertPreviousWindowIndices(timeline, REPEAT_MODE_OFF, true, INDEX_UNSET, 0, 1);
        /* shuffleModeEnabled= */
        TimelineAsserts.assertPreviousWindowIndices(timeline, REPEAT_MODE_ONE, false, 2, 0, 1);
        /* shuffleModeEnabled= */
        TimelineAsserts.assertPreviousWindowIndices(timeline, REPEAT_MODE_ONE, true, 2, 0, 1);
        /* shuffleModeEnabled= */
        TimelineAsserts.assertPreviousWindowIndices(timeline, REPEAT_MODE_ALL, false, 2, 0, 1);
        /* shuffleModeEnabled= */
        TimelineAsserts.assertPreviousWindowIndices(timeline, REPEAT_MODE_ALL, true, 2, 0, 1);
        /* shuffleModeEnabled= */
        TimelineAsserts.assertNextWindowIndices(timeline, REPEAT_MODE_OFF, false, 1, 2, INDEX_UNSET);
        /* shuffleModeEnabled= */
        TimelineAsserts.assertNextWindowIndices(timeline, REPEAT_MODE_OFF, true, 1, 2, INDEX_UNSET);
        /* shuffleModeEnabled= */
        TimelineAsserts.assertNextWindowIndices(timeline, REPEAT_MODE_ONE, false, 1, 2, 0);
        /* shuffleModeEnabled= */
        TimelineAsserts.assertNextWindowIndices(timeline, REPEAT_MODE_ONE, true, 1, 2, 0);
        /* shuffleModeEnabled= */
        TimelineAsserts.assertNextWindowIndices(timeline, REPEAT_MODE_ALL, false, 1, 2, 0);
        /* shuffleModeEnabled= */
        TimelineAsserts.assertNextWindowIndices(timeline, REPEAT_MODE_ALL, true, 1, 2, 0);
        assertThat(/* shuffleModeEnabled= */
        timeline.getFirstWindowIndex(false)).isEqualTo(0);
        assertThat(/* shuffleModeEnabled= */
        timeline.getFirstWindowIndex(true)).isEqualTo(0);
        assertThat(/* shuffleModeEnabled= */
        timeline.getLastWindowIndex(false)).isEqualTo(2);
        assertThat(/* shuffleModeEnabled= */
        timeline.getLastWindowIndex(true)).isEqualTo(2);
    }

    @Test
    public void testNestedTimeline() throws IOException {
        ConcatenatingMediaSource nestedSource1 = /* isAtomic= */
        new ConcatenatingMediaSource(false, new FakeShuffleOrder(0));
        ConcatenatingMediaSource nestedSource2 = /* isAtomic= */
        new ConcatenatingMediaSource(true, new FakeShuffleOrder(0));
        mediaSource.addMediaSource(nestedSource1);
        mediaSource.addMediaSource(nestedSource2);
        testRunner.prepareSource();
        FakeMediaSource[] childSources = ConcatenatingMediaSourceTest.createMediaSources(4);
        nestedSource1.addMediaSource(childSources[0]);
        testRunner.assertTimelineChangeBlocking();
        nestedSource1.addMediaSource(childSources[1]);
        testRunner.assertTimelineChangeBlocking();
        nestedSource2.addMediaSource(childSources[2]);
        testRunner.assertTimelineChangeBlocking();
        nestedSource2.addMediaSource(childSources[3]);
        Timeline timeline = testRunner.assertTimelineChangeBlocking();
        TimelineAsserts.assertWindowTags(timeline, 111, 222, 333, 444);
        TimelineAsserts.assertPeriodCounts(timeline, 1, 2, 3, 4);
        /* shuffleModeEnabled= */
        TimelineAsserts.assertPreviousWindowIndices(timeline, REPEAT_MODE_OFF, false, INDEX_UNSET, 0, 1, 2);
        /* shuffleModeEnabled= */
        TimelineAsserts.assertPreviousWindowIndices(timeline, REPEAT_MODE_ONE, false, 0, 1, 3, 2);
        /* shuffleModeEnabled= */
        TimelineAsserts.assertPreviousWindowIndices(timeline, REPEAT_MODE_ALL, false, 3, 0, 1, 2);
        /* shuffleModeEnabled= */
        TimelineAsserts.assertNextWindowIndices(timeline, REPEAT_MODE_OFF, false, 1, 2, 3, INDEX_UNSET);
        /* shuffleModeEnabled= */
        TimelineAsserts.assertNextWindowIndices(timeline, REPEAT_MODE_ONE, false, 0, 1, 3, 2);
        /* shuffleModeEnabled= */
        TimelineAsserts.assertNextWindowIndices(timeline, REPEAT_MODE_ALL, false, 1, 2, 3, 0);
        /* shuffleModeEnabled= */
        TimelineAsserts.assertPreviousWindowIndices(timeline, REPEAT_MODE_OFF, true, 1, 3, INDEX_UNSET, 2);
        /* shuffleModeEnabled= */
        TimelineAsserts.assertPreviousWindowIndices(timeline, REPEAT_MODE_ONE, true, 0, 1, 3, 2);
        /* shuffleModeEnabled= */
        TimelineAsserts.assertPreviousWindowIndices(timeline, REPEAT_MODE_ALL, true, 1, 3, 0, 2);
        /* shuffleModeEnabled= */
        TimelineAsserts.assertNextWindowIndices(timeline, REPEAT_MODE_OFF, true, INDEX_UNSET, 0, 3, 1);
        /* shuffleModeEnabled= */
        TimelineAsserts.assertNextWindowIndices(timeline, REPEAT_MODE_ONE, true, 0, 1, 3, 2);
        /* shuffleModeEnabled= */
        TimelineAsserts.assertNextWindowIndices(timeline, REPEAT_MODE_ALL, true, 2, 0, 3, 1);
    }

    @Test
    public void testRemoveChildSourceWithActiveMediaPeriod() throws IOException {
        FakeMediaSource childSource = ConcatenatingMediaSourceTest.createFakeMediaSource();
        mediaSource.addMediaSource(childSource);
        Timeline timeline = testRunner.prepareSource();
        MediaPeriod mediaPeriod = testRunner.createPeriod(/* windowSequenceNumber= */
        new MediaPeriodId(/* periodIndex= */
        timeline.getUidOfPeriod(0), 0));
        /* index= */
        mediaSource.removeMediaSource(0);
        testRunner.assertTimelineChangeBlocking();
        testRunner.releasePeriod(mediaPeriod);
        childSource.assertReleased();
        testRunner.releaseSource();
    }

    @Test
    public void testDuplicateMediaSources() throws IOException, InterruptedException {
        Timeline childTimeline = /* windowCount= */
        new FakeTimeline(2);
        FakeMediaSource childSource = /* manifest= */
        new FakeMediaSource(childTimeline, null);
        mediaSource.addMediaSource(childSource);
        mediaSource.addMediaSource(childSource);
        testRunner.prepareSource();
        mediaSource.addMediaSources(Arrays.asList(childSource, childSource));
        Timeline timeline = testRunner.assertTimelineChangeBlocking();
        TimelineAsserts.assertPeriodCounts(timeline, 1, 1, 1, 1, 1, 1, 1, 1);
        testRunner.assertPrepareAndReleaseAllPeriods();
        Object childPeriodUid0 = /* periodIndex= */
        childTimeline.getUidOfPeriod(0);
        Object childPeriodUid1 = /* periodIndex= */
        childTimeline.getUidOfPeriod(1);
        assertThat(childSource.getCreatedMediaPeriods()).containsAllOf(/* windowSequenceNumber= */
        new MediaPeriodId(childPeriodUid0, 0), /* windowSequenceNumber= */
        new MediaPeriodId(childPeriodUid0, 2), /* windowSequenceNumber= */
        new MediaPeriodId(childPeriodUid0, 4), /* windowSequenceNumber= */
        new MediaPeriodId(childPeriodUid0, 6), /* windowSequenceNumber= */
        new MediaPeriodId(childPeriodUid1, 1), /* windowSequenceNumber= */
        new MediaPeriodId(childPeriodUid1, 3), /* windowSequenceNumber= */
        new MediaPeriodId(childPeriodUid1, 5), /* windowSequenceNumber= */
        new MediaPeriodId(childPeriodUid1, 7));
        // Assert that only one manifest load is reported because the source is reused.
        /* windowIndices= */
        testRunner.assertCompletedManifestLoads(0);
        assertCompletedAllMediaPeriodLoads(timeline);
        testRunner.releaseSource();
        childSource.assertReleased();
    }

    @Test
    public void testDuplicateNestedMediaSources() throws IOException, InterruptedException {
        Timeline childTimeline = /* windowCount= */
        new FakeTimeline(1);
        FakeMediaSource childSource = /* manifest= */
        new FakeMediaSource(childTimeline, null);
        ConcatenatingMediaSource nestedConcatenation = new ConcatenatingMediaSource();
        testRunner.prepareSource();
        mediaSource.addMediaSources(Arrays.asList(childSource, nestedConcatenation, nestedConcatenation));
        testRunner.assertTimelineChangeBlocking();
        nestedConcatenation.addMediaSource(childSource);
        testRunner.assertTimelineChangeBlocking();
        nestedConcatenation.addMediaSource(childSource);
        Timeline timeline = testRunner.assertTimelineChangeBlocking();
        TimelineAsserts.assertPeriodCounts(timeline, 1, 1, 1, 1, 1);
        testRunner.assertPrepareAndReleaseAllPeriods();
        Object childPeriodUid = /* periodIndex= */
        childTimeline.getUidOfPeriod(0);
        assertThat(childSource.getCreatedMediaPeriods()).containsAllOf(/* windowSequenceNumber= */
        new MediaPeriodId(childPeriodUid, 0), /* windowSequenceNumber= */
        new MediaPeriodId(childPeriodUid, 1), /* windowSequenceNumber= */
        new MediaPeriodId(childPeriodUid, 2), /* windowSequenceNumber= */
        new MediaPeriodId(childPeriodUid, 3), /* windowSequenceNumber= */
        new MediaPeriodId(childPeriodUid, 4));
        // Assert that only one manifest load is needed because the source is reused.
        /* windowIndices= */
        testRunner.assertCompletedManifestLoads(0);
        assertCompletedAllMediaPeriodLoads(timeline);
        testRunner.releaseSource();
        childSource.assertReleased();
    }

    @Test
    public void testClear() throws IOException {
        DummyMainThread dummyMainThread = new DummyMainThread();
        final FakeMediaSource preparedChildSource = ConcatenatingMediaSourceTest.createFakeMediaSource();
        final FakeMediaSource unpreparedChildSource = /* timeline= */
        /* manifest= */
        new FakeMediaSource(null, null);
        dummyMainThread.runOnMainThread(() -> {
            mediaSource.addMediaSource(preparedChildSource);
            mediaSource.addMediaSource(unpreparedChildSource);
        });
        testRunner.prepareSource();
        final ConcatenatingMediaSourceTest.TimelineGrabber timelineGrabber = new ConcatenatingMediaSourceTest.TimelineGrabber(testRunner);
        dummyMainThread.runOnMainThread(() -> mediaSource.clear(new Handler(), timelineGrabber));
        Timeline timeline = timelineGrabber.assertTimelineChangeBlocking();
        assertThat(timeline.isEmpty()).isTrue();
        preparedChildSource.assertReleased();
        unpreparedChildSource.assertReleased();
    }

    @Test
    public void testReleaseAndReprepareSource() throws IOException {
        FakeMediaSource[] fakeMediaSources = /* count= */
        ConcatenatingMediaSourceTest.createMediaSources(2);
        mediaSource.addMediaSource(fakeMediaSources[0]);// Child source with 1 period.

        mediaSource.addMediaSource(fakeMediaSources[1]);// Child source with 2 periods.

        Timeline timeline = testRunner.prepareSource();
        Object periodId0 = /* periodIndex= */
        timeline.getUidOfPeriod(0);
        Object periodId1 = /* periodIndex= */
        timeline.getUidOfPeriod(1);
        Object periodId2 = /* periodIndex= */
        timeline.getUidOfPeriod(2);
        testRunner.releaseSource();
        /* currentIndex= */
        /* newIndex= */
        mediaSource.moveMediaSource(1, 0);
        timeline = testRunner.prepareSource();
        Object newPeriodId0 = /* periodIndex= */
        timeline.getUidOfPeriod(0);
        Object newPeriodId1 = /* periodIndex= */
        timeline.getUidOfPeriod(1);
        Object newPeriodId2 = /* periodIndex= */
        timeline.getUidOfPeriod(2);
        assertThat(newPeriodId0).isEqualTo(periodId1);
        assertThat(newPeriodId1).isEqualTo(periodId2);
        assertThat(newPeriodId2).isEqualTo(periodId0);
    }

    @Test
    public void testChildTimelineChangeWithActiveMediaPeriod() throws IOException {
        FakeMediaSource[] nestedChildSources = /* count= */
        ConcatenatingMediaSourceTest.createMediaSources(2);
        ConcatenatingMediaSource childSource = new ConcatenatingMediaSource(nestedChildSources);
        mediaSource.addMediaSource(childSource);
        Timeline timeline = testRunner.prepareSource();
        MediaPeriod mediaPeriod = testRunner.createPeriod(/* windowSequenceNumber= */
        new MediaPeriodId(/* periodIndex= */
        timeline.getUidOfPeriod(1), 0));
        /* currentIndex= */
        /* newIndex= */
        childSource.moveMediaSource(0, 1);
        timeline = testRunner.assertTimelineChangeBlocking();
        /* positionUs= */
        testRunner.preparePeriod(mediaPeriod, 0);
        testRunner.assertCompletedMediaPeriodLoads(/* windowSequenceNumber= */
        new MediaPeriodId(/* periodIndex= */
        timeline.getUidOfPeriod(0), 0));
    }

    @Test
    public void testChildSourceIsNotPreparedWithLazyPreparation() throws IOException {
        FakeMediaSource[] childSources = /* count= */
        ConcatenatingMediaSourceTest.createMediaSources(2);
        mediaSource = /* isAtomic= */
        /* useLazyPreparation= */
        new ConcatenatingMediaSource(false, true, new DefaultShuffleOrder(0), childSources);
        testRunner = /* allocator= */
        new MediaSourceTestRunner(mediaSource, null);
        testRunner.prepareSource();
        assertThat(childSources[0].isPrepared()).isFalse();
        assertThat(childSources[1].isPrepared()).isFalse();
    }

    @Test
    public void testChildSourceIsPreparedWithLazyPreparationAfterPeriodCreation() throws IOException {
        FakeMediaSource[] childSources = /* count= */
        ConcatenatingMediaSourceTest.createMediaSources(2);
        mediaSource = /* isAtomic= */
        /* useLazyPreparation= */
        new ConcatenatingMediaSource(false, true, new DefaultShuffleOrder(0), childSources);
        testRunner = /* allocator= */
        new MediaSourceTestRunner(mediaSource, null);
        Timeline timeline = testRunner.prepareSource();
        testRunner.createPeriod(/* windowSequenceNumber= */
        new MediaPeriodId(/* periodIndex= */
        timeline.getUidOfPeriod(0), 0));
        assertThat(childSources[0].isPrepared()).isTrue();
        assertThat(childSources[1].isPrepared()).isFalse();
    }

    @Test
    public void testChildSourceWithLazyPreparationOnlyPreparesSourceOnce() throws IOException {
        FakeMediaSource[] childSources = /* count= */
        ConcatenatingMediaSourceTest.createMediaSources(2);
        mediaSource = /* isAtomic= */
        /* useLazyPreparation= */
        new ConcatenatingMediaSource(false, true, new DefaultShuffleOrder(0), childSources);
        testRunner = /* allocator= */
        new MediaSourceTestRunner(mediaSource, null);
        Timeline timeline = testRunner.prepareSource();
        // The lazy preparation must only be triggered once, even if we create multiple periods from
        // the media source. FakeMediaSource.prepareSource asserts that it's not called twice, so
        // creating two periods shouldn't throw.
        MediaPeriodId mediaPeriodId = /* windowSequenceNumber= */
        new MediaPeriodId(/* periodIndex= */
        timeline.getUidOfPeriod(0), 0);
        testRunner.createPeriod(mediaPeriodId);
        testRunner.createPeriod(mediaPeriodId);
    }

    @Test
    public void testRemoveUnpreparedChildSourceWithLazyPreparation() throws IOException {
        FakeMediaSource[] childSources = /* count= */
        ConcatenatingMediaSourceTest.createMediaSources(2);
        mediaSource = /* isAtomic= */
        /* useLazyPreparation= */
        new ConcatenatingMediaSource(false, true, new DefaultShuffleOrder(0), childSources);
        testRunner = /* allocator= */
        new MediaSourceTestRunner(mediaSource, null);
        testRunner.prepareSource();
        // Check that removal doesn't throw even though the child sources are unprepared.
        mediaSource.removeMediaSource(0);
    }

    @Test
    public void testSetShuffleOrderBeforePreparation() throws Exception {
        mediaSource.setShuffleOrder(/* length= */
        new ShuffleOrder.UnshuffledShuffleOrder(0));
        mediaSource.addMediaSources(Arrays.asList(ConcatenatingMediaSourceTest.createFakeMediaSource(), ConcatenatingMediaSourceTest.createFakeMediaSource(), ConcatenatingMediaSourceTest.createFakeMediaSource()));
        Timeline timeline = testRunner.prepareSource();
        assertThat(/* shuffleModeEnabled= */
        timeline.getFirstWindowIndex(true)).isEqualTo(0);
    }

    @Test
    public void testSetShuffleOrderAfterPreparation() throws Exception {
        mediaSource.addMediaSources(Arrays.asList(ConcatenatingMediaSourceTest.createFakeMediaSource(), ConcatenatingMediaSourceTest.createFakeMediaSource(), ConcatenatingMediaSourceTest.createFakeMediaSource()));
        testRunner.prepareSource();
        mediaSource.setShuffleOrder(/* length= */
        new ShuffleOrder.UnshuffledShuffleOrder(3));
        Timeline timeline = testRunner.assertTimelineChangeBlocking();
        assertThat(/* shuffleModeEnabled= */
        timeline.getFirstWindowIndex(true)).isEqualTo(0);
    }

    @Test
    public void testCustomCallbackBeforePreparationSetShuffleOrder() throws Exception {
        Runnable runnable = Mockito.mock(Runnable.class);
        mediaSource.setShuffleOrder(/* length= */
        new ShuffleOrder.UnshuffledShuffleOrder(0), new Handler(), runnable);
        Mockito.verify(runnable).run();
    }

    @Test
    public void testCustomCallbackAfterPreparationSetShuffleOrder() throws Exception {
        DummyMainThread dummyMainThread = new DummyMainThread();
        try {
            mediaSource.addMediaSources(Arrays.asList(ConcatenatingMediaSourceTest.createFakeMediaSource(), ConcatenatingMediaSourceTest.createFakeMediaSource(), ConcatenatingMediaSourceTest.createFakeMediaSource()));
            testRunner.prepareSource();
            ConcatenatingMediaSourceTest.TimelineGrabber timelineGrabber = new ConcatenatingMediaSourceTest.TimelineGrabber(testRunner);
            dummyMainThread.runOnMainThread(() -> mediaSource.setShuffleOrder(/* length= */
            new ShuffleOrder.UnshuffledShuffleOrder(3), new Handler(), timelineGrabber));
            Timeline timeline = timelineGrabber.assertTimelineChangeBlocking();
            assertThat(/* shuffleModeEnabled= */
            timeline.getFirstWindowIndex(true)).isEqualTo(0);
        } finally {
            dummyMainThread.release();
        }
    }

    private static final class TimelineGrabber implements Runnable {
        private final MediaSourceTestRunner testRunner;

        private final ConditionVariable finishedCondition;

        private Timeline timeline;

        private AssertionError error;

        public TimelineGrabber(MediaSourceTestRunner testRunner) {
            this.testRunner = testRunner;
            finishedCondition = new ConditionVariable();
        }

        @Override
        public void run() {
            try {
                timeline = testRunner.assertTimelineChange();
            } catch (AssertionError e) {
                error = e;
            }
            finishedCondition.open();
        }

        public Timeline assertTimelineChangeBlocking() {
            assertThat(finishedCondition.block(TIMEOUT_MS)).isTrue();
            if ((error) != null) {
                throw error;
            }
            return timeline;
        }
    }
}

