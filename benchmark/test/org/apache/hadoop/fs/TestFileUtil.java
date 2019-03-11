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
package org.apache.hadoop.fs;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.test.PlatformAssumptions;
import org.apache.hadoop.util.StringUtils;
import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarOutputStream;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static Path.SEPARATOR;
import static java.util.jar.Attributes.Name.CLASS_PATH;


public class TestFileUtil {
    private static final Logger LOG = LoggerFactory.getLogger(TestFileUtil.class);

    private static final File TEST_DIR = GenericTestUtils.getTestDir("fu");

    private static final String FILE = "x";

    private static final String LINK = "y";

    private static final String DIR = "dir";

    private final File del = new File(TestFileUtil.TEST_DIR, "del");

    private final File tmp = new File(TestFileUtil.TEST_DIR, "tmp");

    private final File dir1 = new File(del, ((TestFileUtil.DIR) + "1"));

    private final File dir2 = new File(del, ((TestFileUtil.DIR) + "2"));

    private final File partitioned = new File(TestFileUtil.TEST_DIR, "partitioned");

    private InetAddress inet1;

    private InetAddress inet2;

    private InetAddress inet3;

    private InetAddress inet4;

    private InetAddress inet5;

    private InetAddress inet6;

    private URI uri1;

    private URI uri2;

    private URI uri3;

    private URI uri4;

    private URI uri5;

    private URI uri6;

    private FileSystem fs1;

    private FileSystem fs2;

    private FileSystem fs3;

    private FileSystem fs4;

    private FileSystem fs5;

    private FileSystem fs6;

    @Test(timeout = 30000)
    public void testListFiles() throws IOException {
        setupDirs();
        // Test existing files case
        File[] files = FileUtil.listFiles(partitioned);
        Assert.assertEquals(2, files.length);
        // Test existing directory with no files case
        File newDir = new File(tmp.getPath(), "test");
        newDir.mkdir();
        Assert.assertTrue("Failed to create test dir", newDir.exists());
        files = FileUtil.listFiles(newDir);
        Assert.assertEquals(0, files.length);
        newDir.delete();
        Assert.assertFalse("Failed to delete test dir", newDir.exists());
        // Test non-existing directory case, this throws
        // IOException
        try {
            files = FileUtil.listFiles(newDir);
            Assert.fail(("IOException expected on listFiles() for non-existent dir " + (newDir.toString())));
        } catch (IOException ioe) {
            // Expected an IOException
        }
    }

    @Test(timeout = 30000)
    public void testListAPI() throws IOException {
        setupDirs();
        // Test existing files case
        String[] files = FileUtil.list(partitioned);
        Assert.assertEquals("Unexpected number of pre-existing files", 2, files.length);
        // Test existing directory with no files case
        File newDir = new File(tmp.getPath(), "test");
        newDir.mkdir();
        Assert.assertTrue("Failed to create test dir", newDir.exists());
        files = FileUtil.list(newDir);
        Assert.assertEquals("New directory unexpectedly contains files", 0, files.length);
        newDir.delete();
        Assert.assertFalse("Failed to delete test dir", newDir.exists());
        // Test non-existing directory case, this throws
        // IOException
        try {
            files = FileUtil.list(newDir);
            Assert.fail(("IOException expected on list() for non-existent dir " + (newDir.toString())));
        } catch (IOException ioe) {
            // Expected an IOException
        }
    }

    @Test(timeout = 30000)
    public void testFullyDelete() throws IOException {
        setupDirs();
        boolean ret = FileUtil.fullyDelete(del);
        Assert.assertTrue(ret);
        Assert.assertFalse(del.exists());
        validateTmpDir();
    }

    /**
     * Tests if fullyDelete deletes
     * (a) symlink to file only and not the file pointed to by symlink.
     * (b) symlink to dir only and not the dir pointed to by symlink.
     *
     * @throws IOException
     * 		
     */
    @Test(timeout = 30000)
    public void testFullyDeleteSymlinks() throws IOException {
        setupDirs();
        File link = new File(del, TestFileUtil.LINK);
        Assert.assertEquals(5, del.list().length);
        // Since tmpDir is symlink to tmp, fullyDelete(tmpDir) should not
        // delete contents of tmp. See setupDirs for details.
        boolean ret = FileUtil.fullyDelete(link);
        Assert.assertTrue(ret);
        Assert.assertFalse(link.exists());
        Assert.assertEquals(4, del.list().length);
        validateTmpDir();
        File linkDir = new File(del, "tmpDir");
        // Since tmpDir is symlink to tmp, fullyDelete(tmpDir) should not
        // delete contents of tmp. See setupDirs for details.
        ret = FileUtil.fullyDelete(linkDir);
        Assert.assertTrue(ret);
        Assert.assertFalse(linkDir.exists());
        Assert.assertEquals(3, del.list().length);
        validateTmpDir();
    }

