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
package io.elasticjob.lite.internal.guarantee;


import io.elasticjob.lite.config.JobCoreConfiguration;
import io.elasticjob.lite.config.LiteJobConfiguration;
import io.elasticjob.lite.fixture.TestDataflowJob;
import io.elasticjob.lite.fixture.TestSimpleJob;
import io.elasticjob.lite.internal.config.ConfigurationService;
import io.elasticjob.lite.internal.storage.JobNodeStorage;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public final class GuaranteeServiceTest {
    @Mock
    private JobNodeStorage jobNodeStorage;

    @Mock
    private ConfigurationService configService;

    private final GuaranteeService guaranteeService = new GuaranteeService(null, "test_job");

    @Test
    public void assertRegisterStart() {
        guaranteeService.registerStart(Arrays.asList(0, 1));
        Mockito.verify(jobNodeStorage).createJobNodeIfNeeded("guarantee/started/0");
        Mockito.verify(jobNodeStorage).createJobNodeIfNeeded("guarantee/started/1");
    }

    @Test
    public void assertIsNotAllStartedWhenRootNodeIsNotExisted() {
        Mockito.when(jobNodeStorage.isJobNodeExisted("guarantee/started")).thenReturn(false);
        Assert.assertFalse(guaranteeService.isAllStarted());
    }

    @Test
    public void assertIsNotAllStarted() {
        Mockito.when(configService.load(false)).thenReturn(LiteJobConfiguration.newBuilder(new io.elasticjob.lite.config.dataflow.DataflowJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3).build(), TestDataflowJob.class.getCanonicalName(), true)).build());
        Mockito.when(jobNodeStorage.isJobNodeExisted("guarantee/started")).thenReturn(true);
        Mockito.when(jobNodeStorage.getJobNodeChildrenKeys("guarantee/started")).thenReturn(Arrays.asList("0", "1"));
        Assert.assertFalse(guaranteeService.isAllStarted());
    }

    @Test
    public void assertIsAllStarted() {
        Mockito.when(jobNodeStorage.isJobNodeExisted("guarantee/started")).thenReturn(true);
        Mockito.when(configService.load(false)).thenReturn(LiteJobConfiguration.newBuilder(new io.elasticjob.lite.config.simple.SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3).build(), TestSimpleJob.class.getCanonicalName())).build());
        Mockito.when(jobNodeStorage.getJobNodeChildrenKeys("guarantee/started")).thenReturn(Arrays.asList("0", "1", "2"));
        Assert.assertTrue(guaranteeService.isAllStarted());
    }

    @Test
    public void assertClearAllStartedInfo() {
        guaranteeService.clearAllStartedInfo();
        Mockito.verify(jobNodeStorage).removeJobNodeIfExisted("guarantee/started");
    }

    @Test
    public void assertRegisterComplete() {
        guaranteeService.registerComplete(Arrays.asList(0, 1));
        Mockito.verify(jobNodeStorage).createJobNodeIfNeeded("guarantee/completed/0");
        Mockito.verify(jobNodeStorage).createJobNodeIfNeeded("guarantee/completed/1");
    }

    @Test
    public void assertIsNotAllCompletedWhenRootNodeIsNotExisted() {
        Mockito.when(jobNodeStorage.isJobNodeExisted("guarantee/completed")).thenReturn(false);
        Assert.assertFalse(guaranteeService.isAllCompleted());
    }

    @Test
    public void assertIsNotAllCompleted() {
        Mockito.when(configService.load(false)).thenReturn(LiteJobConfiguration.newBuilder(new io.elasticjob.lite.config.simple.SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 10).build(), TestSimpleJob.class.getCanonicalName())).build());
        Mockito.when(jobNodeStorage.isJobNodeExisted("guarantee/completed")).thenReturn(false);
        Mockito.when(jobNodeStorage.getJobNodeChildrenKeys("guarantee/completed")).thenReturn(Arrays.asList("0", "1"));
        Assert.assertFalse(guaranteeService.isAllCompleted());
    }

    @Test
    public void assertIsAllCompleted() {
        Mockito.when(jobNodeStorage.isJobNodeExisted("guarantee/completed")).thenReturn(true);
        Mockito.when(configService.load(false)).thenReturn(LiteJobConfiguration.newBuilder(new io.elasticjob.lite.config.simple.SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3).build(), TestSimpleJob.class.getCanonicalName())).build());
        Mockito.when(jobNodeStorage.getJobNodeChildrenKeys("guarantee/completed")).thenReturn(Arrays.asList("0", "1", "2"));
        Assert.assertTrue(guaranteeService.isAllCompleted());
    }

    @Test
    public void assertClearAllCompletedInfo() {
        guaranteeService.clearAllCompletedInfo();
        Mockito.verify(jobNodeStorage).removeJobNodeIfExisted("guarantee/completed");
    }
}

