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
package org.apache.hadoop.mapreduce.util;


import java.io.File;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A test for MRAsyncDiskService.
 */
public class TestMRAsyncDiskService {
    public static final Logger LOG = LoggerFactory.getLogger(TestMRAsyncDiskService.class);

    private static String TEST_ROOT_DIR = new Path(System.getProperty("test.build.data", "/tmp")).toString();

    /**
     * Test that the relativeToWorking() method above does what we expect.
     */
    @Test
    public void testRelativeToWorking() {
        Assert.assertEquals(".", relativeToWorking(System.getProperty("user.dir", ".")));
        String cwd = System.getProperty("user.dir", ".");
        Path cwdPath = new Path(cwd);
        Path subdir = new Path(cwdPath, "foo");
        Assert.assertEquals("foo", relativeToWorking(subdir.toUri().getPath()));
        Path subsubdir = new Path(subdir, "bar");
        Assert.assertEquals("foo/bar", relativeToWorking(subsubdir.toUri().getPath()));
        Path parent = new Path(cwdPath, "..");
        Assert.assertEquals("..", relativeToWorking(parent.toUri().getPath()));
        Path sideways = new Path(parent, "baz");
        Assert.assertEquals("../baz", relativeToWorking(sideways.toUri().getPath()));
    }

    /**
     * Test that volumes specified as relative paths are handled properly
     * by MRAsyncDiskService (MAPREDUCE-1887).
     */
    @Test
    public void testVolumeNormalization() throws Throwable {
        TestMRAsyncDiskService.LOG.info(("TEST_ROOT_DIR is " + (TestMRAsyncDiskService.TEST_ROOT_DIR)));
        String relativeTestRoot = relativeToWorking(TestMRAsyncDiskService.TEST_ROOT_DIR);
        FileSystem localFileSystem = FileSystem.getLocal(new Configuration());
        String[] vols = new String[]{ relativeTestRoot + "/0", relativeTestRoot + "/1" };
        // Put a file in one of the volumes to be cleared on startup.
        Path delDir = new Path(vols[0], MRAsyncDiskService.TOBEDELETED);
        localFileSystem.mkdirs(delDir);
        localFileSystem.create(new Path(delDir, "foo")).close();
        MRAsyncDiskService service = new MRAsyncDiskService(localFileSystem, vols);
        makeSureCleanedUp(vols, service);
    }

    /**
     * This test creates some directories and then removes them through
     * MRAsyncDiskService.
     */
    @Test
    public void testMRAsyncDiskService() throws Throwable {
        FileSystem localFileSystem = FileSystem.getLocal(new Configuration());
        String[] vols = new String[]{ (TestMRAsyncDiskService.TEST_ROOT_DIR) + "/0", (TestMRAsyncDiskService.TEST_ROOT_DIR) + "/1" };
        MRAsyncDiskService service = new MRAsyncDiskService(localFileSystem, vols);
        String a = "a";
        String b = "b";
        String c = "b/c";
        String d = "d";
        File fa = new File(vols[0], a);
        File fb = new File(vols[1], b);
        File fc = new File(vols[1], c);
        File fd = new File(vols[1], d);
        // Create the directories
        fa.mkdirs();
        fb.mkdirs();
        fc.mkdirs();
        fd.mkdirs();
        Assert.assertTrue(fa.exists());
        Assert.assertTrue(fb.exists());
        Assert.assertTrue(fc.exists());
        Assert.assertTrue(fd.exists());
        // Move and delete them
        service.moveAndDeleteRelativePath(vols[0], a);
        Assert.assertFalse(fa.exists());
        service.moveAndDeleteRelativePath(vols[1], b);
        Assert.assertFalse(fb.exists());
        Assert.assertFalse(fc.exists());
        Assert.assertFalse(service.moveAndDeleteRelativePath(vols[1], "not_exists"));
        // asyncDiskService is NOT able to delete files outside all volumes.
        IOException ee = null;
        try {
            service.moveAndDeleteAbsolutePath(((TestMRAsyncDiskService.TEST_ROOT_DIR) + "/2"));
        } catch (IOException e) {
            ee = e;
        }
        Assert.assertNotNull(("asyncDiskService should not be able to delete files " + "outside all volumes"), ee);
        // asyncDiskService is able to automatically find the file in one
        // of the volumes.
        Assert.assertTrue(service.moveAndDeleteAbsolutePath((((vols[1]) + (Path.SEPARATOR_CHAR)) + d)));
        // Make sure everything is cleaned up
        makeSureCleanedUp(vols, service);
    }

