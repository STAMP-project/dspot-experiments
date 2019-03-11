/**
 * Copyright 2017 LinkedIn Corp. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
package com.github.ambry.server;


import BlobId.BlobDataType;
import BlobId.BlobIdType;
import MockClusterMap.DEFAULT_PARTITION_CLASS;
import ReplicaEventType.Disk_Error;
import ReplicaEventType.Disk_Ok;
import RequestOrResponseType.AdminRequest;
import ServerErrorCode.Bad_Request;
import ServerErrorCode.Blob_Not_Found;
import ServerErrorCode.Disk_Unavailable;
import ServerErrorCode.No_Error;
import ServerErrorCode.Replica_Unavailable;
import ServerErrorCode.Retry_After_Backoff;
import ServerErrorCode.Unknown_Error;
import TestUtils.RANDOM;
import com.codahale.metrics.MetricRegistry;
import com.github.ambry.clustermap.ClusterMap;
import com.github.ambry.clustermap.ClusterMapUtils;
import com.github.ambry.clustermap.DataNodeId;
import com.github.ambry.clustermap.MockClusterMap;
import com.github.ambry.clustermap.MockPartitionId;
import com.github.ambry.clustermap.PartitionId;
import com.github.ambry.clustermap.ReplicaId;
import com.github.ambry.commons.BlobId;
import com.github.ambry.commons.CommonTestUtils;
import com.github.ambry.config.ClusterMapConfig;
import com.github.ambry.config.ReplicationConfig;
import com.github.ambry.config.StoreConfig;
import com.github.ambry.config.VerifiableProperties;
import com.github.ambry.messageformat.MessageFormatException;
import com.github.ambry.network.Request;
import com.github.ambry.network.Send;
import com.github.ambry.network.ServerNetworkResponseMetrics;
import com.github.ambry.network.SocketRequestResponseChannel;
import com.github.ambry.protocol.RequestOrResponse;
import com.github.ambry.protocol.RequestOrResponseType;
import com.github.ambry.replication.MockFindTokenFactory;
import com.github.ambry.replication.ReplicationException;
import com.github.ambry.replication.ReplicationManager;
import com.github.ambry.store.FindInfo;
import com.github.ambry.store.FindToken;
import com.github.ambry.store.FindTokenFactory;
import com.github.ambry.store.MessageReadSet;
import com.github.ambry.store.MessageWriteSet;
import com.github.ambry.store.MockStoreKeyConverterFactory;
import com.github.ambry.store.StorageManager;
import com.github.ambry.store.Store;
import com.github.ambry.store.StoreErrorCodes;
import com.github.ambry.store.StoreException;
import com.github.ambry.store.StoreGetOptions;
import com.github.ambry.store.StoreInfo;
import com.github.ambry.store.StoreKey;
import com.github.ambry.store.StoreKeyConverterFactory;
import com.github.ambry.store.StoreKeyFactory;
import com.github.ambry.store.StoreStats;
import com.github.ambry.utils.ByteBufferChannel;
import com.github.ambry.utils.ByteBufferInputStream;
import com.github.ambry.utils.MockTime;
import com.github.ambry.utils.SystemTime;
import com.github.ambry.utils.Utils;
import com.github.ambry.utils.UtilsTest;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link AmbryRequests}.
 */
public class AmbryRequestsTest {
    private static final FindTokenFactory FIND_TOKEN_FACTORY = new MockFindTokenFactory();

    private final MockClusterMap clusterMap;

    private final DataNodeId dataNodeId;

    private final AmbryRequestsTest.MockStorageManager storageManager;

    private final AmbryRequestsTest.MockReplicationManager replicationManager;

    private final AmbryRequests ambryRequests;

    private final AmbryRequestsTest.MockRequestResponseChannel requestResponseChannel = new AmbryRequestsTest.MockRequestResponseChannel();

    private final Set<StoreKey> validKeysInStore = new HashSet<>();

    private final Map<StoreKey, StoreKey> conversionMap = new HashMap<>();

    private final MockStoreKeyConverterFactory storeKeyConverterFactory;

    public AmbryRequestsTest() throws ReplicationException, StoreException, IOException, InterruptedException {
        clusterMap = new MockClusterMap();
        Properties properties = new Properties();
        properties.setProperty("clustermap.cluster.name", "test");
        properties.setProperty("clustermap.datacenter.name", "DC1");
        properties.setProperty("clustermap.host.name", "localhost");
        properties.setProperty("replication.token.factory", "com.github.ambry.store.StoreFindTokenFactory");
        properties.setProperty("replication.no.of.intra.dc.replica.threads", "0");
        properties.setProperty("replication.no.of.inter.dc.replica.threads", "0");
        VerifiableProperties verifiableProperties = new VerifiableProperties(properties);
        dataNodeId = clusterMap.getDataNodeIds().get(0);
        storageManager = new AmbryRequestsTest.MockStorageManager(validKeysInStore, clusterMap.getReplicaIds(dataNodeId));
        storeKeyConverterFactory = new MockStoreKeyConverterFactory(null, null);
        storeKeyConverterFactory.setConversionMap(conversionMap);
        replicationManager = AmbryRequestsTest.MockReplicationManager.getReplicationManager(verifiableProperties, storageManager, clusterMap, dataNodeId, storeKeyConverterFactory);
        ambryRequests = new AmbryRequests(storageManager, requestResponseChannel, clusterMap, dataNodeId, clusterMap.getMetricRegistry(), AmbryRequestsTest.FIND_TOKEN_FACTORY, null, replicationManager, null, false, storeKeyConverterFactory);
        start();
    }

    /**
     * Tests that compactions are scheduled correctly.
     *
     * @throws InterruptedException
     * 		
     * @throws IOException
     * 		
     */
    @Test
    public void scheduleCompactionSuccessTest() throws IOException, InterruptedException {
        List<? extends PartitionId> partitionIds = clusterMap.getWritablePartitionIds(DEFAULT_PARTITION_CLASS);
        for (PartitionId id : partitionIds) {
            doScheduleCompactionTest(id, No_Error);
            Assert.assertEquals("Partition scheduled for compaction not as expected", id, storageManager.compactionScheduledPartitionId);
        }
    }

