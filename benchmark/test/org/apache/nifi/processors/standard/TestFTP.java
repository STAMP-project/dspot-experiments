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


import AbstractListProcessor.LISTING_LAG_MILLIS;
import CoreAttributes.FILENAME;
import FTPTransfer.HOSTNAME;
import FTPTransfer.PASSWORD;
import FTPTransfer.PORT;
import FTPTransfer.REMOTE_PATH;
import FTPTransfer.USERNAME;
import FetchFTP.COMPLETION_MOVE;
import FetchFTP.COMPLETION_STRATEGY;
import FetchFTP.MOVE_DESTINATION_DIR;
import FetchFTP.REMOTE_FILENAME;
import ListFile.FILE_GROUP_ATTRIBUTE;
import ListFile.FILE_LAST_MODIFY_TIME_ATTRIBUTE;
import ListFile.FILE_OWNER_ATTRIBUTE;
import ListFile.FILE_PERMISSIONS_ATTRIBUTE;
import ListFile.FILE_SIZE_ATTRIBUTE;
import ListFile.PRECISION_MILLIS;
import ListFile.TARGET_SYSTEM_TIMESTAMP_PRECISION;
import PutFTP.REL_SUCCESS;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.nifi.components.ValidationResult;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.MockProcessContext;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.filesystem.FileEntry;
import org.mockftpserver.fake.filesystem.FileSystem;


public class TestFTP {
    final FakeFtpServer fakeFtpServer = new FakeFtpServer();

    final String username = "nifi-ftp-user";

    final String password = "Test test test chocolate";

    int ftpPort;

    @Test
    public void testValidators() {
        TestRunner runner = TestRunners.newTestRunner(PutFTP.class);
        Collection<ValidationResult> results;
        ProcessContext pc;
        /* Set the basic required values */
        results = new HashSet();
        runner.setProperty(USERNAME, "${el-username}");
        runner.setProperty(HOSTNAME, "static-hostname");
        runner.setProperty(PORT, "${el-portNumber}");
        results = new HashSet();
        runner.setProperty(REMOTE_PATH, "static-remote-target");
        runner.enqueue(new byte[0]);
        pc = runner.getProcessContext();
        if (pc instanceof MockProcessContext) {
            results = validate();
        }
        Assert.assertEquals(0, results.size());
        results = new HashSet();
        runner.setProperty(REMOTE_PATH, "${el-remote-target}");
        runner.enqueue(new byte[0]);
        pc = runner.getProcessContext();
        if (pc instanceof MockProcessContext) {
            results = validate();
        }
        Assert.assertEquals(0, results.size());
        results = new HashSet();
        runner.setProperty(USERNAME, "static-username");
        runner.enqueue(new byte[0]);
        pc = runner.getProcessContext();
        if (pc instanceof MockProcessContext) {
            results = validate();
        }
        Assert.assertEquals(0, results.size());
        /* Try an invalid expression */
        results = new HashSet();
        runner.setProperty(USERNAME, "");
        runner.enqueue(new byte[0]);
        pc = runner.getProcessContext();
        if (pc instanceof MockProcessContext) {
            results = validate();
        }
        Assert.assertEquals(1, results.size());
    }

    @Test
    public void basicFileUpload() throws IOException {
        TestRunner runner = TestRunners.newTestRunner(PutFTP.class);
        runner.setProperty(HOSTNAME, "localhost");
        runner.setProperty(USERNAME, username);
        runner.setProperty(PASSWORD, password);
        runner.setProperty(PORT, Integer.toString(ftpPort));
        try (FileInputStream fis = new FileInputStream("src/test/resources/randombytes-1")) {
            Map<String, String> attributes = new HashMap<String, String>();
            attributes.put(FILENAME.key(), "randombytes-1");
            runner.enqueue(fis, attributes);
            runner.run();
        }
        runner.assertQueueEmpty();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        FileSystem results = fakeFtpServer.getFileSystem();
        // Check file was uploaded
        Assert.assertTrue(results.exists("c:\\data\\randombytes-1"));
    }

