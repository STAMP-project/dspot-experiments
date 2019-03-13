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
package com.google.android.exoplayer2.extractor;


import C.TIME_UNSET;
import SeekMap.SeekPoints;
import com.google.android.exoplayer2.C;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/**
 * Unit test for {@link ConstantBitrateSeekMap}.
 */
@RunWith(RobolectricTestRunner.class)
public final class ConstantBitrateSeekMapTest {
    private ConstantBitrateSeekMap constantBitrateSeekMap;

    @Test
    public void testIsSeekable_forKnownInputLength_returnSeekable() {
        constantBitrateSeekMap = /* inputLength= */
        /* firstFrameBytePosition= */
        /* bitrate= */
        /* frameSize= */
        new ConstantBitrateSeekMap(1000, 0, 8000, 100);
        assertThat(constantBitrateSeekMap.isSeekable()).isTrue();
    }

    @Test
    public void testIsSeekable_forUnknownInputLength_returnUnseekable() {
        constantBitrateSeekMap = /* inputLength= */
        /* firstFrameBytePosition= */
        /* bitrate= */
        /* frameSize= */
        new ConstantBitrateSeekMap(C.LENGTH_UNSET, 0, 8000, 100);
        assertThat(constantBitrateSeekMap.isSeekable()).isFalse();
    }

    @Test
    public void testGetSeekPoints_forUnseekableInput_returnSeekPoint0() {
        int firstBytePosition = 100;
        constantBitrateSeekMap = /* inputLength= */
        /* firstFrameBytePosition= */
        /* bitrate= */
        /* frameSize= */
        new ConstantBitrateSeekMap(C.LENGTH_UNSET, firstBytePosition, 8000, 100);
        SeekMap.SeekPoints seekPoints = /* timeUs= */
        constantBitrateSeekMap.getSeekPoints(123);
        assertThat(seekPoints.first.timeUs).isEqualTo(0);
        assertThat(seekPoints.first.position).isEqualTo(firstBytePosition);
        assertThat(seekPoints.second).isEqualTo(seekPoints.first);
    }

    @Test
    public void testGetDurationUs_forKnownInputLength_returnCorrectDuration() {
        constantBitrateSeekMap = /* inputLength= */
        /* firstFrameBytePosition= */
        /* bitrate= */
        /* frameSize= */
        new ConstantBitrateSeekMap(2300, 100, 8000, 100);
        // Bitrate = 8000 (bits/s) = 1000 (bytes/s)
        // FrameSize = 100 (bytes), so 1 frame = 1s = 100_000 us
        // Input length = 2300 (bytes), first frame = 100, so duration = 2_200_000 us.
        assertThat(constantBitrateSeekMap.getDurationUs()).isEqualTo(2200000);
    }

    @Test
    public void testGetDurationUs_forUnnnownInputLength_returnUnknownDuration() {
        constantBitrateSeekMap = /* inputLength= */
        /* firstFrameBytePosition= */
        /* bitrate= */
        /* frameSize= */
        new ConstantBitrateSeekMap(C.LENGTH_UNSET, 100, 8000, 100);
        assertThat(constantBitrateSeekMap.getDurationUs()).isEqualTo(TIME_UNSET);
    }

    @Test
    public void testGetSeekPoints_forSeekableInput_forSyncPosition0_return1SeekPoint() {
        int firstBytePosition = 100;
        constantBitrateSeekMap = /* inputLength= */
        /* firstFrameBytePosition= */
        /* bitrate= */
        /* frameSize= */
        new ConstantBitrateSeekMap(2300, firstBytePosition, 8000, 100);
        SeekMap.SeekPoints seekPoints = /* timeUs= */
        constantBitrateSeekMap.getSeekPoints(0);
        assertThat(seekPoints.first.timeUs).isEqualTo(0);
        assertThat(seekPoints.first.position).isEqualTo(firstBytePosition);
        assertThat(seekPoints.second).isEqualTo(seekPoints.first);
    }

