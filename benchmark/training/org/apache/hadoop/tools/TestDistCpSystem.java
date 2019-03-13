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


import java.net.URI;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.util.ToolRunner;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A JUnit test for copying files recursively.
 */
public class TestDistCpSystem {
    private static final Logger LOG = LoggerFactory.getLogger(TestDistCpSystem.class);

    @Rule
    public Timeout globalTimeout = new Timeout(30000);

    private static final String SRCDAT = "srcdat";

    private static final String DSTDAT = "dstdat";

    private static final long BLOCK_SIZE = 1024;

    private static MiniDFSCluster cluster;

    private static Configuration conf;

    private class FileEntry {
        String path;

        boolean isDir;

        public FileEntry(String path, boolean isDir) {
            this.path = path;
            this.isDir = isDir;
        }

        String getPath() {
            return path;
        }

        boolean isDirectory() {
            return isDir;
        }
    }

    @Test
    public void testRecursiveChunkCopy() throws Exception {
        TestDistCpSystem.FileEntry[] srcFiles = new TestDistCpSystem.FileEntry[]{ new TestDistCpSystem.FileEntry(TestDistCpSystem.SRCDAT, true), new TestDistCpSystem.FileEntry(((TestDistCpSystem.SRCDAT) + "/file0"), false), new TestDistCpSystem.FileEntry(((TestDistCpSystem.SRCDAT) + "/dir1"), true), new TestDistCpSystem.FileEntry(((TestDistCpSystem.SRCDAT) + "/dir2"), true), new TestDistCpSystem.FileEntry(((TestDistCpSystem.SRCDAT) + "/dir1/file1"), false) };
        chunkCopy(srcFiles);
    }

    @Test
    public void testChunkCopyOneFile() throws Exception {
        TestDistCpSystem.FileEntry[] srcFiles = new TestDistCpSystem.FileEntry[]{ new TestDistCpSystem.FileEntry(TestDistCpSystem.SRCDAT, true), new TestDistCpSystem.FileEntry(((TestDistCpSystem.SRCDAT) + "/file0"), false) };
        chunkCopy(srcFiles);
    }

    @Test
    public void testDistcpLargeFile() throws Exception {
        TestDistCpSystem.FileEntry[] srcfiles = new TestDistCpSystem.FileEntry[]{ new TestDistCpSystem.FileEntry(TestDistCpSystem.SRCDAT, true), new TestDistCpSystem.FileEntry(((TestDistCpSystem.SRCDAT) + "/file"), false) };
        final String testRoot = "/testdir";
        final String testSrcRel = TestDistCpSystem.SRCDAT;
        final String testSrc = (testRoot + "/") + testSrcRel;
        final String testDstRel = TestDistCpSystem.DSTDAT;
        final String testDst = (testRoot + "/") + testDstRel;
        String nnUri = FileSystem.getDefaultUri(TestDistCpSystem.conf).toString();
        DistributedFileSystem fs = ((DistributedFileSystem) (FileSystem.get(URI.create(nnUri), TestDistCpSystem.conf)));
        fs.mkdirs(new Path(testRoot));
        fs.mkdirs(new Path(testSrc));
        fs.mkdirs(new Path(testDst));
        long chunkSize = 6;
        createFiles(fs, testRoot, srcfiles, chunkSize);
        String srcFileName = (testRoot + (Path.SEPARATOR)) + (srcfiles[1].getPath());
        Path srcfile = new Path(srcFileName);
        if (!(TestDistCpSystem.cluster.getFileSystem().exists(srcfile))) {
            throw new Exception("src not exist");
        }
        final long srcLen = fs.getFileStatus(srcfile).getLen();
        FileStatus[] srcstats = TestDistCpSystem.getFileStatus(fs, testRoot, srcfiles);
        for (int i = 0; i < (srcfiles.length); i++) {
            fs.setOwner(srcstats[i].getPath(), ("u" + i), null);
        }
        String[] args = new String[]{ "-blocksperchunk", String.valueOf(chunkSize), nnUri + testSrc, nnUri + testDst };
        TestDistCpSystem.LOG.info(((("_____ running distcp: " + (args[0])) + " ") + (args[1])));
        ToolRunner.run(TestDistCpSystem.conf, new DistCp(), args);
        String realTgtPath = testDst;
        FileStatus[] dststat = TestDistCpSystem.getFileStatus(fs, realTgtPath, srcfiles);
        Assert.assertEquals("File length should match", srcLen, dststat[((dststat.length) - 1)].getLen());
        this.compareFiles(fs, srcstats[((srcstats.length) - 1)], dststat[((dststat.length) - 1)]);
        TestDistCpSystem.deldir(fs, testRoot);
    }

