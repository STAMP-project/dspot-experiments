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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.test.PlatformAssumptions;
import org.apache.hadoop.util.DiskChecker.DiskErrorException;
import org.apache.hadoop.util.Shell;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static Path.SEPARATOR;


/**
 * This test LocalDirAllocator works correctly;
 * Every test case uses different buffer dirs to
 * enforce the AllocatorPerContext initialization.
 * This test does not run on Cygwin because under Cygwin
 * a directory can be created in a read-only directory
 * which breaks this test.
 */
@RunWith(Parameterized.class)
public class TestLocalDirAllocator {
    private static final Configuration conf = new Configuration();

    private static final String BUFFER_DIR_ROOT = "build/test/temp";

    private static final String ABSOLUTE_DIR_ROOT;

    private static final String QUALIFIED_DIR_ROOT;

    private static final Path BUFFER_PATH_ROOT = new Path(TestLocalDirAllocator.BUFFER_DIR_ROOT);

    private static final File BUFFER_ROOT = new File(TestLocalDirAllocator.BUFFER_DIR_ROOT);

    private static final String CONTEXT = "mapred.local.dir";

    private static final String FILENAME = "block";

    private static final LocalDirAllocator dirAllocator = new LocalDirAllocator(TestLocalDirAllocator.CONTEXT);

    static LocalFileSystem localFs;

    static final int SMALL_FILE_SIZE = 100;

    private static final String RELATIVE = "/RELATIVE";

    private static final String ABSOLUTE = "/ABSOLUTE";

    private static final String QUALIFIED = "/QUALIFIED";

    private final String ROOT;

    private final String PREFIX;

    static {
        try {
            TestLocalDirAllocator.localFs = FileSystem.getLocal(TestLocalDirAllocator.conf);
            TestLocalDirAllocator.rmBufferDirs();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            System.exit((-1));
        }
        // absolute path in test environment
        // /home/testuser/src/hadoop-common-project/hadoop-common/build/test/temp
        ABSOLUTE_DIR_ROOT = toUri().getPath();
        // file:/home/testuser/src/hadoop-common-project/hadoop-common/build/test/temp
        QUALIFIED_DIR_ROOT = toUri().toString();
    }

    public TestLocalDirAllocator(String root, String prefix) {
        ROOT = root;
        PREFIX = prefix;
    }

    /**
     * Two buffer dirs. The first dir does not exist & is on a read-only disk;
     * The second dir exists & is RW
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 30000)
    public void test0() throws Exception {
        PlatformAssumptions.assumeNotWindows();
        String dir0 = buildBufferDir(ROOT, 0);
        String dir1 = buildBufferDir(ROOT, 1);
        try {
            TestLocalDirAllocator.conf.set(TestLocalDirAllocator.CONTEXT, ((dir0 + ",") + dir1));
            Assert.assertTrue(TestLocalDirAllocator.localFs.mkdirs(new Path(dir1)));
            TestLocalDirAllocator.BUFFER_ROOT.setReadOnly();
            TestLocalDirAllocator.validateTempDirCreation(dir1);
            TestLocalDirAllocator.validateTempDirCreation(dir1);
        } finally {
            Shell.execCommand(Shell.getSetPermissionCommand("u+w", false, TestLocalDirAllocator.BUFFER_DIR_ROOT));
            TestLocalDirAllocator.rmBufferDirs();
        }
    }

    /**
     * Two buffer dirs. The first dir exists & is on a read-only disk;
     * The second dir exists & is RW
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 30000)
    public void testROBufferDirAndRWBufferDir() throws Exception {
        PlatformAssumptions.assumeNotWindows();
        String dir1 = buildBufferDir(ROOT, 1);
        String dir2 = buildBufferDir(ROOT, 2);
        try {
            TestLocalDirAllocator.conf.set(TestLocalDirAllocator.CONTEXT, ((dir1 + ",") + dir2));
            Assert.assertTrue(TestLocalDirAllocator.localFs.mkdirs(new Path(dir2)));
            TestLocalDirAllocator.BUFFER_ROOT.setReadOnly();
            TestLocalDirAllocator.validateTempDirCreation(dir2);
            TestLocalDirAllocator.validateTempDirCreation(dir2);
        } finally {
            Shell.execCommand(Shell.getSetPermissionCommand("u+w", false, TestLocalDirAllocator.BUFFER_DIR_ROOT));
            TestLocalDirAllocator.rmBufferDirs();
        }
    }

    /**
     * Two buffer dirs. Both do not exist but on a RW disk.
     * Check if tmp dirs are allocated in a round-robin
     */
    @Test(timeout = 30000)
    public void testDirsNotExist() throws Exception {
        PlatformAssumptions.assumeNotWindows();
        String dir2 = buildBufferDir(ROOT, 2);
        String dir3 = buildBufferDir(ROOT, 3);
        try {
            TestLocalDirAllocator.conf.set(TestLocalDirAllocator.CONTEXT, ((dir2 + ",") + dir3));
            // create the first file, and then figure the round-robin sequence
            TestLocalDirAllocator.createTempFile(TestLocalDirAllocator.SMALL_FILE_SIZE);
            int firstDirIdx = ((TestLocalDirAllocator.dirAllocator.getCurrentDirectoryIndex()) == 0) ? 2 : 3;
            int secondDirIdx = (firstDirIdx == 2) ? 3 : 2;
            // check if tmp dirs are allocated in a round-robin manner
            TestLocalDirAllocator.validateTempDirCreation(buildBufferDir(ROOT, firstDirIdx));
            TestLocalDirAllocator.validateTempDirCreation(buildBufferDir(ROOT, secondDirIdx));
            TestLocalDirAllocator.validateTempDirCreation(buildBufferDir(ROOT, firstDirIdx));
        } finally {
            TestLocalDirAllocator.rmBufferDirs();
        }
    }

