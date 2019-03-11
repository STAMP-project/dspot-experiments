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
package org.apache.hadoop.fs.azure.metrics;


import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.azure.AbstractWasbTestBase;
import org.apache.hadoop.fs.azure.AzureException;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.metrics2.MetricsRecordBuilder;
import org.apache.hadoop.metrics2.MetricsTag;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Instrumentation test, changing state of time and verifying metrics are
 * consistent.
 */
public class ITestAzureFileSystemInstrumentation extends AbstractWasbTestBase {
    protected static final Logger LOG = LoggerFactory.getLogger(ITestAzureFileSystemInstrumentation.class);

    @Test
    public void testMetricTags() throws Exception {
        String accountName = getTestAccount().getRealAccount().getBlobEndpoint().getAuthority();
        String containerName = getTestAccount().getRealContainer().getName();
        MetricsRecordBuilder myMetrics = getMyMetrics();
        Mockito.verify(myMetrics).add(ArgumentMatchers.argThat(new ITestAzureFileSystemInstrumentation.TagMatcher("accountName", accountName)));
        Mockito.verify(myMetrics).add(ArgumentMatchers.argThat(new ITestAzureFileSystemInstrumentation.TagMatcher("containerName", containerName)));
        Mockito.verify(myMetrics).add(ArgumentMatchers.argThat(new ITestAzureFileSystemInstrumentation.TagMatcher("Context", "azureFileSystem")));
        Mockito.verify(myMetrics).add(ArgumentMatchers.argThat(new ITestAzureFileSystemInstrumentation.TagExistsMatcher("wasbFileSystemId")));
    }

    @Test
    public void testMetricsOnMkdirList() throws Exception {
        long base = getBaseWebResponses();
        // Create a directory
        Assert.assertTrue(fs.mkdirs(new Path("a")));
        // At the time of writing
        // getAncestor uses 2 calls for each folder level /user/<name>/a
        // plus 1 call made by checkContainer
        // mkdir checks the hierarchy with 2 calls per level
        // mkdirs calls storeEmptyDir to create the empty folder, which makes 5 calls
        // For a total of 7 + 6 + 5 = 18 web responses
        base = assertWebResponsesInRange(base, 1, 18);
        Assert.assertEquals(1, AzureMetricsTestUtil.getLongCounterValue(getInstrumentation(), AzureFileSystemInstrumentation.WASB_DIRECTORIES_CREATED));
        // List the root contents
        Assert.assertEquals(1, getFileSystem().listStatus(new Path("/")).length);
        base = assertWebResponsesEquals(base, 1);
        assertNoErrors();
    }

