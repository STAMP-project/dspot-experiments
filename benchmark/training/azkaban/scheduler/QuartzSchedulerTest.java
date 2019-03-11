/**
 * Copyright 2017 LinkedIn Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package azkaban.scheduler;


import azkaban.db.DatabaseOperator;
import azkaban.test.TestUtils;
import org.junit.Test;
import org.quartz.SchedulerException;


/**
 * Use H2-in-mem database to directly test Quartz.
 */
public class QuartzSchedulerTest {
    private static DatabaseOperator dbOperator;

    private static QuartzScheduler scheduler;

    @Test
    public void testCreateScheduleAndRun() throws Exception {
        QuartzSchedulerTest.scheduler.registerJob("* * * * * ?", createJobDescription());
        assertThat(QuartzSchedulerTest.scheduler.ifJobExist("SampleJob", "SampleService")).isEqualTo(true);
        TestUtils.await().untilAsserted(() -> assertThat(SampleQuartzJob.COUNT_EXECUTION).isNotNull().isGreaterThan(1));
    }

    @Test
    public void testNotAllowDuplicateJobRegister() throws Exception {
        QuartzSchedulerTest.scheduler.registerJob("* * * * * ?", createJobDescription());
        assertThatThrownBy(() -> QuartzSchedulerTest.scheduler.registerJob("0 5 * * * ?", createJobDescription())).isInstanceOf(SchedulerException.class).hasMessageContaining("can not register existing job");
    }

    @Test
    public void testInvalidCron() throws Exception {
        assertThatThrownBy(() -> QuartzSchedulerTest.scheduler.registerJob("0 5 * * * *", createJobDescription())).isInstanceOf(SchedulerException.class).hasMessageContaining("The cron expression string");
    }

    @Test
    public void testUnregisterSchedule() throws Exception {
        QuartzSchedulerTest.scheduler.registerJob("* * * * * ?", createJobDescription());
        assertThat(QuartzSchedulerTest.scheduler.ifJobExist("SampleJob", "SampleService")).isEqualTo(true);
        QuartzSchedulerTest.scheduler.unregisterJob("SampleJob", "SampleService");
        assertThat(QuartzSchedulerTest.scheduler.ifJobExist("SampleJob", "SampleService")).isEqualTo(false);
    }

    @Test
    public void testPauseSchedule() throws Exception {
        QuartzSchedulerTest.scheduler.registerJob("* * * * * ?", createJobDescription());
        QuartzSchedulerTest.scheduler.pauseJob("SampleJob", "SampleService");
        assertThat(QuartzSchedulerTest.scheduler.isJobPaused("SampleJob", "SampleService")).isEqualTo(true);
        QuartzSchedulerTest.scheduler.resumeJob("SampleJob", "SampleService");
        assertThat(QuartzSchedulerTest.scheduler.isJobPaused("SampleJob", "SampleService")).isEqualTo(false);
        // test pausing a paused job
        QuartzSchedulerTest.scheduler.pauseJob("SampleJob", "SampleService");
        QuartzSchedulerTest.scheduler.pauseJob("SampleJob", "SampleService");
        assertThat(QuartzSchedulerTest.scheduler.isJobPaused("SampleJob", "SampleService")).isEqualTo(true);
        // test resuming a non-paused job
        QuartzSchedulerTest.scheduler.resumeJob("SampleJob", "SampleService");
        QuartzSchedulerTest.scheduler.resumeJob("SampleJob", "SampleService");
        assertThat(QuartzSchedulerTest.scheduler.isJobPaused("SampleJob", "SampleService")).isEqualTo(false);
    }
}