    @Test
    public void testPreserveUseNonEmptyDir() throws Exception {
        String testRoot = "/testdir." + (getMethodName());
        TestDistCpSystem.FileEntry[] srcfiles = new TestDistCpSystem.FileEntry[]{ new TestDistCpSystem.FileEntry(TestDistCpSystem.SRCDAT, true), new TestDistCpSystem.FileEntry(((TestDistCpSystem.SRCDAT) + "/a"), false), new TestDistCpSystem.FileEntry(((TestDistCpSystem.SRCDAT) + "/b"), true), new TestDistCpSystem.FileEntry(((TestDistCpSystem.SRCDAT) + "/b/c"), false) };
        TestDistCpSystem.FileEntry[] dstfiles = new TestDistCpSystem.FileEntry[]{ new TestDistCpSystem.FileEntry(TestDistCpSystem.DSTDAT, true), new TestDistCpSystem.FileEntry(((TestDistCpSystem.DSTDAT) + "/a"), false), new TestDistCpSystem.FileEntry(((TestDistCpSystem.DSTDAT) + "/b"), true), new TestDistCpSystem.FileEntry(((TestDistCpSystem.DSTDAT) + "/b/c"), false) };
        testPreserveUserHelper(testRoot, srcfiles, srcfiles, false, true, false);
        testPreserveUserHelper(testRoot, srcfiles, dstfiles, false, false, false);
    }

    @Test
    public void testPreserveUserEmptyDir() throws Exception {
        String testRoot = "/testdir." + (getMethodName());
        TestDistCpSystem.FileEntry[] srcfiles = new TestDistCpSystem.FileEntry[]{ new TestDistCpSystem.FileEntry(TestDistCpSystem.SRCDAT, true) };
        TestDistCpSystem.FileEntry[] dstfiles = new TestDistCpSystem.FileEntry[]{ new TestDistCpSystem.FileEntry(TestDistCpSystem.DSTDAT, true) };
        testPreserveUserHelper(testRoot, srcfiles, srcfiles, false, true, false);
        testPreserveUserHelper(testRoot, srcfiles, dstfiles, false, false, false);
    }

    @Test
    public void testPreserveUserSingleFile() throws Exception {
        String testRoot = "/testdir." + (getMethodName());
        TestDistCpSystem.FileEntry[] srcfiles = new TestDistCpSystem.FileEntry[]{ new TestDistCpSystem.FileEntry(TestDistCpSystem.SRCDAT, false) };
        TestDistCpSystem.FileEntry[] dstfiles = new TestDistCpSystem.FileEntry[]{ new TestDistCpSystem.FileEntry(TestDistCpSystem.DSTDAT, false) };
        testPreserveUserHelper(testRoot, srcfiles, srcfiles, false, true, false);
        testPreserveUserHelper(testRoot, srcfiles, dstfiles, false, false, false);
    }

    @Test
    public void testPreserveUserNonEmptyDirWithUpdate() throws Exception {
        String testRoot = "/testdir." + (getMethodName());
        TestDistCpSystem.FileEntry[] srcfiles = new TestDistCpSystem.FileEntry[]{ new TestDistCpSystem.FileEntry(((TestDistCpSystem.SRCDAT) + "/a"), false), new TestDistCpSystem.FileEntry(((TestDistCpSystem.SRCDAT) + "/b"), true), new TestDistCpSystem.FileEntry(((TestDistCpSystem.SRCDAT) + "/b/c"), false) };
        TestDistCpSystem.FileEntry[] dstfiles = new TestDistCpSystem.FileEntry[]{ new TestDistCpSystem.FileEntry("a", false), new TestDistCpSystem.FileEntry("b", true), new TestDistCpSystem.FileEntry("b/c", false) };
        testPreserveUserHelper(testRoot, srcfiles, dstfiles, true, true, true);
    }

    @Test
    public void testSourceRoot() throws Exception {
        FileSystem fs = TestDistCpSystem.cluster.getFileSystem();
        String rootStr = fs.makeQualified(new Path("/")).toString();
        String testRoot = "/testdir." + (getMethodName());
        // Case 1. The target does not exist.
        Path tgtPath = new Path((testRoot + "/nodir"));
        String tgtStr = fs.makeQualified(tgtPath).toString();
        String[] args = new String[]{ rootStr, tgtStr };
        Assert.assertThat(ToolRunner.run(TestDistCpSystem.conf, new DistCp(), args), Is.is(0));
        // Case 2. The target exists.
        Path tgtPath2 = new Path((testRoot + "/dir"));
        Assert.assertTrue(fs.mkdirs(tgtPath2));
        String tgtStr2 = fs.makeQualified(tgtPath2).toString();
        String[] args2 = new String[]{ rootStr, tgtStr2 };
        Assert.assertThat(ToolRunner.run(TestDistCpSystem.conf, new DistCp(), args2), Is.is(0));
    }
}

