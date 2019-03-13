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


import DMNDecisionResult.DecisionEvaluationStatus.NOT_EVALUATED;
import DMNMessage.Severity.ERROR;
import DMNMessageType.ERR_COMPILING_FEEL;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.core.ast.InputDataNode;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.kie.dmn.core.util.DynamicTypeUtils;


public class DMNInputRuntimeTest extends BaseInterpretedVsCompiledTest {
    public DMNInputRuntimeTest(final boolean useExecModelCompiler) {
        super(useExecModelCompiler);
    }

    @Test
    public void testInputStringEvaluateAll() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("0001-input-data-string.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/drools/kie-dmn", "_0001-input-data-string");
        Assert.assertThat(dmnModel, CoreMatchers.notNullValue());
        final DMNContext context = DMNFactory.newContext();
        context.set("Full Name", "John Doe");
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        Assert.assertThat(dmnResult.getDecisionResults().size(), CoreMatchers.is(1));
        Assert.assertThat(dmnResult.getDecisionResultByName("Greeting Message").getResult(), CoreMatchers.is("Hello John Doe"));
        final DMNContext result = dmnResult.getContext();
        Assert.assertThat(result.get("Greeting Message"), CoreMatchers.is("Hello John Doe"));
    }

    @Test
    public void testInputStringEvaluateDecisionByName() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("0001-input-data-string.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/drools/kie-dmn", "_0001-input-data-string");
        Assert.assertThat(dmnModel, CoreMatchers.notNullValue());
        final DMNContext context = DMNFactory.newContext();
        context.set("Full Name", "John Doe");
        DMNResult dmnResult = runtime.evaluateByName(dmnModel, context, "Greeting Message");
        Assert.assertThat(dmnResult.getDecisionResults().size(), CoreMatchers.is(1));
        Assert.assertThat(dmnResult.getDecisionResultByName("Greeting Message").getResult(), CoreMatchers.is("Hello John Doe"));
        final DMNContext result = dmnResult.getContext();
        Assert.assertThat(result.get("Greeting Message"), CoreMatchers.is("Hello John Doe"));
        dmnResult = runtime.evaluateByName(dmnModel, context, "nonExistantName");
        Assert.assertThat(dmnResult.getDecisionResults().size(), CoreMatchers.is(1));
        Assert.assertThat(dmnResult.getDecisionResultByName("Greeting Message").getEvaluationStatus(), CoreMatchers.is(NOT_EVALUATED));
        dmnResult = runtime.evaluateByName(dmnModel, context, "");
        Assert.assertThat(dmnResult.getDecisionResults().size(), CoreMatchers.is(1));
        Assert.assertThat(dmnResult.getDecisionResultByName("Greeting Message").getEvaluationStatus(), CoreMatchers.is(NOT_EVALUATED));
        dmnResult = runtime.evaluateByName(dmnModel, context, ((String) (null)));
        Assert.assertThat(dmnResult.getDecisionResults().size(), CoreMatchers.is(1));
        Assert.assertThat(dmnResult.getDecisionResultByName("Greeting Message").getEvaluationStatus(), CoreMatchers.is(NOT_EVALUATED));
    }

    @Test
    public void testInputStringEvaluateDecisionById() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("0001-input-data-string.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/drools/kie-dmn", "_0001-input-data-string");
        Assert.assertThat(dmnModel, CoreMatchers.notNullValue());
        final DMNContext context = DMNFactory.newContext();
        context.set("Full Name", "John Doe");
        DMNResult dmnResult = runtime.evaluateById(dmnModel, context, "d_GreetingMessage");
        Assert.assertThat(dmnResult.getDecisionResults().size(), CoreMatchers.is(1));
        Assert.assertThat(dmnResult.getDecisionResultById("d_GreetingMessage").getResult(), CoreMatchers.is("Hello John Doe"));
        final DMNContext result = dmnResult.getContext();
        Assert.assertThat(result.get("Greeting Message"), CoreMatchers.is("Hello John Doe"));
        dmnResult = runtime.evaluateById(dmnModel, context, "nonExistantId");
        Assert.assertThat(dmnResult.getDecisionResults().size(), CoreMatchers.is(1));
        Assert.assertThat(dmnResult.getDecisionResultByName("Greeting Message").getEvaluationStatus(), CoreMatchers.is(NOT_EVALUATED));
        dmnResult = runtime.evaluateById(dmnModel, context, "");
        Assert.assertThat(dmnResult.getDecisionResults().size(), CoreMatchers.is(1));
        Assert.assertThat(dmnResult.getDecisionResultByName("Greeting Message").getEvaluationStatus(), CoreMatchers.is(NOT_EVALUATED));
        dmnResult = runtime.evaluateById(dmnModel, context, ((String) (null)));
        Assert.assertThat(dmnResult.getDecisionResults().size(), CoreMatchers.is(1));
        Assert.assertThat(dmnResult.getDecisionResultByName("Greeting Message").getEvaluationStatus(), CoreMatchers.is(NOT_EVALUATED));
    }

    @Test
    public void testInputStringAllowedValuesEvaluateAll() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("0003-input-data-string-allowed-values.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/kie-dmn", "0003-input-data-string-allowed-values");
        Assert.assertThat(dmnModel, CoreMatchers.notNullValue());
        final DMNContext context = DMNFactory.newContext();
        context.set("Employment Status", "SELF-EMPLOYED");
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        final DMNContext result = dmnResult.getContext();
        Assert.assertThat(result.get("Employment Status Statement"), CoreMatchers.is("You are SELF-EMPLOYED"));
    }

    @Test
    public void testInputStringNotInTypeScopeEvaluateAll() {
        testInputStringNotAllowedValuesEvaluateAll("NOT-ALLOWED-VALUE");
    }

    @Test
    public void testInputStringWrongTypeEvaluateAll() {
        testInputStringNotAllowedValuesEvaluateAll(new Object());
    }

    @Test
    public void testInputNumberEvaluateAll() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("0002-input-data-number.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/kie-dmn", "0002-input-data-number");
        Assert.assertThat(dmnModel, CoreMatchers.notNullValue());
        final DMNContext context = DMNFactory.newContext();
        context.set("Monthly Salary", new BigDecimal(1000));
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        final DMNContext result = dmnResult.getContext();
        Assert.assertThat(result.get("Yearly Salary"), CoreMatchers.is(new BigDecimal(12000)));
    }

    @Test
    public void testGetRequiredInputsByName() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("0001-input-data-string.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/drools/kie-dmn", "_0001-input-data-string");
        Assert.assertThat(dmnModel, CoreMatchers.notNullValue());
        Set<InputDataNode> inputs = dmnModel.getRequiredInputsForDecisionName("Greeting Message");
        Assert.assertThat(inputs.size(), CoreMatchers.is(1));
        Assert.assertThat(inputs.iterator().next().getName(), CoreMatchers.is("Full Name"));
        inputs = dmnModel.getRequiredInputsForDecisionName("nonExistantDecisionName");
        Assert.assertThat(inputs.size(), CoreMatchers.is(0));
    }

    @Test
    public void testGetRequiredInputsById() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("0001-input-data-string.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/drools/kie-dmn", "_0001-input-data-string");
        Assert.assertThat(dmnModel, CoreMatchers.notNullValue());
        Set<InputDataNode> inputs = dmnModel.getRequiredInputsForDecisionId("d_GreetingMessage");
        Assert.assertThat(inputs.size(), CoreMatchers.is(1));
        Assert.assertThat(inputs.iterator().next().getName(), CoreMatchers.is("Full Name"));
        inputs = dmnModel.getRequiredInputsForDecisionId("nonExistantId");
        Assert.assertThat(inputs.size(), CoreMatchers.is(0));
    }

    @Test
    public void testNonexistantInputNodeName() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("0001-input-data-string.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/drools/kie-dmn", "_0001-input-data-string");
        Assert.assertThat(dmnModel, CoreMatchers.notNullValue());
        final DMNContext context = DMNFactory.newContext();
        context.set("Nonexistant Input", "John Doe");
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        Assert.assertThat(dmnResult.getDecisionResults().size(), CoreMatchers.is(1));
        Assert.assertThat(dmnResult.getDecisionResultByName("Greeting Message").getResult(), CoreMatchers.is(((String) (null))));
        Assert.assertThat(dmnResult.getMessages().size(), CoreMatchers.is(1));
        Assert.assertThat(dmnResult.getMessages().get(0).getSeverity(), CoreMatchers.is(ERROR));
        Assert.assertThat(dmnResult.getDecisionResults().get(0).getMessages().size(), CoreMatchers.is(1));
        Assert.assertThat(dmnResult.getDecisionResults().get(0).getMessages().get(0).getSeverity(), CoreMatchers.is(ERROR));
    }

    @Test
    public void testAllowedValuesChecks() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("AllowedValuesChecks.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_238bd96d-47cd-4746-831b-504f3e77b442", "AllowedValuesChecks");
        Assert.assertThat(dmnModel, CoreMatchers.notNullValue());
        Assert.assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), CoreMatchers.is(false));
        final DMNContext ctx1 = runtime.newContext();
        ctx1.set("p1", DynamicTypeUtils.prototype(DynamicTypeUtils.entry("Name", "P1"), DynamicTypeUtils.entry("Interests", Collections.singletonList("Golf"))));
        final DMNResult dmnResult1 = runtime.evaluateAll(dmnModel, ctx1);
        Assert.assertThat(DMNRuntimeUtil.formatMessages(dmnResult1.getMessages()), dmnResult1.hasErrors(), CoreMatchers.is(false));
        Assert.assertThat(dmnResult1.getContext().get("MyDecision"), CoreMatchers.is("The Person P1 likes 1 thing(s)."));
        final DMNContext ctx2 = runtime.newContext();
        ctx2.set("p1", DynamicTypeUtils.prototype(DynamicTypeUtils.entry("Name", "P2"), DynamicTypeUtils.entry("Interests", Collections.singletonList("x"))));
        final DMNResult dmnResult2 = runtime.evaluateAll(dmnModel, ctx2);
        Assert.assertThat(DMNRuntimeUtil.formatMessages(dmnResult2.getMessages()), dmnResult2.hasErrors(), CoreMatchers.is(true));
        Assert.assertThat(dmnResult2.getMessages().stream().anyMatch(( m) -> m.getMessageType().equals(DMNMessageType.ERROR_EVAL_NODE)), CoreMatchers.is(true));
        final DMNContext ctx3 = runtime.newContext();
        ctx3.set("p1", DynamicTypeUtils.prototype(DynamicTypeUtils.entry("Name", "P3"), DynamicTypeUtils.entry("Interests", Arrays.asList("Golf", "Computer"))));
        final DMNResult dmnResult3 = runtime.evaluateAll(dmnModel, ctx3);
        Assert.assertThat(DMNRuntimeUtil.formatMessages(dmnResult3.getMessages()), dmnResult3.hasErrors(), CoreMatchers.is(false));
        Assert.assertThat(dmnResult3.getContext().get("MyDecision"), CoreMatchers.is("The Person P3 likes 2 thing(s)."));
        final DMNContext ctx4 = runtime.newContext();
        ctx4.set("p1", DynamicTypeUtils.prototype(DynamicTypeUtils.entry("Name", "P4"), DynamicTypeUtils.entry("Interests", Arrays.asList("Golf", "x"))));
        final DMNResult dmnResult4 = runtime.evaluateAll(dmnModel, ctx4);
        Assert.assertThat(DMNRuntimeUtil.formatMessages(dmnResult4.getMessages()), dmnResult4.hasErrors(), CoreMatchers.is(true));
        Assert.assertThat(dmnResult4.getMessages().stream().anyMatch(( m) -> m.getMessageType().equals(DMNMessageType.ERROR_EVAL_NODE)), CoreMatchers.is(true));
    }

    @Test
    public void testDMNInputDataNodeTypeTest() {
        // DROOLS-1569
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("DMNInputDataNodeTypeTest.dmn", this.getClass());
        final String MODEL_NAMESPACE = "http://www.trisotech.com/definitions/_17396034-163a-48aa-9a7f-c6eb17f9cc6c";
        final String FEEL_NAMESPACE = "http://www.omg.org/spec/FEEL/20140401";
        final DMNModel dmnModel = runtime.getModel(MODEL_NAMESPACE, "DMNInputDataNodeTypeTest");
        Assert.assertThat(dmnModel, CoreMatchers.notNullValue());
        Assert.assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), CoreMatchers.is(false));
        final InputDataNode idnMembership = dmnModel.getInputs().stream().filter(( idn) -> idn.getName().equals("Membership Level")).findFirst().get();
        Assert.assertThat(idnMembership.getType().getBaseType().getNamespace(), CoreMatchers.is(FEEL_NAMESPACE));
        Assert.assertThat(idnMembership.getType().getBaseType().getName(), CoreMatchers.is("string"));
        Assert.assertThat(idnMembership.getType().isCollection(), CoreMatchers.is(false));
        Assert.assertThat(idnMembership.getType().isComposite(), CoreMatchers.is(false));
        Assert.assertThat(idnMembership.getType().getAllowedValues().size(), CoreMatchers.is(3));
        Assert.assertThat(idnMembership.getType().getAllowedValues().get(0).toString(), CoreMatchers.is("\"Gold\""));
        Assert.assertThat(idnMembership.getType().getAllowedValues().get(1).toString(), CoreMatchers.is("\"Silver\""));
        Assert.assertThat(idnMembership.getType().getAllowedValues().get(2).toString(), CoreMatchers.is("\"None\""));
        final InputDataNode idnMembershipLevels = dmnModel.getInputs().stream().filter(( idn) -> idn.getName().equals("Membership Levels")).findFirst().get();
        Assert.assertThat(idnMembershipLevels.getType().getBaseType().getNamespace(), CoreMatchers.is(MODEL_NAMESPACE));
        Assert.assertThat(idnMembershipLevels.getType().getBaseType().getName(), CoreMatchers.is("tMembershipLevel"));
        Assert.assertThat(idnMembershipLevels.getType().isCollection(), CoreMatchers.is(true));
        Assert.assertThat(idnMembershipLevels.getType().isComposite(), CoreMatchers.is(false));
        Assert.assertThat(idnMembershipLevels.getType().getAllowedValues().isEmpty(), CoreMatchers.is(true));
        final InputDataNode idnPercent = dmnModel.getInputs().stream().filter(( idn) -> idn.getName().equals("Percent")).findFirst().get();
        Assert.assertThat(idnPercent.getType().getBaseType().getNamespace(), CoreMatchers.is(FEEL_NAMESPACE));
        Assert.assertThat(idnPercent.getType().getBaseType().getName(), CoreMatchers.is("number"));
        Assert.assertThat(idnPercent.getType().isCollection(), CoreMatchers.is(false));
        Assert.assertThat(idnPercent.getType().isComposite(), CoreMatchers.is(false));
        Assert.assertThat(idnPercent.getType().getAllowedValues().size(), CoreMatchers.is(1));
        Assert.assertThat(idnPercent.getType().getAllowedValues().get(0).toString(), CoreMatchers.is("[0..100]"));
        final InputDataNode idnCarDamageResponsibility = dmnModel.getInputs().stream().filter(( idn) -> idn.getName().equals("Car Damage Responsibility")).findFirst().get();
        Assert.assertThat(idnCarDamageResponsibility.getType().getBaseType(), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertThat(idnCarDamageResponsibility.getType().isCollection(), CoreMatchers.is(false));
        Assert.assertThat(idnCarDamageResponsibility.getType().isComposite(), CoreMatchers.is(true));
    }

    @Test
    public void testInputClauseTypeRefWithAllowedValues() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("actualInputMatchInputValues-forTypeRef.dmn", this.getClass());
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
    public void testInputDataTypeRefWithAllowedValues() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("actualInputMatchInputValues-forTypeRef.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://www.drools.org/kie-dmn/definitions", "definitions");
        Assert.assertThat(dmnModel, CoreMatchers.notNullValue());
        Assert.assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), CoreMatchers.is(false));
        final DMNContext context = runtime.newContext();
        context.set("MyInput", "zzz");
        // <<< `zzz` is NOT in the list of allowed value as declared by the typeRef for this inputdata
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        Assert.assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), CoreMatchers.is(true));
        Assert.assertThat(dmnResult.getMessages().size(), CoreMatchers.is(1));
        Assert.assertThat(dmnResult.getMessages().get(0).getSourceId(), CoreMatchers.is("_3d560678-a126-4654-a686-bc6d941fe40b"));
    }

    @Test
    public void testMissingInputData() {
        final List<DMNMessage> messages = DMNRuntimeUtil.createExpectingDMNMessages("missing_input_data.dmn", getClass());
        Assert.assertThat(messages.get(0).getMessageType(), CoreMatchers.is(ERR_COMPILING_FEEL));
    }
}

