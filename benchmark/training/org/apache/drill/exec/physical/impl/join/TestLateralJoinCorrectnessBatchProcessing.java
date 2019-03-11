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
import RecordBatch.IterOutcome.EMIT;
import RecordBatch.IterOutcome.OK;
import RecordBatch.IterOutcome.OK_NEW_SCHEMA;
import avro.shaded.com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import org.apache.calcite.rel.core.JoinRelType;
import org.apache.drill.exec.ops.OperatorContext;
import org.apache.drill.exec.physical.config.LateralJoinPOP;
import org.apache.drill.exec.physical.impl.MockRecordBatch;
import org.apache.drill.exec.planner.common.DrillLateralJoinRelBase;
import org.apache.drill.exec.record.CloseableRecordBatch;
import org.apache.drill.exec.record.RecordBatch;
import org.apache.drill.exec.record.VectorAccessibleUtilities;
import org.apache.drill.exec.record.VectorContainer;
import org.apache.drill.exec.record.metadata.TupleMetadata;
import org.apache.drill.test.SubOperatorTest;
import org.apache.drill.test.rowSet.DirectRowSet;
import org.apache.drill.test.rowSet.RowSet;
import org.apache.drill.test.rowSet.RowSetComparison;
import org.junit.Assert;
import org.junit.Test;


public class TestLateralJoinCorrectnessBatchProcessing extends SubOperatorTest {
    // private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(TestNewLateralJoinCorrectness.class);
    // Operator Context for mock batch
    private static OperatorContext operatorContext;

    // Left Batch Schema
    private static TupleMetadata leftSchema;

    // Right Batch Schema
    private static TupleMetadata rightSchema;

    // Right Batch Schema
    private static TupleMetadata expectedSchema;

    // Right Batch Schema
    private static TupleMetadata expectedSchemaLeftJoin;

    // Empty left RowSet
    private static RowSet.SingleRowSet emptyLeftRowSet;

    // Non-Empty left RowSet
    private static RowSet.SingleRowSet nonEmptyLeftRowSet;

    // List of left incoming containers
    private static final List<VectorContainer> leftContainer = new ArrayList<>(5);

    // List of left IterOutcomes
    private static final List<RecordBatch.IterOutcome> leftOutcomes = new ArrayList<>(5);

    // Empty right RowSet
    private static RowSet.SingleRowSet emptyRightRowSet;

    // Non-Empty right RowSet
    private static RowSet.SingleRowSet nonEmptyRightRowSet;

    // List of right incoming containers
    private static final List<VectorContainer> rightContainer = new ArrayList<>(5);

    // List of right IterOutcomes
    private static final List<RecordBatch.IterOutcome> rightOutcomes = new ArrayList<>(5);

    // Lateral Join POP Config
    private static LateralJoinPOP ljPopConfig;

    @Test
    public void testLeftAndRightAllMatchingRows_SingleBatch() throws Exception {
        TestLateralJoinCorrectnessBatchProcessing.leftContainer.add(TestLateralJoinCorrectnessBatchProcessing.nonEmptyLeftRowSet.container());
        // Get the left IterOutcomes for Lateral Join
        TestLateralJoinCorrectnessBatchProcessing.leftOutcomes.add(OK_NEW_SCHEMA);
        // Create Left MockRecordBatch
        final CloseableRecordBatch leftMockBatch = new MockRecordBatch(SubOperatorTest.fixture.getFragmentContext(), TestLateralJoinCorrectnessBatchProcessing.operatorContext, TestLateralJoinCorrectnessBatchProcessing.leftContainer, TestLateralJoinCorrectnessBatchProcessing.leftOutcomes, TestLateralJoinCorrectnessBatchProcessing.leftContainer.get(0).getSchema());
        final RowSet.SingleRowSet expectedRowSet = SubOperatorTest.fixture.rowSetBuilder(TestLateralJoinCorrectnessBatchProcessing.expectedSchema).addRow(1, 10, "item1", 11, 110, "item11").addRow(2, 20, "item2", 22, 220, "item22").addRow(3, 30, "item3", 33, 330, "item33").addRow(4, 40, "item4", 44, 440, "item44").build();
        TestLateralJoinCorrectnessBatchProcessing.rightContainer.add(TestLateralJoinCorrectnessBatchProcessing.emptyRightRowSet.container());
        TestLateralJoinCorrectnessBatchProcessing.rightContainer.add(TestLateralJoinCorrectnessBatchProcessing.nonEmptyRightRowSet.container());
        TestLateralJoinCorrectnessBatchProcessing.rightOutcomes.add(OK_NEW_SCHEMA);
        TestLateralJoinCorrectnessBatchProcessing.rightOutcomes.add(EMIT);
        final CloseableRecordBatch rightMockBatch = new MockRecordBatch(SubOperatorTest.fixture.getFragmentContext(), TestLateralJoinCorrectnessBatchProcessing.operatorContext, TestLateralJoinCorrectnessBatchProcessing.rightContainer, TestLateralJoinCorrectnessBatchProcessing.rightOutcomes, TestLateralJoinCorrectnessBatchProcessing.rightContainer.get(0).getSchema());
        final LateralJoinBatch ljBatch = new LateralJoinBatch(TestLateralJoinCorrectnessBatchProcessing.ljPopConfig, SubOperatorTest.fixture.getFragmentContext(), leftMockBatch, rightMockBatch);
        try {
            Assert.assertTrue(((IterOutcome.OK_NEW_SCHEMA) == (ljBatch.next())));
            Assert.assertTrue(((IterOutcome.OK) == (ljBatch.next())));
            Assert.assertTrue(((ljBatch.getRecordCount()) == (TestLateralJoinCorrectnessBatchProcessing.nonEmptyLeftRowSet.rowCount())));
            // verify results
            RowSet actualRowSet = DirectRowSet.fromContainer(ljBatch.getContainer());
            new RowSetComparison(expectedRowSet).verify(actualRowSet);
            Assert.assertTrue(((IterOutcome.NONE) == (ljBatch.next())));
        } finally {
            // Close all the resources for this test case
            ljBatch.close();
            leftMockBatch.close();
            rightMockBatch.close();
            expectedRowSet.clear();
        }
    }

