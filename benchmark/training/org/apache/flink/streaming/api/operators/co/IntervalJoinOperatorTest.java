/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.streaming.api.operators.co;


import java.util.List;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.runtime.checkpoint.OperatorSubtaskState;
import org.apache.flink.shaded.guava18.com.google.common.collect.Lists;
import org.apache.flink.streaming.api.operators.TwoInputStreamOperator;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.apache.flink.streaming.runtime.streamrecord.StreamRecord;
import org.apache.flink.streaming.util.KeyedTwoInputStreamOperatorTestHarness;
import org.apache.flink.streaming.util.TestHarnessUtil;
import org.apache.flink.util.Collector;
import org.apache.flink.util.FlinkException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.apache.flink.streaming.api.functions.co.ProcessJoinFunction.<init>;


/**
 * Tests for {@link IntervalJoinOperator}.
 * Those tests cover correctness and cleaning of state
 */
@RunWith(Parameterized.class)
public class IntervalJoinOperatorTest {
    private final boolean lhsFasterThanRhs;

    public IntervalJoinOperatorTest(boolean lhsFasterThanRhs) {
        this.lhsFasterThanRhs = lhsFasterThanRhs;
    }

    @Test
    public void testImplementationMirrorsCorrectly() throws Exception {
        long lowerBound = 1;
        long upperBound = 3;
        boolean lowerBoundInclusive = true;
        boolean upperBoundInclusive = false;
        setupHarness(lowerBound, lowerBoundInclusive, upperBound, upperBoundInclusive).processElementsAndWatermarks(1, 4).andExpect(streamRecordOf(1, 2), streamRecordOf(1, 3), streamRecordOf(2, 3), streamRecordOf(2, 4), streamRecordOf(3, 4)).noLateRecords().close();
        setupHarness(((-1) * upperBound), upperBoundInclusive, ((-1) * lowerBound), lowerBoundInclusive).processElementsAndWatermarks(1, 4).andExpect(streamRecordOf(2, 1), streamRecordOf(3, 1), streamRecordOf(3, 2), streamRecordOf(4, 2), streamRecordOf(4, 3)).noLateRecords().close();
    }

    // lhs - 2 <= rhs <= rhs + 2
    @Test
    public void testNegativeInclusiveAndNegativeInclusive() throws Exception {
        setupHarness((-2), true, (-1), true).processElementsAndWatermarks(1, 4).andExpect(streamRecordOf(2, 1), streamRecordOf(3, 1), streamRecordOf(3, 2), streamRecordOf(4, 2), streamRecordOf(4, 3)).noLateRecords().close();
    }

    // lhs - 1 <= rhs <= rhs + 1
    @Test
    public void testNegativeInclusiveAndPositiveInclusive() throws Exception {
        setupHarness((-1), true, 1, true).processElementsAndWatermarks(1, 4).andExpect(streamRecordOf(1, 1), streamRecordOf(1, 2), streamRecordOf(2, 1), streamRecordOf(2, 2), streamRecordOf(2, 3), streamRecordOf(3, 2), streamRecordOf(3, 3), streamRecordOf(3, 4), streamRecordOf(4, 3), streamRecordOf(4, 4)).noLateRecords().close();
    }

    // lhs + 1 <= rhs <= lhs + 2
    @Test
    public void testPositiveInclusiveAndPositiveInclusive() throws Exception {
        setupHarness(1, true, 2, true).processElementsAndWatermarks(1, 4).andExpect(streamRecordOf(1, 2), streamRecordOf(1, 3), streamRecordOf(2, 3), streamRecordOf(2, 4), streamRecordOf(3, 4)).noLateRecords().close();
    }

    @Test
    public void testNegativeExclusiveAndNegativeExlusive() throws Exception {
        setupHarness((-3), false, (-1), false).processElementsAndWatermarks(1, 4).andExpect(streamRecordOf(3, 1), streamRecordOf(4, 2)).noLateRecords().close();
    }

    @Test
    public void testNegativeExclusiveAndPositiveExlusive() throws Exception {
        setupHarness((-1), false, 1, false).processElementsAndWatermarks(1, 4).andExpect(streamRecordOf(1, 1), streamRecordOf(2, 2), streamRecordOf(3, 3), streamRecordOf(4, 4)).noLateRecords().close();
    }

