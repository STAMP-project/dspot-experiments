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


import ch.qos.logback.core.Context;
import ch.qos.logback.core.ContextBase;
import ch.qos.logback.core.encoder.Encoder;
import ch.qos.logback.core.rolling.helper.RenameUtil;
import ch.qos.logback.core.testUtil.CoreTestConstants;
import ch.qos.logback.core.testUtil.RandomUtil;
import ch.qos.logback.core.testUtil.StatusChecker;
import ch.qos.logback.core.util.StatusPrinter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class RenameUtilTest {
    Encoder<Object> encoder;

    Context context = new ContextBase();

    StatusChecker statusChecker = new StatusChecker(context);

    long currentTime = System.currentTimeMillis();

    int diff = RandomUtil.getPositiveInt();

    protected String randomOutputDirAsStr = ((CoreTestConstants.OUTPUT_DIR_PREFIX) + (diff)) + "/";

    protected File randomOutputDir = new File(randomOutputDirAsStr);

    @Test
    public void renameToNonExistingDirectory() throws RolloverFailure, IOException {
        RenameUtil renameUtil = new RenameUtil();
        renameUtil.setContext(context);
        int diff2 = RandomUtil.getPositiveInt();
        File fromFile = File.createTempFile(("from" + (diff)), "test", randomOutputDir);
        String randomTARGETDir = (CoreTestConstants.OUTPUT_DIR_PREFIX) + diff2;
        renameUtil.rename(fromFile.toString(), new File((randomTARGETDir + "/to.test")).toString());
        StatusPrinter.printInCaseOfErrorsOrWarnings(context);
        Assert.assertTrue(isErrorFree(0));
    }

    // LOGBACK-1054
    @Test
    public void renameLockedAbstractFile_LOGBACK_1054() throws RolloverFailure, IOException {
        RenameUtil renameUtil = new RenameUtil();
        renameUtil.setContext(context);
        String abstractFileName = "abstract_pathname-" + (diff);
        String src = (CoreTestConstants.OUTPUT_DIR_PREFIX) + abstractFileName;
        String target = abstractFileName + ".target";
        makeFile(src);
        FileInputStream fisLock = new FileInputStream(src);
        renameUtil.rename(src, target);
        // release the lock
        fisLock.close();
        StatusPrinter.print(context);
        Assert.assertEquals(0, matchCount((("Parent of target file ." + target) + ". is null")));
    }
}