    /**
     * Tests failure scenarios for compaction - disk down, store not scheduled for compaction, exception while scheduling.
     *
     * @throws InterruptedException
     * 		
     * @throws IOException
     * 		
     */
    @Test
    public void scheduleCompactionFailureTest() throws IOException, InterruptedException {
        // partitionId not specified
        doScheduleCompactionTest(null, Bad_Request);
        PartitionId id = clusterMap.getWritablePartitionIds(DEFAULT_PARTITION_CLASS).get(0);
        // store is not started - Replica_Unavailable
        storageManager.returnNullStore = true;
        doScheduleCompactionTest(id, Replica_Unavailable);
        storageManager.returnNullStore = false;
        // all stores are shutdown - Disk_Unavailable. This is simulated by shutting down the storage manager.
        shutdown();
        storageManager.returnNullStore = true;
        doScheduleCompactionTest(id, Disk_Unavailable);
        storageManager.returnNullStore = false;
        start();
        // make sure the disk is up when storageManager is restarted.
        doScheduleCompactionTest(id, No_Error);
        // PartitionUnknown is hard to simulate without betraying knowledge of the internals of MockClusterMap.
        // disk unavailable
        ReplicaId replicaId = findReplica(id);
        clusterMap.onReplicaEvent(replicaId, Disk_Error);
        doScheduleCompactionTest(id, Disk_Unavailable);
        clusterMap.onReplicaEvent(replicaId, Disk_Ok);
        // store cannot be scheduled for compaction - Unknown_Error
        storageManager.returnValueOfSchedulingCompaction = false;
        doScheduleCompactionTest(id, Unknown_Error);
        storageManager.returnValueOfSchedulingCompaction = true;
        // exception while attempting to schedule - InternalServerError
        storageManager.exceptionToThrowOnSchedulingCompaction = new IllegalStateException();
        doScheduleCompactionTest(id, Unknown_Error);
        storageManager.exceptionToThrowOnSchedulingCompaction = null;
    }

    /**
     * Tests that {@link AdminRequestOrResponseType#RequestControl} works correctly.
     *
     * @throws InterruptedException
     * 		
     * @throws IOException
     * 		
     */
    @Test
    public void controlRequestSuccessTest() throws IOException, InterruptedException {
        RequestOrResponseType[] requestOrResponseTypes = new RequestOrResponseType[]{ RequestOrResponseType.PutRequest, RequestOrResponseType.DeleteRequest, RequestOrResponseType.GetRequest, RequestOrResponseType.ReplicaMetadataRequest, RequestOrResponseType.TtlUpdateRequest };
        for (RequestOrResponseType requestType : requestOrResponseTypes) {
            List<? extends PartitionId> partitionIds = clusterMap.getWritablePartitionIds(DEFAULT_PARTITION_CLASS);
            for (PartitionId id : partitionIds) {
                doRequestControlRequestTest(requestType, id);
            }
            doRequestControlRequestTest(requestType, null);
        }
    }

    /**
     * Tests that {@link AdminRequestOrResponseType#RequestControl} fails when bad input is provided (or when there is
     * bad internal state).
     */
    @Test
    public void controlRequestFailureTest() throws IOException, InterruptedException {
        // cannot disable admin request
        sendAndVerifyRequestControlRequest(AdminRequest, false, null, Bad_Request);
        // PartitionUnknown is hard to simulate without betraying knowledge of the internals of MockClusterMap.
    }

    /**
     * Tests that {@link AdminRequestOrResponseType#ReplicationControl} works correctly.
     *
     * @throws InterruptedException
     * 		
     * @throws IOException
     * 		
     */
    @Test
    public void controlReplicationSuccessTest() throws IOException, InterruptedException {
        List<? extends PartitionId> partitionIds = clusterMap.getWritablePartitionIds(DEFAULT_PARTITION_CLASS);
        for (PartitionId id : partitionIds) {
            doControlReplicationTest(id, No_Error);
        }
        doControlReplicationTest(null, No_Error);
    }

    /**
     * Tests that {@link AdminRequestOrResponseType#ReplicationControl} fails when bad input is provided (or when there is
     * bad internal state).
     */
    @Test
    public void controlReplicationFailureTest() throws IOException, InterruptedException {
        replicationManager.reset();
        replicationManager.controlReplicationReturnVal = false;
        sendAndVerifyReplicationControlRequest(Collections.EMPTY_LIST, false, clusterMap.getWritablePartitionIds(DEFAULT_PARTITION_CLASS).get(0), Bad_Request);
        replicationManager.reset();
        replicationManager.exceptionToThrow = new IllegalStateException();
        sendAndVerifyReplicationControlRequest(Collections.EMPTY_LIST, false, clusterMap.getWritablePartitionIds(DEFAULT_PARTITION_CLASS).get(0), Unknown_Error);
        // PartitionUnknown is hard to simulate without betraying knowledge of the internals of MockClusterMap.
    }

