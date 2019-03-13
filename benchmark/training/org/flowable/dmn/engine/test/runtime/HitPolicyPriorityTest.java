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
package org.flowable.dmn.engine.test.runtime;


import java.util.Map;
import org.flowable.dmn.api.DecisionExecutionAuditContainer;
import org.flowable.dmn.api.DmnRuleService;
import org.flowable.dmn.engine.DmnEngine;
import org.flowable.dmn.engine.test.DmnDeployment;
import org.flowable.dmn.engine.test.FlowableDmnRule;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


/**
 *
 *
 * @author Yvo Swillens
 */
public class HitPolicyPriorityTest {
    @Rule
    public FlowableDmnRule flowableDmnRule = new FlowableDmnRule();

    @Test
    @DmnDeployment
    public void priorityHitPolicy() {
        DmnEngine dmnEngine = flowableDmnRule.getDmnEngine();
        DmnRuleService dmnRuleService = dmnEngine.getDmnRuleService();
        Map<String, Object> result = dmnRuleService.createExecuteDecisionBuilder().decisionKey("decision1").variable("inputVariable1", 5).executeWithSingleResult();
        Assert.assertEquals(1, result.keySet().size());
        Assert.assertEquals("OUTPUT2", result.get("outputVariable1"));
    }

    @Test
    @DmnDeployment
    public void priorityHitPolicyCompound() {
        DmnEngine dmnEngine = flowableDmnRule.getDmnEngine();
        DmnRuleService dmnRuleService = dmnEngine.getDmnRuleService();
        Map<String, Object> result = dmnRuleService.createExecuteDecisionBuilder().decisionKey("decision1").variable("inputVariable1", 5).executeWithSingleResult();
        Assert.assertEquals(2, result.keySet().size());
        Assert.assertEquals("REFER", result.get("outputVariable1"));
        Assert.assertEquals("LEVEL 2", result.get("outputVariable2"));
    }

    @Test
    @DmnDeployment
    public void priorityHitPolicyCompoundFirstOutputValues() {
        DmnEngine dmnEngine = flowableDmnRule.getDmnEngine();
        DmnRuleService dmnRuleService = dmnEngine.getDmnRuleService();
        Map<String, Object> result = dmnRuleService.createExecuteDecisionBuilder().decisionKey("decision1").variable("inputVariable1", 5).executeWithSingleResult();
        Assert.assertEquals(2, result.keySet().size());
        Assert.assertEquals("REFER", result.get("outputVariable1"));
        Assert.assertEquals("LEVEL 1", result.get("outputVariable2"));
    }

    @Test
    @DmnDeployment
    public void priorityHitPolicyCompoundSecondOutputValues() {
        DmnEngine dmnEngine = flowableDmnRule.getDmnEngine();
        DmnRuleService dmnRuleService = dmnEngine.getDmnRuleService();
        Map<String, Object> result = dmnRuleService.createExecuteDecisionBuilder().decisionKey("decision1").variable("inputVariable1", 5).executeWithSingleResult();
        Assert.assertEquals(2, result.keySet().size());
        Assert.assertEquals("REFER", result.get("outputVariable1"));
        Assert.assertEquals("LEVEL 2", result.get("outputVariable2"));
    }

    @Test
    @DmnDeployment
    public void priorityHitPolicyCompoundNoOutputValues() {
        DmnEngine dmnEngine = flowableDmnRule.getDmnEngine();
        DmnRuleService dmnRuleService = dmnEngine.getDmnRuleService();
        DecisionExecutionAuditContainer result = dmnRuleService.createExecuteDecisionBuilder().decisionKey("decision1").variable("inputVariable1", 5).executeWithAuditTrail();
        Assert.assertEquals(0, result.getDecisionResult().size());
        Assert.assertTrue(result.isFailed());
        Assert.assertNotNull(result.getExceptionMessage());
        Assert.assertNull(result.getValidationMessage());
    }

    @Test
    @DmnDeployment
    public void priorityHitPolicyCompoundNoOutputValuesStrictModeDisabled() {
        DmnEngine dmnEngine = flowableDmnRule.getDmnEngine();
        dmnEngine.getDmnEngineConfiguration().setStrictMode(false);
        DmnRuleService dmnRuleService = dmnEngine.getDmnRuleService();
        DecisionExecutionAuditContainer result = dmnRuleService.createExecuteDecisionBuilder().decisionKey("decision1").variable("inputVariable1", 5).executeWithAuditTrail();
        Assert.assertEquals(1, result.getDecisionResult().size());
        Map<String, Object> outputMap = result.getDecisionResult().iterator().next();
        Assert.assertEquals(2, outputMap.keySet().size());
        Assert.assertEquals("ACCEPT", outputMap.get("outputVariable1"));
        Assert.assertEquals("NONE", outputMap.get("outputVariable2"));
        Assert.assertFalse(result.isFailed());
        Assert.assertNull(result.getExceptionMessage());
        Assert.assertNotNull(result.getValidationMessage());
        dmnEngine.getDmnEngineConfiguration().setStrictMode(true);
    }

    @Test
    @DmnDeployment
    public void priorityHitPolicyTypeConversion() {
        DmnEngine dmnEngine = flowableDmnRule.getDmnEngine();
        DmnRuleService dmnRuleService = dmnEngine.getDmnRuleService();
        Map<String, Object> result = dmnRuleService.createExecuteDecisionBuilder().decisionKey("decision1").variable("inputVariable1", 5).executeWithSingleResult();
        Assert.assertEquals(1, result.keySet().size());
        Assert.assertEquals(20.0, result.get("outputVariable1"));
    }
}

