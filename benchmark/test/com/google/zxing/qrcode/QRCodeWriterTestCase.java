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
package com.google.zxing.qrcode;


import BarcodeFormat.QR_CODE;
import ErrorCorrectionLevel.M;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author satorux@google.com (Satoru Takabayashi) - creator
 * @author dswitkin@google.com (Daniel Switkin) - ported and expanded from C++
 */
public final class QRCodeWriterTestCase extends Assert {
    private static final Path BASE_IMAGE_PATH = Paths.get("src/test/resources/golden/qrcode/");

    @Test
    public void testQRCodeWriter() throws WriterException {
        // The QR should be multiplied up to fit, with extra padding if necessary
        int bigEnough = 256;
        Writer writer = new QRCodeWriter();
        BitMatrix matrix = writer.encode("http://www.google.com/", QR_CODE, bigEnough, bigEnough, null);
        Assert.assertNotNull(matrix);
        Assert.assertEquals(bigEnough, matrix.getWidth());
        Assert.assertEquals(bigEnough, matrix.getHeight());
        // The QR will not fit in this size, so the matrix should come back bigger
        int tooSmall = 20;
        matrix = writer.encode("http://www.google.com/", QR_CODE, tooSmall, tooSmall, null);
        Assert.assertNotNull(matrix);
        Assert.assertTrue((tooSmall < (matrix.getWidth())));
        Assert.assertTrue((tooSmall < (matrix.getHeight())));
        // We should also be able to handle non-square requests by padding them
        int strangeWidth = 500;
        int strangeHeight = 100;
        matrix = writer.encode("http://www.google.com/", QR_CODE, strangeWidth, strangeHeight, null);
        Assert.assertNotNull(matrix);
        Assert.assertEquals(strangeWidth, matrix.getWidth());
        Assert.assertEquals(strangeHeight, matrix.getHeight());
    }

    // Golden images are generated with "qrcode_sample.cc". The images are checked with both eye balls
    // and cell phones. We expect pixel-perfect results, because the error correction level is known,
    // and the pixel dimensions matches exactly.
    @Test
    public void testRegressionTest() throws Exception {
        QRCodeWriterTestCase.compareToGoldenFile("http://www.google.com/", M, 99, "renderer-test-01.png");
    }
}

