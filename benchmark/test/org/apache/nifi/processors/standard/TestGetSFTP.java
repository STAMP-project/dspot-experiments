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


import GetSFTP.REL_SUCCESS;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.nifi.processors.standard.util.SSHTestServer;
import org.apache.nifi.util.TestRunner;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestGetSFTP {
    private static final Logger logger = LoggerFactory.getLogger(TestGetSFTP.class);

    private TestRunner getSFTPRunner;

    private static SSHTestServer sshTestServer;

    @Test
    public void testGetSFTPFileBasicRead() throws IOException {
        emptyTestDirectory();
        touchFile(((TestGetSFTP.sshTestServer.getVirtualFileSystemPath()) + "testFile1.txt"));
        touchFile(((TestGetSFTP.sshTestServer.getVirtualFileSystemPath()) + "testFile2.txt"));
        touchFile(((TestGetSFTP.sshTestServer.getVirtualFileSystemPath()) + "testFile3.txt"));
        touchFile(((TestGetSFTP.sshTestServer.getVirtualFileSystemPath()) + "testFile4.txt"));
        getSFTPRunner.run();
        getSFTPRunner.assertTransferCount(REL_SUCCESS, 4);
        // Verify files deleted
        for (int i = 1; i < 5; i++) {
            Path file1 = Paths.get(((((TestGetSFTP.sshTestServer.getVirtualFileSystemPath()) + "/testFile") + i) + ".txt"));
            Assert.assertTrue("File not deleted.", (!(file1.toAbsolutePath().toFile().exists())));
        }
        getSFTPRunner.clearTransferState();
    }

    @Test
    public void testGetSFTPIgnoreDottedFiles() throws IOException {
        emptyTestDirectory();
        touchFile(((TestGetSFTP.sshTestServer.getVirtualFileSystemPath()) + "testFile1.txt"));
        touchFile(((TestGetSFTP.sshTestServer.getVirtualFileSystemPath()) + ".testFile2.txt"));
        touchFile(((TestGetSFTP.sshTestServer.getVirtualFileSystemPath()) + "testFile3.txt"));
        touchFile(((TestGetSFTP.sshTestServer.getVirtualFileSystemPath()) + ".testFile4.txt"));
        getSFTPRunner.run();
        getSFTPRunner.assertTransferCount(REL_SUCCESS, 2);
        // Verify non-dotted files were deleted and dotted files were not deleted
        Path file1 = Paths.get(((TestGetSFTP.sshTestServer.getVirtualFileSystemPath()) + "/testFile1.txt"));
        Assert.assertTrue("File not deleted.", (!(file1.toAbsolutePath().toFile().exists())));
        file1 = Paths.get(((TestGetSFTP.sshTestServer.getVirtualFileSystemPath()) + "/testFile3.txt"));
        Assert.assertTrue("File not deleted.", (!(file1.toAbsolutePath().toFile().exists())));
        file1 = Paths.get(((TestGetSFTP.sshTestServer.getVirtualFileSystemPath()) + "/.testFile2.txt"));
        Assert.assertTrue("File deleted.", file1.toAbsolutePath().toFile().exists());
        file1 = Paths.get(((TestGetSFTP.sshTestServer.getVirtualFileSystemPath()) + "/.testFile4.txt"));
        Assert.assertTrue("File deleted.", file1.toAbsolutePath().toFile().exists());
        getSFTPRunner.clearTransferState();
    }
}

