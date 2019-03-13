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


import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.hdfs.server.federation.FederationTestUtils;
import org.apache.hadoop.hdfs.server.federation.store.protocol.AddMountTableEntryRequest;
import org.apache.hadoop.hdfs.server.federation.store.protocol.AddMountTableEntryResponse;
import org.apache.hadoop.hdfs.server.federation.store.protocol.GetMountTableEntriesRequest;
import org.apache.hadoop.hdfs.server.federation.store.protocol.RemoveMountTableEntryRequest;
import org.apache.hadoop.hdfs.server.federation.store.protocol.UpdateMountTableEntryRequest;
import org.apache.hadoop.hdfs.server.federation.store.protocol.UpdateMountTableEntryResponse;
import org.apache.hadoop.hdfs.server.federation.store.records.MountTable;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test the basic {@link StateStoreService}
 * {@link MountTableStore} functionality.
 */
public class TestStateStoreMountTable extends TestStateStoreBase {
    private static List<String> nameservices;

    private static MountTableStore mountStore;

    @Test
    public void testStateStoreDisconnected() throws Exception {
        // Close the data store driver
        TestStateStoreBase.getStateStore().closeDriver();
        Assert.assertFalse(TestStateStoreBase.getStateStore().isDriverReady());
        // Test APIs that access the store to check they throw the correct exception
        AddMountTableEntryRequest addRequest = AddMountTableEntryRequest.newInstance();
        FederationTestUtils.verifyException(TestStateStoreMountTable.mountStore, "addMountTableEntry", StateStoreUnavailableException.class, new Class[]{ AddMountTableEntryRequest.class }, new Object[]{ addRequest });
        UpdateMountTableEntryRequest updateRequest = UpdateMountTableEntryRequest.newInstance();
        FederationTestUtils.verifyException(TestStateStoreMountTable.mountStore, "updateMountTableEntry", StateStoreUnavailableException.class, new Class[]{ UpdateMountTableEntryRequest.class }, new Object[]{ updateRequest });
        RemoveMountTableEntryRequest removeRequest = RemoveMountTableEntryRequest.newInstance();
        FederationTestUtils.verifyException(TestStateStoreMountTable.mountStore, "removeMountTableEntry", StateStoreUnavailableException.class, new Class[]{ RemoveMountTableEntryRequest.class }, new Object[]{ removeRequest });
        GetMountTableEntriesRequest getRequest = GetMountTableEntriesRequest.newInstance();
        TestStateStoreMountTable.mountStore.loadCache(true);
        FederationTestUtils.verifyException(TestStateStoreMountTable.mountStore, "getMountTableEntries", StateStoreUnavailableException.class, new Class[]{ GetMountTableEntriesRequest.class }, new Object[]{ getRequest });
    }

    @Test
    public void testSynchronizeMountTable() throws IOException {
        // Synchronize and get mount table entries
        List<MountTable> entries = FederationStateStoreTestUtils.createMockMountTable(TestStateStoreMountTable.nameservices);
        Assert.assertTrue(FederationStateStoreTestUtils.synchronizeRecords(TestStateStoreBase.getStateStore(), entries, MountTable.class));
        for (MountTable e : entries) {
            TestStateStoreMountTable.mountStore.loadCache(true);
            MountTable entry = getMountTableEntry(e.getSourcePath());
            Assert.assertNotNull(entry);
            Assert.assertEquals(e.getDefaultLocation().getDest(), entry.getDefaultLocation().getDest());
        }
    }

    @Test
    public void testAddMountTableEntry() throws IOException {
        // Add 1
        List<MountTable> entries = FederationStateStoreTestUtils.createMockMountTable(TestStateStoreMountTable.nameservices);
        List<MountTable> entries1 = getMountTableEntries("/").getRecords();
        Assert.assertEquals(0, entries1.size());
        MountTable entry0 = entries.get(0);
        AddMountTableEntryRequest request = AddMountTableEntryRequest.newInstance(entry0);
        AddMountTableEntryResponse response = TestStateStoreMountTable.mountStore.addMountTableEntry(request);
        Assert.assertTrue(response.getStatus());
        TestStateStoreMountTable.mountStore.loadCache(true);
        List<MountTable> entries2 = getMountTableEntries("/").getRecords();
        Assert.assertEquals(1, entries2.size());
    }

    @Test
    public void testRemoveMountTableEntry() throws IOException {
        // Add many
        List<MountTable> entries = FederationStateStoreTestUtils.createMockMountTable(TestStateStoreMountTable.nameservices);
        FederationStateStoreTestUtils.synchronizeRecords(TestStateStoreBase.getStateStore(), entries, MountTable.class);
        TestStateStoreMountTable.mountStore.loadCache(true);
        List<MountTable> entries1 = getMountTableEntries("/").getRecords();
        Assert.assertEquals(entries.size(), entries1.size());
        // Remove 1
        RemoveMountTableEntryRequest request = RemoveMountTableEntryRequest.newInstance();
        request.setSrcPath(entries.get(0).getSourcePath());
        Assert.assertTrue(TestStateStoreMountTable.mountStore.removeMountTableEntry(request).getStatus());
        // Verify remove
        TestStateStoreMountTable.mountStore.loadCache(true);
        List<MountTable> entries2 = getMountTableEntries("/").getRecords();
        Assert.assertEquals(((entries.size()) - 1), entries2.size());
    }

    @Test
    public void testUpdateMountTableEntry() throws IOException {
        // Add 1
        List<MountTable> entries = FederationStateStoreTestUtils.createMockMountTable(TestStateStoreMountTable.nameservices);
        MountTable entry0 = entries.get(0);
        String srcPath = entry0.getSourcePath();
        String nsId = entry0.getDefaultLocation().getNameserviceId();
        AddMountTableEntryRequest request = AddMountTableEntryRequest.newInstance(entry0);
        AddMountTableEntryResponse response = TestStateStoreMountTable.mountStore.addMountTableEntry(request);
        Assert.assertTrue(response.getStatus());
        // Verify
        TestStateStoreMountTable.mountStore.loadCache(true);
        MountTable matchingEntry0 = getMountTableEntry(srcPath);
        Assert.assertNotNull(matchingEntry0);
        Assert.assertEquals(nsId, matchingEntry0.getDefaultLocation().getNameserviceId());
        // Edit destination nameservice for source path
        Map<String, String> destMap = Collections.singletonMap("testnameservice", "/");
        MountTable replacement = MountTable.newInstance(srcPath, destMap);
        UpdateMountTableEntryRequest updateRequest = UpdateMountTableEntryRequest.newInstance(replacement);
        UpdateMountTableEntryResponse updateResponse = TestStateStoreMountTable.mountStore.updateMountTableEntry(updateRequest);
        Assert.assertTrue(updateResponse.getStatus());
        // Verify
        TestStateStoreMountTable.mountStore.loadCache(true);
        MountTable matchingEntry1 = getMountTableEntry(srcPath);
        Assert.assertNotNull(matchingEntry1);
        Assert.assertEquals("testnameservice", matchingEntry1.getDefaultLocation().getNameserviceId());
    }
}

