/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.dataformat.barcode;


import BarcodeFormat.AZTEC;
import BarcodeFormat.QR_CODE;
import BarcodeImageType.JPG;
import BarcodeParameters.FORMAT;
import BarcodeParameters.HEIGHT;
import BarcodeParameters.IMAGE_TYPE;
import BarcodeParameters.WIDTH;
import DecodeHintType.TRY_HARDER;
import EncodeHintType.DATA_MATRIX_SHAPE;
import EncodeHintType.ERROR_CORRECTION;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

import static BarcodeImageType.JPG;


/**
 * This class tests all Camel independend test cases
 * for {@link BarcodeDataFormat}.
 */
public class BarcodeDataFormatTest {
    /**
     * Test default constructor.
     */
    @Test
    public final void testDefaultConstructor() {
        BarcodeDataFormat barcodeDataFormat = new BarcodeDataFormat();
        this.checkParams(IMAGE_TYPE, WIDTH, HEIGHT, FORMAT, barcodeDataFormat.getParams());
    }

    /**
     * Test constructor with barcode format.
     */
    @Test
    public final void testConstructorWithBarcodeFormat() {
        BarcodeDataFormat barcodeDataFormat = new BarcodeDataFormat(BarcodeFormat.AZTEC);
        this.checkParams(IMAGE_TYPE, WIDTH, HEIGHT, AZTEC, barcodeDataFormat.getParams());
    }

    /**
     * Test constructor with size.
     */
    @Test
    public final void testConstructorWithSize() {
        BarcodeDataFormat barcodeDataFormat = new BarcodeDataFormat(200, 250);
        this.checkParams(IMAGE_TYPE, 200, 250, FORMAT, barcodeDataFormat.getParams());
    }

    /**
     * Test constructor with image type.
     */
    @Test
    public final void testConstructorWithImageType() {
        BarcodeDataFormat barcodeDataFormat = new BarcodeDataFormat(JPG);
        this.checkParams(JPG, WIDTH, HEIGHT, FORMAT, barcodeDataFormat.getParams());
    }

    /**
     * Test constructor with all.
     */
    @Test
    public final void testConstructorWithAll() {
        BarcodeDataFormat barcodeDataFormat = new BarcodeDataFormat(200, 250, JPG, BarcodeFormat.AZTEC);
        this.checkParams(JPG, 200, 250, AZTEC, barcodeDataFormat.getParams());
    }

    /**
     * Test of optimizeHints method, of class BarcodeDataFormat.
     */
    @Test
    public final void testOptimizeHints() {
        BarcodeDataFormat instance = new BarcodeDataFormat();
        Assert.assertTrue(instance.getWriterHintMap().containsKey(ERROR_CORRECTION));
        Assert.assertTrue(instance.getReaderHintMap().containsKey(TRY_HARDER));
    }

    /**
     * Test optimized hints for data matrix.
     */
    @Test
    public final void testOptimizieHintsForDataMatrix() {
        BarcodeDataFormat instance = new BarcodeDataFormat(BarcodeFormat.DATA_MATRIX);
        Assert.assertTrue("data matrix shape hint incorrect.", instance.getWriterHintMap().containsKey(DATA_MATRIX_SHAPE));
        Assert.assertTrue("try harder hint incorrect.", instance.getReaderHintMap().containsKey(TRY_HARDER));
    }

    /**
     * Test re-optimize hints.
     */
    @Test
    public final void testReOptimizeHints() {
        // DATA-MATRIX
        BarcodeDataFormat instance = new BarcodeDataFormat(BarcodeFormat.DATA_MATRIX);
        Assert.assertTrue(instance.getWriterHintMap().containsKey(DATA_MATRIX_SHAPE));
        Assert.assertTrue(instance.getReaderHintMap().containsKey(TRY_HARDER));
        // -> QR-CODE
        instance.setBarcodeFormat(QR_CODE);
        Assert.assertFalse(instance.getWriterHintMap().containsKey(DATA_MATRIX_SHAPE));
        Assert.assertTrue(instance.getReaderHintMap().containsKey(TRY_HARDER));
    }

    /**
     * Test of addToHintMap method, of class BarcodeDataFormat.
     */
    @Test
    public final void testAddToHintMapEncodeHintTypeObject() {
        EncodeHintType hintType = EncodeHintType.MARGIN;
        Object value = 10;
        BarcodeDataFormat instance = new BarcodeDataFormat();
        instance.addToHintMap(hintType, value);
        Assert.assertTrue(instance.getWriterHintMap().containsKey(hintType));
        Assert.assertEquals(instance.getWriterHintMap().get(hintType), value);
    }

    /**
     * Test of addToHintMap method, of class BarcodeDataFormat.
     */
    @Test
    public final void testAddToHintMapDecodeHintTypeObject() {
        DecodeHintType hintType = DecodeHintType.CHARACTER_SET;
        Object value = "UTF-8";
        BarcodeDataFormat instance = new BarcodeDataFormat();
        instance.addToHintMap(hintType, value);
        Assert.assertTrue(instance.getReaderHintMap().containsKey(hintType));
        Assert.assertEquals(instance.getReaderHintMap().get(hintType), value);
    }

    /**
     * Test of removeFromHintMap method, of class BarcodeDataFormat.
     */
    @Test
    public final void testRemoveFromHintMapEncodeHintType() {
        EncodeHintType hintType = EncodeHintType.ERROR_CORRECTION;
        BarcodeDataFormat instance = new BarcodeDataFormat();
        instance.removeFromHintMap(hintType);
        Assert.assertFalse(instance.getWriterHintMap().containsKey(hintType));
    }

    /**
     * Test of removeFromHintMap method, of class BarcodeDataFormat.
     */
    @Test
    public final void testRemoveFromHintMapDecodeHintType() {
        DecodeHintType hintType = DecodeHintType.TRY_HARDER;
        BarcodeDataFormat instance = new BarcodeDataFormat();
        instance.removeFromHintMap(hintType);
        Assert.assertFalse(instance.getReaderHintMap().containsKey(hintType));
    }

    /**
     * Test of getParams method, of class BarcodeDataFormat.
     */
    @Test
    public final void testGetParams() {
        BarcodeDataFormat instance = new BarcodeDataFormat();
        BarcodeParameters result = instance.getParams();
        Assert.assertNotNull(result);
    }

    /**
     * Test of getWriterHintMap method, of class BarcodeDataFormat.
     */
    @Test
    public final void testGetWriterHintMap() {
        BarcodeDataFormat instance = new BarcodeDataFormat();
        Map<EncodeHintType, Object> result = instance.getWriterHintMap();
        Assert.assertNotNull(result);
    }

    /**
     * Test of getReaderHintMap method, of class BarcodeDataFormat.
     */
    @Test
    public final void testGetReaderHintMap() {
        BarcodeDataFormat instance = new BarcodeDataFormat();
        Map<DecodeHintType, Object> result = instance.getReaderHintMap();
        Assert.assertNotNull(result);
    }
}

