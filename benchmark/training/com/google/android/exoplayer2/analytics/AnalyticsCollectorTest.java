/**
 * Copyright (C) 2018 The Android Open Source Project
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
package com.google.android.exoplayer2.analytics;


import Builder.AUDIO_FORMAT;
import Builder.VIDEO_FORMAT;
import Player.STATE_ENDED;
import Player.STATE_IDLE;
import Player.STATE_READY;
import RobolectricUtil.CustomLooper;
import RobolectricUtil.CustomMessageQueue;
import VideoRendererEventListener.EventDispatcher;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.view.Surface;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.Timeline.Window;
import com.google.android.exoplayer2.audio.AudioRendererEventListener;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSource.MediaPeriodId;
import com.google.android.exoplayer2.source.MediaSourceEventListener.LoadEventInfo;
import com.google.android.exoplayer2.source.MediaSourceEventListener.MediaLoadData;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.testutil.ActionSchedule;
import com.google.android.exoplayer2.testutil.ActionSchedule.PlayerRunnable;
import com.google.android.exoplayer2.testutil.ExoPlayerTestRunner.Builder;
import com.google.android.exoplayer2.testutil.FakeMediaSource;
import com.google.android.exoplayer2.testutil.FakeRenderer;
import com.google.android.exoplayer2.testutil.FakeTimeline;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoRendererEventListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


/**
 * Integration test for {@link AnalyticsCollector}.
 */
@RunWith(RobolectricTestRunner.class)
@Config(shadows = { CustomLooper.class, CustomMessageQueue.class })
public final class AnalyticsCollectorTest {
    private static final int EVENT_PLAYER_STATE_CHANGED = 0;

    private static final int EVENT_TIMELINE_CHANGED = 1;

    private static final int EVENT_POSITION_DISCONTINUITY = 2;

    private static final int EVENT_SEEK_STARTED = 3;

    private static final int EVENT_SEEK_PROCESSED = 4;

    private static final int EVENT_PLAYBACK_PARAMETERS_CHANGED = 5;

    private static final int EVENT_REPEAT_MODE_CHANGED = 6;

    private static final int EVENT_SHUFFLE_MODE_CHANGED = 7;

    private static final int EVENT_LOADING_CHANGED = 8;

    private static final int EVENT_PLAYER_ERROR = 9;

    private static final int EVENT_TRACKS_CHANGED = 10;

    private static final int EVENT_LOAD_STARTED = 11;

    private static final int EVENT_LOAD_COMPLETED = 12;

    private static final int EVENT_LOAD_CANCELED = 13;

    private static final int EVENT_LOAD_ERROR = 14;

    private static final int EVENT_DOWNSTREAM_FORMAT_CHANGED = 15;

    private static final int EVENT_UPSTREAM_DISCARDED = 16;

    private static final int EVENT_MEDIA_PERIOD_CREATED = 17;

    private static final int EVENT_MEDIA_PERIOD_RELEASED = 18;

    private static final int EVENT_READING_STARTED = 19;

    private static final int EVENT_BANDWIDTH_ESTIMATE = 20;

    private static final int EVENT_SURFACE_SIZE_CHANGED = 21;

    private static final int EVENT_METADATA = 23;

    private static final int EVENT_DECODER_ENABLED = 24;

    private static final int EVENT_DECODER_INIT = 25;

    private static final int EVENT_DECODER_FORMAT_CHANGED = 26;

    private static final int EVENT_DECODER_DISABLED = 27;

    private static final int EVENT_AUDIO_SESSION_ID = 28;

    private static final int EVENT_AUDIO_UNDERRUN = 29;

    private static final int EVENT_DROPPED_VIDEO_FRAMES = 30;

    private static final int EVENT_VIDEO_SIZE_CHANGED = 31;

    private static final int EVENT_RENDERED_FIRST_FRAME = 32;

    private static final int EVENT_DRM_KEYS_LOADED = 33;

    private static final int EVENT_DRM_ERROR = 34;

    private static final int EVENT_DRM_KEYS_RESTORED = 35;

    private static final int EVENT_DRM_KEYS_REMOVED = 36;

    private static final int EVENT_DRM_SESSION_ACQUIRED = 37;

    private static final int EVENT_DRM_SESSION_RELEASED = 38;

    private static final int TIMEOUT_MS = 10000;

    private static final Timeline SINGLE_PERIOD_TIMELINE = /* windowCount= */
    new FakeTimeline(1);

    private static final AnalyticsCollectorTest.EventWindowAndPeriodId WINDOW_0 = /* windowIndex= */
    /* mediaPeriodId= */
    new AnalyticsCollectorTest.EventWindowAndPeriodId(0, null);

    private static final AnalyticsCollectorTest.EventWindowAndPeriodId WINDOW_1 = /* windowIndex= */
    /* mediaPeriodId= */
    new AnalyticsCollectorTest.EventWindowAndPeriodId(1, null);

    private AnalyticsCollectorTest.EventWindowAndPeriodId period0;

    private AnalyticsCollectorTest.EventWindowAndPeriodId period1;

    private AnalyticsCollectorTest.EventWindowAndPeriodId period0Seq0;

    private AnalyticsCollectorTest.EventWindowAndPeriodId period1Seq1;

    private AnalyticsCollectorTest.EventWindowAndPeriodId period0Seq1;

    private AnalyticsCollectorTest.EventWindowAndPeriodId period1Seq0;

    private AnalyticsCollectorTest.EventWindowAndPeriodId period1Seq2;

    private AnalyticsCollectorTest.EventWindowAndPeriodId window0Period1Seq0;

    private AnalyticsCollectorTest.EventWindowAndPeriodId window1Period0Seq1;

