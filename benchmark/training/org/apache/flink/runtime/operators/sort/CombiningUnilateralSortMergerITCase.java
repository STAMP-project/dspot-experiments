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
package org.apache.flink.runtime.operators.sort;


import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import org.apache.flink.api.common.functions.GroupCombineFunction;
import org.apache.flink.api.common.functions.RichGroupReduceFunction;
import org.apache.flink.api.common.typeutils.TypeComparator;
import org.apache.flink.api.common.typeutils.TypeSerializerFactory;
import org.apache.flink.api.common.typeutils.base.IntComparator;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.io.disk.iomanager.IOManager;
import org.apache.flink.runtime.jobgraph.tasks.AbstractInvokable;
import org.apache.flink.runtime.memory.MemoryManager;
import org.apache.flink.runtime.operators.testutils.DummyInvokable;
import org.apache.flink.runtime.operators.testutils.TestData;
import org.apache.flink.util.Collector;
import org.apache.flink.util.MutableObjectIterator;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.flink.runtime.operators.testutils.TestData.TupleGenerator.KeyMode.RANDOM;
import static org.apache.flink.runtime.operators.testutils.TestData.TupleGenerator.ValueMode.FIX_LENGTH;


public class CombiningUnilateralSortMergerITCase extends TestLogger {
    private static final Logger LOG = LoggerFactory.getLogger(CombiningUnilateralSortMergerITCase.class);

    private static final long SEED = 649180756312423613L;

    private static final int KEY_MAX = 1000;

    private static final int VALUE_LENGTH = 118;

    private static final int NUM_PAIRS = 50000;

    public static final int MEMORY_SIZE = (1024 * 1024) * 256;

    private final AbstractInvokable parentTask = new DummyInvokable();

    private IOManager ioManager;

    private MemoryManager memoryManager;

    private TypeSerializerFactory<Tuple2<Integer, String>> serializerFactory1;

    private TypeSerializerFactory<Tuple2<Integer, Integer>> serializerFactory2;

    private TypeComparator<Tuple2<Integer, String>> comparator1;

    private TypeComparator<Tuple2<Integer, Integer>> comparator2;

    @Test
    public void testCombine() throws Exception {
        int noKeys = 100;
        int noKeyCnt = 10000;
        TestData.MockTuple2Reader<Tuple2<Integer, Integer>> reader = TestData.getIntIntTupleReader();
        CombiningUnilateralSortMergerITCase.LOG.debug("initializing sortmerger");
        CombiningUnilateralSortMergerITCase.TestCountCombiner comb = new CombiningUnilateralSortMergerITCase.TestCountCombiner();
        Sorter<Tuple2<Integer, Integer>> merger = /* use large record handler */
        new CombiningUnilateralSortMerger(comb, this.memoryManager, this.ioManager, reader, this.parentTask, this.serializerFactory2, this.comparator2, 0.25, 64, 0.7F, true, false);
        final Tuple2<Integer, Integer> rec = new Tuple2();
        rec.setField(1, 1);
        for (int i = 0; i < noKeyCnt; i++) {
            for (int j = 0; j < noKeys; j++) {
                rec.setField(j, 0);
                reader.emit(rec);
            }
        }
        reader.close();
        MutableObjectIterator<Tuple2<Integer, Integer>> iterator = merger.getIterator();
        Iterator<Integer> result = CombiningUnilateralSortMergerITCase.getReducingIterator(iterator, serializerFactory2.getSerializer(), comparator2.duplicate());
        while (result.hasNext()) {
            Assert.assertEquals(noKeyCnt, result.next().intValue());
        } 
        merger.close();
        // if the combiner was opened, it must have been closed
        Assert.assertTrue(((comb.opened) == (comb.closed)));
    }

