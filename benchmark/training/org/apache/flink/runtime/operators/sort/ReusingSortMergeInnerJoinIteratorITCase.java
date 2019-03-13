/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.runtime.operators.sort;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.apache.flink.api.common.functions.FlatJoinFunction;
import org.apache.flink.api.common.typeutils.TypeComparator;
import org.apache.flink.api.common.typeutils.TypePairComparator;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.runtime.io.disk.iomanager.IOManager;
import org.apache.flink.runtime.jobgraph.tasks.AbstractInvokable;
import org.apache.flink.runtime.memory.MemoryManager;
import org.apache.flink.runtime.operators.testutils.DiscardingOutputCollector;
import org.apache.flink.runtime.operators.testutils.DummyInvokable;
import org.apache.flink.runtime.operators.testutils.Match;
import org.apache.flink.runtime.operators.testutils.MatchRemovingJoiner;
import org.apache.flink.runtime.operators.testutils.TestData;
import org.apache.flink.util.Collector;
import org.apache.flink.util.MutableObjectIterator;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;

import static org.apache.flink.runtime.operators.testutils.TestData.TupleGenerator.KeyMode.SORTED;
import static org.apache.flink.runtime.operators.testutils.TestData.TupleGenerator.ValueMode.RANDOM_LENGTH;


@SuppressWarnings("deprecation")
public class ReusingSortMergeInnerJoinIteratorITCase extends TestLogger {
    // total memory
    private static final int MEMORY_SIZE = (1024 * 1024) * 16;

    private static final int PAGES_FOR_BNLJN = 2;

    // the size of the left and right inputs
    private static final int INPUT_1_SIZE = 20000;

    private static final int INPUT_2_SIZE = 1000;

    // random seeds for the left and right input data generators
    private static final long SEED1 = 561349061987311L;

    private static final long SEED2 = 231434613412342L;

    // dummy abstract task
    private final AbstractInvokable parentTask = new DummyInvokable();

    private IOManager ioManager;

    private MemoryManager memoryManager;

    private TypeSerializer<Tuple2<Integer, String>> serializer1;

    private TypeSerializer<Tuple2<Integer, String>> serializer2;

    private TypeComparator<Tuple2<Integer, String>> comparator1;

    private TypeComparator<Tuple2<Integer, String>> comparator2;

    private TypePairComparator<Tuple2<Integer, String>, Tuple2<Integer, String>> pairComparator;

