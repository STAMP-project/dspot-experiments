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


import DestinationOrder.HASH;
import DestinationOrder.HASH_ALL;
import DestinationOrder.LOCAL;
import DestinationOrder.RANDOM;
import java.io.IOException;
import java.security.PrivilegedExceptionAction;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.apache.hadoop.hdfs.server.federation.MiniRouterDFSCluster;
import org.apache.hadoop.hdfs.server.federation.StateStoreDFSCluster;
import org.apache.hadoop.hdfs.server.federation.resolver.MountTableManager;
import org.apache.hadoop.hdfs.server.federation.resolver.RemoteLocation;
import org.apache.hadoop.hdfs.server.federation.store.StateStoreService;
import org.apache.hadoop.hdfs.server.federation.store.protocol.AddMountTableEntryRequest;
import org.apache.hadoop.hdfs.server.federation.store.protocol.AddMountTableEntryResponse;
import org.apache.hadoop.hdfs.server.federation.store.protocol.DisableNameserviceRequest;
import org.apache.hadoop.hdfs.server.federation.store.protocol.DisableNameserviceResponse;
import org.apache.hadoop.hdfs.server.federation.store.protocol.EnableNameserviceRequest;
import org.apache.hadoop.hdfs.server.federation.store.protocol.EnableNameserviceResponse;
import org.apache.hadoop.hdfs.server.federation.store.protocol.RemoveMountTableEntryRequest;
import org.apache.hadoop.hdfs.server.federation.store.protocol.RemoveMountTableEntryResponse;
import org.apache.hadoop.hdfs.server.federation.store.protocol.UpdateMountTableEntryRequest;
import org.apache.hadoop.hdfs.server.federation.store.records.MountTable;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.util.Time;
import org.junit.Assert;
import org.junit.Test;


/**
 * The administrator interface of the {@link Router} implemented by
 * {@link RouterAdminServer}.
 */
public class TestRouterAdmin {
    private static StateStoreDFSCluster cluster;

    private static MiniRouterDFSCluster.RouterContext routerContext;

    public static final String RPC_BEAN = "Hadoop:service=Router,name=FederationRPC";

    private static List<MountTable> mockMountTable;

    private static StateStoreService stateStore;

    @Test
    public void testAddMountTable() throws IOException {
        MountTable newEntry = MountTable.newInstance("/testpath", Collections.singletonMap("ns0", "/testdir"), Time.now(), Time.now());
        RouterClient client = TestRouterAdmin.routerContext.getAdminClient();
        MountTableManager mountTable = client.getMountTableManager();
        // Existing mount table size
        List<MountTable> records = getMountTableEntries(mountTable);
        Assert.assertEquals(records.size(), TestRouterAdmin.mockMountTable.size());
        // Add
        AddMountTableEntryRequest addRequest = AddMountTableEntryRequest.newInstance(newEntry);
        AddMountTableEntryResponse addResponse = mountTable.addMountTableEntry(addRequest);
        Assert.assertTrue(addResponse.getStatus());
        // New mount table size
        List<MountTable> records2 = getMountTableEntries(mountTable);
        Assert.assertEquals(records2.size(), ((TestRouterAdmin.mockMountTable.size()) + 1));
    }

    @Test
    public void testAddDuplicateMountTable() throws IOException {
        MountTable newEntry = MountTable.newInstance("/testpath", Collections.singletonMap("ns0", "/testdir"), Time.now(), Time.now());
        RouterClient client = TestRouterAdmin.routerContext.getAdminClient();
        MountTableManager mountTable = client.getMountTableManager();
        // Existing mount table size
        List<MountTable> entries1 = getMountTableEntries(mountTable);
        Assert.assertEquals(entries1.size(), TestRouterAdmin.mockMountTable.size());
        // Add
        AddMountTableEntryRequest addRequest = AddMountTableEntryRequest.newInstance(newEntry);
        AddMountTableEntryResponse addResponse = mountTable.addMountTableEntry(addRequest);
        Assert.assertTrue(addResponse.getStatus());
        // New mount table size
        List<MountTable> entries2 = getMountTableEntries(mountTable);
        Assert.assertEquals(entries2.size(), ((TestRouterAdmin.mockMountTable.size()) + 1));
        // Add again, should fail
        AddMountTableEntryResponse addResponse2 = mountTable.addMountTableEntry(addRequest);
        Assert.assertFalse(addResponse2.getStatus());
    }

    @Test
    public void testAddReadOnlyMountTable() throws IOException {
        MountTable newEntry = MountTable.newInstance("/readonly", Collections.singletonMap("ns0", "/testdir"), Time.now(), Time.now());
        newEntry.setReadOnly(true);
        RouterClient client = TestRouterAdmin.routerContext.getAdminClient();
        MountTableManager mountTable = client.getMountTableManager();
        // Existing mount table size
        List<MountTable> records = getMountTableEntries(mountTable);
        Assert.assertEquals(records.size(), TestRouterAdmin.mockMountTable.size());
        // Add
        AddMountTableEntryRequest addRequest = AddMountTableEntryRequest.newInstance(newEntry);
        AddMountTableEntryResponse addResponse = mountTable.addMountTableEntry(addRequest);
        Assert.assertTrue(addResponse.getStatus());
        // New mount table size
        List<MountTable> records2 = getMountTableEntries(mountTable);
        Assert.assertEquals(records2.size(), ((TestRouterAdmin.mockMountTable.size()) + 1));
        // Check that we have the read only entry
        MountTable record = getMountTableEntry("/readonly");
        Assert.assertEquals("/readonly", record.getSourcePath());
        Assert.assertTrue(record.isReadOnly());
        // Removing the new entry
        RemoveMountTableEntryRequest removeRequest = RemoveMountTableEntryRequest.newInstance("/readonly");
        RemoveMountTableEntryResponse removeResponse = mountTable.removeMountTableEntry(removeRequest);
        Assert.assertTrue(removeResponse.getStatus());
    }

