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
import PlanItemDefinitionType.USER_EVENT_LISTENER;
import PlanItemInstanceState.ACTIVE;
import PlanItemInstanceState.AVAILABLE;
import PlanItemInstanceState.ENABLED;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.flowable.cmmn.api.runtime.CaseInstance;
import org.flowable.cmmn.api.runtime.PlanItemInstance;
import org.flowable.cmmn.api.runtime.UserEventListenerInstance;
import org.flowable.cmmn.engine.test.CmmnDeployment;
import org.flowable.cmmn.engine.test.FlowableCmmnTestCase;
import org.flowable.task.api.Task;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Joram Barrez
 */
public class PlanItemInstanceQueryTest extends FlowableCmmnTestCase {
    protected String deploymentId;

    protected String caseDefinitionId;

    @Test
    public void testByCaseDefinitionId() {
        startInstances(5);
        Assert.assertEquals(20, cmmnRuntimeService.createPlanItemInstanceQuery().list().size());
    }

    @Test
    public void testByCaseInstanceId() {
        List<String> caseInstanceIds = startInstances(3);
        for (String caseInstanceId : caseInstanceIds) {
            Assert.assertEquals(4, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstanceId).list().size());
        }
    }

    @Test
    public void testByStageInstanceId() {
        startInstances(1);
        PlanItemInstance planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(STAGE).planItemInstanceName("Stage one").singleResult();
        Assert.assertEquals(2, cmmnRuntimeService.createPlanItemInstanceQuery().stageInstanceId(planItemInstance.getId()).count());
    }

    @Test
    public void testByPlanItemInstanceId() {
        startInstances(1);
        List<PlanItemInstance> planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().list();
        for (PlanItemInstance planItemInstance : planItemInstances) {
            Assert.assertEquals(1L, cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceId(planItemInstance.getId()).count());
        }
    }

    @Test
    public void testByElementId() {
        startInstances(4);
        Assert.assertEquals(4, cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceElementId("planItem3").list().size());
    }

    @Test
    public void testByName() {
        startInstances(9);
        Assert.assertEquals(9, cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceName("B").list().size());
    }

    @Test
    public void testByState() {
        startInstances(1);
        Assert.assertEquals(2, cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceState(ACTIVE).list().size());
        Assert.assertEquals(2, cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceStateActive().list().size());
        Assert.assertEquals(1, cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceState(AVAILABLE).list().size());
        Assert.assertEquals(1, cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceStateAvailable().list().size());
        Assert.assertEquals(1, cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceState(ENABLED).list().size());
        Assert.assertEquals(1, cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceStateEnabled().list().size());
    }

    @Test
    public void testByPlanItemDefinitionType() {
        startInstances(3);
        Assert.assertEquals(6, cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(HUMAN_TASK).list().size());
        Assert.assertEquals(6, cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(STAGE).list().size());
    }

    @Test
    public void testByPlanItemDefinitionTypes() {
        startInstances(2);
        Assert.assertEquals(8, cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionTypes(Arrays.asList(STAGE, HUMAN_TASK)).list().size());
    }

    @Test
    public void testByStateEnabled() {
        startInstances(4);
        List<PlanItemInstance> planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceStateEnabled().list();
        assertThat(planItemInstances).hasSize(4).extracting(PlanItemInstance::getName).containsOnly("B");
    }

    @Test
    public void testByStateDisabled() {
        startInstances(5);
        List<PlanItemInstance> planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceStateEnabled().list();
        cmmnRuntimeService.disablePlanItemInstance(planItemInstances.get(0).getId());
        cmmnRuntimeService.disablePlanItemInstance(planItemInstances.get(1).getId());
        assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceStateDisabled().list()).hasSize(2).extracting(PlanItemInstance::getName).containsOnly("B");
    }

    @Test
    public void testByStateAvailable() {
        startInstances(3);
        List<PlanItemInstance> planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceStateAvailable().orderByName().asc().list();
        assertThat(planItemInstances).hasSize(3).extracting(PlanItemInstance::getName).containsOnly("Stage two");
    }

    @Test
    public void testByStateActive() {
        startInstances(2);
        List<PlanItemInstance> planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceStateActive().orderByName().asc().list();
        assertThat(planItemInstances).hasSize(4).extracting(PlanItemInstance::getName).containsExactly("A", "A", "Stage one", "Stage one");
    }

    @Test
    public void testByStateAndType() {
        startInstances(3);
        Assert.assertEquals(3, cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceState(ACTIVE).planItemDefinitionType(HUMAN_TASK).list().size());
        Assert.assertEquals(3, cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceState(ENABLED).planItemDefinitionType(HUMAN_TASK).list().size());
    }

    @Test
    public void testByStateCompleted() {
        startInstances(4);
        List<Task> tasks = cmmnTaskService.createTaskQuery().list();
        cmmnTaskService.complete(tasks.get(0).getId());
        cmmnTaskService.complete(tasks.get(1).getId());
        cmmnTaskService.complete(tasks.get(3).getId());
        List<PlanItemInstance> planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceStateCompleted().ended().list();
        assertThat(planItemInstances).hasSize(3).extracting(PlanItemInstance::getName).containsOnly("A");
        // includeEnded should also return the same result
        assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceStateCompleted().includeEnded().list()).hasSize(3);
        // Without ended, should only return runtime plan item instances
        assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceStateCompleted().list()).hasSize(0);
    }

    @Test
    @CmmnDeployment
    public void testByStateTerminated() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testQueryByStateTerminated").start();
        List<PlanItemInstance> planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().ended().planItemInstanceStateTerminated().list();
        assertThat(planItemInstances).hasSize(0);
        // Completing the user event will terminate A/C/Stage for c
        UserEventListenerInstance userEventListenerInstance = cmmnRuntimeService.createUserEventListenerInstanceQuery().caseInstanceId(caseInstance.getId()).singleResult();
        cmmnRuntimeService.completeUserEventListenerInstance(userEventListenerInstance.getId());
        planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().ended().planItemInstanceStateTerminated().orderByName().asc().list();
        assertThat(planItemInstances).extracting(PlanItemInstance::getName).containsExactly("A", "C", "The Stage");
    }

    @Test
    public void testIncludeEnded() {
        startInstances(11);
        List<Task> tasks = cmmnTaskService.createTaskQuery().list();
        cmmnTaskService.complete(tasks.get(0).getId());
        cmmnTaskService.complete(tasks.get(1).getId());
        cmmnTaskService.complete(tasks.get(2).getId());
        cmmnTaskService.complete(tasks.get(3).getId());
        List<PlanItemInstance> planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceName("A").list();
        assertThat(planItemInstances).hasSize(7);// 11 - 4 (runtime only)

        planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceName("A").includeEnded().list();
        assertThat(planItemInstances).hasSize(11);
        planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceName("A").ended().list();
        assertThat(planItemInstances).hasSize(4);
    }

    @Test
    public void testCreatedBefore() {
        Date now = new Date();
        setClockTo(now);
        startInstances(3);
        setClockTo(new Date(((now.getTime()) + 20000)));
        startInstances(4);
        List<PlanItemInstance> planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceName("A").planItemDefinitionType(HUMAN_TASK).planItemInstanceCreatedBefore(new Date(((now.getTime()) + 10000))).list();
        assertThat(planItemInstances).hasSize(3);
        planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceName("A").planItemDefinitionType(HUMAN_TASK).planItemInstanceCreatedBefore(new Date(((now.getTime()) + 30000))).list();
        assertThat(planItemInstances).hasSize(7);
    }

    @Test
    public void testCreatedAfter() {
        Date now = new Date();
        setClockTo(now);
        startInstances(2);
        setClockTo(new Date(((now.getTime()) + 20000)));
        startInstances(8);
        List<PlanItemInstance> planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceName("A").planItemDefinitionType(HUMAN_TASK).planItemInstanceCreatedAfter(new Date(((now.getTime()) - 10000))).list();
        assertThat(planItemInstances).hasSize(10);
        planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceName("A").planItemDefinitionType(HUMAN_TASK).planItemInstanceCreatedAfter(new Date(((now.getTime()) + 10000))).list();
        assertThat(planItemInstances).hasSize(8);
    }

    @Test
    public void testLastAvailableBeforeAndAfter() {
        Date now = new Date();
        setClockTo(now);
        startInstances(3);
        setClockTo(new Date(((now.getTime()) + 20000)));
        startInstances(5);
        List<PlanItemInstance> planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceName("A").planItemDefinitionType(HUMAN_TASK).planItemInstanceLastAvailableAfter(new Date(((now.getTime()) - 10000))).list();
        assertThat(planItemInstances).hasSize(8);
        planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceName("A").planItemDefinitionType(HUMAN_TASK).planItemInstanceLastAvailableBefore(new Date(((now.getTime()) - 10000))).list();
        assertThat(planItemInstances).hasSize(0);
        planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceName("A").planItemDefinitionType(HUMAN_TASK).planItemInstanceLastAvailableAfter(new Date(((now.getTime()) + 10000))).list();
        assertThat(planItemInstances).hasSize(5);
        planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceName("A").planItemDefinitionType(HUMAN_TASK).planItemInstanceLastAvailableBefore(new Date(((now.getTime()) + 10000))).list();
        assertThat(planItemInstances).hasSize(3);
    }

    @Test
    public void testLastEnabledBeforeAndAfter() {
        Date now = new Date();
        setClockTo(now);
        startInstances(2);
        setClockTo(((now.getTime()) + 10000));
        startInstances(3);
        // Before
        List<PlanItemInstance> planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceLastEnabledBefore(new Date(((now.getTime()) + 30000))).list();
        assertThat(planItemInstances).hasSize(5);
        planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceLastEnabledBefore(new Date(((now.getTime()) + 5000))).list();
        assertThat(planItemInstances).hasSize(2);
        planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceLastEnabledBefore(new Date(((now.getTime()) - 1000))).list();
        assertThat(planItemInstances).hasSize(0);
        // After
        planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceLastEnabledAfter(new Date(((now.getTime()) - 5000))).list();
        assertThat(planItemInstances).hasSize(5);
        planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceLastEnabledAfter(new Date(((now.getTime()) + 250000))).list();
        assertThat(planItemInstances).hasSize(0);
    }

    @Test
    public void testLastDisabledBeforeAndAfter() {
        Date now = new Date();
        setClockTo(now);
        startInstances(3);
        String planItemInstanceId = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceName("B").planItemInstanceStateEnabled().listPage(0, 1).get(0).getId();
        cmmnRuntimeService.disablePlanItemInstance(planItemInstanceId);
        setClockTo(((now.getTime()) + 10000));
        cmmnRuntimeService.disablePlanItemInstance(cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceName("B").planItemInstanceStateEnabled().listPage(0, 1).get(0).getId());
        // Before
        assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceLastDisabledBefore(new Date(((now.getTime()) + 20000))).list()).hasSize(2);
        assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceLastDisabledBefore(new Date(((now.getTime()) + 5000))).list()).hasSize(1);
        assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceLastDisabledBefore(new Date(((now.getTime()) - 5000))).list()).hasSize(0);
        // After
        assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceLastDisabledAfter(new Date(now.getTime())).list()).hasSize(2);
        assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceLastDisabledAfter(new Date(((now.getTime()) + 5000))).list()).hasSize(1);
        assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceLastDisabledAfter(new Date(((now.getTime()) + 11000))).list()).hasSize(0);
        // Re-enable and disable
        PlanItemInstance planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceId(planItemInstanceId).singleResult();
        Date lastEnabledTime = planItemInstance.getLastEnabledTime();
        assertThat(lastEnabledTime).isNotNull();
        setClockTo(((now.getTime()) + 30000));
        cmmnRuntimeService.enablePlanItemInstance(planItemInstanceId);
        cmmnRuntimeService.disablePlanItemInstance(planItemInstanceId);
        planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceId(planItemInstanceId).singleResult();
        assertThat(planItemInstance.getLastEnabledTime()).isNotEqualTo(lastEnabledTime);
        // Recheck queries
        assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceLastDisabledBefore(new Date(((now.getTime()) + 20000))).list()).hasSize(1);
        assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceLastDisabledBefore(new Date(((now.getTime()) + 5000))).list()).hasSize(0);
        assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceLastDisabledAfter(new Date(now.getTime())).list()).hasSize(2);
        assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceLastDisabledAfter(new Date(((now.getTime()) + 15000))).list()).hasSize(1);
        assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceLastDisabledAfter(new Date(((now.getTime()) + 35000))).list()).hasSize(0);
    }

    @Test
    public void testLastStartedBeforeAndAfter() {
        Date now = new Date();
        setClockTo(now);
        startInstances(4);
        assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceLastStartedAfter(new Date(((now.getTime()) - 1000))).list()).hasSize(8);
        assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceLastStartedAfter(new Date(((now.getTime()) + 1000))).list()).hasSize(0);
        assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceLastStartedBefore(new Date(((now.getTime()) + 1000))).list()).hasSize(8);
        assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceLastStartedBefore(new Date(((now.getTime()) - 1000))).list()).hasSize(0);
        // Starting an enabled planitem
        setClockTo(((now.getTime()) + 10000));
        cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceName("B").listPage(0, 2).forEach(( p) -> cmmnRuntimeService.startPlanItemInstance(p.getId()));
        assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceLastStartedAfter(new Date(((now.getTime()) - 1000))).list()).hasSize(10);
        assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceLastStartedAfter(new Date(((now.getTime()) + 5000))).list()).hasSize(2);
        assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceLastStartedAfter(new Date(((now.getTime()) + 15000))).list()).hasSize(0);
        assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceLastStartedBefore(new Date(((now.getTime()) + 1000))).list()).hasSize(8);
        assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceLastStartedBefore(new Date(((now.getTime()) + 15000))).list()).hasSize(10);
        assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceLastStartedBefore(new Date(((now.getTime()) - 1000))).list()).hasSize(0);
    }

    @Test
    public void testCompletedBeforeAndAfter() {
        startInstances(5);
        Date now = new Date();
        setClockTo(now);
        List<Task> tasks = cmmnTaskService.createTaskQuery().listPage(0, 2);
        setClockTo(((now.getTime()) + 10000));
        cmmnTaskService.complete(tasks.get(0).getId());
        setClockTo(((now.getTime()) + 20000));
        cmmnTaskService.complete(tasks.get(1).getId());
        // Completed
        assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceCompletedBefore(new Date(((now.getTime()) + 30000))).list()).hasSize(0);
        assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceCompletedBefore(new Date(((now.getTime()) + 30000))).includeEnded().list()).hasSize(2);
        assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceCompletedBefore(new Date(((now.getTime()) + 15000))).includeEnded().list()).hasSize(1);
        assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceCompletedAfter(new Date(now.getTime())).list()).hasSize(0);
        assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceCompletedAfter(new Date(now.getTime())).includeEnded().list()).hasSize(2);
        assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceCompletedAfter(new Date(now.getTime())).ended().list()).hasSize(2);
        assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceCompletedAfter(new Date(((now.getTime()) + 15000))).includeEnded().list()).hasSize(1);
        // Same queries, but with endedBefore/After
        assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceEndedBefore(new Date(((now.getTime()) + 30000))).list()).hasSize(0);
        assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceEndedBefore(new Date(((now.getTime()) + 30000))).includeEnded().list()).hasSize(2);
        assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceEndedBefore(new Date(((now.getTime()) + 15000))).includeEnded().list()).hasSize(1);
        assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceEndedAfter(new Date(now.getTime())).list()).hasSize(0);
        assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceEndedAfter(new Date(now.getTime())).includeEnded().list()).hasSize(2);
        assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceEndedAfter(new Date(now.getTime())).ended().list()).hasSize(2);
        assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceEndedAfter(new Date(((now.getTime()) + 15000))).includeEnded().list()).hasSize(1);
    }

    @Test
    public void testLastOccurredBeforeAndAfter() {
        Date now = new Date();
        setClockTo(now);
        startInstances(2);
        cmmnRuntimeService.completeStagePlanItemInstance(cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceName("Stage one").listPage(0, 1).get(0).getId(), true);
        setClockTo(((now.getTime()) + 10000));
        cmmnRuntimeService.completeStagePlanItemInstance(cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceName("Stage one").listPage(0, 1).get(0).getId(), true);
        // Occurred (milestone)
        assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceOccurredAfter(new Date(((now.getTime()) - 1000))).list()).hasSize(0);
        assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceOccurredAfter(new Date(((now.getTime()) - 1000))).includeEnded().list()).hasSize(2);
        assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceOccurredAfter(new Date(((now.getTime()) + 1000))).includeEnded().list()).hasSize(1);
        assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceOccurredAfter(new Date(((now.getTime()) + 15000))).includeEnded().list()).hasSize(0);
        assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceOccurredBefore(new Date(((now.getTime()) + 20000))).includeEnded().list()).hasSize(2);
        assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceOccurredBefore(new Date(((now.getTime()) + 5000))).includeEnded().list()).hasSize(1);
        assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceOccurredBefore(new Date(((now.getTime()) - 1000))).includeEnded().list()).hasSize(0);
    }

    @Test
    @CmmnDeployment
    public void testExitBeforeAndAfter() {
        Date now = new Date();
        setClockTo(now);
        cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testQueryByStateTerminated").start();
        UserEventListenerInstance userEventListenerInstance = cmmnRuntimeService.createUserEventListenerInstanceQuery().singleResult();
        cmmnRuntimeService.completeUserEventListenerInstance(userEventListenerInstance.getId());
        setClockTo(((now.getTime()) + 10000));
        cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testQueryByStateTerminated").start();
        userEventListenerInstance = cmmnRuntimeService.createUserEventListenerInstanceQuery().singleResult();
        cmmnRuntimeService.completeUserEventListenerInstance(userEventListenerInstance.getId());
        // Terminated before/after
        assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().includeEnded().planItemInstanceExitAfter(new Date(((now.getTime()) - 1000))).list()).hasSize(6);
        assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().includeEnded().planItemInstanceExitAfter(new Date(((now.getTime()) + 1000))).list()).hasSize(3);
        assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().includeEnded().planItemInstanceExitAfter(new Date(((now.getTime()) + 20000))).list()).hasSize(0);
        assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().includeEnded().planItemInstanceExitBefore(new Date(((now.getTime()) - 1000))).list()).hasSize(0);
        assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().includeEnded().planItemInstanceExitBefore(new Date(((now.getTime()) + 1000))).list()).hasSize(3);
        assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().includeEnded().planItemInstanceExitBefore(new Date(((now.getTime()) + 20000))).list()).hasSize(6);
        // Ended before/after
        assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().includeEnded().planItemInstanceEndedAfter(new Date(((now.getTime()) - 1000))).list()).hasSize(8);// + 2 for user event listener

        assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().includeEnded().planItemInstanceEndedAfter(new Date(((now.getTime()) + 1000))).list()).hasSize(4);
        assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().includeEnded().planItemInstanceEndedAfter(new Date(((now.getTime()) + 20000))).list()).hasSize(0);
        assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().includeEnded().planItemInstanceEndedBefore(new Date(((now.getTime()) - 1000))).list()).hasSize(0);
        assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().includeEnded().planItemInstanceEndedBefore(new Date(((now.getTime()) + 1000))).list()).hasSize(4);
        assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().includeEnded().planItemInstanceEndedBefore(new Date(((now.getTime()) + 20000))).list()).hasSize(8);
        assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().includeEnded().planItemDefinitionType(USER_EVENT_LISTENER).planItemInstanceEndedAfter(new Date(((now.getTime()) - 1000))).list()).hasSize(2);
    }

    @Test
    @CmmnDeployment
    public void testTerminateBeforeAndAfter() {
        Date now = new Date();
        setClockTo(now);
        cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testTerminate").start();
        PlanItemInstance stagePlanItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(STAGE).planItemInstanceName("The Stage").singleResult();
        cmmnRuntimeService.terminatePlanItemInstance(stagePlanItemInstance.getId());
        assertThat(cmmnTaskService.createTaskQuery().taskName("D").singleResult()).isNotNull();
        // Terminated
        assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().includeEnded().planItemInstanceTerminatedBefore(new Date(((now.getTime()) + 1000))).list()).hasSize(2);// 2 -> stage and C

        assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().includeEnded().planItemInstanceTerminatedBefore(new Date(((now.getTime()) - 1000))).list()).hasSize(0);
        assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().includeEnded().planItemInstanceTerminatedAfter(new Date(((now.getTime()) + 1000))).list()).hasSize(0);
        assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().includeEnded().planItemInstanceTerminatedAfter(new Date(((now.getTime()) - 1000))).list()).hasSize(2);
        // Ended
        assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().includeEnded().planItemInstanceEndedBefore(new Date(((now.getTime()) + 1000))).list()).hasSize(2);// 2 -> stage and C

        assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().includeEnded().planItemInstanceEndedBefore(new Date(((now.getTime()) - 1000))).list()).hasSize(0);
        assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().includeEnded().planItemInstanceEndedAfter(new Date(((now.getTime()) + 1000))).list()).hasSize(0);
        assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().includeEnded().planItemInstanceEndedAfter(new Date(((now.getTime()) - 1000))).list()).hasSize(2);
    }
}

