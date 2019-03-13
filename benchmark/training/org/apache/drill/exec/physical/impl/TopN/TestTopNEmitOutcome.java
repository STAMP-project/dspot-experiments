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
package org.apache.drill.exec.physical.impl.TopN;


import RecordBatch.IterOutcome.EMIT;
import RecordBatch.IterOutcome.NONE;
import RecordBatch.IterOutcome.OK;
import RecordBatch.IterOutcome.OK_NEW_SCHEMA;
import RelFieldCollation.Direction.ASCENDING;
import RelFieldCollation.Direction.DESCENDING;
import RelFieldCollation.NullDirection.FIRST;
import org.apache.drill.categories.OperatorTest;
import org.apache.drill.exec.physical.config.TopN;
import org.apache.drill.exec.physical.impl.BaseTestOpBatchEmitOutcome;
import org.apache.drill.exec.physical.impl.MockRecordBatch;
import org.apache.drill.shaded.guava.com.google.common.collect.Lists;
import org.apache.drill.test.PhysicalOpUnitTestBase;
import org.apache.drill.test.rowSet.HyperRowSetImpl;
import org.apache.drill.test.rowSet.RowSet;
import org.apache.drill.test.rowSet.RowSetComparison;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static IterOutcome.EMIT;
import static IterOutcome.NONE;
import static IterOutcome.OK_NEW_SCHEMA;
import static RecordBatch.IterOutcome.OK;


@Category(OperatorTest.class)
public class TestTopNEmitOutcome extends BaseTestOpBatchEmitOutcome {
    // private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(TestTopNEmitOutcome.class);
    /**
     * Verifies that if TopNBatch receives empty batches with OK_NEW_SCHEMA and EMIT outcome then it correctly produces
     * empty batches as output. First empty batch will be with OK_NEW_SCHEMA and second will be with EMIT outcome.
     */
    @Test
    public void testTopNEmptyBatchEmitOutcome() {
        inputContainer.add(emptyInputRowSet.container());
        inputContainer.add(emptyInputRowSet.container());
        inputOutcomes.add(IterOutcome.OK_NEW_SCHEMA);
        inputOutcomes.add(IterOutcome.EMIT);
        final MockRecordBatch mockInputBatch = new MockRecordBatch(operatorFixture.getFragmentContext(), opContext, inputContainer, inputOutcomes, emptyInputRowSet.container().getSchema());
        final TopN topNConfig = new TopN(null, Lists.newArrayList(ordering("id_left", ASCENDING, FIRST)), false, 10);
        final TopNBatch topNBatch = new TopNBatch(topNConfig, operatorFixture.getFragmentContext(), mockInputBatch);
        Assert.assertTrue(((topNBatch.next()) == (OK_NEW_SCHEMA)));
        outputRecordCount += topNBatch.getRecordCount();
        Assert.assertTrue(((topNBatch.next()) == (OK_NEW_SCHEMA)));
        Assert.assertTrue(((topNBatch.next()) == (EMIT)));
        outputRecordCount += topNBatch.getRecordCount();
        Assert.assertEquals(0, outputRecordCount);
        Assert.assertTrue(((topNBatch.next()) == (NONE)));
    }

