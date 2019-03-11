/**
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */
package alluxio.master.file;


import HeartbeatContext.MASTER_PERSISTENCE_CHECKER;
import HeartbeatContext.MASTER_PERSISTENCE_SCHEDULER;
import JobMasterClient.Factory;
import PersistenceState.NOT_PERSISTED;
import Status.CANCELED;
import Status.COMPLETED;
import Status.CREATED;
import Status.FAILED;
import Status.RUNNING;
import alluxio.AlluxioURI;
import alluxio.client.job.JobMasterClient;
import alluxio.conf.ServerConfiguration;
import alluxio.grpc.DeletePOptions;
import alluxio.heartbeat.HeartbeatContext;
import alluxio.heartbeat.HeartbeatScheduler;
import alluxio.heartbeat.ManuallyScheduleHeartbeat;
import alluxio.job.JobConfig;
import alluxio.job.wire.JobInfo;
import alluxio.master.MasterRegistry;
import alluxio.master.SafeModeManager;
import alluxio.master.file.contexts.CompleteFileContext;
import alluxio.master.file.contexts.CreateDirectoryContext;
import alluxio.master.file.contexts.CreateFileContext;
import alluxio.master.file.contexts.DeleteContext;
import alluxio.master.file.contexts.GetStatusContext;
import alluxio.master.file.contexts.RenameContext;
import alluxio.security.LoginUser;
import alluxio.security.authentication.AuthenticatedClientUser;
import alluxio.underfs.UnderFileSystem;
import alluxio.util.CommonUtils;
import alluxio.util.UnderFileSystemUtils;
import alluxio.wire.FileInfo;
import java.io.File;
import java.util.Map;
import java.util.Random;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest(Factory.class)
public final class PersistenceTest {
    private File mJournalFolder;

    private MasterRegistry mRegistry;

    private FileSystemMaster mFileSystemMaster;

    private JobMasterClient mMockJobMasterClient;

    private SafeModeManager mSafeModeManager;

    private long mStartTimeMs;

    private int mPort;

    private static final GetStatusContext GET_STATUS_CONTEXT = GetStatusContext.defaults();

    @Rule
    public ManuallyScheduleHeartbeat mManualScheduler = new ManuallyScheduleHeartbeat(HeartbeatContext.MASTER_PERSISTENCE_CHECKER, HeartbeatContext.MASTER_PERSISTENCE_SCHEDULER);

    @Test
    public void empty() throws Exception {
        checkEmpty();
    }

    @Test
    public void heartbeatEmpty() throws Exception {
        HeartbeatScheduler.execute(MASTER_PERSISTENCE_SCHEDULER);
        checkEmpty();
        HeartbeatScheduler.execute(MASTER_PERSISTENCE_SCHEDULER);
        checkEmpty();
        HeartbeatScheduler.execute(MASTER_PERSISTENCE_CHECKER);
        checkEmpty();
        HeartbeatScheduler.execute(MASTER_PERSISTENCE_CHECKER);
        checkEmpty();
    }

