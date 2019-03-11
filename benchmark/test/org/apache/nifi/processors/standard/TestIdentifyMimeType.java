/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.processors.standard;


import CoreAttributes.FILENAME;
import CoreAttributes.MIME_TYPE;
import IdentifyMimeType.REL_SUCCESS;
import IdentifyMimeType.USE_FILENAME_IN_DETECTION;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;


public class TestIdentifyMimeType {
    @Test
    public void testFiles() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new IdentifyMimeType());
        final File dir = new File("src/test/resources/TestIdentifyMimeType");
        final File[] files = dir.listFiles(( ldir, name) -> (name != null) && (!(name.startsWith("."))));
        int fileCount = 0;
        for (final File file : files) {
            if (file.isDirectory()) {
                continue;
            }
            runner.enqueue(file.toPath());
            fileCount++;
        }
        runner.setThreadCount(1);
        runner.run(fileCount);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, fileCount);
        final Map<String, String> expectedMimeTypes = new HashMap<>();
        expectedMimeTypes.put("1.7z", "application/x-7z-compressed");
        expectedMimeTypes.put("1.mdb", "application/x-msaccess");
        expectedMimeTypes.put("1.txt", "text/plain");
        expectedMimeTypes.put("1.csv", "text/csv");
        expectedMimeTypes.put("1.txt.bz2", "application/x-bzip2");
        expectedMimeTypes.put("1.txt.gz", "application/gzip");
        expectedMimeTypes.put("1.zip", "application/zip");
        expectedMimeTypes.put("bgBannerFoot.png", "image/png");
        expectedMimeTypes.put("blueBtnBg.jpg", "image/jpeg");
        expectedMimeTypes.put("1.pdf", "application/pdf");
        expectedMimeTypes.put("grid.gif", "image/gif");
        expectedMimeTypes.put("1.tar", "application/x-tar");
        expectedMimeTypes.put("1.tar.gz", "application/gzip");
        expectedMimeTypes.put("1.jar", "application/java-archive");
        expectedMimeTypes.put("1.xml", "application/xml");
        expectedMimeTypes.put("flowfilev3", "application/flowfile-v3");
        expectedMimeTypes.put("flowfilev1.tar", "application/flowfile-v1");
        expectedMimeTypes.put("fake.csv", "text/csv");
        final Map<String, String> expectedExtensions = new HashMap<>();
        expectedExtensions.put("1.7z", ".7z");
        expectedExtensions.put("1.mdb", ".mdb");
        expectedExtensions.put("1.txt", ".txt");
        expectedExtensions.put("1.csv", ".csv");
        expectedExtensions.put("1.txt.bz2", ".bz2");
        expectedExtensions.put("1.txt.gz", ".gz");
        expectedExtensions.put("1.zip", ".zip");
        expectedExtensions.put("bgBannerFoot.png", ".png");
        expectedExtensions.put("blueBtnBg.jpg", ".jpg");
        expectedExtensions.put("1.pdf", ".pdf");
        expectedExtensions.put("grid.gif", ".gif");
        expectedExtensions.put("1.tar", ".tar");
        expectedExtensions.put("1.tar.gz", ".gz");
        expectedExtensions.put("1.jar", ".jar");
        expectedExtensions.put("1.xml", ".xml");
        expectedExtensions.put("flowfilev3", "");
        expectedExtensions.put("flowfilev1.tar", "");
        expectedExtensions.put("fake.csv", ".csv");
        final List<MockFlowFile> filesOut = runner.getFlowFilesForRelationship(REL_SUCCESS);
        for (final MockFlowFile file : filesOut) {
            final String filename = file.getAttribute(FILENAME.key());
            final String mimeType = file.getAttribute(MIME_TYPE.key());
            final String expected = expectedMimeTypes.get(filename);
            final String extension = file.getAttribute("mime.extension");
            final String expectedExtension = expectedExtensions.get(filename);
            Assert.assertEquals(((((("Expected " + file) + " to have MIME Type ") + expected) + ", but it was ") + mimeType), expected, mimeType);
            Assert.assertEquals(((((("Expected " + file) + " to have extension ") + expectedExtension) + ", but it was ") + extension), expectedExtension, extension);
        }
    }

    @Test
    public void testIgnoreFileName() throws Exception {
        final TestRunner runner = TestRunners.newTestRunner(new IdentifyMimeType());
        runner.setProperty(USE_FILENAME_IN_DETECTION, "false");
        runner.enqueue(Paths.get("src/test/resources/TestIdentifyMimeType/fake.csv"));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        MockFlowFile flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        flowFile.assertAttributeEquals("mime.extension", ".txt");
        flowFile.assertAttributeEquals("mime.type", "text/plain");
    }
}