    @Test
    public void testPositiveExclusiveAndPositiveExlusive() throws Exception {
        setupHarness(1, false, 3, false).processElementsAndWatermarks(1, 4).andExpect(streamRecordOf(1, 3), streamRecordOf(2, 4)).noLateRecords().close();
    }

    @Test
    public void testStateCleanupNegativeInclusiveNegativeInclusive() throws Exception {
        // set common watermark to 6 and check that data all buffers are empty
        // set common watermark to 4 and check that data is cleaned
        // set common watermark to 1 and check that data is cleaned
        // fill both buffers with values
        setupHarness((-1), true, 0, true).processElement1(1).processElement1(2).processElement1(3).processElement1(4).processElement1(5).processElement2(1).processElement2(2).processElement2(3).processElement2(4).processElement2(5).processWatermark1(1).processWatermark2(1).assertLeftBufferContainsOnly(2, 3, 4, 5).assertRightBufferContainsOnly(1, 2, 3, 4, 5).processWatermark1(4).processWatermark2(4).assertLeftBufferContainsOnly(5).assertRightBufferContainsOnly(4, 5).processWatermark1(6).processWatermark2(6).assertLeftBufferEmpty().assertRightBufferEmpty().close();
    }

    @Test
    public void testStateCleanupNegativePositiveNegativeExlusive() throws Exception {
        // set common watermark to 6 and check that data all buffers are empty
        // set common watermark to 4 and check that data is cleaned
        // set common watermark to 1 and check that data is cleaned
        // fill both buffers with values
        setupHarness((-2), false, 1, false).processElement1(1).processElement1(2).processElement1(3).processElement1(4).processElement1(5).processElement2(1).processElement2(2).processElement2(3).processElement2(4).processElement2(5).processWatermark1(1).processWatermark2(1).assertLeftBufferContainsOnly(2, 3, 4, 5).assertRightBufferContainsOnly(1, 2, 3, 4, 5).processWatermark1(4).processWatermark2(4).assertLeftBufferContainsOnly(5).assertRightBufferContainsOnly(4, 5).processWatermark1(6).processWatermark2(6).assertLeftBufferEmpty().assertRightBufferEmpty().close();
    }

    @Test
    public void testStateCleanupPositiveInclusivePositiveInclusive() throws Exception {
        // set common watermark to 6 and check that data all buffers are empty
        // set common watermark to 4 and check that data is cleaned
        // set common watermark to 1 and check that data is cleaned
        // fill both buffers with values
        setupHarness(0, true, 1, true).processElement1(1).processElement1(2).processElement1(3).processElement1(4).processElement1(5).processElement2(1).processElement2(2).processElement2(3).processElement2(4).processElement2(5).processWatermark1(1).processWatermark2(1).assertLeftBufferContainsOnly(1, 2, 3, 4, 5).assertRightBufferContainsOnly(2, 3, 4, 5).processWatermark1(4).processWatermark2(4).assertLeftBufferContainsOnly(4, 5).assertRightBufferContainsOnly(5).processWatermark1(6).processWatermark2(6).assertLeftBufferEmpty().assertRightBufferEmpty().close();
    }

    @Test
    public void testStateCleanupPositiveExlusivePositiveExclusive() throws Exception {
        // set common watermark to 6 and check that data all buffers are empty
        // set common watermark to 4 and check that data is cleaned
        // set common watermark to 1 and check that data is cleaned
        // fill both buffers with values
        setupHarness((-1), false, 2, false).processElement1(1).processElement1(2).processElement1(3).processElement1(4).processElement1(5).processElement2(1).processElement2(2).processElement2(3).processElement2(4).processElement2(5).processWatermark1(1).processWatermark2(1).assertLeftBufferContainsOnly(1, 2, 3, 4, 5).assertRightBufferContainsOnly(2, 3, 4, 5).processWatermark1(4).processWatermark2(4).assertLeftBufferContainsOnly(4, 5).assertRightBufferContainsOnly(5).processWatermark1(6).processWatermark2(6).assertLeftBufferEmpty().assertRightBufferEmpty().close();
    }

