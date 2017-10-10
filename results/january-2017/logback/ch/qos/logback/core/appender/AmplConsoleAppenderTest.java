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


package ch.qos.logback.core.appender;


/**
 * Redirecting System.out is quite messy. Disable this test in Maven bu not in Package.class
 */
public class AmplConsoleAppenderTest extends ch.qos.logback.core.appender.AbstractAppenderTest<java.lang.Object> {
    ch.qos.logback.core.appender.XTeeOutputStream tee;

    java.io.PrintStream original;

    @org.junit.Before
    public void setUp() {
        original = java.lang.System.out;
        // tee will output bytes on System out but it will also
        // collect them so that the output can be compared against
        // some expected output data
        // tee = new TeeOutputStream(original);
        // keep the console quiet
        tee = new ch.qos.logback.core.appender.XTeeOutputStream(null);
        // redirect System.out to tee
        java.lang.System.setOut(new java.io.PrintStream(tee));
    }

    @org.junit.After
    public void tearDown() {
        java.lang.System.setOut(original);
    }

    @java.lang.Override
    public ch.qos.logback.core.Appender<java.lang.Object> getAppender() {
        return new ch.qos.logback.core.ConsoleAppender<java.lang.Object>();
    }

    protected ch.qos.logback.core.Appender<java.lang.Object> getConfiguredAppender() {
        ch.qos.logback.core.ConsoleAppender<java.lang.Object> ca = new ch.qos.logback.core.ConsoleAppender<java.lang.Object>();
        ca.setEncoder(new ch.qos.logback.core.encoder.NopEncoder<java.lang.Object>());
        ca.start();
        return ca;
    }

    @org.junit.Test
    public void smoke() {
        ch.qos.logback.core.ConsoleAppender<java.lang.Object> ca = ((ch.qos.logback.core.ConsoleAppender<java.lang.Object>) (getAppender()));
        ca.setEncoder(new ch.qos.logback.core.encoder.DummyEncoder<java.lang.Object>());
        ca.start();
        ca.doAppend(new java.lang.Object());
        org.junit.Assert.assertEquals(ch.qos.logback.core.layout.DummyLayout.DUMMY, tee.toString());
    }

    @org.junit.Test
    public void open() {
        ch.qos.logback.core.ConsoleAppender<java.lang.Object> ca = ((ch.qos.logback.core.ConsoleAppender<java.lang.Object>) (getAppender()));
        ch.qos.logback.core.encoder.DummyEncoder<java.lang.Object> dummyEncoder = new ch.qos.logback.core.encoder.DummyEncoder<java.lang.Object>();
        dummyEncoder.setFileHeader("open");
        ca.setEncoder(dummyEncoder);
        ca.start();
        ca.doAppend(new java.lang.Object());
        ca.stop();
        org.junit.Assert.assertEquals((("open" + (ch.qos.logback.core.CoreConstants.LINE_SEPARATOR)) + (ch.qos.logback.core.layout.DummyLayout.DUMMY)), tee.toString());
    }

    @org.junit.Test
    public void testClose() {
        ch.qos.logback.core.ConsoleAppender<java.lang.Object> ca = ((ch.qos.logback.core.ConsoleAppender<java.lang.Object>) (getAppender()));
        ch.qos.logback.core.encoder.DummyEncoder<java.lang.Object> dummyEncoder = new ch.qos.logback.core.encoder.DummyEncoder<java.lang.Object>();
        dummyEncoder.setFileFooter("CLOSED");
        ca.setEncoder(dummyEncoder);
        ca.start();
        ca.doAppend(new java.lang.Object());
        ca.stop();
        // ConsoleAppender must keep the underlying stream open.
        // The console is not ours to close.
        org.junit.Assert.assertFalse(tee.isClosed());
        org.junit.Assert.assertEquals(((ch.qos.logback.core.layout.DummyLayout.DUMMY) + "CLOSED"), tee.toString());
    }

    // See http://jira.qos.ch/browse/LBCORE-143
    @org.junit.Test
    public void changeInConsole() {
        ch.qos.logback.core.ConsoleAppender<java.lang.Object> ca = ((ch.qos.logback.core.ConsoleAppender<java.lang.Object>) (getAppender()));
        ch.qos.logback.core.encoder.EchoEncoder<java.lang.Object> encoder = new ch.qos.logback.core.encoder.EchoEncoder<java.lang.Object>();
        ca.setEncoder(encoder);
        ca.start();
        ca.doAppend("a");
        org.junit.Assert.assertEquals(("a" + (ch.qos.logback.core.CoreConstants.LINE_SEPARATOR)), tee.toString());
        ch.qos.logback.core.appender.XTeeOutputStream newTee = new ch.qos.logback.core.appender.XTeeOutputStream(null);
        java.lang.System.setOut(new java.io.PrintStream(newTee));
        ca.doAppend("b");
        org.junit.Assert.assertEquals(("b" + (ch.qos.logback.core.CoreConstants.LINE_SEPARATOR)), newTee.toString());
    }

    @org.junit.Test
    public void testUTF16BE() throws java.io.UnsupportedEncodingException {
        ch.qos.logback.core.ConsoleAppender<java.lang.Object> ca = ((ch.qos.logback.core.ConsoleAppender<java.lang.Object>) (getAppender()));
        ch.qos.logback.core.encoder.DummyEncoder<java.lang.Object> dummyEncoder = new ch.qos.logback.core.encoder.DummyEncoder<java.lang.Object>();
        java.nio.charset.Charset utf16BE = java.nio.charset.Charset.forName("UTF-16BE");
        dummyEncoder.setCharset(utf16BE);
        ca.setEncoder(dummyEncoder);
        ca.start();
        ca.doAppend(new java.lang.Object());
        org.junit.Assert.assertEquals(ch.qos.logback.core.layout.DummyLayout.DUMMY, new java.lang.String(tee.toByteArray(), utf16BE));
    }

    @org.junit.Test
    public void wrongTarget() {
        ch.qos.logback.core.ConsoleAppender<java.lang.Object> ca = ((ch.qos.logback.core.ConsoleAppender<java.lang.Object>) (getAppender()));
        ch.qos.logback.core.encoder.EchoEncoder<java.lang.Object> encoder = new ch.qos.logback.core.encoder.EchoEncoder<java.lang.Object>();
        encoder.setContext(context);
        ca.setContext(context);
        ca.setTarget("foo");
        ca.setEncoder(encoder);
        ca.start();
        ca.doAppend("a");
        ch.qos.logback.core.status.StatusChecker checker = new ch.qos.logback.core.status.StatusChecker(context);
        // 21:28:01,246 + WARN in ch.qos.logback.core.ConsoleAppender[null] - [foo] should be one of [System.out,
        // System.err]
        // 21:28:01,246 |-WARN in ch.qos.logback.core.ConsoleAppender[null] - Using previously set target, System.out by
        // default.
        // StatusPrinter.print(context);
        checker.assertContainsMatch(ch.qos.logback.core.status.Status.WARN, "\\[foo\\] should be one of \\[System.out, System.err\\]");
    }
}

