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
package org.apache.hadoop.hbase.master.snapshot;


import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.snapshot.SnapshotReferenceUtil;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test that we correctly reload the cache, filter directories, etc.
 */
@Category({ MasterTests.class, MediumTests.class })
public class TestSnapshotFileCache {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestSnapshotFileCache.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestSnapshotFileCache.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static FileSystem fs;

    private static Path rootDir;

    @Test
    public void testLoadAndDelete() throws IOException {
        // don't refresh the cache unless we tell it to
        long period = Long.MAX_VALUE;
        SnapshotFileCache cache = new SnapshotFileCache(TestSnapshotFileCache.fs, TestSnapshotFileCache.rootDir, period, 10000000, "test-snapshot-file-cache-refresh", new TestSnapshotFileCache.SnapshotFiles());
        createAndTestSnapshotV1(cache, "snapshot1a", false, true);
        createAndTestSnapshotV2(cache, "snapshot2a", false, true);
    }

    @Test
    public void testReloadModifiedDirectory() throws IOException {
        // don't refresh the cache unless we tell it to
        long period = Long.MAX_VALUE;
        SnapshotFileCache cache = new SnapshotFileCache(TestSnapshotFileCache.fs, TestSnapshotFileCache.rootDir, period, 10000000, "test-snapshot-file-cache-refresh", new TestSnapshotFileCache.SnapshotFiles());
        createAndTestSnapshotV1(cache, "snapshot1", false, true);
        // now delete the snapshot and add a file with a different name
        createAndTestSnapshotV1(cache, "snapshot1", false, false);
        createAndTestSnapshotV2(cache, "snapshot2", false, true);
        // now delete the snapshot and add a file with a different name
        createAndTestSnapshotV2(cache, "snapshot2", false, false);
    }

    @Test
    public void testSnapshotTempDirReload() throws IOException {
        long period = Long.MAX_VALUE;
        // This doesn't refresh cache until we invoke it explicitly
        SnapshotFileCache cache = new SnapshotFileCache(TestSnapshotFileCache.fs, TestSnapshotFileCache.rootDir, period, 10000000, "test-snapshot-file-cache-refresh", new TestSnapshotFileCache.SnapshotFiles());
        // Add a new non-tmp snapshot
        createAndTestSnapshotV1(cache, "snapshot0v1", false, false);
        createAndTestSnapshotV1(cache, "snapshot0v2", false, false);
    }

    class SnapshotFiles implements SnapshotFileCache.SnapshotFileInspector {
        @Override
        public Collection<String> filesUnderSnapshot(final Path snapshotDir) throws IOException {
            Collection<String> files = new HashSet<>();
            files.addAll(SnapshotReferenceUtil.getHFileNames(TestSnapshotFileCache.UTIL.getConfiguration(), TestSnapshotFileCache.fs, snapshotDir));
            return files;
        }
    }
}

