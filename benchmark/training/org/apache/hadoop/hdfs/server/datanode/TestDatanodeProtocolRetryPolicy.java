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


import java.io.EOFException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ha.HAServiceProtocol.HAServiceState;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.protocolPB.DatanodeProtocolClientSideTranslatorPB;
import org.apache.hadoop.hdfs.server.protocol.DatanodeCommand;
import org.apache.hadoop.hdfs.server.protocol.DatanodeRegistration;
import org.apache.hadoop.hdfs.server.protocol.HeartbeatResponse;
import org.apache.hadoop.hdfs.server.protocol.NamespaceInfo;
import org.apache.hadoop.hdfs.server.protocol.RegisterCommand;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;


/**
 * This tests DatanodeProtocol retry policy
 */
public class TestDatanodeProtocolRetryPolicy {
    private static final Logger LOG = LoggerFactory.getLogger(TestDatanodeProtocolRetryPolicy.class);

    private static final String DATA_DIR = (MiniDFSCluster.getBaseDirectory()) + "data";

    private DataNode dn;

    private Configuration conf;

    private boolean tearDownDone;

    ArrayList<StorageLocation> locations = new ArrayList<StorageLocation>();

    private static final String CLUSTER_ID = "testClusterID";

    private static final String POOL_ID = "BP-TEST";

    private static final InetSocketAddress NN_ADDR = new InetSocketAddress("localhost", 5020);

    private static DatanodeRegistration datanodeRegistration = DFSTestUtil.getLocalDatanodeRegistration();

    static {
        GenericTestUtils.setLogLevel(TestDatanodeProtocolRetryPolicy.LOG, Level.TRACE);
    }

    /**
     * Verify the following scenario.
     * 1. The initial DatanodeProtocol.registerDatanode succeeds.
     * 2. DN starts heartbeat process.
     * 3. In the first heartbeat, NN asks DN to reregister.
     * 4. DN calls DatanodeProtocol.registerDatanode.
     * 5. DatanodeProtocol.registerDatanode throws EOFException.
     * 6. DN retries.
     * 7. DatanodeProtocol.registerDatanode succeeds.
     */
    @Test(timeout = 60000)
    public void testDatanodeRegistrationRetry() throws Exception {
        final DatanodeProtocolClientSideTranslatorPB namenode = Mockito.mock(DatanodeProtocolClientSideTranslatorPB.class);
        Mockito.doAnswer(new Answer<DatanodeRegistration>() {
            int i = 0;

            @Override
            public DatanodeRegistration answer(InvocationOnMock invocation) throws Throwable {
                (i)++;
                if (((i) > 1) && ((i) < 5)) {
                    TestDatanodeProtocolRetryPolicy.LOG.info(("mockito exception " + (i)));
                    throw new EOFException("TestDatanodeProtocolRetryPolicy");
                } else {
                    DatanodeRegistration dr = ((DatanodeRegistration) (invocation.getArguments()[0]));
                    TestDatanodeProtocolRetryPolicy.datanodeRegistration = new DatanodeRegistration(dr.getDatanodeUuid(), dr);
                    TestDatanodeProtocolRetryPolicy.LOG.info(("mockito succeeded " + (TestDatanodeProtocolRetryPolicy.datanodeRegistration)));
                    return TestDatanodeProtocolRetryPolicy.datanodeRegistration;
                }
            }
        }).when(namenode).registerDatanode(Mockito.any(DatanodeRegistration.class));
        Mockito.when(namenode.versionRequest()).thenReturn(new NamespaceInfo(1, TestDatanodeProtocolRetryPolicy.CLUSTER_ID, TestDatanodeProtocolRetryPolicy.POOL_ID, 1L));
        Mockito.doAnswer(new Answer<HeartbeatResponse>() {
            int i = 0;

            @Override
            public HeartbeatResponse answer(InvocationOnMock invocation) throws Throwable {
                (i)++;
                HeartbeatResponse heartbeatResponse;
                if ((i) == 1) {
                    TestDatanodeProtocolRetryPolicy.LOG.info(("mockito heartbeatResponse registration " + (i)));
                    heartbeatResponse = new HeartbeatResponse(new DatanodeCommand[]{ RegisterCommand.REGISTER }, new org.apache.hadoop.hdfs.server.protocol.NNHAStatusHeartbeat(HAServiceState.ACTIVE, 1), null, ((ThreadLocalRandom.current().nextLong()) | 1L));
                } else {
                    TestDatanodeProtocolRetryPolicy.LOG.info(("mockito heartbeatResponse " + (i)));
                    heartbeatResponse = new HeartbeatResponse(new DatanodeCommand[0], new org.apache.hadoop.hdfs.server.protocol.NNHAStatusHeartbeat(HAServiceState.ACTIVE, 1), null, ((ThreadLocalRandom.current().nextLong()) | 1L));
                }
                return heartbeatResponse;
            }
        }).when(namenode).sendHeartbeat(Mockito.any(), Mockito.any(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.any(), Mockito.anyBoolean(), Mockito.any(), Mockito.any());
        dn = new DataNode(conf, locations, null, null) {
            @Override
            DatanodeProtocolClientSideTranslatorPB connectToNN(InetSocketAddress nnAddr) throws IOException {
                Assert.assertEquals(TestDatanodeProtocolRetryPolicy.NN_ADDR, nnAddr);
                return namenode;
            }
        };
        // Trigger a heartbeat so that it acknowledges the NN as active.
        dn.getAllBpOs().get(0).triggerHeartbeatForTests();
        waitForBlockReport(namenode);
    }
}