    @Test
    public void testLeftAndRightAllMatchingRows_MultipleBatch() throws Exception {
        TestLateralJoinCorrectnessBatchProcessing.leftContainer.add(TestLateralJoinCorrectnessBatchProcessing.nonEmptyLeftRowSet.container());
        // Get the left IterOutcomes for Lateral Join
        TestLateralJoinCorrectnessBatchProcessing.leftOutcomes.add(OK_NEW_SCHEMA);
        // Create Left MockRecordBatch
        final CloseableRecordBatch leftMockBatch = new MockRecordBatch(SubOperatorTest.fixture.getFragmentContext(), TestLateralJoinCorrectnessBatchProcessing.operatorContext, TestLateralJoinCorrectnessBatchProcessing.leftContainer, TestLateralJoinCorrectnessBatchProcessing.leftOutcomes, TestLateralJoinCorrectnessBatchProcessing.leftContainer.get(0).getSchema());
        final RowSet.SingleRowSet nonEmptyRightRowSet3 = SubOperatorTest.fixture.rowSetBuilder(TestLateralJoinCorrectnessBatchProcessing.rightSchema).addRow(4, 44, 440, "item44").build();
        TestLateralJoinCorrectnessBatchProcessing.rightContainer.add(TestLateralJoinCorrectnessBatchProcessing.emptyRightRowSet.container());
        TestLateralJoinCorrectnessBatchProcessing.rightContainer.add(TestLateralJoinCorrectnessBatchProcessing.nonEmptyRightRowSet.container());
        TestLateralJoinCorrectnessBatchProcessing.rightContainer.add(nonEmptyRightRowSet3.container());
        final RowSet.SingleRowSet expectedRowSet = SubOperatorTest.fixture.rowSetBuilder(TestLateralJoinCorrectnessBatchProcessing.expectedSchema).addRow(1, 10, "item1", 11, 110, "item11").addRow(2, 20, "item2", 22, 220, "item22").addRow(3, 30, "item3", 33, 330, "item33").addRow(4, 40, "item4", 44, 440, "item44").addRow(4, 40, "item4", 44, 440, "item44").build();
        TestLateralJoinCorrectnessBatchProcessing.rightOutcomes.add(OK_NEW_SCHEMA);
        TestLateralJoinCorrectnessBatchProcessing.rightOutcomes.add(OK);
        TestLateralJoinCorrectnessBatchProcessing.rightOutcomes.add(EMIT);
        final CloseableRecordBatch rightMockBatch = new MockRecordBatch(SubOperatorTest.fixture.getFragmentContext(), TestLateralJoinCorrectnessBatchProcessing.operatorContext, TestLateralJoinCorrectnessBatchProcessing.rightContainer, TestLateralJoinCorrectnessBatchProcessing.rightOutcomes, TestLateralJoinCorrectnessBatchProcessing.rightContainer.get(0).getSchema());
        final LateralJoinBatch ljBatch = new LateralJoinBatch(TestLateralJoinCorrectnessBatchProcessing.ljPopConfig, SubOperatorTest.fixture.getFragmentContext(), leftMockBatch, rightMockBatch);
        try {
            Assert.assertTrue(((IterOutcome.OK_NEW_SCHEMA) == (ljBatch.next())));
            Assert.assertTrue(((IterOutcome.OK) == (ljBatch.next())));
            Assert.assertTrue(((ljBatch.getRecordCount()) == ((TestLateralJoinCorrectnessBatchProcessing.nonEmptyLeftRowSet.rowCount()) + (nonEmptyRightRowSet3.rowCount()))));
            // verify results
            RowSet actualRowSet = DirectRowSet.fromContainer(ljBatch.getContainer());
            new RowSetComparison(expectedRowSet).verify(actualRowSet);
            Assert.assertTrue(((IterOutcome.NONE) == (ljBatch.next())));
        } finally {
            // Close all the resources for this test case
            ljBatch.close();
            leftMockBatch.close();
            rightMockBatch.close();
            nonEmptyRightRowSet3.clear();
            expectedRowSet.clear();
        }
    }

    @Test
    public void testLeftAndRightAllMatchingRows_SecondBatch_Empty() throws Exception {
        TestLateralJoinCorrectnessBatchProcessing.leftContainer.add(TestLateralJoinCorrectnessBatchProcessing.nonEmptyLeftRowSet.container());
        // Get the left IterOutcomes for Lateral Join
        TestLateralJoinCorrectnessBatchProcessing.leftOutcomes.add(OK_NEW_SCHEMA);
        // Create Left MockRecordBatch
        final CloseableRecordBatch leftMockBatch = new MockRecordBatch(SubOperatorTest.fixture.getFragmentContext(), TestLateralJoinCorrectnessBatchProcessing.operatorContext, TestLateralJoinCorrectnessBatchProcessing.leftContainer, TestLateralJoinCorrectnessBatchProcessing.leftOutcomes, TestLateralJoinCorrectnessBatchProcessing.leftContainer.get(0).getSchema());
        final RowSet.SingleRowSet expectedRowSet = SubOperatorTest.fixture.rowSetBuilder(TestLateralJoinCorrectnessBatchProcessing.expectedSchema).addRow(1, 10, "item1", 11, 110, "item11").addRow(2, 20, "item2", 22, 220, "item22").addRow(3, 30, "item3", 33, 330, "item33").addRow(4, 40, "item4", 44, 440, "item44").build();
        TestLateralJoinCorrectnessBatchProcessing.rightContainer.add(TestLateralJoinCorrectnessBatchProcessing.emptyRightRowSet.container());
        TestLateralJoinCorrectnessBatchProcessing.rightContainer.add(TestLateralJoinCorrectnessBatchProcessing.nonEmptyRightRowSet.container());
        TestLateralJoinCorrectnessBatchProcessing.rightContainer.add(TestLateralJoinCorrectnessBatchProcessing.emptyRightRowSet.container());
        TestLateralJoinCorrectnessBatchProcessing.rightOutcomes.add(OK_NEW_SCHEMA);
        TestLateralJoinCorrectnessBatchProcessing.rightOutcomes.add(OK);
        TestLateralJoinCorrectnessBatchProcessing.rightOutcomes.add(EMIT);
        final CloseableRecordBatch rightMockBatch = new MockRecordBatch(SubOperatorTest.fixture.getFragmentContext(), TestLateralJoinCorrectnessBatchProcessing.operatorContext, TestLateralJoinCorrectnessBatchProcessing.rightContainer, TestLateralJoinCorrectnessBatchProcessing.rightOutcomes, TestLateralJoinCorrectnessBatchProcessing.rightContainer.get(0).getSchema());
        final LateralJoinBatch ljBatch = new LateralJoinBatch(TestLateralJoinCorrectnessBatchProcessing.ljPopConfig, SubOperatorTest.fixture.getFragmentContext(), leftMockBatch, rightMockBatch);
        try {
            Assert.assertTrue(((IterOutcome.OK_NEW_SCHEMA) == (ljBatch.next())));
            Assert.assertTrue(((IterOutcome.OK) == (ljBatch.next())));
            Assert.assertTrue(((ljBatch.getRecordCount()) == (TestLateralJoinCorrectnessBatchProcessing.nonEmptyLeftRowSet.rowCount())));
            // verify results
            RowSet actualRowSet = DirectRowSet.fromContainer(ljBatch.getContainer());
            new RowSetComparison(expectedRowSet).verify(actualRowSet);
            Assert.assertTrue(((IterOutcome.NONE) == (ljBatch.next())));
        } finally {
            // Close all the resources for this test case
            ljBatch.close();
            leftMockBatch.close();
            rightMockBatch.close();
            expectedRowSet.clear();
        }
    }

