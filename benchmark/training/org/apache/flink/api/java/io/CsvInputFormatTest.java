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
package org.apache.flink.api.java.io;


import ConfigConstants.DEFAULT_CHARSET;
import FieldParser.ParseErrorState;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import org.apache.flink.api.common.io.ParseException;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.tuple.Tuple5;
import org.apache.flink.api.java.tuple.Tuple6;
import org.apache.flink.api.java.typeutils.PojoTypeInfo;
import org.apache.flink.api.java.typeutils.TupleTypeInfo;
import org.apache.flink.api.java.typeutils.TypeExtractor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.fs.FileInputSplit;
import org.apache.flink.core.fs.Path;
import org.apache.flink.types.parser.StringParser;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link CsvInputFormat}.
 */
public class CsvInputFormatTest {
    private static final Path PATH = new Path("an/ignored/file/");

    // Static variables for testing the removal of \r\n to \n
    private static final String FIRST_PART = "That is the first part";

    private static final String SECOND_PART = "That is the second part";

    @Test
    public void testSplitCsvInputStreamInLargeBuffer() throws Exception {
        testSplitCsvInputStream((1024 * 1024), false);
    }

    @Test
    public void testSplitCsvInputStreamInSmallBuffer() throws Exception {
        testSplitCsvInputStream(2, false);
    }

    @Test
    public void ignoreInvalidLinesAndGetOffsetInLargeBuffer() {
        ignoreInvalidLines((1024 * 1024));
    }

    @Test
    public void ignoreInvalidLinesAndGetOffsetInSmallBuffer() {
        ignoreInvalidLines(2);
    }

    @Test
    public void ignoreSingleCharPrefixComments() {
        try {
            final String fileContent = "#description of the data\n" + ((("#successive commented line\n" + "this is|1|2.0|\n") + "a test|3|4.0|\n") + "#next|5|6.0|\n");
            final FileInputSplit split = createTempFile(fileContent);
            final TupleTypeInfo<Tuple3<String, Integer, Double>> typeInfo = TupleTypeInfo.getBasicTupleTypeInfo(String.class, Integer.class, Double.class);
            final CsvInputFormat<Tuple3<String, Integer, Double>> format = new TupleCsvInputFormat<Tuple3<String, Integer, Double>>(CsvInputFormatTest.PATH, "\n", "|", typeInfo);
            format.setCommentPrefix("#");
            final Configuration parameters = new Configuration();
            format.configure(parameters);
            format.open(split);
            Tuple3<String, Integer, Double> result = new Tuple3<String, Integer, Double>();
            result = format.nextRecord(result);
            Assert.assertNotNull(result);
            Assert.assertEquals("this is", result.f0);
            Assert.assertEquals(Integer.valueOf(1), result.f1);
            Assert.assertEquals(new Double(2.0), result.f2);
            result = format.nextRecord(result);
            Assert.assertNotNull(result);
            Assert.assertEquals("a test", result.f0);
            Assert.assertEquals(Integer.valueOf(3), result.f1);
            Assert.assertEquals(new Double(4.0), result.f2);
            result = format.nextRecord(result);
            Assert.assertNull(result);
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail(((("Test failed due to a " + (ex.getClass().getName())) + ": ") + (ex.getMessage())));
        }
    }

    @Test
    public void ignoreMultiCharPrefixComments() {
        try {
            final String fileContent = "//description of the data\n" + ((("//successive commented line\n" + "this is|1|2.0|\n") + "a test|3|4.0|\n") + "//next|5|6.0|\n");
            final FileInputSplit split = createTempFile(fileContent);
            final TupleTypeInfo<Tuple3<String, Integer, Double>> typeInfo = TupleTypeInfo.getBasicTupleTypeInfo(String.class, Integer.class, Double.class);
            final CsvInputFormat<Tuple3<String, Integer, Double>> format = new TupleCsvInputFormat<Tuple3<String, Integer, Double>>(CsvInputFormatTest.PATH, "\n", "|", typeInfo);
            format.setCommentPrefix("//");
            final Configuration parameters = new Configuration();
            format.configure(parameters);
            format.open(split);
            Tuple3<String, Integer, Double> result = new Tuple3<String, Integer, Double>();
            result = format.nextRecord(result);
            Assert.assertNotNull(result);
            Assert.assertEquals("this is", result.f0);
            Assert.assertEquals(Integer.valueOf(1), result.f1);
            Assert.assertEquals(new Double(2.0), result.f2);
            result = format.nextRecord(result);
            Assert.assertNotNull(result);
            Assert.assertEquals("a test", result.f0);
            Assert.assertEquals(Integer.valueOf(3), result.f1);
            Assert.assertEquals(new Double(4.0), result.f2);
            result = format.nextRecord(result);
            Assert.assertNull(result);
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail(((("Test failed due to a " + (ex.getClass().getName())) + ": ") + (ex.getMessage())));
        }
    }

