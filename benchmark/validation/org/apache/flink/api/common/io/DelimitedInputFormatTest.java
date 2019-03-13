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


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.io.FileInputFormat.FileBaseStatistics;
import org.apache.flink.api.common.io.statistics.BaseStatistics;
import org.apache.flink.configuration.ConfigConstants;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.fs.FileInputSplit;
import org.apache.flink.core.fs.Path;
import org.junit.Assert;
import org.junit.Test;


public class DelimitedInputFormatTest {
    private DelimitedInputFormat<String> format;

    // --------------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------------
    @Test
    public void testConfigure() {
        Configuration cfg = new Configuration();
        cfg.setString("delimited-format.delimiter", "\n");
        format.configure(cfg);
        Assert.assertEquals("\n", new String(format.getDelimiter(), format.getCharset()));
        cfg.setString("delimited-format.delimiter", "&-&");
        format.configure(cfg);
        Assert.assertEquals("&-&", new String(format.getDelimiter(), format.getCharset()));
    }

    @Test
    public void testSerialization() throws Exception {
        final byte[] DELIMITER = new byte[]{ 1, 2, 3, 4 };
        final int NUM_LINE_SAMPLES = 7;
        final int LINE_LENGTH_LIMIT = 12345;
        final int BUFFER_SIZE = 178;
        DelimitedInputFormat<String> format = new DelimitedInputFormatTest.MyTextInputFormat();
        format.setDelimiter(DELIMITER);
        format.setNumLineSamples(NUM_LINE_SAMPLES);
        format.setLineLengthLimit(LINE_LENGTH_LIMIT);
        format.setBufferSize(BUFFER_SIZE);
        ByteArrayOutputStream baos = new ByteArrayOutputStream(4096);
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(format);
        oos.flush();
        oos.close();
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        @SuppressWarnings("unchecked")
        DelimitedInputFormat<String> deserialized = ((DelimitedInputFormat<String>) (ois.readObject()));
        Assert.assertEquals(NUM_LINE_SAMPLES, deserialized.getNumLineSamples());
        Assert.assertEquals(LINE_LENGTH_LIMIT, deserialized.getLineLengthLimit());
        Assert.assertEquals(BUFFER_SIZE, deserialized.getBufferSize());
        Assert.assertArrayEquals(DELIMITER, deserialized.getDelimiter());
    }

    @Test
    public void testOpen() throws IOException {
        final String myString = "my mocked line 1\nmy mocked line 2\n";
        final FileInputSplit split = DelimitedInputFormatTest.createTempFile(myString);
        int bufferSize = 5;
        format.setBufferSize(bufferSize);
        format.open(split);
        Assert.assertEquals(0, format.splitStart);
        Assert.assertEquals(((myString.length()) - bufferSize), format.splitLength);
        Assert.assertEquals(bufferSize, format.getBufferSize());
    }

    @Test
    public void testReadWithoutTrailingDelimiter() throws IOException {
        // 2. test case
        final String myString = "my key|my val$$$my key2\n$$ctd.$$|my value2";
        final FileInputSplit split = DelimitedInputFormatTest.createTempFile(myString);
        final Configuration parameters = new Configuration();
        // default delimiter = '\n'
        format.configure(parameters);
        format.open(split);
        String first = format.nextRecord(null);
        String second = format.nextRecord(null);
        Assert.assertNotNull(first);
        Assert.assertNotNull(second);
        Assert.assertEquals("my key|my val$$$my key2", first);
        Assert.assertEquals("$$ctd.$$|my value2", second);
        Assert.assertNull(format.nextRecord(null));
        Assert.assertTrue(format.reachedEnd());
    }

    @Test
    public void testReadWithTrailingDelimiter() throws IOException {
        // 2. test case
        final String myString = "my key|my val$$$my key2\n$$ctd.$$|my value2\n";
        final FileInputSplit split = DelimitedInputFormatTest.createTempFile(myString);
        final Configuration parameters = new Configuration();
        // default delimiter = '\n'
        format.configure(parameters);
        format.open(split);
        String first = format.nextRecord(null);
        String second = format.nextRecord(null);
        Assert.assertNotNull(first);
        Assert.assertNotNull(second);
        Assert.assertEquals("my key|my val$$$my key2", first);
        Assert.assertEquals("$$ctd.$$|my value2", second);
        Assert.assertNull(format.nextRecord(null));
        Assert.assertTrue(format.reachedEnd());
    }

