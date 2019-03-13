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
public class HitPolicyUniqueTest {
    @Rule
    public FlowableDmnRule flowableDmnRule = new FlowableDmnRule();

    @Test
    @DmnDeployment
    public void uniqueHitPolicy() {
        DmnEngine dmnEngine = flowableDmnRule.getDmnEngine();
        DmnRuleService dmnRuleService = dmnEngine.getDmnRuleService();
        Map<String, Object> result = dmnRuleService.createExecuteDecisionBuilder().decisionKey("decision1").variable("inputVariable1", 10).executeWithSingleResult();
        Assert.assertEquals("eq 10", result.get("outputVariable1"));
    }

    @Test
    @DmnDeployment
    public void uniqueHitPolicyViolated() {
        DmnEngine dmnEngine = flowableDmnRule.getDmnEngine();
        DmnRuleService dmnRuleService = dmnEngine.getDmnRuleService();
        DecisionExecutionAuditContainer result = dmnRuleService.createExecuteDecisionBuilder().decisionKey("decision1").variable("inputVariable1", 9).executeWithAuditTrail();
        Assert.assertEquals(0, result.getDecisionResult().size());
        Assert.assertTrue(result.isFailed());
        Assert.assertNotNull(result.getExceptionMessage());
        Assert.assertNotNull(result.getRuleExecutions().get(1).getExceptionMessage());
        Assert.assertNotNull(result.getRuleExecutions().get(3).getExceptionMessage());
        Assert.assertNull(result.getValidationMessage());
        Assert.assertNull(result.getRuleExecutions().get(1).getValidationMessage());
        Assert.assertNull(result.getRuleExecutions().get(3).getValidationMessage());
    }

    @Test
    @DmnDeployment
    public void uniqueHitPolicyViolatedStrictModeDisabled() {
        DmnEngine dmnEngine = flowableDmnRule.getDmnEngine();
        dmnEngine.getDmnEngineConfiguration().setStrictMode(false);
        DmnRuleService dmnRuleService = dmnEngine.getDmnRuleService();
        DecisionExecutionAuditContainer result = dmnRuleService.createExecuteDecisionBuilder().decisionKey("decision1").variable("inputVariable1", 9).executeWithAuditTrail();
        Assert.assertEquals(1, result.getDecisionResult().size());
        Map<String, Object> outputMap = result.getDecisionResult().iterator().next();
        Assert.assertEquals("lt 20", outputMap.get("outputVariable1"));
        Assert.assertEquals(10.0, outputMap.get("outputVariable2"));
        Assert.assertFalse(result.isFailed());
        Assert.assertNull(result.getExceptionMessage());
        Assert.assertNull(result.getRuleExecutions().get(1).getExceptionMessage());
        Assert.assertNull(result.getRuleExecutions().get(3).getExceptionMessage());
        Assert.assertNotNull(result.getValidationMessage());
        Assert.assertNotNull(result.getRuleExecutions().get(1).getValidationMessage());
        Assert.assertNotNull(result.getRuleExecutions().get(3).getValidationMessage());
        // re enable strict mode
        dmnEngine.getDmnEngineConfiguration().setStrictMode(true);
    }
}

