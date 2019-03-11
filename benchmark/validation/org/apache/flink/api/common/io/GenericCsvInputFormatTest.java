/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.api.common.io;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.fs.FileInputSplit;
import org.apache.flink.core.fs.Path;
import org.apache.flink.types.DoubleValue;
import org.apache.flink.types.IntValue;
import org.apache.flink.types.LongValue;
import org.apache.flink.types.StringValue;
import org.apache.flink.types.Value;
import org.junit.Assert;
import org.junit.Test;


public class GenericCsvInputFormatTest {
    private GenericCsvInputFormatTest.TestCsvInputFormat format;

    @Test
    public void testSparseFieldArray() {
        @SuppressWarnings("unchecked")
        Class<? extends Value>[] originalTypes = new Class[]{ IntValue.class, null, null, StringValue.class, null, DoubleValue.class };
        format.setFieldTypesGeneric(originalTypes);
        Assert.assertEquals(3, getNumberOfNonNullFields());
        Assert.assertEquals(6, getNumberOfFieldsTotal());
        Assert.assertTrue(Arrays.equals(originalTypes, getGenericFieldTypes()));
    }

    @Test
    public void testReadNoPosAll() throws IOException {
        try {
            final String fileContent = "111|222|333|444|555\n666|777|888|999|000|";
            final FileInputSplit split = DelimitedInputFormatTest.createTempFile(fileContent);
            final Configuration parameters = new Configuration();
            setFieldDelimiter("|");
            format.setFieldTypesGeneric(IntValue.class, IntValue.class, IntValue.class, IntValue.class, IntValue.class);
            format.configure(parameters);
            format.open(split);
            Value[] values = createIntValues(5);
            values = format.nextRecord(values);
            Assert.assertNotNull(values);
            Assert.assertEquals(111, getValue());
            Assert.assertEquals(222, getValue());
            Assert.assertEquals(333, getValue());
            Assert.assertEquals(444, getValue());
            Assert.assertEquals(555, getValue());
            values = format.nextRecord(values);
            Assert.assertNotNull(values);
            Assert.assertEquals(666, getValue());
            Assert.assertEquals(777, getValue());
            Assert.assertEquals(888, getValue());
            Assert.assertEquals(999, getValue());
            Assert.assertEquals(0, getValue());
            Assert.assertNull(format.nextRecord(values));
            Assert.assertTrue(reachedEnd());
        } catch (Exception ex) {
            Assert.fail(((("Test failed due to a " + (ex.getClass().getSimpleName())) + ": ") + (ex.getMessage())));
        }
    }

    @Test
    public void testReadNoPosAllDeflate() throws IOException {
        try {
            final String fileContent = "111|222|333|444|555\n666|777|888|999|000|";
            final FileInputSplit split = createTempDeflateFile(fileContent);
            final Configuration parameters = new Configuration();
            setFieldDelimiter("|");
            format.setFieldTypesGeneric(IntValue.class, IntValue.class, IntValue.class, IntValue.class, IntValue.class);
            format.configure(parameters);
            format.open(split);
            Value[] values = createIntValues(5);
            values = format.nextRecord(values);
            Assert.assertNotNull(values);
            Assert.assertEquals(111, getValue());
            Assert.assertEquals(222, getValue());
            Assert.assertEquals(333, getValue());
            Assert.assertEquals(444, getValue());
            Assert.assertEquals(555, getValue());
            values = format.nextRecord(values);
            Assert.assertNotNull(values);
            Assert.assertEquals(666, getValue());
            Assert.assertEquals(777, getValue());
            Assert.assertEquals(888, getValue());
            Assert.assertEquals(999, getValue());
            Assert.assertEquals(0, getValue());
            Assert.assertNull(format.nextRecord(values));
            Assert.assertTrue(reachedEnd());
        } catch (Exception ex) {
            Assert.fail(((("Test failed due to a " + (ex.getClass().getSimpleName())) + ": ") + (ex.getMessage())));
        }
    }

