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


import FSExceptionMessages.PERMISSION_DENIED;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.test.PlatformAssumptions;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestFsShellCopy {
    static final Logger LOG = LoggerFactory.getLogger(TestFsShellCopy.class);

    static Configuration conf;

    static FsShell shell;

    static LocalFileSystem lfs;

    static Path testRootDir;

    static Path srcPath;

    static Path dstPath;

    @Test
    public void testCopyNoCrc() throws Exception {
        shellRun(0, "-get", TestFsShellCopy.srcPath.toString(), TestFsShellCopy.dstPath.toString());
        checkPath(TestFsShellCopy.dstPath, false);
    }

    @Test
    public void testCopyCrc() throws Exception {
        shellRun(0, "-get", "-crc", TestFsShellCopy.srcPath.toString(), TestFsShellCopy.dstPath.toString());
        checkPath(TestFsShellCopy.dstPath, true);
    }

    @Test
    public void testCorruptedCopyCrc() throws Exception {
        FSDataOutputStream out = TestFsShellCopy.lfs.getRawFileSystem().create(TestFsShellCopy.srcPath);
        out.writeChars("bang");
        out.close();
        shellRun(1, "-get", TestFsShellCopy.srcPath.toString(), TestFsShellCopy.dstPath.toString());
    }

    @Test
    public void testCorruptedCopyIgnoreCrc() throws Exception {
        shellRun(0, "-get", "-ignoreCrc", TestFsShellCopy.srcPath.toString(), TestFsShellCopy.dstPath.toString());
        checkPath(TestFsShellCopy.dstPath, false);
    }

    @Test
    public void testCopyFileFromLocal() throws Exception {
        Path testRoot = new Path(TestFsShellCopy.testRootDir, "testPutFile");
        TestFsShellCopy.lfs.delete(testRoot, true);
        TestFsShellCopy.lfs.mkdirs(testRoot);
        Path targetDir = new Path(testRoot, "target");
        Path filePath = new Path(testRoot, new Path("srcFile"));
        TestFsShellCopy.lfs.create(filePath).close();
        checkPut(filePath, targetDir, false);
    }

    @Test
    public void testCopyDirFromLocal() throws Exception {
        Path testRoot = new Path(TestFsShellCopy.testRootDir, "testPutDir");
        TestFsShellCopy.lfs.delete(testRoot, true);
        TestFsShellCopy.lfs.mkdirs(testRoot);
        Path targetDir = new Path(testRoot, "target");
        Path dirPath = new Path(testRoot, new Path("srcDir"));
        TestFsShellCopy.lfs.mkdirs(dirPath);
        TestFsShellCopy.lfs.create(new Path(dirPath, "srcFile")).close();
        checkPut(dirPath, targetDir, false);
    }

    @Test
    public void testCopyFileFromWindowsLocalPath() throws Exception {
        PlatformAssumptions.assumeWindows();
        String windowsTestRootPath = new File(TestFsShellCopy.testRootDir.toUri().getPath().toString()).getAbsolutePath();
        Path testRoot = new Path(windowsTestRootPath, "testPutFile");
        TestFsShellCopy.lfs.delete(testRoot, true);
        TestFsShellCopy.lfs.mkdirs(testRoot);
        Path targetDir = new Path(testRoot, "target");
        Path filePath = new Path(testRoot, new Path("srcFile"));
        TestFsShellCopy.lfs.create(filePath).close();
        checkPut(filePath, targetDir, true);
    }

    @Test
    public void testCopyDirFromWindowsLocalPath() throws Exception {
        PlatformAssumptions.assumeWindows();
        String windowsTestRootPath = new File(TestFsShellCopy.testRootDir.toUri().getPath().toString()).getAbsolutePath();
        Path testRoot = new Path(windowsTestRootPath, "testPutDir");
        TestFsShellCopy.lfs.delete(testRoot, true);
        TestFsShellCopy.lfs.mkdirs(testRoot);
        Path targetDir = new Path(testRoot, "target");
        Path dirPath = new Path(testRoot, new Path("srcDir"));
        TestFsShellCopy.lfs.mkdirs(dirPath);
        TestFsShellCopy.lfs.create(new Path(dirPath, "srcFile")).close();
        checkPut(dirPath, targetDir, true);
    }

    @Test
    public void testRepresentsDir() throws Exception {
        Path subdirDstPath = new Path(TestFsShellCopy.dstPath, TestFsShellCopy.srcPath.getName());
        String[] argv = null;
        TestFsShellCopy.lfs.delete(TestFsShellCopy.dstPath, true);
        Assert.assertFalse(TestFsShellCopy.lfs.exists(TestFsShellCopy.dstPath));
        argv = new String[]{ "-put", TestFsShellCopy.srcPath.toString(), TestFsShellCopy.dstPath.toString() };
        Assert.assertEquals(0, TestFsShellCopy.shell.run(argv));
        Assert.assertTrue(((TestFsShellCopy.lfs.exists(TestFsShellCopy.dstPath)) && (TestFsShellCopy.lfs.isFile(TestFsShellCopy.dstPath))));
        TestFsShellCopy.lfs.delete(TestFsShellCopy.dstPath, true);
        Assert.assertFalse(TestFsShellCopy.lfs.exists(TestFsShellCopy.dstPath));
        // since dst path looks like a dir, it should not copy the file and
        // rename it to what looks like a directory
        TestFsShellCopy.lfs.delete(TestFsShellCopy.dstPath, true);// make copy fail

        for (String suffix : new String[]{ "/", "/." }) {
            argv = new String[]{ "-put", TestFsShellCopy.srcPath.toString(), (TestFsShellCopy.dstPath.toString()) + suffix };
            Assert.assertEquals(1, TestFsShellCopy.shell.run(argv));
            Assert.assertFalse(TestFsShellCopy.lfs.exists(TestFsShellCopy.dstPath));
            Assert.assertFalse(TestFsShellCopy.lfs.exists(subdirDstPath));
        }
        // since dst path looks like a dir, it should not copy the file and
        // rename it to what looks like a directory
        for (String suffix : new String[]{ "/", "/." }) {
            // empty out the directory and create to make copy succeed
            TestFsShellCopy.lfs.delete(TestFsShellCopy.dstPath, true);
            TestFsShellCopy.lfs.mkdirs(TestFsShellCopy.dstPath);
            argv = new String[]{ "-put", TestFsShellCopy.srcPath.toString(), (TestFsShellCopy.dstPath.toString()) + suffix };
            Assert.assertEquals(0, TestFsShellCopy.shell.run(argv));
            Assert.assertTrue(TestFsShellCopy.lfs.exists(subdirDstPath));
            Assert.assertTrue(TestFsShellCopy.lfs.isFile(subdirDstPath));
        }
        // ensure .. is interpreted as a dir
        String dotdotDst = (TestFsShellCopy.dstPath) + "/foo/..";
        TestFsShellCopy.lfs.delete(TestFsShellCopy.dstPath, true);
        TestFsShellCopy.lfs.mkdirs(new Path(TestFsShellCopy.dstPath, "foo"));
        argv = new String[]{ "-put", TestFsShellCopy.srcPath.toString(), dotdotDst };
        Assert.assertEquals(0, TestFsShellCopy.shell.run(argv));
        Assert.assertTrue(TestFsShellCopy.lfs.exists(subdirDstPath));
        Assert.assertTrue(TestFsShellCopy.lfs.isFile(subdirDstPath));
    }

    @Test
    public void testCopyMerge() throws Exception {
        Path root = new Path(TestFsShellCopy.testRootDir, "TestMerge");
        Path f1 = new Path(root, "f1");
        Path f2 = new Path(root, "f2");
        Path f3 = new Path(root, "f3");
        Path empty = new Path(root, "empty");
        Path fnf = new Path(root, "fnf");
        Path d = new Path(root, "dir");
        Path df1 = new Path(d, "df1");
        Path df2 = new Path(d, "df2");
        Path df3 = new Path(d, "df3");
        createFile(f1, f2, f3, df1, df2, df3);
        createEmptyFile(empty);
        int exit;
        // one file, kind of silly
        exit = TestFsShellCopy.shell.run(new String[]{ "-getmerge", f1.toString(), "out" });
        Assert.assertEquals(0, exit);
        Assert.assertEquals("f1", readFile("out"));
        exit = TestFsShellCopy.shell.run(new String[]{ "-getmerge", fnf.toString(), "out" });
        Assert.assertEquals(1, exit);
        Assert.assertFalse(TestFsShellCopy.lfs.exists(new Path("out")));
        // two files
        exit = TestFsShellCopy.shell.run(new String[]{ "-getmerge", f1.toString(), f2.toString(), "out" });
        Assert.assertEquals(0, exit);
        Assert.assertEquals("f1f2", readFile("out"));
        // two files, preserves order
        exit = TestFsShellCopy.shell.run(new String[]{ "-getmerge", f2.toString(), f1.toString(), "out" });
        Assert.assertEquals(0, exit);
        Assert.assertEquals("f2f1", readFile("out"));
        // two files
        exit = TestFsShellCopy.shell.run(new String[]{ "-getmerge", "-nl", f1.toString(), f2.toString(), "out" });
        Assert.assertEquals(0, exit);
        Assert.assertEquals("f1\nf2\n", readFile("out"));
        exit = TestFsShellCopy.shell.run(new String[]{ "-getmerge", "-nl", "-skip-empty-file", f1.toString(), f2.toString(), empty.toString(), "out" });
        Assert.assertEquals(0, exit);
        Assert.assertEquals("f1\nf2\n", readFile("out"));
        // glob three files
        TestFsShellCopy.shell.run(new String[]{ "-getmerge", "-nl", new Path(root, "f*").toString(), "out" });
        Assert.assertEquals(0, exit);
        Assert.assertEquals("f1\nf2\nf3\n", readFile("out"));
        // directory with 1 empty + 3 non empty files, should skip subdir
        TestFsShellCopy.shell.run(new String[]{ "-getmerge", "-nl", root.toString(), "out" });
        Assert.assertEquals(0, exit);
        Assert.assertEquals("\nf1\nf2\nf3\n", readFile("out"));
        // subdir
        TestFsShellCopy.shell.run(new String[]{ "-getmerge", "-nl", d.toString(), "out" });
        Assert.assertEquals(0, exit);
        Assert.assertEquals("df1\ndf2\ndf3\n", readFile("out"));
        // file, dir, file
        TestFsShellCopy.shell.run(new String[]{ "-getmerge", "-nl", f1.toString(), d.toString(), f2.toString(), "out" });
        Assert.assertEquals(0, exit);
        Assert.assertEquals("f1\ndf1\ndf2\ndf3\nf2\n", readFile("out"));
    }

    @Test
    public void testMoveFileFromLocal() throws Exception {
        Path testRoot = new Path(TestFsShellCopy.testRootDir, "testPutFile");
        TestFsShellCopy.lfs.delete(testRoot, true);
        TestFsShellCopy.lfs.mkdirs(testRoot);
        Path target = new Path(testRoot, "target");
        Path srcFile = new Path(testRoot, new Path("srcFile"));
        TestFsShellCopy.lfs.createNewFile(srcFile);
        int exit = TestFsShellCopy.shell.run(new String[]{ "-moveFromLocal", srcFile.toString(), target.toString() });
        Assert.assertEquals(0, exit);
        Assert.assertFalse(TestFsShellCopy.lfs.exists(srcFile));
        Assert.assertTrue(TestFsShellCopy.lfs.exists(target));
        Assert.assertTrue(TestFsShellCopy.lfs.isFile(target));
    }

    @Test
    public void testMoveDirFromLocal() throws Exception {
        Path testRoot = new Path(TestFsShellCopy.testRootDir, "testPutDir");
        TestFsShellCopy.lfs.delete(testRoot, true);
        TestFsShellCopy.lfs.mkdirs(testRoot);
        Path srcDir = new Path(testRoot, "srcDir");
        TestFsShellCopy.lfs.mkdirs(srcDir);
        Path targetDir = new Path(testRoot, "target");
        int exit = TestFsShellCopy.shell.run(new String[]{ "-moveFromLocal", srcDir.toString(), targetDir.toString() });
        Assert.assertEquals(0, exit);
        Assert.assertFalse(TestFsShellCopy.lfs.exists(srcDir));
        Assert.assertTrue(TestFsShellCopy.lfs.exists(targetDir));
    }

    @Test
    public void testMoveDirFromLocalDestExists() throws Exception {
        Path testRoot = new Path(TestFsShellCopy.testRootDir, "testPutDir");
        TestFsShellCopy.lfs.delete(testRoot, true);
        TestFsShellCopy.lfs.mkdirs(testRoot);
        Path srcDir = new Path(testRoot, "srcDir");
        TestFsShellCopy.lfs.mkdirs(srcDir);
        Path targetDir = new Path(testRoot, "target");
        TestFsShellCopy.lfs.mkdirs(targetDir);
        int exit = TestFsShellCopy.shell.run(new String[]{ "-moveFromLocal", srcDir.toString(), targetDir.toString() });
        Assert.assertEquals(0, exit);
        Assert.assertFalse(TestFsShellCopy.lfs.exists(srcDir));
        Assert.assertTrue(TestFsShellCopy.lfs.exists(new Path(targetDir, srcDir.getName())));
        TestFsShellCopy.lfs.mkdirs(srcDir);
        exit = TestFsShellCopy.shell.run(new String[]{ "-moveFromLocal", srcDir.toString(), targetDir.toString() });
        Assert.assertEquals(1, exit);
        Assert.assertTrue(TestFsShellCopy.lfs.exists(srcDir));
    }

    @Test
    public void testMoveFromWindowsLocalPath() throws Exception {
        PlatformAssumptions.assumeWindows();
        Path testRoot = new Path(TestFsShellCopy.testRootDir, "testPutFile");
        TestFsShellCopy.lfs.delete(testRoot, true);
        TestFsShellCopy.lfs.mkdirs(testRoot);
        Path target = new Path(testRoot, "target");
        Path srcFile = new Path(testRoot, new Path("srcFile"));
        TestFsShellCopy.lfs.createNewFile(srcFile);
        String winSrcFile = new File(srcFile.toUri().getPath().toString()).getAbsolutePath();
        shellRun(0, "-moveFromLocal", winSrcFile, target.toString());
        Assert.assertFalse(TestFsShellCopy.lfs.exists(srcFile));
        Assert.assertTrue(TestFsShellCopy.lfs.exists(target));
        Assert.assertTrue(TestFsShellCopy.lfs.isFile(target));
    }

    @Test
    public void testGetWindowsLocalPath() throws Exception {
        PlatformAssumptions.assumeWindows();
        String winDstFile = new File(TestFsShellCopy.dstPath.toUri().getPath().toString()).getAbsolutePath();
        shellRun(0, "-get", TestFsShellCopy.srcPath.toString(), winDstFile);
        checkPath(TestFsShellCopy.dstPath, false);
    }

    @Test
    public void testDirectCopy() throws Exception {
        Path testRoot = new Path(TestFsShellCopy.testRootDir, "testPutFile");
        TestFsShellCopy.lfs.delete(testRoot, true);
        TestFsShellCopy.lfs.mkdirs(testRoot);
        Path target_COPYING_File = new Path(testRoot, "target._COPYING_");
        Path target_File = new Path(testRoot, "target");
        Path srcFile = new Path(testRoot, new Path("srcFile"));
        TestFsShellCopy.lfs.createNewFile(srcFile);
        // If direct write is false , then creation of "file1" ,will delete file
        // (file1._COPYING_) if already exist.
        checkDirectCopy(srcFile, target_File, target_COPYING_File, false);
        TestFsShellCopy.shell.run(new String[]{ "-rm", target_File.toString() });
        // If direct write is true , then creation of "file1", will not create a
        // temporary file and will not delete (file1._COPYING_) if already exist.
        checkDirectCopy(srcFile, target_File, target_COPYING_File, true);
    }

    /**
     * Test copy to a path with non-existent parent directory.
     */
    @Test
    public void testCopyNoParent() throws Exception {
        final String noDirName = "noDir";
        final Path noDir = new Path(noDirName);
        TestFsShellCopy.lfs.delete(noDir, true);
        Assert.assertThat(TestFsShellCopy.lfs.exists(noDir), CoreMatchers.is(false));
        Assert.assertThat("Expected failed put to a path without parent directory", shellRun("-put", TestFsShellCopy.srcPath.toString(), (noDirName + "/foo")), CoreMatchers.is(CoreMatchers.not(0)));
        // Note the trailing '/' in the target path.
        Assert.assertThat("Expected failed copyFromLocal to a non-existent directory", shellRun("-copyFromLocal", TestFsShellCopy.srcPath.toString(), (noDirName + "/")), CoreMatchers.is(CoreMatchers.not(0)));
    }

    @Test
    public void testPutSrcDirNoPerm() throws Exception {
        final Path src = new Path(TestFsShellCopy.testRootDir, "srcNoPerm");
        final Path dst = new Path(TestFsShellCopy.testRootDir, "dst");
        TestFsShellCopy.lfs.delete(src, true);
        TestFsShellCopy.lfs.mkdirs(src, new FsPermission(((short) (0))));
        TestFsShellCopy.lfs.delete(dst, true);
        try {
            final ByteArrayOutputStream err = new ByteArrayOutputStream();
            PrintStream oldErr = System.err;
            System.setErr(new PrintStream(err));
            shellRun(1, "-put", src.toString(), dst.toString());
            System.setErr(oldErr);
            System.err.print(err.toString());
            Assert.assertTrue(err.toString().contains(PERMISSION_DENIED));
        } finally {
            // Make sure the test directory can be deleted
            TestFsShellCopy.lfs.setPermission(src, new FsPermission(((short) (493))));
        }
    }

    @Test
    public void testPutSrcFileNoPerm() throws Exception {
        final Path src = new Path(TestFsShellCopy.testRootDir, "srcNoPerm");
        final Path dst = new Path(TestFsShellCopy.testRootDir, "dst");
        TestFsShellCopy.lfs.delete(src, true);
        TestFsShellCopy.lfs.create(src);
        TestFsShellCopy.lfs.setPermission(src, new FsPermission(((short) (0))));
        TestFsShellCopy.lfs.delete(dst, true);
        try {
            final ByteArrayOutputStream err = new ByteArrayOutputStream();
            PrintStream oldErr = System.err;
            System.setErr(new PrintStream(err));
            shellRun(1, "-put", src.toString(), dst.toString());
            System.setErr(oldErr);
            System.err.print(err.toString());
            Assert.assertTrue(err.toString().contains("(Permission denied)"));
        } finally {
            // make sure the test file can be deleted
            TestFsShellCopy.lfs.setPermission(src, new FsPermission(((short) (493))));
        }
    }
}

