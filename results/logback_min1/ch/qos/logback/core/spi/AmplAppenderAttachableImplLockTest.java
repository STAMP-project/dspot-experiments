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


package ch.qos.logback.core.spi;


/**
 * This test shows the general problem I described in LBCORE-67.
 *
 * In the two test cases below, an appender that throws an OutOfMemoryError
 * while getName is called - but this is just an example to show the general
 * problem.
 *
 * The tests below fail without fixing LBCORE-67 and pass when Joern Huxhorn's
 * patch is applied.
 *
 * Additionally, the following, probably more realistic, situations could
 * happen:
 *
 * -addAppender: appenderList.add() could throw OutOfMemoryError. This could
 * only be shown by using an appenderList mock but appenderList does not (and
 * should not) have a setter. This would leave the write lock locked.
 *
 * -iteratorForAppenders: new ArrayList() could throw an OutOfMemoryError,
 * leaving the read lock locked.
 *
 * I can't imagine a bad situation in isAttached, detachAppender(Appender) or
 * detachAppender(String) but I'd change the code anyway for consistency. I'm
 * also pretty sure that something stupid can happen at any time so it's best to
 * just stick to conventions.
 *
 * @author Joern Huxhorn
 */
public class AmplAppenderAttachableImplLockTest {
    private ch.qos.logback.core.spi.AppenderAttachableImpl<java.lang.Integer> aai = new ch.qos.logback.core.spi.AppenderAttachableImpl<java.lang.Integer>();

    @java.lang.SuppressWarnings(value = "unchecked")
    @org.junit.Test(timeout = 5000)
    public void getAppenderBoom() {
        ch.qos.logback.core.Appender<java.lang.Integer> mockAppender1 = org.mockito.Mockito.mock(ch.qos.logback.core.Appender.class);
        org.mockito.Mockito.when(mockAppender1.getName()).thenThrow(new java.lang.OutOfMemoryError("oops"));
        aai.addAppender(mockAppender1);
        try {
            // appender.getName called as a result of next statement
            aai.getAppender("foo");
        } catch (java.lang.OutOfMemoryError e) {
            // this leaves the read lock locked.
        }
        ch.qos.logback.core.Appender<java.lang.Integer> mockAppender2 = org.mockito.Mockito.mock(ch.qos.logback.core.Appender.class);
        // the next call used to freeze with the earlier ReadWriteLock lock
        // implementation
        aai.addAppender(mockAppender2);
    }

    @java.lang.SuppressWarnings(value = "unchecked")
    @org.junit.Test(timeout = 5000)
    public void detachAppenderBoom() throws java.lang.InterruptedException {
        ch.qos.logback.core.Appender<java.lang.Integer> mockAppender = org.mockito.Mockito.mock(ch.qos.logback.core.Appender.class);
        org.mockito.Mockito.when(mockAppender.getName()).thenThrow(new java.lang.OutOfMemoryError("oops"));
        mockAppender.doAppend(17);
        aai.addAppender(mockAppender);
        java.lang.Thread t = new java.lang.Thread(new java.lang.Runnable() {
            public void run() {
                try {
                    // appender.getName called as a result of next statement
                    aai.detachAppender("foo");
                } catch (java.lang.OutOfMemoryError e) {
                    // this leaves the write lock locked.
                }
            }
        });
        t.start();
        t.join();
        // the next call used to freeze with the earlier ReadWriteLock lock
        // implementation
        aai.appendLoopOnAppenders(17);
    }
}

