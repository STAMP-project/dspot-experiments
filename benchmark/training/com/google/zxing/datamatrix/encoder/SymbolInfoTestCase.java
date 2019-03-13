/**
 * Copyright 2006 Jeremias Maerki
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
package com.google.zxing.datamatrix.encoder;


import SymbolShapeHint.FORCE_NONE;
import SymbolShapeHint.FORCE_RECTANGLE;
import SymbolShapeHint.FORCE_SQUARE;
import com.google.zxing.Dimension;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests the SymbolInfo class.
 */
public final class SymbolInfoTestCase extends Assert {
    @Test
    public void testSymbolInfo() {
        SymbolInfo info = SymbolInfo.lookup(3);
        Assert.assertEquals(5, info.getErrorCodewords());
        Assert.assertEquals(8, info.matrixWidth);
        Assert.assertEquals(8, info.matrixHeight);
        Assert.assertEquals(10, info.getSymbolWidth());
        Assert.assertEquals(10, info.getSymbolHeight());
        info = SymbolInfo.lookup(3, FORCE_RECTANGLE);
        Assert.assertEquals(7, info.getErrorCodewords());
        Assert.assertEquals(16, info.matrixWidth);
        Assert.assertEquals(6, info.matrixHeight);
        Assert.assertEquals(18, info.getSymbolWidth());
        Assert.assertEquals(8, info.getSymbolHeight());
        info = SymbolInfo.lookup(9);
        Assert.assertEquals(11, info.getErrorCodewords());
        Assert.assertEquals(14, info.matrixWidth);
        Assert.assertEquals(6, info.matrixHeight);
        Assert.assertEquals(32, info.getSymbolWidth());
        Assert.assertEquals(8, info.getSymbolHeight());
        info = SymbolInfo.lookup(9, FORCE_SQUARE);
        Assert.assertEquals(12, info.getErrorCodewords());
        Assert.assertEquals(14, info.matrixWidth);
        Assert.assertEquals(14, info.matrixHeight);
        Assert.assertEquals(16, info.getSymbolWidth());
        Assert.assertEquals(16, info.getSymbolHeight());
        try {
            SymbolInfo.lookup(1559);
            Assert.fail("There's no rectangular symbol for more than 1558 data codewords");
        } catch (IllegalArgumentException iae) {
            // expected
        }
        try {
            SymbolInfo.lookup(50, FORCE_RECTANGLE);
            Assert.fail("There's no rectangular symbol for 50 data codewords");
        } catch (IllegalArgumentException iae) {
            // expected
        }
        info = SymbolInfo.lookup(35);
        Assert.assertEquals(24, info.getSymbolWidth());
        Assert.assertEquals(24, info.getSymbolHeight());
        Dimension fixedSize = new Dimension(26, 26);
        info = SymbolInfo.lookup(35, FORCE_NONE, fixedSize, fixedSize, false);
        Assert.assertNotNull(info);
        Assert.assertEquals(26, info.getSymbolWidth());
        Assert.assertEquals(26, info.getSymbolHeight());
        info = SymbolInfo.lookup(45, FORCE_NONE, fixedSize, fixedSize, false);
        Assert.assertNull(info);
        Dimension minSize = fixedSize;
        Dimension maxSize = new Dimension(32, 32);
        info = SymbolInfo.lookup(35, FORCE_NONE, minSize, maxSize, false);
        Assert.assertNotNull(info);
        Assert.assertEquals(26, info.getSymbolWidth());
        Assert.assertEquals(26, info.getSymbolHeight());
        info = SymbolInfo.lookup(40, FORCE_NONE, minSize, maxSize, false);
        Assert.assertNotNull(info);
        Assert.assertEquals(26, info.getSymbolWidth());
        Assert.assertEquals(26, info.getSymbolHeight());
        info = SymbolInfo.lookup(45, FORCE_NONE, minSize, maxSize, false);
        Assert.assertNotNull(info);
        Assert.assertEquals(32, info.getSymbolWidth());
        Assert.assertEquals(32, info.getSymbolHeight());
        info = SymbolInfo.lookup(63, FORCE_NONE, minSize, maxSize, false);
        Assert.assertNull(info);
    }
}

