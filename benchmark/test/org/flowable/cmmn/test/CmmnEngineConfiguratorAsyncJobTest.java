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
package org.flowable.cmmn.test;


import JobServiceConfiguration.JOB_EXECUTION_SCOPE_CMMN;
import java.util.Date;
import java.util.List;
import org.flowable.cmmn.engine.CmmnEngine;
import org.flowable.cmmn.engine.test.impl.CmmnJobTestHelper;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.impl.test.JobTestHelper;
import org.flowable.job.api.Job;
import org.flowable.job.service.impl.asyncexecutor.AsyncExecutor;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Joram Barrez
 */
public class CmmnEngineConfiguratorAsyncJobTest {
    private ProcessEngine processEngine;

    private CmmnEngine cmmnEngine;

    @Test
    public void testSharedAsyncExecutor() throws Exception {
        // The async executor should be the same instance
        AsyncExecutor processEngineAsyncExecutor = processEngine.getProcessEngineConfiguration().getAsyncExecutor();
        AsyncExecutor cmmnEngineAsyncExecutor = cmmnEngine.getCmmnEngineConfiguration().getAsyncExecutor();
        Assert.assertNotNull(processEngineAsyncExecutor);
        Assert.assertNotNull(cmmnEngineAsyncExecutor);
        // Contrary to the asyncHistoryExecutor, the async executors are not shared between the engines (by default)
        Assert.assertNotSame(processEngineAsyncExecutor, cmmnEngineAsyncExecutor);
        Assert.assertNull(processEngineAsyncExecutor.getJobServiceConfiguration().getJobExecutionScope());
        Assert.assertEquals(JOB_EXECUTION_SCOPE_CMMN, cmmnEngineAsyncExecutor.getJobServiceConfiguration().getJobExecutionScope());
        // Deploy and start test processes/cases
        // Trigger one plan item instance to start the process
        processEngine.getRepositoryService().createDeployment().addClasspathResource("org/flowable/cmmn/test/CmmnEngineConfiguratorAsyncJobTest.taskAndTimer.bpmn20.xml").deploy();
        cmmnEngine.getCmmnRepositoryService().createDeployment().addClasspathResource("org/flowable/cmmn/test/CmmnEngineConfiguratorAsyncJobTest.processAndTimer.cmmn.xml").deploy();
        // Starting the case instance starts the process task. The process has an async job at the beginning
        cmmnEngine.getCmmnRuntimeService().createCaseInstanceBuilder().caseDefinitionKey("timerAndProcess").start();
        Job job = processEngine.getManagementService().createJobQuery().singleResult();
        Assert.assertNull(job.getScopeType());
        JobTestHelper.waitForJobExecutorToProcessAllJobs(processEngine.getProcessEngineConfiguration(), processEngine.getManagementService(), 10000L, 100L);
        // There should now be two timers, one for the case and one for the process
        List<Job> timerJobs = processEngine.getManagementService().createTimerJobQuery().list();
        timerJobs.forEach(( timerJob) -> {
            if ((timerJob.getScopeId()) != null) {
                // cmmn
                assertEquals(JobServiceConfiguration.JOB_EXECUTION_SCOPE_CMMN, timerJob.getScopeType());
            } else {
                assertNull(timerJob.getScopeType());
            }
            processEngine.getManagementService().moveTimerToExecutableJob(timerJob.getId());
        });
        // Can't use the JobTestHelper's, so manually starting the executors
        processEngineAsyncExecutor.start();
        cmmnEngineAsyncExecutor.start();
        try {
            long startTime = new Date().getTime();
            while (((processEngine.getManagementService().createJobQuery().count()) > 0) && (((new Date().getTime()) - startTime) > 10000)) {
                Thread.sleep(100L);
            } 
        } finally {
            processEngineAsyncExecutor.shutdown();
            cmmnEngineAsyncExecutor.shutdown();
        }
        // There should be one user task which is async (from the case)
        job = processEngine.getManagementService().createJobQuery().singleResult();
        Assert.assertEquals(JOB_EXECUTION_SCOPE_CMMN, job.getScopeType());
        CmmnJobTestHelper.waitForJobExecutorToProcessAllJobs(cmmnEngine, 10000L, 100L, true);
        // There should be two user tasks now: one after the timer of the case and one after the timer of the process
        Assert.assertEquals(2, processEngine.getTaskService().createTaskQuery().count());
        Assert.assertEquals(2, cmmnEngine.getCmmnTaskService().createTaskQuery().count());
    }
}

