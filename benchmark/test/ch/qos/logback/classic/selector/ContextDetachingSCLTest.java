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
package ch.qos.logback.classic.selector;


import ClassicConstants.JNDI_CONTEXT_NAME;
import ch.qos.logback.classic.selector.servlet.ContextDetachingSCL;
import ch.qos.logback.classic.util.ContextSelectorStaticBinder;
import ch.qos.logback.classic.util.MockInitialContext;
import ch.qos.logback.classic.util.MockInitialContextFactory;
import org.junit.Assert;
import org.junit.Test;


public class ContextDetachingSCLTest {
    static String INITIAL_CONTEXT_KEY = "java.naming.factory.initial";

    ContextDetachingSCL contextDetachingSCL;

    @Test
    public void testDetach() {
        ContextJNDISelector selector = ((ContextJNDISelector) (ContextSelectorStaticBinder.getSingleton().getContextSelector()));
        contextDetachingSCL.contextDestroyed(null);
        Assert.assertEquals(0, selector.getCount());
    }

    @Test
    public void testDetachWithMissingContext() {
        MockInitialContext mic = MockInitialContextFactory.getContext();
        mic.map.put(JNDI_CONTEXT_NAME, "tata");
        ContextJNDISelector selector = ((ContextJNDISelector) (ContextSelectorStaticBinder.getSingleton().getContextSelector()));
        Assert.assertEquals("tata", selector.getLoggerContext().getName());
        mic.map.put(JNDI_CONTEXT_NAME, "titi");
        Assert.assertEquals("titi", selector.getLoggerContext().getName());
        contextDetachingSCL.contextDestroyed(null);
        Assert.assertEquals(2, selector.getCount());
    }
}