    @Test
    public void testMetricsOnFileCreateRead() throws Exception {
        long base = getBaseWebResponses();
        Assert.assertEquals(0, AzureMetricsTestUtil.getCurrentBytesWritten(getInstrumentation()));
        Path filePath = new Path("/metricsTest_webResponses");
        final int FILE_SIZE = 1000;
        // Suppress auto-update of bandwidth metrics so we get
        // to update them exactly when we want to.
        getBandwidthGaugeUpdater().suppressAutoUpdate();
        // Create a file
        Date start = new Date();
        OutputStream outputStream = getFileSystem().create(filePath);
        outputStream.write(ITestAzureFileSystemInstrumentation.nonZeroByteArray(FILE_SIZE));
        outputStream.close();
        long uploadDurationMs = (new Date().getTime()) - (start.getTime());
        // The exact number of requests/responses that happen to create a file
        // can vary  - at the time of writing this code it takes 10
        // requests/responses for the 1000 byte file (33 for 100 MB),
        // plus the initial container-check request but that
        // can very easily change in the future. Just assert that we do roughly
        // more than 2 but less than 15.
        logOpResponseCount("Creating a 1K file", base);
        base = assertWebResponsesInRange(base, 2, 15);
        getBandwidthGaugeUpdater().triggerUpdate(true);
        long bytesWritten = AzureMetricsTestUtil.getCurrentBytesWritten(getInstrumentation());
        Assert.assertTrue((((("The bytes written in the last second " + bytesWritten) + " is pretty far from the expected range of around ") + FILE_SIZE) + " bytes plus a little overhead."), ((bytesWritten > (FILE_SIZE / 2)) && (bytesWritten < (FILE_SIZE * 2))));
        long totalBytesWritten = AzureMetricsTestUtil.getCurrentTotalBytesWritten(getInstrumentation());
        Assert.assertTrue((((("The total bytes written  " + totalBytesWritten) + " is pretty far from the expected range of around ") + FILE_SIZE) + " bytes plus a little overhead."), ((totalBytesWritten >= FILE_SIZE) && (totalBytesWritten < (FILE_SIZE * 2))));
        long uploadRate = AzureMetricsTestUtil.getLongGaugeValue(getInstrumentation(), AzureFileSystemInstrumentation.WASB_UPLOAD_RATE);
        ITestAzureFileSystemInstrumentation.LOG.info((("Upload rate: " + uploadRate) + " bytes/second."));
        long expectedRate = (FILE_SIZE * 1000L) / uploadDurationMs;
        Assert.assertTrue((((((("The upload rate " + uploadRate) + " is below the expected range of around ") + expectedRate) + " bytes/second that the unit test observed. This should never be") + " the case since the test underestimates the rate by looking at ") + " end-to-end time instead of just block upload time."), (uploadRate >= expectedRate));
        long uploadLatency = AzureMetricsTestUtil.getLongGaugeValue(getInstrumentation(), AzureFileSystemInstrumentation.WASB_UPLOAD_LATENCY);
        ITestAzureFileSystemInstrumentation.LOG.info("Upload latency: {}", uploadLatency);
        long expectedLatency = uploadDurationMs;// We're uploading less than a block.

        Assert.assertTrue((("The upload latency " + uploadLatency) + " should be greater than zero now that I've just uploaded a file."), (uploadLatency > 0));
        Assert.assertTrue((((((("The upload latency " + uploadLatency) + " is more than the expected range of around ") + expectedLatency) + " milliseconds that the unit test observed. This should never be") + " the case since the test overestimates the latency by looking at ") + " end-to-end time instead of just block upload time."), (uploadLatency <= expectedLatency));
        // Read the file
        start = new Date();
        InputStream inputStream = getFileSystem().open(filePath);
        int count = 0;
        while ((inputStream.read()) >= 0) {
            count++;
        } 
        inputStream.close();
        long downloadDurationMs = (new Date().getTime()) - (start.getTime());
        Assert.assertEquals(FILE_SIZE, count);
        // Again, exact number varies. At the time of writing this code
        // it takes 4 request/responses, so just assert a rough range between
        // 1 and 10.
        logOpResponseCount("Reading a 1K file", base);
        base = assertWebResponsesInRange(base, 1, 10);
        getBandwidthGaugeUpdater().triggerUpdate(false);
        long totalBytesRead = AzureMetricsTestUtil.getCurrentTotalBytesRead(getInstrumentation());
        Assert.assertEquals(FILE_SIZE, totalBytesRead);
        long bytesRead = AzureMetricsTestUtil.getCurrentBytesRead(getInstrumentation());
        Assert.assertTrue((((("The bytes read in the last second " + bytesRead) + " is pretty far from the expected range of around ") + FILE_SIZE) + " bytes plus a little overhead."), ((bytesRead > (FILE_SIZE / 2)) && (bytesRead < (FILE_SIZE * 2))));
        long downloadRate = AzureMetricsTestUtil.getLongGaugeValue(getInstrumentation(), AzureFileSystemInstrumentation.WASB_DOWNLOAD_RATE);
        ITestAzureFileSystemInstrumentation.LOG.info((("Download rate: " + downloadRate) + " bytes/second."));
        expectedRate = (FILE_SIZE * 1000L) / downloadDurationMs;
        Assert.assertTrue((((((("The download rate " + downloadRate) + " is below the expected range of around ") + expectedRate) + " bytes/second that the unit test observed. This should never be") + " the case since the test underestimates the rate by looking at ") + " end-to-end time instead of just block download time."), (downloadRate >= expectedRate));
        long downloadLatency = AzureMetricsTestUtil.getLongGaugeValue(getInstrumentation(), AzureFileSystemInstrumentation.WASB_DOWNLOAD_LATENCY);
        ITestAzureFileSystemInstrumentation.LOG.info(("Download latency: " + downloadLatency));
        expectedLatency = downloadDurationMs;// We're downloading less than a block.

        Assert.assertTrue((("The download latency " + downloadLatency) + " should be greater than zero now that I've just downloaded a file."), (downloadLatency > 0));
        Assert.assertTrue((((((("The download latency " + downloadLatency) + " is more than the expected range of around ") + expectedLatency) + " milliseconds that the unit test observed. This should never be") + " the case since the test overestimates the latency by looking at ") + " end-to-end time instead of just block download time."), (downloadLatency <= expectedLatency));
        assertNoErrors();
    }

