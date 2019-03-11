/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.plus.webapp;


import Origin.WebFragment;
import java.lang.reflect.InvocationTargetException;
import org.eclipse.jetty.webapp.Descriptor;
import org.eclipse.jetty.webapp.FragmentDescriptor;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebDescriptor;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * PlusDescriptorProcessorTest
 */
public class PlusDescriptorProcessorTest {
    protected WebDescriptor webDescriptor;

    protected FragmentDescriptor fragDescriptor1;

    protected FragmentDescriptor fragDescriptor2;

    protected FragmentDescriptor fragDescriptor3;

    protected FragmentDescriptor fragDescriptor4;

    protected WebAppContext context;

    @Test
    public void testMissingResourceDeclaration() throws Exception {
        ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(context.getClassLoader());
        InvocationTargetException x = Assertions.assertThrows(InvocationTargetException.class, () -> {
            PlusDescriptorProcessor pdp = new PlusDescriptorProcessor();
            pdp.process(context, fragDescriptor4);
            Assertions.fail("Expected missing resource declaration");
        });
        Thread.currentThread().setContextClassLoader(oldLoader);
        MatcherAssert.assertThat(x.getCause(), Matchers.is(Matchers.notNullValue()));
        MatcherAssert.assertThat(x.getCause().getMessage(), Matchers.containsString("jdbc/mymissingdatasource"));
    }

    @Test
    public void testWebXmlResourceDeclarations() throws Exception {
        // if declared in web.xml, fragment declarations ignored
        ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(context.getClassLoader());
        try {
            PlusDescriptorProcessor pdp = new PlusDescriptorProcessor();
            pdp.process(context, webDescriptor);
            Descriptor d = context.getMetaData().getOriginDescriptor("resource-ref.jdbc/mydatasource");
            Assertions.assertNotNull(d);
            Assertions.assertTrue((d == (webDescriptor)));
            pdp.process(context, fragDescriptor1);
            pdp.process(context, fragDescriptor2);
        } finally {
            Thread.currentThread().setContextClassLoader(oldLoader);
        }
    }

    @Test
    public void testMismatchedFragmentResourceDeclarations() throws Exception {
        // if declared in more than 1 fragment, declarations must be the same
        ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(context.getClassLoader());
        try {
            PlusDescriptorProcessor pdp = new PlusDescriptorProcessor();
            pdp.process(context, fragDescriptor1);
            Descriptor d = context.getMetaData().getOriginDescriptor("resource-ref.jdbc/mydatasource");
            Assertions.assertNotNull(d);
            Assertions.assertTrue((d == (fragDescriptor1)));
            Assertions.assertEquals(WebFragment, context.getMetaData().getOrigin("resource-ref.jdbc/mydatasource"));
            pdp.process(context, fragDescriptor2);
            Assertions.fail("Expected conflicting resource-ref declaration");
        } catch (Exception e) {
            // expected
        } finally {
            Thread.currentThread().setContextClassLoader(oldLoader);
        }
    }

    @Test
    public void testMatchingFragmentResourceDeclarations() throws Exception {
        // if declared in more than 1 fragment, declarations must be the same
        ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(context.getClassLoader());
        try {
            PlusDescriptorProcessor pdp = new PlusDescriptorProcessor();
            pdp.process(context, fragDescriptor1);
            Descriptor d = context.getMetaData().getOriginDescriptor("resource-ref.jdbc/mydatasource");
            Assertions.assertNotNull(d);
            Assertions.assertTrue((d == (fragDescriptor1)));
            Assertions.assertEquals(WebFragment, context.getMetaData().getOrigin("resource-ref.jdbc/mydatasource"));
            pdp.process(context, fragDescriptor3);
        } finally {
            Thread.currentThread().setContextClassLoader(oldLoader);
        }
    }
}

