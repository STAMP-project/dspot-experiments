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
package io.elasticjob.lite.api;


import io.elasticjob.lite.api.listener.fixture.ElasticJobListenerCaller;
import io.elasticjob.lite.config.LiteJobConfiguration;
import io.elasticjob.lite.internal.schedule.JobRegistry;
import io.elasticjob.lite.internal.schedule.JobScheduleController;
import io.elasticjob.lite.internal.schedule.JobTriggerListener;
import io.elasticjob.lite.internal.schedule.SchedulerFacade;
import io.elasticjob.lite.reg.base.CoordinatorRegistryCenter;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.unitils.util.ReflectionUtils;


public final class JobSchedulerTest {
    @Mock
    private CoordinatorRegistryCenter regCenter;

    @Mock
    private SchedulerFacade schedulerFacade;

    @Mock
    private ElasticJobListenerCaller caller;

    private LiteJobConfiguration liteJobConfig;

    private JobScheduler jobScheduler;

    @Test
    public void assertInit() throws NoSuchFieldException, SchedulerException {
        Mockito.when(schedulerFacade.updateJobConfiguration(liteJobConfig)).thenReturn(liteJobConfig);
        Mockito.when(schedulerFacade.newJobTriggerListener()).thenReturn(new JobTriggerListener(null, null));
        jobScheduler.init();
        Mockito.verify(schedulerFacade).registerStartUpInfo(true);
        Scheduler scheduler = ReflectionUtils.getFieldValue(JobRegistry.getInstance().getJobScheduleController("test_job"), JobScheduleController.class.getDeclaredField("scheduler"));
        Assert.assertThat(scheduler.getListenerManager().getTriggerListeners().get(0), CoreMatchers.instanceOf(JobTriggerListener.class));
        Assert.assertTrue(scheduler.isStarted());
    }
}

