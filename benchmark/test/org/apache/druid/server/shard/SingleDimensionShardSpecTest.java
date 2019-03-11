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
package org.apache.druid.server.shard;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import java.util.List;
import java.util.Map;
import org.apache.druid.data.input.InputRow;
import org.apache.druid.java.util.common.Pair;
import org.apache.druid.java.util.common.StringUtils;
import org.apache.druid.timeline.partition.ShardSpec;
import org.apache.druid.timeline.partition.SingleDimensionShardSpec;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class SingleDimensionShardSpecTest {
    @Test
    public void testIsInChunk() {
        Map<SingleDimensionShardSpec, List<Pair<Boolean, Map<String, String>>>> tests = ImmutableMap.<SingleDimensionShardSpec, List<Pair<Boolean, Map<String, String>>>>builder().put(makeSpec(null, null), makeListOfPairs(true, null, true, "a", true, "h", true, "p", true, "y")).put(makeSpec(null, "m"), makeListOfPairs(true, null, true, "a", true, "h", false, "p", false, "y")).put(makeSpec("a", "h"), makeListOfPairs(false, null, true, "a", false, "h", false, "p", false, "y")).put(makeSpec("d", "u"), makeListOfPairs(false, null, false, "a", true, "h", true, "p", false, "y")).put(makeSpec("h", null), makeListOfPairs(false, null, false, "a", true, "h", true, "p", true, "y")).build();
        for (Map.Entry<SingleDimensionShardSpec, List<Pair<Boolean, Map<String, String>>>> entry : tests.entrySet()) {
            SingleDimensionShardSpec spec = entry.getKey();
            for (Pair<Boolean, Map<String, String>> pair : entry.getValue()) {
                final InputRow inputRow = new org.apache.druid.data.input.MapBasedInputRow(0, ImmutableList.of("billy"), Maps.transformValues(pair.rhs, ( input) -> input));
                Assert.assertEquals(StringUtils.format("spec[%s], row[%s]", spec, inputRow), pair.lhs, spec.isInChunk(inputRow.getTimestampFromEpoch(), inputRow));
            }
        }
    }

    @Test
    public void testPossibleInDomain() {
        Map<String, RangeSet<String>> domain1 = ImmutableMap.of("dim1", SingleDimensionShardSpecTest.rangeSet(ImmutableList.of(Range.lessThan("abc"))));
        Map<String, RangeSet<String>> domain2 = ImmutableMap.of("dim1", SingleDimensionShardSpecTest.rangeSet(ImmutableList.of(Range.singleton("e"))), "dim2", SingleDimensionShardSpecTest.rangeSet(ImmutableList.of(Range.singleton("na"))));
        ShardSpec shard1 = makeSpec("dim1", null, "abc");
        ShardSpec shard2 = makeSpec("dim1", "abc", "def");
        ShardSpec shard3 = makeSpec("dim1", "def", null);
        ShardSpec shard4 = makeSpec("dim2", null, "hello");
        ShardSpec shard5 = makeSpec("dim2", "hello", "jk");
        ShardSpec shard6 = makeSpec("dim2", "jk", "na");
        ShardSpec shard7 = makeSpec("dim2", "na", null);
        Assert.assertTrue(shard1.possibleInDomain(domain1));
        Assert.assertFalse(shard2.possibleInDomain(domain1));
        Assert.assertFalse(shard3.possibleInDomain(domain1));
        Assert.assertTrue(shard4.possibleInDomain(domain1));
        Assert.assertTrue(shard5.possibleInDomain(domain1));
        Assert.assertTrue(shard6.possibleInDomain(domain1));
        Assert.assertTrue(shard7.possibleInDomain(domain1));
        Assert.assertFalse(shard1.possibleInDomain(domain2));
        Assert.assertFalse(shard2.possibleInDomain(domain2));
        Assert.assertTrue(shard3.possibleInDomain(domain2));
        Assert.assertFalse(shard4.possibleInDomain(domain2));
        Assert.assertFalse(shard5.possibleInDomain(domain2));
        Assert.assertTrue(shard6.possibleInDomain(domain2));
        Assert.assertTrue(shard7.possibleInDomain(domain2));
    }
}

