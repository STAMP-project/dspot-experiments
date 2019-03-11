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
package org.apache.hadoop.hdfs.server.federation.resolver;


import HAServiceState.ACTIVE;
import HAServiceState.STANDBY;
import java.io.IOException;
import java.util.List;
import org.apache.hadoop.hdfs.server.federation.FederationTestUtils;
import org.apache.hadoop.hdfs.server.federation.store.StateStoreService;
import org.apache.hadoop.hdfs.server.federation.store.StateStoreUnavailableException;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test the basic {@link ActiveNamenodeResolver} functionality.
 */
public class TestNamenodeResolver {
    private static StateStoreService stateStore;

    private static ActiveNamenodeResolver namenodeResolver;

    @Test
    public void testStateStoreDisconnected() throws Exception {
        // Add an entry to the store
        NamenodeStatusReport report = FederationTestUtils.createNamenodeReport(FederationTestUtils.NAMESERVICES[0], FederationTestUtils.NAMENODES[0], ACTIVE);
        Assert.assertTrue(TestNamenodeResolver.namenodeResolver.registerNamenode(report));
        // Close the data store driver
        TestNamenodeResolver.stateStore.closeDriver();
        Assert.assertFalse(TestNamenodeResolver.stateStore.isDriverReady());
        // Flush the caches
        TestNamenodeResolver.stateStore.refreshCaches(true);
        // Verify commands fail due to no cached data and no state store
        // connectivity.
        List<? extends FederationNamenodeContext> nns = TestNamenodeResolver.namenodeResolver.getNamenodesForBlockPoolId(FederationTestUtils.NAMESERVICES[0]);
        Assert.assertNull(nns);
        FederationTestUtils.verifyException(TestNamenodeResolver.namenodeResolver, "registerNamenode", StateStoreUnavailableException.class, new Class[]{ NamenodeStatusReport.class }, new Object[]{ report });
    }

    @Test
    public void testRegistrationExpired() throws IOException, InterruptedException {
        // Populate the state store with a single NN element
        // 1) ns0:nn0 - Active
        // Wait for the entry to expire without heartbeating
        // Verify the NN entry is not accessible once expired.
        NamenodeStatusReport report = FederationTestUtils.createNamenodeReport(FederationTestUtils.NAMESERVICES[0], FederationTestUtils.NAMENODES[0], ACTIVE);
        Assert.assertTrue(TestNamenodeResolver.namenodeResolver.registerNamenode(report));
        // Load cache
        TestNamenodeResolver.stateStore.refreshCaches(true);
        // Verify
        verifyFirstRegistration(FederationTestUtils.NAMESERVICES[0], FederationTestUtils.NAMENODES[0], 1, FederationNamenodeServiceState.ACTIVE);
        // Wait past expiration (set in conf to 5 seconds)
        Thread.sleep(6000);
        // Reload cache
        TestNamenodeResolver.stateStore.refreshCaches(true);
        // Verify entry is now expired and is no longer in the cache
        verifyFirstRegistration(FederationTestUtils.NAMESERVICES[0], FederationTestUtils.NAMENODES[0], 0, FederationNamenodeServiceState.ACTIVE);
        // Heartbeat again, updates dateModified
        Assert.assertTrue(TestNamenodeResolver.namenodeResolver.registerNamenode(report));
        // Reload cache
        TestNamenodeResolver.stateStore.refreshCaches(true);
        // Verify updated entry is marked active again and accessible to RPC server
        verifyFirstRegistration(FederationTestUtils.NAMESERVICES[0], FederationTestUtils.NAMENODES[0], 1, FederationNamenodeServiceState.ACTIVE);
    }

