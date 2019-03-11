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


import com.google.android.exoplayer2.extractor.PositionHolder;
import com.google.android.exoplayer2.extractor.SeekMap;
import com.google.android.exoplayer2.testutil.FakeExtractorOutput;
import com.google.android.exoplayer2.testutil.FakeTrackOutput;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import java.io.IOException;
import java.util.Random;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/**
 * Seeking tests for {@link PsExtractor}.
 */
@RunWith(RobolectricTestRunner.class)
public final class PsExtractorSeekTest {
    private static final String PS_FILE_PATH = "ts/elephants_dream.mpg";

    private static final int DURATION_US = 30436333;

    private static final int VIDEO_TRACK_ID = 224;

    private static final long DELTA_TIMESTAMP_THRESHOLD_US = 500000L;

    private static final Random random = new Random(1234L);

    private FakeExtractorOutput expectedOutput;

    private FakeTrackOutput expectedTrackOutput;

    private DefaultDataSource dataSource;

    private PositionHolder positionHolder;

    private long totalInputLength;

    @Test
    public void testPsExtractorReads_nonSeekTableFile_returnSeekableSeekMap() throws IOException, InterruptedException {
        PsExtractor extractor = new PsExtractor();
        SeekMap seekMap = extractSeekMapAndTracks(extractor, new FakeExtractorOutput());
        assertThat(seekMap).isNotNull();
        assertThat(seekMap.getDurationUs()).isEqualTo(PsExtractorSeekTest.DURATION_US);
        assertThat(seekMap.isSeekable()).isTrue();
    }

    @Test
    public void testHandlePendingSeek_handlesSeekingToPositionInFile_extractsCorrectFrame() throws IOException, InterruptedException {
        PsExtractor extractor = new PsExtractor();
        FakeExtractorOutput extractorOutput = new FakeExtractorOutput();
        SeekMap seekMap = extractSeekMapAndTracks(extractor, extractorOutput);
        FakeTrackOutput trackOutput = extractorOutput.trackOutputs.get(PsExtractorSeekTest.VIDEO_TRACK_ID);
        long targetSeekTimeUs = 987000;
        int extractedFrameIndex = seekToTimeUs(extractor, seekMap, targetSeekTimeUs, trackOutput);
        assertThat(extractedFrameIndex).isNotEqualTo((-1));
        assertFirstFrameAfterSeekContainsTargetSeekTime(trackOutput, targetSeekTimeUs, extractedFrameIndex);
    }

    @Test
    public void testHandlePendingSeek_handlesSeekToEoF() throws IOException, InterruptedException {
        PsExtractor extractor = new PsExtractor();
        FakeExtractorOutput extractorOutput = new FakeExtractorOutput();
        SeekMap seekMap = extractSeekMapAndTracks(extractor, extractorOutput);
        FakeTrackOutput trackOutput = extractorOutput.trackOutputs.get(PsExtractorSeekTest.VIDEO_TRACK_ID);
        long targetSeekTimeUs = seekMap.getDurationUs();
        int extractedFrameIndex = seekToTimeUs(extractor, seekMap, targetSeekTimeUs, trackOutput);
        // Assert that this seek will return a position at end of stream, without any frame.
        assertThat(extractedFrameIndex).isEqualTo((-1));
    }

    @Test
    public void testHandlePendingSeek_handlesSeekingBackward_extractsCorrectFrame() throws IOException, InterruptedException {
        PsExtractor extractor = new PsExtractor();
        FakeExtractorOutput extractorOutput = new FakeExtractorOutput();
        SeekMap seekMap = extractSeekMapAndTracks(extractor, extractorOutput);
        FakeTrackOutput trackOutput = extractorOutput.trackOutputs.get(PsExtractorSeekTest.VIDEO_TRACK_ID);
        long firstSeekTimeUs = 987000;
        seekToTimeUs(extractor, seekMap, firstSeekTimeUs, trackOutput);
        long targetSeekTimeUs = 0;
        int extractedFrameIndex = seekToTimeUs(extractor, seekMap, targetSeekTimeUs, trackOutput);
        assertThat(extractedFrameIndex).isNotEqualTo((-1));
        assertFirstFrameAfterSeekContainsTargetSeekTime(trackOutput, targetSeekTimeUs, extractedFrameIndex);
    }

    @Test
    public void testHandlePendingSeek_handlesSeekingForward_extractsCorrectFrame() throws IOException, InterruptedException {
        PsExtractor extractor = new PsExtractor();
        FakeExtractorOutput extractorOutput = new FakeExtractorOutput();
        SeekMap seekMap = extractSeekMapAndTracks(extractor, extractorOutput);
        FakeTrackOutput trackOutput = extractorOutput.trackOutputs.get(PsExtractorSeekTest.VIDEO_TRACK_ID);
        long firstSeekTimeUs = 987000;
        seekToTimeUs(extractor, seekMap, firstSeekTimeUs, trackOutput);
        long targetSeekTimeUs = 1234000;
        int extractedFrameIndex = seekToTimeUs(extractor, seekMap, targetSeekTimeUs, trackOutput);
        assertThat(extractedFrameIndex).isNotEqualTo((-1));
        assertFirstFrameAfterSeekContainsTargetSeekTime(trackOutput, targetSeekTimeUs, extractedFrameIndex);
    }

    @Test
    public void testHandlePendingSeek_handlesRandomSeeks_extractsCorrectFrame() throws IOException, InterruptedException {
        PsExtractor extractor = new PsExtractor();
        FakeExtractorOutput extractorOutput = new FakeExtractorOutput();
        SeekMap seekMap = extractSeekMapAndTracks(extractor, extractorOutput);
        FakeTrackOutput trackOutput = extractorOutput.trackOutputs.get(PsExtractorSeekTest.VIDEO_TRACK_ID);
        long numSeek = 100;
        for (long i = 0; i < numSeek; i++) {
            long targetSeekTimeUs = PsExtractorSeekTest.random.nextInt(((PsExtractorSeekTest.DURATION_US) + 1));
            int extractedFrameIndex = seekToTimeUs(extractor, seekMap, targetSeekTimeUs, trackOutput);
            assertThat(extractedFrameIndex).isNotEqualTo((-1));
            assertFirstFrameAfterSeekContainsTargetSeekTime(trackOutput, targetSeekTimeUs, extractedFrameIndex);
        }
    }

    @Test
    public void testHandlePendingSeek_handlesRandomSeeksAfterReadingFileOnce_extractsCorrectFrame() throws IOException, InterruptedException {
        PsExtractor extractor = new PsExtractor();
        FakeExtractorOutput extractorOutput = new FakeExtractorOutput();
        readInputFileOnce(extractor, extractorOutput);
        SeekMap seekMap = extractorOutput.seekMap;
        FakeTrackOutput trackOutput = extractorOutput.trackOutputs.get(PsExtractorSeekTest.VIDEO_TRACK_ID);
        long numSeek = 100;
        for (long i = 0; i < numSeek; i++) {
            long targetSeekTimeUs = PsExtractorSeekTest.random.nextInt(((PsExtractorSeekTest.DURATION_US) + 1));
            int extractedFrameIndex = seekToTimeUs(extractor, seekMap, targetSeekTimeUs, trackOutput);
            assertThat(extractedFrameIndex).isNotEqualTo((-1));
            assertFirstFrameAfterSeekContainsTargetSeekTime(trackOutput, targetSeekTimeUs, extractedFrameIndex);
        }
    }
}

