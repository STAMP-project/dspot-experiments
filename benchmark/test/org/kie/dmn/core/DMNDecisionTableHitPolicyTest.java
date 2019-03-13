/**
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.dmn.core;


import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.kie.dmn.feel.runtime.events.HitPolicyViolationEvent;


public class DMNDecisionTableHitPolicyTest extends BaseInterpretedVsCompiledTest {
    public DMNDecisionTableHitPolicyTest(final boolean useExecModelCompiler) {
        super(useExecModelCompiler);
    }

    @Test
    public void testSimpleDecisionTableHitPolicyUnique() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("0004-simpletable-U.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/kie-dmn", "0004-simpletable-U");
        Assert.assertThat(dmnModel, CoreMatchers.notNullValue());
        final DMNContext context = getSimpleTableContext(BigDecimal.valueOf(18), "Medium", true);
        final DMNContext result = evaluateSimpleTableWithContext(dmnModel, runtime, context);
        Assert.assertThat(result.get("Approval Status"), CoreMatchers.is("Approved"));
    }

    @Test
    public void testSimpleDecisionTableHitPolicyUniqueSatisfies() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("0004-simpletable-U.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/kie-dmn", "0004-simpletable-U");
        Assert.assertThat(dmnModel, CoreMatchers.notNullValue());
        final DMNContext context = getSimpleTableContext(BigDecimal.valueOf(18), "ASD", false);
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        final DMNContext result = dmnResult.getContext();
        Assert.assertThat(result.get("Approval Status"), CoreMatchers.nullValue());
        Assert.assertTrue(((dmnResult.getMessages().size()) > 0));
    }

    @Test
    public void testSimpleDecisionTableHitPolicyUniqueNullWarn() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("0004-simpletable-U-noinputvalues.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/kie-dmn", "0004-simpletable-U-noinputvalues");
        Assert.assertThat(dmnModel, CoreMatchers.notNullValue());
        final DMNContext context = getSimpleTableContext(BigDecimal.valueOf(18), "ASD", false);
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        final DMNContext result = dmnResult.getContext();
        Assert.assertThat(result.get("Approval Status"), CoreMatchers.nullValue());
        Assert.assertTrue(((dmnResult.getMessages().size()) > 0));
        Assert.assertTrue(dmnResult.getMessages().stream().anyMatch(( dm) -> ((dm.getSeverity().equals(DMNMessage.Severity.WARN)) && ((dm.getFeelEvent()) instanceof HitPolicyViolationEvent)) && (dm.getFeelEvent().getSeverity().equals(FEELEvent.Severity.WARN))));
    }

    @Test
    public void testDecisionTableHitPolicyUnique() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("BranchDistribution.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_cdf29af2-959b-4004-8271-82a9f5a62147", "Dessin 1");
        Assert.assertThat(dmnModel, CoreMatchers.notNullValue());
        final DMNContext context = DMNFactory.newContext();
        context.set("Branches dispersion", "Province");
        context.set("Number of Branches", BigDecimal.valueOf(10));
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        Assert.assertThat(dmnResult.hasErrors(), CoreMatchers.is(false));
        final DMNContext result = dmnResult.getContext();
        Assert.assertThat(result.get("Branches distribution"), CoreMatchers.is("Medium"));
    }

    @Test
    public void testSimpleDecisionTableHitPolicyFirst() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("0004-simpletable-F.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/kie-dmn", "0004-simpletable-F");
        Assert.assertThat(dmnModel, CoreMatchers.notNullValue());
        final DMNContext context = getSimpleTableContext(BigDecimal.valueOf(18), "Medium", true);
        final DMNContext result = evaluateSimpleTableWithContext(dmnModel, runtime, context);
        final Map<String, Object> decisionResult = ((Map<String, Object>) (result.get("Decision Result")));
        Assert.assertThat(decisionResult.values(), Matchers.hasSize(2));
        Assert.assertThat(decisionResult, Matchers.hasEntry(CoreMatchers.is("Approval Status"), CoreMatchers.is("Approved")));
        Assert.assertThat(decisionResult, Matchers.hasEntry(CoreMatchers.is("Decision Review"), CoreMatchers.is("Decision final")));
    }

    @Test
    public void testSimpleDecisionTableHitPolicyAnyEqualRules() {
        testSimpleDecisionTableHitPolicyAny("0004-simpletable-A.dmn", "0004-simpletable-A", true);
    }

    @Test
    public void testSimpleDecisionTableHitPolicyAnyNonEqualRules() {
        testSimpleDecisionTableHitPolicyAny("0004-simpletable-A-non-equal.dmn", "0004-simpletable-A-non-equal", false);
    }

    @Test
    public void testSimpleDecisionTableHitPolicyPriority() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("0004-simpletable-P.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/kie-dmn", "0004-simpletable-P");
        Assert.assertThat(dmnModel, CoreMatchers.notNullValue());
        final DMNContext context = getSimpleTableContext(BigDecimal.valueOf(70), "Medium", true);
        final DMNContext result = evaluateSimpleTableWithContext(dmnModel, runtime, context);
        Assert.assertThat(result.get("Approval Status"), CoreMatchers.is("Declined"));
    }

    @Test
    public void testSimpleDecisionTableHitPolicyPriorityMultipleOutputs() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("0004-simpletable-P-multiple-outputs.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/kie-dmn", "0004-simpletable-P-multiple-outputs");
        Assert.assertThat(dmnModel, CoreMatchers.notNullValue());
        final DMNContext context = getSimpleTableContext(BigDecimal.valueOf(18), "Medium", true);
        final DMNContext result = evaluateSimpleTableWithContext(dmnModel, runtime, context);
        final Map<String, Object> decisionResult = ((Map<String, Object>) (result.get("Decision Result")));
        Assert.assertThat(decisionResult.values(), Matchers.hasSize(2));
        Assert.assertThat(decisionResult, Matchers.hasEntry(CoreMatchers.is("Approval Status"), CoreMatchers.is("Declined")));
        Assert.assertThat(decisionResult, Matchers.hasEntry(CoreMatchers.is("Decision Review"), CoreMatchers.is("Needs verification")));
    }

    @Test
    public void testSimpleDecisionTableHitPolicyOutputOrder() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("0004-simpletable-O.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/kie-dmn", "0004-simpletable-O");
        Assert.assertThat(dmnModel, CoreMatchers.notNullValue());
        final DMNContext context = getSimpleTableContext(BigDecimal.valueOf(70), "Medium", true);
        final DMNContext result = evaluateSimpleTableWithContext(dmnModel, runtime, context);
        final List<String> decisionResults = ((List<String>) (result.get("Approval Status")));
        Assert.assertThat(decisionResults, Matchers.hasSize(3));
        Assert.assertThat(decisionResults, Matchers.contains("Declined", "Declined", "Approved"));
    }

    @Test
    public void testSimpleDecisionTableHitPolicyOutputOrderMultipleOutputs() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("0004-simpletable-O-multiple-outputs.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/kie-dmn", "0004-simpletable-O-multiple-outputs");
        Assert.assertThat(dmnModel, CoreMatchers.notNullValue());
        final DMNContext context = getSimpleTableContext(BigDecimal.valueOf(18), "Medium", true);
        final DMNContext result = evaluateSimpleTableWithContext(dmnModel, runtime, context);
        final List<Map<String, String>> decisionResult = ((List<Map<String, String>>) (result.get("Decision Result")));
        Assert.assertThat(decisionResult, Matchers.hasSize(4));
        // Must be ordered, so we can read from the list by index
        checkMultipleOutputResult(decisionResult.get(0), "Declined", "Needs verification");
        checkMultipleOutputResult(decisionResult.get(1), "Declined", "Decision final");
        checkMultipleOutputResult(decisionResult.get(2), "Approved", "Needs verification");
        checkMultipleOutputResult(decisionResult.get(3), "Approved", "Decision final");
    }

    @Test
    public void testSimpleDecisionTableHitPolicyRuleOrder() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("0004-simpletable-R.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/kie-dmn", "0004-simpletable-R");
        Assert.assertThat(dmnModel, CoreMatchers.notNullValue());
        final DMNContext context = getSimpleTableContext(BigDecimal.valueOf(70), "Medium", true);
        final DMNContext result = evaluateSimpleTableWithContext(dmnModel, runtime, context);
        final List<String> decisionResults = ((List<String>) (result.get("Approval Status")));
        Assert.assertThat(decisionResults, Matchers.hasSize(3));
        Assert.assertThat(decisionResults, Matchers.contains("Approved", "Needs review", "Declined"));
    }

    @Test
    public void testSimpleDecisionTableHitPolicyCollect() {
        final List<BigDecimal> decisionResults = executeTestDecisionTableHitPolicyCollect(getSimpleTableContext(BigDecimal.valueOf(70), "Medium", true));
        Assert.assertThat(decisionResults, Matchers.hasSize(3));
        Assert.assertThat(decisionResults, Matchers.contains(BigDecimal.valueOf(10), BigDecimal.valueOf(25), BigDecimal.valueOf(13)));
    }

    @Test
    public void testSimpleDecisionTableHitPolicyCollectNoHits() {
        final List<BigDecimal> decisionResults = executeTestDecisionTableHitPolicyCollect(getSimpleTableContext(BigDecimal.valueOf(5), "Medium", true));
        Assert.assertThat(decisionResults, Matchers.hasSize(0));
    }

    @Test
    public void testSimpleDecisionTableHitPolicyCollectSum() {
        testSimpleDecisionTableHitPolicyCollectAggregateFunction("0004-simpletable-C-sum.dmn", "0004-simpletable-C-sum", BigDecimal.valueOf(48), getSimpleTableContext(BigDecimal.valueOf(70), "Medium", true));
    }

    @Test
    public void testSimpleDecisionTableHitPolicyCollectSumMultipleOutputs() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("0004-simpletable-C-sum-multiple-outputs.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/kie-dmn", "0004-simpletable-C-sum-multiple-outputs");
        Assert.assertThat(dmnModel, CoreMatchers.notNullValue());
        final DMNContext context = getSimpleTableContext(BigDecimal.valueOf(70), "Medium", true);
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        final DMNContext result = dmnResult.getContext();
        final Map<String, Object> decisionResult = ((Map<String, Object>) (result.get("Decision Result")));
        Assert.assertThat(decisionResult.values(), Matchers.hasSize(2));
        Assert.assertThat(decisionResult, Matchers.hasEntry("Value1", BigDecimal.valueOf(25)));
        Assert.assertThat(decisionResult, Matchers.hasEntry("Value2", BigDecimal.valueOf(32)));
    }

    @Test
    public void testSimpleDecisionTableHitPolicyCollectMin() {
        testSimpleDecisionTableHitPolicyCollectAggregateFunction("0004-simpletable-C-min.dmn", "0004-simpletable-C-min", BigDecimal.valueOf(10), getSimpleTableContext(BigDecimal.valueOf(70), "Medium", true));
    }

    @Test
    public void testSimpleDecisionTableHitPolicyCollectMax() {
        testSimpleDecisionTableHitPolicyCollectAggregateFunction("0004-simpletable-C-max.dmn", "0004-simpletable-C-max", BigDecimal.valueOf(25), getSimpleTableContext(BigDecimal.valueOf(70), "Medium", true));
    }

    @Test
    public void testSimpleDecisionTableHitPolicyCollectCount() {
        testSimpleDecisionTableHitPolicyCollectAggregateFunction("0004-simpletable-C-count.dmn", "0004-simpletable-C-count", BigDecimal.valueOf(3), getSimpleTableContext(BigDecimal.valueOf(70), "Medium", true));
    }

    @Test
    public void testSimpleDecisionTableHitPolicyCollectCountNoHits() {
        testSimpleDecisionTableHitPolicyCollectAggregateFunction("0004-simpletable-C-count.dmn", "0004-simpletable-C-count", BigDecimal.valueOf(0), getSimpleTableContext(BigDecimal.valueOf(5), "Medium", true));
    }

    @Test
    public void testDecisionTableHitPolicyCollect() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("Collect_Hit_Policy.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_da1a4dcb-01bf-4dee-9be8-f498bc68178c", "Collect Hit Policy");
        Assert.assertThat(dmnModel, CoreMatchers.notNullValue());
        final DMNContext context = DMNFactory.newContext();
        context.set("Input", 20);
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        Assert.assertThat(dmnResult.hasErrors(), CoreMatchers.is(false));
        final DMNContext result = dmnResult.getContext();
        Assert.assertThat(result.get("Collect"), CoreMatchers.is(BigDecimal.valueOf(50)));
    }
}