    @Test
    public void readStringFields() {
        try {
            final String fileContent = "abc|def|ghijk\nabc||hhg\n|||";
            final FileInputSplit split = createTempFile(fileContent);
            final TupleTypeInfo<Tuple3<String, String, String>> typeInfo = TupleTypeInfo.getBasicTupleTypeInfo(String.class, String.class, String.class);
            final CsvInputFormat<Tuple3<String, String, String>> format = new TupleCsvInputFormat<Tuple3<String, String, String>>(CsvInputFormatTest.PATH, "\n", "|", typeInfo);
            final Configuration parameters = new Configuration();
            format.configure(parameters);
            format.open(split);
            Tuple3<String, String, String> result = new Tuple3<String, String, String>();
            result = format.nextRecord(result);
            Assert.assertNotNull(result);
            Assert.assertEquals("abc", result.f0);
            Assert.assertEquals("def", result.f1);
            Assert.assertEquals("ghijk", result.f2);
            result = format.nextRecord(result);
            Assert.assertNotNull(result);
            Assert.assertEquals("abc", result.f0);
            Assert.assertEquals("", result.f1);
            Assert.assertEquals("hhg", result.f2);
            result = format.nextRecord(result);
            Assert.assertNotNull(result);
            Assert.assertEquals("", result.f0);
            Assert.assertEquals("", result.f1);
            Assert.assertEquals("", result.f2);
            result = format.nextRecord(result);
            Assert.assertNull(result);
            Assert.assertTrue(format.reachedEnd());
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail(((("Test failed due to a " + (ex.getClass().getName())) + ": ") + (ex.getMessage())));
        }
    }

    @Test
    public void readMixedQuotedStringFields() {
        try {
            final String fileContent = "@a|b|c@|def|@ghijk@\nabc||@|hhg@\n|||";
            final FileInputSplit split = createTempFile(fileContent);
            final TupleTypeInfo<Tuple3<String, String, String>> typeInfo = TupleTypeInfo.getBasicTupleTypeInfo(String.class, String.class, String.class);
            final CsvInputFormat<Tuple3<String, String, String>> format = new TupleCsvInputFormat<Tuple3<String, String, String>>(CsvInputFormatTest.PATH, "\n", "|", typeInfo);
            final Configuration parameters = new Configuration();
            format.configure(parameters);
            format.enableQuotedStringParsing('@');
            format.open(split);
            Tuple3<String, String, String> result = new Tuple3<String, String, String>();
            result = format.nextRecord(result);
            Assert.assertNotNull(result);
            Assert.assertEquals("a|b|c", result.f0);
            Assert.assertEquals("def", result.f1);
            Assert.assertEquals("ghijk", result.f2);
            result = format.nextRecord(result);
            Assert.assertNotNull(result);
            Assert.assertEquals("abc", result.f0);
            Assert.assertEquals("", result.f1);
            Assert.assertEquals("|hhg", result.f2);
            result = format.nextRecord(result);
            Assert.assertNotNull(result);
            Assert.assertEquals("", result.f0);
            Assert.assertEquals("", result.f1);
            Assert.assertEquals("", result.f2);
            result = format.nextRecord(result);
            Assert.assertNull(result);
            Assert.assertTrue(format.reachedEnd());
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail(((("Test failed due to a " + (ex.getClass().getName())) + ": ") + (ex.getMessage())));
        }
    }

    @Test
    public void readStringFieldsWithTrailingDelimiters() {
        try {
            final String fileContent = "abc|-def|-ghijk\nabc|-|-hhg\n|-|-|-\n";
            final FileInputSplit split = createTempFile(fileContent);
            final TupleTypeInfo<Tuple3<String, String, String>> typeInfo = TupleTypeInfo.getBasicTupleTypeInfo(String.class, String.class, String.class);
            final CsvInputFormat<Tuple3<String, String, String>> format = new TupleCsvInputFormat<Tuple3<String, String, String>>(CsvInputFormatTest.PATH, typeInfo);
            format.setFieldDelimiter("|-");
            format.configure(new Configuration());
            format.open(split);
            Tuple3<String, String, String> result = new Tuple3<String, String, String>();
            result = format.nextRecord(result);
            Assert.assertNotNull(result);
            Assert.assertEquals("abc", result.f0);
            Assert.assertEquals("def", result.f1);
            Assert.assertEquals("ghijk", result.f2);
            result = format.nextRecord(result);
            Assert.assertNotNull(result);
            Assert.assertEquals("abc", result.f0);
            Assert.assertEquals("", result.f1);
            Assert.assertEquals("hhg", result.f2);
            result = format.nextRecord(result);
            Assert.assertNotNull(result);
            Assert.assertEquals("", result.f0);
            Assert.assertEquals("", result.f1);
            Assert.assertEquals("", result.f2);
            result = format.nextRecord(result);
            Assert.assertNull(result);
            Assert.assertTrue(format.reachedEnd());
        } catch (Exception ex) {
            Assert.fail(((("Test failed due to a " + (ex.getClass().getName())) + ": ") + (ex.getMessage())));
        }
    }

