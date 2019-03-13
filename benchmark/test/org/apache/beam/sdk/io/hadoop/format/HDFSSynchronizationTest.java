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
package org.apache.beam.sdk.io.hadoop.format;


import java.io.File;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hdfs.protocol.AlreadyBeingCreatedException;
import org.apache.hadoop.ipc.RemoteException;
import org.apache.hadoop.mapreduce.TaskAttemptID;
import org.apache.hadoop.mapreduce.TaskID;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Tests functionality of {@link HDFSSynchronization} class.
 */
public class HDFSSynchronizationTest {
    public static final String DEFAULT_JOB_ID = String.valueOf(1);

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    private HDFSSynchronization tested;

    private Configuration configuration;

    /**
     * Tests that job lock will be acquired only once until it is again released.
     */
    @Test
    public void tryAcquireJobLockTest() {
        boolean firstAttempt = tested.tryAcquireJobLock(configuration);
        boolean secondAttempt = tested.tryAcquireJobLock(configuration);
        boolean thirdAttempt = tested.tryAcquireJobLock(configuration);
        Assert.assertTrue(isFileExists(getJobLockPath()));
        tested.releaseJobIdLock(configuration);
        boolean fourthAttempt = tested.tryAcquireJobLock(configuration);
        boolean fifthAttempt = tested.tryAcquireJobLock(configuration);
        Assert.assertTrue(firstAttempt);
        Assert.assertFalse(secondAttempt);
        Assert.assertFalse(thirdAttempt);
        Assert.assertTrue(fourthAttempt);
        Assert.assertFalse(fifthAttempt);
    }

    /**
     * Missing job id in configuration will throw exception.
     */
    @Test(expected = NullPointerException.class)
    public void testMissingJobId() {
        Configuration conf = new Configuration();
        tested.tryAcquireJobLock(conf);
    }

    /**
     * Multiple attempts to release job will not throw exception.
     */
    @Test
    public void testMultipleTaskDeletion() {
        String jobFolder = getFileInJobFolder("");
        tested.tryAcquireJobLock(configuration);
        Assert.assertTrue(isFileExists(getJobLockPath()));
        tested.releaseJobIdLock(configuration);
        Assert.assertFalse(isFileExists(getJobLockPath()));
        Assert.assertFalse(isFolderExists(jobFolder));
        // any exception will not be thrown
        tested.releaseJobIdLock(configuration);
    }

    @Test
    public void testTaskIdLockAcquire() {
        int tasksCount = 100;
        for (int i = 0; i < tasksCount; i++) {
            TaskID taskID = tested.acquireTaskIdLock(configuration);
            Assert.assertTrue(isFileExists(getTaskIdPath(taskID)));
        }
        String jobFolderName = getFileInJobFolder("");
        File jobFolder = new File(jobFolderName);
        Assert.assertTrue(jobFolder.isDirectory());
        // we have to multiply by 2 because crc files exists
        Assert.assertEquals((tasksCount * 2), jobFolder.list().length);
    }

    @Test
    public void testTaskAttemptIdAcquire() {
        int tasksCount = 100;
        int taskId = 25;
        for (int i = 0; i < tasksCount; i++) {
            TaskAttemptID taskAttemptID = tested.acquireTaskAttemptIdLock(configuration, taskId);
            Assert.assertTrue(isFileExists(getTaskAttemptIdPath(taskId, taskAttemptID.getId())));
        }
    }

    @Test
    public void testCatchingRemoteException() throws IOException {
        FileSystem mockedFileSystem = Mockito.mock(FileSystem.class);
        RemoteException thrownException = new RemoteException(AlreadyBeingCreatedException.class.getName(), "Failed to CREATE_FILE");
        Mockito.when(mockedFileSystem.createNewFile(Mockito.any())).thenThrow(thrownException);
        HDFSSynchronization synchronization = new HDFSSynchronization("someDir", ( conf) -> mockedFileSystem);
        Assert.assertFalse(synchronization.tryAcquireJobLock(configuration));
    }
}

