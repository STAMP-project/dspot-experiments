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
package org.apache.hadoop.hdfs.server.federation.router;


import STATE.INITED;
import STATE.STARTED;
import STATE.STOPPED;
import java.io.IOException;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.server.federation.FederationTestUtils;
import org.apache.hadoop.hdfs.server.federation.MiniRouterDFSCluster;
import org.apache.hadoop.hdfs.server.federation.resolver.ActiveNamenodeResolver;
import org.apache.hadoop.hdfs.server.federation.resolver.FederationNamenodeContext;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;


/**
 * Test the service that heartbeats the state of the namenodes to the State
 * Store.
 */
public class TestRouterNamenodeHeartbeat {
    private static MiniRouterDFSCluster cluster;

    private static ActiveNamenodeResolver namenodeResolver;

    private static List<NamenodeHeartbeatService> services;

    @Rule
    public TestName name = new TestName();

    @Test
    public void testNamenodeHeartbeatService() throws IOException {
        MiniRouterDFSCluster testCluster = new MiniRouterDFSCluster(true, 1);
        Configuration heartbeatConfig = testCluster.generateNamenodeConfiguration(FederationTestUtils.NAMESERVICES[0]);
        NamenodeHeartbeatService server = new NamenodeHeartbeatService(TestRouterNamenodeHeartbeat.namenodeResolver, FederationTestUtils.NAMESERVICES[0], FederationTestUtils.NAMENODES[0]);
        server.init(heartbeatConfig);
        Assert.assertEquals(INITED, server.getServiceState());
        server.start();
        Assert.assertEquals(STARTED, server.getServiceState());
        server.stop();
        Assert.assertEquals(STOPPED, server.getServiceState());
        server.close();
    }

    @Test
    public void testHearbeat() throws IOException, InterruptedException {
        // Set NAMENODE1 to active for all nameservices
        if (TestRouterNamenodeHeartbeat.cluster.isHighAvailability()) {
            for (String ns : TestRouterNamenodeHeartbeat.cluster.getNameservices()) {
                TestRouterNamenodeHeartbeat.cluster.switchToActive(ns, FederationTestUtils.NAMENODES[0]);
                TestRouterNamenodeHeartbeat.cluster.switchToStandby(ns, FederationTestUtils.NAMENODES[1]);
            }
        }
        // Wait for heartbeats to record
        Thread.sleep(5000);
        // Verify the locator has matching NN entries for each NS
        for (String ns : TestRouterNamenodeHeartbeat.cluster.getNameservices()) {
            List<? extends FederationNamenodeContext> nns = TestRouterNamenodeHeartbeat.namenodeResolver.getNamenodesForNameserviceId(ns);
            // Active
            FederationNamenodeContext active = nns.get(0);
            Assert.assertEquals(FederationTestUtils.NAMENODES[0], active.getNamenodeId());
            // Standby
            FederationNamenodeContext standby = nns.get(1);
            Assert.assertEquals(FederationTestUtils.NAMENODES[1], standby.getNamenodeId());
        }
        // Switch active NNs in 1/2 nameservices
        List<String> nss = TestRouterNamenodeHeartbeat.cluster.getNameservices();
        String failoverNS = nss.get(0);
        String normalNs = nss.get(1);
        TestRouterNamenodeHeartbeat.cluster.switchToStandby(failoverNS, FederationTestUtils.NAMENODES[0]);
        TestRouterNamenodeHeartbeat.cluster.switchToActive(failoverNS, FederationTestUtils.NAMENODES[1]);
        // Wait for heartbeats to record
        Thread.sleep(5000);
        // Verify the locator has recorded the failover for the failover NS
        List<? extends FederationNamenodeContext> failoverNSs = TestRouterNamenodeHeartbeat.namenodeResolver.getNamenodesForNameserviceId(failoverNS);
        // Active
        FederationNamenodeContext active = failoverNSs.get(0);
        Assert.assertEquals(FederationTestUtils.NAMENODES[1], active.getNamenodeId());
        // Standby
        FederationNamenodeContext standby = failoverNSs.get(1);
        Assert.assertEquals(FederationTestUtils.NAMENODES[0], standby.getNamenodeId());
        // Verify the locator has the same records for the other ns
        List<? extends FederationNamenodeContext> normalNss = TestRouterNamenodeHeartbeat.namenodeResolver.getNamenodesForNameserviceId(normalNs);
        // Active
        active = normalNss.get(0);
        Assert.assertEquals(FederationTestUtils.NAMENODES[0], active.getNamenodeId());
        // Standby
        standby = normalNss.get(1);
        Assert.assertEquals(FederationTestUtils.NAMENODES[1], standby.getNamenodeId());
    }
}

