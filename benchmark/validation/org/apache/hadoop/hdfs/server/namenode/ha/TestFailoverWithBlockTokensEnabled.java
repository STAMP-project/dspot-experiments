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


import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSClient;
import org.apache.hadoop.hdfs.DFSClientAdapter;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.protocol.LocatedBlock;
import org.apache.hadoop.hdfs.protocol.LocatedBlocks;
import org.apache.hadoop.hdfs.security.token.block.BlockTokenIdentifier;
import org.apache.hadoop.hdfs.security.token.block.BlockTokenSecretManager;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.util.Time;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class TestFailoverWithBlockTokensEnabled {
    private static final Path TEST_PATH = new Path("/test-path");

    private static final String TEST_DATA = "very important text";

    private static final int numNNs = 3;

    private Configuration conf;

    private MiniDFSCluster cluster;

    @Test
    public void ensureSerialNumbersNeverOverlap() {
        BlockTokenSecretManager btsm1 = cluster.getNamesystem(0).getBlockManager().getBlockTokenSecretManager();
        BlockTokenSecretManager btsm2 = cluster.getNamesystem(1).getBlockManager().getBlockTokenSecretManager();
        BlockTokenSecretManager btsm3 = cluster.getNamesystem(2).getBlockManager().getBlockTokenSecretManager();
        setAndCheckSerialNumber(0, btsm1, btsm2, btsm3);
        setAndCheckSerialNumber(Integer.MAX_VALUE, btsm1, btsm2, btsm3);
        setAndCheckSerialNumber(Integer.MIN_VALUE, btsm1, btsm2, btsm3);
        setAndCheckSerialNumber(((Integer.MAX_VALUE) / 2), btsm1, btsm2, btsm3);
        setAndCheckSerialNumber(((Integer.MIN_VALUE) / 2), btsm1, btsm2, btsm3);
        setAndCheckSerialNumber(((Integer.MAX_VALUE) / 3), btsm1, btsm2, btsm3);
        setAndCheckSerialNumber(((Integer.MIN_VALUE) / 3), btsm1, btsm2, btsm3);
    }

    @Test
    public void testSerialNumberMaskMatchIndex() {
        BlockTokenSecretManager btsm1 = cluster.getNamesystem(0).getBlockManager().getBlockTokenSecretManager();
        BlockTokenSecretManager btsm2 = cluster.getNamesystem(1).getBlockManager().getBlockTokenSecretManager();
        BlockTokenSecretManager btsm3 = cluster.getNamesystem(2).getBlockManager().getBlockTokenSecretManager();
        int[] testSet = new int[]{ 0, Integer.MAX_VALUE, Integer.MIN_VALUE, (Integer.MAX_VALUE) / 2, (Integer.MIN_VALUE) / 2, (Integer.MAX_VALUE) / 3, (Integer.MIN_VALUE) / 3 };
        for (int i = 0; i < (testSet.length); i++) {
            setAndCheckHighBitsSerialNumber(testSet[i], btsm1, 0);
            setAndCheckHighBitsSerialNumber(testSet[i], btsm2, 1);
            setAndCheckHighBitsSerialNumber(testSet[i], btsm3, 2);
        }
    }

    @Test
    public void ensureInvalidBlockTokensAreRejected() throws IOException, URISyntaxException {
        cluster.transitionToActive(0);
        FileSystem fs = HATestUtil.configureFailoverFs(cluster, conf);
        DFSTestUtil.writeFile(fs, TestFailoverWithBlockTokensEnabled.TEST_PATH, TestFailoverWithBlockTokensEnabled.TEST_DATA);
        Assert.assertEquals(TestFailoverWithBlockTokensEnabled.TEST_DATA, DFSTestUtil.readFile(fs, TestFailoverWithBlockTokensEnabled.TEST_PATH));
        DFSClient dfsClient = DFSClientAdapter.getDFSClient(((DistributedFileSystem) (fs)));
        DFSClient spyDfsClient = Mockito.spy(dfsClient);
        Mockito.doAnswer(new Answer<LocatedBlocks>() {
            @Override
            public LocatedBlocks answer(InvocationOnMock arg0) throws Throwable {
                LocatedBlocks locatedBlocks = ((LocatedBlocks) (arg0.callRealMethod()));
                for (LocatedBlock lb : locatedBlocks.getLocatedBlocks()) {
                    Token<BlockTokenIdentifier> token = lb.getBlockToken();
                    BlockTokenIdentifier id = lb.getBlockToken().decodeIdentifier();
                    // This will make the token invalid, since the password
                    // won't match anymore
                    id.setExpiryDate(((Time.now()) + 10));
                    Token<BlockTokenIdentifier> newToken = new Token<BlockTokenIdentifier>(id.getBytes(), token.getPassword(), token.getKind(), token.getService());
                    lb.setBlockToken(newToken);
                }
                return locatedBlocks;
            }
        }).when(spyDfsClient).getLocatedBlocks(Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong());
        DFSClientAdapter.setDFSClient(((DistributedFileSystem) (fs)), spyDfsClient);
        try {
            Assert.assertEquals(TestFailoverWithBlockTokensEnabled.TEST_DATA, DFSTestUtil.readFile(fs, TestFailoverWithBlockTokensEnabled.TEST_PATH));
            Assert.fail("Shouldn't have been able to read a file with invalid block tokens");
        } catch (IOException ioe) {
            GenericTestUtils.assertExceptionContains("Could not obtain block", ioe);
        }
    }

    @Test
    public void testFailoverAfterRegistration() throws IOException, URISyntaxException {
        writeUsingBothNameNodes();
    }

    @Test
    public void TestFailoverAfterAccessKeyUpdate() throws IOException, InterruptedException, URISyntaxException {
        TestFailoverWithBlockTokensEnabled.lowerKeyUpdateIntervalAndClearKeys(cluster);
        // Sleep 10s to guarantee DNs heartbeat and get new keys.
        Thread.sleep((10 * 1000));
        writeUsingBothNameNodes();
    }
}

