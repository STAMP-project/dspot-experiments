/**
 * -
 * #%L
 * rapidoid-commons
 * %%
 * Copyright (C) 2014 - 2018 Nikolche Mihajlovski and contributors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.rapidoid.cache;


import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cache.impl.CacheStats;
import org.rapidoid.cache.impl.ConcurrentCacheAtom;
import org.rapidoid.commons.Nums;
import org.rapidoid.commons.Rnd;
import org.rapidoid.lambda.Mapper;
import org.rapidoid.test.TestCommons;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;


@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class SimpleCachingTest extends TestCommons {
    private static final Mapper<Integer, Integer> N_TO_N = ( key) -> key;

    private static Mapper<Integer, Integer> NEXT = ( x) -> x + 1;

    private static Mapper<String, String> ABC = IO::load;

    @Test
    public void testCachedValue() {
        ConcurrentCacheAtom<String, String> cached = new ConcurrentCacheAtom("cached-file.txt", SimpleCachingTest.ABC, 10);
        Msc.benchmarkMT(100, "reads", 10000000, () -> eq(cached.get(), "ABC"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCache() {
        final int capacity = 1000;
        final Cache<Integer, Integer> cache = Caching.of(SimpleCachingTest.NEXT).capacity(capacity).ttl(10).statistics(true).build();
        Msc.benchmarkMT(100, "ops", 10000000, () -> {
            int n = Rnd.rnd((capacity * 100));
            if ((Rnd.rnd(3)) == 0)
                cache.invalidate(n);

            Integer maybe = cache.getIfExists(n);
            isTrue(((maybe == null) || (maybe == (n + 1))));
            eq(cache.get(n).intValue(), (n + 1));
            if ((Rnd.rnd(5)) == 0)
                cache.set(n, (n + 1));

        });
        CacheStats stats = cache.stats();
        U.print(stats);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testPreloadedCache() {
        int count = 100000;
        final Cache<Integer, Integer> cache = Caching.of(SimpleCachingTest.N_TO_N).statistics(true).build();
        loadCacheValues(cache, count);
        final int mask = Nums.bitMask(10);// 1024 hot keys

        int total = 200000000;
        Msc.benchmarkMT(8, "ops", total, ( i) -> {
            int key = i & mask;
            int n = cache.get(key);
            eq(n, key);
            cache.bypass();
        });
        CacheStats stats = cache.stats();
        U.print(stats);
        eq(stats.hits(), (total + (2 * count)));
        eq(stats.requests(), (total + (3 * count)));
        eq(stats.bypassed(), total);
        eq(cache.size(), count);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCacheInvalidation() {
        final AtomicInteger value = new AtomicInteger();
        final Cache<Integer, Integer> cache = Caching.of(((Mapper<Integer, Integer>) (( key) -> value.get()))).capacity(64).build();
        for (int i = 0; i < 100; i++) {
            iteration(value, cache);
        }
    }
}

