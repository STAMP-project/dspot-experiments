/**
 * Copyright 2008 ZXing authors
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


import Mode.ALPHANUMERIC;
import Mode.BYTE;
import Mode.KANJI;
import Mode.NUMERIC;
import Mode.TERMINATOR;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Sean Owen
 */
public final class ModeTestCase extends Assert {
    @Test
    public void testForBits() {
        Assert.assertSame(TERMINATOR, Mode.forBits(0));
        Assert.assertSame(NUMERIC, Mode.forBits(1));
        Assert.assertSame(ALPHANUMERIC, Mode.forBits(2));
        Assert.assertSame(BYTE, Mode.forBits(4));
        Assert.assertSame(KANJI, Mode.forBits(8));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadMode() {
        Mode.forBits(16);
    }

    @Test
    public void testCharacterCount() {
        // Spot check a few values
        Assert.assertEquals(10, NUMERIC.getCharacterCountBits(Version.getVersionForNumber(5)));
        Assert.assertEquals(12, NUMERIC.getCharacterCountBits(Version.getVersionForNumber(26)));
        Assert.assertEquals(14, NUMERIC.getCharacterCountBits(Version.getVersionForNumber(40)));
        Assert.assertEquals(9, ALPHANUMERIC.getCharacterCountBits(Version.getVersionForNumber(6)));
        Assert.assertEquals(8, BYTE.getCharacterCountBits(Version.getVersionForNumber(7)));
        Assert.assertEquals(8, KANJI.getCharacterCountBits(Version.getVersionForNumber(8)));
    }
}