    /**
     * Tests for the response received on a {@link CatchupStatusAdminRequest} for different cases
     *
     * @throws InterruptedException
     * 		
     * @throws IOException
     * 		
     */
    @Test
    public void catchupStatusSuccessTest() throws IOException, InterruptedException {
        List<? extends PartitionId> partitionIds = clusterMap.getAllPartitionIds(null);
        Assert.assertTrue("This test needs more than one partition to work", ((partitionIds.size()) > 1));
        PartitionId id = partitionIds.get(0);
        ReplicaId thisPartRemoteRep = getRemoteReplicaId(id);
        ReplicaId otherPartRemoteRep = getRemoteReplicaId(partitionIds.get(1));
        List<? extends ReplicaId> replicaIds = id.getReplicaIds();
        Assert.assertTrue("This test needs more than one replica for the first partition to work", ((replicaIds.size()) > 1));
        long acceptableLagInBytes = 100;
        // cases with a given partition id
        // all replicas of given partition < acceptableLag
        generateLagOverrides(0, (acceptableLagInBytes - 1));
        doCatchupStatusTest(id, acceptableLagInBytes, Short.MAX_VALUE, No_Error, true);
        // all replicas of given partition = acceptableLag
        generateLagOverrides(acceptableLagInBytes, acceptableLagInBytes);
        doCatchupStatusTest(id, acceptableLagInBytes, Short.MAX_VALUE, No_Error, true);
        // 1 replica of some other partition > acceptableLag
        String key = AmbryRequestsTest.MockReplicationManager.getPartitionLagKey(otherPartRemoteRep.getPartitionId(), otherPartRemoteRep.getDataNodeId().getHostname(), otherPartRemoteRep.getReplicaPath());
        replicationManager.lagOverrides.put(key, (acceptableLagInBytes + 1));
        doCatchupStatusTest(id, acceptableLagInBytes, Short.MAX_VALUE, No_Error, true);
        // 1 replica of this partition > acceptableLag
        key = AmbryRequestsTest.MockReplicationManager.getPartitionLagKey(id, thisPartRemoteRep.getDataNodeId().getHostname(), thisPartRemoteRep.getReplicaPath());
        replicationManager.lagOverrides.put(key, (acceptableLagInBytes + 1));
        doCatchupStatusTest(id, acceptableLagInBytes, Short.MAX_VALUE, No_Error, false);
        // same result if num expected replicas == total count -1.
        doCatchupStatusTest(id, acceptableLagInBytes, ((short) ((replicaIds.size()) - 1)), No_Error, false);
        // caught up if num expected replicas == total count - 2
        doCatchupStatusTest(id, acceptableLagInBytes, ((short) ((replicaIds.size()) - 2)), No_Error, true);
        // caught up if num expected replicas == total count - 3
        doCatchupStatusTest(id, acceptableLagInBytes, ((short) ((replicaIds.size()) - 3)), No_Error, true);
        // all replicas of this partition > acceptableLag
        generateLagOverrides((acceptableLagInBytes + 1), (acceptableLagInBytes + 1));
        doCatchupStatusTest(id, acceptableLagInBytes, Short.MAX_VALUE, No_Error, false);
        // cases with no partition id provided
        // all replicas of all partitions < acceptableLag
        generateLagOverrides(0, (acceptableLagInBytes - 1));
        doCatchupStatusTest(null, acceptableLagInBytes, Short.MAX_VALUE, No_Error, true);
        // all replicas of all partitions = acceptableLag
        generateLagOverrides(acceptableLagInBytes, acceptableLagInBytes);
        doCatchupStatusTest(null, acceptableLagInBytes, Short.MAX_VALUE, No_Error, true);
        // 1 replica of one partition > acceptableLag
        key = AmbryRequestsTest.MockReplicationManager.getPartitionLagKey(id, thisPartRemoteRep.getDataNodeId().getHostname(), thisPartRemoteRep.getReplicaPath());
        replicationManager.lagOverrides.put(key, (acceptableLagInBytes + 1));
        doCatchupStatusTest(null, acceptableLagInBytes, Short.MAX_VALUE, No_Error, false);
        // same result if num expected replicas == total count -1.
        doCatchupStatusTest(null, acceptableLagInBytes, ((short) ((replicaIds.size()) - 1)), No_Error, false);
        // caught up if num expected replicas == total count - 2
        doCatchupStatusTest(null, acceptableLagInBytes, ((short) ((replicaIds.size()) - 2)), No_Error, true);
        // caught up if num expected replicas == total count - 3
        doCatchupStatusTest(null, acceptableLagInBytes, ((short) ((replicaIds.size()) - 3)), No_Error, true);
        // all replicas of all partitions > acceptableLag
        generateLagOverrides((acceptableLagInBytes + 1), (acceptableLagInBytes + 1));
        doCatchupStatusTest(null, acceptableLagInBytes, Short.MAX_VALUE, No_Error, false);
    }

    /**
     * Tests that {@link AdminRequestOrResponseType#CatchupStatus} fails when bad input is provided (or when there is
     * bad internal state).
     */
    @Test
    public void catchupStatusFailureTest() throws IOException, InterruptedException {
        // acceptableLagInBytes < 0
        doCatchupStatusTest(null, (-1), Short.MAX_VALUE, Bad_Request, false);
        // numReplicasCaughtUpPerPartition = 0
        doCatchupStatusTest(null, 0, ((short) (0)), Bad_Request, false);
        // numReplicasCaughtUpPerPartition < 0
        doCatchupStatusTest(null, 0, ((short) (-1)), Bad_Request, false);
        // replication manager error
        replicationManager.reset();
        replicationManager.exceptionToThrow = new IllegalStateException();
        doCatchupStatusTest(null, 0, Short.MAX_VALUE, Unknown_Error, false);
    }

    /**
     * Tests for the response received on a {@link BlobStoreControlAdminRequest} for successful case
     *
     * @throws InterruptedException
     * 		
     * @throws IOException
     * 		
     */
    @Test
    public void controlBlobStoreSuccessTest() throws IOException, InterruptedException {
        List<? extends PartitionId> partitionIds = clusterMap.getAllPartitionIds(null);
        PartitionId id = partitionIds.get(0);
        List<? extends ReplicaId> replicaIds = id.getReplicaIds();
        Assert.assertTrue("This test needs more than one replica for the first partition to work", ((replicaIds.size()) > 1));
        long acceptableLagInBytes = 0;
        short numReplicasCaughtUpPerPartition = 3;
        replicationManager.reset();
        replicationManager.controlReplicationReturnVal = true;
        generateLagOverrides(0, acceptableLagInBytes);
        // stop BlobStore
        sendAndVerifyStoreControlRequest(id, false, numReplicasCaughtUpPerPartition, No_Error);
        // verify APIs are called in the process of stopping BlobStore
        Assert.assertEquals("Compaction on store should be disabled after stopping the BlobStore", false, storageManager.compactionEnableVal);
        Assert.assertEquals("Partition disabled for compaction not as expected", id, storageManager.compactionControlledPartitionId);
        Assert.assertEquals("Origins list should be empty", true, replicationManager.originsVal.isEmpty());
        Assert.assertEquals("Replication on given BlobStore should be disabled", false, replicationManager.enableVal);
        Assert.assertEquals("Partition shutdown not as expected", id, storageManager.shutdownPartitionId);
        // start BlobStore
        sendAndVerifyStoreControlRequest(id, true, numReplicasCaughtUpPerPartition, No_Error);
        // verify APIs are called in the process of starting BlobStore
        Assert.assertEquals("Partition started not as expected", id, storageManager.startedPartitionId);
        Assert.assertEquals("Replication on given BlobStore should be enabled", true, replicationManager.enableVal);
        Assert.assertEquals("Partition controlled for compaction not as expected", id, storageManager.compactionControlledPartitionId);
        Assert.assertEquals("Compaction on store should be enabled after starting the BlobStore", true, storageManager.compactionEnableVal);
    }

