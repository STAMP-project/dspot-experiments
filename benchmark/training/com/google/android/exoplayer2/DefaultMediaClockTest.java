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
package com.google.android.exoplayer2;


import PlaybackParameters.DEFAULT;
import com.google.android.exoplayer2.DefaultMediaClock.PlaybackParameterListener;
import com.google.android.exoplayer2.testutil.FakeClock;
import com.google.android.exoplayer2.testutil.FakeMediaClockRenderer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;

import static PlaybackParameters.DEFAULT;


/**
 * Unit test for {@link DefaultMediaClock}.
 */
@RunWith(RobolectricTestRunner.class)
public class DefaultMediaClockTest {
    private static final long TEST_POSITION_US = 123456789012345678L;

    private static final long SLEEP_TIME_MS = 1000;

    private static final PlaybackParameters TEST_PLAYBACK_PARAMETERS = /* speed= */
    new PlaybackParameters(2.0F);

    @Mock
    private PlaybackParameterListener listener;

    private FakeClock fakeClock;

    private DefaultMediaClock mediaClock;

    @Test
    public void standaloneResetPosition_getPositionShouldReturnSameValue() throws Exception {
        mediaClock.resetPosition(DefaultMediaClockTest.TEST_POSITION_US);
        assertThat(mediaClock.syncAndGetPositionUs()).isEqualTo(DefaultMediaClockTest.TEST_POSITION_US);
    }

    @Test
    public void standaloneGetAndResetPosition_shouldNotTriggerCallback() throws Exception {
        mediaClock.resetPosition(DefaultMediaClockTest.TEST_POSITION_US);
        mediaClock.syncAndGetPositionUs();
        Mockito.verifyNoMoreInteractions(listener);
    }

    @Test
    public void standaloneClock_shouldNotAutoStart() throws Exception {
        assertClockIsStopped();
    }

    @Test
    public void standaloneResetPosition_shouldNotStartClock() throws Exception {
        mediaClock.resetPosition(DefaultMediaClockTest.TEST_POSITION_US);
        assertClockIsStopped();
    }

    @Test
    public void standaloneStart_shouldStartClock() throws Exception {
        mediaClock.start();
        assertClockIsRunning();
    }

    @Test
    public void standaloneStop_shouldKeepClockStopped() throws Exception {
        mediaClock.stop();
        assertClockIsStopped();
    }

    @Test
    public void standaloneStartAndStop_shouldStopClock() throws Exception {
        mediaClock.start();
        mediaClock.stop();
        assertClockIsStopped();
    }

    @Test
    public void standaloneStartStopStart_shouldRestartClock() throws Exception {
        mediaClock.start();
        mediaClock.stop();
        mediaClock.start();
        assertClockIsRunning();
    }

    @Test
    public void standaloneStartAndStop_shouldNotTriggerCallback() throws Exception {
        mediaClock.start();
        mediaClock.stop();
        Mockito.verifyNoMoreInteractions(listener);
    }

    @Test
    public void standaloneGetPlaybackParameters_initializedWithDefaultPlaybackParameters() {
        assertThat(mediaClock.getPlaybackParameters()).isEqualTo(DEFAULT);
    }

    @Test
    public void standaloneSetPlaybackParameters_getPlaybackParametersShouldReturnSameValue() {
        PlaybackParameters parameters = mediaClock.setPlaybackParameters(DefaultMediaClockTest.TEST_PLAYBACK_PARAMETERS);
        assertThat(parameters).isEqualTo(DefaultMediaClockTest.TEST_PLAYBACK_PARAMETERS);
        assertThat(mediaClock.getPlaybackParameters()).isEqualTo(DefaultMediaClockTest.TEST_PLAYBACK_PARAMETERS);
    }

    @Test
    public void standaloneSetPlaybackParameters_shouldTriggerCallback() {
        mediaClock.setPlaybackParameters(DefaultMediaClockTest.TEST_PLAYBACK_PARAMETERS);
        Mockito.verify(listener).onPlaybackParametersChanged(DefaultMediaClockTest.TEST_PLAYBACK_PARAMETERS);
    }

