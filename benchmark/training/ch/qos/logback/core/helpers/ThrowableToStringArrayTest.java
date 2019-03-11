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


import java.io.PrintWriter;
import java.io.StringWriter;
import org.junit.Test;


public class ThrowableToStringArrayTest {
    StringWriter sw = new StringWriter();

    PrintWriter pw = new PrintWriter(sw);

    @Test
    public void smoke() {
        Exception e = new Exception("smoke");
        verify(e);
    }

    @Test
    public void nested() {
        Exception w = null;
        try {
            someMethod();
        } catch (Exception e) {
            w = new Exception("wrapping", e);
        }
        verify(w);
    }

    @Test
    public void multiNested() {
        Exception w = null;
        try {
            someOtherMethod();
        } catch (Exception e) {
            w = new Exception("wrapping", e);
        }
        verify(w);
    }
}

