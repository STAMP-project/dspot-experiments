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
package com.google.android.exoplayer2;


import Builder.AUDIO_FORMAT;
import Builder.VIDEO_FORMAT;
import C.MSG_SET_SURFACE;
import C.POSITION_UNSET;
import Player.DISCONTINUITY_REASON_AD_INSERTION;
import Player.DISCONTINUITY_REASON_INTERNAL;
import Player.DISCONTINUITY_REASON_PERIOD_TRANSITION;
import Player.DISCONTINUITY_REASON_SEEK;
import Player.DISCONTINUITY_REASON_SEEK_ADJUSTMENT;
import Player.REPEAT_MODE_ALL;
import Player.REPEAT_MODE_OFF;
import Player.REPEAT_MODE_ONE;
import Player.STATE_BUFFERING;
import Player.STATE_ENDED;
import Player.STATE_IDLE;
import Player.STATE_READY;
import Player.TIMELINE_CHANGE_REASON_DYNAMIC;
import Player.TIMELINE_CHANGE_REASON_PREPARED;
import Player.TIMELINE_CHANGE_REASON_RESET;
import RobolectricUtil.CustomLooper;
import RobolectricUtil.CustomMessageQueue;
import Timeline.EMPTY;
import TimelineWindowDefinition.DEFAULT_WINDOW_DURATION_US;
import android.content.Context;
import android.support.annotation.Nullable;
import com.google.android.exoplayer2.Player.DiscontinuityReason;
import com.google.android.exoplayer2.Player.EventListener;
import com.google.android.exoplayer2.Timeline.Window;
import com.google.android.exoplayer2.source.ClippingMediaSource;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSource.MediaPeriodId;
import com.google.android.exoplayer2.source.MediaSourceEventListener.EventDispatcher;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.ads.AdPlaybackState;
import com.google.android.exoplayer2.testutil.ActionSchedule;
import com.google.android.exoplayer2.testutil.ActionSchedule.PlayerRunnable;
import com.google.android.exoplayer2.testutil.ActionSchedule.PlayerTarget;
import com.google.android.exoplayer2.testutil.AutoAdvancingFakeClock;
import com.google.android.exoplayer2.testutil.ExoPlayerTestRunner;
import com.google.android.exoplayer2.testutil.ExoPlayerTestRunner.Builder;
import com.google.android.exoplayer2.testutil.FakeMediaClockRenderer;
import com.google.android.exoplayer2.testutil.FakeMediaPeriod;
import com.google.android.exoplayer2.testutil.FakeMediaSource;
import com.google.android.exoplayer2.testutil.FakeRenderer;
import com.google.android.exoplayer2.testutil.FakeShuffleOrder;
import com.google.android.exoplayer2.testutil.FakeTimeline;
import com.google.android.exoplayer2.testutil.FakeTimeline.TimelineWindowDefinition;
import com.google.android.exoplayer2.testutil.FakeTrackSelection;
import com.google.android.exoplayer2.testutil.FakeTrackSelector;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Clock;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static C.INDEX_UNSET;
import static C.MICROS_PER_SECOND;
import static C.POSITION_UNSET;
import static C.TIME_END_OF_SOURCE;
import static C.TIME_UNSET;
import static PlaybackParameters.DEFAULT;
import static Player.DISCONTINUITY_REASON_PERIOD_TRANSITION;
import static Player.STATE_IDLE;
import static Player.STATE_READY;
import static Timeline.EMPTY;


/**
 * Unit test for {@link ExoPlayer}.
 */
@RunWith(RobolectricTestRunner.class)
@Config(shadows = { CustomLooper.class, CustomMessageQueue.class })
public final class ExoPlayerTest {
    /**
     * For tests that rely on the player transitioning to the ended state, the duration in
     * milliseconds after starting the player before the test will time out. This is to catch cases
     * where the player under test is not making progress, in which case the test should fail.
     */
    private static final int TIMEOUT_MS = 10000;

    private Context context;

    /**
     * Tests playback of a source that exposes an empty timeline. Playback is expected to end without
     * error.
     */
    @Test
    public void testPlayEmptyTimeline() throws Exception {
        Timeline timeline = EMPTY;
        FakeRenderer renderer = new FakeRenderer();
        ExoPlayerTestRunner testRunner = new Builder().setTimeline(timeline).setRenderers(renderer).build(context).start().blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
        testRunner.assertNoPositionDiscontinuities();
        testRunner.assertTimelinesEqual(timeline);
        assertThat(renderer.formatReadCount).isEqualTo(0);
        assertThat(renderer.sampleBufferReadCount).isEqualTo(0);
        assertThat(renderer.isEnded).isFalse();
    }

    /**
     * Tests playback of a source that exposes a single period.
     */
    @Test
    public void testPlaySinglePeriodTimeline() throws Exception {
        Timeline timeline = /* windowCount= */
        new FakeTimeline(1);
        Object manifest = new Object();
        FakeRenderer renderer = new FakeRenderer(Builder.VIDEO_FORMAT);
        ExoPlayerTestRunner testRunner = new Builder().setTimeline(timeline).setManifest(manifest).setRenderers(renderer).build(context).start().blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
        testRunner.assertNoPositionDiscontinuities();
        testRunner.assertTimelinesEqual(timeline);
        testRunner.assertManifestsEqual(manifest);
        testRunner.assertTimelineChangeReasonsEqual(TIMELINE_CHANGE_REASON_PREPARED);
        testRunner.assertTrackGroupsEqual(new TrackGroupArray(new com.google.android.exoplayer2.source.TrackGroup(Builder.VIDEO_FORMAT)));
        assertThat(renderer.formatReadCount).isEqualTo(1);
        assertThat(renderer.sampleBufferReadCount).isEqualTo(1);
        assertThat(renderer.isEnded).isTrue();
    }

    /**
     * Tests playback of a source that exposes three periods.
     */
    @Test
    public void testPlayMultiPeriodTimeline() throws Exception {
        Timeline timeline = /* windowCount= */
        new FakeTimeline(3);
        FakeRenderer renderer = new FakeRenderer(Builder.VIDEO_FORMAT);
        ExoPlayerTestRunner testRunner = new Builder().setTimeline(timeline).setRenderers(renderer).build(context).start().blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
        testRunner.assertPositionDiscontinuityReasonsEqual(DISCONTINUITY_REASON_PERIOD_TRANSITION, DISCONTINUITY_REASON_PERIOD_TRANSITION);
        testRunner.assertTimelinesEqual(timeline);
        testRunner.assertTimelineChangeReasonsEqual(TIMELINE_CHANGE_REASON_PREPARED);
        assertThat(renderer.formatReadCount).isEqualTo(3);
        assertThat(renderer.sampleBufferReadCount).isEqualTo(3);
        assertThat(renderer.isEnded).isTrue();
    }

    /**
     * Tests playback of periods with very short duration.
     */
    @Test
    public void testPlayShortDurationPeriods() throws Exception {
        // TimelineWindowDefinition.DEFAULT_WINDOW_DURATION_US / 100 = 1000 us per period.
        Timeline timeline = new FakeTimeline(/* periodCount= */
        /* id= */
        new TimelineWindowDefinition(100, 0));
        FakeRenderer renderer = new FakeRenderer(Builder.VIDEO_FORMAT);
        ExoPlayerTestRunner testRunner = new Builder().setTimeline(timeline).setRenderers(renderer).build(context).start().blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
        Integer[] expectedReasons = new Integer[99];
        Arrays.fill(expectedReasons, DISCONTINUITY_REASON_PERIOD_TRANSITION);
        testRunner.assertPositionDiscontinuityReasonsEqual(expectedReasons);
        testRunner.assertTimelinesEqual(timeline);
        testRunner.assertTimelineChangeReasonsEqual(TIMELINE_CHANGE_REASON_PREPARED);
        assertThat(renderer.formatReadCount).isEqualTo(100);
        assertThat(renderer.sampleBufferReadCount).isEqualTo(100);
        assertThat(renderer.isEnded).isTrue();
    }

    /**
     * Tests that the player does not unnecessarily reset renderers when playing a multi-period
     * source.
     */
    @Test
    public void testReadAheadToEndDoesNotResetRenderer() throws Exception {
        // Use sufficiently short periods to ensure the player attempts to read all at once.
        TimelineWindowDefinition windowDefinition0 = /* periodCount= */
        /* id= */
        /* isSeekable= */
        /* isDynamic= */
        /* durationUs= */
        new TimelineWindowDefinition(1, 0, false, false, 100000);
        TimelineWindowDefinition windowDefinition1 = /* periodCount= */
        /* id= */
        /* isSeekable= */
        /* isDynamic= */
        /* durationUs= */
        new TimelineWindowDefinition(1, 1, false, false, 100000);
        TimelineWindowDefinition windowDefinition2 = /* periodCount= */
        /* id= */
        /* isSeekable= */
        /* isDynamic= */
        /* durationUs= */
        new TimelineWindowDefinition(1, 2, false, false, 100000);
        Timeline timeline = new FakeTimeline(windowDefinition0, windowDefinition1, windowDefinition2);
        final FakeRenderer videoRenderer = new FakeRenderer(Builder.VIDEO_FORMAT);
        FakeMediaClockRenderer audioRenderer = new FakeMediaClockRenderer(Builder.AUDIO_FORMAT) {
            @Override
            public long getPositionUs() {
                // Simulate the playback position lagging behind the reading position: the renderer
                // media clock position will be the start of the timeline until the stream is set to be
                // final, at which point it jumps to the end of the timeline allowing the playing period
                // to advance.
                return isCurrentStreamFinal() ? 30 : 0;
            }

            @Override
            public PlaybackParameters setPlaybackParameters(PlaybackParameters playbackParameters) {
                return DEFAULT;
            }

            @Override
            public PlaybackParameters getPlaybackParameters() {
                return DEFAULT;
            }

            @Override
            public boolean isEnded() {
                return videoRenderer.isEnded();
            }
        };
        ExoPlayerTestRunner testRunner = new Builder().setTimeline(timeline).setRenderers(videoRenderer, audioRenderer).setSupportedFormats(VIDEO_FORMAT, AUDIO_FORMAT).build(context).start().blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
        testRunner.assertPositionDiscontinuityReasonsEqual(DISCONTINUITY_REASON_PERIOD_TRANSITION, DISCONTINUITY_REASON_PERIOD_TRANSITION);
        testRunner.assertTimelinesEqual(timeline);
        assertThat(audioRenderer.positionResetCount).isEqualTo(1);
        assertThat(videoRenderer.isEnded).isTrue();
        assertThat(audioRenderer.isEnded).isTrue();
    }

    @Test
    public void testRepreparationGivesFreshSourceInfo() throws Exception {
        Timeline timeline = /* windowCount= */
        new FakeTimeline(1);
        FakeRenderer renderer = new FakeRenderer(Builder.VIDEO_FORMAT);
        Object firstSourceManifest = new Object();
        MediaSource firstSource = new FakeMediaSource(timeline, firstSourceManifest, Builder.VIDEO_FORMAT);
        final CountDownLatch queuedSourceInfoCountDownLatch = new CountDownLatch(1);
        final CountDownLatch completePreparationCountDownLatch = new CountDownLatch(1);
        MediaSource secondSource = new FakeMediaSource(timeline, new Object(), Builder.VIDEO_FORMAT) {
            @Override
            public synchronized void prepareSourceInternal(@Nullable
            TransferListener mediaTransferListener) {
                super.prepareSourceInternal(mediaTransferListener);
                // We've queued a source info refresh on the playback thread's event queue. Allow the
                // test thread to prepare the player with the third source, and block this thread (the
                // playback thread) until the test thread's call to prepare() has returned.
                queuedSourceInfoCountDownLatch.countDown();
                try {
                    completePreparationCountDownLatch.await();
                } catch (InterruptedException e) {
                    throw new IllegalStateException(e);
                }
            }
        };
        Object thirdSourceManifest = new Object();
        MediaSource thirdSource = new FakeMediaSource(timeline, thirdSourceManifest, Builder.VIDEO_FORMAT);
        // Prepare the player with a source with the first manifest and a non-empty timeline. Prepare
        // the player again with a source and a new manifest, which will never be exposed. Allow the
        // test thread to prepare the player with a third source, and block the playback thread until
        // the test thread's call to prepare() has returned.
        ActionSchedule actionSchedule = new ActionSchedule.Builder("testRepreparation").waitForTimelineChanged(timeline).prepareSource(secondSource).executeRunnable(() -> {
            try {
                queuedSourceInfoCountDownLatch.await();
            } catch ( e) {
                // Ignore.
            }
        }).prepareSource(thirdSource).executeRunnable(completePreparationCountDownLatch::countDown).build();
        ExoPlayerTestRunner testRunner = new Builder().setMediaSource(firstSource).setRenderers(renderer).setActionSchedule(actionSchedule).build(context).start().blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
        testRunner.assertNoPositionDiscontinuities();
        // The first source's preparation completed with a non-empty timeline. When the player was
        // re-prepared with the second source, it immediately exposed an empty timeline, but the source
        // info refresh from the second source was suppressed as we re-prepared with the third source.
        testRunner.assertTimelinesEqual(timeline, EMPTY, timeline);
        testRunner.assertManifestsEqual(firstSourceManifest, null, thirdSourceManifest);
        testRunner.assertTimelineChangeReasonsEqual(TIMELINE_CHANGE_REASON_PREPARED, TIMELINE_CHANGE_REASON_RESET, TIMELINE_CHANGE_REASON_PREPARED);
        testRunner.assertTrackGroupsEqual(new TrackGroupArray(new com.google.android.exoplayer2.source.TrackGroup(Builder.VIDEO_FORMAT)));
        assertThat(renderer.isEnded).isTrue();
    }