    /**
     * Two buffer dirs. Both exists and on a R/W disk.
     * Later disk1 becomes read-only.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 30000)
    public void testRWBufferDirBecomesRO() throws Exception {
        PlatformAssumptions.assumeNotWindows();
        String dir3 = buildBufferDir(ROOT, 3);
        String dir4 = buildBufferDir(ROOT, 4);
        try {
            TestLocalDirAllocator.conf.set(TestLocalDirAllocator.CONTEXT, ((dir3 + ",") + dir4));
            Assert.assertTrue(TestLocalDirAllocator.localFs.mkdirs(new Path(dir3)));
            Assert.assertTrue(TestLocalDirAllocator.localFs.mkdirs(new Path(dir4)));
            // Create the first small file
            TestLocalDirAllocator.createTempFile(TestLocalDirAllocator.SMALL_FILE_SIZE);
            // Determine the round-robin sequence
            int nextDirIdx = ((TestLocalDirAllocator.dirAllocator.getCurrentDirectoryIndex()) == 0) ? 3 : 4;
            TestLocalDirAllocator.validateTempDirCreation(buildBufferDir(ROOT, nextDirIdx));
            // change buffer directory 2 to be read only
            new File(new Path(dir4).toUri().getPath()).setReadOnly();
            TestLocalDirAllocator.validateTempDirCreation(dir3);
            TestLocalDirAllocator.validateTempDirCreation(dir3);
        } finally {
            TestLocalDirAllocator.rmBufferDirs();
        }
    }

    /**
     * Two buffer dirs, on read-write disk.
     *
     * Try to create a whole bunch of files.
     *  Verify that they do indeed all get created where they should.
     *
     *  Would ideally check statistical properties of distribution, but
     *  we don't have the nerve to risk false-positives here.
     *
     * @throws Exception
     * 		
     */
    static final int TRIALS = 100;

    @Test(timeout = 30000)
    public void testCreateManyFiles() throws Exception {
        PlatformAssumptions.assumeNotWindows();
        String dir5 = buildBufferDir(ROOT, 5);
        String dir6 = buildBufferDir(ROOT, 6);
        try {
            TestLocalDirAllocator.conf.set(TestLocalDirAllocator.CONTEXT, ((dir5 + ",") + dir6));
            Assert.assertTrue(TestLocalDirAllocator.localFs.mkdirs(new Path(dir5)));
            Assert.assertTrue(TestLocalDirAllocator.localFs.mkdirs(new Path(dir6)));
            int inDir5 = 0;
            int inDir6 = 0;
            for (int i = 0; i < (TestLocalDirAllocator.TRIALS); ++i) {
                File result = TestLocalDirAllocator.createTempFile();
                if (result.getPath().startsWith(new Path(dir5, TestLocalDirAllocator.FILENAME).toUri().getPath())) {
                    inDir5++;
                } else
                    if (result.getPath().startsWith(new Path(dir6, TestLocalDirAllocator.FILENAME).toUri().getPath())) {
                        inDir6++;
                    }

                result.delete();
            }
            Assert.assertTrue(((inDir5 + inDir6) == (TestLocalDirAllocator.TRIALS)));
        } finally {
            TestLocalDirAllocator.rmBufferDirs();
        }
    }

