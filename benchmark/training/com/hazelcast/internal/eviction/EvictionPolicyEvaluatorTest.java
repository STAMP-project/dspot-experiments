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


import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class EvictionPolicyEvaluatorTest extends HazelcastTestSupport {
    private final class SimpleEvictionCandidate<K, V extends Evictable> implements EvictionCandidate<K, V> {
        private K key;

        private V value;

        private SimpleEvictionCandidate(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getAccessor() {
            return key;
        }

        @Override
        public V getEvictable() {
            return value;
        }

        @Override
        public Object getKey() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object getValue() {
            throw new UnsupportedOperationException();
        }

        @Override
        public long getCreationTime() {
            return getEvictable().getCreationTime();
        }

        @Override
        public long getLastAccessTime() {
            return getEvictable().getLastAccessTime();
        }

        @Override
        public long getAccessHit() {
            return getEvictable().getAccessHit();
        }
    }

    @Test
    public void test_leastRecentlyAccessedEntry_isSelected_when_evictionPolicy_is_LRU() {
        test_evictionPolicyLRU(false);
    }

    @Test
    public void test_expiredEntry_hasMorePriority_than_leastRecentlyAccessedEntry_toBeEvicted_when_evictionPolicy_is_LRU() {
        test_evictionPolicyLRU(true);
    }

    @Test
    public void test_leastFrequentlyUsedEntry_isSelected_when_evictionPolicy_is_LFU() {
        test_evictionPolicyLFU(false);
    }

    @Test
    public void test_expiredEntry_hasMorePriority_than_leastFrequentlyUsedEntry_toBeEvicted_when_evictionPolicy_is_LFU() {
        test_evictionPolicyLFU(true);
    }
}

