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
package org.apache.hadoop.hbase.master;


import MasterProtos.MasterService.BlockingInterface;
import java.io.IOException;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.ipc.RpcClient;
import org.apache.hadoop.hbase.security.User;
import org.apache.hadoop.hbase.shaded.protobuf.ProtobufUtil;
import org.apache.hadoop.hbase.shaded.protobuf.generated.MasterProtos;
import org.apache.hadoop.hbase.shaded.protobuf.generated.MasterProtos.IsMasterRunningRequest;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hbase.thirdparty.com.google.protobuf.BlockingRpcChannel;
import org.apache.hbase.thirdparty.com.google.protobuf.ServiceException;
import org.apache.zookeeper.KeeperException;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ MasterTests.class, MediumTests.class })
public class TestHMasterRPCException {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestHMasterRPCException.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestHMasterRPCException.class);

    private final HBaseTestingUtility testUtil = HBaseTestingUtility.createLocalHTU();

    private HMaster master;

    private RpcClient rpcClient;

    @Test
    public void testRPCException() throws IOException, InterruptedException, KeeperException {
        ServerName sm = master.getServerName();
        boolean fakeZNodeDelete = false;
        for (int i = 0; i < 20; i++) {
            try {
                BlockingRpcChannel channel = rpcClient.createBlockingRpcChannel(sm, User.getCurrent(), 0);
                MasterProtos.MasterService.BlockingInterface stub = MasterProtos.MasterService.newBlockingStub(channel);
                Assert.assertTrue(stub.isMasterRunning(null, IsMasterRunningRequest.getDefaultInstance()).getIsMasterRunning());
                return;
            } catch (ServiceException ex) {
                IOException ie = ProtobufUtil.handleRemoteException(ex);
                // No SocketTimeoutException here. RpcServer is already started after the construction of
                // HMaster.
                Assert.assertTrue(ie.getMessage().startsWith("org.apache.hadoop.hbase.ipc.ServerNotRunningYetException: Server is not running yet"));
                TestHMasterRPCException.LOG.info("Expected exception: ", ie);
                if (!fakeZNodeDelete) {
                    getZooKeeperWatcher().getRecoverableZooKeeper().delete(getZooKeeperWatcher().getZNodePaths().masterAddressZNode, (-1));
                    fakeZNodeDelete = true;
                }
            }
            Thread.sleep(1000);
        }
    }
}

