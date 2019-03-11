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
package org.apache.hadoop.mapreduce.lib.input;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.compress.BZip2Codec;
import org.apache.hadoop.io.compress.CodecPool;
import org.apache.hadoop.io.compress.Decompressor;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.TaskAttemptID;
import org.apache.hadoop.mapreduce.lib.input.LineRecordReader.MAX_LINE_LENGTH;
import org.junit.Assert;
import org.junit.Test;


public class TestLineRecordReader {
    private static Path workDir = new Path(new Path(System.getProperty("test.build.data", "target"), "data"), "TestTextInputFormat");

    private static Path inputDir = new Path(TestLineRecordReader.workDir, "input");

    @Test
    public void testBzip2SplitEndsAtCR() throws IOException {
        // the test data contains a carriage-return at the end of the first
        // split which ends at compressed offset 136498 and the next
        // character is not a linefeed
        testSplitRecords("blockEndingInCR.txt.bz2", 136498);
    }

    @Test
    public void testBzip2SplitEndsAtCRThenLF() throws IOException {
        // the test data contains a carriage-return at the end of the first
        // split which ends at compressed offset 136498 and the next
        // character is a linefeed
        testSplitRecords("blockEndingInCRThenLF.txt.bz2", 136498);
    }

    @Test(expected = IOException.class)
    public void testSafeguardSplittingUnsplittableFiles() throws IOException {
        // The LineRecordReader must fail when trying to read a file that
        // was compressed using an unsplittable file format
        testSplitRecords("TestSafeguardSplittingUnsplittableFiles.txt.gz", 2);
    }

    @Test
    public void testRecordSpanningMultipleSplits() throws IOException {
        checkRecordSpanningMultipleSplits("recordSpanningMultipleSplits.txt", 10, false);
    }

    @Test
    public void testRecordSpanningMultipleSplitsCompressed() throws IOException {
        // The file is generated with bz2 block size of 100k. The split size
        // needs to be larger than that for the CompressedSplitLineReader to
        // work.
        checkRecordSpanningMultipleSplits("recordSpanningMultipleSplits.txt.bz2", (200 * 1000), true);
    }

    @Test
    public void testStripBOM() throws IOException {
        // the test data contains a BOM at the start of the file
        // confirm the BOM is skipped by LineRecordReader
        String UTF8_BOM = "\ufeff";
        URL testFileUrl = getClass().getClassLoader().getResource("testBOM.txt");
        Assert.assertNotNull("Cannot find testBOM.txt", testFileUrl);
        File testFile = new File(testFileUrl.getFile());
        Path testFilePath = new Path(testFile.getAbsolutePath());
        long testFileSize = testFile.length();
        Configuration conf = new Configuration();
        conf.setInt(MAX_LINE_LENGTH, Integer.MAX_VALUE);
        TaskAttemptContext context = new org.apache.hadoop.mapreduce.task.TaskAttemptContextImpl(conf, new TaskAttemptID());
        // read the data and check whether BOM is skipped
        FileSplit split = new FileSplit(testFilePath, 0, testFileSize, ((String[]) (null)));
        LineRecordReader reader = new LineRecordReader();
        reader.initialize(split, context);
        int numRecords = 0;
        boolean firstLine = true;
        boolean skipBOM = true;
        while (reader.nextKeyValue()) {
            if (firstLine) {
                firstLine = false;
                if (reader.getCurrentValue().toString().startsWith(UTF8_BOM)) {
                    skipBOM = false;
                }
            }
            ++numRecords;
        } 
        reader.close();
        Assert.assertTrue("BOM is not skipped", skipBOM);
    }

