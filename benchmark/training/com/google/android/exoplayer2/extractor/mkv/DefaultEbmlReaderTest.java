/**
 * Copyright (C) 2016 The Android Open Source Project
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
package com.google.android.exoplayer2.extractor.mkv;


import com.google.android.exoplayer2.extractor.ExtractorInput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/**
 * Tests {@link DefaultEbmlReader}.
 */
@RunWith(RobolectricTestRunner.class)
public class DefaultEbmlReaderTest {
    @Test
    public void testMasterElement() throws IOException, InterruptedException {
        ExtractorInput input = DefaultEbmlReaderTest.createTestInput(26, 69, 223, 163, 132, 66, 133, 129, 1);
        DefaultEbmlReaderTest.TestOutput expected = new DefaultEbmlReaderTest.TestOutput();
        expected.startMasterElement(DefaultEbmlReaderTest.TestOutput.ID_EBML, 5, 4);
        expected.integerElement(DefaultEbmlReaderTest.TestOutput.ID_DOC_TYPE_READ_VERSION, 1);
        expected.endMasterElement(DefaultEbmlReaderTest.TestOutput.ID_EBML);
        DefaultEbmlReaderTest.assertEvents(input, expected.events);
    }

    @Test
    public void testMasterElementEmpty() throws IOException, InterruptedException {
        ExtractorInput input = DefaultEbmlReaderTest.createTestInput(24, 83, 128, 103, 128);
        DefaultEbmlReaderTest.TestOutput expected = new DefaultEbmlReaderTest.TestOutput();
        expected.startMasterElement(DefaultEbmlReaderTest.TestOutput.ID_SEGMENT, 5, 0);
        expected.endMasterElement(DefaultEbmlReaderTest.TestOutput.ID_SEGMENT);
        DefaultEbmlReaderTest.assertEvents(input, expected.events);
    }

    @Test
    public void testUnsignedIntegerElement() throws IOException, InterruptedException {
        // 0xFE is chosen because for signed integers it should be interpreted as -2
        ExtractorInput input = DefaultEbmlReaderTest.createTestInput(66, 247, 129, 254);
        DefaultEbmlReaderTest.TestOutput expected = new DefaultEbmlReaderTest.TestOutput();
        expected.integerElement(DefaultEbmlReaderTest.TestOutput.ID_EBML_READ_VERSION, 254);
        DefaultEbmlReaderTest.assertEvents(input, expected.events);
    }

    @Test
    public void testUnsignedIntegerElementLarge() throws IOException, InterruptedException {
        ExtractorInput input = DefaultEbmlReaderTest.createTestInput(66, 247, 136, 127, 255, 255, 255, 255, 255, 255, 255);
        DefaultEbmlReaderTest.TestOutput expected = new DefaultEbmlReaderTest.TestOutput();
        expected.integerElement(DefaultEbmlReaderTest.TestOutput.ID_EBML_READ_VERSION, Long.MAX_VALUE);
        DefaultEbmlReaderTest.assertEvents(input, expected.events);
    }

    @Test
    public void testUnsignedIntegerElementTooLargeBecomesNegative() throws IOException, InterruptedException {
        ExtractorInput input = DefaultEbmlReaderTest.createTestInput(66, 247, 136, 255, 255, 255, 255, 255, 255, 255, 255);
        DefaultEbmlReaderTest.TestOutput expected = new DefaultEbmlReaderTest.TestOutput();
        expected.integerElement(DefaultEbmlReaderTest.TestOutput.ID_EBML_READ_VERSION, (-1));
        DefaultEbmlReaderTest.assertEvents(input, expected.events);
    }

    @Test
    public void testStringElement() throws IOException, InterruptedException {
        ExtractorInput input = DefaultEbmlReaderTest.createTestInput(66, 130, 134, 65, 98, 99, 49, 50, 51);
        DefaultEbmlReaderTest.TestOutput expected = new DefaultEbmlReaderTest.TestOutput();
        expected.stringElement(DefaultEbmlReaderTest.TestOutput.ID_DOC_TYPE, "Abc123");
        DefaultEbmlReaderTest.assertEvents(input, expected.events);
    }

    @Test
    public void testStringElementWithZeroPadding() throws IOException, InterruptedException {
        ExtractorInput input = DefaultEbmlReaderTest.createTestInput(66, 130, 134, 65, 98, 99, 0, 0, 0);
        DefaultEbmlReaderTest.TestOutput expected = new DefaultEbmlReaderTest.TestOutput();
        expected.stringElement(DefaultEbmlReaderTest.TestOutput.ID_DOC_TYPE, "Abc");
        DefaultEbmlReaderTest.assertEvents(input, expected.events);
    }

    @Test
    public void testStringElementEmpty() throws IOException, InterruptedException {
        ExtractorInput input = DefaultEbmlReaderTest.createTestInput(66, 130, 128);
        DefaultEbmlReaderTest.TestOutput expected = new DefaultEbmlReaderTest.TestOutput();
        expected.stringElement(DefaultEbmlReaderTest.TestOutput.ID_DOC_TYPE, "");
        DefaultEbmlReaderTest.assertEvents(input, expected.events);
    }

