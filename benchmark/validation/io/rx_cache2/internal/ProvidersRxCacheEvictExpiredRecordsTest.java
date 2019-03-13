/**
 * Copyright 2016 Victor Albertos
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.rx_cache2.internal;


import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.rx_cache2.DynamicKey;
import io.rx_cache2.Encrypt;
import io.rx_cache2.EncryptKey;
import io.rx_cache2.LifeCache;
import io.rx_cache2.internal.common.BaseTestEvictingTask;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runners.MethodSorters;


/**
 * Created by victor on 03/03/16.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ProvidersRxCacheEvictExpiredRecordsTest extends BaseTestEvictingTask {
    @ClassRule
    public static TemporaryFolder temporaryFolder = new TemporaryFolder();

    private ProvidersRxCacheEvictExpiredRecordsTest.ProvidersRxCache providersRxCache;

    @Test
    public void _1_Populate_Disk_With_Expired_Records() {
        Assert.assertEquals(0, getSizeMB(ProvidersRxCacheEvictExpiredRecordsTest.temporaryFolder.getRoot()));
        for (int i = 0; i < 50; i++) {
            waitTime(50);
            TestObserver<List<Mock>> observer = new TestObserver();
            String key = ((System.currentTimeMillis()) + i) + "";
            providersRxCache.getEphemeralMocksPaginate(createObservableMocks(), new DynamicKey(key)).subscribe(observer);
            observer.awaitTerminalEvent();
        }
        Assert.assertNotEquals(0, getSizeMB(ProvidersRxCacheEvictExpiredRecordsTest.temporaryFolder.getRoot()));
    }

    @Test
    public void _2_Perform_Evicting_Task_And_Check_Results() {
        waitTime(1000);
        Assert.assertEquals(0, ProvidersRxCacheEvictExpiredRecordsTest.temporaryFolder.getRoot().listFiles().length);
        Assert.assertEquals(0, getSizeMB(ProvidersRxCacheEvictExpiredRecordsTest.temporaryFolder.getRoot()));
    }

    @Test
    public void _3_Populate_Disk_With_No_Expired_Records() {
        deleteAllFiles();
        Assert.assertEquals(0, getSizeMB(ProvidersRxCacheEvictExpiredRecordsTest.temporaryFolder.getRoot()));
        for (int i = 0; i < 50; i++) {
            waitTime(50);
            TestObserver<List<Mock>> subscriber = new TestObserver();
            String key = ((System.currentTimeMillis()) + i) + "";
            providersRxCache.getMocksPaginate(createObservableMocks(), new DynamicKey(key)).subscribe(subscriber);
            subscriber.awaitTerminalEvent();
        }
        Assert.assertNotEquals(0, getSizeMB(ProvidersRxCacheEvictExpiredRecordsTest.temporaryFolder.getRoot()));
    }

    @Test
    public void _4_Perform_Evicting_Task_And_Check_Results() {
        waitTime(1000);
        Assert.assertNotEquals(0, getSizeMB(ProvidersRxCacheEvictExpiredRecordsTest.temporaryFolder.getRoot()));
    }

    @Test
    public void _5_Populate_Disk_With_Expired_Encrypted_Records() {
        deleteAllFiles();
        Assert.assertEquals(0, ProvidersRxCacheEvictExpiredRecordsTest.temporaryFolder.getRoot().listFiles().length);
        for (int i = 0; i < 50; i++) {
            waitTime(50);
            TestObserver<List<Mock>> observer = new TestObserver();
            String key = ((System.currentTimeMillis()) + i) + "";
            providersRxCache.getEphemeralEncryptedMocksPaginate(createObservableMocks(), new DynamicKey(key)).subscribe(observer);
            observer.awaitTerminalEvent();
        }
        Assert.assertNotEquals(0, getSizeMB(ProvidersRxCacheEvictExpiredRecordsTest.temporaryFolder.getRoot()));
    }

    @Test
    public void _6_Perform_Evicting_Task_And_Check_Results() {
        waitTime(1000);
        Assert.assertEquals(0, ProvidersRxCacheEvictExpiredRecordsTest.temporaryFolder.getRoot().listFiles().length);
        Assert.assertEquals(0, getSizeMB(ProvidersRxCacheEvictExpiredRecordsTest.temporaryFolder.getRoot()));
    }

    @EncryptKey("myStrongKey-1234")
    private interface ProvidersRxCache {
        Observable<List<Mock>> getMocksPaginate(Observable<List<Mock>> mocks, DynamicKey page);

        @LifeCache(duration = 1, timeUnit = TimeUnit.MILLISECONDS)
        Observable<List<Mock>> getEphemeralMocksPaginate(Observable<List<Mock>> mocks, DynamicKey page);

        @Encrypt
        @LifeCache(duration = 1, timeUnit = TimeUnit.MILLISECONDS)
        Observable<List<Mock>> getEphemeralEncryptedMocksPaginate(Observable<List<Mock>> mocks, DynamicKey page);
    }
}

