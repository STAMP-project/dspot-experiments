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
package org.apache.hadoop.fs.azure;


import NativeAzureFileSystem.APPEND_SUPPORT_ENABLE_PROPERTY_NAME;
import java.io.IOException;
import java.net.URI;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test append operations.
 */
public class ITestNativeAzureFileSystemAppend extends AbstractWasbTestBase {
    private Path testPath;

    /* Test case to verify if an append on small size data works. This tests
    append E2E
     */
    @Test
    public void testSingleAppend() throws Throwable {
        FSDataOutputStream appendStream = null;
        try {
            int baseDataSize = 50;
            byte[] baseDataBuffer = createBaseFileWithData(baseDataSize, testPath);
            int appendDataSize = 20;
            byte[] appendDataBuffer = ITestNativeAzureFileSystemAppend.getTestData(appendDataSize);
            appendStream = fs.append(testPath, 10);
            appendStream.write(appendDataBuffer);
            appendStream.close();
            byte[] testData = new byte[baseDataSize + appendDataSize];
            System.arraycopy(baseDataBuffer, 0, testData, 0, baseDataSize);
            System.arraycopy(appendDataBuffer, 0, testData, baseDataSize, appendDataSize);
            Assert.assertTrue(verifyAppend(testData, testPath));
        } finally {
            if (appendStream != null) {
                appendStream.close();
            }
        }
    }

    /* Test case to verify append to an empty file. */
    @Test
    public void testSingleAppendOnEmptyFile() throws Throwable {
        FSDataOutputStream appendStream = null;
        try {
            createBaseFileWithData(0, testPath);
            int appendDataSize = 20;
            byte[] appendDataBuffer = ITestNativeAzureFileSystemAppend.getTestData(appendDataSize);
            appendStream = fs.append(testPath, 10);
            appendStream.write(appendDataBuffer);
            appendStream.close();
            Assert.assertTrue(verifyAppend(appendDataBuffer, testPath));
        } finally {
            if (appendStream != null) {
                appendStream.close();
            }
        }
    }

    /* Test to verify that we can open only one Append stream on a File. */
    @Test
    public void testSingleAppenderScenario() throws Throwable {
        FSDataOutputStream appendStream1 = null;
        FSDataOutputStream appendStream2 = null;
        IOException ioe = null;
        try {
            createBaseFileWithData(0, testPath);
            appendStream1 = fs.append(testPath, 10);
            boolean encounteredException = false;
            try {
                appendStream2 = fs.append(testPath, 10);
            } catch (IOException ex) {
                encounteredException = true;
                ioe = ex;
            }
            appendStream1.close();
            Assert.assertTrue(encounteredException);
            GenericTestUtils.assertExceptionContains("Unable to set Append lease on the Blob", ioe);
        } finally {
            if (appendStream1 != null) {
                appendStream1.close();
            }
            if (appendStream2 != null) {
                appendStream2.close();
            }
        }
    }

    /* Tests to verify multiple appends on a Blob. */
    @Test
    public void testMultipleAppends() throws Throwable {
        int baseDataSize = 50;
        byte[] baseDataBuffer = createBaseFileWithData(baseDataSize, testPath);
        int appendDataSize = 100;
        int targetAppendCount = 50;
        byte[] testData = new byte[baseDataSize + (appendDataSize * targetAppendCount)];
        int testDataIndex = 0;
        System.arraycopy(baseDataBuffer, 0, testData, testDataIndex, baseDataSize);
        testDataIndex += baseDataSize;
        int appendCount = 0;
        FSDataOutputStream appendStream = null;
        try {
            while (appendCount < targetAppendCount) {
                byte[] appendDataBuffer = ITestNativeAzureFileSystemAppend.getTestData(appendDataSize);
                appendStream = fs.append(testPath, 30);
                appendStream.write(appendDataBuffer);
                appendStream.close();
                System.arraycopy(appendDataBuffer, 0, testData, testDataIndex, appendDataSize);
                testDataIndex += appendDataSize;
                appendCount++;
            } 
            Assert.assertTrue(verifyAppend(testData, testPath));
        } finally {
            if (appendStream != null) {
                appendStream.close();
            }
        }
    }

    /* Test to verify we multiple appends on the same stream. */
    @Test
    public void testMultipleAppendsOnSameStream() throws Throwable {
        int baseDataSize = 50;
        byte[] baseDataBuffer = createBaseFileWithData(baseDataSize, testPath);
        int appendDataSize = 100;
        int targetAppendCount = 50;
        byte[] testData = new byte[baseDataSize + (appendDataSize * targetAppendCount)];
        int testDataIndex = 0;
        System.arraycopy(baseDataBuffer, 0, testData, testDataIndex, baseDataSize);
        testDataIndex += baseDataSize;
        int appendCount = 0;
        FSDataOutputStream appendStream = null;
        try {
            while (appendCount < targetAppendCount) {
                appendStream = fs.append(testPath, 50);
                int singleAppendChunkSize = 20;
                int appendRunSize = 0;
                while (appendRunSize < appendDataSize) {
                    byte[] appendDataBuffer = ITestNativeAzureFileSystemAppend.getTestData(singleAppendChunkSize);
                    appendStream.write(appendDataBuffer);
                    System.arraycopy(appendDataBuffer, 0, testData, (testDataIndex + appendRunSize), singleAppendChunkSize);
                    appendRunSize += singleAppendChunkSize;
                } 
                appendStream.close();
                testDataIndex += appendDataSize;
                appendCount++;
            } 
            Assert.assertTrue(verifyAppend(testData, testPath));
        } finally {
            if (appendStream != null) {
                appendStream.close();
            }
        }
    }

    /* Test to verify the behavior when Append Support configuration flag is set to false */
    @Test(expected = UnsupportedOperationException.class)
    public void testFalseConfigurationFlagBehavior() throws Throwable {
        fs = testAccount.getFileSystem();
        Configuration conf = fs.getConf();
        conf.setBoolean(APPEND_SUPPORT_ENABLE_PROPERTY_NAME, false);
        URI uri = fs.getUri();
        fs.initialize(uri, conf);
        FSDataOutputStream appendStream = null;
        try {
            createBaseFileWithData(0, testPath);
            appendStream = fs.append(testPath, 10);
        } finally {
            if (appendStream != null) {
                appendStream.close();
            }
        }
    }
}

