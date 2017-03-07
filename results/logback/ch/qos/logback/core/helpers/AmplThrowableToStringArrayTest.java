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


package ch.qos.logback.core.helpers;


public class AmplThrowableToStringArrayTest {
    java.io.StringWriter sw = new java.io.StringWriter();

    java.io.PrintWriter pw = new java.io.PrintWriter(sw);

    @org.junit.Before
    public void setUp() throws java.lang.Exception {
    }

    @org.junit.After
    public void tearDown() throws java.lang.Exception {
    }

    public void verify(java.lang.Throwable t) {
        t.printStackTrace(pw);
        java.lang.String[] sa = ch.qos.logback.core.helpers.ThrowableToStringArray.convert(t);
        java.lang.StringBuilder sb = new java.lang.StringBuilder();
        for (java.lang.String tdp : sa) {
            sb.append(tdp);
            sb.append(ch.qos.logback.core.CoreConstants.LINE_SEPARATOR);
        }
        java.lang.String expected = sw.toString();
        java.lang.String result = sb.toString().replace("common frames omitted", "more");
        org.junit.Assert.assertEquals(expected, result);
    }

    @org.junit.Test
    public void smoke() {
        java.lang.Exception e = new java.lang.Exception("smoke");
        verify(e);
    }

    @org.junit.Test
    public void nested() {
        java.lang.Exception w = null;
        try {
            someMethod();
        } catch (java.lang.Exception e) {
            w = new java.lang.Exception("wrapping", e);
        }
        verify(w);
    }

    @org.junit.Test
    public void multiNested() {
        java.lang.Exception w = null;
        try {
            someOtherMethod();
        } catch (java.lang.Exception e) {
            w = new java.lang.Exception("wrapping", e);
        }
        verify(w);
    }

    void someMethod() throws java.lang.Exception {
        throw new java.lang.Exception("someMethod");
    }

    void someOtherMethod() throws java.lang.Exception {
        try {
            someMethod();
        } catch (java.lang.Exception e) {
            throw new java.lang.Exception("someOtherMethod", e);
        }
    }
}