    /**
     * Tests the progression of a successful persist job.
     */
    @Test
    public void successfulAsyncPersistence() throws Exception {
        // Create a file and check the internal state.
        AlluxioURI testFile = createTestFile();
        FileInfo fileInfo = mFileSystemMaster.getFileInfo(testFile, PersistenceTest.GET_STATUS_CONTEXT);
        Assert.assertEquals(NOT_PERSISTED.toString(), fileInfo.getPersistenceState());
        // schedule the async persistence, checking the internal state.
        mFileSystemMaster.scheduleAsyncPersistence(testFile);
        checkPersistenceRequested(testFile);
        // Mock the job service interaction.
        Random random = new Random();
        long jobId = random.nextLong();
        Mockito.when(mMockJobMasterClient.run(ArgumentMatchers.any(JobConfig.class))).thenReturn(jobId);
        // Repeatedly execute the persistence checker heartbeat, checking the internal state.
        {
            HeartbeatScheduler.execute(MASTER_PERSISTENCE_SCHEDULER);
            checkPersistenceInProgress(testFile, jobId);
            HeartbeatScheduler.execute(MASTER_PERSISTENCE_SCHEDULER);
            checkPersistenceInProgress(testFile, jobId);
        }
        // Mock the job service interaction.
        JobInfo jobInfo = new JobInfo();
        jobInfo.setStatus(CREATED);
        Mockito.when(mMockJobMasterClient.getStatus(Mockito.anyLong())).thenReturn(jobInfo);
        // Repeatedly execute the persistence checker heartbeat, checking the internal state.
        {
            HeartbeatScheduler.execute(MASTER_PERSISTENCE_CHECKER);
            checkPersistenceInProgress(testFile, jobId);
            HeartbeatScheduler.execute(MASTER_PERSISTENCE_CHECKER);
            checkPersistenceInProgress(testFile, jobId);
        }
        // Mock the job service interaction.
        jobInfo.setStatus(RUNNING);
        Mockito.when(mMockJobMasterClient.getStatus(Mockito.anyLong())).thenReturn(jobInfo);
        // Repeatedly execute the persistence checker heartbeat, checking the internal state.
        {
            HeartbeatScheduler.execute(MASTER_PERSISTENCE_CHECKER);
            checkPersistenceInProgress(testFile, jobId);
            HeartbeatScheduler.execute(MASTER_PERSISTENCE_CHECKER);
            checkPersistenceInProgress(testFile, jobId);
        }
        // Mock the job service interaction.
        jobInfo.setStatus(COMPLETED);
        Mockito.when(mMockJobMasterClient.getStatus(Mockito.anyLong())).thenReturn(jobInfo);
        {
            // Create the temporary UFS file.
            fileInfo = mFileSystemMaster.getFileInfo(testFile, PersistenceTest.GET_STATUS_CONTEXT);
            Map<Long, PersistJob> persistJobs = getPersistJobs();
            PersistJob job = persistJobs.get(fileInfo.getFileId());
            UnderFileSystem ufs = UnderFileSystem.Factory.create(job.getTempUfsPath(), ServerConfiguration.global());
            UnderFileSystemUtils.touch(ufs, job.getTempUfsPath());
        }
        // Repeatedly execute the persistence checker heartbeat, checking the internal state.
        {
            HeartbeatScheduler.execute(MASTER_PERSISTENCE_CHECKER);
            waitUntilPersisted(testFile);
            HeartbeatScheduler.execute(MASTER_PERSISTENCE_CHECKER);
            waitUntilPersisted(testFile);
        }
    }

    /**
     * Tests that a canceled persist job is not retried.
     */
    @Test
    public void noRetryCanceled() throws Exception {
        // Create a file and check the internal state.
        AlluxioURI testFile = createTestFile();
        FileInfo fileInfo = mFileSystemMaster.getFileInfo(testFile, PersistenceTest.GET_STATUS_CONTEXT);
        Assert.assertEquals(NOT_PERSISTED.toString(), fileInfo.getPersistenceState());
        // schedule the async persistence, checking the internal state.
        mFileSystemMaster.scheduleAsyncPersistence(testFile);
        checkPersistenceRequested(testFile);
        // Mock the job service interaction.
        Random random = new Random();
        long jobId = random.nextLong();
        Mockito.when(mMockJobMasterClient.run(ArgumentMatchers.any(JobConfig.class))).thenReturn(jobId);
        // Repeatedly execute the persistence checker heartbeat, checking the internal state.
        {
            HeartbeatScheduler.execute(MASTER_PERSISTENCE_SCHEDULER);
            checkPersistenceInProgress(testFile, jobId);
            HeartbeatScheduler.execute(MASTER_PERSISTENCE_SCHEDULER);
            checkPersistenceInProgress(testFile, jobId);
        }
        // Mock the job service interaction.
        JobInfo jobInfo = new JobInfo();
        jobInfo.setStatus(CANCELED);
        Mockito.when(mMockJobMasterClient.getStatus(Mockito.anyLong())).thenReturn(jobInfo);
        // Execute the persistence checker heartbeat, checking the internal state.
        {
            HeartbeatScheduler.execute(MASTER_PERSISTENCE_CHECKER);
            checkEmpty();
        }
    }