    @Test
    public void testRestoreFromSnapshot() throws Exception {
        // config
        int lowerBound = -1;
        boolean lowerBoundInclusive = true;
        int upperBound = 1;
        boolean upperBoundInclusive = true;
        // create first test harness
        OperatorSubtaskState handles;
        List<StreamRecord<Tuple2<IntervalJoinOperatorTest.TestElem, IntervalJoinOperatorTest.TestElem>>> expectedOutput;
        try (IntervalJoinOperatorTest.TestHarness testHarness = createTestHarness(lowerBound, lowerBoundInclusive, upperBound, upperBoundInclusive)) {
            testHarness.setup();
            testHarness.open();
            // process elements with first test harness
            testHarness.processElement1(IntervalJoinOperatorTest.createStreamRecord(1, "lhs"));
            testHarness.processWatermark1(new Watermark(1));
            testHarness.processElement2(IntervalJoinOperatorTest.createStreamRecord(1, "rhs"));
            testHarness.processWatermark2(new Watermark(1));
            testHarness.processElement1(IntervalJoinOperatorTest.createStreamRecord(2, "lhs"));
            testHarness.processWatermark1(new Watermark(2));
            testHarness.processElement2(IntervalJoinOperatorTest.createStreamRecord(2, "rhs"));
            testHarness.processWatermark2(new Watermark(2));
            testHarness.processElement1(IntervalJoinOperatorTest.createStreamRecord(3, "lhs"));
            testHarness.processWatermark1(new Watermark(3));
            testHarness.processElement2(IntervalJoinOperatorTest.createStreamRecord(3, "rhs"));
            testHarness.processWatermark2(new Watermark(3));
            // snapshot and validate output
            handles = testHarness.snapshot(0, 0);
            testHarness.close();
            expectedOutput = Lists.newArrayList(streamRecordOf(1, 1), streamRecordOf(1, 2), streamRecordOf(2, 1), streamRecordOf(2, 2), streamRecordOf(2, 3), streamRecordOf(3, 2), streamRecordOf(3, 3));
            TestHarnessUtil.assertNoLateRecords(testHarness.getOutput());
            assertOutput(expectedOutput, testHarness.getOutput());
        }
        try (IntervalJoinOperatorTest.TestHarness newTestHarness = createTestHarness(lowerBound, lowerBoundInclusive, upperBound, upperBoundInclusive)) {
            // create new test harness from snapshpt
            newTestHarness.setup();
            newTestHarness.initializeState(handles);
            newTestHarness.open();
            // process elements
            newTestHarness.processElement1(IntervalJoinOperatorTest.createStreamRecord(4, "lhs"));
            newTestHarness.processWatermark1(new Watermark(4));
            newTestHarness.processElement2(IntervalJoinOperatorTest.createStreamRecord(4, "rhs"));
            newTestHarness.processWatermark2(new Watermark(4));
            // assert expected output
            expectedOutput = Lists.newArrayList(streamRecordOf(3, 4), streamRecordOf(4, 3), streamRecordOf(4, 4));
            TestHarnessUtil.assertNoLateRecords(newTestHarness.getOutput());
            assertOutput(expectedOutput, newTestHarness.getOutput());
        }
    }

    @Test
    public void testContextCorrectLeftTimestamp() throws Exception {
        IntervalJoinOperator<String, IntervalJoinOperatorTest.TestElem, IntervalJoinOperatorTest.TestElem, Tuple2<IntervalJoinOperatorTest.TestElem, IntervalJoinOperatorTest.TestElem>> op = new IntervalJoinOperator((-1), 1, true, true, IntervalJoinOperatorTest.TestElem.serializer(), IntervalJoinOperatorTest.TestElem.serializer(), new org.apache.flink.streaming.api.functions.co.ProcessJoinFunction<IntervalJoinOperatorTest.TestElem, IntervalJoinOperatorTest.TestElem, Tuple2<IntervalJoinOperatorTest.TestElem, IntervalJoinOperatorTest.TestElem>>() {
            @Override
            public void processElement(IntervalJoinOperatorTest.TestElem left, IntervalJoinOperatorTest.TestElem right, Context ctx, Collector<Tuple2<IntervalJoinOperatorTest.TestElem, IntervalJoinOperatorTest.TestElem>> out) throws Exception {
                Assert.assertEquals(left.ts, ctx.getLeftTimestamp());
            }
        });
        try (IntervalJoinOperatorTest.TestHarness testHarness = new IntervalJoinOperatorTest.TestHarness(op, ( elem) -> elem.key, ( elem) -> elem.key, TypeInformation.of(String.class))) {
            testHarness.setup();
            testHarness.open();
            processElementsAndWatermarks(testHarness);
        }
    }

