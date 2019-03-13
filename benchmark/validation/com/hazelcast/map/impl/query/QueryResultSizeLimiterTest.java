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
package com.hazelcast.map.impl.query;


import QueryResultSizeLimiter.DISABLED;
import QueryResultSizeLimiter.MINIMUM_MAX_RESULT_LIMIT;
import com.hazelcast.map.QueryResultSizeExceededException;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static QueryResultSizeLimiter.MINIMUM_MAX_RESULT_LIMIT;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class QueryResultSizeLimiterTest {
    private static final String ANY_MAP_NAME = "foobar";

    private static final int DEFAULT_PARTITION_COUNT = 271;

    private final Map<Integer, Integer> localPartitions = new HashMap<Integer, Integer>();

    private QueryResultSizeLimiter limiter;

    @Test(expected = IllegalArgumentException.class)
    public void testNodeResultResultSizeLimitNegative() {
        initMocksWithConfiguration((-2), Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNodeResultResultSizeLimitZero() {
        initMocksWithConfiguration(0, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    @Test
    public void testNodeResultFeatureDisabled() {
        initMocksWithConfiguration((-1), Integer.MAX_VALUE, Integer.MAX_VALUE);
        Assert.assertFalse(limiter.isQueryResultLimitEnabled());
    }

    @Test
    public void testNodeResultFeatureEnabled() {
        initMocksWithConfiguration(1, Integer.MAX_VALUE, Integer.MAX_VALUE);
        Assert.assertTrue(limiter.isQueryResultLimitEnabled());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNodeResultPreCheckLimitNegative() {
        initMocksWithConfiguration(Integer.MAX_VALUE, (-2), Integer.MAX_VALUE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNodeResultPreCheckLimitZero() {
        initMocksWithConfiguration(Integer.MAX_VALUE, 0, Integer.MAX_VALUE);
    }

    @Test
    public void testNodeResultPreCheckLimitDisabled() {
        initMocksWithConfiguration(Integer.MAX_VALUE, (-1), Integer.MAX_VALUE);
        Assert.assertTrue(limiter.isQueryResultLimitEnabled());
        Assert.assertFalse(limiter.isPreCheckEnabled());
    }

    @Test
    public void testNodeResultPreCheckLimitEnabled() {
        initMocksWithConfiguration(Integer.MAX_VALUE, 1, Integer.MAX_VALUE);
        Assert.assertTrue(limiter.isQueryResultLimitEnabled());
        Assert.assertTrue(limiter.isPreCheckEnabled());
    }

    @Test
    public void testNodeResultLimitMinResultLimit() {
        initMocksWithConfiguration(MINIMUM_MAX_RESULT_LIMIT, 3);
        long nodeResultLimit1 = limiter.getNodeResultLimit(1);
        initMocksWithConfiguration(((MINIMUM_MAX_RESULT_LIMIT) / 2), 3);
        long nodeResultLimit2 = limiter.getNodeResultLimit(1);
        Assert.assertEquals(nodeResultLimit1, nodeResultLimit2);
    }

    @Test
    public void testNodeResultLimitSinglePartition() {
        initMocksWithConfiguration(200000, 3);
        Assert.assertEquals(849, limiter.getNodeResultLimit(1));
    }

    @Test
    public void testNodeResultLimitThreePartitions() {
        initMocksWithConfiguration(200000, 3);
        Assert.assertEquals(2547, limiter.getNodeResultLimit(3));
    }

    @Test
    public void testLocalPreCheckDisabled() {
        initMocksWithConfiguration(200000, DISABLED);
        limiter.precheckMaxResultLimitOnLocalPartitions(QueryResultSizeLimiterTest.ANY_MAP_NAME);
    }

    @Test
    public void testLocalPreCheckEnabledWithNoLocalPartitions() {
        initMocksWithConfiguration(200000, 1);
        limiter.precheckMaxResultLimitOnLocalPartitions(QueryResultSizeLimiterTest.ANY_MAP_NAME);
    }

    @Test
    public void testLocalPreCheckEnabledWithEmptyPartition() {
        int[] partitionsSizes = new int[]{ 0 };
        populatePartitions(partitionsSizes);
        initMocksWithConfiguration(200000, 1);
        limiter.precheckMaxResultLimitOnLocalPartitions(QueryResultSizeLimiterTest.ANY_MAP_NAME);
    }

    @Test
    public void testLocalPreCheckEnabledWitPartitionBelowLimit() {
        int[] partitionsSizes = new int[]{ 848 };
        populatePartitions(partitionsSizes);
        initMocksWithConfiguration(200000, 1);
        limiter.precheckMaxResultLimitOnLocalPartitions(QueryResultSizeLimiterTest.ANY_MAP_NAME);
    }

    @Test(expected = QueryResultSizeExceededException.class)
    public void testLocalPreCheckEnabledWitPartitionOverLimit() {
        int[] partitionsSizes = new int[]{ 1090 };
        populatePartitions(partitionsSizes);
        initMocksWithConfiguration(200000, 1);
        limiter.precheckMaxResultLimitOnLocalPartitions(QueryResultSizeLimiterTest.ANY_MAP_NAME);
    }

    @Test
    public void testLocalPreCheckEnabledWitTwoPartitionsBelowLimit() {
        int[] partitionsSizes = new int[]{ 849, 849 };
        populatePartitions(partitionsSizes);
        initMocksWithConfiguration(200000, 2);
        limiter.precheckMaxResultLimitOnLocalPartitions(QueryResultSizeLimiterTest.ANY_MAP_NAME);
    }

    @Test(expected = QueryResultSizeExceededException.class)
    public void testLocalPreCheckEnabledWitTwoPartitionsOverLimit() {
        int[] partitionsSizes = new int[]{ 1062, 1063 };
        populatePartitions(partitionsSizes);
        initMocksWithConfiguration(200000, 2);
        limiter.precheckMaxResultLimitOnLocalPartitions(QueryResultSizeLimiterTest.ANY_MAP_NAME);
    }

    @Test
    public void testLocalPreCheckEnabledWitMorePartitionsThanPreCheckThresholdBelowLimit() {
        int[] partitionSizes = new int[]{ 849, 849, Integer.MAX_VALUE };
        populatePartitions(partitionSizes);
        initMocksWithConfiguration(200000, 2);
        limiter.precheckMaxResultLimitOnLocalPartitions(QueryResultSizeLimiterTest.ANY_MAP_NAME);
    }

    @Test(expected = QueryResultSizeExceededException.class)
    public void testLocalPreCheckEnabledWitMorePartitionsThanPreCheckThresholdOverLimit() {
        int[] partitionSizes = new int[]{ 1200, 1000, Integer.MIN_VALUE };
        populatePartitions(partitionSizes);
        initMocksWithConfiguration(200000, 2);
        limiter.precheckMaxResultLimitOnLocalPartitions(QueryResultSizeLimiterTest.ANY_MAP_NAME);
    }

    @Test
    public void testLocalPreCheckEnabledWitDifferentPartitionSizesBelowLimit() {
        int[] partitionSizes = new int[]{ 566, 1132, Integer.MAX_VALUE };
        populatePartitions(partitionSizes);
        initMocksWithConfiguration(200000, 2);
        limiter.precheckMaxResultLimitOnLocalPartitions(QueryResultSizeLimiterTest.ANY_MAP_NAME);
    }

    @Test(expected = QueryResultSizeExceededException.class)
    public void testLocalPreCheckEnabledWitDifferentPartitionSizesOverLimit() {
        int[] partitionSizes = new int[]{ 0, 2200, Integer.MIN_VALUE };
        populatePartitions(partitionSizes);
        initMocksWithConfiguration(200000, 2);
        limiter.precheckMaxResultLimitOnLocalPartitions(QueryResultSizeLimiterTest.ANY_MAP_NAME);
    }

    private static final class RecordStoreAnswer implements Answer<Integer> {
        private final Iterator<Integer> iterator;

        private Integer lastValue = null;

        private RecordStoreAnswer(Collection<Integer> partitionSizes) {
            this.iterator = partitionSizes.iterator();
        }

        @Override
        public Integer answer(InvocationOnMock invocation) throws Throwable {
            if (iterator.hasNext()) {
                lastValue = iterator.next();
            }
            return lastValue;
        }
    }
}

