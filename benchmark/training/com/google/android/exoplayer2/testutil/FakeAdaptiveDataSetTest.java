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
package com.google.android.exoplayer2.testutil;


import Format.NO_VALUE;
import MimeTypes.VIDEO_H264;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.testutil.FakeDataSet.FakeData;
import java.util.Random;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/**
 * Unit test for {@link FakeAdaptiveDataSet}.
 */
@RunWith(RobolectricTestRunner.class)
public final class FakeAdaptiveDataSetTest {
    private static final Format[] TEST_FORMATS = new Format[]{ Format.createVideoSampleFormat(null, VIDEO_H264, null, 1000000, NO_VALUE, 1280, 720, NO_VALUE, null, null), Format.createVideoSampleFormat(null, VIDEO_H264, null, 300000, NO_VALUE, 640, 360, NO_VALUE, null, null) };

    private static final TrackGroup TRACK_GROUP = new TrackGroup(FakeAdaptiveDataSetTest.TEST_FORMATS);

    @Test
    public void testAdaptiveDataSet() {
        long chunkDuration = 2 * (C.MICROS_PER_SECOND);
        FakeAdaptiveDataSet dataSet = new FakeAdaptiveDataSet(FakeAdaptiveDataSetTest.TRACK_GROUP, (10 * (C.MICROS_PER_SECOND)), chunkDuration, 0.0, new Random(0));
        assertThat(dataSet.getAllData().size()).isEqualTo(FakeAdaptiveDataSetTest.TEST_FORMATS.length);
        assertThat(dataSet.getUri(0).equals(dataSet.getUri(1))).isFalse();
        assertThat(dataSet.getChunkCount()).isEqualTo(5);
        assertThat(dataSet.getChunkIndexByPosition((4 * (C.MICROS_PER_SECOND)))).isEqualTo(2);
        assertThat(dataSet.getChunkIndexByPosition((9 * (C.MICROS_PER_SECOND)))).isEqualTo(4);
        for (int i = 0; i < (dataSet.getChunkCount()); i++) {
            assertThat(dataSet.getChunkDuration(i)).isEqualTo(chunkDuration);
        }
        assertChunkData(dataSet, chunkDuration);
    }

    @Test
    public void testAdaptiveDataSetTrailingSmallChunk() {
        long chunkDuration = 3 * (C.MICROS_PER_SECOND);
        FakeAdaptiveDataSet dataSet = new FakeAdaptiveDataSet(FakeAdaptiveDataSetTest.TRACK_GROUP, (10 * (C.MICROS_PER_SECOND)), chunkDuration, 0.0, new Random(0));
        assertThat(dataSet.getAllData().size()).isEqualTo(FakeAdaptiveDataSetTest.TEST_FORMATS.length);
        assertThat(dataSet.getUri(0).equals(dataSet.getUri(1))).isFalse();
        assertThat(dataSet.getChunkCount()).isEqualTo(4);
        assertThat(dataSet.getChunkIndexByPosition((4 * (C.MICROS_PER_SECOND)))).isEqualTo(1);
        assertThat(dataSet.getChunkIndexByPosition((9 * (C.MICROS_PER_SECOND)))).isEqualTo(3);
        for (int i = 0; i < ((dataSet.getChunkCount()) - 1); i++) {
            assertThat(dataSet.getChunkDuration(i)).isEqualTo(chunkDuration);
        }
        assertThat(dataSet.getChunkDuration(3)).isEqualTo((1 * (C.MICROS_PER_SECOND)));
        assertChunkData(dataSet, chunkDuration);
    }

    @Test
    public void testAdaptiveDataSetChunkSizeDistribution() {
        double expectedStdDev = 4.0;
        FakeAdaptiveDataSet dataSet = new FakeAdaptiveDataSet(FakeAdaptiveDataSetTest.TRACK_GROUP, (100000 * (C.MICROS_PER_SECOND)), (1 * (C.MICROS_PER_SECOND)), expectedStdDev, new Random(0));
        for (int i = 0; i < (FakeAdaptiveDataSetTest.TEST_FORMATS.length); i++) {
            FakeData data = dataSet.getData(dataSet.getUri(i));
            double mean = FakeAdaptiveDataSetTest.computeSegmentSizeMean(data.getSegments());
            double stddev = FakeAdaptiveDataSetTest.computeSegmentSizeStdDev(data.getSegments(), mean);
            double relativePercentStdDev = (stddev / mean) * 100.0;
            assertThat(relativePercentStdDev).isWithin(0.02).of(expectedStdDev);
            assertThat(((mean * 8) / (FakeAdaptiveDataSetTest.TEST_FORMATS[i].bitrate))).isWithin(0.01).of(1.0);
        }
    }
}

