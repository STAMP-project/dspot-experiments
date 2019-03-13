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


import com.google.android.exoplayer2.testutil.FakeExtractorInput;
import com.google.android.exoplayer2.testutil.OggTestData;
import com.google.android.exoplayer2.testutil.TestUtil;
import java.io.EOFException;
import java.io.IOException;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/**
 * Unit test for {@link DefaultOggSeeker} utility methods.
 */
@RunWith(RobolectricTestRunner.class)
public final class DefaultOggSeekerUtilMethodsTest {
    private final Random random = new Random(0);

    @Test
    public void testSkipToNextPage() throws Exception {
        FakeExtractorInput extractorInput = OggTestData.createInput(TestUtil.joinByteArrays(TestUtil.buildTestData(4000, random), new byte[]{ 'O', 'g', 'g', 'S' }, TestUtil.buildTestData(4000, random)), false);
        DefaultOggSeekerUtilMethodsTest.skipToNextPage(extractorInput);
        assertThat(extractorInput.getPosition()).isEqualTo(4000);
    }

    @Test
    public void testSkipToNextPageOverlap() throws Exception {
        FakeExtractorInput extractorInput = OggTestData.createInput(TestUtil.joinByteArrays(TestUtil.buildTestData(2046, random), new byte[]{ 'O', 'g', 'g', 'S' }, TestUtil.buildTestData(4000, random)), false);
        DefaultOggSeekerUtilMethodsTest.skipToNextPage(extractorInput);
        assertThat(extractorInput.getPosition()).isEqualTo(2046);
    }

    @Test
    public void testSkipToNextPageInputShorterThanPeekLength() throws Exception {
        FakeExtractorInput extractorInput = OggTestData.createInput(TestUtil.joinByteArrays(new byte[]{ 'x', 'O', 'g', 'g', 'S' }), false);
        DefaultOggSeekerUtilMethodsTest.skipToNextPage(extractorInput);
        assertThat(extractorInput.getPosition()).isEqualTo(1);
    }

    @Test
    public void testSkipToNextPageNoMatch() throws Exception {
        FakeExtractorInput extractorInput = OggTestData.createInput(new byte[]{ 'g', 'g', 'S', 'O', 'g', 'g' }, false);
        try {
            DefaultOggSeekerUtilMethodsTest.skipToNextPage(extractorInput);
            Assert.fail();
        } catch (EOFException e) {
            // expected
        }
    }

    @Test
    public void testSkipToPageOfGranule() throws IOException, InterruptedException {
        byte[] packet = TestUtil.buildTestData((3 * 254), random);
        byte[] data = // Laces.
        // Laces.
        // Laces.
        TestUtil.joinByteArrays(OggTestData.buildOggHeader(1, 20000, 1000, 3), TestUtil.createByteArray(254, 254, 254), packet, OggTestData.buildOggHeader(4, 40000, 1001, 3), TestUtil.createByteArray(254, 254, 254), packet, OggTestData.buildOggHeader(4, 60000, 1002, 3), TestUtil.createByteArray(254, 254, 254), packet);
        FakeExtractorInput input = new FakeExtractorInput.Builder().setData(data).build();
        // expect to be granule of the previous page returned as elapsedSamples
        skipToPageOfGranule(input, 54000, 40000);
        // expect to be at the start of the third page
        assertThat(input.getPosition()).isEqualTo((2 * (30 + (3 * 254))));
    }

    @Test
    public void testSkipToPageOfGranulePreciseMatch() throws IOException, InterruptedException {
        byte[] packet = TestUtil.buildTestData((3 * 254), random);
        byte[] data = // Laces.
        // Laces.
        // Laces.
        TestUtil.joinByteArrays(OggTestData.buildOggHeader(1, 20000, 1000, 3), TestUtil.createByteArray(254, 254, 254), packet, OggTestData.buildOggHeader(4, 40000, 1001, 3), TestUtil.createByteArray(254, 254, 254), packet, OggTestData.buildOggHeader(4, 60000, 1002, 3), TestUtil.createByteArray(254, 254, 254), packet);
        FakeExtractorInput input = new FakeExtractorInput.Builder().setData(data).build();
        skipToPageOfGranule(input, 40000, 20000);
        // expect to be at the start of the second page
        assertThat(input.getPosition()).isEqualTo((30 + (3 * 254)));
    }

    @Test
    public void testSkipToPageOfGranuleAfterTargetPage() throws IOException, InterruptedException {
        byte[] packet = TestUtil.buildTestData((3 * 254), random);
        byte[] data = // Laces.
        // Laces.
        // Laces.
        TestUtil.joinByteArrays(OggTestData.buildOggHeader(1, 20000, 1000, 3), TestUtil.createByteArray(254, 254, 254), packet, OggTestData.buildOggHeader(4, 40000, 1001, 3), TestUtil.createByteArray(254, 254, 254), packet, OggTestData.buildOggHeader(4, 60000, 1002, 3), TestUtil.createByteArray(254, 254, 254), packet);
        FakeExtractorInput input = new FakeExtractorInput.Builder().setData(data).build();
        skipToPageOfGranule(input, 10000, (-1));
        assertThat(input.getPosition()).isEqualTo(0);
    }

    @Test
    public void testReadGranuleOfLastPage() throws IOException, InterruptedException {
        FakeExtractorInput input = OggTestData.createInput(// laces
        // laces
        // laces
        TestUtil.joinByteArrays(TestUtil.buildTestData(100, random), OggTestData.buildOggHeader(0, 20000, 66, 3), TestUtil.createByteArray(254, 254, 254), TestUtil.buildTestData((3 * 254), random), OggTestData.buildOggHeader(0, 40000, 67, 3), TestUtil.createByteArray(254, 254, 254), TestUtil.buildTestData((3 * 254), random), OggTestData.buildOggHeader(5, 60000, 68, 3), TestUtil.createByteArray(254, 254, 254), TestUtil.buildTestData((3 * 254), random)), false);
        assertReadGranuleOfLastPage(input, 60000);
    }

    @Test
    public void testReadGranuleOfLastPageAfterLastHeader() throws IOException, InterruptedException {
        FakeExtractorInput input = OggTestData.createInput(TestUtil.buildTestData(100, random), false);
        try {
            assertReadGranuleOfLastPage(input, 60000);
            Assert.fail();
        } catch (EOFException e) {
            // ignored
        }
    }

    @Test
    public void testReadGranuleOfLastPageWithUnboundedLength() throws IOException, InterruptedException {
        FakeExtractorInput input = OggTestData.createInput(new byte[0], true);
        try {
            assertReadGranuleOfLastPage(input, 60000);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // ignored
        }
    }
}

