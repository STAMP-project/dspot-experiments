/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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


import DMNDecisionResult.DecisionEvaluationStatus.FAILED;
import DMNDecisionResult.DecisionEvaluationStatus.SUCCEEDED;
import KieServices.Factory;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.core.event.AfterEvaluateDecisionTableEvent;
import org.kie.dmn.api.core.event.DMNRuntimeEventListener;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.kie.dmn.core.util.KieHelper;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DMNDecisionTableRuntimeTest extends BaseInterpretedVsCompiledTest {
    public static final Logger LOG = LoggerFactory.getLogger(DMNDecisionTableRuntimeTest.class);

    public DMNDecisionTableRuntimeTest(final boolean useExecModelCompiler) {
        super(useExecModelCompiler);
    }

    @Test
    public void testDecisionTableWithCalculatedResult() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("calculation1.dmn", this.getClass());
        checkDecisionTableWithCalculatedResult(runtime);
    }

    @Test(timeout = 30000L)
    public void testDecisionTableWithCalculatedResult_parallel() throws Throwable {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("calculation1.dmn", this.getClass());
        final Runnable task = () -> checkDecisionTableWithCalculatedResult(runtime);
        final List<Throwable> problems = Collections.synchronizedList(new ArrayList<>());
        final List<CompletableFuture<Void>> tasks = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            final CompletableFuture<Void> newtask = CompletableFuture.runAsync(task).exceptionally(( t) -> {
                problems.add(t);
                return null;
            });
            tasks.add(newtask);
        }
        CompletableFuture.allOf(tasks.toArray(new CompletableFuture<?>[]{  })).get();
        if ((problems.size()) > 0) {
            throw problems.get(0);
        }
    }

    @Test
    public void testDecisionTableMultipleResults() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("car_damage_responsibility.dmn", this.getClass());
        final DMNRuntimeEventListener listener = Mockito.mock(DMNRuntimeEventListener.class);
        runtime.addListener(listener);
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_820611e9-c21c-47cd-8e52-5cba2be9f9cc", "Car Damage Responsibility");
        Assert.assertThat(dmnModel, CoreMatchers.notNullValue());
        final DMNContext context = DMNFactory.newContext();
        context.set("Membership Level", "Silver");
        context.set("Damage Types", "Body");
        context.set("Responsible", "Driver");
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        Assert.assertThat(dmnResult.hasErrors(), CoreMatchers.is(false));
        final DMNContext result = dmnResult.getContext();
        Assert.assertThat(((Map<String, Object>) (result.get("Car Damage Responsibility"))), Matchers.hasEntry(CoreMatchers.is("EU Rent"), CoreMatchers.is(BigDecimal.valueOf(40))));
        Assert.assertThat(((Map<String, Object>) (result.get("Car Damage Responsibility"))), Matchers.hasEntry(CoreMatchers.is("Renter"), CoreMatchers.is(BigDecimal.valueOf(60))));
        Assert.assertThat(result.get("Payment method"), CoreMatchers.is("Check"));
        final ArgumentCaptor<AfterEvaluateDecisionTableEvent> captor = ArgumentCaptor.forClass(AfterEvaluateDecisionTableEvent.class);
        Mockito.verify(listener, VerificationModeFactory.times(2)).afterEvaluateDecisionTable(captor.capture());
        final AfterEvaluateDecisionTableEvent first = captor.getAllValues().get(0);
        Assert.assertThat(first.getMatches(), CoreMatchers.is(Collections.singletonList(5)));
        Assert.assertThat(first.getSelected(), CoreMatchers.is(Collections.singletonList(5)));
        final AfterEvaluateDecisionTableEvent second = captor.getAllValues().get(1);
        Assert.assertThat(second.getMatches(), CoreMatchers.is(Collections.singletonList(3)));
        Assert.assertThat(second.getSelected(), CoreMatchers.is(Collections.singletonList(3)));
    }

    @Test
    public void testSimpleDecisionTableMultipleOutputWrongOutputType() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("0004-simpletable-P-multiple-outputs-wrong-output.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/kie-dmn", "0004-simpletable-P-multiple-outputs-wrong-output");
        Assert.assertThat(dmnModel, CoreMatchers.notNullValue());
        final DMNContext context = DMNFactory.newContext();
        context.set("Age", BigDecimal.valueOf(18));
        context.set("RiskCategory", "Medium");
        context.set("isAffordable", true);
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        Assert.assertThat(dmnResult.hasErrors(), CoreMatchers.is(true));
        Assert.assertThat(dmnResult.getMessages().stream().filter(( message) -> (message.getFeelEvent().getSourceException()) instanceof NullPointerException).count(), CoreMatchers.is(0L));
    }

    @Test
    public void testDecisionTableInvalidInputErrorMessage() {
        final DMNContext context = DMNFactory.newContext();
        context.set("Branches dispersion", "Province");
        context.set("Number of Branches", BigDecimal.valueOf(10));
        testDecisionTableInvalidInput(context);
    }

    @Test
    public void testDecisionTableInvalidInputTypeErrorMessage() {
        final DMNContext context = DMNFactory.newContext();
        context.set("Branches dispersion", 1);
        context.set("Number of Branches", BigDecimal.valueOf(10));
        testDecisionTableInvalidInput(context);
    }

    @Test
    public void testDecisionTableNonexistingInputErrorMessage() {
        final DMNContext context = DMNFactory.newContext();
        context.set("Not exists", "Province");
        context.set("Number of Branches", BigDecimal.valueOf(10));
        testDecisionTableInvalidInput(context);
    }

    @Test
    public void testDecisionTableDefaultValue() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("decisiontable-default-value.dmn", this.getClass());
        final DMNRuntimeEventListener listener = Mockito.mock(DMNRuntimeEventListener.class);
        runtime.addListener(listener);
        final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/kie-dmn", "decisiontable-default-value");
        Assert.assertThat(dmnModel, CoreMatchers.notNullValue());
        Assert.assertThat(dmnModel.getMessages().toString(), dmnModel.hasErrors(), CoreMatchers.is(false));
        final DMNContext context = DMNFactory.newContext();
        context.set("Age", new BigDecimal(16));
        context.set("RiskCategory", "Medium");
        context.set("isAffordable", true);
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        Assert.assertThat(dmnResult.getMessages().toString(), dmnResult.hasErrors(), CoreMatchers.is(false));
        final DMNContext result = dmnResult.getContext();
        Assert.assertThat(result.get("Approval Status"), CoreMatchers.is("Declined"));
        final ArgumentCaptor<AfterEvaluateDecisionTableEvent> captor = ArgumentCaptor.forClass(AfterEvaluateDecisionTableEvent.class);
        Mockito.verify(listener).afterEvaluateDecisionTable(captor.capture());
        Assert.assertThat(captor.getValue().getMatches(), CoreMatchers.is(Matchers.empty()));
        Assert.assertThat(captor.getValue().getSelected(), CoreMatchers.is(Matchers.empty()));
    }

    @Test
    public void testTwoDecisionTables() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("two_decision_tables.dmn", this.getClass());
        final DMNRuntimeEventListener listener = Mockito.mock(DMNRuntimeEventListener.class);
        runtime.addListener(listener);
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_bbb692e7-3d95-407a-bf39-353085bf57f0", "Invocation with two decision table as parameters");
        Assert.assertThat(dmnModel, CoreMatchers.notNullValue());
        Assert.assertThat(dmnModel.getMessages().toString(), dmnModel.hasErrors(), CoreMatchers.is(false));
        final DMNContext context = DMNFactory.newContext();
        context.set("Number", 50);
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        Assert.assertThat(dmnResult.getMessages().toString(), dmnResult.hasErrors(), CoreMatchers.is(false));
        final DMNContext result = dmnResult.getContext();
        Assert.assertThat(((Map<String, Object>) (result.get("Decision Logic 2"))), Matchers.hasEntry("the 5 analysis", "A number greater than 5"));
        Assert.assertThat(((Map<String, Object>) (result.get("Decision Logic 2"))), Matchers.hasEntry("the 100 analysis", "A number smaller than 100"));
        final ArgumentCaptor<AfterEvaluateDecisionTableEvent> captor = ArgumentCaptor.forClass(AfterEvaluateDecisionTableEvent.class);
        Mockito.verify(listener, VerificationModeFactory.times(2)).afterEvaluateDecisionTable(captor.capture());
        Assert.assertThat(captor.getAllValues().get(0).getDecisionTableName(), CoreMatchers.is("a"));
        Assert.assertThat(captor.getAllValues().get(1).getDecisionTableName(), CoreMatchers.is("b"));
    }

    @Test
    public void testDTInputExpressionLocalXmlnsInference() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("drools1502-InputExpression.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://www.drools.org/kie-dmn/definitions", "definitions");
        Assert.assertThat(dmnModel, CoreMatchers.notNullValue());
        Assert.assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), CoreMatchers.is(false));
        final DMNContext context = runtime.newContext();
        context.set("MyInput", "a");
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        Assert.assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), CoreMatchers.is(false));
        final DMNContext result = dmnResult.getContext();
        Assert.assertThat(result.get("MyDecision"), CoreMatchers.is("Decision taken"));
    }

    @Test
    public void testDTInContext() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("DT_in_context.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_4acdcb25-b298-435e-abd5-efd00ed686a5", "Drawing 1");
        Assert.assertThat(dmnModel, CoreMatchers.notNullValue());
        final DMNContext context = DMNFactory.newContext();
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        Assert.assertThat(dmnResult.getDecisionResults().size(), CoreMatchers.is(1));
        Assert.assertThat(dmnResult.getDecisionResultByName("D1").getResult(), CoreMatchers.is(CoreMatchers.instanceOf(Map.class)));
        final DMNContext result = dmnResult.getContext();
        Assert.assertThat(((Map) (result.get("D1"))).get("Text color"), CoreMatchers.is("red"));
    }

    @Test
    public void testDTUsingEqualsUnaryTestWithVariable1() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("DT_using_variables.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_ed1ec15b-40aa-424d-b1d0-4936df80b135", "DT Using variables");
        Assert.assertThat(dmnModel, CoreMatchers.notNullValue());
        Assert.assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), CoreMatchers.is(false));
        final Map<String, Object> complex = new HashMap<>();
        complex.put("aBoolean", true);
        complex.put("aNumber", 10);
        complex.put("aString", "bar");
        final DMNContext context = DMNFactory.newContext();
        context.set("Complex", complex);
        context.set("Another boolean", true);
        context.set("Another String", "bar");
        context.set("Another number", 10);
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        final DMNContext result = dmnResult.getContext();
        Assert.assertThat(result.get("Compare Boolean"), CoreMatchers.is("Same boolean"));
        Assert.assertThat(result.get("Compare Number"), CoreMatchers.is("Equals"));
        Assert.assertThat(result.get("Compare String"), CoreMatchers.is("Same String"));
    }

    @Test
    public void testDTUsingEqualsUnaryTestWithVariable2() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("DT_using_variables.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_ed1ec15b-40aa-424d-b1d0-4936df80b135", "DT Using variables");
        Assert.assertThat(dmnModel, CoreMatchers.notNullValue());
        Assert.assertThat(dmnModel.hasErrors(), CoreMatchers.is(false));
        final Map<String, Object> complex = new HashMap<>();
        complex.put("aBoolean", true);
        complex.put("aNumber", 10);
        complex.put("aString", "bar");
        final DMNContext context = DMNFactory.newContext();
        context.set("Complex", complex);
        context.set("Another boolean", false);
        context.set("Another String", "foo");
        context.set("Another number", 20);
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        final DMNContext result = dmnResult.getContext();
        Assert.assertThat(result.get("Compare Boolean"), CoreMatchers.is("Not same boolean"));
        Assert.assertThat(result.get("Compare Number"), CoreMatchers.is("Bigger"));
        Assert.assertThat(result.get("Compare String"), CoreMatchers.is("Different String"));
    }

    @Test
    public void testEmptyOutputCell() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("DT_empty_output_cell.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_77ae284e-ce52-4579-a50f-f3cc584d7f4b", "Calculation1");
        Assert.assertThat(dmnModel, CoreMatchers.notNullValue());
        final DMNContext context = DMNFactory.newContext();
        context.set("MonthlyDeptPmt", BigDecimal.valueOf(1));
        context.set("MonthlyPmt", BigDecimal.valueOf(1));
        context.set("MonthlyIncome", BigDecimal.valueOf(1));
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        DMNDecisionTableRuntimeTest.LOG.debug("{}", dmnResult);
        Assert.assertThat(dmnResult.hasErrors(), CoreMatchers.is(false));
        Assert.assertNull(dmnResult.getContext().get("Logique de d?cision 1"));
    }

    @Test
    public void testNullRelation() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("nullrelation.dmn", getClass());
        final DMNModel model = runtime.getModel("http://www.trisotech.com/definitions/_946a2145-89ae-4197-88b4-40e6f88c8101", "Null in relations");
        Assert.assertThat(model, CoreMatchers.notNullValue());
        Assert.assertThat(DMNRuntimeUtil.formatMessages(model.getMessages()), model.hasErrors(), CoreMatchers.is(false));
        final DMNContext context = DMNFactory.newContext();
        context.set("Value", "a3");
        final DMNResult result = runtime.evaluateByName(model, context, "Mapping");
        Assert.assertThat(DMNRuntimeUtil.formatMessages(result.getMessages()), result.hasErrors(), CoreMatchers.is(false));
    }

    @Test
    public void testDecisionTableOutputDMNTypeCollection() {
        // DROOLS-2359
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("DecisionTableOutputDMNTypeCollection.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_ae5d2033-c6d0-411f-a394-da33a70e5638", "Drawing 1");
        Assert.assertThat(dmnModel, CoreMatchers.notNullValue());
        Assert.assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), CoreMatchers.is(false));
        final DMNContext context = DMNFactory.newContext();
        context.set("selector", "asd");
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        Assert.assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), CoreMatchers.is(false));
        final DMNContext result = dmnResult.getContext();
        Assert.assertThat(result.get("a decision"), CoreMatchers.is(Arrays.asList("abc", "xyz")));
    }

    @Test
    public void testDecisionTableOutputDMNTypeCollection_NOtypecheck() {
        // DROOLS-2359
        // do NOT use the DMNRuntimeUtil as that enables typeSafe check override for runtime.
        final KieServices ks = Factory.get();
        final KieContainer kieContainer = KieHelper.getKieContainer(ks.newReleaseId("org.kie", ("dmn-test-" + (UUID.randomUUID())), "1.0"), ks.getResources().newClassPathResource("DecisionTableOutputDMNTypeCollection.dmn", this.getClass()));
        final DMNRuntime runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_ae5d2033-c6d0-411f-a394-da33a70e5638", "Drawing 1");
        Assert.assertThat(dmnModel, CoreMatchers.notNullValue());
        Assert.assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), CoreMatchers.is(false));
        final DMNContext context = DMNFactory.newContext();
        context.set("selector", "asd");
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        Assert.assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), CoreMatchers.is(false));
        final DMNContext result = dmnResult.getContext();
        Assert.assertThat(result.get("a decision"), CoreMatchers.is(Arrays.asList("abc", "xyz")));
    }

    @Test
    public void testDecisionTableOutputDMNTypeCollectionWithLOV() {
        // DROOLS-2359
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("DecisionTableOutputDMNTypeCollectionWithLOV.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_ae5d2033-c6d0-411f-a394-da33a70e5638", "List of Words in DT");
        Assert.assertThat(dmnModel, CoreMatchers.notNullValue());
        Assert.assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), CoreMatchers.is(false));
        final DMNContext context = DMNFactory.newContext();
        context.set("selector", "asd");
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        Assert.assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), CoreMatchers.is(false));
        final DMNContext result = dmnResult.getContext();
        Assert.assertThat(result.get("a decision"), CoreMatchers.is(Arrays.asList("abc", "a")));
    }

    @Test
    public void testDecisionTableOutputDMNTypeCollectionWithLOV_NOtypecheck() {
        // DROOLS-2359
        // do NOT use the DMNRuntimeUtil as that enables typeSafe check override for runtime.
        final KieServices ks = Factory.get();
        final KieContainer kieContainer = KieHelper.getKieContainer(ks.newReleaseId("org.kie", ("dmn-test-" + (UUID.randomUUID())), "1.0"), ks.getResources().newClassPathResource("DecisionTableOutputDMNTypeCollectionWithLOV.dmn", this.getClass()));
        final DMNRuntime runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_ae5d2033-c6d0-411f-a394-da33a70e5638", "List of Words in DT");
        Assert.assertThat(dmnModel, CoreMatchers.notNullValue());
        Assert.assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), CoreMatchers.is(false));
        final DMNContext context = DMNFactory.newContext();
        context.set("selector", "asd");
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        Assert.assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), CoreMatchers.is(false));
        final DMNContext result = dmnResult.getContext();
        Assert.assertThat(result.get("a decision"), CoreMatchers.is(Arrays.asList("abc", "a")));
    }

    @Test
    public void testDecisionTablesQuestionMarkVariableCorrectEvaluation() {
        testDecisionTablesQuestionMarkVariable("questionmarkunarytest/qmark.dmn", "OK", SUCCEEDED);
    }

    @Test
    public void testDecisionTablesQuestionMarkAsString() {
        testDecisionTablesQuestionMarkVariable("questionmarkunarytest/qmarkAsString.dmn", "NOT OK", SUCCEEDED);
        testDecisionTablesQuestionMarkVariable("questionmarkunarytest/qmarkAsString2.dmn", "NOT OK", SUCCEEDED);
        testDecisionTablesQuestionMarkVariable("questionmarkunarytest/qmarkAsString3.dmn", "NOT OK", SUCCEEDED);
    }

    @Test
    public void testDecisionTablesQuestionMarkVariableVsQuestionMarkString() {
        testDecisionTablesQuestionMarkVariable("questionmarkunarytest/qmarkVsQmarkString.dmn", "OK", SUCCEEDED);
    }

    @Test
    public void testDecisionTablesQuestionMarkOnly() {
        testDecisionTablesQuestionMarkVariable("questionmarkunarytest/qmarkonly.dmn", null, FAILED);
    }

    @Test
    public void testDecisionTablesQuestionMarkMultivalue() {
        testDecisionTablesQuestionMarkVariable("questionmarkunarytest/qmarkMultivalue.dmn", "OK", SUCCEEDED);
    }

    @Test
    public void testDecisionTablesQuestionMarkMultivalueWithBrackets() {
        testDecisionTablesQuestionMarkVariable("questionmarkunarytest/qmarkMultivalueWithBrackets.dmn", "OK", SUCCEEDED);
    }

    @Test
    public void testDecisionTablesQuestionMarkWithNot() {
        testDecisionTablesQuestionMarkVariable("questionmarkunarytest/qmarkWithNot.dmn", "OK", SUCCEEDED);
    }

    @Test
    public void testDecisionTablesQuestionMarkWithContext() {
        testDecisionTablesQuestionMarkVariable("questionmarkunarytest/qmarkWithContext.dmn", "OK", SUCCEEDED);
    }

    @Test
    public void testDecisionTablesQuestionMarkWithQuantifiedExpression() {
        testDecisionTablesQuestionMarkVariable("questionmarkunarytest/qmarkWithQuantifiedExpr.dmn", "OK", SUCCEEDED);
    }

    @Test
    public void testDecisionTablesQuestionMarkWithForExpression() {
        testDecisionTablesQuestionMarkVariable("questionmarkunarytest/qmarkWithForExpr.dmn", "OK", SUCCEEDED);
    }

    @Test
    public void testDecisionTablesQuestionMarkWithFuncDef() {
        testDecisionTablesQuestionMarkVariable("questionmarkunarytest/qmarkWithFuncDef.dmn", "OK", SUCCEEDED);
    }

    @Test
    public void testDecisionTablesQuestionMarkInstanceOf() {
        testDecisionTablesQuestionMarkVariable("questionmarkunarytest/qmarkInstanceOf.dmn", "NOT OK", SUCCEEDED);
    }

    @Test
    public void testDecisionTablesQuestionMarkInWithUnaryTests() {
        testDecisionTablesQuestionMarkVariable("questionmarkunarytest/qmarkInWithUnaryTest.dmn", "OK", SUCCEEDED);
    }

    @Test
    public void testDecisionTablesQuestionMarkWithRange() {
        testDecisionTablesQuestionMarkVariable("questionmarkunarytest/qmarkWithRange.dmn", "OK", SUCCEEDED);
    }

    @Test
    public void testDecisionTablesQuestionMarkWithAnd() {
        testDecisionTablesQuestionMarkVariable("questionmarkunarytest/qmarkWithAnd.dmn", "OK", SUCCEEDED);
    }

    @Test
    public void testDecisionTablesQuestionMarkInNonBooleanFunction() {
        testDecisionTablesQuestionMarkVariable("questionmarkunarytest/qmarkInNonBooleanFunction.dmn", null, FAILED);
    }
}