    /**
     * Verifies that if TopNBatch receives a RecordBatch with EMIT outcome post build schema phase then it produces
     * output for those input batch correctly. The first output batch will always be returned with OK_NEW_SCHEMA
     * outcome followed by EMIT with empty batch. The test verifies the output order with the expected baseline.
     */
    @Test
    public void testTopNNonEmptyBatchEmitOutcome() {
        final RowSet.SingleRowSet nonEmptyInputRowSet2 = operatorFixture.rowSetBuilder(BaseTestOpBatchEmitOutcome.inputSchema).addRow(2, 20, "item2").addRow(13, 130, "item13").addRow(4, 40, "item4").build();
        final RowSet.SingleRowSet expectedRowSet = operatorFixture.rowSetBuilder(BaseTestOpBatchEmitOutcome.inputSchema).addRow(13, 130, "item13").addRow(4, 40, "item4").addRow(2, 20, "item2").build();
        inputContainer.add(emptyInputRowSet.container());
        inputContainer.add(nonEmptyInputRowSet2.container());
        inputOutcomes.add(OK_NEW_SCHEMA);
        inputOutcomes.add(EMIT);
        final MockRecordBatch mockInputBatch = new MockRecordBatch(operatorFixture.getFragmentContext(), opContext, inputContainer, inputOutcomes, emptyInputRowSet.container().getSchema());
        final TopN topNConfig = new TopN(null, Lists.newArrayList(ordering("id_left", DESCENDING, FIRST)), false, 10);
        final TopNBatch topNBatch = new TopNBatch(topNConfig, operatorFixture.getFragmentContext(), mockInputBatch);
        Assert.assertTrue(((topNBatch.next()) == (RecordBatch.IterOutcome.OK_NEW_SCHEMA)));
        outputRecordCount += topNBatch.getRecordCount();
        Assert.assertEquals(0, outputRecordCount);
        Assert.assertTrue(((topNBatch.next()) == (RecordBatch.IterOutcome.OK_NEW_SCHEMA)));
        outputRecordCount += topNBatch.getRecordCount();
        Assert.assertEquals(3, outputRecordCount);
        // verify results
        RowSet actualRowSet = HyperRowSetImpl.fromContainer(topNBatch.getContainer(), topNBatch.getSelectionVector4());
        new RowSetComparison(expectedRowSet).verify(actualRowSet);
        Assert.assertTrue(((topNBatch.next()) == (RecordBatch.IterOutcome.EMIT)));
        outputRecordCount += topNBatch.getRecordCount();
        Assert.assertEquals(3, outputRecordCount);
        // Release memory for row sets
        nonEmptyInputRowSet2.clear();
        expectedRowSet.clear();
    }

    @Test
    public void testTopNEmptyBatchFollowedByNonEmptyBatchEmitOutcome() {
        final RowSet.SingleRowSet nonEmptyInputRowSet2 = operatorFixture.rowSetBuilder(BaseTestOpBatchEmitOutcome.inputSchema).addRow(2, 20, "item2").addRow(13, 130, "item13").addRow(4, 40, "item4").build();
        final RowSet.SingleRowSet expectedRowSet = operatorFixture.rowSetBuilder(BaseTestOpBatchEmitOutcome.inputSchema).addRow(13, 130, "item13").addRow(4, 40, "item4").addRow(2, 20, "item2").build();
        inputContainer.add(emptyInputRowSet.container());
        inputContainer.add(emptyInputRowSet.container());
        inputContainer.add(nonEmptyInputRowSet2.container());
        inputOutcomes.add(OK_NEW_SCHEMA);
        inputOutcomes.add(EMIT);
        inputOutcomes.add(EMIT);
        final MockRecordBatch mockInputBatch = new MockRecordBatch(operatorFixture.getFragmentContext(), opContext, inputContainer, inputOutcomes, emptyInputRowSet.container().getSchema());
        final TopN topNConfig = new TopN(null, Lists.newArrayList(ordering("id_left", DESCENDING, FIRST)), false, 10);
        final TopNBatch topNBatch = new TopNBatch(topNConfig, operatorFixture.getFragmentContext(), mockInputBatch);
        Assert.assertTrue(((topNBatch.next()) == (RecordBatch.IterOutcome.OK_NEW_SCHEMA)));
        outputRecordCount += topNBatch.getRecordCount();
        Assert.assertEquals(0, outputRecordCount);
        Assert.assertTrue(((topNBatch.next()) == (RecordBatch.IterOutcome.OK_NEW_SCHEMA)));
        Assert.assertTrue(((topNBatch.next()) == (RecordBatch.IterOutcome.EMIT)));
        Assert.assertTrue(((topNBatch.next()) == (RecordBatch.IterOutcome.EMIT)));
        outputRecordCount += topNBatch.getRecordCount();
        Assert.assertEquals(3, outputRecordCount);
        // verify results
        RowSet actualRowSet = HyperRowSetImpl.fromContainer(topNBatch.getContainer(), topNBatch.getSelectionVector4());
        new RowSetComparison(expectedRowSet).verify(actualRowSet);
        // Release memory for row sets
        nonEmptyInputRowSet2.clear();
        expectedRowSet.clear();
    }