    @Test
    public void testLeftAndRightWithMissingRows_SingleBatch() throws Exception {
        TestLateralJoinCorrectnessBatchProcessing.leftContainer.add(TestLateralJoinCorrectnessBatchProcessing.nonEmptyLeftRowSet.container());
        // Get the left IterOutcomes for Lateral Join
        TestLateralJoinCorrectnessBatchProcessing.leftOutcomes.add(OK_NEW_SCHEMA);
        // Create Left MockRecordBatch
        final CloseableRecordBatch leftMockBatch = new MockRecordBatch(SubOperatorTest.fixture.getFragmentContext(), TestLateralJoinCorrectnessBatchProcessing.operatorContext, TestLateralJoinCorrectnessBatchProcessing.leftContainer, TestLateralJoinCorrectnessBatchProcessing.leftOutcomes, TestLateralJoinCorrectnessBatchProcessing.leftContainer.get(0).getSchema());
        // Get the right container with dummy data
        final RowSet.SingleRowSet nonEmptyRightRowSet2 = SubOperatorTest.fixture.rowSetBuilder(TestLateralJoinCorrectnessBatchProcessing.rightSchema).addRow(1, 11, 110, "item11").addRow(4, 44, 440, "item44").build();
        final RowSet.SingleRowSet expectedRowSet = SubOperatorTest.fixture.rowSetBuilder(TestLateralJoinCorrectnessBatchProcessing.expectedSchema).addRow(1, 10, "item1", 11, 110, "item11").addRow(4, 40, "item4", 44, 440, "item44").build();
        TestLateralJoinCorrectnessBatchProcessing.rightContainer.add(TestLateralJoinCorrectnessBatchProcessing.emptyRightRowSet.container());
        TestLateralJoinCorrectnessBatchProcessing.rightContainer.add(nonEmptyRightRowSet2.container());
        TestLateralJoinCorrectnessBatchProcessing.rightOutcomes.add(OK_NEW_SCHEMA);
        TestLateralJoinCorrectnessBatchProcessing.rightOutcomes.add(EMIT);
        final CloseableRecordBatch rightMockBatch = new MockRecordBatch(SubOperatorTest.fixture.getFragmentContext(), TestLateralJoinCorrectnessBatchProcessing.operatorContext, TestLateralJoinCorrectnessBatchProcessing.rightContainer, TestLateralJoinCorrectnessBatchProcessing.rightOutcomes, TestLateralJoinCorrectnessBatchProcessing.rightContainer.get(0).getSchema());
        final LateralJoinBatch ljBatch = new LateralJoinBatch(TestLateralJoinCorrectnessBatchProcessing.ljPopConfig, SubOperatorTest.fixture.getFragmentContext(), leftMockBatch, rightMockBatch);
        try {
            Assert.assertTrue(((IterOutcome.OK_NEW_SCHEMA) == (ljBatch.next())));
            Assert.assertTrue(((IterOutcome.OK) == (ljBatch.next())));
            Assert.assertTrue(((ljBatch.getRecordCount()) == (nonEmptyRightRowSet2.rowCount())));
            // verify results
            RowSet actualRowSet = DirectRowSet.fromContainer(ljBatch.getContainer());
            new RowSetComparison(expectedRowSet).verify(actualRowSet);
            Assert.assertTrue(((IterOutcome.NONE) == (ljBatch.next())));
        } finally {
            // Close all the resources for this test case
            ljBatch.close();
            leftMockBatch.close();
            rightMockBatch.close();
            nonEmptyRightRowSet2.clear();
            expectedRowSet.clear();
        }
    }

    @Test
    public void testLeftAndRightWithMissingRows_LeftJoin_SingleBatch() throws Exception {
        TestLateralJoinCorrectnessBatchProcessing.leftContainer.add(TestLateralJoinCorrectnessBatchProcessing.nonEmptyLeftRowSet.container());
        // Get the left IterOutcomes for Lateral Join
        TestLateralJoinCorrectnessBatchProcessing.leftOutcomes.add(OK_NEW_SCHEMA);
        // Create Left MockRecordBatch
        final CloseableRecordBatch leftMockBatch = new MockRecordBatch(SubOperatorTest.fixture.getFragmentContext(), TestLateralJoinCorrectnessBatchProcessing.operatorContext, TestLateralJoinCorrectnessBatchProcessing.leftContainer, TestLateralJoinCorrectnessBatchProcessing.leftOutcomes, TestLateralJoinCorrectnessBatchProcessing.leftContainer.get(0).getSchema());
        // Get the right container with dummy data
        final RowSet.SingleRowSet nonEmptyRightRowSet2 = SubOperatorTest.fixture.rowSetBuilder(TestLateralJoinCorrectnessBatchProcessing.rightSchema).addRow(1, 11, 110, "item11").addRow(4, 44, 440, "item44").build();
        final RowSet.SingleRowSet expectedRowSet = SubOperatorTest.fixture.rowSetBuilder(TestLateralJoinCorrectnessBatchProcessing.expectedSchemaLeftJoin).addRow(1, 10, "item1", 11, 110, "item11").addRow(2, 20, "item2", null, null, null).addRow(3, 30, "item3", null, null, null).addRow(4, 40, "item4", 44, 440, "item44").build();
        TestLateralJoinCorrectnessBatchProcessing.rightContainer.add(TestLateralJoinCorrectnessBatchProcessing.emptyRightRowSet.container());
        TestLateralJoinCorrectnessBatchProcessing.rightContainer.add(nonEmptyRightRowSet2.container());
        TestLateralJoinCorrectnessBatchProcessing.rightOutcomes.add(OK_NEW_SCHEMA);
        TestLateralJoinCorrectnessBatchProcessing.rightOutcomes.add(EMIT);
        final CloseableRecordBatch rightMockBatch = new MockRecordBatch(SubOperatorTest.fixture.getFragmentContext(), TestLateralJoinCorrectnessBatchProcessing.operatorContext, TestLateralJoinCorrectnessBatchProcessing.rightContainer, TestLateralJoinCorrectnessBatchProcessing.rightOutcomes, TestLateralJoinCorrectnessBatchProcessing.rightContainer.get(0).getSchema());
        LateralJoinPOP ljPopConfig = new LateralJoinPOP(null, null, JoinRelType.LEFT, DrillLateralJoinRelBase.IMPLICIT_COLUMN, Lists.newArrayList());
        final LateralJoinBatch ljBatch = new LateralJoinBatch(ljPopConfig, SubOperatorTest.fixture.getFragmentContext(), leftMockBatch, rightMockBatch);
        try {
            Assert.assertTrue(((IterOutcome.OK_NEW_SCHEMA) == (ljBatch.next())));
            Assert.assertTrue(((IterOutcome.OK) == (ljBatch.next())));
            Assert.assertTrue(((ljBatch.getRecordCount()) == (TestLateralJoinCorrectnessBatchProcessing.nonEmptyLeftRowSet.rowCount())));
            // verify results
            RowSet actualRowSet = DirectRowSet.fromContainer(ljBatch.getContainer());
            new RowSetComparison(expectedRowSet).verify(actualRowSet);
            Assert.assertTrue(((IterOutcome.NONE) == (ljBatch.next())));
        } finally {
            // Close all the resources for this test case
            ljBatch.close();
            leftMockBatch.close();
            rightMockBatch.close();
            nonEmptyRightRowSet2.clear();
            expectedRowSet.clear();
        }
    }

