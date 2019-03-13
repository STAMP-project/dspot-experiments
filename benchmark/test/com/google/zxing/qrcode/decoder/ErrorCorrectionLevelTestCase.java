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


import ErrorCorrectionLevel.H;
import ErrorCorrectionLevel.L;
import ErrorCorrectionLevel.M;
import ErrorCorrectionLevel.Q;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Sean Owen
 */
public final class ErrorCorrectionLevelTestCase extends Assert {
    @Test
    public void testForBits() {
        Assert.assertSame(M, ErrorCorrectionLevel.forBits(0));
        Assert.assertSame(L, ErrorCorrectionLevel.forBits(1));
        Assert.assertSame(H, ErrorCorrectionLevel.forBits(2));
        Assert.assertSame(Q, ErrorCorrectionLevel.forBits(3));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadECLevel() {
        ErrorCorrectionLevel.forBits(4);
    }
}

