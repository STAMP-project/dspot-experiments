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


package ch.qos.logback.core;


public class AmplFileAppenderResilienceTest {
    ch.qos.logback.core.FileAppender<java.lang.Object> fa = new ch.qos.logback.core.FileAppender<java.lang.Object>();

    ch.qos.logback.core.Context context = new ch.qos.logback.core.ContextBase();

    int diff = ch.qos.logback.core.testUtil.RandomUtil.getPositiveInt();

    java.lang.String outputDirStr = (((ch.qos.logback.core.util.CoreTestConstants.OUTPUT_DIR_PREFIX) + "resilience-") + (diff)) + "/";

    // String outputDirStr = "\\\\192.168.1.3\\lbtest\\" + "resilience-"+ diff +
    // "/";;
    java.lang.String logfileStr = (outputDirStr) + "output.log";

    @org.junit.Before
    public void setUp() throws java.lang.InterruptedException {
        context.getStatusManager().add(new ch.qos.logback.core.status.OnConsoleStatusListener());
        java.io.File outputDir = new java.io.File(outputDirStr);
        outputDir.mkdirs();
        fa.setContext(context);
        fa.setName("FILE");
        fa.setEncoder(new ch.qos.logback.core.encoder.EchoEncoder<java.lang.Object>());
        fa.setFile(logfileStr);
        fa.start();
    }

    @org.junit.Test
    @org.junit.Ignore
    public void manual() throws java.io.IOException, java.lang.InterruptedException {
        ch.qos.logback.core.Runner runner = new ch.qos.logback.core.Runner(fa);
        java.lang.Thread t = new java.lang.Thread(runner);
        t.start();
        while (true) {
            java.lang.Thread.sleep(110);
        } 
    }

    @org.junit.Test
    public void smoke() throws java.io.IOException, java.lang.InterruptedException {
        ch.qos.logback.core.Runner runner = new ch.qos.logback.core.Runner(fa);
        java.lang.Thread t = new java.lang.Thread(runner);
        t.start();
        double delayCoefficient = 2.0;
        for (int i = 0; i < 5; i++) {
            java.lang.Thread.sleep(((int) ((ch.qos.logback.core.recovery.RecoveryCoordinator.BACKOFF_COEFFICIENT_MIN) * delayCoefficient)));
            closeLogFileOnPurpose();
        }
        runner.setDone(true);
        t.join();
        double bestCaseSuccessRatio = 1 / delayCoefficient;
        // expect to loose at most 35% of the events
        double lossinessFactor = 0.35;
        double resilianceFactor = 1 - lossinessFactor;
        ch.qos.logback.core.util.ResilienceUtil.verify(logfileStr, "^hello (\\d{1,5})$", runner.getCounter(), (bestCaseSuccessRatio * resilianceFactor));
    }

    private void closeLogFileOnPurpose() throws java.io.IOException {
        ch.qos.logback.core.recovery.ResilientFileOutputStream resilientFOS = ((ch.qos.logback.core.recovery.ResilientFileOutputStream) (fa.getOutputStream()));
        java.nio.channels.FileChannel fileChannel = resilientFOS.getChannel();
        fileChannel.close();
    }
}

