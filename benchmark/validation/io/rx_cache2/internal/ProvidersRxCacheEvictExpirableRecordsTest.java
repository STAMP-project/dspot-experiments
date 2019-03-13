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


import io.reactivex.observers.TestObserver;
import io.rx_cache2.DynamicKey;
import io.rx_cache2.internal.cache.EvictExpirableRecordsPersistence;
import io.rx_cache2.internal.common.BaseTestEvictingTask;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runners.MethodSorters;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ProvidersRxCacheEvictExpirableRecordsTest extends BaseTestEvictingTask {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private ProvidersRxCache providersRxCache;

    private int maxMgPersistenceCache = 7;

    @Test
    public void When_Expirable_Records_Evict() {
        Assert.assertThat(getSizeMB(temporaryFolder.getRoot()), CoreMatchers.is(0));
        for (int i = 0; i < 50; i++) {
            waitTime(50);
            TestObserver<List<Mock>> observer = new TestObserver();
            String key = i + "";
            providersRxCache.getMocksPaginate(createObservableMocks(), new DynamicKey(key)).subscribe(observer);
            observer.awaitTerminalEvent();
        }
        waitTime(5000);
        int expectedStoredMB = ((int) ((maxMgPersistenceCache) * (EvictExpirableRecordsPersistence.PERCENTAGE_MEMORY_STORED_TO_STOP)));
        Assert.assertThat(getSizeMB(temporaryFolder.getRoot()), CoreMatchers.is(expectedStoredMB));
    }

    @Test
    public void When_No_Expirable_Records_Do_Not_Evict() {
        Assert.assertThat(getSizeMB(temporaryFolder.getRoot()), CoreMatchers.is(0));
        for (int i = 0; i < 50; i++) {
            waitTime(50);
            TestObserver<List<Mock>> observer = new TestObserver();
            String key = i + "";
            providersRxCache.getMocksPaginateNotExpirable(createObservableMocks(), new DynamicKey(key)).subscribe(observer);
            observer.awaitTerminalEvent();
        }
        waitTime(1000);
        Assert.assertThat(getSizeMB(temporaryFolder.getRoot()), CoreMatchers.is(maxMgPersistenceCache));
    }
}

