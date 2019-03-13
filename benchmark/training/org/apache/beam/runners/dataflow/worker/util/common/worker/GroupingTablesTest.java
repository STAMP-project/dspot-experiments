/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.runners.dataflow.worker.util.common.worker;


import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.apache.beam.runners.dataflow.worker.NameContextsForTests;
import org.apache.beam.runners.dataflow.worker.util.common.worker.GroupingTables.Combiner;
import org.apache.beam.runners.dataflow.worker.util.common.worker.GroupingTables.GroupingKeyCreator;
import org.apache.beam.runners.dataflow.worker.util.common.worker.GroupingTables.GroupingTableBase;
import org.apache.beam.runners.dataflow.worker.util.common.worker.GroupingTables.SizeEstimator;
import org.apache.beam.sdk.coders.BigEndianLongCoder;
import org.apache.beam.sdk.coders.IterableCoder;
import org.apache.beam.sdk.coders.KvCoder;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.values.KV;
import org.hamcrest.Matchers;
import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link GroupingTables}.
 */
@RunWith(JUnit4.class)
public class GroupingTablesTest {
    @Test
    public void testBufferingGroupingTable() throws Exception {
        GroupingTableBase<String, String, List<String>> table = ((GroupingTableBase<String, String, List<String>>) (GroupingTables.buffering(new GroupingTablesTest.IdentityGroupingKeyCreator(), new GroupingTablesTest.KvPairInfo(), new GroupingTablesTest.StringPowerSizeEstimator(), new GroupingTablesTest.StringPowerSizeEstimator())));
        table.setMaxSize(1000);
        TestOutputReceiver receiver = new TestOutputReceiver(KvCoder.of(StringUtf8Coder.of(), IterableCoder.of(StringUtf8Coder.of())), NameContextsForTests.nameContextForTest());
        table.put("A", "a", receiver);
        table.put("B", "b1", receiver);
        table.put("B", "b2", receiver);
        table.put("C", "c", receiver);
        Assert.assertThat(receiver.outputElems, Matchers.empty());
        table.put("C", "cccc", receiver);
        Assert.assertThat(receiver.outputElems, Matchers.hasItem(((Object) (KV.of("C", Arrays.asList("c", "cccc"))))));
        table.put("DDDD", "d", receiver);
        Assert.assertThat(receiver.outputElems, Matchers.hasItem(((Object) (KV.of("DDDD", Arrays.asList("d"))))));
        table.flush(receiver);
        Assert.assertThat(receiver.outputElems, IsIterableContainingInAnyOrder.<Object>containsInAnyOrder(KV.of("A", Arrays.asList("a")), KV.of("B", Arrays.asList("b1", "b2")), KV.of("C", Arrays.asList("c", "cccc")), KV.of("DDDD", Arrays.asList("d"))));
    }

    @Test
    public void testCombiningGroupingTable() throws Exception {
        Combiner<Object, Integer, Long, Long> summingCombineFn = new Combiner<Object, Integer, Long, Long>() {
            @Override
            public Long createAccumulator(Object key) {
                return 0L;
            }

            @Override
            public Long add(Object key, Long accumulator, Integer value) {
                return accumulator + value;
            }

            @Override
            public Long merge(Object key, Iterable<Long> accumulators) {
                long sum = 0;
                for (Long part : accumulators) {
                    sum += part;
                }
                return sum;
            }

            @Override
            public Long compact(Object key, Long accumulator) {
                return accumulator;
            }

            @Override
            public Long extract(Object key, Long accumulator) {
                return accumulator;
            }
        };
        GroupingTableBase<String, Integer, Long> table = ((GroupingTableBase<String, Integer, Long>) (GroupingTables.combining(new GroupingTablesTest.IdentityGroupingKeyCreator(), new GroupingTablesTest.KvPairInfo(), summingCombineFn, new GroupingTablesTest.StringPowerSizeEstimator(), new GroupingTablesTest.IdentitySizeEstimator())));
        table.setMaxSize(1000);
        TestOutputReceiver receiver = new TestOutputReceiver(KvCoder.of(StringUtf8Coder.of(), BigEndianLongCoder.of()), NameContextsForTests.nameContextForTest());
        table.put("A", 1, receiver);
        table.put("B", 2, receiver);
        table.put("B", 3, receiver);
        table.put("C", 4, receiver);
        Assert.assertThat(receiver.outputElems, Matchers.empty());
        table.put("C", 5000, receiver);
        Assert.assertThat(receiver.outputElems, Matchers.hasItem(((Object) (KV.of("C", 5004L)))));
        table.put("DDDD", 6, receiver);
        Assert.assertThat(receiver.outputElems, Matchers.hasItem(((Object) (KV.of("DDDD", 6L)))));
        table.flush(receiver);
        Assert.assertThat(receiver.outputElems, IsIterableContainingInAnyOrder.<Object>containsInAnyOrder(KV.of("A", 1L), KV.of("B", (2L + 3)), KV.of("C", (5000L + 4)), KV.of("DDDD", 6L)));
    }

