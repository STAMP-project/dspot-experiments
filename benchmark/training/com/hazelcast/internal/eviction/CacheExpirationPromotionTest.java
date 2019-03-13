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
package com.hazelcast.internal.eviction;


import com.hazelcast.cache.HazelcastCacheManager;
import com.hazelcast.cache.ICache;
import com.hazelcast.cache.impl.HazelcastServerCacheManager;
import com.hazelcast.cache.impl.HazelcastServerCachingProvider;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.OverridePropertyRule;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.test.backup.BackupAccessor;
import com.hazelcast.test.backup.TestBackupUtils;
import java.util.concurrent.TimeUnit;
import javax.cache.expiry.Duration;
import javax.cache.spi.CachingProvider;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ ParallelTest.class, QuickTest.class })
public class CacheExpirationPromotionTest extends HazelcastTestSupport {
    @Rule
    public final OverridePropertyRule overrideTaskSecondsRule = OverridePropertyRule.set(PROP_TASK_PERIOD_SECONDS, "1");

    private String cacheName = "test";

    private int nodeCount = 3;

    private TestHazelcastInstanceFactory factory;

    private HazelcastInstance[] instances;

    @Test
    public void promoted_replica_should_send_eviction_to_other_backup() {
        final CachingProvider provider = HazelcastServerCachingProvider.createCachingProvider(instances[0]);
        provider.getCacheManager().createCache(cacheName, getCacheConfig());
        HazelcastCacheManager cacheManager = ((HazelcastServerCacheManager) (provider.getCacheManager()));
        final String keyOwnedByLastInstance = HazelcastTestSupport.generateKeyOwnedBy(instances[((nodeCount) - 1)]);
        ICache<String, String> cache = cacheManager.getCache(cacheName).unwrap(ICache.class);
        cache.put(keyOwnedByLastInstance, "dummyVal", new javax.cache.expiry.CreatedExpiryPolicy(new Duration(TimeUnit.SECONDS, 5)));
        final BackupAccessor<String, String> backupAccessor = TestBackupUtils.newCacheAccessor(instances, cacheName, 1);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertNotNull(backupAccessor.get(keyOwnedByLastInstance));
            }
        });
        instances[((nodeCount) - 1)].shutdown();
        // the backup replica became the primary, now the backup is the other node.
        // we check if the newly appointed replica sent expiration to backups
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(0, backupAccessor.size());
            }
        });
    }
}