    /**
     * Tests if fullyDelete deletes
     * (a) dangling symlink to file properly
     * (b) dangling symlink to directory properly
     *
     * @throws IOException
     * 		
     */
    @Test(timeout = 30000)
    public void testFullyDeleteDanglingSymlinks() throws IOException {
        setupDirs();
        // delete the directory tmp to make tmpDir a dangling link to dir tmp and
        // to make y as a dangling link to file tmp/x
        boolean ret = FileUtil.fullyDelete(tmp);
        Assert.assertTrue(ret);
        Assert.assertFalse(tmp.exists());
        // dangling symlink to file
        File link = new File(del, TestFileUtil.LINK);
        Assert.assertEquals(5, del.list().length);
        // Even though 'y' is dangling symlink to file tmp/x, fullyDelete(y)
        // should delete 'y' properly.
        ret = FileUtil.fullyDelete(link);
        Assert.assertTrue(ret);
        Assert.assertEquals(4, del.list().length);
        // dangling symlink to directory
        File linkDir = new File(del, "tmpDir");
        // Even though tmpDir is dangling symlink to tmp, fullyDelete(tmpDir) should
        // delete tmpDir properly.
        ret = FileUtil.fullyDelete(linkDir);
        Assert.assertTrue(ret);
        Assert.assertEquals(3, del.list().length);
    }

    @Test(timeout = 30000)
    public void testFullyDeleteContents() throws IOException {
        setupDirs();
        boolean ret = FileUtil.fullyDeleteContents(del);
        Assert.assertTrue(ret);
        Assert.assertTrue(del.exists());
        Assert.assertEquals(0, del.listFiles().length);
        validateTmpDir();
    }

    private final File xSubDir = new File(del, "xSubDir");

    private final File xSubSubDir = new File(xSubDir, "xSubSubDir");

    private final File ySubDir = new File(del, "ySubDir");

    private static final String file1Name = "file1";

    private final File file2 = new File(xSubDir, "file2");

    private final File file22 = new File(xSubSubDir, "file22");

    private final File file3 = new File(ySubDir, "file3");

    private final File zlink = new File(del, "zlink");

    @Test(timeout = 30000)
    public void testFailFullyDelete() throws IOException {
        // Windows Dir.setWritable(false) does not work for directories
        PlatformAssumptions.assumeNotWindows();
        TestFileUtil.LOG.info("Running test to verify failure of fullyDelete()");
        setupDirsAndNonWritablePermissions();
        boolean ret = FileUtil.fullyDelete(new TestFileUtil.MyFile(del));
        validateAndSetWritablePermissions(true, ret);
    }

    @Test(timeout = 30000)
    public void testFailFullyDeleteGrantPermissions() throws IOException {
        setupDirsAndNonWritablePermissions();
        boolean ret = FileUtil.fullyDelete(new TestFileUtil.MyFile(del), true);
        // this time the directories with revoked permissions *should* be deleted:
        validateAndSetWritablePermissions(false, ret);
    }

    /**
     * Extend {@link File}. Same as {@link File} except for two things: (1) This
     * treats file1Name as a very special file which is not delete-able
     * irrespective of it's parent-dir's permissions, a peculiar file instance for
     * testing. (2) It returns the files in alphabetically sorted order when
     * listed.
     */
    public static class MyFile extends File {
        private static final long serialVersionUID = 1L;

        public MyFile(File f) {
            super(f.getAbsolutePath());
        }

        public MyFile(File parent, String child) {
            super(parent, child);
        }

        /**
         * Same as {@link File#delete()} except for file1Name which will never be
         * deleted (hard-coded)
         */
        @Override
        public boolean delete() {
            TestFileUtil.LOG.info(("Trying to delete myFile " + (getAbsolutePath())));
            boolean bool = false;
            if (getName().equals(TestFileUtil.file1Name)) {
                bool = false;
            } else {
                bool = super.delete();
            }
            if (bool) {
                TestFileUtil.LOG.info((("Deleted " + (getAbsolutePath())) + " successfully"));
            } else {
                TestFileUtil.LOG.info(("Cannot delete " + (getAbsolutePath())));
            }
            return bool;
        }

        /**
         * Return the list of files in an alphabetically sorted order
         */
        @Override
        public File[] listFiles() {
            final File[] files = super.listFiles();
            if (files == null) {
                return null;
            }
            List<File> filesList = Arrays.asList(files);
            Collections.sort(filesList);
            File[] myFiles = new TestFileUtil.MyFile[files.length];
            int i = 0;
            for (File f : filesList) {
                myFiles[(i++)] = new TestFileUtil.MyFile(f);
            }
            return myFiles;
        }
    }

    @Test(timeout = 30000)
    public void testFailFullyDeleteContents() throws IOException {
        // Windows Dir.setWritable(false) does not work for directories
        PlatformAssumptions.assumeNotWindows();
        TestFileUtil.LOG.info("Running test to verify failure of fullyDeleteContents()");
        setupDirsAndNonWritablePermissions();
        boolean ret = FileUtil.fullyDeleteContents(new TestFileUtil.MyFile(del));
        validateAndSetWritablePermissions(true, ret);
    }