    @Test
    public void testReturnsCorrectTimestamp() throws Exception {
        IntervalJoinOperator<String, IntervalJoinOperatorTest.TestElem, IntervalJoinOperatorTest.TestElem, Tuple2<IntervalJoinOperatorTest.TestElem, IntervalJoinOperatorTest.TestElem>> op = new IntervalJoinOperator((-1), 1, true, true, IntervalJoinOperatorTest.TestElem.serializer(), IntervalJoinOperatorTest.TestElem.serializer(), new org.apache.flink.streaming.api.functions.co.ProcessJoinFunction<IntervalJoinOperatorTest.TestElem, IntervalJoinOperatorTest.TestElem, Tuple2<IntervalJoinOperatorTest.TestElem, IntervalJoinOperatorTest.TestElem>>() {
            private static final long serialVersionUID = 1L;

            @Override
            public void processElement(IntervalJoinOperatorTest.TestElem left, IntervalJoinOperatorTest.TestElem right, Context ctx, Collector<Tuple2<IntervalJoinOperatorTest.TestElem, IntervalJoinOperatorTest.TestElem>> out) throws Exception {
                Assert.assertEquals(Math.max(left.ts, right.ts), ctx.getTimestamp());
            }
        });
        try (IntervalJoinOperatorTest.TestHarness testHarness = new IntervalJoinOperatorTest.TestHarness(op, ( elem) -> elem.key, ( elem) -> elem.key, TypeInformation.of(String.class))) {
            testHarness.setup();
            testHarness.open();
            processElementsAndWatermarks(testHarness);
        }
    }

    @Test
    public void testContextCorrectRightTimestamp() throws Exception {
        IntervalJoinOperator<String, IntervalJoinOperatorTest.TestElem, IntervalJoinOperatorTest.TestElem, Tuple2<IntervalJoinOperatorTest.TestElem, IntervalJoinOperatorTest.TestElem>> op = new IntervalJoinOperator((-1), 1, true, true, IntervalJoinOperatorTest.TestElem.serializer(), IntervalJoinOperatorTest.TestElem.serializer(), new org.apache.flink.streaming.api.functions.co.ProcessJoinFunction<IntervalJoinOperatorTest.TestElem, IntervalJoinOperatorTest.TestElem, Tuple2<IntervalJoinOperatorTest.TestElem, IntervalJoinOperatorTest.TestElem>>() {
            @Override
            public void processElement(IntervalJoinOperatorTest.TestElem left, IntervalJoinOperatorTest.TestElem right, Context ctx, Collector<Tuple2<IntervalJoinOperatorTest.TestElem, IntervalJoinOperatorTest.TestElem>> out) throws Exception {
                Assert.assertEquals(right.ts, ctx.getRightTimestamp());
            }
        });
        try (IntervalJoinOperatorTest.TestHarness testHarness = new IntervalJoinOperatorTest.TestHarness(op, ( elem) -> elem.key, ( elem) -> elem.key, TypeInformation.of(String.class))) {
            testHarness.setup();
            testHarness.open();
            processElementsAndWatermarks(testHarness);
        }
    }

    @Test(expected = FlinkException.class)
    public void testFailsWithNoTimestampsLeft() throws Exception {
        IntervalJoinOperatorTest.TestHarness newTestHarness = createTestHarness(0L, true, 0L, true);
        newTestHarness.setup();
        newTestHarness.open();
        // note that the StreamRecord has no timestamp in constructor
        newTestHarness.processElement1(new StreamRecord(new IntervalJoinOperatorTest.TestElem(0, "lhs")));
    }