    @Test
    public void standaloneSetPlaybackParameters_shouldApplyNewPlaybackSpeed() {
        mediaClock.setPlaybackParameters(DefaultMediaClockTest.TEST_PLAYBACK_PARAMETERS);
        mediaClock.start();
        // Asserts that clock is running with speed declared in getPlaybackParameters().
        assertClockIsRunning();
    }

    @Test
    public void standaloneSetOtherPlaybackParameters_getPlaybackParametersShouldReturnSameValue() {
        mediaClock.setPlaybackParameters(DefaultMediaClockTest.TEST_PLAYBACK_PARAMETERS);
        PlaybackParameters parameters = mediaClock.setPlaybackParameters(DEFAULT);
        assertThat(parameters).isEqualTo(DEFAULT);
        assertThat(mediaClock.getPlaybackParameters()).isEqualTo(DEFAULT);
    }

    @Test
    public void standaloneSetOtherPlaybackParameters_shouldTriggerCallbackAgain() {
        mediaClock.setPlaybackParameters(DefaultMediaClockTest.TEST_PLAYBACK_PARAMETERS);
        mediaClock.setPlaybackParameters(DEFAULT);
        Mockito.verify(listener).onPlaybackParametersChanged(DEFAULT);
    }

    @Test
    public void standaloneSetSamePlaybackParametersAgain_shouldTriggerCallbackAgain() {
        mediaClock.setPlaybackParameters(DefaultMediaClockTest.TEST_PLAYBACK_PARAMETERS);
        mediaClock.setPlaybackParameters(DefaultMediaClockTest.TEST_PLAYBACK_PARAMETERS);
        Mockito.verify(listener, Mockito.times(2)).onPlaybackParametersChanged(DefaultMediaClockTest.TEST_PLAYBACK_PARAMETERS);
    }

    @Test
    public void enableRendererMediaClock_shouldOverwriteRendererPlaybackParametersIfPossible() throws ExoPlaybackException {
        FakeMediaClockRenderer mediaClockRenderer = /* playbackParametersAreMutable= */
        new DefaultMediaClockTest.MediaClockRenderer(DefaultMediaClockTest.TEST_PLAYBACK_PARAMETERS, true);
        mediaClock.onRendererEnabled(mediaClockRenderer);
        assertThat(mediaClock.getPlaybackParameters()).isEqualTo(DEFAULT);
        Mockito.verifyNoMoreInteractions(listener);
    }

    @Test
    public void enableRendererMediaClockWithFixedParameters_usesRendererPlaybackParameters() throws ExoPlaybackException {
        FakeMediaClockRenderer mediaClockRenderer = /* playbackParametersAreMutable= */
        new DefaultMediaClockTest.MediaClockRenderer(DefaultMediaClockTest.TEST_PLAYBACK_PARAMETERS, false);
        mediaClock.onRendererEnabled(mediaClockRenderer);
        assertThat(mediaClock.getPlaybackParameters()).isEqualTo(DefaultMediaClockTest.TEST_PLAYBACK_PARAMETERS);
    }

    @Test
    public void enableRendererMediaClockWithFixedParameters_shouldTriggerCallback() throws ExoPlaybackException {
        FakeMediaClockRenderer mediaClockRenderer = /* playbackParametersAreMutable= */
        new DefaultMediaClockTest.MediaClockRenderer(DefaultMediaClockTest.TEST_PLAYBACK_PARAMETERS, false);
        mediaClock.onRendererEnabled(mediaClockRenderer);
        Mockito.verify(listener).onPlaybackParametersChanged(DefaultMediaClockTest.TEST_PLAYBACK_PARAMETERS);
    }

    @Test
    public void enableRendererMediaClockWithFixedButSamePlaybackParameters_shouldNotTriggerCallback() throws ExoPlaybackException {
        FakeMediaClockRenderer mediaClockRenderer = /* playbackParametersAreMutable= */
        new DefaultMediaClockTest.MediaClockRenderer(DEFAULT, false);
        mediaClock.onRendererEnabled(mediaClockRenderer);
        Mockito.verifyNoMoreInteractions(listener);
    }

