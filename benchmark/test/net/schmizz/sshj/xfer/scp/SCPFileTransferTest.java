/**
 * Copyright (C)2009 - SSHJ Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.schmizz.sshj.xfer.scp;


import com.hierynomus.sshj.test.SshFixture;
import com.hierynomus.sshj.test.util.FileUtil;
import java.io.File;
import java.io.IOException;
import junit.framework.Assert;
import net.schmizz.sshj.SSHClient;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class SCPFileTransferTest {
    public static final String DEFAULT_FILE_NAME = "my_file.txt";

    File targetDir;

    File sourceFile;

    File targetFile;

    SSHClient sshClient;

    @Rule
    public SshFixture fixture = new SshFixture();

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void shouldSCPUploadFile() throws IOException {
        SCPFileTransfer scpFileTransfer = sshClient.newSCPFileTransfer();
        Assert.assertFalse(targetFile.exists());
        Assert.assertTrue(targetDir.exists());
        scpFileTransfer.upload(sourceFile.getAbsolutePath(), targetDir.getAbsolutePath());
        Assert.assertTrue(targetFile.exists());
    }

    @Test
    public void shouldSCPUploadFileWithBandwidthLimit() throws IOException {
        // Limit upload transfer at 2Mo/s
        SCPFileTransfer scpFileTransfer = sshClient.newSCPFileTransfer().bandwidthLimit(16000);
        Assert.assertFalse(targetFile.exists());
        scpFileTransfer.upload(sourceFile.getAbsolutePath(), targetDir.getAbsolutePath());
        Assert.assertTrue(targetFile.exists());
    }

    @Test
    public void shouldSCPDownloadFile() throws IOException {
        SCPFileTransfer scpFileTransfer = sshClient.newSCPFileTransfer();
        Assert.assertFalse(targetFile.exists());
        scpFileTransfer.download(sourceFile.getAbsolutePath(), targetDir.getAbsolutePath());
        Assert.assertTrue(targetFile.exists());
    }

    @Test
    public void shouldSCPDownloadFileWithBandwidthLimit() throws IOException {
        // Limit download transfer at 128Ko/s
        SCPFileTransfer scpFileTransfer = sshClient.newSCPFileTransfer().bandwidthLimit(1024);
        Assert.assertFalse(targetFile.exists());
        scpFileTransfer.download(sourceFile.getAbsolutePath(), targetDir.getAbsolutePath());
        Assert.assertTrue(targetFile.exists());
    }

    @Test
    public void shouldSCPDownloadFileWithoutPathEscaping() throws IOException {
        SCPFileTransfer scpFileTransfer = sshClient.newSCPFileTransfer();
        Assert.assertFalse(targetFile.exists());
        File file = tempFolder.newFile("new file.txt");
        FileUtil.writeToFile(file, "Some content");
        scpFileTransfer.download(((tempFolder.getRoot().getAbsolutePath()) + "/new file.txt"), targetFile.getAbsolutePath());
        Assert.assertTrue(targetFile.exists());
        MatcherAssert.assertThat(FileUtil.readFromFile(targetFile), CoreMatchers.containsString("Some content"));
    }
}

