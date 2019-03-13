/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.util;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests covering the classpath command-line utility.
 */
public class TestClasspath {
    private static final Logger LOG = LoggerFactory.getLogger(TestClasspath.class);

    private static final File TEST_DIR = GenericTestUtils.getTestDir("TestClasspath");

    private static final Charset UTF8 = Charset.forName("UTF-8");

    static {
        ExitUtil.disableSystemExit();
    }

    private PrintStream oldStdout;

    private PrintStream oldStderr;

    private ByteArrayOutputStream stdout;

    private ByteArrayOutputStream stderr;

    private PrintStream printStdout;

    private PrintStream printStderr;

    @Test
    public void testGlob() {
        Classpath.main(new String[]{ "--glob" });
        String strOut = new String(stdout.toByteArray(), TestClasspath.UTF8);
        Assert.assertEquals(System.getProperty("java.class.path"), strOut.trim());
        Assert.assertTrue(((stderr.toByteArray().length) == 0));
    }

    @Test
    public void testJar() throws IOException {
        File file = new File(TestClasspath.TEST_DIR, "classpath.jar");
        Classpath.main(new String[]{ "--jar", file.getAbsolutePath() });
        Assert.assertTrue(((stdout.toByteArray().length) == 0));
        Assert.assertTrue(((stderr.toByteArray().length) == 0));
        Assert.assertTrue(file.exists());
        TestClasspath.assertJar(file);
    }

    @Test
    public void testJarReplace() throws IOException {
        // Run the command twice with the same output jar file, and expect success.
        testJar();
        testJar();
    }

    @Test
    public void testJarFileMissing() throws IOException {
        try {
            Classpath.main(new String[]{ "--jar" });
            Assert.fail("expected exit");
        } catch (ExitUtil e) {
            Assert.assertTrue(((stdout.toByteArray().length) == 0));
            String strErr = new String(stderr.toByteArray(), TestClasspath.UTF8);
            Assert.assertTrue(strErr.contains("requires path of jar"));
        }
    }

    @Test
    public void testHelp() {
        Classpath.main(new String[]{ "--help" });
        String strOut = new String(stdout.toByteArray(), TestClasspath.UTF8);
        Assert.assertTrue(strOut.contains("Prints the classpath"));
        Assert.assertTrue(((stderr.toByteArray().length) == 0));
    }

    @Test
    public void testHelpShort() {
        Classpath.main(new String[]{ "-h" });
        String strOut = new String(stdout.toByteArray(), TestClasspath.UTF8);
        Assert.assertTrue(strOut.contains("Prints the classpath"));
        Assert.assertTrue(((stderr.toByteArray().length) == 0));
    }

    @Test
    public void testUnrecognized() {
        try {
            Classpath.main(new String[]{ "--notarealoption" });
            Assert.fail("expected exit");
        } catch (ExitUtil e) {
            Assert.assertTrue(((stdout.toByteArray().length) == 0));
            String strErr = new String(stderr.toByteArray(), TestClasspath.UTF8);
            Assert.assertTrue(strErr.contains("unrecognized option"));
        }
    }
}

