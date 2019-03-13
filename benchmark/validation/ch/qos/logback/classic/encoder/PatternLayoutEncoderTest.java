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
package ch.qos.logback.classic.encoder;


import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import org.junit.Assert;
import org.junit.Test;


public class PatternLayoutEncoderTest {
    PatternLayoutEncoder ple = new PatternLayoutEncoder();

    LoggerContext context = new LoggerContext();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    Logger logger = context.getLogger(PatternLayoutEncoderTest.class);

    Charset utf8Charset = Charset.forName("UTF-8");

    @Test
    public void smoke() throws IOException {
        init(baos);
        String msg = "hello";
        ILoggingEvent event = makeLoggingEvent(msg);
        byte[] eventBytes = ple.encode(event);
        baos.write(eventBytes);
        ple.footerBytes();
        Assert.assertEquals(msg, baos.toString());
    }

    @Test
    public void charset() throws IOException {
        ple.setCharset(utf8Charset);
        init(baos);
        String msg = "\u03b1";
        ILoggingEvent event = makeLoggingEvent(msg);
        byte[] eventBytes = ple.encode(event);
        baos.write(eventBytes);
        ple.footerBytes();
        Assert.assertEquals(msg, new String(baos.toByteArray(), utf8Charset));
    }
}