    @Test(timeout = 30000)
    public void testFailFullyDeleteContentsGrantPermissions() throws IOException {
        setupDirsAndNonWritablePermissions();
        boolean ret = FileUtil.fullyDeleteContents(new TestFileUtil.MyFile(del), true);
        // this time the directories with revoked permissions *should* be deleted:
        validateAndSetWritablePermissions(false, ret);
    }

    /**
     * Test that getDU is able to handle cycles caused due to symbolic links
     * and that directory sizes are not added to the final calculated size
     *
     * @throws IOException
     * 		
     */
    @Test(timeout = 30000)
    public void testGetDU() throws Exception {
        setupDirs();
        long du = FileUtil.getDU(TestFileUtil.TEST_DIR);
        // Only two files (in partitioned).  Each has 3 characters + system-specific
        // line separator.
        final long expected = 2 * (3 + (System.getProperty("line.separator").length()));
        Assert.assertEquals(expected, du);
        // target file does not exist:
        final File doesNotExist = new File(tmp, "QuickBrownFoxJumpsOverTheLazyDog");
        long duDoesNotExist = FileUtil.getDU(doesNotExist);
        Assert.assertEquals(0, duDoesNotExist);
        // target file is not a directory:
        File notADirectory = new File(partitioned, "part-r-00000");
        long duNotADirectoryActual = FileUtil.getDU(notADirectory);
        long duNotADirectoryExpected = 3 + (System.getProperty("line.separator").length());
        Assert.assertEquals(duNotADirectoryExpected, duNotADirectoryActual);
        try {
            // one of target files is not accessible, but the containing directory
            // is accessible:
            try {
                FileUtil.chmod(notADirectory.getAbsolutePath(), "0000");
            } catch (InterruptedException ie) {
                // should never happen since that method never throws InterruptedException.
                Assert.assertNull(ie);
            }
            Assert.assertFalse(FileUtil.canRead(notADirectory));
            final long du3 = FileUtil.getDU(partitioned);
            Assert.assertEquals(expected, du3);
            // some target files and containing directory are not accessible:
            try {
                FileUtil.chmod(partitioned.getAbsolutePath(), "0000");
            } catch (InterruptedException ie) {
                // should never happen since that method never throws InterruptedException.
                Assert.assertNull(ie);
            }
            Assert.assertFalse(FileUtil.canRead(partitioned));
            final long du4 = FileUtil.getDU(partitioned);
            Assert.assertEquals(0, du4);
        } finally {
            // Restore the permissions so that we can delete the folder
            // in @After method:
            /* recursive */
            FileUtil.chmod(partitioned.getAbsolutePath(), "0777", true);
        }
    }

    @Test(timeout = 30000)
    public void testUnTar() throws IOException {
        setupDirs();
        // make a simple tar:
        final File simpleTar = new File(del, TestFileUtil.FILE);
        OutputStream os = new FileOutputStream(simpleTar);
        TarOutputStream tos = new TarOutputStream(os);
        try {
            TarEntry te = new TarEntry("/bar/foo");
            byte[] data = "some-content".getBytes("UTF-8");
            te.setSize(data.length);
            tos.putNextEntry(te);
            tos.write(data);
            tos.closeEntry();
            tos.flush();
            tos.finish();
        } finally {
            tos.close();
        }
        // successfully untar it into an existing dir:
        FileUtil.unTar(simpleTar, tmp);
        // check result:
        Assert.assertTrue(new File(tmp, "/bar/foo").exists());
        Assert.assertEquals(12, new File(tmp, "/bar/foo").length());
        final File regularFile = new File(tmp, "QuickBrownFoxJumpsOverTheLazyDog");
        regularFile.createNewFile();
        Assert.assertTrue(regularFile.exists());
        try {
            FileUtil.unTar(simpleTar, regularFile);
            Assert.assertTrue("An IOException expected.", false);
        } catch (IOException ioe) {
            // okay
        }
    }

    @Test(timeout = 30000)
    public void testReplaceFile() throws IOException {
        setupDirs();
        final File srcFile = new File(tmp, "src");
        // src exists, and target does not exist:
        srcFile.createNewFile();
        Assert.assertTrue(srcFile.exists());
        final File targetFile = new File(tmp, "target");
        Assert.assertTrue((!(targetFile.exists())));
        FileUtil.replaceFile(srcFile, targetFile);
        Assert.assertTrue((!(srcFile.exists())));
        Assert.assertTrue(targetFile.exists());
        // src exists and target is a regular file:
        srcFile.createNewFile();
        Assert.assertTrue(srcFile.exists());
        FileUtil.replaceFile(srcFile, targetFile);
        Assert.assertTrue((!(srcFile.exists())));
        Assert.assertTrue(targetFile.exists());
        // src exists, and target is a non-empty directory:
        srcFile.createNewFile();
        Assert.assertTrue(srcFile.exists());
        targetFile.delete();
        targetFile.mkdirs();
        File obstacle = new File(targetFile, "obstacle");
        obstacle.createNewFile();
        Assert.assertTrue(obstacle.exists());
        Assert.assertTrue(((targetFile.exists()) && (targetFile.isDirectory())));
        try {
            FileUtil.replaceFile(srcFile, targetFile);
            Assert.assertTrue(false);
        } catch (IOException ioe) {
            // okay
        }
        // check up the post-condition: nothing is deleted:
        Assert.assertTrue(srcFile.exists());
        Assert.assertTrue(((targetFile.exists()) && (targetFile.isDirectory())));
        Assert.assertTrue(obstacle.exists());
    }