    @Test
    public void testMetricsOnBigFileCreateRead() throws Exception {
        long base = getBaseWebResponses();
        Assert.assertEquals(0, AzureMetricsTestUtil.getCurrentBytesWritten(getInstrumentation()));
        Path filePath = new Path("/metricsTest_webResponses");
        final int FILE_SIZE = (100 * 1024) * 1024;
        // Suppress auto-update of bandwidth metrics so we get
        // to update them exactly when we want to.
        getBandwidthGaugeUpdater().suppressAutoUpdate();
        // Create a file
        OutputStream outputStream = getFileSystem().create(filePath);
        outputStream.write(new byte[FILE_SIZE]);
        outputStream.close();
        // The exact number of requests/responses that happen to create a file
        // can vary  - at the time of writing this code it takes 34
        // requests/responses for the 100 MB file,
        // plus the initial container check request, but that
        // can very easily change in the future. Just assert that we do roughly
        // more than 20 but less than 50.
        logOpResponseCount("Creating a 100 MB file", base);
        base = assertWebResponsesInRange(base, 20, 50);
        getBandwidthGaugeUpdater().triggerUpdate(true);
        long totalBytesWritten = AzureMetricsTestUtil.getCurrentTotalBytesWritten(getInstrumentation());
        Assert.assertTrue((((("The total bytes written  " + totalBytesWritten) + " is pretty far from the expected range of around ") + FILE_SIZE) + " bytes plus a little overhead."), ((totalBytesWritten >= FILE_SIZE) && (totalBytesWritten < (FILE_SIZE * 2))));
        long uploadRate = AzureMetricsTestUtil.getLongGaugeValue(getInstrumentation(), AzureFileSystemInstrumentation.WASB_UPLOAD_RATE);
        ITestAzureFileSystemInstrumentation.LOG.info((("Upload rate: " + uploadRate) + " bytes/second."));
        long uploadLatency = AzureMetricsTestUtil.getLongGaugeValue(getInstrumentation(), AzureFileSystemInstrumentation.WASB_UPLOAD_LATENCY);
        ITestAzureFileSystemInstrumentation.LOG.info(("Upload latency: " + uploadLatency));
        Assert.assertTrue((("The upload latency " + uploadLatency) + " should be greater than zero now that I've just uploaded a file."), (uploadLatency > 0));
        // Read the file
        InputStream inputStream = getFileSystem().open(filePath);
        int count = 0;
        while ((inputStream.read()) >= 0) {
            count++;
        } 
        inputStream.close();
        Assert.assertEquals(FILE_SIZE, count);
        // Again, exact number varies. At the time of writing this code
        // it takes 27 request/responses, so just assert a rough range between
        // 20 and 40.
        logOpResponseCount("Reading a 100 MB file", base);
        base = assertWebResponsesInRange(base, 20, 40);
        getBandwidthGaugeUpdater().triggerUpdate(false);
        long totalBytesRead = AzureMetricsTestUtil.getCurrentTotalBytesRead(getInstrumentation());
        Assert.assertEquals(FILE_SIZE, totalBytesRead);
        long downloadRate = AzureMetricsTestUtil.getLongGaugeValue(getInstrumentation(), AzureFileSystemInstrumentation.WASB_DOWNLOAD_RATE);
        ITestAzureFileSystemInstrumentation.LOG.info((("Download rate: " + downloadRate) + " bytes/second."));
        long downloadLatency = AzureMetricsTestUtil.getLongGaugeValue(getInstrumentation(), AzureFileSystemInstrumentation.WASB_DOWNLOAD_LATENCY);
        ITestAzureFileSystemInstrumentation.LOG.info(("Download latency: " + downloadLatency));
        Assert.assertTrue((("The download latency " + downloadLatency) + " should be greater than zero now that I've just downloaded a file."), (downloadLatency > 0));
    }

    @Test
    public void testMetricsOnFileRename() throws Exception {
        long base = getBaseWebResponses();
        Path originalPath = new Path("/metricsTest_RenameStart");
        Path destinationPath = new Path("/metricsTest_RenameFinal");
        // Create an empty file
        Assert.assertEquals(0, AzureMetricsTestUtil.getLongCounterValue(getInstrumentation(), AzureFileSystemInstrumentation.WASB_FILES_CREATED));
        Assert.assertTrue(getFileSystem().createNewFile(originalPath));
        logOpResponseCount("Creating an empty file", base);
        base = assertWebResponsesInRange(base, 2, 20);
        Assert.assertEquals(1, AzureMetricsTestUtil.getLongCounterValue(getInstrumentation(), AzureFileSystemInstrumentation.WASB_FILES_CREATED));
        // Rename the file
        Assert.assertTrue(((FileSystem) (getFileSystem())).rename(originalPath, destinationPath));
        // Varies: at the time of writing this code it takes 7 requests/responses.
        logOpResponseCount("Renaming a file", base);
        base = assertWebResponsesInRange(base, 2, 15);
        assertNoErrors();
    }

