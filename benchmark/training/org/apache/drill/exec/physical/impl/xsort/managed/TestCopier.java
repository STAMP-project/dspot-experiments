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


import Ordering.NULLS_UNSPECIFIED;
import Ordering.ORDER_ASC;
import java.util.ArrayList;
import java.util.List;
import org.apache.drill.categories.OperatorTest;
import org.apache.drill.common.logical.data.Order.Ordering;
import org.apache.drill.exec.ops.OperatorContext;
import org.apache.drill.exec.physical.config.Sort;
import org.apache.drill.exec.physical.impl.xsort.managed.PriorityQueueCopierWrapper.BatchMerger;
import org.apache.drill.exec.record.BatchSchema;
import org.apache.drill.exec.record.VectorContainer;
import org.apache.drill.test.SubOperatorTest;
import org.apache.drill.test.rowSet.RowSet;
import org.apache.drill.test.rowSet.RowSetUtilities;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Light-weight sanity test of the copier class. The implementation has
 * been used in production, so the tests here just check for the obvious
 * cases.
 * <p>
 * Note, however, that if significant changes are made to the copier,
 * then additional tests should be added to re-validate the code.
 */
@Category(OperatorTest.class)
public class TestCopier extends SubOperatorTest {
    @Test
    public void testEmptyInput() {
        BatchSchema schema = SortTestUtilities.nonNullSchema();
        List<BatchGroup> batches = new ArrayList<>();
        Sort popConfig = SortTestUtilities.makeCopierConfig(ORDER_ASC, NULLS_UNSPECIFIED);
        OperatorContext opContext = SubOperatorTest.fixture.newOperatorContext(popConfig);
        PriorityQueueCopierWrapper copier = new PriorityQueueCopierWrapper(opContext);
        VectorContainer dest = new VectorContainer();
        try {
            // TODO: Create a vector allocator to pass as last parameter so
            // that the test uses the same vector allocator as the production
            // code. Only nuisance is that we don't have the required metadata
            // readily at hand here...
            @SuppressWarnings({ "resource", "unused" })
            BatchMerger merger = copier.startMerge(schema, batches, dest, 10, null);
            Assert.fail();
        } catch (AssertionError e) {
            // Expected
        } finally {
            opContext.close();
        }
    }

    @Test
    public void testEmptyBatch() throws Exception {
        BatchSchema schema = SortTestUtilities.nonNullSchema();
        SortTestUtilities.CopierTester tester = new SortTestUtilities.CopierTester(SubOperatorTest.fixture);
        tester.addInput(SubOperatorTest.fixture.rowSetBuilder(schema).withSv2().build());
        tester.run();
    }

    @Test
    public void testSingleRow() throws Exception {
        BatchSchema schema = SortTestUtilities.nonNullSchema();
        SortTestUtilities.CopierTester tester = new SortTestUtilities.CopierTester(SubOperatorTest.fixture);
        tester.addInput(SubOperatorTest.fixture.rowSetBuilder(schema).addRow(10, "10").withSv2().build());
        tester.addOutput(SubOperatorTest.fixture.rowSetBuilder(schema).addRow(10, "10").build());
        tester.run();
    }

    @Test
    public void testTwoBatchesSingleRow() throws Exception {
        BatchSchema schema = SortTestUtilities.nonNullSchema();
        SortTestUtilities.CopierTester tester = new SortTestUtilities.CopierTester(SubOperatorTest.fixture);
        tester.addInput(SubOperatorTest.fixture.rowSetBuilder(schema).addRow(10, "10").withSv2().build());
        tester.addInput(SubOperatorTest.fixture.rowSetBuilder(schema).addRow(20, "20").withSv2().build());
        tester.addOutput(SubOperatorTest.fixture.rowSetBuilder(schema).addRow(10, "10").addRow(20, "20").build());
        tester.run();
    }

    @Test
    public void testMultipleOutput() throws Exception {
        BatchSchema schema = SortTestUtilities.nonNullSchema();
        SortTestUtilities.CopierTester tester = new SortTestUtilities.CopierTester(SubOperatorTest.fixture);
        tester.addInput(TestCopier.makeDataSet(schema, 0, 2, 10).toIndirect());
        tester.addInput(TestCopier.makeDataSet(schema, 1, 2, 10).toIndirect());
        tester.addOutput(TestCopier.makeDataSet(schema, 0, 1, 10));
        tester.addOutput(TestCopier.makeDataSet(schema, 10, 1, 10));
        tester.run();
    }

