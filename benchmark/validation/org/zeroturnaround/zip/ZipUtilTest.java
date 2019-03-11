/**
 * Copyright (C) 2012 ZeroTurnaround LLC <support@zeroturnaround.com>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.zeroturnaround.zip;


import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import junit.framework.TestCase;
import org.zeroturnaround.zip.commons.FileUtils;
import org.zeroturnaround.zip.commons.IOUtils;


/**
 *
 *
 * @unknown ResultOfMethodCallIgnored
 */
public class ZipUtilTest extends TestCase {
    public void testPackEntryStream() {
        File src = ZipUtilTest.file("TestFile.txt");
        byte[] bytes = ZipUtil.packEntry(src);
        boolean processed = ZipUtil.handle(new ByteArrayInputStream(bytes), "TestFile.txt", new ZipEntryCallback() {
            public void process(InputStream in, ZipEntry zipEntry) throws IOException {
            }
        });
        TestCase.assertTrue(processed);
    }

    public void testPackEntryFile() throws Exception {
        File fileToPack = ZipUtilTest.file("TestFile.txt");
        File dest = File.createTempFile("temp", null);
        ZipUtil.packEntry(fileToPack, dest);
        TestCase.assertTrue(dest.exists());
        ZipUtil.explode(dest);
        TestCase.assertTrue(new File(dest, "TestFile.txt").exists());
        // if fails then maybe somebody changed the file contents and did not update
        // the test
        TestCase.assertEquals(108, new File(dest, "TestFile.txt").length());
    }

    public void testPackEntryFileWithNameParameter() throws Exception {
        File fileToPack = ZipUtilTest.file("TestFile.txt");
        File dest = File.createTempFile("temp", null);
        ZipUtil.packEntry(fileToPack, dest, "TestFile-II.txt");
        TestCase.assertTrue(dest.exists());
        ZipUtil.explode(dest);
        TestCase.assertTrue(new File(dest, "TestFile-II.txt").exists());
        // if fails then maybe somebody changed the file contents and did not update
        // the test
        TestCase.assertEquals(108, new File(dest, "TestFile-II.txt").length());
    }

    public void testPackEntryFileWithNameMapper() throws Exception {
        File fileToPack = ZipUtilTest.file("TestFile.txt");
        File dest = File.createTempFile("temp", null);
        ZipUtil.packEntry(fileToPack, dest, new NameMapper() {
            public String map(String name) {
                return "TestFile-II.txt";
            }
        });
        TestCase.assertTrue(dest.exists());
        ZipUtil.explode(dest);
        TestCase.assertTrue(new File(dest, "TestFile-II.txt").exists());
        // if fails then maybe somebody changed the file contents and did not update
        // the test
        TestCase.assertEquals(108, new File(dest, "TestFile-II.txt").length());
    }

