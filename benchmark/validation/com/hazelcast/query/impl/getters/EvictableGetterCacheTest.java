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
package com.hazelcast.query.impl.getters;


import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class EvictableGetterCacheTest {
    @Test
    public void get_put_correctResult() {
        // GIVEN
        EvictableGetterCache cache = new EvictableGetterCache(10, 10, 0.5F, true);
        Getter x = Mockito.mock(Getter.class);
        Getter y = Mockito.mock(Getter.class);
        // WHEN
        cache.putGetter(String.class, "x", x);
        cache.putGetter(String.class, "y", y);
        // THEN
        MatcherAssert.assertThat(cache.getGetter(String.class, "x"), Matchers.equalTo(x));
        MatcherAssert.assertThat(cache.getGetter(String.class, "y"), Matchers.equalTo(y));
    }

    @Test
    public void get_put_correctSize() {
        // GIVEN
        EvictableGetterCache cache = new EvictableGetterCache(10, 10, 0.5F, true);
        Getter x = Mockito.mock(Getter.class);
        Getter y = Mockito.mock(Getter.class);
        // WHEN
        cache.putGetter(String.class, "x", x);
        cache.putGetter(Double.class, "y", y);
        // THEN
        MatcherAssert.assertThat(cache.getClassCacheSize(), Matchers.equalTo(2));
        MatcherAssert.assertThat(cache.getGetterPerClassCacheSize(String.class), Matchers.equalTo(1));
        MatcherAssert.assertThat(cache.getGetterPerClassCacheSize(Double.class), Matchers.equalTo(1));
    }

    @Test
    public void getterCache_evictLimitNotReached_noEviction() {
        // GIVEN
        int getterCacheSize = 10;
        float evictPercentage = 0.3F;
        EvictableGetterCache cache = new EvictableGetterCache(10, getterCacheSize, evictPercentage, true);
        // WHEN
        for (int i = 0; i < (getterCacheSize - 1); i++) {
            cache.putGetter(String.class, ("x" + i), Mockito.mock(Getter.class));
        }
        // THEN
        MatcherAssert.assertThat(cache.getClassCacheSize(), Matchers.equalTo(1));
        MatcherAssert.assertThat(cache.getGetterPerClassCacheSize(String.class), Matchers.equalTo((getterCacheSize - 1)));
    }

    @Test
    public void getterCache_evictLimitReached_evictionTriggered() {
        // GIVEN
        int getterCacheSize = 10;
        float evictPercentage = 0.3F;
        EvictableGetterCache cache = new EvictableGetterCache(10, getterCacheSize, evictPercentage, true);
        // WHEN
        for (int i = 0; i < getterCacheSize; i++) {
            cache.putGetter(String.class, ("x" + i), Mockito.mock(Getter.class));
        }
        // THEN
        int expectedSizeAfterEviction = ((int) (getterCacheSize * (1 - evictPercentage)));
        MatcherAssert.assertThat(cache.getClassCacheSize(), Matchers.equalTo(1));
        MatcherAssert.assertThat(cache.getGetterPerClassCacheSize(String.class), Matchers.equalTo(expectedSizeAfterEviction));
    }

    @Test
    public void classCache_evictLimitNotReached_noEviction() {
        // GIVEN
        int classCacheSize = 10;
        float evictPercentage = 0.3F;
        EvictableGetterCache cache = new EvictableGetterCache(classCacheSize, 10, evictPercentage, true);
        Class[] classes = new Class[]{ String.class, Character.class, Integer.class, Double.class, Byte.class, Long.class, Number.class, Float.class, BigDecimal.class, BigInteger.class };
        // WHEN
        for (int i = 0; i < (classCacheSize - 1); i++) {
            cache.putGetter(classes[i], "x", Mockito.mock(Getter.class));
        }
        // THEN
        MatcherAssert.assertThat(cache.getClassCacheSize(), Matchers.equalTo((classCacheSize - 1)));
    }

    @Test
    public void classCache_evictLimitReached_evictionTriggered() {
        // GIVEN
        int classCacheSize = 10;
        float evictPercentage = 0.3F;
        EvictableGetterCache cache = new EvictableGetterCache(classCacheSize, 10, evictPercentage, true);
        Class[] classes = new Class[]{ String.class, Character.class, Integer.class, Double.class, Byte.class, Long.class, Number.class, Float.class, BigDecimal.class, BigInteger.class };
        // WHEN
        for (int i = 0; i < classCacheSize; i++) {
            cache.putGetter(classes[i], "x", Mockito.mock(Getter.class));
        }
        // THEN
        int expectedSizeAfterEviction = ((int) (classCacheSize * (1 - evictPercentage)));
        MatcherAssert.assertThat(cache.getClassCacheSize(), Matchers.equalTo(expectedSizeAfterEviction));
    }
}

