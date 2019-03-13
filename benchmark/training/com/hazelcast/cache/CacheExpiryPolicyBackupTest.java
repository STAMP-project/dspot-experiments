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
package com.hazelcast.cache;


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.HashSet;
import java.util.Set;
import javax.cache.expiry.EternalExpiryPolicy;
import javax.cache.expiry.ExpiryPolicy;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@Category({ QuickTest.class, ParallelTest.class })
@RunWith(HazelcastSerialClassRunner.class)
public class CacheExpiryPolicyBackupTest extends HazelcastTestSupport {
    private static final int NINSTANCES = 3;

    private static final int ENTRIES = 100;

    private TestHazelcastInstanceFactory factory;

    private HazelcastInstance[] instances;

    private String cacheName;

    @Test
    public void testSetExpiryPolicyBackupOperation() {
        HazelcastInstance instance = instances[0];
        ICache<String, String> cache = instance.getCacheManager().getCache(cacheName);
        Set<String> keys = new HashSet<String>();
        for (int i = 0; i < (CacheExpiryPolicyBackupTest.ENTRIES); i++) {
            cache.put(("key" + i), "value");
            keys.add(("key" + i));
        }
        ExpiryPolicy expiryPolicy = new EternalExpiryPolicy();
        cache.setExpiryPolicy(keys, expiryPolicy);
        assertExpiryPolicy(expiryPolicy, keys);
    }
}

