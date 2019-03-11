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
package alluxio.master.job;


import Status.CANCELED;
import Status.COMPLETED;
import Status.FAILED;
import Status.RUNNING;
import alluxio.grpc.JobCommand;
import alluxio.job.JobConfig;
import alluxio.job.JobDefinition;
import alluxio.job.JobDefinitionRegistry;
import alluxio.master.job.command.CommandManager;
import alluxio.underfs.UfsManager;
import alluxio.wire.WorkerInfo;
import java.io.Serializable;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 * Tests {@link JobCoordinator}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ JobDefinitionRegistry.class })
public final class JobCoordinatorTest {
    private WorkerInfo mWorkerInfo;

    private long mJobId;

    private JobConfig mJobconfig;

    private CommandManager mCommandManager;

    private List<WorkerInfo> mWorkerInfoList;

    private JobDefinition<JobConfig, Serializable, Serializable> mJobDefinition;

    private UfsManager mUfsManager;

    @Test
    public void createJobCoordinator() throws Exception {
        mockSelectExecutors(mWorkerInfo);
        JobCoordinator.create(mCommandManager, mUfsManager, mWorkerInfoList, mJobId, mJobconfig, null);
        List<JobCommand> commands = mCommandManager.pollAllPendingCommands(mWorkerInfo.getId());
        Assert.assertEquals(1, commands.size());
        Assert.assertEquals(mJobId, commands.get(0).getRunTaskCommand().getJobId());
        Assert.assertEquals(0, commands.get(0).getRunTaskCommand().getTaskId());
    }

    @Test
    public void updateStatusFailure() throws Exception {
        mockSelectExecutors(mWorkerInfo);
        JobCoordinator jobCoordinator = JobCoordinator.create(mCommandManager, mUfsManager, mWorkerInfoList, mJobId, mJobconfig, null);
        setTasksWithStatuses(jobCoordinator, RUNNING, FAILED, COMPLETED);
        Assert.assertEquals(FAILED, jobCoordinator.getJobInfoWire().getStatus());
        Assert.assertTrue(jobCoordinator.getJobInfoWire().getErrorMessage().contains("Task execution failed"));
    }

    @Test
    public void updateStatusFailureOverCancel() throws Exception {
        mockSelectExecutors(mWorkerInfo);
        JobCoordinator jobCoordinator = JobCoordinator.create(mCommandManager, mUfsManager, mWorkerInfoList, mJobId, mJobconfig, null);
        setTasksWithStatuses(jobCoordinator, RUNNING, FAILED, COMPLETED);
        Assert.assertEquals(FAILED, jobCoordinator.getJobInfoWire().getStatus());
    }

    @Test
    public void updateStatusCancel() throws Exception {
        mockSelectExecutors(mWorkerInfo);
        JobCoordinator jobCoordinator = JobCoordinator.create(mCommandManager, mUfsManager, mWorkerInfoList, mJobId, mJobconfig, null);
        setTasksWithStatuses(jobCoordinator, CANCELED, RUNNING, COMPLETED);
        Assert.assertEquals(CANCELED, jobCoordinator.getJobInfoWire().getStatus());
    }

    @Test
    public void updateStatusRunning() throws Exception {
        mockSelectExecutors(mWorkerInfo);
        JobCoordinator jobCoordinator = JobCoordinator.create(mCommandManager, mUfsManager, mWorkerInfoList, mJobId, mJobconfig, null);
        setTasksWithStatuses(jobCoordinator, COMPLETED, RUNNING, COMPLETED);
        Assert.assertEquals(RUNNING, jobCoordinator.getJobInfoWire().getStatus());
    }

    @Test
    public void updateStatusCompleted() throws Exception {
        mockSelectExecutors(mWorkerInfo);
        JobCoordinator jobCoordinator = JobCoordinator.create(mCommandManager, mUfsManager, mWorkerInfoList, mJobId, mJobconfig, null);
        setTasksWithStatuses(jobCoordinator, COMPLETED, COMPLETED, COMPLETED);
        Assert.assertEquals(COMPLETED, jobCoordinator.getJobInfoWire().getStatus());
        Mockito.verify(mJobDefinition).join(Mockito.eq(jobCoordinator.getJobInfoWire().getJobConfig()), Mockito.anyMapOf(WorkerInfo.class, Serializable.class));
    }

    @Test
    public void updateStatusJoinFailure() throws Exception {
        mockSelectExecutors(mWorkerInfo);
        Mockito.when(mJobDefinition.join(Mockito.eq(mJobconfig), Mockito.anyMapOf(WorkerInfo.class, Serializable.class))).thenThrow(new UnsupportedOperationException("test exception"));
        JobCoordinator jobCoordinator = JobCoordinator.create(mCommandManager, mUfsManager, mWorkerInfoList, mJobId, mJobconfig, null);
        setTasksWithStatuses(jobCoordinator, COMPLETED, COMPLETED, COMPLETED);
        Assert.assertEquals(FAILED, jobCoordinator.getJobInfoWire().getStatus());
        Assert.assertEquals("test exception", jobCoordinator.getJobInfoWire().getErrorMessage());
    }

    @Test
    public void noTasks() throws Exception {
        mockSelectExecutors();
        JobCoordinator jobCoordinator = JobCoordinator.create(mCommandManager, mUfsManager, mWorkerInfoList, mJobId, mJobconfig, null);
        Assert.assertEquals(COMPLETED, jobCoordinator.getJobInfoWire().getStatus());
    }

    @Test
    public void failWorker() throws Exception {
        mockSelectExecutors(mWorkerInfo);
        JobCoordinator jobCoordinator = JobCoordinator.create(mCommandManager, mUfsManager, mWorkerInfoList, mJobId, mJobconfig, null);
        jobCoordinator.failTasksForWorker(mWorkerInfo.getId());
        Assert.assertEquals(FAILED, jobCoordinator.getJobInfoWire().getStatus());
    }
}

