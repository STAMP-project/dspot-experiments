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
package org.apache.hadoop.fs.s3a;


import com.amazonaws.services.s3.model.ListObjectsV2Result;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.fs.contract.ContractTestUtils;
import org.junit.Assume;
import org.junit.Test;


/**
 * Test S3Guard list consistency feature by injecting delayed listObjects()
 * visibility via {@link InconsistentAmazonS3Client}.
 *
 * Tests here generally:
 * 1. Use the inconsistency injection mentioned above.
 * 2. Only run when S3Guard is enabled.
 */
public class ITestS3GuardListConsistency extends AbstractS3ATestBase {
    private Invoker invoker;

    /**
     * Tests that after renaming a directory, the original directory and its
     * contents are indeed missing and the corresponding new paths are visible.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testConsistentListAfterRename() throws Exception {
        Path[] mkdirs = new Path[]{ path("d1/f"), path(("d1/f" + (DEFAULT_DELAY_KEY_SUBSTRING))) };
        Path[] srcdirs = new Path[]{ path("d1") };
        Path[] dstdirs = new Path[]{ path("d2") };
        Path[] yesdirs = new Path[]{ path("d2"), path("d2/f"), path(("d2/f" + (DEFAULT_DELAY_KEY_SUBSTRING))) };
        Path[] nodirs = new Path[]{ path("d1"), path("d1/f"), path(("d1/f" + (DEFAULT_DELAY_KEY_SUBSTRING))) };
        doTestRenameSequence(mkdirs, srcdirs, dstdirs, yesdirs, nodirs);
        getFileSystem().delete(path("d1"), true);
        getFileSystem().delete(path("d2"), true);
    }

    /**
     * Tests a circular sequence of renames to verify that overwriting recently
     * deleted files and reading recently created files from rename operations
     * works as expected.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testRollingRenames() throws Exception {
        Path[] dir0 = new Path[]{ path("rolling/1") };
        Path[] dir1 = new Path[]{ path("rolling/2") };
        Path[] dir2 = new Path[]{ path("rolling/3") };
        // These sets have to be in reverse order compared to the movement
        Path[] setA = new Path[]{ dir1[0], dir0[0] };
        Path[] setB = new Path[]{ dir2[0], dir1[0] };
        Path[] setC = new Path[]{ dir0[0], dir2[0] };
        for (int i = 0; i < 2; i++) {
            Path[] firstSet = (i == 0) ? setA : null;
            doTestRenameSequence(firstSet, setA, setB, setB, dir0);
            doTestRenameSequence(null, setB, setC, setC, dir1);
            doTestRenameSequence(null, setC, setA, setA, dir2);
        }
        S3AFileSystem fs = getFileSystem();
        assertFalse("Renaming deleted file should have failed", fs.rename(dir2[0], dir1[0]));
        assertTrue("Renaming over existing file should have succeeded", fs.rename(dir1[0], dir0[0]));
    }

    /**
     * Tests that deleted files immediately stop manifesting in list operations
     * even when the effect in S3 is delayed.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testConsistentListAfterDelete() throws Exception {
        S3AFileSystem fs = getFileSystem();
        // test will fail if NullMetadataStore (the default) is configured: skip it.
        Assume.assumeTrue("FS needs to have a metadatastore.", fs.hasMetadataStore());
        // Any S3 keys that contain DELAY_KEY_SUBSTRING will be delayed
        // in listObjects() results via InconsistentS3Client
        Path inconsistentPath = path(("a/b/dir3-" + (DEFAULT_DELAY_KEY_SUBSTRING)));
        Path[] testDirs = new Path[]{ path("a/b/dir1"), path("a/b/dir2"), inconsistentPath };
        for (Path path : testDirs) {
            assertTrue(("Can't create directory: " + path), fs.mkdirs(path));
        }
        clearInconsistency(fs);
        for (Path path : testDirs) {
            assertTrue(("Can't delete path: " + path), fs.delete(path, false));
        }
        FileStatus[] paths = fs.listStatus(path("a/b/"));
        List<Path> list = new ArrayList<>();
        for (FileStatus fileState : paths) {
            list.add(fileState.getPath());
        }
        assertFalse("This path should be deleted.", list.contains(path("a/b/dir1")));
        assertFalse("This path should be deleted.", list.contains(path("a/b/dir2")));
        assertFalse("This should fail without S3Guard, and succeed with it.", list.contains(inconsistentPath));
    }

    /**
     * Tests that rename immediately after files in the source directory are
     * deleted results in exactly the correct set of destination files and none
     * of the source files.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testConsistentRenameAfterDelete() throws Exception {
        S3AFileSystem fs = getFileSystem();
        // test will fail if NullMetadataStore (the default) is configured: skip it.
        Assume.assumeTrue(fs.hasMetadataStore());
        // Any S3 keys that contain DELAY_KEY_SUBSTRING will be delayed
        // in listObjects() results via InconsistentS3Client
        Path inconsistentPath = path(("a/b/dir3-" + (DEFAULT_DELAY_KEY_SUBSTRING)));
        Path[] testDirs = new Path[]{ path("a/b/dir1"), path("a/b/dir2"), inconsistentPath };
        for (Path path : testDirs) {
            assertTrue(fs.mkdirs(path));
        }
        clearInconsistency(fs);
        assertTrue(fs.delete(testDirs[1], false));
        assertTrue(fs.delete(testDirs[2], false));
        fs.rename(path("a"), path("a3"));
        FileStatus[] paths = fs.listStatus(path("a3/b"));
        List<Path> list = new ArrayList<>();
        for (FileStatus fileState : paths) {
            list.add(fileState.getPath());
        }
        assertTrue(list.contains(path("a3/b/dir1")));
        assertFalse(list.contains(path("a3/b/dir2")));
        // This should fail without S3Guard, and succeed with it.
        assertFalse(list.contains(path(("a3/b/dir3-" + (DEFAULT_DELAY_KEY_SUBSTRING)))));
        try {
            RemoteIterator<LocatedFileStatus> old = fs.listFilesAndEmptyDirectories(path("a"), true);
            fail("Recently renamed dir should not be visible");
        } catch (FileNotFoundException e) {
            // expected
        }
    }

    @Test
    public void testConsistentListStatusAfterPut() throws Exception {
        S3AFileSystem fs = getFileSystem();
        // This test will fail if NullMetadataStore (the default) is configured:
        // skip it.
        Assume.assumeTrue(fs.hasMetadataStore());
        // Any S3 keys that contain DELAY_KEY_SUBSTRING will be delayed
        // in listObjects() results via InconsistentS3Client
        Path inconsistentPath = path(("a/b/dir3-" + (DEFAULT_DELAY_KEY_SUBSTRING)));
        Path[] testDirs = new Path[]{ path("a/b/dir1"), path("a/b/dir2"), inconsistentPath };
        for (Path path : testDirs) {
            assertTrue(fs.mkdirs(path));
        }
        FileStatus[] paths = fs.listStatus(path("a/b/"));
        List<Path> list = new ArrayList<>();
        for (FileStatus fileState : paths) {
            list.add(fileState.getPath());
        }
        assertTrue(list.contains(path("a/b/dir1")));
        assertTrue(list.contains(path("a/b/dir2")));
        // This should fail without S3Guard, and succeed with it.
        assertTrue(list.contains(inconsistentPath));
    }

    /**
     * Similar to {@link #testConsistentListStatusAfterPut()}, this tests that the
     * FS listLocatedStatus() call will return consistent list.
     */
    @Test
    public void testConsistentListLocatedStatusAfterPut() throws Exception {
        final S3AFileSystem fs = getFileSystem();
        // This test will fail if NullMetadataStore (the default) is configured:
        // skip it.
        Assume.assumeTrue(fs.hasMetadataStore());
        String rootDir = "doTestConsistentListLocatedStatusAfterPut";
        fs.mkdirs(path(rootDir));
        final int[] numOfPaths = new int[]{ 0, 1, 5 };
        for (int normalPathNum : numOfPaths) {
            for (int delayedPathNum : new int[]{ 0, 2 }) {
                AbstractS3ATestBase.LOG.info("Testing with normalPathNum={}, delayedPathNum={}", normalPathNum, delayedPathNum);
                doTestConsistentListLocatedStatusAfterPut(fs, rootDir, normalPathNum, delayedPathNum);
            }
        }
    }

