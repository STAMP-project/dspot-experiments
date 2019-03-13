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
package org.apache.flink.runtime.operators.hash;


import org.apache.flink.api.common.typeutils.TypeComparator;
import org.apache.flink.api.common.typeutils.TypePairComparator;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.runtime.io.disk.iomanager.IOManager;
import org.apache.flink.runtime.jobgraph.tasks.AbstractInvokable;
import org.apache.flink.runtime.memory.MemoryManager;
import org.apache.flink.runtime.operators.testutils.DummyInvokable;
import org.apache.flink.runtime.operators.testutils.TestData;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;

import static org.apache.flink.runtime.operators.testutils.TestData.TupleGenerator.KeyMode.RANDOM;
import static org.apache.flink.runtime.operators.testutils.TestData.TupleGenerator.KeyMode.SORTED;
import static org.apache.flink.runtime.operators.testutils.TestData.TupleGenerator.ValueMode.FIX_LENGTH;


public abstract class ReOpenableHashTableTestBase extends TestLogger {
    protected static final int PAGE_SIZE = 8 * 1024;

    protected static final long MEMORY_SIZE = (ReOpenableHashTableTestBase.PAGE_SIZE) * 1000;// 100 Pages.


    protected static final long SEED1 = 561349061987311L;

    protected static final long SEED2 = 231434613412342L;

    protected static final int NUM_PROBES = 3;// number of reopenings of hash join


    protected final AbstractInvokable parentTask = new DummyInvokable();

    protected IOManager ioManager;

    protected MemoryManager memoryManager;

    protected TypeSerializer<Tuple2<Integer, String>> recordSerializer;

    protected TypeComparator<Tuple2<Integer, String>> record1Comparator;

    protected TypeComparator<Tuple2<Integer, String>> record2Comparator;

    protected TypePairComparator<Tuple2<Integer, String>, Tuple2<Integer, String>> recordPairComparator;

    protected TypeSerializer<Tuple2<Integer, Integer>> recordBuildSideAccesssor;

    protected TypeSerializer<Tuple2<Integer, Integer>> recordProbeSideAccesssor;

    protected TypeComparator<Tuple2<Integer, Integer>> recordBuildSideComparator;

    protected TypeComparator<Tuple2<Integer, Integer>> recordProbeSideComparator;

    protected TypePairComparator<Tuple2<Integer, Integer>, Tuple2<Integer, Integer>> pactRecordComparator;

    /**
     * Test behavior with overflow buckets (Overflow buckets must be initialized correctly
     * if the input is reopened again)
     */
    @Test
    public void testOverflow() {
        int buildSize = 1000;
        int probeSize = 1000;
        try {
            TestData.TupleGenerator bgen = new TestData.TupleGenerator(ReOpenableHashTableTestBase.SEED1, 200, 1024, RANDOM, FIX_LENGTH);
            TestData.TupleGenerator pgen = new TestData.TupleGenerator(ReOpenableHashTableTestBase.SEED2, 0, 1024, SORTED, FIX_LENGTH);
            final TestData.TupleGeneratorIterator buildInput = new TestData.TupleGeneratorIterator(bgen, buildSize);
            final TestData.TupleGeneratorIterator probeInput = new TestData.TupleGeneratorIterator(pgen, probeSize);
            doTest(buildInput, probeInput, bgen, pgen);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(("An exception occurred during the test: " + (e.getMessage())));
        }
    }

    /**
     * Verify proper operation if the build side is spilled to disk.
     */
    @Test
    public void testDoubleProbeSpilling() {
        int buildSize = 1000;
        int probeSize = 1000;
        try {
            TestData.TupleGenerator bgen = new TestData.TupleGenerator(ReOpenableHashTableTestBase.SEED1, 0, 1024, SORTED, FIX_LENGTH);
            TestData.TupleGenerator pgen = new TestData.TupleGenerator(ReOpenableHashTableTestBase.SEED2, 0, 1024, SORTED, FIX_LENGTH);
            final TestData.TupleGeneratorIterator buildInput = new TestData.TupleGeneratorIterator(bgen, buildSize);
            final TestData.TupleGeneratorIterator probeInput = new TestData.TupleGeneratorIterator(pgen, probeSize);
            doTest(buildInput, probeInput, bgen, pgen);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(("An exception occurred during the test: " + (e.getMessage())));
        }
    }

    /**
     * This test case verifies that hybrid hash join is able to handle multiple probe phases
     * when the build side fits completely into memory.
     */
    @Test
    public void testDoubleProbeInMemory() {
        int buildSize = 1000;
        int probeSize = 1000;
        try {
            TestData.TupleGenerator bgen = new TestData.TupleGenerator(ReOpenableHashTableTestBase.SEED1, 0, 28, SORTED, FIX_LENGTH);
            TestData.TupleGenerator pgen = new TestData.TupleGenerator(ReOpenableHashTableTestBase.SEED2, 0, 28, SORTED, FIX_LENGTH);
            final TestData.TupleGeneratorIterator buildInput = new TestData.TupleGeneratorIterator(bgen, buildSize);
            final TestData.TupleGeneratorIterator probeInput = new TestData.TupleGeneratorIterator(pgen, probeSize);
            doTest(buildInput, probeInput, bgen, pgen);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(("An exception occurred during the test: " + (e.getMessage())));
        }
    }
}

