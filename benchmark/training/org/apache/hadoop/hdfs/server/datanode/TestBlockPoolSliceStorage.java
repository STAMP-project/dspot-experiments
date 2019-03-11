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
package org.apache.hadoop.hdfs.server.datanode;


import java.io.File;
import java.util.Random;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test that BlockPoolSliceStorage can correctly generate trash and
 * restore directories for a given block file path.
 */
public class TestBlockPoolSliceStorage {
    public static final Logger LOG = LoggerFactory.getLogger(TestBlockPoolSliceStorage.class);

    final Random rand = new Random();

    BlockPoolSliceStorage storage;

    /**
     * BlockPoolSliceStorage with a dummy storage directory. The directory
     * need not exist. We need to extend BlockPoolSliceStorage so we can
     * call {@link Storage#addStorageDir}.
     */
    private static class StubBlockPoolSliceStorage extends BlockPoolSliceStorage {
        StubBlockPoolSliceStorage(int namespaceID, String bpID, long cTime, String clusterId) {
            super(namespaceID, bpID, cTime, clusterId);
            addStorageDir(new StorageDirectory(new File(("/tmp/dontcare/" + bpID))));
            Assert.assertThat(getStorageDirs().size(), Is.is(1));
        }
    }

    @Test(timeout = 300000)
    public void testGetTrashAndRestoreDirectories() {
        storage = makeBlockPoolStorage();
        // Test a few different nesting levels since block files
        // could be nested such as subdir1/subdir5/blk_...
        // Make sure all nesting levels are handled correctly.
        for (int i = 0; i < 3; ++i) {
            getTrashDirectoryForBlockFile("blk_myblockfile", i);
            getTrashDirectoryForBlockFile("blk_myblockfile.meta", i);
            getRestoreDirectoryForBlockFile("blk_myblockfile", i);
            getRestoreDirectoryForBlockFile("blk_myblockfile.meta", i);
        }
    }
}

