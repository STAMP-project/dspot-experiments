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
package io.elasticjob.lite.config;


import io.elasticjob.lite.fixture.TestSimpleJob;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public final class LiteJobConfigurationTest {
    @Test
    public void assertBuildAllProperties() {
        LiteJobConfiguration actual = LiteJobConfiguration.newBuilder(new io.elasticjob.lite.config.simple.SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3).build(), TestSimpleJob.class.getCanonicalName())).monitorExecution(false).maxTimeDiffSeconds(1000).monitorPort(8888).jobShardingStrategyClass("testClass").disabled(true).overwrite(true).reconcileIntervalMinutes(60).build();
        Assert.assertFalse(actual.isMonitorExecution());
        Assert.assertThat(actual.getMaxTimeDiffSeconds(), Is.is(1000));
        Assert.assertThat(actual.getMonitorPort(), Is.is(8888));
        Assert.assertThat(actual.getJobShardingStrategyClass(), Is.is("testClass"));
        Assert.assertTrue(actual.isDisabled());
        Assert.assertTrue(actual.isOverwrite());
        Assert.assertThat(actual.getReconcileIntervalMinutes(), Is.is(60));
    }

    @Test
    public void assertBuildRequiredProperties() {
        LiteJobConfiguration actual = LiteJobConfiguration.newBuilder(new io.elasticjob.lite.config.simple.SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3).build(), TestSimpleJob.class.getCanonicalName())).build();
        Assert.assertTrue(actual.isMonitorExecution());
        Assert.assertThat(actual.getMaxTimeDiffSeconds(), Is.is((-1)));
        Assert.assertThat(actual.getMonitorPort(), Is.is((-1)));
        Assert.assertThat(actual.getJobShardingStrategyClass(), Is.is(""));
        Assert.assertFalse(actual.isDisabled());
        Assert.assertFalse(actual.isOverwrite());
    }

    @Test
    public void assertBuildWhenOptionalParametersIsNull() {
        Assert.assertThat(LiteJobConfiguration.newBuilder(new io.elasticjob.lite.config.simple.SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3).build(), TestSimpleJob.class.getCanonicalName())).jobShardingStrategyClass(null).build().getJobShardingStrategyClass(), Is.is(""));
    }

    @Test
    public void assertIsNotFailover() {
        Assert.assertFalse(LiteJobConfiguration.newBuilder(new io.elasticjob.lite.config.simple.SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3).failover(false).build(), TestSimpleJob.class.getCanonicalName())).monitorExecution(false).build().isFailover());
    }

    @Test
    public void assertIsFailover() {
        Assert.assertTrue(LiteJobConfiguration.newBuilder(new io.elasticjob.lite.config.simple.SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3).failover(true).build(), TestSimpleJob.class.getCanonicalName())).monitorExecution(true).build().isFailover());
    }
}

