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
package org.flowable.cmmn.test.history;


import PlanItemInstanceState.ACTIVE;
import java.util.HashMap;
import java.util.Map;
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
public class HistoryServiceTest extends FlowableCmmnTestCase {
    @Test
    @CmmnDeployment
    public void testStartSimplePassthroughCase() {
        cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").variable("var1", "test").variable("var2", 10).start();
        Assert.assertEquals(1, cmmnHistoryService.createHistoricCaseInstanceQuery().variableValueEquals("var1", "test").count());
        Assert.assertEquals(0, cmmnHistoryService.createHistoricCaseInstanceQuery().variableValueEquals("var1", "test2").count());
        Assert.assertEquals(1, cmmnHistoryService.createHistoricCaseInstanceQuery().variableValueEqualsIgnoreCase("var1", "TEST").count());
        Assert.assertEquals(0, cmmnHistoryService.createHistoricCaseInstanceQuery().variableValueEqualsIgnoreCase("var1", "TEST2").count());
        Assert.assertEquals(1, cmmnHistoryService.createHistoricCaseInstanceQuery().variableValueLike("var1", "te%").count());
        Assert.assertEquals(0, cmmnHistoryService.createHistoricCaseInstanceQuery().variableValueLike("var1", "te2%").count());
        Assert.assertEquals(1, cmmnHistoryService.createHistoricCaseInstanceQuery().variableValueLikeIgnoreCase("var1", "TE%").count());
        Assert.assertEquals(0, cmmnHistoryService.createHistoricCaseInstanceQuery().variableValueLikeIgnoreCase("var1", "TE2%").count());
        Assert.assertEquals(1, cmmnHistoryService.createHistoricCaseInstanceQuery().variableValueGreaterThan("var2", 5).count());
        Assert.assertEquals(0, cmmnHistoryService.createHistoricCaseInstanceQuery().variableValueGreaterThan("var2", 11).count());
        Assert.assertEquals(1, cmmnHistoryService.createHistoricCaseInstanceQuery().variableValueLessThan("var2", 11).count());
        Assert.assertEquals(0, cmmnHistoryService.createHistoricCaseInstanceQuery().variableValueLessThan("var2", 8).count());
        Assert.assertEquals(1, cmmnHistoryService.createHistoricCaseInstanceQuery().variableExists("var1").count());
        Assert.assertEquals(0, cmmnHistoryService.createHistoricCaseInstanceQuery().variableExists("var3").count());
        Assert.assertEquals(1, cmmnHistoryService.createHistoricCaseInstanceQuery().variableNotExists("var3").count());
        Assert.assertEquals(0, cmmnHistoryService.createHistoricCaseInstanceQuery().variableNotExists("var1").count());
        Assert.assertEquals(0, cmmnRuntimeService.createCaseInstanceQuery().count());
    }

    @Test
    @CmmnDeployment
    public void testStartSimplePassthroughCaseWithBlockingTask() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").variable("startVar", "test").variable("changeVar", 10).start();
        Assert.assertEquals(1, cmmnHistoryService.createHistoricCaseInstanceQuery().variableValueEquals("startVar", "test").count());
        Assert.assertEquals(1, cmmnHistoryService.createHistoricCaseInstanceQuery().variableValueEquals("changeVar", 10).count());
        PlanItemInstance planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceState(ACTIVE).singleResult();
        Assert.assertNotNull(planItemInstance);
        Assert.assertEquals("Task A", planItemInstance.getName());
        Map<String, Object> varMap = new HashMap<>();
        varMap.put("newVar", "test");
        varMap.put("changeVar", 11);
        cmmnRuntimeService.setVariables(caseInstance.getId(), varMap);
        Map<String, Object> localVarMap = new HashMap<>();
        localVarMap.put("localVar", "test");
        localVarMap.put("localNumberVar", 2);
        cmmnRuntimeService.setLocalVariables(planItemInstance.getId(), localVarMap);
        cmmnRuntimeService.triggerPlanItemInstance(planItemInstance.getId());
        Assert.assertEquals(1, cmmnHistoryService.createHistoricCaseInstanceQuery().variableValueEquals("startVar", "test").count());
        Assert.assertEquals(1, cmmnHistoryService.createHistoricCaseInstanceQuery().variableValueEquals("changeVar", 11).count());
        Assert.assertEquals(0, cmmnHistoryService.createHistoricCaseInstanceQuery().variableValueEquals("changeVar", 10).count());
        planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceState(ACTIVE).singleResult();
        Assert.assertNotNull(planItemInstance);
        Assert.assertEquals("Task B", planItemInstance.getName());
        cmmnRuntimeService.triggerPlanItemInstance(planItemInstance.getId());
        Assert.assertEquals(0, cmmnRuntimeService.createCaseInstanceQuery().count());
    }
}