    /**
     * Tests that the S3AFS listFiles() call will return consistent file list.
     */
    @Test
    public void testConsistentListFiles() throws Exception {
        final S3AFileSystem fs = getFileSystem();
        // This test will fail if NullMetadataStore (the default) is configured:
        // skip it.
        Assume.assumeTrue(fs.hasMetadataStore());
        final int[] numOfPaths = new int[]{ 0, 2 };
        for (int dirNum : numOfPaths) {
            for (int normalFile : numOfPaths) {
                for (int delayedFile : new int[]{ 0, 1 }) {
                    for (boolean recursive : new boolean[]{ true, false }) {
                        doTestListFiles(fs, dirNum, normalFile, delayedFile, recursive);
                    }
                }
            }
        }
    }

    @Test
    public void testCommitByRenameOperations() throws Throwable {
        S3AFileSystem fs = getFileSystem();
        Assume.assumeTrue(fs.hasMetadataStore());
        Path work = path(("test-commit-by-rename-" + (DEFAULT_DELAY_KEY_SUBSTRING)));
        Path task00 = new Path(work, "task00");
        fs.mkdirs(task00);
        String name = "part-00";
        try (FSDataOutputStream out = fs.create(new Path(task00, name), false)) {
            out.writeChars("hello");
        }
        for (FileStatus stat : fs.listStatus(task00)) {
            fs.rename(stat.getPath(), work);
        }
        List<FileStatus> files = new ArrayList<>(2);
        for (FileStatus stat : fs.listStatus(work)) {
            if (stat.isFile()) {
                files.add(stat);
            }
        }
        assertFalse(((("renamed file " + name) + " not found in ") + work), files.isEmpty());
        assertEquals(((("more files found than expected in " + work) + " ") + (ls(work))), 1, files.size());
        FileStatus status = files.get(0);
        assertEquals(("Wrong filename in " + status), name, status.getPath().getName());
    }

