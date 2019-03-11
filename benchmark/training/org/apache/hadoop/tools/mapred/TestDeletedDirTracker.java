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
package org.apache.hadoop.tools.mapred;


import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.tools.CopyListingFileStatus;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Unit tests of the deleted directory tracker.
 */
@SuppressWarnings("RedundantThrows")
public class TestDeletedDirTracker extends Assert {
    private static final Logger LOG = LoggerFactory.getLogger(TestDeletedDirTracker.class);

    public static final Path ROOT = new Path("hdfs://namenode/");

    public static final Path DIR1 = new Path(TestDeletedDirTracker.ROOT, "dir1");

    public static final Path FILE0 = new Path(TestDeletedDirTracker.ROOT, "file0");

    public static final Path DIR1_FILE1 = new Path(TestDeletedDirTracker.DIR1, "file1");

    public static final Path DIR1_FILE2 = new Path(TestDeletedDirTracker.DIR1, "file2");

    public static final Path DIR1_DIR3 = new Path(TestDeletedDirTracker.DIR1, "dir3");

    public static final Path DIR1_DIR3_DIR4 = new Path(TestDeletedDirTracker.DIR1_DIR3, "dir4");

    public static final Path DIR1_DIR3_DIR4_FILE_3 = new Path(TestDeletedDirTracker.DIR1_DIR3_DIR4, "file1");

    private DeletedDirTracker tracker;

    @Test(expected = IllegalArgumentException.class)
    public void testNoRootDir() throws Throwable {
        shouldDelete(TestDeletedDirTracker.ROOT, true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoRootFile() throws Throwable {
        shouldDelete(dirStatus(TestDeletedDirTracker.ROOT));
    }

    @Test
    public void testFileInRootDir() throws Throwable {
        expectShouldDelete(TestDeletedDirTracker.FILE0, false);
        expectShouldDelete(TestDeletedDirTracker.FILE0, false);
    }

    @Test
    public void testDeleteDir1() throws Throwable {
        expectShouldDelete(TestDeletedDirTracker.DIR1, true);
        expectShouldNotDelete(TestDeletedDirTracker.DIR1, true);
        expectShouldNotDelete(TestDeletedDirTracker.DIR1_FILE1, false);
        expectNotCached(TestDeletedDirTracker.DIR1_FILE1);
        expectShouldNotDelete(TestDeletedDirTracker.DIR1_DIR3, true);
        expectCached(TestDeletedDirTracker.DIR1_DIR3);
        expectShouldNotDelete(TestDeletedDirTracker.DIR1_FILE2, false);
        expectShouldNotDelete(TestDeletedDirTracker.DIR1_DIR3_DIR4_FILE_3, false);
        expectShouldNotDelete(TestDeletedDirTracker.DIR1_DIR3_DIR4, true);
        expectShouldNotDelete(TestDeletedDirTracker.DIR1_DIR3_DIR4, true);
    }

    @Test
    public void testDeleteDirDeep() throws Throwable {
        expectShouldDelete(TestDeletedDirTracker.DIR1, true);
        expectShouldNotDelete(TestDeletedDirTracker.DIR1_DIR3_DIR4_FILE_3, false);
    }

    @Test
    public void testDeletePerfectCache() throws Throwable {
        // run a larger scale test. Also use the ordering we'd expect for a sorted
        // listing, which we implement by sorting the paths
        List<CopyListingFileStatus> statusList = buildStatusList();
        // cache is bigger than the status list
        tracker = new DeletedDirTracker(statusList.size());
        AtomicInteger deletedFiles = new AtomicInteger(0);
        AtomicInteger deletedDirs = new AtomicInteger(0);
        deletePaths(statusList, deletedFiles, deletedDirs);
        Assert.assertEquals(0, deletedFiles.get());
    }

    @Test
    public void testDeleteFullCache() throws Throwable {
        // run a larger scale test. Also use the ordering we'd expect for a sorted
        // listing, which we implement by sorting the paths
        AtomicInteger deletedFiles = new AtomicInteger(0);
        AtomicInteger deletedDirs = new AtomicInteger(0);
        deletePaths(buildStatusList(), deletedFiles, deletedDirs);
        Assert.assertEquals(0, deletedFiles.get());
    }

    @Test
    public void testDeleteMediumCache() throws Throwable {
        tracker = new DeletedDirTracker(100);
        AtomicInteger deletedFiles = new AtomicInteger(0);
        AtomicInteger deletedDirs = new AtomicInteger(0);
        deletePaths(buildStatusList(), deletedFiles, deletedDirs);
        Assert.assertEquals(0, deletedFiles.get());
    }

    @Test
    public void testDeleteFullSmallCache() throws Throwable {
        tracker = new DeletedDirTracker(10);
        AtomicInteger deletedFiles = new AtomicInteger(0);
        AtomicInteger deletedDirs = new AtomicInteger(0);
        deletePaths(buildStatusList(), deletedFiles, deletedDirs);
        Assert.assertEquals(0, deletedFiles.get());
    }
}