    @Test
    public void testMultipleClose() throws IOException {
        URL testFileUrl = getClass().getClassLoader().getResource("recordSpanningMultipleSplits.txt.bz2");
        Assert.assertNotNull("Cannot find recordSpanningMultipleSplits.txt.bz2", testFileUrl);
        File testFile = new File(testFileUrl.getFile());
        Path testFilePath = new Path(testFile.getAbsolutePath());
        long testFileSize = testFile.length();
        Configuration conf = new Configuration();
        conf.setInt(MAX_LINE_LENGTH, Integer.MAX_VALUE);
        TaskAttemptContext context = new org.apache.hadoop.mapreduce.task.TaskAttemptContextImpl(conf, new TaskAttemptID());
        // read the data and check whether BOM is skipped
        FileSplit split = new FileSplit(testFilePath, 0, testFileSize, null);
        LineRecordReader reader = new LineRecordReader();
        reader.initialize(split, context);
        // noinspection StatementWithEmptyBody
        while (reader.nextKeyValue());
        reader.close();
        reader.close();
        BZip2Codec codec = new BZip2Codec();
        codec.setConf(conf);
        Set<Decompressor> decompressors = new HashSet<Decompressor>();
        for (int i = 0; i < 10; ++i) {
            decompressors.add(CodecPool.getDecompressor(codec));
        }
        Assert.assertEquals(10, decompressors.size());
    }

    @Test
    public void testUncompressedInput() throws Exception {
        Configuration conf = new Configuration();
        // single char delimiter, best case
        String inputData = "abc+def+ghi+jkl+mno+pqr+stu+vw +xyz";
        Path inputFile = createInputFile(conf, inputData);
        conf.set("textinputformat.record.delimiter", "+");
        for (int bufferSize = 1; bufferSize <= (inputData.length()); bufferSize++) {
            for (int splitSize = 1; splitSize < (inputData.length()); splitSize++) {
                conf.setInt("io.file.buffer.size", bufferSize);
                testSplitRecordsForFile(conf, splitSize, inputData.length(), inputFile);
            }
        }
        // multi char delimiter, best case
        inputData = "abc|+|def|+|ghi|+|jkl|+|mno|+|pqr|+|stu|+|vw |+|xyz";
        inputFile = createInputFile(conf, inputData);
        conf.set("textinputformat.record.delimiter", "|+|");
        for (int bufferSize = 1; bufferSize <= (inputData.length()); bufferSize++) {
            for (int splitSize = 1; splitSize < (inputData.length()); splitSize++) {
                conf.setInt("io.file.buffer.size", bufferSize);
                testSplitRecordsForFile(conf, splitSize, inputData.length(), inputFile);
            }
        }
        // single char delimiter with empty records
        inputData = "abc+def++ghi+jkl++mno+pqr++stu+vw ++xyz";
        inputFile = createInputFile(conf, inputData);
        conf.set("textinputformat.record.delimiter", "+");
        for (int bufferSize = 1; bufferSize <= (inputData.length()); bufferSize++) {
            for (int splitSize = 1; splitSize < (inputData.length()); splitSize++) {
                conf.setInt("io.file.buffer.size", bufferSize);
                testSplitRecordsForFile(conf, splitSize, inputData.length(), inputFile);
            }
        }
        // multi char delimiter with empty records
        inputData = "abc|+||+|defghi|+|jkl|+||+|mno|+|pqr|+||+|stu|+|vw |+||+|xyz";
        inputFile = createInputFile(conf, inputData);
        conf.set("textinputformat.record.delimiter", "|+|");
        for (int bufferSize = 1; bufferSize <= (inputData.length()); bufferSize++) {
            for (int splitSize = 1; splitSize < (inputData.length()); splitSize++) {
                conf.setInt("io.file.buffer.size", bufferSize);
                testSplitRecordsForFile(conf, splitSize, inputData.length(), inputFile);
            }
        }
        // multi char delimiter with starting part of the delimiter in the data
        inputData = "abc+def+-ghi+jkl+-mno+pqr+-stu+vw +-xyz";
        inputFile = createInputFile(conf, inputData);
        conf.set("textinputformat.record.delimiter", "+-");
        for (int bufferSize = 1; bufferSize <= (inputData.length()); bufferSize++) {
            for (int splitSize = 1; splitSize < (inputData.length()); splitSize++) {
                conf.setInt("io.file.buffer.size", bufferSize);
                testSplitRecordsForFile(conf, splitSize, inputData.length(), inputFile);
            }
        }
        // multi char delimiter with newline as start of the delimiter
        inputData = "abc\n+def\n+ghi\n+jkl\n+mno";
        inputFile = createInputFile(conf, inputData);
        conf.set("textinputformat.record.delimiter", "\n+");
        for (int bufferSize = 1; bufferSize <= (inputData.length()); bufferSize++) {
            for (int splitSize = 1; splitSize < (inputData.length()); splitSize++) {
                conf.setInt("io.file.buffer.size", bufferSize);
                testSplitRecordsForFile(conf, splitSize, inputData.length(), inputFile);
            }
        }
        // multi char delimiter with newline in delimiter and in data
        inputData = "abc\ndef+\nghi+\njkl\nmno";
        inputFile = createInputFile(conf, inputData);
        conf.set("textinputformat.record.delimiter", "+\n");
        for (int bufferSize = 1; bufferSize <= (inputData.length()); bufferSize++) {
            for (int splitSize = 1; splitSize < (inputData.length()); splitSize++) {
                conf.setInt("io.file.buffer.size", bufferSize);
                testSplitRecordsForFile(conf, splitSize, inputData.length(), inputFile);
            }
        }
    }