    @Test
    public void testLeftAndRightWithInitialMissingRows_MultipleBatch() throws Exception {
        TestLateralJoinCorrectnessBatchProcessing.leftContainer.add(TestLateralJoinCorrectnessBatchProcessing.nonEmptyLeftRowSet.container());
        // Get the left IterOutcomes for Lateral Join
        TestLateralJoinCorrectnessBatchProcessing.leftOutcomes.add(OK_NEW_SCHEMA);
        // Create Left MockRecordBatch
        final CloseableRecordBatch leftMockBatch = new MockRecordBatch(SubOperatorTest.fixture.getFragmentContext(), TestLateralJoinCorrectnessBatchProcessing.operatorContext, TestLateralJoinCorrectnessBatchProcessing.leftContainer, TestLateralJoinCorrectnessBatchProcessing.leftOutcomes, TestLateralJoinCorrectnessBatchProcessing.leftContainer.get(0).getSchema());
        // Get the right container with dummy data
        final RowSet.SingleRowSet nonEmptyRightRowSet2 = SubOperatorTest.fixture.rowSetBuilder(TestLateralJoinCorrectnessBatchProcessing.rightSchema).addRow(2, 22, 220, "item22").addRow(3, 33, 330, "item33").build();
        final RowSet.SingleRowSet nonEmptyRightRowSet3 = SubOperatorTest.fixture.rowSetBuilder(TestLateralJoinCorrectnessBatchProcessing.rightSchema).addRow(4, 44, 440, "item44_1").addRow(4, 44, 440, "item44_2").build();
        final RowSet.SingleRowSet expectedRowSet = SubOperatorTest.fixture.rowSetBuilder(TestLateralJoinCorrectnessBatchProcessing.expectedSchema).addRow(2, 20, "item2", 22, 220, "item22").addRow(3, 30, "item3", 33, 330, "item33").addRow(4, 40, "item4", 44, 440, "item44_1").addRow(4, 40, "item4", 44, 440, "item44_2").build();
        TestLateralJoinCorrectnessBatchProcessing.rightContainer.add(TestLateralJoinCorrectnessBatchProcessing.emptyRightRowSet.container());
        TestLateralJoinCorrectnessBatchProcessing.rightContainer.add(nonEmptyRightRowSet2.container());
        TestLateralJoinCorrectnessBatchProcessing.rightContainer.add(nonEmptyRightRowSet3.container());
        TestLateralJoinCorrectnessBatchProcessing.rightOutcomes.add(OK_NEW_SCHEMA);
        TestLateralJoinCorrectnessBatchProcessing.rightOutcomes.add(OK);
        TestLateralJoinCorrectnessBatchProcessing.rightOutcomes.add(EMIT);
        final CloseableRecordBatch rightMockBatch = new MockRecordBatch(SubOperatorTest.fixture.getFragmentContext(), TestLateralJoinCorrectnessBatchProcessing.operatorContext, TestLateralJoinCorrectnessBatchProcessing.rightContainer, TestLateralJoinCorrectnessBatchProcessing.rightOutcomes, TestLateralJoinCorrectnessBatchProcessing.rightContainer.get(0).getSchema());
        final LateralJoinBatch ljBatch = new LateralJoinBatch(TestLateralJoinCorrectnessBatchProcessing.ljPopConfig, SubOperatorTest.fixture.getFragmentContext(), leftMockBatch, rightMockBatch);
        try {
            Assert.assertTrue(((IterOutcome.OK_NEW_SCHEMA) == (ljBatch.next())));
            Assert.assertTrue(((IterOutcome.OK) == (ljBatch.next())));
            Assert.assertTrue(((ljBatch.getRecordCount()) == ((nonEmptyRightRowSet2.rowCount()) + (nonEmptyRightRowSet3.rowCount()))));
            // verify results
            RowSet actualRowSet = DirectRowSet.fromContainer(ljBatch.getContainer());
            new RowSetComparison(expectedRowSet).verify(actualRowSet);
            Assert.assertTrue(((IterOutcome.NONE) == (ljBatch.next())));
        } finally {
            // Close all the resources for this test case
            ljBatch.close();
            leftMockBatch.close();
            rightMockBatch.close();
            nonEmptyRightRowSet2.clear();
            expectedRowSet.clear();
        }
    }

    @Test
    public void testLeftAndRightWithInitialMissingRows_LeftJoin_MultipleBatch() throws Exception {
        TestLateralJoinCorrectnessBatchProcessing.leftContainer.add(TestLateralJoinCorrectnessBatchProcessing.nonEmptyLeftRowSet.container());
        // Get the left IterOutcomes for Lateral Join
        TestLateralJoinCorrectnessBatchProcessing.leftOutcomes.add(OK_NEW_SCHEMA);
        // Create Left MockRecordBatch
        final CloseableRecordBatch leftMockBatch = new MockRecordBatch(SubOperatorTest.fixture.getFragmentContext(), TestLateralJoinCorrectnessBatchProcessing.operatorContext, TestLateralJoinCorrectnessBatchProcessing.leftContainer, TestLateralJoinCorrectnessBatchProcessing.leftOutcomes, TestLateralJoinCorrectnessBatchProcessing.leftContainer.get(0).getSchema());
        // Get the right container with dummy data
        final RowSet.SingleRowSet nonEmptyRightRowSet2 = SubOperatorTest.fixture.rowSetBuilder(TestLateralJoinCorrectnessBatchProcessing.rightSchema).addRow(2, 22, 220, "item22").addRow(3, 33, 330, "item33").build();
        final RowSet.SingleRowSet nonEmptyRightRowSet3 = SubOperatorTest.fixture.rowSetBuilder(TestLateralJoinCorrectnessBatchProcessing.rightSchema).addRow(4, 44, 440, "item44_1").addRow(4, 44, 440, "item44_2").build();
        final RowSet.SingleRowSet expectedRowSet = SubOperatorTest.fixture.rowSetBuilder(TestLateralJoinCorrectnessBatchProcessing.expectedSchemaLeftJoin).addRow(1, 10, "item1", null, null, null).addRow(2, 20, "item2", 22, 220, "item22").addRow(3, 30, "item3", 33, 330, "item33").addRow(4, 40, "item4", 44, 440, "item44_1").addRow(4, 40, "item4", 44, 440, "item44_2").build();
        TestLateralJoinCorrectnessBatchProcessing.rightContainer.add(TestLateralJoinCorrectnessBatchProcessing.emptyRightRowSet.container());
        TestLateralJoinCorrectnessBatchProcessing.rightContainer.add(nonEmptyRightRowSet2.container());
        TestLateralJoinCorrectnessBatchProcessing.rightContainer.add(nonEmptyRightRowSet3.container());
        TestLateralJoinCorrectnessBatchProcessing.rightOutcomes.add(OK_NEW_SCHEMA);
        TestLateralJoinCorrectnessBatchProcessing.rightOutcomes.add(OK);
        TestLateralJoinCorrectnessBatchProcessing.rightOutcomes.add(EMIT);
        final CloseableRecordBatch rightMockBatch = new MockRecordBatch(SubOperatorTest.fixture.getFragmentContext(), TestLateralJoinCorrectnessBatchProcessing.operatorContext, TestLateralJoinCorrectnessBatchProcessing.rightContainer, TestLateralJoinCorrectnessBatchProcessing.rightOutcomes, TestLateralJoinCorrectnessBatchProcessing.rightContainer.get(0).getSchema());
        LateralJoinPOP ljPopConfig = new LateralJoinPOP(null, null, JoinRelType.LEFT, DrillLateralJoinRelBase.IMPLICIT_COLUMN, Lists.newArrayList());
        final LateralJoinBatch ljBatch = new LateralJoinBatch(ljPopConfig, SubOperatorTest.fixture.getFragmentContext(), leftMockBatch, rightMockBatch);
        try {
            Assert.assertTrue(((IterOutcome.OK_NEW_SCHEMA) == (ljBatch.next())));
            Assert.assertTrue(((IterOutcome.OK) == (ljBatch.next())));
            Assert.assertTrue(((ljBatch.getRecordCount()) == ((1 + (nonEmptyRightRowSet2.rowCount())) + (nonEmptyRightRowSet3.rowCount()))));
            // verify results
            RowSet actualRowSet = DirectRowSet.fromContainer(ljBatch.getContainer());
            new RowSetComparison(expectedRowSet).verify(actualRowSet);
            Assert.assertTrue(((IterOutcome.NONE) == (ljBatch.next())));
        } finally {
            // Close all the resources for this test case
            ljBatch.close();
            leftMockBatch.close();
            rightMockBatch.close();
            nonEmptyRightRowSet2.clear();
            expectedRowSet.clear();
        }
    }

