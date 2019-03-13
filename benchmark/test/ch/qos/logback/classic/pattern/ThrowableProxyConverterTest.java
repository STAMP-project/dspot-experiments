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
package ch.qos.logback.classic.pattern;


import CoreConstants.LINE_SEPARATOR;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.util.TestHelper;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


public class ThrowableProxyConverterTest {
    LoggerContext lc = new LoggerContext();

    ThrowableProxyConverter tpc = new ThrowableProxyConverter();

    StringWriter sw = new StringWriter();

    PrintWriter pw = new PrintWriter(sw);

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

    @Test
    public void smoke() {
        Exception t = new Exception("smoke");
        verify(t);
    }

    @Test
    public void nested() {
        Throwable t = TestHelper.makeNestedException(1);
        verify(t);
    }

    @Test
    public void withArgumentOfOne() throws Exception {
        final Throwable t = TestHelper.makeNestedException(0);
        t.printStackTrace(pw);
        final ILoggingEvent le = createLoggingEvent(t);
        final List<String> optionList = Arrays.asList("1");
        tpc.setOptionList(optionList);
        tpc.start();
        final String result = tpc.convert(le);
        final BufferedReader reader = new BufferedReader(new StringReader(result));
        Assert.assertTrue(reader.readLine().contains(t.getMessage()));
        Assert.assertNotNull(reader.readLine());
        Assert.assertNull("Unexpected line in stack trace", reader.readLine());
    }

    @Test
    public void withShortArgument() throws Exception {
        final Throwable t = TestHelper.makeNestedException(0);
        t.printStackTrace(pw);
        final ILoggingEvent le = createLoggingEvent(t);
        final List<String> options = Arrays.asList("short");
        tpc.setOptionList(options);
        tpc.start();
        final String result = tpc.convert(le);
        final BufferedReader reader = new BufferedReader(new StringReader(result));
        Assert.assertTrue(reader.readLine().contains(t.getMessage()));
        Assert.assertNotNull(reader.readLine());
        Assert.assertNull("Unexpected line in stack trace", reader.readLine());
    }

    @Test
    public void skipSelectedLine() throws Exception {
        String nameOfContainingMethod = "skipSelectedLine";
        // given
        final Throwable t = TestHelper.makeNestedException(0);
        t.printStackTrace(pw);
        final ILoggingEvent le = createLoggingEvent(t);
        tpc.setOptionList(Arrays.asList("full", nameOfContainingMethod));
        tpc.start();
        // when
        final String result = tpc.convert(le);
        // then
        Assert.assertThat(result).doesNotContain(nameOfContainingMethod);
    }

    @Test
    public void skipMultipleLines() throws Exception {
        String nameOfContainingMethod = "skipMultipleLines";
        // given
        final Throwable t = TestHelper.makeNestedException(0);
        t.printStackTrace(pw);
        final ILoggingEvent le = createLoggingEvent(t);
        tpc.setOptionList(Arrays.asList("full", nameOfContainingMethod, "junit"));
        tpc.start();
        // when
        final String result = tpc.convert(le);
        // then
        Assert.assertThat(result).doesNotContain(nameOfContainingMethod).doesNotContain("junit");
    }

    @Test
    public void shouldLimitTotalLinesExcludingSkipped() throws Exception {
        // given
        final Throwable t = TestHelper.makeNestedException(0);
        t.printStackTrace(pw);
        final ILoggingEvent le = createLoggingEvent(t);
        tpc.setOptionList(Arrays.asList("3", "shouldLimitTotalLinesExcludingSkipped"));
        tpc.start();
        // when
        final String result = tpc.convert(le);
        // then
        String[] lines = result.split(LINE_SEPARATOR);
        Assert.assertThat(lines).hasSize((3 + 1));
    }
}