    @Test(timeout = 30000)
    public void testCreateLocalTempFile() throws IOException {
        setupDirs();
        final File baseFile = new File(tmp, "base");
        File tmp1 = FileUtil.createLocalTempFile(baseFile, "foo", false);
        File tmp2 = FileUtil.createLocalTempFile(baseFile, "foo", true);
        Assert.assertFalse(tmp1.getAbsolutePath().equals(baseFile.getAbsolutePath()));
        Assert.assertFalse(tmp2.getAbsolutePath().equals(baseFile.getAbsolutePath()));
        Assert.assertTrue(((tmp1.exists()) && (tmp2.exists())));
        Assert.assertTrue(((tmp1.canWrite()) && (tmp2.canWrite())));
        Assert.assertTrue(((tmp1.canRead()) && (tmp2.canRead())));
        tmp1.delete();
        tmp2.delete();
        Assert.assertTrue(((!(tmp1.exists())) && (!(tmp2.exists()))));
    }

    @Test(timeout = 30000)
    public void testUnZip() throws IOException {
        setupDirs();
        // make a simple zip
        final File simpleZip = new File(del, TestFileUtil.FILE);
        OutputStream os = new FileOutputStream(simpleZip);
        ZipOutputStream tos = new ZipOutputStream(os);
        try {
            ZipEntry ze = new ZipEntry("foo");
            byte[] data = "some-content".getBytes("UTF-8");
            ze.setSize(data.length);
            tos.putNextEntry(ze);
            tos.write(data);
            tos.closeEntry();
            tos.flush();
            tos.finish();
        } finally {
            tos.close();
        }
        // successfully unzip it into an existing dir:
        FileUtil.unZip(simpleZip, tmp);
        // check result:
        Assert.assertTrue(new File(tmp, "foo").exists());
        Assert.assertEquals(12, new File(tmp, "foo").length());
        final File regularFile = new File(tmp, "QuickBrownFoxJumpsOverTheLazyDog");
        regularFile.createNewFile();
        Assert.assertTrue(regularFile.exists());
        try {
            FileUtil.unZip(simpleZip, regularFile);
            Assert.assertTrue("An IOException expected.", false);
        } catch (IOException ioe) {
            // okay
        }
    }

    @Test(timeout = 30000)
    public void testUnZip2() throws IOException {
        setupDirs();
        // make a simple zip
        final File simpleZip = new File(del, TestFileUtil.FILE);
        OutputStream os = new FileOutputStream(simpleZip);
        try (ZipOutputStream tos = new ZipOutputStream(os)) {
            // Add an entry that contains invalid filename
            ZipEntry ze = new ZipEntry("../foo");
            byte[] data = "some-content".getBytes(StandardCharsets.UTF_8);
            ze.setSize(data.length);
            tos.putNextEntry(ze);
            tos.write(data);
            tos.closeEntry();
            tos.flush();
            tos.finish();
        }
        // Unzip it into an existing dir
        try {
            FileUtil.unZip(simpleZip, tmp);
            Assert.fail("unZip should throw IOException.");
        } catch (IOException e) {
            GenericTestUtils.assertExceptionContains("would create file outside of", e);
        }
    }

    /* Test method copy(FileSystem srcFS, Path src, File dst, boolean deleteSource, Configuration conf) */
    @Test(timeout = 30000)
    public void testCopy5() throws IOException {
        setupDirs();
        URI uri = tmp.toURI();
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.newInstance(uri, conf);
        final String content = "some-content";
        File srcFile = createFile(tmp, "src", content);
        Path srcPath = new Path(srcFile.toURI());
        // copy regular file:
        final File dest = new File(del, "dest");
        boolean result = FileUtil.copy(fs, srcPath, dest, false, conf);
        Assert.assertTrue(result);
        Assert.assertTrue(dest.exists());
        Assert.assertEquals(((content.getBytes().length) + (System.getProperty("line.separator").getBytes().length)), dest.length());
        Assert.assertTrue(srcFile.exists());// should not be deleted

        // copy regular file, delete src:
        dest.delete();
        Assert.assertTrue((!(dest.exists())));
        result = FileUtil.copy(fs, srcPath, dest, true, conf);
        Assert.assertTrue(result);
        Assert.assertTrue(dest.exists());
        Assert.assertEquals(((content.getBytes().length) + (System.getProperty("line.separator").getBytes().length)), dest.length());
        Assert.assertTrue((!(srcFile.exists())));// should be deleted

        // copy a dir:
        dest.delete();
        Assert.assertTrue((!(dest.exists())));
        srcPath = new Path(partitioned.toURI());
        result = FileUtil.copy(fs, srcPath, dest, true, conf);
        Assert.assertTrue(result);
        Assert.assertTrue(((dest.exists()) && (dest.isDirectory())));
        File[] files = dest.listFiles();
        Assert.assertTrue((files != null));
        Assert.assertEquals(2, files.length);
        for (File f : files) {
            Assert.assertEquals((3 + (System.getProperty("line.separator").getBytes().length)), f.length());
        }
        Assert.assertTrue((!(partitioned.exists())));// should be deleted

    }

