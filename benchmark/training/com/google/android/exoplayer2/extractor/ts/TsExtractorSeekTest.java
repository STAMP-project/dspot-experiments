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
package com.google.android.exoplayer2.extractor.ts;


import android.net.Uri;
import com.google.android.exoplayer2.extractor.PositionHolder;
import com.google.android.exoplayer2.extractor.SeekMap;
import com.google.android.exoplayer2.testutil.FakeExtractorOutput;
import com.google.android.exoplayer2.testutil.FakeTrackOutput;
import com.google.android.exoplayer2.testutil.TestUtil;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import java.io.IOException;
import java.util.Random;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/**
 * Seeking tests for {@link TsExtractor}.
 */
@RunWith(RobolectricTestRunner.class)
public final class TsExtractorSeekTest {
    private static final String TEST_FILE = "ts/bbb_2500ms.ts";

    private static final int DURATION_US = 2500000;

    private static final int AUDIO_TRACK_ID = 257;

    private static final long MAXIMUM_TIMESTAMP_DELTA_US = 500000L;

    private static final Random random = new Random(1234L);

    private FakeTrackOutput expectedTrackOutput;

    private DefaultDataSource dataSource;

    private PositionHolder positionHolder;

    @Test
    public void testTsExtractorReads_nonSeekTableFile_returnSeekableSeekMap() throws IOException, InterruptedException {
        Uri fileUri = TestUtil.buildAssetUri(TsExtractorSeekTest.TEST_FILE);
        TsExtractor extractor = new TsExtractor();
        SeekMap seekMap = TestUtil.extractSeekMap(extractor, new FakeExtractorOutput(), dataSource, fileUri);
        assertThat(seekMap).isNotNull();
        assertThat(seekMap.getDurationUs()).isEqualTo(TsExtractorSeekTest.DURATION_US);
        assertThat(seekMap.isSeekable()).isTrue();
    }

    @Test
    public void testHandlePendingSeek_handlesSeekingToPositionInFile_extractsCorrectFrame() throws IOException, InterruptedException {
        TsExtractor extractor = new TsExtractor();
        Uri fileUri = TestUtil.buildAssetUri(TsExtractorSeekTest.TEST_FILE);
        FakeExtractorOutput extractorOutput = new FakeExtractorOutput();
        SeekMap seekMap = TestUtil.extractSeekMap(extractor, extractorOutput, dataSource, fileUri);
        FakeTrackOutput trackOutput = extractorOutput.trackOutputs.get(TsExtractorSeekTest.AUDIO_TRACK_ID);
        long targetSeekTimeUs = 987000;
        int extractedFrameIndex = TestUtil.seekToTimeUs(extractor, seekMap, targetSeekTimeUs, dataSource, trackOutput, fileUri);
        assertThat(extractedFrameIndex).isNotEqualTo((-1));
        assertFirstFrameAfterSeekContainTargetSeekTime(trackOutput, targetSeekTimeUs, extractedFrameIndex);
    }

    @Test
    public void testHandlePendingSeek_handlesSeekToEoF_extractsLastFrame() throws IOException, InterruptedException {
        TsExtractor extractor = new TsExtractor();
        Uri fileUri = TestUtil.buildAssetUri(TsExtractorSeekTest.TEST_FILE);
        FakeExtractorOutput extractorOutput = new FakeExtractorOutput();
        SeekMap seekMap = TestUtil.extractSeekMap(extractor, extractorOutput, dataSource, fileUri);
        FakeTrackOutput trackOutput = extractorOutput.trackOutputs.get(TsExtractorSeekTest.AUDIO_TRACK_ID);
        long targetSeekTimeUs = seekMap.getDurationUs();
        int extractedFrameIndex = TestUtil.seekToTimeUs(extractor, seekMap, targetSeekTimeUs, dataSource, trackOutput, fileUri);
        assertThat(extractedFrameIndex).isNotEqualTo((-1));
        assertFirstFrameAfterSeekContainTargetSeekTime(trackOutput, targetSeekTimeUs, extractedFrameIndex);
    }