    /**
     * Five buffer dirs, on read-write disk.
     *
     * Try to create a whole bunch of files.
     *  Verify that each successive creation uses a different disk
     *  than the previous one (for sized requests).
     *
     *  Would ideally check statistical properties of distribution, but
     *  we don't have the nerve to risk false-positives here.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 30000)
    public void testCreateManyFilesRandom() throws Exception {
        PlatformAssumptions.assumeNotWindows();
        final int numDirs = 5;
        final int numTries = 100;
        String[] dirs = new String[numDirs];
        for (int d = 0; d < numDirs; ++d) {
            dirs[d] = buildBufferDir(ROOT, d);
        }
        boolean next_dir_not_selected_at_least_once = false;
        try {
            TestLocalDirAllocator.conf.set(TestLocalDirAllocator.CONTEXT, (((((((((dirs[0]) + ",") + (dirs[1])) + ",") + (dirs[2])) + ",") + (dirs[3])) + ",") + (dirs[4])));
            Path[] paths = new Path[5];
            for (int d = 0; d < numDirs; ++d) {
                paths[d] = new Path(dirs[d]);
                Assert.assertTrue(TestLocalDirAllocator.localFs.mkdirs(paths[d]));
            }
            int inDir = 0;
            int prevDir = -1;
            int[] counts = new int[5];
            for (int i = 0; i < numTries; ++i) {
                File result = TestLocalDirAllocator.createTempFile(TestLocalDirAllocator.SMALL_FILE_SIZE);
                for (int d = 0; d < numDirs; ++d) {
                    if (result.getPath().startsWith(toUri().getPath())) {
                        inDir = d;
                        break;
                    }
                }
                // Verify we always select a different dir
                Assert.assertNotEquals(prevDir, inDir);
                // Verify we are not always selecting the next dir - that was the old
                // algorithm.
                if ((prevDir != (-1)) && (inDir != ((prevDir + 1) % numDirs))) {
                    next_dir_not_selected_at_least_once = true;
                }
                prevDir = inDir;
                (counts[inDir])++;
                result.delete();
            }
        } finally {
            TestLocalDirAllocator.rmBufferDirs();
        }
        Assert.assertTrue(next_dir_not_selected_at_least_once);
    }

    /**
     * Two buffer dirs. The first dir does not exist & is on a read-only disk;
     * The second dir exists & is RW
     * getLocalPathForWrite with checkAccess set to false should create a parent
     * directory. With checkAccess true, the directory should not be created.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 30000)
    public void testLocalPathForWriteDirCreation() throws IOException {
        String dir0 = buildBufferDir(ROOT, 0);
        String dir1 = buildBufferDir(ROOT, 1);
        try {
            TestLocalDirAllocator.conf.set(TestLocalDirAllocator.CONTEXT, ((dir0 + ",") + dir1));
            Assert.assertTrue(TestLocalDirAllocator.localFs.mkdirs(new Path(dir1)));
            TestLocalDirAllocator.BUFFER_ROOT.setReadOnly();
            Path p1 = TestLocalDirAllocator.dirAllocator.getLocalPathForWrite("p1/x", TestLocalDirAllocator.SMALL_FILE_SIZE, TestLocalDirAllocator.conf);
            Assert.assertTrue(TestLocalDirAllocator.localFs.getFileStatus(p1.getParent()).isDirectory());
            Path p2 = TestLocalDirAllocator.dirAllocator.getLocalPathForWrite("p2/x", TestLocalDirAllocator.SMALL_FILE_SIZE, TestLocalDirAllocator.conf, false);
            try {
                TestLocalDirAllocator.localFs.getFileStatus(p2.getParent());
            } catch (Exception e) {
                Assert.assertEquals(e.getClass(), FileNotFoundException.class);
            }
        } finally {
            Shell.execCommand(Shell.getSetPermissionCommand("u+w", false, TestLocalDirAllocator.BUFFER_DIR_ROOT));
            TestLocalDirAllocator.rmBufferDirs();
        }
    }

    /* Test when mapred.local.dir not configured and called
    getLocalPathForWrite
     */
    @Test(timeout = 30000)
    public void testShouldNotthrowNPE() throws Exception {
        Configuration conf1 = new Configuration();
        try {
            TestLocalDirAllocator.dirAllocator.getLocalPathForWrite("/test", conf1);
            Assert.fail((("Exception not thrown when " + (TestLocalDirAllocator.CONTEXT)) + " is not set"));
        } catch (IOException e) {
            Assert.assertEquals(((TestLocalDirAllocator.CONTEXT) + " not configured"), e.getMessage());
        } catch (NullPointerException e) {
            Assert.fail("Lack of configuration should not have thrown a NPE.");
        }
        String NEW_CONTEXT = (TestLocalDirAllocator.CONTEXT) + ".new";
        conf1.set(NEW_CONTEXT, "");
        LocalDirAllocator newDirAllocator = new LocalDirAllocator(NEW_CONTEXT);
        try {
            newDirAllocator.getLocalPathForWrite("/test", conf1);
            Assert.fail((("Exception not thrown when " + NEW_CONTEXT) + " is set to empty string"));
        } catch (IOException e) {
            Assert.assertTrue((e instanceof DiskErrorException));
        } catch (NullPointerException e) {
            Assert.fail("Wrong configuration should not have thrown a NPE.");
        }
        try {
            newDirAllocator.getLocalPathToRead("/test", conf1);
            Assert.fail((("Exception not thrown when " + NEW_CONTEXT) + " is set to empty string"));
        } catch (IOException e) {
            Assert.assertTrue((e instanceof DiskErrorException));
        } catch (NullPointerException e) {
            Assert.fail("Wrong configuration should not have thrown a NPE.");
        }
    }

