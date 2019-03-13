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
package org.apache.hadoop.mapred.uploader;


import CommonConfigurationKeys.FS_PERMISSIONS_UMASK_KEY;
import DFSConfigKeys.DFS_NAMENODE_EDITS_DIR_KEY;
import DFSConfigKeys.DFS_NAMENODE_NAME_DIR_KEY;
import com.google.common.collect.Lists;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


/**
 * Unit test class for FrameworkUploader.
 */
public class TestFrameworkUploader {
    private static String testDir;

    /**
     * Test requesting command line help.
     *
     * @throws IOException
     * 		test failure
     */
    @Test
    public void testHelp() throws IOException {
        String[] args = new String[]{ "-help" };
        FrameworkUploader uploader = new FrameworkUploader();
        boolean success = uploader.parseArguments(args);
        Assert.assertFalse("Expected to print help", success);
        Assert.assertEquals("Expected ignore run", null, uploader.input);
        Assert.assertEquals("Expected ignore run", null, uploader.whitelist);
        Assert.assertEquals("Expected ignore run", null, uploader.target);
    }

    /**
     * Test invalid argument parsing.
     *
     * @throws IOException
     * 		test failure
     */
    @Test
    public void testWrongArgument() throws IOException {
        String[] args = new String[]{ "-unexpected" };
        FrameworkUploader uploader = new FrameworkUploader();
        boolean success = uploader.parseArguments(args);
        Assert.assertFalse("Expected to print help", success);
    }

    /**
     * Test normal argument passing.
     *
     * @throws IOException
     * 		test failure
     */
    @Test
    public void testArguments() throws IOException {
        String[] args = new String[]{ "-input", "A", "-whitelist", "B", "-blacklist", "C", "-fs", "hdfs://C:8020", "-target", "D", "-initialReplication", "100", "-acceptableReplication", "120", "-finalReplication", "140", "-timeout", "10" };
        FrameworkUploader uploader = new FrameworkUploader();
        boolean success = uploader.parseArguments(args);
        Assert.assertTrue("Expected to print help", success);
        Assert.assertEquals("Input mismatch", "A", uploader.input);
        Assert.assertEquals("Whitelist mismatch", "B", uploader.whitelist);
        Assert.assertEquals("Blacklist mismatch", "C", uploader.blacklist);
        Assert.assertEquals("Target mismatch", "hdfs://C:8020/D", uploader.target);
        Assert.assertEquals("Initial replication mismatch", 100, uploader.initialReplication);
        Assert.assertEquals("Acceptable replication mismatch", 120, uploader.acceptableReplication);
        Assert.assertEquals("Final replication mismatch", 140, uploader.finalReplication);
        Assert.assertEquals("Timeout mismatch", 10, uploader.timeout);
    }

    /**
     * Test the default ways how to specify filesystems.
     */
    @Test
    public void testNoFilesystem() throws IOException {
        FrameworkUploader uploader = new FrameworkUploader();
        boolean success = uploader.parseArguments(new String[]{  });
        Assert.assertTrue("Expected to parse arguments", success);
        Assert.assertEquals("Expected", "file:////usr/lib/mr-framework.tar.gz#mr-framework", uploader.target);
    }

    /**
     * Test the default ways how to specify filesystems.
     */
    @Test
    public void testDefaultFilesystem() throws IOException {
        FrameworkUploader uploader = new FrameworkUploader();
        Configuration conf = new Configuration();
        conf.set(FS_DEFAULT_NAME_KEY, "hdfs://namenode:555");
        uploader.setConf(conf);
        boolean success = uploader.parseArguments(new String[]{  });
        Assert.assertTrue("Expected to parse arguments", success);
        Assert.assertEquals("Expected", "hdfs://namenode:555/usr/lib/mr-framework.tar.gz#mr-framework", uploader.target);
    }