    @Test
    public void testMerge() {
        try {
            final TestData.TupleGenerator generator1 = new TestData.TupleGenerator(ReusingSortMergeInnerJoinIteratorITCase.SEED1, 500, 4096, SORTED, RANDOM_LENGTH);
            final TestData.TupleGenerator generator2 = new TestData.TupleGenerator(ReusingSortMergeInnerJoinIteratorITCase.SEED2, 500, 2048, SORTED, RANDOM_LENGTH);
            final TestData.TupleGeneratorIterator input1 = new TestData.TupleGeneratorIterator(generator1, ReusingSortMergeInnerJoinIteratorITCase.INPUT_1_SIZE);
            final TestData.TupleGeneratorIterator input2 = new TestData.TupleGeneratorIterator(generator2, ReusingSortMergeInnerJoinIteratorITCase.INPUT_2_SIZE);
            // collect expected data
            final Map<Integer, Collection<Match>> expectedMatchesMap = matchValues(collectData(input1), collectData(input2));
            final FlatJoinFunction<Tuple2<Integer, String>, Tuple2<Integer, String>, Tuple2<Integer, String>> joinFunction = new MatchRemovingJoiner(expectedMatchesMap);
            final Collector<Tuple2<Integer, String>> collector = new DiscardingOutputCollector<Tuple2<Integer, String>>();
            // reset the generators
            generator1.reset();
            generator2.reset();
            input1.reset();
            input2.reset();
            // compare with iterator values
            ReusingMergeInnerJoinIterator<Tuple2<Integer, String>, Tuple2<Integer, String>, Tuple2<Integer, String>> iterator = new ReusingMergeInnerJoinIterator<Tuple2<Integer, String>, Tuple2<Integer, String>, Tuple2<Integer, String>>(input1, input2, this.serializer1, this.comparator1, this.serializer2, this.comparator2, this.pairComparator, this.memoryManager, this.ioManager, ReusingSortMergeInnerJoinIteratorITCase.PAGES_FOR_BNLJN, this.parentTask);
            iterator.open();
            while (iterator.callWithNextKey(joinFunction, collector));
            iterator.close();
            // assert that each expected match was seen
            for (Map.Entry<Integer, Collection<Match>> entry : expectedMatchesMap.entrySet()) {
                Assert.assertTrue((("Collection for key " + (entry.getKey())) + " is not empty"), entry.getValue().isEmpty());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(("An exception occurred during the test: " + (e.getMessage())));
        }
    }

    @Test
    public void testMergeWithHighNumberOfCommonKeys() {
        // the size of the left and right inputs
        final int INPUT_1_SIZE = 200;
        final int INPUT_2_SIZE = 100;
        final int INPUT_1_DUPLICATES = 10;
        final int INPUT_2_DUPLICATES = 4000;
        final int DUPLICATE_KEY = 13;
        try {
            final TestData.TupleGenerator generator1 = new TestData.TupleGenerator(ReusingSortMergeInnerJoinIteratorITCase.SEED1, 500, 4096, SORTED, RANDOM_LENGTH);
            final TestData.TupleGenerator generator2 = new TestData.TupleGenerator(ReusingSortMergeInnerJoinIteratorITCase.SEED2, 500, 2048, SORTED, RANDOM_LENGTH);
            final TestData.TupleGeneratorIterator gen1Iter = new TestData.TupleGeneratorIterator(generator1, INPUT_1_SIZE);
            final TestData.TupleGeneratorIterator gen2Iter = new TestData.TupleGeneratorIterator(generator2, INPUT_2_SIZE);
            final TestData.TupleConstantValueIterator const1Iter = new TestData.TupleConstantValueIterator(DUPLICATE_KEY, "LEFT String for Duplicate Keys", INPUT_1_DUPLICATES);
            final TestData.TupleConstantValueIterator const2Iter = new TestData.TupleConstantValueIterator(DUPLICATE_KEY, "RIGHT String for Duplicate Keys", INPUT_2_DUPLICATES);
            final List<MutableObjectIterator<Tuple2<Integer, String>>> inList1 = new ArrayList<MutableObjectIterator<Tuple2<Integer, String>>>();
            inList1.add(gen1Iter);
            inList1.add(const1Iter);
            final List<MutableObjectIterator<Tuple2<Integer, String>>> inList2 = new ArrayList<MutableObjectIterator<Tuple2<Integer, String>>>();
            inList2.add(gen2Iter);
            inList2.add(const2Iter);
            MutableObjectIterator<Tuple2<Integer, String>> input1 = new MergeIterator<Tuple2<Integer, String>>(inList1, comparator1.duplicate());
            MutableObjectIterator<Tuple2<Integer, String>> input2 = new MergeIterator<Tuple2<Integer, String>>(inList2, comparator2.duplicate());
            // collect expected data
            final Map<Integer, Collection<Match>> expectedMatchesMap = matchValues(collectData(input1), collectData(input2));
            // re-create the whole thing for actual processing
            // reset the generators and iterators
            generator1.reset();
            generator2.reset();
            const1Iter.reset();
            const2Iter.reset();
            gen1Iter.reset();
            gen2Iter.reset();
            inList1.clear();
            inList1.add(gen1Iter);
            inList1.add(const1Iter);
            inList2.clear();
            inList2.add(gen2Iter);
            inList2.add(const2Iter);
            input1 = new MergeIterator<Tuple2<Integer, String>>(inList1, comparator1.duplicate());
            input2 = new MergeIterator<Tuple2<Integer, String>>(inList2, comparator2.duplicate());
            final FlatJoinFunction<Tuple2<Integer, String>, Tuple2<Integer, String>, Tuple2<Integer, String>> matcher = new MatchRemovingJoiner(expectedMatchesMap);
            final Collector<Tuple2<Integer, String>> collector = new DiscardingOutputCollector<Tuple2<Integer, String>>();
            // we create this sort-merge iterator with little memory for the block-nested-loops fall-back to make sure it
            // needs to spill for the duplicate keys
            ReusingMergeInnerJoinIterator<Tuple2<Integer, String>, Tuple2<Integer, String>, Tuple2<Integer, String>> iterator = new ReusingMergeInnerJoinIterator<Tuple2<Integer, String>, Tuple2<Integer, String>, Tuple2<Integer, String>>(input1, input2, this.serializer1, this.comparator1, this.serializer2, this.comparator2, this.pairComparator, this.memoryManager, this.ioManager, ReusingSortMergeInnerJoinIteratorITCase.PAGES_FOR_BNLJN, this.parentTask);
            iterator.open();
            while (iterator.callWithNextKey(matcher, collector));
            iterator.close();
            // assert that each expected match was seen
            for (Map.Entry<Integer, Collection<Match>> entry : expectedMatchesMap.entrySet()) {
                if (!(entry.getValue().isEmpty())) {
                    Assert.fail((("Collection for key " + (entry.getKey())) + " is not empty"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(("An exception occurred during the test: " + (e.getMessage())));
        }
    }
}

