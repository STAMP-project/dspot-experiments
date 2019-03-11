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
import java.util.HashMap;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.tools.util.TestDistCpUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestFileBasedCopyListing {
    private static final Logger LOG = LoggerFactory.getLogger(TestFileBasedCopyListing.class);

    private static final Credentials CREDENTIALS = new Credentials();

    private static final Configuration config = new Configuration();

    private static MiniDFSCluster cluster;

    private static FileSystem fs;

    private static Map<String, String> map = new HashMap<String, String>();

    @Test
    public void testSingleFileMissingTarget() {
        caseSingleFileMissingTarget(false);
        caseSingleFileMissingTarget(true);
    }

    @Test
    public void testSingleFileTargetFile() {
        caseSingleFileTargetFile(false);
        caseSingleFileTargetFile(true);
    }

    @Test
    public void testSingleFileTargetDir() {
        caseSingleFileTargetDir(false);
        caseSingleFileTargetDir(true);
    }

    @Test
    public void testSingleDirTargetMissing() {
        caseSingleDirTargetMissing(false);
        caseSingleDirTargetMissing(true);
    }

    @Test
    public void testSingleDirTargetPresent() {
        try {
            Path listFile = new Path("/tmp/listing");
            Path target = new Path("/tmp/target");
            addEntries(listFile, "/tmp/singledir");
            mkdirs("/tmp/singledir/dir1");
            mkdirs(target.toString());
            runTest(listFile, target, true);
            checkResult(listFile, 1);
        } catch (IOException e) {
            TestFileBasedCopyListing.LOG.error("Exception encountered while testing build listing", e);
            Assert.fail("build listing failure");
        } finally {
            TestDistCpUtils.delete(TestFileBasedCopyListing.fs, "/tmp");
        }
    }

    @Test
    public void testUpdateSingleDirTargetPresent() {
        try {
            Path listFile = new Path("/tmp/listing");
            Path target = new Path("/tmp/target");
            addEntries(listFile, "/tmp/Usingledir");
            mkdirs("/tmp/Usingledir/Udir1");
            mkdirs(target.toString());
            runTest(listFile, target, true, true);
            checkResult(listFile, 1);
        } catch (IOException e) {
            TestFileBasedCopyListing.LOG.error("Exception encountered while testing build listing", e);
            Assert.fail("build listing failure");
        } finally {
            TestDistCpUtils.delete(TestFileBasedCopyListing.fs, "/tmp");
        }
    }

    @Test
    public void testMultiFileTargetPresent() {
        caseMultiFileTargetPresent(false);
        caseMultiFileTargetPresent(true);
    }

    @Test
    public void testMultiFileTargetMissing() {
        caseMultiFileTargetMissing(false);
        caseMultiFileTargetMissing(true);
    }

    @Test
    public void testMultiDirTargetPresent() {
        try {
            Path listFile = new Path("/tmp/listing");
            Path target = new Path("/tmp/target");
            addEntries(listFile, "/tmp/multifile", "/tmp/singledir");
            createFiles("/tmp/multifile/file3", "/tmp/multifile/file4", "/tmp/multifile/file5");
            mkdirs(target.toString(), "/tmp/singledir/dir1");
            runTest(listFile, target, true);
            checkResult(listFile, 4);
        } catch (IOException e) {
            TestFileBasedCopyListing.LOG.error("Exception encountered while testing build listing", e);
            Assert.fail("build listing failure");
        } finally {
            TestDistCpUtils.delete(TestFileBasedCopyListing.fs, "/tmp");
        }
    }

    @Test
    public void testUpdateMultiDirTargetPresent() {
        try {
            Path listFile = new Path("/tmp/listing");
            Path target = new Path("/tmp/target");
            addEntries(listFile, "/tmp/Umultifile", "/tmp/Usingledir");
            createFiles("/tmp/Umultifile/Ufile3", "/tmp/Umultifile/Ufile4", "/tmp/Umultifile/Ufile5");
            mkdirs(target.toString(), "/tmp/Usingledir/Udir1");
            runTest(listFile, target, true);
            checkResult(listFile, 4);
        } catch (IOException e) {
            TestFileBasedCopyListing.LOG.error("Exception encountered while testing build listing", e);
            Assert.fail("build listing failure");
        } finally {
            TestDistCpUtils.delete(TestFileBasedCopyListing.fs, "/tmp");
        }
    }

    @Test
    public void testMultiDirTargetMissing() {
        caseMultiDirTargetMissing(false);
        caseMultiDirTargetMissing(true);
    }

    @Test
    public void testGlobTargetMissingSingleLevel() {
        caseGlobTargetMissingSingleLevel(false);
        caseGlobTargetMissingSingleLevel(true);
    }

    @Test
    public void testGlobTargetMissingMultiLevel() {
        caseGlobTargetMissingMultiLevel(false);
        caseGlobTargetMissingMultiLevel(true);
    }

    @Test
    public void testGlobTargetDirMultiLevel() {
        try {
            Path listFile = new Path("/tmp1/listing");
            Path target = new Path("/tmp/target");
            addEntries(listFile, "/tmp/*/*");
            createFiles("/tmp/multifile/file3", "/tmp/multifile/file4", "/tmp/multifile/file5");
            createFiles("/tmp/singledir1/dir3/file7", "/tmp/singledir1/dir3/file8", "/tmp/singledir1/dir3/file9");
            mkdirs(target.toString());
            runTest(listFile, target, true);
            checkResult(listFile, 6);
        } catch (IOException e) {
            TestFileBasedCopyListing.LOG.error("Exception encountered while testing build listing", e);
            Assert.fail("build listing failure");
        } finally {
            TestDistCpUtils.delete(TestFileBasedCopyListing.fs, "/tmp");
            TestDistCpUtils.delete(TestFileBasedCopyListing.fs, "/tmp1");
        }
    }

    @Test
    public void testUpdateGlobTargetDirMultiLevel() {
        try {
            Path listFile = new Path("/tmp1/listing");
            Path target = new Path("/tmp/target");
            addEntries(listFile, "/tmp/*/*");
            createFiles("/tmp/Umultifile/Ufile3", "/tmp/Umultifile/Ufile4", "/tmp/Umultifile/Ufile5");
            createFiles("/tmp/Usingledir1/Udir3/Ufile7", "/tmp/Usingledir1/Udir3/Ufile8", "/tmp/Usingledir1/Udir3/Ufile9");
            mkdirs(target.toString());
            runTest(listFile, target, true);
            checkResult(listFile, 6);
        } catch (IOException e) {
            TestFileBasedCopyListing.LOG.error("Exception encountered while testing build listing", e);
            Assert.fail("build listing failure");
        } finally {
            TestDistCpUtils.delete(TestFileBasedCopyListing.fs, "/tmp");
            TestDistCpUtils.delete(TestFileBasedCopyListing.fs, "/tmp1");
        }
    }
}