    @Test
    public void testGetSeekPoints_forSeekableInput_forSeekPointAtSyncPosition_return1SeekPoint() {
        constantBitrateSeekMap = /* inputLength= */
        /* firstFrameBytePosition= */
        /* bitrate= */
        /* frameSize= */
        new ConstantBitrateSeekMap(2300, 100, 8000, 100);
        SeekMap.SeekPoints seekPoints = /* timeUs= */
        constantBitrateSeekMap.getSeekPoints(1200000);
        // Bitrate = 8000 (bits/s) = 1000 (bytes/s)
        // FrameSize = 100 (bytes), so 1 frame = 1s = 100_000 us
        assertThat(seekPoints.first.timeUs).isEqualTo(1200000);
        assertThat(seekPoints.first.position).isEqualTo(1300);
        assertThat(seekPoints.second).isEqualTo(seekPoints.first);
    }

    @Test
    public void testGetSeekPoints_forSeekableInput_forNonSyncSeekPosition_return2SeekPoints() {
        constantBitrateSeekMap = /* inputLength= */
        /* firstFrameBytePosition= */
        /* bitrate= */
        /* frameSize= */
        new ConstantBitrateSeekMap(2300, 100, 8000, 100);
        SeekMap.SeekPoints seekPoints = /* timeUs= */
        constantBitrateSeekMap.getSeekPoints(345678);
        // Bitrate = 8000 (bits/s) = 1000 (bytes/s)
        // FrameSize = 100 (bytes), so 1 frame = 1s = 100_000 us
        assertThat(seekPoints.first.timeUs).isEqualTo(300000);
        assertThat(seekPoints.first.position).isEqualTo(400);
        assertThat(seekPoints.second.timeUs).isEqualTo(400000);
        assertThat(seekPoints.second.position).isEqualTo(500);
    }

    @Test
    public void testGetSeekPoints_forSeekableInput_forSeekPointWithinLastFrame_return1SeekPoint() {
        constantBitrateSeekMap = /* inputLength= */
        /* firstFrameBytePosition= */
        /* bitrate= */
        /* frameSize= */
        new ConstantBitrateSeekMap(2300, 100, 8000, 100);
        SeekMap.SeekPoints seekPoints = /* timeUs= */
        constantBitrateSeekMap.getSeekPoints(2123456);
        assertThat(seekPoints.first.timeUs).isEqualTo(2100000);
        assertThat(seekPoints.first.position).isEqualTo(2200);
        assertThat(seekPoints.second).isEqualTo(seekPoints.first);
    }

    @Test
    public void testGetSeekPoints_forSeekableInput_forSeekPointAtEndOfStream_return1SeekPoint() {
        constantBitrateSeekMap = /* inputLength= */
        /* firstFrameBytePosition= */
        /* bitrate= */
        /* frameSize= */
        new ConstantBitrateSeekMap(2300, 100, 8000, 100);
        SeekMap.SeekPoints seekPoints = /* timeUs= */
        constantBitrateSeekMap.getSeekPoints(2200000);
        assertThat(seekPoints.first.timeUs).isEqualTo(2100000);
        assertThat(seekPoints.first.position).isEqualTo(2200);
        assertThat(seekPoints.second).isEqualTo(seekPoints.first);
    }

    @Test
    public void testGetTimeUsAtPosition_forPosition0_return0() {
        constantBitrateSeekMap = /* inputLength= */
        /* firstFrameBytePosition= */
        /* bitrate= */
        /* frameSize= */
        new ConstantBitrateSeekMap(2300, 100, 8000, 100);
        long timeUs = constantBitrateSeekMap.getTimeUsAtPosition(0);
        assertThat(timeUs).isEqualTo(0);
    }

    @Test
    public void testGetTimeUsAtPosition_forPositionWithinStream_returnCorrectTime() {
        constantBitrateSeekMap = /* inputLength= */
        /* firstFrameBytePosition= */
        /* bitrate= */
        /* frameSize= */
        new ConstantBitrateSeekMap(2300, 100, 8000, 100);
        long timeUs = constantBitrateSeekMap.getTimeUsAtPosition(1234);
        assertThat(timeUs).isEqualTo(1134000);
    }

    @Test
    public void testGetTimeUsAtPosition_forPositionAtEndOfStream_returnStreamDuration() {
        constantBitrateSeekMap = /* inputLength= */
        /* firstFrameBytePosition= */
        /* bitrate= */
        /* frameSize= */
        new ConstantBitrateSeekMap(2300, 100, 8000, 100);
        long timeUs = constantBitrateSeekMap.getTimeUsAtPosition(2300);
        assertThat(timeUs).isEqualTo(constantBitrateSeekMap.getDurationUs());
    }
}

