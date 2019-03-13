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
package ch.qos.logback.core.joran;


import ch.qos.logback.core.Context;
import ch.qos.logback.core.ContextBase;
import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.spi.ActionException;
import ch.qos.logback.core.joran.spi.ElementSelector;
import ch.qos.logback.core.status.StatusManager;
import java.util.HashMap;
import org.junit.Test;


/**
 * Test the way Interpreter skips child elements in case of exceptions thrown by
 * Actions. It also tests addition of status messages in case of exceptions.
 *
 * @author Ceki Gulcu
 */
public class SkippingInInterpreterTest {
    HashMap<ElementSelector, Action> rulesMap = new HashMap<ElementSelector, Action>();

    Context context = new ContextBase();

    StatusManager sm = context.getStatusManager();

    @Test
    public void testSkippingRuntimeExInBadBegin() throws Exception {
        doTest("badBegin1.xml", null, IllegalStateException.class);
    }

    @Test
    public void testSkippingActionExInBadBegin() throws Exception {
        doTest("badBegin2.xml", null, ActionException.class);
    }

    @Test
    public void testSkippingRuntimeExInBadEnd() throws Exception {
        doTest("badEnd1.xml", Integer.valueOf(2), IllegalStateException.class);
    }

    @Test
    public void testSkippingActionExInBadEnd() throws Exception {
        doTest("badEnd2.xml", Integer.valueOf(2), ActionException.class);
    }
}