    @Test
    public void testTailingEmptyFields() throws Exception {
        final String fileContent = "aa,bb,cc\n"// ok
         + ((("aa,bb,\n"// the last field is empty
         + "aa,,\n") + // the last two fields are empty
        ",,\n") + // all fields are empty
        "aa,bb");
        // row too short
        final FileInputSplit split = createTempFile(fileContent);
        final TupleTypeInfo<Tuple3<String, String, String>> typeInfo = TupleTypeInfo.getBasicTupleTypeInfo(String.class, String.class, String.class);
        final CsvInputFormat<Tuple3<String, String, String>> format = new TupleCsvInputFormat<Tuple3<String, String, String>>(CsvInputFormatTest.PATH, typeInfo);
        format.setFieldDelimiter(",");
        format.configure(new Configuration());
        format.open(split);
        Tuple3<String, String, String> result = new Tuple3<String, String, String>();
        result = format.nextRecord(result);
        Assert.assertNotNull(result);
        Assert.assertEquals("aa", result.f0);
        Assert.assertEquals("bb", result.f1);
        Assert.assertEquals("cc", result.f2);
        result = format.nextRecord(result);
        Assert.assertNotNull(result);
        Assert.assertEquals("aa", result.f0);
        Assert.assertEquals("bb", result.f1);
        Assert.assertEquals("", result.f2);
        result = format.nextRecord(result);
        Assert.assertNotNull(result);
        Assert.assertEquals("aa", result.f0);
        Assert.assertEquals("", result.f1);
        Assert.assertEquals("", result.f2);
        result = format.nextRecord(result);
        Assert.assertNotNull(result);
        Assert.assertEquals("", result.f0);
        Assert.assertEquals("", result.f1);
        Assert.assertEquals("", result.f2);
        try {
            format.nextRecord(result);
            Assert.fail("Parse Exception was not thrown! (Row too short)");
        } catch (ParseException e) {
        }
    }

    @Test
    public void testIntegerFields() throws IOException {
        try {
            final String fileContent = "111|222|333|444|555\n666|777|888|999|000|\n";
            final FileInputSplit split = createTempFile(fileContent);
            final TupleTypeInfo<Tuple5<Integer, Integer, Integer, Integer, Integer>> typeInfo = TupleTypeInfo.getBasicTupleTypeInfo(Integer.class, Integer.class, Integer.class, Integer.class, Integer.class);
            final CsvInputFormat<Tuple5<Integer, Integer, Integer, Integer, Integer>> format = new TupleCsvInputFormat<Tuple5<Integer, Integer, Integer, Integer, Integer>>(CsvInputFormatTest.PATH, typeInfo);
            format.setFieldDelimiter("|");
            format.configure(new Configuration());
            format.open(split);
            Tuple5<Integer, Integer, Integer, Integer, Integer> result = new Tuple5<Integer, Integer, Integer, Integer, Integer>();
            result = format.nextRecord(result);
            Assert.assertNotNull(result);
            Assert.assertEquals(Integer.valueOf(111), result.f0);
            Assert.assertEquals(Integer.valueOf(222), result.f1);
            Assert.assertEquals(Integer.valueOf(333), result.f2);
            Assert.assertEquals(Integer.valueOf(444), result.f3);
            Assert.assertEquals(Integer.valueOf(555), result.f4);
            result = format.nextRecord(result);
            Assert.assertNotNull(result);
            Assert.assertEquals(Integer.valueOf(666), result.f0);
            Assert.assertEquals(Integer.valueOf(777), result.f1);
            Assert.assertEquals(Integer.valueOf(888), result.f2);
            Assert.assertEquals(Integer.valueOf(999), result.f3);
            Assert.assertEquals(Integer.valueOf(0), result.f4);
            result = format.nextRecord(result);
            Assert.assertNull(result);
            Assert.assertTrue(format.reachedEnd());
        } catch (Exception ex) {
            Assert.fail(((("Test failed due to a " + (ex.getClass().getName())) + ": ") + (ex.getMessage())));
        }
    }

    @Test
    public void testEmptyFields() throws IOException {
        try {
            final String fileContent = "|0|0|0|0|0|\n" + (((("1||1|1|1|1|\n" + "2|2||2|2|2|\n") + "3|3|3| |3|3|\n") + "4|4|4|4||4|\n") + "5|5|5|5|5||\n");
            final FileInputSplit split = createTempFile(fileContent);
            final TupleTypeInfo<Tuple6<Short, Integer, Long, Float, Double, Byte>> typeInfo = TupleTypeInfo.getBasicTupleTypeInfo(Short.class, Integer.class, Long.class, Float.class, Double.class, Byte.class);
            final CsvInputFormat<Tuple6<Short, Integer, Long, Float, Double, Byte>> format = new TupleCsvInputFormat<Tuple6<Short, Integer, Long, Float, Double, Byte>>(CsvInputFormatTest.PATH, typeInfo);
            format.setFieldDelimiter("|");
            format.configure(new Configuration());
            format.open(split);
            Tuple6<Short, Integer, Long, Float, Double, Byte> result = new Tuple6<Short, Integer, Long, Float, Double, Byte>();
            try {
                result = format.nextRecord(result);
                Assert.fail("Empty String Parse Exception was not thrown! (ShortParser)");
            } catch (ParseException e) {
            }
            try {
                result = format.nextRecord(result);
                Assert.fail("Empty String Parse Exception was not thrown! (IntegerParser)");
            } catch (ParseException e) {
            }
            try {
                result = format.nextRecord(result);
                Assert.fail("Empty String Parse Exception was not thrown! (LongParser)");
            } catch (ParseException e) {
            }
            try {
                result = format.nextRecord(result);
                Assert.fail("Empty String Parse Exception was not thrown! (FloatParser)");
            } catch (ParseException e) {
            }
            try {
                result = format.nextRecord(result);
                Assert.fail("Empty String Parse Exception was not thrown! (DoubleParser)");
            } catch (ParseException e) {
            }
            try {
                result = format.nextRecord(result);
                Assert.fail("Empty String Parse Exception was not thrown! (ByteParser)");
            } catch (ParseException e) {
            }
            result = format.nextRecord(result);
            Assert.assertNull(result);
            Assert.assertTrue(format.reachedEnd());
        } catch (Exception ex) {
            Assert.fail(((("Test failed due to a " + (ex.getClass().getName())) + ": ") + (ex.getMessage())));
        }
    }