    @Test
    public void disableRendererMediaClock_shouldKeepPlaybackParameters() throws ExoPlaybackException {
        FakeMediaClockRenderer mediaClockRenderer = /* playbackParametersAreMutable= */
        new DefaultMediaClockTest.MediaClockRenderer(DefaultMediaClockTest.TEST_PLAYBACK_PARAMETERS, false);
        mediaClock.onRendererEnabled(mediaClockRenderer);
        mediaClock.onRendererDisabled(mediaClockRenderer);
        assertThat(mediaClock.getPlaybackParameters()).isEqualTo(DefaultMediaClockTest.TEST_PLAYBACK_PARAMETERS);
    }

    @Test
    public void rendererClockSetPlaybackParameters_getPlaybackParametersShouldReturnSameValue() throws ExoPlaybackException {
        FakeMediaClockRenderer mediaClockRenderer = /* playbackParametersAreMutable= */
        new DefaultMediaClockTest.MediaClockRenderer(DEFAULT, true);
        mediaClock.onRendererEnabled(mediaClockRenderer);
        PlaybackParameters parameters = mediaClock.setPlaybackParameters(DefaultMediaClockTest.TEST_PLAYBACK_PARAMETERS);
        assertThat(parameters).isEqualTo(DefaultMediaClockTest.TEST_PLAYBACK_PARAMETERS);
        assertThat(mediaClock.getPlaybackParameters()).isEqualTo(DefaultMediaClockTest.TEST_PLAYBACK_PARAMETERS);
    }

    @Test
    public void rendererClockSetPlaybackParameters_shouldTriggerCallback() throws ExoPlaybackException {
        FakeMediaClockRenderer mediaClockRenderer = /* playbackParametersAreMutable= */
        new DefaultMediaClockTest.MediaClockRenderer(DEFAULT, true);
        mediaClock.onRendererEnabled(mediaClockRenderer);
        mediaClock.setPlaybackParameters(DefaultMediaClockTest.TEST_PLAYBACK_PARAMETERS);
        Mockito.verify(listener).onPlaybackParametersChanged(DefaultMediaClockTest.TEST_PLAYBACK_PARAMETERS);
    }

    @Test
    public void rendererClockSetPlaybackParametersOverwrite_getParametersShouldReturnSameValue() throws ExoPlaybackException {
        FakeMediaClockRenderer mediaClockRenderer = /* playbackParametersAreMutable= */
        new DefaultMediaClockTest.MediaClockRenderer(DEFAULT, false);
        mediaClock.onRendererEnabled(mediaClockRenderer);
        PlaybackParameters parameters = mediaClock.setPlaybackParameters(DefaultMediaClockTest.TEST_PLAYBACK_PARAMETERS);
        assertThat(parameters).isEqualTo(DEFAULT);
        assertThat(mediaClock.getPlaybackParameters()).isEqualTo(DEFAULT);
    }

    @Test
    public void rendererClockSetPlaybackParametersOverwrite_shouldTriggerCallback() throws ExoPlaybackException {
        FakeMediaClockRenderer mediaClockRenderer = /* playbackParametersAreMutable= */
        new DefaultMediaClockTest.MediaClockRenderer(DEFAULT, false);
        mediaClock.onRendererEnabled(mediaClockRenderer);
        mediaClock.setPlaybackParameters(DefaultMediaClockTest.TEST_PLAYBACK_PARAMETERS);
        Mockito.verify(listener).onPlaybackParametersChanged(DEFAULT);
    }

    @Test
    public void enableRendererMediaClock_usesRendererClockPosition() throws ExoPlaybackException {
        DefaultMediaClockTest.MediaClockRenderer mediaClockRenderer = new DefaultMediaClockTest.MediaClockRenderer();
        mediaClock.start();
        mediaClock.onRendererEnabled(mediaClockRenderer);
        mediaClockRenderer.positionUs = DefaultMediaClockTest.TEST_POSITION_US;
        assertThat(mediaClock.syncAndGetPositionUs()).isEqualTo(DefaultMediaClockTest.TEST_POSITION_US);
        // We're not advancing the renderer media clock. Thus, the clock should appear to be stopped.
        assertClockIsStopped();
    }