    @Test
    public void testAddOrderMountTable() throws IOException {
        testAddOrderMountTable(HASH);
        testAddOrderMountTable(LOCAL);
        testAddOrderMountTable(RANDOM);
        testAddOrderMountTable(HASH_ALL);
    }

    @Test
    public void testRemoveMountTable() throws IOException {
        RouterClient client = TestRouterAdmin.routerContext.getAdminClient();
        MountTableManager mountTable = client.getMountTableManager();
        // Existing mount table size
        List<MountTable> entries1 = getMountTableEntries(mountTable);
        Assert.assertEquals(entries1.size(), TestRouterAdmin.mockMountTable.size());
        // Remove an entry
        RemoveMountTableEntryRequest removeRequest = RemoveMountTableEntryRequest.newInstance("/");
        mountTable.removeMountTableEntry(removeRequest);
        // New mount table size
        List<MountTable> entries2 = getMountTableEntries(mountTable);
        Assert.assertEquals(entries2.size(), ((TestRouterAdmin.mockMountTable.size()) - 1));
    }

    @Test
    public void testEditMountTable() throws IOException {
        RouterClient client = TestRouterAdmin.routerContext.getAdminClient();
        MountTableManager mountTable = client.getMountTableManager();
        // Verify starting condition
        MountTable entry = getMountTableEntry("/");
        Assert.assertEquals(Collections.singletonList(new RemoteLocation("ns0", "/", "/")), entry.getDestinations());
        // Edit the entry for /
        MountTable updatedEntry = MountTable.newInstance("/", Collections.singletonMap("ns1", "/"), Time.now(), Time.now());
        UpdateMountTableEntryRequest updateRequest = UpdateMountTableEntryRequest.newInstance(updatedEntry);
        mountTable.updateMountTableEntry(updateRequest);
        // Verify edited condition
        entry = getMountTableEntry("/");
        Assert.assertEquals(Collections.singletonList(new RemoteLocation("ns1", "/", "/")), entry.getDestinations());
    }

    @Test
    public void testGetMountTable() throws IOException {
        RouterClient client = TestRouterAdmin.routerContext.getAdminClient();
        MountTableManager mountTable = client.getMountTableManager();
        // Verify size of table
        List<MountTable> entries = getMountTableEntries(mountTable);
        Assert.assertEquals(TestRouterAdmin.mockMountTable.size(), entries.size());
        // Verify all entries are present
        int matches = 0;
        for (MountTable e : entries) {
            for (MountTable entry : TestRouterAdmin.mockMountTable) {
                Assert.assertEquals(e.getDestinations().size(), 1);
                Assert.assertNotNull(e.getDateCreated());
                Assert.assertNotNull(e.getDateModified());
                if (entry.getSourcePath().equals(e.getSourcePath())) {
                    matches++;
                }
            }
        }
        Assert.assertEquals(matches, TestRouterAdmin.mockMountTable.size());
    }

    @Test
    public void testGetSingleMountTableEntry() throws IOException {
        MountTable entry = getMountTableEntry("/ns0");
        Assert.assertNotNull(entry);
        Assert.assertEquals(entry.getSourcePath(), "/ns0");
    }

    @Test
    public void testNameserviceManager() throws IOException {
        RouterClient client = TestRouterAdmin.routerContext.getAdminClient();
        NameserviceManager nsManager = client.getNameserviceManager();
        // There shouldn't be any name service disabled
        Set<String> disabled = getDisabledNameservices(nsManager);
        Assert.assertTrue(disabled.isEmpty());
        // Disable one and see it
        DisableNameserviceRequest disableReq = DisableNameserviceRequest.newInstance("ns0");
        DisableNameserviceResponse disableResp = nsManager.disableNameservice(disableReq);
        Assert.assertTrue(disableResp.getStatus());
        // Refresh the cache
        disabled = getDisabledNameservices(nsManager);
        Assert.assertEquals(1, disabled.size());
        Assert.assertTrue(disabled.contains("ns0"));
        // Enable one and we should have no disabled name services
        EnableNameserviceRequest enableReq = EnableNameserviceRequest.newInstance("ns0");
        EnableNameserviceResponse enableResp = nsManager.enableNameservice(enableReq);
        Assert.assertTrue(enableResp.getStatus());
        disabled = getDisabledNameservices(nsManager);
        Assert.assertTrue(disabled.isEmpty());
        // Non existing name services should fail
        disableReq = DisableNameserviceRequest.newInstance("nsunknown");
        disableResp = nsManager.disableNameservice(disableReq);
        Assert.assertFalse(disableResp.getStatus());
    }

    @Test
    public void testNameserviceManagerUnauthorized() throws Exception {
        // Try to disable a name service with a random user
        final String username = "baduser";
        UserGroupInformation user = UserGroupInformation.createRemoteUser(username);
        user.doAs(new PrivilegedExceptionAction<Void>() {
            @Override
            public Void run() throws Exception {
                RouterClient client = TestRouterAdmin.routerContext.getAdminClient();
                NameserviceManager nameservices = client.getNameserviceManager();
                DisableNameserviceRequest disableReq = DisableNameserviceRequest.newInstance("ns0");
                try {
                    nameservices.disableNameservice(disableReq);
                    Assert.fail("We should not be able to disable nameservices");
                } catch (IOException ioe) {
                    assertExceptionContains((username + " is not a super user"), ioe);
                }
                return null;
            }
        });
    }
}