    @Test
    public void testHandlePendingSeek_handlesSeekingBackward_extractsCorrectFrame() throws IOException, InterruptedException {
        TsExtractor extractor = new TsExtractor();
        Uri fileUri = TestUtil.buildAssetUri(TsExtractorSeekTest.TEST_FILE);
        FakeExtractorOutput extractorOutput = new FakeExtractorOutput();
        SeekMap seekMap = TestUtil.extractSeekMap(extractor, extractorOutput, dataSource, fileUri);
        FakeTrackOutput trackOutput = extractorOutput.trackOutputs.get(TsExtractorSeekTest.AUDIO_TRACK_ID);
        long firstSeekTimeUs = 987000;
        TestUtil.seekToTimeUs(extractor, seekMap, firstSeekTimeUs, dataSource, trackOutput, fileUri);
        long targetSeekTimeUs = 0;
        int extractedFrameIndex = TestUtil.seekToTimeUs(extractor, seekMap, targetSeekTimeUs, dataSource, trackOutput, fileUri);
        assertThat(extractedFrameIndex).isNotEqualTo((-1));
        assertFirstFrameAfterSeekContainTargetSeekTime(trackOutput, targetSeekTimeUs, extractedFrameIndex);
    }

    @Test
    public void testHandlePendingSeek_handlesSeekingForward_extractsCorrectFrame() throws IOException, InterruptedException {
        TsExtractor extractor = new TsExtractor();
        Uri fileUri = TestUtil.buildAssetUri(TsExtractorSeekTest.TEST_FILE);
        FakeExtractorOutput extractorOutput = new FakeExtractorOutput();
        SeekMap seekMap = TestUtil.extractSeekMap(extractor, extractorOutput, dataSource, fileUri);
        FakeTrackOutput trackOutput = extractorOutput.trackOutputs.get(TsExtractorSeekTest.AUDIO_TRACK_ID);
        long firstSeekTimeUs = 987000;
        TestUtil.seekToTimeUs(extractor, seekMap, firstSeekTimeUs, dataSource, trackOutput, fileUri);
        long targetSeekTimeUs = 1234000;
        int extractedFrameIndex = TestUtil.seekToTimeUs(extractor, seekMap, targetSeekTimeUs, dataSource, trackOutput, fileUri);
        assertThat(extractedFrameIndex).isNotEqualTo((-1));
        assertFirstFrameAfterSeekContainTargetSeekTime(trackOutput, targetSeekTimeUs, extractedFrameIndex);
    }

    @Test
    public void testHandlePendingSeek_handlesRandomSeeks_extractsCorrectFrame() throws IOException, InterruptedException {
        TsExtractor extractor = new TsExtractor();
        Uri fileUri = TestUtil.buildAssetUri(TsExtractorSeekTest.TEST_FILE);
        FakeExtractorOutput extractorOutput = new FakeExtractorOutput();
        SeekMap seekMap = TestUtil.extractSeekMap(extractor, extractorOutput, dataSource, fileUri);
        FakeTrackOutput trackOutput = extractorOutput.trackOutputs.get(TsExtractorSeekTest.AUDIO_TRACK_ID);
        long numSeek = 100;
        for (long i = 0; i < numSeek; i++) {
            long targetSeekTimeUs = TsExtractorSeekTest.random.nextInt(((TsExtractorSeekTest.DURATION_US) + 1));
            int extractedFrameIndex = TestUtil.seekToTimeUs(extractor, seekMap, targetSeekTimeUs, dataSource, trackOutput, fileUri);
            assertThat(extractedFrameIndex).isNotEqualTo((-1));
            assertFirstFrameAfterSeekContainTargetSeekTime(trackOutput, targetSeekTimeUs, extractedFrameIndex);
        }
    }

    @Test
    public void testHandlePendingSeek_handlesRandomSeeksAfterReadingFileOnce_extractsCorrectFrame() throws IOException, InterruptedException {
        TsExtractor extractor = new TsExtractor();
        Uri fileUri = TestUtil.buildAssetUri(TsExtractorSeekTest.TEST_FILE);
        FakeExtractorOutput extractorOutput = new FakeExtractorOutput();
        readInputFileOnce(extractor, extractorOutput, fileUri);
        SeekMap seekMap = extractorOutput.seekMap;
        FakeTrackOutput trackOutput = extractorOutput.trackOutputs.get(TsExtractorSeekTest.AUDIO_TRACK_ID);
        long numSeek = 100;
        for (long i = 0; i < numSeek; i++) {
            long targetSeekTimeUs = TsExtractorSeekTest.random.nextInt(((TsExtractorSeekTest.DURATION_US) + 1));
            int extractedFrameIndex = TestUtil.seekToTimeUs(extractor, seekMap, targetSeekTimeUs, dataSource, trackOutput, fileUri);
            assertThat(extractedFrameIndex).isNotEqualTo((-1));
            assertFirstFrameAfterSeekContainTargetSeekTime(trackOutput, targetSeekTimeUs, extractedFrameIndex);
        }
    }
}

