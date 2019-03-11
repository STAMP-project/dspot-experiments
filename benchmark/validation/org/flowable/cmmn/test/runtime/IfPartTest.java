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


import PlanItemInstanceState.AVAILABLE;
import java.io.Serializable;
import java.util.List;
import org.flowable.cmmn.api.runtime.CaseInstance;
import org.flowable.cmmn.api.runtime.PlanItemInstance;
import org.flowable.cmmn.engine.test.CmmnDeployment;
import org.flowable.cmmn.engine.test.FlowableCmmnTestCase;
import org.flowable.common.engine.impl.util.CollectionUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Joram Barrez
 */
public class IfPartTest extends FlowableCmmnTestCase {
    @Test
    @CmmnDeployment
    public void testIfPartOnly() {
        // Case 1 : Passing variable from the start
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testIfPartOnly").variable("variable", true).start();
        Assert.assertEquals(2, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceStateActive().count());
        // Case 2 : Passing variable after case instance start
        caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testIfPartOnly").start();
        List<PlanItemInstance> planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceStateActive().list();
        Assert.assertEquals(1, planItemInstances.size());
        cmmnRuntimeService.setVariables(caseInstance.getId(), CollectionUtil.singletonMap("variable", true));
        Assert.assertEquals(2, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceStateActive().count());
        // Case 3 : Completing A after start should end the case instance
        caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testIfPartOnly").start();
        planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceStateActive().list();
        Assert.assertEquals(1, planItemInstances.size());
        Assert.assertEquals("A", planItemInstances.get(0).getName());
        cmmnRuntimeService.triggerPlanItemInstance(planItemInstances.get(0).getId());
        // Be should remain in the available state, until the variable is set
        planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceStateAvailable().list();
        Assert.assertEquals(1, planItemInstances.size());
        Assert.assertEquals("B", planItemInstances.get(0).getName());
        cmmnRuntimeService.setVariables(caseInstance.getId(), CollectionUtil.singletonMap("variable", true));
        planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceStateActive().list();
        Assert.assertEquals(1, planItemInstances.size());
        Assert.assertEquals("B", planItemInstances.get(0).getName());
        // Completing B ends the case instance
        cmmnRuntimeService.triggerPlanItemInstance(planItemInstances.get(0).getId());
        assertCaseInstanceEnded(caseInstance);
    }

    @Test
    @CmmnDeployment
    public void testOnAndIfPart() {
        // Passing the variable for the if part condition at start
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testSimpleCondition").variable("conditionVariable", true).start();
        List<PlanItemInstance> planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceStateActive().list();
        Assert.assertEquals(1, planItemInstances.size());
        Assert.assertEquals("A", planItemInstances.get(0).getName());
        // Completing plan item A should trigger B
        cmmnRuntimeService.triggerPlanItemInstance(planItemInstances.get(0).getId());
        planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceStateActive().list();
        Assert.assertEquals(1, planItemInstances.size());
        Assert.assertEquals("B", planItemInstances.get(0).getName());
        // Completing B should end the case instance
        cmmnRuntimeService.triggerPlanItemInstance(planItemInstances.get(0).getId());
        assertCaseInstanceEnded(caseInstance);
    }

