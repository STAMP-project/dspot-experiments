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


import HdfsServerConstants.ReplicaState.RBW;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.protocol.LocatedBlock;
import org.apache.hadoop.hdfs.server.datanode.DataNode;
import org.apache.hadoop.hdfs.server.datanode.Replica;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestPipelines {
    public static final Logger LOG = LoggerFactory.getLogger(TestPipelines.class);

    private static final short REPL_FACTOR = 3;

    private static final int RAND_LIMIT = 2000;

    private static final int FILE_SIZE = 10000;

    private MiniDFSCluster cluster;

    private DistributedFileSystem fs;

    private static Configuration conf;

    static final Random rand = new Random(TestPipelines.RAND_LIMIT);

    static {
        TestPipelines.initLoggers();
        TestPipelines.setConfiguration();
    }

    /**
     * Creates and closes a file of certain length.
     * Calls append to allow next write() operation to add to the end of it
     * After write() invocation, calls hflush() to make sure that data sunk through
     * the pipeline and check the state of the last block's replica.
     * It supposes to be in RBW state
     *
     * @throws IOException
     * 		in case of an error
     */
    @Test
    public void pipeline_01() throws IOException {
        final String METHOD_NAME = GenericTestUtils.getMethodName();
        if (TestPipelines.LOG.isDebugEnabled()) {
            TestPipelines.LOG.debug(("Running " + METHOD_NAME));
        }
        Path filePath = new Path((("/" + METHOD_NAME) + ".dat"));
        DFSTestUtil.createFile(fs, filePath, TestPipelines.FILE_SIZE, TestPipelines.REPL_FACTOR, TestPipelines.rand.nextLong());
        if (TestPipelines.LOG.isDebugEnabled()) {
            TestPipelines.LOG.debug("Invoking append but doing nothing otherwise...");
        }
        FSDataOutputStream ofs = fs.append(filePath);
        ofs.writeBytes("Some more stuff to write");
        hflush();
        List<LocatedBlock> lb = cluster.getNameNodeRpc().getBlockLocations(filePath.toString(), ((TestPipelines.FILE_SIZE) - 1), TestPipelines.FILE_SIZE).getLocatedBlocks();
        for (DataNode dn : cluster.getDataNodes()) {
            Replica r = cluster.getFsDatasetTestUtils(dn).fetchReplica(lb.get(0).getBlock());
            Assert.assertTrue((("Replica on DN " + dn) + " shouldn't be null"), (r != null));
            Assert.assertEquals((("Should be RBW replica on " + dn) + " after sequence of calls append()/write()/hflush()"), RBW, r.getState());
        }
        ofs.close();
    }
}