    /**
     * Tests that a failed persist job is retried multiple times.
     */
    @Test
    public void retryFailed() throws Exception {
        // Create a file and check the internal state.
        AlluxioURI testFile = createTestFile();
        FileInfo fileInfo = mFileSystemMaster.getFileInfo(testFile, PersistenceTest.GET_STATUS_CONTEXT);
        Assert.assertEquals(NOT_PERSISTED.toString(), fileInfo.getPersistenceState());
        // schedule the async persistence, checking the internal state.
        mFileSystemMaster.scheduleAsyncPersistence(testFile);
        checkPersistenceRequested(testFile);
        // Mock the job service interaction.
        Random random = new Random();
        long jobId = random.nextLong();
        Mockito.when(mMockJobMasterClient.run(ArgumentMatchers.any(JobConfig.class))).thenReturn(jobId);
        // Repeatedly execute the persistence checker heartbeat, checking the internal state.
        {
            HeartbeatScheduler.execute(MASTER_PERSISTENCE_SCHEDULER);
            checkPersistenceInProgress(testFile, jobId);
            HeartbeatScheduler.execute(MASTER_PERSISTENCE_SCHEDULER);
            checkPersistenceInProgress(testFile, jobId);
        }
        // Mock the job service interaction.
        JobInfo jobInfo = new JobInfo();
        jobInfo.setStatus(FAILED);
        Mockito.when(mMockJobMasterClient.getStatus(Mockito.anyLong())).thenReturn(jobInfo);
        // Repeatedly execute the persistence checker and scheduler heartbeats, checking the internal
        // state. After the internal timeout associated with the operation expires, check the operation
        // has been cancelled.
        while (true) {
            HeartbeatScheduler.execute(MASTER_PERSISTENCE_CHECKER);
            checkPersistenceRequested(testFile);
            HeartbeatScheduler.execute(MASTER_PERSISTENCE_SCHEDULER);
            if ((getPersistJobs().size()) != 0) {
                checkPersistenceInProgress(testFile, jobId);
            } else {
                checkEmpty();
                break;
            }
            CommonUtils.sleepMs(100);
        } 
        fileInfo = mFileSystemMaster.getFileInfo(testFile, PersistenceTest.GET_STATUS_CONTEXT);
        Assert.assertEquals(NOT_PERSISTED.toString(), fileInfo.getPersistenceState());
    }

    /**
     * Tests that a persist file job is retried after the file is renamed and the src directory is
     * deleted.
     */
    @Test(timeout = 20000)
    public void retryPersistJobRenameDelete() throws Exception {
        AuthenticatedClientUser.set(LoginUser.get(ServerConfiguration.global()).getName());
        // Create src file and directory, checking the internal state.
        AlluxioURI alluxioDirSrc = new AlluxioURI("/src");
        mFileSystemMaster.createDirectory(alluxioDirSrc, CreateDirectoryContext.defaults().setPersisted(true));
        AlluxioURI alluxioFileSrc = new AlluxioURI("/src/in_alluxio");
        FileInfo info = mFileSystemMaster.createFile(alluxioFileSrc, CreateFileContext.defaults().setPersisted(false));
        Assert.assertEquals(NOT_PERSISTED.toString(), info.getPersistenceState());
        mFileSystemMaster.completeFile(alluxioFileSrc, CompleteFileContext.defaults());
        // Schedule the async persistence, checking the internal state.
        mFileSystemMaster.scheduleAsyncPersistence(alluxioFileSrc);
        checkPersistenceRequested(alluxioFileSrc);
        // Mock the job service interaction.
        Random random = new Random();
        long jobId = random.nextLong();
        Mockito.when(mMockJobMasterClient.run(ArgumentMatchers.any(JobConfig.class))).thenReturn(jobId);
        // Execute the persistence scheduler heartbeat, checking the internal state.
        HeartbeatScheduler.execute(MASTER_PERSISTENCE_SCHEDULER);
        CommonUtils.waitFor("Scheduler heartbeat", () -> (getPersistJobs().size()) > 0);
        checkPersistenceInProgress(alluxioFileSrc, jobId);
        // Mock the job service interaction.
        JobInfo jobInfo = new JobInfo();
        jobInfo.setStatus(CREATED);
        Mockito.when(mMockJobMasterClient.getStatus(Mockito.anyLong())).thenReturn(jobInfo);
        // Execute the persistence checker heartbeat, checking the internal state.
        HeartbeatScheduler.execute(MASTER_PERSISTENCE_CHECKER);
        CommonUtils.waitFor("Checker heartbeat", () -> (getPersistJobs().size()) > 0);
        checkPersistenceInProgress(alluxioFileSrc, jobId);
        // Mock the job service interaction.
        jobInfo.setStatus(COMPLETED);
        Mockito.when(mMockJobMasterClient.getStatus(Mockito.anyLong())).thenReturn(jobInfo);
        // Create the temporary UFS file.
        {
            Map<Long, PersistJob> persistJobs = getPersistJobs();
            PersistJob job = persistJobs.get(info.getFileId());
            UnderFileSystem ufs = UnderFileSystem.Factory.create(job.getTempUfsPath(), ServerConfiguration.global());
            UnderFileSystemUtils.touch(ufs, job.getTempUfsPath());
        }
        // Rename the src file before the persist is commited.
        mFileSystemMaster.createDirectory(new AlluxioURI("/dst"), CreateDirectoryContext.defaults().setPersisted(true));
        AlluxioURI alluxioFileDst = new AlluxioURI("/dst/in_alluxio");
        mFileSystemMaster.rename(alluxioFileSrc, alluxioFileDst, RenameContext.defaults());
        // Delete the src directory recursively.
        mFileSystemMaster.delete(alluxioDirSrc, DeleteContext.mergeFrom(DeletePOptions.newBuilder().setRecursive(true)));
        // Execute the persistence checker heartbeat, checking the internal state. This should
        // re-schedule the persist task as tempUfsPath is deleted.
        HeartbeatScheduler.execute(MASTER_PERSISTENCE_CHECKER);
        CommonUtils.waitFor("Checker heartbeat", () -> (getPersistRequests().size()) > 0);
        checkPersistenceRequested(alluxioFileDst);
        // Mock job service interaction.
        jobId = random.nextLong();
        Mockito.when(mMockJobMasterClient.run(ArgumentMatchers.any(JobConfig.class))).thenReturn(jobId);
        // Execute the persistence scheduler heartbeat, checking the internal state.
        HeartbeatScheduler.execute(MASTER_PERSISTENCE_SCHEDULER);
        CommonUtils.waitFor("Scheduler heartbeat", () -> (getPersistJobs().size()) > 0);
        checkPersistenceInProgress(alluxioFileDst, jobId);
    }