    @Test
    public void testLeftAndRightWithLastMissingRows_MultipleBatch() throws Exception {
        TestLateralJoinCorrectnessBatchProcessing.leftContainer.add(TestLateralJoinCorrectnessBatchProcessing.nonEmptyLeftRowSet.container());
        // Get the left IterOutcomes for Lateral Join
        TestLateralJoinCorrectnessBatchProcessing.leftOutcomes.add(OK_NEW_SCHEMA);
        // Create Left MockRecordBatch
        final CloseableRecordBatch leftMockBatch = new MockRecordBatch(SubOperatorTest.fixture.getFragmentContext(), TestLateralJoinCorrectnessBatchProcessing.operatorContext, TestLateralJoinCorrectnessBatchProcessing.leftContainer, TestLateralJoinCorrectnessBatchProcessing.leftOutcomes, TestLateralJoinCorrectnessBatchProcessing.leftContainer.get(0).getSchema());
        // Get the right container with dummy data
        final RowSet.SingleRowSet nonEmptyRightRowSet2 = SubOperatorTest.fixture.rowSetBuilder(TestLateralJoinCorrectnessBatchProcessing.rightSchema).addRow(1, 11, 110, "item11").addRow(2, 22, 220, "item22").build();
        final RowSet.SingleRowSet nonEmptyRightRowSet3 = SubOperatorTest.fixture.rowSetBuilder(TestLateralJoinCorrectnessBatchProcessing.rightSchema).addRow(3, 33, 330, "item33_1").addRow(3, 33, 330, "item33_2").build();
        final RowSet.SingleRowSet expectedRowSet = SubOperatorTest.fixture.rowSetBuilder(TestLateralJoinCorrectnessBatchProcessing.expectedSchema).addRow(1, 10, "item1", 11, 110, "item11").addRow(2, 20, "item2", 22, 220, "item22").addRow(3, 30, "item3", 33, 330, "item33_1").addRow(3, 30, "item3", 33, 330, "item33_2").build();
        TestLateralJoinCorrectnessBatchProcessing.rightContainer.add(TestLateralJoinCorrectnessBatchProcessing.emptyRightRowSet.container());
        TestLateralJoinCorrectnessBatchProcessing.rightContainer.add(nonEmptyRightRowSet2.container());
        TestLateralJoinCorrectnessBatchProcessing.rightContainer.add(nonEmptyRightRowSet3.container());
        TestLateralJoinCorrectnessBatchProcessing.rightOutcomes.add(OK_NEW_SCHEMA);
        TestLateralJoinCorrectnessBatchProcessing.rightOutcomes.add(OK);
        TestLateralJoinCorrectnessBatchProcessing.rightOutcomes.add(EMIT);
        final CloseableRecordBatch rightMockBatch = new MockRecordBatch(SubOperatorTest.fixture.getFragmentContext(), TestLateralJoinCorrectnessBatchProcessing.operatorContext, TestLateralJoinCorrectnessBatchProcessing.rightContainer, TestLateralJoinCorrectnessBatchProcessing.rightOutcomes, TestLateralJoinCorrectnessBatchProcessing.rightContainer.get(0).getSchema());
        final LateralJoinBatch ljBatch = new LateralJoinBatch(TestLateralJoinCorrectnessBatchProcessing.ljPopConfig, SubOperatorTest.fixture.getFragmentContext(), leftMockBatch, rightMockBatch);
        try {
            Assert.assertTrue(((IterOutcome.OK_NEW_SCHEMA) == (ljBatch.next())));
            Assert.assertTrue(((IterOutcome.OK) == (ljBatch.next())));
            Assert.assertTrue(((ljBatch.getRecordCount()) == ((nonEmptyRightRowSet2.rowCount()) + (nonEmptyRightRowSet3.rowCount()))));
            // verify results
            RowSet actualRowSet = DirectRowSet.fromContainer(ljBatch.getContainer());
            new RowSetComparison(expectedRowSet).verify(actualRowSet);
            Assert.assertTrue(((IterOutcome.NONE) == (ljBatch.next())));
        } finally {
            // Close all the resources for this test case
            ljBatch.close();
            leftMockBatch.close();
            rightMockBatch.close();
            nonEmptyRightRowSet2.clear();
            expectedRowSet.clear();
        }
    }

