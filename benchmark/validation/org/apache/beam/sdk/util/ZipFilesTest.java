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
package org.apache.beam.sdk.util;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.beam.vendor.guava.v20_0.com.google.common.io.ByteSource;
import org.apache.beam.vendor.guava.v20_0.com.google.common.io.CharSource;
import org.apache.beam.vendor.guava.v20_0.com.google.common.io.Files;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for the {@link ZipFiles} class. These tests make sure that the handling of zip-files works
 * fine.
 */
@RunWith(JUnit4.class)
public class ZipFilesTest {
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    private File tmpDir;

    @Rule
    public TemporaryFolder tmpOutputFolder = new TemporaryFolder();

    private File zipFile;

    /**
     * Verify that zipping and unzipping works fine. We zip a directory having some subdirectories,
     * unzip it again and verify the structure to be in place.
     */
    @Test
    public void testZipWithSubdirectories() throws Exception {
        File zipDir = new File(tmpDir, "zip");
        File subDir1 = new File(zipDir, "subDir1");
        File subDir2 = new File(subDir1, "subdir2");
        Assert.assertTrue(subDir2.mkdirs());
        createFileWithContents(subDir2, "myTextFile.txt", "Simple Text");
        assertZipAndUnzipOfDirectoryMatchesOriginal(tmpDir);
    }

    /**
     * An empty subdirectory must have its own zip-entry.
     */
    @Test
    public void testEmptySubdirectoryHasZipEntry() throws Exception {
        File zipDir = new File(tmpDir, "zip");
        File subDirEmpty = new File(zipDir, "subDirEmpty");
        Assert.assertTrue(subDirEmpty.mkdirs());
        ZipFiles.zipDirectory(tmpDir, zipFile);
        assertZipOnlyContains("zip/subDirEmpty/");
    }

    /**
     * A directory with contents should not have a zip entry.
     */
    @Test
    public void testSubdirectoryWithContentsHasNoZipEntry() throws Exception {
        File zipDir = new File(tmpDir, "zip");
        File subDirContent = new File(zipDir, "subdirContent");
        Assert.assertTrue(subDirContent.mkdirs());
        createFileWithContents(subDirContent, "myTextFile.txt", "Simple Text");
        ZipFiles.zipDirectory(tmpDir, zipFile);
        assertZipOnlyContains("zip/subdirContent/myTextFile.txt");
    }

    @Test
    public void testZipDirectoryToOutputStream() throws Exception {
        createFileWithContents(tmpDir, "myTextFile.txt", "Simple Text");
        File[] sourceFiles = tmpDir.listFiles();
        Arrays.sort(sourceFiles);
        Assert.assertThat(sourceFiles, Matchers.not(Matchers.arrayWithSize(0)));
        try (FileOutputStream outputStream = new FileOutputStream(zipFile)) {
            ZipFiles.zipDirectory(tmpDir, outputStream);
        }
        File outputDir = Files.createTempDir();
        ZipFiles.unzipFile(zipFile, outputDir);
        File[] outputFiles = outputDir.listFiles();
        Arrays.sort(outputFiles);
        Assert.assertThat(outputFiles, Matchers.arrayWithSize(sourceFiles.length));
        for (int i = 0; i < (sourceFiles.length); i++) {
            compareFileContents(sourceFiles[i], outputFiles[i]);
        }
        ZipFilesTest.removeRecursive(outputDir.toPath());
        Assert.assertTrue(zipFile.delete());
    }

    @Test
    public void testEntries() throws Exception {
        File zipDir = new File(tmpDir, "zip");
        File subDir1 = new File(zipDir, "subDir1");
        File subDir2 = new File(subDir1, "subdir2");
        Assert.assertTrue(subDir2.mkdirs());
        createFileWithContents(subDir2, "myTextFile.txt", "Simple Text");
        ZipFiles.zipDirectory(tmpDir, zipFile);
        try (ZipFile zip = new ZipFile(zipFile)) {
            Enumeration<? extends ZipEntry> entries = zip.entries();
            for (ZipEntry entry : ZipFiles.entries(zip)) {
                Assert.assertTrue(entries.hasMoreElements());
                // ZipEntry doesn't override equals
                Assert.assertEquals(entry.getName(), entries.nextElement().getName());
            }
            Assert.assertFalse(entries.hasMoreElements());
        }
    }

    @Test
    public void testAsByteSource() throws Exception {
        File zipDir = new File(tmpDir, "zip");
        Assert.assertTrue(zipDir.mkdirs());
        createFileWithContents(zipDir, "myTextFile.txt", "Simple Text");
        ZipFiles.zipDirectory(tmpDir, zipFile);
        try (ZipFile zip = new ZipFile(zipFile)) {
            ZipEntry entry = zip.getEntry("zip/myTextFile.txt");
            ByteSource byteSource = ZipFiles.asByteSource(zip, entry);
            if ((entry.getSize()) != (-1)) {
                Assert.assertEquals(entry.getSize(), byteSource.size());
            }
            Assert.assertArrayEquals("Simple Text".getBytes(StandardCharsets.UTF_8), byteSource.read());
        }
    }

    @Test
    public void testAsCharSource() throws Exception {
        File zipDir = new File(tmpDir, "zip");
        Assert.assertTrue(zipDir.mkdirs());
        createFileWithContents(zipDir, "myTextFile.txt", "Simple Text");
        ZipFiles.zipDirectory(tmpDir, zipFile);
        try (ZipFile zip = new ZipFile(zipFile)) {
            ZipEntry entry = zip.getEntry("zip/myTextFile.txt");
            CharSource charSource = ZipFiles.asCharSource(zip, entry, StandardCharsets.UTF_8);
            Assert.assertEquals("Simple Text", charSource.read());
        }
    }

    /**
     * try to unzip to a non-existent directory and make sure that it fails.
     */
    @Test
    public void testInvalidTargetDirectory() throws IOException {
        File zipDir = new File(tmpDir, "zipdir");
        Assert.assertTrue(zipDir.mkdir());
        ZipFiles.zipDirectory(tmpDir, zipFile);
        File invalidDirectory = new File("/foo/bar");
        Assert.assertTrue((!(invalidDirectory.exists())));
        try {
            ZipFiles.unzipFile(zipFile, invalidDirectory);
            Assert.fail("We expect the IllegalArgumentException, but it never occured");
        } catch (IllegalArgumentException e) {
            // This is the expected exception - we passed the test.
        }
    }

    /**
     * Try to unzip to an existing directory, but failing to create directories.
     */
    @Test
    public void testDirectoryCreateFailed() throws IOException {
        File zipDir = new File(tmpDir, "zipdir");
        Assert.assertTrue(zipDir.mkdir());
        ZipFiles.zipDirectory(tmpDir, zipFile);
        File targetDirectory = Files.createTempDir();
        // Touch a file where the directory should be.
        Files.touch(new File(targetDirectory, "zipdir"));
        try {
            ZipFiles.unzipFile(zipFile, targetDirectory);
            Assert.fail("We expect the IOException, but it never occured");
        } catch (IOException e) {
            // This is the expected exception - we passed the test.
        }
    }
}