    /**
     * Test the explicit filesystem specification.
     */
    @Test
    public void testExplicitFilesystem() throws IOException {
        FrameworkUploader uploader = new FrameworkUploader();
        Configuration conf = new Configuration();
        uploader.setConf(conf);
        boolean success = uploader.parseArguments(new String[]{ "-target", "hdfs://namenode:555/usr/lib/mr-framework.tar.gz#mr-framework" });
        Assert.assertTrue("Expected to parse arguments", success);
        Assert.assertEquals("Expected", "hdfs://namenode:555/usr/lib/mr-framework.tar.gz#mr-framework", uploader.target);
    }

    /**
     * Test the conflicting filesystem specification.
     */
    @Test
    public void testConflictingFilesystem() throws IOException {
        FrameworkUploader uploader = new FrameworkUploader();
        Configuration conf = new Configuration();
        conf.set(FS_DEFAULT_NAME_KEY, "hdfs://namenode:555");
        uploader.setConf(conf);
        boolean success = uploader.parseArguments(new String[]{ "-target", "file:///usr/lib/mr-framework.tar.gz#mr-framework" });
        Assert.assertTrue("Expected to parse arguments", success);
        Assert.assertEquals("Expected", "file:///usr/lib/mr-framework.tar.gz#mr-framework", uploader.target);
    }

    /**
     * Test whether we can filter a class path properly.
     *
     * @throws IOException
     * 		test failure
     */
    @Test
    public void testCollectPackages() throws IOException, UploaderException {
        File parent = new File(TestFrameworkUploader.testDir);
        try {
            parent.deleteOnExit();
            Assert.assertTrue("Directory creation failed", parent.mkdirs());
            File dirA = new File(parent, "A");
            Assert.assertTrue(dirA.mkdirs());
            File dirB = new File(parent, "B");
            Assert.assertTrue(dirB.mkdirs());
            File jarA = new File(dirA, "a.jar");
            Assert.assertTrue(jarA.createNewFile());
            File jarB = new File(dirA, "b.jar");
            Assert.assertTrue(jarB.createNewFile());
            File jarC = new File(dirA, "c.jar");
            Assert.assertTrue(jarC.createNewFile());
            File txtD = new File(dirA, "d.txt");
            Assert.assertTrue(txtD.createNewFile());
            File jarD = new File(dirB, "d.jar");
            Assert.assertTrue(jarD.createNewFile());
            File txtE = new File(dirB, "e.txt");
            Assert.assertTrue(txtE.createNewFile());
            FrameworkUploader uploader = new FrameworkUploader();
            uploader.whitelist = ".*a\\.jar,.*b\\.jar,.*d\\.jar";
            uploader.blacklist = ".*b\\.jar";
            uploader.input = ((((((dirA.getAbsolutePath()) + (File.separatorChar)) + "*") + (File.pathSeparatorChar)) + (dirB.getAbsolutePath())) + (File.separatorChar)) + "*";
            uploader.collectPackages();
            Assert.assertEquals("Whitelist count error", 3, uploader.whitelistedFiles.size());
            Assert.assertEquals("Blacklist count error", 1, uploader.blacklistedFiles.size());
            Assert.assertTrue("File not collected", uploader.filteredInputFiles.contains(jarA.getAbsolutePath()));
            Assert.assertFalse("File collected", uploader.filteredInputFiles.contains(jarB.getAbsolutePath()));
            Assert.assertTrue("File not collected", uploader.filteredInputFiles.contains(jarD.getAbsolutePath()));
            Assert.assertEquals("Too many whitelists", 2, uploader.filteredInputFiles.size());
        } finally {
            FileUtils.deleteDirectory(parent);
        }
    }

