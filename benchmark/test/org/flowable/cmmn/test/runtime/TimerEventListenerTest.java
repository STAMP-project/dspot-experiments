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
package org.flowable.cmmn.test.runtime;


import PlanItemDefinitionType.HUMAN_TASK;
import PlanItemDefinitionType.STAGE;
import PlanItemDefinitionType.TIMER_EVENT_LISTENER;
import PlanItemInstanceState.ACTIVE;
import PlanItemInstanceState.AVAILABLE;
import PlanItemInstanceState.COMPLETED;
import PlanItemInstanceState.UNAVAILABLE;
import ScopeTypes.CMMN;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.flowable.cmmn.api.runtime.CaseInstance;
import org.flowable.cmmn.api.runtime.PlanItemInstance;
import org.flowable.cmmn.engine.test.CmmnDeployment;
import org.flowable.cmmn.engine.test.FlowableCmmnTestCase;
import org.flowable.cmmn.engine.test.impl.CmmnJobTestHelper;
import org.flowable.cmmn.model.HumanTask;
import org.flowable.cmmn.model.Stage;
import org.flowable.cmmn.model.TimerEventListener;
import org.flowable.job.api.Job;
import org.flowable.task.api.Task;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Joram Barrez
 */
public class TimerEventListenerTest extends FlowableCmmnTestCase {
    @Test
    @CmmnDeployment
    public void testTimerExpressionDuration() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testTimerExpression").start();
        Assert.assertNotNull(caseInstance);
        Assert.assertEquals(1, cmmnRuntimeService.createCaseInstanceQuery().count());
        Assert.assertNotNull(cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(TIMER_EVENT_LISTENER).planItemInstanceStateAvailable().singleResult());
        Assert.assertNotNull(cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(HUMAN_TASK).planItemInstanceStateAvailable().singleResult());
        Assert.assertEquals(1L, cmmnManagementService.createTimerJobQuery().count());
        Assert.assertEquals(1L, cmmnManagementService.createTimerJobQuery().scopeId(caseInstance.getId()).scopeType(CMMN).count());
        // User task should not be active before the timer triggers
        Assert.assertEquals(0L, cmmnTaskService.createTaskQuery().count());
        Job timerJob = cmmnManagementService.createTimerJobQuery().scopeDefinitionId(caseInstance.getCaseDefinitionId()).singleResult();
        Assert.assertNotNull(timerJob);
        cmmnManagementService.moveTimerToExecutableJob(timerJob.getId());
        cmmnManagementService.executeJob(timerJob.getId());
        // User task should be active after the timer has triggered
        Assert.assertEquals(1L, cmmnTaskService.createTaskQuery().count());
    }

    /**
     * Similar test as #testTimerExpressionDuration but with the real async executor,
     * instead of manually triggering the timer.
     */
    @Test
    @CmmnDeployment
    public void testTimerExpressionDurationWithRealAsyncExeutor() {
        Date startTime = setClockFixedToCurrentTime();
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testTimerExpression").start();
        PlanItemInstance planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceState(AVAILABLE).planItemDefinitionType(TIMER_EVENT_LISTENER).singleResult();
        Assert.assertNotNull(planItemInstance);
        // User task should not be active before the timer triggers
        Assert.assertEquals(0L, cmmnTaskService.createTaskQuery().count());
        // Timer fires after 1 hour, so setting it to 1 hours + 1 second
        setClockTo(new Date(((startTime.getTime()) + (((60 * 60) * 1000) + 1))));
        CmmnJobTestHelper.waitForJobExecutorToProcessAllJobs(cmmnEngineConfiguration, 7000L, 200L, true);
        // User task should be active after the timer has triggered
        Assert.assertEquals(1L, cmmnTaskService.createTaskQuery().count());
    }

    @Test
    @CmmnDeployment
    public void testStageAfterTimer() {
        Date startTime = setClockFixedToCurrentTime();
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testStageAfterTimerEventListener").start();
        PlanItemInstance planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceState(AVAILABLE).planItemDefinitionType(TIMER_EVENT_LISTENER).singleResult();
        Assert.assertNotNull(planItemInstance);
        Assert.assertEquals(0L, cmmnTaskService.createTaskQuery().count());
        // Timer fires after 1 day, so setting it to 1 day + 1 second
        setClockTo(new Date(((startTime.getTime()) + ((((24 * 60) * 60) * 1000) + 1))));
        CmmnJobTestHelper.waitForJobExecutorToProcessAllJobs(cmmnEngineConfiguration, 7000L, 200L, true);
        // User task should be active after the timer has triggered
        Assert.assertEquals(2L, cmmnTaskService.createTaskQuery().count());
    }

    @Test
    @CmmnDeployment
    public void testTimerInStage() {
        Date startTime = setClockFixedToCurrentTime();
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testTimerInStage").start();
        Assert.assertEquals(1L, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceState(ACTIVE).count());
        PlanItemInstance planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceState(AVAILABLE).planItemDefinitionType(TIMER_EVENT_LISTENER).singleResult();
        Assert.assertNotNull(planItemInstance);
        Assert.assertEquals(1L, cmmnTaskService.createTaskQuery().count());
        // Timer fires after 3 hours, so setting it to 3 hours + 1 second
        setClockTo(new Date(((startTime.getTime()) + ((((3 * 60) * 60) * 1000) + 1))));
        CmmnJobTestHelper.waitForJobExecutorToProcessAllJobs(cmmnEngineConfiguration, 7000L, 200L, true);
        // User task should be active after the timer has triggered
        List<Task> tasks = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).orderByTaskName().asc().list();
        Assert.assertEquals(2, tasks.size());
        Assert.assertEquals("A", tasks.get(0).getName());
        Assert.assertEquals("B", tasks.get(1).getName());
    }

    @Test
    @CmmnDeployment
    public void testExitPlanModelOnTimerOccurrence() {
        Date startTime = setClockFixedToCurrentTime();
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testStageExitOnTimerOccurrence").start();
        Assert.assertEquals(3L, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceState(ACTIVE).planItemDefinitionType(HUMAN_TASK).count());
        Assert.assertEquals(1L, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceState(AVAILABLE).planItemDefinitionType(TIMER_EVENT_LISTENER).count());
        Assert.assertEquals(1L, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceState(ACTIVE).planItemDefinitionType(STAGE).count());
        Assert.assertEquals(4L, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceState(ACTIVE).count());
        // Timer fires after 24 hours, so setting it to 24 hours + 1 second
        setClockTo(new Date(((startTime.getTime()) + ((((24 * 60) * 60) * 1000) + 1))));
        CmmnJobTestHelper.waitForJobExecutorToProcessAllJobs(cmmnEngineConfiguration, 7000L, 200L, true);
        Assert.assertEquals(0L, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).count());
        Assert.assertEquals(0L, cmmnRuntimeService.createCaseInstanceQuery().count());
    }

    @Test
    @CmmnDeployment
    public void testDateExpression() {
        // Timer will fire on 2017-12-05T10:00
        // So moving the clock to the day before
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2017);
        calendar.set(Calendar.MONTH, 12);
        calendar.set(Calendar.DAY_OF_MONTH, 4);
        calendar.set(Calendar.HOUR, 11);
        calendar.set(Calendar.MINUTE, 0);
        Date dayBefore = calendar.getTime();
        setClockTo(dayBefore);
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testDateExpression").start();
        Assert.assertEquals(1L, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceState(AVAILABLE).planItemDefinitionType(TimerEventListener.class.getSimpleName().toLowerCase()).count());
        Assert.assertEquals(2L, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceState(AVAILABLE).planItemDefinitionType(Stage.class.getSimpleName().toLowerCase()).count());
        Assert.assertEquals(0L, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceState(ACTIVE).planItemDefinitionType(Stage.class.getSimpleName().toLowerCase()).count());
        Assert.assertEquals(0L, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceState(ACTIVE).planItemDefinitionType(HumanTask.class.getSimpleName().toLowerCase()).count());
        Assert.assertEquals(1L, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceState(AVAILABLE).planItemDefinitionType(HumanTask.class.getSimpleName().toLowerCase()).count());
        Assert.assertEquals(0L, cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).count());
        setClockTo(new Date(((dayBefore.getTime()) + (((24 * 60) * 60) * 1000))));
        CmmnJobTestHelper.waitForJobExecutorToProcessAllJobs(cmmnEngineConfiguration, 7000L, 200L, true);
        Assert.assertEquals(0L, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceState(ACTIVE).planItemDefinitionType(TimerEventListener.class.getSimpleName().toLowerCase()).count());
        List<Task> tasks = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).list();
        Assert.assertEquals(3, tasks.size());
        for (Task task : tasks) {
            cmmnTaskService.complete(task.getId());
        }
        assertCaseInstanceEnded(caseInstance);
    }

    @Test
    @CmmnDeployment
    public void testTimerWithBeanExpression() {
        Date startTime = setClockFixedToCurrentTime();
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testBean").variable("startTime", startTime).start();
        Assert.assertEquals(2L, cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).count());
        Assert.assertEquals(2L, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceState(ACTIVE).planItemDefinitionType(Stage.class.getSimpleName().toLowerCase()).count());
        Assert.assertEquals(2L, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceState(ACTIVE).planItemDefinitionType(HumanTask.class.getSimpleName().toLowerCase()).count());
        setClockTo(new Date(((startTime.getTime()) + (((2 * 60) * 60) * 1000))));
        CmmnJobTestHelper.waitForJobExecutorToProcessAllJobs(cmmnEngineConfiguration, 7000L, 200L, true);
        Task task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
        Assert.assertEquals("A", task.getName());
        cmmnTaskService.complete(task.getId());
        assertCaseInstanceEnded(caseInstance);
    }

    @Test
    @CmmnDeployment
    public void testTimerStartTrigger() {
        // Completing the stage will be the start trigger for the timer.
        // The timer event will exit the whole plan model
        Date startTime = setClockFixedToCurrentTime();
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testStartTrigger").start();
        Assert.assertEquals(1L, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemDefinitionType(TimerEventListener.class.getSimpleName().toLowerCase()).count());
        Task task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
        Assert.assertEquals("A", task.getName());
        cmmnTaskService.complete(task.getId());
        task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
        Assert.assertEquals("B", task.getName());
        cmmnTaskService.complete(task.getId());
        task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
        Assert.assertEquals("C", task.getName());
        Assert.assertEquals(1L, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceState(AVAILABLE).planItemDefinitionType(TimerEventListener.class.getSimpleName().toLowerCase()).count());
        setClockTo(new Date(((startTime.getTime()) + (((3 * 60) * 60) * 1000))));
        CmmnJobTestHelper.waitForJobExecutorToProcessAllJobs(cmmnEngineConfiguration, 7000L, 200L, true);
        assertCaseInstanceEnded(caseInstance);
    }

    @Test
    @CmmnDeployment
    public void testExitNestedStageThroughTimer() {
        Date startTime = setClockFixedToCurrentTime();
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testExitNestedStageThroughTimer").start();
        Assert.assertEquals(3L, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemDefinitionType(STAGE).count());
        Task task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
        Assert.assertEquals("The task", task.getName());
        setClockTo(new Date(((startTime.getTime()) + (((5 * 60) * 60) * 1000))));
        CmmnJobTestHelper.waitForJobExecutorToProcessAllJobs(cmmnEngineConfiguration, 10000L, 100L, true);
        Assert.assertEquals(0L, cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).count());
        assertCaseInstanceEnded(caseInstance);
    }

    @Test
    @CmmnDeployment
    public void timerActivatesAndExitStages() {
        Date startTime = setClockFixedToCurrentTime();
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("timerActivatesAndExitStages").start();
        List<Task> tasks = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).orderByTaskName().asc().list();
        Assert.assertEquals(2, tasks.size());
        Assert.assertEquals("A", tasks.get(0).getName());
        Assert.assertEquals("B", tasks.get(1).getName());
        Assert.assertEquals(0L, cmmnManagementService.createTimerJobQuery().count());
        // Completing A activates the stage and the timer event listener
        cmmnTaskService.complete(tasks.get(0).getId());
        cmmnTaskService.complete(tasks.get(1).getId());
        // Timer event listener created a timer job
        Assert.assertEquals(1L, cmmnManagementService.createTimerJobQuery().count());
        tasks = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).orderByTaskName().asc().list();
        Assert.assertEquals(3, tasks.size());
        Assert.assertEquals("Stage 1 task", tasks.get(0).getName());
        Assert.assertEquals("Stage 3 task 1", tasks.get(1).getName());
        Assert.assertEquals("Stage 3 task 2", tasks.get(2).getName());
        // Timer is set to 10 hours
        setClockTo(new Date(((startTime.getTime()) + (((11 * 60) * 60) * 1000))));
        CmmnJobTestHelper.waitForJobExecutorToProcessAllJobs(cmmnEngineConfiguration, 10000L, 100L, true);
        tasks = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).orderByTaskName().asc().list();
        Assert.assertEquals(2, tasks.size());
        Assert.assertEquals("Stage 1 task", tasks.get(0).getName());
        Assert.assertEquals("Stage 2 task", tasks.get(1).getName());
        for (Task task : tasks) {
            cmmnTaskService.complete(task.getId());
        }
        assertCaseInstanceEnded(caseInstance);
    }

    @Test
    @CmmnDeployment
    public void testTimerWithAvailableCondition() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testTimerExpression").start();
        Assert.assertEquals(0L, cmmnManagementService.createTimerJobQuery().count());
        // Setting the variable should make the timer available and create the timer job
        cmmnRuntimeService.setVariable(caseInstance.getId(), "timerVar", true);
        Assert.assertEquals(1L, cmmnManagementService.createTimerJobQuery().count());
        PlanItemInstance timerPlanItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(TIMER_EVENT_LISTENER).singleResult();
        Assert.assertEquals(AVAILABLE, timerPlanItemInstance.getState());
        // Setting the variable to false again will dismiss the timer
        cmmnRuntimeService.setVariable(caseInstance.getId(), "timerVar", false);
        Assert.assertEquals(0L, cmmnManagementService.createTimerJobQuery().count());
        timerPlanItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(TIMER_EVENT_LISTENER).singleResult();
        Assert.assertEquals(UNAVAILABLE, timerPlanItemInstance.getState());
        cmmnRuntimeService.setVariable(caseInstance.getId(), "timerVar", true);
        Assert.assertEquals(1L, cmmnManagementService.createTimerJobQuery().count());
        timerPlanItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(TIMER_EVENT_LISTENER).singleResult();
        Assert.assertEquals(AVAILABLE, timerPlanItemInstance.getState());
        // Execute the job
        Job timerJob = cmmnManagementService.createTimerJobQuery().scopeDefinitionId(caseInstance.getCaseDefinitionId()).singleResult();
        Assert.assertNotNull(timerJob);
        cmmnManagementService.moveTimerToExecutableJob(timerJob.getId());
        cmmnManagementService.executeJob(timerJob.getId());
        timerPlanItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(TIMER_EVENT_LISTENER).includeEnded().singleResult();
        Assert.assertEquals(COMPLETED, timerPlanItemInstance.getState());
    }
}

