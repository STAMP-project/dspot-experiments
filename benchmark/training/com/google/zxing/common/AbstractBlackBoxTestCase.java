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
package com.google.zxing.common;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.Reader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Sean Owen
 * @author dswitkin@google.com (Daniel Switkin)
 */
public abstract class AbstractBlackBoxTestCase extends Assert {
    private static final Logger log = Logger.getLogger(AbstractBlackBoxTestCase.class.getSimpleName());

    private final Path testBase;

    private final Reader barcodeReader;

    private final BarcodeFormat expectedFormat;

    private final List<TestResult> testResults;

    protected AbstractBlackBoxTestCase(String testBasePathSuffix, Reader barcodeReader, BarcodeFormat expectedFormat) {
        this.testBase = AbstractBlackBoxTestCase.buildTestBase(testBasePathSuffix);
        this.barcodeReader = barcodeReader;
        this.expectedFormat = expectedFormat;
        testResults = new ArrayList<>();
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s%6$s%n");
    }

    // This workaround is used because AbstractNegativeBlackBoxTestCase overrides this method but does
    // not return SummaryResults.
    @Test
    public void testBlackBox() throws IOException {
        testBlackBoxCountingResults(true);
    }
}

