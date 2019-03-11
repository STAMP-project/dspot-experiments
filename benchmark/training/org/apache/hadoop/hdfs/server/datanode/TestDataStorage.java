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


import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import org.apache.hadoop.hdfs.server.common.HdfsServerConstants.StartupOption;
import org.apache.hadoop.hdfs.server.common.Storage;
import org.apache.hadoop.hdfs.server.common.Storage.StorageDirectory;
import org.apache.hadoop.hdfs.server.protocol.NamespaceInfo;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TestDataStorage {
    private static final String DEFAULT_BPID = "bp-0";

    private static final String CLUSTER_ID = "cluster0";

    private static final String BUILD_VERSION = "2.0";

    private static final String SOFTWARE_VERSION = "2.0";

    private static final long CTIME = 1;

    private static final File TEST_DIR = GenericTestUtils.getTestDir("dstest");

    private static final StartupOption START_OPT = StartupOption.REGULAR;

    private DataNode mockDN = Mockito.mock(DataNode.class);

    private NamespaceInfo nsInfo;

    private DataStorage storage;

    @Test
    public void testAddStorageDirectories() throws IOException, URISyntaxException {
        final int numLocations = 3;
        final int numNamespace = 3;
        List<StorageLocation> locations = TestDataStorage.createStorageLocations(numLocations);
        // Add volumes for multiple namespaces.
        List<NamespaceInfo> namespaceInfos = TestDataStorage.createNamespaceInfos(numNamespace);
        for (NamespaceInfo ni : namespaceInfos) {
            storage.addStorageLocations(mockDN, ni, locations, TestDataStorage.START_OPT);
            for (StorageLocation sl : locations) {
                TestDataStorage.checkDir(new File(sl.getUri()));
                TestDataStorage.checkDir(new File(sl.getUri()), ni.getBlockPoolID());
            }
        }
        Assert.assertEquals(numLocations, storage.getNumStorageDirs());
        locations = TestDataStorage.createStorageLocations(numLocations);
        List<StorageDirectory> addedLocation = storage.addStorageLocations(mockDN, namespaceInfos.get(0), locations, TestDataStorage.START_OPT);
        Assert.assertTrue(addedLocation.isEmpty());
        // The number of active storage dirs has not changed, since it tries to
        // add the storage dirs that are under service.
        Assert.assertEquals(numLocations, storage.getNumStorageDirs());
        // Add more directories.
        locations = TestDataStorage.createStorageLocations(6);
        storage.addStorageLocations(mockDN, nsInfo, locations, TestDataStorage.START_OPT);
        Assert.assertEquals(6, storage.getNumStorageDirs());
    }

    @Test
    public void testMissingVersion() throws IOException, URISyntaxException {
        final int numLocations = 1;
        final int numNamespace = 1;
        List<StorageLocation> locations = TestDataStorage.createStorageLocations(numLocations);
        StorageLocation firstStorage = locations.get(0);
        Storage.StorageDirectory sd = new Storage.StorageDirectory(firstStorage);
        // the directory is not initialized so VERSION does not exist
        // create a fake directory under current/
        File currentDir = new File(sd.getCurrentDir(), "BP-787466439-172.26.24.43-1462305406642");
        Assert.assertTrue(("unable to mkdir " + (currentDir.getName())), currentDir.mkdirs());
        // Add volumes for multiple namespaces.
        List<NamespaceInfo> namespaceInfos = TestDataStorage.createNamespaceInfos(numNamespace);
        for (NamespaceInfo ni : namespaceInfos) {
            storage.addStorageLocations(mockDN, ni, locations, TestDataStorage.START_OPT);
        }
        // It should not format the directory because VERSION is missing.
        Assert.assertTrue("Storage directory was formatted", currentDir.exists());
    }

    @Test
    public void testRecoverTransitionReadFailure() throws IOException {
        final int numLocations = 3;
        List<StorageLocation> locations = TestDataStorage.createStorageLocations(numLocations, true);
        try {
            storage.recoverTransitionRead(mockDN, nsInfo, locations, TestDataStorage.START_OPT);
            Assert.fail("An IOException should throw: all StorageLocations are NON_EXISTENT");
        } catch (IOException e) {
            GenericTestUtils.assertExceptionContains("All specified directories have failed to load.", e);
        }
        Assert.assertEquals(0, storage.getNumStorageDirs());
    }

    /**
     * This test enforces the behavior that if there is an exception from
     * doTransition() during DN starts up, the storage directories that have
     * already been processed are still visible, i.e., in
     * DataStorage.storageDirs().
     */
    @Test
    public void testRecoverTransitionReadDoTransitionFailure() throws IOException {
        final int numLocations = 3;
        List<StorageLocation> locations = TestDataStorage.createStorageLocations(numLocations);
        // Prepare volumes
        storage.recoverTransitionRead(mockDN, nsInfo, locations, TestDataStorage.START_OPT);
        Assert.assertEquals(numLocations, storage.getNumStorageDirs());
        // Reset DataStorage
        storage.unlockAll();
        storage = new DataStorage();
        // Trigger an exception from doTransition().
        nsInfo.clusterID = "cluster1";
        try {
            storage.recoverTransitionRead(mockDN, nsInfo, locations, TestDataStorage.START_OPT);
            Assert.fail("Expect to throw an exception from doTransition()");
        } catch (IOException e) {
            GenericTestUtils.assertExceptionContains("All specified directories", e);
        }
        Assert.assertEquals(0, storage.getNumStorageDirs());
    }
}

