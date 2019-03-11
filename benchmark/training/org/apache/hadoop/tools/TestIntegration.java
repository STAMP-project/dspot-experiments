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
package org.apache.hadoop.tools;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.JobSubmissionFiles;
import org.apache.hadoop.tools.util.TestDistCpUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RunWith(Parameterized.class)
public class TestIntegration {
    private static final Logger LOG = LoggerFactory.getLogger(TestIntegration.class);

    private static FileSystem fs;

    private static Path listFile;

    private static Path target;

    private static String root;

    private int numListstatusThreads;

    public TestIntegration(int numListstatusThreads) {
        this.numListstatusThreads = numListstatusThreads;
    }

    @Test(timeout = 100000)
    public void testSingleFileMissingTarget() {
        caseSingleFileMissingTarget(false);
        caseSingleFileMissingTarget(true);
    }

    @Test(timeout = 100000)
    public void testSingleFileTargetFile() {
        caseSingleFileTargetFile(false);
        caseSingleFileTargetFile(true);
    }

    @Test(timeout = 100000)
    public void testSingleFileTargetDir() {
        caseSingleFileTargetDir(false);
        caseSingleFileTargetDir(true);
    }

    @Test(timeout = 100000)
    public void testSingleDirTargetMissing() {
        caseSingleDirTargetMissing(false);
        caseSingleDirTargetMissing(true);
    }

    @Test(timeout = 100000)
    public void testSingleDirTargetPresent() {
        try {
            addEntries(TestIntegration.listFile, "singledir");
            mkdirs(((TestIntegration.root) + "/singledir/dir1"));
            mkdirs(TestIntegration.target.toString());
            runTest(TestIntegration.listFile, TestIntegration.target, true, false);
            checkResult(TestIntegration.target, 1, "singledir/dir1");
        } catch (IOException e) {
            TestIntegration.LOG.error("Exception encountered while testing distcp", e);
            Assert.fail("distcp failure");
        } finally {
            TestDistCpUtils.delete(TestIntegration.fs, TestIntegration.root);
        }
    }

    @Test(timeout = 100000)
    public void testUpdateSingleDirTargetPresent() {
        try {
            addEntries(TestIntegration.listFile, "Usingledir");
            mkdirs(((TestIntegration.root) + "/Usingledir/Udir1"));
            mkdirs(TestIntegration.target.toString());
            runTest(TestIntegration.listFile, TestIntegration.target, true, true);
            checkResult(TestIntegration.target, 1, "Udir1");
        } catch (IOException e) {
            TestIntegration.LOG.error("Exception encountered while testing distcp", e);
            Assert.fail("distcp failure");
        } finally {
            TestDistCpUtils.delete(TestIntegration.fs, TestIntegration.root);
        }
    }

    @Test(timeout = 100000)
    public void testMultiFileTargetPresent() {
        caseMultiFileTargetPresent(false);
        caseMultiFileTargetPresent(true);
    }

    @Test(timeout = 100000)
    public void testMultiFileTargetMissing() {
        caseMultiFileTargetMissing(false);
        caseMultiFileTargetMissing(true);
    }

    @Test(timeout = 100000)
    public void testMultiDirTargetPresent() {
        try {
            addEntries(TestIntegration.listFile, "multifile", "singledir");
            createFiles("multifile/file3", "multifile/file4", "multifile/file5");
            mkdirs(TestIntegration.target.toString(), ((TestIntegration.root) + "/singledir/dir1"));
            runTest(TestIntegration.listFile, TestIntegration.target, true, false);
            checkResult(TestIntegration.target, 2, "multifile/file3", "multifile/file4", "multifile/file5", "singledir/dir1");
        } catch (IOException e) {
            TestIntegration.LOG.error("Exception encountered while testing distcp", e);
            Assert.fail("distcp failure");
        } finally {
            TestDistCpUtils.delete(TestIntegration.fs, TestIntegration.root);
        }
    }

    @Test(timeout = 100000)
    public void testUpdateMultiDirTargetPresent() {
        try {
            addEntries(TestIntegration.listFile, "Umultifile", "Usingledir");
            createFiles("Umultifile/Ufile3", "Umultifile/Ufile4", "Umultifile/Ufile5");
            mkdirs(TestIntegration.target.toString(), ((TestIntegration.root) + "/Usingledir/Udir1"));
            runTest(TestIntegration.listFile, TestIntegration.target, true, true);
            checkResult(TestIntegration.target, 4, "Ufile3", "Ufile4", "Ufile5", "Udir1");
        } catch (IOException e) {
            TestIntegration.LOG.error("Exception encountered while testing distcp", e);
            Assert.fail("distcp failure");
        } finally {
            TestDistCpUtils.delete(TestIntegration.fs, TestIntegration.root);
        }
    }