    @Test
    public void testUncompressedInputContainingCRLF() throws Exception {
        Configuration conf = new Configuration();
        String inputData = "a\r\nb\rc\nd\r\n";
        Path inputFile = createInputFile(conf, inputData);
        for (int bufferSize = 1; bufferSize <= (inputData.length()); bufferSize++) {
            for (int splitSize = 1; splitSize < (inputData.length()); splitSize++) {
                conf.setInt("io.file.buffer.size", bufferSize);
                testSplitRecordsForFile(conf, splitSize, inputData.length(), inputFile);
            }
        }
    }

    @Test
    public void testUncompressedInputCustomDelimiterPosValue() throws Exception {
        Configuration conf = new Configuration();
        conf.setInt("io.file.buffer.size", 10);
        conf.setInt(MAX_LINE_LENGTH, Integer.MAX_VALUE);
        String inputData = "abcdefghij++kl++mno";
        Path inputFile = createInputFile(conf, inputData);
        String delimiter = "++";
        byte[] recordDelimiterBytes = delimiter.getBytes(StandardCharsets.UTF_8);
        int splitLength = 15;
        FileSplit split = new FileSplit(inputFile, 0, splitLength, ((String[]) (null)));
        TaskAttemptContext context = new org.apache.hadoop.mapreduce.task.TaskAttemptContextImpl(conf, new TaskAttemptID());
        LineRecordReader reader = new LineRecordReader(recordDelimiterBytes);
        reader.initialize(split, context);
        // Get first record: "abcdefghij"
        Assert.assertTrue("Expected record got nothing", reader.nextKeyValue());
        LongWritable key = reader.getCurrentKey();
        Text value = reader.getCurrentValue();
        Assert.assertEquals("Wrong length for record value", 10, value.getLength());
        Assert.assertEquals("Wrong position after record read", 0, key.get());
        // Get second record: "kl"
        Assert.assertTrue("Expected record got nothing", reader.nextKeyValue());
        Assert.assertEquals("Wrong length for record value", 2, value.getLength());
        // Key should be 12 right after "abcdefghij++"
        Assert.assertEquals("Wrong position after record read", 12, key.get());
        // Get third record: "mno"
        Assert.assertTrue("Expected record got nothing", reader.nextKeyValue());
        Assert.assertEquals("Wrong length for record value", 3, value.getLength());
        // Key should be 16 right after "abcdefghij++kl++"
        Assert.assertEquals("Wrong position after record read", 16, key.get());
        Assert.assertFalse(reader.nextKeyValue());
        // Key should be 19 right after "abcdefghij++kl++mno"
        Assert.assertEquals("Wrong position after record read", 19, key.get());
        // after refresh should be empty
        key = reader.getCurrentKey();
        Assert.assertNull("Unexpected key returned", key);
        reader.close();
        split = new FileSplit(inputFile, splitLength, ((inputData.length()) - splitLength), ((String[]) (null)));
        reader = new LineRecordReader(recordDelimiterBytes);
        reader.initialize(split, context);
        // No record is in the second split because the second split dropped
        // the first record, which was already reported by the first split.
        Assert.assertFalse("Unexpected record returned", reader.nextKeyValue());
        key = reader.getCurrentKey();
        Assert.assertNull("Unexpected key returned", key);
        reader.close();
        // multi char delimiter with starting part of the delimiter in the data
        inputData = "abcd+efgh++ijk++mno";
        inputFile = createInputFile(conf, inputData);
        splitLength = 5;
        split = new FileSplit(inputFile, 0, splitLength, ((String[]) (null)));
        reader = new LineRecordReader(recordDelimiterBytes);
        reader.initialize(split, context);
        // Get first record: "abcd+efgh"
        Assert.assertTrue("Expected record got nothing", reader.nextKeyValue());
        key = reader.getCurrentKey();
        value = reader.getCurrentValue();
        Assert.assertEquals("Wrong position after record read", 0, key.get());
        Assert.assertEquals("Wrong length for record value", 9, value.getLength());
        // should have jumped over the delimiter, no record
        Assert.assertFalse(reader.nextKeyValue());
        Assert.assertEquals("Wrong position after record read", 11, key.get());
        // after refresh should be empty
        key = reader.getCurrentKey();
        Assert.assertNull("Unexpected key returned", key);
        reader.close();
        // next split: check for duplicate or dropped records
        split = new FileSplit(inputFile, splitLength, ((inputData.length()) - splitLength), ((String[]) (null)));
        reader = new LineRecordReader(recordDelimiterBytes);
        reader.initialize(split, context);
        Assert.assertTrue("Expected record got nothing", reader.nextKeyValue());
        key = reader.getCurrentKey();
        value = reader.getCurrentValue();
        // Get second record: "ijk" first in this split
        Assert.assertEquals("Wrong position after record read", 11, key.get());
        Assert.assertEquals("Wrong length for record value", 3, value.getLength());
        // Get third record: "mno" second in this split
        Assert.assertTrue("Expected record got nothing", reader.nextKeyValue());
        Assert.assertEquals("Wrong position after record read", 16, key.get());
        Assert.assertEquals("Wrong length for record value", 3, value.getLength());
        // should be at the end of the input
        Assert.assertFalse(reader.nextKeyValue());
        Assert.assertEquals("Wrong position after record read", 19, key.get());
        reader.close();
        inputData = "abcd|efgh|+|ij|kl|+|mno|pqr";
        inputFile = createInputFile(conf, inputData);
        delimiter = "|+|";
        recordDelimiterBytes = delimiter.getBytes(StandardCharsets.UTF_8);
        // walking over the buffer and split sizes checks for proper processing
        // of the ambiguous bytes of the delimiter
        for (int bufferSize = 1; bufferSize <= (inputData.length()); bufferSize++) {
            for (int splitSize = 1; splitSize < (inputData.length()); splitSize++) {
                // track where we are in the inputdata
                int keyPosition = 0;
                conf.setInt("io.file.buffer.size", bufferSize);
                split = new FileSplit(inputFile, 0, bufferSize, ((String[]) (null)));
                reader = new LineRecordReader(recordDelimiterBytes);
                reader.initialize(split, context);
                // Get the first record: "abcd|efgh" always possible
                Assert.assertTrue("Expected record got nothing", reader.nextKeyValue());
                key = reader.getCurrentKey();
                value = reader.getCurrentValue();
                Assert.assertTrue("abcd|efgh".equals(value.toString()));
                // Position should be 0 right at the start
                Assert.assertEquals("Wrong position after record read", keyPosition, key.get());
                // Position should be 12 right after the first "|+|"
                keyPosition = 12;
                // get the next record: "ij|kl" if the split/buffer allows it
                if (reader.nextKeyValue()) {
                    // check the record info: "ij|kl"
                    Assert.assertTrue("ij|kl".equals(value.toString()));
                    Assert.assertEquals("Wrong position after record read", keyPosition, key.get());
                    // Position should be 20 after the second "|+|"
                    keyPosition = 20;
                }
                // get the third record: "mno|pqr" if the split/buffer allows it
                if (reader.nextKeyValue()) {
                    // check the record info: "mno|pqr"
                    Assert.assertTrue("mno|pqr".equals(value.toString()));
                    Assert.assertEquals("Wrong position after record read", keyPosition, key.get());
                    // Position should be the end of the input
                    keyPosition = inputData.length();
                }
                Assert.assertFalse("Unexpected record returned", reader.nextKeyValue());
                // no more records can be read we should be at the last position
                Assert.assertEquals("Wrong position after record read", keyPosition, key.get());
                // after refresh should be empty
                key = reader.getCurrentKey();
                Assert.assertNull("Unexpected key returned", key);
                reader.close();
            }
        }
    }

