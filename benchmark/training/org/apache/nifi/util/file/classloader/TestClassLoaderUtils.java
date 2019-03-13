/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.util.file.classloader;


import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public class TestClassLoaderUtils {
    @Test
    public void testGetCustomClassLoader() throws ClassNotFoundException, MalformedURLException {
        final String jarFilePath = "src/test/resources/TestClassLoaderUtils";
        ClassLoader customClassLoader = ClassLoaderUtils.getCustomClassLoader(jarFilePath, this.getClass().getClassLoader(), getJarFilenameFilter());
        Assert.assertTrue((customClassLoader != null));
        Assert.assertTrue(((customClassLoader.loadClass("TestSuccess")) != null));
    }

    @Test
    public void testGetCustomClassLoaderNoPathSpecified() throws MalformedURLException {
        final ClassLoader originalClassLoader = this.getClass().getClassLoader();
        ClassLoader customClassLoader = ClassLoaderUtils.getCustomClassLoader(null, originalClassLoader, getJarFilenameFilter());
        Assert.assertTrue((customClassLoader != null));
        try {
            customClassLoader.loadClass("TestSuccess");
        } catch (ClassNotFoundException cex) {
            Assert.assertTrue(cex.getLocalizedMessage().equals("TestSuccess"));
            return;
        }
        Assert.fail("exception did not occur, class should not be found");
    }

    @Test
    public void testGetCustomClassLoaderWithInvalidPath() {
        final String jarFilePath = "src/test/resources/FakeTestClassLoaderUtils/TestSuccess.jar";
        try {
            ClassLoaderUtils.getCustomClassLoader(jarFilePath, this.getClass().getClassLoader(), getJarFilenameFilter());
        } catch (MalformedURLException mex) {
            Assert.assertTrue(mex.getLocalizedMessage().equals("Path specified does not exist"));
            return;
        }
        Assert.fail("exception did not occur, path should not exist");
    }

    @Test
    public void testGetCustomClassLoaderWithMultipleLocations() throws Exception {
        final String jarFilePath = " src/test/resources/TestClassLoaderUtils/TestSuccess.jar, http://nifi.apache.org/Test.jar";
        Assert.assertNotNull(ClassLoaderUtils.getCustomClassLoader(jarFilePath, this.getClass().getClassLoader(), getJarFilenameFilter()));
    }

    @Test
    public void testGetCustomClassLoaderWithEmptyLocations() throws Exception {
        String jarFilePath = "";
        Assert.assertNotNull(ClassLoaderUtils.getCustomClassLoader(jarFilePath, this.getClass().getClassLoader(), getJarFilenameFilter()));
        jarFilePath = ",";
        Assert.assertNotNull(ClassLoaderUtils.getCustomClassLoader(jarFilePath, this.getClass().getClassLoader(), getJarFilenameFilter()));
        jarFilePath = ",src/test/resources/TestClassLoaderUtils/TestSuccess.jar, ";
        Assert.assertNotNull(ClassLoaderUtils.getCustomClassLoader(jarFilePath, this.getClass().getClassLoader(), getJarFilenameFilter()));
    }

    @Test
    public void testGetURLsForClasspathWithDirectory() throws MalformedURLException {
        final String jarFilePath = "src/test/resources/TestClassLoaderUtils";
        URL[] urls = ClassLoaderUtils.getURLsForClasspath(jarFilePath, getJarFilenameFilter(), false);
        Assert.assertEquals(2, urls.length);
    }

    @Test
    public void testGetURLsForClasspathWithSingleJAR() throws MalformedURLException {
        final String jarFilePath = "src/test/resources/TestClassLoaderUtils/TestSuccess.jar";
        URL[] urls = ClassLoaderUtils.getURLsForClasspath(jarFilePath, null, false);
        Assert.assertEquals(1, urls.length);
    }

    @Test(expected = MalformedURLException.class)
    public void testGetURLsForClasspathWithSomeNonExistentAndNoSuppression() throws MalformedURLException {
        final String jarFilePath = "src/test/resources/TestClassLoaderUtils/TestSuccess.jar,src/test/resources/TestClassLoaderUtils/FakeTest.jar";
        ClassLoaderUtils.getURLsForClasspath(jarFilePath, null, false);
    }

    @Test
    public void testGetURLsForClasspathWithSomeNonExistentAndSuppression() throws MalformedURLException {
        final String jarFilePath = "src/test/resources/TestClassLoaderUtils/TestSuccess.jar,src/test/resources/TestClassLoaderUtils/FakeTest.jar";
        URL[] urls = ClassLoaderUtils.getURLsForClasspath(jarFilePath, null, true);
        Assert.assertEquals(1, urls.length);
    }

    @Test
    public void testGetURLsForClasspathWithSetAndSomeNonExistentAndSuppression() throws MalformedURLException {
        final Set<String> modules = new HashSet<>();
        modules.add("src/test/resources/TestClassLoaderUtils/TestSuccess.jar,src/test/resources/TestClassLoaderUtils/FakeTest1.jar");
        modules.add("src/test/resources/TestClassLoaderUtils/FakeTest2.jar,src/test/resources/TestClassLoaderUtils/FakeTest3.jar");
        URL[] urls = ClassLoaderUtils.getURLsForClasspath(modules, null, true);
        Assert.assertEquals(1, urls.length);
    }

    @Test
    public void testGenerateAdditionalUrlsFingerprint() throws MalformedURLException, URISyntaxException {
        final Set<URL> urls = new HashSet<>();
        URL testUrl = Paths.get("src/test/resources/TestClassLoaderUtils/TestSuccess.jar").toUri().toURL();
        urls.add(testUrl);
        String testFingerprint = ClassLoaderUtils.generateAdditionalUrlsFingerprint(urls);
        Assert.assertNotNull(testFingerprint);
    }
}