    @Test
    public void testTopNMultipleEmptyBatchFollowedByNonEmptyBatchEmitOutcome() {
        final RowSet.SingleRowSet nonEmptyInputRowSet2 = operatorFixture.rowSetBuilder(BaseTestOpBatchEmitOutcome.inputSchema).addRow(2, 20, "item2").addRow(13, 130, "item13").addRow(4, 40, "item4").build();
        final RowSet.SingleRowSet expectedRowSet = operatorFixture.rowSetBuilder(BaseTestOpBatchEmitOutcome.inputSchema).addRow(13, 130, "item13").addRow(4, 40, "item4").addRow(2, 20, "item2").build();
        inputContainer.add(emptyInputRowSet.container());
        inputContainer.add(emptyInputRowSet.container());
        inputContainer.add(emptyInputRowSet.container());
        inputContainer.add(emptyInputRowSet.container());
        inputContainer.add(nonEmptyInputRowSet2.container());
        inputOutcomes.add(OK_NEW_SCHEMA);
        inputOutcomes.add(EMIT);
        inputOutcomes.add(EMIT);
        inputOutcomes.add(EMIT);
        inputOutcomes.add(EMIT);
        final MockRecordBatch mockInputBatch = new MockRecordBatch(operatorFixture.getFragmentContext(), opContext, inputContainer, inputOutcomes, emptyInputRowSet.container().getSchema());
        final TopN topNConfig = new TopN(null, Lists.newArrayList(ordering("id_left", DESCENDING, FIRST)), false, 10);
        final TopNBatch topNBatch = new TopNBatch(topNConfig, operatorFixture.getFragmentContext(), mockInputBatch);
        Assert.assertTrue(((topNBatch.next()) == (RecordBatch.IterOutcome.OK_NEW_SCHEMA)));
        outputRecordCount += topNBatch.getRecordCount();
        Assert.assertEquals(0, outputRecordCount);
        Assert.assertTrue(((topNBatch.next()) == (RecordBatch.IterOutcome.OK_NEW_SCHEMA)));
        Assert.assertTrue(((topNBatch.next()) == (RecordBatch.IterOutcome.EMIT)));
        Assert.assertTrue(((topNBatch.next()) == (RecordBatch.IterOutcome.EMIT)));
        Assert.assertTrue(((topNBatch.next()) == (RecordBatch.IterOutcome.EMIT)));
        Assert.assertTrue(((topNBatch.next()) == (RecordBatch.IterOutcome.EMIT)));
        outputRecordCount += topNBatch.getRecordCount();
        Assert.assertEquals(3, outputRecordCount);
        // verify results
        RowSet actualRowSet = HyperRowSetImpl.fromContainer(topNBatch.getContainer(), topNBatch.getSelectionVector4());
        new RowSetComparison(expectedRowSet).verify(actualRowSet);
        // Release memory for row sets
        nonEmptyInputRowSet2.clear();
        expectedRowSet.clear();
    }