    /**
     * Tests for the startBlobStore response received on a {@link BlobStoreControlAdminRequest} for different failure cases
     *
     * @throws InterruptedException
     * 		
     * @throws IOException
     * 		
     */
    @Test
    public void startBlobStoreFailureTest() throws IOException, InterruptedException {
        List<? extends PartitionId> partitionIds = clusterMap.getAllPartitionIds(null);
        PartitionId id = partitionIds.get(0);
        short numReplicasCaughtUpPerPartition = 3;
        // test start BlobStore failure
        storageManager.returnValueOfStartingBlobStore = false;
        sendAndVerifyStoreControlRequest(id, true, numReplicasCaughtUpPerPartition, Unknown_Error);
        storageManager.returnValueOfStartingBlobStore = true;
        // test start BlobStore with runtime exception
        storageManager.exceptionToThrowOnStartingBlobStore = new IllegalStateException();
        sendAndVerifyStoreControlRequest(id, true, numReplicasCaughtUpPerPartition, Unknown_Error);
        storageManager.exceptionToThrowOnStartingBlobStore = null;
        // test enable replication failure
        replicationManager.controlReplicationReturnVal = false;
        sendAndVerifyStoreControlRequest(id, true, numReplicasCaughtUpPerPartition, Unknown_Error);
        replicationManager.controlReplicationReturnVal = true;
        // test enable compaction failure
        storageManager.returnValueOfControllingCompaction = false;
        sendAndVerifyStoreControlRequest(id, true, numReplicasCaughtUpPerPartition, Unknown_Error);
        storageManager.returnValueOfControllingCompaction = true;
        // test enable compaction with runtime exception
        storageManager.exceptionToThrowOnControllingCompaction = new IllegalStateException();
        sendAndVerifyStoreControlRequest(id, true, numReplicasCaughtUpPerPartition, Unknown_Error);
        storageManager.exceptionToThrowOnControllingCompaction = null;
    }

    /**
     * Tests for the stopBlobStore response received on a {@link BlobStoreControlAdminRequest} for different failure cases
     *
     * @throws InterruptedException
     * 		
     * @throws IOException
     * 		
     */
    @Test
    public void stopBlobStoreFailureTest() throws IOException, InterruptedException {
        List<? extends PartitionId> partitionIds = clusterMap.getAllPartitionIds(null);
        PartitionId id = partitionIds.get(0);
        short numReplicasCaughtUpPerPartition = 3;
        // test partition unknown
        sendAndVerifyStoreControlRequest(null, false, numReplicasCaughtUpPerPartition, Bad_Request);
        // test validate request failure - Replica_Unavailable
        storageManager.returnNullStore = true;
        sendAndVerifyStoreControlRequest(id, false, numReplicasCaughtUpPerPartition, Replica_Unavailable);
        storageManager.returnNullStore = false;
        // test validate request failure - Disk_Unavailable
        shutdown();
        storageManager.returnNullStore = true;
        sendAndVerifyStoreControlRequest(id, false, numReplicasCaughtUpPerPartition, Disk_Unavailable);
        storageManager.returnNullStore = false;
        start();
        // test invalid numReplicasCaughtUpPerPartition
        numReplicasCaughtUpPerPartition = -1;
        sendAndVerifyStoreControlRequest(id, false, numReplicasCaughtUpPerPartition, Bad_Request);
        numReplicasCaughtUpPerPartition = 3;
        // test disable compaction failure
        storageManager.returnValueOfControllingCompaction = false;
        sendAndVerifyStoreControlRequest(id, false, numReplicasCaughtUpPerPartition, Unknown_Error);
        storageManager.returnValueOfControllingCompaction = true;
        // test disable compaction with runtime exception
        storageManager.exceptionToThrowOnControllingCompaction = new IllegalStateException();
        sendAndVerifyStoreControlRequest(id, false, numReplicasCaughtUpPerPartition, Unknown_Error);
        storageManager.exceptionToThrowOnControllingCompaction = null;
        // test disable replication failure
        replicationManager.reset();
        replicationManager.controlReplicationReturnVal = false;
        sendAndVerifyStoreControlRequest(id, false, numReplicasCaughtUpPerPartition, Unknown_Error);
        // test peers catchup failure
        replicationManager.reset();
        replicationManager.controlReplicationReturnVal = true;
        // all replicas of this partition > acceptableLag
        generateLagOverrides(1, 1);
        sendAndVerifyStoreControlRequest(id, false, numReplicasCaughtUpPerPartition, Retry_After_Backoff);
        // test shutdown BlobStore failure
        replicationManager.reset();
        replicationManager.controlReplicationReturnVal = true;
        storageManager.returnValueOfShutdownBlobStore = false;
        generateLagOverrides(0, 0);
        sendAndVerifyStoreControlRequest(id, false, numReplicasCaughtUpPerPartition, Unknown_Error);
        // test shutdown BlobStore with runtime exception
        storageManager.exceptionToThrowOnShuttingDownBlobStore = new IllegalStateException();
        sendAndVerifyStoreControlRequest(id, false, numReplicasCaughtUpPerPartition, Unknown_Error);
        storageManager.exceptionToThrowOnShuttingDownBlobStore = null;
    }

