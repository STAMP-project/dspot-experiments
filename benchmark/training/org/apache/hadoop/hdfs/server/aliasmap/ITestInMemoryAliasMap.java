/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hdfs.server.aliasmap;


import DFSConfigKeys.DFS_PROVIDED_ALIASMAP_INMEMORY_LEVELDB_DIR;
import InMemoryAliasMap.IterationResult;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.protocol.Block;
import org.apache.hadoop.hdfs.protocol.ProvidedStorageLocation;
import org.junit.Assert;
import org.junit.Test;


/**
 * ITestInMemoryAliasMap is an integration test that writes and reads to
 * an AliasMap. This is an integration test because it can't be run in parallel
 * like normal unit tests since there is conflict over the port being in use.
 */
public class ITestInMemoryAliasMap {
    private InMemoryAliasMap aliasMap;

    private File tempDirectory;

    private static String bpid = "bpid-0";

    @Test
    public void readNotFoundReturnsNothing() throws IOException {
        Block block = new Block(42, 43, 44);
        Optional<ProvidedStorageLocation> actualProvidedStorageLocationOpt = aliasMap.read(block);
        Assert.assertFalse(actualProvidedStorageLocationOpt.isPresent());
    }

    @Test
    public void readWrite() throws Exception {
        Block block = new Block(42, 43, 44);
        Path path = new Path("eagle", "mouse");
        long offset = 47;
        long length = 48;
        int nonceSize = 4;
        byte[] nonce = new byte[nonceSize];
        Arrays.fill(nonce, 0, (nonceSize - 1), Byte.parseByte("0011", 2));
        ProvidedStorageLocation expectedProvidedStorageLocation = new ProvidedStorageLocation(path, offset, length, nonce);
        aliasMap.write(block, expectedProvidedStorageLocation);
        Optional<ProvidedStorageLocation> actualProvidedStorageLocationOpt = aliasMap.read(block);
        Assert.assertTrue(actualProvidedStorageLocationOpt.isPresent());
        Assert.assertEquals(expectedProvidedStorageLocation, actualProvidedStorageLocationOpt.get());
    }

    @Test
    public void list() throws IOException {
        Block block1 = new Block(42, 43, 44);
        Block block2 = new Block(43, 44, 45);
        Block block3 = new Block(44, 45, 46);
        Path path = new Path("eagle", "mouse");
        int nonceSize = 4;
        byte[] nonce = new byte[nonceSize];
        Arrays.fill(nonce, 0, (nonceSize - 1), Byte.parseByte("0011", 2));
        ProvidedStorageLocation expectedProvidedStorageLocation1 = new ProvidedStorageLocation(path, 47, 48, nonce);
        ProvidedStorageLocation expectedProvidedStorageLocation2 = new ProvidedStorageLocation(path, 48, 49, nonce);
        ProvidedStorageLocation expectedProvidedStorageLocation3 = new ProvidedStorageLocation(path, 49, 50, nonce);
        aliasMap.write(block1, expectedProvidedStorageLocation1);
        aliasMap.write(block2, expectedProvidedStorageLocation2);
        aliasMap.write(block3, expectedProvidedStorageLocation3);
        InMemoryAliasMap.IterationResult list = aliasMap.list(Optional.empty());
        // we should have 3 results
        Assert.assertEquals(3, list.getFileRegions().size());
        // no more results expected
        Assert.assertFalse(list.getNextBlock().isPresent());
    }

    @Test
    public void testSnapshot() throws Exception {
        Block block1 = new Block(100);
        Block block2 = new Block(200);
        Path path = new Path("users", "alice");
        ProvidedStorageLocation remoteLocation = new ProvidedStorageLocation(path, 0, 1000, new byte[0]);
        // write the first block
        aliasMap.write(block1, remoteLocation);
        // create snapshot
        File snapshotFile = InMemoryAliasMap.createSnapshot(aliasMap);
        // write the 2nd block after the snapshot
        aliasMap.write(block2, remoteLocation);
        // creata a new aliasmap object from the snapshot
        InMemoryAliasMap snapshotAliasMap = null;
        Configuration newConf = new Configuration();
        newConf.set(DFS_PROVIDED_ALIASMAP_INMEMORY_LEVELDB_DIR, snapshotFile.getAbsolutePath());
        try {
            snapshotAliasMap = InMemoryAliasMap.init(newConf, ITestInMemoryAliasMap.bpid);
            // now the snapshot should have the first block but not the second one.
            Assert.assertTrue(snapshotAliasMap.read(block1).isPresent());
            Assert.assertFalse(snapshotAliasMap.read(block2).isPresent());
        } finally {
            if (snapshotAliasMap != null) {
                snapshotAliasMap.close();
            }
        }
    }
}

