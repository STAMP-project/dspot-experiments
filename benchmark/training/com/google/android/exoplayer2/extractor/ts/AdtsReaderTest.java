/**
 * Copyright (C) 2016 The Android Open Source Project
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


import C.BUFFER_FLAG_KEY_FRAME;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.testutil.FakeTrackOutput;
import com.google.android.exoplayer2.testutil.TestUtil;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.util.Arrays;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/**
 * Test for {@link AdtsReader}.
 */
@RunWith(RobolectricTestRunner.class)
public class AdtsReaderTest {
    public static final byte[] ID3_DATA_1 = TestUtil.createByteArray(73, 68, 51, 4, 0, 0, 0, 0, 0, 61, 84, 88, 88, 88, 0, 0, 0, 51, 0, 0, 3, 0, 32, 42, 42, 42, 32, 84, 72, 73, 83, 32, 73, 83, 32, 84, 105, 109, 101, 100, 32, 77, 101, 116, 97, 68, 97, 116, 97, 32, 64, 32, 45, 45, 32, 48, 48, 58, 48, 48, 58, 48, 48, 46, 48, 32, 42, 42, 42, 32, 0);

    public static final byte[] ID3_DATA_2 = TestUtil.createByteArray(73, 68, 51, 4, 0, 0, 0, 0, 0, 63, 80, 82, 73, 86, 0, 0, 0, 53, 0, 0, 99, 111, 109, 46, 97, 112, 112, 108, 101, 46, 115, 116, 114, 101, 97, 109, 105, 110, 103, 46, 116, 114, 97, 110, 115, 112, 111, 114, 116, 83, 116, 114, 101, 97, 109, 84, 105, 109, 101, 115, 116, 97, 109, 112, 0, 0, 0, 0, 0, 0, 13, 187, 160);

    public static final byte[] ADTS_HEADER = TestUtil.createByteArray(255, 241, 80, 128, 1, 223, 252);

    public static final byte[] ADTS_CONTENT = TestUtil.createByteArray(32, 0, 32, 0, 0, 128, 14);

    private static final byte[] TEST_DATA = TestUtil.joinByteArrays(AdtsReaderTest.ID3_DATA_1, AdtsReaderTest.ID3_DATA_2, AdtsReaderTest.ADTS_HEADER, AdtsReaderTest.ADTS_CONTENT);

    private static final long ADTS_SAMPLE_DURATION = 23219L;

    private FakeTrackOutput adtsOutput;

    private FakeTrackOutput id3Output;

    private AdtsReader adtsReader;

    private ParsableByteArray data;

    private boolean firstFeed;

    @Test
    public void testSkipToNextSample() throws Exception {
        for (int i = 1; i <= ((AdtsReaderTest.ID3_DATA_1.length) + (AdtsReaderTest.ID3_DATA_2.length)); i++) {
            data.setPosition(i);
            feed();
            // Once the data position set to ID3_DATA_1.length, no more id3 samples are read
            int id3SampleCount = Math.min(i, AdtsReaderTest.ID3_DATA_1.length);
            assertSampleCounts(id3SampleCount, i);
        }
    }

    @Test
    public void testSkipToNextSampleResetsState() throws Exception {
        data = new ParsableByteArray(// Adts sample missing the first sync byte
        // The Reader should be able to read the next sample.
        TestUtil.joinByteArrays(AdtsReaderTest.ADTS_HEADER, AdtsReaderTest.ADTS_CONTENT, AdtsReaderTest.ADTS_HEADER, AdtsReaderTest.ADTS_CONTENT, Arrays.copyOfRange(AdtsReaderTest.ADTS_HEADER, 1, AdtsReaderTest.ADTS_HEADER.length), AdtsReaderTest.ADTS_CONTENT, AdtsReaderTest.ADTS_HEADER, AdtsReaderTest.ADTS_CONTENT));
        feed();
        assertSampleCounts(0, 3);
        for (int i = 0; i < 3; i++) {
            /* index= */
            /* data= */
            /* timeUs= */
            /* flags= */
            /* cryptoData= */
            adtsOutput.assertSample(i, AdtsReaderTest.ADTS_CONTENT, ((AdtsReaderTest.ADTS_SAMPLE_DURATION) * i), BUFFER_FLAG_KEY_FRAME, null);
        }
    }

    @Test
    public void testNoData() throws Exception {
        feedLimited(0);
        assertSampleCounts(0, 0);
    }

    @Test
    public void testNotEnoughDataForIdentifier() throws Exception {
        feedLimited((3 - 1));
        assertSampleCounts(0, 0);
    }

    @Test
    public void testNotEnoughDataForHeader() throws Exception {
        feedLimited((10 - 1));
        assertSampleCounts(0, 0);
    }

    @Test
    public void testNotEnoughDataForWholeId3Packet() throws Exception {
        feedLimited(((AdtsReaderTest.ID3_DATA_1.length) - 1));
        assertSampleCounts(0, 0);
    }

    @Test
    public void testConsumeWholeId3Packet() throws Exception {
        feedLimited(AdtsReaderTest.ID3_DATA_1.length);
        assertSampleCounts(1, 0);
        id3Output.assertSample(0, AdtsReaderTest.ID3_DATA_1, 0, BUFFER_FLAG_KEY_FRAME, null);
    }

    @Test
    public void testMultiId3Packet() throws Exception {
        feedLimited((((AdtsReaderTest.ID3_DATA_1.length) + (AdtsReaderTest.ID3_DATA_2.length)) - 1));
        assertSampleCounts(1, 0);
        id3Output.assertSample(0, AdtsReaderTest.ID3_DATA_1, 0, BUFFER_FLAG_KEY_FRAME, null);
    }

    @Test
    public void testMultiId3PacketConsumed() throws Exception {
        feedLimited(((AdtsReaderTest.ID3_DATA_1.length) + (AdtsReaderTest.ID3_DATA_2.length)));
        assertSampleCounts(2, 0);
        id3Output.assertSample(0, AdtsReaderTest.ID3_DATA_1, 0, BUFFER_FLAG_KEY_FRAME, null);
        id3Output.assertSample(1, AdtsReaderTest.ID3_DATA_2, 0, BUFFER_FLAG_KEY_FRAME, null);
    }

    @Test
    public void testMultiPacketConsumed() throws Exception {
        for (int i = 0; i < 10; i++) {
            data.setPosition(0);
            feed();
            long timeUs = (AdtsReaderTest.ADTS_SAMPLE_DURATION) * i;
            int j = i * 2;
            assertSampleCounts((j + 2), (i + 1));
            id3Output.assertSample(j, AdtsReaderTest.ID3_DATA_1, timeUs, BUFFER_FLAG_KEY_FRAME, null);
            id3Output.assertSample((j + 1), AdtsReaderTest.ID3_DATA_2, timeUs, BUFFER_FLAG_KEY_FRAME, null);
            adtsOutput.assertSample(i, AdtsReaderTest.ADTS_CONTENT, timeUs, BUFFER_FLAG_KEY_FRAME, null);
        }
    }

    @Test
    public void testAdtsDataOnly() throws ParserException {
        data.setPosition(((AdtsReaderTest.ID3_DATA_1.length) + (AdtsReaderTest.ID3_DATA_2.length)));
        feed();
        assertSampleCounts(0, 1);
        adtsOutput.assertSample(0, AdtsReaderTest.ADTS_CONTENT, 0, BUFFER_FLAG_KEY_FRAME, null);
    }
}