    @Test(expected = FlinkException.class)
    public void testFailsWithNoTimestampsRight() throws Exception {
        try (IntervalJoinOperatorTest.TestHarness newTestHarness = createTestHarness(0L, true, 0L, true)) {
            newTestHarness.setup();
            newTestHarness.open();
            // note that the StreamRecord has no timestamp in constructor
            newTestHarness.processElement2(new StreamRecord(new IntervalJoinOperatorTest.TestElem(0, "rhs")));
        }
    }

    @Test
    public void testDiscardsLateData() throws Exception {
        // this element is late and should not be joined again
        setupHarness((-1), true, 1, true).processElement1(1).processElement2(1).processElement1(2).processElement2(2).processElement1(3).processElement2(3).processWatermark1(3).processWatermark2(3).processElement1(1).processElement1(4).processElement2(4).processElement1(5).processElement2(5).andExpect(streamRecordOf(1, 1), streamRecordOf(1, 2), streamRecordOf(2, 1), streamRecordOf(2, 2), streamRecordOf(2, 3), streamRecordOf(3, 2), streamRecordOf(3, 3), streamRecordOf(3, 4), streamRecordOf(4, 3), streamRecordOf(4, 4), streamRecordOf(4, 5), streamRecordOf(5, 4), streamRecordOf(5, 5)).noLateRecords().close();
    }

    private class JoinTestBuilder {
        private IntervalJoinOperator<String, IntervalJoinOperatorTest.TestElem, IntervalJoinOperatorTest.TestElem, Tuple2<IntervalJoinOperatorTest.TestElem, IntervalJoinOperatorTest.TestElem>> operator;

        private IntervalJoinOperatorTest.TestHarness testHarness;

        public JoinTestBuilder(IntervalJoinOperatorTest.TestHarness t, IntervalJoinOperator<String, IntervalJoinOperatorTest.TestElem, IntervalJoinOperatorTest.TestElem, Tuple2<IntervalJoinOperatorTest.TestElem, IntervalJoinOperatorTest.TestElem>> operator) throws Exception {
            this.testHarness = t;
            this.operator = operator;
            t.open();
            t.setup();
        }

        public IntervalJoinOperatorTest.TestHarness get() {
            return testHarness;
        }

        public IntervalJoinOperatorTest.JoinTestBuilder processElement1(int ts) throws Exception {
            testHarness.processElement1(IntervalJoinOperatorTest.createStreamRecord(ts, "lhs"));
            return this;
        }

        public IntervalJoinOperatorTest.JoinTestBuilder processElement2(int ts) throws Exception {
            testHarness.processElement2(IntervalJoinOperatorTest.createStreamRecord(ts, "rhs"));
            return this;
        }

        public IntervalJoinOperatorTest.JoinTestBuilder processWatermark1(int ts) throws Exception {
            testHarness.processWatermark1(new Watermark(ts));
            return this;
        }

        public IntervalJoinOperatorTest.JoinTestBuilder processWatermark2(int ts) throws Exception {
            testHarness.processWatermark2(new Watermark(ts));
            return this;
        }

        public IntervalJoinOperatorTest.JoinTestBuilder processElementsAndWatermarks(int from, int to) throws Exception {
            if (lhsFasterThanRhs) {
                // add to lhs
                for (int i = from; i <= to; i++) {
                    testHarness.processElement1(IntervalJoinOperatorTest.createStreamRecord(i, "lhs"));
                    testHarness.processWatermark1(new Watermark(i));
                }
                // add to rhs
                for (int i = from; i <= to; i++) {
                    testHarness.processElement2(IntervalJoinOperatorTest.createStreamRecord(i, "rhs"));
                    testHarness.processWatermark2(new Watermark(i));
                }
            } else {
                // add to rhs
                for (int i = from; i <= to; i++) {
                    testHarness.processElement2(IntervalJoinOperatorTest.createStreamRecord(i, "rhs"));
                    testHarness.processWatermark2(new Watermark(i));
                }
                // add to lhs
                for (int i = from; i <= to; i++) {
                    testHarness.processElement1(IntervalJoinOperatorTest.createStreamRecord(i, "lhs"));
                    testHarness.processWatermark1(new Watermark(i));
                }
            }
            return this;
        }