    @Test
    public void testDoubleFields() throws IOException {
        try {
            final String fileContent = "11.1|22.2|33.3|44.4|55.5\n66.6|77.7|88.8|99.9|00.0|\n";
            final FileInputSplit split = createTempFile(fileContent);
            final TupleTypeInfo<Tuple5<Double, Double, Double, Double, Double>> typeInfo = TupleTypeInfo.getBasicTupleTypeInfo(Double.class, Double.class, Double.class, Double.class, Double.class);
            final CsvInputFormat<Tuple5<Double, Double, Double, Double, Double>> format = new TupleCsvInputFormat<Tuple5<Double, Double, Double, Double, Double>>(CsvInputFormatTest.PATH, typeInfo);
            format.setFieldDelimiter("|");
            format.configure(new Configuration());
            format.open(split);
            Tuple5<Double, Double, Double, Double, Double> result = new Tuple5<Double, Double, Double, Double, Double>();
            result = format.nextRecord(result);
            Assert.assertNotNull(result);
            Assert.assertEquals(Double.valueOf(11.1), result.f0);
            Assert.assertEquals(Double.valueOf(22.2), result.f1);
            Assert.assertEquals(Double.valueOf(33.3), result.f2);
            Assert.assertEquals(Double.valueOf(44.4), result.f3);
            Assert.assertEquals(Double.valueOf(55.5), result.f4);
            result = format.nextRecord(result);
            Assert.assertNotNull(result);
            Assert.assertEquals(Double.valueOf(66.6), result.f0);
            Assert.assertEquals(Double.valueOf(77.7), result.f1);
            Assert.assertEquals(Double.valueOf(88.8), result.f2);
            Assert.assertEquals(Double.valueOf(99.9), result.f3);
            Assert.assertEquals(Double.valueOf(0.0), result.f4);
            result = format.nextRecord(result);
            Assert.assertNull(result);
            Assert.assertTrue(format.reachedEnd());
        } catch (Exception ex) {
            Assert.fail(((("Test failed due to a " + (ex.getClass().getName())) + ": ") + (ex.getMessage())));
        }
    }

    @Test
    public void testReadFirstN() throws IOException {
        try {
            final String fileContent = "111|222|333|444|555|\n666|777|888|999|000|\n";
            final FileInputSplit split = createTempFile(fileContent);
            final TupleTypeInfo<Tuple2<Integer, Integer>> typeInfo = TupleTypeInfo.getBasicTupleTypeInfo(Integer.class, Integer.class);
            final CsvInputFormat<Tuple2<Integer, Integer>> format = new TupleCsvInputFormat<Tuple2<Integer, Integer>>(CsvInputFormatTest.PATH, typeInfo);
            format.setFieldDelimiter("|");
            format.configure(new Configuration());
            format.open(split);
            Tuple2<Integer, Integer> result = new Tuple2<Integer, Integer>();
            result = format.nextRecord(result);
            Assert.assertNotNull(result);
            Assert.assertEquals(Integer.valueOf(111), result.f0);
            Assert.assertEquals(Integer.valueOf(222), result.f1);
            result = format.nextRecord(result);
            Assert.assertNotNull(result);
            Assert.assertEquals(Integer.valueOf(666), result.f0);
            Assert.assertEquals(Integer.valueOf(777), result.f1);
            result = format.nextRecord(result);
            Assert.assertNull(result);
            Assert.assertTrue(format.reachedEnd());
        } catch (Exception ex) {
            Assert.fail(((("Test failed due to a " + (ex.getClass().getName())) + ": ") + (ex.getMessage())));
        }
    }

    @Test
    public void testReadSparseWithNullFieldsForTypes() throws IOException {
        try {
            final String fileContent = "111|x|222|x|333|x|444|x|555|x|666|x|777|x|888|x|999|x|000|x|\n" + "000|x|999|x|888|x|777|x|666|x|555|x|444|x|333|x|222|x|111|x|";
            final FileInputSplit split = createTempFile(fileContent);
            final TupleTypeInfo<Tuple3<Integer, Integer, Integer>> typeInfo = TupleTypeInfo.getBasicTupleTypeInfo(Integer.class, Integer.class, Integer.class);
            final CsvInputFormat<Tuple3<Integer, Integer, Integer>> format = new TupleCsvInputFormat<Tuple3<Integer, Integer, Integer>>(CsvInputFormatTest.PATH, typeInfo, new boolean[]{ true, false, false, true, false, false, false, true });
            format.setFieldDelimiter("|x|");
            format.configure(new Configuration());
            format.open(split);
            Tuple3<Integer, Integer, Integer> result = new Tuple3<Integer, Integer, Integer>();
            result = format.nextRecord(result);
            Assert.assertNotNull(result);
            Assert.assertEquals(Integer.valueOf(111), result.f0);
            Assert.assertEquals(Integer.valueOf(444), result.f1);
            Assert.assertEquals(Integer.valueOf(888), result.f2);
            result = format.nextRecord(result);
            Assert.assertNotNull(result);
            Assert.assertEquals(Integer.valueOf(0), result.f0);
            Assert.assertEquals(Integer.valueOf(777), result.f1);
            Assert.assertEquals(Integer.valueOf(333), result.f2);
            result = format.nextRecord(result);
            Assert.assertNull(result);
            Assert.assertTrue(format.reachedEnd());
        } catch (Exception ex) {
            Assert.fail(((("Test failed due to a " + (ex.getClass().getName())) + ": ") + (ex.getMessage())));
        }
    }

