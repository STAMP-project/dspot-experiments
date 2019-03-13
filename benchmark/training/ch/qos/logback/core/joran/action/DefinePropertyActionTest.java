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
package ch.qos.logback.core.joran.action;


import Status.ERROR;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.ContextBase;
import ch.qos.logback.core.joran.SimpleConfigurator;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.testUtil.CoreTestConstants;
import ch.qos.logback.core.testUtil.StatusChecker;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test {@link DefinePropertyAction}.
 *
 * @author Aleksey Didik
 */
public class DefinePropertyActionTest {
    private static final String DEFINE_INPUT_DIR = (CoreTestConstants.JORAN_INPUT_PREFIX) + "define/";

    private static final String GOOD_XML = "good.xml";

    private static final String NONAME_XML = "noname.xml";

    private static final String NOCLASS_XML = "noclass.xml";

    private static final String BADCLASS_XML = "badclass.xml";

    SimpleConfigurator simpleConfigurator;

    Context context = new ContextBase();

    DefinePropertyAction definerAction;

    InterpretationContext ic;

    StatusChecker checker = new StatusChecker(context);

    @Test
    public void good() throws JoranException {
        doConfigure(((DefinePropertyActionTest.DEFINE_INPUT_DIR) + (DefinePropertyActionTest.GOOD_XML)));
        InterpretationContext ic = simpleConfigurator.getInterpreter().getInterpretationContext();
        String inContextFoo = ic.getProperty("foo");
        Assert.assertEquals("monster", inContextFoo);
    }

    @Test
    public void noName() throws JoranException {
        doConfigure(((DefinePropertyActionTest.DEFINE_INPUT_DIR) + (DefinePropertyActionTest.NONAME_XML)));
        // get from context
        String inContextFoo = context.getProperty("foo");
        Assert.assertNull(inContextFoo);
        // check context errors
        checker.assertContainsMatch(ERROR, "Missing property name for property definer. Near \\[define\\] line 1");
    }

    @Test
    public void noClass() throws JoranException {
        doConfigure(((DefinePropertyActionTest.DEFINE_INPUT_DIR) + (DefinePropertyActionTest.NOCLASS_XML)));
        String inContextFoo = context.getProperty("foo");
        Assert.assertNull(inContextFoo);
        checker.assertContainsMatch(ERROR, "Missing class name for property definer. Near \\[define\\] line 1");
    }

    @Test
    public void testBadClass() throws JoranException {
        doConfigure(((DefinePropertyActionTest.DEFINE_INPUT_DIR) + (DefinePropertyActionTest.BADCLASS_XML)));
        // get from context
        String inContextFoo = context.getProperty("foo");
        Assert.assertNull(inContextFoo);
        // check context errors
        checker.assertContainsMatch(ERROR, "Could not create an PropertyDefiner of type");
    }
}

