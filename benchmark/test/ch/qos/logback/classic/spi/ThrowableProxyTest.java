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
package ch.qos.logback.classic.spi;


import ch.qos.logback.classic.util.TestHelper;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import org.junit.Assume;
import org.junit.Test;


public class ThrowableProxyTest {
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
    public void suppressed() throws IllegalAccessException, InvocationTargetException {
        Assume.assumeTrue(TestHelper.suppressedSupported());// only execute on Java 7, would work anyway but doesn't make

        // sense.
        Exception ex = null;
        try {
            someMethod();
        } catch (Exception e) {
            Exception fooException = new Exception("Foo");
            Exception barException = new Exception("Bar");
            TestHelper.addSuppressed(e, fooException);
            TestHelper.addSuppressed(e, barException);
            ex = e;
        }
        verify(ex);
    }

    @Test
    public void suppressedWithCause() throws IllegalAccessException, InvocationTargetException {
        Assume.assumeTrue(TestHelper.suppressedSupported());// only execute on Java 7, would work anyway but doesn't make

        // sense.
        Exception ex = null;
        try {
            someMethod();
        } catch (Exception e) {
            ex = new Exception("Wrapper", e);
            Exception fooException = new Exception("Foo");
            Exception barException = new Exception("Bar");
            TestHelper.addSuppressed(ex, fooException);
            TestHelper.addSuppressed(e, barException);
        }
        verify(ex);
    }

    @Test
    public void suppressedWithSuppressed() throws Exception {
        Assume.assumeTrue(TestHelper.suppressedSupported());// only execute on Java 7, would work anyway but doesn't make

        // sense.
        Exception ex = null;
        try {
            someMethod();
        } catch (Exception e) {
            ex = new Exception("Wrapper", e);
            Exception fooException = new Exception("Foo");
            Exception barException = new Exception("Bar");
            TestHelper.addSuppressed(barException, fooException);
            TestHelper.addSuppressed(e, barException);
        }
        verify(ex);
    }

    // see also http://jira.qos.ch/browse/LBCLASSIC-216
    @Test
    public void nullSTE() {
        Throwable t = new Exception("someMethodWithNullException") {
            private static final long serialVersionUID = 1L;

            @Override
            public StackTraceElement[] getStackTrace() {
                return null;
            }
        };
        // we can't test output as Throwable.printStackTrace method uses
        // the private getOurStackTrace method instead of getStackTrace
        // tests ThrowableProxyUtil.steArrayToStepArray
        new ThrowableProxy(t);
        // tests ThrowableProxyUtil.findNumberOfCommonFrames
        Exception top = new Exception("top", t);
        new ThrowableProxy(top);
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

