/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.storage.azure;


import FileUtils.FileCopyResult;
import com.microsoft.azure.storage.StorageException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import org.apache.druid.java.util.common.FileUtils;
import org.apache.druid.segment.loading.SegmentLoadingException;
import org.easymock.EasyMockSupport;
import org.junit.Assert;
import org.junit.Test;

import static org.apache.commons.io.FileUtils.deleteDirectory;


public class AzureDataSegmentPullerTest extends EasyMockSupport {
    private static final String SEGMENT_FILE_NAME = "segment";

    private static final String containerName = "container";

    private static final String blobPath = "/path/to/storage/index.zip";

    private AzureStorage azureStorage;

    @Test
    public void testZIPUncompress() throws StorageException, IOException, URISyntaxException, SegmentLoadingException {
        final String value = "bucket";
        final File pulledFile = AzureTestUtils.createZipTempFile(AzureDataSegmentPullerTest.SEGMENT_FILE_NAME, value);
        final File toDir = Files.createTempDirectory("druid").toFile();
        try {
            final InputStream zipStream = new FileInputStream(pulledFile);
            expect(azureStorage.getBlobInputStream(AzureDataSegmentPullerTest.containerName, AzureDataSegmentPullerTest.blobPath)).andReturn(zipStream);
            replayAll();
            AzureDataSegmentPuller puller = new AzureDataSegmentPuller(azureStorage);
            FileUtils.FileCopyResult result = puller.getSegmentFiles(AzureDataSegmentPullerTest.containerName, AzureDataSegmentPullerTest.blobPath, toDir);
            File expected = new File(toDir, AzureDataSegmentPullerTest.SEGMENT_FILE_NAME);
            Assert.assertEquals(value.length(), result.size());
            Assert.assertTrue(expected.exists());
            Assert.assertEquals(value.length(), expected.length());
            verifyAll();
        } finally {
            pulledFile.delete();
            deleteDirectory(toDir);
        }
    }

    @Test(expected = RuntimeException.class)
    public void testDeleteOutputDirectoryWhenErrorIsRaisedPullingSegmentFiles() throws StorageException, IOException, URISyntaxException, SegmentLoadingException {
        final File outDir = Files.createTempDirectory("druid").toFile();
        try {
            expect(azureStorage.getBlobInputStream(AzureDataSegmentPullerTest.containerName, AzureDataSegmentPullerTest.blobPath)).andThrow(new URISyntaxException("error", "error", 404));
            replayAll();
            AzureDataSegmentPuller puller = new AzureDataSegmentPuller(azureStorage);
            puller.getSegmentFiles(AzureDataSegmentPullerTest.containerName, AzureDataSegmentPullerTest.blobPath, outDir);
            Assert.assertFalse(outDir.exists());
            verifyAll();
        } finally {
            deleteDirectory(outDir);
        }
    }
}

