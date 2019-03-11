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
package org.apache.hadoop.hdfs.server.namenode.snapshot;


import DiffListBySkipList.LOG;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.ToIntBiFunction;
import java.util.function.ToIntFunction;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.server.namenode.FSDirectory;
import org.apache.hadoop.hdfs.server.namenode.FSNamesystem;
import org.junit.Test;


/**
 * This class tests the DirectoryDiffList API's.
 */
public class TestDiffListBySkipList {
    static final int NUM_SNAPSHOTS = 100;

    static final int MAX_LEVEL = 5;

    static {
        SnapshotTestHelper.disableLogs();
    }

    private static final Configuration CONF = new Configuration();

    private static MiniDFSCluster cluster;

    private static FSNamesystem fsn;

    private static FSDirectory fsdir;

    private static DistributedFileSystem hdfs;

    @Test
    public void testAddLast() throws Exception {
        TestDiffListBySkipList.testAddLast(TestDiffListBySkipList.NUM_SNAPSHOTS);
    }

    @Test
    public void testAddFirst() throws Exception {
        TestDiffListBySkipList.testAddFirst(TestDiffListBySkipList.NUM_SNAPSHOTS);
    }

    @Test
    public void testRemoveFromTail() throws Exception {
        final int n = TestDiffListBySkipList.NUM_SNAPSHOTS;
        TestDiffListBySkipList.testRemove("FromTail", n, ( i) -> (n - 1) - i);
    }

    @Test
    public void testReomveFromHead() throws Exception {
        TestDiffListBySkipList.testRemove("FromHead", TestDiffListBySkipList.NUM_SNAPSHOTS, ( i) -> 0);
    }

    @Test
    public void testRemoveRandom() throws Exception {
        final int n = TestDiffListBySkipList.NUM_SNAPSHOTS;
        TestDiffListBySkipList.testRemove("Random", n, ( i) -> ThreadLocalRandom.current().nextInt((n - i)));
    }

    @Test
    public void testRemoveFromLowerLevel() throws Exception {
        TestDiffListBySkipList.testRemove("FromLowerLevel", TestDiffListBySkipList.NUM_SNAPSHOTS, new ToIntBiFunction<DiffListBySkipList, Integer>() {
            private int level = 0;

            @Override
            public int applyAsInt(DiffListBySkipList skipList, Integer integer) {
                for (; (level) <= (TestDiffListBySkipList.MAX_LEVEL); (level)++) {
                    final int index = TestDiffListBySkipList.findIndex(skipList, level);
                    if (index != (-1)) {
                        return index;
                    }
                }
                return -1;
            }
        });
    }

    @Test
    public void testRemoveFromUpperLevel() throws Exception {
        TestDiffListBySkipList.testRemove("FromUpperLevel", TestDiffListBySkipList.NUM_SNAPSHOTS, new ToIntBiFunction<DiffListBySkipList, Integer>() {
            private int level = TestDiffListBySkipList.MAX_LEVEL;

            @Override
            public int applyAsInt(DiffListBySkipList skipList, Integer integer) {
                for (; (level) >= 0; (level)--) {
                    final int index = TestDiffListBySkipList.findIndex(skipList, level);
                    if (index != (-1)) {
                        return index;
                    }
                    LOG.info(("change from level " + (level)));
                }
                return -1;
            }
        });
    }
}

