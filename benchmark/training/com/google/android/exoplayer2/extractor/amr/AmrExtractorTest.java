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


import Extractor.RESULT_END_OF_INPUT;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.extractor.PositionHolder;
import com.google.android.exoplayer2.testutil.ExtractorAsserts;
import com.google.android.exoplayer2.testutil.FakeExtractorInput;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.util.Random;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/**
 * Unit test for {@link AmrExtractor}.
 */
@RunWith(RobolectricTestRunner.class)
public final class AmrExtractorTest {
    private static final Random RANDOM = new Random(1234);

    @Test
    public void testSniff_nonAmrSignature_returnFalse() throws IOException, InterruptedException {
        AmrExtractor amrExtractor = AmrExtractorTest.setupAmrExtractorWithOutput();
        FakeExtractorInput input = AmrExtractorTest.fakeExtractorInputWithData(Util.getUtf8Bytes("0#!AMR\n123"));
        boolean result = amrExtractor.sniff(input);
        assertThat(result).isFalse();
    }

    @Test
    public void testRead_nonAmrSignature_throwParserException() throws IOException, InterruptedException {
        AmrExtractor amrExtractor = AmrExtractorTest.setupAmrExtractorWithOutput();
        FakeExtractorInput input = AmrExtractorTest.fakeExtractorInputWithData(Util.getUtf8Bytes("0#!AMR-WB\n"));
        try {
            amrExtractor.read(input, new PositionHolder());
            Assert.fail();
        } catch (ParserException e) {
            // expected
        }
    }

    @Test
    public void testRead_amrNb_returnParserException_forInvalidFrameType() throws IOException, InterruptedException {
        AmrExtractor amrExtractor = AmrExtractorTest.setupAmrExtractorWithOutput();
        // Frame type 12-14 for narrow band is reserved for future usage.
        byte[] amrFrame = newNarrowBandAmrFrameWithType(12);
        byte[] data = AmrExtractorTest.joinData(AmrExtractor.amrSignatureNb(), amrFrame);
        FakeExtractorInput input = AmrExtractorTest.fakeExtractorInputWithData(data);
        try {
            amrExtractor.read(input, new PositionHolder());
            Assert.fail();
        } catch (ParserException e) {
            // expected
        }
    }

    @Test
    public void testRead_amrWb_returnParserException_forInvalidFrameType() throws IOException, InterruptedException {
        AmrExtractor amrExtractor = AmrExtractorTest.setupAmrExtractorWithOutput();
        // Frame type 10-13 for wide band is reserved for future usage.
        byte[] amrFrame = newWideBandAmrFrameWithType(13);
        byte[] data = AmrExtractorTest.joinData(AmrExtractor.amrSignatureWb(), amrFrame);
        FakeExtractorInput input = AmrExtractorTest.fakeExtractorInputWithData(data);
        try {
            amrExtractor.read(input, new PositionHolder());
            Assert.fail();
        } catch (ParserException e) {
            // expected
        }
    }

    @Test
    public void testRead_amrNb_returnEndOfInput_ifInputEncountersEoF() throws IOException, InterruptedException {
        AmrExtractor amrExtractor = AmrExtractorTest.setupAmrExtractorWithOutput();
        byte[] amrFrame = newNarrowBandAmrFrameWithType(3);
        byte[] data = AmrExtractorTest.joinData(AmrExtractor.amrSignatureNb(), amrFrame);
        FakeExtractorInput input = AmrExtractorTest.fakeExtractorInputWithData(data);
        // Read 1st frame, which will put the input at EoF.
        amrExtractor.read(input, new PositionHolder());
        int result = amrExtractor.read(input, new PositionHolder());
        assertThat(result).isEqualTo(RESULT_END_OF_INPUT);
    }

    @Test
    public void testRead_amrWb_returnEndOfInput_ifInputEncountersEoF() throws IOException, InterruptedException {
        AmrExtractor amrExtractor = AmrExtractorTest.setupAmrExtractorWithOutput();
        byte[] amrFrame = newWideBandAmrFrameWithType(5);
        byte[] data = AmrExtractorTest.joinData(AmrExtractor.amrSignatureWb(), amrFrame);
        FakeExtractorInput input = AmrExtractorTest.fakeExtractorInputWithData(data);
        // Read 1st frame, which will put the input at EoF.
        amrExtractor.read(input, new PositionHolder());
        int result = amrExtractor.read(input, new PositionHolder());
        assertThat(result).isEqualTo(RESULT_END_OF_INPUT);
    }

    @Test
    public void testRead_amrNb_returnParserException_forInvalidFrameHeader() throws IOException, InterruptedException {
        AmrExtractor amrExtractor = AmrExtractorTest.setupAmrExtractorWithOutput();
        byte[] invalidHeaderFrame = newNarrowBandAmrFrameWithType(4);
        // The padding bits are at bit-1 positions in the following pattern: 1000 0011
        // Padding bits must be 0.
        invalidHeaderFrame[0] = ((byte) ((invalidHeaderFrame[0]) | 125));
        byte[] data = AmrExtractorTest.joinData(AmrExtractor.amrSignatureNb(), invalidHeaderFrame);
        FakeExtractorInput input = AmrExtractorTest.fakeExtractorInputWithData(data);
        try {
            amrExtractor.read(input, new PositionHolder());
            Assert.fail();
        } catch (ParserException e) {
            // expected
        }
    }

    @Test
    public void testRead_amrWb_returnParserException_forInvalidFrameHeader() throws IOException, InterruptedException {
        AmrExtractor amrExtractor = AmrExtractorTest.setupAmrExtractorWithOutput();
        byte[] invalidHeaderFrame = newWideBandAmrFrameWithType(6);
        // The padding bits are at bit-1 positions in the following pattern: 1000 0011
        // Padding bits must be 0.
        invalidHeaderFrame[0] = ((byte) ((invalidHeaderFrame[0]) | 126));
        byte[] data = AmrExtractorTest.joinData(AmrExtractor.amrSignatureWb(), invalidHeaderFrame);
        FakeExtractorInput input = AmrExtractorTest.fakeExtractorInputWithData(data);
        try {
            amrExtractor.read(input, new PositionHolder());
            Assert.fail();
        } catch (ParserException e) {
            // expected
        }
    }

    @Test
    public void testExtractingNarrowBandSamples() throws Exception {
        ExtractorAsserts.assertBehavior(/* withSeeking= */
        AmrExtractorTest.createAmrExtractorFactory(false), "amr/sample_nb.amr");
    }

    @Test
    public void testExtractingWideBandSamples() throws Exception {
        ExtractorAsserts.assertBehavior(/* withSeeking= */
        AmrExtractorTest.createAmrExtractorFactory(false), "amr/sample_wb.amr");
    }

    @Test
    public void testExtractingNarrowBandSamples_withSeeking() throws Exception {
        ExtractorAsserts.assertBehavior(/* withSeeking= */
        AmrExtractorTest.createAmrExtractorFactory(true), "amr/sample_nb_cbr.amr");
    }

    @Test
    public void testExtractingWideBandSamples_withSeeking() throws Exception {
        ExtractorAsserts.assertBehavior(/* withSeeking= */
        AmrExtractorTest.createAmrExtractorFactory(true), "amr/sample_wb_cbr.amr");
    }
}