    // //////////////////////////////////////////////////////////////////////////
    // Tests for the sampling size estimator.
    @Test
    public void testSampleFlatSizes() throws Exception {
        GroupingTablesTest.IdentitySizeEstimator underlying = new GroupingTablesTest.IdentitySizeEstimator();
        SizeEstimator<Long> estimator = new org.apache.beam.runners.dataflow.worker.util.common.worker.GroupingTables.SamplingSizeEstimator<Long>(underlying, 0.05, 1.0, 10, new Random(1));
        // First 10 elements are always sampled.
        for (int k = 0; k < 10; k++) {
            Assert.assertEquals(100, estimator.estimateSize(100L));
            Assert.assertEquals((k + 1), underlying.calls);
        }
        // Next 10 are sometimes sampled.
        for (int k = 10; k < 20; k++) {
            Assert.assertEquals(100, estimator.estimateSize(100L));
        }
        Assert.assertThat(underlying.calls, GroupingTablesTest.between(11, 19));
        int initialCalls = underlying.calls;
        // Next 1000 are sampled at about 5%.
        for (int k = 20; k < 1020; k++) {
            Assert.assertEquals(100, estimator.estimateSize(100L));
        }
        Assert.assertThat(((underlying.calls) - initialCalls), GroupingTablesTest.between(40, 60));
    }

    @Test
    public void testSampleBoringSizes() throws Exception {
        GroupingTablesTest.IdentitySizeEstimator underlying = new GroupingTablesTest.IdentitySizeEstimator();
        SizeEstimator<Long> estimator = new org.apache.beam.runners.dataflow.worker.util.common.worker.GroupingTables.SamplingSizeEstimator<Long>(underlying, 0.05, 1.0, 10, new Random(1));
        // First 10 elements are always sampled.
        for (int k = 0; k < 10; k += 2) {
            Assert.assertEquals(100, estimator.estimateSize(100L));
            Assert.assertEquals(102, estimator.estimateSize(102L));
            Assert.assertEquals((k + 2), underlying.calls);
        }
        // Next 10 are sometimes sampled.
        for (int k = 10; k < 20; k += 2) {
            Assert.assertThat(estimator.estimateSize(100L), GroupingTablesTest.between(100L, 102L));
            Assert.assertThat(estimator.estimateSize(102L), GroupingTablesTest.between(100L, 102L));
        }
        Assert.assertThat(underlying.calls, GroupingTablesTest.between(11, 19));
        int initialCalls = underlying.calls;
        // Next 1000 are sampled at about 5%.
        for (int k = 20; k < 1020; k += 2) {
            Assert.assertThat(estimator.estimateSize(100L), GroupingTablesTest.between(100L, 102L));
            Assert.assertThat(estimator.estimateSize(102L), GroupingTablesTest.between(100L, 102L));
        }
        Assert.assertThat(((underlying.calls) - initialCalls), GroupingTablesTest.between(40, 60));
    }