    @Test
    public void testReadCustomDelimiter() throws IOException {
        final String myString = "my key|my val$$$my key2\n$$ctd.$$|my value2";
        final FileInputSplit split = DelimitedInputFormatTest.createTempFile(myString);
        final Configuration parameters = new Configuration();
        format.setDelimiter("$$$");
        format.configure(parameters);
        format.open(split);
        String first = format.nextRecord(null);
        Assert.assertNotNull(first);
        Assert.assertEquals("my key|my val", first);
        String second = format.nextRecord(null);
        Assert.assertNotNull(second);
        Assert.assertEquals("my key2\n$$ctd.$$|my value2", second);
        Assert.assertNull(format.nextRecord(null));
        Assert.assertTrue(format.reachedEnd());
    }

    @Test
    public void testMultiCharDelimiter() throws IOException {
        final String myString = "www112xx1123yyy11123zzzzz1123";
        final FileInputSplit split = DelimitedInputFormatTest.createTempFile(myString);
        final Configuration parameters = new Configuration();
        format.setDelimiter("1123");
        format.configure(parameters);
        format.open(split);
        String first = format.nextRecord(null);
        Assert.assertNotNull(first);
        Assert.assertEquals("www112xx", first);
        String second = format.nextRecord(null);
        Assert.assertNotNull(second);
        Assert.assertEquals("yyy1", second);
        String third = format.nextRecord(null);
        Assert.assertNotNull(third);
        Assert.assertEquals("zzzzz", third);
        Assert.assertNull(format.nextRecord(null));
        Assert.assertTrue(format.reachedEnd());
    }

    @Test
    public void testReadCustomDelimiterWithCharset() throws IOException {
        // Unicode row fragments
        String[] records = new String[]{ "\u020e\u021f\u05c0\u020b\u020f", "Apache", "\nFlink", "\u0000", "\u05c0" };
        // Unicode delimiter
        String delimiter = "\u05c0\u05c0";
        String fileContent = StringUtils.join(records, delimiter);
        for (final String charset : new String[]{ "UTF-8", "UTF-16BE", "UTF-16LE" }) {
            // use charset when instantiating the record String
            DelimitedInputFormat<String> format = new DelimitedInputFormat<String>() {
                @Override
                public String readRecord(String reuse, byte[] bytes, int offset, int numBytes) throws IOException {
                    return new String(bytes, offset, numBytes, charset);
                }
            };
            format.setFilePath("file:///some/file/that/will/not/be/read");
            final FileInputSplit split = DelimitedInputFormatTest.createTempFile(fileContent, charset);
            format.setDelimiter(delimiter);
            // use the same encoding to parse the file as used to read the file;
            // the delimiter is reinterpreted when the charset is set
            format.setCharset(charset);
            format.configure(new Configuration());
            format.open(split);
            for (String record : records) {
                String value = format.nextRecord(null);
                Assert.assertEquals(record, value);
            }
            Assert.assertNull(format.nextRecord(null));
            Assert.assertTrue(format.reachedEnd());
        }
    }

    /**
     * Tests that the records are read correctly when the split boundary is in the middle of a record.
     */
    @Test
    public void testReadOverSplitBoundariesUnaligned() throws IOException {
        final String myString = "value1\nvalue2\nvalue3";
        final FileInputSplit split = DelimitedInputFormatTest.createTempFile(myString);
        FileInputSplit split1 = new FileInputSplit(0, split.getPath(), 0, ((split.getLength()) / 2), split.getHostnames());
        FileInputSplit split2 = new FileInputSplit(1, split.getPath(), split1.getLength(), split.getLength(), split.getHostnames());
        final Configuration parameters = new Configuration();
        format.configure(parameters);
        format.open(split1);
        Assert.assertEquals("value1", format.nextRecord(null));
        Assert.assertEquals("value2", format.nextRecord(null));
        Assert.assertNull(format.nextRecord(null));
        Assert.assertTrue(format.reachedEnd());
        format.close();
        format.open(split2);
        Assert.assertEquals("value3", format.nextRecord(null));
        Assert.assertNull(format.nextRecord(null));
        Assert.assertTrue(format.reachedEnd());
        format.close();
    }