    /**
     * Verifies that if TopNBatch receives multiple non-empty record batch with EMIT outcome in between then it produces
     * output for those input batch correctly. In this case it receives first non-empty batch with OK_NEW_SCHEMA in
     * buildSchema phase followed by an empty batch with EMIT outcome. For this combination it produces output for the
     * record received so far along with EMIT outcome. Then it receives second non-empty batch with OK outcome and
     * produces output for it differently. The test validates that for each output received the order of the records are
     * correct
     */
    @Test
    public void testTopNResetsAfterFirstEmitOutcome() {
        final RowSet.SingleRowSet nonEmptyInputRowSet2 = operatorFixture.rowSetBuilder(BaseTestOpBatchEmitOutcome.inputSchema).addRow(2, 20, "item2").addRow(3, 30, "item3").build();
        final RowSet.SingleRowSet expectedRowSet1 = operatorFixture.rowSetBuilder(BaseTestOpBatchEmitOutcome.inputSchema).addRow(1, 10, "item1").build();
        final RowSet.SingleRowSet expectedRowSet2 = operatorFixture.rowSetBuilder(BaseTestOpBatchEmitOutcome.inputSchema).addRow(3, 30, "item3").addRow(2, 20, "item2").build();
        inputContainer.add(nonEmptyInputRowSet.container());
        inputContainer.add(emptyInputRowSet.container());
        inputContainer.add(nonEmptyInputRowSet2.container());
        inputContainer.add(emptyInputRowSet.container());
        inputOutcomes.add(OK_NEW_SCHEMA);
        inputOutcomes.add(EMIT);
        inputOutcomes.add(OK);
        inputOutcomes.add(EMIT);
        final MockRecordBatch mockInputBatch = new MockRecordBatch(operatorFixture.getFragmentContext(), opContext, inputContainer, inputOutcomes, emptyInputRowSet.container().getSchema());
        final TopN topNConfig = new TopN(null, Lists.newArrayList(ordering("id_left", DESCENDING, FIRST)), false, 10);
        final TopNBatch topNBatch = new TopNBatch(topNConfig, operatorFixture.getFragmentContext(), mockInputBatch);
        Assert.assertTrue(((topNBatch.next()) == (RecordBatch.IterOutcome.OK_NEW_SCHEMA)));
        Assert.assertTrue(((topNBatch.next()) == (RecordBatch.IterOutcome.OK_NEW_SCHEMA)));
        Assert.assertEquals(1, topNBatch.getRecordCount());
        // verify results with baseline
        RowSet actualRowSet1 = HyperRowSetImpl.fromContainer(topNBatch.getContainer(), topNBatch.getSelectionVector4());
        new RowSetComparison(expectedRowSet1).verify(actualRowSet1);
        Assert.assertTrue(((topNBatch.next()) == (RecordBatch.IterOutcome.EMIT)));
        Assert.assertEquals(0, topNBatch.getRecordCount());
        // State refresh happens and limit again works on new data batches
        Assert.assertTrue(((topNBatch.next()) == (RecordBatch.IterOutcome.EMIT)));
        Assert.assertEquals(2, topNBatch.getRecordCount());
        // verify results with baseline
        RowSet actualRowSet2 = HyperRowSetImpl.fromContainer(topNBatch.getContainer(), topNBatch.getSelectionVector4());
        new RowSetComparison(expectedRowSet2).verify(actualRowSet2);
        // Release memory for row sets
        nonEmptyInputRowSet2.clear();
        expectedRowSet2.clear();
        expectedRowSet1.clear();
    }

    /**
     * Verifies TopNBatch correctness for the case where it receives non-empty batch in build schema phase followed by
     * empty batchs with OK and EMIT outcomes.
     */
    @Test
    public void testTopN_NonEmptyFirst_EmptyOKEmitOutcome() {
        final RowSet.SingleRowSet expectedRowSet1 = operatorFixture.rowSetBuilder(BaseTestOpBatchEmitOutcome.inputSchema).addRow(1, 10, "item1").build();
        inputContainer.add(nonEmptyInputRowSet.container());
        inputContainer.add(emptyInputRowSet.container());
        inputContainer.add(emptyInputRowSet.container());
        inputContainer.add(emptyInputRowSet.container());
        inputOutcomes.add(OK_NEW_SCHEMA);
        inputOutcomes.add(OK);
        inputOutcomes.add(EMIT);
        inputOutcomes.add(NONE);
        final MockRecordBatch mockInputBatch = new MockRecordBatch(operatorFixture.getFragmentContext(), opContext, inputContainer, inputOutcomes, emptyInputRowSet.container().getSchema());
        final TopN topNConfig = new TopN(null, Lists.newArrayList(ordering("id_left", DESCENDING, FIRST)), false, 10);
        final TopNBatch topNBatch = new TopNBatch(topNConfig, operatorFixture.getFragmentContext(), mockInputBatch);
        Assert.assertTrue(((topNBatch.next()) == (RecordBatch.IterOutcome.OK_NEW_SCHEMA)));
        Assert.assertEquals(0, topNBatch.getRecordCount());
        Assert.assertTrue(((topNBatch.next()) == (RecordBatch.IterOutcome.OK_NEW_SCHEMA)));
        Assert.assertEquals(1, topNBatch.getRecordCount());
        // verify results with baseline
        RowSet actualRowSet1 = HyperRowSetImpl.fromContainer(topNBatch.getContainer(), topNBatch.getSelectionVector4());
        new RowSetComparison(expectedRowSet1).verify(actualRowSet1);
        Assert.assertTrue(((topNBatch.next()) == (RecordBatch.IterOutcome.EMIT)));
        Assert.assertEquals(0, topNBatch.getRecordCount());
        Assert.assertTrue(((topNBatch.next()) == (RecordBatch.IterOutcome.NONE)));
        // Release memory for row set
        expectedRowSet1.clear();
    }

