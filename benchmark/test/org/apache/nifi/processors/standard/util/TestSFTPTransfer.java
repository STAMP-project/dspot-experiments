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
package org.apache.nifi.processors.standard.util;


import SFTPTransfer.DISABLE_DIRECTORY_LISTING;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.MockPropertyValue;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestSFTPTransfer {
    private static final Logger logger = LoggerFactory.getLogger(TestSFTPTransfer.class);

    @Test
    public void testEnsureDirectoryExistsAlreadyExisted() throws SftpException, IOException {
        final ProcessContext processContext = Mockito.mock(ProcessContext.class);
        final ChannelSftp channel = Mockito.mock(ChannelSftp.class);
        final SFTPTransfer sftpTransfer = createSftpTransfer(processContext, channel);
        final MockFlowFile flowFile = new MockFlowFile(0);
        final File remoteDir = new File("/dir1/dir2/dir3");
        sftpTransfer.ensureDirectoryExists(flowFile, remoteDir);
        // Dir existence check should be done by stat
        Mockito.verify(channel).stat(ArgumentMatchers.eq("/dir1/dir2/dir3"));
    }

    @Test
    public void testEnsureDirectoryExistsFailedToStat() throws SftpException, IOException {
        final ProcessContext processContext = Mockito.mock(ProcessContext.class);
        final ChannelSftp channel = Mockito.mock(ChannelSftp.class);
        // stat for the parent was successful, simulating that dir2 exists, but no dir3.
        Mockito.when(channel.stat("/dir1/dir2/dir3")).thenThrow(new SftpException(ChannelSftp.SSH_FX_FAILURE, "Failure"));
        final SFTPTransfer sftpTransfer = createSftpTransfer(processContext, channel);
        final MockFlowFile flowFile = new MockFlowFile(0);
        final File remoteDir = new File("/dir1/dir2/dir3");
        try {
            sftpTransfer.ensureDirectoryExists(flowFile, remoteDir);
            Assert.fail("Should fail");
        } catch (IOException e) {
            Assert.assertEquals("Failed to determine if remote directory exists at /dir1/dir2/dir3 due to 4: Failure", e.getMessage());
        }
        // Dir existence check should be done by stat
        Mockito.verify(channel).stat(ArgumentMatchers.eq("/dir1/dir2/dir3"));
    }

    @Test
    public void testEnsureDirectoryExistsNotExisted() throws SftpException, IOException {
        final ProcessContext processContext = Mockito.mock(ProcessContext.class);
        final ChannelSftp channel = Mockito.mock(ChannelSftp.class);
        // stat for the parent was successful, simulating that dir2 exists, but no dir3.
        Mockito.when(channel.stat("/dir1/dir2/dir3")).thenThrow(new SftpException(ChannelSftp.SSH_FX_NO_SUCH_FILE, "No such file"));
        final SFTPTransfer sftpTransfer = createSftpTransfer(processContext, channel);
        final MockFlowFile flowFile = new MockFlowFile(0);
        final File remoteDir = new File("/dir1/dir2/dir3");
        sftpTransfer.ensureDirectoryExists(flowFile, remoteDir);
        // Dir existence check should be done by stat
        Mockito.verify(channel).stat(ArgumentMatchers.eq("/dir1/dir2/dir3"));// dir3 was not found

        Mockito.verify(channel).stat(ArgumentMatchers.eq("/dir1/dir2"));// so, dir2 was checked

        Mockito.verify(channel).mkdir(ArgumentMatchers.eq("/dir1/dir2/dir3"));// dir2 existed, so dir3 was created.

    }

    @Test
    public void testEnsureDirectoryExistsParentNotExisted() throws SftpException, IOException {
        final ProcessContext processContext = Mockito.mock(ProcessContext.class);
        final ChannelSftp channel = Mockito.mock(ChannelSftp.class);
        // stat for the dir1 was successful, simulating that dir1 exists, but no dir2 and dir3.
        Mockito.when(channel.stat("/dir1/dir2/dir3")).thenThrow(new SftpException(ChannelSftp.SSH_FX_NO_SUCH_FILE, "No such file"));
        Mockito.when(channel.stat("/dir1/dir2")).thenThrow(new SftpException(ChannelSftp.SSH_FX_NO_SUCH_FILE, "No such file"));
        final SFTPTransfer sftpTransfer = createSftpTransfer(processContext, channel);
        final MockFlowFile flowFile = new MockFlowFile(0);
        final File remoteDir = new File("/dir1/dir2/dir3");
        sftpTransfer.ensureDirectoryExists(flowFile, remoteDir);
        // Dir existence check should be done by stat
        Mockito.verify(channel).stat(ArgumentMatchers.eq("/dir1/dir2/dir3"));// dir3 was not found

        Mockito.verify(channel).stat(ArgumentMatchers.eq("/dir1/dir2"));// dir2 was not found, too

        Mockito.verify(channel).stat(ArgumentMatchers.eq("/dir1"));// dir1 was found

        Mockito.verify(channel).mkdir(ArgumentMatchers.eq("/dir1/dir2"));// dir1 existed, so dir2 was created.

        Mockito.verify(channel).mkdir(ArgumentMatchers.eq("/dir1/dir2/dir3"));// then dir3 was created.

    }

    @Test
    public void testEnsureDirectoryExistsNotExistedFailedToCreate() throws SftpException, IOException {
        final ProcessContext processContext = Mockito.mock(ProcessContext.class);
        final ChannelSftp channel = Mockito.mock(ChannelSftp.class);
        // stat for the parent was successful, simulating that dir2 exists, but no dir3.
        Mockito.when(channel.stat("/dir1/dir2/dir3")).thenThrow(new SftpException(ChannelSftp.SSH_FX_NO_SUCH_FILE, "No such file"));
        // Failed to create dir3.
        Mockito.doThrow(new SftpException(ChannelSftp.SSH_FX_FAILURE, "Failed")).when(channel).mkdir(ArgumentMatchers.eq("/dir1/dir2/dir3"));
        final SFTPTransfer sftpTransfer = createSftpTransfer(processContext, channel);
        final MockFlowFile flowFile = new MockFlowFile(0);
        final File remoteDir = new File("/dir1/dir2/dir3");
        try {
            sftpTransfer.ensureDirectoryExists(flowFile, remoteDir);
            Assert.fail("Should fail");
        } catch (IOException e) {
            Assert.assertEquals("Failed to create remote directory /dir1/dir2/dir3 due to 4: Failed", e.getMessage());
        }
        // Dir existence check should be done by stat
        Mockito.verify(channel).stat(ArgumentMatchers.eq("/dir1/dir2/dir3"));// dir3 was not found

        Mockito.verify(channel).stat(ArgumentMatchers.eq("/dir1/dir2"));// so, dir2 was checked

        Mockito.verify(channel).mkdir(ArgumentMatchers.eq("/dir1/dir2/dir3"));// dir2 existed, so dir3 was created.

    }

    @Test
    public void testEnsureDirectoryExistsBlindlyNotExisted() throws SftpException, IOException {
        final ProcessContext processContext = Mockito.mock(ProcessContext.class);
        Mockito.when(processContext.getProperty(DISABLE_DIRECTORY_LISTING)).thenReturn(new MockPropertyValue("true"));
        final ChannelSftp channel = Mockito.mock(ChannelSftp.class);
        final SFTPTransfer sftpTransfer = createSftpTransfer(processContext, channel);
        final MockFlowFile flowFile = new MockFlowFile(0);
        final File remoteDir = new File("/dir1/dir2/dir3");
        sftpTransfer.ensureDirectoryExists(flowFile, remoteDir);
        // stat should not be called.
        Mockito.verify(channel, Mockito.times(0)).stat(ArgumentMatchers.eq("/dir1/dir2/dir3"));
        Mockito.verify(channel).mkdir(ArgumentMatchers.eq("/dir1/dir2/dir3"));// dir3 was created blindly.

    }

    @Test
    public void testEnsureDirectoryExistsBlindlyParentNotExisted() throws SftpException, IOException {
        final ProcessContext processContext = Mockito.mock(ProcessContext.class);
        Mockito.when(processContext.getProperty(DISABLE_DIRECTORY_LISTING)).thenReturn(new MockPropertyValue("true"));
        final ChannelSftp channel = Mockito.mock(ChannelSftp.class);
        final AtomicInteger mkdirCount = new AtomicInteger(0);
        Mockito.doAnswer(( invocation) -> {
            final int cnt = mkdirCount.getAndIncrement();
            if (cnt == 0) {
                // If the parent dir does not exist, no such file exception is thrown.
                throw new SftpException(ChannelSftp.SSH_FX_NO_SUCH_FILE, "Failure");
            } else {
                TestSFTPTransfer.logger.info("Created the dir successfully for the 2nd time");
            }
            return true;
        }).when(channel).mkdir(ArgumentMatchers.eq("/dir1/dir2/dir3"));
        final SFTPTransfer sftpTransfer = createSftpTransfer(processContext, channel);
        final MockFlowFile flowFile = new MockFlowFile(0);
        final File remoteDir = new File("/dir1/dir2/dir3");
        sftpTransfer.ensureDirectoryExists(flowFile, remoteDir);
        // stat should not be called.
        Mockito.verify(channel, Mockito.times(0)).stat(ArgumentMatchers.eq("/dir1/dir2/dir3"));
        // dir3 was created blindly, but failed for the 1st time, and succeeded for the 2nd time.
        Mockito.verify(channel, Mockito.times(2)).mkdir(ArgumentMatchers.eq("/dir1/dir2/dir3"));
        Mockito.verify(channel).mkdir(ArgumentMatchers.eq("/dir1/dir2"));// dir2 was created successfully.

    }

    @Test
    public void testEnsureDirectoryExistsBlindlyAlreadyExisted() throws SftpException, IOException {
        final ProcessContext processContext = Mockito.mock(ProcessContext.class);
        Mockito.when(processContext.getProperty(DISABLE_DIRECTORY_LISTING)).thenReturn(new MockPropertyValue("true"));
        final ChannelSftp channel = Mockito.mock(ChannelSftp.class);
        // If the dir existed, a failure exception is thrown, but should be swallowed.
        Mockito.doThrow(new SftpException(ChannelSftp.SSH_FX_FAILURE, "Failure")).when(channel).mkdir(ArgumentMatchers.eq("/dir1/dir2/dir3"));
        final SFTPTransfer sftpTransfer = createSftpTransfer(processContext, channel);
        final MockFlowFile flowFile = new MockFlowFile(0);
        final File remoteDir = new File("/dir1/dir2/dir3");
        sftpTransfer.ensureDirectoryExists(flowFile, remoteDir);
        // stat should not be called.
        Mockito.verify(channel, Mockito.times(0)).stat(ArgumentMatchers.eq("/dir1/dir2/dir3"));
        Mockito.verify(channel).mkdir(ArgumentMatchers.eq("/dir1/dir2/dir3"));// dir3 was created blindly.

    }

    @Test
    public void testEnsureDirectoryExistsBlindlyFailed() throws SftpException, IOException {
        final ProcessContext processContext = Mockito.mock(ProcessContext.class);
        Mockito.when(processContext.getProperty(DISABLE_DIRECTORY_LISTING)).thenReturn(new MockPropertyValue("true"));
        final ChannelSftp channel = Mockito.mock(ChannelSftp.class);
        Mockito.doThrow(new SftpException(ChannelSftp.SSH_FX_PERMISSION_DENIED, "Permission denied")).when(channel).mkdir(ArgumentMatchers.eq("/dir1/dir2/dir3"));
        final SFTPTransfer sftpTransfer = createSftpTransfer(processContext, channel);
        final MockFlowFile flowFile = new MockFlowFile(0);
        final File remoteDir = new File("/dir1/dir2/dir3");
        try {
            sftpTransfer.ensureDirectoryExists(flowFile, remoteDir);
            Assert.fail("Should fail");
        } catch (IOException e) {
            Assert.assertEquals("Could not blindly create remote directory due to Permission denied", e.getMessage());
        }
        // stat should not be called.
        Mockito.verify(channel, Mockito.times(0)).stat(ArgumentMatchers.eq("/dir1/dir2/dir3"));
        Mockito.verify(channel).mkdir(ArgumentMatchers.eq("/dir1/dir2/dir3"));// dir3 was created blindly.

    }
}

