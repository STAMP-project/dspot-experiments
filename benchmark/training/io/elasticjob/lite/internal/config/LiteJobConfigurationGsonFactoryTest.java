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
package io.elasticjob.lite.internal.config;


import JobProperties.JobPropertiesEnum.EXECUTOR_SERVICE_HANDLER;
import JobProperties.JobPropertiesEnum.JOB_EXCEPTION_HANDLER;
import JobType.DATAFLOW;
import JobType.SCRIPT;
import JobType.SIMPLE;
import io.elasticjob.lite.api.script.ScriptJob;
import io.elasticjob.lite.config.JobCoreConfiguration;
import io.elasticjob.lite.config.LiteJobConfiguration;
import io.elasticjob.lite.executor.handler.impl.DefaultExecutorServiceHandler;
import io.elasticjob.lite.executor.handler.impl.DefaultJobExceptionHandler;
import io.elasticjob.lite.fixture.TestDataflowJob;
import io.elasticjob.lite.fixture.TestSimpleJob;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public final class LiteJobConfigurationGsonFactoryTest {
    private static final String JOB_PROPS_JSON = (((("{\"job_exception_handler\":\"" + (DefaultJobExceptionHandler.class.getCanonicalName())) + "\",") + "\"executor_service_handler\":\"") + (DefaultExecutorServiceHandler.class.getCanonicalName())) + "\"}";

    private String simpleJobJson = ((("{\"jobName\":\"test_job\",\"jobClass\":\"io.elasticjob.lite.fixture.TestSimpleJob\",\"jobType\":\"SIMPLE\",\"cron\":\"0/1 * * * * ?\"," + ("\"shardingTotalCount\":3,\"shardingItemParameters\":\"\",\"jobParameter\":\"\",\"failover\":true,\"misfire\":false,\"description\":\"\"," + "\"jobProperties\":")) + (LiteJobConfigurationGsonFactoryTest.JOB_PROPS_JSON)) + ",\"monitorExecution\":false,\"maxTimeDiffSeconds\":1000,\"monitorPort\":8888,") + "\"jobShardingStrategyClass\":\"testClass\",\"reconcileIntervalMinutes\":15,\"disabled\":true,\"overwrite\":true}";

    private String dataflowJobJson = ((("{\"jobName\":\"test_job\",\"jobClass\":\"io.elasticjob.lite.fixture.TestDataflowJob\",\"jobType\":\"DATAFLOW\",\"cron\":\"0/1 * * * * ?\"," + ("\"shardingTotalCount\":3,\"shardingItemParameters\":\"\",\"jobParameter\":\"\",\"failover\":false,\"misfire\":true,\"description\":\"\"," + "\"jobProperties\":")) + (LiteJobConfigurationGsonFactoryTest.JOB_PROPS_JSON)) + ",\"streamingProcess\":true,") + "\"monitorExecution\":true,\"maxTimeDiffSeconds\":-1,\"monitorPort\":-1,\"jobShardingStrategyClass\":\"\",\"reconcileIntervalMinutes\":10,\"disabled\":false,\"overwrite\":false}";

    private String scriptJobJson = ((("{\"jobName\":\"test_job\",\"jobClass\":\"io.elasticjob.lite.api.script.ScriptJob\",\"jobType\":\"SCRIPT\",\"cron\":\"0/1 * * * * ?\"," + ("\"shardingTotalCount\":3,\"shardingItemParameters\":\"\",\"jobParameter\":\"\",\"failover\":false,\"misfire\":true,\"description\":\"\"," + "\"jobProperties\":")) + (LiteJobConfigurationGsonFactoryTest.JOB_PROPS_JSON)) + ",\"scriptCommandLine\":\"test.sh\",\"monitorExecution\":true,\"maxTimeDiffSeconds\":-1,\"monitorPort\":-1,") + "\"jobShardingStrategyClass\":\"\",\"reconcileIntervalMinutes\":10,\"disabled\":false,\"overwrite\":false}";

    @Test
    public void assertToJsonForSimpleJob() {
        LiteJobConfiguration actual = LiteJobConfiguration.newBuilder(new io.elasticjob.lite.config.simple.SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3).failover(true).misfire(false).build(), TestSimpleJob.class.getCanonicalName())).monitorExecution(false).maxTimeDiffSeconds(1000).monitorPort(8888).jobShardingStrategyClass("testClass").disabled(true).overwrite(true).reconcileIntervalMinutes(15).build();
        Assert.assertThat(LiteJobConfigurationGsonFactory.toJson(actual), Is.is(simpleJobJson));
    }

    @Test
    public void assertToJsonForDataflowJob() {
        LiteJobConfiguration actual = LiteJobConfiguration.newBuilder(new io.elasticjob.lite.config.dataflow.DataflowJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3).build(), TestDataflowJob.class.getCanonicalName(), true)).build();
        Assert.assertThat(LiteJobConfigurationGsonFactory.toJson(actual), Is.is(dataflowJobJson));
    }

    @Test
    public void assertToJsonForScriptJob() {
        LiteJobConfiguration actual = LiteJobConfiguration.newBuilder(new io.elasticjob.lite.config.script.ScriptJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3).build(), "test.sh")).build();
        Assert.assertThat(LiteJobConfigurationGsonFactory.toJson(actual), Is.is(scriptJobJson));
    }

    @Test
    public void assertFromJsonForSimpleJob() {
        LiteJobConfiguration actual = LiteJobConfigurationGsonFactory.fromJson(simpleJobJson);
        Assert.assertThat(actual.getJobName(), Is.is("test_job"));
        Assert.assertThat(actual.getTypeConfig().getJobClass(), Is.is(TestSimpleJob.class.getCanonicalName()));
        Assert.assertThat(actual.getTypeConfig().getJobType(), Is.is(SIMPLE));
        Assert.assertThat(actual.getTypeConfig().getCoreConfig().getCron(), Is.is("0/1 * * * * ?"));
        Assert.assertThat(actual.getTypeConfig().getCoreConfig().getShardingTotalCount(), Is.is(3));
        Assert.assertThat(actual.getTypeConfig().getCoreConfig().getShardingItemParameters(), Is.is(""));
        Assert.assertThat(actual.getTypeConfig().getCoreConfig().getJobParameter(), Is.is(""));
        Assert.assertTrue(actual.getTypeConfig().getCoreConfig().isFailover());
        Assert.assertFalse(actual.getTypeConfig().getCoreConfig().isMisfire());
        Assert.assertThat(actual.getTypeConfig().getCoreConfig().getDescription(), Is.is(""));
        Assert.assertThat(actual.getTypeConfig().getCoreConfig().getJobProperties().get(JOB_EXCEPTION_HANDLER), Is.is(DefaultJobExceptionHandler.class.getCanonicalName()));
        Assert.assertThat(actual.getTypeConfig().getCoreConfig().getJobProperties().get(EXECUTOR_SERVICE_HANDLER), Is.is(DefaultExecutorServiceHandler.class.getCanonicalName()));
        Assert.assertFalse(actual.isMonitorExecution());
        Assert.assertThat(actual.getMaxTimeDiffSeconds(), Is.is(1000));
        Assert.assertThat(actual.getMonitorPort(), Is.is(8888));
        Assert.assertThat(actual.getJobShardingStrategyClass(), Is.is("testClass"));
        Assert.assertThat(actual.getReconcileIntervalMinutes(), Is.is(15));
        Assert.assertTrue(actual.isDisabled());
        Assert.assertTrue(actual.isOverwrite());
    }

    @Test
    public void assertFromJsonForDataflowJob() {
        LiteJobConfiguration actual = LiteJobConfigurationGsonFactory.fromJson(dataflowJobJson);
        Assert.assertThat(actual.getJobName(), Is.is("test_job"));
        Assert.assertThat(actual.getTypeConfig().getJobClass(), Is.is(TestDataflowJob.class.getCanonicalName()));
        Assert.assertThat(actual.getTypeConfig().getJobType(), Is.is(DATAFLOW));
        Assert.assertThat(actual.getTypeConfig().getCoreConfig().getCron(), Is.is("0/1 * * * * ?"));
        Assert.assertThat(actual.getTypeConfig().getCoreConfig().getShardingTotalCount(), Is.is(3));
        Assert.assertThat(actual.getTypeConfig().getCoreConfig().getShardingItemParameters(), Is.is(""));
        Assert.assertThat(actual.getTypeConfig().getCoreConfig().getJobParameter(), Is.is(""));
        Assert.assertFalse(actual.getTypeConfig().getCoreConfig().isFailover());
        Assert.assertTrue(actual.getTypeConfig().getCoreConfig().isMisfire());
        Assert.assertThat(actual.getTypeConfig().getCoreConfig().getDescription(), Is.is(""));
        Assert.assertThat(actual.getTypeConfig().getCoreConfig().getJobProperties().get(JOB_EXCEPTION_HANDLER), Is.is(DefaultJobExceptionHandler.class.getCanonicalName()));
        Assert.assertThat(actual.getTypeConfig().getCoreConfig().getJobProperties().get(EXECUTOR_SERVICE_HANDLER), Is.is(DefaultExecutorServiceHandler.class.getCanonicalName()));
        Assert.assertTrue(actual.isMonitorExecution());
        Assert.assertThat(actual.getMaxTimeDiffSeconds(), Is.is((-1)));
        Assert.assertThat(actual.getMonitorPort(), Is.is((-1)));
        Assert.assertThat(actual.getJobShardingStrategyClass(), Is.is(""));
        Assert.assertThat(actual.getReconcileIntervalMinutes(), Is.is(10));
        Assert.assertFalse(actual.isDisabled());
        Assert.assertFalse(actual.isOverwrite());
        Assert.assertTrue(isStreamingProcess());
    }

    @Test
    public void assertFromJsonForScriptJob() {
        LiteJobConfiguration actual = LiteJobConfigurationGsonFactory.fromJson(scriptJobJson);
        Assert.assertThat(actual.getJobName(), Is.is("test_job"));
        Assert.assertThat(actual.getTypeConfig().getJobClass(), Is.is(ScriptJob.class.getCanonicalName()));
        Assert.assertThat(actual.getTypeConfig().getJobType(), Is.is(SCRIPT));
        Assert.assertThat(actual.getTypeConfig().getCoreConfig().getCron(), Is.is("0/1 * * * * ?"));
        Assert.assertThat(actual.getTypeConfig().getCoreConfig().getShardingTotalCount(), Is.is(3));
        Assert.assertThat(actual.getTypeConfig().getCoreConfig().getShardingItemParameters(), Is.is(""));
        Assert.assertThat(actual.getTypeConfig().getCoreConfig().getJobParameter(), Is.is(""));
        Assert.assertFalse(actual.getTypeConfig().getCoreConfig().isFailover());
        Assert.assertTrue(actual.getTypeConfig().getCoreConfig().isMisfire());
        Assert.assertThat(actual.getTypeConfig().getCoreConfig().getDescription(), Is.is(""));
        Assert.assertThat(actual.getTypeConfig().getCoreConfig().getJobProperties().get(JOB_EXCEPTION_HANDLER), Is.is(DefaultJobExceptionHandler.class.getCanonicalName()));
        Assert.assertThat(actual.getTypeConfig().getCoreConfig().getJobProperties().get(EXECUTOR_SERVICE_HANDLER), Is.is(DefaultExecutorServiceHandler.class.getCanonicalName()));
        Assert.assertTrue(actual.isMonitorExecution());
        Assert.assertThat(actual.getMaxTimeDiffSeconds(), Is.is((-1)));
        Assert.assertThat(actual.getMonitorPort(), Is.is((-1)));
        Assert.assertThat(actual.getJobShardingStrategyClass(), Is.is(""));
        Assert.assertThat(actual.getReconcileIntervalMinutes(), Is.is(10));
        Assert.assertFalse(actual.isDisabled());
        Assert.assertFalse(actual.isOverwrite());
        Assert.assertThat(getScriptCommandLine(), Is.is("test.sh"));
    }
}