    /**
     * Test building a tarball from source jars.
     */
    @Test
    public void testBuildTarBall() throws IOException, InterruptedException, UploaderException {
        String[] testFiles = new String[]{ "upload.tar", "upload.tar.gz" };
        for (String testFile : testFiles) {
            File parent = new File(TestFrameworkUploader.testDir);
            try {
                parent.deleteOnExit();
                FrameworkUploader uploader = prepareTree(parent);
                File gzipFile = new File((((parent.getAbsolutePath()) + "/") + testFile));
                gzipFile.deleteOnExit();
                uploader.target = "file:///" + (gzipFile.getAbsolutePath());
                uploader.beginUpload();
                uploader.buildPackage();
                InputStream stream = new FileInputStream(gzipFile);
                if (gzipFile.getName().endsWith(".gz")) {
                    stream = new GZIPInputStream(stream);
                }
                TarArchiveInputStream result = null;
                try {
                    result = new TarArchiveInputStream(stream);
                    Set<String> fileNames = new HashSet<>();
                    Set<Long> sizes = new HashSet<>();
                    TarArchiveEntry entry1 = result.getNextTarEntry();
                    fileNames.add(entry1.getName());
                    sizes.add(entry1.getSize());
                    TarArchiveEntry entry2 = result.getNextTarEntry();
                    fileNames.add(entry2.getName());
                    sizes.add(entry2.getSize());
                    Assert.assertTrue("File name error", fileNames.contains("a.jar"));
                    Assert.assertTrue("File size error", sizes.contains(((long) (13))));
                    Assert.assertTrue("File name error", fileNames.contains("b.jar"));
                    Assert.assertTrue("File size error", sizes.contains(((long) (14))));
                } finally {
                    if (result != null) {
                        result.close();
                    }
                }
            } finally {
                FileUtils.deleteDirectory(parent);
            }
        }
    }

    /**
     * Test upload to HDFS.
     */
    @Test
    public void testUpload() throws IOException, InterruptedException, UploaderException {
        final String fileName = "/upload.tar.gz";
        File parent = new File(TestFrameworkUploader.testDir);
        try {
            parent.deleteOnExit();
            FrameworkUploader uploader = prepareTree(parent);
            uploader.target = ("file://" + (parent.getAbsolutePath())) + fileName;
            uploader.buildPackage();
            try (TarArchiveInputStream archiveInputStream = new TarArchiveInputStream(new GZIPInputStream(new FileInputStream(((parent.getAbsolutePath()) + fileName))))) {
                Set<String> fileNames = new HashSet<>();
                Set<Long> sizes = new HashSet<>();
                TarArchiveEntry entry1 = archiveInputStream.getNextTarEntry();
                fileNames.add(entry1.getName());
                sizes.add(entry1.getSize());
                TarArchiveEntry entry2 = archiveInputStream.getNextTarEntry();
                fileNames.add(entry2.getName());
                sizes.add(entry2.getSize());
                Assert.assertTrue("File name error", fileNames.contains("a.jar"));
                Assert.assertTrue("File size error", sizes.contains(((long) (13))));
                Assert.assertTrue("File name error", fileNames.contains("b.jar"));
                Assert.assertTrue("File size error", sizes.contains(((long) (14))));
            }
        } finally {
            FileUtils.deleteDirectory(parent);
        }
    }

    /**
     * Test regex pattern matching and environment variable replacement.
     */
    @Test
    public void testEnvironmentReplacement() throws UploaderException {
        String input = "C/$A/B,$B,D";
        Map<String, String> map = new HashMap<>();
        map.put("A", "X");
        map.put("B", "Y");
        map.put("C", "Z");
        FrameworkUploader uploader = new FrameworkUploader();
        String output = uploader.expandEnvironmentVariables(input, map);
        Assert.assertEquals("Environment not expanded", "C/X/B,Y,D", output);
    }

    /**
     * Test regex pattern matching and environment variable replacement.
     */
    @Test
    public void testRecursiveEnvironmentReplacement() throws UploaderException {
        String input = "C/$A/B,$B,D";
        Map<String, String> map = new HashMap<>();
        map.put("A", "X");
        map.put("B", "$C");
        map.put("C", "Y");
        FrameworkUploader uploader = new FrameworkUploader();
        String output = uploader.expandEnvironmentVariables(input, map);
        Assert.assertEquals("Environment not expanded", "C/X/B,Y,D", output);
    }