    @Test
    public void testLeftAndRightWithLastMissingRows_LeftJoin_MultipleBatch() throws Exception {
        TestLateralJoinCorrectnessBatchProcessing.leftContainer.add(TestLateralJoinCorrectnessBatchProcessing.nonEmptyLeftRowSet.container());
        // Get the left IterOutcomes for Lateral Join
        TestLateralJoinCorrectnessBatchProcessing.leftOutcomes.add(OK_NEW_SCHEMA);
        // Create Left MockRecordBatch
        final CloseableRecordBatch leftMockBatch = new MockRecordBatch(SubOperatorTest.fixture.getFragmentContext(), TestLateralJoinCorrectnessBatchProcessing.operatorContext, TestLateralJoinCorrectnessBatchProcessing.leftContainer, TestLateralJoinCorrectnessBatchProcessing.leftOutcomes, TestLateralJoinCorrectnessBatchProcessing.leftContainer.get(0).getSchema());
        // Get the right container with dummy data
        final RowSet.SingleRowSet nonEmptyRightRowSet2 = SubOperatorTest.fixture.rowSetBuilder(TestLateralJoinCorrectnessBatchProcessing.rightSchema).addRow(1, 11, 110, "item11").addRow(2, 22, 220, "item22").build();
        final RowSet.SingleRowSet nonEmptyRightRowSet3 = SubOperatorTest.fixture.rowSetBuilder(TestLateralJoinCorrectnessBatchProcessing.rightSchema).addRow(3, 33, 330, "item33_1").addRow(3, 33, 330, "item33_2").build();
        final RowSet.SingleRowSet expectedRowSet = SubOperatorTest.fixture.rowSetBuilder(TestLateralJoinCorrectnessBatchProcessing.expectedSchemaLeftJoin).addRow(1, 10, "item1", 11, 110, "item11").addRow(2, 20, "item2", 22, 220, "item22").addRow(3, 30, "item3", 33, 330, "item33_1").addRow(3, 30, "item3", 33, 330, "item33_2").addRow(4, 40, "item4", null, null, null).build();
        TestLateralJoinCorrectnessBatchProcessing.rightContainer.add(TestLateralJoinCorrectnessBatchProcessing.emptyRightRowSet.container());
        TestLateralJoinCorrectnessBatchProcessing.rightContainer.add(nonEmptyRightRowSet2.container());
        TestLateralJoinCorrectnessBatchProcessing.rightContainer.add(nonEmptyRightRowSet3.container());
        TestLateralJoinCorrectnessBatchProcessing.rightOutcomes.add(OK_NEW_SCHEMA);
        TestLateralJoinCorrectnessBatchProcessing.rightOutcomes.add(OK);
        TestLateralJoinCorrectnessBatchProcessing.rightOutcomes.add(EMIT);
        final CloseableRecordBatch rightMockBatch = new MockRecordBatch(SubOperatorTest.fixture.getFragmentContext(), TestLateralJoinCorrectnessBatchProcessing.operatorContext, TestLateralJoinCorrectnessBatchProcessing.rightContainer, TestLateralJoinCorrectnessBatchProcessing.rightOutcomes, TestLateralJoinCorrectnessBatchProcessing.rightContainer.get(0).getSchema());
        LateralJoinPOP ljPopConfig = new LateralJoinPOP(null, null, JoinRelType.LEFT, DrillLateralJoinRelBase.IMPLICIT_COLUMN, Lists.newArrayList());
        final LateralJoinBatch ljBatch = new LateralJoinBatch(ljPopConfig, SubOperatorTest.fixture.getFragmentContext(), leftMockBatch, rightMockBatch);
        try {
            Assert.assertTrue(((IterOutcome.OK_NEW_SCHEMA) == (ljBatch.next())));
            Assert.assertTrue(((IterOutcome.OK) == (ljBatch.next())));
            Assert.assertTrue(((ljBatch.getRecordCount()) == ((1 + (nonEmptyRightRowSet2.rowCount())) + (nonEmptyRightRowSet3.rowCount()))));
            // verify results
            RowSet actualRowSet = DirectRowSet.fromContainer(ljBatch.getContainer());
            new RowSetComparison(expectedRowSet).verify(actualRowSet);
            Assert.assertTrue(((IterOutcome.NONE) == (ljBatch.next())));
        } finally {
            // Close all the resources for this test case
            ljBatch.close();
            leftMockBatch.close();
            rightMockBatch.close();
            nonEmptyRightRowSet2.clear();
            expectedRowSet.clear();
        }
    }

    @Test
    public void testLeftAndRight_OutputFull_InRightBatchMiddle() throws Exception {
        TestLateralJoinCorrectnessBatchProcessing.leftContainer.add(TestLateralJoinCorrectnessBatchProcessing.nonEmptyLeftRowSet.container());
        // Get the left IterOutcomes for Lateral Join
        TestLateralJoinCorrectnessBatchProcessing.leftOutcomes.add(OK_NEW_SCHEMA);
        // Create Left MockRecordBatch
        final CloseableRecordBatch leftMockBatch = new MockRecordBatch(SubOperatorTest.fixture.getFragmentContext(), TestLateralJoinCorrectnessBatchProcessing.operatorContext, TestLateralJoinCorrectnessBatchProcessing.leftContainer, TestLateralJoinCorrectnessBatchProcessing.leftOutcomes, TestLateralJoinCorrectnessBatchProcessing.leftContainer.get(0).getSchema());
        // Get the right container with dummy data
        final RowSet.SingleRowSet nonEmptyRightRowSet3 = SubOperatorTest.fixture.rowSetBuilder(TestLateralJoinCorrectnessBatchProcessing.rightSchema).addRow(4, 44, 440, "item44_2_1").addRow(4, 44, 440, "item44_2_2").addRow(4, 44, 440, "item44_2_3").addRow(4, 44, 440, "item44_2_4").build();
        final RowSet.SingleRowSet expectedRowSet = SubOperatorTest.fixture.rowSetBuilder(TestLateralJoinCorrectnessBatchProcessing.expectedSchema).addRow(1, 10, "item1", 11, 110, "item11").addRow(2, 20, "item2", 22, 220, "item22").addRow(3, 30, "item3", 33, 330, "item33").addRow(4, 40, "item4", 44, 440, "item44").addRow(4, 40, "item4", 44, 440, "item44_2_1").addRow(4, 40, "item4", 44, 440, "item44_2_2").build();
        final RowSet.SingleRowSet expectedRowSet1 = SubOperatorTest.fixture.rowSetBuilder(TestLateralJoinCorrectnessBatchProcessing.expectedSchema).addRow(4, 40, "item4", 44, 440, "item44_2_3").addRow(4, 40, "item4", 44, 440, "item44_2_4").build();
        TestLateralJoinCorrectnessBatchProcessing.rightContainer.add(TestLateralJoinCorrectnessBatchProcessing.emptyRightRowSet.container());
        TestLateralJoinCorrectnessBatchProcessing.rightContainer.add(TestLateralJoinCorrectnessBatchProcessing.nonEmptyRightRowSet.container());
        TestLateralJoinCorrectnessBatchProcessing.rightContainer.add(nonEmptyRightRowSet3.container());
        TestLateralJoinCorrectnessBatchProcessing.rightOutcomes.add(OK_NEW_SCHEMA);
        TestLateralJoinCorrectnessBatchProcessing.rightOutcomes.add(OK);
        TestLateralJoinCorrectnessBatchProcessing.rightOutcomes.add(EMIT);
        final CloseableRecordBatch rightMockBatch = new MockRecordBatch(SubOperatorTest.fixture.getFragmentContext(), TestLateralJoinCorrectnessBatchProcessing.operatorContext, TestLateralJoinCorrectnessBatchProcessing.rightContainer, TestLateralJoinCorrectnessBatchProcessing.rightOutcomes, TestLateralJoinCorrectnessBatchProcessing.rightContainer.get(0).getSchema());
        final LateralJoinBatch ljBatch = new LateralJoinBatch(TestLateralJoinCorrectnessBatchProcessing.ljPopConfig, SubOperatorTest.fixture.getFragmentContext(), leftMockBatch, rightMockBatch);
        ljBatch.setMaxOutputRowCount(6);
        ljBatch.setUseMemoryManager(false);
        try {
            Assert.assertTrue(((IterOutcome.OK_NEW_SCHEMA) == (ljBatch.next())));
            Assert.assertTrue(((IterOutcome.OK) == (ljBatch.next())));
            Assert.assertTrue(((ljBatch.getRecordCount()) == 6));
            // verify results
            RowSet actualRowSet = DirectRowSet.fromContainer(ljBatch.getContainer());
            new RowSetComparison(expectedRowSet).verify(actualRowSet);
            // Release container memory for this output batch since other operators will do the same
            VectorAccessibleUtilities.clear(ljBatch);
            Assert.assertTrue(((IterOutcome.OK) == (ljBatch.next())));
            Assert.assertTrue(((ljBatch.getRecordCount()) == (((TestLateralJoinCorrectnessBatchProcessing.nonEmptyRightRowSet.rowCount()) + (nonEmptyRightRowSet3.rowCount())) - 6)));
            // verify results
            RowSet actualRowSet2 = DirectRowSet.fromContainer(ljBatch.getContainer());
            new RowSetComparison(expectedRowSet1).verify(actualRowSet2);
            Assert.assertTrue(((IterOutcome.NONE) == (ljBatch.next())));
        } finally {
            // Close all the resources for this test case
            ljBatch.close();
            leftMockBatch.close();
            rightMockBatch.close();
            expectedRowSet.clear();
            expectedRowSet1.clear();
        }
    }

