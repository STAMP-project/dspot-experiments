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
package ch.qos.logback.core.rolling.helper;


import ch.qos.logback.core.Context;
import ch.qos.logback.core.ContextBase;
import ch.qos.logback.core.testUtil.CoreTestConstants;
import ch.qos.logback.core.testUtil.StatusChecker;
import ch.qos.logback.core.util.Compare;
import org.junit.Assert;
import org.junit.Test;

import static CompressionMode.GZ;
import static CompressionMode.ZIP;


/**
 *
 *
 * @author Ceki Gulcu
 */
public class CompressTest {
    Context context = new ContextBase();

    @Test
    public void test1() throws Exception {
        Compressor compressor = new Compressor(GZ);
        compressor.setContext(context);
        compressor.compress(((CoreTestConstants.TEST_SRC_PREFIX) + "input/compress1.txt"), ((CoreTestConstants.OUTPUT_DIR_PREFIX) + "compress1.txt.gz"), null);
        StatusChecker checker = new StatusChecker(context);
        Assert.assertTrue(isErrorFree(0));
        Assert.assertTrue(Compare.gzCompare(((CoreTestConstants.OUTPUT_DIR_PREFIX) + "compress1.txt.gz"), ((CoreTestConstants.TEST_SRC_PREFIX) + "witness/compress1.txt.gz")));
    }

    @Test
    public void test2() throws Exception {
        Compressor compressor = new Compressor(GZ);
        compressor.setContext(context);
        compressor.compress(((CoreTestConstants.TEST_SRC_PREFIX) + "input/compress2.txt"), ((CoreTestConstants.OUTPUT_DIR_PREFIX) + "compress2.txt"), null);
        StatusChecker checker = new StatusChecker(context);
        Assert.assertTrue(isErrorFree(0));
        Assert.assertTrue(Compare.gzCompare(((CoreTestConstants.OUTPUT_DIR_PREFIX) + "compress2.txt.gz"), ((CoreTestConstants.TEST_SRC_PREFIX) + "witness/compress2.txt.gz")));
    }

    @Test
    public void test3() throws Exception {
        Compressor compressor = new Compressor(ZIP);
        compressor.setContext(context);
        compressor.compress(((CoreTestConstants.TEST_SRC_PREFIX) + "input/compress3.txt"), ((CoreTestConstants.OUTPUT_DIR_PREFIX) + "compress3.txt"), "compress3.txt");
        StatusChecker checker = new StatusChecker(context);
        Assert.assertTrue(isErrorFree(0));
        // we don't know how to compare .zip files
        // assertTrue(Compare.compare(CoreTestConstants.OUTPUT_DIR_PREFIX
        // + "compress3.txt.zip", CoreTestConstants.TEST_SRC_PREFIX
        // + "witness/compress3.txt.zip"));
    }
}