    @Test
    public void testRepeatModeChanges() throws Exception {
        Timeline timeline = /* windowCount= */
        new FakeTimeline(3);
        FakeRenderer renderer = new FakeRenderer(Builder.VIDEO_FORMAT);
        ActionSchedule actionSchedule = /* windowIndex= */
        /* windowIndex= */
        /* windowIndex= */
        /* windowIndex= */
        /* windowIndex= */
        /* windowIndex= */
        /* windowIndex= */
        new ActionSchedule.Builder("testRepeatMode").pause().waitForTimelineChanged(timeline).playUntilStartOfWindow(1).setRepeatMode(REPEAT_MODE_ONE).playUntilStartOfWindow(1).setRepeatMode(REPEAT_MODE_OFF).playUntilStartOfWindow(2).setRepeatMode(REPEAT_MODE_ONE).playUntilStartOfWindow(2).setRepeatMode(REPEAT_MODE_ALL).playUntilStartOfWindow(0).setRepeatMode(REPEAT_MODE_ONE).playUntilStartOfWindow(0).playUntilStartOfWindow(0).setRepeatMode(REPEAT_MODE_OFF).play().build();
        ExoPlayerTestRunner testRunner = new ExoPlayerTestRunner.Builder().setTimeline(timeline).setRenderers(renderer).setActionSchedule(actionSchedule).build(context).start().blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
        testRunner.assertPlayedPeriodIndices(0, 1, 1, 2, 2, 0, 0, 0, 1, 2);
        testRunner.assertPositionDiscontinuityReasonsEqual(DISCONTINUITY_REASON_PERIOD_TRANSITION, DISCONTINUITY_REASON_PERIOD_TRANSITION, DISCONTINUITY_REASON_PERIOD_TRANSITION, DISCONTINUITY_REASON_PERIOD_TRANSITION, DISCONTINUITY_REASON_PERIOD_TRANSITION, DISCONTINUITY_REASON_PERIOD_TRANSITION, DISCONTINUITY_REASON_PERIOD_TRANSITION, DISCONTINUITY_REASON_PERIOD_TRANSITION, DISCONTINUITY_REASON_PERIOD_TRANSITION);
        testRunner.assertTimelinesEqual(timeline);
        testRunner.assertTimelineChangeReasonsEqual(TIMELINE_CHANGE_REASON_PREPARED);
        assertThat(renderer.isEnded).isTrue();
    }

    @Test
    public void testShuffleModeEnabledChanges() throws Exception {
        Timeline fakeTimeline = /* windowCount= */
        new FakeTimeline(1);
        MediaSource[] fakeMediaSources = new MediaSource[]{ new FakeMediaSource(fakeTimeline, null, Builder.VIDEO_FORMAT), new FakeMediaSource(fakeTimeline, null, Builder.VIDEO_FORMAT), new FakeMediaSource(fakeTimeline, null, Builder.VIDEO_FORMAT) };
        ConcatenatingMediaSource mediaSource = new ConcatenatingMediaSource(false, new FakeShuffleOrder(3), fakeMediaSources);
        FakeRenderer renderer = new FakeRenderer(Builder.VIDEO_FORMAT);
        ActionSchedule actionSchedule = /* windowIndex= */
        /* windowIndex= */
        new ActionSchedule.Builder("testShuffleModeEnabled").pause().waitForPlaybackState(STATE_READY).setRepeatMode(REPEAT_MODE_ALL).playUntilStartOfWindow(1).setShuffleModeEnabled(true).playUntilStartOfWindow(1).setShuffleModeEnabled(false).setRepeatMode(REPEAT_MODE_OFF).play().build();
        ExoPlayerTestRunner testRunner = new ExoPlayerTestRunner.Builder().setMediaSource(mediaSource).setRenderers(renderer).setActionSchedule(actionSchedule).build(context).start().blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
        testRunner.assertPlayedPeriodIndices(0, 1, 0, 2, 1, 2);
        testRunner.assertPositionDiscontinuityReasonsEqual(DISCONTINUITY_REASON_PERIOD_TRANSITION, DISCONTINUITY_REASON_PERIOD_TRANSITION, DISCONTINUITY_REASON_PERIOD_TRANSITION, DISCONTINUITY_REASON_PERIOD_TRANSITION, DISCONTINUITY_REASON_PERIOD_TRANSITION);
        assertThat(renderer.isEnded).isTrue();
    }

    @Test
    public void testAdGroupWithLoadErrorIsSkipped() throws Exception {
        AdPlaybackState initialAdPlaybackState = /* adsPerAdGroup= */
        /* adGroupTimesUs= */
        FakeTimeline.createAdPlaybackState(1, (5 * (MICROS_PER_SECOND)));
        Timeline fakeTimeline = new FakeTimeline(/* periodCount= */
        /* id= */
        /* isSeekable= */
        /* isDynamic= */
        /* durationUs= */
        new TimelineWindowDefinition(1, 0, true, false, MICROS_PER_SECOND, initialAdPlaybackState));
        AdPlaybackState errorAdPlaybackState = initialAdPlaybackState.withAdLoadError(0, 0);
        final Timeline adErrorTimeline = new FakeTimeline(/* periodCount= */
        /* id= */
        /* isSeekable= */
        /* isDynamic= */
        /* durationUs= */
        new TimelineWindowDefinition(1, 0, true, false, MICROS_PER_SECOND, errorAdPlaybackState));
        final FakeMediaSource fakeMediaSource = /* manifest= */
        new FakeMediaSource(fakeTimeline, null, Builder.VIDEO_FORMAT);
        ActionSchedule actionSchedule = new ActionSchedule.Builder("testAdGroupWithLoadErrorIsSkipped").pause().waitForPlaybackState(STATE_READY).executeRunnable(() -> fakeMediaSource.setNewSourceInfo(adErrorTimeline, null)).waitForTimelineChanged(adErrorTimeline).play().build();
        ExoPlayerTestRunner testRunner = new ExoPlayerTestRunner.Builder().setMediaSource(fakeMediaSource).setActionSchedule(actionSchedule).build(context).start().blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
        // There is still one discontinuity from content to content for the failed ad insertion.
        testRunner.assertPositionDiscontinuityReasonsEqual(DISCONTINUITY_REASON_AD_INSERTION);
    }

    @Test
    public void testPeriodHoldersReleasedAfterSeekWithRepeatModeAll() throws Exception {
        FakeRenderer renderer = new FakeRenderer(Builder.VIDEO_FORMAT);
        ActionSchedule actionSchedule = // Turn off repeat so that playback can finish.
        // Seek with repeat mode set to Player.REPEAT_MODE_ALL.
        new ActionSchedule.Builder("testPeriodHoldersReleased").setRepeatMode(REPEAT_MODE_ALL).waitForPositionDiscontinuity().seek(0).waitForPositionDiscontinuity().setRepeatMode(REPEAT_MODE_OFF).build();
        new Builder().setRenderers(renderer).setActionSchedule(actionSchedule).build(context).start().blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
        assertThat(renderer.isEnded).isTrue();
    }