    @Test(timeout = 100000)
    public void testMultiDirTargetMissing() {
        try {
            addEntries(TestIntegration.listFile, "multifile", "singledir");
            createFiles("multifile/file3", "multifile/file4", "multifile/file5");
            mkdirs(((TestIntegration.root) + "/singledir/dir1"));
            runTest(TestIntegration.listFile, TestIntegration.target, false, false);
            checkResult(TestIntegration.target, 2, "multifile/file3", "multifile/file4", "multifile/file5", "singledir/dir1");
        } catch (IOException e) {
            TestIntegration.LOG.error("Exception encountered while testing distcp", e);
            Assert.fail("distcp failure");
        } finally {
            TestDistCpUtils.delete(TestIntegration.fs, TestIntegration.root);
        }
    }

    @Test(timeout = 100000)
    public void testUpdateMultiDirTargetMissing() {
        try {
            addEntries(TestIntegration.listFile, "multifile", "singledir");
            createFiles("multifile/file3", "multifile/file4", "multifile/file5");
            mkdirs(((TestIntegration.root) + "/singledir/dir1"));
            runTest(TestIntegration.listFile, TestIntegration.target, false, true);
            checkResult(TestIntegration.target, 4, "file3", "file4", "file5", "dir1");
        } catch (IOException e) {
            TestIntegration.LOG.error("Exception encountered while testing distcp", e);
            Assert.fail("distcp failure");
        } finally {
            TestDistCpUtils.delete(TestIntegration.fs, TestIntegration.root);
        }
    }

    @Test(timeout = 100000)
    public void testDeleteMissingInDestination() {
        try {
            addEntries(TestIntegration.listFile, "srcdir");
            createFiles("srcdir/file1", "dstdir/file1", "dstdir/file2");
            Path target = new Path(((TestIntegration.root) + "/dstdir"));
            runTest(TestIntegration.listFile, target, false, true, true, false);
            checkResult(target, 1, "file1");
        } catch (IOException e) {
            TestIntegration.LOG.error("Exception encountered while running distcp", e);
            Assert.fail("distcp failure");
        } finally {
            TestDistCpUtils.delete(TestIntegration.fs, TestIntegration.root);
            TestDistCpUtils.delete(TestIntegration.fs, "target/tmp1");
        }
    }

    @Test(timeout = 100000)
    public void testOverwrite() {
        byte[] contents1 = "contents1".getBytes();
        byte[] contents2 = "contents2".getBytes();
        Assert.assertEquals(contents1.length, contents2.length);
        try {
            addEntries(TestIntegration.listFile, "srcdir");
            createWithContents("srcdir/file1", contents1);
            createWithContents("dstdir/file1", contents2);
            Path target = new Path(((TestIntegration.root) + "/dstdir"));
            runTest(TestIntegration.listFile, target, false, false, false, true);
            checkResult(target, 1, "file1");
            // make sure dstdir/file1 has been overwritten with the contents
            // of srcdir/file1
            FSDataInputStream is = TestIntegration.fs.open(new Path(((TestIntegration.root) + "/dstdir/file1")));
            byte[] dstContents = new byte[contents1.length];
            is.readFully(dstContents);
            is.close();
            Assert.assertArrayEquals(contents1, dstContents);
        } catch (IOException e) {
            TestIntegration.LOG.error("Exception encountered while running distcp", e);
            Assert.fail("distcp failure");
        } finally {
            TestDistCpUtils.delete(TestIntegration.fs, TestIntegration.root);
            TestDistCpUtils.delete(TestIntegration.fs, "target/tmp1");
        }
    }

