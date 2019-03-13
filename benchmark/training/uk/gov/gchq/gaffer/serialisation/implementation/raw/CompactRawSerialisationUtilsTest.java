/**
 * Copyright 2016-2019 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.gov.gchq.gaffer.serialisation.implementation.raw;


import org.junit.Assert;
import org.junit.Test;


public class CompactRawSerialisationUtilsTest {
    private static final String LONG_VALUE_IS_LEGAL_TO_NOT_REQUIRE_A_LENGTH_BYTE = "long value is legal to not require a length byte";

    private static final String LONG_VALUE_REQUIRES_A_LENGTH_BYTE = "long value requires a length byte";

    private static final String LENGTH_SHOULD_BE_1_AS_IT_DOES_NOT_REQUIRE_A_LENGTH_BYTE = "Length should be 1, as it does not require a length byte";

    private byte[] bytesWithLength;

    @Test
    public void shouldHaveExtraByteForLengthInFieldVariables() throws Exception {
        Assert.assertEquals(((Long.BYTES) + 1), bytesWithLength.length);
    }

    @Test
    public void shouldNotHaveExtraByteForLength() throws Exception {
        // if (value >= -112 && value <= 127) {
        long lLowerLimit = -112;
        long lUpperLimit = 127;
        byte[] bytesLower = CompactRawSerialisationUtils.writeLong(lLowerLimit);
        Assert.assertEquals(CompactRawSerialisationUtilsTest.LONG_VALUE_IS_LEGAL_TO_NOT_REQUIRE_A_LENGTH_BYTE, 1, bytesLower.length);
        Assert.assertEquals(CompactRawSerialisationUtilsTest.LONG_VALUE_IS_LEGAL_TO_NOT_REQUIRE_A_LENGTH_BYTE, bytesLower.length, CompactRawSerialisationUtils.decodeVIntSize(bytesLower[0]));
        byte[] bytesUpper = CompactRawSerialisationUtils.writeLong(lUpperLimit);
        Assert.assertEquals(CompactRawSerialisationUtilsTest.LENGTH_SHOULD_BE_1_AS_IT_DOES_NOT_REQUIRE_A_LENGTH_BYTE, 1, bytesUpper.length);
        Assert.assertEquals(CompactRawSerialisationUtilsTest.LENGTH_SHOULD_BE_1_AS_IT_DOES_NOT_REQUIRE_A_LENGTH_BYTE, bytesUpper.length, CompactRawSerialisationUtils.decodeVIntSize(bytesUpper[0]));
    }

    @Test
    public void shouldHaveExtraByteForLength() throws Exception {
        long lExceedLowerLimit = -113;
        long lExceedUpperLimit = 128;
        byte[] bytesLower = CompactRawSerialisationUtils.writeLong(lExceedLowerLimit);
        Assert.assertEquals(CompactRawSerialisationUtilsTest.LONG_VALUE_REQUIRES_A_LENGTH_BYTE, 2, bytesLower.length);
        Assert.assertEquals(CompactRawSerialisationUtilsTest.LONG_VALUE_REQUIRES_A_LENGTH_BYTE, bytesLower.length, CompactRawSerialisationUtils.decodeVIntSize(bytesLower[0]));
        byte[] bytesUpper = CompactRawSerialisationUtils.writeLong(lExceedUpperLimit);
        Assert.assertNotEquals("Length should not be 1, as it does require a length byte", 1, bytesUpper.length);
        Assert.assertEquals(bytesUpper.length, CompactRawSerialisationUtils.decodeVIntSize(bytesUpper[0]));
    }
}

