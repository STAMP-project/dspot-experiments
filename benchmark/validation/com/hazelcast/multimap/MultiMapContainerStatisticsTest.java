/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
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
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.multimap;


import com.hazelcast.core.MultiMap;
import com.hazelcast.multimap.impl.MultiMapContainer;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class MultiMapContainerStatisticsTest extends HazelcastTestSupport {
    private static final String MULTI_MAP_NAME = "multiMap";

    private String key;

    private MultiMap<String, String> multiMap;

    private MultiMapContainer mapContainer;

    private MultiMapContainer mapBackupContainer;

    private long previousAccessTime;

    private long previousUpdateTime;

    private long previousAccessTimeOnBackup;

    private long previousUpdateTimeOnBackup;

    @Test
    public void testMultiMapContainerStats() {
        HazelcastTestSupport.assertNotEqualsStringFormat("Expected the creationTime not to be %d, but was %d", 0L, mapContainer.getCreationTime());
        HazelcastTestSupport.assertEqualsStringFormat("Expected the lastAccessTime to be %d, but was %d", 0L, mapContainer.getLastAccessTime());
        HazelcastTestSupport.assertEqualsStringFormat("Expected the lastUpdateTime to be %d, but was %d", 0L, mapContainer.getLastUpdateTime());
        HazelcastTestSupport.assertNotEqualsStringFormat("Expected the creationTime on backup not to be %d, but was %d", 0L, mapBackupContainer.getCreationTime());
        HazelcastTestSupport.assertEqualsStringFormat("Expected the lastAccessTime on backup to be %d, but was %d", 0L, mapBackupContainer.getLastAccessTime());
        HazelcastTestSupport.assertEqualsStringFormat("Expected the lastUpdateTime on backup to be %d, but was %d", 0L, mapBackupContainer.getLastUpdateTime());
        // a get operation updates the lastAccessTime, but not the lastUpdateTime
        HazelcastTestSupport.sleepMillis(10);
        multiMap.get(key);
        assertNewLastAccessTime();
        assertSameLastUpdateTime();
        // a put operation updates the lastAccessTime and lastUpdateTime
        HazelcastTestSupport.sleepMillis(10);
        multiMap.put(key, "value");
        assertNewLastAccessTime();
        assertNewLastUpdateTime();
        // a get operation updates the lastAccessTime, but not the lastUpdateTime
        HazelcastTestSupport.sleepMillis(10);
        multiMap.get(key);
        assertNewLastAccessTime();
        assertSameLastUpdateTime();
        // a delete operation updates the lastAccessTime and lastUpdateTime
        HazelcastTestSupport.sleepMillis(10);
        multiMap.delete(key);
        assertNewLastAccessTime();
        assertNewLastUpdateTime();
        // a put operation updates the lastAccessTime and lastUpdateTime
        HazelcastTestSupport.sleepMillis(10);
        multiMap.put(key, "value");
        assertNewLastAccessTime();
        assertNewLastUpdateTime();
        // an unsuccessful remove operation updates the lastAccessTime, but not the lastUpdateTime
        HazelcastTestSupport.sleepMillis(10);
        Assert.assertFalse("Expected an unsuccessful remove operation", multiMap.remove(key, "invalidValue"));
        assertNewLastAccessTime();
        assertSameLastUpdateTime();
        // a successful remove operation updates the lastAccessTime and the lastUpdateTime
        HazelcastTestSupport.sleepMillis(10);
        Assert.assertTrue("Expected a successful remove operation", multiMap.remove(key, "value"));
        assertNewLastAccessTime();
        assertNewLastUpdateTime();
        // an unsuccessful clear operation updates the lastAccessTime, but not the lastUpdateTime
        HazelcastTestSupport.sleepMillis(10);
        multiMap.clear();
        assertNewLastAccessTime();
        assertSameLastUpdateTime();
        // a put operation updates the lastAccessTime and lastUpdateTime
        HazelcastTestSupport.sleepMillis(10);
        multiMap.put(key, "value");
        assertNewLastAccessTime();
        assertNewLastUpdateTime();
        // a successful clear operation updates the lastAccessTime and the lastUpdateTime
        HazelcastTestSupport.sleepMillis(10);
        multiMap.clear();
        assertNewLastAccessTime();
        assertNewLastUpdateTime();
        // no operation should update the lastAccessTime or lastUpdateTime on the backup container
        assertSameLastAccessTimeOnBackup();
        assertSameLastUpdateTimeOnBackup();
    }
}

