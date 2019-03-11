/**
 * Copyright 2014 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.security.zynamics.reil.translators;


import OperandSize.BYTE;
import OperandSize.DWORD;
import OperandSize.QWORD;
import OperandSize.WORD;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class TranslationHelpersTest {
    @Test
    public void testZeroMaskHighestBitBYTE() {
        final long mask = TranslationHelpers.generateZeroMask(7, 1, BYTE);
        Assert.assertEquals(127L, mask);
    }

    @Test
    public void testZeroMaskHighestBitDWORD() {
        final long mask = TranslationHelpers.generateZeroMask(31, 1, DWORD);
        Assert.assertEquals(2147483647L, mask);
    }

    @Test
    public void testZeroMaskHighestBitQWORD() {
        final long mask = TranslationHelpers.generateZeroMask(63, 1, QWORD);
        Assert.assertEquals(9223372036854775807L, mask);
    }

    @Test
    public void testZeroMaskHighestBitWORD() {
        final long mask = TranslationHelpers.generateZeroMask(15, 1, WORD);
        Assert.assertEquals(32767L, mask);
    }

    @Test
    public void testZeroMaskLowestBitDWORD() {
        final long mask = TranslationHelpers.generateZeroMask(0, 1, DWORD);
        Assert.assertEquals(4294967294L, mask);
    }

    @Test
    public void testZeroMaskSomeBits1() {
        final long mask = TranslationHelpers.generateZeroMask(16, 16, DWORD);
        Assert.assertEquals(65535L, mask);
    }

    @Test
    public void testZeroMaskSomeBits2() {
        final long mask = TranslationHelpers.generateZeroMask(0, 16, DWORD);
        Assert.assertEquals(4294901760L, mask);
    }

    @Test
    public void testZeroMaskSomeBits3() {
        final long mask = TranslationHelpers.generateZeroMask(8, 16, DWORD);
        Assert.assertEquals(4278190335L, mask);
    }

    @Test
    public void testZeroMaskSomeBits4() {
        final long mask = TranslationHelpers.generateZeroMask(1, 30, DWORD);
        Assert.assertEquals(2147483649L, mask);
    }
}