    /**
     * Verifies that if TopNBatch receives multiple non-empty record batch with EMIT outcome in between then it produces
     * output for those input batch correctly. In this case it receives first non-empty batch with OK_NEW_SCHEMA in
     * buildSchema phase followed by an empty batch with EMIT outcome. For this combination it produces output for the
     * record received so far along with EMIT outcome. Then it receives second non-empty batch with OK outcome and
     * produces output for it differently. The test validates that for each output received the order of the records are
     * correct
     */
    @Test
    public void testTopNMultipleOutputBatchWithLowerLimits() {
        final RowSet.SingleRowSet nonEmptyInputRowSet2 = operatorFixture.rowSetBuilder(BaseTestOpBatchEmitOutcome.inputSchema).addRow(4, 40, "item4").addRow(2, 20, "item2").addRow(5, 50, "item5").addRow(3, 30, "item3").build();
        final RowSet.SingleRowSet expectedRowSet1 = operatorFixture.rowSetBuilder(BaseTestOpBatchEmitOutcome.inputSchema).addRow(1, 10, "item1").build();
        final RowSet.SingleRowSet expectedRowSet2 = operatorFixture.rowSetBuilder(BaseTestOpBatchEmitOutcome.inputSchema).addRow(5, 50, "item5").build();
        inputContainer.add(nonEmptyInputRowSet.container());
        inputContainer.add(emptyInputRowSet.container());
        inputContainer.add(nonEmptyInputRowSet2.container());
        inputOutcomes.add(OK_NEW_SCHEMA);
        inputOutcomes.add(EMIT);
        inputOutcomes.add(OK);
        final MockRecordBatch mockInputBatch = new MockRecordBatch(operatorFixture.getFragmentContext(), opContext, inputContainer, inputOutcomes, emptyInputRowSet.container().getSchema());
        final TopN topNConfig = new TopN(null, Lists.newArrayList(ordering("id_left", DESCENDING, FIRST)), false, 1);
        final TopNBatch topNBatch = new TopNBatch(topNConfig, operatorFixture.getFragmentContext(), mockInputBatch);
        Assert.assertTrue(((topNBatch.next()) == (RecordBatch.IterOutcome.OK_NEW_SCHEMA)));
        Assert.assertTrue(((topNBatch.next()) == (RecordBatch.IterOutcome.OK_NEW_SCHEMA)));
        Assert.assertEquals(1, topNBatch.getRecordCount());
        // verify results with baseline
        RowSet actualRowSet1 = HyperRowSetImpl.fromContainer(topNBatch.getContainer(), topNBatch.getSelectionVector4());
        new RowSetComparison(expectedRowSet1).verify(actualRowSet1);
        Assert.assertTrue(((topNBatch.next()) == (RecordBatch.IterOutcome.EMIT)));
        Assert.assertEquals(0, topNBatch.getRecordCount());
        // State refresh happens and limit again works on new data batches
        Assert.assertTrue(((topNBatch.next()) == (OK)));
        Assert.assertEquals(1, topNBatch.getRecordCount());
        // verify results with baseline
        RowSet actualRowSet2 = HyperRowSetImpl.fromContainer(topNBatch.getContainer(), topNBatch.getSelectionVector4());
        new RowSetComparison(expectedRowSet2).verify(actualRowSet2);
        Assert.assertTrue(((topNBatch.next()) == (RecordBatch.IterOutcome.NONE)));
        // Release memory for row sets
        nonEmptyInputRowSet2.clear();
        expectedRowSet2.clear();
        expectedRowSet1.clear();
    }

