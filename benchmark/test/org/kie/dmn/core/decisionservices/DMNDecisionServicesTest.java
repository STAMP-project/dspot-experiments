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
package org.kie.dmn.core.decisionservices;


import CoerceDecisionServiceSingletonOutputOption.PROPERTY_NAME;
import java.math.BigDecimal;
import java.util.Map;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.BaseInterpretedVsCompiledTest;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DMNDecisionServicesTest extends BaseInterpretedVsCompiledTest {
    public static final Logger LOG = LoggerFactory.getLogger(DMNDecisionServicesTest.class);

    public DMNDecisionServicesTest(final boolean useExecModelCompiler) {
        super(useExecModelCompiler);
    }

    @Test
    public void testBasic() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("0004-decision-services.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_686f58d4-4ec3-4c65-8c06-0e4fd8983def", "Decision Services");
        Assert.assertThat(dmnModel, CoreMatchers.notNullValue());
        Assert.assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), CoreMatchers.is(false));
        checkDSwithInputData(runtime, dmnModel);
        checkDSwithInputDecision(runtime, dmnModel);
        checkDSwithInputDecision2(runtime, dmnModel);
    }

    @Test
    public void testDSInLiteralExpression() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("DecisionServicesInLiteralExpression.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_686f58d4-4ec3-4c65-8c06-0e4fd8983def", "Decision Services");
        Assert.assertThat(dmnModel, CoreMatchers.notNullValue());
        Assert.assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), CoreMatchers.is(false));
        final DMNContext context = DMNFactory.newContext();
        context.set("D", "d");
        context.set("E", "e");
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        DMNDecisionServicesTest.LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(( x) -> LOG.debug("{}", x));
        Assert.assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), CoreMatchers.is(false));
        final DMNContext result = dmnResult.getContext();
        Assert.assertThat(result.get("Decide based on A and DS"), CoreMatchers.is("xyde"));
    }

    @Test
    public void testDSInLiteralExpressionWithBKM() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("DecisionServicesInLiteralExpressionWithBKM.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_686f58d4-4ec3-4c65-8c06-0e4fd8983def", "Decision Services");
        Assert.assertThat(dmnModel, CoreMatchers.notNullValue());
        Assert.assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), CoreMatchers.is(false));
        final DMNContext context = DMNFactory.newContext();
        context.set("D", "d");
        context.set("E", "e");
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        DMNDecisionServicesTest.LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(( x) -> LOG.debug("{}", x));
        Assert.assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), CoreMatchers.is(false));
        final DMNContext result = dmnResult.getContext();
        Assert.assertThat(result.get("Decide based on A and DS"), CoreMatchers.is("xydemn"));
    }

    @Test
    public void testDSInLiteralExpressionWithBKMUsingInvocation() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("DecisionServicesInLiteralExpressionWithBKMUsingInvocation.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_686f58d4-4ec3-4c65-8c06-0e4fd8983def", "Decision Services");
        Assert.assertThat(dmnModel, CoreMatchers.notNullValue());
        Assert.assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), CoreMatchers.is(false));
        final DMNContext context = DMNFactory.newContext();
        context.set("D", "d");
        context.set("E", "e");
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        DMNDecisionServicesTest.LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(( x) -> LOG.debug("{}", x));
        Assert.assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), CoreMatchers.is(false));
        final DMNContext result = dmnResult.getContext();
        Assert.assertThat(result.get("Decide based on A and DS"), CoreMatchers.is("xydemn"));
    }

    @Test
    public void testDSInLiteralExpressionOnlyfromBKMUsingInvocation() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("DecisionServicesInLiteralExpressionOnlyFromBKMUsingInvocation.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_686f58d4-4ec3-4c65-8c06-0e4fd8983def", "Decision Services");
        Assert.assertThat(dmnModel, CoreMatchers.notNullValue());
        Assert.assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), CoreMatchers.is(false));
        final DMNContext context = DMNFactory.newContext();
        context.set("D", "d");
        context.set("E", "e");
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        DMNDecisionServicesTest.LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(( x) -> LOG.debug("{}", x));
        Assert.assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), CoreMatchers.is(false));
        final DMNContext result = dmnResult.getContext();
        Assert.assertThat(result.get("Decide based on A and DS"), CoreMatchers.is("demn"));
    }

    @Test
    public void testMixtypeDS() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("mixtype-DS.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_c9885563-aa54-4c7b-ae8a-738cfd29b544", "mixtype DS");
        Assert.assertThat(dmnModel, CoreMatchers.notNullValue());
        Assert.assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), CoreMatchers.is(false));
        final DMNContext context = DMNFactory.newContext();
        context.set("Person name", "John");
        context.set("Person year of birth", BigDecimal.valueOf(1980));
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        DMNDecisionServicesTest.LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(( x) -> LOG.debug("{}", x));
        Assert.assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), CoreMatchers.is(false));
        final DMNContext result = dmnResult.getContext();
        Assert.assertThat(result.get("Greet the Person"), CoreMatchers.is("Hello, John"));
        Assert.assertThat(result.get("Person age"), CoreMatchers.is(BigDecimal.valueOf(38)));
        Assert.assertThat(result.get("is Person an adult"), CoreMatchers.is(true));
        Assert.assertThat(((Map<String, Object>) (result.get("eval DS all"))), Matchers.hasEntry(CoreMatchers.is("Greet the Person"), CoreMatchers.is("Hello, ds all")));
        Assert.assertThat(((Map<String, Object>) (result.get("eval DS all"))), Matchers.hasEntry(CoreMatchers.is("Person age"), CoreMatchers.is(BigDecimal.valueOf(18))));
        Assert.assertThat(((Map<String, Object>) (result.get("eval DS all"))), Matchers.hasEntry(CoreMatchers.is("is Person an adult"), CoreMatchers.is(true)));
        Assert.assertThat(((Map<String, Object>) (result.get("eval DS all"))), Matchers.not(Matchers.hasEntry(CoreMatchers.is("hardcoded now"), Matchers.anything())));
        Assert.assertThat(((Map<String, Object>) (result.get("eval DS encapsulate"))), Matchers.hasEntry(CoreMatchers.is("Greet the Person"), CoreMatchers.is("Hello, DS encapsulate")));
        Assert.assertThat(((Map<String, Object>) (result.get("eval DS encapsulate"))), Matchers.not(Matchers.hasEntry(CoreMatchers.is("Person age"), Matchers.anything())));
        Assert.assertThat(((Map<String, Object>) (result.get("eval DS encapsulate"))), Matchers.hasEntry(CoreMatchers.is("is Person an adult"), CoreMatchers.is(true)));
        Assert.assertThat(((Map<String, Object>) (result.get("eval DS encapsulate"))), Matchers.not(Matchers.hasEntry(CoreMatchers.is("hardcoded now"), Matchers.anything())));
        Assert.assertThat(((Map<String, Object>) (result.get("eval DS greet adult"))), Matchers.hasEntry(CoreMatchers.is("Greet the Person"), CoreMatchers.is("Hello, DS greet adult")));
        Assert.assertThat(((Map<String, Object>) (result.get("eval DS greet adult"))), Matchers.not(Matchers.hasEntry(CoreMatchers.is("Person age"), Matchers.anything())));
        Assert.assertThat(((Map<String, Object>) (result.get("eval DS greet adult"))), Matchers.hasEntry(CoreMatchers.is("is Person an adult"), CoreMatchers.is(true)));
        Assert.assertThat(((Map<String, Object>) (result.get("eval DS greet adult"))), Matchers.not(Matchers.hasEntry(CoreMatchers.is("hardcoded now"), Matchers.anything())));
        // additionally check DS one-by-one
        testMixtypeDS_checkDSall(runtime, dmnModel);
        testMixtypeDS_checkDSencapsulate(runtime, dmnModel);
        testMixtypeDS_checkDSgreetadult(runtime, dmnModel);
    }

    @Test
    public void testDSForTypeCheck() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("DecisionService20180718.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_6eef3a7c-bb0d-40bb-858d-f9067789c18a", "Decision Service 20180718");
        Assert.assertThat(dmnModel, CoreMatchers.notNullValue());
        Assert.assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), CoreMatchers.is(false));
        testDSForTypeCheck_runNormal(runtime, dmnModel);
        testDSForTypeCheck_runAllDecisionsWithWrongTypes(runtime, dmnModel);
        testDSForTypeCheck_runDecisionService_Normal(runtime, dmnModel);
        testDSForTypeCheck_runDecisionService_WithWrongTypes(runtime, dmnModel);
    }

    @Test
    public void testDSSingletonOrMultipleOutputDecisions() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("Decision-Services-singleton-or-multiple-output-decisions.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_b4ebfbf2-8608-4297-9662-be70bab01974", "Decision Services singleton or multiple output decisions");
        Assert.assertThat(dmnModel, CoreMatchers.notNullValue());
        Assert.assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), CoreMatchers.is(false));
        final DMNContext emptyContext = DMNFactory.newContext();
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, emptyContext);
        DMNDecisionServicesTest.LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(( x) -> LOG.debug("{}", x));
        Assert.assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), CoreMatchers.is(false));
        final DMNContext result = dmnResult.getContext();
        Assert.assertThat(result.get("a Value"), CoreMatchers.is("a string Value"));
        Assert.assertThat(result.get("a String Value"), CoreMatchers.is("a String Value"));
        Assert.assertThat(result.get("a Number Value"), CoreMatchers.is(BigDecimal.valueOf(47)));
        Assert.assertThat(result.get("eval DS with singleton value"), CoreMatchers.is("a string Value"));
        Assert.assertThat(((Map<String, Object>) (result.get("eval DS with multiple output decisions"))), Matchers.hasEntry(CoreMatchers.is("a String Value"), CoreMatchers.is("a String Value")));
        Assert.assertThat(((Map<String, Object>) (result.get("eval DS with multiple output decisions"))), Matchers.hasEntry(CoreMatchers.is("a Number Value"), CoreMatchers.is(BigDecimal.valueOf(47))));
        final DMNResult dmnResultDSSingleton = runtime.evaluateDecisionService(dmnModel, emptyContext, "DS with singleton value");
        DMNDecisionServicesTest.LOG.debug("{}", dmnResultDSSingleton);
        dmnResultDSSingleton.getDecisionResults().forEach(( x) -> LOG.debug("{}", x));
        Assert.assertThat(DMNRuntimeUtil.formatMessages(dmnResultDSSingleton.getMessages()), dmnResultDSSingleton.hasErrors(), CoreMatchers.is(false));
        Assert.assertThat(dmnResultDSSingleton.getContext().get("a Value"), CoreMatchers.is("a string Value"));
        Assert.assertThat(dmnResultDSSingleton.getContext().getAll(), Matchers.not(Matchers.hasEntry(CoreMatchers.is("a String Value"), Matchers.anything())));// Decision Service will not expose (nor encapsulate hence not execute) this decision.

        Assert.assertThat(dmnResultDSSingleton.getContext().getAll(), Matchers.not(Matchers.hasEntry(CoreMatchers.is("a Number Value"), Matchers.anything())));// Decision Service will not expose (nor encapsulate hence not execute) this decision.

        final DMNResult dmnResultMultiple = runtime.evaluateDecisionService(dmnModel, emptyContext, "DS with multiple output decisions");
        DMNDecisionServicesTest.LOG.debug("{}", dmnResultMultiple);
        dmnResultMultiple.getDecisionResults().forEach(( x) -> LOG.debug("{}", x));
        Assert.assertThat(DMNRuntimeUtil.formatMessages(dmnResultMultiple.getMessages()), dmnResultMultiple.hasErrors(), CoreMatchers.is(false));
        Assert.assertThat(dmnResultMultiple.getContext().get("a String Value"), CoreMatchers.is("a String Value"));
        Assert.assertThat(dmnResultMultiple.getContext().get("a Number Value"), CoreMatchers.is(BigDecimal.valueOf(47)));
        Assert.assertThat(dmnResultMultiple.getContext().getAll(), Matchers.not(Matchers.hasEntry(CoreMatchers.is("a Value"), Matchers.anything())));// Decision Service will not expose (nor encapsulate hence not execute) this decision.

    }

    @Test
    public void testDSSingletonOrMultipleOutputDecisions_OVERRIDE() {
        try {
            System.setProperty(PROPERTY_NAME, "false");
            final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("Decision-Services-singleton-or-multiple-output-decisions.dmn", this.getClass());
            final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_b4ebfbf2-8608-4297-9662-be70bab01974", "Decision Services singleton or multiple output decisions");
            Assert.assertThat(dmnModel, CoreMatchers.notNullValue());
            Assert.assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), CoreMatchers.is(false));
            final DMNContext emptyContext = DMNFactory.newContext();
            final DMNResult dmnResult = runtime.evaluateAll(dmnModel, emptyContext);
            DMNDecisionServicesTest.LOG.debug("{}", dmnResult);
            dmnResult.getDecisionResults().forEach(( x) -> LOG.debug("{}", x));
            Assert.assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), CoreMatchers.is(false));
            final DMNContext result = dmnResult.getContext();
            Assert.assertThat(result.get("a Value"), CoreMatchers.is("a string Value"));
            Assert.assertThat(result.get("a String Value"), CoreMatchers.is("a String Value"));
            Assert.assertThat(result.get("a Number Value"), CoreMatchers.is(BigDecimal.valueOf(47)));
            Assert.assertThat(((Map<String, Object>) (result.get("eval DS with singleton value"))), Matchers.hasEntry(CoreMatchers.is("a Value"), CoreMatchers.is("a string Value")));// DIFFERENCE with base test

            Assert.assertThat(((Map<String, Object>) (result.get("eval DS with multiple output decisions"))), Matchers.hasEntry(CoreMatchers.is("a String Value"), CoreMatchers.is("a String Value")));
            Assert.assertThat(((Map<String, Object>) (result.get("eval DS with multiple output decisions"))), Matchers.hasEntry(CoreMatchers.is("a Number Value"), CoreMatchers.is(BigDecimal.valueOf(47))));
            final DMNResult dmnResultDSSingleton = runtime.evaluateDecisionService(dmnModel, emptyContext, "DS with singleton value");
            DMNDecisionServicesTest.LOG.debug("{}", dmnResultDSSingleton);
            dmnResultDSSingleton.getDecisionResults().forEach(( x) -> LOG.debug("{}", x));
            Assert.assertThat(DMNRuntimeUtil.formatMessages(dmnResultDSSingleton.getMessages()), dmnResultDSSingleton.hasErrors(), CoreMatchers.is(false));
            Assert.assertThat(dmnResultDSSingleton.getContext().get("a Value"), CoreMatchers.is("a string Value"));
            Assert.assertThat(dmnResultDSSingleton.getContext().getAll(), Matchers.not(Matchers.hasEntry(CoreMatchers.is("a String Value"), Matchers.anything())));// Decision Service will not expose (nor encapsulate hence not execute) this decision.

            Assert.assertThat(dmnResultDSSingleton.getContext().getAll(), Matchers.not(Matchers.hasEntry(CoreMatchers.is("a Number Value"), Matchers.anything())));// Decision Service will not expose (nor encapsulate hence not execute) this decision.

            final DMNResult dmnResultMultiple = runtime.evaluateDecisionService(dmnModel, emptyContext, "DS with multiple output decisions");
            DMNDecisionServicesTest.LOG.debug("{}", dmnResultMultiple);
            dmnResultMultiple.getDecisionResults().forEach(( x) -> LOG.debug("{}", x));
            Assert.assertThat(DMNRuntimeUtil.formatMessages(dmnResultMultiple.getMessages()), dmnResultMultiple.hasErrors(), CoreMatchers.is(false));
            Assert.assertThat(dmnResultMultiple.getContext().get("a String Value"), CoreMatchers.is("a String Value"));
            Assert.assertThat(dmnResultMultiple.getContext().get("a Number Value"), CoreMatchers.is(BigDecimal.valueOf(47)));
            Assert.assertThat(dmnResultMultiple.getContext().getAll(), Matchers.not(Matchers.hasEntry(CoreMatchers.is("a Value"), Matchers.anything())));// Decision Service will not expose (nor encapsulate hence not execute) this decision.

        } catch (final Exception e) {
            DMNDecisionServicesTest.LOG.error("{}", e.getLocalizedMessage(), e);
            throw e;
        } finally {
            System.clearProperty(PROPERTY_NAME);
            Assert.assertNull(System.getProperty(PROPERTY_NAME));
        }
    }

    @Test
    public void testImportDS() {
        // DROOLS-2768 DMN Decision Service encapsulate Decision which imports a Decision Service
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntimeWithAdditionalResources("DecisionService20180718.dmn", this.getClass(), "ImportDecisionService20180718.dmn");
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_0ff3708a-c861-4a96-b85c-7b882f18b7a1", "Import Decision Service 20180718");
        Assert.assertThat(dmnModel, CoreMatchers.notNullValue());
        Assert.assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), CoreMatchers.is(false));
        testImportDS_testEvaluateAll(runtime, dmnModel);
        testImportDS_testEvaluateDS(runtime, dmnModel);
    }

    @Test
    public void testTransitiveImportDS() {
        // DROOLS-2768 DMN Decision Service encapsulate Decision which imports a Decision Service
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntimeWithAdditionalResources("DecisionService20180718.dmn", this.getClass(), "ImportDecisionService20180718.dmn", "ImportofImportDecisionService20180718.dmn");
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_6698dc07-cc43-47ec-8187-8faa7d8c35ba", "Import of Import Decision Service 20180718");
        Assert.assertThat(dmnModel, CoreMatchers.notNullValue());
        Assert.assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), CoreMatchers.is(false));
        testTransitiveImportDS_testEvaluateAll(runtime, dmnModel);
        testTransitiveImportDS_testEvaluateDS(runtime, dmnModel);
    }

    @Test
    public void testDecisionServiceCompiler20180830() {
        // DROOLS-2943 DMN DecisionServiceCompiler not correctly wired for DMNv1.2 format
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("DecisionServiceABC.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_2443d3f5-f178-47c6-a0c9-b1fd1c933f60", "Drawing 1");
        Assert.assertThat(dmnModel, CoreMatchers.notNullValue());
        Assert.assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), CoreMatchers.is(false));
        DMNDecisionServicesTest.testDecisionServiceCompiler20180830_testEvaluateDS(runtime, dmnModel);
        DMNDecisionServicesTest.testDecisionServiceCompiler20180830_testEvaluateAll(runtime, dmnModel);
    }

    @Test
    public void testDecisionService20180920() {
        // DROOLS-3005 DMN DecisionService having an imported requiredInput
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntimeWithAdditionalResources("DSWithImportRequiredInput20180920.dmn", this.getClass(), "DSWithImportRequiredInput20180920-import-1.dmn");
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_76165d7d-12f8-46d3-b8af-120f1ac8b3fc", "Model B");
        Assert.assertThat(dmnModel, CoreMatchers.notNullValue());
        Assert.assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), CoreMatchers.is(false));
        DMNDecisionServicesTest.testDecisionService20180920_testEvaluateAll(runtime, dmnModel);
        DMNDecisionServicesTest.testDecisionService20180920_testEvaluateDS(runtime, dmnModel);
    }
}

