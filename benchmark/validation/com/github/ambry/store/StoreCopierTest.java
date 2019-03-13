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
package com.github.ambry.store;


import StoreErrorCodes.ID_Not_Found;
import com.codahale.metrics.MetricRegistry;
import com.github.ambry.clustermap.ClusterMap;
import com.github.ambry.clustermap.MockClusterMap;
import com.github.ambry.config.StoreConfig;
import com.github.ambry.config.VerifiableProperties;
import com.github.ambry.utils.ByteBufferChannel;
import com.github.ambry.utils.MockTime;
import com.github.ambry.utils.Pair;
import com.github.ambry.utils.TestUtils;
import com.github.ambry.utils.Time;
import com.github.ambry.utils.Utils;
import com.github.ambry.utils.UtilsTest;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;

import static StoreDescriptor.STORE_DESCRIPTOR_FILENAME;
import static StoreTestUtils.DEFAULT_DISK_SPACE_ALLOCATOR;


/**
 * Tests functionality of {@link StoreCopier}
 */
public class StoreCopierTest {
    private static final String STORE_ID = "copier_test";

    private static final DiskIOScheduler DISK_IO_SCHEDULER = new DiskIOScheduler(null);

    private static final StoreKeyFactory STORE_KEY_FACTORY;

    static {
        try {
            STORE_KEY_FACTORY = Utils.getObj("com.github.ambry.store.MockIdFactory");
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static final long STORE_CAPACITY = 1000;

    private static final int PUT_RECORD_SIZE = 53;

    private static final int DELETE_RECORD_SIZE = 29;

    private static final int TTL_UPDATE_RECORD_SIZE = 37;

    private final File srcDir;

    private final File tgtDir;

    private final StoreCopier storeCopier;

    private final StoreConfig storeConfig;

    private final ClusterMap clusterMap = new MockClusterMap();

    private final Time time = new MockTime();

    private StoreKey permanentPutId;

    private StoreKey putAndTtlUpdatedId;

    private StoreKey temporaryPutId;

    private byte[] putAndTtlUpdatedData;

    private byte[] permanentPutData;

    private byte[] temporaryPutData;

    private long temporaryPutExpiryTimeMs;

    private StoreKey expiredId;

    private StoreKey deletedId;

    private StoreKey putTtlUpdatedAndDeletedId;

    /**
     * Creates temporary directories and sets up some test state.
     *
     * @throws Exception
     * 		
     */
    public StoreCopierTest() throws Exception {
        srcDir = StoreTestUtils.createTempDirectory(("srcDir-" + (UtilsTest.getRandomString(10))));
        tgtDir = StoreTestUtils.createTempDirectory(("tgtDir-" + (UtilsTest.getRandomString(10))));
        Properties properties = new Properties();
        properties.setProperty("store.key.factory", MockIdFactory.class.getCanonicalName());
        properties.setProperty("src.store.dir", srcDir.getAbsolutePath());
        properties.setProperty("tgt.store.dir", tgtDir.getAbsolutePath());
        properties.setProperty("store.capacity", Long.toString(StoreCopierTest.STORE_CAPACITY));
        VerifiableProperties verifiableProperties = new VerifiableProperties(properties);
        storeConfig = new StoreConfig(verifiableProperties);
        setupTestState();
        time.sleep(TimeUnit.SECONDS.toMillis(((TestUtils.TTL_SECS) + 1)));
        StoreMetrics metrics = new StoreMetrics(clusterMap.getMetricRegistry());
        storeCopier = new StoreCopier("test_store", srcDir, tgtDir, StoreCopierTest.STORE_CAPACITY, ((4 * 1024) * 1024), storeConfig, metrics, StoreCopierTest.STORE_KEY_FACTORY, StoreCopierTest.DISK_IO_SCHEDULER, DEFAULT_DISK_SPACE_ALLOCATOR, Collections.EMPTY_LIST, time);
    }

    /**
     * Tests {@link StoreCopier#copy(FindToken)}.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void copyTest() throws Exception {
        storeCopier.copy(getNewFindToken());
        storeCopier.close();
        // copy the store descriptor file over
        StoreMetrics storeMetrics = new StoreMetrics(new MetricRegistry());
        Files.copy(new File(srcDir, STORE_DESCRIPTOR_FILENAME).toPath(), new File(tgtDir, STORE_DESCRIPTOR_FILENAME).toPath(), StandardCopyOption.REPLACE_EXISTING);
        BlobStore tgt = new BlobStore(StoreCopierTest.STORE_ID, storeConfig, null, null, StoreCopierTest.DISK_IO_SCHEDULER, DEFAULT_DISK_SPACE_ALLOCATOR, storeMetrics, storeMetrics, tgtDir.getAbsolutePath(), StoreCopierTest.STORE_CAPACITY, StoreCopierTest.STORE_KEY_FACTORY, null, null, time);
        tgt.start();
        try {
            // should not be able to get expired or deleted ids
            StoreKey[] failKeys = new StoreKey[]{ expiredId, deletedId, putTtlUpdatedAndDeletedId };
            for (StoreKey key : failKeys) {
                try {
                    tgt.get(Collections.singletonList(key), EnumSet.allOf(StoreGetOptions.class));
                    Assert.fail(("Should have failed to get " + key));
                } catch (StoreException e) {
                    Assert.assertEquals("Unexpected StoreErrorCode", ID_Not_Found, e.getErrorCode());
                }
            }
            // should be able to get the non expired, non deleted entries
            Map<StoreKey, Pair<byte[], Long>> successKeys = new HashMap<>();
            successKeys.put(permanentPutId, new Pair(permanentPutData, Utils.Infinite_Time));
            successKeys.put(putAndTtlUpdatedId, new Pair(putAndTtlUpdatedData, Utils.Infinite_Time));
            successKeys.put(temporaryPutId, new Pair(temporaryPutData, temporaryPutExpiryTimeMs));
            for (Map.Entry<StoreKey, Pair<byte[], Long>> entry : successKeys.entrySet()) {
                StoreInfo storeInfo = tgt.get(Collections.singletonList(entry.getKey()), EnumSet.noneOf(StoreGetOptions.class));
                MessageInfo messageInfo = storeInfo.getMessageReadSetInfo().get(0);
                byte[] data = entry.getValue().getFirst();
                Assert.assertEquals("Size does not match", data.length, messageInfo.getSize());
                Assert.assertEquals("Size does not match", data.length, storeInfo.getMessageReadSet().sizeInBytes(0));
                Assert.assertFalse("Should not be deleted or expired", ((messageInfo.isDeleted()) || (messageInfo.isExpired())));
                Assert.assertEquals("Ttl update flag not as expected", putAndTtlUpdatedId.equals(entry.getKey()), messageInfo.isTtlUpdated());
                Assert.assertEquals("Expiration time does not match", entry.getValue().getSecond().longValue(), messageInfo.getExpirationTimeInMs());
                ByteBufferChannel channel = new ByteBufferChannel(ByteBuffer.allocate(data.length));
                storeInfo.getMessageReadSet().writeTo(0, channel, 0, data.length);
                Assert.assertArrayEquals("Data put does not match data copied", data, channel.getBuffer().array());
            }
        } finally {
            tgt.shutdown();
        }
    }
}