    @Test
    public void testReadSparseWithPositionSetter() throws IOException {
        try {
            final String fileContent = "111|222|333|444|555|666|777|888|999|000|\n000|999|888|777|666|555|444|333|222|111|";
            final FileInputSplit split = createTempFile(fileContent);
            final TupleTypeInfo<Tuple3<Integer, Integer, Integer>> typeInfo = TupleTypeInfo.getBasicTupleTypeInfo(Integer.class, Integer.class, Integer.class);
            final CsvInputFormat<Tuple3<Integer, Integer, Integer>> format = new TupleCsvInputFormat<Tuple3<Integer, Integer, Integer>>(CsvInputFormatTest.PATH, typeInfo, new int[]{ 0, 3, 7 });
            format.setFieldDelimiter("|");
            format.configure(new Configuration());
            format.open(split);
            Tuple3<Integer, Integer, Integer> result = new Tuple3<Integer, Integer, Integer>();
            result = format.nextRecord(result);
            Assert.assertNotNull(result);
            Assert.assertEquals(Integer.valueOf(111), result.f0);
            Assert.assertEquals(Integer.valueOf(444), result.f1);
            Assert.assertEquals(Integer.valueOf(888), result.f2);
            result = format.nextRecord(result);
            Assert.assertNotNull(result);
            Assert.assertEquals(Integer.valueOf(0), result.f0);
            Assert.assertEquals(Integer.valueOf(777), result.f1);
            Assert.assertEquals(Integer.valueOf(333), result.f2);
            result = format.nextRecord(result);
            Assert.assertNull(result);
            Assert.assertTrue(format.reachedEnd());
        } catch (Exception ex) {
            Assert.fail(((("Test failed due to a " + (ex.getClass().getName())) + ": ") + (ex.getMessage())));
        }
    }

    @Test
    public void testReadSparseWithMask() throws IOException {
        try {
            final String fileContent = "111&&222&&333&&444&&555&&666&&777&&888&&999&&000&&\n" + "000&&999&&888&&777&&666&&555&&444&&333&&222&&111&&";
            final FileInputSplit split = createTempFile(fileContent);
            final TupleTypeInfo<Tuple3<Integer, Integer, Integer>> typeInfo = TupleTypeInfo.getBasicTupleTypeInfo(Integer.class, Integer.class, Integer.class);
            final CsvInputFormat<Tuple3<Integer, Integer, Integer>> format = new TupleCsvInputFormat<Tuple3<Integer, Integer, Integer>>(CsvInputFormatTest.PATH, typeInfo, new boolean[]{ true, false, false, true, false, false, false, true });
            format.setFieldDelimiter("&&");
            format.configure(new Configuration());
            format.open(split);
            Tuple3<Integer, Integer, Integer> result = new Tuple3<Integer, Integer, Integer>();
            result = format.nextRecord(result);
            Assert.assertNotNull(result);
            Assert.assertEquals(Integer.valueOf(111), result.f0);
            Assert.assertEquals(Integer.valueOf(444), result.f1);
            Assert.assertEquals(Integer.valueOf(888), result.f2);
            result = format.nextRecord(result);
            Assert.assertNotNull(result);
            Assert.assertEquals(Integer.valueOf(0), result.f0);
            Assert.assertEquals(Integer.valueOf(777), result.f1);
            Assert.assertEquals(Integer.valueOf(333), result.f2);
            result = format.nextRecord(result);
            Assert.assertNull(result);
            Assert.assertTrue(format.reachedEnd());
        } catch (Exception ex) {
            Assert.fail(((("Test failed due to a " + (ex.getClass().getName())) + ": ") + (ex.getMessage())));
        }
    }

    @Test
    public void testParseStringErrors() throws Exception {
        StringParser stringParser = new StringParser();
        stringParser.enableQuotedStringParsing(((byte) ('\"')));
        Object[][] failures = new Object[][]{ new Object[]{ "\"string\" trailing", ParseErrorState.UNQUOTED_CHARS_AFTER_QUOTED_STRING }, new Object[]{ "\"unterminated ", ParseErrorState.UNTERMINATED_QUOTED_STRING } };
        for (Object[] failure : failures) {
            String input = ((String) (failure[0]));
            int result = stringParser.parseField(input.getBytes(DEFAULT_CHARSET), 0, input.length(), new byte[]{ '|' }, null);
            Assert.assertThat(result, CoreMatchers.is((-1)));
            Assert.assertThat(stringParser.getErrorState(), CoreMatchers.is(failure[1]));
        }
    }

    @Test
    public void testWindowsLineEndRemoval() {
        // Check typical use case -- linux file is correct and it is set up to linux (\n)
        this.testRemovingTrailingCR("\n", "\n");
        // Check typical windows case -- windows file endings and file has windows file endings set up
        this.testRemovingTrailingCR("\r\n", "\r\n");
        // Check problematic case windows file -- windows file endings (\r\n) but linux line endings (\n) set up
        this.testRemovingTrailingCR("\r\n", "\n");
        // Check problematic case linux file -- linux file endings (\n) but windows file endings set up (\r\n)
        // Specific setup for windows line endings will expect \r\n because it has to be set up and is not standard.
    }

