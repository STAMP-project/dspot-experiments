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


import ConfigurationNode.ROOT;
import io.elasticjob.lite.config.LiteJobConfiguration;
import io.elasticjob.lite.exception.JobConfigurationException;
import io.elasticjob.lite.exception.JobExecutionEnvironmentException;
import io.elasticjob.lite.fixture.LiteJsonConstants;
import io.elasticjob.lite.fixture.util.JobConfigurationUtil;
import io.elasticjob.lite.internal.storage.JobNodeStorage;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public final class ConfigurationServiceTest {
    @Mock
    private JobNodeStorage jobNodeStorage;

    private final ConfigurationService configService = new ConfigurationService(null, "test_job");

    @Test
    public void assertLoadDirectly() {
        Mockito.when(jobNodeStorage.getJobNodeDataDirectly(ROOT)).thenReturn(LiteJsonConstants.getJobJson());
        LiteJobConfiguration actual = configService.load(false);
        Assert.assertThat(actual.getJobName(), CoreMatchers.is("test_job"));
        Assert.assertThat(actual.getTypeConfig().getCoreConfig().getCron(), CoreMatchers.is("0/1 * * * * ?"));
        Assert.assertThat(actual.getTypeConfig().getCoreConfig().getShardingTotalCount(), CoreMatchers.is(3));
    }

    @Test
    public void assertLoadFromCache() {
        Mockito.when(jobNodeStorage.getJobNodeData(ROOT)).thenReturn(LiteJsonConstants.getJobJson());
        LiteJobConfiguration actual = configService.load(true);
        Assert.assertThat(actual.getJobName(), CoreMatchers.is("test_job"));
        Assert.assertThat(actual.getTypeConfig().getCoreConfig().getCron(), CoreMatchers.is("0/1 * * * * ?"));
        Assert.assertThat(actual.getTypeConfig().getCoreConfig().getShardingTotalCount(), CoreMatchers.is(3));
    }

    @Test
    public void assertLoadFromCacheButNull() {
        Mockito.when(jobNodeStorage.getJobNodeData(ROOT)).thenReturn(null);
        Mockito.when(jobNodeStorage.getJobNodeDataDirectly(ROOT)).thenReturn(LiteJsonConstants.getJobJson());
        LiteJobConfiguration actual = configService.load(true);
        Assert.assertThat(actual.getJobName(), CoreMatchers.is("test_job"));
        Assert.assertThat(actual.getTypeConfig().getCoreConfig().getCron(), CoreMatchers.is("0/1 * * * * ?"));
        Assert.assertThat(actual.getTypeConfig().getCoreConfig().getShardingTotalCount(), CoreMatchers.is(3));
    }

    @Test(expected = JobConfigurationException.class)
    public void assertPersistJobConfigurationForJobConflict() {
        Mockito.when(jobNodeStorage.isJobNodeExisted(ROOT)).thenReturn(true);
        Mockito.when(jobNodeStorage.getJobNodeDataDirectly(ROOT)).thenReturn(LiteJsonConstants.getJobJson("io.elasticjob.lite.api.script.api.ScriptJob"));
        try {
            configService.persist(JobConfigurationUtil.createSimpleLiteJobConfiguration());
        } finally {
            Mockito.verify(jobNodeStorage).isJobNodeExisted(ROOT);
            Mockito.verify(jobNodeStorage).getJobNodeDataDirectly(ROOT);
        }
    }

    @Test
    public void assertPersistNewJobConfiguration() {
        LiteJobConfiguration liteJobConfig = JobConfigurationUtil.createSimpleLiteJobConfiguration();
        configService.persist(liteJobConfig);
        Mockito.verify(jobNodeStorage).replaceJobNode("config", LiteJobConfigurationGsonFactory.toJson(liteJobConfig));
    }

    @Test
    public void assertPersistExistedJobConfiguration() throws NoSuchFieldException {
        Mockito.when(jobNodeStorage.isJobNodeExisted(ROOT)).thenReturn(true);
        Mockito.when(jobNodeStorage.getJobNodeDataDirectly(ROOT)).thenReturn(LiteJsonConstants.getJobJson());
        LiteJobConfiguration liteJobConfig = JobConfigurationUtil.createSimpleLiteJobConfiguration(true);
        configService.persist(liteJobConfig);
        Mockito.verify(jobNodeStorage).replaceJobNode("config", LiteJobConfigurationGsonFactory.toJson(liteJobConfig));
    }

    @Test
    public void assertIsMaxTimeDiffSecondsTolerableWithDefaultValue() throws JobExecutionEnvironmentException {
        Mockito.when(jobNodeStorage.getJobNodeData(ROOT)).thenReturn(LiteJsonConstants.getJobJson((-1)));
        configService.checkMaxTimeDiffSecondsTolerable();
    }

    @Test
    public void assertIsMaxTimeDiffSecondsTolerable() throws JobExecutionEnvironmentException {
        Mockito.when(jobNodeStorage.getJobNodeData(ROOT)).thenReturn(LiteJsonConstants.getJobJson());
        Mockito.when(jobNodeStorage.getRegistryCenterTime()).thenReturn(System.currentTimeMillis());
        configService.checkMaxTimeDiffSecondsTolerable();
        Mockito.verify(jobNodeStorage).getRegistryCenterTime();
    }

    @Test(expected = JobExecutionEnvironmentException.class)
    public void assertIsNotMaxTimeDiffSecondsTolerable() throws JobExecutionEnvironmentException {
        Mockito.when(jobNodeStorage.getJobNodeData(ROOT)).thenReturn(LiteJsonConstants.getJobJson());
        Mockito.when(jobNodeStorage.getRegistryCenterTime()).thenReturn(0L);
        try {
            configService.checkMaxTimeDiffSecondsTolerable();
        } finally {
            Mockito.verify(jobNodeStorage).getRegistryCenterTime();
        }
    }
}

