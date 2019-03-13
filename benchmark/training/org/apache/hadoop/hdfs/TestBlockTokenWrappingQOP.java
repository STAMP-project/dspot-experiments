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
package org.apache.hadoop.hdfs;


import CreateFlag.APPEND;
import HdfsConstants.GRANDFATHER_INODE_ID;
import java.util.EnumSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.protocol.LastBlockWithStatus;
import org.apache.hadoop.hdfs.protocol.LocatedBlock;
import org.apache.hadoop.hdfs.protocol.LocatedBlocks;
import org.apache.hadoop.hdfs.protocol.datatransfer.sasl.SaslDataTransferTestCase;
import org.apache.hadoop.hdfs.security.token.block.BlockKey;
import org.apache.hadoop.security.TestPermission;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * This tests enabling NN sending the established QOP back to client,
 * in encrypted message, using block access token key.
 */
@RunWith(Parameterized.class)
public class TestBlockTokenWrappingQOP extends SaslDataTransferTestCase {
    public static final Log LOG = LogFactory.getLog(TestPermission.class);

    private HdfsConfiguration conf;

    private MiniDFSCluster cluster;

    private String encryptionAlgorithm;

    private DistributedFileSystem dfs;

    private String configKey;

    private String qopValue;

    public TestBlockTokenWrappingQOP(String configKey, String qopValue) {
        this.configKey = configKey;
        this.qopValue = qopValue;
    }

    @Test
    public void testAddBlockWrappingQOP() throws Exception {
        final String src = "/testAddBlockWrappingQOP";
        final Path path = new Path(src);
        dfs = cluster.getFileSystem();
        dfs.create(path);
        DFSClient client = dfs.getClient();
        String clientName = client.getClientName();
        LocatedBlock lb = client.namenode.addBlock(src, clientName, null, null, GRANDFATHER_INODE_ID, null, null);
        byte[] secret = lb.getBlockToken().getDnHandshakeSecret();
        BlockKey currentKey = cluster.getNamesystem().getBlockManager().getBlockTokenSecretManager().getCurrentKey();
        String decrypted = decryptMessage(secret, currentKey, encryptionAlgorithm);
        Assert.assertEquals(this.qopValue, decrypted);
    }

    @Test
    public void testAppendWrappingQOP() throws Exception {
        final String src = "/testAppendWrappingQOP";
        final Path path = new Path(src);
        dfs = cluster.getFileSystem();
        FSDataOutputStream out = dfs.create(path);
        // NameNode append call returns a last block instance. If there is nothing
        // it returns as a null. So write something, so that lastBlock has
        // something
        out.write(0);
        out.close();
        DFSClient client = dfs.getClient();
        String clientName = client.getClientName();
        LastBlockWithStatus lastBlock = client.namenode.append(src, clientName, new org.apache.hadoop.io.EnumSetWritable(EnumSet.of(APPEND)));
        byte[] secret = lastBlock.getLastBlock().getBlockToken().getDnHandshakeSecret();
        BlockKey currentKey = cluster.getNamesystem().getBlockManager().getBlockTokenSecretManager().getCurrentKey();
        String decrypted = decryptMessage(secret, currentKey, encryptionAlgorithm);
        Assert.assertEquals(this.qopValue, decrypted);
    }

    @Test
    public void testGetBlockLocationWrappingQOP() throws Exception {
        final String src = "/testGetBlockLocationWrappingQOP";
        final Path path = new Path(src);
        dfs = cluster.getFileSystem();
        FSDataOutputStream out = dfs.create(path);
        // if the file is empty, there will be no blocks returned. Write something
        // so that getBlockLocations actually returns some block.
        out.write(0);
        out.close();
        FileStatus status = dfs.getFileStatus(path);
        DFSClient client = dfs.getClient();
        LocatedBlocks lbs = client.namenode.getBlockLocations(src, 0, status.getLen());
        Assert.assertTrue(((lbs.getLocatedBlocks().size()) > 0));
        BlockKey currentKey = cluster.getNamesystem().getBlockManager().getBlockTokenSecretManager().getCurrentKey();
        for (LocatedBlock lb : lbs.getLocatedBlocks()) {
            byte[] secret = lb.getBlockToken().getDnHandshakeSecret();
            String decrypted = decryptMessage(secret, currentKey, encryptionAlgorithm);
            Assert.assertEquals(this.qopValue, decrypted);
        }
    }
}

