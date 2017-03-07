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


public class AmplDummyAppenderTest extends ch.qos.logback.core.appender.AbstractAppenderTest<java.lang.Object> {
    java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();

    ch.qos.logback.core.appender.DummyWriterAppender<java.lang.Object> da = new ch.qos.logback.core.appender.DummyWriterAppender<java.lang.Object>(baos);

    protected ch.qos.logback.core.Appender<java.lang.Object> getAppender() {
        return da;
    }

    protected ch.qos.logback.core.Appender<java.lang.Object> getConfiguredAppender() {
        da.setEncoder(new ch.qos.logback.core.encoder.DummyEncoder<java.lang.Object>());
        da.start();
        return da;
    }

    @org.junit.Test
    public void testBasic() throws java.io.IOException {
        ch.qos.logback.core.encoder.Encoder<java.lang.Object> encoder = new ch.qos.logback.core.encoder.DummyEncoder<java.lang.Object>();
        da.setEncoder(encoder);
        da.start();
        da.doAppend(new java.lang.Object());
        org.junit.Assert.assertEquals(ch.qos.logback.core.layout.DummyLayout.DUMMY, baos.toString());
    }
}