    @Test
    public void basicFileGet() throws IOException {
        FileSystem results = fakeFtpServer.getFileSystem();
        FileEntry sampleFile = new FileEntry("c:\\data\\randombytes-2");
        sampleFile.setContents("Just some random test test test chocolate");
        results.add(sampleFile);
        // Check file exists
        Assert.assertTrue(results.exists("c:\\data\\randombytes-2"));
        TestRunner runner = TestRunners.newTestRunner(GetFTP.class);
        runner.setProperty(HOSTNAME, "localhost");
        runner.setProperty(USERNAME, username);
        runner.setProperty(PASSWORD, password);
        runner.setProperty(PORT, Integer.toString(ftpPort));
        runner.setProperty(REMOTE_PATH, "/");
        runner.run();
        final MockFlowFile retrievedFile = runner.getFlowFilesForRelationship(GetFTP.REL_SUCCESS).get(0);
        retrievedFile.assertContentEquals("Just some random test test test chocolate");
    }

    @Test
    public void basicFileFetch() throws IOException {
        FileSystem results = fakeFtpServer.getFileSystem();
        FileEntry sampleFile = new FileEntry("c:\\data\\randombytes-2");
        sampleFile.setContents("Just some random test test test chocolate");
        results.add(sampleFile);
        // Check file exists
        Assert.assertTrue(results.exists("c:\\data\\randombytes-2"));
        TestRunner runner = TestRunners.newTestRunner(FetchFTP.class);
        runner.setProperty(FetchFTP.HOSTNAME, "${host}");
        runner.setProperty(FetchFTP.USERNAME, "${username}");
        runner.setProperty(PASSWORD, password);
        runner.setProperty(PORT, "${port}");
        runner.setProperty(REMOTE_FILENAME, "c:\\data\\randombytes-2");
        runner.setProperty(COMPLETION_STRATEGY, COMPLETION_MOVE);
        runner.setProperty(MOVE_DESTINATION_DIR, "data");
        Map<String, String> attrs = new HashMap<String, String>();
        attrs.put("host", "localhost");
        attrs.put("username", username);
        attrs.put("port", Integer.toString(ftpPort));
        runner.enqueue("", attrs);
        runner.run();
        final MockFlowFile retrievedFile = runner.getFlowFilesForRelationship(FetchFTP.REL_SUCCESS).get(0);
        retrievedFile.assertContentEquals("Just some random test test test chocolate");
    }

    @Test
    public void basicFileList() throws IOException, InterruptedException {
        FileSystem results = fakeFtpServer.getFileSystem();
        FileEntry sampleFile = new FileEntry("c:\\data\\randombytes-2");
        sampleFile.setContents("Just some random test test test chocolate");
        results.add(sampleFile);
        // Check file exists
        Assert.assertTrue(results.exists("c:\\data\\randombytes-2"));
        TestRunner runner = TestRunners.newTestRunner(ListFTP.class);
        runner.setProperty(ListFTP.HOSTNAME, "localhost");
        runner.setProperty(ListFTP.USERNAME, username);
        runner.setProperty(PASSWORD, password);
        runner.setProperty(PORT, Integer.toString(ftpPort));
        runner.setProperty(ListFTP.REMOTE_PATH, "/");
        // FakeFTPServer has timestamp precision in minutes.
        // Specify milliseconds precision so that test does not need to wait for minutes.
        runner.setProperty(TARGET_SYSTEM_TIMESTAMP_PRECISION, PRECISION_MILLIS);
        runner.assertValid();
        // Ensure wait for enough lag time.
        Thread.sleep(((LISTING_LAG_MILLIS.get(TimeUnit.MILLISECONDS)) * 2));
        runner.run();
        runner.assertTransferCount(FetchFTP.REL_SUCCESS, 1);
        final MockFlowFile retrievedFile = runner.getFlowFilesForRelationship(FetchFTP.REL_SUCCESS).get(0);
        runner.assertAllFlowFilesContainAttribute("ftp.remote.host");
        runner.assertAllFlowFilesContainAttribute("ftp.remote.port");
        runner.assertAllFlowFilesContainAttribute("ftp.listing.user");
        runner.assertAllFlowFilesContainAttribute(FILE_OWNER_ATTRIBUTE);
        runner.assertAllFlowFilesContainAttribute(FILE_GROUP_ATTRIBUTE);
        runner.assertAllFlowFilesContainAttribute(FILE_PERMISSIONS_ATTRIBUTE);
        runner.assertAllFlowFilesContainAttribute(FILE_SIZE_ATTRIBUTE);
        runner.assertAllFlowFilesContainAttribute(FILE_LAST_MODIFY_TIME_ATTRIBUTE);
        retrievedFile.assertAttributeEquals("ftp.listing.user", username);
        retrievedFile.assertAttributeEquals("filename", "randombytes-2");
    }
}