    // Also verifies that SV2s work
    @Test
    public void testMultipleOutputDesc() throws Exception {
        BatchSchema schema = SortTestUtilities.nonNullSchema();
        SortTestUtilities.CopierTester tester = new SortTestUtilities.CopierTester(SubOperatorTest.fixture);
        tester.sortOrder = Ordering.ORDER_DESC;
        tester.nullOrder = Ordering.NULLS_UNSPECIFIED;
        RowSet.SingleRowSet input = TestCopier.makeDataSet(schema, 0, 2, 10).toIndirect();
        RowSetUtilities.reverse(input.getSv2());
        tester.addInput(input);
        input = TestCopier.makeDataSet(schema, 1, 2, 10).toIndirect();
        RowSetUtilities.reverse(input.getSv2());
        tester.addInput(input);
        tester.addOutput(TestCopier.makeDataSet(schema, 19, (-1), 10));
        tester.addOutput(TestCopier.makeDataSet(schema, 9, (-1), 10));
        tester.run();
    }

    @Test
    public void testAscNullsLast() throws Exception {
        BatchSchema schema = SortTestUtilities.nullableSchema();
        SortTestUtilities.CopierTester tester = new SortTestUtilities.CopierTester(SubOperatorTest.fixture);
        tester.sortOrder = Ordering.ORDER_ASC;
        tester.nullOrder = Ordering.NULLS_LAST;
        tester.addInput(SubOperatorTest.fixture.rowSetBuilder(schema).addRow(1, "1").addRow(4, "4").addRow(null, "null").withSv2().build());
        tester.addInput(SubOperatorTest.fixture.rowSetBuilder(schema).addRow(2, "2").addRow(3, "3").addRow(null, "null").withSv2().build());
        tester.addOutput(SubOperatorTest.fixture.rowSetBuilder(schema).addRow(1, "1").addRow(2, "2").addRow(3, "3").addRow(4, "4").addRow(null, "null").addRow(null, "null").build());
        tester.run();
    }

    @Test
    public void testAscNullsFirst() throws Exception {
        BatchSchema schema = SortTestUtilities.nullableSchema();
        SortTestUtilities.CopierTester tester = new SortTestUtilities.CopierTester(SubOperatorTest.fixture);
        tester.sortOrder = Ordering.ORDER_ASC;
        tester.nullOrder = Ordering.NULLS_FIRST;
        tester.addInput(SubOperatorTest.fixture.rowSetBuilder(schema).addRow(null, "null").addRow(1, "1").addRow(4, "4").withSv2().build());
        tester.addInput(SubOperatorTest.fixture.rowSetBuilder(schema).addRow(null, "null").addRow(2, "2").addRow(3, "3").withSv2().build());
        tester.addOutput(SubOperatorTest.fixture.rowSetBuilder(schema).addRow(null, "null").addRow(null, "null").addRow(1, "1").addRow(2, "2").addRow(3, "3").addRow(4, "4").build());
        tester.run();
    }

    @Test
    public void testDescNullsLast() throws Exception {
        BatchSchema schema = SortTestUtilities.nullableSchema();
        SortTestUtilities.CopierTester tester = new SortTestUtilities.CopierTester(SubOperatorTest.fixture);
        tester.sortOrder = Ordering.ORDER_DESC;
        tester.nullOrder = Ordering.NULLS_LAST;
        tester.addInput(SubOperatorTest.fixture.rowSetBuilder(schema).addRow(4, "4").addRow(1, "1").addRow(null, "null").withSv2().build());
        tester.addInput(SubOperatorTest.fixture.rowSetBuilder(schema).addRow(3, "3").addRow(2, "2").addRow(null, "null").withSv2().build());
        tester.addOutput(SubOperatorTest.fixture.rowSetBuilder(schema).addRow(4, "4").addRow(3, "3").addRow(2, "2").addRow(1, "1").addRow(null, "null").addRow(null, "null").build());
        tester.run();
    }

    @Test
    public void testDescNullsFirst() throws Exception {
        BatchSchema schema = SortTestUtilities.nullableSchema();
        SortTestUtilities.CopierTester tester = new SortTestUtilities.CopierTester(SubOperatorTest.fixture);
        tester.sortOrder = Ordering.ORDER_DESC;
        tester.nullOrder = Ordering.NULLS_FIRST;
        tester.addInput(SubOperatorTest.fixture.rowSetBuilder(schema).addRow(null, "null").addRow(4, "4").addRow(1, "1").withSv2().build());
        tester.addInput(SubOperatorTest.fixture.rowSetBuilder(schema).addRow(null, "null").addRow(3, "3").addRow(2, "2").withSv2().build());
        tester.addOutput(SubOperatorTest.fixture.rowSetBuilder(schema).addRow(null, "null").addRow(null, "null").addRow(4, "4").addRow(3, "3").addRow(2, "2").addRow(1, "1").build());
        tester.run();
    }

    @Test
    public void testTypes() throws Exception {
        TestCopier.testAllTypes(SubOperatorTest.fixture);
    }

    @Test
    public void testMapType() throws Exception {
        testMapType(SubOperatorTest.fixture);
    }
}

