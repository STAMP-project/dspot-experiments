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


import PlanItemDefinitionType.TIMER_EVENT_LISTENER;
import PlanItemInstanceState.ACTIVE;
import PlanItemInstanceState.AVAILABLE;
import ScopeTypes.CMMN;
import java.util.Date;
import java.util.List;
import org.flowable.cmmn.api.repository.CmmnDeployment;
import org.flowable.cmmn.api.runtime.CaseInstance;
import org.flowable.cmmn.api.runtime.PlanItemInstance;
import org.flowable.cmmn.engine.test.impl.CmmnJobTestHelper;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.engine.impl.test.JobTestHelper;
import org.flowable.job.api.Job;
import org.flowable.task.api.Task;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Tijs Rademakers
 */
public class CmmnTimerTaskTest extends AbstractProcessEngineIntegrationTest {
    @Test
    public void testCmmnTimerTask() {
        Date startTime = setCmmnClockFixedToCurrentTime();
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testTimerInStage").start();
        Assert.assertEquals(1L, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceState(ACTIVE).count());
        PlanItemInstance planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceState(AVAILABLE).planItemDefinitionType(TIMER_EVENT_LISTENER).singleResult();
        Assert.assertNotNull(planItemInstance);
        Assert.assertEquals(1L, cmmnTaskService.createTaskQuery().count());
        List<Job> timerJobs = processEngineManagementService.createTimerJobQuery().scopeId(caseInstance.getId()).scopeType(CMMN).list();
        Assert.assertEquals(1, timerJobs.size());
        String timerJobId = timerJobs.get(0).getId();
        timerJobs = processEngineManagementService.createTimerJobQuery().scopeId(caseInstance.getId()).scopeType(CMMN).executable().list();
        Assert.assertEquals(0, timerJobs.size());
        AbstractProcessEngineIntegrationTest.processEngine.getProcessEngineConfiguration().getClock().setCurrentTime(new Date(((startTime.getTime()) + ((((3 * 60) * 60) * 1000) + 1))));
        timerJobs = processEngineManagementService.createTimerJobQuery().scopeId(caseInstance.getId()).scopeType(CMMN).executable().list();
        Assert.assertEquals(1, timerJobs.size());
        try {
            JobTestHelper.waitForJobExecutorToProcessAllJobsAndExecutableTimerJobs(AbstractProcessEngineIntegrationTest.processEngine.getProcessEngineConfiguration(), processEngineManagementService, 7000, 200, true);
            Assert.fail("should throw time limit exceeded");
        } catch (FlowableException e) {
            Assert.assertEquals("time limit of 7000 was exceeded", e.getMessage());
        }
        // Timer fires after 3 hours, so setting it to 3 hours + 1 second
        AbstractProcessEngineIntegrationTest.cmmnEngineConfiguration.getClock().setCurrentTime(new Date(((startTime.getTime()) + ((((3 * 60) * 60) * 1000) + 1))));
        timerJobs = cmmnManagementService.createTimerJobQuery().caseInstanceId(caseInstance.getId()).executable().list();
        Assert.assertEquals(1, timerJobs.size());
        Assert.assertEquals(timerJobId, timerJobs.get(0).getId());
        CmmnJobTestHelper.waitForJobExecutorToProcessAllJobs(AbstractProcessEngineIntegrationTest.cmmnEngineConfiguration, 7000L, 200L, true);
        // User task should be active after the timer has triggered
        List<Task> tasks = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).orderByTaskName().asc().list();
        Assert.assertEquals(2, tasks.size());
        Assert.assertEquals("A", tasks.get(0).getName());
        Assert.assertEquals("B", tasks.get(1).getName());
        AbstractProcessEngineIntegrationTest.cmmnEngineConfiguration.resetClock();
        resetClock();
        for (CmmnDeployment deployment : cmmnRepositoryService.createDeploymentQuery().list()) {
            cmmnRepositoryService.deleteDeployment(deployment.getId(), true);
        }
    }
}