    @Test(timeout = 30000)
    public void testStat2Paths1() {
        Assert.assertNull(FileUtil.stat2Paths(null));
        FileStatus[] fileStatuses = new FileStatus[0];
        Path[] paths = FileUtil.stat2Paths(fileStatuses);
        Assert.assertEquals(0, paths.length);
        Path path1 = new Path("file://foo");
        Path path2 = new Path("file://moo");
        fileStatuses = new FileStatus[]{ new FileStatus(3, false, 0, 0, 0, path1), new FileStatus(3, false, 0, 0, 0, path2) };
        paths = FileUtil.stat2Paths(fileStatuses);
        Assert.assertEquals(2, paths.length);
        Assert.assertEquals(paths[0], path1);
        Assert.assertEquals(paths[1], path2);
    }

    @Test(timeout = 30000)
    public void testStat2Paths2() {
        Path defaultPath = new Path("file://default");
        Path[] paths = FileUtil.stat2Paths(null, defaultPath);
        Assert.assertEquals(1, paths.length);
        Assert.assertEquals(defaultPath, paths[0]);
        paths = FileUtil.stat2Paths(null, null);
        Assert.assertTrue((paths != null));
        Assert.assertEquals(1, paths.length);
        Assert.assertEquals(null, paths[0]);
        Path path1 = new Path("file://foo");
        Path path2 = new Path("file://moo");
        FileStatus[] fileStatuses = new FileStatus[]{ new FileStatus(3, false, 0, 0, 0, path1), new FileStatus(3, false, 0, 0, 0, path2) };
        paths = FileUtil.stat2Paths(fileStatuses, defaultPath);
        Assert.assertEquals(2, paths.length);
        Assert.assertEquals(paths[0], path1);
        Assert.assertEquals(paths[1], path2);
    }

    @Test(timeout = 30000)
    public void testSymlink() throws Exception {
        Assert.assertFalse(del.exists());
        del.mkdirs();
        byte[] data = "testSymLink".getBytes();
        File file = new File(del, TestFileUtil.FILE);
        File link = new File(del, "_link");
        // write some data to the file
        FileOutputStream os = new FileOutputStream(file);
        os.write(data);
        os.close();
        // create the symlink
        FileUtil.symLink(file.getAbsolutePath(), link.getAbsolutePath());
        // ensure that symlink length is correctly reported by Java
        Assert.assertEquals(data.length, file.length());
        Assert.assertEquals(data.length, link.length());
        // ensure that we can read from link.
        FileInputStream in = new FileInputStream(link);
        long len = 0;
        while ((in.read()) > 0) {
            len++;
        } 
        in.close();
        Assert.assertEquals(data.length, len);
    }

    /**
     * Test that rename on a symlink works as expected.
     */
    @Test(timeout = 30000)
    public void testSymlinkRenameTo() throws Exception {
        Assert.assertFalse(del.exists());
        del.mkdirs();
        File file = new File(del, TestFileUtil.FILE);
        file.createNewFile();
        File link = new File(del, "_link");
        // create the symlink
        FileUtil.symLink(file.getAbsolutePath(), link.getAbsolutePath());
        Assert.assertTrue(file.exists());
        Assert.assertTrue(link.exists());
        File link2 = new File(del, "_link2");
        // Rename the symlink
        Assert.assertTrue(link.renameTo(link2));
        // Make sure the file still exists
        // (NOTE: this would fail on Java6 on Windows if we didn't
        // copy the file in FileUtil#symlink)
        Assert.assertTrue(file.exists());
        Assert.assertTrue(link2.exists());
        Assert.assertFalse(link.exists());
    }

    /**
     * Test that deletion of a symlink works as expected.
     */
    @Test(timeout = 30000)
    public void testSymlinkDelete() throws Exception {
        Assert.assertFalse(del.exists());
        del.mkdirs();
        File file = new File(del, TestFileUtil.FILE);
        file.createNewFile();
        File link = new File(del, "_link");
        // create the symlink
        FileUtil.symLink(file.getAbsolutePath(), link.getAbsolutePath());
        Assert.assertTrue(file.exists());
        Assert.assertTrue(link.exists());
        // make sure that deleting a symlink works properly
        Assert.assertTrue(link.delete());
        Assert.assertFalse(link.exists());
        Assert.assertTrue(file.exists());
    }

