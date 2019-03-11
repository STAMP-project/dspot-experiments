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
package org.apache.drill.exec.physical.impl.join;


import RecordBatch.IterOutcome;
import RecordBatch.IterOutcome.NONE;
import RecordBatch.IterOutcome.OK;
import RecordBatch.IterOutcome.OK_NEW_SCHEMA;
import RecordBatch.IterOutcome.STOP;
import SqlKind.EQUALS;
import java.util.ArrayList;
import java.util.List;
import org.apache.calcite.rel.core.JoinRelType;
import org.apache.drill.categories.OperatorTest;
import org.apache.drill.common.expression.FieldReference;
import org.apache.drill.common.logical.data.JoinCondition;
import org.apache.drill.exec.physical.config.HashJoinPOP;
import org.apache.drill.exec.physical.impl.MockRecordBatch;
import org.apache.drill.exec.record.BatchSchema;
import org.apache.drill.exec.record.RecordBatch;
import org.apache.drill.exec.record.VectorContainer;
import org.apache.drill.exec.record.metadata.TupleSchema;
import org.apache.drill.shaded.guava.com.google.common.collect.Lists;
import org.apache.drill.test.PhysicalOpUnitTestBase;
import org.apache.drill.test.rowSet.RowSet;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Unit tests of the Hash Join getting various outcomes as input
 *  with uninitialized vector containers
 */
@Category(OperatorTest.class)
public class TestHashJoinOutcome extends PhysicalOpUnitTestBase {
    // private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(TestHashJoinOutcome.class);
    // input batch schemas
    private static TupleSchema inputSchemaRight;

    private static TupleSchema inputSchemaLeft;

    private static BatchSchema batchSchemaRight;

    private static BatchSchema batchSchemaLeft;

    // Input containers -- where row count is not set for the 2nd container !!
    private List<VectorContainer> uninitialized2ndInputContainersRight = new ArrayList<>(5);

    private List<VectorContainer> uninitialized2ndInputContainersLeft = new ArrayList<>(5);

    private RowSet.SingleRowSet emptyInputRowSetRight;

    private RowSet.SingleRowSet emptyInputRowSetLeft;

    // default Non-Empty input RowSets
    private RowSet.SingleRowSet nonEmptyInputRowSetRight;

    private RowSet.SingleRowSet nonEmptyInputRowSetLeft;

    // List of incoming containers
    private final List<VectorContainer> inputContainerRight = new ArrayList<>(5);

    private final List<VectorContainer> inputContainerLeft = new ArrayList<>(5);

    // List of incoming IterOutcomes
    private final List<RecordBatch.IterOutcome> inputOutcomesRight = new ArrayList<>(5);

    private final List<RecordBatch.IterOutcome> inputOutcomesLeft = new ArrayList<>(5);

    enum UninitializedSide {

        // which side of the join has an uninitialized container
        Right(true),
        Left(false);
        public boolean isRight;

        UninitializedSide(boolean which) {
            this.isRight = which;
        }
    }

    @Test
    public void testHashJoinStopOutcomeUninitRightSide() {
        testHashJoinOutcomes(TestHashJoinOutcome.UninitializedSide.Right, STOP, STOP);
    }

    @Test
    public void testHashJoinStopOutcomeUninitLeftSide() {
        testHashJoinOutcomes(TestHashJoinOutcome.UninitializedSide.Left, STOP, STOP);
    }

    @Test
    public void testHashJoinNoneOutcomeUninitRightSide() {
        testHashJoinOutcomes(TestHashJoinOutcome.UninitializedSide.Right, NONE, NONE);
    }

    @Test
    public void testHashJoinNoneOutcomeUninitLeftSide() {
        testHashJoinOutcomes(TestHashJoinOutcome.UninitializedSide.Left, NONE, NONE);
    }

    /**
     * Testing for DRILL-6755: No Hash Table is built when the first probe batch is NONE
     */
    @Test
    public void testHashJoinWhenProbeIsNONE() {
        inputOutcomesLeft.add(NONE);
        inputOutcomesRight.add(OK_NEW_SCHEMA);
        inputOutcomesRight.add(OK);
        inputOutcomesRight.add(NONE);
        // for the probe side input - use multiple batches (to check that they are all cleared/drained)
        final List<VectorContainer> buildSideinputContainer = new ArrayList<>(5);
        buildSideinputContainer.add(emptyInputRowSetRight.container());
        buildSideinputContainer.add(nonEmptyInputRowSetRight.container());
        RowSet.SingleRowSet secondInputRowSetRight = operatorFixture.rowSetBuilder(TestHashJoinOutcome.inputSchemaRight).addRow(456).build();
        RowSet.SingleRowSet thirdInputRowSetRight = operatorFixture.rowSetBuilder(TestHashJoinOutcome.inputSchemaRight).addRow(789).build();
        buildSideinputContainer.add(secondInputRowSetRight.container());
        buildSideinputContainer.add(thirdInputRowSetRight.container());
        final MockRecordBatch mockInputBatchRight = new MockRecordBatch(operatorFixture.getFragmentContext(), opContext, buildSideinputContainer, inputOutcomesRight, TestHashJoinOutcome.batchSchemaRight);
        final MockRecordBatch mockInputBatchLeft = new MockRecordBatch(operatorFixture.getFragmentContext(), opContext, inputContainerLeft, inputOutcomesLeft, TestHashJoinOutcome.batchSchemaLeft);
        List<JoinCondition> conditions = Lists.newArrayList();
        conditions.add(new JoinCondition(EQUALS.toString(), FieldReference.getWithQuotedRef("leftcol"), FieldReference.getWithQuotedRef("rightcol")));
        HashJoinPOP hjConf = new HashJoinPOP(null, null, conditions, JoinRelType.INNER);
        HashJoinBatch hjBatch = new HashJoinBatch(hjConf, operatorFixture.getFragmentContext(), mockInputBatchLeft, mockInputBatchRight);
        RecordBatch.IterOutcome gotOutcome = hjBatch.next();
        Assert.assertTrue((gotOutcome == (IterOutcome.OK_NEW_SCHEMA)));
        gotOutcome = hjBatch.next();
        Assert.assertTrue((gotOutcome == (IterOutcome.NONE)));
        secondInputRowSetRight.clear();
        thirdInputRowSetRight.clear();
        buildSideinputContainer.clear();
    }
}

