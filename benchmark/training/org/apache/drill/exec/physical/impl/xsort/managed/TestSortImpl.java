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
package org.apache.drill.exec.physical.impl.xsort.managed;


import ExecConstants.EXTERNAL_SORT_BATCH_LIMIT;
import ValueVector.MAX_ROW_COUNT;
import java.util.ArrayList;
import java.util.List;
import org.apache.drill.categories.OperatorTest;
import org.apache.drill.common.logical.data.Order.Ordering;
import org.apache.drill.exec.physical.impl.xsort.managed.SortImpl.SortResults;
import org.apache.drill.exec.record.BatchSchema;
import org.apache.drill.exec.record.VectorContainer;
import org.apache.drill.exec.vector.ValueVector;
import org.apache.drill.shaded.guava.com.google.common.base.Preconditions;
import org.apache.drill.test.BaseDirTestWatcher;
import org.apache.drill.test.DrillTest;
import org.apache.drill.test.OperatorFixture;
import org.apache.drill.test.rowSet.RowSet;
import org.apache.drill.test.rowSet.RowSetBuilder;
import org.apache.drill.test.rowSet.RowSetComparison;
import org.apache.drill.test.rowSet.RowSetReader;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Tests the external sort implementation: the "guts" of the sort stripped of the
 * Volcano-protocol layer. Assumes the individual components are already tested.
 */
@Category(OperatorTest.class)
public class TestSortImpl extends DrillTest {
    @Rule
    public final BaseDirTestWatcher dirTestWatcher = new BaseDirTestWatcher();

    private static VectorContainer dest;

    /**
     * Handy fixture to hold a sort, a set of input row sets (batches) and the
     * output set of row sets (batches.) Pumps the input into the sort and
     * harvests the output. Subclasses define the specifics of the sort,
     * define the input data, and validate the output data.
     */
    public static class SortTestFixture {
        private final OperatorFixture fixture;

        private final List<RowSet> inputSets = new ArrayList<>();

        private final List<RowSet> expected = new ArrayList<>();

        String sortOrder = Ordering.ORDER_ASC;

        String nullOrder = Ordering.NULLS_UNSPECIFIED;

        public SortTestFixture(OperatorFixture fixture) {
            this.fixture = fixture;
        }

        public SortTestFixture(OperatorFixture fixture, String sortOrder, String nullOrder) {
            this.fixture = fixture;
            this.sortOrder = sortOrder;
            this.nullOrder = nullOrder;
        }

        public void addInput(RowSet input) {
            inputSets.add(input);
        }

        public void addOutput(RowSet output) {
            expected.add(output);
        }

        public void run() {
            SortImpl sort = TestSortImpl.makeSortImpl(fixture, sortOrder, nullOrder);
            // Simulates a NEW_SCHEMA event
            if (!(inputSets.isEmpty())) {
                sort.setSchema(inputSets.get(0).container().getSchema());
            }
            // Simulates an OK event
            for (RowSet input : inputSets) {
                sort.addBatch(input.vectorAccessible());
            }
            // Simulate returning results
            SortResults results = sort.startMerge();
            if ((results.getContainer()) != (TestSortImpl.dest)) {
                TestSortImpl.dest.clear();
                TestSortImpl.dest = results.getContainer();
            }
            for (RowSet expectedSet : expected) {
                Assert.assertTrue(results.next());
                RowSet rowSet = TestSortImpl.toRowSet(results, TestSortImpl.dest);
                new RowSetComparison(expectedSet).verify(rowSet);
                expectedSet.clear();
            }
            Assert.assertFalse(results.next());
            validateSort(sort);
            results.close();
            TestSortImpl.dest.clear();
            sort.close();
            // Note: context closed separately because this is normally done by
            // the external sort itself after closing the output container.
            sort.opContext().close();
            validateFinalStats(sort);
        }

        protected void validateSort(SortImpl sort) {
        }

        protected void validateFinalStats(SortImpl sort) {
        }
    }