    public void testUnpackEntryFromFile() throws IOException {
        final String name = "foo";
        final byte[] contents = "bar".getBytes();
        File file = File.createTempFile("temp", null);
        try {
            // Create the ZIP file
            ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(file));
            try {
                zos.putNextEntry(new ZipEntry(name));
                zos.write(contents);
                zos.closeEntry();
            } finally {
                IOUtils.closeQuietly(zos);
            }
            // Test the ZipUtil
            byte[] actual = ZipUtil.unpackEntry(file, name);
            TestCase.assertNotNull(actual);
            TestCase.assertEquals(new String(contents), new String(actual));
        } finally {
            FileUtils.deleteQuietly(file);
        }
    }

    public void testUnpackEntryFromStreamToFile() throws IOException {
        final String name = "foo";
        final byte[] contents = "bar".getBytes();
        File file = File.createTempFile("temp", null);
        try {
            // Create the ZIP file
            ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(file));
            try {
                zos.putNextEntry(new ZipEntry(name));
                zos.write(contents);
                zos.closeEntry();
            } finally {
                IOUtils.closeQuietly(zos);
            }
            FileInputStream fis = new FileInputStream(file);
            File outputFile = File.createTempFile("temp-output", null);
            boolean result = ZipUtil.unpackEntry(fis, name, outputFile);
            TestCase.assertTrue(result);
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(outputFile));
            byte[] actual = new byte[1024];
            int read = bis.read(actual);
            bis.close();
            TestCase.assertEquals(new String(contents), new String(actual, 0, read));
        } finally {
            FileUtils.deleteQuietly(file);
        }
    }

    public void testUnpackEntryFromStream() throws IOException {
        final String name = "foo";
        final byte[] contents = "bar".getBytes();
        File file = File.createTempFile("temp", null);
        try {
            // Create the ZIP file
            ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(file));
            try {
                zos.putNextEntry(new ZipEntry(name));
                zos.write(contents);
                zos.closeEntry();
            } finally {
                IOUtils.closeQuietly(zos);
            }
            FileInputStream fis = new FileInputStream(file);
            // Test the ZipUtil
            byte[] actual = ZipUtil.unpackEntry(fis, name);
            TestCase.assertNotNull(actual);
            TestCase.assertEquals(new String(contents), new String(actual));
        } finally {
            FileUtils.deleteQuietly(file);
        }
    }

    public void testDuplicateEntryAtAdd() throws IOException {
        File src = ZipUtilTest.file("duplicate.zip");
        File dest = File.createTempFile("temp", null);
        try {
            ZipUtil.addEntries(src, new ZipEntrySource[0], dest);
        } finally {
            FileUtils.deleteQuietly(dest);
        }
    }

    public void testDuplicateEntryAtReplace() throws IOException {
        File src = ZipUtilTest.file("duplicate.zip");
        File dest = File.createTempFile("temp", null);
        try {
            ZipUtil.replaceEntries(src, new ZipEntrySource[0], dest);
        } finally {
            FileUtils.deleteQuietly(dest);
        }
    }

    public void testDuplicateEntryAtAddOrReplace() throws IOException {
        File src = ZipUtilTest.file("duplicate.zip");
        File dest = File.createTempFile("temp", null);
        try {
            ZipUtil.addOrReplaceEntries(src, new ZipEntrySource[0], dest);
        } finally {
            FileUtils.deleteQuietly(dest);
        }
    }

    public void testUnexplode() throws IOException {
        File file = File.createTempFile("tempFile", null);
        File tmpDir = file.getParentFile();
        unexplodeWithException(file, "shouldn't be able to unexplode file that is not a directory");
        TestCase.assertTrue("Should be able to delete tmp file", file.delete());
        unexplodeWithException(file, "shouldn't be able to unexplode file that doesn't exist");
        // create empty tmp dir with the same name as deleted file
        File dir = new File(tmpDir, file.getName());
        dir.deleteOnExit();
        TestCase.assertTrue("Should be able to create directory with the same name as there was tmp file", dir.mkdir());
        unexplodeWithException(dir, "shouldn't be able to unexplode dir that doesn't contain any files");
        // unexplode should succeed with at least one file in directory
        File.createTempFile("temp", null, dir);
        ZipUtil.unexplode(dir);
        TestCase.assertTrue("zip file should exist with the same name as the directory that was unexploded", dir.exists());
        TestCase.assertTrue("unexploding input directory should have produced zip file with the same name", (!(dir.isDirectory())));
        TestCase.assertTrue("Should be able to delete zip that was created from directory", dir.delete());
    }

    public void testPackEntriesWithCompressionLevel() throws Exception {
        long filesizeBestCompression = 0;
        long filesizeNoCompression = 0;
        ZipFile zf = null;
        File dest = null;
        try {
            dest = File.createTempFile("temp-stor", null);
            ZipUtil.packEntries(new File[]{ ZipUtilTest.file("TestFile.txt"), ZipUtilTest.file("TestFile-II.txt") }, dest, Deflater.BEST_COMPRESSION);
            zf = new ZipFile(dest);
            filesizeBestCompression = zf.getEntry("TestFile.txt").getCompressedSize();
        } finally {
            zf.close();
        }
        try {
            dest = File.createTempFile("temp-stor", null);
            ZipUtil.packEntries(new File[]{ ZipUtilTest.file("TestFile.txt"), ZipUtilTest.file("TestFile-II.txt") }, dest, Deflater.NO_COMPRESSION);
            zf = new ZipFile(dest);
            filesizeNoCompression = zf.getEntry("TestFile.txt").getCompressedSize();
        } finally {
            zf.close();
        }
        TestCase.assertTrue((filesizeNoCompression > 0));
        TestCase.assertTrue((filesizeBestCompression > 0));
        TestCase.assertTrue((filesizeNoCompression > filesizeBestCompression));
    }

    public void testPackEntries() throws Exception {
        File fileToPack = ZipUtilTest.file("TestFile.txt");
        File fileToPackII = ZipUtilTest.file("TestFile-II.txt");
        File dest = File.createTempFile("temp", null);
        ZipUtil.packEntries(new File[]{ fileToPack, fileToPackII }, dest);
        TestCase.assertTrue(dest.exists());
        ZipUtil.explode(dest);
        TestCase.assertTrue(new File(dest, "TestFile.txt").exists());
        TestCase.assertTrue(new File(dest, "TestFile-II.txt").exists());
        // if fails then maybe somebody changed the file contents and did not update the test
        TestCase.assertEquals(108, new File(dest, "TestFile.txt").length());
        TestCase.assertEquals(103, new File(dest, "TestFile-II.txt").length());
    }

    public void testPackEntriesToStream() throws Exception {
        String encoding = "UTF-8";
        // list of entries, each entry consists of entry name and entry contents
        List<String[]> entryDescriptions = Arrays.asList(new String[][]{ new String[]{ "foo.txt", "foo" }, new String[]{ "bar.txt", "bar" } });
        ByteArrayOutputStream out = null;
        out = new ByteArrayOutputStream();
        ZipUtil.pack(convertToEntries(entryDescriptions, encoding), out);
        byte[] zipBytes = out.toByteArray();
        TestCase.assertEquals(244, zipBytes.length);
        assertEntries(entryDescriptions, zipBytes, encoding);
    }

    public void testAddEntriesToStream() throws Exception {
        String encoding = "UTF-8";
        // list of entries, each entry consists of entry name and entry contents
        List<String[]> entryDescriptions = Arrays.asList(new String[][]{ new String[]{ "foo.txt", "foo" }, new String[]{ "bar.txt", "bar" } });
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ZipUtil.pack(convertToEntries(entryDescriptions, encoding), out);
        byte[] zipBytes = out.toByteArray();
        List<String[]> entryDescriptions2 = Arrays.asList(new String[][]{ new String[]{ "foo2.txt", "foo2" }, new String[]{ "bar2.txt", "bar2" } });
        ByteArrayOutputStream out2 = new ByteArrayOutputStream();
        ZipUtil.addEntries(new ByteArrayInputStream(zipBytes), convertToEntries(entryDescriptions2, encoding), out2);
        byte[] zipBytes2 = out2.toByteArray();
        ArrayList<String[]> allEntryDescriptions = new ArrayList<String[]>(((entryDescriptions.size()) + (entryDescriptions2.size())));
        allEntryDescriptions.addAll(entryDescriptions);
        allEntryDescriptions.addAll(entryDescriptions2);
        assertEntries(allEntryDescriptions, zipBytes2, encoding);
    }

    public void testPackEntriesWithNameMapper() throws Exception {
        File fileToPack = ZipUtilTest.file("TestFile.txt");
        File fileToPackII = ZipUtilTest.file("TestFile-II.txt");
        File dest = File.createTempFile("temp", null);
        ZipUtil.packEntries(new File[]{ fileToPack, fileToPackII }, dest, new NameMapper() {
            public String map(String name) {
                return "Changed-" + name;
            }
        });
        TestCase.assertTrue(dest.exists());
        ZipUtil.explode(dest);
        TestCase.assertTrue(new File(dest, "Changed-TestFile.txt").exists());
        TestCase.assertTrue(new File(dest, "Changed-TestFile-II.txt").exists());
        // if fails then maybe somebody changed the file contents and did not update
        // the test
        TestCase.assertEquals(108, new File(dest, "Changed-TestFile.txt").length());
        TestCase.assertEquals(103, new File(dest, "Changed-TestFile-II.txt").length());
    }

    public void testZipException() {
        boolean exceptionThrown = false;
        File target = new File("weeheha");
        try {
            ZipUtil.pack(new File("nonExistent"), target);
        } catch (ZipException e) {
            exceptionThrown = true;
        }
        TestCase.assertFalse("Target file is created when source does not exist", target.exists());
        TestCase.assertTrue(exceptionThrown);
    }

    public void testPackEntriesWithNamesList() throws Exception {
        File fileToPack = ZipUtilTest.file("TestFile.txt");
        File fileToPackII = ZipUtilTest.file("TestFile-II.txt");
        File dest = File.createTempFile("temp", null);
        ZipUtil.pack(FileSource.pair(new File[]{ fileToPack, fileToPackII }, new String[]{ "Changed-TestFile.txt", "Changed-TestFile-II.txt" }), dest);
        TestCase.assertTrue(dest.exists());
        ZipUtil.explode(dest);
        TestCase.assertTrue(new File(dest, "Changed-TestFile.txt").exists());
        TestCase.assertTrue(new File(dest, "Changed-TestFile-II.txt").exists());
        // if fails then maybe somebody changed the file contents and did not update
        // the test
        TestCase.assertEquals(108, new File(dest, "Changed-TestFile.txt").length());
        TestCase.assertEquals(103, new File(dest, "Changed-TestFile-II.txt").length());
    }

    public void testPreserveRoot() throws Exception {
        File dest = File.createTempFile("temp", null);
        File parent = ZipUtilTest.file("TestFile.txt").getParentFile();
        ZipUtil.pack(parent, dest, true);
        ZipUtil.explode(dest);
        TestCase.assertTrue(new File(dest, parent.getName()).exists());
    }

    public void testArchiveEquals() {
        File src = ZipUtilTest.file("demo.zip");
        // byte-by-byte copy
        File src2 = ZipUtilTest.file("demo-copy.zip");
        TestCase.assertTrue(ZipUtil.archiveEquals(src, src2));
        // entry by entry copy
        File src3 = ZipUtilTest.file("demo-copy-II.zip");
        TestCase.assertTrue(ZipUtil.archiveEquals(src, src3));
    }

    public void testRepackArchive() throws IOException {
        File src = ZipUtilTest.file("demo.zip");
        File dest = File.createTempFile("temp", null);
        ZipUtil.repack(src, dest, 1);
        TestCase.assertTrue(ZipUtil.archiveEquals(src, dest));
    }

    public void testContainsAnyEntry() throws IOException {
        File src = ZipUtilTest.file("demo.zip");
        boolean exists = ZipUtil.containsAnyEntry(src, new String[]{ "foo.txt", "bar.txt" });
        TestCase.assertTrue(exists);
        exists = ZipUtil.containsAnyEntry(src, new String[]{ "foo.txt", "does-not-exist.txt" });
        TestCase.assertTrue(exists);
        exists = ZipUtil.containsAnyEntry(src, new String[]{ "does-not-exist-I.txt", "does-not-exist-II.txt" });
        TestCase.assertFalse(exists);
    }

    public void testAddEntry() throws IOException {
        File initialSrc = ZipUtilTest.file("demo.zip");
        File src = File.createTempFile("ztr", ".zip");
        FileUtils.copyFile(initialSrc, src);
        final String fileName = "TestFile.txt";
        if (ZipUtil.containsEntry(src, fileName)) {
            ZipUtil.removeEntry(src, fileName);
        }
        TestCase.assertFalse(ZipUtil.containsEntry(src, fileName));
        File newEntry = ZipUtilTest.file(fileName);
        File dest = File.createTempFile("temp.zip", null);
        ZipUtil.addEntry(src, fileName, newEntry, dest);
        TestCase.assertTrue(ZipUtil.containsEntry(dest, fileName));
        FileUtils.forceDelete(src);
    }

    public void testKeepEntriesState() throws IOException {
        File src = ZipUtilTest.file("demo-keep-entries-state.zip");
        final String existingEntryName = "TestFile.txt";
        final String fileNameToAdd = "TestFile-II.txt";
        TestCase.assertFalse(ZipUtil.containsEntry(src, fileNameToAdd));
        File newEntry = ZipUtilTest.file(fileNameToAdd);
        File dest = File.createTempFile("temp.zip", null);
        ZipUtil.addEntry(src, fileNameToAdd, newEntry, dest);
        ZipEntry srcEntry = new ZipFile(src).getEntry(existingEntryName);
        ZipEntry destEntry = new ZipFile(dest).getEntry(existingEntryName);
        TestCase.assertTrue(((srcEntry.getCompressedSize()) == (destEntry.getCompressedSize())));
    }

    public void testRemoveEntry() throws IOException {
        File src = ZipUtilTest.file("demo.zip");
        File dest = File.createTempFile("temp", null);
        try {
            ZipUtil.removeEntry(src, "bar.txt", dest);
            TestCase.assertTrue("Result zip misses entry 'foo.txt'", ZipUtil.containsEntry(dest, "foo.txt"));
            TestCase.assertTrue("Result zip misses entry 'foo1.txt'", ZipUtil.containsEntry(dest, "foo1.txt"));
            TestCase.assertTrue("Result zip misses entry 'foo2.txt'", ZipUtil.containsEntry(dest, "foo2.txt"));
            TestCase.assertFalse("Result zip still contains 'bar.txt'", ZipUtil.containsEntry(dest, "bar.txt"));
        } finally {
            FileUtils.deleteQuietly(dest);
        }
    }

    public void testRemoveMissingEntry() throws IOException {
        File src = ZipUtilTest.file("demo.zip");
        TestCase.assertFalse("Source zip contains entry 'missing.txt'", ZipUtil.containsEntry(src, "missing.txt"));
        File dest = File.createTempFile("temp", null);
        try {
            ZipUtil.removeEntry(src, "missing.txt", dest);
        } finally {
            FileUtils.deleteQuietly(dest);
        }
    }

    public void testRemoveDirs() throws IOException {
        File src = ZipUtilTest.file("demo-dirs.zip");
        File dest = File.createTempFile("temp", null);
        try {
            ZipUtil.removeEntries(src, new String[]{ "bar.txt", "a/b" }, dest);
            TestCase.assertFalse("Result zip still contains 'bar.txt'", ZipUtil.containsEntry(dest, "bar.txt"));
            TestCase.assertFalse("Result zip still contains dir 'a/b'", ZipUtil.containsEntry(dest, "a/b"));
            TestCase.assertTrue("Result doesn't contain 'attic'", ZipUtil.containsEntry(dest, "attic/treasure.txt"));
            TestCase.assertTrue("Entry whose prefix is dir name is removed too: 'b.txt'", ZipUtil.containsEntry(dest, "a/b.txt"));
            TestCase.assertFalse("Entry in a removed dir is still there: 'a/b/c.txt'", ZipUtil.containsEntry(dest, "a/b/c.txt"));
        } finally {
            FileUtils.deleteQuietly(dest);
        }
    }

    public void testRemoveDirsOutputStream() throws IOException {
        File src = ZipUtilTest.file("demo-dirs.zip");
        File dest = File.createTempFile("temp", null);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(dest);
            ZipUtil.removeEntries(src, new String[]{ "bar.txt", "a/b" }, out);
            TestCase.assertFalse("Result zip still contains 'bar.txt'", ZipUtil.containsEntry(dest, "bar.txt"));
            TestCase.assertFalse("Result zip still contains dir 'a/b'", ZipUtil.containsEntry(dest, "a/b"));
            TestCase.assertTrue("Result doesn't contain 'attic'", ZipUtil.containsEntry(dest, "attic/treasure.txt"));
            TestCase.assertTrue("Entry whose prefix is dir name is removed too: 'b.txt'", ZipUtil.containsEntry(dest, "a/b.txt"));
            TestCase.assertFalse("Entry in a removed dir is still there: 'a/b/c.txt'", ZipUtil.containsEntry(dest, "a/b/c.txt"));
        } finally {
            IOUtils.closeQuietly(out);
            FileUtils.deleteQuietly(dest);
        }
    }

    public void testHandle() {
        File src = ZipUtilTest.file("demo.zip");
        boolean entryFound = ZipUtil.handle(src, "foo.txt", new ZipEntryCallback() {
            public void process(InputStream in, ZipEntry zipEntry) throws IOException {
                TestCase.assertEquals("foo.txt", zipEntry.getName());
            }
        });
        TestCase.assertTrue(entryFound);
        entryFound = ZipUtil.handle(src, "non-existent-file.txt", new ZipEntryCallback() {
            public void process(InputStream in, ZipEntry zipEntry) throws IOException {
                throw new RuntimeException("This should not happen!");
            }
        });
        TestCase.assertFalse(entryFound);
    }

    public void testIterate() {
        File src = ZipUtilTest.file("demo.zip");
        final Set files = new HashSet();
        files.add("foo.txt");
        files.add("bar.txt");
        files.add("foo1.txt");
        files.add("foo2.txt");
        ZipUtil.iterate(src, new ZipInfoCallback() {
            public void process(ZipEntry zipEntry) throws IOException {
                files.remove(zipEntry.getName());
            }
        });
        TestCase.assertEquals(0, files.size());
    }

    public void testIterateGivenEntriesZipInfoCallback() {
        File src = ZipUtilTest.file("demo.zip");
        final Set files = new HashSet();
        files.add("foo.txt");
        files.add("bar.txt");
        files.add("foo1.txt");
        files.add("foo2.txt");
        ZipUtil.iterate(src, new String[]{ "foo.txt", "foo1.txt", "foo2.txt" }, new ZipInfoCallback() {
            public void process(ZipEntry zipEntry) throws IOException {
                files.remove(zipEntry.getName());
            }
        });
        TestCase.assertEquals(1, files.size());
        TestCase.assertTrue("Wrong entry hasn't been iterated", files.contains("bar.txt"));
    }

    public void testIterateGivenEntriesZipEntryCallback() {
        File src = ZipUtilTest.file("demo.zip");
        final Set files = new HashSet();
        files.add("foo.txt");
        files.add("bar.txt");
        files.add("foo1.txt");
        files.add("foo2.txt");
        ZipUtil.iterate(src, new String[]{ "foo.txt", "foo1.txt", "foo2.txt" }, new ZipEntryCallback() {
            public void process(InputStream in, ZipEntry zipEntry) throws IOException {
                files.remove(zipEntry.getName());
            }
        });
        TestCase.assertEquals(1, files.size());
        TestCase.assertTrue("Wrong entry hasn't been iterated", files.contains("bar.txt"));
    }

    public void testIterateGivenEntriesFromStream() throws IOException {
        File src = ZipUtilTest.file("demo.zip");
        final Set files = new HashSet();
        files.add("foo.txt");
        files.add("bar.txt");
        files.add("foo1.txt");
        files.add("foo2.txt");
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(src);
            ZipUtil.iterate(inputStream, new String[]{ "foo.txt", "foo1.txt", "foo2.txt" }, new ZipEntryCallback() {
                public void process(InputStream in, ZipEntry zipEntry) throws IOException {
                    files.remove(zipEntry.getName());
                }
            });
            TestCase.assertEquals(1, files.size());
            TestCase.assertTrue("Wrong entry hasn't been iterated", files.contains("bar.txt"));
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    public void testIterateAndBreak() {
        File src = ZipUtilTest.file("demo.zip");
        final Set files = new HashSet();
        files.add("foo.txt");
        files.add("bar.txt");
        files.add("foo1.txt");
        files.add("foo2.txt");
        ZipUtil.iterate(src, new ZipEntryCallback() {
            public void process(InputStream in, ZipEntry zipEntry) throws IOException {
                files.remove(zipEntry.getName());
                throw new ZipBreakException();
            }
        });
        TestCase.assertEquals(3, files.size());
    }

    public void testUnwrapFile() throws Exception {
        File dest = File.createTempFile("temp", null);
        File destDir = File.createTempFile("tempDir", null);
        try {
            destDir.delete();
            destDir.mkdir();
            String child = "TestFile.txt";
            File parent = ZipUtilTest.file(child).getParentFile();
            ZipUtil.pack(parent, dest, true);
            ZipUtil.unwrap(dest, destDir);
            TestCase.assertTrue(new File(destDir, child).exists());
        } finally {
            FileUtils.forceDelete(destDir);
        }
    }

    public void testUnwrapStream() throws Exception {
        File dest = File.createTempFile("temp", null);
        File destDir = File.createTempFile("tempDir", null);
        InputStream is = null;
        try {
            destDir.delete();
            destDir.mkdir();
            String child = "TestFile.txt";
            File parent = ZipUtilTest.file(child).getParentFile();
            ZipUtil.pack(parent, dest, true);
            is = new FileInputStream(dest);
            ZipUtil.unwrap(is, destDir);
            TestCase.assertTrue(new File(destDir, child).exists());
        } finally {
            IOUtils.closeQuietly(is);
            FileUtils.forceDelete(destDir);
        }
    }

    public void testUnwrapEntriesInRoot() throws Exception {
        File src = ZipUtilTest.file("demo.zip");
        File destDir = File.createTempFile("tempDir", null);
        try {
            destDir.delete();
            destDir.mkdir();
            ZipUtil.unwrap(src, destDir);
            TestCase.fail("expected a ZipException, unwrapping with multiple roots is not supported");
        } catch (ZipException e) {
            // this is normal outcome
        } finally {
            FileUtils.forceDelete(destDir);
        }
    }

    public void testUnwrapMultipleRoots() throws Exception {
        File src = ZipUtilTest.file("demo-dirs-only.zip");
        File destDir = File.createTempFile("tempDir", null);
        try {
            destDir.delete();
            destDir.mkdir();
            ZipUtil.unwrap(src, destDir);
            TestCase.fail("expected a ZipException, unwrapping with multiple roots is not supported");
        } catch (ZipException e) {
            // this is normal outcome
        } finally {
            FileUtils.forceDelete(destDir);
        }
    }

    public void testUnwrapSingleRootWithStructure() throws Exception {
        File src = ZipUtilTest.file("demo-single-root-dir.zip");
        File destDir = File.createTempFile("tempDir", null);
        try {
            destDir.delete();
            destDir.mkdir();
            ZipUtil.unwrap(src, destDir);
            TestCase.assertTrue(new File(destDir, "b.txt").exists());
            TestCase.assertTrue(new File(destDir, "bad.txt").exists());
            TestCase.assertTrue(new File(destDir, "b").exists());
            TestCase.assertTrue(new File(new File(destDir, "b"), "c.txt").exists());
        } finally {
            FileUtils.forceDelete(destDir);
        }
    }

    public void testUnwrapEmptyRootDir() throws Exception {
        File src = ZipUtilTest.file("demo-single-empty-root-dir.zip");
        File destDir = File.createTempFile("tempDir", null);
        try {
            destDir.delete();
            destDir.mkdir();
            ZipUtil.unwrap(src, destDir);
            TestCase.assertTrue("Dest dir should be empty, root dir was shaved", ((destDir.list().length) == 0));
        } finally {
            FileUtils.forceDelete(destDir);
        }
    }

    public void testUnpackEntryDir() throws Exception {
        File src = ZipUtilTest.file("demo-dirs.zip");
        File dest = File.createTempFile("unpackEntryDir", null);
        try {
            ZipUtil.unpackEntry(src, "a", dest);
            TestCase.assertTrue("Couldn't unpackEntry of a directory entry from a zip!", dest.exists());
            TestCase.assertTrue("UnpackedEntry of a directory is not a dir!", dest.isDirectory());
        } finally {
            FileUtils.forceDelete(dest);
        }
    }

    public void testAddEntryWithCompressionMethodAndDestFile() throws IOException {
        int compressionMethod = ZipEntry.STORED;
        doTestAddEntryWithCompressionMethodAndDestFile(compressionMethod);
        compressionMethod = ZipEntry.DEFLATED;
        doTestAddEntryWithCompressionMethodAndDestFile(compressionMethod);
    }

    public void testAddEntryWithCompressionMethodStoredInPlace() throws IOException {
        int compressionMethod = ZipEntry.STORED;
        File src = ZipUtilTest.file("demo.zip");
        File srcCopy = File.createTempFile("ztr", ".zip");
        FileUtils.copyFile(src, srcCopy);
        doTestAddEntryWithCompressionMethodInPlace(srcCopy, compressionMethod);
        FileUtils.forceDelete(srcCopy);
    }

    public void testAddEntryWithCompressionMethodDeflatedInPlace() throws IOException {
        int compressionMethod = ZipEntry.DEFLATED;
        File src = ZipUtilTest.file("demo.zip");
        File srcCopy = File.createTempFile("ztr", ".zip");
        FileUtils.copyFile(src, srcCopy);
        doTestAddEntryWithCompressionMethodInPlace(srcCopy, compressionMethod);
        FileUtils.forceDelete(srcCopy);
    }

    public void testReplaceEntryWithCompressionMethod() throws IOException {
        File initialSrc = ZipUtilTest.file("demo.zip");
        File src = File.createTempFile("ztr", ".zip");
        FileUtils.copyFile(initialSrc, src);
        final String fileName = "foo.txt";
        TestCase.assertTrue(ZipUtil.containsEntry(src, fileName));
        TestCase.assertEquals(ZipEntry.STORED, ZipUtil.getCompressionMethodOfEntry(src, fileName));
        byte[] content = "testReplaceEntryWithCompressionMethod".getBytes("UTF-8");
        ZipUtil.replaceEntry(src, fileName, content, ZipEntry.DEFLATED);
        TestCase.assertEquals(ZipEntry.DEFLATED, ZipUtil.getCompressionMethodOfEntry(src, fileName));
        FileUtils.forceDelete(src);
    }

    public void testUnpackBackslashes() throws IOException {
        File initialSrc = ZipUtilTest.file("backSlashTest.zip");
        // lets create a temporary file and then use it as a dir
        File dest = File.createTempFile("unpackEntryDir", null);
        if (!(dest.delete())) {
            throw new IOException(("Could not delete temp file: " + (dest.getAbsolutePath())));
        }
        if (!(dest.mkdir())) {
            throw new IOException(("Could not create temp directory: " + (dest.getAbsolutePath())));
        }
        // unpack the archive that has the backslashes
        // and double check that the file structure is preserved
        ZipUtil.iterate(initialSrc, new ZipUtil.BackslashUnpacker(dest));
        File parentDir = new File(dest, "testDirectory");
        TestCase.assertTrue("Sub directory 'testDirectory' wasn't created", parentDir.isDirectory());
        File file = new File(parentDir, "testfileInTestDirectory.txt");
        TestCase.assertTrue("Can't find file 'testfileInTestDirectory.txt' in testDirectory", file.isFile());
        file = new File(parentDir, "testSubdirectory");
        TestCase.assertTrue("The sub sub directory 'testSubdirectory' isn't a directory", file.isDirectory());
        file = new File(file, "testFileInTestSubdirectory.txt");
        TestCase.assertTrue("The 'testFileInTestSubdirectory.txt' is not a file", file.isFile());
    }

    public void testCreateEmptyFile() throws Exception {
        File src = File.createTempFile("ztr", ".zip");
        ZipUtil.createEmpty(src);
        File dest = File.createTempFile("unpackEntryDir", null);
        if (!(dest.delete())) {
            throw new IOException(("Could not delete temp file: " + (dest.getAbsolutePath())));
        }
        ZipUtil.unpack(src, dest);
        FileUtils.forceDelete(src);
        // As the ZIP file is empty then the unpack doesn't create the directory.
        // So this is why I don't have a forceDelete for the dest variable - FileUtils.forceDelete(dest);
    }
}

