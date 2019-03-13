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
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;


public class TestApplicationClassLoader {
    private static File testDir = GenericTestUtils.getTestDir("appclassloader");

    @Test
    public void testConstructUrlsFromClasspath() throws Exception {
        File file = new File(TestApplicationClassLoader.testDir, "file");
        Assert.assertTrue("Create file", file.createNewFile());
        File dir = new File(TestApplicationClassLoader.testDir, "dir");
        Assert.assertTrue("Make dir", dir.mkdir());
        File jarsDir = new File(TestApplicationClassLoader.testDir, "jarsdir");
        Assert.assertTrue("Make jarsDir", jarsDir.mkdir());
        File nonJarFile = new File(jarsDir, "nonjar");
        Assert.assertTrue("Create non-jar file", nonJarFile.createNewFile());
        File jarFile = new File(jarsDir, "a.jar");
        Assert.assertTrue("Create jar file", jarFile.createNewFile());
        File nofile = new File(TestApplicationClassLoader.testDir, "nofile");
        // don't create nofile
        StringBuilder cp = new StringBuilder();
        cp.append(file.getAbsolutePath()).append(File.pathSeparator).append(dir.getAbsolutePath()).append(File.pathSeparator).append(((jarsDir.getAbsolutePath()) + "/*")).append(File.pathSeparator).append(nofile.getAbsolutePath()).append(File.pathSeparator).append(((nofile.getAbsolutePath()) + "/*")).append(File.pathSeparator);
        URL[] urls = ApplicationClassLoader.constructUrlsFromClasspath(cp.toString());
        Assert.assertEquals(3, urls.length);
        Assert.assertEquals(file.toURI().toURL(), urls[0]);
        Assert.assertEquals(dir.toURI().toURL(), urls[1]);
        Assert.assertEquals(jarFile.toURI().toURL(), urls[2]);
        // nofile should be ignored
    }

    @Test
    public void testIsSystemClass() {
        testIsSystemClassInternal("");
    }

    @Test
    public void testIsSystemNestedClass() {
        testIsSystemClassInternal("$Klass");
    }

    @Test
    public void testGetResource() throws IOException {
        URL testJar = makeTestJar().toURI().toURL();
        ClassLoader currentClassLoader = getClass().getClassLoader();
        ClassLoader appClassloader = new ApplicationClassLoader(new URL[]{ testJar }, currentClassLoader, null);
        Assert.assertNull("Resource should be null for current classloader", currentClassLoader.getResourceAsStream("resource.txt"));
        InputStream in = appClassloader.getResourceAsStream("resource.txt");
        Assert.assertNotNull("Resource should not be null for app classloader", in);
        Assert.assertEquals("hello", IOUtils.toString(in));
    }
}

