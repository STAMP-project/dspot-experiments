/**
 * Copyright 2016 LinkedIn Corp. All rights reserved.
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
package com.github.ambry.store;


import StoreErrorCodes.Already_Exist;
import StoreErrorCodes.Already_Updated;
import StoreErrorCodes.Authorization_Failure;
import StoreErrorCodes.ID_Deleted;
import StoreErrorCodes.ID_Not_Found;
import StoreErrorCodes.Initialization_Error;
import StoreErrorCodes.Store_Already_Started;
import StoreErrorCodes.TTL_Expired;
import StoreErrorCodes.Update_Not_Allowed;
import StoreGetOptions.Store_Include_Deleted;
import StoreGetOptions.Store_Include_Expired;
import TestUtils.RANDOM;
import Utils.Infinite_Time;
import com.github.ambry.clustermap.ReplicaId;
import com.github.ambry.clustermap.ReplicaStatusDelegate;
import com.github.ambry.config.StoreConfig;
import com.github.ambry.config.VerifiableProperties;
import com.github.ambry.utils.MockTime;
import com.github.ambry.utils.Pair;
import com.github.ambry.utils.TestUtils;
import com.github.ambry.utils.Time;
import com.github.ambry.utils.Utils;
import com.github.ambry.utils.UtilsTest;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static BlobStoreCompactor.TEMP_LOG_SEGMENT_NAME_SUFFIX;
import static LogSegmentNameHelper.SUFFIX;


/**
 * Tests for {@link BlobStore}. Tests both segmented and non segmented log use cases.
 */
@RunWith(Parameterized.class)
public class BlobStoreTest {
    private static final StoreKeyFactory STORE_KEY_FACTORY;

