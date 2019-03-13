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
package org.apache.hadoop.hdfs.server.namenode.ha;


import DFSConfigKeys.DFS_PROVIDED_ALIASMAP_INMEMORY_LEVELDB_DIR;
import DFSConfigKeys.DFS_PROVIDED_ALIASMAP_INMEMORY_RPC_ADDRESS;
import java.io.File;
import java.net.URL;
import java.util.Optional;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSUtil;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.protocol.Block;
import org.apache.hadoop.hdfs.server.aliasmap.InMemoryLevelDBAliasMapServer;
import org.apache.hadoop.hdfs.server.namenode.NameNode;
import org.apache.hadoop.hdfs.server.namenode.TransferFsImage;
import org.apache.hadoop.net.NetUtils;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test for aliasmap bootstrap.
 */
public class TestBootstrapAliasmap {
    private MiniDFSCluster cluster;

    @Test
    public void testAliasmapBootstrap() throws Exception {
        InMemoryLevelDBAliasMapServer aliasMapServer = cluster.getNameNode().getAliasMapServer();
        // write some blocks to the aliasmap.
        Block block1 = new Block(1000);
        Block block2 = new Block(1002);
        Path path = new Path("/test1.dat");
        aliasMapServer.write(new Block(block1), new org.apache.hadoop.hdfs.protocol.ProvidedStorageLocation(path, 0, 100, new byte[0]));
        aliasMapServer.write(new Block(block2), new org.apache.hadoop.hdfs.protocol.ProvidedStorageLocation(path, 101, 200, new byte[0]));
        File newLocation = GenericTestUtils.getRandomizedTestDir();
        NameNode nn = cluster.getNameNode();
        Configuration conf = cluster.getConfiguration(0);
        String scheme = DFSUtil.getHttpClientScheme(conf);
        URL nnHttpURL = DFSUtil.getInfoServerWithDefaultHost(nn.getNameNodeAddress().getHostName(), conf, scheme).toURL();
        // transfer the aliasmap.
        newLocation.mkdirs();
        TransferFsImage.downloadAliasMap(nnHttpURL, newLocation, true);
        // create config for new aliasmap server at the new location.
        Configuration newConfig = new Configuration();
        newConfig.set(DFS_PROVIDED_ALIASMAP_INMEMORY_LEVELDB_DIR, newLocation.getAbsolutePath());
        newConfig.set(DFS_PROVIDED_ALIASMAP_INMEMORY_RPC_ADDRESS, ("127.0.0.1:" + (NetUtils.getFreeSocketPort())));
        String blockPoolId = nn.getNamesystem().getBlockPoolId();
        InMemoryLevelDBAliasMapServer newServer = new InMemoryLevelDBAliasMapServer(InMemoryAliasMap::init, blockPoolId);
        newServer.setConf(newConfig);
        newServer.start();
        // the server should have only 2 blocks.
        Assert.assertEquals(2, newServer.list(Optional.empty()).getFileRegions().size());
        Assert.assertNotNull(newServer.read(block1));
        Assert.assertNotNull(newServer.read(block2));
        Assert.assertEquals(blockPoolId, newServer.getBlockPoolId());
    }
}

