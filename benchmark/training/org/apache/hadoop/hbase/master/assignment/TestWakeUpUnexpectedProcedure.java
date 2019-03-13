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
package org.apache.hadoop.hbase.master.assignment;


import RegionState.State;
import java.io.IOException;
import java.net.ConnectException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.MiniHBaseCluster;
import org.apache.hadoop.hbase.PleaseHoldException;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.master.HMaster;
import org.apache.hadoop.hbase.master.MasterServices;
import org.apache.hadoop.hbase.master.ServerManager;
import org.apache.hadoop.hbase.regionserver.HRegionServer;
import org.apache.hadoop.hbase.regionserver.RSRpcServices;
import org.apache.hadoop.hbase.shaded.protobuf.ProtobufUtil;
import org.apache.hadoop.hbase.shaded.protobuf.generated.AdminProtos.ExecuteProceduresRequest;
import org.apache.hadoop.hbase.shaded.protobuf.generated.AdminProtos.ExecuteProceduresResponse;
import org.apache.hadoop.hbase.shaded.protobuf.generated.RegionServerStatusProtos.RegionStateTransition;
import org.apache.hadoop.hbase.shaded.protobuf.generated.RegionServerStatusProtos.RegionStateTransition.TransitionCode;
import org.apache.hadoop.hbase.shaded.protobuf.generated.RegionServerStatusProtos.ReportRegionStateTransitionRequest;
import org.apache.hadoop.hbase.shaded.protobuf.generated.RegionServerStatusProtos.ReportRegionStateTransitionResponse;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.JVMClusterUtil.RegionServerThread;
import org.apache.hbase.thirdparty.com.google.protobuf.RpcController;
import org.apache.hbase.thirdparty.com.google.protobuf.ServiceException;
import org.apache.zookeeper.KeeperException;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Testcase for HBASE-21811.
 */
@Category({ MasterTests.class, LargeTests.class })
public class TestWakeUpUnexpectedProcedure {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestWakeUpUnexpectedProcedure.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestWakeUpUnexpectedProcedure.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static TableName NAME = TableName.valueOf("Assign");

    private static final List<ServerName> EXCLUDE_SERVERS = new CopyOnWriteArrayList<>();

    private static byte[] CF = Bytes.toBytes("cf");

    private static volatile ServerName SERVER_TO_KILL;

    private static volatile CountDownLatch ARRIVE_EXEC_PROC;

    private static volatile CountDownLatch RESUME_EXEC_PROC;

    private static volatile CountDownLatch RESUME_IS_SERVER_ONLINE;

    private static volatile CountDownLatch ARRIVE_REPORT;

    private static volatile CountDownLatch RESUME_REPORT;

    private static final class RSRpcServicesForTest extends RSRpcServices {
        public RSRpcServicesForTest(HRegionServer rs) throws IOException {
            super(rs);
        }

        @Override
        public ExecuteProceduresResponse executeProcedures(RpcController controller, ExecuteProceduresRequest request) throws ServiceException {
            if ((request.getOpenRegionCount()) > 0) {
                if ((TestWakeUpUnexpectedProcedure.ARRIVE_EXEC_PROC) != null) {
                    TestWakeUpUnexpectedProcedure.SERVER_TO_KILL = regionServer.getServerName();
                    TestWakeUpUnexpectedProcedure.ARRIVE_EXEC_PROC.countDown();
                    TestWakeUpUnexpectedProcedure.ARRIVE_EXEC_PROC = null;
                    try {
                        TestWakeUpUnexpectedProcedure.RESUME_EXEC_PROC.await();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    throw new ServiceException(new ConnectException("Inject error"));
                }
            }
            return super.executeProcedures(controller, request);
        }
    }

    public static final class RSForTest extends MiniHBaseCluster.MiniHBaseClusterRegionServer {
        public RSForTest(Configuration conf) throws IOException, InterruptedException {
            super(conf);
        }

        @Override
        protected RSRpcServices createRpcServices() throws IOException {
            return new TestWakeUpUnexpectedProcedure.RSRpcServicesForTest(this);
        }
    }

    private static final class AMForTest extends AssignmentManager {
        public AMForTest(MasterServices master) {
            super(master);
        }

        @Override
        public ReportRegionStateTransitionResponse reportRegionStateTransition(ReportRegionStateTransitionRequest req) throws PleaseHoldException {
            RegionStateTransition rst = req.getTransition(0);
            if (((rst.getTransitionCode()) == (TransitionCode.OPENED)) && (ProtobufUtil.toTableName(rst.getRegionInfo(0).getTableName()).equals(TestWakeUpUnexpectedProcedure.NAME))) {
                CountDownLatch arrive = TestWakeUpUnexpectedProcedure.ARRIVE_REPORT;
                if ((TestWakeUpUnexpectedProcedure.ARRIVE_REPORT) != null) {
                    TestWakeUpUnexpectedProcedure.ARRIVE_REPORT = null;
                    arrive.countDown();
                    // so we will choose another rs next time
                    TestWakeUpUnexpectedProcedure.EXCLUDE_SERVERS.add(ProtobufUtil.toServerName(req.getServer()));
                    try {
                        TestWakeUpUnexpectedProcedure.RESUME_REPORT.await();
                    } catch (InterruptedException e) {
                        throw new RuntimeException();
                    }
                }
            }
            return super.reportRegionStateTransition(req);
        }
    }