    @Test
    public void testUncompressedInputDefaultDelimiterPosValue() throws Exception {
        Configuration conf = new Configuration();
        String inputData = "1234567890\r\n12\r\n345";
        Path inputFile = createInputFile(conf, inputData);
        conf.setInt("io.file.buffer.size", 10);
        conf.setInt(MAX_LINE_LENGTH, Integer.MAX_VALUE);
        FileSplit split = new FileSplit(inputFile, 0, 15, ((String[]) (null)));
        TaskAttemptContext context = new org.apache.hadoop.mapreduce.task.TaskAttemptContextImpl(conf, new TaskAttemptID());
        LineRecordReader reader = new LineRecordReader(null);
        reader.initialize(split, context);
        LongWritable key;
        Text value;
        reader.nextKeyValue();
        key = reader.getCurrentKey();
        value = reader.getCurrentValue();
        // Get first record:"1234567890"
        Assert.assertEquals(10, value.getLength());
        Assert.assertEquals(0, key.get());
        reader.nextKeyValue();
        // Get second record:"12"
        Assert.assertEquals(2, value.getLength());
        // Key should be 12 right after "1234567890\r\n"
        Assert.assertEquals(12, key.get());
        Assert.assertFalse(reader.nextKeyValue());
        // Key should be 16 right after "1234567890\r\n12\r\n"
        Assert.assertEquals(16, key.get());
        split = new FileSplit(inputFile, 15, 4, ((String[]) (null)));
        reader = new LineRecordReader(null);
        reader.initialize(split, context);
        // The second split dropped the first record "\n"
        reader.nextKeyValue();
        key = reader.getCurrentKey();
        value = reader.getCurrentValue();
        // Get third record:"345"
        Assert.assertEquals(3, value.getLength());
        // Key should be 16 right after "1234567890\r\n12\r\n"
        Assert.assertEquals(16, key.get());
        Assert.assertFalse(reader.nextKeyValue());
        // Key should be 19 right after "1234567890\r\n12\r\n345"
        Assert.assertEquals(19, key.get());
        inputData = "123456789\r\r\n";
        inputFile = createInputFile(conf, inputData);
        split = new FileSplit(inputFile, 0, 12, ((String[]) (null)));
        reader = new LineRecordReader(null);
        reader.initialize(split, context);
        reader.nextKeyValue();
        key = reader.getCurrentKey();
        value = reader.getCurrentValue();
        // Get first record:"123456789"
        Assert.assertEquals(9, value.getLength());
        Assert.assertEquals(0, key.get());
        reader.nextKeyValue();
        // Get second record:""
        Assert.assertEquals(0, value.getLength());
        // Key should be 10 right after "123456789\r"
        Assert.assertEquals(10, key.get());
        Assert.assertFalse(reader.nextKeyValue());
        // Key should be 12 right after "123456789\r\r\n"
        Assert.assertEquals(12, key.get());
    }