    @Test
    public void testLeftAndRight_OutputFull_WithPendingLeftRow() throws Exception {
        TestLateralJoinCorrectnessBatchProcessing.leftContainer.add(TestLateralJoinCorrectnessBatchProcessing.nonEmptyLeftRowSet.container());
        // Get the left IterOutcomes for Lateral Join
        TestLateralJoinCorrectnessBatchProcessing.leftOutcomes.add(OK_NEW_SCHEMA);
        // Create Left MockRecordBatch
        final CloseableRecordBatch leftMockBatch = new MockRecordBatch(SubOperatorTest.fixture.getFragmentContext(), TestLateralJoinCorrectnessBatchProcessing.operatorContext, TestLateralJoinCorrectnessBatchProcessing.leftContainer, TestLateralJoinCorrectnessBatchProcessing.leftOutcomes, TestLateralJoinCorrectnessBatchProcessing.leftContainer.get(0).getSchema());
        // Get the right container with dummy data
        final RowSet.SingleRowSet nonEmptyRightRowSet2 = SubOperatorTest.fixture.rowSetBuilder(TestLateralJoinCorrectnessBatchProcessing.rightSchema).addRow(1, 11, 110, "item11").addRow(2, 22, 220, "item22").addRow(3, 33, 330, "item33").build();
        final RowSet.SingleRowSet expectedRowSet = SubOperatorTest.fixture.rowSetBuilder(TestLateralJoinCorrectnessBatchProcessing.expectedSchema).addRow(1, 10, "item1", 11, 110, "item11").addRow(2, 20, "item2", 22, 220, "item22").addRow(3, 30, "item3", 33, 330, "item33").build();
        TestLateralJoinCorrectnessBatchProcessing.rightContainer.add(TestLateralJoinCorrectnessBatchProcessing.emptyRightRowSet.container());
        TestLateralJoinCorrectnessBatchProcessing.rightContainer.add(nonEmptyRightRowSet2.container());
        TestLateralJoinCorrectnessBatchProcessing.rightOutcomes.add(OK_NEW_SCHEMA);
        TestLateralJoinCorrectnessBatchProcessing.rightOutcomes.add(EMIT);
        final CloseableRecordBatch rightMockBatch = new MockRecordBatch(SubOperatorTest.fixture.getFragmentContext(), TestLateralJoinCorrectnessBatchProcessing.operatorContext, TestLateralJoinCorrectnessBatchProcessing.rightContainer, TestLateralJoinCorrectnessBatchProcessing.rightOutcomes, TestLateralJoinCorrectnessBatchProcessing.rightContainer.get(0).getSchema());
        final LateralJoinBatch ljBatch = new LateralJoinBatch(TestLateralJoinCorrectnessBatchProcessing.ljPopConfig, SubOperatorTest.fixture.getFragmentContext(), leftMockBatch, rightMockBatch);
        ljBatch.setMaxOutputRowCount(3);
        ljBatch.setUseMemoryManager(false);
        try {
            Assert.assertTrue(((IterOutcome.OK_NEW_SCHEMA) == (ljBatch.next())));
            Assert.assertTrue(((IterOutcome.OK) == (ljBatch.next())));
            Assert.assertTrue(((ljBatch.getRecordCount()) == (nonEmptyRightRowSet2.rowCount())));
            // verify results
            RowSet actualRowSet = DirectRowSet.fromContainer(ljBatch.getContainer());
            new RowSetComparison(expectedRowSet).verify(actualRowSet);
            Assert.assertTrue(((IterOutcome.NONE) == (ljBatch.next())));
        } finally {
            // Close all the resources for this test case
            ljBatch.close();
            leftMockBatch.close();
            rightMockBatch.close();
            nonEmptyRightRowSet2.clear();
            expectedRowSet.clear();
        }
    }

    @Test
    public void testLeftAndRight_OutputFull_WithPendingLeftRow_LeftJoin() throws Exception {
        TestLateralJoinCorrectnessBatchProcessing.leftContainer.add(TestLateralJoinCorrectnessBatchProcessing.nonEmptyLeftRowSet.container());
        // Get the left IterOutcomes for Lateral Join
        TestLateralJoinCorrectnessBatchProcessing.leftOutcomes.add(OK_NEW_SCHEMA);
        // Create Left MockRecordBatch
        final CloseableRecordBatch leftMockBatch = new MockRecordBatch(SubOperatorTest.fixture.getFragmentContext(), TestLateralJoinCorrectnessBatchProcessing.operatorContext, TestLateralJoinCorrectnessBatchProcessing.leftContainer, TestLateralJoinCorrectnessBatchProcessing.leftOutcomes, TestLateralJoinCorrectnessBatchProcessing.leftContainer.get(0).getSchema());
        // Get the right container with dummy data
        final RowSet.SingleRowSet nonEmptyRightRowSet2 = SubOperatorTest.fixture.rowSetBuilder(TestLateralJoinCorrectnessBatchProcessing.rightSchema).addRow(1, 11, 110, "item11").addRow(2, 22, 220, "item22").build();
        final RowSet.SingleRowSet expectedRowSet = SubOperatorTest.fixture.rowSetBuilder(TestLateralJoinCorrectnessBatchProcessing.expectedSchemaLeftJoin).addRow(1, 10, "item1", 11, 110, "item11").addRow(2, 20, "item2", 22, 220, "item22").addRow(3, 30, "item3", null, null, null).build();
        final RowSet.SingleRowSet expectedRowSet1 = SubOperatorTest.fixture.rowSetBuilder(TestLateralJoinCorrectnessBatchProcessing.expectedSchemaLeftJoin).addRow(4, 40, "item4", null, null, null).build();
        TestLateralJoinCorrectnessBatchProcessing.rightContainer.add(TestLateralJoinCorrectnessBatchProcessing.emptyRightRowSet.container());
        TestLateralJoinCorrectnessBatchProcessing.rightContainer.add(nonEmptyRightRowSet2.container());
        TestLateralJoinCorrectnessBatchProcessing.rightOutcomes.add(OK_NEW_SCHEMA);
        TestLateralJoinCorrectnessBatchProcessing.rightOutcomes.add(EMIT);
        final CloseableRecordBatch rightMockBatch = new MockRecordBatch(SubOperatorTest.fixture.getFragmentContext(), TestLateralJoinCorrectnessBatchProcessing.operatorContext, TestLateralJoinCorrectnessBatchProcessing.rightContainer, TestLateralJoinCorrectnessBatchProcessing.rightOutcomes, TestLateralJoinCorrectnessBatchProcessing.rightContainer.get(0).getSchema());
        LateralJoinPOP ljPopConfig = new LateralJoinPOP(null, null, JoinRelType.LEFT, DrillLateralJoinRelBase.IMPLICIT_COLUMN, Lists.newArrayList());
        final LateralJoinBatch ljBatch = new LateralJoinBatch(ljPopConfig, SubOperatorTest.fixture.getFragmentContext(), leftMockBatch, rightMockBatch);
        ljBatch.setMaxOutputRowCount(3);
        ljBatch.setUseMemoryManager(false);
        try {
            Assert.assertTrue(((IterOutcome.OK_NEW_SCHEMA) == (ljBatch.next())));
            Assert.assertTrue(((IterOutcome.OK) == (ljBatch.next())));
            Assert.assertTrue(((ljBatch.getRecordCount()) == 3));
            // verify results
            RowSet actualRowSet = DirectRowSet.fromContainer(ljBatch.getContainer());
            new RowSetComparison(expectedRowSet).verify(actualRowSet);
            // Release output container memory for this batch as other operators will do
            VectorAccessibleUtilities.clear(ljBatch);
            Assert.assertTrue(((IterOutcome.OK) == (ljBatch.next())));
            Assert.assertTrue(((ljBatch.getRecordCount()) == ((TestLateralJoinCorrectnessBatchProcessing.nonEmptyLeftRowSet.rowCount()) - 3)));
            // verify results
            RowSet actualRowSet2 = DirectRowSet.fromContainer(ljBatch.getContainer());
            new RowSetComparison(expectedRowSet1).verify(actualRowSet2);
            Assert.assertTrue(((IterOutcome.NONE) == (ljBatch.next())));
        } finally {
            // Close all the resources for this test case
            ljBatch.close();
            leftMockBatch.close();
            rightMockBatch.close();
            nonEmptyRightRowSet2.clear();
            expectedRowSet.clear();
            expectedRowSet1.clear();
        }
    }