    @Test
    public void testCombineSpilling() throws Exception {
        int noKeys = 100;
        int noKeyCnt = 10000;
        TestData.MockTuple2Reader<Tuple2<Integer, Integer>> reader = TestData.getIntIntTupleReader();
        CombiningUnilateralSortMergerITCase.LOG.debug("initializing sortmerger");
        CombiningUnilateralSortMergerITCase.TestCountCombiner comb = new CombiningUnilateralSortMergerITCase.TestCountCombiner();
        Sorter<Tuple2<Integer, Integer>> merger = /* use large record handler */
        new CombiningUnilateralSortMerger(comb, this.memoryManager, this.ioManager, reader, this.parentTask, this.serializerFactory2, this.comparator2, 0.01, 64, 0.005F, true, true);
        final Tuple2<Integer, Integer> rec = new Tuple2();
        rec.setField(1, 1);
        for (int i = 0; i < noKeyCnt; i++) {
            for (int j = 0; j < noKeys; j++) {
                rec.setField(j, 0);
                reader.emit(rec);
            }
        }
        reader.close();
        MutableObjectIterator<Tuple2<Integer, Integer>> iterator = merger.getIterator();
        Iterator<Integer> result = CombiningUnilateralSortMergerITCase.getReducingIterator(iterator, serializerFactory2.getSerializer(), comparator2.duplicate());
        while (result.hasNext()) {
            Assert.assertEquals(noKeyCnt, result.next().intValue());
        } 
        merger.close();
        // if the combiner was opened, it must have been closed
        Assert.assertTrue(((comb.opened) == (comb.closed)));
    }

    @Test
    public void testCombineSpillingDisableObjectReuse() throws Exception {
        int noKeys = 100;
        int noKeyCnt = 10000;
        TestData.MockTuple2Reader<Tuple2<Integer, Integer>> reader = TestData.getIntIntTupleReader();
        CombiningUnilateralSortMergerITCase.LOG.debug("initializing sortmerger");
        CombiningUnilateralSortMergerITCase.MaterializedCountCombiner comb = new CombiningUnilateralSortMergerITCase.MaterializedCountCombiner();
        // set maxNumFileHandles = 2 to trigger multiple channel merging
        Sorter<Tuple2<Integer, Integer>> merger = /* use large record handler */
        new CombiningUnilateralSortMerger(comb, this.memoryManager, this.ioManager, reader, this.parentTask, this.serializerFactory2, this.comparator2, 0.01, 2, 0.005F, true, false);
        final Tuple2<Integer, Integer> rec = new Tuple2();
        for (int i = 0; i < noKeyCnt; i++) {
            rec.setField(i, 0);
            for (int j = 0; j < noKeys; j++) {
                rec.setField(j, 1);
                reader.emit(rec);
            }
        }
        reader.close();
        MutableObjectIterator<Tuple2<Integer, Integer>> iterator = merger.getIterator();
        Iterator<Integer> result = CombiningUnilateralSortMergerITCase.getReducingIterator(iterator, serializerFactory2.getSerializer(), comparator2.duplicate());
        while (result.hasNext()) {
            Assert.assertEquals(4950, result.next().intValue());
        } 
        merger.close();
    }

    @Test
    public void testSortAndValidate() throws Exception {
        final Hashtable<Integer, Integer> countTable = new Hashtable<>(CombiningUnilateralSortMergerITCase.KEY_MAX);
        for (int i = 1; i <= (CombiningUnilateralSortMergerITCase.KEY_MAX); i++) {
            countTable.put(i, 0);
        }
        // comparator
        final TypeComparator<Integer> keyComparator = new IntComparator(true);
        // reader
        TestData.MockTuple2Reader<Tuple2<Integer, String>> reader = TestData.getIntStringTupleReader();
        // merge iterator
        CombiningUnilateralSortMergerITCase.LOG.debug("initializing sortmerger");
        CombiningUnilateralSortMergerITCase.TestCountCombiner2 comb = new CombiningUnilateralSortMergerITCase.TestCountCombiner2();
        Sorter<Tuple2<Integer, String>> merger = /* use large record handler */
        new CombiningUnilateralSortMerger(comb, this.memoryManager, this.ioManager, reader, this.parentTask, this.serializerFactory1, this.comparator1, 0.25, 2, 0.7F, true, false);
        // emit data
        CombiningUnilateralSortMergerITCase.LOG.debug("emitting data");
        TestData.TupleGenerator generator = new TestData.TupleGenerator(CombiningUnilateralSortMergerITCase.SEED, CombiningUnilateralSortMergerITCase.KEY_MAX, CombiningUnilateralSortMergerITCase.VALUE_LENGTH, RANDOM, FIX_LENGTH);
        Tuple2<Integer, String> rec = new Tuple2();
        for (int i = 0; i < (CombiningUnilateralSortMergerITCase.NUM_PAIRS); i++) {
            Assert.assertTrue(((rec = generator.next(rec)) != null));
            final Integer key = rec.f0;
            rec.setField("1", 1);
            reader.emit(rec);
            countTable.put(key, ((countTable.get(key)) + 1));
        }
        reader.close();
        // check order
        MutableObjectIterator<Tuple2<Integer, String>> iterator = merger.getIterator();
        CombiningUnilateralSortMergerITCase.LOG.debug("checking results");
        Tuple2<Integer, String> rec1 = new Tuple2();
        Tuple2<Integer, String> rec2 = new Tuple2();
        Assert.assertTrue(((rec1 = iterator.next(rec1)) != null));
        countTable.put(rec1.f0, ((countTable.get(rec1.f0)) - (Integer.parseInt(rec1.f1))));
        while ((rec2 = iterator.next(rec2)) != null) {
            int k1 = rec1.f0;
            int k2 = rec2.f0;
            Assert.assertTrue(((keyComparator.compare(k1, k2)) <= 0));
            countTable.put(k2, ((countTable.get(k2)) - (Integer.parseInt(rec2.f1))));
            rec1 = rec2;
        } 
        for (Integer cnt : countTable.values()) {
            Assert.assertTrue((cnt == 0));
        }
        merger.close();
        // if the combiner was opened, it must have been closed
        Assert.assertTrue(((comb.opened) == (comb.closed)));
    }

