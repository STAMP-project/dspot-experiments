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


public class AmplFileAppenderTest extends ch.qos.logback.core.appender.AbstractAppenderTest<java.lang.Object> {
    int diff = ch.qos.logback.core.testUtil.RandomUtil.getPositiveInt();

    protected ch.qos.logback.core.Appender<java.lang.Object> getAppender() {
        return new ch.qos.logback.core.FileAppender<java.lang.Object>();
    }

    protected ch.qos.logback.core.Appender<java.lang.Object> getConfiguredAppender() {
        ch.qos.logback.core.FileAppender<java.lang.Object> appender = new ch.qos.logback.core.FileAppender<java.lang.Object>();
        appender.setEncoder(new ch.qos.logback.core.encoder.NopEncoder<java.lang.Object>());
        appender.setFile(((ch.qos.logback.core.util.CoreTestConstants.OUTPUT_DIR_PREFIX) + "temp.log"));
        appender.setName("test");
        appender.setContext(context);
        appender.start();
        return appender;
    }

    @org.junit.Test
    public void smoke() {
        java.lang.String filename = (ch.qos.logback.core.util.CoreTestConstants.OUTPUT_DIR_PREFIX) + "/fat-smoke.log";
        ch.qos.logback.core.FileAppender<java.lang.Object> appender = new ch.qos.logback.core.FileAppender<java.lang.Object>();
        appender.setEncoder(new ch.qos.logback.core.encoder.DummyEncoder<java.lang.Object>());
        appender.setAppend(false);
        appender.setFile(filename);
        appender.setName("smoke");
        appender.setContext(context);
        appender.start();
        appender.doAppend(new java.lang.Object());
        appender.stop();
        java.io.File file = new java.io.File(filename);
        org.junit.Assert.assertTrue(file.exists());
        org.junit.Assert.assertTrue(("failed to delete " + (file.getAbsolutePath())), file.delete());
    }

    @org.junit.Test
    public void testCreateParentFolders() {
        java.lang.String filename = (((ch.qos.logback.core.util.CoreTestConstants.OUTPUT_DIR_PREFIX) + "/fat-testCreateParentFolders-") + (diff)) + "/testCreateParentFolders.txt";
        java.io.File file = new java.io.File(filename);
        org.junit.Assert.assertFalse(file.getParentFile().exists());
        org.junit.Assert.assertFalse(file.exists());
        ch.qos.logback.core.FileAppender<java.lang.Object> appender = new ch.qos.logback.core.FileAppender<java.lang.Object>();
        appender.setEncoder(new ch.qos.logback.core.encoder.DummyEncoder<java.lang.Object>());
        appender.setAppend(false);
        appender.setFile(filename);
        appender.setName("testCreateParentFolders");
        appender.setContext(context);
        appender.start();
        appender.doAppend(new java.lang.Object());
        appender.stop();
        org.junit.Assert.assertTrue(file.getParentFile().exists());
        org.junit.Assert.assertTrue(file.exists());
        // cleanup
        org.junit.Assert.assertTrue(("failed to delete " + (file.getAbsolutePath())), file.delete());
        java.io.File parent = file.getParentFile();
        org.junit.Assert.assertTrue(("failed to delete " + (parent.getAbsolutePath())), parent.delete());
    }

    @org.junit.Test
    public void testPrudentModeLogicalImplications() {
        java.lang.String filename = ((ch.qos.logback.core.util.CoreTestConstants.OUTPUT_DIR_PREFIX) + (diff)) + "fat-testPrudentModeLogicalImplications.txt";
        java.io.File file = new java.io.File(filename);
        ch.qos.logback.core.FileAppender<java.lang.Object> appender = new ch.qos.logback.core.FileAppender<java.lang.Object>();
        appender.setEncoder(new ch.qos.logback.core.encoder.DummyEncoder<java.lang.Object>());
        appender.setFile(filename);
        appender.setName("testPrudentModeLogicalImplications");
        appender.setContext(context);
        appender.setAppend(false);
        appender.setPrudent(true);
        appender.start();
        org.junit.Assert.assertTrue(appender.isAppend());
        ch.qos.logback.core.status.StatusManager sm = context.getStatusManager();
        // StatusPrinter.print(context);
        ch.qos.logback.core.status.StatusChecker statusChecker = new ch.qos.logback.core.status.StatusChecker(context);
        org.junit.Assert.assertEquals(ch.qos.logback.core.status.Status.WARN, statusChecker.getHighestLevel(0));
        java.util.List<ch.qos.logback.core.status.Status> statusList = sm.getCopyOfStatusList();
        org.junit.Assert.assertTrue(("Expecting status list size to be 2 or larger, but was " + (statusList.size())), ((statusList.size()) >= 2));
        java.lang.String msg1 = statusList.get(1).getMessage();
        org.junit.Assert.assertTrue((("Got message [" + msg1) + "]"), msg1.startsWith("Setting \"Append\" property"));
        appender.doAppend(new java.lang.Object());
        appender.stop();
        org.junit.Assert.assertTrue(file.exists());
        org.junit.Assert.assertTrue(("failed to delete " + (file.getAbsolutePath())), file.delete());
    }

    @org.junit.Test
    public void fileNameCollision() {
        java.lang.String fileName = ((ch.qos.logback.core.util.CoreTestConstants.OUTPUT_DIR_PREFIX) + (diff)) + "fileNameCollision";
        ch.qos.logback.core.FileAppender<java.lang.Object> appender0 = new ch.qos.logback.core.FileAppender<java.lang.Object>();
        appender0.setName("FA0");
        appender0.setFile(fileName);
        appender0.setContext(context);
        appender0.setEncoder(new ch.qos.logback.core.encoder.DummyEncoder<java.lang.Object>());
        appender0.start();
        org.junit.Assert.assertTrue(appender0.isStarted());
        ch.qos.logback.core.FileAppender<java.lang.Object> appender1 = new ch.qos.logback.core.FileAppender<java.lang.Object>();
        appender1.setName("FA1");
        appender1.setFile(fileName);
        appender1.setContext(context);
        appender1.setEncoder(new ch.qos.logback.core.encoder.DummyEncoder<java.lang.Object>());
        appender1.start();
        org.junit.Assert.assertFalse(appender1.isStarted());
        ch.qos.logback.core.util.StatusPrinter.print(context);
        ch.qos.logback.core.status.StatusChecker checker = new ch.qos.logback.core.status.StatusChecker(context);
        checker.assertContainsMatch(ch.qos.logback.core.status.Status.ERROR, "'File' option has the same value");
    }
}

