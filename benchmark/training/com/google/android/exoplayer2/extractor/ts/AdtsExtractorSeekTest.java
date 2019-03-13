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
 * Unit test for {@link AdtsExtractor}.
 */
@RunWith(RobolectricTestRunner.class)
public final class AdtsExtractorSeekTest {
    private static final Random random = new Random(1234L);

    private static final String TEST_FILE = "ts/sample.adts";

    private static final int FILE_DURATION_US = 3356772;

    private static final long DELTA_TIMESTAMP_THRESHOLD_US = 200000;

    private FakeTrackOutput expectedTrackOutput;

    private DefaultDataSource dataSource;

    @Test
    public void testAdtsExtractorReads_returnSeekableSeekMap() throws IOException, InterruptedException {
        String fileName = AdtsExtractorSeekTest.TEST_FILE;
        Uri fileUri = TestUtil.buildAssetUri(fileName);
        expectedTrackOutput = TestUtil.extractAllSamplesFromFile(AdtsExtractorSeekTest.createAdtsExtractor(), application, fileName).trackOutputs.get(0);
        AdtsExtractor extractor = AdtsExtractorSeekTest.createAdtsExtractor();
        SeekMap seekMap = TestUtil.extractSeekMap(extractor, new FakeExtractorOutput(), dataSource, fileUri);
        assertThat(seekMap).isNotNull();
        assertThat(seekMap.getDurationUs()).isEqualTo(AdtsExtractorSeekTest.FILE_DURATION_US);
        assertThat(seekMap.isSeekable()).isTrue();
    }

    @Test
    public void testSeeking_handlesSeekingToPositionInFile_extractsCorrectSample() throws IOException, InterruptedException {
        String fileName = AdtsExtractorSeekTest.TEST_FILE;
        Uri fileUri = TestUtil.buildAssetUri(fileName);
        expectedTrackOutput = TestUtil.extractAllSamplesFromFile(AdtsExtractorSeekTest.createAdtsExtractor(), application, fileName).trackOutputs.get(0);
        AdtsExtractor extractor = AdtsExtractorSeekTest.createAdtsExtractor();
        FakeExtractorOutput extractorOutput = new FakeExtractorOutput();
        SeekMap seekMap = TestUtil.extractSeekMap(extractor, extractorOutput, dataSource, fileUri);
        FakeTrackOutput trackOutput = extractorOutput.trackOutputs.get(0);
        long targetSeekTimeUs = 980000;
        int extractedSampleIndex = TestUtil.seekToTimeUs(extractor, seekMap, targetSeekTimeUs, dataSource, trackOutput, fileUri);
        assertThat(extractedSampleIndex).isNotEqualTo((-1));
        assertFirstSampleAfterSeekContainTargetSeekTime(trackOutput, targetSeekTimeUs, extractedSampleIndex);
    }

    @Test
    public void testSeeking_handlesSeekToEoF_extractsLastSample() throws IOException, InterruptedException {
        String fileName = AdtsExtractorSeekTest.TEST_FILE;
        Uri fileUri = TestUtil.buildAssetUri(fileName);
        expectedTrackOutput = TestUtil.extractAllSamplesFromFile(AdtsExtractorSeekTest.createAdtsExtractor(), application, fileName).trackOutputs.get(0);
        AdtsExtractor extractor = AdtsExtractorSeekTest.createAdtsExtractor();
        FakeExtractorOutput extractorOutput = new FakeExtractorOutput();
        SeekMap seekMap = TestUtil.extractSeekMap(extractor, extractorOutput, dataSource, fileUri);
        FakeTrackOutput trackOutput = extractorOutput.trackOutputs.get(0);
        long targetSeekTimeUs = seekMap.getDurationUs();
        int extractedSampleIndex = TestUtil.seekToTimeUs(extractor, seekMap, targetSeekTimeUs, dataSource, trackOutput, fileUri);
        assertThat(extractedSampleIndex).isNotEqualTo((-1));
        assertFirstSampleAfterSeekContainTargetSeekTime(trackOutput, targetSeekTimeUs, extractedSampleIndex);
    }

