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
package com.google.android.exoplayer2.extractor.amr;


import RuntimeEnvironment.application;
import android.net.Uri;
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
 * Unit test for {@link AmrExtractor}.
 */
@RunWith(RobolectricTestRunner.class)
public final class AmrExtractorSeekTest {
    private static final Random random = new Random(1234L);

    private static final String NARROW_BAND_AMR_FILE = "amr/sample_nb.amr";

    private static final int NARROW_BAND_FILE_DURATION_US = 4360000;

    private static final String WIDE_BAND_AMR_FILE = "amr/sample_wb.amr";

    private static final int WIDE_BAND_FILE_DURATION_US = 3380000;

    private FakeTrackOutput expectedTrackOutput;

    private DefaultDataSource dataSource;

    @Test
    public void testAmrExtractorReads_returnSeekableSeekMap_forNarrowBandAmr() throws IOException, InterruptedException {
        String fileName = AmrExtractorSeekTest.NARROW_BAND_AMR_FILE;
        Uri fileUri = TestUtil.buildAssetUri(fileName);
        expectedTrackOutput = TestUtil.extractAllSamplesFromFile(createAmrExtractor(), application, fileName).trackOutputs.get(0);
        AmrExtractor extractor = createAmrExtractor();
        SeekMap seekMap = TestUtil.extractSeekMap(extractor, new FakeExtractorOutput(), dataSource, fileUri);
        assertThat(seekMap).isNotNull();
        assertThat(seekMap.getDurationUs()).isEqualTo(AmrExtractorSeekTest.NARROW_BAND_FILE_DURATION_US);
        assertThat(seekMap.isSeekable()).isTrue();
    }

    @Test
    public void testSeeking_handlesSeekingToPositionInFile_extractsCorrectFrame_forNarrowBandAmr() throws IOException, InterruptedException {
        String fileName = AmrExtractorSeekTest.NARROW_BAND_AMR_FILE;
        Uri fileUri = TestUtil.buildAssetUri(fileName);
        expectedTrackOutput = TestUtil.extractAllSamplesFromFile(createAmrExtractor(), application, fileName).trackOutputs.get(0);
        AmrExtractor extractor = createAmrExtractor();
        FakeExtractorOutput extractorOutput = new FakeExtractorOutput();
        SeekMap seekMap = TestUtil.extractSeekMap(extractor, extractorOutput, dataSource, fileUri);
        FakeTrackOutput trackOutput = extractorOutput.trackOutputs.get(0);
        long targetSeekTimeUs = 980000;
        int extractedFrameIndex = TestUtil.seekToTimeUs(extractor, seekMap, targetSeekTimeUs, dataSource, trackOutput, fileUri);
        assertThat(extractedFrameIndex).isNotEqualTo((-1));
        assertFirstFrameAfterSeekContainTargetSeekTime(trackOutput, targetSeekTimeUs, extractedFrameIndex);
    }

    @Test
    public void testSeeking_handlesSeekToEoF_extractsLastFrame_forNarrowBandAmr() throws IOException, InterruptedException {
        String fileName = AmrExtractorSeekTest.NARROW_BAND_AMR_FILE;
        Uri fileUri = TestUtil.buildAssetUri(fileName);
        expectedTrackOutput = TestUtil.extractAllSamplesFromFile(createAmrExtractor(), application, fileName).trackOutputs.get(0);
        AmrExtractor extractor = createAmrExtractor();
        FakeExtractorOutput extractorOutput = new FakeExtractorOutput();
        SeekMap seekMap = TestUtil.extractSeekMap(extractor, extractorOutput, dataSource, fileUri);
        FakeTrackOutput trackOutput = extractorOutput.trackOutputs.get(0);
        long targetSeekTimeUs = seekMap.getDurationUs();
        int extractedFrameIndex = TestUtil.seekToTimeUs(extractor, seekMap, targetSeekTimeUs, dataSource, trackOutput, fileUri);
        assertThat(extractedFrameIndex).isNotEqualTo((-1));
        assertFirstFrameAfterSeekContainTargetSeekTime(trackOutput, targetSeekTimeUs, extractedFrameIndex);
    }