    // --------------------------------------------------------------------------------------------
    public static class TestCountCombiner extends RichGroupReduceFunction<Tuple2<Integer, Integer>, Tuple2<Integer, Integer>> implements GroupCombineFunction<Tuple2<Integer, Integer>, Tuple2<Integer, Integer>> {
        private static final long serialVersionUID = 1L;

        private Integer count = 0;

        public volatile boolean opened = false;

        public volatile boolean closed = false;

        @Override
        public void combine(Iterable<Tuple2<Integer, Integer>> values, Collector<Tuple2<Integer, Integer>> out) {
            Tuple2<Integer, Integer> rec = new Tuple2();
            int cnt = 0;
            for (Tuple2<Integer, Integer> next : values) {
                rec = next;
                cnt += rec.f1;
            }
            this.count = cnt;
            rec.setField(this.count, 1);
            out.collect(rec);
        }

        @Override
        public void reduce(Iterable<Tuple2<Integer, Integer>> values, Collector<Tuple2<Integer, Integer>> out) {
        }

        @Override
        public void open(Configuration parameters) throws Exception {
            opened = true;
        }

        @Override
        public void close() throws Exception {
            closed = true;
        }
    }

    public static class TestCountCombiner2 extends RichGroupReduceFunction<Tuple2<Integer, String>, Tuple2<Integer, String>> implements GroupCombineFunction<Tuple2<Integer, String>, Tuple2<Integer, String>> {
        private static final long serialVersionUID = 1L;

        public volatile boolean opened = false;

        public volatile boolean closed = false;

        @Override
        public void combine(Iterable<Tuple2<Integer, String>> values, Collector<Tuple2<Integer, String>> out) {
            Tuple2<Integer, String> rec = new Tuple2();
            int cnt = 0;
            for (Tuple2<Integer, String> next : values) {
                rec = next;
                cnt += Integer.parseInt(rec.f1);
            }
            out.collect(new Tuple2(rec.f0, (cnt + "")));
        }

        @Override
        public void reduce(Iterable<Tuple2<Integer, String>> values, Collector<Tuple2<Integer, String>> out) {
            // yo, nothing, mon
        }

        @Override
        public void open(Configuration parameters) throws Exception {
            opened = true;
        }

        @Override
        public void close() throws Exception {
            closed = true;
        }
    }

    // --------------------------------------------------------------------------------------------
    public static class MaterializedCountCombiner extends RichGroupReduceFunction<Tuple2<Integer, Integer>, Tuple2<Integer, Integer>> implements GroupCombineFunction<Tuple2<Integer, Integer>, Tuple2<Integer, Integer>> {
        private static final long serialVersionUID = 1L;

        @Override
        public void combine(Iterable<Tuple2<Integer, Integer>> values, Collector<Tuple2<Integer, Integer>> out) {
            ArrayList<Tuple2<Integer, Integer>> valueList = new ArrayList<>();
            for (Tuple2<Integer, Integer> next : values) {
                valueList.add(next);
            }
            int count = 0;
            Tuple2<Integer, Integer> rec = new Tuple2();
            for (Tuple2<Integer, Integer> tuple : valueList) {
                rec.setField(tuple.f0, 0);
                count += tuple.f1;
            }
            rec.setField(count, 1);
            out.collect(rec);
        }

        @Override
        public void reduce(Iterable<Tuple2<Integer, Integer>> values, Collector<Tuple2<Integer, Integer>> out) throws Exception {
        }
    }
}

