/**
 * Copyright 2007 ZXing authors
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
package com.google.zxing.qrcode.decoder;


import ErrorCorrectionLevel.Q;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Sean Owen
 */
public final class FormatInformationTestCase extends Assert {
    private static final int MASKED_TEST_FORMAT_INFO = 11245;

    private static final int UNMASKED_TEST_FORMAT_INFO = (FormatInformationTestCase.MASKED_TEST_FORMAT_INFO) ^ 21522;

    @Test
    public void testBitsDiffering() {
        Assert.assertEquals(0, FormatInformation.numBitsDiffering(1, 1));
        Assert.assertEquals(1, FormatInformation.numBitsDiffering(0, 2));
        Assert.assertEquals(2, FormatInformation.numBitsDiffering(1, 2));
        Assert.assertEquals(32, FormatInformation.numBitsDiffering((-1), 0));
    }

    @Test
    public void testDecode() {
        // Normal case
        FormatInformation expected = FormatInformation.decodeFormatInformation(FormatInformationTestCase.MASKED_TEST_FORMAT_INFO, FormatInformationTestCase.MASKED_TEST_FORMAT_INFO);
        Assert.assertNotNull(expected);
        Assert.assertEquals(((byte) (7)), expected.getDataMask());
        Assert.assertSame(Q, expected.getErrorCorrectionLevel());
        // where the code forgot the mask!
        Assert.assertEquals(expected, FormatInformation.decodeFormatInformation(FormatInformationTestCase.UNMASKED_TEST_FORMAT_INFO, FormatInformationTestCase.MASKED_TEST_FORMAT_INFO));
    }

    @Test
    public void testDecodeWithBitDifference() {
        FormatInformation expected = FormatInformation.decodeFormatInformation(FormatInformationTestCase.MASKED_TEST_FORMAT_INFO, FormatInformationTestCase.MASKED_TEST_FORMAT_INFO);
        // 1,2,3,4 bits difference
        Assert.assertEquals(expected, FormatInformation.decodeFormatInformation(((FormatInformationTestCase.MASKED_TEST_FORMAT_INFO) ^ 1), ((FormatInformationTestCase.MASKED_TEST_FORMAT_INFO) ^ 1)));
        Assert.assertEquals(expected, FormatInformation.decodeFormatInformation(((FormatInformationTestCase.MASKED_TEST_FORMAT_INFO) ^ 3), ((FormatInformationTestCase.MASKED_TEST_FORMAT_INFO) ^ 3)));
        Assert.assertEquals(expected, FormatInformation.decodeFormatInformation(((FormatInformationTestCase.MASKED_TEST_FORMAT_INFO) ^ 7), ((FormatInformationTestCase.MASKED_TEST_FORMAT_INFO) ^ 7)));
        Assert.assertNull(FormatInformation.decodeFormatInformation(((FormatInformationTestCase.MASKED_TEST_FORMAT_INFO) ^ 15), ((FormatInformationTestCase.MASKED_TEST_FORMAT_INFO) ^ 15)));
    }

    @Test
    public void testDecodeWithMisread() {
        FormatInformation expected = FormatInformation.decodeFormatInformation(FormatInformationTestCase.MASKED_TEST_FORMAT_INFO, FormatInformationTestCase.MASKED_TEST_FORMAT_INFO);
        Assert.assertEquals(expected, FormatInformation.decodeFormatInformation(((FormatInformationTestCase.MASKED_TEST_FORMAT_INFO) ^ 3), ((FormatInformationTestCase.MASKED_TEST_FORMAT_INFO) ^ 15)));
    }
}