    @Test
    public void testSeeking_handlesSeekingBackward_extractsCorrectFrames_forNarrowBandAmr() throws IOException, InterruptedException {
        String fileName = AmrExtractorSeekTest.NARROW_BAND_AMR_FILE;
        Uri fileUri = TestUtil.buildAssetUri(fileName);
        expectedTrackOutput = TestUtil.extractAllSamplesFromFile(createAmrExtractor(), application, fileName).trackOutputs.get(0);
        AmrExtractor extractor = createAmrExtractor();
        FakeExtractorOutput extractorOutput = new FakeExtractorOutput();
        SeekMap seekMap = TestUtil.extractSeekMap(extractor, extractorOutput, dataSource, fileUri);
        FakeTrackOutput trackOutput = extractorOutput.trackOutputs.get(0);
        long firstSeekTimeUs = 980000;
        TestUtil.seekToTimeUs(extractor, seekMap, firstSeekTimeUs, dataSource, trackOutput, fileUri);
        long targetSeekTimeUs = 0;
        int extractedFrameIndex = TestUtil.seekToTimeUs(extractor, seekMap, targetSeekTimeUs, dataSource, trackOutput, fileUri);
        assertThat(extractedFrameIndex).isNotEqualTo((-1));
        assertFirstFrameAfterSeekContainTargetSeekTime(trackOutput, targetSeekTimeUs, extractedFrameIndex);
    }

    @Test
    public void testSeeking_handlesSeekingForward_extractsCorrectFrames_forNarrowBandAmr() throws IOException, InterruptedException {
        String fileName = AmrExtractorSeekTest.NARROW_BAND_AMR_FILE;
        Uri fileUri = TestUtil.buildAssetUri(fileName);
        expectedTrackOutput = TestUtil.extractAllSamplesFromFile(createAmrExtractor(), application, fileName).trackOutputs.get(0);
        AmrExtractor extractor = createAmrExtractor();
        FakeExtractorOutput extractorOutput = new FakeExtractorOutput();
        SeekMap seekMap = TestUtil.extractSeekMap(extractor, extractorOutput, dataSource, fileUri);
        FakeTrackOutput trackOutput = extractorOutput.trackOutputs.get(0);
        long firstSeekTimeUs = 980000;
        TestUtil.seekToTimeUs(extractor, seekMap, firstSeekTimeUs, dataSource, trackOutput, fileUri);
        long targetSeekTimeUs = 1200000;
        int extractedFrameIndex = TestUtil.seekToTimeUs(extractor, seekMap, targetSeekTimeUs, dataSource, trackOutput, fileUri);
        assertThat(extractedFrameIndex).isNotEqualTo((-1));
        assertFirstFrameAfterSeekContainTargetSeekTime(trackOutput, targetSeekTimeUs, extractedFrameIndex);
    }

    @Test
    public void testSeeking_handlesRandomSeeks_extractsCorrectFrames_forNarrowBandAmr() throws IOException, InterruptedException {
        String fileName = AmrExtractorSeekTest.NARROW_BAND_AMR_FILE;
        Uri fileUri = TestUtil.buildAssetUri(fileName);
        expectedTrackOutput = TestUtil.extractAllSamplesFromFile(createAmrExtractor(), application, fileName).trackOutputs.get(0);
        AmrExtractor extractor = createAmrExtractor();
        FakeExtractorOutput extractorOutput = new FakeExtractorOutput();
        SeekMap seekMap = TestUtil.extractSeekMap(extractor, extractorOutput, dataSource, fileUri);
        FakeTrackOutput trackOutput = extractorOutput.trackOutputs.get(0);
        long numSeek = 100;
        for (long i = 0; i < numSeek; i++) {
            long targetSeekTimeUs = AmrExtractorSeekTest.random.nextInt(((AmrExtractorSeekTest.NARROW_BAND_FILE_DURATION_US) + 1));
            int extractedFrameIndex = TestUtil.seekToTimeUs(extractor, seekMap, targetSeekTimeUs, dataSource, trackOutput, fileUri);
            assertThat(extractedFrameIndex).isNotEqualTo((-1));
            assertFirstFrameAfterSeekContainTargetSeekTime(trackOutput, targetSeekTimeUs, extractedFrameIndex);
        }
    }

