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


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import junit.framework.TestCase;
import tests.support.resource.Support_Resources;

import static java.util.jar.Attributes.Name.MANIFEST_VERSION;
import static java.util.jar.Attributes.Name.SIGNATURE_VERSION;


public class OldManifestTest extends TestCase {
    public void test_ConstructorLjava_util_jar_Manifest() {
        // Test for method java.util.jar.Manifest()
        Manifest emptyManifest = new Manifest();
        Manifest emptyClone = new Manifest(emptyManifest);
        TestCase.assertTrue("Should have no entries", emptyClone.getEntries().isEmpty());
        TestCase.assertTrue("Should have no main attributes", emptyClone.getMainAttributes().isEmpty());
        TestCase.assertEquals(emptyClone, emptyManifest);
        TestCase.assertEquals(emptyClone, emptyManifest.clone());
    }

    public void test_clone() throws IOException {
        Manifest emptyManifest = new Manifest();
        Manifest emptyClone = ((Manifest) (emptyManifest.clone()));
        TestCase.assertTrue("Should have no entries", emptyClone.getEntries().isEmpty());
        TestCase.assertTrue("Should have no main attributes", emptyClone.getMainAttributes().isEmpty());
        TestCase.assertEquals(emptyClone, emptyManifest);
        TestCase.assertEquals(emptyManifest.clone().getClass().getName(), "java.util.jar.Manifest");
        Manifest manifest = new Manifest(new URL(Support_Resources.getURL("manifest/hyts_MANIFEST.MF")).openStream());
        Manifest manifestClone = ((Manifest) (manifest.clone()));
        manifestClone.getMainAttributes();
        checkManifest(manifestClone);
    }

    public void test_equals() throws IOException {
        Manifest manifest1 = new Manifest(new URL(Support_Resources.getURL("manifest/hyts_MANIFEST.MF")).openStream());
        Manifest manifest2 = new Manifest(new URL(Support_Resources.getURL("manifest/hyts_MANIFEST.MF")).openStream());
        Manifest manifest3 = new Manifest();
        TestCase.assertTrue(manifest1.equals(manifest1));
        TestCase.assertTrue(manifest1.equals(manifest2));
        TestCase.assertFalse(manifest1.equals(manifest3));
        TestCase.assertFalse(manifest1.equals(this));
    }

    public void test_writeLjava_io_OutputStream() throws IOException {
        byte[] b;
        Manifest manifest1 = null;
        Manifest manifest2 = null;
        try {
            manifest1 = new Manifest(new URL(Support_Resources.getURL("manifest/hyts_MANIFEST.MF")).openStream());
        } catch (MalformedURLException e) {
            TestCase.fail("Malformed URL");
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        manifest1.write(baos);
        b = baos.toByteArray();
        File f = File.createTempFile("111", "111");
        FileOutputStream fos = new FileOutputStream(f);
        fos.close();
        try {
            manifest1.write(fos);
            TestCase.fail("IOException expected");
        } catch (IOException e) {
            // expected
        }
        f.delete();
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        try {
            manifest2 = new Manifest(bais);
        } catch (MalformedURLException e) {
            TestCase.fail("Malformed URL");
        }
        TestCase.assertTrue(manifest1.equals(manifest2));
    }

    public void test_write_no_version() throws Exception {
        // If you write a manifest with no MANIFEST_VERSION, your attributes don't get written out.
        TestCase.assertEquals(null, doRoundTrip(null));
        // But they do if you supply a MANIFEST_VERSION.
        TestCase.assertEquals("image/pr0n", doRoundTrip(MANIFEST_VERSION));
        TestCase.assertEquals("image/pr0n", doRoundTrip("Signature-Version"));
        TestCase.assertEquals(null, doRoundTrip("Random-String-Version"));
    }

    public void test_write_two_versions() throws Exception {
        // It's okay to have two versions.
        Manifest m1 = new Manifest();
        m1.getMainAttributes().put(MANIFEST_VERSION, "1.0");
        m1.getMainAttributes().put(SIGNATURE_VERSION, "2.0");
        m1.getMainAttributes().putValue("Aardvark-Version", "3.0");
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        m1.write(os);
        // The Manifest-Version takes precedence,
        // and the Signature-Version gets no special treatment.
        String[] lines = new String(os.toByteArray(), "UTF-8").split("\r\n");
        TestCase.assertEquals("Manifest-Version: 1.0", lines[0]);
        TestCase.assertEquals("Aardvark-Version: 3.0", lines[1]);
        TestCase.assertEquals("Signature-Version: 2.0", lines[2]);
    }
}

