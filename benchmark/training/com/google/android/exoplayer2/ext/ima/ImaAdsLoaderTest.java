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
package com.google.android.exoplayer2.ext.ima;


import AdEventType.CONTENT_PAUSE_REQUESTED;
import AdEventType.CONTENT_RESUME_REQUESTED;
import AdEventType.FIRST_QUARTILE;
import AdEventType.LOADED;
import AdEventType.MIDPOINT;
import AdEventType.STARTED;
import AdEventType.THIRD_QUARTILE;
import AdLoadException.TYPE_UNEXPECTED;
import AdsLoader.AdViewProvider;
import Player.DISCONTINUITY_REASON_SEEK;
import Player.STATE_READY;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import com.google.ads.interactivemedia.v3.api.AdDisplayContainer;
import com.google.ads.interactivemedia.v3.api.AdsManager;
import com.google.ads.interactivemedia.v3.api.AdsRenderingSettings;
import com.google.ads.interactivemedia.v3.api.ImaSdkSettings;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.SinglePeriodTimeline;
import com.google.android.exoplayer2.source.ads.AdPlaybackState;
import com.google.android.exoplayer2.source.ads.AdsLoader;
import com.google.android.exoplayer2.source.ads.AdsMediaSource.AdLoadException;
import com.google.android.exoplayer2.upstream.DataSpec;
import java.io.IOException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


/**
 * Test for {@link ImaAdsLoader}.
 */
@RunWith(RobolectricTestRunner.class)
public class ImaAdsLoaderTest {
    private static final long CONTENT_DURATION_US = 10 * (C.MICROS_PER_SECOND);

    private static final Timeline CONTENT_TIMELINE = /* isSeekable= */
    /* isDynamic= */
    new SinglePeriodTimeline(ImaAdsLoaderTest.CONTENT_DURATION_US, true, false);

    private static final Uri TEST_URI = Uri.EMPTY;

    private static final long TEST_AD_DURATION_US = 5 * (C.MICROS_PER_SECOND);

    private static final long[][] PREROLL_ADS_DURATIONS_US = new long[][]{ new long[]{ ImaAdsLoaderTest.TEST_AD_DURATION_US } };

    private static final Float[] PREROLL_CUE_POINTS_SECONDS = new Float[]{ 0.0F };

    private static final FakeAd UNSKIPPABLE_AD = /* skippable= */
    /* podIndex= */
    /* totalAds= */
    /* adPosition= */
    new FakeAd(false, 0, 1, 1);

    @Mock
    private ImaSdkSettings imaSdkSettings;

    @Mock
    private AdsRenderingSettings adsRenderingSettings;

    @Mock
    private AdDisplayContainer adDisplayContainer;

    @Mock
    private AdsManager adsManager;

    private SingletonImaFactory testImaFactory;

    private ViewGroup adViewGroup;

    private View adOverlayView;

    private AdViewProvider adViewProvider;

    private ImaAdsLoaderTest.TestAdsLoaderListener adsLoaderListener;

    private FakePlayer fakeExoPlayer;

    private ImaAdsLoader imaAdsLoader;

    @Test
    public void testBuilder_overridesPlayerType() {
        Mockito.when(imaSdkSettings.getPlayerType()).thenReturn("test player type");
        setupPlayback(ImaAdsLoaderTest.CONTENT_TIMELINE, ImaAdsLoaderTest.PREROLL_ADS_DURATIONS_US, ImaAdsLoaderTest.PREROLL_CUE_POINTS_SECONDS);
        Mockito.verify(imaSdkSettings).setPlayerType("google/exo.ext.ima");
    }

    @Test
    public void testStart_setsAdUiViewGroup() {
        setupPlayback(ImaAdsLoaderTest.CONTENT_TIMELINE, ImaAdsLoaderTest.PREROLL_ADS_DURATIONS_US, ImaAdsLoaderTest.PREROLL_CUE_POINTS_SECONDS);
        imaAdsLoader.start(adsLoaderListener, adViewProvider);
        Mockito.verify(adDisplayContainer, Mockito.atLeastOnce()).setAdContainer(adViewGroup);
        Mockito.verify(adDisplayContainer, Mockito.atLeastOnce()).registerVideoControlsOverlay(adOverlayView);
    }

