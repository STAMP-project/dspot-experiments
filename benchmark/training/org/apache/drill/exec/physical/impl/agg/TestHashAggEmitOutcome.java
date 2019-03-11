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
package org.apache.drill.exec.physical.impl.agg;


import RecordBatch.IterOutcome;
import java.util.Arrays;
import java.util.List;
import org.apache.drill.categories.OperatorTest;
import org.apache.drill.exec.physical.impl.BaseTestOpBatchEmitOutcome;
import org.apache.drill.exec.record.RecordBatch;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static IterOutcome.EMIT;
import static IterOutcome.NONE;
import static IterOutcome.OK;
import static IterOutcome.OK_NEW_SCHEMA;


@Category(OperatorTest.class)
public class TestHashAggEmitOutcome extends BaseTestOpBatchEmitOutcome {
    // //////////////////////////////////////////////
    // 
    // T H E    U N I T   T E S T S
    // 
    // //////////////////////////////////////////////
    /**
     * Test receiving just a single input batch, empty
     */
    @Test
    public void testHashAggrWithEmptyDataSet() {
        int[] inpRowSet = new int[]{ 0 };
        RecordBatch[] inpOutcomes = new IterOutcome[]{ OK_NEW_SCHEMA };
        List<Integer> outputRowCounts = Arrays.asList(0, 0);
        List<RecordBatch.IterOutcome> outputOutcomes = Arrays.asList(IterOutcome.OK_NEW_SCHEMA, IterOutcome.NONE);
        testHashAggrEmit(null, null, null, null, null, null, null, null, null, null, inpRowSet, inpOutcomes, outputRowCounts, outputOutcomes);
    }

    /**
     * Verifies that if HashAggBatch receives empty batches with OK_NEW_SCHEMA and EMIT outcome then it correctly produces
     * empty batches as output. First empty batch will be with OK_NEW_SCHEMA and second will be with EMIT outcome.
     */
    @Test
    public void testHashAggrEmptyBatchEmitOutcome() {
        int[] inpRowSet = new int[]{ 0, 0 };
        RecordBatch[] inpOutcomes = new IterOutcome[]{ OK_NEW_SCHEMA, EMIT };
        List<Integer> outputRowCounts = Arrays.asList(0, 0, 0);
        List<RecordBatch.IterOutcome> outputOutcomes = Arrays.asList(IterOutcome.OK_NEW_SCHEMA, IterOutcome.EMIT, IterOutcome.NONE);
        testHashAggrEmit(null, null, null, null, null, null, null, null, null, null, inpRowSet, inpOutcomes, outputRowCounts, outputOutcomes);
    }

    /**
     * Verifies that if HashAgg receives a RecordBatch with EMIT outcome post build schema phase then it produces
     * output for those input batch correctly. The first output batch will always be returned with OK_NEW_SCHEMA
     * outcome followed by EMIT with empty batch.
     */
    @Test
    public void testHashAggrNonEmptyBatchEmitOutcome() {
        int[] inp2_1 = new int[]{ 2, 2, 13, 13, 4 };
        int[] inp2_2 = new int[]{ 20, 20, 130, 130, 40 };
        String[] inp2_3 = new String[]{ "item2", "item2", "item13", "item13", "item4" };
        String[] exp1_1 = new String[]{ "item2", "item13", "item4" };
        int[] exp1_2 = new int[]{ 44, 286, 44 };
        int[] inpRowSet = new int[]{ 0, 2 };
        RecordBatch[] inpOutcomes = new IterOutcome[]{ OK_NEW_SCHEMA, EMIT };
        List<Integer> outputRowCounts = Arrays.asList(0, 3, 0);
        List<RecordBatch.IterOutcome> outputOutcomes = Arrays.asList(IterOutcome.OK_NEW_SCHEMA, IterOutcome.EMIT, IterOutcome.NONE);
        testHashAggrEmit(inp2_1, inp2_2, inp2_3, null, null, null, exp1_1, exp1_2, null, null, inpRowSet, inpOutcomes, outputRowCounts, outputOutcomes);
    }