    /**
     * Tests blobIds can be converted as expected and works correctly with GetRequest.
     * If all blobIds can be converted correctly, no error is expected.
     * If any blobId can't be converted correctly, Blob_Not_Found is expected.
     *
     * @throws InterruptedException
     * 		
     * @throws IOException
     * 		
     */
    @Test
    public void listOfOriginalStoreKeysGetTest() throws IOException, InterruptedException {
        int numIds = 10;
        PartitionId partitionId = clusterMap.getAllPartitionIds(null).get(0);
        List<BlobId> blobIds = new ArrayList<>();
        for (int i = 0; i < numIds; i++) {
            BlobId originalBlobId = new BlobId(CommonTestUtils.getCurrentBlobIdVersion(), BlobIdType.NATIVE, ClusterMapUtils.UNKNOWN_DATACENTER_ID, Utils.getRandomShort(RANDOM), Utils.getRandomShort(RANDOM), partitionId, false, BlobDataType.DATACHUNK);
            BlobId convertedBlobId = new BlobId(CommonTestUtils.getCurrentBlobIdVersion(), BlobIdType.CRAFTED, ClusterMapUtils.UNKNOWN_DATACENTER_ID, originalBlobId.getAccountId(), originalBlobId.getContainerId(), partitionId, false, BlobDataType.DATACHUNK);
            conversionMap.put(originalBlobId, convertedBlobId);
            validKeysInStore.add(convertedBlobId);
            blobIds.add(originalBlobId);
        }
        sendAndVerifyGetOriginalStoreKeys(blobIds, No_Error);
        // test with duplicates
        List<BlobId> blobIdsWithDups = new ArrayList(blobIds);
        // add the same blob ids
        blobIdsWithDups.addAll(blobIds);
        // add converted ids
        conversionMap.values().forEach(( id) -> blobIdsWithDups.add(((BlobId) (id))));
        sendAndVerifyGetOriginalStoreKeys(blobIdsWithDups, No_Error);
        // store must not have received duplicates
        Assert.assertEquals("Size is not as expected", blobIds.size(), AmbryRequestsTest.MockStorageManager.idsReceived.size());
        for (int i = 0; i < (blobIds.size()); i++) {
            BlobId key = blobIds.get(i);
            StoreKey converted = conversionMap.get(key);
            Assert.assertEquals((((key + "/") + converted) + " was not received at the store"), converted, AmbryRequestsTest.MockStorageManager.idsReceived.get(i));
        }
        // Check a valid key mapped to null
        BlobId originalBlobId = new BlobId(CommonTestUtils.getCurrentBlobIdVersion(), BlobIdType.NATIVE, ClusterMapUtils.UNKNOWN_DATACENTER_ID, Utils.getRandomShort(RANDOM), Utils.getRandomShort(RANDOM), partitionId, false, BlobDataType.DATACHUNK);
        blobIds.add(originalBlobId);
        conversionMap.put(originalBlobId, null);
        validKeysInStore.add(originalBlobId);
        sendAndVerifyGetOriginalStoreKeys(blobIds, No_Error);
        // Check a invalid key mapped to null
        originalBlobId = new BlobId(CommonTestUtils.getCurrentBlobIdVersion(), BlobIdType.NATIVE, ClusterMapUtils.UNKNOWN_DATACENTER_ID, Utils.getRandomShort(RANDOM), Utils.getRandomShort(RANDOM), partitionId, false, BlobDataType.DATACHUNK);
        blobIds.add(originalBlobId);
        conversionMap.put(originalBlobId, null);
        sendAndVerifyGetOriginalStoreKeys(blobIds, Blob_Not_Found);
        // Check exception
        storeKeyConverterFactory.setException(new Exception("StoreKeyConverter Mock Exception"));
        sendAndVerifyGetOriginalStoreKeys(blobIds, Unknown_Error);
    }

    /**
     * Tests for success and failure scenarios for TTL updates
     *
     * @throws InterruptedException
     * 		
     * @throws IOException
     * 		
     * @throws MessageFormatException
     * 		
     */
    @Test
    public void ttlUpdateTest() throws MessageFormatException, IOException, InterruptedException {
        MockPartitionId id = ((MockPartitionId) (clusterMap.getWritablePartitionIds(DEFAULT_PARTITION_CLASS).get(0)));
        int correlationId = RANDOM.nextInt();
        String clientId = UtilsTest.getRandomString(10);
        BlobId blobId = new BlobId(CommonTestUtils.getCurrentBlobIdVersion(), BlobIdType.NATIVE, ClusterMapUtils.UNKNOWN_DATACENTER_ID, Utils.getRandomShort(RANDOM), Utils.getRandomShort(RANDOM), id, false, BlobDataType.DATACHUNK);
        long expiresAtMs = Utils.getRandomLong(RANDOM, Long.MAX_VALUE);
        long opTimeMs = SystemTime.getInstance().milliseconds();
        // storekey not valid for store
        doTtlUpdate((correlationId++), clientId, blobId, expiresAtMs, opTimeMs, Blob_Not_Found);
        // valid now
        validKeysInStore.add(blobId);
        doTtlUpdate((correlationId++), clientId, blobId, expiresAtMs, opTimeMs, No_Error);
        // after conversion
        validKeysInStore.remove(blobId);
        BlobId converted = new BlobId(blobId.getVersion(), BlobIdType.CRAFTED, blobId.getDatacenterId(), Utils.getRandomShort(RANDOM), Utils.getRandomShort(RANDOM), blobId.getPartition(), BlobId.isEncrypted(blobId.getID()), BlobDataType.DATACHUNK);
        // not in conversion map
        doTtlUpdate((correlationId++), clientId, blobId, expiresAtMs, opTimeMs, Blob_Not_Found);
        // in conversion map but key not valid
        conversionMap.put(blobId, converted);
        doTtlUpdate((correlationId++), clientId, blobId, expiresAtMs, opTimeMs, Blob_Not_Found);
        // valid now
        validKeysInStore.add(converted);
        doTtlUpdate((correlationId++), clientId, blobId, expiresAtMs, opTimeMs, No_Error);
        // READ_ONLY is fine too
        changePartitionState(id, true);
        doTtlUpdate((correlationId++), clientId, blobId, expiresAtMs, opTimeMs, No_Error);
        changePartitionState(id, false);
        miscTtlUpdateFailuresTest();
    }

