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


import ch.qos.logback.core.Context;
import ch.qos.logback.core.ContextBase;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.testUtil.RandomUtil;
import org.junit.Test;


public class PropertyEvalScriptBuilderTest {
    Context context = new ContextBase();

    InterpretationContext localPropContainer = new InterpretationContext(context, null);

    PropertyEvalScriptBuilder pesb = new PropertyEvalScriptBuilder(localPropContainer);

    int diff = RandomUtil.getPositiveInt();

    String k = "ka" + (diff);

    String v = "va";

    String containsScript = ((("p(\"" + (k)) + "\").contains(\"") + (v)) + "\")";

    String isNullScriptStr = ("isNull(\"" + (k)) + "\")";

    String isDefiedScriptStr = ("isDefined(\"" + (k)) + "\")";

    @Test
    public void existingLocalPropertyShouldEvaluateToTrue() throws Exception {
        localPropContainer.addSubstitutionProperty(k, v);
        buildAndAssertTrue(containsScript);
    }

    @Test
    public void existingContextPropertyShouldEvaluateToTrue() throws Exception {
        context.putProperty(k, v);
        buildAndAssertTrue(containsScript);
    }

    @Test
    public void existingSystemPropertyShouldEvaluateToTrue() throws Exception {
        System.setProperty(k, v);
        buildAndAssertTrue(containsScript);
    }

    @Test
    public void isNullForExistingLocalProperty() throws Exception {
        localPropContainer.addSubstitutionProperty(k, v);
        buildAndAssertFalse(isNullScriptStr);
    }

    @Test
    public void isNullForExistingContextProperty() throws Exception {
        context.putProperty(k, v);
        buildAndAssertFalse(isNullScriptStr);
    }

    @Test
    public void isNullForExistingSystemProperty() throws Exception {
        System.setProperty(k, v);
        buildAndAssertFalse(isNullScriptStr);
    }

    @Test
    public void inexistentPropertyShouldEvaluateToFalse() throws Exception {
        buildAndAssertFalse(containsScript);
    }

    @Test
    public void isNullForInexistentPropertyShouldEvaluateToTrue() throws Exception {
        buildAndAssertTrue(isNullScriptStr);
    }

    @Test
    public void isDefinedForInexistentPropertyShouldEvaluateToTrue() throws Exception {
        buildAndAssertFalse(isDefiedScriptStr);
    }
}