    /**
     *
     */
    @Test
    public void testHashAggrEmptyBatchFollowedByNonEmptyBatchEmitOutcome() {
        int[] inp2_1 = new int[]{ 2, 13, 4, 0, 0, 0 };
        int[] inp2_2 = new int[]{ 20, 130, 40, 2000, 1300, 4000 };
        String[] inp2_3 = new String[]{ "item2", "item13", "item4", "item2", "item13", "item4" };
        String[] exp1_1 = new String[]{ "item2", "item13", "item4" };
        int[] exp1_2 = new int[]{ 2022, 1443, 4044 };
        int[] inpRowSet = new int[]{ 0, 0, 2 };
        RecordBatch[] inpOutcomes = new IterOutcome[]{ OK_NEW_SCHEMA, EMIT, EMIT };
        List<Integer> outputRowCounts = Arrays.asList(0, 0, 3, 0);
        List<RecordBatch.IterOutcome> outputOutcomes = Arrays.asList(IterOutcome.OK_NEW_SCHEMA, IterOutcome.EMIT, IterOutcome.EMIT, IterOutcome.NONE);
        testHashAggrEmit(inp2_1, inp2_2, inp2_3, null, null, null, exp1_1, exp1_2, null, null, inpRowSet, inpOutcomes, outputRowCounts, outputOutcomes);
    }

    /**
     *
     */
    @Test
    public void testHashAggrMultipleEmptyBatchFollowedByNonEmptyBatchEmitOutcome() {
        int[] inp2_1 = new int[]{ 2, 13, 4, 0, 1, 0, 1 };
        int[] inp2_2 = new int[]{ 20, 130, 40, 0, 11000, 0, 33000 };
        String[] inp2_3 = new String[]{ "item2", "item13", "item4", "item2", "item2", "item13", "item13" };
        String[] exp1_1 = new String[]{ "item2", "item13", "item4" };
        int[] exp1_2 = new int[]{ 11023, 33144, 44 };
        int[] inpRowSet = new int[]{ 0, 0, 0, 0, 2 };
        RecordBatch[] inpOutcomes = new IterOutcome[]{ OK_NEW_SCHEMA, EMIT, EMIT, EMIT, EMIT };
        List<Integer> outputRowCounts = Arrays.asList(0, 0, 0, 0, 3, 0);
        List<RecordBatch.IterOutcome> outputOutcomes = Arrays.asList(IterOutcome.OK_NEW_SCHEMA, IterOutcome.EMIT, IterOutcome.EMIT, IterOutcome.EMIT, IterOutcome.EMIT, IterOutcome.NONE);
        testHashAggrEmit(inp2_1, inp2_2, inp2_3, null, null, null, exp1_1, exp1_2, null, null, inpRowSet, inpOutcomes, outputRowCounts, outputOutcomes);
    }

    /**
     * Verifies that if HashAggr receives multiple non-empty record batch with EMIT outcome in between then it produces
     * output for those input batch correctly. In this case it receives first non-empty batch with OK_NEW_SCHEMA in
     * buildSchema phase followed by an empty batch with EMIT outcome. For this combination it produces output for the
     * record received so far along with EMIT outcome. Then it receives second non-empty batch with OK outcome and
     * produces output for it differently.
     */
    @Test
    public void testHashAgrResetsAfterFirstEmitOutcome() {
        int[] inp2_1 = new int[]{ 2, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2 };
        int[] inp2_2 = new int[]{ 20, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 20 };
        String[] inp2_3 = new String[]{ "item2", "item3", "item3", "item3", "item3", "item3", "item3", "item3", "item3", "item3", "item3", "item2" };
        String[] exp1_1 = new String[]{ "item1" };
        int[] exp1_2 = new int[]{ 11 };
        String[] exp2_1 = new String[]{ "item2", "item3" };
        int[] exp2_2 = new int[]{ 44, 330 };
        int[] inpRowSet = new int[]{ 1, 0, 2, 0 };
        RecordBatch[] inpOutcomes = new IterOutcome[]{ OK_NEW_SCHEMA, EMIT, OK, EMIT };
        List<Integer> outputRowCounts = Arrays.asList(0, 1, 2, 0);
        List<RecordBatch.IterOutcome> outputOutcomes = Arrays.asList(IterOutcome.OK_NEW_SCHEMA, IterOutcome.EMIT, IterOutcome.EMIT, IterOutcome.NONE);
        testHashAggrEmit(inp2_1, inp2_2, inp2_3, null, null, null, exp1_1, exp1_2, exp2_1, exp2_2, inpRowSet, inpOutcomes, outputRowCounts, outputOutcomes);
    }

