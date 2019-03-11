/**
 * Copyright 1999-2015 dangdang.com.
 * <p>
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
 * </p>
 */
package io.elasticjob.lite.executor.type;


import State.TASK_FINISHED;
import State.TASK_RUNNING;
import State.TASK_STAGING;
import io.elasticjob.lite.exception.JobExecutionEnvironmentException;
import io.elasticjob.lite.exception.JobSystemException;
import io.elasticjob.lite.executor.AbstractElasticJobExecutor;
import io.elasticjob.lite.executor.JobFacade;
import io.elasticjob.lite.executor.ShardingContexts;
import io.elasticjob.lite.executor.handler.impl.DefaultExecutorServiceHandler;
import io.elasticjob.lite.executor.handler.impl.DefaultJobExceptionHandler;
import io.elasticjob.lite.fixture.ShardingContextsBuilder;
import io.elasticjob.lite.fixture.config.TestSimpleJobConfiguration;
import io.elasticjob.lite.fixture.job.JobCaller;
import io.elasticjob.lite.fixture.job.TestSimpleJob;
import java.util.Collections;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.unitils.util.ReflectionUtils;


@RunWith(MockitoJUnitRunner.class)
public final class SimpleJobExecutorTest {
    @Mock
    private JobCaller jobCaller;

    @Mock
    private JobFacade jobFacade;

    private SimpleJobExecutor simpleJobExecutor;

    @Test
    public void assertNewExecutorWithDefaultHandlers() throws NoSuchFieldException {
        Mockito.when(jobFacade.loadJobRootConfiguration(true)).thenReturn(new TestSimpleJobConfiguration("ErrorHandler", Object.class.getName()));
        SimpleJobExecutor simpleJobExecutor = new SimpleJobExecutor(new TestSimpleJob(jobCaller), jobFacade);
        Assert.assertThat(ReflectionUtils.getFieldValue(simpleJobExecutor, AbstractElasticJobExecutor.class.getDeclaredField("executorService")), CoreMatchers.instanceOf(new DefaultExecutorServiceHandler().createExecutorService("test_job").getClass()));
        Assert.assertThat(ReflectionUtils.getFieldValue(simpleJobExecutor, AbstractElasticJobExecutor.class.getDeclaredField("jobExceptionHandler")), CoreMatchers.instanceOf(DefaultJobExceptionHandler.class));
    }

    @Test(expected = JobSystemException.class)
    public void assertExecuteWhenCheckMaxTimeDiffSecondsIntolerable() throws JobExecutionEnvironmentException {
        Mockito.doThrow(JobExecutionEnvironmentException.class).when(jobFacade).checkJobExecutionEnvironment();
        try {
            simpleJobExecutor.execute();
        } finally {
            Mockito.verify(jobFacade).checkJobExecutionEnvironment();
            Mockito.verify(jobCaller, Mockito.times(0)).execute();
        }
    }

    @Test
    public void assertExecuteWhenPreviousJobStillRunning() throws JobExecutionEnvironmentException {
        ShardingContexts shardingContexts = new ShardingContexts("fake_task_id", "test_job", 10, "", Collections.<Integer, String>emptyMap());
        Mockito.when(jobFacade.getShardingContexts()).thenReturn(shardingContexts);
        Mockito.when(jobFacade.misfireIfRunning(shardingContexts.getShardingItemParameters().keySet())).thenReturn(true);
        simpleJobExecutor.execute();
        Mockito.verify(jobFacade).postJobStatusTraceEvent(shardingContexts.getTaskId(), TASK_STAGING, "Job 'test_job' execute begin.");
        Mockito.verify(jobFacade).postJobStatusTraceEvent(shardingContexts.getTaskId(), TASK_FINISHED, "Previous job 'test_job' - shardingItems '[]' is still running, misfired job will start after previous job completed.");
        Mockito.verify(jobFacade).checkJobExecutionEnvironment();
        Mockito.verify(jobFacade).getShardingContexts();
        Mockito.verify(jobFacade).misfireIfRunning(shardingContexts.getShardingItemParameters().keySet());
        Mockito.verify(jobCaller, Mockito.times(0)).execute();
    }

    @Test
    public void assertExecuteWhenShardingItemsIsEmpty() throws JobExecutionEnvironmentException {
        ShardingContexts shardingContexts = new ShardingContexts("fake_task_id", "test_job", 10, "", Collections.<Integer, String>emptyMap());
        ElasticJobVerify.prepareForIsNotMisfire(jobFacade, shardingContexts);
        simpleJobExecutor.execute();
        Mockito.verify(jobFacade).postJobStatusTraceEvent(shardingContexts.getTaskId(), TASK_STAGING, "Job 'test_job' execute begin.");
        Mockito.verify(jobFacade).postJobStatusTraceEvent(shardingContexts.getTaskId(), TASK_FINISHED, "Sharding item for job 'test_job' is empty.");
        Mockito.verify(jobFacade).checkJobExecutionEnvironment();
        Mockito.verify(jobFacade).getShardingContexts();
        Mockito.verify(jobFacade).misfireIfRunning(shardingContexts.getShardingItemParameters().keySet());
        Mockito.verify(jobCaller, Mockito.times(0)).execute();
    }