    @Test(timeout = 100000)
    public void testGlobTargetMissingSingleLevel() {
        try {
            Path listFile = new Path("target/tmp1/listing").makeQualified(TestIntegration.fs.getUri(), TestIntegration.fs.getWorkingDirectory());
            addEntries(listFile, "*");
            createFiles("multifile/file3", "multifile/file4", "multifile/file5");
            createFiles("singledir/dir2/file6");
            runTest(listFile, TestIntegration.target, false, false);
            checkResult(TestIntegration.target, 2, "multifile/file3", "multifile/file4", "multifile/file5", "singledir/dir2/file6");
        } catch (IOException e) {
            TestIntegration.LOG.error("Exception encountered while testing distcp", e);
            Assert.fail("distcp failure");
        } finally {
            TestDistCpUtils.delete(TestIntegration.fs, TestIntegration.root);
            TestDistCpUtils.delete(TestIntegration.fs, "target/tmp1");
        }
    }

    @Test(timeout = 100000)
    public void testUpdateGlobTargetMissingSingleLevel() {
        try {
            Path listFile = new Path("target/tmp1/listing").makeQualified(TestIntegration.fs.getUri(), TestIntegration.fs.getWorkingDirectory());
            addEntries(listFile, "*");
            createFiles("multifile/file3", "multifile/file4", "multifile/file5");
            createFiles("singledir/dir2/file6");
            runTest(listFile, TestIntegration.target, false, true);
            checkResult(TestIntegration.target, 4, "file3", "file4", "file5", "dir2/file6");
        } catch (IOException e) {
            TestIntegration.LOG.error("Exception encountered while running distcp", e);
            Assert.fail("distcp failure");
        } finally {
            TestDistCpUtils.delete(TestIntegration.fs, TestIntegration.root);
            TestDistCpUtils.delete(TestIntegration.fs, "target/tmp1");
        }
    }

    @Test(timeout = 100000)
    public void testGlobTargetMissingMultiLevel() {
        try {
            Path listFile = new Path("target/tmp1/listing").makeQualified(TestIntegration.fs.getUri(), TestIntegration.fs.getWorkingDirectory());
            addEntries(listFile, "*/*");
            createFiles("multifile/file3", "multifile/file4", "multifile/file5");
            createFiles("singledir1/dir3/file7", "singledir1/dir3/file8", "singledir1/dir3/file9");
            runTest(listFile, TestIntegration.target, false, false);
            checkResult(TestIntegration.target, 4, "file3", "file4", "file5", "dir3/file7", "dir3/file8", "dir3/file9");
        } catch (IOException e) {
            TestIntegration.LOG.error("Exception encountered while running distcp", e);
            Assert.fail("distcp failure");
        } finally {
            TestDistCpUtils.delete(TestIntegration.fs, TestIntegration.root);
            TestDistCpUtils.delete(TestIntegration.fs, "target/tmp1");
        }
    }

    @Test(timeout = 100000)
    public void testUpdateGlobTargetMissingMultiLevel() {
        try {
            Path listFile = new Path("target/tmp1/listing").makeQualified(TestIntegration.fs.getUri(), TestIntegration.fs.getWorkingDirectory());
            addEntries(listFile, "*/*");
            createFiles("multifile/file3", "multifile/file4", "multifile/file5");
            createFiles("singledir1/dir3/file7", "singledir1/dir3/file8", "singledir1/dir3/file9");
            runTest(listFile, TestIntegration.target, false, true);
            checkResult(TestIntegration.target, 6, "file3", "file4", "file5", "file7", "file8", "file9");
        } catch (IOException e) {
            TestIntegration.LOG.error("Exception encountered while running distcp", e);
            Assert.fail("distcp failure");
        } finally {
            TestDistCpUtils.delete(TestIntegration.fs, TestIntegration.root);
            TestDistCpUtils.delete(TestIntegration.fs, "target/tmp1");
        }
    }

    @Test(timeout = 100000)
    public void testCleanup() {
        try {
            Path sourcePath = new Path("noscheme:///file");
            List<Path> sources = new ArrayList<Path>();
            sources.add(sourcePath);
            DistCpOptions options = build();
            Configuration conf = TestIntegration.getConf();
            Path stagingDir = JobSubmissionFiles.getStagingDir(new org.apache.hadoop.mapreduce.Cluster(conf), conf);
            stagingDir.getFileSystem(conf).mkdirs(stagingDir);
            try {
                execute();
            } catch (Throwable t) {
                Assert.assertEquals(stagingDir.getFileSystem(conf).listStatus(stagingDir).length, 0);
            }
        } catch (Exception e) {
            TestIntegration.LOG.error("Exception encountered ", e);
            Assert.fail(("testCleanup failed " + (e.getMessage())));
        }
    }
}

