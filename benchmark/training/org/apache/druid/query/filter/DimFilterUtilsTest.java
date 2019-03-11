/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.query.filter;


import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import org.apache.druid.timeline.partition.ShardSpec;
import org.easymock.EasyMock;
import org.junit.Test;


public class DimFilterUtilsTest {
    private static final Function<ShardSpec, ShardSpec> CONVERTER = new Function<ShardSpec, ShardSpec>() {
        @Nullable
        @Override
        public ShardSpec apply(@Nullable
        ShardSpec input) {
            return input;
        }
    };

    @Test
    public void testFilterShards() {
        DimFilter filter1 = EasyMock.createMock(DimFilter.class);
        EasyMock.expect(filter1.getDimensionRangeSet("dim1")).andReturn(DimFilterUtilsTest.rangeSet(ImmutableList.of(Range.lessThan("abc")))).anyTimes();
        EasyMock.expect(filter1.getDimensionRangeSet("dim2")).andReturn(null).anyTimes();
        ShardSpec shard1 = DimFilterUtilsTest.shardSpec("dim1", true);
        ShardSpec shard2 = DimFilterUtilsTest.shardSpec("dim1", false);
        ShardSpec shard3 = DimFilterUtilsTest.shardSpec("dim1", false);
        ShardSpec shard4 = DimFilterUtilsTest.shardSpec("dim2", false);
        ShardSpec shard5 = DimFilterUtilsTest.shardSpec("dim2", false);
        ShardSpec shard6 = DimFilterUtilsTest.shardSpec("dim2", false);
        ShardSpec shard7 = DimFilterUtilsTest.shardSpec("dim2", false);
        List<ShardSpec> shards = ImmutableList.of(shard1, shard2, shard3, shard4, shard5, shard6, shard7);
        EasyMock.replay(filter1, shard1, shard2, shard3, shard4, shard5, shard6, shard7);
        Set<ShardSpec> expected1 = ImmutableSet.of(shard1, shard4, shard5, shard6, shard7);
        assertFilterResult(filter1, shards, expected1);
    }
}