    @Test
    public void testStart_updatesAdPlaybackState() {
        setupPlayback(ImaAdsLoaderTest.CONTENT_TIMELINE, ImaAdsLoaderTest.PREROLL_ADS_DURATIONS_US, ImaAdsLoaderTest.PREROLL_CUE_POINTS_SECONDS);
        imaAdsLoader.start(adsLoaderListener, adViewProvider);
        assertThat(adsLoaderListener.adPlaybackState).isEqualTo(/* adGroupTimesUs= */
        new AdPlaybackState(0).withAdDurationsUs(ImaAdsLoaderTest.PREROLL_ADS_DURATIONS_US));
    }

    @Test
    public void testStartAfterRelease() {
        setupPlayback(ImaAdsLoaderTest.CONTENT_TIMELINE, ImaAdsLoaderTest.PREROLL_ADS_DURATIONS_US, ImaAdsLoaderTest.PREROLL_CUE_POINTS_SECONDS);
        imaAdsLoader.release();
        imaAdsLoader.start(adsLoaderListener, adViewProvider);
    }

    @Test
    public void testStartAndCallbacksAfterRelease() {
        setupPlayback(ImaAdsLoaderTest.CONTENT_TIMELINE, ImaAdsLoaderTest.PREROLL_ADS_DURATIONS_US, ImaAdsLoaderTest.PREROLL_CUE_POINTS_SECONDS);
        imaAdsLoader.release();
        imaAdsLoader.start(adsLoaderListener, adViewProvider);
        /* position= */
        fakeExoPlayer.setPlayingContentPosition(0);
        fakeExoPlayer.setState(STATE_READY, true);
        // If callbacks are invoked there is no crash.
        // Note: we can't currently call getContentProgress/getAdProgress as a VerifyError is thrown
        // when using Robolectric and accessing VideoProgressUpdate.VIDEO_TIME_NOT_READY, due to the IMA
        // SDK being proguarded.
        imaAdsLoader.requestAds(adViewGroup);
        imaAdsLoader.onAdEvent(ImaAdsLoaderTest.getAdEvent(LOADED, ImaAdsLoaderTest.UNSKIPPABLE_AD));
        imaAdsLoader.loadAd(ImaAdsLoaderTest.TEST_URI.toString());
        imaAdsLoader.onAdEvent(ImaAdsLoaderTest.getAdEvent(CONTENT_PAUSE_REQUESTED, ImaAdsLoaderTest.UNSKIPPABLE_AD));
        imaAdsLoader.playAd();
        imaAdsLoader.onAdEvent(ImaAdsLoaderTest.getAdEvent(STARTED, ImaAdsLoaderTest.UNSKIPPABLE_AD));
        imaAdsLoader.pauseAd();
        imaAdsLoader.stopAd();
        imaAdsLoader.onPlayerError(ExoPlaybackException.createForSource(new IOException()));
        imaAdsLoader.onPositionDiscontinuity(DISCONTINUITY_REASON_SEEK);
        imaAdsLoader.onAdEvent(/* ad= */
        ImaAdsLoaderTest.getAdEvent(CONTENT_RESUME_REQUESTED, null));
        /* adGroupIndex= */
        /* adIndexInAdGroup= */
        imaAdsLoader.handlePrepareError(0, 0, new IOException());
    }

