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
package org.quartz.impl.jdbcjobstore;


import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests for QTZ-326
 *
 * @author Zemian Deng
 */
public class DeleteNonExistsJobTest {
    private static Logger LOG = LoggerFactory.getLogger(DeleteNonExistsJobTest.class);

    private static String DB_NAME = "DeleteNonExistsJobTestDatasase";

    private static String SCHEDULER_NAME = "DeleteNonExistsJobTestScheduler";

    private static Scheduler scheduler;

    @Test
    public void deleteJobDetailOnly() throws Exception {
        JobDetail jobDetail = JobBuilder.newJob(DeleteNonExistsJobTest.TestJob.class).withIdentity("testjob").storeDurably().build();
        DeleteNonExistsJobTest.scheduler.addJob(jobDetail, true);
        modifyStoredJobClassName();
        DeleteNonExistsJobTest.scheduler.deleteJob(jobDetail.getKey());
    }

    @Test
    public void deleteJobDetailWithTrigger() throws Exception {
        JobDetail jobDetail = JobBuilder.newJob(DeleteNonExistsJobTest.TestJob.class).withIdentity("testjob2").storeDurably().build();
        Trigger trigger = TriggerBuilder.newTrigger().withIdentity("testjob2").withSchedule(CronScheduleBuilder.cronSchedule("* * * * * ?")).build();
        DeleteNonExistsJobTest.scheduler.scheduleJob(jobDetail, trigger);
        modifyStoredJobClassName();
        DeleteNonExistsJobTest.scheduler.deleteJob(jobDetail.getKey());
    }

    @Test
    public void deleteTrigger() throws Exception {
        JobDetail jobDetail = JobBuilder.newJob(DeleteNonExistsJobTest.TestJob.class).withIdentity("testjob3").storeDurably().build();
        Trigger trigger = TriggerBuilder.newTrigger().withIdentity("testjob3").withSchedule(CronScheduleBuilder.cronSchedule("* * * * * ?")).build();
        DeleteNonExistsJobTest.scheduler.scheduleJob(jobDetail, trigger);
        modifyStoredJobClassName();
        DeleteNonExistsJobTest.scheduler.unscheduleJob(trigger.getKey());
    }

    @Test
    public void replaceJobDetail() throws Exception {
        JobDetail jobDetail = JobBuilder.newJob(DeleteNonExistsJobTest.TestJob.class).withIdentity("testjob3").storeDurably().build();
        Trigger trigger = TriggerBuilder.newTrigger().withIdentity("testjob3").withSchedule(CronScheduleBuilder.cronSchedule("* * * * * ?")).build();
        DeleteNonExistsJobTest.scheduler.scheduleJob(jobDetail, trigger);
        modifyStoredJobClassName();
        jobDetail = JobBuilder.newJob(DeleteNonExistsJobTest.TestJob.class).withIdentity("testjob3").storeDurably().build();
        DeleteNonExistsJobTest.scheduler.addJob(jobDetail, true);
    }

    public static class TestJob implements Job {
        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            DeleteNonExistsJobTest.LOG.info("Job is executing {}", context);
        }
    }
}