    @Test
    @CmmnDeployment
    public void testIfPartConditionTriggerOnSetVariables() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testSimpleCondition").start();
        List<PlanItemInstance> planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceStateActive().list();
        Assert.assertEquals(1, planItemInstances.size());
        Assert.assertEquals("A", planItemInstances.get(0).getName());
        // Completing plan item A should not trigger B
        cmmnRuntimeService.triggerPlanItemInstance(planItemInstances.get(0).getId());
        PlanItemInstance planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery().singleResult();
        Assert.assertEquals("B", planItemInstance.getName());
        Assert.assertEquals(AVAILABLE, planItemInstance.getState());
    }

    @Test
    @CmmnDeployment
    public void testManualEvaluateCriteria() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testManualEvaluateCriteria").variable("someBean", new IfPartTest.TestBean()).start();
        // Triggering the evaluation twice will satisfy the entry criterion for B
        Assert.assertEquals(1, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceStateActive().count());
        IfPartTest.TestBean.RETURN_VALUE = true;
        cmmnRuntimeService.evaluateCriteria(caseInstance.getId());
        Assert.assertEquals(2, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceStateActive().count());
    }

    @Test
    @CmmnDeployment
    public void testMultipleOnParts() {
        cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testMultipleOnParts").variable("conditionVariable", true).start();
        List<PlanItemInstance> planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceStateActive().orderByName().asc().list();
        Assert.assertEquals(3, planItemInstances.size());
        Assert.assertEquals("A", planItemInstances.get(0).getName());
        Assert.assertEquals("B", planItemInstances.get(1).getName());
        Assert.assertEquals("C", planItemInstances.get(2).getName());
        for (PlanItemInstance planItemInstance : planItemInstances) {
            cmmnRuntimeService.triggerPlanItemInstance(planItemInstance.getId());
        }
        PlanItemInstance planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceStateActive().singleResult();
        Assert.assertEquals("D", planItemInstance.getName());
    }

    @Test
    @CmmnDeployment
    public void testEntryAndExitConditionBothSatisfied() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testEntryAndExitConditionBothSatisfied").start();
        Assert.assertNull(cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceStateActive().planItemInstanceName("A").singleResult());
        Assert.assertNotNull(cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceStateActive().planItemInstanceName("B").singleResult());
        // Setting the variable will trigger the entry condition of A and the exit condition of B
        cmmnRuntimeService.setVariables(caseInstance.getId(), CollectionUtil.singletonMap("conditionVariable", true));
        Assert.assertNotNull(cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceStateActive().planItemInstanceName("A").singleResult());
        Assert.assertNull(cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceStateActive().planItemInstanceName("B").singleResult());
    }

    @Test
    @CmmnDeployment
    public void testExitPlanModelWithIfPart() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testExitPlanModelWithIfPart").start();
        List<PlanItemInstance> planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceStateActive().orderByName().asc().list();
        Assert.assertEquals(2, planItemInstances.size());
        Assert.assertEquals("A", planItemInstances.get(0).getName());
        Assert.assertEquals("B", planItemInstances.get(1).getName());
        // Completing B terminates the case through one of the exit criteria
        cmmnRuntimeService.triggerPlanItemInstance(planItemInstances.get(1).getId());
        assertCaseInstanceEnded(caseInstance);
        // Now B isn't completed, but A is. When the variable is set, the case is terminated
        caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testExitPlanModelWithIfPart").start();
        planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceStateActive().orderByName().asc().list();
        cmmnRuntimeService.triggerPlanItemInstance(planItemInstances.get(0).getId());
        Assert.assertEquals(1, cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceStateActive().count());
        cmmnRuntimeService.setVariables(caseInstance.getId(), CollectionUtil.singletonMap("exitPlanModelVariable", true));
        assertCaseInstanceEnded(caseInstance);
    }

    @Test
    @CmmnDeployment
    public void testNestedStagesWithIfPart() {
        // Start case, activate inner nested stage
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testNestedStagesWithIfPart").start();
        List<PlanItemInstance> planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceStateActive().orderByName().asc().list();
        Assert.assertEquals(4, planItemInstances.size());
        Assert.assertEquals("A", planItemInstances.get(0).getName());
        Assert.assertEquals("C", planItemInstances.get(1).getName());
        Assert.assertEquals("Stage1", planItemInstances.get(2).getName());
        Assert.assertEquals("Stage2", planItemInstances.get(3).getName());
        Assert.assertNotNull(cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceStateAvailable().planItemInstanceName("Stage3").singleResult());
        cmmnRuntimeService.setVariables(caseInstance.getId(), CollectionUtil.singletonMap("nestedStageEntryVariable", true));
        Assert.assertEquals(6, cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceStateActive().orderByName().asc().count());
        PlanItemInstance planItemInstanceB = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceStateActive().planItemInstanceName("B").singleResult();
        Assert.assertNotNull(planItemInstanceB);
        // Triggering B should delete all stages
        cmmnRuntimeService.triggerPlanItemInstance(planItemInstanceB.getId());
        planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceStateActive().orderByName().asc().list();
        Assert.assertEquals(1, planItemInstances.size());
        Assert.assertEquals("A", planItemInstances.get(0).getName());
        cmmnRuntimeService.triggerPlanItemInstance(planItemInstances.get(0).getId());
        assertCaseInstanceEnded(caseInstance);
    }

    @Test
    @CmmnDeployment
    public void testNestedStagesWithIfPart2() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testNestedStagesWithIfPart").start();
        List<PlanItemInstance> planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceStateActive().orderByName().asc().list();
        Assert.assertEquals(4, planItemInstances.size());
        // Setting the destroyStages variables, deletes all stages
        cmmnRuntimeService.setVariables(caseInstance.getId(), CollectionUtil.singletonMap("destroyStages", true));
        planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceStateActive().orderByName().asc().list();
        Assert.assertEquals(1, planItemInstances.size());
        Assert.assertEquals("A", planItemInstances.get(0).getName());
        cmmnRuntimeService.triggerPlanItemInstance(planItemInstances.get(0).getId());
        assertCaseInstanceEnded(caseInstance);
    }

    @Test
    @CmmnDeployment
    public void testNestedStagesWithIfPart3() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testNestedStagesWithIfPart").start();
        Assert.assertEquals(4, cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceStateActive().count());
        // / Setting the destroyAll variable should terminate all
        cmmnRuntimeService.setVariables(caseInstance.getId(), CollectionUtil.singletonMap("destroyAll", true));
        assertCaseInstanceEnded(caseInstance);
    }

    @Test
    @CmmnDeployment
    public void testStageWithExitIfPart() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testStageWithExitIfPart").variable("enableStage", true).start();
        List<PlanItemInstance> planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceStateActive().orderByName().asc().list();
        Assert.assertEquals(4, planItemInstances.size());
        Assert.assertEquals("A", planItemInstances.get(0).getName());
        Assert.assertEquals("B", planItemInstances.get(1).getName());
        Assert.assertEquals("C", planItemInstances.get(2).getName());
        // Triggering A should terminate the stage and thus also the case
        cmmnRuntimeService.triggerPlanItemInstance(planItemInstances.get(0).getId());
        assertCaseInstanceEnded(caseInstance);
    }

    @Test
    @CmmnDeployment
    public void testStageWithExitIfPart2() {
        // Not setting the enableStage variable now
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testStageWithExitIfPart").start();
        List<PlanItemInstance> planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceStateActive().orderByName().asc().list();
        Assert.assertEquals(1, planItemInstances.size());
        Assert.assertEquals("A", planItemInstances.get(0).getName());
        cmmnRuntimeService.triggerPlanItemInstance(planItemInstances.get(0).getId());
        assertCaseInstanceEnded(caseInstance);
    }

    public static class TestBean implements Serializable {
        public static boolean RETURN_VALUE;

        public boolean isSatisfied() {
            return IfPartTest.TestBean.RETURN_VALUE;
        }
    }
}