    /**
     * Tests that the correct number of records is read when the split boundary is exact at the record boundary.
     */
    @Test
    public void testReadWithBufferSizeIsMultiple() throws IOException {
        final String myString = "aaaaaaa\nbbbbbbb\nccccccc\nddddddd\n";
        final FileInputSplit split = DelimitedInputFormatTest.createTempFile(myString);
        FileInputSplit split1 = new FileInputSplit(0, split.getPath(), 0, ((split.getLength()) / 2), split.getHostnames());
        FileInputSplit split2 = new FileInputSplit(1, split.getPath(), split1.getLength(), split.getLength(), split.getHostnames());
        final Configuration parameters = new Configuration();
        format.setBufferSize((2 * ((int) (split1.getLength()))));
        format.configure(parameters);
        String next;
        int count = 0;
        // read split 1
        format.open(split1);
        while ((next = format.nextRecord(null)) != null) {
            Assert.assertEquals(7, next.length());
            count++;
        } 
        Assert.assertNull(format.nextRecord(null));
        Assert.assertTrue(format.reachedEnd());
        format.close();
        // this one must have read one too many, because the next split will skipp the trailing remainder
        // which happens to be one full record
        Assert.assertEquals(3, count);
        // read split 2
        format.open(split2);
        while ((next = format.nextRecord(null)) != null) {
            Assert.assertEquals(7, next.length());
            count++;
        } 
        format.close();
        Assert.assertEquals(4, count);
    }

    @Test
    public void testReadExactlyBufferSize() throws IOException {
        final String myString = "aaaaaaa\nbbbbbbb\nccccccc\nddddddd\n";
        final FileInputSplit split = DelimitedInputFormatTest.createTempFile(myString);
        final Configuration parameters = new Configuration();
        format.setBufferSize(((int) (split.getLength())));
        format.configure(parameters);
        format.open(split);
        String next;
        int count = 0;
        while ((next = format.nextRecord(null)) != null) {
            Assert.assertEquals(7, next.length());
            count++;
        } 
        Assert.assertNull(format.nextRecord(null));
        Assert.assertTrue(format.reachedEnd());
        format.close();
        Assert.assertEquals(4, count);
    }

    @Test
    public void testReadRecordsLargerThanBuffer() throws IOException {
        final String myString = "aaaaaaaaaaaaaaaaaaaaa\n" + (("bbbbbbbbbbbbbbbbbbbbbbbbb\n" + "ccccccccccccccccccc\n") + "ddddddddddddddddddddddddddddddddddd\n");
        final FileInputSplit split = DelimitedInputFormatTest.createTempFile(myString);
        FileInputSplit split1 = new FileInputSplit(0, split.getPath(), 0, ((split.getLength()) / 2), split.getHostnames());
        FileInputSplit split2 = new FileInputSplit(1, split.getPath(), split1.getLength(), split.getLength(), split.getHostnames());
        final Configuration parameters = new Configuration();
        format.setBufferSize(8);
        format.configure(parameters);
        String next;
        List<String> result = new ArrayList<String>();
        format.open(split1);
        while ((next = format.nextRecord(null)) != null) {
            result.add(next);
        } 
        Assert.assertNull(format.nextRecord(null));
        Assert.assertTrue(format.reachedEnd());
        format.close();
        format.open(split2);
        while ((next = format.nextRecord(null)) != null) {
            result.add(next);
        } 
        Assert.assertNull(format.nextRecord(null));
        Assert.assertTrue(format.reachedEnd());
        format.close();
        Assert.assertEquals(4, result.size());
        Assert.assertEquals(Arrays.asList(myString.split("\n")), result);
    }

    @Test
    public void testDelimiterOnBufferBoundary() throws IOException {
        String[] records = new String[]{ "1234567890<DEL?NO!>1234567890", "1234567890<DEL?NO!>1234567890", "<DEL?NO!>" };
        String delimiter = "<DELIM>";
        String fileContent = StringUtils.join(records, delimiter);
        final FileInputSplit split = DelimitedInputFormatTest.createTempFile(fileContent);
        final Configuration parameters = new Configuration();
        format.setBufferSize(12);
        format.setDelimiter(delimiter);
        format.configure(parameters);
        format.open(split);
        for (String record : records) {
            String value = format.nextRecord(null);
            Assert.assertEquals(record, value);
        }
        Assert.assertNull(format.nextRecord(null));
        Assert.assertTrue(format.reachedEnd());
        format.close();
    }

