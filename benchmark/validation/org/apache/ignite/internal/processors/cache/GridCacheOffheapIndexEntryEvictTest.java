/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.internal.processors.cache;


import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 *
 */
public class GridCacheOffheapIndexEntryEvictTest extends GridCommonAbstractTest {
    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testQueryWhenLocked() throws Exception {
        IgniteCache<Integer, GridCacheOffheapIndexEntryEvictTest.TestValue> cache = grid(0).cache(DEFAULT_CACHE_NAME);
        List<Lock> locks = new ArrayList<>();
        final int ENTRIES = 1000;
        try {
            for (int i = 0; i < ENTRIES; i++) {
                cache.put(i, new GridCacheOffheapIndexEntryEvictTest.TestValue(i));
                Lock lock = cache.lock(i);
                lock.lock();// Lock entry so that it should not be evicted.

                locks.add(lock);
                for (int j = 0; j < 3; j++)
                    assertNotNull(cache.get(i));

            }
            checkQuery(cache, "_key >= 100", (ENTRIES - 100));
        } finally {
            for (Lock lock : locks)
                lock.unlock();

        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testUpdates() throws Exception {
        final int ENTRIES = 500;
        IgniteCache<Integer, GridCacheOffheapIndexEntryEvictTest.TestValue> cache = grid(0).cache(DEFAULT_CACHE_NAME);
        for (int i = 0; i < ENTRIES; i++) {
            for (int j = 0; j < 3; j++) {
                cache.getAndPut(i, new GridCacheOffheapIndexEntryEvictTest.TestValue(i));
                assertNotNull(cache.get(i));
                assertNotNull(cache.localPeek(i));
            }
            checkQuery(cache, "_key >= 0", (i + 1));
        }
        for (int i = 0; i < ENTRIES; i++) {
            if ((i % 2) == 0)
                cache.getAndRemove(i);
            else
                cache.remove(i);

            checkQuery(cache, "_key >= 0", (ENTRIES - (i + 1)));
        }
    }

    /**
     *
     */
    static class TestValue implements Externalizable {
        /**
         *
         */
        private int val;

        /**
         *
         */
        public TestValue() {
            // No-op.
        }

        /**
         *
         *
         * @param val
         * 		Value.
         */
        public TestValue(int val) {
            this.val = val;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeInt(val);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            val = in.readInt();
        }
    }
}