    /**
     * Test native IO.
     */
    @Test
    public void testNativeIO() throws IOException {
        FrameworkUploader uploader = new FrameworkUploader();
        File parent = new File(TestFrameworkUploader.testDir);
        try {
            // Create a parent directory
            parent.deleteOnExit();
            Assert.assertTrue(parent.mkdirs());
            // Create a target file
            File targetFile = new File(parent, "a.txt");
            try (FileOutputStream os = new FileOutputStream(targetFile)) {
                IOUtils.writeLines(Lists.newArrayList("a", "b"), null, os);
            }
            Assert.assertFalse(uploader.checkSymlink(targetFile));
            // Create a symlink to the target
            File symlinkToTarget = new File(parent, "symlinkToTarget.txt");
            try {
                Files.createSymbolicLink(Paths.get(symlinkToTarget.getAbsolutePath()), Paths.get(targetFile.getAbsolutePath()));
            } catch (UnsupportedOperationException e) {
                // Symlinks are not supported, so ignore the test
                Assume.assumeTrue(false);
            }
            Assert.assertTrue(uploader.checkSymlink(symlinkToTarget));
            // Create a symlink to the target with /./ in the path
            symlinkToTarget = new File(((parent.getAbsolutePath()) + "/./symlinkToTarget2.txt"));
            try {
                Files.createSymbolicLink(Paths.get(symlinkToTarget.getAbsolutePath()), Paths.get(targetFile.getAbsolutePath()));
            } catch (UnsupportedOperationException e) {
                // Symlinks are not supported, so ignore the test
                Assume.assumeTrue(false);
            }
            Assert.assertTrue(uploader.checkSymlink(symlinkToTarget));
            // Create a symlink outside the current directory
            File symlinkOutside = new File(parent, "symlinkToParent.txt");
            try {
                Files.createSymbolicLink(Paths.get(symlinkOutside.getAbsolutePath()), Paths.get(parent.getAbsolutePath()));
            } catch (UnsupportedOperationException e) {
                // Symlinks are not supported, so ignore the test
                Assume.assumeTrue(false);
            }
            Assert.assertFalse(uploader.checkSymlink(symlinkOutside));
        } finally {
            FileUtils.deleteDirectory(parent);
        }
    }

    @Test
    public void testPermissionSettingsOnRestrictiveUmask() throws Exception {
        File parent = new File(TestFrameworkUploader.testDir);
        parent.deleteOnExit();
        MiniDFSCluster cluster = null;
        try {
            Assert.assertTrue("Directory creation failed", parent.mkdirs());
            Configuration hdfsConf = new HdfsConfiguration();
            String namenodeDir = new File(MiniDFSCluster.getBaseDirectory(), "name").getAbsolutePath();
            hdfsConf.set(DFS_NAMENODE_NAME_DIR_KEY, namenodeDir);
            hdfsConf.set(DFS_NAMENODE_EDITS_DIR_KEY, namenodeDir);
            hdfsConf.set(FS_PERMISSIONS_UMASK_KEY, "027");
            cluster = numDataNodes(1).build();
            DistributedFileSystem dfs = cluster.getFileSystem();
            cluster.waitActive();
            File file1 = new File(parent, "a.jar");
            file1.createNewFile();
            File file2 = new File(parent, "b.jar");
            file2.createNewFile();
            File file3 = new File(parent, "c.jar");
            file3.createNewFile();
            FrameworkUploader uploader = new FrameworkUploader();
            uploader.whitelist = "";
            uploader.blacklist = "";
            uploader.input = ((parent.getAbsolutePath()) + (File.separatorChar)) + "*";
            String hdfsUri = hdfsConf.get(FS_DEFAULT_NAME_KEY);
            String targetPath = "/test.tar.gz";
            uploader.target = hdfsUri + targetPath;
            uploader.acceptableReplication = 1;
            uploader.setConf(hdfsConf);
            uploader.collectPackages();
            uploader.buildPackage();
            FileStatus fileStatus = dfs.getFileStatus(new Path(targetPath));
            FsPermission perm = fileStatus.getPermission();
            Assert.assertEquals("Permissions", new FsPermission(420), perm);
        } finally {
            if (cluster != null) {
                cluster.close();
            }
            FileUtils.deleteDirectory(parent);
        }
    }
}

