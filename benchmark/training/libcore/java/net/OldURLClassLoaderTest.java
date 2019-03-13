/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package libcore.java.net;


import java.io.File;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.util.Enumeration;
import java.util.jar.Manifest;
import junit.framework.TestCase;


public class OldURLClassLoaderTest extends TestCase {
    URLClassLoader ucl;

    SecurityManager sm = new SecurityManager() {
        public void checkPermission(Permission perm) {
        }

        public void checkCreateClassLoader() {
            throw new SecurityException();
        }
    };

    /**
     * java.net.URLClassLoader#URLClassLoader(java.net.URL[])
     */
    public void test_Constructor$Ljava_net_URL() throws MalformedURLException {
        URL[] u = new URL[0];
        ucl = new URLClassLoader(u);
        TestCase.assertTrue("Failed to set parent", (((ucl) != null) && ((ucl.getParent()) == (URLClassLoader.getSystemClassLoader()))));
        URL[] urls = new URL[]{ new URL("http://foo.com/foo"), new URL("jar:file://foo.jar!/foo.c"), new URL("ftp://foo1/foo2/foo.c") };
        URLClassLoader ucl1 = new URLClassLoader(urls);
        TestCase.assertTrue(((urls.length) == (ucl1.getURLs().length)));
        try {
            Class.forName("test", false, ucl);
            TestCase.fail("Should throw ClassNotFoundException");
        } catch (ClassNotFoundException e) {
            // expected
        }
        try {
            new URLClassLoader(new URL[]{ null });
        } catch (Exception e) {
            TestCase.fail(("Unexpected exception was thrown: " + (e.getMessage())));
        }
    }

    /**
     * java.net.URLClassLoader#findResources(java.lang.String)
     */
    public void test_findResourcesLjava_lang_String() throws Exception {
        Enumeration<URL> res = null;
        String[] resValues = new String[]{ "This is a test resource file.", "This is a resource from a subdir" };
        String tmp = (System.getProperty("java.io.tmpdir")) + "/";
        File tmpDir = new File(tmp);
        File test1 = new File((tmp + "test0"));
        test1.deleteOnExit();
        FileOutputStream out = new FileOutputStream(test1);
        out.write(resValues[0].getBytes());
        out.flush();
        out.close();
        File subDir = new File((tmp + "subdir/"));
        subDir.mkdir();
        File test2 = new File((tmp + "subdir/test0"));
        test2.deleteOnExit();
        out = new FileOutputStream(test2);
        out.write(resValues[1].getBytes());
        out.flush();
        out.close();
        URL[] urls = new URL[2];
        urls[0] = new URL((("file://" + (tmpDir.getAbsolutePath())) + "/"));
        urls[1] = new URL((("file://" + (subDir.getAbsolutePath())) + "/"));
        ucl = new URLClassLoader(urls);
        res = ucl.findResources("test0");
        TestCase.assertNotNull("Failed to locate resources", res);
        int i = 0;
        while (res.hasMoreElements()) {
            StringBuffer sb = getResContent(res.nextElement());
            TestCase.assertEquals("Returned incorrect resource/or in wrong order", resValues[(i++)], sb.toString());
        } 
        TestCase.assertEquals("Incorrect number of resources returned", 2, i);
    }

    public void test_addURLLjava_net_URL() throws MalformedURLException {
        URL[] u = new URL[0];
        URL[] urls = new URL[]{ new URL("http://foo.com/foo"), new URL("jar:file://foo.jar!/foo.c"), new URL("ftp://foo1/foo2/foo.c"), null };
        OldURLClassLoaderTest.TestURLClassLoader tucl = new OldURLClassLoaderTest.TestURLClassLoader(u);
        for (int i = 0; i < (urls.length);) {
            tucl.addURL(urls[i]);
            i++;
            URL[] result = tucl.getURLs();
            TestCase.assertEquals(("Result array length is incorrect: " + i), i, result.length);
            for (int j = 0; j < (result.length); j++) {
                TestCase.assertEquals(("Result array item is incorrect: " + j), urls[j], result[j]);
            }
        }
    }

    public void test_definePackage() throws MalformedURLException {
        Manifest manifest = new Manifest();
        URL[] u = new URL[0];
        OldURLClassLoaderTest.TestURLClassLoader tucl = new OldURLClassLoaderTest.TestURLClassLoader(u);
        URL[] urls = new URL[]{ new URL("http://foo.com/foo"), new URL("jar:file://foo.jar!/foo.c"), new URL("ftp://foo1/foo2/foo.c"), new URL("file://new/package/name/"), null };
        String packageName = "new.package.name";
        for (int i = 0; i < (urls.length); i++) {
            Package pack = tucl.definePackage((packageName + i), manifest, urls[i]);
            TestCase.assertEquals((packageName + i), pack.getName());
            TestCase.assertNull("Implementation Title is not null", pack.getImplementationTitle());
            TestCase.assertNull("Implementation Vendor is not null", pack.getImplementationVendor());
            TestCase.assertNull("Implementation Version is not null.", pack.getImplementationVersion());
        }
        try {
            tucl.definePackage((packageName + "0"), manifest, null);
            TestCase.fail("IllegalArgumentException was not thrown.");
        } catch (IllegalArgumentException iae) {
            // expected
        }
    }

    class TestURLClassLoader extends URLClassLoader {
        public TestURLClassLoader(URL[] urls) {
            super(urls);
        }

        public void addURL(URL url) {
            super.addURL(url);
        }

        public Package definePackage(String name, Manifest man, URL url) throws IllegalArgumentException {
            return super.definePackage(name, man, url);
        }

        public Class<?> findClass(String name) throws ClassNotFoundException {
            return super.findClass(name);
        }

        protected PermissionCollection getPermissions(CodeSource codesource) {
            return super.getPermissions(codesource);
        }
    }
}

