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
package libcore.java.util.jar;


import java.io.File;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import junit.framework.TestCase;
import tests.support.resource.Support_Resources;


public class OldJarEntryTest extends TestCase {
    private ZipEntry zipEntry;

    private JarEntry jarEntry;

    private JarFile jarFile;

    private final String jarName = "hyts_patch.jar";

    private final String entryName = "foo/bar/A.class";

    private File resources;

    /**
     *
     *
     * @throws IOException
    java.util.jar.JarEntry#JarEntry(java.util.jar.JarEntry)
     * 		
     */
    public void test_ConstructorLjava_util_jar_JarEntry_on_null() throws IOException {
        JarEntry newJarEntry = new JarEntry(jarFile.getJarEntry(entryName));
        TestCase.assertNotNull(newJarEntry);
        jarEntry = null;
        try {
            newJarEntry = new JarEntry(jarEntry);
            TestCase.fail("Should throw NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }
    }

    /**
     * java.util.jar.JarEntry#JarEntry(java.util.zip.ZipEntry)
     */
    public void test_ConstructorLjava_util_zip_ZipEntry() {
        TestCase.assertNotNull("Jar file is null", jarFile);
        zipEntry = jarFile.getEntry(entryName);
        TestCase.assertNotNull("Zip entry is null", zipEntry);
        jarEntry = new JarEntry(zipEntry);
        TestCase.assertNotNull("Jar entry is null", jarEntry);
        TestCase.assertEquals("Wrong entry constructed--wrong name", entryName, jarEntry.getName());
        TestCase.assertEquals("Wrong entry constructed--wrong size", 311, jarEntry.getSize());
    }

    /**
     * java.util.jar.JarEntry#getAttributes()
     */
    public void test_getAttributes() {
        JarFile attrJar = null;
        File file = null;
        Support_Resources.copyFile(resources, null, "Broken_manifest.jar");
        try {
            attrJar = new JarFile(new File(resources, "Broken_manifest.jar"));
            jarEntry = attrJar.getJarEntry("META-INF/");
            jarEntry.getAttributes();
            TestCase.fail("IOException expected");
        } catch (IOException e) {
            // expected.
        }
    }

    public void test_ConstructorLjava_lang_String() {
        TestCase.assertNotNull("Jar file is null", jarFile);
        zipEntry = jarFile.getEntry(entryName);
        TestCase.assertNotNull("Zip entry is null", zipEntry);
        jarEntry = new JarEntry(entryName);
        TestCase.assertNotNull("Jar entry is null", jarEntry);
        TestCase.assertEquals("Wrong entry constructed--wrong name", entryName, jarEntry.getName());
        try {
            jarEntry = new JarEntry(((String) (null)));
            TestCase.fail("NullPointerException expected");
        } catch (NullPointerException ee) {
            // expected
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 65536; i++) {
            sb.append('3');
        }
        try {
            jarEntry = new JarEntry(new String(sb));
            TestCase.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException ee) {
            // expected
        }
    }

    public void test_ConstructorLjava_util_jar_JarEntry() {
        TestCase.assertNotNull("Jar file is null", jarFile);
        JarEntry je = jarFile.getJarEntry(entryName);
        TestCase.assertNotNull("Jar entry is null", je);
        jarEntry = new JarEntry(je);
        TestCase.assertNotNull("Jar entry is null", jarEntry);
        TestCase.assertEquals("Wrong entry constructed--wrong name", entryName, jarEntry.getName());
        TestCase.assertEquals("Wrong entry constructed--wrong size", 311, jarEntry.getSize());
    }
}

