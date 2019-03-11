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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.Test;


public class ExtendedThrowableProxyConverterTest {
    LoggerContext lc = new LoggerContext();

    ExtendedThrowableProxyConverter etpc = new ExtendedThrowableProxyConverter();

    StringWriter sw = new StringWriter();

    PrintWriter pw = new PrintWriter(sw);

    @Test
    public void integration() {
        PatternLayout pl = new PatternLayout();
        pl.setContext(lc);
        pl.setPattern("%m%n%xEx");
        pl.start();
        ILoggingEvent e = createLoggingEvent(new Exception("x"));
        String res = pl.doLayout(e);
        // make sure that at least some package data was output
        Pattern p = Pattern.compile("\\s*at .*?\\[.*?\\]");
        Matcher m = p.matcher(res);
        int i = 0;
        while (m.find()) {
            i++;
        } 
        assertThat(i).isGreaterThan(5);
    }

    @Test
    public void smoke() {
        Exception t = new Exception("smoke");
        verify(t);
    }

    @Test
    public void nested() {
        Throwable t = makeNestedException(1);
        verify(t);
    }
}