    /**
     * Test for null input (no input batches). Note that, in this case,
     * we never see a schema.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testNullInput() throws Exception {
        try (OperatorFixture fixture = OperatorFixture.standardFixture(dirTestWatcher)) {
            TestSortImpl.SortTestFixture sortTest = new TestSortImpl.SortTestFixture(fixture);
            sortTest.run();
        }
    }

    /**
     * Test for an input with a schema, but only an empty input batch.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testEmptyInput() throws Exception {
        try (OperatorFixture fixture = OperatorFixture.standardFixture(dirTestWatcher)) {
            BatchSchema schema = SortTestUtilities.nonNullSchema();
            TestSortImpl.SortTestFixture sortTest = new TestSortImpl.SortTestFixture(fixture);
            sortTest.addInput(fixture.rowSetBuilder(schema).build());
            sortTest.run();
        }
    }

    /**
     * Degenerate case: single row in single batch.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSingleRow() throws Exception {
        try (OperatorFixture fixture = OperatorFixture.standardFixture(dirTestWatcher)) {
            BatchSchema schema = SortTestUtilities.nonNullSchema();
            TestSortImpl.SortTestFixture sortTest = new TestSortImpl.SortTestFixture(fixture);
            sortTest.addInput(fixture.rowSetBuilder(schema).addRow(1, "first").build());
            sortTest.addOutput(fixture.rowSetBuilder(schema).addRow(1, "first").build());
            sortTest.run();
        }
    }

    /**
     * Degenerate case: two (unsorted) rows in single batch
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSingleBatch() throws Exception {
        try (OperatorFixture fixture = OperatorFixture.standardFixture(dirTestWatcher)) {
            BatchSchema schema = SortTestUtilities.nonNullSchema();
            TestSortImpl.SortTestFixture sortTest = new TestSortImpl.SortTestFixture(fixture);
            sortTest.addInput(fixture.rowSetBuilder(schema).addRow(2, "second").addRow(1, "first").build());
            sortTest.addOutput(fixture.rowSetBuilder(schema).addRow(1, "first").addRow(2, "second").build());
            sortTest.run();
        }
    }

    /**
     * Degenerate case, one row in each of two
     * (unsorted) batches.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testTwoBatches() throws Exception {
        try (OperatorFixture fixture = OperatorFixture.standardFixture(dirTestWatcher)) {
            BatchSchema schema = SortTestUtilities.nonNullSchema();
            TestSortImpl.SortTestFixture sortTest = new TestSortImpl.SortTestFixture(fixture);
            sortTest.addInput(fixture.rowSetBuilder(schema).addRow(2, "second").build());
            sortTest.addInput(fixture.rowSetBuilder(schema).addRow(1, "first").build());
            sortTest.addOutput(fixture.rowSetBuilder(schema).addRow(1, "first").addRow(2, "second").build());
            sortTest.run();
        }
    }

    /**
     * Crude-but-effective data generator that produces pseudo-random data
     * that can be easily verified. The pseudo-random data is generate by the
     * simple means of incrementing a counter using a random value, and wrapping.
     * This ensures we visit each value twice, and that the sorted output will
     * be a continuous run of numbers in proper order.
     */
    public static class DataGenerator {
        private final OperatorFixture fixture;

        private final BatchSchema schema;

        private final int targetCount;

        private final int batchSize;

        private final int step;

        private int rowCount;

        private int currentValue;

        public DataGenerator(OperatorFixture fixture, int targetCount, int batchSize) {
            this(fixture, targetCount, batchSize, 0, TestSortImpl.DataGenerator.guessStep(targetCount));
        }

        public DataGenerator(OperatorFixture fixture, int targetCount, int batchSize, int seed, int step) {
            this.fixture = fixture;
            this.targetCount = targetCount;
            Preconditions.checkArgument(((batchSize > 0) && (batchSize <= (ValueVector.MAX_ROW_COUNT))));
            this.batchSize = batchSize;
            this.step = step;
            schema = SortTestUtilities.nonNullSchema();
            currentValue = seed;
        }

        /**
         * Pick a reasonable prime step based on data size.
         *
         * @param target
         * 		number of rows to generate
         * @return the prime step size
         */
        private static int guessStep(int target) {
            if (target < 10) {
                return 7;
            } else
                if (target < 200) {
                    return 71;
                } else
                    if (target < 2000) {
                        return 701;
                    } else
                        if (target < 20000) {
                            return 7001;
                        } else {
                            return 17011;
                        }



        }