    @Test
    public void testReadNoPosAllGzip() throws IOException {
        try {
            final String fileContent = "111|222|333|444|555\n666|777|888|999|000|";
            final FileInputSplit split = createTempGzipFile(fileContent);
            final Configuration parameters = new Configuration();
            setFieldDelimiter("|");
            format.setFieldTypesGeneric(IntValue.class, IntValue.class, IntValue.class, IntValue.class, IntValue.class);
            format.configure(parameters);
            format.open(split);
            Value[] values = createIntValues(5);
            values = format.nextRecord(values);
            Assert.assertNotNull(values);
            Assert.assertEquals(111, getValue());
            Assert.assertEquals(222, getValue());
            Assert.assertEquals(333, getValue());
            Assert.assertEquals(444, getValue());
            Assert.assertEquals(555, getValue());
            values = format.nextRecord(values);
            Assert.assertNotNull(values);
            Assert.assertEquals(666, getValue());
            Assert.assertEquals(777, getValue());
            Assert.assertEquals(888, getValue());
            Assert.assertEquals(999, getValue());
            Assert.assertEquals(0, getValue());
            Assert.assertNull(format.nextRecord(values));
            Assert.assertTrue(reachedEnd());
        } catch (Exception ex) {
            Assert.fail(((("Test failed due to a " + (ex.getClass().getSimpleName())) + ": ") + (ex.getMessage())));
        }
    }

    @Test
    public void testReadNoPosFirstN() throws IOException {
        try {
            final String fileContent = "111|222|333|444|555|\n666|777|888|999|000|";
            final FileInputSplit split = DelimitedInputFormatTest.createTempFile(fileContent);
            final Configuration parameters = new Configuration();
            setFieldDelimiter("|");
            format.setFieldTypesGeneric(IntValue.class, IntValue.class);
            format.configure(parameters);
            format.open(split);
            Value[] values = createIntValues(2);
            // if this would parse all, we would get an index out of bounds exception
            values = format.nextRecord(values);
            Assert.assertNotNull(values);
            Assert.assertEquals(111, getValue());
            Assert.assertEquals(222, getValue());
            values = format.nextRecord(values);
            Assert.assertNotNull(values);
            Assert.assertEquals(666, getValue());
            Assert.assertEquals(777, getValue());
            Assert.assertNull(format.nextRecord(values));
            Assert.assertTrue(reachedEnd());
        } catch (Exception ex) {
            Assert.fail(((("Test failed due to a " + (ex.getClass().getSimpleName())) + ": ") + (ex.getMessage())));
        }
    }

    @Test
    public void testSparseParse() {
        try {
            final String fileContent = "111|222|333|444|555|666|777|888|999|000|\n" + "000|999|888|777|666|555|444|333|222|111|";
            final FileInputSplit split = DelimitedInputFormatTest.createTempFile(fileContent);
            final Configuration parameters = new Configuration();
            setFieldDelimiter("|");
            format.setFieldTypesGeneric(IntValue.class, null, null, IntValue.class, null, null, null, IntValue.class);
            format.configure(parameters);
            format.open(split);
            Value[] values = createIntValues(3);
            values = format.nextRecord(values);
            Assert.assertNotNull(values);
            Assert.assertEquals(111, getValue());
            Assert.assertEquals(444, getValue());
            Assert.assertEquals(888, getValue());
            values = format.nextRecord(values);
            Assert.assertNotNull(values);
            Assert.assertEquals(0, getValue());
            Assert.assertEquals(777, getValue());
            Assert.assertEquals(333, getValue());
            Assert.assertNull(format.nextRecord(values));
            Assert.assertTrue(reachedEnd());
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
            Assert.fail("Test erroneous");
        }
    }