    /**
     * Tests that persist file requests are not forgotten across restarts.
     */
    @Test
    public void replayPersistRequest() throws Exception {
        // Create a file and check the internal state.
        AlluxioURI testFile = createTestFile();
        FileInfo fileInfo = mFileSystemMaster.getFileInfo(testFile, PersistenceTest.GET_STATUS_CONTEXT);
        Assert.assertEquals(NOT_PERSISTED.toString(), fileInfo.getPersistenceState());
        // schedule the async persistence, checking the internal state.
        mFileSystemMaster.scheduleAsyncPersistence(testFile);
        checkPersistenceRequested(testFile);
        // Simulate restart.
        stopServices();
        startServices();
        checkPersistenceRequested(testFile);
    }

    /**
     * Tests that persist file jobs are not forgotten across restarts.
     */
    @Test
    public void replayPersistJob() throws Exception {
        // Create a file and check the internal state.
        AlluxioURI testFile = createTestFile();
        FileInfo fileInfo = mFileSystemMaster.getFileInfo(testFile, PersistenceTest.GET_STATUS_CONTEXT);
        Assert.assertEquals(NOT_PERSISTED.toString(), fileInfo.getPersistenceState());
        // schedule the async persistence, checking the internal state.
        mFileSystemMaster.scheduleAsyncPersistence(testFile);
        checkPersistenceRequested(testFile);
        // Mock the job service interaction.
        Random random = new Random();
        long jobId = random.nextLong();
        Mockito.when(mMockJobMasterClient.run(ArgumentMatchers.any(JobConfig.class))).thenReturn(jobId);
        // Repeatedly execute the persistence checker heartbeat, checking the internal state.
        {
            HeartbeatScheduler.execute(MASTER_PERSISTENCE_SCHEDULER);
            checkPersistenceInProgress(testFile, jobId);
            HeartbeatScheduler.execute(MASTER_PERSISTENCE_SCHEDULER);
            checkPersistenceInProgress(testFile, jobId);
        }
        // Simulate restart.
        stopServices();
        startServices();
        checkPersistenceInProgress(testFile, jobId);
    }
}

