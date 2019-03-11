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


import OptimizerOptions.DELIMITED_FORMAT_MAX_SAMPLE_LEN;
import java.io.File;
import org.apache.flink.api.common.io.statistics.BaseStatistics;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.testutils.TestFileSystem;
import org.apache.flink.testutils.TestFileUtils;
import org.apache.flink.types.IntValue;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class DelimitedInputFormatSamplingTest {
    private static final String TEST_DATA1 = "123456789\n" + (((((((("123456789\n" + "123456789\n") + "123456789\n") + "123456789\n") + "123456789\n") + "123456789\n") + "123456789\n") + "123456789\n") + "123456789\n");

    private static final String TEST_DATA2 = "12345\n" + (((((((("12345\n" + "12345\n") + "12345\n") + "12345\n") + "12345\n") + "12345\n") + "12345\n") + "12345\n") + "12345\n");

    private static final int TEST_DATA_1_LINES = DelimitedInputFormatSamplingTest.TEST_DATA1.split("\n").length;

    private static final int TEST_DATA_1_LINEWIDTH = DelimitedInputFormatSamplingTest.TEST_DATA1.split("\n")[0].length();

    private static final int TEST_DATA_2_LINEWIDTH = DelimitedInputFormatSamplingTest.TEST_DATA2.split("\n")[0].length();

    private static final int TOTAL_SIZE = (DelimitedInputFormatSamplingTest.TEST_DATA1.length()) + (DelimitedInputFormatSamplingTest.TEST_DATA2.length());

    private static final int DEFAULT_NUM_SAMPLES = 4;

    private static Configuration CONFIG;

    @ClassRule
    public static TemporaryFolder tempFolder = new TemporaryFolder();

    private static File testTempFolder;

    // ========================================================================
    // Tests
    // ========================================================================
    @Test
    public void testNumSamplesOneFile() {
        try {
            final String tempFile = TestFileUtils.createTempFile(DelimitedInputFormatSamplingTest.TEST_DATA1);
            final Configuration conf = new Configuration();
            final DelimitedInputFormatSamplingTest.TestDelimitedInputFormat format = new DelimitedInputFormatSamplingTest.TestDelimitedInputFormat(DelimitedInputFormatSamplingTest.CONFIG);
            setFilePath(tempFile.replace("file", "test"));
            format.configure(conf);
            TestFileSystem.resetStreamOpenCounter();
            getStatistics(null);
            Assert.assertEquals("Wrong number of samples taken.", DelimitedInputFormatSamplingTest.DEFAULT_NUM_SAMPLES, TestFileSystem.getNumtimeStreamOpened());
            DelimitedInputFormatSamplingTest.TestDelimitedInputFormat format2 = new DelimitedInputFormatSamplingTest.TestDelimitedInputFormat(DelimitedInputFormatSamplingTest.CONFIG);
            setFilePath(tempFile.replace("file", "test"));
            setNumLineSamples(8);
            format2.configure(conf);
            TestFileSystem.resetStreamOpenCounter();
            getStatistics(null);
            Assert.assertEquals("Wrong number of samples taken.", 8, TestFileSystem.getNumtimeStreamOpened());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testNumSamplesMultipleFiles() {
        try {
            final String tempFile = TestFileUtils.createTempFileDir(DelimitedInputFormatSamplingTest.testTempFolder, DelimitedInputFormatSamplingTest.TEST_DATA1, DelimitedInputFormatSamplingTest.TEST_DATA1, DelimitedInputFormatSamplingTest.TEST_DATA1, DelimitedInputFormatSamplingTest.TEST_DATA1);
            final Configuration conf = new Configuration();
            final DelimitedInputFormatSamplingTest.TestDelimitedInputFormat format = new DelimitedInputFormatSamplingTest.TestDelimitedInputFormat(DelimitedInputFormatSamplingTest.CONFIG);
            setFilePath(tempFile.replace("file", "test"));
            format.configure(conf);
            TestFileSystem.resetStreamOpenCounter();
            getStatistics(null);
            Assert.assertEquals("Wrong number of samples taken.", DelimitedInputFormatSamplingTest.DEFAULT_NUM_SAMPLES, TestFileSystem.getNumtimeStreamOpened());
            DelimitedInputFormatSamplingTest.TestDelimitedInputFormat format2 = new DelimitedInputFormatSamplingTest.TestDelimitedInputFormat(DelimitedInputFormatSamplingTest.CONFIG);
            setFilePath(tempFile.replace("file", "test"));
            setNumLineSamples(8);
            format2.configure(conf);
            TestFileSystem.resetStreamOpenCounter();
            getStatistics(null);
            Assert.assertEquals("Wrong number of samples taken.", 8, TestFileSystem.getNumtimeStreamOpened());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testSamplingOneFile() {
        try {
            final String tempFile = TestFileUtils.createTempFile(DelimitedInputFormatSamplingTest.TEST_DATA1);
            final Configuration conf = new Configuration();
            final DelimitedInputFormatSamplingTest.TestDelimitedInputFormat format = new DelimitedInputFormatSamplingTest.TestDelimitedInputFormat(DelimitedInputFormatSamplingTest.CONFIG);
            setFilePath(tempFile);
            format.configure(conf);
            BaseStatistics stats = format.getStatistics(null);
            final int numLines = DelimitedInputFormatSamplingTest.TEST_DATA_1_LINES;
            final float avgWidth = ((float) (DelimitedInputFormatSamplingTest.TEST_DATA1.length())) / (DelimitedInputFormatSamplingTest.TEST_DATA_1_LINES);
            Assert.assertTrue("Wrong record count.", (((stats.getNumberOfRecords()) < (numLines + 1)) & ((stats.getNumberOfRecords()) > (numLines - 1))));
            Assert.assertTrue("Wrong avg record size.", (((stats.getAverageRecordWidth()) < (avgWidth + 1)) & ((stats.getAverageRecordWidth()) > (avgWidth - 1))));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testSamplingDirectory() {
        try {
            final String tempFile = TestFileUtils.createTempFileDir(DelimitedInputFormatSamplingTest.testTempFolder, DelimitedInputFormatSamplingTest.TEST_DATA1, DelimitedInputFormatSamplingTest.TEST_DATA2);
            final Configuration conf = new Configuration();
            final DelimitedInputFormatSamplingTest.TestDelimitedInputFormat format = new DelimitedInputFormatSamplingTest.TestDelimitedInputFormat(DelimitedInputFormatSamplingTest.CONFIG);
            setFilePath(tempFile);
            format.configure(conf);
            BaseStatistics stats = format.getStatistics(null);
            final int maxNumLines = ((int) (Math.ceil(((DelimitedInputFormatSamplingTest.TOTAL_SIZE) / ((double) (Math.min(DelimitedInputFormatSamplingTest.TEST_DATA_1_LINEWIDTH, DelimitedInputFormatSamplingTest.TEST_DATA_2_LINEWIDTH)))))));
            final int minNumLines = ((int) ((DelimitedInputFormatSamplingTest.TOTAL_SIZE) / ((double) (Math.max(DelimitedInputFormatSamplingTest.TEST_DATA_1_LINEWIDTH, DelimitedInputFormatSamplingTest.TEST_DATA_2_LINEWIDTH)))));
            final float maxAvgWidth = ((float) (DelimitedInputFormatSamplingTest.TOTAL_SIZE)) / minNumLines;
            final float minAvgWidth = ((float) (DelimitedInputFormatSamplingTest.TOTAL_SIZE)) / maxNumLines;
            if (!(((stats.getNumberOfRecords()) <= maxNumLines) & ((stats.getNumberOfRecords()) >= minNumLines))) {
                System.err.println((((((("Records: " + (stats.getNumberOfRecords())) + " out of (") + minNumLines) + ", ") + maxNumLines) + ")."));
                Assert.fail("Wrong record count.");
            }
            if (!(((stats.getAverageRecordWidth()) <= maxAvgWidth) & ((stats.getAverageRecordWidth()) >= minAvgWidth))) {
                Assert.fail("Wrong avg record size.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testDifferentDelimiter() {
        try {
            final String DELIMITER = "12345678-";
            String testData = DelimitedInputFormatSamplingTest.TEST_DATA1.replace("\n", DELIMITER);
            final String tempFile = TestFileUtils.createTempFile(testData);
            final Configuration conf = new Configuration();
            final DelimitedInputFormatSamplingTest.TestDelimitedInputFormat format = new DelimitedInputFormatSamplingTest.TestDelimitedInputFormat(DelimitedInputFormatSamplingTest.CONFIG);
            setFilePath(tempFile);
            setDelimiter(DELIMITER);
            format.configure(conf);
            BaseStatistics stats = format.getStatistics(null);
            final int numLines = DelimitedInputFormatSamplingTest.TEST_DATA_1_LINES;
            final float avgWidth = ((float) (testData.length())) / (DelimitedInputFormatSamplingTest.TEST_DATA_1_LINES);
            Assert.assertTrue("Wrong record count.", (((stats.getNumberOfRecords()) < (numLines + 1)) & ((stats.getNumberOfRecords()) > (numLines - 1))));
            Assert.assertTrue("Wrong avg record size.", (((stats.getAverageRecordWidth()) < (avgWidth + 1)) & ((stats.getAverageRecordWidth()) > (avgWidth - 1))));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testSamplingOverlyLongRecord() {
        try {
            final String tempFile = TestFileUtils.createTempFile((2 * (DELIMITED_FORMAT_MAX_SAMPLE_LEN.defaultValue())));
            final Configuration conf = new Configuration();
            final DelimitedInputFormatSamplingTest.TestDelimitedInputFormat format = new DelimitedInputFormatSamplingTest.TestDelimitedInputFormat(DelimitedInputFormatSamplingTest.CONFIG);
            setFilePath(tempFile);
            format.configure(conf);
            Assert.assertNull("Expected exception due to overly long record.", getStatistics(null));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testCachedStatistics() {
        try {
            final String tempFile = TestFileUtils.createTempFile(DelimitedInputFormatSamplingTest.TEST_DATA1);
            final Configuration conf = new Configuration();
            final DelimitedInputFormatSamplingTest.TestDelimitedInputFormat format = new DelimitedInputFormatSamplingTest.TestDelimitedInputFormat(DelimitedInputFormatSamplingTest.CONFIG);
            setFilePath(("test://" + tempFile));
            format.configure(conf);
            TestFileSystem.resetStreamOpenCounter();
            BaseStatistics stats = format.getStatistics(null);
            Assert.assertEquals("Wrong number of samples taken.", DelimitedInputFormatSamplingTest.DEFAULT_NUM_SAMPLES, TestFileSystem.getNumtimeStreamOpened());
            final DelimitedInputFormatSamplingTest.TestDelimitedInputFormat format2 = new DelimitedInputFormatSamplingTest.TestDelimitedInputFormat(DelimitedInputFormatSamplingTest.CONFIG);
            setFilePath(("test://" + tempFile));
            format2.configure(conf);
            TestFileSystem.resetStreamOpenCounter();
            BaseStatistics stats2 = format2.getStatistics(stats);
            Assert.assertTrue("Using cached statistics should cicumvent sampling.", (0 == (TestFileSystem.getNumtimeStreamOpened())));
            Assert.assertTrue("Using cached statistics should cicumvent sampling.", (stats == stats2));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    // ========================================================================
    // Mocks
    // ========================================================================
    private static final class TestDelimitedInputFormat extends DelimitedInputFormat<IntValue> {
        private static final long serialVersionUID = 1L;

        TestDelimitedInputFormat(Configuration configuration) {
            super(null, configuration);
        }

        @Override
        public IntValue readRecord(IntValue reuse, byte[] bytes, int offset, int numBytes) {
            throw new UnsupportedOperationException();
        }
    }
}

