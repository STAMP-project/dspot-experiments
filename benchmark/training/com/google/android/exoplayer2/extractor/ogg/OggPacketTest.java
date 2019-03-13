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
package com.google.android.exoplayer2.extractor.ogg;


import RuntimeEnvironment.application;
import com.google.android.exoplayer2.testutil.FakeExtractorInput;
import com.google.android.exoplayer2.testutil.OggTestData;
import com.google.android.exoplayer2.testutil.TestUtil;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/**
 * Unit test for {@link OggPacket}.
 */
@RunWith(RobolectricTestRunner.class)
public final class OggPacketTest {
    private static final String TEST_FILE = "ogg/bear.opus";

    private Random random;

    private OggPacket oggPacket;

    @Test
    public void testReadPacketsWithEmptyPage() throws Exception {
        byte[] firstPacket = TestUtil.buildTestData(8, random);
        byte[] secondPacket = TestUtil.buildTestData(272, random);
        byte[] thirdPacket = TestUtil.buildTestData(256, random);
        byte[] fourthPacket = TestUtil.buildTestData(271, random);
        FakeExtractorInput input = OggTestData.createInput(// First page with a single packet.
        // Laces
        // Second page with a single packet.
        // Laces
        // Third page with zero packets.
        // Fourth page with two packets.
        // Laces
        TestUtil.joinByteArrays(OggTestData.buildOggHeader(2, 0, 1000, 1), TestUtil.createByteArray(8), firstPacket, OggTestData.buildOggHeader(0, 16, 1001, 2), TestUtil.createByteArray(255, 17), secondPacket, OggTestData.buildOggHeader(0, 16, 1002, 0), OggTestData.buildOggHeader(4, 128, 1003, 4), TestUtil.createByteArray(255, 1, 255, 16), thirdPacket, fourthPacket), true);
        assertReadPacket(input, firstPacket);
        assertThat((((oggPacket.getPageHeader().type) & 2) == 2)).isTrue();
        assertThat((((oggPacket.getPageHeader().type) & 4) == 4)).isFalse();
        assertThat(oggPacket.getPageHeader().type).isEqualTo(2);
        assertThat(oggPacket.getPageHeader().headerSize).isEqualTo((27 + 1));
        assertThat(oggPacket.getPageHeader().bodySize).isEqualTo(8);
        assertThat(oggPacket.getPageHeader().revision).isEqualTo(0);
        assertThat(oggPacket.getPageHeader().pageSegmentCount).isEqualTo(1);
        assertThat(oggPacket.getPageHeader().pageSequenceNumber).isEqualTo(1000);
        assertThat(oggPacket.getPageHeader().streamSerialNumber).isEqualTo(4096);
        assertThat(oggPacket.getPageHeader().granulePosition).isEqualTo(0);
        assertReadPacket(input, secondPacket);
        assertThat((((oggPacket.getPageHeader().type) & 2) == 2)).isFalse();
        assertThat((((oggPacket.getPageHeader().type) & 4) == 4)).isFalse();
        assertThat(oggPacket.getPageHeader().type).isEqualTo(0);
        assertThat(oggPacket.getPageHeader().headerSize).isEqualTo((27 + 2));
        assertThat(oggPacket.getPageHeader().bodySize).isEqualTo((255 + 17));
        assertThat(oggPacket.getPageHeader().pageSegmentCount).isEqualTo(2);
        assertThat(oggPacket.getPageHeader().pageSequenceNumber).isEqualTo(1001);
        assertThat(oggPacket.getPageHeader().granulePosition).isEqualTo(16);
        assertReadPacket(input, thirdPacket);
        assertThat((((oggPacket.getPageHeader().type) & 2) == 2)).isFalse();
        assertThat((((oggPacket.getPageHeader().type) & 4) == 4)).isTrue();
        assertThat(oggPacket.getPageHeader().type).isEqualTo(4);
        assertThat(oggPacket.getPageHeader().headerSize).isEqualTo((27 + 4));
        assertThat(oggPacket.getPageHeader().bodySize).isEqualTo((((255 + 1) + 255) + 16));
        assertThat(oggPacket.getPageHeader().pageSegmentCount).isEqualTo(4);
        // Page 1002 is empty, so current page is 1003.
        assertThat(oggPacket.getPageHeader().pageSequenceNumber).isEqualTo(1003);
        assertThat(oggPacket.getPageHeader().granulePosition).isEqualTo(128);
        assertReadPacket(input, fourthPacket);
        assertReadEof(input);
    }

    @Test
    public void testReadPacketWithZeroSizeTerminator() throws Exception {
        byte[] firstPacket = TestUtil.buildTestData(255, random);
        byte[] secondPacket = TestUtil.buildTestData(8, random);
        FakeExtractorInput input = OggTestData.createInput(// Laces.
        TestUtil.joinByteArrays(OggTestData.buildOggHeader(6, 0, 1000, 4), TestUtil.createByteArray(255, 0, 0, 8), firstPacket, secondPacket), true);
        assertReadPacket(input, firstPacket);
        assertReadPacket(input, secondPacket);
        assertReadEof(input);
    }

