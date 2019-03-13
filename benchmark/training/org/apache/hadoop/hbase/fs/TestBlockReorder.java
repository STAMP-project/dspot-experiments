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
package org.apache.hadoop.hbase.fs;


import java.net.BindException;
import java.net.ServerSocket;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.MiscTests;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.protocol.DatanodeInfo;
import org.apache.hadoop.hdfs.protocol.LocatedBlock;
import org.apache.hadoop.hdfs.protocol.LocatedBlocks;
import org.apache.hadoop.hdfs.server.datanode.DataNode;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests for the hdfs fix from HBASE-6435.
 *
 * Please don't add new subtest which involves starting / stopping MiniDFSCluster in this class.
 * When stopping MiniDFSCluster, shutdown hooks would be cleared in hadoop's ShutdownHookManager
 *   in hadoop 3.
 * This leads to 'Failed suppression of fs shutdown hook' error in region server.
 */
@Category({ MiscTests.class, LargeTests.class })
public class TestBlockReorder {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestBlockReorder.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestBlockReorder.class);

    private Configuration conf;

    private MiniDFSCluster cluster;

    private HBaseTestingUtility htu;

    private DistributedFileSystem dfs;

    private static final String host1 = "host1";

    private static final String host2 = "host2";

    private static final String host3 = "host3";

    @Rule
    public TestName name = new TestName();

    /**
     * Test that we're can add a hook, and that this hook works when we try to read the file in HDFS.
     */
    @Test
    public void testBlockLocationReorder() throws Exception {
        Path p = new Path("hello");
        Assert.assertTrue((((short) (cluster.getDataNodes().size())) > 1));
        final int repCount = 2;
        // Let's write the file
        FSDataOutputStream fop = dfs.create(p, ((short) (repCount)));
        final double toWrite = 875.5613;
        fop.writeDouble(toWrite);
        fop.close();
        // Let's check we can read it when everybody's there
        long start = System.currentTimeMillis();
        FSDataInputStream fin = dfs.open(p);
        Assert.assertTrue((toWrite == (fin.readDouble())));
        long end = System.currentTimeMillis();
        TestBlockReorder.LOG.info(("readtime= " + (end - start)));
        fin.close();
        Assert.assertTrue(((end - start) < (30 * 1000)));
        // Let's kill the first location. But actually the fist location returned will change
        // The first thing to do is to get the location, then the port
        FileStatus f = dfs.getFileStatus(p);
        BlockLocation[] lbs;
        do {
            lbs = dfs.getFileBlockLocations(f, 0, 1);
        } while (((lbs.length) != 1) && ((lbs[0].getLength()) != repCount) );
        final String name = lbs[0].getNames()[0];
        Assert.assertTrue(((name.indexOf(':')) > 0));
        String portS = name.substring(((name.indexOf(':')) + 1));
        final int port = Integer.parseInt(portS);
        TestBlockReorder.LOG.info(("port= " + port));
        int ipcPort = -1;
        // Let's find the DN to kill. cluster.getDataNodes(int) is not on the same port, so we need
        // to iterate ourselves.
        boolean ok = false;
        final String lookup = lbs[0].getHosts()[0];
        StringBuilder sb = new StringBuilder();
        for (DataNode dn : cluster.getDataNodes()) {
            final String dnName = getHostName(dn);
            sb.append(dnName).append(' ');
            if (lookup.equals(dnName)) {
                ok = true;
                TestBlockReorder.LOG.info(((("killing datanode " + name) + " / ") + lookup));
                ipcPort = dn.ipcServer.getListenerAddress().getPort();
                dn.shutdown();
                TestBlockReorder.LOG.info(((("killed datanode " + name) + " / ") + lookup));
                break;
            }
        }
        Assert.assertTrue(((("didn't find the server to kill, was looking for " + lookup) + " found ") + sb), ok);
        TestBlockReorder.LOG.info(("ipc port= " + ipcPort));
        // Add the hook, with an implementation checking that we don't use the port we've just killed.
        Assert.assertTrue(HFileSystem.addLocationsOrderInterceptor(conf, new HFileSystem.ReorderBlocks() {
            @Override
            public void reorderBlocks(Configuration c, LocatedBlocks lbs, String src) {
                for (LocatedBlock lb : lbs.getLocatedBlocks()) {
                    if ((lb.getLocations().length) > 1) {
                        DatanodeInfo[] infos = lb.getLocations();
                        if (infos[0].getHostName().equals(lookup)) {
                            TestBlockReorder.LOG.info("HFileSystem bad host, inverting");
                            DatanodeInfo tmp = infos[0];
                            infos[0] = infos[1];
                            infos[1] = tmp;
                        }
                    }
                }
            }
        }));
        final int retries = 10;
        ServerSocket ss = null;
        ServerSocket ssI;
        try {
            ss = new ServerSocket(port);// We're taking the port to have a timeout issue later.

            ssI = new ServerSocket(ipcPort);
        } catch (BindException be) {
            TestBlockReorder.LOG.warn(((((("Got bind exception trying to set up socket on " + port) + " or ") + ipcPort) + ", this means that the datanode has not closed the socket or") + " someone else took it. It may happen, skipping this test for this time."), be);
            if (ss != null) {
                ss.close();
            }
            return;
        }
        // Now it will fail with a timeout, unfortunately it does not always connect to the same box,
        // so we try retries times;  with the reorder it will never last more than a few milli seconds
        for (int i = 0; i < retries; i++) {
            start = System.currentTimeMillis();
            fin = dfs.open(p);
            Assert.assertTrue((toWrite == (fin.readDouble())));
            fin.close();
            end = System.currentTimeMillis();
            TestBlockReorder.LOG.info(("HFileSystem readtime= " + (end - start)));
            Assert.assertFalse("We took too much time to read", ((end - start) > 60000));
        }
        ss.close();
        ssI.close();
    }
}

