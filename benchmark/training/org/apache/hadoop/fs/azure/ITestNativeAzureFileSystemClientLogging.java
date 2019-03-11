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


import org.apache.commons.logging.impl.Log4JLogger;
import org.apache.hadoop.test.GenericTestUtils.LogCapturer;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test to validate Azure storage client side logging. Tests works only when
 * testing with Live Azure storage because Emulator does not have support for
 * client-side logging.
 *
 * <I>Important: </I> Do not attempt to move off commons-logging.
 * The tests will fail.
 */
public class ITestNativeAzureFileSystemClientLogging extends AbstractWasbTestBase {
    // Core-site config controlling Azure Storage Client logging
    private static final String KEY_LOGGING_CONF_STRING = "fs.azure.storage.client.logging";

    // Temporary directory created using WASB.
    private static final String TEMP_DIR = "tempDir";

    @Test
    public void testLoggingEnabled() throws Exception {
        LogCapturer logs = LogCapturer.captureLogs(new Log4JLogger(Logger.getRootLogger()));
        // Update configuration based on the Test.
        updateFileSystemConfiguration(true);
        performWASBOperations();
        String output = getLogOutput(logs);
        Assert.assertTrue(((("Log entry " + (ITestNativeAzureFileSystemClientLogging.TEMP_DIR)) + " not found  in ") + output), verifyStorageClientLogs(output, ITestNativeAzureFileSystemClientLogging.TEMP_DIR));
    }

    @Test
    public void testLoggingDisabled() throws Exception {
        LogCapturer logs = LogCapturer.captureLogs(new Log4JLogger(Logger.getRootLogger()));
        // Update configuration based on the Test.
        updateFileSystemConfiguration(false);
        performWASBOperations();
        String output = getLogOutput(logs);
        Assert.assertFalse(((("Log entry " + (ITestNativeAzureFileSystemClientLogging.TEMP_DIR)) + " found  in ") + output), verifyStorageClientLogs(output, ITestNativeAzureFileSystemClientLogging.TEMP_DIR));
    }
}

