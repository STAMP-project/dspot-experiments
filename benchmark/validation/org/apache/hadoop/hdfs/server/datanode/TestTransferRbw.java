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


import DataNode.LOG;
import DatanodeReportType.LIVE;
import Status.SUCCESS;
import java.util.Random;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSClientAdapter;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.protocol.DatanodeInfo;
import org.apache.hadoop.hdfs.protocol.ExtendedBlock;
import org.apache.hadoop.hdfs.protocol.proto.DataTransferProtos.BlockOpResponseProto;
import org.apache.hadoop.hdfs.server.protocol.DatanodeRegistration;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.log4j.Level;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test transferring RBW between datanodes
 */
public class TestTransferRbw {
    private static final Logger LOG = LoggerFactory.getLogger(TestTransferRbw.class);

    {
        GenericTestUtils.setLogLevel(DataNode.LOG, Level.ALL);
    }

    private static final Random RAN = new Random();

    private static final short REPLICATION = ((short) (1));

    @Test
    public void testTransferRbw() throws Exception {
        final HdfsConfiguration conf = new HdfsConfiguration();
        final MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(TestTransferRbw.REPLICATION).build();
        try {
            cluster.waitActive();
            final DistributedFileSystem fs = cluster.getFileSystem();
            // create a file, write some data and leave it open.
            final Path p = new Path("/foo");
            final int size = (1 << 16) + (TestTransferRbw.RAN.nextInt((1 << 16)));
            TestTransferRbw.LOG.info(("size = " + size));
            final FSDataOutputStream out = fs.create(p, TestTransferRbw.REPLICATION);
            final byte[] bytes = new byte[1024];
            for (int remaining = size; remaining > 0;) {
                TestTransferRbw.RAN.nextBytes(bytes);
                final int len = ((bytes.length) < remaining) ? bytes.length : remaining;
                out.write(bytes, 0, len);
                out.hflush();
                remaining -= len;
            }
            // get the RBW
            final ReplicaBeingWritten oldrbw;
            final DataNode newnode;
            final DatanodeInfo newnodeinfo;
            final String bpid = cluster.getNamesystem().getBlockPoolId();
            {
                final DataNode oldnode = cluster.getDataNodes().get(0);
                oldrbw = TestTransferRbw.getRbw(oldnode, bpid);
                TestTransferRbw.LOG.info(("oldrbw = " + oldrbw));
                // add a datanode
                cluster.startDataNodes(conf, 1, true, null, null);
                newnode = cluster.getDataNodes().get(TestTransferRbw.REPLICATION);
                final DatanodeInfo oldnodeinfo;
                {
                    final DatanodeInfo[] datatnodeinfos = cluster.getNameNodeRpc().getDatanodeReport(LIVE);
                    Assert.assertEquals(2, datatnodeinfos.length);
                    int i = 0;
                    for (DatanodeRegistration dnReg = newnode.getDNRegistrationForBP(bpid); (i < (datatnodeinfos.length)) && (!(datatnodeinfos[i].equals(dnReg))); i++);
                    Assert.assertTrue((i < (datatnodeinfos.length)));
                    newnodeinfo = datatnodeinfos[i];
                    oldnodeinfo = datatnodeinfos[(1 - i)];
                }
                // transfer RBW
                final ExtendedBlock b = new ExtendedBlock(bpid, oldrbw.getBlockId(), oldrbw.getBytesAcked(), oldrbw.getGenerationStamp());
                final BlockOpResponseProto s = DFSTestUtil.transferRbw(b, DFSClientAdapter.getDFSClient(fs), oldnodeinfo, newnodeinfo);
                Assert.assertEquals(SUCCESS, s.getStatus());
            }
            // check new rbw
            final ReplicaBeingWritten newrbw = TestTransferRbw.getRbw(newnode, bpid);
            TestTransferRbw.LOG.info(("newrbw = " + newrbw));
            Assert.assertEquals(oldrbw.getBlockId(), newrbw.getBlockId());
            Assert.assertEquals(oldrbw.getGenerationStamp(), newrbw.getGenerationStamp());
            Assert.assertEquals(oldrbw.getVisibleLength(), newrbw.getVisibleLength());
            TestTransferRbw.LOG.info("DONE");
        } finally {
            cluster.shutdown();
        }
    }
}