    @Test
    public void testPojoType() throws Exception {
        File tempFile = File.createTempFile("CsvReaderPojoType", "tmp");
        tempFile.deleteOnExit();
        tempFile.setWritable(true);
        OutputStreamWriter wrt = new OutputStreamWriter(new FileOutputStream(tempFile));
        wrt.write("123,AAA,3.123,BBB\n");
        wrt.write("456,BBB,1.123,AAA\n");
        wrt.close();
        @SuppressWarnings("unchecked")
        PojoTypeInfo<CsvInputFormatTest.PojoItem> typeInfo = ((PojoTypeInfo<CsvInputFormatTest.PojoItem>) (TypeExtractor.createTypeInfo(CsvInputFormatTest.PojoItem.class)));
        CsvInputFormat<CsvInputFormatTest.PojoItem> inputFormat = new PojoCsvInputFormat<CsvInputFormatTest.PojoItem>(new Path(tempFile.toURI().toString()), typeInfo);
        inputFormat.configure(new Configuration());
        FileInputSplit[] splits = inputFormat.createInputSplits(1);
        inputFormat.open(splits[0]);
        validatePojoItem(inputFormat);
    }

    @Test
    public void testPojoTypeWithPrivateField() throws Exception {
        File tempFile = File.createTempFile("CsvReaderPojoType", "tmp");
        tempFile.deleteOnExit();
        tempFile.setWritable(true);
        OutputStreamWriter wrt = new OutputStreamWriter(new FileOutputStream(tempFile));
        wrt.write("123,AAA,3.123,BBB\n");
        wrt.write("456,BBB,1.123,AAA\n");
        wrt.close();
        @SuppressWarnings("unchecked")
        PojoTypeInfo<CsvInputFormatTest.PrivatePojoItem> typeInfo = ((PojoTypeInfo<CsvInputFormatTest.PrivatePojoItem>) (TypeExtractor.createTypeInfo(CsvInputFormatTest.PrivatePojoItem.class)));
        CsvInputFormat<CsvInputFormatTest.PrivatePojoItem> inputFormat = new PojoCsvInputFormat<CsvInputFormatTest.PrivatePojoItem>(new Path(tempFile.toURI().toString()), typeInfo);
        inputFormat.configure(new Configuration());
        FileInputSplit[] splits = inputFormat.createInputSplits(1);
        inputFormat.open(splits[0]);
        CsvInputFormatTest.PrivatePojoItem item = new CsvInputFormatTest.PrivatePojoItem();
        inputFormat.nextRecord(item);
        Assert.assertEquals(123, item.field1);
        Assert.assertEquals("AAA", item.field2);
        Assert.assertEquals(Double.valueOf(3.123), item.field3);
        Assert.assertEquals("BBB", item.field4);
        inputFormat.nextRecord(item);
        Assert.assertEquals(456, item.field1);
        Assert.assertEquals("BBB", item.field2);
        Assert.assertEquals(Double.valueOf(1.123), item.field3);
        Assert.assertEquals("AAA", item.field4);
    }

    @Test
    public void testPojoTypeWithTrailingEmptyFields() throws Exception {
        final String fileContent = "123,,3.123,,\n456,BBB,3.23,,";
        final FileInputSplit split = createTempFile(fileContent);
        @SuppressWarnings("unchecked")
        PojoTypeInfo<CsvInputFormatTest.PrivatePojoItem> typeInfo = ((PojoTypeInfo<CsvInputFormatTest.PrivatePojoItem>) (TypeExtractor.createTypeInfo(CsvInputFormatTest.PrivatePojoItem.class)));
        CsvInputFormat<CsvInputFormatTest.PrivatePojoItem> inputFormat = new PojoCsvInputFormat<CsvInputFormatTest.PrivatePojoItem>(CsvInputFormatTest.PATH, typeInfo);
        inputFormat.configure(new Configuration());
        inputFormat.open(split);
        CsvInputFormatTest.PrivatePojoItem item = new CsvInputFormatTest.PrivatePojoItem();
        inputFormat.nextRecord(item);
        Assert.assertEquals(123, item.field1);
        Assert.assertEquals("", item.field2);
        Assert.assertEquals(Double.valueOf(3.123), item.field3);
        Assert.assertEquals("", item.field4);
        inputFormat.nextRecord(item);
        Assert.assertEquals(456, item.field1);
        Assert.assertEquals("BBB", item.field2);
        Assert.assertEquals(Double.valueOf(3.23), item.field3);
        Assert.assertEquals("", item.field4);
    }

    @Test
    public void testPojoTypeWithMappingInformation() throws Exception {
        File tempFile = File.createTempFile("CsvReaderPojoType", "tmp");
        tempFile.deleteOnExit();
        tempFile.setWritable(true);
        OutputStreamWriter wrt = new OutputStreamWriter(new FileOutputStream(tempFile));
        wrt.write("123,3.123,AAA,BBB\n");
        wrt.write("456,1.123,BBB,AAA\n");
        wrt.close();
        @SuppressWarnings("unchecked")
        PojoTypeInfo<CsvInputFormatTest.PojoItem> typeInfo = ((PojoTypeInfo<CsvInputFormatTest.PojoItem>) (TypeExtractor.createTypeInfo(CsvInputFormatTest.PojoItem.class)));
        CsvInputFormat<CsvInputFormatTest.PojoItem> inputFormat = new PojoCsvInputFormat<CsvInputFormatTest.PojoItem>(new Path(tempFile.toURI().toString()), typeInfo, new String[]{ "field1", "field3", "field2", "field4" });
        inputFormat.configure(new Configuration());
        FileInputSplit[] splits = inputFormat.createInputSplits(1);
        inputFormat.open(splits[0]);
        validatePojoItem(inputFormat);
    }

