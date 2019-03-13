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


import ch.qos.logback.core.recovery.RecoveryCoordinator;
import ch.qos.logback.core.testUtil.CoreTestConstants;
import ch.qos.logback.core.testUtil.RandomUtil;
import ch.qos.logback.core.util.ResilienceUtil;
import java.io.IOException;
import org.junit.Test;


public class FileAppenderResilienceTest {
    FileAppender<Object> fa = new FileAppender<Object>();

    Context context = new ContextBase();

    int diff = RandomUtil.getPositiveInt();

    String outputDirStr = (((CoreTestConstants.OUTPUT_DIR_PREFIX) + "resilience-") + (diff)) + "/";

    // String outputDirStr = "\\\\192.168.1.3\\lbtest\\" + "resilience-"+ diff +
    // "/";;
    String logfileStr = (outputDirStr) + "output.log";

    @Test
    public void smoke() throws IOException, InterruptedException {
        Runner runner = new Runner(fa);
        Thread t = new Thread(runner);
        t.start();
        double delayCoefficient = 2.0;
        for (int i = 0; i < 5; i++) {
            Thread.sleep(((int) ((RecoveryCoordinator.BACKOFF_COEFFICIENT_MIN) * delayCoefficient)));
            closeLogFileOnPurpose();
        }
        runner.setDone(true);
        t.join();
        double bestCaseSuccessRatio = 1 / delayCoefficient;
        // expect to loose at most 35% of the events
        double lossinessFactor = 0.35;
        double resilianceFactor = 1 - lossinessFactor;
        ResilienceUtil.verify(logfileStr, "^hello (\\d{1,5})$", runner.getCounter(), (bestCaseSuccessRatio * resilianceFactor));
    }
}