    @Test
    public void testTopNMultipleEMITOutcome() {
        final RowSet.SingleRowSet nonEmptyInputRowSet2 = operatorFixture.rowSetBuilder(BaseTestOpBatchEmitOutcome.inputSchema).addRow(2, 20, "item2").addRow(3, 30, "item3").build();
        inputContainer.add(nonEmptyInputRowSet.container());
        inputContainer.add(emptyInputRowSet.container());
        inputContainer.add(nonEmptyInputRowSet2.container());
        inputContainer.add(emptyInputRowSet.container());
        inputOutcomes.add(OK_NEW_SCHEMA);
        inputOutcomes.add(EMIT);
        inputOutcomes.add(EMIT);
        inputOutcomes.add(EMIT);
        final MockRecordBatch mockInputBatch = new MockRecordBatch(operatorFixture.getFragmentContext(), opContext, inputContainer, inputOutcomes, emptyInputRowSet.container().getSchema());
        final TopN topNConfig = new TopN(null, Lists.newArrayList(ordering("id_left", DESCENDING, FIRST)), false, 10);
        final TopNBatch topNBatch = new TopNBatch(topNConfig, operatorFixture.getFragmentContext(), mockInputBatch);
        // first limit evaluation
        Assert.assertTrue(((topNBatch.next()) == (RecordBatch.IterOutcome.OK_NEW_SCHEMA)));
        Assert.assertTrue(((topNBatch.next()) == (RecordBatch.IterOutcome.OK_NEW_SCHEMA)));
        Assert.assertEquals(1, topNBatch.getRecordCount());
        Assert.assertTrue(((topNBatch.next()) == (RecordBatch.IterOutcome.EMIT)));
        Assert.assertEquals(0, topNBatch.getRecordCount());
        // After seeing EMIT limit will refresh it's state and again evaluate limit on next set of input batches
        Assert.assertTrue(((topNBatch.next()) == (RecordBatch.IterOutcome.EMIT)));
        Assert.assertEquals(2, topNBatch.getRecordCount());
        Assert.assertTrue(((topNBatch.next()) == (RecordBatch.IterOutcome.EMIT)));
        Assert.assertEquals(0, topNBatch.getRecordCount());
        nonEmptyInputRowSet2.clear();
    }

    @Test
    public void testTopNMultipleInputToSingleOutputBatch() {
        final RowSet.SingleRowSet nonEmptyInputRowSet2 = operatorFixture.rowSetBuilder(BaseTestOpBatchEmitOutcome.inputSchema).addRow(2, 20, "item2").build();
        final RowSet.SingleRowSet expectedRowSet = operatorFixture.rowSetBuilder(BaseTestOpBatchEmitOutcome.inputSchema).addRow(2, 20, "item2").addRow(1, 10, "item1").build();
        inputContainer.add(nonEmptyInputRowSet.container());
        inputContainer.add(emptyInputRowSet.container());
        inputContainer.add(nonEmptyInputRowSet2.container());
        inputContainer.add(emptyInputRowSet.container());
        inputOutcomes.add(OK_NEW_SCHEMA);
        inputOutcomes.add(OK);
        inputOutcomes.add(OK);
        inputOutcomes.add(EMIT);
        final MockRecordBatch mockInputBatch = new MockRecordBatch(operatorFixture.getFragmentContext(), opContext, inputContainer, inputOutcomes, emptyInputRowSet.container().getSchema());
        final TopN topNConfig = new TopN(null, Lists.newArrayList(ordering("id_left", DESCENDING, FIRST)), false, 10);
        final TopNBatch topNBatch = new TopNBatch(topNConfig, operatorFixture.getFragmentContext(), mockInputBatch);
        Assert.assertTrue(((topNBatch.next()) == (RecordBatch.IterOutcome.OK_NEW_SCHEMA)));
        Assert.assertTrue(((topNBatch.next()) == (RecordBatch.IterOutcome.OK_NEW_SCHEMA)));
        Assert.assertEquals(2, topNBatch.getRecordCount());
        // Verify results
        RowSet actualRowSet = HyperRowSetImpl.fromContainer(topNBatch.getContainer(), topNBatch.getSelectionVector4());
        new RowSetComparison(expectedRowSet).verify(actualRowSet);
        Assert.assertTrue(((topNBatch.next()) == (RecordBatch.IterOutcome.EMIT)));
        Assert.assertEquals(0, topNBatch.getRecordCount());
        nonEmptyInputRowSet2.clear();
    }

