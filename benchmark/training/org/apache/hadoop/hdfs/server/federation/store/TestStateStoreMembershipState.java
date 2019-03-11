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
package org.apache.hadoop.hdfs.server.federation.store;


import FederationNamenodeServiceState.ACTIVE;
import FederationNamenodeServiceState.EXPIRED;
import FederationNamenodeServiceState.STANDBY;
import FederationNamenodeServiceState.UNAVAILABLE;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.hdfs.server.federation.FederationTestUtils;
import org.apache.hadoop.hdfs.server.federation.store.protocol.GetNamenodeRegistrationsRequest;
import org.apache.hadoop.hdfs.server.federation.store.protocol.NamenodeHeartbeatRequest;
import org.apache.hadoop.hdfs.server.federation.store.protocol.UpdateNamenodeRegistrationRequest;
import org.apache.hadoop.hdfs.server.federation.store.records.MembershipState;
import org.apache.hadoop.util.Time;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test the basic {@link MembershipStore} membership functionality.
 */
public class TestStateStoreMembershipState extends TestStateStoreBase {
    private static MembershipStore membershipStore;

    @Test
    public void testNamenodeStateOverride() throws Exception {
        // Populate the state store
        // 1) ns0:nn0 - Standby
        String ns = "ns0";
        String nn = "nn0";
        MembershipState report = createRegistration(ns, nn, FederationTestUtils.ROUTERS[1], STANDBY);
        Assert.assertTrue(namenodeHeartbeat(report));
        // Load data into cache and calculate quorum
        Assert.assertTrue(TestStateStoreBase.getStateStore().loadCache(MembershipStore.class, true));
        MembershipState existingState = getNamenodeRegistration(ns, nn);
        Assert.assertEquals(STANDBY, existingState.getState());
        // Override cache
        UpdateNamenodeRegistrationRequest request = UpdateNamenodeRegistrationRequest.newInstance(ns, nn, ACTIVE);
        Assert.assertTrue(TestStateStoreMembershipState.membershipStore.updateNamenodeRegistration(request).getResult());
        MembershipState newState = getNamenodeRegistration(ns, nn);
        Assert.assertEquals(ACTIVE, newState.getState());
    }

    @Test
    public void testStateStoreDisconnected() throws Exception {
        // Close the data store driver
        TestStateStoreBase.getStateStore().closeDriver();
        Assert.assertFalse(TestStateStoreBase.getStateStore().isDriverReady());
        NamenodeHeartbeatRequest hbRequest = NamenodeHeartbeatRequest.newInstance();
        hbRequest.setNamenodeMembership(FederationStateStoreTestUtils.createMockRegistrationForNamenode("test", "test", UNAVAILABLE));
        FederationTestUtils.verifyException(TestStateStoreMembershipState.membershipStore, "namenodeHeartbeat", StateStoreUnavailableException.class, new Class[]{ NamenodeHeartbeatRequest.class }, new Object[]{ hbRequest });
        // Information from cache, no exception should be triggered for these
        // TODO - should cached info expire at some point?
        GetNamenodeRegistrationsRequest getRequest = GetNamenodeRegistrationsRequest.newInstance();
        FederationTestUtils.verifyException(TestStateStoreMembershipState.membershipStore, "getNamenodeRegistrations", null, new Class[]{ GetNamenodeRegistrationsRequest.class }, new Object[]{ getRequest });
        FederationTestUtils.verifyException(TestStateStoreMembershipState.membershipStore, "getExpiredNamenodeRegistrations", null, new Class[]{ GetNamenodeRegistrationsRequest.class }, new Object[]{ getRequest });
        UpdateNamenodeRegistrationRequest overrideRequest = UpdateNamenodeRegistrationRequest.newInstance();
        FederationTestUtils.verifyException(TestStateStoreMembershipState.membershipStore, "updateNamenodeRegistration", null, new Class[]{ UpdateNamenodeRegistrationRequest.class }, new Object[]{ overrideRequest });
    }

    @Test
    public void testRegistrationMajorityQuorum() throws IOException, InterruptedException {
        // Populate the state store with a set of non-matching elements
        // 1) ns0:nn0 - Standby (newest)
        // 2) ns0:nn0 - Active (oldest)
        // 3) ns0:nn0 - Active (2nd oldest)
        // 4) ns0:nn0 - Active (3nd oldest element, newest active element)
        // Verify the selected entry is the newest majority opinion (4)
        String ns = "ns0";
        String nn = "nn0";
        // Active - oldest
        MembershipState report = createRegistration(ns, nn, FederationTestUtils.ROUTERS[1], ACTIVE);
        Assert.assertTrue(namenodeHeartbeat(report));
        Thread.sleep(1000);
        // Active - 2nd oldest
        report = createRegistration(ns, nn, FederationTestUtils.ROUTERS[2], ACTIVE);
        Assert.assertTrue(namenodeHeartbeat(report));
        Thread.sleep(1000);
        // Active - 3rd oldest, newest active element
        report = createRegistration(ns, nn, FederationTestUtils.ROUTERS[3], ACTIVE);
        Assert.assertTrue(namenodeHeartbeat(report));
        // standby - newest overall
        report = createRegistration(ns, nn, FederationTestUtils.ROUTERS[0], STANDBY);
        Assert.assertTrue(namenodeHeartbeat(report));
        // Load and calculate quorum
        Assert.assertTrue(TestStateStoreBase.getStateStore().loadCache(MembershipStore.class, true));
        // Verify quorum entry
        MembershipState quorumEntry = getNamenodeRegistration(report.getNameserviceId(), report.getNamenodeId());
        Assert.assertNotNull(quorumEntry);
        Assert.assertEquals(quorumEntry.getRouterId(), FederationTestUtils.ROUTERS[3]);
    }