    @Test
    public void testReadContinuedPacketOverTwoPages() throws Exception {
        byte[] firstPacket = TestUtil.buildTestData(518);
        FakeExtractorInput input = OggTestData.createInput(// First page.
        // Laces.
        // Second page (continued packet).
        // Laces.
        TestUtil.joinByteArrays(OggTestData.buildOggHeader(2, 0, 1000, 2), TestUtil.createByteArray(255, 255), Arrays.copyOf(firstPacket, 510), OggTestData.buildOggHeader(5, 10, 1001, 1), TestUtil.createByteArray(8), Arrays.copyOfRange(firstPacket, 510, (510 + 8))), true);
        assertReadPacket(input, firstPacket);
        assertThat((((oggPacket.getPageHeader().type) & 4) == 4)).isTrue();
        assertThat((((oggPacket.getPageHeader().type) & 2) == 2)).isFalse();
        assertThat(oggPacket.getPageHeader().pageSequenceNumber).isEqualTo(1001);
        assertReadEof(input);
    }

    @Test
    public void testReadContinuedPacketOverFourPages() throws Exception {
        byte[] firstPacket = TestUtil.buildTestData(1028);
        FakeExtractorInput input = OggTestData.createInput(// First page.
        // Laces.
        // Second page (continued packet).
        // Laces.
        // Third page (continued packet).
        // Laces.
        // Fourth page (continued packet).
        // Laces.
        TestUtil.joinByteArrays(OggTestData.buildOggHeader(2, 0, 1000, 2), TestUtil.createByteArray(255, 255), Arrays.copyOf(firstPacket, 510), OggTestData.buildOggHeader(1, 10, 1001, 1), TestUtil.createByteArray(255), Arrays.copyOfRange(firstPacket, 510, (510 + 255)), OggTestData.buildOggHeader(1, 10, 1002, 1), TestUtil.createByteArray(255), Arrays.copyOfRange(firstPacket, (510 + 255), ((510 + 255) + 255)), OggTestData.buildOggHeader(5, 10, 1003, 1), TestUtil.createByteArray(8), Arrays.copyOfRange(firstPacket, ((510 + 255) + 255), (((510 + 255) + 255) + 8))), true);
        assertReadPacket(input, firstPacket);
        assertThat((((oggPacket.getPageHeader().type) & 4) == 4)).isTrue();
        assertThat((((oggPacket.getPageHeader().type) & 2) == 2)).isFalse();
        assertThat(oggPacket.getPageHeader().pageSequenceNumber).isEqualTo(1003);
        assertReadEof(input);
    }

    @Test
    public void testReadDiscardContinuedPacketAtStart() throws Exception {
        byte[] pageBody = TestUtil.buildTestData((256 + 8));
        FakeExtractorInput input = OggTestData.createInput(// Page with a continued packet at start.
        // Laces.
        TestUtil.joinByteArrays(OggTestData.buildOggHeader(1, 10, 1001, 3), TestUtil.createByteArray(255, 1, 8), pageBody), true);
        // Expect the first partial packet to be discarded.
        assertReadPacket(input, Arrays.copyOfRange(pageBody, 256, (256 + 8)));
        assertReadEof(input);
    }

    @Test
    public void testReadZeroSizedPacketsAtEndOfStream() throws Exception {
        byte[] firstPacket = TestUtil.buildTestData(8, random);
        byte[] secondPacket = TestUtil.buildTestData(8, random);
        byte[] thirdPacket = TestUtil.buildTestData(8, random);
        FakeExtractorInput input = OggTestData.createInput(// Laces.
        // Laces.
        // Laces.
        TestUtil.joinByteArrays(OggTestData.buildOggHeader(2, 0, 1000, 1), TestUtil.createByteArray(8), firstPacket, OggTestData.buildOggHeader(4, 0, 1001, 3), TestUtil.createByteArray(8, 0, 0), secondPacket, OggTestData.buildOggHeader(4, 0, 1002, 3), TestUtil.createByteArray(8, 0, 0), thirdPacket), true);
        assertReadPacket(input, firstPacket);
        assertReadPacket(input, secondPacket);
        assertReadPacket(input, thirdPacket);
        assertReadEof(input);
    }

    @Test
    public void testParseRealFile() throws IOException, InterruptedException {
        byte[] data = TestUtil.getByteArray(application, OggPacketTest.TEST_FILE);
        FakeExtractorInput input = new FakeExtractorInput.Builder().setData(data).build();
        int packetCounter = 0;
        while (readPacket(input)) {
            packetCounter++;
        } 
        assertThat(packetCounter).isEqualTo(277);
    }
}