    @Test
    public void testPlayback_withPrerollAd_marksAdAsPlayed() {
        setupPlayback(ImaAdsLoaderTest.CONTENT_TIMELINE, ImaAdsLoaderTest.PREROLL_ADS_DURATIONS_US, ImaAdsLoaderTest.PREROLL_CUE_POINTS_SECONDS);
        // Load the preroll ad.
        imaAdsLoader.start(adsLoaderListener, adViewProvider);
        imaAdsLoader.onAdEvent(ImaAdsLoaderTest.getAdEvent(LOADED, ImaAdsLoaderTest.UNSKIPPABLE_AD));
        imaAdsLoader.loadAd(ImaAdsLoaderTest.TEST_URI.toString());
        imaAdsLoader.onAdEvent(ImaAdsLoaderTest.getAdEvent(CONTENT_PAUSE_REQUESTED, ImaAdsLoaderTest.UNSKIPPABLE_AD));
        // Play the preroll ad.
        imaAdsLoader.playAd();
        /* adGroupIndex= */
        /* adIndexInAdGroup= */
        /* position= */
        /* contentPosition= */
        fakeExoPlayer.setPlayingAdPosition(0, 0, 0, 0);
        fakeExoPlayer.setState(STATE_READY, true);
        imaAdsLoader.onAdEvent(ImaAdsLoaderTest.getAdEvent(STARTED, ImaAdsLoaderTest.UNSKIPPABLE_AD));
        imaAdsLoader.onAdEvent(ImaAdsLoaderTest.getAdEvent(FIRST_QUARTILE, ImaAdsLoaderTest.UNSKIPPABLE_AD));
        imaAdsLoader.onAdEvent(ImaAdsLoaderTest.getAdEvent(MIDPOINT, ImaAdsLoaderTest.UNSKIPPABLE_AD));
        imaAdsLoader.onAdEvent(ImaAdsLoaderTest.getAdEvent(THIRD_QUARTILE, ImaAdsLoaderTest.UNSKIPPABLE_AD));
        // Play the content.
        fakeExoPlayer.setPlayingContentPosition(0);
        imaAdsLoader.stopAd();
        imaAdsLoader.onAdEvent(/* ad= */
        ImaAdsLoaderTest.getAdEvent(CONTENT_RESUME_REQUESTED, null));
        // Verify that the preroll ad has been marked as played.
        assertThat(adsLoaderListener.adPlaybackState).isEqualTo(/* adResumePositionUs= */
        /* adGroupIndex= */
        /* adIndexInAdGroup= */
        /* adGroupIndex= */
        /* adIndexInAdGroup= */
        /* uri= */
        /* adGroupIndex= */
        /* adCount= */
        /* adGroupTimesUs= */
        new AdPlaybackState(0).withContentDurationUs(ImaAdsLoaderTest.CONTENT_DURATION_US).withAdCount(0, 1).withAdUri(0, 0, ImaAdsLoaderTest.TEST_URI).withAdDurationsUs(ImaAdsLoaderTest.PREROLL_ADS_DURATIONS_US).withPlayedAd(0, 0).withAdResumePositionUs(0));
    }

    @Test
    public void testStop_unregistersAllVideoControlOverlays() {
        setupPlayback(ImaAdsLoaderTest.CONTENT_TIMELINE, ImaAdsLoaderTest.PREROLL_ADS_DURATIONS_US, ImaAdsLoaderTest.PREROLL_CUE_POINTS_SECONDS);
        imaAdsLoader.start(adsLoaderListener, adViewProvider);
        imaAdsLoader.requestAds(adViewGroup);
        imaAdsLoader.stop();
        InOrder inOrder = Mockito.inOrder(adDisplayContainer);
        inOrder.verify(adDisplayContainer).registerVideoControlsOverlay(adOverlayView);
        inOrder.verify(adDisplayContainer).unregisterAllVideoControlsOverlays();
    }

    /**
     * Ad loader event listener that forwards ad playback state to a fake player.
     */
    private static final class TestAdsLoaderListener implements AdsLoader.EventListener {
        private final FakePlayer fakeExoPlayer;

        private final Timeline contentTimeline;

        private final long[][] adDurationsUs;

        public AdPlaybackState adPlaybackState;

        public TestAdsLoaderListener(FakePlayer fakeExoPlayer, Timeline contentTimeline, long[][] adDurationsUs) {
            this.fakeExoPlayer = fakeExoPlayer;
            this.contentTimeline = contentTimeline;
            this.adDurationsUs = adDurationsUs;
        }

        @Override
        public void onAdPlaybackState(AdPlaybackState adPlaybackState) {
            adPlaybackState = adPlaybackState.withAdDurationsUs(adDurationsUs);
            this.adPlaybackState = adPlaybackState;
            fakeExoPlayer.updateTimeline(new com.google.android.exoplayer2.source.ads.SinglePeriodAdTimeline(contentTimeline, adPlaybackState));
        }

        @Override
        public void onAdLoadError(AdLoadException error, DataSpec dataSpec) {
            assertThat(error.type).isNotEqualTo(TYPE_UNEXPECTED);
        }

        @Override
        public void onAdClicked() {
            // Do nothing.
        }

        @Override
        public void onAdTapped() {
            // Do nothing.
        }
    }
}

