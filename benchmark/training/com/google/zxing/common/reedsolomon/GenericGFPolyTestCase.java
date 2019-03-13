/**
 * Copyright 2018 ZXing authors
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
package com.google.zxing.common.reedsolomon;


import org.junit.Assert;
import org.junit.Test;

import static GenericGF.QR_CODE_FIELD_256;


/**
 * Tests {@link GenericGFPoly}.
 */
public final class GenericGFPolyTestCase extends Assert {
    private static final GenericGF FIELD = QR_CODE_FIELD_256;

    @Test
    public void testPolynomialString() {
        Assert.assertEquals("0", GenericGFPolyTestCase.FIELD.getZero().toString());
        Assert.assertEquals("-1", GenericGFPolyTestCase.FIELD.buildMonomial(0, (-1)).toString());
        GenericGFPoly p = new GenericGFPoly(GenericGFPolyTestCase.FIELD, new int[]{ 3, 0, -2, 1, 1 });
        Assert.assertEquals("a^25x^4 - ax^2 + x + 1", p.toString());
        p = new GenericGFPoly(GenericGFPolyTestCase.FIELD, new int[]{ 3 });
        Assert.assertEquals("a^25", p.toString());
    }

    @Test
    public void testZero() {
        Assert.assertEquals(GenericGFPolyTestCase.FIELD.getZero(), GenericGFPolyTestCase.FIELD.buildMonomial(1, 0));
        Assert.assertEquals(GenericGFPolyTestCase.FIELD.getZero(), GenericGFPolyTestCase.FIELD.buildMonomial(1, 2).multiply(0));
    }

    @Test
    public void testEvaluate() {
        Assert.assertEquals(3, GenericGFPolyTestCase.FIELD.buildMonomial(0, 3).evaluateAt(0));
    }
}

