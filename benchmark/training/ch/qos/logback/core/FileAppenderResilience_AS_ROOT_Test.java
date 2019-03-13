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


import ch.qos.logback.core.testUtil.RandomUtil;
import ch.qos.logback.core.util.ResilienceUtil;
import java.io.IOException;
import org.junit.Test;


public class FileAppenderResilience_AS_ROOT_Test {
    static String MOUNT_POINT = "/mnt/loop/";

    static String LONG_STR = " xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";

    static String PATH_LOOPFS_SCRIPT = "/home/ceki/java/logback/logback-core/src/test/loopfs.sh";

    enum LoopFSCommand {

        setup,
        shake,
        teardown;}

    Context context = new ContextBase();

    int diff = RandomUtil.getPositiveInt();

    String outputDirStr = (((FileAppenderResilience_AS_ROOT_Test.MOUNT_POINT) + "resilience-") + (diff)) + "/";

    String logfileStr = (outputDirStr) + "output.log";

    FileAppender<Object> fa = new FileAppender<Object>();

    static int TOTAL_DURATION = 5000;

    static int NUM_STEPS = 500;

    static int DELAY = (FileAppenderResilience_AS_ROOT_Test.TOTAL_DURATION) / (FileAppenderResilience_AS_ROOT_Test.NUM_STEPS);

    @Test
    public void go() throws IOException, InterruptedException {
        if (!(FileAppenderResilience_AS_ROOT_Test.isConformingHost())) {
            return;
        }
        Process p = runLoopFSScript(FileAppenderResilience_AS_ROOT_Test.LoopFSCommand.shake);
        for (int i = 0; i < (FileAppenderResilience_AS_ROOT_Test.NUM_STEPS); i++) {
            fa.append(((String.valueOf(i)) + (FileAppenderResilience_AS_ROOT_Test.LONG_STR)));
            Thread.sleep(FileAppenderResilience_AS_ROOT_Test.DELAY);
        }
        p.waitFor();
        // the extrernal script has the file system ready for IO 50% of the time
        double bestCase = 0.5;
        ResilienceUtil.verify(logfileStr, "^(\\d{1,3}) x*$", FileAppenderResilience_AS_ROOT_Test.NUM_STEPS, (bestCase * 0.6));
        System.out.println("Done go");
    }
}

