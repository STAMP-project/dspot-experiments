/**
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
package org.flowable.engine.test.bpmn.event.timer;


import java.util.Date;
import java.util.List;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.common.engine.impl.calendar.BusinessCalendar;
import org.flowable.engine.impl.test.ResourceFlowableTestCase;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.test.Deployment;
import org.flowable.job.api.Job;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;


/**
 * testing custom calendar for timer definitions Created by martin.grofcik
 */
public class TimerCustomCalendarTest extends ResourceFlowableTestCase {
    public TimerCustomCalendarTest() {
        super("org/flowable/engine/test/bpmn/event/timer/TimerCustomCalendarTest.flowable.cfg.xml");
    }

    @Test
    @Deployment
    public void testCycleTimer() {
        List<Job> jobs = this.managementService.createTimerJobQuery().list();
        MatcherAssert.assertThat("One job is scheduled", jobs.size(), CoreMatchers.is(1));
        MatcherAssert.assertThat("Job must be scheduled by custom business calendar to Date(0)", jobs.get(0).getDuedate(), CoreMatchers.is(new Date(0)));
        managementService.moveTimerToExecutableJob(jobs.get(0).getId());
        managementService.executeJob(jobs.get(0).getId());
        jobs = this.managementService.createTimerJobQuery().list();
        MatcherAssert.assertThat("One job is scheduled (repetition is 2x)", jobs.size(), CoreMatchers.is(1));
        MatcherAssert.assertThat("Job must be scheduled by custom business calendar to Date(0)", jobs.get(0).getDuedate(), CoreMatchers.is(new Date(0)));
        managementService.moveTimerToExecutableJob(jobs.get(0).getId());
        managementService.executeJob(jobs.get(0).getId());
        jobs = this.managementService.createTimerJobQuery().list();
        MatcherAssert.assertThat("There must be no job.", jobs.isEmpty());
    }

    @Test
    @Deployment
    public void testCustomDurationTimerCalendar() {
        ProcessInstance processInstance = this.runtimeService.startProcessInstanceByKey("testCustomDurationCalendar");
        List<Job> jobs = this.managementService.createTimerJobQuery().list();
        MatcherAssert.assertThat("One job is scheduled", jobs.size(), CoreMatchers.is(1));
        MatcherAssert.assertThat("Job must be scheduled by custom business calendar to Date(0)", jobs.get(0).getDuedate(), CoreMatchers.is(new Date(0)));
        managementService.moveTimerToExecutableJob(jobs.get(0).getId());
        managementService.executeJob(jobs.get(0).getId());
        waitForJobExecutorToProcessAllJobsAndExecutableTimerJobs(10000, 200);
        Execution execution = runtimeService.createExecutionQuery().processInstanceId(processInstance.getId()).activityId("receive").singleResult();
        runtimeService.trigger(execution.getId());
    }

    @Test
    @Deployment
    public void testInvalidDurationTimerCalendar() {
        try {
            this.runtimeService.startProcessInstanceByKey("testCustomDurationCalendar");
            fail("Activiti exception expected - calendar not found");
        } catch (FlowableException e) {
            MatcherAssert.assertThat(e.getMessage(), CoreMatchers.containsString("INVALID does not exist"));
        }
    }

    @Test
    @Deployment
    public void testBoundaryTimer() {
        this.runtimeService.startProcessInstanceByKey("testBoundaryTimer");
        List<Job> jobs = this.managementService.createTimerJobQuery().list();
        MatcherAssert.assertThat("One job is scheduled", jobs.size(), CoreMatchers.is(1));
        MatcherAssert.assertThat("Job must be scheduled by custom business calendar to Date(0)", jobs.get(0).getDuedate(), CoreMatchers.is(new Date(0)));
        managementService.moveTimerToExecutableJob(jobs.get(0).getId());
        managementService.executeJob(jobs.get(0).getId());
        waitForJobExecutorToProcessAllJobsAndExecutableTimerJobs(10000, 200);
    }

    public static class CustomBusinessCalendar implements BusinessCalendar {
        @Override
        public Date resolveDuedate(String duedateDescription) {
            return new Date(0);
        }

        @Override
        public Date resolveDuedate(String duedateDescription, int maxIterations) {
            return new Date(0);
        }

        @Override
        public Boolean validateDuedate(String duedateDescription, int maxIterations, Date endDate, Date newTimer) {
            return true;
        }

        @Override
        public Date resolveEndDate(String endDateString) {
            return new Date(0);
        }
    }
}