    /**
     * This test creates some directories inside the volume roots, and then
     * call asyncDiskService.MoveAndDeleteAllVolumes.
     * We should be able to delete all files/dirs inside the volumes except
     * the toBeDeleted directory.
     */
    @Test
    public void testMRAsyncDiskServiceMoveAndDeleteAllVolumes() throws Throwable {
        FileSystem localFileSystem = FileSystem.getLocal(new Configuration());
        String[] vols = new String[]{ (TestMRAsyncDiskService.TEST_ROOT_DIR) + "/0", (TestMRAsyncDiskService.TEST_ROOT_DIR) + "/1" };
        MRAsyncDiskService service = new MRAsyncDiskService(localFileSystem, vols);
        String a = "a";
        String b = "b";
        String c = "b/c";
        String d = "d";
        File fa = new File(vols[0], a);
        File fb = new File(vols[1], b);
        File fc = new File(vols[1], c);
        File fd = new File(vols[1], d);
        // Create the directories
        fa.mkdirs();
        fb.mkdirs();
        fc.mkdirs();
        fd.mkdirs();
        Assert.assertTrue(fa.exists());
        Assert.assertTrue(fb.exists());
        Assert.assertTrue(fc.exists());
        Assert.assertTrue(fd.exists());
        // Delete all of them
        service.cleanupAllVolumes();
        Assert.assertFalse(fa.exists());
        Assert.assertFalse(fb.exists());
        Assert.assertFalse(fc.exists());
        Assert.assertFalse(fd.exists());
        // Make sure everything is cleaned up
        makeSureCleanedUp(vols, service);
    }

    /**
     * This test creates some directories inside the toBeDeleted directory and
     * then start the asyncDiskService.
     * AsyncDiskService will create tasks to delete the content inside the
     * toBeDeleted directories.
     */
    @Test
    public void testMRAsyncDiskServiceStartupCleaning() throws Throwable {
        FileSystem localFileSystem = FileSystem.getLocal(new Configuration());
        String[] vols = new String[]{ (TestMRAsyncDiskService.TEST_ROOT_DIR) + "/0", (TestMRAsyncDiskService.TEST_ROOT_DIR) + "/1" };
        String a = "a";
        String b = "b";
        String c = "b/c";
        String d = "d";
        // Create directories inside SUBDIR
        String suffix = (Path.SEPARATOR_CHAR) + (MRAsyncDiskService.TOBEDELETED);
        File fa = new File(((vols[0]) + suffix), a);
        File fb = new File(((vols[1]) + suffix), b);
        File fc = new File(((vols[1]) + suffix), c);
        File fd = new File(((vols[1]) + suffix), d);
        // Create the directories
        fa.mkdirs();
        fb.mkdirs();
        fc.mkdirs();
        fd.mkdirs();
        Assert.assertTrue(fa.exists());
        Assert.assertTrue(fb.exists());
        Assert.assertTrue(fc.exists());
        Assert.assertTrue(fd.exists());
        // Create the asyncDiskService which will delete all contents inside SUBDIR
        MRAsyncDiskService service = new MRAsyncDiskService(localFileSystem, vols);
        // Make sure everything is cleaned up
        makeSureCleanedUp(vols, service);
    }

    @Test
    public void testToleratesSomeUnwritableVolumes() throws Throwable {
        FileSystem localFileSystem = FileSystem.getLocal(new Configuration());
        String[] vols = new String[]{ (TestMRAsyncDiskService.TEST_ROOT_DIR) + "/0", (TestMRAsyncDiskService.TEST_ROOT_DIR) + "/1" };
        Assert.assertTrue(new File(vols[0]).mkdirs());
        Assert.assertEquals(0, FileUtil.chmod(vols[0], "400"));// read only

        try {
            new MRAsyncDiskService(localFileSystem, vols);
        } finally {
            FileUtil.chmod(vols[0], "755");// make writable again

        }
    }
}