    /**
     * Implementation of {@link Request} to help with tests.
     */
    private static class MockRequest implements Request {
        private final InputStream stream;

        /**
         * Constructs a {@link MockRequest} from {@code request}.
         *
         * @param request
         * 		the {@link RequestOrResponse} to construct the {@link MockRequest} for.
         * @return an instance of {@link MockRequest} that represents {@code request}.
         * @throws IOException
         * 		
         */
        static AmbryRequestsTest.MockRequest fromRequest(RequestOrResponse request) throws IOException {
            ByteBuffer buffer = ByteBuffer.allocate(((int) (request.sizeInBytes())));
            request.writeTo(new ByteBufferChannel(buffer));
            buffer.flip();
            // read length (to bring it to a state where AmbryRequests can handle it).
            buffer.getLong();
            return new AmbryRequestsTest.MockRequest(new ByteBufferInputStream(buffer));
        }

        /**
         * Constructs a {@link MockRequest}.
         *
         * @param stream
         * 		the {@link InputStream} that will be returned on a call to {@link #getInputStream()}.
         */
        private MockRequest(InputStream stream) {
            this.stream = stream;
        }

        @Override
        public InputStream getInputStream() {
            return stream;
        }

        @Override
        public long getStartTimeInMs() {
            return 0;
        }
    }

    /**
     * An extension of {@link SocketRequestResponseChannel} to help with tests.
     */
    private static class MockRequestResponseChannel extends SocketRequestResponseChannel {
        /**
         * {@link Request} provided in the last call to {@link #sendResponse(Send, Request, ServerNetworkResponseMetrics).
         */
        Request lastOriginalRequest = null;

        /**
         * The {@link Send} provided in the last call to {@link #sendResponse(Send, Request, ServerNetworkResponseMetrics).
         */
        Send lastResponse = null;

        MockRequestResponseChannel() {
            super(1, 1);
        }

        @Override
        public void sendResponse(Send payloadToSend, Request originalRequest, ServerNetworkResponseMetrics metrics) {
            lastResponse = payloadToSend;
            lastOriginalRequest = originalRequest;
        }
    }

    /**
     * An extension of {@link StorageManager} to help with tests.
     */
    private static class MockStorageManager extends StorageManager {
        /**
         * The operation received at the store.
         */
        static RequestOrResponseType operationReceived = null;

        /**
         * The {@link MessageWriteSet} received at the store (only for put, delete and ttl update)
         */
        static MessageWriteSet messageWriteSetReceived = null;

        /**
         * The IDs received at the store (only for get)
         */
        static List<? extends StoreKey> idsReceived = null;

        /**
         * The {@link StoreGetOptions} received at the store (only for get)
         */
        static EnumSet<StoreGetOptions> storeGetOptionsReceived;

        /**
         * The {@link FindToken} received at the store (only for findEntriesSince())
         */
        static FindToken tokenReceived = null;

        /**
         * The maxTotalSizeOfEntries received at the store (only for findEntriesSince())
         */
        static Long maxTotalSizeOfEntriesReceived = null;

        /**
         * StoreException to throw when an API is invoked
         */
        static StoreException storeException = null;

        /**
         * RuntimeException to throw when an API is invoked. Will be preferred over {@link #storeException}.
         */
        static RuntimeException runtimeException = null;