        public RowSet nextRowSet() {
            if ((rowCount) == (targetCount)) {
                return null;
            }
            RowSetBuilder builder = fixture.rowSetBuilder(schema);
            int end = Math.min(batchSize, ((targetCount) - (rowCount)));
            for (int i = 0; i < end; i++) {
                builder.addRow(currentValue, ((i + ", ") + (currentValue)));
                currentValue = ((currentValue) + (step)) % (targetCount);
                (rowCount)++;
            }
            return builder.build();
        }
    }

    /**
     * Validate a sort output batch based on the expectation that the key
     * is an ordered sequence of integers, split across multiple batches.
     */
    public static class DataValidator {
        private final int targetCount;

        private final int batchSize;

        private int batchCount;

        private int rowCount;

        public DataValidator(int targetCount, int batchSize) {
            this.targetCount = targetCount;
            Preconditions.checkArgument(((batchSize > 0) && (batchSize <= (ValueVector.MAX_ROW_COUNT))));
            this.batchSize = batchSize;
        }

        public void validate(RowSet output) {
            (batchCount)++;
            int expectedSize = Math.min(batchSize, ((targetCount) - (rowCount)));
            Assert.assertEquals(("Size of batch " + (batchCount)), expectedSize, output.rowCount());
            RowSetReader reader = output.reader();
            while (reader.next()) {
                Assert.assertEquals(((("Value of " + (batchCount)) + ":") + (rowCount)), rowCount, scalar(0).getInt());
                (rowCount)++;
            } 
        }

        public void validateDone() {
            Assert.assertEquals("Wrong row count", targetCount, rowCount);
        }
    }

    /**
     * Most tests have used small row counts because we want to probe specific bits
     * of interest. Try 1000 rows just to ensure things work
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testModerateBatch() throws Exception {
        try (OperatorFixture fixture = OperatorFixture.standardFixture(dirTestWatcher)) {
            runJumboBatchTest(fixture, 1000);
        }
    }

    /**
     * Hit the sort with the largest possible batch size to ensure nothing is lost
     * at the edges.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testLargeBatch() throws Exception {
        try (OperatorFixture fixture = OperatorFixture.standardFixture(dirTestWatcher)) {
            // partyOnMemory(fixture.allocator());
            runJumboBatchTest(fixture, MAX_ROW_COUNT);
        }
    }

    /**
     * Test wide rows with the stock copier.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testWideRows() throws Exception {
        try (OperatorFixture fixture = OperatorFixture.standardFixture(dirTestWatcher)) {
            runWideRowsTest(fixture, 1000, MAX_ROW_COUNT);
        }
    }

    /**
     * Force the sorter to spill, and verify that the resulting data
     * is correct. Uses a specific property of the sort to set the
     * in-memory batch limit so that we don't have to fiddle with filling
     * up memory. The point here is not to test the code that decides when
     * to spill (that was already tested.) Nor to test the spilling
     * mechanism itself (that has also already been tested.) Rather it is
     * to ensure that, when those components are integrated into the
     * sort implementation, that the whole assembly does the right thing.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSpill() throws Exception {
        OperatorFixture.Builder builder = OperatorFixture.builder(dirTestWatcher);
        builder.configBuilder().put(EXTERNAL_SORT_BATCH_LIMIT, 2);
        try (OperatorFixture fixture = builder.build()) {
            BatchSchema schema = SortTestUtilities.nonNullSchema();
            TestSortImpl.SortTestFixture sortTest = new TestSortImpl.SortTestFixture(fixture) {
                @Override
                protected void validateSort(SortImpl sort) {
                    Assert.assertEquals(1, sort.getMetrics().getSpillCount());
                    Assert.assertEquals(0, sort.getMetrics().getMergeCount());
                    Assert.assertEquals(2, sort.getMetrics().getPeakBatchCount());
                }

                @Override
                protected void validateFinalStats(SortImpl sort) {
                    Assert.assertTrue(((sort.getMetrics().getWriteBytes()) > 0));
                }
            };
            sortTest.addInput(fixture.rowSetBuilder(schema).addRow(2, "second").build());
            sortTest.addInput(fixture.rowSetBuilder(schema).addRow(3, "third").build());
            sortTest.addInput(fixture.rowSetBuilder(schema).addRow(1, "first").build());
            sortTest.addOutput(fixture.rowSetBuilder(schema).addRow(1, "first").addRow(2, "second").addRow(3, "third").build());
            sortTest.run();
        }
    }
}

