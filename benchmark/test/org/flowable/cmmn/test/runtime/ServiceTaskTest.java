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


import PlanItemInstanceState.ACTIVE;
import org.flowable.cmmn.api.runtime.CaseInstance;
import org.flowable.cmmn.api.runtime.PlanItemInstance;
import org.flowable.cmmn.engine.test.CmmnDeployment;
import org.flowable.cmmn.engine.test.FlowableCmmnTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Tijs Rademakers
 */
public class ServiceTaskTest extends FlowableCmmnTestCase {
    @Test
    @CmmnDeployment
    public void testJavaServiceTask() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").variable("test", "test2").start();
        Assert.assertNotNull(caseInstance);
        PlanItemInstance planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceState(ACTIVE).singleResult();
        Assert.assertNotNull(planItemInstance);
        Assert.assertEquals("executed", cmmnRuntimeService.getVariable(caseInstance.getId(), "javaDelegate"));
        Assert.assertEquals("test2", cmmnRuntimeService.getVariable(caseInstance.getId(), "test"));
        // Triggering the task should start the child case instance
        cmmnRuntimeService.triggerPlanItemInstance(planItemInstance.getId());
        Assert.assertEquals(0, cmmnRuntimeService.createCaseInstanceQuery().count());
        Assert.assertEquals("executed", cmmnHistoryService.createHistoricVariableInstanceQuery().caseInstanceId(caseInstance.getId()).variableName("javaDelegate").singleResult().getValue());
    }

    @Test
    @CmmnDeployment
    public void testJavaServiceTaskFields() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").variable("test", "test").start();
        Assert.assertNotNull(caseInstance);
        PlanItemInstance planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceState(ACTIVE).singleResult();
        Assert.assertNotNull(planItemInstance);
        Assert.assertEquals("test", cmmnRuntimeService.getVariable(caseInstance.getId(), "testValue"));
        Assert.assertEquals(true, cmmnRuntimeService.getVariable(caseInstance.getId(), "testExpression"));
        // Triggering the task should start the child case instance
        cmmnRuntimeService.triggerPlanItemInstance(planItemInstance.getId());
        Assert.assertEquals(0, cmmnRuntimeService.createCaseInstanceQuery().count());
        Assert.assertEquals("test", cmmnHistoryService.createHistoricVariableInstanceQuery().caseInstanceId(caseInstance.getId()).variableName("testValue").singleResult().getValue());
        Assert.assertEquals(true, cmmnHistoryService.createHistoricVariableInstanceQuery().caseInstanceId(caseInstance.getId()).variableName("testExpression").singleResult().getValue());
    }

    @Test
    @CmmnDeployment
    public void testResultVariableName() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").variable("test", "test").start();
        Assert.assertNotNull(caseInstance);
        PlanItemInstance planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceState(ACTIVE).singleResult();
        Assert.assertNotNull(planItemInstance);
        Assert.assertEquals("hello test", cmmnRuntimeService.getVariable(caseInstance.getId(), "beanResponse"));
        // Triggering the task should start the child case instance
        cmmnRuntimeService.triggerPlanItemInstance(planItemInstance.getId());
        Assert.assertEquals(0, cmmnRuntimeService.createCaseInstanceQuery().count());
        Assert.assertEquals("hello test", cmmnHistoryService.createHistoricVariableInstanceQuery().caseInstanceId(caseInstance.getId()).variableName("beanResponse").singleResult().getValue());
    }

    @Test
    @CmmnDeployment
    public void testDelegateExpression() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").variable("test", "test2").start();
        Assert.assertNotNull(caseInstance);
        PlanItemInstance planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceState(ACTIVE).singleResult();
        Assert.assertNotNull(planItemInstance);
        Assert.assertEquals("executed", cmmnRuntimeService.getVariable(caseInstance.getId(), "javaDelegate"));
        Assert.assertEquals("test2", cmmnRuntimeService.getVariable(caseInstance.getId(), "test"));
        // Triggering the task should start the child case instance
        cmmnRuntimeService.triggerPlanItemInstance(planItemInstance.getId());
        Assert.assertEquals(0, cmmnRuntimeService.createCaseInstanceQuery().count());
        Assert.assertEquals("executed", cmmnHistoryService.createHistoricVariableInstanceQuery().caseInstanceId(caseInstance.getId()).variableName("javaDelegate").singleResult().getValue());
    }

    @Test
    @CmmnDeployment
    public void testDelegateExpressionFields() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").variable("test", "test").start();
        Assert.assertNotNull(caseInstance);
        PlanItemInstance planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceState(ACTIVE).singleResult();
        Assert.assertNotNull(planItemInstance);
        Assert.assertEquals("test", cmmnRuntimeService.getVariable(caseInstance.getId(), "testValue"));
        Assert.assertEquals(true, cmmnRuntimeService.getVariable(caseInstance.getId(), "testExpression"));
        // Triggering the task should start the child case instance
        cmmnRuntimeService.triggerPlanItemInstance(planItemInstance.getId());
        Assert.assertEquals(0, cmmnRuntimeService.createCaseInstanceQuery().count());
        Assert.assertEquals("test", cmmnHistoryService.createHistoricVariableInstanceQuery().caseInstanceId(caseInstance.getId()).variableName("testValue").singleResult().getValue());
        Assert.assertEquals(true, cmmnHistoryService.createHistoricVariableInstanceQuery().caseInstanceId(caseInstance.getId()).variableName("testExpression").singleResult().getValue());
    }
}