    @Test
    public void testAmrExtractorReads_returnSeekableSeekMap_forWideBandAmr() throws IOException, InterruptedException {
        String fileName = AmrExtractorSeekTest.WIDE_BAND_AMR_FILE;
        Uri fileUri = TestUtil.buildAssetUri(fileName);
        expectedTrackOutput = TestUtil.extractAllSamplesFromFile(createAmrExtractor(), application, fileName).trackOutputs.get(0);
        AmrExtractor extractor = createAmrExtractor();
        SeekMap seekMap = TestUtil.extractSeekMap(extractor, new FakeExtractorOutput(), dataSource, fileUri);
        assertThat(seekMap).isNotNull();
        assertThat(seekMap.getDurationUs()).isEqualTo(AmrExtractorSeekTest.WIDE_BAND_FILE_DURATION_US);
        assertThat(seekMap.isSeekable()).isTrue();
    }

    @Test
    public void testSeeking_handlesSeekingToPositionInFile_extractsCorrectFrame_forWideBandAmr() throws IOException, InterruptedException {
        String fileName = AmrExtractorSeekTest.WIDE_BAND_AMR_FILE;
        Uri fileUri = TestUtil.buildAssetUri(fileName);
        expectedTrackOutput = TestUtil.extractAllSamplesFromFile(createAmrExtractor(), application, fileName).trackOutputs.get(0);
        AmrExtractor extractor = createAmrExtractor();
        FakeExtractorOutput extractorOutput = new FakeExtractorOutput();
        SeekMap seekMap = TestUtil.extractSeekMap(extractor, extractorOutput, dataSource, fileUri);
        FakeTrackOutput trackOutput = extractorOutput.trackOutputs.get(0);
        long targetSeekTimeUs = 980000;
        int extractedFrameIndex = TestUtil.seekToTimeUs(extractor, seekMap, targetSeekTimeUs, dataSource, trackOutput, fileUri);
        assertThat(extractedFrameIndex).isNotEqualTo((-1));
        assertFirstFrameAfterSeekContainTargetSeekTime(trackOutput, targetSeekTimeUs, extractedFrameIndex);
    }

    @Test
    public void testSeeking_handlesSeekToEoF_extractsLastFrame_forWideBandAmr() throws IOException, InterruptedException {
        String fileName = AmrExtractorSeekTest.WIDE_BAND_AMR_FILE;
        Uri fileUri = TestUtil.buildAssetUri(fileName);
        expectedTrackOutput = TestUtil.extractAllSamplesFromFile(createAmrExtractor(), application, fileName).trackOutputs.get(0);
        AmrExtractor extractor = createAmrExtractor();
        FakeExtractorOutput extractorOutput = new FakeExtractorOutput();
        SeekMap seekMap = TestUtil.extractSeekMap(extractor, extractorOutput, dataSource, fileUri);
        FakeTrackOutput trackOutput = extractorOutput.trackOutputs.get(0);
        long targetSeekTimeUs = seekMap.getDurationUs();
        int extractedFrameIndex = TestUtil.seekToTimeUs(extractor, seekMap, targetSeekTimeUs, dataSource, trackOutput, fileUri);
        assertThat(extractedFrameIndex).isNotEqualTo((-1));
        assertFirstFrameAfterSeekContainTargetSeekTime(trackOutput, targetSeekTimeUs, extractedFrameIndex);
    }