    @Test
    public void testSeekProcessedCallback() throws Exception {
        Timeline timeline = /* windowCount= */
        new FakeTimeline(2);
        ActionSchedule actionSchedule = // Seek twice in concession, expecting the first seek to be replaced (and thus except
        // only on seek processed callback).
        // Start playback and wait until playback reaches second window.
        // Wait until media source prepared and re-seek to same position. Expect a seek
        // processed while still being in STATE_READY.
        // Multiple overlapping seeks while the player is still preparing. Expect only one seek
        // processed.
        // Initial seek. Expect immediate seek processed.
        new ActionSchedule.Builder("testSeekProcessedCallback").pause().seek(5).waitForSeekProcessed().seek(2).seek(10).waitForPlaybackState(STATE_READY).seek(10).play().waitForPositionDiscontinuity().seek(5).seek(60).build();
        final List<Integer> playbackStatesWhenSeekProcessed = new ArrayList<>();
        EventListener eventListener = new EventListener() {
            private int currentPlaybackState = STATE_IDLE;

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                currentPlaybackState = playbackState;
            }

            @Override
            public void onSeekProcessed() {
                playbackStatesWhenSeekProcessed.add(currentPlaybackState);
            }
        };
        ExoPlayerTestRunner testRunner = new ExoPlayerTestRunner.Builder().setTimeline(timeline).setEventListener(eventListener).setActionSchedule(actionSchedule).build(context).start().blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
        testRunner.assertPositionDiscontinuityReasonsEqual(DISCONTINUITY_REASON_SEEK, DISCONTINUITY_REASON_SEEK, DISCONTINUITY_REASON_SEEK, DISCONTINUITY_REASON_SEEK, DISCONTINUITY_REASON_PERIOD_TRANSITION, DISCONTINUITY_REASON_SEEK, DISCONTINUITY_REASON_SEEK);
        assertThat(playbackStatesWhenSeekProcessed).containsExactly(STATE_BUFFERING, STATE_BUFFERING, STATE_READY, STATE_BUFFERING).inOrder();
    }

    @Test
    public void testSeekProcessedCalledWithIllegalSeekPosition() throws Exception {
        ActionSchedule actionSchedule = // Cause an illegal seek exception by seeking to an invalid position while the media
        // source is still being prepared and the player doesn't immediately know it will fail.
        // Because the media source prepares immediately, the exception will be thrown when the
        // player processed the seek.
        /* windowIndex= */
        /* positionMs= */
        new ActionSchedule.Builder("testSeekProcessedCalledWithIllegalSeekPosition").waitForPlaybackState(STATE_BUFFERING).seek(100, 0).waitForPlaybackState(STATE_IDLE).build();
        final boolean[] onSeekProcessedCalled = new boolean[1];
        EventListener listener = new EventListener() {
            @Override
            public void onSeekProcessed() {
                onSeekProcessedCalled[0] = true;
            }
        };
        ExoPlayerTestRunner testRunner = new Builder().setActionSchedule(actionSchedule).setEventListener(listener).build(context);
        try {
            testRunner.start().blockUntilActionScheduleFinished(ExoPlayerTest.TIMEOUT_MS).blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
            Assert.fail();
        } catch (ExoPlaybackException e) {
            // Expected exception.
            assertThat(e.getUnexpectedException()).isInstanceOf(IllegalSeekPositionException.class);
        }
        assertThat(onSeekProcessedCalled[0]).isTrue();
    }

    @Test
    public void testSeekDiscontinuity() throws Exception {
        FakeTimeline timeline = new FakeTimeline(1);
        ActionSchedule actionSchedule = new ActionSchedule.Builder("testSeekDiscontinuity").seek(10).build();
        ExoPlayerTestRunner testRunner = new ExoPlayerTestRunner.Builder().setTimeline(timeline).setActionSchedule(actionSchedule).build(context).start().blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
        testRunner.assertPositionDiscontinuityReasonsEqual(DISCONTINUITY_REASON_SEEK);
    }

    @Test
    public void testSeekDiscontinuityWithAdjustment() throws Exception {
        FakeTimeline timeline = new FakeTimeline(1);
        FakeMediaSource mediaSource = new FakeMediaSource(timeline, null, Builder.VIDEO_FORMAT) {
            @Override
            protected FakeMediaPeriod createFakeMediaPeriod(MediaPeriodId id, TrackGroupArray trackGroupArray, Allocator allocator, EventDispatcher eventDispatcher, @Nullable
            TransferListener transferListener) {
                FakeMediaPeriod mediaPeriod = new FakeMediaPeriod(trackGroupArray, eventDispatcher);
                mediaPeriod.setSeekToUsOffset(10);
                return mediaPeriod;
            }
        };
        ActionSchedule actionSchedule = new ActionSchedule.Builder("testSeekDiscontinuityAdjust").pause().waitForPlaybackState(STATE_READY).seek(10).play().build();
        ExoPlayerTestRunner testRunner = new ExoPlayerTestRunner.Builder().setMediaSource(mediaSource).setActionSchedule(actionSchedule).build(context).start().blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
        testRunner.assertPositionDiscontinuityReasonsEqual(DISCONTINUITY_REASON_SEEK, DISCONTINUITY_REASON_SEEK_ADJUSTMENT);
    }

    @Test
    public void testInternalDiscontinuityAtNewPosition() throws Exception {
        FakeTimeline timeline = new FakeTimeline(1);
        FakeMediaSource mediaSource = new FakeMediaSource(timeline, null, Builder.VIDEO_FORMAT) {
            @Override
            protected FakeMediaPeriod createFakeMediaPeriod(MediaPeriodId id, TrackGroupArray trackGroupArray, Allocator allocator, EventDispatcher eventDispatcher, @Nullable
            TransferListener transferListener) {
                FakeMediaPeriod mediaPeriod = new FakeMediaPeriod(trackGroupArray, eventDispatcher);
                mediaPeriod.setDiscontinuityPositionUs(10);
                return mediaPeriod;
            }
        };
        ExoPlayerTestRunner testRunner = new ExoPlayerTestRunner.Builder().setMediaSource(mediaSource).build(context).start().blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
        testRunner.assertPositionDiscontinuityReasonsEqual(DISCONTINUITY_REASON_INTERNAL);
    }

    @Test
    public void testInternalDiscontinuityAtInitialPosition() throws Exception {
        FakeTimeline timeline = new FakeTimeline(1);
        FakeMediaSource mediaSource = new FakeMediaSource(timeline, null, Builder.VIDEO_FORMAT) {
            @Override
            protected FakeMediaPeriod createFakeMediaPeriod(MediaPeriodId id, TrackGroupArray trackGroupArray, Allocator allocator, EventDispatcher eventDispatcher, @Nullable
            TransferListener transferListener) {
                FakeMediaPeriod mediaPeriod = new FakeMediaPeriod(trackGroupArray, eventDispatcher);
                mediaPeriod.setDiscontinuityPositionUs(0);
                return mediaPeriod;
            }
        };
        ExoPlayerTestRunner testRunner = new ExoPlayerTestRunner.Builder().setMediaSource(mediaSource).build(context).start().blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
        // If the position is unchanged we do not expect the discontinuity to be reported externally.
        testRunner.assertNoPositionDiscontinuities();
    }

    @Test
    public void testAllActivatedTrackSelectionAreReleasedForSinglePeriod() throws Exception {
        Timeline timeline = /* windowCount= */
        new FakeTimeline(1);
        MediaSource mediaSource = new FakeMediaSource(timeline, null, Builder.VIDEO_FORMAT, Builder.AUDIO_FORMAT);
        FakeRenderer videoRenderer = new FakeRenderer(Builder.VIDEO_FORMAT);
        FakeRenderer audioRenderer = new FakeRenderer(Builder.AUDIO_FORMAT);
        FakeTrackSelector trackSelector = new FakeTrackSelector();
        new Builder().setMediaSource(mediaSource).setRenderers(videoRenderer, audioRenderer).setTrackSelector(trackSelector).build(context).start().blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
        List<FakeTrackSelection> createdTrackSelections = trackSelector.getAllTrackSelections();
        int numSelectionsEnabled = 0;
        // Assert that all tracks selection are disabled at the end of the playback.
        for (FakeTrackSelection trackSelection : createdTrackSelections) {
            assertThat(trackSelection.isEnabled).isFalse();
            numSelectionsEnabled += trackSelection.enableCount;
        }
        // There are 2 renderers, and track selections are made once (1 period).
        assertThat(createdTrackSelections).hasSize(2);
        assertThat(numSelectionsEnabled).isEqualTo(2);
    }

    @Test
    public void testAllActivatedTrackSelectionAreReleasedForMultiPeriods() throws Exception {
        Timeline timeline = /* windowCount= */
        new FakeTimeline(2);
        MediaSource mediaSource = new FakeMediaSource(timeline, null, Builder.VIDEO_FORMAT, Builder.AUDIO_FORMAT);
        FakeRenderer videoRenderer = new FakeRenderer(Builder.VIDEO_FORMAT);
        FakeRenderer audioRenderer = new FakeRenderer(Builder.AUDIO_FORMAT);
        FakeTrackSelector trackSelector = new FakeTrackSelector();
        new Builder().setMediaSource(mediaSource).setRenderers(videoRenderer, audioRenderer).setTrackSelector(trackSelector).build(context).start().blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
        List<FakeTrackSelection> createdTrackSelections = trackSelector.getAllTrackSelections();
        int numSelectionsEnabled = 0;
        // Assert that all tracks selection are disabled at the end of the playback.
        for (FakeTrackSelection trackSelection : createdTrackSelections) {
            assertThat(trackSelection.isEnabled).isFalse();
            numSelectionsEnabled += trackSelection.enableCount;
        }
        // There are 2 renderers, and track selections are made twice (2 periods).
        assertThat(createdTrackSelections).hasSize(4);
        assertThat(numSelectionsEnabled).isEqualTo(4);
    }

    @Test
    public void testAllActivatedTrackSelectionAreReleasedWhenTrackSelectionsAreRemade() throws Exception {
        Timeline timeline = /* windowCount= */
        new FakeTimeline(1);
        MediaSource mediaSource = new FakeMediaSource(timeline, null, Builder.VIDEO_FORMAT, Builder.AUDIO_FORMAT);
        FakeRenderer videoRenderer = new FakeRenderer(Builder.VIDEO_FORMAT);
        FakeRenderer audioRenderer = new FakeRenderer(Builder.AUDIO_FORMAT);
        final FakeTrackSelector trackSelector = new FakeTrackSelector();
        ActionSchedule disableTrackAction = new ActionSchedule.Builder("testChangeTrackSelection").pause().waitForPlaybackState(STATE_READY).disableRenderer(0).play().build();
        new Builder().setMediaSource(mediaSource).setRenderers(videoRenderer, audioRenderer).setTrackSelector(trackSelector).setActionSchedule(disableTrackAction).build(context).start().blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
        List<FakeTrackSelection> createdTrackSelections = trackSelector.getAllTrackSelections();
        int numSelectionsEnabled = 0;
        // Assert that all tracks selection are disabled at the end of the playback.
        for (FakeTrackSelection trackSelection : createdTrackSelections) {
            assertThat(trackSelection.isEnabled).isFalse();
            numSelectionsEnabled += trackSelection.enableCount;
        }
        // There are 2 renderers, and track selections are made twice. The second time one renderer is
        // disabled, so only one out of the two track selections is enabled.
        assertThat(createdTrackSelections).hasSize(4);
        assertThat(numSelectionsEnabled).isEqualTo(3);
    }

    @Test
    public void testAllActivatedTrackSelectionAreReleasedWhenTrackSelectionsAreReused() throws Exception {
        Timeline timeline = /* windowCount= */
        new FakeTimeline(1);
        MediaSource mediaSource = new FakeMediaSource(timeline, null, Builder.VIDEO_FORMAT, Builder.AUDIO_FORMAT);
        FakeRenderer videoRenderer = new FakeRenderer(Builder.VIDEO_FORMAT);
        FakeRenderer audioRenderer = new FakeRenderer(Builder.AUDIO_FORMAT);
        final FakeTrackSelector trackSelector = /* reuse track selection */
        new FakeTrackSelector(true);
        ActionSchedule disableTrackAction = new ActionSchedule.Builder("testReuseTrackSelection").pause().waitForPlaybackState(STATE_READY).disableRenderer(0).play().build();
        new Builder().setMediaSource(mediaSource).setRenderers(videoRenderer, audioRenderer).setTrackSelector(trackSelector).setActionSchedule(disableTrackAction).build(context).start().blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
        List<FakeTrackSelection> createdTrackSelections = trackSelector.getAllTrackSelections();
        int numSelectionsEnabled = 0;
        // Assert that all tracks selection are disabled at the end of the playback.
        for (FakeTrackSelection trackSelection : createdTrackSelections) {
            assertThat(trackSelection.isEnabled).isFalse();
            numSelectionsEnabled += trackSelection.enableCount;
        }
        // There are 2 renderers, and track selections are made twice. The second time one renderer is
        // disabled, and the selector re-uses the previous selection for the enabled renderer. So we
        // expect two track selections, one of which will have been enabled twice.
        assertThat(createdTrackSelections).hasSize(2);
        assertThat(numSelectionsEnabled).isEqualTo(3);
    }

    @Test
    public void testDynamicTimelineChangeReason() throws Exception {
        Timeline timeline1 = new FakeTimeline(new TimelineWindowDefinition(false, false, 100000));
        final Timeline timeline2 = new FakeTimeline(new TimelineWindowDefinition(false, false, 20000));
        final FakeMediaSource mediaSource = new FakeMediaSource(timeline1, null, Builder.VIDEO_FORMAT);
        ActionSchedule actionSchedule = new ActionSchedule.Builder("testDynamicTimelineChangeReason").pause().waitForTimelineChanged(timeline1).executeRunnable(() -> mediaSource.setNewSourceInfo(timeline2, null)).waitForTimelineChanged(timeline2).play().build();
        ExoPlayerTestRunner testRunner = new ExoPlayerTestRunner.Builder().setMediaSource(mediaSource).setActionSchedule(actionSchedule).build(context).start().blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
        testRunner.assertTimelinesEqual(timeline1, timeline2);
        testRunner.assertTimelineChangeReasonsEqual(TIMELINE_CHANGE_REASON_PREPARED, TIMELINE_CHANGE_REASON_DYNAMIC);
    }

    @Test
    public void testRepreparationWithPositionResetAndShufflingUsesFirstPeriod() throws Exception {
        Timeline fakeTimeline = new FakeTimeline(/* isSeekable= */
        /* isDynamic= */
        /* durationUs= */
        new TimelineWindowDefinition(true, false, 100000));
        ConcatenatingMediaSource firstMediaSource = /* isAtomic= */
        new ConcatenatingMediaSource(false, /* length= */
        new FakeShuffleOrder(2), new FakeMediaSource(fakeTimeline, null, Builder.VIDEO_FORMAT), new FakeMediaSource(fakeTimeline, null, Builder.VIDEO_FORMAT));
        ConcatenatingMediaSource secondMediaSource = /* isAtomic= */
        new ConcatenatingMediaSource(false, /* length= */
        new FakeShuffleOrder(2), new FakeMediaSource(fakeTimeline, null, Builder.VIDEO_FORMAT), new FakeMediaSource(fakeTimeline, null, Builder.VIDEO_FORMAT));
        ActionSchedule actionSchedule = // Reprepare with second media source (keeping state, but with position reset).
        // Plays period 1 and 0 because of the reversed fake shuffle order.
        /* resetPosition= */
        /* resetState= */
        // Wait for first preparation and enable shuffling. Plays period 0.
        new ActionSchedule.Builder("testRepreparationWithShuffle").pause().waitForPlaybackState(STATE_READY).setShuffleModeEnabled(true).prepareSource(secondMediaSource, true, false).play().build();
        ExoPlayerTestRunner testRunner = new ExoPlayerTestRunner.Builder().setMediaSource(firstMediaSource).setActionSchedule(actionSchedule).build(context).start().blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
        testRunner.assertPlayedPeriodIndices(0, 1, 0);
    }

    @Test
    public void testSetPlaybackParametersBeforePreparationCompletesSucceeds() throws Exception {
        // Test that no exception is thrown when playback parameters are updated between creating a
        // period and preparation of the period completing.
        final CountDownLatch createPeriodCalledCountDownLatch = new CountDownLatch(1);
        final FakeMediaPeriod[] fakeMediaPeriodHolder = new FakeMediaPeriod[1];
        MediaSource mediaSource = new FakeMediaSource(new FakeTimeline(1), null, Builder.VIDEO_FORMAT) {
            @Override
            protected FakeMediaPeriod createFakeMediaPeriod(MediaPeriodId id, TrackGroupArray trackGroupArray, Allocator allocator, EventDispatcher eventDispatcher, @Nullable
            TransferListener transferListener) {
                // Defer completing preparation of the period until playback parameters have been set.
                fakeMediaPeriodHolder[0] = /* deferOnPrepared= */
                new FakeMediaPeriod(trackGroupArray, eventDispatcher, true);
                createPeriodCalledCountDownLatch.countDown();
                return fakeMediaPeriodHolder[0];
            }
        };
        ActionSchedule actionSchedule = // Complete preparation of the fake media period.
        // Set playback parameters (while the fake media period is not yet prepared).
        // Block until createPeriod has been called on the fake media source.
        new ActionSchedule.Builder("testSetPlaybackParametersBeforePreparationCompletesSucceeds").waitForPlaybackState(STATE_BUFFERING).executeRunnable(() -> {
            try {
                createPeriodCalledCountDownLatch.await();
            } catch ( e) {
                throw new <e>IllegalStateException();
            }
        }).setPlaybackParameters(/* speed= */
        /* pitch= */
        new PlaybackParameters(2.0F, 2.0F)).executeRunnable(() -> fakeMediaPeriodHolder[0].setPreparationComplete()).build();
        new ExoPlayerTestRunner.Builder().setMediaSource(mediaSource).setActionSchedule(actionSchedule).build(context).start().blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
    }

    @Test
    public void testStopDoesNotResetPosition() throws Exception {
        Timeline timeline = /* windowCount= */
        new FakeTimeline(1);
        final long[] positionHolder = new long[1];
        ActionSchedule actionSchedule = /* windowIndex= */
        /* positionMs= */
        new ActionSchedule.Builder("testStopDoesNotResetPosition").pause().waitForPlaybackState(STATE_READY).playUntilPosition(0, 50).stop().executeRunnable(new PlayerRunnable() {
            @Override
            public void run(SimpleExoPlayer player) {
                positionHolder[0] = player.getCurrentPosition();
            }
        }).build();
        ExoPlayerTestRunner testRunner = new ExoPlayerTestRunner.Builder().setTimeline(timeline).setActionSchedule(actionSchedule).build(context).start().blockUntilActionScheduleFinished(ExoPlayerTest.TIMEOUT_MS).blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
        testRunner.assertTimelinesEqual(timeline);
        testRunner.assertTimelineChangeReasonsEqual(TIMELINE_CHANGE_REASON_PREPARED);
        testRunner.assertNoPositionDiscontinuities();
        assertThat(positionHolder[0]).isAtLeast(50L);
    }

    @Test
    public void testStopWithoutResetDoesNotResetPosition() throws Exception {
        Timeline timeline = /* windowCount= */
        new FakeTimeline(1);
        final long[] positionHolder = new long[1];
        ActionSchedule actionSchedule = /* reset= */
        /* windowIndex= */
        /* positionMs= */
        new ActionSchedule.Builder("testStopWithoutResetDoesNotReset").pause().waitForPlaybackState(STATE_READY).playUntilPosition(0, 50).stop(false).executeRunnable(new PlayerRunnable() {
            @Override
            public void run(SimpleExoPlayer player) {
                positionHolder[0] = player.getCurrentPosition();
            }
        }).build();
        ExoPlayerTestRunner testRunner = new ExoPlayerTestRunner.Builder().setTimeline(timeline).setActionSchedule(actionSchedule).build(context).start().blockUntilActionScheduleFinished(ExoPlayerTest.TIMEOUT_MS).blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
        testRunner.assertTimelinesEqual(timeline);
        testRunner.assertTimelineChangeReasonsEqual(TIMELINE_CHANGE_REASON_PREPARED);
        testRunner.assertNoPositionDiscontinuities();
        assertThat(positionHolder[0]).isAtLeast(50L);
    }

    @Test
    public void testStopWithResetDoesResetPosition() throws Exception {
        Timeline timeline = /* windowCount= */
        new FakeTimeline(1);
        final long[] positionHolder = new long[1];
        ActionSchedule actionSchedule = /* reset= */
        /* windowIndex= */
        /* positionMs= */
        new ActionSchedule.Builder("testStopWithResetDoesReset").pause().waitForPlaybackState(STATE_READY).playUntilPosition(0, 50).stop(true).executeRunnable(new PlayerRunnable() {
            @Override
            public void run(SimpleExoPlayer player) {
                positionHolder[0] = player.getCurrentPosition();
            }
        }).build();
        ExoPlayerTestRunner testRunner = new ExoPlayerTestRunner.Builder().setTimeline(timeline).setActionSchedule(actionSchedule).build(context).start().blockUntilActionScheduleFinished(ExoPlayerTest.TIMEOUT_MS).blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
        testRunner.assertTimelinesEqual(timeline, EMPTY);
        testRunner.assertTimelineChangeReasonsEqual(TIMELINE_CHANGE_REASON_PREPARED, TIMELINE_CHANGE_REASON_RESET);
        testRunner.assertNoPositionDiscontinuities();
        assertThat(positionHolder[0]).isEqualTo(0);
    }

    @Test
    public void testStopWithoutResetReleasesMediaSource() throws Exception {
        Timeline timeline = /* windowCount= */
        new FakeTimeline(1);
        final FakeMediaSource mediaSource = /* manifest= */
        new FakeMediaSource(timeline, null, Builder.VIDEO_FORMAT);
        ActionSchedule actionSchedule = /* reset= */
        new ActionSchedule.Builder("testStopReleasesMediaSource").waitForPlaybackState(STATE_READY).stop(false).build();
        ExoPlayerTestRunner testRunner = new ExoPlayerTestRunner.Builder().setTimeline(timeline).setActionSchedule(actionSchedule).build(context).start().blockUntilActionScheduleFinished(ExoPlayerTest.TIMEOUT_MS);
        mediaSource.assertReleased();
        testRunner.blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
    }

    @Test
    public void testStopWithResetReleasesMediaSource() throws Exception {
        Timeline timeline = /* windowCount= */
        new FakeTimeline(1);
        final FakeMediaSource mediaSource = /* manifest= */
        new FakeMediaSource(timeline, null, Builder.VIDEO_FORMAT);
        ActionSchedule actionSchedule = /* reset= */
        new ActionSchedule.Builder("testStopReleasesMediaSource").waitForPlaybackState(STATE_READY).stop(true).build();
        ExoPlayerTestRunner testRunner = new ExoPlayerTestRunner.Builder().setTimeline(timeline).setActionSchedule(actionSchedule).build(context).start().blockUntilActionScheduleFinished(ExoPlayerTest.TIMEOUT_MS);
        mediaSource.assertReleased();
        testRunner.blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
    }

    @Test
    public void testRepreparationDoesNotResetAfterStopWithReset() throws Exception {
        Timeline timeline = /* windowCount= */
        new FakeTimeline(1);
        MediaSource secondSource = new FakeMediaSource(timeline, null, Builder.VIDEO_FORMAT);
        ActionSchedule actionSchedule = /* reset= */
        new ActionSchedule.Builder("testRepreparationAfterStop").waitForPlaybackState(STATE_READY).stop(true).waitForPlaybackState(STATE_IDLE).prepareSource(secondSource).build();
        ExoPlayerTestRunner testRunner = new ExoPlayerTestRunner.Builder().setTimeline(timeline).setActionSchedule(actionSchedule).setExpectedPlayerEndedCount(2).build(context).start().blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
        testRunner.assertTimelinesEqual(timeline, EMPTY, timeline);
        testRunner.assertTimelineChangeReasonsEqual(TIMELINE_CHANGE_REASON_PREPARED, TIMELINE_CHANGE_REASON_RESET, TIMELINE_CHANGE_REASON_PREPARED);
        testRunner.assertNoPositionDiscontinuities();
    }

    @Test
    public void testSeekBeforeRepreparationPossibleAfterStopWithReset() throws Exception {
        Timeline timeline = /* windowCount= */
        new FakeTimeline(1);
        Timeline secondTimeline = /* windowCount= */
        new FakeTimeline(2);
        MediaSource secondSource = new FakeMediaSource(secondTimeline, null, Builder.VIDEO_FORMAT);
        ActionSchedule actionSchedule = /* resetPosition= */
        /* resetState= */
        // If we were still using the first timeline, this would throw.
        /* windowIndex= */
        /* positionMs= */
        /* reset= */
        new ActionSchedule.Builder("testSeekAfterStopWithReset").waitForPlaybackState(STATE_READY).stop(true).waitForPlaybackState(STATE_IDLE).seek(1, 0).prepareSource(secondSource, false, true).build();
        ExoPlayerTestRunner testRunner = new ExoPlayerTestRunner.Builder().setTimeline(timeline).setActionSchedule(actionSchedule).setExpectedPlayerEndedCount(2).build(context).start().blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
        testRunner.assertTimelinesEqual(timeline, EMPTY, secondTimeline);
        testRunner.assertTimelineChangeReasonsEqual(TIMELINE_CHANGE_REASON_PREPARED, TIMELINE_CHANGE_REASON_RESET, TIMELINE_CHANGE_REASON_PREPARED);
        testRunner.assertPositionDiscontinuityReasonsEqual(DISCONTINUITY_REASON_SEEK);
        testRunner.assertPlayedPeriodIndices(0, 1);
    }

    @Test
    public void testStopDuringPreparationOverwritesPreparation() throws Exception {
        Timeline timeline = /* windowCount= */
        new FakeTimeline(1);
        ActionSchedule actionSchedule = new ActionSchedule.Builder("testStopOverwritesPrepare").waitForPlaybackState(STATE_BUFFERING).seek(0).stop(true).waitForSeekProcessed().build();
        ExoPlayerTestRunner testRunner = new ExoPlayerTestRunner.Builder().setTimeline(timeline).setActionSchedule(actionSchedule).build(context).start().blockUntilActionScheduleFinished(ExoPlayerTest.TIMEOUT_MS).blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
        testRunner.assertTimelinesEqual(EMPTY);
        testRunner.assertTimelineChangeReasonsEqual(TIMELINE_CHANGE_REASON_PREPARED);
        testRunner.assertPositionDiscontinuityReasonsEqual(DISCONTINUITY_REASON_SEEK);
    }

    @Test
    public void testStopAndSeekAfterStopDoesNotResetTimeline() throws Exception {
        // Combining additional stop and seek after initial stop in one test to get the seek processed
        // callback which ensures that all operations have been processed by the player.
        Timeline timeline = /* windowCount= */
        new FakeTimeline(1);
        ActionSchedule actionSchedule = new ActionSchedule.Builder("testStopTwice").waitForPlaybackState(STATE_READY).stop(false).stop(false).seek(0).waitForSeekProcessed().build();
        ExoPlayerTestRunner testRunner = new ExoPlayerTestRunner.Builder().setTimeline(timeline).setActionSchedule(actionSchedule).build(context).start().blockUntilActionScheduleFinished(ExoPlayerTest.TIMEOUT_MS).blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
        testRunner.assertTimelinesEqual(timeline);
        testRunner.assertTimelineChangeReasonsEqual(TIMELINE_CHANGE_REASON_PREPARED);
        testRunner.assertPositionDiscontinuityReasonsEqual(DISCONTINUITY_REASON_SEEK);
    }

    @Test
    public void testReprepareAfterPlaybackError() throws Exception {
        Timeline timeline = /* windowCount= */
        new FakeTimeline(1);
        ActionSchedule actionSchedule = /* resetPosition= */
        /* resetState= */
        new ActionSchedule.Builder("testReprepareAfterPlaybackError").waitForPlaybackState(STATE_READY).throwPlaybackException(ExoPlaybackException.createForSource(new IOException())).waitForPlaybackState(STATE_IDLE).prepareSource(/* manifest= */
        new FakeMediaSource(timeline, null), true, false).waitForPlaybackState(STATE_READY).build();
        ExoPlayerTestRunner testRunner = new ExoPlayerTestRunner.Builder().setTimeline(timeline).setActionSchedule(actionSchedule).build(context);
        try {
            testRunner.start().blockUntilActionScheduleFinished(ExoPlayerTest.TIMEOUT_MS).blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
            Assert.fail();
        } catch (ExoPlaybackException e) {
            // Expected exception.
        }
        testRunner.assertTimelinesEqual(timeline, timeline);
        testRunner.assertTimelineChangeReasonsEqual(TIMELINE_CHANGE_REASON_PREPARED, TIMELINE_CHANGE_REASON_PREPARED);
    }

    @Test
    public void testSeekAndReprepareAfterPlaybackError() throws Exception {
        Timeline timeline = /* windowCount= */
        new FakeTimeline(1);
        final long[] positionHolder = new long[2];
        ActionSchedule actionSchedule = /* resetPosition= */
        /* resetState= */
        /* positionMs= */
        new ActionSchedule.Builder("testReprepareAfterPlaybackError").pause().waitForPlaybackState(STATE_READY).throwPlaybackException(ExoPlaybackException.createForSource(new IOException())).waitForPlaybackState(STATE_IDLE).seek(50).waitForSeekProcessed().executeRunnable(new PlayerRunnable() {
            @Override
            public void run(SimpleExoPlayer player) {
                positionHolder[0] = player.getCurrentPosition();
            }
        }).prepareSource(/* manifest= */
        new FakeMediaSource(timeline, null), false, false).waitForPlaybackState(STATE_READY).executeRunnable(new PlayerRunnable() {
            @Override
            public void run(SimpleExoPlayer player) {
                positionHolder[1] = player.getCurrentPosition();
            }
        }).play().build();
        ExoPlayerTestRunner testRunner = new ExoPlayerTestRunner.Builder().setTimeline(timeline).setActionSchedule(actionSchedule).build(context);
        try {
            testRunner.start().blockUntilActionScheduleFinished(ExoPlayerTest.TIMEOUT_MS).blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
            Assert.fail();
        } catch (ExoPlaybackException e) {
            // Expected exception.
        }
        testRunner.assertTimelinesEqual(timeline, timeline);
        testRunner.assertTimelineChangeReasonsEqual(TIMELINE_CHANGE_REASON_PREPARED, TIMELINE_CHANGE_REASON_PREPARED);
        testRunner.assertPositionDiscontinuityReasonsEqual(DISCONTINUITY_REASON_SEEK);
        assertThat(positionHolder[0]).isEqualTo(50);
        assertThat(positionHolder[1]).isEqualTo(50);
    }

    @Test
    public void testPlaybackErrorDuringSourceInfoRefreshStillUpdatesTimeline() throws Exception {
        final Timeline timeline = /* windowCount= */
        new FakeTimeline(1);
        final FakeMediaSource mediaSource = /* timeline= */
        /* manifest= */
        new FakeMediaSource(null, null);
        ActionSchedule actionSchedule = // Cause an internal exception by seeking to an invalid position while the media source
        // is still being prepared. The error will be thrown while the player handles the new
        // source info.
        /* windowIndex= */
        /* positionMs= */
        new ActionSchedule.Builder("testPlaybackErrorDuringSourceInfoRefreshStillUpdatesTimeline").waitForPlaybackState(STATE_BUFFERING).seek(100, 0).executeRunnable(() -> /* newManifest= */
        mediaSource.setNewSourceInfo(timeline, null)).waitForPlaybackState(STATE_IDLE).build();
        ExoPlayerTestRunner testRunner = new ExoPlayerTestRunner.Builder().setMediaSource(mediaSource).setActionSchedule(actionSchedule).build(context);
        try {
            testRunner.start().blockUntilActionScheduleFinished(ExoPlayerTest.TIMEOUT_MS).blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
            Assert.fail();
        } catch (ExoPlaybackException e) {
            // Expected exception.
            assertThat(e.getUnexpectedException()).isInstanceOf(IllegalSeekPositionException.class);
        }
        testRunner.assertTimelinesEqual(timeline);
        testRunner.assertTimelineChangeReasonsEqual(TIMELINE_CHANGE_REASON_PREPARED);
    }

    @Test
    public void testPlaybackErrorDuringSourceInfoRefreshWithShuffleModeEnabledUsesCorrectFirstPeriod() throws Exception {
        FakeMediaSource mediaSource = /* manifest= */
        new FakeMediaSource(/* windowCount= */
        new FakeTimeline(1), null);
        ConcatenatingMediaSource concatenatingMediaSource = /* isAtomic= */
        new ConcatenatingMediaSource(false, new FakeShuffleOrder(0), mediaSource, mediaSource);
        AtomicInteger windowIndexAfterError = new AtomicInteger();
        ActionSchedule actionSchedule = // Cause an internal exception by seeking to an invalid position while the media source
        // is still being prepared. The error will be thrown while the player handles the new
        // source info.
        /* windowIndex= */
        /* positionMs= */
        new ActionSchedule.Builder("testPlaybackErrorDuringSourceInfoRefreshUsesCorrectFirstPeriod").setShuffleModeEnabled(true).waitForPlaybackState(STATE_BUFFERING).seek(100, 0).waitForPlaybackState(STATE_IDLE).executeRunnable(new PlayerRunnable() {
            @Override
            public void run(SimpleExoPlayer player) {
                windowIndexAfterError.set(player.getCurrentWindowIndex());
            }
        }).build();
        ExoPlayerTestRunner testRunner = new ExoPlayerTestRunner.Builder().setMediaSource(concatenatingMediaSource).setActionSchedule(actionSchedule).build(context);
        try {
            testRunner.start().blockUntilActionScheduleFinished(ExoPlayerTest.TIMEOUT_MS).blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
            Assert.fail();
        } catch (ExoPlaybackException e) {
            // Expected exception.
            assertThat(e.getUnexpectedException()).isInstanceOf(IllegalSeekPositionException.class);
        }
        assertThat(windowIndexAfterError.get()).isEqualTo(1);
    }

    @Test
    public void testRestartAfterEmptyTimelineWithShuffleModeEnabledUsesCorrectFirstPeriod() throws Exception {
        Timeline timeline = /* windowCount= */
        new FakeTimeline(1);
        FakeMediaSource mediaSource = /* manifest= */
        new FakeMediaSource(timeline, null);
        ConcatenatingMediaSource concatenatingMediaSource = /* isAtomic= */
        new ConcatenatingMediaSource(false, new FakeShuffleOrder(0));
        AtomicInteger windowIndexAfterAddingSources = new AtomicInteger();
        ActionSchedule actionSchedule = // Add two sources at once such that the default start position in the shuffled order
        // will be the second source.
        // Preparing with an empty media source will transition to ended state.
        new ActionSchedule.Builder("testRestartAfterEmptyTimelineUsesCorrectFirstPeriod").setShuffleModeEnabled(true).waitForPlaybackState(STATE_ENDED).executeRunnable(() -> concatenatingMediaSource.addMediaSources(Arrays.asList(mediaSource, mediaSource))).waitForTimelineChanged().executeRunnable(new PlayerRunnable() {
            @Override
            public void run(SimpleExoPlayer player) {
                windowIndexAfterAddingSources.set(player.getCurrentWindowIndex());
            }
        }).build();
        new ExoPlayerTestRunner.Builder().setMediaSource(concatenatingMediaSource).setActionSchedule(actionSchedule).build(context).start().blockUntilActionScheduleFinished(ExoPlayerTest.TIMEOUT_MS).blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
        assertThat(windowIndexAfterAddingSources.get()).isEqualTo(1);
    }

    @Test
    public void testPlaybackErrorAndReprepareDoesNotResetPosition() throws Exception {
        final Timeline timeline = /* windowCount= */
        new FakeTimeline(2);
        final long[] positionHolder = new long[3];
        final int[] windowIndexHolder = new int[3];
        final FakeMediaSource secondMediaSource = /* timeline= */
        /* manifest= */
        new FakeMediaSource(null, null);
        ActionSchedule actionSchedule = /* resetPosition= */
        /* resetState= */
        /* windowIndex= */
        /* positionMs= */
        new ActionSchedule.Builder("testPlaybackErrorDoesNotResetPosition").pause().waitForPlaybackState(STATE_READY).playUntilPosition(1, 500).throwPlaybackException(ExoPlaybackException.createForSource(new IOException())).waitForPlaybackState(STATE_IDLE).executeRunnable(new PlayerRunnable() {
            @Override
            public void run(SimpleExoPlayer player) {
                // Position while in error state
                positionHolder[0] = player.getCurrentPosition();
                windowIndexHolder[0] = player.getCurrentWindowIndex();
            }
        }).prepareSource(secondMediaSource, false, false).waitForPlaybackState(STATE_BUFFERING).executeRunnable(new PlayerRunnable() {
            @Override
            public void run(SimpleExoPlayer player) {
                // Position while repreparing.
                positionHolder[1] = player.getCurrentPosition();
                windowIndexHolder[1] = player.getCurrentWindowIndex();
                /* newManifest= */
                secondMediaSource.setNewSourceInfo(timeline, null);
            }
        }).waitForPlaybackState(STATE_READY).executeRunnable(new PlayerRunnable() {
            @Override
            public void run(SimpleExoPlayer player) {
                // Position after repreparation finished.
                positionHolder[2] = player.getCurrentPosition();
                windowIndexHolder[2] = player.getCurrentWindowIndex();
            }
        }).play().build();
        ExoPlayerTestRunner testRunner = new ExoPlayerTestRunner.Builder().setTimeline(timeline).setActionSchedule(actionSchedule).build(context);
        try {
            testRunner.start().blockUntilActionScheduleFinished(ExoPlayerTest.TIMEOUT_MS).blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
            Assert.fail();
        } catch (ExoPlaybackException e) {
            // Expected exception.
        }
        assertThat(positionHolder[0]).isAtLeast(500L);
        assertThat(positionHolder[1]).isEqualTo(positionHolder[0]);
        assertThat(positionHolder[2]).isEqualTo(positionHolder[0]);
        assertThat(windowIndexHolder[0]).isEqualTo(1);
        assertThat(windowIndexHolder[1]).isEqualTo(1);
        assertThat(windowIndexHolder[2]).isEqualTo(1);
    }

    @Test
    public void testPlaybackErrorTwiceStillKeepsTimeline() throws Exception {
        final Timeline timeline = /* windowCount= */
        new FakeTimeline(1);
        final FakeMediaSource mediaSource2 = /* timeline= */
        /* manifest= */
        new FakeMediaSource(null, null);
        ActionSchedule actionSchedule = /* resetPosition= */
        /* resetState= */
        new ActionSchedule.Builder("testPlaybackErrorDoesNotResetPosition").pause().waitForPlaybackState(STATE_READY).throwPlaybackException(ExoPlaybackException.createForSource(new IOException())).waitForPlaybackState(STATE_IDLE).prepareSource(mediaSource2, false, false).waitForPlaybackState(STATE_BUFFERING).throwPlaybackException(ExoPlaybackException.createForSource(new IOException())).waitForPlaybackState(STATE_IDLE).build();
        ExoPlayerTestRunner testRunner = new ExoPlayerTestRunner.Builder().setTimeline(timeline).setActionSchedule(actionSchedule).build(context);
        try {
            testRunner.start().blockUntilActionScheduleFinished(ExoPlayerTest.TIMEOUT_MS).blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
            Assert.fail();
        } catch (ExoPlaybackException e) {
            // Expected exception.
        }
        testRunner.assertTimelinesEqual(timeline, timeline);
        testRunner.assertTimelineChangeReasonsEqual(TIMELINE_CHANGE_REASON_PREPARED, TIMELINE_CHANGE_REASON_PREPARED);
    }

    @Test
    public void testSendMessagesDuringPreparation() throws Exception {
        Timeline timeline = /* windowCount= */
        new FakeTimeline(1);
        ExoPlayerTest.PositionGrabbingMessageTarget target = new ExoPlayerTest.PositionGrabbingMessageTarget();
        ActionSchedule actionSchedule = /* positionMs= */
        new ActionSchedule.Builder("testSendMessages").pause().waitForPlaybackState(STATE_BUFFERING).sendMessage(target, 50).play().build();
        new Builder().setTimeline(timeline).setActionSchedule(actionSchedule).build(context).start().blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
        assertThat(target.positionMs).isAtLeast(50L);
    }

    @Test
    public void testSendMessagesAfterPreparation() throws Exception {
        Timeline timeline = /* windowCount= */
        new FakeTimeline(1);
        ExoPlayerTest.PositionGrabbingMessageTarget target = new ExoPlayerTest.PositionGrabbingMessageTarget();
        ActionSchedule actionSchedule = /* positionMs= */
        new ActionSchedule.Builder("testSendMessages").pause().waitForTimelineChanged(timeline).sendMessage(target, 50).play().build();
        new Builder().setTimeline(timeline).setActionSchedule(actionSchedule).build(context).start().blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
        assertThat(target.positionMs).isAtLeast(50L);
    }

    @Test
    public void testMultipleSendMessages() throws Exception {
        Timeline timeline = /* windowCount= */
        new FakeTimeline(1);
        ExoPlayerTest.PositionGrabbingMessageTarget target50 = new ExoPlayerTest.PositionGrabbingMessageTarget();
        ExoPlayerTest.PositionGrabbingMessageTarget target80 = new ExoPlayerTest.PositionGrabbingMessageTarget();
        ActionSchedule actionSchedule = /* positionMs= */
        /* positionMs= */
        new ActionSchedule.Builder("testSendMessages").pause().waitForPlaybackState(STATE_BUFFERING).sendMessage(target80, 80).sendMessage(target50, 50).play().build();
        new Builder().setTimeline(timeline).setActionSchedule(actionSchedule).build(context).start().blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
        assertThat(target50.positionMs).isAtLeast(50L);
        assertThat(target80.positionMs).isAtLeast(80L);
        assertThat(target80.positionMs).isAtLeast(target50.positionMs);
    }

    @Test
    public void testMultipleSendMessagesAtSameTime() throws Exception {
        Timeline timeline = /* windowCount= */
        new FakeTimeline(1);
        ExoPlayerTest.PositionGrabbingMessageTarget target1 = new ExoPlayerTest.PositionGrabbingMessageTarget();
        ExoPlayerTest.PositionGrabbingMessageTarget target2 = new ExoPlayerTest.PositionGrabbingMessageTarget();
        ActionSchedule actionSchedule = /* positionMs= */
        /* positionMs= */
        new ActionSchedule.Builder("testSendMessages").pause().waitForPlaybackState(STATE_BUFFERING).sendMessage(target1, 50).sendMessage(target2, 50).play().build();
        new Builder().setTimeline(timeline).setActionSchedule(actionSchedule).build(context).start().blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
        assertThat(target1.positionMs).isAtLeast(50L);
        assertThat(target2.positionMs).isAtLeast(50L);
    }

    @Test
    public void testSendMessagesMultiPeriodResolution() throws Exception {
        Timeline timeline = new FakeTimeline(/* periodCount= */
        /* id= */
        new TimelineWindowDefinition(10, 0));
        ExoPlayerTest.PositionGrabbingMessageTarget target = new ExoPlayerTest.PositionGrabbingMessageTarget();
        ActionSchedule actionSchedule = /* positionMs= */
        new ActionSchedule.Builder("testSendMessages").pause().waitForPlaybackState(STATE_BUFFERING).sendMessage(target, 50).play().build();
        new Builder().setTimeline(timeline).setActionSchedule(actionSchedule).build(context).start().blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
        assertThat(target.positionMs).isAtLeast(50L);
    }

    @Test
    public void testSendMessagesAtStartAndEndOfPeriod() throws Exception {
        Timeline timeline = /* windowCount= */
        new FakeTimeline(2);
        ExoPlayerTest.PositionGrabbingMessageTarget targetStartFirstPeriod = new ExoPlayerTest.PositionGrabbingMessageTarget();
        ExoPlayerTest.PositionGrabbingMessageTarget targetEndMiddlePeriod = new ExoPlayerTest.PositionGrabbingMessageTarget();
        ExoPlayerTest.PositionGrabbingMessageTarget targetStartMiddlePeriod = new ExoPlayerTest.PositionGrabbingMessageTarget();
        ExoPlayerTest.PositionGrabbingMessageTarget targetEndLastPeriod = new ExoPlayerTest.PositionGrabbingMessageTarget();
        long duration1Ms = timeline.getWindow(0, new Window()).getDurationMs();
        long duration2Ms = timeline.getWindow(1, new Window()).getDurationMs();
        ActionSchedule actionSchedule = /* resetPosition= */
        /* resetState= */
        // Add additional prepare at end and wait until it's processed to ensure that
        // messages sent at end of playback are received before test ends.
        /* windowIndex= */
        /* positionMs= */
        /* windowIndex= */
        /* positionMs= */
        /* windowIndex= */
        /* positionMs= */
        /* windowIndex= */
        /* positionMs= */
        new ActionSchedule.Builder("testSendMessages").pause().waitForPlaybackState(STATE_BUFFERING).sendMessage(targetStartFirstPeriod, 0, 0).sendMessage(targetEndMiddlePeriod, 0, duration1Ms).sendMessage(targetStartMiddlePeriod, 1, 0).sendMessage(targetEndLastPeriod, 1, duration2Ms).play().waitForPlaybackState(STATE_ENDED).prepareSource(new FakeMediaSource(timeline, null), false, true).waitForPlaybackState(STATE_BUFFERING).waitForPlaybackState(STATE_ENDED).build();
        new Builder().setTimeline(timeline).setActionSchedule(actionSchedule).build(context).start().blockUntilActionScheduleFinished(ExoPlayerTest.TIMEOUT_MS).blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
        assertThat(targetStartFirstPeriod.windowIndex).isEqualTo(0);
        assertThat(targetStartFirstPeriod.positionMs).isAtLeast(0L);
        assertThat(targetEndMiddlePeriod.windowIndex).isEqualTo(0);
        assertThat(targetEndMiddlePeriod.positionMs).isAtLeast(duration1Ms);
        assertThat(targetStartMiddlePeriod.windowIndex).isEqualTo(1);
        assertThat(targetStartMiddlePeriod.positionMs).isAtLeast(0L);
        assertThat(targetEndLastPeriod.windowIndex).isEqualTo(1);
        assertThat(targetEndLastPeriod.positionMs).isAtLeast(duration2Ms);
    }

    @Test
    public void testSendMessagesSeekOnDeliveryTimeDuringPreparation() throws Exception {
        Timeline timeline = /* windowCount= */
        new FakeTimeline(1);
        ExoPlayerTest.PositionGrabbingMessageTarget target = new ExoPlayerTest.PositionGrabbingMessageTarget();
        ActionSchedule actionSchedule = /* positionMs= */
        /* positionMs= */
        new ActionSchedule.Builder("testSendMessages").waitForPlaybackState(STATE_BUFFERING).sendMessage(target, 50).seek(50).build();
        new Builder().setTimeline(timeline).setActionSchedule(actionSchedule).build(context).start().blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
        assertThat(target.positionMs).isAtLeast(50L);
    }

    @Test
    public void testSendMessagesSeekOnDeliveryTimeAfterPreparation() throws Exception {
        Timeline timeline = /* windowCount= */
        new FakeTimeline(1);
        ExoPlayerTest.PositionGrabbingMessageTarget target = new ExoPlayerTest.PositionGrabbingMessageTarget();
        ActionSchedule actionSchedule = /* positionMs= */
        /* positionMs= */
        new ActionSchedule.Builder("testSendMessages").waitForPlaybackState(STATE_BUFFERING).sendMessage(target, 50).waitForTimelineChanged(timeline).seek(50).build();
        new Builder().setTimeline(timeline).setActionSchedule(actionSchedule).build(context).start().blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
        assertThat(target.positionMs).isAtLeast(50L);
    }

    @Test
    public void testSendMessagesSeekAfterDeliveryTimeDuringPreparation() throws Exception {
        Timeline timeline = /* windowCount= */
        new FakeTimeline(1);
        ExoPlayerTest.PositionGrabbingMessageTarget target = new ExoPlayerTest.PositionGrabbingMessageTarget();
        ActionSchedule actionSchedule = /* positionMs= */
        /* positionMs= */
        new ActionSchedule.Builder("testSendMessages").pause().waitForPlaybackState(STATE_BUFFERING).sendMessage(target, 50).seek(51).play().build();
        new Builder().setTimeline(timeline).setActionSchedule(actionSchedule).build(context).start().blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
        assertThat(target.positionMs).isEqualTo(POSITION_UNSET);
    }

    @Test
    public void testSendMessagesSeekAfterDeliveryTimeAfterPreparation() throws Exception {
        Timeline timeline = /* windowCount= */
        new FakeTimeline(1);
        ExoPlayerTest.PositionGrabbingMessageTarget target = new ExoPlayerTest.PositionGrabbingMessageTarget();
        ActionSchedule actionSchedule = /* positionMs= */
        /* positionMs= */
        new ActionSchedule.Builder("testSendMessages").pause().sendMessage(target, 50).waitForTimelineChanged(timeline).seek(51).play().build();
        new Builder().setTimeline(timeline).setActionSchedule(actionSchedule).build(context).start().blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
        assertThat(target.positionMs).isEqualTo(POSITION_UNSET);
    }

    @Test
    public void testSendMessagesRepeatDoesNotRepost() throws Exception {
        Timeline timeline = /* windowCount= */
        new FakeTimeline(1);
        ExoPlayerTest.PositionGrabbingMessageTarget target = new ExoPlayerTest.PositionGrabbingMessageTarget();
        ActionSchedule actionSchedule = /* positionMs= */
        new ActionSchedule.Builder("testSendMessages").pause().waitForPlaybackState(STATE_BUFFERING).sendMessage(target, 50).setRepeatMode(REPEAT_MODE_ALL).play().waitForPositionDiscontinuity().setRepeatMode(REPEAT_MODE_OFF).build();
        new Builder().setTimeline(timeline).setActionSchedule(actionSchedule).build(context).start().blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
        assertThat(target.messageCount).isEqualTo(1);
        assertThat(target.positionMs).isAtLeast(50L);
    }

    @Test
    public void testSendMessagesRepeatWithoutDeletingDoesRepost() throws Exception {
        Timeline timeline = /* windowCount= */
        new FakeTimeline(1);
        ExoPlayerTest.PositionGrabbingMessageTarget target = new ExoPlayerTest.PositionGrabbingMessageTarget();
        ActionSchedule actionSchedule = /* windowIndex= */
        /* windowIndex= */
        /* positionMs= */
        /* windowIndex= */
        /* positionMs= */
        /* deleteAfterDelivery= */
        new ActionSchedule.Builder("testSendMessages").pause().waitForPlaybackState(STATE_BUFFERING).sendMessage(target, 0, 50, false).setRepeatMode(REPEAT_MODE_ALL).playUntilPosition(0, 1).playUntilStartOfWindow(0).setRepeatMode(REPEAT_MODE_OFF).play().build();
        new Builder().setTimeline(timeline).setActionSchedule(actionSchedule).build(context).start().blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
        assertThat(target.messageCount).isEqualTo(2);
        assertThat(target.positionMs).isAtLeast(50L);
    }

    @Test
    public void testSendMessagesMoveCurrentWindowIndex() throws Exception {
        Timeline timeline = new FakeTimeline(/* periodCount= */
        /* id= */
        new TimelineWindowDefinition(1, 0));
        final Timeline secondTimeline = new FakeTimeline(/* periodCount= */
        /* id= */
        new TimelineWindowDefinition(1, 1), /* periodCount= */
        /* id= */
        new TimelineWindowDefinition(1, 0));
        final FakeMediaSource mediaSource = new FakeMediaSource(timeline, null, Builder.VIDEO_FORMAT);
        ExoPlayerTest.PositionGrabbingMessageTarget target = new ExoPlayerTest.PositionGrabbingMessageTarget();
        ActionSchedule actionSchedule = /* positionMs= */
        new ActionSchedule.Builder("testSendMessages").pause().waitForTimelineChanged(timeline).sendMessage(target, 50).executeRunnable(() -> mediaSource.setNewSourceInfo(secondTimeline, null)).waitForTimelineChanged(secondTimeline).play().build();
        new Builder().setMediaSource(mediaSource).setActionSchedule(actionSchedule).build(context).start().blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
        assertThat(target.positionMs).isAtLeast(50L);
        assertThat(target.windowIndex).isEqualTo(1);
    }

    @Test
    public void testSendMessagesMultiWindowDuringPreparation() throws Exception {
        Timeline timeline = /* windowCount= */
        new FakeTimeline(3);
        ExoPlayerTest.PositionGrabbingMessageTarget target = new ExoPlayerTest.PositionGrabbingMessageTarget();
        ActionSchedule actionSchedule = /* windowIndex = */
        /* positionMs= */
        new ActionSchedule.Builder("testSendMessages").pause().waitForPlaybackState(STATE_BUFFERING).sendMessage(target, 2, 50).play().build();
        new Builder().setTimeline(timeline).setActionSchedule(actionSchedule).build(context).start().blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
        assertThat(target.windowIndex).isEqualTo(2);
        assertThat(target.positionMs).isAtLeast(50L);
    }

    @Test
    public void testSendMessagesMultiWindowAfterPreparation() throws Exception {
        Timeline timeline = /* windowCount= */
        new FakeTimeline(3);
        ExoPlayerTest.PositionGrabbingMessageTarget target = new ExoPlayerTest.PositionGrabbingMessageTarget();
        ActionSchedule actionSchedule = /* windowIndex = */
        /* positionMs= */
        new ActionSchedule.Builder("testSendMessages").pause().waitForTimelineChanged(timeline).sendMessage(target, 2, 50).play().build();
        new Builder().setTimeline(timeline).setActionSchedule(actionSchedule).build(context).start().blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
        assertThat(target.windowIndex).isEqualTo(2);
        assertThat(target.positionMs).isAtLeast(50L);
    }

    @Test
    public void testSendMessagesMoveWindowIndex() throws Exception {
        Timeline timeline = new FakeTimeline(/* periodCount= */
        /* id= */
        new TimelineWindowDefinition(1, 0), /* periodCount= */
        /* id= */
        new TimelineWindowDefinition(1, 1));
        final Timeline secondTimeline = new FakeTimeline(/* periodCount= */
        /* id= */
        new TimelineWindowDefinition(1, 1), /* periodCount= */
        /* id= */
        new TimelineWindowDefinition(1, 0));
        final FakeMediaSource mediaSource = new FakeMediaSource(timeline, null, Builder.VIDEO_FORMAT);
        ExoPlayerTest.PositionGrabbingMessageTarget target = new ExoPlayerTest.PositionGrabbingMessageTarget();
        ActionSchedule actionSchedule = /* windowIndex= */
        /* positionMs= */
        /* windowIndex = */
        /* positionMs= */
        new ActionSchedule.Builder("testSendMessages").pause().waitForTimelineChanged(timeline).sendMessage(target, 1, 50).executeRunnable(() -> mediaSource.setNewSourceInfo(secondTimeline, null)).waitForTimelineChanged(secondTimeline).seek(0, 0).play().build();
        new Builder().setMediaSource(mediaSource).setActionSchedule(actionSchedule).build(context).start().blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
        assertThat(target.positionMs).isAtLeast(50L);
        assertThat(target.windowIndex).isEqualTo(0);
    }

    @Test
    public void testSendMessagesNonLinearPeriodOrder() throws Exception {
        Timeline fakeTimeline = /* windowCount= */
        new FakeTimeline(1);
        MediaSource[] fakeMediaSources = new MediaSource[]{ new FakeMediaSource(fakeTimeline, null, Builder.VIDEO_FORMAT), new FakeMediaSource(fakeTimeline, null, Builder.VIDEO_FORMAT), new FakeMediaSource(fakeTimeline, null, Builder.VIDEO_FORMAT) };
        ConcatenatingMediaSource mediaSource = new ConcatenatingMediaSource(false, new FakeShuffleOrder(3), fakeMediaSources);
        ExoPlayerTest.PositionGrabbingMessageTarget target1 = new ExoPlayerTest.PositionGrabbingMessageTarget();
        ExoPlayerTest.PositionGrabbingMessageTarget target2 = new ExoPlayerTest.PositionGrabbingMessageTarget();
        ExoPlayerTest.PositionGrabbingMessageTarget target3 = new ExoPlayerTest.PositionGrabbingMessageTarget();
        ActionSchedule actionSchedule = /* windowIndex= */
        /* positionMs= */
        /* windowIndex = */
        /* positionMs= */
        /* windowIndex = */
        /* positionMs= */
        /* windowIndex = */
        /* positionMs= */
        new ActionSchedule.Builder("testSendMessages").pause().waitForPlaybackState(STATE_READY).sendMessage(target1, 0, 50).sendMessage(target2, 1, 50).sendMessage(target3, 2, 50).setShuffleModeEnabled(true).seek(2, 0).play().build();
        new ExoPlayerTestRunner.Builder().setMediaSource(mediaSource).setActionSchedule(actionSchedule).build(context).start().blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
        assertThat(target1.windowIndex).isEqualTo(0);
        assertThat(target2.windowIndex).isEqualTo(1);
        assertThat(target3.windowIndex).isEqualTo(2);
    }

    @Test
    public void testCancelMessageBeforeDelivery() throws Exception {
        Timeline timeline = /* windowCount= */
        new FakeTimeline(1);
        final ExoPlayerTest.PositionGrabbingMessageTarget target = new ExoPlayerTest.PositionGrabbingMessageTarget();
        final AtomicReference<PlayerMessage> message = new AtomicReference<>();
        ActionSchedule actionSchedule = // Play a bit to ensure message arrived in internal player.
        /* windowIndex= */
        /* positionMs= */
        new ActionSchedule.Builder("testCancelMessage").pause().waitForPlaybackState(STATE_BUFFERING).executeRunnable(new PlayerRunnable() {
            @Override
            public void run(SimpleExoPlayer player) {
                message.set(/* positionMs= */
                player.createMessage(target).setPosition(50).send());
            }
        }).playUntilPosition(0, 30).executeRunnable(() -> message.get().cancel()).play().build();
        new Builder().setTimeline(timeline).setActionSchedule(actionSchedule).build(context).start().blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
        assertThat(message.get().isCanceled()).isTrue();
        assertThat(target.messageCount).isEqualTo(0);
    }

    @Test
    public void testCancelRepeatedMessageAfterDelivery() throws Exception {
        Timeline timeline = /* windowCount= */
        new FakeTimeline(1);
        final ExoPlayerTest.PositionGrabbingMessageTarget target = new ExoPlayerTest.PositionGrabbingMessageTarget();
        final AtomicReference<PlayerMessage> message = new AtomicReference<>();
        ActionSchedule actionSchedule = // Seek back, cancel the message, and play past the same position again.
        /* positionMs= */
        // Play until the message has been delivered.
        /* windowIndex= */
        /* positionMs= */
        new ActionSchedule.Builder("testCancelMessage").pause().waitForPlaybackState(STATE_BUFFERING).executeRunnable(new PlayerRunnable() {
            @Override
            public void run(SimpleExoPlayer player) {
                message.set(/* deleteAfterDelivery= */
                /* positionMs= */
                player.createMessage(target).setPosition(50).setDeleteAfterDelivery(false).send());
            }
        }).playUntilPosition(0, 51).seek(0).executeRunnable(() -> message.get().cancel()).play().build();
        new Builder().setTimeline(timeline).setActionSchedule(actionSchedule).build(context).start().blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
        assertThat(message.get().isCanceled()).isTrue();
        assertThat(target.messageCount).isEqualTo(1);
    }

    @Test
    public void testSetAndSwitchSurface() throws Exception {
        final List<Integer> rendererMessages = new ArrayList<>();
        Renderer videoRenderer = new FakeRenderer(Builder.VIDEO_FORMAT) {
            @Override
            public void handleMessage(int what, @Nullable
            Object object) throws ExoPlaybackException {
                super.handleMessage(what, object);
                rendererMessages.add(what);
            }
        };
        ActionSchedule actionSchedule = ExoPlayerTest.addSurfaceSwitch(new ActionSchedule.Builder("testSetAndSwitchSurface")).build();
        new ExoPlayerTestRunner.Builder().setRenderers(videoRenderer).setActionSchedule(actionSchedule).build(context).start().blockUntilActionScheduleFinished(ExoPlayerTest.TIMEOUT_MS).blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
        assertThat(Collections.frequency(rendererMessages, MSG_SET_SURFACE)).isEqualTo(2);
    }

    @Test
    public void testSwitchSurfaceOnEndedState() throws Exception {
        ActionSchedule.Builder scheduleBuilder = new ActionSchedule.Builder("testSwitchSurfaceOnEndedState").waitForPlaybackState(STATE_ENDED);
        ActionSchedule waitForEndedAndSwitchSchedule = ExoPlayerTest.addSurfaceSwitch(scheduleBuilder).build();
        new ExoPlayerTestRunner.Builder().setTimeline(EMPTY).setActionSchedule(waitForEndedAndSwitchSchedule).build(context).start().blockUntilActionScheduleFinished(ExoPlayerTest.TIMEOUT_MS).blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
    }

    @Test
    public void testTimelineUpdateDropsPrebufferedPeriods() throws Exception {
        Timeline timeline1 = new FakeTimeline(/* periodCount= */
        /* id= */
        new TimelineWindowDefinition(1, 1), /* periodCount= */
        /* id= */
        new TimelineWindowDefinition(1, 2));
        final Timeline timeline2 = new FakeTimeline(/* periodCount= */
        /* id= */
        new TimelineWindowDefinition(1, 1), /* periodCount= */
        /* id= */
        new TimelineWindowDefinition(1, 3));
        final FakeMediaSource mediaSource = /* manifest= */
        new FakeMediaSource(timeline1, null, Builder.VIDEO_FORMAT);
        ActionSchedule actionSchedule = // Ensure next period is pre-buffered by playing until end of first period.
        /* windowIndex= */
        /* positionMs= */
        new ActionSchedule.Builder("testTimelineUpdateDropsPeriods").pause().waitForPlaybackState(STATE_READY).playUntilPosition(0, C.usToMs(DEFAULT_WINDOW_DURATION_US)).executeRunnable(() -> /* newManifest= */
        mediaSource.setNewSourceInfo(timeline2, null)).waitForTimelineChanged(timeline2).play().build();
        ExoPlayerTestRunner testRunner = new ExoPlayerTestRunner.Builder().setMediaSource(mediaSource).setActionSchedule(actionSchedule).build(context).start().blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
        testRunner.assertPlayedPeriodIndices(0, 1);
        // Assert that the second period was re-created from the new timeline.
        assertThat(mediaSource.getCreatedMediaPeriods()).containsExactly(/* windowSequenceNumber= */
        new MediaPeriodId(/* periodIndex= */
        timeline1.getUidOfPeriod(0), 0), /* windowSequenceNumber= */
        new MediaPeriodId(/* periodIndex= */
        timeline1.getUidOfPeriod(1), 1), /* windowSequenceNumber= */
        new MediaPeriodId(/* periodIndex= */
        timeline2.getUidOfPeriod(1), 2)).inOrder();
    }

    @Test
    public void testRepeatedSeeksToUnpreparedPeriodInSameWindowKeepsWindowSequenceNumber() throws Exception {
        Timeline timeline = new FakeTimeline(/* periodCount= */
        /* id= */
        /* isSeekable= */
        /* isDynamic= */
        /* durationUs= */
        new TimelineWindowDefinition(2, 0, true, false, (10 * (MICROS_PER_SECOND))));
        FakeMediaSource mediaSource = /* manifest= */
        new FakeMediaSource(timeline, null);
        ActionSchedule actionSchedule = /* windowIndex= */
        /* positionMs= */
        /* windowIndex= */
        /* positionMs= */
        /* windowIndex= */
        /* positionMs= */
        new ActionSchedule.Builder("testSeekToUnpreparedPeriod").pause().waitForPlaybackState(STATE_READY).seek(0, 9999).seek(0, 1).seek(0, 9999).play().build();
        ExoPlayerTestRunner testRunner = new ExoPlayerTestRunner.Builder().setMediaSource(mediaSource).setActionSchedule(actionSchedule).build(context).start().blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
        testRunner.assertPlayedPeriodIndices(0, 1, 0, 1);
        assertThat(mediaSource.getCreatedMediaPeriods()).containsAllOf(/* windowSequenceNumber= */
        new MediaPeriodId(/* periodIndex= */
        timeline.getUidOfPeriod(0), 0), /* windowSequenceNumber= */
        new MediaPeriodId(/* periodIndex= */
        timeline.getUidOfPeriod(1), 0));
        assertThat(mediaSource.getCreatedMediaPeriods()).doesNotContain(/* windowSequenceNumber= */
        new MediaPeriodId(/* periodIndex= */
        timeline.getUidOfPeriod(1), 1));
    }

    @Test
    public void testRecursivePlayerChangesReportConsistentValuesForAllListeners() throws Exception {
        // We add two listeners to the player. The first stops the player as soon as it's ready and both
        // record the state change events they receive.
        final AtomicReference<Player> playerReference = new AtomicReference<>();
        final List<Integer> eventListener1States = new ArrayList<>();
        final List<Integer> eventListener2States = new ArrayList<>();
        final EventListener eventListener1 = new EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                eventListener1States.add(playbackState);
                if (playbackState == (STATE_READY)) {
                    /* reset= */
                    playerReference.get().stop(true);
                }
            }
        };
        final EventListener eventListener2 = new EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                eventListener2States.add(playbackState);
            }
        };
        ActionSchedule actionSchedule = new ActionSchedule.Builder("testRecursivePlayerChanges").executeRunnable(new PlayerRunnable() {
            @Override
            public void run(SimpleExoPlayer player) {
                playerReference.set(player);
                player.addListener(eventListener1);
                player.addListener(eventListener2);
            }
        }).build();
        new ExoPlayerTestRunner.Builder().setActionSchedule(actionSchedule).build(context).start().blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
        assertThat(eventListener1States).containsExactly(STATE_BUFFERING, STATE_READY, STATE_IDLE).inOrder();
        assertThat(eventListener2States).containsExactly(STATE_BUFFERING, STATE_READY, STATE_IDLE).inOrder();
    }

    @Test
    public void testRecursivePlayerChangesAreReportedInCorrectOrder() throws Exception {
        // The listener stops the player as soon as it's ready (which should report a timeline and state
        // change) and sets playWhenReady to false when the timeline callback is received.
        final AtomicReference<Player> playerReference = new AtomicReference<>();
        final List<Boolean> eventListenerPlayWhenReady = new ArrayList<>();
        final List<Integer> eventListenerStates = new ArrayList<>();
        final EventListener eventListener = new EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, @Nullable
            Object manifest, int reason) {
                if (timeline.isEmpty()) {
                    /* playWhenReady= */
                    playerReference.get().setPlayWhenReady(false);
                }
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                eventListenerPlayWhenReady.add(playWhenReady);
                eventListenerStates.add(playbackState);
                if (playbackState == (Player.STATE_READY)) {
                    /* reset= */
                    playerReference.get().stop(true);
                }
            }
        };
        ActionSchedule actionSchedule = new ActionSchedule.Builder("testRecursivePlayerChanges").executeRunnable(new PlayerRunnable() {
            @Override
            public void run(SimpleExoPlayer player) {
                playerReference.set(player);
                player.addListener(eventListener);
            }
        }).build();
        new ExoPlayerTestRunner.Builder().setActionSchedule(actionSchedule).build(context).start().blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
        assertThat(eventListenerStates).containsExactly(STATE_BUFFERING, STATE_READY, STATE_IDLE, STATE_IDLE).inOrder();
        assertThat(eventListenerPlayWhenReady).containsExactly(true, true, true, false).inOrder();
    }

    @Test
    public void testClippedLoopedPeriodsArePlayedFully() throws Exception {
        long startPositionUs = 300000;
        long expectedDurationUs = 700000;
        MediaSource mediaSource = new ClippingMediaSource(/* manifest= */
        new FakeMediaSource(/* windowCount= */
        new FakeTimeline(1), null), startPositionUs, (startPositionUs + expectedDurationUs));
        Clock clock = new AutoAdvancingFakeClock();
        AtomicReference<Player> playerReference = new AtomicReference<>();
        AtomicLong positionAtDiscontinuityMs = new AtomicLong(TIME_UNSET);
        AtomicLong clockAtStartMs = new AtomicLong(TIME_UNSET);
        AtomicLong clockAtDiscontinuityMs = new AtomicLong(TIME_UNSET);
        EventListener eventListener = new EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if ((playbackState == (Player.STATE_READY)) && ((clockAtStartMs.get()) == (C.TIME_UNSET))) {
                    clockAtStartMs.set(clock.elapsedRealtime());
                }
            }

            @Override
            public void onPositionDiscontinuity(@DiscontinuityReason
            int reason) {
                if (reason == (DISCONTINUITY_REASON_PERIOD_TRANSITION)) {
                    positionAtDiscontinuityMs.set(playerReference.get().getCurrentPosition());
                    clockAtDiscontinuityMs.set(clock.elapsedRealtime());
                }
            }
        };
        ActionSchedule actionSchedule = /* windowIndex= */
        // Play until the media repeats once.
        /* windowIndex= */
        /* positionMs= */
        new ActionSchedule.Builder("testClippedLoopedPeriodsArePlayedFully").executeRunnable(new PlayerRunnable() {
            @Override
            public void run(SimpleExoPlayer player) {
                playerReference.set(player);
                player.addListener(eventListener);
            }
        }).pause().setRepeatMode(REPEAT_MODE_ALL).waitForPlaybackState(STATE_READY).playUntilPosition(0, 1).playUntilStartOfWindow(0).setRepeatMode(REPEAT_MODE_OFF).play().build();
        new ExoPlayerTestRunner.Builder().setClock(clock).setMediaSource(mediaSource).setActionSchedule(actionSchedule).build(context).start().blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
        assertThat(positionAtDiscontinuityMs.get()).isAtLeast(0L);
        assertThat(((clockAtDiscontinuityMs.get()) - (clockAtStartMs.get()))).isAtLeast(C.usToMs(expectedDurationUs));
    }

    @Test
    public void testUpdateTrackSelectorThenSeekToUnpreparedPeriod_returnsEmptyTrackGroups() throws Exception {
        // Use unset duration to prevent pre-loading of the second window.
        Timeline fakeTimeline = new FakeTimeline(/* isSeekable= */
        /* isDynamic= */
        /* durationUs= */
        new TimelineWindowDefinition(true, false, TIME_UNSET));
        MediaSource[] fakeMediaSources = new MediaSource[]{ new FakeMediaSource(fakeTimeline, null, Builder.VIDEO_FORMAT), new FakeMediaSource(fakeTimeline, null, Builder.AUDIO_FORMAT) };
        MediaSource mediaSource = new ConcatenatingMediaSource(fakeMediaSources);
        FakeRenderer renderer = new FakeRenderer(Builder.VIDEO_FORMAT);
        DefaultTrackSelector trackSelector = new DefaultTrackSelector();
        ActionSchedule actionSchedule = /* windowIndex= */
        /* positionMs= */
        new ActionSchedule.Builder("testUpdateTrackSelectorThenSeekToUnpreparedPeriod").pause().waitForPlaybackState(STATE_READY).seek(1, 0).play().build();
        List<TrackGroupArray> trackGroupsList = new ArrayList<>();
        List<TrackSelectionArray> trackSelectionsList = new ArrayList<>();
        new Builder().setMediaSource(mediaSource).setTrackSelector(trackSelector).setRenderers(renderer).setActionSchedule(actionSchedule).setEventListener(new EventListener() {
            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                trackGroupsList.add(trackGroups);
                trackSelectionsList.add(trackSelections);
            }
        }).build(context).start().blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
        assertThat(trackGroupsList).hasSize(3);
        // First track groups of the 1st period are reported.
        // Then the seek to an unprepared period will result in empty track groups and selections being
        // returned.
        // Then the track groups of the 2nd period are reported.
        assertThat(trackGroupsList.get(0).get(0).getFormat(0)).isEqualTo(VIDEO_FORMAT);
        assertThat(trackGroupsList.get(1)).isEqualTo(TrackGroupArray.EMPTY);
        assertThat(trackSelectionsList.get(1).get(0)).isNull();
        assertThat(trackGroupsList.get(2).get(0).getFormat(0)).isEqualTo(AUDIO_FORMAT);
    }

    @Test
    public void testSecondMediaSourceInPlaylistOnlyThrowsWhenPreviousPeriodIsFullyRead() throws Exception {
        Timeline fakeTimeline = new FakeTimeline(/* isSeekable= */
        /* isDynamic= */
        /* durationUs= */
        new TimelineWindowDefinition(true, false, (10 * (MICROS_PER_SECOND))));
        MediaSource workingMediaSource = /* manifest= */
        new FakeMediaSource(fakeTimeline, null, Builder.VIDEO_FORMAT);
        MediaSource failingMediaSource = new FakeMediaSource(null, null, Builder.VIDEO_FORMAT) {
            @Override
            public void maybeThrowSourceInfoRefreshError() throws IOException {
                throw new IOException();
            }
        };
        ConcatenatingMediaSource concatenatingMediaSource = new ConcatenatingMediaSource(workingMediaSource, failingMediaSource);
        FakeRenderer renderer = new FakeRenderer(Builder.VIDEO_FORMAT);
        ExoPlayerTestRunner testRunner = new Builder().setMediaSource(concatenatingMediaSource).setRenderers(renderer).build(context);
        try {
            testRunner.start().blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
            Assert.fail();
        } catch (ExoPlaybackException e) {
            // Expected exception.
        }
        assertThat(renderer.sampleBufferReadCount).isAtLeast(1);
        assertThat(renderer.hasReadStreamToEnd()).isTrue();
    }

    @Test
    public void testDynamicallyAddedSecondMediaSourceInPlaylistOnlyThrowsWhenPreviousPeriodIsFullyRead() throws Exception {
        Timeline fakeTimeline = new FakeTimeline(/* isSeekable= */
        /* isDynamic= */
        /* durationUs= */
        new TimelineWindowDefinition(true, false, (10 * (MICROS_PER_SECOND))));
        MediaSource workingMediaSource = /* manifest= */
        new FakeMediaSource(fakeTimeline, null, Builder.VIDEO_FORMAT);
        MediaSource failingMediaSource = new FakeMediaSource(null, null, Builder.VIDEO_FORMAT) {
            @Override
            public void maybeThrowSourceInfoRefreshError() throws IOException {
                throw new IOException();
            }
        };
        ConcatenatingMediaSource concatenatingMediaSource = new ConcatenatingMediaSource(workingMediaSource);
        ActionSchedule actionSchedule = new ActionSchedule.Builder("testFailingSecondMediaSourceInPlaylistOnlyThrowsLater").pause().waitForPlaybackState(STATE_READY).executeRunnable(() -> concatenatingMediaSource.addMediaSource(failingMediaSource)).play().build();
        FakeRenderer renderer = new FakeRenderer(Builder.VIDEO_FORMAT);
        ExoPlayerTestRunner testRunner = new Builder().setMediaSource(concatenatingMediaSource).setActionSchedule(actionSchedule).setRenderers(renderer).build(context);
        try {
            testRunner.start().blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
            Assert.fail();
        } catch (ExoPlaybackException e) {
            // Expected exception.
        }
        assertThat(renderer.sampleBufferReadCount).isAtLeast(1);
        assertThat(renderer.hasReadStreamToEnd()).isTrue();
    }

    @Test
    public void failingDynamicUpdateOnlyThrowsWhenAvailablePeriodHasBeenFullyRead() throws Exception {
        Timeline fakeTimeline = new FakeTimeline(/* isSeekable= */
        /* isDynamic= */
        /* durationUs= */
        new TimelineWindowDefinition(true, true, (10 * (MICROS_PER_SECOND))));
        AtomicReference<Boolean> wasReadyOnce = new AtomicReference<>(false);
        MediaSource mediaSource = new FakeMediaSource(fakeTimeline, null, Builder.VIDEO_FORMAT) {
            @Override
            public void maybeThrowSourceInfoRefreshError() throws IOException {
                if (wasReadyOnce.get()) {
                    throw new IOException();
                }
            }
        };
        ActionSchedule actionSchedule = new ActionSchedule.Builder("testFailingDynamicMediaSourceInTimelineOnlyThrowsLater").pause().waitForPlaybackState(STATE_READY).executeRunnable(() -> wasReadyOnce.set(true)).play().build();
        FakeRenderer renderer = new FakeRenderer(Builder.VIDEO_FORMAT);
        ExoPlayerTestRunner testRunner = new Builder().setMediaSource(mediaSource).setActionSchedule(actionSchedule).setRenderers(renderer).build(context);
        try {
            testRunner.start().blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
            Assert.fail();
        } catch (ExoPlaybackException e) {
            // Expected exception.
        }
        assertThat(renderer.sampleBufferReadCount).isAtLeast(1);
        assertThat(renderer.hasReadStreamToEnd()).isTrue();
    }

    @Test
    public void removingLoopingLastPeriodFromPlaylistDoesNotThrow() throws Exception {
        Timeline timeline = new FakeTimeline(/* isSeekable= */
        /* isDynamic= */
        /* durationUs= */
        new TimelineWindowDefinition(true, true, 100000));
        MediaSource mediaSource = /* manifest= */
        new FakeMediaSource(timeline, null);
        ConcatenatingMediaSource concatenatingMediaSource = new ConcatenatingMediaSource(mediaSource);
        ActionSchedule actionSchedule = // Remove the media source.
        // Enable repeat mode to trigger the creation of new media periods.
        // Play almost to end to ensure the current period is fully buffered.
        /* windowIndex= */
        /* positionMs= */
        new ActionSchedule.Builder("removingLoopingLastPeriodFromPlaylistDoesNotThrow").pause().waitForPlaybackState(STATE_READY).playUntilPosition(0, 90).setRepeatMode(REPEAT_MODE_ALL).executeRunnable(concatenatingMediaSource::clear).build();
        new Builder().setMediaSource(concatenatingMediaSource).setActionSchedule(actionSchedule).build(context).start().blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
    }

    @Test
    public void seekToUnpreparedWindowWithNonZeroOffsetInConcatenationStartsAtCorrectPosition() throws Exception {
        Timeline timeline = /* windowCount= */
        new FakeTimeline(1);
        FakeMediaSource mediaSource = /* timeline= */
        /* manifest= */
        new FakeMediaSource(null, null);
        MediaSource clippedMediaSource = /* startPositionUs= */
        /* endPositionUs= */
        new ClippingMediaSource(mediaSource, (3 * (MICROS_PER_SECOND)), TIME_END_OF_SOURCE);
        MediaSource concatenatedMediaSource = new ConcatenatingMediaSource(clippedMediaSource);
        AtomicLong positionWhenReady = new AtomicLong();
        ActionSchedule actionSchedule = /* positionMs= */
        new ActionSchedule.Builder("seekToUnpreparedWindowWithNonZeroOffsetInConcatenation").pause().waitForPlaybackState(STATE_BUFFERING).seek(10).waitForTimelineChanged().executeRunnable(() -> /* newManifest= */
        mediaSource.setNewSourceInfo(timeline, null)).waitForTimelineChanged().waitForPlaybackState(STATE_READY).executeRunnable(new PlayerRunnable() {
            @Override
            public void run(SimpleExoPlayer player) {
                positionWhenReady.set(player.getContentPosition());
            }
        }).play().build();
        new Builder().setMediaSource(concatenatedMediaSource).setActionSchedule(actionSchedule).build(context).start().blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
        assertThat(positionWhenReady.get()).isEqualTo(10);
    }

    @Test
    public void seekToUnpreparedWindowWithMultiplePeriodsInConcatenationStartsAtCorrectPeriod() throws Exception {
        long periodDurationMs = 5000;
        Timeline timeline = new FakeTimeline(/* periodCount = */
        /* id= */
        /* isSeekable= */
        /* isDynamic= */
        /* durationUs= */
        new TimelineWindowDefinition(2, new Object(), true, false, ((2 * periodDurationMs) * 1000)));
        FakeMediaSource mediaSource = /* timeline= */
        /* manifest= */
        new FakeMediaSource(null, null);
        MediaSource concatenatedMediaSource = new ConcatenatingMediaSource(mediaSource);
        AtomicInteger periodIndexWhenReady = new AtomicInteger();
        AtomicLong positionWhenReady = new AtomicLong();
        ActionSchedule actionSchedule = // Seek 10ms into the second period.
        /* positionMs= */
        new ActionSchedule.Builder("seekToUnpreparedWindowWithMultiplePeriodsInConcatenation").pause().waitForPlaybackState(STATE_BUFFERING).seek((periodDurationMs + 10)).waitForTimelineChanged().executeRunnable(() -> /* newManifest= */
        mediaSource.setNewSourceInfo(timeline, null)).waitForTimelineChanged().waitForPlaybackState(STATE_READY).executeRunnable(new PlayerRunnable() {
            @Override
            public void run(SimpleExoPlayer player) {
                periodIndexWhenReady.set(player.getCurrentPeriodIndex());
                positionWhenReady.set(player.getContentPosition());
            }
        }).play().build();
        new Builder().setMediaSource(concatenatedMediaSource).setActionSchedule(actionSchedule).build(context).start().blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
        assertThat(periodIndexWhenReady.get()).isEqualTo(1);
        assertThat(positionWhenReady.get()).isEqualTo((periodDurationMs + 10));
    }

    @Test
    public void periodTransitionReportsCorrectBufferedPosition() throws Exception {
        int periodCount = 3;
        long periodDurationUs = 5 * (MICROS_PER_SECOND);
        long windowDurationUs = periodCount * periodDurationUs;
        Timeline timeline = new FakeTimeline(/* id= */
        /* isSeekable= */
        /* isDynamic= */
        new TimelineWindowDefinition(periodCount, new Object(), true, false, windowDurationUs));
        AtomicReference<Player> playerReference = new AtomicReference<>();
        AtomicLong bufferedPositionAtFirstDiscontinuityMs = new AtomicLong(TIME_UNSET);
        EventListener eventListener = new EventListener() {
            @Override
            public void onPositionDiscontinuity(@DiscontinuityReason
            int reason) {
                if (reason == (Player.DISCONTINUITY_REASON_PERIOD_TRANSITION)) {
                    if ((bufferedPositionAtFirstDiscontinuityMs.get()) == (C.TIME_UNSET)) {
                        bufferedPositionAtFirstDiscontinuityMs.set(playerReference.get().getBufferedPosition());
                    }
                }
            }
        };
        ActionSchedule actionSchedule = /* targetIsLoading= */
        // Wait until all periods are fully buffered.
        /* targetIsLoading= */
        new ActionSchedule.Builder("periodTransitionReportsCorrectBufferedPosition").executeRunnable(new PlayerRunnable() {
            @Override
            public void run(SimpleExoPlayer player) {
                playerReference.set(player);
                player.addListener(eventListener);
            }
        }).pause().waitForIsLoading(true).waitForIsLoading(false).play().build();
        new Builder().setTimeline(timeline).setActionSchedule(actionSchedule).build(context).start().blockUntilEnded(ExoPlayerTest.TIMEOUT_MS);
        assertThat(bufferedPositionAtFirstDiscontinuityMs.get()).isEqualTo(C.usToMs(windowDurationUs));
    }

    // Internal classes.
    private static final class PositionGrabbingMessageTarget extends PlayerTarget {
        public int windowIndex;

        public long positionMs;

        public int messageCount;

        public PositionGrabbingMessageTarget() {
            windowIndex = INDEX_UNSET;
            positionMs = POSITION_UNSET;
        }

        @Override
        public void handleMessage(SimpleExoPlayer player, int messageType, @Nullable
        Object message) {
            if (player != null) {
                windowIndex = player.getCurrentWindowIndex();
                positionMs = player.getCurrentPosition();
            }
            (messageCount)++;
        }
    }
}

