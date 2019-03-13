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
import org.junit.Test;


public class IfThenElseAndIncludeCompositionTest {
    Context context = new ContextBase();

    TrivialConfigurator tc;

    int diff = RandomUtil.getPositiveInt();

    static final String CONDITIONAL_DIR_PREFIX = (CoreTestConstants.JORAN_INPUT_PREFIX) + "conditional/";

    static final String THEN_FILE_TO_INCLUDE_KEY = "thenFileToInclude";

    static final String ELSE_FILE_TO_INCLUDE_KEY = "elseFileToInclude";

    static final String NESTED_INCLUDE_FILE = (IfThenElseAndIncludeCompositionTest.CONDITIONAL_DIR_PREFIX) + "nestedInclude.xml";

    static final String THEN_FILE_TO_INCLUDE = (IfThenElseAndIncludeCompositionTest.CONDITIONAL_DIR_PREFIX) + "includedA.xml";

    static final String ELSE_FILE_TO_INCLUDE = (IfThenElseAndIncludeCompositionTest.CONDITIONAL_DIR_PREFIX) + "includedB.xml";

    StackAction stackAction = new StackAction();

    @Test
    public void includeNestedWithinIf() throws JoranException {
        context.putProperty(IfThenElseAndIncludeCompositionTest.THEN_FILE_TO_INCLUDE_KEY, IfThenElseAndIncludeCompositionTest.THEN_FILE_TO_INCLUDE);
        context.putProperty(IfThenElseAndIncludeCompositionTest.ELSE_FILE_TO_INCLUDE_KEY, IfThenElseAndIncludeCompositionTest.ELSE_FILE_TO_INCLUDE);
        doConfigure(IfThenElseAndIncludeCompositionTest.NESTED_INCLUDE_FILE);
        verifyConfig(new String[]{ "BEGIN", "e0", "IncludedB0", "e1", "END" });
    }
}

