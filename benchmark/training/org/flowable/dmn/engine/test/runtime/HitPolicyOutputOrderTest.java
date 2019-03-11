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


import java.util.List;
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
public class HitPolicyOutputOrderTest {
    @Rule
    public FlowableDmnRule flowableDmnRule = new FlowableDmnRule();

    @Test
    @DmnDeployment
    public void outputOrderHitPolicy() {
        DmnEngine dmnEngine = flowableDmnRule.getDmnEngine();
        DmnRuleService dmnRuleService = dmnEngine.getDmnRuleService();
        List<Map<String, Object>> result = dmnRuleService.createExecuteDecisionBuilder().decisionKey("decision1").variable("inputVariable1", 5).execute();
        Assert.assertEquals(3, result.size());
        Assert.assertEquals("OUTPUT2", result.get(0).get("outputVariable1"));
        Assert.assertEquals("OUTPUT3", result.get(1).get("outputVariable1"));
        Assert.assertEquals("OUTPUT1", result.get(2).get("outputVariable1"));
    }

    @Test
    @DmnDeployment
    public void outputOrderHitPolicyNoOutputValuesStrictModeDisabled() {
        DmnEngine dmnEngine = flowableDmnRule.getDmnEngine();
        dmnEngine.getDmnEngineConfiguration().setStrictMode(false);
        DmnRuleService dmnRuleService = dmnEngine.getDmnRuleService();
        DecisionExecutionAuditContainer result = dmnRuleService.createExecuteDecisionBuilder().decisionKey("decision1").variable("inputVariable1", 5).executeWithAuditTrail();
        Assert.assertEquals(3, result.getDecisionResult().size());
        Assert.assertEquals("OUTPUT1", result.getDecisionResult().get(0).get("outputVariable1"));
        Assert.assertEquals("OUTPUT2", result.getDecisionResult().get(1).get("outputVariable1"));
        Assert.assertEquals("OUTPUT3", result.getDecisionResult().get(2).get("outputVariable1"));
        Assert.assertFalse(result.isFailed());
        Assert.assertNull(result.getExceptionMessage());
        Assert.assertNotNull(result.getValidationMessage());
        dmnEngine.getDmnEngineConfiguration().setStrictMode(true);
    }

    @Test
    @DmnDeployment
    public void outputOrderHitPolicyNoOutputValues() {
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
    public void outputOrderHitPolicyCompound() {
        DmnEngine dmnEngine = flowableDmnRule.getDmnEngine();
        DmnRuleService dmnRuleService = dmnEngine.getDmnRuleService();
        List<Map<String, Object>> result = dmnRuleService.createExecuteDecisionBuilder().decisionKey("decision1").variable("inputVariable1", 5).execute();
        Assert.assertEquals(4, result.size());
        Assert.assertEquals("DECLINE", result.get(0).get("outputVariable1"));
        Assert.assertEquals("REFER", result.get(1).get("outputVariable1"));
        Assert.assertEquals("REFER", result.get(2).get("outputVariable1"));
        Assert.assertEquals("ACCEPT", result.get(3).get("outputVariable1"));
        Assert.assertEquals("NONE", result.get(0).get("outputVariable2"));
        Assert.assertEquals("LEVEL 2", result.get(1).get("outputVariable2"));
        Assert.assertEquals("LEVEL 1", result.get(2).get("outputVariable2"));
        Assert.assertEquals("NONE", result.get(3).get("outputVariable2"));
    }

    @Test
    @DmnDeployment
    public void outputOrderHitPolicyCompoundOtherTypes() {
        DmnEngine dmnEngine = flowableDmnRule.getDmnEngine();
        DmnRuleService dmnRuleService = dmnEngine.getDmnRuleService();
        List<Map<String, Object>> result = dmnRuleService.createExecuteDecisionBuilder().decisionKey("decision1").variable("inputVariable1", 5).execute();
        Assert.assertEquals(4, result.size());
        Assert.assertEquals("DECLINE", result.get(0).get("outputVariable1"));
        Assert.assertEquals("REFER", result.get(1).get("outputVariable1"));
        Assert.assertEquals("REFER", result.get(2).get("outputVariable1"));
        Assert.assertEquals("ACCEPT", result.get(3).get("outputVariable1"));
        Assert.assertEquals(10.0, result.get(0).get("outputVariable2"));
        Assert.assertEquals(30.0, result.get(1).get("outputVariable2"));
        Assert.assertEquals(20.0, result.get(2).get("outputVariable2"));
        Assert.assertEquals(10.0, result.get(3).get("outputVariable2"));
    }

    @Test
    @DmnDeployment
    public void outputOrderHitPolicyCompoundFirstOutputValues() {
        DmnEngine dmnEngine = flowableDmnRule.getDmnEngine();
        DmnRuleService dmnRuleService = dmnEngine.getDmnRuleService();
        List<Map<String, Object>> result = dmnRuleService.createExecuteDecisionBuilder().decisionKey("decision1").variable("inputVariable1", 5).execute();
        Assert.assertEquals(4, result.size());
        Assert.assertEquals("DECLINE", result.get(0).get("outputVariable1"));
        Assert.assertEquals("REFER", result.get(1).get("outputVariable1"));
        Assert.assertEquals("REFER", result.get(2).get("outputVariable1"));
        Assert.assertEquals("ACCEPT", result.get(3).get("outputVariable1"));
        Assert.assertEquals("NONE", result.get(0).get("outputVariable2"));
        Assert.assertEquals("LEVEL 1", result.get(1).get("outputVariable2"));
        Assert.assertEquals("LEVEL 2", result.get(2).get("outputVariable2"));
        Assert.assertEquals("NONE", result.get(3).get("outputVariable2"));
    }

    @Test
    @DmnDeployment
    public void outputOrderHitPolicyCompoundSecondOutputValues() {
        DmnEngine dmnEngine = flowableDmnRule.getDmnEngine();
        DmnRuleService dmnRuleService = dmnEngine.getDmnRuleService();
        List<Map<String, Object>> result = dmnRuleService.createExecuteDecisionBuilder().decisionKey("decision1").variable("inputVariable1", 5).execute();
        Assert.assertEquals(4, result.size());
        Assert.assertEquals("REFER", result.get(0).get("outputVariable1"));
        Assert.assertEquals("REFER", result.get(1).get("outputVariable1"));
        Assert.assertEquals("ACCEPT", result.get(2).get("outputVariable1"));
        Assert.assertEquals("DECLINE", result.get(3).get("outputVariable1"));
        Assert.assertEquals("LEVEL 2", result.get(0).get("outputVariable2"));
        Assert.assertEquals("LEVEL 1", result.get(1).get("outputVariable2"));
        Assert.assertEquals("NONE", result.get(2).get("outputVariable2"));
        Assert.assertEquals("NONE", result.get(3).get("outputVariable2"));
    }
}