    @Test
    public void resetPositionWhileUsingRendererMediaClock_shouldHaveNoEffect() throws ExoPlaybackException {
        DefaultMediaClockTest.MediaClockRenderer mediaClockRenderer = new DefaultMediaClockTest.MediaClockRenderer();
        mediaClock.start();
        mediaClock.onRendererEnabled(mediaClockRenderer);
        mediaClockRenderer.positionUs = DefaultMediaClockTest.TEST_POSITION_US;
        assertThat(mediaClock.syncAndGetPositionUs()).isEqualTo(DefaultMediaClockTest.TEST_POSITION_US);
        mediaClock.resetPosition(0);
        assertThat(mediaClock.syncAndGetPositionUs()).isEqualTo(DefaultMediaClockTest.TEST_POSITION_US);
    }

    @Test
    public void disableRendererMediaClock_standaloneShouldBeSynced() throws ExoPlaybackException {
        DefaultMediaClockTest.MediaClockRenderer mediaClockRenderer = new DefaultMediaClockTest.MediaClockRenderer();
        mediaClock.start();
        mediaClock.onRendererEnabled(mediaClockRenderer);
        mediaClockRenderer.positionUs = DefaultMediaClockTest.TEST_POSITION_US;
        mediaClock.syncAndGetPositionUs();
        mediaClock.onRendererDisabled(mediaClockRenderer);
        fakeClock.advanceTime(DefaultMediaClockTest.SLEEP_TIME_MS);
        assertThat(mediaClock.syncAndGetPositionUs()).isEqualTo(((DefaultMediaClockTest.TEST_POSITION_US) + (C.msToUs(DefaultMediaClockTest.SLEEP_TIME_MS))));
        assertClockIsRunning();
    }

    @Test
    public void getPositionWithPlaybackParameterChange_shouldTriggerCallback() throws ExoPlaybackException {
        DefaultMediaClockTest.MediaClockRenderer mediaClockRenderer = /* playbackParametersAreMutable= */
        new DefaultMediaClockTest.MediaClockRenderer(DEFAULT, true);
        mediaClock.onRendererEnabled(mediaClockRenderer);
        // Silently change playback parameters of renderer clock.
        mediaClockRenderer.playbackParameters = DefaultMediaClockTest.TEST_PLAYBACK_PARAMETERS;
        mediaClock.syncAndGetPositionUs();
        Mockito.verify(listener).onPlaybackParametersChanged(DefaultMediaClockTest.TEST_PLAYBACK_PARAMETERS);
    }

    @Test
    public void rendererNotReady_shouldStillUseRendererClock() throws ExoPlaybackException {
        DefaultMediaClockTest.MediaClockRenderer mediaClockRenderer = /* isReady= */
        /* isEnded= */
        /* hasReadStreamToEnd= */
        new DefaultMediaClockTest.MediaClockRenderer(false, false, false);
        mediaClock.start();
        mediaClock.onRendererEnabled(mediaClockRenderer);
        // We're not advancing the renderer media clock. Thus, the clock should appear to be stopped.
        assertClockIsStopped();
    }

    @Test
    public void rendererNotReadyAndReadStreamToEnd_shouldFallbackToStandaloneClock() throws ExoPlaybackException {
        DefaultMediaClockTest.MediaClockRenderer mediaClockRenderer = /* isReady= */
        /* isEnded= */
        /* hasReadStreamToEnd= */
        new DefaultMediaClockTest.MediaClockRenderer(false, false, true);
        mediaClock.start();
        mediaClock.onRendererEnabled(mediaClockRenderer);
        assertClockIsRunning();
    }

    @Test
    public void rendererEnded_shouldFallbackToStandaloneClock() throws ExoPlaybackException {
        DefaultMediaClockTest.MediaClockRenderer mediaClockRenderer = /* isReady= */
        /* isEnded= */
        /* hasReadStreamToEnd= */
        new DefaultMediaClockTest.MediaClockRenderer(true, true, true);
        mediaClock.start();
        mediaClock.onRendererEnabled(mediaClockRenderer);
        assertClockIsRunning();
    }