    @Test
    public void testLongLongLong() {
        try {
            final String fileContent = "1,2,3\n3,2,1";
            final FileInputSplit split = DelimitedInputFormatTest.createTempFile(fileContent);
            final Configuration parameters = new Configuration();
            setFieldDelimiter(",");
            format.setFieldTypesGeneric(LongValue.class, LongValue.class, LongValue.class);
            format.configure(parameters);
            format.open(split);
            Value[] values = createLongValues(3);
            values = format.nextRecord(values);
            Assert.assertNotNull(values);
            Assert.assertEquals(1L, getValue());
            Assert.assertEquals(2L, getValue());
            Assert.assertEquals(3L, getValue());
            values = format.nextRecord(values);
            Assert.assertNotNull(values);
            Assert.assertEquals(3L, getValue());
            Assert.assertEquals(2L, getValue());
            Assert.assertEquals(1L, getValue());
            Assert.assertNull(format.nextRecord(values));
            Assert.assertTrue(reachedEnd());
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
            Assert.fail("Test erroneous");
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSparseParseWithIndices() {
        try {
            final String fileContent = "111|222|333|444|555|666|777|888|999|000|\n000|999|888|777|666|555|444|333|222|111|";
            final FileInputSplit split = DelimitedInputFormatTest.createTempFile(fileContent);
            final Configuration parameters = new Configuration();
            setFieldDelimiter("|");
            setFieldsGeneric(new int[]{ 0, 3, 7 }, ((Class<? extends Value>[]) (new Class[]{ IntValue.class, IntValue.class, IntValue.class })));
            format.configure(parameters);
            format.open(split);
            Value[] values = createIntValues(3);
            values = format.nextRecord(values);
            Assert.assertNotNull(values);
            Assert.assertEquals(111, getValue());
            Assert.assertEquals(444, getValue());
            Assert.assertEquals(888, getValue());
            values = format.nextRecord(values);
            Assert.assertNotNull(values);
            Assert.assertEquals(0, getValue());
            Assert.assertEquals(777, getValue());
            Assert.assertEquals(333, getValue());
            Assert.assertNull(format.nextRecord(values));
            Assert.assertTrue(reachedEnd());
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
            Assert.fail("Test erroneous");
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSparseParseWithIndicesMultiCharDelimiter() {
        try {
            final String fileContent = "111|-|222|-|333|-|444|-|555|-|666|-|777|-|888|-|999|-|000|-|\n" + (("000|-|999|-|888|-|777|-|666|-|555|-|444|-|333|-|222|-|111\n" + "555|-|999|-|888|-|111|-|666|-|555|-|444|-|777|-|222|-|111|-|\n") + "22222|-|99999|-|8|-|99999999|-|6666666|-|5|-|4444|-|8|-|22222|-|1\n");
            final FileInputSplit split = DelimitedInputFormatTest.createTempFile(fileContent);
            final Configuration parameters = new Configuration();
            setFieldDelimiter("|-|");
            setFieldsGeneric(new int[]{ 0, 3, 7 }, ((Class<? extends Value>[]) (new Class[]{ IntValue.class, IntValue.class, IntValue.class })));
            format.configure(parameters);
            format.open(split);
            Value[] values = createIntValues(3);
            values = format.nextRecord(values);
            Assert.assertNotNull(values);
            Assert.assertEquals(111, getValue());
            Assert.assertEquals(444, getValue());
            Assert.assertEquals(888, getValue());
            values = format.nextRecord(values);
            Assert.assertNotNull(values);
            Assert.assertEquals(0, getValue());
            Assert.assertEquals(777, getValue());
            Assert.assertEquals(333, getValue());
            values = format.nextRecord(values);
            Assert.assertNotNull(values);
            Assert.assertEquals(555, getValue());
            Assert.assertEquals(111, getValue());
            Assert.assertEquals(777, getValue());
            values = format.nextRecord(values);
            Assert.assertNotNull(values);
            Assert.assertEquals(22222, getValue());
            Assert.assertEquals(99999999, getValue());
            Assert.assertEquals(8, getValue());
            Assert.assertNull(format.nextRecord(values));
            Assert.assertTrue(reachedEnd());
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
            Assert.fail("Test erroneous");
        }
    }

    @Test
    public void testReadTooShortInput() throws IOException {
        try {
            final String fileContent = "111|222|333|444\n666|777|888|999";
            final FileInputSplit split = DelimitedInputFormatTest.createTempFile(fileContent);
            final Configuration parameters = new Configuration();
            setFieldDelimiter("|");
            format.setFieldTypesGeneric(IntValue.class, IntValue.class, IntValue.class, IntValue.class, IntValue.class);
            format.configure(parameters);
            format.open(split);
            Value[] values = createIntValues(5);
            try {
                format.nextRecord(values);
                Assert.fail("Should have thrown a parse exception on too short input.");
            } catch (ParseException e) {
                // all is well
            }
        } catch (Exception ex) {
            Assert.fail(((("Test failed due to a " + (ex.getClass().getSimpleName())) + ": ") + (ex.getMessage())));
        }
    }

    @Test
    public void testReadTooShortInputLenient() throws IOException {
        try {
            final String fileContent = "666|777|888|999|555\n111|222|333|444\n666|777|888|999|555";
            final FileInputSplit split = DelimitedInputFormatTest.createTempFile(fileContent);
            final Configuration parameters = new Configuration();
            setFieldDelimiter("|");
            format.setFieldTypesGeneric(IntValue.class, IntValue.class, IntValue.class, IntValue.class, IntValue.class);
            setLenient(true);
            format.configure(parameters);
            format.open(split);
            Value[] values = createIntValues(5);
            Assert.assertNotNull(format.nextRecord(values));// line okay

            Assert.assertNull(format.nextRecord(values));// line too short

            Assert.assertNotNull(format.nextRecord(values));// line okay

        } catch (Exception ex) {
            Assert.fail(((("Test failed due to a " + (ex.getClass().getSimpleName())) + ": ") + (ex.getMessage())));
        }
    }

    @Test
    public void testReadInvalidContents() throws IOException {
        try {
            final String fileContent = "abc|222|def|444\nkkz|777|888|hhg";
            final FileInputSplit split = DelimitedInputFormatTest.createTempFile(fileContent);
            final Configuration parameters = new Configuration();
            setFieldDelimiter("|");
            setFieldTypesGeneric(StringValue.class, IntValue.class, StringValue.class, IntValue.class);
            format.configure(parameters);
            format.open(split);
            Value[] values = new Value[]{ new StringValue(), new IntValue(), new StringValue(), new IntValue() };
            Assert.assertNotNull(format.nextRecord(values));
            try {
                format.nextRecord(values);
                Assert.fail("Input format accepted on invalid input.");
            } catch (ParseException ignored) {
            }
        } catch (Exception ex) {
            Assert.fail(((("Test failed due to a " + (ex.getClass().getSimpleName())) + ": ") + (ex.getMessage())));
        }
    }

    @Test
    public void testReadInvalidContentsLenient() {
        try {
            final String fileContent = "abc|222|def|444\nkkz|777|888|hhg";
            final FileInputSplit split = DelimitedInputFormatTest.createTempFile(fileContent);
            final Configuration parameters = new Configuration();
            setFieldDelimiter("|");
            setFieldTypesGeneric(StringValue.class, IntValue.class, StringValue.class, IntValue.class);
            setLenient(true);
            format.configure(parameters);
            format.open(split);
            Value[] values = new Value[]{ new StringValue(), new IntValue(), new StringValue(), new IntValue() };
            Assert.assertNotNull(format.nextRecord(values));
            Assert.assertNull(format.nextRecord(values));
        } catch (Exception ex) {
            Assert.fail(((("Test failed due to a " + (ex.getClass().getSimpleName())) + ": ") + (ex.getMessage())));
        }
    }

    @Test
    public void testReadInvalidContentsLenientWithSkipping() {
        try {
            final String fileContent = "abc|dfgsdf|777|444\n"// good line
             + (("kkz|777|foobar|hhg\n"// wrong data type in field
             + "kkz|777foobarhhg  \n")// too short, a skipped field never ends
             + "xyx|ignored|42|\n");
            // another good line
            final FileInputSplit split = DelimitedInputFormatTest.createTempFile(fileContent);
            final Configuration parameters = new Configuration();
            setFieldDelimiter("|");
            format.setFieldTypesGeneric(StringValue.class, null, IntValue.class);
            setLenient(true);
            format.configure(parameters);
            format.open(split);
            Value[] values = new Value[]{ new StringValue(), new IntValue() };
            Assert.assertNotNull(format.nextRecord(values));
            Assert.assertNull(format.nextRecord(values));
            Assert.assertNull(format.nextRecord(values));
            Assert.assertNotNull(format.nextRecord(values));
        } catch (Exception ex) {
            Assert.fail(((("Test failed due to a " + (ex.getClass().getSimpleName())) + ": ") + (ex.getMessage())));
        }
    }

    @Test
    public void testReadWithCharset() throws IOException {
        // Unicode row fragments
        String[] records = new String[]{ "\u020e\u021f", "Flink", "\u020b\u020f" };
        // Unicode delimiter
        String delimiter = "\u05c0\u05c0";
        String fileContent = StringUtils.join(records, delimiter);
        // StringValueParser does not use charset so rely on StringParser
        GenericCsvInputFormat<String[]> format = new GenericCsvInputFormat<String[]>() {
            @Override
            public String[] readRecord(String[] target, byte[] bytes, int offset, int numBytes) throws IOException {
                return parseRecord(target, bytes, offset, numBytes) ? target : null;
            }
        };
        format.setFilePath("file:///some/file/that/will/not/be/read");
        for (String charset : new String[]{ "UTF-8", "UTF-16BE", "UTF-16LE" }) {
            File tempFile = File.createTempFile("test_contents", "tmp");
            tempFile.deleteOnExit();
            // write string with proper encoding
            try (Writer out = new OutputStreamWriter(new FileOutputStream(tempFile), charset)) {
                out.write(fileContent);
            }
            FileInputSplit split = new FileInputSplit(0, new Path(tempFile.toURI().toString()), 0, tempFile.length(), new String[]{ "localhost" });
            format.setFieldDelimiter(delimiter);
            format.setFieldTypesGeneric(String.class, String.class, String.class);
            // use the same encoding to parse the file as used to read the file;
            // the field delimiter is reinterpreted when the charset is set
            format.setCharset(charset);
            format.configure(new Configuration());
            format.open(split);
            String[] values = new String[]{ "", "", "" };
            values = format.nextRecord(values);
            // validate results
            Assert.assertNotNull(values);
            for (int i = 0; i < (records.length); i++) {
                Assert.assertEquals(records[i], values[i]);
            }
            Assert.assertNull(format.nextRecord(values));
            Assert.assertTrue(format.reachedEnd());
        }
        format.close();
    }

    @Test
    public void readWithEmptyField() {
        try {
            final String fileContent = "abc|def|ghijk\nabc||hhg\n|||";
            final FileInputSplit split = DelimitedInputFormatTest.createTempFile(fileContent);
            final Configuration parameters = new Configuration();
            setFieldDelimiter("|");
            format.setFieldTypesGeneric(StringValue.class, StringValue.class, StringValue.class);
            format.configure(parameters);
            format.open(split);
            Value[] values = new Value[]{ new StringValue(), new StringValue(), new StringValue() };
            values = format.nextRecord(values);
            Assert.assertNotNull(values);
            Assert.assertEquals("abc", getValue());
            Assert.assertEquals("def", getValue());
            Assert.assertEquals("ghijk", getValue());
            values = format.nextRecord(values);
            Assert.assertNotNull(values);
            Assert.assertEquals("abc", getValue());
            Assert.assertEquals("", getValue());
            Assert.assertEquals("hhg", getValue());
            values = format.nextRecord(values);
            Assert.assertNotNull(values);
            Assert.assertEquals("", getValue());
            Assert.assertEquals("", getValue());
            Assert.assertEquals("", getValue());
        } catch (Exception ex) {
            Assert.fail(((("Test failed due to a " + (ex.getClass().getSimpleName())) + ": ") + (ex.getMessage())));
        }
    }

    @Test
    public void readWithParseQuotedStrings() {
        try {
            final String fileContent = "\"ab\\\"c\"|\"def\"\n\"ghijk\"|\"abc\"";
            final FileInputSplit split = DelimitedInputFormatTest.createTempFile(fileContent);
            final Configuration parameters = new Configuration();
            setFieldDelimiter("|");
            format.setFieldTypesGeneric(StringValue.class, StringValue.class);
            enableQuotedStringParsing('"');
            format.configure(parameters);
            format.open(split);
            Value[] values = new Value[]{ new StringValue(), new StringValue() };
            values = format.nextRecord(values);
            Assert.assertNotNull(values);
            Assert.assertEquals("ab\\\"c", getValue());
            Assert.assertEquals("def", getValue());
            values = format.nextRecord(values);
            Assert.assertNotNull(values);
            Assert.assertEquals("ghijk", getValue());
            Assert.assertEquals("abc", getValue());
        } catch (Exception ex) {
            Assert.fail(((("Test failed due to a " + (ex.getClass().getSimpleName())) + ": ") + (ex.getMessage())));
        }
    }

    @Test
    public void readWithHeaderLine() {
        try {
            final String fileContent = "colname-1|colname-2|some name 3|column four|\n" + ("123|abc|456|def|\n" + "987|xyz|654|pqr|\n");
            final FileInputSplit split = DelimitedInputFormatTest.createTempFile(fileContent);
            final Configuration parameters = new Configuration();
            setFieldDelimiter("|");
            setFieldTypesGeneric(IntValue.class, StringValue.class, IntValue.class, StringValue.class);
            setSkipFirstLineAsHeader(true);
            format.configure(parameters);
            format.open(split);
            Value[] values = new Value[]{ new IntValue(), new StringValue(), new IntValue(), new StringValue() };
            // first line is skipped as header
            Assert.assertNotNull(format.nextRecord(values));// first row (= second line)

            Assert.assertNotNull(format.nextRecord(values));// second row (= third line)

            Assert.assertNull(format.nextRecord(values));// exhausted

            Assert.assertTrue(reachedEnd());// exhausted

        } catch (Exception ex) {
            Assert.fail(((("Test failed due to a " + (ex.getClass().getSimpleName())) + ": ") + (ex.getMessage())));
        }
    }

    @Test
    public void readWithHeaderLineAndInvalidIntermediate() {
        try {
            final String fileContent = "colname-1|colname-2|some name 3|column four|\n" + (("123|abc|456|def|\n" + "colname-1|colname-2|some name 3|column four|\n")// repeated header in the middle
             + "987|xyz|654|pqr|\n");
            final FileInputSplit split = DelimitedInputFormatTest.createTempFile(fileContent);
            final Configuration parameters = new Configuration();
            setFieldDelimiter("|");
            setFieldTypesGeneric(IntValue.class, StringValue.class, IntValue.class, StringValue.class);
            setSkipFirstLineAsHeader(true);
            format.configure(parameters);
            format.open(split);
            Value[] values = new Value[]{ new IntValue(), new StringValue(), new IntValue(), new StringValue() };
            // first line is skipped as header
            Assert.assertNotNull(format.nextRecord(values));// first row (= second line)

            try {
                format.nextRecord(values);
                Assert.fail("Format accepted invalid line.");
            } catch (ParseException e) {
                // as we expected
            }
        } catch (Exception ex) {
            Assert.fail(((("Test failed due to a " + (ex.getClass().getSimpleName())) + ": ") + (ex.getMessage())));
        }
    }

    private static final class TestCsvInputFormat extends GenericCsvInputFormat<Value[]> {
        private static final long serialVersionUID = 2653609265252951059L;

        @Override
        public Value[] readRecord(Value[] target, byte[] bytes, int offset, int numBytes) {
            return parseRecord(target, bytes, offset, numBytes) ? target : null;
        }
    }
}