    @Test
    public void testSeeking_handlesSeekingBackward_extractsCorrectSamples() throws IOException, InterruptedException {
        String fileName = AdtsExtractorSeekTest.TEST_FILE;
        Uri fileUri = TestUtil.buildAssetUri(fileName);
        expectedTrackOutput = TestUtil.extractAllSamplesFromFile(AdtsExtractorSeekTest.createAdtsExtractor(), application, fileName).trackOutputs.get(0);
        AdtsExtractor extractor = AdtsExtractorSeekTest.createAdtsExtractor();
        FakeExtractorOutput extractorOutput = new FakeExtractorOutput();
        SeekMap seekMap = TestUtil.extractSeekMap(extractor, extractorOutput, dataSource, fileUri);
        FakeTrackOutput trackOutput = extractorOutput.trackOutputs.get(0);
        long firstSeekTimeUs = 980000;
        TestUtil.seekToTimeUs(extractor, seekMap, firstSeekTimeUs, dataSource, trackOutput, fileUri);
        long targetSeekTimeUs = 0;
        int extractedSampleIndex = TestUtil.seekToTimeUs(extractor, seekMap, targetSeekTimeUs, dataSource, trackOutput, fileUri);
        assertThat(extractedSampleIndex).isNotEqualTo((-1));
        assertFirstSampleAfterSeekContainTargetSeekTime(trackOutput, targetSeekTimeUs, extractedSampleIndex);
    }

    @Test
    public void testSeeking_handlesSeekingForward_extractsCorrectSamples() throws IOException, InterruptedException {
        String fileName = AdtsExtractorSeekTest.TEST_FILE;
        Uri fileUri = TestUtil.buildAssetUri(fileName);
        expectedTrackOutput = TestUtil.extractAllSamplesFromFile(AdtsExtractorSeekTest.createAdtsExtractor(), application, fileName).trackOutputs.get(0);
        AdtsExtractor extractor = AdtsExtractorSeekTest.createAdtsExtractor();
        FakeExtractorOutput extractorOutput = new FakeExtractorOutput();
        SeekMap seekMap = TestUtil.extractSeekMap(extractor, extractorOutput, dataSource, fileUri);
        FakeTrackOutput trackOutput = extractorOutput.trackOutputs.get(0);
        long firstSeekTimeUs = 980000;
        TestUtil.seekToTimeUs(extractor, seekMap, firstSeekTimeUs, dataSource, trackOutput, fileUri);
        long targetSeekTimeUs = 1200000;
        int extractedSampleIndex = TestUtil.seekToTimeUs(extractor, seekMap, targetSeekTimeUs, dataSource, trackOutput, fileUri);
        assertThat(extractedSampleIndex).isNotEqualTo((-1));
        assertFirstSampleAfterSeekContainTargetSeekTime(trackOutput, targetSeekTimeUs, extractedSampleIndex);
    }

    @Test
    public void testSeeking_handlesRandomSeeks_extractsCorrectSamples() throws IOException, InterruptedException {
        String fileName = AdtsExtractorSeekTest.TEST_FILE;
        Uri fileUri = TestUtil.buildAssetUri(fileName);
        expectedTrackOutput = TestUtil.extractAllSamplesFromFile(AdtsExtractorSeekTest.createAdtsExtractor(), application, fileName).trackOutputs.get(0);
        AdtsExtractor extractor = AdtsExtractorSeekTest.createAdtsExtractor();
        FakeExtractorOutput extractorOutput = new FakeExtractorOutput();
        SeekMap seekMap = TestUtil.extractSeekMap(extractor, extractorOutput, dataSource, fileUri);
        FakeTrackOutput trackOutput = extractorOutput.trackOutputs.get(0);
        long numSeek = 100;
        for (long i = 0; i < numSeek; i++) {
            long targetSeekTimeUs = AdtsExtractorSeekTest.random.nextInt(((AdtsExtractorSeekTest.FILE_DURATION_US) + 1));
            int extractedSampleIndex = TestUtil.seekToTimeUs(extractor, seekMap, targetSeekTimeUs, dataSource, trackOutput, fileUri);
            assertThat(extractedSampleIndex).isNotEqualTo((-1));
            assertFirstSampleAfterSeekContainTargetSeekTime(trackOutput, targetSeekTimeUs, extractedSampleIndex);
        }
    }
}

