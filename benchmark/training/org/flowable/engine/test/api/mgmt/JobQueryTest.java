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
package org.flowable.engine.test.api.mgmt;


import java.util.Date;
import java.util.List;
import org.flowable.common.engine.api.FlowableIllegalArgumentException;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.common.engine.impl.interceptor.CommandExecutor;
import org.flowable.engine.impl.test.PluggableFlowableTestCase;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.test.Deployment;
import org.flowable.job.api.Job;
import org.flowable.job.api.JobQuery;
import org.flowable.job.api.TimerJobQuery;
import org.flowable.job.service.impl.persistence.entity.JobEntity;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author Joram Barrez
 * @author Falko Menge
 */
public class JobQueryTest extends PluggableFlowableTestCase {
    private String deploymentId;

    private String messageId;

    private CommandExecutor commandExecutor;

    private JobEntity jobEntity;

    private Date testStartTime;

    private Date timerOneFireTime;

    private Date timerTwoFireTime;

    private Date timerThreeFireTime;

    private String processInstanceIdOne;

    private String processInstanceIdTwo;

    private String processInstanceIdThree;

    private static final long ONE_HOUR = (60L * 60L) * 1000L;

    private static final long ONE_SECOND = 1000L;

    private static final String EXCEPTION_MESSAGE = "problem evaluating script: javax.script.ScriptException: java.lang.RuntimeException: This is an exception thrown from scriptTask";

    @Test
    public void testQueryByNoCriteria() {
        JobQuery query = managementService.createJobQuery();
        verifyQueryResults(query, 1);
        TimerJobQuery timerQuery = managementService.createTimerJobQuery();
        verifyQueryResults(timerQuery, 3);
    }

    @Test
    public void testQueryByProcessInstanceId() {
        TimerJobQuery query = managementService.createTimerJobQuery().processInstanceId(processInstanceIdOne);
        verifyQueryResults(query, 1);
    }

    @Test
    public void testQueryByInvalidProcessInstanceId() {
        TimerJobQuery query = managementService.createTimerJobQuery().processInstanceId("invalid");
        verifyQueryResults(query, 0);
        try {
            managementService.createJobQuery().processInstanceId(null);
            fail();
        } catch (FlowableIllegalArgumentException e) {
        }
    }

    @Test
    public void testQueryByExecutionId() {
        Job job = managementService.createTimerJobQuery().processInstanceId(processInstanceIdOne).singleResult();
        TimerJobQuery query = managementService.createTimerJobQuery().executionId(job.getExecutionId());
        assertEquals(query.singleResult().getId(), job.getId());
        verifyQueryResults(query, 1);
    }

    @Test
    public void testQueryByInvalidExecutionId() {
        JobQuery query = managementService.createJobQuery().executionId("invalid");
        verifyQueryResults(query, 0);
        TimerJobQuery timerQuery = managementService.createTimerJobQuery().executionId("invalid");
        verifyQueryResults(timerQuery, 0);
        try {
            managementService.createJobQuery().executionId(null).list();
            fail();
        } catch (FlowableIllegalArgumentException e) {
        }
        try {
            managementService.createTimerJobQuery().executionId(null).list();
            fail();
        } catch (FlowableIllegalArgumentException e) {
        }
    }

    @Test
    public void testQueryByHandlerType() {
        final JobEntity job = ((JobEntity) (managementService.createJobQuery().singleResult()));
        job.setJobHandlerType("test");
        managementService.executeCommand(new org.flowable.common.engine.impl.interceptor.Command<Void>() {
            @Override
            public Void execute(CommandContext commandContext) {
                CommandContextUtil.getJobService(commandContext).updateJob(job);
                return null;
            }
        });
        Job handlerTypeJob = managementService.createJobQuery().handlerType("test").singleResult();
        assertNotNull(handlerTypeJob);
    }

    @Test
    public void testQueryByInvalidJobType() {
        JobQuery query = managementService.createJobQuery().handlerType("invalid");
        verifyQueryResults(query, 0);
        TimerJobQuery timerQuery = managementService.createTimerJobQuery().executionId("invalid");
        verifyQueryResults(timerQuery, 0);
        try {
            managementService.createJobQuery().executionId(null).list();
            fail();
        } catch (FlowableIllegalArgumentException e) {
        }
        try {
            managementService.createTimerJobQuery().executionId(null).list();
            fail();
        } catch (FlowableIllegalArgumentException e) {
        }
    }

    @Test
    public void testQueryByRetriesLeft() {
        JobQuery query = managementService.createJobQuery();
        verifyQueryResults(query, 1);
        TimerJobQuery timerQuery = managementService.createTimerJobQuery();
        verifyQueryResults(timerQuery, 3);
        final Job job = managementService.createTimerJobQuery().processInstanceId(processInstanceIdOne).singleResult();
        managementService.setTimerJobRetries(job.getId(), 0);
        managementService.moveJobToDeadLetterJob(job.getId());
        // Re-running the query should give only 3 jobs now, since one job has retries=0
        verifyQueryResults(query, 1);
        verifyQueryResults(timerQuery, 2);
    }

