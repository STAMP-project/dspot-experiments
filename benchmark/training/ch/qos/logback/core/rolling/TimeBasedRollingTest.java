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
package ch.qos.logback.core.rolling;


import ch.qos.logback.core.encoder.EchoEncoder;
import ch.qos.logback.core.rolling.testUtil.ScaffoldingForRollingTests;
import ch.qos.logback.core.testUtil.EnvUtilForTests;
import ch.qos.logback.core.util.StatusPrinter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.junit.Test;


/**
 * A rather exhaustive set of tests. Tests include leaving the file option
 * blank, or setting it, with and without compression, and tests with or without
 * stopping/restarting the RollingFileAppender.
 * <p>
 * The regression tests log a few times using a RollingFileAppender. Then, they
 * predict the names of the files which should be generated and compare them
 * with witness files.
 * <p>
 * <pre>
 *                Compression     file option    Stop/Restart
 *     Test1      NO              BLANK           NO
 *     Test2      YES             BLANK           NO
 *     Test3      NO              BLANK           YES
 *     Test4      NO              SET             YES
 *     Test5      NO              SET             NO
 *     Test6      YES             SET             NO
 * </pre>
 *
 * @author Ceki G&uuml;lc&uuml;
 */
public class TimeBasedRollingTest extends ScaffoldingForRollingTests {
    static final int NO_RESTART = 0;

    static final int WITH_RESTART = 1;

    static final int WITH_RESTART_AND_LONG_WAIT = 2000;

    static final boolean FILE_OPTION_SET = true;

    static final boolean FILE_OPTION_BLANK = false;

    RollingFileAppender<Object> rfa1 = new RollingFileAppender<Object>();

    TimeBasedRollingPolicy<Object> tbrp1 = new TimeBasedRollingPolicy<Object>();

    RollingFileAppender<Object> rfa2 = new RollingFileAppender<Object>();

    TimeBasedRollingPolicy<Object> tbrp2 = new TimeBasedRollingPolicy<Object>();

    EchoEncoder<Object> encoder = new EchoEncoder<Object>();

    RolloverChecker rolloverChecker;

    @Test
    public void noCompression_FileBlank_NoRestart_1() throws IOException {
        defaultTest("test1", "test1", "", TimeBasedRollingTest.FILE_OPTION_BLANK, TimeBasedRollingTest.NO_RESTART);
    }

    @Test
    public void withCompression_FileBlank_NoRestart_2() throws IOException {
        defaultTest("test2", "test2", ".gz", TimeBasedRollingTest.FILE_OPTION_BLANK, TimeBasedRollingTest.NO_RESTART);
    }

    @Test
    public void noCompression_FileBlank_StopRestart_3() throws IOException {
        defaultTest("test3", "test3", "", TimeBasedRollingTest.FILE_OPTION_BLANK, TimeBasedRollingTest.WITH_RESTART);
    }

    @Test
    public void noCompression_FileSet_StopRestart_4() throws IOException {
        defaultTest("test4", "test4", "", TimeBasedRollingTest.FILE_OPTION_SET, TimeBasedRollingTest.WITH_RESTART);
    }

    @Test
    public void noCompression_FileSet_StopRestart_WithLongWait_4B() throws IOException {
        defaultTest("test4B", "test4B", "", TimeBasedRollingTest.FILE_OPTION_SET, TimeBasedRollingTest.WITH_RESTART_AND_LONG_WAIT);
    }

    @Test
    public void noCompression_FileSet_NoRestart_5() throws IOException {
        defaultTest("test5", "test5", "", TimeBasedRollingTest.FILE_OPTION_SET, TimeBasedRollingTest.NO_RESTART);
    }

    @Test
    public void withCompression_FileSet_NoRestart_6() throws IOException {
        defaultTest("test6", "test6", ".gz", TimeBasedRollingTest.FILE_OPTION_SET, TimeBasedRollingTest.NO_RESTART);
    }

    // LOGBACK-168
    @Test
    public void withMissingTargetDirWithCompression() throws IOException {
        defaultTest("test7", "%d{yyyy-MM-dd, aux}/test7", ".gz", TimeBasedRollingTest.FILE_OPTION_SET, TimeBasedRollingTest.NO_RESTART);
    }

    @Test
    public void withMissingTargetDirWithZipCompression() throws IOException {
        defaultTest("test8", "%d{yyyy-MM-dd, aux}/test8", ".zip", TimeBasedRollingTest.FILE_OPTION_SET, TimeBasedRollingTest.NO_RESTART);
    }

    @Test
    public void failed_rename() throws IOException {
        if (!(EnvUtilForTests.isWindows()))
            return;

        FileOutputStream fos = null;
        try {
            String fileName = testId2FileName("failed_rename");
            File file = new File(fileName);
            file.getParentFile().mkdirs();
            fos = new FileOutputStream(fileName);
            String testId = "failed_rename";
            rolloverChecker = new ZRolloverChecker(testId);
            genericTest(testId, "failed_rename", "", TimeBasedRollingTest.FILE_OPTION_SET, TimeBasedRollingTest.NO_RESTART);
        } finally {
            StatusPrinter.print(context);
            if (fos != null)
                fos.close();

        }
    }
}

