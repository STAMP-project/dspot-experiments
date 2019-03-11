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
import FTPTransfer.PORT;
import ListFile.FILE_GROUP_ATTRIBUTE;
import ListFile.FILE_LAST_MODIFY_TIME_ATTRIBUTE;
import ListFile.FILE_OWNER_ATTRIBUTE;
import ListFile.FILE_PERMISSIONS_ATTRIBUTE;
import ListFile.FILE_SIZE_ATTRIBUTE;
import ListFile.MAX_SIZE;
import ListFile.MIN_SIZE;
import ListFile.PRECISION_MILLIS;
import ListFile.TARGET_SYSTEM_TIMESTAMP_PRECISION;
import ListSFTP.HOSTNAME;
import ListSFTP.REL_SUCCESS;
import ListSFTP.REMOTE_PATH;
import ListSFTP.USERNAME;
import SFTPTransfer.PASSWORD;
import com.github.stefanbirkner.fakesftpserver.rule.FakeSftpServerRule;
import java.util.concurrent.TimeUnit;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Rule;
import org.junit.Test;


public class TestListSFTP {
    @Rule
    public final FakeSftpServerRule sftpServer = new FakeSftpServerRule();

    int port;

    final String username = "nifi-sftp-user";

    final String password = "Test test test chocolate";

    @Test
    public void basicFileList() throws InterruptedException {
        TestRunner runner = TestRunners.newTestRunner(ListSFTP.class);
        runner.setProperty(HOSTNAME, "localhost");
        runner.setProperty(USERNAME, username);
        runner.setProperty(PASSWORD, password);
        runner.setProperty(PORT, Integer.toString(port));
        runner.setProperty(REMOTE_PATH, "/directory/");
        runner.setProperty(TARGET_SYSTEM_TIMESTAMP_PRECISION, PRECISION_MILLIS);
        runner.assertValid();
        // Ensure wait for enough lag time.
        Thread.sleep(((LISTING_LAG_MILLIS.get(TimeUnit.MILLISECONDS)) * 2));
        runner.run();
        runner.assertTransferCount(REL_SUCCESS, 3);
        runner.assertAllFlowFilesContainAttribute("sftp.remote.host");
        runner.assertAllFlowFilesContainAttribute("sftp.remote.port");
        runner.assertAllFlowFilesContainAttribute("sftp.listing.user");
        runner.assertAllFlowFilesContainAttribute(FILE_OWNER_ATTRIBUTE);
        runner.assertAllFlowFilesContainAttribute(FILE_GROUP_ATTRIBUTE);
        runner.assertAllFlowFilesContainAttribute(FILE_PERMISSIONS_ATTRIBUTE);
        runner.assertAllFlowFilesContainAttribute(FILE_SIZE_ATTRIBUTE);
        runner.assertAllFlowFilesContainAttribute(FILE_LAST_MODIFY_TIME_ATTRIBUTE);
        runner.assertAllFlowFilesContainAttribute("filename");
        final MockFlowFile retrievedFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        retrievedFile.assertAttributeEquals("sftp.listing.user", username);
    }

    @Test
    public void sizeFilteredFileList() throws InterruptedException {
        TestRunner runner = TestRunners.newTestRunner(ListSFTP.class);
        runner.setProperty(HOSTNAME, "localhost");
        runner.setProperty(USERNAME, username);
        runner.setProperty(PASSWORD, password);
        runner.setProperty(PORT, Integer.toString(port));
        runner.setProperty(REMOTE_PATH, "/directory/");
        runner.setProperty(MIN_SIZE, "8B");
        runner.setProperty(MAX_SIZE, "100B");
        runner.setProperty(TARGET_SYSTEM_TIMESTAMP_PRECISION, PRECISION_MILLIS);
        runner.assertValid();
        // Ensure wait for enough lag time.
        Thread.sleep(((LISTING_LAG_MILLIS.get(TimeUnit.MILLISECONDS)) * 2));
        runner.run();
        runner.assertTransferCount(REL_SUCCESS, 1);
        final MockFlowFile retrievedFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        // the only file between the limits
        retrievedFile.assertAttributeEquals("filename", "file.txt");
    }
}