    @Test
    public void testRegistrationQuorumExcludesExpired() throws IOException, InterruptedException {
        // Populate the state store with some expired entries and verify the expired
        // entries are ignored.
        // 1) ns0:nn0 - Active
        // 2) ns0:nn0 - Expired
        // 3) ns0:nn0 - Expired
        // 4) ns0:nn0 - Expired
        // Verify the selected entry is the active entry
        List<MembershipState> registrationList = new ArrayList<>();
        String ns = "ns0";
        String nn = "nn0";
        String rpcAddress = "testrpcaddress";
        String serviceAddress = "testserviceaddress";
        String lifelineAddress = "testlifelineaddress";
        String blockPoolId = "testblockpool";
        String clusterId = "testcluster";
        String webAddress = "testwebaddress";
        boolean safemode = false;
        // Active
        MembershipState record = MembershipState.newInstance(FederationTestUtils.ROUTERS[0], ns, nn, clusterId, blockPoolId, rpcAddress, serviceAddress, lifelineAddress, webAddress, ACTIVE, safemode);
        registrationList.add(record);
        // Expired
        record = MembershipState.newInstance(FederationTestUtils.ROUTERS[1], ns, nn, clusterId, blockPoolId, rpcAddress, serviceAddress, lifelineAddress, webAddress, EXPIRED, safemode);
        registrationList.add(record);
        // Expired
        record = MembershipState.newInstance(FederationTestUtils.ROUTERS[2], ns, nn, clusterId, blockPoolId, rpcAddress, serviceAddress, lifelineAddress, webAddress, EXPIRED, safemode);
        registrationList.add(record);
        // Expired
        record = MembershipState.newInstance(FederationTestUtils.ROUTERS[3], ns, nn, clusterId, blockPoolId, rpcAddress, serviceAddress, lifelineAddress, webAddress, EXPIRED, safemode);
        registrationList.add(record);
        registerAndLoadRegistrations(registrationList);
        // Verify quorum entry chooses active membership
        MembershipState quorumEntry = getNamenodeRegistration(record.getNameserviceId(), record.getNamenodeId());
        Assert.assertNotNull(quorumEntry);
        Assert.assertEquals(FederationTestUtils.ROUTERS[0], quorumEntry.getRouterId());
    }

    @Test
    public void testRegistrationQuorumAllExpired() throws IOException {
        // 1) ns0:nn0 - Expired (oldest)
        // 2) ns0:nn0 - Expired
        // 3) ns0:nn0 - Expired
        // 4) ns0:nn0 - Expired
        // Verify no entry is either selected or cached
        List<MembershipState> registrationList = new ArrayList<>();
        String ns = FederationTestUtils.NAMESERVICES[0];
        String nn = FederationTestUtils.NAMENODES[0];
        String rpcAddress = "testrpcaddress";
        String serviceAddress = "testserviceaddress";
        String lifelineAddress = "testlifelineaddress";
        String blockPoolId = "testblockpool";
        String clusterId = "testcluster";
        String webAddress = "testwebaddress";
        boolean safemode = false;
        long startingTime = Time.now();
        // Expired
        MembershipState record = MembershipState.newInstance(FederationTestUtils.ROUTERS[0], ns, nn, clusterId, blockPoolId, rpcAddress, webAddress, lifelineAddress, webAddress, EXPIRED, safemode);
        record.setDateModified((startingTime - 10000));
        registrationList.add(record);
        // Expired
        record = MembershipState.newInstance(FederationTestUtils.ROUTERS[1], ns, nn, clusterId, blockPoolId, rpcAddress, serviceAddress, lifelineAddress, webAddress, EXPIRED, safemode);
        record.setDateModified(startingTime);
        registrationList.add(record);
        // Expired
        record = MembershipState.newInstance(FederationTestUtils.ROUTERS[2], ns, nn, clusterId, blockPoolId, rpcAddress, serviceAddress, lifelineAddress, webAddress, EXPIRED, safemode);
        record.setDateModified(startingTime);
        registrationList.add(record);
        // Expired
        record = MembershipState.newInstance(FederationTestUtils.ROUTERS[3], ns, nn, clusterId, blockPoolId, rpcAddress, serviceAddress, lifelineAddress, webAddress, EXPIRED, safemode);
        record.setDateModified(startingTime);
        registrationList.add(record);
        registerAndLoadRegistrations(registrationList);
        // Verify no entry is found for this nameservice
        Assert.assertNull(getNamenodeRegistration(record.getNameserviceId(), record.getNamenodeId()));
    }