    @Test
    public void testTopNMultipleInputToMultipleOutputBatch_LowerLimits() {
        final RowSet.SingleRowSet nonEmptyInputRowSet2 = operatorFixture.rowSetBuilder(BaseTestOpBatchEmitOutcome.inputSchema).addRow(7, 70, "item7").addRow(3, 30, "item3").addRow(13, 130, "item13").build();
        final RowSet.SingleRowSet nonEmptyInputRowSet3 = operatorFixture.rowSetBuilder(BaseTestOpBatchEmitOutcome.inputSchema).addRow(17, 170, "item17").addRow(3, 30, "item3").addRow(13, 130, "item13").build();
        final RowSet.SingleRowSet expectedRowSet1 = operatorFixture.rowSetBuilder(BaseTestOpBatchEmitOutcome.inputSchema).addRow(13, 130, "item13").addRow(7, 70, "item7").build();
        final RowSet.SingleRowSet expectedRowSet2 = operatorFixture.rowSetBuilder(BaseTestOpBatchEmitOutcome.inputSchema).addRow(17, 170, "item17").addRow(13, 130, "item13").build();
        inputContainer.add(nonEmptyInputRowSet.container());
        inputContainer.add(emptyInputRowSet.container());
        inputContainer.add(nonEmptyInputRowSet2.container());
        inputContainer.add(emptyInputRowSet.container());
        inputContainer.add(nonEmptyInputRowSet3.container());
        inputContainer.add(emptyInputRowSet.container());
        inputOutcomes.add(OK_NEW_SCHEMA);
        inputOutcomes.add(OK);
        inputOutcomes.add(EMIT);
        inputOutcomes.add(OK);
        inputOutcomes.add(OK);
        inputOutcomes.add(EMIT);
        final MockRecordBatch mockInputBatch = new MockRecordBatch(operatorFixture.getFragmentContext(), opContext, inputContainer, inputOutcomes, emptyInputRowSet.container().getSchema());
        final TopN topNConfig = new TopN(null, Lists.newArrayList(ordering("id_left", DESCENDING, FIRST)), false, 2);
        final TopNBatch topNBatch = new TopNBatch(topNConfig, operatorFixture.getFragmentContext(), mockInputBatch);
        Assert.assertTrue(((topNBatch.next()) == (RecordBatch.IterOutcome.OK_NEW_SCHEMA)));
        Assert.assertTrue(((topNBatch.next()) == (RecordBatch.IterOutcome.OK_NEW_SCHEMA)));
        Assert.assertEquals(2, topNBatch.getRecordCount());
        RowSet actualRowSet1 = HyperRowSetImpl.fromContainer(topNBatch.getContainer(), topNBatch.getSelectionVector4());
        new RowSetComparison(expectedRowSet1).verify(actualRowSet1);
        Assert.assertTrue(((topNBatch.next()) == (RecordBatch.IterOutcome.EMIT)));
        Assert.assertEquals(0, topNBatch.getRecordCount());
        Assert.assertTrue(((topNBatch.next()) == (RecordBatch.IterOutcome.EMIT)));
        Assert.assertEquals(2, topNBatch.getRecordCount());
        RowSet actualRowSet2 = HyperRowSetImpl.fromContainer(topNBatch.getContainer(), topNBatch.getSelectionVector4());
        new RowSetComparison(expectedRowSet2).verify(actualRowSet2);
        nonEmptyInputRowSet2.clear();
        nonEmptyInputRowSet3.clear();
        expectedRowSet1.clear();
        expectedRowSet2.clear();
    }

