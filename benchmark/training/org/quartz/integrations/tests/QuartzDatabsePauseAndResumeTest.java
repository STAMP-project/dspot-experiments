/**
 * All content copyright Terracotta, Inc., unless otherwise indicated. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.quartz.integrations.tests;


import Trigger.TriggerState;
import Trigger.TriggerState.NORMAL;
import Trigger.TriggerState.PAUSED;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;


/**
 * Created by zemian on 10/25/16.
 */
public class QuartzDatabsePauseAndResumeTest extends QuartzDatabaseTestSupport {
    @Test
    public void testPauseAndResumeTriggers() throws Exception {
        JobDetail jobDetail = JobBuilder.newJob(HelloJob.class).withIdentity("test_1").build();
        CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity("test_1", "abc").withSchedule(CronScheduleBuilder.cronSchedule("* * * * * ?")).build();
        scheduler.scheduleJob(jobDetail, trigger);
        Trigger.TriggerState state = scheduler.getTriggerState(TriggerKey.triggerKey("test_1", "abc"));
        Assert.assertThat(state, CoreMatchers.is(NORMAL));
        Assert.assertThat(state, Matchers.not(PAUSED));
        scheduler.pauseTriggers(GroupMatcher.triggerGroupEquals("abc"));
        state = scheduler.getTriggerState(TriggerKey.triggerKey("test_1", "abc"));
        Assert.assertThat(state, CoreMatchers.is(PAUSED));
        Assert.assertThat(state, Matchers.not(NORMAL));
        scheduler.resumeTriggers(GroupMatcher.triggerGroupEquals("abc"));
        state = scheduler.getTriggerState(TriggerKey.triggerKey("test_1", "abc"));
        Assert.assertThat(state, CoreMatchers.is(NORMAL));
        Assert.assertThat(state, Matchers.not(PAUSED));
    }

    @Test
    public void testResumeTriggersBeforeAddJob() throws Exception {
        scheduler.pauseTriggers(GroupMatcher.triggerGroupEquals("abc"));
        scheduler.resumeTriggers(GroupMatcher.triggerGroupEquals("abc"));
        JobDetail jobDetail = JobBuilder.newJob(HelloJob.class).withIdentity("test_2").build();
        CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity("test_2", "abc").withSchedule(CronScheduleBuilder.cronSchedule("* * * * * ?")).build();
        scheduler.scheduleJob(jobDetail, trigger);
        Trigger.TriggerState state = scheduler.getTriggerState(TriggerKey.triggerKey("test_2", "abc"));
        Assert.assertThat(state, CoreMatchers.is(NORMAL));
        Assert.assertThat(state, Matchers.not(PAUSED));
        scheduler.pauseTriggers(GroupMatcher.triggerGroupEquals("abc"));
        state = scheduler.getTriggerState(TriggerKey.triggerKey("test_2", "abc"));
        Assert.assertThat(state, CoreMatchers.is(PAUSED));
        Assert.assertThat(state, Matchers.not(NORMAL));
        scheduler.resumeTriggers(GroupMatcher.triggerGroupEquals("abc"));
        state = scheduler.getTriggerState(TriggerKey.triggerKey("test_2", "abc"));
        Assert.assertThat(state, CoreMatchers.is(NORMAL));
        Assert.assertThat(state, Matchers.not(PAUSED));
    }

    @Test
    public void testPauseAndResumeJobs() throws Exception {
        JobDetail jobDetail = JobBuilder.newJob(HelloJob.class).withIdentity("test_3", "abc").build();
        CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity("test_3", "abc").withSchedule(CronScheduleBuilder.cronSchedule("* * * * * ?")).build();
        scheduler.scheduleJob(jobDetail, trigger);
        Trigger.TriggerState state = scheduler.getTriggerState(TriggerKey.triggerKey("test_3", "abc"));
        Assert.assertThat(state, CoreMatchers.is(NORMAL));
        Assert.assertThat(state, Matchers.not(PAUSED));
        scheduler.pauseJobs(GroupMatcher.jobGroupEquals("abc"));
        state = scheduler.getTriggerState(TriggerKey.triggerKey("test_3", "abc"));
        Assert.assertThat(state, CoreMatchers.is(PAUSED));
        Assert.assertThat(state, Matchers.not(NORMAL));
        scheduler.resumeJobs(GroupMatcher.jobGroupEquals("abc"));
        state = scheduler.getTriggerState(TriggerKey.triggerKey("test_3", "abc"));
        Assert.assertThat(state, CoreMatchers.is(NORMAL));
        Assert.assertThat(state, Matchers.not(PAUSED));
    }

    @Test
    public void testResumeJobsBeforeAddJobs() throws Exception {
        scheduler.pauseJobs(GroupMatcher.jobGroupEquals("abc"));
        scheduler.resumeJobs(GroupMatcher.jobGroupEquals("abc"));
        JobDetail jobDetail = JobBuilder.newJob(HelloJob.class).withIdentity("test_4", "abc").build();
        CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity("test_4", "abc").withSchedule(CronScheduleBuilder.cronSchedule("* * * * * ?")).build();
        scheduler.scheduleJob(jobDetail, trigger);
        Trigger.TriggerState state = scheduler.getTriggerState(TriggerKey.triggerKey("test_4", "abc"));
        Assert.assertThat(state, CoreMatchers.is(NORMAL));
        Assert.assertThat(state, Matchers.not(PAUSED));
        scheduler.pauseJobs(GroupMatcher.jobGroupEquals("abc"));
        state = scheduler.getTriggerState(TriggerKey.triggerKey("test_4", "abc"));
        Assert.assertThat(state, CoreMatchers.is(PAUSED));
        Assert.assertThat(state, Matchers.not(NORMAL));
        scheduler.resumeJobs(GroupMatcher.jobGroupEquals("abc"));
        state = scheduler.getTriggerState(TriggerKey.triggerKey("test_4", "abc"));
        Assert.assertThat(state, CoreMatchers.is(NORMAL));
        Assert.assertThat(state, Matchers.not(PAUSED));
    }
}

