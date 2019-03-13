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


import BuiltInType.NUMBER;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.api.core.ast.ItemDefNode;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.impl.CompositeTypeImpl;
import org.kie.dmn.core.impl.SimpleTypeImpl;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.kie.dmn.core.util.DynamicTypeUtils;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.types.AliasFEELType;
import org.kie.dmn.feel.util.ClassLoaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DMNCompilerTest extends BaseInterpretedVsCompiledTest {
    public static final Logger LOG = LoggerFactory.getLogger(DMNCompilerTest.class);

    public DMNCompilerTest(final boolean useExecModelCompiler) {
        super(useExecModelCompiler);
    }

    @Test
    public void testItemDefAllowedValuesString() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("0003-input-data-string-allowed-values.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/kie-dmn", "0003-input-data-string-allowed-values");
        Assert.assertThat(dmnModel, CoreMatchers.notNullValue());
        final ItemDefNode itemDef = dmnModel.getItemDefinitionByName("tEmploymentStatus");
        Assert.assertThat(itemDef.getName(), CoreMatchers.is("tEmploymentStatus"));
        Assert.assertThat(itemDef.getId(), CoreMatchers.is(CoreMatchers.nullValue()));
        final DMNType type = itemDef.getType();
        Assert.assertThat(type, CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(type.getName(), CoreMatchers.is("tEmploymentStatus"));
        Assert.assertThat(type.getId(), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertThat(type, CoreMatchers.is(CoreMatchers.instanceOf(SimpleTypeImpl.class)));
        final SimpleTypeImpl feelType = ((SimpleTypeImpl) (type));
        final EvaluationContext ctx = new org.kie.dmn.feel.lang.impl.EvaluationContextImpl(ClassLoaderUtil.findDefaultClassLoader(), null);
        Assert.assertThat(feelType.getFeelType(), CoreMatchers.is(CoreMatchers.instanceOf(AliasFEELType.class)));
        Assert.assertThat(feelType.getFeelType().getName(), CoreMatchers.is("tEmploymentStatus"));
        Assert.assertThat(feelType.getAllowedValuesFEEL().size(), CoreMatchers.is(4));
        Assert.assertThat(feelType.getAllowedValuesFEEL().get(0).apply(ctx, "UNEMPLOYED"), CoreMatchers.is(true));
        Assert.assertThat(feelType.getAllowedValuesFEEL().get(1).apply(ctx, "EMPLOYED"), CoreMatchers.is(true));
        Assert.assertThat(feelType.getAllowedValuesFEEL().get(2).apply(ctx, "SELF-EMPLOYED"), CoreMatchers.is(true));
        Assert.assertThat(feelType.getAllowedValuesFEEL().get(3).apply(ctx, "STUDENT"), CoreMatchers.is(true));
    }

    @Test
    public void testCompositeItemDefinition() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("0008-LX-arithmetic.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/kie-dmn", "0008-LX-arithmetic");
        Assert.assertThat(dmnModel, CoreMatchers.notNullValue());
        final ItemDefNode itemDef = dmnModel.getItemDefinitionByName("tLoan");
        Assert.assertThat(itemDef.getName(), CoreMatchers.is("tLoan"));
        Assert.assertThat(itemDef.getId(), CoreMatchers.is("tLoan"));
        final DMNType type = itemDef.getType();
        Assert.assertThat(type, CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(type.getName(), CoreMatchers.is("tLoan"));
        Assert.assertThat(type.getId(), CoreMatchers.is("tLoan"));
        Assert.assertThat(type, CoreMatchers.is(CoreMatchers.instanceOf(CompositeTypeImpl.class)));
        final CompositeTypeImpl compType = ((CompositeTypeImpl) (type));
        Assert.assertThat(compType.getFields().size(), CoreMatchers.is(3));
        final DMNType principal = compType.getFields().get("principal");
        Assert.assertThat(principal, CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(principal.getName(), CoreMatchers.is("number"));
        Assert.assertThat(getFeelType(), CoreMatchers.is(NUMBER));
        final DMNType rate = compType.getFields().get("rate");
        Assert.assertThat(rate, CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(rate.getName(), CoreMatchers.is("number"));
        Assert.assertThat(getFeelType(), CoreMatchers.is(NUMBER));
        final DMNType termMonths = compType.getFields().get("termMonths");
        Assert.assertThat(termMonths, CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(termMonths.getName(), CoreMatchers.is("number"));
        Assert.assertThat(getFeelType(), CoreMatchers.is(NUMBER));
    }

    @Test
    public void testCompilationThrowsNPE() {
        try {
            DMNRuntimeUtil.createRuntime("compilationThrowsNPE.dmn", this.getClass());
        } catch (final IllegalStateException ex) {
            Assert.assertThat(ex.getMessage(), Matchers.containsString("Unable to compile DMN model for the resource"));
        }
    }

    @Test
    public void testRecursiveFunctions() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("Recursive.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/kie-dmn", "Recursive");
        Assert.assertThat(dmnModel, CoreMatchers.notNullValue());
        Assert.assertFalse(runtime.evaluateAll(dmnModel, DMNFactory.newContext()).hasErrors());
    }

    @Test
    public void testImport() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntimeWithAdditionalResources("Importing_Model.dmn", this.getClass(), "Imported_Model.dmn");
        final DMNModel importedModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_f27bb64b-6fc7-4e1f-9848-11ba35e0df36", "Imported Model");
        Assert.assertThat(importedModel, CoreMatchers.notNullValue());
        for (final DMNMessage message : importedModel.getMessages()) {
            DMNCompilerTest.LOG.debug("{}", message);
        }
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_f79aa7a4-f9a3-410a-ac95-bea496edab52", "Importing Model");
        Assert.assertThat(dmnModel, CoreMatchers.notNullValue());
        for (final DMNMessage message : dmnModel.getMessages()) {
            DMNCompilerTest.LOG.debug("{}", message);
        }
        final DMNContext context = runtime.newContext();
        context.set("A Person", DynamicTypeUtils.mapOf(DynamicTypeUtils.entry("name", "John"), DynamicTypeUtils.entry("age", 47)));
        final DMNResult evaluateAll = runtime.evaluateAll(dmnModel, context);
        for (final DMNMessage message : evaluateAll.getMessages()) {
            DMNCompilerTest.LOG.debug("{}", message);
        }
        DMNCompilerTest.LOG.debug("{}", evaluateAll);
        Assert.assertThat(evaluateAll.getDecisionResultByName("Greeting").getResult(), CoreMatchers.is("Hello John!"));
    }
}