    @Test
    public void testPojoTypeWithPartialFieldInCSV() throws Exception {
        File tempFile = File.createTempFile("CsvReaderPojoType", "tmp");
        tempFile.deleteOnExit();
        tempFile.setWritable(true);
        OutputStreamWriter wrt = new OutputStreamWriter(new FileOutputStream(tempFile));
        wrt.write("123,NODATA,AAA,NODATA,3.123,BBB\n");
        wrt.write("456,NODATA,BBB,NODATA,1.123,AAA\n");
        wrt.close();
        @SuppressWarnings("unchecked")
        PojoTypeInfo<CsvInputFormatTest.PojoItem> typeInfo = ((PojoTypeInfo<CsvInputFormatTest.PojoItem>) (TypeExtractor.createTypeInfo(CsvInputFormatTest.PojoItem.class)));
        CsvInputFormat<CsvInputFormatTest.PojoItem> inputFormat = new PojoCsvInputFormat<CsvInputFormatTest.PojoItem>(new Path(tempFile.toURI().toString()), typeInfo, new boolean[]{ true, false, true, false, true, true });
        inputFormat.configure(new Configuration());
        FileInputSplit[] splits = inputFormat.createInputSplits(1);
        inputFormat.open(splits[0]);
        validatePojoItem(inputFormat);
    }

    @Test
    public void testPojoTypeWithMappingInfoAndPartialField() throws Exception {
        File tempFile = File.createTempFile("CsvReaderPojoType", "tmp");
        tempFile.deleteOnExit();
        tempFile.setWritable(true);
        OutputStreamWriter wrt = new OutputStreamWriter(new FileOutputStream(tempFile));
        wrt.write("123,3.123,AAA,BBB\n");
        wrt.write("456,1.123,BBB,AAA\n");
        wrt.close();
        @SuppressWarnings("unchecked")
        PojoTypeInfo<CsvInputFormatTest.PojoItem> typeInfo = ((PojoTypeInfo<CsvInputFormatTest.PojoItem>) (TypeExtractor.createTypeInfo(CsvInputFormatTest.PojoItem.class)));
        CsvInputFormat<CsvInputFormatTest.PojoItem> inputFormat = new PojoCsvInputFormat<CsvInputFormatTest.PojoItem>(new Path(tempFile.toURI().toString()), typeInfo, new String[]{ "field1", "field4" }, new boolean[]{ true, false, false, true });
        inputFormat.configure(new Configuration());
        FileInputSplit[] splits = inputFormat.createInputSplits(1);
        inputFormat.open(splits[0]);
        CsvInputFormatTest.PojoItem item = new CsvInputFormatTest.PojoItem();
        inputFormat.nextRecord(item);
        Assert.assertEquals(123, item.field1);
        Assert.assertEquals("BBB", item.field4);
    }

    @Test
    public void testPojoTypeWithInvalidFieldMapping() throws Exception {
        File tempFile = File.createTempFile("CsvReaderPojoType", "tmp");
        tempFile.deleteOnExit();
        tempFile.setWritable(true);
        @SuppressWarnings("unchecked")
        PojoTypeInfo<CsvInputFormatTest.PojoItem> typeInfo = ((PojoTypeInfo<CsvInputFormatTest.PojoItem>) (TypeExtractor.createTypeInfo(CsvInputFormatTest.PojoItem.class)));
        try {
            new PojoCsvInputFormat<CsvInputFormatTest.PojoItem>(new Path(tempFile.toURI().toString()), typeInfo, new String[]{ "field1", "field2" });
            Assert.fail("The number of POJO fields cannot be same as that of selected CSV fields");
        } catch (IllegalArgumentException e) {
            // success
        }
        try {
            new PojoCsvInputFormat<CsvInputFormatTest.PojoItem>(new Path(tempFile.toURI().toString()), typeInfo, new String[]{ "field1", "field2", null, "field4" });
            Assert.fail("Fields mapping cannot contain null.");
        } catch (NullPointerException e) {
            // success
        }
        try {
            new PojoCsvInputFormat<CsvInputFormatTest.PojoItem>(new Path(tempFile.toURI().toString()), typeInfo, new String[]{ "field1", "field2", "field3", "field5" });
            Assert.fail("Invalid field name");
        } catch (IllegalArgumentException e) {
            // success
        }
    }