    // -- Statistics --//
    @Test
    public void testGetStatistics() throws IOException {
        final String myString = "my mocked line 1\nmy mocked line 2\n";
        final long size = myString.length();
        final Path filePath = DelimitedInputFormatTest.createTempFilePath(myString);
        final String myString2 = "my mocked line 1\nmy mocked line 2\nanother mocked line3\n";
        final long size2 = myString2.length();
        final Path filePath2 = DelimitedInputFormatTest.createTempFilePath(myString2);
        final long totalSize = size + size2;
        DelimitedInputFormat<String> format = new DelimitedInputFormatTest.MyTextInputFormat();
        format.setFilePaths(filePath.toUri().toString(), filePath2.toUri().toString());
        FileInputFormat.FileBaseStatistics stats = format.getStatistics(null);
        Assert.assertNotNull(stats);
        Assert.assertEquals("The file size from the statistics is wrong.", totalSize, stats.getTotalInputSize());
    }

    @Test
    public void testGetStatisticsFileDoesNotExist() throws IOException {
        DelimitedInputFormat<String> format = new DelimitedInputFormatTest.MyTextInputFormat();
        format.setFilePaths("file:///path/does/not/really/exist", "file:///another/path/that/does/not/exist");
        FileBaseStatistics stats = format.getStatistics(null);
        Assert.assertNull("The file statistics should be null.", stats);
    }

    @Test
    public void testGetStatisticsSingleFileWithCachedVersion() throws IOException {
        final String myString = "my mocked line 1\nmy mocked line 2\n";
        final Path tempFile = DelimitedInputFormatTest.createTempFilePath(myString);
        final long size = myString.length();
        final long fakeSize = 10065;
        DelimitedInputFormat<String> format = new DelimitedInputFormatTest.MyTextInputFormat();
        format.setFilePath(tempFile);
        format.configure(new Configuration());
        FileBaseStatistics stats = format.getStatistics(null);
        Assert.assertNotNull(stats);
        Assert.assertEquals("The file size from the statistics is wrong.", size, stats.getTotalInputSize());
        format = new DelimitedInputFormatTest.MyTextInputFormat();
        format.setFilePath(tempFile);
        format.configure(new Configuration());
        FileBaseStatistics newStats = format.getStatistics(stats);
        Assert.assertEquals("Statistics object was changed.", newStats, stats);
        // insert fake stats with the correct modification time. the call should return the fake stats
        format = new DelimitedInputFormatTest.MyTextInputFormat();
        format.setFilePath(tempFile);
        format.configure(new Configuration());
        FileBaseStatistics fakeStats = new FileBaseStatistics(stats.getLastModificationTime(), fakeSize, BaseStatistics.AVG_RECORD_BYTES_UNKNOWN);
        BaseStatistics latest = format.getStatistics(fakeStats);
        Assert.assertEquals("The file size from the statistics is wrong.", fakeSize, latest.getTotalInputSize());
        // insert fake stats with the expired modification time. the call should return new accurate stats
        format = new DelimitedInputFormatTest.MyTextInputFormat();
        format.setFilePath(tempFile);
        format.configure(new Configuration());
        FileBaseStatistics outDatedFakeStats = new FileBaseStatistics(((stats.getLastModificationTime()) - 1), fakeSize, BaseStatistics.AVG_RECORD_BYTES_UNKNOWN);
        BaseStatistics reGathered = format.getStatistics(outDatedFakeStats);
        Assert.assertEquals("The file size from the statistics is wrong.", size, reGathered.getTotalInputSize());
    }

    protected static final class MyTextInputFormat extends DelimitedInputFormat<String> {
        private static final long serialVersionUID = 1L;

        @Override
        public String readRecord(String reuse, byte[] bytes, int offset, int numBytes) {
            return new String(bytes, offset, numBytes, ConfigConstants.DEFAULT_CHARSET);
        }

        @Override
        public boolean supportsMultiPaths() {
            return true;
        }
    }
}

