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


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.regex.Pattern;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocalFileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static ApplicationClassLoader.SYSTEM_CLASSES_DEFAULT;


public class TestRunJar {
    private static final String FOOBAR_TXT = "foobar.txt";

    private static final String FOOBAZ_TXT = "foobaz.txt";

    private static final int BUFF_SIZE = 2048;

    private File TEST_ROOT_DIR;

    private static final String TEST_JAR_NAME = "test-runjar.jar";

    private static final String TEST_JAR_2_NAME = "test-runjar2.jar";

    private static final long MOCKED_NOW = 1460389972000L;

    private static final long MOCKED_NOW_PLUS_TWO_SEC = (TestRunJar.MOCKED_NOW) + 2000;

    /**
     * Test default unjarring behavior - unpack everything
     */
    @Test
    public void testUnJar() throws Exception {
        File unjarDir = getUnjarDir("unjar-all");
        // Unjar everything
        RunJar.unJar(new File(TEST_ROOT_DIR, TestRunJar.TEST_JAR_NAME), unjarDir, RunJar.MATCH_ANY);
        Assert.assertTrue("foobar unpacked", new File(unjarDir, TestRunJar.FOOBAR_TXT).exists());
        Assert.assertTrue("foobaz unpacked", new File(unjarDir, TestRunJar.FOOBAZ_TXT).exists());
    }

    /**
     * Test unjarring a specific regex
     */
    @Test
    public void testUnJarWithPattern() throws Exception {
        File unjarDir = getUnjarDir("unjar-pattern");
        // Unjar only a regex
        RunJar.unJar(new File(TEST_ROOT_DIR, TestRunJar.TEST_JAR_NAME), unjarDir, Pattern.compile(".*baz.*"));
        Assert.assertFalse("foobar not unpacked", new File(unjarDir, TestRunJar.FOOBAR_TXT).exists());
        Assert.assertTrue("foobaz unpacked", new File(unjarDir, TestRunJar.FOOBAZ_TXT).exists());
    }

    /**
     * Test unjarring a big file. This checks appending the remainder of the file
     * to the tee output stream in RunJar.unJarAndSave.
     */
    @SuppressWarnings("deprecation")
    @Test
    public void testBigJar() throws Exception {
        Random r = new Random(System.currentTimeMillis());
        File dir = new File(TEST_ROOT_DIR, Long.toHexString(r.nextLong()));
        Assert.assertTrue(dir.mkdirs());
        File input = generateBigJar(dir);
        File output = new File(dir, "job2.jar");
        try {
            try (InputStream is = new FileInputStream(input)) {
                RunJar.unJarAndSave(is, dir, "job2.jar", Pattern.compile(".*"));
            }
            Assert.assertEquals(input.length(), output.length());
            for (int i = 0; i < 10; ++i) {
                File subdir = new File(dir, ((i % 2) == 0 ? "dir/" : ""));
                File f = new File(subdir, ("f" + (Integer.toString(i))));
                Assert.assertEquals(756, f.length());
            }
        } finally {
            // Clean up
            FileSystem fs = LocalFileSystem.getLocal(new Configuration());
            fs.delete(new Path(dir.getAbsolutePath()), true);
        }
    }

    @Test
    public void testUnJarDoesNotLooseLastModify() throws Exception {
        File unjarDir = getUnjarDir("unjar-lastmod");
        // Unjar everything
        RunJar.unJar(new File(TEST_ROOT_DIR, TestRunJar.TEST_JAR_NAME), unjarDir, RunJar.MATCH_ANY);
        String failureMessage = "Last modify time was lost during unJar";
        Assert.assertEquals(failureMessage, TestRunJar.MOCKED_NOW, new File(unjarDir, TestRunJar.FOOBAR_TXT).lastModified());
        Assert.assertEquals(failureMessage, TestRunJar.MOCKED_NOW_PLUS_TWO_SEC, new File(unjarDir, TestRunJar.FOOBAZ_TXT).lastModified());
    }