    /**
     * Verifies HashAggr correctness for the case where it receives non-empty batch in build schema phase followed by
     * empty batchs with OK and EMIT outcomes.
     */
    @Test
    public void testHashAggr_NonEmptyFirst_EmptyOKEmitOutcome() {
        String[] exp1_1 = new String[]{ "item1" };
        int[] exp1_2 = new int[]{ 11 };
        int[] inpRowSet = new int[]{ 1, 0, 0, 0 };
        RecordBatch[] inpOutcomes = new IterOutcome[]{ OK_NEW_SCHEMA, OK, EMIT, NONE };
        List<Integer> outputRowCounts = Arrays.asList(0, 1, 0);
        List<RecordBatch.IterOutcome> outputOutcomes = Arrays.asList(IterOutcome.OK_NEW_SCHEMA, IterOutcome.EMIT, IterOutcome.NONE);
        testHashAggrEmit(null, null, null, null, null, null, exp1_1, exp1_2, null, null, inpRowSet, inpOutcomes, outputRowCounts, outputOutcomes);
    }

    /**
     * Verifies that if HashAggr receives multiple non-empty record batches with EMIT outcome in between then it produces
     * output for those input batch correctly. In this case it receives first non-empty batch with OK_NEW_SCHEMA in
     * buildSchema phase followed by an empty batch with EMIT outcome. For this combination it produces output for the
     * record received so far along with EMIT outcome. Then it receives second non-empty batch with OK outcome and
     * produces output for it differently.
     */
    @Test
    public void testHashAggrMultipleOutputBatch() {
        int[] inp2_1 = new int[]{ 4, 2, 5, 3, 5, 4 };
        int[] inp2_2 = new int[]{ 40, 20, 50, 30, 50, 40 };
        String[] inp2_3 = new String[]{ "item4", "item2", "item5", "item3", "item5", "item4" };
        String[] exp1_1 = new String[]{ "item1" };
        int[] exp1_2 = new int[]{ 11 };
        String[] exp2_1 = new String[]{ "item4", "item2", "item5", "item3" };
        int[] exp2_2 = new int[]{ 88, 22, 110, 33 };
        int[] inpRowSet = new int[]{ 1, 0, 2 };
        RecordBatch[] inpOutcomes = new IterOutcome[]{ OK_NEW_SCHEMA, EMIT, OK };
        List<Integer> outputRowCounts = Arrays.asList(0, 1, 4, 0);
        List<RecordBatch.IterOutcome> outputOutcomes = Arrays.asList(IterOutcome.OK_NEW_SCHEMA, IterOutcome.EMIT, IterOutcome.OK, IterOutcome.NONE);
        testHashAggrEmit(inp2_1, inp2_2, inp2_3, null, null, null, exp1_1, exp1_2, exp2_1, exp2_2, inpRowSet, inpOutcomes, outputRowCounts, outputOutcomes);
    }

    /**
     *
     */
    @Test
    public void testHashAggrMultipleEMITOutcome() {
        int[] inp2_1 = new int[]{ 2, 3 };
        int[] inp2_2 = new int[]{ 20, 30 };
        String[] inp2_3 = new String[]{ "item2", "item3" };
        String[] exp1_1 = new String[]{ "item1" };
        int[] exp1_2 = new int[]{ 11 };
        String[] exp2_1 = new String[]{ "item2", "item3" };
        int[] exp2_2 = new int[]{ 22, 33 };
        int[] inpRowSet = new int[]{ 1, 0, 2, 0 };
        RecordBatch[] inpOutcomes = new IterOutcome[]{ OK_NEW_SCHEMA, EMIT, EMIT, EMIT };
        List<Integer> outputRowCounts = Arrays.asList(0, 1, 2, 0, 0);
        List<RecordBatch.IterOutcome> outputOutcomes = Arrays.asList(IterOutcome.OK_NEW_SCHEMA, IterOutcome.EMIT, IterOutcome.EMIT, IterOutcome.EMIT, IterOutcome.NONE);
        testHashAggrEmit(inp2_1, inp2_2, inp2_3, null, null, null, exp1_1, exp1_2, exp2_1, exp2_2, inpRowSet, inpOutcomes, outputRowCounts, outputOutcomes);
    }

