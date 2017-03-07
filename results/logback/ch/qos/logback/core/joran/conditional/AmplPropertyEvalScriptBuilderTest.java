/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2015, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */


package ch.qos.logback.core.joran.conditional;


public class AmplPropertyEvalScriptBuilderTest {
    ch.qos.logback.core.Context context = new ch.qos.logback.core.ContextBase();

    ch.qos.logback.core.joran.spi.InterpretationContext localPropContainer = new ch.qos.logback.core.joran.spi.InterpretationContext(context, null);

    ch.qos.logback.core.joran.conditional.PropertyEvalScriptBuilder pesb = new ch.qos.logback.core.joran.conditional.PropertyEvalScriptBuilder(localPropContainer);

    int diff = ch.qos.logback.core.testUtil.RandomUtil.getPositiveInt();

    java.lang.String k = "ka" + (diff);

    java.lang.String v = "va";

    java.lang.String containsScript = ((("p(\"" + (k)) + "\").contains(\"") + (v)) + "\")";

    java.lang.String isNullScriptStr = ("isNull(\"" + (k)) + "\")";

    java.lang.String isDefiedScriptStr = ("isDefined(\"" + (k)) + "\")";

    @org.junit.Before
    public void setUp() {
        context.setName(("c" + (diff)));
        pesb.setContext(context);
    }

    @org.junit.After
    public void tearDown() {
        java.lang.System.clearProperty(k);
    }

    void buildAndAssertTrue(java.lang.String scriptStr) throws java.lang.Exception {
        ch.qos.logback.core.joran.conditional.Condition condition = pesb.build(scriptStr);
        org.junit.Assert.assertNotNull(condition);
        org.junit.Assert.assertTrue(condition.evaluate());
    }

    void buildAndAssertFalse(java.lang.String scriptStr) throws java.lang.Exception {
        ch.qos.logback.core.joran.conditional.Condition condition = pesb.build(scriptStr);
        org.junit.Assert.assertNotNull(condition);
        org.junit.Assert.assertFalse(condition.evaluate());
    }

    @org.junit.Test
    public void existingLocalPropertyShouldEvaluateToTrue() throws java.lang.Exception {
        localPropContainer.addSubstitutionProperty(k, v);
        buildAndAssertTrue(containsScript);
    }

    @org.junit.Test
    public void existingContextPropertyShouldEvaluateToTrue() throws java.lang.Exception {
        context.putProperty(k, v);
        buildAndAssertTrue(containsScript);
    }

    @org.junit.Test
    public void existingSystemPropertyShouldEvaluateToTrue() throws java.lang.Exception {
        java.lang.System.setProperty(k, v);
        buildAndAssertTrue(containsScript);
    }

    @org.junit.Test
    public void isNullForExistingLocalProperty() throws java.lang.Exception {
        localPropContainer.addSubstitutionProperty(k, v);
        buildAndAssertFalse(isNullScriptStr);
    }

    @org.junit.Test
    public void isNullForExistingContextProperty() throws java.lang.Exception {
        context.putProperty(k, v);
        buildAndAssertFalse(isNullScriptStr);
    }

    @org.junit.Test
    public void isNullForExistingSystemProperty() throws java.lang.Exception {
        java.lang.System.setProperty(k, v);
        buildAndAssertFalse(isNullScriptStr);
    }

    @org.junit.Test
    public void inexistentPropertyShouldEvaluateToFalse() throws java.lang.Exception {
        buildAndAssertFalse(containsScript);
    }

    @org.junit.Test
    public void isNullForInexistentPropertyShouldEvaluateToTrue() throws java.lang.Exception {
        buildAndAssertTrue(isNullScriptStr);
    }

    public void isDefinedForIExistimgtPropertyShouldEvaluateToTrue() throws java.lang.Exception {
        localPropContainer.addSubstitutionProperty(k, v);
        buildAndAssertTrue(isDefiedScriptStr);
    }

    @org.junit.Test
    public void isDefinedForInexistentPropertyShouldEvaluateToTrue() throws java.lang.Exception {
        buildAndAssertFalse(isDefiedScriptStr);
    }
}