    @Test
    public void testQuotedStringParsingWithIncludeFields() throws Exception {
        final String fileContent = "\"20:41:52-1-3-2015\"|\"Re: Taskmanager memory error in Eclipse\"|" + "\"Blahblah <blah@blahblah.org>\"|\"blaaa|\"blubb\"";
        final File tempFile = File.createTempFile("CsvReaderQuotedString", "tmp");
        tempFile.deleteOnExit();
        tempFile.setWritable(true);
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(tempFile));
        writer.write(fileContent);
        writer.close();
        TupleTypeInfo<Tuple2<String, String>> typeInfo = TupleTypeInfo.getBasicTupleTypeInfo(String.class, String.class);
        CsvInputFormat<Tuple2<String, String>> inputFormat = new TupleCsvInputFormat<Tuple2<String, String>>(new Path(tempFile.toURI().toString()), typeInfo, new boolean[]{ true, false, true });
        inputFormat.enableQuotedStringParsing('"');
        inputFormat.setFieldDelimiter("|");
        inputFormat.setDelimiter('\n');
        inputFormat.configure(new Configuration());
        FileInputSplit[] splits = inputFormat.createInputSplits(1);
        inputFormat.open(splits[0]);
        Tuple2<String, String> record = inputFormat.nextRecord(new Tuple2<String, String>());
        Assert.assertEquals("20:41:52-1-3-2015", record.f0);
        Assert.assertEquals("Blahblah <blah@blahblah.org>", record.f1);
    }

    @Test
    public void testQuotedStringParsingWithEscapedQuotes() throws Exception {
        final String fileContent = "\"\\\"Hello\\\" World\"|\"We are\\\" young\"";
        final File tempFile = File.createTempFile("CsvReaderQuotedString", "tmp");
        tempFile.deleteOnExit();
        tempFile.setWritable(true);
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(tempFile));
        writer.write(fileContent);
        writer.close();
        TupleTypeInfo<Tuple2<String, String>> typeInfo = TupleTypeInfo.getBasicTupleTypeInfo(String.class, String.class);
        CsvInputFormat<Tuple2<String, String>> inputFormat = new TupleCsvInputFormat(new Path(tempFile.toURI().toString()), typeInfo);
        inputFormat.enableQuotedStringParsing('"');
        inputFormat.setFieldDelimiter("|");
        inputFormat.setDelimiter('\n');
        inputFormat.configure(new Configuration());
        FileInputSplit[] splits = inputFormat.createInputSplits(1);
        inputFormat.open(splits[0]);
        Tuple2<String, String> record = inputFormat.nextRecord(new Tuple2<String, String>());
        Assert.assertEquals("\\\"Hello\\\" World", record.f0);
        Assert.assertEquals("We are\\\" young", record.f1);
    }

    /**
     * Tests that the CSV input format can deal with POJOs which are subclasses.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testPojoSubclassType() throws Exception {
        final String fileContent = "t1,foobar,tweet2\nt2,barfoo,tweet2";
        final File tempFile = File.createTempFile("CsvReaderPOJOSubclass", "tmp");
        tempFile.deleteOnExit();
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(tempFile));
        writer.write(fileContent);
        writer.close();
        @SuppressWarnings("unchecked")
        PojoTypeInfo<CsvInputFormatTest.TwitterPOJO> typeInfo = ((PojoTypeInfo<CsvInputFormatTest.TwitterPOJO>) (TypeExtractor.createTypeInfo(CsvInputFormatTest.TwitterPOJO.class)));
        CsvInputFormat<CsvInputFormatTest.TwitterPOJO> inputFormat = new PojoCsvInputFormat(new Path(tempFile.toURI().toString()), typeInfo);
        inputFormat.configure(new Configuration());
        FileInputSplit[] splits = inputFormat.createInputSplits(1);
        inputFormat.open(splits[0]);
        List<CsvInputFormatTest.TwitterPOJO> expected = new ArrayList<>();
        for (String line : fileContent.split("\n")) {
            String[] elements = line.split(",");
            expected.add(new CsvInputFormatTest.TwitterPOJO(elements[0], elements[1], elements[2]));
        }
        List<CsvInputFormatTest.TwitterPOJO> actual = new ArrayList<>();
        CsvInputFormatTest.TwitterPOJO pojo;
        while ((pojo = inputFormat.nextRecord(new CsvInputFormatTest.TwitterPOJO())) != null) {
            actual.add(pojo);
        } 
        Assert.assertEquals(expected, actual);
    }

    // --------------------------------------------------------------------------------------------
    // Custom types for testing
    // --------------------------------------------------------------------------------------------
    /**
     * Sample test pojo.
     */
    public static class PojoItem {
        public int field1;

        public String field2;

        public Double field3;

        public String field4;
    }

    /**
     * Sample test pojo with private fields.
     */
    public static class PrivatePojoItem {
        private int field1;

        private String field2;

        private Double field3;

        private String field4;

        public int getField1() {
            return field1;
        }

        public void setField1(int field1) {
            this.field1 = field1;
        }

        public String getField2() {
            return field2;
        }

        public void setField2(String field2) {
            this.field2 = field2;
        }

        public Double getField3() {
            return field3;
        }

        public void setField3(Double field3) {
            this.field3 = field3;
        }

        public String getField4() {
            return field4;
        }

        public void setField4(String field4) {
            this.field4 = field4;
        }
    }

    /**
     * Sample test pojo.
     */
    public static class POJO {
        public String table;

        public String time;

        public POJO() {
            this("", "");
        }

        public POJO(String table, String time) {
            this.table = table;
            this.time = time;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof CsvInputFormatTest.POJO) {
                CsvInputFormatTest.POJO other = ((CsvInputFormatTest.POJO) (obj));
                return (table.equals(other.table)) && (time.equals(other.time));
            } else {
                return false;
            }
        }
    }

    /**
     * Sample test pojo representing tweets.
     */
    public static class TwitterPOJO extends CsvInputFormatTest.POJO {
        public String tweet;

        public TwitterPOJO() {
            this("", "", "");
        }

        public TwitterPOJO(String table, String time, String tweet) {
            super(table, time);
            this.tweet = tweet;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof CsvInputFormatTest.TwitterPOJO) {
                CsvInputFormatTest.TwitterPOJO other = ((CsvInputFormatTest.TwitterPOJO) (obj));
                return (super.equals(other)) && (tweet.equals(other.tweet));
            } else {
                return false;
            }
        }
    }
}

