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


import Cache.Entry;
import com.hazelcast.test.HazelcastParametersRunnerFactory;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Arrays;
import java.util.Iterator;
import javax.cache.spi.CachingProvider;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParametersRunnerFactory.class)
@Category({ QuickTest.class, ParallelTest.class })
public class CacheClusterWideIteratorTest extends HazelcastTestSupport {
    private CachingProvider cachingProvider;

    @Parameterized.Parameter(0)
    public boolean prefetchValues;

    @Test
    public void testIterator() {
        ICache<Integer, Integer> cache = ((ICache<Integer, Integer>) (getCache()));
        int size = 1111;
        int multiplier = 11;
        for (int i = 0; i < size; i++) {
            cache.put(i, (i * multiplier));
        }
        int[] keys = new int[size];
        int k = 0;
        Iterator<Entry<Integer, Integer>> iter = getIterator(cache);
        while (iter.hasNext()) {
            Entry<Integer, Integer> e = iter.next();
            int key = e.getKey();
            int value = e.getValue();
            Assert.assertEquals((key * multiplier), value);
            keys[(k++)] = key;
        } 
        Assert.assertEquals(size, k);
        Arrays.sort(keys);
        for (int i = 0; i < size; i++) {
            Assert.assertEquals(i, keys[i]);
        }
    }

    @Test
    public void testIteratorRemove() {
        ICache<Integer, Integer> cache = ((ICache<Integer, Integer>) (getCache()));
        int size = 1111;
        for (int i = 0; i < size; i++) {
            cache.put(i, i);
        }
        Iterator<Entry<Integer, Integer>> iter = getIterator(cache);
        while (iter.hasNext()) {
            iter.next();
            iter.remove();
        } 
        Assert.assertEquals(0, cache.size());
    }

    @Test(expected = IllegalStateException.class)
    public void testIteratorIllegalRemove() {
        ICache<Integer, Integer> cache = ((ICache<Integer, Integer>) (getCache()));
        int size = 10;
        for (int i = 0; i < size; i++) {
            cache.put(i, i);
        }
        Iterator<Entry<Integer, Integer>> iter = getIterator(cache);
        if (iter.hasNext()) {
            iter.remove();
        }
    }
}