    @Test(expected = JobSystemException.class)
    public void assertExecuteWhenRunOnceAndThrowExceptionForSingleShardingItem() throws JobExecutionEnvironmentException {
        assertExecuteWhenRunOnceAndThrowException(ShardingContextsBuilder.getSingleShardingContexts());
    }

    @Test
    public void assertExecuteWhenRunOnceAndThrowExceptionForMultipleShardingItems() throws JobExecutionEnvironmentException {
        assertExecuteWhenRunOnceAndThrowException(ShardingContextsBuilder.getMultipleShardingContexts());
    }

    @Test
    public void assertExecuteWhenRunOnceSuccessForSingleShardingItems() {
        assertExecuteWhenRunOnceSuccess(ShardingContextsBuilder.getSingleShardingContexts());
    }

    @Test
    public void assertExecuteWhenRunOnceSuccessForMultipleShardingItems() {
        assertExecuteWhenRunOnceSuccess(ShardingContextsBuilder.getMultipleShardingContexts());
    }

    @Test
    public void assertExecuteWhenRunOnceWithMisfireIsEmpty() {
        ShardingContexts shardingContexts = ShardingContextsBuilder.getMultipleShardingContexts();
        Mockito.when(jobFacade.getShardingContexts()).thenReturn(shardingContexts);
        Mockito.when(jobFacade.isExecuteMisfired(shardingContexts.getShardingItemParameters().keySet())).thenReturn(false);
        simpleJobExecutor.execute();
        ElasticJobVerify.verifyForIsNotMisfire(jobFacade, shardingContexts);
        Mockito.verify(jobCaller, Mockito.times(2)).execute();
    }

    @Test
    public void assertExecuteWhenRunOnceWithMisfireIsNotEmptyButIsNotEligibleForJobRunning() {
        ShardingContexts shardingContexts = ShardingContextsBuilder.getMultipleShardingContexts();
        Mockito.when(jobFacade.getShardingContexts()).thenReturn(shardingContexts);
        Mockito.when(jobFacade.isExecuteMisfired(shardingContexts.getShardingItemParameters().keySet())).thenReturn(false);
        simpleJobExecutor.execute();
        ElasticJobVerify.verifyForIsNotMisfire(jobFacade, shardingContexts);
        Mockito.verify(jobCaller, Mockito.times(2)).execute();
        Mockito.verify(jobFacade, Mockito.times(0)).clearMisfire(shardingContexts.getShardingItemParameters().keySet());
    }

    @Test
    public void assertExecuteWhenRunOnceWithMisfire() throws JobExecutionEnvironmentException {
        ShardingContexts shardingContexts = ShardingContextsBuilder.getMultipleShardingContexts();
        Mockito.when(jobFacade.getShardingContexts()).thenReturn(shardingContexts);
        Mockito.when(jobFacade.misfireIfRunning(shardingContexts.getShardingItemParameters().keySet())).thenReturn(false);
        Mockito.when(jobFacade.isExecuteMisfired(shardingContexts.getShardingItemParameters().keySet())).thenReturn(true, false);
        simpleJobExecutor.execute();
        Mockito.verify(jobFacade).postJobStatusTraceEvent(shardingContexts.getTaskId(), TASK_STAGING, "Job 'test_job' execute begin.");
        Mockito.verify(jobFacade, Mockito.times(2)).postJobStatusTraceEvent(shardingContexts.getTaskId(), TASK_RUNNING, "");
        Mockito.verify(jobFacade).checkJobExecutionEnvironment();
        Mockito.verify(jobFacade).getShardingContexts();
        Mockito.verify(jobFacade).misfireIfRunning(shardingContexts.getShardingItemParameters().keySet());
        Mockito.verify(jobFacade, Mockito.times(2)).registerJobBegin(shardingContexts);
        Mockito.verify(jobCaller, Mockito.times(4)).execute();
        Mockito.verify(jobFacade, Mockito.times(2)).registerJobCompleted(shardingContexts);
    }

    @Test(expected = JobSystemException.class)
    public void assertBeforeJobExecutedFailure() {
        ShardingContexts shardingContexts = ShardingContextsBuilder.getMultipleShardingContexts();
        Mockito.when(jobFacade.getShardingContexts()).thenReturn(shardingContexts);
        Mockito.when(jobFacade.misfireIfRunning(shardingContexts.getShardingItemParameters().keySet())).thenReturn(false);
        Mockito.doThrow(RuntimeException.class).when(jobFacade).beforeJobExecuted(shardingContexts);
        try {
            simpleJobExecutor.execute();
        } finally {
            Mockito.verify(jobCaller, Mockito.times(0)).execute();
        }
    }

    @Test(expected = JobSystemException.class)
    public void assertAfterJobExecutedFailure() {
        ShardingContexts shardingContexts = ShardingContextsBuilder.getMultipleShardingContexts();
        Mockito.when(jobFacade.getShardingContexts()).thenReturn(shardingContexts);
        Mockito.when(jobFacade.misfireIfRunning(shardingContexts.getShardingItemParameters().keySet())).thenReturn(false);
        Mockito.when(jobFacade.isExecuteMisfired(shardingContexts.getShardingItemParameters().keySet())).thenReturn(false);
        Mockito.doThrow(RuntimeException.class).when(jobFacade).afterJobExecuted(shardingContexts);
        try {
            simpleJobExecutor.execute();
        } finally {
            Mockito.verify(jobCaller, Mockito.times(2)).execute();
        }
    }
}

