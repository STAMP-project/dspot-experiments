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


public class AmplFileAppenderResilience_AS_ROOT_Test {
    enum LoopFSCommand {
        setup, shake, teardown;}

    static java.lang.String MOUNT_POINT = "/mnt/loop/";

    static java.lang.String LONG_STR = " xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";

    static java.lang.String PATH_LOOPFS_SCRIPT = "/home/ceki/java/logback/logback-core/src/test/loopfs.sh";

    ch.qos.logback.core.Context context = new ch.qos.logback.core.ContextBase();

    int diff = ch.qos.logback.core.testUtil.RandomUtil.getPositiveInt();

    java.lang.String outputDirStr = (((ch.qos.logback.core.AmplFileAppenderResilience_AS_ROOT_Test.MOUNT_POINT) + "resilience-") + (diff)) + "/";

    java.lang.String logfileStr = (outputDirStr) + "output.log";

    ch.qos.logback.core.FileAppender<java.lang.Object> fa = new ch.qos.logback.core.FileAppender<java.lang.Object>();

    static int TOTAL_DURATION = 5000;

    static int NUM_STEPS = 500;

    static int DELAY = (ch.qos.logback.core.AmplFileAppenderResilience_AS_ROOT_Test.TOTAL_DURATION) / (ch.qos.logback.core.AmplFileAppenderResilience_AS_ROOT_Test.NUM_STEPS);

    static boolean isConformingHost() {
        return ch.qos.logback.core.testUtil.EnvUtilForTests.isLocalHostNameInList(new java.lang.String[]{ "haro" });
    }

    @org.junit.Before
    public void setUp() throws java.io.IOException, java.lang.InterruptedException {
        if (!(ch.qos.logback.core.AmplFileAppenderResilience_AS_ROOT_Test.isConformingHost())) {
            return ;
        }
        java.lang.Process p = runLoopFSScript(ch.qos.logback.core.AmplFileAppenderResilience_AS_ROOT_Test.LoopFSCommand.setup);
        p.waitFor();
        dump("/tmp/loopfs.log");
        fa.setContext(context);
        java.io.File outputDir = new java.io.File(outputDirStr);
        outputDir.mkdirs();
        java.lang.System.out.println((("FileAppenderResilienceTest output dir [" + (outputDirStr)) + "]"));
        fa.setName("FILE");
        fa.setEncoder(new ch.qos.logback.core.encoder.EchoEncoder<java.lang.Object>());
        fa.setFile(logfileStr);
        fa.start();
    }

    void dump(java.lang.String file) throws java.io.IOException {
        java.io.FileInputStream fis = null;
        try {
            fis = new java.io.FileInputStream(file);
            int r;
            while ((r = fis.read()) != (-1)) {
                char c = ((char) (r));
                java.lang.System.out.print(c);
            } 
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
    }

    @org.junit.After
    public void tearDown() throws java.io.IOException, java.lang.InterruptedException {
        if (!(ch.qos.logback.core.AmplFileAppenderResilience_AS_ROOT_Test.isConformingHost())) {
            return ;
        }
        ch.qos.logback.core.util.StatusPrinter.print(context);
        fa.stop();
        java.lang.Process p = runLoopFSScript(ch.qos.logback.core.AmplFileAppenderResilience_AS_ROOT_Test.LoopFSCommand.teardown);
        p.waitFor();
        java.lang.System.out.println("Tearing down");
    }

    @org.junit.Test
    public void go() throws java.io.IOException, java.lang.InterruptedException {
        if (!(ch.qos.logback.core.AmplFileAppenderResilience_AS_ROOT_Test.isConformingHost())) {
            return ;
        }
        java.lang.Process p = runLoopFSScript(ch.qos.logback.core.AmplFileAppenderResilience_AS_ROOT_Test.LoopFSCommand.shake);
        for (int i = 0; i < (ch.qos.logback.core.AmplFileAppenderResilience_AS_ROOT_Test.NUM_STEPS); i++) {
            fa.append(((java.lang.String.valueOf(i)) + (ch.qos.logback.core.AmplFileAppenderResilience_AS_ROOT_Test.LONG_STR)));
            java.lang.Thread.sleep(ch.qos.logback.core.AmplFileAppenderResilience_AS_ROOT_Test.DELAY);
        }
        p.waitFor();
        // the extrernal script has the file system ready for IO 50% of the time
        double bestCase = 0.5;
        ch.qos.logback.core.util.ResilienceUtil.verify(logfileStr, "^(\\d{1,3}) x*$", ch.qos.logback.core.AmplFileAppenderResilience_AS_ROOT_Test.NUM_STEPS, (bestCase * 0.6));
        java.lang.System.out.println("Done go");
    }

    // the loopfs script is tightly coupled with the host machine
    // it needs to be Unix, with sudo privileges granted to the script
    java.lang.Process runLoopFSScript(ch.qos.logback.core.AmplFileAppenderResilience_AS_ROOT_Test.LoopFSCommand cmd) throws java.io.IOException, java.lang.InterruptedException {
        // causing a NullPointerException is better than locking the whole
        // machine which the next operation can and will do.
        if (!(ch.qos.logback.core.AmplFileAppenderResilience_AS_ROOT_Test.isConformingHost())) {
            return null;
        }
        java.lang.ProcessBuilder pb = new java.lang.ProcessBuilder();
        pb.command("/usr/bin/sudo", ch.qos.logback.core.AmplFileAppenderResilience_AS_ROOT_Test.PATH_LOOPFS_SCRIPT, cmd.toString());
        return pb.start();
    }
}

