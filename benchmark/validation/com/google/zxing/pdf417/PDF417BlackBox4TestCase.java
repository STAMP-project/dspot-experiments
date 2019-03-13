/**
 * Copyright 2013 ZXing authors
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
package com.google.zxing.pdf417;


import BarcodeFormat.PDF_417;
import com.google.zxing.common.AbstractBlackBoxTestCase;
import com.google.zxing.common.TestResult;
import com.google.zxing.multi.MultipleBarcodeReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.junit.Test;


/**
 * This class tests Macro PDF417 barcode specific functionality. It ensures that information, which is split into
 * several barcodes can be properly combined again to yield the original data content.
 *
 * @author Guenther Grau
 */
public final class PDF417BlackBox4TestCase extends AbstractBlackBoxTestCase {
    private static final Logger log = Logger.getLogger(AbstractBlackBoxTestCase.class.getSimpleName());

    private final MultipleBarcodeReader barcodeReader = new PDF417Reader();

    private final List<TestResult> testResults = new ArrayList<>();

    public PDF417BlackBox4TestCase() {
        super("src/test/resources/blackbox/pdf417-4", null, PDF_417);
        testResults.add(new TestResult(3, 3, 0, 0, 0.0F));
    }

    @Test
    @Override
    public void testBlackBox() throws IOException {
        testPDF417BlackBoxCountingResults(true);
    }
}