    static {
        try {
            STORE_KEY_FACTORY = Utils.getObj("com.github.ambry.store.MockIdFactory");
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    // setupTestState() is coupled to these numbers. Changing them *will* cause setting test state or tests to fail.
    private static final long LOG_CAPACITY = 30000;

    private static final long SEGMENT_CAPACITY = 3000;

    private static final int MAX_IN_MEM_ELEMENTS = 5;

    // deliberately do not divide the capacities perfectly.
    private static final int PUT_RECORD_SIZE = 53;

    private static final int DELETE_RECORD_SIZE = 29;

    private static final int TTL_UPDATE_RECORD_SIZE = 37;

    private static final byte[] DELETE_BUF = TestUtils.getRandomBytes(BlobStoreTest.DELETE_RECORD_SIZE);

    private static final byte[] TTL_UPDATE_BUF = TestUtils.getRandomBytes(BlobStoreTest.TTL_UPDATE_RECORD_SIZE);

    private final Random random = new Random();

    /**
     * A mock implementation of {@link MessageStoreHardDelete} that can be set to return {@link MessageInfo} for a
     * particular {@link MockId}.
     */
    private static class MockMessageStoreHardDelete implements MessageStoreHardDelete {
        private MessageInfo messageInfo = null;

        @Override
        public Iterator<HardDeleteInfo> getHardDeleteMessages(MessageReadSet readSet, StoreKeyFactory factory, List<byte[]> recoveryInfoList) {
            throw new UnsupportedOperationException();
        }

        @Override
        public MessageInfo getMessageInfo(Read read, long offset, StoreKeyFactory factory) {
            return messageInfo;
        }

        void setMessageInfo(MessageInfo messageInfo) {
            this.messageInfo = messageInfo;
        }
    }

    /**
     * An abstraction to unify and contains the results from the {@link Putter}, {@link Getter} and {@link Deleter}
     */
    private static class CallableResult {
        // Will be non-null only for PUT.
        final MockId id;

        // Will be non-null only for GET.
        final StoreInfo storeInfo;

        CallableResult(MockId id, StoreInfo storeInfo) {
            this.id = id;
            this.storeInfo = storeInfo;
        }
    }

    // a static instance to return for Deleter::call() and TtlUpdater::call().
    private static final BlobStoreTest.CallableResult EMPTY_RESULT = new BlobStoreTest.CallableResult(null, null);

    /**
     * Puts a blob and returns the {@link MockId} associated with it.
     */
    private class Putter implements Callable<BlobStoreTest.CallableResult> {
        @Override
        public BlobStoreTest.CallableResult call() throws Exception {
            return new BlobStoreTest.CallableResult(put(1, BlobStoreTest.PUT_RECORD_SIZE, Infinite_Time).get(0), null);
        }
    }

    /**
     * Gets a blob by returning the {@link StoreInfo} associated with it.
     */
    private class Getter implements Callable<BlobStoreTest.CallableResult> {
        final MockId id;

        final EnumSet<StoreGetOptions> storeGetOptions;

        /**
         *
         *
         * @param id
         * 		the {@link MockId} of the blob to GET
         * @param storeGetOptions
         * 		options for the GET.
         */
        Getter(MockId id, EnumSet<StoreGetOptions> storeGetOptions) {
            this.id = id;
            this.storeGetOptions = storeGetOptions;
        }

        @Override
        public BlobStoreTest.CallableResult call() throws Exception {
            return new BlobStoreTest.CallableResult(null, store.get(Collections.singletonList(id), storeGetOptions));
        }
    }

    /**
     * Deletes a blob.
     */
    private class Deleter implements Callable<BlobStoreTest.CallableResult> {
        final MockId id;

        /**
         *
         *
         * @param id
         * 		the {@link MockId} to delete.
         */
        Deleter(MockId id) {
            this.id = id;
        }

        @Override
        public BlobStoreTest.CallableResult call() throws Exception {
            delete(id);
            return BlobStoreTest.EMPTY_RESULT;
        }
    }

    /**
     * Updates the ttl of a blob.
     */
    private class TtlUpdater implements Callable<BlobStoreTest.CallableResult> {
        final MockId id;

        /**
         *
         *
         * @param id
         * 		the {@link MockId} to delete.
         */
        TtlUpdater(MockId id) {
            this.id = id;
        }

        @Override
        public BlobStoreTest.CallableResult call() throws Exception {
            updateTtl(id);
            return BlobStoreTest.EMPTY_RESULT;
        }
    }

    // used by getUniqueId() to make sure keys are never regenerated in a single test run.
    private final Set<MockId> generatedKeys = Collections.newSetFromMap(new ConcurrentHashMap<MockId, Boolean>());

    // A map of all the keys. The key is the MockId and the value is a Pair that contains the metadata and data of the
    // message.
    private final Map<MockId, Pair<MessageInfo, ByteBuffer>> allKeys = new ConcurrentHashMap<>();

    // A list of keys grouped by the log segment that they belong to
    private final List<Set<MockId>> idsByLogSegment = new ArrayList<>();

    // Set of all deleted keys
    private final Set<MockId> deletedKeys = Collections.newSetFromMap(new ConcurrentHashMap<MockId, Boolean>());

    // Set of all expired keys
    private final Set<MockId> expiredKeys = Collections.newSetFromMap(new ConcurrentHashMap<MockId, Boolean>());

    // Set of all keys that are not deleted/expired
    private final Set<MockId> liveKeys = Collections.newSetFromMap(new ConcurrentHashMap<MockId, Boolean>());

    // Set of all keys that have had their TTLs updated
    private final Set<MockId> ttlUpdatedKeys = Collections.newSetFromMap(new ConcurrentHashMap<MockId, Boolean>());

    // Indicates whether the log is segmented
    private final boolean isLogSegmented;

    // Variables that represent the folder where the data resides
    private final File tempDir;

    private final String tempDirStr;

    // the time instance that will be used in the index
    private final Time time = new MockTime();

    private final String storeId = UtilsTest.getRandomString(10);

    private final DiskIOScheduler diskIOScheduler = new DiskIOScheduler(null);

    private final DiskSpaceAllocator diskSpaceAllocator = StoreTestUtils.DEFAULT_DISK_SPACE_ALLOCATOR;

    private final Properties properties = new Properties();

    private final long expiresAtMs;

    // TODO: make these final once again once compactor is ready and the hack to clear state is removed
    private ScheduledExecutorService scheduler = Utils.newScheduler(1, false);

    private ScheduledExecutorService storeStatsScheduler = Utils.newScheduler(1, false);

    // The BlobStore instance
    private BlobStore store;

    // The MessageStoreRecovery that is used with the BlobStore
    private MessageStoreRecovery recovery = new DummyMessageStoreRecovery();

    // The MessageStoreHardDelete that is used with the BlobStore
    private MessageStoreHardDelete hardDelete = new BlobStoreTest.MockMessageStoreHardDelete();

    /**
     * Creates a temporary directory and sets up some test state.
     *
     * @throws InterruptedException
     * 		
     * @throws IOException
     * 		
     * @throws StoreException
     * 		
     */
    public BlobStoreTest(boolean isLogSegmented) throws StoreException, IOException, InterruptedException {
        this.isLogSegmented = isLogSegmented;
        tempDir = StoreTestUtils.createTempDirectory(("storeDir-" + (UtilsTest.getRandomString(10))));
        tempDirStr = tempDir.getAbsolutePath();
        StoreConfig config = new StoreConfig(new VerifiableProperties(properties));
        long bufferTimeMs = TimeUnit.SECONDS.toMillis(config.storeTtlUpdateBufferTimeSeconds);
        expiresAtMs = ((time.milliseconds()) + bufferTimeMs) + (TimeUnit.HOURS.toMillis(1));
        setupTestState(true);
    }

    /**
     * Tests blob store use of {@link ReplicaStatusDelegate}
     *
     * @throws StoreException
     * 		
     */
    @Test
    public void testClusterManagerReplicaStatusDelegateUse() throws StoreException, IOException, InterruptedException {
        // Setup threshold test properties, replicaId, mock replica status delegate
        // TODO: while the compactor is upgraded to understand TTL updates, this hack ensures that this test (which uses
        // TODO: compaction for segmented logs) never encounters TTL updates
        if (isLogSegmented) {
            cleanup();
            scheduler = Utils.newScheduler(1, false);
            storeStatsScheduler = Utils.newScheduler(1, false);
            setupTestState(false);
        }
        // Setup threshold test properties, replicaId, mock write status delegate
        StoreConfig defaultConfig = changeThreshold(65, 5, true);
        StoreTestUtils.MockReplicaId replicaId = getMockReplicaId(tempDirStr);
        ReplicaStatusDelegate replicaStatusDelegate = Mockito.mock(ReplicaStatusDelegate.class);
        Mockito.when(replicaStatusDelegate.unseal(ArgumentMatchers.any())).thenReturn(true);
        Mockito.when(replicaStatusDelegate.seal(ArgumentMatchers.any())).thenReturn(true);
        // Restart store
        reloadStore(defaultConfig, replicaId, replicaStatusDelegate);
        // Check that after start, because ReplicaId defaults to non-sealed, delegate is not called
        Mockito.verifyZeroInteractions(replicaStatusDelegate);
        // Verify that putting in data that doesn't go over the threshold doesn't trigger the delegate
        put(1, 50, Infinite_Time);
        Mockito.verifyNoMoreInteractions(replicaStatusDelegate);
        // Verify that after putting in enough data, the store goes to read only
        List<MockId> addedIds = put(4, 2000, Infinite_Time);
        Mockito.verify(replicaStatusDelegate, Mockito.times(1)).seal(replicaId);
        // Assumes ClusterParticipant sets replicaId status to true
        replicaId.setSealedState(true);
        // Change config threshold but with delegate disabled, verify that nothing happens
        reloadStore(changeThreshold(99, 1, false), replicaId, replicaStatusDelegate);
        Mockito.verifyNoMoreInteractions(replicaStatusDelegate);
        // Change config threshold to higher, see that it gets changed to unsealed on reset
        reloadStore(changeThreshold(99, 1, true), replicaId, replicaStatusDelegate);
        Mockito.verify(replicaStatusDelegate, Mockito.times(1)).unseal(replicaId);
        replicaId.setSealedState(false);
        // Reset thresholds, verify that it changed back
        reloadStore(defaultConfig, replicaId, replicaStatusDelegate);
        Mockito.verify(replicaStatusDelegate, Mockito.times(2)).seal(replicaId);
        replicaId.setSealedState(true);
        // Remaining tests only relevant for segmented logs
        if (isLogSegmented) {
            // Delete added data
            for (MockId addedId : addedIds) {
                delete(addedId);
            }
            // Need to restart blob otherwise compaction will ignore segments in journal (which are all segments right now).
            // By restarting, only last segment will be in journal
            reloadStore(defaultConfig, replicaId, replicaStatusDelegate);
            Mockito.verifyNoMoreInteractions(replicaStatusDelegate);
            // Advance time by 8 days, call compaction to compact segments with deleted data, then verify
            // that the store is now read-write
            time.sleep(TimeUnit.DAYS.toMillis(8));
            store.compact(store.getCompactionDetails(new CompactAllPolicy(defaultConfig, time)), new byte[((BlobStoreTest.PUT_RECORD_SIZE) * 2) + 1]);
            Mockito.verify(replicaStatusDelegate, Mockito.times(2)).unseal(replicaId);
            // Test if replicaId is erroneously true that it updates the status upon startup
            replicaId.setSealedState(true);
            reloadStore(defaultConfig, replicaId, replicaStatusDelegate);
            Mockito.verify(replicaStatusDelegate, Mockito.times(3)).unseal(replicaId);
        }
        store.shutdown();
    }

    /**
     * Tests {@link BlobStore#start()} for corner cases and error cases.
     * Corner cases
     * 1. Creating a directory on first startup
     * Error cases
     * 1. Start an already started store.
     * 2. Unable to create store directory on first startup.
     * 3. Starting two stores at the same path.
     * 4. Directory not readable.
     * 5. Path is not a directory.
     *
     * @throws IOException
     * 		
     * @throws StoreException
     * 		
     */
    @Test
    public void storeStartupTests() throws StoreException, IOException {
        // attempt to start when store is already started fails
        verifyStartupFailure(store, Store_Already_Started);
        String nonExistentDir = new File(tempDir, UtilsTest.getRandomString(10)).getAbsolutePath();
        // fail if attempt to create directory fails
        String badPath = new File(nonExistentDir, UtilsTest.getRandomString(10)).getAbsolutePath();
        BlobStore blobStore = createBlobStore(getMockReplicaId(badPath));
        verifyStartupFailure(blobStore, Initialization_Error);
        ReplicaId replicaIdWithNonExistentDir = getMockReplicaId(nonExistentDir);
        // create directory if it does not exist
        blobStore = createBlobStore(replicaIdWithNonExistentDir);
        verifyStartupSuccess(blobStore);
        File createdDir = new File(nonExistentDir);
        Assert.assertTrue("Directory should now exist", ((createdDir.exists()) && (createdDir.isDirectory())));
        // should not be able to start two stores at the same path
        blobStore = createBlobStore(replicaIdWithNonExistentDir);
        blobStore.start();
        BlobStore secondStore = createBlobStore(replicaIdWithNonExistentDir);
        verifyStartupFailure(secondStore, Initialization_Error);
        blobStore.shutdown();
        // fail if directory is not readable
        Assert.assertTrue("Could not set readable state to false", createdDir.setReadable(false));
        verifyStartupFailure(blobStore, Initialization_Error);
        Assert.assertTrue("Could not set readable state to true", createdDir.setReadable(true));
        Assert.assertTrue("Directory could not be deleted", StoreTestUtils.cleanDirectory(createdDir, true));
        // fail if provided path is not a directory
        File file = new File(tempDir, UtilsTest.getRandomString(10));
        Assert.assertTrue("Test file could not be created", file.createNewFile());
        file.deleteOnExit();
        blobStore = createBlobStore(getMockReplicaId(file.getAbsolutePath()));
        verifyStartupFailure(blobStore, Initialization_Error);
    }

    /**
     * Does a basic test by getting all the blobs that were put (updated and deleted) during the test setup. This
     * implicitly tests all of PUT, GET, TTL UPDATE and DELETE.
     * </p>
     * Also tests GET with different combinations of {@link StoreGetOptions}.
     *
     * @throws InterruptedException
     * 		
     * @throws IOException
     * 		
     * @throws StoreException
     * 		
     */
    @Test
    public void basicTest() throws StoreException, IOException, InterruptedException {
        // PUT a key that is slated to expire when time advances by 1s
        MockId addedId = put(1, BlobStoreTest.PUT_RECORD_SIZE, ((time.seconds()) + 1)).get(0);
        time.sleep((2 * (Time.MsPerSec)));
        liveKeys.remove(addedId);
        expiredKeys.add(addedId);
        // GET of all the keys implicitly tests the PUT, UPDATE and DELETE.
        // live keys
        StoreInfo storeInfo = store.get(new ArrayList(liveKeys), EnumSet.noneOf(StoreGetOptions.class));
        checkStoreInfo(storeInfo, liveKeys);
        BlobStoreTest.MockMessageStoreHardDelete hd = ((BlobStoreTest.MockMessageStoreHardDelete) (hardDelete));
        for (MockId id : deletedKeys) {
            hd.setMessageInfo(allKeys.get(id).getFirst());
            // cannot get without StoreGetOptions
            verifyGetFailure(id, ID_Deleted);
            // with StoreGetOptions.Store_Include_Deleted
            storeInfo = store.get(Collections.singletonList(id), EnumSet.of(Store_Include_Deleted));
            checkStoreInfo(storeInfo, Collections.singleton(id));
            // with all StoreGetOptions
            storeInfo = store.get(Collections.singletonList(id), EnumSet.allOf(StoreGetOptions.class));
            checkStoreInfo(storeInfo, Collections.singleton(id));
        }
        for (MockId id : expiredKeys) {
            // cannot get without StoreGetOptions
            verifyGetFailure(id, TTL_Expired);
            // with StoreGetOptions.Store_Include_Expired
            storeInfo = store.get(Collections.singletonList(id), EnumSet.of(Store_Include_Expired));
            checkStoreInfo(storeInfo, Collections.singleton(id));
            // with all StoreGetOptions
            storeInfo = store.get(Collections.singletonList(id), EnumSet.allOf(StoreGetOptions.class));
            checkStoreInfo(storeInfo, Collections.singleton(id));
        }
        // should be able to delete expired blobs
        delete(addedId);
        // non existent ID has to fail
        verifyGetFailure(getUniqueId(), ID_Not_Found);
    }

    /**
     * Tests the case where there are many concurrent PUTs.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void concurrentPutTest() throws Exception {
        int blobCount = (4000 / (BlobStoreTest.PUT_RECORD_SIZE)) + 1;
        List<BlobStoreTest.Putter> putters = new ArrayList<>(blobCount);
        for (int i = 0; i < blobCount; i++) {
            putters.add(new BlobStoreTest.Putter());
        }
        ExecutorService executorService = Executors.newFixedThreadPool(putters.size());
        List<Future<BlobStoreTest.CallableResult>> futures = executorService.invokeAll(putters);
        verifyPutFutures(putters, futures);
    }

    /**
     * Tests the case where there are many concurrent GETs.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void concurrentGetTest() throws Exception {
        int extraBlobCount = (4000 / (BlobStoreTest.PUT_RECORD_SIZE)) + 1;
        put(extraBlobCount, BlobStoreTest.PUT_RECORD_SIZE, Infinite_Time);
        List<BlobStoreTest.Getter> getters = new ArrayList(allKeys.size());
        for (MockId id : allKeys.keySet()) {
            getters.add(new BlobStoreTest.Getter(id, EnumSet.noneOf(StoreGetOptions.class)));
        }
        ExecutorService executorService = Executors.newFixedThreadPool(getters.size());
        List<Future<BlobStoreTest.CallableResult>> futures = executorService.invokeAll(getters);
        verifyGetFutures(getters, futures);
    }

    /**
     * Tests the case where there are many concurrent DELETEs.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void concurrentDeleteTest() throws Exception {
        int extraBlobCount = (2000 / (BlobStoreTest.PUT_RECORD_SIZE)) + 1;
        put(extraBlobCount, BlobStoreTest.PUT_RECORD_SIZE, Infinite_Time);
        List<BlobStoreTest.Deleter> deleters = new ArrayList(liveKeys.size());
        for (MockId id : liveKeys) {
            deleters.add(new BlobStoreTest.Deleter(id));
        }
        ExecutorService executorService = Executors.newFixedThreadPool(deleters.size());
        List<Future<BlobStoreTest.CallableResult>> futures = executorService.invokeAll(deleters);
        verifyDeleteFutures(deleters, futures);
    }

    /**
     * Tests the case where there are many concurrent ttl updates.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void concurrentTtlUpdateTest() throws Exception {
        int extraBlobCount = (2000 / (BlobStoreTest.PUT_RECORD_SIZE)) + 1;
        List<MockId> ids = put(extraBlobCount, BlobStoreTest.PUT_RECORD_SIZE, expiresAtMs);
        List<BlobStoreTest.TtlUpdater> ttlUpdaters = new ArrayList<>();
        for (MockId id : ids) {
            ttlUpdaters.add(new BlobStoreTest.TtlUpdater(id));
        }
        ExecutorService executorService = Executors.newFixedThreadPool(ttlUpdaters.size());
        List<Future<BlobStoreTest.CallableResult>> futures = executorService.invokeAll(ttlUpdaters);
        verifyTtlUpdateFutures(ttlUpdaters, futures);
    }

    /**
     * Tests the case where there are concurrent PUTs, GETs and DELETEs.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void concurrentAllTest() throws Exception {
        int putBlobCount = (1500 / (BlobStoreTest.PUT_RECORD_SIZE)) + 1;
        List<BlobStoreTest.Putter> putters = new ArrayList<>(putBlobCount);
        for (int i = 0; i < putBlobCount; i++) {
            putters.add(new BlobStoreTest.Putter());
        }
        List<BlobStoreTest.Getter> getters = new ArrayList(liveKeys.size());
        for (MockId id : liveKeys) {
            getters.add(new BlobStoreTest.Getter(id, EnumSet.allOf(StoreGetOptions.class)));
        }
        int deleteBlobCount = 1000 / (BlobStoreTest.PUT_RECORD_SIZE);
        List<MockId> idsToDelete = put(deleteBlobCount, BlobStoreTest.PUT_RECORD_SIZE, Infinite_Time);
        List<BlobStoreTest.Deleter> deleters = new ArrayList<>(deleteBlobCount);
        for (MockId id : idsToDelete) {
            deleters.add(new BlobStoreTest.Deleter(id));
        }
        int updateTtlBlobCount = 1000 / (BlobStoreTest.PUT_RECORD_SIZE);
        List<MockId> idsToUpdateTtl = put(updateTtlBlobCount, BlobStoreTest.PUT_RECORD_SIZE, expiresAtMs);
        List<BlobStoreTest.TtlUpdater> ttlUpdaters = new ArrayList<>(updateTtlBlobCount);
        for (MockId id : idsToUpdateTtl) {
            ttlUpdaters.add(new BlobStoreTest.TtlUpdater(id));
        }
        List<Callable<BlobStoreTest.CallableResult>> callables = new ArrayList<Callable<BlobStoreTest.CallableResult>>(putters);
        callables.addAll(getters);
        callables.addAll(deleters);
        callables.addAll(ttlUpdaters);
        ExecutorService executorService = Executors.newFixedThreadPool(callables.size());
        List<Future<BlobStoreTest.CallableResult>> futures = executorService.invokeAll(callables);
        verifyPutFutures(putters, futures.subList(0, putters.size()));
        verifyGetFutures(getters, futures.subList(putters.size(), ((putters.size()) + (getters.size()))));
        verifyDeleteFutures(deleters, futures.subList(((putters.size()) + (getters.size())), (((putters.size()) + (getters.size())) + (deleters.size()))));
        verifyTtlUpdateFutures(ttlUpdaters, futures.subList((((putters.size()) + (getters.size())) + (deleters.size())), callables.size()));
    }

    /**
     * Tests some error cases for {@link BlobStore#get(List, EnumSet)};
     *
     * @throws StoreException
     * 		
     */
    @Test
    public void getErrorCasesTest() throws StoreException {
        // duplicate IDs
        List<StoreKey> listWithDups = new ArrayList<>();
        listWithDups.add(liveKeys.iterator().next());
        listWithDups.add(listWithDups.get(0));
        try {
            store.get(listWithDups, EnumSet.noneOf(StoreGetOptions.class));
            Assert.fail((("GET of " + listWithDups) + " should  have failed"));
        } catch (IllegalArgumentException e) {
            // expected. Nothing to do.
        }
    }

    /**
     * Tests error cases for {@link BlobStore#put(MessageWriteSet)}.
     *
     * @throws StoreException
     * 		
     */
    @Test
    public void putErrorCasesTest() throws StoreException {
        // ID that exists
        // live
        verifyPutFailure(liveKeys.iterator().next(), Already_Exist);
        // expired
        verifyPutFailure(expiredKeys.iterator().next(), Already_Exist);
        // deleted
        verifyPutFailure(deletedKeys.iterator().next(), Already_Exist);
        // duplicates
        MockId id = getUniqueId();
        MessageInfo info = new MessageInfo(id, BlobStoreTest.PUT_RECORD_SIZE, id.getAccountId(), id.getContainerId(), Utils.Infinite_Time);
        MessageWriteSet writeSet = new MockMessageWriteSet(Arrays.asList(info, info), Arrays.asList(ByteBuffer.allocate(1), ByteBuffer.allocate(1)));
        try {
            store.put(writeSet);
            Assert.fail("Store PUT should have failed");
        } catch (IllegalArgumentException e) {
            // expected. Nothing to do.
        }
    }

    /**
     * Tests error cases for {@link BlobStore#delete(MessageWriteSet)}.
     *
     * @throws StoreException
     * 		
     */
    @Test
    public void deleteErrorCasesTest() throws StoreException {
        // ID that is already deleted
        verifyDeleteFailure(deletedKeys.iterator().next(), ID_Deleted);
        // ID that does not exist
        verifyDeleteFailure(getUniqueId(), ID_Not_Found);
        MockId id = getUniqueId();
        MessageInfo info = new MessageInfo(id, BlobStoreTest.DELETE_RECORD_SIZE, id.getAccountId(), id.getContainerId(), time.milliseconds());
        MessageWriteSet writeSet = new MockMessageWriteSet(Arrays.asList(info, info), Arrays.asList(ByteBuffer.allocate(1), ByteBuffer.allocate(1)));
        try {
            store.delete(writeSet);
            Assert.fail("Store DELETE should have failed");
        } catch (IllegalArgumentException e) {
            // expected. Nothing to do.
        }
    }

    /**
     * Test DELETE with valid accountId and containerId.
     */
    @Test
    public void deleteAuthorizationSuccessTest() throws Exception {
        short[] accountIds = new short[]{ -1, Utils.getRandomShort(RANDOM), -1 };
        short[] containerIds = new short[]{ -1, -1, Utils.getRandomShort(RANDOM) };
        for (int i = 0; i < (accountIds.length); i++) {
            MockId mockId = put(1, BlobStoreTest.PUT_RECORD_SIZE, Infinite_Time, accountIds[i], containerIds[i]).get(0);
            delete(new MockId(mockId.getID(), mockId.getAccountId(), mockId.getContainerId()));
            verifyDeleteFailure(mockId, ID_Deleted);
        }
    }

    /**
     * Test DELETE with invalid accountId/containerId. Failure is expected.
     */
    @Test
    public void deleteAuthorizationFailureTest() throws Exception {
        MockId mockId = put(1, BlobStoreTest.PUT_RECORD_SIZE, Infinite_Time).get(0);
        short[] accountIds = new short[]{ -1, Utils.getRandomShort(RANDOM), -1, mockId.getAccountId(), Utils.getRandomShort(RANDOM) };
        short[] containerIds = new short[]{ -1, -1, Utils.getRandomShort(RANDOM), Utils.getRandomShort(RANDOM), mockId.getContainerId() };
        for (int i = 0; i < (accountIds.length); i++) {
            verifyDeleteFailure(new MockId(mockId.getID(), accountIds[i], containerIds[i]), Authorization_Failure);
        }
    }

    /**
     * Test GET with valid accountId and containerId.
     */
    @Test
    public void getAuthorizationSuccessTest() throws Exception {
        short[] accountIds = new short[]{ -1, Utils.getRandomShort(RANDOM), -1 };
        short[] containerIds = new short[]{ -1, -1, Utils.getRandomShort(RANDOM) };
        for (int i = 0; i < (accountIds.length); i++) {
            MockId mockId = put(1, BlobStoreTest.PUT_RECORD_SIZE, Infinite_Time, accountIds[i], containerIds[i]).get(0);
            StoreInfo storeinfo = store.get(Collections.singletonList(new MockId(mockId.getID(), accountIds[i], containerIds[i])), EnumSet.noneOf(StoreGetOptions.class));
            checkStoreInfo(storeinfo, Collections.singleton(mockId));
        }
    }

    /**
     * Test GET with invalid accountId/containerId. Failure is expected.
     */
    @Test
    public void getAuthorizationFailureTest() throws Exception {
        MockId mockId = put(1, BlobStoreTest.PUT_RECORD_SIZE, Infinite_Time).get(0);
        short[] accountIds = new short[]{ -1, Utils.getRandomShort(RANDOM), -1, mockId.getAccountId(), Utils.getRandomShort(RANDOM) };
        short[] containerIds = new short[]{ -1, -1, Utils.getRandomShort(RANDOM), Utils.getRandomShort(RANDOM), mockId.getContainerId() };
        for (int i = 0; i < (accountIds.length); i++) {
            verifyGetFailure(new MockId(mockId.getID(), accountIds[i], containerIds[i]), Authorization_Failure);
        }
    }

    /**
     * Tests different possible error cases for TTL update
     *
     * @throws Exception
     * 		
     */
    @Test
    public void ttlUpdateErrorCasesTest() throws Exception {
        // ID that does not exist
        verifyTtlUpdateFailure(getUniqueId(), Infinite_Time, ID_Not_Found);
        // ID that has expired
        for (MockId expired : expiredKeys) {
            verifyTtlUpdateFailure(expired, Infinite_Time, Update_Not_Allowed);
        }
        // ID that has not expired but is within the "no updates" period
        inNoTtlUpdatePeriodTest();
        // ID that is already updated
        for (MockId ttlUpdated : ttlUpdatedKeys) {
            if (!(deletedKeys.contains(ttlUpdated))) {
                verifyTtlUpdateFailure(ttlUpdated, Infinite_Time, Already_Updated);
            }
        }
        // ID that is already deleted
        for (MockId deleted : deletedKeys) {
            verifyTtlUpdateFailure(deleted, Infinite_Time, ID_Deleted);
        }
        // Attempt to set expiry time to anything other than infinity
        MockId id = getIdToTtlUpdate(liveKeys);
        verifyTtlUpdateFailure(id, ((time.milliseconds()) + 5), Update_Not_Allowed);
        // authorization failure
        ttlUpdateAuthorizationFailureTest();
        // duplicates
        id = getUniqueId();
        MessageInfo info = new MessageInfo(id, BlobStoreTest.TTL_UPDATE_RECORD_SIZE, false, true, Utils.Infinite_Time, id.getAccountId(), id.getContainerId(), time.milliseconds());
        MessageWriteSet writeSet = new MockMessageWriteSet(Arrays.asList(info, info), Arrays.asList(ByteBuffer.allocate(1), ByteBuffer.allocate(1)));
        try {
            store.updateTtl(writeSet);
            Assert.fail("Store TTL UPDATE should have failed");
        } catch (IllegalArgumentException e) {
            // expected. Nothing to do
        }
    }

    /**
     * Test TTL UPDATE with valid accountId and containerId.
     */
    @Test
    public void ttlUpdateAuthorizationSuccessTest() throws Exception {
        short[] accountIds = new short[]{ -1, Utils.getRandomShort(RANDOM), -1 };
        short[] containerIds = new short[]{ -1, -1, Utils.getRandomShort(RANDOM) };
        for (int i = 0; i < (accountIds.length); i++) {
            MockId id = put(1, BlobStoreTest.PUT_RECORD_SIZE, expiresAtMs, accountIds[i], containerIds[i]).get(0);
            updateTtl(new MockId(id.getID(), id.getAccountId(), id.getContainerId()));
            verifyTtlUpdate(id);
        }
    }

    /**
     * Test various duplicate and collision cases for {@link BlobStore#put(MessageWriteSet)}
     *
     * @throws Exception
     * 		
     */
    @Test
    public void idCollisionTest() throws Exception {
        // Populate global lists of keys and crcs.
        List<StoreKey> allMockIdList = new ArrayList<>();
        List<Long> allCrcList = new ArrayList<>();
        for (long i = 0; i < 4; i++) {
            allMockIdList.add(new MockId(Long.toString(i)));
            allCrcList.add(i);
        }
        // Put the initial two messages.
        List<StoreKey> mockIdList = Arrays.asList(allMockIdList.get(0), allMockIdList.get(1));
        List<Long> crcList = Arrays.asList(allCrcList.get(0), allCrcList.get(1));
        Set<StoreKey> missingKeysAfter = new HashSet(Arrays.asList(allMockIdList.get(2), allMockIdList.get(3)));
        putWithKeysAndCrcs(mockIdList, crcList);
        Assert.assertEquals(missingKeysAfter, store.findMissingKeys(allMockIdList));
        // 1. SOME_NOT_ALL_DUPLICATE - should fail.
        // first one duplicate, second one absent.
        mockIdList = Arrays.asList(allMockIdList.get(0), allMockIdList.get(2));
        crcList = Arrays.asList(allCrcList.get(0), allCrcList.get(2));
        try {
            putWithKeysAndCrcs(mockIdList, crcList);
            Assert.fail("Put should fail if some keys exist, but some do not");
        } catch (StoreException e) {
            Assert.assertEquals(Already_Exist, e.getErrorCode());
        }
        Assert.assertEquals(missingKeysAfter, store.findMissingKeys(allMockIdList));
        // first one absent, second one duplicate.
        mockIdList = Arrays.asList(allMockIdList.get(2), allMockIdList.get(0));
        crcList = Arrays.asList(allCrcList.get(2), allCrcList.get(0));
        try {
            putWithKeysAndCrcs(mockIdList, crcList);
            Assert.fail("Put should fail if some keys exist, but some do not");
        } catch (StoreException e) {
            Assert.assertEquals(Already_Exist, e.getErrorCode());
        }
        Assert.assertEquals(missingKeysAfter, store.findMissingKeys(allMockIdList));
        // 2. COLLIDING - should fail.
        // first one duplicate, second one colliding.
        mockIdList = Arrays.asList(allMockIdList.get(0), allMockIdList.get(1));
        crcList = Arrays.asList(allCrcList.get(0), allCrcList.get(2));
        try {
            putWithKeysAndCrcs(mockIdList, crcList);
            Assert.fail("Put should fail if some keys exist, but some do not");
        } catch (StoreException e) {
            Assert.assertEquals(Already_Exist, e.getErrorCode());
        }
        Assert.assertEquals(missingKeysAfter, store.findMissingKeys(allMockIdList));
        // first one absent, second one colliding.
        mockIdList = Arrays.asList(allMockIdList.get(3), allMockIdList.get(1));
        crcList = Arrays.asList(allCrcList.get(3), allCrcList.get(2));
        try {
            putWithKeysAndCrcs(mockIdList, crcList);
            Assert.fail("Put should fail if some keys exist, but some do not");
        } catch (StoreException e) {
            Assert.assertEquals(Already_Exist, e.getErrorCode());
        }
        Assert.assertEquals(missingKeysAfter, store.findMissingKeys(allMockIdList));
        // 3. ALL_DUPLICATE - should succeed.
        mockIdList = Arrays.asList(allMockIdList.get(0), allMockIdList.get(1));
        crcList = Arrays.asList(allCrcList.get(0), allCrcList.get(1));
        putWithKeysAndCrcs(mockIdList, crcList);
        Assert.assertEquals(missingKeysAfter, store.findMissingKeys(allMockIdList));
        // 4. ALL_ABSENT
        mockIdList = Arrays.asList(allMockIdList.get(2), allMockIdList.get(3));
        crcList = Arrays.asList(allCrcList.get(2), allCrcList.get(3));
        putWithKeysAndCrcs(mockIdList, crcList);
        // Ensure that all new entries were added.
        missingKeysAfter.clear();
        Assert.assertEquals(missingKeysAfter, store.findMissingKeys(allMockIdList));
    }

    /**
     * Tests {@link BlobStore#findEntriesSince(FindToken, long)}.
     * <p/>
     * This test is minimal for two reasons
     * 1. The BlobStore simply calls into the index for this function and the index has extensive tests for this.
     * 2. It is almost impossible to validate the StoreFindToken here.
     * If the first condition changes, then more tests will be required here
     *
     * @throws StoreException
     * 		
     */
    @Test
    public void findEntriesSinceTest() throws StoreException {
        FindInfo findInfo = store.findEntriesSince(new StoreFindToken(), Long.MAX_VALUE);
        Set<StoreKey> keysPresent = new HashSet<>();
        for (MessageInfo info : findInfo.getMessageEntries()) {
            keysPresent.add(info.getStoreKey());
        }
        Assert.assertEquals("All keys were not present in the return from findEntriesSince()", allKeys.keySet(), keysPresent);
    }

    /**
     * Tests {@link BlobStore#findMissingKeys(List)}.
     *
     * @throws StoreException
     * 		
     */
    @Test
    public void findMissingKeysTest() throws StoreException {
        List<StoreKey> idsToProvide = new ArrayList<StoreKey>(allKeys.keySet());
        Set<StoreKey> nonExistentIds = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            nonExistentIds.add(getUniqueId());
        }
        idsToProvide.addAll(nonExistentIds);
        Collections.shuffle(idsToProvide);
        Set<StoreKey> missingKeys = store.findMissingKeys(idsToProvide);
        Assert.assertEquals("Set of missing keys not as expected", nonExistentIds, missingKeys);
    }

    /**
     * Tests {@link BlobStore#isKeyDeleted(StoreKey)} including error cases.
     *
     * @throws StoreException
     * 		
     */
    @Test
    public void isKeyDeletedTest() throws StoreException {
        for (MockId id : allKeys.keySet()) {
            Assert.assertEquals("Returned state is not as expected", deletedKeys.contains(id), store.isKeyDeleted(id));
        }
        // non existent id
        try {
            store.isKeyDeleted(getUniqueId());
            Assert.fail("Getting the deleted state of a non existent key should have failed");
        } catch (StoreException e) {
            Assert.assertEquals("Unexpected StoreErrorCode", ID_Not_Found, e.getErrorCode());
        }
    }

    /**
     * Tests store shutdown and the ability to do operations when a store is shutdown or has not been started yet.
     *
     * @throws StoreException
     * 		
     */
    @Test
    public void shutdownTest() throws StoreException {
        store.shutdown();
        // no operations should be possible if store is not up or has been shutdown
        verifyOperationFailuresOnInactiveStore(store);
        store = createBlobStore(getMockReplicaId(tempDirStr));
        verifyOperationFailuresOnInactiveStore(store);
    }

    /**
     * Tests that {@link BlobStore#getDiskSpaceRequirements()} functions as expected.
     *
     * @throws StoreException
     * 		
     */
    @Test
    public void diskSpaceRequirementsTest() throws Exception {
        // expect three log segments to be already allocated (from setup process)
        int segmentsAllocated = 3;
        doDiskSpaceRequirementsTest(segmentsAllocated, 0);
        // try adding fake swap segment log segment.
        File tempFile = File.createTempFile("sample-swap", ((SUFFIX) + (TEMP_LOG_SEGMENT_NAME_SUFFIX)), tempDir);
        doDiskSpaceRequirementsTest(segmentsAllocated, 1);
        Assert.assertTrue("Could not delete temp file", tempFile.delete());
        addCuratedData(BlobStoreTest.SEGMENT_CAPACITY, true);
        segmentsAllocated += 1;
        doDiskSpaceRequirementsTest(segmentsAllocated, 0);
        File.createTempFile("sample-swap", ((SUFFIX) + (TEMP_LOG_SEGMENT_NAME_SUFFIX)), tempDir).deleteOnExit();
        File.createTempFile("sample-swap", ((SUFFIX) + (TEMP_LOG_SEGMENT_NAME_SUFFIX)), tempDir).deleteOnExit();
        addCuratedData(BlobStoreTest.SEGMENT_CAPACITY, true);
        segmentsAllocated += 1;
        doDiskSpaceRequirementsTest(segmentsAllocated, 2);
    }
}