    @Test
    public void testQueryByExecutable() {
        processEngineConfiguration.getClock().setCurrentTime(new Date(((timerThreeFireTime.getTime()) + (JobQueryTest.ONE_SECOND))));// all obs should be executable at t3 + 1hour.1second

        JobQuery query = managementService.createJobQuery();
        verifyQueryResults(query, 1);
        TimerJobQuery timerQuery = managementService.createTimerJobQuery().executable();
        verifyQueryResults(timerQuery, 3);
        // Setting retries of one job to 0, makes it non-executable
        final Job job = managementService.createTimerJobQuery().processInstanceId(processInstanceIdOne).singleResult();
        managementService.setTimerJobRetries(job.getId(), 0);
        managementService.moveJobToDeadLetterJob(job.getId());
        verifyQueryResults(query, 1);
        verifyQueryResults(timerQuery, 2);
        // Setting the clock before the start of the process instance, makes
        // none of the timer jobs executable
        processEngineConfiguration.getClock().setCurrentTime(testStartTime);
        verifyQueryResults(query, 1);
        verifyQueryResults(timerQuery, 0);
        // Moving the job back to be executable
        managementService.moveDeadLetterJobToExecutableJob(job.getId(), 5);
        verifyQueryResults(query, 2);
        verifyQueryResults(timerQuery, 0);
    }

    @Test
    public void testQueryByOnlyTimers() {
        JobQuery query = managementService.createJobQuery().timers();
        verifyQueryResults(query, 0);
        TimerJobQuery timerQuery = managementService.createTimerJobQuery().timers();
        verifyQueryResults(timerQuery, 3);
    }

    @Test
    public void testQueryByOnlyMessages() {
        JobQuery query = managementService.createJobQuery().messages();
        verifyQueryResults(query, 1);
    }

    @Test
    public void testInvalidOnlyTimersUsage() {
        try {
            managementService.createJobQuery().timers().messages().list();
            fail();
        } catch (FlowableIllegalArgumentException e) {
            assertTextPresent("Cannot combine onlyTimers() with onlyMessages() in the same query", e.getMessage());
        }
    }

    @Test
    public void testQueryByDuedateLowerThan() {
        JobQuery query = managementService.createJobQuery().duedateLowerThan(testStartTime);
        verifyQueryResults(query, 0);
        TimerJobQuery timerQuery = managementService.createTimerJobQuery().duedateLowerThan(testStartTime);
        verifyQueryResults(timerQuery, 0);
        timerQuery = managementService.createTimerJobQuery().duedateLowerThan(new Date(((timerOneFireTime.getTime()) + (JobQueryTest.ONE_SECOND))));
        verifyQueryResults(timerQuery, 1);
        timerQuery = managementService.createTimerJobQuery().duedateLowerThan(new Date(((timerTwoFireTime.getTime()) + (JobQueryTest.ONE_SECOND))));
        verifyQueryResults(timerQuery, 2);
        timerQuery = managementService.createTimerJobQuery().duedateLowerThan(new Date(((timerThreeFireTime.getTime()) + (JobQueryTest.ONE_SECOND))));
        verifyQueryResults(timerQuery, 3);
    }

    @Test
    public void testQueryByDuedateHigherThan() {
        JobQuery query = managementService.createJobQuery().duedateHigherThan(testStartTime);
        verifyQueryResults(query, 0);
        query = managementService.createJobQuery();
        verifyQueryResults(query, 1);
        TimerJobQuery timerQuery = managementService.createTimerJobQuery().duedateHigherThan(testStartTime);
        verifyQueryResults(timerQuery, 3);
        query = managementService.createJobQuery().duedateHigherThan(timerOneFireTime);
        verifyQueryResults(query, 0);
        timerQuery = managementService.createTimerJobQuery().duedateHigherThan(timerOneFireTime);
        verifyQueryResults(timerQuery, 2);
        timerQuery = managementService.createTimerJobQuery().duedateHigherThan(timerTwoFireTime);
        verifyQueryResults(timerQuery, 1);
        timerQuery = managementService.createTimerJobQuery().duedateHigherThan(timerThreeFireTime);
        verifyQueryResults(timerQuery, 0);
    }

    @Test
    @Deployment(resources = { "org/flowable/engine/test/api/mgmt/ManagementServiceTest.testGetJobExceptionStacktrace.bpmn20.xml" })
    public void testQueryByException() {
        TimerJobQuery query = managementService.createTimerJobQuery().withException();
        verifyQueryResults(query, 0);
        ProcessInstance processInstance = startProcessInstanceWithFailingJob();
        query = managementService.createTimerJobQuery().processInstanceId(processInstance.getId()).withException();
        verifyFailedJob(query, processInstance);
    }

    @Test
    @Deployment(resources = { "org/flowable/engine/test/api/mgmt/ManagementServiceTest.testGetJobExceptionStacktrace.bpmn20.xml" })
    public void testQueryByExceptionMessage() {
        TimerJobQuery query = managementService.createTimerJobQuery().exceptionMessage(JobQueryTest.EXCEPTION_MESSAGE);
        verifyQueryResults(query, 0);
        ProcessInstance processInstance = startProcessInstanceWithFailingJob();
        query = managementService.createTimerJobQuery().exceptionMessage(JobQueryTest.EXCEPTION_MESSAGE);
        verifyFailedJob(query, processInstance);
    }