        /**
         * An empty {@link Store} implementation.
         */
        private Store store = new Store() {
            @Override
            public void start() throws StoreException {
                throwExceptionIfRequired();
            }

            @Override
            public StoreInfo get(List<? extends StoreKey> ids, EnumSet<StoreGetOptions> storeGetOptions) throws StoreException {
                AmbryRequestsTest.MockStorageManager.operationReceived = RequestOrResponseType.GetRequest;
                AmbryRequestsTest.MockStorageManager.idsReceived = ids;
                AmbryRequestsTest.MockStorageManager.storeGetOptionsReceived = storeGetOptions;
                throwExceptionIfRequired();
                checkValidityOfIds(ids);
                return new StoreInfo(new MessageReadSet() {
                    @Override
                    public long writeTo(int index, WritableByteChannel channel, long relativeOffset, long maxSize) {
                        return 0;
                    }

                    @Override
                    public int count() {
                        return 0;
                    }

                    @Override
                    public long sizeInBytes(int index) {
                        return 0;
                    }

                    @Override
                    public StoreKey getKeyAt(int index) {
                        return null;
                    }

                    @Override
                    public void doPrefetch(int index, long relativeOffset, long size) {
                        return;
                    }
                }, Collections.EMPTY_LIST);
            }

            @Override
            public void put(MessageWriteSet messageSetToWrite) throws StoreException {
                AmbryRequestsTest.MockStorageManager.operationReceived = RequestOrResponseType.PutRequest;
                AmbryRequestsTest.MockStorageManager.messageWriteSetReceived = messageSetToWrite;
                throwExceptionIfRequired();
            }

            @Override
            public void delete(MessageWriteSet messageSetToDelete) throws StoreException {
                AmbryRequestsTest.MockStorageManager.operationReceived = RequestOrResponseType.DeleteRequest;
                AmbryRequestsTest.MockStorageManager.messageWriteSetReceived = messageSetToDelete;
                throwExceptionIfRequired();
                checkValidityOfIds(messageSetToDelete.getMessageSetInfo().stream().map(MessageInfo::getStoreKey).collect(Collectors.toList()));
            }

            @Override
            public void updateTtl(MessageWriteSet messageSetToUpdate) throws StoreException {
                AmbryRequestsTest.MockStorageManager.operationReceived = RequestOrResponseType.TtlUpdateRequest;
                AmbryRequestsTest.MockStorageManager.messageWriteSetReceived = messageSetToUpdate;
                throwExceptionIfRequired();
                checkValidityOfIds(messageSetToUpdate.getMessageSetInfo().stream().map(MessageInfo::getStoreKey).collect(Collectors.toList()));
            }

            @Override
            public FindInfo findEntriesSince(FindToken token, long maxTotalSizeOfEntries) throws StoreException {
                AmbryRequestsTest.MockStorageManager.operationReceived = RequestOrResponseType.ReplicaMetadataRequest;
                AmbryRequestsTest.MockStorageManager.tokenReceived = token;
                AmbryRequestsTest.MockStorageManager.maxTotalSizeOfEntriesReceived = maxTotalSizeOfEntries;
                throwExceptionIfRequired();
                return new FindInfo(Collections.EMPTY_LIST, AmbryRequestsTest.FIND_TOKEN_FACTORY.getNewFindToken());
            }

            @Override
            public Set<StoreKey> findMissingKeys(List<StoreKey> keys) {
                throw new UnsupportedOperationException();
            }

            @Override
            public StoreStats getStoreStats() {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean isKeyDeleted(StoreKey key) {
                throw new UnsupportedOperationException();
            }

            @Override
            public long getSizeInBytes() {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            public void shutdown() throws StoreException {
                throwExceptionIfRequired();
            }

            /**
             * Throws a {@link RuntimeException} or {@link StoreException} if so configured
             *
             * @throws StoreException
             * 		
             */
            private void throwExceptionIfRequired() throws StoreException {
                if ((AmbryRequestsTest.MockStorageManager.runtimeException) != null) {
                    throw AmbryRequestsTest.MockStorageManager.runtimeException;
                }
                if ((AmbryRequestsTest.MockStorageManager.storeException) != null) {
                    throw AmbryRequestsTest.MockStorageManager.storeException;
                }
            }

            /**
             * Checks the validity of the {@code ids}
             *
             * @param ids
             * 		the {@link StoreKey}s to check
             * @throws StoreException
             * 		if the key is not valid
             */
            private void checkValidityOfIds(Collection<? extends StoreKey> ids) throws StoreException {
                for (StoreKey id : ids) {
                    if (!(validKeysInStore.contains(id))) {
                        throw new StoreException("Not a valid key.", StoreErrorCodes.ID_Not_Found);
                    }
                }
            }
        };

        private static final VerifiableProperties VPROPS = new VerifiableProperties(new Properties());

        /**
         * if {@code true}, a {@code null} {@link Store} is returned on a call to {@link #getStore(PartitionId)}. Otherwise
         * {@link #store} is returned.
         */
        boolean returnNullStore = false;

        /**
         * If non-null, the given exception is thrown when {@link #scheduleNextForCompaction(PartitionId)} is called.
         */
        RuntimeException exceptionToThrowOnSchedulingCompaction = null;

        /**
         * If non-null, the given exception is thrown when {@link #controlCompactionForBlobStore(PartitionId, boolean)} is called.
         */
        RuntimeException exceptionToThrowOnControllingCompaction = null;

        /**
         * If non-null, the given exception is thrown when {@link #shutdownBlobStore(PartitionId)} is called.
         */
        RuntimeException exceptionToThrowOnShuttingDownBlobStore = null;

        /**
         * If non-null, the given exception is thrown when {@link #startBlobStore(PartitionId)} is called.
         */
        RuntimeException exceptionToThrowOnStartingBlobStore = null;

        /**
         * The return value for a call to {@link #scheduleNextForCompaction(PartitionId)}.
         */
        boolean returnValueOfSchedulingCompaction = true;

        /**
         * The return value for a call to {@link #controlCompactionForBlobStore(PartitionId, boolean)}.
         */
        boolean returnValueOfControllingCompaction = true;

        /**
         * The return value for a call to {@link #shutdownBlobStore(PartitionId)}.
         */
        boolean returnValueOfShutdownBlobStore = true;

        /**
         * The return value for a call to {@link #startBlobStore(PartitionId)}.
         */
        boolean returnValueOfStartingBlobStore = true;

        /**
         * The {@link PartitionId} that was provided in the call to {@link #scheduleNextForCompaction(PartitionId)}
         */
        PartitionId compactionScheduledPartitionId = null;

        /**
         * The {@link PartitionId} that was provided in the call to {@link #controlCompactionForBlobStore(PartitionId, boolean)}
         */
        PartitionId compactionControlledPartitionId = null;

        /**
         * The {@link boolean} that was provided in the call to {@link #controlCompactionForBlobStore(PartitionId, boolean)}
         */
        Boolean compactionEnableVal = null;

        /**
         * The {@link PartitionId} that was provided in the call to {@link #shutdownBlobStore(PartitionId)}
         */
        PartitionId shutdownPartitionId = null;

        /**
         * The {@link PartitionId} that was provided in the call to {@link #startBlobStore(PartitionId)}
         */
        PartitionId startedPartitionId = null;

        private final Set<StoreKey> validKeysInStore;

        MockStorageManager(Set<StoreKey> validKeysInStore, List<? extends ReplicaId> replicas) throws StoreException {
            super(new StoreConfig(AmbryRequestsTest.MockStorageManager.VPROPS), new com.github.ambry.config.DiskManagerConfig(AmbryRequestsTest.MockStorageManager.VPROPS), Utils.newScheduler(1, true), new MetricRegistry(), replicas, null, null, null, null, new MockTime());
            this.validKeysInStore = validKeysInStore;
        }

        @Override
        public Store getStore(PartitionId id) {
            return returnNullStore ? null : store;
        }

        @Override
        public boolean scheduleNextForCompaction(PartitionId id) {
            if ((exceptionToThrowOnSchedulingCompaction) != null) {
                throw exceptionToThrowOnSchedulingCompaction;
            }
            compactionScheduledPartitionId = id;
            return returnValueOfSchedulingCompaction;
        }

        @Override
        public boolean controlCompactionForBlobStore(PartitionId id, boolean enabled) {
            if ((exceptionToThrowOnControllingCompaction) != null) {
                throw exceptionToThrowOnControllingCompaction;
            }
            compactionControlledPartitionId = id;
            compactionEnableVal = enabled;
            return returnValueOfControllingCompaction;
        }

        @Override
        public boolean shutdownBlobStore(PartitionId id) {
            if ((exceptionToThrowOnShuttingDownBlobStore) != null) {
                throw exceptionToThrowOnShuttingDownBlobStore;
            }
            shutdownPartitionId = id;
            return returnValueOfShutdownBlobStore;
        }

        @Override
        public boolean startBlobStore(PartitionId id) {
            if ((exceptionToThrowOnStartingBlobStore) != null) {
                throw exceptionToThrowOnStartingBlobStore;
            }
            startedPartitionId = id;
            return returnValueOfStartingBlobStore;
        }

        /**
         * Resets variables associated with the {@link Store} impl
         */
        void resetStore() {
            AmbryRequestsTest.MockStorageManager.operationReceived = null;
            AmbryRequestsTest.MockStorageManager.messageWriteSetReceived = null;
            AmbryRequestsTest.MockStorageManager.idsReceived = null;
            AmbryRequestsTest.MockStorageManager.storeGetOptionsReceived = null;
            AmbryRequestsTest.MockStorageManager.tokenReceived = null;
            AmbryRequestsTest.MockStorageManager.maxTotalSizeOfEntriesReceived = null;
        }
    }

    /**
     * An extension of {@link ReplicationManager} to help with testing.
     */
    private static class MockReplicationManager extends ReplicationManager {
        // General variables
        RuntimeException exceptionToThrow = null;

        // Variables for controlling and examining the values provided to controlReplicationForPartitions()
        Boolean controlReplicationReturnVal;

        Collection<PartitionId> idsVal;

        List<String> originsVal;

        Boolean enableVal;

        // Variables for controlling getRemoteReplicaLagFromLocalInBytes()
        // the key is partitionId:hostname:replicaPath
        Map<String, Long> lagOverrides = null;

        /**
         * Static construction helper
         *
         * @param verifiableProperties
         * 		the {@link VerifiableProperties} to use for config.
         * @param storageManager
         * 		the {@link StorageManager} to use.
         * @param clusterMap
         * 		the {@link ClusterMap} to use.
         * @param dataNodeId
         * 		the {@link DataNodeId} to use.
         * @param storeKeyConverterFactory
         * 		the {@link StoreKeyConverterFactory} to use.
         * @return an instance of {@link MockReplicationManager}
         * @throws ReplicationException
         * 		
         */
        static AmbryRequestsTest.MockReplicationManager getReplicationManager(VerifiableProperties verifiableProperties, StorageManager storageManager, ClusterMap clusterMap, DataNodeId dataNodeId, StoreKeyConverterFactory storeKeyConverterFactory) throws ReplicationException {
            ReplicationConfig replicationConfig = new ReplicationConfig(verifiableProperties);
            ClusterMapConfig clusterMapConfig = new ClusterMapConfig(verifiableProperties);
            StoreConfig storeConfig = new StoreConfig(verifiableProperties);
            return new AmbryRequestsTest.MockReplicationManager(replicationConfig, clusterMapConfig, storeConfig, storageManager, clusterMap, dataNodeId, storeKeyConverterFactory);
        }

        /**
         * Constructor for MockReplicationManager.
         *
         * @param replicationConfig
         * 		the config for replication.
         * @param clusterMapConfig
         * 		the config for clustermap.
         * @param storeConfig
         * 		the config for the store.
         * @param storageManager
         * 		the {@link StorageManager} to use.
         * @param clusterMap
         * 		the {@link ClusterMap} to use.
         * @param dataNodeId
         * 		the {@link DataNodeId} to use.
         * @throws ReplicationException
         * 		
         */
        MockReplicationManager(ReplicationConfig replicationConfig, ClusterMapConfig clusterMapConfig, StoreConfig storeConfig, StorageManager storageManager, ClusterMap clusterMap, DataNodeId dataNodeId, StoreKeyConverterFactory storeKeyConverterFactory) throws ReplicationException {
            super(replicationConfig, clusterMapConfig, storeConfig, storageManager, new StoreKeyFactory() {
                @Override
                public StoreKey getStoreKey(DataInputStream stream) {
                    return null;
                }

                @Override
                public StoreKey getStoreKey(String input) {
                    return null;
                }
            }, clusterMap, null, dataNodeId, null, clusterMap.getMetricRegistry(), null, storeKeyConverterFactory, null);
            reset();
        }

        @Override
        public boolean controlReplicationForPartitions(Collection<PartitionId> ids, List<String> origins, boolean enable) {
            failIfRequired();
            if ((controlReplicationReturnVal) == null) {
                throw new IllegalStateException("Return val not set. Don't know what to return");
            }
            idsVal = ids;
            originsVal = origins;
            enableVal = enable;
            return controlReplicationReturnVal;
        }

        @Override
        public long getRemoteReplicaLagFromLocalInBytes(PartitionId partitionId, String hostName, String replicaPath) {
            failIfRequired();
            long lag;
            String key = AmbryRequestsTest.MockReplicationManager.getPartitionLagKey(partitionId, hostName, replicaPath);
            if (((lagOverrides) == null) || (!(lagOverrides.containsKey(key)))) {
                lag = super.getRemoteReplicaLagFromLocalInBytes(partitionId, hostName, replicaPath);
            } else {
                lag = lagOverrides.get(key);
            }
            return lag;
        }

        /**
         * Resets all state
         */
        void reset() {
            exceptionToThrow = null;
            controlReplicationReturnVal = null;
            idsVal = null;
            originsVal = null;
            enableVal = null;
            lagOverrides = null;
        }

        /**
         * Gets the key for the lag override in {@code lagOverrides} using the given parameters.
         *
         * @param partitionId
         * 		the {@link PartitionId} whose replica {@code hostname} is.
         * @param hostname
         * 		the hostname of the replica whose lag override key is required.
         * @param replicaPath
         * 		the replica path of the replica whose lag override key is required.
         * @return 
         */
        static String getPartitionLagKey(PartitionId partitionId, String hostname, String replicaPath) {
            return ((((partitionId.toString()) + ":") + hostname) + ":") + replicaPath;
        }

        /**
         * Throws a {@link RuntimeException} if the {@link MockReplicationManager} is required to.
         */
        private void failIfRequired() {
            if ((exceptionToThrow) != null) {
                throw exceptionToThrow;
            }
        }
    }
}

