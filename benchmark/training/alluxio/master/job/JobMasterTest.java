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


import ExceptionMessage.JOB_DEFINITION_DOES_NOT_EXIST;
import ExceptionMessage.JOB_DOES_NOT_EXIST;
import ExceptionMessage.JOB_MASTER_FULL_CAPACITY;
import PropertyKey.JOB_MASTER_JOB_CAPACITY;
import alluxio.conf.ServerConfiguration;
import alluxio.exception.JobDoesNotExistException;
import alluxio.exception.status.ResourceExhaustedException;
import alluxio.job.JobConfig;
import alluxio.job.TestJobConfig;
import alluxio.master.job.command.CommandManager;
import alluxio.underfs.UfsManager;
import com.google.common.collect.Maps;
import java.util.Map;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 * Tests {@link JobMaster}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ JobCoordinator.class })
public final class JobMasterTest {
    private static final int TEST_JOB_MASTER_JOB_CAPACITY = 100;

    private JobMaster mJobMaster;

    @Rule
    public TemporaryFolder mTestFolder = new TemporaryFolder();

    @Test
    public void runNonExistingJobConfig() throws Exception {
        try {
            mJobMaster.run(new JobMasterTest.DummyJobConfig());
            Assert.fail("cannot run non-existing job");
        } catch (JobDoesNotExistException e) {
            Assert.assertEquals(JOB_DEFINITION_DOES_NOT_EXIST.getMessage("dummy"), e.getMessage());
        }
    }

    @Test
    public void run() throws Exception {
        JobCoordinator coordinator = PowerMockito.mock(JobCoordinator.class);
        PowerMockito.mockStatic(JobCoordinator.class);
        Mockito.when(JobCoordinator.create(Mockito.any(CommandManager.class), Mockito.any(UfsManager.class), Mockito.anyList(), Mockito.anyLong(), Mockito.any(JobConfig.class), Mockito.any(null))).thenReturn(coordinator);
        TestJobConfig jobConfig = new TestJobConfig("/test");
        for (long i = 0; i < (JobMasterTest.TEST_JOB_MASTER_JOB_CAPACITY); i++) {
            mJobMaster.run(jobConfig);
        }
        Assert.assertEquals(JobMasterTest.TEST_JOB_MASTER_JOB_CAPACITY, mJobMaster.list().size());
    }

    @Test
    public void flowControl() throws Exception {
        JobCoordinator coordinator = PowerMockito.mock(JobCoordinator.class);
        PowerMockito.mockStatic(JobCoordinator.class);
        Mockito.when(JobCoordinator.create(Mockito.any(CommandManager.class), Mockito.any(UfsManager.class), Mockito.anyList(), Mockito.anyLong(), Mockito.any(JobConfig.class), Mockito.any(null))).thenReturn(coordinator);
        TestJobConfig jobConfig = new TestJobConfig("/test");
        for (long i = 0; i < (JobMasterTest.TEST_JOB_MASTER_JOB_CAPACITY); i++) {
            mJobMaster.run(jobConfig);
        }
        try {
            mJobMaster.run(jobConfig);
            Assert.fail("should not be able to run more jobs than job master capacity");
        } catch (ResourceExhaustedException e) {
            Assert.assertEquals(JOB_MASTER_FULL_CAPACITY.getMessage(ServerConfiguration.get(JOB_MASTER_JOB_CAPACITY)), e.getMessage());
        }
    }

    @Test
    public void cancelNonExistingJob() {
        try {
            mJobMaster.cancel(1);
            Assert.fail("cannot cancel non-existing job");
        } catch (JobDoesNotExistException e) {
            Assert.assertEquals(JOB_DOES_NOT_EXIST.getMessage(1), e.getMessage());
        }
    }

    @Test
    public void cancel() throws Exception {
        JobCoordinator coordinator = Mockito.mock(JobCoordinator.class);
        Map<Long, JobCoordinator> map = Maps.newHashMap();
        long jobId = 1L;
        map.put(jobId, coordinator);
        Whitebox.setInternalState(mJobMaster, "mIdToJobCoordinator", map);
        mJobMaster.cancel(jobId);
        Mockito.verify(coordinator).cancel();
    }

    private static class DummyJobConfig implements JobConfig {
        private static final long serialVersionUID = 1L;

        @Override
        public String getName() {
            return "dummy";
        }
    }
}