    @Test
    public void testSampleHighVarianceSizes() throws Exception {
        // The largest element is much larger than the average.
        List<Long> sizes = Arrays.asList(1L, 10L, 100L, 1000L);
        GroupingTablesTest.IdentitySizeEstimator underlying = new GroupingTablesTest.IdentitySizeEstimator();
        SizeEstimator<Long> estimator = new org.apache.beam.runners.dataflow.worker.util.common.worker.GroupingTables.SamplingSizeEstimator<Long>(underlying, 0.1, 0.2, 10, new Random(1));
        // First 10 elements are always sampled.
        for (int k = 0; k < 10; k++) {
            long size = sizes.get((k % (sizes.size())));
            Assert.assertEquals(size, estimator.estimateSize(size));
            Assert.assertEquals((k + 1), underlying.calls);
        }
        // We're still not out of the woods; sample every element.
        for (int k = 10; k < 20; k++) {
            long size = sizes.get((k % (sizes.size())));
            Assert.assertEquals(size, estimator.estimateSize(size));
            Assert.assertEquals((k + 1), underlying.calls);
        }
        // Sample some more to let things settle down.
        for (int k = 20; k < 500; k++) {
            estimator.estimateSize(sizes.get((k % (sizes.size()))));
        }
        // Next 1000 are sampled at about 20% (maxSampleRate).
        int initialCalls = underlying.calls;
        for (int k = 500; k < 1500; k++) {
            long size = sizes.get((k % (sizes.size())));
            Assert.assertThat(estimator.estimateSize(size), Matchers.anyOf(Matchers.isIn(sizes), GroupingTablesTest.between(250L, 350L)));
        }
        Assert.assertThat(((underlying.calls) - initialCalls), GroupingTablesTest.between(180, 220));
        // Sample some more to let things settle down.
        for (int k = 1500; k < 3000; k++) {
            estimator.estimateSize(sizes.get((k % (sizes.size()))));
        }
        // Next 1000 are sampled at about 10% (minSampleRate).
        initialCalls = underlying.calls;
        for (int k = 3000; k < 4000; k++) {
            long size = sizes.get((k % (sizes.size())));
            Assert.assertThat(estimator.estimateSize(size), Matchers.anyOf(Matchers.isIn(sizes), GroupingTablesTest.between(250L, 350L)));
        }
        Assert.assertThat(((underlying.calls) - initialCalls), GroupingTablesTest.between(90, 110));
    }

    @Test
    public void testSampleChangingSizes() throws Exception {
        GroupingTablesTest.IdentitySizeEstimator underlying = new GroupingTablesTest.IdentitySizeEstimator();
        SizeEstimator<Long> estimator = new org.apache.beam.runners.dataflow.worker.util.common.worker.GroupingTables.SamplingSizeEstimator<Long>(underlying, 0.05, 1.0, 10, new Random(1));
        // First 10 elements are always sampled.
        for (int k = 0; k < 10; k++) {
            Assert.assertEquals(100, estimator.estimateSize(100L));
            Assert.assertEquals((k + 1), underlying.calls);
        }
        // Next 10 are sometimes sampled.
        for (int k = 10; k < 20; k++) {
            Assert.assertEquals(100, estimator.estimateSize(100L));
        }
        Assert.assertThat(underlying.calls, GroupingTablesTest.between(11, 19));
        int initialCalls = underlying.calls;
        // Next 1000 are sampled at about 5%.
        for (int k = 20; k < 1020; k++) {
            Assert.assertEquals(100, estimator.estimateSize(100L));
        }
        Assert.assertThat(((underlying.calls) - initialCalls), GroupingTablesTest.between(40, 60));
        // Inject a big element until it is sampled.
        while ((estimator.estimateSize(1000000L)) == 100) {
        } 
        // Check that we have started sampling more regularly again.
        Assert.assertEquals(99, estimator.estimateSize(99L));
    }

    /**
     * Return the key as its grouping key.
     */
    private static class IdentityGroupingKeyCreator implements GroupingKeyCreator<Object> {
        @Override
        public Object createGroupingKey(Object key) {
            return key;
        }
    }

    /**
     * "Estimate" the size of longs by looking at their value.
     */
    private static class IdentitySizeEstimator implements SizeEstimator<Long> {
        public int calls = 0;

        @Override
        public long estimateSize(Long element) {
            (calls)++;
            return element;
        }
    }

    /**
     * "Estimate" the size of strings by taking the tenth power of their length.
     */
    private static class StringPowerSizeEstimator implements SizeEstimator<String> {
        @Override
        public long estimateSize(String element) {
            return ((long) (Math.pow(10, element.length())));
        }
    }

    private static class KvPairInfo implements GroupingTables.PairInfo {
        @SuppressWarnings("unchecked")
        @Override
        public Object getKeyFromInputPair(Object pair) {
            return ((KV<Object, ?>) (pair)).getKey();
        }

        @Override
        public Object getValueFromInputPair(Object pair) {
            return ((KV<?, ?>) (pair)).getValue();
        }

        @Override
        public Object makeOutputPair(Object key, Object value) {
            return KV.of(key, value);
        }
    }
}