    /**
     * Test no side effect files are left over. After creating a temp
     * temp file, remove both the temp file and its parent. Verify that
     * no files or directories are left over as can happen when File objects
     * are mistakenly created from fully qualified path strings.
     *
     * @throws IOException
     * 		
     */
    @Test(timeout = 30000)
    public void testNoSideEffects() throws IOException {
        PlatformAssumptions.assumeNotWindows();
        String dir = buildBufferDir(ROOT, 0);
        try {
            TestLocalDirAllocator.conf.set(TestLocalDirAllocator.CONTEXT, dir);
            File result = TestLocalDirAllocator.dirAllocator.createTmpFileForWrite(TestLocalDirAllocator.FILENAME, (-1), TestLocalDirAllocator.conf);
            Assert.assertTrue(result.delete());
            Assert.assertTrue(result.getParentFile().delete());
            Assert.assertFalse(new File(dir).exists());
        } finally {
            Shell.execCommand(Shell.getSetPermissionCommand("u+w", false, TestLocalDirAllocator.BUFFER_DIR_ROOT));
            TestLocalDirAllocator.rmBufferDirs();
        }
    }

    /**
     * Test getLocalPathToRead() returns correct filename and "file" schema.
     *
     * @throws IOException
     * 		
     */
    @Test(timeout = 30000)
    public void testGetLocalPathToRead() throws IOException {
        PlatformAssumptions.assumeNotWindows();
        String dir = buildBufferDir(ROOT, 0);
        try {
            TestLocalDirAllocator.conf.set(TestLocalDirAllocator.CONTEXT, dir);
            Assert.assertTrue(TestLocalDirAllocator.localFs.mkdirs(new Path(dir)));
            File f1 = TestLocalDirAllocator.dirAllocator.createTmpFileForWrite(TestLocalDirAllocator.FILENAME, TestLocalDirAllocator.SMALL_FILE_SIZE, TestLocalDirAllocator.conf);
            Path p1 = TestLocalDirAllocator.dirAllocator.getLocalPathToRead(f1.getName(), TestLocalDirAllocator.conf);
            Assert.assertEquals(f1.getName(), p1.getName());
            Assert.assertEquals("file", p1.getFileSystem(TestLocalDirAllocator.conf).getUri().getScheme());
        } finally {
            Shell.execCommand(Shell.getSetPermissionCommand("u+w", false, TestLocalDirAllocator.BUFFER_DIR_ROOT));
            TestLocalDirAllocator.rmBufferDirs();
        }
    }