    @Test
    public void testMetricsOnFileExistsDelete() throws Exception {
        long base = getBaseWebResponses();
        Path filePath = new Path("/metricsTest_delete");
        // Check existence
        Assert.assertFalse(getFileSystem().exists(filePath));
        // At the time of writing this code it takes 2 requests/responses to
        // check existence, which seems excessive, plus initial request for
        // container check, plus 2 ancestor checks only in the secure case.
        logOpResponseCount("Checking file existence for non-existent file", base);
        base = assertWebResponsesInRange(base, 1, 5);
        // Create an empty file
        Assert.assertTrue(getFileSystem().createNewFile(filePath));
        base = getCurrentWebResponses();
        // Check existence again
        Assert.assertTrue(getFileSystem().exists(filePath));
        logOpResponseCount("Checking file existence for existent file", base);
        base = assertWebResponsesInRange(base, 1, 4);
        // Delete the file
        Assert.assertEquals(0, AzureMetricsTestUtil.getLongCounterValue(getInstrumentation(), AzureFileSystemInstrumentation.WASB_FILES_DELETED));
        Assert.assertTrue(getFileSystem().delete(filePath, false));
        // At the time of writing this code it takes 4 requests/responses to
        // delete, which seems excessive. Check for range 1-4 for now.
        logOpResponseCount("Deleting a file", base);
        base = assertWebResponsesInRange(base, 1, 4);
        Assert.assertEquals(1, AzureMetricsTestUtil.getLongCounterValue(getInstrumentation(), AzureFileSystemInstrumentation.WASB_FILES_DELETED));
        assertNoErrors();
    }

    @Test
    public void testMetricsOnDirRename() throws Exception {
        long base = getBaseWebResponses();
        Path originalDirName = new Path("/metricsTestDirectory_RenameStart");
        Path innerFileName = new Path(originalDirName, "innerFile");
        Path destDirName = new Path("/metricsTestDirectory_RenameFinal");
        // Create an empty directory
        Assert.assertTrue(getFileSystem().mkdirs(originalDirName));
        base = getCurrentWebResponses();
        // Create an inner file
        Assert.assertTrue(getFileSystem().createNewFile(innerFileName));
        base = getCurrentWebResponses();
        // Rename the directory
        Assert.assertTrue(getFileSystem().rename(originalDirName, destDirName));
        // At the time of writing this code it takes 11 requests/responses
        // to rename the directory with one file. Check for range 1-20 for now.
        logOpResponseCount("Renaming a directory", base);
        base = assertWebResponsesInRange(base, 1, 20);
        assertNoErrors();
    }

    @Test
    public void testClientErrorMetrics() throws Exception {
        String fileName = "metricsTestFile_ClientError";
        Path filePath = new Path(("/" + fileName));
        final int FILE_SIZE = 100;
        OutputStream outputStream = null;
        String leaseID = null;
        try {
            // Create a file
            outputStream = getFileSystem().create(filePath);
            leaseID = getTestAccount().acquireShortLease(fileName);
            try {
                outputStream.write(new byte[FILE_SIZE]);
                outputStream.close();
                Assert.assertTrue("Should've thrown", false);
            } catch (AzureException ex) {
                Assert.assertTrue(("Unexpected exception: " + ex), ex.getMessage().contains("lease"));
            }
            Assert.assertEquals(1, AzureMetricsTestUtil.getLongCounterValue(getInstrumentation(), AzureFileSystemInstrumentation.WASB_CLIENT_ERRORS));
            Assert.assertEquals(0, AzureMetricsTestUtil.getLongCounterValue(getInstrumentation(), AzureFileSystemInstrumentation.WASB_SERVER_ERRORS));
        } finally {
            if (leaseID != null) {
                getTestAccount().releaseLease(leaseID, fileName);
            }
            IOUtils.closeStream(outputStream);
        }
    }

    /**
     * A matcher class for asserting that we got a tag with a given
     * value.
     */
    private static class TagMatcher extends ITestAzureFileSystemInstrumentation.TagExistsMatcher {
        private final String tagValue;

        public TagMatcher(String tagName, String tagValue) {
            super(tagName);
            this.tagValue = tagValue;
        }

        @Override
        public boolean matches(MetricsTag toMatch) {
            return toMatch.value().equals(tagValue);
        }

        @Override
        public String toString() {
            return ((super.toString()) + " with value ") + (tagValue);
        }
    }

    /**
     * A matcher class for asserting that we got a tag with any value.
     */
    private static class TagExistsMatcher implements ArgumentMatcher<MetricsTag> {
        private final String tagName;

        public TagExistsMatcher(String tagName) {
            this.tagName = tagName;
        }

        @Override
        public boolean matches(MetricsTag asTag) {
            return asTag.name().equals(tagName);
        }

        @Override
        public String toString() {
            return "Has tag " + (tagName);
        }
    }
}