    @Test
    public void testEmptyTimeline() throws Exception {
        FakeMediaSource mediaSource = /* manifest= */
        new FakeMediaSource(Timeline.EMPTY, null, Builder.VIDEO_FORMAT, Builder.AUDIO_FORMAT);
        AnalyticsCollectorTest.TestAnalyticsListener listener = AnalyticsCollectorTest.runAnalyticsTest(mediaSource);
        /* setPlayWhenReady */
        /* BUFFERING */
        /* ENDED */
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_PLAYER_STATE_CHANGED)).containsExactly(AnalyticsCollectorTest.WINDOW_0, AnalyticsCollectorTest.WINDOW_0, AnalyticsCollectorTest.WINDOW_0);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_TIMELINE_CHANGED)).containsExactly(AnalyticsCollectorTest.WINDOW_0);
        listener.assertNoMoreEvents();
    }

    @Test
    public void testSinglePeriod() throws Exception {
        FakeMediaSource mediaSource = /* manifest= */
        new FakeMediaSource(AnalyticsCollectorTest.SINGLE_PERIOD_TIMELINE, null, Builder.VIDEO_FORMAT, Builder.AUDIO_FORMAT);
        AnalyticsCollectorTest.TestAnalyticsListener listener = AnalyticsCollectorTest.runAnalyticsTest(mediaSource);
        populateEventIds(AnalyticsCollectorTest.SINGLE_PERIOD_TIMELINE);
        /* setPlayWhenReady */
        /* BUFFERING */
        /* READY */
        /* ENDED */
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_PLAYER_STATE_CHANGED)).containsExactly(AnalyticsCollectorTest.WINDOW_0, AnalyticsCollectorTest.WINDOW_0, period0, period0);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_TIMELINE_CHANGED)).containsExactly(AnalyticsCollectorTest.WINDOW_0);
        /* started */
        /* stopped */
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_LOADING_CHANGED)).containsExactly(period0, period0);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_TRACKS_CHANGED)).containsExactly(period0);
        /* manifest */
        /* media */
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_LOAD_STARTED)).containsExactly(AnalyticsCollectorTest.WINDOW_0, period0);
        /* manifest */
        /* media */
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_LOAD_COMPLETED)).containsExactly(AnalyticsCollectorTest.WINDOW_0, period0);
        /* audio */
        /* video */
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_DOWNSTREAM_FORMAT_CHANGED)).containsExactly(period0, period0);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_MEDIA_PERIOD_CREATED)).containsExactly(period0);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_READING_STARTED)).containsExactly(period0);
        /* audio */
        /* video */
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_DECODER_ENABLED)).containsExactly(period0, period0);
        /* audio */
        /* video */
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_DECODER_INIT)).containsExactly(period0, period0);
        /* audio */
        /* video */
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_DECODER_FORMAT_CHANGED)).containsExactly(period0, period0);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_AUDIO_SESSION_ID)).containsExactly(period0);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_DROPPED_VIDEO_FRAMES)).containsExactly(period0);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_VIDEO_SIZE_CHANGED)).containsExactly(period0);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_RENDERED_FIRST_FRAME)).containsExactly(period0);
        listener.assertNoMoreEvents();
    }

    @Test
    public void testAutomaticPeriodTransition() throws Exception {
        MediaSource mediaSource = new ConcatenatingMediaSource(/* manifest= */
        new FakeMediaSource(AnalyticsCollectorTest.SINGLE_PERIOD_TIMELINE, null, Builder.VIDEO_FORMAT, Builder.AUDIO_FORMAT), /* manifest= */
        new FakeMediaSource(AnalyticsCollectorTest.SINGLE_PERIOD_TIMELINE, null, Builder.VIDEO_FORMAT, Builder.AUDIO_FORMAT));
        AnalyticsCollectorTest.TestAnalyticsListener listener = AnalyticsCollectorTest.runAnalyticsTest(mediaSource);
        populateEventIds(listener.lastReportedTimeline);
        /* setPlayWhenReady */
        /* BUFFERING */
        /* READY */
        /* ENDED */
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_PLAYER_STATE_CHANGED)).containsExactly(AnalyticsCollectorTest.WINDOW_0, AnalyticsCollectorTest.WINDOW_0, period0, period1);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_TIMELINE_CHANGED)).containsExactly(AnalyticsCollectorTest.WINDOW_0);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_POSITION_DISCONTINUITY)).containsExactly(period1);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_LOADING_CHANGED)).containsExactly(period0, period0, period0, period0);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_TRACKS_CHANGED)).containsExactly(period0, period1);
        /* manifest */
        /* media */
        /* manifest */
        /* media */
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_LOAD_STARTED)).containsExactly(AnalyticsCollectorTest.WINDOW_0, period0, AnalyticsCollectorTest.WINDOW_1, period1);
        /* manifest */
        /* media */
        /* manifest */
        /* media */
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_LOAD_COMPLETED)).containsExactly(AnalyticsCollectorTest.WINDOW_0, period0, AnalyticsCollectorTest.WINDOW_1, period1);
        /* audio */
        /* video */
        /* audio */
        /* video */
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_DOWNSTREAM_FORMAT_CHANGED)).containsExactly(period0, period0, period1, period1);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_MEDIA_PERIOD_CREATED)).containsExactly(period0, period1);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_MEDIA_PERIOD_RELEASED)).containsExactly(period0);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_READING_STARTED)).containsExactly(period0, period1);
        /* audio */
        /* video */
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_DECODER_ENABLED)).containsExactly(period0, period0);
        /* audio */
        /* video */
        /* audio */
        /* video */
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_DECODER_INIT)).containsExactly(period0, period0, period1, period1);
        /* audio */
        /* video */
        /* audio */
        /* video */
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_DECODER_FORMAT_CHANGED)).containsExactly(period0, period0, period1, period1);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_AUDIO_SESSION_ID)).containsExactly(period0);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_DROPPED_VIDEO_FRAMES)).containsExactly(period1);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_VIDEO_SIZE_CHANGED)).containsExactly(period0);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_RENDERED_FIRST_FRAME)).containsExactly(period0);
        listener.assertNoMoreEvents();
    }

    @Test
    public void testPeriodTransitionWithRendererChange() throws Exception {
        MediaSource mediaSource = new ConcatenatingMediaSource(/* manifest= */
        new FakeMediaSource(AnalyticsCollectorTest.SINGLE_PERIOD_TIMELINE, null, Builder.VIDEO_FORMAT), /* manifest= */
        new FakeMediaSource(AnalyticsCollectorTest.SINGLE_PERIOD_TIMELINE, null, Builder.AUDIO_FORMAT));
        AnalyticsCollectorTest.TestAnalyticsListener listener = AnalyticsCollectorTest.runAnalyticsTest(mediaSource);
        populateEventIds(listener.lastReportedTimeline);
        /* setPlayWhenReady */
        /* BUFFERING */
        /* READY */
        /* BUFFERING */
        /* READY */
        /* ENDED */
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_PLAYER_STATE_CHANGED)).containsExactly(AnalyticsCollectorTest.WINDOW_0, AnalyticsCollectorTest.WINDOW_0, period0, period1, period1, period1);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_TIMELINE_CHANGED)).containsExactly(AnalyticsCollectorTest.WINDOW_0);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_POSITION_DISCONTINUITY)).containsExactly(period1);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_LOADING_CHANGED)).containsExactly(period0, period0, period0, period0);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_TRACKS_CHANGED)).containsExactly(period0, period1);
        /* manifest */
        /* media */
        /* manifest */
        /* media */
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_LOAD_STARTED)).containsExactly(AnalyticsCollectorTest.WINDOW_0, period0, AnalyticsCollectorTest.WINDOW_1, period1);
        /* manifest */
        /* media */
        /* manifest */
        /* media */
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_LOAD_COMPLETED)).containsExactly(AnalyticsCollectorTest.WINDOW_0, period0, AnalyticsCollectorTest.WINDOW_1, period1);
        /* video */
        /* audio */
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_DOWNSTREAM_FORMAT_CHANGED)).containsExactly(period0, period1);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_MEDIA_PERIOD_CREATED)).containsExactly(period0, period1);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_MEDIA_PERIOD_RELEASED)).containsExactly(period0);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_READING_STARTED)).containsExactly(period0, period1);
        /* video */
        /* audio */
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_DECODER_ENABLED)).containsExactly(period0, period1);
        /* video */
        /* audio */
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_DECODER_INIT)).containsExactly(period0, period1);
        /* video */
        /* audio */
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_DECODER_FORMAT_CHANGED)).containsExactly(period0, period1);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_DECODER_DISABLED)).containsExactly(period0);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_AUDIO_SESSION_ID)).containsExactly(period1);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_DROPPED_VIDEO_FRAMES)).containsExactly(period0);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_VIDEO_SIZE_CHANGED)).containsExactly(period0);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_RENDERED_FIRST_FRAME)).containsExactly(period0);
        listener.assertNoMoreEvents();
    }

    @Test
    public void testSeekToOtherPeriod() throws Exception {
        MediaSource mediaSource = new ConcatenatingMediaSource(/* manifest= */
        new FakeMediaSource(AnalyticsCollectorTest.SINGLE_PERIOD_TIMELINE, null, Builder.VIDEO_FORMAT), /* manifest= */
        new FakeMediaSource(AnalyticsCollectorTest.SINGLE_PERIOD_TIMELINE, null, Builder.AUDIO_FORMAT));
        ActionSchedule actionSchedule = /* windowIndex= */
        /* positionMs= */
        new ActionSchedule.Builder("AnalyticsCollectorTest").pause().waitForPlaybackState(STATE_READY).seek(1, 0).play().build();
        AnalyticsCollectorTest.TestAnalyticsListener listener = AnalyticsCollectorTest.runAnalyticsTest(mediaSource, actionSchedule);
        populateEventIds(listener.lastReportedTimeline);
        /* setPlayWhenReady=true */
        /* BUFFERING */
        /* setPlayWhenReady=false */
        /* READY */
        /* BUFFERING */
        /* READY */
        /* setPlayWhenReady=true */
        /* ENDED */
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_PLAYER_STATE_CHANGED)).containsExactly(AnalyticsCollectorTest.WINDOW_0, AnalyticsCollectorTest.WINDOW_0, AnalyticsCollectorTest.WINDOW_0, period0, period1, period1, period1, period1);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_TIMELINE_CHANGED)).containsExactly(AnalyticsCollectorTest.WINDOW_0);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_POSITION_DISCONTINUITY)).containsExactly(period1);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_SEEK_STARTED)).containsExactly(period0);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_SEEK_PROCESSED)).containsExactly(period1);
        List<AnalyticsCollectorTest.EventWindowAndPeriodId> loadingEvents = listener.getEvents(AnalyticsCollectorTest.EVENT_LOADING_CHANGED);
        assertThat(loadingEvents).hasSize(4);
        assertThat(loadingEvents).containsAllOf(period0, period0);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_TRACKS_CHANGED)).containsExactly(period0, period1);
        /* manifest */
        /* media */
        /* manifest */
        /* media */
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_LOAD_STARTED)).containsExactly(AnalyticsCollectorTest.WINDOW_0, period0, AnalyticsCollectorTest.WINDOW_1, period1);
        /* manifest */
        /* media */
        /* manifest */
        /* media */
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_LOAD_COMPLETED)).containsExactly(AnalyticsCollectorTest.WINDOW_0, period0, AnalyticsCollectorTest.WINDOW_1, period1);
        /* video */
        /* audio */
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_DOWNSTREAM_FORMAT_CHANGED)).containsExactly(period0, period1);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_MEDIA_PERIOD_CREATED)).containsExactly(period0, period1);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_MEDIA_PERIOD_RELEASED)).containsExactly(period0);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_READING_STARTED)).containsExactly(period0, period1);
        /* video */
        /* audio */
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_DECODER_ENABLED)).containsExactly(period0, period1);
        /* video */
        /* audio */
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_DECODER_INIT)).containsExactly(period0, period1);
        /* video */
        /* audio */
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_DECODER_FORMAT_CHANGED)).containsExactly(period0, period1);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_DECODER_DISABLED)).containsExactly(period0);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_AUDIO_SESSION_ID)).containsExactly(period1);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_VIDEO_SIZE_CHANGED)).containsExactly(period0);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_RENDERED_FIRST_FRAME)).containsExactly(period0);
        listener.assertNoMoreEvents();
    }

    @Test
    public void testSeekBackAfterReadingAhead() throws Exception {
        MediaSource mediaSource = new ConcatenatingMediaSource(/* manifest= */
        new FakeMediaSource(AnalyticsCollectorTest.SINGLE_PERIOD_TIMELINE, null, Builder.VIDEO_FORMAT), /* manifest= */
        new FakeMediaSource(AnalyticsCollectorTest.SINGLE_PERIOD_TIMELINE, null, Builder.VIDEO_FORMAT, Builder.AUDIO_FORMAT));
        long periodDurationMs = /* windowIndex= */
        AnalyticsCollectorTest.SINGLE_PERIOD_TIMELINE.getWindow(0, new Window()).getDurationMs();
        ActionSchedule actionSchedule = /* positionMs= */
        /* windowIndex= */
        new ActionSchedule.Builder("AnalyticsCollectorTest").pause().waitForPlaybackState(STATE_READY).playUntilPosition(0, periodDurationMs).seek(0).waitForPlaybackState(STATE_READY).play().build();
        AnalyticsCollectorTest.TestAnalyticsListener listener = AnalyticsCollectorTest.runAnalyticsTest(mediaSource, actionSchedule);
        populateEventIds(listener.lastReportedTimeline);
        /* setPlayWhenReady=true */
        /* BUFFERING */
        /* setPlayWhenReady=false */
        /* READY */
        /* setPlayWhenReady=true */
        /* setPlayWhenReady=false */
        /* BUFFERING */
        /* READY */
        /* setPlayWhenReady=true */
        /* BUFFERING */
        /* READY */
        /* ENDED */
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_PLAYER_STATE_CHANGED)).containsExactly(AnalyticsCollectorTest.WINDOW_0, AnalyticsCollectorTest.WINDOW_0, AnalyticsCollectorTest.WINDOW_0, period0, period0, period0, period0, period0, period0, period1Seq2, period1Seq2, period1Seq2);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_TIMELINE_CHANGED)).containsExactly(AnalyticsCollectorTest.WINDOW_0);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_POSITION_DISCONTINUITY)).containsExactly(period0, period1Seq2);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_SEEK_STARTED)).containsExactly(period0);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_SEEK_PROCESSED)).containsExactly(period0);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_LOADING_CHANGED)).containsExactly(period0, period0, period0, period0, period0, period0);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_TRACKS_CHANGED)).containsExactly(period0, period1Seq2);
        /* manifest */
        /* media */
        /* manifest */
        /* media */
        /* media */
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_LOAD_STARTED)).containsExactly(AnalyticsCollectorTest.WINDOW_0, period0, AnalyticsCollectorTest.WINDOW_1, period1Seq1, period1Seq2);
        /* manifest */
        /* media */
        /* manifest */
        /* media */
        /* media */
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_LOAD_COMPLETED)).containsExactly(AnalyticsCollectorTest.WINDOW_0, period0, AnalyticsCollectorTest.WINDOW_1, period1Seq1, period1Seq2);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_DOWNSTREAM_FORMAT_CHANGED)).containsExactly(period0, period1Seq1, period1Seq2, period1Seq2);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_MEDIA_PERIOD_CREATED)).containsExactly(period0, period1Seq1, period1Seq2);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_MEDIA_PERIOD_RELEASED)).containsExactly(period0, period1Seq1);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_READING_STARTED)).containsExactly(period0, period1Seq1, period1Seq2);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_DECODER_ENABLED)).containsExactly(period0, period0, period1Seq2);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_DECODER_INIT)).containsExactly(period0, period1Seq1, period1Seq2, period1Seq2);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_DECODER_FORMAT_CHANGED)).containsExactly(period0, period1Seq1, period1Seq2, period1Seq2);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_DECODER_DISABLED)).containsExactly(period0);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_AUDIO_SESSION_ID)).containsExactly(period1Seq2);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_DROPPED_VIDEO_FRAMES)).containsExactly(period0, period0, period1Seq2);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_VIDEO_SIZE_CHANGED)).containsExactly(period0, period1Seq2);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_RENDERED_FIRST_FRAME)).containsExactly(period0, period1Seq2);
        listener.assertNoMoreEvents();
    }

    @Test
    public void testPrepareNewSource() throws Exception {
        MediaSource mediaSource1 = /* manifest= */
        new FakeMediaSource(AnalyticsCollectorTest.SINGLE_PERIOD_TIMELINE, null, Builder.VIDEO_FORMAT);
        MediaSource mediaSource2 = /* manifest= */
        new FakeMediaSource(AnalyticsCollectorTest.SINGLE_PERIOD_TIMELINE, null, Builder.VIDEO_FORMAT);
        ActionSchedule actionSchedule = new ActionSchedule.Builder("AnalyticsCollectorTest").pause().waitForPlaybackState(STATE_READY).prepareSource(mediaSource2).play().build();
        AnalyticsCollectorTest.TestAnalyticsListener listener = AnalyticsCollectorTest.runAnalyticsTest(mediaSource1, actionSchedule);
        populateEventIds(AnalyticsCollectorTest.SINGLE_PERIOD_TIMELINE);
        /* setPlayWhenReady=true */
        /* BUFFERING */
        /* setPlayWhenReady=false */
        /* READY */
        /* BUFFERING */
        /* setPlayWhenReady=true */
        /* READY */
        /* ENDED */
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_PLAYER_STATE_CHANGED)).containsExactly(AnalyticsCollectorTest.WINDOW_0, AnalyticsCollectorTest.WINDOW_0, AnalyticsCollectorTest.WINDOW_0, period0Seq0, AnalyticsCollectorTest.WINDOW_0, AnalyticsCollectorTest.WINDOW_0, period0Seq1, period0Seq1);
        /* prepared */
        /* reset */
        /* prepared */
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_TIMELINE_CHANGED)).containsExactly(AnalyticsCollectorTest.WINDOW_0, AnalyticsCollectorTest.WINDOW_0, AnalyticsCollectorTest.WINDOW_0);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_LOADING_CHANGED)).containsExactly(period0Seq0, period0Seq0, period0Seq1, period0Seq1);
        /* prepared */
        /* reset */
        /* prepared */
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_TRACKS_CHANGED)).containsExactly(period0Seq0, AnalyticsCollectorTest.WINDOW_0, period0Seq1);
        /* manifest */
        /* media */
        /* manifest */
        /* media */
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_LOAD_STARTED)).containsExactly(AnalyticsCollectorTest.WINDOW_0, period0Seq0, AnalyticsCollectorTest.WINDOW_0, period0Seq1);
        /* manifest */
        /* media */
        /* manifest */
        /* media */
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_LOAD_COMPLETED)).containsExactly(AnalyticsCollectorTest.WINDOW_0, period0Seq0, AnalyticsCollectorTest.WINDOW_0, period0Seq1);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_DOWNSTREAM_FORMAT_CHANGED)).containsExactly(period0Seq0, period0Seq1);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_MEDIA_PERIOD_CREATED)).containsExactly(period0Seq0, period0Seq1);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_MEDIA_PERIOD_RELEASED)).containsExactly(period0Seq0);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_READING_STARTED)).containsExactly(period0Seq0, period0Seq1);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_DECODER_ENABLED)).containsExactly(period0Seq0, period0Seq1);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_DECODER_INIT)).containsExactly(period0Seq0, period0Seq1);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_DECODER_FORMAT_CHANGED)).containsExactly(period0Seq0, period0Seq1);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_DECODER_DISABLED)).containsExactly(period0Seq0);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_DROPPED_VIDEO_FRAMES)).containsExactly(period0Seq1);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_VIDEO_SIZE_CHANGED)).containsExactly(period0Seq0, period0Seq1);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_RENDERED_FIRST_FRAME)).containsExactly(period0Seq0, period0Seq1);
        listener.assertNoMoreEvents();
    }

    @Test
    public void testReprepareAfterError() throws Exception {
        MediaSource mediaSource = /* manifest= */
        new FakeMediaSource(AnalyticsCollectorTest.SINGLE_PERIOD_TIMELINE, null, Builder.VIDEO_FORMAT);
        ActionSchedule actionSchedule = /* resetPosition= */
        /* resetState= */
        new ActionSchedule.Builder("AnalyticsCollectorTest").waitForPlaybackState(STATE_READY).throwPlaybackException(ExoPlaybackException.createForSource(new IOException())).waitForPlaybackState(STATE_IDLE).prepareSource(mediaSource, false, false).waitForPlaybackState(STATE_ENDED).build();
        AnalyticsCollectorTest.TestAnalyticsListener listener = AnalyticsCollectorTest.runAnalyticsTest(mediaSource, actionSchedule);
        populateEventIds(AnalyticsCollectorTest.SINGLE_PERIOD_TIMELINE);
        /* setPlayWhenReady=true */
        /* BUFFERING */
        /* READY */
        /* IDLE */
        /* BUFFERING */
        /* READY */
        /* ENDED */
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_PLAYER_STATE_CHANGED)).containsExactly(AnalyticsCollectorTest.WINDOW_0, AnalyticsCollectorTest.WINDOW_0, period0Seq0, AnalyticsCollectorTest.WINDOW_0, AnalyticsCollectorTest.WINDOW_0, period0Seq0, period0Seq0);
        /* prepared */
        /* prepared */
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_TIMELINE_CHANGED)).containsExactly(AnalyticsCollectorTest.WINDOW_0, AnalyticsCollectorTest.WINDOW_0);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_LOADING_CHANGED)).containsExactly(period0Seq0, period0Seq0, period0Seq0, period0Seq0);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_PLAYER_ERROR)).containsExactly(AnalyticsCollectorTest.WINDOW_0);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_TRACKS_CHANGED)).containsExactly(period0Seq0, period0Seq0);
        /* manifest */
        /* media */
        /* manifest */
        /* media */
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_LOAD_STARTED)).containsExactly(AnalyticsCollectorTest.WINDOW_0, period0Seq0, AnalyticsCollectorTest.WINDOW_0, period0Seq0);
        /* manifest */
        /* media */
        /* manifest */
        /* media */
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_LOAD_COMPLETED)).containsExactly(AnalyticsCollectorTest.WINDOW_0, period0Seq0, AnalyticsCollectorTest.WINDOW_0, period0Seq0);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_DOWNSTREAM_FORMAT_CHANGED)).containsExactly(period0Seq0, period0Seq0);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_MEDIA_PERIOD_CREATED)).containsExactly(period0Seq0, period0Seq0);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_MEDIA_PERIOD_RELEASED)).containsExactly(period0Seq0);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_READING_STARTED)).containsExactly(period0Seq0, period0Seq0);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_DECODER_ENABLED)).containsExactly(period0Seq0, period0Seq0);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_DECODER_INIT)).containsExactly(period0Seq0, period0Seq0);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_DECODER_FORMAT_CHANGED)).containsExactly(period0Seq0, period0Seq0);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_DECODER_DISABLED)).containsExactly(period0Seq0);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_DROPPED_VIDEO_FRAMES)).containsExactly(period0Seq0, period0Seq0);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_VIDEO_SIZE_CHANGED)).containsExactly(period0Seq0, period0Seq0);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_RENDERED_FIRST_FRAME)).containsExactly(period0Seq0, period0Seq0);
        listener.assertNoMoreEvents();
    }

    @Test
    public void testDynamicTimelineChange() throws Exception {
        MediaSource childMediaSource = /* manifest= */
        new FakeMediaSource(AnalyticsCollectorTest.SINGLE_PERIOD_TIMELINE, null, Builder.VIDEO_FORMAT);
        final ConcatenatingMediaSource concatenatedMediaSource = new ConcatenatingMediaSource(childMediaSource, childMediaSource);
        long periodDurationMs = /* windowIndex= */
        AnalyticsCollectorTest.SINGLE_PERIOD_TIMELINE.getWindow(0, new Window()).getDurationMs();
        ActionSchedule actionSchedule = // Ensure second period is already being read from.
        /* windowIndex= */
        /* positionMs= */
        new ActionSchedule.Builder("AnalyticsCollectorTest").pause().waitForPlaybackState(STATE_READY).playUntilPosition(0, periodDurationMs).executeRunnable(() -> /* currentIndex= */
        /* newIndex= */
        concatenatedMediaSource.moveMediaSource(0, 1)).waitForTimelineChanged().play().build();
        AnalyticsCollectorTest.TestAnalyticsListener listener = AnalyticsCollectorTest.runAnalyticsTest(concatenatedMediaSource, actionSchedule);
        populateEventIds(listener.lastReportedTimeline);
        /* setPlayWhenReady=true */
        /* BUFFERING */
        /* setPlayWhenReady=false */
        /* READY */
        /* setPlayWhenReady=true */
        /* setPlayWhenReady=false */
        /* setPlayWhenReady=true */
        /* BUFFERING */
        /* ENDED */
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_PLAYER_STATE_CHANGED)).containsExactly(AnalyticsCollectorTest.WINDOW_0, AnalyticsCollectorTest.WINDOW_0, AnalyticsCollectorTest.WINDOW_0, window0Period1Seq0, window0Period1Seq0, window0Period1Seq0, period1Seq0, period1Seq0, period1Seq0);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_TIMELINE_CHANGED)).containsExactly(AnalyticsCollectorTest.WINDOW_0, period1Seq0);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_LOADING_CHANGED)).containsExactly(window0Period1Seq0, window0Period1Seq0, window0Period1Seq0, window0Period1Seq0);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_TRACKS_CHANGED)).containsExactly(window0Period1Seq0);
        /* manifest */
        /* media */
        /* media */
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_LOAD_STARTED)).containsExactly(AnalyticsCollectorTest.WINDOW_0, window0Period1Seq0, window1Period0Seq1);
        /* manifest */
        /* media */
        /* media */
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_LOAD_COMPLETED)).containsExactly(AnalyticsCollectorTest.WINDOW_0, window0Period1Seq0, window1Period0Seq1);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_DOWNSTREAM_FORMAT_CHANGED)).containsExactly(window0Period1Seq0, window1Period0Seq1);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_MEDIA_PERIOD_CREATED)).containsExactly(window0Period1Seq0, window1Period0Seq1);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_MEDIA_PERIOD_RELEASED)).containsExactly(window1Period0Seq1);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_READING_STARTED)).containsExactly(window0Period1Seq0, window1Period0Seq1);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_DECODER_ENABLED)).containsExactly(window0Period1Seq0, window0Period1Seq0);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_DECODER_INIT)).containsExactly(window0Period1Seq0, window1Period0Seq1);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_DECODER_FORMAT_CHANGED)).containsExactly(window0Period1Seq0, window1Period0Seq1);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_DECODER_DISABLED)).containsExactly(window0Period1Seq0);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_DROPPED_VIDEO_FRAMES)).containsExactly(window0Period1Seq0);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_VIDEO_SIZE_CHANGED)).containsExactly(window0Period1Seq0);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_RENDERED_FIRST_FRAME)).containsExactly(window0Period1Seq0);
        listener.assertNoMoreEvents();
    }

    @Test
    public void testNotifyExternalEvents() throws Exception {
        MediaSource mediaSource = /* manifest= */
        new FakeMediaSource(AnalyticsCollectorTest.SINGLE_PERIOD_TIMELINE, null);
        ActionSchedule actionSchedule = /* positionMs= */
        new ActionSchedule.Builder("AnalyticsCollectorTest").pause().waitForPlaybackState(STATE_READY).executeRunnable(new PlayerRunnable() {
            @Override
            public void run(SimpleExoPlayer player) {
                player.getAnalyticsCollector().notifySeekStarted();
            }
        }).seek(0).play().build();
        AnalyticsCollectorTest.TestAnalyticsListener listener = AnalyticsCollectorTest.runAnalyticsTest(mediaSource, actionSchedule);
        populateEventIds(AnalyticsCollectorTest.SINGLE_PERIOD_TIMELINE);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_SEEK_STARTED)).containsExactly(period0);
        assertThat(listener.getEvents(AnalyticsCollectorTest.EVENT_SEEK_PROCESSED)).containsExactly(period0);
    }

    private static final class FakeVideoRenderer extends FakeRenderer {
        private final EventDispatcher eventDispatcher;

        private final DecoderCounters decoderCounters;

        private Format format;

        private boolean renderedFirstFrame;

        public FakeVideoRenderer(Handler handler, VideoRendererEventListener eventListener) {
            super(VIDEO_FORMAT);
            eventDispatcher = new VideoRendererEventListener.EventDispatcher(handler, eventListener);
            decoderCounters = new DecoderCounters();
        }

        @Override
        protected void onEnabled(boolean joining) throws ExoPlaybackException {
            super.onEnabled(joining);
            eventDispatcher.enabled(decoderCounters);
            renderedFirstFrame = false;
        }

        @Override
        protected void onStopped() throws ExoPlaybackException {
            super.onStopped();
            /* droppedFrameCount= */
            /* elapsedMs= */
            eventDispatcher.droppedFrames(0, 0);
        }

        @Override
        protected void onDisabled() {
            super.onDisabled();
            eventDispatcher.disabled(decoderCounters);
        }

        @Override
        protected void onPositionReset(long positionUs, boolean joining) throws ExoPlaybackException {
            super.onPositionReset(positionUs, joining);
            renderedFirstFrame = false;
        }

        @Override
        protected void onFormatChanged(Format format) {
            eventDispatcher.inputFormatChanged(format);
            /* decoderName= */
            /* initializedTimestampMs= */
            /* initializationDurationMs= */
            eventDispatcher.decoderInitialized("fake.video.decoder", SystemClock.elapsedRealtime(), 0);
            this.format = format;
        }

        @Override
        protected void onBufferRead() {
            if (!(renderedFirstFrame)) {
                eventDispatcher.videoSizeChanged(format.width, format.height, format.rotationDegrees, format.pixelWidthHeightRatio);
                /* surface= */
                eventDispatcher.renderedFirstFrame(null);
                renderedFirstFrame = true;
            }
        }
    }

    private static final class FakeAudioRenderer extends FakeRenderer {
        private final AudioRendererEventListener.EventDispatcher eventDispatcher;

        private final DecoderCounters decoderCounters;

        private boolean notifiedAudioSessionId;

        public FakeAudioRenderer(Handler handler, AudioRendererEventListener eventListener) {
            super(AUDIO_FORMAT);
            eventDispatcher = new AudioRendererEventListener.EventDispatcher(handler, eventListener);
            decoderCounters = new DecoderCounters();
        }

        @Override
        protected void onEnabled(boolean joining) throws ExoPlaybackException {
            super.onEnabled(joining);
            eventDispatcher.enabled(decoderCounters);
            notifiedAudioSessionId = false;
        }

        @Override
        protected void onDisabled() {
            super.onDisabled();
            eventDispatcher.disabled(decoderCounters);
        }

        @Override
        protected void onPositionReset(long positionUs, boolean joining) throws ExoPlaybackException {
            super.onPositionReset(positionUs, joining);
        }

        @Override
        protected void onFormatChanged(Format format) {
            eventDispatcher.inputFormatChanged(format);
            /* decoderName= */
            /* initializedTimestampMs= */
            /* initializationDurationMs= */
            eventDispatcher.decoderInitialized("fake.audio.decoder", SystemClock.elapsedRealtime(), 0);
        }

        @Override
        protected void onBufferRead() {
            if (!(notifiedAudioSessionId)) {
                /* audioSessionId= */
                eventDispatcher.audioSessionId(1);
                notifiedAudioSessionId = true;
            }
        }
    }

    private static final class EventWindowAndPeriodId {
        private final int windowIndex;

        @Nullable
        private final MediaPeriodId mediaPeriodId;

        public EventWindowAndPeriodId(int windowIndex, @Nullable
        MediaPeriodId mediaPeriodId) {
            this.windowIndex = windowIndex;
            this.mediaPeriodId = mediaPeriodId;
        }

        @Override
        public boolean equals(@Nullable
        Object other) {
            if (!(other instanceof AnalyticsCollectorTest.EventWindowAndPeriodId)) {
                return false;
            }
            AnalyticsCollectorTest.EventWindowAndPeriodId event = ((AnalyticsCollectorTest.EventWindowAndPeriodId) (other));
            return ((windowIndex) == (event.windowIndex)) && (Util.areEqual(mediaPeriodId, event.mediaPeriodId));
        }

        @Override
        public String toString() {
            return (mediaPeriodId) != null ? (((((("Event{" + "window=") + (windowIndex)) + ", period=") + (mediaPeriodId.periodUid)) + ", sequence=") + (mediaPeriodId.windowSequenceNumber)) + '}' : (("Event{" + "window=") + (windowIndex)) + ", period = null}";
        }

        @Override
        public int hashCode() {
            return (31 * (windowIndex)) + ((mediaPeriodId) == null ? 0 : mediaPeriodId.hashCode());
        }
    }

    private static final class TestAnalyticsListener implements AnalyticsListener {
        public Timeline lastReportedTimeline;

        private final ArrayList<AnalyticsCollectorTest.TestAnalyticsListener.ReportedEvent> reportedEvents;

        public TestAnalyticsListener() {
            reportedEvents = new ArrayList<>();
            lastReportedTimeline = Timeline.EMPTY;
        }

        public List<AnalyticsCollectorTest.EventWindowAndPeriodId> getEvents(int eventType) {
            ArrayList<AnalyticsCollectorTest.EventWindowAndPeriodId> eventTimes = new ArrayList<>();
            Iterator<AnalyticsCollectorTest.TestAnalyticsListener.ReportedEvent> eventIterator = reportedEvents.iterator();
            while (eventIterator.hasNext()) {
                AnalyticsCollectorTest.TestAnalyticsListener.ReportedEvent event = eventIterator.next();
                if ((event.eventType) == eventType) {
                    eventTimes.add(event.eventWindowAndPeriodId);
                    eventIterator.remove();
                }
            } 
            return eventTimes;
        }

        public void assertNoMoreEvents() {
            assertThat(reportedEvents).isEmpty();
        }

        @Override
        public void onPlayerStateChanged(EventTime eventTime, boolean playWhenReady, int playbackState) {
            reportedEvents.add(new AnalyticsCollectorTest.TestAnalyticsListener.ReportedEvent(AnalyticsCollectorTest.EVENT_PLAYER_STATE_CHANGED, eventTime));
        }

        @Override
        public void onTimelineChanged(EventTime eventTime, int reason) {
            lastReportedTimeline = eventTime.timeline;
            reportedEvents.add(new AnalyticsCollectorTest.TestAnalyticsListener.ReportedEvent(AnalyticsCollectorTest.EVENT_TIMELINE_CHANGED, eventTime));
        }

        @Override
        public void onPositionDiscontinuity(EventTime eventTime, int reason) {
            reportedEvents.add(new AnalyticsCollectorTest.TestAnalyticsListener.ReportedEvent(AnalyticsCollectorTest.EVENT_POSITION_DISCONTINUITY, eventTime));
        }

        @Override
        public void onSeekStarted(EventTime eventTime) {
            reportedEvents.add(new AnalyticsCollectorTest.TestAnalyticsListener.ReportedEvent(AnalyticsCollectorTest.EVENT_SEEK_STARTED, eventTime));
        }

        @Override
        public void onSeekProcessed(EventTime eventTime) {
            reportedEvents.add(new AnalyticsCollectorTest.TestAnalyticsListener.ReportedEvent(AnalyticsCollectorTest.EVENT_SEEK_PROCESSED, eventTime));
        }

        @Override
        public void onPlaybackParametersChanged(EventTime eventTime, PlaybackParameters playbackParameters) {
            reportedEvents.add(new AnalyticsCollectorTest.TestAnalyticsListener.ReportedEvent(AnalyticsCollectorTest.EVENT_PLAYBACK_PARAMETERS_CHANGED, eventTime));
        }

        @Override
        public void onRepeatModeChanged(EventTime eventTime, int repeatMode) {
            reportedEvents.add(new AnalyticsCollectorTest.TestAnalyticsListener.ReportedEvent(AnalyticsCollectorTest.EVENT_REPEAT_MODE_CHANGED, eventTime));
        }

        @Override
        public void onShuffleModeChanged(EventTime eventTime, boolean shuffleModeEnabled) {
            reportedEvents.add(new AnalyticsCollectorTest.TestAnalyticsListener.ReportedEvent(AnalyticsCollectorTest.EVENT_SHUFFLE_MODE_CHANGED, eventTime));
        }

        @Override
        public void onLoadingChanged(EventTime eventTime, boolean isLoading) {
            reportedEvents.add(new AnalyticsCollectorTest.TestAnalyticsListener.ReportedEvent(AnalyticsCollectorTest.EVENT_LOADING_CHANGED, eventTime));
        }

        @Override
        public void onPlayerError(EventTime eventTime, ExoPlaybackException error) {
            reportedEvents.add(new AnalyticsCollectorTest.TestAnalyticsListener.ReportedEvent(AnalyticsCollectorTest.EVENT_PLAYER_ERROR, eventTime));
        }

        @Override
        public void onTracksChanged(EventTime eventTime, TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
            reportedEvents.add(new AnalyticsCollectorTest.TestAnalyticsListener.ReportedEvent(AnalyticsCollectorTest.EVENT_TRACKS_CHANGED, eventTime));
        }

        @Override
        public void onLoadStarted(EventTime eventTime, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
            reportedEvents.add(new AnalyticsCollectorTest.TestAnalyticsListener.ReportedEvent(AnalyticsCollectorTest.EVENT_LOAD_STARTED, eventTime));
        }

        @Override
        public void onLoadCompleted(EventTime eventTime, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
            reportedEvents.add(new AnalyticsCollectorTest.TestAnalyticsListener.ReportedEvent(AnalyticsCollectorTest.EVENT_LOAD_COMPLETED, eventTime));
        }

        @Override
        public void onLoadCanceled(EventTime eventTime, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
            reportedEvents.add(new AnalyticsCollectorTest.TestAnalyticsListener.ReportedEvent(AnalyticsCollectorTest.EVENT_LOAD_CANCELED, eventTime));
        }

        @Override
        public void onLoadError(EventTime eventTime, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData, IOException error, boolean wasCanceled) {
            reportedEvents.add(new AnalyticsCollectorTest.TestAnalyticsListener.ReportedEvent(AnalyticsCollectorTest.EVENT_LOAD_ERROR, eventTime));
        }

        @Override
        public void onDownstreamFormatChanged(EventTime eventTime, MediaLoadData mediaLoadData) {
            reportedEvents.add(new AnalyticsCollectorTest.TestAnalyticsListener.ReportedEvent(AnalyticsCollectorTest.EVENT_DOWNSTREAM_FORMAT_CHANGED, eventTime));
        }

        @Override
        public void onUpstreamDiscarded(EventTime eventTime, MediaLoadData mediaLoadData) {
            reportedEvents.add(new AnalyticsCollectorTest.TestAnalyticsListener.ReportedEvent(AnalyticsCollectorTest.EVENT_UPSTREAM_DISCARDED, eventTime));
        }

        @Override
        public void onMediaPeriodCreated(EventTime eventTime) {
            reportedEvents.add(new AnalyticsCollectorTest.TestAnalyticsListener.ReportedEvent(AnalyticsCollectorTest.EVENT_MEDIA_PERIOD_CREATED, eventTime));
        }

        @Override
        public void onMediaPeriodReleased(EventTime eventTime) {
            reportedEvents.add(new AnalyticsCollectorTest.TestAnalyticsListener.ReportedEvent(AnalyticsCollectorTest.EVENT_MEDIA_PERIOD_RELEASED, eventTime));
        }

        @Override
        public void onReadingStarted(EventTime eventTime) {
            reportedEvents.add(new AnalyticsCollectorTest.TestAnalyticsListener.ReportedEvent(AnalyticsCollectorTest.EVENT_READING_STARTED, eventTime));
        }

        @Override
        public void onBandwidthEstimate(EventTime eventTime, int totalLoadTimeMs, long totalBytesLoaded, long bitrateEstimate) {
            reportedEvents.add(new AnalyticsCollectorTest.TestAnalyticsListener.ReportedEvent(AnalyticsCollectorTest.EVENT_BANDWIDTH_ESTIMATE, eventTime));
        }

        @Override
        public void onSurfaceSizeChanged(EventTime eventTime, int width, int height) {
            reportedEvents.add(new AnalyticsCollectorTest.TestAnalyticsListener.ReportedEvent(AnalyticsCollectorTest.EVENT_SURFACE_SIZE_CHANGED, eventTime));
        }

        @Override
        public void onMetadata(EventTime eventTime, Metadata metadata) {
            reportedEvents.add(new AnalyticsCollectorTest.TestAnalyticsListener.ReportedEvent(AnalyticsCollectorTest.EVENT_METADATA, eventTime));
        }

        @Override
        public void onDecoderEnabled(EventTime eventTime, int trackType, DecoderCounters decoderCounters) {
            reportedEvents.add(new AnalyticsCollectorTest.TestAnalyticsListener.ReportedEvent(AnalyticsCollectorTest.EVENT_DECODER_ENABLED, eventTime));
        }

        @Override
        public void onDecoderInitialized(EventTime eventTime, int trackType, String decoderName, long initializationDurationMs) {
            reportedEvents.add(new AnalyticsCollectorTest.TestAnalyticsListener.ReportedEvent(AnalyticsCollectorTest.EVENT_DECODER_INIT, eventTime));
        }

        @Override
        public void onDecoderInputFormatChanged(EventTime eventTime, int trackType, Format format) {
            reportedEvents.add(new AnalyticsCollectorTest.TestAnalyticsListener.ReportedEvent(AnalyticsCollectorTest.EVENT_DECODER_FORMAT_CHANGED, eventTime));
        }

        @Override
        public void onDecoderDisabled(EventTime eventTime, int trackType, DecoderCounters decoderCounters) {
            reportedEvents.add(new AnalyticsCollectorTest.TestAnalyticsListener.ReportedEvent(AnalyticsCollectorTest.EVENT_DECODER_DISABLED, eventTime));
        }

        @Override
        public void onAudioSessionId(EventTime eventTime, int audioSessionId) {
            reportedEvents.add(new AnalyticsCollectorTest.TestAnalyticsListener.ReportedEvent(AnalyticsCollectorTest.EVENT_AUDIO_SESSION_ID, eventTime));
        }

        @Override
        public void onAudioUnderrun(EventTime eventTime, int bufferSize, long bufferSizeMs, long elapsedSinceLastFeedMs) {
            reportedEvents.add(new AnalyticsCollectorTest.TestAnalyticsListener.ReportedEvent(AnalyticsCollectorTest.EVENT_AUDIO_UNDERRUN, eventTime));
        }

        @Override
        public void onDroppedVideoFrames(EventTime eventTime, int droppedFrames, long elapsedMs) {
            reportedEvents.add(new AnalyticsCollectorTest.TestAnalyticsListener.ReportedEvent(AnalyticsCollectorTest.EVENT_DROPPED_VIDEO_FRAMES, eventTime));
        }

        @Override
        public void onVideoSizeChanged(EventTime eventTime, int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
            reportedEvents.add(new AnalyticsCollectorTest.TestAnalyticsListener.ReportedEvent(AnalyticsCollectorTest.EVENT_VIDEO_SIZE_CHANGED, eventTime));
        }

        @Override
        public void onRenderedFirstFrame(EventTime eventTime, Surface surface) {
            reportedEvents.add(new AnalyticsCollectorTest.TestAnalyticsListener.ReportedEvent(AnalyticsCollectorTest.EVENT_RENDERED_FIRST_FRAME, eventTime));
        }

        @Override
        public void onDrmSessionAcquired(EventTime eventTime) {
            reportedEvents.add(new AnalyticsCollectorTest.TestAnalyticsListener.ReportedEvent(AnalyticsCollectorTest.EVENT_DRM_SESSION_ACQUIRED, eventTime));
        }

        @Override
        public void onDrmKeysLoaded(EventTime eventTime) {
            reportedEvents.add(new AnalyticsCollectorTest.TestAnalyticsListener.ReportedEvent(AnalyticsCollectorTest.EVENT_DRM_KEYS_LOADED, eventTime));
        }

        @Override
        public void onDrmSessionManagerError(EventTime eventTime, Exception error) {
            reportedEvents.add(new AnalyticsCollectorTest.TestAnalyticsListener.ReportedEvent(AnalyticsCollectorTest.EVENT_DRM_ERROR, eventTime));
        }

        @Override
        public void onDrmKeysRestored(EventTime eventTime) {
            reportedEvents.add(new AnalyticsCollectorTest.TestAnalyticsListener.ReportedEvent(AnalyticsCollectorTest.EVENT_DRM_KEYS_RESTORED, eventTime));
        }

        @Override
        public void onDrmKeysRemoved(EventTime eventTime) {
            reportedEvents.add(new AnalyticsCollectorTest.TestAnalyticsListener.ReportedEvent(AnalyticsCollectorTest.EVENT_DRM_KEYS_REMOVED, eventTime));
        }

        @Override
        public void onDrmSessionReleased(EventTime eventTime) {
            reportedEvents.add(new AnalyticsCollectorTest.TestAnalyticsListener.ReportedEvent(AnalyticsCollectorTest.EVENT_DRM_SESSION_RELEASED, eventTime));
        }

        private static final class ReportedEvent {
            public final int eventType;

            public final AnalyticsCollectorTest.EventWindowAndPeriodId eventWindowAndPeriodId;

            public ReportedEvent(int eventType, EventTime eventTime) {
                this.eventType = eventType;
                this.eventWindowAndPeriodId = new AnalyticsCollectorTest.EventWindowAndPeriodId(eventTime.windowIndex, eventTime.mediaPeriodId);
            }
        }
    }
}

