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
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.tools.util.TestDistCpUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestDistCpViewFs {
    private static final Logger LOG = LoggerFactory.getLogger(TestDistCpViewFs.class);

    private static FileSystem fs;

    private static Path listFile;

    private static Path target;

    private static String root;

    @Test
    public void testSingleFileMissingTarget() throws IOException {
        caseSingleFileMissingTarget(false);
        caseSingleFileMissingTarget(true);
    }

    @Test
    public void testSingleFileTargetFile() throws IOException {
        caseSingleFileTargetFile(false);
        caseSingleFileTargetFile(true);
    }

    @Test
    public void testSingleFileTargetDir() throws IOException {
        caseSingleFileTargetDir(false);
        caseSingleFileTargetDir(true);
    }

    @Test
    public void testSingleDirTargetMissing() throws IOException {
        caseSingleDirTargetMissing(false);
        caseSingleDirTargetMissing(true);
    }

    @Test
    public void testSingleDirTargetPresent() throws IOException {
        try {
            addEntries(TestDistCpViewFs.listFile, "singledir");
            mkdirs(((TestDistCpViewFs.root) + "/singledir/dir1"));
            mkdirs(TestDistCpViewFs.target.toString());
            runTest(TestDistCpViewFs.listFile, TestDistCpViewFs.target, true, false);
            checkResult(TestDistCpViewFs.target, 1, "singledir/dir1");
        } finally {
            TestDistCpUtils.delete(TestDistCpViewFs.fs, TestDistCpViewFs.root);
        }
    }

    @Test
    public void testUpdateSingleDirTargetPresent() throws IOException {
        try {
            addEntries(TestDistCpViewFs.listFile, "Usingledir");
            mkdirs(((TestDistCpViewFs.root) + "/Usingledir/Udir1"));
            mkdirs(TestDistCpViewFs.target.toString());
            runTest(TestDistCpViewFs.listFile, TestDistCpViewFs.target, true, true);
            checkResult(TestDistCpViewFs.target, 1, "Udir1");
        } finally {
            TestDistCpUtils.delete(TestDistCpViewFs.fs, TestDistCpViewFs.root);
        }
    }

    @Test
    public void testMultiFileTargetPresent() throws IOException {
        caseMultiFileTargetPresent(false);
        caseMultiFileTargetPresent(true);
    }

    @Test
    public void testMultiFileTargetMissing() throws IOException {
        caseMultiFileTargetMissing(false);
        caseMultiFileTargetMissing(true);
    }

    @Test
    public void testMultiDirTargetPresent() throws IOException {
        try {
            addEntries(TestDistCpViewFs.listFile, "multifile", "singledir");
            createFiles("multifile/file3", "multifile/file4", "multifile/file5");
            mkdirs(TestDistCpViewFs.target.toString(), ((TestDistCpViewFs.root) + "/singledir/dir1"));
            runTest(TestDistCpViewFs.listFile, TestDistCpViewFs.target, true, false);
            checkResult(TestDistCpViewFs.target, 2, "multifile/file3", "multifile/file4", "multifile/file5", "singledir/dir1");
        } finally {
            TestDistCpUtils.delete(TestDistCpViewFs.fs, TestDistCpViewFs.root);
        }
    }

    @Test
    public void testUpdateMultiDirTargetPresent() throws IOException {
        try {
            addEntries(TestDistCpViewFs.listFile, "Umultifile", "Usingledir");
            createFiles("Umultifile/Ufile3", "Umultifile/Ufile4", "Umultifile/Ufile5");
            mkdirs(TestDistCpViewFs.target.toString(), ((TestDistCpViewFs.root) + "/Usingledir/Udir1"));
            runTest(TestDistCpViewFs.listFile, TestDistCpViewFs.target, true, true);
            checkResult(TestDistCpViewFs.target, 4, "Ufile3", "Ufile4", "Ufile5", "Udir1");
        } finally {
            TestDistCpUtils.delete(TestDistCpViewFs.fs, TestDistCpViewFs.root);
        }
    }

    @Test
    public void testMultiDirTargetMissing() throws IOException {
        try {
            addEntries(TestDistCpViewFs.listFile, "multifile", "singledir");
            createFiles("multifile/file3", "multifile/file4", "multifile/file5");
            mkdirs(((TestDistCpViewFs.root) + "/singledir/dir1"));
            runTest(TestDistCpViewFs.listFile, TestDistCpViewFs.target, false, false);
            checkResult(TestDistCpViewFs.target, 2, "multifile/file3", "multifile/file4", "multifile/file5", "singledir/dir1");
        } finally {
            TestDistCpUtils.delete(TestDistCpViewFs.fs, TestDistCpViewFs.root);
        }
    }

    @Test
    public void testUpdateMultiDirTargetMissing() throws IOException {
        try {
            addEntries(TestDistCpViewFs.listFile, "multifile", "singledir");
            createFiles("multifile/file3", "multifile/file4", "multifile/file5");
            mkdirs(((TestDistCpViewFs.root) + "/singledir/dir1"));
            runTest(TestDistCpViewFs.listFile, TestDistCpViewFs.target, false, true);
            checkResult(TestDistCpViewFs.target, 4, "file3", "file4", "file5", "dir1");
        } finally {
            TestDistCpUtils.delete(TestDistCpViewFs.fs, TestDistCpViewFs.root);
        }
    }

    @Test
    public void testGlobTargetMissingSingleLevel() throws IOException {
        try {
            Path listFile = new Path("target/tmp1/listing").makeQualified(TestDistCpViewFs.fs.getUri(), TestDistCpViewFs.fs.getWorkingDirectory());
            addEntries(listFile, "*");
            createFiles("multifile/file3", "multifile/file4", "multifile/file5");
            createFiles("singledir/dir2/file6");
            runTest(listFile, TestDistCpViewFs.target, false, false);
            checkResult(TestDistCpViewFs.target, 2, "multifile/file3", "multifile/file4", "multifile/file5", "singledir/dir2/file6");
        } finally {
            TestDistCpUtils.delete(TestDistCpViewFs.fs, TestDistCpViewFs.root);
            TestDistCpUtils.delete(TestDistCpViewFs.fs, "target/tmp1");
        }
    }

    @Test
    public void testUpdateGlobTargetMissingSingleLevel() throws IOException {
        try {
            Path listFile = new Path("target/tmp1/listing").makeQualified(TestDistCpViewFs.fs.getUri(), TestDistCpViewFs.fs.getWorkingDirectory());
            addEntries(listFile, "*");
            createFiles("multifile/file3", "multifile/file4", "multifile/file5");
            createFiles("singledir/dir2/file6");
            runTest(listFile, TestDistCpViewFs.target, false, true);
            checkResult(TestDistCpViewFs.target, 4, "file3", "file4", "file5", "dir2/file6");
        } finally {
            TestDistCpUtils.delete(TestDistCpViewFs.fs, TestDistCpViewFs.root);
            TestDistCpUtils.delete(TestDistCpViewFs.fs, "target/tmp1");
        }
    }

    @Test
    public void testGlobTargetMissingMultiLevel() throws IOException {
        try {
            Path listFile = new Path("target/tmp1/listing").makeQualified(TestDistCpViewFs.fs.getUri(), TestDistCpViewFs.fs.getWorkingDirectory());
            addEntries(listFile, "*/*");
            createFiles("multifile/file3", "multifile/file4", "multifile/file5");
            createFiles("singledir1/dir3/file7", "singledir1/dir3/file8", "singledir1/dir3/file9");
            runTest(listFile, TestDistCpViewFs.target, false, false);
            checkResult(TestDistCpViewFs.target, 4, "file3", "file4", "file5", "dir3/file7", "dir3/file8", "dir3/file9");
        } finally {
            TestDistCpUtils.delete(TestDistCpViewFs.fs, TestDistCpViewFs.root);
            TestDistCpUtils.delete(TestDistCpViewFs.fs, "target/tmp1");
        }
    }

    @Test
    public void testUpdateGlobTargetMissingMultiLevel() throws IOException {
        try {
            Path listFile = new Path("target/tmp1/listing").makeQualified(TestDistCpViewFs.fs.getUri(), TestDistCpViewFs.fs.getWorkingDirectory());
            addEntries(listFile, "*/*");
            createFiles("multifile/file3", "multifile/file4", "multifile/file5");
            createFiles("singledir1/dir3/file7", "singledir1/dir3/file8", "singledir1/dir3/file9");
            runTest(listFile, TestDistCpViewFs.target, false, true);
            checkResult(TestDistCpViewFs.target, 6, "file3", "file4", "file5", "file7", "file8", "file9");
        } finally {
            TestDistCpUtils.delete(TestDistCpViewFs.fs, TestDistCpViewFs.root);
            TestDistCpUtils.delete(TestDistCpViewFs.fs, "target/tmp1");
        }
    }
}

