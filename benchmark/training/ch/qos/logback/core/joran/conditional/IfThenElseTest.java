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
import ch.qos.logback.core.joran.TrivialConfigurator;
import ch.qos.logback.core.joran.action.ext.StackAction;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.testUtil.CoreTestConstants;
import ch.qos.logback.core.testUtil.RandomUtil;
import ch.qos.logback.core.testUtil.StatusChecker;
import org.junit.Assert;
import org.junit.Test;


public class IfThenElseTest {
    Context context = new ContextBase();

    StatusChecker checker = new StatusChecker(context);

    TrivialConfigurator tc;

    int diff = RandomUtil.getPositiveInt();

    static final String CONDITIONAL_DIR_PREFIX = (CoreTestConstants.JORAN_INPUT_PREFIX) + "conditional/";

    String ki1 = "ki1";

    String val1 = "val1";

    String sysKey = "sysKey";

    String dynaKey = "dynaKey";

    StackAction stackAction = new StackAction();

    @Test
    public void whenContextPropertyIsSet_IfThenBranchIsEvaluated() throws JoranException {
        context.putProperty(ki1, val1);
        doConfigure(((IfThenElseTest.CONDITIONAL_DIR_PREFIX) + "if0.xml"));
        verifyConfig(new String[]{ "BEGIN", "a", "END" });
    }

    @Test
    public void whenLocalPropertyIsSet_IfThenBranchIsEvaluated() throws JoranException {
        doConfigure(((IfThenElseTest.CONDITIONAL_DIR_PREFIX) + "if_localProperty.xml"));
        verifyConfig(new String[]{ "BEGIN", "a", "END" });
    }

    @Test
    public void whenNoPropertyIsDefined_ElseBranchIsEvaluated() throws JoranException {
        doConfigure(((IfThenElseTest.CONDITIONAL_DIR_PREFIX) + "if0.xml"));
        verifyConfig(new String[]{ "BEGIN", "b", "END" });
    }

    @Test
    public void whenContextPropertyIsSet_IfThenBranchIsEvaluated_NO_ELSE_DEFINED() throws JoranException {
        context.putProperty(ki1, val1);
        doConfigure(((IfThenElseTest.CONDITIONAL_DIR_PREFIX) + "ifWithoutElse.xml"));
        verifyConfig(new String[]{ "BEGIN", "a", "END" });
    }

    @Test
    public void whenNoPropertyIsDefined_IfThenBranchIsNotEvaluated_NO_ELSE_DEFINED() throws JoranException {
        doConfigure(((IfThenElseTest.CONDITIONAL_DIR_PREFIX) + "ifWithoutElse.xml"));
        verifyConfig(new String[]{ "BEGIN", "END" });
        Assert.assertTrue(isErrorFree(0));
    }

    @Test
    public void nestedIf() throws JoranException {
        doConfigure(((IfThenElseTest.CONDITIONAL_DIR_PREFIX) + "nestedIf.xml"));
        verifyConfig(new String[]{ "BEGIN", "a", "c", "END" });
        Assert.assertTrue(isErrorFree(0));
    }

    @Test
    public void useNonExistenceOfSystemPropertyToDefineAContextProperty() throws JoranException {
        Assert.assertNull(System.getProperty(sysKey));
        Assert.assertNull(context.getProperty(dynaKey));
        doConfigure(((IfThenElseTest.CONDITIONAL_DIR_PREFIX) + "ifSystem.xml"));
        System.out.println((((dynaKey) + "=") + (context.getProperty(dynaKey))));
        Assert.assertNotNull(context.getProperty(dynaKey));
    }

    @Test
    public void noContextPropertyShouldBeDefinedIfSystemPropertyExists() throws JoranException {
        System.setProperty(sysKey, "a");
        Assert.assertNull(context.getProperty(dynaKey));
        System.out.println(((("before " + (dynaKey)) + "=") + (context.getProperty(dynaKey))));
        doConfigure(((IfThenElseTest.CONDITIONAL_DIR_PREFIX) + "ifSystem.xml"));
        System.out.println((((dynaKey) + "=") + (context.getProperty(dynaKey))));
        Assert.assertNull(context.getProperty(dynaKey));
    }
}