    @Test
    public void testMultipleLeftAndRight_OutputFull_WithPendingLeftRow_LeftJoin() throws Exception {
        // Get the right container with dummy data
        final RowSet.SingleRowSet nonEmptyLeftRowSet2 = SubOperatorTest.fixture.rowSetBuilder(TestLateralJoinCorrectnessBatchProcessing.leftSchema).addRow(5, 50, "item5").addRow(6, 60, "item6").build();
        TestLateralJoinCorrectnessBatchProcessing.leftContainer.add(TestLateralJoinCorrectnessBatchProcessing.nonEmptyLeftRowSet.container());
        TestLateralJoinCorrectnessBatchProcessing.leftContainer.add(nonEmptyLeftRowSet2.container());
        // Get the left IterOutcomes for Lateral Join
        TestLateralJoinCorrectnessBatchProcessing.leftOutcomes.add(OK_NEW_SCHEMA);
        TestLateralJoinCorrectnessBatchProcessing.leftOutcomes.add(OK);
        // Create Left MockRecordBatch
        final CloseableRecordBatch leftMockBatch = new MockRecordBatch(SubOperatorTest.fixture.getFragmentContext(), TestLateralJoinCorrectnessBatchProcessing.operatorContext, TestLateralJoinCorrectnessBatchProcessing.leftContainer, TestLateralJoinCorrectnessBatchProcessing.leftOutcomes, TestLateralJoinCorrectnessBatchProcessing.leftContainer.get(0).getSchema());
        // Get the right container with dummy data
        final RowSet.SingleRowSet nonEmptyRightRowSet2 = SubOperatorTest.fixture.rowSetBuilder(TestLateralJoinCorrectnessBatchProcessing.rightSchema).addRow(1, 11, 110, "item11").addRow(2, 22, 220, "item22").build();
        final RowSet.SingleRowSet expectedRowSet = SubOperatorTest.fixture.rowSetBuilder(TestLateralJoinCorrectnessBatchProcessing.expectedSchemaLeftJoin).addRow(1, 10, "item1", 11, 110, "item11").addRow(2, 20, "item2", 22, 220, "item22").addRow(3, 30, "item3", null, null, null).build();
        final RowSet.SingleRowSet expectedRowSet1 = SubOperatorTest.fixture.rowSetBuilder(TestLateralJoinCorrectnessBatchProcessing.expectedSchemaLeftJoin).addRow(4, 40, "item4", null, null, null).addRow(5, 50, "item5", null, null, null).addRow(6, 60, "item6", null, null, null).build();
        TestLateralJoinCorrectnessBatchProcessing.rightContainer.add(TestLateralJoinCorrectnessBatchProcessing.emptyRightRowSet.container());
        TestLateralJoinCorrectnessBatchProcessing.rightContainer.add(nonEmptyRightRowSet2.container());
        TestLateralJoinCorrectnessBatchProcessing.rightContainer.add(TestLateralJoinCorrectnessBatchProcessing.emptyRightRowSet.container());
        TestLateralJoinCorrectnessBatchProcessing.rightOutcomes.add(OK_NEW_SCHEMA);
        TestLateralJoinCorrectnessBatchProcessing.rightOutcomes.add(EMIT);
        TestLateralJoinCorrectnessBatchProcessing.rightOutcomes.add(EMIT);
        final CloseableRecordBatch rightMockBatch = new MockRecordBatch(SubOperatorTest.fixture.getFragmentContext(), TestLateralJoinCorrectnessBatchProcessing.operatorContext, TestLateralJoinCorrectnessBatchProcessing.rightContainer, TestLateralJoinCorrectnessBatchProcessing.rightOutcomes, TestLateralJoinCorrectnessBatchProcessing.rightContainer.get(0).getSchema());
        LateralJoinPOP ljPopConfig = new LateralJoinPOP(null, null, JoinRelType.LEFT, DrillLateralJoinRelBase.IMPLICIT_COLUMN, Lists.newArrayList());
        final LateralJoinBatch ljBatch = new LateralJoinBatch(ljPopConfig, SubOperatorTest.fixture.getFragmentContext(), leftMockBatch, rightMockBatch);
        ljBatch.setMaxOutputRowCount(3);
        ljBatch.setUseMemoryManager(false);
        try {
            Assert.assertTrue(((IterOutcome.OK_NEW_SCHEMA) == (ljBatch.next())));
            Assert.assertTrue(((IterOutcome.OK) == (ljBatch.next())));
            Assert.assertTrue(((ljBatch.getRecordCount()) == 3));
            // verify results
            RowSet actualRowSet = DirectRowSet.fromContainer(ljBatch.getContainer());
            new RowSetComparison(expectedRowSet).verify(actualRowSet);
            // Release output container memory for this batch as other operators will do
            VectorAccessibleUtilities.clear(ljBatch);
            Assert.assertTrue(((IterOutcome.OK) == (ljBatch.next())));
            Assert.assertTrue(((ljBatch.getRecordCount()) == (((TestLateralJoinCorrectnessBatchProcessing.nonEmptyLeftRowSet.rowCount()) + (nonEmptyLeftRowSet2.rowCount())) - 3)));
            // verify results
            RowSet actualRowSet2 = DirectRowSet.fromContainer(ljBatch.getContainer());
            new RowSetComparison(expectedRowSet1).verify(actualRowSet2);
            Assert.assertTrue(((IterOutcome.NONE) == (ljBatch.next())));
        } finally {
            // Close all the resources for this test case
            ljBatch.close();
            leftMockBatch.close();
            rightMockBatch.close();
            nonEmptyLeftRowSet2.clear();
            nonEmptyRightRowSet2.clear();
            expectedRowSet.clear();
            expectedRowSet1.clear();
        }
    }
}