    /**
     * Test that length on a symlink works as expected.
     */
    @Test(timeout = 30000)
    public void testSymlinkLength() throws Exception {
        Assert.assertFalse(del.exists());
        del.mkdirs();
        byte[] data = "testSymLinkData".getBytes();
        File file = new File(del, TestFileUtil.FILE);
        File link = new File(del, "_link");
        // write some data to the file
        FileOutputStream os = new FileOutputStream(file);
        os.write(data);
        os.close();
        Assert.assertEquals(0, link.length());
        // create the symlink
        FileUtil.symLink(file.getAbsolutePath(), link.getAbsolutePath());
        // ensure that File#length returns the target file and link size
        Assert.assertEquals(data.length, file.length());
        Assert.assertEquals(data.length, link.length());
        file.delete();
        Assert.assertFalse(file.exists());
        Assert.assertEquals(0, link.length());
        link.delete();
        Assert.assertFalse(link.exists());
    }

    /**
     * This test validates the correctness of
     * {@link FileUtil#symLink(String, String)} in case of null pointer inputs.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testSymlinkWithNullInput() throws IOException {
        Assert.assertFalse(del.exists());
        del.mkdirs();
        File file = new File(del, TestFileUtil.FILE);
        File link = new File(del, "_link");
        // Create the same symbolic link
        // The operation should fail and returns 1
        int result = FileUtil.symLink(null, null);
        Assert.assertEquals(1, result);
        // Create the same symbolic link
        // The operation should fail and returns 1
        result = FileUtil.symLink(file.getAbsolutePath(), null);
        Assert.assertEquals(1, result);
        // Create the same symbolic link
        // The operation should fail and returns 1
        result = FileUtil.symLink(null, link.getAbsolutePath());
        Assert.assertEquals(1, result);
        file.delete();
        link.delete();
    }

    /**
     * This test validates the correctness of
     * {@link FileUtil#symLink(String, String)} in case the file already exists.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testSymlinkFileAlreadyExists() throws IOException {
        Assert.assertFalse(del.exists());
        del.mkdirs();
        File file = new File(del, TestFileUtil.FILE);
        File link = new File(del, "_link");
        // Create a symbolic link
        // The operation should succeed
        int result1 = FileUtil.symLink(file.getAbsolutePath(), link.getAbsolutePath());
        Assert.assertEquals(0, result1);
        // Create the same symbolic link
        // The operation should fail and returns 1
        result1 = FileUtil.symLink(file.getAbsolutePath(), link.getAbsolutePath());
        Assert.assertEquals(1, result1);
        file.delete();
        link.delete();
    }

    /**
     * This test validates the correctness of
     * {@link FileUtil#symLink(String, String)} in case the file and the link are
     * the same file.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testSymlinkSameFile() throws IOException {
        Assert.assertFalse(del.exists());
        del.mkdirs();
        File file = new File(del, TestFileUtil.FILE);
        // Create a symbolic link
        // The operation should succeed
        int result = FileUtil.symLink(file.getAbsolutePath(), file.getAbsolutePath());
        Assert.assertEquals(0, result);
        file.delete();
    }

    /**
     * This test validates the correctness of
     * {@link FileUtil#symLink(String, String)} in case we want to use a link for
     * 2 different files.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testSymlink2DifferentFile() throws IOException {
        Assert.assertFalse(del.exists());
        del.mkdirs();
        File file = new File(del, TestFileUtil.FILE);
        File fileSecond = new File(del, ((TestFileUtil.FILE) + "_1"));
        File link = new File(del, "_link");
        // Create a symbolic link
        // The operation should succeed
        int result = FileUtil.symLink(file.getAbsolutePath(), link.getAbsolutePath());
        Assert.assertEquals(0, result);
        // The operation should fail and returns 1
        result = FileUtil.symLink(fileSecond.getAbsolutePath(), link.getAbsolutePath());
        Assert.assertEquals(1, result);
        file.delete();
        fileSecond.delete();
        link.delete();
    }

    /**
     * This test validates the correctness of
     * {@link FileUtil#symLink(String, String)} in case we want to use a 2
     * different links for the same file.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testSymlink2DifferentLinks() throws IOException {
        Assert.assertFalse(del.exists());
        del.mkdirs();
        File file = new File(del, TestFileUtil.FILE);
        File link = new File(del, "_link");
        File linkSecond = new File(del, "_link_1");
        // Create a symbolic link
        // The operation should succeed
        int result = FileUtil.symLink(file.getAbsolutePath(), link.getAbsolutePath());
        Assert.assertEquals(0, result);
        // The operation should succeed
        result = FileUtil.symLink(file.getAbsolutePath(), linkSecond.getAbsolutePath());
        Assert.assertEquals(0, result);
        file.delete();
        link.delete();
        linkSecond.delete();
    }

    @Test(timeout = 30000)
    public void testUntar() throws IOException {
        String tarGzFileName = (System.getProperty("test.cache.data", "target/test/cache")) + "/test-untar.tgz";
        String tarFileName = (System.getProperty("test.cache.data", "build/test/cache")) + "/test-untar.tar";
        File dataDir = GenericTestUtils.getTestDir();
        File untarDir = new File(dataDir, "untarDir");
        doUntarAndVerify(new File(tarGzFileName), untarDir);
        doUntarAndVerify(new File(tarFileName), untarDir);
    }

    @Test(timeout = 30000)
    public void testCreateJarWithClassPath() throws Exception {
        // setup test directory for files
        Assert.assertFalse(tmp.exists());
        Assert.assertTrue(tmp.mkdirs());
        // create files expected to match a wildcard
        List<File> wildcardMatches = Arrays.asList(new File(tmp, "wildcard1.jar"), new File(tmp, "wildcard2.jar"), new File(tmp, "wildcard3.JAR"), new File(tmp, "wildcard4.JAR"));
        for (File wildcardMatch : wildcardMatches) {
            Assert.assertTrue(("failure creating file: " + wildcardMatch), wildcardMatch.createNewFile());
        }
        // create non-jar files, which we expect to not be included in the classpath
        Assert.assertTrue(new File(tmp, "text.txt").createNewFile());
        Assert.assertTrue(new File(tmp, "executable.exe").createNewFile());
        Assert.assertTrue(new File(tmp, "README").createNewFile());
        // create classpath jar
        String wildcardPath = ((tmp.getCanonicalPath()) + (File.separator)) + "*";
        String nonExistentSubdir = (((tmp.getCanonicalPath()) + (SEPARATOR)) + "subdir") + (SEPARATOR);
        List<String> classPaths = Arrays.asList("", "cp1.jar", "cp2.jar", wildcardPath, "cp3.jar", nonExistentSubdir);
        String inputClassPath = StringUtils.join(File.pathSeparator, classPaths);
        String[] jarCp = FileUtil.createJarWithClassPath(((inputClassPath + (File.pathSeparator)) + "unexpandedwildcard/*"), new Path(tmp.getCanonicalPath()), System.getenv());
        String classPathJar = jarCp[0];
        Assert.assertNotEquals("Unexpanded wildcard was not placed in extra classpath", jarCp[1].indexOf("unexpanded"), (-1));
        // verify classpath by reading manifest from jar file
        JarFile jarFile = null;
        try {
            jarFile = new JarFile(classPathJar);
            Manifest jarManifest = jarFile.getManifest();
            Assert.assertNotNull(jarManifest);
            Attributes mainAttributes = jarManifest.getMainAttributes();
            Assert.assertNotNull(mainAttributes);
            Assert.assertTrue(mainAttributes.containsKey(CLASS_PATH));
            String classPathAttr = mainAttributes.getValue(CLASS_PATH);
            Assert.assertNotNull(classPathAttr);
            List<String> expectedClassPaths = new ArrayList<String>();
            for (String classPath : classPaths) {
                if ((classPath.length()) == 0) {
                    continue;
                }
                if (wildcardPath.equals(classPath)) {
                    // add wildcard matches
                    for (File wildcardMatch : wildcardMatches) {
                        expectedClassPaths.add(wildcardMatch.toURI().toURL().toExternalForm());
                    }
                } else {
                    File fileCp = null;
                    if (!(new Path(classPath).isAbsolute())) {
                        fileCp = new File(tmp, classPath);
                    } else {
                        fileCp = new File(classPath);
                    }
                    if (nonExistentSubdir.equals(classPath)) {
                        // expect to maintain trailing path separator if present in input, even
                        // if directory doesn't exist yet
                        expectedClassPaths.add(((fileCp.toURI().toURL().toExternalForm()) + (SEPARATOR)));
                    } else {
                        expectedClassPaths.add(fileCp.toURI().toURL().toExternalForm());
                    }
                }
            }
            List<String> actualClassPaths = Arrays.asList(classPathAttr.split(" "));
            Collections.sort(expectedClassPaths);
            Collections.sort(actualClassPaths);
            Assert.assertEquals(expectedClassPaths, actualClassPaths);
        } finally {
            if (jarFile != null) {
                try {
                    jarFile.close();
                } catch (IOException e) {
                    TestFileUtil.LOG.warn(("exception closing jarFile: " + classPathJar), e);
                }
            }
        }
    }

    @Test
    public void testGetJarsInDirectory() throws Exception {
        List<Path> jars = FileUtil.getJarsInDirectory("/foo/bar/bogus/");
        Assert.assertTrue("no jars should be returned for a bogus path", jars.isEmpty());
        // setup test directory for files
        Assert.assertFalse(tmp.exists());
        Assert.assertTrue(tmp.mkdirs());
        // create jar files to be returned
        File jar1 = new File(tmp, "wildcard1.jar");
        File jar2 = new File(tmp, "wildcard2.JAR");
        List<File> matches = Arrays.asList(jar1, jar2);
        for (File match : matches) {
            Assert.assertTrue(("failure creating file: " + match), match.createNewFile());
        }
        // create non-jar files, which we expect to not be included in the result
        Assert.assertTrue(new File(tmp, "text.txt").createNewFile());
        Assert.assertTrue(new File(tmp, "executable.exe").createNewFile());
        Assert.assertTrue(new File(tmp, "README").createNewFile());
        // pass in the directory
        String directory = tmp.getCanonicalPath();
        jars = FileUtil.getJarsInDirectory(directory);
        Assert.assertEquals("there should be 2 jars", 2, jars.size());
        for (Path jar : jars) {
            URL url = jar.toUri().toURL();
            Assert.assertTrue("the jar should match either of the jars", ((url.equals(jar1.toURI().toURL())) || (url.equals(jar2.toURI().toURL()))));
        }
    }

    @Test
    public void testCompareFsNull() throws Exception {
        setupCompareFs();
        Assert.assertEquals(FileUtil.compareFs(null, fs1), false);
        Assert.assertEquals(FileUtil.compareFs(fs1, null), false);
    }

    @Test
    public void testCompareFsDirectories() throws Exception {
        setupCompareFs();
        Assert.assertEquals(FileUtil.compareFs(fs1, fs1), true);
        Assert.assertEquals(FileUtil.compareFs(fs1, fs2), false);
        Assert.assertEquals(FileUtil.compareFs(fs1, fs5), false);
        Assert.assertEquals(FileUtil.compareFs(fs3, fs4), true);
        Assert.assertEquals(FileUtil.compareFs(fs1, fs6), false);
    }

    @Test(timeout = 8000)
    public void testCreateSymbolicLinkUsingJava() throws IOException {
        setupDirs();
        final File simpleTar = new File(del, TestFileUtil.FILE);
        OutputStream os = new FileOutputStream(simpleTar);
        TarArchiveOutputStream tos = new TarArchiveOutputStream(os);
        File untarFile = null;
        try {
            // Files to tar
            final String tmpDir = "tmp/test";
            File tmpDir1 = new File(tmpDir, "dir1/");
            File tmpDir2 = new File(tmpDir, "dir2/");
            // Delete the directories if they already exist
            tmpDir1.mkdirs();
            tmpDir2.mkdirs();
            java.nio.file.Path symLink = FileSystems.getDefault().getPath(((tmpDir1.getPath()) + "/sl"));
            // Create Symbolic Link
            Files.createSymbolicLink(symLink, FileSystems.getDefault().getPath(tmpDir2.getPath())).toString();
            Assert.assertTrue(Files.isSymbolicLink(symLink.toAbsolutePath()));
            // put entries in tar file
            putEntriesInTar(tos, tmpDir1.getParentFile());
            tos.close();
            untarFile = new File(tmpDir, "2");
            // Untar using java
            FileUtil.unTarUsingJava(simpleTar, untarFile, false);
            // Check symbolic link and other directories are there in untar file
            Assert.assertTrue(Files.exists(untarFile.toPath()));
            Assert.assertTrue(Files.exists(FileSystems.getDefault().getPath(untarFile.getPath(), tmpDir)));
            Assert.assertTrue(Files.isSymbolicLink(FileSystems.getDefault().getPath(untarFile.getPath().toString(), symLink.toString())));
        } finally {
            FileUtils.deleteDirectory(new File("tmp"));
            tos.close();
        }
    }

    /**
     * This test validates the correctness of {@link FileUtil#readLink(File)} in
     * case of null pointer inputs.
     */
    @Test
    public void testReadSymlinkWithNullInput() {
        String result = FileUtil.readLink(null);
        Assert.assertEquals("", result);
    }

    /**
     * This test validates the correctness of {@link FileUtil#readLink(File)}.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testReadSymlink() throws IOException {
        Assert.assertFalse(del.exists());
        del.mkdirs();
        File file = new File(del, TestFileUtil.FILE);
        File link = new File(del, "_link");
        // Create a symbolic link
        FileUtil.symLink(file.getAbsolutePath(), link.getAbsolutePath());
        String result = FileUtil.readLink(link);
        Assert.assertEquals(file.getAbsolutePath(), result);
        file.delete();
        link.delete();
    }

    /**
     * This test validates the correctness of {@link FileUtil#readLink(File)} when
     * it gets a file in input.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testReadSymlinkWithAFileAsInput() throws IOException {
        Assert.assertFalse(del.exists());
        del.mkdirs();
        File file = new File(del, TestFileUtil.FILE);
        String result = FileUtil.readLink(file);
        Assert.assertEquals("", result);
        file.delete();
    }
}

