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


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.Certificate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import junit.framework.TestCase;
import tests.support.resource.Support_Resources;

import static java.util.jar.Attributes.Name.MANIFEST_VERSION;


public class OldJarURLConnectionTest extends TestCase {
    JarURLConnection juc;

    public void test_getAttributes() throws Exception {
        URL u = createContent("lf.jar", "swt.dll");
        juc = ((JarURLConnection) (u.openConnection()));
        Attributes a = juc.getAttributes();
        TestCase.assertEquals("Returned incorrect Attributes", "SHA MD5", a.get(new Attributes.Name("Digest-Algorithms")));
        URL invURL = createContent("InvalidJar.jar", "Test.class");
        JarURLConnection juConn = ((JarURLConnection) (invURL.openConnection()));
        try {
            juConn.getAttributes();
            TestCase.fail("IOException was not thrown.");
        } catch (IOException io) {
            // expected
        }
    }

    public void test_getCertificates() throws Exception {
        URL u = createContent("TestCodeSigners.jar", "Test.class");
        juc = ((JarURLConnection) (u.openConnection()));
        TestCase.assertNull(juc.getCertificates());
        JarEntry je = juc.getJarEntry();
        JarFile jf = juc.getJarFile();
        InputStream is = jf.getInputStream(je);
        is.skip(je.getSize());
        Certificate[] certs = juc.getCertificates();
        TestCase.assertEquals(3, certs.length);
        URL invURL = createContent("InvalidJar.jar", "Test.class");
        JarURLConnection juConn = ((JarURLConnection) (invURL.openConnection()));
        try {
            juConn.getCertificates();
            TestCase.fail("IOException was not thrown.");
        } catch (IOException io) {
            // expected
        }
    }

    public void test_getManifest() throws Exception {
        URL u = createContent("lf.jar", "swt.dll");
        juc = ((JarURLConnection) (u.openConnection()));
        Manifest manifest = juc.getManifest();
        Map<String, Attributes> attr = manifest.getEntries();
        TestCase.assertEquals(new HashSet<String>(Arrays.asList("plus.bmp", "swt.dll")), attr.keySet());
        URL invURL = createContent("InvalidJar.jar", "Test.class");
        JarURLConnection juConn = ((JarURLConnection) (invURL.openConnection()));
        try {
            juConn.getManifest();
            TestCase.fail("IOException was not thrown.");
        } catch (IOException io) {
            // expected
        }
    }

    public void test_getEntryName() throws Exception {
        URL u = createContent("lf.jar", "plus.bmp");
        juc = ((JarURLConnection) (u.openConnection()));
        TestCase.assertEquals("Returned incorrect entryName", "plus.bmp", juc.getEntryName());
        u = createContent("lf.jar", "");
        juc = ((JarURLConnection) (u.openConnection()));
        TestCase.assertNull("Returned incorrect entryName", juc.getEntryName());
        // Regression test for harmony-3053
        URL url = new URL("jar:file:///bar.jar!/foo.jar!/Bugs/HelloWorld.class");
        TestCase.assertEquals("foo.jar!/Bugs/HelloWorld.class", ((JarURLConnection) (url.openConnection())).getEntryName());
    }

    public void test_getJarEntry() throws Exception {
        URL u = createContent("lf.jar", "plus.bmp");
        juc = ((JarURLConnection) (u.openConnection()));
        TestCase.assertEquals("Returned incorrect JarEntry", "plus.bmp", juc.getJarEntry().getName());
        u = createContent("lf.jar", "");
        juc = ((JarURLConnection) (u.openConnection()));
        TestCase.assertNull("Returned incorrect JarEntry", juc.getJarEntry());
        URL invURL = createContent("InvalidJar.jar", "Test.class");
        JarURLConnection juConn = ((JarURLConnection) (invURL.openConnection()));
        try {
            juConn.getJarEntry();
            TestCase.fail("IOException was not thrown.");
        } catch (IOException io) {
            // expected
        }
    }

    public void test_getJarFile() throws IOException {
        URL url = createContent("lf.jar", "missing");
        JarURLConnection connection = ((JarURLConnection) (url.openConnection()));
        try {
            connection.connect();
            TestCase.fail("Did not throw exception on connect");
        } catch (IOException e) {
            // expected
        }
        try {
            connection.getJarFile();
            TestCase.fail("Did not throw exception after connect");
        } catch (IOException e) {
            // expected
        }
        URL invURL = createContent("InvalidJar.jar", "Test.class");
        JarURLConnection juConn = ((JarURLConnection) (invURL.openConnection()));
        try {
            juConn.getJarFile();
            TestCase.fail("IOException was not thrown.");
        } catch (IOException io) {
            // expected
        }
        File resources = Support_Resources.createTempFolder();
        Support_Resources.copyFile(resources, null, "hyts_att.jar");
        File file = new File(((resources.toString()) + "/hyts_att.jar"));
        URL fUrl1 = new URL((("jar:file:" + (file.getPath())) + "!/"));
        JarURLConnection con1 = ((JarURLConnection) (fUrl1.openConnection()));
        ZipFile jf1 = con1.getJarFile();
        JarURLConnection con2 = ((JarURLConnection) (fUrl1.openConnection()));
        ZipFile jf2 = con2.getJarFile();
        TestCase.assertTrue("file: JarFiles not the same", (jf1 == jf2));
        jf1.close();
        TestCase.assertTrue("File should exist", file.exists());
        fUrl1 = createContent("lf.jar", "");
        con1 = ((JarURLConnection) (fUrl1.openConnection()));
        jf1 = con1.getJarFile();
        con2 = ((JarURLConnection) (fUrl1.openConnection()));
        jf2 = con2.getJarFile();
        TestCase.assertTrue("http: JarFiles not the same", (jf1 == jf2));
        jf1.close();
    }