    @Test
    @Deployment(resources = { "org/flowable/engine/test/api/mgmt/ManagementServiceTest.testGetJobExceptionStacktrace.bpmn20.xml" })
    public void testQueryByExceptionMessageEmpty() {
        JobQuery query = managementService.createJobQuery().exceptionMessage("");
        verifyQueryResults(query, 0);
        startProcessInstanceWithFailingJob();
        query = managementService.createJobQuery().exceptionMessage("");
        verifyQueryResults(query, 0);
    }

    @Test
    public void testQueryByExceptionMessageNull() {
        try {
            managementService.createJobQuery().exceptionMessage(null);
            fail("ActivitiException expected");
        } catch (FlowableIllegalArgumentException e) {
            assertEquals("Provided exception message is null", e.getMessage());
        }
    }

    @Test
    public void testJobQueryWithExceptions() throws Throwable {
        createJobWithoutExceptionMsg();
        Job job = managementService.createJobQuery().jobId(jobEntity.getId()).singleResult();
        assertNotNull(job);
        List<Job> list = managementService.createJobQuery().withException().list();
        assertEquals(1, list.size());
        deleteJobInDatabase();
        createJobWithoutExceptionStacktrace();
        job = managementService.createJobQuery().jobId(jobEntity.getId()).singleResult();
        assertNotNull(job);
        list = managementService.createJobQuery().withException().list();
        assertEquals(1, list.size());
        deleteJobInDatabase();
    }

    // sorting //////////////////////////////////////////
    @Test
    public void testQuerySorting() {
        // asc
        assertEquals(1, managementService.createJobQuery().orderByJobId().asc().count());
        assertEquals(1, managementService.createJobQuery().orderByJobDuedate().asc().count());
        assertEquals(1, managementService.createJobQuery().orderByExecutionId().asc().count());
        assertEquals(1, managementService.createJobQuery().orderByProcessInstanceId().asc().count());
        assertEquals(1, managementService.createJobQuery().orderByJobRetries().asc().count());
        assertEquals(3, managementService.createTimerJobQuery().orderByJobId().asc().count());
        assertEquals(3, managementService.createTimerJobQuery().orderByJobDuedate().asc().count());
        assertEquals(3, managementService.createTimerJobQuery().orderByExecutionId().asc().count());
        assertEquals(3, managementService.createTimerJobQuery().orderByProcessInstanceId().asc().count());
        assertEquals(3, managementService.createTimerJobQuery().orderByJobRetries().asc().count());
        // desc
        assertEquals(1, managementService.createJobQuery().orderByJobId().desc().count());
        assertEquals(1, managementService.createJobQuery().orderByJobDuedate().desc().count());
        assertEquals(1, managementService.createJobQuery().orderByExecutionId().desc().count());
        assertEquals(1, managementService.createJobQuery().orderByProcessInstanceId().desc().count());
        assertEquals(1, managementService.createJobQuery().orderByJobRetries().desc().count());
        assertEquals(3, managementService.createTimerJobQuery().orderByJobId().desc().count());
        assertEquals(3, managementService.createTimerJobQuery().orderByJobDuedate().desc().count());
        assertEquals(3, managementService.createTimerJobQuery().orderByExecutionId().desc().count());
        assertEquals(3, managementService.createTimerJobQuery().orderByProcessInstanceId().desc().count());
        assertEquals(3, managementService.createTimerJobQuery().orderByJobRetries().desc().count());
        // sorting on multiple fields
        setRetries(processInstanceIdTwo, 2);
        processEngineConfiguration.getClock().setCurrentTime(new Date(((timerThreeFireTime.getTime()) + (JobQueryTest.ONE_SECOND))));// make sure all timers can fire

        TimerJobQuery query = managementService.createTimerJobQuery().timers().executable().orderByJobRetries().asc().orderByJobDuedate().desc();
        List<Job> jobs = query.list();
        assertEquals(3, jobs.size());
        assertEquals(2, jobs.get(0).getRetries());
        assertEquals(3, jobs.get(1).getRetries());
        assertEquals(3, jobs.get(2).getRetries());
        assertEquals(processInstanceIdTwo, jobs.get(0).getProcessInstanceId());
        assertEquals(processInstanceIdThree, jobs.get(1).getProcessInstanceId());
        assertEquals(processInstanceIdOne, jobs.get(2).getProcessInstanceId());
    }

    @Test
    public void testQueryInvalidSortingUsage() {
        try {
            managementService.createJobQuery().orderByJobId().list();
            fail();
        } catch (FlowableIllegalArgumentException e) {
            assertTextPresent("call asc() or desc() after using orderByXX()", e.getMessage());
        }
        try {
            managementService.createJobQuery().asc();
            fail();
        } catch (FlowableIllegalArgumentException e) {
            assertTextPresent("You should call any of the orderBy methods first before specifying a direction", e.getMessage());
        }
    }
}