    /**
     * Tests the client classloader to verify the main class and its dependent
     * class are loaded correctly by the application classloader, and others are
     * loaded by the system classloader.
     */
    @Test
    public void testClientClassLoader() throws Throwable {
        RunJar runJar = Mockito.spy(new RunJar());
        // enable the client classloader
        Mockito.when(runJar.useClientClassLoader()).thenReturn(true);
        // set the system classes and blacklist the test main class and the test
        // third class so they can be loaded by the application classloader
        String mainCls = ClassLoaderCheckMain.class.getName();
        String thirdCls = ClassLoaderCheckThird.class.getName();
        String systemClasses = ((((("-" + mainCls) + ",") + "-") + thirdCls) + ",") + (SYSTEM_CLASSES_DEFAULT);
        Mockito.when(runJar.getSystemClasses()).thenReturn(systemClasses);
        // create the test jar
        File testJar = JarFinder.makeClassLoaderTestJar(this.getClass(), TEST_ROOT_DIR, TestRunJar.TEST_JAR_2_NAME, TestRunJar.BUFF_SIZE, mainCls, thirdCls);
        // form the args
        String[] args = new String[3];
        args[0] = testJar.getAbsolutePath();
        args[1] = mainCls;
        // run RunJar
        runJar.run(args);
        // it should not throw an exception
        Mockito.verify(runJar, Mockito.times(1)).unJar(ArgumentMatchers.any(File.class), ArgumentMatchers.any(File.class));
    }

    @Test
    public void testClientClassLoaderSkipUnjar() throws Throwable {
        RunJar runJar = Mockito.spy(new RunJar());
        // enable the client classloader
        Mockito.when(runJar.useClientClassLoader()).thenReturn(true);
        // set the system classes and blacklist the test main class and the test
        // third class so they can be loaded by the application classloader
        String mainCls = ClassLoaderCheckMain.class.getName();
        String thirdCls = ClassLoaderCheckThird.class.getName();
        String systemClasses = ((((("-" + mainCls) + ",") + "-") + thirdCls) + ",") + (SYSTEM_CLASSES_DEFAULT);
        Mockito.when(runJar.getSystemClasses()).thenReturn(systemClasses);
        // create the test jar
        File testJar = JarFinder.makeClassLoaderTestJar(this.getClass(), TEST_ROOT_DIR, TestRunJar.TEST_JAR_2_NAME, TestRunJar.BUFF_SIZE, mainCls, thirdCls);
        // form the args
        String[] args = new String[3];
        args[0] = testJar.getAbsolutePath();
        args[1] = mainCls;
        Mockito.when(runJar.skipUnjar()).thenReturn(true);
        // run RunJar
        runJar.run(args);
        // it should not throw an exception
        Mockito.verify(runJar, Mockito.times(0)).unJar(ArgumentMatchers.any(File.class), ArgumentMatchers.any(File.class));
    }

    @Test
    public void testUnJar2() throws IOException {
        // make a simple zip
        File jarFile = new File(TEST_ROOT_DIR, TestRunJar.TEST_JAR_NAME);
        JarOutputStream jstream = new JarOutputStream(new FileOutputStream(jarFile));
        JarEntry je = new JarEntry("META-INF/MANIFEST.MF");
        byte[] data = "Manifest-Version: 1.0\nCreated-By: 1.8.0_1 (Manual)".getBytes(StandardCharsets.UTF_8);
        je.setSize(data.length);
        jstream.putNextEntry(je);
        jstream.write(data);
        jstream.closeEntry();
        je = new JarEntry("../outside.path");
        data = "any data here".getBytes(StandardCharsets.UTF_8);
        je.setSize(data.length);
        jstream.putNextEntry(je);
        jstream.write(data);
        jstream.closeEntry();
        jstream.close();
        File unjarDir = getUnjarDir("unjar-path");
        // Unjar everything
        try {
            RunJar.unJar(jarFile, unjarDir, RunJar.MATCH_ANY);
            Assert.fail("unJar should throw IOException.");
        } catch (IOException e) {
            GenericTestUtils.assertExceptionContains("would create file outside of", e);
        }
        try {
            RunJar.unJar(new FileInputStream(jarFile), unjarDir, RunJar.MATCH_ANY);
            Assert.fail("unJar should throw IOException.");
        } catch (IOException e) {
            GenericTestUtils.assertExceptionContains("would create file outside of", e);
        }
    }
}

