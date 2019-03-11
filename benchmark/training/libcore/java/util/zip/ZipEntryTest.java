/**
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package libcore.java.util.zip;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import junit.framework.TestCase;


public class ZipEntryTest extends TestCase {
    // http://code.google.com/p/android/issues/detail?id=4690
    public void test_utf8FileNames() throws Exception {
        // Create a zip file containing non-ASCII filenames.
        File f = File.createTempFile("your", "mum");
        List<String> filenames = // russian
        // greek
        Arrays.asList("us-ascii", "\u043c\u0430\u0440\u0442\u0430", "\u1f00\u03c0\u1f78", "\u30b3\u30f3\u30cb\u30c1\u30cf");// japanese

        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(f));
        for (String filename : filenames) {
            out.putNextEntry(new ZipEntry(filename));
            out.closeEntry();// Empty files are fine.

        }
        out.close();
        // Read it back, and check we find all those names.
        // This failed when we were mangling the encoding.
        ZipFile zipFile = new ZipFile(f);
        for (String filename : filenames) {
            TestCase.assertNotNull(filename, zipFile.getEntry(filename));
        }
        // Check that ZipInputStream works too.
        ZipInputStream in = new ZipInputStream(new FileInputStream(f));
        ZipEntry entry;
        int entryCount = 0;
        while ((entry = in.getNextEntry()) != null) {
            TestCase.assertTrue(entry.getName(), filenames.contains(entry.getName()));
            ++entryCount;
        } 
        TestCase.assertEquals(filenames.size(), entryCount);
        in.close();
    }

    // http://b/2099615
    public void testClone() {
        byte[] extra = new byte[]{ 5, 7, 9 };
        JarEntry jarEntry = new JarEntry("foo");
        jarEntry.setExtra(extra);
        TestCase.assertSame("Expected no defensive copy of extra", extra, jarEntry.getExtra());
        ZipEntry clone = ((ZipEntry) (jarEntry.clone()));
        TestCase.assertEquals(JarEntry.class, clone.getClass());
        TestCase.assertNotSame(extra, clone.getExtra());
    }

    public void testTooLongName() throws Exception {
        String tooLongName = ZipEntryTest.makeString(65536, "z");
        try {
            new ZipEntry(tooLongName);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    public void testMaxLengthName() throws Exception {
        String maxLengthName = ZipEntryTest.makeString(65535, "z");
        File f = ZipEntryTest.createTemporaryZipFile();
        ZipOutputStream out = ZipEntryTest.createZipOutputStream(f);
        out.putNextEntry(new ZipEntry(maxLengthName));
        out.closeEntry();
        out.close();
        // Read it back, and check that we see the entry.
        ZipFile zipFile = new ZipFile(f);
        TestCase.assertNotNull(zipFile.getEntry(maxLengthName));
        zipFile.close();
    }

    public void testTooLongExtra() throws Exception {
        byte[] tooLongExtra = new byte[65536];
        ZipEntry ze = new ZipEntry("x");
        try {
            ze.setExtra(tooLongExtra);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    public void testMaxLengthExtra() throws Exception {
        byte[] maxLengthExtra = new byte[65535];
        File f = ZipEntryTest.createTemporaryZipFile();
        ZipOutputStream out = ZipEntryTest.createZipOutputStream(f);
        ZipEntry ze = new ZipEntry("x");
        ze.setExtra(maxLengthExtra);
        out.putNextEntry(ze);
        out.closeEntry();
        out.close();
        // Read it back, and check that we see the entry.
        ZipFile zipFile = new ZipFile(f);
        TestCase.assertEquals(maxLengthExtra.length, zipFile.getEntry("x").getExtra().length);
        zipFile.close();
    }

    public void testTooLongComment() throws Exception {
        String tooLongComment = ZipEntryTest.makeString(65536, "z");
        ZipEntry ze = new ZipEntry("x");
        try {
            ze.setComment(tooLongComment);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    public void testMaxLengthComment() throws Exception {
        String maxLengthComment = ZipEntryTest.makeString(65535, "z");
        File f = ZipEntryTest.createTemporaryZipFile();
        ZipOutputStream out = ZipEntryTest.createZipOutputStream(f);
        ZipEntry ze = new ZipEntry("x");
        ze.setComment(maxLengthComment);
        out.putNextEntry(ze);
        out.closeEntry();
        out.close();
        // Read it back, and check that we see the entry.
        ZipFile zipFile = new ZipFile(f);
        TestCase.assertEquals(maxLengthComment, zipFile.getEntry("x").getComment());
        zipFile.close();
    }

    public void testCommentAndExtraInSameOrder() throws Exception {
        String comment = ZipEntryTest.makeString(17, "z");
        byte[] extra = ZipEntryTest.makeString(11, "a").getBytes();
        File f = ZipEntryTest.createTemporaryZipFile();
        ZipOutputStream out = ZipEntryTest.createZipOutputStream(f);
        ZipEntry ze = new ZipEntry("x");
        ze.setExtra(extra);
        ze.setComment(comment);
        out.putNextEntry(ze);
        out.closeEntry();
        out.close();
        // Read it back and make sure comments and extra are in the right order
        ZipFile zipFile = new ZipFile(f);
        try {
            TestCase.assertEquals(comment, zipFile.getEntry("x").getComment());
            TestCase.assertTrue(Arrays.equals(extra, zipFile.getEntry("x").getExtra()));
        } finally {
            zipFile.close();
        }
    }
}