    /**
     *
     */
    @Test
    public void testHashAggrMultipleInputToSingleOutputBatch() {
        int[] inp2_1 = new int[]{ 2 };
        int[] inp2_2 = new int[]{ 20 };
        String[] inp2_3 = new String[]{ "item2" };
        String[] exp1_1 = new String[]{ "item1", "item2" };
        int[] exp1_2 = new int[]{ 11, 22 };
        int[] inpRowSet = new int[]{ 1, 0, 2, 0 };
        RecordBatch[] inpOutcomes = new IterOutcome[]{ OK_NEW_SCHEMA, OK, OK, EMIT };
        List<Integer> outputRowCounts = Arrays.asList(0, 2, 0);
        List<RecordBatch.IterOutcome> outputOutcomes = Arrays.asList(IterOutcome.OK_NEW_SCHEMA, IterOutcome.EMIT, IterOutcome.NONE);
        testHashAggrEmit(inp2_1, inp2_2, inp2_3, null, null, null, exp1_1, exp1_2, null, null, inpRowSet, inpOutcomes, outputRowCounts, outputOutcomes);
    }

    /**
     *
     */
    @Test
    public void testHashAggrMultipleInputToMultipleOutputBatch() {
        int[] inp2_1 = new int[]{ 7, 2, 7, 3 };
        int[] inp2_2 = new int[]{ 70, 20, 70, 33 };
        String[] inp2_3 = new String[]{ "item7", "item1", "item7", "item3" };
        int[] inp3_1 = new int[]{ 17, 7, 3, 13, 9, 13 };
        int[] inp3_2 = new int[]{ 170, 71, 30, 130, 123, 130 };
        String[] inp3_3 = new String[]{ "item17", "item7", "item3", "item13", "item3", "item13" };
        String[] exp1_1 = new String[]{ "item1", "item7", "item3" };
        int[] exp1_2 = new int[]{ 33, 154, 36 };
        String[] exp2_1 = new String[]{ "item17", "item7", "item3", "item13" };
        int[] exp2_2 = new int[]{ 187, 78, 165, 286 };
        int[] inpRowSet = new int[]{ 1, 0, 2, 0, 3, 0 };
        RecordBatch[] inpOutcomes = new IterOutcome[]{ OK_NEW_SCHEMA, OK, EMIT, OK, OK, EMIT };
        List<Integer> outputRowCounts = Arrays.asList(0, 3, 4, 0);
        List<RecordBatch.IterOutcome> outputOutcomes = Arrays.asList(IterOutcome.OK_NEW_SCHEMA, IterOutcome.EMIT, IterOutcome.EMIT, IterOutcome.NONE);
        testHashAggrEmit(inp2_1, inp2_2, inp2_3, inp3_1, inp3_2, inp3_3, exp1_1, exp1_2, exp2_1, exp2_2, inpRowSet, inpOutcomes, outputRowCounts, outputOutcomes);
    }

    /**
     * **************************************************************************************
     *      Test validating a regular HashAggr behavior with no EMIT outcome input
     * **************************************************************************************
     */
    @Test
    public void testHashAggr_WithEmptyNonEmptyBatchesAndOKOutcome() {
        int[] inp2_1 = new int[]{ 2, 7, 3, 13, 13, 13 };
        int[] inp2_2 = new int[]{ 20, 70, 33, 130, 130, 130 };
        String[] inp2_3 = new String[]{ "item1", "item7", "item3", "item13", "item13", "item13" };
        int[] inp3_1 = new int[]{ 17, 23, 130, 0 };
        int[] inp3_2 = new int[]{ 170, 230, 1300, 0 };
        String[] inp3_3 = new String[]{ "item7", "item23", "item130", "item130" };
        String[] exp1_1 = new String[]{ "item1", "item7", "item3", "item13", "item23", "item130" };
        int[] exp1_2 = new int[]{ 33, 264, 36, 429, 253, 1430 };
        int[] inpRowSet = new int[]{ 1, 0, 2, 0, 3, 0 };
        RecordBatch[] inpOutcomes = new IterOutcome[]{ OK_NEW_SCHEMA, OK, OK, OK, OK, OK };
        List<Integer> outputRowCounts = Arrays.asList(0, 6, 0);
        List<RecordBatch.IterOutcome> outputOutcomes = Arrays.asList(IterOutcome.OK_NEW_SCHEMA, IterOutcome.OK, IterOutcome.NONE);
        testHashAggrEmit(inp2_1, inp2_2, inp2_3, inp3_1, inp3_2, inp3_3, exp1_1, exp1_2, null, null, inpRowSet, inpOutcomes, outputRowCounts, outputOutcomes);
    }
}