    public void test_getJarFile29() throws Exception {
        File jarFile = File.createTempFile("1+2 3", "test.jar");
        jarFile.deleteOnExit();
        JarOutputStream out = new JarOutputStream(new FileOutputStream(jarFile));
        out.putNextEntry(new ZipEntry("test"));
        out.closeEntry();
        out.close();
        JarURLConnection conn = ((JarURLConnection) (new URL((("jar:file:" + (jarFile.getAbsolutePath().replaceAll(" ", "%20"))) + "!/")).openConnection()));
        conn.getJarFile().entries();
    }

    // Regression for HARMONY-3436
    public void test_setUseCaches() throws Exception {
        File resources = Support_Resources.createTempFolder();
        Support_Resources.copyFile(resources, null, "hyts_att.jar");
        File file = new File(((resources.toString()) + "/hyts_att.jar"));
        URL url = new URL((("jar:file:" + (file.getPath())) + "!/HasAttributes.txt"));
        JarURLConnection connection = ((JarURLConnection) (url.openConnection()));
        connection.setUseCaches(false);
        connection.getInputStream();
        InputStream in = connection.getInputStream();
        JarFile jarFile1 = connection.getJarFile();
        JarEntry jarEntry1 = connection.getJarEntry();
        in.read();
        in.close();
        JarFile jarFile2 = connection.getJarFile();
        JarEntry jarEntry2 = connection.getJarEntry();
        TestCase.assertSame(jarFile1, jarFile2);
        TestCase.assertSame(jarEntry1, jarEntry2);
        try {
            connection.getInputStream();
            TestCase.fail("should throw IllegalStateException");
        } catch (IllegalStateException e) {
            // expected
        }
    }

    public void test_getJarFileURL() throws Exception {
        URL u = createContent("lf.jar", "plus.bmp");
        URL fileURL = new URL(u.getPath().substring(0, u.getPath().indexOf("!")));
        juc = ((JarURLConnection) (u.openConnection()));
        TestCase.assertTrue("Returned incorrect file URL", juc.getJarFileURL().equals(fileURL));
        URL url = new URL("jar:file:///bar.jar!/foo.jar!/Bugs/HelloWorld.class");
        String jarFileUrl = ((JarURLConnection) (url.openConnection())).getJarFileURL().toString();
        // The RI omits the empty authority "//" but the RFC doesn't say this is necessary
        TestCase.assertTrue(((jarFileUrl.equals("file:///bar.jar")) || (jarFileUrl.equals("file:/bar.jar"))));
    }

    public void test_getMainAttributes() throws Exception {
        URL u = createContent("lf.jar", "swt.dll");
        juc = ((JarURLConnection) (u.openConnection()));
        Attributes a = juc.getMainAttributes();
        TestCase.assertEquals("Returned incorrect Attributes", "1.0", a.get(MANIFEST_VERSION));
        URL invURL = createContent("InvalidJar.jar", "Test.class");
        JarURLConnection juConn = ((JarURLConnection) (invURL.openConnection()));
        try {
            juConn.getMainAttributes();
            TestCase.fail("IOException was not thrown.");
        } catch (IOException io) {
            // expected
        }
    }

    public void test_getInputStream_DeleteJarFileUsingURLConnection() throws Exception {
        String entry = "text.txt";
        String cts = System.getProperty("java.io.tmpdir");
        File tmpDir = new File(cts);
        File jarFile = File.createTempFile("file", ".jar", tmpDir);
        String jarFileName = jarFile.getPath();
        FileOutputStream jarFileOutputStream = new FileOutputStream(jarFileName);
        JarOutputStream out = new JarOutputStream(new BufferedOutputStream(jarFileOutputStream));
        JarEntry jarEntry = new JarEntry(entry);
        out.putNextEntry(jarEntry);
        out.write(new byte[]{ 'a', 'b', 'c' });
        out.close();
        URL url = new URL(((("jar:file:" + jarFileName) + "!/") + entry));
        URLConnection conn = url.openConnection();
        conn.setUseCaches(false);
        InputStream is = conn.getInputStream();
        is.close();
        TestCase.assertTrue(jarFile.delete());
    }

    public void test_Constructor() {
        try {
            String jarFileName = "file.jar";
            String entry = "text.txt";
            URL url = new URL(((("jar:file:" + jarFileName) + "!/") + entry));
            OldJarURLConnectionTest.TestJarURLConnection jarConn = new OldJarURLConnectionTest.TestJarURLConnection(url);
            TestCase.assertEquals(new URL("file:file.jar"), jarConn.getJarFileURL());
        } catch (MalformedURLException me) {
            TestCase.fail("MalformedURLException was thrown.");
        }
        try {
            URL[] urls = new URL[]{ new URL("file:file.jar"), new URL("http://foo.com/foo/foo.jar") };
            for (URL url : urls) {
                try {
                    new OldJarURLConnectionTest.TestJarURLConnection(url);
                    TestCase.fail("MalformedURLException was not thrown.");
                } catch (MalformedURLException me) {
                    // expected
                }
            }
        } catch (MalformedURLException me) {
            TestCase.fail("MalformedURLException was thrown.");
        }
    }

    class TestJarURLConnection extends JarURLConnection {
        protected TestJarURLConnection(URL arg0) throws MalformedURLException {
            super(arg0);
        }

        @Override
        public JarFile getJarFile() throws IOException {
            return null;
        }

        @Override
        public void connect() throws IOException {
        }
    }
}