    /**
     * Test that {@link LocalDirAllocator#getAllLocalPathsToRead(String, Configuration)}
     * returns correct filenames and "file" schema.
     *
     * @throws IOException
     * 		
     */
    @Test(timeout = 30000)
    public void testGetAllLocalPathsToRead() throws IOException {
        PlatformAssumptions.assumeNotWindows();
        String dir0 = buildBufferDir(ROOT, 0);
        String dir1 = buildBufferDir(ROOT, 1);
        try {
            TestLocalDirAllocator.conf.set(TestLocalDirAllocator.CONTEXT, ((dir0 + ",") + dir1));
            Assert.assertTrue(TestLocalDirAllocator.localFs.mkdirs(new Path(dir0)));
            Assert.assertTrue(TestLocalDirAllocator.localFs.mkdirs(new Path(dir1)));
            TestLocalDirAllocator.localFs.create(new Path(((dir0 + (SEPARATOR)) + (TestLocalDirAllocator.FILENAME))));
            TestLocalDirAllocator.localFs.create(new Path(((dir1 + (SEPARATOR)) + (TestLocalDirAllocator.FILENAME))));
            // check both the paths are returned as paths to read:
            final Iterable<Path> pathIterable = TestLocalDirAllocator.dirAllocator.getAllLocalPathsToRead(TestLocalDirAllocator.FILENAME, TestLocalDirAllocator.conf);
            int count = 0;
            for (final Path p : pathIterable) {
                count++;
                Assert.assertEquals(TestLocalDirAllocator.FILENAME, p.getName());
                Assert.assertEquals("file", p.getFileSystem(TestLocalDirAllocator.conf).getUri().getScheme());
            }
            Assert.assertEquals(2, count);
            // test #next() while no element to iterate any more:
            try {
                Path p = pathIterable.iterator().next();
                Assert.assertFalse((("NoSuchElementException must be thrown, but returned [" + p) + "] instead."), true);// exception expected

            } catch (NoSuchElementException nsee) {
                // okay
            }
            // test modification not allowed:
            final Iterable<Path> pathIterable2 = TestLocalDirAllocator.dirAllocator.getAllLocalPathsToRead(TestLocalDirAllocator.FILENAME, TestLocalDirAllocator.conf);
            final Iterator<Path> it = pathIterable2.iterator();
            try {
                it.remove();
                Assert.assertFalse(true);// exception expected

            } catch (UnsupportedOperationException uoe) {
                // okay
            }
        } finally {
            Shell.execCommand(new String[]{ "chmod", "u+w", TestLocalDirAllocator.BUFFER_DIR_ROOT });
            TestLocalDirAllocator.rmBufferDirs();
        }
    }

    @Test(timeout = 30000)
    public void testRemoveContext() throws IOException {
        String dir = buildBufferDir(ROOT, 0);
        try {
            String contextCfgItemName = "application_1340842292563_0004.app.cache.dirs";
            TestLocalDirAllocator.conf.set(contextCfgItemName, dir);
            LocalDirAllocator localDirAllocator = new LocalDirAllocator(contextCfgItemName);
            localDirAllocator.getLocalPathForWrite("p1/x", TestLocalDirAllocator.SMALL_FILE_SIZE, TestLocalDirAllocator.conf);
            Assert.assertTrue(LocalDirAllocator.isContextValid(contextCfgItemName));
            LocalDirAllocator.removeContext(contextCfgItemName);
            Assert.assertFalse(LocalDirAllocator.isContextValid(contextCfgItemName));
        } finally {
            TestLocalDirAllocator.rmBufferDirs();
        }
    }

    /**
     * Test to check the LocalDirAllocation for the invalid path HADOOP-8437
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 30000)
    public void testGetLocalPathForWriteForInvalidPaths() throws Exception {
        TestLocalDirAllocator.conf.set(TestLocalDirAllocator.CONTEXT, " ");
        try {
            TestLocalDirAllocator.dirAllocator.getLocalPathForWrite("/test", TestLocalDirAllocator.conf);
            Assert.fail("not throwing the exception");
        } catch (IOException e) {
            Assert.assertEquals("Incorrect exception message", "No space available in any of the local directories.", e.getMessage());
        }
    }
}