    private static final class SMForTest extends ServerManager {
        public SMForTest(MasterServices master) {
            super(master);
        }

        @Override
        public boolean isServerOnline(ServerName serverName) {
            ServerName toKill = TestWakeUpUnexpectedProcedure.SERVER_TO_KILL;
            if ((toKill != null) && (toKill.equals(serverName))) {
                for (StackTraceElement ele : new Exception().getStackTrace()) {
                    // halt it is called from RSProcedureDispatcher, to delay the remoteCallFailed.
                    if ("scheduleForRetry".equals(ele.getMethodName())) {
                        if ((TestWakeUpUnexpectedProcedure.RESUME_IS_SERVER_ONLINE) != null) {
                            try {
                                TestWakeUpUnexpectedProcedure.RESUME_IS_SERVER_ONLINE.await();
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        break;
                    }
                }
            }
            return super.isServerOnline(serverName);
        }

        @Override
        public List<ServerName> createDestinationServersList() {
            return super.createDestinationServersList(TestWakeUpUnexpectedProcedure.EXCLUDE_SERVERS);
        }
    }

    public static final class HMasterForTest extends HMaster {
        public HMasterForTest(Configuration conf) throws IOException, KeeperException {
            super(conf);
        }

        @Override
        protected AssignmentManager createAssignmentManager(MasterServices master) {
            return new TestWakeUpUnexpectedProcedure.AMForTest(master);
        }

        @Override
        protected ServerManager createServerManager(MasterServices master) throws IOException {
            setupClusterConnection();
            return new TestWakeUpUnexpectedProcedure.SMForTest(master);
        }
    }

    @Test
    public void test() throws Exception {
        RegionInfo region = TestWakeUpUnexpectedProcedure.UTIL.getMiniHBaseCluster().getRegions(TestWakeUpUnexpectedProcedure.NAME).get(0).getRegionInfo();
        AssignmentManager am = TestWakeUpUnexpectedProcedure.UTIL.getMiniHBaseCluster().getMaster().getAssignmentManager();
        RegionStateNode rsn = am.getRegionStates().getRegionStateNode(region);
        ServerName sn = rsn.getRegionLocation();
        TestWakeUpUnexpectedProcedure.RESUME_EXEC_PROC = new CountDownLatch(1);
        TestWakeUpUnexpectedProcedure.ARRIVE_EXEC_PROC = new CountDownLatch(1);
        TestWakeUpUnexpectedProcedure.RESUME_IS_SERVER_ONLINE = new CountDownLatch(1);
        // reopen the region, and halt the executeProcedures method at RS side
        am.moveAsync(new org.apache.hadoop.hbase.master.RegionPlan(region, sn, sn));
        TestWakeUpUnexpectedProcedure.ARRIVE_EXEC_PROC.await();
        TestWakeUpUnexpectedProcedure.RESUME_REPORT = new CountDownLatch(1);
        TestWakeUpUnexpectedProcedure.ARRIVE_REPORT = new CountDownLatch(1);
        // kill the region server
        ServerName serverToKill = TestWakeUpUnexpectedProcedure.SERVER_TO_KILL;
        TestWakeUpUnexpectedProcedure.UTIL.getMiniHBaseCluster().stopRegionServer(serverToKill);
        TestWakeUpUnexpectedProcedure.RESUME_EXEC_PROC.countDown();
        // wait until we are going to open the region on a new rs
        TestWakeUpUnexpectedProcedure.ARRIVE_REPORT.await();
        // resume the isServerOnline check, to let the rs procedure
        TestWakeUpUnexpectedProcedure.RESUME_IS_SERVER_ONLINE.countDown();
        // before HBASE-20811 the state could become OPEN, and this is why later the region will be
        // assigned to two regionservers.
        for (int i = 0; i < 15; i++) {
            if ((rsn.getState()) == (State.OPEN)) {
                break;
            }
            Thread.sleep(1000);
        }
        // resume the old report
        TestWakeUpUnexpectedProcedure.RESUME_REPORT.countDown();
        // wait a bit to let the region to be online, it is not easy to write a condition for this so
        // just sleep a while.
        Thread.sleep(10000);
        // confirm that the region is only on one rs
        int count = 0;
        for (RegionServerThread t : TestWakeUpUnexpectedProcedure.UTIL.getMiniHBaseCluster().getRegionServerThreads()) {
            if (!(t.getRegionServer().getRegions(TestWakeUpUnexpectedProcedure.NAME).isEmpty())) {
                TestWakeUpUnexpectedProcedure.LOG.info("{} is on {}", region, t.getRegionServer().getServerName());
                count++;
            }
        }
        Assert.assertEquals(1, count);
    }
}

