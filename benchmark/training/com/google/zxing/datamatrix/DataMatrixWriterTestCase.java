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
package com.google.zxing.datamatrix;


import BarcodeFormat.DATA_MATRIX;
import EncodeHintType.DATA_MATRIX_SHAPE;
import SymbolShapeHint.FORCE_SQUARE;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author satorux@google.com (Satoru Takabayashi) - creator
 * @author dswitkin@google.com (Daniel Switkin) - ported and expanded from C++
 */
public final class DataMatrixWriterTestCase extends Assert {
    @Test
    public void testDataMatrixImageWriter() {
        Map<EncodeHintType, Object> hints = new java.util.EnumMap(EncodeHintType.class);
        hints.put(DATA_MATRIX_SHAPE, FORCE_SQUARE);
        int bigEnough = 64;
        DataMatrixWriter writer = new DataMatrixWriter();
        BitMatrix matrix = writer.encode("Hello Google", DATA_MATRIX, bigEnough, bigEnough, hints);
        Assert.assertNotNull(matrix);
        Assert.assertTrue((bigEnough >= (matrix.getWidth())));
        Assert.assertTrue((bigEnough >= (matrix.getHeight())));
    }

    @Test
    public void testDataMatrixWriter() {
        Map<EncodeHintType, Object> hints = new java.util.EnumMap(EncodeHintType.class);
        hints.put(DATA_MATRIX_SHAPE, FORCE_SQUARE);
        int bigEnough = 14;
        DataMatrixWriter writer = new DataMatrixWriter();
        BitMatrix matrix = writer.encode("Hello Me", DATA_MATRIX, bigEnough, bigEnough, hints);
        Assert.assertNotNull(matrix);
        Assert.assertEquals(bigEnough, matrix.getWidth());
        Assert.assertEquals(bigEnough, matrix.getHeight());
    }

    @Test
    public void testDataMatrixTooSmall() {
        // The DataMatrix will not fit in this size, so the matrix should come back bigger
        int tooSmall = 8;
        DataMatrixWriter writer = new DataMatrixWriter();
        BitMatrix matrix = writer.encode("http://www.google.com/", DATA_MATRIX, tooSmall, tooSmall, null);
        Assert.assertNotNull(matrix);
        Assert.assertTrue((tooSmall < (matrix.getWidth())));
        Assert.assertTrue((tooSmall < (matrix.getHeight())));
    }
}