    @Test
    public void staleDisableRendererClock_shouldNotThrow() throws ExoPlaybackException {
        DefaultMediaClockTest.MediaClockRenderer mediaClockRenderer = new DefaultMediaClockTest.MediaClockRenderer();
        mediaClockRenderer.positionUs = DefaultMediaClockTest.TEST_POSITION_US;
        mediaClock.onRendererDisabled(mediaClockRenderer);
        assertThat(mediaClock.syncAndGetPositionUs()).isEqualTo(C.msToUs(fakeClock.elapsedRealtime()));
    }

    @Test
    public void enableSameRendererClockTwice_shouldNotThrow() throws ExoPlaybackException {
        DefaultMediaClockTest.MediaClockRenderer mediaClockRenderer = new DefaultMediaClockTest.MediaClockRenderer();
        mediaClock.onRendererEnabled(mediaClockRenderer);
        mediaClock.onRendererEnabled(mediaClockRenderer);
        mediaClockRenderer.positionUs = DefaultMediaClockTest.TEST_POSITION_US;
        assertThat(mediaClock.syncAndGetPositionUs()).isEqualTo(DefaultMediaClockTest.TEST_POSITION_US);
    }

    @Test
    public void enableOtherRendererClock_shouldThrow() throws ExoPlaybackException {
        DefaultMediaClockTest.MediaClockRenderer mediaClockRenderer1 = new DefaultMediaClockTest.MediaClockRenderer();
        DefaultMediaClockTest.MediaClockRenderer mediaClockRenderer2 = new DefaultMediaClockTest.MediaClockRenderer();
        mediaClockRenderer1.positionUs = DefaultMediaClockTest.TEST_POSITION_US;
        mediaClock.onRendererEnabled(mediaClockRenderer1);
        try {
            mediaClock.onRendererEnabled(mediaClockRenderer2);
            Assert.fail();
        } catch (ExoPlaybackException e) {
            // Expected.
        }
        assertThat(mediaClock.syncAndGetPositionUs()).isEqualTo(DefaultMediaClockTest.TEST_POSITION_US);
    }

    @SuppressWarnings("HidingField")
    private static class MediaClockRenderer extends FakeMediaClockRenderer {
        private final boolean playbackParametersAreMutable;

        private final boolean isReady;

        private final boolean isEnded;

        public PlaybackParameters playbackParameters;

        public long positionUs;

        public MediaClockRenderer() throws ExoPlaybackException {
            this(DEFAULT, false, true, false, false);
        }

        public MediaClockRenderer(PlaybackParameters playbackParameters, boolean playbackParametersAreMutable) throws ExoPlaybackException {
            this(playbackParameters, playbackParametersAreMutable, true, false, false);
        }

        public MediaClockRenderer(boolean isReady, boolean isEnded, boolean hasReadStreamToEnd) throws ExoPlaybackException {
            this(DEFAULT, false, isReady, isEnded, hasReadStreamToEnd);
        }

        private MediaClockRenderer(PlaybackParameters playbackParameters, boolean playbackParametersAreMutable, boolean isReady, boolean isEnded, boolean hasReadStreamToEnd) throws ExoPlaybackException {
            this.playbackParameters = playbackParameters;
            this.playbackParametersAreMutable = playbackParametersAreMutable;
            this.isReady = isReady;
            this.isEnded = isEnded;
            this.positionUs = DefaultMediaClockTest.TEST_POSITION_US;
            if (!hasReadStreamToEnd) {
                resetPosition(0);
            }
        }

        @Override
        public long getPositionUs() {
            return positionUs;
        }

        @Override
        public PlaybackParameters setPlaybackParameters(PlaybackParameters playbackParameters) {
            if (playbackParametersAreMutable) {
                this.playbackParameters = playbackParameters;
            }
            return this.playbackParameters;
        }

        @Override
        public PlaybackParameters getPlaybackParameters() {
            return playbackParameters;
        }

        @Override
        public boolean isReady() {
            return isReady;
        }

        @Override
        public boolean isEnded() {
            return isEnded;
        }
    }
}