    /**
     * ***************************************************************************************
     * Tests for validating regular TopN behavior with no EMIT outcome
     * ****************************************************************************************
     */
    @Test
    public void testTopN_WithEmptyNonEmptyBatchesAndOKOutcome() {
        final RowSet.SingleRowSet nonEmptyInputRowSet2 = operatorFixture.rowSetBuilder(BaseTestOpBatchEmitOutcome.inputSchema).addRow(7, 70, "item7").addRow(3, 30, "item3").addRow(13, 130, "item13").build();
        final RowSet.SingleRowSet nonEmptyInputRowSet3 = operatorFixture.rowSetBuilder(BaseTestOpBatchEmitOutcome.inputSchema).addRow(17, 170, "item17").addRow(23, 230, "item23").addRow(130, 1300, "item130").build();
        final RowSet.SingleRowSet expectedRowSet = operatorFixture.rowSetBuilder(BaseTestOpBatchEmitOutcome.inputSchema).addRow(130, 1300, "item130").addRow(23, 230, "item23").addRow(17, 170, "item17").addRow(13, 130, "item13").build();
        inputContainer.add(nonEmptyInputRowSet.container());
        inputContainer.add(emptyInputRowSet.container());
        inputContainer.add(nonEmptyInputRowSet2.container());
        inputContainer.add(emptyInputRowSet.container());
        inputContainer.add(nonEmptyInputRowSet3.container());
        inputContainer.add(emptyInputRowSet.container());
        inputOutcomes.add(OK_NEW_SCHEMA);
        inputOutcomes.add(OK);
        inputOutcomes.add(OK);
        inputOutcomes.add(OK);
        inputOutcomes.add(OK);
        inputOutcomes.add(OK);
        final MockRecordBatch mockInputBatch = new MockRecordBatch(operatorFixture.getFragmentContext(), opContext, inputContainer, inputOutcomes, emptyInputRowSet.container().getSchema());
        final TopN topNConfig = new TopN(null, Lists.newArrayList(ordering("id_left", DESCENDING, FIRST)), false, 4);
        final TopNBatch topNBatch = new TopNBatch(topNConfig, operatorFixture.getFragmentContext(), mockInputBatch);
        Assert.assertTrue(((topNBatch.next()) == (RecordBatch.IterOutcome.OK_NEW_SCHEMA)));
        Assert.assertTrue(((topNBatch.next()) == (RecordBatch.IterOutcome.OK_NEW_SCHEMA)));
        Assert.assertEquals(4, topNBatch.getRecordCount());
        RowSet actualRowSet = HyperRowSetImpl.fromContainer(topNBatch.getContainer(), topNBatch.getSelectionVector4());
        new RowSetComparison(expectedRowSet).verify(actualRowSet);
        Assert.assertTrue(((topNBatch.next()) == (RecordBatch.IterOutcome.NONE)));
        nonEmptyInputRowSet2.clear();
        nonEmptyInputRowSet3.clear();
        expectedRowSet.clear();
    }

    @Test
    public void testRegularTopNWithEmptyDataSet() {
        inputContainer.add(emptyInputRowSet.container());
        inputOutcomes.add(OK_NEW_SCHEMA);
        final MockRecordBatch mockInputBatch = new MockRecordBatch(operatorFixture.getFragmentContext(), opContext, inputContainer, inputOutcomes, emptyInputRowSet.container().getSchema());
        final TopN topNConfig = new TopN(null, Lists.newArrayList(ordering("id_left", DESCENDING, FIRST)), false, 4);
        final TopNBatch topNBatch = new TopNBatch(topNConfig, operatorFixture.getFragmentContext(), mockInputBatch);
        Assert.assertTrue(((topNBatch.next()) == (RecordBatch.IterOutcome.OK_NEW_SCHEMA)));
        Assert.assertTrue(((topNBatch.next()) == (RecordBatch.IterOutcome.NONE)));
    }

    @Test
    public void testRegularTopNWithEmptyDataSetAndNoneOutcome() {
        inputContainer.add(emptyInputRowSet.container());
        inputOutcomes.add(NONE);
        final MockRecordBatch mockInputBatch = new MockRecordBatch(operatorFixture.getFragmentContext(), opContext, inputContainer, inputOutcomes, emptyInputRowSet.container().getSchema());
        final TopN topNConfig = new TopN(null, Lists.newArrayList(ordering("id_left", DESCENDING, FIRST)), false, 4);
        final TopNBatch topNBatch = new TopNBatch(topNConfig, operatorFixture.getFragmentContext(), mockInputBatch);
        Assert.assertTrue(((topNBatch.next()) == (RecordBatch.IterOutcome.NONE)));
    }
}