    @Test
    public void testBzipWithMultibyteDelimiter() throws IOException {
        String testFileName = "compressedMultibyteDelimiter.txt.bz2";
        // firstSplitLength < (headers + blockMarker) will pass always since no
        // records will be read (in the test file that is byte 0..9)
        // firstSplitlength > (compressed file length - one compressed block
        // size + 1) will also always pass since the second split will be empty
        // (833 bytes is the last block start in the used data file)
        int firstSplitLength = 100;
        URL testFileUrl = getClass().getClassLoader().getResource(testFileName);
        Assert.assertNotNull(("Cannot find " + testFileName), testFileUrl);
        File testFile = new File(testFileUrl.getFile());
        long testFileSize = testFile.length();
        Path testFilePath = new Path(testFile.getAbsolutePath());
        Assert.assertTrue("Split size is smaller than header length", (firstSplitLength > 9));
        Assert.assertTrue(("Split size is larger than compressed file size " + testFilePath), (testFileSize > firstSplitLength));
        Configuration conf = new Configuration();
        conf.setInt(MAX_LINE_LENGTH, Integer.MAX_VALUE);
        String delimiter = "<E-LINE>\r\r\n";
        conf.set("textinputformat.record.delimiter", delimiter);
        testSplitRecordsForFile(conf, firstSplitLength, testFileSize, testFilePath);
    }
}

