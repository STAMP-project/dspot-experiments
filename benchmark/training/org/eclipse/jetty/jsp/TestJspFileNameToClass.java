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
package org.eclipse.jetty.jsp;


import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class TestJspFileNameToClass {
    @Test
    public void testJspFileNameToClassName() throws Exception {
        ServletHolder h = new ServletHolder();
        h.setName("test");
        Assertions.assertEquals(null, h.getClassNameForJsp(null));
        Assertions.assertEquals(null, h.getClassNameForJsp(""));
        Assertions.assertEquals(null, h.getClassNameForJsp("/blah/"));
        Assertions.assertEquals(null, h.getClassNameForJsp("//blah///"));
        Assertions.assertEquals(null, h.getClassNameForJsp("/a/b/c/blah/"));
        Assertions.assertEquals("org.apache.jsp.a.b.c.blah", h.getClassNameForJsp("/a/b/c/blah"));
        Assertions.assertEquals("org.apache.jsp.blah_jsp", h.getClassNameForJsp("/blah.jsp"));
        Assertions.assertEquals("org.apache.jsp.blah_jsp", h.getClassNameForJsp("//blah.jsp"));
        Assertions.assertEquals("org.apache.jsp.blah_jsp", h.getClassNameForJsp("blah.jsp"));
        Assertions.assertEquals("org.apache.jsp.a.b.c.blah_jsp", h.getClassNameForJsp("/a/b/c/blah.jsp"));
        Assertions.assertEquals("org.apache.jsp.a.b.c.blah_jsp", h.getClassNameForJsp("a/b/c/blah.jsp"));
    }
}