    @Test
    public void testRegistrationNamenodeSelection() throws IOException, InterruptedException {
        // 1) ns0:nn0 - Active
        // 2) ns0:nn1 - Standby (newest)
        // Verify the selected entry is the active entry
        Assert.assertTrue(TestNamenodeResolver.namenodeResolver.registerNamenode(FederationTestUtils.createNamenodeReport(FederationTestUtils.NAMESERVICES[0], FederationTestUtils.NAMENODES[0], ACTIVE)));
        Thread.sleep(100);
        Assert.assertTrue(TestNamenodeResolver.namenodeResolver.registerNamenode(FederationTestUtils.createNamenodeReport(FederationTestUtils.NAMESERVICES[0], FederationTestUtils.NAMENODES[1], STANDBY)));
        TestNamenodeResolver.stateStore.refreshCaches(true);
        verifyFirstRegistration(FederationTestUtils.NAMESERVICES[0], FederationTestUtils.NAMENODES[0], 2, FederationNamenodeServiceState.ACTIVE);
        // 1) ns0:nn0 - Expired (stale)
        // 2) ns0:nn1 - Standby (newest)
        // Verify the selected entry is the standby entry as the active entry is
        // stale
        Assert.assertTrue(TestNamenodeResolver.namenodeResolver.registerNamenode(FederationTestUtils.createNamenodeReport(FederationTestUtils.NAMESERVICES[0], FederationTestUtils.NAMENODES[0], ACTIVE)));
        // Expire active registration
        Thread.sleep(6000);
        // Refresh standby registration
        Assert.assertTrue(TestNamenodeResolver.namenodeResolver.registerNamenode(FederationTestUtils.createNamenodeReport(FederationTestUtils.NAMESERVICES[0], FederationTestUtils.NAMENODES[1], STANDBY)));
        // Verify that standby is selected (active is now expired)
        TestNamenodeResolver.stateStore.refreshCaches(true);
        verifyFirstRegistration(FederationTestUtils.NAMESERVICES[0], FederationTestUtils.NAMENODES[1], 1, FederationNamenodeServiceState.STANDBY);
        // 1) ns0:nn0 - Active
        // 2) ns0:nn1 - Unavailable (newest)
        // Verify the selected entry is the active entry
        Assert.assertTrue(TestNamenodeResolver.namenodeResolver.registerNamenode(FederationTestUtils.createNamenodeReport(FederationTestUtils.NAMESERVICES[0], FederationTestUtils.NAMENODES[0], ACTIVE)));
        Thread.sleep(100);
        Assert.assertTrue(TestNamenodeResolver.namenodeResolver.registerNamenode(FederationTestUtils.createNamenodeReport(FederationTestUtils.NAMESERVICES[0], FederationTestUtils.NAMENODES[1], null)));
        TestNamenodeResolver.stateStore.refreshCaches(true);
        verifyFirstRegistration(FederationTestUtils.NAMESERVICES[0], FederationTestUtils.NAMENODES[0], 2, FederationNamenodeServiceState.ACTIVE);
        // 1) ns0:nn0 - Unavailable (newest)
        // 2) ns0:nn1 - Standby
        // Verify the selected entry is the standby entry
        Assert.assertTrue(TestNamenodeResolver.namenodeResolver.registerNamenode(FederationTestUtils.createNamenodeReport(FederationTestUtils.NAMESERVICES[0], FederationTestUtils.NAMENODES[1], STANDBY)));
        Thread.sleep(1000);
        Assert.assertTrue(TestNamenodeResolver.namenodeResolver.registerNamenode(FederationTestUtils.createNamenodeReport(FederationTestUtils.NAMESERVICES[0], FederationTestUtils.NAMENODES[0], null)));
        TestNamenodeResolver.stateStore.refreshCaches(true);
        verifyFirstRegistration(FederationTestUtils.NAMESERVICES[0], FederationTestUtils.NAMENODES[1], 2, FederationNamenodeServiceState.STANDBY);
        // 1) ns0:nn0 - Active (oldest)
        // 2) ns0:nn1 - Standby
        // 3) ns0:nn2 - Active (newest)
        // Verify the selected entry is the newest active entry
        Assert.assertTrue(TestNamenodeResolver.namenodeResolver.registerNamenode(FederationTestUtils.createNamenodeReport(FederationTestUtils.NAMESERVICES[0], FederationTestUtils.NAMENODES[0], null)));
        Thread.sleep(100);
        Assert.assertTrue(TestNamenodeResolver.namenodeResolver.registerNamenode(FederationTestUtils.createNamenodeReport(FederationTestUtils.NAMESERVICES[0], FederationTestUtils.NAMENODES[1], STANDBY)));
        Thread.sleep(100);
        Assert.assertTrue(TestNamenodeResolver.namenodeResolver.registerNamenode(FederationTestUtils.createNamenodeReport(FederationTestUtils.NAMESERVICES[0], FederationTestUtils.NAMENODES[2], ACTIVE)));
        TestNamenodeResolver.stateStore.refreshCaches(true);
        verifyFirstRegistration(FederationTestUtils.NAMESERVICES[0], FederationTestUtils.NAMENODES[2], 3, FederationNamenodeServiceState.ACTIVE);
        // 1) ns0:nn0 - Standby (oldest)
        // 2) ns0:nn1 - Standby (newest)
        // 3) ns0:nn2 - Standby
        // Verify the selected entry is the newest standby entry
        Assert.assertTrue(TestNamenodeResolver.namenodeResolver.registerNamenode(FederationTestUtils.createNamenodeReport(FederationTestUtils.NAMESERVICES[0], FederationTestUtils.NAMENODES[0], STANDBY)));
        Assert.assertTrue(TestNamenodeResolver.namenodeResolver.registerNamenode(FederationTestUtils.createNamenodeReport(FederationTestUtils.NAMESERVICES[0], FederationTestUtils.NAMENODES[2], STANDBY)));
        Thread.sleep(1500);
        Assert.assertTrue(TestNamenodeResolver.namenodeResolver.registerNamenode(FederationTestUtils.createNamenodeReport(FederationTestUtils.NAMESERVICES[0], FederationTestUtils.NAMENODES[1], STANDBY)));
        TestNamenodeResolver.stateStore.refreshCaches(true);
        verifyFirstRegistration(FederationTestUtils.NAMESERVICES[0], FederationTestUtils.NAMENODES[1], 3, FederationNamenodeServiceState.STANDBY);
    }
}

