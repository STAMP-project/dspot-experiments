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
package com.hazelcast.map.impl.querycache.utils;


import com.hazelcast.map.impl.querycache.QueryCacheContext;
import com.hazelcast.map.impl.querycache.accumulator.Accumulator;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class QueryCacheUtilTest extends HazelcastTestSupport {
    private QueryCacheContext context;

    @Test
    public void testConstructor() {
        HazelcastTestSupport.assertUtilityConstructor(QueryCacheUtil.class);
    }

    @Test
    public void getAccumulators_whenNoAccumulatorsRegistered_thenReturnEmptyMap() {
        Map<Integer, Accumulator> accumulators = QueryCacheUtil.getAccumulators(context, "myMap", "myCache");
        Assert.assertNotNull(accumulators);
        Assert.assertEquals(0, accumulators.size());
    }

    @Test
    public void getAccumulatorOrNull_whenNoAccumulatorsRegistered_thenReturnNull() {
        Accumulator accumulator = QueryCacheUtil.getAccumulatorOrNull(context, "myMap", "myCache", (-1));
        Assert.assertNull(accumulator);
    }
}