    @Test
    public void testSeeking_handlesSeekingBackward_extractsCorrectFrames_forWideBandAmr() throws IOException, InterruptedException {
        String fileName = AmrExtractorSeekTest.WIDE_BAND_AMR_FILE;
        Uri fileUri = TestUtil.buildAssetUri(fileName);
        expectedTrackOutput = TestUtil.extractAllSamplesFromFile(createAmrExtractor(), application, fileName).trackOutputs.get(0);
        AmrExtractor extractor = createAmrExtractor();
        FakeExtractorOutput extractorOutput = new FakeExtractorOutput();
        SeekMap seekMap = TestUtil.extractSeekMap(extractor, extractorOutput, dataSource, fileUri);
        FakeTrackOutput trackOutput = extractorOutput.trackOutputs.get(0);
        long firstSeekTimeUs = 980000;
        TestUtil.seekToTimeUs(extractor, seekMap, firstSeekTimeUs, dataSource, trackOutput, fileUri);
        long targetSeekTimeUs = 0;
        int extractedFrameIndex = TestUtil.seekToTimeUs(extractor, seekMap, targetSeekTimeUs, dataSource, trackOutput, fileUri);
        assertThat(extractedFrameIndex).isNotEqualTo((-1));
        assertFirstFrameAfterSeekContainTargetSeekTime(trackOutput, targetSeekTimeUs, extractedFrameIndex);
    }

    @Test
    public void testSeeking_handlesSeekingForward_extractsCorrectFrames_forWideBandAmr() throws IOException, InterruptedException {
        String fileName = AmrExtractorSeekTest.WIDE_BAND_AMR_FILE;
        Uri fileUri = TestUtil.buildAssetUri(fileName);
        expectedTrackOutput = TestUtil.extractAllSamplesFromFile(createAmrExtractor(), application, fileName).trackOutputs.get(0);
        AmrExtractor extractor = createAmrExtractor();
        FakeExtractorOutput extractorOutput = new FakeExtractorOutput();
        SeekMap seekMap = TestUtil.extractSeekMap(extractor, extractorOutput, dataSource, fileUri);
        FakeTrackOutput trackOutput = extractorOutput.trackOutputs.get(0);
        long firstSeekTimeUs = 980000;
        TestUtil.seekToTimeUs(extractor, seekMap, firstSeekTimeUs, dataSource, trackOutput, fileUri);
        long targetSeekTimeUs = 1200000;
        int extractedFrameIndex = TestUtil.seekToTimeUs(extractor, seekMap, targetSeekTimeUs, dataSource, trackOutput, fileUri);
        assertThat(extractedFrameIndex).isNotEqualTo((-1));
        assertFirstFrameAfterSeekContainTargetSeekTime(trackOutput, targetSeekTimeUs, extractedFrameIndex);
    }

    @Test
    public void testSeeking_handlesRandomSeeks_extractsCorrectFrames_forWideBandAmr() throws IOException, InterruptedException {
        String fileName = AmrExtractorSeekTest.WIDE_BAND_AMR_FILE;
        Uri fileUri = TestUtil.buildAssetUri(fileName);
        expectedTrackOutput = TestUtil.extractAllSamplesFromFile(createAmrExtractor(), application, fileName).trackOutputs.get(0);
        AmrExtractor extractor = createAmrExtractor();
        FakeExtractorOutput extractorOutput = new FakeExtractorOutput();
        SeekMap seekMap = TestUtil.extractSeekMap(extractor, extractorOutput, dataSource, fileUri);
        FakeTrackOutput trackOutput = extractorOutput.trackOutputs.get(0);
        long numSeek = 100;
        for (long i = 0; i < numSeek; i++) {
            long targetSeekTimeUs = AmrExtractorSeekTest.random.nextInt(((AmrExtractorSeekTest.NARROW_BAND_FILE_DURATION_US) + 1));
            int extractedFrameIndex = TestUtil.seekToTimeUs(extractor, seekMap, targetSeekTimeUs, dataSource, trackOutput, fileUri);
            assertThat(extractedFrameIndex).isNotEqualTo((-1));
            assertFirstFrameAfterSeekContainTargetSeekTime(trackOutput, targetSeekTimeUs, extractedFrameIndex);
        }
    }
}