    @Test
    public void testRegistrationNoQuorum() throws IOException, InterruptedException {
        // Populate the state store with a set of non-matching elements
        // 1) ns0:nn0 - Standby (newest)
        // 2) ns0:nn0 - Standby (oldest)
        // 3) ns0:nn0 - Active (2nd oldest)
        // 4) ns0:nn0 - Active (3nd oldest element, newest active element)
        // Verify the selected entry is the newest entry (1)
        MembershipState report1 = createRegistration(FederationTestUtils.NAMESERVICES[0], FederationTestUtils.NAMENODES[0], FederationTestUtils.ROUTERS[1], STANDBY);
        Assert.assertTrue(namenodeHeartbeat(report1));
        Thread.sleep(100);
        MembershipState report2 = createRegistration(FederationTestUtils.NAMESERVICES[0], FederationTestUtils.NAMENODES[0], FederationTestUtils.ROUTERS[2], ACTIVE);
        Assert.assertTrue(namenodeHeartbeat(report2));
        Thread.sleep(100);
        MembershipState report3 = createRegistration(FederationTestUtils.NAMESERVICES[0], FederationTestUtils.NAMENODES[0], FederationTestUtils.ROUTERS[3], ACTIVE);
        Assert.assertTrue(namenodeHeartbeat(report3));
        Thread.sleep(100);
        MembershipState report4 = createRegistration(FederationTestUtils.NAMESERVICES[0], FederationTestUtils.NAMENODES[0], FederationTestUtils.ROUTERS[0], STANDBY);
        Assert.assertTrue(namenodeHeartbeat(report4));
        // Load and calculate quorum
        Assert.assertTrue(TestStateStoreBase.getStateStore().loadCache(MembershipStore.class, true));
        // Verify quorum entry uses the newest data, even though it is standby
        MembershipState quorumEntry = getNamenodeRegistration(report1.getNameserviceId(), report1.getNamenodeId());
        Assert.assertNotNull(quorumEntry);
        Assert.assertEquals(FederationTestUtils.ROUTERS[0], quorumEntry.getRouterId());
        Assert.assertEquals(STANDBY, quorumEntry.getState());
    }

    @Test
    public void testRegistrationExpired() throws IOException, InterruptedException {
        // Populate the state store with a single NN element
        // 1) ns0:nn0 - Active
        // Wait for the entry to expire without heartbeating
        // Verify the NN entry is populated as EXPIRED internally in the state store
        MembershipState report = createRegistration(FederationTestUtils.NAMESERVICES[0], FederationTestUtils.NAMENODES[0], FederationTestUtils.ROUTERS[0], ACTIVE);
        Assert.assertTrue(namenodeHeartbeat(report));
        // Load cache
        Assert.assertTrue(TestStateStoreBase.getStateStore().loadCache(MembershipStore.class, true));
        // Verify quorum and entry
        MembershipState quorumEntry = getNamenodeRegistration(report.getNameserviceId(), report.getNamenodeId());
        Assert.assertNotNull(quorumEntry);
        Assert.assertEquals(FederationTestUtils.ROUTERS[0], quorumEntry.getRouterId());
        Assert.assertEquals(ACTIVE, quorumEntry.getState());
        // Wait past expiration (set in conf to 5 seconds)
        Thread.sleep(6000);
        // Reload cache
        Assert.assertTrue(TestStateStoreBase.getStateStore().loadCache(MembershipStore.class, true));
        // Verify entry is now expired and is no longer in the cache
        quorumEntry = getNamenodeRegistration(FederationTestUtils.NAMESERVICES[0], FederationTestUtils.NAMENODES[0]);
        Assert.assertNull(quorumEntry);
        // Verify entry is now expired and can't be used by RPC service
        quorumEntry = getNamenodeRegistration(report.getNameserviceId(), report.getNamenodeId());
        Assert.assertNull(quorumEntry);
        // Heartbeat again, updates dateModified
        Assert.assertTrue(namenodeHeartbeat(report));
        // Reload cache
        Assert.assertTrue(TestStateStoreBase.getStateStore().loadCache(MembershipStore.class, true));
        // Verify updated entry marked as active and is accessible to RPC server
        quorumEntry = getNamenodeRegistration(report.getNameserviceId(), report.getNamenodeId());
        Assert.assertNotNull(quorumEntry);
        Assert.assertEquals(FederationTestUtils.ROUTERS[0], quorumEntry.getRouterId());
        Assert.assertEquals(ACTIVE, quorumEntry.getState());
    }
}

