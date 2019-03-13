/**
 * Copyright 2002-2016 the original author or authors.
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
package org.springframework.security.acls;


import Permission.RESERVED_OFF;
import Permission.RESERVED_ON;
import org.junit.Test;
import org.springframework.security.acls.domain.AclFormattingUtils;


/**
 * Tests for {@link AclFormattingUtils}.
 *
 * @author Andrei Stefan
 */
public class AclFormattingUtilsTests {
    // ~ Methods
    // ========================================================================================================
    @Test
    public final void testDemergePatternsParametersConstraints() throws Exception {
        try {
            AclFormattingUtils.demergePatterns(null, "SOME STRING");
            fail("It should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
        try {
            AclFormattingUtils.demergePatterns("SOME STRING", null);
            fail("It should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
        try {
            AclFormattingUtils.demergePatterns("SOME STRING", "LONGER SOME STRING");
            fail("It should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
        try {
            AclFormattingUtils.demergePatterns("SOME STRING", "SAME LENGTH");
        } catch (IllegalArgumentException notExpected) {
            fail("It shouldn't have thrown IllegalArgumentException");
        }
    }

    @Test
    public final void testDemergePatterns() throws Exception {
        String original = "...........................A...R";
        String removeBits = "...............................R";
        assertThat(AclFormattingUtils.demergePatterns(original, removeBits)).isEqualTo("...........................A....");
        assertThat(AclFormattingUtils.demergePatterns("ABCDEF", "......")).isEqualTo("ABCDEF");
        assertThat(AclFormattingUtils.demergePatterns("ABCDEF", "GHIJKL")).isEqualTo("......");
    }

    @Test
    public final void testMergePatternsParametersConstraints() throws Exception {
        try {
            AclFormattingUtils.mergePatterns(null, "SOME STRING");
            fail("It should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
        try {
            AclFormattingUtils.mergePatterns("SOME STRING", null);
            fail("It should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
        try {
            AclFormattingUtils.mergePatterns("SOME STRING", "LONGER SOME STRING");
            fail("It should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
        try {
            AclFormattingUtils.mergePatterns("SOME STRING", "SAME LENGTH");
        } catch (IllegalArgumentException notExpected) {
        }
    }

    @Test
    public final void testMergePatterns() throws Exception {
        String original = "...............................R";
        String extraBits = "...........................A....";
        assertThat(AclFormattingUtils.mergePatterns(original, extraBits)).isEqualTo("...........................A...R");
        assertThat(AclFormattingUtils.mergePatterns("ABCDEF", "......")).isEqualTo("ABCDEF");
        assertThat(AclFormattingUtils.mergePatterns("ABCDEF", "GHIJKL")).isEqualTo("GHIJKL");
    }

    @Test
    public final void testBinaryPrints() throws Exception {
        assertThat(AclFormattingUtils.printBinary(15)).isEqualTo("............................****");
        try {
            AclFormattingUtils.printBinary(15, RESERVED_ON);
            fail("It should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException notExpected) {
        }
        try {
            AclFormattingUtils.printBinary(15, RESERVED_OFF);
            fail("It should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException notExpected) {
        }
        assertThat(AclFormattingUtils.printBinary(15, 'x')).isEqualTo("............................xxxx");
    }

    @Test
    public void testPrintBinaryNegative() {
        assertThat(AclFormattingUtils.printBinary(-2147483648)).isEqualTo("*...............................");
    }

    @Test
    public void testPrintBinaryMinusOne() {
        assertThat(AclFormattingUtils.printBinary(-1)).isEqualTo("********************************");
    }
}