        @SafeVarargs
        public final IntervalJoinOperatorTest.JoinTestBuilder andExpect(StreamRecord<Tuple2<IntervalJoinOperatorTest.TestElem, IntervalJoinOperatorTest.TestElem>>... elems) {
            assertOutput(Lists.newArrayList(elems), testHarness.getOutput());
            return this;
        }

        public IntervalJoinOperatorTest.JoinTestBuilder assertLeftBufferContainsOnly(long... timestamps) {
            try {
                assertContainsOnly(operator.getLeftBuffer(), timestamps);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return this;
        }

        public IntervalJoinOperatorTest.JoinTestBuilder assertRightBufferContainsOnly(long... timestamps) {
            try {
                assertContainsOnly(operator.getRightBuffer(), timestamps);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return this;
        }

        public IntervalJoinOperatorTest.JoinTestBuilder assertLeftBufferEmpty() {
            try {
                assertEmpty(operator.getLeftBuffer());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return this;
        }

        public IntervalJoinOperatorTest.JoinTestBuilder assertRightBufferEmpty() {
            try {
                assertEmpty(operator.getRightBuffer());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return this;
        }

        public IntervalJoinOperatorTest.JoinTestBuilder noLateRecords() {
            TestHarnessUtil.assertNoLateRecords(this.testHarness.getOutput());
            return this;
        }

        public void close() throws Exception {
            testHarness.close();
        }
    }

    private static class PassthroughFunction extends org.apache.flink.streaming.api.functions.co.ProcessJoinFunction<IntervalJoinOperatorTest.TestElem, IntervalJoinOperatorTest.TestElem, Tuple2<IntervalJoinOperatorTest.TestElem, IntervalJoinOperatorTest.TestElem>> {
        @Override
        public void processElement(IntervalJoinOperatorTest.TestElem left, IntervalJoinOperatorTest.TestElem right, Context ctx, Collector<Tuple2<IntervalJoinOperatorTest.TestElem, IntervalJoinOperatorTest.TestElem>> out) throws Exception {
            out.collect(Tuple2.of(left, right));
        }
    }

    private static class TestElem {
        String key;

        long ts;

        String source;

        public TestElem(long ts, String source) {
            this.key = "key";
            this.ts = ts;
            this.source = source;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            IntervalJoinOperatorTest.TestElem testElem = ((IntervalJoinOperatorTest.TestElem) (o));
            if ((ts) != (testElem.ts)) {
                return false;
            }
            if ((key) != null ? !(key.equals(testElem.key)) : (testElem.key) != null) {
                return false;
            }
            return (source) != null ? source.equals(testElem.source) : (testElem.source) == null;
        }

        @Override
        public int hashCode() {
            int result = ((key) != null) ? key.hashCode() : 0;
            result = (31 * result) + ((int) ((ts) ^ ((ts) >>> 32)));
            result = (31 * result) + ((source) != null ? source.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return ((this.source) + ":") + (this.ts);
        }

        public static TypeSerializer<IntervalJoinOperatorTest.TestElem> serializer() {
            return TypeInformation.of(new org.apache.flink.api.common.typeinfo.TypeHint<IntervalJoinOperatorTest.TestElem>() {}).createSerializer(new ExecutionConfig());
        }
    }

    /**
     * Custom test harness to avoid endless generics in all of the test code.
     */
    private static class TestHarness extends KeyedTwoInputStreamOperatorTestHarness<String, IntervalJoinOperatorTest.TestElem, IntervalJoinOperatorTest.TestElem, Tuple2<IntervalJoinOperatorTest.TestElem, IntervalJoinOperatorTest.TestElem>> {
        TestHarness(TwoInputStreamOperator<IntervalJoinOperatorTest.TestElem, IntervalJoinOperatorTest.TestElem, Tuple2<IntervalJoinOperatorTest.TestElem, IntervalJoinOperatorTest.TestElem>> operator, KeySelector<IntervalJoinOperatorTest.TestElem, String> keySelector1, KeySelector<IntervalJoinOperatorTest.TestElem, String> keySelector2, TypeInformation<String> keyType) throws Exception {
            super(operator, keySelector1, keySelector2, keyType);
        }
    }
}

