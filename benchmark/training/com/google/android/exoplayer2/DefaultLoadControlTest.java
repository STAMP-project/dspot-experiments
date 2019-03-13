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
package com.google.android.exoplayer2;


import DefaultLoadControl.DEFAULT_MAX_BUFFER_MS;
import DefaultLoadControl.DEFAULT_MIN_BUFFER_MS;
import com.google.android.exoplayer2.DefaultLoadControl.Builder;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static C.DEFAULT_BUFFER_SEGMENT_SIZE;


/**
 * Unit tests for {@link DefaultLoadControl}.
 */
@RunWith(RobolectricTestRunner.class)
public class DefaultLoadControlTest {
    private static final float SPEED = 1.0F;

    private static final long MIN_BUFFER_US = C.msToUs(DEFAULT_MIN_BUFFER_MS);

    private static final long MAX_BUFFER_US = C.msToUs(DEFAULT_MAX_BUFFER_MS);

    private static final int TARGET_BUFFER_BYTES = (DEFAULT_BUFFER_SEGMENT_SIZE) * 2;

    private Builder builder;

    private DefaultAllocator allocator;

    private DefaultLoadControl loadControl;

    @Test
    public void testShouldContinueLoading_untilMaxBufferExceeded() {
        createDefaultLoadControl();
        assertThat(/* bufferedDurationUs= */
        loadControl.shouldContinueLoading(0, DefaultLoadControlTest.SPEED)).isTrue();
        assertThat(loadControl.shouldContinueLoading(DefaultLoadControlTest.MIN_BUFFER_US, DefaultLoadControlTest.SPEED)).isTrue();
        assertThat(loadControl.shouldContinueLoading(((DefaultLoadControlTest.MAX_BUFFER_US) - 1), DefaultLoadControlTest.SPEED)).isTrue();
        assertThat(loadControl.shouldContinueLoading(DefaultLoadControlTest.MAX_BUFFER_US, DefaultLoadControlTest.SPEED)).isFalse();
    }

    @Test
    public void testShouldNotContinueLoadingOnceBufferingStopped_untilBelowMinBuffer() {
        createDefaultLoadControl();
        assertThat(loadControl.shouldContinueLoading(DefaultLoadControlTest.MAX_BUFFER_US, DefaultLoadControlTest.SPEED)).isFalse();
        assertThat(loadControl.shouldContinueLoading(((DefaultLoadControlTest.MAX_BUFFER_US) - 1), DefaultLoadControlTest.SPEED)).isFalse();
        assertThat(loadControl.shouldContinueLoading(DefaultLoadControlTest.MIN_BUFFER_US, DefaultLoadControlTest.SPEED)).isFalse();
        assertThat(loadControl.shouldContinueLoading(((DefaultLoadControlTest.MIN_BUFFER_US) - 1), DefaultLoadControlTest.SPEED)).isTrue();
    }

    @Test
    public void testShouldContinueLoadingWithTargetBufferBytesReached_untilMinBufferReached() {
        createDefaultLoadControl();
        makeSureTargetBufferBytesReached();
        assertThat(/* bufferedDurationUs= */
        loadControl.shouldContinueLoading(0, DefaultLoadControlTest.SPEED)).isTrue();
        assertThat(loadControl.shouldContinueLoading(((DefaultLoadControlTest.MIN_BUFFER_US) - 1), DefaultLoadControlTest.SPEED)).isTrue();
        assertThat(loadControl.shouldContinueLoading(DefaultLoadControlTest.MIN_BUFFER_US, DefaultLoadControlTest.SPEED)).isFalse();
        assertThat(loadControl.shouldContinueLoading(DefaultLoadControlTest.MAX_BUFFER_US, DefaultLoadControlTest.SPEED)).isFalse();
    }

    @Test
    public void testShouldNeverContinueLoading_ifMaxBufferReachedAndNotPrioritizeTimeOverSize() {
        builder.setPrioritizeTimeOverSizeThresholds(false);
        createDefaultLoadControl();
        // Put loadControl in buffering state.
        assertThat(/* bufferedDurationUs= */
        loadControl.shouldContinueLoading(0, DefaultLoadControlTest.SPEED)).isTrue();
        makeSureTargetBufferBytesReached();
        assertThat(/* bufferedDurationUs= */
        loadControl.shouldContinueLoading(0, DefaultLoadControlTest.SPEED)).isFalse();
        assertThat(loadControl.shouldContinueLoading(DefaultLoadControlTest.MIN_BUFFER_US, DefaultLoadControlTest.SPEED)).isFalse();
        assertThat(loadControl.shouldContinueLoading(DefaultLoadControlTest.MAX_BUFFER_US, DefaultLoadControlTest.SPEED)).isFalse();
    }

    @Test
    public void testShouldContinueLoadingWithMinBufferReached_inFastPlayback() {
        createDefaultLoadControl();
        // At normal playback speed, we stop buffering when the buffer reaches the minimum.
        assertThat(loadControl.shouldContinueLoading(DefaultLoadControlTest.MIN_BUFFER_US, DefaultLoadControlTest.SPEED)).isFalse();
        // At double playback speed, we continue loading.
        assertThat(/* playbackSpeed= */
        loadControl.shouldContinueLoading(DefaultLoadControlTest.MIN_BUFFER_US, 2.0F)).isTrue();
    }

    @Test
    public void testShouldNotContinueLoadingWithMaxBufferReached_inFastPlayback() {
        createDefaultLoadControl();
        assertThat(/* playbackSpeed= */
        loadControl.shouldContinueLoading(DefaultLoadControlTest.MAX_BUFFER_US, 100.0F)).isFalse();
    }

    @Test
    public void testStartsPlayback_whenMinBufferSizeReached() {
        createDefaultLoadControl();
        assertThat(/* rebuffering= */
        loadControl.shouldStartPlayback(DefaultLoadControlTest.MIN_BUFFER_US, DefaultLoadControlTest.SPEED, false)).isTrue();
    }
}

