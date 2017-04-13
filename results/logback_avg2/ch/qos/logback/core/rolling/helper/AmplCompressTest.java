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


/**
 * @author Ceki Gulcu
 */
public class AmplCompressTest {
    ch.qos.logback.core.Context context = new ch.qos.logback.core.ContextBase();

    @org.junit.Before
    public void setUp() throws java.io.IOException {
        // Copy source files
        // Delete output files
        {
            java.io.File source = new java.io.File(((ch.qos.logback.core.util.CoreTestConstants.TEST_SRC_PREFIX) + "input/compress1.copy"));
            java.io.File dest = new java.io.File(((ch.qos.logback.core.util.CoreTestConstants.TEST_SRC_PREFIX) + "input/compress1.txt"));
            copy(source, dest);
            java.io.File target = new java.io.File(((ch.qos.logback.core.util.CoreTestConstants.OUTPUT_DIR_PREFIX) + "compress1.txt.gz"));
            target.mkdirs();
            target.delete();
        }
        {
            java.io.File source = new java.io.File(((ch.qos.logback.core.util.CoreTestConstants.TEST_SRC_PREFIX) + "input/compress2.copy"));
            java.io.File dest = new java.io.File(((ch.qos.logback.core.util.CoreTestConstants.TEST_SRC_PREFIX) + "input/compress2.txt"));
            copy(source, dest);
            java.io.File target = new java.io.File(((ch.qos.logback.core.util.CoreTestConstants.OUTPUT_DIR_PREFIX) + "compress2.txt.gz"));
            target.mkdirs();
            target.delete();
        }
        {
            java.io.File source = new java.io.File(((ch.qos.logback.core.util.CoreTestConstants.TEST_SRC_PREFIX) + "input/compress3.copy"));
            java.io.File dest = new java.io.File(((ch.qos.logback.core.util.CoreTestConstants.TEST_SRC_PREFIX) + "input/compress3.txt"));
            copy(source, dest);
            java.io.File target = new java.io.File(((ch.qos.logback.core.util.CoreTestConstants.OUTPUT_DIR_PREFIX) + "compress3.txt.zip"));
            target.mkdirs();
            target.delete();
        }
    }

    @org.junit.Test
    public void test1() throws java.lang.Exception {
        ch.qos.logback.core.rolling.helper.Compressor compressor = new ch.qos.logback.core.rolling.helper.Compressor(ch.qos.logback.core.rolling.helper.CompressionMode.GZ);
        compressor.setContext(context);
        compressor.compress(((ch.qos.logback.core.util.CoreTestConstants.TEST_SRC_PREFIX) + "input/compress1.txt"), ((ch.qos.logback.core.util.CoreTestConstants.OUTPUT_DIR_PREFIX) + "compress1.txt.gz"), null);
        ch.qos.logback.core.status.StatusChecker checker = new ch.qos.logback.core.status.StatusChecker(context);
        org.junit.Assert.assertTrue(checker.isErrorFree(0));
        org.junit.Assert.assertTrue(ch.qos.logback.core.util.Compare.gzCompare(((ch.qos.logback.core.util.CoreTestConstants.OUTPUT_DIR_PREFIX) + "compress1.txt.gz"), ((ch.qos.logback.core.util.CoreTestConstants.TEST_SRC_PREFIX) + "witness/compress1.txt.gz")));
    }

    @org.junit.Test
    public void test2() throws java.lang.Exception {
        ch.qos.logback.core.rolling.helper.Compressor compressor = new ch.qos.logback.core.rolling.helper.Compressor(ch.qos.logback.core.rolling.helper.CompressionMode.GZ);
        compressor.setContext(context);
        compressor.compress(((ch.qos.logback.core.util.CoreTestConstants.TEST_SRC_PREFIX) + "input/compress2.txt"), ((ch.qos.logback.core.util.CoreTestConstants.OUTPUT_DIR_PREFIX) + "compress2.txt"), null);
        ch.qos.logback.core.status.StatusChecker checker = new ch.qos.logback.core.status.StatusChecker(context);
        org.junit.Assert.assertTrue(checker.isErrorFree(0));
        org.junit.Assert.assertTrue(ch.qos.logback.core.util.Compare.gzCompare(((ch.qos.logback.core.util.CoreTestConstants.OUTPUT_DIR_PREFIX) + "compress2.txt.gz"), ((ch.qos.logback.core.util.CoreTestConstants.TEST_SRC_PREFIX) + "witness/compress2.txt.gz")));
    }

    @org.junit.Test
    public void test3() throws java.lang.Exception {
        ch.qos.logback.core.rolling.helper.Compressor compressor = new ch.qos.logback.core.rolling.helper.Compressor(ch.qos.logback.core.rolling.helper.CompressionMode.ZIP);
        compressor.setContext(context);
        compressor.compress(((ch.qos.logback.core.util.CoreTestConstants.TEST_SRC_PREFIX) + "input/compress3.txt"), ((ch.qos.logback.core.util.CoreTestConstants.OUTPUT_DIR_PREFIX) + "compress3.txt"), "compress3.txt");
        ch.qos.logback.core.status.StatusChecker checker = new ch.qos.logback.core.status.StatusChecker(context);
        org.junit.Assert.assertTrue(checker.isErrorFree(0));
        // we don't know how to compare .zip files
        // assertTrue(Compare.compare(CoreTestConstants.OUTPUT_DIR_PREFIX
        // + "compress3.txt.zip", CoreTestConstants.TEST_SRC_PREFIX
        // + "witness/compress3.txt.zip"));
    }

    private void copy(java.io.File src, java.io.File dst) throws java.io.IOException {
        java.io.InputStream in = new java.io.FileInputStream(src);
        java.io.OutputStream out = new java.io.FileOutputStream(dst);
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        } 
        in.close();
        out.close();
    }
}

