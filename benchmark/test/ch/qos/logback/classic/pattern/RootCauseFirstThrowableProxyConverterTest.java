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


import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.util.TestHelper;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.Test;


/**
 *
 *
 * @author Tomasz Nurkiewicz
 * @since 2010-08-15, 18:34:21
 */
public class RootCauseFirstThrowableProxyConverterTest {
    private LoggerContext context = new LoggerContext();

    private ThrowableProxyConverter converter = new RootCauseFirstThrowableProxyConverter();

    private StringWriter stringWriter = new StringWriter();

    private PrintWriter printWriter = new PrintWriter(stringWriter);

    @Test
    public void integration() {
        // given
        context.setPackagingDataEnabled(true);
        PatternLayout pl = new PatternLayout();
        pl.setContext(context);
        pl.setPattern("%m%rEx%n");
        pl.start();
        // when
        ILoggingEvent e = createLoggingEvent(new Exception("x"));
        String result = pl.doLayout(e);
        // then
        // make sure that at least some package data was output
        Pattern p = Pattern.compile("\\s*at .*?\\[.*?\\]");
        Matcher m = p.matcher(result);
        int i = 0;
        while (m.find()) {
            i++;
        } 
        assertThat(i).isGreaterThan(5);
    }

    @Test
    public void smoke() {
        // given
        Exception exception = new Exception("smoke");
        exception.printStackTrace(printWriter);
        // when
        ILoggingEvent le = createLoggingEvent(exception);
        String result = converter.convert(le);
        // then
        result = result.replace("common frames omitted", "more");
        result = result.replaceAll(" ~?\\[.*\\]", "");
        assertThat(result).isEqualTo(stringWriter.toString());
    }

    @Test
    public void nested() {
        // given
        Throwable nestedException = TestHelper.makeNestedException(2);
        nestedException.printStackTrace(printWriter);
        // when
        ILoggingEvent le = createLoggingEvent(nestedException);
        String result = converter.convert(le);
        // then
        assertThat(result).startsWith("java.lang.Exception: nesting level=0");
        assertThat(TestHelper.positionOf("nesting level=0").in(result)).isLessThan(TestHelper.positionOf("nesting level =1").in(result));
        assertThat(TestHelper.positionOf("nesting level =1").in(result)).isLessThan(TestHelper.positionOf("nesting level =2").in(result));
    }
}