    @Test
    public void testInconsistentS3ClientDeletes() throws Throwable {
        // Test only implemented for v2 S3 list API
        Assume.assumeTrue(((getConfiguration().getInt(LIST_VERSION, DEFAULT_LIST_VERSION)) == 2));
        S3AFileSystem fs = getFileSystem();
        Path root = path(("testInconsistentClient" + (DEFAULT_DELAY_KEY_SUBSTRING)));
        for (int i = 0; i < 3; i++) {
            fs.mkdirs(new Path(root, ("dir" + i)));
            ContractTestUtils.touch(fs, new Path(root, ("file" + i)));
            for (int j = 0; j < 3; j++) {
                ContractTestUtils.touch(fs, new Path(new Path(root, ("dir" + i)), ((("file" + i) + "-") + j)));
            }
        }
        clearInconsistency(fs);
        String key = (fs.pathToKey(root)) + "/";
        ListObjectsV2Result preDeleteDelimited = listObjectsV2(fs, key, "/");
        ListObjectsV2Result preDeleteUndelimited = listObjectsV2(fs, key, null);
        fs.delete(root, true);
        ListObjectsV2Result postDeleteDelimited = listObjectsV2(fs, key, "/");
        ListObjectsV2Result postDeleteUndelimited = listObjectsV2(fs, key, null);
        assertListSizeEqual(("InconsistentAmazonS3Client added back objects incorrectly " + "in a non-recursive listing"), preDeleteDelimited.getObjectSummaries(), postDeleteDelimited.getObjectSummaries());
        assertListSizeEqual(("InconsistentAmazonS3Client added back prefixes incorrectly " + "in a non-recursive listing"), preDeleteDelimited.getCommonPrefixes(), postDeleteDelimited.getCommonPrefixes());
        assertListSizeEqual(("InconsistentAmazonS3Client added back objects incorrectly " + "in a recursive listing"), preDeleteUndelimited.getObjectSummaries(), postDeleteUndelimited.getObjectSummaries());
        assertListSizeEqual(("InconsistentAmazonS3Client added back prefixes incorrectly " + "in a recursive listing"), preDeleteUndelimited.getCommonPrefixes(), postDeleteUndelimited.getCommonPrefixes());
    }
}

