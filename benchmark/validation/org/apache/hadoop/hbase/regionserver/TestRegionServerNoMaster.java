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
package org.apache.hadoop.hbase.regionserver;


import AdminProtos.CloseRegionResponse;
import AdminProtos.OpenRegionRequest;
import java.io.IOException;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HRegionInfo;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.client.TableDescriptor;
import org.apache.hadoop.hbase.shaded.protobuf.ProtobufUtil;
import org.apache.hadoop.hbase.shaded.protobuf.RequestConverter;
import org.apache.hadoop.hbase.shaded.protobuf.generated.AdminProtos;
import org.apache.hadoop.hbase.shaded.protobuf.generated.AdminProtos.CloseRegionRequest;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hbase.thirdparty.com.google.protobuf.ServiceException;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests on the region server, without the master.
 */
@Category({ RegionServerTests.class, MediumTests.class })
public class TestRegionServerNoMaster {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRegionServerNoMaster.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestRegionServerNoMaster.class);

    private static final int NB_SERVERS = 1;

    private static Table table;

    private static final byte[] row = Bytes.toBytes("ee");

    private static HRegionInfo hri;

    private static byte[] regionName;

    private static final HBaseTestingUtility HTU = new HBaseTestingUtility();

    @Test
    public void testCloseByRegionServer() throws Exception {
        closeRegionNoZK();
        TestRegionServerNoMaster.openRegion(TestRegionServerNoMaster.HTU, TestRegionServerNoMaster.getRS(), TestRegionServerNoMaster.hri);
    }

    @Test
    public void testMultipleCloseFromMaster() throws Exception {
        for (int i = 0; i < 10; i++) {
            AdminProtos.CloseRegionRequest crr = ProtobufUtil.buildCloseRegionRequest(TestRegionServerNoMaster.getRS().getServerName(), TestRegionServerNoMaster.regionName, null);
            try {
                AdminProtos.CloseRegionResponse responseClose = TestRegionServerNoMaster.getRS().rpcServices.closeRegion(null, crr);
                Assert.assertTrue((("request " + i) + " failed"), ((responseClose.getClosed()) || (responseClose.hasClosed())));
            } catch (org.apache.hbase se) {
                Assert.assertTrue("The next queries may throw an exception.", (i > 0));
            }
        }
        TestRegionServerNoMaster.checkRegionIsClosed(TestRegionServerNoMaster.HTU, TestRegionServerNoMaster.getRS(), TestRegionServerNoMaster.hri);
        TestRegionServerNoMaster.openRegion(TestRegionServerNoMaster.HTU, TestRegionServerNoMaster.getRS(), TestRegionServerNoMaster.hri);
    }

    /**
     * Test that if we do a close while opening it stops the opening.
     */
    @Test
    public void testCancelOpeningWithoutZK() throws Exception {
        // We close
        closeRegionNoZK();
        TestRegionServerNoMaster.checkRegionIsClosed(TestRegionServerNoMaster.HTU, TestRegionServerNoMaster.getRS(), TestRegionServerNoMaster.hri);
        // Let do the initial steps, without having a handler
        TestRegionServerNoMaster.getRS().getRegionsInTransitionInRS().put(TestRegionServerNoMaster.hri.getEncodedNameAsBytes(), Boolean.TRUE);
        // That's a close without ZK.
        AdminProtos.CloseRegionRequest crr = ProtobufUtil.buildCloseRegionRequest(TestRegionServerNoMaster.getRS().getServerName(), TestRegionServerNoMaster.regionName);
        try {
            TestRegionServerNoMaster.getRS().rpcServices.closeRegion(null, crr);
            Assert.assertTrue(false);
        } catch (org.apache.hbase expected) {
        }
        // The state in RIT should have changed to close
        Assert.assertEquals(Boolean.FALSE, TestRegionServerNoMaster.getRS().getRegionsInTransitionInRS().get(TestRegionServerNoMaster.hri.getEncodedNameAsBytes()));
        // Let's start the open handler
        TableDescriptor htd = TestRegionServerNoMaster.getRS().tableDescriptors.get(TestRegionServerNoMaster.hri.getTable());
        TestRegionServerNoMaster.getRS().executorService.submit(new org.apache.hadoop.hbase.regionserver.handler.OpenRegionHandler(TestRegionServerNoMaster.getRS(), TestRegionServerNoMaster.getRS(), TestRegionServerNoMaster.hri, htd, (-1)));
        // The open handler should have removed the region from RIT but kept the region closed
        TestRegionServerNoMaster.checkRegionIsClosed(TestRegionServerNoMaster.HTU, TestRegionServerNoMaster.getRS(), TestRegionServerNoMaster.hri);
        TestRegionServerNoMaster.openRegion(TestRegionServerNoMaster.HTU, TestRegionServerNoMaster.getRS(), TestRegionServerNoMaster.hri);
    }

    /**
     * Tests an on-the-fly RPC that was scheduled for the earlier RS on the same port
     * for openRegion. The region server should reject this RPC. (HBASE-9721)
     */
    @Test
    public void testOpenCloseRegionRPCIntendedForPreviousServer() throws Exception {
        Assert.assertTrue(TestRegionServerNoMaster.getRS().getRegion(TestRegionServerNoMaster.regionName).isAvailable());
        ServerName sn = TestRegionServerNoMaster.getRS().getServerName();
        ServerName earlierServerName = ServerName.valueOf(sn.getHostname(), sn.getPort(), 1);
        try {
            CloseRegionRequest request = ProtobufUtil.buildCloseRegionRequest(earlierServerName, TestRegionServerNoMaster.regionName);
            TestRegionServerNoMaster.getRS().getRSRpcServices().closeRegion(null, request);
            Assert.fail("The closeRegion should have been rejected");
        } catch (org.apache.hbase se) {
            Assert.assertTrue(((se.getCause()) instanceof IOException));
            Assert.assertTrue(se.getCause().getMessage().contains("This RPC was intended for a different server"));
        }
        // actual close
        closeRegionNoZK();
        try {
            AdminProtos.OpenRegionRequest orr = RequestConverter.buildOpenRegionRequest(earlierServerName, TestRegionServerNoMaster.hri, null);
            TestRegionServerNoMaster.getRS().getRSRpcServices().openRegion(null, orr);
            Assert.fail("The openRegion should have been rejected");
        } catch (org.apache.hbase se) {
            Assert.assertTrue(((se.getCause()) instanceof IOException));
            Assert.assertTrue(se.getCause().getMessage().contains("This RPC was intended for a different server"));
        } finally {
            TestRegionServerNoMaster.openRegion(TestRegionServerNoMaster.HTU, TestRegionServerNoMaster.getRS(), TestRegionServerNoMaster.hri);
        }
    }
}