    @Test
    public void testFloatElementFourBytes() throws IOException, InterruptedException {
        ExtractorInput input = DefaultEbmlReaderTest.createTestInput(68, 137, 132, 63, 128, 0, 0);
        DefaultEbmlReaderTest.TestOutput expected = new DefaultEbmlReaderTest.TestOutput();
        expected.floatElement(DefaultEbmlReaderTest.TestOutput.ID_DURATION, 1.0);
        DefaultEbmlReaderTest.assertEvents(input, expected.events);
    }

    @Test
    public void testFloatElementEightBytes() throws IOException, InterruptedException {
        ExtractorInput input = DefaultEbmlReaderTest.createTestInput(68, 137, 136, 192, 0, 0, 0, 0, 0, 0, 0);
        DefaultEbmlReaderTest.TestOutput expected = new DefaultEbmlReaderTest.TestOutput();
        expected.floatElement(DefaultEbmlReaderTest.TestOutput.ID_DURATION, (-2.0));
        DefaultEbmlReaderTest.assertEvents(input, expected.events);
    }

    @Test
    public void testBinaryElement() throws IOException, InterruptedException {
        ExtractorInput input = DefaultEbmlReaderTest.createTestInput(163, 136, 1, 2, 3, 4, 5, 6, 7, 8);
        DefaultEbmlReaderTest.TestOutput expected = new DefaultEbmlReaderTest.TestOutput();
        expected.binaryElement(DefaultEbmlReaderTest.TestOutput.ID_SIMPLE_BLOCK, 8, DefaultEbmlReaderTest.createTestInput(1, 2, 3, 4, 5, 6, 7, 8));
        DefaultEbmlReaderTest.assertEvents(input, expected.events);
    }

    /**
     * An {@link EbmlReaderOutput} that records each event callback.
     */
    private static final class TestOutput implements EbmlReaderOutput {
        // Element IDs
        private static final int ID_EBML = 440786851;

        private static final int ID_EBML_READ_VERSION = 17143;

        private static final int ID_DOC_TYPE = 17026;

        private static final int ID_DOC_TYPE_READ_VERSION = 17029;

        private static final int ID_SEGMENT = 408125543;

        private static final int ID_DURATION = 17545;

        private static final int ID_SIMPLE_BLOCK = 163;

        private final List<String> events = new ArrayList<>();

        @Override
        @ElementType
        public int getElementType(int id) {
            switch (id) {
                case DefaultEbmlReaderTest.TestOutput.ID_EBML :
                case DefaultEbmlReaderTest.TestOutput.ID_SEGMENT :
                    return TYPE_MASTER;
                case DefaultEbmlReaderTest.TestOutput.ID_EBML_READ_VERSION :
                case DefaultEbmlReaderTest.TestOutput.ID_DOC_TYPE_READ_VERSION :
                    return TYPE_UNSIGNED_INT;
                case DefaultEbmlReaderTest.TestOutput.ID_DOC_TYPE :
                    return TYPE_STRING;
                case DefaultEbmlReaderTest.TestOutput.ID_SIMPLE_BLOCK :
                    return TYPE_BINARY;
                case DefaultEbmlReaderTest.TestOutput.ID_DURATION :
                    return TYPE_FLOAT;
                default :
                    return TYPE_UNKNOWN;
            }
        }

        @Override
        public boolean isLevel1Element(int id) {
            return false;
        }

        @Override
        public void startMasterElement(int id, long contentPosition, long contentSize) {
            events.add(DefaultEbmlReaderTest.TestOutput.formatEvent(id, ((("start contentPosition=" + contentPosition) + " contentSize=") + contentSize)));
        }

        @Override
        public void endMasterElement(int id) {
            events.add(DefaultEbmlReaderTest.TestOutput.formatEvent(id, "end"));
        }

        @Override
        public void integerElement(int id, long value) {
            events.add(DefaultEbmlReaderTest.TestOutput.formatEvent(id, ("integer=" + (String.valueOf(value)))));
        }

        @Override
        public void floatElement(int id, double value) {
            events.add(DefaultEbmlReaderTest.TestOutput.formatEvent(id, ("float=" + (String.valueOf(value)))));
        }

        @Override
        public void stringElement(int id, String value) {
            events.add(DefaultEbmlReaderTest.TestOutput.formatEvent(id, ("string=" + value)));
        }

        @Override
        public void binaryElement(int id, int contentSize, ExtractorInput input) throws IOException, InterruptedException {
            byte[] bytes = new byte[contentSize];
            input.readFully(bytes, 0, contentSize);
            events.add(DefaultEbmlReaderTest.TestOutput.formatEvent(id, ("bytes=" + (Arrays.toString(bytes)))));
        }

        private static String formatEvent(int id, String event) {
            return (("[" + (Integer.toHexString(id))) + "] ") + event;
        }
    }
}

